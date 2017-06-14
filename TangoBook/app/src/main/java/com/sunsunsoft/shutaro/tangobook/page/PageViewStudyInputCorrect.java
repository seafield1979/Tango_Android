package com.sunsunsoft.shutaro.tangobook.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.study_card.CardsStackCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.study_card.StudyCardStackInput;
import com.sunsunsoft.shutaro.tangobook.study_card.StudyCardsManager;
import com.sunsunsoft.shutaro.tangobook.study_card.StudyUtil;
import com.sunsunsoft.shutaro.tangobook.database.TangoBook;
import com.sunsunsoft.shutaro.tangobook.database.TangoCard;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;

import java.util.List;

/**
 * Created by shutaro on 2016/12/29.
 *
 * 学習モードのページ
 * 正解を１文字ずつ入力する。１文字でも間違って入力したらNG
 */

public class PageViewStudyInputCorrect extends PageViewStudy
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

    private static final int TOP_AREA_H = 150;
    private static final int BOTTOM_AREA_H = 150;
    private static final int TEXT_SIZE = 50;
    private static final int BUTTON_W = 300;
    private static final int BUTTON_H = 120;
    private static final int DRAW_PRIORITY = 100;
    private static final int SETTING_BUTTON_W = 120;

    // button ids
    private static final int ButtonIdSkip = 101;
    private static final int ButtonIdSetting = 102;
    private static final int ButtonIdStudySorted = 300;
    private static final int ButtonIdStudyRandom = 301;

    /**
     * Member variables
     */
    private State mState;
    private boolean mFirstStudy;       // 単語帳を選択して最初の学習のみtrue。リトライ時はfalse

    private StudyCardsManager mCardsManager;
    private StudyCardStackInput mCardsStack;

    private UTextView mTextCardCount;
    private UButtonText mExitButton;
    private UButtonText mSkipButton;
    private UButtonImage mSettingButton;

    // 学習する単語帳 or カードリスト
    private TangoBook mBook;
    private List<TangoCard> mCards;

    // 終了確認ダイアログ
    private UDialogWindow mDialog;


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
    public PageViewStudyInputCorrect(Context context, View parentView, String title) {
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
        mCardsStack = new StudyCardStackInput(mCardsManager, this,
                MARGIN_H, TOP_AREA_H,
                screenW, mParentView.getWidth() - MARGIN_H * 2,
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
                screenW / 2 - BUTTON_W - MARGIN_H / 2, screenH - 150,
                BUTTON_W, BUTTON_H,
                TEXT_SIZE, Color.BLACK, Color.rgb(100,200,100));
        mExitButton.addToDrawManager();

        // 現在のカードをスキップボタン
        mSkipButton = new UButtonText(this, UButtonType.Press,
                ButtonIdSkip,
                DRAW_PRIORITY, mContext.getString(R.string.skip),
                screenW / 2 + MARGIN_H / 2, screenH - 150,
                BUTTON_W, BUTTON_H,
                TEXT_SIZE, Color.BLACK, UColor.LightPink);
        mSkipButton.addToDrawManager();

        // 設定ボタン
        Bitmap image = UResourceManager.getBitmapWithColor(R.drawable.settings_1, UColor.Green);
        mSettingButton = UButtonImage.createButton(this, ButtonIdSetting, DRAW_PRIORITY,
                screenW - SETTING_BUTTON_W - MARGIN_H, screenH - 150,
                SETTING_BUTTON_W, SETTING_BUTTON_W,
                image, null);
        mSettingButton.addToDrawManager();
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
            case ButtonIdSkip:
                // 次の問題へ
                mCardsStack.skipCard();
                break;
            case ButtonIdSetting:
                if (mDialog != null) {
                    mDialog.closeDialog();
                    mDialog = null;
                }
                showSettingDialog();
                break;
            case ButtonIdStudySorted:
            case ButtonIdStudyRandom: {
                boolean flag = (id == ButtonIdStudyRandom) ? true : false;
                MySharedPref.writeBoolean(MySharedPref.StudyMode4OptionKey, flag);
                if (mDialog != null) {
                    mDialog.closeDialog();
                    mDialog = null;
                }
            }
            break;
        }
        return false;
    }

    /**
     * 設定用のダイアログを開く
     */
    private void showSettingDialog() {
        mDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                this, this,
                UDialogWindow.ButtonDir.Vertical, UDialogWindow.DialogPosType.Center,
                true, mParentView.getWidth(), mParentView.getHeight(),
                Color.BLACK, Color.LTGRAY);
        mDialog.addToDrawManager();
        mDialog.setTitle(UResourceManager.getStringById(R.string.option_mode4_22));
        mDialog.addButton(ButtonIdStudySorted, UResourceManager.getStringById(R.string.option_mode4_3), UColor.BLACK, UColor.White);
        mDialog.addButton(ButtonIdStudyRandom, UResourceManager.getStringById(R.string.option_mode4_4), Color.BLACK, UColor.White);
        mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
    }

    /**
     * UDialogCallbacks
     */
    public void dialogClosed(UDialogWindow dialog) {
        super.dialogClosed(dialog);

        if (dialog == mDialog) {
            mDialog = null;
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
