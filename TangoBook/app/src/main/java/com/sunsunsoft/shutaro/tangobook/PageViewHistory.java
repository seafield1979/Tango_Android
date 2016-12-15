package com.sunsunsoft.shutaro.tangobook;

/**
 * Created by shutaro on 2016/12/13.
 * 履歴ページ
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class PageViewHistory extends UPageView implements UButtonCallbacks, UListItemCallbacks{
    /**
     * Constants
     */
    private static final int DRAW_PRIORITY = 100;

    private static final int TOP_Y = 50;
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 50;
    private static final int BUTTON_W = 300;
    private static final int BUTTON_H = 120;

    private static final int TITLE_TEXT_SIZE = 70;
    private static final int TITLE_TEXT_COLOR = Color.rgb(150,150,50);

    private static final int ButtonIdReturn = 100;

    /**
     * Member variables
     */
    private UTextView mTitleText;
    private ListViewStudyHistory mListView;
    private UButtonText mReturnButton;

    /**
     * Constructor
     */
    public PageViewHistory(Context context, View parentView) {
        super(context, parentView);
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

        int width = mParentView.getWidth();
        int height = mParentView.getHeight();

        float x = MARGIN_H;
        float y = TOP_Y;

        // Title
        mTitleText = UTextView.createInstance(UResourceManager.getStringById(R.string
                .title_history),
                TITLE_TEXT_SIZE, DRAW_PRIORITY,
                UAlignment.CenterX, width, false, false,
                width / 2, y, width, TITLE_TEXT_COLOR, 0);
        mTitleText.addToDrawManager();
        y += mTitleText.size.height + MARGIN_H;

        // ListView
        int listViewH = height - (TOP_Y + MARGIN_H * 3 + mTitleText.size.height + BUTTON_H);
        mListView = new ListViewStudyHistory(this, DRAW_PRIORITY, x, y,
                width - MARGIN_H * 2, listViewH, 0);
        mListView.setFrameColor(Color.BLACK);

        y += listViewH + MARGIN_H;

        // Button
        mReturnButton = new UButtonText(this, UButtonType.Press, ButtonIdReturn,
                DRAW_PRIORITY, UResourceManager.getStringById(R.string.return1),
                (width - BUTTON_W)/2, y, BUTTON_W, BUTTON_H, 50, Color.WHITE, Color.rgb(100,200,
                100));
        mReturnButton.addToDrawManager();
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
            case ButtonIdReturn:
                PageViewManagerTango.getInstance().popPage();
                break;
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

    }
}
