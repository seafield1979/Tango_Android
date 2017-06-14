package com.sunsunsoft.shutaro.tangobook.database;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by shutaro on 2016/12/22.
 *
 * 単語帳のデータの整合性をチェックする DAO
 *
 * 決して表示されないデータや古い履歴の削除等を行う
 */

public class TangoItemsCheckDao {
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
    public TangoItemsCheckDao(Realm realm) {
        mRealm = realm;
    }

    /**
     * 親を持たないアイテムを列挙
     */
    public List<TangoItem> selectNoParentItems() {
        RealmResults<TangoCard> cards = mRealm.where(TangoCard.class).findAll();
        RealmResults<TangoBook> books = mRealm.where(TangoBook.class).findAll();

        LinkedList<TangoItem> noParentItems = new LinkedList<>();
        LinkedList<TangoItem> items = new LinkedList<>();

        for (TangoItem item : cards) {
            items.add(item);
        }
        for (TangoItem item : books) {
            items.add(item);
        }

        if (items != null && items.size() > 0) {
            for (TangoItem item : items) {
                TangoItemPos pos = RealmManager.getItemPosDao().selectByItem(item);
                if (pos == null) {
                    noParentItems.add(item);
                }
            }
        }

        return noParentItems;
    }
}
