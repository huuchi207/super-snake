package com.mywill.supersnake;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.android.billingclient.api.BillingClient.BillingResponse.OK;

public class PurchaseActivity extends AppCompatActivity implements PurchasesUpdatedListener {
  private BillingClient mBillingClient;
  private List<SkuDetails> listData = new ArrayList<>();
  private RecyclerView mRecyclerView;
  private PurchaseListAdapter adapter;
  private FirebaseAnalytics mFirebaseAnalytics;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_purchase);

    mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    mRecyclerView.setHasFixedSize(true);
    LinearLayoutManager llm = new LinearLayoutManager(this);
    llm.setOrientation(LinearLayoutManager.VERTICAL);
    mRecyclerView.setLayoutManager(llm);
    adapter = new PurchaseListAdapter(this, listData);
    adapter.setClickListener(new PurchaseListAdapter.ItemClickListener() {
      @Override
      public void onItemClick(SkuDetails skuDetails) {
        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build();
        int responseCode = mBillingClient.launchBillingFlow(PurchaseActivity.this, flowParams);
      }
    });
    mRecyclerView.setAdapter(adapter);


    mBillingClient = BillingClient.newBuilder(this).setListener(this).build();
    mBillingClient.startConnection(new BillingClientStateListener() {
      @Override
      public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
        if (billingResponseCode == OK) {
          // The billing client is ready. You can query purchases here.
          getAllPurchaseSession();
        }
      }

      @Override
      public void onBillingServiceDisconnected() {
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
      }
    });
  }

  Integer done = 0;

  private void getAllPurchaseSession() {
    Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
    if (purchasesResult != null && purchasesResult.getResponseCode() == OK) {
      final int size = purchasesResult.getPurchasesList().size();
      if (size == 0) {
        getProductList();
        return;
      }
      for (Purchase purchase : purchasesResult.getPurchasesList()) {
        mBillingClient.consumeAsync(purchase.getPurchaseToken(), new ConsumeResponseListener() {
          @Override
          public void onConsumeResponse(int responseCode, String purchaseToken) {
            done++;
            if (done == size) {
              getProductList();
            }
          }
        });
      }
    }
  }

  private void getProductList() {
    String[] list = {
        "reserved_product_id_1", "reserved_product_id_2", "reserved_product_id_3",
        "reserved_product_id_4", "reserved_product_id_5", "reserved_product_id_6",
        "product_id_1", "product_id_2", "product_id_3",
        "product_id_4", "product_id_5", "product_id_6",
        "product_id_7", "product_id_8", "product_id_9",
    };
    List<String> skuList = Arrays.asList(list);
    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
    mBillingClient.querySkuDetailsAsync(params.build(),
        new SkuDetailsResponseListener() {
          @Override
          public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
            if (responseCode == OK
                && skuDetailsList != null) {
              listData.clear();
              listData.addAll(skuDetailsList);
              adapter.notifyDataSetChanged();
            }
          }
        });

  }


  @Override
  public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
    if (responseCode == OK
        && purchases != null) {
      Toast.makeText(this, "Purchase Successfully", Toast.LENGTH_SHORT).show();
      for (Purchase purchase : purchases) {
        mBillingClient.consumeAsync(purchase.getPurchaseToken(), new ConsumeResponseListener() {
          @Override
          public void onConsumeResponse(@BillingClient.BillingResponse int responseCode, String outToken) {
            if (responseCode == OK) {
              // Handle the success of the consume operation.
              // For example, increase the number of coins inside the user's basket.
              Toast.makeText(PurchaseActivity.this, "Consume Successfully", Toast.LENGTH_SHORT).show();

            }
          }});
      }
    } else {
      // Handle an error caused by a user cancelling the purchase flow.
      Bundle bundle = new Bundle();
      bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "BillingError");
      bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text");
      BillingResponseCode code = BillingResponseCode.valueOf(responseCode);
      bundle.putString(FirebaseAnalytics.Param.CONTENT, code != null
          ? code.toString() : "");
      mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, bundle);
    }
  }
}
