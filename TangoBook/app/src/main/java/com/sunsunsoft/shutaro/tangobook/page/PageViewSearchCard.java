package com.sunsunsoft.shutaro.tangobook.page;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.EditTextCallbacks;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.database.TangoCard;
import com.sunsunsoft.shutaro.tangobook.TopFragment;
import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.util.ULog;
import com.sunsunsoft.shutaro.tangobook.listview.ListItemSearchedCard;
import com.sunsunsoft.shutaro.tangobook.u_dialog.DialogCard;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;

import java.util.List;

/**
 * Created by shutaro on 2016/12/21.
 */

public class PageViewSearchCard extends UPageView
        implements UButtonCallbacks, EditTextCallbacks, UListItemCallbacks
{
    /**
     * Consts
     */
    public static final String TAG = "PageViewSearchCard";

    private static final int LIST_ITEM_MAX = 20;
    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 50;

    /**
     * Members
     */
    // 検索結果を表示するリストView
    private UListView mListView;
    private DialogCard mCardDialog;

    /**
     * Constructor
     */
    public PageViewSearchCard(Context context, View parentView, String title) {
        super(context, parentView, title);

    }

    /**
     * Methods
     */

    protected void onShow() {
        // 画面上部にEditTextを表示
        TopFragment.getInstance().showEditLayout(true);
        TopFragment.getInstance().setEditTextCallback(this);
    }

    protected void onHide() {
        super.onHide();
        TopFragment.getInstance().showEditLayout(false);
    }

    /**
     * 描画処理
     * サブクラスのdrawでこのメソッドを最初に呼び出す
     * @param canvas
     * @param paint
     * @return
     */
    protected boolean draw(Canvas canvas, Paint paint) {
        if (isFirst) {
            isFirst = false;
            initDrawables();
        }
        return false;
    }

    /**
     * タッチ処理
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt) {

        return false;
    }

    /**
     * そのページで表示される描画オブジェクトを初期化する
     */
    public void initDrawables() {
        UDrawManager.getInstance().init();

        int width = mParentView.getWidth();
        int height = mParentView.getHeight();
        float y = MARGIN_V;

        mListView = new UListView(null, this, 100, MARGIN_H, y, width - MARGIN_H * 2, height -
                MARGIN_V * 2,
                Color.WHITE);
        mListView.setFrameColor(Color.BLACK);
        mListView.addToDrawManager();
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
     * EditTextCallbacks
     */
    /**
     * 編集前イベント
     */
    public void beforeTextChanged(String str, int start, int count, int after) {
        ULog.print(TAG, "beforeTextChanged:" + str);
    }
    /**
     * 編集後イベント
     */
    public void onTextChanged(String str, int start, int before, int count) {
        ULog.print(TAG, "onTextChanged:" + str);

        List<TangoCard> cards = RealmManager.getCardDao().selectByWordA(str);

        int itemCnt = 0;
        mListView.clear();
        mParentView.invalidate();

        if (cards == null || cards.size() == 0) {
            return;
        }

        for (TangoCard card : cards) {
            ULog.print(TAG, "wordA:" + card.getWordA());
            ListItemSearchedCard item = new ListItemSearchedCard(this, card, mListView.getWidth(),
                    Color.WHITE);
            mListView.add(item);

            itemCnt++;
            // ListViewの最大表示件数を超えていたら抜ける
            if( itemCnt >= LIST_ITEM_MAX) {
                break;
            }
        }

        // スクロールバーが表示されるようにWindowサイズを更新
        mListView.updateWindow();
    }
    /**
     * 編集確定後イベント
     */
    public void afterTextChanged(String str) {
        ULog.print(TAG, "afterTextChanged:");
    }


    /**
     * UListItemCallbacks
     */
    /**
     * 項目がクリックされた
     * @param item
     */
    public void ListItemClicked(UListItem item) {
        if (!(item instanceof ListItemSearchedCard)) return;

        ListItemSearchedCard _item = (ListItemSearchedCard)item;

        mCardDialog = new DialogCard(_item.getCard(), true, mParentView.getWidth(), mParentView
                .getHeight());
        mCardDialog.addToDrawManager();
    }

    /**
     * 項目のボタンがクリックされた
     */
    public void ListItemButtonClicked(UListItem item, int buttonId) {

    }
}
