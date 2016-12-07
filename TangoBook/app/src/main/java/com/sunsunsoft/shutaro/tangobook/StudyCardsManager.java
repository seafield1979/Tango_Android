package com.sunsunsoft.shutaro.tangobook;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by shutaro on 2016/12/07.
 *
 * 学習画面のカードを管理するクラス
 * これから出題するカード、学習済みのカード等をリスト管理する
 */

public class StudyCardsManager {
    /**
     * Enums
     */
    enum BoxType {
        OK,
        NG
    }

    /**
     * Consts
     */
    public static final String TAG = "StudyCardsManager";


    /**
     * Member Variables
     */
    // 学習するカードのリスト
    private LinkedList<TangoCard> mCards = new LinkedList<>();

    // 学習済みのカードのリスト
    private LinkedList<TangoCard> mNgCards = new LinkedList<>();
    private LinkedList<TangoCard> mOkCards = new LinkedList<>();

    // Options
    private boolean mStudyType;

    /**
     * Get/Set
     */
    public boolean getStudyType() {
        return mStudyType;
    }

    public int getCardCount() {
        if (mCards == null) return 0;
        return mCards.size();
    }

    /**
     * Constructor
     */
    public StudyCardsManager(TangoBook book, boolean studyType, boolean orderRandom, boolean
            notLearned)
    {
        List<TangoCard> _cards = RealmManager.getItemPosDao()
                .selectCardsByBookIdWithOption(book.getId(), notLearned);

        for (TangoCard card : _cards) {
            mCards.add(card);
        }

        // ランダムに並び替える
        if (orderRandom) {
            Collections.shuffle(mCards);
        }
        mStudyType = studyType;
    }

    /**
     * Methods
     */
    /**
     * 出題するカードを１枚抜き出す
     * 抜き出したカードはリストから削除
     * @return
     */
    public TangoCard popCard() {
        return mCards.pop();
    }

    public void putCardIntoBox(TangoCard card, BoxType boxType) {
        if (boxType == boxType.OK) {
            mOkCards.add(card);
        } else {
            mNgCards.add(card);
        }
    }

}
