package jdo.spc.bsm.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: ����ֵ�Ķ���</p>
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
@XmlRootElement(name = "ROOT")
public class ReturnHisBean {
	private  String  RTSTATUS ;//״̬��Ϣ 0������������his����1�������������װ��ҩ������2����ɹ�
	private  String  RTMSG;     //������Ϣ
	public String getRTSTATUS() {
		return RTSTATUS;
	}
	public void setRTSTATUS(String rTSTATUS) {
		RTSTATUS = rTSTATUS;
	}
	public String getRTMSG() {
		return RTMSG;
	}
	public void setRTMSG(String rTMSG) {
		RTMSG = rTMSG;
	}
	

}

