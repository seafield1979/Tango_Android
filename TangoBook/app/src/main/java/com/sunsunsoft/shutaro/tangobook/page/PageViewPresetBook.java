package com.sunsunsoft.shutaro.tangobook.page;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.preset.*;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.uview.UListView;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.uview.text.UTextView;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;
import com.sunsunsoft.shutaro.tangobook.listview.ListItemCard;
import com.sunsunsoft.shutaro.tangobook.listview.ListItemPresetBook;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;
import com.sunsunsoft.shutaro.tangobook.uview.window.UDialogCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.window.UDialogWindow;

import java.util.List;

/**
 * Created by shutaro on 2016/12/18.
 *
 * プリセット単語帳リストを表示、追加するページ
 */

public class PageViewPresetBook extends UPageView
        implements UButtonCallbacks, UListItemCallbacks, UDialogCallbacks {
    /**
     * Constants
     */
    private static final int DRAW_PRIORITY = 100;
    private static final int DRAW_PRIORYTY_DIALOG = 50;

    private static final int TOP_Y = 17;
    private static final int MARGIN_H = 17;
    private static final int MARGIN_V = 17;

    // button id
    private static final int ButtonIdReturn = 100;
    private static final int ButtonIdAddOk = 200;
    private static final int ButtonIdAddOk2 = 201;

    /**
     * Member variables
     */
    private UTextView mTitleText;
    private UListView mListView;
    private UDialogWindow mDialog;      // OK/NGのカード一覧を表示するダイアログ
    private PresetBook mBook;

    // 終了確認ダイアログ
    private UDialogWindow mConfirmDialog;
    private UDialogWindow mMessageDialog;

    /**
     * Constructor
     */
    public PageViewPresetBook(Context context, View parentView, String title) {
        super(context, parentView, title);

        PresetBookManager.getInstance().makeBookList();
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

        float x = UDpi.toPixel(MARGIN_H);
        float y = UDpi.toPixel(TOP_Y);

        // Title
        mTitleText = UTextView.createInstance(UResourceManager.getStringById(R.string
                        .preset_title2),
                UDraw.getFontSize(FontSize.L), DRAW_PRIORITY,
                UAlignment.CenterX, width, true, false,
                width / 2, y, width, Color.BLACK, 0);
        mTitleText.addToDrawManager();
        y += mTitleText.getSize().height + UDpi.toPixel(MARGIN_V);

        // ListView
        int listViewH = height - (UDpi.toPixel(MARGIN_H) * 3 + mTitleText.getSize().height);
        mListView = new UListView(null, this, DRAW_PRIORITY, x, y,
                width - UDpi.toPixel(MARGIN_H) * 2, listViewH, 0);
        mListView.setFrameColor(Color.BLACK);
        mListView.addToDrawManager();

        // add items to ListView
        List<PresetBook> presetBooks = PresetBookManager.getInstance().getBooks();
        for (PresetBook presetBook : presetBooks) {
            ListItemPresetBook item = new ListItemPresetBook(this, presetBook, mListView.getClientSize().width);
            mListView.add(item);
        }
        mListView.updateWindow();
    }

    /**
     * ダイアログを表示する
     * @param book
     */
    private void showDialog(PresetBook book) {

        int width = mParentView.getWidth();
        // Dialog
        mDialog = UDialogWindow.createInstance(null, null,
                UDialogWindow.ButtonDir.Horizontal, width, mParentView
                .getHeight());
        mDialog.addToDrawManager();

        // Title
        mDialog.setTitle(book.mName);

        // ListView
        UListView listView = new UListView(null, this,
                DRAW_PRIORYTY_DIALOG, 0, 0,
                mDialog.getSize().width, mParentView.getHeight() - UDpi.toPixel(117), Color.LTGRAY
        );
        mDialog.addDrawable(listView);

        // Add items to ListView
        for (PresetCard presetCard : book.getCards()) {
            ListItemCard itemCard = new ListItemCard(null, presetCard, listView.getClientSize().width);
            listView.add(itemCard);
        }
        listView.updateWindow();

        mDialog.addCloseButton(UResourceManager.getStringById(R.string.close));
    }

    private void showMessageDialog() {
        if (mMessageDialog == null) {
            mMessageDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                    this, this,
                    UDialogWindow.ButtonDir.Horizontal, UDialogWindow.DialogPosType.Center,
                    true, mParentView.getWidth(), mParentView.getHeight(),
                    Color.BLACK, Color.LTGRAY);
            mMessageDialog.addToDrawManager();
            String title = String.format(UResourceManager.getStringById(R.string.confirm_add_book2), mBook.mName);
            mMessageDialog.setTitle(title);
            mMessageDialog.addButton(ButtonIdAddOk2, "OK", Color.BLACK, Color.WHITE);
        }
    }

    /**
     * ソフトウェアキーの戻るボタンを押したときの処理
     * @return
     */
    public boolean onBackKeyDown() {
        if (mDialog != null && mDialog.onBackKeyDown()) {
            return true;
        }
        if (mConfirmDialog != null && mConfirmDialog.onBackKeyDown()) {
            return true;
        }
        if (mMessageDialog != null && mMessageDialog.onBackKeyDown()) {
            return true;
        }
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
            case ButtonIdAddOk: {
                // プリセットを追加するかの確認ダイアログでOKボタンを押した
                if (mBook != null) {
                    PresetBookManager.getInstance().addBookToDB(mBook);
                }
                mConfirmDialog.closeDialog();
                showMessageDialog();
            }
                break;
            case ButtonIdAddOk2:
                mMessageDialog.closeDialog();
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
        // クリックされた項目の学習カード一覧を表示する
        if (!(item instanceof ListItemPresetBook)) return;

        ListItemPresetBook book = (ListItemPresetBook) item;
        showDialog(book.getBook());
    }

    /**
     * 項目のボタンがクリックされた
     * @param item
     * @param buttonId
     */
    public void ListItemButtonClicked(UListItem item, int buttonId) {
        if (!(item instanceof ListItemPresetBook)) return;

        switch(buttonId) {
            case ListItemPresetBook.ButtonIdAdd: {
                ListItemPresetBook book = (ListItemPresetBook)item;

                // 追加するかを確認する
                // 終了ボタンを押したら確認用のモーダルダイアログを表示
                if (mConfirmDialog == null) {
                    mConfirmDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                            this, this,
                            UDialogWindow.ButtonDir.Horizontal, UDialogWindow.DialogPosType.Center,
                            true, mParentView.getWidth(), mParentView.getHeight(),
                            Color.BLACK, Color.LTGRAY);
                    mConfirmDialog.addToDrawManager();
                    String title = String.format(UResourceManager.getStringById(R.string.confirm_add_book), book.getBook().mName);
                    mConfirmDialog.setTitle(title);
                    mConfirmDialog.addButton(ButtonIdAddOk, "OK", Color.BLACK, Color.WHITE);
                    mConfirmDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));

                    // クリックされた項目のBookを記憶しておく
                    mBook = book.getBook();
                }
            }
                break;
        }
    }

    /**
     * UDialogCallbacks
     */
    public void dialogClosed(UDialogWindow dialog) {
        if (dialog == mConfirmDialog) {
            mConfirmDialog = null;
        }
        else if (dialog == mMessageDialog) {
            mMessageDialog = null;
        }
    }
}