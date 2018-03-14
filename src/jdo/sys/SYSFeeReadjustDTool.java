package jdo.sys;

import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title:调价计划细项Tool
 * </p>
 *
 * <p>
 * Description:调价计划细项Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.9.18
 * @version 1.0
 */
public class SYSFeeReadjustDTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static SYSFeeReadjustDTool instanceObject;

    /**
     * 得到实例
     *
     * @return CTZTool
     */
    public static SYSFeeReadjustDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSFeeReadjustDTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SYSFeeReadjustDTool() {
        setModuleName("sys\\SYSFeeReadjustDModule.x");
        onInit();
    }

    /**
     * 查询
     * @param parm TParm
     * @return TParm
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
     * 新增
     *
     * @param parm
     * @param conn
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
     * 更新调价计划细项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateSYSFeeReadjustD(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = this.onDeleteAll(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        TParm parmData = parm.getParm("PARM_DATA");
        TParm inparm = new TParm();
        for (int i = 0; i < parmData.getCount("ORDER_CODE"); i++) {
            if ("".equals(parmData.getValue("ORDER_CODE", i))) {
                continue;
            }
            inparm.setData("RPP_CODE", parm.getData("RPP_CODE"));
            inparm.setData("SEQ_NO", i);
            inparm.setData("ORDER_CODE", parmData.getData("ORDER_CODE", i));
            inparm.setData("START_DATE", parmData.getData("START_DATE", i));
            inparm.setData("END_DATE", parmData.getData("END_DATE", i));
            inparm.setData("OWN_PRICE", parmData.getData("OWN_PRICE_NEW", i));
            inparm.setData("OWN_PRICE2", parmData.getData("OWN_PRICE2_NEW", i));
            inparm.setData("OWN_PRICE3", parmData.getData("OWN_PRICE3_NEW", i));
            inparm.setData("OPT_USER", parm.getData("OPT_USER"));
            inparm.setData("OPT_DATE", parm.getData("OPT_DATE"));
            inparm.setData("OPT_TERM", parm.getData("OPT_TERM"));
            result = this.onInsert(inparm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        return result;
    }

    /**
     * 更新
     *
     * @param parm
     * @param conn
     * @return
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
     * 删除
     *
     * @param parm
     * @return
     */
    public TParm onDelete(TParm parm) {
        TParm result = this.update("delete", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除
     *
     * @param parm
     * @return
     */
    public TParm onDeleteAll(TParm parm, TConnection conn) {
        TParm result = this.update("deleteAll", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
