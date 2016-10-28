package com.sunsunsoft.shutaro.testdb;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
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

import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

public class TangoCardFragment extends Fragment implements OnClickListener {
    private final static String BACKGROUND_COLOR = "background_color";

    // データベースモデル
    TangoCardDao mCardDao;

    private ListView listView;
    private Button[] buttons = new Button[4];
    private EditText wordAEdit, wordBEdit;

    public static TangoCardFragment newInstance(@ColorRes int IdRes) {
        TangoCardFragment frag = new TangoCardFragment();
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

        wordAEdit = (EditText)view.findViewById(R.id.editText);
        wordBEdit = (EditText)view.findViewById(R.id.editText2);

        for (Button button : buttons) {
            button.setOnClickListener(this);
        }

        // DAOの準備
        mCardDao = new TangoCardDao(getActivity());

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
                addItem();
                break;
            case R.id.button3:
                updateItem();
                break;
            case R.id.button4:
                deleteItem();
                break;
        }
    }

    /**
     * ListViewを最新のレコードで更新する
     */
    private void showList() {
        List<TangoCard> cards = mCardDao.selectAll();
        TangoCardAdapter adapter = new TangoCardAdapter(getContext(), 0, cards);
        listView.setAdapter(adapter);
    }

    /**
     * 項目を追加する
     */
    private void addItem() {
        String wordA = wordAEdit.getText().toString();
        String wordB = wordBEdit.getText().toString();

        mCardDao.add1(wordA, wordB);
        showList();
    }

    /**
     * チェックされた項目を更新する
     */
    private void updateItem() {
        String wordA = wordAEdit.getText().toString();
        String wordB = wordBEdit.getText().toString();

        // チェックされた項目のIDを取得する
        Integer[] checkedIds = getCheckedIds();

        mCardDao.updateIds(checkedIds, wordA, wordB);
        showList();
    }

    /**
     * チェックされた項目を削除する
     */
    private void deleteItem() {
        Integer[] checkedIds = getCheckedIds();

        mCardDao.deleteIds(checkedIds);
        showList();
    }

    /**
     * チェックされた項目のIDを取得する
     */
    protected Integer[] getCheckedIds() {
        // チェックされた項目のIDを取得する
        LinkedList<Integer> idsList = new LinkedList<Integer>();
        TangoCardAdapter adapter = (TangoCardAdapter) listView.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            TangoCard card = adapter.getItem(i);
            if (card.getIsChecked()) {
                idsList.add(card.getId());
            }
        }
        return idsList.toArray(new Integer[0]);
    }

//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        TangoCardAdapter adapter = (TangoCardAdapter)listView.getAdapter();
//        TangoCard data = (TangoCard)adapter.getItem(position);
//        //data.setWordA("hogehogehoge");
//        adapter.notifyDataSetChanged();
//    }

    // Toast を表示する
    // x,y はデフォルトの表示位置(画面中央)からのオフセット
    private void makeToast(String message, int x, int y){
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, x, y);
        toast.show();
    }
}
