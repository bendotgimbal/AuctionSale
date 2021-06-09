package com.project.auction.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.RequiresApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.project.auction.R;

public class ProductActivity extends AppCompatActivity {

  private WebView myWebView;

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_product);

    myWebView = (WebView) findViewById(R.id.webview);
    myWebView.setWebViewClient(new WebViewClient());
    WebSettings webSettings = myWebView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    myWebView.loadUrl("http://10.4.56.104/auctionapp");
  }

  @RequiresApi(api = Build.VERSION_CODES.KITKAT)
  @Override
  public void onBackPressed() {
    //myWebView.evaluateJavascript("canGoBack();", s -> {
    //  if (Boolean.parseBoolean(s)) {
    //    myWebView.goBack();
    //  } else {
    //    super.onBackPressed();
    //  }
    //});

    if (myWebView.canGoBack()) {
      myWebView.goBack();
    } else {
      super.onBackPressed();
    }
  }
}

