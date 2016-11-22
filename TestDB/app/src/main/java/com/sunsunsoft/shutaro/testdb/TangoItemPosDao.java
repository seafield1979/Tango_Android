package com.sunsunsoft.shutaro.testdb;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * TangoItemListのDAO
 */

public class TangoItemPosDao {

    private Realm mRealm;

    public TangoItemPosDao(Realm realm) {
        mRealm = realm;
    }

    /**
     * 全要素取得
     * @return nameのString[]
     */
    public List<TangoItemPos> selectAll() {
        RealmResults<TangoItemPos> results = mRealm.where(TangoItemPos.class).findAllSorted
                ("pos", Sort.ASCENDING);
        return results;
    }

    /**
     * 指定の単語帳に含まれるカードのIDを取得する
     */
    public Integer[] getCardIdsByBookId(int bookId) {
        RealmResults<TangoItemPos> results = mRealm.where(TangoItemPos.class)
                .equalTo("parentType", TangoParentType.Book.ordinal())
                .equalTo("parentId", bookId)
                .findAllSorted("pos", Sort.ASCENDING);
        if (results == null) return null;

        // IDのリストを作成
        Integer[] ids = new Integer[results.size()];
        for (int i=0; i<results.size(); i++) {
            TangoItemPos item = results.get(i);
            ids[i] = item.getId();
        }
        return ids;
    }

    /**
     * 指定の単語帳に含まれるカードを取得する
     * @param bookId
     * @return
     */
    public List<TangoCard> selectCardsByBookId(int bookId) {
        RealmResults<TangoItemPos> results = mRealm.where(TangoItemPos.class)
                .equalTo("parentType", TangoParentType.Book.ordinal())
                .equalTo("parentId", bookId)
                .findAllSorted("pos", Sort.ASCENDING);
        if (results == null) return null;

        // IDのリストを作成
        Integer[] ids = new Integer[results.size()];
        for (int i=0; i<results.size(); i++) {
            TangoItemPos item = results.get(i);
            ids[i] = item.getId();
        }
        List<TangoCard> cards = MyRealmManager.getCardDao().selectByIds(ids);

        return cards;
    }

    /**
     * 指定のボックスに含まれるアイテムを取得する
     * @param boxId
     * @return カード/単語帳 のアイテムリスト
     */
    public TangoItem[] selectByBoxId(int boxId) {
        RealmResults<TangoItemPos> results = mRealm.where(TangoItemPos.class)
                .equalTo("parentType", TangoParentType.Box.ordinal())
                .equalTo("parentId", boxId)
                .findAllSorted("pos", Sort.ASCENDING);
        if (results == null) return null;

        // 格納先を先に確保しておく
        TangoItem[] items = new TangoItem[results.size()];

        // IDのリストを作成(カード)
        LinkedList<Integer> ids = new LinkedList<>();
        for (TangoItemPos item : results) {
            if (item.getItemType() == TangoItemType.Card.ordinal()) {
                ids.add(item.getId());
            }
        }
        if (ids.size() > 0) {
            // カードのリストを作成
            List<TangoCard> cards = MyRealmManager.getCardDao().selectByIds(ids.toArray(new
                    Integer[0]));
            if (cards != null) {
                // posの並び順に格納
                int posIndex = 0;
                for (TangoCard card : cards) {
                    while (posIndex < items.length) {
                        TangoItemPos itemPos = results.get(posIndex);
                        if (itemPos.getItemType() == TangoItemType.Card.ordinal() &&
                                card.getId() == itemPos.getId()) {
                            if (itemPos.getPos() < items.length) {
                                items[itemPos.getPos()] = card;
                            }
                            posIndex++;
                            break;
                        }
                        posIndex++;
                    }
                }
            }
        }

        // IDのリストを作成(単語帳)
        ids.clear();
        if (ids.size() > 0) {
            for (TangoItemPos item : results) {
                if (item.getItemType() == TangoItemType.Book.ordinal()) {
                    ids.add(item.getId());
                }
            }

            // カードのリストを作成
            List<TangoBook> books = MyRealmManager.getBookDao().selectByIds(ids.toArray(new
                    Integer[0]));
            if (books != null) {
                // posの並び順に格納
                int posIndex = 0;
                for (TangoBook book : books) {
                    while (posIndex < items.length) {
                        TangoItemPos itemPos = results.get(posIndex);
                        if (itemPos.getItemType() == TangoItemType.Book.ordinal() &&
                                book.getId() == itemPos.getId()) {
                            if (itemPos.getPos() < items.length) {
                                items[itemPos.getPos()] = book;
                            }
                            posIndex++;
                            break;
                        }
                        posIndex++;
                    }
                }
            }
        }

        return items;
    }

    public static List<Integer> listToIds(List<TangoCard> list) {
        LinkedList<Integer> ids = new LinkedList<>();

        for (TangoCard obj : list) {
            ids.add(obj.getId());
        }
        return ids;
    }

    /**
     * 変更不可なRealmのオブジェクトを変更可能なリストに変換する
     * @param list
     * @return
     */
    public List<TangoItemPos> toChangeable(List<TangoItemPos> list) {
        LinkedList<TangoItemPos> newList = new LinkedList<TangoItemPos>();
        for (TangoItemPos item : list) {
            newList.add(mRealm.copyFromRealm(item));
        }
        return newList;
    }


    /**
     * 全要素削除
     * @return
     */
    public boolean deleteAll() {
        RealmResults<TangoItemPos> results = mRealm.where(TangoItemPos.class).findAll();
        mRealm.beginTransaction();
        boolean ret = results.deleteAllFromRealm();
        mRealm.commitTransaction();
        return ret;
    }

    /**
     * IDの位置リストに一致する項目を全て削除する
     */
    public void deletePositions(Integer[] positions) {
        if (positions.length <= 0) return;

        mRealm.beginTransaction();

        RealmQuery<TangoItemPos> query = mRealm.where(TangoItemPos.class);

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
        RealmResults<TangoItemPos> results = query.findAll();

        results.deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    /**
     * 指定の単語帳に含まれるカードを削除する
     *
     * @param bookId
     * @param ids  単語IDの配列
     */
    public void deteteCardsInBook(int bookId, Integer[] ids) {
        if (ids.length <= 0) return;

        RealmQuery<TangoItemPos> query = mRealm.where(TangoItemPos.class);

        // Add query conditions:
        query.equalTo("parentType", TangoParentType.Book.ordinal())
                .equalTo("parentId", bookId)
                .equalTo("itemType", TangoItemType.Card.ordinal());

        boolean isFirst = true;
        for (int id : ids) {
            if (isFirst) {
                isFirst = false;
            } else {
                query.or();
            }
            query.equalTo("id", id);
        }

        RealmResults<TangoItemPos> results = query.findAll();

        mRealm.beginTransaction();
        results.deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    /**
     * 指定のボックスに含まれるアイテムを削除する
     * @param boxId
     * @param items
     */
    public void deleteItemsInBox(int boxId, List<TangoItem> items) {
        if (items == null) return;
        boolean isFirst = false;

        RealmQuery<TangoItemPos> query = mRealm.where(TangoItemPos.class);

        // Add query conditions:
        query.equalTo("parentType", TangoParentType.Box.ordinal())
                .equalTo("parentId", boxId);

        for (TangoItem item : items) {
            if (!isFirst) {
                isFirst = true;
            } else {
                query.or();
            }
            if (item instanceof TangoCard) {
                query.equalTo("itemType", TangoItemType.Card.ordinal())
                        .equalTo("id", item.getId());
            } else if (item instanceof TangoBook) {
                query.equalTo("itemType", TangoItemType.Book.ordinal())
                        .equalTo("id", item.getId());
            }
            RealmResults<TangoItemPos> results = query.findAll();
            if (results == null) return;

            mRealm.beginTransaction();
            results.deleteAllFromRealm();
            mRealm.commitTransaction();
        }
    }


    /**
     * 要素を追加する
     */
    public void addOne(TangoItemPos item) {
        item.setPos(getNextPos(mRealm));

        mRealm.beginTransaction();
        mRealm.copyToRealm(item);
        mRealm.commitTransaction();
    }

    /**
     * 単語帳にカードを追加する
     * @param bookId
     * @param cardIds
     */
    public void addCardsInBook(int bookId, Integer[] cardIds) {
        int pos = 0;

        for (int id : cardIds) {
            TangoItemPos itemPos = new TangoItemPos();
            itemPos.setParentType(TangoParentType.Book.ordinal());
            itemPos.setParentId(bookId);
            itemPos.setItemType(TangoItemType.Card.ordinal());
            itemPos.setId(id);
            itemPos.setPos(pos);

            mRealm.beginTransaction();
            mRealm.copyToRealm(itemPos);
            mRealm.commitTransaction();
            pos++;
        }
    }

    /**
     * ボックスに要素(カード、単語帳)を追加する
     * @param boxId
     * @param items
     */
    public void addItemsInBox(int boxId, List<TangoItem> items) {
        int itemType = 0;

        for (TangoItem item : items) {
            TangoItemPos itemPos = new TangoItemPos();
            itemPos.setParentType(TangoParentType.Box.ordinal());
            itemPos.setParentId(boxId);
            if (item instanceof TangoCard) {
                itemType = TangoItemType.Card.ordinal();
            } else if (item instanceof TangoBook) {
                itemType = TangoItemType.Book.ordinal();
            }
            itemPos.setItemType(itemType);
            itemPos.setId(item.getId());
            itemPos.setPos(item.getPos());

            mRealm.beginTransaction();
            mRealm.copyToRealm(itemPos);
            mRealm.commitTransaction();
        }
    }

    /**
     * アイテムの位置(pos)を変更する
     * @param oldPos
     * @param newPos
     */
    public void updatePos(int oldPos, int newPos) {
        TangoItemPos item = mRealm.where(TangoItemPos.class).equalTo("pos", oldPos).findFirst();
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
        TangoItemPos item1 = mRealm.where(TangoItemPos.class).equalTo("pos", pos1).findFirst();
        TangoItemPos item2 = mRealm.where(TangoItemPos.class).equalTo("pos", pos2).findFirst();
        if (item1 == null || item2 == null) return;

        mRealm.beginTransaction();
        item1.setPos(pos2);
        item2.setPos(pos1);
        mRealm.commitTransaction();
    }

    /**
     * リストの全要素を更新する
     */
    public void updateAll(List<TangoItemPos> list) {
        mRealm.beginTransaction();

        // いったんクリア
        RealmResults<TangoItemPos> results = mRealm.where(TangoItemPos.class).findAll();
        results.deleteAllFromRealm();

        // 全要素を追加
        for (TangoItemPos item : list) {
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
        Number maxUserId = realm.where(TangoItemPos.class).max("pos");
        // 1度もデータが作成されていない場合はNULLが返ってくるため、NULLチェックをする
        if(maxUserId != null) {
            nextId = maxUserId.intValue() + 1;
        }
        return nextId;
    }
}
