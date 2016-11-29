package com.sunsunsoft.shutaro.tangobook;

import android.view.View;

/**
 * アイコンをクリックしたときに表示されるダイアログ
 */
abstract public class IconInfoDialog extends UWindow{
    /**
     * Consts
     */

    /**
     * Member Variables
     */
    protected View mParentView;
    protected UButtonCallbacks mButtonCallbacks;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */


    public IconInfoDialog(View parentView,
                        UButtonCallbacks buttonCallbacks, UWindowCallbacks windowCallbacks,
                          float x, float y,
                          int color)
    {
        // width, height はinit内で計算するのでここでは0を設定
        super(windowCallbacks, DrawPriority.Dialog.p(), x, y, 0, 0, color);

        mParentView = parentView;
        mButtonCallbacks = buttonCallbacks;

    }


    /**
     * Methods
     */
    /**
     * Callbacks
     */
}
