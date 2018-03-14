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
 * Description: 住院费用结算管理：住院费用明细上传
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author pangben 2012-2-6
 * @version 1.0
 */
public class INSIbsUpLoadTool extends TJDOTool{
	/**
	 * 实例
	 */
	public static INSIbsUpLoadTool instanceObject;
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 得到实例
	 * 
	 * @return
	 */
	public static INSIbsUpLoadTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSIbsUpLoadTool();
		return instanceObject;
	}
	 /**
     * 构造器
     */
    public INSIbsUpLoadTool() {
        setModuleName("ins\\INSIbsUpLoadModule.x");
        onInit();
    }
	/**
	 * 删除明细档数据
	 * @param parm
	 * @return
	 */
	public TParm deleteINSIbsUpload(TParm parm,TConnection connection){
		TParm result = update("deleteINSIbsUpload",parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 删除明细档数据
	 * @param parm
	 * @return
	 */
	public TParm deleteINSIbsUpload(TParm parm){
		TParm result = update("deleteINSIbsUpload",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 删除明细档细项数据数据
	 * @param parm
	 * @return
	 */
	public TParm deleteINSIbsUploadSeq(TParm parm){
		TParm result = update("deleteINSIbsUploadSeq",parm);
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
	public TParm insertINSIbsUpload(TParm parm,TConnection connection){
		TParm result = update("insertINSIbsUpload",parm,connection);
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
	public TParm insertINSIbsUpload(TParm parm){
		TParm result = update("insertINSIbsUpload",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 查询此就诊号总金额
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm querySumIbsUpLoad(TParm parm){
		TParm result = query("querySumIbsUpLoad",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 查询最大SEQ_NO 累计增负使用
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm queryMaxIbsUpLoad(TParm parm){
		TParm result = query("queryMaxIbsUpLoad",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 累计增负添加数据
	 * @param parm
	 * @return
	 */
	public TParm insertINSIbsUploadOne(TParm parm,TConnection connection){
		TParm result = update("insertINSIbsUploadOne",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 累计增负修改数据
	 * @param parm
	 * @return
	 */
	public TParm updateINSIbsUploadOne(TParm parm,TConnection connection){
		TParm result = update("updateINSIbsUploadOne",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 查询是否存在累计增负数据
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm queryIbsUploadAdd(TParm parm){
		TParm result = query("queryIbsUploadAdd",parm);
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
	public TParm queryCheckSumIbsUpLoad(TParm parm){
		TParm result = query("queryCheckSumIbsUpLoad",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 费用分割 查询分割后明细数据
	 * @param parm
	 * @return
	 */
	public TParm queryNewSplit(TParm parm){
		TParm result = query("queryNewSplit",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 费用分割 查询费用分割后明细中上传数据显示
	 * @param parm
	 * @return
	 */
	public TParm queryNewSplitUpLoad(TParm parm){
		TParm result = query("queryNewSplitUpLoad",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 结算操作全数据查询
	 * @param parm
	 * @return
	 */
	public TParm queryAllSumAmt(TParm parm){
		TParm result = query("queryAllSumAmt",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 费用分割后修改数据
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm updateSplit(TParm parm,TConnection connection){
		TParm result = update("updateSplit",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 单病种操作查询床位费用自需金额和医用材料费特需金额
	 * @param parm
	 * @return
	 */
	public TParm queryBedAndMaterial(TParm parm){
		TParm result = query("queryBedAndMaterial",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 累计增负操作 删除 NHI_ORDER_CODE=‘***018’的数据
	 * @param parm
	 * @return
	 */
	public TParm deleteAddIbsUpload(TParm parm,TConnection connection){
		TParm result = update("deleteAddIbsUpload",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
}
