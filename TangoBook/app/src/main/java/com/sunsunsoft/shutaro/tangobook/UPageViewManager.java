package com.sunsunsoft.shutaro.tangobook;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

enum PageView {
    Title,              // タイトル画面
    Edit,          // 単語帳を編集
    StudySelect,        // 学習する単語帳を選択する
    Study,         // 単語帳学習
    StudyResult,        // 単語帳結果
    Settings,
    ;
}

/**
 * Created by shutaro on 2016/12/05.
 *
 * 各ページを管理するクラス
 * 現在のページ番号を元に配下の PageView の処理を呼び出す
 */

public class UPageViewManager {
    /**
     * Enums
     */
    /**
     * Consts
     */

    /**
     * Member Variables
     */
    private Context mContext;
    private View mParentView;
    private UPageView[] pages = new UPageView[PageView.values().length];
    private LinkedList<PageView> pageIdStack = new LinkedList<>();

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    // Singletonオブジェクト
    private static UPageViewManager singleton;

    // Singletonオブジェクトを作成する
    public static UPageViewManager createInstance(Context context, View parentView) {
        if (singleton == null) {
            singleton = new UPageViewManager(context, parentView);
        }
        return singleton;
    }
    public static UPageViewManager getInstance() { return singleton; }

    private UPageViewManager(Context context, View parentView) {
        mContext = context;
        mParentView = parentView;

        initPages();
    }

    /**
     * Methods
     */
    /**
     * カレントのページIDを取得する
     * @return カレントページID
     */
    public PageView currentPage() {
        if (pageIdStack.size() > 0) {
            return pageIdStack.getLast();
        }
        return null;
    }

    /**
     * 配下のページを追加する
     */
    public void initPages() {
        UPageView page;
        // Title
        page = new PageViewTitle(mContext, mParentView);
        pages[PageView.Title.ordinal()] = page;

        // Edit
        page = new PageViewTangoEdit(mContext, mParentView);
        pages[PageView.Edit.ordinal()] = page;

        // StudySelect
        page = new PageViewStudySelect(mContext, mParentView);
        pages[PageView.StudySelect.ordinal()] = page;

        // Study
        page = new PageViewStudy(mContext, mParentView);
        pages[PageView.Study.ordinal()] = page;

        // TangoResult
        page = new PageViewResult(mContext, mParentView);
        pages[PageView.StudyResult.ordinal()] = page;

        // Settings
        page = new PageViewSettings(mContext, mParentView);
        pages[PageView.Settings.ordinal()] = page;

        // 最初に表示するページ
        stackPage(PageView.Title);
    }

    /**
     * 描画処理
     * 配下のUViewPageの描画処理を呼び出す
     * @param canvas
     * @param paint
     * @return
     */
    public boolean draw(Canvas canvas, Paint paint) {
        PageView pageId = currentPage();
        if (pageId == null) return false;

        return pages[pageId.ordinal()].draw(canvas, paint);
    }

    /**
     * バックキーが押されたときの処理
     * @return
     */
    public boolean onBackKeyDown() {
        // スタックをポップして１つ前の画面に戻る
        PageView pageId = currentPage();
        if (pageId == null) return false;

        // 各ページで処理
        if (pages[pageId.ordinal()].onBackKeyDown()) {
            // 何かしら処理がされたら何もしない
            return true;
        }

        // スタックを１つポップする
        if (pageIdStack.size() > 1) {
            if (popPage()) {
                return true;
            }
        }
        // スタックのページが１つだけなら終了
        return false;
    }

    /**
     * 表示ページを切り替える
     * @param pageId
     */
    public void changePage(PageView pageId) {
        if (pageIdStack.size() > 0) {
            // 古いページの後処理(onHide)
            PageView page = pageIdStack.getLast();
            pages[page.ordinal()].onHide();

            pageIdStack.removeLast();

        }
        pageIdStack.add(pageId);

        // 新しいページの前処理(onShow)
        pageId = pageIdStack.getLast();
        pages[pageId.ordinal()].onShow();
    }

    /**
     * ページをスタックする
     * ソフトウェアキーの戻るボタンを押すと元のページに戻れる
     * @param pageId
     */
    public void stackPage(PageView pageId) {

        // 古いページの後処理
        if (pageIdStack.size() > 0) {
            PageView page = pageIdStack.getLast();
            pages[page.ordinal()].onHide();
        }

        pageIdStack.add(pageId);
        if (pages[pageId.ordinal()] != null) {
            pages[pageId.ordinal()].onShow();
        }
    }

    /**
     * ページをポップする
     * 下にページがあったら移動
     */
    public boolean popPage() {
        if (pageIdStack.size() > 0) {
            // 古いページの後処理
            PageView page = pageIdStack.getLast();
            pages[page.ordinal()].onHide();

            pageIdStack.removeLast();

            // 新しいページの前処理
            page = pageIdStack.getLast();
            pages[page.ordinal()].onShow();

            return true;
        }
        return false;
    }

    /**
     * 学習ページを表示開始
     * 他のページと異なり引数を受け取る必要があるため関数化
     * @param book
     */
    public void startStudyPage(TangoBook book) {
        PageViewStudy page = (PageViewStudy)pages[PageView.Study.ordinal()];
        page.setBook(book);
        stackPage(PageView.Study);
    }

    /**
     * リザルトページを開始
     * 他のページと異なり引数を受け取る必要があるため関数化
     */
    public static void StartStudyResultPage(TangoBook book, List<TangoCard> okCards, List<TangoCard> ngCards) {
        UPageViewManager instance = getInstance();
        instance.startStudyResultPage(book, okCards, ngCards);
    }

    public void startStudyResultPage(TangoBook book, List<TangoCard> okCards, List<TangoCard> ngCards) {
        PageViewResult page = (PageViewResult)pages[PageView.StudyResult.ordinal()];
        page.setBook(book);
        page.setCardsLists(okCards, ngCards);
        changePage(PageView.StudyResult);
    }

    /**
     * Callbacks
     */

}
