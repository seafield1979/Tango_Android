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
    private StudyMode mStudyMode;

    /**
     * Get/Set
     */
    public StudyMode getStudyMode() {
        return mStudyMode;
    }

    public int getCardCount() {
        if (mCards == null) return 0;
        return mCards.size();
    }

    public LinkedList<TangoCard> getNgCards() {
        return mNgCards;
    }

    public LinkedList<TangoCard> getOkCards() {
        return mOkCards;
    }

    /**
     * Constructor
     */
    public static StudyCardsManager createInstance(List<TangoCard> cards) {
        StudyCardsManager instance = new StudyCardsManager(cards);
        return instance;
    }

    public static StudyCardsManager createInstance(TangoBook book) {
        boolean notLearned = StudyFilter.toEnum(MySharedPref.readInt(MySharedPref.StudyFilterKey)
        ) == StudyFilter.NotLearned;

        List<TangoCard> _cards = RealmManager.getItemPosDao()
                .selectCardsByBookIdWithOption(book.getId(), notLearned);
        StudyCardsManager instance = new StudyCardsManager(_cards);
        return instance;
    }

    public StudyCardsManager(List<TangoCard> cards) {
        mStudyMode = StudyMode.toEnum(MySharedPref.readInt(MySharedPref.StudyModeKey));
        StudyOrder studyOrder = StudyOrder.toEnum(MySharedPref.readInt(MySharedPref
                .StudyOrderKey));

        if (cards != null) {
            for (TangoCard card : cards) {
                mCards.add(card);
            }

            // ランダムに並び替える
            if (studyOrder == StudyOrder.Random) {
                Collections.shuffle(mCards);
            }
        }
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
