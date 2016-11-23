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
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class TangoItemPosFragment extends Fragment implements View.OnClickListener, TItemPosDialogFragment.OnOkClickListener{

    private final static String BACKGROUND_COLOR = "background_color";
    public static final int REQUEST_CODE = 1;

    public static final int[] buttonIds = {
            R.id.buttonHome,
            R.id.buttonTrash,
            R.id.buttonAddToHome,
            R.id.buttonAddToTrash,
            R.id.buttonDeleteFromHome,
            R.id.buttonDeleteFromTrash
    };


    // データベースモデル
    TangoItemPosDao mItemPosDao;

    private ListView listView;
    private Button[] buttons = new Button[buttonIds.length];
    // ダイアログを呼び出しモード
    // 返り値を受け取るときに呼び出しモードに応じた処理を行う
    private TItemPosDialogFragment.DialogMode dialogMode = TItemPosDialogFragment.DialogMode
            .AddToHome;

    /**
     * 新しいFragmentを生成する
     * @param color
     * @return
     */

    public static TangoItemPosFragment newInstance(int color) {
        TangoItemPosFragment frag = new TangoItemPosFragment();
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
        View view = inflater.inflate(R.layout.fragment_tango_item_pos, null);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.top_layout);
        linearLayout.setBackgroundColor(getArguments().getInt(BACKGROUND_COLOR));

        listView = (ListView) view.findViewById(R.id.listView);

        // OnClickListener登録
        for (int id : buttonIds) {
            (view.findViewById(id)).setOnClickListener(this);
        }

        // DAOの準備
        mItemPosDao = MyRealmManager.getItemPosDao();

        showHomeItems();
        return view;
    }

    /**
     * クリックイベント
     *
     * @param v
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonHome:
                showHomeItems();
                break;
            case R.id.buttonTrash:
                showTrashItems();
                break;
            case R.id.buttonAddToHome:
                addToHome();
                break;
            case R.id.buttonAddToTrash:
                addToTrash();
                break;
            case R.id.buttonDeleteFromHome:
                deleteFromHome();
                break;
            case R.id.buttonDeleteFromTrash:
                deleteFromTrash();
                break;
        }
    }

    /**
     * ListViewを最新のレコードで更新する
     */
    private void showHomeItems() {
        List<TangoItemPos> items = mItemPosDao.selectByParentType(TangoParentType.Home);
        items = mItemPosDao.toChangeableItemPos(items);
        TangoItemPosAdapter adapter = new TangoItemPosAdapter(getContext(), 0, items);
        listView.setAdapter(adapter);
    }

    private void showTrashItems() {
        List<TangoItemPos> items = mItemPosDao.selectByParentType(TangoParentType.Trash);
        items = mItemPosDao.toChangeableItemPos(items);
        TangoItemPosAdapter adapter = new TangoItemPosAdapter(getContext(), 0, items);
        listView.setAdapter(adapter);
    }

    private void launchItemPosDialog(TItemPosDialogFragment.DialogMode mode) {
        dialogMode = mode;

        DialogFragment dialogFragment = TItemPosDialogFragment.createInstance(dialogMode);
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getFragmentManager(), "fragment_dialog");

    }

    /**
     * ホームにアイテムを追加するためのダイアログを起動
     */
    private void addToHome() {
        launchItemPosDialog(TItemPosDialogFragment.DialogMode.AddToHome);
    }

    /**
     * ゴミ箱にアイテムを追加するためのダイアログを起動
     */
    private void addToTrash() {
        launchItemPosDialog(TItemPosDialogFragment.DialogMode.AddToTrash);
    }

    private void deleteFromHome() {
        launchItemPosDialog(TItemPosDialogFragment.DialogMode.DeleteFromHome);
    }

    private void deleteFromTrash() {
        launchItemPosDialog(TItemPosDialogFragment.DialogMode.DeleteFromTrash);
    }

    /**
     * 適当に順番を並び替える
     */
    private void updateItems() {
        List<TangoItemPos> items = mItemPosDao.selectAll();
        if (items != null && items.size() > 0) {
            Random rand = new Random();
            items = mItemPosDao.toChangeableItemPos(items);
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
        showHomeItems();
    }

    /**
     * チェックされた項目のIDを取得する
     */
    protected Integer[] getCheckedPoes() {
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

    /**
     * DialogFragmentからコールバックされるメソッド
     */
    @Override
    public void onOkClicked(Bundle args) {
        if (args != null) {
            int mode = args.getInt(TItemPosDialogFragment.KEY_RET, 0);
            switch(TItemPosDialogFragment.DialogMode.toEnum(mode)) {
                case AddToHome:
                case DeleteFromHome:
                    showHomeItems();
                    break;
                case AddToTrash:
                case DeleteFromTrash:
                    showTrashItems();
                    break;
            }
        }
    }
}
