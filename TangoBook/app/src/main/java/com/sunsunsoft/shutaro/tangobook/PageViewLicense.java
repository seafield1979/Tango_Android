package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by shutaro on 2016/12/23.
 *
 * ライセンス表示ページ
 */

public class PageViewLicense extends UPageView implements UButtonCallbacks{

    /**
     * Constants
     */
    private static final int DRAW_PRIORITY = 100;

    private static final int TOP_Y = 50;
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 50;
    private static final int MARGIN_V_S = 20;
    private static final int BUTTON_W = 300;
    private static final int BUTTON_H = 120;
    private static final int BUTTON2_W = 350;
    private static final int BUTTON2_H = 200;

    private static final int TITLE_TEXT_SIZE = 70;
    private static final int TITLE_TEXT_COLOR = Color.rgb(150,150,50);
    private static final int TEXT_SIZE_S = 40;
    private static final int TEXT_SIZE = 50;

    private static final int ButtonIdBackup = 100;


    /**
     * Member variables
     */

    // Dialog
    private UDialogWindow mDialog;

    /**
     * Constructor
     */
    public PageViewLicense(Context context, View parentView, String title) {
        super(context, parentView, title);
    }

    /**
     * Methods
     */

    protected void onShow() {

    }

    protected void onHide() {
        super.onHide();
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
     * そのページで表示される描画オブジェクトを初期化する
     */
    public void initDrawables() {
        UDrawManager.getInstance().init();

        int width = mParentView.getWidth();
        int height = mParentView.getHeight();
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
            case ButtonIdBackup: {
                // バックアップボタン
                // バックアップページに遷移
                PageViewManager.getInstance().stackPage(PageView.BackupDB);
            }
            break;
        }
        return false;
    }
}
