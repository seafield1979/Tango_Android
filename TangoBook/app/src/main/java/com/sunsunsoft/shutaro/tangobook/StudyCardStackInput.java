package com.sunsunsoft.shutaro.tangobook;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by shutaro on 2016/12/28.
 *
 * 正解を１文字づつ選択するモード（正解は英語のみ対応）
 */

public class StudyCardStackInput extends UDrawable {
    /**
     * Enums
     */
    enum State {
        Starting,       // 開始時の演出
        Main,           // 学習中のメイン状態
        End             // すべての問題を学習終了
    }

    /**
     * Consts
     */
    public static final String TAG = "StudyCardStackSelect";

    // layout
    public static final int MARGIN_V = 30;
    protected static final int TEXT_SIZE = 50;
    protected static final int DRAW_PRIORITY = 100;

    // color
    protected static final int TEXT_COLOR = Color.BLACK;

    /**
     * Member Variables
     */
    protected StudyCardsManager mCardManager;
    protected CardsStackCallbacks cardsStackCallbacks;
    protected int mCanvasW;
    protected StudyMode mStudyMode;
    protected StudyCardInput mStudyCard;
    protected State mState = State.Main;

    // 学習中するカードリスト。出題ごとに１つづつ減っていく
    protected LinkedList<TangoCard> mCards = new LinkedList<>();

    /**
     * Get/Set
     */

    /**
     * 残りのカード枚数を取得する
     * @return
     */
    public int getCardCount() {
        return mCards.size();
    }

    /**
     * Constructor
     */
    public StudyCardStackInput(StudyCardsManager cardManager,
                               CardsStackCallbacks cardsStackCallbacks,
                               float x, float y, int canvasW,
                               int width, int height)
    {
        super(90, x, y, width, height );

        this.cardsStackCallbacks = cardsStackCallbacks;
        mCardManager = cardManager;
        mCanvasW = canvasW;
        mStudyMode = MySharedPref.getStudyMode();

        // カードマネージャーのカードリストをコピー
        for (TangoCard card : mCardManager.getCards()) {
            mCards.add(card);
        }
        if (MySharedPref.getStudyOrder() == StudyOrder.Random) {
            Collections.shuffle(mCards);
        }

        setStudyCard();
    }

    /**
     * Methods
     */


    /**
     * 出題するカードを準備する
     */
    protected void setStudyCard() {
        if (mCards.size() > 0) {
            TangoCard card = mCards.pop();
            mStudyCard = new StudyCardInput(card, mCanvasW, size.height - MARGIN_V);
        } else {
            // 終了
            mState = State.End;
            if (cardsStackCallbacks != null) {
                cardsStackCallbacks.CardsStackFinished();
            }
        }
    }

    /**
     * 出題中のカードをスキップする
     */
    protected void skipCard() {
        if (mStudyCard.mState == StudyCardInput.State.None) {
            mStudyCard.showCorrect();
        } else {
            setStudyCard();
        }
    }

    /**
     * 毎フレームの処理
     * @return true:処理中
     */
    public boolean doAction() {

        switch(mState) {
            case Main:
                if (mStudyCard.getRequest() == StudyCardInput.RequestToParent.End) {
                    if (mStudyCard.isMistaken()) {
                        mCardManager.addNgCard(mStudyCard.mCard);
                    } else {
                        mCardManager.addOkCard(mStudyCard.mCard);
                    }
                    // 表示中のカードが終了したので次のカードを表示
                    if (mCards.size() == 0) {
                        // もうカードがないので終了
                        mState = State.End;
                        if (cardsStackCallbacks != null) {
                            cardsStackCallbacks.CardsStackFinished();
                        }
                    } else {
                        // 次の問題を準備
                        mState = State.Main;
                        setStudyCard();
                        if (cardsStackCallbacks != null) {
                            cardsStackCallbacks.CardsStackChangedCardNum(mCards.size());
                        }
                    }
                }
                break;
        }

        // カードの移動等の処理
        if (mStudyCard.doAction()) {
            return true;
        }

        return false;
    }


    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        // 配下のカードを描画する
        mStudyCard.draw(canvas, paint, pos);
    }

    /**
     * タッチ処理
     * @param vt
     * @return true:処理中
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (mStudyCard.touchEvent(vt, pos)) {
            return true;
        }
        return false;
    }
}
