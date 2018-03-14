package jdo.spc.bsm.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: 同步字典库存对象</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.05.26 
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement(name = "CONSIS_PHC_STORAGEVW ")   
public class DictionaryStock {

	private String DRUG_CODE ;
	private String DISPENSARY ;
	private String DRUG_QUANTITY ;
	private String LOCATIONINFO ;
	/**
	 * 药品编号
	 * @return
	 */
	public String getDRUG_CODE() {
		return DRUG_CODE;
	}
	/**
	 * 药品编号
	 * @param dRUGCODE
	 */
	public void setDRUG_CODE(String dRUGCODE) {
		DRUG_CODE = dRUGCODE;
	}
	/**
	 * 发药药局代码
	 * @return
	 */
	public String getDISPENSARY() {
		return DISPENSARY;
	}
	/**
	 * 发药药局代码
	 * @param dISPENSARY
	 */
	public void setDISPENSARY(String dISPENSARY) {
		DISPENSARY = dISPENSARY;
	}
	/**
	 * 药品数量
	 * @return
	 */
	public String getDRUG_QUANTITY() {
		return DRUG_QUANTITY;
	}
	/**
	 * 药品数量
	 * @param dRUGQUANTITY
	 */
	public void setDRUG_QUANTITY(String dRUGQUANTITY) {
		DRUG_QUANTITY = dRUGQUANTITY;
	}
	/**
	 * 药品货位
	 * @return
	 */
	public String getLOCATIONINFO() {
		return LOCATIONINFO;
	}
	/**
	 * 药品货位
	 * @param lOCATIONINFO
	 */
	public void setLOCATIONINFO(String lOCATIONINFO) {
		LOCATIONINFO = lOCATIONINFO;
	}
	
}
