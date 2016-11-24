package com.sunsunsoft.shutaro.tangobook;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * TangoItemPosのDAO
 */

public class TangoItemPosDao {
    public static final String TAG = "TangoItemPosDao";

    private Realm mRealm;

    public TangoItemPosDao(Realm realm) {
        mRealm = realm;
    }

    /**
     * 全要素取得
     *
     * @return
     */
    public List<TangoItemPos> selectAll() {
        RealmResults<TangoItemPos> results = mRealm.where(TangoItemPos.class)
                .findAllSorted("pos", Sort.ASCENDING);

        if (UDebug.debugDAO) {
            Log.d(TAG, "TangoItem selectAll");
            for (TangoItemPos item : results) {
                Log.d("TangoItemPosDao", ""
                        + " parentType:" + item.getParentType()
                        + " parentId:" + item.getParentId()
                        + " type:" + item.getItemType()
                        + " id:" + item.getId()
                        + " pos:" + item.getPos()
                );
            }
        }
        return results;
    }

    /**
     * 指定の親以下にあるアイテムを全て取得する
     */
    public List<TangoItemPos> selectByParentType(TangoParentType parentType) {
        RealmResults<TangoItemPos> results = mRealm.where(TangoItemPos.class)
                .equalTo("parentType", parentType.ordinal())
                .findAllSorted("pos", Sort.ASCENDING);
        return results;
    }


    /**
     * ホームのアイテムを取得
     *
     * @return
     */
    public List<TangoItem> selectItemsInHome(boolean changeable) {
        return selectItemsByParentType(TangoParentType.Home, changeable);
    }

    public List<TangoItem> selectItemsInTrash(boolean changeable) {
        return selectItemsByParentType(TangoParentType.Trash, changeable);
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
        for (int i = 0; i < results.size(); i++) {
            TangoItemPos item = results.get(i);
            ids[i] = item.getId();
        }
        return ids;
    }

    /**
     * 指定の単語帳に含まれるカードを取得する
     *
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
        for (int i = 0; i < results.size(); i++) {
            TangoItemPos item = results.get(i);
            ids[i] = item.getId();
        }
        List<TangoCard> cards = RealmManager.getCardDao().selectByIds(ids);

        return cards;
    }

    /**
     * 指定の親の配下にある全てのアイテムを取得する(主にホーム用)
     * アイテムのposでソート済みのリストを返す
     *
     * @param parentType
     * @return
     */
    public List<TangoItem> selectItemsByParentType(
            TangoParentType parentType, boolean changeable) {
        RealmResults<TangoItemPos> results = mRealm.where(TangoItemPos.class)
                .equalTo("parentType", parentType.ordinal())
                .findAll();
//                    .findAllSorted("pos", Sort.ASCENDING);
        if (results == null) return null;

        // 格納先
        List<TangoItem> items;

        // IDのリストを作成(カード)
        LinkedList<Integer> cardIds = new LinkedList<>();
        LinkedList<Integer> bookIds = new LinkedList<>();
        LinkedList<Integer> boxIds = new LinkedList<>();
        for (TangoItemPos item : results) {
            switch (TangoItemType.toEnum(item.getItemType())) {
                case Card:
                    cardIds.add(item.getId());
                    break;
                case Book:
                    bookIds.add(item.getId());
                    break;
                case Box:
                    boxIds.add(item.getId());
                    break;
            }
        }

        // 種類別にTangoItemを取得する
        // Card
        List<TangoCard> cards;
        if (cardIds.size() > 0) {
            cards = RealmManager.getCardDao()
                    .selectByIds(cardIds.toArray(new Integer[0]));
        } else {
            cards = new LinkedList<>();
        }

        // Book
        List<TangoBook> books;
        if (bookIds.size() > 0) {
            books = RealmManager.getBookDao()
                    .selectByIds(bookIds);
        } else {
            books = new LinkedList<>();
        }

        // Box
        List<TangoBox> boxes;
        if (boxIds.size() > 0) {
            boxes = RealmManager.getBoxDao()
                    .selectByIds(boxIds);
        } else {
            boxes = new LinkedList<>();
        }

        // posの順にリストを作成
        items = joinWithSort(cards, books, boxes);

        if (changeable) {
            items = toChangeableItem(items);
        }

        return items;
    }

    /**
     * 指定のParentType配下の TangoItemPos のリストを取得する
     *
     * @param parentType
     * @return
     */
    public List<TangoItemPos> selectItemPosesByParentType(TangoParentType parentType) {
        RealmResults<TangoItemPos> results = mRealm.where(TangoItemPos.class)
                .equalTo("parentType", parentType.ordinal())
                .findAll();
        return results;
    }

    /**
     * 指定のアイテム(TangoItemPos)を除外したアイテムを取得する
     *
     * @param excludeItemPoses
     * @param changeable
     * @return
     */
    public List<TangoItem> selectItemExcludeItemPoses(
            List<TangoItemPos> excludeItemPoses,
            boolean changeable) {
        // 各type毎に除外IDリストを作成
        LinkedList<Integer> cardIds = new LinkedList<>();
        LinkedList<Integer> bookIds = new LinkedList<>();
        LinkedList<Integer> boxIds = new LinkedList<>();

        for (TangoItemPos item : excludeItemPoses) {
            switch (TangoItemType.toEnum(item.getItemType())) {
                case Card:
                    cardIds.add(item.getId());
                    break;
                case Book:
                    bookIds.add(item.getId());
                    break;
                case Box:
                    boxIds.add(item.getId());
                    break;
            }
        }

        // 除外IDを使用して各Typeのリストを取得
        // 種類別にTangoItemを取得する
        // Card
        List<TangoCard> cards;
        cards = RealmManager.getCardDao()
                .selectExceptIds(cardIds);

        // Book
        List<TangoBook> books;
        books = RealmManager.getBookDao()
                .selectByExceptIds(bookIds);

        // Box
        List<TangoBox> boxes;
        boxes = RealmManager.getBoxDao()
                .selectByExceptIds(boxIds);


        // posの順にリストを作成
        List<TangoItem> items = joinWithSort(cards, books, boxes);
        if (changeable) {
            items = toChangeableItem(items);
        }
        return items;
    }

    /**
     * ３種類のアイテムリストを結合＆posが小さい順にソートする
     *
     * @param cards
     * @param books
     * @param boxes
     * @return
     */
    public List<TangoItem> joinWithSort(List<TangoCard> cards,
                                        List<TangoBook> books,
                                        List<TangoBox> boxes) {
        final int minInit = 10000000;
        LinkedList<TangoItem> items = new LinkedList<>();

        // posの順にリストを作成
        // 各ループでCard,Book,Boxのアイテムの中で一番小さいposのものを出力先のリストに追加する
        int[] indexs = new int[3];
        int[] poses = new int[3];

        for (int i = 0; i < 3; i++) {
            indexs[i] = 0;
        }

        // 各アイテムの先頭のposを取得する
        if (indexs[0] < cards.size()) {
            poses[0] = cards.get(indexs[0]).getPos();
        } else {
            poses[0] = minInit;
        }

        if (indexs[1] < books.size()) {
            poses[1] = books.get(indexs[1]).getPos();
        } else {
            poses[1] = minInit;
        }

        if (indexs[2] < boxes.size()) {
            poses[2] = boxes.get(indexs[2]).getPos();
        } else {
            poses[2] = minInit;
        }

        int totalCount = cards.size() + books.size() + boxes.size();
        int count = 0;

        while (true) {
            // 各アイテムリストの先頭のposを取得する
            int posMin = minInit;
            int gotTypeIndex = 0;
            for (int i = 0; i < 3; i++) {
                if (posMin > poses[i]) {
                    posMin = poses[i];
                    gotTypeIndex = i;
                }
            }
            switch (gotTypeIndex) {
                case 0:
                    if (indexs[0] < cards.size()) {
                        items.add(cards.get(indexs[0]));
                        // 取得したアイテムを持つリストを１つすすめる
                        indexs[gotTypeIndex]++;
                        count++;
                        if (indexs[0] < cards.size()) {
                            poses[0] = cards.get(indexs[0]).getPos();
                        } else {
                            poses[0] = minInit;
                        }
                    }
                    break;
                case 1:
                    if (indexs[1] < books.size()) {
                        items.add(books.get(indexs[1]));
                    }

                    indexs[1]++;
                    count++;
                    if (indexs[1] < books.size()) {
                        poses[1] = books.get(indexs[gotTypeIndex]).getPos();
                    } else {
                        poses[1] = 100000000;
                    }
                    break;
                case 2:
                    if (indexs[2] < boxes.size()) {
                        items.add(boxes.get(indexs[2]));
                    }

                    indexs[2]++;
                    count++;
                    if (indexs[2] < boxes.size()) {
                        poses[2] = boxes.get(indexs[2]).getPos();
                    } else {
                        poses[2] = 100000000;
                    }
                    break;
            }

            // 全ての要素をチェックし終わったら終了
            if (count >= totalCount) break;
        }

        return items;

    }

    /**
     * 指定のボックスに含まれるアイテムを取得する
     *
     * @param boxId
     * @param changeable
     * @return カード/単語帳 のアイテムリスト
     */
    public List<TangoItem> selectByBoxId(int boxId, boolean changeable) {
        RealmResults<TangoItemPos> results = mRealm.where(TangoItemPos.class)
                .equalTo("parentType", TangoParentType.Box.ordinal())
                .equalTo("parentId", boxId)
                .findAllSorted("pos", Sort.ASCENDING);
        if (results == null) return null;

        // 格納先を先に確保しておく
        LinkedList<TangoItem> items = new LinkedList<>();

        // IDのリストを作成(カード)
        LinkedList<Integer> cardIds = new LinkedList<>();
        LinkedList<Integer> bookIds = new LinkedList<>();
        for (TangoItemPos item : results) {
            switch (TangoItemType.toEnum(item.getItemType())) {
                case Card:
                    cardIds.add(item.getId());
                    break;
                case Book:
                    bookIds.add(item.getId());
                    break;
            }
        }

        List<TangoCard> cards;
        if (cardIds.size() > 0) {
            cards = RealmManager.getCardDao()
                    .selectByIds(cardIds.toArray(new Integer[0]));
        } else {
            cards = new LinkedList<>();
        }

        List<TangoBook> books;
        if (bookIds.size() > 0) {
            books = RealmManager.getBookDao()
                    .selectByIds(bookIds);
        } else {
            books = new LinkedList<>();
        }

        // posの順にリストを作成
        int cardIndex = 0;
        int bookIndex = 0;
        int cardPos;
        int bookPos;
        while (true) {
            if (cardIndex < cards.size()) {
                cardPos = cards.get(cardIndex).getPos();
            } else {
                cardPos = -1;
            }

            if (bookIndex < books.size()) {
                bookPos = books.get(bookIndex).getPos();
            } else {
                bookPos = -1;
            }

            if (cardPos > bookPos) {
                items.add(cards.get(cardIndex));
                cardIndex++;
            } else {
                items.add(books.get(bookIndex));
                bookIndex++;
            }
            if (cardIndex >= cards.size() &&
                    bookIndex >= books.size()) {
                break;
            }
        }
        // チェック
        if (cardIndex + bookIndex != items.size()) {
            Log.d(TAG, "not enoght!! cardIndex:" + cardIndex
                    + " bookIndex:" + bookIndex);
        }

        if (changeable) {
            items = toChangeableItem(items);
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
     * TangoItemPos
     *
     * @param list
     * @return
     */
    public List<TangoItemPos> toChangeableItemPos(List<TangoItemPos> list) {
        LinkedList<TangoItemPos> newList = new LinkedList<TangoItemPos>();
        for (TangoItemPos item : list) {
            newList.add(mRealm.copyFromRealm(item));
        }
        return newList;
    }

    /**
     * 変更不可なRealmのオブジェクトを変更可能なリストに変換する
     * TangoItem
     *
     * @param list
     * @return
     */
    public LinkedList<TangoItem> toChangeableItem(List<TangoItem> list) {
        LinkedList<TangoItem> newList = new LinkedList<>();
        for (TangoItem item : list) {
            // TangoItemのインスタンス別に処理する
            if (item instanceof TangoCard) {
                TangoCard _item = (TangoCard) item;
                newList.add((mRealm.copyFromRealm(_item)));
            } else if (item instanceof TangoBook) {
                TangoBook _item = (TangoBook) item;
                newList.add((mRealm.copyFromRealm(_item)));
            } else if (item instanceof TangoBox) {
                TangoBox _item = (TangoBox) item;
                newList.add((mRealm.copyFromRealm(_item)));
            }
        }
        return newList;
    }

    /**
     * 全要素削除
     *
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
     * @param ids    単語IDの配列
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
     *
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


    public void deleteItemPoses(List<TangoItemPos> items) {
        if (items == null) return;
        boolean isFirst = false;

        RealmQuery<TangoItemPos> query = mRealm.where(TangoItemPos.class);

        for (TangoItemPos item : items) {
            if (!isFirst) {
                isFirst = true;
            } else {
                query.or();
            }
            query.equalTo("parentType", item.getParentType())
                    .equalTo("itemType", item.getItemType())
                    .equalTo("id", item.getId());
        }
        RealmResults<TangoItemPos> results = query.findAll();
        if (results == null) return;

        mRealm.beginTransaction();
        results.deleteAllFromRealm();
        mRealm.commitTransaction();
    }

    /**
     * １アイテムを追加する
     * @param item
     * @param parentType
     * @param parentId
     */
    public void addOne(TangoItem item, TangoParentType parentType, int parentId) {
        TangoItemPos itemPos = new TangoItemPos();
        itemPos.setParentType(parentType.ordinal());
        itemPos.setParentId(parentId);
        itemPos.setItemType(item.getItemType().ordinal());
        itemPos.setId(item.getId());
        itemPos.setPos( getNextPos(parentType.ordinal(), parentId) );

        mRealm.beginTransaction();
        mRealm.copyToRealm(itemPos);
        mRealm.commitTransaction();
    }

    /**
     * 単語帳にカードを追加する
     *
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
            itemPos.setPos(getNextPosInBook(bookId));

            mRealm.beginTransaction();
            mRealm.copyToRealm(itemPos);
            mRealm.commitTransaction();
            pos++;
        }
    }

    /**
     * ボックスに要素(カード、単語帳)を追加する
     *
     * @param boxId
     * @param items
     */
    public void addItemsToBox(int boxId, List<TangoItem> items) {
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
            itemPos.setPos(getNextPosInBox(boxId));

            mRealm.beginTransaction();
            mRealm.copyToRealm(itemPos);
            mRealm.commitTransaction();
        }
    }

    /**
     * ボックスに要素の追加情報(TangoItemPos)を追加
     *
     * @param itemPoses
     */
    public void addItemPoses(List<TangoItemPos> itemPoses) {

        mRealm.beginTransaction();
        for (TangoItemPos itemPos : itemPoses) {
            int pos = getNextPos(itemPos.getParentType(), itemPos.getParentId());
            itemPos.setPos(pos);

            mRealm.copyToRealm(itemPos);
        }
        mRealm.commitTransaction();
    }

    /**
     * 追加先の親のタイプにあった最大posを取得する
     * @param parentType
     * @param parentId
     * @return
     */
    public int getNextPos(int parentType, int parentId) {
        switch (TangoParentType.toEnum(parentType)) {
            case Home:
                return getNextPos(TangoParentType.Home.ordinal());
            case Book:
                return getNextPosInBook(parentId);
            case Box:
                return getNextPosInBox(parentId);
            case Trash:
                return getNextPos(TangoParentType.Trash.ordinal());
        }
        return 0;
    }

    /**
     * アイテムの位置(pos)を変更する
     *
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
     *
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
     * ParentIdを指定しないHome/Trash用
     *
     * @param itemPoses 新しい配置
     */
    public void updateAll(List<TangoItemPos> itemPoses,
                          TangoParentType parentType, int parentId) {
        // Home/Trash以外なら処理しない
        RealmResults<TangoItemPos> results = null;
        if (parentType == TangoParentType.Home && parentType == TangoParentType.Trash) {
            results = mRealm.where(TangoItemPos.class)
                    .equalTo("parentType", parentType.ordinal())
                    .findAll();
        } else {
            results = mRealm.where(TangoItemPos.class)
                    .equalTo("parentType", parentType.ordinal())
                    .equalTo("parentId", parentId)
                    .findAll();
        }

        // いったんクリア
        if (results == null) return;

        mRealm.beginTransaction();
        results.deleteAllFromRealm();

        // 全要素を追加
        for (TangoItemPos item : itemPoses) {
            mRealm.copyToRealm(item);
        }

        mRealm.commitTransaction();
    }

    /**
     * かぶらないposを取得する
     *
     * @return
     */
    public int getNextPos(int parentType) {
        // 初期化
        int nextPos = 1;
        // 最大値を取得
        Number maxPos = mRealm.where(TangoItemPos.class)
                .equalTo("parentType", parentType)
                .max("pos");
        // 1度もデータが作成されていない場合はNULLが返ってくるため、NULLチェックをする
        if (maxPos != null) {
            nextPos = maxPos.intValue() + 1;
        }
        return nextPos;
    }


    public int getNextPosInBook(int bookId) {
        int nextPos = 1;
        Number maxPos = mRealm.where(TangoItemPos.class)
                .equalTo("parentType", TangoParentType.Book.ordinal())
                .equalTo("parentId", bookId)
                .max("pos");
        if (maxPos != null) {
            nextPos = maxPos.intValue() + 1;
        }
        return nextPos;
    }

    public int getNextPosInBox(int boxId) {
        int nextPos = 1;
        Number maxPos = mRealm.where(TangoItemPos.class)
                .equalTo("parentType", TangoParentType.Box.ordinal())
                .equalTo("parentId", boxId)
                .max("pos");
        if (maxPos != null) {
            nextPos = maxPos.intValue() + 1;
        }
        return nextPos;
    }

    /**
     * 移動系
     */
    /**
     * １アイテムを移動する
     *
     * @param item       移動元アイテム
     * @param parentType 移動先のType
     * @param parentId   移動先のId
     * @return
     */
    public boolean moveOne(TangoItem item, int parentType, int parentId) {
        TangoItemPos result = mRealm.where(TangoItemPos.class)
                .equalTo("itemType", item.getItemType().ordinal())
                .equalTo("id", item.getId())
                .findFirst();
        if (result == null) return false;
        mRealm.beginTransaction();
        result.setParentType(parentType);
        result.setParentId(parentId);
        mRealm.commitTransaction();

        return true;
    }

    /**
     * 複数のアイテムを移動する
     *
     * @param items
     * @param parentType 移動先のType
     * @param parentId   移動先のId
     * @return
     */
    public boolean moveItems(List<TangoItem> items, int parentType, int parentId) {
        RealmQuery<TangoItemPos> query = mRealm.where(TangoItemPos.class);

        boolean isFirst = true;

        for (TangoItem item : items) {
            if (isFirst) {
                isFirst = false;
            } else {
                query.or();
            }
            query.equalTo("itemType", item.getItemType().ordinal())
                    .equalTo("id", item.getId());
        }

        RealmResults<TangoItemPos> results = query.findAll();
        if (results == null) return false;

        // update
        mRealm.beginTransaction();
        for (TangoItemPos itemPos : results) {
            itemPos.setParentType(parentType);
            itemPos.setParentId(parentId);
        }
        mRealm.commitTransaction();
        return true;
    }

    /**
     * カード１つを移動する (Home->Book, Book->Box等の移動で使用可能)
     *
     * @param card       移動元のCard
     * @param parentType 移動先のParentType
     * @param parentId   移動先のParentId
     */
    public boolean moveCard(TangoCard card, TangoParentType parentType, int parentId) {
        return moveOne(card, parentType.ordinal(), parentId);
    }
}