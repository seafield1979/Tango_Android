package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import static com.sunsunsoft.shutaro.tangobook.MySharedPref.EditCardNameKey;

/**
 * Created by shutaro on 2017/01/27.
 * オプション設定ページ
 */

public class PageViewOptions extends UPageView
        implements UButtonCallbacks, UDialogCallbacks, OptionColorDialogCallbacks,
        DefaultNameDialogCallbacks, UListItemCallbacks, UWindowCallbacks
{
    /**DefaultNameDialogCallbacks
     * Enum
     */
    enum OptionItems {
        TitleEdit(R.string.title_option_edit, true, Color.BLACK, UColor.LightGreen),
        ColorBook(R.string.option_color_book, false, Color.BLACK, Color.WHITE),
        ColorCard(R.string.option_color_card, false, Color.BLACK, Color.WHITE),
        CardTitle(R.string.option_card_title, false, Color.BLACK, Color.WHITE),
        DefaultNameBook(R.string.option_default_name_book, false, Color.BLACK, Color.WHITE),
        DefaultNameCard(R.string.option_default_name_card, false, Color.BLACK, Color.WHITE),
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

    private static final int TEXT_SIZE = 50;

    // button ids
    private static final int ButtonIdReturn = 100;
    private static final int ButtonIdCardNameA = 101;
    private static final int ButtonIdCardNameB = 102;


    /**
     * Member variables
     */
    private UTextView mTitleText;
    private UListView mListView;
    private UDialogWindow mDialog;

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
        String title;
        for (OptionItems option : OptionItems.values()) {
            if (option == OptionItems.CardTitle) {
                boolean cardTitleE = MySharedPref.readBoolean(MySharedPref.EditCardNameKey, false);
                String str = UResourceManager.getStringById( cardTitleE ?
                        R.string.word_b : R.string.word_a);
                title = option.title + " : " + str;
            } else {
                title = option.title;
            }
            ListItemOption item = new ListItemOption(this, title,
                    option.isTitle, option.color, option.bgColor,
                    0, mListView.getWidth());
            mListView.add(item);
        }

        // スクロールバー等のサイズを更新
        mListView.updateWindow();
    }

    private void closeDialog() {
        if (mDialog != null) {
            mDialog.closeDialog();
            mDialog = null;
        }
    }

    /**
     * リストの項目を更新
     */
    private void updateListItem(OptionItems option) {
        switch(option) {
            case CardTitle: {
                boolean cardTitleE = MySharedPref.readBoolean(MySharedPref.EditCardNameKey, false);
                ListItemOption item = (ListItemOption)mListView.get(option.ordinal());
                String str = UResourceManager.getStringById( cardTitleE ?
                        R.string.word_b : R.string.word_a);
                String title = option.title + " : " + str;
                item.setTitle(title);
            }
                break;
        }
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
            case ButtonIdCardNameA:
                MySharedPref.writeBoolean(EditCardNameKey, false);
                updateListItem(OptionItems.CardTitle);
                if (mDialog != null) {
                    mDialog.closeDialog();
                    mDialog = null;
                }
                break;
            case ButtonIdCardNameB:
                MySharedPref.writeBoolean(EditCardNameKey, true);
                updateListItem(OptionItems.CardTitle);
                if (mDialog != null) {
                    mDialog.closeDialog();
                    mDialog = null;
                }
                break;
        }
        return false;
    }

    /**
     * UDialogCallbacks
     */
    public void dialogClosed(UDialogWindow dialog) {
        if (dialog != null && dialog == mDialog) {
            mDialog = null;
        }
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
            {
                if (mDialog != null) {
                    mDialog.closeDialog();
                    mDialog = null;
                }
                mDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                        this, this,
                        UDialogWindow.ButtonDir.Horizontal, UDialogWindow.DialogPosType.Center,
                        true, mParentView.getWidth(), mParentView.getHeight(),
                        Color.BLACK, Color.LTGRAY);
                mDialog.addToDrawManager();
                mDialog.setTitle(UResourceManager.getStringById(R.string.card_name_title));
                mDialog.addButton(ButtonIdCardNameA, UResourceManager.getStringById(R.string.word_a), UColor.White, UColor.DarkBlue);
                mDialog.addButton(ButtonIdCardNameB, UResourceManager.getStringById(R.string.word_b), Color.WHITE, UColor.LightRed);
                mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
            }
                break;
            case DefaultNameBook:
            {
                DefaultBookNameFragment dialogFragment = DefaultBookNameFragment.createInstance(this);
                dialogFragment.show(((AppCompatActivity)mContext).getSupportFragmentManager(),
                        "fragment_dialog");
            }
            break;
            case DefaultNameCard:
            {
                DefaultCardNameFragment dialogFragment = DefaultCardNameFragment.createInstance
                        (this);
                dialogFragment.show(((AppCompatActivity)mContext).getSupportFragmentManager(),
                        "fragment_dialog");
            }
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
     * DefaultNameDialogCallbacks
     */
    public void submitDefaultName(Bundle args) {
//        updateListItem(OptionItems.CardTitle);
    }
    public void cancelDefaultName() {
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
