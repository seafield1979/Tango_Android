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
