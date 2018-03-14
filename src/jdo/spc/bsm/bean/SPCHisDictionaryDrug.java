package jdo.spc.bsm.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: 药品信息字典同步his</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.10.15
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement(name = "ROOT") 
public class SPCHisDictionaryDrug {

	private String DRUGCODE;//药品代码
	private String DRUGDESC;//药品名称
	private String ALIASDESC;//商品名
	private String SPEC;//规格
	private String MANUF;//生产厂商
	private String RTLPRICE;//零售价
	private String PURPRICE;//采购价(片)
	private String TOXCODE;//麻精标记
	private String PHATYPE;//药品分类
	private String PURUNIT;//采购单位代码
	private String PURRATIO;//采购单位转换率
	private String STKUNIT;//库存单位代码
	private String STKRATIO;//库存单位转换率
	private String DOSUNIT;//配药单位代码
	private String DOSRATIO;//配药单位转换率
	private String DOSECODE;//剂型
	private String PYCODE;//拼音码
	private String ISACTIVE;//是否启用
	public String getDRUGCODE() {
		return DRUGCODE;
	}
	public void setDRUGCODE(String dRUGCODE) {
		DRUGCODE = dRUGCODE;
	}
	public String getDRUGDESC() {
		return DRUGDESC;
	}
	public void setDRUGDESC(String dRUGDESC) {
		DRUGDESC = dRUGDESC;
	}
	public String getALIASDESC() {
		return ALIASDESC;
	}
	public void setALIASDESC(String aLIASDESC) {
		ALIASDESC = aLIASDESC;
	}
	public String getSPEC() {
		return SPEC;
	}
	public void setSPEC(String sPEC) {
		SPEC = sPEC;
	}
	public String getMANUF() {
		return MANUF;
	}
	public void setMANUF(String mANUF) {
		MANUF = mANUF;
	}
	public String getRTLPRICE() {
		return RTLPRICE;
	}
	public void setRTLPRICE(String rTLPRICE) {
		RTLPRICE = rTLPRICE;
	}
	public String getPURPRICE() {
		return PURPRICE;
	}
	public void setPURPRICE(String pURPRICE) {
		PURPRICE = pURPRICE;
	}
	public String getTOXCODE() {
		return TOXCODE;
	}
	public void setTOXCODE(String tOXCODE) {
		TOXCODE = tOXCODE;
	}
	public String getPHATYPE() {
		return PHATYPE;
	}
	public void setPHATYPE(String pHATYPE) {
		PHATYPE = pHATYPE;
	}
	public String getPURUNIT() {
		return PURUNIT;
	}
	public void setPURUNIT(String pURUNIT) {
		PURUNIT = pURUNIT;
	}
	public String getPURRATIO() {
		return PURRATIO;
	}
	public void setPURRATIO(String pURRATIO) {
		PURRATIO = pURRATIO;
	}
	public String getSTKUNIT() {
		return STKUNIT;
	}
	public void setSTKUNIT(String sTKUNIT) {
		STKUNIT = sTKUNIT;
	}
	public String getSTKRATIO() {
		return STKRATIO;
	}
	public void setSTKRATIO(String sTKRATIO) {
		STKRATIO = sTKRATIO;
	}
	public String getDOSUNIT() {
		return DOSUNIT;
	}
	public void setDOSUNIT(String dOSUNIT) {
		DOSUNIT = dOSUNIT;
	}
	public String getDOSRATIO() {
		return DOSRATIO;
	}
	public void setDOSRATIO(String dOSRATIO) {
		DOSRATIO = dOSRATIO;
	}
	public String getDOSECODE() {
		return DOSECODE;
	}
	public void setDOSECODE(String dOSECODE) {
		DOSECODE = dOSECODE;
	}
	public String getPYCODE() {
		return PYCODE;
	}
	public void setPYCODE(String pYCODE) {
		PYCODE = pYCODE;
	}
	public String getISACTIVE() {
		return ISACTIVE;
	}
	public void setISACTIVE(String iSACTIVE) {
		ISACTIVE = iSACTIVE;
	}
	
}
