package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.net.Uri;
import android.view.View;

import java.util.Date;

/**
 * Created by shutaro on 2016/12/05.
 *
 * 設定ページ
 */

public class PageViewSettingsTop extends UPageView
        implements UButtonCallbacks, UDialogCallbacks {

    /**
     * Constants
     */
    private static final int DRAW_PRIORITY = 100;

    private static final int BUTTON2_H = 200;
    private static final int TEXT_SIZE = 50;
    private static final int BUTTON_COLOR = UColor.LightBlue;

    // button ids
    private static final int ButtonIdOption = 99;
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
    public PageViewSettingsTop(Context context, View parentView, String title) {
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


        UButtonText button1;
        // 各種設定
        button1 = new UButtonText(this, UButtonType.Press, ButtonIdOption ,
                DRAW_PRIORITY,
                UResourceManager.getStringById(R.string.option),
                0, 0, width - MARGIN_H * 2, BUTTON2_H, TEXT_SIZE, UColor.BLACK, BUTTON_COLOR);
        mWindow.addDrawable(button1, false);

        // backup button
        button1 = new UButtonText(this, UButtonType.Press, ButtonIdBackup,
                DRAW_PRIORITY,
                UResourceManager.getStringById(R.string.backup_and_restore),
                0, 0, width - MARGIN_H * 2, BUTTON2_H, TEXT_SIZE, UColor.BLACK, BUTTON_COLOR);
        mWindow.addDrawable(button1, false);

        // ライセンス
        button1 = new UButtonText(this, UButtonType.Press, ButtonIdLicense, DRAW_PRIORITY,
                 UResourceManager.getStringById(R.string.license),
                0, 0, width - MARGIN_H * 2, BUTTON2_H, TEXT_SIZE, UColor.BLACK, BUTTON_COLOR);
        mWindow.addDrawable(button1, false);

        // お問い合わせ（メール）
        button1 = new UButtonText(this, UButtonType.Press, ButtonIdContact, DRAW_PRIORITY,
                UResourceManager.getStringById(R.string.contact_us),
                0, 0, width - MARGIN_H * 2, BUTTON2_H, TEXT_SIZE, UColor.BLACK, BUTTON_COLOR);
        mWindow.addDrawable(button1, true);
    }

    /**
     * Call mailer
     * メーラーを立ち上げる
     */
    private void callMailer(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SENDTO);

        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:" + AppInfo.contactMailTo));
        intent.putExtra(Intent.EXTRA_SUBJECT, UResourceManager.getStringById(R.string.contact_mail_title));
        intent.putExtra(Intent.EXTRA_TEXT, UResourceManager.getStringById(R.string
                .contact_mail_body));

        mContext.startActivity(Intent.createChooser(intent, null));

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
            case ButtonIdOption: {
                // オプション設定ページに移動
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
                // Main2Activity アクティビティを呼び出す
                Intent intent = new Intent(mContext, LicensePageActivity.class);

                mContext.startActivity(intent);
            }
                break;
            case ButtonIdContact:
            {
                // お問い合わせメールダイアログを表示
                if (mDialog == null) {
                    mDialog = UDialogWindow.createInstance(this, this,
                            UDialogWindow.ButtonDir.Horizontal,
                            mParentView.getWidth(),
                            mParentView.getHeight());
                    mDialog.setTitle(UResourceManager.getStringById(R.string.contact_us));
                    mDialog.addTextView(UResourceManager.getStringById(R.string.contact_message),
                            UAlignment.CenterX, true, false, TEXT_SIZE, TEXT_COLOR, 0);
                    mDialog.addButton(ButtonIdContactOK,
                            UResourceManager.getStringById(R.string.send_mail), 0, Color.WHITE);
                    mDialog.addCloseButton(UResourceManager.getStringById(R.string.close));
                    mDialog.addToDrawManager();
                }
            }
                break;
            case ButtonIdContactOK:
                mDialog.closeDialog();
                callMailer();
                break;
        }
        return false;
    }

    /**
     * UDialogCallbacks
     */
    public void dialogClosed(UDialogWindow dialog) {
        if (mDialog == dialog) {
            mDialog = null;
        }
    }
}
