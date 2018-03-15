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
 * Description: 住院费用结算管理
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
public class INSIbsTool extends TJDOTool{
	/**
	 * 实例
	 */
	public static INSIbsTool instanceObject;
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 得到实例
	 * 
	 * @return
	 */
	public static INSIbsTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSIbsTool();
		return instanceObject;
	}
	  /**
     * 构造器
     */
    public INSIbsTool() {
    	setModuleName("ins\\INSIbsModule.x");
        onInit();
    }
	/**
	 * 修改资格确认书编号 
	 * 住院资格确认书下载开立操作
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateINSIbsConfirmNo(TParm parm,TConnection conn){
		TParm result = update("updateINSIbsConfirmNo",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 从IBS_OrdD读取数据   读取住院批价资料
	 * @param parm
	 * @return
	 */
	public TParm queryIbsOrdd(TParm parm){
		TParm result = query("queryIbsOrdd",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 *  读取住院批价资料 因为医保升级问题，
	 *  医保时间问题存在没有查询出的医嘱，将重新获得未查询的数据
	 * @param parm
	 * @return
	 */
	public TParm queryIbsOrddOther(TParm parm){
		TParm result = query("queryIbsOrddOther",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 添加数据
	 * @param parm
	 * @return
	 */
	public TParm insertInsIbs(TParm parm){
		TParm result = update("insertInsIbs",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 修改数据 费用分割界面转病患信息使用
	 * @param parm
	 * @return
	 */
	public TParm updateINSIbs(TParm parm){
		TParm result = update("updateINSIbs",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 费用分割结算后更新
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateSplitByIns(TParm parm,TConnection connection){
		TParm result = update("updateSplitByIns",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 费用分割界面中 费用结算操作修改 医保回参数据保存
	 * @param parm
	 * @return
	 */
	public TParm updateInsIbsAmt(TParm parm,TConnection connection){
		TParm result = update("updateInsIbsAmt",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 查询结算表全字段
	 * @param parm
	 * @return
	 */
	public TParm queryIbsSum(TParm parm){
		TParm result = query("queryIbsSum",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 费用分割 结算使用查询医保返回数据
	 * @param parm
	 * @return
	 */
	public TParm queryRetrunInsAmt(TParm parm){
		TParm result = query("queryRetrunInsAmt",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 查询ADM_SEQ
	 * @param parm
	 * @return
	 */
	public TParm queryAdmSeq(TParm parm){
		TParm result = query("queryAdmSeq",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 修改费用分割界面数据
	 * @param parm
	 * @return
	 */
	public TParm updateIbsOther(TParm parm){
		TParm result = update("updateIbsOther",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 修改床位费特需金额和医用材料费特需金额
	 * @param parm
	 * @return
	 */
	public TParm updateIbsBedFee(TParm parm){
		TParm result = update("updateIbsBedFee",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 查询医保三目字典中用药类型
	 * @param parm
	 * @return
	 */
	public TParm queryInsRuleET(TParm parm){
		TParm result = query("queryInsRuleET",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 费用结算使用  获得医保 数据 保存INS_IBS 数据
	 * @param parm
	 * @return
	 */
	public TParm queryInsAmt(TParm parm){
		TParm result = query("queryInsAmt",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 费用分割界面中 费用结算操作修改 医保回参数据保存--单病种
	 * @param parm
	 * @return
	 */
	public TParm updateInsIbsSingleAmt(TParm parm,TConnection connection){
		TParm result = update("updateInsIbsSingleAmt",parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 单病种费用分割 病历首页 中保存操作
	 * @param parm
	 * @return
	 */
	public TParm updateInsIbsMro(TParm parm){
		TParm result = update("updateInsIbsMro",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * 查询医保数据
	 * @param parm
	 * @return
	 */
	public TParm queryInsIbsOrderByInsRule(TParm parm){
		TParm result = query("queryInsIbsOrderByInsRule",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
}
