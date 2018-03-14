package jdo.spc.bsm.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: ͬ��his������Ϣ</p>
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
	private String       CASENO       ;//�����
	private String       STATIONCODE  ;//��ʿվ(����)����
	private String       STATIONDESC  ;//��ʿվ(����)����
	private String       DRCODE       ;//����ҽʦ����
	private String       DRNAME       ;//����ҽʦ����
	private String       BEDNO        ;//����
	private String       MRNO         ;//������
	private String       PATNAME      ;//��������
	private String       SEX          ;//�Ա�
	private String       EXECDEPTCODE ;//ִ�в���
	private String       BIRTHDAY    ;//��������
	@XmlElement(name = "RXMSG")
	private  List<SPCHisOdiPrescriptionOrder> orderDetail; //ҽ����ϸ
	/**
	 * �õ�������ϸ
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
