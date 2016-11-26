package com.sunsunsoft.shutaro.tangobook;

import android.view.View;

/**
 * アイコンをクリックしたときに表示されるダイアログ
 */
abstract public class IconInfoDialog extends UDialogWindow{
    /**
     * Consts
     */

    /**
     * Member Variables
     */
    protected View mParentView;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */


    public IconInfoDialog(UButtonCallbacks buttonCallbacks, UDialogCallbacks dialogCallbacks,
                          ButtonDir dir,
                          boolean isAnimation,
                          float x, float y,
                          int screenW, int screenH,
                          int textColor, int dialogColor)
    {
        super(DialogType.Normal, buttonCallbacks, dialogCallbacks,
                dir, DialogPosType.Point, isAnimation,
                x, y, screenW, screenH, textColor, dialogColor);
    }


    /**
     * Methods
     */
    /**
     * Callbacks
     */
}
