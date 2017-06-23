package com.sunsunsoft.shutaro.tangobook.page;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.database.TangoBook;
import com.sunsunsoft.shutaro.tangobook.database.TangoCard;
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;
import com.sunsunsoft.shutaro.tangobook.study_card.StudyMode;
import com.sunsunsoft.shutaro.tangobook.study_card.StudyType;
import com.sunsunsoft.shutaro.tangobook.listview.ListItemResult;
import com.sunsunsoft.shutaro.tangobook.listview.ListViewResult;
import com.sunsunsoft.shutaro.tangobook.u_dialog.DialogCard;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonText;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonType;
import com.sunsunsoft.shutaro.tangobook.uview.text.UTextView;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;

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

    // 座標系
    private static final int TOP_Y = 10;
    private static final int MARGIN_H = 17;
    private static final int MARGIN_V = 17;
    private static final int MARGIN_V_S = 7;

    private static final int TITLE_TEXT_SIZE = 23;
    private static final int TEXT_SIZE = 17;
    private static final int BUTTON_TEXT_SIZE = 17;
    private static final int BUTTON_H = 67;

    // 優先順位系
    private static final int PRIORITY_LV = 100;
    private static final int DRAW_PRIORITY = 100;

    // color
    private static final int TEXT_COLOR = Color.BLACK;

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
    private StudyMode mStudyMode;             // 出題モード
    private StudyType mStudyType;           // 出題タイプ(英->日, 日->英)

    private UTextView mTitleText;           // タイトル
    private UTextView mResultText;          // 結果
    private UButtonText mButtonRetry1;      // 全部リトライ
    private UButtonText mButtonRetry2;      // NGのみリトライ
    private UButtonText mButtonExit;      // 戻るボタン

    private DialogCard mCardDialog;

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
    public PageViewResult(Context context, View parentView, String title) {
        super(context, parentView, title);
    }
    /**
     * Methods
     */

    public void onShow() {
        mStudyMode = StudyMode.toEnum(MySharedPref.readInt(MySharedPref.StudyModeKey));
        mStudyType = StudyType.toEnum(MySharedPref.readInt(MySharedPref.StudyTypeKey));
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

        float y = UDpi.toPixel(TOP_Y);
        int buttonH = UDpi.toPixel(BUTTON_H);
        int marginH = UDpi.toPixel(MARGIN_H);
        int marginV = UDpi.toPixel(MARGIN_V);

        // Title
        String title = String.format(UResourceManager.getStringById(R.string
                .title_result2), mBook.getName());
        mTitleText = UTextView.createInstance(title,
                UDpi.toPixel(TITLE_TEXT_SIZE),
                DRAW_PRIORITY,
                UAlignment.CenterX, width, false, false, width / 2, y, width,
                TEXT_COLOR, 0);
        mTitleText.addToDrawManager();
        y += mTitleText.getHeight() + UDpi.toPixel(MARGIN_V_S);

        // Result
        String text = "OK: " + mOkCards.size() + "  NG: " + mNgCards.size();
        mResultText = UTextView.createInstance(text, UDpi.toPixel(TEXT_SIZE), DRAW_PRIORITY,
                UAlignment.CenterX, width, false, false, width / 2, y, width,
                TEXT_COLOR, 0);
        mResultText.addToDrawManager();
        y += mResultText.getHeight() + UDpi.toPixel(MARGIN_V_S);

        // Buttons
        int buttonW = (width - marginH * 4) / 3;
        float x = UDpi.toPixel(MARGIN_H);
        // Retury1
        mButtonRetry1 = new UButtonText(this, UButtonType.Press, ButtonIdRetry1,
                DRAW_PRIORITY,UResourceManager.getStringById(R.string.retry1),
                x, y, buttonW, buttonH,
                UDpi.toPixel(BUTTON_TEXT_SIZE), BUTTON_TEXT_COLOR, BUTTON1_BG_COLOR);
        mButtonRetry1.addToDrawManager();
        x += buttonW + marginH;

        // Retry2
        mButtonRetry2 = new UButtonText(this, UButtonType.Press, ButtonIdRetry2,
                DRAW_PRIORITY, UResourceManager.getStringById(R.string.retry2),
                x, y, buttonW, buttonH,
                UDpi.toPixel(BUTTON_TEXT_SIZE), BUTTON_TEXT_COLOR, BUTTON1_BG_COLOR);
        mButtonRetry2.addToDrawManager();
        if (mNgCards.size() == 0) {
            mButtonRetry2.setEnabled(false);
        }
        x += buttonW + marginH;

        // Exit
        mButtonExit = new UButtonText(this, UButtonType.Press, ButtonIdReturn,
                DRAW_PRIORITY, UResourceManager.getStringById(R.string.finish),
                x, y, buttonW, buttonH,
                UDpi.toPixel(BUTTON_TEXT_SIZE), BUTTON_TEXT_COLOR, BUTTON2_BG_COLOR);
        mButtonExit.addToDrawManager();

        y += buttonH + marginV;

        // ListView
        mListView = new ListViewResult(this, mOkCards, mNgCards, mStudyMode, mStudyType,
                PRIORITY_LV, marginH, y,
                width - marginH * 2, height - (int)y - marginV, Color.WHITE);
        mListView.addToDrawManager();
        mListView.setFrameColor(Color.BLACK);
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
                PageViewManager.getInstance().startStudyPage(mBook, null, false);
                break;
            case ButtonIdRetry2:
                PageViewManager.getInstance().startStudyPage(mBook, mNgCards, false);
                break;
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
        // クリックされた項目の詳細を表示する
        if (!(item instanceof ListItemResult)) return;

        ListItemResult _item = (ListItemResult)item;
        if (_item.getType() != ListItemResult.ListItemResultType.Title) {

            mCardDialog = new DialogCard(_item.getCard(), true, mParentView.getWidth(), mParentView
                    .getHeight());
            mCardDialog.addToDrawManager();
        }
    }

    public void ListItemButtonClicked(UListItem item, int buttonId) {

    }
}
