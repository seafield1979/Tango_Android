package com.sunsunsoft.shutaro.tangobook.database;

import android.graphics.Color;
import java.util.Date;
import java.util.Random;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * 単語カード
 * RealmObjectのサブクラスなのでそのままテーブルとして使用される
 */
public class TangoCard extends RealmObject implements TangoItem {
    @PrimaryKey
    private int id;

    private int originalId;     // コピー元のカードのID

    @Required
    private String wordA;       // 単語帳の表
    private String wordB;       // 単語帳の裏
    private String comment;     // 説明や例文
    private Date createTime;    // 作成日時
    private Date updateTime;    // 更新日時
//    private Date lastStudiedTime;   // 最終学習時間

    private int color;          // カードの色
    private boolean star;       // 覚えたフラグ
    private boolean newFlag;    // 新規作成フラグ

    @Ignore
    private TangoItemPos itemPos;   // どこにあるか？

    /**
     * Get/Set
     */
    // id
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    // originalId
    public int getOriginalId() {
        return originalId;
    }

    public void setOriginalId(int originalId) {
        this.originalId = originalId;
    }

    // wordA
    public String getWordA(){ return wordA; }
    public void setWordA(String wordA) { this.wordA = wordA; }

    // wordB
    public String getWordB(){ return wordB; }
    public void setWordB(String wordB) { this.wordB = wordB; }

    // comment
    public String getComment(){ return comment; }
    public void setComment(String comment) { this.comment = comment; }

    // star
    public boolean getStar(){ return star; }
    public void setStar(boolean star) { this.star = star; }


    // createTime
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    // updateTime
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    // lastStudiedTime
    public Date getLastStudiedTime() {
        return null;
    }

    public void setLastStudiedTime(Date time) {
    }


    // pos
    public TangoItemPos getItemPos() {
        return itemPos;
    }

    public void setItemPos(TangoItemPos itemPos) {
        this.itemPos = itemPos;
    }

    // title
    public String getTitle(){ return wordA; }

    // color
    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    // Star
    public boolean isStar() {
        return star;
    }

    // New flag
    public boolean isNewFlag() {
        return newFlag;
    }
    public void setNewFlag(boolean newFlag) {
        this.newFlag = newFlag;
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


    /**
     * Constructor
     */
    public static TangoCard createCard() {
        TangoCard card = new TangoCard();
        card.originalId = 0;
        card.newFlag = true;
        card.color = Color.BLACK;
        card.wordA = "";
        card.wordB = "";
        card.star = false;
        card.createTime = new Date();
        card.updateTime = new Date();

        return card;
    }

    // テスト用のダミーカードを取得
    public static TangoCard createDummyCard() {
        Random rand = new Random();
        int randVal = rand.nextInt(1000);

        TangoCard card = new TangoCard();
        card.wordA = "A " + randVal;
        card.wordB = "あ " + randVal;
        card.comment = "C " + randVal;
        card.color = Color.BLACK;
        card.star = false;
        card.newFlag = true;
        return card;
    }

    // コピーを作成する
    public static TangoCard copyCard(TangoCard card) {
        TangoCard newCard = new TangoCard();
        newCard.id = RealmManager.getCardDao().getNextId();
        if (card.wordA != null) {
            newCard.wordA = new String(card.wordA);
        }
        if (card.wordB != null) {
            newCard.wordB = new String(card.wordB);
        }
        newCard.createTime = new Date();
        newCard.updateTime = new Date();

        newCard.color = card.color;
        newCard.star = card.star;
        return newCard;
    }

    /**
     * TangoItem interface
     */
    public TangoItemType getItemType() {
        return TangoItemType.Card;
    }
}
