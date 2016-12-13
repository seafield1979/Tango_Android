package com.sunsunsoft.shutaro.tangobook;

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
     * Select
     */
    public List<TangoCardHistory> selectAll() {
        RealmResults<TangoCardHistory> results = mRealm.where(TangoCardHistory.class).findAll();
        return results;
    }

    /**
     * Book情報から選択
     * @param card
     * @return
     */
    List<TangoCardHistory> selectByCard(TangoCard card) {
        RealmResults<TangoCardHistory> results = mRealm.where(TangoCardHistory.class)
                .equalTo("cardId", card.getId())
                .findAll();
        return results;
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

}
