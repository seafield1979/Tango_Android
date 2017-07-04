package com.sunsunsoft.shutaro.tangobook.database;

import android.util.Log;

import com.sunsunsoft.shutaro.tangobook.backup.saveitem.StudiedC;
import com.sunsunsoft.shutaro.tangobook.util.UDebug;

import java.util.List;

import io.realm.Realm;
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
     * 要素数を取得
     */
    public int getNum() {
        List<TangoStudiedCard> list = selectAll();
        return list.size();
    }

    /**
     * 取得系(Selection type)
     */
    /**
     * TangoTag 全要素取得
     * @return nameのString[]
     */
    public List<TangoStudiedCard> selectAll() {
        RealmResults<TangoStudiedCard> results = mRealm.where(TangoStudiedCard.class)
                .findAll();

        if (UDebug.debugDAO) {
            Log.d(TAG, "TangoStudiedCard selectAll");
            for (TangoStudiedCard card : results) {
                Log.d(TAG, " historyId:" + card.getBookHistoryId() +
                        " cardId:" + card.getCardId() +
                        " okFlag:" + card.isOkFlag()
                        );
            }
        }
        return results;
    }

    /**
     * 指定bookHistoryIdに関連付けられたカードを取得
     * @param bookHistoryId
     * @return
     */
    public List<TangoStudiedCard> selectByHistoryId(int bookHistoryId) {

        RealmResults<TangoStudiedCard> results = mRealm.where(TangoStudiedCard.class)
                .equalTo("bookHistoryId", bookHistoryId)
                .findAll();

        if (UDebug.debugDAO) {
            Log.d(TAG, "TangoStudiedCard selectAll");
            for (TangoStudiedCard card : results) {
                Log.d(TAG, " historyId:" + card.getBookHistoryId() +
                        " cardId:" + card.getCardId() +
                        " okFlag:" + card.isOkFlag()
                );
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

    /**
     * XMLファイルから読み込んだデータを追加する
     */
    public void addXmlCard(List<StudiedC> studiedCard, boolean transaction) {
        if (studiedCard == null || studiedCard.size() == 0) {
            return;
        }
        if (transaction) {
            mRealm.beginTransaction();
        }
        for (StudiedC _card : studiedCard) {
            TangoStudiedCard card = new TangoStudiedCard();
            card.setCardId( _card.getCardId());
            card.setBookHistoryId( _card.getBookHistoryId());
            card.setOkFlag( _card.isOkFlag());

            mRealm.copyToRealm(card);
        }
        if (transaction) {
            mRealm.commitTransaction();
        }
    }
}
