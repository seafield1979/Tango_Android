package com.sunsunsoft.shutaro.tangobook.save;

import android.content.Context;
import android.util.Log;

import com.sunsunsoft.shutaro.tangobook.R;
import com.sunsunsoft.shutaro.tangobook.database.*;
import com.sunsunsoft.shutaro.tangobook.save.saveitem.SaveBook;
import com.sunsunsoft.shutaro.tangobook.save.saveitem.SaveBookHistory;
import com.sunsunsoft.shutaro.tangobook.save.saveitem.SaveCard;
import com.sunsunsoft.shutaro.tangobook.save.saveitem.SaveCardHistory;
import com.sunsunsoft.shutaro.tangobook.save.saveitem.SaveItemPos;
import com.sunsunsoft.shutaro.tangobook.save.saveitem.SaveStudiedCard;
import com.sunsunsoft.shutaro.tangobook.util.*;


import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import io.realm.Realm;

/**
 * Created by shutaro on 2017/06/26.
 *
 * ファイルにバックアップを行うクラス
 * ファイルにバックアップと、バックアップファイルからの復元を行う
 */

public class BackupManager {
    /**
     * enum
     */
    // スレッド処理モード
    public enum RunMode {
        None,
        BackupAuto,
        BackupManual
    }

    /**
     * Consts
     */
    public static final String TAG = "BackupManager";

    // 手動バックアップファイル名
    public static final String ManualBackupFile = "tango_m%02d.bin";

    private static final int WRITE_BUF_SIZE = 1000; // 書き込みバッファー

    /**
     * Member variables
     */
    private Context mContext;

    private XmlManager.RunMode mRunMode;               // スレッド実行モード
    private XmlBackupCallbacks mCallbacks;  // バックアップ完了のコールバック
    private int mSaveSlot;                  // マニュアルバックアップのスロット番号

    // バックアップ情報
    private int mBackupCardNum;
    private int mBackupBookNum;

    // 書き込み情報を一時的に溜め込むバッファー
    private ByteBuffer mBuf;


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
    private static BackupManager singleton;

    // Singletonオブジェクトを作成する
    public static BackupManager createInstance(Context context) {
        if (singleton == null ) {
            singleton = new BackupManager(context);
        }
        return singleton;
    }
    public static BackupManager getInstance() { return singleton; }

    private BackupManager(Context context) {
        mContext = context;
        mBuf = ByteBuffer.allocate(WRITE_BUF_SIZE);
    }

    /**
     * Methods
     */
    /**
     * オートバックアップをスレッドで実行
     * @param callbacks
     * @param context
     */
//    public static void startBackupAuto(XmlBackupCallbacks callbacks, Context context) {
//        XmlManager runable = new XmlManager(context);
//        runable.mCallbacks = callbacks;
//        runable.mRunMode = XmlManager.RunMode.BackupAuto;
//        Thread thread = new Thread(runable);
//        thread.start();
//    }

    /**
     * マニュアルバックアップをスレッドで実行
     * @param callbacks
     * @param context
     */
//    public static void startBackupManual(XmlBackupCallbacks callbacks, Context context, int slot) {
//        BackupManager runable = new BackupManager(context);
//        runable.mCallbacks = callbacks;
//        runable.mRunMode = XmlManager.RunMode.BackupAuto;
//        runable.mSaveSlot = slot;
//        Thread thread = new Thread(runable);
//        thread.start();
//    }

    /**
     * スレッド処理
     */
//    public void run() {
//        switch(mRunMode) {
//            case BackupAuto: {
//                BackupFileInfo fileInfo = saveAutoBackup();
//                if (mCallbacks != null) {
//                    mCallbacks.finishBackup(fileInfo);
//                }
//            }
//            break;
//            case BackupManual: {
//                BackupFileInfo fileInfo = saveManualBackup(mSaveSlot);
//                if (mCallbacks != null) {
//                    mCallbacks.finishBackup(fileInfo);
//                }
//            }
//            break;
//        }
//    }

    public String[] getXmlFileList() {
        return null;
    }

    public static File getManualBackupFile(int slot) {
        File path = UUtil.getPath(getInstance().mContext, FilePathType.ExternalDocument);
        File file = new File(path, String.format(ManualBackupFile, slot));

        return file;
    }

    public static File getAutoBackupFile() {
        File path = UUtil.getPath(getInstance().mContext, FilePathType.ExternalDocument);
        File file = new File(path, String.format(ManualBackupFile, BackupFileDao.AUTO_BACKUP_ID));

        return file;
    }

    /**
     * バックアップファイルの情報を取得
     * @return XMLファイル情報（ファイルパス、カード数、単語帳数、更新日）
     *          null: 失敗
     */
    public static String getManualBackupInfo(int slot) {
        File file = getManualBackupFile(slot);

        if (file == null) { return null; }

        return getBackupInfo(file);
    }
    public static String getAutoBackupInfo() {
        File file = getAutoBackupFile();

        if (file == null) { return null; }

        return getBackupInfo(file);
    }

    /**
     * バックアップの情報を取得する
     * @param file バックアップファイルから情報を取得
     * @return
     */
    public static String getBackupInfo(File file) {

        BackupFileInfo backupInfo = null;
        try {

            BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));

            // ヘッダ部分のみ読み込む
            final int readSize = 4 + 4 + 4 + 7;
            ByteBuffer byteBuf = ByteBuffer.allocate(readSize);
            input.read(byteBuf.array(), 0, readSize);

            // header
            int version = byteBuf.getInt();     // シーク用に必要
            int cardNum = byteBuf.getInt();
            int bookNum = byteBuf.getInt();
            Date createdDate = readDate(byteBuf);

            backupInfo = new BackupFileInfo(file.getName(), file.getPath(), createdDate, bookNum, cardNum);
         } catch (Exception e) {
            Log.e("tag", e.toString());
            return null;
        }

        String str =  UUtil.convDateFormat(backupInfo.getBackupDate(),
                ConvDateMode.DateTime) + "\n" +
                UResourceManager.getStringById(R.string.filename) +
                " :  " + file.getName() + "\n" +
                UResourceManager.getStringById(R.string.card_count) +
                " :  " + backupInfo.getCardNum() + "\n" +
                UResourceManager.getStringById(R.string.book_count) +
                " :  " + backupInfo.getBookNum() + "\n";
        return str;
    }

    /**
     * バックアップの情報を取得する
     * BackupFileInfoからバックアップ情報を取得
     * @param backupInfo
     * @return
     */
    public static String getBackupInfo(BackupFileInfo backupInfo) {
        if (backupInfo == null) {
            return null;
        }
        String str =  UUtil.convDateFormat(new Date(), ConvDateMode.DateTime) + "\n" +
                UResourceManager.getStringById(R.string.filename) +
                " :  " + backupInfo.getFileName() + "\n" +
                UResourceManager.getStringById(R.string.card_count) +
                " :  " + backupInfo.getCardNum() + "\n" +
                UResourceManager.getStringById(R.string.book_count) +
                " :  " + backupInfo.getBookNum() + "\n";
        return str;
    }

    /**
     * バックアップの情報を取得する
     * @param slot バックアップファイルのスロット番号
     * @return
     */
    public static String getBackupInfo(int slot) {

        BackupFile backupFile = null;
        try {
            backupFile = RealmManager.getBackupFileDao().selectById(slot);
        } catch (Exception e) {
            Log.e("tag", e.toString());
            return null;
        }

        String filename = new File(backupFile.getFilePath()).getName();

        String str =  UUtil.convDateFormat(backupFile.getDateTime(),
                ConvDateMode.DateTime) + "\n" +
                UResourceManager.getStringById(R.string.filename) +
                " :  " + filename + "\n" +
                UResourceManager.getStringById(R.string.card_count) +
                " :  " + backupFile.getCardNum() + "\n" +
                UResourceManager.getStringById(R.string.book_count) +
                " :  " + backupFile.getBookNum() + "\n";
        return str;
    }

    /**
     * マニュアルファイルに保存する
     * @param slot  backupのスロット
     * @return
     */
    public BackupFileInfo saveManualBackup(int slot) {
        File file = getManualBackupFile(slot);

        if (file == null) { return null; }

        return saveToFile(file);
    }
    public BackupFileInfo saveAutoBackup() {
        File file = getAutoBackupFile();

        if (file == null) { return null; }

        BackupFileInfo backup = saveToFile(file);

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
    public BackupFileInfo saveToFile(File file) {
        BackupData backupData = new BackupData();

        ULog.initSystemTime();

        // データベースから保存情報をかき集める
        // TangoCard
        backupData.cards = RealmManager.getCardDao().selectAll();

        ULog.print(TAG, "point1");

        // TangoBook
        backupData.books = RealmManager.getBookDao().selectAll();

        ULog.print(TAG, "point2");

        // ItemPos
        backupData.itemPoses = RealmManager.getItemPosDao().selectAll();

        ULog.print(TAG, "point3");

        // TangoBookHistory
        backupData.bookHistories = RealmManager.getBookHistoryDao().selectAll(false);

        ULog.print(TAG, "point4");

        // TangoCardHistory
        backupData.cardHistories = RealmManager.getCardHistoryDao().selectAll();

        ULog.print(TAG, "point5");

        // TangoStudiedCard
        backupData.studiedCards = RealmManager.getStudiedCardDao().selectAll();

        ULog.print(TAG, "point6");

        // カード数
        singleton.mBackupCardNum = backupData.cardNum = backupData.cards.size();

        // 単語帳数
        singleton.mBackupBookNum = backupData.bookNum = backupData.books.size();

        // 最終更新日時
        backupData.updateDate = new Date();

        // ファイルに書き込む
        BackupFileInfo backupInfo = null;
        try {
            File path = UUtil.getPath(getInstance().mContext, FilePathType.ExternalDocument);
            if (path.exists() == false) {
                // フォルダがなかったら作成する
                if (path.mkdir() == false) {
                    throw new Exception("Couldn't create external document directory.");
                }
            }

            int cardNum = (backupData.cards != null) ? backupData.cards.size() : 0;
            int bookNum = (backupData.books != null) ? backupData.books.size() : 0;

            backupInfo = new BackupFileInfo(file.getName(), path.getPath(), backupData.updateDate, bookNum, cardNum);

            writeToFile(file, backupData);

        } catch (Exception e) {
            ULog.print(TAG, e.toString());
            return null;
        }

        ULog.print(TAG, "point7");
        return backupInfo;
    }

    /**
     * バックアップファイルに書き込む
     * @param backupData
     * @return
     */
    private BackupFileInfo writeToFile(File file, BackupData backupData) {
        // 書き込み用のファイルを開く
        BufferedOutputStream output = null;
        try {
            output = new BufferedOutputStream(new FileOutputStream(file));

            mBuf.clear();

            // version
            mBuf.putInt(backupData.version);

            // Number of card
            mBuf.putInt(backupData.cardNum);

            // Number of book
            mBuf.putInt(backupData.bookNum);

            // last update date
            writeDate(mBuf, backupData.updateDate);

            output.write(mBuf.array(), 0, mBuf.position());

            /**
             * Database
             */
            //---------------
            // card
            //---------------
            mBuf.clear();
            // num
            mBuf.putInt(backupData.cardNum);
            output.write(mBuf.array(), 0, 4);
            // data
            SaveCard saveCard = new SaveCard(mBuf);
            for (TangoCard card : backupData.cards) {
                saveCard.writeData(output, card);
            }
            //---------------
            // book
            //---------------
            mBuf.clear();
            SaveBook saveBook = new SaveBook(mBuf);
            // num
            mBuf.putInt(backupData.bookNum);
            output.write(mBuf.array(), 0, 4);
            // data
            for (TangoBook book : backupData.books) {
                saveBook.writeData(output, book);
            }

            //---------------
            // card&book position
            //---------------
            mBuf.clear();
            // num
            mBuf.putInt(backupData.itemPoses.size());
            output.write(mBuf.array(), 0, 4);
            // data
            SaveItemPos savePos = new SaveItemPos(mBuf);
            for (TangoItemPos pos : backupData.itemPoses) {
                savePos.writeData(output, pos);
            }

            //---------------
            // 学習した単語帳履歴(1学習1履歴)
            //---------------
            mBuf.clear();
            // num
            mBuf.putInt(backupData.bookHistories.size());
            output.write(mBuf.array(), 0, 4);
            // data
            SaveBookHistory saveBookHistory = new SaveBookHistory(mBuf);
            for (TangoBookHistory history : backupData.bookHistories) {
                saveBookHistory.writeData(output, history);
            }

            //---------------
            // 学習カード(1枚学習するたびに1つ)
            //---------------
            mBuf.clear();
            // num
            mBuf.putInt(backupData.studiedCards.size());
            output.write(mBuf.array(), 0, 4);
            // data
            SaveStudiedCard saveStudiedCard = new SaveStudiedCard(mBuf);
            for (TangoStudiedCard card : backupData.studiedCards) {
                saveStudiedCard.writeData(output, card);
            }

            //---------------
            // 学習カード履歴(1カード1履歴)
            //---------------
            mBuf.clear();
            // num
            mBuf.putInt(backupData.cardHistories.size());
            output.write(mBuf.array(), 0, 4);
            // data
            SaveCardHistory saveCardHistory = new SaveCardHistory(mBuf);
            for (TangoCardHistory history : backupData.cardHistories) {
                saveCardHistory.writeData(output, history);
            }

            if (output != null) {
                output.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    /**
     * バックアップファイルから情報を読み込む
     * @return file バックアプファイル
     */
    private BackupLoadData readFromFile(File file) {
        BufferedInputStream input;
        BackupLoadData backup = new BackupLoadData();
        try {
            input = new BufferedInputStream(new FileInputStream(file));

            // ファイルのデータを全て読み込む
            int fileSize = (int)file.length();
            ByteBuffer byteBuf = ByteBuffer.allocate(fileSize);
            input.read(byteBuf.array(), 0, fileSize);

            // header
            backup.version = byteBuf.getInt();
            backup.cardNum = byteBuf.getInt();
            backup.bookNum = byteBuf.getInt();
            backup.updateDate = readDate(byteBuf);

            // card
            int cardNum = byteBuf.getInt();
            backup.cards = new LinkedList<>();
            SaveCard saveCard = new SaveCard(mBuf);
            for (int i=0; i<cardNum; i++) {
                backup.cards.add(saveCard.readData(byteBuf));
            }
            ULog.print(TAG, "cardNum:" + cardNum);

            // book
            int bookNum = byteBuf.getInt();
            backup.books = new LinkedList<>();
            SaveBook saveBook = new SaveBook(mBuf);
            for (int i=0; i<bookNum; i++) {
                backup.books.add(saveBook.readData(byteBuf));
            }
            ULog.print(TAG, "bookNum:" + bookNum);

            // position
            int posNum = byteBuf.getInt();
            backup.itemPoses = new LinkedList<>();
            SaveItemPos saveItemPos = new SaveItemPos(mBuf);
            for (int i=0; i<posNum; i++) {
                backup.itemPoses.add(saveItemPos.readData(byteBuf));
            }
            ULog.print(TAG, "posNum:" + posNum);

            //　book history
            int bookHistoriesNum = byteBuf.getInt();
            backup.bookHistories = new LinkedList<>();
            SaveBookHistory saveBookHistory = new SaveBookHistory(mBuf);
            for (int i=0; i<bookHistoriesNum; i++) {
                backup.bookHistories.add(saveBookHistory.readData(byteBuf));
            }
            ULog.print(TAG, "bookHistoriesNum:" + bookHistoriesNum);

            // studied card
            int studiedCardNum = byteBuf.getInt();
            backup.studiedCards = new LinkedList<>();
            SaveStudiedCard saveStudiedCard = new SaveStudiedCard(mBuf);
            for (int i=0; i<studiedCardNum; i++) {
                backup.studiedCards.add(saveStudiedCard.readData(byteBuf));
            }
            ULog.print(TAG, "studiedCardNum:" + studiedCardNum);

            // card history
            int cardHistoriesNum = byteBuf.getInt();
            backup.cardHistories = new LinkedList<>();
            SaveCardHistory saveCardHistory = new SaveCardHistory(mBuf);
            for (int i=0; i<cardHistoriesNum; i++) {
                backup.cardHistories.add(saveCardHistory.readData(byteBuf));
            }
            ULog.print(TAG, "cardHistoriesNum:" + cardHistoriesNum);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return backup;
    }

    /**
     * 指定したファイルから情報を取得し、システム(Realmデータベース)に保存する
     * @param file  復元元のバックアップファイル
     * @return
     */
    public boolean loadBackup(File file) {

        BackupLoadData backupData = null;
        try {
            backupData = readFromFile(file);

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

        RealmManager.getCardDao().addXmlCards(backupData.cards, false);
        RealmManager.getBookDao().addXmlBooks(backupData.books, false);
        RealmManager.getItemPosDao().addXmlPos(backupData.itemPoses, false);
        RealmManager.getBookHistoryDao().addXmlBook(backupData.bookHistories, false);
        RealmManager.getCardHistoryDao().addXmlCard(backupData.cardHistories, false);
        RealmManager.getStudiedCardDao().addXmlCard(backupData.studiedCards, false);

        realm.commitTransaction();
        return true;
    }

    /**
     * xmlファイルを削除する
     * @param slot
     */
    public static boolean removeManualXml(int slot) {
        File file = getManualBackupFile(slot);
        if (file == null) {return false;}

        return removeXml(file);
    }
    public static boolean removeAutoXml() {
        File file = getAutoBackupFile();
        if (file == null) {return false;}

        return removeXml(file);
    }

    public static boolean removeXml(File file) {
        return file.delete();
    }


    /**
     * Static Methods
     */
    /**
     * Date型のデータをバイナリ形式で書き込む
     * @param buf      書き込み先のバッファー
     * @param date     書き込む日付情報
     */
    public static void writeDate(ByteBuffer buf, Date date) throws IOException {
        if (date == null) {
            // 全て0で書き込み
            buf.put(new byte[7], 0, 7);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            buf.putShort((short)calendar.get(Calendar.YEAR));
            buf.put((byte)calendar.get(Calendar.MONTH));
            buf.put((byte)calendar.get(Calendar.DAY_OF_MONTH));
            buf.put((byte)calendar.get(Calendar.HOUR));
            buf.put((byte)calendar.get(Calendar.MINUTE));
            buf.put((byte)calendar.get(Calendar.SECOND));
        }
    }

    /**
     * バイナリ形式のDateデータを読み込む
     */
    public static Date readDate(ByteBuffer buf) throws IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, buf.getShort());
        calendar.set(Calendar.MONTH, buf.get());
        calendar.set(Calendar.DAY_OF_MONTH, buf.get());
        calendar.set(Calendar.HOUR, buf.get());
        calendar.set(Calendar.MINUTE, buf.get());
        calendar.set(Calendar.SECOND, buf.get());

        return calendar.getTime();
    }

    /**
     * 文字列を書き込む
     * @param str
     * @throws IOException
     */
    public static void writeString(ByteBuffer buf, String str) throws IOException {
        if (str == null || str.length() == 0) {
            buf.putInt(0);
        } else {
            byte[] bytes = str.getBytes();
            buf.putInt(bytes.length);
            buf.put(bytes);
        }
    }

    /**
     * 文字列を読み込む
     * @return 読み込んだ文字列
     */
    public String readString(ByteBuffer buf) throws IOException {
        int strLen = buf.getInt();
        byte[] bytes = new byte[strLen];
        buf.get(bytes, 0, strLen);
        return new String(bytes);
    }
}
