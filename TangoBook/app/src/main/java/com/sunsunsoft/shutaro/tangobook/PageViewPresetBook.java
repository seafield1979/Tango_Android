package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.List;

/**
 * Created by shutaro on 2016/12/18.
 */

public class PageViewPresetBook extends UPageView
        implements UButtonCallbacks, UListItemCallbacks{
    /**
     * Constants
     */
    private static final int DRAW_PRIORITY = 100;
    private static final int DRAW_PRIORYTY_DIALOG = 50;

    private static final int TOP_Y = 50;
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 50;
    private static final int BUTTON_W = 300;
    private static final int BUTTON_H = 120;

    private static final int TEXT_SIZE = 50;
    private static final int ButtonIdReturn = 100;

    /**
     * Member variables
     */
    private UTextView mTitleText;
    private UListView mListView;
    private UButtonText mReturnButton;
    private UDialogWindow mDialog;      // OK/NGのカード一覧を表示するダイアログ

    /**
     * Constructor
     */
    public PageViewPresetBook(Context context, View parentView, String title) {
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

        int width = mParentView.getWidth();
        int height = mParentView.getHeight();

        float x = MARGIN_H;
        float y = TOP_Y;

        // Title
        mTitleText = UTextView.createInstance(UResourceManager.getStringById(R.string
                        .history_book),
                TEXT_SIZE, DRAW_PRIORITY,
                UAlignment.CenterX, width, false, false,
                width / 2, y, width, Color.BLACK, 0);
        mTitleText.addToDrawManager();
        y += mTitleText.size.height;

        // ListView
        int listViewH = height - (MARGIN_H * 3 + mTitleText.size.height + BUTTON_H);
        mListView = new UListView(null, this, DRAW_PRIORITY, x, y,
                width - MARGIN_H * 2, listViewH, 0);
        mListView.setFrameColor(Color.BLACK);
        mListView.addToDrawManager();

        // add items to ListView
        List<PresetBook> presetBooks = PresetBookManager.getInstance().getBooks();
        for (PresetBook presetBook : presetBooks) {
            ListItemPresetBook item = new ListItemPresetBook(this, presetBook, mListView.clientSize.width);
            mListView.add(item);
        }

        y += listViewH + MARGIN_H;

        // Button
        mReturnButton = new UButtonText(this, UButtonType.Press, ButtonIdReturn,
                DRAW_PRIORITY, UResourceManager.getStringById(R.string.return1),
                (width - BUTTON_W)/2, y, BUTTON_W, BUTTON_H, 50, Color.WHITE, Color.rgb(100,200,
                100));
        mReturnButton.addToDrawManager();
    }

    /**
     * ダイアログを表示する
     * @param book
     */
    private void showDialog(PresetBook book) {

        int width = mParentView.getWidth();
        // Dialog
        mDialog = UDialogWindow.createInstance(null, width, mParentView
                .getHeight());
        mDialog.addToDrawManager();
        UListView listView = new UListView(null, this,
                DRAW_PRIORYTY_DIALOG, 0, 0,
                mDialog.size.width - MARGIN_H * 2, 700, Color.LTGRAY
        );
        mDialog.addDrawable(listView);

        // Add items to ListView
        for (PresetCard card : book.mCards) {
//            ListItem
        }

        mDialog.addCloseButton(UResourceManager.getStringById(R.string.close));
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
                PageViewManager.getInstance().popPage();
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
        // クリックされた項目の学習カード一覧を表示する
        if (!(item instanceof ListItemPresetBook)) return;

        ListItemPresetBook book = (ListItemPresetBook) item;
        showDialog(book.getBook());
    }
}