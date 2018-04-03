package jdo.mro;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 病案出入库管理</p>
 *
 * <p>Description: 病案出入库管理</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-12
 * @version 1.0
 */
public class MROQueueTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static MROQueueTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MROQueueTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MROQueueTool();
        return instanceObject;
    }

    public MROQueueTool() {
        this.setModuleName("mro\\MROQueueModule.x");
        this.onInit();
    }

    /**
     * 查询待出库病案
     * @return TParm
     */
    public TParm queryQueue(TParm parm) {
        TParm result = this.query("selectOut", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 修改Queue表 病历出入库状态
     * @param parm TParm
     * @return TParm
     */
    public TParm updateISSUE(TParm parm, TConnection conn) {
        TParm result = this.update("updateOut", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 修改病历主表 病历在库状态
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm updateIn_flg(TParm parm, TConnection conn) {
        TParm result = this.update("updateIn_flg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 病历出入库 方法
     * @param parm TParm
     * @return TParm
     */
    public TParm updateOUT(TParm parm, TConnection conn) {
        TParm p;
        TParm result = new TParm();
        if (parm.getData("MRV") != null) {
            p = parm.getParm("MRV");
            result = this.updateIn_flg(p, conn);

            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }
        else{
            result.setErr(-1,"缺少MRV表参数");
            return result;
        }
        if (parm.getData("Queue") != null) {
            p = parm.getParm("Queue");
            result = this.updateISSUE(p, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
        }else{
            result.setErr(-1,"缺少Queue表参数");
            return result;
        }
        if (parm.getData("Tranhis") != null) {
            p = parm.getParm("Tranhis");
            result = this.insertTRANHIS(p, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
            return result;
        }else{
            result.setErr(-1,"缺少Tranhis表参数");
            return result;
        }

    }

    /**
     * 插入病历借阅历史记录表
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertTRANHIS(TParm parm, TConnection conn) {
        TParm result = this.update("insertTRANHIS", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 病历借阅历史记录表 查询
     * @param parm TParm
     * @return TParm
     */
    public TParm selectTRANHIS(TParm parm){
        TParm result = this.query("selectTRANHIS",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 病历待入库信息查询
     * @return TParm
     */
    public TParm selectIn(TParm parm){
        TParm result = this.query("selectIn",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询出院病历归档（页签3使用）
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOutHp(TParm parm){
        TParm result = this.query("selectOutHp",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询出库病历信息（页签3使用）
     * @param parm TParm
     * @return TParm
     */
    public TParm selectOutQueue(TParm parm){
        TParm result = this.query("selectOutQueue",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 插入病历主档
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertMRO_MRV(TParm parm){
        TParm result = this.update("insertMRO_MRV",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 根据CASE_NO取消待出库病历(住院登记取消时使用)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm cancelQueueByCASE_NO(TParm parm,TConnection conn){
        TParm result = this.update("cancelQueueByCASE_NO",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 查询病历主档
     * @param parm TParm
     * @return TParm
     */
    public TParm selectMRO_MRV(TParm parm){
        TParm result = this.query("selectMRO_MRV",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 检查是否已经存在病历主档
     * @param MR_NO String
     * @return boolean 返回结果：true：该病案号的病历主档已存在存在  false：该病案号的病历主档不存在
     */
    public boolean checkHasMRO_MRV(String MR_NO){
        TParm parm = new TParm();
        parm.setData("MR_NO",MR_NO);
        TParm result = this.selectMRO_MRV(parm);
        boolean flg = false;
        if(result.getCount()>0){
            flg = true;
        }
        return flg;
    }

    /**
     * 查询病历是否归档
     * @param parm TParm
     * @return TParm
     */
	public TParm selectMRO_MRV_TECH(TParm parm) {
		TParm result = this.query("selectMRO_MRV_TECH", parm);
		if(result.getErrCode() < 0){
			err("ERR:" + result.getErrCode() + result.getErrText() +
					result.getErrName());
			return result;
		}
		return result;
	}
	
	
	/**
	 * 方法描述：针对住院出库数据中已出库后再取消住院的数据进行归还日期设定
	 * 迁移目的：入出转科管理
	 * 创建人：zhutong   
	 * 创建时间：2018年3月30日 下午1:09:07    
	 */
	public TParm updateRtnDateByCaseNo(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("UPDATE MRO_QUEUE SET RTN_DATE = TO_DATE(TO_CHAR(SYSDATE, 'YYYY/MM/DD'), 'YYYY/MM/DD')");
		sbSql.append(" WHERE ISSUE_CODE = '1' AND CASE_NO = '");
		sbSql.append(parm.getValue("CASE_NO"));
		sbSql.append("'");
		
		TParm result = new TParm(TJDODBTool.getInstance().update(sbSql.toString()));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
}
