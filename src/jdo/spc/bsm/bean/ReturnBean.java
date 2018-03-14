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
public class ReturnBean {
	private  String  RETVAL ;
	private  String  RETMSG;     //返回信息
	private  int  RETCODE ;   //0代表失败，1代表成功
	public String getRETVAL() {
		return RETVAL;
	}
	public void setRETVAL(String rETVAL) {
		RETVAL = rETVAL;
	}
	/**
	 * 返回信息
	 * @return
	 */
	public String getRETMSG() {
		return RETMSG;
	}
	/**
	 * 返回信息
	 * @param rETMSG
	 */
	public void setRETMSG(String rETMSG) {
		RETMSG = rETMSG;
	}
	/**
	 * 0代表失败，1代表成功
	 * @return
	 */
	public int getRETCODE() {
		return RETCODE;
	}
	/**
	 * 0代表失败，1代表成功
	 * @param rETCODE
	 */
	public void setRETCODE(int rETCODE) {
		RETCODE = rETCODE;
	}
	

}
