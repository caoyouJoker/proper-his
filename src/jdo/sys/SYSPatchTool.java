package jdo.sys;

import java.sql.Timestamp;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 *
 * <p>
 * Title: 批次程序
 * </p>
 *
 * <p>
 * Description:批次程序
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author zhangy 2009/07/22
 * @version 1.0
 */

public class SYSPatchTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static SYSPatchTool instanceObject;

    /**
     * 得到实例
     *
     * @return
     */
    public static SYSPatchTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSPatchTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SYSPatchTool() {
        setModuleName("sys\\SYSPatchModule.x");
        onInit();
    }

    /**
     * 新增
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm) {
        TParm result = this.update("insert", parm);
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
     * 更新
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm) {
        TParm result = this.update("update", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新
     *
     * @param parm
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
     * SYS_FEE使用接口，生成一次性执行的批次
     *
     * @param parm
     *
     * @param conn
     * @return
     */
    public TParm onCreateNewPatch(TParm parm, TConnection conn) {
        TParm result = new TParm();
        // 取号原则
        String patch_code = SystemTool.getInstance().getNo("ALL", "PUB",
            "PATCH_CODE", "PATCH_CODE");
        parm.setData("PATCH_CODE", patch_code);
        parm.setData("PATCH_DESC", "调价计划" + patch_code);
        parm.setData("PATCH_TYPE", 1);
        parm.setData("PATCH_REOMIT_COUNT", 0);
        parm.setData("PATCH_REOMIT_INTERVAL", "");
        parm.setData("PATCH_REOMIT_POINT", "N");
        parm.setData("STATUS", "Y");
        TNull tnull = new TNull(Timestamp.class);
        parm.setData("END_DATE", tnull);
        // 新增批次程序
        result = this.onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // 新增批次参数-1: 药品名称
        parm.setData("PATCH_PARM_NAME", "ORDER_CODE");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_ORDER_CODE"));
        result = SYSPatchParmTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // 新增批次参数-2: 新零售价格
        parm.setData("PATCH_PARM_NAME", "OWN_PRICE");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_OWN_PRICE"));
        result = SYSPatchParmTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        // 新增批次参数-3: 新贵宾价格
        parm.setData("PATCH_PARM_NAME", "OWN_PRICE2");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_OWN_PRICE2"));
        result = SYSPatchParmTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        // 新增批次参数-4: 新国际医疗价格
        parm.setData("PATCH_PARM_NAME", "OWN_PRICE3");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_OWN_PRICE3"));
        result = SYSPatchParmTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        // 新增批次参数-5: 调价计划单号
        parm.setData("PATCH_PARM_NAME", "RPP_CODE");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_RPP_CODE"));
        result = SYSPatchParmTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        return result;
    }

    /**
     * SYS_FEE使用接口，更新一次性执行的批次
     *
     * @param parm
     *
     * @param conn
     * @return
     */
    public TParm onUpdateNewPatch(TParm parm, TConnection conn) {
        TParm result = new TParm();
        parm.setData("PATCH_PARM_NAME", "RPP_CODE");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_RPP_CODE"));
        result = SYSPatchParmTool.getInstance().onQueryPatchCode(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        parm.setData("PATCH_CODE", result.getValue("PATCH_CODE", 0));
        parm.setData("PATCH_DESC", "调价计划" + parm.getValue("PARM_RPP_CODE"));
        parm.setData("PATCH_TYPE", 1);
        parm.setData("PATCH_REOMIT_COUNT", 0);
        parm.setData("PATCH_REOMIT_INTERVAL", "");
        parm.setData("PATCH_REOMIT_POINT", "N");
        parm.setData("STATUS", "Y");
        TNull tnull = new TNull(Timestamp.class);
        parm.setData("END_DATE", tnull);
        // 更新批次程序
        result = this.onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        // 更新批次参数-1: 药品名称
        parm.setData("PATCH_PARM_NAME", "ORDER_CODE");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_ORDER_CODE"));
        result = SYSPatchParmTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // 新增批次参数-2: 新零售价格
        parm.setData("PATCH_PARM_NAME", "OWN_PRICE");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_OWN_PRICE"));
        result = SYSPatchParmTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        // 新增批次参数-3: 新贵宾价格
        parm.setData("PATCH_PARM_NAME", "OWN_PRICE2");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_OWN_PRICE2"));
        result = SYSPatchParmTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        // 新增批次参数-4: 新国际医疗价格
        parm.setData("PATCH_PARM_NAME", "OWN_PRICE3");
        parm.setData("PATCH_PARM_VALUE", parm.getValue("PARM_OWN_PRICE3"));
        result = SYSPatchParmTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        return result;
    }

}
