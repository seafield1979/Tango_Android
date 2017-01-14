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
        Help(R.string.title_help, UColor.White, UColor.DarkOrange, Color.rgb(255,178,102), R.drawable.question),
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
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 50;

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
        int rowNum;
        if( width > mParentView.getHeight()) {
            columnNum = 3;
            rowNum =2;
        } else {
            columnNum = 2;
            rowNum = 3;
        }

        int buttonW = (width - (columnNum + 1) * MARGIN_H) / columnNum;
        int buttonH = (height - (rowNum + 1) * MARGIN_H) / rowNum;
        float x = MARGIN_H;
        float y = MARGIN_V;

        for (int i = 0; i< mButtons.length; i++) {
            if (i != 0 && (i % columnNum) == 0) {
                x = MARGIN_H;
                y += buttonH + MARGIN_H;
            }
            ButtonId id = ButtonId.values()[i];

            // デバッグモードがONの場合のみDebugを表示
            if (id == ButtonId.Debug) {
                if (!UDebug.isDebug) continue;
            }

            buttonType = UButtonType.Press;

            mButtons[i] = new UButtonText(this, buttonType, id.ordinal(), DRAW_PRIORITY,
                    id.getTitle(mContext), x, y,
                    buttonW, buttonH,
                    TEXT_SIZE, id.textColor, id.bgColor);
            Bitmap image = UResourceManager.getBitmapWithColor(id.imageId, id.lineColor);
            mButtons[i].setImage(image, new Size(IMAGE_W, IMAGE_W));


            UDrawManager.getInstance().addDrawable(mButtons[i]);
            // 表示座標を少し調整
            mButtons[i].setImageOffset(0, -50f);
            mButtons[i].setTextOffset(0, 40f);

            x += buttonW + MARGIN_H;
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
                PageViewManager.getInstance().stackPage(PageView.Help);
                break;
            case Debug:
                PageViewManager.getInstance().stackPage(PageView.Debug);
                break;
        }
        return false;
    }
}
