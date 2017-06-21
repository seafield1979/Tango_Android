package com.sunsunsoft.shutaro.tangobook.save;

/**
 * Created by shutaro on 2017/06/14.
 */

import java.util.Date;

/**
 * TangoCard保存用
 */
public class Card {
    private int id;
    private String wA;       // 単語帳の表
    private String wB;       // 単語帳の裏
    private String cm;     // 説明や例文
    private Date ct;    // 作成日時

    private int cl;          // カードの色
    private boolean st;       // 覚えたフラグ
    private boolean nfl;      // 新規作成フラグ

    /**
     * Get/Set
     */
    public int getId() {
        return id;
    }

    public String getWordA() {
        if (wA == null) return "";
        return wA;
    }

    public String getWordB() {
        if (wB == null) return "";
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

    public boolean isNewFlag() {return nfl; }

    // Simple XML がデシリアイズするときに呼ぶダミーのコントストラクタ
    public Card() {}
    public Card(int id, String wordA, String wordB, String comment, Date createTime,
                int color, boolean star, boolean newFlag)
    {
        this.id = id;
        this.wA = wordA;
        this.wB = wordB;
        this.cm = comment;
        this.ct = createTime;
        this.cl = color;
        this.st = star;
        this.nfl = newFlag;
    }
}
