package com.sunsunsoft.shutaro.tangobook.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;
import com.sunsunsoft.shutaro.tangobook.study_card.CardsStackCallbacks;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.study_card.StudyCardStackSelect;
import com.sunsunsoft.shutaro.tangobook.study_card.StudyCardsManager;
import com.sunsunsoft.shutaro.tangobook.study_card.StudyUtil;
import com.sunsunsoft.shutaro.tangobook.database.TangoBook;
import com.sunsunsoft.shutaro.tangobook.database.TangoCard;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButton;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonImage;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonText;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonType;
import com.sunsunsoft.shutaro.tangobook.uview.text.UTextView;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;
import com.sunsunsoft.shutaro.tangobook.uview.window.UDialogWindow;

import java.util.List;

/**
 * Created by shutaro on 2016/12/27.
 *
 * 学習ページ(４択)
 * 正解を１つだけふくむ４つの選択肢から正解を選ぶ学習モード
 */

public class PageViewStudySelect4 extends PageViewStudy
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

    // 座標系
    private static final int TOP_AREA_H = 50;
    private static final int BOTTOM_AREA_H = 50;
    private static final int TEXT_SIZE = 17;
    private static final int BUTTON_W = 100;
    private static final int BUTTON_H = 40;
    private static final int SETTING_BUTTON_W = 40;
    private static final int CARD_STACK_MARGIN_H = 35;

    private static final int DRAW_PRIORITY = 100;

    // button ids
    private static final int ButtonIdOk = 101;
    private static final int ButtonIdNg = 102;
    private static final int ButtonIdSetting = 103;
    private static final int ButtonIdSelectFromAll = 104;
    private static final int ButtonIdSelectFromOneBook = 105;

    /**
     * Member variables
     */
    private State mState;
    private boolean mFirstStudy;       // 単語帳を選択して最初の学習のみtrue。リトライ時はfalse

    private StudyCardsManager mCardsManager;
    private StudyCardStackSelect mCardsStack;

    private UTextView mTextCardCount;
    private UButtonText mExitButton;
    private UButtonImage mSettingButton;
    
    // 設定用のダイアログ
    private UDialogWindow mDialog;

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
        int width = mParentView.getWidth();
        int height = mParentView.getHeight();

        // カードスタック
        mCardsStack = new StudyCardStackSelect(mCardsManager, this,
                UDpi.toPixel(CARD_STACK_MARGIN_H), UDpi.toPixel(TOP_AREA_H),
                width, mParentView.getWidth() - UDpi.toPixel(CARD_STACK_MARGIN_H) * 2,
                mParentView.getHeight() - UDpi.toPixel(TOP_AREA_H + BOTTOM_AREA_H)
        );
        mCardsStack.addToDrawManager();


        // あと〜枚
        String title = getCardsRemainText(mCardsStack.getCardCount());
        mTextCardCount = UTextView.createInstance( title, UDpi.toPixel(TEXT_SIZE), DRAW_PRIORITY,
                UAlignment.CenterX, width, false, true,
                width / 2, UDpi.toPixel(17), UDpi.toPixel(100), UColor.ExitButton, 0);
        mTextCardCount.addToDrawManager();

        // 終了ボタン
        mExitButton = new UButtonText(this, UButtonType.Press,
                ButtonIdExit,
                DRAW_PRIORITY, mContext.getString(R.string.finish),
                (width - UDpi.toPixel(BUTTON_W)) / 2, height - UDpi.toPixel(50),
                UDpi.toPixel(BUTTON_W), UDpi.toPixel(BUTTON_H),
                UDpi.toPixel(TEXT_SIZE), Color.BLACK, UColor.ExitButton);
        mExitButton.addToDrawManager();
        
        // 設定ボタン
        Bitmap image = UResourceManager.getBitmapWithColor(R.drawable.settings_1, UColor.Green);
        mSettingButton = UButtonImage.createButton(this, ButtonIdSetting, DRAW_PRIORITY,
                width + UDpi.toPixel( -SETTING_BUTTON_W - MARGIN_H), height - UDpi.toPixel(50),
                UDpi.toPixel(SETTING_BUTTON_W), UDpi.toPixel(SETTING_BUTTON_W),
                image, null);
        mSettingButton.addToDrawManager();
    }

    private String getCardsRemainText(int count) {
        return String.format(mContext.getString(R.string.cards_remain), count);
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
        mDialog.setTitle(UResourceManager.getStringById(R.string.option_mode3_1));
        UButton button = mDialog.addButton(ButtonIdSelectFromAll, UResourceManager.getStringById(R
                .string
                .option_mode3_2),
                UColor.BLACK, UColor.White);

        UButton button2 = mDialog.addButton(ButtonIdSelectFromOneBook, UResourceManager
                .getStringById(R.string
                .option_mode3_3), Color.BLACK, UColor.White);

        if (MySharedPref.readBoolean(MySharedPref.StudyMode3OptionKey)) {
            button.setChecked(true);
        } else {
            button2.setChecked(true);
        }

        mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
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
            case ButtonIdSetting:
                if (mDialog != null) {
                    mDialog.closeDialog();
                    mDialog = null;
                }
                showSettingDialog();
                break;
            case ButtonIdSelectFromAll:
            case ButtonIdSelectFromOneBook: {
                boolean flag = (id == ButtonIdSelectFromAll) ? true : false;
                MySharedPref.writeBoolean(MySharedPref.StudyMode3OptionKey, flag);
                if (mDialog != null) {
                    mDialog.closeDialog();
                    mDialog = null;
                }
            }    break;
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
