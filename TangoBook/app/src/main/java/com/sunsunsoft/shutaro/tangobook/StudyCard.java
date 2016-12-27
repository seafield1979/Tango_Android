package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by shutaro on 2016/12/07.
 *
 * 学習カードスタック(StudyCardsStack)に表示されるカード
 * 左右にスライドしてボックスに振りわ得ることができる
 */

public class StudyCard extends UDrawable implements UButtonCallbacks{
    /**
     * Enums
     */
    enum State {
        None,
        Moving,
    }

    // 親に対する要求
    enum RequestToParent {
        None,
        MoveToOK,
        MoveToNG,
        MoveIntoOK,
        MoveIntoNG
    }

    /**
     * Consts
     */
    public static final int WIDTH = 500;
    public static final int MIN_HEIGHT = 150;

    protected static final int MOVE_FRAME = 10;
    protected static final int MOVE_IN_FRAME = 20;

    protected static final int TEXT_SIZE = 50;
    protected static final int MARGIN_TEXT_H = 40;
    protected static final int MARGIN_TEXT_V = 20;

    protected static final int TEXT_COLOR = Color.BLACK;
    protected static final int BG_COLOR = Color.WHITE;
    protected static final int FRAME_COLOR = Color.rgb(150,150,150);
    protected static final int OK_BG_COLOR = Color.rgb(100,200,100);
    protected static final int NG_BG_COLOR = Color.rgb(200,100,100);

    protected static final int ARROW_W = 150;
    protected static final int ARROW_H = 150;
    protected static final int ARROW_MARGIN = 20;

    protected static final int ButtonIdArrowL = 200;
    protected static final int ButtonIdArrowR = 201;

    // スライド系
    // 左右にスライドできる距離。これ以上スライドするとOK/NGボックスに入る
    protected static final int SLIDE_LEN = 250;

    /**
     * Static Varialbes
     */
    protected Bitmap arrowLImage, arrowRImage;

    /**
     * Member Variables
     */
    protected PointF basePos = new PointF();
    protected State mState;
    protected String wordA, wordB;
    protected String hintA, hintB;
    protected TangoCard mCard;
    protected boolean isTouching;
    protected float slideX;
    protected boolean showArrow;
    protected boolean isMoveToBox;

    protected UButtonImage mArrowL, mArrowR;

    // ボックス移動要求（親への通知用)
    protected RequestToParent moveRequest = RequestToParent.None;
    protected RequestToParent lastRequest = RequestToParent.None;

    public RequestToParent getMoveRequest() {
        return moveRequest;
    }

    public void setMoveRequest(RequestToParent moveRequest) {
        this.moveRequest = moveRequest;
    }

    /**
     * Get/Set
     */

    public TangoCard getTangoCard() {
        return mCard;
    }

    public boolean isShowArrow() {
        return showArrow;
    }

    /**
     * Constructor
     */
    /**
     *
     * @param card
     * @param isEnglish 出題タイプ false:英語 -> 日本語 / true:日本語 -> 英語
     */
    public StudyCard(TangoCard card, boolean isEnglish, int canvasW) {
        super(0, 0, 0, WIDTH, 0);
        if (isEnglish) {
            wordA = card.getWordA();
            wordB = card.getWordB();
            hintA = card.getHintAB();
            hintB = card.getHintBA();
        } else {
            wordA = card.getWordB();
            wordB = card.getWordA();
            hintA = card.getHintBA();
            hintB = card.getHintAB();
        }
        mState = State.None;
        mCard = card;

        // カードのサイズを計算する
        // WordA,WordBの大きい方の高さに合わせる
        Size sizeA = UDraw.getTextSize(canvasW, wordA, TEXT_SIZE);
        Size sizeB = UDraw.getTextSize(canvasW, wordB, TEXT_SIZE);

        // width
        int width =  (sizeA.width > sizeB.width) ? sizeA.width : sizeB.width;
        width += MARGIN_TEXT_H * 2;
        if (width > canvasW - 300) {
            width = canvasW - 300;
        } else if (width < size.width) {
            // 元のサイズより小さい場合は元のサイズを採用
            width = size.width;
        }
        size.width = width;

        // height
        int height = (sizeA.height > sizeB.height) ? sizeA.height : sizeB.height;
        height += MARGIN_TEXT_V * 2;
        if (height < MIN_HEIGHT) height = MIN_HEIGHT;
        size.height = height;

        if (arrowLImage == null) {
            arrowLImage = UResourceManager.getBitmapWithColor(R.drawable.arrow_l, UColor.DarkRed);
        }
        mArrowL = UButtonImage.createButton(this, ButtonIdArrowL, 0,
                - (size.width / 2 + ARROW_MARGIN + ARROW_W), (size.height - ARROW_H) / 2,
                ARROW_W, ARROW_H, arrowLImage, null);

        if (arrowRImage == null) {
            arrowRImage = UResourceManager.getBitmapWithColor(R.drawable.arrow_r, UColor.DarkGreen);
        }
        mArrowR = UButtonImage.createButton(this, ButtonIdArrowR, 0,
                size.width / 2 + ARROW_MARGIN, (size.height - ARROW_H) / 2,
                ARROW_W, ARROW_H, arrowRImage, null);
    }

    /**
     * Methods
     */
    public void startMoving(float dstX, float dstY, int frame) {
        startMoving(MovingType.Deceleration, dstX, dstY, frame);
        setBasePos(dstX, dstY);
        showArrow = false;
        mState = State.Moving;
    }

    /**
     * ボックスの中に移動
     */
    public void startMoveIntoBox(float dstX, float dstY)
    {
        startMoving(MovingType.Deceleration, dstX, dstY, size.width / 5, size.height / 5,
                MOVE_IN_FRAME);
        mState = State.Moving;
        isMoveToBox = true;
    }

    /**
     * スライドした位置を元に戻す
     */
    public void moveToBasePos(int frame) {
        startMoving(MovingType.Deceleration, basePos.x, basePos.y, frame);
        mState = State.Moving;
    }

    public void setBasePos(float x, float y) {
        basePos.x = x;
        basePos.y = y;
    }

    /**
     * 自動で実行される何かしらの処理
     * @return
     */
    public boolean doAction() {
        switch (mState) {
            case Moving:
                if (autoMoving()) {
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * 自動移動完了
     */
    public void endMoving() {
        mState = State.None;
        switch(lastRequest) {
            case None:
                showArrow = true;
                break;
            case MoveToOK:
                moveRequest = RequestToParent.MoveIntoOK;
                break;
            case MoveToNG:
                moveRequest = RequestToParent.MoveIntoNG;
                break;
        }
    }

    /**
     * Drawable methods
     */
    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        PointF _pos = new PointF(pos.x, pos.y);
        if (offset != null) {
            _pos.x += offset.x;
            _pos.y += offset.y;
        }
        _pos.x += slideX;

        // BG
        // スライド量に合わせて色を帰る
        if (isMovingSize) {
        }
        else if (slideX == 0) {
            color = BG_COLOR;
        } else if (slideX < 0) {
            color = UColor.mixRGBColor(BG_COLOR, NG_BG_COLOR, -slideX / (float)SLIDE_LEN);
        } else {
            color = UColor.mixRGBColor(BG_COLOR, OK_BG_COLOR, slideX / (float)SLIDE_LEN);
        }
        UDraw.drawRoundRectFill(canvas, paint,
                new RectF(_pos.x - size.width / 2 , _pos.y,
                        _pos.x + size.width / 2, _pos.y + size.height),
                10, color, 5, FRAME_COLOR);

        // Text
        if (!isMoveToBox) {
            // タッチ中は正解を表示
            String text = isTouching ? wordB : wordA;
            UDraw.drawText(canvas, text, UAlignment.Center, TEXT_SIZE,
                    _pos.x, _pos.y + size.height / 2, TEXT_COLOR);
        }

        // 矢印
        if (showArrow && !isTouching && !isMoveToBox) {
            mArrowL.draw(canvas, paint, _pos);
            mArrowR.draw(canvas, paint, _pos);
        }
    }

    /**
     * タッチ処理
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt) {
        return touchEvent(vt, null);
    }

    public boolean touchEvent(ViewTouch vt, PointF parentPos) {
        boolean done = false;

        // 矢印
        if ( mArrowL.touchUpEvent(vt) ) {
            done = true;
        }
        if ( mArrowR.touchUpEvent(vt) ) {
            done = true;
        }

        PointF offset = new PointF(parentPos.x + pos.x + size.width / 2, parentPos.y + pos.y);
        if ( mArrowL.touchEvent(vt, offset) ) {
            return true;
        }
        if (mArrowR.touchEvent(vt, offset)) {
            return true;
        }

        switch(vt.type) {
            case Touch:        // タッチ開始
                if (rect.contains((int)(vt.touchX() - parentPos.x), (int)(vt.touchY() - parentPos.y))) {
                    isTouching = true;
                    done = true;
                }
                break;
            case Moving:       // 移動
                if (isTouching && mState == State.None) {
                    done = true;
                    // 左右にスライド
                    slideX += vt.moveX;
                    // 一定ラインを超えたらボックスに移動
                    if (slideX <= -SLIDE_LEN) {
                        // NG
                        pos.x += slideX;
                        slideX = 0;
                        moveRequest = lastRequest = RequestToParent.MoveToNG;
                    } else if (slideX >= SLIDE_LEN) {
                        // OK
                        pos.x += slideX;
                        slideX = 0;
                        moveRequest = lastRequest = RequestToParent.MoveToOK;
                    }
                }
                break;
            case Click: {
            }
                break;
        }
        if (vt.isTouchUp()) {
            if (isTouching) {
                isTouching = false;
                done = true;
                if (mState == State.None && slideX != 0) {
                    // ベースの位置に戻る
                    pos.x = basePos.x + slideX;
                    moveToBasePos(MOVE_FRAME);
                    slideX = 0;
                }
            }
        }

        return done;
    }

    /**
     * Callbacks
     */
    /**
     * UButtonCallbacks
     */
    /**
     * ボタンがクリックされた時の処理
     * @param id  button id
     * @param pressedOn  押された状態かどうか(On/Off)
     * @return
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        switch(id) {
            case ButtonIdArrowL:
                showArrow = false;
                moveRequest = lastRequest = RequestToParent.MoveToNG;
                return true;
            case ButtonIdArrowR:
                showArrow = false;
                moveRequest = lastRequest = RequestToParent.MoveToOK;
                return true;
        }
        return false;
    }
}
