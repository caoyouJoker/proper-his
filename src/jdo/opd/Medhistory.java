package jdo.opd;

import java.sql.Timestamp;

import jdo.sys.Pat;

import com.dongyang.data.StringValue;
import com.dongyang.data.TModifiedData;
import com.dongyang.data.TParm;
import com.dongyang.data.TimestampValue;

/**
 *
 * <p>Title: 既往史</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author lizk
 * @version 1.0
 */
public class Medhistory extends TModifiedData{
    private Pat pat;
    /**
     * 疾病诊断代码
     */
    private StringValue icdCode = new StringValue(this);	
    /**
     * 看诊日
     */	
    private StringValue admDate= new StringValue(this);
    /**
     * 诊断类别 西医:W 中医:C
     */
    private StringValue icdType = new StringValue(this);
    /**
     * 备注
     */
    private StringValue description = new StringValue(this);
    /**
     * 部门代码
     */
    private StringValue deptCode = new StringValue(this);
    /**
     * 医生代码
     */
    private StringValue drCode = new StringValue(this);
    /**
     * 门急住别
     */
    private StringValue admType = new StringValue(this);
    /**
     * 看诊号
     */
    private StringValue caseNo = new StringValue(this);
    /**
     * 操作用户
     */
    private StringValue optUser = new StringValue(this);
    /**
     * 操作日期
     */
    private TimestampValue optDate= new TimestampValue(this) ;
    /**
     * 操作端末
     */
    private StringValue optTerm = new StringValue(this);
    /**
     * 设置病患对象
     * @param pat
     */
    public void setPat(Pat pat)
    {
        this.pat = pat;
    }
    /**
     * 得到病患对象
     * @return Pat
     */
    public Pat getPat()
    {
        return pat;
    }
   /**
    * 得到诊断码
    * @return icdCode String
    */
    public String getIcdCode() {
		return icdCode.getValue();
	}
    /**
     * 设置疾病诊断码
     * @param icdCode String
     */
	public void setIcdCode(String icdCode) {
		this.icdCode.setValue(icdCode);
	}
	/**
	 * 修改疾病诊断码
	 * @param icdCode String
	 */
	public void modifyIcdCode(String icdCode){
		this.icdCode.modifyValue(icdCode);
	}
	/**
	 * 得到诊断日期
	 * @return admdate String
	 */
	public String getAdmDate() {
		return admDate.getValue();
	}
	/**
	 * 设置诊断日期
	 * @param admDate String
	 */
	public void setAdmDateTime(String admDate) {
		this.admDate.setValue(admDate);
	}
	/**
	 * 修改诊断日期
	 * @param admDate String
	 */
	public void modifyAdmDate(String admDate){
		this.admDate.modifyValue(admDate);
	}
	/**
	 * 得到诊断类型
	 * @return icdType String
	 */
	public String getIcdType() {
		return icdType.getValue();
	}
	/**
	 * 设置诊断类型
	 * @param icdType String
	 */
	public void setIcdType(String icdType) {
		this.icdType.setValue(icdType);
	}
	/**
	 * 修改诊断类型
	 * @param icdType String
	 */
	public void modifyIcdType(String icdType){
		this.icdType.modifyValue(icdType);
	}
	/**
	 * 得到备注
	 * @return description String
	 */
	public String getDescription() {
		return description.getValue();
	}
	/**
	 * 设置备注
	 * @param description String
	 */
	public void setDescription(String description) {
		this.description.setValue(description);
	}
	/**
	 * 修改备注
	 * @param description String
	 */
	public void modifyDescription(String description){
		this.description.modifyValue(description);
	}
	/**
	 * 得到部门代码
	 * @return deptCode String
	 */
	public String getDeptCode() {
		return deptCode.getValue();
	}
	/**
	 * 设置部门代码
	 * @param deptCode String
	 */
	public void setDeptCode(String deptCode) {
		this.deptCode.setValue(deptCode);
	}
	/**
	 * 修改部门代码
	 * @param deptCode String
	 */
	public void modifyDeptCode(String deptCode){
		this.deptCode.modifyValue(deptCode);
	}
	/**
	 * 得到医师代码
	 * @return drCode String
	 */
	public String getDrCode() {
		return drCode.getValue();
	}
	/**
	 * 设置医师代码
	 * @param drCode String
	 */
	public void setDrCode(String drCode) {
		this.drCode.setValue(drCode);
	}
	/**
	 * 修改医师代码
	 * @param drCode String
	 */
	public void modifyDrCode(String drCode){
		this.drCode.modifyValue(drCode);
	}
	/**
	 * 得到门急别
	 * @return admType String
	 */
	public String getAdmType() {
		return admType.getValue();
	}
	/**
	 * 设置门急别
	 * @param admType String
	 */
	public void setAdmType(String admType) {
		this.admType.setValue(admType);
	}
	/**
	 * 修改门急别
	 * @param admType String
	 */
	public void modifyAdmType(String admType){
		this.admType.modifyValue(admType);
	}
	/**
	 * 得到看诊号
	 * @return caseNo String
	 */
	public String getCaseNo() {
		return caseNo.getValue();
	}
	/**
	 * 设置看诊号
	 * @param caseNo String
	 */
	public void setCaseNo(String caseNo) {
		this.caseNo.setValue(caseNo);
	}
	/**
	 * 修改看诊号
	 * @param caseNo String
	 */
	public void modifyCaseNo(String caseNo){
		this.caseNo.modifyValue(caseNo);
	}
	/**
	 * 得到操作用户
	 * @return OptUser String
	 */
	public String getOptUser() {
		return optUser.getValue();
	}
	/**
	 * 设置操作用户
	 * @param optUser String
	 */
	public void setOptUser(String optUser) {
		this.optUser.setValue(optUser);
	}
	/**
	 * 修改操作用户
	 * @param optUser String
	 */
	public void modifyOptUser(String optUser){
		this.optUser.modifyValue(optUser);
	}
	/**
	 * 得到操作时间
	 * @return Optdate Timestamp
	 */
	public Timestamp getOptDate() {
		return optDate.getValue();
	}
	/**
	 * 设置操作时间
	 * @param optDate Timestamp
	 */
	public void setOptDate(Timestamp optDate) {
		this.optDate.setValue(optDate);
	}
	/**
	 * 修改操作时间
	 * @param optDate Timestamp
	 */
	public void modifyOptDate(Timestamp optDate) {
		this.optDate.modifyValue(optDate);
	}
	/**
	 * 得到操作端末
	 * @return OptTerm String
	 */
	public String getOptTerm() {
		return optTerm.getValue();
	}
	/**
	 * 设置操作端末
	 * @param optTerm String
	 */
	public void setOptTerm(String optTerm) {
		this.optTerm.setValue(optTerm);
	}
	/**
	 * 修改操作端末
	 * @param optTerm String
	 */
	public void modifyOptTerm(String optTerm){
		this.optTerm.modifyValue(optTerm);
	}
	public TParm getParm()
    {
        TParm result = super.getParm();
        if(getPat() != null)
            result.setData("MR_NO",pat.getMrNo());
        return result;
    }
	
}
