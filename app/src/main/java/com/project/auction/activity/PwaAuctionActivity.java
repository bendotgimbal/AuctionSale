package com.project.auction.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.WebViewTransport;
import android.webkit.WebViewClient;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

class JsObject {
  @JavascriptInterface
  void start(String token) {
    PwaAuctionActivity.syncPWA();
  }

  @JavascriptInterface
  override fun toString(): String {
    return "injectedObject"
  }
}

public class PwaAuctionActivity extends AppCompatActivity {

  private WebView webView;

  private final boolean ENABLE_MIXED_CONTENT = true;
  private final boolean POSTFIX_USER_AGENT = true;
  private final boolean OVERRIDE_USER_AGENT = true;

  private final String WEBAPP_URL = "http://auctionsale.xyz/auction";
  private final String USER_AGENT_POSTFIX = "Android App";
  private final String USER_AGENT = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36";

  @Override protected void onCreate(
      @Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setupWebView();
    loadHome();
  }

  @SuppressLint("SetJavaScriptEnabled")
  private void setupWebView() {
    WebSettings webSettings = webView.getSettings();

    CookieManager.getInstance().setAcceptCookie(true);
    webSettings.setJavaScriptEnabled(true);
    webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    webSettings.setSupportMultipleWindows(true);

    // PWA settings
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      webSettings.setDatabasePath(getApplicationContext().getFilesDir().getAbsolutePath());
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
      webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
    }

    webSettings.setDomStorageEnabled(true);
    webSettings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
    webSettings.setAppCacheEnabled(true);
    webSettings.setDatabaseEnabled(true);

    // enable mixed content mode conditionally
    if (ENABLE_MIXED_CONTENT
        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
    }

    // retrieve content from cache primarily if not connected
    webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

    // set User Agent
    if (OVERRIDE_USER_AGENT || POSTFIX_USER_AGENT) {
      String userAgent = webSettings.getUserAgentString();
      if (OVERRIDE_USER_AGENT) {
        userAgent = USER_AGENT;
      }
      if (POSTFIX_USER_AGENT) {
        userAgent = userAgent + " " + USER_AGENT_POSTFIX;
      }
      webSettings.setUserAgentString(userAgent);
    }

    webView.setWebChromeClient(new WebChromeClient(){
      @Override public boolean onCreateWindow(
          WebView view,
          boolean isDialog,
          boolean isUserGesture,
          Message resultMsg) {
        WebView newWebView = new WebView((PwaAuctionActivity)this);

        WebSettings webSettings = newWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        newWebView.setWebChromeClient(new WebChromeClient() {});
        ((WebViewTransport) resultMsg.obj).getWebView() = newWebView;
        resultMsg.sendToTarget();
        return true;
      }
    });

    webView.setWebViewClient(new WebViewClient() {
      @Override public void onReceivedError(
          WebView view,
          int errorCode,
          String description,
          String failingUrl) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
          handleLoadError(errorCode);
        }
      }

      @TargetApi(Build.VERSION_CODES.M)
      @Override public void onReceivedError(
          WebView view,
          WebResourceRequest request,
          WebResourceError error) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          String url = request.getUrl().toString();
          if (view.getUrl().equals(url)) {
            handleLoadError(error.getErrorCode());
          }
        }
      }
    });

    webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
      Intent i = new Intent(Intent.ACTION_VIEW);
      i.setData(Uri.parse(url));
      startActivity(i);
    });

    webView.addJavascriptInterface(new JsOb(this), "AuctionSale");
  }


  private void handleLoadError(int errorCode) {
    new Handler().postDelayed(this::onBackPressed, 100);
  }


  private void loadHome() {
    webView.loadUrl(WEBAPP_URL);
  }
}
