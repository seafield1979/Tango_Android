package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Color;

/**
 * Created by shutaro on 2016/12/04.
 *
 * OKかキャンセルを押すまで消えないポップアップWindow
 */

enum UPopupType {
    OK,
    OKCancel
}

public class UPopupWindow extends UDialogWindow {

    /**
     * Constants
     */
    public static final int OKButtonId = 10005000;

    /**
     * Member variables
     */

    /**
     * Constructor
     */
    public UPopupWindow(UPopupType popupType,
                        String title,
                        boolean isAnimation,
                        int screenW, int screenH)
    {
        super(DialogType.Mordal, null, null,
                ButtonDir.Horizontal, DialogPosType.Center,
                isAnimation, 0, 0, screenW, screenH,
                Color.BLACK, Color.WHITE);

        this.buttonCallbacks = this;
        this.frameColor = Color.BLACK;
        this.title = title;

        if (popupType == UPopupType.OK) {
            addButton(OKButtonId, "OK", Color.BLACK, Color.LTGRAY);
        } else {
            addButton(OKButtonId, "OK", Color.BLACK, Color.LTGRAY);
            addCloseButton(UResourceManager.getStringById(R.string.cancel));
        }
    }

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        if (super.UButtonClicked(id, pressedOn)) {
            return true;
        }

        switch(id) {
            case OKButtonId:
                startClosing();
                return true;
        }
        return false;
    }
}
