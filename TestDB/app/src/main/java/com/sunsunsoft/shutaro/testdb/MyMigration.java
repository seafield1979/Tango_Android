package com.sunsunsoft.shutaro.testdb;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * データベースのモデルが変更された時の処理
 *
 * https://github.com/realm/realm-java/blob/master/examples/migrationExample/src/main/java/io/realm/examples/realmmigrationexample/model/Migration.java
 */

public class MyMigration implements RealmMigration{
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
//        Realm realm = RealmManager.getRealm();
        RealmSchema schema = realm.getSchema();

        // Migrate from version 0 to version 1
        if (oldVersion == 0) {
            // マイグレーション処理
            oldVersion = 1;
        }

        // Migrate from version 1 to version 2
        if (oldVersion == RealmManager.Version1) {
            // マイグレーション処理

            // Create a new class
            schema.create("TangoBox")
                    .addField("id", Integer.class, FieldAttribute.PRIMARY_KEY)
                    .addField("name", String.class, FieldAttribute.REQUIRED)
                    .addField("comment", String.class, FieldAttribute.REQUIRED)
                    .addField("color", Integer.class, FieldAttribute.REQUIRED)
                    .addField("createTime", Date.class, FieldAttribute.REQUIRED)
                    .addField("updateTime", Date.class, FieldAttribute.REQUIRED);

            oldVersion = RealmManager.Version2;
        }

        if (oldVersion == RealmManager.Version2) {
            // 間違って追加した TangoBox の studyTime カラムを削除
            schema.get("TangoBox")
                    .removeField("studyTime");

            oldVersion = RealmManager.Version21;
        }

        if (oldVersion == RealmManager.Version21) {
            // TangoBoxのidにプライマリーキーを追加する
            RealmObjectSchema boxSchema = schema.get("TangoBox");
            if(!boxSchema.hasPrimaryKey()) boxSchema.addPrimaryKey("id");

            oldVersion = RealmManager.Version22;
        }

        if (oldVersion == RealmManager.Version22) {
            // TangoBoxのStringフィールドにnullable属性を追加
            RealmObjectSchema boxSchema = schema.get("TangoBox");

            boxSchema.setNullable("name", true);
            boxSchema.setNullable("comment", true);
            boxSchema.setNullable("createTime", true);
            boxSchema.setNullable("updateTime", true);

            oldVersion = RealmManager.Version23;
        }

        if (oldVersion == RealmManager.Version23) {
            // TangoCardInBook テーブル追加
            schema.create("TangoCardInBook")
                    .addField("bookId", Integer.class)
                    .addField("cardId", String.class);

            oldVersion = RealmManager.Version30;
        }

        if (oldVersion == RealmManager.Version30) {
            // TangoCardInBook テーブル追加
            schema.create("TangoItemPos")
                    .addField("pos", Integer.class)
                    .addField("itemType", Integer.class)
                    .addField("id", Integer.class);

            oldVersion = RealmManager.Version40;
        }
    }
}