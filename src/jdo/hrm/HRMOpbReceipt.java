package jdo.hrm;

import java.sql.Timestamp;

import jdo.bil.BIL;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
 * <p>Title: 健康检查票据详细</p>
 *
 * <p>Description: 健康检查票据详细</p>
 *
 * <p>Copyright: javahis 20090922</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMOpbReceipt extends TDataStore {
    // 初始化SQL
    private static final String INIT = "SELECT * FROM BIL_OPB_RECP WHERE CASE_NO='#'";
    //根据CASE_NO,RECEIPT_NO查询
    private static final String INIT_BY_CASE_RECEIPT="SELECT * FROM BIL_OPB_RECP WHERE CASE_NO='#' AND RECEIPT_NO='#'";
    //根据RECEIPT_NO查询
    private static final String INIT_BY_RECEIPT="SELECT * FROM BIL_OPB_RECP WHERE RECEIPT_NO='#'";
    private String id = Operator.getID();
    private String ip = Operator.getIP();
    /**
     * 查询事件
     *
     * @return
     */
    public int onQuery() {
        this.setSQL(INIT);
        return this.retrieve();
    }
    /**
     * 根据RECEIPT_NO初始化
     * @param receiptNo
     * @return
     */
    public boolean onQueryByReceiptNo(String receiptNo){
        if(StringUtil.isNullString(receiptNo)){
            return false;
        }
        this.setSQL(this.INIT_BY_RECEIPT.replaceFirst("#", receiptNo));
        this.retrieve();
        return true;
    }
    /**
     * 新增一行
     * @param parm
     * @return
     */
    public boolean insert(TParm parm,int mark){
        if(parm==null){
            return false;
        }
        BIL bil =new BIL();
        TParm orderParm=(TParm)parm.getData("ORDER_PARM");
        // System.out.println("orderParm==="+orderParm);
        TParm chargeParm=bil.getChargeFeeParm(orderParm);
        // System.out.println("orderParm.rexp"+orderParm.getValue("REXP_CODE",0));
        // System.out.println("chargeParm==="+chargeParm);
        if(chargeParm==null){
            return false;
        }
        double totAmt=this.getTotAmt(chargeParm);
        int row=this.insertRow();

        //CASE_NO
        this.setItem(row, "CASE_NO", parm.getValue("CASE_NO"));
        //RECEIPT_NO
        //取号原则得到票据号
        String receiptNo=SystemTool.getInstance().getNo("ALL", "OPB", "RECEIPT_NO",
              "RECEIPT_NO");
        if(StringUtil.isNullString(receiptNo)){
            return false;
        }
        this.setItem(row, "RECEIPT_NO", receiptNo);
        //ADM_TYPE
        this.setItem(row, "ADM_TYPE", "H");
        //REGION_CODE
        this.setItem(row, "REGION_CODE", Operator.getRegion());
        //MR_NO
        this.setItem(row, "MR_NO", parm.getValue("MR_NO"));
        //RESET_RECEIPT_NO
        //PRINT_NO
        this.setItem(row, "PRINT_NO", parm.getValue("PRINT_NO"));
        //BILL_DATE
        Timestamp now=this.getDBTime();
        this.setItem(row, "BILL_DATE", now);
        //CHARGE_DATE
//      this.setItem(row, "CHARGE_DATE", now);
        //PRINT_DATE
        this.setItem(row, "PRINT_DATE", now);
        //CHARGE01
        this.setItem(row, "CHARGE01", chargeParm.getDouble("CHARGE01")*mark);
        //CHARGE02
        this.setItem(row, "CHARGE02", chargeParm.getDouble("CHARGE02")*mark);
        //CHARGE03
        this.setItem(row, "CHARGE03", chargeParm.getDouble("CHARGE03")*mark);
        //CHARGE04
        this.setItem(row, "CHARGE04", chargeParm.getDouble("CHARGE04")*mark);
        //CHARGE05
        this.setItem(row, "CHARGE05", chargeParm.getDouble("CHARGE05")*mark);
        //CHARGE06
        this.setItem(row, "CHARGE06", chargeParm.getDouble("CHARGE06")*mark);
        //CHARGE07
        this.setItem(row, "CHARGE07", chargeParm.getDouble("CHARGE07")*mark);
        //CHARGE08
        this.setItem(row, "CHARGE08", chargeParm.getDouble("CHARGE08")*mark);
        //CHARGE09
        this.setItem(row, "CHARGE09", chargeParm.getDouble("CHARGE09")*mark);
        //CHARGE10
        this.setItem(row, "CHARGE10", chargeParm.getDouble("CHARGE10")*mark);
        //CHARGE11
        this.setItem(row, "CHARGE11", chargeParm.getDouble("CHARGE11")*mark);
        //CHARGE12
        this.setItem(row, "CHARGE12", chargeParm.getDouble("CHARGE12")*mark);
        //CHARGE13
        this.setItem(row, "CHARGE13", chargeParm.getDouble("CHARGE13")*mark);
        //CHARGE14
        this.setItem(row, "CHARGE14", chargeParm.getDouble("CHARGE14")*mark);
        //CHARGE15
        this.setItem(row, "CHARGE15", chargeParm.getDouble("CHARGE15")*mark);
        //CHARGE16
        this.setItem(row, "CHARGE16", chargeParm.getDouble("CHARGE16")*mark);
        //CHARGE17
        this.setItem(row, "CHARGE17", chargeParm.getDouble("CHARGE17")*mark);
        //CHARGE18
        this.setItem(row, "CHARGE18", chargeParm.getDouble("CHARGE18")*mark);
        //CHARGE19
        this.setItem(row, "CHARGE19", chargeParm.getDouble("CHARGE19")*mark);
        //CHARGE20
        this.setItem(row, "CHARGE20", chargeParm.getDouble("CHARGE20")*mark);
        //CHARGE21
        this.setItem(row, "CHARGE21", chargeParm.getDouble("CHARGE21")*mark);
        //CHARGE22
        this.setItem(row, "CHARGE22", chargeParm.getDouble("CHARGE22")*mark);
        //CHARGE23
        this.setItem(row, "CHARGE23", chargeParm.getDouble("CHARGE23")*mark);
        //CHARGE24
        this.setItem(row, "CHARGE24", chargeParm.getDouble("CHARGE24")*mark);
        //CHARGE25
        this.setItem(row, "CHARGE25", chargeParm.getDouble("CHARGE25")*mark);
        //CHARGE26
        this.setItem(row, "CHARGE26", chargeParm.getDouble("CHARGE26")*mark);
        //CHARGE27
        this.setItem(row, "CHARGE27", chargeParm.getDouble("CHARGE27")*mark);
        //CHARGE28
        this.setItem(row, "CHARGE28", chargeParm.getDouble("CHARGE28")*mark);
        //CHARGE29
        this.setItem(row, "CHARGE29", chargeParm.getDouble("CHARGE29")*mark);
        //CHARGE30
        this.setItem(row, "CHARGE30", chargeParm.getDouble("CHARGE30")*mark);
        //TOT_AMT
        this.setItem(row, "TOT_AMT", totAmt*mark);
        //REDUCE_REASON
        //REDUCE_AMT
        //REDUCE_DATE
        //REDUCE_DEPT_CODE
        //REDUCE_RESPOND
        //AR_AMT
        this.setItem(row, "AR_AMT", totAmt*mark);
        String payType=parm.getValue("PAY_TYPE");
        this.setItem(row, payType, totAmt);
        //PAY_CASH
                if("PAY_BANK_CARD".equals(payType)){
                    this.setItem(row, "PAY_CASH", 0);
                }else{
                    this.setItem(row, "PAY_CASH", totAmt*mark);
                }
        //PAY_MEDICAL_CARD
        this.setItem(row, "PAY_MEDICAL_CARD", 0.00);
        //PAY_BANK_CARD
        this.setItem(row, "PAY_BANK_CARD", 0.00);
        //PAY_INS_CARD
        this.setItem(row, "PAY_INS_CARD", 0.00);
        //PAY_CHECK
        this.setItem(row, "PAY_CHECK", 0.00);
        //PAY_DEBIT
        this.setItem(row, "PAY_DEBIT", 0.00);
        //PAY_BILPAY
        this.setItem(row, "PAY_BILPAY", 0.00);
        //PAY_INS
        this.setItem(row, "PAY_INS", 0.00);
        //PAY_OTHER1
        this.setItem(row, "PAY_OTHER1", 0.00);
        //PAY_OTHER2
        this.setItem(row, "PAY_OTHER2", 0.00);
        this.setItem(row, payType, totAmt*mark);
        //============xueyf modify 20120308 start
        //PAY_REMARK
        this.setItem(row, "PAY_REMARK", parm.getValue("PAY_REMARK"));
        //============xueyf modify 20120308 start
        //CONTRACT_CODE
        this.setItem(row, "CONTRACT_CODE", parm.getValue("CONTRACT_CODE"));
        //CASHIER_CODE
        this.setItem(row, "CASHIER_CODE", id);
        //CANCEL_FLG
        this.setItem(row, "CANCEL_FLG", 0);
        //============xueyf modify 20120222 start
        this.setItem(row, "PRINT_USER", id);
        //============xueyf modify 20120222 start
        //xueyf
        //ACCOUNT_FLG
        //ACCOUNT_SEQ
        //ACCOUNT_USER
        //ACCOUNT_DATE
        //BANK_SEQ
        //BANK_USER
        //BANK_DATE
        //OPT_USER
        this.setItem(row, "OPT_USER", id);
        //OPT_DATE
        this.setItem(row, "OPT_DATE", now);
        //OPT_TERM
        this.setItem(row, "OPT_TERM", ip);
        this.setActive(row,true);
        return true;
    }
    /**
     * 新增一行
     * @param parm
     * @return
     */
    public boolean insert(TParm parm,String receiptNo){
        if(parm==null){
            return false;
        }
        if(StringUtil.isNullString(receiptNo)){
            return false;
        }
        BIL bil =new BIL();
        TParm chargeParm=bil.getChargeFeeParm(parm);
        if(chargeParm==null){
            return false;
        }
        double totAmt=this.getTotAmt(chargeParm);
        int row=this.insertRow();

        //CASE_NO
        this.setItem(row, "CASE_NO", parm.getValue("CASE_NO"));
        //RECEIPT_NO

        this.setItem(row, "RECEIPT_NO", receiptNo);
        //ADM_TYPE
        this.setItem(row, "ADM_TYPE", "H");
        //REGION_CODE
        this.setItem(row, "REGION_CODE", Operator.getRegion());
        //MR_NO
        this.setItem(row, "MR_NO", parm.getValue("MR_NO"));
        //RESET_RECEIPT_NO
        //PRINT_NO
        this.setItem(row, "PRINT_NO", parm.getValue("PRINT_NO"));
        //BILL_DATE
        Timestamp now=this.getDBTime();
        this.setItem(row, "BILL_DATE", now);
        //CHARGE_DATE
        this.setItem(row, "CHARGE_DATE", now);
        //PRINT_DATE
        this.setItem(row, "PRINT_DATE", now);
        //CHARGE01
        this.setItem(row, "CHARGE01", chargeParm.getDouble("CHARGE01"));
        //CHARGE02
        this.setItem(row, "CHARGE02", chargeParm.getDouble("CHARGE02"));
        //CHARGE03
        this.setItem(row, "CHARGE03", chargeParm.getDouble("CHARGE03"));
        //CHARGE04
        this.setItem(row, "CHARGE04", chargeParm.getDouble("CHARGE04"));
        //CHARGE05
        this.setItem(row, "CHARGE05", chargeParm.getDouble("CHARGE05"));
        //CHARGE06
        this.setItem(row, "CHARGE06", chargeParm.getDouble("CHARGE06"));
        //CHARGE07
        this.setItem(row, "CHARGE07", chargeParm.getDouble("CHARGE07"));
        //CHARGE08
        this.setItem(row, "CHARGE08", chargeParm.getDouble("CHARGE08"));
        //CHARGE09
        this.setItem(row, "CHARGE09", chargeParm.getDouble("CHARGE09"));
        //CHARGE10
        this.setItem(row, "CHARGE10", chargeParm.getDouble("CHARGE10"));
        //CHARGE11
        this.setItem(row, "CHARGE11", chargeParm.getDouble("CHARGE11"));
        //CHARGE12
        this.setItem(row, "CHARGE12", chargeParm.getDouble("CHARGE12"));
        //CHARGE13
        this.setItem(row, "CHARGE13", chargeParm.getDouble("CHARGE13"));
        //CHARGE14
        this.setItem(row, "CHARGE14", chargeParm.getDouble("CHARGE14"));
        //CHARGE15
        this.setItem(row, "CHARGE15", chargeParm.getDouble("CHARGE15"));
        //CHARGE16
        this.setItem(row, "CHARGE16", chargeParm.getDouble("CHARGE16"));
        //CHARGE17
        this.setItem(row, "CHARGE17", chargeParm.getDouble("CHARGE17"));
        //CHARGE18
        this.setItem(row, "CHARGE18", chargeParm.getDouble("CHARGE18"));
        //CHARGE19
        this.setItem(row, "CHARGE19", chargeParm.getDouble("CHARGE19"));
        //CHARGE20
        this.setItem(row, "CHARGE20", chargeParm.getDouble("CHARGE20"));
        //CHARGE21
        this.setItem(row, "CHARGE21", chargeParm.getDouble("CHARGE21"));
        //CHARGE22
        this.setItem(row, "CHARGE22", chargeParm.getDouble("CHARGE22"));
        //CHARGE23
        this.setItem(row, "CHARGE23", chargeParm.getDouble("CHARGE23"));
        //CHARGE24
        this.setItem(row, "CHARGE24", chargeParm.getDouble("CHARGE24"));
        //CHARGE25
        this.setItem(row, "CHARGE25", chargeParm.getDouble("CHARGE25"));
        //CHARGE26
        this.setItem(row, "CHARGE26", chargeParm.getDouble("CHARGE26"));
        //CHARGE27
        this.setItem(row, "CHARGE27", chargeParm.getDouble("CHARGE27"));
        //CHARGE28
        this.setItem(row, "CHARGE28", chargeParm.getDouble("CHARGE28"));
        //CHARGE29
        this.setItem(row, "CHARGE29", chargeParm.getDouble("CHARGE29"));
        //CHARGE30
        this.setItem(row, "CHARGE30", chargeParm.getDouble("CHARGE30"));
        //TOT_AMT
        this.setItem(row, "TOT_AMT", totAmt);
        //REDUCE_REASON
        //REDUCE_AMT
        //REDUCE_DATE
        //REDUCE_DEPT_CODE
        //REDUCE_RESPOND
        //AR_AMT
        this.setItem(row, "AR_AMT", totAmt);
        //PAY_CASH
        this.setItem(row, "PAY_CASH", totAmt);
        //PAY_MEDICAL_CARD
        this.setItem(row, "PAY_MEDICAL_CARD", 0.00);
        //PAY_BANK_CARD
        this.setItem(row, "PAY_BANK_CARD", 0.00);
        //PAY_INS_CARD
        this.setItem(row, "PAY_INS_CARD", 0.00);
        //PAY_CHECK
        this.setItem(row, "PAY_CHECK", 0.00);
        //PAY_DEBIT
        this.setItem(row, "PAY_DEBIT", 0.00);
        //PAY_BILPAY
        this.setItem(row, "PAY_BILPAY", 0.00);
        //PAY_INS
        this.setItem(row, "PAY_INS", 0.00);
        //PAY_OTHER1
        this.setItem(row, "PAY_OTHER1", 0.00);
        //PAY_OTHER2
        this.setItem(row, "PAY_OTHER2", 0.00);
        //CONTRACT_CODE
        this.setItem(row, "CONTRACT_CODE", parm.getValue("CONTRACT_CODE"));
        //CASHIER_CODE
        this.setItem(row, "CASHIER_CODE", id);
        //ACCOUNT_FLG
        //ACCOUNT_SEQ
        //ACCOUNT_USER
        //ACCOUNT_DATE
        //BANK_SEQ
        //BANK_USER
        //BANK_DATE
        //OPT_USER
        this.setItem(row, "OPT_USER", id);
        //OPT_DATE
        this.setItem(row, "OPT_DATE", now);
        //OPT_TERM
        this.setItem(row, "OPT_TERM", ip);
        this.setActive(row,true);
        return true;
    }
    /**
     * 新增一行
     * @param parm
     * @return
     */
    public boolean insertForBill(TParm parm,String receiptNo,String caseNo,String mrNo,String printNo,String recpType,String payRemark){
        if(parm==null){
            return false;
        }
        if(StringUtil.isNullString(receiptNo)){
            return false;
        }
        BIL bil =new BIL();
        
        //总价TParm
        TParm chargeParm=bil.getChargeFeeParm(parm);
        if(chargeParm==null){
            return false;
        }
        
        //折扣后总价TParm
        TParm chareAramTParm = bil.getChargeFeeArAmtParm(parm);
        if(chareAramTParm == null ){
            return false;
        }
        
        //总价
        double totAmt=this.getTotAmt(chargeParm);
        
        //折扣后总价
        double totArAmt = this.getTotArAmt(chareAramTParm);
        
        int row=this.insertRow();

        //CASE_NO
        this.setItem(row, "CASE_NO", caseNo);
        //RECEIPT_NO

        this.setItem(row, "RECEIPT_NO", receiptNo);
        //ADM_TYPE
        this.setItem(row, "ADM_TYPE", "H");
        //REGION_CODE
        this.setItem(row, "REGION_CODE", Operator.getRegion());
        //MR_NO
        this.setItem(row, "MR_NO", mrNo);
        //RESET_RECEIPT_NO
        //PRINT_NO
        this.setItem(row, "PRINT_NO", printNo);
        //BILL_DATE
        Timestamp now=this.getDBTime();
        this.setItem(row, "BILL_DATE", now);
        //CHARGE_DATE
        this.setItem(row, "CHARGE_DATE", now);
        //PRINT_DATE
        this.setItem(row, "PRINT_DATE", now);
        //CHARGE01
        this.setItem(row, "CHARGE01", chareAramTParm.getDouble("CHARGE01"));
        //CHARGE02
        this.setItem(row, "CHARGE02", chareAramTParm.getDouble("CHARGE02"));
        //CHARGE03
        this.setItem(row, "CHARGE03", chareAramTParm.getDouble("CHARGE03"));
        //CHARGE04
        this.setItem(row, "CHARGE04", chareAramTParm.getDouble("CHARGE04"));
        //CHARGE05
        this.setItem(row, "CHARGE05", chareAramTParm.getDouble("CHARGE05"));
        //CHARGE06
        this.setItem(row, "CHARGE06", chareAramTParm.getDouble("CHARGE06"));
        //CHARGE07
        this.setItem(row, "CHARGE07", chareAramTParm.getDouble("CHARGE07"));
        //CHARGE08
        this.setItem(row, "CHARGE08", chareAramTParm.getDouble("CHARGE08"));
        //CHARGE09
        this.setItem(row, "CHARGE09", chareAramTParm.getDouble("CHARGE09"));
        //CHARGE10
        this.setItem(row, "CHARGE10", chareAramTParm.getDouble("CHARGE10"));
        //CHARGE11
        this.setItem(row, "CHARGE11", chareAramTParm.getDouble("CHARGE11"));
        //CHARGE12
        this.setItem(row, "CHARGE12", chareAramTParm.getDouble("CHARGE12"));
        //CHARGE13
        this.setItem(row, "CHARGE13", chareAramTParm.getDouble("CHARGE13"));
        //CHARGE14
        this.setItem(row, "CHARGE14", chareAramTParm.getDouble("CHARGE14"));
        //CHARGE15
        this.setItem(row, "CHARGE15", chareAramTParm.getDouble("CHARGE15"));
        //CHARGE16
        this.setItem(row, "CHARGE16", chareAramTParm.getDouble("CHARGE16"));
        //CHARGE17
        this.setItem(row, "CHARGE17", chareAramTParm.getDouble("CHARGE17"));
        //CHARGE18
        this.setItem(row, "CHARGE18", chareAramTParm.getDouble("CHARGE18"));
        //CHARGE19
        this.setItem(row, "CHARGE19", chareAramTParm.getDouble("CHARGE19"));
        //CHARGE20
        this.setItem(row, "CHARGE20", chareAramTParm.getDouble("CHARGE20"));
        //CHARGE21
        this.setItem(row, "CHARGE21", chareAramTParm.getDouble("CHARGE21"));
        //CHARGE22
        this.setItem(row, "CHARGE22", chareAramTParm.getDouble("CHARGE22"));
        //CHARGE23
        this.setItem(row, "CHARGE23", chareAramTParm.getDouble("CHARGE23"));
        //CHARGE24
        this.setItem(row, "CHARGE24", chareAramTParm.getDouble("CHARGE24"));
        //CHARGE25
        this.setItem(row, "CHARGE25", chareAramTParm.getDouble("CHARGE25"));
        //CHARGE26
        this.setItem(row, "CHARGE26", chareAramTParm.getDouble("CHARGE26"));
        //CHARGE27
        this.setItem(row, "CHARGE27", chareAramTParm.getDouble("CHARGE27"));
        //CHARGE28
        this.setItem(row, "CHARGE28", chareAramTParm.getDouble("CHARGE28"));
        //CHARGE29
        this.setItem(row, "CHARGE29", chareAramTParm.getDouble("CHARGE29"));
        //CHARGE30
        this.setItem(row, "CHARGE30", chareAramTParm.getDouble("CHARGE30"));
        //TOT_AMT
        this.setItem(row, "TOT_AMT", totAmt);
        //REDUCE_REASON
        //REDUCE_AMT
        //REDUCE_DATE
        //REDUCE_DEPT_CODE
        //REDUCE_RESPOND
        //AR_AMT
        //============xueyf modify 20120308 start
        this.setItem(row, "PAY_REMARK", payRemark);
        //============xueyf modify 20120308 stop
        this.setItem(row, "AR_AMT", totArAmt);
        // PAY_CASH
        if ("PAY_CASH".equals(recpType)) this.setItem(row, "PAY_CASH", totArAmt);
        else this.setItem(row, "PAY_CASH", 0.00);
        // PAY_MEDICAL_CARD
        if ("PAY_MEDICAL_CARD".equals(recpType)) this.setItem(row, "PAY_MEDICAL_CARD", totArAmt);
        else this.setItem(row, "PAY_MEDICAL_CARD", 0.00);
        // PAY_BANK_CARD
        if ("PAY_BANK_CARD".equals(recpType)) this.setItem(row, "PAY_BANK_CARD", totArAmt);
        else this.setItem(row, "PAY_BANK_CARD", 0.00);
        //PAY_INS_CARD
        this.setItem(row, "PAY_INS_CARD", 0.00);
        //PAY_CHECK
        if ("PAY_CHECK".equals(recpType)) this.setItem(row, "PAY_CHECK", totArAmt);
        else this.setItem(row, "PAY_CHECK", 0.00);
        // PAY_DEBIT记账
        if ("PAY_DEBIT".equals(recpType)) this.setItem(row, "PAY_DEBIT", totArAmt);
        else this.setItem(row, "PAY_DEBIT", 0.00);
        // 汇票
        if ("PAY_DRAFT".equals(recpType)) {
            this.setItem(row, "PAY_DRAFT", totArAmt);
        } else {
            this.setItem(row, "PAY_DRAFT", 0.00);
        }
        //PAY_BILPAY
        this.setItem(row, "PAY_BILPAY", 0.00);
        //PAY_INS
        this.setItem(row, "PAY_INS", 0.00);
        //PAY_OTHER1
        this.setItem(row, "PAY_OTHER1", 0.00);
        //PAY_OTHER2
        this.setItem(row, "PAY_OTHER2", 0.00);
        //CASHIER_CODE
        this.setItem(row, "CASHIER_CODE", id);
        //ACCOUNT_FLG
        //ACCOUNT_SEQ
        //ACCOUNT_USER
        //ACCOUNT_DATE
        //BANK_SEQ
        //BANK_USER
        //BANK_DATE
        //OPT_USER
        this.setItem(row, "OPT_USER", id);
        //OPT_DATE
        this.setItem(row, "OPT_DATE", now);
        //OPT_TERM
        this.setItem(row, "OPT_TERM", ip);
        this.setActive(row,true);
        return true;
    }
    /**
     * 取得charge01-charge30的费用总和(原价)
     * @param parm
     * @return
     */
    public double getTotAmt(TParm parm){
        double totAmt=0.00;
        if(parm==null){
            return totAmt;
        }
        String[] names=parm.getNames();
        for(int i=0;i<names.length;i++){
            totAmt+=parm.getDouble(names[i]);
        }
        return totAmt;
    }
    
    /**
     * 取得charge01-charge30的费用总和(折扣 )
     * @param parm
     * @return
     */
    public double getTotArAmt(TParm parm){
        double totAmt=0.00;
        if(parm==null){
            return totAmt;
        }
        String[] names=parm.getNames();
        for(int i=0;i<names.length;i++){
            totAmt+=parm.getDouble(names[i]);
        }
        return totAmt;
    }
    
    /**
     * 按CASE_NO查询票据
     * @param caseNo
     * @return
     */
    public boolean onQuryByCaseNo(String caseNo){
        if(StringUtil.isNullString(caseNo)){
            return false;
        }
        String sql=this.INIT.replaceFirst("#", caseNo)+"  ORDER BY RECEIPT_NO DESC";
//      // System.out.println("receiptNo.sql="+sql);
        this.setSQL(sql);
        this.retrieve();
        return true;
    }
    /**
     * 根据给入的主键做补印动作，即更新PRINT_NO，PRINT_DATA
     * @param caseNo
     * @param receiptNo
     * @param printNo
     * @return
     */
    public boolean onRePrint(String caseNo,String receiptNo,String printNo){
        // System.out.println("caseNo="+caseNo);
        // System.out.println("receiptNo="+receiptNo);
        // System.out.println("printNo"+printNo);
        if(StringUtil.isNullString(caseNo)||StringUtil.isNullString(receiptNo)||StringUtil.isNullString(printNo)){
            // System.out.println("1");
            return false;
        }
        int count=this.rowCount();
        if(this.rowCount()<=0){
            // System.out.println("2");
            return false;
        }
        String filter=this.getFilter();
        this.setFilter("CASE_NO='" +caseNo+"' AND RECEIPT_NO='" +receiptNo+"'");
        this.filter();
        Timestamp now=this.getDBTime();
        this.setItem(0, "PRINT_NO", printNo);
        this.setItem(0, "PRINT_DATE", now);
        this.setItem(0, "OPT_USER", Operator.getID());
        this.setItem(0, "OPT_DATE", now);
        this.setItem(0, "OPT_TERM", Operator.getIP());
        this.setFilter(filter);
        this.filter();
        return true;
    }
    /**
     * 退费动作，根据原来的RECEIPT_NO查到原来的数据，再将原票新增一笔负的记录
     * @param row
     * @return
     */
    public boolean onDisCharge(int row,String printNo,String payType){
        TParm parm=this.getRowParm(row);
        // System.out.println("parm==="+parm);
        if(parm==null){
            // System.out.println("onDisCharge.parm is null");
            return false;
        }
        if(StringUtil.isNullString(parm.getValue("RECEIPT_NO"))){
            // System.out.println("onDisCharge.receiptNo is null");
            return false;
        }
        //已日结不可退费
//      if(TypeTool.getBoolean(parm.getData("ACCOUNT_FLG"))){//delete by wanglong 20130325
//          // System.out.println("onDisCharge.已日结不可退费");
//          return false;
//      }
        if(StringUtil.isNullString(printNo)){
            // System.out.println("onDisCharge.printNo is null");
            return false;
        }
        parm.setData("PRINT_NO",printNo);
        parm.setData("PAY_TYPE",payType);
        return this.onInsertDisCharge(parm);
    }
    /**
     * 插入退费的一张数据
     * @param parm
     * @return
     */
    public boolean onInsertDisCharge(TParm parm){
        if(parm==null){
            return false;
        }
        int row=this.insertRow();
    //  CASE_NO
        this.setItem(row, "CASE_NO", parm.getValue("CASE_NO"));
    //  RECEIPT_NO
        //取号原则得到票据号
        String receiptNo=SystemTool.getInstance().getNo("ALL", "OPB", "RECEIPT_NO",
              "RECEIPT_NO");
        if(StringUtil.isNullString(receiptNo)){
            return false;
        }
        this.setItem(row, "RECEIPT_NO", receiptNo);
    //  ADM_TYPE
        this.setItem(row, "ADM_TYPE", "H");
    //  REGION_CODE
        this.setItem(row, "REGION_CODE", Operator.getRegion());
    //  MR_NO
        this.setItem(row, "MR_NO", parm.getValue("MR_NO"));
    //  RESET_RECEIPT_NO

    //  PRINT_NO
        this.setItem(row, "PRINT_NO", parm.getValue("PRINT_NO"));
    //  BILL_DATE
        Timestamp now=this.getDBTime();
        this.setItem(row, "BILL_DATE", now);
    //  CHARGE_DATE
        this.setItem(row, "CHARGE_DATE", now);
    //  PRINT_DATE
        this.setItem(row, "PRINT_DATE", now);
    //  CHARGE01
        this.setItem(row, "CHARGE01", TypeTool.getDouble(parm.getData("CHARGE01"))*-1);
    //  CHARGE02
        this.setItem(row, "CHARGE02", TypeTool.getDouble(parm.getData("CHARGE02"))*-1);
    //  CHARGE03
        this.setItem(row, "CHARGE03", TypeTool.getDouble(parm.getData("CHARGE03"))*-1);
    //  CHARGE04
        this.setItem(row, "CHARGE04", TypeTool.getDouble(parm.getData("CHARGE04"))*-1);
    //  CHARGE05
        this.setItem(row, "CHARGE05", TypeTool.getDouble(parm.getData("CHARGE05"))*-1);
    //  CHARGE06
        this.setItem(row, "CHARGE06", TypeTool.getDouble(parm.getData("CHARGE06"))*-1);
    //  CHARGE07
        this.setItem(row, "CHARGE07", TypeTool.getDouble(parm.getData("CHARGE07"))*-1);
    //  CHARGE08
        this.setItem(row, "CHARGE08", TypeTool.getDouble(parm.getData("CHARGE08"))*-1);
    //  CHARGE09
        this.setItem(row, "CHARGE09", TypeTool.getDouble(parm.getData("CHARGE09"))*-1);
    //  CHARGE10
        this.setItem(row, "CHARGE10", TypeTool.getDouble(parm.getData("CHARGE10"))*-1);
    //  CHARGE11
        this.setItem(row, "CHARGE11", TypeTool.getDouble(parm.getData("CHARGE11"))*-1);
    //  CHARGE12
        this.setItem(row, "CHARGE12", TypeTool.getDouble(parm.getData("CHARGE12"))*-1);
    //  CHARGE13
        this.setItem(row, "CHARGE13", TypeTool.getDouble(parm.getData("CHARGE13"))*-1);
    //  CHARGE14
        this.setItem(row, "CHARGE14", TypeTool.getDouble(parm.getData("CHARGE14"))*-1);
    //  CHARGE15
        this.setItem(row, "CHARGE15", TypeTool.getDouble(parm.getData("CHARGE15"))*-1);
    //  CHARGE16
        this.setItem(row, "CHARGE16", TypeTool.getDouble(parm.getData("CHARGE16"))*-1);
    //  CHARGE17
        this.setItem(row, "CHARGE17", TypeTool.getDouble(parm.getData("CHARGE17"))*-1);
    //  CHARGE18
        this.setItem(row, "CHARGE18", TypeTool.getDouble(parm.getData("CHARGE18"))*-1);
    //  CHARGE19
        this.setItem(row, "CHARGE19", TypeTool.getDouble(parm.getData("CHARGE19"))*-1);
    //  CHARGE20
        this.setItem(row, "CHARGE20", TypeTool.getDouble(parm.getData("CHARGE20"))*-1);
    //  CHARGE21
        this.setItem(row, "CHARGE21", TypeTool.getDouble(parm.getData("CHARGE21"))*-1);
    //  CHARGE22
        this.setItem(row, "CHARGE22", TypeTool.getDouble(parm.getData("CHARGE22"))*-1);
    //  CHARGE23
        this.setItem(row, "CHARGE23", TypeTool.getDouble(parm.getData("CHARGE23"))*-1);
    //  CHARGE24
        this.setItem(row, "CHARGE24", TypeTool.getDouble(parm.getData("CHARGE24"))*-1);
    //  CHARGE25
        this.setItem(row, "CHARGE25", TypeTool.getDouble(parm.getData("CHARGE25"))*-1);
    //  CHARGE26
        this.setItem(row, "CHARGE26", TypeTool.getDouble(parm.getData("CHARGE26"))*-1);
    //  CHARGE27
        this.setItem(row, "CHARGE27", TypeTool.getDouble(parm.getData("CHARGE27"))*-1);
    //  CHARGE28
        this.setItem(row, "CHARGE28", TypeTool.getDouble(parm.getData("CHARGE28"))*-1);
    //  CHARGE29
        this.setItem(row, "CHARGE29", TypeTool.getDouble(parm.getData("CHARGE29"))*-1);
    //  CHARGE30
        this.setItem(row, "CHARGE30", TypeTool.getDouble(parm.getData("CHARGE30"))*-1);
    //  TOT_AMT
        this.setItem(row, "TOT_AMT", TypeTool.getDouble(parm.getData("TOT_AMT"))*-1);
    //  REDUCE_REASON
        this.setItem(row, "REDUCE_REASON", parm.getData("REDUCE_REASON"));//add by wanglong 20130826
    //  REDUCE_AMT
        this.setItem(row, "REDUCE_AMT", TypeTool.getDouble(parm.getData("REDUCE_AMT"))*-1);
    //  REDUCE_DATE
        this.setItem(row, "REDUCE_DATE", parm.getData("REDUCE_DATE"));
    //  REDUCE_DEPT_CODE
        this.setItem(row, "REDUCE_DEPT_CODE", parm.getData("REDUCE_DEPT_CODE"));
    //  REDUCE_RESPOND
        this.setItem(row, "REDUCE_RESPOND", parm.getData("REDUCE_RESPOND"));
    //  REDUCE_APPROVER
        this.setItem(row, "REDUCE_APPROVER", parm.getData("REDUCE_APPROVER"));//add end
    //  AR_AMT
        this.setItem(row, "AR_AMT", TypeTool.getDouble(parm.getData("AR_AMT"))*-1);
    //  PAY_CASH
        this.setItem(row, "PAY_CASH", TypeTool.getDouble(parm.getData("PAY_CASH"))*-1);
    //  PAY_MEDICAL_CARD
        this.setItem(row, "PAY_MEDICAL_CARD", TypeTool.getDouble(parm.getData("PAY_MEDICAL_CARD"))*-1);
    //  PAY_BANK_CARD
        this.setItem(row, "PAY_BANK_CARD", TypeTool.getDouble(parm.getData("PAY_BANK_CARD"))*-1);
    //  PAY_INS_CARD
        this.setItem(row, "PAY_INS_CARD", TypeTool.getDouble(parm.getData("PAY_INS_CARD"))*-1);
    //  PAY_CHECK
        this.setItem(row, "PAY_CHECK", TypeTool.getDouble(parm.getData("PAY_CHECK"))*-1);
    //  PAY_DEBIT
        this.setItem(row, "PAY_DEBIT", TypeTool.getDouble(parm.getData("PAY_DEBIT"))*-1);
    //  PAY_BILPAY
        this.setItem(row, "PAY_BILPAY", TypeTool.getDouble(parm.getData("PAY_BILPAY"))*-1);
    //  PAY_INS
        this.setItem(row, "PAY_INS", TypeTool.getDouble(parm.getData("PAY_INS"))*-1);
    //  PAY_DRAFT
        this.setItem(row, "PAY_DRAFT", TypeTool.getDouble(parm.getData("PAY_DRAFT")) * -1);// add by wanglong 20130313
    //  PAY_OTHER1
        this.setItem(row, "PAY_OTHER1", TypeTool.getDouble(parm.getData("PAY_OTHER1"))*-1);
    //  PAY_OTHER2
        this.setItem(row, "PAY_OTHER2", TypeTool.getDouble(parm.getData("PAY_OTHER2"))*-1);
    //  PAY_REMARK
        this.setItem(row, "PAY_REMARK", parm.getData("PAY_REMARK"));
    //  CONTRACT_CODE
//        this.setItem(row, parm.getValue("PAY_TYPE"), TypeTool.getDouble(parm.getData("AR_AMT"))*-1);//delete by wanglong 20130313
    //  CASHIER_CODE
        this.setItem(row, "CASHIER_CODE", id);
    //  ACCOUNT_FLG
    //  ACCOUNT_SEQ
    //  ACCOUNT_USER
    //  ACCOUNT_DATE
    //  BANK_SEQ
    //  BANK_USER
    //  BANK_DATE
    //  OPT_USER
        this.setItem(row, "OPT_USER", id);
    //  OPT_DATE
        this.setItem(row, "OPT_DATE", now);
    //  OPT_TERM
        this.setItem(row, "OPT_TERM", ip);
        return true;
    }
}