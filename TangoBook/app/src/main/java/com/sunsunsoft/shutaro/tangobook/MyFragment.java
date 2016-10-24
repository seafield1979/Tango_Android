package com.sunsunsoft.shutaro.tangobook;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View.OnTouchListener;

public class MyFragment extends Fragment implements OnTouchListener {
    private final static String BACKGROUND_COLOR = "background_color";
    private ImageView[] imageViews = new ImageView[3];

    public static MyFragment newInstance(@ColorRes int IdRes) {
        MyFragment frag = new MyFragment();
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

        imageViews[0] = (ImageView)view.findViewById(R.id.imageView);
        imageViews[1] = (ImageView)view.findViewById(R.id.imageView2);
        imageViews[2] = (ImageView)view.findViewById(R.id.imageView3);

        for (int i=0; i<imageViews.length; i++) {
            imageViews[i].setOnTouchListener(this);
        }

        return view;
    }


    /**
     * タッチイベント
     * @param v
     * @param e
     * @return
     */
    public boolean onTouch(View v, MotionEvent e) {

        // ImageViewをタッチしている間はViewPagerがスクロールしないようにする
        if (v.getId() == R.id.imageView ||
                v.getId() == R.id.imageView2 ||
                v.getId() == R.id.imageView3 )
        {
            String action = "";
            HoldableViewPager viewPager = (HoldableViewPager) getActivity().findViewById(R.id.main_viewpager);
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    action = "ACTION_DOWN";
                    // ViewPagerのスクロール停止
                    viewPager.setSwipeHold(true);
                    break;
                case MotionEvent.ACTION_UP:
                    action = "ACTION_UP";
                    // ViewPagerのスクロール再開
                    viewPager.setSwipeHold(false);
                    break;
                case MotionEvent.ACTION_MOVE:
                    action = "ACTION_MOVE";
                    break;
                case MotionEvent.ACTION_CANCEL:
                    action = "ACTION_CANCEL";
                    break;
                default:
            }
            Log.v("mylog", "action:" + action);
        }
        return true;
    }

}
