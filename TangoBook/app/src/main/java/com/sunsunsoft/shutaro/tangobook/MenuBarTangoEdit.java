package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

/**
 * Created by shutaro on 2016/12/06.
 *
 * 単語帳編集ページで表示するメニューバー
 */

public class MenuBarTangoEdit extends UMenuBar {
    /**
     * Enums
     */
    enum MenuItemType {
        Top,
        Child,
        State
    }

    // メニューのID、画像ID、Topかどうかのフラグ
    enum MenuItemId {
        AddTop(MenuItemType.Top, R.drawable.add, R.string.add_item, false),
        AddCard(MenuItemType.Child, R.drawable.card, R.string.add_card, false),
        AddBook(MenuItemType.Child, R.drawable.cards, R.string.add_book, false),
        AddDummyCard(MenuItemType.Child, R.drawable.number_1, R.string.add_dummy_card, true),
        AddDummyBook(MenuItemType.Child, R.drawable.number_2, R.string.add_dummy_book, true),
        AddPresetBook(MenuItemType.Child, R.drawable.cards, R.string.add_preset, false),
        AddCsvBook(MenuItemType.Child, R.drawable.cards, R.string.add_csv, false),

        ;

        private MenuItemType type;
        private int imageId;
        private int stringId;
        private boolean forDebug;       // デバッグ用のアイテム

        MenuItemId(MenuItemType type, int imageId, int stringId, boolean forDebug) {
            this.imageId = imageId;
            this.type = type;
            this.stringId = stringId;
            this.forDebug = forDebug;
        }

        public int getImageId() {
            return imageId;
        }
        public MenuItemType getType() { return type; }
        public int getStringId() { return stringId; }

        public static MenuItemId toEnum(int value) {
            if (value >= values().length) return AddTop;
            return values()[value];
        }
    }

    /**
     * Consts
     */
    private static final int TEXT_COLOR = Color.WHITE;
    private static final int TEXT_BG_COLOR = Color.argb(128,0,0,0);
    private static final int ICON_COLOR = UColor.Blue;

    /**
     * Constructor
     */
    public MenuBarTangoEdit(UMenuItemCallbacks callbackClass, int parentW, int parentH, int bgColor) {
        super(callbackClass, parentW, parentH, bgColor);

        // 画面右端に寄せる
        pos.x = parentW - UMenuItem.ITEM_W - MARGIN_H * 2;
    }

    /**
     * メニューバーを生成する
     * @param parentView
     * @param callbackClass
     * @param parentW     親Viewのwidth
     * @param parentH    親Viewのheight
     * @param bgColor
     * @return
     */

    public static MenuBarTangoEdit createInstance(View parentView, UMenuItemCallbacks callbackClass, int parentW, int parentH, int bgColor)
    {
        MenuBarTangoEdit instance = new MenuBarTangoEdit( callbackClass, parentW, parentH, bgColor);
        instance.initMenuBar();
        return instance;
    }


    protected void initMenuBar() {
        UMenuItem item = null;
        UMenuItem itemTop = null;

        // add menu items
        for (MenuItemId itemId : MenuItemId.values()) {
            if (itemId.forDebug) continue;

            Bitmap image = UResourceManager.getBitmapWithColor(itemId.getImageId(), ICON_COLOR);
            switch(itemId.getType()) {
                case Top:
                    item = itemTop = addTopMenuItem(itemId.ordinal(), image);
//                    item.addTitle(UResourceManager.getStringById(itemId.getStringId()),
//                            UAlignment.CenterX,
//                            item.getWidth() / 2, item.getHeight() - 40, TEXT_COLOR, TEXT_BG_COLOR);
                    break;
                case Child:
                    item = addMenuItem(itemTop, itemId.ordinal(), image);

                    // アイコンの左側に表示
                    item.addTitle(UResourceManager.getStringById(itemId.getStringId()),
                            UAlignment.Right_CenterY,
                            -20, item.getHeight() / 2, TEXT_COLOR, TEXT_BG_COLOR);
                    break;
                case State:
                    item.addState(image);
                    break;
            }
        }
        mDrawList = UDrawManager.getInstance().addDrawable(this);
        updateBGSize();
    }

    /**
     * ソフトウェアキーボードの戻るボタンの処理
     * @return
     */
    public boolean onBackKeyDown() {
        // トップメニューが開いていたら閉じる
        for (UMenuItem item : topItems) {
            if (item.isOpened) {
                item.closeMenu();
                return true;
            }
        }
        return false;
    }
}
