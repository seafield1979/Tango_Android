package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

/**
 * Created by shutaro on 2017/01/29.
 *
 * PageViewStudy~系クラスの親クラス
 */

abstract  class PageViewStudy extends UPageView
    implements UButtonCallbacks, UDialogCallbacks
{
    /**
     * Constants
     */
    public static final int ButtonIdExit = 200;
    public static final int ButtonIdExitOk = 201;


    /**
     * Member variables
     */
    // 終了確認ダイアログ
    protected UDialogWindow mConfirmDialog;
    protected boolean isCloseOk;


    /**
     * Constructor
     */
    public PageViewStudy(Context context, View parentView, String title) {
        super(context, parentView, title);
    }


    /**
     * ページ終了確認ダイアログを表示する
     */
    private void showExitConfirm() {
        if (mConfirmDialog == null) {
            isCloseOk = false;

            mConfirmDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                    this, this,
                    UDialogWindow.ButtonDir.Horizontal, UDialogWindow.DialogPosType.Center,
                    true, mParentView.getWidth(), mParentView.getHeight(),
                    Color.BLACK, Color.LTGRAY);
            mConfirmDialog.addToDrawManager();
            mConfirmDialog.setTitle(mContext.getString(R.string.confirm_exit));
            mConfirmDialog.addButton(ButtonIdExitOk, "OK", Color.BLACK, Color.WHITE);
            mConfirmDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
        }
    }

    /**
     * ソフトウェアキーの戻るボタンを押したときの処理
     * @return
     */
    public boolean onBackKeyDown() {
        showExitConfirm();
        return true;
    }

    /**
     * Callbacks
     */

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        switch(id) {
            case ButtonIdExit:
                // 終了ボタンを押したら確認用のモーダルダイアログを表示
                showExitConfirm();
                break;
            case ButtonIdExitOk:
                // 終了
                isCloseOk = true;
                mConfirmDialog.startClosing();
                break;
        }
        return false;
    }

    /**
     * UDialogCallbacks
     */
    public void dialogClosed(UDialogWindow dialog) {
        if (isCloseOk) {
            // 終了して前のページに戻る
            PageViewManager.getInstance().popPage();
        }
        if (dialog == mConfirmDialog) {
            mConfirmDialog = null;
        }
    }

}
