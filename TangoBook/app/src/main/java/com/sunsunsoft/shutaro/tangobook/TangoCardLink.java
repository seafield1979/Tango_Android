package com.sunsunsoft.shutaro.tangobook;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by shutaro on 2016/12/04.
 *
 * Cardのリンク情報
 * CardAにCardBのリンクを表示するには
 * srcId = CardAのID
 * dstId = CardBのID
 * を設定する
 * １つのsrcIdに対して複数のdstIdのレコードを作成することも可能
 */

public class TangoCardLink extends RealmObject {
    @Index
    private int srcId;

    @Index
    private int dstId;

    /**
     * Get/Set
     */
    public int getSrcId() {
        return srcId;
    }

    public void setSrcId(int srcId) {
        this.srcId = srcId;
    }

    public int getDstId() {
        return dstId;
    }

    public void setDstId(int dstId) {
        this.dstId = dstId;
    }
}
