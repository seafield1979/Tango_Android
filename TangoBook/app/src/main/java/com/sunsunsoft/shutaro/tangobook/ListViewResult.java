package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

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
    // OKカードとNGカードが別で渡ってくるパターン(リザルトページ)
    // @param studyMode  学習モード false:英->日  true:日->英
    public ListViewResult( UListItemCallbacks listItemCallbacks,
                          List<TangoCard> okCards, List<TangoCard> ngCards, boolean studyMode,
                     int priority, float x, float y, int width, int
                             height, int color)
    {
        super(null, listItemCallbacks, priority, x, y, width, height, color);

        initItems(okCards, ngCards, studyMode, true);

    }

    // OKカードとNGカードが同じリストに入って渡ってくる
    // 履歴ページで表示する用途
    public ListViewResult( UListItemCallbacks listItemCallbacks,
                           List<TangoStudiedCard> studiedCards, boolean studyMode,
                           int priority, float x, float y, int width, int
                                   height, int color)
    {
        super(null, listItemCallbacks, priority, x, y, width, height, color);

        List<TangoCard> ngCards = RealmManager.getCardDao().selectByStudiedCards(studiedCards,
                false, false);
        List<TangoCard> okCards = RealmManager.getCardDao().selectByStudiedCards(studiedCards,
                true, false);
        initItems(okCards, ngCards, studyMode, false);
    }

    /**
     * Methods
     */

    public void drawContent(Canvas canvas, Paint paint, PointF offset) {
        super.drawContent(canvas, paint, offset);
    }

    /**
     * アイテムを追加する
     * @param okCards
     * @param ngCards
     * @param studyMode 学習モード false:英->日 true:日->英
     */
    private void initItems(List<TangoCard> okCards, List<TangoCard> ngCards, boolean studyMode,
                           boolean star) {
        ListItemResult item = null;
        // OK
        if (okCards != null && okCards.size() > 0) {
            // Title
            item = ListItemResult.createTitle("OK", size.width, TITLE_TEXT_COLOR, TITLE_OK_COLOR);
            add(item);
            // Items
            for (TangoCard card : okCards) {
                item = ListItemResult.createOK(card, studyMode, star, size.width, ITEM_TEXT_COLOR,
                        ITEM_BG_COLOR);
                add(item);
            }
        }

        // NG
        if (ngCards != null && ngCards.size() > 0) {
            // Title
            item = ListItemResult.createTitle("NG", size.width, TITLE_TEXT_COLOR, TITLE_NG_COLOR);
            add(item);
            // Items
            for (TangoCard card : ngCards) {
                item = ListItemResult.createNG(card, studyMode, size.width, ITEM_TEXT_COLOR, ITEM_BG_COLOR);
                add(item);
            }
        }
        updateWindow();
    }


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
