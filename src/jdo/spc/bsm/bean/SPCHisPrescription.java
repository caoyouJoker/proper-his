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
* @author chenx 2013.09.10 
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement(name = "ROOT") 
public class SPCHisPrescription {

	@XmlElement(name = "CASENO")
	private String caseNo ; //��ҽ���
	@XmlElement(name = "RXNO")
	private String rxNo ; //����ǩ��
	@XmlElement(name = "ORDERDATE")
	private String orderDate ; //��������ʱ��
	@XmlElement(name = "MRNO")
	private String mrNo ; //������
	@XmlElement(name = "ADMTYPE")
	private String admType ; //��ҽ����
	@XmlElement(name = "EXECDEPTCODE")
	private String execDeptCode ; //ִ�в���
	private String DEPTCODE ; //��������
	@XmlElement(name = "PATNAME")
	private String patName ; //��������
	@XmlElement(name = "PATSEX")
	private String patSex ; //�����Ա�
	@XmlElement(name = "BIRTHDATE")
	private String birthDate ; //��������
	@XmlElement(name = "DRNAME")
	private String drName ; //����ҽʦ����
	@XmlElement(name = "OPWINID")
	private String opWinId ; //��ҩ���ں�
	@XmlElement(name = "OPTUSER")
	private String optUser ; //������Ա
	@XmlElement(name = "OPTDATE")
	private String optDate ; //����ʱ��
	@XmlElement(name = "OPTTERM")
	private String optTerm ; //������ĩIP
	@XmlElement(name = "RXMSG")
	private  List<SPCHisPrescriptionOrder> orderDetail; //ҽ����ϸ
	
	public String getDeptCode() {
		return DEPTCODE;
	}
	public void setDeptCode(String deptCode) {
		DEPTCODE = deptCode;
	}
	/**
	 * �õ�������ϸ
	 * @return
	 */
	 public List<SPCHisPrescriptionOrder> getOrderDetail() {  
	        if (orderDetail == null) {  
	        	orderDetail = new ArrayList<SPCHisPrescriptionOrder>();  
	        }  
	        return this.orderDetail;  
	    }
	public String getCaseNo() {
		return caseNo;
	}
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	public String getRxNo() {
		return rxNo;
	}
	public void setRxNo(String rxNo) {
		this.rxNo = rxNo;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getMrNo() {
		return mrNo;
	}
	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}
	public String getAdmType() {
		return admType;
	}
	public void setAdmType(String admType) {
		this.admType = admType;
	}
	public String getExecDeptCode() {
		return execDeptCode;
	}
	public void setExecDeptCode(String execDeptCode) {
		this.execDeptCode = execDeptCode;
	}
	public String getPatName() {
		return patName;
	}
	public void setPatName(String patName) {
		this.patName = patName;
	}
	public String getPatSex() {
		return patSex;
	}
	public void setPatSex(String patSex) {
		this.patSex = patSex;
	}
	public String getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	public String getDrName() {
		return drName;
	}
	public void setDrName(String drName) {
		this.drName = drName;
	}
	public String getOpWinId() {
		return opWinId;
	}
	public void setOpWinId(String opWinId) {
		this.opWinId = opWinId;
	}
	public String getOptUser() {
		return optUser;
	}
	public void setOptUser(String optUser) {
		this.optUser = optUser;
	}
	public String getOptDate() {
		return optDate;
	}
	public void setOptDate(String optDate) {
		this.optDate = optDate;
	}
	public String getOptTerm() {
		return optTerm;
	}
	public void setOptTerm(String optTerm) {
		this.optTerm = optTerm;
	}

	
}
