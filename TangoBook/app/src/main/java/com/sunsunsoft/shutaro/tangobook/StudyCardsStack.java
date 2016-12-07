package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.LinkedList;

/**
 * Created by shutaro on 2016/12/07.
 */

public class StudyCardsStack extends UDrawable {
    /**
     * Enums
     */
    enum State {
        Starting,   // 開始時の演出
        Main        // 学習中のメイン状態
    }

    /**
     * Consts
     */
    public static final String TAG = "StudyCardsStack";

    // layout
    public static final int MARGIN_V = 50;
    protected static final int MARGIN_H = 50;
    protected static final int MOVING_FRAME = 10;


    /**
     * Member Variables
     */
    protected int mMaxCardNum;
    protected StudyCardsManager mCardManager;

    // 表示前のCard
    protected LinkedList<StudyCard> cardsInBackYard = new LinkedList<>();

    // 表示中のCard
    protected LinkedList<StudyCard> cards = new LinkedList<>();

    /**
     * Get/Set
     */
    /**
     * 残りのカード枚数を取得する
     * @return
     */
    public int getCardCount() {
        return cardsInBackYard.size() + cards.size();
    }

    /**
     * Constructor
     */
    public StudyCardsStack(StudyCardsManager cardManager, float x, float y, int maxHeight) {
        super(100, x, y, StudyCard.WIDTH + MARGIN_H * 2, 0 );

        mMaxCardNum = maxHeight / (StudyCard.HEIGHT + MARGIN_V);
        size.height = mMaxCardNum * (StudyCard.HEIGHT + MARGIN_V);
        mCardManager = cardManager;

        setInitialCards();
    }

    /**
     * Methods
     */
    /**
     * 初期表示分のカードを取得
     */
    protected void setInitialCards() {
        while(mCardManager.getCardCount() > 0) {
            TangoCard tangoCard = mCardManager.popCard();
            StudyCard studyCard = new StudyCard(tangoCard, mCardManager.getStudyType());
            cardsInBackYard.add(studyCard);
        }
    }

    /**
     * 毎フレームの処理
     * @return true:処理中
     */
    public boolean doAction() {
        // 表示待ちのカードを表示させるかの判定
        if (cardsInBackYard.size() > 0 && cards.size() <= mMaxCardNum - 1) {
            boolean startFlag = false;
            if (cards.size() == 0 ) {
                startFlag = true;
            } else {
                // 現在表示中のカードが一定位置より下に来たら次のカードを投入する
                StudyCard card = cards.getLast();
                if (card.pos.y >= StudyCard.HEIGHT) {
                    startFlag = true;
                }
            }
            if (startFlag) {
                StudyCard _card = cardsInBackYard.pop();
                cards.add(_card);
                // 初期座標設定
                _card.setPos(MARGIN_H, -StudyCard.HEIGHT);
                float dstY = (mMaxCardNum - cards.size()) * (StudyCard.HEIGHT + MARGIN_V);
                _card.startAppearMoving(MARGIN_H, dstY, MOVING_FRAME);
            }
        }

        // カードの移動等の処理
        boolean isAllFinished = true;
        for (StudyCard card : cards) {
            if (card.doAction()) {
                isAllFinished = false;
            }
        }
        return !isAllFinished;
    }

    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        // 配下のカードを描画する
        for (StudyCard card : cards) {
            card.draw(canvas, paint, pos);
        }
    }

    /**
     * タッチ処理
     * @param vt
     * @return true:処理中
     */
    public boolean touchEvent(ViewTouch vt) {
        for (StudyCard card : cards) {
            if (card.touchEvent(vt)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Callbacks
     */
}
