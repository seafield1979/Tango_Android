package com.sunsunsoft.shutaro.tangobook.page;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.util.Size;
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.activity.LicensePageActivity;
import com.sunsunsoft.shutaro.tangobook.app.AppInfo;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonText;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonType;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;
import com.sunsunsoft.shutaro.tangobook.uview.window.UDialogCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.window.UDialogWindow;
import com.sunsunsoft.shutaro.tangobook.uview.window.UDrawItemsWindow;

/**
 * Created by shutaro on 2016/12/05.
 *
 * 設定ページ
 */

public class PageViewSettingsTop extends UPageView
        implements UButtonCallbacks, UDialogCallbacks {

    /**
     * Enums
     */
    enum ButtonId {
        Option(R.string.title_options, UColor.DarkBlue, UColor.DarkBlue,
                Color.rgb(153,204,255), R.drawable.settings_1),
        Backup(R.string.backup, UColor.DarkBlue, UColor.DarkBlue,
                Color.rgb(153,204,255), R.drawable.backup),
        Restore(R.string.restore, UColor.DarkBlue, UColor.DarkBlue,
                Color.rgb(153,204,255), R.drawable.restore),
        License(R.string.license, UColor.DarkBlue, UColor.DarkBlue,
                Color.rgb(153,204,255), R.drawable.license),
        Contact(R.string.contact_us, UColor.DarkBlue, UColor.DarkBlue,
                Color.rgb(153,204,255), R.drawable.mail)
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
            if (value >= values().length) return Option;
            return values()[value];
        }
    }

    /**
     * Constants
     */
    private static final int DRAW_PRIORITY = 100;

    private static final int BUTTON2_H = 67;
    private static final int TEXT_SIZE = 17;
    private static final int IMAGE_W = 35;

    // button ids
    private static final int ButtonIdContactOK = 100;

    private static final int TEXT_COLOR = Color.BLACK;

    /**
     * Member variables
     */
    private UDrawItemsWindow mWindow;

    // Buttons
    private UButtonText mButtons[] = new UButtonText[ButtonId.values().length];

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


        float x = MARGIN_H;
        float y = MARGIN_V;

        int buttonW = width - MARGIN_H * 2;
        int buttonH = UDpi.toPixel(BUTTON2_H);

        for (int i = 0; i < ButtonId.values().length; i++) {
            ButtonId id = ButtonId.values()[i];

            mButtons[i] = new UButtonText(this, UButtonType.Press, id.ordinal(), DRAW_PRIORITY,
                    id.getTitle(mContext), x, y,
                    buttonW, buttonH,
                    UDpi.toPixel(TEXT_SIZE), id.textColor, id.bgColor);
            Bitmap image = UResourceManager.getBitmapWithColor(id.imageId, id.lineColor);
            mButtons[i].setImage(image, new Size(UDpi.toPixel(IMAGE_W), UDpi.toPixel(IMAGE_W)));
            UDrawManager.getInstance().addDrawable(mButtons[i]);

            // 表示座標を少し調整
            mButtons[i].setImageAlignment(UAlignment.Center);
            mButtons[i].setImageOffset(UDpi.toPixel(-IMAGE_W - 50), 0);
            mButtons[i].setTextOffset(MARGIN_H / 2, 0);

            y += buttonH + MARGIN_V;
        }
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
        if (id == ButtonIdContactOK) {
            mDialog.closeDialog();
            callMailer();
            return true;
        }

        ButtonId buttonId = ButtonId.toEnum(id);
        switch (buttonId) {
            case Option:
                // オプション設定ページに移動
                PageViewManager.getInstance().stackPage(PageView.Options);
                break;
            case Backup:
                // バックアップページに遷移
                PageViewManager.getInstance().stackPage(PageView.BackupDB);
                break;
            case Restore:
                // バックアップページに遷移
                PageViewManager.getInstance().stackPage(PageView.RestoreDB);
                break;
            case License:
                // ライセンスページに遷移
                // Main2Activity アクティビティを呼び出す
                Intent intent = new Intent(mContext, LicensePageActivity.class);

                mContext.startActivity(intent);
                break;
            case Contact:
                // お問い合わせメールダイアログを表示
                if (mDialog == null) {
                    mDialog = UDialogWindow.createInstance(this, this,
                            UDialogWindow.ButtonDir.Horizontal,
                            mParentView.getWidth(),
                            mParentView.getHeight());
                    mDialog.setTitle(UResourceManager.getStringById(R.string.contact_us));
                    mDialog.addTextView(UResourceManager.getStringById(R.string.contact_message),
                            UAlignment.CenterX, true, false, UDpi.toPixel(TEXT_SIZE), TEXT_COLOR, 0);
                    mDialog.addButton(ButtonIdContactOK,
                            UResourceManager.getStringById(R.string.send_mail), 0, Color.WHITE);
                    mDialog.addCloseButton(UResourceManager.getStringById(R.string.close));
                    mDialog.addToDrawManager();
                }
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
