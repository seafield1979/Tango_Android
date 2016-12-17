package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.LinkedList;

/**
 * Created by shutaro on 2016/12/07.
 *
 * 学習中のカードのスタックを表示するクラス
 * カードをスライドしてボックスにふり分ける
 */

interface CardsStackCallbacks {
    /**
     * 残りのカード枚数が変わった
     * @param cardNum
     */
    void CardsStackChangedCardNum(int cardNum);

    /**
     * カードが０になった
     */
    void CardsStackFinished();

}

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
    public static final int MARGIN_V = 30;
    protected static final int MOVING_FRAME = 10;


    /**
     * Member Variables
     */
    protected int mMaxCardNum;
    protected StudyCardsManager mCardManager;
    protected CardsStackCallbacks cardsStackCallbacks;

    // 表示前のCard
    protected LinkedList<StudyCard> mCardsInBackYard = new LinkedList<>();

    // 表示中のCard
    protected LinkedList<StudyCard> mCards = new LinkedList<>();
    // ボックスへ移動中のカード
    protected LinkedList<StudyCard> mToBoxCards = new LinkedList<>();

    protected PointF mOkBoxPos = new PointF(), mNgBoxPos = new PointF();

    /**
     * Get/Set
     */
    public void setOkBoxPos(float x, float y) {
        this.mOkBoxPos.x = x;
        this.mOkBoxPos.y = y;
    }

    public void setNgBoxPos(float x, float y) {
        this.mNgBoxPos.x = x;
        this.mNgBoxPos.y = y;
    }

    /**
     * 残りのカード枚数を取得する
     * @return
     */
    public int getCardCount() {
        return mCardsInBackYard.size() + mCards.size();
    }

    /**
     * Constructor
     */
    public StudyCardsStack(StudyCardsManager cardManager,
                           CardsStackCallbacks cardsStackCallbacks,
                           float x, float y,
                           int width, int maxHeight)
    {
        super(90, x, y, width, 0 );

        this.cardsStackCallbacks = cardsStackCallbacks;
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
            mCardsInBackYard.add(studyCard);
        }
    }

    /**
     * 毎フレームの処理
     * @return true:処理中
     */
    public boolean doAction() {
        // 表示待ちのカードを表示させるかの判定
        if (mCardsInBackYard.size() > 0 && mCards.size() <= mMaxCardNum - 1) {
            boolean startFlag = false;
            if (mCards.size() == 0 ) {
                startFlag = true;
            } else {
                // 現在表示中のカードが一定位置より下に来たら次のカードを投入する
                StudyCard card = mCards.getLast();
                if (card.pos.y >= StudyCard.HEIGHT) {
                    startFlag = true;
                }
            }
            if (startFlag) {
                appearCardFromBackYard();
            }
        }

        // スライドしたカードをボックスに移動する処理
        for (int i=0; i<mCards.size(); i++) {
            StudyCard card = mCards.get(i);
            // ボックスへの移動開始
            boolean breakLoop = false;

            if (card.getMoveRequest() == StudyCard.RequestToParent.MoveToOK ||
                    card.getMoveRequest() == StudyCard.RequestToParent.MoveToNG)
            {
                if (card.getMoveRequest() == StudyCard.RequestToParent.MoveToOK ) {
                    mCardManager.putCardIntoBox(card.getTangoCard(), StudyCardsManager.BoxType.OK);
                    card.startMoveIntoBox(mOkBoxPos.x + 50, mOkBoxPos.y + 50);
                } else {
                    mCardManager.putCardIntoBox(card.getTangoCard(), StudyCardsManager.BoxType.NG);
                    card.startMoveIntoBox(mNgBoxPos.x + 50, mNgBoxPos.y + 50);
                }

                card.setMoveRequest(StudyCard.RequestToParent.None);
                // スライドして無くなったすきまを埋めるための移動
                for (int j=i+1; j<mCards.size(); j++) {
                    StudyCard card2 = mCards.get(j);
                    card2.startMoving(0, (mMaxCardNum - j) * (StudyCard.HEIGHT + MARGIN_V),
                            MOVING_FRAME + 5);
                }
                mToBoxCards.add(card);
                mCards.remove(card);

                if (cardsStackCallbacks != null) {
                    cardsStackCallbacks.CardsStackChangedCardNum(getCardCount());
                }
            }

            if (breakLoop) {
                break;
            }
        }

        // ボックスへ移動中のカードへの要求を処理
        for (int i=0; i<mToBoxCards.size(); i++) {
            StudyCard card = mToBoxCards.get(i);
            // ボックスへの移動開始
            boolean breakLoop = false;

            switch (card.getMoveRequest()) {
                case MoveIntoOK:
                case MoveIntoNG:
                    card.setMoveRequest(StudyCard.RequestToParent.None);
                    mToBoxCards.remove(card);
                    breakLoop = true;
                    if (getCardCount() == 0) {
                        cardsStackCallbacks.CardsStackFinished();
                    }
                    break;
            }
            if (breakLoop) break;
        }


        // カードの移動等の処理
        boolean isAllFinished = true;
        for (StudyCard card : mCards) {
            if (card.doAction()) {
                isAllFinished = false;
            }
        }
        for (StudyCard card : mToBoxCards) {
            if (card.doAction()) {
                isAllFinished = false;
            }
        }
        return !isAllFinished;
    }

    /**
     * バックヤードから１つカードを補充
     */
    protected void appearCardFromBackYard() {
        // バックヤードから取り出して表示用のリストに追加
        StudyCard _card = mCardsInBackYard.pop();
        mCards.add(_card);

        // 初期座標設定
        _card.setPos(0, -StudyCard.HEIGHT);
        float dstY = (mMaxCardNum - mCards.size()) * (StudyCard.HEIGHT + MARGIN_V);
        _card.startMoving(0, dstY, MOVING_FRAME);
        _card.setBasePos(0, dstY);
    }


    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        // 配下のカードを描画する
        for (StudyCard card : mCards) {
            card.draw(canvas, paint, pos);
        }
        for (StudyCard card : mToBoxCards) {
            card.draw(canvas, paint, pos);
        }
    }

    /**
     * タッチ処理
     * @param vt
     * @return true:処理中
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        for (StudyCard card : mCards) {
            if (card.touchEvent(vt, pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Callbacks
     */

}
