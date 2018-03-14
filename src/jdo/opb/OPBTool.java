package jdo.opb;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

import jdo.odo.OpdOrderHistory;
import jdo.opd.OrderTool;

import com.dongyang.util.StringTool;

import jdo.adm.ADMInpTool;
import jdo.bil.BILPrintTool;
import jdo.opd.OrderList;
import jdo.bil.BILInvrcptTool;
import jdo.bil.BILInvoiceTool;

import com.dongyang.data.TNull;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.omg.PortableServer.ForwardRequestHelper;

import jdo.ekt.EKTNewTool;
import jdo.ekt.EKTTool;
import jdo.bil.BILContractRecordTool;

import com.dongyang.jdo.TJDODBTool;
import com.javahis.ui.testOpb.tools.AssembleTool;
import com.javahis.util.StringUtil;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.ekt.EKTGreenPathTool;
import jdo.hl7.Hl7Communications;
import jdo.ins.INSOpdOrderTJTool;
import jdo.ins.INSOpdTJTool;
import jdo.reg.PatAdmTool;
import jdo.reg.Reg;

/**
 * <p>
 * Title: 门诊费用
 * </p>
 *
 * <p>
 * Description:门诊费用
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author fudw 20090709
 * @version 1.0
 */
public class OPBTool extends TJDOTool {
    /**
     * 实例
     */
    public static OPBTool instanceObject;

    /**
     * 得到实例
     *
     * @return INVVerifyinTool
     */
    public static OPBTool getInstance() {
        if (instanceObject == null)
            instanceObject = new OPBTool();
        return instanceObject;
    }

    private String[] chargName = {"CHARGE01", "CHARGE02", "CHARGE03",
     "CHARGE04", "CHARGE05", "CHARGE06", "CHARGE07","CHARGE08",
     "CHARGE09", "CHARGE10", "CHARGE11", "CHARGE12","CHARGE13",
     "CHARGE14", "CHARGE15", "CHARGE16", "CHARGE17","CHARGE18",
     "CHARGE19", "CHARGE20", "CHARGE21", "CHARGE22","CHARGE23",
     "CHARGE24", "CHARGE25", "CHARGE26", "CHARGE27","CHARGE28","CHARGE29", "CHARGE30"};
	// 医疗卡 删除医嘱操作 点选取消按钮 执行撤销删除医嘱操作，门诊医生站使用==pangben 2013-5-2
	public static final String[] orderName = { "RX_NO", "SEQ_NO", "PRESRT_NO",
	"REGION_CODE", "MR_NO", "ADM_TYPE", "RX_TYPE", "TEMPORARY_FLG","RELEASE_FLG", "LINKMAIN_FLG", "LINK_NO", "SPECIFICATION",
	"GOODS_DESC", "ORDER_CAT1_CODE", "MEDI_QTY", "MEDI_UNIT","FREQ_CODE", "ROUTE_CODE", "TAKE_DAYS", "DOSAGE_QTY",
	"DISPENSE_QTY", "DISPENSE_UNIT", "GIVEBOX_FLG", "OWN_PRICE","NHI_PRICE", "DISCOUNT_RATE", "OWN_AMT", "AR_AMT", "DR_NOTE",
	"NS_NOTE", "DR_CODE", "ORDER_DATE", "DEPT_CODE", "DC_DR_CODE","DC_ORDER_DATE", "DC_DEPT_CODE", "EXEC_DEPT_CODE", "SETMAIN_FLG",
	"ORDERSET_GROUP_NO", "ORDERSET_CODE", "HIDE_FLG", "RPTTYPE_CODE","OPTITEM_CODE", "DEV_CODE", "MR_CODE", "FILE_NO", "DEGREE_CODE",
	"URGENT_FLG", "INSPAY_TYPE", "PHA_TYPE", "DOSE_TYPE","PRINTTYPEFLG_INFANT", "EXPENSIVE_FLG", "CTRLDRUGCLASS_CODE",
	"PRESCRIPT_NO", "ATC_FLG", "SENDATC_DATE", "RECEIPT_NO","BILL_USER", "PRINT_FLG", "REXP_CODE", "HEXP_CODE",
	"CONTRACT_CODE", "CTZ1_CODE", "CTZ2_CODE", "CTZ3_CODE","PHA_CHECK_CODE", "PHA_CHECK_DATE", "PHA_DOSAGE_CODE",
	"PHA_DOSAGE_DATE", "PHA_DISPENSE_CODE", "PHA_DISPENSE_DATE","NS_EXEC_CODE", "NS_EXEC_DATE", "NS_EXEC_DEPT", "DCTAGENT_CODE",
	"DCTEXCEP_CODE", "DCT_TAKE_QTY", "MED_APPLY_NO", "DOSAGE_UNIT","PACKAGE_TOT", "AGENCY_ORG_CODE", "DCTAGENT_FLG", "DECOCT_CODE",
	"EXEC_FLG", "RECEIPT_FLG", "BILL_TYPE", "CAT1_TYPE","COST_CENTER_CODE", "EXEC_DR_CODE", "BUSINESS_NO", "PRINT_NO","TRADE_ENG_DESC" };
	//===pangben 2013-5-2 门诊医生站清空操作 获得控件名称
	public static final String [] controlName={"QUE_NO","PAT_NAME","MR_NO","SEX_CODE","AGE","WEIGHT",
	   "PRE_WEEK","CTZ1_CODE","CTZ2_CODE","CTZ3_CODE","PAT1_CODE","PAT2_CODE","PAT3_CODE","DRG_CODE","LMP_DATE","EXA_RX","OP_RX",
	   "MED_RX","CHN_RX","CTRL_RX","OP_EXEC_DEPT","MED_RBORDER_DEPT_CODE","CHN_EXEC_DEPT_CODE","CTRL_RBORDER_DEPT_CODE","DR_NOTE","CHN_FREQ_CODE",
	   "CHN_ROUTE_CODE","DCTAGENT_CODE","DR_NOTE","PACKAGE_TOT", "CHN_AMT","BREASTFEED_STARTDATE","BREASTFEED_ENDDATE"};
	public static final String [] controlNameAmt={"EXA_CARD","OP_CARD","MED_CARD","CHN_CARD","CTRL_CARD","EXA_AMT",
		"OP_AMT","MED_AMT","CTRL_AMT","CHN_AMT","PACKAGE_TOT"};//医生站金额控件 ===pangben 2013-5-2
	public static final String [] controlNameCombo={"EXA_RX","OP_RX","MED_RX","CHN_RX","CTRL_RX"};
	//医生站表格控件 ===pangben 2013-5-2
	public static final String [] controlNameTable={"TABLEEXA","TABLEOP","TABLEMED","TABLECHN","TABLECTRL","TABLEDIAGNOSIS","TABLEMEDHISTORY","TABLEALLERGY"};
    /**
     * 根据case查询医嘱,用于初始化处方签
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm opbQuery(TParm parm) {
        TParm result = new TParm();
        // 得到问诊号
        String caseNo = parm.getValue("CASE_NO");
        if (caseNo.length() == 0)
            return err( -1, "CASE_NO is null");
        // 拿到病患order
        TParm orderresult = OrderTool.getInstance().query(caseNo);
        if (orderresult.getErrCode() != 0) {
            return result.newErrParm( -1, "捞取医嘱错误!");
        }
        // 返回order
        result.setData("ORDER", orderresult.getData());
        return result;

    }

    /**
     * 门诊收费保存
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm opbCharge(TParm parm, TConnection connection) {
        TParm result = new TParm();
        // 保存医嘱
        TParm orderparm = parm.getParm("ORDER");
        orderparm.setData("FLG", parm.getData("FLG"));
        result = OrderTool.getInstance().onSaveOPB(orderparm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        return result;
    }

    /**
     * 给医嘱添加票据流水号
     *
     * @param parm
     *            TParm
     * @param receiptNo
     *            String
     */
    public void dealOrderReceiptNo(TParm parm, String receiptNo) {
        TParm newOrderParm = parm.getParm(OrderList.NEW);
        int row = newOrderParm.getCount();
        for (int i = 0; i < row; i++) {
            newOrderParm.setData("RECEIPT_NO", i, receiptNo);
        }
        TParm modifyOrderParm = parm.getParm(OrderList.MODIFIED);
        row = modifyOrderParm.getCount();
        for (int i = 0; i < row; i++) {
            modifyOrderParm.setData("RECEIPT_NO", i, receiptNo);
        }

    }

    /**
     * 保存退费
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm backReceipt(TParm parm, TConnection connection) {
        TParm result = new TParm();
        // 得到退费插入负数的票据
        TParm receiptBaceParm = parm.getParm("INRECEIPT");
        // 插入票据档
        result = OPBReceiptTool.getInstance().insertBackReceipt(
                receiptBaceParm, false, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            connection.close();
            return result;
        }
        // 后台取号原则取出的此票号
        String receiptNo = result.getData("RECEIPT_NO").toString();
        // 更新的退费票据
        TParm upReceiptParm = parm.getParm("UPRECEIPT");
        upReceiptParm.setData("RESET_RECEIPT_NO", receiptNo);
        result = OPBReceiptTool.getInstance().updateBackReceipt(upReceiptParm,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            connection.close();
            return result;
        }
        // 作废票据
        TParm bilInvrcptParm = parm.getParm("BILINVRCPT");
        result = BILInvrcptTool.getInstance().updataData(bilInvrcptParm,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            connection.close();
            return result;
        }
        // 更新医嘱
        TParm orderListParm = parm.getParm("ORDER");
        orderListParm.setData("FLG", "N");
        result = OrderTool.getInstance().onUpdate(orderListParm, connection);
        if (result.getErrCode() < 0)
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
        return result;

    }

    /**
     * 门诊收费补打票据:医疗卡补打
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */

    public TParm saveOPBEKTRePrint(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = updateBilinvoice(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            connection.close();
            return result;
        }
        return result;
    }

    /**
     * 修改医保操作票据号码
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    private TParm updateInsInvNo(TParm parm, TConnection connection) {
        TParm tempParm = new TParm();
        // System.out.println("---------------医保退费-------------:" + opbParm);
        tempParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        tempParm.setData("INV_NO", parm.getValue("PRINT_NO"));
        tempParm.setData("RECP_TYPE", "OPB"); // 收费类型

        // 查询是否医保 退费
        TParm result = INSOpdTJTool.getInstance().selectInsInvNo(tempParm);
        if (result.getErrCode() < 0) {
            return result;
        }
        if (null != result && null != result.getValue("CONFIRM_NO", 0)
            && result.getValue("CONFIRM_NO", 0).length() > 0) {
            tempParm.setData("CONFIRM_NO", result.getValue("CONFIRM_NO", 0));
            tempParm.setData("INV_NO", parm.getValue("NEW_PRINT_NO")); //新的票据号码
            //修改医保主档票据号码
            result = INSOpdTJTool.getInstance().updateInsOpdInvNo(tempParm,
                    connection);
            if (result.getErrCode() < 0) {
                return result;
            }
            //修改医保票据号码
            result = INSOpdOrderTJTool.getInstance().updateInsOpdOrderInvNo(
                    tempParm, connection);
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    }

    /**
     * 更新票据主档表：现金和医疗卡
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    private TParm updateBilinvoice(TParm parm, TConnection connection) {

        String caseNo = parm.getValue("CASE_NO");
        TParm selInvoice = new TParm();
        selInvoice.setData("STATUS", "0");
        selInvoice.setData("RECP_TYPE", "OPB");
        int ektCount = parm.getInt("COUNT"); // 判断是否执行医疗卡补印操作
        selInvoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        selInvoice.setData("TERM_IP", parm.getData("OPT_TERM"));
        // System.out.println("票据主档数据" + selInvoice);
        TParm invoice = BILInvoiceTool.getInstance().selectNowReceipt(
                selInvoice);
        String invNo = invoice.getValue("UPDATE_NO", 0);
        // System.out.println("票据主档数据" + invoice);
        TParm prtInvoice = new TParm();
        prtInvoice.setData("UPDATE_NO", StringTool.addString(invNo));
        prtInvoice.setData("RECP_TYPE", "OPB");
        prtInvoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        prtInvoice.setData("STATUS", "0");
        prtInvoice.setData("START_INVNO", invoice.getData("START_INVNO", 0));
        //======pangben 2012-3-19
        parm.setData("NEW_PRINT_NO", invNo);
        TParm result = updateInsInvNo(parm, connection); //修改医保票据号码
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // System.out.println("票据主档数据All======"+prtInvoice);
        // 调用更新票据
        result = BILInvoiceTool.getInstance().updateDatePrint(prtInvoice,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // 保存费用和票据参数
        TParm opbReceipt = new TParm();
        opbReceipt.setData("OPT_USER", parm.getData("OPT_USER"));
        opbReceipt.setData("OPT_DATE", parm.getData("OPT_DATE"));
        opbReceipt.setData("OPT_TERM", parm.getData("OPT_TERM"));
        // 查找原来票据参数
        TParm selInvrcp = new TParm();
        selInvrcp.setData("INV_NO", parm.getData("PRINT_NO"));
        // 更新原来票据参数
        TParm invRcpParm = new TParm();
        invRcpParm.setData("CANCEL_FLG", "3");
        invRcpParm.setData("CANCEL_USER", parm.getData("OPT_USER"));
        invRcpParm.setData("OPT_USER", parm.getData("OPT_USER"));
        invRcpParm.setData("OPT_TERM", parm.getData("OPT_TERM"));

        // 票据明细档新增一条新数据
        TParm insertInvrcp = new TParm();
        if (ektCount > 0) { // 医疗卡补印
            opbReceipt.setData("CASE_NO", caseNo);
            opbReceipt.setData("NEWPRINT_NO", invNo);
            opbReceipt.setData("PRINT_NO", parm.getValue("PRINT_NO"));
            selInvrcp.setData("RECP_TYPE", "OPB");
            invRcpParm.setData("RECP_TYPE", "OPB");
            insertInvrcp.setData("RECP_TYPE", "OPB");
            result = OPBReceiptTool.getInstance().updateEKTPrintNO(opbReceipt,
                    connection);
        } else { // 其它
            opbReceipt.setData("CASE_NO", caseNo);
            opbReceipt.setData("PRINT_NO", invNo);
            opbReceipt.setData("RECEIPT_NO", parm.getData("RECEIPT_NO"));
            selInvrcp.setData("RECP_TYPE", "OPB");
            invRcpParm.setData("RECP_TYPE", "OPB");
            insertInvrcp.setData("RECP_TYPE", "OPB");
            // 更新票据号
            result = OPBReceiptTool.getInstance().updatePrintNO(opbReceipt,
                    connection);
        }

        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }

        // 查询原票据明细档
        TParm oneInvParm = BILInvrcptTool.getInstance().getOneInv(selInvrcp);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        invRcpParm.setData("INV_NO", oneInvParm.getData("INV_NO", 0));
        // 补印,更新票据明细档票据状态
        result = BILInvrcptTool.getInstance()
                 .updataData(invRcpParm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        insertInvrcp.setData("INV_NO", invNo);
        insertInvrcp.setData("RECEIPT_NO", parm.getData("RECEIPT_NO"));
        insertInvrcp.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        insertInvrcp.setData("AR_AMT", oneInvParm.getValue("AR_AMT", 0));
        insertInvrcp.setData("CANCEL_FLG", "0");
        insertInvrcp.setData("STATUS", "0");
        insertInvrcp.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));
        insertInvrcp.setData("OPT_TERM", parm.getData("OPT_TERM"));
        insertInvrcp.setData("OPT_USER", parm.getData("OPT_USER"));
        result = BILInvrcptTool.getInstance().insertData(insertInvrcp,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        result.setData("PRINT_NO", invNo);
        result.setData("OLDPRINT_NO", parm.getValue("PRINT_NO"));
        return result;
    }

    /**
     * 门诊收费补打票据:现金补打
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm saveOPBRePrint(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = updateBilinvoice(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            connection.close();
            return result;
        }
        return result;
    }

    /**
     * 金额汇总
     * @param parm TParm
     * @param chargeDouble double[]
     * @return TParm
     */
    private TParm getInsertAmt(TParm parm, double[] chargeDouble) {
        TParm opbreceipt = new TParm();
        opbreceipt.setData("CASE_NO", parm.getData("CASE_NO"));
        opbreceipt.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));
        opbreceipt.setData("REGION_CODE", parm.getData("REGION_CODE"));
        opbreceipt.setData("MR_NO", parm.getData("MR_NO"));
        opbreceipt.setData("RESET_RECEIPT_NO", new TNull(String.class));
        opbreceipt.setData("PRINT_NO", parm.getData("INV_NO"));
        opbreceipt.setData("BILL_DATE", SystemTool.getInstance().getDate());
        opbreceipt.setData("CHARGE_DATE", SystemTool.getInstance().getDate());
        opbreceipt.setData("PRINT_DATE", SystemTool.getInstance().getDate());
        int index = 1;
        // 写入数据
        for (int i = 0; i < chargeDouble.length; i++) {
            String chargeTemp = "CHARGE";
            if (i < 9) {
                opbreceipt.setData(chargeTemp + "0" + index, chargeDouble[i]);
            } else {
                opbreceipt.setData(chargeTemp + index, chargeDouble[i]);
            }
            index++;
        }

        return opbreceipt;
    }

    /**
     * 门诊医生站修改医嘱操作  添加一条负值收据
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm reSetInsertOpbRecp(TParm parm, TConnection connection) {
        TParm temp = parm.getRow(0);
        temp.setData("OPT_USER_T", temp.getValue("OPT_USER"));
        temp.setData("OPT_TERM_T", temp.getValue("OPT_TERM"));
        TParm result = OPBReceiptTool.getInstance().insertBackReceipt(
                getOpbreceiptTemp(temp, false), false,
                connection);
        return result;
    }

    /**
     * 门诊收费医疗卡打票
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm =============pangben 201110224 修改医疗卡打票创建新收据号
     */
    public TParm onOPBEktprint(TParm parm, TConnection connection) {

        // double insAmt=getInsAmt(parm);//获得医保扣款金额
        TParm opbParm = parm.getParm("opdParm"); // 获得需要汇总的数据
        String caseNo=parm.getValue("CASE_NO");
        double[] chargeDouble = new double[30]; // 金额分类
        double[] sumAmt = chargeDouble(opbParm, chargeDouble, 1,caseNo);

        TParm opbreceipt = getInsertAmt(parm, chargeDouble);

        //System.out.println("打印收据入参:::::::"+opbParm);
        TParm result = new TParm();
        double accountAmt = parm.getDouble("ACCOUNT_AMT"); //医保金额
        TParm bilInvricpt = new TParm();
        bilInvricpt.setData("RECP_TYPE", "OPB"); // 医疗卡打票
        bilInvricpt.setData("CANCEL_USER", new TNull(String.class));
        bilInvricpt.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        bilInvricpt.setData("OPT_USER", parm.getData("OPT_USER"));
        bilInvricpt.setData("INV_NO", parm.getData("INV_NO"));
        bilInvricpt.setData("OPT_TERM", parm.getData("OPT_TERM"));
        bilInvricpt.setData("CANCEL_DATE", new TNull(Timestamp.class));
        bilInvricpt.setData("TOT_AMT", sumAmt[0]);

        bilInvricpt.setData("AR_AMT", sumAmt[0]);
        // bilInvricpt.setData("INS_AMT", insAmt);//医保扣款金额
        TParm bilInvoice = new TParm();
        bilInvoice.setData("RECP_TYPE", "OPB");
        bilInvoice.setData("STATUS", "0");
        bilInvoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        bilInvoice.setData("START_INVNO", parm.getData("START_INVNO"));
        bilInvoice.setData("UPDATE_NO", StringTool.addString(parm.getData(
                "INV_NO").toString()));
        TParm actionParm = new TParm();
        actionParm.setData("BILINVRICPT", bilInvricpt.getData());
        actionParm.setData("BILINVOICE", bilInvoice.getData());
        actionParm.setData("ADM_TYPE", parm.getValue("ADM_TYPE")); //===pangben 2012-3-19
        result = BILPrintTool.getInstance().saveEktOpb(actionParm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        String recpNo = result.getValue("RECEIPT_NO", 0); // 最新的收据号码

        opbreceipt.setData("TOT_AMT", sumAmt[0]); //总金额
        opbreceipt.setData("RECEIPT_NO", recpNo);
        opbreceipt.setData("REDUCE_REASON", new TNull(String.class));
        opbreceipt.setData("REDUCE_AMT", 0.00);
        opbreceipt.setData("REDUCE_DATE", new TNull(Timestamp.class));
        opbreceipt.setData("REDUCE_DEPT_CODE", new TNull(String.class));
        opbreceipt.setData("REDUCE_RESPOND", new TNull(String.class));
        opbreceipt.setData("AR_AMT", sumAmt[0]); //总金额
        opbreceipt.setData("PAY_CASH", 0.00);
        if (sumAmt[2] > 0) { // sumAmt[2]);绿色通道金额
            //此次操作使用绿色通道
            //判断使用绿色通道的金额是否大于医保金额
            if (sumAmt[2] >= accountAmt) {
                opbreceipt.setData("PAY_OTHER1", sumAmt[2] - accountAmt); //绿色通道金额
                opbreceipt.setData("PAY_MEDICAL_CARD", sumAmt[1]); // 扣除医保金额
            } else {
                //使用绿色通道金额小于医保金额
                opbreceipt.setData("PAY_OTHER1", 0.00); //绿色通道金额
                opbreceipt.setData("PAY_MEDICAL_CARD",
                                   sumAmt[1] - sumAmt[2] + accountAmt); // 扣除医保金额
            }
        } else {
            //没有使用绿色通道
            opbreceipt.setData("PAY_OTHER1", 0.00); //绿色通道金额
            opbreceipt.setData("PAY_MEDICAL_CARD", sumAmt[1] - accountAmt); // 扣除医保金额
        }

        opbreceipt.setData("PAY_BANK_CARD", 0.00);
        opbreceipt.setData("PAY_INS_CARD", accountAmt);
        opbreceipt.setData("PAY_CHECK", 0.00);
        opbreceipt.setData("PAY_DEBIT", 0.00);
        opbreceipt.setData("PAY_BILPAY", 0.00);
        opbreceipt.setData("PAY_INS", 0.00);
        opbreceipt.setData("PAY_OTHER2", 0.00);
        opbreceipt.setData("ALIPAY", 0.00); //add by huangtt 20160612支付宝
        opbreceipt.setData("QE_PAY_TYPE", new TNull(String.class)); 
        opbreceipt.setData("PAY_REMARK", new TNull(String.class));
        opbreceipt.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        opbreceipt.setData("OPT_USER", parm.getData("OPT_USER"));
        opbreceipt.setData("OPT_DATE", parm.getData("OPT_DATE"));
        opbreceipt.setData("OPT_TERM", parm.getData("OPT_TERM"));
        opbreceipt.setData("FLG", "Y");
        
        double medicalCardAmt = opbreceipt.getDouble("PAY_MEDICAL_CARD");//通过医疗卡当前余额 和 本次扣款金额 可以算出 医疗卡 扣款之前的金额 add by haungjw 20150804
        //System.out.println("添加一条汇总数据opbreceipt:::"+opbreceipt);
        // 添加一条汇总数据
        result = OPBReceiptTool.getInstance().initReceipt(opbreceipt,
                connection);
        TParm upOpdParm = new TParm();
        // upOpdParm.setData("RECEIPT_NO",recpNo);
        upOpdParm.setData("CASE_NO", parm.getData("CASE_NO"));
        upOpdParm.setData("RECEIPT_NO", recpNo); // 最新的收据号码
        upOpdParm.setData("PRINT_NO", parm.getData("INV_NO")); // 修改医疗卡打票票据号
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // 修改医嘱
        TParm upOpd = OrderTool.getInstance().updateForOPBEKT(upOpdParm,
                connection);
        if (upOpd.getErrCode() < 0) {
            err("ERR:" + upOpd.getErrCode() + upOpd.getErrText()
                + upOpd.getErrName());
            return upOpd;
        }
        
        //插入历史表数据  add by huangtt 20160919 start 
        String sql = "SELECT * FROM  OPD_ORDER " +
        		" WHERE CASE_NO='"+parm.getData("CASE_NO")+"'" +
        		" AND BILL_FLG = 'Y' " +
        		" AND BILL_TYPE = 'E' " +
        		" AND QE_BILL_FLG = 'N' " +
        		" AND (PRINT_FLG IS NULL OR PRINT_FLG ='N' )";
//        System.out.println("历史表sql----"+sql);
        TParm opdUParm = new TParm(TJDODBTool.getInstance().select(sql));
//        System.out.println("opdUParm==="+opdUParm);
        TParm parmAll = new TParm();
        for (int i = 0; i < opdUParm.getCount(); i++) {
        	opdUParm.setData("PRINT_FLG", i, "Y");
			opdUParm.setData("RECEIPT_NO", i, recpNo);
			parmAll.addRowData(opdUParm, i);
			
		}
        if(parmAll.getCount("ORDER_CODE") > 0){
        	parmAll.setCount(parmAll.getCount("ORDER_CODE"));
        	
        	TParm inParm = new TParm();
			inParm.setData("orderParm", parmAll.getData());
 			inParm.setData("EKT_HISTORY_NO", "");
 			inParm.setData("OPT_TYPE", "UPDATE");
 			inParm.setData("OPT_USER", parm.getValue("OPT_USER"));
 			inParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
 			TParm historyParm = AssembleTool.getInstance().parmToSql(inParm);
			TParm sqlParm = historyParm.getParm("sqlParm");
			String ids = historyParm.getValue("LastHistoryIds");

// 			System.out.println(sqlTemp.length);
 			for (int i = 0; i < sqlParm.getCount("SQL"); i++) {
// 				System.out.println("history---print--"+sqlTemp[i]);
 				result=new TParm(TJDODBTool.getInstance().update(sqlParm.getValue("SQL",i), connection));
 				if(result.getErrCode()!=0){
 					System.out.println("ODOSaveTool wrong sql:"+sqlParm.getValue("SQL",i));
 					return result;
 				}
 			}
 			
 			if(ids.length() > 0){
 				ids = ids.substring(0, ids.length()-1);
 				String sqlU="UPDATE OPD_ORDER_HISTORY_NEW SET ACTIVE_FLG='N' WHERE HISTORY_ID IN ("+ids+")";
// 				System.out.println("history---print--"+sqlU);
 				result=new TParm(TJDODBTool.getInstance().update(sqlU, connection));
 				if(result.getErrCode()!=0){
 					System.out.println("ODOSaveTool wrong sql:"+sqlU);
 					return result;
 				}
 			}
        	
        	
        }
        
        //插入历史表数据  add by huangtt 20160919 end  
        
        result = new TParm();
        
      //===================================医疗卡日志数据add by huangjw 20150710 start
//		//double initAmt=0.00;//期初金额
//    	//double returnAmt=opbreceipt.getDouble("PAY_MEDICAL_CARD");//收费金额
//		double lastAmt=0.00;//期末金额
//		TParm lastParm=new TParm();
//		lastParm.setData("MR_NO",parm.getData("MR_NO"));
//		lastParm.setData("WRITE_FLG","Y");
//		lastAmt=EKTTool.getInstance().queryCurrentBalance(lastParm).getDouble("CURRENT_BALANCE",0);
//		
//		TParm inParm=new TParm();
//		String date=TJDODBTool.getInstance().getDBTime().toString().substring(0,10).replaceAll("-", "/");
//		inParm.setData("MR_NO",parm.getValue("MR_NO"));
//		inParm.setData("EKT_DATE",date);
//		TParm queryParm=EKTTool.getInstance().queryEktBilLog(inParm);
//		
//		TParm logParm=new TParm();
//		
//		if(queryParm.getCount()>0){//更新数据
//			logParm.setData("MR_NO",parm.getData("MR_NO"));
//			logParm.setData("EKT_DATE",date);
//			//logParm.setData("INIT_AMT",parm.getDouble("CURRENT_BALANCE"));//期初金额
//			logParm.setData("LAST_AMT",lastAmt);//期末金额
//			logParm.setData("OPT_USER",parm.getData("OPT_USER"));
//			logParm.setData("OPT_TERM",parm.getData("OPT_TERM"));
//			result=EKTTool.getInstance().updateEktBilLog(logParm, connection);
//		}else{
//			logParm.setData("MR_NO",parm.getValue("MR_NO"));
//			logParm.setData("EKT_DATE",date);
//			logParm.setData("INIT_AMT",lastAmt+medicalCardAmt);//期初金额
//			logParm.setData("LAST_AMT",lastAmt);//期末金额
//			logParm.setData("OPT_USER",parm.getValue("OPT_USER"));
//			logParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
//			result=EKTTool.getInstance().insertEktBilLog(logParm, connection);
//		}
//		if (result.getErrCode() != 0) {
//			err("ERR:EKTAction.TXEKTonFee " + result.getErrCode()
//					+ result.getErrText());
//			//connection.close();
//			return result;
//		}
		//============================================医疗卡日志数据add by huangjw 20150710 end
        
        result.setData("PRINT_NO", parm.getData("PRINT_NO"));
        result.addData("RECEIPT_NO", recpNo);
        
        
        return result;
        
    }

    /**
     * 获得医保扣款金额
     * @param parm TParm
     * @return double
     */
    private double getInsAmt(TParm parm) {
        double insAmt = 0.00;
        String insFlg = parm.getValue("INS_FLG");
        TParm insParm = parm.getParm("INS_RESULT");
        if (null != insFlg && insFlg.equals("Y")) {
            insAmt = insParm.getDouble("ACCOUNT_AMT"); // 医保扣款金额
        }
        return insAmt;
    }

    /**
     * 门诊收费现金打票
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm onOPBCashprint(TParm parm, TConnection connection) {
        TParm result = new TParm();
        TParm selOpdParm = new TParm();
        double insAmt = getInsAmt(parm); // 获得医保金额
        selOpdParm.setData("CASE_NO", parm.getData("CASE_NO"));

        TParm opdParm = OrderTool.getInstance().selDataForOPBCash(selOpdParm);
        if (opdParm.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }

        double[] chargeDouble = new double[30]; // 金额分类
        double[] sumAmt = chargeDouble(opdParm, chargeDouble, 2,parm.getValue("CASE_NO"));
        double allArAmt = sumAmt[0]; //总金额
        TParm opbreceipt = new TParm();
        opbreceipt.setData("CASE_NO", parm.getData("CASE_NO"));
        opbreceipt.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));
        opbreceipt.setData("REGION_CODE", parm.getData("REGION_CODE"));
        opbreceipt.setData("MR_NO", parm.getData("MR_NO"));
        opbreceipt.setData("RESET_RECEIPT_NO", new TNull(String.class));
        opbreceipt.setData("PRINT_NO", parm.getData("INV_NO"));
        opbreceipt.setData("BILL_DATE", parm.getData("OPT_DATE"));
        opbreceipt.setData("CHARGE_DATE", parm.getData("OPT_DATE"));
        opbreceipt.setData("PRINT_DATE", parm.getData("OPT_DATE"));
        int index = 1;
        for (int i = 0; i < chargeDouble.length; i++) {
            String chargeTemp = "CHARGE";
            if (i < 9) {
                opbreceipt.setData(chargeTemp + "0" + index, chargeDouble[i]);
            } else {
                opbreceipt.setData(chargeTemp + index, chargeDouble[i]);
            }
            index++;
        }
        opbreceipt.setData("TOT_AMT", allArAmt);
        opbreceipt.setData("REDUCE_REASON", new TNull(String.class));
        opbreceipt.setData("REDUCE_AMT", 0.00);
        opbreceipt.setData("REDUCE_DATE", new TNull(Timestamp.class));
        opbreceipt.setData("REDUCE_DEPT_CODE", new TNull(String.class));
        opbreceipt.setData("REDUCE_RESPOND", new TNull(String.class));
        opbreceipt.setData("AR_AMT", allArAmt);
        opbreceipt.setData("PAY_CASH", allArAmt - insAmt); // 扣除医保金额
        opbreceipt.setData("PAY_MEDICAL_CARD", 0.00);
        opbreceipt.setData("PAY_BANK_CARD", 0.00);
        opbreceipt.setData("PAY_INS_CARD", insAmt);
        opbreceipt.setData("PAY_CHECK", 0.00);
        opbreceipt.setData("PAY_DEBIT", 0.00);
        opbreceipt.setData("PAY_BILPAY", 0.00);
        opbreceipt.setData("PAY_INS", 0.00);
        opbreceipt.setData("PAY_OTHER1", 0.00);
        opbreceipt.setData("PAY_OTHER2", 0.00);
        opbreceipt.setData("ALIPAY", 0.00);
        opbreceipt.setData("QE_PAY_TYPE", new TNull(String.class)); 
        opbreceipt.setData("PAY_REMARK", new TNull(String.class));
        opbreceipt.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        opbreceipt.setData("OPT_USER", parm.getData("OPT_USER"));
        opbreceipt.setData("OPT_DATE", parm.getData("OPT_DATE"));
        opbreceipt.setData("OPT_TERM", parm.getData("OPT_TERM"));
        opbreceipt.setData("PRINT_USER", parm.getData("OPT_USER"));

        TParm bilInvricpt = new TParm();
        bilInvricpt.setData("RECP_TYPE", "OPB");
        bilInvricpt.setData("CANCEL_USER", new TNull(String.class));
        bilInvricpt.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        bilInvricpt.setData("OPT_USER", parm.getData("OPT_USER"));
        bilInvricpt.setData("INV_NO", parm.getData("INV_NO"));
        bilInvricpt.setData("OPT_TERM", parm.getData("OPT_TERM"));
        bilInvricpt.setData("CANCEL_DATE", new TNull(Timestamp.class));
        bilInvricpt.setData("TOT_AMT", allArAmt);
        bilInvricpt.setData("AR_AMT", allArAmt);

        TParm bilInvoice = new TParm();
        bilInvoice.setData("RECP_TYPE", "OPB");
        bilInvoice.setData("STATUS", "0");
        bilInvoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        bilInvoice.setData("START_INVNO", parm.getData("START_INVNO"));
        bilInvoice.setData("UPDATE_NO", StringTool.addString(parm.getData(
                "INV_NO").toString()));
        TParm actionParm = new TParm();
        actionParm.setData("BILINVRICPT", bilInvricpt.getData());
        actionParm.setData("BILINVOICE", bilInvoice.getData());
        actionParm.setData("OPBRECEIPT", opbreceipt.getData());
        actionParm.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));
        actionParm.setData("billFlg", parm.getValue("billFlg")); // ==========pangben
        // modify
        // 20110818 记账操作
        // 不记账:Y 记账:N
        actionParm.setData("CONTRACT_CODE", parm.getValue("CONTRACT_CODE")); // ==========pangben
        // modify
        // 20110818
        // 记账单位
        result = BILPrintTool.getInstance().saveOpb(actionParm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // =========此就诊病患，执行记账 OPD_ORDER 表 PRINT_FLG 列更改为N 不执行打票操作，PRINT_NO=""
        TParm upOpdParm = new TParm();
        if ("N".equals(parm.getValue("billFlg"))) {
            upOpdParm.setData("PRINT_FLG", "N");
        } else
            upOpdParm.setData("PRINT_FLG", "Y");
        String recpNo = result.getValue("RECEIPT_NO", 0);
        upOpdParm.setData("RECEIPT_NO", recpNo);
        upOpdParm.setData("CASE_NO", parm.getData("CASE_NO"));

        TParm upOpd = OrderTool.getInstance().updateForOPBCash(upOpdParm,
                connection);
        if (upOpd.getErrCode() < 0) {
            err("ERR:" + upOpd.getErrCode() + upOpd.getErrText()
                + upOpd.getErrName());
            return upOpd;
        }
        // System.out.println("OPBTOOL门诊收费返回值"+result);
        return result;
    }


    /**
     * 获得金额分类数据
     *
     * @param opdParm
     *            TParm
     * @param chargeDouble
     *            double[]
     * @param  type int 1 .医疗卡操作  2.现金操作
     * @return double
     */
    private double[] chargeDouble(TParm opdParm, double[] chargeDouble,
                                  int type,String caseNo) {
        int opdCount = opdParm.getCount("ORDER_CODE");
        String rexpCode = "";
        double arAmt = 0.00;
        double[] sumAmt = new double[3]; //获得总金额  医疗卡金额  绿色通道金额
        double allArAmt = 0.00;
        // ========20120220 zhangp modify start
        String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_CHARGE' ORDER BY SEQ";
        TParm sysChargeParm = new TParm(TJDODBTool.getInstance().select(sql));
        sql = "SELECT CHARGE01, CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07,"
                + " CHARGE08, CHARGE09, CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14,CHARGE15, "
                + " CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24, "
                + " CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29,CHARGE30 "
                + " FROM BIL_RECPPARM WHERE ADM_TYPE ='O'";
        TParm bilRecpParm = new TParm(TJDODBTool.getInstance().select(sql));
        // 获得历史记录查询此病患所有未打票的数据总金额
        TParm parm=new TParm();
        parm.setData("CASE_NO",caseNo);
		//TParm opbrecParm=OPBReceiptTool.getInstance().getReceipt(caseNo);
        int chargeCount = sysChargeParm.getCount("ID");
        String[] chargeName = new String[30];
        int index = 1;
        for (int i = 0; i < 30; i++) {
            String chargeTemp = "CHARGE";
            if (i < 9) {
                chargeName[i] = bilRecpParm.getData(chargeTemp + "0" + index, 0).
                                toString();
            } else {
                chargeName[i] = bilRecpParm.getData(chargeTemp + index, 0).
                                toString();
            }
            index++;
        }
        TParm p = new TParm();
        for (int i = 0; i < chargeCount; i++) {
            String sysChargeId = sysChargeParm.getData("ID", i).toString();
            for (int j = 0; j < chargeName.length; j++) {
                if (sysChargeId.equals(chargeName[j])) {
                    p.setData("CHARGE", i, j);
                    p.setData("ID", i, sysChargeParm.getData("ID", i));
                    p.setData("CHN_DESC", i,
                              sysChargeParm.getData("CHN_DESC", i));
                    break;
                }
            }
        }
        String idCharge = "";
        int charge = 0;
        StringBuffer tradeNo=new StringBuffer();//汇总此次操作的TRADE_NO
        for (int i = 0; i < opdCount; i++) {
            rexpCode = opdParm.getValue("REXP_CODE", i);
            arAmt = opdParm.getDouble("AR_AMT", i);
            allArAmt = allArAmt + arAmt;
			if (!tradeNo.toString()
					.contains(opdParm.getValue("BUSINESS_NO", i))) {
				tradeNo.append("'").append(opdParm.getValue("BUSINESS_NO", i))
						.append("',");// UPDATE EKT_TRADE 表使用 修改已经扣款的数据 冲负使用
			}
            for (int j = 0; j < p.getCount("ID"); j++) {
                idCharge = p.getData("ID", j).toString();
                charge = p.getInt("CHARGE", j);
                if (rexpCode.equals(idCharge))
                    chargeDouble[charge] = chargeDouble[charge] + arAmt;
                // ========20120220 zhangp modify end
            }
        }
        parm.setData("TRADE_SUM_NO",null==tradeNo||tradeNo.length()<=0?"''":tradeNo.toString().substring(0,tradeNo.toString().lastIndexOf(",")));
		TParm ektSumTradeParm = EKTNewTool.getInstance().selectEktSumTrade(parm);
        sumAmt[0] = allArAmt;
        if(null!=opdParm.getValue("INS_FLG") && opdParm.getValue("INS_FLG").equals("Y")){
        	sumAmt[1] = allArAmt; //医疗卡金额
        }else{
        	sumAmt[1] = ektSumTradeParm.getDouble("AMT",0);//-opbrecParm.getDouble("PAY_MEDICAL_CARD",0); //医疗卡金额
        }
        sumAmt[2] = ektSumTradeParm.getDouble("GREEN_BUSINESS_AMT",0);//-opbrecParm.getDouble("PAY_OTHER1",0); //绿色通道金额
        //===zhangp 20120719 start
        sumAmt = reGreenAmt(sumAmt, opdParm,caseNo);
        return sumAmt;
    }
    /**
     * 绿色通道退票以后再充钱打票
     * =======zhangp 20120719
     * @param sumAmt
     * @param opbParm
     * @return
     */
	private double[] reGreenAmt(double[] sumAmt, TParm opbParm,String caseNo) {
		//String mr_no = opbParm.getData("MR_NO", 0).toString();
		String case_no = caseNo;
		double allArAmt = sumAmt[0];
		double ektAmt = sumAmt[1]; // 医疗卡金额
		double greenAmt = sumAmt[2]; // 绿色通道金额
		if (greenAmt > 0) {
			String sql = " SELECT GREEN_BALANCE, GREEN_PATH_TOTAL,MR_NO"
					+ " FROM REG_PATADM" + " WHERE CASE_NO = '" + case_no + "'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			double green_balance = result.getDouble("GREEN_BALANCE", 0);
			double green_path_total = result.getDouble("GREEN_PATH_TOTAL", 0);
		    sql = " SELECT A.CARD_NO,B.CURRENT_BALANCE "
				+ " FROM EKT_ISSUELOG A, EKT_MASTER B "
				+ " WHERE A.MR_NO = '" + result.getValue("MR_NO",0)
				+ "' AND A.WRITE_FLG = 'Y' AND A.CARD_NO = B.CARD_NO ";
		    result = new TParm(TJDODBTool.getInstance().select(sql));
		    String card_no = result.getValue("CARD_NO", 0);
			double current_balance = result.getDouble("CURRENT_BALANCE", 0);
			if (current_balance > 0) {
				if (greenAmt <= current_balance) {
					green_balance = green_balance + allArAmt;// update reg_patadm.green_balance
					current_balance = current_balance - greenAmt;// update ekt_master.current_balance
					greenAmt = 0;
					ektAmt = allArAmt;
				} else if (greenAmt > current_balance) {
					green_balance = green_balance + current_balance;// update reg_patadm.green_balance
					greenAmt = greenAmt - current_balance;
					ektAmt = ektAmt + current_balance;
					current_balance = 0;// update ekt_master.current_balance
				}
				updateGreenAmt(green_balance, current_balance, case_no, card_no);
			}
			sumAmt[0] = allArAmt;
	        sumAmt[1] = ektAmt; //医疗卡金额
	        sumAmt[2] = greenAmt; //绿色通道金额
		}
		return sumAmt;
	}
	/**
	 * 走绿色通道后，补缴现金，更新钱
	 * ======zhangp 20120719
	 * @param green_balance
	 * @param current_balance
	 * @param case_no
	 * @param card_no
	 * @return
	 */
	private TParm updateGreenAmt(double green_balance, double current_balance,
			String case_no, String card_no) {
		String sql = " UPDATE REG_PATADM" + " SET GREEN_BALANCE = "
				+ green_balance + " WHERE CASE_NO = '" + case_no + "'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		sql = " UPDATE EKT_MASTER" + " SET CURRENT_BALANCE = "
				+ current_balance + " WHERE CARD_NO = '" + card_no + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql));
		return result;
	}

    /**
     * 修改记账表的数据
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm ==================pangben modify 20110823
     */
    private TParm updateRecode(TParm parm, TConnection connection) {
        // 修改完成状态
        TParm result = new TParm();
        TParm parmOne = new TParm();
        parmOne.setData("BIL_STATUS", "3"); // 记账退费
        parmOne.setData("" +
        		"RECEIPT_FLG", "2"); // 退费
        parmOne.setData("OPT_USER", parm.getValue("OPT_USER"));
        parmOne.setData("OPT_TERM", parm.getValue("OPT_TERM"));
        parmOne.setData("RECEIPT_NO", parm.getData("RECEIPT_NO"));
        parmOne.setData("RECEIPT_TYPE", "OPB");
        result = BILContractRecordTool.getInstance().updateRecode(parmOne,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            connection.close();
            return result;
        }
        return result;
    }

    /**
     * 现金退费作废票据:记账操作，没有执行结算的票据，不执行修改BIL_INVRCP表
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm ==================pangben modify 20110822
     */
    public TParm backOPBRecpStatus(TParm parm, TConnection connection) {
        TParm result = new TParm();
        // 修改记账表的数据
        result = updateRecode(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            connection.close();
            return result;
        }
        // 退费作废票据:记账操作和不记账操作公用的部分
        result = backOPBRecpTemp(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            connection.close();
            return result;
        }
        return result;
    }

    /**
     * 现金退费作废票据:记账操作和不记账操作公用的部分
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    private TParm backOPBRecpTemp(TParm parm, TConnection connection) {
        TParm result = new TParm();
        TParm opbreceipt = getOpbreceiptTemp(parm, false);
        // 插入票据档
        result = OPBReceiptTool.getInstance().insertBackReceipt(opbreceipt,
                false, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        // 后台取号原则取出的此票号
        String receiptNo = result.getData("RECEIPT_NO").toString();
        
        //更新医保表ins_opd中的receipt_no
        if(parm.getBoolean("INS_FLG")){
        	 String caseNo = parm.getValue("CASE_NO");
        	 String confirmNo = "*"+parm.getValue("CONFIRM_NO");
    	     String sql = " UPDATE INS_OPD SET RECEIPT_NO ='"+ receiptNo+ "'" +
    					   " WHERE CASE_NO ='" + caseNo + "'" +
    					   " AND CONFIRM_NO = '" + confirmNo + "'";
    		TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql));
    		if (updateParm.getErrCode() < 0) {
    			err(updateParm.getErrCode() + " " + updateParm.getErrText());
    			updateParm.setErr(-1, "更新医保表失败");
    			return updateParm;
    		}	
        }   
        // 更新的退费票据
        TParm upReceiptParm = updateOpbRept(parm, receiptNo);
        if (parm.getValue("RECP_TYPE").equals("EKT")) {
            result = OPBReceiptTool.getInstance().updateBackReceiptOne(
                    upReceiptParm, connection);
        } else {
            result = OPBReceiptTool.getInstance().updateBackReceipt(
                    upReceiptParm, connection);
        }
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        // 更新医嘱
        TParm orderListParm = new TParm();
        orderListParm.setData("CASE_NO", parm.getData("CASE_NO"));
        orderListParm.setData("RECEIPT_NO", parm.getData("RECEIPT_NO"));
        //===================pangben 2012-3-28 医保全部报销操作时,执行现金退费操作修改医嘱状态
        if (null!=parm.getValue("INS_UN_FLG") && parm.getValue("INS_UN_FLG").equals("Y")) {
        	OrderTool.getInstance().upForOPBEKTReturn(orderListParm, connection);
		}else{
        result = OrderTool.getInstance().upForOPBReturn(orderListParm,
                connection);
		}
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 退费
     * @param parm TParm
     * @param flg boolean
     * @return TParm
     */
    private TParm getOpbreceiptTemp(TParm parm, boolean flg) {

        // 得到退费插入负数的票据
        TParm opbreceipt = new TParm();
        opbreceipt.setData("CASE_NO", parm.getData("CASE_NO"));
        opbreceipt.setData("ADM_TYPE", parm.getData("ADM_TYPE"));
        opbreceipt.setData("REGION_CODE", parm.getData("REGION_CODE"));
        opbreceipt.setData("MR_NO", parm.getData("MR_NO"));
        opbreceipt.setData("RESET_RECEIPT_NO", new TNull(String.class));
        if (flg) {
            opbreceipt.setData("PRINT_NO", "");
            opbreceipt.setData("PRINT_DATE", "");
        } else {
            opbreceipt.setData("PRINT_NO",
                               null == parm.getData("PRINT_NO") ?
                               new TNull(String.class)
                               : parm.getData("PRINT_NO"));
            opbreceipt
                    .setData("PRINT_DATE",
                             null == parm.getData("PRINT_DATE") ? new TNull(
                                     String.class) : parm.getData("PRINT_DATE"));
        }

        opbreceipt.setData("BILL_DATE", SystemTool.getInstance().getDate());
        opbreceipt.setData("CHARGE_DATE", SystemTool.getInstance().getDate());
        for (int i = 0; i < chargName.length; i++) {
            opbreceipt.setData(chargName[i], getResetAmt(parm
                    .getDouble(chargName[i]), flg));

        }
        opbreceipt.setData("TOT_AMT", getResetAmt(parm.getDouble("TOT_AMT"),
                                                  flg));
        opbreceipt.setData("REDUCE_REASON", new TNull(String.class));
        opbreceipt.setData("REDUCE_AMT", 0.00);
        opbreceipt.setData("REDUCE_DATE", new TNull(Timestamp.class));
        opbreceipt.setData("REDUCE_DEPT_CODE", new TNull(String.class));
        opbreceipt.setData("REDUCE_RESPOND", new TNull(String.class));
        opbreceipt
                .setData("AR_AMT", getResetAmt(parm.getDouble("AR_AMT"), flg));

        opbreceipt.setData("PAY_CASH", getResetAmt(parm.getDouble("PAY_CASH"),
                flg));
        opbreceipt.setData("PAY_MEDICAL_CARD", getResetAmt(parm
                .getDouble("PAY_MEDICAL_CARD"), flg));
        opbreceipt.setData("PAY_BANK_CARD", getResetAmt(parm
                .getDouble("PAY_BANK_CARD"), flg));
        opbreceipt.setData("PAY_INS_CARD", getResetAmt(parm
                .getDouble("PAY_INS_CARD"), flg));
        opbreceipt.setData("PAY_CHECK", getResetAmt(
                parm.getDouble("PAY_CHECK"), flg));
        opbreceipt.setData("PAY_DEBIT", getResetAmt(
                parm.getDouble("PAY_DEBIT"), flg));
        opbreceipt.setData("PAY_BILPAY", getResetAmt(parm
                .getDouble("PAY_BILPAY"), flg));
        opbreceipt.setData("PAY_INS", getResetAmt(parm.getDouble("PAY_INS"),
                                                  flg));
        opbreceipt.setData("PAY_OTHER1", getResetAmt(parm
                .getDouble("PAY_OTHER1"), flg));
        opbreceipt.setData("PAY_OTHER2", getResetAmt(parm
                .getDouble("PAY_OTHER2"), flg));
        //add by huangtt 20160612 支付宝支付方式
        opbreceipt.setData("ALIPAY", getResetAmt(parm
                .getDouble("ALIPAY"), flg));
        opbreceipt.setData("QE_PAY_TYPE",  null == parm.getData("QE_PAY_TYPE") ?
                new TNull(String.class) : parm.getData("QE_PAY_TYPE"));
        opbreceipt.setData("PAY_REMARK", new TNull(String.class));
        opbreceipt.setData("CASHIER_CODE", parm.getData("OPT_USER_T"));
        opbreceipt.setData("OPT_USER", parm.getData("OPT_USER_T"));
        opbreceipt.setData("OPT_DATE", SystemTool.getInstance().getDate());
        opbreceipt.setData("OPT_TERM", parm.getData("OPT_TERM_T"));
        return opbreceipt;
    }

    /**
     * 获得金额
     * @param amt double
     * @param flg boolean
     * @return double
     */
    private double getResetAmt(double amt, boolean flg) {
        if (flg) {
            return amt;
        } else {
            return -amt;
        }
    }

    /**
     * 医疗卡退费修改收据表
     *
     * @param parm
     *            TParm
     * @param receiptNo
     *            String
     * @return TParm
     */
    private TParm updateOpbRept(TParm parm, String receiptNo) {
        TParm upReceiptParm = new TParm();
        upReceiptParm.setData("CASE_NO", parm.getData("CASE_NO"));
        upReceiptParm.setData("RESET_RECEIPT_NO", receiptNo);
        upReceiptParm.setData("RECEIPT_NO", parm.getData("RECEIPT_NO"));
        upReceiptParm.setData("PRINT_NO", parm.getData("PRINT_NO"));
        upReceiptParm.setData("OPT_USER", parm.getData("OPT_USER_T"));
        upReceiptParm.setData("OPT_DATE", parm.getData("OPT_DATE_T"));
        upReceiptParm.setData("OPT_TERM", parm.getData("OPT_TERM_T"));
        return upReceiptParm;
    }

    /**
     * 医疗卡退费作废票据
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */

    public TParm backEKTOPBRecp(TParm parm, TConnection connection) {
    	
        TParm result = null;
        // 作废票据
        result = updateBilInvrcpt(parm, connection, "OPB");
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }

        TParm opbParm = OPBReceiptTool.getInstance().getOneReceipt(
                parm.getValue("RECEIPT_NO")).getRow(0); // 查询所有此票据的收据
        TParm printParm = new TParm();
        String receiptNo = SystemTool.getInstance().getNo("ALL", "OPB",
                "RECEIPT_NO", "RECEIPT_NO");
        printParm = updateOpbRept(parm, receiptNo);
        // 医疗卡退费清将收据执行作废状态
        OPBReceiptTool.getInstance().updateUnPrintNo(printParm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // 添加一条退费收据信息
        opbParm.setData("CASE_NO", parm.getData("CASE_NO"));
        opbParm.setData("ADM_TYPE",  parm.getValue("ADM_TYPE"));
        opbParm.setData("REGION_CODE", parm.getData("REGION_CODE"));
        opbParm.setData("MR_NO", parm.getData("MR_NO"));
        opbParm.setData("PRINT_NO", parm.getData("PRINT_NO"));
        opbParm.setData("OPT_USER_T", parm.getValue("OPT_USER_T"));
        opbParm.setData("OPT_TERM_T", parm.getValue("OPT_TERM_T"));
        // 将所有的收据汇总添加一条收据,重新打票状态
        TParm opbreceipt=getOpbreceiptTemp(opbParm, false);
        opbreceipt.setData("RECEIPT_NO",receiptNo);
        result = OPBReceiptTool.getInstance().insertBackReceipt(
        		opbreceipt, false, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //更新医保表ins_opd中的receipt_no
        if(parm.getBoolean("INS_FLG")){
        	 String caseNo = parm.getValue("CASE_NO");
        	 String confirmNo = "*"+parm.getValue("CONFIRM_NO");
    	     String sql = " UPDATE INS_OPD SET RECEIPT_NO ='"+ receiptNo+ "'" +
    					   " WHERE CASE_NO ='" + caseNo + "'" +
    					   " AND CONFIRM_NO = '" + confirmNo + "'";
    		TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql));
    		if (updateParm.getErrCode() < 0) {
    			err(updateParm.getErrCode() + " " + updateParm.getErrText());
    			updateParm.setErr(-1, "更新医保表失败");
    			return updateParm;
    		}	
        }             
        // 更新医嘱
        TParm orderListParm = new TParm();
        orderListParm.setData("CASE_NO", parm.getData("CASE_NO"));
        orderListParm.setData("RECEIPT_NO", parm.getValue("RECEIPT_NO"));
        result = OrderTool.getInstance().upForOPBEKTReturn(orderListParm,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
      //===================================add by huangjw 20150710
		//double initAmt=0.00;//期初金额
    	//double returnAmt=opbreceipt.getDouble("PAY_MEDICAL_CARD");//收费金额
		double lastAmt=0.00;//期末金额
		TParm lastParm=new TParm();
		lastParm.setData("MR_NO",parm.getData("MR_NO"));
		lastParm.setData("WRITE_FLG","Y");
		lastAmt=EKTTool.getInstance().queryCurrentBalance(lastParm).getDouble("CURRENT_BALANCE",0);
		
		TParm inParm=new TParm();
		inParm.setData("MR_NO",parm.getValue("MR_NO"));
		inParm.setData("EKT_DATE",TJDODBTool.getInstance().getDBTime().toString().substring(0,10).replaceAll("-", "/"));
		TParm queryParm=EKTTool.getInstance().queryEktBilLog(inParm);
		
		TParm logParm=new TParm();
		String date=TJDODBTool.getInstance().getDBTime().toString().substring(0,10).replaceAll("-", "/");
		if(queryParm.getCount()>0){//更新
			logParm.setData("MR_NO",parm.getData("MR_NO"));
			logParm.setData("EKT_DATE",date);
			//logParm.setData("INIT_AMT",parm.getDouble("CURRENT_BALANCE"));//期初金额
			logParm.setData("LAST_AMT",lastAmt);//期末金额
			logParm.setData("OPT_USER",parm.getValue("OPT_USER_T"));
			logParm.setData("OPT_TERM",parm.getValue("OPT_TERM_T"));
			result=EKTTool.getInstance().updateEktBilLog(logParm, connection); 
		}else{
			logParm.setData("MR_NO",parm.getValue("MR_NO"));
			logParm.setData("EKT_DATE",date);
			logParm.setData("INIT_AMT",lastAmt);//期初金额
			logParm.setData("LAST_AMT",lastAmt);//期末金额
			logParm.setData("OPT_USER",parm.getValue("OPT_USER"));
			logParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
			result=EKTTool.getInstance().insertEktBilLog(logParm, connection);
		}
		if (result.getErrCode() != 0) {
			err("ERR:EKTAction.TXEKTonFee " + result.getErrCode()
					+ result.getErrText());
			//connection.close();
			return result;
		}
		//============================================医疗卡日志数据add by huangjw 20150710 end
        return result;
    }

    /**
     * 现金退费作废票据
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm backOPBRecp(TParm parm, TConnection connection) {
        String sql = "SELECT * FROM BIL_CONTRACT_RECODE WHERE RECEIPT_NO='"
                     + parm.getData("RECEIPT_NO") + "'";
        TParm contractParm = new TParm(TJDODBTool.getInstance().select(sql));
        TParm result = null;
        if (null != contractParm && contractParm.getCount() > 0) {
            result = updateRecode(parm, connection);
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return result;
            }

        }
        result = backOPBRecpTemp(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        result = updateBilInvrcpt(parm, connection, "OPB");
        return result;

    }

    /**
     * 作废票据数据
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @param recpType
     *            String
     * @return TParm
     */
    private TParm updateBilInvrcpt(TParm parm, TConnection connection,
                                   String recpType) {
        // 作废票据
        TParm bilInvrcptParm = new TParm();
        bilInvrcptParm.setData("CANCEL_FLG", "1");
        bilInvrcptParm.setData("CANCEL_USER", parm.getData("OPT_USER_T"));
        bilInvrcptParm.setData("OPT_USER", parm.getData("OPT_USER_T"));
        bilInvrcptParm.setData("OPT_TERM", parm.getData("OPT_TERM_T"));
        bilInvrcptParm.setData("RECP_TYPE", recpType);
        bilInvrcptParm.setData("INV_NO", parm.getData("PRINT_NO"));

        TParm result = BILInvrcptTool.getInstance().updataData(bilInvrcptParm,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 取得明细（收据用） =======zhangp 20120224
     * @param parm TParm
     * @return TParm
     */
    public TParm getReceiptDetail(TParm parm) {
        DecimalFormat df = new DecimalFormat("########0.00");
        String caseNo = parm.getValue("CASE_NO");
        String no = "";
        String sql = "";
        no = parm.getValue("NO");
        sql =
            " SELECT A.ORDER_DESC || A.SPECIFICATION AS ORDER_DESC, D.ADDPAY_RATE, A.DOSAGE_QTY, " +
            "        A.OWN_PRICE, A.AR_AMT, A.REXP_CODE,C.CHN_DESC " +
            "   FROM OPD_ORDER A,BIL_OPB_RECP B,SYS_DICTIONARY C,SYS_FEE D "+
            "  WHERE A.RECEIPT_NO = B.RECEIPT_NO "+
            "    AND A.REXP_CODE = C.ID  "+
            "    AND C.GROUP_ID = 'SYS_CHARGE' "+
            "    AND A.SETMAIN_FLG = 'N'" +
            "    AND A.ORDER_CODE = D.ORDER_CODE "+
            "    AND A.CASE_NO = '" + caseNo + "' " +
            "    AND A.CASE_NO = B.CASE_NO" +
            "    AND A.ADM_TYPE = '" + parm.getValue("ADM_TYPE") +"' "+
            "    AND A.RECEIPT_NO = '" + no + "' " +
            "  ORDER BY A.REXP_CODE ";
//        System.out.println("取得明细费用sql:" + sql);

        TParm orderParm = new TParm(TJDODBTool.getInstance().select(sql));
//        System.out.println("取得明细费用:" + orderParm);
        String rexpCode = orderParm.getValue("REXP_CODE", 0);
        TParm orderParmf = new TParm();
        double amt = 0.00;
        for (int i = 0; i < orderParm.getCount(); i++) {
            if (rexpCode.equals(orderParm.getData("REXP_CODE", i).toString())) {
                orderParmf.addData("ORDER_DESC", orderParm.getData(
                        "ORDER_DESC", i));
                orderParmf.addData("ADDPAY_RATE", orderParm.getData(
                        "ADDPAY_RATE", i));
                orderParmf
                        .addData("DOSAGE_QTY",
                                 orderParm.getData("DOSAGE_QTY", i));
                orderParmf.addData("OWN_PRICE", orderParm.getData("OWN_PRICE",
                        i));
                orderParmf.addData("AR_AMT",
                                   df.format(StringTool.round(orderParm
                        .getDouble("AR_AMT", i), 2)));
                amt = amt + orderParm.getDouble("AR_AMT", i);
            } else {
//                orderParmf.addData("ORDER_DESC", "");
//                orderParmf.addData("ADDPAY_RATE", "");
//                orderParmf.addData("DOSAGE_QTY", "");
//                orderParmf.addData("OWN_PRICE", orderParm.getData("CHN_DESC",
//                        i - 1));
//                orderParmf.addData("AR_AMT", df.format(StringTool.round(amt, 2)));

                orderParmf.addData("ORDER_DESC", orderParm.getData(
                        "ORDER_DESC", i));
                orderParmf.addData("ADDPAY_RATE", orderParm.getData(
                        "ADDPAY_RATE", i));
                orderParmf
                        .addData("DOSAGE_QTY",
                                 orderParm.getData("DOSAGE_QTY", i));
                // =====zhangp 20120301 modify start
                orderParmf.addData("OWN_PRICE", StringTool.round(orderParm
                        .getDouble("OWN_PRICE", i), 2));
                // =====zhangp 20120301 modify end
                orderParmf.addData("AR_AMT",
                                   df.format(StringTool.round(orderParm
                        .getDouble("AR_AMT", i), 2)));
                amt = orderParm.getDouble("AR_AMT", i);
            }
            rexpCode = orderParm.getData("REXP_CODE", i).toString();
        }
//        orderParmf.addData("ORDER_DESC", "");
//        orderParmf.addData("ADDPAY_RATE", "");
//        orderParmf.addData("DOSAGE_QTY", "");
////        orderParmf.addData("OWN_PRICE", orderParm.getData("CHN_DESC", orderParm
////                .getCount() - 1));
//        orderParmf.addData("OWN_PRICE", "");//modify by lich 20141205
////        orderParmf.addData("AR_AMT", df.format(StringTool.round(amt, 2)));
//        orderParmf.addData("AR_AMT", "");//modify by lich 20141205
        TParm tableresultparm = new TParm();
        for (int i = 0; i < 6; i++) {
            tableresultparm.addData("ORDER_DESC", orderParmf.getData(
                    "ORDER_DESC", i));
            tableresultparm.addData("ADDPAY_RATE", orderParmf.getData(
                    "ADDPAY_RATE", i));
            tableresultparm.addData("DOSAGE_QTY",
                                    orderParmf.getData("DOSAGE_QTY",
                    i));
            tableresultparm.addData("OWN_PRICE", orderParmf.getData(
                    "OWN_PRICE", i));
            // =====zhangp 20120227 modify start
            tableresultparm.addData("AR_AMT", orderParmf.getData("AR_AMT", i));
            // =====zhangp 20120227 modify end
        }
        tableresultparm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        tableresultparm.addData("SYSTEM", "COLUMNS", "ADDPAY_RATE");
        tableresultparm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
        tableresultparm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        tableresultparm.addData("SYSTEM", "COLUMNS", "AR_AMT");
        tableresultparm.setCount(tableresultparm.getCount("ORDER_DESC"));
        
//        System.out.println("tableresultparm++++++_____"+tableresultparm);
        int y = 2;
        int tableresultparmCount = tableresultparm.getCount("ORDER_DESC");
        int tableParmCount =  (int) Math.ceil((double)tableresultparmCount/y);
        TParm tableParm = new TParm();
        
        for(int i = 0;i < 2;i++){
        	
        	tableParm.addData("ORDER_DESC_L", null);
         	tableParm.addData("ADDPAY_RATE_L", null);
         	tableParm.addData("DOSAGE_QTY_L", null);
         	tableParm.addData("OWN_PRICE_L", null);
         	tableParm.addData("AR_AMT_L", null);
         	
         	tableParm.addData("ORDER_DESC_R", null);
         	tableParm.addData("ADDPAY_RATE_R", null);
         	tableParm.addData("DOSAGE_QTY_R", null);
         	tableParm.addData("OWN_PRICE_R", null);
         	tableParm.addData("AR_AMT_R", null);
         	
        	for(int j = 0; j < tableresultparmCount;j++){
        		if(j == i * y){
        			tableParm.setData("ORDER_DESC_L", i, tableresultparm.getData("ORDER_DESC", j));
        			tableParm.setData("ADDPAY_RATE_L", i, tableresultparm.getData("ADDPAY_RATE", j));
        			tableParm.setData("DOSAGE_QTY_L", i, tableresultparm.getData("DOSAGE_QTY", j));
        			tableParm.setData("OWN_PRICE_L", i, tableresultparm.getData("OWN_PRICE", j));
//        			System.out.println("j="+j+"		OWN_PRICE_L"+tableParm.getData("OWN_PRICE_L"));
        			tableParm.setData("AR_AMT_L", i, tableresultparm.getData("AR_AMT", j));
        		}
        		if(j == i * y + 1){
        			tableParm.setData("ORDER_DESC_R", i, tableresultparm.getData("ORDER_DESC", j));
        			tableParm.setData("ADDPAY_RATE_R", i, tableresultparm.getData("ADDPAY_RATE", j));
        			tableParm.setData("DOSAGE_QTY_R", i, tableresultparm.getData("DOSAGE_QTY", j));
        			tableParm.setData("OWN_PRICE_R", i, tableresultparm.getData("OWN_PRICE", j));
//        			System.out.println("j="+j+"		OWN_PRICE_R"+tableParm.getData("OWN_PRICE_R"));
        			tableParm.setData("AR_AMT_R", i, tableresultparm.getData("AR_AMT", j));
        		}
        		
        	}
        	
        }
        tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC_L");
    	tableParm.addData("SYSTEM", "COLUMNS", "ADDPAY_RATE_L");
    	tableParm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY_L");
    	tableParm.addData("SYSTEM", "COLUMNS", "OWN_PRICE_L");
    	tableParm.addData("SYSTEM", "COLUMNS", "AR_AMT_L");
    	
    	tableParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC_R");
    	tableParm.addData("SYSTEM", "COLUMNS", "ADDPAY_RATE_R");
    	tableParm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY_R");
    	tableParm.addData("SYSTEM", "COLUMNS", "OWN_PRICE_R");
    	tableParm.addData("SYSTEM", "COLUMNS", "AR_AMT_R");
    	tableParm.setCount(tableParmCount); 
//      return tableresultparm;
//        System.out.println("tableParm----------"+tableParm);
        return tableParm;
    }


    /**
     * 得到医疗保险门诊专用收据报表信息 ===xueyf 20120225
     * @param printParm TParm
     * @return TParm
     */
    public TParm getReceiptTparm(TParm printParm) {
        String receiptNo = (String) printParm.getValue("RECEIPT_NO");
        if (receiptNo != null) {
            // TParm parm=getChargeData(receiptNo);
            // printParm.setData(parm.getData());
        }
        Timestamp printDate = (SystemTool.getInstance().getDate());
        TParm selAdmInp = new TParm();
        selAdmInp.setData("CASE_NO", printParm.getValue("CASE_NO"));
        TParm admInpParm = ADMInpTool.getInstance().selectall(selAdmInp);
        Timestamp inDataOut = admInpParm.getTimestamp("IN_DATE", 0);
        Timestamp outDataOut;
        if (admInpParm.getData("DS_DATE", 0) != null)
            outDataOut = admInpParm.getTimestamp("DS_DATE", 0);
        else
            outDataOut = printDate;
        String deptCode = admInpParm.getValue("DEPT_CODE", 0);
        String inDate = StringTool.getString(inDataOut, "yyyyMMdd");
        String outDate = StringTool.getString(outDataOut, "yyyyMMdd");
        String pDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                            "yyyyMMdd");
        String sYear = inDate.substring(0, 4);
        String sMonth = inDate.substring(4, 6);
        String sDate = inDate.substring(6, 8);
        String eYear = outDate.substring(0, 4);
        String eMonth = outDate.substring(4, 6);
        String eDate = outDate.substring(6, 8);
        String pYear = pDate.substring(0, 4);
        String pMonth = pDate.substring(4, 6);
        String pDay = pDate.substring(6, 8);
        // int rollDate = StringTool.getDateDiffer(outDataOut, inDataOut) ==
        // 0 ? 1 : StringTool.getDateDiffer(outDataOut, inDataOut);
        int rollDate = StringTool.getDateDiffer(outDataOut, inDataOut) + 1;
        printParm.setData("COPY", "TEXT", "COPY");
        printParm.setData("sYear", "TEXT", sYear);
        printParm.setData("sMonth", "TEXT", sMonth);
        printParm.setData("sDate", "TEXT", sDate);
        printParm.setData("eYear", "TEXT", eYear);
        printParm.setData("eMonth", "TEXT", eMonth);
        printParm.setData("eDate", "TEXT", eDate);
        printParm.setData("pYear", "TEXT", pYear);
        printParm.setData("pMonth", "TEXT", pMonth);
        printParm.setData("pDay", "TEXT", pDay);
        printParm.setData("rollDate", "TEXT", rollDate);
        printParm.setData("CASHIER_CODE", "TEXT", Operator.getName());
        // printParm.setData("MR_NO","TEXT", this.getValueString("MR_NO"));
        printParm.setData("BILL_DATE", "TEXT", outDataOut);
        printParm.setData("CHARGE_DATE", "TEXT", outDataOut);
        // printParm.setData("DEPT_CODE","TEXT", getDept(deptCode));
        printParm.setData("OPT_USER", "TEXT", Operator.getName());
        printParm.setData("OPT_DATE", "TEXT", printDate);
        String printDateC = StringTool.getString(printDate, "yyyy年MM月dd日");
        printParm.setData("DATE", "TEXT", printDateC);
        printParm.setData("NO1", "TEXT", receiptNo);
        // printParm.setData("NO2", "TEXT", printNo);
        printParm.setData("HOSP", "TEXT", Operator.getHospitalCHNFullName());
        return printParm;
    }
    /**
	 * 获得HL7数据集合
	 * 门诊医生站  门诊收费使用
	 * @param hl7ParmEnd
	 * @param checkParm
	 * @param i
	 */
	public void setHl7TParm(TParm hl7ParmEnd, TParm checkParm, int i,String billFlg) {
		// HL7数据集合 获得新增 的集合医嘱主项 发送接口使用
		if ((checkParm.getValue("CAT1_TYPE", i).equals("RIS") || checkParm
				.getValue("CAT1_TYPE", i).equals("LIS"))
				&& checkParm.getBoolean("SETMAIN_FLG", i)
				&& checkParm.getValue("ORDERSET_CODE", i).equals(
						checkParm.getValue("ORDER_CODE", i))) {
			hl7ParmEnd.addData("ORDER_CAT1_CODE", checkParm.getData(
					"ORDER_CAT1_CODE", i));
			hl7ParmEnd.addData("TEMPORARY_FLG", checkParm.getData(
					"TEMPORARY_FLG", i));
			hl7ParmEnd.addData("ADM_TYPE", checkParm.getData("ADM_TYPE", i));
			hl7ParmEnd.addData("RX_NO", checkParm.getData("RX_NO", i));
			hl7ParmEnd.addData("SEQ_NO", checkParm.getData("SEQ_NO", i));
			hl7ParmEnd.addData("MED_APPLY_NO", checkParm.getData(
					"MED_APPLY_NO", i));
			hl7ParmEnd.addData("CAT1_TYPE", checkParm.getData("CAT1_TYPE", i));
			hl7ParmEnd.addData("BILL_FLG", billFlg);
		}
	}
	/**
	 * 此次交易需要操作的医嘱集合赋值
	 */
	public void setNewParm(TParm newParm,TParm parm ,int i,String billlFlg,String billType){
		newParm.addData("RX_NO", parm.getValue("RX_NO", i));
		newParm.addData("ORDER_CODE", parm.getValue(
				"ORDER_CODE", i));
		newParm.addData("SEQ_NO", parm.getValue("SEQ_NO", i));
		newParm.addData("AMT", parm.getDouble("AR_AMT", i));
		newParm.addData("BUSINESS_NO", parm.getValue("BUSINESS_NO", i));
		newParm.addData("BILL_FLG", billlFlg);
		newParm.addData("BILL_TYPE", billType);
	}
	/**
	 * 修改医疗卡操作OPD_ORDER 医嘱的交易号码BUSINESS_NO flg true 门诊收费操作 false 门诊医生站操作
	 * 
	 * @param parm
	 *            TParm
	 * @param orderParm
	 *            TParm
	 * @param flg
	 *            boolean
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateOpdOrderEkt(TParm parm, TParm orderParm,
			TConnection connection) {
		// 收费
		String sql = "";
		TParm result = new TParm();
		//String unFlg = parm.getValue("UN_FLG");
		TParm hl7Parm=parm.getParm("hl7Parm");//HL7发送接口集合
		String tradeNo="";
		String billDate="";
		String billUser="";
		// 此次操作的医嘱
		TParm orderParmHistroy = new TParm(); //插入历史表数据 add by huangtt 20160919
//		System.out.println("updateOpdOrderEkt----1---"+SystemTool.getInstance().getDate());
		for (int i = 0; i < orderParm.getCount("RX_NO"); i++) {
			if(null!=orderParm.getValue("BILL_FLG",i) && orderParm.getValue("BILL_FLG",i).equals("N")){
				tradeNo="";
				billDate="''";
				billUser="";
			}else{
				tradeNo=parm.getValue("TRADE_NO");
				billDate="SYSDATE";
				billUser=parm.getValue("OPT_USER");
			}
			sql = "UPDATE OPD_ORDER SET BUSINESS_NO='"
					+ tradeNo + "', BILL_FLG='"+orderParm.getValue("BILL_FLG",i)+"' "
					+ " , BILL_DATE="+billDate+",BILL_USER='"
					+ billUser
					+ "',BILL_TYPE='"+orderParm.getValue("BILL_TYPE",i)+"' WHERE CASE_NO='"
					+ parm.getValue("CASE_NO") + "' " + "AND RX_NO ='"
					+ orderParm.getValue("RX_NO", i) + "' " + "AND SEQ_NO='"
					+ orderParm.getValue("SEQ_NO", i) + "' ";
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));

			if (result.getErrCode() < 0) {
				return result;
			}
			
			//add by huangtt 20160919 历史表数据 start
			String sqlH = "SELECT * FROM OPD_ORDER WHERE CASE_NO='"
					+ parm.getValue("CASE_NO") + "' " + "AND RX_NO ='"
					+ orderParm.getValue("RX_NO", i) + "' " + "AND SEQ_NO='"
					+ orderParm.getValue("SEQ_NO", i) + "' ";
			TParm parmH = new TParm(TJDODBTool.getInstance().select(sqlH));
			if(parmH.getCount() > 0){
				parmH.setData("BUSINESS_NO", 0, tradeNo);
				parmH.setData("BILL_FLG", 0, orderParm.getValue("BILL_FLG",i));
				if(billDate.length() > 2){
					 SimpleDateFormat  format=new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
					 Timestamp date = SystemTool.getInstance().getDate();
					 String time=format.format(date.getTime());
					 parmH.setData("BILL_DATE", 0, time);
				}else{
					parmH.setData("BILL_DATE", 0, "");
				}
				
				parmH.setData("BILL_USER", 0, billUser);
				parmH.setData("BILL_TYPE", 0, orderParm.getValue("BILL_TYPE",i));
				orderParmHistroy.addRowData(parmH, 0);
			}
			
			
			//add by huangtt 20160919 历史表数据 end
			
		}
//		System.out.println("updateOpdOrderEkt----2---"+SystemTool.getInstance().getDate());

		if(orderParmHistroy.getCount("ORDER_CODE") > 0){
			
			TParm inParm = new TParm();
			inParm.setData("orderParm", orderParmHistroy.getData());			
			inParm.setData("EKT_HISTORY_NO", parm.getValue("HISTORY_NO"));
			inParm.setData("OPT_TYPE", "UPDATE");
			inParm.setData("OPT_USER", parm.getValue("OPT_USER"));
			inParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
			
			
			TParm historyParm = AssembleTool.getInstance().parmToSql(inParm);
			
//			System.out.println("updateOpdOrderEkt----2.1---"+SystemTool.getInstance().getDate());
			
			TParm sqlParm = historyParm.getParm("sqlParm");
			String ids = historyParm.getValue("LastHistoryIds");
			
			for (int j = 0; j < sqlParm.getCount("SQL"); j++) {
				result=new TParm(TJDODBTool.getInstance().update(sqlParm.getValue("SQL",j), connection));
				if(result.getErrCode()!=0){
					System.out.println("ODOSaveTool wrong sql:"+sqlParm.getValue("SQL",j));
					return result;
				}
			}
//			System.out.println("updateOpdOrderEkt----2.2---"+SystemTool.getInstance().getDate());
//			 OpdOrderHistory opdOrderHistory = new  OpdOrderHistory();
//			orderParmHistroy.setCount(orderParmHistroy.getCount("ORDER_CODE"));
//			System.out.println("进来-----------");
//			opdOrderHistory.onQuery();
//			System.out.println("updateOpdOrderEkt----2.0---"+SystemTool.getInstance().getDate());
//			orderParmHistroy.setData("EKT_HISTORY_NO", parm.getValue("HISTORY_NO"));
//			orderParmHistroy.setData("OPT_TYPE", "UPDATE");
//			orderParmHistroy.setData("OPT_USER", parm.getValue("OPT_USER"));
//			orderParmHistroy.setData("OPT_TERM", parm.getValue("OPT_TERM"));
//			opdOrderHistory.insert(orderParmHistroy);
//			System.out.println("updateOpdOrderEkt----2.1---"+SystemTool.getInstance().getDate());
//			String [] sqlTemp = opdOrderHistory.getUpdateSQL();
//			for (int i = 0; i < sqlTemp.length; i++) {
//				result=new TParm(TJDODBTool.getInstance().update(sqlTemp[i], connection));
//				if(result.getErrCode()!=0){
//					System.out.println("ODOSaveTool wrong sql:"+sqlTemp[i]);
//					return result;
//				}
//			}
//			System.out.println("updateOpdOrderEkt----2.2---"+SystemTool.getInstance().getDate());

//			String ids = opdOrderHistory.getLastHistoryIds();
			
			
			if(ids.length() > 0){
				ids = ids.substring(0, ids.length()-1);
				String sqlU="UPDATE OPD_ORDER_HISTORY_NEW SET ACTIVE_FLG='N' WHERE HISTORY_ID IN ("+ids+")";
				result=new TParm(TJDODBTool.getInstance().update(sqlU, connection));
				if(result.getErrCode()!=0){
					System.out.println("ODOSaveTool wrong sql:"+sqlU);
					return result;
				}
			}
//			System.out.println("updateOpdOrderEkt----2.3---"+SystemTool.getInstance().getDate());
		}
//		System.out.println("updateOpdOrderEkt----3---"+SystemTool.getInstance().getDate());

		
		result=exeHl7ParmBillFlg(hl7Parm, parm, connection);
//		System.out.println("updateOpdOrderEkt----4---"+SystemTool.getInstance().getDate());

		return result;
	}
	public TParm onHl7ExeBillFlg(TParm parm,TConnection connection){
		TParm hl7Parm=parm.getParm("hl7Parm");//HL7发送接口集合
		String tradeNo=parm.getValue("TRADE_NO");
		TParm billParm=parm.getParm("parmBill");
		TParm result=new TParm();
		String sql="";
		TParm orderParmHistroy = new TParm();
		//如果存在未收费的医嘱需要修改状态
		for (int i = 0; i < billParm.getCount("RX_NO"); i++) {
			sql = "UPDATE OPD_ORDER SET BUSINESS_NO='" + tradeNo
					+ "', BILL_FLG='" + billParm.getValue("BILL_FLG", i) + "' "
					+ " , BILL_DATE=SYSDATE,BILL_USER='"
					+ parm.getValue("OPT_USER") + "',BILL_TYPE='"
					+ billParm.getValue("BILL_TYPE", i) + "' WHERE CASE_NO='"
					+ parm.getValue("CASE_NO") + "' " + "AND RX_NO ='"
					+ billParm.getValue("RX_NO", i) + "' " + "AND SEQ_NO='"
					+ billParm.getValue("SEQ_NO", i) + "' ";
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				return result;
			}
			
			//add by huangtt 20160919 历史表数据 start
			String sqlH = "SELECT * FROM OPD_ORDER  WHERE CASE_NO='"
					+ parm.getValue("CASE_NO") + "' " + "AND RX_NO ='"
					+ billParm.getValue("RX_NO", i) + "' " + "AND SEQ_NO='"
					+ billParm.getValue("SEQ_NO", i) + "' ";
			TParm parmH = new TParm(TJDODBTool.getInstance().select(sqlH));
			if(parmH.getCount() > 0){
				parmH.setData("BUSINESS_NO", 0, tradeNo);
				parmH.setData("BILL_FLG", 0, billParm.getValue("BILL_FLG", i));
				 SimpleDateFormat  format=new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
				 Timestamp date = SystemTool.getInstance().getDate();
				 String time=format.format(date.getTime());
				 parmH.setData("BILL_DATE", 0, time);
				
				parmH.setData("BILL_USER", 0, parm.getValue("OPT_USER"));
				parmH.setData("BILL_TYPE", 0, billParm.getValue("BILL_TYPE", i));
				orderParmHistroy.addRowData(parmH, 0);
			}
			
			
			
			
		}
		
		if(orderParmHistroy.getCount("ORDER_CODE") > 0){
			 TParm inParm = new TParm();
			 inParm.setData("orderParm", orderParmHistroy.getData());
			 inParm.setData("MZCONFIRM_NO", parm.getValue("CONFIRM_NO"));
			 inParm.setData("EKT_HISTORY_NO", parm.getValue("HISTORY_NO"));
			 inParm.setData("OPT_TYPE", "UPDATE");
			 inParm.setData("OPT_USER", parm.getValue("OPT_USER"));
			 inParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
			 TParm historyParm = AssembleTool.getInstance().parmToSql(inParm);
			 TParm sqlParm = historyParm.getParm("sqlParm");
			 String ids = historyParm.getValue("LastHistoryIds");
			
			for (int i = 0; i < sqlParm.getCount("SQL"); i++) {
				result=new TParm(TJDODBTool.getInstance().update(sqlParm.getValue("SQL",i), connection));
				if(result.getErrCode()!=0){
					System.out.println("ODOSaveTool wrong sql:"+sqlParm.getValue("SQL",i));
					return result;
				}
			}
			if(ids.length() > 0){
				ids = ids.substring(0, ids.length()-1);
				String sqlU="UPDATE OPD_ORDER_HISTORY_NEW SET ACTIVE_FLG='N' WHERE HISTORY_ID IN ("+ids+")";
				result=new TParm(TJDODBTool.getInstance().update(sqlU, connection));
				if(result.getErrCode()!=0){
					System.out.println("ODOSaveTool wrong sql:"+sqlU);
					return result;
				}
			}
		}
		//add by huangtt 20160919 历史表数据 end
		
		result=exeHl7ParmBillFlg(hl7Parm, parm, connection);
		return result;
	}
	/**
	 * 发送HL7接口执行收费状态
	 * @param hl7Parm
	 * @param parm
	 * @param connection
	 * @return
	 */
	private TParm exeHl7ParmBillFlg(TParm hl7Parm,TParm parm,TConnection connection){
		TParm result=new TParm();
		String sql="";
		if(null==hl7Parm)
			return new TParm();
		for (int i = 0; i < hl7Parm.getCount("RX_NO"); i++) {
			// 修改检验检查收费状态
			sql = "UPDATE MED_APPLY SET BILL_FLG='" + hl7Parm.getValue("BILL_FLG",i)
					+ "' WHERE APPLICATION_NO='"
					+ hl7Parm.getValue("MED_APPLY_NO", i)
					+ "' AND CASE_NO='" + parm.getValue("CASE_NO")
					+ "' AND ORDER_NO='"
					+ hl7Parm.getValue("RX_NO", i)
					+ "' AND SEQ_NO='"
					+ hl7Parm.getValue("SEQ_NO", i) + "'";
			result = new TParm(TJDODBTool.getInstance().update(sql,
					connection));
			if (result.getErrCode() < 0) {
				System.out.println("result:"+result.getErrText());
				return result;
			}
		}
		return result;
	}
	/**
	 * 调用HL7
	 */
	public TParm  sendHL7Mes(TParm hl7ParmEnd,String patName,boolean EKTmessage,String caseNo) {
		/**
		 * 发送HL7消息
		 * 
		 * @param admType
		 *            String 门急住别
		 * @param catType
		 *            医令分类
		 * @param patName
		 *            病患姓名
		 * @param caseNo
		 *            String 就诊号
		 * @param applictionNo
		 *            String 条码号
		 * @param flg
		 *            String 状态(0,发送1,取消)
		 */
//		int count = 0;
//		if (null != hl7ParmEnd && null != hl7ParmEnd.getData("ADM_TYPE"))
//			count = ((Vector) hl7ParmEnd.getData("ADM_TYPE")).size();
		if(hl7ParmEnd.getCount("ADM_TYPE")==0){
			return new TParm();
		}
		List list = new ArrayList();
		//String patName = getValue("PAT_NAME").toString();
		for (int i = 0; i < hl7ParmEnd.getCount("ADM_TYPE"); i++) {
			TParm temp = hl7ParmEnd.getRow(i);
			if (temp.getValue("TEMPORARY_FLG").length() == 0) {
				continue;
			}
			TParm parm = new TParm();
			parm.setData("PAT_NAME", patName);
			parm.setData("ADM_TYPE", temp.getValue("ADM_TYPE"));
			if (EKTmessage) {
				parm.setData("FLG", 1);// 退费
			} else {
				parm.setData("FLG", 0);
			}
			parm.setData("CASE_NO", caseNo);
			parm.setData("LAB_NO", temp.getValue("MED_APPLY_NO"));
			parm.setData("CAT1_TYPE", temp.getValue("CAT1_TYPE"));
			parm.setData("ORDER_NO", temp.getValue("RX_NO"));
			parm.setData("SEQ_NO", temp.getValue("SEQ_NO"));
			list.add(parm);
		}
		// 调用接口
		TParm resultParm = Hl7Communications.getInstance().Hl7Message(list);
		return resultParm;
		
	}
	/**
	 * 根据挂号参数中有效天数校验是否可以就诊，添加医嘱操作
	 * ============pangben 2013-4-28
	 * @return
	 */
	public boolean canEdit(Reg reg,TParm regParm) {
		Timestamp admDate = reg.getAdmDate(); // 挂号日期
		Timestamp now = SystemTool.getInstance().getDate(); // 当前时间
		// 获取挂号参数档中“有效天数”的23点59分59秒的时间
		// 例如3/5 晚上挂号，一直到 3/6 全天都视为有效
		int effDays = regParm.getInt("EFFECT_DAYS",0);
		if (effDays == 0) { // 急诊晚班
			effDays++;
		}
		Timestamp time = StringTool.getTimestamp(StringTool.getString(
				StringTool.rollDate(admDate, effDays), "yyyyMMdd")
				+ "235959", "yyyyMMddHHmmss");
		// 当前时间晚于挂号限定时间 则不可以修改
		if (now.getTime() > time.getTime()) {
			return false;
		}
		return true;
	}
	
	
	/**
     * 更新票据主档表
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    private TParm updateQeAppBilinvoice(TParm parm, TConnection connection) {
    	TParm result = new TParm();
    	String sql = "SELECT PRINT_NO FROM BIL_OPB_RECP WHERE CASE_NO='"+parm.getData("CASE_NO")+"' AND RECEIPT_NO='"+parm.getData("RECEIPT_NO")+"' ";
    	TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(selParm.getValue("PRINT_NO", 0).length() > 0){
    		result.setData("PRINT_NO", selParm.getValue("PRINT_NO", 0));
            result.setData("OLDPRINT_NO", parm.getValue("PRINT_NO"));
            result.setData("PRINT_FLG", "N");
            return result;
    	}
    	
        String caseNo = parm.getValue("CASE_NO");
        String confirmNo = parm.getValue("CONFIRM_NO");
        TParm selInvoice = new TParm();
        selInvoice.setData("STATUS", "0");
        selInvoice.setData("RECP_TYPE", "OPB");
        selInvoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        selInvoice.setData("TERM_IP", parm.getData("OPT_TERM"));
        // System.out.println("票据主档数据" + selInvoice);
        TParm invoice = BILInvoiceTool.getInstance().selectNowReceipt(
                selInvoice);
        String invNo = invoice.getValue("UPDATE_NO", 0);
        // System.out.println("票据主档数据" + invoice);
        TParm prtInvoice = new TParm();
        prtInvoice.setData("UPDATE_NO", StringTool.addString(invNo));
        prtInvoice.setData("RECP_TYPE", "OPB");
        prtInvoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        prtInvoice.setData("STATUS", "0");
        prtInvoice.setData("START_INVNO", invoice.getData("START_INVNO", 0));
        parm.setData("NEW_PRINT_NO", invNo);
        
        //医保-------start
        if(confirmNo.length() > 0){
        	result = new TParm(); 
     		 sql = " UPDATE INS_OPD SET INV_NO='" + invNo
     		+ "' WHERE CONFIRM_NO='" + confirmNo + "'";
            // 更新医保交易表INS_OPD
     		result = new TParm(TJDODBTool.getInstance().update(sql,connection));
     		if (result.getErrCode() < 0) {
                 err("ERR:" + result.getErrCode() + result.getErrText()
                     + result.getErrName());
                 return result;
             }
             sql = " UPDATE INS_OPD_ORDER SET INV_NO='" + invNo
     		+ "' WHERE CONFIRM_NO='" + confirmNo + "'";
           // 更新医保交易表INS_OPD_ORDER
            result = new TParm(TJDODBTool.getInstance().update(sql,connection));
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }		
             
        }
       
        //医保-------end
        
       
        // System.out.println("票据主档数据All======"+prtInvoice);
        // 调用更新票据
         result = BILInvoiceTool.getInstance().updateDatePrint(prtInvoice,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        // 保存费用和票据参数
        TParm opbReceipt = new TParm();
        opbReceipt.setData("OPT_USER", parm.getData("OPT_USER"));
        opbReceipt.setData("OPT_DATE", parm.getData("OPT_DATE"));
        opbReceipt.setData("OPT_TERM", parm.getData("OPT_TERM"));
        // 查找原来票据参数
        TParm selInvrcp = new TParm();
        selInvrcp.setData("INV_NO", parm.getData("PRINT_NO"));
        // 更新原来票据参数
        TParm invRcpParm = new TParm();
        invRcpParm.setData("CANCEL_FLG", "3");
        invRcpParm.setData("CANCEL_USER", parm.getData("OPT_USER"));
        invRcpParm.setData("OPT_USER", parm.getData("OPT_USER"));
        invRcpParm.setData("OPT_TERM", parm.getData("OPT_TERM"));

        // 票据明细档新增一条新数据
        TParm insertInvrcp = new TParm();
        opbReceipt.setData("CASE_NO", caseNo);
        opbReceipt.setData("PRINT_NO", invNo);
        opbReceipt.setData("RECEIPT_NO", parm.getData("RECEIPT_NO"));
        selInvrcp.setData("RECP_TYPE", "OPB");
        invRcpParm.setData("RECP_TYPE", "OPB");
        insertInvrcp.setData("RECP_TYPE", "OPB");
        // 更新BIL_OPB_RECP 票据号
        result = OPBReceiptTool.getInstance().updatePrintNO(opbReceipt,
                connection);

        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        
        //更新opb_order表中print_flg
         sql = " UPDATE OPD_ORDER SET PRINT_FLG='Y'  " +
        		"WHERE CASE_NO='"+caseNo+"' AND RECEIPT_NO='"+parm.getData("RECEIPT_NO")+"'";
       // 更新医保交易表INS_OPD_ORDER
        result = new TParm(TJDODBTool.getInstance().update(sql,connection));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }		
        
        if(parm.getValue("PRINT_NO").length() > 0){
        	// 查询原票据明细档
            TParm oneInvParm = BILInvrcptTool.getInstance().getOneInv(selInvrcp);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }

            invRcpParm.setData("INV_NO", oneInvParm.getData("INV_NO", 0));
            // 补印,更新票据明细档票据状态
            result = BILInvrcptTool.getInstance()
                     .updataData(invRcpParm, connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }

        
        insertInvrcp.setData("INV_NO", invNo);
        insertInvrcp.setData("RECEIPT_NO", parm.getData("RECEIPT_NO"));
        insertInvrcp.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        insertInvrcp.setData("AR_AMT", parm.getValue("AR_AMT"));
        insertInvrcp.setData("CANCEL_FLG", "0");
        insertInvrcp.setData("STATUS", "0");
        insertInvrcp.setData("ADM_TYPE", parm.getValue("ADM_TYPE"));
        insertInvrcp.setData("OPT_TERM", parm.getData("OPT_TERM"));
        insertInvrcp.setData("OPT_USER", parm.getData("OPT_USER"));
        result = BILInvrcptTool.getInstance().insertData(insertInvrcp,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }

        result.setData("PRINT_NO", invNo);
        result.setData("OLDPRINT_NO", parm.getValue("PRINT_NO"));
        result.setData("PRINT_FLG", "Y");
        return result;
    }
    
    /**
     * 门诊收费补打票据:现金补打(Q医） 
     *
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm saveQeAppOPBRePrint(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = updateQeAppBilinvoice(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            connection.rollback();
            connection.close();
            return result;
        }
        return result;
    }
	
}
