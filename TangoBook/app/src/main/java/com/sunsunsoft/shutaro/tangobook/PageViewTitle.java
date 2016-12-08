package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
        Edit(R.string.title_edit),
        Study(R.string.title_study),
        Settings(R.string.title_settings)
        ;

        private int titleId;
        ButtonId(int titleId) {
            this.titleId = titleId;
        }
        String getTitle(Context context) {
            return context.getString(titleId);
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
    public static final int BUTTON_PRIORITY = 100;

    /**
     * Member variables
     */
    UButton buttons[] = new UButton[ButtonId.values().length];


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
        float y = 300;
        UButtonType buttonType;

        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();

        for (int i=0; i<buttons.length; i++) {
            ButtonId id = ButtonId.values()[i];
            buttonType = UButtonType.BGColor;

            buttons[i] = new UButtonText(this, buttonType, id.ordinal(), BUTTON_PRIORITY,
                    id.getTitle(mContext), 100, y,
                    width - 100*2, 120,
                    Color.WHITE,
                    Color.rgb(0,128,0));


            UDrawManager.getInstance().addDrawable(buttons[i]);
            y += 150;
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
                UPageViewManager.getInstance().stackPage(PageView.TangoEdit);
                break;
            case Study:
                UPageViewManager.getInstance().stackPage(PageView.TangoSelect);
                break;
            case Settings:
                UPageViewManager.getInstance().stackPage(PageView.Settings);
                break;
        }
        return false;
    }
}
