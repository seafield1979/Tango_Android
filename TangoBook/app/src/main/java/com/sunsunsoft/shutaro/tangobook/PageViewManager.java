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
            case StudySelect:        // 学習する単語帳を選択する
                page = new PageViewStudySelect2(mContext, mParentView,
                        UResourceManager.getStringById(R.string.title_study_select));
                break;
            case Study:              // 単語帳学習
                page = new PageViewStudy(mContext, mParentView,
                        UResourceManager.getStringById(R.string.title_studying));
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
        PageViewStudy page = (PageViewStudy)getPageView(PageView.Study);
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
        PageViewStudy page = (PageViewStudy)getPageView(PageView.Study);
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
        PageViewResult page = (PageViewResult)getPageView(PageView.StudyResult);
        page.setBook(book);
        page.setCardsLists(okCards, ngCards);
        changePage(PageView.StudyResult);
    }

    /**
     * Callbacks
     */

}
