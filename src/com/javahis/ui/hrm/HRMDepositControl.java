package com.javahis.ui.hrm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import jdo.bil.BILComparator;
import jdo.hrm.HRMBill;
import jdo.hrm.HRMCheckFeeTool;
import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMContractD;
import jdo.hrm.HRMOrder;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title: 健检结算 </p>
 * 
 * <p> Description: 健检结算 </p>
 * 
 * <p> Copyright: javahis 20090922 </p>
 * 
 * <p> Company:JavaHis </p>
 * 
 * @author ehui
 * @version 1.0
 */
public class HRMDepositControl
        extends TControl {

    // 医嘱TABLE，账单TAABLE
    private TTable patTable, orderTable, billTable, billDetailTable;
    // 团体、合同TTextFormat
    private TTextFormat company, contract;
    // 合同细相对象
    private HRMContractD contractD;
    // 医嘱对象
    private HRMOrder order;
    // 账单对象
    private HRMBill bill;
    // 带生成账单的CASEnO
    private List<String> caseNos;
    // 人员列表SQL add by wanglong 20130314
    private static final String PAT_SQL = // modify by wanglong 20130419
            "SELECT * FROM (SELECT 'N' AS FLG,A.COMPANY_CODE,A.CONTRACT_CODE,A.CONTRACT_DESC,A.PACKAGE_CODE,"
                    + "       A.COVER_FLG,A.REAL_CHK_DATE REPORT_DATE,B.CASE_NO,A.MR_NO,A.SEQ_NO,A.PAT_NAME,A.PAT_DEPT,"
                    + "       A.DISCNT,SUM(B.OWN_AMT) OWN_AMT,SUM(B.AR_AMT) AR_AMT,"
                    + "       CASE WHEN B.RECEIPT_NO IS NULL AND B.BILL_NO IS NULL THEN 0 "// 未结算
                    + "            WHEN B.RECEIPT_NO IS NULL AND B.BILL_NO IS NOT NULL THEN 1 "// 已结算
                    + "            WHEN B.RECEIPT_NO IS NOT NULL THEN -1 "// 已缴费
                    + "        END BILL_FLG "
                    + "  FROM HRM_CONTRACTD A, HRM_ORDER B, HRM_PATADM C "
                    + " WHERE A.CONTRACT_CODE = C.CONTRACT_CODE "
                    + "   AND A.MR_NO = C.MR_NO "
                    + "   AND B.CASE_NO = C.CASE_NO "
                    + " @ # $ "
                    + "GROUP BY A.COMPANY_CODE,A.CONTRACT_CODE,A.CONTRACT_DESC,A.COVER_FLG,B.CASE_NO,A.MR_NO,A.SEQ_NO,"
                    + "         A.PAT_NAME,A.REAL_CHK_DATE,A.PACKAGE_CODE,A.PAT_DEPT,A.DISCNT,A.BILL_FLG,B.RECEIPT_NO,B.BILL_NO) "
                    + " WHERE 1=1 % ORDER BY SEQ_NO";
    private BILComparator compare = new BILComparator();// add by wanglong 20130419
    private boolean ascending = false;
    private int sortColumn = -1;
    private TNumberTextField arAmt;// add by wanglong 20130423
    
    /**
     * 初始化事件
     */
    public void onInit() {
        super.onInit();
        // 初始化控件
        initComponent();
        // 清空
        onClear();
    }

    /**
     * 初始化数据
     */
    public void initData() {
        contractD = new HRMContractD();
        order = new HRMOrder();
        order.onQuery();
        bill = new HRMBill();
        arAmt.setValue(0);// add by wanglong 20130423
        
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
     * 清空事件
     */
    public void onPartClear() {// add by wanglong 20130408
        this.clearValue("ALL_CHOOSE;DISCNT;START_SEQ;END_SEQ;PAT_COUNT");
        orderTable.removeRowAll();
        billTable.removeRowAll();
        billDetailTable.removeRowAll();
        caseNos = new ArrayList<String>();
        // 初始化数据
        initData();
    }
    
    /**
     * 清空事件
     */
    public void onClear() {
        this.setValue("COMPANY_CODE", "");
        this.setValue("CONTRACT_CODE", "");
        this.setValue("COVER_FLG_COMBO", "");// add by wanglong 20130419
        this.setValue("BILL_FLG_COMBO", "");
        this.clearValue("ALL_CHOOSE;ALL;MR_NO;PAT_NAME;DISCNT;START_SEQ;END_SEQ;PAT_COUNT");// modify by wanglong 20130227
        this.callFunction("UI|REPORT|setSelected", true);
        orderTable.removeRowAll();
        billTable.removeRowAll();
        billDetailTable.removeRowAll();
        patTable.removeRowAll();
        caseNos = new ArrayList<String>();
        // 初始化数据
        initData();
        ((TTextField) this.getComponent("MR_NO")).requestFocus();
        this.callFunction("UI|COVER_FLG_COMBO|setValue", "ALL");//add by wanglong 20130423
        this.callFunction("UI|BILL_FLG_COMBO|setValue", "N");
    }

    /**
     * 初始化控件
     */
    public void initComponent() {
        company = (TTextFormat) this.getComponent("COMPANY_CODE");
        contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
        patTable = (TTable) this.getComponent("PAT_TABLE");
        orderTable = (TTable) this.getComponent("ORDER_TABLE");
        billTable = (TTable) this.getComponent("BILL_TABLE");
        billDetailTable = (TTable) this.getComponent("BILL_DETAIL_TABLE");
        patTable.addEventListener("PAT_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                                  "onPatValueChange");// 人员列表勾选事件
        patTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onPatCheckBox");// 点击人员后显示其医嘱
        addSortListener(patTable);// add by wanglong 20130419
        // orderTable.addEventListener("ORDER_TABLE->" + TTableEvent.CHANGE_VALUE, this,
        // "onOrderValueChange");//暂时没作用
        // orderTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
        // "onOrderCheckBox");//暂时没作用
        billTable.addEventListener("BILL_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                                   "onBillValueChange");
        arAmt = (TNumberTextField) this.getComponent("AR_AMT");
    }

    /**
     * 人员列表单元格修改事件
     * 
     * @param tNode
     *            TTableNode
     * @return boolean
     */
    public boolean onPatValueChange(TTableNode tNode) {
        // int row = tNode.getTable().getSelectedRow();
        // TParm parm = tNode.getTable().getParmValue().getRow(row);
        // String caseNo = parm.getValue("CASE_NO");
        int column = tNode.getColumn();
        String colName = patTable.getParmMap(column);
        if ("FLG".equals(colName)) {
            if ("Y".equals(tNode.getValue())) {
                return false;
            }
        }
        if ("DISCNT".equals(colName)) {// add by wanglong 20130306
            String nodeStr = tNode.getValue().toString();
            if (!nodeStr.matches("\\p{Digit}+(\\056?\\p{Digit}+)?")) {
                this.messageBox("请输入数字");
                return true;
            }
            double discnt = StringTool.getDouble(nodeStr);
            if (discnt < 0 || discnt > 1) {
                this.messageBox("折扣只能在0和1之间");
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 人员列表勾选事件：显示账单
     * 
     * @param obj
     * @return
     */
    public boolean onPatCheckBox(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        this.showBill();
        return false;
    }

    /**
     * 人员列表点击事件：显示该人员的医嘱
     */
    public void showOrderTable() {
        // ================modify by by wanglong 20130227
        // String companyCode=this.getValueString("COMPANY_CODE");
        // String contractCode=this.getValueString("CONTRACT_CODE");
        int row = patTable.getSelectedRow();
        String caseNo = patTable.getParmValue().getValue("CASE_NO", row);
        String companyCode = patTable.getParmValue().getValue("COMPANY_CODE", row);
        String contractCode = patTable.getParmValue().getValue("CONTRACT_CODE", row);
        // this.setValue("CONTRACT_CODE", "");
        contractD = new HRMContractD();
        company.setValue(companyCode);
        TParm contractParm = contractD.onQueryByCompany(companyCode);
        if (contractParm.getErrCode() != 0) {
            this.messageBox_("没有数据");
        }
        contract.setPopupMenuData(contractParm);
        contract.setComboSelectRow();
        contract.popupMenuShowData();
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox_("未查询到该团体的合同信息");
            return;
        }
        contract.setValue(contractCode);
        // ================modify end
        if (row < 0) {
            return;
        }
        TParm orderParm = order.getDepositParm(companyCode, contractCode, caseNo);
        if (orderParm == null) {
            // this.messageBox_("ererer");
            return;
        }
        if (orderParm.getErrCode() != 0) {
            this.messageBox("取得数据失败");
            return;
        }
        orderTable.setParmValue(orderParm);
    }

    /**
     * 医嘱明细值改变事件
     * 
     * @param tNode
     */
    public boolean onOrderValueChange(TTableNode tNode) {
        int row = tNode.getRow();
        TParm orderParm = orderTable.getParmValue();
        if (orderParm == null) {
            return true;
        }
        int count = orderParm.getCount();
        if (count <= 0) {
            return true;
        }
//        String caseNo = orderParm.getValue("CASE_NO", row);
        String orderCode = orderParm.getValue("ORDER_CODE", row);
//        int orderGroupNo = orderParm.getInt("ORDERSET_GROUP_NO", row);
        boolean setMainFlg = TypeTool.getBoolean(orderParm.getData("SETMAIN_FLG", row));
        String billFlg = TypeTool.getBoolean(tNode.getValue()) ? "Y" : "N";
        int column = tNode.getColumn();
        String colName = orderTable.getParmMap(column);
        if ("BILL_FLG".equalsIgnoreCase(colName)) {
            if (setMainFlg) {
                for (int i = row; i < count; i++) {
                    if (!orderCode.equalsIgnoreCase(orderParm.getValue("ORDERSET_CODE", i))) {
                        // this.messageBox_("break");
                        break;
                    }
                    orderParm.setData("BILL_FLG", i, billFlg);
                    // this.messageBox_("in billflg");
                }
            }
            double amt = orderTable.getItemDouble(row, "AR_AMT");
            // double amt=order.getAmt(caseNo, orderCode,
            // orderGroupNo,billFlg,bill.getItemString(bill.rowCount()-1, "BILL_NO"));
            // this.messageBox_(amt);
            if (TypeTool.getBoolean(tNode.getValue())) {
                bill.setItem(bill.rowCount() - 1, "OWN_AMT",
                             bill.getItemDouble(bill.rowCount() - 1, "OWN_AMT") + amt);
                // this.messageBox_("herer");
            } else {
                double originalAmt = bill.getItemDouble(bill.rowCount() - 1, "OWN_AMT");
                double currentAmt = originalAmt - amt;
                bill.setItem(bill.rowCount() - 1, "OWN_AMT", currentAmt);
            }
            billTable.setDSValue();
            orderTable.setParmValue(orderParm);
            return false;
        }
        return true;
    }

    /**
     * 医嘱列表checkBox事件
     * 
     * @param obj
     * @return
     */
    public boolean onOrderCheckBox(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        return false;
    }

    /**
     * 账单列表值改变事件
     * 
     * @param tNode
     * @return
     */
    public boolean onBillValueChange(TTableNode tNode) {
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String colName = billTable.getParmMap(column);
        if (TypeTool.getBoolean(bill.getItemData(row, "REXP_FLG"))) {
            this.messageBox_("已结算账单不能修改");
            return true;
        }
        if (StringUtil.isNullString(bill.getItemString(row, "BILL_NO"))) {
            this.messageBox_("无账单号账单不能修改");
            return true;
        }
        if ("CUT_AMT".equalsIgnoreCase(colName)) {
            double cutAmt = TypeTool.getDouble(tNode.getValue());
            if (cutAmt <= 0) {
                return true;
            }
            bill.setActive(row, true);
            return false;
        }
        if ("DISCOUNT_RATE".equalsIgnoreCase(colName)) {
            double rate = TypeTool.getDouble(tNode.getValue());
            if (rate < 0.01 || rate > 1) {
                this.messageBox_("折扣率应在0~1之间");
                return true;
            }
            bill.setActive(row, true);
            return false;
        }
        if ("CUT_DESCRIPTION".equalsIgnoreCase(colName)
                || "DISCOUNT_DESCRIPTION".equalsIgnoreCase(colName)) {
            String value = TypeTool.getString(tNode.getValue());
            if (StringUtil.isNullString(value)) {
                return true;
            }
            bill.setActive(row, true);
            return false;
        }
        return true;
    }

    /**
     * BILL_TABLE单击事件
     */
    public void onBillTableClick() {
        int row = billTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        int count = bill.rowCount();
        if (count <= 0) {
            return;
        }
        String billNo = bill.getItemString(row, "BILL_NO");
        if (StringUtil.isNullString(billNo)) {
            this.messageBox_("取得结算单号失败");
            return;
        }
        // 显示有bill_no的医嘱，没有则不显示
        TParm parm = order.getParmBybillNo(billNo);
        if (parm == null) {
            // this.messageBox_("取得数据失败");
            billDetailTable.removeRowAll();
            return;
        }
        if (parm.getErrCode() != 0) {
            this.messageBox_("取得结算单号" + billNo + "对应医嘱数据失败");
            return;
        }
        billDetailTable.setParmValue(parm);
    }

    /**
     * 全选事件
     */
    public void onClickAll() {// 暂时没用
        if (orderTable.getRowCount() <= 0) {
            return;
        }
        String isAll = TypeTool.getBoolean(this.getValue("ALL")) ? "Y" : "N";
        TParm orderParm = orderTable.getParmValue();
        int count = orderParm.getCount();
        if (count <= 0) {
            return;
        }
        orderTable.acceptText();
        double amt = 0;
        double ownamt = 0;
        for (int i = 0; i < count; i++) {
            if (!orderParm.getBoolean("SETMAIN_FLG", i)) {
                continue;
            }
            if (this.getValueBoolean("ALL") && orderParm.getBoolean("BILL_FLG", i)) {
                continue;
            }
//            String caseNo = orderParm.getValue("CASE_NO", i);
            String orderCode = orderParm.getValue("ORDER_CODE", i);
//            int orderGroupNo = orderParm.getInt("ORDERSET_GROUP_NO", i);
            //
            // double amt=order.getAmt(caseNo, orderCode,
            // orderGroupNo,isAll,bill.getItemString(bill.rowCount()-1, "BILL_NO"));
            // 应付价
            amt += orderTable.getItemDouble(i, "AR_AMT");
            // 总价
            ownamt += orderTable.getItemDouble(i, "OWN_AMT");
            for (int j = i; j < count; j++) {
                if (!orderCode.equalsIgnoreCase(orderParm.getValue("ORDERSET_CODE", j))) {
                    break;
                }
                orderParm.setData("BILL_FLG", j, isAll);
            }
        }
        if (TypeTool.getBoolean(isAll)) {
            bill.setItem(bill.rowCount() - 1, "OWN_AMT", ownamt); // 原总价
            bill.setItem(bill.rowCount() - 1, "AR_AMT", amt); // 折扣后总价
            bill.setItem(bill.rowCount() - 1, "DISCOUNT_AMT", ownamt - amt); // 拆扣总价
        } else {
            bill.setItem(bill.rowCount() - 1, "OWN_AMT", ownamt);
            bill.setItem(bill.rowCount() - 1, "AR_AMT", amt);
            bill.setItem(bill.rowCount() - 1, "DISCOUNT_AMT", ownamt - amt);
        }
        orderTable.setParmValue(orderParm);
        billTable.setDSValue();
    }

    /**
     * 全选病患
     */
    public void onAllPat() {
        TParm parm = patTable.getParmValue();
        int rowCount = parm.getCount();
        if (rowCount <= 0) {
            this.messageBox("无人员信息");
            return;
        }
        if (this.getValueString("ALL_CHOOSE").equals("Y")) {// add by wanglong 20130417
            if (rowCount == 1) {
                patTable.setSelectedRow(0);
                showOrderTable();
            } else {
                Set<String> conSet = new HashSet<String>();
                for (int i = 0; i < parm.getCount(); i++) {
                    conSet.add(parm.getValue("CONTRACT_CODE", i));
                }
                if (conSet.size() > 1) {
                    this.messageBox("不同合同下的人员不允许合在同一张账单下");
                    this.setValue("ALL_CHOOSE", "N");
                    for (int i = 0; i < rowCount; i++) {
                        parm.setData("FLG", i, "N");
                    }
                }
            }
        }
        for (int i = 0; i < rowCount; i++) {
            parm.setData("FLG", i, this.getValueString("ALL_CHOOSE"));
        }
        patTable.setParmValue(parm);
        if (this.getValueString("ALL_CHOOSE").equals("N")) {// add by wanglong 20130417
            patTable.setParmValue(parm);
            orderTable.removeRowAll();
            billTable.removeRowAll();
            billDetailTable.removeRowAll();
            caseNos = new ArrayList<String>();
            bill = new HRMBill();
            return;
        }
        this.showBill();
    }

    /**
     * 团体代码点选事件，根据选定的团体代码，初始化该团体的合同信息TTextFormat，并初始化账单对象
     */
    public void onCompanyCodeChoose() {
        String companyCode = this.getValueString("COMPANY_CODE");
        if (StringUtil.isNullString(companyCode)) {
            return;
        }
        TParm contractParm = contractD.onQueryByCompany(companyCode);
        if (contractParm.getErrCode() != 0) {
            this.messageBox_("没有数据");
        }
        // System.out.println("contractParm="+contractParm);
        contract.setPopupMenuData(contractParm);
        contract.setComboSelectRow();
        contract.popupMenuShowData();
        String contractCode = contractParm.getValue("ID", 0);
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox_("未查询到该团体的合同信息");
            return;
        }
        contract.setValue(contractCode);
        this.clearValue("MR_NO;PAT_NAME");// modify by wanglong 20130227
        // 查询
        onContractCodeChoose();
    }

    /**
     * 合同代码点选事件，调用查询事件
     */
    public void onContractCodeChoose() {// modify by wanglong 20130314
        String companyCode = this.getValueString("COMPANY_CODE");
        String contractCode = this.getValueString("CONTRACT_CODE");
        String sql = PAT_SQL + "";
        if (!companyCode.equals("")) {
            sql = sql.replaceFirst("@", " AND A.COMPANY_CODE = '" + companyCode + "' ");
        } else {
            sql = sql.replaceFirst("@", " ");
        }
        if (!contractCode.equals("")) {
            sql = sql.replaceFirst("#", " AND A.CONTRACT_CODE = '" + contractCode + "' ");
        } else {
            sql = sql.replaceFirst("#", " ");
        }
//        if (this.getValueBoolean("UNREPORT")) {// add by wanglong 20130314
//            sql = sql.replaceFirst("\\$", " AND A.COVER_FLG = 'N' ");
//        } else if (this.getValueBoolean("REPORT")) {
//            sql = sql.replaceFirst("\\$", " AND A.COVER_FLG = 'Y' ");
//        } else if (this.getValueBoolean("ALL")) {
//            sql = sql.replaceFirst("\\$", " ");
//        }
        if (this.getValue("COVER_FLG_COMBO").equals("ALL")
                || this.getValue("COVER_FLG_COMBO").equals("")) {// add by wanglong 20130314
            sql = sql.replaceFirst("\\$", " ");
        } else if (this.getValue("COVER_FLG_COMBO").equals("N")) {
            sql = sql.replaceFirst("\\$", " AND A.COVER_FLG = 'N' ");
        } else if (this.getValue("COVER_FLG_COMBO").equals("Y")) {
            sql = sql.replaceFirst("\\$", " AND A.COVER_FLG = 'Y' ");
        }
        if (this.getValue("BILL_FLG_COMBO").equals("ALL")
                || this.getValue("BILL_FLG_COMBO").equals("")) {// add by wanglong 20130314
            sql = sql.replaceFirst("%", " ");
        } else if (this.getValue("BILL_FLG_COMBO").equals("N")) {
            sql = sql.replaceFirst("%", " AND BILL_FLG >= 0 ");
        } else if (this.getValue("BILL_FLG_COMBO").equals("Y")) {
            sql = sql.replaceFirst("%", " AND BILL_FLG = -1 ");
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            this.messageBox("查询人员列表失败");
            return;
        }
        onPartClear();// add by wanglong 20130408
        patTable.setParmValue(result);
        if (result.getCount() >= 0) {
            this.setValue("PAT_COUNT", result.getCount() + "人");
        } else this.setValue("COUNT", "");
//        order.onQueryByContractCode(contractCode);// 初始化order信息
        // onQuery();
    }

    /**
     * 病案号查询
     */
    public void onQueryByMr() {// modify by wanglong 20130314
        String mrNo = this.getValueString("MR_NO").trim();
        if (mrNo.equals("")) {
            return;
        }
        mrNo = PatTool.getInstance().checkMrno(mrNo);// 病案号补齐长度
        this.setValue("MR_NO", mrNo);
        
        // modify by huangtt 20160929 EMPI患者查重提示 start
		Pat pat = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);// 病案号 
		}
		// modify by huangtt 20160929 EMPI患者查重提示 end
        
        
        String mrSql = "SELECT * FROM HRM_CONTRACTD WHERE MR_NO = '" + mrNo + "'";
        
        // add by wangb 2016/06/23 团体名称需要根据不同登录角色筛选
		String roleType = "";
		roleType = this.getPopedemParm().getValue("ID").replace("[", "")
				.replace("]", "").replace(",", "','").replace(" ", "");
		mrSql = mrSql + " AND ROLE_TYPE IN ('" + roleType + "') ";
        
        TParm parm = new TParm(TJDODBTool.getInstance().select(mrSql));
        if (parm.getErrCode() != 0) {
            this.messageBox(parm.getErrCode() + parm.getErrText());
            return;
        }
        if (parm.getCount() < 1) {
            this.messageBox("健检系统中不存在此人员");
            return;
        }
        this.setValue("PAT_NAME", parm.getValue("PAT_NAME", 0));
        String sql = PAT_SQL + "";
        sql = sql.replaceFirst("@", " ");
        sql = sql.replaceFirst("#", " AND A.MR_NO = '" + mrNo + "' ");
//        if (this.getValueBoolean("UNREPORT")) {// add by wanglong 20130314
//            sql = sql.replaceFirst("\\$", " AND A.COVER_FLG = 'N' ");
//        } else if (this.getValueBoolean("REPORT")) {
//            sql = sql.replaceFirst("\\$", " AND A.COVER_FLG = 'Y' ");
//        } else if (this.getValueBoolean("ALL")) {
//            sql = sql.replaceFirst("\\$", " ");
//        }
        if (this.getValue("COVER_FLG_COMBO").equals("ALL")
                || this.getValue("COVER_FLG_COMBO").equals("")) {// add by wanglong 20130314
            sql = sql.replaceFirst("\\$", " ");
        } else if (this.getValue("COVER_FLG_COMBO").equals("N")) {
            sql = sql.replaceFirst("\\$", " AND A.COVER_FLG = 'N' ");
        } else if (this.getValue("COVER_FLG_COMBO").equals("Y")) {
            sql = sql.replaceFirst("\\$", " AND A.COVER_FLG = 'Y' ");
        }
        if (this.getValue("BILL_FLG_COMBO").equals("ALL")
                || this.getValue("BILL_FLG_COMBO").equals("")) {// add by wanglong 20130314
            sql = sql.replaceFirst("%", " ");
        } else if (this.getValue("BILL_FLG_COMBO").equals("N")) {
            sql = sql.replaceFirst("%", " AND BILL_FLG >= 0 ");
        } else if (this.getValue("BILL_FLG_COMBO").equals("Y")) {
            sql = sql.replaceFirst("%", " AND BILL_FLG = -1 ");
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            this.messageBox("查询人员列表失败");
            return;
        }
        onPartClear();// add by wanglong 20130408
        this.setValue("COMPANY_CODE", "");
        this.setValue("CONTRACT_CODE", "");
        this.callFunction("UI|COVER_FLG_COMBO|setValue", "ALL");//add by wanglong 20130423
        this.callFunction("UI|BILL_FLG_COMBO|setValue", "ALL");
        contractD = new HRMContractD();
        patTable.setParmValue(result);
        if (result.getCount() >= 0) {
            this.setValue("PAT_COUNT", result.getCount() + "人");
        } else this.setValue("COUNT", "");
        order.onQueryByMrNo(mrNo);// 初始化order信息
    }

    /**
     * “未报到”、“已报到”、“全部”单选按钮事件
     * “未缴费”、“已缴费”、“全部”单选按钮事件
     */
    public void onStateChoose() {
        String mrNo = this.getValueString("MR_NO").trim();
        if (!mrNo.equals("")) {
            onQueryByMr();
        } else {
            String contractCode = this.getValueString("CONTRACT_CODE");
            if (contractCode.equals("")) {
                this.messageBox("请选择合同");
                return;
            } else {
                onContractCodeChoose();
            }
        }
    }
    
    /**
     * 性别筛选
     */
    public void onCustomizeChoose() {// modify by wanglong 20130419
        TParm parm = patTable.getParmValue();
        if (this.getValueString("START_SEQ").equals("")
                || this.getValueString("END_SEQ").equals("")) {
            return;
        }
        if (!this.getValueString("START_SEQ").matches("[0-9]+")
                || !this.getValueString("END_SEQ").matches("[0-9]+")) {
            messageBox("请输入数字");
            return;
        }
        int startSeq = this.getValueInt("START_SEQ");
        int endSeq = this.getValueInt("END_SEQ");
        if (startSeq > endSeq) {
            startSeq = startSeq + endSeq;
            endSeq = startSeq - endSeq;
            startSeq = startSeq - endSeq;
        }
        int count = parm.getCount();
        for (int i = count - 1; i >= 0; i--) {
            if (parm.getInt("SEQ_NO", i) < startSeq || (parm.getInt("SEQ_NO", i) > endSeq)) {
                parm.removeRow(i);
            }
        }
        patTable.setParmValue(parm);
        if (parm.getCount() >= 0) {
            this.setValue("PAT_COUNT", parm.getCount() + "人");
        } else this.setValue("COUNT", "");
    }
    
    /**
     * 查询事件,根据选定的团体代码和合同代码查询医嘱，显示医嘱数据
     */
    public void showBill() {
        String companyCode = this.getValueString("COMPANY_CODE");
        String contractCode = this.getValueString("CONTRACT_CODE");
        List<String> caseNo = new ArrayList<String>();
        TParm patParm = patTable.getParmValue();
        int rowCount = patParm.getCount();
        for (int i = 0; i < rowCount; i++) {
            TParm temp = patParm.getRow(i);
            if ("Y".equals(temp.getValue("FLG"))) {
                caseNo.add(temp.getValue("CASE_NO"));
            }
        }
//        int listSize = caseNo.size();
        // add by lx 2012/05/27
        caseNos = caseNo;
//      StringBuffer caseNoStr = new StringBuffer();//delete by wanglong 20130806
//      for (int i = 0; i < listSize; i++) {
//          caseNoStr.append("'" + caseNo.get(i).toString() + "'");
//          if (i != listSize - 1) caseNoStr.append(",");
//      }
      // this.messageBox_(caseNoStr.toString());
      // =========== add by chenxi 20130207 账单显示当前选中的 人员的账单
        StringBuffer billNoStr = new StringBuffer();
        String caseNoWhere = HRMOrder.getInStatement("CASE_NO", caseNos);//add by wanglong 20130806
        String sql = "";
        if (caseNos.size() == 0) {
            sql = "SELECT BILL_NO FROM HRM_BILL WHERE CONTRACT_CODE = '#'";
        } else {
            sql = // modify by wanglong 20130806
                    "SELECT DISTINCT(BILL_NO) FROM HRM_ORDER WHERE CONTRACT_CODE = '#' AND (" + caseNoWhere + ")";
        }
        sql = sql.replaceFirst("#", contractCode);
//        System.out.println("sql============="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        for (int i = 0; i < result.getCount(); i++) {
            billNoStr.append("'" + result.getValue("BILL_NO", i) + "'");
            if (i != result.getCount() - 1) billNoStr.append(",");
        }
        // =========== add by chenxi 20130207 账单显示当前选中的 人员的账单
        if (StringUtil.isNullString(companyCode) || StringUtil.isNullString(contractCode)) {
            this.messageBox_("查询条件不足");
            return;
        }
        bill = new HRMBill();
        // 查询现有的账单（不一定已结算）
        bill.onQueryByCom(companyCode, contractCode, billNoStr.toString());
        bill.insertRow(-1, companyCode, contractCode);// 新生成一行
        billTable.setDataStore(bill);
        billTable.setDSValue();
        // 对没有生成账单的人员，增加一行（合在一起）来显示他们的账单
        createNewBill(companyCode, contractCode, caseNos);
        arAmt.setValue(bill.getItemDouble(0, "AR_AMT"));// add by wanglong 20130423
    }

    /**
     * 生成新账单
     */
    public void createNewBill(String companyCode, String contractCode, List<String> caseNos) {//modify by wanglong 20130806
        if (caseNos.size() < 1) {
            return;
        }
        TParm orderParm = order.getDepositBillParm(companyCode, contractCode, caseNos);
        int count = orderParm.getCount();
        if (count <= 0) {
            return;
        }
        int row = bill.rowCount() - 1;
        // double amt = orderParm.getDouble("AR_AMT");
        bill.setItem(row, "OWN_AMT", orderParm.getDouble("OWN_AMT"));
        bill.setItem(row, "AR_AMT", orderParm.getDouble("AR_AMT"));
        bill.setItem(row, "DISCOUNT_AMT",
                     orderParm.getDouble("OWN_AMT") - orderParm.getDouble("AR_AMT"));
        billTable.setDSValue();
    }

    /**
     * 根据LIST中的内容拼接ORDER的过滤条件
     * 
     * @return
     */
    public String getCaseFilter() {
        String filter = "";
        if (caseNos.size() <= 0) {
            return filter;
        }
        for (int i = 0; i < caseNos.size(); i++) {
            filter += "CASE_NO='" + caseNos.get(i) + "' OR ";
        }
        filter = filter.substring(0, filter.lastIndexOf("OR"));
        // System.out.println("==filter=="+filter);
        return filter;
    }
    
    /**
     * 结算（生成HRM_Bill数据，更新HRM_order数据）
     */
    public void onSave() {
        if (bill.getItemDouble(bill.rowCount() - 1, "AR_AMT") > 0) {
            bill.setActive(bill.rowCount() - 1, true);
        } else {
            this.messageBox("没有需要结算的账单");
            return;
        }
        // TParm parm = orderTable.getParmValue();
//        String case_no = "";
//        String orderset_no = "";
//        int orderset_group_no = 0;
        String billNo =
                billTable.getDataStore().getItemString(billTable.getDataStore().rowCount() - 1,
                                                       "BILL_NO");
        if (billNo.equals("")) {
            this.messageBox("结算单号为空");
            return;
        }
        String flagSql = "SELECT * FROM HRM_BILL WHERE BILL_NO='" + billNo + "'";// add by wanglong 20130329
        TParm flagParm = new TParm(TJDODBTool.getInstance().select(flagSql));
        if (flagParm.getErrCode() != 0) {
            this.messageBox("查询账单状态出错 " + flagParm.getErrText());
            return;
        }
        String contractCode = this.getValueString("CONTRACT_CODE");
        if (flagParm.getCount() > 0) {
            this.messageBox("该账单已结算");
            if (!contractCode.equals("")) {
                onContractCodeChoose();
//                order = new HRMOrder();//delete by wanglong 20130417
                bill = new HRMBill();
                orderTable.removeRowAll();
                billTable.removeRowAll();
                billDetailTable.removeRowAll();
            } else {
                onClear();
            }
            return;
        }
        String id = Operator.getID();
//        String ip = Operator.getIP();
        String[] sql = bill.getUpdateSQL();
        String[] sql_order_list = new String[1];
        // add by lx 2012/05/27
        // System.out.println("=======sql======="+this.getCaseFilter());
        String caseNoWhere=HRMOrder.getInStatement("CASE_NO",caseNos);//add by wanglong 20130806
//        String orderSql = "SELECT * FROM HRM_ORDER WHERE CONTRACT_CODE='#' AND (" + caseNoWhere+")";
//        orderSql=orderSql.replaceFirst("#", contractCode);
//        // System.out.println("=======orderSql======="+orderSql);
//        TParm parm = new TParm(TJDODBTool.getInstance().select(orderSql));
//        for (int i = 0; i < parm.getCount("ORDER_DESC"); i++) {
//            // 假如已生成账单则继续
//            if ("Y".equals(parm.getValue("BILL_FLG", i))) {
//                continue;
//            }
//            case_no = parm.getValue("CASE_NO", i);
//            orderset_no = parm.getValue("ORDERSET_CODE", i);
//            orderset_group_no = parm.getInt("ORDERSET_GROUP_NO", i);
//            String orderCat1Code = parm.getValue("ORDER_CAT1_CODE", i);
//            // 如果是药品，更新语句变换
//            if ("PHA_W".equals(orderCat1Code)) {
//                sql_order_list[0] =
//                        " UPDATE HRM_ORDER SET BILL_FLG = 'Y', BILL_NO = '" + billNo
//                                + "', BILL_USER = '" + id
//                                + "', BILL_DATE = SYSDATE  WHERE CASE_NO = '" + case_no
//                                + "' AND ORDER_CODE = '" + parm.getValue("ORDER_CODE", i) + "' AND BILL_NO IS NULL";
//            } else {
//                sql_order_list[0] =
//                        " UPDATE HRM_ORDER SET BILL_FLG = 'Y', BILL_NO = '" + billNo
//                                + "', BILL_USER = '" + id
//                                + "', BILL_DATE = SYSDATE  WHERE CASE_NO = '" + case_no
//                                + "' AND ORDERSET_CODE = '" + orderset_no
//                                + "' AND ORDERSET_GROUP_NO = " + orderset_group_no + " AND BILL_NO IS NULL";
//            }
//            sql = StringTool.copyArray(sql, sql_order_list);
//        }
        sql_order_list[0]= " UPDATE HRM_ORDER SET BILL_FLG = 'Y', BILL_NO = '" + billNo
              + "', BILL_USER = '" + id
              + "', BILL_DATE = SYSDATE  WHERE BILL_NO IS NULL AND (" + caseNoWhere + ")";//add by wanglong 20130806
        sql = StringTool.copyArray(sql, sql_order_list);
        // if(order.isModified()){
        // if(!order.updateOpt()){
        // this.messageBox_("更新医嘱数据失败");
        // return;
        // }
        // }
        String updateBillSql = // add by wanglong 20130829
                "UPDATE HRM_CONTRACTD SET BILL_NO = '#' WHERE COMPANY_CODE = '#' AND CONTRACT_CODE = '#' "
                        + " AND MR_NO IN (SELECT DISTINCT MR_NO FROM HRM_ORDER WHERE 1=1 AND (#))";
        updateBillSql = updateBillSql.replaceFirst("#", billNo);
        updateBillSql = updateBillSql.replaceFirst("#", this.getValueString("COMPANY_CODE"));
        updateBillSql = updateBillSql.replaceFirst("#", contractCode);
        updateBillSql = updateBillSql.replaceFirst("#", caseNoWhere);
        sql = StringTool.copyArray(sql, new String[]{updateBillSql });//add end
        if (sql == null || sql.length <= 0) {
            this.messageBox_("没有需要处理的数据");
            return;
        }
        TParm inParm = new TParm();
        Map<String,String[]> inMap = new HashMap<String,String[]>();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result =
                TIOM_AppServer
                        .executeAction("action.hrm.HRMDepositAction", "onSaveDeposit", inParm);
        if (result.getErrCode() != 0) {
            // this.messageBox_(result.getErrText());
            this.messageBox("结算失败 " + result.getErrText());
        } else {
            this.messageBox("结算成功");
            TParm parmValue = patTable.getParmValue();
            for (int i = 0; i < parmValue.getCount(); i++) {
                if (parmValue.getValue("FLG", i).equals("Y")
                        && caseNos.contains(parmValue.getValue("CASE_NO", i))) {//结算成功后，修改人员列表状态为“已结算”
                    parmValue.setData("BILL_FLG", i, 1);
                }
            }
            patTable.setParmValue(parmValue);
            billTable.setSelectedRow(bill.rowCount()-1);
            onBillTableClick();// modify by wanglong 20130329
//            String contractCode = this.getValueString("CONTRACT_CODE");
//            if (!contractCode.equals("")) {
//                onContractCodeChoose();
//                orderTable.removeRowAll();
//                billTable.removeRowAll();
//                billDetailTable.removeRowAll();
//            } else {
//                onClear();
//            }
        }
    }

    /**
     * 取消结算（已缴费，则不能删除，否则，在HRM_BILL中删除，同时将HRM_ORDER.BILL_NO置空）
     */
    public void onDelete() {
        if (billTable == null) {
            return;
        }
        if (bill == null) {
            return;
        }
        if (bill.rowCount() < 1) {this.messageBox("没有账单信息");
            return;
        }
        int row = billTable.getSelectedRow();
        if (row < 0) {
            this.messageBox("请选择一个账单");
            return;
        }
        if (TypeTool.getBoolean(bill.getItemData(row, "REXP_FLG"))) {// 打票注记
            this.messageBox_("账单已打票，不能取消结算");
            return;
        }
        String billNo = bill.getItemString(row, "BILL_NO");
        if (StringUtil.isNullString(billNo)) {
            this.messageBox_("结算单号为空，取消结算失败");
        }
        String billSql = "SELECT * FROM HRM_BILL WHERE BILL_NO='" + billNo + "'";
        TParm result1 = new TParm(TJDODBTool.getInstance().select(billSql));
        if (result1.getErrCode() != 0) {
            this.messageBox("检查账单信息失败 " + result1.getErrText());
            return;
        }
        if (result1.getCount() < 1) {
            this.messageBox("该账单未结算，无需取消");
            return;
        }
        if (!result1.getValue("RECEIPT_NO", 0).equals("")) {
            this.messageBox("该账单已缴费，不能取消");
            return;
        }
        bill.onQueryByBillNo(billNo);// add by wanglong 20130415
        bill.deleteRow(row);
        //==================================modify by wanglong 20130726
//        order = new HRMOrder();// add by wanglong 20130415
//        order.onQueryByBillNo(billNo);// add by wanglong 20130415
//        order.retrieve();// add by wanglong 20130415
//        if (!order.removeBillByBillNo(billNo)) {
//            this.messageBox("准备数据失败");
//            return;
//        }
//        String[] sql = order.getUpdateSQL();
        String[] sql = new String[]{};// add by wanglong 20130726
        String updateOrderSql =
                "UPDATE HRM_ORDER SET BILL_NO = '',BILL_FLG = 'N', BILL_USER = '', BILL_DATE = '', "
                        + "OPT_USER = '#', OPT_DATE = SYSDATE, OPT_TERM='#' WHERE BILL_NO = '#'";// add by wanglong 20130726
        updateOrderSql = updateOrderSql.replaceFirst("#", Operator.getID());
        updateOrderSql = updateOrderSql.replaceFirst("#", Operator.getIP());
        updateOrderSql = updateOrderSql.replaceFirst("#", billNo);
        sql = StringTool.copyArray(sql, new String[]{updateOrderSql });
        //==================================modify end
        sql = StringTool.copyArray(sql, bill.getUpdateSQL());
        String updateDSql = "UPDATE HRM_CONTRACTD SET BILL_NO = '' WHERE BILL_NO='" + billNo + "'";
        sql = StringTool.copyArray(sql, new String[]{updateDSql });// add by wanglong 20130415
        if (sql == null || sql.length <= 0) {
            this.messageBox("没有需要处理的数据");
            return;
        }
        TParm inParm = new TParm();
        Map<String,String[]> inMap = new HashMap<String,String[]>();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result =
                TIOM_AppServer
                        .executeAction("action.hrm.HRMDepositAction", "onSaveDeposit", inParm);
        if (result.getErrCode() != 0) {
            this.messageBox_("保存失败 " + result.getErrText());
        } else {
            this.messageBox("取消结算成功");
            String contractCode = this.getValueString("CONTRACT_CODE");
            if (!contractCode.equals("")) {
                onContractCodeChoose();
                orderTable.removeRowAll();
                billTable.removeRowAll();
                billDetailTable.removeRowAll();
            } else {
                onClear();
            }
        }
    }

    /**
     * 打印收费清单
     */
    public void onPrint() {
        if (billTable == null) {
            return;
        }
        int row = billTable.getSelectedRow();
        if (row < 0) {
            this.messageBox_("请选择一个账单进行操作");
            return;
        }
        if (bill == null || bill.rowCount() <= 0) {
            this.messageBox_("没有账单可打印");
            return;
        }
        String billNo = bill.getItemString(row, "BILL_NO");
        if (StringUtil.isNullString(billNo)) {
            this.messageBox_("取得结算单号失败");
            return;
        }
//         TParm parm = order.getParmByBillNoMain(billNo);
        TParm parm = order.getParmByBillNo(billNo);// modify by wanglong 20130403
        if (parm == null) {
            this.messageBox_("没有账单明细信息（该账单可能未结算）");
            return;
        }
        if (parm.getErrCode() != 0) {
            this.messageBox("查询医嘱明细失败 " + parm.getErrText());
            return;
        }
        TParm result = new TParm();
        Timestamp now = order.getDBTime();
        String data = "打印时间：" + StringTool.getString(now, "yyyy/MM/dd");
        result.setData("PRINT_TIME", "TEXT", data);
        result.setData("PRINT_USER", "TEXT", "制表人：" + Operator.getName());
        Vector AR_AMT = (Vector) parm.getData("AR_AMT");
        double sum = 0.00;
        for (int i = 0; i < AR_AMT.size(); i++) {
            // System.out.println(AR_AMT.get(i)+"");
            sum += Double.parseDouble(AR_AMT.get(i) + "");
        }
        new BigDecimal(sum).setScale(0, BigDecimal.ROUND_HALF_UP);
        result.setData("sum5", "TEXT",
                       "合计：" + new BigDecimal(sum).setScale(2, BigDecimal.ROUND_HALF_UP));
        // System.out.println(parm.getData());
        result.setData("BILL_TABLE", parm.getData());
        openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMBillDetail.jhw", result, false);
    }

    /**
     * 统一折扣
     */
    public void onSameDiscnt() {// add by wanglong 20130306
        double discnt = StringTool.getDouble(this.getText("DISCNT"));// 折扣
        if (discnt <= 0 || discnt > 1) {
            this.messageBox("折扣只能在0和1之间");
            this.setValue("DISCNT", 0);
            return;
        }
        TParm parm = patTable.getParmValue();
        int count = parm.getCount();
        if (count <= 0) {
            return;
        }
        boolean flag = true;
        for (int i = 0; i < count; i++) {
            if (parm.getBoolean("FLG", i) == true) {
                parm.setData("DISCNT", i, discnt);
                flag = false;
            }
        }
        if (flag == true) {
            this.messageBox("没有选择任何人员");
        }
        patTable.setParmValue(parm);
    }

    /**
     * 更改医嘱的折扣（已结算的不更改）
     */
    public void onChangeOrderDiscnt() {// add by wanglong 20130306
        //onSameDiscnt();
        patTable.acceptText();
        TParm parm = patTable.getParmValue();
        int count = parm.getCount();
        if (count <= 0) {
            return;
        }
        double discnt = 0;
        String contractCode = "";
        String mrNo = "";
        String caseNo = "";
        boolean flag = true;
        boolean isBilled = true;// 是否有已结算的人员
        int doneNum = 0;// 成功修改折扣的人数
        for (int i = 0; i < count; i++) {
            if (parm.getValue("FLG", i).equals("Y")) {
                if (!parm.getValue("BILL_FLG", i).equals("0") && isBilled == true) {
                    this.messageBox("已结算的账单，折扣不会被更改");
                    isBilled = false;
                } else if (parm.getValue("BILL_FLG", i).equals("0")) {
                    String[] sql = new String[]{};
                    discnt = parm.getDouble("DISCNT", i);
                    if (discnt <= 0 || discnt > 1) {
                        this.messageBox("折扣只能在0和1之间");
                        return;
                    }
                    contractCode = parm.getValue("CONTRACT_CODE", i);
                    mrNo = parm.getValue("MR_NO", i);
                    caseNo = parm.getValue("CASE_NO", i);
                    String updateContractDSql =
                            "UPDATE HRM_CONTRACTD SET DISCNT=" + discnt + " WHERE CONTRACT_CODE='"
                                    + contractCode + "' AND MR_NO='" + mrNo + "'";
                    sql = StringTool.copyArray(sql, new String[]{updateContractDSql });
                    String updateOrderSql =
                            "UPDATE HRM_ORDER SET DISCOUNT_RATE="
                                    + discnt
                                    + ", AR_AMT=DISPENSE_QTY*OWN_PRICE*"
                                    + discnt
                                    + " WHERE BILL_NO IS NULL AND CAT1_TYPE<>'PHA' AND CONTRACT_CODE='"
                                    + contractCode + "' AND MR_NO='" + mrNo + "'";
                    sql = StringTool.copyArray(sql, new String[]{updateOrderSql });
                    String updatePatAdmSql =
                            "UPDATE HRM_PATADM SET DISCNT=" + discnt + " WHERE CASE_NO='" + caseNo
                                    + "'";
                    sql = StringTool.copyArray(sql, new String[]{updatePatAdmSql });
                    Map<String,String[]> inMap = new HashMap<String,String[]>();
                    inMap.put("SQL", sql);
                    TParm inParm = new TParm();
                    inParm.setData("IN_MAP", inMap);
                    TParm result =
                            TIOM_AppServer.executeAction("action.hrm.HRMDepositAction",
                                                         "onSaveDeposit", inParm);
                    if (result.getErrCode() != 0) {
                        this.messageBox("姓名：" + parm.getData("PAT_NAME", i) + "(病案号:" + mrNo
                                + ")  折扣修改失败" + result.getErrText());
                        flag = false;
                    } else {
                        doneNum++;
                        TParm param = new TParm();
                        param.setData("COMPANY_CODE", parm.getValue("COMPANY_CODE", i));
                        param.setData("CONTRACT_CODE", contractCode);
                        param.setData("MR_NO", mrNo);
                        TParm result2 = HRMCheckFeeTool.getInstance().onQueryMaster(param);
                        if (result2.getErrCode() != 0) {
                            this.messageBox("查询修改后的人员费用信息失败");
                        }
                        double arAmt = 0;
                        for (int j = 0; j < result2.getCount(); j++) {
                            arAmt += result2.getDouble("AR_AMT", j);
                        }
                        parm.setData("AR_AMT", i, arAmt);//modify by wanglong 20130417
                    }
                }
            }
        }
        if (flag == true && doneNum > 0) {
            this.messageBox("折扣修改成功");
        }
        patTable.setParmValue(parm);
        this.showBill();// 重新展示账单
        // this.onClear();
    }
    
    /**
     * 打印缴费通知单
     */
    public void onPrintBill() {// wanglong add 20141205
        if (billTable == null) {
            return;
        }
        int row = billTable.getSelectedRow();
        if (row < 0) {
            this.messageBox_("请选择一个账单进行操作");
            return;
        }
        if (bill == null || bill.rowCount() <= 0) {
            this.messageBox_("没有账单可打印");
            return;
        }
        String billNo = bill.getItemString(row, "BILL_NO");
        if (StringUtil.isNullString(billNo)) {
            this.messageBox_("取得结算单号失败");
            return;
        }
        String sql =
                "SELECT DISTINCT A.COMPANY_CODE, E.COMPANY_DESC, A.CONTRACT_CODE, A.CONTRACT_DESC, A.SEQ_NO, A.MR_NO, "
                        + "      A.PAT_NAME, D.AR_AMT, C.BILL_DATE, C.BILL_USER "
                        + " FROM HRM_CONTRACTD A, HRM_PATADM B, HRM_ORDER C, HRM_BILL D, HRM_COMPANY E "
                        + "WHERE A.CONTRACT_CODE = B.CONTRACT_CODE "
                        + "  AND A.MR_NO = B.MR_NO "
                        + "  AND B.CASE_NO = C.CASE_NO "
                        + "  AND C.CONTRACT_CODE = D.CONTRACT_CODE "
                        + "  AND C.BILL_NO = D.BILL_NO "
                        + "  AND A.BILL_NO = D.BILL_NO "
                        + "  AND A.COMPANY_CODE = E.COMPANY_CODE "
                        + "  AND A.BILL_NO = '#' "
                        + "  AND A.CONTRACT_CODE = '@' ORDER BY A.SEQ_NO";
        sql = sql.replaceFirst("#", bill.getItemString(row, "BILL_NO"));
        sql = sql.replaceFirst("@", bill.getItemString(row, "CONTRACT_CODE"));
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("取得账单信息失败 " + result.getErrText());
            return;
        }
        if (result.getCount() < 1) {
            this.messageBox("账单不存在");
            return;
        }
        TParm parm = result.getRow(0);
        Timestamp now = SystemTool.getInstance().getDate();
        if (result.getCount() > 1
                && !result.getValue("MR_NO", 0).equals(result.getValue("MR_NO", 1))) {
            parm.setData("MR_NO", "");
            parm.setData("PAT_NAME", "");
        }
        parm.setData("BILL_DATE",
                     StringTool.getString(parm.getTimestamp("BILL_DATE"), "yyyy/MM/dd"));
        parm.setData("NOW", now);// 当前时间
        parm.setData("PRINT_USER", "TEXT", Operator.getName());// 制表人
        openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMBill.jhw", parm, false);
    }
    
 // ====================排序功能begin======================add by wanglong 20130419
    /**
     * 加入表格排序监听方法
     * @param table
     */
    public void addSortListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                if (j == sortColumn) {
                    ascending = !ascending;// 点击相同列，翻转排序
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                TParm tableData = table.getParmValue();// 取得表单中的数据
                String columnName[] = tableData.getNames("Data");// 获得列名
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
                int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // 将排序后的vector转成parm;
                cloneVectoryParam(vct, new TParm(), strNames, table);
            }
        });
    }

    /**
     * 根据列名数据，将TParm转为Vector
     * @param parm
     * @param group
     * @param names
     * @param size
     * @return
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size)
            count = size;
        for (int i = 0; i < count; i++) {
            Vector row = new Vector();
            for (int j = 0; j < nameArray.length; j++) {
                row.add(parm.getData(group, nameArray[j], i));
            }
            data.add(row);
        }
        return data;
    }

    /**
     * 返回指定列在列名数组中的index
     * @param columnName
     * @param tblColumnName
     * @return int
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * 根据列名数据，将Vector转成Parm
     * @param vectorTable
     * @param parmTable
     * @param columnNames
     * @param table
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
            String columnNames, final TTable table) {
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        table.setParmValue(parmTable);
    }
    /**
     * 汇出Excel
     */
    public void onExport() {
    	if(patTable.getRowCount()<=0){
    		this.messageBox("没有汇出数据");
    		return;
    	}
    	String header = patTable.getHeader();
    	String parmMap = patTable.getParmMap();
		ExportExcelUtil.getInstance().exportExcel(
				header.substring(header.indexOf(";") + 1),
				parmMap.substring(parmMap.indexOf(";") + 1),
				patTable.getShowParmValue(), "个人费用表");
    }
    // ====================排序功能end======================
}
