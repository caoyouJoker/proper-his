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
 * Title: 诊断jdo
 * </p>
 * 
 * <p>
 * Description:诊断jdo
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
public class DiagRec extends TModifiedData {
	/**
	 * 病患信息
	 */
	private Pat pat;
	/**
	 * 看诊序号
	 */
	private StringValue caseNo = new StringValue(this);
	/**
	 * 诊断码
	 */
	private StringValue icdCode = new StringValue(this);
	/**
	 * 诊断类别 西医:W 中医:C'
	 */
	private StringValue icdType = new StringValue(this);
	/**
	 * 主診斷註記
	 */
	private StringValue mainDiagFlg= new StringValue(this);
	/**
	 * 门急住别
	 */
	private StringValue admType= new StringValue(this);
	/**
	 * 診斷附註
	 */
	private StringValue diagNote = new StringValue(this);
	/**
	 * 开立医师
	 */
	private StringValue drCode = new StringValue(this);
	/**
	 * 开立时间
	 */
	private TimestampValue orderDate = new TimestampValue(this);
	/**
	 * 结构化疾病报告卡序号(MR_FILE_INDEX.FILE_NO)
	 */
	private StringValue fileNO= new StringValue(this);
	/**
	 * 操作人員
	 */
	private StringValue optUser= new StringValue(this);
	/**
	 * 操作日期
	 */
	private TimestampValue optDate= new TimestampValue(this);
	/**
	 * 操作端末
	 */
	private StringValue optTerm = new StringValue(this);

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

	/**
	 * 看诊序号
	 * 
	 * @return the cASE_NO String
	 */
	public String getCaseNo() {
		return caseNo.getValue();
	}

	/**
	 * 看诊序号
	 * 
	 * @param case_no
	 */
	public void setCaseNo(String caseNo) {
		this.caseNo.setValue(caseNo);
	}

	/**
	 * 看诊序号
	 * 
	 * @param case_no
	 */
	public void modifyCaseNo(String caseNo) {
		this.caseNo.modifyValue(caseNo);
	}

	/**
	 * 诊断码
	 * 
	 * @return the iCD_CODE String
	 */
	public String getIcdCode() {
		return icdCode.getValue();
	}

	/**
	 * 诊断码
	 * 
	 * @param icd_code
	 */
	public void setIcdCode(String icdCode) {
		this.icdCode.setValue(icdCode);
	}

	/**
	 * 诊断码
	 * 
	 * @param icd_code
	 */
	public void modifyIcdCode(String ii) {
		this.icdCode.modifyValue(ii);
	}

	/**
	 * 诊断类别 西医:W 中医:C
	 * 
	 * @return the icdType String
	 */
	public String getIcdType() {
		return icdType.getValue();
	}

	/**
	 * 诊断类别 西医:W 中医:C
	 * 
	 * @param icdType
	 */
	public void setIcdType(String icdType) {
		this.icdType.setValue(icdType);
	}

	/**
	 * 诊断类别 西医:W 中医:C
	 * 
	 * @param icdType
	 */
	public void modifyIcdType(String icdType) {
		this.icdType.modifyValue(icdType);
	}

	/**
	 * 主診斷註記
	 * 
	 * @return the mainDiagFlg String
	 */
	public String getMainDiagFlg() {
		return mainDiagFlg.getValue();
	}

	/**
	 * 主診斷註記
	 * 
	 * @param mainDiagFlg
	 */
	public void setMainDiagFlg(String mainDiagFlg) {
		this.mainDiagFlg.setValue(mainDiagFlg);
	}

	/**
	 * 主診斷註記
	 * 
	 * @param mainDiagFlg
	 */
	public void modifyMainDiagFlg(String mainDiagFlg) {
		this.mainDiagFlg.modifyValue(mainDiagFlg);
	}

	/**
	 * 门急住别
	 * 
	 * @return the admType String
	 */
	public String getAdmType() {
		return admType.getValue();
	}

	/**
	 * 门急住别
	 * 
	 * @param admType
	 */
	public void setAdmType(String admType) {
		this.admType.setValue(admType);
	}

	/**
	 * 门急住别
	 * 
	 * @param admType
	 */
	public void modifyAdmType(String admType) {
		this.admType.modifyValue(admType);
	}

	
	/**
	 * 診斷附註
	 * 
	 * @return the diagNote String
	 */
	public String getDiagNote() {
		return diagNote.getValue();
	}

	/**
	 * 診斷附註
	 * 
	 * @param diag_note
	 */
	public void setDiagNote(String diagNote) {
		this.diagNote.setValue(diagNote);
	}

	/**
	 * 診斷附註
	 * 
	 * @param diagNote
	 */
	public void modifyDiagNote(String diagNote) {
		this.diagNote.modifyValue(diagNote);
	}

	/**
	 * 开立医师
	 * 
	 * @return the drCode String
	 */
	public String getDrCode() {
		return drCode.getValue();
	}

	/**
	 * 开立医师
	 * 
	 * @param drCode
	 */
	public void setDrCode(String drCode) {
		this.drCode.setValue(drCode);
	}

	/**
	 * 开立医师
	 * 
	 * @param drCode
	 */
	public void modifyDrCode(String drCode) {
		this.drCode.modifyValue(drCode);
	}

	/**
	 * 开立时间
	 * 
	 * @return the orderDate Timestamp
	 */
	public Timestamp getOrderDate() {
		return orderDate.getValue();
	}

	/**
	 * 开立时间
	 * 
	 * @param orderDate Timestamp
	 */
	public void setOrderDate(Timestamp orderDate) {
		this.orderDate.setValue(orderDate);
	}

	/**
	 * 开立时间
	 * 
	 * @param orderDate Timestamp
	 */
	public void modifyOrderDate(Timestamp orderDate) {
		this.orderDate.modifyValue(orderDate);
	}

	/**
	 * 结构化疾病报告卡序号(MR_FILE_INDEX.FILE_NO)
	 * 
	 * @return the fileNO String
	 */
	public String getFileNO() {
		return fileNO.getValue();
	}

	/**
	 * 结构化疾病报告卡序号(MR_FILE_INDEX.FILE_NO)
	 * 
	 * @param fileNO
	 */
	public void setFileNO(String fileNO) {
		this.fileNO.setValue(fileNO);
	}

	/**
	 * 结构化疾病报告卡序号(MR_FILE_INDEX.FILE_NO)
	 * 
	 * @param fileNO
	 */
	public void modifyFileNO(String fileNO) {
		this.fileNO.modifyValue(fileNO);
	}

	/**
	 * 操作人員
	 * 
	 * @return the optUser String
	 */
	public String getOptUser() {
		return optUser.getValue();
	}

	/**
	 * 操作人員
	 * 
	 * @param optUser
	 */
	public void setOptUser(String optUser) {
		this.optUser.setValue(optUser);
	}

	/**
	 * 操作人員
	 * 
	 * @param optUser
	 */
	public void modifyOptUser(String optUser) {
		this.optUser.modifyValue(optUser);
	}

	/**
	 * 操作日期
	 * 
	 * @return the optDate String
	 */
	public Timestamp getOptDate() {
		return optDate.getValue();
	}

	/**
	 * 操作日期
	 * 
	 * @param optDate
	 */
	public void setOptDate(Timestamp optDate) {
		this.optDate.setValue(optDate);
	}

	/**
	 * 操作日期
	 * 
	 * @param optDate
	 */
	public void modifyOptDate(Timestamp optDate) {
		this.optDate.modifyValue(optDate);
	}

	/**
	 * 操作端末
	 * 
	 * @return the optTerm String
	 */
	public String getOptTerm() {
		return optTerm.getValue();
	}

	/**
	 * 操作端末
	 * 
	 * @param optTerm
	 */
	public void setOptTerm(String optTerm) {
		this.optTerm.setValue(optTerm);
	}

	/**
	 * 操作端末
	 * 
	 * @param optTerm
	 */
	public void modifyOptTerm(String optTerm) {
		this.optTerm.modifyValue(optTerm);
	}
	/**
	 * 得到parm
	 */
	public TParm getParm() {
		TParm result = super.getParm();
		if (getPat() != null)
			result.setData("MR_NO", pat.getMrNo());
		return result;
	}
}
