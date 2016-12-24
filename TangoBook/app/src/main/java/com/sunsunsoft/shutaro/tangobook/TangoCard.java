package com.sunsunsoft.shutaro.tangobook;

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
public class TangoCard extends RealmObject implements TangoItem{
    @PrimaryKey
    private int id;
    @Required
    private String wordA;       // 単語帳の表
    private String wordB;       // 単語帳の裏
    private String hintAB;    // 思い出すためのヒント A->B
    private String hintBA;    // 思い出すためのヒント B->A
    private String comment;     // 説明や例文
    private Date createTime;    // 作成日時
    private Date updateTime;    // 更新日時

    private boolean star;       // お気に入り

    @Ignore
    private TangoItemPos itemPos;   // どこにあるか？

    // GetSet
    // id
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    // wordA
    public String getWordA(){ return wordA; }
    public void setWordA(String wordA) { this.wordA = wordA; }

    // wordB
    public String getWordB(){ return wordB; }
    public void setWordB(String wordB) { this.wordB = wordB; }

    // hintAtoB
    public String getHintAB(){ return hintAB; }
    public void setHintAB(String hintAtoB) { this.hintAB = hintAtoB; }

    // hintBtoA
    public String getHintBA(){ return hintBA; }
    public void setHintBA(String hintBtoA) { this.hintBA = hintBtoA; }

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

    public TangoItemPos getItemPos() {
        return itemPos;
    }

    public void setItemPos(TangoItemPos itemPos) {
        this.itemPos = itemPos;
    }

    public String getTitle(){ return wordA; }

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
    public static TangoCard createDummyCard() {
        Random rand = new Random();
        int randVal = rand.nextInt(1000);

        TangoCard card = new TangoCard();
        card.wordA = "A " + randVal;
        card.wordB = "B " + randVal;
        card.hintAB = "HB " + randVal;
        card.hintBA = "HA " + randVal;
        card.comment = "C " + randVal;
        card.star = false;
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
        if (card.hintAB != null) {
            newCard.hintAB = new String(card.hintAB);
        }
        if (card.hintAB != null) {
            newCard.hintBA = new String(card.hintBA);
        }
        if (card.hintAB != null) {
            newCard.comment = new String(card.comment);
        }
        newCard.createTime = new Date();
        newCard.updateTime = new Date();

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
