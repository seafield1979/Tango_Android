package com.sunsunsoft.shutaro.tangobook;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by shutaro on 2016/12/17.
 *
 * xmlの単語帳
 * Simple-XMLで読み込んだ先を格納するフォーマット
 */

@Root
public class XmlTangoBook {
    @ElementList
    private List<XmlTangoCard> cards;

    @Attribute
    private String name;

    @Element
    private String comment;

    public String getName() {
        return name;
    }

    public List<XmlTangoCard> getCards() {
        return cards;
    }

    public String getComment() {
        return comment;
    }
}

