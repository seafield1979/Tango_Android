package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
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
        Edit(R.string.title_edit, Color.rgb(100, 200, 100)),
        Study(R.string.title_study, Color.rgb(200,100,100)),
        History(R.string.title_history, Color.rgb(200,200,0)),
        Settings(R.string.title_settings, Color.rgb(153,204,255)),
        Help(R.string.title_help, Color.rgb(255,178,102))

        ;

        private int textId;
        private int color;

        ButtonId(int textId, int color) {
            this.textId = textId;
            this.color = color;
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



    /**
     * Member variables
     */
    // Title
    private UTextView mTitleText;

    // Buttons
    private UButton mButtons[] = new UButton[ButtonId.values().length];


    /**
     * Constructor
     */
    public PageViewTitle(Context context, View parentView) {
        super(context, parentView);
    }

    /**
     * Methods
     */

    public void onShow() {

    }

    public void onHide() {
        isFirst = true;
    }

    /**
     * そのページで表示される描画オブジェクトを初期化する
     */
    public void initDrawables() {
        int width = mParentView.getWidth();
        float y = TOP_Y;

        UButtonType buttonType;

        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();

        // タイトル
        mTitleText = new UTextView(UResourceManager.getStringById(R.string.app_title),
                TITLE_TEXT_SIZE, DRAW_PRIORITY,
                UAlignment.CenterX, width, false, false, false,
                width / 2, y, width, UColor.getRandomColor(), 0);
        mTitleText.addToDrawManager();
        y += mTitleText.size.height + MARGIN_V;

        // ボタンの配置
        // 1行に２つづつ配置
        float x;
        int buttonW = BUTTON_W;
        if (buttonW * 2 + MARGIN_H * 3 > width) {
            buttonW = (width - MARGIN_H * 3) / 2;
        }
        for (int i = 0; i< mButtons.length; i++) {
            ButtonId id = ButtonId.values()[i];
            buttonType = UButtonType.Press;

            if (i % 2 == 0) {
                x = width / 2 - buttonW - MARGIN_H / 2;
            } else {
                x = width / 2 + MARGIN_H / 2;
            }
            mButtons[i] = new UButtonText(this, buttonType, id.ordinal(), DRAW_PRIORITY,
                    id.getTitle(mContext), x, y,
                    buttonW, buttonW,
                    TEXT_SIZE, Color.WHITE, id.color);


            UDrawManager.getInstance().addDrawable(mButtons[i]);
            if (i % 2 == 1) {
                y += buttonW + MARGIN_V;
            }
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
                UPageViewManager.getInstance().stackPage(PageView.Edit);
                break;
            case Study:
                UPageViewManager.getInstance().stackPage(PageView.StudySelect);
                break;
            case History:
                UPageViewManager.getInstance().stackPage(PageView.History);
                break;
            case Settings:
                UPageViewManager.getInstance().stackPage(PageView.Settings);
                break;
            case Help:
                UPageViewManager.getInstance().stackPage(PageView.Help);
                break;
        }
        return false;
    }
}
