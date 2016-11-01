package com.sunsunsoft.shutaro.testdb;


import java.util.Date;
import java.util.Random;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class TangoBook extends RealmObject {
    @PrimaryKey
    private int id;

    private String name;        // 単語帳の名前
    private String comment;     // 単語帳の説明
    private int color;          // 表紙の色

    // メタデータ
    private Date createTime;    // 作成日時
    private Date updateTime;    // 更新日時
    private Date studyTime;     // 最後に学習した日

    @Ignore
    private boolean isChecked;  // ListViewで選択状態を示す


    // Get/Set
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    // テスト用のダミーカードを取得
    public static TangoBook createDummy() {
        Random rand = new Random();
        int randVal = rand.nextInt(1000);

        TangoBook book = new TangoBook();
        book.name = "Name " + randVal;
        book.comment = "Comment " + randVal;
        book.color = MyColor.getRandomColor();
        book.createTime = new Date();
        book.updateTime = new Date();

        return book;
    }
}
