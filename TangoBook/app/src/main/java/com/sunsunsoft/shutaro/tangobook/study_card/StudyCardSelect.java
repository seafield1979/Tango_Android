package com.sunsunsoft.shutaro.tangobook.study_card;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.database.TangoCard;
import com.sunsunsoft.shutaro.tangobook.util.UColor;
import com.sunsunsoft.shutaro.tangobook.util.Size;

/**
 * Created by shutaro on 2016/12/27.
 *
 * ４択学習モードで表示するカード
 * 出題中の４枚のカードのうちの１つ
 */

public class StudyCardSelect extends UDrawable {
    /**
     * Enums
     */
    enum State {
        None,
        Appearance,         // 出現
        ShowAnswer,         // 正解表示中
        Disappearance       // 消える
    }

    // 親に対する要求
    enum RequestToParent {
        None,
        Touch,
        End
    }

    /**
     * Consts
     */
    protected static final int TEXT_SIZE = 50;
    protected static final int TEXT_COLOR = Color.BLACK;
    protected static final int FRAME_COLOR = Color.rgb(150,150,150);


    /**
     * Member Variables
     */
    protected State mState;
    protected String wordA, wordB;
    protected TangoCard mCard;
    protected boolean isTouching;
    protected PointF basePos;

    // 正解のカードかどうか
    protected boolean isCorrect;

    // 正解、不正解のまるばつを表示するかどうか
    protected boolean isShowCorrect;

    // ボックス移動要求（親への通知用)
    protected RequestToParent mRequest = RequestToParent.None;

    public RequestToParent getRequest() {
        return mRequest;
    }

    public void setRequest(RequestToParent request) {
        mRequest = request;
    }

    /**
     * Get/Set
     */

    public TangoCard getTangoCard() {
        return mCard;
    }
    public void setState(State state) {
        mState = state;
    }

    /**
     * 正解/不正解を設定する
     * @param showCorrect
     */
    public void setShowCorrect(boolean showCorrect) {
        mState = State.ShowAnswer;
        isShowCorrect = showCorrect;
    }

    public Rect getRect() {
        return new Rect((int)pos.x - size.width / 2, (int)pos.y - size.height / 2,
                (int)pos.x + size.width / 2, (int)pos.y + size.height / 2);
    }

    /**
     * Constructor
     */
    /**
     *
     * @param card
     * @param isCorrect 正解のカードかどうか(true:正解のカード / false:不正解のカード)
     * @param isEnglish 出題タイプ false:英語 -> 日本語 / true:日本語 -> 英語
     */
    public StudyCardSelect(TangoCard card, boolean isCorrect, boolean isEnglish, int canvasW, int height)
    {
        super(0, 0, 0, canvasW - 200, height);
        this.isCorrect = isCorrect;

        if (isEnglish) {
            wordA = card.getWordA();
            wordB = card.getWordB();
        } else {
            wordA = card.getWordB();
            wordB = card.getWordA();
        }
        mState = State.None;
        mCard = card;

        basePos = new PointF(size.width / 2, size.height / 2);
    }

    /**
     * Methods
     */
    /**
     * 出現時の拡大処理
     */
    public void startAppearance(int frame) {
        Size _size = new Size(size.width, size.height);
        setSize(0, 0);
        startMovingSize(_size.width, _size.height, frame);
        mState = State.Appearance;
    }

    /**
     * 消えるときの縮小処理
     * @param frame
     */
    public void startDisappearange(int frame) {
        startMovingSize(0, 0, frame);
        mState = State.Disappearance;
    }

    /**
     * 自動で実行される何かしらの処理
     * @return
     */
    public DoActionRet doAction() {
        switch (mState) {
            case Appearance:
            case Disappearance:
                if (autoMoving()) {
                    return DoActionRet.Redraw;
                }
                break;
        }
        return DoActionRet.None;
    }

    /**
     * 自動移動完了
     */
    public void endMoving() {
        if (mState == State.Disappearance) {
            // 親に非表示完了を通知する
            mRequest = RequestToParent.End;
        }
        else {
            mState = State.None;
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

        // BG
        int color = 0;
        if (mState == State.ShowAnswer && isShowCorrect) {
            // 解答表示時
            if (isCorrect) {
                color = UColor.LightGreen;
            } else {
                color = UColor.LightRed;
            }
        } else {
            color = Color.WHITE;
        }

        if (isMovingSize) {
            // Open/Close animation
            float x = _pos.x + basePos.x - size.width / 2;
            float y = _pos.y + basePos.y - size.height / 2;

            UDraw.drawRoundRectFill(canvas, paint,
                    new RectF(x, y, x + size.width, y + size.height),
                    10, color, 5, FRAME_COLOR);
        } else {
            UDraw.drawRoundRectFill(canvas, paint,
                    new RectF(_pos.x, _pos.y,
                            _pos.x + size.width, _pos.y + size.height),
                    10, color, 5, FRAME_COLOR);
        }

        // 正解中はマルバツを表示
        PointF _pos2 = new PointF(_pos.x + size.width / 2, _pos.y + size.height / 2);
        if (mState == State.ShowAnswer && isShowCorrect) {
            if (isCorrect) {
                UDraw.drawCircle(canvas, paint, new PointF(_pos2.x, _pos2.y),
                        70, 20, UColor.Green);
            } else {
                UDraw.drawCross(canvas, paint, new PointF(_pos2.x, _pos2.y),
                        70, 20, UColor.Red);
            }
        }

        // Text
        // タッチ中は正解を表示
        if (mState == State.None || mState == State.ShowAnswer) {
            StringBuffer text = new StringBuffer(wordA);
            if (mState == State.ShowAnswer) {
                text.append("\n");
                text.append(wordB);
            }
            UDraw.drawText(canvas, text.toString(), UAlignment.Center, TEXT_SIZE,
                _pos2.x, _pos2.y, TEXT_COLOR);
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

        // アニメーションや移動中はタッチ受付しない
        if (isMovingSize) {
            return false;
        }

        switch(vt.type) {
            case Touch:        // タッチ開始
                break;
            case Click: {
//                UDrawManager.setDebugPoint(100, vt.touchX(), vt.touchY(), UColor.DarkBlue, false);
//                UDrawManager.setDebugPoint(mCard.getId(), pos.x + parentPos.x, pos.y + parentPos.y
//                        , UColor.DarkBlue, false);

                Rect rect = new Rect((int)(pos.x + parentPos.x),
                        (int)(pos.y + parentPos.y),
                        (int)(pos.x + parentPos.x + size.width),
                        (int)(pos.y + parentPos.y + size.height));
                if (rect.contains((int)vt.touchX(), (int)vt.touchY())) {
                    setRequest(RequestToParent.Touch);
                    done = true;
                }
            }
            break;
        }

        return done;
    }

    /**
     * Callbacks
     */
}
