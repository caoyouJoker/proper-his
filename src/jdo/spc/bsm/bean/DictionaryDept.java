package jdo.spc.bsm.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


	/**
	*
	* <p>Title: Í¬²½¿ÆÊÒ×Öµä</p>
	*
	* <p>Description: </p>
	*
	* <p>Copyright: Copyright (c) 2013</p>
	*
	* <p>Company: JavaHis</p>
	*
	* @author chenx 2013.09.27 
	* @version 4.0
	*/
	@XmlAccessorType(XmlAccessType.FIELD) 
	@XmlRootElement(name = "CONSIS_PHC_DEPTVW")   
	public class DictionaryDept {

		private String DEPT_CODE ;
		private String DEPT_NAME ;
		public String getDEPT_CODE() {
			return DEPT_CODE;
		}
		public void setDEPT_CODE(String dEPTCODE) {
			DEPT_CODE = dEPTCODE;
		}
		public String getDEPT_NAME() {
			return DEPT_NAME;
		}
		public void setDEPT_NAME(String dEPTNAME) {
			DEPT_NAME = dEPTNAME;
		}
}
