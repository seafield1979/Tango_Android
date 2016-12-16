package com.sunsunsoft.shutaro.tangobook;

import android.util.Log;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by shutaro on 2016/12/15.
 */

public class TangoStudiedCardDao {
    /**
     * Constants
     */
    public static final String TAG = "TangoStudiedCardDao";

    /**
     * Member variables
     */
    private Realm mRealm;

    /**
     * Constructor
     * @param realm
     */
    public TangoStudiedCardDao(Realm realm) {
        mRealm = realm;
    }


    /**
     * 取得系(Selection type)
     */
    /**
     * TangoTag 全要素取得
     * @return nameのString[]
     */
    public List<TangoTag> selectByHistoryId(int bookHistoryId) {

        RealmResults<TangoTag> results = mRealm.where(TangoTag.class)
                .equalTo("bookHistoryId", bookHistoryId)
                .findAll();

        if (UDebug.debugDAO) {
            Log.d(TAG, "TangoTag selectAll");
            for (TangoTag tag : results) {
                Log.d(TAG, "itemType:" + tag.getItemType() +
                        " itemid:" + tag.getItemId());
            }
        }
        return results;
    }

    /**
     * 追加系 (Addition type)
     */
    public void addStudiedCards(int bookHistoryId,
                               List<TangoCard> okCards, List<TangoCard> ngCards)
    {
        mRealm.beginTransaction();

        for (TangoCard card : okCards) {
            TangoStudiedCard item = new TangoStudiedCard();
            item.setBookHistoryId(bookHistoryId);
            item.setCardId(card.getId());
            item.setOkFlag(true);
            mRealm.copyToRealm(item);
        }
        for (TangoCard card : ngCards) {
            TangoStudiedCard item = new TangoStudiedCard();
            item.setBookHistoryId(bookHistoryId);
            item.setCardId(card.getId());
            item.setOkFlag(false);
            mRealm.copyToRealm(item);
        }

        mRealm.commitTransaction();
    }
    /**
     * 削除系 (Delete type)
     */
    /**
     * 全削除 for Debug
     */
    public void deleteAll() {
        RealmResults<TangoStudiedCard> results = mRealm.where(TangoStudiedCard.class).findAll();
        if (results == null || results.size() == 0) {
            return;
        }
        mRealm.beginTransaction();
        results.deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    /**
     * 指定の BookHistoryIdのレコードを削除
     * @param bookHistoryId
     */
    public void deleteByHistoryId(int bookHistoryId) {
        RealmResults<TangoStudiedCard> results = mRealm.where(TangoStudiedCard.class)
                .equalTo("bookHistoryId", bookHistoryId)
                .findAll();
        if (results == null || results.size() == 0) {
            return;
        }

        mRealm.beginTransaction();
        results.deleteAllFromRealm();
        mRealm.commitTransaction();
    }
}
