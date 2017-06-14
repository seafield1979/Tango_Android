package com.sunsunsoft.shutaro.tangobook.util;

/**
 * Created by shutaro on 2017/06/14.
 * ファイルの保存先の種類
 */
public enum FilePathType {
    AppStorage,     // アプリの永続化ストレージ
    AppCache,       // アプリのキャッシュ（一時的に使用する）領域
    AppExternal,    // アプリの外部
    ExternalStorage,        // 外部ストレージ
    ExternalDocument,       // 外部ストレージ(共有ドキュメント)
    ExternalDownload,       // 外部ストレージ(共有ダウンロード)
}
