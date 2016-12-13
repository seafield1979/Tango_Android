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
        AddTop,
        AddCard,
        AddBook,
        AddDummyCard,
        AddDummyBook,
        SortTop,
        Sort1,
        Sort2,
        Sort3,
        ListTypeTop,
        ListType1,
        ListType2,
        ListType3,
        DebugTop,
        Debug1,
        Debug2,
        Debug3,
        Debug4,
        Debug5,
        Debug6,
        Debug2Top,
        Debug2RealmCopy,
        Debug2RealmRestore
        ;

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

        // Add
        item = addTopMenuItem(MenuItemId.AddTop.ordinal(), R.drawable.add);
        addMenuItem(item, MenuItemId.AddCard.ordinal(), R.drawable.file_add);
        addMenuItem(item, MenuItemId.AddBook.ordinal(), R.drawable.folder_add);
        addMenuItem(item, MenuItemId.AddDummyCard.ordinal(), R.drawable.number_1);
        addMenuItem(item, MenuItemId.AddDummyBook.ordinal(), R.drawable.number_2);

        // Sort
        item = addTopMenuItem(MenuItemId.SortTop.ordinal(), R.drawable.sort);
        addMenuItem(item, MenuItemId.Sort1.ordinal(), R.drawable.sort_by_alphabet2_asc);
        addMenuItem(item, MenuItemId.Sort2.ordinal(), R.drawable.sort_by_alphabet2_desc);

        // ListType
        item = addTopMenuItem(MenuItemId.ListTypeTop.ordinal(), R.drawable.list);
        addMenuItem(item, MenuItemId.ListType1.ordinal(), R.drawable.list1);
        addMenuItem(item, MenuItemId.ListType2.ordinal(), R.drawable.grid_icons);
        // Debug
        item = addTopMenuItem(MenuItemId.DebugTop.ordinal(), R.drawable.debug);
        addMenuItem(item, MenuItemId.Debug1.ordinal(), R.drawable.number_1);
        addMenuItem(item, MenuItemId.Debug2.ordinal(), R.drawable.number_2);
        addMenuItem(item, MenuItemId.Debug3.ordinal(), R.drawable.number_3);
        addMenuItem(item, MenuItemId.Debug4.ordinal(), R.drawable.number_4);
        addMenuItem(item, MenuItemId.Debug5.ordinal(), R.drawable.number_5);
        addMenuItem(item, MenuItemId.Debug6.ordinal(), R.drawable.number_6);

        // Debug2
        item = addTopMenuItem(MenuItemId.Debug2Top.ordinal(), R.drawable.debug);
        addMenuItem(item, MenuItemId.Debug2RealmCopy.ordinal(), R.drawable.number_1);
        addMenuItem(item, MenuItemId.Debug2RealmRestore.ordinal(), R.drawable.number_2);


        mDrawList = UDrawManager.getInstance().addDrawable(this);
        updateBGSize();
    }
}
