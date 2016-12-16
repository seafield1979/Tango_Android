package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

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
    private static final int BUTTON_W = 300;
    private static final int BUTTON_H = 120;
    private static final int BUTTON2_W = 350;
    private static final int BUTTON2_H = 200;

    private static final int TITLE_TEXT_SIZE = 70;
    private static final int TITLE_TEXT_COLOR = Color.rgb(150,150,50);
    private static final int TEXT_SIZE_S = 35;
    private static final int TEXT_SIZE = 50;
    private static final int TEXT_COLOR = Color.WHITE;

    private static final int ButtonIdBackup = 100;
    private static final int ButtonIdRestore = 101;
    private static final int ButtonIdReturn = 102;
    private static final int ButtonIdBackupOK = 103;
    private static final int ButtonIdRestoreOK = 104;


    /**
     * Member variables
     */
    private UTextView mBackupTitle;
    private UTextView mBackupPath;
    private UButtonText mBackupButton;
    private UButtonText mRestoreButton;
    private UButtonText mReturnButton;

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

        float x = MARGIN_H;
        float y = TOP_Y;

        // Backup
        // title
        mBackupTitle = UTextView.createInstance(
                UResourceManager.getStringById(R.string.backup_path_title),
                TEXT_SIZE_S, DRAW_PRIORITY,
                UAlignment.None, width, false, false,
                MARGIN_H, y, width, Color.BLACK, 0);
        mBackupTitle.addToDrawManager();
        y += mBackupTitle.size.height;

        // backup path
        mBackupPath = UTextView.createInstance(
                MySharedPref.readString(MySharedPref.RealmBackupPathKey),
                TEXT_SIZE, DRAW_PRIORITY,
                UAlignment.None, width, true, true,
                MARGIN_H, y, width - MARGIN_H * 2, TEXT_COLOR, Color.LTGRAY);
        mBackupPath.addToDrawManager();
        y += mBackupPath.size.height + MARGIN_H;

        // backup button
        x = (width - BUTTON2_W * 2 - MARGIN_H) / 2;
        mBackupButton = new UButtonText(this, UButtonType.Press, ButtonIdBackup, DRAW_PRIORITY,
                UResourceManager.getStringById(R.string.backup),
                x, y, BUTTON2_W, BUTTON2_H, TEXT_SIZE, TEXT_COLOR, Color.rgb(100,200,100));
        mBackupButton.addToDrawManager();
        x += BUTTON2_W + MARGIN_H;

        // restore button
        mRestoreButton = new UButtonText(this, UButtonType.Press, ButtonIdRestore, DRAW_PRIORITY,
                UResourceManager.getStringById(R.string.restore),
                x, y, BUTTON2_W, BUTTON2_H, TEXT_SIZE, TEXT_COLOR, Color.rgb(100,200,100));
        mRestoreButton.addToDrawManager();

        y += BUTTON_W + MARGIN_V;

        // Button
        mReturnButton = new UButtonText(this, UButtonType.Press, ButtonIdReturn,
                DRAW_PRIORITY, UResourceManager.getStringById(R.string.return1),
                (width - BUTTON_W)/2, height - BUTTON_H - MARGIN_H, BUTTON_W, BUTTON_H, 50, Color
                .WHITE, Color.rgb(100,200,
                100));
        mReturnButton.addToDrawManager();

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
            case ButtonIdBackup:
            {
                // バックアップボタン
                if (mDialog != null) {
                    mDialog.closeWindow();
                }
                mDialog = UDialogWindow.createInstance(this, mParentView.getWidth(), mParentView
                        .getHeight());
                mDialog.addToDrawManager();
                mDialog.setTitle(UResourceManager.getStringById(R.string.confirm_backup));
                mDialog.addButton(ButtonIdBackupOK, "OK", Color.BLACK, Color.LTGRAY);
                mDialog.addCloseButton("Cancel");
            }
                break;
            case ButtonIdRestore:
            {
                // 復元ボタン
                if (mDialog != null) {
                    mDialog.closeWindow();
                }
                mDialog = UDialogWindow.createInstance(this, mParentView.getWidth(), mParentView
                        .getHeight());
                mDialog.addToDrawManager();
                mDialog.setTitle(UResourceManager.getStringById(R.string.confirm_restore));
                mDialog.addButton(ButtonIdRestoreOK, "OK", Color.BLACK, Color.LTGRAY);
                mDialog.addCloseButton("Cancel");
            }
                break;
            case ButtonIdReturn:
                PageViewManager.getInstance().popPage();
                break;
            case ButtonIdBackupOK: {
                // バックアップ
                String filePath = RealmManager.backup();
                if (filePath != null) {
                    mBackupPath.setText(filePath);
                    MySharedPref.writeString(MySharedPref.RealmBackupPathKey, filePath);
                }
                mDialog.startClosing();
            }
                break;
            case ButtonIdRestoreOK:
                // バックアップから復元
                RealmManager.restore();

                mDialog.startClosing();
                break;
        }
        return false;
    }
}
