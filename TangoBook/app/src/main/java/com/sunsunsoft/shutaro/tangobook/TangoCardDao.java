package com.sunsunsoft.shutaro.tangobook;

import android.util.Log;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * 単語帳のDAO
 */
public class TangoCardDao {
    public static final String TAG = "TangoCardDao";

    private Realm mRealm;

    public TangoCardDao(Realm realm) {
        mRealm = realm;
    }

    /**
     * 全要素取得
     * @return nameのString[]
     */
    public List<TangoCard> selectAll() {

        RealmResults<TangoCard> results = mRealm.where(TangoCard.class).findAll();

        if (UDebug.debugDAO) {
            Log.d(TAG, "TangoCard selectAll");
            for (TangoCard card : results) {
                Log.d(TAG, "id:" + card.getId() + " wordA:" + card.getWordA());
            }
        }
        return results;
    }

    /**
     * 指定の単語帳に追加されていない単語を取得
     * @return
     */
    public List<TangoCard> selectExceptIds(Iterable<Integer> ids) {

        RealmQuery<TangoCard> query = mRealm.where(TangoCard.class);

        for (int id : ids) {
            query.notEqualTo("id", id);
        }
        RealmResults<TangoCard> results = query.findAll();

        return results;
    }

    /**
     * 変更不可なRealmのオブジェクトを変更可能なリストに変換する
     * @param list
     * @return
     */
    public List<TangoCard> toChangeable(List<TangoCard> list) {
        LinkedList<TangoCard> newList = new LinkedList<TangoCard>();
        for (TangoCard card : list) {
            newList.add(mRealm.copyFromRealm(card));
        }
        return newList;
    }

    /**
     * List<TangoCard>を List<TangoItem>に変換する
     * @param cards
     * @return
     */
    public static List<TangoItem> toItems(List<TangoCard> cards) {
        if (cards == null) return null;

        LinkedList<TangoItem> items = new LinkedList<>();
        for (TangoCard card : cards) {
            items.add(card);
        }
        return items;
    }

    /**
     * 指定のIDの要素を取得
     * @param ids
     * @return
     */
    public List<TangoCard> selectByIds(Integer[] ids) {
        // Build the query looking at all users:
        RealmQuery<TangoCard> query = mRealm.where(TangoCard.class);

        // Add query conditions:
        boolean isFirst = true;
        for (int id : ids) {
            if (isFirst) {
                isFirst = false;
                query.equalTo("id", id);
            } else {
                query.or().equalTo("id", id);
            }
        }
        // Execute the query:
        RealmResults<TangoCard> results = query.findAll();

        return results;
    }

    /**
     * 指定のIDの要素を取得(1つ)
     */
    public TangoCard selectById(int id) {
        TangoCard card =
                mRealm.where(TangoCard.class)
                        .equalTo("id", id).
                        findFirst();

        if (card == null) return null;
        TangoCard newCard = mRealm.copyFromRealm(card);

        return newCard;
    }

    /**
     * 要素を追加
     * @param
     * @param
     */
    public void add1(String wordA, String wordB) {
        int newId = getNextId();

        TangoCard card = new TangoCard();
        card.setId(newId);
        card.setWordA(wordA);
        card.setWordB(wordB);
        card.setHintAB("hintAB");
        card.setHintAB("hintBA");
        card.setComment("comment");
        Date now = new Date();
        card.setCreateTime(now);
        card.setUpdateTime(now);

        mRealm.beginTransaction();
        mRealm.copyToRealm(card);
        mRealm.commitTransaction();
    }

    /**
     * 要素を追加 TangoCardオブジェクトをそのまま追加
     * @param card
     */
    public void addOne(TangoCard card) {
        card.setId(getNextId());

        mRealm.beginTransaction();
        mRealm.copyToRealm(card);
        mRealm.commitTransaction();
    }

    /**
     * ダミーのデータを一件追加
     */
    public void addDummy() {
        int newId = getNextId();
        Random rand = new Random();
        int randVal = rand.nextInt(1000);

        TangoCard card = new TangoCard();
        card.setId(newId);
        card.setWordA("hoge" + randVal);
        card.setWordB("ほげ" + randVal);
        card.setHintAB("hintAB:" + randVal);
        card.setHintBA("hintBA:" + randVal);
        card.setComment("comment:" + randVal);
        card.setStar(true);
        byte[] history = new byte[3];
        for (int i=0; i<history.length; i++) {
            history[i] = 1;
        }
        card.setHistory(history);

        Date now = new Date();
        card.setCreateTime(now);
        card.setUpdateTime(now);

        mRealm.beginTransaction();
        mRealm.copyToRealm(card);
        mRealm.commitTransaction();
    }

    /**
     * 要素を更新
     */
    public void updateOne(int id, String wordA, String wordB) {
        mRealm.beginTransaction();
        TangoCard card = mRealm.where(TangoCard.class).equalTo("id", id).findFirst();
        card.setWordA(wordA);
        card.setWordB(wordB);

        mRealm.commitTransaction();
    }

    /**
     * 指定したIDの項目を更新する
     * @param card
     */
    public void updateOne(TangoCard card) {
        mRealm.beginTransaction();

        TangoCard newCard =
                mRealm.where(TangoCard.class)
                        .equalTo("id", card.getId()).
                        findFirst();

        newCard.setWordA(card.getWordA());
        newCard.setWordB(card.getWordB());
        newCard.setHintAB(card.getHintAB());
        newCard.setHintBA(card.getHintBA());
        newCard.setComment(card.getComment());
        newCard.setUpdateTime(new Date());

        mRealm.commitTransaction();
    }


    /**
     * IDのリストに一致する項目を全て更新する
     * @param ids
     * @param wordA  更新するA
     * @param wordB  更新するB
     */
    public void updateByIds(Integer[] ids, String wordA, String wordB) {
        mRealm.beginTransaction();

        // Build the query looking at all users:
        RealmQuery<TangoCard> query = mRealm.where(TangoCard.class);

        // Add query conditions:
        boolean isFirst = true;
        for (int id : ids) {
            if (isFirst) {
                isFirst = false;
                query.equalTo("id", id);
            } else {
                query.or().equalTo("id", id);
            }
        }
        // Execute the query:
        RealmResults<TangoCard> results = query.findAll();

        for (TangoCard card : results) {
            card.setWordA(wordA);
            card.setWordB(wordB);
        }

        mRealm.commitTransaction();
    }

    /**
     * IDのリストに一致する項目を全て削除する
     */
    public void deleteIds(Integer[] ids) {
        if (ids.length <= 0) return;

        mRealm.beginTransaction();

        // Build the query looking at all users:
        RealmQuery<TangoCard> query = mRealm.where(TangoCard.class);

        // Add query conditions:
        boolean isFirst = true;
        for (int id : ids) {
            if (isFirst) {
                isFirst = false;
                query.equalTo("id", id);
            } else {
                query.or().equalTo("id", id);
            }
        }
        // Execute the query:
        RealmResults<TangoCard> results = query.findAll();

        results.deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    /**
     * 学習履歴(OK/NG)を追加する
     */
    public void addHistory() {

    }

    /**
     * 学習日付を更新する
     */
    private void updateStudyTime() {

    }

    /**
     * かぶらないプライマリIDを取得する
     * @return
     */
    public int getNextId() {
        // 初期化
        int nextId = 1;
        // userIdの最大値を取得
        Number maxUserId = mRealm.where(TangoCard.class).max("id");
        // 1度もデータが作成されていない場合はNULLが返ってくるため、NULLチェックをする
        if(maxUserId != null) {
            nextId = maxUserId.intValue() + 1;
        }
        return nextId;
    }
}