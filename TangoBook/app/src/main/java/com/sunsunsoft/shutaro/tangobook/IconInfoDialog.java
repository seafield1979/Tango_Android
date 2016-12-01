package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Color;
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
    void IconInfoEditIcon(UIcon icon);

    /**
     * アイコンをコピー
     * @param icon
     */
    void IconInfoCopyIcon(UIcon icon);

    /**
     * アイコンをゴミ箱に移動
     * @param icon
     */
    void IconInfoThrowIcon(UIcon icon);

    /**
     * アイコンを開く
     * @param icon
     */
    void IconInfoOpenIcon(UIcon icon);

    /**
     * コンテナタイプのアイコン以下をクリーンアップ(全削除)する
     * @param icon
     */
    void IconInfoCleanup(UIcon icon);

    /**
     * ゴミ箱内のアイコンを元に戻す
     * @param icon
     */
    void IconInfoReturnIcon(UIcon icon);

    /**
     * ゴミ箱内のアイコンを削除する
     * @param icon
     */
    void IconInfoDeleteIcon(UIcon icon);
}


/**
 * アイコンをクリックしたときに表示されるダイアログ
 */
abstract public class IconInfoDialog extends UWindow{
    /**
     * Consts
     */
    protected static final int FRAME_WIDTH = 4;
    protected static final int FRAME_COLOR = Color.rgb(120,120,120);


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
