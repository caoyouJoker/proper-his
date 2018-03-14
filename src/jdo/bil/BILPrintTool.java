package jdo.bil;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.opb.OPBReceiptTool;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import com.dongyang.data.TNull;
import jdo.opd.OrderTool;

public class BILPrintTool extends TJDOTool {
    /**
     * 实例
     */
    public static BILPrintTool instanceObject;
    /**
     * 得到实例
     * @return BILPrintTool
     */
    public static BILPrintTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILPrintTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BILPrintTool() {
        onInit();
    }

    /**
     * 门诊医疗卡打票
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm saveEktOpb(TParm parm, TConnection connection) {
        //更新当前票号BIL_Invoice
        TParm printReceipt = (TParm) parm.getParm("BILINVOICE");
        //取号原则得到票据号
        String receiptNo = SystemTool.getInstance().getNo("ALL", "OPB",
                "RECEIPT_NO",
                "RECEIPT_NO");
        //System.out.println("新取账务顺序号》》》》" + receiptNo);
        //调用写票
        TParm result = BILInvoiceTool.getInstance().updateDatePrint(
                printReceipt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //插入打印的票据BIL_Invrcpt
        TParm bilInvrcpt = (TParm) parm.getParm("BILINVRICPT");
        //收据流水号
        bilInvrcpt.setData("RECEIPT_NO", receiptNo);
        bilInvrcpt.setData("CANCEL_FLG", "0");
        bilInvrcpt.setData("STATUS", "0");
        bilInvrcpt.setData("ADM_TYPE", parm.getValue("ADM_TYPE")); //====pangben 2012-3-19
        //调用写票据明细
        result = BILInvrcptTool.getInstance().insertData(bilInvrcpt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        result.addData("RECEIPT_NO", receiptNo);
        return result;
    }

    /**
     * 门诊收费
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm saveOpb(TParm parm, TConnection connection) {
//        System.out.println("门诊收费票据"+parm);
        //得到一个票据
        TParm result = null;
        TParm receiptOne = (TParm) parm.getParm("OPBRECEIPT");
        double arAmt = receiptOne.getDouble("AR_AMT");
        String billFlg = parm.getValue("billFlg"); //==========pangben modify 20110818 记账操作 N 记账，Y 不记账
        String contractCode = parm.getValue("CONTRACT_CODE"); //==========pangben modify 20110818 记账单位
        if (arAmt <= 0) {
            receiptOne.setData("PRINT_NO", new TNull(String.class));
        }
        //============= 记账操作 不保存票号和打印时间
        if ("N".equals(billFlg)) {
            receiptOne.setData("PRINT_NO", new TNull(String.class));
            receiptOne.setData("PRINT_DATE", new TNull(String.class));
            receiptOne.setData("PRINT_USER", new TNull(String.class));
        }
        //调用存储票据
        result = OPBReceiptTool.getInstance().initReceipt(receiptOne,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        //后台取号原则取出的此票号
        String receiptNo = result.getData("RECEIPT_NO").toString();
        //=========pangben 20110818 记账操作添加数据
        if ("N".equals(billFlg)) {
            receiptOne.setData("RECEIPT_NO", receiptNo);
            result = insertRecode(receiptOne, contractCode, connection);
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return result;
            }
            //将OPD_ORDER 表中的打印票据 PRINT_FLG列 改成没有 N 显示没有打票
            TParm p = new TParm();
            p.setData("CASE_NO", receiptOne.getValue("CASE_NO"));
            p.setData("PRINT_FLG", "N");
            result = OrderTool.getInstance().updateForRecode(p, connection);
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return result;
            }
        }
        //=========pangben 20110818 stop
        if (arAmt > 0) {
            if ("Y".equals(billFlg)) { //=============pangben 20110818 添加条件 ：是否记账 Y 不记账，打印票据  N 记账
                //更新当前票号BIL_Invoice
                TParm printReceipt = (TParm) parm.getParm("BILINVOICE");
                //调用写票
                result = BILInvoiceTool.getInstance().updateDatePrint(
                        printReceipt,
                        connection);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    return result;
                }
                //插入打印的票据BIL_Invrcpt
                TParm bilInvrcpt = (TParm) parm.getParm("BILINVRICPT");
                //收据流水号
                bilInvrcpt.setData("RECEIPT_NO", receiptNo);
                bilInvrcpt.setData("CANCEL_FLG", "0");
                bilInvrcpt.setData("STATUS", "0");
                bilInvrcpt.setData("ADM_TYPE", parm.getValue("ADM_TYPE")); //=====pangben 2012-3-19
                //调用写票据明细
                result = BILInvrcptTool.getInstance().insertData(bilInvrcpt,
                        connection);
                if (result.getErrCode() < 0) {
                    err("ERR:" + result.getErrCode() + result.getErrText() +
                        result.getErrName());
                    return result;
                }
            }
        } else {
            result.addData("PRINT_NOFEE", "Y");
        }
        //存储过程得到的票据号返回
        result.addData("RECEIPT_NO", receiptNo);
        return result;
    }

    /**
     * 添加BIL_CONTRACT_RECODE 表数据
     * @param parm TParm
     * @param CONTRACT_CODE String
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertRecode(TParm parm, String CONTRACT_CODE,
                              TConnection connection) {
        //=================pangben 20110817
        TParm reslut = new TParm();
        reslut.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO")); //收据号
        reslut.setData("CONTRACT_CODE", CONTRACT_CODE); //记账单位
        reslut.setData("ADM_TYPE", parm.getValue("ADM_TYPE")); //门急住别
        reslut.setData("REGION_CODE", parm.getValue("REGION_CODE")); //院区
        reslut.setData("CASHIER_CODE", parm.getValue("CASHIER_CODE")); //收费人员
        reslut.setData("CHARGE_DATE", SystemTool.getInstance().getDate()); //收费日期时间
        reslut.setData("RECEIPT_TYPE", "OPB"); //票据类型：REG 、OPB
        reslut.setData("DATA_TYPE", "OPB"); //扣款来源 REG OPB  HRM
        reslut.setData("CASE_NO", parm.getValue("CASE_NO")); //就诊号
        reslut.setData("MR_NO", parm.getValue("MR_NO"));
        reslut.setData("AR_AMT", parm.getValue("AR_AMT")); //应缴金额
        reslut.setData("BIL_STATUS", "1"); //记账状态1 记账  2 结算完成写入  =1 记账单位缴费时候 update =2
        reslut.setData("OPT_USER", parm.getValue("OPT_USER"));
        reslut.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        reslut.setData("RECEIPT_FLG", "1"); //状态：1 收费 2，退费
        return BILContractRecordTool.getInstance().insertRecode(reslut,
                connection);
    }

    /**
     * 预交金保存
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm saveBilPay(TParm parm, TConnection connection) {
        //得到一个票据
        TParm result = null;
        //后台取号原则取出的此票号
        String receiptNo = SystemTool.getInstance().getNo("ALL", "BIL",
                "RECEIPT_NO", "RECEIPT_NO");
        parm.setData("RECEIPT_NO", receiptNo);
        // System.out.println("预交金保存parm"+parm);
        parm.setData("PRINT_NO", parm.getValue("INV_NO"));
        parm.setData("RESET_RECP_NO", new TNull(String.class));
        //交预交金
        result = BILPayTool.getInstance().insertData(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        //更新当前票号BIL_Invoice
        TParm printReceipt = (TParm) parm.getParm("BILINVOICE");
        //调用写票
        result = BILInvoiceTool.getInstance().updateDatePrint(printReceipt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //插入打印的票据BIL_Invrcpt
        TParm bilInvrcpt = (TParm) parm.getParm("BILINVRICPT");
        //收据流水号
        bilInvrcpt.setData("RECEIPT_NO", receiptNo);
        bilInvrcpt.setData("CANCEL_FLG", "0");
        bilInvrcpt.setData("STATUS", "0");
        bilInvrcpt.setData("ADM_TYPE", "I");
        //调用写票据明细
        result = BILInvrcptTool.getInstance().insertData(bilInvrcpt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //存储过程得到的票据号返回
        result.addData("RECEIPT_NO", receiptNo);
        return result;
    }

    /**
     * 挂号收费
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm saveReg(TParm parm, TConnection connection) {
        //得到一个票据
        TParm result = null;
        TParm receiptOne = parm.getParm("REG_RECEIPT");
        //调用些收据
        result = BILREGRecpTool.getInstance().insertBill(receiptOne,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            connection.close();
            return result;
        }
        //记账不执行修改写票操作，不执行保存票据操作
        //============pangben 20110819
        if (null != receiptOne.getValue("PRINT_NO") &&
            receiptOne.getValue("PRINT_NO").length() > 0 &&
            !receiptOne.getValue("PRINT_NO").equals("<TNULL>")) {
            TParm printReceipt = parm.getParm("BIL_INVOICE");
            printReceipt.setData("UPDATE_NO",
                                 StringTool.addString(printReceipt.
                    getData("UPDATE_NO").toString()));
            //调用写票

            result = BILInvoiceTool.getInstance().updateDatePrint(printReceipt,
                    connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                connection.close();
                return result;
            }
//            System.out.println("调用写票::"+parm.getParm("BIL_INVRCP"));
            //插入打印的票据BIL_Invrcpt
            TParm bilInvrcpt = parm.getParm("BIL_INVRCP");
            bilInvrcpt.setData("CANCEL_FLG", "0");
            bilInvrcpt.setData("STATUS", "0");
            bilInvrcpt.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));
            //调用写票据明细
            result = BILInvrcptTool.getInstance().insertData(bilInvrcpt,
                    connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                connection.close();
                return result;
            }
//            System.out.println("调用写票据明细");
        }
        return result;
    }

    /**
     * 缴费作业更新票据档
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm saveIBSRecp(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //插入打印的票据主档BIL_Invrcpt
        TParm printReceipt = parm.getParm("BIL_INVOICE");
        printReceipt.setData("UPDATE_NO",
                             StringTool.addString(printReceipt.
                                                  getData("UPDATE_NO").toString()));
        //调用写票
        result = BILInvoiceTool.getInstance().updateDatePrint(printReceipt,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        //插入打印的票据明细档BIL_Invrcpt
        TParm bilInvrcpt = parm.getParm("BIL_INVRCP");
        bilInvrcpt.setData("CANCEL_FLG", "0");
        bilInvrcpt.setData("STATUS", "0");
        bilInvrcpt.setData("ADM_TYPE", "I");
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
     * 获得费用的中文名 
     * @param admType 门急住别
     * @return
     * 返回TParm的每行内容类似如下：“CHARGE01=抗生素”
     */
    public TParm getChargeDesc(String admType) {//add by wanglong 20130306
        String descSql = "SELECT ID, CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_CHARGE'";
        TParm descParm = new TParm(TJDODBTool.getInstance().select(descSql));
        String chargeSql = "SELECT * FROM BIL_RECPPARM A WHERE A.ADM_TYPE = '" + admType + "'";
        TParm chargeParm = new TParm(TJDODBTool.getInstance().select(chargeSql));
        TParm result = new TParm();
        for (int i = 1; i <= 20; i++) {
            String rexpCode = chargeParm.getValue("CHARGE" + StringTool.fill0(i + "", 2), 0);
            for (int j = 0; j < descParm.getCount(); j++) {
                if (descParm.getValue("ID", j).equals(rexpCode)) {
                    result.setData("CHARGE" + StringTool.fill0(i + "", 2),
                                   descParm.getValue("CHN_DESC", j).replaceAll("\\(住\\)", "")
                                           .replaceAll("（住）", ""));
                }
            }
        }
        return result;
    }
}
