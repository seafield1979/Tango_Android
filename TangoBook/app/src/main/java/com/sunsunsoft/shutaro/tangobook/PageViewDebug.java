package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by shutaro on 2016/12/15.
 *
 * Debug page
 */

public class PageViewDebug extends UPageView implements UListItemCallbacks{
    /**
     * Enums
     */

    /**
     * Constants
     */
    public static final String TAG = "PageViewDebug";
    private static final int MenuIdTop = 100;

    private static final int DRAW_PRIORITY = 100;
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 50;


    /**
     * Constructor
     */
    public PageViewDebug(Context context, View parentView, String title) {
        super(context, parentView, title);
    }

    /**
     * Member variables
     */
    private UListView mListView;


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

        for (int i=0; i<10; i++) {
            ListItemDebug item = new ListItemDebug(this, "hoge", true, 0, mListView.size.width);
            mListView.add(item);
        }

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

        switch(item.mIndex) {
            case 0:

                break;
        }
    }
}
