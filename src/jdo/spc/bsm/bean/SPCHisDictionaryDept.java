package jdo.spc.bsm.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: ͬ��his�����ֵ�</p>
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
	private String DEPTCODE;//���Ҵ���
	private String DEPTNAME;//��������
	private String COSTCENTER;//�ɱ����Ĵ���
	private String ISACTIVE;//�Ƿ�����
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
