package action.bil;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import jdo.bil.BILContractRecordTool;
import com.dongyang.db.TConnection;
import jdo.sys.SystemTool;
import jdo.bil.BILInvoiceTool;
import com.dongyang.util.StringTool;
import jdo.bil.BILInvrcptTool;
import jdo.bil.BilInvoice;
import jdo.bil.BILREGRecpTool;
import jdo.sys.Operator;
import jdo.opd.OrderTool;
import jdo.opb.OPBReceiptTool;

/**
 * <p>Title: 记账单位账务结算</p>
 *
 * <p>Description: 记账单位账务结算</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110817
 * @version 1.0
 */
public class BILContractRecordAction extends TAction {

    public BILContractRecordAction() {
    }

    /**
     * 添加方法
     * @param parm TParm
     * @return TParm
     */
    public TParm insertRecode(TParm parm) {
        TConnection connection = getConnection();
        TParm result = BILContractRecordTool.getInstance().insertRecode(parm,
                connection);
        if (result == null || result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 执行保存操作
     * @param parm TParm
     * @return TParm
     */
    public TParm onSave(TParm parm) {
        TConnection connection = getConnection();
        TParm result = null;
        TParm recodeParm=parm.getParm("recodeParm");
        for (int i = 0; i < recodeParm.getCount(); i++) {
            // 勾选的执行操作
            if (recodeParm.getBoolean("FLG", i)) {
                TParm recode=recodeParm.getRow(i);
                recode.setData("OPT_USER",parm.getValue("OPT_USER"));
                recode.setData("OPT_TERM",parm.getValue("OPT_TERM"));
                //挂号操作
                if ("REG".equals(recode.getValue("RECEIPT_TYPE"))) {
                    result = regSave(recode, connection);
                } else {
                    //计费操作
                    result = opbSave(recode, connection);
                }
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    connection.close();
                    return result;
                }

                //修改完成状态
                TParm parmOne=recodeParm.getRow(i);
                parmOne.setData("BIL_STATUS","2");
                parmOne.setData("RECEIPT_FLG","1");
                parmOne.setData("OPT_USER",parm.getValue("OPT_USER"));
                parmOne.setData("OPT_TERM",parm.getValue("OPT_TERM"));
                result = BILContractRecordTool.getInstance().updateRecode(
                        parmOne, connection);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    connection.close();
                    return result;
                }

            }
            connection.commit();
        }
        connection.close();
        return result;
    }

    /**
     * 挂号操作
     * 修改BIL_REG_RECP 表 BIL_INVOICE 表
     * 添加 BIL_INVRCP 表
     * @return TParm
     */
    private TParm regSave(TParm parm, TConnection connection) {
//        String receiptNo = SystemTool.getInstance().getNo("ALL", "REG",
//                "RECEIPT_NO",
//                "RECEIPT_NO");
        //获得当前票据号
        TParm parms = new TParm();
        parms.setData("RECP_TYPE", "REG");
        parms.setData("CASHIER_CODE", parm.getValue("OPT_USER"));
        parms.setData("STATUS", "0");
        parms.setData("TERM_IP", parm.getValue("OPT_TERM"));
        TParm noParm = BILInvoiceTool.getInstance().selectNowReceipt(parms);
        //   return null;


        //修改票号主档表
        TParm printReceipt = noParm.getRow(0);
        printReceipt.setData("UPDATE_NO",
                             StringTool.addString(noParm.getValue("UPDATE_NO",0)));
        //调用写票
        TParm result = BILInvoiceTool.getInstance().updateDatePrint(
                printReceipt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }

        //修改挂号收据表添加票号、票号时间
        TParm newDataRecpParm = new TParm();
        newDataRecpParm.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
        newDataRecpParm.setData("PRINT_NO", noParm.getValue("UPDATE_NO",0));
        newDataRecpParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        newDataRecpParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        newDataRecpParm.setData("OPT_USER", parm.getValue("OPT_USER"));
        result = BILREGRecpTool.getInstance().upRecpForRePrint(
                newDataRecpParm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }

        //插入打印的票据BIL_Invrcpt
        TParm bilInvrcpt = new TParm();
        bilInvrcpt.setData("RECP_TYPE", "REG");
        bilInvrcpt.setData("INV_NO", noParm.getValue("UPDATE_NO",0));
        bilInvrcpt.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
        bilInvrcpt.setData("CASHIER_CODE", parm.getValue("OPT_USER"));
        bilInvrcpt.setData("AR_AMT", parm.getValue("AR_AMT"));
        bilInvrcpt.setData("OPT_USER", parm.getValue("OPT_USER"));
        bilInvrcpt.setData("CANCEL_FLG", "0");
        bilInvrcpt.setData("STATUS", "0");
        bilInvrcpt.setData("ADM_TYPE", "O");
        bilInvrcpt.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        //调用写票据明细
        result = BILInvrcptTool.getInstance().insertData(bilInvrcpt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        return result;
    }

    /**
     * 计费方法
     * 修改 BIL_OPB_RECP 表 BIL_INVOICE 表 OPD_ORDER 表
     * 添加 BIL_INVRCP 表
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    private TParm opbSave(TParm parm, TConnection connection) {

        TParm result = null;

        //BIL_INVOICE 表
        //获得当前票据号
        TParm parms = new TParm();
        parms.setData("RECP_TYPE", "OPB");
        parms.setData("CASHIER_CODE", parm.getValue("OPT_USER"));
        parms.setData("STATUS", "0");
        parms.setData("TERM_IP", parm.getValue("OPT_TERM"));
        TParm noParm = BILInvoiceTool.getInstance().selectNowReceipt(parms);
        TParm printReceipt = noParm.getRow(0);
        printReceipt.setData("UPDATE_NO",
                             StringTool.addString(noParm.getValue("UPDATE_NO",0)));
        result = BILInvoiceTool.getInstance().updateDatePrint(
                printReceipt,
                connection);

        //OPD_ORDER 表修改
        TParm p = new TParm();
        p.setData("CASE_NO", parm.getValue("CASE_NO"));
        p.setData("PRINT_FLG", "Y");
        p.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
        result = OrderTool.getInstance().updateForRecode(p, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        //BIL_OPB_RECP 表
        //修改门诊收据表添加票号、票号时间
        TParm opbReceipt = new TParm();
        opbReceipt.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
        opbReceipt.setData("PRINT_NO", noParm.getValue("UPDATE_NO",0));
        opbReceipt.setData("CASE_NO", parm.getValue("CASE_NO"));
        opbReceipt.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        opbReceipt.setData("OPT_USER", parm.getValue("OPT_USER"));
        result = OPBReceiptTool.getInstance().updatePrintNO(opbReceipt,
                connection);
        //插入打印的票据BIL_Invrcpt
        TParm bilInvrcpt = new TParm();
        bilInvrcpt.setData("RECP_TYPE", "OPB");
        bilInvrcpt.setData("INV_NO", noParm.getValue("UPDATE_NO",0));
        bilInvrcpt.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
        bilInvrcpt.setData("CASHIER_CODE", parm.getValue("OPT_USER"));
        bilInvrcpt.setData("AR_AMT", parm.getValue("AR_AMT"));
        bilInvrcpt.setData("OPT_USER", parm.getValue("OPT_USER"));
        bilInvrcpt.setData("CANCEL_FLG", "0");
        bilInvrcpt.setData("STATUS", "0");
        bilInvrcpt.setData("ADM_TYPE", "O");
        bilInvrcpt.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        //调用写票据明细
        result = BILInvrcptTool.getInstance().insertData(bilInvrcpt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        return result;
    }
}

