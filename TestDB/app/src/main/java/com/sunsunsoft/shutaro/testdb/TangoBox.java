package com.sunsunsoft.shutaro.testdb;

import java.util.Date;
import java.util.Random;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * 単語帳や単語カードを入れる箱
 * 箱の中のアイテムとの関連は別モデルで管理する
 */
public class TangoBox extends RealmObject implements TangoItem {
    @PrimaryKey
    private int id;

    private String name;        // 単語帳の名前
    private String comment;     // 単語帳の説明
    private int color;          // 表紙の色

    // メタデータ
    private Date createTime;    // 作成日時
    private Date updateTime;    // 更新日時

    @Ignore
    private boolean isChecked;  // ListViewで選択状態を示す
    private int pos;            // 位置、現在のグループの中(ホーム、単語帳、ボックス等）の何番目に表示されるか

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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    // テスト用のダミーカードを取得
    public static TangoBox createDummy() {
        Random rand = new Random();
        int randVal = rand.nextInt(1000);

        TangoBox box = new TangoBox();
        box.name = "Name " + randVal;
        box.comment = "Comment " + randVal;
        box.color = UColor.getRandomColor();
        box.createTime = new Date();
        box.updateTime = new Date();

        return box;
    }

    /**
     * TangoItem interface
     */
    public TangoItemType getItemType() {
        return TangoItemType.Box;
    }
}
