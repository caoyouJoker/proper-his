package jdo.spc.bsm.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: ͬ��his��������</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.10.25
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement(name = "ROOT") 
public class SPCHisRequestDetail {
	private String  REQUESTNO ;//���뵥��
	private String  SEQNO ;//��ˮ��
	private String  ORDERCODE ;//ҩƷ����
	private String  QTY ;//��������
	private String  UNITCODE ;//��λ����
	private String  RETAILPRICE ;//���ۼ�
	public String getREQUESTNO() {
		return REQUESTNO;
	}
	public void setREQUESTNO(String rEQUESTNO) {
		REQUESTNO = rEQUESTNO;
	}
	public String getSEQNO() {
		return SEQNO;
	}
	public void setSEQNO(String sEQNO) {
		SEQNO = sEQNO;
	}
	public String getORDERCODE() {
		return ORDERCODE;
	}
	public void setORDERCODE(String oRDERCODE) {
		ORDERCODE = oRDERCODE;
	}
	public String getQTY() {
		return QTY;
	}
	public void setQTY(String qTY) {
		QTY = qTY;
	}
	public String getUNITCODE() {
		return UNITCODE;
	}
	public void setUNITCODE(String uNITCODE) {
		UNITCODE = uNITCODE;
	}
	public String getRETAILPRICE() {
		return RETAILPRICE;
	}
	public void setRETAILPRICE(String rETAILPRICE) {
		RETAILPRICE = rETAILPRICE;
	}

}
