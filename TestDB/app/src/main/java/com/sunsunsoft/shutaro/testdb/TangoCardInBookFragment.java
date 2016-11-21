package com.sunsunsoft.shutaro.testdb;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;
import android.view.View.OnClickListener;

/**
 * 単語帳に含まれるカード(TangoardInBook)のテスト用フラグメント
 */
public class TangoCardInBookFragment extends Fragment implements OnClickListener, TCardInBookDialogFragment.OnOkClickListener{

    private final static String BACKGROUND_COLOR = "background_color";
    public static final int REQUEST_CODE = 1;

    private ListView listView;
    private Button[] buttons = new Button[2];

    // ダイアログを呼び出しモード
    // 返り値を受け取るときに呼び出しモードに応じた処理を行う
    TCardInBookDialogFragment.DialogMode dialogMode = TCardInBookDialogFragment.DialogMode.Add;

    /**
     * 新しいFragmentを生成する
     *
     * @param color
     * @return
     */
    public static TangoCardInBookFragment newInstance(int color) {
        TangoCardInBookFragment frag = new TangoCardInBookFragment();
        Bundle b = new Bundle();
        b.putInt(BACKGROUND_COLOR, color);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tango_card_book, null);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_tango_card_linearlayout);
        linearLayout.setBackgroundColor(getArguments().getInt(BACKGROUND_COLOR));

        listView = (ListView) view.findViewById(R.id.listView);

        buttons[0] = (Button) view.findViewById(R.id.button);
        buttons[1] = (Button) view.findViewById(R.id.button2);

        for (Button button : buttons) {
            button.setOnClickListener(this);
        }

        showList();
        return view;
    }

    /**
     * クリックイベント
     *
     * @param v
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                addItemsByDialog();
                break;
            case R.id.button2:
                deleteItemsByDialog();
                break;
        }
    }

    /**
     * ListViewを最新のレコードで更新する
     */
    private void showList() {
        List<TangoBook> books = MyRealmManager.getBookDao().selectAll();
        TangoBookAdapter adapter = new TangoBookAdapter(getContext(), 0, books);
        listView.setAdapter(adapter);
    }

    /**
     * 単語カードを追加するためのダイアログを立ち上げる
     */
    protected void addItemsByDialog() {
        dialogMode = TCardInBookDialogFragment.DialogMode.Add;

        // リストのチェックのついた項目のIDを取得
        Integer[] ids = getCheckedIds();
        if (ids.length <= 0) return;

        int bookId = ids[0];

        DialogFragment dialogFragment = TCardInBookDialogFragment.createInstance(dialogMode, bookId);
        dialogFragment.setTargetFragment(TangoCardInBookFragment.this, 0);
        dialogFragment.show(getFragmentManager(), "fragment_dialog");
    }

    /**
     * 単語カードを削除するためのダイアログを立ち上げる
     */
    protected void deleteItemsByDialog() {
        dialogMode = TCardInBookDialogFragment.DialogMode.Delete;

        // リストのチェックのついた項目のIDを取得
        Integer[] ids = getCheckedIds();
        if (ids.length <= 0) return;

        int bookId = ids[0];

        DialogFragment dialogFragment = TCardInBookDialogFragment.createInstance(dialogMode, bookId);
        dialogFragment.setTargetFragment(TangoCardInBookFragment.this, 0);
        dialogFragment.show(getFragmentManager(), "fragment_dialog");
    }

    /**
     * チェックされた項目のIDを取得する
     */
    protected Integer[] getCheckedIds() {
        // チェックされた項目のIDを取得する
        LinkedList<Integer> idsList = new LinkedList<Integer>();
        TangoBookAdapter adapter = (TangoBookAdapter) listView.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            TangoBook book = adapter.getItem(i);
            if (book.isChecked()) {
                idsList.add(book.getId());
            }
        }
        return idsList.toArray(new Integer[0]);
    }

    /**
     * TCardDialogActivityからコールバックされるメソッド
     */
    @Override
    public void onOkClicked(Bundle args) {
        if (args != null) {
        }
    }
}
