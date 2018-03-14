package jdo.spc;

import java.sql.Timestamp;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

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
        setModuleName("spc\\INDDispenseMModule.x");
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
     * 新增主项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsertMAcnt(TParm parm, TConnection conn) {//wanglong add 20150202
        TParm result = this.update("createNewDispenseMAcnt", parm, conn);
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
     * 查询出库申请主档
     *
     * @param parm
     * @return
     */
    public TParm onQueryOutMAcnt(TParm parm) {//wanglong add 20150202
        String status = parm.getValue("STATUS");
        TParm result = new TParm();
        if ("A".equals(status)) {
            // 完成
            result = this.query("querySPCDispenseOutB", parm);
            // 过滤查询条件
            for (int i = result.getCount("DISPENSE_NO") - 1; i >= 0; i--) {
                if ("THI".equals(result.getValue("REQTYPE_CODE", i))) {
                    result.removeRow(i);
                }
            }
        }
        else if ("B".equals(status)) {
            // 未出库
            result = IndRequestMTool.getInstance().onQueryOutReqNoAcnt(parm);
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
            result = this.query("querySPCDispenseOutA", parm);
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
     * 查询入库申请主档
     *
     * @param parm
     * @return
     */
    public TParm onQueryInMAcnt(TParm parm) {//wanglong add 20150202
        TNull tnull = new TNull(Timestamp.class);
        String status = parm.getValue("STATUS");
        TParm result = new TParm();
        if ("A".equals(status)) {
            // 完成
            result = this.query("querySPCDispenseOutB", parm);
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
            result = this.query("querySPCDispenseOutA", parm);
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
            TParm resultOther = IndRequestMTool.getInstance().onQueryOtherInNoAcnt(
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
     * 更新主项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateMAcnt(TParm parm, TConnection conn) {//wanglong add 20150202
        TParm result = this.update("updateDispenseMAcnt", parm, conn);
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
     * 更新主项状态
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateMFlgAcnt(TParm parm, TConnection conn) {//wanglong add 20150202
        TParm result = this.update("updateDispenseFlgAcnt", parm, conn);
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
    
    /**
     * 药品种类出库汇总表
     *
     * @param parm
     * @return
     */
    public TParm onQueryDispenseAcnt(TParm parm) {//wanglong add 20150202
        TParm result = this.query("getQueryDispenseAcnt", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    public TParm onQueryMByNo(String dispenseNo){
    	String sql = " SELECT DISPENSE_NO, REQTYPE_CODE, REQUEST_NO, REQUEST_DATE, APP_ORG_CODE, "+
    				 " TO_ORG_CODE, URGENT_FLG, DISPENSE_DATE, DISPENSE_USER, WAREHOUSING_DATE,  "+
    				 " WAREHOUSING_USER, UNIT_TYPE, UPDATE_FLG, OPT_USER, OPT_DATE, "+
    				 " OPT_TERM, REGION_CODE, DRUG_CATEGORY" +
    				 " FROM IND_DISPENSEM "+
    				 " WHERE DISPENSE_NO='"+dispenseNo+"' ";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getCount() >  0 ){
    		return result.getRow(0);
    	}
    	return null;
    }

    public TParm onQueryMByNoAcnt(String dispenseNo){//wanglong add 20150202
        String sql = " SELECT DISPENSE_NO, REQTYPE_CODE, REQUEST_NO, REQUEST_DATE, APP_ORG_CODE, "+
                     " TO_ORG_CODE, URGENT_FLG, DISPENSE_DATE, DISPENSE_USER, WAREHOUSING_DATE,  "+
                     " WAREHOUSING_USER, UNIT_TYPE, UPDATE_FLG, OPT_USER, OPT_DATE, "+
                     " OPT_TERM, REGION_CODE, DRUG_CATEGORY" +
                     " FROM SPC_DISPENSEM "+
                     " WHERE DISPENSE_NO='"+dispenseNo+"' ";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if(result.getCount() >  0 ){
            return result.getRow(0);
        }
        return null;
    }
    
    /**
     * 更新主表出库时间
     *
     * @param parm
     * @param conn
     * @return
     * @author wangb 2015/06/16
     */
    public TParm onUpdateMAcntOutDate(TParm parm) {
        StringBuffer sbSql = new StringBuffer();
        sbSql.append("UPDATE SPC_DISPENSEM SET DISPENSE_DATE = TO_DATE('");
        sbSql.append(parm.getValue("DISPENSE_DATE"));
        sbSql.append("','YYYY/MM/DD HH24:MI:SS'),OPT_USER='");
        sbSql.append(parm.getValue("OPT_USER"));
        sbSql.append("',OPT_DATE=SYSDATE,OPT_TERM='");
        sbSql.append(parm.getValue("OPT_TERM"));
        sbSql.append("' WHERE DISPENSE_NO = '");
        sbSql.append(parm.getValue("DISPENSE_NO"));
        sbSql.append("'");
        
        TParm result = new TParm(TJDODBTool.getInstance().update(sbSql.toString()));
        
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
}
