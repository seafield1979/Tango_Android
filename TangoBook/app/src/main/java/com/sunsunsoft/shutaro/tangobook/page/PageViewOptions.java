package com.sunsunsoft.shutaro.tangobook.page;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.uview.UAlignment;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButton;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonCallbacks;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.uview.window.UDialogCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.window.UDialogWindow;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;
import com.sunsunsoft.shutaro.tangobook.uview.UListItem;
import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.uview.UListView;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.uview.text.UTextView;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;
import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;
import com.sunsunsoft.shutaro.tangobook.fragment.DefaultBookNameFragment;
import com.sunsunsoft.shutaro.tangobook.fragment.DefaultCardNameFragment;
import com.sunsunsoft.shutaro.tangobook.fragment.DefaultNameDialogCallbacks;
import com.sunsunsoft.shutaro.tangobook.fragment.OptionColorDialogCallbacks;
import com.sunsunsoft.shutaro.tangobook.fragment.OptionColorFragment;
import com.sunsunsoft.shutaro.tangobook.listview.ListItemOption;

import static com.sunsunsoft.shutaro.tangobook.app.MySharedPref.EditCardNameKey;

/**
 * Created by shutaro on 2017/01/27.
 * オプション設定ページ
 */



public class PageViewOptions extends UPageView
        implements UButtonCallbacks, UDialogCallbacks, OptionColorDialogCallbacks,
        DefaultNameDialogCallbacks, UListItemCallbacks
{
    /**
     * Enums
     */
    // モード(リストに表示する項目が変わる)
    enum Mode {
        All,        // 全オプションを表示
        Edit,       // 単語帳編集系の項目を表示
        Study       // 学習系の項目を表示
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
     private static final int ButtonIdCardWordA = 101;       // カードに表示する名前(A->英語)
    private static final int ButtonIdCardWordB = 102;       // カードに表示する名前(B->日本語)
//    private static final int ButtonIdAddNgCardOk = 103;
//    private static final int ButtonIdAddNgCardNg = 104;
    private static final int ButtonIdSelectFromAll = 103;   // ４択学習モードで不正解のカードをカード全体から取得
    private static final int ButtonIdSelectFromOne = 104;   // ４択学習もーどで不正解のカードを同じ単語帳から取得
    private static final int ButtonIdStudySorted = 105;     // 正解入力学習モードの文字並びをA-Zでソート
    private static final int ButtonIdStudyRandom = 106;     // 正解入力学習モードの文字並びをランダムに表示

    /**
     * Member variables
     */
    private Mode mMode;
    private UTextView mTitleText;
    private UListView mListView;
    private UDialogWindow mDialog;

    /**
     * Constructor
     */
    public PageViewOptions(Context context, View parentView, String title) {
        super(context, parentView, title);
        mMode = Mode.All;
    }

    /**
     * Get/Set
     */
    public void setMode(Mode mode) {
        mMode = mode;
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
        y += mTitleText.getSize().height + MARGIN_V_S;

        // ListView
        int listViewH = height - (MARGIN_V_S * 3 + mTitleText.getSize().height);
        mListView = new UListView(null, this, DRAW_PRIORITY, x, y,
                width - MARGIN_H * 2, listViewH, 0);
        mListView.setFrameColor(Color.BLACK);
        mListView.addToDrawManager();

        // アイテムを追加
        for (OptionItems option : OptionItems.getItems(mMode)) {
            ListItemOption item = new ListItemOption(this, option, getItemTitle(option),
                    option.isTitle, option.color, option.bgColor,
                    0, mListView.getWidth());
            mListView.add(item);
        }

        // スクロールバー等のサイズを更新
        mListView.updateWindow();
    }

    /**
     * アイテムに表示するテキストを取得する
     * @param option
     * @return
     */
    private String getItemTitle(OptionItems option) {
        String title = null;

        switch (option) {
            case CardTitle: {
                boolean cardTitleE = MySharedPref.readBoolean(MySharedPref.EditCardNameKey, false);
                String str = UResourceManager.getStringById(cardTitleE ?
                        R.string.word_b : R.string.word_a);
                title = option.title + " : " + str;
            }
            break;
            case DefaultNameBook: {
                String str = MySharedPref.readString(MySharedPref.DefaultNameBookKey);
                if (str != null && str.length() > 0) {
                    title = option.title + "\n" + str;
                } else {
                    title = option.title;
                }
            }
            break;
            case DefaultNameCard:
            {
                StringBuffer buf = new StringBuffer(OptionItems.DefaultNameCard.title);
                String str = MySharedPref.readString(MySharedPref
                        .DefaultCardWordAKey);
                if (str != null && str.length() > 0) {
                    buf.append("\n" + UResourceManager.getStringById(R.string.word_a) + " : " +
                            str);
                }
                str = MySharedPref.readString(MySharedPref
                        .DefaultCardWordBKey);
                if (str != null && str.length() > 0) {
                    buf.append("\n" + UResourceManager.getStringById(R.string.word_b) + " : "
                            +str);
                }
                title = buf.toString();
            }
            break;
//            case AddNgCard:
//            {
//                String str = UResourceManager.getStringById(
//                        MySharedPref.readBoolean(MySharedPref.AddNgCardToBookKey) ?
//                                R.string.option_add_ng_card1 : R.string.option_add_ng_card2);
//
//                if (str != null) {
//                    title = option.title + "\n    " + str;
//                } else {
//                    title = option.title;
//                }
//            }
//            break;
            case StudyMode3:
            {
                String str = UResourceManager.getStringById(
                        MySharedPref.readBoolean(MySharedPref.StudyMode3OptionKey) ?
                                R.string.option_mode3_2 : R.string.option_mode3_3);

                if (str != null) {
                    title = option.title + "\n    " + str;
                } else {
                    title = option.title;
                }
            }
                break;

            case StudyMode4:
            {
                String str = UResourceManager.getStringById(
                        MySharedPref.readBoolean(MySharedPref.StudyMode4OptionKey) ?
                                R.string.option_mode4_4 : R.string.option_mode4_3);

                if (str != null) {
                    title = option.title + "\n    " + str;
                } else {
                    title = option.title;
                }
            }
                break;

            default:
                title = option.title;
                break;
        }
        return title;
    }

    private void closeDialog() {
        if (mDialog != null) {
            mDialog.closeDialog();
            mDialog = null;
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
     * カードのタイトル表示設定のダイアログを表示する
     */
    private void showCardTitleDialog() {
        mDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                this, this,
                UDialogWindow.ButtonDir.Vertical, UDialogWindow.DialogPosType.Center,
                true, mParentView.getWidth(), mParentView.getHeight(),
                Color.BLACK, Color.LTGRAY);
        mDialog.addToDrawManager();
        mDialog.setTitle(UResourceManager.getStringById(R.string.card_name_title));
        mDialog.addButton(ButtonIdCardWordA, UResourceManager.getStringById(R.string.word_a), UColor.BLACK, UColor.White);
        mDialog.addButton(ButtonIdCardWordB, UResourceManager.getStringById(R.string.word_b), Color.BLACK, UColor.White);
        mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
    }

    /**
     * NGカード自動追加ダイアログを表示
     */
//    private void showAddNgCardDialog() {
//        mDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
//                this, this,
//                UDialogWindow.ButtonDir.Vertical, UDialogWindow.DialogPosType.Center,
//                true, mParentView.getWidth(), mParentView.getHeight(),
//                Color.BLACK, Color.LTGRAY);
//        mDialog.addToDrawManager();
//        mDialog.setTitle(UResourceManager.getStringById(R.string.option_add_ng_card_msg));
//        mDialog.addButton(ButtonIdAddNgCardOk, UResourceManager.getStringById(R.string.option_add_ng_card1), UColor
//                .BLACK, UColor.White);
//        mDialog.addButton(ButtonIdAddNgCardNg, UResourceManager.getStringById(R.string.option_add_ng_card2), Color.BLACK, UColor.White);
//        mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
//    }

    /**
     * 学習モード3の不正解カードをどこから選択するかのダイアログを表示
     */
    private void showStudyMode3OptionDialog() {
        mDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                this, this,
                UDialogWindow.ButtonDir.Vertical, UDialogWindow.DialogPosType.Center,
                true, mParentView.getWidth(), mParentView.getHeight(),
                Color.BLACK, Color.LTGRAY);
        mDialog.addToDrawManager();
        mDialog.setTitle(UResourceManager.getStringById(R.string.option_mode3_1));

        // buttons
        UButton button1 = mDialog.addButton(ButtonIdSelectFromAll, UResourceManager.getStringById(R.string.option_mode3_2),
                UColor.BLACK, UColor.White);
        UButton button2 = mDialog.addButton(ButtonIdSelectFromOne, UResourceManager.getStringById(R.string
                .option_mode3_3), Color.BLACK, UColor.White);
        if (MySharedPref.readBoolean(MySharedPref.StudyMode3OptionKey)) {
            button1.setChecked(true);
        } else {
            button2.setChecked(true);
        }

        mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
    }

    /**
     * 学習モード４の単語の並び設定のダイアログを表示
     */
    private void showStudyMode4OptionDialog() {
        mDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                this, this,
                UDialogWindow.ButtonDir.Vertical, UDialogWindow.DialogPosType.Center,
                true, mParentView.getWidth(), mParentView.getHeight(),
                Color.BLACK, Color.LTGRAY);
        mDialog.addToDrawManager();
        mDialog.setTitle(UResourceManager.getStringById(R.string.option_mode4_2));

        // buttons
        UButton button1 = mDialog.addButton(ButtonIdStudySorted, UResourceManager.getStringById(R.string.option_mode4_3), UColor.BLACK, UColor.White);
        UButton button2 = mDialog.addButton(ButtonIdStudyRandom, UResourceManager.getStringById(R.string.option_mode4_4), Color.BLACK, UColor.White);
        if (MySharedPref.readBoolean(MySharedPref.StudyMode4OptionKey)) {
            button2.setChecked(true);
        } else {
            button1.setChecked(true);
        }

        mDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));
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
            case ButtonIdCardWordA:
            case ButtonIdCardWordB: {
                boolean flag = (id == ButtonIdCardWordA) ? false : true;
                MySharedPref.writeBoolean(EditCardNameKey, flag);

                // アイテムのテキストを更新
                ListItemOption item = (ListItemOption) mListView.get(OptionItems.CardTitle.ordinal());
                item.setTitle(getItemTitle(OptionItems.CardTitle));
                if (mDialog != null) {
                    mDialog.closeDialog();
                    mDialog = null;
                }
            }
            break;
            // NG単語帳に自動追加は未実装
//            case ButtonIdAddNgCardOk:
//            case ButtonIdAddNgCardNg: {
//                boolean flag = (id == ButtonIdAddNgCardOk) ? true : false;
//                MySharedPref.writeBoolean(MySharedPref.AddNgCardToBookKey, flag);
//
//                // アイテムのテキストを更新
//                ListItemOption item = (ListItemOption) mListView.get(OptionItems.AddNgCard.ordinal());
//                item.setTitle(getItemTitle(OptionItems.AddNgCard));
//
//                if (mDialog != null) {
//                    mDialog.closeDialog();
//                    mDialog = null;
//                }
//            }
//                break;
            case ButtonIdSelectFromAll:
            case ButtonIdSelectFromOne: {
                boolean flag = (id == ButtonIdSelectFromAll) ? true : false;
                MySharedPref.writeBoolean(MySharedPref.StudyMode3OptionKey, flag);

                // アイテムのテキストを更新
                ListItemOption item = (ListItemOption) mListView.get(OptionItems.StudyMode3
                        .ordinal());
                item.setTitle(getItemTitle(OptionItems.StudyMode3));

                if (mDialog != null) {
                    mDialog.closeDialog();
                    mDialog = null;
                }
            }
                break;

            case ButtonIdStudySorted:
            case ButtonIdStudyRandom: {
                boolean flag = (id == ButtonIdStudyRandom) ? true : false;
                MySharedPref.writeBoolean(MySharedPref.StudyMode4OptionKey, flag);

                // アイテムのテキストを更新
                ListItemOption item = (ListItemOption) mListView.get(OptionItems.StudyMode4.ordinal());
                item.setTitle(getItemTitle(OptionItems.StudyMode4));

                if (mDialog != null) {
                    mDialog.closeDialog();
                    mDialog = null;
                }
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
                showCardTitleDialog();
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
            // NGカード自動追加は未実装
//            case AddNgCard:
//                if (mDialog != null) {
//                    mDialog.closeDialog();
//                    mDialog = null;
//                }
//                showAddNgCardDialog();
//                break;
            case StudyMode3:
                if (mDialog != null) {
                    mDialog.closeDialog();
                    mDialog = null;
                }
                showStudyMode3OptionDialog();
                break;
            case StudyMode4:
                if (mDialog != null) {
                    mDialog.closeDialog();
                    mDialog = null;
                }
                showStudyMode4OptionDialog();
                break;
        }
    }

    public void ListItemButtonClicked(UListItem item, int buttonId) {

    }

    /**
     * OptionColorDialogCallbacks
     */
    public void submitOptionColor(Bundle args) {
        mParentView.invalidate();
    }
    public void cancelOptionColor() {

    }

    /**
     * DefaultNameDialogCallbacks
     */
    public void submitDefaultName(Bundle args) {
        int fragment_type = 0;
        if (args != null) {
            fragment_type = args.getInt(DefaultBookNameFragment.KEY_FRAGMENT_TYPE);
        }

        // アイテムのテキストを更新
        OptionItems option = (fragment_type == DefaultBookNameFragment.FragmentType) ?
                OptionItems.DefaultNameBook : OptionItems.DefaultNameCard;

        ListItemOption item = (ListItemOption) mListView.get(option.ordinal());
        if (item != null) {
            item.setTitle(getItemTitle(option));
            mParentView.invalidate();
        }
    }
    public void cancelDefaultName() {
    }

}
