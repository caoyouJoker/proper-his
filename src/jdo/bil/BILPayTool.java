package jdo.bil;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * <p>Title: 预交金工具类</p>
 *
 * <p>Description: 预交金工具类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class BILPayTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static BILPayTool instanceObject;
    /**
     * 得到实例
     * @return BILPayTool
     */
    public static BILPayTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILPayTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BILPayTool() {
        setModuleName("bil\\BILPayModule.x");
        onInit();
    }
    /**
     * 查询全部数据(根据就诊序号或病案号)
     * @param parm TParm
     * @return TParm
     */
    public TParm selectAllData(TParm parm) {
        TParm result = query("selectAllData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 查询全部数据(根据预交金收据号)
     * @param receiptNo String
     * @return TParm
     */
    public TParm selAllDataByRecpNo(String receiptNo) {
        TParm parm = new TParm();
        parm.setData("RECEIPT_NO", receiptNo);
        TParm result = query("selAllDataByRecpNo", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 费用清单
     * @param parm TParm
     * @return TParm
     */
    public TParm selAllDataByRecpNo(TParm parm) {
        TParm result = query("selAllDataByRecpNo", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 根据ID号查询最大就诊序号
     * @param parm TParm
     * @return TParm mrNo
     */
    public TParm selectPatCaseNo(TParm parm) {
        TParm result = query("selectPatCaseNo", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * (根据就诊序号)查询病患基本信息(出院也可退费)
     * @param parm TParm
     * @return TParm
     */
    public TParm seldataByCaseNo(TParm parm) {
        TParm result = query("seldataByCaseNo", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * (根据病案号)查询病患基本信息(出院也可退费)
     * @param parm TParm
     * @return TParm
     */
    public TParm seldataByIpdNo(TParm parm) {
        TParm result = query("seldataByIpdNo", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 新增预交金数据(交预交金)
     * @param parm TParm
     * @return TParm
     */
    public TParm insertData(TParm parm) {
        TParm result = update("insertData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 新增预交金数据(交预交金)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection connection) {
        TParm result = update("insertData", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 更新数据数据
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updataData(TParm parm, TConnection connection) {
        TParm result = update("updataData", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 查询预交金数据(缴费作业)
     * @param parm TParm
     * @return TParm
     */
    public TParm selDataForCharge(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "BILPayTool.selDataForCharge()参数异常!");
            return result;
        }
        result = query("selDataForCharge", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }
    /**
     * 更新冲销收据号
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updataOffBilPay(TParm parm, TConnection connection) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "BILPayTool.updataOffBilPay()参数异常!");
            return result;
        }
        result = update("updataOffBilPay", parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }
    /**
     * 更新预交金补印收据
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm upRecpForRePrint(TParm parm, TConnection connection) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "BILPayTool.updataOffBilPay()参数异常!");
            return result;
        }
        result = update("upRecpForRePrint", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }
    /**
     * 预交金余额
     * @param caseNo String
     * @return TParm
     */
    public TParm selBilPayLeft(String caseNo){
        String sql =
            "SELECT SUM (PRE_AMT) AS PRE_AMT FROM BIL_PAY "+
            " WHERE CASE_NO='"+caseNo+"' "+
            "   AND TRANSACT_TYPE IN ('01', '02') ";
        TParm  result = new TParm();
        result.setData(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

//    /**
//     * 退预交金操作
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onReturnBILPay(TParm parm) {
//        TParm result = TIOM_AppServer.executeAction("action.bil.BILPayAction",
//            "onReturnFee", parm);
//        if (result.getErrCode() < 0) {
//            err(result.getErrCode() + " " + result.getErrText());
//            return result;
//        }
//        return result;
//    }
}
