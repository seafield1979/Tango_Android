package com.sunsunsoft.shutaro.tangobook;

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
        AddPresetBook(MenuItemType.Child, R.drawable.number_3, R.string.add_preset, false),

        SortTop(MenuItemType.Top, R.drawable.sort, R.string.sort, false),
        SortByWordAsc(MenuItemType.Child, R.drawable.sort_by_alphabet2_asc, R.string.sort_word_asc, false),
        SortByWordDesc(MenuItemType.Child, R.drawable.sort_by_alphabet2_desc, R.string
                .sort_word_desc, false),
        SortByTimeAsc(MenuItemType.Child, R.drawable.sort_by_time_asc, R.string.sort_time_asc, false),
        SortByTimeDesc(MenuItemType.Child, R.drawable.sort_by_time_desc, R.string.sort_time_desc, false),

        DebugTop(MenuItemType.Top, R.drawable.debug, R.string.debug, true),
        Debug1(MenuItemType.Child, R.drawable.number_1, R.string.debug1, true),

        Help(MenuItemType.Top, R.drawable.question, R.string.help, false),
        ShowMenuName(MenuItemType.Child, R.drawable.number_1, R.string.disp_menu_name, false),
        ShowMenuHelp(MenuItemType.Child, R.drawable.number_2, R.string.disp_menu_help, false),

        Setting(MenuItemType.Top, R.drawable.settings_1, R.string.title_settings, false),
        SearchCard(MenuItemType.Top, R.drawable.loupe, R.string.search_card, false)
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

    /**
     * Constructor
     */
    public MenuBarTangoEdit(UMenuItemCallbacks callbackClass, int parentW, int parentH, int bgColor) {
        super(callbackClass, parentW, parentH, bgColor);
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
            switch(itemId.getType()) {
                case Top:
                    item = itemTop = addTopMenuItem(itemId.ordinal(), itemId.getImageId());
                    item.addTitle(UResourceManager.getStringById(itemId.getStringId()),
                            UAlignment.CenterX,
                            item.getWidth() / 2, item.getHeight() - 40, TEXT_COLOR, TEXT_BG_COLOR);
                    break;
                case Child:
                    item = addMenuItem(itemTop, itemId.ordinal(), itemId.getImageId());
                    // テキストは右側に表示する
                    item.addTitle(UResourceManager.getStringById(itemId.getStringId()),
                            UAlignment.CenterY,
                            item.getWidth() + 10, item.getHeight() / 2, TEXT_COLOR, TEXT_BG_COLOR);
                    break;
                case State:
                    item.addState(UResourceManager.getBitmapById(itemId.getImageId()));
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
