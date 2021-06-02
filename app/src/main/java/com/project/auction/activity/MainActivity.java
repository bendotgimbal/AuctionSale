package com.project.auction.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import com.project.auction.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  AppCompatImageButton btnMyProduct;
  AppCompatImageButton btnAuctionList;
  AppCompatImageButton btnHistory;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    btnMyProduct = findViewById(R.id.btnMyProduct);
    btnAuctionList = findViewById(R.id.btnAuctionList);
    btnHistory = findViewById(R.id.btnHistory);

    btnMyProduct.setOnClickListener(this);
    btnAuctionList.setOnClickListener(this);
    btnHistory.setOnClickListener(this);
  }

  @SuppressLint("NonConstantResourceId")
  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btnMyProduct:
        Intent product = new Intent(this, ProductActivity.class);
        startActivity(product);
        break;
      case R.id.btnAuctionList:
        Intent auction = new Intent(this, CountDown.class);
        startActivity(auction);
        break;
      case R.id.btnHistory:
        Intent webview = new Intent(this, PwaAuctionActivity.class);
        startActivity(webview);
        break;
    }
  }
}
