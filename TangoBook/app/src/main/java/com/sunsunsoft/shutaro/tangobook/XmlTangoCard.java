package com.sunsunsoft.shutaro.tangobook;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by shutaro on 2016/12/17.
 *
 * xmlのカード情報
 * Simple-XMLで読み込んだ先を格納するフォーマット
 */

@Root
public class XmlTangoCard {
    @Element
    private String wordA;
    @Element
    private String wordB;
    @Element
    private String comment;

    public String getWordA() {
        return wordA;
    }

    public String getWordB() {
        return wordB;
    }

    public String getComment() {
        return comment;
    }
}
