package com.sunsunsoft.shutaro.tangobook.icon;

import com.sunsunsoft.shutaro.tangobook.database.TangoParentType;

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

    /**
     * Get/Set
     */
    public UIconWindow getSubWindow() {
        return subWindow;
    }

    // 自分が親になるとき(内包するアイコンがあるとき）の自分のParentTypeを返す
    abstract public TangoParentType getParentType();

    /**
     * Constructor
     */
    public IconContainer(UIconWindow parentWindow, UIconCallbacks iconCallbacks,
                         IconType type, float x,
                         float y, int width, int height)
    {
        super(parentWindow, iconCallbacks, type, x, y, width, height);
    }
}
