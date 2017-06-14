package com.sunsunsoft.shutaro.tangobook.save;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.Date;
import java.util.List;

/**
 * Created by shutaro on 2017/01/17.
 *
 * 全データをxmlに書き込むための DTOクラス
 */


@Root
public class XmlTangoTop {
    @Element
    // Xml backup version
    public int version = 100;

    // Number of card
    @Element
    public int cardNum;

    // Number of book
    @Element
    public int bookNum;

    // last update date
    @Element (required = false)
    public Date updateDate;

    /**
     * Database
     */
    // card
    @ElementList(required = false)
    public List<Card> card;

    // book
    @ElementList(required = false)
    public List<Book> book;

    // card&book location
    @ElementList(required = false)
    public List<Pos> itemPos;

    // 学習単語帳履歴(1学習1履歴)
    @ElementList(required = false)
    public List<BHistory> bHistory;

    // 学習カード(1回学習するたびに1つ)
    @ElementList(required = false)
    public List<StudiedC> studiedC;

    // 学習カード履歴(1カード1履歴)
    @ElementList(required = false)
    public List<CHistory> cHistory;
}
