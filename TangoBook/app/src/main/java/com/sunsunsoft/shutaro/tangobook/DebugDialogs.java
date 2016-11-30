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
        Icons,
        DrawManager
    }

    // buttonIds
    enum DialogButtons {
        // SelectDao のボタン
        SelectCard(101),
        SelectBook(102),
        SelectBox(103),
        SelectItemPos(104),

        // DeleteDao のボタン
        DeleteCardAll(201),
        DeleteBookAll(202),
        DeleteBoxAll(203),
        DeleteItemPosAll(204),

        // Iconsのボタン
        IconsShowAll(301),

        // UDrawManagerのボタン
        DrawManagerShowAll(401),
        DrawManagerShowAllReverse(402),
        DrawManagerShowDrawables(403),
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

        // Icons
        static List<DialogButtons> iconsValues() {
            LinkedList<DialogButtons> ids = new LinkedList<>();
            ids.add(IconsShowAll);
            return ids;
        }

        // DrawManager
        static List<DialogButtons> drawManagerValues() {
            LinkedList<DialogButtons> ids = new LinkedList<>();
            ids.add(DrawManagerShowAll);
            ids.add(DrawManagerShowAllReverse);
            ids.add(DrawManagerShowDrawables);
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
                this, this,
                UDialogWindow.ButtonDir.Vertical, UDialogWindow.DialogPosType.Center,
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
            case Icons:
            {
                // タイトル
                mDialog.setTitle("Icons");
                // ボタンを追加
                for (DialogButtons id : DialogButtons.iconsValues()) {
                    mDialog.addButton(id.getInt(), id.toString(), Color.WHITE, Color.rgb(150, 80,
                            80));

                }
                mDialog.addCloseButton(null);
                // 描画マネージャに登録
                mDialog.setDrawPriority(DrawPriority.Dialog.p());
            }
                break;
            case DrawManager:

                // タイトル
                mDialog.setTitle("DrawManager");
                // ボタンを追加
                for (DialogButtons id : DialogButtons.drawManagerValues()) {
                    mDialog.addButton(id.getInt(), id.toString(), Color.WHITE, Color.rgb(150, 80,
                            80));
                }
                mDialog.addCloseButton(null);
                // 描画マネージャに登録
                mDialog.setDrawPriority(DrawPriority.Dialog.p());
                break;
        }

    }

    /**
     * Callbacks
     */

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClick(int id) {

        switch (DialogButtons.toEnum(id)) {
            // Dao
            case SelectCard:
                RealmManager.getCardDao().selectAll();
                return true;
            case SelectBook:
                RealmManager.getBookDao().selectAll();
                return true;
            case SelectItemPos:
                RealmManager.getItemPosDao().selectAll();
                return true;

            // Delete
            case DeleteCardAll:
                RealmManager.getCardDao().deleteAll();
                return true;
            case DeleteBookAll:
                RealmManager.getBookDao().deleteAll();
                return true;
            case DeleteItemPosAll:
                RealmManager.getItemPosDao().deleteAll();
                return true;

            // Icons
            case IconsShowAll:
                UIconWindows.getPublicInstance().showAllIconsInfo();
                return true;

            // DrawManager
            case DrawManagerShowAll:
                UDrawManager.getInstance().showAllList(true, false);
                break;
            case DrawManagerShowAllReverse:
                UDrawManager.getInstance().showAllList(false, false);
                break;
            case DrawManagerShowDrawables:
                UDrawManager.getInstance().showAllList(false, true);
                break;
        }
        return false;
    }

    public boolean UButtonLongClick(int id) {
        return false;
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
