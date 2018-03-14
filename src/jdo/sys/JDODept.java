package jdo.sys;

import com.dongyang.data.StringValue;
import com.dongyang.data.TModifiedData;
import com.dongyang.data.TParm;

/**
 * 
 * <p>
 * Title: 部门jdo
 * </p>
 * 
 * <p>
 * Description:部门jdo
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
public class JDODept extends TModifiedData  {
	/**
	 * 用户ID
	 */
	private StringValue userId=new StringValue(this);
	/**
	 * 部门代码
	 */
	private StringValue deptCode=new StringValue(this);
	/**
	 * 主科室注记
	 */
	private StringValue mainFlg=new StringValue(this);
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
	 * 得到部门代码
	 * @return deptCode String
	 */
	public String getDeptCode() {
		return deptCode.getValue();
	}
	/**
	 * 设置部门代码
	 * @parm deptCode String
	 */
	public void setDeptCode(String deptCode) {
		this.deptCode.setValue(deptCode);
	}
	/**
	 * 修改部门代码
	 * @parm deptCode String
	 */
	public void modifyDeptCode(String deptCode) {
		this.deptCode.modifyValue(deptCode);
	}
	/**
	 * 得到主科室注记
	 * @return mainFlg String
	 */
	public String getMainFlg() {
		return mainFlg.getValue();
	}
	/**
	 * 设置主科室注记
	 * @parm mainFlg String
	 */
	public void setMainFlg(String mainFlg) {
		this.mainFlg.setValue(mainFlg);
	}
	/**
	 * 修改主科室注记
	 * @parm mainFlg String
	 */
	public void modifyMainFlg(String mainFlg) {
		this.mainFlg.modifyValue(mainFlg);
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
