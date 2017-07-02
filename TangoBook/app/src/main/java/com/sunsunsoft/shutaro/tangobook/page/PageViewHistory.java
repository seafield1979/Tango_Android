package com.sunsunsoft.shutaro.tangobook.page;

/**
 * Created by shutaro on 2016/12/13.
 * 履歴ページ
 * 過去に学習した単語帳のリストを表示する
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
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
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

    // layout
    private static final int TOP_Y = 17;
    private static final int TEXT_SIZE = 17;

    // button ids
    private static final int ButtonIdReturn = 100;
    private static final int ButtonIdClearOK = 102;

    /**
     * Member variables
     */
    private ListViewStudyHistory mListView;

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

        float x = UDpi.toPixel(MARGIN_H);
        float y = UDpi.toPixel(TOP_Y);

        // ListView
        int listViewH = height - (UDpi.toPixel(MARGIN_H) * 3);
        mListView = new ListViewStudyHistory(this, DRAW_PRIORITY, x, y,
                width - UDpi.toPixel(MARGIN_H) * 2, listViewH, 0);
        if (mListView.getItemNum() > 0) {
            mListView.setFrameColor(Color.BLACK);
            mListView.addToDrawManager();
        } else {
            mListView = null;
            y += UDpi.toPixel(67);
            UTextView text = UTextView.createInstance(UResourceManager.getStringById(R.string.no_study_history),
                    UDpi.toPixel(TEXT_SIZE), DRAW_PRIORITY - 1,
                    UAlignment.CenterX, width, false, false,
                    width / 2, y, width, Color.BLACK, 0);
            text.addToDrawManager();
        }

        y += listViewH + UDpi.toPixel(MARGIN_H);

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
     * アクションIDを処理する
     * サブクラスでオーバーライドして使用する
     */
    public void setActionId(int id) {
        switch (id) {
            case R.id.action_clear_history: {
                // クリア確認ダイアログを表示する
                // お問い合わせメールダイアログを表示
                if (mDialog != null) {
                    mDialog.closeDialog();
                }

                boolean isEmpty = false;
                String title, message;


                if (mListView == null || mListView.getItemNum() == 0) {
                    isEmpty = true;
                }
                if (isEmpty) {
                    // リストが空の場合はクリアできないメッセージを表示
                    title = UResourceManager.getStringById(R.string.error);
                    message = UResourceManager.getStringById(R.string
                            .study_list_is_empty1);
                } else {
                    // リストがある場合はクリア確認メッセージを表示
                    title = UResourceManager.getStringById(R.string.confirm);
                    message = UResourceManager.getStringById(R.string
                            .confirm_clear_history);
                }

                mDialog = UDialogWindow.createInstance(this, this,
                        UDialogWindow.ButtonDir.Horizontal,
                        mParentView.getWidth(),
                        mParentView.getHeight());
                mDialog.setTitle(title);
                mDialog.addTextView(message,
                        UAlignment.CenterX, true, false, UDpi.toPixel(TEXT_SIZE), TEXT_COLOR, 0);

                if (isEmpty) {
                    mDialog.addCloseButton("OK", TEXT_COLOR, UColor.WHITE);
                } else {
                    mDialog.addButton(ButtonIdClearOK,
                            "OK",TEXT_COLOR,
                            Color.WHITE);
                    mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));                  }
                mDialog.addToDrawManager();

                mParentView.invalidate();
            }
            break;
        }
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
        int height = mParentView.getHeight();

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
                mDialog.getSize().width - UDpi.toPixel(MARGIN_H) * 2, height - UDpi.toPixel(67 + MARGIN_H) * 2, Color.WHITE
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
