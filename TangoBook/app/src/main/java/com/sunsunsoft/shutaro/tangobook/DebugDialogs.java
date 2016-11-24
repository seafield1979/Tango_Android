package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Color;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * デバッグ用のダイアログを管理するクラス
 *
 * 機能
 *  デバッグダイアログの表示(showDialog)
 *  ダイアログのボタンが押された時の処理(UButtonCallbacksのメソッド)
 *
 */

public class DebugDialogs implements UButtonCallbacks, UDialogCallbacks {
    enum DialogType {
        SelectDao,
        DeleteDao,
    }

    // buttonIds
    enum DialogButtons {
        SelectCard(101),
        SelectBook(102),
        SelectBox(103),
        SelectItemPos(104),

        DeleteCardAll(201),
        DeleteBookAll(202),
        DeleteBoxAll(203),
        DeleteItemPosAll(204),
        None(0)
        ;

        private final int id;

        DialogButtons(final int id) {
            this.id = id;
        }

        // リストを返す
        // Select
        static List<DialogButtons> selectValues() {
            LinkedList<DialogButtons> ids = new LinkedList<>();
            ids.add(SelectCard);
            ids.add(SelectBook);
            ids.add(SelectBox);
            ids.add(SelectItemPos);
            return ids;
        }
        // Delete
        static List<DialogButtons> deleteValues() {
            LinkedList<DialogButtons> ids = new LinkedList<>();
            ids.add(DeleteCardAll);
            ids.add(DeleteBookAll);
            ids.add(DeleteBoxAll);
            ids.add(DeleteItemPosAll);
            return ids;
        }

        // intを返す
        public int getInt() {
            return this.id;
        }
        // int を enumに変換する
        public static DialogButtons toEnum(int val) {
            for (DialogButtons id : values()) {
                if (id.getInt() == val) {
                    return id;
                }
            }
            return None;
        }
    }

    /**
     * Consts
     */
    public static final String TAG = "DebugDialogs";



    /**
     * Member Variables
     */
    private UDialogWindow mDialog;
    private View mParentView;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public DebugDialogs(View parentView) {
        mParentView = parentView;
    }

    /**
     * Methods
     */
    public void showDialog(DialogType type) {
        if (mDialog != null) {
            mDialog.closeDialog();
            mDialog = null;
        }

        // Daoデバッグ用のダイアログを表示
        mDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                this,
                this,
                UDialogWindow.ButtonDir.Vertical,
                false,
                mParentView.getWidth(), mParentView.getHeight(),
                Color.rgb(200,100,100), Color.WHITE);

        switch(type) {
            case SelectDao:
            {
                // タイトル
                mDialog.setTitle("Select Dao");
                // ボタンを追加
                for (DialogButtons id : DialogButtons.selectValues()) {
                    mDialog.addButton(id.getInt(), id.toString(), Color.WHITE, Color.rgb(150, 80,
                            80));


                }
                mDialog.addCloseButton(null);
                // 描画マネージャに登録
                mDialog.setDrawPriority(DrawPriority.Dialog.p());
            }
            break;

            case DeleteDao:
            {
                // タイトル
                mDialog.setTitle("Delete Dao");
                // ボタンを追加
                for (DialogButtons id : DialogButtons.deleteValues()) {
                    mDialog.addButton(id.getInt(), id.toString(), Color.WHITE, Color.rgb(150, 80,
                            80));

                }
                mDialog.addCloseButton(null);
                // 描画マネージャに登録
                mDialog.setDrawPriority(DrawPriority.Dialog.p());
            }
                break;
        }

    }

    /**
     * Callbacks
     */

    /**
     * UButtonCallbacks
     */
    public void UButtonClick(int id) {
        switch (DialogButtons.toEnum(id)) {
            case SelectCard:
                RealmManager.getCardDao().selectAll();
                break;
            case SelectBook:
                RealmManager.getBookDao().selectAll();
                break;
            case SelectBox:
                RealmManager.getBoxDao().selectAll();
                break;
            case SelectItemPos:
                RealmManager.getItemPosDao().selectAll();
                break;

            case DeleteCardAll:
                RealmManager.getCardDao().deleteAll();
                break;
            case DeleteBookAll:
                RealmManager.getBookDao().deleteAll();
                break;
            case DeleteBoxAll:
                RealmManager.getBoxDao().deleteAll();
                break;
            case DeleteItemPosAll:
                RealmManager.getItemPosDao().deleteAll();
                break;
        }
    }

    public void UButtonLongClick(int id) {
    }

    /**
     * DialogCallbacks
     */
    public void dialogClosed(UDialogWindow dialog) {
        if (mDialog == dialog) {
            mDialog = null;
        }
    }
}
