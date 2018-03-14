package jdo.spc.bsm.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: 同步his请领数据</p>
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
	private String  REQUESTNO ;//申请单号
	private String  SEQNO ;//流水号
	private String  ORDERCODE ;//药品编码
	private String  QTY ;//请领数量
	private String  UNITCODE ;//单位代码
	private String  RETAILPRICE ;//零售价
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
