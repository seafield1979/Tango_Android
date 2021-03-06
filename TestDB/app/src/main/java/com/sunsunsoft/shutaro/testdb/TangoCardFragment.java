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
import android.view.View.OnClickListener;

import java.util.LinkedList;
import java.util.List;

/**
 * 単語帳(TangoBook)のテスト用Fragment
 */
public class TangoCardFragment extends Fragment implements OnClickListener, TCardDialogFragment.OnOkClickListener {
    // Enums
    enum DialogMode {
        Add,
        Update
    }

    private final static String BACKGROUND_COLOR = "background_color";
    public static final int REQUEST_CODE = 1;

    // データベースモデル
    TangoCardDao mCardDao;

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
    public static TangoCardFragment newInstance(int IdRes) {
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

        for (Button button : buttons) {
            button.setOnClickListener(this);
        }

        // DAOの準備
        mCardDao = RealmManager.getCardDao();

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
//                showList();
            {
                RealmManager.getCardDao().selectAll();
            }
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
        List<TangoCard> cards = mCardDao.selectAll();
        cards = mCardDao.toChangeable(cards);
        TangoCardAdapter adapter = new TangoCardAdapter(getContext(), 0, cards);
        listView.setAdapter(adapter);
    }

    /**
     * チェックされた項目を１つ更新する
     * @param card
     */
    private void updateCheckedItemOne(TangoCard card) {
        // チェックされた項目のIDを取得する
        Integer[] checkedIds = getCheckedIds();

        if (checkedIds.length <= 0) return;

        mCardDao.updateOne(card);
        showList();
    }

    /**
     * チェックされた項目を削除する
     */
    private void deleteItems() {
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
            if (card.isChecked()) {
                idsList.add(card.getId());
            }
        }
        return idsList.toArray(new Integer[0]);
    }

    /**
     * 単語カードを追加するためのダイアログを立ち上げる
     */
    protected void addItemByDialog() {
        dialogMode = DialogMode.Add;
        DialogFragment dialogFragment = TCardDialogFragment.createInstance(null);
        dialogFragment.setTargetFragment(TangoCardFragment.this, 0);
        dialogFragment.show(getFragmentManager(), "fragment_dialog");
    }

    /**
     * 単語カードを更新するためのダイアログを立ち上げる
     * Fragmentの戻り値でカードを更新
     */
    protected void updateItemByDialog() {
        Integer[] ids = getCheckedIds();
        if (ids.length <= 0) return;

        dialogMode = DialogMode.Update;
        TangoCard card = mCardDao.selectById(ids[0]);

        if (card == null) return;

        DialogFragment dialogFragment = TCardDialogFragment.createInstance(card);
        dialogFragment.setTargetFragment(TangoCardFragment.this, 0);
        dialogFragment.show(getFragmentManager(), "fragment_dialog");
    }

    /*
     * TCardDialogActivityからコールバックされるメソッド
     */
    @Override
    public void onOkClicked(Bundle args) {
        if (args != null) {
            // ダイアログの戻り値を取得
            String wordA = args.getString(TCardDialogFragment.KEY_RET_WORD_A);
            String wordB = args.getString(TCardDialogFragment.KEY_RET_WORD_B);
            String hintAB = args.getString(TCardDialogFragment.KEY_RET_HINT_AB);
            String hintBA = args.getString(TCardDialogFragment.KEY_RET_HINT_BA);
            String comment = args.getString(TCardDialogFragment.KEY_RET_COMMENT);

            switch(dialogMode) {
                case Add:
                {
                    // カードを追加する
                    TangoCard card = new TangoCard();
                    card.setWordA(wordA);
                    card.setWordB(wordB);
                    card.setHintAB(hintAB);
                    card.setHintBA(hintBA);
                    card.setComment(comment);

                    mCardDao.addOne(card);
                    showList();
                }
                    break;
                case Update:
                {
                    // チェックされた項目のカードを更新する
                    Integer[] ids = getCheckedIds();
                    if (ids.length <= 0) return;

                    TangoCard card = mCardDao.selectById(ids[0]);
                    card.setWordA(wordA);
                    card.setWordB(wordB);
                    card.setHintAB(hintAB);
                    card.setHintBA(hintBA);
                    card.setComment(comment);

                    updateCheckedItemOne(card);
                }
                    break;
            }
        }
    }
}
