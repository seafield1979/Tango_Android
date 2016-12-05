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
        Edit("単語帳を作る"),
        Study("学習する"),
        Settings("設定")
        ;

        private String title;
        ButtonId(String title) {
            this.title = title;
        }
        String getTitle() {
            return title;
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
        super(context, parentView, PageView.Title.getDrawId());
    }

    /**
     * Methods
     */

    /**
     * そのページで表示される描画オブジェクトを初期化する
     */
    public void initDrawables() {
        int width = mParentView.getWidth();
        float y = 300;
        UButtonType buttonType;

        for (int i=0; i<buttons.length; i++) {
            ButtonId id = ButtonId.values()[i];
            buttonType = UButtonType.BGColor;

            buttons[i] = new UButtonText(this, buttonType, id.ordinal(), BUTTON_PRIORITY,
                    id.getTitle(), 100, y,
                    width - 100*2, 120,
                    Color.WHITE,
                    Color.rgb(0,128,0));


            UDrawManager.getInstance().addDrawable(buttons[i]);
            y += 150;
        }
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
        return false;
    }

    /**
     * タッチ処理
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt) {

        return false;
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
    public boolean UButtonClick(int id) {
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
