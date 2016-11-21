package com.sunsunsoft.shutaro.tangobook;

import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            TopFragment fragment = new TopFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // コンテナにMainFragmentを格納
            transaction.add(R.id.fragment_container, fragment, TopFragment.TAG);
            // 画面に表示
            transaction.commit();

            UDrawManager.getInstance().init();
            ULog.init();
        }
    }
}
