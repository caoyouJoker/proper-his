package com.javahis.web.bean;

import java.io.Serializable;

/**
 * <p>Title: 全文检索结果类</p>
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
public class EMRSearchResultVO implements  Serializable{
    public EMRSearchResultVO() {
    }
    private String fileName;
    private String desc;
    //
    private String filePath;
    //CASE NO;
    private String caseNo;
    //文件序号
    private String fileSeq;

    private String pdfFilePath;

    private String htmlFilePath;


    public String getCaseNo() {
            return caseNo;
    }

    public void setCaseNo(String caseNo) {
            this.caseNo = caseNo;
    }

    public String getFileSeq() {
            return fileSeq;
    }

    public void setFileSeq(String fileSeq) {
            this.fileSeq = fileSeq;
    }



    public String getPdfFilePath() {
        return pdfFilePath;
    }

    public void setPdfFilePath(String pdfFilePath) {
        this.pdfFilePath = pdfFilePath;
    }

    public String getHtmlFilePath() {
        return htmlFilePath;
    }

    public void setHtmlFilePath(String htmlFilePath) {
        this.htmlFilePath = htmlFilePath;
    }




    public String getFileName() {
            return fileName;
    }
    public void setFileName(String fileName) {
            this.fileName = fileName;
    }
    public String getDesc() {
            return desc;
    }
    public void setDesc(String desc) {
            this.desc = desc;
    }

    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }





}
