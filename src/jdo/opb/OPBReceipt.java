package jdo.opb;

import com.dongyang.data.TModifiedData;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import jdo.opd.OrderList;
import jdo.opd.PrescriptionList;
import java.util.List;
import jdo.opd.Order;

/**
 *
 * <p>Title: 门诊收费票据</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:JavaHis </p>
 *
 * @author fudw
 * @version 1.0
 */
public class OPBReceipt
    extends TModifiedData {
    private OrderList orderList;
    /**
     * 就诊序号
     */
    private String caseNo;
    /**
     * 收据号
     */
    private String receiptNo;
    /**
     * 门急住别
     */
    private String admType;
    /**
     * 区域
     */
    private String region;
    /**
     * 病案号
     */
    private String mrNo;
    /**
     * 冲红原收据序号
     */
    private String resetReceiptNo;
    /**
     * 收据印刷号码
     */
    private String printNo;
    /**
     * 记帐日期
     */
    private Timestamp billDate;
    /**
     * 收据打印日期
     */
    private Timestamp chargeDate;
    /**
     * 打印在收据上的收费日期
     */
    private Timestamp printDate;
    /**
     * 收据类别01，金额
     */
    private double charge01;
    /**
     * 收据类别02，金额
     */
    private double charge02;
    /**
     * 收据类别03，金额
     */
    private double charge03;
    /**
     * 收据类别4，金额
     */
    private double charge04;
    /**
     * 收据类别05，金额
     */
    private double charge05;
    /**
     * 收据类别06，金额
     */
    private double charge06;
    /**
     * 收据类别07，金额
     */
    private double charge07;
    /**
     * 收据类别08，金额
     */
    private double charge08;
    /**
     * 收据类别09，金额
     */
    private double charge09;
    /**
     * 收据类别10，金额
     */
    private double charge10;
    /**
     * 收据类别11，金额
     */
    private double charge11;
    /**
     * 收据类别12，金额
     */
    private double charge12;
    /**
     * 收据类别13，金额
     */
    private double charge13;
    /**
     * 收据类别14，金额
     */
    private double charge14;
    /**
     * 收据类别15，金额
     */
    private double charge15;
    /**
     * 收据类别16，金额
     */
    private double charge16;
    /**
     * 收据类别17，金额
     */
    private double charge17;
    /**
     * 收据类别18，金额
     */
    private double charge18;
    /**
     * 收据类别19，金额
     */
    private double charge19;
    /**
     * 收据类别20，金额
     */
    private double charge20;
    /**
     * 收据类别21，金额
     */
    private double charge21;
    /**
     * 收据类别22，金额
     */
    private double charge22;
    /**
     * 收据类别23，金额
     */
    private double charge23;
    /**
     * 收据类别24，金额
     */
    private double charge24;
    /**
     * 收据类别25，金额
     */
    private double charge25;
    /**
     * 收据类别26，金额
     */
    private double charge26;
    /**
     * 收据类别27，金额
     */
    private double charge27;
    /**
     * 收据类别28，金额
     */
    private double charge28;
    /**
     * 收据类别29，金额
     */
    private double charge29;
    /**
     * 收据类别30，金额
     */
    private double charge30;
    /**
     * 应付金额
     */
    private double totAmt;
    /**
     * 折扣/减免原因
     */
    private String reduceReason;
    /**
     * 折扣/减免金额
     */
    private double reduceAmt;
    /**
     * 折扣/减免时间
     */
    private Timestamp reduceDate;
    /**
     * 折扣/减免科室
     */
    private String reduceDeptCode;
    /**
     * 折扣人员
     */
    private String reduceRespond;
    /**
     * 应收金额
     */
    private double arAmt;
    /**
     * 现金支付
     */
    private double payCash;
    /**
     * 医疗卡支付
     */
    private double payMedicalCard;
    /**
     * 刷卡
     */
    private double payBankCard;
    /**
     * 医保卡支付
     */
    private double payInsCard;
    /**
     * 支票支付
     */
    private double payCheck;
    /**
     * 记帐支付
     */
    private double payDepit;
    /**
     * 预交金支付
     */
    private double payBilPay;
    /**
     * 医保支付
     */
    private double payIns;
    /**
     * 其他支付1
     */
    private double payOther1;
    /**
     * 其他支付二
     */
    private double payOther2;
    /**
     * 收款备注
     */
    private String payRemark;
    /**
     * 收费员
     */
    private String cashierCode;
    /**
     * 日结帐标志
     */
    private String accountFlg;
    /**
     * 日结帐日期
     */
    private Timestamp accountDate;
    /**
     * 日结操作员
     */
    private String accountUser;
    /**
     * 记账单位编码
     */
    private String contractCode;
    /**
     * 银行交款序号
     */
    private String bankSeq;
    /**
     * 银行交款时间
     */
    private Timestamp bankDate;
    /**
     * 银行交款，报表产生人员
     */
    private String bankUser;
    /**
     * 操作员
     */
    private String optUser;
    /**
     * 操作时间
     */
    private Timestamp optDate;
    /**
     * 操作终端
     */
    private String optTerm;
    /**
     * 设置医嘱列表
     * @param orderList OrderList
     */
    public void setOrderList(OrderList orderList) {
        this.orderList = orderList;
    }

    /**
     * 得到医嘱列表
     * @return OrderList
     */
    public OrderList getOrderList() {
        return this.orderList;
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
     *  设置收据号
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
     * 设置门急住别
     * @param admType String
     */
    public void setAdmType(String admType) {
        this.admType = admType;
    }

    /**
     * 得到门急住别
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
    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    /**
     * 得到病案号
     * @return String
     */
    public String getMrNo() {
        return mrNo;
    }

    /**
     * 设置冲红原收据序号
     * @param resetReceiptNo String
     */
    public void setResetReceiptNo(String resetReceiptNo) {
        this.resetReceiptNo = resetReceiptNo;
    }

    /**
     * 得到冲红原收据序号
     * @return String
     */
    public String getResetReceiptNo() {
        return resetReceiptNo;
    }

    /**
     * 设置收据印刷号码
     * @param printNo String
     */
    public void setPrintNo(String printNo) {
        this.printNo = printNo;
    }

    /**
     * 得到收据印刷号码
     * @return String
     */
    public String getPrintNo() {
        return printNo;
    }

    /**
     * 设置记帐日期
     * @param billDate Timestamp
     */
    public void setBilDate(Timestamp billDate) {
        this.billDate = billDate;
    }

    /**
     * 得到记帐日期
     * @return Timestamp
     */
    public Timestamp getBillDate() {
        return billDate;
    }

    /**
     * 设置收据打印日期
     * @param chargeDate Timestamp
     */
    public void setChargeDate(Timestamp chargeDate) {
        this.chargeDate = chargeDate;
    }

    /**
     * 得到收据打印日期
     * @return Timestamp
     */
    public Timestamp getChargeDate() {
        return chargeDate;
    }

    /**
     * 设置打印在收据上的收费日期
     * @param printDate Timestamp
     */
    public void setPrintDate(Timestamp printDate) {
        this.printDate = printDate;
    }

    /**
     * 得到打印在收据上的收费日期
     * @return Timestamp
     */
    public Timestamp getPrintDate() {
        return printDate;
    }

    /**
     * 设置收据类别01，金额
     * @param charge01 double
     */
    public void setCharge01(double charge01) {
        this.charge01 = charge01;
    }

    /**
     * 得到收据类别01，金额
     * @return double
     */
    public double getCharge01() {
        return charge01;
    }

    /**
     * 设置收据类别02，金额
     * @param charge02 double
     */
    public void setCharge02(double charge02) {
        this.charge02 = charge02;
    }

    /**
     * 得到收据类别02，金额
     * @return double
     */
    public double getCharge02() {
        return charge02;
    }

    /**
     * 设置收据类别03，金额
     * @param charge03 double
     */
    public void setCharge03(double charge03) {
        this.charge03 = charge03;
    }

    /**
     * 得到收据类别03，金额
     * @return double
     */
    public double getCharge03() {
        return charge03;
    }

    /**
     * 设置收据类别04，金额
     * @param charge04 double
     */
    public void setCharge04(double charge04) {
        this.charge04 = charge04;
    }

    /**
     * 得到收据类别04，金额
     * @return double
     */
    public double getCharge04() {
        return charge04;
    }

    /**
     * 设置收据类别05，金额
     * @param charge05 double
     */
    public void setCharge05(double charge05) {
        this.charge05 = charge05;
    }

    /**
     * 得到收据类别5，金额
     * @return double
     */
    public double getCharge05() {
        return charge05;
    }

    /**
     * 设置收据类别01，金额
     * @param charge06 double
     */
    public void setCharge06(double charge06) {
        this.charge06 = charge06;
    }

    /**
     * 得到收据类别06，金额
     * @return double
     */
    public double getCharge06() {
        return charge06;
    }

    /**
     * 设置收据类别07，金额
     * @param charge07 double
     */
    public void setCharge07(double charge07) {
        this.charge07 = charge07;
    }

    /**
     * 得到收据类别07，金额
     * @return double
     */
    public double getCharge07() {
        return charge07;
    }

    /**
     * 设置收据类别08，金额
     * @param charge08 double
     */
    public void setCharge08(double charge08) {
        this.charge08 = charge08;
    }

    /**
     * 得到收据类别8，金额
     * @return double
     */
    public double getCharge08() {
        return charge08;
    }

    /**
     * 设置收据类别9，金额
     * @param charge09 double
     */
    public void setCharge09(double charge09) {
        this.charge09 = charge09;
    }

    /**
     * 得到收据类别09，金额
     * @return double
     */
    public double getCharge09() {
        return charge09;
    }

    /**
     * 设置收据类别10，金额
     * @param charge10 double
     */
    public void setCharge10(double charge10) {
        this.charge10 = charge10;
    }

    /**
     * 得到收据类别10，金额
     * @return double
     */
    public double getCharge10() {
        return charge10;
    }

    /**
     * 设置收据类别11，金额
     * @param charge11 double
     */
    public void setCharge11(double charge11) {
        this.charge11 = charge11;
    }

    /**
     * 得到收据类别11，金额
     * @return double
     */
    public double getCharge11() {
        return charge11;
    }

    /**
     * 设置收据类别12，金额
     * @param charge12 double
     */
    public void setCharge12(double charge12) {
        this.charge12 = charge12;
    }

    /**
     * 得到收据类别12，金额
     * @return double
     */
    public double getCharge12() {
        return charge12;
    }

    /**
     * 设置收据类别13，金额
     * @param charge13 double
     */
    public void setCharge13(double charge13) {
        this.charge13 = charge13;
    }

    /**
     * 得到收据类别13，金额
     * @return double
     */
    public double getCharge13() {
        return charge13;
    }

    /**
     * 设置收据类别14，金额
     * @param charge14 double
     */
    public void setCharge14(double charge14) {
        this.charge14 = charge14;
    }

    /**
     * 得到收据类别14，金额
     * @return double
     */
    public double getCharge14() {
        return charge14;
    }

    /**
     * 设置收据类别15，金额
     * @param charge15 double
     */
    public void setCharge15(double charge15) {
        this.charge15 = charge15;
    }

    /**
     * 得到收据类别15，金额
     * @return double
     */
    public double getCharge15() {
        return charge15;
    }

    /**
     * 设置收据类别16，金额
     * @param charge16 double
     */
    public void setCharge16(double charge16) {
        this.charge16 = charge16;
    }

    /**
     * 得到收据类别16，金额
     * @return double
     */
    public double getCharge16() {
        return charge16;
    }

    /**
     * 设置收据类别17，金额
     * @param charge17 double
     */
    public void setCharge17(double charge17) {
        this.charge17 = charge17;
    }

    /**
     * 得到收据类别17，金额
     * @return double
     */
    public double getCharge17() {
        return charge17;
    }

    /**
     * 设置收据类别18，金额
     * @param charge18 double
     */
    public void setCharge18(double charge18) {
        this.charge18 = charge18;
    }

    /**
     * 得到收据类别18，金额
     * @return double
     */
    public double getCharge18() {
        return charge18;
    }

    /**
     * 设置收据类别19，金额
     * @param charge19 double
     */
    public void setCharge19(double charge19) {
        this.charge19 = charge19;
    }

    /**
     * 得到收据类别18，金额
     * @return double
     */
    public double getCharge19() {
        return charge19;
    }

    /**
     * 设置收据类别20，金额
     * @param charge20 double
     */
    public void setCharge20(double charge20) {
        this.charge20 = charge20;
    }

    /**
     * 得到收据类别20，金额
     * @return double
     */
    public double getCharge20() {
        return charge20;
    }

    /**
     * 设置收据类别21，金额
     * @param charge21 double
     */
    public void setCharge21(double charge21) {
        this.charge21 = charge21;
    }

    /**
     * 得到收据类别21，金额
     * @return double
     */
    public double getCharge21() {
        return charge21;
    }

    /**
     * 设置收据类别22，金额
     * @param charge22 double
     */
    public void setCharge22(double charge22) {
        this.charge22 = charge22;
    }

    /**
     * 得到收据类别22，金额
     * @return double
     */
    public double getCharge22() {
        return charge22;
    }

    /**
     * 设置收据类别23，金额
     * @param charge23 double
     */
    public void setCharge23(double charge23) {
        this.charge23 = charge23;
    }

    /**
     * 得到收据类别23，金额
     * @return double
     */
    public double getCharge23() {
        return charge23;
    }

    /**
     * 设置收据类别24，金额
     * @param charge24 double
     */
    public void setCharge24(double charge24) {
        this.charge24 = charge24;
    }

    /**
     * 得到收据类别24，金额
     * @return double
     */
    public double getCharge24() {
        return charge24;
    }

    /**
     * 设置收据类别25，金额
     * @param charge25 double
     */
    public void setCharge25(double charge25) {
        this.charge25 = charge25;
    }

    /**
     * 得到收据类别25，金额
     * @return double
     */
    public double getCharge25() {
        return charge25;
    }

    /**
     * 设置收据类别26，金额
     * @param charge26 double
     */
    public void setCharge26(double charge26) {
        this.charge26 = charge26;
    }

    /**
     * 得到收据类别26，金额
     * @return double
     */
    public double getCharge26() {
        return charge26;
    }

    /**
     * 设置收据类别27，金额
     * @param charge27 double
     */
    public void setCharge27(double charge27) {
        this.charge27 = charge27;
    }

    /**
     * 得到收据类别27，金额
     * @return double
     */
    public double getCharge27() {
        return charge27;
    }

    /**
     * 设置收据类别28，金额
     * @param charge28 double
     */
    public void setCharge28(double charge28) {
        this.charge28 = charge28;
    }

    /**
     * 得到收据类别28，金额
     * @return double
     */
    public double getCharge28() {
        return charge28;
    }

    /**
     * 设置收据类别29，金额
     * @param charge29 double
     */
    public void setCharge29(double charge29) {
        this.charge29 = charge29;
    }

    /**
     * 得到收据类别29，金额
     * @return double
     */
    public double getCharge29() {
        return charge29;
    }

    /**
     * 设置收据类别30，金额
     * @param charge30 double
     */
    public void setCharge30(double charge30) {
        this.charge30 = charge30;
    }

    /**
     * 得到收据类别30，金额
     * @return double
     */
    public double getCharge30() {
        return charge30;
    }

    /**
     * 设置应付金额
     * @param totAmt double
     */
    public void setTotAmt(double totAmt) {
        this.totAmt = totAmt;
    }

    /**
     * 得到应付金额
     * @return double
     */
    public double getTotAmt() {
        return totAmt;
    }

    /**
     * 设置折扣/减免原因
     * @param reduceReason String
     */
    public void setReduceReason(String reduceReason) {
        this.reduceReason = reduceReason;
    }

    /**
     * 得到折扣/减免原因
     * @return String
     */
    public String getReduceReason() {
        return reduceReason;
    }

    /**
     * 设置折扣/减免科室
     * @param reduceDeptCode String
     */
    public void setReduceDeptCode(String reduceDeptCode) {
        this.reduceDeptCode = reduceDeptCode;
    }

    /**
     * 得到折扣/减免科室
     * @return String
     */
    public String getReduceDeptCode() {
        return reduceDeptCode;
    }

    /**
     * 设置折扣/减免金额
     * @param reduceAmt double
     */
    public void setReduceAmt(double reduceAmt) {
        this.reduceAmt = reduceAmt;
    }

    /**
     * 得到折扣/减免金额
     * @return double
     */
    public double getReduceAmt() {
        return reduceAmt;
    }

    /**
     * 设置折扣/减免时间
     * @param reduceDate Timestamp
     */
    public void setReduceDate(Timestamp reduceDate) {
        this.reduceDate = reduceDate;
    }

    /**
     * 得到折扣/减免时间
     * @return Timestamp
     */
    public Timestamp getReduceDate() {
        return reduceDate;
    }

    /**
     * 设置折扣人员
     * @param reduceRespond String
     */
    public void setReduceRespond(String reduceRespond) {
        this.reduceRespond = reduceRespond;
    }

    /**
     * 得到折扣人员
     * @return String
     */
    public String getReduceRespond() {
        return reduceRespond;
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
     * 设置刷卡
     * @param payBankCard double
     */
    public void setPayBankCard(double payBankCard) {
        this.payBankCard = payBankCard;
    }

    /**
     * 得到刷卡
     * @return double
     */
    public double getPayBankCard() {
        return payBankCard;
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
     * 设置记帐支付
     * @param payDepit double
     */
    public void setPayDepit(double payDepit) {
        this.payDepit = payDepit;
    }

    /**
     * 得到记帐支付
     * @return double
     */
    public double getPayDepit() {
        return payDepit;
    }

    /**
     * 设置预交金支付
     * @param payBilPay double
     */
    public void setPayBilPay(double payBilPay) {
        this.payBilPay = payBilPay;
    }

    /**
     * 得到预交金支付
     * @return double
     */
    public double getPayBilPay() {
        return payBilPay;
    }

    /**
     * 设置医保支付
     * @param payIns double
     */
    public void setPayIns(double payIns) {
        this.payIns = payIns;
    }

    /**
     * 得到医保支付
     * @return double
     */
    public double getPayIns() {
        return payIns;
    }

    /**
     * 设置其他支付1
     * @param payOther1 double
     */
    public void setPayOther1(double payOther1) {
        this.payOther1 = payOther1;
    }

    /**
     * 得到其他支付1
     * @return double
     */
    public double getPayOther1() {
        return payOther1;
    }

    /**
     * 设置其他支付2
     * @param payOther2 double
     */
    public void setPayOther2(double payOther2) {
        this.payOther2 = payOther2;
    }

    /**
     * 得到其他支付2
     * @return double
     */
    public double getPayOther2() {
        return payOther2;
    }

    /**
     * 设置收款备注
     * @param payRemark String
     */
    public void setPayRemark(String payRemark) {
        this.payRemark = payRemark;
    }

    /**
     * 得到收款备注
     * @return String
     */
    public String getPayRemark() {
        return payRemark;
    }

    /**
     * 设置收费员
     * @param cashierCode String
     */
    public void setCashierCode(String cashierCode) {
        this.cashierCode = cashierCode;
    }

    /**
     * 得到收费员
     * @return String
     */
    public String getCashierCode() {
        return cashierCode;
    }

    /**
     * 设置日结帐标志
     * @param accountFlg String
     */
    public void setAccountFlg(String accountFlg) {
        this.accountFlg = accountFlg;
    }

    /**
     * 得到日结帐标志
     * @return String
     */
    public String getAccountFlg() {
        return accountFlg;
    }

    /**
     * 设置日结帐日期
     * @param accountDate Timestamp
     */
    public void setAccountDate(Timestamp accountDate) {
        this.accountDate = accountDate;
    }

    /**
     * 得到日结帐日期
     * @return Timestamp
     */
    public Timestamp getAccountDate() {
        return accountDate;
    }

    /**
     * 设置日结操作员
     * @param accountUser String
     */
    public void setAccountUser(String accountUser) {
        this.accountUser = accountUser;
    }

    /**
     * 得到日结操作员
     * @return String
     */
    public String getAccountUser() {
        return accountUser;
    }

    /**
     * 设置记账单位编码
     * @param contractCode String
     */
    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    /**
     * 得到记账单位编码
     * @return String
     */
    public String getContractCode() {
        return contractCode;
    }

    /**
     * 设置银行交款序号
     * @param bankSeq String
     */
    public void setBankSeq(String bankSeq) {
        this.bankSeq = bankSeq;
    }

    /**
     * 得到银行交款序号
     * @return String
     */
    public String getBankSeq() {
        return bankSeq;
    }

    /**
     * 设置银行交款时间
     * @param bankDate Timestamp
     */
    public void setBankDate(Timestamp bankDate) {
        this.bankDate = bankDate;
    }

    /**
     * 得到银银行交款时间
     * @return Timestamp
     */
    public Timestamp getBankDate() {
        return bankDate;
    }

    /**
     * 设置银银行交款，报表产生人员
     * @param bankUser String
     */
    public void setBankUser(String bankUser) {
        this.bankUser = bankUser;
    }

    /**
     * 得到银行交款，报表产生人员
     * @return String
     */
    public String getBankUser() {
        return bankUser;
    }

    /**
     * 设置操作员
     * @param optUser String
     */
    public void setOptUser(String optUser) {
        this.optUser = optUser;
    }

    /**
     * 得到操作员
     * @return String
     */
    public String getOptUser() {
        return optUser;
    }

    /**
     * 设置操作时间
     * @param optDate Timestamp
     */
    public void setOptDate(Timestamp optDate) {
        this.optDate = optDate;
    }

    /**
     * 得到操作时间
     * @return Timestamp
     */
    public Timestamp getOptDate() {
        return optDate;
    }

    /**
     * 设置操作终端
     * @param optTerm String
     */
    public void setOptTerm(String optTerm) {
        this.optTerm = optTerm;
    }

    /**
     * 得到操作终端
     * @return String
     */
    public String getOptTerm() {
        return optTerm;
    }

    // 构造器
    public OPBReceipt() {
        StringBuffer sb = new StringBuffer();
        // 就诊序号
        sb.append("caseNo:CASE_NO;");
        //收据号
        sb.append("receiptNo:RECEIPT_NO;");
        // 门急住别
        sb.append("admType:ADM_TYPE;");
        //区域
        sb.append("region:REGION_CODE;");
        // 病案号
        sb.append("mrNo:MR_NO;");
        //冲红原收据序号
        sb.append("resetReceiptNo:RESET_RECEIPT_NO;");
        //收据印刷号码
        sb.append("printNo:PRINT_NO;");
        //记帐日期
        sb.append("billDate:BILL_DATE;");
        //收据打印日期
        sb.append("chargeDate:CHARGE_DATE;");
        // 打印在收据上的收费日期
        sb.append("printDate:PRINT_DATE;");
        // 收据类别01，金额
        sb.append("charge01:CHARGE01;");
        // 收据类别02，金额
        sb.append("charge02:CHARGE02;");
        // 收据类别03，金额
        sb.append("charge03:CHARGE03;");
        // 收据类别04，金额
        sb.append("charge04:CHARGE04;");
        // 收据类别05，金额
        sb.append("charge05:CHARGE05;");
        // 收据类别06，金额
        sb.append("charge06:CHARGE06;");
        // 收据类别7，金额
        sb.append("charge07:CHARGE07;");
        // 收据类别8，金额
        sb.append("charge08:CHARGE08;");
        // 收据类别1，金额
        sb.append("charge09:CHARGE09;");
        // 收据类别10，金额
        sb.append("charge10:CHARGE10;");
        // 收据类别11，金额
        sb.append("charge11:CHARGE11;");
        // 收据类别12，金额
        sb.append("charge12:CHARGE12;");
        // 收据类别13，金额
        sb.append("charge13:CHARGE13;");
        // 收据类别14，金额
        sb.append("charge14:CHARGE14;");
        // 收据类别15，金额
        sb.append("charge15:CHARGE15;");
        // 收据类别16，金额
        sb.append("charge16:CHARGE16;");
        // 收据类别17，金额
        sb.append("charge17:CHARGE17;");
        // 收据类别1，金额
        sb.append("charge18:CHARGE18;");
        // 收据类别19，金额
        sb.append("charge19:CHARGE19;");
        // 收据类别20，金额
        sb.append("charge20:CHARGE20;");
        // 收据类别21，金额
        sb.append("charge21:CHARGE21;");
        // 收据类别22，金额
        sb.append("charge22:CHARGE22;");
        // 收据类别23，金额
        sb.append("charge23:CHARGE23;");
        // 收据类别24，金额
        sb.append("charge24:CHARGE24;");
        // 收据类别25，金额
        sb.append("charge25:CHARGE25;");
        // 收据类别26，金额
        sb.append("charge26:CHARGE26;");
        // 收据类别27，金额
        sb.append("charge27:CHARGE27;");
        // 收据类别28，金额
        sb.append("charge28:CHARGE28;");
        // 收据类别29，金额
        sb.append("charge29:CHARGE29;");
        // 收据类别30，金额
        sb.append("charge30:CHARGE30;");
        // 应付金额
        sb.append("totAmt:TOT_AMT;");
        //折扣/减免原因
        sb.append("reduceReason:REDUCE_REASON;");
        // 折扣/减免金额
        sb.append("reduceAmt:REDUCE_AMT;");
        // 折扣/减免时间
        sb.append("reduceDate:REDUCE_DATE;");
        //折扣/减免科室
        sb.append("reduceDeptCode:REDUCE_DEPT_CODE;");
        // 折扣人员
        sb.append("reduceRespond:REDUCE_RESPOND;");
        // 应收金额
        sb.append("arAmt:AR_AMT;");
        //现金支付
        sb.append("payCash:PAY_CASH;");
        // 医疗卡支付
        sb.append("payMedicalCard:PAY_MEDICAL_CARD;");
        //刷卡
        sb.append("payBankCard:PAY_BANK_CARD;");
        //医保卡支付
        sb.append("payInsCard:PAY_INS_CARD;");
        //支票支付
        sb.append("payCheck:PAY_CHECK;");
        //记帐支付
        sb.append("payDepit:PAY_DEBIT;");
        //预交金支付
        sb.append("payBilPay:PAY_BILPAY;");
        //医保
        sb.append("payIns:PAY_INS;");
        //其他支付1
        sb.append("payOther1:PAY_OTHER1;");
        //其他支付二
        sb.append("payOther2:PAY_OTHER2;");
        //收款备注
        sb.append("payRemark:PAY_REMARK;");
        //收费员
        sb.append("cashierCode:CASHIER_CODE;");
        //日结帐标志
        sb.append("accountFlg:ACCOUNT_FLG;");
        //日结帐日期
        sb.append("accountDate:ACCOUNT_DATE;");
        //日结操作员
        sb.append("accountUser:ACCOUNT_USER;");
        //记账单位编码
        sb.append("contractCode:ACCOUNT_CODE;");
        //银行交款序号
        sb.append("bankSeq:BANK_SEQ;");
        //银行交款时间
        sb.append("bankDate:BANK_DATE;");
        //银行交款，报表产生人员
        sb.append("bankUser:BANK_USER;");
        //操作员
        sb.append("optUser:OPT_USER;");
        //操作时间
        sb.append("optDate:OPT_DATE;");
        //操作终端
        sb.append("optTerm:OPT_TERM;");
        setMapString(sb.toString());
    }

    /**
     *
     * @param parm TParm
     */
    public void initCharge(TParm parm) {
        this.setCharge01(parm.getDouble("CHARGE01"));
        this.setCharge02(parm.getDouble("CHARGE02"));
        this.setCharge03(parm.getDouble("CHARGE03"));
        this.setCharge04(parm.getDouble("CHARGE04"));
        this.setCharge05(parm.getDouble("CHARGE05"));
        this.setCharge06(parm.getDouble("CHARGE06"));
        this.setCharge07(parm.getDouble("CHARGE07"));
        this.setCharge08(parm.getDouble("CHARGE08"));
        this.setCharge09(parm.getDouble("CHARGE09"));
        this.setCharge10(parm.getDouble("CHARGE10"));
        this.setCharge11(parm.getDouble("CHARGE11"));
        this.setCharge12(parm.getDouble("CHARGE12"));
        this.setCharge13(parm.getDouble("CHARGE13"));
        this.setCharge14(parm.getDouble("CHARGE14"));
        this.setCharge15(parm.getDouble("CHARGE15"));
        this.setCharge16(parm.getDouble("CHARGE16"));
        this.setCharge17(parm.getDouble("CHARGE17"));
        this.setCharge18(parm.getDouble("CHARGE18"));
        this.setCharge19(parm.getDouble("CHARGE19"));
        this.setCharge20(parm.getDouble("CHARGE20"));
        this.setCharge21(parm.getDouble("CHARGE21"));
        this.setCharge22(parm.getDouble("CHARGE22"));
        this.setCharge23(parm.getDouble("CHARGE23"));
        this.setCharge24(parm.getDouble("CHARGE24"));
        this.setCharge25(parm.getDouble("CHARGE25"));
        this.setCharge26(parm.getDouble("CHARGE26"));
        this.setCharge27(parm.getDouble("CHARGE27"));
        this.setCharge28(parm.getDouble("CHARGE28"));
        this.setCharge29(parm.getDouble("CHARGE29"));
        this.setCharge30(parm.getDouble("CHARGE30"));
    }

    /**
     * 得到加载参数
     * @return TParm
     */
    public TParm getParm() {
        TParm parm = super.getParm();
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        return parm;
    }

    /**
     * 得到一张票据
     * @param receiptNo String
     * @return OPBReceipt
     */
    public OPBReceipt getOneReceipt(String receiptNo) {
        //捞取收费数据
        TParm result = OPBReceiptTool.getInstance().getOneReceipt(receiptNo);
        if (result.getErrCode() < 0)
            return null;
        int row = result.getCount();
        if (row <= 0)
            return null;
        return getReceiptByTParm(result.getRow(0), 1);
    }

    /**
     * 传入一个TParm返回一个票据对象
     * @param parm TParm
     * @param mark int
     * @return OPBReceipt
     */
    public OPBReceipt getReceiptByTParm(TParm parm, int mark) {
        OPBReceipt opbReceiptOne = new OPBReceipt();
        //就诊序号
        opbReceiptOne.setCaseNo(parm.getValue("CASE_NO"));
        //票据号
        opbReceiptOne.setReceiptNo(parm.getValue("RECEIPT_NO"));
        //门急住别
        opbReceiptOne.setAdmType(parm.getValue("ADM_TYPE"));
        //区域
        opbReceiptOne.setRegion(parm.getValue("REGION"));
        //病案号
        opbReceiptOne.setMrNo(parm.getValue("MR_NO"));
        //回冲收据号
        opbReceiptOne.setResetReceiptNo(parm.getValue("RESET_RECEIPT_NO"));
        //打印的票据号
        opbReceiptOne.setPrintNo(parm.getValue("PRINT_NO"));
        //收费时间
        opbReceiptOne.setBilDate( (Timestamp) parm.getData("BILL_DATE"));
        //计费时间
        opbReceiptOne.setChargeDate( (Timestamp) parm.getData("CHARGE_DATE"));
        //打票时间
        opbReceiptOne.setPrintDate(parm.getTimestamp("PRINT_DATE"));
        //收费项目明细1~30
        opbReceiptOne.setCharge01(parm.getDouble("CHARGE01") * mark);
        opbReceiptOne.setCharge02(parm.getDouble("CHARGE02") * mark);
        opbReceiptOne.setCharge03(parm.getDouble("CHARGE03") * mark);
        opbReceiptOne.setCharge04(parm.getDouble("CHARGE04") * mark);
        opbReceiptOne.setCharge05(parm.getDouble("CHARGE05") * mark);
        opbReceiptOne.setCharge06(parm.getDouble("CHARGE06") * mark);
        opbReceiptOne.setCharge07(parm.getDouble("CHARGE07") * mark);
        opbReceiptOne.setCharge08(parm.getDouble("CHARGE08") * mark);
        opbReceiptOne.setCharge09(parm.getDouble("CHARGE09") * mark);
        opbReceiptOne.setCharge10(parm.getDouble("CHARGE10") * mark);
        opbReceiptOne.setCharge11(parm.getDouble("CHARGE11") * mark);
        opbReceiptOne.setCharge12(parm.getDouble("CHARGE12") * mark);
        opbReceiptOne.setCharge13(parm.getDouble("CHARGE13") * mark);
        opbReceiptOne.setCharge14(parm.getDouble("CHARGE14") * mark);
        opbReceiptOne.setCharge15(parm.getDouble("CHARGE15") * mark);
        opbReceiptOne.setCharge16(parm.getDouble("CHARGE16") * mark);
        opbReceiptOne.setCharge17(parm.getDouble("CHARGE17") * mark);
        opbReceiptOne.setCharge18(parm.getDouble("CHARGE18") * mark);
        opbReceiptOne.setCharge19(parm.getDouble("CHARGE19") * mark);
        opbReceiptOne.setCharge20(parm.getDouble("CHARGE20") * mark);
        opbReceiptOne.setCharge21(parm.getDouble("CHARGE21") * mark);
        opbReceiptOne.setCharge22(parm.getDouble("CHARGE22") * mark);
        opbReceiptOne.setCharge23(parm.getDouble("CHARGE23") * mark);
        opbReceiptOne.setCharge24(parm.getDouble("CHARGE24") * mark);
        opbReceiptOne.setCharge25(parm.getDouble("CHARGE25") * mark);
        opbReceiptOne.setCharge26(parm.getDouble("CHARGE26") * mark);
        opbReceiptOne.setCharge27(parm.getDouble("CHARGE27") * mark);
        opbReceiptOne.setCharge28(parm.getDouble("CHARGE28") * mark);
        opbReceiptOne.setCharge29(parm.getDouble("CHARGE29") * mark);
        opbReceiptOne.setCharge30(parm.getDouble("CHARGE30") * mark);
        //TOT_AMT;REDUCE_AMT;AR_AMT;PAY_CASH;PAY_MEDICAL_CARD;
        //PAY_BANK_CARD;PAY_INS_CARD;PAY_CHECK;PAY_DEBIT;PAY_BILPAY;PAY_INS;PAY_OTHER1;PAY_OTHER2;CASHIER_CODE
        //各种支付方式
        opbReceiptOne.setTotAmt(parm.getDouble("TOT_AMT") * mark);
        opbReceiptOne.setReduceAmt(parm.getDouble("REDUCE_AMT") * mark);
        opbReceiptOne.setArAmt(parm.getDouble("AR_AMT") * mark);
        opbReceiptOne.setPayCash(parm.getDouble("PAY_CASH") * mark);
        opbReceiptOne.setPayMedicalCard(parm.getDouble("PAY_MEDICAL_CARD") *
                                        mark);
        opbReceiptOne.setPayBankCard(parm.getDouble("PAY_BANK_CARD") * mark);
        opbReceiptOne.setPayInsCard(parm.getDouble("PAY_INS_CARD") * mark);
        opbReceiptOne.setPayCheck(parm.getDouble("PAY_CHECK") * mark);
        opbReceiptOne.setPayDepit(parm.getDouble("PAY_DEBIT") * mark);
        opbReceiptOne.setPayBilPay(parm.getDouble("PAY_BILPAY") * mark);
        opbReceiptOne.setPayIns(parm.getDouble("PAY_INS") * mark);
        opbReceiptOne.setPayOther1(parm.getDouble("PAY_OTHER1") * mark);
        opbReceiptOne.setPayOther2(parm.getDouble("PAY_OTHER2") * mark);
        //支票号
        opbReceiptOne.setPayRemark(parm.getValue("PAY_REMARK"));
        //收费员
        opbReceiptOne.setCashierCode(parm.getValue("CASHIER_CODE"));
        //日结标记
        opbReceiptOne.setAccountFlg("N");
        //日结日期
        opbReceiptOne.setAccountDate(null);
        //日结人员
        opbReceiptOne.setAccountUser("");
        //
        opbReceiptOne.setContractCode("");
        //银行交账号
        opbReceiptOne.setBankSeq("");
        //交账时间
        opbReceiptOne.setBankDate(null);
        //交账人员
        opbReceiptOne.setBankUser("");
        return opbReceiptOne;
    }

    /**
     * 处理退费时自身的ordre
     */
    public void setBackReceiptValue() {
        int count = this.orderList.size(orderList.PRIMARY);
        for (int i = 0; i < count; i++) {
            Order order = (Order) orderList.getOrder(i);
            dealBackReceiptValue(order);
        }
    }

    /**
     * 处理退费时order的数据
     * @param order Order
     */
    public void dealBackReceiptValue(Order order) {
        order.modifyChargeFlg(false);
        order.modifyBillDate(null);
        order.modifyBillFlg("N");
        order.setReceiptNo("");
    }

    /**
     * 初始化票据带的医嘱
     * @param prescriptionList PrescriptionList
     */
    public void initOrderList(PrescriptionList prescriptionList) {
        //从处方签中取出符合本票据的orderList
        OrderList odList = prescriptionList.getOrderList(this);
        //放入本票据上
        this.setOrderList(odList);
    }
}
