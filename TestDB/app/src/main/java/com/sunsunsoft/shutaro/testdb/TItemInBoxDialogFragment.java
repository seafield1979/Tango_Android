package com.sunsunsoft.shutaro.testdb;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;
import android.view.View.OnClickListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class TItemInBoxDialogFragment extends DialogFragment implements OnClickListener {
    interface OnOkClickListener {
        void onOkClicked(Bundle args);
    }

    enum DialogMode {
        AddCards,
        AddBooks,
        Delete
    }

    public static final String KEY_MODE = "key_mode";
    public static final String KEY_BOX_ID = "key_box_id";
    public static final int[] buttonIds = {
            R.id.buttonOK,
            R.id.buttonCancel
    };

    DialogMode mMode;
    int mBoxId;

    private OnOkClickListener mListener;
    private ListView mListView;


    static TItemInBoxDialogFragment createInstance(DialogMode mode, int
            boxId) {
        TItemInBoxDialogFragment fragment = new TItemInBoxDialogFragment();

        // set arguments
        Bundle args = new Bundle();
        args.putInt(KEY_MODE, mode.ordinal());
        args.putInt(KEY_BOX_ID, boxId);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            Fragment targetFragment = this.getTargetFragment();
            if (targetFragment == null) {
                mListener = (OnOkClickListener)context;
            } else {
                mListener = (OnOkClickListener) targetFragment;
            }
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Don't implement OnCustomDialogListener.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 引数を取得
        Bundle args = getArguments();
        if (args != null) {
            DialogMode[] values = DialogMode.values();
            mMode = values[getArguments().getInt(KEY_MODE)];
            mBoxId = getArguments().getInt(KEY_BOX_ID);
        }

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tcard_in_book_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Buttons
        for (int id : buttonIds) {
            (view.findViewById(id)).setOnClickListener(this);
        }

        // ListView
        mListView = (ListView)view.findViewById(R.id.listView);
        switch (mMode) {
            case AddCards:
                showAddableCards();
                break;
            case AddBooks:
                showAddableBooks();
                break;
            case Delete:
                showItems(mBoxId);
                break;
        }

    }

    /**
     * 全てのカードを表示する
     */
    private void showAllCards() {
        List<TangoCard> cards = RealmManager.getCardDao().selectAll();
        cards = RealmManager.getCardDao().toChangeable(cards);
        TangoCardAdapter adapter = new TangoCardAdapter(getContext(), 0, cards);
        mListView.setAdapter(adapter);
    }

    /**
     * ボックスに未追加のカード番号を表示する
     */
    private void showAddableCards() {
        // すでに追加済みのカードは除外する
        // 追加済みのカードIDを取得
        List<TangoItem> itemsInBox = RealmManager.getItemPosDao().selectByBoxId(mBoxId, true);
        if (itemsInBox == null) return;

        LinkedList<Integer> cardIds = new LinkedList<>();

        for (TangoItem item : itemsInBox) {
            if(item instanceof TangoCard) {
                cardIds.add(item.getId());
            }
        }

        List<TangoCard> cards = RealmManager.getCardDao()
                .selectExceptIds(cardIds);
        cards = RealmManager.getCardDao().toChangeable(cards);

        TangoCardAdapter adapter = new TangoCardAdapter(getContext(), 0, cards);
        mListView.setAdapter(adapter);
    }

    /**
     * 全ての単語帳を表示する
     */
    private void showAllBooks() {
        List<TangoBook> books = RealmManager.getBookDao().selectAll();
        books = RealmManager.getBookDao().toChangeable(books);
        TangoBookAdapter adapter = new TangoBookAdapter(getContext(), 0, books);
        mListView.setAdapter(adapter);
    }

    /**
     * ボックスに未追加の単語帳を表示する
     */
    private void showAddableBooks() {
        // すでに追加済みのカードは除外する
        // 追加済みのカードIDを取得
        List<TangoItem> itemsInBox = RealmManager.getItemPosDao().selectByBoxId(mBoxId, true);
        if (itemsInBox == null) return;
        LinkedList<Integer> bookIds = new LinkedList<>();

        for (TangoItem item : itemsInBox) {
            if(item instanceof TangoBook) {
                bookIds.add(item.getId());
            }
        }

        List<TangoBook> books = RealmManager.getBookDao().selectByExceptIds(bookIds);
        books = RealmManager.getBookDao().toChangeable(books);
        TangoBookAdapter adapter = new TangoBookAdapter(getContext(), 0, books);
        mListView.setAdapter(adapter);
    }

    /**
     * 指定のボックスに含まれるアイテムを表示する
     */
    private void showItems(int boxId) {
        List<TangoItem> items = RealmManager.getItemPosDao().selectByBoxId(boxId, true);
        if (items == null) return;

        LinkedList<Integer> cardIds = new LinkedList<>();
        LinkedList<Integer> bookIds = new LinkedList<>();

        for (TangoItem item : items) {
            if (item instanceof TangoCard) {
                cardIds.add(item.getId());
            } else if (item instanceof  TangoBook){
                bookIds.add(item.getId());
            }
        }

        // アイテムのIDからカード、単語帳の情報を取得する
        TangoItemAdapter adapter = null;
        LinkedList<TangoItemInBoxList> itemLists = new LinkedList<>();

        if (cardIds.size() > 0) {
            List<TangoCard> cards = RealmManager.getCardDao()
                    .selectByIds(cardIds.toArray(new Integer[0]));
            cards = RealmManager.getCardDao().toChangeable(cards);
            if (cards != null) {
                for (TangoCard card : cards) {
                    TangoItemInBoxList itemList = new TangoItemInBoxList(TangoItemType.Card,
                            card.getId(),
                            card.getWordA());
                    itemLists.add(itemList);
                }
            }
        }
        if (bookIds.size() > 0) {
            List<TangoBook> books = RealmManager.getBookDao()
                    .selectByIds(bookIds);
            books = RealmManager.getBookDao().toChangeable(books);
            if (books != null) {
                for (TangoBook book : books) {
                    TangoItemInBoxList itemList = new TangoItemInBoxList(
                            TangoItemType.Book,
                            book.getId(),
                            book.getName());
                    itemLists.add(itemList);
                }
            }
        }
        adapter = new TangoItemAdapter(getContext(), 0, itemLists);

        mListView.setAdapter(adapter);
    }

    /**
     * 呼び出し元に引数を返して終了
     */
    private void submit() {
        switch(mMode) {
            case AddCards:
                // 追加
                addCardsToBox(mBoxId);
                break;
            case AddBooks:
                // 追加
                addBooksToBox(mBoxId);
                break;
            case Delete:
                // 削除
                deleteItemsFromBox(mBoxId);
                break;
        }

        dismiss();
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonOK:
                submit();
                break;
            case R.id.buttonCancel:
                dismiss();
                break;
        }
    }

    /**
     * チェックされたカードのIDを取得する
     */
    protected Integer[] getCheckedCardIds() {
        // チェックされた項目のIDを取得する
        LinkedList<Integer> idsList = new LinkedList<Integer>();
        ListAdapter adapter = mListView.getAdapter();

        // 取得したいアダプタと表示中のアダプタの種類が異なるなら何もしない
        if (!(adapter instanceof TangoCardAdapter)) return null;

        TangoCardAdapter cardAdapter = (TangoCardAdapter)adapter;

        for (int i = 0; i < cardAdapter.getCount(); i++) {
            TangoCard card = cardAdapter.getItem(i);
            if (card.isChecked()) {
                idsList.add(card.getId());
            }
        }
        return idsList.toArray(new Integer[0]);
    }

    /**
     * チェックされた単語帳のIDを取得する
     */
    protected Integer[] getCheckedBookIds() {
        // チェックされた項目のIDを取得する
        LinkedList<Integer> idsList = new LinkedList<Integer>();
        ListAdapter adapter = mListView.getAdapter();

        // 取得したいアダプタと表示中のアダプタの種類が異なるなら何もしない
        if (!(adapter instanceof TangoBookAdapter)) return null;

        TangoBookAdapter bookAdapter = (TangoBookAdapter)adapter;

        for (int i = 0; i < bookAdapter.getCount(); i++) {
            TangoBook book = bookAdapter.getItem(i);
            if (book.isChecked()) {
                idsList.add(book.getId());
            }
        }
        return idsList.toArray(new Integer[0]);
    }

    /**
     * チェックされた項目を取得する
     */
    protected TangoItemInBoxList[] getCheckedItems() {
        // チェックされた項目のIDを取得する
        LinkedList<TangoItemInBoxList> list = new LinkedList<>();
        TangoItemAdapter adapter = (TangoItemAdapter) mListView.getAdapter();
        if (adapter == null) return null;

        for (int i = 0; i < adapter.getCount(); i++) {
            TangoItemInBoxList item = adapter.getItem(i);
            if (item.isChecked()) {
                list.add(item);
            }
        }

        return list.toArray(new TangoItemInBoxList[0]);
    }



    /**
     * 指定のボックスに単語を追加する
     */
    protected void addCardsToBox(int boxId) {
        Integer[] cardIds = getCheckedCardIds();
        LinkedList<TangoItemPos> itemPoses = new LinkedList<>();
        for (int id : cardIds) {
            TangoItemPos itemPos = new TangoItemPos();
            itemPos.setParentType(TangoParentType.Box.ordinal());
            itemPos.setParentId(boxId);
            itemPos.setItemType(TangoItemType.Card.ordinal());
            itemPos.setId(id);

            itemPoses.add(itemPos);
        }

        if (itemPoses.size() > 0) {
            RealmManager.getItemPosDao().addItemPoses(itemPoses);
        }
    }

    /**
     * 指定のボックスに単語を追加する
     */
    protected void addBooksToBox(int boxId) {
        Integer[] bookIds = getCheckedBookIds();

        LinkedList<TangoItemPos> itemPoses = new LinkedList<>();
        for (int id : bookIds) {
            TangoItemPos itemPos = new TangoItemPos();
            itemPos.setParentType(TangoParentType.Box.ordinal());
            itemPos.setParentId(boxId);
            itemPos.setItemType(TangoItemType.Book.ordinal());
            itemPos.setId(id);

            itemPoses.add(itemPos);
        }

        if (bookIds != null) {
            RealmManager.getItemPosDao().addItemPoses(itemPoses);
        }
    }

    /**
     * 指定の単語帳から単語を削除する
     */
    protected void deleteItemsFromBox(int boxId) {
        TangoItemInBoxList[] items = getCheckedItems();
        if (items == null || items.length <= 0) return;

        LinkedList<TangoItemPos> deleteItems = new LinkedList<>();
        for (TangoItemInBoxList item : items) {
            TangoItemPos deleteItem = new TangoItemPos();
            deleteItem.setParentType(TangoParentType.Box.ordinal());
            deleteItem.setParentId(boxId);
            deleteItem.setItemType(item.getType().ordinal());
            deleteItem.setId(item.getItemId());

            deleteItems.add(deleteItem);
        }

        if (deleteItems != null) {
            RealmManager.getItemPosDao().deleteItemPoses(deleteItems);
        }
    }
}
