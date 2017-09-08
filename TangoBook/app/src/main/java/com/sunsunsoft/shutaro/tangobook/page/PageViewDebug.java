package com.sunsunsoft.shutaro.tangobook.page;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.database.TangoCard;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.UDebug;
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.util.ULog;
import com.sunsunsoft.shutaro.tangobook.listview.ListItemDebug;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.text.UTextView;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;
import com.sunsunsoft.shutaro.tangobook.uview.window.UDialogWindow;

import java.util.List;

/**
 * Created by shutaro on 2016/12/15.
 *
 * Debug page
 */

public class PageViewDebug extends UPageView implements UListItemCallbacks, UButtonCallbacks {
    /**
     * Enums
     */
    enum DebugMenu {
        ShowSystemInfo,     // システムの状態を表示
        DebugDB,            // データベースのデバッグ
        ClearData,          // 全データクリア
        None
        ;

        public static DebugMenu toEnum(int value) {
            if (value < DebugMenu.values().length) {
                return DebugMenu.values()[value];
            }
            return None;
        }
    }

    /**
     * Constants
     */
    public static final String TAG = "PageViewDebug";
    private static final int MenuIdTop = 100;

    private static final int DRAW_PRIORITY = 100;
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 50;
    private static final int TEXT_SIZE = 13;

    // button ids
    private static final int ButtonIdClearOK = 100;


    /**
     * Member variables
     */
    private UListView mListView;
    private UDialogWindow mDialog;


    /**
     * Constructor
     */
    public PageViewDebug(Context context, View parentView, String title) {
        super(context, parentView, title);
    }

    /**
     * Methods
     */

    protected void onShow() {

    }

    protected void onHide() {
        super.onHide();
    }

    /**
     * 描画処理
     * サブクラスのdrawでこのメソッドを最初に呼び出す
     * @param canvas
     * @param paint
     * @return
     */
    protected boolean draw(Canvas canvas, Paint paint) {
        if (isFirst) {
            isFirst = false;
            initDrawables();
        }
        return false;
    }

    /**
     * タッチ処理
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt) {

        return false;
    }

    /**
     * そのページで表示される描画オブジェクトを初期化する
     */
    public void initDrawables() {
        UDrawManager.getInstance().init();

        // ListView
        mListView = new UListView(null, this, DRAW_PRIORITY, MARGIN_H, MARGIN_V,
                mParentView.getWidth() - MARGIN_H * 2, mParentView.getHeight() - MARGIN_V * 2,
                Color.WHITE);
        mListView.setFrameColor(Color.BLACK);
        mListView.addToDrawManager();

        for (DebugMenu menu : DebugMenu.values()) {
            if (menu != DebugMenu.None) {
                ListItemDebug item = new ListItemDebug(this, menu.toString(),
                        true, 0, mListView.getSize().width);
                mListView.add(item);
            }
        }
    }

    /**
     * システムの各種情報を表示するダイアログ
     */
    private void showInfo() {
        if (mDialog != null) {
            mDialog.closeDialog();
        }
        mDialog = UDialogWindow.createInstance(this, null, UDialogWindow.ButtonDir.Horizontal,
                mParentView.getWidth(), mParentView
                .getHeight());
        mDialog.addToDrawManager();


        final float version = 1.00f;

        int cardNum = RealmManager.getCardDao().getNum();
        int bookNum = RealmManager.getBookDao().getNum();
        int itemPosNum = RealmManager.getItemPosDao().getNum();
        int bookHistoryNum = RealmManager.getBookHistoryDao().getNum();
        int cardHistoryNum = RealmManager.getCardHistoryDao().getNum();
        int studiedCardNum = RealmManager.getStudiedCardDao().getNum();

        String text = "version:" + version + "\n" +
                        "cardNum:" + cardNum + "\n" +
                "bookNum:" + bookNum + "\n" +
                "itemPosNum:" + itemPosNum + "\n" +
                "bookHistoryNum:" + bookHistoryNum + "\n" +
                "cardHistoryNum:" + cardHistoryNum + "\n" +
                "studiedCardNum:" + studiedCardNum;
        mDialog.addTextView(text, UAlignment.CenterX, true, false, UDpi.toPixel(TEXT_SIZE), UColor.BLACK, 0);
        mDialog.addCloseButton("close");
    }

    /**
     * システムクリア確認ダイアログを表示
     */
    private void showClearDialog() {
        if (mDialog != null) {
            mDialog.closeDialog();
        }
        mDialog = UDialogWindow.createInstance(this, null, UDialogWindow.ButtonDir.Horizontal,
                mParentView.getWidth(), mParentView
                        .getHeight());
        mDialog.addToDrawManager();

        mDialog.setTitle("アプリの情報を初期化しますか？");
        mDialog.addButton(ButtonIdClearOK, "OK", UColor.BLACK, UColor.OkButton);
        mDialog.addCloseButton("Cancel");
    }

    /**
     * ソフトウェアキーの戻るボタンを押したときの処理
     * @return
     */
    public boolean onBackKeyDown() {

        return false;
    }

    /**
     * Callbacks
     */

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        switch(id) {
            case ButtonIdClearOK:
                // システムを初期化する　
                UDebug.clearSystemData();

                // クリア後のダイアログ表示
                if (mDialog != null) {
                    mDialog.closeDialog();
                }
                mDialog = UDialogWindow.createInstance(this, null, UDialogWindow.ButtonDir.Horizontal,
                        mParentView.getWidth(), mParentView
                                .getHeight());
                mDialog.addToDrawManager();
                mDialog.setTitle("System data has cleared!!");

                mDialog.addCloseButton("OK", UColor.BLACK, UColor.OkButton);
                return true;
        }
        return false;
    }


    /**
     * UListItemCallbacks
     */
    /**
     * 項目がクリックされた
     * @param item
     */
    public void ListItemClicked(UListItem item) {
        ULog.print(TAG, "item clicked:" + item.getMIndex());

        switch(DebugMenu.toEnum(item.getMIndex())) {
            case ShowSystemInfo:
                showInfo();
                break;
            case DebugDB:
                PageViewManager.getInstance().stackPage(PageView.DebugDB);
                break;
            case ClearData:
                showClearDialog();
                break;
        }
    }
    public void ListItemButtonClicked(UListItem item, int buttonId) {

    }
}
