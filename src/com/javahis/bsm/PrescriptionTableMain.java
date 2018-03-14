package com.javahis.bsm;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
*
* <p>Title: 处方主表对象</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.05.14 
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD)  
@XmlRootElement(name = "CONSIS_PRESC_MSTVW")
public class PrescriptionTableMain {

	@XmlElement(name = "CONSIS_PRESC_DTLVW")
	private  List<PrescriptionTableDetail> detail; //处方明细
	private  String  PRESC_DATE;    //处方时间
	private  String  PRESC_NO;      //处方编号
	private  String  DISPENSARY;    //发药药局
	private  String  PATIENT_ID;    //就诊卡号
	private  String  PATIENT_NAME;  //患者姓名
	private  String  PATIENT_TYPE ; //患者类型
	private  String  DATE_OF_BIRTH; //患者出生日期
	private  String  SEX;           //患者性别
	private  String  PRESC_IDENTITY;//患者身份
	private  String  CHARGE_TYPE;	//医保类型
	private  String  PRESC_ATTR;    //处方属性
	private  String  PRESC_INFO;    //处方类型
	private  String  RCPT_INFO;     //诊断信息
	private  String  RCPT_REMARK;   //处方备注信息
	private  String  REPETITION;    //剂数
	private  String  COSTS;        //费用
	private  String  PAYMENTS;     //实付费用
	private  String  ORDERED_BY;   //开单科室
	private  String  PRESCRIBED_BY;//开方医生
	private  String  ENTERED_BY;   //录方人
	private  String  DISPENSE_PRI; //配药优先级（付费处到药房距离）数字从小到大表示优先级从高到低
	private  String  ORDERED_ID ;  //开单科室代码
	private  String  PRESCRIBED_ID ;//开方医生代码
	/**
	 * 处方时间
	 * @return
	 */
	public String getPRESC_DATE() {
		return PRESC_DATE;
	}
	/**
	 * 
	 * 处方时间
	 */
	public void setPRESC_DATE(String pRESCDATE) {
		PRESC_DATE = pRESCDATE;
	}
	/**
	 * 处方编号
	 * @return
	 */
	public String getPRESC_NO() {
		return PRESC_NO;
	}
	/**
	 * 处方编号
	 * @param pRESCNO
	 */
	public void setPRESC_NO(String pRESCNO) {
		PRESC_NO = pRESCNO;
	}
	/**
	 * 发药药局
	 * @return
	 */
	public String getDISPENSARY() {
		return DISPENSARY;
	}
	/**
	 * 发药药局
	 * @param dISPENSARY
	 */
	public void setDISPENSARY(String dISPENSARY) {
		DISPENSARY = dISPENSARY;
	}
	/**
	 * 就诊卡号
	 * @return
	 */
	public String getPATIENT_ID() {
		return PATIENT_ID;
	}
	/**
	 * 就诊卡号
	 * @param pATIENTID
	 */
	public void setPATIENT_ID(String pATIENTID) {
		PATIENT_ID = pATIENTID;
	}
	/**
	 * 患者姓名
	 * @return
	 */
	public String getPATIENT_NAME() {
		return PATIENT_NAME;
	}
	/**
	 * 患者姓名
	 * @param pATIENTNAME
	 */
	public void setPATIENT_NAME(String pATIENTNAME) {
		PATIENT_NAME = pATIENTNAME;
	}
	/**
	 * 患者类型
‘00’ 普通
‘01’ 特需 

	 * @return
	 */
	public String getPATIENT_TYPE() {
		return PATIENT_TYPE;
	}
	/**
	 * 患者类型
‘00’ 普通
‘01’ 特需 

	 * @param pATIENTTYPE
	 */
	public void setPATIENT_TYPE(String pATIENTTYPE) {
		PATIENT_TYPE = pATIENTTYPE;
	}
	/**
	 * 患者出生日期
	 * @return
	 */
	public String getDATE_OF_BIRTH() {
		return DATE_OF_BIRTH;
	}
	/**
	 * 患者出生日期
	 * @param dATEOFBIRTH
	 */
	public void setDATE_OF_BIRTH(String dATEOFBIRTH) {
		DATE_OF_BIRTH = dATEOFBIRTH;
	}
	/**
	 * 患者性别
	 * @return
	 */
	public String getSEX() {
		return SEX;
	}
	/**
	 * 患者性别
	 * @param sEX
	 */
	public void setSEX(String sEX) {
		SEX = sEX;
	}
	/**
	 * 患者身份
	 * @return
	 */
	public String getPRESC_IDENTITY() {
		return PRESC_IDENTITY;
	}
	/**
	 * 患者身份
	 * @param pRESCIDENTITY
	 */
	public void setPRESC_IDENTITY(String pRESCIDENTITY) {
		PRESC_IDENTITY = pRESCIDENTITY;
	}
	/**
	 * 医保类型
	 * @return
	 */
	public String getCHARGE_TYPE() {
		return CHARGE_TYPE;
	}
	/**
	 * 医保类型
	 * @param cHARGETYPE
	 */
	public void setCHARGE_TYPE(String cHARGETYPE) {
		CHARGE_TYPE = cHARGETYPE;
	}
	/**
	 * 处方属性
手工处方，临时处方等文本信息

	 * @return
	 */
	public String getPRESC_ATTR() {
		return PRESC_ATTR;
	}
	/**
	 * 处方属性
手工处方，临时处方等文本信息

	 * @param pRESCATTR
	 */
	public void setPRESC_ATTR(String pRESCATTR) {
		PRESC_ATTR = pRESCATTR;
	}
	/**
	 * 处方类型
费用相关处方类型文本信息（计费方式）

	 * @return
	 */
	public String getPRESC_INFO() {
		return PRESC_INFO;
	}
	/**
	 * 处方类型
费用相关处方类型文本信息（计费方式）

	 * @param pRESCINFO
	 */
	public void setPRESC_INFO(String pRESCINFO) {
		PRESC_INFO = pRESCINFO;
	}
	/**
	 * 诊断信息
	 * @return
	 */
	public String getRCPT_INFO() {
		return RCPT_INFO;
	}
	/**
	 * 诊断信息
	 * @param rCPTINFO
	 */
	public void setRCPT_INFO(String rCPTINFO) {
		RCPT_INFO = rCPTINFO;
	}
	/**
	 * 处方备注信息
	 * @return
	 */
	public String getRCPT_REMARK() {
		return RCPT_REMARK;
	}
	/**
	 * 处方备注信息
	 * @param rCPTREMARK
	 */
	public void setRCPT_REMARK(String rCPTREMARK) {
		RCPT_REMARK = rCPTREMARK;
	}
	/**
	 * 剂数
	 * @return
	 */
	public String getREPETITION() {
		return REPETITION;
	}
	/**
	 * 剂数
	 * @param rEPETITION
	 */
	public void setREPETITION(String rEPETITION) {
		REPETITION = rEPETITION;
	}
	/**
	 * 费用
	 * @return
	 */
	public String getCOSTS() {
		return COSTS;
	}
	/**
	 * 费用
	 * @param cOSTS
	 */
	public void setCOSTS(String cOSTS) {
		COSTS = cOSTS;
	}
	/**
	 * 实付费用
	 * @return
	 */
	public String getPAYMENTS() {
		return PAYMENTS;
	}
	/**
	 * 实付费用
	 * @param pAYMENTS
	 */
	public void setPAYMENTS(String pAYMENTS) {
		PAYMENTS = pAYMENTS;
	}
	/**
	 * 开单科室
	 * @return
	 */
	public String getORDERED_BY() {
		return ORDERED_BY;
	}
	/**
	 * 开单科室
	 * @param oRDEREDBY
	 */
	public void setORDERED_BY(String oRDEREDBY) {
		ORDERED_BY = oRDEREDBY;
	}
	/**
	 * 开方医生
	 * @return
	 */
	public String getPRESCRIBED_BY() {
		return PRESCRIBED_BY;
	}
	/**
	 * 开方医生
	 * @param pRESCRIBEDBY
	 */
	public void setPRESCRIBED_BY(String pRESCRIBEDBY) {
		PRESCRIBED_BY = pRESCRIBEDBY;
	}
	/**
	 * 录方人
	 * @return
	 */
	public String getENTERED_BY() {
		return ENTERED_BY;
	}
	/**
	 * 录方人
	 * @param eNTEREDBY
	 */
	public void setENTERED_BY(String eNTEREDBY) {
		ENTERED_BY = eNTEREDBY;
	}
	/**
	 * 配药优先级（付费处到药房距离）数字从小到大表示优先级从高到低
	 * @return
	 */
	public String getDISPENSE_PRI() {
		return DISPENSE_PRI;
	}
	/**
	 * 配药优先级（付费处到药房距离）数字从小到大表示优先级从高到低
	 * @param dISPENSEPRI
	 */
	public void setDISPENSE_PRI(String dISPENSEPRI) {
		DISPENSE_PRI = dISPENSEPRI;
	}
	/**
	 * 得到处方明细
	 * @return
	 */
	 public List<PrescriptionTableDetail> getDetail() {  
	        if (detail == null) {  
	        	detail = new ArrayList<PrescriptionTableDetail>();  
	        }  
	        return this.detail;  
	    }
	 /**
	  * 开单科室代码
	  * @return
	  */
	public String getORDERED_ID() {
		return ORDERED_ID;
	}
	/**
	 * 开单科室代码
	 * @param oRDEREDID
	 */
	public void setORDERED_ID(String oRDEREDID) {
		ORDERED_ID = oRDEREDID;
	}
	/**
	 * 开方医生代码
	 * @return
	 */
	public String getPRESCRIBED_ID() {
		return PRESCRIBED_ID;
	}
	/**
	 * 开方医生代码
	 * @param pRESCRIBEDID
	 */
	public void setPRESCRIBED_ID(String pRESCRIBEDID) {
		PRESCRIBED_ID = pRESCRIBEDID;
	}  
	 
}
