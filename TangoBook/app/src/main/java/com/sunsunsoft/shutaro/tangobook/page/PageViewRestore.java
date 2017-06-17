package com.sunsunsoft.shutaro.tangobook.page;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;
import com.sunsunsoft.shutaro.tangobook.listview.ListViewBackupDB;
import com.sunsunsoft.shutaro.tangobook.save.BackupFileType;
import com.sunsunsoft.shutaro.tangobook.save.XmlManager;
import com.sunsunsoft.shutaro.tangobook.util.FileDialog;
import com.sunsunsoft.shutaro.tangobook.util.FilePathType;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.util.UUtil;
import com.sunsunsoft.shutaro.tangobook.uview.*;

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
    private static final int ButtonIdRestoreFromFile = 100;     // 選択したファイルから復元ボタンを押した
    private static final int ButtonIdRestoreFromFileOK = 101;   // 選択したファイルから復元するかどうかでOKを選択
    private static final int ButtonIdRestoreOK1 = 102;          // 復元確認1でOKを選択
    private static final int ButtonIdRestoreOK2 = 103;          // 復元確認2でOKを選択

    /**
     * Member variables
     */
    private Context mContext;
    // バックアップタイトル
    private UTextView mAutoBackupTitle, mManualBackupTitle;

    private UButtonText mRestoreButton;     // xmlファイル選択で復元ボタン
    private ListViewBackupDB mListView;

    // Dialog
    private UDialogWindow mDialog;

    private FileDialog fileDialog;      // ファイルを選択するモーダルダイアログ
    private File mRestoreFile;          // 復元元のxmlファイル

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

        float x = MARGIN_H;
        float y = TOP_Y;

        UDrawManager.getInstance().init();

        // 復元元のxmlファイル選択
        // button
        boolean enableFlag = true;
        String title = UResourceManager.getStringById(R.string.restore_from_file);
        mRestoreButton = new UButtonText(this, UButtonType.Press, ButtonIdRestoreFromFile,
                DRAW_PRIORITY, title,
                MARGIN_H, y, width - MARGIN_H * 2, 0, TEXT_SIZE, UColor.BLACK, Color.LTGRAY);
        mRestoreButton.setEnabled(enableFlag);
        mRestoreButton.addToDrawManager();

        y += mRestoreButton.getHeight() + MARGIN_V;

        // ListView
        int listViewH = height - ((int)y + MARGIN_V);
        mListView = new ListViewBackupDB(this, DRAW_PRIORITY, x, y,
                width - MARGIN_H * 2, listViewH, 0);
        mListView.setFrameColor(Color.BLACK);
        mListView.addToDrawManager();

    }

    /**
     * xmlファイルを選択するためのダイアログを表示する
     */
    private void getFilePath() {
        File mPath = UUtil.getPath(mContext, FilePathType.ExternalDocument);
//        File mPath = new File(Environment.getExternalStorageDirectory() + "/");
        fileDialog = new FileDialog((Activity)mContext, mPath, ".xml");

        // ファイルを選択
        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                if (file != null) {
                    showRestoreConfirmDialog(file);
                }
            }
        });

        fileDialog.showDialog();
    }

    /**
     * 選択したファイルから復元するかの確認ダイアログを表示する
     */
    private void showRestoreConfirmDialog(File file) {
        String xmlInfo = XmlManager.getInstance().getXmlInfo(file);

        if (mDialog != null) {
            mDialog.closeDialog();
        }
        // Dialog
        mDialog = UDialogWindow.createInstance(this, this,
                UDialogWindow.ButtonDir.Horizontal, mParentView.getWidth(), mParentView.getHeight());
        mDialog.addToDrawManager();

        if (xmlInfo != null) {
            mRestoreFile = file;

            // 復元確認ダイアログの表示
            mDialog.setTitle(mContext.getString(R.string.restore_confirm));
            mDialog.addTextView(xmlInfo + "\n\n", UAlignment.CenterX, true, false, TEXT_SIZE_S, TEXT_COLOR, 0);
            mDialog.addButton(ButtonIdRestoreFromFileOK, "OK", Color.BLACK, Color.WHITE);
            mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
        } else {
            // xmlファイルから情報を取得できなかった
            mDialog.setTitle(mContext.getString(R.string.failed_restore));
            mDialog.addCloseButton("OK", TEXT_COLOR, 0);
        }
        mParentView.invalidate();
    }

    /**
     * 復元を行う
     * @return 結果(成功/失敗)
     */
    private boolean doRestore() {
        boolean ret = XmlManager.getInstance().loadXml(mRestoreFile);

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
                doRestore();
            }
                break;
            case ButtonIdRestoreOK1: {

            }
                break;
            case ButtonIdRestoreOK2: {

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
