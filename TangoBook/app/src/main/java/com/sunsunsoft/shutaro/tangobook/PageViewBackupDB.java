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

    private static final int TITLE_TEXT_SIZE = 70;
    private static final int TITLE_TEXT_COLOR = Color.rgb(150,150,50);
    private static final int TEXT_SIZE_S = 40;
    private static final int TEXT_SIZE = 50;

    // button Ids
    private static final int ButtonIdBackup = 100;
//    private static final int ButtonIdBackup2 = 101;
    private static final int ButtonIdRestore = 102;
    private static final int ButtonIdReturn = 103;
    private static final int ButtonIdBackupOK = 104;
//    private static final int ButtonIdBackupXmlOK = 105;
    private static final int ButtonIdRestoreOK = 106;

    /**
     * Member variables
     */

    // バックアップタイトル
    private UTextView mBackupTitle;

    // バックアップパス
    private UTextView mBackupPath;

    // バックアップ日時
    private UTextView mBackupDate;

    private UButtonText mBackupButton;
    private UButtonText mRestoreButton;
    private UCheckBox mAutoBackupCheck;
//    private UButtonText mBackupButton2;

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
        int height = mParentView.getHeight();

        float x;
        float y = TOP_Y;

        // Backup
        // mTitle
        mBackupTitle = UTextView.createInstance(
                UResourceManager.getStringById(R.string.backup_path_title),
                TEXT_SIZE_S, DRAW_PRIORITY,
                UAlignment.None, width, false, false,
                MARGIN_H, y, width, Color.BLACK, 0);
        mBackupTitle.addToDrawManager();
        y += mBackupTitle.size.height + MARGIN_V_S;

        // backup file path
        String backupPath = MySharedPref.readString(MySharedPref.RealmBackupPathKey);
        if (backupPath.length() == 0) {
            backupPath = UResourceManager.getStringById(R.string.no_backup);
        }
        mBackupPath = UTextView.createInstance(backupPath,
                TEXT_SIZE, DRAW_PRIORITY,
                UAlignment.None, width, true, true,
                MARGIN_H, y, 0, UColor.DarkGreen, Color.LTGRAY);
        mBackupPath.addToDrawManager();
        y += mBackupPath.size.height + MARGIN_H;

        // backup date time
        String backupDate = UResourceManager.getStringById(R.string.backup_datetime) + " : " +
                MySharedPref.readString(MySharedPref.RealmBackupDateKey);

        mBackupDate = UTextView.createInstance( backupDate,
                TEXT_SIZE_S, DRAW_PRIORITY,
                UAlignment.None, width, false, false,
                MARGIN_H, y, width, Color.BLACK, 0);
        mBackupDate.addToDrawManager();
        y += mBackupDate.size.height + MARGIN_V;

        // backup button
        x = (width - BUTTON2_W * 2 - MARGIN_H) / 2;
        mBackupButton = new UButtonText(this, UButtonType.Press, ButtonIdBackup, DRAW_PRIORITY,
                UResourceManager.getStringById(R.string.backup),
                x, y, BUTTON2_W, BUTTON2_H, TEXT_SIZE, UColor.DarkGreen, UColor.LightGreen);
        mBackupButton.addToDrawManager();
        x += BUTTON2_W + MARGIN_H;

        // restore button
        mRestoreButton = new UButtonText(this, UButtonType.Press, ButtonIdRestore, DRAW_PRIORITY,
                UResourceManager.getStringById(R.string.restore),
                x, y, BUTTON2_W, BUTTON2_H, TEXT_SIZE, UColor.DarkYellow, UColor.LightYellow);
        mRestoreButton.addToDrawManager();
        x = MARGIN_H;
        y += mRestoreButton.getHeight() + MARGIN_V;

        // 自動バックアップ CheckBox
        mAutoBackupCheck = new UCheckBox(this, DRAW_PRIORITY, x, y,
                mParentView.getWidth(), BOX_WIDTH, UResourceManager.getStringById(R.string
                .auto_backup), TEXT_SIZE, TEXT_COLOR);
        mAutoBackupCheck.addToDrawManager();
        if (MySharedPref.readBoolean(MySharedPref.RealmAutoBackup)) {
            mAutoBackupCheck.setChecked(true);
        }
        y += mAutoBackupCheck.getHeight() + MARGIN_V;

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
            case ButtonIdRestore:
            {
                // 復元ボタン
                if (mDialog != null) {
                    mDialog.closeWindow();
                }
                mDialog = UDialogWindow.createInstance(this, null, UDialogWindow.ButtonDir.Horizontal,
                        mParentView.getWidth(), mParentView
                        .getHeight());
                mDialog.addToDrawManager();
                mDialog.setTitle(UResourceManager.getStringById(R.string.confirm_restore));
                mDialog.addButton(ButtonIdRestoreOK, "OK", Color.BLACK, Color.WHITE);
                mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
            }
            break;
            case ButtonIdReturn:
                PageViewManager.getInstance().popPage();
                break;
            case ButtonIdBackupOK:
//                // バックアップ
//                String filePath = RealmManager.backup();
//                if (filePath != null) {
//                    String dateTime = UUtil.convDateFormat(new Date(), ConvDateMode.DateTime);
//
//                    mBackupPath.setText(filePath);
//                    mBackupDate.setText(UResourceManager.getStringById(R.string.backup_datetime) +
//                            " : " +
//                            dateTime);
//                    MySharedPref.writeString(MySharedPref.RealmBackupPathKey, filePath);
//                    MySharedPref.writeString(MySharedPref.RealmBackupDateKey, dateTime);
//                }
//                mDialog.startClosing();
//            }
//            break;
                XmlManager.getInstance().saveXml("tango.xml");
                mDialog.startClosing();
                break;
            case ButtonIdRestoreOK:
                // バックアップから復元
//                RealmManager.restore();
//
                XmlManager.getInstance().loadXml("tango.xml");
                mDialog.startClosing();
                break;
        }
        return false;
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
        MySharedPref.writeBoolean(MySharedPref.RealmAutoBackup, checked);
    }
}
