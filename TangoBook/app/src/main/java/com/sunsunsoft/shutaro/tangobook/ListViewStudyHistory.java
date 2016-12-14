package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Color;

import java.util.List;

/**
 * Created by shutaro on 2016/12/14.
 *
 * 学習履歴
 */

public class ListViewStudyHistory extends UListView{
    /**
     * Enums
     */
    /**
     * Constants
     */
    private static final int TITLE_TEXT_COLOR = Color.WHITE;
    private static final int TITLE_OK_COLOR = Color.rgb(50,200,50);
    private static final int TITLE_NG_COLOR = Color.rgb(200,50,50);
    private static final int ITEM_BG_COLOR = Color.WHITE;
    private static final int ITEM_TEXT_COLOR = Color.BLACK;

    private static final int LIMIT = 100;

    /**
     * Member variables
     */

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public ListViewStudyHistory(UListItemCallbacks listItemCallbacks,
                          int priority, float x, float y, int width, int
                                  height, int color)
    {
        super(null, listItemCallbacks, priority, x, y, width, height, color);

        List<TangoBookHistory> histories = RealmManager.getBookHistoryDao().selectAllWithLimit
                (true, LIMIT);

        for (TangoBookHistory history : histories) {
            ListItemStudiedBook item = ListItemStudiedBook.createHistory( history,
                width, Color.BLACK, Color.WHITE);
            add(item);
        }

        updateWindow();
    }

    /**
     * Methods
     */


    /**
     * for Debug
     */
    public void addDummyItems(int count) {

        updateWindow();
    }

    /**
     * Callbacks
     */
}
