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
public class EMRMetaDataDO implements  Serializable{
   private static final long serialVersionUID = 3105194809252351987L;
    public EMRMetaDataDO() {
    }
    private String dataCode;
    private String dataTitle;
    private String dataDefine;
    private boolean isLeaf;

    public String getDataCode() {
                return this.dataCode;
    }

    public void setDataCode(String dataCode) {
            this.dataCode = dataCode;
    }

    public String getDataTitle() {
            return this.dataTitle;
    }

    public void setDataTitle(String dataTitle) {
            this.dataTitle = dataTitle;
    }

    public String getDataDefine() {
        return this.dataDefine;
    }

    public void setDataDefine(String dataDefine) {
        this.dataDefine = dataDefine;
    }

    public boolean isLeaf() {
            return isLeaf;
    }
    public void setLeaf(boolean isLeaf) {
            this.isLeaf = isLeaf;
    }






}
