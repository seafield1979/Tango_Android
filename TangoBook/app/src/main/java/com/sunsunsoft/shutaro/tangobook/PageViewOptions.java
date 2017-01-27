package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.List;

/**
 * Created by shutaro on 2017/01/27.
 * オプション設定ページ
 */

public class PageViewOptions extends UPageView
        implements OptionColorDialogCallbacks, UListItemCallbacks, UWindowCallbacks{
    /**
     * Enum
     */
    enum OptionItems {
        TitleEdit(R.string.title_option_edit, true, Color.BLACK, UColor.LightGreen),
        ColorBook(R.string.option_color_book, false, Color.BLACK, Color.WHITE),
        ColorCard(R.string.option_color_card, false, Color.BLACK, Color.WHITE),
        CardTitle(R.string.option_card_title, false, Color.BLACK, Color.WHITE),
        DefaultNameBook(R.string.option_default_name_card, false, Color.BLACK, Color.WHITE),
        DefaultNameCard(R.string.option_default_name_book, false, Color.BLACK, Color.WHITE),
        TitleStudy(R.string.title_option_study, true, Color.BLACK, UColor.LightRed),
        AddNgCard(R.string.option_add_ng_card, false, Color.BLACK, Color.WHITE),
        StudyMode4_1(R.string.option_mode4_1, false, Color.BLACK, Color.WHITE),
        ;

        public String title;
        public boolean isTitle;
        public int color;
        public int bgColor;

        OptionItems(int titleId, boolean isTitle, int color, int bgColor) {
            this.title = UResourceManager.getStringById(titleId);
            this.isTitle = isTitle;
            this.color = color;
            this.bgColor = bgColor;
        }
        public static OptionItems toEnum(int val) {
            if (val >= values().length) {
                return TitleEdit;
            }
            return values()[val];
        }
    }


    /**
     * Constants
     */
    private static final int DRAW_PRIORITY = 100;

    private static final int MARGIN_H = 50;
    private static final int MARGIN_V_S = 30;
    private static final int BUTTON_W = 300;
    private static final int BUTTON_H = 120;

    private static final int TEXT_SIZE = 50;
    private static final int ButtonIdReturn = 100;

    /**
     * Member variables
     */
    private UTextView mTitleText;
    private UListView mListView;

    /**
     * Constructor
     */
    public PageViewOptions(Context context, View parentView, String title) {
        super(context, parentView, title);
    }

    /**
     * Methods
     */

    protected void onShow() {

    }

    protected void onHide() {
        super.onHide();
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

        float x = MARGIN_H;
        float y = MARGIN_V_S;

        // Title
        mTitleText = UTextView.createInstance(UResourceManager.getStringById(R.string
                        .title_options2),
                TEXT_SIZE, DRAW_PRIORITY,
                UAlignment.CenterX, width, false, false,
                width / 2, y, width, Color.BLACK, 0);
        mTitleText.addToDrawManager();
        y += mTitleText.size.height + MARGIN_V_S;

        // ListView
        int listViewH = height - (MARGIN_V_S * 3 + mTitleText.size.height);
        mListView = new UListView(null, this, DRAW_PRIORITY, x, y,
                width - MARGIN_H * 2, listViewH, 0);
        mListView.setFrameColor(Color.BLACK);
        mListView.addToDrawManager();

        // アイテムを追加
        for (OptionItems option : OptionItems.values()) {
            ListItemOption item = new ListItemOption(this, option.title,
                    option.isTitle, option.color, option.bgColor,
                    0, mListView.getWidth());
            mListView.add(item);
        }

        // スクロールバー等のサイズを更新
        mListView.updateWindow();
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
        switch(id) {
            case ButtonIdReturn:
                PageViewManager.getInstance().popPage();
                break;
        }
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
        OptionItems itemId = OptionItems.toEnum(item.getIndex());
        switch(itemId) {
            case ColorBook:
            case ColorCard:
            {
                OptionColorFragment.ColorMode mode = (itemId == OptionItems.ColorBook) ?  OptionColorFragment.ColorMode.Book : OptionColorFragment.ColorMode.Card;
                OptionColorFragment dialogFragment = OptionColorFragment.createInstance(this, mode);
                dialogFragment.show(((AppCompatActivity)mContext).getSupportFragmentManager(),
                        "fragment_dialog");
            }
                break;
            case CardTitle:
                break;
            case DefaultNameBook:
                break;
            case DefaultNameCard:
                break;
            case AddNgCard:
                break;
            case StudyMode4_1:
                break;
        }
    }
    public void ListItemButtonClicked(UListItem item, int buttonId) {

    }

    /**
     * OptionColorDialogCallbacks
     */
    public void submitOptionColor(Bundle args) {
        if (args == null) return;
    }
    public void cancelOptionColor() {

    }


    /**
     * UWindowCallbacks
     */
    public void windowClose(UWindow window) {
//        // Windowを閉じる
//        if (mPreStudyWindow == window) {
//            mPreStudyWindow.setShow(false);
//        }
    }
}
