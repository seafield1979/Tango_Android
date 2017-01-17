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

@Root
public class XmlTangoTop {

    // カード
    @ElementList(required = false)
    public List<Card> card;

    // 単語帳
    @ElementList(required = false)
    public List<Book> book;

    // 単語帳、カードの配置場所
    @ElementList(required = false)
    public List<Pos> itemPos;
}
