package jdo.reg;

import com.dongyang.data.TModifiedData;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import jdo.bil.BilInvrcpt;
import jdo.bil.BilInvoice;
import java.sql.Timestamp;
/**
 *
 * <p>Title: 挂号收据对象</p>
 *
 * <p>Description: 挂号收据对象</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class RegReceipt
    extends TModifiedData {
    /**
     * 就诊号 CASE_NO
     */
    private String caseNo;
    /**
     * 收据号 RECEIPT_NO
     */
    private String receiptNo;
    /**
     * 门急住别 ADM_TYPE
     */
    private String admType;
    /**
     * 区域 REGION
     */
    private String region;
    /**
     * 病案号 MR_NO
     */
    private String mrNo;
    /**
     * 冲销收据号码 RESET_RECEIPT_NO
     */
    private String resetReceiptNo;
    /**
     * 收据印刷号 PRINT_NO
     */
    private String printNo;
    /**
     * 记账日期 BILL_DATE
     */
    private Timestamp billDate;
    /**
     * 收费日期 CHARGE_DATE
     */
    private Timestamp chargeDate;
    /**
     * 收据打印日期 PRINT_DATE
     */
    private Timestamp printDate;
    /**
     * 挂号费 REG_FEE
     */
    private double regFee;
    /**
     * 折扣后挂号费 REG_FEE_REAL
     */
    private double regFeeReal;
    /**
     * 诊查费 CLINIC_FEE
     */
    private double clinicFee;
    /**
     * 折扣后诊查费 CLINIC_FEE_REAL
     */
    private double clinicFeeReal;
    /**
     * 附加费 SPC_FEE
     */
    private double spcFee;
    /**
     * 其它费用1 OTHER_FEE1
     */
    private double otherFee1;
    /**
     * 其它费用2 OTHER_FEE2
     */
    private double otherFee2;
    /**
     * 其它费用3 OTHER_FEE3
     */
    private double otherFee3;
    /**
     * 应收金额 AR_AMT
     */
    private double arAmt;
    /**
     * 现金支付 PAY_CASH
     */
    private double payCash;
    /**
     * 银行卡支付 PAY_BANK_CARD
     */
    private double payBankCard;
    /**
     * 支票支付 PAY_CHECK
     */
    private double payCheck;
    /**
     * 医疗卡支付 PAY_MEDICAL_CARD
     */
    private double payMedicalCard;
    /**
     * 医保卡支付 PAY_INS_CARD
     */
    private double payInsCard;
    /**
     * 记账支付 PAY_DEBIT
     */
    private double payDebit;
    /**
     * 门急诊财政记账 PAY_INS
     */
    private double payIns;
    /**
     * 备注(写支票号) REMARK
     */
    private String remark;
    /**
     * 收款员编码 CASH_CODE
     */
    private String cashCode;
    /**
     *挂号员  ACCOUNT_USER
     */
    private String accountUser;
    /**
     * 结帐标志 ACCOUNT_FLG
     */
    private String accountFlg;
    /**
     * 结账日期 ACCOUNT_DATE
     */
    private Timestamp accountDate;
    /**
     * 日结报表号 ACCOUNT_SEQ
     */
    private String accountSeq;
    /**
     * 银行对账报表号 BANK_SEQ
     */
    private String bankSeq;
    /**
     * 银行对账时间 BANK_DATE
     */
    private Timestamp bankDate;
    /**
     * 银行对账人员 BANK_USER
     */
    private String bankUser;
    /**
     * 票据明细档 bilInvrcpt
     */
    private BilInvrcpt bilInvrcpt;
    /**
     * 票据主档 bilInvoice
     */
    private BilInvoice bilInvoice;

    // 构造器
    public RegReceipt() {
        StringBuffer sb = new StringBuffer();
        //就诊号
        sb.append("caseNo:CASE_NO;");
        //收据号
        sb.append("receiptNo:RECEIPT_NO;");
        //门急别
        sb.append("admType:ADM_TYPE;");
        //区域
        sb.append("region:REGION_CODE;");
        //病案号
        sb.append("mrNo:MR_NO;");
        //冲销收据号码
        sb.append("resetReceiptNo:RESET_RECEIPT_NO;");
        //收据印刷号
        sb.append("printNo:PRINT_NO;");
        //记账日期
        sb.append("billDate:BILL_DATE;");
        //收费日期
        sb.append("chargeDate:CHARGE_DATE;");
        //收据打印日期
        sb.append("printDate:PRINT_DATE;");
        //挂号费
        sb.append("regFee:REG_FEE;");
        //折扣前挂号费
        sb.append("regFeeReal:REG_FEE_REAL;");
        //诊查费
        sb.append("clinicFee:CLINIC_FEE;");
        //折扣前诊查费
        sb.append("clinicFeeReal:CLINIC_FEE_REAL;");
        //附加费
        sb.append("spcFee:SPC_FEE;");
        //其它费用1
        sb.append("otherFee1:OTHER_FEE1;");
        //其它费用2
        sb.append("otherFee2:OTHER_FEE2;");
        //其它费用3
        sb.append("otherFee3:OTHER_FEE3;");
        //应收金额
        sb.append("arAmt:AR_AMT;");
        //现金支付
        sb.append("payCash:PAY_CASH;");
        //银行卡支付
        sb.append("payBankCard:PAY_BANK_CARD;");
        //支票支付
        sb.append("payCheck:PAY_CHECK;");
        //医疗卡支付
        sb.append("payMedicalCard:PAY_MEDICAL_CARD;");
        //医保卡支付
        sb.append("payInsCard:PAY_INS_CARD;");
        //记账支付
        sb.append("payDebit:PAY_DEBIT;");
        //门急诊财政记账
        sb.append("payIns:PAY_INS;");
        //备注(写支票号)
        sb.append("remark:REMARK;");
        //收款员编码
        sb.append("cashCode:CASH_CODE;");
        //挂号员
        sb.append("accountUser:ACCOUNT_USER;");
        //结帐标志
        sb.append("accountFlg:ACCOUNT_FLG;");
        //结账日期
        sb.append("accountDate:ACCOUNT_DATE;");
        //日结报表号
        sb.append("accountSeq:ACCOUNT_SEQ;");
        //银行对账报表号
        sb.append("bankSeq:BANK_SEQ;");
        //银行对账时间
        sb.append("bankDate:BANK_DATE;");
        //银行对账人员
        sb.append("bankUser:BANK_USER;");
        setMapString(sb.toString());
    }

    /**
     * 设置就诊序号
     * @param caseNo String
     */
    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    /**
     * 得到就诊序号
     * @return String
     */
    public String getCaseNo() {
        return caseNo;
    }

    /**
     * 设置收据号
     * @param receiptNo String
     */
    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    /**
     * 得到收据号
     * @return String
     */
    public String getReceiptNo() {
        return receiptNo;
    }

    /**
     * 设置门急别
     * @param admType String
     */
    public void setAdmType(String admType) {
        this.admType = admType;
    }

    /**
     * 得到门急别
     * @return String
     */
    public String getAdmType() {
        return admType;
    }

    /**
     * 设置区域
     * @param region String
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * 得到区域
     * @return String
     */
    public String getRegion() {
        return region;
    }
    /**
     * 设置病案号
     * @param mrNo String
     */
    public void setMrNo(String mrNo){
        this.mrNo = mrNo;
    }
    /**
     * 得到病案号
     * @return String
     */
    public String getMrNo(){
        return mrNo;
    }
    /**
     * 设置冲销收据号码
     * @param resetReceiptNo String
     */
    public void setResetReceiptNo(String resetReceiptNo) {
        this.resetReceiptNo = resetReceiptNo;
    }

    /**
     * 得到冲销收据号码
     * @return String
     */
    public String getResetReceiptNo() {
        return resetReceiptNo;
    }
    /**
     * 设置收据印刷号
     * @param printNo String
     */
    public void setPrintNo(String printNo){
        this.printNo = printNo;
    }
    /**
     * 得到收据印刷号
     * @return String
     */
    public String getPrintNo(){
        return printNo;
    }
    /**
     * 设置记账日期
     * @param billDate Timestamp
     */
    public void setBillDate(Timestamp billDate) {
        this.billDate = billDate;
    }

    /**
     * 得到记账日期
     * @return Timestamp
     */
    public Timestamp getBillDate() {
        return billDate;
    }

    /**
     * 设置收费日期
     * @param chargeDate Timestamp
     */
    public void setChargeDate(Timestamp chargeDate) {
        this.chargeDate = chargeDate;
    }

    /**
     * 得到收费日期
     * @return TimestampValue
     */
    public Timestamp getChargeDate() {
        return chargeDate;
    }
    /**
     * 设置收据打印日期
     * @param printDate Timestamp
     */
    public void setPrintDate(Timestamp printDate){
        this.printDate = printDate;
    }
    /**
     * 得到收据打印日期
     * @return Timestamp
     */
    public Timestamp getPrintDate(){
        return printDate;
    }
    /**
     * 设置挂号费
     * @param regFee double
     */
    public void setRegFee(double regFee) {
        this.regFee = regFee;
    }

    /**
     * 得到挂号费
     * @return double
     */
    public double getRegFee() {
        return regFee;
    }

    /**
     * 设置折扣前挂号费
     * @param regFeeReal double
     */
    public void setRegFeeReal(double regFeeReal) {
        this.regFeeReal = regFeeReal;
    }

    /**
     * 得到折扣前挂号费
     * @return double
     */
    public double getRegFeeReal() {
        return regFeeReal;
    }

    /**
     * 设置诊查费
     * @param clinicFee double
     */
    public void setClinicFee(double clinicFee) {
        this.clinicFee = clinicFee;
    }

    /**
     * 得到诊查费
     * @return double
     */
    public double getClinicFee() {
        return clinicFee;
    }

    /**
     * 设置折扣前诊查费
     * @param clinicFeeReal double
     */
    public void setClinicFeeReal(double clinicFeeReal) {
        this.clinicFeeReal = clinicFeeReal;
    }

    /**
     * 得到折扣前诊查费
     * @return double
     */
    public double getClinicFeeReal() {
        return clinicFeeReal;
    }

    /**
     * 设置附加费
     * @param spcFee double
     */
    public void setSpcFee(double spcFee) {
        this.spcFee = spcFee;
    }

    /**
     * 得到附加费
     * @return double
     */
    public double getSpcFee() {
        return spcFee;
    }

    /**
     * 设置其它费用1
     * @param otherFee1 double
     */
    public void setOtherFee1(double otherFee1) {
        this.otherFee1 = otherFee1;
    }

    /**
     * 得到其它费用1
     * @return double
     */
    public double getOtherFee1() {
        return otherFee1;
    }

    /**
     * 设置其它费用2
     * @param otherFee2 double
     */
    public void setotherFee2(double otherFee2) {
        this.otherFee2 = otherFee2;
    }

    /**
     * 得到其它费用2
     * @return double
     */
    public double getotherFee2() {
        return otherFee2;
    }

    /**
     * 设置其它费用3
     * @param otherFee3 double
     */
    public void setotherFee3(double otherFee3) {
        this.otherFee3 = otherFee3;
    }

    /**
     * 得到其它费用3
     * @return double
     */
    public double getotherFee3() {
        return otherFee3;
    }

    /**
     * 设置应收金额
     * @param arAmt double
     */
    public void setArAmt(double arAmt) {
        this.arAmt = arAmt;
    }

    /**
     * 得到应收金额
     * @return double
     */
    public double getArAmt() {
        return arAmt;
    }

    /**
     * 设置现金支付
     * @param payCash double
     */
    public void setPayCash(double payCash) {
        this.payCash = payCash;
    }

    /**
     * 得到现金支付
     * @return double
     */
    public double getPayCash() {
        return payCash;
    }

    /**
     * 设置银行卡支付
     * @param payBankCard double
     */
    public void setPayBankCard(double payBankCard) {
        this.payBankCard = payBankCard;
    }

    /**
     * 得到银行卡支付
     * @return double
     */
    public double getPayBankCard() {
        return payBankCard;
    }

    /**
     * 设置支票支付
     * @param payCheck double
     */
    public void setPayCheck(double payCheck) {
        this.payCheck = payCheck;
    }

    /**
     * 得到支票支付
     * @return double
     */
    public double getPayCheck() {
        return payCheck;
    }

    /**
     * 设置医疗卡支付
     * @param payMedicalCard double
     */
    public void setPayMedicalCard(double payMedicalCard) {
        this.payMedicalCard = payMedicalCard;
    }

    /**
     * 得到医疗卡支付
     * @return double
     */
    public double getPayMedicalCard() {
        return payMedicalCard;
    }

    /**
     * 设置医保卡支付
     * @param payInsCard double
     */
    public void setPayInsCard(double payInsCard) {
        this.payInsCard = payInsCard;
    }

    /**
     * 得到医保卡支付
     * @return double
     */
    public double getPayInsCard() {
        return payInsCard;
    }

    /**
     * 设置记账支付
     * @param payDebit double
     */
    public void setPayDebit(double payDebit) {
        this.payDebit = payDebit;
    }

    /**
     * 得到记账支付
     * @return double
     */
    public double getPayDebit() {
        return payDebit;
    }

    /**
     * 设置门急诊财政记账
     * @param payIns double
     */
    public void setPayIns(double payIns) {
        this.payIns = payIns;
    }

    /**
     * 得到门急诊财政记账
     * @return double
     */
    public double getPayIns() {
        return payIns;
    }

    /**
     * 设置备注(写支票号)
     * @param remark String
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 得到备注(写支票号)
     * @return String
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置收款员编码
     * @param cashCode String
     */
    public void setCashCode(String cashCode) {
        this.cashCode = cashCode;
    }

    /**
     * 得到收款员编码
     * @return String
     */
    public String getashCode() {
        return cashCode;
    }

    /**
     * 设置挂号员
     * @param accountUser String
     */
    public void setAccountUser(String accountUser) {
        this.accountUser = accountUser;
    }

    /**
     * 得到挂号员
     * @return String
     */
    public String getAccountUser() {
        return accountUser;
    }
    /**
     * 设置结帐标志
     * @param accountFlg String
     */
    public void setAccountFlg(String accountFlg) {
        this.accountFlg = accountFlg;
    }

    /**
     * 得到结帐标志
     * @return String
     */
    public String getAccountFlg() {
        return accountFlg;
    }
    /**
     * 设置结账日期
     * @param accountDate Timestamp
     */
    public void setAccountDate(Timestamp accountDate) {
        this.accountDate = accountDate;
    }

    /**
     * 得到结账日期
     * @return String
     */
    public Timestamp getAccountDate() {
        return accountDate;
    }
    /**
     * 设置日结报表号
     * @param accountSeq String
     */
    public void setAccountSeq(String accountSeq) {
        this.accountSeq = accountSeq;
    }

    /**
     * 得到日结报表号
     * @return String
     */
    public String getAccountSeq() {
        return accountSeq;
    }
    /**
     * 设置银行对账报表号
     * @param bankSeq String
     */
    public void setBankSeq(String bankSeq) {
        this.bankSeq = bankSeq;
    }

    /**
     * 得到银行对账报表号
     * @return String
     */
    public String getBankSeq() {
        return bankSeq;
    }
    /**
     * 设置银行对账时间
     * @param bankDate Timestamp
     */
    public void setBankDate(Timestamp bankDate) {
        this.bankDate = bankDate;
    }

    /**
     * 得到银行对账时间
     * @return String
     */
    public Timestamp getBankDate() {
        return bankDate;
    }
    /**
     * 设置银银行对账人员
     * @param bankUser String
     */
    public void setBankUser(String bankUser) {
        this.bankUser = bankUser;
    }

    /**
     * 得到银行对账人员
     * @return String
     */
    public String getBankUser() {
        return bankUser;
    }

    /**
     * 创建票据明细档
     */
    public void createBilInvrcpt() {
        bilInvrcpt = new BilInvrcpt();
        //票据明细对象

    }

    /**
     * 设置票据明细
     * @param bilInvrcpt BilInvrcpt
     */
    public void setBilInvrcpt(BilInvrcpt bilInvrcpt) {
        this.bilInvrcpt = bilInvrcpt;
    }

    /**
     * 得到票据明细
     * @return bilInvrcpt
     */
    public BilInvrcpt getBilInvrcpt() {
        return bilInvrcpt;
    }

    /**
     * 创建票据主档
     */
    public void createBilInvoice() {
        BilInvoice bilInvoice = new BilInvoice();
       setBilInvoice( bilInvoice.initBilInvoice("REG"));
        //票据主档对象

    }

    /**
     * 设置票据主档
     * @param bilInvoice BilInvrcpt
     */
    public void setBilInvoice(BilInvoice bilInvoice) {
        this.bilInvoice = bilInvoice;
    }

    /**
     * 得到票据主档
     * @return bilInvrcpt
     */
    public BilInvoice getBilInvoice() {
        return bilInvoice;
    }

    /**
     * 初始化票据
     * @param recpType String
     */
    public void initBilInvoice(String recpType) {
        this.setBilInvoice(getBilInvoice().initBilInvoice(recpType));
    }

    /**
     * 得到加载参数
     * @return TParm
     */
    public TParm getParm() {
        TParm parm = super.getParm();
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        return parm;
    }
}
