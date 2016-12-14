package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.List;

/**
 * Created by shutaro on 2016/12/05.
 */

public class PageViewResult extends UPageView
        implements UButtonCallbacks, UListItemCallbacks
{
    /**
     * Constants
     */
    public static final String TAG = "PageViewResult";

    private static final int ButtonIdRetry1 = 200;
    private static final int ButtonIdRetry2 = 201;
    private static final int ButtonIdReturn = 202;

    private static final int TOP_Y = 30;
    private static final int PRIORITY_LV = 100;
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 50;

    private static final int TITLE_TEXT_SIZE = 70;
    private static final int DRAW_PRIORITY = 100;
    private static final int TEXT_SIZE = 50;
    private static final int BUTTON_TEXT_SIZE = 50;
    private static final int TEXT_COLOR = Color.BLACK;

    private static final int BUTTON_H = 200;
    private static final int TITLE_BG_COLOR = Color.rgb(100,100,200);
    private static final int BUTTON_TEXT_COLOR = Color.WHITE;
    private static final int BUTTON1_BG_COLOR = Color.rgb(100,200,100);
    private static final int BUTTON2_BG_COLOR = Color.rgb(200,100,100);

    /**
     * Member variables
     */
    private TangoBook mBook;
    private ListViewResult mListView;
    private List<TangoCard> mOkCards;
    private List<TangoCard> mNgCards;
    private boolean mStudyMode;             // 出題モード(false:英->日 / true:日->英)

    private UTextView mTitleText;           // タイトル
    private UTextView mBookNameText;        // Book名
    private UTextView mResultText;          // 結果
    private UButtonText mButtonRetry1;      // 全部リトライ
    private UButtonText mButtonRetry2;      // OKのみリトライ
    private UButtonText mButtonReturn;      // 戻るボタン

    private UDialogWindow mDialog;

    /**
     * Get/Set
     */
    public void setCardsLists(List<TangoCard> okCards, List<TangoCard> ngCards) {
        mOkCards = okCards;
        mNgCards = ngCards;
    }

    public void setBook(TangoBook mBook) {
        this.mBook = mBook;
    }

    /**
     * Constructor
     */
    public PageViewResult(Context context, View parentView) {
        super(context, parentView);
    }
    /**
     * Methods
     */

    public void onShow() {
        mStudyMode = MySharedPref.readBoolean(MySharedPref.StudyOption1Key);
    }

    public void onHide() {
        super.onHide();
    }

    /**
     * そのページで表示される描画オブジェクトを初期化する
     */
    public void initDrawables() {
        UDrawManager.getInstance().init();

        int width = mParentView.getWidth();
        int height = mParentView.getHeight();

        float y = TOP_Y;
        // Title
        mTitleText = UTextView.createInstance(UResourceManager.getStringById(R.string
                .title_result),
                TITLE_TEXT_SIZE,
                DRAW_PRIORITY,
                UAlignment.CenterX, width, false, false, width / 2, y, width,
                TEXT_COLOR, 0);
        mTitleText.addToDrawManager();
        y += mTitleText.size.height;

        // Book Name
        mBookNameText = UTextView.createInstance(mBook.getName(), TEXT_SIZE, DRAW_PRIORITY,
                UAlignment.CenterX, width, false, false, width / 2, y, width,
                TEXT_COLOR, 0);
        mBookNameText.addToDrawManager();
        y += mBookNameText.size.height;

        // Result
        String text = "OK: " + mOkCards.size() + "  NG: " + mNgCards.size();
        mResultText = UTextView.createInstance(text, TEXT_SIZE, DRAW_PRIORITY,
                UAlignment.CenterX, width, false, false, width / 2, y, width,
                TEXT_COLOR, 0);
        mResultText.addToDrawManager();
        y += mResultText.size.height;

        // Buttons
        int buttonW = (width - MARGIN_H * 4) / 3;
        float x = MARGIN_H;
        mButtonRetry1 = new UButtonText(this, UButtonType.Press, ButtonIdRetry1,
                DRAW_PRIORITY,UResourceManager.getStringById(R.string.retry1),
                x, y, buttonW, BUTTON_H,
                BUTTON_TEXT_SIZE, BUTTON_TEXT_COLOR, BUTTON1_BG_COLOR);
        mButtonRetry1.addToDrawManager();
        x += buttonW + MARGIN_H;

        mButtonRetry2 = new UButtonText(this, UButtonType.Press, ButtonIdRetry2,
                DRAW_PRIORITY, UResourceManager.getStringById(R.string.retry2),
                x, y, buttonW, BUTTON_H,
                BUTTON_TEXT_SIZE, BUTTON_TEXT_COLOR, BUTTON1_BG_COLOR);
        mButtonRetry2.addToDrawManager();
        x += buttonW + MARGIN_H;

        mButtonReturn = new UButtonText(this, UButtonType.Press, ButtonIdReturn,
                DRAW_PRIORITY, UResourceManager.getStringById(R.string.finish),
                x, y, buttonW, BUTTON_H,
                BUTTON_TEXT_SIZE, BUTTON_TEXT_COLOR, BUTTON2_BG_COLOR);
        mButtonReturn.addToDrawManager();

        y += BUTTON_H + MARGIN_V;

        // ListView
        mListView = new ListViewResult(this, mOkCards, mNgCards, mStudyMode,
                PRIORITY_LV, MARGIN_H, y,
                width - MARGIN_H * 2, height - (int)y - MARGIN_V, Color.WHITE);

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
            case ButtonIdRetry1:
                PageViewManagerTango.getInstance().startStudyPage(mBook, null, false);
                break;
            case ButtonIdRetry2:
                PageViewManagerTango.getInstance().startStudyPage(mBook, mNgCards, false);
                break;
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
        // クリックされた項目の詳細を表示する
        if (!(item instanceof ListItemResult)) return;

        ListItemResult _item = (ListItemResult)item;
        if (_item.getType() != ListItemResult.ListItemResultType.Title) {
            int _color;
            if (_item.getType() == ListItemResult.ListItemResultType.OK) {
                _color = Color.rgb(150, 250, 150);
            } else {
                _color = Color.rgb(250, 150, 150);
            }
            mDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                    this, null, UDialogWindow.ButtonDir.Vertical,
                    UDialogWindow.DialogPosType.Center, false,
                    mParentView.getWidth(), mParentView.getHeight(),
                    Color.BLACK, _color);
            // 項目を追加
            TangoCard card = _item.getCard();
            mDialog.addTextView(card.getWordA(), UAlignment.CenterX, false, false, 50,
                    Color.BLACK, Color.WHITE);
            mDialog.addTextView(card.getWordB(), UAlignment.CenterX, false, false, 50,
                    Color.BLACK, Color.WHITE);
            mDialog.addTextView(card.getComment(), UAlignment.CenterX, false, false, 50,
                    Color.BLACK, Color.WHITE);
            mDialog.addCloseButton("Close");
        }
    }
}
