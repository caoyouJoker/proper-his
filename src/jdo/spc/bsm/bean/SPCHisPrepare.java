package jdo.spc.bsm.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: his֪ͨ��ʼ��ҩ</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.09.10 
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement(name = "ROOT") 
public class SPCHisPrepare {

	private String PRESC_DATE ;//��ҩ���ʱ��
	private String PRESC_NO ;//����ǩ��
	private String OPMANNAME ;//�ն˲���Ա����
	private String OPMANNO ;//�ն˲���Ա���
	private String OPWINID ;//��ҩ���ں�
	public String getPRESC_DATE() {
		return PRESC_DATE;
	}
	public void setPRESC_DATE(String pRESCDATE) {
		PRESC_DATE = pRESCDATE;
	}
	public String getPRESC_NO() {
		return PRESC_NO;
	}
	public void setPRESC_NO(String pRESCNO) {
		PRESC_NO = pRESCNO;
	}
	public String getOPMANNAME() {
		return OPMANNAME;
	}
	public void setOPMANNAME(String oPMANNAME) {
		OPMANNAME = oPMANNAME;
	}
	public String getOPMANNO() {
		return OPMANNO;
	}
	public void setOPMANNO(String oPMANNO) {
		OPMANNO = oPMANNO;
	}
	public String getOPWINID() {
		return OPWINID;
	}
	public void setOPWINID(String oPWINID) {
		OPWINID = oPWINID;
	}
	
}
