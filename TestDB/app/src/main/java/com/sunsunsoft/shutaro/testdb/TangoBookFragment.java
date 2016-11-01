package com.sunsunsoft.shutaro.testdb;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TangoBookFragment extends Fragment implements OnClickListener, TBookDialogFragment.OnOkClickListener {
    // Enums
    enum DialogMode {
        Add,
        Update
    }

    private final static String BACKGROUND_COLOR = "background_color";
    public static final int REQUEST_CODE = 1;

    // データベースモデル
    TangoBookDao mBookDao;

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
    public static TangoBookFragment newInstance(@ColorRes int IdRes) {
        TangoBookFragment frag = new TangoBookFragment();
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

        // DAOの準備
        mBookDao = new TangoBookDao(getActivity());

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
        List<TangoBook> books = mBookDao.selectAll();
        TangoBookAdapter adapter = new TangoBookAdapter(getContext(), 0, books);
        listView.setAdapter(adapter);
    }

    /**
     * チェックされた項目を１つ更新する
     * @param book
     */
    private void updateCheckedItemOne(TangoBook book) {
        // チェックされた項目のIDを取得する
        Integer[] checkedIds = getCheckedIds();

        if (checkedIds.length <= 0) return;

        mBookDao.updateOne(checkedIds[0], book);
        showList();
    }

    /**
     * チェックされた項目を削除する
     */
    private void deleteItems() {
        Integer[] checkedIds = getCheckedIds();

        mBookDao.deleteIds(checkedIds);
        showList();
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
     * 単語カードを追加するためのダイアログを立ち上げる
     */
    protected void addItemByDialog() {
        dialogMode = DialogMode.Add;

        TangoBook book = TangoBook.createDummy();

        DialogFragment dialogFragment = TBookDialogFragment.createInstance(book);
        dialogFragment.setTargetFragment(TangoBookFragment.this, 0);
        dialogFragment.show(getFragmentManager(), "fragment_dialog");
    }

    /**
     * 単語カードを更新するためのダイアログを立ち上げる
     * Fragmentの戻り値でカードを更新
     */
    protected void updateItemByDialog() {
        Integer[] ids = getCheckedIds();
        if (ids.length <= 0) return;

        TangoBook book = mBookDao.selectById(ids[0]);

        if (book == null) return;

        dialogMode = DialogMode.Update;
        DialogFragment dialogFragment = TBookDialogFragment.createInstance(book);
        dialogFragment.setTargetFragment(TangoBookFragment.this, 0);
        dialogFragment.show(getFragmentManager(), "fragment_dialog");
    }

    /*
     * TBookDialogActivityからコールバックされるメソッド
     */
    @Override
    public void onOkClicked(Bundle args) {
        if (args != null) {
            // ダイアログの戻り値を取得
            String name = args.getString(TBookDialogFragment.KEY_RET_NAME);
            int color = args.getInt(TBookDialogFragment.KEY_RET_COLOR);
            String comment = args.getString(TBookDialogFragment.KEY_RET_COMMENT);

            switch(dialogMode) {
                case Add:
                {
                    // カードを追加する
                    TangoBook book = new TangoBook();
                    book.setName(name);
                    book.setColor(color);
                    book.setComment(comment);
                    book.setCreateTime(new Date());
                    book.setUpdateTime(new Date());

                    mBookDao.addOne(book);
                    showList();
                }
                break;
                case Update:
                {
                    // チェックされた項目のカードを更新する
                    Integer[] ids = getCheckedIds();
                    if (ids.length <= 0) return;

                    TangoBook book = mBookDao.selectById(ids[0]);
                    book.setName(name);
                    book.setColor(color);
                    book.setComment(comment);
                    book.setUpdateTime(new Date());

                    updateCheckedItemOne(book);
                }
                break;
            }
        }
    }

    // Toast を表示する
    // x,y はデフォルトの表示位置(画面中央)からのオフセット
    private void makeToast(String message, int x, int y){
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, x, y);
        toast.show();
    }
}
