package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Color;
import java.util.List;


/**
 * Created by shutaro on 2016/12/11.
 *
 * リザルトページで表示するListView
 */

public class ListViewResult extends UListView implements UButtonCallbacks{

    /**
     * Enums
     */
    /**
     * Constants
     */
    private static final int TITLE_OK_COLOR = Color.rgb(50,200,50);
    private static final int TITLE_NG_COLOR = Color.rgb(50,50,200);
    private static final int ITEM_OK_COLOR = Color.rgb(100,200,100);
    private static final int ITEM_NG_COLOR = Color.rgb(100,100,200);

    /**
     * Member variables
     */

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public ListViewResult( UListItemCallbacks listItemCallbacks,
                          List<TangoCard> okCards, List<TangoCard> ngCards,
                     int priority, float x, float y, int width, int
                             height, int color)
    {
        super(null, listItemCallbacks, priority, x, y, width, height, color);

        // OK
        // Title
        ListItemResult item = ListItemResult.createTitle("OK", width, TITLE_OK_COLOR);
        add(item);
        // Items
        for (TangoCard card : okCards) {
            item = ListItemResult.createOK(card, width, ITEM_OK_COLOR);
            add(item);
        }

        // NG
        // Title
        item = ListItemResult.createTitle("NG", width, TITLE_NG_COLOR);
        add(item);
        // Items
        for (TangoCard card : ngCards) {
            item = ListItemResult.createNG(card, width, ITEM_NG_COLOR);
            add(item);
        }
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
    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        switch (id) {

        }
        return false;
    }

}
