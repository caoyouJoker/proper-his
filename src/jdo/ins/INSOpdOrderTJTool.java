package jdo.ins;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title:医保卡费用分割主档
 * </p>
 * 
 * <p>
 * Description: 医保卡费用分割主档
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 20111107
 * @version 1.0
 */
public class INSOpdOrderTJTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static INSOpdOrderTJTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INSOpdApproveTool
	 */
	public static INSOpdOrderTJTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSOpdOrderTJTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public INSOpdOrderTJTool() {
		setModuleName("ins\\INSOpdOrderTJModule.x");
		onInit();
	}

	/**
	 * 插入分割主档
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertINSOpdOrder(TParm parm) {
		TParm result = update("insertINSOpdOrder", parm);
		return result;
	}
	/**
	 * 退费使用查询需要添加的退费数据
	 * @param parm
	 * @return
	 */
	public TParm selectResetOpdOrder(TParm parm){
		TParm result = query("selectResetOpdOrder", parm);
		return result;
	}
	/**
	 * 删除
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteINSOpdOrder(TParm parm, TConnection conn) {
		TParm result = update("deleteINSOpdOrder", parm, conn);
		return result;
	}
	/**
	 * 删除
	 * 对账使用
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteINSOpdOrder(TParm parm) {
		TParm result = update("deleteINSOpdOrder", parm);
		return result;
	}
	/**
	 * 查询最大SEQ_NO
	 * 
	 * @param parm
	 * @return
	 */
	public TParm selectMAXSeqNo(TParm parm) {
		TParm result = query("selectMAXSeqNo", parm);
		return result;
	}
	/**
	 * 修改对账状态
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateINSOpdOrder(TParm parm, TConnection conn){
		TParm result = update("updateINSOpdOrder", parm, conn);
		return result;
	}
	/**
	 * 修改对账状态
	 * 对账使用
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateINSOpdOrder(TParm parm){
		TParm result = update("updateINSOpdOrder", parm);
		return result;
	}
	/**
	 * 修改打票状态
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updatePrintNo(TParm parm){
		TParm result = update("updatePrintNo", parm);
		return result;
	}
	/**
	 * INSAMT_FLG对账标志和票据号
	 * @param parm
	 * @return
	 */
	public TParm updateINSOpdOrderPrint(TParm parm, TConnection conn){
		TParm result = update("updateINSOpdOrderPrint", parm,conn);
		return result;
	}
	/**
	 * 门诊对账 查询对明细账数据
	 * @param parm
	 * @return
	 */
	public TParm selectOpdOrderAccount(TParm parm){
		TParm result = query("selectOpdOrderAccount", parm);
		return result;
	}
	/**
	 * 添加一条累计增负的数据
	 * @param parm
	 * @return
	 */
	public TParm insertAddOpdOpder(TParm parm){
		TParm result = update("insertAddOpdOpder", parm);
		return result;
	}
	/**
	 * 删除操作--删除累计增负数据
	 * @param parm
	 * @return
	 */
	public TParm deleteAddINSOpdOrder(TParm parm){
		TParm result = update("deleteAddINSOpdOrder", parm);
		return result;
	}
	/**
	 * 查询发生金额金额 ，累计增负使用
	 * @param parm
	 * @return
	 */
	public TParm queryAddInsOpdOrder(TParm parm){
		TParm result = query("queryAddInsOpdOrder", parm);
		return result;
	}
	/**
	 * 费用分割后查询，执行上传操作
	 * @param parm
	 * @return
	 */
	public TParm selectOpdOrder(TParm parm){
		TParm result = query("selectOpdOrder", parm);
		return result;
	}
	/**
	 * 删除
	 * 此就诊病患所有数据
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm deleteSumINSOpdOrder(TParm parm) {
		TParm result = update("deleteSumINSOpdOrder", parm);
		return result;
	}
	/**
	 * 票据补打修改票据号码
	 */
	public TParm updateInsOpdOrderInvNo(TParm parm, TConnection conn){
		TParm result = update("updateInsOpdOrderInvNo", parm);
		return result;
	}
}
