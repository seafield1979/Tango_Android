package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * UWindow呼び出し元に通知するためのコールバック
 */
interface UWindowCallbacks {
    void windowClose(UWindow window);
}

/**
 * Viewの中に表示できるWindow
 * 座標、サイズを持ち自由に配置が行える
 */
abstract public class UWindow extends UDrawable implements UButtonCallbacks{
    /**
     * Enums
     */
    enum CloseIconPos {
        LeftTop,
        RightTop
    }


    /**
     * Consts
     */
    public static final String TAG = "UWindow";
    public static final int CloseButtonId = 1000123;

    protected static final int SCROLL_BAR_W = 100;

    /**
     * Member Variables
     */
    protected UWindowCallbacks windowCallbacks;
    protected int bgColor;

    /**
     * Get/Set
     */
    protected Size contentSize = new Size();     // 領域全体のサイズ
    protected Size clientSize = new Size();      // ウィンドウの幅からスクロールバーのサイズを引いたサイズ
    protected PointF contentTop = new PointF();  // 画面に表示する領域の左上の座標
    protected UScrollBar mScrollBarH;
    protected UScrollBar mScrollBarV;
    protected UButtonClose closeIcon;            // 閉じるボタン
    protected CloseIconPos closeIconPos;     // 閉じるボタンの位置

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
     * インスタンス生成には createWindow を使うべし
     */
    protected UWindow(UWindowCallbacks callbacks, int priority, float x, float y, int width, int
            height, int color) {
        super(priority, x,y,width,height);
        this.windowCallbacks = callbacks;
        this.bgColor = color;
        clientSize.width = size.width - SCROLL_BAR_W;
        clientSize.height = size.height - SCROLL_BAR_W;
        updateRect();

        // ScrollBar
        mScrollBarV = new UScrollBar(ScrollBarType.Right, ScrollBarInOut.In, this.pos, width, height, SCROLL_BAR_W, contentSize.height);
        mScrollBarV.setBgColor(Color.rgb(128, 128, 128));

        mScrollBarH = new UScrollBar(ScrollBarType.Bottom, ScrollBarInOut.In, this.pos, width, height, SCROLL_BAR_W, contentSize.height);
        mScrollBarH.setBgColor(Color.rgb(128, 128, 128));

        // 描画オブジェクトに登録する
        drawList = UDrawManager.getInstance().addDrawable(this);
    }

    public void setContentSize(int width, int height) {
        contentSize.width = width;
        contentSize.height = height;
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
    public void setSize(int width, int height) {
        super.setSize(width, height);

        // スクロールバー
        if (mScrollBarV != null) {
            mScrollBarV.updateSize(width, height);
            contentTop.y = mScrollBarV.updateContent(contentSize);
        }
        if (mScrollBarH != null) {
            mScrollBarH.updateSize(width, height);
            contentTop.x = mScrollBarH.updateContent(contentSize);
        }

        // 閉じるボタン
        updateCloseIconPos();
    }

    /**
     * Windowを閉じるときの処理
     */
    public void closeWindow() {
        // 描画オブジェクトから削除する
        if (drawList != null) {
            UDrawManager.getInstance().removeDrawable(this);
//            drawList = null;
        }
    }

    /**
     * 毎フレーム行う処理
     *
     * @return true:描画を行う
     */
    abstract public boolean doAction();

    /**
     * 描画
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        if (!isShow) return;

        // Window内部
        drawContent(canvas, paint);

        // Window枠
        drawFrame(canvas, paint);
    }

    /**
     * コンテンツを描画する
     * @param canvas
     * @param paint
     */
    abstract public void drawContent(Canvas canvas, Paint paint );

    /**
     * Windowの枠やバー、ボタンを描画する
     * @param canvas
     * @param paint
     */
    public void drawFrame(Canvas canvas, Paint paint) {
        // Close Button
        if (closeIcon != null) {
            closeIcon.draw(canvas, paint, pos);
        }

        // スクロールバー
        if (mScrollBarV != null) {
            mScrollBarV.draw(canvas, paint);
        }
        if (mScrollBarH != null) {
            mScrollBarH.draw(canvas, paint);
        }
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
        float moveX = vt.moveX * (-1);
        float moveY = vt.moveY * (-1);

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
        mScrollBarV.updateScroll(contentTop);

        return true;
    }

    /**
     * タッチイベント処理
     * @param vt
     * @return true:再描画
     */
    public boolean touchEvent(ViewTouch vt) {
        if (closeIcon != null) {
            if (closeIcon.touchEvent(vt, pos)) {
                return true;
            }
        }

        // スクロールバーのタッチ処理
        if (mScrollBarV.touchEvent(vt)) {
            contentTop.y = mScrollBarV.getTopPos();
            return true;
        }
        if (mScrollBarH.touchEvent(vt)) {
            contentTop.x = mScrollBarH.getTopPos();
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
     * 閉じるボタンの座標を変更
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

    public boolean UButtonClick(int id) {
        switch (id) {
            case CloseButtonId:
                // 閉じるボタンを押したら自身のWindowを閉じてから呼び出し元の閉じる処理を呼び出す
                closeWindow();
                if (windowCallbacks != null) {
                    windowCallbacks.windowClose(this);
                }
                return true;
        }
        return false;
    }
    public boolean UButtonLongClick(int id) {
        return false;
    }
}
