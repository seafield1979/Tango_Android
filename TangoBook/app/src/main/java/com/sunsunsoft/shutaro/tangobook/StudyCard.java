package com.sunsunsoft.shutaro.tangobook;

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

public class StudyCard extends UDrawable{
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
    public static final int WIDTH = 400;
    public static final int HEIGHT = 250;

    protected static final int MOVE_FRAME = 10;
    protected static final int MOVE_IN_FRAME = 20;

    protected static final int TEXT_SIZE = 50;
    protected static final int TEXT_COLOR = Color.BLACK;
    protected static final int BG_COLOR = Color.rgb(200,100,200);

    // スライド系
    // 左右にスライドできる距離。これ以上スライドするとOK/NGボックスに入る
    protected static final int SLIDE_LEN = 300;

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
     * @param studyType 出題タイプ false:英語 -> 日本語 / true:日本語 -> 英語
     */
    public StudyCard(TangoCard card, boolean studyType) {
        super(0, 0, 0, WIDTH, HEIGHT);
        if (studyType) {
            wordA = card.getWordB();
            wordB = card.getWordA();
            hintA = card.getHintBA();
            hintB = card.getHintAB();
        } else {
            wordA = card.getWordA();
            wordB = card.getWordB();
            hintA = card.getHintAB();
            hintB = card.getHintBA();
        }
        mState = State.None;
        mCard = card;
    }

    /**
     * Methods
     */
    public void startMoving(float dstX, float dstY, int frame) {
        startMoving(MovingType.Deceleration, dstX, dstY, frame);
        setBasePos(dstX, dstY);
        mState = State.Moving;
    }

    /**
     * ボックスの中に移動
     */
    public void startMoveIntoBox(float dstX, float dstY)
    {
        startMoving(MovingType.Deceleration, dstX, dstY, size.width / 4, size.height / 4,
                MOVE_IN_FRAME);
        mState = State.Moving;
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
        if (lastRequest == RequestToParent.MoveToOK) {
            moveRequest = RequestToParent.MoveIntoOK;
        } else if (lastRequest == RequestToParent.MoveToNG) {
            moveRequest = RequestToParent.MoveIntoNG;
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
        int color = isTouching ? Color.rgb(100,100,200) : BG_COLOR;
        UDraw.drawRoundRectFill(canvas, paint,
                new RectF(_pos.x, _pos.y, _pos.x + size.width, _pos.y + size.height),
                10, color, 0, 0);

        // Text
        if (!isMovingSize) {
            // タッチ中は正解を表示
            String text = isTouching ? wordA : wordB;
            UDraw.drawText(canvas, text, UAlignment.Center, TEXT_SIZE,
                    _pos.x + size.width / 2, _pos.y + size.height / 2, TEXT_COLOR);
        }

        // 矢印
        if (showArrow) {

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
}