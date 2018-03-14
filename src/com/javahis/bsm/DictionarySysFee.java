package com.javahis.bsm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: 同步字典SYS_FEE对象</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.05.24 
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement(name = "CONSIS_BASIC_DRUGSVW ")
public class DictionarySysFee {

	/**
	 * 药品编号
	 */
	private String DRUG_CODE  ;          //药品编号
	private String DRUG_NAME  ;          //药品名称
	private String TRADE_NAME  ;         //药品商品名
	private String DRUG_SPEC  ;          //药品规格
	private String DRUG_PACKAGE  ;       //药品包装规格
	private String DRUG_UNIT  ;          //药品单位HIS系统售价单位
	private String FIRM_ID  ;            //药品厂家
	private String DRUG_PRICE  ;         //药品价格
	private String DRUG_FORM  ;          //药品剂型
	private String DRUG_SORT  ;          //药品分类药房需特殊分窗口发放的分类名称（毒理分类）
	private String BARCODE  ;            //药品条码
	private String LAST_DATE  ;          //最后更新时间
	private String PINYIN  ;             //药品拼音
	private String DRUG_CONVERTATION  ;  //换算药房发药单位与药品售价单位换算比如1盒（药房发药单位）=24片（售价单位）则为24
	private String ALLOWIND  ;           //	药品是否启用‘Y’启用‘N’停用
	/**
	 * 药品编号
	 * @return
	 */
	public String getDRUG_CODE() {
		return DRUG_CODE;
	}
	/**
	 * 药品编号
	 * @param dRUGCODE
	 */
	public void setDRUG_CODE(String dRUGCODE) {
		DRUG_CODE = dRUGCODE;
	}
	/**
	 * 药品名称
	 * @return
	 */
	public String getDRUG_NAME() {
		return DRUG_NAME;
	}
	/**
	 * 药品名称
	 * @param dRUGNAME
	 */
	public void setDRUG_NAME(String dRUGNAME) {
		DRUG_NAME = dRUGNAME;
	}
	/**
	 * 药品商品名
	 * @return
	 */
	public String getTRADE_NAME() {
		return TRADE_NAME;
	}
	/**
	 * 药品商品名
	 * @param tRADENAME
	 */
	public void setTRADE_NAME(String tRADENAME) {
		TRADE_NAME = tRADENAME;
	}
	/**
	 * 药品规格
	 * @return
	 */
	public String getDRUG_SPEC() {
		return DRUG_SPEC;
	}
	/**
	 * 药品规格
	 * @param dRUGSPEC
	 */
	public void setDRUG_SPEC(String dRUGSPEC) {
		DRUG_SPEC = dRUGSPEC;
	}
	/**
	 * 药品包装规格
	 * @return
	 */
	public String getDRUG_PACKAGE() {
		return DRUG_PACKAGE;
	}
	/**
	 * 药品包装规格
	 * @param dRUGPACKAGE
	 */
	public void setDRUG_PACKAGE(String dRUGPACKAGE) {
		DRUG_PACKAGE = dRUGPACKAGE;
	}
	/**
	 * 药品单位HIS系统售价单位
	 * @return
	 */
	public String getDRUG_UNIT() {
		return DRUG_UNIT;
	}
	/**
	 * 药品单位HIS系统售价单位
	 * @param dRUGUNIT
	 */
	public void setDRUG_UNIT(String dRUGUNIT) {
		DRUG_UNIT = dRUGUNIT;
	}
	/**
	 * 药品厂家
	 * @return
	 */
	public String getFIRM_ID() {
		return FIRM_ID;
	}
	/**
	 * 药品厂家
	 * @param fIRMID
	 */
	public void setFIRM_ID(String fIRMID) {
		FIRM_ID = fIRMID;
	}
	/**
	 * 药品价格
	 * @return
	 */
	public String getDRUG_PRICE() {
		return DRUG_PRICE;
	}
	/**
	 * 药品价格
	 * @param dRUGPRICE
	 */
	public void setDRUG_PRICE(String dRUGPRICE) {
		DRUG_PRICE = dRUGPRICE;
	}
	/**
	 * 药品剂型
	 * @return
	 */
	public String getDRUG_FORM() {
		return DRUG_FORM;
	}
	/**
	 * 药品剂型
	 * @param dRUGFORM
	 */
	public void setDRUG_FORM(String dRUGFORM) {
		DRUG_FORM = dRUGFORM;
	}
	/**
	 * 药品分类
	 * @return
	 */
	public String getDRUG_SORT() {
		return DRUG_SORT;
	}
	/**
	 * 药品分类
	 * @param dRUGSORT
	 */
	public void setDRUG_SORT(String dRUGSORT) {
		DRUG_SORT = dRUGSORT;
	}
	/**
	 * 药品条码
	 * @return
	 */
	public String getBARCODE() {
		return BARCODE;
	}
	/**
	 * 药品条码
	 * @param bARCODE
	 */
	public void setBARCODE(String bARCODE) {
		BARCODE = bARCODE;
	}
	/**
	 * 最后更新时间
	 * @return
	 */
	public String getLAST_DATE() {
		return LAST_DATE;
	}
	/**
	 * 最后更新时间
	 * @param lASTDATE
	 */
	public void setLAST_DATE(String lASTDATE) {
		LAST_DATE = lASTDATE;
	}
	/**
	 * 药品拼音
	 * @return
	 */
	public String getPINYIN() {
		return PINYIN;
	}
	/**
	 * 药品拼音
	 * @param pINYIN
	 */
	public void setPINYIN(String pINYIN) {
		PINYIN = pINYIN;
	}
	/**
	 * 换算率
药房发药单位与药品售价单位换算比
如1盒（药房发药单位）
=24片（售价单位）则为24

	 * @return
	 */
	public String getDRUG_CONVERTATION() {
		return DRUG_CONVERTATION;
	}
	/**
	 * 换算率
药房发药单位与药品售价单位换算比
如1盒（药房发药单位）
=24片（售价单位）则为24

	 * @param dRUGCONVERTATION
	 */
	public void setDRUG_CONVERTATION(String dRUGCONVERTATION) {
		DRUG_CONVERTATION = dRUGCONVERTATION;
	}
	/**
	 * 药品是否启用
	 * @return
	 */
	public String getALLOWIND() {
		return ALLOWIND;
	}
	/**
	 * 药品是否启用
	 * @param aLLOWIND
	 */
	public void setALLOWIND(String aLLOWIND) {
		ALLOWIND = aLLOWIND;
	}

	
}
