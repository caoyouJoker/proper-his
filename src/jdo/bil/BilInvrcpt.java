package jdo.bil;

import com.dongyang.data.TModifiedData;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import com.dongyang.data.TimestampValue;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
/**
 *
 * <p>Title:票据管理对象 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis </p>
 *
 * @author fudw
 * @version 1.0
 */
public class BilInvrcpt
    extends TModifiedData{
    /**
     * 收据类别 RECP_TYPE
     */
    private String recpType;
    /**
     * 票据号 INV_NO
     */
    private String invNo;
    /**
     * 收据号 RECEIPT_NO
     */
    private String receiptNo;
    /**
     * 领用人员 CASHIER_CODE
     */
    private String cashierCode;

    /**
     * 总金额 TOT_AMT
     */
    private double arAmt;
    /**
     * 取消注记 CANCEL_FLG
     */
    private String cancelFlg;

    /**
     * 取消人员 CANCEL_USER
     */
    private String cancelUser;
    /**
     * 取消日期 CANCEL_DATE
     */
    private Timestamp cancelDate;

    // 构造器
    public BilInvrcpt() {
        StringBuffer sb = new StringBuffer();
        //收据类别
        sb.append("recpType:RECP_TYPE;");
        //票据号
        sb.append("invNo:INV_NO;");
        //收据号
        sb.append("receiptNo:RECEIPT_NO;");
        //领用人员
        sb.append("cashierCode:CASHIER_CODE;");
        //总金额
        sb.append("arAmt:AR_AMT;");
        //取消注记
        sb.append("cancelFlg:CANCEL_FLG;");
        //取消人员
        sb.append("cancelUser:CANCEL_USER;");
        //取消日期
        sb.append("cancelDate:CANCEL_DATE;");
        setMapString(sb.toString());
    }
    /**
     * 设置收据类别
     * @param recpType String
     */
    public void setRecpType(String recpType) {
        this.recpType = recpType;
    }
    /**
     * 得到收据类别
     * @return String
     */
    public String getRecpType() {
        return recpType;
    }
    /**
     * 设置票据号
     * @param invNo String
     */
    public void setInvNo(String invNo) {
        this.invNo = invNo;
    }
    /**
     * 得到票据号
     * @return String
     */
    public String getInvNo() {
        return invNo;
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
     * 设置领用人员
     * @param cashierCode String
     */
    public void setCashierCode(String cashierCode) {
        this.cashierCode = cashierCode;
    }
    /**
     * 得到领用人员
     * @return String
     */
    public String getCashierCode() {
        return cashierCode;
    }
    /**
     * 设置总金额
     * @param arAmt double
     */
    public void setArAmt(double arAmt) {
        this.arAmt = arAmt;
    }
    /**
     * 得到总金额
     * @return double
     */
    public double getArAmt() {
        return arAmt;
    }
    /**
     * 设置取消注记
     * @param cancelFlg String
     */
    public void setCancelFlg(String cancelFlg) {
        this.cancelFlg = cancelFlg;
    }
    /**
     * 得到取消注记
     * @return String
     */
    public String getCancelFlg() {
        return cancelFlg;
    }
    /**
     *  设置取消人员
     * @param cancelUser String
     */
    public void setCancelUser(String cancelUser) {
        this.cancelUser = cancelUser;
    }
    /**
     * 得到取消人员
     * @return String
     */
    public String getCancelUser() {
        return cancelUser;
    }
    /**
     * 设置取消日期
     * @param cancelDate Timestamp
     */
    public void setCancelDate(Timestamp cancelDate) {
        this.cancelDate = cancelDate;
    }
    /**
     * 得到取消日期
     * @return String
     */

    public Timestamp getCancelDate() {
        return cancelDate;
    }
    /**
     * 得到加载参数
     * @return TParm
     */
    public TParm getParm() {
        TParm parm = super.getParm();
        parm.setData("OPT_DATE",  SystemTool.getInstance().getDate());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        return parm;
    }
    /**
     * RECP_TYPE
     * @param recpType String
     * @param invNo String
     * @return boolean
     */
    public boolean initBilInvrcpt( String recpType,String invNo) {
        if (recpType == null ||
            recpType.length() == 0||invNo==null||invNo.length()==0) {
            out("getOneInv进参错误");
            return false;
        }
        TParm parm = new TParm();
        parm.setData("RECP_TYPE", recpType);
        parm.setData("INV_NO",invNo);
        TParm result = BILInvrcptTool.getInstance().getOneInv(parm);
        if (result.getErrCode() < 0) {
            out("getOneInv返回错误！");
            return false;
        }
        if(result.getCount("RECEIPT_NO")>1){
            out("getOneInv返回错误！");
            return false;
        }
        if (result.getCount() == 1)
            return true;
        return false;
    }
    /**
     * 根据传入数据组装一个票据对象
     * @param bilInvrcpt BilInvrcpt
     * @param parm TParm
     * @return BilInvrcpt
     */
    public BilInvrcpt setValueForParm(BilInvrcpt bilInvrcpt,TParm parm){
//        System.out.println("添加数据parm="+parm);
        //收据类别
        bilInvrcpt.setRecpType(parm.getValue("RECP_TYPE"));
        //票据号
        bilInvrcpt.setInvNo(parm.getValue("INV_NO"));
        //收据号
        bilInvrcpt.setReceiptNo(parm.getValue("RECEIPT_NO"));
        //领用人员
        bilInvrcpt.setCashierCode(parm.getValue("CASHIER_CODE"));
        //总金额
        bilInvrcpt.setArAmt(parm.getDouble("AR_AMT"));
        //取消注记
        bilInvrcpt.setCancelFlg(parm.getValue("CANCEL_FLG"));
        //取消人员
        bilInvrcpt.setCancelUser(parm.getValue("CANCEL_USER"));
        //取消日期
        bilInvrcpt.setCancelDate((Timestamp)parm.getData("CANCEL_DATE"));
//        System.out.println("放入parm后的数据============="+bilInvrcpt.getParm());
        return bilInvrcpt;
    }
}
