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
            R.raw.animal,
            R.raw.fruit,
            R.raw.week,
            R.raw.month,
            R.raw.questions,
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
        RealmManager.getBookDao().addOne(book, -1);

        // 中のカードを作成する
        for (PresetCard presetCard : presetBook.getCards()) {
            TangoCard card = TangoCard.createCard();
            card.setWordA(presetCard.mWordA);
            card.setWordB(presetCard.mWordB);
            card.setComment(presetCard.mComment);
            card.setNewFlag(false);
            RealmManager.getCardDao().addOne(card, TangoParentType.Book, book.getId(), -1);
        }

        return book;
    }

    /**
     * Csvファイルにエクスポートする
     * @return エクスポートファイルのパス
     */
    public String exportToCsvFile(TangoBook book, List<TangoCard> cards) {
        File path = UUtil.getPath(mContext, FilePathType.ExternalDocument);
        try {
            String filePath = path.toString() + "/" + book.getName() + ".csv";
            FileWriter fw = new FileWriter(filePath);

            // 1行目はbooke名
            StringBuffer bookText = new StringBuffer(encodeCsv(book.getName()));
            if (book.getComment() != null && book.getComment().length() > 0) {
                bookText.append("," + encodeCsv(book.getComment()));
            }
            fw.write(bookText.toString() + "\n");
            // 2行目以降はcardの英語、日本語
            for (TangoCard card : cards) {
                if (card.getWordA() != null) {
                    fw.write(encodeCsv(card.getWordA()));
                }
                fw.write(",");

                if (card.getWordB() != null) {
                    fw.write(encodeCsv(card.getWordB()));
                }
                fw.write(",");

                if (card.getComment() != null) {
                    fw.write(encodeCsv(card.getComment()));
                }
                fw.write("\n");
            }
            fw.close();

            return filePath;
        } catch (Exception e) {
            ULog.print(TAG, e.toString());
            return null;
        }
    }

    /**
     * CSVの文字列をエスケープする
     * CSV文字列中にカンマ(,)があったら文字列を""で囲む
     * 改行を\nに変換する
     * @param word
     * @return
     */
    private String encodeCsv(String word) {
        String output = word.replace("\n", "\\n");

        if (output.contains(",")) {
            return "\"" + output + "\"";
        }
        return output;
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
    public String getFileName() {
        if (mFile != null) {
            return "(" + mFile.getName() + ")";
        }
        return "";
    }

    /**
     * Constructor
     */
    // アプリ内のCSVから追加する
    public PresetBook(Context context, int csvId, String name, String comment) {
        mContext = context;
        mCsvId = csvId;
        mName = name;
        mComment = comment;
    }
    // ストレージにあるCSVから追加する
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