package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.List;

/**
 * Created by shutaro on 2016/12/22.
 */

public class PageViewDebugDB extends UPageView implements UListItemCallbacks{
    /**
     * Enums
     */
    enum DebugMenu {
        ShowTangoCard,
        ShowTangoBook,
        ShowItemPos,
        GetNoParentItems,
        RescureNoParentItems,
        ClearAll        // 全てのDBを空にする
        ;

        public static DebugMenu toEnum(int value) {
            if (value < DebugMenu.values().length) {
                return DebugMenu.values()[value];
            }
            return ShowTangoCard;
        }
    }

    /**
     * Constants
     */
    public static final String TAG = "PageViewDebugDB";
    private static final int MenuIdTop = 100;

    private static final int DRAW_PRIORITY = 100;
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 50;


    /**
     * Constructor
     */
    public PageViewDebugDB(Context context, View parentView, String title) {
        super(context, parentView, title);
    }

    /**
     * Member variables
     */
    private UListView mListView;
    private UDialogWindow mDialog;


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

        mListView = new UListView(null, this, DRAW_PRIORITY, MARGIN_H, MARGIN_V,
                mParentView.getWidth() - MARGIN_H * 2, mParentView.getHeight() - MARGIN_V * 2,
                Color.WHITE);
        mListView.addToDrawManager();

        for (DebugMenu menu : DebugMenu.values()) {
            ListItemDebug item = new ListItemDebug(this, menu.toString(),
                    true, 0, mListView.size.width);
            mListView.add(item);
        }
    }

    /**
     * Drawableを表示するダイアログ表示テスト
     */
    private void showDrawableDialog() {
        if (mDialog != null) {
            mDialog.closeDialog();
        }
        mDialog = UDialogWindow.createInstance(null, null, UDialogWindow.ButtonDir.Horizontal,
                mParentView.getWidth(), mParentView
                .getHeight());
        mDialog.addToDrawManager();

        UTextView textView = UTextView.createInstance("hello world", 0, mParentView.getWidth(),
                false, 0, 0);
        mDialog.addDrawable(textView);
        mDialog.addCloseButton("close");
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
        ULog.print(TAG, "item clicked:" + item.mIndex);

        switch(DebugMenu.toEnum(item.mIndex)) {
            case ShowTangoCard:
                RealmManager.getCardDao().selectAll();
                break;
            case ShowTangoBook:
                RealmManager.getBookDao().selectAll();
                break;
            case ShowItemPos:
                RealmManager.getItemPosDao().selectAll();
                break;
            case GetNoParentItems: {
                List<TangoItem> items = RealmManager.getCheckDao().selectNoParentItems();
                for (TangoItem _item : items) {
                    ULog.print(TAG, "itemType:" + _item.getItemType() + " itemId:"+ _item.getId()
                            + " title:" + _item.getTitle());
                }

            }
                break;
            case RescureNoParentItems:
                // 親のないアイテムをホームに移動する
                List<TangoItem> items = RealmManager.getCheckDao().selectNoParentItems();
                RealmManager.getItemPosDao().moveNoParentItems(items, TangoParentType.Home
                        .ordinal(), 0);
                break;
            case ClearAll:
            {
                RealmManager.clearAll();
            }
                break;
        }
    }
    public void ListItemButtonClicked(UListItem item, int buttonId) {

    }
}
