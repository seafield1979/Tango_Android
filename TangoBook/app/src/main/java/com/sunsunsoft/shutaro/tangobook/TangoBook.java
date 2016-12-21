package com.sunsunsoft.shutaro.tangobook;

import java.util.Date;
import java.util.Random;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * 単語帳データクラス
 */
public class TangoBook extends RealmObject implements TangoItem {
    @PrimaryKey
    private int id;
    @Required
    private String name;        // 単語帳の名前
    private String comment;     // 単語帳の説明
    private int color;          // 表紙の色

    // メタデータ
    private Date createTime;    // 作成日時
    private Date updateTime;    // 更新日時
    private Date studyTime;     // 最後に学習した日

    @Ignore
    private TangoItemPos itemPos;   // どこにあるか？


    /**
     * Constructor
     */
    public TangoBook() {
    }

    /**
     * Get/Set
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() { return name; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getStudyTime() {
        return studyTime;
    }

    public void setStudyTime(Date studyTime) {
        this.studyTime = studyTime;
    }

    public TangoItemPos getItemPos() {
        return itemPos;
    }
    public void setItemPos(TangoItemPos itemPos) {
        this.itemPos = itemPos;
    }

    @Override
    public int getPos() {
        if (itemPos == null) return 0;
        return itemPos.getPos();
    }

    public void setPos(int pos) {
        if (itemPos == null) return;
        itemPos.setPos(pos);
    }

    // テスト用のダミーカードを取得
    public static TangoBook createDummyBook() {
        Random rand = new Random();
        int randVal = rand.nextInt(1000);

        TangoBook book = new TangoBook();
        book.name = "Name " + randVal;
        book.comment = "Comment " + randVal;
        book.color = UColor.getRandomColor();
        book.createTime = new Date();
        book.updateTime = new Date();

        return book;
    }

    /**
     * TangoItem interface
     */
    public TangoItemType getItemType() {
        return TangoItemType.Book;
    }
}
