package com.sunsunsoft.shutaro.tangobook.uview.window;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.sunsunsoft.shutaro.tangobook.TouchType;
import com.sunsunsoft.shutaro.tangobook.util.Size;
import com.sunsunsoft.shutaro.tangobook.util.SizeL;
import com.sunsunsoft.shutaro.tangobook.util.ULog;
import com.sunsunsoft.shutaro.tangobook.uview.DoActionRet;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDraw;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawManager;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawable;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonCallbacks;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonClose;
import com.sunsunsoft.shutaro.tangobook.uview.button.UButtonType;
import com.sunsunsoft.shutaro.tangobook.uview.scrollbar.ScrollBarShowType;
import com.sunsunsoft.shutaro.tangobook.uview.scrollbar.ScrollBarType;
import com.sunsunsoft.shutaro.tangobook.uview.scrollbar.UScrollBar;

/**
 * Viewの中に表示できるWindow
 * 座標、サイズを持ち自由に配置が行える
 */
abstract public class UWindow extends UDrawable implements UButtonCallbacks {
    /**
     * Enums
     */
    public enum CloseIconPos {
        LeftTop,
        RightTop
    }

    // スクロールバーの表示タイプ
    public enum WindowSBShowType {
        Hidden,             // 非表示
        Show,               // 表示
        Show2,              // 表示(スクロール中のみ表示)
        ShowAllways         // 常に表示
    }

    /**
     * Consts
     */
    public static final String TAG = "UWindow";
    public static final int CloseButtonId = 1000123;

    protected static final int SCROLL_BAR_W = 50;
    protected static final int TOP_BAR_H = 50;
    protected static final int TOP_BAR_COLOR = Color.rgb(100,100,200);
    protected static final int FRAME_COLOR = 0;

    /**
     * Member Variables
     */
    protected UWindowCallbacks windowCallbacks;
    protected int bgColor;
    protected int frameColor;

    protected SizeL contentSize = new SizeL();     // 領域全体のサイズ
    protected Size clientSize = new Size();      // ウィンドウの幅からスクロールバーのサイズを引いたサイズ
    protected int topBarH;      // ウィンドウ上部のバーの高さ
    protected int topBarColor;
    protected Size frameSize;       // ウィンドウのフレームのサイズ
    protected PointF contentTop = new PointF();  // クライアント領域のうち画面に表示する領域の左上の座標
    protected UScrollBar mScrollBarH;
    protected UScrollBar mScrollBarV;
    protected UButtonClose closeIcon;            // 閉じるボタン
    protected CloseIconPos closeIconPos;     // 閉じるボタンの位置

    protected WindowSBShowType mSBType;

    /**
     * Get/Set
     */
    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public void setPos(float x, float y, boolean update) {
        pos.x = x;
        pos.y = y;
        if (update) {
            updateRect();
        }
    }
    public Size getClientSize() {
        return size;
    }
    public Rect getClientRect() {
        return new Rect(frameSize.width, frameSize.height + topBarH,
                frameSize.width + clientSize.width, frameSize.height + topBarH + clientSize.height);
    }

    public PointF getContentTop() {
        return contentTop;
    }

    public void setContentTop(PointF contentTop) {
        this.contentTop = contentTop;
    }

    public void setContentTop(float x, float y) {
        contentTop.x = x;
        contentTop.y = y;
    }

    public void setFrameColor(int frameColor) {
        this.frameColor = frameColor;
    }

    public void setTopBar(int height, int color) {
        topBarH = height;
        topBarColor = color;
    }

    public void setFrame(Size size, int color) {
        this.frameSize = size;
        this.frameColor = color;
    }

    public UWindowCallbacks getWindowCallbacks() {
        return windowCallbacks;
    }

    // 座標系を変換する
    // 座標系は以下の３つある
    // 1.Screen座標系  画面上の左上原点
    // 2.Window座標系  ウィンドウ左上原点 + スクロールして表示されている左上が原点

    // Screen座標系 -> Window座標系
    public float toWinX(float screenX) {
        return screenX + contentTop.x - pos.x;
    }

    public float toWinY(float screenY) {
        return screenY + contentTop.y - pos.y;
    }

    public PointF getToWinPos() {
        return new PointF(contentTop.x - pos.x, contentTop.y - pos.y);
    }

    // Windows座標系 -> Screen座標系
    public float toScreenX(float winX) {
        return winX - contentTop.x + pos.x;
    }

    public float toScreenY(float winY) {
        return winY - contentTop.y + pos.y;
    }

    public PointF getToScreenPos() {
        return new PointF(-contentTop.x + pos.x, -contentTop.y + pos.y);
    }

    // Window1の座標系から Window2の座標系に変換
    public float win1ToWin2X(float win1X, UWindow win1, UWindow win2) {
        return win1X + win1.pos.x - win1.contentTop.x - win2.pos.x + win2.contentTop.x;
    }

    public float win1ToWin2Y(float win1Y, UWindow win1, UWindow win2) {
        return win1Y + win1.pos.y - win1.contentTop.y - win2.pos.y + win2.contentTop.y;
    }

    public PointF getWin1ToWin2(UWindow win1, UWindow win2) {
        return new PointF(
                win1.pos.x - win1.contentTop.x - win2.pos.x + win2.contentTop.x,
                win1.pos.y - win1.contentTop.y - win2.pos.y + win2.contentTop.y
        );
    }

    /**
     * Constructor
     */
    /**
     * 外部からインスタンスを生成できないようにprivateでコンストラクタを定義する
     */
    protected UWindow(UWindowCallbacks callbacks, int priority,
                      float x, float y, int width, int height,
                      int bgColor)
    {
        this(callbacks, priority,
                x, y, width, height,
                bgColor, 0, 0, 0);
    }

    protected UWindow(UWindowCallbacks callbacks, int priority,
                      float x, float y, int width, int height,
                      int bgColor, int topBarH, int frameW, int frameH)
    {
        super(priority, x,y,width,height);
        this.windowCallbacks = callbacks;
        this.bgColor = bgColor;
        mSBType = WindowSBShowType.Show2;
        clientSize.width = width - frameW * 2;
        clientSize.height = height - topBarH - frameH * 2;
        this.topBarH = topBarH;
        this.topBarColor = TOP_BAR_COLOR;
        this.frameSize = new Size(frameW, frameH);
        this.frameColor = FRAME_COLOR;
        updateRect();

        // ScrollBar
        ScrollBarShowType showType = ScrollBarShowType.Show;
        switch(mSBType) {
            case Show:               // 表示
                showType = ScrollBarShowType.Show;
                break;
            case Show2:              // 表示(スクロール中のみ表示)
                showType = ScrollBarShowType.Show2;
                break;
            case ShowAllways:        // 常に表示
                showType = ScrollBarShowType.ShowAllways;
                break;
        }

        if (mSBType != WindowSBShowType.Hidden) {
            mScrollBarV = new UScrollBar(ScrollBarType.Vertical, showType, pos,
                    size.width - frameSize.width - SCROLL_BAR_W,
                    frameSize.height + topBarH,
                    clientSize.height, SCROLL_BAR_W,
                    height - SCROLL_BAR_W, contentSize.height);

            mScrollBarH = new UScrollBar( ScrollBarType.Horizontal, showType, pos,
                    frameSize.width,
                    size.height - frameSize.height - SCROLL_BAR_W,
                    clientSize.width,
                    SCROLL_BAR_W, width - SCROLL_BAR_W, contentSize.width);
        }

        // 描画オブジェクトに登録する
//        drawList = UDrawManager.getInstance().addDrawable(this);
    }


    /**
     * Methods
     */

    /**
     * Windowのサイズを更新する
     * サイズ変更に合わせて中のアイコンを再配置する
     * @param width
     * @param height
     */
    public void setSize(int width, int height, boolean update) {
        super.setSize(width, height);

        if (update) {
            updateWindow();
        }

        // 閉じるボタン
        updateCloseIconPos();
    }

    public void setContentSize(int width, int height, boolean update) {
        contentSize.width = width;
        contentSize.height = height;

        if (update) {
            updateWindow();
        }
    }

    public void updateWindow() {
        // clientSize
        if (clientSize.width < contentSize.width &&
                mSBType != WindowSBShowType.Show2)
        {
            clientSize.height = size.height - mScrollBarH.getBgWidth();
        }
        if (clientSize.height < contentSize.height &&
                mSBType != WindowSBShowType.Show2)
        {
            clientSize.width = size.width - mScrollBarV.getBgWidth();
        }

        // スクロールバー
        if (mScrollBarV != null) {
            mScrollBarV.setPageLen(clientSize.height);
            mScrollBarV.updateSize();
            contentTop.y = mScrollBarV.updateContent(contentSize.height);
        }
        if (mScrollBarH != null) {
            mScrollBarH.setPageLen(clientSize.width);
            mScrollBarH.updateSize();
            contentTop.x = mScrollBarH.updateContent(contentSize.width);
        }
    }


    /**
     * Windowを閉じるときの処理
     */
    public void closeWindow() {
        // 描画オブジェクトから削除する
        if (drawList != null) {
            UDrawManager.getInstance().removeDrawable(this);
        }
    }

    /**
     * 毎フレーム行う処理
     *
     * @return true:描画を行う
     */
    abstract public DoActionRet doAction();

    /**
     * 描画
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        if (!isShow) return;

        // BG
        if (offset != null) {
            drawBG(canvas, paint, offset);
        } else {
            drawBG(canvas, paint);
        }

        // Window内部
        PointF _pos = new PointF(frameSize.width, frameSize.height + topBarH);
        if (offset != null) {
            _pos.x += offset.x;
            _pos.y += offset.y;
        }
        drawContent(canvas, paint, _pos);

        // Window枠
        drawFrame(canvas, paint, offset);
    }

    /**
     * コンテンツを描画する
     * @param canvas
     * @param paint
     */
    abstract public void drawContent(Canvas canvas, Paint paint, PointF offset);

    /**
     * Windowの背景を描画する
     * @param canvas
     * @param paint
     * @param rect
     */
    protected void drawBG(Canvas canvas, Paint paint, RectF rect) {
        int frameWidth = (frameColor == 0) ? 0 : 5;
        UDraw.drawRoundRectFill(canvas, paint, rect, 20, bgColor, frameWidth, frameColor);
    }
    protected void drawBG(Canvas canvas, Paint paint, Rect rect) {
        // BG,Frame
        int frameWidth = (frameColor == 0) ? 0 : 5;
        UDraw.drawRoundRectFill(canvas, paint, new RectF(rect), 20, bgColor, frameWidth, frameColor);
    }
    protected void drawBG(Canvas canvas, Paint paint) {
        this.drawBG(canvas, paint, rect);
    }

    protected void drawBG(Canvas canvas, Paint paint, PointF offset) {
        this.drawBG(canvas, paint, new Rect((int)offset.x + rect.left,
                (int)offset.y + rect.top, (int)offset.x + rect.right, (int)offset.y + rect.bottom));
    }

    /**
     * Windowの枠やバー、ボタンを描画する
     * @param canvas
     * @param paint
     */
    public void drawFrame(Canvas canvas, Paint paint, PointF offset) {
        PointF _pos = new PointF(pos.x, pos.y);
        if (offset != null) {
            _pos.x += offset.x;
            _pos.y += offset.y;
        }
        // Frame
        if (frameSize.width > 0 && frameColor != 0) {
            // 左右
            UDraw.drawRectFill(canvas, paint,
                    new Rect((int)_pos.x, (int)_pos.y,
                            (int)_pos.x + frameSize.width, (int)_pos.y + size.height),
                    frameColor, 0, 0);
            UDraw.drawRectFill(canvas, paint,
                    new Rect((int)_pos.x + size.width - frameSize.width, (int)_pos.y,
                            (int)_pos.x + size.width, (int)_pos.y + size.height),
                    frameColor, 0, 0);
        }

        if (frameSize.height > 0 && frameColor != 0) {
            // 上下
            UDraw.drawRectFill(canvas, paint, new Rect((int)_pos.x, (int)_pos.y,
                            (int)_pos.x + size.width, (int)_pos.y + frameSize.height),
                    0, 0, 0);
            UDraw.drawRectFill(canvas, paint,
                    new Rect((int)_pos.x, (int)_pos.y + size.height - frameSize.height,
                            (int)_pos.x + size.width, (int)_pos.y + size.height), 0, 0,
                    0);
        }

        // TopBar
        if (topBarH > 0 && topBarColor != 0) {
            UDraw.drawRectFill(canvas, paint,
                    new Rect((int)_pos.x, (int)_pos.y + frameSize.height,
                            (int)_pos.x + size.width - frameSize.width, (int)_pos.y + frameSize
                            .height + topBarH), topBarColor, 0, 0);
        }

        // Close Button
        if (closeIcon != null && closeIcon.isShow()) {
            closeIcon.draw(canvas, paint, _pos);
        }

        // スクロールバー
        if (mScrollBarV != null && mScrollBarV.isShow()) {

            mScrollBarV.draw(canvas, paint, offset);
        }
        if (mScrollBarH != null && mScrollBarH.isShow()) {
            mScrollBarH.draw(canvas, paint, offset);
        }
    }

    public boolean autoMoving() {
        // Windowはサイズ変更時にclientSizeも変更する必要がある
        if (!isMoving) return false;

        boolean ret = super.autoMoving();

        clientSize = size;

        if (mScrollBarH != null) {
            mScrollBarH.setBgLength(clientSize.width);
        }
        if (mScrollBarV != null) {
            mScrollBarV.setBgLength(clientSize.height);
        }
        updateWindow();
        ULog.print(TAG, "winSize:height:" + size.height + " clientSize:" + clientSize.height);

        return ret;
    }


    /**
     * Viewをスクロールする処理
     * Viewの空きスペースをドラッグすると表示領域をスクロールすることができる
     * @param vt
     * @return
     */
    protected boolean scrollView(ViewTouch vt) {
        if (vt.type != TouchType.Moving) return false;

        // タッチの移動とスクロール方向は逆
        float moveX = vt.getMoveX() * (-1);
        float moveY = vt.getMoveY() * (-1);

        // 横
        if (size.width < contentSize.width) {
            contentTop.x += moveX;
            if (contentTop.x < 0) {
                contentTop.x = 0;
            } else if (contentTop.x + size.width > contentSize.width) {
                contentTop.x = contentSize.width - size.width;
            }
        }

        // 縦
        if (size.height < contentSize.height) {
            contentTop.y += moveY;
            if (contentTop.y < 0) {
                contentTop.y = 0;
            } else if (contentTop.y + size.height > contentSize.height) {
                contentTop.y = contentSize.height - size.height;
            }
        }
        // スクロールバーの表示を更新
        mScrollBarV.updateScroll((long)contentTop.y);

        return true;
    }

    /**
     * タッチイベント処理、子クラスのタッチイベント処理より先に呼び出す
     * @param vt
     * @return true:再描画
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (offset == null) {
            offset = new PointF(pos.x, pos.y);
        }

        if (closeIcon != null && closeIcon.isShow()) {
            if (closeIcon.touchEvent(vt, offset)) {
                return true;
            }
        }

        PointF offset2 = new PointF(offset.x + pos.x, offset.y + pos.y);

        // スクロールバーのタッチ処理
        if (mScrollBarV.isShow()){
            if ( mScrollBarV.touchEvent(vt, offset2)) {
                contentTop.y = mScrollBarV.getTopPos();
                return true;
            }
        }
        if (mScrollBarH.isShow()) {
            if ( mScrollBarH.touchEvent(vt, offset2)) {
                contentTop.x = mScrollBarH.getTopPos();
                return true;
            }
        }
        return false;
    }

    /**
     * 子クラスのタッチ処理の後に呼び出すタッチイベント
     * @param vt
     * @param offset
     * @return
     */
    public boolean touchEvent2(ViewTouch vt, PointF offset) {
        // 配下にタッチイベントを送らないようにウィンドウ内がタッチされたらtureを返す
        if (offset == null) {
            offset = new PointF();
        }
        if (rect.contains((int)vt.touchX(offset.x), (int)vt.touchY(offset.y))) {
            return true;
        }
        return false;
    }

    /**
     * アイコンタイプの閉じるボタンを追加する
     */
    protected void addCloseIcon() {
        this.addCloseIcon(CloseIconPos.LeftTop);
    }
    protected void addCloseIcon(CloseIconPos pos) {
        if (closeIcon != null) return;

        closeIconPos = pos;

        closeIcon = new UButtonClose(this, UButtonType.Press, CloseButtonId, 0, 0, 0,
                Color.rgb(255,0,0));
        updateCloseIconPos();
    }

    /**
     * 閉じるボタンの座標を更新
     * ※Windowが移動したり、サイズが変わった時に呼び出される
     */
    protected void updateCloseIconPos() {
        if (closeIcon == null) return;

        float x, y;
        y = UButtonClose.BUTTON_RADIUS * 2;
        if (closeIconPos == CloseIconPos.LeftTop) {
            x = UButtonClose.BUTTON_RADIUS * 2;
        } else {
            x = size.width - UButtonClose.BUTTON_RADIUS * 2;
        }

        closeIcon.setPos(x, y);
    }

    /**
     * 移動が完了した時の処理
     */
    public void endMoving() {
        super.endMoving();
    }

    /**
     * UButtonCallbacks
     */

    public boolean UButtonClicked(int id, boolean pressedOn) {
        switch (id) {
            case CloseButtonId:
                // 閉じるボタンを押したら自身のWindowを閉じてから呼び出し元の閉じる処理を呼び出す
                if (windowCallbacks != null) {
                    windowCallbacks.windowClose(this);
                } else {
                    closeWindow();
                }
                return true;
        }
        return false;
    }
}
