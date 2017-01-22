package com.sunsunsoft.shutaro.tangobook;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    TitleInfo("title1", true, Color.WHITE, Color.BLUE),
    Info1("1", false, Color.BLACK, Color.WHITE),
    Info2("2", false, Color.BLACK, Color.WHITE),
    Info3("3", false, Color.BLACK, Color.WHITE),
    TitleEdit("title2", true, Color.WHITE, Color.BLUE),
    Edit1("1", false, Color.BLACK, Color.WHITE),
    Edit2("2", false, Color.BLACK, Color.WHITE),
    Edit3("3", false, Color.BLACK, Color.WHITE),
    TitleStudy("title3", true, Color.WHITE, Color.BLUE),
    Study1("1", false, Color.BLACK, Color.WHITE),
    Study2("2", false, Color.BLACK, Color.WHITE),
    Study3("3", false, Color.BLACK, Color.WHITE),
    TitleBackup("title4", true, Color.WHITE, Color.BLUE),
    Backup1("1", false, Color.BLACK, Color.WHITE),
    Backup2("2", false, Color.BLACK, Color.WHITE),
    ;

    private String text;
    private boolean isTitle;
    private int textColor;
    private int bgColor;

    private HelpPageId(String text, boolean isTitle, int textColor, int bgColor) {
        this.text = text;
        this.isTitle = isTitle;
        this.textColor = textColor;
        this.bgColor = bgColor;
    }
    public String getText() { return text; }
    public boolean isTitle() { return isTitle; }
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
        List<HelpListItemData> objects = new ArrayList<>();

        for (HelpPageId id : HelpPageId.values()) {
            HelpListItemData item = new HelpListItemData(id.getText(), id.isTitle(), id.getTextColor(),
                    id.getBgColor());
            objects.add(item);
        }

        // カスタムしたAdapterを作成
        HelpListAdapter adapter = new HelpListAdapter(getContext(), 0, objects);

        // Apadpterを設定
        ListView listView = (ListView)view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        return view;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView lv = (ListView)parent;
        HelpListItemData data = (HelpListItemData)lv.getItemAtPosition(position);

        HelpPageId _id = HelpPageId.toEnum((int)id);
        switch(_id) {
            case Info1:
            case Info2:
            case Info3:
            case Edit1:
            case Edit2:
            case Edit3:
            case Study1:
            case Study2:
            case Study3:
            case Backup1:
            case Backup2:
                MainActivity.getInstance().showHelpPage(_id);

                break;
        }
    }

}
