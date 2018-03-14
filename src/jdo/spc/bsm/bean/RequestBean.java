package jdo.spc.bsm.bean;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: 请领单对象</p>
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
public class RequestBean {
	private String BARCODE;      //物联网药品编码
	private String   APPQUANTITY;  //请领数量
	private String   CURQUANTITY;  //当前库存
	/**
	 * 物联网药品编码
	 * @return
	 */
	public String getBARCODE() {
		return BARCODE;
	}
	/**
	 * 物联网药品编码
	 * @param bARCODE
	 */
	public void setBARCODE(String bARCODE) {
		BARCODE = bARCODE;
	}
	/**
	 * 请领数量
	 * @return
	 */
	public String getAPPQUANTITY() {
		return APPQUANTITY;
	}
	/**
	 * 请领数量
	 * @param aPPQUANTITY
	 */
	public void setAPPQUANTITY(String aPPQUANTITY) {
		APPQUANTITY = aPPQUANTITY;
	}
	/**
	 * 当前库存
	 * @return
	 */
	public String getCURQUANTITY() {
		return CURQUANTITY;
	}
	/**
	 * 当前库存
	 * @param cURQUANTITY
	 */
	public void setCURQUANTITY(String cURQUANTITY) {
		CURQUANTITY = cURQUANTITY;
	}
	
}
