package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.Date;

/**
 * Created by shutaro on 2016/12/23.
 *
 * データベースのバックアップ、復元ページ
 */

public class PageViewBackupDB extends UPageView
        implements UButtonCallbacks, UCheckBoxCallbacks{

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

    private static final int ButtonIdBackup1 = 200;
    private static final int ButtonIdBackup2 = 201;

    /**
     * Member variables
     */
    // バックアップタイトル
    private UTextView mBackupTitle, mAutoBackupTitle;

    // バックアップパス
    private UButtonText mBackupButton;
    private UButtonText mRestoreButton1, mRestoreButton2;

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
    public void initDrawables() {
        UDrawManager.getInstance().init();

        int width = mParentView.getWidth();

        float y = TOP_Y;

        // backup button
        mBackupButton = new UButtonText(this, UButtonType.Press, ButtonIdBackup, DRAW_PRIORITY,
                UResourceManager.getStringById(R.string.backup),
                MARGIN_H, y, width - MARGIN_H * 2, BUTTON2_H, TEXT_SIZE, UColor.DarkGreen, UColor
                .LightGreen);
        mBackupButton.addToDrawManager();
        y += mBackupButton.size.height + MARGIN_V;


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
        y += mBackupTitle.size.height + MARGIN_V_S;

        // path & datetime
        String backupPath = MySharedPref.readString(MySharedPref.BackupPathKey);
        if (backupPath.length() == 0) {
            backupPath = UResourceManager.getStringById(R.string.no_backup);
        } else {
            backupPath = UResourceManager.getStringById(R.string.location) +
                    backupPath + "\n" + UResourceManager.getStringById(R.string.backup_datetime) +
                    " : " +
                    MySharedPref.readString(MySharedPref.BackupDateKey);
        }

        // button
        mRestoreButton1 = new UButtonText(this, UButtonType.Press, ButtonIdBackup1,
                DRAW_PRIORITY, backupPath,
                MARGIN_H, y, width - MARGIN_H * 2, 0, TEXT_SIZE, UColor.DarkGreen, Color.LTGRAY);
        mRestoreButton1.addToDrawManager();

        y += mRestoreButton1.size.height + MARGIN_H;





        // 自動バックアップ
        // title
        mAutoBackupTitle = UTextView.createInstance(
                UResourceManager.getStringById(R.string.backup_path_title2),
                TEXT_SIZE_S, DRAW_PRIORITY,
                UAlignment.None, width, false, false,
                MARGIN_H, y, width, Color.BLACK, 0);
        mAutoBackupTitle.addToDrawManager();
        y += mAutoBackupTitle.size.height + MARGIN_V_S;

        // path & datetime
        backupPath = MySharedPref.readString(MySharedPref.AutoBackupPathKey);
        if (backupPath.length() == 0) {
            backupPath = UResourceManager.getStringById(R.string.no_backup);
        } else {
            backupPath = UResourceManager.getStringById(R.string.location) +
                    backupPath + "\n" + UResourceManager.getStringById(R.string.backup_datetime) +
                    " : " +
                    MySharedPref.readString(MySharedPref.AutoBackupDateKey);
        }
        // button
        mRestoreButton2 = new UButtonText(this, UButtonType.Press, ButtonIdBackup2,
                DRAW_PRIORITY, backupPath,
                MARGIN_H, y, width - MARGIN_H * 2, 0, TEXT_SIZE, UColor.DarkGreen, Color.LTGRAY);
        mRestoreButton2.addToDrawManager();

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
                String filePath = XmlManager.getInstance().saveXml(XmlManager.ManualBackupFile);
                mDialog.startClosing();

                // 画面表示更新
                String dateTime = UUtil.convDateFormat(new Date(), ConvDateMode.DateTime);
                mRestoreButton1.setText(filePath);

                MySharedPref.writeString(MySharedPref.BackupPathKey, filePath);
                MySharedPref.writeString(MySharedPref.BackupDateKey, dateTime);

                // ダイアログが閉じたら完了メッセージダイアログを表示
                showDialog(UResourceManager.getStringById(R.string.finish_backup));

                isFirst = true;
                return true;
            }

            case ButtonIdRestore1OK:
                XmlManager.getInstance().loadXml(XmlManager.ManualBackupFile);
                mDialog.startClosing();

                // ダイアログが閉じたら完了メッセージダイアログを表示
                showDialog(UResourceManager.getStringById(R.string.finish_restore));
                break;
            case ButtonIdRestore2OK:
                XmlManager.getInstance().loadXml(XmlManager.AutoBackupFile);
                mDialog.startClosing();

                // ダイアログが閉じたら完了メッセージダイアログを表示
                showDialog(UResourceManager.getStringById(R.string.finish_restore));
                break;
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
