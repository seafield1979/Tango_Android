package com.sunsunsoft.shutaro.tangobook.backup;

/**
 * Created by shutaro on 2017/07/04.
 *
 * バックアップ関連の例外
 */

public class BackupFileException extends Exception {

    /**
     * Enums
     */
    public enum BackupError {
        None,
        FileIsNotTangoApp,      // バックアップファイルが単語帳アプリのものではない
    }

    /**
     * Member variables
     */
    private BackupError mException;

    /**
     * Methods
     */
    public BackupError getException() {
        return mException;
    }


    /**
     * Constructor
     */
    public BackupFileException(String str) {
        super("MyException:" + str);
        mException = BackupError.None;
    }
    public BackupFileException(String str, BackupError exception) {
        super("MyException:" + str);
        mException = exception;
    }
}
