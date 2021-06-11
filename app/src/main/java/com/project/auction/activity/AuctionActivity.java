package com.project.auction.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.project.auction.R;

public class AuctionActivity extends AppCompatActivity {

  private WebView myWebView;

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auction);

    myWebView = findViewById(R.id.webview);
    myWebView.setWebViewClient(new WebViewClient());
    WebSettings webSettings = myWebView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    myWebView.loadUrl("http://bid.auctionsale.xyz/");
  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT)
  @Override
  public void onBackPressed() {
    if (myWebView.canGoBack()) {
      myWebView.goBack();
    } else {
      super.onBackPressed();
    }
  }
}

