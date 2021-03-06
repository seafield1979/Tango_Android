package com.sunsunsoft.shutaro.tangobook.icon;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.sunsunsoft.shutaro.tangobook.util.UDpi;
import com.sunsunsoft.shutaro.tangobook.util.ULog;
import com.sunsunsoft.shutaro.tangobook.uview.menu.UMenuBar;
import com.sunsunsoft.shutaro.tangobook.util.URect;
import com.sunsunsoft.shutaro.tangobook.uview.window.UWindow;
import com.sunsunsoft.shutaro.tangobook.uview.window.UWindowCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;
import com.sunsunsoft.shutaro.tangobook.activity.MainActivity;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.database.TangoItem;
import com.sunsunsoft.shutaro.tangobook.database.TangoItemPos;
import com.sunsunsoft.shutaro.tangobook.database.TangoParentType;
import com.sunsunsoft.shutaro.tangobook.util.UDebug;
import com.sunsunsoft.shutaro.tangobook.uview.DoActionRet;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.DrawPriority;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Windows for Icons
 * Window can have many icons
 */

public class UIconWindow extends UWindow {
    /**
     * Window state
     * Window behavior is changed by state.
     */
    enum WindowState {
        none,
        drag,               // single icon draging
        icon_moving,        // icons moving (icons sort animation)
        icon_selecting      // icons can be selected
    }

    /**
     * Type of icon window
     * Home is a window that shows desktop icons
     * Sub is a window that shows icons which in a box
     */
    enum WindowType {
        Home,
        Sub
    }

    /**
     * Window directions
     * If screen width is longer than height, it is Horizontal
     * If screen height is longer than width, it is Vertical
     */
    public enum WindowDir {
        Horizontal,
        Vertical
    }

    /**
     * Icon moving type
     */
    public enum IconMovingType {
        Exchange,       // exchange A and B
        Insert,         // insert A before B
        MoveIn          // move A into B
    }


    /**
     * Consts
     */
    public static final String TAG = "UIconWindow";

    public static final int DRAW_PRIORITY = 100;
    public static final int DRAG_ICON_PRIORITY = 11;

    public static final int ICON_MARGIN = 10;

    public static final int ICON_W = 57;
    public static final int ICON_H = 50;
    protected static final int MARGIN_D = UMenuBar.MENU_BAR_H;

    protected static final int MOVING_TIME = 10;
    protected static final int SELECTED_ICON_BG_COLOR = Color.argb(80, 255, 100, 100);


    /**
     * Member veriables
     */
    protected WindowType type;
    public UIconManager mIconManager;
    protected PointF basePos;
    protected WindowDir dir;

    // 他のIconWindow
    // ドラッグで他のWindowにアイコンを移動するのに使用する
    protected UIconWindows windows;

    // Windowの親のタイプ
    protected TangoParentType parentType;
    protected int parentId;

    // ドラッグ中のアイコン
    protected UIcon dragedIcon;

    protected WindowState state = WindowState.none;
    protected WindowState nextState = WindowState.none;

    protected boolean isDragMove;
    protected boolean isDropInBox;
    protected boolean isAnimating;
    protected boolean isAppearance = true;       // true:出現中 / false:退出中

    // DPI補正の計算結果を保持するヘンス
    protected int iconMargin;               // アイコン間のマージン
    protected int iconW, iconH;             // アイコンサイズ

    /**
     * Get/Set
     */
    public WindowType getType() {
        return type;
    }
    public void setType(WindowType type) {
        this.type = type;
    }

    public PointF getBasePos() {
        return basePos;
    }

    public UIconManager getIconManager() {
        return mIconManager;
    }
    public void setIconManager(UIconManager mIconManager) {
        this.mIconManager = mIconManager;
    }

    public List<UIcon> getIcons() {
        if (mIconManager == null) return null;
        return mIconManager.getIcons();
    }

    public void setWindows(UIconWindows windows) {
        this.windows = windows;
    }

    public UIconWindows getWindows() { return this.windows; }

    public void setAnimating(boolean animating) {
        isAnimating = animating;
    }

    public void setDragedIcon(UIcon dragedIcon) {
        if (dragedIcon == null) {
            if (this.dragedIcon != null) {
                UDrawManager.getInstance().removeDrawable(this.dragedIcon);
            }
        }
        else {
            UDrawManager.getInstance().addWithNewPriority(dragedIcon, DrawPriority.DragIcon.p());
        }
        this.dragedIcon = dragedIcon;
    }

    public void setPos(float x, float y) {
        super.setPos(x,y);
        ULog.print(TAG, "x:" + x + " y:" + y);
    }

    public boolean isAppearance() {
        return isAppearance;
    }

    public void setAppearance(boolean appearance) {
        isAppearance = appearance;
    }

    public TangoParentType getParentType() {
        return parentType;
    }
    public int getParentId() {
        return parentId;
    }

    /**
     * 状態を設定する
     * 状態に移る時の前処理、後処理を実行できる
     * @param state
     */
    public void setState(WindowState state) {
        if (this.state == state) return;

        // 状態変化時の処理
        // 後処理
        switch (this.state) {
            case icon_moving:
            {
                // ドラッグアイコンの描画オブジェクトをクリア
                UDrawManager.getInstance().removeWithPriority(DRAG_ICON_PRIORITY);
            }
            break;
            case icon_selecting:
            {
                isDragMove = false;
                // アイコンのチェック状態をクリア
                if (state == WindowState.none) {
                    List<UIcon> icons = getIcons();
                    for (UIcon icon : icons) {
                        icon.isChecking = false;
                        if (icon.isChecked) {
                            icon.isChecked = false;
                        }
                    }
                    UDrawManager.getInstance().removeWithPriority(DRAG_ICON_PRIORITY);
                }
            }
            break;
        }

        // 前処理
        switch(state){
            case none:
                // アクションバーを更新
                MainActivity.getInstance().setMenuType(MainActivity.MenuType.TangoEdit);
                break;
            case icon_moving:
            {
                List<UIcon> icons = mIconManager.getCheckedIcons();
                for (UIcon icon : icons) {
                    UDrawManager.getInstance().addDrawable(icon);
                }
            }
                break;
            case icon_selecting:
                isDragMove = false;
                // ゴミ箱の中のアイコンを選択状態にしてもゴミ箱メニューは表示しない(TangoEdit2に切り替えない)
                if (getParentType() != TangoParentType.Trash) {
                    MainActivity.getInstance().setMenuType(MainActivity.MenuType.TangoEdit2);
                }
                break;
        }

        this.state = state;
    }

    /**
     * Windowに表示するアイコンを設定する
     * どのアイコンを表示するかはどの親をもつアイコンを表示するかで指定する
     * @param parentType
     * @param parentId
     */
    public void setIcons(TangoParentType parentType, int parentId) {

        this.parentType = parentType;
        this.parentId= parentId;

        // DBからホームに表示するアイコンをロード
        List<TangoItem> items = RealmManager.getItemPosDao().selectItemsByParentType(
                parentType, parentId, true
        );
        // 今あるアイコンはクリアしておく
        mIconManager.getIcons().clear();

        // ゴミ箱を配置
        if (parentType == TangoParentType.Home) {
            mIconManager.addNewIcon(IconType.Trash, TangoParentType.Home, 0, AddPos.Top);
        }

        for (TangoItem item : items) {
            mIconManager.addIcon(item, AddPos.Tail);
        }

        sortIcons(false);
    }

    /**
     * Constructor
     */
    protected UIconWindow(UWindowCallbacks windowCallbacks,
                        UIconCallbacks iconCallbacks,
                        boolean isHome, WindowDir dir,
                        int width, int height, int bgColor) {
        super(null, DRAW_PRIORITY, 0, 0, width, height, bgColor);
        basePos = new PointF(0,0);
        if (isHome) {
            type = WindowType.Home;
        } else {
            type = WindowType.Sub;
            addCloseIcon();
        }
        mIconManager = UIconManager.createInstance(this, iconCallbacks);
        this.windowCallbacks = windowCallbacks;
        this.dir = dir;
        iconMargin = UDpi.toPixel(ICON_MARGIN);
        iconW = UDpi.toPixel(ICON_W);
        iconH = UDpi.toPixel(ICON_H);
    }

    /**
     * Create class instance
     * It doesn't allow to create multi Home windows.
     * @return
     */
    public static UIconWindow createInstance(UWindowCallbacks windowCallbacks,
                                             UIconCallbacks iconCallbacks,
                                             boolean isHome, WindowDir dir,
                                             int width, int height, int bgColor)
    {
        UIconWindow instance = new UIconWindow(windowCallbacks,
                iconCallbacks, isHome, dir, width, height, bgColor);

        return instance;
    }

    /**
     * Windowを生成する
     * インスタンス生成後に一度だけ呼ぶ
     */
    public void init() {
        if (type == WindowType.Home) {
            setIcons(TangoParentType.Home, 0);
        }
    }

    /**
     * 毎フレーム行う処理
     * @return true:再描画を行う(まだ処理が終わっていない)
     */
    public DoActionRet doAction() {
        DoActionRet ret = DoActionRet.None;
        boolean allFinished;
        List<UIcon> icons = getIcons();

        // Windowの移動
        if (isMoving) {
            if (!autoMoving()) {
                isMoving = false;
            }
        }

        // アイコンの移動
        if (icons != null) {
            if (state == WindowState.icon_moving) {
                allFinished = true;
                for (UIcon icon : icons) {
                    if (icon.autoMoving()) {
                        allFinished = false;
                    }
                }
                if (allFinished) {
                    endIconMoving();
                }
                ret = DoActionRet.Redraw;
            }
        }

        return ret;
    }

    /**
     * 描画処理
     * UIconManagerに登録されたIconを描画する
     * @param canvas
     * @param paint
     * @return trueなら描画継続
     */
    public void drawContent(Canvas canvas, Paint paint, PointF offset)
    {
        if (!isShow) return;

        List<UIcon> icons = getIcons();
        if (icons == null) return;

        // 背景を描画
        drawBG(canvas, paint);

        // ウィンドウの座標とスクロールの座標を求める
        PointF _offset = new PointF(pos.x - contentTop.x, pos.y - contentTop.y);
        Rect windowRect = new Rect((int)contentTop.x, (int)contentTop.y, (int)contentTop.x + size.width, (int)contentTop.y + size.height);

        // クリッピング領域を設定
        canvas.save();
        canvas.clipRect(rect);

        // 選択中のアイコンに枠を表示する
        if (mIconManager.getSelectedIcon() != null) {
            UDraw.drawRoundRectFill(canvas, paint,
                    new RectF(mIconManager.getSelectedIcon().getRectWithOffset
                            (_offset, 5)), 10.0f, SELECTED_ICON_BG_COLOR, 0, 0);
        }
        for (UIcon icon : mIconManager.getIcons()) {
            if (icon == dragedIcon) continue;
            // 矩形範囲外なら描画しない
            if (URect.intersect(windowRect, icon.getRect())) {
                icon.draw(canvas, paint, _offset);

            } else {
            }
        }

        if (UDebug.DRAW_ICON_BLOCK_RECT) {
            mIconManager.getBlockManager().draw(canvas, paint, getToScreenPos());
        }

        // クリッピング解除
        canvas.restore();
    }


    /**
     * 描画オフセットを取得する
     * @return
     */
    public PointF getDrawOffset() {
        return null;
    }

    /**
     * Windowのサイズを更新する
     * Windowのサイズを更新する
     * Windowのサイズを更新する
     * サイズ変更に合わせて中のアイコンを再配置する
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        super.setSize(width, height);
        // アイコンの整列
        sortIcons(false);
    }

    /**
     * アイコンを整列する
     * Viewのサイズが確定した時点で呼び出す
     */
    public void sortIcons(boolean animate) {
        List<UIcon> icons = getIcons();
        UIcon selectedIcon = null;
        if (icons == null) return;

        int maxSize = 0;

        int i=0;
        if (dir == WindowDir.Vertical) {
            int column = (clientSize.width - iconMargin) / (iconW + iconMargin);
            if (column <= 0) {
                return;
            }
            int margin = (clientSize.width - iconW * column) / (column + 1);
            for (UIcon icon : icons) {
                int x = margin + (i % column) * (iconW + margin);
                int y = margin + (i / column) * (iconH + margin);
                int height = y + (iconH + margin) * 2;
                if (height >= maxSize) {
                    maxSize = height;
                }
                if (animate) {
                    icon.startMoving( x, y, MOVING_TIME);
                } else {
                    icon.setPos(x, y);
                }
                // 選択アイコンがあるかどうかチェック
                if (icon == mIconManager.getSelectedIcon()) {
                    selectedIcon = icon;
                }

                i++;
            }
        } else {
            int column = (clientSize.height - iconMargin) / (iconH + iconMargin);
            if (column <= 0) {
                return;
            }
            int margin = (clientSize.height - iconH * column) / (column + 1);
            for (UIcon icon : icons) {
                int x = margin + (i / column) * (iconW + margin);
                int y = margin + (i % column) * (iconH + margin);
                int width = x + (iconW + margin);
                if (width >= maxSize) {
                    maxSize = width;
                }
                if (animate) {
                    icon.startMoving(x, y, MOVING_TIME);
                } else {
                    icon.setPos(x, y);
                }

                // 選択アイコンがあるかどうかチェック
                if (icon == mIconManager.getSelectedIcon()) {
                    selectedIcon = icon;
                }
                i++;
            }
        }

        if (!animate) {
            IconsPosFixed();
        }

        if (state == WindowState.icon_selecting) {
            if (isDropInBox) {
                nextState = WindowState.none;
            } else {
                nextState = WindowState.icon_selecting;
            }
        } else {
            nextState = WindowState.none;
        }

        setState(WindowState.icon_moving);

        // メニューバーに重ならないように下にマージンを設ける
        if (dir == WindowDir.Vertical) {
            setContentSize(size.width, maxSize + MARGIN_D, true);
            contentTop.y = mScrollBarV.updateContent(contentSize.height);
        } else {
            setContentSize(maxSize + MARGIN_D, size.height, true);
            contentTop.x = mScrollBarH.updateContent(contentSize.width);
        }

        // 必要があれば選択アイコンをクリア
        if (selectedIcon == null) {
            mIconManager.setSelectedIcon(null);
        }
    }

    /**
     * アイコンの座標が確定
     * アイコンの再配置完了時(アニメーションありの場合はそれが終わったタイミング)
     */
    private void IconsPosFixed() {
        mIconManager.updateBlockRect();
    }

    /**
     * 長押しされた時の処理
     * @param vt
     */
    private boolean longPressed(ViewTouch vt) {
        List<UIcon> icons = getIcons();
        if (icons == null) return false;

        // 長押しを離したときにClickイベントが発生しないようにする
        vt.setTouching(false);

        if (state == WindowState.none) {
            // チェック中のアイコンが１つでも存在していたら他のアイコンを全部チェック可能状態に変更
            boolean isChecking = false;
            for (UIcon icon : icons) {
                if (icon.isChecking) {
                    isChecking = true;
                    break;
                }
            }
            if (isChecking) {
                changeIconChecked(icons, true);
                setState(WindowState.icon_selecting);

                // Vibrate
                MainActivity.getInstance().startVibration(100);
            }
        } else if (state == WindowState.icon_selecting) {
            // チェック中ならチェック可能状態を解除
            setState(WindowState.none);

            // Vibrate
            MainActivity.getInstance().startVibration(100);
        }
        return true;
    }

    /**
     * アイコンをドラッグ開始
     * @param vt
     */
    private boolean dragStart(ViewTouch vt) {
        List<UIcon> icons = getIcons();
        if (icons == null) return false;

        boolean ret = false;
        isDragMove = false;

        List<UIcon> checkedIcons = mIconManager.getCheckedIcons();
        if (checkedIcons.size() > 0) {
            setState(WindowState.icon_selecting);
        }

        if (state == WindowState.none) {
            // ドラッグ中のアイコンが１つでもあればドラッグ開始
            for (UIcon icon : icons) {
                if (icon.isDraging) {
                    setDragedIcon(icon);
                    ret = true;
                    isDragMove = true;
                    System.out.println("isDragMove=true");
                    break;
                }
            }

            if (ret) {
                setState(WindowState.drag);
                return true;
            }
        } else if (state == WindowState.icon_selecting) {
            // チェックしたアイコンをまとめてドラッグ
            PointF offset = getToWinPos();

            // チェックされたアイコンが最前面に表示されるように描画優先度をあげる
            for (UIcon icon : checkedIcons) {
                icon.isDraging = true;
                UDrawManager.getInstance().addWithNewPriority(icon, DrawPriority.DragIcon.p());
            }
            // チェックアイコンのどれかをタッチしていたらドラッグ開始
            for (UIcon icon : checkedIcons) {
                if (icon.getRect().contains((int) vt.touchX(offset.x), (int) vt.touchY(offset.y))) {
                    ret = true;
                    isDragMove = true;
                    System.out.println("isDragMove=true2");
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * ドラッグ中の移動処理
     * @param vt
     * @return
     */
    private boolean dragMove(ViewTouch vt) {
        // ドラッグ中のアイコンを移動
        boolean isDone = false;
        if (!isDragMove) return false;

        if (state == WindowState.drag) {
            if (dragedIcon == null) return false;
            // ドラッグ中のアイコンを移動する
            dragedIcon.move(vt.getMoveX(), vt.getMoveY());
        } else if (state == WindowState.icon_selecting){
            // チェックしたアイコンをまとめて移動する
            List<UIcon> icons = mIconManager.getCheckedIcons();
            if (icons != null) {
                for (UIcon icon : icons) {
                    icon.move(vt.getMoveX(), vt.getMoveY());
                }
            }
        } else {
            return false;
        }

        // 現在のドロップフラグをクリア
        mIconManager.setDropedIcon(null);

        for (UIconWindow window : windows.getWindows()) {
            // ドラッグ中のアイコンが別のアイコンの上にあるかをチェック
            Point dragPos = new Point((int) window.toWinX(vt.getX()), (int) window.toWinY(vt.getY()));

            UIconManager manager = window.getIconManager();
            if (manager == null) continue;

            // ドラッグ先のアイコンと重なっているアイコンを取得する
            // 高速化のために幾つかのアイコンをセットにしたブロックと判定する処理(getOverLappedIcon()内)を使用する
            UIcon dropIcon;
            if (state == WindowState.drag) {
                LinkedList<UIcon> exceptIcons = new LinkedList<>();
                exceptIcons.add(dragedIcon);
                dropIcon = manager.getOverlappedIcon(dragPos, exceptIcons);
            } else {
                List<UIcon> checkedIcons = mIconManager.getCheckedIcons();
                dropIcon = manager.getOverlappedIcon(dragPos, checkedIcons);
            }
            if (dropIcon != null) {
                if (state == WindowState.icon_selecting) {
                    // 複数アイコンチェック中
                    List<UIcon> checkedIcons = mIconManager.getCheckedIcons();
                    boolean allOk = true;
                    for (UIcon _dragIcon : checkedIcons) {
                        if (_dragIcon.canDrop(dropIcon, dragPos.x, dragPos.y) == false) {
                            allOk = false;
                            break;
                        }
                    }
                    if (allOk) {
                        mIconManager.setDropedIcon(dropIcon);
                    }
                } else {
                    // シングル
                    isDone = true;
                    if (dragedIcon.canDrop(dropIcon, dragPos.x, dragPos.y)) {
                        mIconManager.setDropedIcon(dropIcon);
                    }
                }
                break;
            }
        }

        return isDone;
    }

    /**
     * ドラッグ終了時の処理（通常時)
     * @param vt
     * @return trueならViewを再描画
     */
    private boolean dragEndNormal(ViewTouch vt) {

        // 他のアイコンの上にドロップされたらドロップ処理を呼び出す
        if (dragedIcon == null) return false;

        mIconManager.setDropedIcon(null);

        List<UIcon> srcIcons = getIcons();
        for (UIconWindow window : windows.getWindows()) {
            // Windowの領域外ならスキップ
            if (!(window.rect.contains((int)vt.getX(),(int)vt.getY()))){
                continue;
            }

            // BookタイプのアイコンをサブWindowに移動できない
            // ただしサブWindowがゴミ箱の場合は除く
            if (window == windows.getSubWindow()) {
                if (dragedIcon.type == IconType.Book &&
                        window.getParentType() != TangoParentType.Trash)
                {
                    continue;
                }
            }

            List<UIcon> dstIcons = window.getIcons();

            if (dstIcons == null) continue;

            // スクリーン座標系からWindow座標系に変換
            float winX = window.toWinX(vt.getX());
            float winY = window.toWinY(vt.getY());

            // 全アイコンに対してドロップをチェックする
            ReturnValueDragEnd ret = checkDropNormal(dstIcons, winX, winY);
            boolean isDroped = ret.isDroped;

            // 移動あり
            if (isDroped && ret.dropedIcon != null) {
                switch(ret.movingType) {
                    case Insert:
                        // ドロップ先の位置に挿入
                        insertIcons(dragedIcon, ret.dropedIcon, true);
                        break;
                    case Exchange:
                        // ドロップ先のアイコンと場所交換
                        changeIcons(dragedIcon, ret.dropedIcon);
                        break;
                }
            }

            // その他の場所にドロップされた場合
            if (!isDroped && dstIcons != null ) {
                boolean isMoved = false;

                // 最後のアイコン以降の領域
                if (dstIcons.size() > 0) {
                    UIcon lastIcon = dstIcons.get(dstIcons.size() - 1);
                    if ((lastIcon.getY() <= winY &&
                            winY <= lastIcon.getBottom() &&
                            lastIcon.getRight() <= winX) ||
                            (lastIcon.getBottom() <= winY))
                    {
                        isMoved = true;
                        isDroped = true;
                    }
                } else {
                    isMoved = true;
                    isDroped = true;
                }

                if (isMoved) {


                    // 最後のアイコンの後の空きスペースにドロップされた場合
                    // ドラッグ中のアイコンをリストの最後に移動
                    srcIcons.remove(dragedIcon);
                    dstIcons.add(dragedIcon);
                    // 親の付け替え
                    dragedIcon.setParentWindow(window);

                    // データベース更新
                    if (this == window) {
                        RealmManager.getItemPosDao().saveIcons(srcIcons,
                                parentType, parentId);
                    } else {
                        TangoItemPos itemPos = dragedIcon.getTangoItem().getItemPos();
                        itemPos.setParentType(window.parentType.ordinal());
                        itemPos.setParentId(window.parentId);

                        RealmManager.getItemPosDao().saveIcons(srcIcons,
                                parentType, parentId);
                        RealmManager.getItemPosDao().saveIcons(dstIcons,
                                window.parentType, window.parentId);
                    }
                }
            }

            // 再配置
            if (this != window) {
                // 座標系変換(移動元Windowから移動先Window)
                if (isDroped) {
                    dragedIcon.setPos(win1ToWin2X(dragedIcon.getPos().x, this, window), win1ToWin2Y(dragedIcon.getPos().y, this, window));
                }
                window.sortIcons(true);
            }
            if (isDroped) break;
        }
        this.sortIcons(true);

        return true;
    }

    /**
     * dragEndNormalInsert の戻り値
     */
    public class ReturnValueDragEnd {
        UIcon dropedIcon;
        IconMovingType movingType;
        boolean isDroped;
    }

    /**
     * ReturnValueDragEnd からドロップ判定部分の処理を抜き出し
     * @return
     */
    private ReturnValueDragEnd checkDropNormal(
            List<UIcon>dstIcons, float winX, float winY)
    {
        ReturnValueDragEnd ret = new ReturnValueDragEnd();
        ret.movingType = IconMovingType.Insert;

        for (int i=0; i<dstIcons.size(); i++) {
            UIcon dropIcon = dstIcons.get(i);
            if (dropIcon == dragedIcon) continue;

            // ドラッグアイコンが画面外ならスキップ or break
            if (dir == WindowDir.Vertical) {
                if (contentTop.y > dropIcon.getBottom()) {
                    continue;
                } else if (contentTop.y + size.height < dropIcon.getY()){
                    // これ以降は画面外に表示されるアイコンなので処理を中止
                    break;
                }
            } else {
                if (contentTop.x > dropIcon.getRight()) {
                    continue;
                } else if (contentTop.x + size.width < dropIcon.getX()){
                    break;
                }
            }

            // ドロップ処理をチェックする
            if (dragedIcon.canDrop(dropIcon, winX, winY)) {
                ret.dropedIcon = dropIcon;
                switch (dropIcon.getType()) {
                    case Card:
                        // ドラッグ位置のアイコンと場所を交換する
                        ret.movingType = IconMovingType.Exchange;
                        ret.isDroped = true;
                        break;
                    case Book:
                        if (dragedIcon.getType() != IconType.Card) {
                            ret.movingType = IconMovingType.Exchange;
                            ret.isDroped = true;
                            break;
                        }
                    case Trash:
                        // Containerの中に挿入する　
                        moveIconIn(dragedIcon, dropIcon);
                        ret.movingType = IconMovingType.MoveIn;

                        for (UIconWindow win : windows.getWindows()) {
                            UIconManager manager = win.getIconManager();
                            if (manager != null) {
                                manager.updateBlockRect();
                            }
                        }
                        ret.isDroped = true;
                        break;
                }
                break;
            } else {
                // アイコンのマージン部分にドロップされたかのチェック
                if (dir == WindowDir.Vertical) {
                    // 縦画面
                    if (dropIcon.getX() - iconMargin * 2 <= winX &&
                            winX <= dropIcon.getRight() + iconMargin * 2 &&
                            dropIcon.getY() - iconMargin * 2  <= winY &&
                            winY <= dropIcon.getBottom() + iconMargin * 2 )
                    {
                        // ドラッグ位置（アイコンの左側)にアイコンを挿入する
                        ret.dropedIcon = dropIcon;
                        ret.isDroped = true;
                        break;
                    } else if (dropIcon.getX() + (iconMargin + iconW) * 2 > size.width ) {
                        // 右端のアイコンは右側に挿入できる
                        if (winX > dropIcon.getRight() &&
                                dropIcon.getY() <= winY &&
                                winY <= dropIcon.getY() + dropIcon.getSize().height )
                        {
                            // 右側の場合は次のアイコンの次の位置に挿入
                            if (i < dstIcons.size() - 1) {
                                dropIcon = dstIcons.get(i+1);
                            }
                            ret.dropedIcon = dropIcon;
                            ret.isDroped = true;
                            break;
                        }
                    }
                } else {
                    // 横画面
                    if (dropIcon.getY() - iconMargin * 2 <= winY &&
                            winY <= dropIcon.getY() + iconMargin &&
                            dropIcon.getX() <= winX && winX <= dropIcon.getX() + dropIcon.getSize()
                            .width )
                    {
                        ret.dropedIcon = dropIcon;
                        ret.isDroped = true;
                        break;
                    } else if (dropIcon.getY() + (iconMargin + iconH) * 2 > size.height ) {
                        // 下端のアイコンは下側に挿入できる
                        if (winY > dropIcon.getBottom() &&
                                dropIcon.getX() <= winX &&
                                winX <= dropIcon.getX() + dropIcon.getSize().width )
                        {
                            // 右側の場合は次のアイコンの次の位置に挿入
                            if (i < dstIcons.size() - 1) {
                                dropIcon = dstIcons.get(i+1);
                            }
                            ret.dropedIcon = dropIcon;
                            ret.isDroped = true;
                            break;
                        }
                    }
                }
            }
        }
        return ret;
    }

    /**
     * ドラッグ終了時の処理（アイコン選択時)
     * @param vt
     * @return trueならViewを再描画
     */
    private boolean dragEndChecked(ViewTouch vt) {
        // ドロップ処理
        // 他のアイコンの上にドロップされたらドロップ処理を呼び出す
        boolean isDroped = false, isMoved = false;

        mIconManager.setDropedIcon(null);

        List<UIcon> srcIcons = getIcons();
        List<UIcon> checkedIcons = mIconManager.getCheckedIcons();

        for (UIconWindow window : windows.getWindows()) {
            // Windowの領域外ならスキップ
            if (!(window.rect.contains((int)vt.getX(),(int)vt.getY()))){
                continue;
            }

            List<UIcon> dstIcons = window.getIcons();
            if (dstIcons == null) continue;

            // スクリーン座標系からWindow座標系に変換
            float winX = window.toWinX(vt.getX());
            float winY = window.toWinY(vt.getY());


            isDroped = checkDropChecked(checkedIcons, dstIcons, winX, winY);

            // その他の場所にドロップされた場合
            if (!isDroped && dstIcons != null ) {
                isMoved = false;
                if (dstIcons.size() > 0) {
                    UIcon lastIcon = dstIcons.get(dstIcons.size() - 1);
                    if ((lastIcon.getY() <= winY &&
                            winY <= lastIcon.getBottom() &&
                            lastIcon.getRight() <= winX) ||
                            (lastIcon.getBottom() <= winY))
                    {
                        isMoved = true;
                        isDroped = true;
                    }
                } else {
                    isMoved = true;
                }

                if (isMoved) {
                    // 最後のアイコンの後の空きスペースにドロップされた場合
                    // ドラッグ中のアイコンをリストの最後に移動
                    srcIcons.removeAll(checkedIcons);
                    dstIcons.addAll(checkedIcons);
                    // 親の付け替え
                    for (UIcon icon : checkedIcons) {
                        icon.setParentWindow(window);
                    }
                    isDropInBox = true;

                    // DB更新処理
                    if (this == window) {
                        RealmManager.getItemPosDao().saveIcons(srcIcons,
                                parentType, parentId);
                    } else {
                        // ItemPos を更新
                        int dstParentType = window.parentType.ordinal();
                        int dstParentId = window.parentId;

                        for (UIcon icon : checkedIcons) {
                            TangoItemPos itemPos = icon.getTangoItem().getItemPos();
                            itemPos.setParentType(dstParentType);
                            itemPos.setParentId(dstParentId);
                        }
                        // 更新したItemPosを DBに反映する
                        RealmManager.getItemPosDao().saveIcons(srcIcons,
                                parentType, parentId);
                        RealmManager.getItemPosDao().saveIcons(dstIcons,
                                window.parentType, window.parentId);
                    }
                }
            }
            // 再配置
            if (isDroped && srcIcons != dstIcons) {
                // 座標系変換(移動元Windowから移動先Window)
                for (UIcon icon : checkedIcons) {
                    icon.setPos(win1ToWin2X(icon.getX(), this, window), win1ToWin2Y(icon.getY(), this, window));
                }
                window.sortIcons(true);
            }
            if (isDroped) break;
        }
        if ( isDragMove) {
            System.out.println("sort!");
            this.sortIcons(true);
            return true;
        }
        return false;
    }

    /**
     * dragEndCheckedのドロップ処理
     */
    private boolean checkDropChecked(
            List<UIcon>checkedIcons, List<UIcon>dstIcons, float x, float y)
    {
        UIcon dropedIcon = null;

        // ドロップ先に挿入するアイコンのリスト
        LinkedList<UIcon> icons = new LinkedList<>();

        for (UIcon dropIcon : dstIcons) {
            if (dropIcon.getType() == IconType.Card) {
                continue;
            }

            for (UIcon _dragIcon : checkedIcons) {
                if (_dragIcon.canDropIn(dropIcon, x, y)) {
                    icons.add(_dragIcon);
                }
            }
            if (icons.size() > 0) {
                dropedIcon = dropIcon;
                break;
            }
        }

        if (dropedIcon != null) {
            moveIconsIntoBox(icons, dropedIcon);

            // BlockRect更新
            for (UIconWindow win : windows.getWindows()) {
                UIconManager manager = win.getIconManager();
                if (manager != null) {
                    manager.updateBlockRect();
                }
            }
            return true;
        }
        return false;
    }

    /**
     * タッチ処理
     * @param vt
     * @return trueならViewを再描画
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (!isShow) return false;
        if (state == WindowState.icon_moving) return false;

        if (offset == null) {
            offset = new PointF();
        }
        if (super.touchEvent(vt, offset)) {
            return true;
        }

        // 範囲外なら抜ける
        if (!rect.contains((int)vt.touchX(), (int)vt.touchY())) {
            return false;
        }

        boolean done = false;

        // 配下のアイコンのタッチ処理
        List<UIcon> icons = getIcons();
        if (icons != null) {
            for (UIcon icon : icons) {
                if (icon.touchEvent(vt, getToWinPos())) {
                    done = true;
                    break;
                }
            }
        }

        switch (vt.type) {
            case Click:
                if (state == WindowState.icon_selecting) {
                    // 選択されたアイコンがなくなったら選択状態を解除
                    List<UIcon> checkedIcons = mIconManager.getCheckedIcons();
                    if (checkedIcons.size() <= 0) {
                        setState(WindowState.none);
                        done = true;
                    }
                } else {
                    // MainWindowの何もないところをクリックしたらSubWindowを閉じる
                    if (!done && this.type == WindowType.Home) {
                        if (windows.getSubWindow().isShow()) {
                            if (windows.getSubWindow().windowCallbacks != null) {
                                windows.getSubWindow().windowCallbacks.windowClose(windows.getSubWindow());
                            }
                        }
                    }
                }
                break;
            case LongPress:
                longPressed(vt);
                done = true;
                break;
            case Moving:
                if (vt.isMoveStart()) {
                    if (dragStart(vt)) {
                        done = true;
                    }
                }
                if (dragMove(vt)) {
                    done = true;
                }
                break;
            case MoveEnd:
                switch(state) {
                    case none:
                    case drag:
                        if (dragEndNormal(vt)) {
                            done = true;
                        }
                        break;
                    case icon_selecting:
                        // アイコン選択中は
                        if (dragEndChecked(vt)) {
                            done = true;
                        }
                        break;
                }
                break;
            case MoveCancel:
                sortIcons(false);
                setDragedIcon(null);
                break;
        }

        if (!done) {
            // 画面のスクロール処理
            if (scrollView(vt)){
                done = true;
            }

            if (super.touchEvent2(vt, offset)) {
                return true;
            }
        }

        return done;
    }

    /**
     * アイコンの移動が完了
     */
    private void endIconMoving() {
        setState(nextState);
        mIconManager.updateBlockRect();
        if (nextState == WindowState.none) {
            changeIconCheckedAll(false);
        }
        setDragedIcon(null);
    }

    /**
     * ２つのアイコンの位置を交換する
     * @param icon1
     * @param icon2
     */
    private void changeIcons(UIcon icon1, UIcon icon2 )
    {
        // アイコンの位置を交換
        // 並び順も重要！
        UIconWindow window1 = icon1.parentWindow;
        UIconWindow window2 = icon2.parentWindow;
        List<UIcon> icons1 = window1.getIcons();
        List<UIcon> icons2 = window2.getIcons();

        int index = icons2.indexOf(icon2);
        int index2 = icons1.indexOf(icon1);
        if (index == -1 || index2 == -1) return;


        icons1.remove(icon1);
        icons2.add(index, icon1);
        icons2.remove(icon2);
        icons1.add(index2, icon2);

        // データベース更新
        RealmManager.getItemPosDao().changePos(icon1.getTangoItem(), icon2.getTangoItem());

        // 再配置
        if (icons1 != icons2) {
            // 親の付け替え
            icon1.setParentWindow(window2);
            icon2.setParentWindow(window1);

            // ドロップアイコンの座標系を変換

            // アイコン2 UWindow -> アイコン1 UWindow
            icon2.setPos(icon2.getX() + (window2.pos.x - window1.pos.x),
                    icon2.getY() + (window2.pos.y - window1.pos.y));
            window2.sortIcons(true);

        }

        window1.sortIcons(true);
    }

    /**
     * アイコンを挿入する
     * @param icon1  挿入元のアイコン
     * @param icon2  挿入先のアイコン
     * @param animate
     */
    private void insertIcons(UIcon icon1, UIcon icon2, boolean animate)
    {
        UIconWindow window1 = icon1.parentWindow;
        UIconWindow window2 = icon2.parentWindow;
        List<UIcon> icons1 = window1.getIcons();
        List<UIcon> icons2 = window2.getIcons();

        int index1 = icons1.indexOf(icon1);
        int index2 = icons2.indexOf(icon2);

        if (index1 == -1 || index2 == -1) return;

        // 挿入元と先の位置関係で追加と削除の順番が前後する
        if (index1 < index2) {
            icons2.add(index2+1, icon1);
            icons1.remove(icon1);
        } else {
            icons1.remove(icon1);
            icons2.add(index2+1, icon1);
        }

        // 再配置
        if (icons1 != icons2) {
            // 親の付け替え
            icon1.setParentWindow(window2);
            icon2.setParentWindow(window1);

            // ドロップアイコンの座標系を変換
            dragedIcon.setPos(icon1.getX() + window2.pos.x - window1.pos.x,
                    icon1.getY() + window2.pos.y - window1.pos.y);
            window2.sortIcons(animate);

            // データベース更新
            // 挿入位置以降の全てのposを更新
            if (index1 < icons1.size()) {
                RealmManager.getItemPosDao().updatePoses(icons1, icons1.get(index1).getTangoItem()
                        .getPos());
            }
            if (index1 < icons2.size()) {
                RealmManager.getItemPosDao().updatePoses(icons2, icons2.get(index2).getTangoItem()
                        .getPos());
            }
        } else {
            // データベース更新
            // 挿入位置でずれた先頭以降のposを更新
            int startPos = (index1 < index2) ? index1 : index2;
            RealmManager.getItemPosDao().updatePoses(icons1, startPos);
        }

        window1.sortIcons(animate);
    }

    /**
     * アイコンを移動する
     * アイコンを別のボックスタイプのアイコンにドロップした時に使用する
     * @param icon1 ドロップ元のIcon(Card/Book)
     * @param icon2 ドロップ先のIcon(Book/Trash)
     */
    private void moveIconIn(UIcon icon1, UIcon icon2)
    {
        if (icon1 == null || icon2 == null) return;

        // Cardの中には挿入できない
        if (!(icon2 instanceof IconContainer)) {
            return;
        }

        IconContainer container = (IconContainer)icon2;

        UIconWindow window1 = icon1.parentWindow;
        UIconWindow window2 = container.getSubWindow();
        List<UIcon> icons = window1.getIcons();

        icons.remove(icon1);

        if (icon2 == windows.getSubWindow().getParentIcon() && window2.isShow()) {
            List<UIcon> win2Icons = window2.getIcons();
            win2Icons.add(icon1);

            window2.sortIcons(false);
            icon1.setParentWindow(window2);
        }
        // データベース更新
        // 位置情報(TangoItemPos)を書き換える
        int itemId = 0;
        if (container.getParentType() == TangoParentType.Book) {
            itemId = container.getTangoItem().getId();
        }
        RealmManager.getItemPosDao().moveItem(icon1.getTangoItem(),
                container.getParentType().ordinal(),
                itemId);

        window1.sortIcons(true);
        if (window1 != window2) {
            window2.sortIcons(true);
        }
    }

    /**
     * アイコンをゴミ箱の中に移動
     * @param icon
     */
    public void moveIconIntoTrash(UIcon icon) {
        moveIconIn(icon, mIconManager.getTrashIcon());
    }

    /**
     * アイコンをホームに移動する
     * @param icon
     * @param mainWindow
     */
    public void moveIconIntoHome(UIcon icon, UIconWindow mainWindow) {
        if (icon == null) return;

        UIconWindow window1 = icon.parentWindow;
        UIconWindow window2 = mainWindow;
        List<UIcon> icons1 = window1.getIcons();
        List<UIcon> icons2 = window2.getIcons();

        icons1.remove(icon);
        icons2.add(icon);

        if (window2 != null && window2.isShow()) {
            window2.sortIcons(false);
            icon.setParentWindow(window2);
        }
        // データベース更新
        RealmManager.getItemPosDao().moveItemToHome(icon.getTangoItem());

        sortIcons(false);
    }

    /**
     * チェックされた複数のアイコンをBook/Trashの中に移動する
     * @param dropedIcon
     */
    public void moveIconsIntoBox(List<UIcon>checkedIcons, UIcon dropedIcon) {

        if (!(dropedIcon instanceof IconContainer)) {
            return;
        }
        IconContainer _dropedIcon = (IconContainer)dropedIcon;

        // チェックされたアイコンのリストを作成
        if (checkedIcons.size() <= 0) return;

        // 最初のチェックアイコン
        UIcon dragIcon = checkedIcons.get(0);

        UIconWindow window1 = dragIcon.parentWindow;
        UIconWindow window2 = _dropedIcon.getSubWindow();
        List<UIcon> icons = window1.getIcons();

        icons.removeAll(checkedIcons);

        window2.sortIcons(false);
        for (UIcon icon : checkedIcons) {
            icon.isChecking = false;
            icon.setParentWindow(window2);
        }
        // DB更新
        LinkedList<TangoItem> items = new LinkedList<>();
        for (UIcon icon : checkedIcons) {
            items.add(icon.getTangoItem());

        }

        int itemId = 0;
        if (_dropedIcon.getType() != IconType.Trash) {
            itemId = _dropedIcon.getTangoItem().getId();
        }
        RealmManager.getItemPosDao().moveItems(items, _dropedIcon.getParentType().ordinal(),
                itemId);

        // 箱の中に入れた後のアイコン整列後にチェックを解除したいのでフラグを持っておく
        isDropInBox = true;
    }

    /**
     * アイコンを完全に削除する
     * @param icon
     * @return
     */
    public void removeIcon(UIcon icon) {

        mIconManager.removeIcon(icon);
        sortIcons(true);

        // DB更新
        RealmManager.getItemPosDao().deleteItemInTrash(icon.getTangoItem());
    }


    /**
     * アイコンの選択状態を変更する
     * ただしゴミ箱アイコンは除く
     * @param icons
     * @param isChecking  false:チェック状態を解除 / true:チェック可能状態にする
     */
    private void changeIconChecked(List<UIcon> icons, boolean isChecking) {
        if (icons == null) return;

        for (UIcon icon : icons) {
            if (icon instanceof IconTrash) {
                continue;
            }
            icon.isChecking = isChecking;
            if (!isChecking) {
                icon.isChecked = false;
            }
        }
    }

    /**
     * 全てのウィンドウのアイコンの選択状態を変更する
     * @param isChecking
     */
    private void changeIconCheckedAll(boolean isChecking) {
        for (UIconWindow window : windows.getWindows()) {
            List<UIcon> icons = window.getIcons();
            changeIconChecked(icons, isChecking);
        }
    }

    /**
     * 以下Drawableインターフェースのメソッド
     */
    /**
     * アニメーション処理
     * onDrawからの描画処理で呼ばれる
     * @return true:アニメーション中
     */
    public boolean animate() {
        boolean allFinished = true;

        List<UIcon> icons = getIcons();
        if (isAnimating) {
            if (icons != null) {
                allFinished = true;
                for (UIcon icon : icons) {
                    if (icon.animate()) {
                        allFinished = false;
                    }
                }
                if (allFinished) {
                    isAnimating = false;
                }
            }
        }
        return !allFinished;
    }

    /**
     * 移動が完了した時の処理
     */
    public void endMoving() {
        super.endMoving();

        if (isAppearance) {

        } else {
            isShow = false;
        }
        mScrollBarH.setShow(true);
        mScrollBarV.setShow(true);
    }


    public void startMoving() {
        super.startMoving();

        mScrollBarH.setShow(false);
        mScrollBarV.setShow(false);
    }


    /**
     * UButtonCallbacks
     */

}
