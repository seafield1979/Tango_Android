package com.sunsunsoft.shutaro.testdb;

/**
 * Created by shutaro on 2016/10/26.
 */

/**
 * contactテーブルのデータ
 */
public class ContactData {
    String name;
    int age;
    boolean isChecked;

    public ContactData(String name, int age) {
        this.name = name;
        this.age = age;
        isChecked = false;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getText() {
        return "name: " + name + " age:" + age;
    }
}
