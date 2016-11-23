package com.sunsunsoft.shutaro.testdb;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

import io.realm.RealmResults;

/**
 * 単語帳に含まれるカードを管理するDialogFragment
 */
public class TCardInBookDialogFragment extends DialogFragment implements View.OnClickListener {
    enum DialogMode {
        Add,
        Delete
    }

    public static final String KEY_MODE = "key_mode";
    public static final String KEY_BOOK_ID = "key_book_id";
    private static final int[] buttonIds = {
            R.id.buttonOK,
            R.id.buttonCancel
    };

    DialogMode mMode;
    int mBookId;

    private OnOkClickListener mListener;
    private ListView mListView;


    static TCardInBookDialogFragment createInstance(DialogMode mode, int bookId) {
        TCardInBookDialogFragment fragment = new TCardInBookDialogFragment();

        // set arguments
        Bundle args = new Bundle();
        args.putInt(KEY_MODE, mode.ordinal());
        args.putInt(KEY_BOOK_ID, bookId);

        fragment.setArguments(args);

        return fragment;
    }

    public interface OnOkClickListener {
        void onOkClicked(Bundle args);
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
            mBookId = getArguments().getInt(KEY_BOOK_ID);
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
            case Add:
                showAddableCards();
                break;
            case Delete:
                showCards(mBookId);
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
     * 未追加のカードを表示する
     */
    private void showAddableCards() {
        // 単語帳に含まれる
        Integer[] idsInBook = MyRealmManager.getItemPosDao().getCardIdsByBookId
                (mBookId);

        List<TangoCard> cards = MyRealmManager.getCardDao().selectExceptIds(idsInBook);
        cards = MyRealmManager.getCardDao().toChangeable(cards);
        TangoCardAdapter adapter = new TangoCardAdapter(getContext(), 0, cards);
        mListView.setAdapter(adapter);
    }

    /**
     * 指定の単語帳に含まれるカードを表示する
     */
    private void showCards(int bookId) {
        List<TangoCard> cards = MyRealmManager.getItemPosDao().selectCardsByBookId
                (bookId);
        if (cards == null) return;

        // 変更可能にする
        cards = MyRealmManager.getCardDao().toChangeable(cards);

        TangoCardAdapter adapter = new TangoCardAdapter(getContext(), 0, cards);
        mListView.setAdapter(adapter);
    }

    /**
     * 呼び出し元に引数を返して終了
     */
    private void submit() {
        if (mMode == DialogMode.Add) {
            // 追加
            addCardsToBook(mBookId);
        } else if (mMode == DialogMode.Delete) {
            // 削除
            deleteCardsFromBook(mBookId);
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
     * チェックされた項目のIDを取得する
     */
    protected Integer[] getCheckedCardIds() {
        // チェックされた項目のIDを取得する
        LinkedList<Integer> idsList = new LinkedList<Integer>();
        TangoCardAdapter adapter = (TangoCardAdapter) mListView.getAdapter();
        if (adapter == null) return null;

        for (int i = 0; i < adapter.getCount(); i++) {
            TangoCard card = adapter.getItem(i);
            if (card.isChecked()) {
                idsList.add(card.getId());
            }
        }

        return idsList.toArray(new Integer[0]);
    }

    /**
     * 指定の単語帳に単語を追加する
     */
    protected void addCardsToBook(int bookId) {
        Integer[] cardIds = getCheckedCardIds();

        if (cardIds != null) {
            MyRealmManager.getItemPosDao().addCardsInBook(bookId, cardIds);
        }
    }

    /**
     * 指定の単語帳から単語を削除する
     */
    protected void deleteCardsFromBook(int bookId) {
        Integer[] cardIds = getCheckedCardIds();

        if (cardIds != null) {
            MyRealmManager.getItemPosDao().addCardsInBook(bookId, cardIds);
        }
    }
}
