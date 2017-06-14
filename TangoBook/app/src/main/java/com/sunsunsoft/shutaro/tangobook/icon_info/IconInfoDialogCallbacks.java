package com.sunsunsoft.shutaro.tangobook.icon_info;

/**
 * Created by shutaro on 2017/06/14.
 */

import com.sunsunsoft.shutaro.tangobook.icon.UIcon;

/**
 * IconInfoDialogのコールバック
 */
public interface IconInfoDialogCallbacks {
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
     * Book の学習開始
     * @param icon
     */
    void IconInfoStudy(UIcon icon);

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