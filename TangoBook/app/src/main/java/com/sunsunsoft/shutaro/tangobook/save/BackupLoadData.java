package com.sunsunsoft.shutaro.tangobook.save;

import com.sunsunsoft.shutaro.tangobook.save.saveitem.BHistory;
import com.sunsunsoft.shutaro.tangobook.save.saveitem.Book;
import com.sunsunsoft.shutaro.tangobook.save.saveitem.CHistory;
import com.sunsunsoft.shutaro.tangobook.save.saveitem.Card;
import com.sunsunsoft.shutaro.tangobook.save.saveitem.Pos;
import com.sunsunsoft.shutaro.tangobook.save.saveitem.StudiedC;

import java.util.Date;
import java.util.List;

/**
 * Created by shutaro on 2017/06/27.
 *
 * バイナリ形式のバックアップファイルから読み込んだデータを格納するクラス
 * あくまで読み込み時にしか使用しない
 */

public class BackupLoadData {
    // backup file version
    public int version;

    // Number of card
    public int cardNum;

    // Number of book
    public int bookNum;

    // last update date
    public Date updateDate;

    /**
     * Database
     */
    // card
    public List<Card> cards;

    // book
    public List<Book> books;

    // card&book location
    public List<Pos> itemPoses;

    // 学習単語帳履歴(1学習1履歴)
    public List<BHistory> bookHistories;

    // 学習カード(1回学習するたびに1つ)
    public List<StudiedC> studiedCards;

    // 学習カード履歴(1カード1履歴)
    public List<CHistory> cardHistories;
}
