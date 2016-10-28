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

import java.util.List;

public class TangoCardFragment extends Fragment implements OnClickListener, OnItemClickListener {
    private final static String BACKGROUND_COLOR = "background_color";

    // データベースモデル
    TangoCardDao mCardDao;

    private ListView listView;
    private Button button1, button2, button3;
    private EditText nameEdit, idEdit;

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


        button1 = (Button)view.findViewById(R.id.button);
        button2 = (Button)view.findViewById(R.id.button2);
        button3 = (Button)view.findViewById(R.id.button3);
        nameEdit = (EditText)view.findViewById(R.id.editText);
        idEdit = (EditText)view.findViewById(R.id.editText2);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

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
                mCardDao.add1("hoge","ほげ");
                showList();
                break;
            case R.id.button3:
                break;
            case R.id.button4:
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


    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TangoCardAdapter adapter = (TangoCardAdapter)listView.getAdapter();
        TangoCard data = (TangoCard)adapter.getItem(position);
        data.setWordA("hogehogehoge");
        adapter.notifyDataSetChanged();
    }

    // Toast を表示する
    // x,y はデフォルトの表示位置(画面中央)からのオフセット
    private void makeToast(String message, int x, int y){
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, x, y);
        toast.show();
    }
}
