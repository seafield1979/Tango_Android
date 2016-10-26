package com.sunsunsoft.shutaro.testdb;

/**
 * Created by shutaro on 2016/10/26.
 */

/**
 * contactテーブルのデータ
 */
public class ContactData {
    int _id;
    String name;
    int age;
    boolean isChecked;

    public ContactData(int id, String name, int age) {
        _id = id;
        this.name = name;
        this.age = age;
        isChecked = false;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getText() {
        return "id:" + _id + " name: " + name + " age:" + age;
    }
}
