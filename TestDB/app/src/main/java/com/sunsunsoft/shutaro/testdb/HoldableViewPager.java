package com.sunsunsoft.shutaro.testdb;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * スクロール切り替え機能付きViewPager
 */
public class HoldableViewPager extends ViewPager {
    boolean isSwipeHold_ = false;   // スワイプによるページ切り替えを抑制する

    /*
     * スワイプによるページ切り替え有効/無効設定
     */
    public void setSwipeHold(boolean enable) {
        Log.v("myLog", "setSwipeHold:" + enable);
        isSwipeHold_ = enable;
    }

    public HoldableViewPager(Context context) {
        super(context);
    }

    public HoldableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ( isSwipeHold_ ) return false;
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)  {
        if ( isSwipeHold_ ) return false;
        return super.onInterceptTouchEvent(event);
    }
}