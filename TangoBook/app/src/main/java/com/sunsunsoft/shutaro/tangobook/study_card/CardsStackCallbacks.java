package com.sunsunsoft.shutaro.tangobook.study_card;

/**
 * Created by shutaro on 2017/06/14.
 */
public interface CardsStackCallbacks {
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
