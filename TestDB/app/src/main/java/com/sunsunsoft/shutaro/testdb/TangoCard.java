package com.sunsunsoft.shutaro.testdb;

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
public class TangoCard extends RealmObject{
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
    private boolean star;       // 何かしらのチェック
    // メタデータ
    private byte[] history;     // 過去のOK/NG履歴
    private Date studyTime;     // 最後に学習した日

    @Ignore
    private boolean isChecked;  // ListViewで選択状態を示す

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

    public byte[] getHistory() { return history; }
    public void setHistory(byte[] history) { this.history = history; }

    public boolean isChecked() { return isChecked; }
    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    // テスト用のダミーカードを取得
    public static TangoCard createDummyCard() {
        Random rand = new Random();
        int randVal = rand.nextInt(1000);

        TangoCard card = new TangoCard();
        card.wordA = "WordA " + randVal;
        card.wordB = "WordB " + randVal;
        card.hintAB = "HintAB " + randVal;
        card.hintBA = "HintBA " + randVal;
        card.comment = "Comment " + randVal;
        card.star = true;
        card.history = new byte[3];
        for (int i = 0; i < card.history.length; i++) {
            card.history[i] = 1;
        }
        return card;
    }
}
