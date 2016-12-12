package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.List;

/**
 * Created by shutaro on 2016/12/05.
 */

public class PageViewResult extends UPageView
        implements UButtonCallbacks, UListItemCallbacks
{
    /**
     * Constants
     */
    public static final int LV_TOP_Y = 400;

    /**
     * Member variables
     */
    private ListViewResult mListView;
    private List<TangoCard> mOkCards;
    private List<TangoCard> mNgCards;


    /**
     * Constructor
     */
    public PageViewResult(Context context, View parentView) {
        super(context, parentView);
    }

    /**
     * Get/Set
     */
    public void setCardsLists(List<TangoCard> okCards, List<TangoCard> ngCards) {
        mOkCards = okCards;
        mNgCards = ngCards;
    }

    /**
     * Methods
     */

    public void onShow() {

    }

    public void onHide() {
        isFirst = true;
    }

    /**
     * そのページで表示される描画オブジェクトを初期化する
     */
    public void initDrawables() {
        UDrawManager.getInstance().init();

        // ListView
        int width = mParentView.getWidth();
        int height = mParentView.getHeight();
        mListView = new ListViewResult(this, mOkCards, mNgCards, 0, 100, LV_TOP_Y,
                width - 200, height - LV_TOP_Y - 50, Color.WHITE);

    }

    /**
     * ソフトウェアキーの戻るボタンを押したときの処理
     * @return
     */
    public boolean onBackKeyDown() {

        return false;
    }

    /**
     * Callbacks
     */

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {

        return false;
    }

    /**
     * UListItemCallbacks
     */
    /**
     * 項目がクリックされた
     * @param item
     */
    public void ListItemClicked(UListItem item) {
        // クリックされた項目の詳細を表示する
        if (!(item instanceof ListItemResult)) return;

        ListItemResult _item = (ListItemResult)item;
        if (_item.getType() != ListItemResult.ListItemResultType.Title) {

        }
    }
}
