package com.sunsunsoft.shutaro.tangobook;

import java.util.Date;
import java.util.LinkedList;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;

/**
 * Created by shutaro on 2016/12/04.
 *
 * Cardの学習履歴等の情報
 *
 */

public class TangoCardHistory extends RealmObject {
    /**
     * Constants
     */
    // OK/NG履歴数
    public static final int CORRECT_HISTORY_MAX = 10;

    /**
     * Member varialbes
     */
    @Index
    private int cardId;

    private boolean learned;

    // 正解フラグの数(最大 CORRECT_HISTORY_MAX)
    private int correctFlagNum;

    // 正解フラグ
    private byte[] correctFlags = new byte[CORRECT_HISTORY_MAX];

    // 最後に学習した日付
    private Date studiedDate;

    @Ignore

    // 正解フラグリスト
    LinkedList<Byte> correctFlagsList = new LinkedList<>();

    /**
     * Get/Set
     */
    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public boolean isLearned() {
        return learned;
    }

    public void setLearned(boolean learned) {
        learned = learned;
    }

    public int getCorrectFlagNum() {
        return correctFlagNum;
    }

    public void setCorrectFlagNum(int correctFlagNum) {
        this.correctFlagNum = correctFlagNum;
    }

    public byte[] getCorrectFlags() {
        return correctFlags;
    }

    public void setCorrectFlags(byte[] correctFlags) {
        this.correctFlags = correctFlags;
    }

    public Date getStudiedDate() {
        return studiedDate;
    }

    public void setStudiedDate(Date studiedDate) {
        this.studiedDate = studiedDate;
    }

    /**
     * Member methods
     */
    /**
     * correctFlags -> correctFlagsList に変換
     */
    private void toCorrectList() {
        correctFlagsList.clear();

        for (int i=0; i<correctFlagNum; i++) {
            correctFlagsList.add(new Byte(correctFlags[i]));
        }
    }

    /**
     * correctFlagsList -> correctFlags 変換
     */
    private void toCorrectArray() {
        if (correctFlagsList.size() > CORRECT_HISTORY_MAX) return;

        correctFlagNum = correctFlagsList.size();
        for (int i=0; i<correctFlagNum; i++) {
            correctFlags[i] = correctFlagsList.get(i);
        }
    }

    /**
     * correctFlagsListに正解フラグを１つ追加
     * 少し遅いが一旦LinkedListに変換してから使用する
     * @param correctFlag
     */
    public void addCorrectFlags(boolean correctFlag) {
        // ArrayからListに変換
        toCorrectList();

        // リストがいっぱいなら古いもの（先頭）から削除
        if (correctFlagsList.size() >= CORRECT_HISTORY_MAX) {
            correctFlagsList.removeFirst();
        }
        correctFlagsList.add(new Byte((byte)(correctFlag ? 1 : 0)));

        // Arrayに戻す
        toCorrectArray();
    }
}
