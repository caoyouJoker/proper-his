package jdo.sys;

import java.sql.Timestamp;

import com.dongyang.data.StringValue;
import com.dongyang.data.TModifiedData;
import com.dongyang.data.TParm;
import com.dongyang.data.TimestampValue;
/**
 * 
 * <p>
 * Title: 证照jdo
 * </p>
 * 
 * <p>
 * Description:证照jdo
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
 * @author ehui 20081111
 * @version 1.0
 */
public class JDOLicense extends TModifiedData {
	/**
	 * 用户ID
	 */
	private StringValue userId=new StringValue(this);
	/**
	 * 证照类别
	 */
	private StringValue lcsClassCode=new StringValue(this);
	/**
	 * 证照号
	 */
	private StringValue lcsNo=new StringValue(this);
	/**
	 * 生效日期
	 */
	private TimestampValue effLcsDate=new TimestampValue(this);
	/**
	 * 失效日期
	 */
	private TimestampValue endLcsDate=new TimestampValue(this);
	/**
	 * 操作用户
	 */
	private StringValue optUser=new StringValue(this);
	/**
	 * 操作端末
	 */
	private StringValue optTerm=new StringValue(this);
	/**
	 * 得到用户代码
	 * @return userId String
	 */
	public String getUserId() {
		return userId.getValue();
	}
	/**
	 * 设置用户代码
	 * @parm userId String
	 */
	public void setUserId(String userId) {
		this.userId.setValue(userId);
	}
	/**
	 * 修改用户代码
	 * @parm userId String
	 */
	public void modifyUserId(String userId) {
		this.userId.modifyValue(userId);
	}
	/**
	 * 得到证照类别
	 * @return lcsClassCode String
	 */
	public String getLcsClassCode() {
		return lcsClassCode.getValue();
	}
	/**
	 * 设置证照类别
	 * @parm lcsClassCode String
	 */
	public void setLcsClassCode(String lcsClassCode) {
		this.lcsClassCode.setValue(lcsClassCode);
	}
	/**
	 * 修改证照类别
	 * @parm deptCode String
	 */
	public void modifyLcsClassCode(String lcsClassCode) {
		this.lcsClassCode.modifyValue(lcsClassCode);
	}
	/**
	 * 得到证照号
	 * @return lcsNo String
	 */
	public String getLcsNo() {
		return lcsNo.getValue();
	}
	/**
	 * 设置证照号
	 * @parm lcsNo String
	 */
	public void setLcsNo(String lcsNo) {
		this.lcsNo.setValue(lcsNo);
	}
	/**
	 * 修改证照号
	 * @parm lcsNo String
	 */
	public void modifyLcsNo(String lcsNo) {
		this.lcsNo.modifyValue(lcsNo);
	}
	/**
	 * 得到生效日期
	 * @return endLcsDate Timestamp
	 */
	public Timestamp getEffLcsDate(){
		return this.effLcsDate.getValue();
	}
	/**
	 * 设置生效日期
	 * @param effLcsDate Timestamp
	 */
	public void setEffLcsDate(Timestamp effLcsDate){
		this.effLcsDate.setValue(effLcsDate);
	}
	/**
	 * 修改生效日期
	 * @param effLcsDate Timestamp
	 */
	public void modifyEffLcsDate(Timestamp effLcsDate){
		this.effLcsDate.modifyValue(effLcsDate);
	}
	/**
	 * 得到失效日期
	 * @return endLcsDate Timestamp
	 */
	public Timestamp getEndLcsDate(){
		return this.endLcsDate.getValue();
	}
	/**
	 * 设置失效日期
	 * @param endLcsDate Timestamp
	 */
	public void setEndLcsDate(Timestamp endLcsDate){
		this.endLcsDate.setValue(endLcsDate);
	}
	/**
	 * 修改失效日期
	 * @param endLcsDate Timestamp
	 */
	public void modifyEndLcsDate(Timestamp endLcsDate){
		this.endLcsDate.modifyValue(endLcsDate);
	}
	/**
	 * 得到操作用户
	 * @return optUser String
	 */
	public String getOptUser() {
		return optUser.getValue();
	}
	/**
	 * 设置操作用户
	 * @parm optUser String
	 */
	public void setOptUser(String optUser) {
		this.optUser.setValue(optUser);
	}
	/**
	 * 修改操作用户
	 * @parm optUser String
	 */
	public void modifyOptUser(String optUser) {
		this.optUser.modifyValue(optUser);
	}
	/**
	 * 得到操作端末
	 * @return optTerm String
	 */
	public String getOptTerm() {
		return optTerm.getValue();
	}
	/**
	 * 设置操作端末
	 * @parm optTerm String
	 */
	public void setOptTerm(String optTerm) {
		this.optTerm.setValue(optTerm);
	}
	/**
	 * 修改操作端末
	 * @parm optTerm String
	 */
	public void modifyOptTerm(String optTerm) {
		this.optTerm.modifyValue(optTerm);
	}
	/**
	 * 得到parm
	 */
	public TParm getParm() {
		TParm result = super.getParm();
		return result;
	}
}
