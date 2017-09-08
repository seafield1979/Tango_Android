package com.sunsunsoft.shutaro.tangobook.page;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.sunsunsoft.shutaro.tangobook.icon.IconSortMode;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.database.*;
import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;
import com.sunsunsoft.shutaro.tangobook.listview.ListItemStudyBook;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonText;
import com.sunsunsoft.shutaro.tangobook.uview.text.UTextView;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;
import com.sunsunsoft.shutaro.tangobook.uview.window.UWindow;
import com.sunsunsoft.shutaro.tangobook.uview.window.UWindowCallbacks;

import java.util.List;

/**
 * Created by shutaro on 2016/12/16.
 *
 * 学習する単語帳を選択するページ(リストビュー版)
 */

public class PageViewStudyBookSelect extends UPageView
        implements UButtonCallbacks, UListItemCallbacks, UWindowCallbacks {
    /**
     * Enums
     */

    /**
     * Constants
     */
    private static final int DRAW_PRIORITY = 100;

    private static final int MARGIN_H = 17;
    private static final int MARGIN_V_S = 10;

    private static final int TEXT_SIZE = 17;

    // Button Ids
    private static final int ButtonIdReturn = 100;

    // 開始ダイアログ(PreStudyWindow)でボタンが押されたときに使用する
    public static final int ButtonIdStartStudy = 2001;
    public static final int ButtonIdCancel = 2002;

    /**
     * Member variables
     */
    private UTextView mTitleText;
    private UListView mListView;
    private TangoBook mBook;
    private IconSortMode mSortMode;

    // 学習開始前のオプション等を選択するダイアログ
    private PreStudyWindow mPreStudyWindow;

    /**
     * Constructor
     */
    public PageViewStudyBookSelect(Context context, View parentView, String title) {
        super(context, parentView, title);

        mSortMode = IconSortMode.toEnum(MySharedPref.readInt(MySharedPref
                .StudyBookSortKey));
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
        float y = UDpi.toPixel(MARGIN_V_S);

        // ListViewにアイテムを追加
        List<TangoItem> books = RealmManager.getItemPosDao().selectItemsByParentTypeWithSort(
                TangoParentType.Home, 0, TangoItemType.Book,
                mSortMode, true);

        if (books == null || books.size() == 0) {
            // リストが空
            mListView = null;
            y += UDpi.toPixel(67);
            UTextView text = UTextView.createInstance(UResourceManager.getStringById(R.string.study_list_is_empty1),
                    UDpi.toPixel(TEXT_SIZE), DRAW_PRIORITY - 1,
                    UAlignment.CenterX, width, false, false,
                    width / 2, y, width, Color.BLACK, 0);
            text.addToDrawManager();

        } else {
            // Title
            mTitleText = UTextView.createInstance(UResourceManager.getStringById(R.string
                            .title_study2),
                    UDpi.toPixel(TEXT_SIZE), DRAW_PRIORITY,
                    UAlignment.CenterX, width, false, false,
                    width / 2, y, width, Color.BLACK, 0);
            mTitleText.addToDrawManager();
            y += mTitleText.getHeight() + UDpi.toPixel(MARGIN_V_S);

            // ListView
            int listViewH = height - (UDpi.toPixel(MARGIN_V_S) * 3 + mTitleText.getHeight());
            mListView = new UListView(null, this, DRAW_PRIORITY, x, y,
                    width - UDpi.toPixel(MARGIN_H) * 2, listViewH, 0);
            mListView.setFrameColor(Color.BLACK);
            mListView.addToDrawManager();

            for (TangoItem book : books) {
                TangoBook _book = (TangoBook) book;
                ListItemStudyBook listItem = new ListItemStudyBook(this, _book, mListView.getWidth(),
                        Color.WHITE);
                mListView.add(listItem);
            }
            // スクロールバー等のサイズを更新
            mListView.updateWindow();

            y += listViewH + UDpi.toPixel(MARGIN_V_S);

            // PreStudyWindow 学習開始前に設定を行うウィンドウ
            mPreStudyWindow = new PreStudyWindow(this, this, mParentView);
            mPreStudyWindow.addToDrawManager();
        }
    }

    /**
     * アクションIDを処理する
     * サブクラスでオーバーライドして使用する
     */
    public void setActionId(int id) {

        switch (id) {
            case R.id.action_sort_none:
                mSortMode = IconSortMode.None;
                break;
            case R.id.action_sort_word_asc:
                mSortMode = IconSortMode.TitleAsc;
                break;
            case R.id.action_sort_word_desc:
                mSortMode = IconSortMode.TitleDesc;
                break;
            case R.id.action_sort_time_asc:
                mSortMode = IconSortMode.CreateTimeAsc;
                break;
            case R.id.action_sort_time_desc:
                mSortMode = IconSortMode.CreateTimeDesc;
                break;
            case R.id.action_sort_studied_time_asc:
                mSortMode = IconSortMode.StudiedTimeAsc;
                break;
            case R.id.action_sort_studied_time_desc:
                mSortMode = IconSortMode.StudiedTimeDesc;
                break;
            default:
                return;
        }
        MySharedPref.writeInt(MySharedPref.StudyBookSortKey, mSortMode.ordinal());
        isFirst = true;
        mParentView.invalidate();
    }

    /**
     * ソフトウェアキーの戻るボタンを押したときの処理
     * @return
     */
    public boolean onBackKeyDown() {
        if (mPreStudyWindow != null) {
            if (mPreStudyWindow.onBackKeyDown()) {
                return true;
            }
            else if (mPreStudyWindow.isShow()) {
                mPreStudyWindow.setShow(false);
                return true;
            }
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
            case ButtonIdStartStudy:
                // 学習開始
                PageViewManager.getInstance().startStudyPage( mBook, true);
                break;
            case ButtonIdCancel:
                mPreStudyWindow.setShow(false);
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
        // 学習開始前のダイアログを表示する
        if (!(item instanceof ListItemStudyBook)) return;

        ListItemStudyBook bookItem = (ListItemStudyBook)item;

        mPreStudyWindow.showWithBook(bookItem.getBook());
        mBook = bookItem.getBook();
    }
    public void ListItemButtonClicked(UListItem item, int buttonId) {

    }

    /**
     * UWindowCallbacks
     */
    public void windowClose(UWindow window) {
        // Windowを閉じる
        if (mPreStudyWindow == window) {
            mPreStudyWindow.setShow(false);
        }
    }
}
