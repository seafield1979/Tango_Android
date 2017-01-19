package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.View;

import java.util.Date;

/**
 * Created by shutaro on 2016/12/06.
 *
 * Bookを学習する前に表示されるダイアログ
 */

public class PreStudyWindow extends UWindow implements UDialogCallbacks{
    /**
     * Enum
     */
    enum ButtonId {
        Start,
        Cancel,
        Option1,
        Option2,
        Option3,
        Option4
    }

    /**
     * Consts
     */
    public static final String TAG = "PreStudyWindow";
    private static final int FRAME_WIDTH = 4;
    private static final int TOP_ITEM_Y = 30;
    private static final int TEXT_VIEW_H = 100;
    private static final int MARGIN_V = 40;
    private static final int MARGIN_H = 40;
    private static final int TEXT_SIZE = 50;
    private static final int TEXT_SIZE_2 = 40;
    private static final int TEXT_SIZE_3 = 70;
    private static final int BUTTON_TEXT_SIZE = 50;
    private static final int TITLE_WIDTH = 0;

    private static final int BUTTON_W = 600;
    private static final int BUTTON_H = 120;
    private static final int BUTTON2_W = 400;
    private static final int BUTTON2_H = 200;

    private static final int BUTTON_ICON_W = 200;

    private static final int BG_COLOR = Color.WHITE;
    private static final int FRAME_COLOR = Color.rgb(120,120,120);
    private static final int TEXT_COLOR = Color.BLACK;
    private static final int TEXT_DATE_COLOR = Color.rgb(80,80,80);

    // button Id
    private static final int ButtonIdOption1 = 100;
    private static final int ButtonIdOption2 = 200;
    private static final int ButtonIdOption3 = 300;
    private static final int ButtonIdOption4 = 400;

    // 出題モード
    private static final int ButtonIdOption1_1 = 101;
    private static final int ButtonIdOption1_2 = 102;
    private static final int ButtonIdOption1_3 = 103;
    private static final int ButtonIdOption1_4 = 104;

    // 出題モード2
    private static final int ButtonIdOption2_1 = 110;
    private static final int ButtonIdOption2_2 = 111;

    // 並び順
    private static final int ButtonIdOption3_1 = 201;
    private static final int ButtonIdOption3_2 = 202;

    // 絞り込み
    private static final int ButtonIdOption4_1 = 301;
    private static final int ButtonIdOption4_2 = 302;

    /**
     * Member Variables
     */
    protected UButtonCallbacks mButtonCallbacks;
    protected View mParentView;
    private UTextView textTitle, textCount, textLastStudied;
    private UTextView textStudyMode, textStudyType, textStudyOrder, textStudyFilter;

    private int mCardCount, mNgCount;

    // options
    protected StudyMode mStudyMode;
    protected StudyType mStudyType;
    protected StudyOrder mStudyOrder;
    protected StudyFilter mStudyFilter;

    // buttons
    private UButtonText[] buttons = new UButtonText[ButtonId.values().length];

    // ダイアログに情報を表示元のTangoBook
    protected TangoBook mBook;

    // オプション選択ダイアログ
    protected UDialogWindow mDialog;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public PreStudyWindow(UWindowCallbacks windowCallbacks, UButtonCallbacks buttonCallbacks,
                          View parentView)
    {
        // width, height はinit内で計算するのでここでは0を設定
        super(windowCallbacks, DrawPriority.Dialog.p() + 1, 0, 0,
                parentView.getWidth(), parentView.getHeight(), BG_COLOR);

        mButtonCallbacks = buttonCallbacks;
        mParentView = parentView;
        isShow = false;     // 初期状態は非表示

        // get options
        mStudyMode = MySharedPref.getStudyMode();
        mStudyType = MySharedPref.getStudyType();
        mStudyOrder = MySharedPref.getStudyOrder();
        mStudyFilter = MySharedPref.getStudyFilter();
    }

    /**
     * Methods
     */
    /**
     * 指定のBook情報でWindowを表示する
     * @param book
     */
    public void showWithBook(TangoBook book) {
        isShow = true;
        mBook = book;
        updateLayout();
    }

    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (!isShow) return false;

        if (offset == null) {
            offset = new PointF(pos.x, pos.y);
        }
        if (super.touchEvent(vt, offset)) {
            return true;
        }

        boolean isRedraw = false;

        // touch up
        for (UButton button : buttons) {
            if (button == null) continue;
            if (button.touchUpEvent(vt)) {
                isRedraw = true;
            }
        }
        // touch
        for (UButton button : buttons) {
            if (button == null) continue;
            if (button.touchEvent(vt, offset)) {
                return true;
            }
        }

        if (super.touchEvent2(vt, null)) {
            return true;
        }

        return isRedraw;
    }


    /**
     * 毎フレーム行う処理
     *
     * @return true:描画を行う
     */
    public boolean doAction() {
        for (UButton button : buttons) {
            if (button == null) continue;

            if (button.doAction()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Windowのコンテンツ部分を描画する
     * @param canvas
     * @param paint
     */
    public void drawContent(Canvas canvas, Paint paint, PointF offset) {
        // BG
        UDraw.drawRoundRectFill(canvas, paint, new RectF(getRect()), 20,
                bgColor, FRAME_WIDTH, FRAME_COLOR);

        // textViews
        textTitle.draw(canvas, paint, pos);
        textCount.draw(canvas, paint, pos);
        textLastStudied.draw(canvas, paint, pos);
        textStudyMode.draw(canvas, paint, pos);
        textStudyType.draw(canvas, paint, pos);
        textStudyOrder.draw(canvas, paint, pos);
        textStudyFilter.draw(canvas, paint, pos);

        // buttons
        for (UButton button : buttons) {
            button.draw(canvas, paint, pos) ;
        }
    }

    /**
     * レイアウト更新
     */
    protected void updateLayout() {

        int y = TOP_ITEM_Y;
        int screenW = mParentView.getWidth();
        int screenH = mParentView.getHeight();
        int width = screenW;

        // カード数
        mCardCount = RealmManager.getItemPosDao().countInParentType(
                TangoParentType.Book, mBook.getId()
        );
        mNgCount = RealmManager.getItemPosDao().countCardInBook(mBook.getId(),
                TangoItemPosDao.BookCountType.NG);

        // タイトル(単語帳の名前)
        String title = UResourceManager.getInstance().getStringById(R.string.book) + " : " + mBook
                .getName();
        textTitle = UTextView.createInstance( title, TEXT_SIZE_3, 0,
                UAlignment.CenterX, screenW, false, false,
                width / 2, y, TITLE_WIDTH, TEXT_COLOR, 0);
        y += textTitle.getHeight() + MARGIN_V;

        // カード数
        String cardCount = UResourceManager.getStringById(R.string.card_count) + ": " + mCardCount +
                "  " + UResourceManager.getStringById(R.string.count_not_learned) + ": " + mNgCount;

        textCount = UTextView.createInstance(
                cardCount,
                TEXT_SIZE, 0,
                UAlignment.CenterX, screenW, false, false,
                width / 2, y, TITLE_WIDTH, TEXT_COLOR, 0);
        y += textCount.getHeight() + MARGIN_V;

        // 最終学習日時
        Date date = RealmManager.getBookHistoryDao().selectMaxDateByBook(mBook.getId());
        textLastStudied = UTextView.createInstance(
                UResourceManager.getStringById(R.string
                .last_studied_date) + ": " + UUtil.convDateFormat(date, ConvDateMode.DateTime),
                TEXT_SIZE, 0,
                UAlignment.CenterX, screenW, false, false,
                width / 2, y, TITLE_WIDTH, TEXT_DATE_COLOR, 0);
        y += textLastStudied.getHeight() + MARGIN_V * 2;

        /**
         * Buttons
         */
        float titleX = (width - (TITLE_WIDTH + BUTTON_W)) / 2 + TITLE_WIDTH;
        float buttonX = titleX + MARGIN_H;

        // 出題方法（出題モード)
        // タイトル
        textStudyMode = UTextView.createInstance(
                UResourceManager.getStringById(R.string.study_mode),
                TEXT_SIZE_2, 0,
                UAlignment.Right_CenterY, screenW, false, false,
                titleX, y + BUTTON_H / 2, TITLE_WIDTH, TEXT_COLOR, 0);

        // Button
        buttons[ButtonId.Option1.ordinal()] = new UButtonText(this, UButtonType.BGColor,
                ButtonIdOption1,
                0, mStudyMode.getString(),
                buttonX, y, BUTTON_W, BUTTON_H,
                BUTTON_TEXT_SIZE, TEXT_COLOR, UColor.LightBlue);
        buttons[ButtonId.Option1.ordinal()].setPullDownIcon(true);

        y += BUTTON_H + MARGIN_V;

        // 出題タイプ(英日)
        textStudyType = UTextView.createInstance(
                UResourceManager.getStringById(R.string.study_type),
                TEXT_SIZE_2, 0,
                UAlignment.Right_CenterY, screenW, false, false,
                titleX, y + BUTTON_H / 2, TITLE_WIDTH, TEXT_COLOR, 0);

        // Button
        buttons[ButtonId.Option2.ordinal()] = new UButtonText(this, UButtonType.BGColor,
                ButtonIdOption2,
                0, mStudyType.getString(),
                buttonX, y, BUTTON_W, BUTTON_H,
                BUTTON_TEXT_SIZE, TEXT_COLOR, UColor.LightGreen);
        buttons[ButtonId.Option2.ordinal()].setPullDownIcon(true);

        y += BUTTON_H + MARGIN_V;

        // 順番
        // タイトル
        textStudyOrder = UTextView.createInstance(
                UResourceManager.getStringById(R.string.study_order),
                TEXT_SIZE_2, 0,
                UAlignment.Right_CenterY, screenW, false, false,
                titleX, y + BUTTON_H / 2, TITLE_WIDTH, TEXT_COLOR, Color.argb(1,0,0,0));

        // Button
        StudyOrder studyOrder = StudyOrder.toEnum(MySharedPref.readInt(MySharedPref.StudyOrderKey));
        buttons[ButtonId.Option3.ordinal()] = new UButtonText(this, UButtonType.BGColor,
                ButtonIdOption3,
                0, studyOrder.getString(),
                buttonX, y, BUTTON_W, BUTTON_H,
                BUTTON_TEXT_SIZE, TEXT_COLOR, UColor.Gold);
        buttons[ButtonId.Option3.ordinal()].setPullDownIcon(true);

        y += BUTTON_H + MARGIN_V;


        // 学習単語
        // タイトル
        textStudyFilter = UTextView.createInstance(
                UResourceManager.getStringById(R.string.study_filter),
                TEXT_SIZE_2, 0,
                UAlignment.Right_CenterY, screenW, false, false,
                titleX, y + BUTTON_H / 2, TITLE_WIDTH, TEXT_COLOR, 0);

        // Button
        StudyFilter studyFilter = StudyFilter.toEnum(MySharedPref.readInt(MySharedPref
                .StudyFilterKey));
        buttons[ButtonId.Option4.ordinal()] = new UButtonText(this, UButtonType.BGColor,
                ButtonIdOption4,
                0, studyFilter.getString(),
                buttonX, y, BUTTON_W, BUTTON_H,
                BUTTON_TEXT_SIZE, TEXT_COLOR, UColor.LightPink);
        buttons[ButtonId.Option4.ordinal()].setPullDownIcon(true);

        y += BUTTON_H + MARGIN_V;

        // センタリング
        pos.x = (screenW - size.width) / 2;
        pos.y = (screenH - size.height) / 2;

        // 開始ボタン
        buttons[ButtonId.Start.ordinal()] = new UButtonText(this, UButtonType.Press,
                PageViewStudyBookSelect.ButtonIdStartStudy,
                0, UResourceManager.getStringById(R.string.start),
                width / 2 - BUTTON2_W - MARGIN_H / 2, size.height - BUTTON2_H - MARGIN_V,
                BUTTON2_W, BUTTON2_H,
                TEXT_SIZE, TEXT_COLOR, Color.rgb(100,200,100));
        if (mCardCount == 0) {
            buttons[ButtonId.Start.ordinal()].setEnabled(false);
        }

        // キャンセルボタン
        buttons[ButtonId.Cancel.ordinal()] = new UButtonText(this, UButtonType.Press,
                PageViewStudyBookSelect.ButtonIdCancel,
                0, UResourceManager.getStringById(R.string.cancel),
                width / 2 + MARGIN_H / 2, size.height - BUTTON2_H - MARGIN_V,
                BUTTON2_W, BUTTON2_H,
                TEXT_SIZE, Color.WHITE, Color.rgb(200,100,100));

        updateRect();
    }

    /**
     * 出題モード選択ダイアログを表示する
     */
    private void showOption1Dialog() {
        if (mDialog == null) {
            mDialog = UDialogWindow.createInstance(this, this, UDialogWindow.ButtonDir.Vertical,
                    mParentView.getWidth(), mParentView.getHeight());
            mDialog.setTitle(UResourceManager.getStringById(R.string.study_mode));

            // Slide one
            UButtonText button = new UButtonText(
            this, UButtonType.Press, ButtonIdOption1_1,
            0, UResourceManager.getStringById(R.string.study_mode_1),
                MARGIN_H, 0, mDialog.getWidth() - MARGIN_H * 2, BUTTON_ICON_W + 50,
                TEXT_SIZE, TEXT_COLOR, UColor.LightBlue);
            button.setImage(UResourceManager.getBitmapById(R.drawable.study_mode1), new Size
                    (BUTTON_ICON_W,BUTTON_ICON_W));
            button.setImageOffset(-BUTTON_ICON_W - 50, 0);
            mDialog.addDrawable(button);

            // Slide multi
            button = new UButtonText(
                    this, UButtonType.Press, ButtonIdOption1_2,
                    0, UResourceManager.getStringById(R.string.study_mode_2),
                    MARGIN_H, 0, mDialog.getWidth() - MARGIN_H * 2, BUTTON_ICON_W + 50,
                    TEXT_SIZE, TEXT_COLOR, UColor.LightBlue);
            button.setImage(UResourceManager.getBitmapById(R.drawable.study_mode2), new Size
                    (BUTTON_ICON_W,BUTTON_ICON_W));
            button.setImageOffset(-BUTTON_ICON_W - 50, 0);
            mDialog.addDrawable(button);

            // 4 choice
            button = new UButtonText(
                    this, UButtonType.Press, ButtonIdOption1_3,
                    0, UResourceManager.getStringById(R.string.study_mode_3),
                    MARGIN_H, 0, mDialog.getWidth() - MARGIN_H * 2, BUTTON_ICON_W + 50,
                    TEXT_SIZE, TEXT_COLOR, UColor.LightBlue);
            button.setImage(UResourceManager.getBitmapById(R.drawable.study_mode3), new Size
                    (BUTTON_ICON_W,BUTTON_ICON_W));
            button.setImageOffset(-BUTTON_ICON_W - 50, 0);
            mDialog.addDrawable(button);

            // input correct
            button = new UButtonText(
                    this, UButtonType.Press, ButtonIdOption1_4,
                    0, UResourceManager.getStringById(R.string.study_mode_4),
                    MARGIN_H, 0, mDialog.getWidth() - MARGIN_H * 2, BUTTON_ICON_W + 50,
                    TEXT_SIZE, TEXT_COLOR, UColor.LightBlue);
            button.setImage(UResourceManager.getBitmapById(R.drawable.study_mode4), new Size
                    (BUTTON_ICON_W,BUTTON_ICON_W));
            button.setImageOffset(-BUTTON_ICON_W - 50, 0);
            mDialog.addDrawable(button);

            mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));

            mDialog.addToDrawManager();
        }
    }

    /**
     * 並び順を選択するダイアログを表示
     */
    private void showOption2Dialog() {
        if (mDialog == null) {
            mDialog = UDialogWindow.createInstance(this, this, UDialogWindow.ButtonDir.Vertical,
                    mParentView.getWidth(), mParentView.getHeight());
            mDialog.setTitle(UResourceManager.getStringById(R.string.study_type));
            mDialog.addTextView(UResourceManager.getStringById(R.string.study_order_exp),
                    UAlignment.Center, false, false, TEXT_SIZE_2, TEXT_COLOR, 0);
            mDialog.addButton(ButtonIdOption2_1, UResourceManager.getStringById(R.string
                    .study_type_1), TEXT_COLOR, UColor.LightGreen);
            mDialog.addButton(ButtonIdOption2_2, UResourceManager.getStringById(R.string
                    .study_type_2), TEXT_COLOR, UColor.LightGreen);

            mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));

            mDialog.addToDrawManager();
        }
    }

    /**
     * 並び順を選択するダイアログを表示
     */
    private void showOption3Dialog() {
        if (mDialog == null) {
            mDialog = UDialogWindow.createInstance(this, this, UDialogWindow.ButtonDir.Vertical,
                    mParentView.getWidth(), mParentView.getHeight());
            mDialog.setTitle(UResourceManager.getStringById(R.string.study_order));
            mDialog.addTextView(UResourceManager.getStringById(R.string.study_order_exp),
                    UAlignment.Center, false, false, TEXT_SIZE_2, TEXT_COLOR, 0);
            mDialog.addButton(ButtonIdOption3_1, UResourceManager.getStringById(R.string
                    .study_order_1), TEXT_COLOR, UColor.Gold);
            mDialog.addButton(ButtonIdOption3_2, UResourceManager.getStringById(R.string
                    .study_order_2), TEXT_COLOR, UColor.Gold);

            mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));

            mDialog.addToDrawManager();
        }
    }

    /**
     * 出題単語の絞り込み
     */
    private void showOption4Dialog() {
        if (mDialog == null) {
            mDialog = UDialogWindow.createInstance(this, this, UDialogWindow.ButtonDir.Vertical,
                    mParentView.getWidth(), mParentView.getHeight());
            mDialog.setTitle(UResourceManager.getStringById(R.string.study_filter));
            mDialog.addTextView(UResourceManager.getStringById(R.string.study_filter_exp),
                    UAlignment.Center, false, false, TEXT_SIZE_2, TEXT_COLOR, 0);
            mDialog.addButton(ButtonIdOption4_1, UResourceManager.getStringById(R.string
                    .study_filter_1), TEXT_COLOR, UColor.LightPink);
            mDialog.addButton(ButtonIdOption4_2, UResourceManager.getStringById(R.string
                    .study_filter_2), TEXT_COLOR, UColor.LightPink);

            mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));

            mDialog.addToDrawManager();
        }
    }

    /**
     * Callbacks
     */
    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        switch (id) {
            case PageViewStudyBookSelect.ButtonIdStartStudy:
                if (mCardCount == 0 || (mStudyFilter == StudyFilter.NotLearned && mNgCount == 0))
                {
                    // 未収得カード数が0なら終了
                    mDialog = UDialogWindow.createInstance(
                            null, this,
                            UDialogWindow.ButtonDir.Vertical, mParentView.getWidth(), mParentView
                                    .getHeight());
                    mDialog.setTitle(UResourceManager.getStringById(R.string.not_exit_study_card));
                    mDialog.addCloseButton(UResourceManager.getStringById(R.string.ok));
                    mDialog.addToDrawManager();
                    break;
                }
                // オプションを保存
                MySharedPref.writeInt(MySharedPref.StudyModeKey, mStudyMode.ordinal());
                MySharedPref.writeInt(MySharedPref.StudyTypeKey, mStudyType.ordinal());
                MySharedPref.writeInt(MySharedPref.StudyOrderKey, mStudyOrder.ordinal());
                MySharedPref.writeInt(MySharedPref.StudyFilterKey, mStudyFilter.ordinal());

                if (mButtonCallbacks != null) {
                    mButtonCallbacks.UButtonClicked(id, pressedOn);
                }
                break;
            case PageViewStudyBookSelect.ButtonIdCancel:
                if (mButtonCallbacks != null) {
                    mButtonCallbacks.UButtonClicked(id, pressedOn);
                }
                break;
            case ButtonIdOption1:
                showOption1Dialog();
                break;
            case ButtonIdOption2:
                showOption2Dialog();
                break;
            case ButtonIdOption3:
                showOption3Dialog();
                break;
            case ButtonIdOption4:
                showOption4Dialog();
                break;
            case ButtonIdOption1_1:
                mDialog.startClosing();
                setStudyMode(StudyMode.SlideOne);
                break;
            case ButtonIdOption1_2:
                mDialog.startClosing();
                setStudyMode(StudyMode.SlideMulti);
                break;
            case ButtonIdOption1_3:
                mDialog.startClosing();
                setStudyMode(StudyMode.Choice4);
                break;
            case ButtonIdOption1_4:
                mDialog.startClosing();
                setStudyMode(StudyMode.Input);
                break;
            case ButtonIdOption2_1:
                mDialog.startClosing();
                setStudyType(StudyType.EtoJ);
                break;
            case ButtonIdOption2_2:
                mDialog.startClosing();
                setStudyType(StudyType.JtoE);
                break;
            case ButtonIdOption3_1:
                mDialog.startClosing();
                setStudyOrder(StudyOrder.Normal);
                break;
            case ButtonIdOption3_2:
                mDialog.startClosing();
                setStudyOrder(StudyOrder.Random);
                break;
            case ButtonIdOption4_1:
                mDialog.startClosing();
                setStudyFilter(StudyFilter.All);
                break;
            case ButtonIdOption4_2:
                mDialog.startClosing();
                setStudyFilter(StudyFilter.NotLearned);
                break;

        }
        if (super.UButtonClicked(id, pressedOn)) {
            return true;
        }
        return false;
    }

    /**
     * 学習モードを設定
     */
    private void setStudyMode(StudyMode mode) {
        if (mStudyMode != mode) {
            mStudyMode = mode;
            MySharedPref.writeInt(MySharedPref.StudyModeKey, mode.ordinal());
            buttons[ButtonId.Option1.ordinal()].setText(mode.getString());
        }
    }

    /**
     * 学習タイプを設定
     */
    private void setStudyType(StudyType type) {
        if (mStudyType != type) {
            mStudyType = type;
            MySharedPref.writeInt(MySharedPref.StudyTypeKey, type.ordinal());
            buttons[ButtonId.Option2.ordinal()].setText(type.getString());
        }
    }

    /**
     * 並び順を設定
     * @param order
     */
    private void setStudyOrder(StudyOrder order) {
        if (mStudyOrder != order) {
            mStudyOrder = order;
            MySharedPref.writeInt(MySharedPref.StudyOrderKey, order.ordinal());
            buttons[ButtonId.Option3.ordinal()].setText(order.getString());
        }
    }

    /**
     * 絞り込みを設定
     */
    private void setStudyFilter(StudyFilter filter) {
        if (mStudyFilter != filter) {
            mStudyFilter = filter;
            MySharedPref.writeInt(MySharedPref.StudyFilterKey, filter.ordinal());
            buttons[ButtonId.Option4.ordinal()].setText(filter.getString());
        }
    }

    /**
     * ソフトウェアキーの戻るボタンを押したときの処理
     * @return
     */
    public boolean onBackKeyDown() {
        if (mDialog != null) {
            mDialog.closeDialog();
            mDialog = null;
            return true;
        }
        return false;
    }

    /**
     * UDialogCallbacks
     */
    public void dialogClosed(UDialogWindow dialog) {
        if (dialog == mDialog) {
            mDialog = null;
        }
    }
}
