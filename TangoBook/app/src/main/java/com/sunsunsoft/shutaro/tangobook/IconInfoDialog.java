package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * ダイアログに表示する項目
 */
class IconInfoItem {
    public UTextView title;
    public UTextView body;
}

/**
 * アイコン情報ダイアログのアクションアイコン
 */
enum ActionIcons{
    Open(101, R.drawable.open, R.string.open),
    Edit(102, R.drawable.edit, R.string.edit),
    MoveToTrash(103, R.drawable.trash, R.string.trash),
    Copy(104, R.drawable.copy, R.string.copy),
    Favorite(105, R.drawable.favorites, R.string.learned),
    CleanUp(110, R.drawable.trash2, R.string.clean_up),
    OpenTrash(111, R.drawable.trash2, R.string.open),
    Return(201, R.drawable.return1, R.string.return_to_home),
    Delete(202, R.drawable.trash2, R.string.delete),
    Study(301, R.drawable.play, R.string.study)
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

    // Book Study
    public static List<ActionIcons> getBookStudyIcons() {
        LinkedList<ActionIcons> list = new LinkedList<>();
        list.add(Study);
        list.add(Open);
        return list;
    }

    // Trash
    public static List<ActionIcons> getTrashIcons() {
        LinkedList<ActionIcons> list = new LinkedList<>();
        list.add(OpenTrash);
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
    private int titleId;

    ActionIcons(int id, int imageId, int titleId) {
        this.id = id;
        this.imageId = imageId;
        this.titleId = titleId;
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

    public String getTitle(Context context) {
        return context.getString(titleId);
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


/**
 * アイコンをクリックしたときに表示されるダイアログ
 */
abstract public class IconInfoDialog extends UWindow{
    /**
     * Consts
     */
    protected static final int FRAME_WIDTH = 4;
    protected static final int FRAME_COLOR = Color.rgb(120,120,120);
    protected static final int TOP_ITEM_Y = 20;

    protected static final int MARGIN_H = 50;
    protected static final int MARGIN_V = 50;
    protected static final int MARGIN_V_S = 20;

    /**
     * Member Variables
     */
    protected View mParentView;
    protected Context mContext;
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
        mContext = parentView.getContext();
        mIconInfoCallbacks = iconInfoCallbacks;
        mIcon = icon;
    }


    /**
     * Methods
     */

    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (super.touchEvent(vt, offset)) {
            return true;
        }

        return false;
    }
    /**
     * Callbacks
     */
}
