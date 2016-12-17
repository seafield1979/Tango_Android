package com.sunsunsoft.shutaro.tangobook;

/**
 * Created by shutaro on 2016/12/17.
 */

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class Test1 {
    @Element
    public String name;
    @Element
    public String author;
    @Element
    public int price;
}
