package com.sunsunsoft.shutaro.tangobook.tango_edit;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.uview.menu.UMenuBar;
import com.sunsunsoft.shutaro.tangobook.uview.menu.UMenuItem;
import com.sunsunsoft.shutaro.tangobook.uview.menu.UMenuItemCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;

/**
 * Created by shutaro on 2016/12/06.
 *
 * 単語帳編集ページで表示するメニューバー
 */

public class MenuBarTangoEdit extends UMenuBar {
    /**
     * Enums
     */
    public enum MenuItemType {
        Top,
        Child,
        State
    }

    // メニューのID、画像ID、Topかどうかのフラグ
    public enum MenuItemId {
        AddTop(MenuItemType.Top, R.drawable.add, R.string.add_item, UColor.Blue, false),
        AddCard(MenuItemType.Child, R.drawable.card, R.string.add_card, ICON_COLOR, false),
        AddBook(MenuItemType.Child, R.drawable.cards, R.string.add_book, ICON_COLOR, false),
        AddDummyCard(MenuItemType.Child, R.drawable.number_1, R.string.add_dummy_card, ICON_COLOR, true),
        AddDummyBook(MenuItemType.Child, R.drawable.number_2, R.string.add_dummy_book, ICON_COLOR, true),
        AddPresetBook(MenuItemType.Child, R.drawable.cards, R.string.add_preset, ICON_COLOR, false),
        AddCsvBook(MenuItemType.Child, R.drawable.cards, R.string.add_csv, ICON_COLOR, false),
        ;

        private MenuItemType type;
        private int imageId;
        private int stringId;
        private int color;
        private boolean forDebug;       // デバッグ用のアイテム

        MenuItemId(MenuItemType type, int imageId, int stringId, int color, boolean forDebug) {
            this.imageId = imageId;
            this.type = type;
            this.stringId = stringId;
            this.color = color;
            this.forDebug = forDebug;
        }

        public int getImageId() {
            return imageId;
        }
        public MenuItemType getType() { return type; }
        public int getStringId() { return stringId; }
        public int getColor() { return color; }

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
    private static final int ICON_COLOR = UColor.BLACK;

    /**
     * Constructor
     */
    public MenuBarTangoEdit(UMenuItemCallbacks callbackClass, int parentW, int parentH, int bgColor) {
        super(callbackClass, parentW, parentH, bgColor);

        // 画面右端に寄せる
        pos.x = parentW - UDpi.toPixel(UMenuItem.ITEM_W + MARGIN_H);
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

    /**
     * メニューバーを初期化
     * バーに表示する項目を追加する
     */
    protected void initMenuBar() {
        UMenuItem item = null;
        UMenuItem itemTop = null;

        // add menu items
        for (MenuItemId itemId : MenuItemId.values()) {
            if (itemId.forDebug) continue;

            Bitmap image = UResourceManager.getBitmapWithColor(itemId.getImageId(), itemId
                    .getColor());
            switch(itemId.getType()) {
                case Top:
                    item = itemTop = addTopMenuItem(itemId.ordinal(), image);
                    break;
                case Child:
                    item = addMenuItem(itemTop, itemId.ordinal(), image);

                    // アイコンの左側に表示
                    item.addTitle(UResourceManager.getStringById(itemId.getStringId()),
                            UAlignment.Right_CenterY,
                            UDpi.toPixel(-8), item.getHeight() / 2, TEXT_COLOR, TEXT_BG_COLOR);
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
            if (item.isOpened()) {
                item.closeMenu();
                return true;
            }
        }
        return false;
    }
}
