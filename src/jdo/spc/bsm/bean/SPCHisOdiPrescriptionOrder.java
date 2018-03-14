package jdo.spc.bsm.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: 同步his处方信息</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.09.13
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement(name = "ROOT") 
public class SPCHisOdiPrescriptionOrder {
	private String ORDERNO  ;//医嘱号
	private String ORDERSEQ ;//医嘱序号
	private String STARTDTTM ;//医嘱展开启用日期
	private String ENDDTTM ;//医嘱展开结束日期
	private String DSPNKIND ;//配药种类
	private String ORDERCATCODE ;//医嘱分类
	private String LINKMAINFLG ;//是否静脉配液主液(溶剂)
	private String LINKNO ;//静脉配液组号
	private String IVABARCODE ;//瓶签条码号
	private String ORDERCODE ;//药品编码
	private String ORDERDESC ;//药品名称
	private String SPECIFICATION ;//规格
	private String MEDIQTY ;//开药数量
	private String MEDIUNIT ;//开药单位
	private String FREQCODE ;//频次代码
	private String ROUTECODE ;//用法代码
	private String DOSAGEQTY ;//配药数量
	private String DOSAGEUNIT ;//配药单位
	private String ORDERDTTM ;//医嘱开立时间
	private String SENDACTFLG ;//是否送包药机
	private String       CASENO       ;//就诊号
	private String       STATIONCODE  ;//护士站(病区)代号
	private String       STATIONDESC  ;//护士站(病区)名称
	private String       DRCODE       ;//开单医师代号
	private String       DRNAME       ;//开单医师姓名
	private String       BEDNO        ;//床号
	private String       MRNO         ;//病案号
	private String       PATNAME      ;//病患姓名
	private String       SEX          ;//性别
	private String       EXECDEPTCODE ;//执行部门
	private String       BIRTHDAY    ;//出生日期
	private String       DRNOTE    ;//出生日期
	
	public String getCASENO() {
		return CASENO;
	}
	public void setCASENO(String cASENO) {
		CASENO = cASENO;
	}
	public String getSTATIONCODE() {
		return STATIONCODE;
	}
	public void setSTATIONCODE(String sTATIONCODE) {
		STATIONCODE = sTATIONCODE;
	}
	public String getSTATIONDESC() {
		return STATIONDESC;
	}
	public void setSTATIONDESC(String sTATIONDESC) {
		STATIONDESC = sTATIONDESC;
	}
	public String getDRCODE() {
		return DRCODE;
	}
	public void setDRCODE(String dRCODE) {
		DRCODE = dRCODE;
	}
	public String getDRNAME() {
		return DRNAME;
	}
	public void setDRNAME(String dRNAME) {
		DRNAME = dRNAME;
	}
	public String getBEDNO() {
		return BEDNO;
	}
	public void setBEDNO(String bEDNO) {
		BEDNO = bEDNO;
	}
	public String getMRNO() {
		return MRNO;
	}
	public void setMRNO(String mRNO) {
		MRNO = mRNO;
	}
	public String getPATNAME() {
		return PATNAME;
	}
	public void setPATNAME(String pATNAME) {
		PATNAME = pATNAME;
	}
	public String getSEX() {
		return SEX;
	}
	public void setSEX(String sEX) {
		SEX = sEX;
	}
	public String getEXECDEPTCODE() {
		return EXECDEPTCODE;
	}
	public void setEXECDEPTCODE(String eXECDEPTCODE) {
		EXECDEPTCODE = eXECDEPTCODE;
	}
	public String getBIRTHDAY() {
		return BIRTHDAY;
	}
	public void setBIRTHDAY(String bIRTHDAY) {
		BIRTHDAY = bIRTHDAY;
	}
	public String getORDERNO() {
		return ORDERNO;
	}
	public void setORDERNO(String oRDERNO) {
		ORDERNO = oRDERNO;
	}
	public String getORDERSEQ() {
		return ORDERSEQ;
	}
	public void setORDERSEQ(String oRDERSEQ) {
		ORDERSEQ = oRDERSEQ;
	}
	public String getSTARTDTTM() {
		return STARTDTTM;
	}
	public void setSTARTDTTM(String sTARTDTTM) {
		STARTDTTM = sTARTDTTM;
	}
	public String getENDDTTM() {
		return ENDDTTM;
	}
	public void setENDDTTM(String eNDDTTM) {
		ENDDTTM = eNDDTTM;
	}
	public String getDSPNKIND() {
		return DSPNKIND;
	}
	public void setDSPNKIND(String dSPNKIND) {
		DSPNKIND = dSPNKIND;
	}
	public String getORDERCATCODE() {
		return ORDERCATCODE;
	}
	public void setORDERCATCODE(String oRDERCATCODE) {
		ORDERCATCODE = oRDERCATCODE;
	}
	public String getLINKMAINFLG() {
		return LINKMAINFLG;
	}
	public void setLINKMAINFLG(String lINKMAINFLG) {
		LINKMAINFLG = lINKMAINFLG;
	}
	public String getLINKNO() {
		return LINKNO;
	}
	public void setLINKNO(String lINKNO) {
		LINKNO = lINKNO;
	}
	public String getIVABARCODE() {
		return IVABARCODE;
	}
	public void setIVABARCODE(String iVABARCODE) {
		IVABARCODE = iVABARCODE;
	}
	public String getORDERCODE() {
		return ORDERCODE;
	}
	public void setORDERCODE(String oRDERCODE) {
		ORDERCODE = oRDERCODE;
	}
	public String getORDERDESC() {
		return ORDERDESC;
	}
	public void setORDERDESC(String oRDERDESC) {
		ORDERDESC = oRDERDESC;
	}
	public String getSPECIFICATION() {
		return SPECIFICATION;
	}
	public void setSPECIFICATION(String sPECIFICATION) {
		SPECIFICATION = sPECIFICATION;
	}
	public String getMEDIQTY() {
		return MEDIQTY;
	}
	public void setMEDIQTY(String mEDIQTY) {
		MEDIQTY = mEDIQTY;
	}
	public String getMEDIUNIT() {
		return MEDIUNIT;
	}
	public void setMEDIUNIT(String mEDIUNIT) {
		MEDIUNIT = mEDIUNIT;
	}
	public String getFREQCODE() {
		return FREQCODE;
	}
	public void setFREQCODE(String fREQCODE) {
		FREQCODE = fREQCODE;
	}
	public String getROUTECODE() {
		return ROUTECODE;
	}
	public void setROUTECODE(String rOUTECODE) {
		ROUTECODE = rOUTECODE;
	}
	public String getDOSAGEQTY() {
		return DOSAGEQTY;
	}
	public void setDOSAGEQTY(String dOSAGEQTY) {
		DOSAGEQTY = dOSAGEQTY;
	}
	public String getDOSAGEUNIT() {
		return DOSAGEUNIT;
	}
	public void setDOSAGEUNIT(String dOSAGEUNIT) {
		DOSAGEUNIT = dOSAGEUNIT;
	}
	public String getORDERDTTM() {
		return ORDERDTTM;
	}
	public void setORDERDTTM(String oRDERDTTM) {
		ORDERDTTM = oRDERDTTM;
	}
	public String getSENDACTFLG() {
		return SENDACTFLG;
	}
	public void setSENDACTFLG(String sENDACTFLG) {
		SENDACTFLG = sENDACTFLG;
	}
	public String getDRNOTE() {
		return DRNOTE;
	}
	public void setDRNOTE(String dRNOTE) {
		DRNOTE = dRNOTE;
	}
	
}
