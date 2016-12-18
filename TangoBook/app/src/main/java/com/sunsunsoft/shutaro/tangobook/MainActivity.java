package com.sunsunsoft.shutaro.tangobook;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    TopFragment mTopFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        mTopFragment = new TopFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // コンテナにMainFragmentを格納
        transaction.add(R.id.fragment_container, mTopFragment, TopFragment.TAG);
        // 画面に表示
        transaction.commit();

        // ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_title);

        // 各種シングルトンの初期化
        UDrawManager.getInstance().init();

        // ULog
        ULog.init();

        // Realmの初期化
        RealmManager.initRealm(getApplicationContext(), false);

        // SharedPreferencesの初期化
        MySharedPref.init(this);

        // PresetBookManager
        PresetBookManager.createInstance(this);
        PresetBookManager.getInstance().makeBookList();

        UXmlParser.realTangoBook(this, R.raw.book2);
    }

    /**
     *  Androidのキーイベント
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode != KeyEvent.KEYCODE_BACK){
            return super.onKeyDown(keyCode, event);
        } else {
            // 戻るボタン
            if (mTopFragment.onBackKeyDown()) {
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
    }

    // メニューの項目が選択されると呼ばれる
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // item.getItemId() は選択されたメニューのID
        int itemId = item.getItemId();

        // 戻るボタンのIDならアクティビティを終了
        if(itemId == android.R.id.home) {
            // 戻るボタン
            if (mTopFragment.onBackKeyDown()) {
                return true;
            }
            return true;
        }
        return false;
    }
}
