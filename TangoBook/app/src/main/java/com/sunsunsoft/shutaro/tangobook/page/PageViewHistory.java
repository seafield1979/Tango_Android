package com.sunsunsoft.shutaro.tangobook.page;

/**
 * Created by shutaro on 2016/12/13.
 * 履歴ページ
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.database.TangoBookHistory;
import com.sunsunsoft.shutaro.tangobook.database.TangoStudiedCard;
import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.study_card.StudyMode;
import com.sunsunsoft.shutaro.tangobook.study_card.StudyType;
import com.sunsunsoft.shutaro.tangobook.listview.*;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonText;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonType;
import com.sunsunsoft.shutaro.tangobook.uview.text.UTextView;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;
import com.sunsunsoft.shutaro.tangobook.uview.window.UDialogCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.window.UDialogWindow;

import java.util.List;

public class PageViewHistory extends UPageView
        implements UDialogCallbacks, UButtonCallbacks, UListItemCallbacks {
    /**
     * Constants
     */
    private static final int DRAW_PRIORITY = 100;
    private static final int DRAW_PRIORYTY_DIALOG = 50;

    private static final int TOP_Y = 50;
    private static final int BUTTON_W = 300;
    private static final int BUTTON_H = 120;

    private static final int TEXT_SIZE = 50;

    // button ids
    private static final int ButtonIdReturn = 100;
    private static final int ButtonIdClear = 101;
    private static final int ButtonIdClearOK = 102;

    /**
     * Member variables
     */
    private UTextView mTitleText;
    private ListViewStudyHistory mListView;
    private UButtonText mClearButton;

    private UDialogWindow mDialog;      // OK/NGのカード一覧を表示するダイアログ

    /**
     * Constructor
     */
    public PageViewHistory(Context context, View parentView, String title) {
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
        y += mTitleText.getSize().height + MARGIN_V;

        // ListView
        int listViewH = height - (MARGIN_H * 3 + mTitleText.getSize().height);
        mListView = new ListViewStudyHistory(this, DRAW_PRIORITY, x, y,
                width - MARGIN_H * 2, listViewH, 0);
        if (mListView.getItemNum() > 0) {
            mListView.setFrameColor(Color.BLACK);
            mListView.addToDrawManager();

            // Clear button
            mClearButton = new UButtonText(this, UButtonType.Press, ButtonIdClear,
                    DRAW_PRIORITY, UResourceManager.getStringById(R.string.clear),
                    width - BUTTON_W - MARGIN_H, 20,
                    BUTTON_W, BUTTON_H,
                    TEXT_SIZE, Color.WHITE, UColor.Salmon );
            mClearButton.addToDrawManager();

        } else {
            mListView = null;
            y += 200;
            UTextView text = UTextView.createInstance(UResourceManager.getStringById(R.string.no_study_history),
                    TEXT_SIZE, DRAW_PRIORITY - 1,
                    UAlignment.CenterX, width, false, false,
                    width / 2, y, width, Color.BLACK, 0);
            text.addToDrawManager();
        }

        y += listViewH + MARGIN_H;

    }

    /**
     * ソフトウェアキーの戻るボタンを押したときの処理
     * @return
     */
    public boolean onBackKeyDown() {
        if (mDialog != null) {
            if (!mDialog.isClosing()) {
                mDialog.startClosing();
            }
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
            case ButtonIdClear:
            {
                // クリア確認ダイアログを表示する
                // お問い合わせメールダイアログを表示
                if (mDialog == null) {
                    mDialog = UDialogWindow.createInstance(this, this,
                            UDialogWindow.ButtonDir.Horizontal,
                            mParentView.getWidth(),
                            mParentView.getHeight());
                    mDialog.setTitle(UResourceManager.getStringById(R.string.confirm));
                    mDialog.addTextView(UResourceManager.getStringById(R.string
                            .confirm_clear_history),
                            UAlignment.CenterX, true, false, TEXT_SIZE, TEXT_COLOR, 0);
                    mDialog.addButton(ButtonIdClearOK,
                            "OK",TEXT_COLOR,
                            Color.WHITE);
                    mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
                    mDialog.addToDrawManager();
                }

            }
                break;
            case ButtonIdClearOK:
                RealmManager.getBookHistoryDao().deleteAll();
                mListView.clear();
                mDialog.startClosing();

                mParentView.invalidate();
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
        if (!(item instanceof ListItemStudiedBook)) return;

        int width = mParentView.getWidth();

        ListItemStudiedBook studiedBook = (ListItemStudiedBook)item;
        if (studiedBook.getType() != ListItemStudiedBookType.History) return;

        TangoBookHistory history = studiedBook.getBookHistory();
        List<TangoStudiedCard> cards = RealmManager
                .getStudiedCardDao().selectByHistoryId(history.getId());

        // Dialog
        mDialog = UDialogWindow.createInstance(this, this,
                UDialogWindow.ButtonDir.Horizontal, width, mParentView.getHeight());
        mDialog.addToDrawManager();
        ListViewResult listView = new ListViewResult(null, cards, StudyMode.SlideOne,
                StudyType.EtoJ,
                DRAW_PRIORYTY_DIALOG, 0, 0,
                mDialog.getSize().width - MARGIN_H * 2, 700, Color.WHITE
                );
        mDialog.addDrawable(listView);

        mDialog.addCloseButton(UResourceManager.getStringById(R.string.close));
    }

    public void ListItemButtonClicked(UListItem item, int buttonId) {

    }

    /**
     * UDialogCallbacks
     */
    public void dialogClosed(UDialogWindow dialog) {
        if (mDialog == dialog) {
            mDialog = null;
        }
    }
}
