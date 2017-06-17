package com.sunsunsoft.shutaro.tangobook.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.sunsunsoft.shutaro.tangobook.save.BackupFileInfo;
import com.sunsunsoft.shutaro.tangobook.util.ConvDateMode;
import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;
import com.sunsunsoft.shutaro.tangobook.page.PageViewManager;
import com.sunsunsoft.shutaro.tangobook.preset.PresetBookManager;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.save.BackupFileType;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.TopFragment;
import com.sunsunsoft.shutaro.tangobook.uview.UDrawManager;
import com.sunsunsoft.shutaro.tangobook.util.ULog;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.util.UUtil;
import com.sunsunsoft.shutaro.tangobook.save.XmlManager;
import com.sunsunsoft.shutaro.tangobook.fragment.HelpFragment;
import com.sunsunsoft.shutaro.tangobook.fragment.HelpPageId;
import com.sunsunsoft.shutaro.tangobook.help.HelpPageFragment;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    /**
     * Enum
     */
    // アクションバーに表示するメニューの種類
    public enum MenuType {
        None,           // 非表示
        TangoEdit,      // 単語帳編集ページ
        TangoEdit2,     // 単語帳編集ページ(アイコン選択時0
        SelectStudyBook // 学習する単語帳選択ページ
    }

    /**
     * Static variables
     */
    private static MainActivity gActivity;

    /**
     * Member variables
     */
    TopFragment mTopFragment;
    HelpFragment mHelpFragment;
    HelpPageFragment mHelpPageFragment;

    private boolean mShowMenu;
    private MenuType mMenuType;

    public static MainActivity getInstance() {
        return gActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        gActivity = this;

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

        // SharedPreferencesの初期化
        MySharedPref.init(this);

        // Realmの初期化
        RealmManager.initRealm(this, false);

        // Xmlマネージャ
        XmlManager.createInstance(this);

        // UResourceManager
        UResourceManager.createInstance(this);

        // セーブデータを初期化
        RealmManager.getBackupFileDao().createInitialRecords();

        // オートバックアップ
        if (MySharedPref.readBoolean(MySharedPref.AutoBackup)) {
            XmlManager.saveAutoBackup();
        }

        // PresetBookManager
        PresetBookManager.createInstance(this);
        mShowMenu = true;
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
            if (mTopFragment.onBackKeyDown()) {
                return true;
            }
            else if (mHelpPageFragment != null && mHelpPageFragment.isVisible()) {
                if (mHelpPageFragment.onBackKeyDown()) {
                    return true;
                }
            }
            else if (mHelpFragment != null && mHelpFragment.isVisible()) {
                if (mHelpFragment.onBackKeyDown()) {
                    return true;
                }
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
        switch(itemId) {
            case android.R.id.home:
                // 戻るボタン
                if (mHelpPageFragment != null && mHelpPageFragment.isVisible()) {
                    if (mHelpPageFragment.onBackKeyDown()) {
                        return true;
                    }
                }
                else if (mHelpFragment != null && mHelpFragment.isVisible()) {
                    if (mHelpFragment.onBackKeyDown()) {
                        showActionBarBack(false);
                        return true;
                    }
                }
                if (mTopFragment.onBackKeyDown()) {
                    return true;
                }
                return true;

            case R.id.action_move_to_trash:
            case R.id.action_sort_none:
            case R.id.action_sort_word_asc:
            case R.id.action_sort_word_desc:
            case R.id.action_sort_time_asc:
            case R.id.action_sort_time_desc:
            case R.id.action_sort_studied_time_asc:
            case R.id.action_sort_studied_time_desc:
            case R.id.action_card_name_a:
            case R.id.action_card_name_b:
            case R.id.action_search_card:
            case R.id.action_settings:
                PageViewManager.getInstance().setActionId(itemId);
                break;
        }
        return false;
    }

    /**
     * ヘルプトップページを表示する
     */
    public void showHelpTopPage() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mHelpFragment = new HelpFragment();

        transaction.replace(R.id.fragment_container, mHelpFragment, TopFragment.TAG);
        // 戻るボタンで元のFragmentを表示
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * ヘルプ詳細ページを表示する
     * @param helpPage
     */
    public void showHelpPage(HelpPageId helpPage) {
        mHelpPageFragment = HelpPageFragment.createInstance(helpPage);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, mHelpPageFragment, TopFragment.TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * メニューボタンを生成する
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch(mMenuType) {
            case TangoEdit:
                getMenuInflater().inflate(R.menu.menu_tango_edit, menu);
                break;
            case TangoEdit2:
                getMenuInflater().inflate(R.menu.menu_tango_edit2, menu);
                break;
            case SelectStudyBook:
                getMenuInflater().inflate(R.menu.menu_select_study_book, menu);
                break;
        }

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * メニューの表示切り替え
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return mShowMenu;
    }

    /**
     * メニューのタイプを設定
     * @param type
     */
    public void setMenuType(MenuType type) {
        mMenuType = type;
        if (type == MenuType.None) {
            mShowMenu = false;
            invalidateOptionsMenu();
        } else {
            mShowMenu = true;
            invalidateOptionsMenu();
        }
    }

    /**
     * アクションバーのタイトル文字を設定する
     * @param text
     */
    public void setActionBarTitle(String text) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(text);
    }

    /**
     * アクションバーの戻るボタン(←)を表示する
     * @param show false:非表示 / true:表示
     */
    public void showActionBarBack(boolean show) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(show);
        actionBar.setDisplayHomeAsUpEnabled(show);
    }
}
