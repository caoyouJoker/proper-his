package jdo.spc.bsm.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
*
* <p>Title: ��������</p>
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
public class Prescription {
	private  String  OPWINID ;    //�����ն˴��ں�
	private  String  OPTYPE;      //��������
	private  String  OPIP;        //�ն�ip��ַ
	private  String  OPMANNO;     //�ն˲���Ա���
	private  String  OPMANNAME;   //�ն˲���Ա����
	@XmlElement(name = "CONSIS_PRESC_MSTVW")
	private  List<PrescriptionTableMain> main; //��������
	@XmlElement(name = "CONSIS_BASIC_DRUGSVW")
	private List<DictionarySysFee>  sysFee ;//�ֵ�ҩƷ
	@XmlElement(name = "CONSIS_PHC_STORAGEVW")//��λ�ֵ�
	private List<DictionaryStock>   stock ;
	@XmlElement(name = "CONSIS_STORAGE_APPVW")//305����
	private List<RequestBean>   request ;
	@XmlElement(name = "CONSIS_PRESC_RETVW")//301����
	private List<SendBean>   returnbean ;
	@XmlElement(name = "CONSIS_PHC_DEPTVW")//104���������ֵ�(��װ��ҩ��)
	private List<DictionaryDept>   dept ;
	@XmlElement(name = "SYS_DEPT_DTL")//�����ֵ�(HIS)
	private List<SPCHisDictionaryDept>   hisDept ;
	@XmlElement(name = "PHA_BASE_DTL")//ҩƷ�ֵ�(HIS)
	private List<SPCHisDictionaryDrug>   hisPhaBase ;
	@XmlElement(name = "REQUESTM")//�������ݶ���
	private List<SPCHisRequest>   requestHis ;
	@XmlElement(name= "DISPENSE_MAS") //�������ݶ���
	private List<SPCHisDispens>  dispens ;
	/**
	 * �����ն˴��ں�
	 * @return
	 */
	public String getOPWINID() {
		return OPWINID;
	}
	/**
	 * �����ն˴��ں�
	 * @param oPWINID
	 */
	public void setOPWINID(String oPWINID) {
		OPWINID = oPWINID;
	}
	/**
	 * ��������	201 ����
	 * @return
	 */
	public String getOPTYPE() {
		return OPTYPE;
	}
	/**
	 * ��������	201 ����
	 * @param oPTYPE
	 */
	public void setOPTYPE(String oPTYPE) {
		OPTYPE = oPTYPE;
	}
	/**
	 * �ն�ip��ַ
	 * @return
	 */
	public String getOPIP() {
		return OPIP;
	}
	/**
	 * �ն�ip��ַ
	 * @param oPIP
	 */
	public void setOPIP(String oPIP) {
		OPIP = oPIP;
	}
	/**
	 * �ն˲���Ա���
	 * @return
	 */
	public String getOPMANNO() {
		return OPMANNO;
	}
	/**
	 * �ն˲���Ա���
	 * @param oPMANNO
	 */
	public void setOPMANNO(String oPMANNO) {
		OPMANNO = oPMANNO;
	}
	/**
	 * �ն˲���Ա����
	 * @return
	 */
	public String getOPMANNAME() {
		return OPMANNAME;
	}
	/**
	 * �ն˲���Ա����
	 * @param oPMANNAME
	 */
	public void setOPMANNAME(String oPMANNAME) {
		OPMANNAME = oPMANNAME;
	}
	/**
	 * �õ���������
	 * @return
	 */
	public List<PrescriptionTableMain> getMain() {
		if (main == null) {  
			main = new ArrayList<PrescriptionTableMain>();  
        }  
        return this.main;  
    }  
	/**
	 * �õ�ҩƷ��sys_fee���ֵ�����
	 * @return
	 */
	public List<DictionarySysFee> getSysFee() {
		if (sysFee == null) {  
			sysFee = new ArrayList<DictionarySysFee>();  
        }  
        return this.sysFee;  
    }  
	/**
	 * �õ�ҩƷ�������
	 * @return
	 */
	public List<DictionaryStock> getStock() {
		if (stock == null) {  
			stock = new ArrayList<DictionaryStock>();  
        }  
        return this.stock;  
    }  
	/**
	 * �õ����ض������쵥��305
	 * @return
	 */
	public List<RequestBean> getRequest() {
		if (request == null) {  
			request = new ArrayList<RequestBean>();  
        }  
		return request;
	}
	/**
	 * ���÷��ض������쵥305
	 * @param request
	 */
	public void setRequest(List<RequestBean> request) {
		if (request == null) {  
			request = new ArrayList<RequestBean>();  
        }  
		this.request = request;
	}
	/**
	 * �õ�301���ض���
	 * @return
	 */
	public List<SendBean> getReturn() {
		if (returnbean == null) {  
			returnbean = new ArrayList<SendBean>();  
        }  
		return returnbean ;
	}
	/**
	 * ����301���ض���
	 * @param request
	 */
	public void setReturnt(List<SendBean> request) {
		if (request == null) {  
			request = new ArrayList<SendBean>();  
        }  
		this.returnbean = request;
	}
	public List<DictionaryDept> getDept() {
		if (dept == null) {  
			dept = new ArrayList<DictionaryDept>();  
        }  
		return dept;
	}
	public void setDept(List<DictionaryDept> dept) {
		if (dept == null) {  
			dept = new ArrayList<DictionaryDept>();  
        }  
		this.dept = dept;
	}
	public List<SPCHisDictionaryDept> getHisDept() {
		if (hisDept == null) {  
			hisDept = new ArrayList<SPCHisDictionaryDept>();  
        }  
		return hisDept;
	}
	public void setHisDept(List<SPCHisDictionaryDept> hisDept) {
		if (hisDept == null) {  
			hisDept = new ArrayList<SPCHisDictionaryDept>();  
        }  
		this.hisDept = hisDept;
	}
	public List<SPCHisDictionaryDrug> getHisPhaBase() {
		if (hisPhaBase == null) {  
			hisPhaBase = new ArrayList<SPCHisDictionaryDrug>();  
        }  
		return hisPhaBase;
	}
	public void setHisPhaBase(List<SPCHisDictionaryDrug> hisPhaBase) {
		if (hisPhaBase == null) {  
			hisPhaBase = new ArrayList<SPCHisDictionaryDrug>();  
        }  
		this.hisPhaBase = hisPhaBase;
	}
	public List<SPCHisRequest> getRequestHis() {
		if(requestHis==null){
			requestHis = new ArrayList<SPCHisRequest>() ;
		}
		return requestHis;
	}
	public void setRequestHis(List<SPCHisRequest> requestHis) {
		if(requestHis ==null){
			requestHis= new ArrayList<SPCHisRequest>() ;
		}
		this.requestHis = requestHis;
	}
	/**
	 * �õ�������Ϣ
	 * @return
	 */
	public List<SPCHisDispens> getDispens() {
		if(dispens ==null){
			dispens = new ArrayList<SPCHisDispens>() ;
		}
		return dispens;
	}
	/**
	 * ���ó�����Ϣ
	 * @param dispens
	 */
	public void setDispens(List<SPCHisDispens> dispens) {
		if(dispens ==null){
			dispens = new ArrayList<SPCHisDispens>() ;
		}
		this.dispens = dispens;
	}

	
}