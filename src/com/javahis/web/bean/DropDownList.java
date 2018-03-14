package com.javahis.web.bean;

import java.io.Serializable;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DropDownList implements  Serializable{
    private static final long serialVersionUID = -5926013594891430991L;
    public DropDownList() {
    }

    private String value;
    private String title;

    public String getValue() {
            return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTitle() {
            return this.title;
    }
    public void setTitle(String title) {
            this.title = title;
    }


}
