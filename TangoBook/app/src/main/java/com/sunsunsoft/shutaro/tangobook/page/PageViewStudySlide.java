package com.sunsunsoft.shutaro.tangobook.page;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.study_card.CardsStackCallbacks;
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.study_card.StudyCard;
import com.sunsunsoft.shutaro.tangobook.study_card.StudyCardsManager;
import com.sunsunsoft.shutaro.tangobook.study_card.StudyCardsStack;
import com.sunsunsoft.shutaro.tangobook.study_card.StudyUtil;
import com.sunsunsoft.shutaro.tangobook.database.TangoBook;
import com.sunsunsoft.shutaro.tangobook.database.TangoCard;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonText;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonType;
import com.sunsunsoft.shutaro.tangobook.uview.text.UTextView;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;

import java.util.List;

/**
 * Created by shutaro on 2016/12/05.
 *
 * 単語学習ページ
 * カードが全てOK/NG処理されるまで上から単語カードが降ってくる
 */

public class PageViewStudySlide extends PageViewStudy
        implements CardsStackCallbacks
{
    /**
     * Enums
     */
    enum State {
        Start,
        Main,
        Finish
    }

    /**
     * Constants
     */
    public static final String TAG = "PageViewStudySlide";

    private static final int TOP_AREA_H = 50;
    private static final int BOTTOM_AREA_H = 100;
    private static final int TEXT_SIZE = 17;
    private static final int BUTTON_W = 100;
    private static final int BUTTON_H = 40;
    private static final int BOX_W = 50;
    private static final int BOX_H = 50;
    private static final int MARGIN_V = 27;
    private static final int MARGIN_H = 17;

    private static final int DRAW_PRIORITY = 100;

    // button ids
    private static final int ButtonIdOk = 101;
    private static final int ButtonIdNg = 102;


    /**
     * Member variables
     */
    private State mState;
    private boolean mFirstStudy;       // 単語帳を選択して最初の学習のみtrue。リトライ時はfalse

    private StudyCardsManager mCardsManager;
    private StudyCardsStack mCardsStack;

    private UTextView mTextCardCount;
    private UButtonText mExitButton;
    private UImageView mOkView, mNgView;

    // 学習する単語帳 or カードリスト
    private TangoBook mBook;
    private List<TangoCard> mCards;

    /**
     * Get/Set
     */
    public void setBook(TangoBook book) {
        mBook = book;
    }

    public void setCards(List<TangoCard> cards) {
        mCards = cards;
    }

    public void setFirstStudy(boolean firstStudy) {
        mFirstStudy = firstStudy;
    }

    /**
     * Constructor
     */
    public PageViewStudySlide(Context context, View parentView, String title) {
        super(context, parentView, title);

    }

    /**
     * Methods
     */
    protected void onShow() {
        UDrawManager.getInstance().init();

        mState = State.Main;
        if (mCards != null) {
            // リトライ時
            mCardsManager = StudyCardsManager.createInstance(mBook.getId(), mCards);
        } else {
            // 通常時(選択された単語帳)
            mCardsManager = StudyCardsManager.createInstance(mBook);
        }
    }

    protected void onHide() {
        super.onHide();
        mCardsManager = null;
        mCardsStack.cleanUp();
        mCardsStack = null;
        mCards = null;
    }

    /**
     * 毎フレームの処理
     * @return true:処理中
     */
    public DoActionRet doAction() {
        switch (mState) {
            case Start:
                break;
            case Main:
                break;
            case Finish:
                return DoActionRet.Done;
        }
        return DoActionRet.None;
    }

    /**
     * そのページで表示される描画オブジェクトを初期化する
     */
    public void initDrawables() {
        int screenW = mParentView.getWidth();
        int screenH = mParentView.getHeight();

        // カードスタック
        mCardsStack = new StudyCardsStack(mCardsManager, this,
                (mParentView.getWidth() - StudyCard.WIDTH) / 2, UDpi.toPixel(TOP_AREA_H),
                screenW, StudyCard.WIDTH,
                mParentView.getHeight() - UDpi.toPixel(TOP_AREA_H + BOTTOM_AREA_H)
        );
        mCardsStack.addToDrawManager();


        // あと〜枚
        String title = getCardsRemainText(mCardsStack.getCardCount());
        mTextCardCount = UTextView.createInstance( title, UDraw.getFontSize(FontSize.L), DRAW_PRIORITY,
                UAlignment.CenterX, screenW, false, true,
                screenW / 2, UDpi.toPixel(10), UDpi.toPixel(100), Color.rgb(100,50,50), 0);
        mTextCardCount.addToDrawManager();

        // 終了ボタン
        mExitButton = new UButtonText(this, UButtonType.Press,
                ButtonIdExit,
                DRAW_PRIORITY, mContext.getString(R.string.finish),
                (screenW - UDpi.toPixel(BUTTON_W)) / 2,
                screenH - UDpi.toPixel(50),
                UDpi.toPixel(BUTTON_W), UDpi.toPixel(BUTTON_H),
                UDpi.toPixel(TEXT_SIZE), Color.BLACK, UColor.ExitButton);
        mExitButton.addToDrawManager();

        // OK
        mOkView = new UImageView(DRAW_PRIORITY, R.drawable.box1,
                       screenW - UDpi.toPixel( BOX_W + MARGIN_H),
                        screenH - UDpi.toPixel(BOX_H + MARGIN_V),
                UDpi.toPixel(BOX_W), UDpi.toPixel(BOX_H), UColor.DarkGreen);
        mOkView.setTitle(UResourceManager.getStringById(R.string.know), UDpi.toPixel(17), UColor.DarkGreen);
        mOkView.addToDrawManager();

        // NG
        mNgView = new UImageView(DRAW_PRIORITY, R.drawable.box1,
                UDpi.toPixel(MARGIN_H), screenH - UDpi.toPixel(BOX_H + MARGIN_V),
                UDpi.toPixel(BOX_W), UDpi.toPixel(BOX_H), UColor.DarkRed);
        mNgView.setTitle(UResourceManager.getStringById(R.string.dont_know), UDpi.toPixel(17), UColor.DarkRed);
        mNgView.addToDrawManager();


        // OK/NGボタンの座標をCardsStackに教えてやる
        PointF _pos = mOkView.getPos();
        mCardsStack.setOkBoxPos(_pos.x - (mCardsStack.getX() + mCardsStack.getWidth() / 2),
                _pos.y - mCardsStack.getY());
        _pos = mNgView.getPos();
        mCardsStack.setNgBoxPos(_pos.x - (mCardsStack.getX() + mCardsStack.getWidth() / 2),
                _pos.y - mCardsStack.getY());
    }

    private String getCardsRemainText(int count) {
        return String.format(mContext.getString(R.string.cards_remain), count);
    }


    /**
     * Callbacks
     */

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        if (super.UButtonClicked(id, pressedOn)) {
            return true;
        }

        switch(id) {
            case ButtonIdOk:
                break;
            case ButtonIdNg:
                break;
        }
        return false;
    }


    /**
     * CardsStackCallbacks
     */
    public void CardsStackChangedCardNum(int count) {
        String title = getCardsRemainText(count);
        mTextCardCount.setText(title);
    }

    /**
     * 学習終了時のイベント
     */
    public void CardsStackFinished() {
        if (mFirstStudy) {
            // 学習結果をDBに保存する
            mFirstStudy = false;

            StudyUtil.saveStudyResult(mCardsManager, mBook);
        }

        // カードが０になったので学習完了。リザルトページに遷移
        mState = State.Finish;
        PageViewManager.getInstance().startStudyResultPage( mBook,
                mCardsManager.getOkCards(), mCardsManager.getNgCards());

        mParentView.invalidate();
    }


}
