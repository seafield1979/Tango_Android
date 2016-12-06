package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
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

public class PreStudyWindow extends UWindow {
    /**
     * Enum
     */
    enum ButtonId {
        Start,
        Cancel,
        Option1_1,
        Option1_2,
        Option2_1,
        Option2_2,
        Option3_1,
        Option3_2
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
    private static final int TEXT_SIZE_2 = 35;
    private static final int TEXT_SIZE_3 = 70;
    private static final int TITLE_WIDTH = 100;

    private static final int BUTTON_W = 400;
    private static final int BUTTON_H = 120;

    private static final int BG_COLOR = Color.WHITE;
    private static final int FRAME_COLOR = Color.rgb(120,120,120);
    private static final int TEXT_COLOR = Color.BLACK;

    // button Id
    private static final int ButtonIdOption1_1 = 101;
    private static final int ButtonIdOption1_2 = 102;
    private static final int ButtonIdOption2_1 = 103;
    private static final int ButtonIdOption2_2 = 104;
    private static final int ButtonIdOption3_1 = 105;
    private static final int ButtonIdOption3_2 = 106;

    /**
     * Member Variables
     */
    protected View mParentView;
    protected Context mContext;
    protected UButtonCallbacks mButtonCallbacks;
    protected boolean isUpdate = true;
    private UTextView textTitle, textCount, textLastStudied;
    private UTextView textOption1, textOption2, textOption3;

    // buttons
    private UButton[] buttons = new UButton[ButtonId.values().length];

    // ダイアログに情報を表示元のTangoBook
    protected TangoBook mBook;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public PreStudyWindow(View parentView,
                          UWindowCallbacks windowCallbacks, UButtonCallbacks buttonCallbacks )
    {
        // width, height はinit内で計算するのでここでは0を設定
        super(windowCallbacks, DrawPriority.Dialog.p(), 0, 0, 0, 0, BG_COLOR);

        mParentView = parentView;
        mContext = parentView.getContext();
        mButtonCallbacks = buttonCallbacks;
        isShow = false;     // 初期状態は非表示
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
    }

    public boolean touchEvent(ViewTouch vt) {
        if (super.touchEvent(vt)) {
            return true;
        }

        for (UButton button : buttons) {
            if (button == null) continue;
            if (button.touchEvent(vt, pos)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 毎フレーム行う処理
     *
     * @return true:描画を行う
     */
    public boolean doAction() {

        return false;
    }

    /**
     * Windowのコンテンツ部分を描画する
     * @param canvas
     * @param paint
     */
    public void drawContent(Canvas canvas, Paint paint) {
        if (isUpdate) {
            isUpdate = false;
            updateLayout(canvas);
        }

        // BG
        UDraw.drawRoundRectFill(canvas, paint, new RectF(getRect()), 20,
                bgColor, FRAME_WIDTH, FRAME_COLOR);

        // textViews
        textTitle.draw(canvas, paint, pos);
        textCount.draw(canvas, paint, pos);
        textLastStudied.draw(canvas, paint, pos);
        textOption1.draw(canvas, paint, pos);
        textOption2.draw(canvas, paint, pos);
        textOption3.draw(canvas, paint, pos);

        // buttons
        for (UButton button : buttons) {
            button.draw(canvas, paint, pos) ;
        }
    }

    /**
     * レイアウト更新
     * @param canvas
     */
    protected void updateLayout(Canvas canvas) {

        int y = TOP_ITEM_Y;
        int width = BUTTON_W * 2 + MARGIN_H * 3;

        // タイトル(単語帳の名前)
        String title = mContext.getString(R.string.book) + " : " + mBook.getName();
        textTitle = UTextView.createInstance( title, TEXT_SIZE_3, 0,
                UDraw.UAlignment.CenterX, canvas.getWidth(), false, true,
                width / 2, y, TITLE_WIDTH, TEXT_COLOR, 0);
        y += TEXT_SIZE_3 + MARGIN_V;

        // カード数
        long count = RealmManager.getItemPosDao().countInParentType(
                TangoParentType.Book, mBook.getId()
        );
        textCount = UTextView.createInstance( mContext.getString(R.string.card_count) + ":" + count,
                TEXT_SIZE, 0,
                UDraw.UAlignment.CenterX, canvas.getWidth(), false, true,
                width / 2, y, TITLE_WIDTH, TEXT_COLOR, 0);
        y += TEXT_SIZE + MARGIN_V;

        // 最終学習日時
        Date studiedTime = mBook.getStudyTime();
        textLastStudied = UTextView.createInstance( mContext.getString(R.string
                .last_studied_date) + ":" + studiedTime,
                TEXT_SIZE, 0,
                UDraw.UAlignment.CenterX, canvas.getWidth(), false, true,
                width / 2, y, TITLE_WIDTH, TEXT_COLOR, 0);
        y += TEXT_SIZE + MARGIN_V + 40;

        // 開始ボタン
        buttons[ButtonId.Start.ordinal()] = new UButtonText(mButtonCallbacks, UButtonType.Press,
                PageViewStudySelect.ButtonIdStartStudy,
                0, mContext.getString(R.string.start), MARGIN_H, y,
                width - MARGIN_H * 2, BUTTON_H,
                TEXT_COLOR, Color.rgb(100,200,100));
        y += BUTTON_H + MARGIN_V;

        // キャンセルボタン
        buttons[ButtonId.Cancel.ordinal()] = new UButtonText(mButtonCallbacks, UButtonType.Press,
                PageViewStudySelect.ButtonIdCancel,
                0, mContext.getString(R.string.cancel),
                MARGIN_H, y,
                width - MARGIN_H * 2, BUTTON_H,
                TEXT_COLOR, Color.rgb(200,100,100));
        y += BUTTON_H + MARGIN_V + 30;

        // Option1 出題方法
        // タイトル
        textOption1 = UTextView.createInstance( mContext.getString(R.string.study_type),
                TEXT_SIZE_2, 0,
                UDraw.UAlignment.None, canvas.getWidth(), false, false,
                MARGIN_H, y, TITLE_WIDTH, TEXT_COLOR, 0);
        y += TEXT_SIZE_2 + 20;

        // 英語->日本語
        buttons[ButtonId.Option1_1.ordinal()] = new UButtonText(this, UButtonType.Press,
                ButtonIdOption1_1,
                0, mContext.getString(R.string.e_to_j), MARGIN_H, y, BUTTON_W, BUTTON_H,
                TEXT_COLOR, Color.LTGRAY);

        // 日本語->英語
        buttons[ButtonId.Option1_2.ordinal()] = new UButtonText(this, UButtonType.Press,
                ButtonIdOption1_2,
                0, mContext.getString(R.string.j_to_e),
                MARGIN_H + BUTTON_W + MARGIN_H, y, BUTTON_W, BUTTON_H,
                TEXT_COLOR, Color.LTGRAY);

        y += BUTTON_H + MARGIN_V;

        // Option2 順番
        // タイトル
        textOption2 = UTextView.createInstance( mContext.getString(R.string.order_type),
                TEXT_SIZE_2, 0,
                UDraw.UAlignment.None, canvas.getWidth(), false, false,
                MARGIN_H, y, TITLE_WIDTH, TEXT_COLOR, Color.argb(1,0,0,0));
        y += TEXT_SIZE_2 + 20;

        // 順番通り
        buttons[ButtonId.Option2_1.ordinal()] = new UButtonText(this, UButtonType.Press,
                ButtonIdOption2_1,
                0, mContext.getString(R.string.order_normal), MARGIN_H, y, BUTTON_W, BUTTON_H,
                Color.WHITE, Color.BLUE);

        // ランダム
        buttons[ButtonId.Option2_2.ordinal()] = new UButtonText(this, UButtonType.Press,
                ButtonIdOption2_2,
                0, mContext.getString(R.string.order_random),
                MARGIN_H + BUTTON_W + MARGIN_H, y, BUTTON_W, BUTTON_H,
                Color.WHITE, Color.BLUE);

        y += BUTTON_H + MARGIN_V;


        // Option3 学習単語
        // タイトル
        textOption3 = UTextView.createInstance( mContext.getString(R.string.study_pattern),
                TEXT_SIZE_2, 0,
                UDraw.UAlignment.None, canvas.getWidth(), false, false,
                MARGIN_H, y, TITLE_WIDTH, TEXT_COLOR, 0);
        y += TEXT_SIZE_2 + 20;

        // すべて
        buttons[ButtonId.Option3_1.ordinal()] = new UButtonText(this, UButtonType.Press,
                ButtonIdOption3_1,
                0, mContext.getString(R.string.all), MARGIN_H, y, BUTTON_W, BUTTON_H,
                TEXT_COLOR, Color.YELLOW);

        // 未収得
        buttons[ButtonId.Option3_2.ordinal()] = new UButtonText(this, UButtonType.Press,
                ButtonIdOption3_2,
                0, mContext.getString(R.string.not_learned),
                MARGIN_H + BUTTON_W + MARGIN_H, y, BUTTON_W, BUTTON_H,
                TEXT_COLOR, Color.YELLOW);

        y += BUTTON_H + MARGIN_V;

        size.width = width;
        size.height = y;

        // センタリング
        pos.x = (mParentView.getWidth() - size.width) / 2;
        pos.y = (mParentView.getHeight() - size.height) / 2;

        setSize(width, y);

        updateRect();
    }

    /**
     * Callbacks
     */
    /**
     * UButtonCallbacks
     */
    public boolean UButtonClick(int id) {
        switch (id) {
            case ButtonIdOption1_1:

                break;
            case ButtonIdOption1_2:
                break;
            case ButtonIdOption2_1:
                break;
            case ButtonIdOption2_2:
                break;
            case ButtonIdOption3_1:
                break;
            case ButtonIdOption3_2:
                break;
        }
        return false;
    }

}
