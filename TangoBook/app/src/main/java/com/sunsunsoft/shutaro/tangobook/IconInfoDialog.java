package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Color;
import android.graphics.PointF;
import android.view.View;

import java.util.LinkedList;
import java.util.List;


/**
 * アイコン情報ダイアログのアクションアイコン
 */
enum IconInfoAction {
    EditCard,
    Trash,
    CopyCard,
    Favorite
}

enum ActionIcons{
    Open(101, R.drawable.open, "open"),
    Edit(102, R.drawable.edit, "edit"),
    MoveToTrash(103, R.drawable.trash, "ゴミ箱"),
    Copy(104, R.drawable.copy, "copy"),
    Favorite(105, R.drawable.favorites, null),
    CleanUp(110, R.drawable.trash_empty, "空にする"),
    Return(201, R.drawable.return1, "元に戻す"),
    Delete(202, R.drawable.trash, "削除")
    ;

    // Card
    public static List<ActionIcons> getCardIcons() {
        LinkedList<ActionIcons> list = new LinkedList<>();
        list.add(Edit);
        list.add(Copy);
        list.add(MoveToTrash);
        list.add(Favorite);
        return list;
    }

    // Book
    public static List<ActionIcons> getBookIcons() {
        LinkedList<ActionIcons> list = new LinkedList<>();
        list.add(Open);
        list.add(Copy);
        list.add(MoveToTrash);
        list.add(Favorite);
        return list;
    }

    // Trash
    public static List<ActionIcons> getTrashIcons() {
        LinkedList<ActionIcons> list = new LinkedList<>();
        list.add(Open);
        list.add(CleanUp);
        return list;
    }

    // in Trash
    public static List<ActionIcons> getInTrashIcons() {
        LinkedList<ActionIcons> list = new LinkedList<>();
        list.add(Return);
        list.add(Delete);
        return list;
    }

    private int id;
    private int imageId;
    private String title;

    ActionIcons(int id, int imageId, String title) {
        this.id = id;
        this.imageId = imageId;
        this.title = title;
    }

    protected static ActionIcons toEnum(int id) {
        if (id >= values().length) {
            return Edit;
        }
        return values()[id];
    }

    /**
     * アイコン用の画像IDを取得
     */
    public int getImageId() {
        return imageId;
    }

    public String getTitle() {
        return title;
    }
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

    public boolean touchEvent(ViewTouch vt) {
        if (super.touchEvent(vt)) {
            return true;
        }
        return false;
    }
    /**
     * Callbacks
     */
}
