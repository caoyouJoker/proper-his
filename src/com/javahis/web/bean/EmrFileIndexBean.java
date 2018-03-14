package com.javahis.web.bean;

public class EmrFileIndexBean {
	private String viewPattern;
	private String xmlPath;
	private String xslPath;
	private String pdfPath;
	private String caseNo;
	private String fileSeq;
	private String viewType;
	private String mrNo;
	private String cdaXml;
	public String getCdaXml() {
		return cdaXml;
	}
	public void setCdaXml(String cdaXml) {
		this.cdaXml = cdaXml;
	}
	public String getMrNo() {
		return mrNo;
	}
	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}
	public String getViewType() {
		return viewType;
	}
	public void setViewType(String viewType) {
		this.viewType = viewType;
	}
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
	public String getXmlPath() {
		return xmlPath;
	}
	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}
	public String getXslPath() {
		return xslPath;
	}
	public void setXslPath(String xslPath) {
		this.xslPath = xslPath;
	}
	public String getPdfPath() {
		return pdfPath;
	}
	public void setPdfPath(String pdfPath) {
		this.pdfPath = pdfPath;
	}
	public String getViewPattern() {
		return viewPattern;
	}
	public void setViewPattern(String viewPattern) {
		this.viewPattern = viewPattern;
	}
}
