package com.sunsunsoft.shutaro.testdb;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * TangoItemListのDAO
 */

public class TangoListItemDao  {

    private Realm mRealm;

    public TangoListItemDao(Realm realm) {
        mRealm = realm;
    }

    /**
     * 全要素取得
     * @return nameのString[]
     */
    public List<TangoListItem> selectAll() {
        RealmResults<TangoListItem> results = mRealm.where(TangoListItem.class).findAllSorted
                ("pos", Sort.ASCENDING);
        return results;
    }

    /**
     * 変更不可なRealmのオブジェクトを変更可能なリストに変換する
     * @param list
     * @return
     */
    public List<TangoListItem> toChangeable(List<TangoListItem> list) {
        LinkedList<TangoListItem> newList = new LinkedList<TangoListItem>();
        for (TangoListItem item : list) {
            newList.add(mRealm.copyFromRealm(item));
        }
        return newList;
    }


    /**
     * 全要素削除
     * @return
     */
    public boolean deleteAll() {
        RealmResults<TangoListItem> results = mRealm.where(TangoListItem.class).findAll();
        mRealm.beginTransaction();
        boolean ret = results.deleteAllFromRealm();
        mRealm.commitTransaction();
        return ret;
    }

    /**
     * IDのリストに一致する項目を全て削除する
     */
    public void deletePositions(Integer[] positions) {
        if (positions.length <= 0) return;

        mRealm.beginTransaction();

        RealmQuery<TangoListItem> query = mRealm.where(TangoListItem.class);

        // Add query conditions:
        boolean isFirst = true;
        for (int pos : positions) {
            if (isFirst) {
                isFirst = false;
                query.equalTo("pos", pos);
            } else {
                query.or().equalTo("pos", pos);
            }
        }
        // Execute the query:
        RealmResults<TangoListItem> results = query.findAll();

        results.deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    /**
     * 要素を追加する
     */
    public void addOne(TangoListItem item) {
        item.setPos(getNextPos(mRealm));

        mRealm.beginTransaction();
        mRealm.copyToRealm(item);
        mRealm.commitTransaction();
    }

    /**
     * アイテムの位置(pos)を変更する
     * @param oldPos
     * @param newPos
     */
    public void updatePos(int oldPos, int newPos) {
        TangoListItem item = mRealm.where(TangoListItem.class).equalTo("pos", oldPos).findFirst();
        if (item == null) return;

        mRealm.beginTransaction();
        item.setPos(newPos);
        mRealm.commitTransaction();
    }

    /**
     * ２つのアイテムの位置(pos)を入れ替える
     * @param pos1
     * @param pos2
     */
    public void changePos(int pos1, int pos2) {
        TangoListItem item1 = mRealm.where(TangoListItem.class).equalTo("pos", pos1).findFirst();
        TangoListItem item2 = mRealm.where(TangoListItem.class).equalTo("pos", pos2).findFirst();
        if (item1 == null || item2 == null) return;

        mRealm.beginTransaction();
        item1.setPos(pos2);
        item2.setPos(pos1);
        mRealm.commitTransaction();
    }

    /**
     * リストの全要素を更新する
     */
    public void updateAll(List<TangoListItem> list) {
        mRealm.beginTransaction();

        // いったんクリア
        RealmResults<TangoListItem> results = mRealm.where(TangoListItem.class).findAll();
        results.deleteAllFromRealm();

        // 全要素を追加
        for (TangoListItem item : list) {
            mRealm.copyToRealm(item);
        }

        mRealm.commitTransaction();
    }

    /**
     * かぶらないposを取得する
     * @param realm
     * @return
     */
    public int getNextPos(Realm realm) {
        // 初期化
        int nextId = 1;
        // userIdの最大値を取得
        Number maxUserId = realm.where(TangoListItem.class).max("pos");
        // 1度もデータが作成されていない場合はNULLが返ってくるため、NULLチェックをする
        if(maxUserId != null) {
            nextId = maxUserId.intValue() + 1;
        }
        return nextId;
    }
}
