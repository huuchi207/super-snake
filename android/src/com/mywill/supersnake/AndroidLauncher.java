package com.mywill.supersnake;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
    config.useAccelerometer = false;
    config.useCompass = false;
    SuperSnake superSnake = new SuperSnake();
    superSnake.setOnButtonPress(new SuperSnake.OnButtonPress() {
      @Override
      public void onClickPurchase() {
        startActivityForResult(new Intent(getApplicationContext(), PurchaseActivity.class), 207);
      }

      @Override
      public void onClickLicense() {
        startActivity(new Intent(getApplicationContext(), LicenseAcitivity.class));
      }
    });
    initialize(superSnake, config);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 207) {
      if (resultCode == Activity.RESULT_OK) {
        DialogFactory.createSimpleOkErrorDialog(this, "Info", "Purchase and Consume Successfully").show();
        if (resultCode == Activity.RESULT_CANCELED) {
          //Write your code if there's no result
        }
      }
    }
  }
}
