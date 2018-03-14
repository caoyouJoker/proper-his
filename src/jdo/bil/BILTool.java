package jdo.bil;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.adm.ADMInpTool;
import jdo.sys.SystemTool;
import com.dongyang.data.TNull;
import com.dongyang.util.TypeTool;
import jdo.opb.OPBReceiptTool;
import jdo.ibs.IBSBilldTool;
import java.sql.Timestamp;
import jdo.ibs.IBSBillmTool;
import jdo.ins.TJINSRecpTool;

import java.util.Vector;
import com.dongyang.util.StringTool;
import jdo.adm.ADMTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.SYSStationTool;

/**
 *
 * <p>Title: ����ϵͳ������</p>
 *
 * <p>Description: ����ϵͳ������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class BILTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static BILTool instanceObject;
    /**
     * �õ�ʵ��
     * @return BILTool
     */
    public static BILTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILTool();
        return instanceObject;
    }

    /**
     * ������
     */
    public BILTool() {
        onInit();
    }

    /**
     * ��Ԥ����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onBillPay(TParm parm, TConnection connection) {
        TParm result = new TParm();
        TParm admParm = new TParm();
        admParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        TParm totBilPay = ADMInpTool.getInstance().queryCaseNo(parm);
        admParm.setData("CUR_AMT",
                        totBilPay.getDouble("CUR_AMT", 0) +
                        parm.getDouble("PRE_AMT"));
        admParm.setData("TOTAL_BILPAY",
                        totBilPay.getDouble("TOTAL_BILPAY", 0) +
                        parm.getDouble("PRE_AMT"));
        admParm.setData("OPT_USER", parm.getData("OPT_USER"));
        admParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
        //����adm_inp��STOP_BILL_FLG(ֹͣ�Ƽ�ע��),TOTAL_BILPAY(Ԥ����),CUR_AMT(Ŀǰ���)�ֶ�
        result = ADMInpTool.getInstance().updateForBillPay(admParm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        TParm bilInvricpt = new TParm();
        bilInvricpt.setData("RECP_TYPE", "PAY");
        bilInvricpt.setData("CANCEL_USER", new TNull(String.class));
        bilInvricpt.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        bilInvricpt.setData("OPT_USER", parm.getData("OPT_USER"));
        bilInvricpt.setData("INV_NO", parm.getData("INV_NO"));
        bilInvricpt.setData("OPT_TERM", parm.getData("OPT_TERM"));
        bilInvricpt.setData("CANCEL_DATE", new TNull(Timestamp.class));
        bilInvricpt.setData("TOT_AMT", parm.getData("PRE_AMT"));
        bilInvricpt.setData("AR_AMT", parm.getData("PRE_AMT"));

        TParm bilInvoice = new TParm();
        bilInvoice.setData("RECP_TYPE", "PAY");
        bilInvoice.setData("STATUS", "0");
        bilInvoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        bilInvoice.setData("START_INVNO", parm.getData("START_INVNO"));
        bilInvoice.setData("UPDATE_NO",
                           StringTool.addString(parm.getData("INV_NO").
                                                toString()));
        TParm actionParm = new TParm();
        actionParm = parm;
        actionParm.setData("BILINVRICPT", bilInvricpt.getData());
        actionParm.setData("BILINVOICE", bilInvoice.getData());
        //�洢Ʊ�ݵ�
        result = BILPrintTool.getInstance().saveBilPay(actionParm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * ��Ԥ����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onReturnBILPay(TParm parm, TConnection connection) {
        TParm result = new TParm();
        /**
         * ��REFOUND_FLG Ϊ'Y' ��ʾ�˷�
         */
        result = BILPayTool.getInstance().updataData(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        TParm inParm = new TParm();
        inParm = parm;
        String receiptNo = SystemTool.getInstance().getNo("ALL", "BIL",
                "RECEIPT_NO", "RECEIPT_NO");
//        System.out.println("ȡ��ԭ��" + receiptNo);
        inParm.setData("RECEIPT_NO", receiptNo);
        inParm.setData("TRANSACT_TYPE", "02");
        if (inParm.getData("CHECK_NO") == null)
            inParm.setData("CHECK_NO", new TNull(String.class));
        if (inParm.getData("REMARK") == null)
            inParm.setData("REMARK", new TNull(String.class));
        inParm.setData("PRE_AMT", -TypeTool.getDouble(parm.getData("PRE_AMT")));
        inParm.setData("RESET_RECP_NO", new TNull(String.class));
        /**
         * ����һ�ʸ�ֵ
         */
        result = BILPayTool.getInstance().insertData(inParm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        TParm admParm = new TParm();
        admParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        TParm totBilPay = ADMInpTool.getInstance().queryCaseNo(parm);
        admParm.setData("CUR_AMT",
                        totBilPay.getDouble("CUR_AMT", 0) +
                        parm.getDouble("PRE_AMT"));
        admParm.setData("TOTAL_BILPAY",
                        totBilPay.getDouble("TOTAL_BILPAY", 0) +
                        parm.getDouble("PRE_AMT"));
        admParm.setData("OPT_USER", parm.getData("OPT_USER"));
        admParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
        //����adm_inp��STOP_BILL_FLG(ֹͣ�Ƽ�ע��),TOTAL_BILPAY(Ԥ����),CUR_AMT(Ŀǰ���)�ֶ�
        result = ADMInpTool.getInstance().updateForBillPay(admParm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        TParm invRcpParm = new TParm();
        invRcpParm.setData("CANCEL_FLG", "1");
        invRcpParm.setData("CANCEL_USER", parm.getData("OPT_USER"));
        invRcpParm.setData("OPT_USER", parm.getData("OPT_USER"));
        invRcpParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
        invRcpParm.setData("RECP_TYPE", "PAY");
        invRcpParm.setData("INV_NO", parm.getData("PRINT_NO"));
        //��Ԥ�������Ʊ����ϸ��Ʊ��״̬
        result = BILInvrcptTool.getInstance().updataData(invRcpParm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }

        return result;
    }

    /**
     * ����Ԥ����
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onOffBilPay(TParm parm, TConnection connection) {
        TParm result = new TParm();
        //System.out.println("����Ԥ����"+parm);
        int count = parm.getCount("RECEIPT_NO");
        TParm inBilPayParm = new TParm();
        for (int i = 0; i < count; i++) {
            inBilPayParm.setData("RECEIPT_NO", parm.getData("RECEIPT_NO", i));
            inBilPayParm.setData("CASE_NO", parm.getData("CASE_NO", i));
            inBilPayParm.setData("IPD_NO", parm.getData("IPD_NO", i));
            inBilPayParm.setData("MR_NO", parm.getData("MR_NO", i));
            inBilPayParm.setData("TRANSACT_TYPE",
                                 parm.getData("TRANSACT_TYPE", i));
            inBilPayParm.setData("REFUND_FLG", parm.getData("REFUND_FLG", i));
            inBilPayParm.setData("RESET_BIL_PAY_NO",
                                 parm.getData("RESET_BIL_PAY_NO", i));
            inBilPayParm.setData("RESET_RECP_NO", parm.getData("IBS_RECP_NO"));
            inBilPayParm.setData("CASHIER_CODE", parm.getData("CASHIER_CODE", i));
            inBilPayParm.setData("CHARGE_DATE", parm.getData("CHARGE_DATE", i));
            inBilPayParm.setData("ADM_TYPE", parm.getData("ADM_TYPE", i));
            inBilPayParm.setData("PRE_AMT", parm.getData("PRE_AMT", i));
            inBilPayParm.setData("PAY_TYPE", parm.getData("PAY_TYPE", i));
            inBilPayParm.setData("CHECK_NO", parm.getData("CHECK_NO", i));
            inBilPayParm.setData("REMARK", parm.getData("REMARK", i));
            inBilPayParm.setData("REFUND_CODE", parm.getData("REFUND_CODE", i));
            inBilPayParm.setData("REFUND_DATE", parm.getData("REFUND_DATE", i));
            inBilPayParm.setData("PRINT_NO", parm.getData("PRINT_NO", i));
            inBilPayParm.setData("OPT_USER", parm.getData("OPT_USER", i));
            inBilPayParm.setData("OPT_DATE", parm.getData("OPT_DATE", i));
            inBilPayParm.setData("OPT_TERM", parm.getData("OPT_TERM", i));
            inBilPayParm.setData("IBS_RECEIPT_NO",
                                 parm.getData("IBS_RECEIPT_NO", i));
//            System.out.println("���³����վݺ�"+inBilPayParm);
            /**
             * ���³����վݺ�RESET_RECP_NO
             */
            result = BILPayTool.getInstance().updataOffBilPay(inBilPayParm,
                    connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
            TParm inParm = new TParm();
            inParm = inBilPayParm;
            String receiptNo = SystemTool.getInstance().getNo("ALL", "BIL",
                    "RECEIPT_NO", "RECEIPT_NO");
            inParm.setData("RECEIPT_NO", receiptNo);
            //����(����)
            inParm.setData("TRANSACT_TYPE", "03");
            if (inParm.getData("CHECK_NO") == null)
                inParm.setData("CHECK_NO", new TNull(String.class));
            if (inParm.getData("REMARK") == null)
                inParm.setData("REMARK", new TNull(String.class));
            inParm.setData("PRE_AMT",
                           -TypeTool.getDouble(inBilPayParm.getData("PRE_AMT")));
//            System.out.println("�������");
            //System.out.println("����ʱ��22222222222"+inBilPayParm.getData("OPT_DATE"));
            inParm.setData("CHARGE_DATE", inBilPayParm.getData("OPT_DATE"));
            inParm.setData("RESET_RECP_NO", parm.getData("IBS_RECP_NO"));
            //System.out.println("��ֵ��ֵ��ֵ��ֵ��ֵ��ֵ��ֵ��ֵ��ֵ��ֵ"+inParm);
            /**
             * ����һ�ʸ�ֵ
             */
            result = BILPayTool.getInstance().insertData(inParm, connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
            TParm admParm = new TParm();
            admParm.setData("CASE_NO", inBilPayParm.getValue("CASE_NO"));
            TParm totBilPay = ADMInpTool.getInstance().queryCaseNo(inBilPayParm);
            admParm.setData("CUR_AMT",
                            totBilPay.getDouble("CUR_AMT", 0) +
                            inBilPayParm.getDouble("PRE_AMT"));
            admParm.setData("TOTAL_BILPAY",
                            totBilPay.getDouble("TOTAL_BILPAY", 0) +
                            inBilPayParm.getDouble("PRE_AMT"));
            admParm.setData("OPT_USER", parm.getData("OPT_USER", 0));
            admParm.setData("OPT_TERM", parm.getData("OPT_TERM", 0));
            //����adm_inp��STOP_BILL_FLG(ֹͣ�Ƽ�ע��),TOTAL_BILPAY(Ԥ����),CUR_AMT(Ŀǰ���)�ֶ�
            result = ADMInpTool.getInstance().updateForBillPay(admParm,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return result;
            }
        }
        return result;
    }

    /**
     * �����ս�
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onSaveAccountOpb(TParm parm, TConnection connection) {
        TParm saveParm = parm.getParm("ACCOUNT");
        TParm result = new TParm();
        //ȡ��ԭ��õ��ս��
        String accountSeq = SystemTool.getInstance().getNo("ALL", "BIL",
                "ACCOUNT_SEQ",
                "ACCOUNT_SEQ");
        saveParm.setData("ACCOUNT_SEQ", accountSeq);
        if (accountSeq == null || accountSeq.equals("")) {
            out("�ս��ȡ�ô���!");
            return result.newErrParm( -1, "�ս��ȡ�ô���!");
        }
        //�ս�Ʊ�ݵ�
        TParm receiptParm = new TParm();
        receiptParm.setData("ACCOUNT_SEQ", saveParm.getValue("ACCOUNT_SEQ"));
        receiptParm.setData("ACCOUNT_USER", saveParm.getValue("ACCOUNT_USER"));
        receiptParm.setData("ADM_TYPE", saveParm.getValue("ADM_TYPE"));
        //=====zhangp 20120305 modify start
//        receiptParm.setData("PRINT_USER", saveParm.getValue("ACCOUNT_USER"));//========pangben 2011-12-30 �޸Ĳ�ѯ����
        receiptParm.setData("CASHIER_CODE", saveParm.getValue("ACCOUNT_USER"));
        //====zhangp 20120305 modify end
        receiptParm.setData("BILL_DATE", saveParm.getData("ACCOUNT_DATA"));
        if (receiptParm.getValue("ADM_TYPE").equals("") ||
            receiptParm.getValue("ADM_TYPE") == null) {
            result = OPBReceiptTool.getInstance().saveAcconntReceiptAll(
                    receiptParm,
                    connection);
        } else {
            result = OPBReceiptTool.getInstance().saveAcconntReceipt(
                    receiptParm,
                    connection);
        }
        if (result.getErrCode() < 0) {
            err("�ս�receipt���� " + result.getErrText());
            return result;
        }
        //�ս��ӡƱ�ݵ�
        TParm invrcptParm = new TParm();
        invrcptParm.setData("ACCOUNT_SEQ", saveParm.getValue("ACCOUNT_SEQ"));
        invrcptParm.setData("ACCOUNT_USER", saveParm.getValue("ACCOUNT_USER"));
        invrcptParm.setData("RECP_TYPE", saveParm.getValue("RECP_TYPE"));
        invrcptParm.setData("ADM_TYPE", saveParm.getValue("ADM_TYPE"));
        invrcptParm.setData("CASHIER_CODE", saveParm.getValue("ACCOUNT_USER"));
        invrcptParm.setData("BILL_DATE", saveParm.getData("ACCOUNT_DATA"));
        invrcptParm.setData("PRINT_DATE", saveParm.getData("ACCOUNT_DATA"));
        if (invrcptParm.getValue("ADM_TYPE").equals("") ||
            invrcptParm.getValue("ADM_TYPE") == null) {
            result = BILInvrcptTool.getInstance().accountAll(invrcptParm,
                    connection);
        } else {
            result = BILInvrcptTool.getInstance().account(invrcptParm,
                    connection);
        }
        if (result.getErrCode() < 0) {
            err("�ս�invrcpt���� " + result.getErrText());
            return result;
        }
        //====zhangp 20120327 start
        result = updateRestInvrcp(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //====zhangp 20120327 end
        //�ս���ս�
        TParm accountParm = new TParm();
        accountParm.setData("ACCOUNT_SEQ", saveParm.getValue("ACCOUNT_SEQ"));
        accountParm.setData("ACCOUNT_USER", saveParm.getValue("ACCOUNT_USER"));
        accountParm.setData("ACCOUNT_TYPE", saveParm.getValue("RECP_TYPE"));
        accountParm.setData("ACCOUNT_DATE", saveParm.getData("ACCOUNT_DATA"));
        accountParm.setData("ADM_TYPE", saveParm.getValue("ADM_TYPE"));
        accountParm.setData("AR_AMT", saveParm.getValue("AR_AMT"));
        accountParm.setData("OPT_USER", saveParm.getValue("ACCOUNT_USER"));
        accountParm.setData("OPT_TERM", saveParm.getValue("OPT_TERM"));
        accountParm.setData("STATUS", saveParm.getValue("STATUS"));
        accountParm.setData("INVALID_COUNT", saveParm.getValue("INVALID_COUNT"));
        accountParm.setData("REGION_CODE", saveParm.getValue("REGION_CODE"));
        //===zhangp 20120312 start
        if (accountParm.getValue("ADM_TYPE").equals("") ||
            accountParm.getValue("ADM_TYPE") == null) {
            accountParm.setData("ADM_TYPE", "O");
            result = BILAccountTool.getInstance().insertAccount(accountParm,
                    connection);
            accountParm.setData("ADM_TYPE", "E");
            result = BILAccountTool.getInstance().insertAccount(accountParm,
                    connection);
            accountParm.setData("ADM_TYPE", "H");
            result = BILAccountTool.getInstance().insertAccount(accountParm,
                    connection);
        } else {
            result = BILAccountTool.getInstance().insertAccount(accountParm,
                    connection);
        }
        //===zhangp 20120312 end
        if (result.getErrCode() < 0) {
            err("�ս�����ս����ݴ��� " + result.getErrText());
            return result;
        }
        //===zhangp 20120619 start
        result = updateReprintInvrcp(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //===zhangp 20120619 end
        return result;

    }

    /**
     * �˵���˱���
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onSaveAuditFee(TParm parm, TConnection connection) {
        TParm result = new TParm();
//        System.out.println("�˵���˱���"+parm);
        String flg = "";
        String approveFlg = parm.getValue("APPROVE_FLG");
        if ("Y".equals(approveFlg))
            flg = "N";
        else
            flg = "Y";
        String upApproveFlg =
                " UPDATE IBS_BILLM SET APPROVE_FLG = '" + approveFlg +
                "' WHERE BILL_NO IN (" +
                parm.getValue("BILL_NO") + ") AND APPROVE_FLG = '" + flg + "' ";
//        System.out.println("upApproveFlg"+upApproveFlg);
        result = new TParm(TJDODBTool.getInstance().update(upApproveFlg,
                connection));
        if (result.getErrCode() < 0) {
            err("���ݴ��� " + result.getErrText());
            return result;
        }
        return result;

    }
    /**
     * �˵���˸������״̬
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onAuditFeeCheck(TParm parm, TConnection connection) {
//        System.out.println("�����˵���˸������״̬JDO"+parm);
        TParm result = new TParm();
        String caseNo = parm.getValue("CASE_NO");
        String ibsBillmSql =
                " SELECT CASE_NO, APPROVE_FLG " +
                "   FROM IBS_BILLM " +
                "  WHERE CASE_NO = '" + caseNo + "' " +
                "    AND (APPROVE_FLG = 'N' OR APPROVE_FLG IS NULL) " +
                "    AND REFUND_FLG = 'N'";//caowl 20130427 ��ѯδ���ϵ��˵��Ƿ�ȫ�����
        TParm ibsBillmParm = new TParm(TJDODBTool.getInstance().select(
                ibsBillmSql));
        if (ibsBillmParm.getErrCode() < 0) {
            err("���ݴ��� " + ibsBillmParm.getErrText());
            return result;
        }
//        System.out.println("���������"+ibsBillmParm);
        if (ibsBillmParm.getCount("CASE_NO") > 0) {
            result = ADMTool.getInstance().updateBillStatus("2", caseNo,
                    connection);
            if (result.getErrCode() < 0) {
                err("���ݴ��� " + result.getErrText());
                return result;
            }
        } else {
            result = ADMTool.getInstance().updateBillStatus("3", caseNo,
                    connection);
            if (result.getErrCode() < 0) {
                err("���ݴ��� " + result.getErrText());
                return result;
            }
        }
        return result;

    }

    /**
     * סԺ�սᱣ��
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onSaveAcctionBIL(TParm parm, TConnection connection) {
        TParm result = new TParm();
        Vector cashiers = new Vector();
        String cashierCode = parm.getValue("CASHIER_CODE");
        TParm p = new TParm();
        //===zhangp 20120502 start
//        Timestamp date = SystemTool.getInstance().getDate();
        String accntdate = parm.getValue("ACCOUNT_DATE");
        Timestamp date = StringTool.getTimestamp(accntdate, "yyyyMMddHHmmss");
        //===zhangp 20120502 end
        p.setData("CHARGE_DATE", date);
        p.setData("REGION_CODE", parm.getValue("REGION_CODE"));

        if (cashierCode != null && cashierCode.length() > 0) {
            cashiers.add(cashierCode);
        } else {
            TParm cashiersParm = BILIBSRecpmTool.getInstance().selCashier(p);
            if (cashiersParm.getErrCode() < 0)
                return cashiersParm;
            if (cashiersParm.getCount() <= 0) {
                result.setErr( -1, "û���ҵ�δ��������!");
                return result;
            }
            cashiers = (Vector) cashiersParm.getData("CASHIER_CODE");
        }

        p.setData("CHARGE_DATE", StringTool.getString(date, "yyyyMMddHHmmss"));
//        System.out.println("סԺ�ս���Ա"+cashiers);
        for (int i = 0; i < cashiers.size(); i++) {
            p.setData("CASHIER_CODE", cashiers.get(i));
            TParm accout = BILIBSRecpmTool.getInstance().selDateForAccount(p);
            if (accout.getErrCode() < 0) {
                err(accout.getErrName() + " " + accout.getErrText());
                return accout;
            }
            if (accout.getCount() <= 0)
                continue;

            //����ȡ��ԭ��
            String accountNo = SystemTool.getInstance().getNo("ALL", "BIL",
                    "ACCOUNT_SEQ",
                    "ACCOUNT_SEQ");

            //����סԺ�վݵ�
            TParm upIBSRecp = new TParm();
            upIBSRecp.setData("ACCOUNT_SEQ", accountNo);
            upIBSRecp.setData("ACCOUNT_USER", parm.getData("OPT_USER"));
            upIBSRecp.setData("CASHIER_CODE", p.getData("CASHIER_CODE"));
            upIBSRecp.setData("CHARGE_DATE", p.getData("CHARGE_DATE"));
            result = BILIBSRecpmTool.getInstance().updateAccount(upIBSRecp,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }

            //����Ʊ��״̬,Ʊ����ϸ��д���ս��,��Ա,ʱ��
            TParm upBILIbsInvRcp = new TParm();
            upBILIbsInvRcp.setData("ACCOUNT_SEQ", accountNo);
            upBILIbsInvRcp.setData("ACCOUNT_USER", parm.getData("OPT_USER"));
            upBILIbsInvRcp.setData("RECP_TYPE", "IBS");
            upBILIbsInvRcp.setData("ADM_TYPE", "I");
            upBILIbsInvRcp.setData("CASHIER_CODE", p.getData("CASHIER_CODE"));
            upBILIbsInvRcp.setData("PRINT_DATE", p.getData("CHARGE_DATE"));
            result = BILInvrcptTool.getInstance().account(upBILIbsInvRcp,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }

            //��ѯ��������
            TParm selCancelRecpNoParm = new TParm();
            selCancelRecpNoParm.setData("RECP_TYPE", "IBS");
            selCancelRecpNoParm.setData("ADM_TYPE", "I");
            selCancelRecpNoParm.setData("CASHIER_CODE",
                                        p.getData("CASHIER_CODE"));
            selCancelRecpNoParm.setData("PRINT_DATE", p.getData("CHARGE_DATE"));
            TParm selCancelRecpNo = BILInvrcptTool.getInstance().
                                    getInvalidCount(
                                            selCancelRecpNoParm);
            if (selCancelRecpNo.getErrCode() < 0) {
                err(selCancelRecpNo.getErrName() + " " +
                    selCancelRecpNo.getErrText());
                return selCancelRecpNo;
            }
            int canselCount = selCancelRecpNo.getInt("COUNT", 0);

            //������
            int count = accout.getCount();
            double amt = 0;
            for (int j = 0; j < count; j++)
                amt += accout.getDouble("AR_AMT", j);

            TParm accountParm = new TParm();
            accountParm.setData("ACCOUNT_TYPE", "IBS");
            accountParm.setData("ACCOUNT_SEQ", accountNo);
            accountParm.setData("ACCOUNT_USER", parm.getData("OPT_USER"));
            accountParm.setData("ACCOUNT_DATE", p.getData("CHARGE_DATE"));
            accountParm.setData("AR_AMT", amt);
            accountParm.setData("STATUS", "0");
            accountParm.setData("INVALID_COUNT", canselCount);
            accountParm.setData("ADM_TYPE", "I");
            accountParm.setData("REGION_CODE", parm.getData("REGION_CODE"));
            accountParm.setData("OPT_USER", parm.getData("OPT_USER"));
            accountParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
            result = BILAccountTool.getInstance().insertAccount(accountParm,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
        }
        return result;
    }

    /**
     * סԺ���˵��ٻر���
     * @param caseNo String
     * @param optUser String
     * @param optTerm String
     * @param connection TConnection
     * @return TParm
     */
    public TParm admReturn(String caseNo, String optUser, String optTerm,
                           TConnection connection) {
        TParm result = new TParm();
        TParm adminpParm = new TParm();
        adminpParm.setData("CASE_NO", caseNo);
        adminpParm.setData("OPT_USER", optUser);
        adminpParm.setData("OPT_TERM", optTerm);
        //סԺ�ٻط��� ����CASE_NO;OPT_USER;OPT_TERM
        result = ADMInpTool.getInstance().returnAdm(adminpParm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        result = ADMTool.getInstance().updateBillStatus("0", caseNo,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * �վ��ٻر���
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertRcpReturn(TParm parm, TConnection connection) {
    	
        TParm result = new TParm();
        TParm actionParm = parm.getParm("DATA");
        String receiptNo = actionParm.getValue("RECEIPT_NO");
        double arAmt = TypeTool.getDouble(actionParm.getValue("AR_AMT"));
        String mrNo = actionParm.getValue("MR_NO");
        String ipdNo = actionParm.getValue("IPD_NO");
        String caseNo = actionParm.getValue("CASE_NO");
        String optUser = actionParm.getValue("OPT_USER");
        Timestamp optDate = actionParm.getTimestamp("OPT_DATE");
        String optTerm = actionParm.getValue("OPT_TERM");
        String flg = actionParm.getValue("FLG");
        TParm adminpParm = new TParm();
        if ("Y".equals(flg)) {
            //סԺ�ٻ�
        	result=admReturn(caseNo, optUser, optTerm, connection);
        	if (result.getErrCode() < 0) {//======pangben 2014-7-8 ��ӱ����Ժ󷵻��߼�
                err(result.getErrText());
                return result;
            }
        } else {
            adminpParm.setData("CASE_NO", caseNo);
            adminpParm.setData("OPT_USER", optUser);
            adminpParm.setData("OPT_TERM", optTerm);
            //System.out.println("�����ٻ����"+adminpParm);
            //�����ٻ�
            result = ADMInpTool.getInstance().returnAdmBill(adminpParm,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
            result = ADMTool.getInstance().updateBillStatus("1", caseNo,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrText());
                return result;
            }
        }
        TParm selRecpmParm = new TParm();
        selRecpmParm.setData("CASE_NO", caseNo);
        selRecpmParm.setData("RECEIPT_NO", receiptNo);
        //�վ���������
        TParm selRecpm = BILIBSRecpmTool.getInstance().selectAllData(
                selRecpmParm);
        //===zhangp 20130717 start
        TParm tjInsParm = TJINSRecpTool.getInstance().selectTjInsDataForReturn(selRecpmParm);
        //===zhangp 20130717 end
        //סԺ��ˮ��
        String newReceiptNo = SystemTool.getInstance().getNo("ALL", "IBS",
                "RECEIPT_NO", "RECEIPT_NO");
        TParm selInvrcpParm = new TParm();
        selInvrcpParm.setData("RECP_TYPE", "IBS");
        selInvrcpParm.setData("RECEIPT_NO", receiptNo);
        selInvrcpParm.setData("STATUS", "0");
        TParm invrcpParm = BILInvrcptTool.getInstance().selectAllData(
                selInvrcpParm);
        String invNo = invrcpParm.getValue("INV_NO", 0);
        //����Ʊ��
        TParm bilInvrcptParm = new TParm();
        bilInvrcptParm.setData("CANCEL_FLG", "1");
        bilInvrcptParm.setData("CANCEL_USER", optUser);
        bilInvrcptParm.setData("OPT_USER", optUser);
        bilInvrcptParm.setData("OPT_TERM", optTerm);
        bilInvrcptParm.setData("RECP_TYPE", "IBS");
        bilInvrcptParm.setData("INV_NO", invNo);
        result = BILInvrcptTool.getInstance().updataData(bilInvrcptParm,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        TParm bilPayParm = new TParm();
        bilPayParm.setData("CASE_NO", caseNo);
        TParm bilPay = BILPayTool.getInstance().selectAllData(bilPayParm);
        TParm newBilPay = new TParm();
        String bilPayrecpNo = "";
        if (bilPay.getCount("CASE_NO") > 0) {
            //Ԥ������ˮ��(For �س�)
            bilPayrecpNo = SystemTool.getInstance().getNo("ALL", "BIL",
                    "RECEIPT_NO", "RECEIPT_NO");
            newBilPay.setData("RECEIPT_NO", bilPayrecpNo);
            newBilPay.setData("CASE_NO", caseNo);
            newBilPay.setData("IPD_NO", ipdNo);
            newBilPay.setData("MR_NO", mrNo);
            newBilPay.setData("TRANSACT_TYPE", "04"); //01���ѡ�02�˷ѡ�03���㡢04�س塢05����
            newBilPay.setData("REFUND_FLG", "N");
            newBilPay.setData("CASHIER_CODE", optUser);
            newBilPay.setData("CHARGE_DATE", optDate);
            newBilPay.setData("ADM_TYPE", bilPay.getData("ADM_TYPE", 0));
            newBilPay.setData("PRE_AMT", arAmt);
            newBilPay.setData("PAY_TYPE", bilPay.getData("PAY_TYPE", 0));
            newBilPay.setData("CHECK_NO",
                              bilPay.getData("CHECK_NO", 0) == null ?
                              new TNull(String.class) :
                              bilPay.getData("CHECK_NO", 0));
            newBilPay.setData("REMARK",
                              bilPay.getData("REMARK", 0) == null ?
                              new TNull(String.class) :
                              bilPay.getData("REMARK", 0));
            newBilPay.setData("RESET_RECP_NO", new TNull(String.class));
            newBilPay.setData("PRINT_NO", bilPay.getData("PRINT_NO", 0));
            newBilPay.setData("OPT_USER", optUser);
            newBilPay.setData("OPT_TERM", optTerm);
            //�س�Ԥ����
            result = BILPayTool.getInstance().insertData(newBilPay, connection);
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return result;
            }
        }else{
        	//pangben 2014-8-4 �˲���û�г�ֵԤ�������������Ʊ��ʱû�лس�˴�����Ʊ�ݵĽ��
        	//Ԥ������ˮ��(For �س�)
            bilPayrecpNo = SystemTool.getInstance().getNo("ALL", "BIL",
                    "RECEIPT_NO", "RECEIPT_NO");
            newBilPay.setData("RECEIPT_NO", bilPayrecpNo);
            newBilPay.setData("CASE_NO", caseNo);
            newBilPay.setData("IPD_NO", ipdNo);
            newBilPay.setData("MR_NO", mrNo);
            newBilPay.setData("TRANSACT_TYPE", "04"); //01���ѡ�02�˷ѡ�03���㡢04�س塢05����
            newBilPay.setData("REFUND_FLG", "N");
            newBilPay.setData("CASHIER_CODE", optUser);
            newBilPay.setData("CHARGE_DATE", optDate);
            newBilPay.setData("ADM_TYPE","I");
            newBilPay.setData("PRE_AMT", arAmt);
            newBilPay.setData("PAY_TYPE", "PAY_CASH");
            newBilPay.setData("CHECK_NO",
            		 new TNull(String.class) );
            newBilPay.setData("REMARK",
            		 new TNull(String.class) );
            newBilPay.setData("RESET_RECP_NO", new TNull(String.class));
            newBilPay.setData("PRINT_NO",bilPayrecpNo);
            newBilPay.setData("OPT_USER", optUser);
            newBilPay.setData("OPT_TERM", optTerm);
            //�س�Ԥ����
            result = BILPayTool.getInstance().insertData(newBilPay, connection);
            if (result.getErrCode() < 0) {
                err(result.getErrCode() + " " + result.getErrText());
                return result;
            }
        }
        TParm admParm = new TParm();
        admParm.setData("CASE_NO", caseNo);
        TParm totBilPay = ADMInpTool.getInstance().queryCaseNo(admParm);
        double bilPayN = newBilPay.getData("PRE_AMT") == null ? 0.00 :
                         newBilPay.getDouble("PRE_AMT");
        admParm.setData("CUR_AMT",
                        totBilPay.getDouble("CUR_AMT", 0) + bilPayN);
        admParm.setData("TOTAL_BILPAY",
                        totBilPay.getDouble("TOTAL_BILPAY", 0) + bilPayN);
        admParm.setData("OPT_USER", optUser);
        admParm.setData("OPT_TERM", optTerm);
        //����adm_inp��STOP_BILL_FLG(ֹͣ�Ƽ�ע��),TOTAL_BILPAY(Ԥ����),CUR_AMT(Ŀǰ���)�ֶ�
        result = ADMInpTool.getInstance().updateForBillPay(admParm,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }

        //�վ���������
        TParm insertRecpmParm = new TParm();
        insertRecpmParm.setData("CASE_NO", selRecpm.getValue("CASE_NO", 0));
        insertRecpmParm.setData("RECEIPT_NO", newReceiptNo);
        insertRecpmParm.setData("ADM_TYPE", selRecpm.getValue("ADM_TYPE", 0));
        insertRecpmParm.setData("IPD_NO", selRecpm.getValue("IPD_NO", 0));
        insertRecpmParm.setData("MR_NO", selRecpm.getValue("MR_NO", 0));
        insertRecpmParm.setData("REGION_CODE",
                                selRecpm.getValue("REGION_CODE", 0));
        insertRecpmParm.setData("RESET_RECEIPT_NO",
                                selRecpm.getValue("RESET_RECEIPT_NO", 0));
        insertRecpmParm.setData("REFUND_FLG", selRecpm.getValue("REFUND_FLG", 0));
        insertRecpmParm.setData("CASHIER_CODE",
                                selRecpm.getValue("CASHIER_CODE", 0));
        insertRecpmParm.setData("CHARGE_DATE", optDate); //=====caowl 20130419
        insertRecpmParm.setData("OWN_AMT",
                                selRecpm.getValue("OWN_AMT", 0) == null ?
                                0.00 :
                                -selRecpm.getDouble("OWN_AMT", 0));
        insertRecpmParm.setData("DISCT_RESON",
                                selRecpm.getValue("DISCT_RESON", 0) == null ?
                                new TNull(String.class) :
                                selRecpm.getValue("DISCT_RESON", 0));
        insertRecpmParm.setData("DISCNT_AMT",
                                selRecpm.getValue("DISCNT_AMT", 0) == null ?
                                0.00 :
                                -selRecpm.getDouble("DISCNT_AMT", 0));
        insertRecpmParm.setData("AR_AMT",
                                selRecpm.getValue("AR_AMT", 0) == null ?
                                0.00 :
                                -selRecpm.getDouble("AR_AMT", 0));
        insertRecpmParm.setData("REFUND_CODE", "");
        insertRecpmParm.setData("REFUND_DATE", "");
        insertRecpmParm.setData("PAY_CASH",
                                selRecpm.getValue("PAY_CASH", 0) == null ?
                                0.00 :
                                -selRecpm.getDouble("PAY_CASH", 0));
        insertRecpmParm.setData("PAY_MEDICAL_CARD",
                                selRecpm.getValue("PAY_MEDICAL_CARD", 0) == null ?
                                0.00 :
                                -selRecpm.getDouble("PAY_MEDICAL_CARD", 0));
        insertRecpmParm.setData("PAY_BANK_CARD",
                                selRecpm.getValue("PAY_BANK_CARD", 0) == null ?
                                0.00 :
                                -selRecpm.getDouble("PAY_BANK_CARD", 0));
        insertRecpmParm.setData("PAY_INS_CARD",
                                selRecpm.getValue("PAY_INS_CARD", 0) == null ?
                                0.00 :
                                -selRecpm.getDouble("PAY_INS_CARD", 0));
        insertRecpmParm.setData("PAY_CHECK",
                                selRecpm.getValue("PAY_CHECK", 0) == null ?
                                0.00 :
                                -selRecpm.getDouble("PAY_CHECK", 0));
        insertRecpmParm.setData("PAY_DEBIT",
                                selRecpm.getValue("PAY_DEBIT", 0) == null ?
                                0.00 :
                                -selRecpm.getDouble("PAY_DEBIT", 0));
        insertRecpmParm.setData("PAY_BILPAY",
                                selRecpm.getValue("PAY_BILPAY", 0) == null ?
                                0.00 :
                                -selRecpm.getDouble("PAY_BILPAY", 0));
        insertRecpmParm.setData("PAY_INS",
                                selRecpm.getValue("PAY_INS", 0) == null ?
                                0.00 :
                                -selRecpm.getDouble("PAY_INS", 0));
        insertRecpmParm.setData("PAY_OTHER1",
                                selRecpm.getValue("PAY_OTHER1", 0) == null ?
                                0.00 :
                                -selRecpm.getDouble("PAY_OTHER1", 0));
        insertRecpmParm.setData("PAY_OTHER2",
                                selRecpm.getValue("PAY_OTHER2", 0) == null ?
                                0.00 :
                                -selRecpm.getDouble("PAY_OTHER2", 0));
        insertRecpmParm.setData("PAY_REMK",
                                selRecpm.getValue("PAY_REMK", 0) == null ?
                                0.00 :
                                -selRecpm.getDouble("PAY_REMK", 0));
        insertRecpmParm.setData("PREPAY_WRTOFF",
                                selRecpm.getValue("PREPAY_WRTOFF", 0) == null ?
                                0.00 :
                                -selRecpm.getDouble("PREPAY_WRTOFF", 0));
        insertRecpmParm.setData("PRINT_NO", selRecpm.getValue("PRINT_NO", 0) == null ?
                                new TNull(String.class) :
                                selRecpm.getValue("PRINT_NO", 0)
                );
        insertRecpmParm.setData("OPT_USER", optUser);
        insertRecpmParm.setData("OPT_TERM", optTerm);
        //�����վ�����
        result = BILIBSRecpmTool.getInstance().insertData(insertRecpmParm,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //===zhangp 20130717 start
        tjInsParm.setData("CASE_NO", selRecpm.getValue("CASE_NO", 0));
        tjInsParm.setData("RECEIPT_NO", newReceiptNo);
        result = TJINSRecpTool.getInstance().updateRecpm(tjInsParm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //===zhangp 20130717 end
        TParm updateRecpmParm = new TParm();
        updateRecpmParm.setData("CASE_NO", caseNo);
        updateRecpmParm.setData("RECEIPT_NO", receiptNo);
        updateRecpmParm.setData("REFUND_FLG", "Y");
        updateRecpmParm.setData("RESET_RECEIPT_NO", newReceiptNo);
        updateRecpmParm.setData("REFUND_CODE", optUser);
        //�����վ�����
        result = BILIBSRecpmTool.getInstance().updataData(updateRecpmParm,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //�վ���ϸ������
        TParm selRecpdParm = new TParm();
        selRecpdParm.setData("RECEIPT_NO", receiptNo);
        TParm selRecpd = BILIBSRecpdTool.getInstance().selectAllData(
                selRecpdParm);
//        System.out.println("�վ���ϸ������" + selRecpd);
        //�����վ���ϸ��
        TParm inRecpdParm = new TParm();
        int recpdCount = selRecpd.getCount("RECEIPT_NO");
        for (int i = 0; i < recpdCount; i++) {
            inRecpdParm.setData("RECEIPT_NO", newReceiptNo);
            inRecpdParm.setData("BILL_NO", selRecpd.getData("BILL_NO", i));
            inRecpdParm.setData("REXP_CODE", selRecpd.getData("REXP_CODE", i));
            inRecpdParm.setData("WRT_OFF_AMT",
                                -selRecpd.getDouble("WRT_OFF_AMT", i));
            inRecpdParm.setData("OPT_USER", selRecpd.getData("OPT_USER", i));
            inRecpdParm.setData("OPT_TERM", selRecpd.getData("OPT_USER", i));
            inRecpdParm.setData("RESET_RECEIPT_NO", new TNull(String.class));
            inRecpdParm.setData("REFUND_FLG", new TNull(String.class));
            inRecpdParm.setData("REFUND_CODE", new TNull(String.class));
            result = BILIBSRecpdTool.getInstance().insertData(inRecpdParm,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
        }
        TParm updateRecpdParm = new TParm();
        updateRecpdParm.setData("CASE_NO", caseNo);
        updateRecpdParm.setData("RECEIPT_NO", receiptNo);
        updateRecpdParm.setData("REFUND_FLG", "Y");
        updateRecpdParm.setData("RESET_RECEIPT_NO", newReceiptNo);
        updateRecpdParm.setData("REFUND_CODE", optUser);
        //�����վ���ϸ��
        result = BILIBSRecpdTool.getInstance().updataData(updateRecpdParm,
                connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }

        //�˵���������
        TParm selBillmParm = new TParm();
        selBillmParm.setData("RECEIPT_NO", receiptNo);
        TParm selBillm = IBSBillmTool.getInstance().selectAllData(selBillmParm); 
        int billCount = selBillm.getCount("CASE_NO");
        for (int k = 0; k < billCount; k++) {
            TParm updateBillmParm = new TParm();
            updateBillmParm.setData("BILL_NO", selBillm.getData("BILL_NO", k));
            updateBillmParm.setData("RECEIPT_NO", receiptNo);
            updateBillmParm.setData("REFUND_BILL_NO", "");
            updateBillmParm.setData("REFUND_CODE", optUser);
            result = IBSBillmTool.getInstance().updataForReturnRecp(
                    updateBillmParm,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
            TParm insertBillmParm = new TParm();
            TParm selMaxSeqParm = new TParm();
            selMaxSeqParm.setData("RECEIPT_NO", receiptNo);
            selMaxSeqParm.setData("BILL_NO", selBillm.getData("BILL_NO", k));
            TParm selMaxSeq = IBSBillmTool.getInstance().selMaxSeq(
                    selMaxSeqParm);
            int maxBillSeq = selMaxSeq.getInt("BILL_SEQ", 0);
            //����IBS_BILLM���и�����
            insertBillmParm.setData("BILL_SEQ", maxBillSeq + 1);
            insertBillmParm.setData("BILL_NO", selBillm.getData("BILL_NO", k));
            insertBillmParm.setData("CASE_NO", selBillm.getData("CASE_NO", k));
            insertBillmParm.setData("IPD_NO", selBillm.getData("IPD_NO", k));
            insertBillmParm.setData("MR_NO", selBillm.getData("MR_NO", k));
            insertBillmParm.setData("BILL_DATE",
            		selBillm.getData("BILL_DATE", k)== null ?
                             new TNull(Timestamp.class) :
            						selBillm.getData("BILL_DATE", k));
            insertBillmParm.setData("REFUND_FLG", "Y");//modify by caowl 20130131 
            insertBillmParm.setData("REFUND_BILL_NO",
                                    selBillm.getData("REFUND_BILL_NO", 0) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("REFUND_BILL_NO", 0));
            insertBillmParm.setData("RECEIPT_NO",
            		newReceiptNo);//caowl 20130419
            insertBillmParm.setData("CHARGE_DATE",optDate);//caowl 20130419
            insertBillmParm.setData("CTZ1_CODE",
                                    selBillm.getData("CTZ1_CODE", k) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("CTZ1_CODE", k));
            insertBillmParm.setData("CTZ2_CODE",
                                    selBillm.getData("CTZ2_CODE", k) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("CTZ2_CODE", k));
            insertBillmParm.setData("CTZ3_CODE",
                                    selBillm.getData("CTZ3_CODE", k) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("CTZ3_CODE", k));
            insertBillmParm.setData("BEGIN_DATE",
                                    selBillm.getData("BEGIN_DATE", k));
            insertBillmParm.setData("END_DATE", selBillm.getData("END_DATE", k));
            insertBillmParm.setData("DISCHARGE_FLG",
                                    selBillm.getData("DISCHARGE_FLG", k) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("DISCHARGE_FLG", k));
            insertBillmParm.setData("DEPT_CODE",
                                    selBillm.getData("DEPT_CODE", k));
            insertBillmParm.setData("STATION_CODE",
                                    selBillm.getData("STATION_CODE", k));
            TParm stationInfo = SYSStationTool.getInstance().selStationRegion(
                    selBillm.getValue("STATION_CODE", k));
            insertBillmParm.setData("REGION_CODE",
                                    stationInfo.getData("REGION_CODE", 0));
            insertBillmParm.setData("BED_NO", selBillm.getData("BED_NO", k));
            insertBillmParm.setData("OWN_AMT", -selBillm.getDouble("OWN_AMT", k));
            insertBillmParm.setData("NHI_AMT", -selBillm.getDouble("NHI_AMT", k));
            insertBillmParm.setData("APPROVE_FLG",
                                    selBillm.getData("APPROVE_FLG", k));
            insertBillmParm.setData("REDUCE_REASON",
                                    selBillm.getData("REDUCE_REASON", k) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("REDUCE_REASON", k));
            insertBillmParm.setData("REDUCE_AMT",
                                    selBillm.getData("REDUCE_AMT", k));
            insertBillmParm.setData("REDUCE_DATE",
                                    selBillm.getData("REDUCE_DATE", k) == null ?
                                    new TNull(Timestamp.class) :
                                    selBillm.getData("REDUCE_DATE", k));
            insertBillmParm.setData("REDUCE_DEPT_CODE",
                                    selBillm.getData("REDUCE_DEPT_CODE", k) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("REDUCE_DEPT_CODE", k));
            insertBillmParm.setData("REDUCE_RESPOND",
                                    selBillm.getData("REDUCE_RESPOND", k) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("REDUCE_RESPOND", k));
            insertBillmParm.setData("AR_AMT", -selBillm.getDouble("AR_AMT", k));
            insertBillmParm.setData("PAY_AR_AMT",
                                    selBillm.getDouble("PAY_AR_AMT", k));
            insertBillmParm.setData("CANDEBT_CODE",
                                    selBillm.getData("CANDEBT_CODE", k) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("CANDEBT_CODE", k));
            insertBillmParm.setData("CANDEBT_PERSON",
                                    selBillm.getData("CANDEBT_PERSON", k) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("CANDEBT_PERSON", k));
            insertBillmParm.setData("REFUND_CODE",optUser);//caowl 20130424
            insertBillmParm.setData("REFUND_DATE",optDate);//caowl 20130424
            insertBillmParm.setData("OPT_USER", optUser);
            insertBillmParm.setData("OPT_TERM", optTerm);
            result = IBSBillmTool.getInstance().insertdata(insertBillmParm,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }
//        String billNo = SystemTool.getInstance().getNo("ALL", "IBS",
//          "BILL_NO",
//          "BILL_NO");
            //�����˵�����������
            insertBillmParm = new TParm();
            insertBillmParm.setData("BILL_SEQ", maxBillSeq + 2);
            insertBillmParm.setData("BILL_NO", selBillm.getData("BILL_NO", k));
            insertBillmParm.setData("CASE_NO", selBillm.getData("CASE_NO", k));
            insertBillmParm.setData("IPD_NO", selBillm.getData("IPD_NO", k));
            insertBillmParm.setData("MR_NO", selBillm.getData("MR_NO", k));
            insertBillmParm.setData("BILL_DATE",
            		selBillm.getData("BILL_DATE", k)== null ?
                            new TNull(Timestamp.class) :
           						selBillm.getData("BILL_DATE", k));
            insertBillmParm.setData("REFUND_FLG", "N");
            insertBillmParm.setData("REFUND_BILL_NO", "");
            insertBillmParm.setData("RECEIPT_NO", "");
            insertBillmParm.setData("CHARGE_DATE",
                                    new TNull(Timestamp.class));
            insertBillmParm.setData("CTZ1_CODE",
                                    selBillm.getData("CTZ1_CODE", k) == null ?
                                    "" :
                                    selBillm.getData("CTZ1_CODE", k));
            insertBillmParm.setData("CTZ2_CODE",
                                    selBillm.getData("CTZ2_CODE", k) == null ?
                                    "" :
                                    selBillm.getData("CTZ2_CODE", k));
            insertBillmParm.setData("CTZ3_CODE",
                                    selBillm.getData("CTZ3_CODE", k) == null ?
                                    "" :
                                    selBillm.getData("CTZ3_CODE", k));
            insertBillmParm.setData("BEGIN_DATE",
                                    selBillm.getData("BEGIN_DATE", k));
            insertBillmParm.setData("END_DATE", selBillm.getData("END_DATE", k));
            insertBillmParm.setData("DISCHARGE_FLG",
                                    selBillm.getData("DISCHARGE_FLG", 0) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("DISCHARGE_FLG", 0));
            insertBillmParm.setData("DEPT_CODE",
                                    selBillm.getData("DEPT_CODE", k));
            insertBillmParm.setData("STATION_CODE",
                                    selBillm.getData("STATION_CODE", k));
            insertBillmParm.setData("REGION_CODE",
                                    stationInfo.getData("REGION_CODE", 0));
            insertBillmParm.setData("BED_NO", selBillm.getData("BED_NO", k));
            insertBillmParm.setData("OWN_AMT", selBillm.getDouble("OWN_AMT", k));
            insertBillmParm.setData("NHI_AMT", selBillm.getDouble("NHI_AMT", k));
            insertBillmParm.setData("APPROVE_FLG", "N");
            insertBillmParm.setData("REDUCE_REASON",
                                    selBillm.getData("REDUCE_REASON", k) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("REDUCE_REASON", k));
            insertBillmParm.setData("REDUCE_AMT",
                                    selBillm.getData("REDUCE_AMT", k));
            insertBillmParm.setData("REDUCE_DATE",
                                    selBillm.getData("REDUCE_DATE", k) == null ?
                                    new TNull(Timestamp.class) :
                                    selBillm.getData("REDUCE_DATE", k));
            insertBillmParm.setData("REDUCE_DEPT_CODE",
                                    selBillm.getData("REDUCE_DEPT_CODE", k) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("REDUCE_DEPT_CODE", k));
            insertBillmParm.setData("REDUCE_RESPOND",
                                    selBillm.getData("REDUCE_RESPOND", k) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("REDUCE_RESPOND", k));
            insertBillmParm.setData("AR_AMT", selBillm.getDouble("AR_AMT", k));
            insertBillmParm.setData("PAY_AR_AMT",
                                    selBillm.getDouble("PAY_AR_AMT", k));
            insertBillmParm.setData("CANDEBT_CODE",
                                    selBillm.getData("CANDEBT_CODE", k) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("CANDEBT_CODE", k));
            insertBillmParm.setData("CANDEBT_PERSON",
                                    selBillm.getData("CANDEBT_PERSON", k) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("CANDEBT_PERSON", k));
            insertBillmParm.setData("REFUND_CODE",
                                    selBillm.getData("REFUND_CODE", k) == null ?
                                    new TNull(String.class) :
                                    selBillm.getData("REFUND_CODE", k));
            insertBillmParm.setData("REFUND_DATE",
                                    selBillm.getData("REFUND_DATE", k) == null ?
                                    new TNull(Timestamp.class) :
                                    selBillm.getData("REFUND_DATE", k));
            insertBillmParm.setData("OPT_USER", optUser);
            insertBillmParm.setData("OPT_TERM", optTerm);
            result = IBSBillmTool.getInstance().insertdata(insertBillmParm,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }

            //�˵���ϸ������
            TParm selBilldParm = new TParm();
            selBilldParm.setData("BILL_NO", selBillm.getData("BILL_NO", k));
            selBilldParm.setData("BILL_SEQ", selBillm.getData("BILL_SEQ", k));
            TParm selBliid = IBSBilldTool.getInstance().selectAllData(
                    selBilldParm);
            TParm updateBliidParm = new TParm();
            updateBliidParm.setData("BILL_NO", selBillm.getData("BILL_NO", k));
            updateBliidParm.setData("REFUND_BILL_NO", "");
            updateBliidParm.setData("REFUND_CODE", optUser);
            result = IBSBilldTool.getInstance().updataDate(updateBliidParm,
                    connection);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                return result;
            }

            int billDCount = selBliid.getCount("BILL_NO");
            TParm insertBilldParm = new TParm();
            for (int i = 0; i < billDCount; i++) {
                insertBilldParm.setData("BILL_NO",
                                        selBliid.getData("BILL_NO", i));
                insertBilldParm.setData("BILL_SEQ",
                                        maxBillSeq + 1);
                insertBilldParm.setData("REXP_CODE",
                                        selBliid.getData("REXP_CODE", i));
                insertBilldParm.setData("OWN_AMT",
                                        -selBliid.getDouble("OWN_AMT", i));
                insertBilldParm.setData("AR_AMT",
                                        -selBliid.getDouble("AR_AMT", i));
                insertBilldParm.setData("PAY_AR_AMT",
                                        selBliid.getDouble("PAY_AR_AMT", i));
                insertBilldParm.setData("REFUND_BILL_NO",
                                        selBillm.getData("REFUND_BILL_NO", k) == null ?
                                        new TNull(String.class) :
                                        selBillm.getData("REFUND_BILL_NO", k));
                insertBilldParm.setData("REFUND_FLG",
                                        "Y");//caowl 20130424
                insertBilldParm.setData("REFUND_CODE",optUser);//caowl 20130424
                insertBilldParm.setData("REFUND_DATE",optDate);//caowl 20130424
                insertBilldParm.setData("OPT_USER",
                                        selBliid.getData("OPT_USER", i));
                insertBilldParm.setData("OPT_TERM",
                                        selBliid.getData("OPT_TERM", i));
                result = IBSBilldTool.getInstance().insertdata(insertBilldParm,
                        connection);
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    return result;
                }
            }
            for (int i = 0; i < billDCount; i++) {
                insertBilldParm.setData("BILL_NO",
                                        selBliid.getData("BILL_NO", i));
                insertBilldParm.setData("BILL_SEQ",
                                        maxBillSeq + 2);
                insertBilldParm.setData("REXP_CODE",
                                        selBliid.getData("REXP_CODE", i));
                insertBilldParm.setData("OWN_AMT",
                                        selBliid.getDouble("OWN_AMT", i));
                insertBilldParm.setData("AR_AMT",
                                        selBliid.getDouble("AR_AMT", i));
                insertBilldParm.setData("PAY_AR_AMT",
                                        selBliid.getDouble("PAY_AR_AMT", i));
                insertBilldParm.setData("REFUND_BILL_NO",
                                        selBillm.getData("REFUND_BILL_NO", k) == null ?
                                        new TNull(String.class) :
                                        selBillm.getData("REFUND_BILL_NO", k));
                insertBilldParm.setData("REFUND_FLG",
                                        selBliid.getData("REFUND_FLG", i));
                insertBilldParm.setData("REFUND_CODE",
                                        selBillm.getData("REFUND_CODE", k) == null ?
                                        new TNull(String.class) :
                                        selBillm.getData("REFUND_CODE", k));
                insertBilldParm.setData("REFUND_DATE",
                                        selBillm.getData("REFUND_DATE", k) == null ?
                                        new TNull(Timestamp.class) :
                                        selBillm.getData("REFUND_DATE", k));
                insertBilldParm.setData("OPT_USER",
                                        selBliid.getData("OPT_USER", i));
                insertBilldParm.setData("OPT_TERM",
                                        selBliid.getData("OPT_TERM", i));
                result = IBSBilldTool.getInstance().insertdata(insertBilldParm,
                        connection);
                if (result.getErrCode() < 0) {
                    err(result.getErrName() + " " + result.getErrText());
                    return result;
                }
            }
        }
        result.setData("PRE_AMT", arAmt);
        result.setData("PAY_TYPE",null==bilPay.getData("PAY_TYPE", 0)?"PAY_CASH":bilPay.getData("PAY_TYPE", 0));//�޸�����Ʊ�����ֽ�ʽ�س嵽Ԥ����====pangben 2014-8-4
        result.setData("RECEIPT_NO", bilPayrecpNo);
        result.setData("STATION_CODE", selBillm.getData("STATION_CODE", 0));
        result.setData("DEPT_CODE", selBillm.getData("DEPT_CODE", 0));
        result.setData("INV_NO", invNo);
        return result;
    }

    /**
     * סԺƱ�ݲ�ӡ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onIBSReprint(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String caseNo = parm.getValue("CASE_NO");
        String receiptNo = parm.getValue("RECEIPT_NO");
        TParm selInvoice = new TParm();
        selInvoice.setData("STATUS", "0");
        selInvoice.setData("RECP_TYPE", "IBS");
        selInvoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        selInvoice.setData("TERM_IP", parm.getData("OPT_TERM"));
        TParm invoice = BILInvoiceTool.getInstance().selectNowReceipt(
                selInvoice);
        String invNo = invoice.getValue("UPDATE_NO", 0);
//        System.out.println("Ʊ����������"+invoice);
        invoice.setData("UPDATE_NO", StringTool.addString(invNo));
        invoice.setData("RECP_TYPE", "IBS");
        invoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        invoice.setData("STATUS", "0");
        invoice.setData("START_INVNO", invoice.getData("START_INVNO", 0));

        //����Ʊ������
        result = BILInvoiceTool.getInstance().updateDatePrint(invoice,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        TParm oldDataRecpParm = new TParm();
        TParm selOldParm = new TParm();
        selOldParm.setData("CASE_NO", caseNo);
        selOldParm.setData("RECEIPT_NO", receiptNo);
        oldDataRecpParm = BILIBSRecpmTool.getInstance().selectAllData(
                selOldParm);
        TParm newDataRecpParm = new TParm();
        newDataRecpParm.setData("CASE_NO", caseNo);
        newDataRecpParm.setData("PRINT_NO", invNo);
        newDataRecpParm.setData("RECEIPT_NO", receiptNo);
        newDataRecpParm.setData("OPT_USER", parm.getData("OPT_USER"));
        newDataRecpParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
//        System.out.println("�����վݵ�����"+newDataRecpParm);
        //��ӡ,�����վݵ����վ�״̬(FOR REG)
        result = BILIBSRecpmTool.getInstance().upRecpForRePrint(
                newDataRecpParm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        TParm selInvrcp = new TParm();
        selInvrcp.setData("RECP_TYPE", "IBS");
        selInvrcp.setData("INV_NO", oldDataRecpParm.getData("PRINT_NO", 0));
        //��ѯԭƱ����ϸ��
        TParm oneInvParm = BILInvrcptTool.getInstance().getOneInv(selInvrcp);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        TParm invRcpParm = new TParm();
        invRcpParm.setData("CANCEL_FLG", "3");
        invRcpParm.setData("CANCEL_USER", parm.getData("OPT_USER"));
        invRcpParm.setData("OPT_USER", parm.getData("OPT_USER"));
        invRcpParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
        invRcpParm.setData("RECP_TYPE", "IBS");
        invRcpParm.setData("INV_NO", oneInvParm.getData("INV_NO", 0));
        //��ӡ,����Ʊ����ϸ��Ʊ��״̬
        result = BILInvrcptTool.getInstance().updataData(invRcpParm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //Ʊ����ϸ������һ��������
        TParm insertInvrcp = new TParm();
        insertInvrcp.setData("INV_NO", invNo);
        insertInvrcp.setData("RECP_TYPE", "IBS");
        insertInvrcp.setData("ADM_TYPE", "I");
        insertInvrcp.setData("RECEIPT_NO", receiptNo);
        insertInvrcp.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        insertInvrcp.setData("AR_AMT", oneInvParm.getValue("AR_AMT", 0));
        insertInvrcp.setData("CANCEL_FLG", "0");
        insertInvrcp.setData("STATUS", "0");
        insertInvrcp.setData("OPT_TERM", parm.getData("OPT_TERM"));
        insertInvrcp.setData("OPT_USER", parm.getData("OPT_USER"));
        result = BILInvrcptTool.getInstance().insertData(insertInvrcp,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;

    }

    /**
     * Ԥ����ӡ
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onBilPayReprint(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String caseNo = parm.getValue("CASE_NO");
        String receiptNo = parm.getValue("RECEIPT_NO");
        TParm selInvoice = new TParm();
        selInvoice.setData("STATUS", "0");
        selInvoice.setData("RECP_TYPE", "PAY");
        selInvoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        selInvoice.setData("TERM_IP", parm.getData("OPT_TERM"));
        TParm invoice = BILInvoiceTool.getInstance().selectNowReceipt(
                selInvoice);
        String invNo = invoice.getValue("UPDATE_NO", 0);
        String startInvNo = invoice.getValue("START_INVNO", 0);
//        System.out.println("Ʊ����������"+invoice);
        invoice.setData("UPDATE_NO", StringTool.addString(invNo));
        invoice.setData("RECP_TYPE", "PAY");
        invoice.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        invoice.setData("STATUS", "0");
        invoice.setData("START_INVNO", startInvNo);

        //����Ʊ������
        result = BILInvoiceTool.getInstance().updateDatePrint(invoice,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        TParm oldDataRecpParm = new TParm();
        TParm selOldParm = new TParm();
        selOldParm.setData("CASE_NO", caseNo);
        selOldParm.setData("RECEIPT_NO", receiptNo);
        //System.out.println("��Ԥ�������"+selOldParm);
        oldDataRecpParm = BILPayTool.getInstance().selectAllData(selOldParm);
//        System.out.println("��Ԥ��������"+oldDataRecpParm);
        TParm newDataRecpParm = new TParm();
        newDataRecpParm.setData("PRINT_NO", invNo);
        newDataRecpParm.setData("RECEIPT_NO", receiptNo);
        newDataRecpParm.setData("OPT_USER", parm.getData("OPT_USER"));
        newDataRecpParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
//        System.out.println("�����վݵ�����"+newDataRecpParm);
        //��ӡ,�����վݵ����վ�״̬(FOR REG)
        result = BILPayTool.getInstance().upRecpForRePrint(
                newDataRecpParm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        TParm selInvrcp = new TParm();
        selInvrcp.setData("RECP_TYPE", "PAY");
        selInvrcp.setData("INV_NO", oldDataRecpParm.getData("PRINT_NO", 0));
        //��ѯԭƱ����ϸ��
        TParm oneInvParm = BILInvrcptTool.getInstance().getOneInv(selInvrcp);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        TParm invRcpParm = new TParm();
        invRcpParm.setData("CANCEL_FLG", "3");
        invRcpParm.setData("CANCEL_USER", parm.getData("OPT_USER"));
        invRcpParm.setData("OPT_USER", parm.getData("OPT_USER"));
        invRcpParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
        invRcpParm.setData("RECP_TYPE", "PAY");
        invRcpParm.setData("INV_NO", oneInvParm.getData("INV_NO", 0));
        //��ӡ,����Ʊ����ϸ��Ʊ��״̬
        result = BILInvrcptTool.getInstance().updataData(invRcpParm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        //Ʊ����ϸ������һ��������
        TParm insertInvrcp = new TParm();
        insertInvrcp.setData("INV_NO", invNo);
        insertInvrcp.setData("RECP_TYPE", "PAY");
        insertInvrcp.setData("ADM_TYPE", "I");
        insertInvrcp.setData("RECEIPT_NO", receiptNo);
        insertInvrcp.setData("CASHIER_CODE", parm.getData("OPT_USER"));
        insertInvrcp.setData("AR_AMT", oneInvParm.getValue("AR_AMT", 0));
        insertInvrcp.setData("CANCEL_FLG", "0");
        insertInvrcp.setData("STATUS", "0");
        insertInvrcp.setData("OPT_TERM", parm.getData("OPT_TERM"));
        insertInvrcp.setData("OPT_USER", parm.getData("OPT_USER"));
        result = BILInvrcptTool.getInstance().insertData(insertInvrcp,
                connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;

    }

    /**
     * �жϵ�ǰƱ���Ƿ���ڽ���Ʊ��=====zhangp 20120306
     * @param recpType String
     * @param opratorId String
     * @param regionCode String
     * @param updateNo String
     * @return boolean
     */
    public boolean compareUpdateNo(String recpType, String opratorId,
                                   String regionCode, String updateNo) {
        String sql =
                "SELECT END_INVNO FROM BIL_INVOICE " +
                " WHERE RECP_TYPE = '" + recpType + "' AND CASHIER_CODE = '" +
                opratorId + "' " +
                " AND REGION_CODE = '" + regionCode + "' AND STATUS = '0'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return false;
        }
        //====zhangp 20120307 modify start
        if (result.getCount() <= 0) {
            return false;
        }
        //====zhangp 20120307 modify end
        if (updateNo.compareTo(result.getData("END_INVNO", 0).toString()) > 0) {
            return false;
        }
        return true;
    }

    /**
     * ����������Ʊ�ݵ�accountSeq===zhangp 20120327
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateRestInvrcp(TParm parm, TConnection connection) {
        parm = parm.getParm("ACCOUNT");
        String billDate = parm.getData("ACCOUNT_DATA").toString();
        String sql =
                "SELECT PRINT_NO FROM BIL_OPB_RECP WHERE AR_AMT < 0 AND (ACCOUNT_FLG IS NULL OR ACCOUNT_FLG = 'N') " +
                " AND ADM_TYPE = '" + parm.getData("ADM_TYPE") +
                "' AND CASHIER_CODE = '" + parm.getData("ACCOUNT_USER") + "' " +
                " AND BILL_DATE < TO_DATE('" + billDate +
                "','yyyyMMddHH24miss') " +
                " AND RESET_RECEIPT_NO IS NULL";
        TParm temp = new TParm(TJDODBTool.getInstance().select(sql));
        if (temp.getErrCode() < 0) {
            return temp;
        }
        if (temp.getCount() < 0) {
            return temp;
        }
        String printNos = "";
        for (int i = 0; i < temp.getCount(); i++) {
            printNos += ",'" + temp.getData("PRINT_NO", i) + "'";
        }
        printNos = printNos.substring(1, printNos.length());
        String accntDate = parm.getData("ACCOUNT_DATA").toString();
//		accntDate = accntDate.substring(0, 4) + accntDate.substring(5, 7) + accntDate.substring(8, 10) +
//					accntDate.substring(11, 13) + accntDate.substring(14, 16) + accntDate.substring(17, 19);
        sql =
                "UPDATE BIL_INVRCP SET ACCOUNT_SEQ = '" +
                parm.getData("ACCOUNT_SEQ") + "',ACCOUNT_USER = '" +
                parm.getData("ACCOUNT_USER") +
                "',ACCOUNT_DATE = TO_DATE('" + accntDate +
                "','yyyyMMddHH24miss') WHERE INV_NO IN (" + printNos +
                ") AND RECP_TYPE = 'OPB'";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        return result;
    }
    /**
     * ��Ժ���ɼƷ�
     * @param caseNo String
     * @return boolean
     */
    public boolean checkRecp(String caseNo) {
        TParm admParm = new TParm();
        admParm.setData("CASE_NO", caseNo);
        TParm selAdmParm = ADMInpTool.getInstance().selectall(admParm);
        String billStatus = selAdmParm.getValue("BILL_STATUS", 0);
        //��Ժ�ѽ���״̬���˵������
        if ("4".equals(billStatus) || "3".equals(billStatus)) {
            return false;
        }
        return true;
    }
    /**
     * ���¸������ϣ���ӡ���շ��ս�
     * ====zhangp 20120617
     * @param parm
     * @param connection
     * @return
     */
    public TParm updateReprintInvrcp(TParm parm, TConnection connection){
//    	System.out.println("���¸������ϣ���ӡ���շ��ս�");
    	parm = parm.getParm("ACCOUNT");
    	String billDate = parm.getData("ACCOUNT_DATA").toString();
    	String date = billDate.substring(0, 8) + "000000";
    	String sql = 
    		" SELECT INV_NO" +
    		" FROM BIL_INVRCP" +
    		" WHERE RECP_TYPE = 'OPB'" +
//    		" AND CANCEL_DATE < TO_DATE ('" + date + "', 'YYYYMMDDHH24MISS')" +
    		" AND CANCEL_USER = '" + parm.getData("ACCOUNT_USER") + "'" +
//    		" AND CANCEL_FLG = '3'" +
    		" AND CANCEL_FLG IN ('1','3')" +
    		" AND ADM_TYPE = '" + parm.getData("ADM_TYPE") + "'" +
    		" AND ACCOUNT_SEQ <> '" + parm.getData("ACCOUNT_SEQ") + "'" +
    		" AND ACCOUNT_DATE < TO_DATE ('" + date + "', 'YYYYMMDDHH24MISS')" +
    		" AND ACCOUNT_DATE < CANCEL_DATE";
//    	System.out.println("���¸������ϣ���ӡ���շ��ս�sql==="+sql);
        TParm temp = new TParm(TJDODBTool.getInstance().select(sql));
        if (temp.getErrCode() < 0) {
            return temp;
        }
        if (temp.getCount() < 0) {
            return temp;
        }
        String printNos = "";
        for (int i = 0; i < temp.getCount(); i++) {
            printNos += ",'" + temp.getData("INV_NO", i) + "'";
        }
        printNos = printNos.substring(1, printNos.length());
        sql =
            "UPDATE BIL_INVRCP SET ACCOUNT_SEQ = '" +
            parm.getData("ACCOUNT_SEQ") + "',ACCOUNT_USER = '" +
            parm.getData("ACCOUNT_USER") +
            "',ACCOUNT_DATE = TO_DATE('" + billDate +
            "','yyyyMMddHH24miss') WHERE INV_NO IN (" + printNos +
            ") AND RECP_TYPE = 'OPB'";
//        System.out.println("���¸������ϣ���ӡ���շ��ս�updatesql==="+sql);
    TParm result = new TParm(TJDODBTool.getInstance().update(sql,
            connection));
    return result;
    }
    
    /**
     * �ս��շ�Ա�������ϵ������˵�Ʊ�ݣ������ս����
     * ====zhangp 20120617
     * @param parm
     * @param connection
     * @return
     */
    public TParm updateReprintInvrcpOneDay(TParm parm, TConnection connection){
    	parm = parm.getParm("ACCOUNT");
    	String billDate = parm.getData("ACCOUNT_DATA").toString();
    	String accSql = 
    		" SELECT   ACCOUNT_DATE" +
    		" FROM BIL_ACCOUNT" +
    		" WHERE ACCOUNT_TYPE = 'OPB' AND ADM_TYPE = '" + parm.getData("ADM_TYPE") + "' AND ACCOUNT_USER = '" + parm.getData("ACCOUNT_USER") + "'" +
    		" ORDER BY ACCOUNT_DATE DESC";
    	System.out.println("accSql="+accSql);
    	TParm accParm = new TParm(TJDODBTool.getInstance().select(accSql));
    	String startDate = "";
    	if(accParm.getCount()<0){
    		startDate = "20110101000000";//��ʱд��
    	}else{
    		startDate = billDate = accParm.getData("ACCOUNT_DATE").toString().substring(0, 8) + "000000";
    	}
    	String sql = 
    		" SELECT INV_NO" +
    		" FROM BIL_INVRCP" +
    		" WHERE CANCEL_USER = '" + parm.getData("ACCOUNT_USER") + "'" +
    		" AND RECP_TYPE = 'OPB'" +
    		" AND ADM_TYPE = '" + parm.getData("ADM_TYPE") + "'" +
    		" AND CANCEL_FLG IN (1, 3)" +
    		" AND CANCEL_USER <> CASHIER_CODE" +
    		" AND CANCEL_DATE BETWEEN TO_DATE ('"+startDate+"', 'YYYYMMDDHH24MISS')" +
    		" AND TO_DATE ('"+billDate+"', 'YYYYMMDDHH24MISS')";
    	System.out.println("fdf=="+sql);
        TParm temp = new TParm(TJDODBTool.getInstance().select(sql));
        if (temp.getErrCode() < 0) {
            return temp;
        }
        if (temp.getCount() < 0) {
            return temp;
        }
        String printNos = "";
        for (int i = 0; i < temp.getCount(); i++) {
            printNos += ",'" + temp.getData("INV_NO", i) + "'";
        }
        printNos = printNos.substring(1, printNos.length());
        sql =
            "UPDATE BIL_INVRCP SET ACCOUNT_SEQ = '" +
            parm.getData("ACCOUNT_SEQ") + "',ACCOUNT_USER = '" +
            parm.getData("ACCOUNT_USER") +
            "',ACCOUNT_DATE = TO_DATE('" + billDate +
            "','yyyyMMddHH24miss') WHERE INV_NO IN (" + printNos +
            ") AND RECP_TYPE = 'OPB'";
    TParm result = new TParm(TJDODBTool.getInstance().update(sql,
            connection));
    return result;
    }
}
