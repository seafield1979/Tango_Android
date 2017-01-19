package com.sunsunsoft.shutaro.tangobook;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.Date;
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

    // バックアップファイルの種類
    enum BackupFileType {
        ManualBackup,
        AutoBackup
    }

    /**
     * Consts
     */
    public static final String TAG = "XmlManager";

    // 手動バックアップファイル名
    public static final String ManualBackupFile = "tango_m.xml";

    // 自動バックアップファイル名
    public static final String AutoBackupFile = "tango_a.xml";

    /**
     * Member variables
     */
    private Context mContext;

    // バックアップ情報
    private int mBackupCardNum;
    private int mBackupBookNum;

    /**
     * Get/Set
     */
    public int getBackpuCardNum() {
        return mBackupCardNum;
    }
    public int getBackupBookNum() {
        return mBackupBookNum;
    }

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
     * バックアップファイルの情報を取得
     * @return XMLファイル情報（ファイルパス、カード数、単語帳数、更新日）
     */
    public static String getXmlInfo(BackupFileType type) {
        String filename = (type == BackupFileType.ManualBackup) ? ManualBackupFile : AutoBackupFile;

        // 指定の場所にファイルがあるかをチェック
        File path = getInstance().getPath(DirType.ExternalDocument);
        File source = new File(path, filename);

        XmlTangoTop tangoTop = null;
        try {
            Serializer serializer = new Persister();
            tangoTop = serializer.read(XmlTangoTop.class, source);

            ULog.print(TAG, "ok");
        } catch (Exception e) {
            Log.e("tag", e.toString());
            return null;
        }

        String str =  UResourceManager.getStringById(R.string.card_count) +
                ":" + tangoTop.cardNum +
                "   " + UResourceManager.getStringById(R.string.book_count) +
                ":" + tangoTop.bookNum + "\n" +
                UResourceManager.getStringById(R.string.location) +
                source.toString() + "\n" +
                UResourceManager.getStringById(R.string.datetime) +
                " : " + UUtil.convDateFormat(tangoTop.updateDate, ConvDateMode.DateTime);
        return str;
    }

    /**
     * 指定したファイルにデータベースの情報を保存する
     * @param type
     * @return ファイルのパス
     */
    public static String saveXml(BackupFileType type) {
        String filename = (type == BackupFileType.ManualBackup) ? ManualBackupFile : AutoBackupFile;

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

        // TangoBookHistory
        List<TangoBookHistory> bookHistory = RealmManager.getBookHistoryDao().selectAll(false);
        LinkedList<BHistory> bookHistory2 = new LinkedList<>();
        for (TangoBookHistory history : bookHistory) {
            BHistory saveBHistory = new BHistory(history.getId(), history.getId(),
                    history.getOkNum(), history.getNgNum(),
                    history.getCorrectRatio(), history.getStudiedDateTime());
            bookHistory2.add(saveBHistory);
        }
        tangoTop.bHistory = bookHistory2;

        // TangoCardHistory
        List<TangoCardHistory> cardHistory = RealmManager.getCardHistoryDao().selectAll();
        LinkedList<CHistory> cardHistory2 = new LinkedList<>();
        for (TangoCardHistory history : cardHistory) {
            CHistory saveCHistory = new CHistory(history.getCardId(), history.getCorrectFlagNum(),
                    history.getCorrectFlags(), history.getStudiedDate());
            cardHistory2.add(saveCHistory);
        }
        tangoTop.cHistory = cardHistory2;

        // TangoStudiedCard
        List<TangoStudiedCard> studiedCard = RealmManager.getStudiedCardDao().selectAll();
        LinkedList<StudiedC> studiedCard2 = new LinkedList<>();
        for (TangoStudiedCard history : studiedCard) {
            StudiedC saveStudiedC = new StudiedC( history.getBookHistoryId(),
                    history.getCardId(), history.isOkFlag());
            studiedCard2.add(saveStudiedC);
        }
        tangoTop.studiedC = studiedCard2;

        // カード数
        singleton.mBackupCardNum = tangoTop.cardNum = cards.size();

        // 単語帳数
        singleton.mBackupBookNum = tangoTop.bookNum = books.size();

        // 最終更新日時
        tangoTop.updateDate = new Date();

        // ファイルに書き込む
        File path = getInstance().getPath(DirType.ExternalDocument);
        File result = new File(path, filename);
        try {
            Serializer serializer = new Persister();
            serializer.write(tangoTop, result);
            ULog.print(TAG, "output:" + result.toString());
        } catch (Exception e) {
            ULog.print(TAG, e.toString());
            return null;
        }

        return result.toString();
    }

    /**
     * 指定したxmlファイルから情報を取得し、システム(Realmデータベース)に保存する
     * @param type  バックアップファイルのタイプ(手動、自動)
     * @return
     */
    public static boolean loadXml(BackupFileType type) {
        String filename = (type == BackupFileType.ManualBackup) ? ManualBackupFile : AutoBackupFile;

        File path = getInstance().getPath(DirType.ExternalDocument);
        File source = new File(path, filename);

        XmlTangoTop tangoTop = null;
        try {
            Serializer serializer = new Persister();
            tangoTop = serializer.read(XmlTangoTop.class, source);

            ULog.print(TAG, "ok");

        } catch (Exception e) {
            Log.e("tag", e.toString());
            return false;
        }

        // データベースを削除
        RealmManager.getCardDao().deleteAll();
        RealmManager.getBookDao().deleteAll();
        RealmManager.getItemPosDao().deleteAll();
        RealmManager.getBookHistoryDao().deleteAll();
        RealmManager.getCardHistoryDao().deleteAll();
        RealmManager.getStudiedCardDao().deleteAll();

        // データベースにxmlファイルから読み込んだデータを追加
        RealmManager.getCardDao().addXmlCards(tangoTop.card);
        RealmManager.getBookDao().addXmlBooks(tangoTop.book);
        RealmManager.getItemPosDao().addXmlPos(tangoTop.itemPos);
        RealmManager.getBookHistoryDao().addXmlBook(tangoTop.bHistory);
        RealmManager.getCardHistoryDao().addXmlCard(tangoTop.cHistory);
        RealmManager.getStudiedCardDao().addXmlCard(tangoTop.studiedC);

        return true;
    }

    /**
     * xmlファイルを削除する
     * @param type
     */
    public static void removeXml(BackupFileType type) {
        String filename = (type == BackupFileType.ManualBackup) ? ManualBackupFile : AutoBackupFile;

        File path = getInstance().getPath(DirType.ExternalDocument);
        path = new File(path, filename);
        path.delete();
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
