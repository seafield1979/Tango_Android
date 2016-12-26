package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by shutaro on 2016/12/18.
 *
 * プリセットの単語帳を管理する
 */

public class PresetBookManager {
    /**
     * Constants
     */
    public static final String TAG = "PresetBookManager";

    private static int[] presetCsvs = {
            R.raw.cbook1,
            R.raw.cbook2,
            R.raw.cbook3
    };

    /**
     * Member variables
     */
    private LinkedList<PresetBook> mBooks = new LinkedList<>();
    private Context mContext;

    /**
     * Get/Set
     */
    public List<PresetBook> getBooks() {
        return mBooks;
    }

    public Context getContext() { return mContext; }

    /**
     * Constructor
     */
    // Singletonオブジェクト
    private static PresetBookManager singleton;

    // Singletonオブジェクトを作成する
    public static PresetBookManager createInstance(Context context) {
        singleton = new PresetBookManager(context);
        return singleton;
    }
    public static PresetBookManager getInstance() { return singleton; }

    private PresetBookManager(Context context) {
        mContext = context;
    }


    /**
     * Methods
     */
    /**
     * 一覧に表示するためのプリセット単語帳リストを作成する
     */
    public void makeBookList() {
        // csvからプリセット単語帳とカード情報を読み込んで mBooksに追加する
        for (int csvId : presetCsvs) {
            PresetBook book = CsvParser.getPresetBook(mContext, csvId, true);
            mBooks.add(book);
        }
        for (PresetBook book : mBooks) {
            book.log();
        }
    }

    /**
     * データベースにプリセット単語帳のデータを登録
     * @return 作成したBook
     */
    public TangoBook addBookToDB(PresetBook presetBook) {
        // まずは単語帳を作成
        TangoBook book = TangoBook.createBook();
        book.setName(presetBook.mName);
        book.setComment(presetBook.mComment);
        RealmManager.getBookDao().addOne(book, true);

        // 中のカードを作成する
        for (PresetCard presetCard : presetBook.getCards()) {
            TangoCard card = TangoCard.createCard();
            card.setWordA(presetCard.mWordA);
            card.setWordB(presetCard.mWordB);
            card.setComment(presetCard.mComment);
            RealmManager.getCardDao().addOne(card, TangoParentType.Book, book.getId(), true);
        }

        return book;
    }

    /**
     * プリセット単語帳を追加  テスト用
     * @return  作成したBook
     */
    public TangoBook test1() {
        PresetBook book = mBooks.get(0);
        return addBookToDB(book);
    }
}

/**
 * プリセット単語帳を保持するクラス
 */
class PresetBook {
    public String mName;
    public String mComment;
    private Context mContext;
    private int mCsvId;

    private LinkedList<PresetCard> mCards;

    /**
     * Get/Set
     */
    public List<PresetCard> getCards() {
        if (mCards == null) {
            mCards = CsvParser.getPresetCards(PresetBookManager.getInstance().getContext(),
                    mCsvId);
        }
        return mCards;
    }

    /**
     * Constructor
     */
    public PresetBook(Context context, int csvId, String name, String comment) {
        mContext = context;
        mCsvId = csvId;
        mName = name;
        mComment = comment;
    }

    public void addCard(PresetCard card) {
        mCards.add(card);
    }

    public void log() {
        ULog.print(PresetBookManager.TAG, "bookName:" + mName + " comment:" + mComment);
    }

}

/**
 * プリセット単語帳の中のカードクラス
 */
class PresetCard {
    public String mWordA;
    public String mWordB;
    public String mComment;

    /**
     * Constructor
     */
    public PresetCard(String wordA, String wordB, String comment) {
        mWordA = wordA;
        mWordB = wordB;
        mComment = comment;
    }

    public void log() {
        ULog.print(PresetBookManager.TAG, "wordA:" + mWordA + " wordB:" + mWordB);
    }
}