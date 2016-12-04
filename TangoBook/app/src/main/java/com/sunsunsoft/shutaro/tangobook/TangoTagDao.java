package com.sunsunsoft.shutaro.tangobook;

import android.util.Log;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by shutaro on 2016/12/02.
 *
 * TangoTag/TangoTagInfo モデルのDAO
 * Card/BookにつけるTagのモデルを操作する
 */

public class TangoTagDao {
    /**
     * Constants
     */
    public static final String TAG = "TangoTagDao";

    /**
     * Member variables
     */
    private Realm mRealm;

    /**
     * Constructor
     * @param realm
     */
    public TangoTagDao(Realm realm) {
        mRealm = realm;
    }


    /**
     * 取得系(Selection type)
     */
    /**
     * TangoTag 全要素取得
     * @return nameのString[]
     */
    public List<TangoTag> selectAll() {

        RealmResults<TangoTag> results = mRealm.where(TangoTag.class).findAll();

        if (UDebug.debugDAO) {
            Log.d(TAG, "TangoTag selectAll");
            for (TangoTag tag : results) {
                Log.d(TAG, "itemType:" + tag.getItemType() +
                        " itemid:" + tag.getItemId());
            }
        }
        return results;
    }

    public List<TangoTag> selectByItem(TangoItem item) {
        RealmResults<TangoTag> results = mRealm.where(TangoTag.class)
                .equalTo("itemType", item.getItemType().ordinal())
                .equalTo("itemId", item.getId())
                .findAll();
        return results;
    }

    /**
     * 指定の単語アイテムのタグを取得する
     * @param item
     * @return  タグ名リスト
     */
    public List<TangoTagInfo> getTagNamesByItem(TangoItem item) {
        List<TangoTag> tags = selectByItem(item);

        if (tags == null || tags.size() == 0) return null;

        // tagIdを元にTangoTagInfoの情報を参照
        RealmQuery<TangoTagInfo> query = mRealm.where(TangoTagInfo.class);
        boolean isFirst = true;
        for (TangoTag tag : tags) {
            if (isFirst) {
                isFirst = false;
            } else {
                query.or();
            }
            query.equalTo("id", tag.getTagId());
        }

        RealmResults<TangoTagInfo> results = query.findAll();

        return results;
    }

    /**
     * 指定のタグ名のレコードがすでに存在するかどうかを判定する
     * @param tagName
     * @return
     */
    public int getTagInfoId(String tagName) {
        TangoTagInfo result = mRealm.where(TangoTagInfo.class)
                .equalTo("name", tagName)
                .findFirst();

        if (result == null) return 0;
        return result.getId();
    }

    /**
     * 追加系 (Addition type)




    /**
     * TangoTagInfo 要素を追加
     * @param
     * @return 追加したTagInfoのId
     */
    public int addTagInfo(String name) {
        int newId = getNextId();

        int tagId = getTagInfoId(name);
        // 登録済みならそれを返す
        if (tagId >0) {
            return tagId;
        }

        TangoTagInfo tag = new TangoTagInfo();
        tag.setId(newId);
        tag.setName(name);

        mRealm.beginTransaction();
        mRealm.copyToRealm(tag);
        mRealm.commitTransaction();
        return newId;
    }

    /**
     * 単語アイテムにタグを追加する（TangoTagにTangoTagInfoを関連づける）
     * @param item
     * @param tagName
     * @return
     */
    public boolean addTag(TangoItem item, String tagName) {
        // タグのIDを取得（なければ作成）
        int tagId = addTagInfo(tagName);

        TangoTag tag = new TangoTag();
        tag.setItemType(item.getItemType().ordinal());
        tag.setItemId(item.getId());
        tag.setTagId(tagId);

        mRealm.beginTransaction();
        mRealm.copyToRealm(tag);
        mRealm.commitTransaction();

        return true;
    }

    /**
     * 削除系 (Delete type)
     */
    /**
     * 全削除 for Debug
     */
    public void deleteAll() {
        RealmResults<TangoTagInfo> results = mRealm.where(TangoTagInfo.class).findAll();
        mRealm.beginTransaction();
        results.deleteAllFromRealm();
        mRealm.commitTransaction();

        RealmResults<TangoTag> results2 = mRealm.where(TangoTag.class).findAll();
        mRealm.beginTransaction();
        results2.deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    /**
     * 指定のアイテムのタグ情報を削除
     * @param item
     * @return
     */
    public boolean deleteByItem(TangoItem item) {
        RealmResults<TangoTag> results = mRealm.where(TangoTag.class)
                .equalTo("itemType", item.getItemType().ordinal())
                .equalTo("itemId", item.getId())
                .findAll();

        if (results == null || results.size() == 0) return false;

        mRealm.beginTransaction();
        results.deleteAllFromRealm();
        mRealm.commitTransaction();
        return true;
    }

    /**
     * 指定のタグ名のTagInfoレコードを削除する
     * @param tagName
     * @return
     */
    public boolean deleteByTagName(String tagName) {
        TangoTagInfo result = mRealm.where(TangoTagInfo.class)
                .equalTo("name", tagName)
                .findFirst();

        if (result == null) return false;

        mRealm.beginTransaction();
        result.deleteFromRealm();
        mRealm.commitTransaction();
        return true;
    }


    /**
     * かぶらないプライマリIDを取得する
     * @return
     */
    public int getNextId() {
        int nextId = 1;
        Number maxId = mRealm.where(TangoTagInfo.class).max("id");
        if(maxId != null) {
            nextId = maxId.intValue() + 1;
        }
        return nextId;
    }
}
