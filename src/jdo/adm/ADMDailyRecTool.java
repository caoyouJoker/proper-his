package jdo.adm;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author JiaoY 2009.05.15
 * @version 1.0
 */
public class ADMDailyRecTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static ADMDailyRecTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static ADMDailyRecTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMDailyRecTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ADMDailyRecTool() {
        setModuleName("adm\\ADMDailyRec.x");
        onInit();
    }

    /**
     * 删除当天病患的记录
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm delDailRec(TParm parm, TConnection conn) {
        TParm result = new TParm();
        if (parm == null) {
            err("-1 参数错误!");
            result.getErrName();
            return result;
        }
        result = update("deletedata", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }

    /**
     * 插入病患档
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertDailyRec(TParm parm, TConnection conn) {
        TParm result = new TParm();
        if (parm == null) {
            err("-1 参数错误!");
            result.getErrName();
            return result;
        }
        result = update("insertDailyRec", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询床日记录档 是否存在该病患的数据
     * @param POST_DATE String
     * @param CASE_NO String
     * @return boolean  ture:存在数据   false:没有数据
     */
    public boolean checkDAILY_REC(String POST_DATE, String CASE_NO) {
        TParm parm = new TParm();
        parm.setData("POST_DATE", POST_DATE);
        parm.setData("CASE_NO", CASE_NO);
        TParm result = this.query("checkDAILY_REC", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;
        }
        if(result.getCount()>0)
            return true;
        else
            return false;
    }
    /**
     * 查询床日记录档
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDailyRec(TParm parm){
        TParm result = this.query("selectDailyRec",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
