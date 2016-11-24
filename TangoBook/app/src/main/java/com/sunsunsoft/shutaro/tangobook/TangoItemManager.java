package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Color;

import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;


/**
 * ホームに表示するアイコン用の情報
 */
class HomeTangoItems {
    public List<TangoCard> cards;
    public List<TangoBook> books;
    public List<TangoBox> boxes;
}

/**
 * 単語帳のアイテム(カード、単語帳、ボックス)を管理する
 * 主にDBとアイコンの仲介を行う
 */

public class TangoItemManager {
    /**
     * Consts
     */

    /**
     * Member Variables
     */

    LinkedList<TangoItem> homeItems = new LinkedList<>();

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public TangoItemManager() {
    }

    /**
     * Methods
     */

    public TangoItem[] loadHomeIcons() {
        List<TangoItemPos> itemPos = RealmManager.getItemPosDao().selectAll();
        if (itemPos == null) {
            return null;
        }
        TangoItem[] items = new TangoItem[itemPos.size()];

        LinkedList<Integer> cardIds = new LinkedList<>();
        LinkedList<Integer> bookIds = new LinkedList<>();
        LinkedList<Integer> boxIds = new LinkedList<>();
        for (TangoItemPos pos : itemPos) {
            switch(TangoItemType.values()[pos.getItemType()]) {
                case Card:
                    cardIds.add(pos.getItemId());
                    break;
                case Book:
                    bookIds.add(pos.getItemId());
                    break;
                case Box:
                    boxIds.add(pos.getItemId());
                    break;
            }
        }

        // card
        List<TangoCard> cards = null;
        if (cardIds.size() > 0) {
            cards = RealmManager.getCardDao().selectByIds(cardIds);
        }
        // cardsの各要素にposを設定する処理
        int posIndex = 0;
        for (TangoCard card : cards) {
            while(posIndex < itemPos.size()) {
                TangoItemPos item = itemPos.get(posIndex);
                if (item.getItemType() == TangoItemType.Card.ordinal() &&
                        card.getId() == item.getItemId())
                {
                    if (item.getPos() < items.length) {
                        items[item.getPos()] = card;
                    }
                    posIndex++;
                    break;
                }
                posIndex++;
            }
        }

        // book
        List<TangoBook> books = null;
        if (bookIds.size() > 0) {
            books = RealmManager.getBookDao().selectByIds(bookIds);
        }
        // booksの各要素にposを設定する処理
        posIndex = 0;
        for (TangoBook book : books) {
            while(posIndex < itemPos.size()) {
                TangoItemPos item = itemPos.get(posIndex);
                if (item.getItemType() == TangoItemType.Book.ordinal() &&
                        book.getId() == item.getItemId())
                {
                    if (item.getPos() < items.length) {
                        items[item.getPos()] = book;
                    }
                    posIndex++;
                    break;
                }
                posIndex++;
            }
        }

        // box
        List<TangoBox> boxes = null;
        if (boxIds.size() > 0) {
            boxes = RealmManager.getBoxDao().selectByIds(boxIds);
        }
        // boxedの各要素にposを設定する処理
        posIndex = 0;
        for (TangoBox box : boxes) {
            while(posIndex < itemPos.size()) {
                TangoItemPos item = itemPos.get(posIndex);
                if (item.getItemType() == TangoItemType.Box.ordinal() &&
                        box.getId() == item.getItemId())
                {
                    if (item.getPos() < items.length) {
                        items[item.getPos()] = box;
                    }
                    posIndex++;
                    break;
                }
                posIndex++;
            }
        }

        return items;
    }

    /**
     * ホームに表示されているアイコンの並びを保存する
     * @param items
     */
    public void saveHomeIcons(TangoItem[] items) {
        LinkedList<TangoItemPos> list = new LinkedList<>();

        int pos = 0;
        int id = 0;
        for (TangoItem item : items) {
            TangoItemPos addItem = new TangoItemPos();

            // card
            if (item instanceof TangoCard) {
                TangoCard card = (TangoCard)item;
                addItem.setItemType(TangoItemType.Card.ordinal());
                id = card.getId();
            }
            // book
            if (item instanceof TangoBook) {
                TangoBook book = (TangoBook)item;
                addItem.setItemType(TangoItemType.Book.ordinal());
                id = book.getId();
            }
            // box
            if (item instanceof TangoBox) {
                TangoBox box = (TangoBox)item;
                addItem.setItemType(TangoItemType.Box.ordinal());
                id = box.getId();
            }
            addItem.setPos(pos);
            addItem.setItemId(id);
            list.add(addItem);
        }

        RealmManager.getItemPosDao().updateAll(list, TangoParentType.Home, 0);
    }

    /**
     * 単語カードを追加する
     */
    public TangoCard addCard() {
        return addCard("new", null, null, null, null);
    }

    public TangoCard addCard(String wordA, String wordB,
                            String hintAB, String hintBA,
                            String comment)
    {
        TangoCard card = new TangoCard();
        card.setWordA(wordA);
        card.setWordB(wordB);
        card.setHintAB(hintAB);
        card.setHintBA(hintBA);
        card.setComment(comment);

        // DBに保存
        RealmManager.getCardDao().addOne(card);

        return card;
    }

    /**
     * 単語帳を追加する
     */
    public TangoBook addBook() {
        return addBook("new", null, Color.WHITE);
    }

    public TangoBook addBook(String name, String comment, int color)
    {
        TangoBook book = new TangoBook();
        book.setName(name);
        book.setComment(comment);
        book.setColor(color);

        // DBに保存
        RealmManager.getBookDao().addOne(book);

        return book;
    }

    /**
     * ボックスを追加する
     */
    /**
     * 単語帳を追加する
     */
    public TangoBox addBox() {
        return addBox("new", null, Color.WHITE);
    }

    public TangoBox addBox(String name, String comment, int color)
    {
        TangoBox box = new TangoBox();
        box.setName(name);
        box.setComment(comment);
        box.setColor(color);

        // DBに保存
        RealmManager.getBoxDao().addOne(box);

        return box;
    }

    /**
     * カードを更新する
     * @return
     */
    public void updateCard(TangoCard card)
    {
        RealmManager.getCardDao().updateOne(card);
    }

    /**
     * 単語帳を更新する
     */
    public void udpateBook(TangoBook book) {
        RealmManager.getBookDao().updateOne(book);
    }

    /**
     * ボックスを更新する
     */
    public void updateBox(TangoBox box) {
        RealmManager.getBoxDao().updateOne(box);
    }

    /**
     * 移動系
     */
    /**
     * ２つのアイテムを入れ替える
     * @param item1
     * @param item2
     */
    public void changePos(TangoItem item1, TangoItem item2) {
        RealmManager.getItemPosDao().changePos(item1, item2);
    }
}
