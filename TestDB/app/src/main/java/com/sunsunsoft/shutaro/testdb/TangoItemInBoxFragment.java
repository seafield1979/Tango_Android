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


/**
 * ボックス(TangoBox)に含まれる単語,単語帳のテストを行うフラグメント
 */
public class TangoItemInBoxFragment extends Fragment implements View.OnClickListener, TItemInBoxDialogFragment.OnOkClickListener{

    private final static String BACKGROUND_COLOR = "background_color";
    public static final int REQUEST_CODE = 1;

    private ListView listView;
    private Button[] buttons = new Button[3];

    // ダイアログを呼び出しモード
    // 返り値を受け取るときに呼び出しモードに応じた処理を行う
    TItemInBoxDialogFragment.DialogMode dialogMode = TItemInBoxDialogFragment.DialogMode
            .AddCards;

    /**
     * 新しいFragmentを生成する
     *
     * @param color
     * @return
     */
    public static TangoItemInBoxFragment newInstance(int color) {
        TangoItemInBoxFragment frag = new TangoItemInBoxFragment();
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
        View view = inflater.inflate(R.layout.fragment_tango_item_in_box, null);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.top_layout);
        linearLayout.setBackgroundColor(getArguments().getInt(BACKGROUND_COLOR));

        listView = (ListView) view.findViewById(R.id.listView);

        buttons[0] = (Button) view.findViewById(R.id.buttonAddCards);
        buttons[1] = (Button) view.findViewById(R.id.buttonAddBooks);
        buttons[2] = (Button) view.findViewById(R.id.buttonDelete);

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
            case R.id.buttonAddCards:
                addItemsByDialog(TItemInBoxDialogFragment.DialogMode.AddCards);
                break;
            case R.id.buttonAddBooks:
                addItemsByDialog(TItemInBoxDialogFragment.DialogMode.AddBooks);
                break;
            case R.id.buttonDelete:
                deleteItemsByDialog();
                break;
        }
    }

    /**
     * ListViewを最新のレコードで更新する
     */
    private void showList() {
        List<TangoBox> boxes = MyRealmManager.getBoxDao().selectAll();
        TangoBoxAdapter adapter = new TangoBoxAdapter(getContext(), 0, boxes);
        listView.setAdapter(adapter);
    }

    /**
     * 単語カードを追加するためのダイアログを立ち上げる
     */
    protected void addItemsByDialog(TItemInBoxDialogFragment.DialogMode mode) {
        dialogMode = mode;

        // リストのチェックのついた項目のIDを取得
        Integer[] ids = getCheckedIds();
        if (ids.length <= 0) return;

        int boxId = ids[0];

        DialogFragment dialogFragment = TItemInBoxDialogFragment.createInstance(dialogMode, boxId);
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getFragmentManager(), "fragment_dialog");
    }

    /**
     * 単語カードを削除するためのダイアログを立ち上げる
     */
    protected void deleteItemsByDialog() {
        dialogMode = TItemInBoxDialogFragment.DialogMode.Delete;

        // リストのチェックのついた項目のIDを取得
        Integer[] ids = getCheckedIds();
        if (ids.length <= 0) return;

        // 削除対象のボックスは１つだけ
        int boxId = ids[0];

        DialogFragment dialogFragment = TItemInBoxDialogFragment.createInstance(dialogMode, boxId);
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getFragmentManager(), "fragment_dialog");
    }

    /**
     * チェックされた項目のIDを取得する
     */
    protected Integer[] getCheckedIds() {
        // チェックされた項目のIDを取得する
        LinkedList<Integer> idsList = new LinkedList<Integer>();
        TangoBoxAdapter adapter = (TangoBoxAdapter) listView.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            TangoBox box = adapter.getItem(i);
            if (box.isChecked()) {
                idsList.add(box.getId());
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