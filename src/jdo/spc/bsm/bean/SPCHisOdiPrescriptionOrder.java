package jdo.spc.bsm.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
public class SPCHisOdiPrescriptionOrder {
	private String ORDERNO  ;//ҽ����
	private String ORDERSEQ ;//ҽ�����
	private String STARTDTTM ;//ҽ��չ����������
	private String ENDDTTM ;//ҽ��չ����������
	private String DSPNKIND ;//��ҩ����
	private String ORDERCATCODE ;//ҽ������
	private String LINKMAINFLG ;//�Ƿ�����Һ��Һ(�ܼ�)
	private String LINKNO ;//������Һ���
	private String IVABARCODE ;//ƿǩ�����
	private String ORDERCODE ;//ҩƷ����
	private String ORDERDESC ;//ҩƷ����
	private String SPECIFICATION ;//���
	private String MEDIQTY ;//��ҩ����
	private String MEDIUNIT ;//��ҩ��λ
	private String FREQCODE ;//Ƶ�δ���
	private String ROUTECODE ;//�÷�����
	private String DOSAGEQTY ;//��ҩ����
	private String DOSAGEUNIT ;//��ҩ��λ
	private String ORDERDTTM ;//ҽ������ʱ��
	private String SENDACTFLG ;//�Ƿ��Ͱ�ҩ��
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
	private String       DRNOTE    ;//��������
	
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
