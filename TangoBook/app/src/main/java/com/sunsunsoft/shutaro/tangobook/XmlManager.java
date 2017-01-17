package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by shutaro on 2017/01/17.
 *
 * Xmlファイルに書き込み、読み込みを行うクラス
 */

public class XmlManager {
    /**
     * enum
     */
    // ファイルの保存先の種類
    enum DirType{
        AppStorage,     // アプリの永続化ストレージ
        AppCache,       // アプリのキャッシュ（一時的に使用する）領域
        AppExternal,    // アプリの外部
        ExternalStorage,        // 外部ストレージ
        ExternalDocument,       // 外部ストレージ(共有ドキュメント)
        ExternalDownload,       // 外部ストレージ(共有ダウンロード)
    }

    /**
     * Consts
     */
    public static final String TAG = "XmlManager";

    /**
     * Member variables
     */
    private Context mContext;

    /**
     * Constructor
     */
    // Singletonオブジェクト
    private static XmlManager singleton;

    // Singletonオブジェクトを作成する
    public static XmlManager createInstance(Context context) {
        if (singleton == null ) {
            singleton = new XmlManager(context);
        }
        return singleton;
    }
    public static XmlManager getInstance() { return singleton; }

    private XmlManager(Context context) {
        mContext = context;
    }

    /**
     * Methods
     */
    public String[] getXmlFileList() {
        return null;
    }

    /**
     * 指定したファイルにデータベースの情報を保存する
     * @param filename
     * @return
     */
    public static boolean saveXml(String filename) {

        XmlTangoTop tangoTop = new XmlTangoTop();

        // データベースから保存情報をかき集める

        // TangoCard
        List<TangoCard> cards = RealmManager.getCardDao().selectAll();
        LinkedList<Card> cards2 = new LinkedList<>();
        for (TangoCard card : cards) {
            Card saveCard = new Card(card.getId(),card.getWordA(), card.getWordB(),
                    card.getComment(), card.getCreateTime(), card.getColor(),
                    card.getStar());
            cards2.add(saveCard);
        }
        tangoTop.card = cards2;

        // TangoBook
        List<TangoBook> books = RealmManager.getBookDao().selectAll();
        LinkedList<Book> book2 = new LinkedList<>();
        for (TangoBook book : books) {
            Book saveBook = new Book(book.getId(), book.getName(), book.getComment(),
                    book.getColor(), book.getCreateTime());
            book2.add(saveBook);
        }
        tangoTop.book = book2;

        // ItemPos
        List<TangoItemPos> itemPos = RealmManager.getItemPosDao().selectAll();
        LinkedList<Pos> itemPos2 = new LinkedList<>();
        for (TangoItemPos pos : itemPos) {
            Pos savePos = new Pos(pos.getParentType(),
                    pos.getParentId(), pos.getPos(), pos.getItemType(),
                    pos.getItemId());
            itemPos2.add(savePos);
        }
        tangoTop.itemPos = itemPos2;

        // ファイルに書き込む
        File path = getInstance().getPath(DirType.ExternalDocument);
        File result = new File(path, filename);
        try {
            Serializer serializer = new Persister();
            serializer.write(tangoTop, result);
            ULog.print(TAG, "output:" + result.toString());
        } catch (Exception e) {
            ULog.print(TAG, e.toString());
            return false;
        }

        return true;
    }

    /**
     * 指定したxmlファイルから情報を取得し、システム(Realmデータベース)に保存する
     * @param filename
     * @return
     */
    public static boolean loadXml(String filename) {
        File path = getInstance().getPath(DirType.ExternalDocument);
        File source = new File(path, filename);

        try {
            Serializer serializer = new Persister();
            XmlTangoTop tangoTop = serializer.read(XmlTangoTop.class, source);

            // データベースを削除
            RealmManager.getCardDao().deleteAll();
            RealmManager.getBookDao().deleteAll();
            RealmManager.getItemPosDao().deleteAll();

            // データベースにxmlファイルから読み込んだデータを追加
            RealmManager.getCardDao().addXmlCards(tangoTop.card);
            RealmManager.getBookDao().addXmlBooks(tangoTop.book);
            RealmManager.getItemPosDao().addXmlPos(tangoTop.itemPos);

            ULog.print(TAG, "ok");

        } catch (Exception e) {
            Log.e("tag", e.toString());
            return false;
        }
        return true;
    }

    /**
     *
     * @param dirType
     * @return
     */

    private File getPath(DirType dirType) {
        switch (dirType) {
            case AppStorage:
                return mContext.getFilesDir();
            case AppCache:
                return mContext.getCacheDir();
            case AppExternal:
            {
//                File[] dirs = mContext.getExternalFilesDirs(null);
//                StringBuffer buf = new StringBuffer();
//                if (dirs != null && dirs.length > 0) {
//                    return dirs[0];
//                }
            }
            case ExternalStorage:
                return Environment.getExternalStorageDirectory();
            case ExternalDocument:
                return Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOCUMENTS);
            case ExternalDownload:
                return Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS);
        }
        return null;
    }
}
