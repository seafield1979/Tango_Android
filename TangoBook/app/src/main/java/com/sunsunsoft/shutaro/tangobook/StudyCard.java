package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by shutaro on 2016/12/07.
 */

public class StudyCard extends UDrawable{
    /**
     * Enums
     */
    enum State {
        None,
        Moving,
    }

    /**
     * Consts
     */
    public static final int WIDTH = 400;
    public static final int HEIGHT = 250;
    protected static final int TEXT_SIZE = 50;
    protected static final int TEXT_COLOR = Color.BLACK;
    public static final int BG_COLOR = Color.rgb(200,100,200);

    /**
     * Member Variables
     */
    protected PointF basePos = new PointF();
    protected State mState;
    protected String wordA, wordB;
    protected String hintA, hintB;
    protected TangoCard mCard;
    protected boolean isTouching;

    /**
     * Get/Set
     */

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
    public void startAppearMoving(float dstX, float dstY, int frame) {
        startMoving(MovingType.Deceleration, dstX, dstY, frame);
        mState = State.Moving;
    }

    public void moveToBasePos(int frame) {
        startMoving(MovingType.Deceleration, basePos.x, basePos.y, frame);
    }

    public void setBasePos(float x, float y) {
        basePos.x = x;
        basePos.y = y;
    }

    /**
     * 自動で実行される何かしらの処理
     * @return
     */
    protected boolean doAction() {
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

        // BG
        UDraw.drawRoundRectFill(canvas, paint,
                new RectF(_pos.x, _pos.y, _pos.x + size.width, _pos.y + size.height),
                10, BG_COLOR, 0, 0);

        // Text
        // タッチ中は正解を表示
        String text = isTouching ? wordA : wordB;
        UDraw.drawText(canvas, text, UAlignment.Center, TEXT_SIZE,
                _pos.x + size.width / 2, _pos.y + size.height / 2, TEXT_COLOR);
    }

    /**
     * タッチ処理
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt) {
        return false;
    }

    /**
     * Callbacks
     */
}
