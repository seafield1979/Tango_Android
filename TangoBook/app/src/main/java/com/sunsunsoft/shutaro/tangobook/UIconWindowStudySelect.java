package com.sunsunsoft.shutaro.tangobook;

import android.view.View;

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
    public UIconWindowStudySelect(View parent, UWindowCallbacks windowCallbacks,
                                  UIconCallbacks iconCallbacks,
                                  boolean isHome, WindowDir dir,
                                  int width, int height, int bgColor) {
        super(parent, windowCallbacks,
                iconCallbacks, isHome, dir, width, height, bgColor);
    }

    /**
     * Create class instance
     * It doesn't allow to create multi Home windows.
     * @return
     */
    public static UIconWindowStudySelect createInstance(View parent, UWindowCallbacks windowCallbacks,
                                             UIconCallbacks iconCallbacks,
                                             boolean isHome, WindowDir dir,
                                             int width, int height, int bgColor)
    {
        UIconWindowStudySelect instance = new UIconWindowStudySelect(parent, windowCallbacks,
                iconCallbacks, isHome, dir, width, height, bgColor);


        // 描画はDrawManagerに任せるのでDrawManagerに登録
        instance.drawList = UDrawManager.getInstance().addDrawable(instance);

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
