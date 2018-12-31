package com.mywill.supersnake;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
    SuperSnake superSnake = new SuperSnake();
    superSnake.setOnButtonPress(new SuperSnake.OnButtonPress() {
      @Override
      public void onClickPurchase() {
       startActivity(new Intent(getApplication(), PurchaseActivity.class));
      }
    });
		initialize(superSnake, config);
	}
}
