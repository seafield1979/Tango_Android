package com.sunsunsoft.shutaro.tangobook;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * ヘルプのトップページのFragment
 */
/**
 * Enum
 */
enum HelpPageId {
    TitleInfo(R.string.help_title_basic, true, -1, Color.WHITE, Color.BLUE),
    Info1(R.string.help_title_basic1, false, R.layout.help_page_info1, Color.BLACK, Color.WHITE),
    Info2(R.string.help_title_basic2, false, R.layout.help_page_info2, Color.BLACK, Color.WHITE),

    TitleEdit(R.string.help_title_edit, true, -1, Color.WHITE, Color.BLUE),
    Edit0(R.string.help_title_edit0, false, R.layout.help_edit0, Color.BLACK, Color.WHITE),
    Edit1(R.string.help_title_edit1, false, R.layout.help_edit1, Color.BLACK, Color.WHITE),
    Edit2(R.string.help_title_edit2, false, R.layout.help_edit2, Color.BLACK, Color.WHITE),
    Edit3(R.string.help_title_edit3, false, R.layout.help_edit3, Color.BLACK, Color.WHITE),
    Edit4(R.string.help_title_edit4, false, R.layout.help_edit4, Color.BLACK, Color.WHITE),
    Edit5(R.string.help_title_edit5, false, R.layout.help_edit5, Color.BLACK, Color.WHITE),
    Edit6(R.string.help_title_edit6, false, R.layout.help_edit6, Color.BLACK, Color.WHITE),
    Edit7(R.string.help_title_edit7, false, R.layout.help_edit7, Color.BLACK, Color.WHITE),
    Edit8(R.string.help_title_edit8, false, R.layout.help_edit8, Color.BLACK, Color.WHITE),
    TitleStudy(R.string.help_title_study, true, -1, Color.WHITE, Color.BLUE),
    Study1(R.string.help_title_study1, false, R.layout.help_study1, Color.BLACK, Color.WHITE),
    Study2(R.string.help_title_study2, false, R.layout.help_study2, Color.BLACK, Color.WHITE),
    Study3(R.string.help_title_study3, false, R.layout.help_study3, Color.BLACK, Color.WHITE),
    Study4(R.string.help_title_study4, false, R.layout.help_study4, Color.BLACK, Color.WHITE),
    Study5(R.string.help_title_study5, false, R.layout.help_study5, Color.BLACK, Color.WHITE),
    TitleBackup(R.string.help_title_backup, true, -1, Color.WHITE, Color.BLUE),
    Backup1(R.string.help_title_backup1, false, R.layout.help_backup1, Color.BLACK, Color
            .WHITE),
    Backup2(R.string.help_title_backup2, false, R.layout.help_backup2, Color.BLACK, Color.WHITE),
    Backup3(R.string.help_title_backup3, false, R.layout.help_backup3, Color.BLACK, Color.WHITE),
    ;

    private String text;
    private boolean isTitle;
    private int layoutId;
    private int textColor;
    private int bgColor;

    HelpPageId(int textId, boolean isTitle, int layoutId, int textColor, int bgColor) {
        this.text = UResourceManager.getStringById(textId);
        this.isTitle = isTitle;
        this.layoutId = layoutId;
        this.textColor = textColor;
        this.bgColor = bgColor;
    }
    public String getText() { return text; }
    public boolean isTitle() { return isTitle; }
    public int getLayoutId() { return layoutId; }
    public int getTextColor() { return textColor; }
    public int getBgColor() { return bgColor; }

    // int を enumに変換する
    public static HelpPageId toEnum(int val) {
        if (val >= values().length) return TitleInfo;
        return values()[val];
    }
}

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
