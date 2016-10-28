package com.sunsunsoft.shutaro.testdb;

import android.content.Context;
import android.util.Log;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by shutaro on 2016/10/28.
 */

public class TangoCardDao {
    private Realm mRealm;

    public TangoCardDao(Context context) {
        // Realm.getDefaultInstance() の前に Realm.setDefaultConfiguration をコールしておかないとエラーになる
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context)
                .schemaVersion(1)
                .migration(new TangoCardMigration())
                .build();
        Realm.setDefaultConfiguration(realmConfig);
        mRealm = Realm.getDefaultInstance();
    }

    /**
     * 全要素取得
     * @return nameのString[]
     */
    public List<TangoCard> selectAll() {
        RealmResults<TangoCard> results = mRealm.where(TangoCard.class).findAll();
        LinkedList<TangoCard> list = new LinkedList<>();
        for (TangoCard card : results) {
            Log.d("select", "id:" + card.getId() + " A:" + card.getWordA() + " B:" + card.getWordB());
            list.add(card);
        }

        return list;
    }

    /**
     * 要素を追加
     * @param
     * @param
     */
    public void add1(String wordA, String wordB) {
        int newId = getNextUserId(mRealm);

        TangoCard card = new TangoCard();
        card.setId(newId);
        card.setWordA(wordA);
        card.setWordB(wordB);
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
     * IDのリストに一致する項目を全て更新する
     * @param ids
     * @param wordA  更新するA
     * @param wordB  更新するB
     */
    public void updateIds(Integer[] ids, String wordA, String wordB) {
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
     * かぶらないプライマリIDを取得する
     * @param realm
     * @return
     */
    public int getNextUserId(Realm realm) {
        // 初期化
        int nextId = 1;
        // userIdの最大値を取得
        Number maxUserId = realm.where(TangoCard.class).max("id");
        // 1度もデータが作成されていない場合はNULLが返ってくるため、NULLチェックをする
        if(maxUserId != null) {
            nextId = maxUserId.intValue() + 1;
        }
        return nextId;
    }
}