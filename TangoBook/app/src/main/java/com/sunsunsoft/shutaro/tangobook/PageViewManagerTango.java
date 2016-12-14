package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.view.View;

import java.util.List;

/**
 * Created by shutaro on 2016/12/14.
 */


enum PageView {
    Title,              // タイトル画面
    Edit,               // 単語帳を編集
    StudySelect,        // 学習する単語帳を選択する
    Study,              // 単語帳学習
    StudyResult,        // 単語帳結果
    History,            // 履歴
    Settings,           // 設定
    Help,               // ヘルプ
    ;
}

public class PageViewManagerTango extends UPageViewManager{
    /**
     * Constructor
     */
    // Singletonオブジェクト
    private static PageViewManagerTango singleton;

    // Singletonオブジェクトを作成する
    public static PageViewManagerTango createInstance(Context context, View parentView) {
        if (singleton == null) {
            singleton = new PageViewManagerTango(context, parentView);
        }
        return singleton;
    }
    public static PageViewManagerTango getInstance() { return singleton; }

    private PageViewManagerTango(Context context, View parentView) {
        mContext = context;
        mParentView = parentView;

        initPages();
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

        // History
        page = new PageViewHistory(mContext, mParentView);
        pages[PageView.History.ordinal()] = page;

        // Settings
        page = new PageViewSettings(mContext, mParentView);
        pages[PageView.Settings.ordinal()] = page;

        // Help
        page = new PageViewHelp(mContext, mParentView);
        pages[PageView.Help.ordinal()] = page;

        // 最初に表示するページ
        stackPage(PageView.Title);
    }


    /**
     * 学習ページを表示開始
     * 他のページと異なり引数を受け取る必要があるため関数化
     * @param book
     * @param firstStudy trueならリトライでない学習
     */
    public void startStudyPage(TangoBook book, boolean firstStudy) {
        PageViewStudy page = (PageViewStudy)pages[PageView.Study.ordinal()];
        page.setBook(book);
        page.setFirstStudy(firstStudy);
        stackPage(PageView.Study);
    }

    /**
     * 学習ページを表示開始(リトライ時)
     * @param book
     * @param cards  リトライで学習するカード
     */
    public void startStudyPage(TangoBook book, List<TangoCard> cards, boolean stack) {
        PageViewStudy page = (PageViewStudy)pages[PageView.Study.ordinal()];
        page.setBook(book);
        page.setCards(cards);
        if (stack) {
            stackPage(PageView.Study);
        } else {
            changePage(PageView.Study);
        }
    }

    /**
     * リザルトページを開始
     * 他のページと異なり引数を受け取る必要があるため関数化
     */
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
