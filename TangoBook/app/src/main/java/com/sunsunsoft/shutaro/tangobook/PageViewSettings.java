package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.Date;

/**
 * Created by shutaro on 2016/12/05.
 *
 * 設定ページ
 */

public class PageViewSettings extends UPageView implements UButtonCallbacks{

    /**
     * Constants
     */
    private static final int DRAW_PRIORITY = 100;

    private static final int TOP_Y = 50;
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 50;
    private static final int BUTTON2_H = 200;
    private static final int TEXT_SIZE = 50;

    private static final int ButtonIdBackup = 100;

    private static final int CHECK_BOX_W = 70;

    /**
     * Member variables
     */

    private UButtonText mBackupButton;
    private UButtonText mLicenseButton;
    private UButtonText mContactButton;

    private UCheckBox mCheckBox1;

    // Dialog
    private UDialogWindow mDialog;

    /**
     * Constructor
     */
    public PageViewSettings(Context context, View parentView, String title) {
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

        float x;
        float y = TOP_Y;

        // Backup
        // backup button
        x = MARGIN_H;
        mBackupButton = new UButtonText(this, UButtonType.Press, ButtonIdBackup, DRAW_PRIORITY,
                UResourceManager.getStringById(R.string.backup_and_restore),
                x, y, width - MARGIN_H * 2, BUTTON2_H, TEXT_SIZE, UColor.DarkGreen, UColor
                .LightGreen);
        mBackupButton.addToDrawManager();
        y += mBackupButton.getHeight() + MARGIN_V;

        // checkBox
        mCheckBox1 = new UCheckBox(null, DRAW_PRIORITY, x, y, mParentView.getWidth(),
                CHECK_BOX_W, "hogehoge", TEXT_SIZE, Color.BLACK);
        mCheckBox1.addToDrawManager();
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
