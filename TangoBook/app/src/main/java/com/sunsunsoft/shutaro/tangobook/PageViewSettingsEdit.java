package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by shutaro on 2016/12/24.
 *
 * 単語帳編集ページの設定ページ
 */

public class PageViewSettingsEdit extends UPageView implements UButtonCallbacks{

    /**
     * Constants
     */
    private static final int DRAW_PRIORITY = 100;

    private static final int BUTTON2_H = 200;
    private static final int TEXT_SIZE = 50;

    // button ids
    private static final int ButtonIdOptions = 99;
    private static final int ButtonIdBackup = 100;
    private static final int ButtonIdLicense = 101;
    private static final int ButtonIdContact = 102;
    private static final int ButtonIdContactOK = 103;

    private static final int TEXT_COLOR = Color.BLACK;

    /**
     * Member variables
     */
    private UDrawItemsWindow mWindow;

    // Dialog
    private UDialogWindow mDialog;

    /**
     * Constructor
     */
    public PageViewSettingsEdit(Context context, View parentView, String title) {
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

        mWindow = new UDrawItemsWindow(null, DRAW_PRIORITY, 0, 0,
                width, height, Color.WHITE);
        mWindow.addToDrawManager();

        // backup button
        UButtonText button1 = new UButtonText(this, UButtonType.Press, ButtonIdBackup,
                DRAW_PRIORITY,
                UResourceManager.getStringById(R.string.backup_and_restore),
                0, 0, width - MARGIN_H * 2, BUTTON2_H, TEXT_SIZE, UColor.DarkGreen, UColor
                .LightGreen);
        mWindow.addDrawable(button1, false);

//        for (int i=0; i<10; i++) {
//            mWindow.addButton(this, 0, 300, 150, false, "hoge", Color.BLACK, Color.LTGRAY);
//        }
//        for (int i=0; i<10; i++) {
//            mWindow.addTextView("hoge", UAlignment.None, false, true, false, 300, 150, 50,
//                    Color.BLACK, Color.LTGRAY);
//        }
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
            case ButtonIdOptions: {
                // バックアップページに遷移
                PageViewManager.getInstance().stackPage(PageView.Options);
            }
            break;
            case ButtonIdBackup: {
                // バックアップページに遷移
                PageViewManager.getInstance().stackPage(PageView.BackupDB);
            }
            break;
            case ButtonIdLicense:
            {
                // ライセンスページに遷移
                PageViewManager.getInstance().stackPage(PageView.License);
            }
            break;
            case ButtonIdContact:
            {
                // お問い合わせメールダイアログを表示
                if (mDialog == null) {
                    mDialog = UDialogWindow.createInstance(this, null,
                            UDialogWindow.ButtonDir.Horizontal,
                            mParentView.getWidth(),
                            mParentView.getHeight());
                    mDialog.setTitle(UResourceManager.getStringById(R.string.contact_us));
                    mDialog.addTextView(UResourceManager.getStringById(R.string.contact_message),
                            UAlignment.CenterX, true, false, TEXT_SIZE, TEXT_COLOR, 0);
                    mDialog.addButton(ButtonIdContactOK,
                            UResourceManager.getStringById(R.string.send_mail), 0, 0);
                    mDialog.addCloseButton(UResourceManager.getStringById(R.string.close));
                    mDialog.addToDrawManager();
                }
            }
            break;
            case ButtonIdContactOK:
                break;
        }
        return false;
    }
}
