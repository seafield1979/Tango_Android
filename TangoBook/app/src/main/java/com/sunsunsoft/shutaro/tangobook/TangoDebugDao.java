package com.sunsunsoft.shutaro.tangobook;

import io.realm.Realm;

/**
 * データベースのテスト用Dao
 */

public class TangoDebugDao {
    public static final String TAG = "TangoCardDao";

    private Realm mRealm;

    public TangoDebugDao(Realm realm) {
        mRealm = realm;
    }
}
