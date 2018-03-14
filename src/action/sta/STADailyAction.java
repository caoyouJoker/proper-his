package action.sta;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import jdo.sta.STAOpdDailyTool;
import com.dongyang.db.TConnection;
import jdo.sta.STAStationDailyTool;

/**
 * <p>Title: 中间表信息导入</p>
 *
 * <p>Description: 中间表信息导入</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-27
 * @version 1.0
 */
public class STADailyAction
    extends TAction {
    public STADailyAction() {
    }
    /**
     * 插入 门急诊中间档 数据
     * @param parm TParm  参数包括两部分："SQL"表示SQL语句参数  “DEPT”表示部门列表
     * @return TParm
     */
    public TParm insertSTA_OPD_DAILY(TParm parm) {
        TParm result = new TParm();
        // modified by WangQing at 20170209 -start
        // 将参数判断移到conn创建之前
        if (parm == null) {
            result.setErr( -1, "参数为空！");
            return result;
        }
        // modified by WangQing at 20170209 -end
        TConnection conn = this.getConnection();
        
        result = STAOpdDailyTool.getInstance().insertData(parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            // modified by WangQing at 20170209 -start
            // conn rollback
            conn.rollback();
            // modified by WangQing at 20170209 -end
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 插入 院区中间档 数据
     * @param parm TParm  参数包括两部分："SQL"表示SQL语句参数  “DEPT”表示部门列表
     * @return TParm
     */
    public TParm insertStation_Daily(TParm parm){
        TParm result = new TParm();
        // modified by WangQing at 20170209 -start
        // 将参数判断移到conn创建之前
        if (parm == null) {
            result.setErr( -1, "参数为空！");
            return result;
        }
        // modified by WangQing at 20170209 -end
        TConnection conn = this.getConnection();
 
        result = STAStationDailyTool.getInstance().insertData(parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            // modified by WangQing at 20170209 -start
            // conn rollback
            conn.rollback();
            // modified by WangQing at 20170209 -end
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 自动批次导入 门急诊中间档 数据
     * @param parm TParm  参数为空TParm 即可
     * @return TParm
     */
    public TParm batchSTA_OPD_DAILY(TParm parm) {
        TParm result = new TParm();
        // modified by WangQing at 20170209 -start
        // 将参数判断移到conn创建之前
        if (parm == null) {
            result.setErr( -1, "参数为空！");
            return result;
        }
        // modified by WangQing at 20170209 -end
        TConnection conn = this.getConnection();
        result = STAOpdDailyTool.getInstance().batchData(parm,conn);
        if (result.getErrCode() < 0) {
            err("STA门诊批次ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            // modified by WangQing at 20170209 -start
            // conn rollback
            conn.rollback();
            // modified by WangQing at 20170209 -end
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    /**
     * 自动批次导入 院区中间档 数据
     * @param parm TParm  参数为空TParm 即可
     * @return TParm
     */
    public TParm batchStation_Daily(TParm parm){
        TParm result = new TParm();
     // modified by WangQing at 20170209 -start
        // 将参数判断移到conn创建之前
        if (parm == null) {
            result.setErr( -1, "参数为空！");
            return result;
        }
        // modified by WangQing at 20170209 -end
        TConnection conn = this.getConnection();
        result = STAStationDailyTool.getInstance().batchData(parm,conn);
        if (result.getErrCode() < 0) {
            err("STA住院批次ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            // modified by WangQing at 20170209 -start
            // conn rollback
            conn.rollback();
            // modified by WangQing at 20170209 -end
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

}
