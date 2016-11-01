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

/**
 * Created by shutaro on 2016/11/01.
 */

public class TCardBookDialogFragment extends DialogFragment implements View.OnClickListener {
    enum DialogMode {
        Add,
        Delete
    }

    public static final String KEY_MODE = "key_mode";
    public static final String KEY_BOOK_ID = "key_book_id";

    DialogMode mMode;
    int mBookId;

    private OnOkClickListener mListener;
    private ListView mListView;


    static TCardDialogFragment createInstance(DialogMode mode, int book_id) {
        TCardDialogFragment fragment = new TCardDialogFragment();

        // set arguments
        Bundle args = new Bundle();
        args.putInt(KEY_MODE, mode.ordinal());
        args.putInt(KEY_BOOK_ID, mode.ordinal());

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
        return inflater.inflate(R.layout.fragment_tcard_book_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Buttons
        ((Button)view.findViewById(R.id.buttonOK)).setOnClickListener(this);

        // ListView
        mListView = (ListView)view.findViewById(R.id.listView);
        switch (mMode) {
            case Add:
                showAllCards();
                break;
            case Delete:
                showCardsByBookId(mBookId);
                break;
        }

    }

    /**
     * 全てのカードを表示する
     */
    private void showAllCards() {
        List<TangoCard> cards = MyRealmManager.getCardDao().selectAll();
        TangoCardAdapter adapter = new TangoCardAdapter(getContext(), 0, cards);
        mListView.setAdapter(adapter);

        // todo
        // すでにその単語帳に含まれる単語はリストから除外する
    }

    /**
     * 指定の単語帳に含まれるカードを表示する
     */
    private void showCardsByBookId(int book_id) {

        // まずは TangoCardBook から指定の単語帳に含まれるカードのIDを取得する
        List<TangoCardBook> list = MyRealmManager.getCardBookDao().selectByBookId(book_id);
        LinkedList<Integer> idsList = new LinkedList<Integer>();
        for (TangoCardBook item : list) {
            idsList.add(item.getCardId());
        }

        if (idsList.size() <= 0) return;

        // カードのIDからカードの情報を取得する
        List<TangoCard> cards = MyRealmManager.getCardDao().selectByIds(idsList.toArray(new Integer[0]));
        TangoCardAdapter adapter = new TangoCardAdapter(getContext(), 0, cards);
        mListView.setAdapter(adapter);
    }

    /**
     * 呼び出し元に引数を返して終了
     */
    private void submit() {
        Bundle arg = new Bundle();
//        arg.putString(KEY_RET_NAME, mEditName.getText().toString());

        mListener.onOkClicked(arg);
        dismiss();
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonOK:
                submit();
                break;
        }
    }
}
