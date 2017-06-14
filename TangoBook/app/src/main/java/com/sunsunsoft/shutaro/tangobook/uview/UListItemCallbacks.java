package com.sunsunsoft.shutaro.tangobook.uview;

/**
 * Created by shutaro on 2016/12/09.
 *
 * UListViewに表示する項目
 */
public interface UListItemCallbacks {
    /**
     * 項目がクリックされた
     * @param item
     */
    void ListItemClicked(UListItem item);

    /**
     * 項目のボタンがクリックされた
     */
    void ListItemButtonClicked(UListItem item, int buttonId);
}
