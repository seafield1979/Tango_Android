package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by shutaro on 2016/12/05.
 *
 * UViewPageManager配下のページの基底クラス
 */

abstract public class UPageView {
    /**
     * Enums
     */
    /**
     * Consts
     */

    /**
     * Member Variables
     */
    protected Context mContext;
    protected View mParentView;

    protected int drawPageId;
    private boolean isFirst = true;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public UPageView(Context context, View parentView, int drawPageId) {
        mContext = context;
        mParentView = parentView;
        this.drawPageId = drawPageId;
    }

    /**
     * Methods
     */
    /**
     * スタックの先頭になって表示され始める前に呼ばれる
     */
    protected void onShow() {
        // ページを切り替える
        UDrawManager.getInstance().setCurrentPage(drawPageId);
    }

    /**
     * スタックの先頭でなくなって表示されなくなる前に呼ばれる
     */
    protected void onHide() {

    }

    protected boolean draw(Canvas canvas, Paint paint) {
        if (isFirst) {
            isFirst = false;
            initDrawables();
        }
        return false;
    }

    abstract boolean touchEvent(ViewTouch vt);

    abstract void initDrawables();

    abstract boolean onBackKeyDown();

    /**
     * Callbacks
     */
}
