package com.sunsunsoft.shutaro.tangobook;


import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;


public class TopFragment extends Fragment implements OnClickListener, OnTouchListener{

    /**
     * Constract
     */
    private final static String BACKGROUND_COLOR = "background_color";

    /**
     * Member variables
     */
    private TopView topView;

    public static TopFragment newInstance(@ColorRes int IdRes) {
        TopFragment frag = new TopFragment();
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
        View view = inflater.inflate(R.layout.fragment_top, null);

        // Viewを追加
        topView = new TopView(getContext());
        LinearLayout containerView = (LinearLayout)view.findViewById(R.id.view_container);
        containerView.addView(topView);

        return view;
    }

    public void onClick(View v) {

    }
    public boolean onTouch(View v, MotionEvent e) {
        return true;
    }
}
