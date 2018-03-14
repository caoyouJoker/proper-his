package jdo.spc.bsm.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: 同步his科室字典</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.09.13
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement(name = "ROOT") 
public class SPCHisDictionaryDept {
	private String DEPTCODE;//科室代号
	private String DEPTNAME;//科室名称
	private String COSTCENTER;//成本中心代号
	private String ISACTIVE;//是否启用
	public String getDEPTCODE() {
		return DEPTCODE;
	}
	public void setDEPTCODE(String dEPTCODE) {
		DEPTCODE = dEPTCODE;
	}
	public String getDEPTNAME() {
		return DEPTNAME;
	}
	public void setDEPTNAME(String dEPTNAME) {
		DEPTNAME = dEPTNAME;
	}
	public String getCOSTCENTER() {
		return COSTCENTER;
	}
	public void setCOSTCENTER(String cOSTCENTER) {
		COSTCENTER = cOSTCENTER;
	}
	public String getISACTIVE() {
		return ISACTIVE;
	}
	public void setISACTIVE(String iSACTIVE) {
		ISACTIVE = iSACTIVE;
	}



}
