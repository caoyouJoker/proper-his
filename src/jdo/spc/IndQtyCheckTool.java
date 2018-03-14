package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 盘点管理
 * </p>
 *
 * <p>
 * Description: 盘点管理
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
 * @author zhangy 2009.06.10
 * @version 1.0
 */
public class IndQtyCheckTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static IndQtyCheckTool instanceObject;

    /**
     * 得到实例
     *
     * @return IndAgentTool
     */
    public static IndQtyCheckTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndQtyCheckTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public IndQtyCheckTool() {
        setModuleName("spc\\INDQtyCheckModule.x");		
        onInit();
    }

    /**
     * 查询
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("query", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询冻结时间
     *
     * @param parm
     * @return
     */
    public TParm onQueryFrozenDate(TParm parm) {
        TParm result = this.query("queryFrozenDate", parm);
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
        TParm result = this.update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据选择的冻结时间查询冻结数据
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryQtyCheck(TParm parm) {
        String sql = "";
        //System.out.println("----CHECKREASON_CODE----"+parm.getValue("CHECKREASON_CODE"));
        if ("0".equals(parm.getValue("CHECKREASON_CODE"))) {
            // 全库盘点
            sql = INDSQL.getQtyCheckDataByType0(parm.getValue("ORG_CODE"),
                                                parm.getValue("FROZEN_DATE"),
                                                parm.getValue("ACTIVE_FLG"),
                                                parm.getValue("VALID_FLG"),
                                                parm.getValue("SORT"));
        }
        else {
            // 抽样盘点
            if ("A".equals(parm.getValue("CHECK_TYPE"))) {
                sql = INDSQL.getQtyCheckDataByType0(parm.getValue("ORG_CODE"),
                    parm.getValue("FROZEN_DATE"),
                    parm.getValue("ACTIVE_FLG"),
                    parm.getValue("VALID_FLG"),
                    parm.getValue("SORT"));

            }
            else if ("B".equals(parm.getValue("CHECK_TYPE"))) {
                sql = INDSQL.getQtyCheckDataByTypeB(parm.getValue("ORG_CODE"),
                    parm.getValue("FROZEN_DATE"),
                    parm.getValue("ACTIVE_FLG"),
                    parm.getValue("VALID_FLG"),
                    parm.getValue("SORT"),
                    parm.getValue("ORDER_CODE"));

            }
            else if ("C".equals(parm.getValue("CHECK_TYPE"))) {
                sql = INDSQL.getQtyCheckDataByTypeC(parm.getValue("ORG_CODE"),
                    parm.getValue("FROZEN_DATE"),
                    parm.getValue("ACTIVE_FLG"),
                    parm.getValue("VALID_FLG"),
                    parm.getValue("SORT"),
                    parm.getValue("ORDER_CODE"),
                    parm.getValue("VALID_DATE"));
            }
            else if ("D".equals(parm.getValue("CHECK_TYPE"))) {
                sql = INDSQL.getQtyCheckDataByTypeD(parm.getValue("ORG_CODE"),
                    parm.getValue("FROZEN_DATE"),
                    parm.getValue("ACTIVE_FLG"),
                    parm.getValue("VALID_FLG"),
                    parm.getValue("SORT"),
                    parm.getValue("MATERIAL_LOC_CODE"));
            }
            else {
                sql = INDSQL.getQtyCheckDataByTypeOther(parm.getValue(
                    "ORG_CODE"), parm.getValue("FROZEN_DATE"));
            }
        }
        //System.out.println("SQL:" + sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 更新实际数量和调整量
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("update", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 保存盘点数据
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyCheck(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyCheck", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 解除冻结
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateUnLock(TParm parm, TConnection conn) {
        TParm result = this.update("updateUnLock", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 盘盈亏统计(汇总)
     *
     * @param parm
     * @return
     */
    public TParm onQueryQtyCheckMaster(TParm parm) {
        TParm result = this.query("getQueryQtyCheckMaster", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 盘盈亏统计(明细)
     *
     * @param parm
     * @return
     */
    public TParm onQueryQtyCheckDetail(TParm parm) {
        TParm result = this.query("getQueryQtyCheckDetail", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
