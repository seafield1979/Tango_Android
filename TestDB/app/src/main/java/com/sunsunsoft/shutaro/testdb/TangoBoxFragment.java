package com.sunsunsoft.shutaro.testdb;


import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.view.View.OnClickListener;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TangoBoxFragment extends Fragment implements OnClickListener, TBoxDialogFragment.OnOkClickListener {
    // Enums
    enum DialogMode {
        Add,
        Update
    }

    private final static String BACKGROUND_COLOR = "background_color";
    public static final int REQUEST_CODE = 1;

    private ListView listView;
    private Button[] buttons = new Button[4];

    // ダイアログを呼び出しモード
    // 返り値を受け取るときに呼び出しモードに応じた処理を行う
    DialogMode dialogMode = DialogMode.Add;


    /**
     * 新しいFragmentを生成する
     * @param IdRes
     * @return
     */
    public static TangoBoxFragment newInstance(@ColorRes int IdRes) {
        TangoBoxFragment frag = new TangoBoxFragment();
        Bundle b = new Bundle();
        b.putInt(BACKGROUND_COLOR, IdRes);
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
        View view = inflater.inflate(R.layout.fragment_page1, null);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_page1_linearlayout);
        linearLayout.setBackgroundResource(getArguments().getInt(BACKGROUND_COLOR));

        listView = (ListView)view.findViewById(R.id.listView);

        buttons[0] = (Button)view.findViewById(R.id.button);
        buttons[1] = (Button)view.findViewById(R.id.button2);
        buttons[2] = (Button)view.findViewById(R.id.button3);
        buttons[3] = (Button)view.findViewById(R.id.button4);

        for (Button button : buttons) {
            button.setOnClickListener(this);
        }

        showList();
        return view;
    }

    /**
     * クリックイベント
     * @param v
     */
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button:
                showList();
                break;
            case R.id.button2:
                addItemByDialog();
                break;
            case R.id.button3:
                updateItemByDialog();
                break;
            case R.id.button4:
                deleteItems();
                break;
        }
    }

    /**
     * ListViewを最新のレコードで更新する
     */
    private void showList() {
        List<TangoBox> boxes = RealmManager.getBoxDao().selectAll();
        TangoBoxAdapter adapter = new TangoBoxAdapter(getContext(), 0, boxes);
        listView.setAdapter(adapter);
    }

    /**
     * チェックされた項目を１つ更新する
     * @param box
     */
    private void updateCheckedItemOne(TangoBox box) {
        // チェックされた項目のIDを取得する
        Integer[] checkedIds = getCheckedIds();

        if (checkedIds.length <= 0) return;

        RealmManager.getBoxDao().updateOne(box);
        showList();
    }

    /**
     * チェックされた項目を削除する
     */
    private void deleteItems() {
        Integer[] checkedIds = getCheckedIds();

        RealmManager.getBoxDao().deleteIds(checkedIds);
        showList();
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
     * 単語カードを追加するためのダイアログを立ち上げる
     */
    protected void addItemByDialog() {
        dialogMode = DialogMode.Add;
        TangoBox box = TangoBox.createDummy();

        DialogFragment dialogFragment = TBoxDialogFragment.createInstance(box);
        dialogFragment.setTargetFragment(TangoBoxFragment.this, 0);
        dialogFragment.show(getFragmentManager(), "fragment_dialog");
    }

    /**
     * 単語カードを更新するためのダイアログを立ち上げる
     * Fragmentの戻り値でカードを更新
     */
    protected void updateItemByDialog() {
        Integer[] ids = getCheckedIds();
        if (ids.length <= 0) return;

        TangoBox box = RealmManager.getBoxDao().selectById(ids[0]);

        if (box == null) return;

        dialogMode = DialogMode.Update;
        DialogFragment dialogFragment = TBoxDialogFragment.createInstance(box);
        dialogFragment.setTargetFragment(TangoBoxFragment.this, 0);
        dialogFragment.show(getFragmentManager(), "fragment_dialog");
    }

    /*
     * TBoxDialogActivityからコールバックされるメソッド
     */
    @Override
    public void onOkClicked(Bundle args) {
        if (args != null) {
            // ダイアログの戻り値を取得
            String name = args.getString(TBoxDialogFragment.KEY_RET_NAME);
            int color = args.getInt(TBoxDialogFragment.KEY_RET_COLOR);
            String comment = args.getString(TBoxDialogFragment.KEY_RET_COMMENT);

            switch(dialogMode) {
                case Add:
                {
                    // カードを追加する
                    TangoBox box = new TangoBox();
                    box.setName(name);
                    box.setColor(color);
                    box.setComment(comment);
                    box.setCreateTime(new Date());
                    box.setUpdateTime(new Date());

                    RealmManager.getBoxDao().addOne(box);
                    showList();
                }
                break;
                case Update:
                {
                    // チェックされた項目のカードを更新する
                    Integer[] ids = getCheckedIds();
                    if (ids.length <= 0) return;

                    TangoBox box = RealmManager.getBoxDao().selectById(ids[0]);
                    box.setName(name);
                    box.setColor(color);
                    box.setComment(comment);
                    box.setUpdateTime(new Date());

                    updateCheckedItemOne(box);
                }
                break;
            }
        }
    }
}
