package com.sunsunsoft.shutaro.tangobook.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.util.Size;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.UDebug;
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.activity.MainActivity;
import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonImage;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonText;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonType;
import com.sunsunsoft.shutaro.tangobook.uview.text.UTextView;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;

/**
 * Created by shutaro on 2016/12/05.
 *
 * タイトル画面のページ
 */

public class PageViewTitle extends UPageView implements UButtonCallbacks {

    /**
     * Enums
     */
    enum ButtonId {
        Edit(R.string.title_edit, UColor.DarkGreen, UColor.DarkGreen, Color.rgb(100, 200, 100), R
                .drawable.edit),
        Study(R.string.title_study, Color.WHITE, Color.WHITE, Color.rgb(200,100,100), R.drawable
                .study),
        History(R.string.title_history, UColor.DarkYellow, UColor.DarkYellow, UColor.Yellow, R.drawable
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

    private static final int BUTTON_H = 65;
    private static final int ZOOM_BUTTON_W = 40;
    private static final int MARGIN_H = 18;
    private static final int MARGIN_V = 10;

    private static final int TEXT_SIZE = 17;
    private static final int IMAGE_W = 35;

    // button Ids
    private static final int ButtonIdZoomIn = 100;
    private static final int ButtonIdZoomOut = 101;


    /**
     * Member variables
     */
    private Toast mToast;
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

        UButtonType buttonType;

        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();

        // ボタンの配置
        // 横向きなら３列、縦向きなら３列
        int columnNum;
        columnNum = 2;

        // 単語帳作成＆学習ボタンは正方形
        int buttonW = (width - (columnNum + 1) * UDpi.toPixel(MARGIN_H)) / columnNum;

        buttonType = UButtonType.Press;

        // ズームボタン
        int zoomButtonW = UDpi.toPixel(ZOOM_BUTTON_W);
        if (zoomButtonW < ZOOM_BUTTON_W) {
            zoomButtonW = ZOOM_BUTTON_W;
        }

        UButtonImage button = new UButtonImage(this, ButtonIdZoomIn, DRAW_PRIORITY,
                width - zoomButtonW * 2 - UDpi.toPixel(20), UDpi.toPixel(10),
                zoomButtonW, zoomButtonW,
                R.drawable.zoom_in, 0);
        button.addToDrawManager();

        button = new UButtonImage(this, ButtonIdZoomOut, DRAW_PRIORITY,
                width - zoomButtonW - UDpi.toPixel(10), UDpi.toPixel(10),
                zoomButtonW, zoomButtonW,
                R.drawable.zoom_out, 0);
        button.addToDrawManager();

        float x = UDpi.toPixel(MARGIN_H);
        float y = UDpi.toPixel(MARGIN_V + 10) + zoomButtonW;

        for (int i = 0; i < 2; i++) {
            ButtonId id = ButtonId.values()[i];

            mButtons[i] = new UButtonText(this, buttonType, id.ordinal(), DRAW_PRIORITY,
                    id.getTitle(mContext), x, y,
                    buttonW, buttonW,
                    UDpi.toPixel(TEXT_SIZE), id.textColor, id.bgColor);
            Bitmap image = UResourceManager.getBitmapWithColor(id.imageId, id.lineColor);
            mButtons[i].setImage(image, new Size(UDpi.toPixel(IMAGE_W), UDpi.toPixel(IMAGE_W)));
            UDrawManager.getInstance().addDrawable(mButtons[i]);

            // 表示座標を少し調整
            mButtons[i].setImageAlignment(UAlignment.Center);
            mButtons[i].setImageOffset(0, UDpi.toPixel(-20));
            mButtons[i].setTextOffset(0, UDpi.toPixel(16));

            x += buttonW + UDpi.toPixel(MARGIN_H);
        }
        y += buttonW + UDpi.toPixel(MARGIN_V);

        // 下の段は横長ボタン
        buttonW = width - UDpi.toPixel(MARGIN_H) * 2;
        int buttonH = UDpi.toPixel(BUTTON_H);
        x = UDpi.toPixel(MARGIN_H);
        for (int i = 2; i < ButtonId.values().length; i++) {
            // デバッグモードがONの場合のみDebugを表示
            ButtonId id = ButtonId.values()[i];

            if (id == ButtonId.Debug) {
                if (!UDebug.isDebug) continue;
            }

            mButtons[i] = new UButtonText(this, buttonType, id.ordinal(), DRAW_PRIORITY,
                    id.getTitle(mContext), x, y,
                    buttonW, buttonH,
                    UDpi.toPixel(TEXT_SIZE), id.textColor, id.bgColor);
            Bitmap image = UResourceManager.getBitmapWithColor(id.imageId, id.lineColor);
            mButtons[i].setImage(image, new Size(UDpi.toPixel(IMAGE_W), UDpi.toPixel(IMAGE_W)));
            UDrawManager.getInstance().addDrawable(mButtons[i]);

            // 表示座標を少し調整
            mButtons[i].setImageAlignment(UAlignment.Center);
            mButtons[i].setImageOffset(UDpi.toPixel(-IMAGE_W - 20 - MARGIN_H / 2), 0);
            mButtons[i].setTextOffset(UDpi.toPixel(MARGIN_H) / 2, 0);

            y += buttonH + UDpi.toPixel(MARGIN_V);
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
        if ( id < ButtonId.values().length ) {

            ButtonId buttonId = ButtonId.toEnum(id);
            switch (buttonId) {
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
        } else {
            // ズームボタン
            switch (id) {
                case ButtonIdZoomOut:
                    UDpi.scaleDown();
                    initDrawables();
                    mParentView.invalidate();
                    showScaleToast();
                    break;
                case ButtonIdZoomIn:
                    UDpi.scaleUp();
                    initDrawables();
                    mParentView.invalidate();

                    showScaleToast();
                    break;
            }
        }
        return false;
    }

    /**
     * スケール変更時のToastを表示する
     */
    private void showScaleToast() {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(mContext, UDpi.getScaleText(), Toast.LENGTH_LONG);
        mToast.show();
    }
}
