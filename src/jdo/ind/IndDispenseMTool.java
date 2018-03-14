package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TNull;
import java.sql.Timestamp;

/**
 * <p>
 * Title: 出入库管理主档Tool
 * </p>
 *
 * <p>
 * Description: 出入库管理主档Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.05.25
 * @version 1.0
 */
public class IndDispenseMTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static IndDispenseMTool instanceObject;

    /**
     * 得到实例
     *
     * @return IndStockMTool
     */
    public static IndDispenseMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndDispenseMTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public IndDispenseMTool() {
        setModuleName("ind\\INDDispenseMModule.x");
        onInit();
    }

    /**
     * 新增主项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsertM(TParm parm, TConnection conn) {
        TParm result = this.update("createNewDispenseM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询出库申请主档
     *
     * @param parm
     * @return
     */
    public TParm onQueryOutM(TParm parm) {
        String status = parm.getValue("STATUS");
        TParm result = new TParm();
        if ("A".equals(status)) {
            // 完成
            result = this.query("queryDispenseOutB", parm);
            // 过滤查询条件
            for (int i = result.getCount("DISPENSE_NO") - 1; i >= 0; i--) {
                if ("THI".equals(result.getValue("REQTYPE_CODE", i))) {
                    result.removeRow(i);
                }
            }
        }
        else if ("B".equals(status)) {
            // 未出库
            result = IndRequestMTool.getInstance().onQueryOutReqNo(parm);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
            //zhangyong20110517 注释掉 begin
//            String request_no = "";
//            for (int i = result.getCount("REQUEST_NO") - 1; i >= 0; i--) {
//                request_no = result.getValue("REQUEST_NO", i);
//                // 出库单
//                TParm dispense = new TParm(TJDODBTool.getInstance().select(
//                    INDSQL.getDispenseDByReqNo(request_no)));
//                // 申请单
//                TParm request = new TParm(TJDODBTool.getInstance().select(
//                    INDSQL.getRequestDByNo(request_no)));
//                if (dispense.getCount() > 0 &&
//                    dispense.getCount() == request.getCount()) {
//                    result.removeRow(i);
//                }
//            }
            //zhangyong20110517 注释掉 end
        }
        else {
            // 途中
            result = this.query("queryDispenseOutA", parm);
            // 过滤查询条件
            for (int i = result.getCount("DISPENSE_NO") - 1; i >= 0; i--) {
                if ("THI".equals(result.getValue("REQTYPE_CODE", i)) ||
                    "COS".equals(result.getValue("REQTYPE_CODE", i))) {
                    result.removeRow(i);
                }
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
     * 查询入库申请主档
     *
     * @param parm
     * @return
     */
    public TParm onQueryInM(TParm parm) {
        TNull tnull = new TNull(Timestamp.class);
        String status = parm.getValue("STATUS");
        TParm result = new TParm();
        if ("A".equals(status)) {
            // 完成
            result = this.query("queryDispenseOutB", parm);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
            // 过滤查询条件
            for (int i = result.getCount("DISPENSE_NO") - 1; i >= 0; i--) {
                if ("THO".equals(result.getValue("REQTYPE_CODE", i)) ||
                    "EXM".equals(result.getValue("REQTYPE_CODE", i)) ||
                    "WAS".equals(result.getValue("REQTYPE_CODE", i)) ||
                    "COS".equals(result.getValue("REQTYPE_CODE", i))) {
                    result.removeRow(i);
                }
            }
        }
        else {
            // 未入库
            // 查询出库在途
            result = this.query("queryDispenseOutA", parm);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
            // 过滤查询条件
            for (int i = result.getCount("DISPENSE_NO") - 1; i >= 0; i--) {
                if ("COS".equals(result.getValue("REQTYPE_CODE", i))) {
                    result.removeRow(i);
                }
            }
            // 其他入库方式:查询所有未入库
            TParm resultOther = IndRequestMTool.getInstance().onQueryOtherInNo(
                parm);
            if (resultOther.getErrCode() < 0) {
                err("ERR:" + resultOther.getErrCode()
                    + resultOther.getErrText() + resultOther.getErrName());
                return resultOther;
            }
            //int count = result.getCount("REQUEST_NO");
            if (resultOther.getCount("REQUEST_NO") > 0) {
                for (int i = 0; i < resultOther.getCount("REQUEST_NO"); i++) {
                    result.addData("REQUEST_NO", resultOther
                                   .getData("REQUEST_NO", i));
                    result.addData("REQTYPE_CODE", resultOther
                                   .getData("REQTYPE_CODE", i));
                    result.addData("APP_ORG_CODE", resultOther
                                   .getData("APP_ORG_CODE", i));
                    result.addData("TO_ORG_CODE", resultOther
                                   .getData("TO_ORG_CODE", i));
                    result.addData("REQUEST_DATE", resultOther
                                   .getData("REQUEST_DATE", i));
//                    result.setData("REQUEST_USER", count + i, resultOther
//                                   .getData("REQUEST_USER", i));
                    result.addData("REASON_CHN_DESC", resultOther
                                   .getData("REASON_CHN_DESC", i));
                    result.addData("DESCRIPTION", resultOther
                                   .getData("DESCRIPTION", i));
                    result.addData("UNIT_TYPE", resultOther.getData(
                        "UNIT_TYPE", i));
                    result.addData("URGENT_FLG", resultOther
                                   .getData("URGENT_FLG", i));
                    result.addData("DISPENSE_USER", "");
                    result.addData("DISPENSE_DATE", tnull);
                    result.addData("DISPENSE_NO", "");
                    result.addData("OPT_TERM", "");
                    result.addData("OPT_DATE", tnull);
                    result.addData("OPT_USER", "");
                    result.addData("WAREHOUSING_USER",  "");
                    result.addData("UPDATE_FLG",  "0");
                    result.addData("WAREHOUSING_DATE",  tnull);
                }
                result.setCount(resultOther.getCount("REQUEST_NO") +
                                result.getCount());
            }
        }
        return result;
    }

    /**
     * 更新主项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateM(TParm parm, TConnection conn) {
        TParm result = this.update("updateDispenseM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新主项状态
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateMFlg(TParm parm, TConnection conn) {
        TParm result = this.update("updateDispenseFlg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 药品种类出库汇总表
     *
     * @param parm
     * @return
     */
    public TParm onQueryDispense(TParm parm) {
        TParm result = this.query("getQueryDispense", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
