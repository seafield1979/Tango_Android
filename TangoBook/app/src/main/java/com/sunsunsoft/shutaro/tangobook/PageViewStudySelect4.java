package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import java.util.List;

/**
 * Created by shutaro on 2016/12/27.
 *
 * 学習ページ(４択)
 * 正解を１つだけふくむ４つの選択肢から正解を選ぶ学習モード
 */

public class PageViewStudySelect4 extends UPageView
        implements UButtonCallbacks, UDialogCallbacks, CardsStackCallbacks
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

    private static final int TOP_AREA_H = 150;
    private static final int BOTTOM_AREA_H = 150;
    private static final int TEXT_SIZE = 50;
    private static final int BUTTON_W = 300;
    private static final int BUTTON_H = 120;

    private static final int DRAW_PRIORITY = 100;

    // button ids
    private static final int ButtonIdExit = 100;
    private static final int ButtonIdOk = 101;
    private static final int ButtonIdNg = 102;

    private static final int ButtonIdExitOk = 200;

    /**
     * Member variables
     */
    private State mState;
    private boolean mFirstStudy;       // 単語帳を選択して最初の学習のみtrue。リトライ時はfalse

    private StudyCardsManager mCardsManager;
    private StudyCardStackSelect mCardsStack;

    private UTextView mTextCardCount;
    private UButtonText mExitButton;

    // 学習する単語帳 or カードリスト
    private TangoBook mBook;
    private List<TangoCard> mCards;

    // 終了確認ダイアログ
    private UDialogWindow mConfirmDialog;

    private boolean isCloseOk;

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
    public PageViewStudySelect4(Context context, View parentView, String title) {
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
            mCardsManager = StudyCardsManager.createInstance(mCards);
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
        mCardsStack = new StudyCardStackSelect(mCardsManager, this,
                100, TOP_AREA_H,
                screenW, mParentView.getWidth() - 200,
                mParentView.getHeight() - (TOP_AREA_H + BOTTOM_AREA_H)
        );
        mCardsStack.addToDrawManager();


        // あと〜枚
        String title = getCardsRemainText(mCardsStack.getCardCount());
        mTextCardCount = UTextView.createInstance( title, TEXT_SIZE, DRAW_PRIORITY,
                UAlignment.CenterX, screenW, false, true,
                screenW / 2, 50, 300, Color.rgb(100,50,50), 0);
        mTextCardCount.addToDrawManager();

        // 終了ボタン
        mExitButton = new UButtonText(this, UButtonType.Press,
                ButtonIdExit,
                DRAW_PRIORITY, mContext.getString(R.string.finish),
                (screenW - BUTTON_W) / 2, screenH - 150,
                BUTTON_W, BUTTON_H,
                TEXT_SIZE, Color.BLACK, Color.rgb(100,200,100));
        mExitButton.addToDrawManager();
    }

    private String getCardsRemainText(int count) {
        return String.format(mContext.getString(R.string.cards_remain), count);
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
            case ButtonIdExit:
                // 終了ボタンを押したら確認用のモーダルダイアログを表示
                if (mConfirmDialog == null) {
                    isCloseOk = false;

                    mConfirmDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                            this, this,
                            UDialogWindow.ButtonDir.Horizontal, UDialogWindow.DialogPosType.Center,
                            true, mParentView.getWidth(), mParentView.getHeight(),
                            Color.BLACK, Color.LTGRAY);
                    mConfirmDialog.addToDrawManager();
                    mConfirmDialog.setTitle(mContext.getString(R.string.confirm_exit));
                    mConfirmDialog.addButton(ButtonIdExitOk, "OK", Color.BLACK, Color.WHITE);
                    mConfirmDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
                }
                break;
            case ButtonIdOk:
                break;
            case ButtonIdNg:
                break;
            case ButtonIdExitOk:
                // 終了
                isCloseOk = true;
                mConfirmDialog.startClosing();
                break;
        }
        return false;
    }

    /**
     * UDialogCallbacks
     */
    public void dialogClosed(UDialogWindow dialog) {
        if (isCloseOk) {
            // 終了して前のページに戻る
            PageViewManager.getInstance().popPage();
        }
        if (dialog == mConfirmDialog) {
            mConfirmDialog = null;
        }
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
