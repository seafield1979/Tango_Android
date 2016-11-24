package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Color;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * デバッグ用のダイアログを管理するクラス
 *
 * デバッグダイアログの表示
 * ダイアログのボタンが押された時の処理
 */

public class DebugDialogs implements UButtonCallbacks, UDialogCallbacks {
    enum DialogType {
        TestDao,
    }

    /**
     * Consts
     */
    public static final String TAG = "DebugDialogs";

    // buttonIds
    enum DialogButtons {
        SelectCard(101),
        SelectBook(102),
        SelectBox(103),
        SelectItemPos(104),
        None(0)
        ;

        private final int id;

        private DialogButtons(final int id) {
            this.id = id;
        }
        static List<DialogButtons> test1Values() {
            LinkedList<DialogButtons> ids = new LinkedList<>();
            ids.add(SelectCard);
            ids.add(SelectBook);
            ids.add(SelectBox);
            ids.add(SelectItemPos);
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

        switch(type) {
            case TestDao:
            {
                // Daoデバッグ用のダイアログを表示
                mDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                        this,
                        this,
                        UDialogWindow.ButtonDir.Vertical,
                        false,
                        mParentView.getWidth(), mParentView.getHeight(),
                        Color.rgb(200,100,100), Color.WHITE);
                for (DialogButtons id : DialogButtons.test1Values()) {
                    mDialog.addButton(id.getInt(), id.toString(), Color.WHITE, Color.rgb(150, 80,
                            80));

                }
                mDialog.addCloseButton(null);
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
