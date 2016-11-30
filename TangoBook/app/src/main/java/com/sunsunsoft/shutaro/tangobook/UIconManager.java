package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

// アイコンの挿入位置
enum AddPos {
    Top,
    Tail
}

/**
 * IconWindowに表示するアイコンを管理するクラス
 *
 * Rect判定の高速化のためにいくつかのアイコンをまとめたブロックのRectを作成し、個々のアイコンのRect判定前に
 * ブロックのRectと判定を行う
 */
public class UIconManager implements UIconCallbacks{
    /**
     * Consts
     */

    /**
     * Member Variables
     */
    private View mParentView;
    private UIconWindow mParentWindow;
    private UIconCallbacks mIconCallbacks;
    private LinkedList<UIcon> icons;
    private UIconsBlockManager mBlockManager;

    private UIcon selectedIcon;
    private UIcon dropedIcon;       // アイコンをドロップ中のアイコン

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

    public static UIconManager createInstance(View parentView,
                                              UIconWindow parentWindow, UIconCallbacks iconCallbacks) {
        UIconManager instance = new UIconManager();
        instance.mParentView = parentView;
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
        switch (copySrc.getType()) {
            case Card: {
                TangoCard card = (TangoCard)copySrc.getTangoItem();
                RealmManager.getCardDao().addOne(card);
                TangoItemPos itemPos = RealmManager.getItemPosDao().addOne(card, TangoParentType
                        .Home, 0);
                card.setItemPos(itemPos);
                icon = new IconCard(card, mParentView, mParentWindow, this);
            }
            break;
            case Book:
            {
                TangoBook book = (TangoBook)copySrc.getTangoItem();

                RealmManager.getBookDao().addOne(book);
                TangoItemPos itemPos = RealmManager.getItemPosDao().addOne(book, TangoParentType.Home, 0);
                book.setItemPos(itemPos);
                icon = new IconBook(book, mParentView, mParentWindow, this);

            }
            break;
        }
        if (icon == null) return null;

        // リストに追加
        if (addPos != null) {
            if (addPos == AddPos.Top) {
                icons.push(icon);
            } else {
                UIcon lastIcon = icons.getLast();

                icons.add(icon);

                // 出現位置は最後のアイコン
                if (lastIcon != null) {
                    icon.setPos(lastIcon.getPos());
                }
            }
        }
        else {
            int pos = icons.indexOf(copySrc);
            if (pos != -1) {
                icons.add(pos + 1, icon);
                icon.setPos(copySrc.pos);
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
    public UIcon addNewIcon(IconType type, AddPos addPos) {
        UIcon icon = null;
        switch (type) {
            case Card: {
                TangoCard card = TangoCard.createDummyCard();
                RealmManager.getCardDao().addOne(card);
                TangoItemPos itemPos = RealmManager.getItemPosDao().addOne(card, TangoParentType
                        .Home, 0);
                card.setItemPos(itemPos);
                icon = new IconCard(card, mParentView, mParentWindow, this);
            }
                break;
            case Book:
            {
                TangoBook book = TangoBook.createDummyBook();
                RealmManager.getBookDao().addOne(book);
                TangoItemPos itemPos = RealmManager.getItemPosDao().addOne(book, TangoParentType.Home, 0);
                book.setItemPos(itemPos);
                icon = new IconBook(book, mParentView, mParentWindow, this);
            }
                break;
            case Trash:
            {
                icon = new IconTrash(mParentView, mParentWindow, this);
            }
                break;
        }
        if (icon == null) return null;

        // リストに追加
        if (addPos == AddPos.Top) {
            icons.push(icon);
        } else {
            UIcon lastIcon = icons.getLast();

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
                    icon = new IconCard(card, mParentView, mParentWindow, this);
                }
                break;
            case Book:
                if (item instanceof  TangoBook) {
                    TangoBook book = (TangoBook) item;
                    icon = new IconBook(book, mParentView, mParentWindow, this);
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
     * UIconCallbacks
     */
    public void clickIcon(UIcon icon) {
        if (getParentWindow().getType() == UIconWindow.WindowType.Home) {
            selectedIcon = icon;
        }
        if (mIconCallbacks != null) {
            mIconCallbacks.clickIcon(icon);
        }
    }
    public void longClickIcon(UIcon icon) {
        if (mIconCallbacks != null) {
            mIconCallbacks.clickIcon(icon);
        }
    }

    public  void dropToIcon(UIcon icon) {

    }
}
