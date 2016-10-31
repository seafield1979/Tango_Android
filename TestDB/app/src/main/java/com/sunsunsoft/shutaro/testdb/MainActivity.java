package com.sunsunsoft.shutaro.testdb;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setViews();

        // Realmの初期化
        MyRealmManager.initRealm(getApplicationContext());
    }

    private void setViews() {
        // toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        // view pager
        FragmentManager manager = getSupportFragmentManager();
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(manager);
        viewPager.setAdapter(adapter);

        // tab に view pager のページのタイトルを表示
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tab);
        tabLayout.setupWithViewPager(viewPager);

        // drawer
        setDrawer();
    }

    /**
     * Drawerをセットアップ
     */
    private void setDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.main_drawer_navigation);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(select);
    }

    private NavigationView.OnNavigationItemSelectedListener select =
            new NavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.menu_manage:
                    break;
                case R.id.menu_gallery:
                    break;
                case R.id.menu_alert:
                    break;
                case R.id.menu_save:
                    break;
                case R.id.menu_setting:
                    break;
                default:
                    // ドロワーを閉じる
                    mDrawerLayout.closeDrawers();
            }
            return true;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MyRealmManager.closeRealm();
    }
}
