package com.sunsunsoft.shutaro.tangobook.page;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.csv.CsvParser;
import com.sunsunsoft.shutaro.tangobook.preset.*;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.util.FileDialog;
import com.sunsunsoft.shutaro.tangobook.util.FilePathType;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.util.UUtil;
import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.listview.ListItemCard;
import com.sunsunsoft.shutaro.tangobook.listview.ListItemPresetBook;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.text.UTextView;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;
import com.sunsunsoft.shutaro.tangobook.uview.window.UDialogCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.window.UDialogWindow;

import java.io.File;
import java.util.List;

/**
 * Created by shutaro on 2017/01/20.
 *
 * CSVから単語帳を追加するページ
 * 検索パス以下にあるcsvを見つけてListViewに表示する
 * このListViewの項目を選択して追加
 */

public class PageViewCsvBook extends UPageView
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
    private FileDialog mFileDialog;      // ファイルを選択するモーダルダイアログ
    private PresetBook mBook;

    // 終了確認ダイアログ
    private UDialogWindow mConfirmDialog;
    private UDialogWindow mMessageDialog;

    // Dpi計算済み
    private int marginH;

    /**
     * Constructor
     */
    public PageViewCsvBook(Context context, View parentView, String title) {
        super(context, parentView, title);

        marginH = UDpi.toPixel(MARGIN_H);
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

        float x = marginH;
        float y = UDpi.toPixel(TOP_Y);

        // Title
        mTitleText = UTextView.createInstance(UResourceManager.getStringById(R.string
                        .csv_title2),
                UDraw.getFontSize(FontSize.L), DRAW_PRIORITY,
                UAlignment.CenterX, width, true, false,
                width / 2, y, width, Color.BLACK, 0);
        mTitleText.addToDrawManager();
        y += mTitleText.getHeight() + MARGIN_V;

        // ListView
        int listViewH = height - (marginH * 3 + mTitleText.getSize().height);
        mListView = new UListView(null, this, DRAW_PRIORITY, x, y,
                width - marginH * 2, listViewH, 0);
        mListView.setFrameColor(Color.BLACK);
        mListView.addToDrawManager();

        // add items to ListView
        List<PresetBook> books = PresetBookManager.getInstance().getCsvBookList();
        if (books != null) {
            for (PresetBook csvBook : books) {
                ListItemPresetBook item = new ListItemPresetBook(this, csvBook, mListView.getClientSize().width);
                mListView.add(item);
            }
        }
        mListView.updateWindow();

        y += listViewH + marginH;
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

    /**
     * csvファイルから単語帳を追加完了した時に表示するダイアログ
     */
    private void showMessageDialog(String title) {
        if (mMessageDialog == null) {
            mMessageDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                    this, this,
                    UDialogWindow.ButtonDir.Horizontal, UDialogWindow.DialogPosType.Center,
                    true, mParentView.getWidth(), mParentView.getHeight(),
                    Color.BLACK, Color.LTGRAY);
            mMessageDialog.addToDrawManager();
            mMessageDialog.setTitle(title);
            mMessageDialog.addButton(ButtonIdAddOk2, "OK", Color.BLACK, UColor.OkButton);
        }
    }

    /**
     * リストをクリックした後に、CSVファイルを追加するかの確認ダイアログを表示する
     * @param book
     */
    private void showConfirmAddDialog(PresetBook book) {
        // 追加するかを確認する
        // 終了ボタンを押したら確認用のモーダルダイアログを表示
        if (mConfirmDialog == null) {
            mConfirmDialog = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                    this, this,
                    UDialogWindow.ButtonDir.Horizontal, UDialogWindow.DialogPosType.Center,
                    true, mParentView.getWidth(), mParentView.getHeight(),
                    Color.BLACK, Color.LTGRAY);
            mConfirmDialog.addToDrawManager();
            String title = String.format(UResourceManager.getStringById(R.string.confirm_add_book), book.mName);
            mConfirmDialog.setTitle(title);
            mConfirmDialog.addButton(ButtonIdAddOk, "OK", Color.BLACK, UColor.OkButton);
            mConfirmDialog.addCloseButton(UResourceManager.getStringById(R.string.cancel));

            // クリックされた項目のBookを記憶しておく
            mBook = book;
        }
    }

    /**
     * xmlファイルを選択するためのダイアログを表示する
     */
    private void selectImportCsvFile() {
        File mPath = UUtil.getPath(mContext, FilePathType.ExternalDocument);
        mFileDialog = new FileDialog((Activity)mContext, mPath, ".csv");

        // ファイルを選択
        mFileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                if (file != null) {
                    addBookFromCsvFile(file);
                    mParentView.invalidate();
                }
            }
        });

        mFileDialog.showDialog();
    }

    /**
     * CSVファイルから単語帳を追加する
     * @param csvfile
     * @return false:追加失敗 / true:追加成功
     */
    private boolean addBookFromCsvFile(File csvfile) {
        PresetBook book = CsvParser.getFileBook(mContext, csvfile, true);
        if (book == null) {
            showMessageDialog(UResourceManager.getStringById(R.string.failed_import));
            return false;
        }
        // データベースに追加
        PresetBookManager.getInstance().addBookToDB(book);
        showConfirmAddDialog(book);

        return true;
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
     * アクションIDを処理する
     * サブクラスでオーバーライドして使用する
     */
    public void setActionId(int id) {
        switch (id) {
            case R.id.action_select_csv_file: {
                selectImportCsvFile();
            }
            break;
        }
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

                String title = String.format(UResourceManager.getStringById(R.string.confirm_add_book2), mBook.mName);
                showMessageDialog(title);
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
                    showConfirmAddDialog(book.getBook());
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