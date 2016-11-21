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



/**
 * A simple {@link Fragment} subclass.
 */
public class TItemInBoxDialogFragment extends DialogFragment implements View.OnClickListener {
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
        List<TangoCard> cards = MyRealmManager.getCardDao().selectAll();
        cards = MyRealmManager.getCardDao().toChangeable(cards);
        TangoCardAdapter adapter = new TangoCardAdapter(getContext(), 0, cards);
        mListView.setAdapter(adapter);
    }

    /**
     * ボックスに未追加のカード番号を表示する
     */
    private void showAddableCards() {
        List<TangoItemInBox> itemsInBox = MyRealmManager.getItemInBoxDao().selectByBoxId
                (mBoxId);
        LinkedList<Integer> cardIds = new LinkedList<>();

        for (TangoItemInBox item : itemsInBox) {
            if(item.getItemType() == TangoItemType.Card.ordinal()) {
                cardIds.add(item.getItemId());
            }
        }

        List<TangoCard> cards = MyRealmManager.getCardDao().selectExceptIds(cardIds);
        cards = MyRealmManager.getCardDao().toChangeable(cards);
        TangoCardAdapter adapter = new TangoCardAdapter(getContext(), 0, cards);
        mListView.setAdapter(adapter);
    }

    /**
     * 全ての単語帳を表示する
     */
    private void showAllBooks() {
        List<TangoBook> books = MyRealmManager.getBookDao().selectAll();
        books = MyRealmManager.getBookDao().toChangeable(books);
        TangoBookAdapter adapter = new TangoBookAdapter(getContext(), 0, books);
        mListView.setAdapter(adapter);
    }

    /**
     * ボックスに未追加の単語帳を表示する
     */
    private void showAddableBooks() {
        List<TangoItemInBox> itemsInBox = MyRealmManager.getItemInBoxDao().selectByBoxId
                (mBoxId);
        LinkedList<Integer> bookIds = new LinkedList<>();

        for (TangoItemInBox item : itemsInBox) {
            if(item.getItemType() == TangoItemType.Book.ordinal()) {
                bookIds.add(item.getItemId());
            }
        }

        List<TangoBook> books = MyRealmManager.getBookDao().selectExceptIds(bookIds);
        books = MyRealmManager.getBookDao().toChangeable(books);
        TangoBookAdapter adapter = new TangoBookAdapter(getContext(), 0, books);
        mListView.setAdapter(adapter);
    }

    /**
     * 指定の単語帳に含まれるカードを表示する
     */
    private void showItems(int boxId) {
        List<TangoItemInBox> items = MyRealmManager.getItemInBoxDao().selecteByBoxId(boxId);

        if (items.size() <= 0) return;

        LinkedList<Integer> cardIds = new LinkedList<>();
        LinkedList<Integer> bookIds = new LinkedList<>();

        for (TangoItemInBox item : items) {
            if (item.getItemType() == TangoItemType.Card.ordinal()) {
                cardIds.add(item.getItemId());
            } else {
                bookIds.add(item.getItemId());
            }
        }

        // アイテムのIDからカード、単語帳の情報を取得する
        TangoItemAdapter adapter = null;
        LinkedList<TangoItemInBoxList> itemLists = new LinkedList<>();

        if (cardIds.size() > 0) {
            List<TangoCard> cards = MyRealmManager.getCardDao()
                    .selectByIds(cardIds.toArray(new Integer[0]));
            //cards = MyRealmManager.getCardDao().toChangeable(cards);
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
            List<TangoBook> books = MyRealmManager.getBookDao()
                    .selectByIds(bookIds.toArray(new Integer[0]));
            //books = MyRealmManager.getBookDao().toChangeable(books);
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

        if (cardIds != null) {
            MyRealmManager.getItemInBoxDao().addCards(boxId, cardIds);
        }
    }

    /**
     * 指定のボックスに単語を追加する
     */
    protected void addBooksToBox(int boxId) {
        Integer[] bookIds = getCheckedBookIds();

        if (bookIds != null) {
            MyRealmManager.getItemInBoxDao().addBooks(boxId, bookIds);
        }
    }

    /**
     * 指定の単語帳から単語を削除する
     */
    protected void deleteItemsFromBox(int boxId) {
        TangoItemInBoxList[] items = getCheckedItems();

        if (items != null) {
            MyRealmManager.getItemInBoxDao().deleteByItems(boxId, items);
        }
    }
}
