package com.sunsunsoft.shutaro.tangobook.database;

import android.util.Log;

import com.sunsunsoft.shutaro.tangobook.backup.saveitem.CHistory;
import com.sunsunsoft.shutaro.tangobook.util.UDebug;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by shutaro on 2016/12/04.
 *
 * Cardの履歴のDAO
 */

public class TangoCardHistoryDao {
    /**
     * Constants
     */
    public static final String TAG = "TangoCardDao";

    /**
     * Member variables
     */
    private Realm mRealm;

    /**
     * Constructor
     * @param realm
     */
    public TangoCardHistoryDao(Realm realm) {
        mRealm = realm;
    }

    /**
     * 要素数を取得
     */
    public int getNum() {
        List<TangoCardHistory> list = selectAll();
        return list.size();
    }

    /**
     * Choice4
     */
    public List<TangoCardHistory> selectAll() {
        RealmResults<TangoCardHistory> results = mRealm.where(TangoCardHistory.class).findAll();

        if (UDebug.debugDAO) {
            Log.d(TAG, "TangoCardHistory selectAll");
            for (TangoCardHistory history : results) {
                Log.d(TAG, "cardId:" + history.getCardId() + " correctNum:" + history.getCorrectFlagNum() + " flags:" + history.getCorrectFlagsAsString());
            }
        }

        return results;
    }

    /**
     * Book情報から選択
     * @param card
     * @return
     */
    public TangoCardHistory selectByCard(TangoCard card) {
        TangoCardHistory result = mRealm.where(TangoCardHistory.class)
                .equalTo("cardId", card.getId())
                .findFirst();
        return result;
    }

    /**
     * Add
     */
    public boolean addOne(int cardId, boolean correctFlag) {
        TangoCardHistory history = new TangoCardHistory();
        history.setCardId(cardId);
        history.addCorrectFlags(correctFlag);

        history.setStudiedDate(new Date());

        mRealm.beginTransaction();
        mRealm.insert(history);
        mRealm.commitTransaction();

        return true;
    }


    /**
     * Delete
     */

    /**
     * 配下の学習カード履歴も含めてすべて削除
     */
    public void deleteAll() {
        RealmResults<TangoCardHistory> results = mRealm.where(TangoCardHistory.class)
                .findAll();

        mRealm.beginTransaction();
        results.deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    public boolean deleteByCardId(int cardId) {
        TangoCardHistory result = mRealm.where(TangoCardHistory.class)
                .equalTo("cardId", cardId).findFirst();
        if (result == null) return false;

        mRealm.beginTransaction();
        result.deleteFromRealm();
        mRealm.commitTransaction();

        return true;
    }

    /**
     * Update
     */
    public boolean updateOne(int cardId, boolean correctFlag) {
        TangoCardHistory result = mRealm.where(TangoCardHistory.class)
                .equalTo("cardId", cardId).findFirst();
        if (result == null) {
            // なかったら追加する
            addOne(cardId, correctFlag);
            return true;
        }

        mRealm.beginTransaction();
        result.addCorrectFlags(correctFlag);

        mRealm.commitTransaction();
        return true;
    }

    /**
     * Add or Update list
     */
    public void updateCards(List<TangoCard> okCards, List<TangoCard> ngCards) {
        for (TangoCard card : okCards) {
            updateOne(card.getId(), true);
        }
        for (TangoCard card : ngCards) {
            updateOne(card.getId(), false);
        }
    }

    /**
     * XMLファイルから読み込んだデータを追加する
     */
    public void addXmlCard(List<CHistory> cardHistory, boolean transaction) {
        if (cardHistory == null || cardHistory.size() == 0) {
            return;
        }
        if (transaction) {
            mRealm.beginTransaction();
        }
        for (CHistory _history : cardHistory) {
            TangoCardHistory history = new TangoCardHistory();
            history.setCardId( _history.getCardId());
            history.setCorrectFlagNum( _history.getCorrectFlagNum());
            history.setCorrectFlags( _history.getCorrectFlag());
            history.setStudiedDate( _history.getStudiedDate());
            mRealm.insert(history);
        }
        if (transaction) {
            mRealm.commitTransaction();
        }
    }
}
