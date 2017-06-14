package com.sunsunsoft.shutaro.tangobook.study_card;

import com.sunsunsoft.shutaro.tangobook.database.TangoBook;
import com.sunsunsoft.shutaro.tangobook.database.TangoBookDao;
import com.sunsunsoft.shutaro.tangobook.database.TangoCard;
import com.sunsunsoft.shutaro.tangobook.database.TangoCardHistoryDao;
import com.sunsunsoft.shutaro.tangobook.app.MySharedPref;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;

import java.util.Date;
import java.util.List;

/**
 * Created by shutaro on 2017/01/28.
 *
 * 学習関連の処理
 */

public class StudyUtil {

    /**
     * 学習結果を保存
     */
    public static void saveStudyResult(StudyCardsManager cardsManager, TangoBook book) {
        List<TangoCard> okCards = cardsManager.getOkCards();
        List<TangoCard> ngCards = cardsManager.getNgCards();

        // 単語帳の学習履歴
        int historyId = RealmManager.getBookHistoryDao().addOne(book.getId(), okCards.size(),
                ngCards.size());

        // 単語帳の最終学習日時
        book.setLastStudiedTime(new Date());
        RealmManager.getBookDao().updateOne(book);

        // 学習したカード番号
        RealmManager.getStudiedCardDao().addStudiedCards(historyId, okCards, ngCards);

        // カードの学習履歴
        TangoCardHistoryDao cardHistoryDao = RealmManager.getCardHistoryDao();
        cardHistoryDao.updateCards(okCards, ngCards);

        // NGカードを単語帳に追加
        if (MySharedPref.readBoolean(MySharedPref.AddNgCardToBookKey)) {
            // NG単語帳の有無をチェック
            TangoBook ngBook = RealmManager.getBookDao().selectById(TangoBookDao.NGBookId);
            if (ngBook == null) {
                // なかったら作成
                RealmManager.getBookDao().addNgBook();
            }
            // カードを追加
            RealmManager.getCardDao().addNgCards(ngCards);
        }
    }

}
