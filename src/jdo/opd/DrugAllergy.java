package jdo.opd;

import java.sql.Timestamp;

import jdo.sys.Pat;

import com.dongyang.data.StringValue;
import com.dongyang.data.TModifiedData;
import com.dongyang.data.TParm;
import com.dongyang.data.TimestampValue;

/**
 * 
 * <p>
 * Title: 过敏jdo
 * </p>
 * 
 * <p>
 * Description:过敏jdo
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author ehui 200800909
 * @version 1.0
 */
public class DrugAllergy extends TModifiedData {
	/**
	 * {@literal病患信息}
	 */
	private Pat pat;
	/**
	 * 病歷號
	 */
	private StringValue mrNo = new StringValue(this);
	/**
	 * 看诊日
	 */
	private StringValue admDate = new StringValue(this);
	/**
	 * 过敏种类 A：成份 B：药品 C：其它
	 */
	private StringValue drugType = new StringValue(this);
	/**
	 * 过敏源
	 */
	private StringValue drugOringrdCode= new StringValue(this);
	/**
	 * 门急住别
	 */
	private StringValue admType = new StringValue(this);
	/**
	 * 看诊序号
	 */
	private StringValue caseNo= new StringValue(this);
	/**
	 * 科别代码
	 */
	private StringValue deptCode= new StringValue(this);
	/**
	 * 医师代码
	 */
	private StringValue drCode= new StringValue(this);
	/**
	 * 過敏情形
	 */
	private StringValue allergyNote= new StringValue(this);
	/**
	 * 操作人員
	 */
	private StringValue optUser= new StringValue(this);
	/**
	 * 操作日期
	 */
	private TimestampValue optDate=new TimestampValue(this);
	/**
	 * 操作端末
	 */
	private StringValue optTerm= new StringValue(this);
	/**
	 * 病患信息
	 * 
	 * @param pat
	 */
	public void setPat(Pat pat) {
		this.pat = pat;
	}

	/**
	 * 病患信息
	 * 
	 * @return Pat
	 */
	public Pat getPat() {
		return pat;
	}

	public String getMrNo() {
		return mrNo.getValue();
	}

	public void setMrNO(String mrNO) {
		this.mrNo.setValue(mrNO);
	}

	public String getAdmDate() {
		return admDate.getValue();
	}

	public void setAdmDate(String admDate) {
		this.admDate.setValue(admDate);
	}

	public String getDrugType() {
		return drugType.getValue();
	}

	public void setDrugType(String drugType) {
		this.drugType.setValue(drugType);
	}

	public String getDrugOringrdCode() {
		return drugOringrdCode.getValue();
	}

	public void setDrugOringrdCode(String drugOringrdCode) {
		this.drugOringrdCode.setValue(drugOringrdCode);
	}


	public String getAdmType() {
		return admType.getValue();
	}

	public void setAdmType(String admType) {
		this.admType.setValue(admType);
	}

	public String getCaseNo() {
		return caseNo.getValue();
	}

	public void setCaseNo(String caseNo) {
		this.caseNo.setValue(caseNo);
	}

	public String getDeptCode() {
		return deptCode.getValue();
	}

	public void setDeptCode(String deptCode) {
		this.deptCode.setValue(deptCode);
	}

	public String getDrCode() {
		return drCode.getValue();
	}

	public void setDrCode(String drCode) {
		this.drCode.setValue(drCode);
	}

	public String getAllergyNote() {
		return allergyNote.getValue();
	}

	public void setAllergyNote(String allergyNote) {
		this.allergyNote.setValue(allergyNote);
	}

	public String getOptUser() {
		return optUser.getValue();
	}

	public void setOptUser(String optUser) {
		this.optUser.setValue(optUser);
	}

	public Timestamp getOptDate() {
		return optDate.getValue();
	}

	public void setOptDate(Timestamp optDate) {
		this.optDate.setValue(optDate);
	}

	public String getOptTerm() {
		return optTerm.getValue();
	}

	public void setOptTerm(String optTerm) {
		this.optTerm.setValue(optTerm);
	}

	/**
	 * 病歷號
	 * @param mr_no 
	 */
	public void modifyMrNo(String mrNo) {
		this.mrNo.modifyValue(mrNo);
	}

	/**
	 * 看诊日
	 * @param adm_date 
	 */
	public void modifyAdmDate(String admDate) {
		this.admDate.modifyValue(admDate);
	}

	/**
	 * 过敏种类 A：成份 B：药品 C：其它
	 * @param drug_type 
	 */
	public void modifyDrugType(String drugType) {
		this.drugType.modifyValue(drugType);
	}

	/**
	 * 过敏源
	 * @param drugoringrd_code 
	 */
	public void modifyDrugoringrdCode(String drugoringrdCode) {
		this.drugOringrdCode.modifyValue(drugoringrdCode);
	}

	/**
	 * 门急住别
	 * @param adm_type 
	 */
	public void modifyAdmType(String admType) {
		this.admType.modifyValue(admType);
	}

	/**
	 * 看诊序号
	 * @param case_no 
	 */
	public void modifyCaseNo(String caseNo) {
		this.caseNo.modifyValue(caseNo);
	}

	/**
	 * 科别代码
	 * @param dept_code 
	 */
	public void modifyDeptCode(String deptCode) {
		this.deptCode.modifyValue(deptCode);
	}

	/**
	 * 医师代码
	 * @param dr_code 
	 */
	public void modifyDrCode(String drCode) {
		this.drCode.modifyValue(drCode);
	}

	/**
	 * 過敏情形
	 * @param allergy_note 
	 */
	public void modifyAllergyNote(String allergyNote) {
		this.allergyNote.modifyValue(allergyNote);
	}

	/**
	 * 操作人員
	 * @param opt_user 
	 */
	public void modifyOptUser(String optUser) {
		this.optUser.modifyValue(optUser);
	}

	/**
	 * 操作日期
	 * @param opt_date 
	 */
	public void modifyOptDate(Timestamp optDate) {
		this.optDate.modifyValue(optDate);
	}

	/**
	 * 操作端末
	 * @param opt_term 
	 */
	public void modifyOptTerm(String optTerm) {
		this.optTerm.modifyValue(optTerm);
	}
	/**
	 * 取得pram
	 */
	public TParm getParm()
    {
        TParm result = super.getParm();
        if(getPat() != null)
            result.setData("MR_NO",pat.getMrNo());
        return result;
    }
	
}
