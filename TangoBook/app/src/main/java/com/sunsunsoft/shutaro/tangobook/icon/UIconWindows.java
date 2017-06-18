package com.sunsunsoft.shutaro.tangobook.icon;

import com.sunsunsoft.shutaro.tangobook.util.ULog;
import com.sunsunsoft.shutaro.tangobook.uview.window.UWindow;
import com.sunsunsoft.shutaro.tangobook.uview.window.UWindowCallbacks;
import com.sunsunsoft.shutaro.tangobook.util.Size;

import java.util.LinkedList;
import java.util.List;

/**
 * 複数のUIconWindowを管理する
 * Window間でアイコンのやり取りを行ったりするのに使用する
 * 想定はメインWindowが１つにサブWindowが１つ
 */

public class UIconWindows implements UWindowCallbacks {
    enum DirectionType {
        Landscape,      // 横長
        Portlait,       // 縦長
    }
    /**
     * Consts
     */
    public static final String TAG = "UIconWindows";
    public static final int MOVING_FRAME = 12;

    /**
     * Member Variables
     */
    private LinkedList<UIconWindow> windows = new LinkedList<>();
    private UIconWindow mainWindow;
    private UIconWindowSub subWindow;
    private Size size;
    private DirectionType directionType;

    public static UIconWindows publicInstance;

    /**
     * Get/Set
     */
    public UIconWindow getMainWindow() {
        return mainWindow;
    }

    public UIconWindowSub getSubWindow() {
        return subWindow;
    }

    public LinkedList<UIconWindow> getWindows() {
        return windows;
    }

    // デバッグ用のどこからでも参照できるインスタンス
    public static UIconWindows getInstance() {
        return publicInstance;
    }

    /**
     * Constructor
     * インスタンスの生成はcreateInstanceを使用すること
     */
    private UIconWindows() {
    }

    public static UIconWindows createInstance(UIconWindow mainWindow, UIconWindowSub subWindow,
                                              int screenW, int screenH) {
        UIconWindows instance = new UIconWindows();
        instance.size = new Size(screenW, screenH);
        instance.directionType = (screenW > screenH) ? DirectionType.Landscape : DirectionType
                .Portlait;
        instance.mainWindow = mainWindow;
        instance.subWindow = subWindow;

        instance.windows.add(mainWindow);
        instance.windows.add(subWindow);

        // 初期配置(HomeWindowで画面が占有されている)
        mainWindow.setPos(0, 0);
        mainWindow.setSize(screenW, screenH);
        if (instance.directionType == DirectionType.Landscape) {
            subWindow.setPos(screenW, 0);
        } else {
            subWindow.setPos(0, screenH);
        }

        publicInstance = instance;

        return instance;
    }

    /**
     * Methods
     */

    /**
     * Windowを表示する
     * @param window
     * @param animation
     */
    public void showWindow(UIconWindow window, boolean animation) {
        window.setShow(true);
        window.setAppearance(true);

        updateLayout(animation);
    }

    /**
     * 指定のウィンドウを非表示にする
     * @param window
     */
    public boolean hideWindow(UIconWindow window, boolean animation) {
        // すでに非表示なら何もしない
        if (!window.getIsShow() || !window.isAppearance()) return false;

        window.setAppearance(false);
        updateLayout(animation);
        return true;
    }

    /**
     * レイアウトを更新する
     * ウィンドウを追加、削除した場合に呼び出す
     */
    private void updateLayout(boolean animation) {
        LinkedList<UIconWindow> showWindows = new LinkedList<>();
        for (UIconWindow _window : windows) {
            if (_window.isAppearance()) {
                showWindows.add(_window);
            }
        }
        if (showWindows.size() == 0) return;

        // 各ウィンドウが同じサイズになるように並べる
        int width;
        int height;
        if (directionType == DirectionType.Landscape) {
            width = size.width / showWindows.size();
            height = size.height;
        } else {
            width = size.width;
            height = size.height / showWindows.size();
        }

        // 座標を設定する
        if (animation) {
            // Main
            mainWindow.setPos(0, 0);
            mainWindow.startMovingSize(width, height, MOVING_FRAME);

            // Sub
            if (subWindow.isAppearance()) {
                // appear
                if (directionType == DirectionType.Landscape) {
                    subWindow.setPos(size.width, 0);
                    subWindow.startMoving(width, 0, width, height, MOVING_FRAME);
                } else {
                    subWindow.setPos(0, size.height);
                    subWindow.startMoving(0, height, width, height, MOVING_FRAME);
                }

            } else {
                // disappear
                if (directionType == DirectionType.Landscape) {
                    subWindow.startMoving(size.width, 0, 0, height, MOVING_FRAME);
                } else {
                    subWindow.startMoving(0, size.height, width, 0, MOVING_FRAME);
                }
            }

        } else {
            float x = 0, y = 0;
            for (UIconWindow _window : showWindows) {
                _window.setPos(x, y);
                _window.setSize(width, height);
                if (directionType == DirectionType.Landscape) {
                    x += width;
                } else {
                    y += height;
                }
            }
        }
    }

    /**
     * 全てのウィンドウのカードの表示を更新する
     */
    public void resetCardTitle() {
        for (UIconWindow window : windows) {
            List<UIcon> icons = window.getIcons();

            if (icons == null) continue;
            for (UIcon icon : icons) {
                icon.updateTitle();
            }
        }
    }

    /**
     * 全てのアイコンのドロップ状態をクリアする
     */
    public void clearDroped() {
        for (UIconWindow window : windows) {
            List<UIcon> icons = window.getIcons();

            if (icons == null) continue;
            for (UIcon icon : icons) {
                icon.isDroped = false;
            }
        }
    }

    /**
     * 全てのアイコンの情報を表示する for Debug
     */
    public void showAllIconsInfo() {
        for (UIconWindow window : windows) {
            List<UIcon> icons = window.getIcons();
            int pos = 1;
            if (icons == null) continue;
            for (UIcon icon : icons) {
                ULog.print(TAG, "pos:" + pos +
                        " iconType:" + icon.getType() +
                        " iconId:" + icon.getTangoItem().getId() +
                        " itemPos:" + icon.getTangoItem().getPos() +
                " mTitle:" + icon.getTitle());
                pos++;
            }
        }
    }


    /**
     * UWindowCallbacks
     */
    public void windowClose(UWindow window) {
        // Windowを閉じる
        for (UIconWindow _window : windows) {
            if (window == _window) {
                hideWindow(_window, true);
                break;
            }
        }
    }
}
