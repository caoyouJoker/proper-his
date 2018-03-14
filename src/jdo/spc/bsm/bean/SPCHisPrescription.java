package jdo.spc.bsm.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: 同步his处方信息</p>
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
	private String caseNo ; //就医序号
	@XmlElement(name = "RXNO")
	private String rxNo ; //处方签号
	@XmlElement(name = "ORDERDATE")
	private String orderDate ; //处方开立时间
	@XmlElement(name = "MRNO")
	private String mrNo ; //病案号
	@XmlElement(name = "ADMTYPE")
	private String admType ; //就医类型
	@XmlElement(name = "EXECDEPTCODE")
	private String execDeptCode ; //执行部门
	private String DEPTCODE ; //开单科室
	@XmlElement(name = "PATNAME")
	private String patName ; //病患姓名
	@XmlElement(name = "PATSEX")
	private String patSex ; //病患性别
	@XmlElement(name = "BIRTHDATE")
	private String birthDate ; //出生日期
	@XmlElement(name = "DRNAME")
	private String drName ; //开单医师姓名
	@XmlElement(name = "OPWINID")
	private String opWinId ; //领药窗口号
	@XmlElement(name = "OPTUSER")
	private String optUser ; //操作人员
	@XmlElement(name = "OPTDATE")
	private String optDate ; //操作时间
	@XmlElement(name = "OPTTERM")
	private String optTerm ; //操作端末IP
	@XmlElement(name = "RXMSG")
	private  List<SPCHisPrescriptionOrder> orderDetail; //医嘱明细
	
	public String getDeptCode() {
		return DEPTCODE;
	}
	public void setDeptCode(String deptCode) {
		DEPTCODE = deptCode;
	}
	/**
	 * 得到处方明细
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
