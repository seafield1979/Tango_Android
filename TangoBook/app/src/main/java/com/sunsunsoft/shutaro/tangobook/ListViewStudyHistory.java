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

        // add items
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
