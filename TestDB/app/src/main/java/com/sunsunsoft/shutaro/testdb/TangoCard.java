package com.sunsunsoft.shutaro.testdb;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by shutaro on 2016/10/28.
 */

public class TangoCard extends RealmObject{
    @PrimaryKey
    private int id;
    private String wordA;       // 単語帳の表
    private String wordB;       // 単語帳の裏
    private String hintAtoB;    // 思い出すためのヒント A->B
    private String hintBtoA;    // 思い出すためのヒント B->A
    private String comment;     // 説明や例文
    private Date createTime;    // 作成日時
    private Date updateTime;    // 更新日時
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
    public String getHintAtoB(){ return hintAtoB; }
    public void setHintAtoB(String hintAtoB) { this.hintAtoB = hintAtoB; }

    // hintBtoA
    public String getHintBtoA(){ return hintBtoA; }
    public void setHintBtoA(String hintBtoA) { this.hintBtoA = hintBtoA; }

    // comment
    public String getComment(){ return comment; }
    public void setComment(String comment) { this.comment = comment; }

    // createTime
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    // updateTime
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public byte[] getHistory() { return history; }
    public void setHistory(byte[] history) { this.history = history; }

    public Date getStudyTime() { return studyTime; }
    public void setStudyTime(Date studyTime) { this.studyTime = studyTime; }

    public boolean getIsChecked() { return isChecked; }
    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
