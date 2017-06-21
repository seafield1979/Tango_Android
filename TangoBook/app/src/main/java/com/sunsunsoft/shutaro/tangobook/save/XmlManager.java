package com.sunsunsoft.shutaro.tangobook.save;

import android.content.Context;
import android.util.Log;

import com.sunsunsoft.shutaro.tangobook.database.BackupFileDao;
import com.sunsunsoft.shutaro.tangobook.util.ConvDateMode;
import com.sunsunsoft.shutaro.tangobook.util.FilePathType;
import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.util.UResourceManager;
import com.sunsunsoft.shutaro.tangobook.util.UUtil;
import com.sunsunsoft.shutaro.tangobook.database.RealmManager;
import com.sunsunsoft.shutaro.tangobook.database.TangoBook;
import com.sunsunsoft.shutaro.tangobook.database.TangoBookHistory;
import com.sunsunsoft.shutaro.tangobook.database.TangoCard;
import com.sunsunsoft.shutaro.tangobook.database.TangoCardHistory;
import com.sunsunsoft.shutaro.tangobook.database.TangoItemPos;
import com.sunsunsoft.shutaro.tangobook.database.TangoStudiedCard;
import com.sunsunsoft.shutaro.tangobook.util.ULog;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by shutaro on 2017/01/17.
 *
 * Xmlファイルに書き込み、読み込みを行うクラス
 */

public class XmlManager {
    /**
     * enum
     */

    /**
     * Consts
     */
    public static final String TAG = "XmlManager";

    // 手動バックアップファイル名
    public static final String ManualBackupFile = "tango_m%02d.xml";

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

    public static File getManualXmlFile(int slot) {
        File path = UUtil.getPath(getInstance().mContext, FilePathType.ExternalDocument);
        File file = new File(path, String.format(ManualBackupFile, slot));

        return file;
    }

    public static File getAutoXmlFile() {
        File path = UUtil.getPath(getInstance().mContext, FilePathType.ExternalDocument);
        File file = new File(path, String.format(ManualBackupFile, BackupFileDao.AUTO_BACKUP_ID));

        return file;
    }

    /**
     * バックアップファイルの情報を取得
     * @return XMLファイル情報（ファイルパス、カード数、単語帳数、更新日）
     *          null: 失敗
     */
    public static String getManualXmlInfo(int slot) {
        File file = getManualXmlFile(slot);

        if (file == null) { return null; }

        return getXmlInfo(file);
    }
    public static String getAutoXmlInfo() {
        File file = getAutoXmlFile();

        if (file == null) { return null; }

        return getXmlInfo(file);
    }

    public static String getXmlInfo(File file) {

        XmlTangoTop tangoTop = null;
        try {
            Serializer serializer = new Persister();
            tangoTop = serializer.read(XmlTangoTop.class, file);

            ULog.print(TAG, "ok");
        } catch (Exception e) {
            Log.e("tag", e.toString());
            return null;
        }

        String str =  UUtil.convDateFormat(tangoTop.updateDate, ConvDateMode.DateTime) + "\n" +
                UResourceManager.getStringById(R.string.filename) +
                " :  " + file.getName() + "\n" +
                UResourceManager.getStringById(R.string.card_count) +
                " :  " + tangoTop.cardNum + "\n" +
                UResourceManager.getStringById(R.string.book_count) +
                " :  " + tangoTop.bookNum + "\n";
        return str;
    }

    /**
     * マニュアルファイルに保存する
     * @param slot  xmlのスロット
     * @return
     */
    public static BackupFileInfo saveManualBackup(int slot) {
        File file = getManualXmlFile(slot);

        if (file == null) { return null; }

        return saveToXml(file);
    }
    public static BackupFileInfo saveAutoBackup() {
        File file = getAutoXmlFile();

        if (file == null) { return null; }

        BackupFileInfo backup = saveToXml(file);

        // データベース更新(BackupFile)
        RealmManager.getBackupFileDao().updateOne(BackupFileDao.AUTO_BACKUP_ID,
                backup.getFilePath(), backup.getBookNum(), backup.getCardNum());


        return backup;
    }

    /**
     * 指定したファイルにデータベースの情報を保存する
     * @param file 保存ファイル
     * @return バックアップ情報のBean
     */
    public static BackupFileInfo saveToXml(File file) {
        XmlTangoTop tangoTop = new XmlTangoTop();

        ULog.initSystemTime();

        // データベースから保存情報をかき集める
        // TangoCard
        List<TangoCard> cards = RealmManager.getCardDao().selectAll();
        LinkedList<Card> cards2 = new LinkedList<>();
        for (TangoCard card : cards) {
            Card saveCard = new Card(card.getId(),card.getWordA(), card.getWordB(),
                    card.getComment(), card.getCreateTime(), card.getColor(),
                    card.getStar(), card.isNewFlag());
            cards2.add(saveCard);
        }
        tangoTop.card = cards2;

        ULog.print(TAG, "point1");

        // TangoBook
        List<TangoBook> books = RealmManager.getBookDao().selectAll();
        LinkedList<Book> book2 = new LinkedList<>();
        for (TangoBook book : books) {
            Book saveBook = new Book(book.getId(), book.getName(), book.getComment(),
                    book.getColor(), book.getCreateTime(), book.isNewFlag());
            book2.add(saveBook);
        }
        tangoTop.book = book2;

        ULog.print(TAG, "point2");

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

        ULog.print(TAG, "point3");

        // TangoBookHistory
        List<TangoBookHistory> bookHistory = RealmManager.getBookHistoryDao().selectAll(false);
        LinkedList<BHistory> bookHistory2 = new LinkedList<>();
        for (TangoBookHistory history : bookHistory) {
            BHistory saveBHistory = new BHistory(history.getId(), history.getId(),
                    history.getOkNum(), history.getNgNum(),
                    history.getStudiedDateTime());
            bookHistory2.add(saveBHistory);
        }
        tangoTop.bHistory = bookHistory2;

        ULog.print(TAG, "point4");

        // TangoCardHistory
        List<TangoCardHistory> cardHistory = RealmManager.getCardHistoryDao().selectAll();
        LinkedList<CHistory> cardHistory2 = new LinkedList<>();
        for (TangoCardHistory history : cardHistory) {
            CHistory saveCHistory = new CHistory(history.getCardId(), history.getCorrectFlagNum(),
                    history.getCorrectFlags(), history.getStudiedDate());
            cardHistory2.add(saveCHistory);
        }
        tangoTop.cHistory = cardHistory2;

        ULog.print(TAG, "point5");

        // TangoStudiedCard
        List<TangoStudiedCard> studiedCard = RealmManager.getStudiedCardDao().selectAll();
        LinkedList<StudiedC> studiedCard2 = new LinkedList<>();
        for (TangoStudiedCard history : studiedCard) {
            StudiedC saveStudiedC = new StudiedC( history.getBookHistoryId(),
                    history.getCardId(), history.isOkFlag());
            studiedCard2.add(saveStudiedC);
        }
        tangoTop.studiedC = studiedCard2;

        ULog.print(TAG, "point6");

        // カード数
        singleton.mBackupCardNum = tangoTop.cardNum = cards.size();

        // 単語帳数
        singleton.mBackupBookNum = tangoTop.bookNum = books.size();

        // 最終更新日時
        tangoTop.updateDate = new Date();

        // ファイルに書き込む
        File path = UUtil.getPath(getInstance().mContext, FilePathType.ExternalDocument);
        File result = new File(path, file.getName());
        try {
            Serializer serializer = new Persister();
            serializer.write(tangoTop, result);
            ULog.print(TAG, "output:" + result.toString());
        } catch (Exception e) {
            ULog.print(TAG, e.toString());
            return null;
        }

        ULog.print(TAG, "point7");

        // バックアップテーブルに書き込む情報を設定する
        BackupFileInfo backupInfo = new BackupFileInfo(file.getName(), result.toString(), tangoTop.bookNum, tangoTop.cardNum);

        ULog.print(TAG, "point8");
        return backupInfo;
    }

    /**
     * 指定したxmlファイルから情報を取得し、システム(Realmデータベース)に保存する
     * @param file  復元元のバックアップファイル
     * @return
     */
    public static boolean loadXml(File file) {

        XmlTangoTop tangoTop = null;
        try {
            Serializer serializer = new Persister();
            tangoTop = serializer.read(XmlTangoTop.class, file);

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
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        RealmManager.getCardDao().addXmlCards(tangoTop.card, false);
        RealmManager.getBookDao().addXmlBooks(tangoTop.book, false);
        RealmManager.getItemPosDao().addXmlPos(tangoTop.itemPos, false);
        RealmManager.getBookHistoryDao().addXmlBook(tangoTop.bHistory, false);
        RealmManager.getCardHistoryDao().addXmlCard(tangoTop.cHistory, false);
        RealmManager.getStudiedCardDao().addXmlCard(tangoTop.studiedC, false);

        realm.commitTransaction();
        return true;
    }

    /**
     * xmlファイルを削除する
     * @param slot
     */
    public static boolean removeManualXml(int slot) {
        File file = getManualXmlFile(slot);
        if (file == null) {return false;}

        return removeXml(file);
    }
    public static boolean removeAutoXml() {
        File file = getAutoXmlFile();
        if (file == null) {return false;}

        return removeXml(file);
    }

    public static boolean removeXml(File file) {
        return file.delete();
    }
}
