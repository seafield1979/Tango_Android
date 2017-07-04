package com.sunsunsoft.shutaro.tangobook.backup.saveitem;

/**
 * Created by shutaro on 2017/06/14.
 */
import java.util.Date;

/**
 * TangoBook保存用
 */
public class Book {
    private int id;
    private String name;        // 単語帳の名前
    private String comment;       // 単語帳の説明
    private int color;          // 表紙の色
    private Date createdDate;         // 作成日時
    private Date studiedDate;       // 学習日時
    private boolean newFlag;      // 新規作成フラグ

    /**
     * Get/Set
     */
    public int getId() {
        return id;
    }

    public String getName() {
        if (name == null) return "";
        return name;
    }

    public String getComment() {
        return comment;
    }

    public int getColor() {
        return color;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getStudiedDate() {
        return studiedDate;
    }

    public boolean isNewFlag() { return newFlag; }

    // Simple XML がデシリアイズするときに呼ぶダミーのコントストラクタ
    public Book() {
    }
    public Book(int id, String name, String comment, int color,
                Date createDate, Date studiedDate, boolean newFlag) {
        this.id = id;
        this.name = name;
        this.comment = comment;
        this.color = color;
        this.createdDate = createDate;
        this.studiedDate = studiedDate;
        this.newFlag = newFlag;
    }
}