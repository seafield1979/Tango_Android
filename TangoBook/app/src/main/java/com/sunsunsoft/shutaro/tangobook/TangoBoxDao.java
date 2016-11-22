package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * TangoBox の DAO(Data Access Object)
 */

public class TangoBoxDao {
    private Realm mRealm;

    public TangoBoxDao(Realm realm) {
        mRealm = realm;
    }

    /**
     * 全要素取得
     * @return nameのString[]
     */
    public List<TangoBox> selectAll() {
        RealmResults<TangoBox> results = mRealm.where(TangoBox.class).findAll();
        LinkedList<TangoBox> list = new LinkedList<>();
        for (TangoBox box : results) {
            list.add(box);
        }

        return list;
    }

    /**
     * 指定のIDの要素を取得
     * @param ids
     * @return
     */
    public List<TangoBox>selectByIds(Integer[] ids) {
        // Build the query looking at all users:
        RealmQuery<TangoBox> query = mRealm.where(TangoBox.class);

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
        RealmResults<TangoBox> results = query.findAll();

        return results;
    }

    /**
     * 指定のIDの要素を取得(1つ)
     */
    public TangoBox selectById(int id) {
        TangoBox box =
                mRealm.where(TangoBox.class)
                        .equalTo("id", id).
                        findFirst();

        if (box == null) return null;
        TangoBox newBox = mRealm.copyFromRealm(box);

        return newBox;
    }

    /**
     * 要素を追加 TangoBoxオブジェクトをそのまま追加
     * @param box
     */
    public void addOne(TangoBox box) {
        box.setId(getNextUserId(mRealm));

        mRealm.beginTransaction();
        mRealm.copyToRealm(box);
        mRealm.commitTransaction();
    }

    /**
     * ダミーのデータを一件追加
     */
    public void addDummy() {
        int newId = getNextUserId(mRealm);
        Random rand = new Random();
        int randVal = rand.nextInt(1000);

        TangoBox box = new TangoBox();
        box.setId(newId);
        box.setName("box" + randVal);
        box.setColor(0xffffff);
        box.setComment("comment:" + randVal);
        byte[] history = new byte[3];
        for (int i=0; i<history.length; i++) {
            history[i] = 1;
        }

        Date now = new Date();
        box.setCreateTime(now);
        box.setUpdateTime(now);

        mRealm.beginTransaction();
        mRealm.copyToRealm(box);
        mRealm.commitTransaction();
    }

    /**
     * 一件更新  ユーザーが設定するデータ全て
     * @param box
     * @return true:更新成功
     */
    public boolean updateOne(TangoBox box) {

        TangoBox newBox = mRealm.where(TangoBox.class).equalTo("id", box.getId()).findFirst();
        if (newBox == null) return false;

        mRealm.beginTransaction();

        newBox.setName(box.getName());
        newBox.setColor(box.getColor());
        newBox.setComment(box.getComment());
        newBox.setUpdateTime(new Date());

        mRealm.commitTransaction();
        return true;
    }

    /**
     * IDのリストに一致する項目を全て削除する
     */
    public void deleteIds(Integer[] ids) {
        if (ids.length <= 0) return;

        mRealm.beginTransaction();

        // Build the query looking at all users:
        RealmQuery<TangoBox> query = mRealm.where(TangoBox.class);

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
        RealmResults<TangoBox> results = query.findAll();

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
        Number maxUserId = realm.where(TangoBox.class).max("id");
        // 1度もデータが作成されていない場合はNULLが返ってくるため、NULLチェックをする
        if(maxUserId != null) {
            nextId = maxUserId.intValue() + 1;
        }
        return nextId;
    }
}