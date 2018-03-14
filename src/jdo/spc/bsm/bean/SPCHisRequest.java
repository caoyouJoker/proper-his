package jdo.spc.bsm.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: ͬ��his������Ϣ</p>
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
public class SPCHisRequest {
     private String  REQUESTNO ;//���뵥��
     private String  REQTYPECODE ;//�������	
     private String  REGIONCODE ;//Ժ������
     private String  APPORGCODE ;//���벿�Ŵ���
     private String  TOORGCODE ;//���ܲ��Ŵ���
     private String  REQUESTDATE ;//��������
     private String  REQUESTUSER ;//������Ա
     private String  REASONCHNDESC ;//����ԭ��
     private String  URGENTFLG ;//�Ƿ����
     private String  DRUGCATEGORY ;//����ҩƷ����
     private String  OPTUSER ;//������Ա
     private String  OPTDATE ;//����ʱ��
     private String  OPTTERM ;//������ĩIP
     private String  UNITTYPE ;//��λ���
     @XmlElement(name = "REQUESTD")//ҩƷ�ֵ�(HIS)
     private List<SPCHisRequestDetail> requestDetail ;
	public String getREQUESTNO() {
		return REQUESTNO;
	}
	public void setREQUESTNO(String rEQUESTNO) {
		REQUESTNO = rEQUESTNO;
	}
	public String getREQTYPECODE() {
		return REQTYPECODE;
	}
	public void setREQTYPECODE(String rEQTYPECODE) {
		REQTYPECODE = rEQTYPECODE;
	}
	public String getREGIONCODE() {
		return REGIONCODE;
	}
	public void setREGIONCODE(String rEGIONCODE) {
		REGIONCODE = rEGIONCODE;
	}
	public String getAPPORGCODE() {
		return APPORGCODE;
	}
	public void setAPPORGCODE(String aPPORGCODE) {
		APPORGCODE = aPPORGCODE;
	}
	public String getTOORGCODE() {
		return TOORGCODE;
	}
	public void setTOORGCODE(String tOORGCODE) {
		TOORGCODE = tOORGCODE;
	}
	public String getREQUESTDATE() {
		return REQUESTDATE;
	}
	public void setREQUESTDATE(String rEQUESTDATE) {
		REQUESTDATE = rEQUESTDATE;
	}
	public String getREQUESTUSER() {
		return REQUESTUSER;
	}
	public void setREQUESTUSER(String rEQUESTUSER) {
		REQUESTUSER = rEQUESTUSER;
	}
	public String getREASONCHNDESC() {
		return REASONCHNDESC;
	}
	public void setREASONCHNDESC(String rEASONCHNDESC) {
		REASONCHNDESC = rEASONCHNDESC;
	}
	public String getURGENTFLG() {
		return URGENTFLG;
	}
	public void setURGENTFLG(String uRGENTFLG) {
		URGENTFLG = uRGENTFLG;
	}
	public String getDRUGCATEGORY() {
		return DRUGCATEGORY;
	}
	public void setDRUGCATEGORY(String dRUGCATEGORY) {
		DRUGCATEGORY = dRUGCATEGORY;
	}
	public String getOPTUSER() {
		return OPTUSER;
	}
	public void setOPTUSER(String oPTUSER) {
		OPTUSER = oPTUSER;
	}
	public String getOPTDATE() {
		return OPTDATE;
	}
	public void setOPTDATE(String oPTDATE) {
		OPTDATE = oPTDATE;
	}
	public String getOPTTERM() {
		return OPTTERM;
	}
	public void setOPTTERM(String oPTTERM) {
		OPTTERM = oPTTERM;
	}
	
	public String getUNITTYPE() {
		return UNITTYPE;
	}
	public void setUNITTYPE(String uNITTYPE) {
		UNITTYPE = uNITTYPE;
	}
	public List<SPCHisRequestDetail> getRequestDetail() {
		if (requestDetail == null) {  
			requestDetail = new ArrayList<SPCHisRequestDetail>();  
        }  
		return requestDetail;
	}
	public void setRquestDetail(List<SPCHisRequestDetail> requestDetail) {
		if (requestDetail == null) {  
			requestDetail = new ArrayList<SPCHisRequestDetail>();  
        }  
		this.requestDetail = requestDetail;
	}
     
}
