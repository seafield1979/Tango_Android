package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;
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
            R.raw.fruit,
            R.raw.week,
            R.raw.month,
            R.raw.toeic1
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
     * Methods
     */
    /**
     * 一覧に表示するためのcsv単語帳リストを作成する
     */
    public List<PresetBook> getCsvBookList() {
        // 指定のフォルダにあるcsvファイルを読み込み
        File[] files = UUtil.getPath(mContext, FilePathType.ExternalDocument).listFiles();
        LinkedList<PresetBook> books = new LinkedList<>();

        if (files.length <= 0) {
            return null;
        }

        for(File file : files){
            if(file.isFile() && file.getName().endsWith(".csv")){
                PresetBook book = CsvParser.getFileBook(mContext, file, true);
                if (book != null) {
                    books.add(book);
                }
            }
        }

        for (PresetBook book : books) {
            book.log();
        }
        return books;
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
            card.setNewFlag(false);
            RealmManager.getCardDao().addOne(card, TangoParentType.Book, book.getId(), true);
        }

        return book;
    }

    /**
     * Csvファイルにエクスポートする
     * @return エクスポートファイルのパス
     */
    public String exportToCsvFile(String bookName, List<TangoCard> cards) {
        File path = UUtil.getPath(mContext, FilePathType.ExternalDocument);
        try {
            String filePath = path.toString() + "/" + bookName + ".csv";
            FileWriter fw = new FileWriter(filePath);

            // 1行目はbooke名
            fw.write(bookName + "\n");
            // 2行目以降はcardの英語、日本語
            for (TangoCard card : cards) {
                fw.write(card.getWordA() + "," + card.getWordB() + "\n");
            }
            fw.close();

            return filePath;
        } catch (Exception e) {
            ULog.print(TAG, e.toString());
            return null;
        }
    }
}

/**
 * プリセット単語帳を保持するクラス
 */
class PresetBook {
    public String mName;
    public String mComment;
    private Context mContext;
    private int mCsvId = -1;
    private File mFile;

    private LinkedList<PresetCard> mCards;

    /**
     * Get/Set
     */
    public List<PresetCard> getCards() {
        if (mCards == null) {
            if (mCsvId != -1) {
                mCards = CsvParser.getPresetCards(PresetBookManager.getInstance().getContext(),
                        mCsvId);
            } else if (mFile != null){
                mCards = CsvParser.getPresetCards(mFile);
            }
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
    public PresetBook(Context context, File file, String name, String comment) {
        mContext = context;
        mName = name;
        mFile = file;
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