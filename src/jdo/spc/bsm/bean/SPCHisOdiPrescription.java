package jdo.spc.bsm.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
public class SPCHisOdiPrescription {
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
	@XmlElement(name = "RXMSG")
	private  List<SPCHisOdiPrescriptionOrder> orderDetail; //医嘱明细
	/**
	 * 得到处方明细
	 * @return
	 */
	 public List<SPCHisOdiPrescriptionOrder> getOrderDetail() {  
	        if (orderDetail == null) {  
	        	orderDetail = new ArrayList<SPCHisOdiPrescriptionOrder>();  
	        }  
	        return this.orderDetail;  
	    }
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

	
	

}
