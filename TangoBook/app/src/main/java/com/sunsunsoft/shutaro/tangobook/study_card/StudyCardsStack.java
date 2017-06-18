package com.sunsunsoft.shutaro.tangobook.study_card;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.sunsunsoft.shutaro.tangobook.uview.DoActionRet;
import com.sunsunsoft.shutaro.tangobook.database.TangoCard;
import com.sunsunsoft.shutaro.tangobook.uview.udraw.UDrawable;
import com.sunsunsoft.shutaro.tangobook.uview.ViewTouch;
import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;

import java.util.LinkedList;

/**
 * Created by shutaro on 2016/12/07.
 *
 * 学習中のカードのスタックを表示するクラス
 * カードをスライドしてボックスにふり分ける
 */
public class StudyCardsStack extends UDrawable {
    /**
     * Enums
     */
    public enum State {
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
    protected StudyCardsManager mCardManager;
    protected CardsStackCallbacks cardsStackCallbacks;
    protected StudyMode mStudyMode;

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

    public void setStudyMode(StudyMode studyMode) {
        mStudyMode = studyMode;
    }

    /**
     * 残りのカード枚数を取得する
     * @return
     */
    public int getCardCount() {
        return mCardsInBackYard.size() + mCards.size();
    }
    public int getCardCount2() {
        return mCardsInBackYard.size() + mCards.size() + mToBoxCards.size();
    }

    /**
     * Constructor
     */
    public StudyCardsStack(StudyCardsManager cardManager,
                           CardsStackCallbacks cardsStackCallbacks,
                           float x, float y, int canvasW,
                           int width, int maxHeight)
    {
        super(90, x, y, width, 0 );

        this.cardsStackCallbacks = cardsStackCallbacks;
        size.height = maxHeight;
        mCardManager = cardManager;
        mStudyMode = StudyMode.toEnum(MySharedPref.readInt(MySharedPref.StudyModeKey));

        boolean isMultiCard = false;
        if (mStudyMode == StudyMode.SlideMulti) {
            isMultiCard = true;
        }

        setInitialCards(canvasW, isMultiCard, maxHeight);
    }

    /**
     * Methods
     */
    /**
     * 初期表示分のカードを取得
     */
    protected void setInitialCards(int canvasW, boolean isMultiCard, int maxHeight) {
        boolean isEnglish = (MySharedPref.getStudyType() == StudyType.EtoJ);

        while(mCardManager.getCardCount() > 0) {
            TangoCard tangoCard = mCardManager.popCard();
            StudyCard studyCard = new StudyCard(tangoCard, isMultiCard, isEnglish,
                    canvasW, maxHeight);
            mCardsInBackYard.add(studyCard);
        }
    }

    /**
     * 毎フレームの処理
     * @return true:処理中
     */
    public DoActionRet doAction() {
        // 表示待ちのカードを表示させるかの判定
        if (mCardsInBackYard.size() > 0) {
            boolean startFlag = false;
            if (mCards.size() == 0 ) {
                // 表示中のカードが0なら無条件で投入
                startFlag = true;
            } else {
                // 現在表示中のカードが一定位置より下に来たら次のカードを投入する
                StudyCard card = mCards.getLast();
                if (card.getY() >= card.getHeight()) {
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
                float bottomY = card.getBottom();

                for (int j=i+1; j<mCards.size(); j++) {
                    StudyCard card2 = mCards.get(j);
                    card2.startMoving(0, bottomY - card2.getHeight(),
                            MOVING_FRAME + 5);
                    bottomY -= card2.getHeight() + MARGIN_V;
                }
                mCards.remove(card);
                mToBoxCards.add(card);
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

                    if (cardsStackCallbacks != null) {
                        cardsStackCallbacks.CardsStackChangedCardNum(getCardCount2());
                    }

                    if (getCardCount2() == 0) {
                        cardsStackCallbacks.CardsStackFinished();
                    }
                    break;
            }
            if (breakLoop) break;
        }


        // カードの移動等の処理
        DoActionRet ret = DoActionRet.None;
        for (StudyCard card : mCards) {
            if (card.doAction() != DoActionRet.None) {
                ret = DoActionRet.Redraw;
            }
        }
        for (StudyCard card : mToBoxCards) {
            if (card.doAction() != DoActionRet.None) {
                ret = DoActionRet.Redraw;
            }
        }
        return ret;
    }

    /**
     * バックヤードから１つカードを補充
     */
    protected void appearCardFromBackYard() {
        if (mCardsInBackYard.size() == 0) {
            return;
        }

        // バックヤードから取り出して表示用のリストに追加
        StudyCard card = mCardsInBackYard.pop();

        // 初期座標設定
        card.setPos(0, -card.getHeight());

        float dstY;

        if (mCards.size() > 0) {
            // スタックの最後のカードの上に配置
            int height = 0;
            for (StudyCard _card : mCards) {
                height += _card.getHeight() + MARGIN_V;
            }
            dstY = size.height - height - card.getHeight();
        } else {
            dstY = size.height - card.getHeight();
        }

        mCards.add(card);

        card.startMoving(0, dstY, MOVING_FRAME);
        card.setBasePos(0, dstY);
    }


    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        PointF _offset = new PointF(pos.x + size.width / 2, pos.y);
        // 配下のカードを描画する
        for (StudyCard card : mCards) {
            card.draw(canvas, paint, _offset);
        }
        for (StudyCard card : mToBoxCards) {
            card.draw(canvas, paint, _offset);
        }
    }

    /**
     * タッチ処理
     * @param vt
     * @return true:処理中
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        PointF _offset = new PointF(pos.x + size.width / 2, pos.y);
        for (StudyCard card : mCards) {
            if (card.touchEvent(vt, _offset)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Callbacks
     */

}
