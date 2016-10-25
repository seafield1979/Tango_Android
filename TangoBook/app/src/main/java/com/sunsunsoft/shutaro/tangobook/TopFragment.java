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
    private final static String BACKGROUND_COLOR = "background_color";
    private TopView myView;
    private Button button1;

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
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.fragment_page_linearlayout);
        linearLayout.setBackgroundResource(getArguments().getInt(BACKGROUND_COLOR));

        myView = (TopView)view.findViewById(R.id.IconsView);
        Log.v("topfragment", view.getWidth() + " " + myView.getHeight());

        button1 = (Button)view.findViewById(R.id.button);
        button1.setOnClickListener(this);

        return view;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            myView.setLayoutParams(new LinearLayout.LayoutParams(1000, 2000));
        }
    }
    public boolean onTouch(View v, MotionEvent e) {
        return true;
    }
}
