package com.sunsunsoft.shutaro.tangobook.save;

/**
 * Created by shutaro on 2017/06/14.
 */
import java.util.Date;

/**
 * TangoBook保存用
 */
public class Book {
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