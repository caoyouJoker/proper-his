package com.javahis.ui.hrm;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import jdo.hrm.HRMCompany;
import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMContractD;
import jdo.hrm.HRMContractM;
import jdo.hrm.HRMFeePackTool;
import jdo.hrm.HRMPackageD;
import jdo.sid.IdCardO;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SystemTool;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TNode;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TUIStyle;
import com.dongyang.ui.base.TTableCellRenderer;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.DateTool;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatHRMPackagem;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;


/**
 * <p>
 * Title: 健康检查团体信息控制类
 * </p>
 * 
 * <p>
 * Description: 健康检查团体信息控制类
 * </p>
 * 
 * <p>
 * Copyright: javahis 20090922
 * </p>
 * 
 * <p>
 * Company:JavaHis
 * </p>
 * 
 * @author ehui
 * @version 1.0
 */
public class HRMCompanyInfoControl
        extends TControl {

    /** 团体、合同、员工TABLE */
    private TTable comTab, conTab, mrTab;
    /** 公司信息 */
    private HRMCompany company;
    /** 合同主档 */
    private HRMContractM contractM;
    /** 合同明细 */
    private HRMContractD contractD;
    /** 删除时用的表名 */
    private String tableName;
    /** 单击合同TABLE时记录合同名称 */
    private int conDescRow;
    private boolean isChangeContract = false;
    private ArrayList<String> patLogSQLList = new ArrayList<String>();// wanglong add 20140623
    private String roleType; // add by wangb 2016/06/21 登录角色
	private TTextFormat packageM;// add by wangb 2016/7/5 套餐下拉框
    
    /**
     * 初始化事件
     */
    public void onInit() {
        super.onInit();
        // 初始化控件
        initComponent();
        // add by wanglong 20130304 人员table选中行变红
        TTableCellRenderer cellRenderer = new TTableCellRenderer(mrTab) {

            public void setComponentForeColor(Component component, boolean isSelected, int row,
                                              int column) {
                if (isSelected) {
                    component.setForeground(Color.RED);// TUIStyle.getTableSelectionForeColor()
                    return;
                }
                /**
                 * 自定义行字体颜色
                 */
                Color color = getTable().getRowTextColor(row);
                if (color != null) {
                    component.setForeground(color);
                    return;
                }
                // 自定义列前景颜色
                color = getColumnForeColor(getTable().getColumnModel().getColumnIndex(column));
                if (color != null) {
                    component.setForeground(color);
                    return;
                }
                component.setForeground(TUIStyle.getTableForeColor());
            }
        };
        mrTab.setCellRenderer(cellRenderer);
        //上面的方法导致对齐方式设置在.x文件中无效，只能写在程序中
        conTab.setColumnHorizontalAlignmentData("1,left;2,left;3,left;4,left;5,left;6,right;7,right;8,right;9,left;10,left");
        mrTab.setColumnHorizontalAlignmentData("1,left;3,left;6,left;7,left;9,left;10,right;11,left;12,left;14,left");
        // 初始化数据
        initData();
        onClear();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        company = new HRMCompany();
        company.onQuery();
        comTab.setDataStore(company);
        comTab.setDSValue();
        contractM = new HRMContractM();
        contractM.onQuery();
        conTab.setDataStore(contractM);
        contractD = new HRMContractD();
//        contractD.onQuery();//wanglong delete 20140512
        if (isChangeContract) {
            mrTab.setDataStore(contractD);
        }
        
        // add by wangb 2016/06/21 登录角色
        roleType = this.getPopedemParm().getValue("ID", 0);
        if (this.getPopedem("SYSDBA")) {
        	roleType = "H";
        }
        
		String filterRoleType = "";
		if (this.getPopedem("SYSDBA")) {
			filterRoleType = HRMCompanyTool.ALL_ROLE_TYPE.replace(",", "','");
		} else {
			filterRoleType = this.getPopedemParm().getValue("ID", 0);
		}
        
		// 查询套餐信息
		TParm pakcageData = HRMFeePackTool.getInstance().selectHrmPackageByRoleType(filterRoleType);
        packageM.setPopupMenuData(pakcageData);
        packageM.setComboSelectRow();
        packageM.popupMenuShowData();
    }

    /**
     * 初始化控件
     */
    private void initComponent() {
        comTab = (TTable) this.getComponent("TAB_COMPANY");
        conTab = (TTable) this.getComponent("TAB_CONTRACT");
        mrTab = (TTable) this.getComponent("TAB_MR");
        // 合同TABLE值改变事件
        conTab.addEventListener("TAB_CONTRACT->" + TTableEvent.CHANGE_VALUE, this,
                                "onTabConValueChanged");
        // 合同细相TABLE值改变事件
        mrTab.addEventListener("TAB_MR->" + TTableEvent.CHANGE_VALUE, this, "onTabMrValueChanged");
        mrTab.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onCheckBox");
        
        // add by wangb 2016/07/04 一期临床登录显示受试者招募信息按钮
        if (!this.getPopedem("SYSDBA") && !this.getPopedem("PIC")) {
        	this.callFunction("UI|recruitInfo|visible", false);
        }
        
        packageM = (TTextFormat) this.getComponent("PACKAGE_CODE");
    }

    /**
     * 团体TABLE单击事件
     */
    public void onTabComClicked() {
        tableName = "COMPANY";
        int row = comTab.getSelectedRow();
        this.setValue("COMPANY_DESC", company.getItemString(row, "COMPANY_DESC"));
        this.setValue("PY1", company.getItemString(row, "PY1"));
        this.setValue("PY2", company.getItemString(row, "PY2"));
        this.setValue("DESCRIPTION", company.getItemString(row, "DESCRIPTION"));
        this.setValue("ADMINISTRATOR", company.getItemString(row, "ADMINISTRATOR"));
        this.setValue("TEL", company.getItemString(row, "TEL"));
        this.setValue("IND_TYPE_CODE", company.getItemString(row, "IND_TYPE_CODE"));
        this.setValue("FAX_NO", company.getItemString(row, "FAX_NO"));
        this.setValue("CONTACTS_NAME", company.getItemString(row, "CONTACTS_NAME"));
        this.setValue("CONTACTS_TEL", company.getItemString(row, "CONTACTS_TEL"));
        this.setValue("ADDRESS", company.getItemString(row, "ADDRESS"));
        this.setValue("POST_CODE", company.getItemString(row, "POST_CODE"));
        this.setValue("E_MAIL", company.getItemString(row, "E_MAIL"));
        contractM.filt(company.getItemString(row, "COMPANY_CODE"));
        conTab.setDSValue();
        mrTab.setDSValue();
    }

    /**
     * 合同TABLE单击事件
     */
    public void onTabConClicked() {
        tableName = "CONTRACT";
        int row = conTab.getSelectedRow();
        String contractCode = contractM.getItemString(row, "CONTRACT_CODE");
        conDescRow = row;
        if (StringUtil.isNullString(contractCode)) {
            return;
        }
        // 通过合同代码、团体代码过滤合同细相信息并判断如果合同细相对象的最后一行不是空行，则新增一行
        contractD.onQuery(contractM.getItemString(row, "COMPANY_CODE"),
                          contractM.getItemString(row, "CONTRACT_CODE"));// wanglong add20140512
//        contractD.filt(contractM.getItemString(row, "CONTRACT_CODE"),
//                       contractM.getItemString(row, "COMPANY_CODE"));
        if (contractD.rowCount() < 1
                || !StringUtil.isNullString(contractD.getItemString(contractD.rowCount() - 1,
                                                                    "MR_NO"))) {
            int rowCount = contractD.rowCount();
            // add by wangb 2016/06/22 设置角色类型
            contractD.setRoleType(roleType);
            contractD.insertRow(contractM.getItemString(row, "COMPANY_CODE"),
                                contractM.getItemString(row, "CONTRACT_CODE"),
                                contractM.getItemString(row, "CONTRACT_DESC"),
                                contractM.getItemDouble(row, "DISCNT"), rowCount + 1);
        }
        patLogSQLList = new ArrayList<String>();// wanglong add 20140623
        if (mrTab.getDataStore() instanceof HRMContractD) {
            mrTab.setDSValue();
        } else {
            mrTab.setDataStore(contractD);
            mrTab.setDSValue();
        }
        this.clearValue("PACKAGE_CODE");
    }

    /**
     * 员工合同变更(有问题，待修改)
     */
    public void onChangeContract() {
        int row = mrTab.getSelectedRow();
        if (row < 0) {
            this.messageBox("请选择人员");
            return;
        }
        TParm showParm = mrTab.getShowParmValue().getRow(row);
        if (showParm.getValue("MR_NO").equals("Y")) {
            this.messageBox("新建的人员尚未保存到数据库，不能变更，请先执行保存操作");
            return;
        }
        if (showParm.getValue("COVER_FLG").equals("Y")) {
            if (this.messageBox("提示", "该人员已报到，是否继续操作", 2) != 0) {
                return;
            }
        }
        TParm billStateParm = new TParm();
        if (showParm.getValue("BILL_FLG").equals("Y")) {
            String sql =
                    "SELECT DISTINCT C.RECEIPT_NO, C.BILL_NO "
                            + "  FROM HRM_CONTRACTD A, HRM_PATADM B, HRM_ORDER C "
                            + " WHERE A.CONTRACT_CODE = B.CONTRACT_CODE "
                            + "   AND A.MR_NO = B.MR_NO AND B.CASE_NO = C.CASE_NO "
                            + "   AND A.MR_NO = '#' AND A.CONTRACT_CODE = '#'";
            billStateParm = new TParm(TJDODBTool.getInstance().select(sql));
            if(!billStateParm.getValue("RECEIPT_NO", 0).equals("")){
                if (this.messageBox("提示", "该人员已打票，是否继续操作", 2) != 0) {
                    return;
                }
            }
        }
        TParm dataStoreParm = mrTab.getDataStore().getRowParm(row);
        TParm parm = new TParm();
        parm.setData("showParm", showParm);
        parm.setData("dataStoreParm", dataStoreParm);
        TParm reParm = (TParm) this.openDialog("%ROOT%\\config\\hrm\\HRMChangeContract.x", parm);
        if (reParm == null) {
            this.messageBox("合同变更取消!");
            return;
        }
        // 先查询变更人是否在另外一个合同里，有不能变更
        String selectSql =
                " SElECT A.COMPANY_CODE FROM HRM_CONTRACTD A " + " WHERE A.COMPANY_CODE='"
                        + reParm.getValue("COMPANY_CODE") + "' AND A.CONTRACT_CODE='"
                        + reParm.getValue("CONTRACT_CODE") + "' AND A.MR_NO='"
                        + dataStoreParm.getValue("MR_NO") + "' ";
        TParm selectParm = new TParm();
        selectParm.setData(TJDODBTool.getInstance().select(selectSql));
        if (selectParm.getCount("COMPANY_CODE") > 0) {
            this.messageBox("合同变更失败！待转入合同中已存在此人员！");
            return;
        }
        String updateContractSql =
                "UPDATE HRM_CONTRACTD SET COMPANY_CODE = '#', CONTRACT_CODE = '@', CONTRACT_DESC = '!' "
                        + " WHERE CONTRACT_CODE = '&' AND MR_NO = '？'";
        updateContractSql = updateContractSql.replaceFirst("#", reParm.getValue("COMPANY_CODE"));
        updateContractSql = updateContractSql.replaceFirst("@", reParm.getValue("CONTRACT_CODE"));
        updateContractSql = updateContractSql.replaceFirst("!", reParm.getValue("CONTRACT_DESC"));
        updateContractSql =
                updateContractSql.replaceFirst("&", dataStoreParm.getValue("CONTRACT_CODE"));
        updateContractSql = updateContractSql.replaceFirst("？", dataStoreParm.getValue("MR_NO"));
        String[] updateSql = new String[]{};
        String caseNoSql =
                "SELECT A.BILL_NO,B.CASE_NO FROM HRM_CONTRACTD A, HRM_PATADM B "
                        + "      WHERE A.CONTRACT_CODE = B.CONTRACT_CODE "
                        + "        AND A.MR_NO = B.MR_NO           "
                        + "        AND A.CONTRACT_CODE = '&' AND A.MR_NO = '？'";
        caseNoSql = caseNoSql.replaceFirst("&", dataStoreParm.getValue("CONTRACT_CODE"));
        caseNoSql = caseNoSql.replaceFirst("？", dataStoreParm.getValue("MR_NO"));
        TParm caseNoParm = new TParm(TJDODBTool.getInstance().select(caseNoSql));
        if (caseNoParm.getValue("CASE_NO", 0).equals("")) {
            updateSql = new String[]{updateContractSql };
        } else {
            String updateOrderSql = "UPDATE HRM_ORDER SET CONTRACT_CODE = '@' WHERE CASE_NO ='？'";
            updateOrderSql = updateOrderSql.replaceFirst("@", reParm.getValue("CONTRACT_CODE"));
            updateOrderSql = updateOrderSql.replaceFirst("？", caseNoParm.getValue("CASE_NO", 0));
            String updatePatSql =
                    "UPDATE HRM_PATADM SET COMPANY_CODE = '#', CONTRACT_CODE = '@' WHERE CASE_NO = '？'";
            updatePatSql = updatePatSql.replaceFirst("#", reParm.getValue("COMPANY_CODE"));
            updatePatSql = updatePatSql.replaceFirst("@", reParm.getValue("CONTRACT_CODE"));
            updatePatSql = updatePatSql.replaceFirst("？", caseNoParm.getValue("CASE_NO", 0));
            updateSql = new String[]{updateContractSql, updateOrderSql, updatePatSql };
            if (!caseNoParm.getValue("BILL_NO", 0).equals("")) {
                String hrmBillSql =
                        "SELECT DISTINCT MR_NO, RECEIPT_NO FROM HRM_CONTRACTD WHERE BILL_NO = '#'"
                                .replaceFirst("#", caseNoParm.getValue("BILL_NO", 0));
                TParm hrmBillParm = new TParm(TJDODBTool.getInstance().select(hrmBillSql));
                String updateBillSql = "";
                if (hrmBillParm.getCount("MR_NO") == 1) {
                    updateBillSql =
                            "UPDATE HRM_BILL SET COMPANY_CODE = '#', CONTRACT_CODE = '@' " +
                            " WHERE BILL_NO = '"+caseNoParm.getValue("BILL_NO", 0)+"' " +
                            		" AND CONTRACT_CODE = '&' ";//huangjw 20150910添加BILL_NO 筛选
                    updateBillSql =
                            updateBillSql.replaceFirst("#", reParm.getValue("COMPANY_CODE"));
                    updateBillSql =
                            updateBillSql.replaceFirst("@", reParm.getValue("CONTRACT_CODE"));
                    updateBillSql =
                            updateBillSql
                                    .replaceFirst("&", dataStoreParm.getValue("CONTRACT_CODE"));
                    updateSql =
                            new String[]{updateContractSql, updateOrderSql, updatePatSql,
                                    updateBillSql };
                    if (!hrmBillParm.getValue("RECEIPT_NO", 0).equals("")) {
                        String updateBilRecp =
                                "UPDATE BIL_OPB_RECP SET MR_NO = '#', CASE_NO = '@' WHERE CASE_NO = '&'";
                        updateBilRecp =
                                updateBilRecp.replaceFirst("#", reParm.getValue("COMPANY_CODE"));
                        updateBilRecp =
                                updateBilRecp.replaceFirst("@", reParm.getValue("CONTRACT_CODE"));
                        updateBilRecp =
                                updateBilRecp.replaceFirst("&",
                                                           dataStoreParm.getValue("CONTRACT_CODE"));
                        updateSql =
                                new String[]{updateContractSql, updateOrderSql, updatePatSql,
                                        updateBillSql, updateBilRecp };
                    }
                }
            }
        }
        TParm result = new TParm();
        isChangeContract = true;
        result.setData(TJDODBTool.getInstance().update(updateSql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            this.messageBox("变更失败，请检查合同“" + reParm.getValue("CONTRACT_DESC") + "”中是否已存在该人员");
            return;
        }
//        initData();
        onTabConClicked();
        isChangeContract = false;
        this.messageBox("变更成功");
    }

    /**
     * 员工TABLE单击事件
     */
    public void onTabMrClicked() {
        tableName = "MR";
        // conTab.clearSelection() ;
    }

    /**
     * 合同TABLE值改变事件
     * 
     * @param tNode
     */
    public boolean onTabConValueChanged(TTableNode tNode) {
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String colName = conTab.getParmMap(column);
        Object value = tNode.getValue();
        Object oldValue = tNode.getOldValue();
        // 如果是合同名称，则自动生成简拼代码，并将合同名称赋值给每一条合同细想
        if ("CONTRACT_DESC".equalsIgnoreCase(colName)) {
            String strValue = (String) value;
            String strOldValue = (String) oldValue;
            if (StringUtil.isNullString(strValue) || strValue.equalsIgnoreCase(strOldValue)) {
                return true;
            }
            // ========================= chenxi modify 20130313
            for (int i = 0; i < conTab.getRowCount(); i++) {
                if (strValue.equals(conTab.getItemString(i, "CONTRACT_DESC"))) {
                    this.messageBox("合同名称不能重复");
                    return true;
                }
            }
            String py1 = TMessage.getPy(strValue);
            contractM.setItem(row, "PY1", py1);
            contractM.setActive(row, true);
            int count = contractD.rowCount();
            for (int i = 0; i < count; i++) {
                contractD.setItem(i, "CONTRACT_DESC", strValue);
//                contractD.setItem(i, "SEQ_NO", i + 1);
            }
            mrTab.setDSValue();
            return false;
        }
        // 如果是套餐代码则将套餐代码赋值给每一条合同细相
        if ("PACKAGE_CODE".equalsIgnoreCase(colName)) {
            String strValue = (String) value;
            String strOldValue = (String) oldValue;
            if (StringUtil.isNullString(strValue) || strValue.equalsIgnoreCase(strOldValue)) {
                return true;
            }
            int count = contractD.rowCount();
            for (int i = 0; i < count; i++) {
                contractD.setItem(i, "PACKAGE_CODE", strValue);
            }
            mrTab.setDSValue();
            contractM.setActive(row, true);
            return false;
        }
        // 如果是折扣信息或公司支付%比，则判断是否该值大于1，如果是，则为非法数字。不能修改
        if ("DISCNT".equalsIgnoreCase(colName) || "CP_PAY".equalsIgnoreCase(colName)) {
            double num = Double.parseDouble(tNode.getValue() + "");
            double oldNum = Double.parseDouble(tNode.getOldValue() + "");
            if (num == oldNum) {
                return true;
            }
            if (num > 1.0) {
                this.messageBox_("折扣率应介于0到1之间");
                return true;
            }
            String billFlg = contractM.getItemString(row, "BILL_FLG");
            if (billFlg != null && billFlg.equals("Y")) {
                this.messageBox_("已经结算不能修改");
                return true;
            }
            int count = contractD.rowCount();
            for (int i = 0; i < count; i++) {
                contractD.setItem(i, "DISCNT", num);
            }
            // ==== add by lx 2012/05/18 加入总价=====$$//
            TParm parm = conTab.getDataStore().getRowParm(row);
            contractM
                    .setItem(row, "TOT_AMT", StringTool.round(parm.getDouble("SUBTOTAL") * num, 2));
            conTab.setDSValue();
            mrTab.setDSValue();
            contractM.setActive(row, true);
            return false;
        }
        return false;
    }

    /**
     * 员工TABLE值改变事件
     * 
     * @param tNode
     * @return
     */
    public boolean onTabMrValueChanged(TTableNode tNode) {
        // if (conTab.getItemString(conTab.getSelectedRow(), "CONTRACT_DESC").trim().equals("")) {
        // add-by-wanglong-20130302
        // this.messageBox_("合同名为空，不能录入人员");
        // return true;
        // }
        tableName = "MR";
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String colName = mrTab.getParmMap(column);
        Object value = tNode.getValue();
        Object oldValue = tNode.getOldValue();
        if (TypeTool.getBoolean(contractD.getItemData(row, "COVER_FLG"))
                && !"SEQ_NO".equalsIgnoreCase(colName)) {//wanglong 20140924 到诊人员只有序号能改
            this.messageBox_("已到诊人员信息不能修改");
            return true;
        }
        if (StringUtil.isNullString(contractD.getItemString(row, "CONTRACT_DESC"))) {
            this.messageBox("没有合同名称！请先完善合同信息！");
            return true;
        }
        mrTab.acceptText();
        if ("FOREIGNER_FLG".equalsIgnoreCase(colName)) {
            if (!StringUtil.isNullString(contractD.getItemString(row, "IDNO"))) {
                contractD.setItem(row, "IDNO", "");
            }
            // if ("Y".equals(value.toString())) {
            // contractD.setItem(row, "MR_NO", "Y");
            // } else {
            // contractD.setItem(row, "MR_NO", "");
            // }
            return false;
        }
        // 如果是折扣信息或公司支付%比，则判断是否该值大于1，如果是，则为非法数字。不能修改
        if ("DISCNT".equalsIgnoreCase(colName)) {
            double num = Double.parseDouble(tNode.getValue() + "");
            double oldNum = Double.parseDouble(tNode.getOldValue() + "");
            if (num == oldNum) {
                return true;
            }
            if (num > 1 || num < -1) {
                this.messageBox_("折扣率应介于0到1之间");
                return true;
            }
            contractM.setActive(row, true);
            return false;
        }
        // 病案号
        if ("MR_NO".equalsIgnoreCase(colName)) {// add by wanglong 20130506
            String strValue = (String) value;
            if (strValue.trim().equals("")) {
                return true;
            }
            if (contractD.isActive(row)) {
                this.messageBox("已确定的病案号不能修改，请删除该条记录后重新录入");
                return true;
            }
            Pat pat = Pat.onQueryByMrNo(strValue.trim());
            if (pat == null) {
                this.messageBox("病案号不存在");
                return true;
            }
            String srcMrNo = PatTool.getInstance().checkMrno(strValue.trim());
            String mrNo = pat.getMrNo();
            if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(mrNo)) {// wanglong
                                                                             // add 20150423
                this.messageBox("病案号" + srcMrNo + " 已合并至 " + "" + mrNo);
            }
            TParm parm = PatTool.getInstance().getInfoForMrno(mrNo);
            if (parm.getCount() <= 0) {
                this.messageBox_("病案号不存在");
                return true;
            }
            int rowCount = contractD.rowCount();
            for (int i = 0; i < rowCount; i++) {// wanglong add 20140606
                if (!contractD.isActive(i)) continue;
                if (i == row) continue;
                if (contractD.getItemString(i, "MR_NO").equals(strValue)) {
                    this.messageBox("该人员已存在");
                    return true;
                }
            }
            contractD.setItem(row, "IDNO", parm.getData("IDNO", 0));
            if (!isId(parm.getValue("IDNO", 0))) {
                contractD.setItem(row, "FOREIGNER_FLG", "Y");
            }
            contractD.setItem(row, "MR_NO", mrNo);
            contractD.setItem(row, "PAT_NAME", parm.getData("PAT_NAME", 0));
            contractD.setItem(row, "SEX_CODE", parm.getData("SEX_CODE", 0));
            contractD.setItem(row, "BIRTHDAY", parm.getData("BIRTH_DATE", 0));
            contractD.setItem(row, "MARRIAGE_CODE", parm.getData("MARRIAGE_CODE", 0));
            contractD.setItem(row, "ROLE_TYPE", roleType);
            if (!parm.getValue("CELL_PHONE", 0).equals("")
                    && parm.getValue("CELL_PHONE", 0).matches("[^\u4e00-\u9fa5]*")) {// wanglong add
                                                                                     // 20140522
                                                                                     // 自动取电话号码
                contractD.setItem(row, "TEL", parm.getData("CELL_PHONE", 0));
            } else if (parm.getValue("TEL_HOME", 0).trim().length() == 11
                    && parm.getValue("TEL_HOME", 0).trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                contractD.setItem(row, "TEL", parm.getValue("TEL_HOME", 0));
            }
            if ((row == contractD.rowCount() - 1) && contractD.getItemInt(row, "SEQ_NO") == 0) {
                contractD.setItem(row, "SEQ_NO", contractD.getItemInt(row - 1, "SEQ_NO") + 1);
            }
            if (!contractD.getItemString(row, "PACKAGE_CODE").equals("")
                    && !contractD.getItemString(row, "PAT_NAME").equals("")
                    && !contractD.getItemString(row, "MR_NO").equals("")) {// wanglong add 20141225
            	contractD.setRoleType(roleType);
                contractD.insertRow(contractD.getItemString(row, "COMPANY_CODE"),
                                    contractD.getItemString(row, "CONTRACT_CODE"),
                                    contractD.getItemString(row, "CONTRACT_DESC"),
                                    contractD.getItemDouble(row, "DISCNT"),
                                    contractD.getItemInt(row, "SEQ_NO") + 1);// 插入新的一行
            }
            mrTab.setDSValue();
            tNode.setValue(mrNo);// add by wanglong 20130829
            tNode.getTable().grabFocus();
            mrTab.setSelectedColumn(tNode.getTable().getColumnIndex("PACKAGE_CODE"));
            setColorTable();
            return false;
        }
        // 判断是否为有效身份证号
        if ("IDNO".equalsIgnoreCase(colName)) {
            String strValue = ((String) value).trim();
            if (strValue.equals("")) {
                return true;
            }
//            String strOldValue = (String) oldValue;
            String isForeigner = contractD.getItemString(row, "FOREIGNER_FLG");
            String checkid = checkID(strValue);
            if (!checkid.equals("TRUE") && !checkid.equals("FALSE")) {// add by wanglong 20130409
                this.messageBox_("身份证校验位错误，最后一位应该为" + checkid.substring(17, 18));
                contractD.setItem(row, "SEX_CODE", StringTool.isMaleFromID(checkid));// add by
                                                                                     // wanglong
                                                                                     // 20130417
                contractD.setItem(row, "BIRTHDAY", StringTool.getBirdayFromID(checkid));
                return false;// modify by wanglong 20130416
            }
            if (!isId(strValue)
                    && ("N".equalsIgnoreCase(isForeigner) || StringUtil.isNullString(isForeigner))) {
                this.messageBox_("身份证不正确");
                return true;
            }
            int rowCount = contractD.rowCount();
            for (int i = 0; i < rowCount; i++) {// wanglong add 20140606
                if (!contractD.isActive(i)) continue;
                if (i == row) continue;
                if (contractD.getItemString(i, "FOREIGNER_FLG").equals(isForeigner)
                        && contractD.getItemString(i, "IDNO").equals(strValue)) {
                    this.messageBox("该身份证号已存在");
                    return true;
                }
            }
            // ============xueyf modify 20120305 start
            TParm mrParm =
                    // modify by wanglong 20130502
                    new TParm(
                            TJDODBTool
                                    .getInstance()
                                    .select("SELECT SYS_PATINFO.*,OPT_DATE AS REPORT_DATE FROM SYS_PATINFO WHERE IDNO='"
                                                    + strValue + "' ORDER BY MR_NO DESC"));
            // ============xueyf modify 20120305 stop
            boolean mergeMrFlg = false;//wanglong add 20140623
            if (mrParm.getCount("MR_NO") > 0) {// ///////add by wanglong 20130502
                if (this.messageBox("病案号合并", "系统中存在使用此证件号码的病患记录，是否查看是不是他（她）？\n", 2) == 0) {
                    Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", mrParm);
                    if (obj != null) {
                        TParm samePatParm = (TParm) obj;
                        contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                        contractD.setItem(row, "PAT_NAME", samePatParm.getData("PAT_NAME"));
                        contractD.setItem(row, "SEX_CODE", samePatParm.getData("SEX_CODE"));
                        contractD.setItem(row, "BIRTHDAY", samePatParm.getData("BIRTH_DATE"));
                        contractD.setItem(row, "MARRIAGE_CODE",
                                          samePatParm.getData("MARRIAGE_CODE"));
						contractD.setItem(row, "ROLE_TYPE", roleType);
                        if (!samePatParm.getValue("CELL_PHONE").equals("")
                                && samePatParm.getValue("CELL_PHONE").matches("[^\u4e00-\u9fa5]*")) {// wanglong
                                                                                                     // add
                                                                                                     // 20140522
                                                                                                     // 自动取电话号码
                            contractD.setItem(row, "TEL", samePatParm.getData("CELL_PHONE"));
                        } else if (samePatParm.getValue("TEL_HOME").trim().length() == 11
                                && samePatParm.getValue("TEL_HOME").trim()
                                        .matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                            contractD.setItem(row, "TEL", samePatParm.getValue("TEL_HOME"));
                        }
                        mergeMrFlg = true;
                    }
                }
            }
            if (isId(strValue)) {// modify by wanglong 20130502
                String sexCode = StringTool.isMaleFromID(strValue);
                contractD.setItem(row, "SEX_CODE", sexCode);
                contractD.setItem(row, "BIRTHDAY", StringTool.getBirdayFromID(strValue));
            }
            if (!contractD.getItemString(row, "MR_NO").equals("")// wanglong add 20140623
                    && !contractD.getItemString(row, "MR_NO").equals("Y") && mergeMrFlg == false) {
                for (int j = 0; j < patLogSQLList.size(); j++) {
                    if ((patLogSQLList.get(j).indexOf(contractD.getItemString(row, "MR_NO")) != -1 && patLogSQLList
                            .get(j).indexOf(contractD.getItemString(row, "FOREIGNER_FLG")) != -1)
                            || (patLogSQLList.get(j).indexOf(contractD.getItemString(row, "MR_NO")) != -1 && patLogSQLList
                                    .get(j).indexOf(contractD.getItemString(row, "身份证")) != -1)) {
                        patLogSQLList.remove(j);
                        j--;
                    }
                }
                String updateSQL =
                        "UPDATE SYS_PATINFO SET FOREIGNER_FLG='#', IDNO='#', OPT_USER='@', OPT_DATE=SYSDATE, OPT_TERM='@' WHERE MR_NO='@'";
                updateSQL = updateSQL.replaceFirst("#", isForeigner);
                updateSQL = updateSQL.replaceFirst("#", strValue);
                updateSQL = updateSQL.replaceFirst("@", Operator.getID());
                updateSQL = updateSQL.replaceFirst("@", Operator.getIP());
                updateSQL = updateSQL.replaceFirst("@", contractD.getItemString(row, "MR_NO"));
                String insertSql =
                        "INSERT INTO SYS_PATLOG(MR_NO, OPT_DATE, MODI_ITEM, ITEM_OLD, ITEM_NEW, OPT_USER, OPT_TERM ) "
                                + "VALUES('@', SYSDATE, '#', '#', '#', '@', '@')";
                insertSql = insertSql.replaceFirst("@", contractD.getItemString(row, "MR_NO"));
                insertSql = insertSql.replaceFirst("#", "身份证");
                insertSql = insertSql.replaceFirst("#", TCM_Transform.getString(oldValue));
                insertSql = insertSql.replaceFirst("#", strValue);
                insertSql = insertSql.replaceFirst("@", Operator.getID());
                insertSql = insertSql.replaceFirst("@", Operator.getIP());
                patLogSQLList.add(updateSQL);
                patLogSQLList.add(insertSql);
                if ("N".equalsIgnoreCase(isForeigner) || StringUtil.isNullString(isForeigner)) {
                    TParm parm =
                            PatTool.getInstance().getInfoForMrno(contractD.getItemString(row,
                                                                                         "MR_NO"));
                    if (parm.getData("BIRTH_DATE", 0) != contractD.getItemData(row, "BIRTHDAY")) {
                        for (int j = 0; j < patLogSQLList.size(); j++) {
                            if ((patLogSQLList.get(j).indexOf(contractD.getItemString(row, "MR_NO")) != -1 && patLogSQLList
                                    .get(j).indexOf(contractD.getItemString(row, "BIRTH_DATE")) != -1)
                                    || (patLogSQLList.get(j).indexOf(contractD.getItemString(row, "MR_NO")) != -1 && patLogSQLList
                                            .get(j).indexOf(contractD.getItemString(row, "生日")) != -1)) {
                                patLogSQLList.remove(j);
                                j--;
                            }
                        }
                        String updateSQL1 =
                                "UPDATE SYS_PATINFO SET BIRTH_DATE=TO_DATE('#','YYYY/MM/DD'), OPT_USER='@', OPT_DATE=SYSDATE, OPT_TERM='@' WHERE MR_NO='@'";
                        updateSQL1 = updateSQL1.replaceFirst("#", StringTool.getString(contractD.getItemTimestamp(row, "BIRTHDAY"), "yyyy/MM/dd"));
                        updateSQL1 = updateSQL1.replaceFirst("@", Operator.getID());
                        updateSQL1 = updateSQL1.replaceFirst("@", Operator.getIP());
                        updateSQL1 = updateSQL1.replaceFirst("@", contractD.getItemString(row, "MR_NO"));
                        String insertSql1 =
                                "INSERT INTO SYS_PATLOG(MR_NO, OPT_DATE, MODI_ITEM, ITEM_OLD, ITEM_NEW, OPT_USER, OPT_TERM ) "
                                        + "VALUES('@', SYSDATE, '#', '#', '#', '@', '@')";
                        insertSql1 =
                                insertSql1.replaceFirst("@", contractD.getItemString(row, "MR_NO"));
                        insertSql1 = insertSql1.replaceFirst("#", "生日");
                        insertSql1 =
                                insertSql1.replaceFirst("#", StringTool.getString(parm
                                        .getTimestamp("BIRTH_DATE", 0), "yyyy/MM/dd"));
                        insertSql1 =
                                insertSql1.replaceFirst("#", StringTool.getString(contractD
                                        .getItemTimestamp(row, "BIRTHDAY"), "yyyy/MM/dd"));
                        insertSql1 = insertSql1.replaceFirst("@", Operator.getID());
                        insertSql1 = insertSql1.replaceFirst("@", Operator.getIP());
                        patLogSQLList.add(updateSQL1);
                        patLogSQLList.add(insertSql1);
                    }
                    //if (parm.getValue("SEX_CODE", 0) != contractD.getItemString(row, "SEX_CODE")) {wanglong历史记录
                    //zhanglei 修改 由于龙爷的！=被程序判定为真故更换判定条件
                    if (!parm.getValue("SEX_CODE", 0).equals(contractD.getItemString(row, "SEX_CODE"))) {
                    	//this.messageBox("11111111111");
                        for (int j = 0; j < patLogSQLList.size(); j++) {
                            if ((patLogSQLList.get(j).indexOf(contractD.getItemString(row, "MR_NO")) != -1 && patLogSQLList
                                    .get(j).indexOf(contractD.getItemString(row, "SEX_CODE")) != -1)
                                    || (patLogSQLList.get(j).indexOf(contractD.getItemString(row, "MR_NO")) != -1 && patLogSQLList
                                            .get(j).indexOf(contractD.getItemString(row, "性别")) != -1)) {
                                patLogSQLList.remove(j);
                                j--;
                            }
                        }
                        String updateSQL2 =
                                "UPDATE SYS_PATINFO SET SEX_CODE='#', OPT_USER='@', OPT_DATE=SYSDATE, OPT_TERM='@' WHERE MR_NO='@'";
                        updateSQL2 = updateSQL2.replaceFirst("#", contractD.getItemString(row, "SEX_CODE"));
                        updateSQL2 = updateSQL2.replaceFirst("@", Operator.getID());
                        updateSQL2 = updateSQL2.replaceFirst("@", Operator.getIP());
                        updateSQL2 = updateSQL2.replaceFirst("@", contractD.getItemString(row, "MR_NO"));
                        String insertSql2 =
                                "INSERT INTO SYS_PATLOG(MR_NO, OPT_DATE, MODI_ITEM, ITEM_OLD, ITEM_NEW, OPT_USER, OPT_TERM ) "
                                        + "VALUES('@', SYSDATE, '#', '#', '#', '@', '@')";
                        insertSql2 =
                                insertSql2.replaceFirst("@", contractD.getItemString(row, "MR_NO"));
                        insertSql2 = insertSql2.replaceFirst("#", "性别");
                        insertSql2 = insertSql2.replaceFirst("#", parm.getValue("SEX_CODE", 0));
                        insertSql2 =
                                insertSql2.replaceFirst("#",
                                                        contractD.getItemString(row, "SEX_CODE"));
                        insertSql2 = insertSql2.replaceFirst("@", Operator.getID());
                        insertSql2 = insertSql2.replaceFirst("@", Operator.getIP());
                        patLogSQLList.add(updateSQL2);
                        patLogSQLList.add(insertSql2);
                    }
                }
            } else if (contractD.getItemString(row, "MR_NO").equals("")// wanglong add 20140623
                    || contractD.getItemString(row, "MR_NO").equals("Y")) {
                contractD.setItem(row, "MR_NO", "Y");
            }
            if ((row == contractD.rowCount() - 1) && contractD.getItemInt(row, "SEQ_NO") == 0) {
                contractD.setItem(row, "SEQ_NO", contractD.getItemInt(row - 1, "SEQ_NO") + 1);
            }
            contractD.setItem(row, "IS_VIP", "1");//身份 默认为普通，add by huangjw 20160920
            if (!contractD.getItemString(row, "PACKAGE_CODE").equals("")
                    && !contractD.getItemString(row, "PAT_NAME").equals("")
                    && !contractD.getItemString(row, "MR_NO").equals("")) {// wanglong add 20141225
            	contractD.setRoleType(roleType);
                contractD.insertRow(contractD.getItemString(row, "COMPANY_CODE"),
                                    contractD.getItemString(row, "CONTRACT_CODE"),
                                    contractD.getItemString(row, "CONTRACT_DESC"),
                                    contractD.getItemDouble(row, "DISCNT"),
                                    contractD.getItemInt(row, "SEQ_NO") + 1);// 插入新的一行
            }
            mrTab.setDSValue();
            tNode.getTable().grabFocus();
            mrTab.setSelectedColumn(tNode.getTable().getColumnIndex("PACKAGE_CODE"));
            setColorTable();
            return false;
        }
        // 如果是除了身份证号的其他列则判断身份证号没填就不能填写
        String buffer = contractD.isFilter() ? contractD.FILTER : contractD.PRIMARY;//wanglong add 20141225
        if (TCM_Transform.isNull(contractD.getItemData(row, "IDNO", buffer))
                && !TCM_Transform.getBoolean(contractD.getItemData(row, "FOREIGNER_FLG", buffer))) {
            this.messageBox_("请先输入身份证号");
            return true;
        }
        // 病患姓名
        boolean mergeMrFlg = false;//wanglong add 20140623
        if ("PAT_NAME".equalsIgnoreCase(colName)) {
            String strValue = (String) value;
            String strOldValue = (String) oldValue;
            if (strValue.equalsIgnoreCase(strOldValue) || StringUtil.isNullString(strValue)) {
                return true;
            }
            String mrNo1 = contractD.getItemString(row, "MR_NO");
            if (!StringUtil.isNullString(mrNo1) && !mrNo1.equalsIgnoreCase("Y")) {// modify by
                                                                                  // wanglong
                                                                                  // 20130608
                String contractCode1 = contractD.getItemString(row, "CONTRACT_CODE");
                String sql1 =
                        "SELECT * FROM HRM_PATADM WHERE MR_NO = '" + mrNo1
                                + "' AND CONTRACT_CODE = '" + contractCode1 + "'";
                // System.out.println("-----------sql1---------------"+sql1);
                TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
                if (result1.getErrCode() < 0) {
                    this.messageBox("检查人员信息出错");
                    return true;
                }
                if (result1.getCount() > 0) {
                    this.messageBox("医嘱已展开，不能更改姓名，否则会造成数据不匹配");// add by wanglong 20130308
                    return true;
                }
            } else if (!StringUtil.isNullString(mrNo1)) {
                String idNo = contractD.getItemString(row, "IDNO");
                String mrNo = contractD.getItemString(row, "MR_NO");
                Timestamp birthday = contractD.getItemTimestamp(row, "BIRTHDAY");
                // add by wanglong 20130223 增加同名同年龄的人员，病案号合并
                if (idNo.length() == 18 && isId(idNo) && mrNo.equals("Y")) {
                    Timestamp idBirthday = StringTool.getBirdayFromID(idNo);
                    String samePatSql =
                            "SELECT MR_NO,OPT_DATE AS REPORT_DATE,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS,MARRIAGE_CODE "
                                    + "    FROM SYS_PATINFO      "
                                    + "   WHERE PAT_NAME = '#' # "
                                    + "ORDER BY OPT_DATE DESC NULLS LAST";
                    samePatSql = samePatSql.replaceFirst("#", strValue);
                    String birthYear = StringTool.getString(idBirthday, "yyyy");
                    samePatSql =
                            samePatSql.replaceFirst("#", " AND TO_CHAR( BIRTH_DATE, 'yyyy') = '"
                                    + birthYear + "' ");
                    TParm result = new TParm(TJDODBTool.getInstance().select(samePatSql));
                    if (result.getCount() > 0) {
                        if (this.messageBox("病案号合并", "系统中存在和人员“" + strValue
                                + "”同名同年龄的病患记录，是否查看是不是他（她）？\n", 2) == 0) {
                            Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
                            if (obj != null) {
                                TParm patParm = (TParm) obj;
                                contractD.setItem(row, "MR_NO", patParm.getValue("MR_NO"));
                                mergeMrFlg = true;
                            }
                        }
                    }
                }
                // add by wanglong 20130223 增加同名同年龄的人员，病案号合并
                else if (birthday != null && mrNo.equals("Y")) {
                    String samePatSql =
                            "SELECT MR_NO,OPT_DATE AS REPORT_DATE,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS,MARRIAGE_CODE "
                                    + "    FROM SYS_PATINFO      "
                                    + "   WHERE PAT_NAME = '#' # "
                                    + "ORDER BY OPT_DATE DESC NULLS LAST";
                    samePatSql = samePatSql.replaceFirst("#", strValue);
                    String birthYear = StringTool.getString(birthday, "yyyy");
                    samePatSql =
                            samePatSql.replaceFirst("#", " AND TO_CHAR( BIRTH_DATE, 'yyyy') = '"
                                    + birthYear + "' ");
                    TParm result = new TParm(TJDODBTool.getInstance().select(samePatSql));
                    if (result.getCount() > 0) {
                        if (this.messageBox("病案号合并", "系统中存在和人员“" + strValue
                                + "”同名同年龄的病患记录，是否查看是不是他（她）？\n", 2) == 0) {
                            Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
                            if (obj != null) {
                                TParm patParm = (TParm) obj;
                                contractD.setItem(row, "MR_NO", patParm.getValue("MR_NO"));
                                mergeMrFlg = true;
                            }
                        }
                    }
                }
                contractD.setItem(row, "COVER_FLG", "N");
            }
            if (!contractD.getItemString(row, "MR_NO").equals("")// wanglong add 20140623
                    && !contractD.getItemString(row, "MR_NO").equals("Y") && mergeMrFlg == false) {
                for (int j = 0; j < patLogSQLList.size(); j++) {
                    if ((patLogSQLList.get(j).indexOf(contractD.getItemString(row, "MR_NO")) != -1 && patLogSQLList
                            .get(j).indexOf(contractD.getItemString(row, "PAT_NAME")) != -1)
                            || (patLogSQLList.get(j).indexOf(contractD.getItemString(row, "MR_NO")) != -1 && patLogSQLList
                                    .get(j).indexOf(contractD.getItemString(row, "姓名")) != -1)) {
                        patLogSQLList.remove(j);
                        j--;
                    }
                }
                String updateSQL =
                        "UPDATE SYS_PATINFO SET PAT_NAME='#', OPT_USER='@', OPT_DATE=SYSDATE, OPT_TERM='@' WHERE MR_NO='@'";
                updateSQL = updateSQL.replaceFirst("#", strValue);
                updateSQL = updateSQL.replaceFirst("@", Operator.getID());
                updateSQL = updateSQL.replaceFirst("@", Operator.getIP());
                updateSQL = updateSQL.replaceFirst("@", contractD.getItemString(row, "MR_NO"));
                String insertSql =
                        "INSERT INTO SYS_PATLOG(MR_NO, OPT_DATE, MODI_ITEM, ITEM_OLD, ITEM_NEW, OPT_USER, OPT_TERM ) "
                                + "VALUES('@', SYSDATE, '#', '#', '#', '@', '@')";
                insertSql = insertSql.replaceFirst("#", "姓名");
                insertSql = insertSql.replaceFirst("#", TCM_Transform.getString(oldValue));
                insertSql = insertSql.replaceFirst("#", strValue);
                insertSql = insertSql.replaceFirst("@", contractD.getItemString(row, "MR_NO"));
                insertSql = insertSql.replaceFirst("@", Operator.getID());
                insertSql = insertSql.replaceFirst("@", Operator.getIP());
                patLogSQLList.add(updateSQL);
                patLogSQLList.add(insertSql);
            }
            String py = SystemTool.getInstance().charToCode(tNode.getValue() + "");
            contractD.setItem(row, "PY1", py);
            contractD.setActive(row, true);
            String comCode = contractM.getItemString(conDescRow, "COMPANY_CODE");
            String contractCode = contractM.getItemString(conDescRow, "CONTRACT_CODE");
            String contractDesc = contractM.getItemString(conDescRow, "CONTRACT_DESC");
            Double discnt = contractM.getItemDouble(conDescRow, "DISCNT");
            // contractD.setItem(row, "MR_NO", "Y");
            int count = contractD.rowCount();
            if (contractD.isActive(contractD.rowCount() - 1)
                    && !contractD.getItemString(row, "PACKAGE_CODE").equals("")) {
                contractD.insertRow(comCode, contractCode, contractDesc, discnt, count + 1);//插入新的一行
            }
            // contractD.setItem(row, "SEQ_NO", count);
            mrTab.setDSValue();
            mrTab.getTable().grabFocus();
            mrTab.setSelectedRow(row);
            if (contractD.getItemString(row, "PACKAGE_CODE").equals("")) {
                mrTab.setSelectedColumn(mrTab.getColumnIndex("PACKAGE_CODE"));
            } else mrTab.setSelectedColumn(mrTab.getColumnIndex("STAFF_NO"));
            // onPackage();//delete by wanglong 20130314
            return false;
        }
        // 出生日期
        if ("BIRTHDAY".equalsIgnoreCase(colName)) {// add by wanglong 20130223
            Timestamp newBirthday = (Timestamp) value;
            String idNo = contractD.getItemString(row, "IDNO");
            String mrNo = contractD.getItemString(row, "MR_NO");
            String patName = contractD.getItemString(row, "PAT_NAME");
            if (idNo.length() == 18 && isId(idNo)) {
                Timestamp birthday = StringTool.getBirdayFromID(idNo);
                if (!newBirthday.equals(birthday)) {
                    this.messageBox("生日与身份证不符");
                    return true;
                }
            } else if (newBirthday != null && mrNo.equals("Y") && !patName.equals("")) {
                String samePatSql =
                        "SELECT MR_NO,OPT_DATE AS REPORT_DATE,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS,MARRIAGE_CODE "
                                + "    FROM SYS_PATINFO      "
                                + "   WHERE PAT_NAME = '#' # "
                                + "ORDER BY OPT_DATE DESC NULLS LAST";
                samePatSql = samePatSql.replaceFirst("#", patName);
                String birthYear = StringTool.getString(newBirthday, "yyyy");
                samePatSql =
                        samePatSql.replaceFirst("#", " AND TO_CHAR( BIRTH_DATE, 'yyyy') = '"
                                + birthYear + "' ");
                TParm result = new TParm(TJDODBTool.getInstance().select(samePatSql));
                if (result.getCount() > 0) {
                    if (this.messageBox("病案号合并", "系统中存在和人员“" + patName
                            + "”同名同年龄的病患记录，是否查看是不是他（她）？\n", 2) == 0) {
                        Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
                        if (obj != null) {
                            TParm patParm = (TParm) obj;
                            contractD.setItem(row, "MR_NO", patParm.getValue("MR_NO"));
                        }
                    }
                }
            }
        }
        // 预检时间
        if ("PRE_CHK_DATE".equalsIgnoreCase(colName)) {
            Timestamp now = contractD.getDBTime();
            Timestamp valueTime = (Timestamp) value;
            Timestamp oldValueTime = (Timestamp) oldValue;
            if (valueTime.getTime() == oldValueTime.getTime()) {
                return true;
            }
            if (valueTime.getTime() < now.getTime()) {
                return true;
            }
            return false;
        }
        // 结,30,boolean;公司名称,140;合同名称,140;
        // 折扣,60,double;付款方式,100,PAY_TYPE;公司支付,100,double;结算方式,100;合约小计,80,double;总价,60,double;账单号,100;票据号,100
        // BILL_FLG; COMPANY_DESC;CONTRACT_DESC;DISCNT; PAY_TYPE; CP_PAY;
        // RECP_TYPE; TOT_AMT; REAL_COUNT; BILL_NO;RECEIPT_NO
        // 填写套餐代码时，将套餐的总价写入合同主项对象
        if ("PACKAGE_CODE".equalsIgnoreCase(colName)) {
            String mrNo1 = contractD.getItemString(row, "MR_NO");
            String contractCode1 = contractD.getItemString(row, "CONTRACT_CODE");
            String sql1 =
                    "SELECT * FROM HRM_PATADM WHERE MR_NO = '" + mrNo1 + "' AND CONTRACT_CODE = '"
                            + contractCode1 + "'";
            // System.out.println("-----------sql1---------------"+sql1);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql1));
            if (result.getErrCode() < 0) {
                this.messageBox("检查人员信息出错");
                return true;
            }
            if (result.getCount() > 0) {
                this.messageBox("医嘱已展开，不能更改套餐");// add by wanglong 20130308
                return true;
            }
            // boolean flag = false;
            // String sql1 =
            // "SELECT PACKAGE_DESC FROM HRM_PACKAGEM WHERE PACKAGE_CODE = '" + value + "'";
            // TParm result = new TParm(TJDODBTool.getInstance().select(sql1));
            // // System.out.println("====================PACKAGE_DESC=================="+result);
            // if (result.getErrCode() < 0) {
            // this.messageBox("查询套餐信息出错");
            // return true;
            // }
            // if (result.getCount() > 0) {
            // if (result.getValue("PACKAGE_DESC", 0).indexOf("自定义") < 0) {
            // this.messageBox("不能更改成此套餐，否则数据不匹配");// modify by wanglong 20130308
            // return true;
            // } else {
            // flag = true;
            // }
            // }
            // this.messageBox("===onTabMrValueChanged===");
            double subTotal = contractM.getItemDouble(conDescRow, "SUBTOTAL");
            double oldPrice = HRMPackageD.getPackageAmt(tNode.getOldValue() + "");
            double newPrice = HRMPackageD.getPackageAmt(tNode.getValue() + "");
            contractM.setItem(conDescRow, "SUBTOTAL", subTotal - oldPrice + newPrice);
            int mrSelectRow = mrTab.getSelectedRow();
            int mrSelectColumn = mrTab.getSelectedColumn();
            mrTab.setDSValue();
            int conSelectRow = conTab.getSelectedRow();
            int conSelectColumn = conTab.getSelectedColumn();
            // $$==== add by lx 2012-05-19 合同套餐变化，总价也变化start====$$//
            // this.messageBox("SUBTOTAL"+contractM.getItemDouble(conDescRow,
            // "SUBTOTAL"));
            contractM.setItem(conDescRow,
                              "TOT_AMT",
                              StringTool.round(contractM.getItemDouble(conDescRow, "SUBTOTAL")
                                      * contractM.getItemDouble(conDescRow, "DISCNT"), 2));
            // this.messageBox("==TOT_AMT=="+contractM.getItemDouble(conDescRow,
            // "TOT_AMT"));
            // $$==== add by lx 2012-05-19 合同套餐变化，总价也变化 end====$$//
            conTab.setDSValue(conDescRow);
            mrTab.setSelectedRow(mrSelectRow);
            mrTab.setSelectedColumn(mrSelectColumn);
            conTab.setSelectedRow(conSelectRow);
            conTab.setSelectedColumn(conSelectColumn);
            // $$======add by lx 2012-06-10 改变套餐则删除
            // HRM_ORDER(CONTRACT_CODE,MR_NO)表===$$//
            String flg = contractD.getItemString(row, "FOREIGNER_FLG").trim();// 外国人注记
            String idNo = contractD.getItemString(row, "IDNO");
            if (flg.equals("Y") || !idNo.equals("")) {// add by wanglong 20130107
                contractD.setActive(row, true);
                String comCode = contractM.getItemString(conDescRow, "COMPANY_CODE");
                String contractCode = contractM.getItemString(conDescRow, "CONTRACT_CODE");
                String contractDesc = contractM.getItemString(conDescRow, "CONTRACT_DESC");
                Double discnt = contractM.getItemDouble(conDescRow, "DISCNT");
                int count = contractD.rowCount();
                if (contractD.isActive(count - 1)) {// modify by wanglong 20130318
                    if (contractD.getItemString(row, "MR_NO").equals("")) {// add by wanglong
                                                                           // 20130327
                        contractD.setItem(row, "MR_NO", "Y");
                    }
                    int newRow = contractD.insertRow();
                    Timestamp now = SystemTool.getInstance().getDate();
                    contractD.setItem(newRow, "COMPANY_CODE", comCode);
                    contractD.setItem(newRow, "CONTRACT_CODE", contractCode);
                    contractD.setItem(newRow, "CONTRACT_DESC", contractDesc);
                    contractD.setItem(newRow, "PRE_CHK_DATE", now);
                    contractD.setItem(newRow, "PACKAGE_CODE", contractD.packageCode);
                    // this.setItem(newRow, "MR_NO", "Y");
                    contractD.setItem(newRow, "OPT_USER", Operator.getID());
                    contractD.setItem(newRow, "OPT_TERM", Operator.getIP());
                    contractD.setItem(newRow, "DISCNT", discnt);
                    contractD.setItem(newRow, "OPT_DATE", now);
                    contractD.setItem(newRow, "SEQ_NO", contractD.getItemInt(row, "SEQ_NO") + 1);
                    contractD.setItem(newRow, "ROLE_TYPE", roleType);
                    contractD.setActive(newRow, false);
                    // contractD.insertRow(comCode, contractCode, contractDesc, discnt, count + 1);
                }
                // contractD.setItem(row, "SEQ_NO", count);
                mrTab.setDSValue();
                mrTab.getTable().grabFocus();
                mrTab.setSelectedRow(row);
            }
            // String contractCode = contractM.getItemString(conDescRow, "CONTRACT_CODE");
            // String strMrNo = mrTab.getItemString(mrSelectRow, "MR_NO");
            // String patADmSQL = "SELECT CASE_NO FROM HRM_PATADM WHERE MR_NO='" + strMrNo + "'";
            // patADmSQL += " AND CONTRACT_CODE='" + contractCode + "'";
            // TParm parm = new TParm(TJDODBTool.getInstance().select(patADmSQL));
            // String caseNo = parm.getValue("CASE_NO", 0);
            // if (flag == true) {// modify by wanglong 20130308
            // String updateADMSQL =
            // "update HRM_PATADM set package_code = '" + value + "' WHERE CASE_NO='"
            // + caseNo + "'";
            // updateAdmSql = StringTool.copyArray(updateAdmSql, new String[]{updateADMSQL });
            // }
            // System.out.println("=====caseNo======="+caseNo);
            // String delADMSQL = "DELETE FROM HRM_PATADM WHERE CASE_NO='" + caseNo + "'";
            // TJDODBTool.getInstance().update(delADMSQL);
            // String sql = "DELETE FROM HRM_ORDER WHERE MR_NO='" + strMrNo + "'";
            // sql += " AND CONTRACT_CODE='" + contractCode + "' AND CASE_NO='" + caseNo + "' ";
            // // System.out.println("=====sql======="+sql);
            // TJDODBTool.getInstance().update(sql);
            // $$======add by lx 2012-06-10 改变套餐则删除
            // HRM_ORDER(CONTRACT_CODE,MR_NO)表===$$//
            return false;
        }
        // 序号
        if ("SEQ_NO".equalsIgnoreCase(colName)) {
            String strValue = (String) value;
            if (strValue.trim().equals("")) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 删除事件
     */
    public void onDelete() {
        if (StringUtil.isNullString(tableName)) {
            this.messageBox_("没有删除数据");
            return;
        }
        // 如果删除团体信息，则合同主项、细相信息同时删除
        if ("COMPANY".equalsIgnoreCase(tableName)
                && ((TPanel) this.getComponent("tPanel_1")).isShowing()) {
            int row = comTab.getSelectedRow();
            String companyCode = company.getItemString(row, "COMPANY_CODE");
            // ===============add by wanglong 20130314
            String conSql =
                    "SELECT CONTRACT_CODE FROM HRM_CONTRACTM WHERE COMPANY_CODE = '"
                            + companyCode + "'";// add by wanglong 20130304
            TParm conParm = new TParm(TJDODBTool.getInstance().select(conSql));
            if (conParm.getErrCode() != 0) {
                this.messageBox("查询团体报到信息出错");
                return;
            }
            if (conParm.getCount() > 0) {
                this.messageBox("请先删除其中的合同信息");
                return;
            }
            if (this.messageBox("提示", "是否删除", 2) != 0) {
                return;
            }
            company.deleteRow(row);
            contractM.deleteContract(companyCode);
            contractD.deleteContractByCompanyCode(companyCode);
            comTab.setDSValue();
            conTab.setDSValue();
            mrTab.setDSValue();
            String[] sql = company.getUpdateSQL();
            sql = StringTool.copyArray(sql, contractM.getUpdateSQL());
            sql = StringTool.copyArray(sql, contractD.getUpdateSQL());
            TParm inParm = new TParm();
            Map<String,String[]> inMap = new HashMap<String,String[]>();
            // for (int i = 0; i < sql.length; i++) {
            // System.out.println("=============sql[" + i + "]==========" + sql[i]);
            // }
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm result =
                    TIOM_AppServer.executeAction("action.hrm.HRMCompanyAction", "onSaveContract",
                                                 inParm);
            if (result.getErrCode() != 0) {
                // this.messageBox_(result.getErrText());
                this.messageBox("E0001");
                return;
            }
            company.resetModify();
            contractM.resetModify();
            contractD.resetModify();
            contractD.getPat().resetModify();
            this.messageBox("P0001");
            // =============add end
            return;
        }
        // 如果删除合同主项信息，则合同细相同时删除
        if ("CONTRACT".equalsIgnoreCase(tableName)
                && ((TPanel) this.getComponent("tPanel_2")).isShowing()) {
            int row = conTab.getSelectedRow();
            String contractCode = contractM.getItemString(row, "CONTRACT_CODE");
            // ================add by wanglong 20130314
            boolean coverFlg = false;
            int count = contractD.rowCount();
            for (int i = 0; i < count; i++) {
                if (TypeTool.getBoolean(contractD.getItemData(i, "COVER_FLG"))) {
                    coverFlg = true;
                    break;
                }
            }
            String checkSql =
                    "SELECT COUNT(*) NUM FROM HRM_PATADM WHERE CONTRACT_CODE = '#'"
                            .replaceFirst("#", contractCode);
            TParm check = new TParm(TJDODBTool.getInstance().select(checkSql));
            if (check.getErrCode() < 0) {
                this.messageBox("查询合同已展开信息失败");
                return;
            }
            if (coverFlg == true) {
                this.messageBox("该合同已有人员报到，无法删除");// modify by wanglong 20130417
                return;
            } else if (check.getCount() > 0 && check.getInt("NUM", 0) > 0) {
                if (this.messageBox("提示", "该合同有人员医嘱已展开，是否继续删除", 2) != 0) {
                    return;
                }
            } else {
                if (this.messageBox("提示", "是否删除", 2) != 0) {
                    return;
                }
            }
            contractM.deleteRow(row);
            conTab.setDSValue();
            contractD.deleteContract(contractCode);
            mrTab.setDSValue();
            String[] sql = company.getUpdateSQL();
            sql = StringTool.copyArray(sql, contractM.getUpdateSQL());
            sql = StringTool.copyArray(sql, contractD.getUpdateSQL());
            String deleteHRMPatadmSql =
                    "DELETE FROM HRM_PATADM WHERE CASE_NO "
                            + "                IN (SELECT B.CASE_NO "
                            + "                      FROM HRM_CONTRACTD A, HRM_PATADM B "
                            + "                     WHERE A.CONTRACT_CODE = B.CONTRACT_CODE "
                            + "                       AND A.MR_NO = B.MR_NO "
                            + "                       AND B.COVER_FLG <> 'Y' "
                            + "                       AND A.CONTRACT_CODE = '#')";
            deleteHRMPatadmSql = deleteHRMPatadmSql.replaceFirst("#", contractCode);
            sql = StringTool.copyArray(sql, new String[]{deleteHRMPatadmSql });
            String updateMedApplySql =
                    "UPDATE MED_APPLY "
                            + "   SET STATUS = '9',"
                            + "       OPT_USER = '@',"
                            + "       OPT_DATE = SYSDATE,"
                            + "       OPT_TERM = '&' "
                            + " WHERE (CAT1_TYPE, APPLICATION_NO, ORDER_NO, SEQ_NO)"
                            + "    IN (SELECT DISTINCT C.CAT1_TYPE,C.MED_APPLY_NO, C.CASE_NO,C.SEQ_NO "
                            + "          FROM HRM_CONTRACTD A, HRM_PATADM B, HRM_ORDER C "
                            + "         WHERE A.CONTRACT_CODE = B.CONTRACT_CODE "
                            + "           AND A.MR_NO = B.MR_NO "
                            + "           AND B.CASE_NO = C.CASE_NO "
                            + "           AND B.COVER_FLG <> 'Y' "
                            + "           AND C.MED_APPLY_NO IS NOT NULL "
                            + "           AND A.CONTRACT_CODE = '#')";
            updateMedApplySql = updateMedApplySql.replaceFirst("#", contractCode);
            updateMedApplySql = updateMedApplySql.replaceFirst("@", Operator.getID());
            updateMedApplySql = updateMedApplySql.replaceFirst("&", Operator.getIP());
            sql = StringTool.copyArray(sql, new String[]{updateMedApplySql });
            TParm inParm = new TParm();
            Map<String,String[]> inMap = new HashMap<String,String[]>();
            // for (int i = 0; i < sql.length; i++) {
            // System.out.println("=============sql["+i+"]=========="+sql[i]);
            // }
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm result =
                    TIOM_AppServer.executeAction("action.hrm.HRMCompanyAction", "onSaveContract",
                                                 inParm);
            if (result.getErrCode() != 0) {
                // this.messageBox_(result.getErrText());
                this.messageBox("E0001");
                return;
            }
            company.resetModify();
            contractM.resetModify();
            contractD.resetModify();
            contractD.getPat().resetModify();
            this.messageBox("P0001");
            onTabChanged();
            // =============add end
            return;
        }
        // 合同细相信息点中某行进行删除
        if ("MR".equalsIgnoreCase(tableName)
                && ((TPanel) this.getComponent("tPanel_2")).isShowing()) {
            int row = mrTab.getSelectedRow();
            if (TypeTool.getBoolean(contractD.getItemData(row, "COVER_FLG"))) {
                this.messageBox("该人员已报到，不能删除");// add by wanglong 20130314
                return;
            }
            //=======================add by wanglong 20130922
            String mrSql =
                    "SELECT DISTINCT MR_NO FROM HRM_PATADM WHERE CONTRACT_CODE = '#' AND MR_NO = '#'";
            mrSql = mrSql.replaceFirst("#", contractD.getItemString(row, "CONTRACT_CODE"));
            mrSql = mrSql.replaceFirst("#", contractD.getItemString(row, "MR_NO"));
            TParm mrParm = new TParm(TJDODBTool.getInstance().select(mrSql));
            if (mrParm.getErrCode() != 0) {
                this.messageBox("查询病患医嘱展开信息出错");
                return;
            }
            if (mrParm.getCount() > 0) {
                this.messageBox("该人员医嘱已展开，不能删除");
                return;
            }
            //=======================add end
            contractD.deleteRow(row);
            mrTab.setDSValue();
            setColorTable();
            // chenxi modify 20130329 ==========================================
            String[] sql = contractD.getUpdateSQL();
            String deleteHRMPatadmSql =
                    "DELETE FROM HRM_PATADM WHERE CASE_NO "
                            + "                IN (SELECT B.CASE_NO "
                            + "                      FROM HRM_CONTRACTD A, HRM_PATADM B "
                            + "                     WHERE A.CONTRACT_CODE = B.CONTRACT_CODE "
                            + "                       AND A.MR_NO = B.MR_NO "
                            + "                       AND B.COVER_FLG <> 'Y' "
                            + "                       AND A.CONTRACT_CODE = '#' "
                            + "                       AND A.MR_NO = '#')";
            deleteHRMPatadmSql = deleteHRMPatadmSql.replaceFirst("#", contractD.getItemString(row, "CONTRACT_CODE"));
            deleteHRMPatadmSql = deleteHRMPatadmSql.replaceFirst("#", contractD.getItemString(row, "MR_NO"));
            sql = StringTool.copyArray(sql, new String[]{deleteHRMPatadmSql });
            String updateMedApplySql =
                    "UPDATE MED_APPLY "
                            + "   SET STATUS = '9',"
                            + "       OPT_USER = '@',"
                            + "       OPT_DATE = SYSDATE,"
                            + "       OPT_TERM = '&' "
                            + " WHERE (CAT1_TYPE, APPLICATION_NO, ORDER_NO, SEQ_NO)"
                            + "    IN (SELECT DISTINCT C.CAT1_TYPE,C.MED_APPLY_NO, C.CASE_NO,C.SEQ_NO "
                            + "          FROM HRM_CONTRACTD A, HRM_PATADM B, HRM_ORDER C "
                            + "         WHERE A.CONTRACT_CODE = B.CONTRACT_CODE "
                            + "           AND A.MR_NO = B.MR_NO "
                            + "           AND B.CASE_NO = C.CASE_NO "
                            + "           AND B.COVER_FLG <> 'Y' "
                            + "           AND C.MED_APPLY_NO IS NOT NULL "
                            + "           AND A.CONTRACT_CODE = '#' "
                            + "           AND A.MR_NO = '#')";
            updateMedApplySql = updateMedApplySql.replaceFirst("#", contractD.getItemString(row, "CONTRACT_CODE"));
            updateMedApplySql = updateMedApplySql.replaceFirst("#", contractD.getItemString(row, "MR_NO"));
            updateMedApplySql = updateMedApplySql.replaceFirst("@", Operator.getID());
            updateMedApplySql = updateMedApplySql.replaceFirst("&", Operator.getIP());
            sql = StringTool.copyArray(sql, new String[]{updateMedApplySql });
            TParm inParm = new TParm();
            Map<String,String[]> inMap = new HashMap<String,String[]>();
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm result =
                    TIOM_AppServer.executeAction("action.hrm.HRMCompanyAction", "onSaveContract",
                                                 inParm);
            if (result.getErrCode() != 0) {
                // this.messageBox_(result.getErrText());
                this.messageBox("E0001");
                return;
            }
            company.resetModify();
            contractM.resetModify();
            contractD.resetModify();
            contractD.getPat().resetModify();
            // ============================//add by wanglong20130502
            TParm parm =
                    contractM.onQueryAmt(company.getItemString(comTab.getSelectedRow(),
                                                               "COMPANY_CODE"));
            int count = contractM.rowCount();
            for (int i = 0; i < count; i++) {
                for (int j = 0; j < parm.getCount(); j++) {
                    if (contractM.getItemString(i, "CONTRACT_CODE")
                            .equals(parm.getValue("CONTRACT_CODE", j))) {
                        contractM.setItem(i, "SUBTOTAL", parm.getDouble("SUBTOTAL", j));
                        contractM.setItem(i, "TOT_AMT", parm.getDouble("TOT_AMT", j));
                    }
                }
            }
            parm =
                    contractM.updateAmt(company.getItemString(comTab.getSelectedRow(),
                                                              "COMPANY_CODE"));
            // ============================add end
            // this.messageBox("P0001");
            // chenxi modify 20130329 ===========================================
            // onPackage();//delete by wanglong 20130314
            // return;
        }
    }

    /**
     * 保存
     */
    public void onSave() {
        comTab.acceptText();
        conTab.acceptText();
        mrTab.acceptText();
        int rowCount = contractD.rowCount();
        for (int i = 0; i < rowCount; i++) {
            if (!contractD.isActive(i)) continue;
            String flg = contractD.getItemString(i, "FOREIGNER_FLG").trim();// 外国人注记
            String idNo = contractD.getItemString(i, "IDNO").trim();// 身份证号
            String sexCode = contractD.getItemString(i, "SEX_CODE").trim();// 性别
            String packageCode = contractD.getItemString(i, "PACKAGE_CODE").trim();// 套餐
            String birthday = contractD.getItemString(i, "BIRTHDAY");// 出生日期
            String patName = contractD.getItemString(i, "PAT_NAME").trim();// 姓名
            if (!flg.equals("Y") && idNo.equals("")) {
                if (!patName.equals("")) {
                    this.messageBox("第" + (i + 1) + "行，姓名为 " + patName + " 的人员身份证号为空！");
                } else this.messageBox("第" + (i + 1) + "行，身份证号为空！");
                return;
            }
            if (sexCode.equals("")) {
                if (!patName.equals("")) {
                    this.messageBox("第" + (i + 1) + "行，姓名为 " + patName + " 的人员性别为空！");
                } else this.messageBox("第" + (i + 1) + "行，性别为空！");
                return;
            }
            if (birthday == null || birthday.length() == 0) {
                if (!patName.equals("")) {
                    this.messageBox("第" + (i + 1) + "行，姓名为 " + patName + " 的人员出生日期为空！");
                } else this.messageBox("第" + (i + 1) + "行，出生日期为空！");
                return;
            }
            if (packageCode.equals("")) {
                if (!patName.equals("")) {
                    this.messageBox("第" + (i + 1) + "行，姓名为 " + patName + " 的人员套餐为空！");
                } else this.messageBox("第" + (i + 1) + "行，套餐为空！");
                return;
            }
            if (patName.equals("")) {
                if (!idNo.equals("")) {
                    this.messageBox("第" + (i + 1) + "行，身份证为 " + idNo + " 的人员姓名为空！");
                } else this.messageBox("第" + (i + 1) + "行，人员姓名为空！");
                return;
            }
        }
        // 查重
        Map<String, Integer> singleMrRow = new HashMap<String, Integer>();
        Map<String, Integer> singleMrSeq = new HashMap<String, Integer>();
        Map<String, Integer> singleIdRow = new HashMap<String, Integer>();
        Map<String, Integer> singleIdSeq = new HashMap<String, Integer>();
        for (int i = 0; i < rowCount; i++) {
            int size = singleMrRow.size();
            String mrNo = contractD.getItemString(i, "MR_NO");
            String flg = contractD.getItemString(i, "FOREIGNER_FLG");
            String idNo = contractD.getItemString(i, "IDNO");
            int seqNo = contractD.getItemInt(i, "SEQ_NO");
            if (!mrNo.equals("Y") && !mrNo.equals("")) {
                singleMrRow.put(mrNo, i);
                singleMrSeq.put(mrNo, seqNo);
                if ((size + 1) != singleMrRow.size()) {
                    if (!flg.equals("Y") && singleIdRow.containsKey(idNo)) {
                        this.messageBox("第" + (singleIdRow.get(idNo) + 1) + "行(序号："
                                + singleIdSeq.get(idNo) + ")与第" + (i + 1) + "行(序号：" + seqNo
                                + ")的人员身份证号相同");
                    } else {
                        this.messageBox("第" + (singleMrRow.get(idNo) + 1) + "行(序号："
                                + singleMrSeq.get(mrNo) + ")与第" + (i + 1) + "行(序号：" + seqNo
                                + ")的人员病案号相同");
                    }
                    return;
                } else {
                    singleIdRow.put(idNo, i);
                    singleIdSeq.put(idNo, seqNo);
                }
            }
        }
        /** 保存团体信息[公司标签]SQL */
        String[] sql = onSaveCom();
        if (sql == null) {
            sql = new String[]{};
        }
        // 取得合同主细相保存SQL，并验证
        String[] tempSql = onSaveContractM();
        if (tempSql == null) {
            tempSql = new String[]{};
        }
        sql = StringTool.copyArray(sql, tempSql);
        if (sql == null) {
            this.messageBox_("没有数据");
            return;
        }
        if (sql.length < 1) {
            this.messageBox_("没有数据");
            return;
        }
        // sql = StringTool.copyArray(sql, updateAdmSql);//modify by wanglong 20130308
//        for (int i = 0; i < sql.length; i++) {
//            System.out.println("------onSave----sql["+i+"]-----"+sql[i]);
//        }
        // 保存动作配参并验证结果
        TParm inParm = new TParm();
        Map<String,String[]> inMap = new HashMap<String,String[]>();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result =
                TIOM_AppServer.executeAction("action.hrm.HRMCompanyAction", "onSaveContract",
                                             inParm);
        if (result.getErrCode() != 0) {
            this.messageBox_(result.getErrText());
            this.messageBox("E0001");
            return;
        }
        company.resetModify();
        contractM.resetModify();
        contractD.resetModify();
        contractD.getPat().resetModify();
        // ============================//add by wanglong20130502
        TParm parm =
                contractM
                        .onQueryAmt(company.getItemString(comTab.getSelectedRow(), "COMPANY_CODE"));
        int count = contractM.rowCount();
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < parm.getCount(); j++) {
                if (contractM.getItemString(i, "CONTRACT_CODE")
                        .equals(parm.getValue("CONTRACT_CODE", j))) {
                    contractM.setItem(i, "SUBTOTAL", parm.getDouble("SUBTOTAL", j));
                    contractM.setItem(i, "TOT_AMT", parm.getDouble("TOT_AMT", j));
                }
            }
        }
        parm = contractM.updateAmt(company.getItemString(comTab.getSelectedRow(), "COMPANY_CODE"));
        
        // ============================add end
        if (!StringUtil.isNullString(contractM.getItemString(contractM.rowCount() - 1,
                                                             "CONTRACT_DESC"))
                || contractM.rowCount() < 1) {
            contractM.insertRow(company.getItemString(comTab.getSelectedRow(), "COMPANY_CODE"));
        }
//        int seleRow = mrTab.getSelectedRow();
        int row = comTab.getSelectedRow(); // chenxi modify 20130329
        mrTab.setDSValue();
        conTab.setDSValue();
        comTab.setDSValue(); // chenxi modify 20130329
        if (row >= 0) comTab.setSelectedRow(row); // ==== chenxi modify 20130329
        onTabConClicked();
//        if (seleRow >= 0) mrTab.setSelectedRow(seleRow);
        if (!isChangeContract) {
            this.messageBox("P0001");
        }
        // this.onQuery();
    }

    /**
     * 清空TABLE
     */
    public void onSetDsValue() {
        comTab.setDSValue();
        conTab.setDSValue();
        mrTab.setDSValue();
    }

    /**
     * 保存事件(团体信息)[公司标签]
     */
    private String[] onSaveCom() {
        String[] sql = new String[]{};
        // System.out.println("selectrow========"+comTab.getSelectedRow());
        if (comTab.getSelectedRow() < 0) {
            String companyDesc = this.getValueString("COMPANY_DESC");
            if (StringUtil.isNullString(companyDesc)) {
                this.messageBox_("团体名称不能为空");
                return null;
            }
            if (company.isHaveSaveDesc(companyDesc) && !isChangeContract) {
                this.messageBox_("该团体已经存在");
                return null;
            }
            TParm parmTag =
                    this.getParmForTag("COMPANY_DESC;PY1;PY2;DESCRIPTION;ADMINISTRATOR;TEL;IND_TYPE_CODE;FAX_NO;CONTACTS_NAME;CONTACTS_TEL;POST_CODE;ADDRESS;E_MAIL");
            parmTag.setData("ROLE_TYPE", roleType);
            company.newCompany(parmTag);
        } else {
            int row = comTab.getSelectedRow();
            if (StringUtil.isNullString(this.getValueString("COMPANY_DESC"))) {
                this.messageBox_("团体名称不能为空");
                return null;
            }
            TParm parmTag =
                    this.getParmForTag("COMPANY_DESC;PY1;PY2;DESCRIPTION;ADMINISTRATOR;TEL;IND_TYPE_CODE;FAX_NO;CONTACTS_NAME;CONTACTS_TEL;POST_CODE;ADDRESS;E_MAIL");
            parmTag.setData("ROW", row);
            company.updateCompany(parmTag);
        }
        sql = company.getUpdateSQL();
        return sql;
    }

    /**
     * 保存合同信息
     */
    public String[] onSaveContractM() {
        int[] newMRows = contractM.getNewRows();
        for (int row : newMRows) {// add by wanglong 20130302
            String contractDesc = contractM.getItemString(row, "CONTRACT_DESC");
            if (contractDesc.trim().equals("")) {
                contractM.setActive(row, false);
            } else {
                contractM.setActive(row, true);
            }
        }
        String[] sql = contractM.getUpdateSQL();
        if (patLogSQLList.size() > 0) {// wanglong add 20140623
            sql = StringTool.copyArray(sql, (String[]) patLogSQLList.toArray(new String[0]));
        }
        ArrayList<String> sqlList = new ArrayList<String>();//add by wanglong 20130608 增加SYS_PatInfo表姓名同步
        int[] newDRows = contractD.getNewRows();
        for (int row : newDRows) {// add by wanglong 20130302
            String contractDesc = contractD.getItemString(row, "CONTRACT_DESC");
            if (contractDesc.trim().equals("")) {
                contractD.setActive(row, false);
            } else {
                contractD.setActive(row, true);
                String contractCode = contractD.getItemString(row, "CONTRACT_CODE");
                String updateSYSPatInfo =//modify by wanglong 20130829
                        "UPDATE SYS_PATINFO A SET "
                                + " (A.PAT_NAME, A.SEX_CODE, A.FOREIGNER_FLG, A.IDNO, A.TEL_HOME, A.CELL_PHONE, A.MARRIAGE_CODE) "
                                + " = (SELECT B.PAT_NAME, B.SEX_CODE, B.FOREIGNER_FLG, B.IDNO, B.TEL, B.TEL, B.MARRIAGE_CODE "
                                + "      FROM HRM_CONTRACTD B "
                                + "     WHERE B.CONTRACT_CODE = '#' "
                                + "       AND B.MR_NO = A.MR_NO),"
                                + "A.COMPANY_DESC = (SELECT C.COMPANY_DESC FROM HRM_CONTRACTM B, HRM_COMPANY C WHERE B.CONTRACT_CODE = '#' AND B.COMPANY_CODE = C.COMPANY_CODE),"
                                + "A.OPT_USER = '@', A.OPT_DATE = SYSDATE, A.OPT_TERM = '&' "
                                + " WHERE A.MR_NO IN (SELECT MR_NO FROM HRM_CONTRACTD WHERE CONTRACT_CODE = '#') ";
                updateSYSPatInfo = updateSYSPatInfo.replaceAll("#", contractCode);
                updateSYSPatInfo = updateSYSPatInfo.replaceFirst("@", Operator.getID());
                updateSYSPatInfo = updateSYSPatInfo.replaceFirst("&", Operator.getIP());
                if (!sqlList.contains(updateSYSPatInfo)) {
                    sqlList.add(updateSYSPatInfo);
                }
                String updateHrmPatAdm = // wanglong add 20141216
                        "UPDATE HRM_PATADM A SET "
                                + " (A.PAT_NAME, A.ID_NO, A.SEX_CODE, A.BIRTHDAY, A.TEL, A.PAT_DEPT, A.MARRIAGE_CODE) "
                                + " = (SELECT B.PAT_NAME B, B.IDNO, B.SEX_CODE, B.BIRTHDAY, B.TEL, B.PAT_DEPT, B.MARRIAGE_CODE "
                                + "      FROM HRM_CONTRACTD B "
                                + "     WHERE B.CONTRACT_CODE = A.CONTRACT_CODE "
                                + "       AND B.MR_NO = A.MR_NO),"
                                + "A.OPT_USER = '@', A.OPT_DATE = SYSDATE, A.OPT_TERM = '&' "
                                + " WHERE A.CASE_NO IN (SELECT C.CASE_NO FROM HRM_CONTRACTD B, HRM_PATADM C "
                                + "                      WHERE B.CONTRACT_CODE = '#' AND B.CONTRACT_CODE = C.CONTRACT_CODE AND B.MR_NO = C.MR_NO)";
                updateHrmPatAdm = updateHrmPatAdm.replaceFirst("#", contractCode);
                updateHrmPatAdm = updateHrmPatAdm.replaceFirst("@", Operator.getID());
                updateHrmPatAdm = updateHrmPatAdm.replaceFirst("&", Operator.getIP());
                if (!sqlList.contains(updateHrmPatAdm)) {
                    sqlList.add(updateHrmPatAdm);
                }
            }
        }
        sql = StringTool.copyArray(sql, contractD.getSql());///////////////////////////其中还生成SYS_PATINFO。
        //contractD.getSql()执行后会将DataStore状态清空，之后再使用getNewRows()或getModifiedRows()不准确
        sql = StringTool.copyArray(sql, contractD.getUpdateSQL());
        sql = StringTool.copyArray(sql, (String[]) sqlList.toArray(new String[0]));//add by wanglong 20130608 增加SYS_PatInfo表姓名同步
        if (sql == null || sql.length < 1) {
            return null;
        }
        return sql;
    }

    /**
     * 清空事件
     */
    public void onClear() {
        company.onQuery();
        comTab.setDSValue();
        comTab.clearSelection();
        
        // add by wangb 2016/6/21 START
        // 根据不同入口筛选不同数据(H_健康检查,PIC_一期临床,SYSDBA_最高权限)
        String filter = "";
		if (this.getPopedem("SYSDBA")) {
			filter = " ROLE_TYPE IN ('H','PIC') ";
		} else {
			filter = " ROLE_TYPE = '" + roleType + "' ";
		}
        comTab.setFilter(filter);
        comTab.filter();
        // add by wangb 2016/6/21 END
        
        contractM.onQuery();//wanglong add 20140512
        contractM.filt("#");
        conTab.setDSValue();
        conTab.clearSelection();
        contractD.onQuery("#", "#");//wanglong add 20140512
        contractD.filt("#");
        mrTab.setDSValue();
        mrTab.clearSelection();
        this.clearValue("COMPANY_CODE;COMPANY_DESC;PY1;PY2;DESCRIPTION;ADMINISTRATOR;TEL;IND_TYPE_CODE;FAX_NO;CONTACTS_NAME;CONTACTS_TEL;POST_CODE;ADDRESS;E_MAIL;COUNT;QUERY_CONTRACT;TOT_AMT;CONTRACT_DESC;COMPANY_DESC;PAT_NAME;MR_NO;PACKAGE_CODE");
    }

    /**
     * 页签点击事件,如果没有选中某团体，则不能显示合同主细相信息.
     */
    public void onTabChanged() {
        TTabbedPane tabbedPane = (TTabbedPane) this.getComponent("TAB_PANEL");
        if (tabbedPane.getSelectedIndex() == 0) {
            return;
        }
        if (comTab.getSelectedRow() < 0) {
            this.messageBox_("没有团体信息");
            tabbedPane.setSelectedIndex(0);
            return;
        }
        int row = comTab.getSelectedRow();
        String comCode = company.getItemString(row, "COMPANY_CODE");
        if (StringUtil.isNullString(comCode)) {
            this.messageBox_("取得团体信息失败");
            return;
        }
        contractM.setCompanyDesc(company.getItemString(row, "COMPANY_DESC"));
        contractM.filt(comCode);
        if (!StringUtil.isNullString(contractM.getItemString(contractM.rowCount() - 1,
                                                             "CONTRACT_DESC"))
                || contractM.rowCount() < 1) {
            // add by wangb 2016/06/22 设置角色类型
            contractM.setRoleType(roleType);
            contractM.insertRow(comCode);
        }
        // ============================//add by wanglong20130502
        TParm result = contractM.onQueryAmt(comCode);
        int count = contractM.rowCount();
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < result.getCount(); j++) {
                if (contractM.getItemString(i, "CONTRACT_CODE")
                        .equals(result.getValue("CONTRACT_CODE", j))) {
                    contractM.setItem(i, "SUBTOTAL", result.getDouble("SUBTOTAL", j));
                    contractM.setItem(i, "TOT_AMT", result.getDouble("TOT_AMT", j));
                }
            }
        }
        result = contractM.updateAmt(comCode);
        // ============================add end
        conTab.setDSValue();
        
        // add by wangb 2016/07/04 一期临床登录表格列名调整
        if (this.getPopedem("SYSDBA") || this.getPopedem("PIC")) {
        	// 选中的团体信息
        	String selRoleType = company.getItemString(row, "ROLE_TYPE");
        	String mrTabHeader = mrTab.getHeader();
        	roleType = selRoleType;
			if ("PIC".equals(selRoleType)) {
				mrTab.setHeader(mrTabHeader.replace("部门", "入组").replace("工号",
						"筛选号").replace("PACKAGE_H", "PACKAGE_PIC"));
				// modify by wangb 2016/08/31 防止来回切换Tab页造成多次拼接列
				if (!mrTab.getHeader().contains("方案编号")) {
					mrTab.setHeader(mrTab.getHeader() + ";方案编号,120;方案名称,200");
					mrTab.setParmMap(mrTab.getParmMap() + ";PLAN_NO;PLAN_DESC");
				}
			} else {
				mrTab.setHeader(mrTabHeader.replace("入组", "部门").replace("筛选号",
						"工号").replace("PACKAGE_PIC", "PACKAGE_H").replace(
						";方案编号,120;方案名称,200", ""));
				mrTab.setParmMap(mrTab.getParmMap().replace(
						";PLAN_NO;PLAN_DESC", ""));
			}
        }
    }

    /**
     * 通过excel导入员工信息
     * 1)excel第一行为表头，应包含：序号，姓名，身份证号，性别，套餐代码，外国注记，出生日期，工号，增项团体，电话，邮编，地址，预检时间，婚姻状态
     * 2)各列的顺序可变
     * 3)所有信息必须存在excel的第一个sheet页中
     */
    public void onInsertPatByExl() {// refactor by wanglong 20130116
        conTab.acceptText();
        if (conTab == null) {
            this.messageBox_("没有合同信息");
            return;
        }
        int clickRow = conTab.getSelectedRow();
        if (clickRow < 0) {
            this.messageBox_("没有合同信息");
            return;
        }
        if (conTab.getItemString(clickRow, "CONTRACT_DESC").trim().equals("")) {
            this.messageBox_("没有选择合同");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new FileFilter() {// 过滤xls文件 add by wanglong20130116

                    public boolean accept(File f) {
                        if (f.isDirectory()) {// 忽略文件夹
                            return true;
                        }
                        return f.getName().endsWith(".xls");
                    }

                    public String getDescription() {
                        return ".xls";
                    }
                });
        int option = fileChooser.showOpenDialog(null);
        TParm parm = new TParm();
        String discnt = conTab.getItemString(conTab.getSelectedRow(), "DISCNT");// 合同折扣率
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Workbook wb = Workbook.getWorkbook(file);
                Sheet st = wb.getSheet(0);
//                int row = st.getRows();
                int row = getRightRows(st);
                int column = st.getColumns();
                if (row <= 1 || column <= 0) {
                    this.messageBox("excel中没有数据");
                    return;
                }
                StringBuffer wrongMsg = new StringBuffer();
                // wrongMsg.append("excel文件名为：" + file.getName() + "\r\n");
                ArrayList<Integer> indexList = new ArrayList<Integer>();
                ArrayList<String> titleList = new ArrayList<String>();
                for (int j = 0; j < column; j++) {
                    String cell = st.getCell(j, 0).getContents();
                    if (cell.indexOf("套餐代码") != -1) {
                        indexList.add(j);
                        titleList.add("PACKAGE_CODE");
                        continue;
                    }
                    if (cell.indexOf("序号") != -1) {
                        indexList.add(j);
                        titleList.add("SEQ_NO");
                        continue;
                    }
                    if (cell.indexOf("工号") != -1) {
                        indexList.add(j);
                        titleList.add("STAFF_NO");
                        continue;
                    }
                    if (cell.indexOf("姓名") != -1) {
                        indexList.add(j);
                        titleList.add("PAT_NAME");
                        continue;
                    }
                    if (cell.indexOf("性别") != -1) {
                        indexList.add(j);
                        titleList.add("SEX_CODE");
                        continue;
                    }
                    if (cell.indexOf("身份证号") != -1) {
                        indexList.add(j);
                        titleList.add("IDNO");
                        continue;
                    }
                    if (cell.indexOf("婚姻状态") != -1) {
                        indexList.add(j);
                        titleList.add("MARRIAGE_CODE");
                        continue;
                    }
                    if (cell.indexOf("外国人注记") != -1) {
                        indexList.add(j);
                        titleList.add("FOREIGNER_FLG");
                        continue;
                    }
                    if (cell.indexOf("出生日期") != -1) {
                        indexList.add(j);
                        titleList.add("BIRTHDAY");
                        continue;
                    }
                    if (cell.indexOf("增项团体支付") != -1) {
                        indexList.add(j);
                        titleList.add("COMPANY_PAY_FLG");
                        continue;
                    }
                    if (cell.indexOf("电话") != -1) {
                        indexList.add(j);
                        titleList.add("TEL");
                        continue;
                    }
                    if (cell.indexOf("邮编") != -1) {
                        indexList.add(j);
                        titleList.add("POST_CODE");
                        continue;
                    }
                    if (cell.indexOf("地址") != -1) {
                        indexList.add(j);
                        titleList.add("ADDRESS");
                        continue;
                    }
                    if (cell.indexOf("预检时间") != -1) {
                        indexList.add(j);
                        titleList.add("PRE_CHK_DATE");
                        continue;
                    }
                    if (cell.indexOf("部门") != -1) {// add by wanglong 20130225
                        indexList.add(j);
                        titleList.add("PAT_DEPT");
                        continue;
                    }
                    if (cell.indexOf("病案号") != -1) {// wanglong add 20140924
                        indexList.add(j);
                        titleList.add("MR_NO");
                        continue;
                    }
                    if (cell.indexOf("身份") != -1) {// huangjw add 20160920
                        indexList.add(j);
                        titleList.add("IS_VIP");
                        continue;
                    }
                }
                column = indexList.size();
                if (!titleList.contains("PACKAGE_CODE")) {// 套餐代码
                    this.messageBox("缺少“套餐代码”列！（且为必填项）");
                    return;
                }
                if (!titleList.contains("SEQ_NO")) {// 序号
                    this.messageBox("缺少“序号”列！（非必填项）");
//                    return;
                }
                if (!titleList.contains("STAFF_NO")) {// 工号
                    this.messageBox("缺少“工号”列！（非必填项）");
//                    return;
                }
                if (!titleList.contains("PAT_NAME")) {// 姓名
                    this.messageBox("缺少“姓名”列！（且为必填项）");
                    return;
                }
                if (!titleList.contains("SEX_CODE")) {// 性别
                    this.messageBox("缺少“性别”列！（且为必填项）");
                    return;
                }
                if (!titleList.contains("IDNO")) {// 身份证号
                    this.messageBox("缺少“身份证号”列！（默认必须有值，外国人在“外国人注记”填写为Y之后，此项才可空缺）");
                    return;
                }
                if (!titleList.contains("MARRIAGE_CODE")) {// 婚姻状态
                    this.messageBox("缺少“婚姻状态”列！（有效值只能填写婚姻的编码，或者不填写）");
                    return;
                }
                if (!titleList.contains("FOREIGNER_FLG")) {// 外国人注记
                    this.messageBox("缺少“外国人注记”列！（有效值只能填写Y或N，或者不填写（视作N））");
//                    return;
                }
                if (!titleList.contains("BIRTHDAY")) {// 出生日期
                    this.messageBox("缺少“出生日期”列！（有效值的形式为yyyyMMdd，对于外国人，必须填写）");
//                    return;
                }
                if (!titleList.contains("COMPANY_PAY_FLG")) {// 增项团体支付
                    this.messageBox("缺少“增项团体支付”列！（有效值只能填写Y或者N，或者不填写（视作N））");
//                    return;
                }
                if (!titleList.contains("TEL")) {// 电话
                    this.messageBox("缺少“电话”列！（非必填项）");
//                    return;
                }
                if (!titleList.contains("POST_CODE")) {// 邮编
                    this.messageBox("缺少“邮编”列！（非必填项）");
//                    return;
                }
                if (!titleList.contains("ADDRESS")) {// 地址
                    this.messageBox("缺少“地址”列！（非必填项）");
//                    return;
                }
                if (!titleList.contains("PRE_CHK_DATE")) {// 预检时间
                    this.messageBox("缺少“预检时间”列（有效值的形式为yyyyMMdd，或者不填写）");
//                    return;
                }
                if (!titleList.contains("PAT_DEPT")) {// 部门 add by wanglong 20130225
                    this.messageBox("缺少“部门”列！（非必填项）");
//                    return;
                }
                if (!titleList.contains("MR_NO")) {// 病案号 wanglong add 20140924
                    this.messageBox("缺少“病案号”列！（非必填项）");
//                    return;
                }
                if (!titleList.contains("IS_VIP")) {// 身份 huangjw add 20160920
                    this.messageBox("缺少“身份”列！（非必填项）");
//                    return;
                }
                String sexSql = "SELECT ID FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX'";
                TParm sexParm = new TParm(TJDODBTool.getInstance().select(sexSql));
                if (sexParm.getErrCode() != 0 || sexParm.getCount() < 1) {
                    this.messageBox("获取系统中性别信息时出错");
                    return;
                }
                List<String> sexList = Arrays.asList(sexParm.getStringArray("ID"));
                String packageSql = "SELECT PACKAGE_CODE FROM HRM_PACKAGEM WHERE ACTIVE_FLG = 'Y'";
                TParm packageParm = new TParm(TJDODBTool.getInstance().select(packageSql));
                if (packageParm.getErrCode() != 0 || packageParm.getCount() < 1) {
                    this.messageBox("获取系统中套餐信息时出错");
                    return;
                }
                List<String> packageList = Arrays.asList(packageParm.getStringArray("PACKAGE_CODE"));
                String marriageSql =
                        "SELECT ID FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_MARRIAGE'";
                TParm marriageParm = new TParm(TJDODBTool.getInstance().select(marriageSql));
                if (marriageParm.getErrCode() != 0 || marriageParm.getCount() < 1) {
                    this.messageBox("获取系统中婚姻状态信息时出错");
                    return;
                }
                List<String> marriageList = Arrays.asList(marriageParm.getStringArray("ID"));
                int count = 0;
                for (int i = 1; i < row; i++) {// 一行一行加入excel中的数据
                    for (int j = 0; j < column; j++) {// 每次导入一行的所有列
                        String cell = st.getCell(j, i).getContents();
                        if (titleList.get(j).equals("PACKAGE_CODE")
                                || titleList.get(j).equals("PAT_NAME")
                                || titleList.get(j).equals("SEX_CODE")
                                || titleList.get(j).equals("IDNO")
                                || titleList.get(j).equals("FOREIGNER_FLG")
                                || titleList.get(j).equals("BIRTHDAY")
                                || titleList.get(j).equals("MARRIAGE_CODE")) {
                            parm.addData(titleList.get(j),
                                         cell.trim().replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5]", ""));
                        } else {
                            parm.addData(titleList.get(j), cell.trim());
                        }
                    }
                    parm.addData("DISCNT", discnt);// 折扣
                    count = parm.getCount("PAT_NAME");
                    if (parm.getData("SEX_CODE", count - 1).equals("男")) {//wanglong add 20140512
                        parm.setData("SEX_CODE", count - 1, "1");
                    } else if (parm.getData("SEX_CODE", count - 1).equals("女")) {
                        parm.setData("SEX_CODE", count - 1, "2");
                    }
                    if (parm.getData("MARRIAGE_CODE", count - 1).equals("未婚")) {// wanglong add 20140528
                        parm.setData("MARRIAGE_CODE", count - 1, "1");
                    } else if (parm.getData("MARRIAGE_CODE", count - 1).equals("已婚")) {
                        parm.setData("MARRIAGE_CODE", count - 1, "2");
                    } else if (parm.getData("MARRIAGE_CODE", count - 1).equals("离婚")) {
                        parm.setData("MARRIAGE_CODE", count - 1, "3");
                    } else if (parm.getData("MARRIAGE_CODE", count - 1).equals("丧偶")) {
                        parm.setData("MARRIAGE_CODE", count - 1, "4");
                    } else {
                        parm.setData("MARRIAGE_CODE", count - 1, "");
                    }
                    
                    if (parm.getData("IS_VIP", count - 1).equals("普通")) {// huangjw add 20160920
                        parm.setData("IS_VIP", count - 1, "1");
                    } else if (parm.getData("IS_VIP", count - 1).equals("VIP")) {
                        parm.setData("IS_VIP", count - 1, "2");
                    }
                    parm.setCount(count);
                    // ===================数据检查开始
                    String packageCode = parm.getValue("PACKAGE_CODE", count - 1);// 套餐代码
                    if (packageCode.equals("")) {
                        wrongMsg.append("excel第" + (i + 1) + "行,“套餐代码”列缺少值,将不会被导入\r\n");
                        this.messageBox("excel第" + (i + 1) + "行,“套餐代码”列缺少值！将不会被导入");
                        count--;
                        parm.removeRow(count);
                        parm.setCount(count);
                        continue;
                    }
                    if (!packageList.contains(packageCode)) {
                        wrongMsg.append("excel第" + (i + 1) + "行,“套餐代码”列的值(值：" + packageCode
                                + ")在系统中不存在,不会被导入\r\n");
                        this.messageBox("excel第" + (i + 1) + "行,“套餐代码”列的值在系统中不存在！将不会被导入");
                        count--;
                        parm.removeRow(count);
                        parm.setCount(count);
                        continue;
                    }
                    String patName = parm.getValue("PAT_NAME", count - 1);// 姓名
                    String mrNo = parm.getValue("MR_NO", count - 1);// 病案号 wanglong add 20140924
                    if (patName.equals("")&&mrNo.equals("")) {
                        wrongMsg.append("excel第" + (i + 1) + "行,“姓名”列缺少值,不会被导入\r\n");
                        this.messageBox("excel第" + (i + 1) + "行,“姓名”列缺少值！将不会被导入");
                        count--;
                        parm.removeRow(count);
                        parm.setCount(count);
                        continue;
                    }
                    String idNo = parm.getValue("IDNO", count - 1).toUpperCase();
                    String sexCode = parm.getValue("SEX_CODE", count - 1);
                    String birthday = parm.getValue("BIRTHDAY", count - 1);
                    String foreignerFlg = parm.getValue("FOREIGNER_FLG", count - 1);
                    // 中国人（18位身份证校验位错误，暂时不自动纠正）
                    String checkid = checkID(idNo);
                    if (!foreignerFlg.equalsIgnoreCase("Y") && !checkid.equals("TRUE")
                            && !checkid.equals("FALSE")) {// add by wanglong 20130409
                        wrongMsg.append("excel第" + (i + 1) + "行,姓名为“" + patName
                                + "”的人员二代身份证号校验位错误(值：" + idNo + "),正确值:"
                                + checkid.substring(17, 18) + "\r\n");
                        this.messageBox("excel第" + (i + 1) + "行,姓名为“" + patName
                                + "”的人员二代身份证号校验位错误！（正确值：" + checkid.substring(17, 18) + "）");// modify by wanglong 20130416
                        parm.setData("IDNO", count - 1, idNo);// modify by wanglong 20130416
                        String idSexCode = StringTool.isMaleFromID(checkid);// add by wanglong
                                                                            // 20130726
                        if (!idSexCode.equals(sexCode)) {// add by wanglong 20130726
                            if (this.messageBox("警告", "excel第" + (i + 1) + "行,姓名为“" + patName
                                    + "”的人员性别与其身份证上的性别不符\n是否将性别改成和身份证上一致", 2) == 0) {
                                parm.setData("SEX_CODE", count - 1, idSexCode);// 将性别改为和身份证上的信息一致
                                wrongMsg.append("excel第" + (i + 1) + "行,姓名为“" + patName
                                        + "”的人员性别与其身份证上的性别不符\r\n");
                            } else if (!sexCode.matches("[12349]")) {// add by wanglong 20130830
                                this.messageBox("excel第" + (i + 1) + "行,姓名为“" + patName
                                        + "”的人员性别数值有误，请不要直接使用中文等非法字符作为标识，暂时将被改为和身份证一致");
                                parm.setData("SEX_CODE", count - 1, idSexCode);// 将性别改为和身份证上的信息一致
                                wrongMsg.append("excel第" + (i + 1) + "行,姓名为“" + patName
                                        + "”的人员性别使用了非法字符作为标识\r\n");
                            }
                        }
                        parm.setData("BIRTHDAY", count - 1, StringTool.getBirdayFromID(checkid));
                    }
                    // 中国人（身份证完全错误）
                    else if (!foreignerFlg.equalsIgnoreCase("Y") && checkid.equals("FALSE")) {// modify by wanglong 20130409
                        wrongMsg.append("excel第" + (i + 1) + "行,姓名为“" + patName + "”的人员身份证号错误(值："
                                + idNo + "),或者他(她)是外国人\r\n");
                        this.messageBox("excel第" + (i + 1) + "行,姓名为“" + patName
                                + "”的人员身份证号错误！或者他(她)是外国人");
                        TParm temp = new TParm();
                        temp.setData("PAT_NAME", patName);
                        temp.setData("STAFF_NO", parm.getValue("STAFF_NO", count - 1));
                        temp.setData("IDNO", idNo);
                        temp.setData("FOREIGNER_FLG", foreignerFlg.toUpperCase());
                        temp.setData("SEX_CODE", sexCode);
                        temp.setData("BIRTHDAY", birthday);
                        TParm reParm = null;
                        while (reParm == null) {
                            reParm =
                                    (TParm) this.openDialog("%ROOT%\\config\\hrm\\HRMIDCheckUI.x",
                                                            temp);// 身份证填写
                        }
                        parm.setData("IDNO", count - 1, reParm.getValue("IDNO").toUpperCase());
                        parm.setData("FOREIGNER_FLG", count - 1, reParm.getData("FOREIGNER_FLG"));
                        parm.setData("SEX_CODE", count - 1, reParm.getData("SEX_CODE"));
                        parm.setData("BIRTHDAY", count - 1, reParm.getData("BIRTHDAY"));
                    }
                    // 中国人（且身份证正确）
                    else if (!foreignerFlg.equalsIgnoreCase("Y") && isId(idNo)) {
                        parm.setData("FOREIGNER_FLG", count - 1, "N");
                        String idSexCode = StringTool.isMaleFromID(idNo);// add by wanglong 20130726
                        if (!idSexCode.equals(sexCode)) {// add by wanglong 20130726
                            if (this.messageBox("警告", "excel第" + (i + 1) + "行,姓名为“" + patName
                                    + "”的人员性别与其身份证上的性别不符\n是否将性别改成和身份证上一致", 2) == 0) {
                                parm.setData("SEX_CODE", count - 1, idSexCode);// 将性别改为和身份证上的信息一致
                                wrongMsg.append("excel第" + (i + 1) + "行,姓名为“" + patName
                                        + "”的人员性别与其身份证上的性别不符\r\n");
                            } else if (!sexCode.matches("[12349]")) {//add by wanglong 20130830
                                this.messageBox("excel第" + (i + 1) + "行,姓名为“" + patName
                                                + "”的人员性别数值有误，请不要直接使用中文等非法字符作为标识，暂时将被改为和身份证一致");
                                parm.setData("SEX_CODE", count - 1, idSexCode);// 将性别改为和身份证上的信息一致
                                wrongMsg.append("excel第" + (i + 1) + "行,姓名为“" + patName
                                        + "”的人员性别使用了非法字符作为标识\r\n");
                            }
                        }
                        parm.setData("BIRTHDAY", count - 1, StringTool.getBirdayFromID(idNo));// 将出生日期改为和身份证上的信息一致
                    }
                    // 外国人
                    else if (foreignerFlg.equalsIgnoreCase("Y")) {
                        parm.setData("FOREIGNER_FLG", count - 1, "Y");
                        if (!sexList.contains(sexCode)) {
                            wrongMsg.append("excel第" + (i + 1) + "行,姓名为“" + patName + "”的人员性别错误(值："
                                    + sexCode + ")\r\n");
                            this.messageBox("excel第" + (i + 1) + "行,姓名为“" + patName + "”的人员性别错误！");
                            TParm temp = new TParm();
                            temp.setData("PAT_NAME", patName);
                            temp.setData("STAFF_NO", parm.getValue("STAFF_NO", count - 1));
                            temp.setData("IDNO", idNo);
                            temp.setData("FOREIGNER_FLG", foreignerFlg.toUpperCase());
                            // temp.setData("SEX_CODE", sexCode);
                            temp.setData("BIRTHDAY", birthday);
                            TParm reParm = null;
                            while (reParm == null) {
                                reParm =
                                        (TParm) this
                                                .openDialog("%ROOT%\\config\\hrm\\HRMIDCheckUI.x",
                                                            temp);// 身份证填写
                            }
                            parm.setData("IDNO", count - 1, reParm.getValue("IDNO").toUpperCase());
                            parm.setData("FOREIGNER_FLG", count - 1,
                                         reParm.getData("FOREIGNER_FLG"));
                            parm.setData("SEX_CODE", count - 1, reParm.getData("SEX_CODE"));
                            parm.setData("BIRTHDAY", count - 1, reParm.getData("BIRTHDAY"));
                        }
                        birthday = parm.getValue("BIRTHDAY", count - 1);
                        if (!(parm.getData("BIRTHDAY", count - 1) instanceof Timestamp)
                                && !DateTool.checkDate(birthday, "yyyyMMdd")) {
                            wrongMsg.append("excel第" + (i + 1) + "行,姓名为“" + patName
                                    + "”的人员出生日期错误(值：" + birthday + ")\r\n");
                            this.messageBox("excel第" + (i + 1) + "行,姓名为“" + patName + "”的人员出生日期错误！");
                            TParm temp = new TParm();
                            temp.setData("PAT_NAME", patName);
                            temp.setData("STAFF_NO", parm.getValue("STAFF_NO", count - 1));
                            temp.setData("IDNO", idNo);
                            temp.setData("FOREIGNER_FLG", foreignerFlg.toUpperCase());
                            temp.setData("SEX_CODE", sexCode);
                            // temp.setData("BIRTHDAY", birthday);
                            TParm reParm = null;
                            while (reParm == null) {
                                reParm =
                                        (TParm) this
                                                .openDialog("%ROOT%\\config\\hrm\\HRMIDCheckUI.x",
                                                            temp);// 身份证填写
                            }
                            parm.setData("IDNO", count - 1, reParm.getValue("IDNO").toUpperCase());
                            parm.setData("FOREIGNER_FLG", count - 1,
                                         reParm.getData("FOREIGNER_FLG"));
                            parm.setData("SEX_CODE", count - 1, reParm.getData("SEX_CODE"));
                            parm.setData("BIRTHDAY", count - 1, reParm.getData("BIRTHDAY"));
                        }
                    }
                    if (parm.getValue("COMPANY_PAY_FLG", count - 1).equalsIgnoreCase("y")) {// 增项团体支付
                        parm.setData("COMPANY_PAY_FLG", count - 1, "Y");
                    } else if (!parm.getValue("COMPANY_PAY_FLG", count - 1).equalsIgnoreCase("Y")) {
                        parm.setData("COMPANY_PAY_FLG", count - 1, "N");
                    }
                    String preChkDate = parm.getValue("PRE_CHK_DATE", count - 1);// 预检时间
                    if (preChkDate.equals("")) {
                        wrongMsg.append("excel第" + (i + 1) + "行,“预检时间”列缺少值,将被置为空\r\n");
                        // this.messageBox("excel第" + (i + 1) + "行,“预检时间”列缺少值！将被置为空");
                        parm.setData("PRE_CHK_DATE", count - 1, "");
                    } else if (!DateTool.checkDate(preChkDate, "yyyyMMdd")) {
                        wrongMsg.append("excel第" + (i + 1) + "行,“预检时间”列的值(值：" + preChkDate
                                + ")错误,将被置为空\r\n");
                        // this.messageBox("excel第" + (i + 1) + "行,“预检时间”列的值错误！将被置为空");
                        parm.setData("PRE_CHK_DATE", count - 1, "");
                    }
                    String marriageCode = parm.getValue("MARRIAGE_CODE", count - 1);// 婚姻状态
                    if (marriageCode.equals("")) {// add by wanglong 20130502
                        wrongMsg.append("excel第" + (i + 1) + "行,“婚姻状态”列缺少值,将被置为空\r\n");
                        // this.messageBox("excel第" + (i + 1) + "行,“婚姻状态”列的值在系统中不存在！将被置为空");
                        parm.setData("MARRIAGE_CODE", count - 1, "");
                    } else if (!marriageList.contains(marriageCode)) {
                        wrongMsg.append("excel第" + (i + 1) + "行,“婚姻状态”列的值(值：" + marriageCode
                                + ")在系统中不存在,将被置为空\r\n");
                        // this.messageBox("excel第" + (i + 1) + "行,“婚姻状态”列的值在系统中不存在！将被置为空");
                        parm.setData("MARRIAGE_CODE", count - 1, "");
                    }
                    
                    String isVip = parm.getValue("IS_VIP", count - 1);// 身份
                    if ("".equals(isVip)) {// add by huangjw 20160920
                        wrongMsg.append("excel第" + (i + 1) + "行,“身份”列缺少值,将被置为空\r\n");
                        parm.setData("IS_VIP", count - 1, "");
                    } else if (!"1".equals(isVip) && !"2".equals(isVip)) {
                        wrongMsg.append("excel第" + (i + 1) + "行,“身份”列的值(值：" + isVip
                                + ")在系统中不存在,将被置为空\r\n");
                        parm.setData("IS_VIP", count - 1, "");
                    }
                    // ===================数据检查结束
                }
                String msg = wrongMsg.toString();
                if (!StringUtil.isNullString(msg)) {// modify by wanglong 20130502
                // String dir = TConfig.getSystemValue("UDD_DISBATCH_LocalPath") + "\\";
                    String fileName = "健检批量导入日志（" + file.getName() + "）" + ".txt";
                    // this.messageBox_("人员信息导入有误,错误信息请见"+dir+fileName);
                    javax.swing.filechooser.FileSystemView fsv =
                            javax.swing.filechooser.FileSystemView.getFileSystemView();
                    FileTool.setString(fsv.getHomeDirectory() + "\\" + fileName, msg);
                    // FileTool.setString(dir + fileName, msg);
                }
                parm.setCount(count);
                if (count < 1) {
                    this.messageBox_("有效数据少于一行，导入操作终止");
                    return;
                }
                // setColorTable();
            }
            catch (BiffException e) {
                this.messageBox_("excel文件操作出错");
                e.printStackTrace();
                return;
            }
            catch (IOException e) {
                this.messageBox_("打开文件出错");
                e.printStackTrace();
                return;
            }
        } else return;
        String companyCode = contractM.getItemString(conTab.getSelectedRow(), "COMPANY_CODE");
        String contractCode = contractM.getItemString(conTab.getSelectedRow(), "CONTRACT_CODE");
        String contractDesc = contractM.getItemString(conTab.getSelectedRow(), "CONTRACT_DESC");
        if (StringUtil.isNullString(companyCode) || StringUtil.isNullString(contractCode)
                || StringUtil.isNullString(contractDesc)) {
            return;
        }
        String optUser = Operator.getID();
        String optTerm = Operator.getIP();
        Timestamp optDate = SystemTool.getInstance().getDate();
        int row = contractD.rowCount() - 1;
        patLoop:
        for (int i = 0; i < parm.getCount(); i++) {
            TParm parmRow = parm.getRow(i);
            if (!contractD.getItemString(row, "MR_NO").equals("")) {// add by wanglong 20130225
                row = contractD.insertRow();
            }
            int seqNo = parmRow.getInt("SEQ_NO");

            String patName = parmRow.getValue("PAT_NAME");
            // =========wanglong add 20140924 增加病案号列，可以病案号直接带出病患信息
            String mrNo = parmRow.getValue("MR_NO");
            if (!mrNo.equals("")) {
                mrNo = PatTool.getInstance().checkMrno(mrNo);
                TParm mrParm = PatTool.getInstance().getInfoForMrno(mrNo);
                if (mrParm.getCount() == 0) {
                    Pat pat = Pat.onQueryByMrNo(mrNo);
                    if (pat != null) {// wanglong add 20150423
                        this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
                        mrNo = pat.getMrNo();
                        contractD.setItem(row, "MR_NO", mrNo);
                        contractD.setItem(row, "IDNO", pat.getIdNo().toUpperCase());
                        contractD.setItem(row, "STAFF_NO", parmRow.getValue("STAFF_NO"));
                        contractD.setItem(row, "PAT_NAME", pat.getName());
                        contractD.setItem(row, "PY1",
                                          SystemTool.getInstance().charToCode(pat.getName()));
                        contractD.setItem(row, "COMPANY_CODE", companyCode);
                        contractD.setItem(row, "CONTRACT_CODE", contractCode);
                        contractD.setItem(row, "CONTRACT_DESC", contractDesc);
                        contractD.setItem(row, "PACKAGE_CODE", parmRow.getValue("PACKAGE_CODE"));
                        contractD.setItem(row, "SEX_CODE", pat.getSexCode());
                        contractD.setItem(row, "BIRTHDAY", pat.getBirthday());
                        contractD.setItem(row, "DISCNT", parmRow.getDouble("DISCNT"));// 折扣
                        if (!pat.getCellPhone().trim().equals("")
                                && pat.getCellPhone().trim().matches("[^\u4e00-\u9fa5]*")) {// 手机号(不含中文)
                            contractD.setItem(row, "TEL", pat.getCellPhone());
                        } else if (pat.getCellPhone().trim().length() == 11
                                && pat.getCellPhone().trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                            contractD.setItem(row, "TEL", pat.getCellPhone());
                        } else {
                            contractD.setItem(row, "TEL", parmRow.getValue("TEL"));
                        }
                        contractD.setItem(row, "SEQ_NO", seqNo);
                        contractD.setItem(row, "MARRIAGE_CODE", parmRow.getValue("MARRIAGE_CODE")); // 婚姻状态
                        contractD.setItem(row, "IS_VIP", parmRow.getValue("IS_VIP")); // 身份 add by huangjw 20160920
                        contractD.setItem(row, "PAT_DEPT", parmRow.getValue("PAT_DEPT")); // 部门
                        if (parmRow.getValue("PRE_CHK_DATE").equals("")) {
                            contractD.setItem(row, "PRE_CHK_DATE", optDate);
                        } else {
                            Timestamp preChkDate =
                                    StringTool.getTimestamp(parmRow.getValue("PRE_CHK_DATE"),
                                                            "yyyyMMdd");
                            contractD.setItem(row, "PRE_CHK_DATE", preChkDate);
                        }
                        contractD.setItem(row, "FOREIGNER_FLG", parmRow.getValue("FOREIGNER_FLG"));
                        contractD.setItem(row, "COMPANY_PAY_FLG", parmRow.getValue("COMPANY_PAY_FLG"));
                        contractD.setItem(row, "OPT_USER", optUser);
                        contractD.setItem(row, "OPT_TERM", optTerm);
                        contractD.setItem(row, "OPT_DATE", optDate);
                        contractD.setActive(row, true);
                    } else {
                        if (patName.equals("")) {
                            this.messageBox("病案号为" + mrNo + "的人员在系统中不存在，将不会被导入");
                        } else {
//                            this.messageBox("病案号为" + mrNo + "，姓名为 " + patName + " 的人员在系统中不存在，将不会被导入");
                            parmRow.setData("MR_NO", "Y");
                            parm.addRowData(parmRow, -1);
                        }
                        continue patLoop;
                    }
                } else if (mrParm.getCount() == 1) {
                    int rowCount = contractD.rowCount();
                    Pat pat = Pat.onQueryByMrNo(mrNo);
                    for (int j = 0; j < rowCount; j++) {// wanglong add 20140606
                        if (!contractD.isActive(j)) continue;
                        if (contractD.getItemString(j, "MR_NO").equals(mrNo)) {
                            if (patName.equals("")) {
                                this.messageBox("病案号为" + mrNo + "的人员在系统中已存在，将不会被再次导入");
                            } else {
                                this.messageBox("病案号为" + mrNo + "，姓名为 " + patName
                                        + " 的人员在系统中已存在，将不会被再次导入");
                            }
                            continue patLoop;
                        }
                        if(contractD.getItemString(j, "MR_NO").equals(pat.getMrNo())){// wanglong add 20150423
                            this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
                            mrNo = pat.getMrNo();
                            this.messageBox("病案号为" + mrNo + "的人员在系统中已存在，将不会被再次导入");
                            continue patLoop;
                        }
                    }
                    if (!mrNo.equals(pat.getMrNo())) {// wanglong add 20150423
                        this.messageBox("病案号" + mrNo + " 已合并至 " + pat.getMrNo());
                        mrNo = pat.getMrNo();
                        contractD.setItem(row, "MR_NO", mrNo);
                        contractD.setItem(row, "IDNO", pat.getIdNo().toUpperCase());
                        contractD.setItem(row, "STAFF_NO", parmRow.getValue("STAFF_NO"));
                        contractD.setItem(row, "PAT_NAME", pat.getName());
                        contractD.setItem(row, "PY1",
                                          SystemTool.getInstance().charToCode(pat.getName()));
                        contractD.setItem(row, "COMPANY_CODE", companyCode);
                        contractD.setItem(row, "CONTRACT_CODE", contractCode);
                        contractD.setItem(row, "CONTRACT_DESC", contractDesc);
                        contractD.setItem(row, "PACKAGE_CODE", parmRow.getValue("PACKAGE_CODE"));
                        contractD.setItem(row, "SEX_CODE", pat.getSexCode());
                        contractD.setItem(row, "BIRTHDAY", pat.getBirthday());
                        contractD.setItem(row, "DISCNT", parmRow.getDouble("DISCNT"));// 折扣
                        if (!pat.getCellPhone().trim().equals("")
                                && pat.getCellPhone().trim().matches("[^\u4e00-\u9fa5]*")) {// 手机号(不含中文)
                            contractD.setItem(row, "TEL", pat.getCellPhone());
                        } else if (pat.getCellPhone().trim().length() == 11
                                && pat.getCellPhone().trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                            contractD.setItem(row, "TEL", pat.getCellPhone());
                        } else {
                            contractD.setItem(row, "TEL", parmRow.getValue("TEL"));
                        }
                        contractD.setItem(row, "SEQ_NO", seqNo);
                        contractD.setItem(row, "MARRIAGE_CODE", parmRow.getValue("MARRIAGE_CODE")); // 婚姻状态
                        contractD.setItem(row, "IS_VIP", parmRow.getValue("IS_VIP")); // 身份 add by huangjw 20160920
                        contractD.setItem(row, "PAT_DEPT", parmRow.getValue("PAT_DEPT")); // 部门
                        if (parmRow.getValue("PRE_CHK_DATE").equals("")) {
                            contractD.setItem(row, "PRE_CHK_DATE", optDate);
                        } else {
                            Timestamp preChkDate =
                                    StringTool.getTimestamp(parmRow.getValue("PRE_CHK_DATE"),
                                                            "yyyyMMdd");
                            contractD.setItem(row, "PRE_CHK_DATE", preChkDate);
                        }
                        contractD.setItem(row, "FOREIGNER_FLG", parmRow.getValue("FOREIGNER_FLG"));
                        contractD.setItem(row, "COMPANY_PAY_FLG",
                                          parmRow.getValue("COMPANY_PAY_FLG"));
                        contractD.setItem(row, "OPT_USER", optUser);
                        contractD.setItem(row, "OPT_TERM", optTerm);
                        contractD.setItem(row, "OPT_DATE", optDate);
                        contractD.setActive(row, true);
                    } else {
                        contractD.setItem(row, "MR_NO", mrParm.getValue("MR_NO", 0));
                        contractD.setItem(row, "IDNO", mrParm.getValue("IDNO", 0).toUpperCase());
                        contractD.setItem(row, "STAFF_NO", parmRow.getValue("STAFF_NO"));
                        contractD.setItem(row, "PAT_NAME", mrParm.getValue("PAT_NAME", 0));
                        contractD.setItem(row,
                                          "PY1",
                                          SystemTool.getInstance()
                                                  .charToCode(mrParm.getValue("PAT_NAME", 0)));
                        contractD.setItem(row, "COMPANY_CODE", companyCode);
                        contractD.setItem(row, "CONTRACT_CODE", contractCode);
                        contractD.setItem(row, "CONTRACT_DESC", contractDesc);
                        contractD.setItem(row, "PACKAGE_CODE", parmRow.getValue("PACKAGE_CODE"));
                        contractD.setItem(row, "SEX_CODE", mrParm.getValue("SEX_CODE", 0));
                        contractD.setItem(row, "BIRTHDAY", mrParm.getTimestamp("BIRTH_DATE", 0));
                        contractD.setItem(row, "DISCNT", parmRow.getDouble("DISCNT"));// 折扣
                        if (!mrParm.getValue("CELL_PHONE", 0).trim().equals("")
                                && mrParm.getValue("CELL_PHONE", 0).trim()
                                        .matches("[^\u4e00-\u9fa5]*")) {// 手机号(不含中文)
                            contractD.setItem(row, "TEL", mrParm.getValue("CELL_PHONE", 0));
                        } else if (mrParm.getValue("TEL_HOME", 0).trim().length() == 11
                                && mrParm.getValue("TEL_HOME", 0).trim()
                                        .matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                            contractD.setItem(row, "TEL", mrParm.getValue("TEL_HOME", 0));
                        } else {
                            contractD.setItem(row, "TEL", parmRow.getValue("TEL"));
                        }
                        contractD.setItem(row, "SEQ_NO", seqNo);
                        contractD.setItem(row, "MARRIAGE_CODE", parmRow.getValue("MARRIAGE_CODE")); // 婚姻状态
                        contractD.setItem(row, "IS_VIP", parmRow.getValue("IS_VIP")); //身份 add by huangjw 20160920
                        contractD.setItem(row, "PAT_DEPT", parmRow.getValue("PAT_DEPT")); // 部门
                        if (parmRow.getValue("PRE_CHK_DATE").equals("")) {
                            contractD.setItem(row, "PRE_CHK_DATE", optDate);
                        } else {
                            Timestamp preChkDate =
                                    StringTool.getTimestamp(parmRow.getValue("PRE_CHK_DATE"),
                                                            "yyyyMMdd");
                            contractD.setItem(row, "PRE_CHK_DATE", preChkDate);
                        }
                        contractD.setItem(row, "FOREIGNER_FLG", parmRow.getValue("FOREIGNER_FLG"));
                        contractD.setItem(row, "COMPANY_PAY_FLG",
                                          parmRow.getValue("COMPANY_PAY_FLG"));
                        contractD.setItem(row, "OPT_USER", optUser);
                        contractD.setItem(row, "OPT_TERM", optTerm);
                        contractD.setItem(row, "OPT_DATE", optDate);
                        contractD.setActive(row, true);
                    }
                    continue;
                }
            }
            //=========add end
            String idNo = parmRow.getValue("IDNO").toUpperCase();
            String patSql =
                    "SELECT SYS_PATINFO.*, OPT_DATE AS REPORT_DATE "
                            + "  FROM SYS_PATINFO "
                            + " WHERE UPPER(IDNO) = '#' AND MERGE_FLG <> 'Y' "
                            + " UNION "
                            + "SELECT SYS_PATINFO.*, OPT_DATE AS REPORT_DATE "
                            + "  FROM SYS_PATINFO "
                            + " WHERE MR_NO IN (SELECT MERGE_TOMRNO FROM SYS_PATINFO "
                            + "                  WHERE UPPER(IDNO) = '#' "
                            + "                    AND MERGE_FLG = 'Y' AND MERGE_TOMRNO IS NOT NULL) "
                            + "ORDER BY REPORT_DATE DESC";// 根据身份证号查病案号
            patSql = patSql.replaceAll("#", idNo);
            TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));
            if (patParm.getErrCode() != 0) {
                // System.out.println("=======================patParm============" + patParm);
                this.messageBox("查询人员病案号时出错");
                return;
            }
            // 第一次就诊
            if (patParm.getCount("MR_NO") <= 0) {
                String birthDay = "";
                if (parmRow.getData("BIRTHDAY") instanceof Timestamp) {
                    birthDay = StringTool.getString(parmRow.getTimestamp("BIRTHDAY"), "yyyyMMdd");
                } else {
                    birthDay = parmRow.getValue("BIRTHDAY");
                }
                String samePatSql =
                        "SELECT MR_NO,OPT_DATE AS REPORT_DATE,PAT_NAME,UPPER(IDNO) IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS,TEL_COMPANY,TEL_HOME,CELL_PHONE "
                                + " FROM SYS_PATINFO WHERE PAT_NAME = '#' # "
                                + "ORDER BY OPT_DATE DESC NULLS LAST";// 查同名同龄
                samePatSql = samePatSql.replaceFirst("#", patName);
                if (birthDay.equals("")) {
                    contractD.setItem(row, "MR_NO", "Y");
                } else {
                    samePatSql =
                            samePatSql.replaceFirst("#",
                                                    " AND TO_CHAR( BIRTH_DATE, 'yyyymmdd') = '"
                                                            + birthDay + "' ");
                    TParm result = new TParm(TJDODBTool.getInstance().select(samePatSql));
                    if (result.getErrCode() != 0) {
                        this.messageBox("查询人员历次就诊信息时出错");
                        return;
                    }
                    if (result.getCount() < 1) {
                        contractD.setItem(row, "MR_NO", "Y");
                    } else if (result.getCount() == 1) {// add by wanglong 20130409
                        if (this.messageBox("病案号合并", "系统中存在和人员“" + patName + "”(序号" + seqNo
                                + ")同名同年龄的病患记录，是否查看是不是他（她）？\n", 2) == 0) {// add by wanglong 20130726
                            Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
                            if (obj != null) {
                                TParm samePatParm = (TParm) obj;
                                int rowCount = contractD.rowCount();
                                for (int j = 0; j < rowCount; j++) {// wanglong add 20140606
                                    if (!contractD.isActive(j)) continue;
                                    if (contractD.getItemString(j, "MR_NO")
                                            .equals(samePatParm.getValue("MR_NO"))) {
                                        if (patName.equals("")) {
                                            this.messageBox("病案号为" + samePatParm.getValue("MR_NO")
                                                    + "的人员在系统中已存在，将不会被再次导入");
                                        } else {
                                            this.messageBox("病案号为" + samePatParm.getValue("MR_NO")
                                                    + "，姓名为 " + samePatParm.getValue("PAT_NAME")
                                                    + " 的人员在系统中已存在，将不会被再次导入");
                                        }
                                        continue patLoop;
                                    }
                                }
                                contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                                if (!isId(idNo)) {// 20130409
                                    String patIdNo = samePatParm.getValue("IDNO");
                                    if (patIdNo.length() == 18 && isId(patIdNo)) {
                                        idNo = patIdNo;
                                    }
                                }
                                String sexCode = samePatParm.getValue("SEX_CODE");// add by wanglong 21030726
                                if (!parmRow.getValue("SEX_CODE").equals(sexCode)) {// add by wanglong 21030726
                                    this.messageBox("人员“" + patName + "”(序号" + seqNo
                                            + ")从病患记录中传回的性别与excel导入的性别不同\n"
                                            + "（请检查该人员在“病患信息”中的信息与在“健检团体信息设定”中的信息是否一致");
                                }
//                                contractD.setItem(row, "MR_NO", result.getData("MR_NO", 0));
                                if (parmRow.getValue("TEL").equals("")
                                        && !samePatParm.getValue("CELL_PHONE").equals("")
                                        && samePatParm.getValue("CELL_PHONE")
                                                .matches("[^\u4e00-\u9fa5]*")) {// wanglong add
                                                                                // 20140522 自动取电话号码
                                    parmRow.setData("TEL", samePatParm.getValue("CELL_PHONE"));
                                } else if (parmRow.getValue("TEL").equals("")
                                        && samePatParm.getValue("TEL_HOME").trim().length() == 11
                                        && samePatParm.getValue("TEL_HOME").trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                                    parmRow.setData("TEL", samePatParm.getValue("TEL_HOME"));
                                }
                            } else {
                                contractD.setItem(row, "MR_NO", "Y");
                            }
                        } else contractD.setItem(row, "MR_NO", "Y");
                    } else {
                        int j = 0;
                        for (; j < result.getCount(); j++) {// add by wanglong 20130409
                            String patIdNo = result.getValue("IDNO", j).trim();
                            if (isId(patIdNo) && uptoeighteen(patIdNo).equals(idNo)) {
                                contractD.setItem(row, "MR_NO", result.getData("MR_NO", j));
                                if (parmRow.getValue("TEL").equals("")
                                        && !result.getValue("CELL_PHONE", j).equals("")
                                        && result.getValue("CELL_PHONE", j)
                                                .matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 自动取电话号码
                                    parmRow.setData("TEL", result.getValue("CELL_PHONE",j));
                                } else if (parmRow.getValue("TEL").equals("")
                                        && result.getValue("TEL_HOME", j).trim().length() == 11
                                        && result.getValue("TEL_HOME", j).trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                                    parmRow.setData("TEL", result.getValue("TEL_HOME", j));
                                }
                                break;
                            }
                        }
                        if (j == result.getCount()) {
                            if (this.messageBox("病案号合并", "系统中存在和人员“" + patName + "”(序号" + seqNo
                                    + ")同名同年龄的病患记录，是否查看是不是他（她）？\n", 2) == 0) {
                                Object obj =
                                        openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
                                if (obj != null) {
                                    TParm samePatParm = (TParm) obj;
                                    int rowCount = contractD.rowCount();
                                    for (int k = 0; k < rowCount; k++) {// wanglong add 20140606
                                        if (!contractD.isActive(k)) continue;
                                        if (contractD.getItemString(k, "MR_NO")
                                                .equals(samePatParm.getValue("MR_NO"))) {
                                            if (patName.equals("")) {
                                                this.messageBox("病案号为"
                                                        + samePatParm.getValue("MR_NO")
                                                        + "的人员在系统中已存在，将不会被再次导入");
                                            } else {
                                                this.messageBox("病案号为"
                                                        + samePatParm.getValue("MR_NO") + "，姓名为 "
                                                        + samePatParm.getValue("PAT_NAME")
                                                        + " 的人员在系统中已存在，将不会被再次导入");
                                            }
                                            continue patLoop;
                                        }
                                    }
                                    contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                                    if (!isId(idNo)) {// 20130409
                                        String patIdNo = samePatParm.getValue("IDNO");
                                        if (patIdNo.length() == 18 && isId(patIdNo)) {
                                            idNo = patIdNo;
                                        }
                                    }
                                    String sexCode = samePatParm.getValue("SEX_CODE");// add by wanglong 21030726
                                    if (!parmRow.getValue("SEX_CODE").equals(sexCode)) {// add by wanglong 21030726
                                        this.messageBox("人员“" + patName + "”(序号" + seqNo
                                                + ")从病患记录中传回的性别与excel导入的性别不同\n"
                                                + "（请检查该人员在“病患信息”中的信息与在“健检团体信息设定”中的信息是否一致");
                                    }
                                    if (parmRow.getValue("TEL").equals("")
                                            && !samePatParm.getValue("CELL_PHONE").equals("")
                                            && samePatParm.getValue("CELL_PHONE")
                                                    .matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 自动取电话号码
                                        parmRow.setData("TEL", samePatParm.getValue("CELL_PHONE"));
                                    } else if (parmRow.getValue("TEL").equals("")
                                            && samePatParm.getValue("TEL_HOME").trim().length() == 11
                                            && samePatParm.getValue("TEL_HOME").trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                                        parmRow.setData("TEL", samePatParm.getValue("TEL_HOME"));
                                    }
                                } else {
                                    contractD.setItem(row, "MR_NO", "Y");
                                }
                            } else contractD.setItem(row, "MR_NO", "Y");
                        }
                    }
                }
            }
            // 存在就诊信息
            else if (patParm.getCount("MR_NO") == 1) {
                if (!patParm.getValue("PAT_NAME", 0).equals(patName)) {
                    if (this.messageBox("病案号合并", "系统中存在和人员“" + patName + "”(序号" + seqNo
                            + ")身份证相同但姓名有所不同的病患记录，是否查看是不是他（她）？\n", 2) == 0) {
                        Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", patParm);
                        if (obj != null) {
                            TParm samePatParm = (TParm) obj;
                            int rowCount = contractD.rowCount();
                            for (int j = 0; j < rowCount; j++) {// wanglong add 20140606
                                if (!contractD.isActive(j)) continue;
                                if (contractD.getItemString(j, "MR_NO")
                                        .equals(samePatParm.getValue("MR_NO"))) {
                                    if (patName.equals("")) {
                                        this.messageBox("病案号为" + samePatParm.getValue("MR_NO")
                                                + "的人员在系统中已存在，将不会被再次导入");
                                    } else {
                                        this.messageBox("病案号为" + samePatParm.getValue("MR_NO")
                                                + "，姓名为 " + samePatParm.getValue("PAT_NAME")
                                                + " 的人员在系统中已存在，将不会被再次导入");
                                    }
                                    continue patLoop;
                                }
                            }
                            contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                            patName = samePatParm.getValue("PAT_NAME");
                            String sexCode = samePatParm.getValue("SEX_CODE");// add by wanglong 21030726
                            if (!parmRow.getValue("SEX_CODE").equals(sexCode)) {// add by wanglong 21030726
                                this.messageBox("人员“" + patName + "”(序号" + seqNo
                                        + ")从病患记录中传回的性别与excel导入的性别不同\n"
                                        + "（请检查该人员在“病患信息”中的信息与在“健检团体信息设定”中的信息是否一致");
                            }
                            if (parmRow.getValue("TEL").equals("")
                                    && !samePatParm.getValue("CELL_PHONE").equals("")
                                    && samePatParm.getValue("CELL_PHONE")
                                            .matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 自动取电话号码
                                parmRow.setData("TEL", samePatParm.getValue("CELL_PHONE"));
                            } else if (parmRow.getValue("TEL").equals("")
                                    && samePatParm.getValue("TEL_HOME").trim().length() == 11
                                    && samePatParm.getValue("TEL_HOME").trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                                parmRow.setData("TEL", samePatParm.getValue("TEL_HOME"));
                            }
                        } else {
                            contractD.setItem(row, "MR_NO", "Y");
                        }
                    } else contractD.setItem(row, "MR_NO", "Y");
                } else {
                    int rowCount = contractD.rowCount();
                    for (int j = 0; j < rowCount; j++) {// wanglong add 20140606
                        if (!contractD.isActive(j)) continue;
                        if (contractD.getItemString(j, "MR_NO")
                                .equals(patParm.getValue("MR_NO", 0))) {
                            if (patName.equals("")) {
                                this.messageBox("病案号为" + patParm.getValue("MR_NO", 0)
                                        + "的人员在系统中已存在，将不会被再次导入");
                            } else {
                                this.messageBox("病案号为" + patParm.getValue("MR_NO", 0) + "，姓名为 "
                                        + patParm.getValue("PAT_NAME", 0) + " 的人员在系统中已存在，将不会被再次导入");
                            }
                            continue patLoop;
                        }
                    }
                    contractD.setItem(row, "MR_NO", patParm.getData("MR_NO", 0));
                    if (parmRow.getValue("TEL").equals("")
                            && !patParm.getValue("CELL_PHONE", 0).equals("")
                            && patParm.getValue("CELL_PHONE", 0).matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 自动取电话号码
                        parmRow.setData("TEL", patParm.getValue("CELL_PHONE", 0));
                    } else if (parmRow.getValue("TEL").equals("")
                            && patParm.getValue("TEL_HOME", 0).trim().length() == 11
                            && patParm.getValue("TEL_HOME", 0).trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                        parmRow.setData("TEL", patParm.getValue("TEL_HOME", 0));
                    }
                }
            }
            // 存在多条就诊信息
            else if (patParm.getCount("MR_NO") > 1) {
                int j = 0;
                for (; j < patParm.getCount("MR_NO"); j++) {
                    if (patParm.getValue("PAT_NAME", j).equals(patName)) {
                        int rowCount = contractD.rowCount();
                        for (int k = 0; k < rowCount; k++) {// wanglong add 20140606
                            if (!contractD.isActive(k)) continue;
                            if (contractD.getItemString(k, "MR_NO")
                                    .equals(patParm.getValue("MR_NO", j))) {
                                if (patName.equals("")) {
                                    this.messageBox("病案号为" + patParm.getValue("MR_NO", j)
                                            + "的人员在系统中已存在，将不会被再次导入");
                                } else {
                                    this.messageBox("病案号为" + patParm.getValue("MR_NO", j) + "，姓名为 "
                                            + patParm.getValue("PAT_NAME", j)
                                            + " 的人员在系统中已存在，将不会被再次导入");
                                }
                                continue patLoop;
                            }
                        }
                        contractD.setItem(row, "MR_NO", patParm.getData("MR_NO", j));
                        if (parmRow.getValue("TEL").equals("")
                                && !patParm.getValue("CELL_PHONE", j).equals("")
                                && patParm.getValue("CELL_PHONE", j).matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 自动取电话号码
                            parmRow.setData("TEL", patParm.getValue("CELL_PHONE", j));
                        } else if (parmRow.getValue("TEL").equals("")
                                && patParm.getValue("TEL_HOME", j).trim().length() == 11
                                && patParm.getValue("TEL_HOME", j).trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                            parmRow.setData("TEL", patParm.getValue("TEL_HOME", j));
                        }
                        break;
                    }
                }
                if (j == patParm.getCount("MR_NO")) {
                    if (this.messageBox("病案号合并", "系统中存在和人员“" + patName + "”(序号" + seqNo
                            + ")身份证相同但姓名有所不同的病患记录，是否查看是不是他（她）？\n", 2) == 0) {
                        Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", patParm);
                        if (obj != null) {
                            TParm samePatParm = (TParm) obj;
                            int rowCount = contractD.rowCount();
                            for (int k = 0; k < rowCount; k++) {// wanglong add 20140606
                                if (!contractD.isActive(k)) continue;
                                if (contractD.getItemString(k, "MR_NO")
                                        .equals(samePatParm.getValue("MR_NO"))) {
                                    if (patName.equals("")) {
                                        this.messageBox("病案号为" + samePatParm.getValue("MR_NO")
                                                + "的人员在系统中已存在，将不会被再次导入");
                                    } else {
                                        this.messageBox("病案号为" + samePatParm.getValue("MR_NO")
                                                + "，姓名为 " + samePatParm.getValue("PAT_NAME")
                                                + " 的人员在系统中已存在，将不会被再次导入");
                                    }
                                    continue patLoop;
                                }
                            }
                            contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                            patName = samePatParm.getValue("PAT_NAME");
                            String sexCode = samePatParm.getValue("SEX_CODE");// add by wanglong 21030726
                            if (!parmRow.getValue("SEX_CODE").equals(sexCode)) {// add by wanglong 21030726
                                this.messageBox("人员“" + patName + "”(序号" + seqNo
                                        + ")从病患记录中传回的性别与excel导入的性别不同\n"
                                        + "（请检查该人员在“病患信息”中的信息与在“健检团体信息设定”中的信息是否一致");
                            }
                            if (parmRow.getValue("TEL").equals("")
                                    && !samePatParm.getValue("CELL_PHONE").equals("")
                                    && samePatParm.getValue("CELL_PHONE")
                                            .matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 自动取电话号码
                                parmRow.setData("TEL", samePatParm.getValue("CELL_PHONE"));
                            } else if (parmRow.getValue("TEL").equals("")
                                    && samePatParm.getValue("TEL_HOME").trim().length() == 11
                                    && samePatParm.getValue("TEL_HOME").trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                                parmRow.setData("TEL", samePatParm.getValue("TEL_HOME"));
                            }
                        } else {
                            contractD.setItem(row, "MR_NO", "Y");
                        }
                    } else contractD.setItem(row, "MR_NO", "Y");
                }
            }
            contractD.setItem(row, "IDNO", idNo.toUpperCase());
            contractD.setItem(row, "STAFF_NO", parmRow.getValue("STAFF_NO"));
            contractD.setItem(row, "PAT_NAME", patName);
            contractD.setItem(row, "PY1", SystemTool.getInstance().charToCode(patName));
            contractD.setItem(row, "COMPANY_CODE", companyCode);
            contractD.setItem(row, "CONTRACT_CODE", contractCode);
            contractD.setItem(row, "CONTRACT_DESC", contractDesc);
            contractD.setItem(row, "PACKAGE_CODE", parmRow.getValue("PACKAGE_CODE"));
            contractD.setItem(row, "SEX_CODE", parmRow.getValue("SEX_CODE"));
            Timestamp birthday = SystemTool.getInstance().getDate();
            if (!(parmRow.getData("BIRTHDAY") instanceof Timestamp)) {
                birthday = StringTool.getTimestamp(parmRow.getValue("BIRTHDAY"), "yyyyMMdd");
            } else birthday = parmRow.getTimestamp("BIRTHDAY");
            contractD.setItem(row, "BIRTHDAY", birthday);
            // 折扣 modify by wanglong 20130116
            contractD.setItem(row, "DISCNT", parmRow.getDouble("DISCNT"));
            if (parmRow.getValue("TEL").equals("")
                    && !contractD.getItemString(row, "MR_NO").equals("Y")) {// wanglong add 20140512 自动取电话号码
                if (patParm.getCount("MR_NO") > 0) {
                    if (!patParm.getValue("CELL_PHONE", 0).trim().equals("")
                            && patParm.getValue("CELL_PHONE", 0).trim()
                                    .matches("[^\u4e00-\u9fa5]*")) {// 手机号(不含中文)
                        contractD.setItem(row, "TEL", patParm.getValue("CELL_PHONE", 0));
                    } else if (patParm.getValue("TEL_HOME", 0).trim().length() == 11
                            && patParm.getValue("TEL_HOME", 0).trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                        contractD.setItem(row, "TEL", patParm.getValue("TEL_HOME", 0));
                    }
                }
            } else {
                contractD.setItem(row, "TEL", parmRow.getValue("TEL"));
            }
            contractD.setItem(row, "SEQ_NO", seqNo);
            // 婚姻状态 add by wanglong 20130116
            contractD.setItem(row, "MARRIAGE_CODE", parmRow.getValue("MARRIAGE_CODE"));
            contractD.setItem(row, "IS_VIP", parmRow.getValue("IS_VIP")); //身份 add by huangjw 20160920
            // 部门 add by wanglong 20130225
            contractD.setItem(row, "PAT_DEPT", parmRow.getValue("PAT_DEPT"));
            if (parmRow.getValue("PRE_CHK_DATE").equals("")) {
                contractD.setItem(row, "PRE_CHK_DATE", optDate);
            } else {
                Timestamp preChkDate =
                        StringTool.getTimestamp(parmRow.getValue("PRE_CHK_DATE"), "yyyyMMdd");
                contractD.setItem(row, "PRE_CHK_DATE", preChkDate);
            }
            contractD.setItem(row, "FOREIGNER_FLG", parmRow.getValue("FOREIGNER_FLG"));
            contractD.setItem(row, "COMPANY_PAY_FLG", parmRow.getValue("COMPANY_PAY_FLG"));
            contractD.setItem(row, "OPT_USER", optUser);
            contractD.setItem(row, "OPT_TERM", optTerm);
            contractD.setItem(row, "OPT_DATE", optDate);
            contractD.setActive(row, true);
        }
        if (!contractD.getItemString(row, "PAT_NAME").equals("")) {
            int newRow = contractD.insertRow();
            contractD.setItem(newRow, "COMPANY_CODE", companyCode);
            contractD.setItem(newRow, "CONTRACT_CODE", contractCode);
            contractD.setItem(newRow, "CONTRACT_DESC", contractDesc);
            contractD.setItem(newRow, "PRE_CHK_DATE", optDate);
            if (!StringUtil.isNullString(contractD.packageCode)) {
                contractD.setItem(newRow, "PACKAGE_CODE", contractD.packageCode);
            } else contractD.setItem(newRow, "PACKAGE_CODE", "");
            // contractD.setItem(row, "MR_NO", "Y");
            contractD.setItem(newRow, "OPT_USER", optUser);
            contractD.setItem(newRow, "OPT_TERM", optTerm);
            contractD.setItem(newRow, "DISCNT", discnt);
            contractD.setItem(newRow, "OPT_DATE", optDate);
            contractD.setItem(newRow, "SEQ_NO",
                              contractD.getItemInt(contractD.rowCount() - 2, "SEQ_NO") + 1);
            contractD.setActive(newRow, false);
        }
        double amt = contractD.getContractAmt();
        // this.messageBox_(amt);
        contractM.setItem(conTab.getSelectedRow(), "SUBTOTAL", amt);
        contractM.setItem(conTab.getSelectedRow(), "TOT_AMT", amt);
        mrTab.setDSValue();
        this.messageBox_("导入成功");
    }

    public static void main(String[] args) {
        String id = "340104650406354";
        // System.out.println(""+StringTool.getBirdayFromID(id));
    }

    /**
     * 设置TABLE颜色
     */
    public void setColorTable() {
        /**
         * 红色
         */
        Color antibioticColor = new Color(255, 0, 0);
        /**
         * 普通颜色
         */
        Color normalColor = new Color(0, 0, 0);
        int count = mrTab.getRowCount();
        TParm tableParm = contractD.getBuffer(contractD.PRIMARY);
        // System.out.println("行数"+count);
        for (int i = 0; i < count; i++) {
            TParm temp = tableParm.getRow(i);
            if ("N".equals(temp.getValue("MR_NO"))) {
                mrTab.setRowTextColor(i, antibioticColor);
            } else {
                mrTab.setRowTextColor(i, normalColor);
            }
        }
    }

    /**
     * 右击MENU弹出事件
     * 
     * @param tableName
     */
    public void showPopMenu() {
        TParm action = mrTab.getDataStore().getRowParm(mrTab.getSelectedRow());
        if ("N".equals(action.getValue("MR_NO"))) {
            mrTab.setPopupMenuSyntax("选择病案号,openRigthPopMenu");
            return;
        } else {
            mrTab.setPopupMenuSyntax("");
            return;
        }
    }

    /**
     * 右键查询
     */
    public void openRigthPopMenu() {
        TParm action = mrTab.getDataStore().getRowParm(mrTab.getSelectedRow());
        TParm reParm =
                (TParm) this.openDialog("%ROOT%\\config\\hrm\\HRMQueryMrNoUI.x",
                                        action.getValue("IDNO"));
        if (reParm == null) {
            mrTab.getDataStore().setItem(mrTab.getSelectedRow(), "MR_NO", "Y");
            mrTab.setDSValue();
        } else {
            mrTab.getDataStore().setItem(mrTab.getSelectedRow(), "MR_NO", reParm.getData("MR_NO"));
            mrTab.setDSValue();
        }
        setColorTable();
    }

    /**
     * 套餐改变事件,将选中的套餐代码带入合同细相信息
     */
    public void onPackage() {
        String packageCode = this.getValueString("PACKAGE_CODE");
        if (StringUtil.isNullString(packageCode)) {
            return;
        }
        contractD.packageCode = packageCode;
        int count = contractD.rowCount();
        String contractCode = contractD.getItemString(count - 1, "CONTRACT_CODE");
    
        String mrSql =
                "SELECT DISTINCT MR_NO FROM HRM_PATADM WHERE CONTRACT_CODE = '" + contractCode
                        + "' AND (#)";// add by wanglong 201303014
        mrSql = mrSql.replaceFirst("#", getInStatement("MR_NO", contractD));
        TParm patAdmParm = new TParm(TJDODBTool.getInstance().select(mrSql));
        if (patAdmParm.getErrCode() != 0) {
            this.messageBox("查询病患医嘱展开信息出错");
            return;
        }
        ArrayList<String> patAdmList = new ArrayList<String>();
        for (int i = 0; i < patAdmParm.getCount(); i++) {
            patAdmList.add(patAdmParm.getValue("MR_NO", i));
        }
        boolean flag = false;
        for (int i = 0; i < count; i++) {
            // add by wanglong 20130314
            if (TypeTool.getBoolean(contractD.getItemData(i, "COVER_FLG"))) {
//                this.messageBox(contractD.getItemString(i, "PAT_NAME") + "已报到，不能更改套餐");
                flag = true;
                continue;
            }
            String mrNo = contractD.getItemString(i, "MR_NO");
            if (patAdmList.contains(mrNo)) {
//                this.messageBox(contractD.getItemString(i, "PAT_NAME") + "医嘱已展开，不能更改套餐");
                flag = true;
                continue;
            }
            contractD.setItem(i, "PACKAGE_CODE", packageCode);
        }
        if (flag == true) {
            this.messageBox("已报到的人员，以及医嘱已展开的人员，不能更改套餐");
        }
        double amt = contractD.getContractAmt();
        // this.messageBox_(amt);
        contractM.setItem(conDescRow, "SUBTOTAL", amt);
        contractM.setItem(conDescRow, "TOT_AMT", amt);
        conTab.setDSValue();
        mrTab.setDSValue();
    }

    /**
     * 根据病案号查询合同细相
     */
    public void onMrNo() {}

    /**
     * 根据邮编查找地址
     */
    public void onPostCheck() {
        String sql =
                "SELECT STATE||CITY ADDRESS FROM SYS_POSTCODE WHERE POST_CODE='"
                        + this.getValueString("POST_CODE") + "'";
        // System.out.println("sql==="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        String add = result.getValue("ADDRESS", 0);
        this.setValue("ADDRESS", add);
    }

    /**
     * 检查邮件地址是否有效
     */
    public void onCheckEmail() {
        String email = this.getValueString("E_MAIL");
        if (!StringTool.isEmail(email)) {
            this.messageBox_("地址无效");
            this.callFunction("UI|E_MAIL|grabFocus");
            return;
        }
        this.callFunction("UI|CONTACTS_NAME|grabFocus");
    }

    /**
     * 查询事件
     */
    public void onQuery() {
        String companyDesc = this.getValueString("COMPANY_DESC");
        if (StringUtil.isNullString(companyDesc)) {
            this.messageBox_("团体信息为空");
            return;
        }
        // caowl 20130308 start 修改模糊查询时只查询出一条信息的问题
        TParm companyParm = company.queryCodeByName(companyDesc);
        String str = "COMPANY_CODE =";
        if (companyParm.getCount() <= 0) {
            this.messageBox_("查无信息");
            return;
        }
        for (int i = 0; i < companyParm.getCount(); i++) {
            if (i == 0) {
                str += "'" + companyParm.getValue("COMPANY_CODE", i) + "' ";
            } else {
                str += "OR COMPANY_CODE = '" + companyParm.getValue("COMPANY_CODE", i) + "'";
            }
        }
        company.setFilter(str);
        // caowl 20130308 end
        company.filter();
        comTab.setDSValue();
    }

    /**
     * 西药，管制药品，处置的checkBox事件
     * 
     * @param obj
     * @return
     */
    public boolean onCheckBox(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        table.setDSValue();
        return false;
    }

    /**
     * 汇出Excel add by wanglong 20130117
     */
    public void onExcel() {
        TTabbedPane tabbedPane = (TTabbedPane) this.getComponent("TAB_PANEL");
        if (tabbedPane.getSelectedIndex() == 0) {
            this.messageBox("请在合同详细页签下使用");
            return;
        }
        if (mrTab.getRowCount() <= 0) {
            this.messageBox("没有数据");
            return;
        } else {
            int rowCount = contractD.rowCount();
            if (rowCount <= 0) {
                this.messageBox("没有数据");
                return;
            }
            boolean flag = false;
            for (int i = 0; i < rowCount; i++) {
                if (!contractD.isActive(i)) continue;
                else {
                    flag = true;
                }
            }
            if (flag == false) {
                this.messageBox("没有数据");
                return;
            }
        }
        TParm result = mrTab.getShowParmValue();
        result.addData("SYSTEM", "COLUMNS", "COVER_FLG");
        result.addData("SYSTEM", "COLUMNS", "CONTRACT_DESC");
        result.addData("SYSTEM", "COLUMNS", "FOREIGNER_FLG");
        result.addData("SYSTEM", "COLUMNS", "IDNO");
        result.addData("SYSTEM", "COLUMNS", "SEX_CODE");
        result.addData("SYSTEM", "COLUMNS", "BIRTHDAY");
        result.addData("SYSTEM", "COLUMNS", "PACKAGE_CODE");
        result.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        result.addData("SYSTEM", "COLUMNS", "MARRIAGE_CODE");
        result.addData("SYSTEM", "COLUMNS", "SEQ_NO");
        result.addData("SYSTEM", "COLUMNS", "PAT_DEPT");
        result.addData("SYSTEM", "COLUMNS", "STAFF_NO");
        result.addData("SYSTEM", "COLUMNS", "MR_NO");
        result.addData("SYSTEM", "COLUMNS", "TEL");
        result.addData("SYSTEM", "COLUMNS", "PRE_CHK_DATE");
        result.addData("SYSTEM", "COLUMNS", "REAL_CHK_DATE");
        result.addData("SYSTEM", "COLUMNS", "COMPANY_PAY_FLG");
        result.addData("SYSTEM", "COLUMNS", "DISCNT");
        result.addData("SYSTEM", "COLUMNS", "BILL_FLG");
        result.addData("SYSTEM", "COLUMNS", "BILL_NO");
        result.addData("SYSTEM", "COLUMNS", "RECEIPT_NO");
        result.setData("TITLE", comTab.getItemString(comTab.getSelectedRow(), "COMPANY_DESC") + "_"
                + mrTab.getItemString(0, "CONTRACT_DESC"));
        result.setData("HEAD",
                       "到检,60;合同名称,130;其他证件,60;身份证号,190;性别,65;出生日期,115;套餐名称,200;员工名称,110;婚姻状态,110;序号,60;部门,100;"
                               + "工号,100;病案号,130;电话,120;预检时间,110;到检时间,110;增项团体支付,110;折扣率,90;结算状态,110;账单号,130;收据号,130");
        TParm[] execleTable = new TParm[]{result };
        ExportExcelUtil.getInstance().exeSaveExcel(execleTable, "健检团体合同信息设定");
        // ExportExcelUtil.getInstance().exportExcel(mrTab,"健检团体信息");
    }

    /**
     * 返回去掉空行的记录数
     * @param sheet
     * @return
     */
    private int getRightRows(Sheet sheet) {
        int rsCols = sheet.getColumns(); // 列数
        int rsRows = sheet.getRows(); // 行数
        int nullCellNum;
        int afterRows = rsRows;
        for (int i = 1; i < rsRows; i++) { // 统计行中为空的单元格数
            nullCellNum = 0;
            for (int j = 0; j < rsCols; j++) {
                String val = sheet.getCell(j, i).getContents();
                val = StringUtils.trimToEmpty(val);
                if (StringUtils.isBlank(val)) nullCellNum++;
            }
            if (nullCellNum >= rsCols) { // 如果nullCellNum大于或等于总的列数
                afterRows--; // 行数减一
            }
        }
        return afterRows;
    }
    
    /**
     * 校验身份证（T40里的isID方法没有校验身份证中间的出生日期是否正确）
     * 
     * @param idcard
     * @return
     */
    public boolean isId(String idcard) {// add by wanglong 20130417
        if ((idcard == null) || (idcard.length() == 0)) {
            return false;
        }
        if (idcard.length() == 15) {
            idcard = uptoeighteen(idcard);
        }
        if (idcard.length() != 18) {
            return false;
        }
        String birthday = idcard.substring(6, 14);
        String regexString =
                "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})"
                        + "(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))"
                        + "|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)";
        if (birthday.matches(regexString)) {
            if (StringTool.isId(idcard)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查身份证正确性。对于18位身份证，如果校验位不正确，自动纠正。
     * 
     * @param idcard
     * @return
     */
    public String checkID(String idcard) {// add by wanglong 20130409
        if (idcard.length() != 15 && idcard.length() != 18) {
            return "FALSE";
        }
        if (idcard.length() == 15) {
            if (StringTool.isId(idcard)) {
                return "TRUE";
            } else {
                return "FALSE";
            }
        }
        String date = idcard.substring(6, 14);
        String regexString =
                "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})"
                        + "(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))"
                        + "|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)";
        if (date.matches(regexString)) {
            if (!StringTool.isId(idcard)) {
                String verrifyChar = getIDVerify(idcard);
                if (verrifyChar.equals("")) {// add by wanglong 20130521
                    return "FALSE";
                }
                idcard = idcard.substring(0, 17) + verrifyChar;
                return idcard;
            } else return "TRUE";
        } else return "FALSE";
    }

    /**
     * 身份证15位升18位
     * 
     * @param fifteencardid
     * @return
     */
    public String uptoeighteen(String fifteencardid) {// add by wanglong 20130409
        String eightcardid = fifteencardid.substring(0, 6);
        eightcardid = eightcardid + "19";
        eightcardid = eightcardid + fifteencardid.substring(6, 15);
        eightcardid = eightcardid + getIDVerify(eightcardid);
        return eightcardid;
    }

    /**
     * 得到18位身份证检验位
     * 
     * @param eightcardid
     * @return
     */
    public String getIDVerify(String eightcardid) {// add by wanglong 20130409
        int[] wi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };
        int[] vi = {1, 0, 88, 9, 8, 7, 6, 5, 4, 3, 2 };
        int[] ai = new int[18];
        int remaining = 0;
        if (eightcardid.length() == 18) {
            eightcardid = eightcardid.substring(0, 17);
        }
        if (eightcardid.length() == 17) {
            int sum = 0;
            for (int i = 0; i < 17; i++) {
                String k = eightcardid.substring(i, i + 1);
                try {// add by wanglong 20130521
                    ai[i] = Integer.parseInt(k);
                }
                catch (Exception e) {
                    return "";
                }
            }
            for (int i = 0; i < 17; i++) {
                sum += wi[i] * ai[i];
            }
            remaining = sum % 11;
        }
        return remaining == 2 ? "X" : String.valueOf(vi[remaining]);
    }
    
    /**
     * 生成超过1000元素的MR_NO IN()语句
     * 
     * @param field
     * @param Nos
     * @return 返回类似“MR_NO IN (1,2,3...) OR MR_NO IN (1001,1002,1003...)”
     */
    public static String getInStatement(String field, TDataStore ds) {// add by wanglong 20130819
        if (ds.rowCount() < 1) {
            return " 1=1 ";
        }
        ArrayList<String> al=new ArrayList<String>();
        for (int i = 0; i < ds.rowCount(); i++) {
            if (!ds.getItemString(i, field).equals("")) {
                al.add("'"+ds.getItemString(i, field)+"'");
            }
        }
        StringBuffer inStr = new StringBuffer();
        inStr.append(field + " IN (");
        for (int i = 0; i < al.size(); i++) {
            inStr.append(al.get(i));
            if ((i + 1) != al.size()) {
                if ((i + 1) % 1000 != 0) {
                    inStr.append(",");
                } else if (((i + 1) % 1000 == 0)) {
                    inStr.append(") OR " + field + " IN (");
                }
            }
        }
        inStr.append(")");
        return inStr.toString();
    }
    
    /**
     * 打开受试者招募表
     * 
     * @author wangb 2016/07/01
     */
	public void onQueryRecruitInfo() {
		conTab.acceptText();
		if (conTab == null) {
			this.messageBox_("没有合同信息");
			return;
		}
		int clickRow = conTab.getSelectedRow();
		if (clickRow < 0) {
			this.messageBox_("没有合同信息");
			return;
		}
		if (conTab.getItemString(clickRow, "CONTRACT_DESC").trim().equals("")) {
			this.messageBox_("没有选择合同");
			return;
		}

		TParm parm = new TParm();
		TParm recruitInfoResult = (TParm) this.openDialog(
				"%ROOT%\\config\\hrm\\HRMRecruitInfo.x", parm);
		
		if (recruitInfoResult == null || recruitInfoResult.getCount("SEQ") < 1) {
			return;
		}
		
		this.insertIntoContractD(recruitInfoResult);
	}
	
	/**
	 * 将选择的招募信息导入合同明细
	 * 
	 * @param parm 受试者招募信息
	 */
	private void insertIntoContractD(TParm parm) {
		String discnt = conTab.getItemString(conTab.getSelectedRow(), "DISCNT");// 合同折扣率
		String companyCode = contractM.getItemString(conTab.getSelectedRow(), "COMPANY_CODE");
        String contractCode = contractM.getItemString(conTab.getSelectedRow(), "CONTRACT_CODE");
        String contractDesc = contractM.getItemString(conTab.getSelectedRow(), "CONTRACT_DESC");
        if (StringUtil.isNullString(companyCode) || StringUtil.isNullString(contractCode)
                || StringUtil.isNullString(contractDesc)) {
            return;
        }
        String optUser = Operator.getID();
        String optTerm = Operator.getIP();
        Timestamp optDate = SystemTool.getInstance().getDate();
        int row = contractD.rowCount() - 1;
        patLoop:
        for (int i = 0; i < parm.getCount(); i++) {
            TParm parmRow = parm.getRow(i);
            if (!contractD.getItemString(row, "MR_NO").equals("")) {// add by wanglong 20130225
                row = contractD.insertRow();
            }
            int seqNo = parmRow.getInt("SEQ");

            String patName = parmRow.getValue("PY1");
            String idNo = parmRow.getValue("IDNO").toUpperCase();
            String patSql =
                    "SELECT SYS_PATINFO.*, OPT_DATE AS REPORT_DATE "
                            + "  FROM SYS_PATINFO "
                            + " WHERE UPPER(IDNO) = '#' AND MERGE_FLG <> 'Y' "
                            + " UNION "
                            + "SELECT SYS_PATINFO.*, OPT_DATE AS REPORT_DATE "
                            + "  FROM SYS_PATINFO "
                            + " WHERE MR_NO IN (SELECT MERGE_TOMRNO FROM SYS_PATINFO "
                            + "                  WHERE UPPER(IDNO) = '#' "
                            + "                    AND MERGE_FLG = 'Y' AND MERGE_TOMRNO IS NOT NULL) "
                            + "ORDER BY REPORT_DATE DESC";// 根据身份证号查病案号
            patSql = patSql.replaceAll("#", idNo);
            TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));
            if (patParm.getErrCode() != 0) {
                // System.out.println("=======================patParm============" + patParm);
                this.messageBox("查询人员病案号时出错");
                return;
            }
            // 第一次就诊
            if (patParm.getCount("MR_NO") <= 0) {
                String birthDay = "";
                if (parmRow.getData("BIRTH_DATE") instanceof Timestamp) {
                    birthDay = StringTool.getString(parmRow.getTimestamp("BIRTH_DATE"), "yyyyMMdd");
                } else {
                    birthDay = parmRow.getValue("BIRTH_DATE");
                }
                String samePatSql =
                        "SELECT MR_NO,OPT_DATE AS REPORT_DATE,PAT_NAME,UPPER(IDNO) IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS,TEL_COMPANY,TEL_HOME,CELL_PHONE "
                                + " FROM SYS_PATINFO WHERE PAT_NAME = '#' # "
                                + "ORDER BY OPT_DATE DESC NULLS LAST";// 查同名同龄
                samePatSql = samePatSql.replaceFirst("#", patName);
                if (birthDay.equals("")) {
                    contractD.setItem(row, "MR_NO", "Y");
                } else {
                    samePatSql =
                            samePatSql.replaceFirst("#",
                                                    " AND TO_CHAR( BIRTH_DATE, 'yyyymmdd') = '"
                                                            + birthDay + "' ");
                    TParm result = new TParm(TJDODBTool.getInstance().select(samePatSql));
                    if (result.getErrCode() != 0) {
                        this.messageBox("查询人员历次就诊信息时出错");
                        return;
                    }
                    if (result.getCount() < 1) {
                        contractD.setItem(row, "MR_NO", "Y");
                    } else if (result.getCount() == 1) {// add by wanglong 20130409
                        if (this.messageBox("病案号合并", "系统中存在和人员“" + patName + "”(序号" + seqNo
                                + ")同名同年龄的病患记录，是否查看是不是他（她）？\n", 2) == 0) {// add by wanglong 20130726
                            Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
                            if (obj != null) {
                                TParm samePatParm = (TParm) obj;
                                int rowCount = contractD.rowCount();
                                for (int j = 0; j < rowCount; j++) {// wanglong add 20140606
                                    if (!contractD.isActive(j)) continue;
                                    if (contractD.getItemString(j, "MR_NO")
                                            .equals(samePatParm.getValue("MR_NO"))) {
                                        if (patName.equals("")) {
                                            this.messageBox("病案号为" + samePatParm.getValue("MR_NO")
                                                    + "的人员在系统中已存在，将不会被再次导入");
                                        } else {
                                            this.messageBox("病案号为" + samePatParm.getValue("MR_NO")
                                                    + "，姓名为 " + samePatParm.getValue("PAT_NAME")
                                                    + " 的人员在系统中已存在，将不会被再次导入");
                                        }
                                        continue patLoop;
                                    }
                                }
                                contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                                if (!isId(idNo)) {// 20130409
                                    String patIdNo = samePatParm.getValue("IDNO");
                                    if (patIdNo.length() == 18 && isId(patIdNo)) {
                                        idNo = patIdNo;
                                    }
                                }
                                String sexCode = samePatParm.getValue("SEX_CODE");// add by wanglong 21030726
                                if (!parmRow.getValue("SEX_CODE").equals(sexCode)) {// add by wanglong 21030726
                                    this.messageBox("人员“" + patName + "”(序号" + seqNo
                                            + ")从病患记录中传回的性别与excel导入的性别不同\n"
                                            + "（请检查该人员在“病患信息”中的信息与在“健检团体信息设定”中的信息是否一致");
                                }
//                                contractD.setItem(row, "MR_NO", result.getData("MR_NO", 0));
                                if (parmRow.getValue("CELL_PHONE").equals("")
                                        && !samePatParm.getValue("CELL_PHONE").equals("")
                                        && samePatParm.getValue("CELL_PHONE")
                                                .matches("[^\u4e00-\u9fa5]*")) {// wanglong add
                                                                                // 20140522 自动取电话号码
                                    parmRow.setData("CELL_PHONE", samePatParm.getValue("CELL_PHONE"));
                                } else if (parmRow.getValue("CELL_PHONE").equals("")
                                        && samePatParm.getValue("TEL_HOME").trim().length() == 11
                                        && samePatParm.getValue("TEL_HOME").trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                                    parmRow.setData("CELL_PHONE", samePatParm.getValue("TEL_HOME"));
                                }
                            } else {
                                contractD.setItem(row, "MR_NO", "Y");
                            }
                        } else contractD.setItem(row, "MR_NO", "Y");
                    } else {
                        int j = 0;
                        for (; j < result.getCount(); j++) {// add by wanglong 20130409
                            String patIdNo = result.getValue("IDNO", j).trim();
                            if (isId(patIdNo) && uptoeighteen(patIdNo).equals(idNo)) {
                                contractD.setItem(row, "MR_NO", result.getData("MR_NO", j));
                                if (parmRow.getValue("CELL_PHONE").equals("")
                                        && !result.getValue("CELL_PHONE", j).equals("")
                                        && result.getValue("CELL_PHONE", j)
                                                .matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 自动取电话号码
                                    parmRow.setData("CELL_PHONE", result.getValue("CELL_PHONE",j));
                                } else if (parmRow.getValue("CELL_PHONE").equals("")
                                        && result.getValue("TEL_HOME", j).trim().length() == 11
                                        && result.getValue("TEL_HOME", j).trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                                    parmRow.setData("CELL_PHONE", result.getValue("TEL_HOME", j));
                                }
                                break;
                            }
                        }
                        if (j == result.getCount()) {
                            if (this.messageBox("病案号合并", "系统中存在和人员“" + patName + "”(序号" + seqNo
                                    + ")同名同年龄的病患记录，是否查看是不是他（她）？\n", 2) == 0) {
                                Object obj =
                                        openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
                                if (obj != null) {
                                    TParm samePatParm = (TParm) obj;
                                    int rowCount = contractD.rowCount();
                                    for (int k = 0; k < rowCount; k++) {// wanglong add 20140606
                                        if (!contractD.isActive(k)) continue;
                                        if (contractD.getItemString(k, "MR_NO")
                                                .equals(samePatParm.getValue("MR_NO"))) {
                                            if (patName.equals("")) {
                                                this.messageBox("病案号为"
                                                        + samePatParm.getValue("MR_NO")
                                                        + "的人员在系统中已存在，将不会被再次导入");
                                            } else {
                                                this.messageBox("病案号为"
                                                        + samePatParm.getValue("MR_NO") + "，姓名为 "
                                                        + samePatParm.getValue("PAT_NAME")
                                                        + " 的人员在系统中已存在，将不会被再次导入");
                                            }
                                            continue patLoop;
                                        }
                                    }
                                    contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                                    if (!isId(idNo)) {// 20130409
                                        String patIdNo = samePatParm.getValue("IDNO");
                                        if (patIdNo.length() == 18 && isId(patIdNo)) {
                                            idNo = patIdNo;
                                        }
                                    }
                                    String sexCode = samePatParm.getValue("SEX_CODE");// add by wanglong 21030726
                                    if (!parmRow.getValue("SEX_CODE").equals(sexCode)) {// add by wanglong 21030726
                                        this.messageBox("人员“" + patName + "”(序号" + seqNo
                                                + ")从病患记录中传回的性别与excel导入的性别不同\n"
                                                + "（请检查该人员在“病患信息”中的信息与在“健检团体信息设定”中的信息是否一致");
                                    }
                                    if (parmRow.getValue("CELL_PHONE").equals("")
                                            && !samePatParm.getValue("CELL_PHONE").equals("")
                                            && samePatParm.getValue("CELL_PHONE")
                                                    .matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 自动取电话号码
                                        parmRow.setData("CELL_PHONE", samePatParm.getValue("CELL_PHONE"));
                                    } else if (parmRow.getValue("CELL_PHONE").equals("")
                                            && samePatParm.getValue("TEL_HOME").trim().length() == 11
                                            && samePatParm.getValue("TEL_HOME").trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                                        parmRow.setData("CELL_PHONE", samePatParm.getValue("TEL_HOME"));
                                    }
                                } else {
                                    contractD.setItem(row, "MR_NO", "Y");
                                }
                            } else contractD.setItem(row, "MR_NO", "Y");
                        }
                    }
                }
            }
            // 存在就诊信息
            else if (patParm.getCount("MR_NO") == 1) {
                if (!patParm.getValue("PAT_NAME", 0).equals(patName)) {
                    if (this.messageBox("病案号合并", "系统中存在和人员“" + patName + "”(序号" + seqNo
                            + ")身份证相同但姓名有所不同的病患记录，是否查看是不是他（她）？\n", 2) == 0) {
                        Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", patParm);
                        if (obj != null) {
                            TParm samePatParm = (TParm) obj;
                            int rowCount = contractD.rowCount();
                            for (int j = 0; j < rowCount; j++) {// wanglong add 20140606
                                if (!contractD.isActive(j)) continue;
                                if (contractD.getItemString(j, "MR_NO")
                                        .equals(samePatParm.getValue("MR_NO"))) {
                                    if (patName.equals("")) {
                                        this.messageBox("病案号为" + samePatParm.getValue("MR_NO")
                                                + "的人员在系统中已存在，将不会被再次导入");
                                    } else {
                                        this.messageBox("病案号为" + samePatParm.getValue("MR_NO")
                                                + "，姓名为 " + samePatParm.getValue("PAT_NAME")
                                                + " 的人员在系统中已存在，将不会被再次导入");
                                    }
                                    continue patLoop;
                                }
                            }
                            contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                            patName = samePatParm.getValue("PAT_NAME");
                            String sexCode = samePatParm.getValue("SEX_CODE");// add by wanglong 21030726
                            if (!parmRow.getValue("SEX_CODE").equals(sexCode)) {// add by wanglong 21030726
                                this.messageBox("人员“" + patName + "”(序号" + seqNo
                                        + ")从病患记录中传回的性别与excel导入的性别不同\n"
                                        + "（请检查该人员在“病患信息”中的信息与在“健检团体信息设定”中的信息是否一致");
                            }
                            if (parmRow.getValue("CELL_PHONE").equals("")
                                    && !samePatParm.getValue("CELL_PHONE").equals("")
                                    && samePatParm.getValue("CELL_PHONE")
                                            .matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 自动取电话号码
                                parmRow.setData("CELL_PHONE", samePatParm.getValue("CELL_PHONE"));
                            } else if (parmRow.getValue("CELL_PHONE").equals("")
                                    && samePatParm.getValue("TEL_HOME").trim().length() == 11
                                    && samePatParm.getValue("TEL_HOME").trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                                parmRow.setData("CELL_PHONE", samePatParm.getValue("TEL_HOME"));
                            }
                        } else {
                            contractD.setItem(row, "MR_NO", "Y");
                        }
                    } else contractD.setItem(row, "MR_NO", "Y");
                } else {
                    int rowCount = contractD.rowCount();
                    for (int j = 0; j < rowCount; j++) {// wanglong add 20140606
                        if (!contractD.isActive(j)) continue;
                        if (contractD.getItemString(j, "MR_NO")
                                .equals(patParm.getValue("MR_NO", 0))) {
                            if (patName.equals("")) {
                                this.messageBox("病案号为" + patParm.getValue("MR_NO", 0)
                                        + "的人员在系统中已存在，将不会被再次导入");
                            } else {
                                this.messageBox("病案号为" + patParm.getValue("MR_NO", 0) + "，姓名为 "
                                        + patParm.getValue("PAT_NAME", 0) + " 的人员在系统中已存在，将不会被再次导入");
                            }
                            continue patLoop;
                        }
                    }
                    contractD.setItem(row, "MR_NO", patParm.getData("MR_NO", 0));
                    if (parmRow.getValue("CELL_PHONE").equals("")
                            && !patParm.getValue("CELL_PHONE", 0).equals("")
                            && patParm.getValue("CELL_PHONE", 0).matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 自动取电话号码
                        parmRow.setData("CELL_PHONE", patParm.getValue("CELL_PHONE", 0));
                    } else if (parmRow.getValue("CELL_PHONE").equals("")
                            && patParm.getValue("TEL_HOME", 0).trim().length() == 11
                            && patParm.getValue("TEL_HOME", 0).trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                        parmRow.setData("CELL_PHONE", patParm.getValue("TEL_HOME", 0));
                    }
                }
            }
            // 存在多条就诊信息
            else if (patParm.getCount("MR_NO") > 1) {
                int j = 0;
                for (; j < patParm.getCount("MR_NO"); j++) {
                    if (patParm.getValue("PAT_NAME", j).equals(patName)) {
                        int rowCount = contractD.rowCount();
                        for (int k = 0; k < rowCount; k++) {// wanglong add 20140606
                            if (!contractD.isActive(k)) continue;
                            if (contractD.getItemString(k, "MR_NO")
                                    .equals(patParm.getValue("MR_NO", j))) {
                                if (patName.equals("")) {
                                    this.messageBox("病案号为" + patParm.getValue("MR_NO", j)
                                            + "的人员在系统中已存在，将不会被再次导入");
                                } else {
                                    this.messageBox("病案号为" + patParm.getValue("MR_NO", j) + "，姓名为 "
                                            + patParm.getValue("PAT_NAME", j)
                                            + " 的人员在系统中已存在，将不会被再次导入");
                                }
                                continue patLoop;
                            }
                        }
                        contractD.setItem(row, "MR_NO", patParm.getData("MR_NO", j));
                        if (parmRow.getValue("CELL_PHONE").equals("")
                                && !patParm.getValue("CELL_PHONE", j).equals("")
                                && patParm.getValue("CELL_PHONE", j).matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 自动取电话号码
                            parmRow.setData("CELL_PHONE", patParm.getValue("CELL_PHONE", j));
                        } else if (parmRow.getValue("CELL_PHONE").equals("")
                                && patParm.getValue("TEL_HOME", j).trim().length() == 11
                                && patParm.getValue("TEL_HOME", j).trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                            parmRow.setData("CELL_PHONE", patParm.getValue("TEL_HOME", j));
                        }
                        break;
                    }
                }
                if (j == patParm.getCount("MR_NO")) {
                    if (this.messageBox("病案号合并", "系统中存在和人员“" + patName + "”(序号" + seqNo
                            + ")身份证相同但姓名有所不同的病患记录，是否查看是不是他（她）？\n", 2) == 0) {
                        Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", patParm);
                        if (obj != null) {
                            TParm samePatParm = (TParm) obj;
                            int rowCount = contractD.rowCount();
                            for (int k = 0; k < rowCount; k++) {// wanglong add 20140606
                                if (!contractD.isActive(k)) continue;
                                if (contractD.getItemString(k, "MR_NO")
                                        .equals(samePatParm.getValue("MR_NO"))) {
                                    if (patName.equals("")) {
                                        this.messageBox("病案号为" + samePatParm.getValue("MR_NO")
                                                + "的人员在系统中已存在，将不会被再次导入");
                                    } else {
                                        this.messageBox("病案号为" + samePatParm.getValue("MR_NO")
                                                + "，姓名为 " + samePatParm.getValue("PAT_NAME")
                                                + " 的人员在系统中已存在，将不会被再次导入");
                                    }
                                    continue patLoop;
                                }
                            }
                            contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                            patName = samePatParm.getValue("PAT_NAME");
                            String sexCode = samePatParm.getValue("SEX_CODE");// add by wanglong 21030726
                            if (!parmRow.getValue("SEX_CODE").equals(sexCode)) {// add by wanglong 21030726
                                this.messageBox("人员“" + patName + "”(序号" + seqNo
                                        + ")从病患记录中传回的性别与excel导入的性别不同\n"
                                        + "（请检查该人员在“病患信息”中的信息与在“健检团体信息设定”中的信息是否一致");
                            }
                            if (parmRow.getValue("CELL_PHONE").equals("")
                                    && !samePatParm.getValue("CELL_PHONE").equals("")
                                    && samePatParm.getValue("CELL_PHONE")
                                            .matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 自动取电话号码
                                parmRow.setData("CELL_PHONE", samePatParm.getValue("CELL_PHONE"));
                            } else if (parmRow.getValue("CELL_PHONE").equals("")
                                    && samePatParm.getValue("TEL_HOME").trim().length() == 11
                                    && samePatParm.getValue("TEL_HOME").trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                                parmRow.setData("CELL_PHONE", samePatParm.getValue("TEL_HOME"));
                            }
                        } else {
                            contractD.setItem(row, "MR_NO", "Y");
                        }
                    } else contractD.setItem(row, "MR_NO", "Y");
                }
            }
            contractD.setItem(row, "IDNO", idNo.toUpperCase());
            contractD.setItem(row, "STAFF_NO", parmRow.getValue("STAFF_NO"));
            contractD.setItem(row, "PAT_NAME", parmRow.getValue("PY1"));
            contractD.setItem(row, "PY1", SystemTool.getInstance().charToCode(patName));
            contractD.setItem(row, "COMPANY_CODE", companyCode);
            contractD.setItem(row, "CONTRACT_CODE", contractCode);
            contractD.setItem(row, "CONTRACT_DESC", contractDesc);
            contractD.setItem(row, "PACKAGE_CODE", parmRow.getValue("PACKAGE_CODE"));
            contractD.setItem(row, "SEX_CODE", parmRow.getValue("SEX_CODE"));
            Timestamp birthday = SystemTool.getInstance().getDate();
            if (!(parmRow.getData("BIRTH_DATE") instanceof Timestamp)) {
                birthday = StringTool.getTimestamp(parmRow.getValue("BIRTH_DATE"), "yyyyMMdd");
            } else birthday = parmRow.getTimestamp("BIRTH_DATE");
            contractD.setItem(row, "BIRTHDAY", birthday);
            if (StringUtils.isNotEmpty(parmRow.getValue("DISCNT"))) {
            	// 折扣 modify by wanglong 20130116
                contractD.setItem(row, "DISCNT", parmRow.getDouble("DISCNT"));
            } else {
            	contractD.setItem(row, "DISCNT", discnt);
            }
            
            if (parmRow.getValue("CELL_PHONE").equals("")
                    && !contractD.getItemString(row, "MR_NO").equals("Y")) {// wanglong add 20140512 自动取电话号码
                if (patParm.getCount("MR_NO") > 0) {
                    if (!patParm.getValue("CELL_PHONE", 0).trim().equals("")
                            && patParm.getValue("CELL_PHONE", 0).trim()
                                    .matches("[^\u4e00-\u9fa5]*")) {// 手机号(不含中文)
                        contractD.setItem(row, "TEL", patParm.getValue("CELL_PHONE", 0));
                    } else if (patParm.getValue("TEL_HOME", 0).trim().length() == 11
                            && patParm.getValue("TEL_HOME", 0).trim().matches("[^\u4e00-\u9fa5]*")) {// 家庭电话处如果填写的是11位的，当做手机号
                        contractD.setItem(row, "TEL", patParm.getValue("TEL_HOME", 0));
                    }
                }
            } else {
                contractD.setItem(row, "TEL", parmRow.getValue("CELL_PHONE"));
            }
            contractD.setItem(row, "SEQ_NO", seqNo);
            // 婚姻状态 add by wanglong 20130116
            contractD.setItem(row, "MARRIAGE_CODE", parmRow.getValue("MARRIAGE_CODE"));
            // 部门 add by wanglong 20130225
            contractD.setItem(row, "PAT_DEPT", parmRow.getValue("PAT_DEPT"));
            if (parmRow.getValue("PRE_CHK_DATE").equals("")) {
                contractD.setItem(row, "PRE_CHK_DATE", optDate);
            } else {
                Timestamp preChkDate =
                        StringTool.getTimestamp(parmRow.getValue("PRE_CHK_DATE"), "yyyyMMdd");
                contractD.setItem(row, "PRE_CHK_DATE", preChkDate);
            }
            contractD.setItem(row, "FOREIGNER_FLG", parmRow.getValue("FOREIGNER_FLG"));
            contractD.setItem(row, "COMPANY_PAY_FLG", parmRow.getValue("COMPANY_PAY_FLG"));
            contractD.setItem(row, "OPT_USER", optUser);
            contractD.setItem(row, "OPT_TERM", optTerm);
            contractD.setItem(row, "OPT_DATE", optDate);
            contractD.setItem(row, "ROLE_TYPE", roleType);
            contractD.setItem(row, "PLAN_NO", parmRow.getValue("CONTRACT_CODE"));
            contractD.setItem(row, "PLAN_DESC", parmRow.getValue("CONTRACT_DESC"));
            contractD.setActive(row, true);
        }
        if (!contractD.getItemString(row, "PAT_NAME").equals("")) {
            int newRow = contractD.insertRow();
            contractD.setItem(newRow, "COMPANY_CODE", companyCode);
            contractD.setItem(newRow, "CONTRACT_CODE", contractCode);
            contractD.setItem(newRow, "CONTRACT_DESC", contractDesc);
            contractD.setItem(newRow, "PRE_CHK_DATE", optDate);
            if (!StringUtil.isNullString(contractD.packageCode)) {
                contractD.setItem(newRow, "PACKAGE_CODE", contractD.packageCode);
            } else contractD.setItem(newRow, "PACKAGE_CODE", "");
            // contractD.setItem(row, "MR_NO", "Y");
            contractD.setItem(newRow, "OPT_USER", optUser);
            contractD.setItem(newRow, "OPT_TERM", optTerm);
            contractD.setItem(newRow, "DISCNT", discnt);
            contractD.setItem(newRow, "OPT_DATE", optDate);
            contractD.setItem(row, "ROLE_TYPE", roleType);
            contractD.setItem(newRow, "SEQ_NO",
                              contractD.getItemInt(contractD.rowCount() - 2, "SEQ_NO") + 1);
            contractD.setActive(newRow, false);
        }
        double amt = contractD.getContractAmt();
        // this.messageBox_(amt);
        contractM.setItem(conTab.getSelectedRow(), "SUBTOTAL", amt);
        contractM.setItem(conTab.getSelectedRow(), "TOT_AMT", amt);
        mrTab.setDSValue();
        this.messageBox_("导入成功");
	}
	
	/**
	 * 读卡
	 */
	public void onReadIdCard(){
		TParm idParm = IdCardO.getInstance().readIdCard();
		if (idParm.getErrCode()<0) {
			this.messageBox(idParm.getErrText());
			return;
		}
		if (idParm.getCount() > 0) {// 多行数据显示
			if (idParm.getCount()==1) {
				setIdNoInfoToTable(idParm);
			}else{
				Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x",
						idParm);
				TParm patParm = new TParm();
				if (obj != null) {
					patParm = (TParm) obj;
					setIdNoInfoToTable(patParm);
				}else{
					return ;
				}
			}
		} else {
			setIdNoInfoToTable(idParm);
		}
	}
	
	/**
	 * 设置身份证信息
	 * @param idParm
	 */
	public  void setIdNoInfoToTable(TParm idParm){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		mrTab = (TTable) this.getComponent("TAB_MR");
		TDataStore ds = mrTab.getDataStore();
		int addRow = ds.rowCount()-1;
		ds.setItem(addRow, "IDNO", idParm.getValue("IDNO"));
		ds.setActive(addRow, false);
		mrTab.setDSValue();
		TTableNode tNode = getTTableNode(mrTab,addRow,mrTab.getColumnIndex("IDNO"),null,idParm.getValue("IDNO"));
		
		if(onTabMrValueChanged(tNode)){
			ds.setItem(addRow, "IDNO", null);
			ds.setActive(addRow, false);
			mrTab.setDSValue();
			return;
		}
		ds.setItem(addRow, "PAT_NAME", idParm.getValue("PAT_NAME"));
		ds.setActive(addRow, false);
		mrTab.setDSValue();
		tNode = getTTableNode(mrTab,addRow,mrTab.getColumnIndex("PAT_NAME"),null,idParm.getValue("PAT_NAME"));
		if(onTabMrValueChanged(tNode)){
			ds.setItem(addRow, "IDNO", null);
			ds.setItem(addRow, "PAT_NAME", null);
			ds.setActive(addRow, false);
			mrTab.setDSValue();
			return;
		}
	}
	
	/**
	 * 创造一个TTableNode对象
	 * @param table
	 * @param row
	 * @param column
	 * @param oldValue
	 * @param value
	 * @return
	 */
	public TTableNode getTTableNode(TTable table,int row,int column,String oldValue,String value){
		TTableNode tNode = new TTableNode();
		tNode.setTable(table);
		tNode.setRow(row);
		tNode.setColumn(column);
		tNode.setOldValue(oldValue);
		tNode.setValue(value);
		return tNode;
	}
	
}
