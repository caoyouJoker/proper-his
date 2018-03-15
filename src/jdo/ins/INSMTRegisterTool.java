package jdo.ins;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
/**
 * 
 * Title: 门特登记 \审核
 * 
 * Description:门特登记 \审核:门特登记开具或下载 \审核查询
 * 
 * Copyright: ProperSoft (c) 2011
 * 
 * @author pangben 2012-1-12
 * @version 2.0
 */
public class INSMTRegisterTool extends TJDOTool{
	/**
	 * 实例
	 */
	public static INSMTRegisterTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INSTJTool
	 */
	public static INSMTRegisterTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSMTRegisterTool();
		return instanceObject;
	}
	/**
	 * 构造器
	 */
	public INSMTRegisterTool() {
		setModuleName("ins\\INSMTRegisterModule.x");
		onInit();
	}
	/**
	 * 查询数据
	 * @param parm
	 * @return
	 */
	public TParm queryINSMTRegister(TParm parm){
		TParm result = query("queryINSMTRegister", parm);
		return result;
	}
	/**
	 * 校验是否存在数据   开具下载使用
	 * @param parm
	 * @return
	 */
	public TParm queryCheckMTRegister(TParm parm){
		TParm result = query("queryCheckMTRegister", parm);
		return result;
	}
	/**
	 * 查询已经保存的数据   开具下载使用
	 * @param parm
	 * @return
	 */
	public TParm queryMrMTRegister(TParm parm){
		TParm result = query("queryMrMTRegister", parm);
		return result;
	}
	
	/**
	 * 保存操作
	 * @param parm
	 * @return
	 */
	public TParm saveINSMTRegister(TParm parm){
		TParm result=queryCheckMTRegister(parm);
		if (result.getErrCode()<0) {
			result.setErr(-1,"操作INSMTRegisterTool.saveINSMTRegister方法失败");
			return result;
		}
		if (result.getCount()>0) {
			result=updateINSMTRegister(parm);//修改数据
		}else{
			result=insertINSMTRegister(parm);//添加数据
		}
		if (result.getErrCode()<0) {
			result.setErr(-1,"操作INSMTRegisterTool.saveINSMTRegister方法失败");
			return result;
		}
		String sql="UPDATE REG_PATADM SET DISEASE_HISTORY='"+parm.getValue("DISEASE_HISTORY")+
		           "' , ASSISTANT_EXAMINE='"+parm.getValue("ASSISTANT_EXAMINE")+
		           "' , MED_HISTORY='"+parm.getValue("MED_HISTORY")+
		           "' , ASSSISTANT_STUFF='"+parm.getValue("ASSSISTANT_STUFF")+
		           "' WHERE CASE_NO='"+parm.getValue("CASE_NO")+"'";
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode()<0) {
			result.setErr(-1,"操作INSMTRegisterTool.saveINSMTRegister方法失败");
			return result;
		}
		TParm p=new TParm();
		p.setData("MR_NO",parm.getValue("MR_NO"));
		p.setData("CASE_NO",parm.getValue("CASE_NO"));
		p.setData("BEGIN_DATE",parm.getValue("BEGIN_DATE"));
		p.setData("CASE_NO",parm.getValue("CASE_NO"));
		p.setData("END_DATE",parm.getValue("END_DATE"));
		if (null!=parm.getValue("REGISTER_NO") && parm.getValue("REGISTER_NO").length()>0) {
			p.setData("REGISTER_NO",parm.getValue("REGISTER_NO"));
		}
		p.setData("INS_CROWD_TYPE",parm.getValue("INS_CROWD_TYPE"));
		result=queryMrMTRegister(p);
		return result;
	}
	/**
	 * 添加数据
	 * @param parm
	 * @return
	 */
	public TParm insertINSMTRegister(TParm parm){
		
		TParm result = update("insertINSMTRegister", parm);
		return result;
	}
	/**
	 * 修改数据
	 * @param parm
	 * @return
	 */
	public TParm updateINSMTRegister(TParm parm){
		TParm result = update("updateINSMTRegister", parm);
		return result;
	}
	/**
	 * 审核数据
	 * @param parm
	 * @return
	 */
	public TParm queryAudit(TParm parm){
		TParm result = query("queryAudit", parm);
		return result;
	}
	/**
	 * 第三个页签修改数据
	 * @param parm
	 * @return
	 */
	public TParm updateThreePageData(TParm parm){
		TParm result = update("updateThreePageData", parm);
		return result;
	}
	/**
	 * 审核修改数据 StutsType=1
	 * @param parm
	 * @return
	 */
	public TParm updateStutsType(TParm parm){
		TParm result = update("updateStutsType", parm);
		return result;
	}
}
