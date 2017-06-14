package com.sunsunsoft.shutaro.tangobook.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.sunsunsoft.shutaro.tangobook.help.HelpListAdapter;
import com.sunsunsoft.shutaro.tangobook.help.HelpListItemData;
import com.sunsunsoft.shutaro.tangobook.activity.MainActivity;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * ヘルプのトップページのFragment
 */

public class HelpFragment extends Fragment implements OnItemClickListener{

    /**
     * Consts
     */
    public static final String TAG = "HelpFragment";

    /**
     * Member variables
     */
    private ListView mListView;

    public HelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        mListView = (ListView)view.findViewById(R.id.listView);

        // Adapterに設定するCustomData配列を作成する
        List<HelpListItemData> list = new ArrayList<>();

        for (HelpPageId id : HelpPageId.values()) {
            HelpListItemData item = new HelpListItemData(id.getText(), id.isTitle(), id.getTextColor(),
                    id.getBgColor());
            list.add(item);
        }

        // カスタムしたAdapterを作成
        HelpListAdapter adapter = new HelpListAdapter(getContext(), 0, list);

        // Apadpterを設定
        ListView listView = (ListView)view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        // 戻るボタンを表示
        MainActivity.getInstance().showActionBarBack(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.getInstance().setActionBarTitle(UResourceManager.getStringById(R.string
                .title_help));
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView lv = (ListView)parent;
        HelpListItemData data = (HelpListItemData)lv.getItemAtPosition(position);

        HelpPageId _id = HelpPageId.toEnum((int)id);
        switch(_id) {
            case Info1:
            case Info2:
            case Edit0:
            case Edit1:
            case Edit2:
            case Edit3:
            case Edit4:
            case Edit5:
            case Edit6:
            case Edit7:
            case Edit8:
            case Study1:
            case Study2:
            case Study3:
            case Study4:
            case Study5:
            case Backup1:
            case Backup2:
            case Backup3:
                MainActivity.getInstance().showHelpPage(_id);

                break;
        }
    }


    /**
     * 戻るボタンが押された時の処理
     */
    public boolean onBackKeyDown() {
        getFragmentManager().popBackStack();
        if (getFragmentManager().getBackStackEntryCount() <= 1) {
            MainActivity.getInstance().showActionBarBack(false);
        }
        return true;
    }
}
