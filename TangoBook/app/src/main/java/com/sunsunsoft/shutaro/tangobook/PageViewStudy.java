package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by shutaro on 2016/12/05.
 *
 * 単語学習ページ
 * カードが全てOK/NG処理されるまで上から単語カードが降ってくる
 */

public class PageViewStudy extends UPageView implements UButtonCallbacks, UDialogCallbacks{
    /**
     * Constants
     */
    public static final String TAG = "PageViewStudy";

    private static final int TOP_AREA_H = 200;
    private static final int BOTTOM_AREA_H = 200;
    private static final int TEXT_SIZE = 50;
    private static final int BUTTON_W = 300;
    private static final int BUTTON_H = 120;
    private static final int BUTTON2_W = 150;
    private static final int BUTTON2_H = 150;
    private static final int MARGIN_V = 50;
    private static final int MARGIN_H = 50;

    private static final int DRAW_PRIORITY = 100;

    // button ids
    private static final int ButtonIdExit = 100;
    private static final int ButtonIdOk = 101;
    private static final int ButtonIdNg = 102;

    private static final int ButtonIdExitOk = 200;

    /**
     * Member variables
     */
    private boolean option1, option2, option3;
    private StudyCardsManager mCardsManager;
    private StudyCardsStack mCardsStack;

    private UTextView mTextCardCount;
    private UButtonText mExitButton;
    private UButtonText mOkCardsButton, mNgCardsButton;

    // 学習する単語帳
    private TangoBook mBook;

    // 終了確認ダイアログ
    private UDialogWindow mConfirmDialog;

    private boolean isCloseOk;

    /**
     * Get/Set
     */
    public void setBook(TangoBook book) {
        mBook = book;
    }

    /**
     * Constructor
     */
    public PageViewStudy(Context context, View parentView) {
        super(context, parentView, PageView.TangoStudy.getDrawId());

    }

    /**
     * Methods
     */
    protected void onShow() {
        super.onShow();

        // get options
        option1 = MySharedPref.getInstance().readBoolean(MySharedPref.Option1Key);
        option2 = MySharedPref.getInstance().readBoolean(MySharedPref.Option2Key);
        option3 = MySharedPref.getInstance().readBoolean(MySharedPref.Option3Key);

        mCardsManager = new StudyCardsManager(mBook, option1, option2, option3);
        mCardsStack = new StudyCardsStack(mCardsManager,
                (mParentView.getWidth() - StudyCard.WIDTH) / 2, TOP_AREA_H,
                StudyCard.WIDTH,
                mParentView.getHeight() - (TOP_AREA_H + BOTTOM_AREA_H));
        UDrawManager.getInstance().addDrawable(mCardsStack);
    }

    protected void onHide() {
        mCardsManager = null;
        mCardsStack.cleanUp();
        mCardsStack = null;
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

        boolean done = false;
        if (doAction()) {
            done = true;
        }

        if (UDrawManager.getInstance().draw(canvas, paint)) {
            done = true;
        }

        return done;
    }

    /**
     * 毎フレームの処理
     * @return
     */
    protected boolean doAction() {
        if (mCardsStack.doAction()) {
            return true;
        }
        return false;
    }

    /**
     * タッチ処理
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt) {
        if (UDrawManager.getInstance().touchEvent(vt)) {
            return true;
        }
        return false;
    }

    /**
     * そのページで表示される描画オブジェクトを初期化する
     */
    public void initDrawables() {
        int screenW = mParentView.getWidth();
        int screenH = mParentView.getHeight();

        // あと〜枚
        String title = getCardsRemainText(mCardsStack.getCardCount());
        mTextCardCount = UTextView.createInstance( title, TEXT_SIZE, DRAW_PRIORITY,
                UAlignment.CenterX, screenW, false, true,
                screenW / 2, 50, 300, Color.rgb(100,50,50), 0);
        UDrawManager.getInstance().addDrawable(mTextCardCount);

        // 終了ボタン
        mExitButton = new UButtonText(this, UButtonType.Press,
                ButtonIdExit,
                DRAW_PRIORITY, mContext.getString(R.string.finish),
                (screenW - BUTTON_W) / 2, screenH - 150,
                BUTTON_W, BUTTON_H,
                Color.BLACK, Color.rgb(100,200,100));
        UDrawManager.getInstance().addDrawable(mExitButton);

        // OKボタン
        mOkCardsButton = new UButtonText(this, UButtonType.BGColor,
                ButtonIdOk,
                DRAW_PRIORITY, "OK",
                screenW - BUTTON2_W - MARGIN_H, screenH - BUTTON2_H - MARGIN_V,
                BUTTON2_W, BUTTON2_H,
                Color.BLACK, Color.rgb(100,200,100));
        UDrawManager.getInstance().addDrawable(mOkCardsButton);

        // NGボタン
        mNgCardsButton = new UButtonText(this, UButtonType.BGColor,
                ButtonIdNg,
                DRAW_PRIORITY, "NG",
                MARGIN_H, screenH - BUTTON2_H - MARGIN_V,
                BUTTON2_W, BUTTON2_H,
                Color.BLACK, Color.rgb(100,200,100));
        UDrawManager.getInstance().addDrawable(mNgCardsButton);
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
                    mConfirmDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                            this, this,
                            UDialogWindow.ButtonDir.Horizontal, UDialogWindow.DialogPosType.Center,
                            true, mParentView.getWidth(), mParentView.getHeight(),
                            Color.BLACK, Color.LTGRAY);
                    mConfirmDialog.setTitle(mContext.getString(R.string.confirm_exit));
                    mConfirmDialog.addButton(ButtonIdExitOk, "OK", Color.BLACK, Color.LTGRAY);
                    mConfirmDialog.addCloseButton("Cancel");
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
            UPageViewManager.getInstance().popPage();
        }
        if (dialog == mConfirmDialog) {
            mConfirmDialog = null;
        }
    }
}
