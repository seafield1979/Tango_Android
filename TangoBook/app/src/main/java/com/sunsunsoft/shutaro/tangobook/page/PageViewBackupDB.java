package com.sunsunsoft.shutaro.tangobook.page;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.util.ConvDateMode;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.uview.UButtonCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.UButtonText;
import com.sunsunsoft.shutaro.tangobook.uview.UButtonType;
import com.sunsunsoft.shutaro.tangobook.uview.UCheckBox;
import com.sunsunsoft.shutaro.tangobook.uview.UCheckBoxCallbacks;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.uview.UDialogWindow;
import com.sunsunsoft.shutaro.tangobook.uview.UDrawManager;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.uview.UTextView;
import com.sunsunsoft.shutaro.tangobook.util.UUtil;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;
import com.sunsunsoft.shutaro.tangobook.save.XmlManager;
import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;
import com.sunsunsoft.shutaro.tangobook.save.*;

import java.util.Date;

/**
 * Created by shutaro on 2016/12/23.
 *
 * データベースのバックアップ、復元ページ
 */

public class PageViewBackupDB extends UPageView
        implements UButtonCallbacks, UCheckBoxCallbacks {

    /**
     * Constants
     */
    private static final int DRAW_PRIORITY = 100;

    private static final int TOP_Y = 50;
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 50;
    private static final int MARGIN_V_S = 20;
    private static final int BUTTON2_W = 350;
    private static final int BUTTON2_H = 200;

    private static final int TEXT_COLOR = Color.BLACK;
    private static final int BOX_WIDTH = 70;

    private static final int TEXT_SIZE_S = 40;
    private static final int TEXT_SIZE = 50;

    // button Ids
    private static final int ButtonIdBackup = 100;
    private static final int ButtonIdReturn = 102;
    private static final int ButtonIdBackupOK = 103;
    private static final int ButtonIdRestore1OK = 104;
    private static final int ButtonIdRestore2OK = 105;
    private static final int ButtonIdCleanUp = 106;     //

    private static final int ButtonIdBackup1 = 200;
    private static final int ButtonIdBackup2 = 201;

    /**
     * Member variables
     */
    // バックアップタイトル
    private UTextView mBackupTitle, mAutoBackupTitle;

    // buttons
    private UButtonText mBackupButton;
    private UButtonText mRestoreButton1, mRestoreButton2;
    private UButtonText mCleanupButton;

    private UCheckBox mAutoBackupCheck;

    // Dialog
    private UDialogWindow mDialog;

    /**
     * Constructor
     */
    public PageViewBackupDB(Context context, View parentView, String title) {
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
    public void initDrawables(){
        int width = mParentView.getWidth();

        float y = TOP_Y;

        UDrawManager.getInstance().init();

        // backup button
        mBackupButton = new UButtonText(this, UButtonType.Press, ButtonIdBackup, DRAW_PRIORITY,
                UResourceManager.getStringById(R.string.backup),
                MARGIN_H, y, width - MARGIN_H * 2, BUTTON2_H, TEXT_SIZE, UColor.DarkGreen, UColor
                .LightGreen);

        mBackupButton.addToDrawManager();

        y += mBackupButton.getSize().height + MARGIN_V;


        // 自動バックアップ CheckBox
        mAutoBackupCheck = new UCheckBox(this, DRAW_PRIORITY, MARGIN_H, y,
                mParentView.getWidth(), BOX_WIDTH, UResourceManager.getStringById(R.string
                .auto_backup), TEXT_SIZE, TEXT_COLOR);
        mAutoBackupCheck.addToDrawManager();

        if (MySharedPref.readBoolean(MySharedPref.AutoBackup)) {
            mAutoBackupCheck.setChecked(true);
        }
        y += mAutoBackupCheck.getHeight() + MARGIN_V;



        // 手動バックアップ
        // mTitle
        mBackupTitle = UTextView.createInstance(
                UResourceManager.getStringById(R.string.backup_path_title1),
                TEXT_SIZE_S, DRAW_PRIORITY,
                UAlignment.None, width, false, false,
                MARGIN_H, y, width, Color.BLACK, 0);
        mBackupTitle.addToDrawManager();

        y += mBackupTitle.getSize().height + MARGIN_V_S;

        // button
        // タイトルに表示する文字列
        // Prefにあったらそれを使用、なかったらxmlファイルから情報を取得
        String title = MySharedPref.readString(MySharedPref.BackupInfoKey);
        if (title.length() == 0) {
            title = XmlManager.getXmlInfo(BackupFileType.ManualBackup);
            if (title == null) {
                title = UResourceManager.getStringById(R.string.no_backup);
            } else {
                MySharedPref.writeString(MySharedPref.BackupInfoKey, title);
            }
        }

        mRestoreButton1 = new UButtonText(this, UButtonType.Press, ButtonIdBackup1,
                DRAW_PRIORITY, title,
                MARGIN_H, y, width - MARGIN_H * 2, 0, TEXT_SIZE, UColor.BLACK, Color.LTGRAY);
        mRestoreButton1.addToDrawManager();

        y += mRestoreButton1.getSize().height + MARGIN_H;


        // 自動バックアップ
        // title
        mAutoBackupTitle = UTextView.createInstance(
                UResourceManager.getStringById(R.string.backup_path_title2),
                TEXT_SIZE_S, DRAW_PRIORITY,
                UAlignment.None, width, false, false,
                MARGIN_H, y, width, Color.BLACK, 0);
        mAutoBackupTitle.addToDrawManager();

        y += mAutoBackupTitle.getSize().height + MARGIN_V_S;


        // button
        title = MySharedPref.readString(MySharedPref.AutoBackupInfoKey);
        if (title.length() == 0) {
            title = XmlManager.getXmlInfo(BackupFileType.AutoBackup);
            if (title == null) {
                title = UResourceManager.getStringById(R.string.no_backup);
            } else {
                MySharedPref.writeString(MySharedPref.AutoBackupInfoKey, title);
            }
        }
        mRestoreButton2 = new UButtonText(this, UButtonType.Press, ButtonIdBackup2,
                DRAW_PRIORITY, title,
                MARGIN_H, y, width - MARGIN_H * 2, 0, TEXT_SIZE, UColor.BLACK, Color.LTGRAY);
        mRestoreButton2.addToDrawManager();

        y += mRestoreButton2.getHeight() + MARGIN_V;

        // cleanup (for Debug)
//        if (UDebug.isDebug) {
            mCleanupButton = new UButtonText(this, UButtonType.Press, ButtonIdCleanUp, DRAW_PRIORITY,
                    UResourceManager.getStringById(R.string.clean_up),
                    MARGIN_H, y, width - MARGIN_H * 2, BUTTON2_H, TEXT_SIZE, UColor.BLACK, UColor
                    .LTGRAY);
            mCleanupButton.addToDrawManager();
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
            case ButtonIdBackup:
            {
                // バックアップボタン
                if (mDialog != null) {
                    mDialog.closeWindow();
                }
                mDialog = UDialogWindow.createInstance(this, null, UDialogWindow.ButtonDir.Horizontal, mParentView.getWidth(),
                        mParentView
                        .getHeight());
                mDialog.addToDrawManager();
                mDialog.setTitle(UResourceManager.getStringById(R.string.confirm_backup));
                mDialog.addButton(ButtonIdBackupOK, "OK", Color.BLACK, Color.WHITE);
                mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
            }
                break;
            case ButtonIdBackup1:
            case ButtonIdBackup2:
                // 復元ボタン
                if (mDialog != null) {
                    mDialog.closeWindow();
                }
                mDialog = UDialogWindow.createInstance(this, null, UDialogWindow.ButtonDir.Horizontal,
                        mParentView.getWidth(), mParentView
                                .getHeight());
                mDialog.addToDrawManager();
                mDialog.setTitle(UResourceManager.getStringById(R.string.confirm_restore));
                mDialog.addButton((id == ButtonIdBackup1) ? ButtonIdRestore1OK : ButtonIdRestore2OK,
                        "OK", Color.BLACK, Color.WHITE);
                mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
                break;

            case ButtonIdReturn:
                PageViewManager.getInstance().popPage();
                break;

            case ButtonIdBackupOK:
            {
                // xmlに保存
                String filePath = XmlManager.getInstance().saveXml(BackupFileType.ManualBackup);
                mDialog.startClosing();


                // 画面表示更新
                String buttonTitle;
                int messageId;

                if (filePath == null) {
                    buttonTitle = UResourceManager.getStringById(R.string.no_backup);
                    messageId = R.string.failed_backup;
                } else {
                    String dateTime = UUtil.convDateFormat(new Date(), ConvDateMode.DateTime);
                    buttonTitle =  UResourceManager.getStringById(R.string.card_count) +
                            ":" + XmlManager.getInstance().getBackpuCardNum() +
                            "   " + UResourceManager.getStringById(R.string.book_count) +
                            ":" + XmlManager.getInstance().getBackupBookNum() + "\n" +
                            UResourceManager.getStringById(R.string.location) +
                            filePath + "\n" +
                            UResourceManager.getStringById(R.string.datetime) +
                            " : " + dateTime;
                    messageId = R.string.finish_backup;
                }
                mRestoreButton1.setText(buttonTitle);

                // 情報を保存
                MySharedPref.writeString(MySharedPref.BackupInfoKey, buttonTitle);

                initDrawables();

                // ダイアログが閉じたら完了メッセージダイアログを表示
                showDialog(UResourceManager.getStringById(messageId));

                return true;
            }

            case ButtonIdRestore1OK:
                XmlManager.getInstance().loadXml(BackupFileType.ManualBackup);
                mDialog.startClosing();

                // ダイアログが閉じたら完了メッセージダイアログを表示
                showDialog(UResourceManager.getStringById(R.string.finish_restore));
                break;
            case ButtonIdRestore2OK:
                XmlManager.getInstance().loadXml(BackupFileType.AutoBackup);
                mDialog.startClosing();

                // ダイアログが閉じたら完了メッセージダイアログを表示
                showDialog(UResourceManager.getStringById(R.string.finish_restore));
                break;

            case ButtonIdCleanUp:
                MySharedPref.delete(MySharedPref.BackupInfoKey);
                XmlManager.getInstance().removeXml(BackupFileType.ManualBackup);

                MySharedPref.delete(MySharedPref.AutoBackupInfoKey);
                XmlManager.getInstance().removeXml(BackupFileType.AutoBackup);

                initDrawables();

                return true;
        }
        return false;
    }

    private void showDialog(String message) {
        mDialog = UDialogWindow.createInstance(null, null, UDialogWindow.ButtonDir.Horizontal,
                mParentView.getWidth(), mParentView.getHeight());
        mDialog.setTitle(message);
        mDialog.addToDrawManager();
        mDialog.addCloseButton(null,Color.BLACK, Color.WHITE);
    }

    /**
     * Callbacks
     */
    /**
     * UCheckBoxCallbacks
     */
    /**
     * チェックされた時のイベント
     */
    public void UCheckBoxChanged(boolean checked) {
        MySharedPref.writeBoolean(MySharedPref.AutoBackup, checked);
    }

}
