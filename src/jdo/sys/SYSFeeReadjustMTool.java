package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.data.TNull;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title:调价计划主项Tool
 * </p>
 *
 * <p>
 * Description:调价计划主项Tool
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
public class SYSFeeReadjustMTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static SYSFeeReadjustMTool instanceObject;

    /**
     * 得到实例
     *
     * @return
     */
    public static SYSFeeReadjustMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SYSFeeReadjustMTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SYSFeeReadjustMTool() {
        setModuleName("sys\\SYSFeeReadjustMModule.x");
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
     * 更新调价计划单
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateM(TParm parm, TConnection conn) {
        TParm result = this.onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        result = SYSFeeReadjustDTool.getInstance().onUpdate(parm, conn);
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
     * 删除调价计划单
     *
     * @param parm
     * @return
     */
    public TParm onDeleteM(TParm parm, TConnection conn) {
        TParm result = this.update("delete", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        result = SYSFeeReadjustDTool.getInstance().onDeleteAll(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 执行调价计划
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onCreateSYSFeeReadjust(TParm parm, TConnection conn) {
        TParm result = new TParm();
        String rpp_code = parm.getValue("RPP_CODE");
        TParm data = parm.getParm("PARM_DATA");
        String order_list = "";
        String own_list = "";
        String own2_list = "";
        String own3_list = "";
        //String nhi_list = "";    
        //String gov_list = "";
        for (int i = 0; i < data.getCount("ORDER_CODE"); i++) {
            if ("".equals(data.getValue("ORDER_CODE", i))) {
                continue;
            }
            // 查询SYS_FEE_HISTORY数据
            System.out.println("ORDER_CODE："+data.getValue("ORDER_CODE", i));     
            TParm oldParm = new TParm(TJDODBTool.getInstance().select(SYSSQL.
                getSYSFeeHistory(data.getValue("ORDER_CODE", i), true)));
            System.out.println("oldParm--"+oldParm);      
            order_list += data.getValue("ORDER_CODE", i) + ";";
            if (oldParm.getErrCode() < 0) {
                err("ERR:" + oldParm.getErrCode() + oldParm.getErrText()
                    + oldParm.getErrName());
                return oldParm;
            }
            // 新增SYS_FEE_HISTORY数据
            TParm newParm = new TParm();
            newParm = setParmData(newParm, oldParm);
            newParm.setData("START_DATE",
                            data.getValue("START_DATE", i).substring(0, 4)
                            + data.getValue("START_DATE", i).substring(5, 7)
                            + data.getValue("START_DATE", i).substring(8, 10)
                            + data.getValue("START_DATE", i).substring(11, 13)
                            + data.getValue("START_DATE", i).substring(14, 16)
                            + data.getValue("START_DATE", i).substring(17, 19));
            newParm.setData("END_DATE",
                            data.getValue("END_DATE", i).substring(0, 4)
                            + data.getValue("END_DATE", i).substring(5, 7)
                            + data.getValue("END_DATE", i).substring(8, 10)
                            + data.getValue("END_DATE", i).substring(11, 13)
                            + data.getValue("END_DATE", i).substring(14, 16)
                            + data.getValue("END_DATE", i).substring(17, 19));
            newParm.setData("OPT_USER", parm.getData("OPT_USER"));
            newParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
            newParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
            newParm.setData("OWN_PRICE", data.getData("OWN_PRICE_NEW", i));
            own_list += data.getDouble("OWN_PRICE_NEW", i) + ";";
            newParm.setData("OWN_PRICE2", data.getData("OWN_PRICE2_NEW", i));
            own2_list += data.getDouble("OWN_PRICE2_NEW", i) + ";";
            newParm.setData("OWN_PRICE3", data.getData("OWN_PRICE3_NEW", i));
            own3_list += data.getDouble("OWN_PRICE3_NEW", i) + ";";
            newParm.setData("RPP_CODE", rpp_code);
            System.out.println("newParm---"+newParm);
            result = SYSFeeHistoryTool.getInstance().onInsert(newParm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
            // 更新SYS_FEE_HISTORY数据
            TParm updateParm = new TParm();
            updateParm.setData("ORDER_CODE", oldParm.getValue("ORDER_CODE", 0));
            updateParm.setData("START_DATE", oldParm.getValue("START_DATE", 0));
            updateParm.setData("END_DATE",
                               data.getValue("START_DATE", i).substring(0, 4)
                               + data.getValue("START_DATE", i).substring(5, 7)
                               + data.getValue("START_DATE", i).substring(8, 10)
                               +
                               data.getValue("START_DATE", i).substring(11, 13)
                               +
                               data.getValue("START_DATE", i).substring(14, 16)
                               +
                               data.getValue("START_DATE", i).substring(17, 19));
            updateParm.setData("ACTIVE_FLG", "N");
            updateParm.setData("RPP_CODE", rpp_code);
            updateParm.setData("OPT_USER", parm.getData("OPT_USER"));
            updateParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
            updateParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
            result = SYSFeeHistoryTool.getInstance().onUpdate(updateParm, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        // 创建一次性批次
        TParm patchParm = new TParm();
        patchParm.setData("PARM_ORDER_CODE",
                          order_list.substring(0, order_list.length() - 1));
        patchParm.setData("PARM_OWN_PRICE",
                          own_list.substring(0, own_list.length() - 1));
        patchParm.setData("PARM_OWN_PRICE2",
                          own2_list.substring(0, own2_list.length() - 1));
        patchParm.setData("PARM_OWN_PRICE3",
                          own3_list.substring(0, own3_list.length() - 1));
        patchParm.setData("PARM_RPP_CODE", rpp_code);
        patchParm.setData("PATCH_SRC", "action.sys.SYSFeeReadjustBatchPatch");
        patchParm.setData("PATCH_DATE",
                          data.getValue("START_DATE", 0).substring(0, 4)
                          + data.getValue("START_DATE", 0).substring(5, 7)
                          + data.getValue("START_DATE", 0).substring(8, 10)
                          + data.getValue("START_DATE", 0).substring(11, 13)
                          + data.getValue("START_DATE", 0).substring(14, 16)
                          + data.getValue("START_DATE", 0).substring(17, 19));  
        patchParm.setData("OPT_USER", parm.getData("OPT_USER"));
        patchParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
        patchParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
        result = SYSPatchTool.getInstance().onCreateNewPatch(patchParm, conn);
        System.out.println("result::::"+result);
        if (result.getErrCode() < 0) {  
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 给PARM赋值
     * @param parm TParm
     * @param data TParm
     * @return TParm
     */
    public TParm setParmData(TParm parm, TParm data) {
        String[] arrays = {
            "ORDER_CODE", "START_DATE", "END_DATE", "ORDER_DESC", "ACTIVE_FLG",//1-5
            "LAST_FLG", "PY1", "PY2", "SEQ", "DESCRIPTION",//6-10
            "TRADE_ENG_DESC", "GOODS_DESC", "GOODS_PYCODE", "ALIAS_DESC","ALIAS_PYCODE",//11-15
            "SPECIFICATION", "NHI_FEE_DESC", "HABITAT_TYPE", "MAN_CODE","HYGIENE_TRADE_CODE",//16-20
            "ORDER_CAT1_CODE", "CHARGE_HOSP_CODE", "OWN_PRICE", "NHI_PRICE","GOV_PRICE",//21-25
            "UNIT_CODE", "LET_KEYIN_FLG", "DISCOUNT_FLG", "EXPENSIVE_FLG","OPD_FIT_FLG",//26-30
            "EMG_FIT_FLG", "IPD_FIT_FLG", "HRM_FIT_FLG", "DR_ORDER_FLG","INTV_ORDER_FLG",//31-35
            "LCS_CLASS_CODE", "TRANS_OUT_FLG", "TRANS_HOSP_CODE", "USEDEPT_CODE", "EXEC_ORDER_FLG",//36-40
            "EXEC_DEPT_CODE", "INSPAY_TYPE", "ADDPAY_RATE", "ADDPAY_AMT","NHI_CODE_O",//41-45
            "NHI_CODE_E", "NHI_CODE_I", "CTRL_FLG", "CLPGROUP_CODE","ORDERSET_FLG",//46-50
            "INDV_FLG", "SUB_SYSTEM_CODE", "RPTTYPE_CODE", "DEV_CODE","OPTITEM_CODE",//51-55
            "MR_CODE", "DEGREE_CODE", "CIS_FLG", "OWN_PRICE2", "OWN_PRICE3",//56-60
            "TUBE_TYPE", "IS_REMARK", "ACTION_CODE","EXEC_PRG", "ATC_FLG", "ATC_FLG_I", //61-65
            "CAT1_TYPE", "REGION_CODE"}; //66
        for (int i = 0; i < arrays.length; i++) {
            parm.setData(arrays[i], data.getData(arrays[i], 0));
        }
        return parm;
    }

    /**
     * SYS_FEE调用调价计划新增
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onSYSFeeReadjustPatch(TParm parm, TConnection conn) {
        TParm result = new TParm();
        TNull tnull = new TNull(Timestamp.class);
        //System.out.println("ORDER----" + parm);
        TParm orderParm = parm.getParm("ORDER");
        // 创建调价计划主项
        TParm readjustM = new TParm();
        String rpp_code = SystemTool.getInstance().getNo("ALL", "PUB",
            "SYS_FEE_READJUST", "No");
        readjustM.setData("RPP_CODE", rpp_code);
        readjustM.setData("RPP_DESC", "调价计划" + rpp_code);
        readjustM.setData("DESCRIPTION", "");
        //PY1
        //PY2
        //SEQ
        readjustM.setData("RPP_USER", parm.getData("OPT_USER"));
        readjustM.setData("RPP_DATE", parm.getData("OPT_DATE"));
        readjustM.setData("CHK_USER", parm.getData("OPT_USER"));
        readjustM.setData("CHK_DATE", parm.getData("OPT_DATE"));

        //StringTool.getTimestamp(orderParm.getTimestamp())
        readjustM.setData("READJUST_DATE",
                          StringTool.getTimestamp(orderParm.getValue(
                              "START_DATE",
                              0), "yyyy/MM/dd HH:mm:ss"));
        readjustM.setData("READJUSTOP_DATE", tnull);
        readjustM.setData("RPP_STATUS", "1");
        readjustM.setData("OPT_USER", parm.getData("OPT_USER"));
        readjustM.setData("OPT_DATE", parm.getData("OPT_DATE"));
        readjustM.setData("OPT_TERM", parm.getData("OPT_TERM"));
        result = this.onInsert(readjustM, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        // 创建调价计划细项
        TParm readjustD = new TParm();
        String order_list = "";
        String own_list = "";
        String own2_list = "";
        String own3_list = "";
        //String nhi_list = "";
        //String gov_list = "";
        //System.out.println("START----" + orderParm);
        for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
            //System.out.println("END----");
            readjustD.setData("RPP_CODE", rpp_code);
            readjustD.setData("SEQ_NO", i);
            readjustD.setData("ORDER_CODE", orderParm.getValue("ORDER_CODE", i));
            order_list += orderParm.getValue("ORDER_CODE", i) + ";";
            readjustD.setData("START_DATE",
                              StringTool.getTimestamp(orderParm.
                getValue("START_DATE", i), "yyyy/MM/dd HH:mm:ss"));
            readjustD.setData("END_DATE",
                              StringTool.getTimestamp(orderParm.
                getValue("END_DATE", i), "yyyyMMddHHmmss"));
            readjustD.setData("OWN_PRICE", orderParm.getDouble("OWN_PRICE", i));
            own_list += orderParm.getDouble("OWN_PRICE", i) + ";";
            readjustD.setData("OWN_PRICE2", orderParm.getDouble("OWN_PRICE2", i));
            own2_list += orderParm.getDouble("OWN_PRICE2", i) + ";";
            readjustD.setData("OWN_PRICE3", orderParm.getDouble("OWN_PRICE3", i));
            own3_list += orderParm.getDouble("OWN_PRICE3", i) + ";";
            readjustD.setData("OPT_USER", parm.getData("OPT_USER"));
            readjustD.setData("OPT_DATE", parm.getData("OPT_DATE"));
            readjustD.setData("OPT_TERM", parm.getData("OPT_TERM"));
            result = SYSFeeReadjustDTool.getInstance().onInsert(readjustD, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }

        // 回写SYS_FEE_HISTORY
        for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
            String start_date = orderParm.getValue("START_DATE", i).substring(0,
                4) +
                orderParm.getValue("START_DATE", i).substring(5, 7) +
                orderParm.getValue("START_DATE", i).substring(8, 10) +
                orderParm.getValue("START_DATE", i).substring(11, 13) +
                orderParm.getValue("START_DATE", i).substring(14, 16) +
                orderParm.getValue("START_DATE", i).substring(17, 19);
            TParm sysParm = new TParm(TJDODBTool.getInstance().update(SYSSQL.
                updateSYSFeeHistoryRppCode(orderParm.getValue("ORDER_CODE", i),
                                           start_date, rpp_code), conn));
            if (sysParm.getErrCode() < 0) {
                err("ERR:" + sysParm.getErrCode() + sysParm.getErrText()
                    + sysParm.getErrName());
                return sysParm;
            }
        }

        // 创建一次性批次
        TParm patchParm = new TParm();
        patchParm.setData("PARM_ORDER_CODE",
                          order_list.substring(0, order_list.length() - 1));
        patchParm.setData("PARM_OWN_PRICE",
                          own_list.substring(0, own_list.length() - 1));
        patchParm.setData("PARM_OWN_PRICE2",
                          own2_list.substring(0, own2_list.length() - 1));
        patchParm.setData("PARM_OWN_PRICE3",
                          own3_list.substring(0, own3_list.length() - 1));
        patchParm.setData("PARM_RPP_CODE", rpp_code);
        patchParm.setData("PATCH_SRC", "action.sys.SYSFeeReadjustBatchPatch");
        String start_date = orderParm.getValue("START_DATE", 0);

        patchParm.setData("PATCH_DATE",
                          start_date.substring(0, 4) +
                          start_date.substring(5, 7) +
                          start_date.substring(8, 10) +
                          start_date.substring(11, 13) +
                          start_date.substring(14, 16) +
                          start_date.substring(17, 19));
        patchParm.setData("OPT_USER", parm.getData("OPT_USER"));
        patchParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
        patchParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
        result = SYSPatchTool.getInstance().onCreateNewPatch(patchParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * SYS_FEE调用调价计划更新
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateSYSFeeReadjustPatch(TParm parm, TConnection conn) {
        TParm result = new TParm();
        TParm orderParm = parm.getParm("ORDER");
        TNull tnull = new TNull(Timestamp.class);
        // 更新调价计划主项
        String rpp_code = orderParm.getValue("RPP_CODE", 0);
        TParm readjustM = new TParm();
        readjustM.setData("RPP_CODE", rpp_code);
        readjustM.setData("RPP_DESC", "调价计划" + rpp_code);
        readjustM.setData("DESCRIPTION", "");
        readjustM.setData("RPP_USER", parm.getData("OPT_USER"));
        readjustM.setData("RPP_DATE", parm.getData("OPT_DATE"));
        readjustM.setData("CHK_USER", parm.getData("OPT_USER"));
        readjustM.setData("CHK_DATE", parm.getData("OPT_DATE"));
        readjustM.setData("READJUST_DATE",
                          StringTool.getTimestamp(orderParm.getValue(
                              "START_DATE",
                              0), "yyyyMMddHHmmss"));
        readjustM.setData("READJUSTOP_DATE", tnull);
        readjustM.setData("RPP_STATUS", "1");
        readjustM.setData("OPT_USER", parm.getData("OPT_USER"));
        readjustM.setData("OPT_DATE", parm.getData("OPT_DATE"));
        readjustM.setData("OPT_TERM", parm.getData("OPT_TERM"));

        result = this.onUpdate(readjustM, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        // 删除调价计划细项
        result = SYSFeeReadjustDTool.getInstance().onDeleteAll(readjustM, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        // 创建调价计划细项
        TParm readjustD = new TParm();
        String order_list = "";
        String own_list = "";
        String own2_list = "";
        String own3_list = "";
        //String nhi_list = "";
        //String gov_list = "";
        for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
            readjustD.setData("RPP_CODE", rpp_code);
            readjustD.setData("SEQ_NO", i);
            readjustD.setData("ORDER_CODE", orderParm.getValue("ORDER_CODE", i));
            order_list += orderParm.getValue("ORDER_CODE", i) + ";";
            readjustD.setData("START_DATE",
                              StringTool.getTimestamp(orderParm.
                getValue("START_DATE", i), "yyyyMMddHHmmss"));
            readjustD.setData("END_DATE",
                              StringTool.getTimestamp(orderParm.
                getValue("END_DATE", i), "yyyyMMddHHmmss"));
            readjustD.setData("OWN_PRICE", orderParm.getDouble("OWN_PRICE", i));
            own_list += orderParm.getDouble("OWN_PRICE", i) + ";";
            readjustD.setData("OWN_PRICE2", orderParm.getDouble("OWN_PRICE2", i));
            own2_list += orderParm.getDouble("OWN_PRICE2", i) + ";";
            readjustD.setData("OWN_PRICE3", orderParm.getDouble("OWN_PRICE3", i));
            own3_list += orderParm.getDouble("OWN_PRICE3", i) + ";";
            readjustD.setData("OPT_USER", parm.getData("OPT_USER"));
            readjustD.setData("OPT_DATE", parm.getData("OPT_DATE"));
            readjustD.setData("OPT_TERM", parm.getData("OPT_TERM"));
            result = SYSFeeReadjustDTool.getInstance().onInsert(readjustD, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }

        // 更新一次性批次
        TParm patchParm = new TParm();
        patchParm.setData("PARM_ORDER_CODE",
                          order_list.substring(0, order_list.length() - 1));
        patchParm.setData("PARM_OWN_PRICE",
                          own_list.substring(0, own_list.length() - 1));
        patchParm.setData("PARM_OWN_PRICE2",
                          own2_list.substring(0, own2_list.length() - 1));
        patchParm.setData("PARM_OWN_PRICE3",
                          own3_list.substring(0, own3_list.length() - 1));
        patchParm.setData("PARM_RPP_CODE", rpp_code);
        patchParm.setData("PATCH_DATE", orderParm.getValue("START_DATE", 0));
        patchParm.setData("OPT_USER", parm.getData("OPT_USER"));
        patchParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
        patchParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
        result = SYSPatchTool.getInstance().onUpdateNewPatch(patchParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        return result;
    }

    /**
     * 更新调价计划主项中的执行展开调价时间和完成状态
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateReadjustStatus(TParm parm, TConnection conn) {
        TParm result = this.update("updateReadjustStatus", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     *
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsertSYSFeeReadjust(TParm parm, TConnection conn) {
        TParm result = new TParm();
        TNull tnull = new TNull(Timestamp.class);
        //System.out.println("ORDER----" + parm);
        TParm orderParm = parm.getParm("ORDER");
        // 创建调价计划主项
        TParm readjustM = new TParm();
        String rpp_code = SystemTool.getInstance().getNo("ALL", "PUB",
            "SYS_FEE_READJUST", "No");
        //System.out.println("----1----");
        readjustM.setData("RPP_CODE", rpp_code);
        readjustM.setData("RPP_DESC", "调价计划" + rpp_code);
        readjustM.setData("DESCRIPTION", "");
        //PY1
        //PY2
        //SEQ
        readjustM.setData("RPP_USER", parm.getData("OPT_USER"));
        readjustM.setData("RPP_DATE", parm.getData("OPT_DATE"));
        readjustM.setData("CHK_USER", parm.getData("OPT_USER"));
        readjustM.setData("CHK_DATE", parm.getData("OPT_DATE"));

        //StringTool.getTimestamp(orderParm.getTimestamp())
        readjustM.setData("READJUST_DATE",
                          StringTool.getTimestamp(orderParm.getValue(
                              "START_DATE",
                              0), "yyyy/MM/dd HH:mm:ss"));
        readjustM.setData("READJUSTOP_DATE", tnull);
        readjustM.setData("RPP_STATUS", "1");
        readjustM.setData("OPT_USER", parm.getData("OPT_USER"));
        readjustM.setData("OPT_DATE", parm.getData("OPT_DATE"));
        readjustM.setData("OPT_TERM", parm.getData("OPT_TERM"));
        result = this.onInsert(readjustM, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        //System.out.println("----2----");

        // 创建调价计划细项
        TParm readjustD = new TParm();
        String order_list = "";
        String own_list = "";
        String own2_list = "";
        String own3_list = "";
        //String nhi_list = "";
        //String gov_list = "";
        //System.out.println("START----" + orderParm);
        for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
            //System.out.println("END----");
            readjustD.setData("RPP_CODE", rpp_code);
            readjustD.setData("SEQ_NO", i);
            readjustD.setData("ORDER_CODE", orderParm.getValue("ORDER_CODE", i));
            order_list += orderParm.getValue("ORDER_CODE", i) + ";";
            readjustD.setData("START_DATE",
                              StringTool.getTimestamp(orderParm.
                getValue("START_DATE", i), "yyyy/MM/dd HH:mm:ss"));
            readjustD.setData("END_DATE",
                              StringTool.getTimestamp(orderParm.
                getValue("END_DATE", i), "yyyyMMddHHmmss"));
            readjustD.setData("OWN_PRICE", orderParm.getDouble("OWN_PRICE", i));
            own_list += orderParm.getDouble("OWN_PRICE", i) + ";";
            readjustD.setData("OWN_PRICE2", orderParm.getDouble("OWN_PRICE2", i));
            own2_list += orderParm.getDouble("OWN_PRICE2", i) + ";";
            readjustD.setData("OWN_PRICE3", orderParm.getDouble("OWN_PRICE3", i));
            own3_list += orderParm.getDouble("OWN_PRICE3", i) + ";";
            readjustD.setData("OPT_USER", parm.getData("OPT_USER"));
            readjustD.setData("OPT_DATE", parm.getData("OPT_DATE"));
            readjustD.setData("OPT_TERM", parm.getData("OPT_TERM"));
            result = SYSFeeReadjustDTool.getInstance().onInsert(readjustD, conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }
        //System.out.println("----3----");

        // 回写SYS_FEE_HISTORY
        for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
            String start_date = orderParm.getValue("START_DATE", i).substring(0,
                4) +
                orderParm.getValue("START_DATE", i).substring(5, 7) +
                orderParm.getValue("START_DATE", i).substring(8, 10) +
                orderParm.getValue("START_DATE", i).substring(11, 13) +
                orderParm.getValue("START_DATE", i).substring(14, 16) +
                orderParm.getValue("START_DATE", i).substring(17, 19);
            TParm sysParm = new TParm(TJDODBTool.getInstance().update(SYSSQL.
                updateSYSFeeHistoryRppCode(orderParm.getValue("ORDER_CODE", i),
                                           start_date, rpp_code), conn));
            if (sysParm.getErrCode() < 0) {
                err("ERR:" + sysParm.getErrCode() + sysParm.getErrText()
                    + sysParm.getErrName());
                return sysParm;
            }
        }
        //System.out.println("----4----");

        // 调用批次方法，不创建批次
        TParm patchParm = new TParm();
        patchParm.setData("ORDER_CODE",
                          order_list.substring(0, order_list.length() - 1));
        patchParm.setData("OWN_PRICE",
                          own_list.substring(0, own_list.length() - 1));
        patchParm.setData("OWN_PRICE2",
                          own2_list.substring(0, own2_list.length() - 1));
        patchParm.setData("OWN_PRICE3",
                          own3_list.substring(0, own3_list.length() - 1));
        patchParm.setData("RPP_CODE", rpp_code);
        result = SYSFeeReadjustBatchTool.getInstance().
            onSYSFeeReadjustBatchAction(patchParm, conn);
        if (result.getErrCode() < 0) {
            return result;
        }
        //System.out.println("----5----");
        return result;
    }

}
