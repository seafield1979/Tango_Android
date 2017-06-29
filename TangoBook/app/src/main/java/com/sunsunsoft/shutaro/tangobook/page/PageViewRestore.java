package com.sunsunsoft.shutaro.tangobook.page;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.database.BackupFile;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.listview.ListItemBackup;
import com.sunsunsoft.shutaro.tangobook.listview.ListViewBackup;
import com.sunsunsoft.shutaro.tangobook.save.BackupManager;
import com.sunsunsoft.shutaro.tangobook.util.*;
import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonText;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonType;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;
import com.sunsunsoft.shutaro.tangobook.uview.window.UDialogCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.window.UDialogWindow;

import java.io.File;

/**
 * Created by shutaro on 2017/06/16.
 */

public class PageViewRestore extends UPageView
        implements UDialogCallbacks, UButtonCallbacks, UListItemCallbacks {

    /**
     * Constants
     */
    private static final int DRAW_PRIORITY = 100;

    // layout
    private static final int TOP_Y = 17;
    private static final int MARGIN_H = 17;
    private static final int MARGIN_V = 17;
    private static final int TEXT_SIZE_S = 15;
    private static final int TEXT_SIZE = 17;

    private static final int TEXT_COLOR = Color.BLACK;

    // button Ids
    private static final int ButtonIdRestoreFromFile = 100;     // 選択したファイルから復元ボタンを押した
    private static final int ButtonIdRestoreFromFileOK = 101;   // 選択したファイルから復元するかどうかでOKを選択
    private static final int ButtonIdRestoreOK1 = 102;          // 復元確認1でOKを選択
    private static final int ButtonIdRestoreOK2 = 103;          // 復元確認2でOKを選択
    private static final int ButtonIdNotFoundOK = 104;          // バックアップファイルが見つからなかった

    /**
     * Member variables
     */
    private Context mContext;

    private UButtonText mRestoreButton;     // 復元ボタン
    private ListViewBackup mListView;
    private ListItemBackup mBackupItem;     // リストで選択したアイテム

    // Dialog
    private UDialogWindow mDialog;

    private FileDialog mFileDialog;      // ファイルを選択するモーダルダイアログ
    private File mRestoreFile;          // 復元元のバックアップファイル

    /**
     * Constructor
     */
    public PageViewRestore(Context context, View parentView, String title) {
        super(context, parentView, title);

        mContext = context;
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

        float x = UDpi.toPixel(MARGIN_H);
        float y = UDpi.toPixel(TOP_Y);

        UDrawManager.getInstance().init();

        // 復元元のファイル選択
        // button
        boolean enableFlag = true;
        String title = UResourceManager.getStringById(R.string.restore_from_file);
        mRestoreButton = new UButtonText(this, UButtonType.Press, ButtonIdRestoreFromFile,
                DRAW_PRIORITY, title,
                UDpi.toPixel(MARGIN_H), y, width - UDpi.toPixel(MARGIN_H) * 2, 0,
                UDpi.toPixel(TEXT_SIZE), UColor.BLACK, Color.LTGRAY);
        mRestoreButton.setEnabled(enableFlag);
        mRestoreButton.addToDrawManager();

        y += mRestoreButton.getHeight() + UDpi.toPixel(MARGIN_V);

        // ListView
        int listViewH = height - ((int)y + UDpi.toPixel(MARGIN_V));
        mListView = new ListViewBackup(this, ListViewBackup.ListViewType.Restore,
                DRAW_PRIORITY, x, y,
                width - UDpi.toPixel(MARGIN_H) * 2, listViewH, 0);
        mListView.setFrameColor(Color.BLACK);
        mListView.addToDrawManager();
    }

    /**
     * ファイルを選択するためのダイアログを表示する
     */
    private void getFilePath() {
        File mPath = UUtil.getPath(mContext, FilePathType.ExternalDocument);
        mFileDialog = new FileDialog((Activity)mContext, mPath, ".bin");

        // ファイルを選択
        mFileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                if (file != null) {
                    showRestoreConfirmDialog(file);
                }
            }
        });

        mFileDialog.showDialog();
    }

    /**
     * 選択したファイルから復元するかの確認ダイアログを表示する
     */
    private void showRestoreConfirmDialog(File file) {
        String fileInfo = BackupManager.getInstance().getBackupInfo(file);
        if (mDialog != null) {
            mDialog.closeDialog();
        }
        // Dialog
        mDialog = UDialogWindow.createInstance(this, this,
                UDialogWindow.ButtonDir.Horizontal, mParentView.getWidth(), mParentView.getHeight());
        mDialog.addToDrawManager();

        if (fileInfo != null) {
            mRestoreFile = file;

            // 復元確認ダイアログの表示
            mDialog.setTitle(mContext.getString(R.string.confirm_restore));
            mDialog.addTextView(fileInfo + "\n\n", UAlignment.CenterX, true, false, UDpi.toPixel(TEXT_SIZE_S), TEXT_COLOR, 0);
            mDialog.addButton(ButtonIdRestoreFromFileOK, "OK", Color.BLACK, Color.WHITE);
            mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
        } else {
            // ファイルから情報を取得できなかった
            mDialog.setTitle(mContext.getString(R.string.failed_restore));
            mDialog.addCloseButton("OK", TEXT_COLOR, 0);
        }
        mParentView.invalidate();
    }

    /**
     * 復元確認ダイアログを表示する（２回目の確認用)
     */
    private void confirmRestore() {
        if (mDialog != null) {
            mDialog.closeDialog();
        }

        // Dialog
        mDialog = UDialogWindow.createInstance(this, this,
                UDialogWindow.ButtonDir.Horizontal, mParentView.getWidth(), mParentView.getHeight());
        mDialog.addToDrawManager();

        // 復元確認ダイアログの表示
        mDialog.setTitle(mContext.getString(R.string.confirm_restore2));
        mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
        mDialog.addButton(ButtonIdRestoreOK2, "OK", Color.BLACK, Color.WHITE);
    }
    /**
     * 復元を行う
     * @return 結果(成功/失敗)
     */
    private boolean doRestore(File file) {
        boolean ret = BackupManager.getInstance().loadBackup(file);

        String title = UResourceManager.getStringById( ret ? R.string.succeed_restore : R.string.failed_restore);

        // ダイアログを表示
        if (mDialog != null) {
            mDialog.closeDialog();
        }
        mDialog = UDialogWindow.createInstance(this, this,
                UDialogWindow.ButtonDir.Horizontal, mParentView.getWidth(), mParentView.getHeight());
        mDialog.addToDrawManager();
        mDialog.setTitle(title);
        mDialog.addCloseButton("OK", Color.BLACK, Color.WHITE);

        return ret;
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
        switch (id) {
            case ButtonIdRestoreFromFile: {
                getFilePath();
            }
            break;
            case ButtonIdRestoreFromFileOK: {
                doRestore(mRestoreFile);
            }
                break;
            case ButtonIdRestoreOK1: {
                confirmRestore();
            }
                break;
            case ButtonIdRestoreOK2: {
                File file = BackupManager.getManualBackupFile(mBackupItem.getBackup().getId());
                doRestore(file);
            }
                break;
            case ButtonIdNotFoundOK: {
                mDialog.closeDialog();
                // バックアップ情報を削除する
                RealmManager.getBackupFileDao().clearOne(mBackupItem.getBackup().getId());
                mBackupItem.setText( UResourceManager.getStringById(R.string.empty) );
            }
                break;
        }
        return false;
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

        if (backup.isEnabled()) {
            // show confirmation dialog 1

            mBackupItem = backupItem;

            // バックアップファイルの有無を確認（手動で消されていることもありえる）
            File file = new File(backup.getFilePath());
            if (file.exists() == false) {
                // ファイルが見つからなかった
                mDialog = UDialogWindow.createInstance(this, this,
                        UDialogWindow.ButtonDir.Horizontal, width, mParentView.getHeight());
                mDialog.addToDrawManager();
                mDialog.setTitle(mContext.getString(R.string.backup_not_found));
                mDialog.addButton(ButtonIdNotFoundOK, "OK", Color.BLACK, Color.WHITE);
            } else {
                // ファイルがあるので復元確認ダイアログを表示する
                mDialog = UDialogWindow.createInstance(this, this,
                        UDialogWindow.ButtonDir.Horizontal, width, mParentView.getHeight());
                mDialog.addToDrawManager();
                mDialog.setTitle(mContext.getString(R.string.confirm_restore));
                mDialog.addButton(ButtonIdRestoreOK1, "OK", Color.BLACK, Color.WHITE);
                mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
            }
        }
    }
    /**
     * 項目のボタンがクリックされた
     */
    public void ListItemButtonClicked(UListItem item, int buttonId){

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
