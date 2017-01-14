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
    StudyBookSelect,    // 学習する単語帳を選択する
    StudySlide,         // 単語帳学習(カードスライド式)
    StudySelect4,       // 単語帳学習(４択)
    StudyInputCorrect,  // 単語帳学習(正解文字入力)
    StudyResult,        // 単語帳結果
    History,            // 履歴
    Settings,           // 設定
    BackupDB,           // バックアップ
    PresetBook,         // プリセット単語帳選択
    SearchCard,         // カード検索
    Help,               // ヘルプ
    License,            // ライセンス表示
    Debug,               // Debug
    DebugDB             // Debug DB(Realm)
    ;
}

public class PageViewManager extends UPageViewManager{
    /**
     * Constructor
     */
    // Singletonオブジェクト
    private static PageViewManager singleton;

    // Singletonオブジェクトを作成する
    public static PageViewManager createInstance(Context context, View parentView) {
        singleton = new PageViewManager(context, parentView);
        return singleton;
    }
    public static PageViewManager getInstance() { return singleton; }

    private PageViewManager(Context context, View parentView) {
        mContext = context;
        mParentView = parentView;

        // 最初に表示するページ
        stackPage(PageView.Title);
    }

    /**
     * 配下のページを追加する
     */
    public void initPage(PageView pageView) {
        UPageView page = null;

        switch(pageView) {
            case Title:              // タイトル画面
                page = new PageViewTitle(mContext, mParentView,
                        UResourceManager.getStringById(R.string.app_title));
                break;
            case Edit:               // 単語帳を編集
                page = new PageViewTangoEdit(mContext, mParentView,
                        UResourceManager.getStringById(R.string.title_edit));
                break;
            case StudyBookSelect:     // 学習する単語帳を選択する
                page = new PageViewStudyBookSelect(mContext, mParentView,
                        UResourceManager.getStringById(R.string.title_study_select));
                break;
            case StudySlide:          // 単語帳学習
                page = new PageViewStudySlide(mContext, mParentView,
                        UResourceManager.getStringById(R.string.title_studying_slide));
                break;
            case StudySelect4:
                page = new PageViewStudySelect4(mContext, mParentView,
                        UResourceManager.getStringById(R.string.title_studying_select));
                break;
            case StudyInputCorrect:
                page = new PageViewStudyInputCorrect(mContext, mParentView,
                        UResourceManager.getStringById(R.string.title_studying_input_correct));
                break;
            case StudyResult:        // 単語帳結果
                page = new PageViewResult(mContext, mParentView,
                        UResourceManager.getStringById(R.string.title_result));
                break;
            case History:            // 履歴
                page = new PageViewHistory(mContext, mParentView,
                        UResourceManager.getStringById(R.string.title_history));
                break;
            case Settings:           // 設定
                page = new PageViewSettingsTop(mContext, mParentView,
                        UResourceManager.getStringById(R.string.title_settings));
                break;
            case BackupDB:           // バックアップ
                page = new PageViewBackupDB(mContext, mParentView,
                        UResourceManager.getStringById(R.string.title_backup));
                break;
            case PresetBook:         // プリセット単語帳選択
                page = new PageViewPresetBook(mContext, mParentView,
                        UResourceManager.getStringById(R.string.title_preset_book));
                break;
            case SearchCard:         // カード検索
                page = new PageViewSearchCard(mContext, mParentView,
                        UResourceManager.getStringById(R.string.title_search_card));
                break;
            case Help:               // ヘルプ
                page = new PageViewHelp(mContext, mParentView,
                        UResourceManager.getStringById(R.string.title_help));
                break;
            case License:            // ライセンス
                page = new PageViewLicense(mContext, mParentView,
                        UResourceManager.getStringById(R.string.license));
                break;
            case Debug:               // Debug
                page = new PageViewDebug(mContext, mParentView, "Debug");
                break;
            case DebugDB:             // Debug DB(Realm)
                page = new PageViewDebugDB(mContext, mParentView, "DebugDB");
                break;
        }
        if (page != null) {
            pages[pageView.ordinal()] = page;
        }
    }


    /**
     * 学習ページを表示開始
     * 他のページと異なり引数を受け取る必要があるため関数化
     * @param book
     * @param firstStudy trueならリトライでない学習
     */
    public void startStudyPage(TangoBook book, boolean firstStudy) {

        switch( MySharedPref.getStudyMode()) {
            case SlideOne:
            case SlideMulti:
            {
                PageViewStudySlide page = (PageViewStudySlide)getPageView(PageView.StudySlide);
                page.setBook(book);
                page.setFirstStudy(firstStudy);
                stackPage(PageView.StudySlide);
            }
                break;
            case Choice4:
            {
                PageViewStudySelect4 page = (PageViewStudySelect4)getPageView(PageView
                        .StudySelect4);
                page.setBook(book);
                page.setFirstStudy(firstStudy);
                stackPage(PageView.StudySelect4);
            }
                break;
            case Input:
            {
                PageViewStudyInputCorrect page = (PageViewStudyInputCorrect)getPageView(PageView
                        .StudyInputCorrect);
                page.setBook(book);
                page.setFirstStudy(firstStudy);
                stackPage(PageView.StudyInputCorrect);
            }
                break;
        }

    }

    /**
     * 学習ページを表示開始(リトライ時)
     * @param book
     * @param cards  リトライで学習するカード
     */
    public void startStudyPage(TangoBook book, List<TangoCard> cards, boolean stack) {

        PageView pageView = null;
        switch( MySharedPref.getStudyMode()) {
            case SlideOne:
            case SlideMulti: {
                pageView = PageView.StudySlide;
                PageViewStudySlide page = (PageViewStudySlide) getPageView(pageView);
                page.setBook(book);
                page.setCards(cards);

                if (stack) {
                    stackPage(pageView);
                } else {
                    changePage(pageView);
                }
            }
                break;
            case Choice4: {
                pageView = PageView.StudySelect4;
                PageViewStudySelect4 page = (PageViewStudySelect4) getPageView(pageView);
                page.setBook(book);
                page.setCards(cards);

                if (stack) {
                    stackPage(pageView);
                } else {
                    changePage(pageView);
                }
            }
                break;
            case Input: {
                pageView = PageView.StudyInputCorrect;
                PageViewStudyInputCorrect page = (PageViewStudyInputCorrect) getPageView(pageView);
                page.setBook(book);
                page.setCards(cards);

                if (stack) {
                    stackPage(pageView);
                } else {
                    changePage(pageView);
                }
            }
                break;
        }

        if (pageView != null) {

        }
    }

    /**
     * リザルトページを開始
     * 他のページと異なり引数を受け取る必要があるため関数化
     */
    public void startStudyResultPage(TangoBook book, List<TangoCard> okCards, List<TangoCard> ngCards) {
        PageViewResult page = (PageViewResult)getPageView(PageView.StudyResult);
        page.setBook(book);
        page.setCardsLists(okCards, ngCards);
        changePage(PageView.StudyResult);
    }

    /**
     * Callbacks
     */

}
