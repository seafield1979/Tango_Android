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
    private static final int TITLE_TEXT_COLOR = Color.WHITE;
    private static final int TITLE_OK_COLOR = Color.rgb(50,200,50);
    private static final int TITLE_NG_COLOR = Color.rgb(200,50,50);
    private static final int ITEM_BG_COLOR = Color.WHITE;
    private static final int ITEM_TEXT_COLOR = Color.BLACK;

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
                          List<TangoCard> okCards, List<TangoCard> ngCards, boolean mode,
                     int priority, float x, float y, int width, int
                             height, int color)
    {
        super(null, listItemCallbacks, priority, x, y, width, height, color);

        ListItemResult item = null;
        // OK
        if (okCards.size() > 0) {
            // Title
            item = ListItemResult.createTitle("OK", width, TITLE_TEXT_COLOR, TITLE_OK_COLOR);
            add(item);
            // Items
            for (TangoCard card : okCards) {
                item = ListItemResult.createOK(card, mode, width, ITEM_TEXT_COLOR, ITEM_BG_COLOR);
                add(item);
            }
        }

        // NG
        if (ngCards.size() > 0) {
            // Title
            item = ListItemResult.createTitle("NG", width, TITLE_TEXT_COLOR, TITLE_NG_COLOR);
            add(item);
            // Items
            for (TangoCard card : ngCards) {
                item = ListItemResult.createNG(card, mode, width, ITEM_TEXT_COLOR, ITEM_BG_COLOR);
                add(item);
            }
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
    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        switch (id) {

        }
        return false;
    }

}
