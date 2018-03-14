package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
import java.util.Vector;
import com.dongyang.jdo.TJDODBTool;

public class IndRequestDTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static IndRequestDTool instanceObject;

    /**
     * 得到实例
     *
     * @return IndStockMTool
     */
    public static IndRequestDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndRequestDTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public IndRequestDTool() {
        setModuleName("ind\\INDRequestDModule.x");
        onInit();
    }

    /**
     * 更新申请单累计完成量
     *
     * @param parm
     * @return
     */
    public TParm onUpdateActualQty(TParm parm, TConnection conn) {
        TParm result = this.update("updateActualQty", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 取消出库，更新D表状态和实际出库量
     * @author liyh 
     * @date 20120619
     * @param parm
     * @return
     */
    public TParm onUpdateActualQtyCancel(TParm parm, TConnection conn) {
        TParm result = this.update("updateActualQtyCancel", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新申请单明细
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        // 结果集
        TParm result = new TParm();
        Vector order_list = (Vector) parm.getData("ORDER_CODE_LIST");
        if (order_list != null && order_list.size() > 0) {//判断药房是否有药品信息，如果没有新增
            for (int i = 0; i < order_list.size(); i++) {
                TParm inparm = new TParm();
                inparm.setData("ORG_CODE", parm.getData("ORG_CODE"));
                inparm.setData("ORDER_CODE", order_list.get(i));
                result = IndStockMTool.getInstance().onQuery(inparm);
                if (result.getCount("ORG_CODE") > 0) {
                    continue;
                }
                inparm.setData("OPT_USER", parm.getData("OPT_USER"));
                inparm.setData("OPT_DATE", parm.getData("OPT_DATE"));
                inparm.setData("OPT_TERM", parm.getData("OPT_TERM"));
                inparm.setData("REGION_CODE", parm.getData("REGION_CODE"));
                result = IndStockMTool.getInstance().onInsert(inparm, conn);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText()
                        + result.getErrName());
                    return result;
                }
            }
        }

        Object update = parm.getData("UPDATE_SQL");
        if (update == null) {
            return null;
        }
        String[] updateSql;
        if (update instanceof String[]) {
            updateSql = (String[]) update;
        }
        else {
            return null;
        }
        // 保存申请单细项
        for (int i = 0; i < updateSql.length; i++) {
            result = new TParm(TJDODBTool.getInstance().update(updateSql[i],
                conn));
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createNewRequestD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 盒装包药机新增    chenx
     *
     * @param parm
     * @return
     */
    public TParm onBoxInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createBoxRequestD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新申请单状态
     *
     * @param parm
     * @return
     */
    public TParm onUpdateFlg(TParm parm, TConnection conn) {
        TParm result = this.update("updateRequestFlg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
