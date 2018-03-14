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
 * Title: �������������Ϣ������
 * </p>
 * 
 * <p>
 * Description: �������������Ϣ������
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

    /** ���塢��ͬ��Ա��TABLE */
    private TTable comTab, conTab, mrTab;
    /** ��˾��Ϣ */
    private HRMCompany company;
    /** ��ͬ���� */
    private HRMContractM contractM;
    /** ��ͬ��ϸ */
    private HRMContractD contractD;
    /** ɾ��ʱ�õı��� */
    private String tableName;
    /** ������ͬTABLEʱ��¼��ͬ���� */
    private int conDescRow;
    private boolean isChangeContract = false;
    private ArrayList<String> patLogSQLList = new ArrayList<String>();// wanglong add 20140623
    private String roleType; // add by wangb 2016/06/21 ��¼��ɫ
	private TTextFormat packageM;// add by wangb 2016/7/5 �ײ�������
    
    /**
     * ��ʼ���¼�
     */
    public void onInit() {
        super.onInit();
        // ��ʼ���ؼ�
        initComponent();
        // add by wanglong 20130304 ��Աtableѡ���б��
        TTableCellRenderer cellRenderer = new TTableCellRenderer(mrTab) {

            public void setComponentForeColor(Component component, boolean isSelected, int row,
                                              int column) {
                if (isSelected) {
                    component.setForeground(Color.RED);// TUIStyle.getTableSelectionForeColor()
                    return;
                }
                /**
                 * �Զ�����������ɫ
                 */
                Color color = getTable().getRowTextColor(row);
                if (color != null) {
                    component.setForeground(color);
                    return;
                }
                // �Զ�����ǰ����ɫ
                color = getColumnForeColor(getTable().getColumnModel().getColumnIndex(column));
                if (color != null) {
                    component.setForeground(color);
                    return;
                }
                component.setForeground(TUIStyle.getTableForeColor());
            }
        };
        mrTab.setCellRenderer(cellRenderer);
        //����ķ������¶��뷽ʽ������.x�ļ�����Ч��ֻ��д�ڳ�����
        conTab.setColumnHorizontalAlignmentData("1,left;2,left;3,left;4,left;5,left;6,right;7,right;8,right;9,left;10,left");
        mrTab.setColumnHorizontalAlignmentData("1,left;3,left;6,left;7,left;9,left;10,right;11,left;12,left;14,left");
        // ��ʼ������
        initData();
        onClear();
    }

    /**
     * ��ʼ������
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
        
        // add by wangb 2016/06/21 ��¼��ɫ
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
        
		// ��ѯ�ײ���Ϣ
		TParm pakcageData = HRMFeePackTool.getInstance().selectHrmPackageByRoleType(filterRoleType);
        packageM.setPopupMenuData(pakcageData);
        packageM.setComboSelectRow();
        packageM.popupMenuShowData();
    }

    /**
     * ��ʼ���ؼ�
     */
    private void initComponent() {
        comTab = (TTable) this.getComponent("TAB_COMPANY");
        conTab = (TTable) this.getComponent("TAB_CONTRACT");
        mrTab = (TTable) this.getComponent("TAB_MR");
        // ��ͬTABLEֵ�ı��¼�
        conTab.addEventListener("TAB_CONTRACT->" + TTableEvent.CHANGE_VALUE, this,
                                "onTabConValueChanged");
        // ��ͬϸ��TABLEֵ�ı��¼�
        mrTab.addEventListener("TAB_MR->" + TTableEvent.CHANGE_VALUE, this, "onTabMrValueChanged");
        mrTab.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onCheckBox");
        
        // add by wangb 2016/07/04 һ���ٴ���¼��ʾ��������ļ��Ϣ��ť
        if (!this.getPopedem("SYSDBA") && !this.getPopedem("PIC")) {
        	this.callFunction("UI|recruitInfo|visible", false);
        }
        
        packageM = (TTextFormat) this.getComponent("PACKAGE_CODE");
    }

    /**
     * ����TABLE�����¼�
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
     * ��ͬTABLE�����¼�
     */
    public void onTabConClicked() {
        tableName = "CONTRACT";
        int row = conTab.getSelectedRow();
        String contractCode = contractM.getItemString(row, "CONTRACT_CODE");
        conDescRow = row;
        if (StringUtil.isNullString(contractCode)) {
            return;
        }
        // ͨ����ͬ���롢���������˺�ͬϸ����Ϣ���ж������ͬϸ���������һ�в��ǿ��У�������һ��
        contractD.onQuery(contractM.getItemString(row, "COMPANY_CODE"),
                          contractM.getItemString(row, "CONTRACT_CODE"));// wanglong add20140512
//        contractD.filt(contractM.getItemString(row, "CONTRACT_CODE"),
//                       contractM.getItemString(row, "COMPANY_CODE"));
        if (contractD.rowCount() < 1
                || !StringUtil.isNullString(contractD.getItemString(contractD.rowCount() - 1,
                                                                    "MR_NO"))) {
            int rowCount = contractD.rowCount();
            // add by wangb 2016/06/22 ���ý�ɫ����
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
     * Ա����ͬ���(�����⣬���޸�)
     */
    public void onChangeContract() {
        int row = mrTab.getSelectedRow();
        if (row < 0) {
            this.messageBox("��ѡ����Ա");
            return;
        }
        TParm showParm = mrTab.getShowParmValue().getRow(row);
        if (showParm.getValue("MR_NO").equals("Y")) {
            this.messageBox("�½�����Ա��δ���浽���ݿ⣬���ܱ��������ִ�б������");
            return;
        }
        if (showParm.getValue("COVER_FLG").equals("Y")) {
            if (this.messageBox("��ʾ", "����Ա�ѱ������Ƿ��������", 2) != 0) {
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
                if (this.messageBox("��ʾ", "����Ա�Ѵ�Ʊ���Ƿ��������", 2) != 0) {
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
            this.messageBox("��ͬ���ȡ��!");
            return;
        }
        // �Ȳ�ѯ������Ƿ�������һ����ͬ��в��ܱ��
        String selectSql =
                " SElECT A.COMPANY_CODE FROM HRM_CONTRACTD A " + " WHERE A.COMPANY_CODE='"
                        + reParm.getValue("COMPANY_CODE") + "' AND A.CONTRACT_CODE='"
                        + reParm.getValue("CONTRACT_CODE") + "' AND A.MR_NO='"
                        + dataStoreParm.getValue("MR_NO") + "' ";
        TParm selectParm = new TParm();
        selectParm.setData(TJDODBTool.getInstance().select(selectSql));
        if (selectParm.getCount("COMPANY_CODE") > 0) {
            this.messageBox("��ͬ���ʧ�ܣ���ת���ͬ���Ѵ��ڴ���Ա��");
            return;
        }
        String updateContractSql =
                "UPDATE HRM_CONTRACTD SET COMPANY_CODE = '#', CONTRACT_CODE = '@', CONTRACT_DESC = '!' "
                        + " WHERE CONTRACT_CODE = '&' AND MR_NO = '��'";
        updateContractSql = updateContractSql.replaceFirst("#", reParm.getValue("COMPANY_CODE"));
        updateContractSql = updateContractSql.replaceFirst("@", reParm.getValue("CONTRACT_CODE"));
        updateContractSql = updateContractSql.replaceFirst("!", reParm.getValue("CONTRACT_DESC"));
        updateContractSql =
                updateContractSql.replaceFirst("&", dataStoreParm.getValue("CONTRACT_CODE"));
        updateContractSql = updateContractSql.replaceFirst("��", dataStoreParm.getValue("MR_NO"));
        String[] updateSql = new String[]{};
        String caseNoSql =
                "SELECT A.BILL_NO,B.CASE_NO FROM HRM_CONTRACTD A, HRM_PATADM B "
                        + "      WHERE A.CONTRACT_CODE = B.CONTRACT_CODE "
                        + "        AND A.MR_NO = B.MR_NO           "
                        + "        AND A.CONTRACT_CODE = '&' AND A.MR_NO = '��'";
        caseNoSql = caseNoSql.replaceFirst("&", dataStoreParm.getValue("CONTRACT_CODE"));
        caseNoSql = caseNoSql.replaceFirst("��", dataStoreParm.getValue("MR_NO"));
        TParm caseNoParm = new TParm(TJDODBTool.getInstance().select(caseNoSql));
        if (caseNoParm.getValue("CASE_NO", 0).equals("")) {
            updateSql = new String[]{updateContractSql };
        } else {
            String updateOrderSql = "UPDATE HRM_ORDER SET CONTRACT_CODE = '@' WHERE CASE_NO ='��'";
            updateOrderSql = updateOrderSql.replaceFirst("@", reParm.getValue("CONTRACT_CODE"));
            updateOrderSql = updateOrderSql.replaceFirst("��", caseNoParm.getValue("CASE_NO", 0));
            String updatePatSql =
                    "UPDATE HRM_PATADM SET COMPANY_CODE = '#', CONTRACT_CODE = '@' WHERE CASE_NO = '��'";
            updatePatSql = updatePatSql.replaceFirst("#", reParm.getValue("COMPANY_CODE"));
            updatePatSql = updatePatSql.replaceFirst("@", reParm.getValue("CONTRACT_CODE"));
            updatePatSql = updatePatSql.replaceFirst("��", caseNoParm.getValue("CASE_NO", 0));
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
                            		" AND CONTRACT_CODE = '&' ";//huangjw 20150910���BILL_NO ɸѡ
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
            this.messageBox("���ʧ�ܣ������ͬ��" + reParm.getValue("CONTRACT_DESC") + "�����Ƿ��Ѵ��ڸ���Ա");
            return;
        }
//        initData();
        onTabConClicked();
        isChangeContract = false;
        this.messageBox("����ɹ�");
    }

    /**
     * Ա��TABLE�����¼�
     */
    public void onTabMrClicked() {
        tableName = "MR";
        // conTab.clearSelection() ;
    }

    /**
     * ��ͬTABLEֵ�ı��¼�
     * 
     * @param tNode
     */
    public boolean onTabConValueChanged(TTableNode tNode) {
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String colName = conTab.getParmMap(column);
        Object value = tNode.getValue();
        Object oldValue = tNode.getOldValue();
        // ����Ǻ�ͬ���ƣ����Զ����ɼ�ƴ���룬������ͬ���Ƹ�ֵ��ÿһ����ͬϸ��
        if ("CONTRACT_DESC".equalsIgnoreCase(colName)) {
            String strValue = (String) value;
            String strOldValue = (String) oldValue;
            if (StringUtil.isNullString(strValue) || strValue.equalsIgnoreCase(strOldValue)) {
                return true;
            }
            // ========================= chenxi modify 20130313
            for (int i = 0; i < conTab.getRowCount(); i++) {
                if (strValue.equals(conTab.getItemString(i, "CONTRACT_DESC"))) {
                    this.messageBox("��ͬ���Ʋ����ظ�");
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
        // ������ײʹ������ײʹ��븳ֵ��ÿһ����ͬϸ��
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
        // ������ۿ���Ϣ��˾֧��%�ȣ����ж��Ƿ��ֵ����1������ǣ���Ϊ�Ƿ����֡������޸�
        if ("DISCNT".equalsIgnoreCase(colName) || "CP_PAY".equalsIgnoreCase(colName)) {
            double num = Double.parseDouble(tNode.getValue() + "");
            double oldNum = Double.parseDouble(tNode.getOldValue() + "");
            if (num == oldNum) {
                return true;
            }
            if (num > 1.0) {
                this.messageBox_("�ۿ���Ӧ����0��1֮��");
                return true;
            }
            String billFlg = contractM.getItemString(row, "BILL_FLG");
            if (billFlg != null && billFlg.equals("Y")) {
                this.messageBox_("�Ѿ����㲻���޸�");
                return true;
            }
            int count = contractD.rowCount();
            for (int i = 0; i < count; i++) {
                contractD.setItem(i, "DISCNT", num);
            }
            // ==== add by lx 2012/05/18 �����ܼ�=====$$//
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
     * Ա��TABLEֵ�ı��¼�
     * 
     * @param tNode
     * @return
     */
    public boolean onTabMrValueChanged(TTableNode tNode) {
        // if (conTab.getItemString(conTab.getSelectedRow(), "CONTRACT_DESC").trim().equals("")) {
        // add-by-wanglong-20130302
        // this.messageBox_("��ͬ��Ϊ�գ�����¼����Ա");
        // return true;
        // }
        tableName = "MR";
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String colName = mrTab.getParmMap(column);
        Object value = tNode.getValue();
        Object oldValue = tNode.getOldValue();
        if (TypeTool.getBoolean(contractD.getItemData(row, "COVER_FLG"))
                && !"SEQ_NO".equalsIgnoreCase(colName)) {//wanglong 20140924 ������Աֻ������ܸ�
            this.messageBox_("�ѵ�����Ա��Ϣ�����޸�");
            return true;
        }
        if (StringUtil.isNullString(contractD.getItemString(row, "CONTRACT_DESC"))) {
            this.messageBox("û�к�ͬ���ƣ��������ƺ�ͬ��Ϣ��");
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
        // ������ۿ���Ϣ��˾֧��%�ȣ����ж��Ƿ��ֵ����1������ǣ���Ϊ�Ƿ����֡������޸�
        if ("DISCNT".equalsIgnoreCase(colName)) {
            double num = Double.parseDouble(tNode.getValue() + "");
            double oldNum = Double.parseDouble(tNode.getOldValue() + "");
            if (num == oldNum) {
                return true;
            }
            if (num > 1 || num < -1) {
                this.messageBox_("�ۿ���Ӧ����0��1֮��");
                return true;
            }
            contractM.setActive(row, true);
            return false;
        }
        // ������
        if ("MR_NO".equalsIgnoreCase(colName)) {// add by wanglong 20130506
            String strValue = (String) value;
            if (strValue.trim().equals("")) {
                return true;
            }
            if (contractD.isActive(row)) {
                this.messageBox("��ȷ���Ĳ����Ų����޸ģ���ɾ��������¼������¼��");
                return true;
            }
            Pat pat = Pat.onQueryByMrNo(strValue.trim());
            if (pat == null) {
                this.messageBox("�����Ų�����");
                return true;
            }
            String srcMrNo = PatTool.getInstance().checkMrno(strValue.trim());
            String mrNo = pat.getMrNo();
            if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(mrNo)) {// wanglong
                                                                             // add 20150423
                this.messageBox("������" + srcMrNo + " �Ѻϲ��� " + "" + mrNo);
            }
            TParm parm = PatTool.getInstance().getInfoForMrno(mrNo);
            if (parm.getCount() <= 0) {
                this.messageBox_("�����Ų�����");
                return true;
            }
            int rowCount = contractD.rowCount();
            for (int i = 0; i < rowCount; i++) {// wanglong add 20140606
                if (!contractD.isActive(i)) continue;
                if (i == row) continue;
                if (contractD.getItemString(i, "MR_NO").equals(strValue)) {
                    this.messageBox("����Ա�Ѵ���");
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
                                                                                     // �Զ�ȡ�绰����
                contractD.setItem(row, "TEL", parm.getData("CELL_PHONE", 0));
            } else if (parm.getValue("TEL_HOME", 0).trim().length() == 11
                    && parm.getValue("TEL_HOME", 0).trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
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
                                    contractD.getItemInt(row, "SEQ_NO") + 1);// �����µ�һ��
            }
            mrTab.setDSValue();
            tNode.setValue(mrNo);// add by wanglong 20130829
            tNode.getTable().grabFocus();
            mrTab.setSelectedColumn(tNode.getTable().getColumnIndex("PACKAGE_CODE"));
            setColorTable();
            return false;
        }
        // �ж��Ƿ�Ϊ��Ч���֤��
        if ("IDNO".equalsIgnoreCase(colName)) {
            String strValue = ((String) value).trim();
            if (strValue.equals("")) {
                return true;
            }
//            String strOldValue = (String) oldValue;
            String isForeigner = contractD.getItemString(row, "FOREIGNER_FLG");
            String checkid = checkID(strValue);
            if (!checkid.equals("TRUE") && !checkid.equals("FALSE")) {// add by wanglong 20130409
                this.messageBox_("���֤У��λ�������һλӦ��Ϊ" + checkid.substring(17, 18));
                contractD.setItem(row, "SEX_CODE", StringTool.isMaleFromID(checkid));// add by
                                                                                     // wanglong
                                                                                     // 20130417
                contractD.setItem(row, "BIRTHDAY", StringTool.getBirdayFromID(checkid));
                return false;// modify by wanglong 20130416
            }
            if (!isId(strValue)
                    && ("N".equalsIgnoreCase(isForeigner) || StringUtil.isNullString(isForeigner))) {
                this.messageBox_("���֤����ȷ");
                return true;
            }
            int rowCount = contractD.rowCount();
            for (int i = 0; i < rowCount; i++) {// wanglong add 20140606
                if (!contractD.isActive(i)) continue;
                if (i == row) continue;
                if (contractD.getItemString(i, "FOREIGNER_FLG").equals(isForeigner)
                        && contractD.getItemString(i, "IDNO").equals(strValue)) {
                    this.messageBox("�����֤���Ѵ���");
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
                if (this.messageBox("�����źϲ�", "ϵͳ�д���ʹ�ô�֤������Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {
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
                                                                                                     // �Զ�ȡ�绰����
                            contractD.setItem(row, "TEL", samePatParm.getData("CELL_PHONE"));
                        } else if (samePatParm.getValue("TEL_HOME").trim().length() == 11
                                && samePatParm.getValue("TEL_HOME").trim()
                                        .matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
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
                                    .get(j).indexOf(contractD.getItemString(row, "���֤")) != -1)) {
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
                insertSql = insertSql.replaceFirst("#", "���֤");
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
                                            .get(j).indexOf(contractD.getItemString(row, "����")) != -1)) {
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
                        insertSql1 = insertSql1.replaceFirst("#", "����");
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
                    //if (parm.getValue("SEX_CODE", 0) != contractD.getItemString(row, "SEX_CODE")) {wanglong��ʷ��¼
                    //zhanglei �޸� ������ү�ģ�=�������ж�Ϊ��ʸ����ж�����
                    if (!parm.getValue("SEX_CODE", 0).equals(contractD.getItemString(row, "SEX_CODE"))) {
                    	//this.messageBox("11111111111");
                        for (int j = 0; j < patLogSQLList.size(); j++) {
                            if ((patLogSQLList.get(j).indexOf(contractD.getItemString(row, "MR_NO")) != -1 && patLogSQLList
                                    .get(j).indexOf(contractD.getItemString(row, "SEX_CODE")) != -1)
                                    || (patLogSQLList.get(j).indexOf(contractD.getItemString(row, "MR_NO")) != -1 && patLogSQLList
                                            .get(j).indexOf(contractD.getItemString(row, "�Ա�")) != -1)) {
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
                        insertSql2 = insertSql2.replaceFirst("#", "�Ա�");
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
            contractD.setItem(row, "IS_VIP", "1");//��� Ĭ��Ϊ��ͨ��add by huangjw 20160920
            if (!contractD.getItemString(row, "PACKAGE_CODE").equals("")
                    && !contractD.getItemString(row, "PAT_NAME").equals("")
                    && !contractD.getItemString(row, "MR_NO").equals("")) {// wanglong add 20141225
            	contractD.setRoleType(roleType);
                contractD.insertRow(contractD.getItemString(row, "COMPANY_CODE"),
                                    contractD.getItemString(row, "CONTRACT_CODE"),
                                    contractD.getItemString(row, "CONTRACT_DESC"),
                                    contractD.getItemDouble(row, "DISCNT"),
                                    contractD.getItemInt(row, "SEQ_NO") + 1);// �����µ�һ��
            }
            mrTab.setDSValue();
            tNode.getTable().grabFocus();
            mrTab.setSelectedColumn(tNode.getTable().getColumnIndex("PACKAGE_CODE"));
            setColorTable();
            return false;
        }
        // ����ǳ������֤�ŵ����������ж����֤��û��Ͳ�����д
        String buffer = contractD.isFilter() ? contractD.FILTER : contractD.PRIMARY;//wanglong add 20141225
        if (TCM_Transform.isNull(contractD.getItemData(row, "IDNO", buffer))
                && !TCM_Transform.getBoolean(contractD.getItemData(row, "FOREIGNER_FLG", buffer))) {
            this.messageBox_("�����������֤��");
            return true;
        }
        // ��������
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
                    this.messageBox("�����Ա��Ϣ����");
                    return true;
                }
                if (result1.getCount() > 0) {
                    this.messageBox("ҽ����չ�������ܸ��������������������ݲ�ƥ��");// add by wanglong 20130308
                    return true;
                }
            } else if (!StringUtil.isNullString(mrNo1)) {
                String idNo = contractD.getItemString(row, "IDNO");
                String mrNo = contractD.getItemString(row, "MR_NO");
                Timestamp birthday = contractD.getItemTimestamp(row, "BIRTHDAY");
                // add by wanglong 20130223 ����ͬ��ͬ�������Ա�������źϲ�
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
                        if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + strValue
                                + "��ͬ��ͬ����Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {
                            Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
                            if (obj != null) {
                                TParm patParm = (TParm) obj;
                                contractD.setItem(row, "MR_NO", patParm.getValue("MR_NO"));
                                mergeMrFlg = true;
                            }
                        }
                    }
                }
                // add by wanglong 20130223 ����ͬ��ͬ�������Ա�������źϲ�
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
                        if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + strValue
                                + "��ͬ��ͬ����Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {
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
                                    .get(j).indexOf(contractD.getItemString(row, "����")) != -1)) {
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
                insertSql = insertSql.replaceFirst("#", "����");
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
                contractD.insertRow(comCode, contractCode, contractDesc, discnt, count + 1);//�����µ�һ��
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
        // ��������
        if ("BIRTHDAY".equalsIgnoreCase(colName)) {// add by wanglong 20130223
            Timestamp newBirthday = (Timestamp) value;
            String idNo = contractD.getItemString(row, "IDNO");
            String mrNo = contractD.getItemString(row, "MR_NO");
            String patName = contractD.getItemString(row, "PAT_NAME");
            if (idNo.length() == 18 && isId(idNo)) {
                Timestamp birthday = StringTool.getBirdayFromID(idNo);
                if (!newBirthday.equals(birthday)) {
                    this.messageBox("���������֤����");
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
                    if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + patName
                            + "��ͬ��ͬ����Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {
                        Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
                        if (obj != null) {
                            TParm patParm = (TParm) obj;
                            contractD.setItem(row, "MR_NO", patParm.getValue("MR_NO"));
                        }
                    }
                }
            }
        }
        // Ԥ��ʱ��
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
        // ��,30,boolean;��˾����,140;��ͬ����,140;
        // �ۿ�,60,double;���ʽ,100,PAY_TYPE;��˾֧��,100,double;���㷽ʽ,100;��ԼС��,80,double;�ܼ�,60,double;�˵���,100;Ʊ�ݺ�,100
        // BILL_FLG; COMPANY_DESC;CONTRACT_DESC;DISCNT; PAY_TYPE; CP_PAY;
        // RECP_TYPE; TOT_AMT; REAL_COUNT; BILL_NO;RECEIPT_NO
        // ��д�ײʹ���ʱ�����ײ͵��ܼ�д���ͬ�������
        if ("PACKAGE_CODE".equalsIgnoreCase(colName)) {
            String mrNo1 = contractD.getItemString(row, "MR_NO");
            String contractCode1 = contractD.getItemString(row, "CONTRACT_CODE");
            String sql1 =
                    "SELECT * FROM HRM_PATADM WHERE MR_NO = '" + mrNo1 + "' AND CONTRACT_CODE = '"
                            + contractCode1 + "'";
            // System.out.println("-----------sql1---------------"+sql1);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql1));
            if (result.getErrCode() < 0) {
                this.messageBox("�����Ա��Ϣ����");
                return true;
            }
            if (result.getCount() > 0) {
                this.messageBox("ҽ����չ�������ܸ����ײ�");// add by wanglong 20130308
                return true;
            }
            // boolean flag = false;
            // String sql1 =
            // "SELECT PACKAGE_DESC FROM HRM_PACKAGEM WHERE PACKAGE_CODE = '" + value + "'";
            // TParm result = new TParm(TJDODBTool.getInstance().select(sql1));
            // // System.out.println("====================PACKAGE_DESC=================="+result);
            // if (result.getErrCode() < 0) {
            // this.messageBox("��ѯ�ײ���Ϣ����");
            // return true;
            // }
            // if (result.getCount() > 0) {
            // if (result.getValue("PACKAGE_DESC", 0).indexOf("�Զ���") < 0) {
            // this.messageBox("���ܸ��ĳɴ��ײͣ��������ݲ�ƥ��");// modify by wanglong 20130308
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
            // $$==== add by lx 2012-05-19 ��ͬ�ײͱ仯���ܼ�Ҳ�仯start====$$//
            // this.messageBox("SUBTOTAL"+contractM.getItemDouble(conDescRow,
            // "SUBTOTAL"));
            contractM.setItem(conDescRow,
                              "TOT_AMT",
                              StringTool.round(contractM.getItemDouble(conDescRow, "SUBTOTAL")
                                      * contractM.getItemDouble(conDescRow, "DISCNT"), 2));
            // this.messageBox("==TOT_AMT=="+contractM.getItemDouble(conDescRow,
            // "TOT_AMT"));
            // $$==== add by lx 2012-05-19 ��ͬ�ײͱ仯���ܼ�Ҳ�仯 end====$$//
            conTab.setDSValue(conDescRow);
            mrTab.setSelectedRow(mrSelectRow);
            mrTab.setSelectedColumn(mrSelectColumn);
            conTab.setSelectedRow(conSelectRow);
            conTab.setSelectedColumn(conSelectColumn);
            // $$======add by lx 2012-06-10 �ı��ײ���ɾ��
            // HRM_ORDER(CONTRACT_CODE,MR_NO)��===$$//
            String flg = contractD.getItemString(row, "FOREIGNER_FLG").trim();// �����ע��
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
            // $$======add by lx 2012-06-10 �ı��ײ���ɾ��
            // HRM_ORDER(CONTRACT_CODE,MR_NO)��===$$//
            return false;
        }
        // ���
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
     * ɾ���¼�
     */
    public void onDelete() {
        if (StringUtil.isNullString(tableName)) {
            this.messageBox_("û��ɾ������");
            return;
        }
        // ���ɾ��������Ϣ�����ͬ���ϸ����Ϣͬʱɾ��
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
                this.messageBox("��ѯ���屨����Ϣ����");
                return;
            }
            if (conParm.getCount() > 0) {
                this.messageBox("����ɾ�����еĺ�ͬ��Ϣ");
                return;
            }
            if (this.messageBox("��ʾ", "�Ƿ�ɾ��", 2) != 0) {
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
        // ���ɾ����ͬ������Ϣ�����ͬϸ��ͬʱɾ��
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
                this.messageBox("��ѯ��ͬ��չ����Ϣʧ��");
                return;
            }
            if (coverFlg == true) {
                this.messageBox("�ú�ͬ������Ա�������޷�ɾ��");// modify by wanglong 20130417
                return;
            } else if (check.getCount() > 0 && check.getInt("NUM", 0) > 0) {
                if (this.messageBox("��ʾ", "�ú�ͬ����Աҽ����չ�����Ƿ����ɾ��", 2) != 0) {
                    return;
                }
            } else {
                if (this.messageBox("��ʾ", "�Ƿ�ɾ��", 2) != 0) {
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
        // ��ͬϸ����Ϣ����ĳ�н���ɾ��
        if ("MR".equalsIgnoreCase(tableName)
                && ((TPanel) this.getComponent("tPanel_2")).isShowing()) {
            int row = mrTab.getSelectedRow();
            if (TypeTool.getBoolean(contractD.getItemData(row, "COVER_FLG"))) {
                this.messageBox("����Ա�ѱ���������ɾ��");// add by wanglong 20130314
                return;
            }
            //=======================add by wanglong 20130922
            String mrSql =
                    "SELECT DISTINCT MR_NO FROM HRM_PATADM WHERE CONTRACT_CODE = '#' AND MR_NO = '#'";
            mrSql = mrSql.replaceFirst("#", contractD.getItemString(row, "CONTRACT_CODE"));
            mrSql = mrSql.replaceFirst("#", contractD.getItemString(row, "MR_NO"));
            TParm mrParm = new TParm(TJDODBTool.getInstance().select(mrSql));
            if (mrParm.getErrCode() != 0) {
                this.messageBox("��ѯ����ҽ��չ����Ϣ����");
                return;
            }
            if (mrParm.getCount() > 0) {
                this.messageBox("����Աҽ����չ��������ɾ��");
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
     * ����
     */
    public void onSave() {
        comTab.acceptText();
        conTab.acceptText();
        mrTab.acceptText();
        int rowCount = contractD.rowCount();
        for (int i = 0; i < rowCount; i++) {
            if (!contractD.isActive(i)) continue;
            String flg = contractD.getItemString(i, "FOREIGNER_FLG").trim();// �����ע��
            String idNo = contractD.getItemString(i, "IDNO").trim();// ���֤��
            String sexCode = contractD.getItemString(i, "SEX_CODE").trim();// �Ա�
            String packageCode = contractD.getItemString(i, "PACKAGE_CODE").trim();// �ײ�
            String birthday = contractD.getItemString(i, "BIRTHDAY");// ��������
            String patName = contractD.getItemString(i, "PAT_NAME").trim();// ����
            if (!flg.equals("Y") && idNo.equals("")) {
                if (!patName.equals("")) {
                    this.messageBox("��" + (i + 1) + "�У�����Ϊ " + patName + " ����Ա���֤��Ϊ�գ�");
                } else this.messageBox("��" + (i + 1) + "�У����֤��Ϊ�գ�");
                return;
            }
            if (sexCode.equals("")) {
                if (!patName.equals("")) {
                    this.messageBox("��" + (i + 1) + "�У�����Ϊ " + patName + " ����Ա�Ա�Ϊ�գ�");
                } else this.messageBox("��" + (i + 1) + "�У��Ա�Ϊ�գ�");
                return;
            }
            if (birthday == null || birthday.length() == 0) {
                if (!patName.equals("")) {
                    this.messageBox("��" + (i + 1) + "�У�����Ϊ " + patName + " ����Ա��������Ϊ�գ�");
                } else this.messageBox("��" + (i + 1) + "�У���������Ϊ�գ�");
                return;
            }
            if (packageCode.equals("")) {
                if (!patName.equals("")) {
                    this.messageBox("��" + (i + 1) + "�У�����Ϊ " + patName + " ����Ա�ײ�Ϊ�գ�");
                } else this.messageBox("��" + (i + 1) + "�У��ײ�Ϊ�գ�");
                return;
            }
            if (patName.equals("")) {
                if (!idNo.equals("")) {
                    this.messageBox("��" + (i + 1) + "�У����֤Ϊ " + idNo + " ����Ա����Ϊ�գ�");
                } else this.messageBox("��" + (i + 1) + "�У���Ա����Ϊ�գ�");
                return;
            }
        }
        // ����
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
                        this.messageBox("��" + (singleIdRow.get(idNo) + 1) + "��(��ţ�"
                                + singleIdSeq.get(idNo) + ")���" + (i + 1) + "��(��ţ�" + seqNo
                                + ")����Ա���֤����ͬ");
                    } else {
                        this.messageBox("��" + (singleMrRow.get(idNo) + 1) + "��(��ţ�"
                                + singleMrSeq.get(mrNo) + ")���" + (i + 1) + "��(��ţ�" + seqNo
                                + ")����Ա��������ͬ");
                    }
                    return;
                } else {
                    singleIdRow.put(idNo, i);
                    singleIdSeq.put(idNo, seqNo);
                }
            }
        }
        /** ����������Ϣ[��˾��ǩ]SQL */
        String[] sql = onSaveCom();
        if (sql == null) {
            sql = new String[]{};
        }
        // ȡ�ú�ͬ��ϸ�ౣ��SQL������֤
        String[] tempSql = onSaveContractM();
        if (tempSql == null) {
            tempSql = new String[]{};
        }
        sql = StringTool.copyArray(sql, tempSql);
        if (sql == null) {
            this.messageBox_("û������");
            return;
        }
        if (sql.length < 1) {
            this.messageBox_("û������");
            return;
        }
        // sql = StringTool.copyArray(sql, updateAdmSql);//modify by wanglong 20130308
//        for (int i = 0; i < sql.length; i++) {
//            System.out.println("------onSave----sql["+i+"]-----"+sql[i]);
//        }
        // ���涯����β���֤���
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
     * ���TABLE
     */
    public void onSetDsValue() {
        comTab.setDSValue();
        conTab.setDSValue();
        mrTab.setDSValue();
    }

    /**
     * �����¼�(������Ϣ)[��˾��ǩ]
     */
    private String[] onSaveCom() {
        String[] sql = new String[]{};
        // System.out.println("selectrow========"+comTab.getSelectedRow());
        if (comTab.getSelectedRow() < 0) {
            String companyDesc = this.getValueString("COMPANY_DESC");
            if (StringUtil.isNullString(companyDesc)) {
                this.messageBox_("�������Ʋ���Ϊ��");
                return null;
            }
            if (company.isHaveSaveDesc(companyDesc) && !isChangeContract) {
                this.messageBox_("�������Ѿ�����");
                return null;
            }
            TParm parmTag =
                    this.getParmForTag("COMPANY_DESC;PY1;PY2;DESCRIPTION;ADMINISTRATOR;TEL;IND_TYPE_CODE;FAX_NO;CONTACTS_NAME;CONTACTS_TEL;POST_CODE;ADDRESS;E_MAIL");
            parmTag.setData("ROLE_TYPE", roleType);
            company.newCompany(parmTag);
        } else {
            int row = comTab.getSelectedRow();
            if (StringUtil.isNullString(this.getValueString("COMPANY_DESC"))) {
                this.messageBox_("�������Ʋ���Ϊ��");
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
     * �����ͬ��Ϣ
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
        ArrayList<String> sqlList = new ArrayList<String>();//add by wanglong 20130608 ����SYS_PatInfo������ͬ��
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
        sql = StringTool.copyArray(sql, contractD.getSql());///////////////////////////���л�����SYS_PATINFO��
        //contractD.getSql()ִ�к�ὫDataStore״̬��գ�֮����ʹ��getNewRows()��getModifiedRows()��׼ȷ
        sql = StringTool.copyArray(sql, contractD.getUpdateSQL());
        sql = StringTool.copyArray(sql, (String[]) sqlList.toArray(new String[0]));//add by wanglong 20130608 ����SYS_PatInfo������ͬ��
        if (sql == null || sql.length < 1) {
            return null;
        }
        return sql;
    }

    /**
     * ����¼�
     */
    public void onClear() {
        company.onQuery();
        comTab.setDSValue();
        comTab.clearSelection();
        
        // add by wangb 2016/6/21 START
        // ���ݲ�ͬ���ɸѡ��ͬ����(H_�������,PIC_һ���ٴ�,SYSDBA_���Ȩ��)
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
     * ҳǩ����¼�,���û��ѡ��ĳ���壬������ʾ��ͬ��ϸ����Ϣ.
     */
    public void onTabChanged() {
        TTabbedPane tabbedPane = (TTabbedPane) this.getComponent("TAB_PANEL");
        if (tabbedPane.getSelectedIndex() == 0) {
            return;
        }
        if (comTab.getSelectedRow() < 0) {
            this.messageBox_("û��������Ϣ");
            tabbedPane.setSelectedIndex(0);
            return;
        }
        int row = comTab.getSelectedRow();
        String comCode = company.getItemString(row, "COMPANY_CODE");
        if (StringUtil.isNullString(comCode)) {
            this.messageBox_("ȡ��������Ϣʧ��");
            return;
        }
        contractM.setCompanyDesc(company.getItemString(row, "COMPANY_DESC"));
        contractM.filt(comCode);
        if (!StringUtil.isNullString(contractM.getItemString(contractM.rowCount() - 1,
                                                             "CONTRACT_DESC"))
                || contractM.rowCount() < 1) {
            // add by wangb 2016/06/22 ���ý�ɫ����
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
        
        // add by wangb 2016/07/04 һ���ٴ���¼�����������
        if (this.getPopedem("SYSDBA") || this.getPopedem("PIC")) {
        	// ѡ�е�������Ϣ
        	String selRoleType = company.getItemString(row, "ROLE_TYPE");
        	String mrTabHeader = mrTab.getHeader();
        	roleType = selRoleType;
			if ("PIC".equals(selRoleType)) {
				mrTab.setHeader(mrTabHeader.replace("����", "����").replace("����",
						"ɸѡ��").replace("PACKAGE_H", "PACKAGE_PIC"));
				// modify by wangb 2016/08/31 ��ֹ�����л�Tabҳ��ɶ��ƴ����
				if (!mrTab.getHeader().contains("�������")) {
					mrTab.setHeader(mrTab.getHeader() + ";�������,120;��������,200");
					mrTab.setParmMap(mrTab.getParmMap() + ";PLAN_NO;PLAN_DESC");
				}
			} else {
				mrTab.setHeader(mrTabHeader.replace("����", "����").replace("ɸѡ��",
						"����").replace("PACKAGE_PIC", "PACKAGE_H").replace(
						";�������,120;��������,200", ""));
				mrTab.setParmMap(mrTab.getParmMap().replace(
						";PLAN_NO;PLAN_DESC", ""));
			}
        }
    }

    /**
     * ͨ��excel����Ա����Ϣ
     * 1)excel��һ��Ϊ��ͷ��Ӧ��������ţ����������֤�ţ��Ա��ײʹ��룬���ע�ǣ��������ڣ����ţ��������壬�绰���ʱ࣬��ַ��Ԥ��ʱ�䣬����״̬
     * 2)���е�˳��ɱ�
     * 3)������Ϣ�������excel�ĵ�һ��sheetҳ��
     */
    public void onInsertPatByExl() {// refactor by wanglong 20130116
        conTab.acceptText();
        if (conTab == null) {
            this.messageBox_("û�к�ͬ��Ϣ");
            return;
        }
        int clickRow = conTab.getSelectedRow();
        if (clickRow < 0) {
            this.messageBox_("û�к�ͬ��Ϣ");
            return;
        }
        if (conTab.getItemString(clickRow, "CONTRACT_DESC").trim().equals("")) {
            this.messageBox_("û��ѡ���ͬ");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new FileFilter() {// ����xls�ļ� add by wanglong20130116

                    public boolean accept(File f) {
                        if (f.isDirectory()) {// �����ļ���
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
        String discnt = conTab.getItemString(conTab.getSelectedRow(), "DISCNT");// ��ͬ�ۿ���
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Workbook wb = Workbook.getWorkbook(file);
                Sheet st = wb.getSheet(0);
//                int row = st.getRows();
                int row = getRightRows(st);
                int column = st.getColumns();
                if (row <= 1 || column <= 0) {
                    this.messageBox("excel��û������");
                    return;
                }
                StringBuffer wrongMsg = new StringBuffer();
                // wrongMsg.append("excel�ļ���Ϊ��" + file.getName() + "\r\n");
                ArrayList<Integer> indexList = new ArrayList<Integer>();
                ArrayList<String> titleList = new ArrayList<String>();
                for (int j = 0; j < column; j++) {
                    String cell = st.getCell(j, 0).getContents();
                    if (cell.indexOf("�ײʹ���") != -1) {
                        indexList.add(j);
                        titleList.add("PACKAGE_CODE");
                        continue;
                    }
                    if (cell.indexOf("���") != -1) {
                        indexList.add(j);
                        titleList.add("SEQ_NO");
                        continue;
                    }
                    if (cell.indexOf("����") != -1) {
                        indexList.add(j);
                        titleList.add("STAFF_NO");
                        continue;
                    }
                    if (cell.indexOf("����") != -1) {
                        indexList.add(j);
                        titleList.add("PAT_NAME");
                        continue;
                    }
                    if (cell.indexOf("�Ա�") != -1) {
                        indexList.add(j);
                        titleList.add("SEX_CODE");
                        continue;
                    }
                    if (cell.indexOf("���֤��") != -1) {
                        indexList.add(j);
                        titleList.add("IDNO");
                        continue;
                    }
                    if (cell.indexOf("����״̬") != -1) {
                        indexList.add(j);
                        titleList.add("MARRIAGE_CODE");
                        continue;
                    }
                    if (cell.indexOf("�����ע��") != -1) {
                        indexList.add(j);
                        titleList.add("FOREIGNER_FLG");
                        continue;
                    }
                    if (cell.indexOf("��������") != -1) {
                        indexList.add(j);
                        titleList.add("BIRTHDAY");
                        continue;
                    }
                    if (cell.indexOf("��������֧��") != -1) {
                        indexList.add(j);
                        titleList.add("COMPANY_PAY_FLG");
                        continue;
                    }
                    if (cell.indexOf("�绰") != -1) {
                        indexList.add(j);
                        titleList.add("TEL");
                        continue;
                    }
                    if (cell.indexOf("�ʱ�") != -1) {
                        indexList.add(j);
                        titleList.add("POST_CODE");
                        continue;
                    }
                    if (cell.indexOf("��ַ") != -1) {
                        indexList.add(j);
                        titleList.add("ADDRESS");
                        continue;
                    }
                    if (cell.indexOf("Ԥ��ʱ��") != -1) {
                        indexList.add(j);
                        titleList.add("PRE_CHK_DATE");
                        continue;
                    }
                    if (cell.indexOf("����") != -1) {// add by wanglong 20130225
                        indexList.add(j);
                        titleList.add("PAT_DEPT");
                        continue;
                    }
                    if (cell.indexOf("������") != -1) {// wanglong add 20140924
                        indexList.add(j);
                        titleList.add("MR_NO");
                        continue;
                    }
                    if (cell.indexOf("���") != -1) {// huangjw add 20160920
                        indexList.add(j);
                        titleList.add("IS_VIP");
                        continue;
                    }
                }
                column = indexList.size();
                if (!titleList.contains("PACKAGE_CODE")) {// �ײʹ���
                    this.messageBox("ȱ�١��ײʹ��롱�У�����Ϊ�����");
                    return;
                }
                if (!titleList.contains("SEQ_NO")) {// ���
                    this.messageBox("ȱ�١���š��У����Ǳ����");
//                    return;
                }
                if (!titleList.contains("STAFF_NO")) {// ����
                    this.messageBox("ȱ�١����š��У����Ǳ����");
//                    return;
                }
                if (!titleList.contains("PAT_NAME")) {// ����
                    this.messageBox("ȱ�١��������У�����Ϊ�����");
                    return;
                }
                if (!titleList.contains("SEX_CODE")) {// �Ա�
                    this.messageBox("ȱ�١��Ա��У�����Ϊ�����");
                    return;
                }
                if (!titleList.contains("IDNO")) {// ���֤��
                    this.messageBox("ȱ�١����֤�š��У���Ĭ�ϱ�����ֵ��������ڡ������ע�ǡ���дΪY֮�󣬴���ſɿ�ȱ��");
                    return;
                }
                if (!titleList.contains("MARRIAGE_CODE")) {// ����״̬
                    this.messageBox("ȱ�١�����״̬���У�����Чֵֻ����д�����ı��룬���߲���д��");
                    return;
                }
                if (!titleList.contains("FOREIGNER_FLG")) {// �����ע��
                    this.messageBox("ȱ�١������ע�ǡ��У�����Чֵֻ����дY��N�����߲���д������N����");
//                    return;
                }
                if (!titleList.contains("BIRTHDAY")) {// ��������
                    this.messageBox("ȱ�١��������ڡ��У�����Чֵ����ʽΪyyyyMMdd����������ˣ�������д��");
//                    return;
                }
                if (!titleList.contains("COMPANY_PAY_FLG")) {// ��������֧��
                    this.messageBox("ȱ�١���������֧�����У�����Чֵֻ����дY����N�����߲���д������N����");
//                    return;
                }
                if (!titleList.contains("TEL")) {// �绰
                    this.messageBox("ȱ�١��绰���У����Ǳ����");
//                    return;
                }
                if (!titleList.contains("POST_CODE")) {// �ʱ�
                    this.messageBox("ȱ�١��ʱࡱ�У����Ǳ����");
//                    return;
                }
                if (!titleList.contains("ADDRESS")) {// ��ַ
                    this.messageBox("ȱ�١���ַ���У����Ǳ����");
//                    return;
                }
                if (!titleList.contains("PRE_CHK_DATE")) {// Ԥ��ʱ��
                    this.messageBox("ȱ�١�Ԥ��ʱ�䡱�У���Чֵ����ʽΪyyyyMMdd�����߲���д��");
//                    return;
                }
                if (!titleList.contains("PAT_DEPT")) {// ���� add by wanglong 20130225
                    this.messageBox("ȱ�١����š��У����Ǳ����");
//                    return;
                }
                if (!titleList.contains("MR_NO")) {// ������ wanglong add 20140924
                    this.messageBox("ȱ�١������š��У����Ǳ����");
//                    return;
                }
                if (!titleList.contains("IS_VIP")) {// ��� huangjw add 20160920
                    this.messageBox("ȱ�١���ݡ��У����Ǳ����");
//                    return;
                }
                String sexSql = "SELECT ID FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX'";
                TParm sexParm = new TParm(TJDODBTool.getInstance().select(sexSql));
                if (sexParm.getErrCode() != 0 || sexParm.getCount() < 1) {
                    this.messageBox("��ȡϵͳ���Ա���Ϣʱ����");
                    return;
                }
                List<String> sexList = Arrays.asList(sexParm.getStringArray("ID"));
                String packageSql = "SELECT PACKAGE_CODE FROM HRM_PACKAGEM WHERE ACTIVE_FLG = 'Y'";
                TParm packageParm = new TParm(TJDODBTool.getInstance().select(packageSql));
                if (packageParm.getErrCode() != 0 || packageParm.getCount() < 1) {
                    this.messageBox("��ȡϵͳ���ײ���Ϣʱ����");
                    return;
                }
                List<String> packageList = Arrays.asList(packageParm.getStringArray("PACKAGE_CODE"));
                String marriageSql =
                        "SELECT ID FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_MARRIAGE'";
                TParm marriageParm = new TParm(TJDODBTool.getInstance().select(marriageSql));
                if (marriageParm.getErrCode() != 0 || marriageParm.getCount() < 1) {
                    this.messageBox("��ȡϵͳ�л���״̬��Ϣʱ����");
                    return;
                }
                List<String> marriageList = Arrays.asList(marriageParm.getStringArray("ID"));
                int count = 0;
                for (int i = 1; i < row; i++) {// һ��һ�м���excel�е�����
                    for (int j = 0; j < column; j++) {// ÿ�ε���һ�е�������
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
                    parm.addData("DISCNT", discnt);// �ۿ�
                    count = parm.getCount("PAT_NAME");
                    if (parm.getData("SEX_CODE", count - 1).equals("��")) {//wanglong add 20140512
                        parm.setData("SEX_CODE", count - 1, "1");
                    } else if (parm.getData("SEX_CODE", count - 1).equals("Ů")) {
                        parm.setData("SEX_CODE", count - 1, "2");
                    }
                    if (parm.getData("MARRIAGE_CODE", count - 1).equals("δ��")) {// wanglong add 20140528
                        parm.setData("MARRIAGE_CODE", count - 1, "1");
                    } else if (parm.getData("MARRIAGE_CODE", count - 1).equals("�ѻ�")) {
                        parm.setData("MARRIAGE_CODE", count - 1, "2");
                    } else if (parm.getData("MARRIAGE_CODE", count - 1).equals("���")) {
                        parm.setData("MARRIAGE_CODE", count - 1, "3");
                    } else if (parm.getData("MARRIAGE_CODE", count - 1).equals("ɥż")) {
                        parm.setData("MARRIAGE_CODE", count - 1, "4");
                    } else {
                        parm.setData("MARRIAGE_CODE", count - 1, "");
                    }
                    
                    if (parm.getData("IS_VIP", count - 1).equals("��ͨ")) {// huangjw add 20160920
                        parm.setData("IS_VIP", count - 1, "1");
                    } else if (parm.getData("IS_VIP", count - 1).equals("VIP")) {
                        parm.setData("IS_VIP", count - 1, "2");
                    }
                    parm.setCount(count);
                    // ===================���ݼ�鿪ʼ
                    String packageCode = parm.getValue("PACKAGE_CODE", count - 1);// �ײʹ���
                    if (packageCode.equals("")) {
                        wrongMsg.append("excel��" + (i + 1) + "��,���ײʹ��롱��ȱ��ֵ,�����ᱻ����\r\n");
                        this.messageBox("excel��" + (i + 1) + "��,���ײʹ��롱��ȱ��ֵ�������ᱻ����");
                        count--;
                        parm.removeRow(count);
                        parm.setCount(count);
                        continue;
                    }
                    if (!packageList.contains(packageCode)) {
                        wrongMsg.append("excel��" + (i + 1) + "��,���ײʹ��롱�е�ֵ(ֵ��" + packageCode
                                + ")��ϵͳ�в�����,���ᱻ����\r\n");
                        this.messageBox("excel��" + (i + 1) + "��,���ײʹ��롱�е�ֵ��ϵͳ�в����ڣ������ᱻ����");
                        count--;
                        parm.removeRow(count);
                        parm.setCount(count);
                        continue;
                    }
                    String patName = parm.getValue("PAT_NAME", count - 1);// ����
                    String mrNo = parm.getValue("MR_NO", count - 1);// ������ wanglong add 20140924
                    if (patName.equals("")&&mrNo.equals("")) {
                        wrongMsg.append("excel��" + (i + 1) + "��,����������ȱ��ֵ,���ᱻ����\r\n");
                        this.messageBox("excel��" + (i + 1) + "��,����������ȱ��ֵ�������ᱻ����");
                        count--;
                        parm.removeRow(count);
                        parm.setCount(count);
                        continue;
                    }
                    String idNo = parm.getValue("IDNO", count - 1).toUpperCase();
                    String sexCode = parm.getValue("SEX_CODE", count - 1);
                    String birthday = parm.getValue("BIRTHDAY", count - 1);
                    String foreignerFlg = parm.getValue("FOREIGNER_FLG", count - 1);
                    // �й��ˣ�18λ���֤У��λ������ʱ���Զ�������
                    String checkid = checkID(idNo);
                    if (!foreignerFlg.equalsIgnoreCase("Y") && !checkid.equals("TRUE")
                            && !checkid.equals("FALSE")) {// add by wanglong 20130409
                        wrongMsg.append("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                + "������Ա�������֤��У��λ����(ֵ��" + idNo + "),��ȷֵ:"
                                + checkid.substring(17, 18) + "\r\n");
                        this.messageBox("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                + "������Ա�������֤��У��λ���󣡣���ȷֵ��" + checkid.substring(17, 18) + "��");// modify by wanglong 20130416
                        parm.setData("IDNO", count - 1, idNo);// modify by wanglong 20130416
                        String idSexCode = StringTool.isMaleFromID(checkid);// add by wanglong
                                                                            // 20130726
                        if (!idSexCode.equals(sexCode)) {// add by wanglong 20130726
                            if (this.messageBox("����", "excel��" + (i + 1) + "��,����Ϊ��" + patName
                                    + "������Ա�Ա��������֤�ϵ��Ա𲻷�\n�Ƿ��Ա�ĳɺ����֤��һ��", 2) == 0) {
                                parm.setData("SEX_CODE", count - 1, idSexCode);// ���Ա��Ϊ�����֤�ϵ���Ϣһ��
                                wrongMsg.append("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                        + "������Ա�Ա��������֤�ϵ��Ա𲻷�\r\n");
                            } else if (!sexCode.matches("[12349]")) {// add by wanglong 20130830
                                this.messageBox("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                        + "������Ա�Ա���ֵ�����벻Ҫֱ��ʹ�����ĵȷǷ��ַ���Ϊ��ʶ����ʱ������Ϊ�����֤һ��");
                                parm.setData("SEX_CODE", count - 1, idSexCode);// ���Ա��Ϊ�����֤�ϵ���Ϣһ��
                                wrongMsg.append("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                        + "������Ա�Ա�ʹ���˷Ƿ��ַ���Ϊ��ʶ\r\n");
                            }
                        }
                        parm.setData("BIRTHDAY", count - 1, StringTool.getBirdayFromID(checkid));
                    }
                    // �й��ˣ����֤��ȫ����
                    else if (!foreignerFlg.equalsIgnoreCase("Y") && checkid.equals("FALSE")) {// modify by wanglong 20130409
                        wrongMsg.append("excel��" + (i + 1) + "��,����Ϊ��" + patName + "������Ա���֤�Ŵ���(ֵ��"
                                + idNo + "),������(��)�������\r\n");
                        this.messageBox("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                + "������Ա���֤�Ŵ��󣡻�����(��)�������");
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
                                                            temp);// ���֤��д
                        }
                        parm.setData("IDNO", count - 1, reParm.getValue("IDNO").toUpperCase());
                        parm.setData("FOREIGNER_FLG", count - 1, reParm.getData("FOREIGNER_FLG"));
                        parm.setData("SEX_CODE", count - 1, reParm.getData("SEX_CODE"));
                        parm.setData("BIRTHDAY", count - 1, reParm.getData("BIRTHDAY"));
                    }
                    // �й��ˣ������֤��ȷ��
                    else if (!foreignerFlg.equalsIgnoreCase("Y") && isId(idNo)) {
                        parm.setData("FOREIGNER_FLG", count - 1, "N");
                        String idSexCode = StringTool.isMaleFromID(idNo);// add by wanglong 20130726
                        if (!idSexCode.equals(sexCode)) {// add by wanglong 20130726
                            if (this.messageBox("����", "excel��" + (i + 1) + "��,����Ϊ��" + patName
                                    + "������Ա�Ա��������֤�ϵ��Ա𲻷�\n�Ƿ��Ա�ĳɺ����֤��һ��", 2) == 0) {
                                parm.setData("SEX_CODE", count - 1, idSexCode);// ���Ա��Ϊ�����֤�ϵ���Ϣһ��
                                wrongMsg.append("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                        + "������Ա�Ա��������֤�ϵ��Ա𲻷�\r\n");
                            } else if (!sexCode.matches("[12349]")) {//add by wanglong 20130830
                                this.messageBox("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                                + "������Ա�Ա���ֵ�����벻Ҫֱ��ʹ�����ĵȷǷ��ַ���Ϊ��ʶ����ʱ������Ϊ�����֤һ��");
                                parm.setData("SEX_CODE", count - 1, idSexCode);// ���Ա��Ϊ�����֤�ϵ���Ϣһ��
                                wrongMsg.append("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                        + "������Ա�Ա�ʹ���˷Ƿ��ַ���Ϊ��ʶ\r\n");
                            }
                        }
                        parm.setData("BIRTHDAY", count - 1, StringTool.getBirdayFromID(idNo));// ���������ڸ�Ϊ�����֤�ϵ���Ϣһ��
                    }
                    // �����
                    else if (foreignerFlg.equalsIgnoreCase("Y")) {
                        parm.setData("FOREIGNER_FLG", count - 1, "Y");
                        if (!sexList.contains(sexCode)) {
                            wrongMsg.append("excel��" + (i + 1) + "��,����Ϊ��" + patName + "������Ա�Ա����(ֵ��"
                                    + sexCode + ")\r\n");
                            this.messageBox("excel��" + (i + 1) + "��,����Ϊ��" + patName + "������Ա�Ա����");
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
                                                            temp);// ���֤��д
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
                            wrongMsg.append("excel��" + (i + 1) + "��,����Ϊ��" + patName
                                    + "������Ա�������ڴ���(ֵ��" + birthday + ")\r\n");
                            this.messageBox("excel��" + (i + 1) + "��,����Ϊ��" + patName + "������Ա�������ڴ���");
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
                                                            temp);// ���֤��д
                            }
                            parm.setData("IDNO", count - 1, reParm.getValue("IDNO").toUpperCase());
                            parm.setData("FOREIGNER_FLG", count - 1,
                                         reParm.getData("FOREIGNER_FLG"));
                            parm.setData("SEX_CODE", count - 1, reParm.getData("SEX_CODE"));
                            parm.setData("BIRTHDAY", count - 1, reParm.getData("BIRTHDAY"));
                        }
                    }
                    if (parm.getValue("COMPANY_PAY_FLG", count - 1).equalsIgnoreCase("y")) {// ��������֧��
                        parm.setData("COMPANY_PAY_FLG", count - 1, "Y");
                    } else if (!parm.getValue("COMPANY_PAY_FLG", count - 1).equalsIgnoreCase("Y")) {
                        parm.setData("COMPANY_PAY_FLG", count - 1, "N");
                    }
                    String preChkDate = parm.getValue("PRE_CHK_DATE", count - 1);// Ԥ��ʱ��
                    if (preChkDate.equals("")) {
                        wrongMsg.append("excel��" + (i + 1) + "��,��Ԥ��ʱ�䡱��ȱ��ֵ,������Ϊ��\r\n");
                        // this.messageBox("excel��" + (i + 1) + "��,��Ԥ��ʱ�䡱��ȱ��ֵ��������Ϊ��");
                        parm.setData("PRE_CHK_DATE", count - 1, "");
                    } else if (!DateTool.checkDate(preChkDate, "yyyyMMdd")) {
                        wrongMsg.append("excel��" + (i + 1) + "��,��Ԥ��ʱ�䡱�е�ֵ(ֵ��" + preChkDate
                                + ")����,������Ϊ��\r\n");
                        // this.messageBox("excel��" + (i + 1) + "��,��Ԥ��ʱ�䡱�е�ֵ���󣡽�����Ϊ��");
                        parm.setData("PRE_CHK_DATE", count - 1, "");
                    }
                    String marriageCode = parm.getValue("MARRIAGE_CODE", count - 1);// ����״̬
                    if (marriageCode.equals("")) {// add by wanglong 20130502
                        wrongMsg.append("excel��" + (i + 1) + "��,������״̬����ȱ��ֵ,������Ϊ��\r\n");
                        // this.messageBox("excel��" + (i + 1) + "��,������״̬���е�ֵ��ϵͳ�в����ڣ�������Ϊ��");
                        parm.setData("MARRIAGE_CODE", count - 1, "");
                    } else if (!marriageList.contains(marriageCode)) {
                        wrongMsg.append("excel��" + (i + 1) + "��,������״̬���е�ֵ(ֵ��" + marriageCode
                                + ")��ϵͳ�в�����,������Ϊ��\r\n");
                        // this.messageBox("excel��" + (i + 1) + "��,������״̬���е�ֵ��ϵͳ�в����ڣ�������Ϊ��");
                        parm.setData("MARRIAGE_CODE", count - 1, "");
                    }
                    
                    String isVip = parm.getValue("IS_VIP", count - 1);// ���
                    if ("".equals(isVip)) {// add by huangjw 20160920
                        wrongMsg.append("excel��" + (i + 1) + "��,����ݡ���ȱ��ֵ,������Ϊ��\r\n");
                        parm.setData("IS_VIP", count - 1, "");
                    } else if (!"1".equals(isVip) && !"2".equals(isVip)) {
                        wrongMsg.append("excel��" + (i + 1) + "��,����ݡ��е�ֵ(ֵ��" + isVip
                                + ")��ϵͳ�в�����,������Ϊ��\r\n");
                        parm.setData("IS_VIP", count - 1, "");
                    }
                    // ===================���ݼ�����
                }
                String msg = wrongMsg.toString();
                if (!StringUtil.isNullString(msg)) {// modify by wanglong 20130502
                // String dir = TConfig.getSystemValue("UDD_DISBATCH_LocalPath") + "\\";
                    String fileName = "��������������־��" + file.getName() + "��" + ".txt";
                    // this.messageBox_("��Ա��Ϣ��������,������Ϣ���"+dir+fileName);
                    javax.swing.filechooser.FileSystemView fsv =
                            javax.swing.filechooser.FileSystemView.getFileSystemView();
                    FileTool.setString(fsv.getHomeDirectory() + "\\" + fileName, msg);
                    // FileTool.setString(dir + fileName, msg);
                }
                parm.setCount(count);
                if (count < 1) {
                    this.messageBox_("��Ч��������һ�У����������ֹ");
                    return;
                }
                // setColorTable();
            }
            catch (BiffException e) {
                this.messageBox_("excel�ļ���������");
                e.printStackTrace();
                return;
            }
            catch (IOException e) {
                this.messageBox_("���ļ�����");
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
            // =========wanglong add 20140924 ���Ӳ������У����Բ�����ֱ�Ӵ���������Ϣ
            String mrNo = parmRow.getValue("MR_NO");
            if (!mrNo.equals("")) {
                mrNo = PatTool.getInstance().checkMrno(mrNo);
                TParm mrParm = PatTool.getInstance().getInfoForMrno(mrNo);
                if (mrParm.getCount() == 0) {
                    Pat pat = Pat.onQueryByMrNo(mrNo);
                    if (pat != null) {// wanglong add 20150423
                        this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
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
                        contractD.setItem(row, "DISCNT", parmRow.getDouble("DISCNT"));// �ۿ�
                        if (!pat.getCellPhone().trim().equals("")
                                && pat.getCellPhone().trim().matches("[^\u4e00-\u9fa5]*")) {// �ֻ���(��������)
                            contractD.setItem(row, "TEL", pat.getCellPhone());
                        } else if (pat.getCellPhone().trim().length() == 11
                                && pat.getCellPhone().trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
                            contractD.setItem(row, "TEL", pat.getCellPhone());
                        } else {
                            contractD.setItem(row, "TEL", parmRow.getValue("TEL"));
                        }
                        contractD.setItem(row, "SEQ_NO", seqNo);
                        contractD.setItem(row, "MARRIAGE_CODE", parmRow.getValue("MARRIAGE_CODE")); // ����״̬
                        contractD.setItem(row, "IS_VIP", parmRow.getValue("IS_VIP")); // ��� add by huangjw 20160920
                        contractD.setItem(row, "PAT_DEPT", parmRow.getValue("PAT_DEPT")); // ����
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
                            this.messageBox("������Ϊ" + mrNo + "����Ա��ϵͳ�в����ڣ������ᱻ����");
                        } else {
//                            this.messageBox("������Ϊ" + mrNo + "������Ϊ " + patName + " ����Ա��ϵͳ�в����ڣ������ᱻ����");
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
                                this.messageBox("������Ϊ" + mrNo + "����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                            } else {
                                this.messageBox("������Ϊ" + mrNo + "������Ϊ " + patName
                                        + " ����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                            }
                            continue patLoop;
                        }
                        if(contractD.getItemString(j, "MR_NO").equals(pat.getMrNo())){// wanglong add 20150423
                            this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
                            mrNo = pat.getMrNo();
                            this.messageBox("������Ϊ" + mrNo + "����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                            continue patLoop;
                        }
                    }
                    if (!mrNo.equals(pat.getMrNo())) {// wanglong add 20150423
                        this.messageBox("������" + mrNo + " �Ѻϲ��� " + pat.getMrNo());
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
                        contractD.setItem(row, "DISCNT", parmRow.getDouble("DISCNT"));// �ۿ�
                        if (!pat.getCellPhone().trim().equals("")
                                && pat.getCellPhone().trim().matches("[^\u4e00-\u9fa5]*")) {// �ֻ���(��������)
                            contractD.setItem(row, "TEL", pat.getCellPhone());
                        } else if (pat.getCellPhone().trim().length() == 11
                                && pat.getCellPhone().trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
                            contractD.setItem(row, "TEL", pat.getCellPhone());
                        } else {
                            contractD.setItem(row, "TEL", parmRow.getValue("TEL"));
                        }
                        contractD.setItem(row, "SEQ_NO", seqNo);
                        contractD.setItem(row, "MARRIAGE_CODE", parmRow.getValue("MARRIAGE_CODE")); // ����״̬
                        contractD.setItem(row, "IS_VIP", parmRow.getValue("IS_VIP")); // ��� add by huangjw 20160920
                        contractD.setItem(row, "PAT_DEPT", parmRow.getValue("PAT_DEPT")); // ����
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
                        contractD.setItem(row, "DISCNT", parmRow.getDouble("DISCNT"));// �ۿ�
                        if (!mrParm.getValue("CELL_PHONE", 0).trim().equals("")
                                && mrParm.getValue("CELL_PHONE", 0).trim()
                                        .matches("[^\u4e00-\u9fa5]*")) {// �ֻ���(��������)
                            contractD.setItem(row, "TEL", mrParm.getValue("CELL_PHONE", 0));
                        } else if (mrParm.getValue("TEL_HOME", 0).trim().length() == 11
                                && mrParm.getValue("TEL_HOME", 0).trim()
                                        .matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
                            contractD.setItem(row, "TEL", mrParm.getValue("TEL_HOME", 0));
                        } else {
                            contractD.setItem(row, "TEL", parmRow.getValue("TEL"));
                        }
                        contractD.setItem(row, "SEQ_NO", seqNo);
                        contractD.setItem(row, "MARRIAGE_CODE", parmRow.getValue("MARRIAGE_CODE")); // ����״̬
                        contractD.setItem(row, "IS_VIP", parmRow.getValue("IS_VIP")); //��� add by huangjw 20160920
                        contractD.setItem(row, "PAT_DEPT", parmRow.getValue("PAT_DEPT")); // ����
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
                            + "ORDER BY REPORT_DATE DESC";// �������֤�Ų鲡����
            patSql = patSql.replaceAll("#", idNo);
            TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));
            if (patParm.getErrCode() != 0) {
                // System.out.println("=======================patParm============" + patParm);
                this.messageBox("��ѯ��Ա������ʱ����");
                return;
            }
            // ��һ�ξ���
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
                                + "ORDER BY OPT_DATE DESC NULLS LAST";// ��ͬ��ͬ��
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
                        this.messageBox("��ѯ��Ա���ξ�����Ϣʱ����");
                        return;
                    }
                    if (result.getCount() < 1) {
                        contractD.setItem(row, "MR_NO", "Y");
                    } else if (result.getCount() == 1) {// add by wanglong 20130409
                        if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + patName + "��(���" + seqNo
                                + ")ͬ��ͬ����Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {// add by wanglong 20130726
                            Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
                            if (obj != null) {
                                TParm samePatParm = (TParm) obj;
                                int rowCount = contractD.rowCount();
                                for (int j = 0; j < rowCount; j++) {// wanglong add 20140606
                                    if (!contractD.isActive(j)) continue;
                                    if (contractD.getItemString(j, "MR_NO")
                                            .equals(samePatParm.getValue("MR_NO"))) {
                                        if (patName.equals("")) {
                                            this.messageBox("������Ϊ" + samePatParm.getValue("MR_NO")
                                                    + "����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                                        } else {
                                            this.messageBox("������Ϊ" + samePatParm.getValue("MR_NO")
                                                    + "������Ϊ " + samePatParm.getValue("PAT_NAME")
                                                    + " ����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
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
                                    this.messageBox("��Ա��" + patName + "��(���" + seqNo
                                            + ")�Ӳ�����¼�д��ص��Ա���excel������Ա�ͬ\n"
                                            + "���������Ա�ڡ�������Ϣ���е���Ϣ���ڡ�����������Ϣ�趨���е���Ϣ�Ƿ�һ��");
                                }
//                                contractD.setItem(row, "MR_NO", result.getData("MR_NO", 0));
                                if (parmRow.getValue("TEL").equals("")
                                        && !samePatParm.getValue("CELL_PHONE").equals("")
                                        && samePatParm.getValue("CELL_PHONE")
                                                .matches("[^\u4e00-\u9fa5]*")) {// wanglong add
                                                                                // 20140522 �Զ�ȡ�绰����
                                    parmRow.setData("TEL", samePatParm.getValue("CELL_PHONE"));
                                } else if (parmRow.getValue("TEL").equals("")
                                        && samePatParm.getValue("TEL_HOME").trim().length() == 11
                                        && samePatParm.getValue("TEL_HOME").trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
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
                                                .matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 �Զ�ȡ�绰����
                                    parmRow.setData("TEL", result.getValue("CELL_PHONE",j));
                                } else if (parmRow.getValue("TEL").equals("")
                                        && result.getValue("TEL_HOME", j).trim().length() == 11
                                        && result.getValue("TEL_HOME", j).trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
                                    parmRow.setData("TEL", result.getValue("TEL_HOME", j));
                                }
                                break;
                            }
                        }
                        if (j == result.getCount()) {
                            if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + patName + "��(���" + seqNo
                                    + ")ͬ��ͬ����Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {
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
                                                this.messageBox("������Ϊ"
                                                        + samePatParm.getValue("MR_NO")
                                                        + "����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                                            } else {
                                                this.messageBox("������Ϊ"
                                                        + samePatParm.getValue("MR_NO") + "������Ϊ "
                                                        + samePatParm.getValue("PAT_NAME")
                                                        + " ����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
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
                                        this.messageBox("��Ա��" + patName + "��(���" + seqNo
                                                + ")�Ӳ�����¼�д��ص��Ա���excel������Ա�ͬ\n"
                                                + "���������Ա�ڡ�������Ϣ���е���Ϣ���ڡ�����������Ϣ�趨���е���Ϣ�Ƿ�һ��");
                                    }
                                    if (parmRow.getValue("TEL").equals("")
                                            && !samePatParm.getValue("CELL_PHONE").equals("")
                                            && samePatParm.getValue("CELL_PHONE")
                                                    .matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 �Զ�ȡ�绰����
                                        parmRow.setData("TEL", samePatParm.getValue("CELL_PHONE"));
                                    } else if (parmRow.getValue("TEL").equals("")
                                            && samePatParm.getValue("TEL_HOME").trim().length() == 11
                                            && samePatParm.getValue("TEL_HOME").trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
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
            // ���ھ�����Ϣ
            else if (patParm.getCount("MR_NO") == 1) {
                if (!patParm.getValue("PAT_NAME", 0).equals(patName)) {
                    if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + patName + "��(���" + seqNo
                            + ")���֤��ͬ������������ͬ�Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {
                        Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", patParm);
                        if (obj != null) {
                            TParm samePatParm = (TParm) obj;
                            int rowCount = contractD.rowCount();
                            for (int j = 0; j < rowCount; j++) {// wanglong add 20140606
                                if (!contractD.isActive(j)) continue;
                                if (contractD.getItemString(j, "MR_NO")
                                        .equals(samePatParm.getValue("MR_NO"))) {
                                    if (patName.equals("")) {
                                        this.messageBox("������Ϊ" + samePatParm.getValue("MR_NO")
                                                + "����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                                    } else {
                                        this.messageBox("������Ϊ" + samePatParm.getValue("MR_NO")
                                                + "������Ϊ " + samePatParm.getValue("PAT_NAME")
                                                + " ����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                                    }
                                    continue patLoop;
                                }
                            }
                            contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                            patName = samePatParm.getValue("PAT_NAME");
                            String sexCode = samePatParm.getValue("SEX_CODE");// add by wanglong 21030726
                            if (!parmRow.getValue("SEX_CODE").equals(sexCode)) {// add by wanglong 21030726
                                this.messageBox("��Ա��" + patName + "��(���" + seqNo
                                        + ")�Ӳ�����¼�д��ص��Ա���excel������Ա�ͬ\n"
                                        + "���������Ա�ڡ�������Ϣ���е���Ϣ���ڡ�����������Ϣ�趨���е���Ϣ�Ƿ�һ��");
                            }
                            if (parmRow.getValue("TEL").equals("")
                                    && !samePatParm.getValue("CELL_PHONE").equals("")
                                    && samePatParm.getValue("CELL_PHONE")
                                            .matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 �Զ�ȡ�绰����
                                parmRow.setData("TEL", samePatParm.getValue("CELL_PHONE"));
                            } else if (parmRow.getValue("TEL").equals("")
                                    && samePatParm.getValue("TEL_HOME").trim().length() == 11
                                    && samePatParm.getValue("TEL_HOME").trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
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
                                this.messageBox("������Ϊ" + patParm.getValue("MR_NO", 0)
                                        + "����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                            } else {
                                this.messageBox("������Ϊ" + patParm.getValue("MR_NO", 0) + "������Ϊ "
                                        + patParm.getValue("PAT_NAME", 0) + " ����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                            }
                            continue patLoop;
                        }
                    }
                    contractD.setItem(row, "MR_NO", patParm.getData("MR_NO", 0));
                    if (parmRow.getValue("TEL").equals("")
                            && !patParm.getValue("CELL_PHONE", 0).equals("")
                            && patParm.getValue("CELL_PHONE", 0).matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 �Զ�ȡ�绰����
                        parmRow.setData("TEL", patParm.getValue("CELL_PHONE", 0));
                    } else if (parmRow.getValue("TEL").equals("")
                            && patParm.getValue("TEL_HOME", 0).trim().length() == 11
                            && patParm.getValue("TEL_HOME", 0).trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
                        parmRow.setData("TEL", patParm.getValue("TEL_HOME", 0));
                    }
                }
            }
            // ���ڶ���������Ϣ
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
                                    this.messageBox("������Ϊ" + patParm.getValue("MR_NO", j)
                                            + "����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                                } else {
                                    this.messageBox("������Ϊ" + patParm.getValue("MR_NO", j) + "������Ϊ "
                                            + patParm.getValue("PAT_NAME", j)
                                            + " ����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                                }
                                continue patLoop;
                            }
                        }
                        contractD.setItem(row, "MR_NO", patParm.getData("MR_NO", j));
                        if (parmRow.getValue("TEL").equals("")
                                && !patParm.getValue("CELL_PHONE", j).equals("")
                                && patParm.getValue("CELL_PHONE", j).matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 �Զ�ȡ�绰����
                            parmRow.setData("TEL", patParm.getValue("CELL_PHONE", j));
                        } else if (parmRow.getValue("TEL").equals("")
                                && patParm.getValue("TEL_HOME", j).trim().length() == 11
                                && patParm.getValue("TEL_HOME", j).trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
                            parmRow.setData("TEL", patParm.getValue("TEL_HOME", j));
                        }
                        break;
                    }
                }
                if (j == patParm.getCount("MR_NO")) {
                    if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + patName + "��(���" + seqNo
                            + ")���֤��ͬ������������ͬ�Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {
                        Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", patParm);
                        if (obj != null) {
                            TParm samePatParm = (TParm) obj;
                            int rowCount = contractD.rowCount();
                            for (int k = 0; k < rowCount; k++) {// wanglong add 20140606
                                if (!contractD.isActive(k)) continue;
                                if (contractD.getItemString(k, "MR_NO")
                                        .equals(samePatParm.getValue("MR_NO"))) {
                                    if (patName.equals("")) {
                                        this.messageBox("������Ϊ" + samePatParm.getValue("MR_NO")
                                                + "����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                                    } else {
                                        this.messageBox("������Ϊ" + samePatParm.getValue("MR_NO")
                                                + "������Ϊ " + samePatParm.getValue("PAT_NAME")
                                                + " ����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                                    }
                                    continue patLoop;
                                }
                            }
                            contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                            patName = samePatParm.getValue("PAT_NAME");
                            String sexCode = samePatParm.getValue("SEX_CODE");// add by wanglong 21030726
                            if (!parmRow.getValue("SEX_CODE").equals(sexCode)) {// add by wanglong 21030726
                                this.messageBox("��Ա��" + patName + "��(���" + seqNo
                                        + ")�Ӳ�����¼�д��ص��Ա���excel������Ա�ͬ\n"
                                        + "���������Ա�ڡ�������Ϣ���е���Ϣ���ڡ�����������Ϣ�趨���е���Ϣ�Ƿ�һ��");
                            }
                            if (parmRow.getValue("TEL").equals("")
                                    && !samePatParm.getValue("CELL_PHONE").equals("")
                                    && samePatParm.getValue("CELL_PHONE")
                                            .matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 �Զ�ȡ�绰����
                                parmRow.setData("TEL", samePatParm.getValue("CELL_PHONE"));
                            } else if (parmRow.getValue("TEL").equals("")
                                    && samePatParm.getValue("TEL_HOME").trim().length() == 11
                                    && samePatParm.getValue("TEL_HOME").trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
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
            // �ۿ� modify by wanglong 20130116
            contractD.setItem(row, "DISCNT", parmRow.getDouble("DISCNT"));
            if (parmRow.getValue("TEL").equals("")
                    && !contractD.getItemString(row, "MR_NO").equals("Y")) {// wanglong add 20140512 �Զ�ȡ�绰����
                if (patParm.getCount("MR_NO") > 0) {
                    if (!patParm.getValue("CELL_PHONE", 0).trim().equals("")
                            && patParm.getValue("CELL_PHONE", 0).trim()
                                    .matches("[^\u4e00-\u9fa5]*")) {// �ֻ���(��������)
                        contractD.setItem(row, "TEL", patParm.getValue("CELL_PHONE", 0));
                    } else if (patParm.getValue("TEL_HOME", 0).trim().length() == 11
                            && patParm.getValue("TEL_HOME", 0).trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
                        contractD.setItem(row, "TEL", patParm.getValue("TEL_HOME", 0));
                    }
                }
            } else {
                contractD.setItem(row, "TEL", parmRow.getValue("TEL"));
            }
            contractD.setItem(row, "SEQ_NO", seqNo);
            // ����״̬ add by wanglong 20130116
            contractD.setItem(row, "MARRIAGE_CODE", parmRow.getValue("MARRIAGE_CODE"));
            contractD.setItem(row, "IS_VIP", parmRow.getValue("IS_VIP")); //��� add by huangjw 20160920
            // ���� add by wanglong 20130225
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
        this.messageBox_("����ɹ�");
    }

    public static void main(String[] args) {
        String id = "340104650406354";
        // System.out.println(""+StringTool.getBirdayFromID(id));
    }

    /**
     * ����TABLE��ɫ
     */
    public void setColorTable() {
        /**
         * ��ɫ
         */
        Color antibioticColor = new Color(255, 0, 0);
        /**
         * ��ͨ��ɫ
         */
        Color normalColor = new Color(0, 0, 0);
        int count = mrTab.getRowCount();
        TParm tableParm = contractD.getBuffer(contractD.PRIMARY);
        // System.out.println("����"+count);
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
     * �һ�MENU�����¼�
     * 
     * @param tableName
     */
    public void showPopMenu() {
        TParm action = mrTab.getDataStore().getRowParm(mrTab.getSelectedRow());
        if ("N".equals(action.getValue("MR_NO"))) {
            mrTab.setPopupMenuSyntax("ѡ�񲡰���,openRigthPopMenu");
            return;
        } else {
            mrTab.setPopupMenuSyntax("");
            return;
        }
    }

    /**
     * �Ҽ���ѯ
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
     * �ײ͸ı��¼�,��ѡ�е��ײʹ�������ͬϸ����Ϣ
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
            this.messageBox("��ѯ����ҽ��չ����Ϣ����");
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
//                this.messageBox(contractD.getItemString(i, "PAT_NAME") + "�ѱ��������ܸ����ײ�");
                flag = true;
                continue;
            }
            String mrNo = contractD.getItemString(i, "MR_NO");
            if (patAdmList.contains(mrNo)) {
//                this.messageBox(contractD.getItemString(i, "PAT_NAME") + "ҽ����չ�������ܸ����ײ�");
                flag = true;
                continue;
            }
            contractD.setItem(i, "PACKAGE_CODE", packageCode);
        }
        if (flag == true) {
            this.messageBox("�ѱ�������Ա���Լ�ҽ����չ������Ա�����ܸ����ײ�");
        }
        double amt = contractD.getContractAmt();
        // this.messageBox_(amt);
        contractM.setItem(conDescRow, "SUBTOTAL", amt);
        contractM.setItem(conDescRow, "TOT_AMT", amt);
        conTab.setDSValue();
        mrTab.setDSValue();
    }

    /**
     * ���ݲ����Ų�ѯ��ͬϸ��
     */
    public void onMrNo() {}

    /**
     * �����ʱ���ҵ�ַ
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
     * ����ʼ���ַ�Ƿ���Ч
     */
    public void onCheckEmail() {
        String email = this.getValueString("E_MAIL");
        if (!StringTool.isEmail(email)) {
            this.messageBox_("��ַ��Ч");
            this.callFunction("UI|E_MAIL|grabFocus");
            return;
        }
        this.callFunction("UI|CONTACTS_NAME|grabFocus");
    }

    /**
     * ��ѯ�¼�
     */
    public void onQuery() {
        String companyDesc = this.getValueString("COMPANY_DESC");
        if (StringUtil.isNullString(companyDesc)) {
            this.messageBox_("������ϢΪ��");
            return;
        }
        // caowl 20130308 start �޸�ģ����ѯʱֻ��ѯ��һ����Ϣ������
        TParm companyParm = company.queryCodeByName(companyDesc);
        String str = "COMPANY_CODE =";
        if (companyParm.getCount() <= 0) {
            this.messageBox_("������Ϣ");
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
     * ��ҩ������ҩƷ�����õ�checkBox�¼�
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
     * ���Excel add by wanglong 20130117
     */
    public void onExcel() {
        TTabbedPane tabbedPane = (TTabbedPane) this.getComponent("TAB_PANEL");
        if (tabbedPane.getSelectedIndex() == 0) {
            this.messageBox("���ں�ͬ��ϸҳǩ��ʹ��");
            return;
        }
        if (mrTab.getRowCount() <= 0) {
            this.messageBox("û������");
            return;
        } else {
            int rowCount = contractD.rowCount();
            if (rowCount <= 0) {
                this.messageBox("û������");
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
                this.messageBox("û������");
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
                       "����,60;��ͬ����,130;����֤��,60;���֤��,190;�Ա�,65;��������,115;�ײ�����,200;Ա������,110;����״̬,110;���,60;����,100;"
                               + "����,100;������,130;�绰,120;Ԥ��ʱ��,110;����ʱ��,110;��������֧��,110;�ۿ���,90;����״̬,110;�˵���,130;�վݺ�,130");
        TParm[] execleTable = new TParm[]{result };
        ExportExcelUtil.getInstance().exeSaveExcel(execleTable, "���������ͬ��Ϣ�趨");
        // ExportExcelUtil.getInstance().exportExcel(mrTab,"����������Ϣ");
    }

    /**
     * ����ȥ�����еļ�¼��
     * @param sheet
     * @return
     */
    private int getRightRows(Sheet sheet) {
        int rsCols = sheet.getColumns(); // ����
        int rsRows = sheet.getRows(); // ����
        int nullCellNum;
        int afterRows = rsRows;
        for (int i = 1; i < rsRows; i++) { // ͳ������Ϊ�յĵ�Ԫ����
            nullCellNum = 0;
            for (int j = 0; j < rsCols; j++) {
                String val = sheet.getCell(j, i).getContents();
                val = StringUtils.trimToEmpty(val);
                if (StringUtils.isBlank(val)) nullCellNum++;
            }
            if (nullCellNum >= rsCols) { // ���nullCellNum���ڻ�����ܵ�����
                afterRows--; // ������һ
            }
        }
        return afterRows;
    }
    
    /**
     * У�����֤��T40���isID����û��У�����֤�м�ĳ��������Ƿ���ȷ��
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
     * ������֤��ȷ�ԡ�����18λ���֤�����У��λ����ȷ���Զ�������
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
     * ���֤15λ��18λ
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
     * �õ�18λ���֤����λ
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
     * ���ɳ���1000Ԫ�ص�MR_NO IN()���
     * 
     * @param field
     * @param Nos
     * @return �������ơ�MR_NO IN (1,2,3...) OR MR_NO IN (1001,1002,1003...)��
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
     * ����������ļ��
     * 
     * @author wangb 2016/07/01
     */
	public void onQueryRecruitInfo() {
		conTab.acceptText();
		if (conTab == null) {
			this.messageBox_("û�к�ͬ��Ϣ");
			return;
		}
		int clickRow = conTab.getSelectedRow();
		if (clickRow < 0) {
			this.messageBox_("û�к�ͬ��Ϣ");
			return;
		}
		if (conTab.getItemString(clickRow, "CONTRACT_DESC").trim().equals("")) {
			this.messageBox_("û��ѡ���ͬ");
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
	 * ��ѡ�����ļ��Ϣ�����ͬ��ϸ
	 * 
	 * @param parm ��������ļ��Ϣ
	 */
	private void insertIntoContractD(TParm parm) {
		String discnt = conTab.getItemString(conTab.getSelectedRow(), "DISCNT");// ��ͬ�ۿ���
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
                            + "ORDER BY REPORT_DATE DESC";// �������֤�Ų鲡����
            patSql = patSql.replaceAll("#", idNo);
            TParm patParm = new TParm(TJDODBTool.getInstance().select(patSql));
            if (patParm.getErrCode() != 0) {
                // System.out.println("=======================patParm============" + patParm);
                this.messageBox("��ѯ��Ա������ʱ����");
                return;
            }
            // ��һ�ξ���
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
                                + "ORDER BY OPT_DATE DESC NULLS LAST";// ��ͬ��ͬ��
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
                        this.messageBox("��ѯ��Ա���ξ�����Ϣʱ����");
                        return;
                    }
                    if (result.getCount() < 1) {
                        contractD.setItem(row, "MR_NO", "Y");
                    } else if (result.getCount() == 1) {// add by wanglong 20130409
                        if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + patName + "��(���" + seqNo
                                + ")ͬ��ͬ����Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {// add by wanglong 20130726
                            Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", result);
                            if (obj != null) {
                                TParm samePatParm = (TParm) obj;
                                int rowCount = contractD.rowCount();
                                for (int j = 0; j < rowCount; j++) {// wanglong add 20140606
                                    if (!contractD.isActive(j)) continue;
                                    if (contractD.getItemString(j, "MR_NO")
                                            .equals(samePatParm.getValue("MR_NO"))) {
                                        if (patName.equals("")) {
                                            this.messageBox("������Ϊ" + samePatParm.getValue("MR_NO")
                                                    + "����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                                        } else {
                                            this.messageBox("������Ϊ" + samePatParm.getValue("MR_NO")
                                                    + "������Ϊ " + samePatParm.getValue("PAT_NAME")
                                                    + " ����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
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
                                    this.messageBox("��Ա��" + patName + "��(���" + seqNo
                                            + ")�Ӳ�����¼�д��ص��Ա���excel������Ա�ͬ\n"
                                            + "���������Ա�ڡ�������Ϣ���е���Ϣ���ڡ�����������Ϣ�趨���е���Ϣ�Ƿ�һ��");
                                }
//                                contractD.setItem(row, "MR_NO", result.getData("MR_NO", 0));
                                if (parmRow.getValue("CELL_PHONE").equals("")
                                        && !samePatParm.getValue("CELL_PHONE").equals("")
                                        && samePatParm.getValue("CELL_PHONE")
                                                .matches("[^\u4e00-\u9fa5]*")) {// wanglong add
                                                                                // 20140522 �Զ�ȡ�绰����
                                    parmRow.setData("CELL_PHONE", samePatParm.getValue("CELL_PHONE"));
                                } else if (parmRow.getValue("CELL_PHONE").equals("")
                                        && samePatParm.getValue("TEL_HOME").trim().length() == 11
                                        && samePatParm.getValue("TEL_HOME").trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
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
                                                .matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 �Զ�ȡ�绰����
                                    parmRow.setData("CELL_PHONE", result.getValue("CELL_PHONE",j));
                                } else if (parmRow.getValue("CELL_PHONE").equals("")
                                        && result.getValue("TEL_HOME", j).trim().length() == 11
                                        && result.getValue("TEL_HOME", j).trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
                                    parmRow.setData("CELL_PHONE", result.getValue("TEL_HOME", j));
                                }
                                break;
                            }
                        }
                        if (j == result.getCount()) {
                            if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + patName + "��(���" + seqNo
                                    + ")ͬ��ͬ����Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {
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
                                                this.messageBox("������Ϊ"
                                                        + samePatParm.getValue("MR_NO")
                                                        + "����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                                            } else {
                                                this.messageBox("������Ϊ"
                                                        + samePatParm.getValue("MR_NO") + "������Ϊ "
                                                        + samePatParm.getValue("PAT_NAME")
                                                        + " ����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
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
                                        this.messageBox("��Ա��" + patName + "��(���" + seqNo
                                                + ")�Ӳ�����¼�д��ص��Ա���excel������Ա�ͬ\n"
                                                + "���������Ա�ڡ�������Ϣ���е���Ϣ���ڡ�����������Ϣ�趨���е���Ϣ�Ƿ�һ��");
                                    }
                                    if (parmRow.getValue("CELL_PHONE").equals("")
                                            && !samePatParm.getValue("CELL_PHONE").equals("")
                                            && samePatParm.getValue("CELL_PHONE")
                                                    .matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 �Զ�ȡ�绰����
                                        parmRow.setData("CELL_PHONE", samePatParm.getValue("CELL_PHONE"));
                                    } else if (parmRow.getValue("CELL_PHONE").equals("")
                                            && samePatParm.getValue("TEL_HOME").trim().length() == 11
                                            && samePatParm.getValue("TEL_HOME").trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
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
            // ���ھ�����Ϣ
            else if (patParm.getCount("MR_NO") == 1) {
                if (!patParm.getValue("PAT_NAME", 0).equals(patName)) {
                    if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + patName + "��(���" + seqNo
                            + ")���֤��ͬ������������ͬ�Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {
                        Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", patParm);
                        if (obj != null) {
                            TParm samePatParm = (TParm) obj;
                            int rowCount = contractD.rowCount();
                            for (int j = 0; j < rowCount; j++) {// wanglong add 20140606
                                if (!contractD.isActive(j)) continue;
                                if (contractD.getItemString(j, "MR_NO")
                                        .equals(samePatParm.getValue("MR_NO"))) {
                                    if (patName.equals("")) {
                                        this.messageBox("������Ϊ" + samePatParm.getValue("MR_NO")
                                                + "����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                                    } else {
                                        this.messageBox("������Ϊ" + samePatParm.getValue("MR_NO")
                                                + "������Ϊ " + samePatParm.getValue("PAT_NAME")
                                                + " ����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                                    }
                                    continue patLoop;
                                }
                            }
                            contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                            patName = samePatParm.getValue("PAT_NAME");
                            String sexCode = samePatParm.getValue("SEX_CODE");// add by wanglong 21030726
                            if (!parmRow.getValue("SEX_CODE").equals(sexCode)) {// add by wanglong 21030726
                                this.messageBox("��Ա��" + patName + "��(���" + seqNo
                                        + ")�Ӳ�����¼�д��ص��Ա���excel������Ա�ͬ\n"
                                        + "���������Ա�ڡ�������Ϣ���е���Ϣ���ڡ�����������Ϣ�趨���е���Ϣ�Ƿ�һ��");
                            }
                            if (parmRow.getValue("CELL_PHONE").equals("")
                                    && !samePatParm.getValue("CELL_PHONE").equals("")
                                    && samePatParm.getValue("CELL_PHONE")
                                            .matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 �Զ�ȡ�绰����
                                parmRow.setData("CELL_PHONE", samePatParm.getValue("CELL_PHONE"));
                            } else if (parmRow.getValue("CELL_PHONE").equals("")
                                    && samePatParm.getValue("TEL_HOME").trim().length() == 11
                                    && samePatParm.getValue("TEL_HOME").trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
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
                                this.messageBox("������Ϊ" + patParm.getValue("MR_NO", 0)
                                        + "����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                            } else {
                                this.messageBox("������Ϊ" + patParm.getValue("MR_NO", 0) + "������Ϊ "
                                        + patParm.getValue("PAT_NAME", 0) + " ����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                            }
                            continue patLoop;
                        }
                    }
                    contractD.setItem(row, "MR_NO", patParm.getData("MR_NO", 0));
                    if (parmRow.getValue("CELL_PHONE").equals("")
                            && !patParm.getValue("CELL_PHONE", 0).equals("")
                            && patParm.getValue("CELL_PHONE", 0).matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 �Զ�ȡ�绰����
                        parmRow.setData("CELL_PHONE", patParm.getValue("CELL_PHONE", 0));
                    } else if (parmRow.getValue("CELL_PHONE").equals("")
                            && patParm.getValue("TEL_HOME", 0).trim().length() == 11
                            && patParm.getValue("TEL_HOME", 0).trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
                        parmRow.setData("CELL_PHONE", patParm.getValue("TEL_HOME", 0));
                    }
                }
            }
            // ���ڶ���������Ϣ
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
                                    this.messageBox("������Ϊ" + patParm.getValue("MR_NO", j)
                                            + "����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                                } else {
                                    this.messageBox("������Ϊ" + patParm.getValue("MR_NO", j) + "������Ϊ "
                                            + patParm.getValue("PAT_NAME", j)
                                            + " ����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                                }
                                continue patLoop;
                            }
                        }
                        contractD.setItem(row, "MR_NO", patParm.getData("MR_NO", j));
                        if (parmRow.getValue("CELL_PHONE").equals("")
                                && !patParm.getValue("CELL_PHONE", j).equals("")
                                && patParm.getValue("CELL_PHONE", j).matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 �Զ�ȡ�绰����
                            parmRow.setData("CELL_PHONE", patParm.getValue("CELL_PHONE", j));
                        } else if (parmRow.getValue("CELL_PHONE").equals("")
                                && patParm.getValue("TEL_HOME", j).trim().length() == 11
                                && patParm.getValue("TEL_HOME", j).trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
                            parmRow.setData("CELL_PHONE", patParm.getValue("TEL_HOME", j));
                        }
                        break;
                    }
                }
                if (j == patParm.getCount("MR_NO")) {
                    if (this.messageBox("�����źϲ�", "ϵͳ�д��ں���Ա��" + patName + "��(���" + seqNo
                            + ")���֤��ͬ������������ͬ�Ĳ�����¼���Ƿ�鿴�ǲ�������������\n", 2) == 0) {
                        Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", patParm);
                        if (obj != null) {
                            TParm samePatParm = (TParm) obj;
                            int rowCount = contractD.rowCount();
                            for (int k = 0; k < rowCount; k++) {// wanglong add 20140606
                                if (!contractD.isActive(k)) continue;
                                if (contractD.getItemString(k, "MR_NO")
                                        .equals(samePatParm.getValue("MR_NO"))) {
                                    if (patName.equals("")) {
                                        this.messageBox("������Ϊ" + samePatParm.getValue("MR_NO")
                                                + "����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                                    } else {
                                        this.messageBox("������Ϊ" + samePatParm.getValue("MR_NO")
                                                + "������Ϊ " + samePatParm.getValue("PAT_NAME")
                                                + " ����Ա��ϵͳ���Ѵ��ڣ������ᱻ�ٴε���");
                                    }
                                    continue patLoop;
                                }
                            }
                            contractD.setItem(row, "MR_NO", samePatParm.getValue("MR_NO"));
                            patName = samePatParm.getValue("PAT_NAME");
                            String sexCode = samePatParm.getValue("SEX_CODE");// add by wanglong 21030726
                            if (!parmRow.getValue("SEX_CODE").equals(sexCode)) {// add by wanglong 21030726
                                this.messageBox("��Ա��" + patName + "��(���" + seqNo
                                        + ")�Ӳ�����¼�д��ص��Ա���excel������Ա�ͬ\n"
                                        + "���������Ա�ڡ�������Ϣ���е���Ϣ���ڡ�����������Ϣ�趨���е���Ϣ�Ƿ�һ��");
                            }
                            if (parmRow.getValue("CELL_PHONE").equals("")
                                    && !samePatParm.getValue("CELL_PHONE").equals("")
                                    && samePatParm.getValue("CELL_PHONE")
                                            .matches("[^\u4e00-\u9fa5]*")) {// wanglong add 20140522 �Զ�ȡ�绰����
                                parmRow.setData("CELL_PHONE", samePatParm.getValue("CELL_PHONE"));
                            } else if (parmRow.getValue("CELL_PHONE").equals("")
                                    && samePatParm.getValue("TEL_HOME").trim().length() == 11
                                    && samePatParm.getValue("TEL_HOME").trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
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
            	// �ۿ� modify by wanglong 20130116
                contractD.setItem(row, "DISCNT", parmRow.getDouble("DISCNT"));
            } else {
            	contractD.setItem(row, "DISCNT", discnt);
            }
            
            if (parmRow.getValue("CELL_PHONE").equals("")
                    && !contractD.getItemString(row, "MR_NO").equals("Y")) {// wanglong add 20140512 �Զ�ȡ�绰����
                if (patParm.getCount("MR_NO") > 0) {
                    if (!patParm.getValue("CELL_PHONE", 0).trim().equals("")
                            && patParm.getValue("CELL_PHONE", 0).trim()
                                    .matches("[^\u4e00-\u9fa5]*")) {// �ֻ���(��������)
                        contractD.setItem(row, "TEL", patParm.getValue("CELL_PHONE", 0));
                    } else if (patParm.getValue("TEL_HOME", 0).trim().length() == 11
                            && patParm.getValue("TEL_HOME", 0).trim().matches("[^\u4e00-\u9fa5]*")) {// ��ͥ�绰�������д����11λ�ģ������ֻ���
                        contractD.setItem(row, "TEL", patParm.getValue("TEL_HOME", 0));
                    }
                }
            } else {
                contractD.setItem(row, "TEL", parmRow.getValue("CELL_PHONE"));
            }
            contractD.setItem(row, "SEQ_NO", seqNo);
            // ����״̬ add by wanglong 20130116
            contractD.setItem(row, "MARRIAGE_CODE", parmRow.getValue("MARRIAGE_CODE"));
            // ���� add by wanglong 20130225
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
        this.messageBox_("����ɹ�");
	}
	
	/**
	 * ����
	 */
	public void onReadIdCard(){
		TParm idParm = IdCardO.getInstance().readIdCard();
		if (idParm.getErrCode()<0) {
			this.messageBox(idParm.getErrText());
			return;
		}
		if (idParm.getCount() > 0) {// ����������ʾ
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
	 * �������֤��Ϣ
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
	 * ����һ��TTableNode����
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
