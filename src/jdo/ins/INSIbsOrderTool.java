package jdo.ins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>
 * Title: 住院费用结算管理
 * </p>
 * 
 * <p>
 * Description: 住院费用结算管理：住院费用明细
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author pangben 2012-2-1
 * @version 1.0
 */
public class INSIbsOrderTool extends TJDOTool{
	/**
	 * 实例
	 */
	public static INSIbsOrderTool instanceObject;
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 得到实例
	 * 
	 * @return
	 */
	public static INSIbsOrderTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSIbsOrderTool();
		return instanceObject;
	}
	  /**
     * 构造器
     */
    public INSIbsOrderTool() {
    	setModuleName("ins\\INSIbsOrderModule.x");
        onInit();
    }
	/**
	 * 删除明细档数据
	 * @param parm
	 * @return
	 */
	public TParm deleteINSIbsOrder(TParm parm){
		TParm result = update("deleteINSIbsOrder",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 添加明细档数据
	 * @param parm
	 * @return
	 */
	public TParm insertINSIbsOrder(TParm parm){
		TParm result = update("insertINSIbsOrder",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 查询上传数据
	 * @param parm
	 * @return
	 */
	public TParm queryInsIbsDUnion(TParm parm){
		TParm result = query("queryInsIbsDUnion",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 校验费用分割后金额是否相同
	 * @param parm
	 * @return
	 */
	public TParm queryCheckSumIbsOrder(TParm parm){
		TParm result = query("queryCheckSumIbsOrder",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 费用分割 查询分割前数据
	 * @param parm
	 * @return
	 */
	public TParm queryOldSplit(TParm parm){
		TParm result = query("queryOldSplit",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
}
