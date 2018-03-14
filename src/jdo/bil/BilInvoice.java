package jdo.bil;

import com.dongyang.data.TModifiedData;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title: 票据管理对象</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author fudw
 * @version 1.0
 */
public class BilInvoice
    extends TModifiedData {
    /**
     * 收据类别 RECP_TYPE
     */
    private String recpType;
    /**
     * 起始票号 START_INVNO
     */
    private String startInvno;
    /**
     * 领用日期 START_VALID_DATE
     */
    private String startValidDate;
    /**
     * 结束票号 END_INVNO
     */
    private String endInvno;
    /**
     * 当前票号 UPDATE_NO
     */
    private String updateNo;
    /**
     * 领用人员 CASHIER_CODE
     */
    private String cashierCode;
    /**
     * 交回日期 END_VALID_DATE
     */
    private Timestamp endValidDate;
    /**
     * 使用状态 STATUS
     */
    private String status;
    /**
     * 使用者IP
     */
    private String termIP;

    // 构造器
    public BilInvoice() {
        StringBuffer sb = new StringBuffer();
        //收据类别
        sb.append("recpType:RECP_TYPE;");
        //起始票号
        sb.append("startInvno:START_INVNO;");
        //领用日期
        sb.append("startValidDate:START_VALID_DATE;");
        //结束票号
        sb.append("endInvno:END_INVNO;");
        //当前票号
        sb.append("updateNo:UPDATE_NO;");
        //领用人员
        sb.append("cashierCode:CASHIER_CODE;");
        //交回日期
        sb.append("endValidDate:END_VALID_DATE;");
        //使用状态
        sb.append("status:STATUS;");
        sb.append("termIP:TERM_IP;");
        setMapString(sb.toString());
    }

    /**
     * 带参初始化
     * @param recpType String
     */
    public BilInvoice(String recpType) {

        TParm parm = new TParm();
        parm.setData("RECP_TYPE", recpType);
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("STATUS", "0");
        parm.setData("TERM_IP", Operator.getIP());
        TParm result = BILInvoiceTool.getInstance().selectNowReceipt(parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
        }
        this.setRecpType(result.getValue("RECP_TYPE", 0));
        this.setUpdateNo(result.getValue("UPDATE_NO", 0));
        this.setCashierCode(result.getValue("CASHIER_CODE", 0));
        this.setEndInvno(result.getValue("END_INVNO", 0));
        this.setStartInvno(result.getValue("START_INVNO", 0));
        this.setStatus(result.getValue("STATUS", 0));
        this.setTermIP(result.getValue("TERM_IP", 0));

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
     * 设置起始票号
     * @param startInvno String
     */
    public void setStartInvno(String startInvno) {
        this.startInvno = startInvno;
    }

    /**
     * 得到起始票号
     * @return String
     */
    public String getStartInvno() {
        return startInvno;
    }

    /**
     * 设置领用日期
     * @param startValidDate String
     */
    public void setStartValidDate(String startValidDate) {
        this.startValidDate = startValidDate;
    }

    /**
     * 得到领用日期
     * @return String
     */
    public String getStartValidDate() {
        return startValidDate;
    }

    /**
     * 设置结束票号
     * @param endInvno String
     */
    public void setEndInvno(String endInvno) {
        this.endInvno = endInvno;
    }

    /**
     * 得到结束票号
     * @return String
     */
    public String getEndInvno() {
        return endInvno;
    }

    /**
     * 设置当前票号
     * @param updateNo String
     */
    public void setUpdateNo(String updateNo) {
        this.updateNo = updateNo;
    }

    /**
     * 得到当前票号
     * @return String
     */
    public String getUpdateNo() {
        return updateNo;
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
     * 设置交回日期
     * @param endValidDate Timestamp
     */
    public void setEndValidDate(Timestamp endValidDate) {
        this.endValidDate = endValidDate;
    }

    /**
     * 得到交回日期
     * @return String
     */
    public Timestamp getEndValidDate() {
        return endValidDate;
    }

    /**
     * 设置使用状态
     * @param status String
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 得到使用状态
     * @return String
     */

    public String getStatus() {
        return status;
    }

    /**
     * 设置使用者IP
     * @param termIP String
     */
    public void setTermIP(String termIP) {
        this.termIP = termIP;
    }

    /**
     * 得到使用者IP
     * @return String
     */
    public String getTermIP() {
        return termIP;
    }

    /**
     * 得到加载参数
     * @return TParm
     */
    public TParm getParm() {
        TParm parm = super.getParm();
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        return parm;
    }

    /**
     * 初始化当前使用的票据
     * @param receType String
     * @return BilInvoice
     */
    public BilInvoice initBilInvoice(String receType) {
        TParm parm = new TParm();
        parm.setData("RECP_TYPE", receType);
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("STATUS", "0");
        parm.setData("TERM_IP", Operator.getIP());
        TParm result = BILInvoiceTool.getInstance().selectNowReceipt(parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return null;
        }
        BilInvoice bilInvoice = new BilInvoice();
        bilInvoice.setRecpType(result.getValue("RECP_TYPE", 0));
        bilInvoice.setUpdateNo(result.getValue("UPDATE_NO", 0));
        bilInvoice.setCashierCode(result.getValue("CASHIER_CODE", 0));
        bilInvoice.setEndInvno(result.getValue("END_INVNO", 0));
        bilInvoice.setStartInvno(result.getValue("START_INVNO", 0));
        bilInvoice.setStatus(result.getValue("STATUS", 0));
        bilInvoice.setTermIP(result.getValue("TERM_IP", 0));
        return bilInvoice;
    }

    /**
     * 检核票据有效性
     * @return boolean
     */
    public boolean checkEndInvNo() {
        if (this.getUpdateNo() == null || this.getUpdateNo().length() == 0 ||
            this.getUpdateNo().equals("null")) {
            return false;
        }
//        if(this.getUpdate)
        return true;
    }
}
