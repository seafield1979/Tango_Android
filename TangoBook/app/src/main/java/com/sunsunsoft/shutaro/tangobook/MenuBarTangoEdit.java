package com.sunsunsoft.shutaro.tangobook;

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
        AddTop(R.drawable.add, MenuItemType.Top),
        AddCard(R.drawable.file_add, MenuItemType.Child),
        AddBook(R.drawable.folder_add, MenuItemType.Child),
        AddDummyCard(R.drawable.number_1, MenuItemType.Child),
        AddDummyBook(R.drawable.number_2, MenuItemType.Child),
        AddPresetBook(R.drawable.number_3, MenuItemType.Child),

        SortTop(R.drawable.sort, MenuItemType.Top),
        Sort1(R.drawable.sort_by_alphabet2_asc, MenuItemType.Child),
        Sort2(R.drawable.sort_by_alphabet2_desc, MenuItemType.Child),

        ListTypeTop(R.drawable.list, MenuItemType.Top),
        ListType1(R.drawable.list1, MenuItemType.Child),
        ListType2(R.drawable.grid_icons, MenuItemType.Child),

        DebugTop(R.drawable.debug, MenuItemType.Top),
        Debug1(R.drawable.number_1, MenuItemType.Child),
        Debug2(R.drawable.number_2, MenuItemType.Child),
        Debug3(R.drawable.number_3, MenuItemType.Child),
        Debug4(R.drawable.number_4, MenuItemType.Child),
        Debug5(R.drawable.number_5, MenuItemType.Child),
        Debug6(R.drawable.number_6, MenuItemType.Child),
        Debug2Top(R.drawable.debug, MenuItemType.Top),
        Debug2RealmCopy(R.drawable.number_1, MenuItemType.Child),
        Debug2RealmRestore(R.drawable.number_2, MenuItemType.Child),
        ;

        private MenuItemType type;
        private int imageId;

        MenuItemId(int imageId, MenuItemType type) {
            this.imageId = imageId;
            this.type = type;
        }

        public int getImageId() {
            return imageId;
        }
        public MenuItemType getType() { return type; }

        public static MenuItemId toEnum(int value) {
            if (value >= values().length) return AddTop;
            return values()[value];
        }
    }

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
                    break;
                case Child:
                    item = addMenuItem(itemTop, itemId.ordinal(), itemId.getImageId());
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
