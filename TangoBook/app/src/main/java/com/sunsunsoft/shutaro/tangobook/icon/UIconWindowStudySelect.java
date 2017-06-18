package com.sunsunsoft.shutaro.tangobook.icon;

import com.sunsunsoft.shutaro.tangobook.uview.window.UWindowCallbacks;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.database.TangoItem;
import com.sunsunsoft.shutaro.tangobook.database.TangoItemType;
import com.sunsunsoft.shutaro.tangobook.database.TangoParentType;

import java.util.List;

/**
 * Created by shutaro on 2016/12/06.
 *
 * 学習する単語帳を選択するWindow
 * ホームに単語アイコンは表示しない
 * アイコンをクリックした時の処理が通常と異なる
 */

public class UIconWindowStudySelect extends UIconWindow {

    /**
     * Member variables
     */

    /**
     * Constructor
     */
    public UIconWindowStudySelect( UWindowCallbacks windowCallbacks,
                                  UIconCallbacks iconCallbacks,
                                  boolean isHome, WindowDir dir,
                                  int width, int height, int bgColor) {
        super(windowCallbacks,
                iconCallbacks, isHome, dir, width, height, bgColor);
    }

    /**
     * Create class instance
     * It doesn't allow to create multi Home windows.
     * @return
     */
    public static UIconWindowStudySelect createInstance( UWindowCallbacks windowCallbacks,
                                             UIconCallbacks iconCallbacks,
                                             boolean isHome, WindowDir dir,
                                             int width, int height, int bgColor)
    {
        UIconWindowStudySelect instance = new UIconWindowStudySelect( windowCallbacks,
                iconCallbacks, isHome, dir, width, height, bgColor);

        return instance;
    }

    /**
     * Windowを生成する
     * インスタンス生成後に一度だけ呼ぶ
     */
    public void init() {
        if (type == WindowType.Home) {
            setIcons(TangoParentType.Home, 0);
        }
    }

    /**
     * Windowに表示するアイコンを設定する
     * どのアイコンを表示するかはどの親をもつアイコンを表示するかで指定する
     * @param parentType
     * @param parentId
     */
    public void setIcons(TangoParentType parentType, int parentId) {

        this.parentType = parentType;
        this.parentId= parentId;

        // DBからホームに表示するアイコンをロード
        List<TangoItem> items = RealmManager.getItemPosDao().selectItemsByParentType(
                parentType, parentId, TangoItemType.Book, true
        );
        // 今あるアイコンはクリアしておく
        mIconManager.getIcons().clear();

        for (TangoItem item : items) {
            mIconManager.addIcon(item, AddPos.Tail);
        }

        sortIcons(false);
    }

}
