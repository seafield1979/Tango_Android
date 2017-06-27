package com.sunsunsoft.shutaro.tangobook.save.saveitem;

/**
 * Created by shutaro on 2017/06/14.
 */

import java.util.Date;

/**
 * TangoCard保存用
 */
public class Card {
    private int id;
    private String wordA;       // 単語帳の表
    private String wordB;       // 単語帳の裏
    private String comment;      // 説明や例文
    private Date createdTime;        // 作成日時
    private Date updateDate;    // 更新日時
    private Date studiedDate;   // 最終学習日時

    private int color;          // カードの色
    private boolean star;       // 覚えたフラグ
    private boolean newFlag;      // 新規作成フラグ

    /**
     * Get/Set
     */
    public int getId() {
        return id;
    }

    public String getWordA() {
        if (wordA == null) return "";
        return wordA;
    }

    public String getWordB() {
        if (wordB == null) return "";
        return wordB;
    }

    public String getComment() {
        return comment;
    }

    public Date getCreateTime() {
        return createdTime;
    }

    public int getColor() {
        return color;
    }

    public boolean isStar() {
        return star;
    }

    public boolean isNewFlag() {return newFlag; }

    // Simple XML がデシリアイズするときに呼ぶダミーのコントストラクタ
    public Card() {}
    public Card(int id, String wordA, String wordB, String comment, Date createTime,
                Date updateDate, Date studiedDate,
                int color, boolean star, boolean newFlag)
    {
        this.id = id;
        this.wordA = wordA;
        this.wordB = wordB;
        this.comment = comment;
        this.createdTime = createTime;
        this.updateDate = updateDate;
        this.studiedDate = studiedDate;
        this.color = color;
        this.star = star;
        this.newFlag = newFlag;
    }
}
