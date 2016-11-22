package com.sunsunsoft.shutaro.testdb;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class TangoItemPosFragment extends Fragment implements View.OnClickListener{

    private final static String BACKGROUND_COLOR = "background_color";
    public static final int REQUEST_CODE = 1;

    public static final int[] buttonIds = {
            R.id.button,
            R.id.button2,
            R.id.button3,
            R.id.button4
    };

    // データベースモデル
    TangoItemPosDao mItemPosDao;

    private ListView listView;
    private Button[] buttons = new Button[buttonIds.length];

    /**
     * 新しいFragmentを生成する
     * @param IdRes
     * @return
     */

    public static TangoItemPosFragment newInstance(int IdRes) {
        TangoItemPosFragment frag = new TangoItemPosFragment();
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
        View view = inflater.inflate(R.layout.fragment_tango_item_pos, null);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.top_layout);
        int hoge = getArguments().getInt(BACKGROUND_COLOR);
//        linearLayout.setBackgroundResource(hoge);

        listView = (ListView) view.findViewById(R.id.listView);


        buttons[0] = (Button) view.findViewById(R.id.button);
        buttons[1] = (Button) view.findViewById(R.id.button2);
        buttons[2] = (Button) view.findViewById(R.id.button3);
        buttons[3] = (Button) view.findViewById(R.id.button4);

        for (Button button : buttons) {
            button.setOnClickListener(this);
        }

        // DAOの準備
        mItemPosDao = MyRealmManager.getItemPosDao();

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
                showList();
                break;
            case R.id.button2:
                addItems();
                break;
            case R.id.button3:
                updateItems();
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
        List<TangoItemPos> items = mItemPosDao.selectAll();
        items = mItemPosDao.toChangeable(items);
        TangoItemPosAdapter adapter = new TangoItemPosAdapter(getContext(), 0, items);
        listView.setAdapter(adapter);
    }

    /**
     * 適当に順番を並び替える
     */
    private void updateItems() {
        List<TangoItemPos> items = mItemPosDao.selectAll();
        if (items != null && items.size() > 0) {
            Random rand = new Random();
            items = mItemPosDao.toChangeable(items);
            int size = items.size();
            boolean[] setFlag = new boolean[items.size()];

            for (TangoItemPos item : items) {
                int newPos = rand.nextInt(size);
                for (int i=0; i<size; i++) {
                    int pos = (newPos + i) % size;
                    if (setFlag[pos] == false) {
                        setFlag[pos] = true;
                        item.setPos(pos);
                        break;
                    }
                }
            }
        }
        mItemPosDao.updateAll(items);
        showList();
    }

    /**
     * 全ての要素を追加する
     */
    private void addItems() {
        LinkedList<TangoItemPos> items = new LinkedList<>();

        int pos = 0;
        // Card
        List<TangoCard> cards = MyRealmManager.getCardDao().selectAll();
        for (TangoCard card : cards) {
            TangoItemPos item = new TangoItemPos();
            item.setParams(pos, TangoItemType.Card.ordinal(), card.getId());
            items.add(item);
            pos++;
        }

        // Book
        List<TangoBook> books = MyRealmManager.getBookDao().selectAll();
        for (TangoBook book : books) {
            TangoItemPos item = new TangoItemPos();
            item.setParams(pos, TangoItemType.Book.ordinal(), book.getId());
            items.add(item);
            pos++;
        }

        // Box
        List<TangoBox> boxes = MyRealmManager.getBoxDao().selectAll();
        for (TangoBox box : boxes) {
            TangoItemPos item = new TangoItemPos();
            item.setParams(pos, TangoItemType.Box.ordinal(), box.getId());
            items.add(item);
            pos++;
        }

        mItemPosDao.updateAll(items);
        showList();
    }

    /**
     * チェックされた項目を削除する
     */
    private void deleteItems() {
        Integer[] checkedPositions = getCheckedPositions();

        mItemPosDao.deletePositions(checkedPositions);
        showList();
    }

    /**
     * チェックされた項目のIDを取得する
     */
    protected Integer[] getCheckedPositions() {
        // チェックされた項目のIDを取得する
        LinkedList<Integer> idsList = new LinkedList<Integer>();
        TangoItemPosAdapter adapter = (TangoItemPosAdapter) listView.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            TangoItemPos item = adapter.getItem(i);
            if (item.isChecked()) {
                idsList.add(item.getPos());
            }
        }
        return idsList.toArray(new Integer[0]);
    }
}
