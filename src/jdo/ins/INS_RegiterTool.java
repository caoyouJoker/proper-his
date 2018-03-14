package jdo.ins;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>
 * Title: 诊疗项目备案管理
 * </p>
 * 
 * <p>
 * Description:诊疗项目备案管理
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author zhangs 20140414
 * @version 1.0
 */

public class INS_RegiterTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static INS_RegiterTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INSADMConfirmTool
	 */
	public static INS_RegiterTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INS_RegiterTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public INS_RegiterTool() {
		setModuleName("ins\\INS_RegiterModule.x");
		onInit();
	}
	/**
	 * 删除旧的需备案目录信息
	 * @param parm
	 * @return
	 */
	public TParm deleteINSNeedRegisterItem(TParm parm,TConnection connection){
		TParm result = update("deleteINSNeedRegisterItem",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 需备案目录信息下载添加数据
	 * @param parm
	 * @return
	 */
	public TParm insertINSNeedRegisterItemOne(TParm parm,TConnection connection){
		System.out.println("insertINSNeedRegisterItemOne:"+parm);
		TParm result = update("insertINSNeedRegisterItemOne",parm,connection);
		System.out.println("insertINSNeedRegisterItemOne:"+result);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 诊疗项目备案信息下载更新数据
	 * @param parm
	 * @return
	 */
	public TParm updateINSRegisterItemOne(TParm parm,TConnection connection){
		TParm result = update("updateINSRegisterItemOne",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 诊疗项目备案信息取消更新数据
	 * @param parm
	 * @return
	 */
	public TParm onCancelInsRegisterItemOne(TParm parm,TConnection connection){
		TParm result = update("onCancelInsRegisterItemOne",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 诊疗项目备案信息删除更新数据
	 * @param parm
	 * @return
	 */
	public TParm onDeleteInsRegisterItem(TParm parm,TConnection connection){
		TParm result = update("onDeleteInsRegisterItem",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 诊疗项目备案信息新增数据
	 * @param parm
	 * @return
	 */
	public TParm onInsertInsRegisterItem(TParm parm,TConnection connection){
//		System.out.println("onInsertInsRegisterItem_jdo_start"+parm);
		TParm result = update("onInsertInsRegisterItem",parm,connection);
//		System.out.println("onInsertInsRegisterItem_jdo_end"+result);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 诊疗项目备案信息修改数据
	 * @param parm
	 * @return
	 */
	public TParm onInsRegisterItemUpdate(TParm parm,TConnection connection){
		System.out.println("onInsRegisterItemUpdate_jdo_start"+parm);
		TParm result = update("onInsRegisterItemUpdate",parm,connection);
		System.out.println("onInsRegisterItemUpdate_jdo_end"+result);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
}
