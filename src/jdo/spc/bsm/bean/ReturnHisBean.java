package jdo.spc.bsm.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: 返回值的对象</p>
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
	private  String  RTSTATUS ;//状态信息 0代表物联网与his出错、1代表物联网与盒装包药机出错，2代表成功
	private  String  RTMSG;     //返回信息
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

