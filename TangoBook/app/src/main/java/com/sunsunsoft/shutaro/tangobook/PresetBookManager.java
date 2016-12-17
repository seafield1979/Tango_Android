package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.view.View;

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

    private static int[] presetXmls = {
            R.raw.book1,
            R.raw.book2,
            R.raw.book3
    };

    /**
     * Member variables
     */
    private LinkedList<PresetBook> mBooks = new LinkedList<>();
    private Context mContext;

    /**
     * Constructor
     */
    // Singletonオブジェクト
    private static PresetBookManager singleton;

    // Singletonオブジェクトを作成する
    public static PresetBookManager createInstance(Context context) {
        if (singleton == null ) {
            singleton = new PresetBookManager(context);
        }
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
    public static void makeBookList() {
        PresetBookManager instance = getInstance();

        // xmlからプリセット単語帳とカード情報を読み込んで mBooksに追加する
        for (int xmlId : presetXmls) {
            XmlTangoBook xmlBook = UXmlParser.realTangoBook(instance.mContext, xmlId);
            if (xmlBook == null) continue;
            PresetBook book = new PresetBook(xmlBook.getName(), xmlBook.getComment());
            for (XmlTangoCard xmlCard : xmlBook.getCards()) {
                PresetCard card = new PresetCard(xmlCard.getWordA(), xmlCard.getWordB(), xmlCard
                        .getComment());
                book.addCard(card);
            }
            instance.mBooks.add(book);
        }
        for (PresetBook book : instance.mBooks) {
            book.log();
        }
    }
}

class PresetBook {
    public String mName;
    public String mComment;

    public LinkedList<PresetCard> mCards = new LinkedList<>();

    /**
     * Constructor
     */
    public PresetBook(String name, String comment) {
        mName = name;
        mComment = comment;
    }

    public void addCard(PresetCard card) {
        mCards.add(card);
    }

    public void log() {
        ULog.print(PresetBookManager.TAG, "bookName:" + mName + " comment:" + mComment);
        for (PresetCard card : mCards) {
            card.log();
        }
    }

}
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