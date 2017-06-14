package com.sunsunsoft.shutaro.tangobook.study_card;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.sunsunsoft.shutaro.tangobook.uview.*;
import com.sunsunsoft.shutaro.tangobook.database.TangoCard;
import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;
import com.sunsunsoft.shutaro.tangobook.app.StudyMode;
import com.sunsunsoft.shutaro.tangobook.app.StudyType;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by shutaro on 2016/12/27.
 *
 * 4択学習用のカードスタック
 */

public class StudyCardStackSelect extends UDrawable {
    /**
     * Enums
     */
    enum State {
        Starting,      // 開始時の演出
        Main,          // 学習中のメイン状態
        ShowCorrect,   // 正解判定後
        ShowCorrectEnd,// 正解表示終了　
        End             // すべての問題を学習終了
    }

    /**
     * Consts
     */
    public static final String TAG = "StudyCardStackSelect";

    // layout
    public static final int MARGIN_V = 30;
    protected static final int MOVING_FRAME = 10;
    protected static final int STUDY_CARD_NUM = 4;
    protected static final int TEXT_SIZE = 50;
    protected static final int DRAW_PRIORITY = 100;
    protected static final int CARD_MARGIN_V = 20;

    // color
    protected static final int TEXT_COLOR = Color.BLACK;

    /**
     * Member Variables
     */
    protected StudyCardsManager mCardManager;
    protected CardsStackCallbacks cardsStackCallbacks;
    protected int mCanvasW;
    protected StudyMode mStudyMode;
    protected StudyType mStudyType;

    protected StudyCardSelect[] mStudyCards = new StudyCardSelect[STUDY_CARD_NUM];
    protected State mState = State.Main;

    protected UTextView mQuestionView;

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
    public StudyCardStackSelect(StudyCardsManager cardManager,
                                CardsStackCallbacks cardsStackCallbacks,
                                float x, float y, int canvasW,
                                int width, int height)
    {
        super(90, x, y, width, height );

        this.cardsStackCallbacks = cardsStackCallbacks;
        mCardManager = cardManager;
        mCanvasW = canvasW;
        mStudyMode = MySharedPref.getStudyMode();
        mStudyType = MySharedPref.getStudyType();

        // カードマネージャーのカードリストをコピー
        for (TangoCard card : mCardManager.getCards()) {
            mCards.add(card);
        }

        // 出題 TextView
        mQuestionView = UTextView.createInstance(
                "", TEXT_SIZE, DRAW_PRIORITY,
                UAlignment.CenterX, canvasW,
                true, false, width / 2, 0,
                width, TEXT_COLOR, 0);

        setStudyCard();
    }

    /**
     * Methods
     */


    /**
     * １解答分のカードを準備する
     * １枚の正解と３枚の不正解をランダムで配置する
     */
    protected void setStudyCard() {
        TangoCard okCard = mCards.pop();
        List<TangoCard> ngCards;

        boolean isEnglish = (mStudyType == StudyType.EtoJ);

        // 問題
        String questionStr = isEnglish ? okCard.getWordA() : okCard.getWordB();
        mQuestionView.setText(questionStr);

        // 不正解用のカードを取得
        ngCards = RealmManager.getCardDao().selectAtRandom(STUDY_CARD_NUM - 1, okCard.getId());

        StudyCardSelect card;
        int height = (size.height - MARGIN_V - mQuestionView.getHeight()) / STUDY_CARD_NUM;

        // 出題カードの配置
        float y = mQuestionView.getHeight() + MARGIN_V;

        int correctIndex = new Random().nextInt(STUDY_CARD_NUM);
        int ngIndex = 0;
        for (int i=0; i<STUDY_CARD_NUM; i++) {
            if (i == correctIndex) {
                card = new StudyCardSelect(okCard, true, !isEnglish, mCanvasW, height - CARD_MARGIN_V);
            } else {
                card = new StudyCardSelect(ngCards.get(ngIndex), false, !isEnglish, mCanvasW,
                        height -
                        CARD_MARGIN_V);
                ngIndex++;
            }
            mStudyCards[i] = card;

            // 初期座標設定
            // 座標はエリアの中心を指定する
            card.setPos(0, y + i * height);
            card.startAppearance(MOVING_FRAME);
        }
    }

    /**
     * 毎フレームの処理
     * @return true:処理中
     */
    public DoActionRet doAction() {

        switch(mState) {
            case Main:
                // カードがタッチされたら正解判定を行う
                for (StudyCardSelect card : mStudyCards) {
                    if (card.getRequest() == StudyCardSelect.RequestToParent.Touch) {
                        mState = State.ShowCorrect;

                        // 全てのカードを正解表示状態にする
                        for (StudyCardSelect _card : mStudyCards) {
                            card.setRequest(StudyCardSelect.RequestToParent.None);
                            _card.setShowCorrect(_card.isCorrect);
                        }
                        card.setShowCorrect(true);
                        if (card.isCorrect) {
                            // 正解
                            mCardManager.addOkCard(card.mCard);
                        } else {
                            // 不正解
                            mCardManager.addNgCard(card.mCard);
                        }
                    }
                }
                break;
            case ShowCorrect:
                // タッチされたらカードが消えるアニメーション開始
                boolean isTouched = false;
                for (StudyCardSelect card : mStudyCards) {
                    if (card.getRequest() == StudyCardSelect.RequestToParent.Touch) {
                        isTouched = true;
                    }
                }
                if (isTouched) {
                    mState = State.ShowCorrectEnd;
                    for (StudyCardSelect card : mStudyCards) {
                        card.startDisappearange(MOVING_FRAME);
                    }
                }
                break;
            case ShowCorrectEnd:
                // 全てのカードが非表示になるまで待つ
                boolean isAllFinished = true;
                for (StudyCardSelect card : mStudyCards) {
                    if (card.getRequest() != StudyCardSelect.RequestToParent.End) {
                        isAllFinished = false;
                    }
                }
                if (isAllFinished) {
                    if (mCards.size() == 0) {
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
        DoActionRet ret = DoActionRet.None;
        for (StudyCardSelect card : mStudyCards) {
            if (card.doAction() != DoActionRet.None) {
                ret = DoActionRet.Redraw;
            }
        }

        return ret;
    }


    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        // 問題
        mQuestionView.draw(canvas, paint, pos);
        // 配下のカードを描画する
        for (StudyCardSelect card : mStudyCards) {
            card.draw(canvas, paint, pos);
        }
    }

    /**
     * タッチ処理
     * @param vt
     * @return true:処理中
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        for (StudyCardSelect card : mStudyCards) {
            if (card.touchEvent(vt, pos)) {
                return true;
            }
        }
        return false;
    }
}
