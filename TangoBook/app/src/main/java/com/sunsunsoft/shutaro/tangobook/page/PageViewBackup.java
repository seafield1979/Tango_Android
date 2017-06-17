package com.sunsunsoft.shutaro.tangobook.page;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;
import com.sunsunsoft.shutaro.tangobook.database.BackupFile;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.listview.ListItemBackup;
import com.sunsunsoft.shutaro.tangobook.listview.ListViewBackupDB;
import com.sunsunsoft.shutaro.tangobook.save.BackupFileInfo;
import com.sunsunsoft.shutaro.tangobook.save.XmlManager;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.uview.*;

import java.util.List;

/**
 * Created by shutaro on 2017/06/16.
 */

public class PageViewBackup extends UPageView
        implements UDialogCallbacks, UButtonCallbacks, UCheckBoxCallbacks, UListItemCallbacks {

    /**
     * Constants
     */
    private static final int DRAW_PRIORITY = 100;

    private static final int TOP_Y = 50;
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 50;

    private static final int TEXT_COLOR = Color.BLACK;
    private static final int BOX_WIDTH = 70;

    private static final int TEXT_SIZE = 50;

    // button IDs
    private static final int ButtonIdOverWriteOK = 100;  // 上書き確認
    private static final int ButtonIdDoneBackup = 101;   // バックアップ完了

    /**
     * Member variables
     */
    private UCheckBox mAutoBackupCheck;
    private ListViewBackupDB mListView;
    private UDialogWindow mDialog;          // バックアップをするかどうかの確認ダイアログ
    private ListItemBackup mBackupItem;     // リストで選択したアイテム

    /**
     * Constructor
     */
    public PageViewBackup(Context context, View parentView, String title) {
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
        int width = mParentView.getWidth();
        int height = mParentView.getHeight();

        float x = MARGIN_H;
        float y = TOP_Y;

        UDrawManager.getInstance().init();

        // 自動バックアップ CheckBox
        mAutoBackupCheck = new UCheckBox(this, DRAW_PRIORITY, x, y,
                mParentView.getWidth(), BOX_WIDTH, UResourceManager.getStringById(R.string
                .auto_backup), TEXT_SIZE, TEXT_COLOR);
        mAutoBackupCheck.addToDrawManager();

        if (MySharedPref.readBoolean(MySharedPref.AutoBackup)) {
            mAutoBackupCheck.setChecked(true);
        }
        y += mAutoBackupCheck.getHeight() + MARGIN_V;

        // ListView
        int listViewH = height - (MARGIN_H * 3 + mAutoBackupCheck.getHeight());
        mListView = new ListViewBackupDB(this, DRAW_PRIORITY, x, y,
                width - MARGIN_H * 2, listViewH, 0);
        mListView.setFrameColor(Color.BLACK);
        mListView.addToDrawManager();
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
     * UCheckBoxCallbacks
     */
    /**
     * チェックされた時のイベント
     */
    public void UCheckBoxChanged(boolean checked) {
        MySharedPref.writeBoolean(MySharedPref.AutoBackup, checked);
    }

    /**
     * UListItemCallbacks
     */
    /**
     * 項目がクリックされた
     * @param item
     */
    public void ListItemClicked(UListItem item) {
        int width = mParentView.getWidth();

        // リストの種類を判定
        if (!(item instanceof ListItemBackup)) return;

        ListItemBackup backupItem = (ListItemBackup)item;

        BackupFile backup = backupItem.getBackup();
        if (backup == null) return;

        if (backup.isEnabled() == false) {
            boolean ret = doBackup(backupItem, backup);
            showDoneDialog(ret);
        } else {
            // バックアップファイルがあったら上書き確認
            mBackupItem = backupItem;
            // Dialog
            mDialog = UDialogWindow.createInstance(this, this,
                    UDialogWindow.ButtonDir.Horizontal, width, mParentView.getHeight());
            mDialog.addToDrawManager();
            mDialog.setTitle(mContext.getString(R.string.confirm_overwrite));
            mDialog.addButton(ButtonIdOverWriteOK, "OK", Color.BLACK, Color.WHITE);
            mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
        }
    }

    /**
     * バックアップ処理
     * xmlファイル作成と、データベースにバックアップ情報を保存
     * @param backupItem
     * @param backup
     */
    private boolean doBackup(ListItemBackup backupItem, BackupFile backup) {
        mBackupItem = backupItem;

        // バックアップファイルがなければそのまま保存
        // xmlに保存
        BackupFileInfo backupInfo = XmlManager.getInstance().saveManualBackup(backup.getId());
        String newText = XmlManager.getInstance().getManualXmlInfo(backup.getId());
        if (newText == null) {
            return false;
        }
        backupItem.setText(newText);

        // データベース更新(BackupFile)
        RealmManager.getBackupFileDao().updateOne(backup.getId(), backupInfo.getFilePath(), backupInfo.getBookNum(), backupInfo.getCardNum());
        return true;
    }

    /**
     * バックアップ完了時ダイアログを表示
     * @param success バックアップ成功したかどうか
     */
    private void showDoneDialog(boolean success) {
        if (mDialog != null) {
            mDialog.closeDialog();
        }

        String text;
        if (success) {
            text = UResourceManager.getStringById(R.string.backup_complete) + "\n\n" +
                    XmlManager.getInstance().getManualXmlInfo(mBackupItem.getBackup().getId());
        } else {
            text = UResourceManager.getStringById(R.string.backup_failed);
        }

        mDialog = UDialogWindow.createInstance(this, this,
                UDialogWindow.ButtonDir.Horizontal, mParentView.getWidth(), mParentView.getHeight());
        mDialog.addToDrawManager();
        mDialog.setTitle(text);
        mDialog.addCloseButton("OK", Color.BLACK, Color.WHITE);
    }

    public void ListItemButtonClicked(UListItem item, int buttonId) {

    }

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        switch (id) {
            case ButtonIdOverWriteOK:
                boolean ret = doBackup(mBackupItem, mBackupItem.getBackup());
                showDoneDialog(ret);
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
