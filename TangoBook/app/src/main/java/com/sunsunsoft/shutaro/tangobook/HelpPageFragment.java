package com.sunsunsoft.shutaro.tangobook;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * ヘルプページのFragment
 * ページレイアウトのxmlを表示するだけ
 */
public class HelpPageFragment extends Fragment {

    /**
     * Member variables
     */
    private HelpPageId mHelpItem;

    public HelpPageFragment() {}

    public static HelpPageFragment createInstance(HelpPageId helpItem) {
        HelpPageFragment instance = new HelpPageFragment();

        Bundle args = new Bundle();
        args.putInt("helpItem", helpItem.ordinal());
        instance.setArguments(args);
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 引数を受け取る
        mHelpItem = HelpPageId.toEnum(getArguments().getInt("helpItem"));

        int layoutId = 0;

        switch(mHelpItem) {
            case Info1:
                layoutId = R.layout.help_page_info1;
                break;
            case Info2:
                layoutId = R.layout.help_page_info2;
                break;
            default:
                layoutId = R.layout.fragment_help_page;
        }

        return inflater.inflate(layoutId, container, false);
    }



    /**
     * 戻るボタンが押された時の処理
     */
    public boolean onBackKeyDown() {
        getFragmentManager().popBackStack();
        return true;
    }
}
