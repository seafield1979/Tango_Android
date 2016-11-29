package com.sunsunsoft.shutaro.tangobook;

import android.view.View;


/**
 * アイコン情報ダイアログのアクションアイコン
 */
enum IconInfoAction {
    EditCard,
    Trash,
    CopyCard,
    Favorite
}

/**
 * IconInfoDialogのコールバック
 */
interface IconInfoDialogCallbacks {
    /**
     * ダイアログで表示しているアイコンの内容を編集
     * @param icon
     */
    void editIcon(UIcon icon);

    /**
     * ダイアログで表示しているアイコンをコピー
     * @param icon
     */
    void copyIcon(UIcon icon);

    /**
     * ダイアログで表示しているアイコンをゴミ箱に移動
     * @param icon
     */
    void throwIcon(UIcon icon);
}


/**
 * アイコンをクリックしたときに表示されるダイアログ
 */
abstract public class IconInfoDialog extends UWindow{
    /**
     * Consts
     */

    /**
     * Member Variables
     */
    protected View mParentView;
    protected IconInfoDialogCallbacks mIconInfoCallbacks;

    // ダイアログに情報を表示元のアイコン
    protected UIcon mIcon;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */


    public IconInfoDialog(View parentView,
                          IconInfoDialogCallbacks iconInfoCallbacks,
                          UWindowCallbacks windowCallbacks,
                          UIcon icon,
                          float x, float y,
                          int color)
    {
        // width, height はinit内で計算するのでここでは0を設定
        super(windowCallbacks, DrawPriority.Dialog.p(), x, y, 0, 0, color);

        mParentView = parentView;
        mIconInfoCallbacks = iconInfoCallbacks;
        mIcon = icon;
    }


    /**
     * Methods
     */
    /**
     * Callbacks
     */
}
