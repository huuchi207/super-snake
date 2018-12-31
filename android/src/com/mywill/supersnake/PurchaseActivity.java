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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PurchaseActivity extends AppCompatActivity implements PurchasesUpdatedListener {
  private BillingClient mBillingClient;
  private List<SkuDetails> listData = new ArrayList<>();
  private RecyclerView mRecyclerView;
  private PurchaseListAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_purchase);

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
        if (billingResponseCode == BillingClient.BillingResponse.OK) {
          // The billing client is ready. You can query purchases here.
          getProductList();
        }
      }
      @Override
      public void onBillingServiceDisconnected() {
        // Try to restart the connection on the next request to
        // Google Play by calling the startConnection() method.
      }
    });
  }

  private void getProductList(){
    String[] list= {"reserved_product_id_1", "reserved_product_id_2","reserved_product_id_3","reserved_product_id_4"};
    List<String> skuList = Arrays.asList(list);
    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
    mBillingClient.querySkuDetailsAsync(params.build(),
        new SkuDetailsResponseListener() {
          @Override
          public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList){
            if (responseCode == BillingClient.BillingResponse.OK
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
    if (responseCode == BillingClient.BillingResponse.OK
        && purchases != null) {
      Toast.makeText(this, "Purchase Successfully", Toast.LENGTH_SHORT).show();
      for (Purchase purchase : purchases) {
        mBillingClient.consumeAsync(purchase.getPurchaseToken(), new ConsumeResponseListener() {
          @Override
          public void onConsumeResponse(@BillingClient.BillingResponse int responseCode, String outToken) {
            if (responseCode == BillingClient.BillingResponse.OK) {
              // Handle the success of the consume operation.
              // For example, increase the number of coins inside the user's basket.
              Toast.makeText(PurchaseActivity.this, "Consume Successfully", Toast.LENGTH_SHORT).show();

            }
          }});
      }
    } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
      // Handle an error caused by a user cancelling the purchase flow.
    } else {
      // Handle any other error codes.
    }
  }
}
