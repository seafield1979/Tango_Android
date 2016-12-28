package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Point;
import android.view.View;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

// アイコンの挿入位置
enum AddPos {
    SrcNext,    // コピー元の次
    Top,        // リストの先頭
    Tail        // リストの末尾
}

/**
 * IconWindowに表示するアイコンを管理するクラス
 *
 * Rect判定の高速化のためにいくつかのアイコンをまとめたブロックのRectを作成し、個々のアイコンのRect判定前に
 * ブロックのRectと判定を行う
 */
public class UIconManager implements UIconCallbacks{
    /**
     * Enums
     */
    enum SortMode {
        TitleAsc,       // タイトル文字昇順(カードはWordA,単語帳はName)
        TitleDesc,      // タイトル文字降順
        CreateDateAsc,  // 更新日時 昇順
        CreateDateDesc  // 更新日時 降順
    }

    /**
     * Consts
     */
    public static final String TAG = "UIconManager";

    /**
     * Member Variables
     */
    private UIconWindow mParentWindow;
    private UIconCallbacks mIconCallbacks;
    private LinkedList<UIcon> icons;
    private UIconsBlockManager mBlockManager;

    private UIcon selectedIcon;
    private UIcon dropedIcon;       // アイコンをドロップ中のアイコン
    private UIcon mTrashIcon;

    /**
     * Get/Set
     */
    public List<UIcon> getIcons() {
        return icons;
    }

    public void setIcons(LinkedList<UIcon> icons) {
        this.icons = icons;
    }

    public UIconWindow getParentWindow() {
        return mParentWindow;
    }

    public UIcon getSelectedIcon() {
        return selectedIcon;
    }

    public void setSelectedIcon(UIcon selectedIcon) {
        this.selectedIcon = selectedIcon;
    }

    public UIcon getDropedIcon() {
        return dropedIcon;
    }

    public void setDropedIcon(UIcon dropedIcon) {
        // 全てのアイコンのdropフラグを解除
        for (UIcon icon : icons) {
            icon.isDroping = false;
        }
        if (dropedIcon != null) {
            this.dropedIcon = dropedIcon;
            dropedIcon.isDroping = true;
        }
    }

    public UIcon getTrashIcon() {
        return mTrashIcon;
    }

    /**
     * チェックされたアイコンのリストを取得する
     * @return
     */
    public List<UIcon> getCheckedIcons() {
        LinkedList<UIcon> checkedIcons = new LinkedList<>();

        for (UIcon icon : icons) {
            if (icon.isChecked) {
                checkedIcons.add(icon);
            }
        }
        return checkedIcons;
    }

    /**
     * Constructor
     */
    public UIconsBlockManager getBlockManager() {
        return mBlockManager;
    }

    public static UIconManager createInstance(UIconWindow parentWindow, UIconCallbacks iconCallbacks) {
        UIconManager instance = new UIconManager();
        instance.mParentWindow = parentWindow;
        instance.mIconCallbacks = iconCallbacks;
        instance.icons = new LinkedList<>();
        instance.mBlockManager = UIconsBlockManager.createInstance(instance.icons);
        return instance;
    }

    /**
     * 指定タイプのアイコンを作成してから追加
     * @param copySrc  コピー元のIcon
     * @param addPos
     * @return
     */
    public UIcon copyIcon(UIcon copySrc, AddPos addPos) {
        UIcon icon = null;
        boolean addItemPos = (addPos != null) ? true : false;

        switch (copySrc.getType()) {
            case Card: {
                TangoCard card = TangoCard.copyCard((TangoCard)copySrc.getTangoItem());
                RealmManager.getCardDao().addOne(card, TangoParentType.Home, 0, addItemPos);
                icon = new IconCard(card, mParentWindow, this);
            }
            break;
            case Book:
            {
                TangoBook book = TangoBook.copyBook((TangoBook)copySrc.getTangoItem());

                RealmManager.getBookDao().addOne(book, addItemPos);
                icon = new IconBook(book, mParentWindow, this);

            }
            break;
        }
        if (icon == null) return null;

        // リストに追加
        if (addPos != null) {
            switch (addPos) {
                case SrcNext: {
                    int pos = icons.indexOf(copySrc);
                    if (pos != -1) {
                        icons.add(pos + 1, icon);
                        icon.setPos(copySrc.pos);
                    }
                }
                    break;
                case Top:
                    icons.push(icon);
                    break;
                case Tail: {
                    UIcon lastIcon = icons.getLast();

                    icons.add(icon);

                    // 出現位置は最後のアイコン
                    if (lastIcon != null) {
                        icon.setPos(lastIcon.getPos());
                    }
                }
                    break;
            }
        }

        return icon;
    }

    /**
     * アイコンを追加する
     * @param type
     * @param addPos
     * @return
     */
    public UIcon addNewIcon(IconType type, TangoParentType parentType,
                            int parentId, AddPos addPos) {
        UIcon icon = null;
        switch (type) {
            case Card: {
                TangoCard card = TangoCard.createDummyCard();
                RealmManager.getCardDao().addOne(card, parentType, parentId, true);
                icon = new IconCard(card, mParentWindow, this);
            }
                break;
            case Book:
            {
                TangoBook book = TangoBook.createDummyBook();
                RealmManager.getBookDao().addOne(book, true);
                icon = new IconBook(book, mParentWindow, this);
            }
                break;
            case Trash:
            {
                mTrashIcon = icon = new IconTrash(mParentWindow, this);
            }
                break;
        }
        if (icon == null) return null;

        // リストに追加
        if (addPos == AddPos.Top) {
            icons.push(icon);
        } else {
            UIcon lastIcon = null;
            if (icons.size() > 0) {
                lastIcon = icons.getLast();
            }
            icons.add(icon);

            // 出現位置は最後のアイコン
            if (lastIcon != null) {
                icon.setPos(lastIcon.getPos());
            }
        }

        return icon;
    }

    /**
     * TangoItemを元にアイコンを追加する
     * @param item
     * @return
     */
    public UIcon addIcon(TangoItem item, AddPos addPos) {
        UIcon icon = null;

        switch(item.getItemType()) {
            case Card:
                if (item instanceof  TangoCard) {
                    TangoCard card = (TangoCard) item;
                    icon = new IconCard(card, mParentWindow, this);
                }
                break;
            case Book:
                if (item instanceof  TangoBook) {
                    TangoBook book = (TangoBook) item;
                    icon = new IconBook(book, mParentWindow, this);
                }
                break;
        }
        if (icon == null) return null;

        if (addPos == AddPos.Top) {
            icons.push(icon);

        } else {
            icons.add(icon);
            // 出現位置は最後のアイコン
            UIcon lastIcon = icons.getLast();
            if (lastIcon != null) {
                icon.setPos(lastIcon.getPos());
            }
        }
        return icon;
    }

    /**
     * すでに作成済みのアイコンを追加
     * ※べつのWindowにアイコンを移動するのに使用する
     * @param icon
     * @return
     */
    public boolean addIcon(UIcon icon) {
        // すでに追加されている場合は追加しない
        if (!icons.contains(icon)) {
            icons.add(icon);
            return true;
        }
        return false;
    }

    /**
     * アイコンを削除(データベースからも削除）
     * @param icon
     */
    public void removeIcon(UIcon icon) {
        TangoItem item = icon.getTangoItem();
        if (item == null) return;

        switch(icon.getType()) {
            case Card:
                RealmManager.getCardDao().deleteById(item.getId());
                break;
            case Book:
                RealmManager.getBookDao().deleteById(item.getId());
                break;
        }
        RealmManager.getItemPosDao().deleteItem(icon.getTangoItem());
        icons.remove(icon);
    }

    /**
     * UIconのリストからTangoItemのリストを作成する
     * @return
     */
    public List<TangoItem> getTangoItems() {
        LinkedList<TangoItem> list = new LinkedList<>();
        for (UIcon icon : icons) {
            if (icon.getTangoItem() != null) {
                list.add(icon.getTangoItem());
            }
        }
        return list;
    }

    /**
     * アイコンを内包するRectを求める
     * アイコンの座標確定時に呼ぶ
     */
    public void updateBlockRect() {
        mBlockManager.update();
    }

    /**
     * 指定座標下にあるアイコンを取得する
     * @param pos
     * @param exceptIcons
     * @return
     */
    public UIcon getOverlappedIcon(Point pos, List<UIcon> exceptIcons) {
        return mBlockManager.getOverlapedIcon(pos, exceptIcons);
    }

    /**
     * ソートする
     * @param mode
     */
    public void sortWithMode(SortMode mode) {
        UIcon[] _icons = getIcons().toArray(new UIcon[0]);
        final SortMode _mode = mode;

        // _icons を SortMode の方法でソートする
        Arrays.sort(_icons, new Comparator<UIcon>() {
            public int compare(UIcon icon1, UIcon icon2) {
                TangoItem item1 = icon1.getTangoItem();
                TangoItem item2 = icon2.getTangoItem();
                if (item1 == null || item2 == null) {
                    return 0;
                }
                switch(_mode) {
                    case TitleAsc:       // タイトル文字昇順(カードはWordA,単語帳はName)
                        return item1.getTitle().compareTo (
                                item2.getTitle());
                    case TitleDesc:      // タイトル文字降順
                        return item2.getTitle().compareTo(
                                item1.getTitle());
                    case CreateDateAsc:  // 作成日時 昇順
                        if (item1.getCreateTime() == null || item2.getCreateTime() == null)
                            break;
                        return item1.getCreateTime().compareTo(
                                item2.getCreateTime());
                    case CreateDateDesc:  // 作成日時 降順
                        if (item1.getCreateTime() == null || item2.getCreateTime() == null)
                            break;
                        return item2.getCreateTime().compareTo(
                                item1.getCreateTime());
                }
                return 0;
            }
        });

        // ソート済みの新しいアイコンリストを作成する
        icons.clear();

        int pos = 1;
        for (UIcon icon : _icons) {
            icons.add(icon);

            if (icon.getTangoItem() == null) continue;

            // DB更新用にItemPosを設定しておく
            icon.getTangoItem().setPos(pos);
            pos++;
        }
        // DBの位置情報を更新
        RealmManager.getItemPosDao().updateAll(getTangoItems(),
                mParentWindow.getParentType(),
                mParentWindow.getParentId());
    }


    /**
     * UIconCallbacks
     */
    public void iconClicked(UIcon icon) {
        if (getParentWindow().getType() == UIconWindow.WindowType.Home) {
            selectedIcon = icon;
        }
        if (mIconCallbacks != null) {
            mIconCallbacks.iconClicked(icon);
        }
    }
    public void longClickIcon(UIcon icon) {
        if (mIconCallbacks != null) {
            mIconCallbacks.iconClicked(icon);
        }
    }

    public  void iconDroped(UIcon icon) {

    }
}
