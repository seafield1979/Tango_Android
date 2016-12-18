package com.sunsunsoft.shutaro.tangobook;

import android.view.View;

/**
 * Created by shutaro on 2016/12/06.
 */

public class MenuBarTangoEdit extends UMenuBar {
    /**
     * Enums
     */
    // メニューをタッチした時に返されるID
    enum MenuItemId {
        AddTop(R.drawable.add, true),
        AddCard(R.drawable.file_add, false),
        AddBook(R.drawable.folder_add, false),
        AddDummyCard(R.drawable.number_1, false),
        AddDummyBook(R.drawable.number_2, false),
        AddPresetBook(R.drawable.number_3, false),
        SortTop(R.drawable.sort, true),
        Sort1(R.drawable.sort_by_alphabet2_asc, false),
        Sort2(R.drawable.sort_by_alphabet2_desc, false),
        ListTypeTop(R.drawable.list, true),
        ListType1(R.drawable.list1, false),
        ListType2(R.drawable.grid_icons, false),
        DebugTop(R.drawable.debug, true),
        Debug1(R.drawable.number_1, false),
        Debug2(R.drawable.number_2, false),
        Debug3(R.drawable.number_3, false),
        Debug4(R.drawable.number_4, false),
        Debug5(R.drawable.number_5, false),
        Debug6(R.drawable.number_6, false),
        Debug2Top(R.drawable.debug, true),
        Debug2RealmCopy(R.drawable.number_1, false),
        Debug2RealmRestore(R.drawable.number_2, false),
        ;

        private boolean isTop;
        private int imageId;

        MenuItemId(int imageId, boolean isTop) {
            this.imageId = imageId;
            this.isTop = isTop;
        }

        public int getImageId() {
            return imageId;
        }
        public boolean isTop() {
            return isTop;
        }

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
        UMenuItem itemTop = null;

        // add menu items
        for (MenuItemId itemId : MenuItemId.values()) {
            if (itemId.isTop()) {
                // Parent
                itemTop = addTopMenuItem(itemId.ordinal(), itemId.getImageId());
            } else {
                // Child
                addMenuItem(itemTop, itemId.ordinal(), itemId.getImageId());
            }
        }

        mDrawList = UDrawManager.getInstance().addDrawable(this);
        updateBGSize();
    }
}
