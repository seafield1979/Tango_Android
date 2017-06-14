package com.sunsunsoft.shutaro.tangobook.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.sunsunsoft.shutaro.tangobook.R;

/**
 * ライセンス用のWebViewを表示するActivity
 */
public class LicensePageActivity extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_page);

        mWebView = (WebView)findViewById(R.id.webview1);

        mWebView.loadUrl("file:///android_asset/license.html");

        ActionBar ab = getSupportActionBar();
        // set title
        ab.setTitle(getString(R.string.license));

        // add return button to actionbar
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    // メニューの項目が選択されると呼ばれる
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // item.getItemId() は選択されたメニューのID
        int itemId = item.getItemId();

        // 戻るボタンのIDならアクティビティを終了
        if(itemId == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

}
