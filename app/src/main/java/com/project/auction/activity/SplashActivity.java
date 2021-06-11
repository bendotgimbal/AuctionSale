package com.project.auction.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.project.auction.R;

public class SplashActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    new Handler().postDelayed(this::moveToAuction, 2000);
  }

  private void moveToAuction(){
    Intent intent = new Intent(this, AuctionActivity.class);
    startActivity(intent);
    finish();
  }
}
