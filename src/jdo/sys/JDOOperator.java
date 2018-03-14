package jdo.sys;

import java.sql.Timestamp;

import com.dongyang.data.StringValue;
import com.dongyang.data.TModifiedData;
import com.dongyang.data.TParm;
import com.dongyang.data.TimestampValue;

/**
 * 
 * <p>
 * Title: 用户JDO
 * </p>
 * 
 * <p>
 * Description:用户JDO
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
public class JDOOperator extends TModifiedData {
	private JDODeptList deptlist;
	/**
	 * 用户代码
	 */
	private StringValue userId=new StringValue(this);
	/**
	 * 用户名称
	 */
	private StringValue userName=new StringValue(this);
	/**
	 * 拼音1
	 */
	private StringValue py1=new StringValue(this);
	/**
	 * 拼音2
	 */
	private StringValue py2=new StringValue(this);
	/*
	 * 部门显示用
	 */
	private StringValue deptCode=new StringValue(this);
	/**
	 * 身份证
	 */
	private StringValue idCode=new StringValue(this);
	/**
	 * 性别代码
	 */
	private StringValue sexCode=new StringValue(this);
	/**
	 * 密码
	 */
	private StringValue userPassword=new StringValue(this);
	/**
	 * 职别代码
	 */
	private StringValue posCode=new StringValue(this);
	/**
	 * 角色代码
	 */
	private StringValue roleId=new StringValue(this);
	/**
	 * 生效日期
	 */
	private TimestampValue activeDate=new TimestampValue(this);
	/**
	 * 失效日期
	 */
	private TimestampValue endDate=new TimestampValue(this);
	/**
	 * 默认进入程序
	 */
	private StringValue pubFunction=new StringValue(this);
	/**
	 * EMAIL
	 */
	private StringValue email=new StringValue(this);
	/**
	 * 证照号
	 */
	private StringValue lcsNo=new StringValue(this);
	/**
	 * 证照生效日期
	 */
	private TimestampValue effLcsDate=new TimestampValue(this);
	/**
	 * 证照失效日期
	 */
	private TimestampValue endLcsDate=new TimestampValue(this);
	/**
	 * 全职注记
	 */
	private StringValue fullTimeFlg=new StringValue(this);
	/**
	 * 控制药品权限
	 */
	private StringValue ctrlFlg=new StringValue(this);
	/**
	 * 区域代码
	 */
	private StringValue regionCode=new StringValue(this);
	/**
	 * 最后登入日期
	 */
	private TimestampValue rcntLoginDate=new TimestampValue(this);
	/**
	 * 最后登出日期
	 */
	private TimestampValue rcntLogoutDate=new TimestampValue(this);
	/**
	 * 最后登出IP
	 */
	private StringValue rcntIp=new StringValue(this);
	/**
	 * 操作人员
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
	 * 设置用户代码
	 * @parm userId String
	 */
	public void modifyUserId(String userId) {
		this.userId.modifyValue(userId);
	}
	/**
	 * 得到用户名称
	 * @return userName String
	 */
	public String getUserName() {
		return userName.getValue();
	}
	/**
	 * 设置用户名称
	 * @parm userName String
	 */
	public void setUserName(String userName) {
		this.userName.setValue(userName);
	}
	/**
	 * 修改用户名称
	 * @parm userName String
	 */
	public void modifyUserName(String userName) {
		this.userName.modifyValue(userName);
	}
	/**
	 * 得到拼音1
	 * @return py1 String
	 */
	public String getPy1() {
		return py1.getValue();
	}
	/**
	 * 设置拼音1
	 * @parm py1 String
	 */
	public void setPy1(String py1) {
		this.py1.setValue(py1);
	}
	/**
	 * 修改拼音1
	 * @parm py1 String
	 */
	public void modifyPy1(String py1) {
		this.py1.modifyValue(py1);
	}
	/**
	 * 得到拼音2
	 * @return py2 String
	 */
	public String getPy2() {
		return py2.getValue();
	}
	/**
	 * 设置拼音2
	 * @parm py2 String
	 */
	public void setPy2(String py2) {
		this.py2.setValue(py2);
	}
	/**
	 * 修改拼音2
	 * @parm py2 String
	 */
	public void modifyPy2(String py2) {
		this.py2.modifyValue(py2);
	}
	/**
	 * 得到身份证
	 * @return idCode String
	 */
	public String getIdCode() {
		return idCode.getValue();
	}
	/**
	 * 设置身份证
	 * @parm idCode String
	 */
	public void setIdCode(String idCode) {
		this.idCode.setValue(idCode);
	}
	/**
	 * 修改身份证
	 * @parm idCode String
	 */
	public void modifyIdCode(String idCode) {
		this.idCode.modifyValue(idCode);
	}
	/**
	 * 得到性别代码
	 * @return sexCode String
	 */
	public String getSexCode() {
		return sexCode.getValue();
	}
	/**
	 * 设置性别代码
	 * @parm sexCode String
	 */
	public void setSexCode(String sexCode) {
		this.sexCode.setValue(sexCode);
	}
	/**
	 * 修改性别代码
	 * @parm sexCode String
	 */
	public void modifySexCode(String sexCode) {
		this.sexCode.modifyValue(sexCode);
	}
	/**
	 * 得到密码
	 * @return userPassword String
	 */
	public String getUserPassword() {
		return userPassword.getValue();
	}
	/**
	 * 设置密码
	 * @parm userPassword String
	 */
	public void setUserPassword(String userPassword) {
		this.userPassword.setValue(userPassword);
	}
	/**
	 * 修改密码
	 * @parm userPassword String
	 */
	public void modifyUserPassword(String userPassword) {
		this.userPassword.modifyValue(userPassword);
	}
	/**
	 * 得到职别代码
	 * @return posCode String
	 */
	public String getPosCode() {
		return posCode.getValue();
	}
	/**
	 * 设置职别代码
	 * @parm posCode String
	 */
	public void setPosCode(String posCode) {
		this.posCode.setValue(posCode);
	}
	/**
	 * 修改职别代码
	 * @parm posCode String
	 */
	public void modifyPosCode(String posCode) {
		this.posCode.modifyValue(posCode);
	}
	/**
	 * 得到角色代码
	 * @return roleId String
	 */
	public String getRoleId() {
		return roleId.getValue();
	}
	/**
	 * 设置角色代码
	 * @parm roleId String
	 */
	public void setRoleId(String roleId) {
		this.roleId.setValue(roleId);
	}
	/**
	 * 修改角色代码
	 * @parm roleId String
	 */
	public void modifyRoleId(String roleId) {
		this.roleId.modifyValue(roleId);
	}
	/**
	 * 得到生效日期
	 * @return activeDate Timestamp
	 */
	public Timestamp getActiveDate() {
		return activeDate.getValue();
	}
	/**
	 * 设置生效日期
	 * @parm activeDate Timestamp
	 */
	public void setActiveDate(Timestamp activeDate) {
		this.activeDate.setValue(activeDate);
	}
	/**
	 * 修改生效日期
	 * @parm activeDate Timestamp
	 */
	public void modifyActiveDate(Timestamp activeDate) {
		this.activeDate.modifyValue(activeDate);
	}
	/**
	 * 得到失效日期
	 * @return endDate Timestamp
	 */
	public Timestamp getEndDate() {
		return endDate.getValue();
	}
	/**
	 * 设置失效日期
	 * @parm endDate Timestamp
	 */
	public void setEndDate(Timestamp endDate) {
		this.endDate.setValue(endDate);
	}
	/**
	 * 修改失效日期
	 * @parm endDate Timestamp
	 */
	public void modifyEndDate(Timestamp endDate) {
		this.endDate.modifyValue(endDate);
	}
	/**
	 * 得到默认进入程序
	 * @return pubFunction String
	 */
	public String getPubFunction() {
		return pubFunction.getValue();
	}
	/**
	 * 设置默认进入程序
	 * @parm pubFunction String
	 */
	public void setPubFunction(String pubFunction) {
		this.pubFunction.setValue(pubFunction);
	}
	/**
	 * 修改默认进入程序
	 * @parm pubFunction String
	 */
	public void modifyPubFunction(String pubFunction) {
		this.pubFunction.modifyValue(pubFunction);
	}
	/**
	 * 得到EMAIL
	 * @return email String
	 */
	public String getEmail() {
		return email.getValue();
	}
	/**
	 * 设置EMAIL
	 * @parm email String
	 */
	public void setEmail(String email) {
		this.email.setValue(email);
	}
	/**
	 * 修改EMAIL
	 * @parm email String
	 */
	public void modifyEmail(String email) {
		this.email.modifyValue(email);
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
	 * 得到证照生效日期
	 * @return effLcsDate Timestamp
	 */
	public Timestamp getEffLcsDate() {
		return effLcsDate.getValue();
	}
	/**
	 * 设置证照生效日期
	 * @parm effLcsDate Timestamp
	 */
	public void setEffLcsDate(Timestamp effLcsDate) {
		this.effLcsDate.setValue(effLcsDate);
	}
	/**
	 * 修改证照生效日期
	 * @parm effLcsDate Timestamp
	 */
	public void modifyEffLcsDate(Timestamp effLcsDate) {
		this.effLcsDate.modifyValue(effLcsDate);
	}
	/**
	 * 得到证照失效日期
	 * @return endLcsDate Timestamp
	 */
	public Timestamp getEndLcsDate() {
		return endLcsDate.getValue();
	}
	/**
	 * 设置证照失效日期
	 * @parm endLcsDate Timestamp
	 */
	public void setEndLcsDate(Timestamp endLcsDate) {
		this.endLcsDate.setValue(endLcsDate);
	}
	/**
	 * 修改证照失效日期
	 * @parm endLcsDate Timestamp
	 */
	public void modifyEndLcsDate(Timestamp endLcsDate) {
		this.endLcsDate.modifyValue(endLcsDate);
	}
	/**
	 * 得到全职注记
	 * @return fullTimeFlg String
	 */
	public String getFullTimeFlg() {
		return fullTimeFlg.getValue();
	}
	/**
	 * 设置全职注记
	 * @parm fullTimeFlg String
	 */
	public void setFullTimeFlg(String fullTimeFlg) {
		this.fullTimeFlg.setValue(fullTimeFlg);
	}
	/**
	 * 修改全职注记
	 * @parm fullTimeFlg String
	 */
	public void modifyFullTimeFlg(String fullTimeFlg) {
		this.fullTimeFlg.modifyValue(fullTimeFlg);
	}
	/**
	 * 得到控制药品权限
	 * @return ctrlFlg String
	 */
	public String getCtrlFlg() {
		return ctrlFlg.getValue();
	}
	/**
	 * 设置控制药品权限
	 * @parm ctrlFlg String
	 */
	public void setCtrlFlg(String ctrlFlg) {
		this.ctrlFlg.setValue(ctrlFlg);
	}
	/**
	 * 修改控制药品权限
	 * @parm ctrlFlg String
	 */
	public void modifyCtrlFlg(String ctrlFlg) {
		this.ctrlFlg.modifyValue(ctrlFlg);
	}
	/**
	 * 得到区域代码
	 * @return regionCode String
	 */
	public String getRegionCode() {
		return regionCode.getValue();
	}
	/**
	 * 设置区域代码
	 * @parm regionCode String
	 */
	public void setRegionCode(String regionCode) {
		this.regionCode.setValue(regionCode);
	}
	/**
	 * 修改区域代码
	 * @parm regionCode String
	 */
	public void modifyRegionCode(String regionCode) {
		this.regionCode.modifyValue(regionCode);
	}
	/**
	 * 得到最后登入日期
	 * @return rcntLoginDate Timestamp
	 */
	public Timestamp getRcntLoginDate() {
		return rcntLoginDate.getValue();
	}
	/**
	 * 设置最后登入日期
	 * @parm rcntLoginDate Timestamp
	 */
	public void setRcntLoginDate(Timestamp rcntLoginDate) {
		this.rcntLoginDate.setValue(rcntLoginDate);
	}
	/**
	 * 修改最后登入日期
	 * @parm rcntLoginDate Timestamp
	 */
	public void modifyRcntLoginDate(Timestamp rcntLoginDate) {
		this.rcntLoginDate.modifyValue(rcntLoginDate);
	}
	/**
	 * 得到最后登出日期
	 * @return rcntLogoutDate Timestamp
	 */
	public Timestamp getRcntLogoutDate() {
		return rcntLogoutDate.getValue();
	}
	/**
	 * 设置最后登出日期
	 * @parm rcntLogoutDate Timestamp
	 */
	public void setRcntLogoutDate(Timestamp rcntLogoutDate) {
		this.rcntLogoutDate.setValue(rcntLogoutDate);
	}
	/**
	 * 修改最后登出日期
	 * @parm rcntLogoutDate Timestamp
	 */
	public void modifyRcntLogoutDate(Timestamp rcntLogoutDate) {
		this.rcntLogoutDate.modifyValue(rcntLogoutDate);
	}
	/**
	 * 得到最后登陆IP
	 * @return rcntIp String
	 */
	public String getRcntIp() {
		return rcntIp.getValue();
	}
	/**
	 * 设置最后登陆IP
	 * @parm rcntIp String
	 */
	public void setRcntIp(String rcntIp) {
		this.rcntIp.setValue(rcntIp);
	}
	/**
	 * 修改最后登陆IP
	 * @parm rcntIp String
	 */
	public void modifyRcntIp(String rcntIp) {
		this.rcntIp.modifyValue(rcntIp);
	}
	/**
	 * 得到操作人员
	 * @return optUser String
	 */
	public String getOptUser() {
		return optUser.getValue();
	}
	/**
	 * 设置操作人员
	 * @parm optUser String
	 */
	public void setOptUser(String optUser) {
		this.optUser.setValue(optUser);
	}
	/**
	 * 修改操作人员
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
	 * 得到部门（显示用）
	 * @return deptCode String
	 */
	public String getDeptCode(){
		return this.deptCode.getValue();
	}
	/**
	 * 设置部门
	 * @param deptCode String
	 */
	public void setDeptCode(String deptCode){
		this.deptCode.setValue(deptCode);
	}
	public JDODeptList getJDODeptList(){
		return this.deptlist;
	}
	public void setJDODeptList(JDODeptList deptlist){
		this.deptlist=deptlist;
	}
	/**
	 * 得到parm
	 */
	public TParm getParm() {
		TParm result = super.getParm();
		return result;
	}
}
