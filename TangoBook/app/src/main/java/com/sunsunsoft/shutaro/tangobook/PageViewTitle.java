package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

/**
 * Created by shutaro on 2016/12/05.
 *
 * タイトル画面のページ
 */

public class PageViewTitle extends UPageView implements UButtonCallbacks{

    /**
     * Enums
     */
    enum ButtonId {
        Edit(R.string.title_edit, UColor.DarkGreen, UColor.DarkGreen, Color.rgb(100, 200, 100), R
                .drawable.edit),
        Study(R.string.title_study, Color.WHITE, Color.WHITE, Color.rgb(200,100,100), R.drawable
                .study),
        History(R.string.title_history, UColor.DarkYellow, UColor.DarkYellow, Color.rgb(200,200,0), R.drawable
                .history),
        Settings(R.string.title_settings, UColor.DarkBlue, UColor.DarkBlue, Color.rgb(153,204,255), R.drawable
                .settings_1),
        Help(R.string.title_help, UColor.White, UColor.DarkOrange, Color.rgb(255,178,102), R.drawable.question2),
        Debug(R.string.title_debug, UColor.WHITE, UColor.DarkGray, Color.rgb(150,150,150), R.drawable.debug),
        ;

        private int textId;
        private int textColor;
        private int lineColor;
        private int bgColor;
        private int imageId;

        ButtonId(int textId, int textColor, int lineColor, int bgColor, int imageId) {
            this.textId = textId;
            this.textColor = textColor;
            this.lineColor = lineColor;
            this.bgColor = bgColor;
            this.imageId = imageId;
        }
        String getTitle(Context context) {
            return context.getString(textId);
        }

        static ButtonId toEnum(int value) {
            if (value >= values().length) return Edit;
            return values()[value];
        }
    }

    /**
     * Constants
     */
    public static final String TAG = "PageViewTitle";
    private static final int DRAW_PRIORITY = 100;

    private static final int TOP_Y = 50;
    private static final int BUTTON_W = 400;
    private static final int BUTTON_H = 200;
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 30;

    private static final int TITLE_TEXT_SIZE = 80;
    private static final int TEXT_SIZE = 50;
    private static final int IMAGE_W = 100;



    /**
     * Member variables
     */
    // Title
    private UTextView mTitleText;

    // Buttons
    private UButtonText mButtons[] = new UButtonText[ButtonId.values().length];


    /**
     * Constructor
     */
    public PageViewTitle(Context context, View parentView, String title) {
        super(context, parentView, title);
    }

    /**
     * Methods
     */

    public void onShow() {

    }

    public void onHide() {
        super.onHide();
    }

    /**
     * そのページで表示される描画オブジェクトを初期化する
     */
    public void initDrawables() {
        int width = mParentView.getWidth();
        int height = mParentView.getHeight();

        UButtonType buttonType;

        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();

        // ボタンの配置
        // 横向きなら３列、縦向きなら３列
        int columnNum;
        columnNum = 2;

        // 単語帳作成＆学習ボタンは正方形
        int buttonW = (width - (columnNum + 1) * MARGIN_H) / columnNum;

        float x = MARGIN_H;
        float y = MARGIN_V + 100.f;
        buttonType = UButtonType.Press;

        for (int i = 0; i < 2; i++) {
            ButtonId id = ButtonId.values()[i];

            mButtons[i] = new UButtonText(this, buttonType, id.ordinal(), DRAW_PRIORITY,
                    id.getTitle(mContext), x, y,
                    buttonW, buttonW,
                    TEXT_SIZE, id.textColor, id.bgColor);
            Bitmap image = UResourceManager.getBitmapWithColor(id.imageId, id.lineColor);
            mButtons[i].setImage(image, new Size(IMAGE_W, IMAGE_W));
            UDrawManager.getInstance().addDrawable(mButtons[i]);

            // 表示座標を少し調整
            mButtons[i].setImageOffset(0, -50f);
            mButtons[i].setTextOffset(0, 40f);

            x += buttonW + MARGIN_H;
        }
        y += buttonW + MARGIN_V;

        // 下の段は横長ボタン
        buttonW = width - MARGIN_H * 2;
        int buttonH = BUTTON_H;
        x = MARGIN_H;
        for (int i = 2; i < ButtonId.values().length; i++) {
            // デバッグモードがONの場合のみDebugを表示
            ButtonId id = ButtonId.values()[i];

            if (id == ButtonId.Debug) {
                if (!UDebug.isDebug) continue;
            }

            mButtons[i] = new UButtonText(this, buttonType, id.ordinal(), DRAW_PRIORITY,
                    id.getTitle(mContext), x, y,
                    buttonW, buttonH,
                    TEXT_SIZE, id.textColor, id.bgColor);
            Bitmap image = UResourceManager.getBitmapWithColor(id.imageId, id.lineColor);
            mButtons[i].setImage(image, new Size(IMAGE_W, IMAGE_W));
            UDrawManager.getInstance().addDrawable(mButtons[i]);

            // 表示座標を少し調整
            mButtons[i].setImageOffset(-IMAGE_W - 60 - MARGIN_H / 2, 0);
            mButtons[i].setTextOffset(MARGIN_H / 2, 0);

            y += buttonH + MARGIN_V;
        }
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
        ButtonId buttonId = ButtonId.toEnum(id);
        switch(buttonId) {
            case Edit:
                PageViewManager.getInstance().stackPage(PageView.Edit);
                break;
            case Study:
                PageViewManager.getInstance().stackPage(PageView.StudyBookSelect);
                break;
            case History:
                PageViewManager.getInstance().stackPage(PageView.History);
                break;
            case Settings:
                PageViewManager.getInstance().stackPage(PageView.Settings);
                break;
            case Help:
                MainActivity.getInstance().showHelpTopPage();
                break;
            case Debug:
                PageViewManager.getInstance().stackPage(PageView.Debug);
                break;
        }
        return false;
    }
}
