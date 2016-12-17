package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.List;

/**
 * Created by shutaro on 2016/12/16.
 *
 * 学習する単語帳を選択するページ(リストビュー版)
 */

public class PageViewStudySelect2 extends UPageView
        implements UButtonCallbacks, UListItemCallbacks, UWindowCallbacks{
    /**
     * Constants
     */
    private static final int DRAW_PRIORITY = 100;

    private static final int TOP_Y = 50;
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 50;
    private static final int BUTTON_W = 300;
    private static final int BUTTON_H = 120;

    private static final int TEXT_SIZE = 50;
    private static final int ButtonIdReturn = 100;

    // 開始ダイアログ(PreStudyWindow)でボタンが押されたときに使用する
    public static final int ButtonIdStartStudy = 2001;
    public static final int ButtonIdCancel = 2002;

    /**
     * Member variables
     */
    private UTextView mTitleText;
    private UListView mListView;
    private UButtonText mReturnButton;
    private TangoBook mBook;

    // 学習開始前のオプション等を選択するダイアログ
    private PreStudyWindow mPreStudyWindow;

    /**
     * Constructor
     */
    public PageViewStudySelect2(Context context, View parentView, String title) {
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
        int listViewH = height - (TOP_Y + MARGIN_H * 3 + mTitleText.size.height + BUTTON_H);
        mListView = new UListView(null, this, DRAW_PRIORITY, x, y,
                width - MARGIN_H * 2, listViewH, 0);
        mListView.setFrameColor(Color.BLACK);
        mListView.addToDrawManager();
        // アイテムを追加
        List<TangoItem> books = RealmManager.getItemPosDao().selectItemsByParentType(
                TangoParentType.Home, 0, TangoItemType.Book, true);
        for (TangoItem book : books) {
            TangoBook _book = (TangoBook)book;
            ListItemStudyBook listItem = new ListItemStudyBook(this, _book, mListView.size.width,
                    Color.WHITE);
            mListView.add(listItem);
        }

        y += listViewH + MARGIN_H;

        // Button
        mReturnButton = new UButtonText(this, UButtonType.Press, ButtonIdReturn,
                DRAW_PRIORITY, UResourceManager.getStringById(R.string.return1),
                (width - BUTTON_W)/2, y, BUTTON_W, BUTTON_H, 50, Color.WHITE, Color.rgb(100,200,
                100));
        mReturnButton.addToDrawManager();

        // PreStudyWindow 学習開始前に設定を行うウィンドウ
        mPreStudyWindow = new PreStudyWindow( this, this, mParentView);
        mPreStudyWindow.addToDrawManager();

    }

    private void initListView() {

    }

    /**
     * ソフトウェアキーの戻るボタンを押したときの処理
     * @return
     */
    public boolean onBackKeyDown() {
        if (mPreStudyWindow != null) {
            mPreStudyWindow.setShow(false);
            return true;
        }

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
            case ButtonIdStartStudy:
                // 学習開始
                PageViewManager.getInstance().startStudyPage( mBook, true);
                break;
            case ButtonIdCancel:
                mPreStudyWindow.setShow(false);
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
        // 学習開始前のダイアログを表示する
        if (!(item instanceof ListItemStudyBook)) return;

        ListItemStudyBook bookItem = (ListItemStudyBook)item;

        mPreStudyWindow.showWithBook(bookItem.getBook());
        mBook = bookItem.getBook();
    }

    /**
     * UWindowCallbacks
     */
    public void windowClose(UWindow window) {
        // Windowを閉じる
        if (mPreStudyWindow == window) {
            mPreStudyWindow.setShow(false);
        }
    }
}
