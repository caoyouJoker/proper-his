package jdo.spc.bsm.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: ҩƷ��Ϣ�ֵ�ͬ��his</p>
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

	private String DRUGCODE;//ҩƷ����
	private String DRUGDESC;//ҩƷ����
	private String ALIASDESC;//��Ʒ��
	private String SPEC;//���
	private String MANUF;//��������
	private String RTLPRICE;//���ۼ�
	private String PURPRICE;//�ɹ���(Ƭ)
	private String TOXCODE;//�龫���
	private String PHATYPE;//ҩƷ����
	private String PURUNIT;//�ɹ���λ����
	private String PURRATIO;//�ɹ���λת����
	private String STKUNIT;//��浥λ����
	private String STKRATIO;//��浥λת����
	private String DOSUNIT;//��ҩ��λ����
	private String DOSRATIO;//��ҩ��λת����
	private String DOSECODE;//����
	private String PYCODE;//ƴ����
	private String ISACTIVE;//�Ƿ�����
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
