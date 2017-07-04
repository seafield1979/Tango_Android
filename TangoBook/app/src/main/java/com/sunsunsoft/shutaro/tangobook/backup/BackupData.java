package com.sunsunsoft.shutaro.tangobook.backup;

/**
 * Created by shutaro on 2017/06/26.
 *
 * バイナリ形式のバックアップファイルに保存するデータを保持するクラス
 * あくまで保存時にしか使用しない
 */

import com.sunsunsoft.shutaro.tangobook.database.*;

import java.util.Date;
import java.util.List;


public class BackupData {
    // backup file version
    public int version = 100;

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
    public List<TangoCard> cards;

    // book
    public List<TangoBook> books;

    // card&book location
    public List<TangoItemPos> itemPoses;

    // 学習単語帳履歴(1学習1履歴)
    public List<TangoBookHistory> bookHistories;

    // 学習カード(1回学習するたびに1つ)
    public List<TangoStudiedCard> studiedCards;

    // 学習カード履歴(1カード1履歴)
    public List<TangoCardHistory> cardHistories;
}
