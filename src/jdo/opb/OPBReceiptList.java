package jdo.opb;

import com.dongyang.data.TModifiedList;
import jdo.sys.Pat;
import jdo.reg.Reg;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import jdo.opd.PrescriptionList;
import jdo.opd.OrderList;
import java.util.Map;
import java.util.HashMap;
import jdo.bil.BIL;
import jdo.sys.SYSPublic;
import com.dongyang.util.StringTool;
import java.text.DecimalFormat;
import com.dongyang.util.TypeTool;

/**
 *
 * <p>Title: 票据list</p>
 *
 * <p>Description: </p>
 *
 * Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author fudw
 * @version 1.0
 */

public class OPBReceiptList
    extends TModifiedList {
    //取当前时间
    Timestamp date = SystemTool.getInstance().getDate();
    /**
     * 票据档
     */
    private OPBReceipt receiptOne;
    private Pat pat;
    private Reg reg;
    public void setPat(Pat pat){
    this.pat=pat;
    }
    public Pat getPat(){
    return this.pat;
    }
    public void setReg(Reg reg){
    this.reg = reg;
    }
    public Reg getReg(){
    return this.reg;
    }

    /**
     * 设置一张票据
     * @param OpbReceipt OPBReceipt
     */
    public void setOPBReceipt(OPBReceipt OpbReceipt) {
        this.receiptOne = OpbReceipt;
    }

    /**
     * 得到一张票据
     * @return OPBReceipt
     */
    public OPBReceipt getOPBReceipt() {
        return this.receiptOne;
    }


    /**
     * 构造器
     */
    public OPBReceiptList() {
        setMapString("caseNo:CASE_NO;receiptNo:RECEIPT_NO;admType:ADM_TYPE;region:REGION_CODE;mrNo:MR_NO;resetReceiptNo:RESET_RECEIPT_NO;"
                     + "printNo:PRINT_NO;billDate:BILL_DATE;chargeDate:CHARGE_DATE;printDate:PRINT_DATE;charge01:CHARGE01;charge02:CHARGE02;"
                     + "charge03:CHARGE03;charge04:CHARGE04;charge05:CHARGE05;charge06:CHARGE06;charge07:CHARGE07;charge08:CHARGE08;charge09:CHARGE09;"
                     + "charge10:CHARGE10;charge11:CHARGE11;charge12:CHARGE12;charge13:CHARGE13;charge14:CHARGE14;charge15:CHARGE15;"
                     + "charge16:CHARGE16;charge17:CHARGE17;charge18:CHARGE18;charge19:CHARGE19;charge20:CHARGE20;charge21:CHARGE21;"
                     + "charge22:CHARGE22;charge23:CHARGE23;charge24:CHARGE24;charge25:CHARGE25;charge26:CHARGE26;charge27:CHARGE27;"
                     + "charge28:CHARGE28;charge29:CHARGE29;charge30:CHARGE30;totAmt:TOT_AMT;reduceReason:REDUCE_REASON;reduceAmt:REDUCE_AMT;"
                     + "reduceTime:REDUCE_DATE;reduceDeptCode:REDUCE_DEPT_CODE;reduceRespond:REDUCE_RESPOND;arAmt:AR_AMT;payCash:PAY_CASH;"
                     + "payMedicalCard:PAY_MEDICAL_CARD;payBankCard:PAY_BANK_CARD;payInsCard:PAY_INS_CARD;payCheck:PAY_CHECK;"
                     + "payDepit:PAY_DEBIT;payBilPay:PAY_BILPAY;payIns:PAY_INS;payOther1:PAY_OTHER1;payOther2:PAY_OTHER2;payRemark:PAY_REMARK;"
                     + "cashierCode:CASHIER_CODE;accountFlg:ACCOUNT_FLG;accountDate:ACCOUNT_DATE;accountUser:ACCOUNT_USER;"
                     + "contractCode:ACCOUNT_CODE;bankSeq:BANK_SEQ;bankDate:BANK_DATE;bankUser:BANK_USER;optUser:OPT_USER;"
                     + "optDate:OPT_DATE;optTerm:OPT_TERM;");
    }

    /**
     * 新增票据
     * @return OPBReceipt
     */
    public OPBReceipt newReceipt() {
        OPBReceipt newReceipt = new OPBReceipt();
        //就诊序号
        newReceipt.setCaseNo(reg.caseNo());
        //票据号
//                newReceipt.setReceiptNo();
        //门急住别
        newReceipt.setAdmType(reg.getAdmType());
        //区域
        newReceipt.setRegion(Operator.getRegion());

        //病案号
        newReceipt.setMrNo(pat.getMrNo());
        //红虫收据号
//                newReceipt.setResetReceiptNo();
        //打印的票据号
//                newReceipt.setPrintNo();
        //收费时间
        newReceipt.setBilDate(date);
        //计费时间
        newReceipt.setChargeDate(date);
        //大票时间
        newReceipt.setPrintDate(date);
        //费用1~~30默认全0
        newReceipt.setCharge01(0);
        newReceipt.setCharge02(0);
        newReceipt.setCharge03(0);
        newReceipt.setCharge04(0);
        newReceipt.setCharge05(0);
        newReceipt.setCharge06(0);
        newReceipt.setCharge08(0);
        newReceipt.setCharge09(0);
        newReceipt.setCharge10(0);
        newReceipt.setCharge11(0);
        newReceipt.setCharge12(0);
        newReceipt.setCharge13(0);
        newReceipt.setCharge14(0);
        newReceipt.setCharge15(0);
        newReceipt.setCharge16(0);
        newReceipt.setCharge17(0);
        newReceipt.setCharge18(0);
        newReceipt.setCharge19(0);
        newReceipt.setCharge20(0);
        newReceipt.setCharge21(0);
        newReceipt.setCharge22(0);
        newReceipt.setCharge23(0);
        newReceipt.setCharge24(0);
        newReceipt.setCharge25(0);
        newReceipt.setCharge26(0);
        newReceipt.setCharge27(0);
        newReceipt.setCharge28(0);
        newReceipt.setCharge29(0);
        newReceipt.setCharge30(0);
        //总金额
        newReceipt.setTotAmt(0);
//                newReceipt.setReduceDeptCode();
//                newReceipt.setReduceReason();
        //折扣金额
        newReceipt.setReduceAmt(0);
        newReceipt.setReduceDate(date);
//                newReceipt.setReduceRespond();
        //自费金额
        newReceipt.setArAmt(0);
        //现金
        newReceipt.setPayCash(0);
        //医疗卡
        newReceipt.setPayMedicalCard(0);
        //刷卡
        newReceipt.setPayBankCard(0);
        //医保卡
        newReceipt.setPayInsCard(0);
        //支票
        newReceipt.setPayCheck(0);
        //记账
        newReceipt.setPayDepit(0);
        //押金
        newReceipt.setPayBilPay(0);
        //医保
        newReceipt.setPayIns(0);
        //其它支付1
        newReceipt.setPayOther1(0);
        //其它支付2
        newReceipt.setPayOther2(0);
//                newReceipt.setPayRemark();
        newReceipt.setCashierCode(Operator.getID());
        newReceipt.setOptUser(Operator.getID());
        newReceipt.setOptDate(date);
        newReceipt.setOptTerm(Operator.getIP());
        //对象中添加一条新的票据
        add(newReceipt);
        return newReceipt;
    }

    /**
     * 根据传入的tparm组建票据list
     * @param parm TParm
     * @return OPBReceipt
     */
    public OPBReceiptList InitOpbReceiptList(TParm parm) {
        int count = parm.getCount();
        //如果无数据
        if (parm.getCount() <= 0)
            return null;
        OPBReceiptList opbReceiptList=new OPBReceiptList();
        //循环所有数据
        for (int i = 0; i < count; i++) {
            //新建票据对象
            OPBReceipt opbReceiptOne =getReceiptByTParm(parm, i);
            //给list添加一个对象
            opbReceiptList.add(i,opbReceiptOne);
        }

    return opbReceiptList;
    }
    /**
     * 传入一个TParm返回一个票据对象
     * @param parm TParm
     * @param row int
     * @return OPBReceipt
     */
    public OPBReceipt getReceiptByTParm(TParm parm, int row) {
        OPBReceipt opbReceiptOne = new OPBReceipt();
        //就诊序号
        opbReceiptOne.setCaseNo(parm.getValue("CASE_NO", row));
        //票据号
        opbReceiptOne.setReceiptNo(parm.getValue("RECEIPT_NO", row));
        //门急住别
        opbReceiptOne.setAdmType(parm.getValue("ADM_TYPE", row));
        //区域
        opbReceiptOne.setRegion(parm.getValue("REGION", row));
        //病案号
        opbReceiptOne.setMrNo(parm.getValue("MR_NO", row));
        //红虫收据号
        opbReceiptOne.setResetReceiptNo(parm.getValue("RESET_RECEIPT_NO", row));
        //打印的票据号
        opbReceiptOne.setPrintNo(parm.getValue("PRINT_NO", row));
        //收费时间
        opbReceiptOne.setBilDate(parm.getTimestamp("BILL_DATE", row));
        //计费时间
        opbReceiptOne.setChargeDate(parm.getTimestamp("CHARGE_DATE", row));
        //大票时间
        opbReceiptOne.setPrintDate(parm.getTimestamp("PRINT_DATE", row));
        //收费项目明细1~30
        opbReceiptOne.setCharge01(parm.getDouble("CHARGE01", row));
        opbReceiptOne.setCharge02(parm.getDouble("CHARGE02", row));
        opbReceiptOne.setCharge03(parm.getDouble("CHARGE03", row));
        opbReceiptOne.setCharge04(parm.getDouble("CHARGE04", row));
        opbReceiptOne.setCharge05(parm.getDouble("CHARGE05", row));
        opbReceiptOne.setCharge06(parm.getDouble("CHARGE06", row));
        opbReceiptOne.setCharge07(parm.getDouble("CHARGE07", row));
        opbReceiptOne.setCharge08(parm.getDouble("CHARGE08", row));
        opbReceiptOne.setCharge09(parm.getDouble("CHARGE09", row));
        opbReceiptOne.setCharge10(parm.getDouble("CHARGE10", row));
        opbReceiptOne.setCharge11(parm.getDouble("CHARGE11", row));
        opbReceiptOne.setCharge12(parm.getDouble("CHARGE12", row));
        opbReceiptOne.setCharge13(parm.getDouble("CHARGE13", row));
        opbReceiptOne.setCharge14(parm.getDouble("CHARGE14", row));
        opbReceiptOne.setCharge15(parm.getDouble("CHARGE15", row));
        opbReceiptOne.setCharge16(parm.getDouble("CHARGE16", row));
        opbReceiptOne.setCharge17(parm.getDouble("CHARGE17", row));
        opbReceiptOne.setCharge18(parm.getDouble("CHARGE18", row));
        opbReceiptOne.setCharge19(parm.getDouble("CHARGE19", row));
        opbReceiptOne.setCharge20(parm.getDouble("CHARGE20", row));
        opbReceiptOne.setCharge21(parm.getDouble("CHARGE21", row));
        opbReceiptOne.setCharge22(parm.getDouble("CHARGE22", row));
        opbReceiptOne.setCharge23(parm.getDouble("CHARGE23", row));
        opbReceiptOne.setCharge24(parm.getDouble("CHARGE24", row));
        opbReceiptOne.setCharge25(parm.getDouble("CHARGE25", row));
        opbReceiptOne.setCharge26(parm.getDouble("CHARGE26", row));
        opbReceiptOne.setCharge27(parm.getDouble("CHARGE27", row));
        opbReceiptOne.setCharge28(parm.getDouble("CHARGE28", row));
        opbReceiptOne.setCharge29(parm.getDouble("CHARGE29", row));
        opbReceiptOne.setCharge30(parm.getDouble("CHARGE30", row));
        //TOT_AMT;REDUCE_AMT;AR_AMT;PAY_CASH;PAY_MEDICAL_CARD;
        //PAY_BANK_CARD;PAY_INS_CARD;PAY_CHECK;PAY_DEBIT;PAY_BILPAY;PAY_INS;PAY_OTHER1;PAY_OTHER2;CASHIER_CODE
        //各种支付方式
        opbReceiptOne.setTotAmt(parm.getDouble("TOT_AMT",row));
        opbReceiptOne.setReduceAmt(parm.getDouble("REDUCE_AMT",row));
        opbReceiptOne.setArAmt(parm.getDouble("AR_AMT",row));
        opbReceiptOne.setPayCash(parm.getDouble("PAY_CASH",row));
        opbReceiptOne.setPayMedicalCard(parm.getDouble("PAY_MEDICAL_CARD",row));
        opbReceiptOne.setPayBankCard(parm.getDouble("PAY_BANK_CARD",row));
        opbReceiptOne.setPayInsCard(parm.getDouble("PAY_INS_CARD",row));
        opbReceiptOne.setPayCheck(parm.getDouble("PAY_CHECK",row));
        opbReceiptOne.setPayDepit(parm.getDouble("PAY_DEBIT",row));
        opbReceiptOne.setPayBilPay(parm.getDouble("PAY_BILPAY",row));
        opbReceiptOne.setPayIns(parm.getDouble("PAY_INS",row));
        opbReceiptOne.setPayOther1(parm.getDouble("PAY_OTHER1",row));
        opbReceiptOne.setPayOther2(parm.getDouble("PAY_OTHER2",row));
        //支票号
        opbReceiptOne.setPayRemark(parm.getValue("PAY_REMARK", row));
        //收费员
        opbReceiptOne.setCashierCode(parm.getValue("CASHIER_CODE", row));
        //日结标记
        opbReceiptOne.setAccountFlg(parm.getValue("ACCOUNT_FLG", row));
        //日结日期
        opbReceiptOne.setAccountDate(parm.getTimestamp("ACCOUNT_DATE", row));
        //日结人员
        opbReceiptOne.setAccountUser(parm.getValue("ACCOUNT_USER", row));
        //
        opbReceiptOne.setContractCode(parm.getValue("CONTRACT_CODE", row));
        //银行交账号
        opbReceiptOne.setBankSeq(parm.getValue("BANK_SEQ", row));
        //交账时间
        opbReceiptOne.setBankDate(parm.getTimestamp("BANK_DATE", row));
        //交账人员
        opbReceiptOne.setBankUser(parm.getValue("BANK_USER", row));
        return opbReceiptOne;
    }

    /**
     *  删除一张票据
     * @param row int
     */
    public void deleteReceipt(int row) {
        this.removeData(row);

    }

    /**
     * 初始化对象列表
     * @param caseNo String
     * @return OPBReceiptList
     */
    public OPBReceiptList initReceiptList(String caseNo){
        //捞取收费数据
        TParm result=OPBReceiptTool.getInstance().getReceipt(caseNo);
        if(result.getErrCode()<0)
            return null;
        int row=result.getCount();
        if(row<=0)
            return null;
        return InitOpbReceiptList(result);
    }
    /**
     * 初始化对象列表:查询病患的票据列表：记账查找，没有执行结算操作的数据退费
     * @param caseNo String
     * @return OPBReceiptList
     */
    public OPBReceiptList initContractReceiptList(String caseNo){
        //捞取收费数据
        TParm result=OPBReceiptTool.getInstance().getContractReceipt(caseNo);
        if(result.getErrCode()<0)
            return null;
        int row=result.getCount();
        if(row<=0)
            return null;
        return InitOpbReceiptList(result);
    }

    /**
     * 票据分票
     * @param prescriptionList PrescriptionList
     */
    public void initOrder(PrescriptionList prescriptionList) {
        int count=this.size();
        for(int i=0;i<count;i++){
            OPBReceipt opbReceipt=(OPBReceipt)this.get(i);
            opbReceipt.initOrderList(prescriptionList);
        }
    }
    /**
     * 得到费用清单
     * @return TParm
     */
    public TParm getOrderParm(){
        //记录总的parm
        TParm parm=new TParm();
        parm.setCount(0);
        //每张票据上的orderparm
        TParm parmOrder;
        //票据张数
        int count=this.size();
        for(int i=0;i<count;i++){
            //顺序取出其中的票据
            OPBReceipt opbReceipt=(OPBReceipt)this.get(i);
            //得到其中的parm
            parmOrder = opbReceipt.getOrderList().getParm(OrderList.PRIMARY);
            parm=addNewRow(parm,parmOrder);
        }
        return parm;
    }
    /**
     * 把一个parm中的所有数据放入另一个parm
     * @param parmMain TParm
     * @param parmDetial TParm
     * @return TParm
     */
    public TParm addNewRow(TParm parmMain,TParm parmDetial){
        //主Parma的行数
        int rowMain=parmMain.getCount();
        //明细parm的行数
        int rowDetial=parmDetial.getCount();
        for(int i=0;i<rowDetial;i++){
            //主行的最后添加一行添加一条明细parm的第i行
            parmMain.addRowData(parmDetial,i);
        }
        parmMain.setCount(rowMain+rowDetial);
        return parmMain;
    }
    /**
     * 处理医嘱列表..是为了费用清单使用
     *   主要是按费用类别归类
     * @param parmIn TParm
     * @return TParm
     */
    public TParm dealTParm(TParm parmIn,TParm batchParm){
        BIL bil=new BIL();
        //费用类别代码和名称的对照
        Map chargeToRexpMap=bil.getChargeToRexpdDescMap();
        if(chargeToRexpMap==null)
            return null;
        //得到单位代码map
        Map unitMap=SYSPublic.getUnitMap();
        //存储处理完成后的数据
        TParm parmOut=new TParm();
        //存储收费类别
        Map rexpMap=new HashMap();
        //传入数据行数
        int rowCount=parmIn.getCount();
        DecimalFormat df = new DecimalFormat("0.00");
        double sum=0.00;//总计
        for(int i=0;i<rowCount;i++){
            //取出收费类别
            String rexpCode=parmIn.getValue("REXP_CODE",i);
            if(rexpMap.get(rexpCode)==null){
                rexpMap.put(rexpCode,"Y");
               for(int j=i;j<rowCount;j++){
                   String rexpCodeNew=parmIn.getValue("REXP_CODE",j);
                   if(rexpCodeNew.equals(rexpCode)){
                       int row=parmOut.insertRow();
                       //===============pangben 20111014 start
                       //收费项目
                       //parmOut.setData("REXP_CODE",row,chargeToRexpMap.get(parmIn.getData("REXP_CODE",j)));
                       //名称 规格
                       boolean istrue=false;
                       //批号检索
                       for(int index=0;index<batchParm.getCount();index++){
                           if(batchParm.getValue("ORDER_CODE",index).equals(parmIn.getValue("ORDER_CODE",j))){
                              parmOut.setData("BATCH_NO",row,batchParm.getValue("BATCH_NO",index));
                              istrue=true;
                              break;
                           }
                       }
                       parmOut.setData("ORDER_DESC",row,parmIn.getValue("ORDER_DESC",j));
                       //批号
                       if(!istrue){
                           parmOut.setData("BATCH_NO", row,
                                           "");
                       }
                       //剂型
                       parmOut.setData("DOSE_CHN_DESC",row,parmIn.getValue("DOSE_CHN_DESC",j));
                       //规格
                       parmOut.setData("SPECIFICATION",row,parmIn.getValue("SPECIFICATION",j));
                       //增负
                       parmOut.setData("AL",row,parmIn.getData("AL",j));
                       //总量
                       parmOut.setData("DOSAGE_QTY",row,parmIn.getData("DOSAGE_QTY",j));
                       //单位
                       //parmOut.setData("UNIT_CHN_DESC",row,TypeTool.getString(unitMap.get(parmIn.getValue("DOSAGE_UNIT",j))));
                       //单价
                       parmOut.setData("OWN_PRICE",row,df.format(StringTool.round(parmIn.getDouble("OWN_PRICE",j),2)));
                       //价格
                       parmOut.setData("AR_AMT",row,df.format(StringTool.round(parmIn.getDouble("AR_AMT",j),2)));
                       sum += StringTool.round(parmIn.getDouble("AR_AMT",j),3);
                       //===============pangben 20111014 stop
                   }
               }
            }
        }
        //===============pangben 20111014 start
        parmOut.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        parmOut.addData("SYSTEM", "COLUMNS", "BATCH_NO");
        parmOut.addData("SYSTEM", "COLUMNS", "DOSE_CHN_DESC");
        parmOut.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        parmOut.addData("SYSTEM", "COLUMNS", "AL");
        parmOut.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
        parmOut.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        parmOut.addData("SYSTEM", "COLUMNS", "AR_AMT");
        //===============pangben 20111014 stop
        parmOut.setCount(rowCount);
        parmOut.setData("SUM",sum);
        return parmOut;
    }
}
