package com.javahis.ui.hrm;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import jdo.bil.BilInvoice;
import jdo.hrm.HRMBill;
import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMContractD;
import jdo.hrm.HRMInvRcp;
import jdo.hrm.HRMOpbReceipt;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMPackageD;
import jdo.hrm.HRMPatAdm;
import jdo.hrm.HRMPatInfo;
import jdo.hrm.HRMPrePay;
import jdo.opb.OPBReceiptTool;
import jdo.reg.REGSysParmTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatHRMCompany;
import com.javahis.util.StringUtil;

/**
 * <p> Title: 健康检查团体缴费 </p>
 *
 * <p> Description: 健康检查团体缴费 </p>
 *
 * <p> Copyright: javahis 20090922 </p>
 *
 * <p> Company:JavaHis </p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMChargeControl extends TControl {

    // 收费TABLE、帐务TABLE、医嘱TABLE
    private TTable feeTable, billTable, billDetailTable;
    // 合同、团体TEXTFORMAT
    private TTextFormat contract, company;
    // 合同细相对象
    private HRMContractD contractD;
    // 预交金对象
    private HRMPrePay prePay;
    // 医嘱对象
    private HRMOrder order;
    // 病患对象
    private HRMPatInfo pat;
    // 报到对象
    private HRMPatAdm adm;
    // 票据对象
    private HRMOpbReceipt receipt;
    // 套餐细相对象
    private HRMPackageD packageD;
    // 发票对象
    private HRMInvRcp invRcp;
    // 票据管理对象
    private BilInvoice bilInvoice;
    // 账单对象
    private HRMBill bill;
    // 下一票号
    private String updateNo;
    // 默认支付方式
    private String payType;
    //账单列表
    private String billNoList;//add by wanglong 20130510
    // 发票号
    private String receiptNo;
    // 账单列表
    private TParm billParm;// add by wanglong 20130324

    /**
     * 初始化事件
     */
    public void onInit() {
        super.onInit();
        // 初始化控件
        initComponent();
        // 清空界面
        onClear();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        contractD = new HRMContractD();
        order = new HRMOrder();
        order.onQuery();
        bill = new HRMBill();
        bill.onQuery();
        receipt = new HRMOpbReceipt();
        invRcp = new HRMInvRcp();
        billParm = new TParm();// add by wanglong 20130324
//         查询默认支付方式
        TParm regParm = REGSysParmTool.getInstance().selPayWay();
        if (regParm.getErrCode() != 0) {
            this.messageBox_("初始化参数失败");
            return;
        }
        payType = regParm.getValue("DEFAULT_PAY_WAY", 0);
//        payType = "PAY_CASH";// 现金支付
        this.setValue("PAY_TYPE", "PAY_CASH");
        
        // add by wangb 2016/06/23 团体名称需要根据不同登录角色筛选
		String roleType = "";
		if (this.getPopedem("SYSDBA")) {
			roleType = HRMCompanyTool.ALL_ROLE_TYPE.replace(",", "','");
		} else {
			roleType = this.getPopedemParm().getValue("ID", 0);
		}
        
		// 查询团体信息
		TParm companyData = HRMCompanyTool.getInstance().selectCompanyComboByRoleType(roleType);
        company.setPopupMenuData(companyData);
        company.setComboSelectRow();
        company.popupMenuShowData();
    }

    /**
     * 初始化控件
     */
    private void initComponent() {
        company = (TTextFormat) this.getComponent("COMPANY_CODE");
        contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
        billTable = (TTable) this.getComponent("BILL_TABLE");
        billDetailTable = (TTable) this.getComponent("BILL_DETAIL_TABLE");
        billTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onPatCheckBox");// 点击账单后显示其人员账单明细 add by wanglong 20130510
    }

    /**
     * 清空事件
     */
    public void onClear() {
        billTable.removeRowAll();
        billDetailTable.removeRowAll();
        this.setValue("COMPANY_CODE", "");
        this.setValue("CONTRACT_CODE", "");
        this.setValue("PRINT_NO", "");
        this.clearValue("MR_NO;PAT_NAME;ID_NO;UPDATE_NO");
        this.setValue("AR_AMT", 0);// 应收
        this.setValue("TOT_AMT", 0);// 实收
        this.setValue("CHARGE", 0);// 找零
        this.setValue("NOPRINT", "");// 不打印收据
        this.setValue("PAY_TYPE", "PAY_CASH");
        this.setValue("PAY_REMARK", "");//支票备注
        this.setValue("CHECK_NO", "");//支票号 add by wanglong 20130716
        String noPrint = this.getValueString("NOPRINT");
        if (!"Y".equals(noPrint)) {
            // 取得下一票号，赋值到界面上
            bilInvoice = new BilInvoice("OPB");
            updateNo = getNextUpdateNo();
        } else {
            bilInvoice = new BilInvoice("OPB");
            updateNo = bilInvoice.getUpdateNo();
        }
        billNoList="";//modify by wanglong 20130510
        receiptNo = "";
        this.callFunction("UI|BUT_FEE|setEnabled", true);
        this.callFunction("UI|delete|setEnabled", false);
        this.callFunction("UI|print|setEnabled", false);
        this.callFunction("UI|prePay|setEnabled", true);
        // this.setValue("ALL", "Y");
        this.setValue("UNFEE", "Y");//add by wanglong 20130510
        this.callFunction("UI|ALL_CHOOSE|setEnabled", true);
        // 初始化数据，将数据赋到界面上
        initData();
    }

    /**
     * 退费后清空事件
     */
    public void onClearAfterDischarge() {
        billTable.removeRowAll();
        billDetailTable.removeRowAll();
        this.setValue("COMPANY_CODE", "");
        this.setValue("CONTRACT_CODE", "");
        this.setValue("PAT_NAME", "");
        this.clearValue("MR_NO;ID_NO;UPDATE_NO");
        this.setValue("AR_AMT", 0);
        this.setValue("TOT_AMT", 0);
        this.setValue("CHARGE", 0);
        billNoList = "";//modify by wanglong 20130510
        receiptNo = "";
        this.callFunction("UI|BUT_FEE|setEnabled", true);
        this.callFunction("UI|delete|setEnabled", false);
        this.callFunction("UI|print|setEnabled", false);
        this.callFunction("UI|prePay|setEnabled", true);
        // 初始化数据，将数据赋到界面上
        initData();
    }

    /**
     * 刷新界面
     */
    public void onRefreshUI() {// add by wanglong 20130329
        int billlRow = billTable.getSelectedRow();
        if (billlRow < 0) {
            onClear();
            return;
        }
        if (billParm == null || billParm.getCount() < 1) {
            onClear();
            return;
        }
        String billNo = billParm.getValue("BILL_NO", billlRow);
        onStateChoose();
        if (billParm == null || billParm.getCount() < 1) {
            onClear();
            return;
        }
        int i = 0;
        for (; i < billParm.getCount(); i++) {
            if (billNo.equals(billParm.getValue("BILL_NO", i))) {
                break;
            }
        }
        if (i == billParm.getCount()) {
            onClear();
            return;
        }
        this.clearValue("AR_AMT;TOT_AMT;CHARGE;NOPRINT");
        this.setValue("PAY_TYPE", "PAY_CASH");
        this.callFunction("UI|BUT_FEE|setEnabled", false);
        billTable.setSelectedRow(i);
        onBillTableClick();
        String noPrint = this.getValueString("NOPRINT");
        if (!"Y".equals(noPrint)) {
            // 取得下一票号，赋值到界面上
            bilInvoice = new BilInvoice("OPB");
            updateNo = getNextUpdateNo();
        } else {
            bilInvoice = new BilInvoice("OPB");
            updateNo = bilInvoice.getUpdateNo();
        }
    }
    
    /**
     * 团体代码选择事件
     */
    public void onCompanyChoose() {
        String companyCode = this.getValueString("COMPANY_CODE");
        if (StringUtil.isNullString(companyCode)) {
            return;
        }
        // 根据团体代码查得该团体的合同主项
        TParm contractParm = contractD.onQueryByCompanyWithBlank(companyCode);//add by wanglong 20130510
        if (contractParm.getErrCode() != 0) {
            this.messageBox_("没有数据");
        }
        // 构造一个TTextFormat,将合同主项赋值给这个控件，取得最后一个合同代码赋值给这个控件初始值
        contract.setPopupMenuData(contractParm);
        contract.setComboSelectRow();
        contract.popupMenuShowData();
        String contractCode = contractParm.getValue("ID", 1);//modify by wanglong 20130510
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox_("查询失败");
            return;
        }
        contract.setValue(contractCode);
//        onContractChoose();// add by wanglong 20130324
    }

    /**
     * 合同代码选择事件
     */
    public void onContractChoose() {
    	onContractChooseResult(false);
    }
    
    /**
     * 合同代码选择结果
     * @param flg
     */
    public void onContractChooseResult(boolean flg){
    	//this.messageBox(""+flg);
    	String companyCode = this.getValueString("COMPANY_CODE");
    	if(!flg){
	        if (StringUtil.isNullString(companyCode)) {
	            this.messageBox_("团体名称不可为空");
	            return;
	        }
    	}
        String contractCode = this.getValueString("CONTRACT_CODE");
        // 根据团体代码和合同代码查得该合同的账单信息赋值在界面上
        String sql =//modify by wanglong 20130906
                "WITH AA AS (SELECT DISTINCT CASE WHEN A.REXP_FLG='Y' THEN 'Y' ELSE 'N' END REXP_FLG,A.RECEIPT_NO,A.COMPANY_CODE,"
                        + "         A.CONTRACT_CODE,A.BILL_NO,A.OWN_AMT,A.AR_AMT,A.DISCOUNT_AMT,A.CUT_AMT,C.PAT_NAME,C.MR_NO,"//modify by wanglong 20131111
                        + "         DENSE_RANK() OVER (PARTITION BY A.BILL_NO ORDER BY C.MR_NO) NUM "
                        + "    FROM HRM_BILL A, HRM_ORDER B, HRM_CONTRACTD C & "
                        + "   WHERE & A.BILL_NO = B.BILL_NO "
                        + "     AND B.MR_NO = C.MR_NO "
                        + "     # "
                        + "     # @ ) "
                        + "SELECT '' FLG,CASE WHEN AA.REXP_FLG='Y' THEN 'Y' ELSE 'N' END REXP_FLG,AA.RECEIPT_NO,AA.COMPANY_CODE,AA.CONTRACT_CODE,"
                        + "        WM_CONCAT(AA.PAT_NAME) PAT_NAME,WM_CONCAT(AA.MR_NO) MR_NO,AA.BILL_NO,AA.OWN_AMT,AA.DISCOUNT_AMT,"
                        + "        ROUND(AA.OWN_AMT-AA.DISCOUNT_AMT,2) AR_AMT,AA.CUT_AMT,AA.AR_AMT AS RECV_AMT "//add by wanglong 20131111
                        + "  FROM AA "
                        + " WHERE 1 = 1 "
                        + "   AND NUM < 50 "//空间有限，每个账单只显示前49人
                        + "GROUP BY AA.REXP_FLG,AA.RECEIPT_NO,AA.COMPANY_CODE,AA.CONTRACT_CODE,AA.BILL_NO,AA.OWN_AMT,AA.AR_AMT,AA.DISCOUNT_AMT,AA.CUT_AMT "
                        + "ORDER BY AA.RECEIPT_NO";
//        String sql = // modify by wanglong 20130510
//                "SELECT '' FLG,CASE WHEN A.REXP_FLG = 'Y' THEN 'Y' ELSE 'N' END REXP_FLG,A.RECEIPT_NO,A.COMPANY_CODE,A.CONTRACT_CODE,"
//                        + "WM_CONCAT(DISTINCT C.PAT_NAME) PAT_NAME,WM_CONCAT(DISTINCT C.MR_NO) MR_NO,"
//                        + "A.BILL_NO,A.OWN_AMT,A.AR_AMT,A.DISCOUNT_AMT "
//                        + " FROM HRM_BILL A, HRM_ORDER B, HRM_CONTRACTD C "
//                        + "WHERE A.BILL_NO = B.BILL_NO "
//                        + "  AND A.CONTRACT_CODE = B.CONTRACT_CODE "
//                        + "  AND B.MR_NO = C.MR_NO "
//                        + "  AND B.CONTRACT_CODE = C.CONTRACT_CODE "
//                        + "  AND A.COMPANY_CODE = C.COMPANY_CODE "
//                        + "  AND ROWNUM < 2000 "// 2000是医嘱（含细项）的数量，如果每个人开40个医嘱（含细项），则大概显示50个人。add by wanglong 20130829
//                                                // 这里限制人数显示是因为WM_CONCAT()方法有字符数限制。
//                        + "  AND A.COMPANY_CODE = '#' "
//                        + " # "
//                        + " @ "
//                        + "GROUP BY A.REXP_FLG,A.RECEIPT_NO,A.COMPANY_CODE,A.CONTRACT_CODE,A.BILL_NO,A.OWN_AMT,A.AR_AMT,A.DISCOUNT_AMT";
        if(!flg){
        	 sql = sql.replaceFirst("#", " AND A.COMPANY_CODE = "+companyCode);// modify by wanglong 20130829
        }else{
        	 sql = sql.replaceFirst("#", "");// modify by huangjw 2016/11/14
        }
       
        if (!StringUtil.isNullString(contractCode)) {
            sql = sql.replaceFirst("#", " AND A.CONTRACT_CODE = '" + contractCode + "' ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        if (this.getValueBoolean("UNFEE")) {// add by wanglong 20130324
            sql = sql.replaceFirst("@", " AND (A.REXP_FLG <> 'Y' OR A.REXP_FLG IS NULL) ");
        } else if (this.getValueBoolean("FEE")) {
            sql = sql.replaceFirst("@", " AND A.REXP_FLG = 'Y' ");
        } 
        
        //收据号 add by huangjw 20160615
        if(this.getValue("PRINT_NO")!=null && !"".equals(this.getValue("PRINT_NO"))){
        	sql = sql.replaceFirst("&", " ,BIL_OPB_RECP D ");
        	sql = sql.replaceFirst("&", " D.PRINT_NO = '"+this.getValue("PRINT_NO")+"' AND A.RECEIPT_NO = D.RECEIPT_NO AND A.CONTRACT_CODE = D.CASE_NO AND  ");
        }else{
        	sql = sql.replaceFirst("&", "");
        	sql = sql.replaceFirst("&", "");
        }
        // else {
        // sql = sql.replaceFirst("@", "");
        // }
//        System.out.println("--------onContractChoose------sql-------------"+sql);
        billParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (billParm.getErrCode() != 0) {
            this.messageBox("查询账单列表失败");
            return;
        }
        order = new HRMOrder();
        bill = new HRMBill();
        receipt = new HRMOpbReceipt();
        invRcp = new HRMInvRcp();
        billTable.setParmValue(billParm);
        billDetailTable.removeRowAll();
        this.clearValue("AR_AMT;TOT_AMT;CHARGE;NOPRINT");
        this.setValue("PAY_TYPE", "PAY_CASH");
        this.callFunction("UI|BUT_FEE|setEnabled", false);
        String noPrint = this.getValueString("NOPRINT");
        if (!"Y".equals(noPrint)) {
            // 取得下一票号，赋值到界面上
            bilInvoice = new BilInvoice("OPB");
            updateNo = getNextUpdateNo();
        } else {
            bilInvoice = new BilInvoice("OPB");
            updateNo = bilInvoice.getUpdateNo();
        }
    
    }
    /**
     * “未打票”、“已打票”、“全部”单选按钮事件
     */
    public void onStateChoose() {//modify by wanglong 20130510
        String contractCode = this.getValueString("CONTRACT_CODE");
        if (contractCode.equals("")) {
            return;
        }
        if (this.getValueBoolean("FEE")) {
            this.callFunction("UI|ALL_CHOOSE|setEnabled", false);
        } else {
            this.callFunction("UI|ALL_CHOOSE|setEnabled", true);
        }
        onContractChoose();
        // }
    }

    /**
     * 全选事件
     */
    public void onSelectAll() {//add by wanglong 20130510
        String select = getValueString("ALL_CHOOSE");
        TParm parm = billTable.getParmValue();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            parm.setData("FLG", i, select);
        }
        billTable.setParmValue(parm);
        billTable.setSelectedRow(0);
        this.onBillTableClick();
       // table.setSelectedRow(0);// add by wanglong 20130121
    }
    
    /**
     * 账单列表勾选事件：显示其中每个人的账单
     * 
     * @param obj
     * @return
     */
    public boolean onPatCheckBox(Object obj) {//add by wanglong 20130510
        TTable table = (TTable) obj;
        table.acceptText();
        if (billParm == null) {
            return true;
        }
        if (billParm.getCount() < 1) {
            this.messageBox("没有账单信息");
            return true;
        }
        if (this.getValueBoolean("FEE")) {// 对于已缴费的账单，只能单选
            int row = table.getSelectedRow();
            if (table.getItemString(row, "FLG").equals("Y")) {
                for (int i = 0; i < table.getRowCount(); i++) {
                    if (i != row) {
                        table.setValueAt("N", i, 0);
                    }
                }
            }
        }
        this.onBillTableClick();
        return false;
    }
    
    /**
     * 账单TABLE单击事件
     */
    public void onBillTableClick() {//modify by wanglong 20130510
        billNoList = "";
        HashSet<String> billSet=new HashSet<String>();
        for (int i = 0; i < billParm.getCount(); i++) {
            if (billTable.getItemString(i, "FLG").equals("Y")
                    && StringUtil.isNullString(billParm.getValue("BILL_NO", i))) {
                this.messageBox_("结算单号为空");
                return;
            } else if (billTable.getItemString(i, "FLG").equals("Y")) {
                billSet.add(billParm.getValue("BILL_NO", i));
                // billNoList += "'" + billParm.getValue("BILL_NO", i) + "',";
                if (this.getValueBoolean("FEE")) {
                    for (int j = 0; j < billParm.getCount(); j++) {
                        if ((j != i)
                                && billParm.getValue("RECEIPT_NO", j)
                                        .equals(billParm.getValue("RECEIPT_NO", i))) {
                            billSet.add(billParm.getValue("BILL_NO", j));
                            billTable.setItem(j, "FLG", true);
                        }
                    }
                }
            }
        }
//        if(billNoList.equals("")){
        if (billSet.size() == 0) {
            billDetailTable.removeRowAll();
            this.setValue("PAT_NAME", "");
            this.clearValue("MR_NO;ID_NO;UPDATE_NO");
            this.setValue("AR_AMT", 0);
            this.setValue("TOT_AMT", 0);
            this.setValue("CHARGE", 0);

            billNoList="";
            receiptNo = "";
            this.callFunction("UI|BUT_FEE|setEnabled", true);
            this.callFunction("UI|delete|setEnabled", false);
            this.callFunction("UI|print|setEnabled", false);
            this.callFunction("UI|prePay|setEnabled", true);
            // 初始化数据，将数据赋到界面上
            order = new HRMOrder();
            order.onQuery();
            bill = new HRMBill();
            bill.onQuery();
            receipt = new HRMOpbReceipt();
            invRcp = new HRMInvRcp();
            payType = "PAY_CASH";// 现金支付
            this.setValue("PAY_TYPE", "PAY_CASH");
            return;
        }
        for (String bill : billSet) {
            billNoList += "'" + bill + "',";
        }
        billNoList = billNoList.substring(0, billNoList.length() - 1);
        double arAmt = 0;
        if (billParm.getBoolean("REXP_FLG", billTable.getSelectedRow())) {
            this.callFunction("UI|BUT_FEE|setEnabled", false);
            this.callFunction("UI|delete|setEnabled", true);
            this.callFunction("UI|print|setEnabled", true);
            this.callFunction("UI|prePay|setEnabled", false);
            String sql = // add by wanglong 20130423
                    "SELECT CASE WHEN PAY_CASH <> 0 THEN 'PAY_CASH' WHEN PAY_MEDICAL_CARD <> 0 THEN 'PAY_MEDICAL_CARD' "
                            + " WHEN PAY_BANK_CARD <> 0 THEN 'PAY_BANK_CARD' WHEN PAY_INS_CARD <> 0 THEN 'PAY_INS_CARD' "
                            + " WHEN PAY_CHECK <> 0 THEN 'PAY_CHECK' WHEN PAY_DEBIT <> 0 THEN 'PAY_DEBIT' "
                            + " WHEN PAY_DRAFT <> 0 THEN 'PAY_DRAFT' WHEN PAY_BILPAY <> 0 THEN 'PAY_BILPAY' "
                            + " WHEN PAY_INS <> 0 THEN 'PAY_INS' END AS PAY_TYPE "
                            + " FROM BIL_OPB_RECP WHERE ADM_TYPE = 'H' "
                            + "  AND RECEIPT_NO = '"
                            + billParm.getValue("RECEIPT_NO", billTable.getSelectedRow()) + "'";System.out.println("-----billParm---------sql--------"+sql);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() != 0 || result.getCount() == 0) {
                this.messageBox("查询支付方式出错");
                return;
            } else {
                this.setValue("PAY_TYPE", result.getValue("PAY_TYPE", 0));
            }
        } else {
            this.callFunction("UI|BUT_FEE|setEnabled", true);
            this.callFunction("UI|delete|setEnabled", false);
            this.callFunction("UI|print|setEnabled", false);
            this.callFunction("UI|prePay|setEnabled", true);
            // 取得账单金额赋值到界面上
            for (int i = 0; i < billParm.getCount(); i++) {
                if (billTable.getItemString(i, "FLG").equals("Y")) {
                    arAmt += billParm.getDouble("AR_AMT", i);
                }
            }
            this.setValue("AR_AMT", arAmt);// 应收金额
            this.setValue("TOT_AMT", arAmt);// 实收金额
            this.setValue("CHARGE", 0);// 找零
        }
        // billTableRow = row;
        TParm caseParm = order.getChargeData(billNoList, billParm.getBoolean("REXP_FLG", billTable.getSelectedRow()));
        if (caseParm == null) {
            this.messageBox_("结算单号为空");
            return;
        }
        if (caseParm.getErrCode() != 0) {
            this.messageBox_("取得费用详细数据失败 " + caseParm.getErrText());
            return;
        }
        billDetailTable.setParmValue(caseParm);
        // 总额控件得到焦点
        this.callFunction("UI|TOT_AMT|grabFocus");
        bill = new HRMBill();
        bill.onQueryByBillNoList(billNoList);//add by wanglong 20130510
        return;
    }

    /**
     * 收费
     */
    public void onFee() {
        if (this.messageBox("提示", "是否收费", 2) != 0) {
            return;
        }
        String payTypePage = this.getValueString("PAY_TYPE");
        if (payTypePage.equals("")) {
            this.messageBox("请选择支付方式");
            return;
        }
        if ("PAY_MEDICAL_CARD".equals(payTypePage)) { // 医疗卡收费
            this.messageBox("目前不支持医疗卡收费！");
            return;
        }
        double totAmt = StringTool.getDouble(this.getValueString("TOT_AMT"));
        double arAmt = StringTool.getDouble(this.getValueString("AR_AMT"));
        totAmt = StringTool.round(totAmt, 4);
        arAmt = StringTool.round(arAmt, 4);
        if (totAmt < arAmt) {
            this.messageBox("实收金额小于应收金额");
            return;
        }
        TParm detailParm = billDetailTable.getParmValue();
        if (detailParm == null) {
            this.messageBox_("获取费用详细失败");
            return;
        }
        int count = detailParm.getCount();
        if (count < 1) {
            this.messageBox_("没有费用详细数据");
            return;
        }
        // 取得要保存的SQL
        String[] sql = getOnFeeSql(detailParm);
        if (sql == null || sql.length < 1) {
            this.messageBox_("取得数据失败");
            onRefreshUI();// add by wanglong 20130329
            return;
        }
        // 送后台保存
        TParm inParm = new TParm();
        Map<String,String[]> inMap = new HashMap<String,String[]>();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result =
                TIOM_AppServer.executeAction("action.hrm.HRMChargeAction", "onSaveCharge", inParm);
        // 检查保存结果
        if (result.getErrCode() != 0) {
            this.messageBox("保存失败 " + result.getErrText());
            return;// add by wanglong 20130118
        } else {
            this.messageBox("P0001");// 保存成功
        }
        String noPrint = this.getValueString("NOPRINT");
        if (!"Y".equals(noPrint)) {//正常打票
            String[] receipt = new String[]{receiptNo };
            dealPrintData(receipt,
                          ((TTextFormat) this.getComponent("COMPANY_CODE")).getText(),//TextFormatHRMCompany改为TTextFormat
                          this.getValueString("DEPT_CODE_COM"), this.getValueString("SEX_CODE"));
        }
        onRefreshUI();// add by wanglong 20130329
        // onClear();
    }
    
    /**
     * 处理打印数据
     * @param receiptNo String[]
     * @param patName String
     * @param deptCode String
     * @param sexCode String
     */
    public void dealPrintData(String[] receiptNo, String patName, String deptCode, String sexCode) {
        int size = receiptNo.length;
        for (int i = 0; i < size; i++) {
            // 取出一张票据号
            String recpNo = receiptNo[i];
            if (recpNo == null || recpNo.length() == 0) return;
            // System.out.println("recpNo="+recpNo);
            // 调用打印一张票据的方法
            onPrint(OPBReceiptTool.getInstance().getOneReceipt(recpNo), patName, deptCode, sexCode);
        }
    }
    
    /**
     * 打印票据
     * @param recpParm TParm
     * @param patName String
     * @param deptCode String
     * @param sexCode String
     */
    public void onPrint(TParm recpParm, String patName, String deptCode, String sexCode) {
        if (recpParm == null) return;
        TParm oneReceiptParm = new TParm();
        // 票据信息
        oneReceiptParm.setData("CASE_NO", recpParm.getData("CASE_NO", 0));
        //20170704 zhanglei 修改补印没有医师姓名 START
        oneReceiptParm.setData("RECEIPT_NO", recpParm.getData("RECEIPT_NO", 0));
        //20170704 zhanglei 修改补印没有医师姓名 END
//        oneReceiptParm.setData("RECEIPT_NO", receiptNo);
        oneReceiptParm.setData("MR_NO", recpParm.getData("MR_NO", 0));
        oneReceiptParm.setData("BILL_DATE", recpParm.getData("BILL_DATE", 0));
        oneReceiptParm.setData("CHARGE_DATE", recpParm.getData("CHARGE_DATE", 0));
        oneReceiptParm.setData("CHARGE01", recpParm.getDouble("CHARGE01", 0) + recpParm.getDouble("CHARGE02", 0));
        // oneReceiptParm.setData("CHARGE02", recpParm.getData("CHARGE02", 0));
        oneReceiptParm.setData("CHARGE03", recpParm.getData("CHARGE03", 0));
        oneReceiptParm.setData("CHARGE04", recpParm.getData("CHARGE04", 0));
        oneReceiptParm.setData("CHARGE05", recpParm.getData("CHARGE05", 0));
        oneReceiptParm.setData("CHARGE06", recpParm.getData("CHARGE06", 0));
        oneReceiptParm.setData("CHARGE07", recpParm.getData("CHARGE07", 0));
        oneReceiptParm.setData("CHARGE08", recpParm.getData("CHARGE08", 0));
        oneReceiptParm.setData("CHARGE09", recpParm.getData("CHARGE09", 0));
        oneReceiptParm.setData("CHARGE10", recpParm.getData("CHARGE10", 0));
        oneReceiptParm.setData("CHARGE11", recpParm.getData("CHARGE11", 0));
        oneReceiptParm.setData("CHARGE12", recpParm.getData("CHARGE12", 0));
        oneReceiptParm.setData("CHARGE13", recpParm.getData("CHARGE13", 0));
        oneReceiptParm.setData("CHARGE14", recpParm.getData("CHARGE14", 0));
        oneReceiptParm.setData("CHARGE15", recpParm.getData("CHARGE15", 0));
        oneReceiptParm.setData("CHARGE16", recpParm.getData("CHARGE16", 0));
        oneReceiptParm.setData("CHARGE17", recpParm.getData("CHARGE17", 0));
        oneReceiptParm.setData("CHARGE18", recpParm.getData("CHARGE18", 0));
        oneReceiptParm.setData("CHARGE19", recpParm.getData("CHARGE19", 0));
        oneReceiptParm.setData("CHARGE20", recpParm.getData("CHARGE20", 0));
        oneReceiptParm.setData("CHARGE21", recpParm.getData("CHARGE21", 0));
        oneReceiptParm.setData("CHARGE22", recpParm.getData("CHARGE22", 0));
        oneReceiptParm.setData("CHARGE23", recpParm.getData("CHARGE23", 0));
        oneReceiptParm.setData("CHARGE24", recpParm.getData("CHARGE24", 0));
        oneReceiptParm.setData("CHARGE25", recpParm.getData("CHARGE25", 0));
        oneReceiptParm.setData("CHARGE26", recpParm.getData("CHARGE26", 0));
        oneReceiptParm.setData("CHARGE27", recpParm.getData("CHARGE27", 0));
        oneReceiptParm.setData("CHARGE28", recpParm.getData("CHARGE28", 0));
        oneReceiptParm.setData("CHARGE29", recpParm.getData("CHARGE29", 0));
        oneReceiptParm.setData("CHARGE30", recpParm.getData("CHARGE30", 0));
        oneReceiptParm.setData("TOT_AMT", StringTool.round(recpParm.getDouble("TOT_AMT", 0), 2));
        oneReceiptParm.setData("AR_AMT", StringTool.round(recpParm.getDouble("AR_AMT", 0), 2));
        oneReceiptParm.setData("CASHIER_CODE", recpParm.getData("CASHIER_CODE", 0));
        oneReceiptParm.setData("PAT_NAME", patName);
        oneReceiptParm.setData("DEPT_CODE", deptCode);
        oneReceiptParm.setData("SEX_CODE", sexCode);
        oneReceiptParm.setData("OPT_USER", Operator.getName());
        oneReceiptParm.setData("OPT_ID", Operator.getID());
        oneReceiptParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        oneReceiptParm.setData("PRINT_DATE", StringTool.getString(SystemTool.getInstance()
                .getDate(), "yyyy/MM/dd HH:mm:ss"));
        oneReceiptParm.setData("PRINT_NO", recpParm.getData("PRINT_NO", 0));
        //医疗机构类型
        String regionSql = "SELECT HOSP_CLASS FROM SYS_REGION WHERE REGION_CODE = '"+ Operator.getRegion()+"'";
        TParm regionParm = new TParm(TJDODBTool.getInstance().select(regionSql));
        String hospClass = regionParm.getValue("HOSP_CLASS",0);
        String sqlhospClass = "SELECT CHN_DESC  FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_HOSPITAL_CLASS' AND ID = '"+hospClass+"'";
        TParm  hospClassParm =  new TParm(TJDODBTool.getInstance().select(sqlhospClass));
        oneReceiptParm.setData("HOSP_CLASS","TEXT",hospClassParm.getValue("CHN_DESC",0));
        
        //查询就诊医生
        String caseNo = recpParm.getValue("CASE_NO", 0); 
       /* String selectDrSql = "SELECT C.USER_NAME FROM  HRM_PATADM A, SYS_OPERATOR C WHERE  A.CASE_NO = '" + caseNo +
                            "' AND A.FINAL_JUDGE_DR = C.USER_ID ";*/
        String selectDrSql = " SELECT C.USER_NAME FROM HRM_BILL A, HRM_ORDER B, SYS_OPERATOR C "+
        "  WHERE     A.RECEIPT_NO = '"+recpParm.getData("RECEIPT_NO", 0).toString()+"' "+
        //20170704 zhanglei 修改补印没有医师姓名 START
//		"  WHERE     A.RECEIPT_NO = '"+receiptNo+"' "+  
        //20170704 zhanglei 修改补印没有医师姓名 END
        " AND A.BILL_NO = B.BILL_NO "+
		" AND B.DR_CODE = C.USER_ID ORDER BY B.ORDER_DATE ";
        TParm  drParm =  new TParm(TJDODBTool.getInstance().select(selectDrSql));
        oneReceiptParm.setData("DR_NAME","TEXT",drParm.getValue("USER_NAME",0));
        
        // BILL_OPB_RECT中团体结算MR_NO是公司代号(COMPANY_CODE)，CASE_NO是合同代号(CONTRACT_CODE)
        TParm patParm = new TParm(TJDODBTool.getInstance().select(
                "SELECT B.DEPT_CHN_DESC FROM HRM_PATADM A,SYS_DEPT B WHERE A.COMPANY_CODE='"
                        + recpParm.getData("MR_NO", 0)
                        + "' AND A.CONTRACT_CODE='"
                        + recpParm.getData("CASE_NO", 0)
                        + "' AND A.DEPT_CODE=B.DEPT_CODE"));
        oneReceiptParm.setData("MR_NO", "TEXT", "团体名称:" + patName);
        oneReceiptParm.setData("DEPT_CODE", "TEXT", "科室:" + patParm.getValue("DEPT_CHN_DESC", 0));
        oneReceiptParm.setData("CLINICROOM_DESC", "TEXT", "");
        oneReceiptParm.setData("DR_CODE", "TEXT", "");
        oneReceiptParm.setData("QUE_NO", "TEXT", "");
        //姓名
        oneReceiptParm.setData("PAT_NAME", "TEXT", patName);
        //收款单位
        oneReceiptParm.setData("HOSP_DESC", "TEXT", Manager.getOrganization()
                .getHospitalCHNFullName(Operator.getRegion()));
        //业务流水号
        oneReceiptParm.setData("RECEIPT_NO", "TEXT", receiptNo);
        oneReceiptParm.setData("PRINT_NO", "TEXT", recpParm.getData("PRINT_NO", 0));
        //费用合计
        oneReceiptParm.setData("TOT_AMT", "TEXT",
                               StringTool.round(recpParm.getDouble("AR_AMT", 0), 2));
        //合计大写
        oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance().numberToWord(StringTool.round(recpParm.getDouble("AR_AMT", 0), 2)));
        //
        oneReceiptParm.setData("OPT_USER", "TEXT", "操作员:" + Operator.getName());
        String printDate = StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd");
        oneReceiptParm.setData("PRINT_DATE", "TEXT", printDate);
        oneReceiptParm.setData("YEAR", "TEXT", printDate.substring(0, 4));
        oneReceiptParm.setData("MONTH", "TEXT", printDate.substring(5, 7));
        oneReceiptParm.setData("DAY", "TEXT", printDate.substring(8, 10));
        oneReceiptParm.setData("OPT_ID", "TEXT", Operator.getID());
        //性别
        if("1".equals(sexCode)){
            oneReceiptParm.setData("SEX","TEXT", "男");
        } 
        if("2".equals(sexCode)){
            oneReceiptParm.setData("SEX","TEXT", "女");
        }
//     // 医生名称
//      ONERECEIPTPARM.SETDATA("DR_NAME", "TEXT", RECPPARM.GETVALUE(
//              "CASHIER_CODE", 0));
        //医保统筹支付
        oneReceiptParm.setData("PAY_DEBIT","TEXT","0.00");
        //个人账户支付
        oneReceiptParm.setData("DA_AMT","TEXT","0.00");
        //其他医保支付
        oneReceiptParm.setData("QTYL", "TEXT", "0.00");
        
     // 现金支付
        oneReceiptParm.setData("Cash", "TEXT", StringTool.round(recpParm
                .getDouble("PAY_CASH", 0), 2));
        //============================   chenxi modify 20130219 ========
        if(recpParm.getDouble("CHARGE01", 0)+recpParm.getDouble("CHARGE02", 0)==0.0)
          oneReceiptParm.setData("CHARGE01", "TEXT","");
        else  oneReceiptParm.setData("CHARGE01", "TEXT",
                recpParm.getDouble("CHARGE01", 0)+recpParm.getDouble("CHARGE02", 0));
        
        String[] array = {"CHARGE03","CHARGE04","CHARGE05","CHARGE06","CHARGE07","CHARGE08","CHARGE09",
                          "CHARGE13","CHARGE14","CHARGE15","CHARGE16","CHARGE17","CHARGE18"} ;
        for(int i=0;i<array.length;i++){
            if( recpParm.getData(array[i], 0).equals(0.0))
                 oneReceiptParm.setData(array[i], "TEXT","");
            else
                 oneReceiptParm.setData(array[i], "TEXT",
                         recpParm.getData(array[i], 0));
        }
        // ======================= chen xi modify 20130219============
        oneReceiptParm.setData("CHARGE10", "TEXT", recpParm.getData("CHARGE10", 0));
        oneReceiptParm.setData("CHARGE11", "TEXT", recpParm.getData("CHARGE11", 0));
      
        //自定义费用（体检费）
        if (recpParm.getDouble("CHARGE11", 0) > 0) {// add by wanglong 20130123
            oneReceiptParm.setData("CUSTOM_TEXT1", "TEXT", "体检费");
        } else {
            oneReceiptParm.setData("CHARGE11", "TEXT", "");
        }
        oneReceiptParm.setData("CHARGE12", "TEXT", recpParm.getData("CHARGE12", 0));

        oneReceiptParm.setData("CHARGE19", "TEXT", recpParm.getData("CHARGE19", 0));
        oneReceiptParm.setData("CHARGE20", "TEXT", recpParm.getData("CHARGE20", 0));
        oneReceiptParm.setData("CHARGE21", "TEXT", recpParm.getData("CHARGE21", 0));
        oneReceiptParm.setData("CHARGE22", "TEXT", recpParm.getData("CHARGE22", 0));
        oneReceiptParm.setData("CHARGE23", "TEXT", recpParm.getData("CHARGE23", 0));
        oneReceiptParm.setData("CHARGE24", "TEXT", recpParm.getData("CHARGE24", 0));
        oneReceiptParm.setData("CHARGE25", "TEXT", recpParm.getData("CHARGE25", 0));
        oneReceiptParm.setData("CHARGE26", "TEXT", recpParm.getData("CHARGE26", 0));
        oneReceiptParm.setData("CHARGE27", "TEXT", recpParm.getData("CHARGE27", 0));
        oneReceiptParm.setData("CHARGE28", "TEXT", recpParm.getData("CHARGE28", 0));
        oneReceiptParm.setData("CHARGE29", "TEXT", recpParm.getData("CHARGE29", 0));
        oneReceiptParm.setData("CHARGE30", "TEXT", recpParm.getData("CHARGE30", 0));
        oneReceiptParm.setData("USER_NAME", "TEXT", "" + Operator.getID());// modify by wanglong
                                                                           // 20130123
        oneReceiptParm.setData("OPT_DATE", "TEXT", StringTool.getString(SystemTool.getInstance().getDate(), "yyyy  MM  dd"));
        TParm tableParm = new TParm();//wanglong add 20150313
        tableParm.addData("COL1", "体检");
        tableParm.addData("COL2", " ");
        tableParm.addData("COL3", "1");
        tableParm.addData("COL4", " ");
        tableParm.addData("COL5", ""+ StringTool.round(recpParm.getDouble("AR_AMT", 0), 2));
        tableParm.setCount(tableParm.getCount("COL1"));
        tableParm.addData("SYSTEM", "COLUMNS", "COL1");
        tableParm.addData("SYSTEM", "COLUMNS", "COL2");
        tableParm.addData("SYSTEM", "COLUMNS", "COL3");
        tableParm.addData("SYSTEM", "COLUMNS", "COL4");
        tableParm.addData("SYSTEM", "COLUMNS", "COL5");
        oneReceiptParm.setData("TABLE", tableParm.getData());
//        this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBRECTPrint.jhw", oneReceiptParm);
        this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
                             IReportTool.getInstance().getReportParm("OPBRECTPrintForHRM.class", oneReceiptParm));//报表合并modify by wanglong 20130730
    }
    
    /**
     * 取得收费用到的SQL语句
     * @param detailParm TParm
     * @return String[]
     */
    private String[] getOnFeeSql(TParm detailParm) {//modify by wanglong 20130510
        String[] sql = new String[]{};
        if (detailParm == null) {
            return null;
        }
        int count = detailParm.getCount();
        if (count < 1) {
            return null;
        }
        String flagSql = "SELECT * FROM HRM_BILL WHERE BILL_NO IN (#)";// modify by wanglong 20130510
        flagSql = flagSql.replaceFirst("#", billNoList);
        TParm flagParm = new TParm(TJDODBTool.getInstance().select(flagSql));
        if (flagParm.getErrCode() != 0) {
            this.messageBox("查询账单状态出错 " + flagParm.getErrText());
            return null;
        }
        if (flagParm.getCount() < 1) {
            this.messageBox("该账单不存在");
            return null;
        } else if (flagParm.getCount() != billNoList.split(",").length) {//add by wanglong 20130510
            this.messageBox("账单与结算单的数量不匹配");
            return null;
        }
        if (flagParm.getValue("REXP_FLG", 0).equals("Y")) {
            this.messageBox("该账单已缴费");
            return null;
        } else if (!StringUtil.isNullString(flagParm.getValue("RECEIPT_NO", 0))) {
            this.messageBox("该账单已缴费，但未打票");
            return null;
        }
        // 取号原则得到票据号
        receiptNo = SystemTool.getInstance().getNo("ALL", "OPB", "RECEIPT_NO", "RECEIPT_NO");
        // 收费对象初始化、赋值
        receipt.onQuery();
        // 团体保存到BIL_OPB_RECP中的MR_NO是COMPANY_CODE,CASE_NO是CONTRACT_CODE
        String mrNo = billParm.getValue("COMPANY_CODE", billTable.getSelectedRow());
        String caseNo = billParm.getValue("CONTRACT_CODE", billTable.getSelectedRow());
        TParm orderAmtParm = order.getTParmByBillNoList(billNoList);//modify by wanglong 20130510
        if (orderAmtParm == null) {
            this.messageBox_("结算号为空");
            return null;
        }
        if (orderAmtParm.getErrCode() != 0) {
            this.messageBox_("取得账单对应医嘱信息失败");
            return null;
        }
        String noPrint = this.getValueString("NOPRINT");
        // 如果不打印，不写发票号
        if (!"Y".equals(noPrint)) {//打印发票
            receipt.insertForBill(orderAmtParm, receiptNo, caseNo, mrNo, updateNo, //modify by wanglong 20130716增加支票号
                                  this.getValueString("PAY_TYPE"), this.getValueString("CHECK_NO") + " " + this.getValueString("PAY_REMARK"));
        } else {//不打印发票
            receipt.insertForBill(orderAmtParm, receiptNo, caseNo, mrNo, "", //modify by wanglong 20130716增加支票号
                                  this.getValueString("PAY_TYPE"), this.getValueString("CHECK_NO") + " " + this.getValueString("PAY_REMARK"));
        }
        sql = receipt.getUpdateSQL();
        // 票据对象初始化、赋值
        if (!"Y".equals(noPrint)) {// 正常打票
            invRcp.onQuery();
            // System.out.println("3---"+SystemTool.getInstance().getDate());
            TParm invRcpParm = new TParm();
            invRcpParm.setData("RECP_TYPE", "OPB");
            invRcpParm.setData("INV_NO", updateNo);
            invRcpParm.setData("RECEIPT_NO", receiptNo);
            invRcpParm.setData("AR_AMT", receipt.getItemDouble(receipt.rowCount() - 1, "AR_AMT"));
            invRcpParm.setData("STATUS", "0");
            invRcp.insert(invRcpParm);
            // System.out.println("wrong sql"+invRcp.getUpdateSQL()[0]);
            sql = StringTool.copyArray(sql, invRcp.getUpdateSQL());
            // 更新票据档的SQL
            String sqlUpdate =
                    "UPDATE BIL_INVOICE SET UPDATE_NO='#' WHERE RECP_TYPE='OPB' AND START_INVNO='#'";
            sqlUpdate =
                    sqlUpdate.replaceFirst("#",
                                           StringTool.addString(this.getValueString("UPDATE_NO")));
            sqlUpdate = sqlUpdate.replaceFirst("#", bilInvoice.getStartInvno());
            // System.out.println("sqlUPdate="+sqlUpdate);
            String[] temp = new String[]{sqlUpdate };
            sql = StringTool.copyArray(sql, temp);
        }
        for (int i = 0; i < detailParm.getCount("MR_NO"); i++) {// add by wanglong 20130324
            String updateDSql =
                    "UPDATE HRM_CONTRACTD B SET RECEIPT_NO='#',BILL_NO='#',BILL_FLG = '1' WHERE CONTRACT_CODE='#' AND MR_NO='#'";
            updateDSql = updateDSql.replaceFirst("#", receiptNo);
            updateDSql = updateDSql.replaceFirst("#", detailParm.getValue("BILL_NO", i));
            updateDSql = updateDSql.replaceFirst("#", detailParm.getValue("CONTRACT_CODE", i));
            updateDSql = updateDSql.replaceFirst("#", detailParm.getValue("MR_NO", i));
            String updatePatAdmSql = "UPDATE HRM_PATADM SET BILL_FLG = '1' WHERE CASE_NO = '#'";
            updatePatAdmSql = updatePatAdmSql.replaceFirst("#", detailParm.getValue("CASE_NO", i));
            String[] temp = new String[]{updateDSql, updatePatAdmSql };
            sql = StringTool.copyArray(sql, temp);
        }
        // 循环医嘱细相的条目数，
        for (int i = 0; i < count; i++) {
            caseNo = detailParm.getValue("CASE_NO", i);
            mrNo = detailParm.getValue("MR_NO", i);
            if (StringUtil.isNullString(caseNo) || StringUtil.isNullString(mrNo)) {
                this.messageBox_("第 " + i + " 行医嘱数据错误");
                return null;
            }
        }
        // 医嘱对象的初始化、赋值
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        String orderSql = "UPDATE HRM_ORDER SET RECEIPT_NO='#',PRINT_FLG='Y' WHERE BILL_NO IN (#)";
        orderSql = orderSql.replaceFirst("#", receiptNo);
        orderSql = orderSql.replaceFirst("#", billNoList);
        sql = StringTool.copyArray(sql, new String[]{orderSql });
        String id = Operator.getID();
        String ip = Operator.getIP();
        if (bill.rowCount() < 1) {
            this.messageBox("获得结算数据出错");
            return null;
        }
        for (int i = 0; i < bill.rowCount(); i++) {//add by wanglong 20130510
            bill.setItem(i, "REXP_FLG", "Y");
            bill.setItem(i, "RECEIPT_NO", receiptNo);
            bill.setItem(i, "OPT_USER", id);
            bill.setItem(i, "OPT_DATE", now);
            bill.setItem(i, "OPT_TERM", ip);
        }
        sql = StringTool.copyArray(sql, bill.getUpdateSQL());
        return sql;
    }

    /**
     * 根据病案号查询
     */
    public void onMrNO() {// 暂时发现没被使用
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(mrNo)) {
            return;
        }
        // 取得MR_NO的长度
        TParm mrParm =
                new TParm(TJDODBTool.getInstance()
                        .select("SELECT MAX(MR_NO) MR_NO FROM SYS_PATINFO"));
        if (mrParm.getErrCode() != 0) {
            this.messageBox_("取得病案号长度失败");
            return;
        }
        int mrLength = mrParm.getValue("MR_NO", 0).length();
        if (mrLength < 1) {
            this.messageBox_("取得病案号长度错误");
            return;
        }
        // MR_NO自动补零，赋值到界面上
        mrNo = StringTool.fill0(mrNo, PatTool.getInstance().getMrNoLength()); // ========= chenxi
        this.setValue("MR_NO", mrNo);
//        int result = bill.onQueryByMrNo(mrNo);
        billTable.setDataStore(bill);
        billTable.setDSValue();
    }

    /**
     * 找零事件,将2个控件的钱数相减赋值到找零的控件上显示找零数目
     */
    public void onTotAmt() {
        Double totAmt = TypeTool.getDouble(this.getValue("TOT_AMT"));
        Double arAmt = TypeTool.getDouble(this.getValue("AR_AMT"));
        if (totAmt < arAmt) {
            this.setValue("TOT_AMT", 0.0);
            this.callFunction("UI|TOT_AMT|grabFoces");
            return;
        }
        double charge = totAmt - arAmt;
        this.setValue("CHARGE", charge);
        this.callFunction("UI|BUT_FEE|grabFocus");
    }

    /**
     * 补印OPB_RECEIPT更新PRINT_NO和PRINT_DATE，BIL_INVRCP更新原先的CANCEL_FLG为1，CANCEL_USER,CANCEL_DATE，再新插入一条
     */
    public void onRePrint() {
        if (billTable.getSelectedRow() < 0) {//modify by wanglong 20130510
            this.messageBox("请选择一个账单");
            return;
        }
        bilInvoice = new BilInvoice("OPB");
        String updateNo = getNextUpdateNo();
        if (StringUtil.isNullString(updateNo)) {
            return;
        }
        String receiptNo = billParm.getValue("RECEIPT_NO", billTable.getSelectedRow());//modify by wanglong 20130510
        String flagSql = "SELECT * FROM BIL_OPB_RECP WHERE RECEIPT_NO='" + receiptNo + "'";// add by wanglong 20130329
        TParm flagParm = new TParm(TJDODBTool.getInstance().select(flagSql));
        if (flagParm.getErrCode() != 0) {
            this.messageBox("查询账单状态出错 " + flagParm.getErrText());
            return;
        }
        if (flagParm.getCount() < 1) {
            this.messageBox("该账单不存在");
            onContractChoose();
            return;
        }
        if (StringUtil.isNullString(flagParm.getValue("PRINT_NO", 0))) {
            this.messageBox("该账单未打票,不能补打票据");
            return;
        }
        if (!receipt.onQueryByReceiptNo(receiptNo)) {
            this.messageBox_("初始化收据失败");
            return;
        }
        String oldPrintNo = receipt.getItemString(0, "PRINT_NO");
        if (oldPrintNo == null || oldPrintNo.equals("") || oldPrintNo.equals("null")) {
            this.messageBox("不能补印,没有打印过票据");
            return;
        }
        String[] receiptNos = new String[]{receiptNo };
//        String patName = this.getValueString("PAT_NAME");
        String sexCode = this.getValueString("SEX_CODE");
        if (!receipt.onRePrint(receipt.getItemString(0, "CASE_NO"), receiptNo, updateNo)) {
            this.messageBox_("改写票据失败");
            return;
        }
        if (!invRcp.onRePrint(oldPrintNo, updateNo)) {
            this.messageBox_("改写发票失败");
            return;
        }
        String[] sql = receipt.getUpdateSQL();
        sql = StringTool.copyArray(sql, invRcp.getUpdateSQL());
        String[] tempSql = new String[]{getBilInvoiceUpdate() };
        sql = StringTool.copyArray(sql, tempSql);
        TParm inParm = new TParm();
        Map<String,String[]> inMap = new HashMap<String,String[]>();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result =
                TIOM_AppServer.executeAction("action.hrm.HRMPersonReportAction", "onSave", inParm);
        if (result.getErrCode() != 0) {
            // this.messageBox_(result.getErrText());
            this.messageBox("E0001");
            return;
        }
        dealPrintData(receiptNos,
                      ((TTextFormat) this.getComponent("COMPANY_CODE")).getText(),//TextFormatHRMCompany改为TTextFormat
                      Operator.getDept(), sexCode);
        onClear();
    }

    /**
     * 补印时返回票据档的更新语句
     * 
     * @return String
     */
    private String getBilInvoiceUpdate() {
        String sqlUpdate =
                "UPDATE BIL_INVOICE SET UPDATE_NO='#' WHERE RECP_TYPE='OPB' AND START_INVNO='#'";
        sqlUpdate =
                sqlUpdate.replaceFirst("#", StringTool.addString(bilInvoice.getUpdateNo()))
                        .replaceFirst("#", bilInvoice.getStartInvno());
        // System.out.println("sqlUpdate=" + sqlUpdate);
        return sqlUpdate;
    }

    /**
     * 取得下一票号
     * 
     * @return String
     */
    private String getNextUpdateNo() {
        String updateNo = bilInvoice.getUpdateNo();
        this.setValue("UPDATE_NO", updateNo);
        // 检核当前票号
        if (updateNo.length() == 0 || updateNo == null) {
            this.messageBox_("无可打印的票据!");
            return "";
        }
        if (updateNo.equals(bilInvoice.getEndInvno())) {
            this.messageBox_("最后一张票据!");
        }
        if (StringTool.bitDifferOfString(updateNo, bilInvoice.getEndInvno()) < 0) {
            this.messageBox_("无可打印的票据!");
            return "";
        }
        return updateNo;
    }

    /**
     * 退费，将退费医嘱删除，将BIL_OPB_RECEIPT的数据变成负数插入一条新的数据，更新旧的BIL_INVRCP数据的CANCEL_FLG为2，CANCEL_USER,CANCEL_DATE
     */
    public void onDelete() {
        bilInvoice = new BilInvoice("OPB");
        if(!this.getValueBoolean("FEE")){//add by wanglong 20130510
            this.messageBox("请选择已缴费的账单");
            return;
        }
        if (billTable.getSelectedRow()<0) {
            this.messageBox_("请选择一张收据退费");
            return;
        }
        TParm detailParm = billDetailTable.getParmValue();
        if (detailParm == null) {
            this.messageBox_("获取费用详细失败");
            return;
        }
        if (detailParm.getCount() < 1) {
            this.messageBox_("没有费用详细数据");
            return;
        }
        String receiptNo = billParm.getValue("RECEIPT_NO", billTable.getSelectedRow());
        if (StringUtil.isNullString(receiptNo)) {
            this.messageBox("取得账单号失败");
            return;
        }
        billNoList="";
        for (int i = 0; i < billParm.getCount(); i++) {//add by wanglong 20130510
            if (billTable.getItemString(i, "FLG").equals("Y")&& StringUtil.isNullString( billParm.getValue("BILL_NO", i))) {
                this.messageBox_("结算单号为空");
                return;
            }else if(billTable.getItemString(i, "FLG").equals("Y")){
                billNoList+="'"+billParm.getValue("BILL_NO", i)+"',";
            }
        }
        billNoList = billNoList.substring(0, billNoList.length() - 1);
        String flagSql = "SELECT * FROM HRM_BILL WHERE BILL_NO IN (#)";// add by wanglong 20130329
        flagSql = flagSql.replaceFirst("#", billNoList);
        TParm flagParm = new TParm(TJDODBTool.getInstance().select(flagSql));
        if (flagParm.getErrCode() != 0) {
            this.messageBox("查询账单状态出错 " + flagParm.getErrText());
            return;
        }
        if (flagParm.getCount() < 1) {
            this.messageBox("该账单不存在");
            onContractChoose();
            return;
        } else if (flagParm.getCount() != billNoList.split(",").length) {//add by wanglong 20130510
            this.messageBox("账单与结算单的数量不匹配");
            return;
        }
        if (StringUtil.isNullString(flagParm.getValue("RECEIPT_NO", 0))) {
            this.messageBox("该账单未缴费，无需退费");
            return;
        } else if (!flagParm.getValue("REXP_FLG", 0).equals("Y")) {
            this.messageBox("该账单未打票，不能退费");
            return;
        }
        order = new HRMOrder();
        order.onQueryByReceiptNo(receiptNo);
        String buffer = order.isFilter() ? HRMOrder.FILTER : HRMOrder.PRIMARY;
        TParm orderParm = order.getBuffer(buffer);
        String[] insertOrderHistorySql = createOrderHistorySql(orderParm);// wanglong add 20140722
        int count = order.rowCount();
        if (count <= 0) {
            this.messageBox_("取得医嘱数据失败");
            return;
        }
        if (!order.deleteMedApplyByAppNo()) {// 更改Hrm_order表状态RECEIPT_NO=''
            this.messageBox_("删除医嘱失败");
            return;
        }
        this.setValue("PAY_TYPE", billParm.getValue("PAY_TYPE",billTable.getSelectedRow()));//add by wanglong 20130313
        receipt = new HRMOpbReceipt();
        receipt.onQueryByReceiptNo(receiptNo);
        String oldPrintNo = receipt.getItemString(0, "PRINT_NO");
        if (oldPrintNo.equals("")) {// add by wanglong 20130221
            messageBox("该收据未打票，无需退费");
            return;
        }
        receipt.onDisCharge(0, oldPrintNo, this.getValueString("PAY_TYPE"));// 插一条负值数据
        int[] insertRows = receipt.getNewRows();
        receipt.setItem(0, "RESET_RECEIPT_NO", receipt.getItemString(insertRows[0], "RECEIPT_NO"));// modify by wanglong 20130221
        invRcp = new HRMInvRcp();
        invRcp.onQueryByInvNo(oldPrintNo);
        if (!StringUtil.isNullString(invRcp.getItemString(0, "CANCEL_FLG"))
                && !"0".equalsIgnoreCase(invRcp.getItemString(0, "CANCEL_FLG"))) {
            this.messageBox("已退费账单不可重复退费");
            return;
        }
        invRcp.setItem(0, "CANCEL_FLG", "1");
        invRcp.setItem(0, "CANCEL_USER", Operator.getID());
        invRcp.setItem(0, "CANCEL_DATE", invRcp.getDBTime());
        invRcp.setActive(0, true);
        Timestamp now = bill.getDBTime();
        for (int i = 0; i < bill.rowCount(); i++) {//modify by wanglong 20130510
            // ====add by wanglong 20131111
            bill.setItem(i, "AR_AMT", StringTool.round(bill.getItemDouble(i, "AR_AMT") + bill.getItemDouble(i, "CUT_AMT"), 2));
            bill.setItem(i, "CUT_AMT", 0);
            bill.setItem(i, "CUT_DESCRIPTION", "");
            bill.setItem(i, "CUT_USER", "");
            bill.setItem(i, "CASHIER_CODE", "");
            bill.setItem(i, "CASHIER_DATE", "");
            // ====add end
            bill.setItem(i, "RECEIPT_NO", "");
            bill.setItem(i, "REXP_FLG", "N");
            bill.setItem(i, "OPT_USER", Operator.getID());
            bill.setItem(i, "OPT_DATE", now);
            bill.setItem(i, "OPT_TERM", Operator.getIP());
            bill.setActive(i, true);
        }
        String[] sql = receipt.getUpdateSQL();
        sql = StringTool.copyArray(sql, invRcp.getUpdateSQL());
        sql = StringTool.copyArray(sql, order.getUpdateSQL());
        sql = StringTool.copyArray(sql, bill.getUpdateSQL());
        sql = StringTool.copyArray(sql, insertOrderHistorySql); // wanglong add 20140722
        String updateDSql =
                "UPDATE HRM_CONTRACTD SET RECEIPT_NO='',BILL_FLG = '0' WHERE RECEIPT_NO='#'";
        updateDSql = updateDSql.replaceFirst("#", receiptNo);
        sql = StringTool.copyArray(sql, new String[]{updateDSql });
        for (int i = 0; i < detailParm.getCount("MR_NO"); i++) {// add by wanglong 20130324
            String updatePatAdmSql = "UPDATE HRM_PATADM SET BILL_FLG = '0' WHERE CASE_NO = '#'";
            updatePatAdmSql = updatePatAdmSql.replaceFirst("#", detailParm.getValue("CASE_NO", i));
            sql = StringTool.copyArray(sql, new String[]{updatePatAdmSql });
        }
        TParm inParm = new TParm();
        Map<String,String[]> inMap = new HashMap<String,String[]>();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result =
                TIOM_AppServer.executeAction("action.hrm.HRMChargeAction", "onSaveCharge", inParm);
        if (result.getErrCode() != 0) {
            this.messageBox("退费失败 " + result.getErrText());
            return;
        }
        receipt.resetModify();
        invRcp.resetModify();
        order.resetModify();
        order.resetMedApply();
        bill.resetModify();
        this.messageBox("退费成功");
        onRefreshUI();// add by wanglong 20130329
//        onClear();
    }
    
    /**
     * 查询费用明细
     */
    public void onReceiptDetail() {
        if (billTable.getSelectedRow() < 0) {//modify by wanglong 20130510
            this.messageBox("请选择一个账单");
            return;
        }
        TParm parm = billParm.getRow(billTable.getSelectedRow());//modify by wanglong 20130510
        if (parm == null) {
            this.messageBox("从界面获取数据失败");
            return;
        }
        if (parm.getValue("RECEIPT_NO").equals("")) {
            this.messageBox("没有账单号（未缴费），不能查看账单明细");
            return;
        }
        TParm result = OPBReceiptTool.getInstance().getOneReceipt(parm.getValue("RECEIPT_NO"));
        if (result.getErrCode() != 0) {
            this.messageBox("查询账单信息失败");
            return;
        }
        if (result.getCount() <= 0) {
            this.messageBox("账单信息不存在");
            return;
        }
        this.openDialog("%ROOT%\\config\\hrm\\HRMReceiptDetail.x", result);
        onRefreshUI();// add by wanglong 20130329
    }
    
    /**
     * 生成插入历史表SQL
     * @param orderParm
     * @return
     */
    public String[] createOrderHistorySql(TParm orderParm) {// wanglong add 20140722
        if (orderParm.getCount() < 0) {
            return null;
        }
        TDataStore orderHistory = new TDataStore();
        orderHistory.setSQL("SELECT * FROM HRM_ORDER_HISTORY");
        String columns[] = orderHistory.getColumns();
        for (int i = 0; i < orderParm.getCount(); i++) {
            int newRow = orderHistory.insertRow();
            for (String col : columns) {
                if (orderParm.getData(col, i) == null) {
                    continue;
                }
                orderHistory.setItem(newRow, col, orderParm.getData(col, i));
            }
            orderHistory.setActive(newRow, true);
        }
        if (orderHistory.getUpdateSQL() == null) {
            return new String[0];
        }
        return orderHistory.getUpdateSQL();
    }
}
