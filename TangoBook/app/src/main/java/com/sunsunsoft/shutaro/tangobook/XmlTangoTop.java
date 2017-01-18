package com.sunsunsoft.shutaro.tangobook;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.Date;
import java.util.List;

import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by shutaro on 2017/01/17.
 *
 * 全データをxmlに書き込むための DTOクラス
 */

/**
 * TangoCard保存用
 */
class Card {
    private int id;
    private String wA;       // 単語帳の表
    private String wB;       // 単語帳の裏
    private String cm;     // 説明や例文
    private Date ct;    // 作成日時

    private int cl;          // カードの色
    private boolean st;       // 覚えたフラグ

    /**
     * Get/Set
     */
    public int getId() {
        return id;
    }

    public String getWordA() {
        return wA;
    }

    public String getWordB() {
        return wB;
    }

    public String getComment() {
        return cm;
    }

    public Date getCreateTime() {
        return ct;
    }

    public int getColor() {
        return cl;
    }

    public boolean isStar() {
        return st;
    }

    // Simple XML がデシリアイズするときに呼ぶダミーのコントストラクタ
    public Card() {}
    public Card(int id, String wordA, String wordB, String comment, Date createTime,
                    int color, boolean star)
    {
        this.id = id;
        this.wA = wordA;
        this.wB = wordB;
        this.cm = comment;
        this.ct = createTime;
        this.cl = color;
        this.st = star;
    }
}

/**
 * TangoBook保存用
 */
class Book {
    private int id;
    private String nm;        // 単語帳の名前
    private String cm;     // 単語帳の説明
    private int cl;          // 表紙の色
    private Date ct;    // 作成日時

    /**
     * Get/Set
     */
    public int getId() {
        return id;
    }

    public String getName() {
        return nm;
    }

    public String getComment() {
        return cm;
    }

    public int getColor() {
        return cl;
    }

    public Date getCreateTime() {
        return ct;
    }


    // Simple XML がデシリアイズするときに呼ぶダミーのコントストラクタ
    public Book() {
    }
    public Book(int id, String name, String comment, int color, Date createTime) {
        this.id = id;
        this.nm = name;
        this.cm = comment;
        this.cl = color;
        this.ct = createTime;
    }
}

/**
 * TangoItemPos保存用
 */
class Pos {
    private int pType;  // parentType
    private int pId;    // parentId
    private int pos;
    private int iType;  // itemType
    private int iId;    // itemId

    /**
     * Get/Set
     */
    public int getParentType() {
        return pType;
    }

    public int getParentId() {
        return pId;
    }

    public int getPos() {
        return pos;
    }

    public int getItemType() {
        return iType;
    }

    public int getItemId() {
        return iId;
    }

    public Pos(){}
    public Pos(int parentType, int parentId, int pos, int itemType, int itemId) {
        this.pType = parentType;
        this.pId = parentId;
        this.pos = pos;
        this.iType = itemType;
        this.iId = itemId;
    }
}

/**
 * TangoBookHistory保存用
 */
class BHistory {
    private int id;
    private int bId;        // bookId
    private boolean learn;  // learned
    private int ok;         // okNum
    private int ng;         // ngNum
    private float cr;       // correctRatio
    private Date st;        // StudiedDateTime

    /**
     * Get/Set
     */
    public int getId() {
        return id;
    }

    public int getBookId() {
        return bId;
    }

     public int getOkNum() {
        return ok;
    }

    public int getNgNum() {
        return ng;
    }

    public float getCorrectRatio() {
        return cr;
    }

    public Date getStudiedDateTime() {
        return st;
    }

    /**
     * Constructor
     */
    public BHistory(){}
    public BHistory(int id, int bookId, int okNum, int ngNum, float correctRatio, Date
            studiedTime) {
        this.id = id;
        this.bId = bookId;
        this.ok = okNum;
        this.ng = ngNum;
        this.cr = correctRatio;
        this.st = studiedTime;
    }
}

/**
 * TangoStudiedCard保存用
 */
class StudiedC {
    private int bId;        // bookHistoryId
    private int cId;        // cardId
    private boolean ok;     // okFlag

    /**
     * Get/Set
     */
    public int getBookHistoryId() {
        return bId;
    }

    public int getCardId() {
        return cId;
    }

    public boolean isOkFlag() {
        return ok;
    }

    public StudiedC(){}
    public StudiedC(int bookHistoryId, int cardId, boolean okFlag) {
        this.bId = bookHistoryId;
        this.cId = cardId;
        this.ok = okFlag;
    }
}

/**
 * TangoCardHistory保存用
 */
class CHistory {
    private int cId;             // cardId
    private int cfn;     // correctFlagNum
    private byte[] cf = new byte[10];     // correctFlags
    private Date date;       // studiedDate

    /**
     * Get/Set
     */
    public int getCardId() {
        return cId;
    }

    public int getCorrectFlagNum() {
        return cfn;
    }

    public byte[] getCorrectFlag() {
        return cf;
    }

    public Date getStudiedDate() {
        return date;
    }

    /**
     * Constructor
     */
    public CHistory(){}
    public CHistory(int cardId, int correctFlagNum, byte[] correctFlag, Date studiedDate){
        this.cId = cardId;
        this.cfn = correctFlagNum;
        this.cf = correctFlag;
        this.date = studiedDate;
    }
}

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
