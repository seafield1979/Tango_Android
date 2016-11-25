package com.sunsunsoft.shutaro.tangobook;

import android.view.View;

import java.util.List;

/**
 * 他のアイコンを内包できるアイコン
 */

abstract public class IconContainer extends UIcon {

    /**
     * Constants
     */

    /**
     * Memver variable
     */
    protected UIconWindow subWindow;
    protected View mParentView;
    protected UIconManager mIconManager;

    /**
     * Get/Set
     */
    public List<UIcon> getIcons() {
        return mIconManager.getIcons();
    }
    public UIconWindow getSubWindow() {
        return subWindow;
    }

    // 自分が親になるときのParentTypeを返す
    abstract public TangoParentType getParentType();

    /**
     * Constructor
     */
    public IconContainer(UIconWindow parentWindow, UIconCallbacks iconCallbacks, IconType type, float x,
                 float y, int width, int height)
    {
        super(parentWindow, iconCallbacks, type, x, y, width, height);

    }
}
