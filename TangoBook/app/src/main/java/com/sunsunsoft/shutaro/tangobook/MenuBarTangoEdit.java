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
        AddTop(MenuItemType.Top, R.drawable.add, R.string.add_item),
        AddCard(MenuItemType.Child, R.drawable.file_add, R.string.add_card),
        AddBook(MenuItemType.Child, R.drawable.folder_add, R.string.add_book),
        AddDummyCard(MenuItemType.Child, R.drawable.number_1, R.string.add_dummy_card),
        AddDummyBook(MenuItemType.Child, R.drawable.number_2, R.string.add_dummy_book),
        AddPresetBook(MenuItemType.Child, R.drawable.number_3, R.string.add_preset),

        SortTop(MenuItemType.Top, R.drawable.sort, R.string.sort),
        SortByWordAsc(MenuItemType.Child, R.drawable.sort_by_alphabet2_asc, R.string.sort_word_asc),
        SortByWordDesc(MenuItemType.Child, R.drawable.sort_by_alphabet2_desc, R.string
                .sort_word_desc),
        SortByTimeAsc(MenuItemType.Child, R.drawable.sort_by_time_asc, R.string.sort_time_asc),
        SortByTimeDesc(MenuItemType.Child, R.drawable.sort_by_time_desc, R.string.sort_time_desc),

        ListTypeTop(MenuItemType.Top, R.drawable.list, R.string.disp_list),
        ListType1(MenuItemType.Child, R.drawable.list1, R.string.list_type1),
        ListType2(MenuItemType.Child, R.drawable.grid_icons, R.string.list_type_grid),

        DebugTop(MenuItemType.Top, R.drawable.debug, R.string.debug),
        Debug1(MenuItemType.Child, R.drawable.number_1, R.string.debug1),
        Debug2(MenuItemType.Child, R.drawable.number_2, R.string.debug2),
        Debug3(MenuItemType.Child, R.drawable.number_3, R.string.debug3),
        Debug4(MenuItemType.Child, R.drawable.number_4, R.string.debug4),
        Debug5(MenuItemType.Child, R.drawable.number_5, R.string.debug5),
        Debug6(MenuItemType.Child, R.drawable.number_6, R.string.debug6),

        Help(MenuItemType.Top, R.drawable.question, R.string.help),
        ShowMenuName(MenuItemType.Child, R.drawable.number_1, R.string.disp_menu_name),
        ShowMenuHelp(MenuItemType.Child, R.drawable.number_2, R.string.disp_menu_help),
        ;

        private MenuItemType type;
        private int imageId;
        private int stringId;

        MenuItemId(MenuItemType type, int imageId, int stringId) {
            this.imageId = imageId;
            this.type = type;
            this.stringId = stringId;
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
}
