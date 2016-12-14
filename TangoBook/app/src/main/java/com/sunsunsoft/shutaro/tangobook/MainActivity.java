package com.sunsunsoft.shutaro.tangobook;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

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


            // 各種シングルトンの初期化
            UDrawManager.getInstance().init();

            // ULog
            ULog.init();

            // Realmの初期化
            RealmManager.initRealm(getApplicationContext());

            // SharedPreferencesの初期化
            MySharedPref.init(this);

    }

//
//    @Override
//    protected void onDestroy() {
//        RealmManager.closeRealm();
//
//        super.onDestroy();
//    }

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
}
