package com.javahis.ui.sys;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SYSSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.system.combo.TComboADMType;
import com.javahis.system.combo.TComboDept;
import com.javahis.system.combo.TComboOperatorCode;
import com.javahis.system.combo.TComboRegion;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>
 * Title:病历主索引
 * </p>
 *
 * <p>
 * Description:病历主索引
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.06.14
 * @version 1.0
 */
public class SYSEmrIndexControl
    extends TControl {

    private String action = "save";
    // 主项表格
    private TTable table;

    public SYSEmrIndexControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        initPage();
        //========pangben modify 20110421 start 权限添加
        this.setValue("REGION_CODE",Operator.getRegion());
       TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
       cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
               getValueString("REGION_CODE")));
       //===========pangben modify 20110421 stop

    }

    /**
     * 查询方法
     */
    public void onQuery() {
        // 初始化Table
        table = getTable("TABLE");
        table.removeRowAll();
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSEmrIndex(this.getValueString("REGION_CODE")));//=========pangben modify 20110609
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
        // 就诊序号
        String code = getValueString("CASE_NO");
        String filterString = "";
        if (code.length() > 0)
            filterString += "CASE_NO like '" + code + "%'";
        // 门急住别
        if (!"".equals(getValueString("ADM_TYPE"))) {
            if (filterString.length() > 0) {
                filterString += " AND ADM_TYPE = '" + getValueString("ADM_TYPE") +
                    "'";
            }
            else {
                filterString += "ADM_TYPE = '" + getValueString("ADM_TYPE") +
                    "'";
            }
        }
        // 病案号
        if (!"".equals(getValueString("MR_NO"))) {
            if (filterString.length() > 0) {
                filterString += " AND MR_NO like '" + getValueString("MR_NO") +
                    "%'";
            }
            else {
                filterString += "MR_NO like '" + getValueString("MR_NO") +
                    "%'";
            }
        }
        // 科室
        if (!"".equals(getValueString("DEPT_CODE"))) {
            if (filterString.length() > 0) {
                filterString += " AND DEPT_CODE = '" + getValueString("DEPT_CODE") +
                    "'";
            }
            else {
                filterString += "DEPT_CODE = '" + getValueString("DEPT_CODE") +
                    "'";
            }
        }
        // 医生
        if (!"".equals(getValueString("DR_CODE"))) {
            if (filterString.length() > 0) {
                filterString += " AND DR_CODE = '" + getValueString("DR_CODE") +
                    "'";
            }
            else {
                filterString += "DR_CODE = '" + getValueString("DR_CODE") +
                    "'";
            }
        }

        table.setFilter(filterString);
        table.filter();
    }

    /**
     * 保存方法
     */
    public void onSave() {
        int row = 0;
        Timestamp date = StringTool.getTimestamp(new Date());
        if ("save".equals(action)) {
            TTextField combo = getTextField("CASE_NO");
            boolean flg = combo.isEnabled();
            if (flg) {
                if (!CheckData())
                    return;
                row = table.addRow();
            }
            else {
                row = table.getSelectedRow();
            }
            table.setItem(row, "CASE_NO", getValueString("CASE_NO"));
            table.setItem(row, "ADM_TYPE", getValueString("ADM_TYPE"));
            table.setItem(row, "REGION_CODE", getValueString("REGION_CODE"));
            table.setItem(row, "IPD_NO", getValueString("IPD_NO"));
            table.setItem(row, "MR_NO", getValueString("MR_NO"));
            table.setItem(row, "ADM_DATE", getValue("ADM_DATE"));
            table.setItem(row, "DEPT_CODE", getValueString("DEPT_CODE"));
            table.setItem(row, "DR_CODE", getValueString("DR_CODE"));
            table.setItem(row, "DS_DATE", getValue("DS_DATE"));
            table.setItem(row, "OPT_USER", Operator.getID());
            table.setItem(row, "OPT_DATE", date);
            table.setItem(row, "OPT_TERM", Operator.getIP());
        }
        TDataStore dataStore = table.getDataStore();
        if (dataStore.isModified()) {
            table.acceptText();
            if (!table.update()) {
                messageBox("E0001");
                table.removeRow(row);
                table.setDSValue();
                onClear();
                return;
            }
            table.setDSValue();
        }
        messageBox("P0001");
        table.setDSValue();
        onClear();
    }

    /**
     * TABLE单击事件
     */
    public void onTableClicked() {
        int row = table.getSelectedRow();
        if (row != -1) {
            TParm parm = table.getDataStore().getRowParm(row);
            String likeNames = "CASE_NO;ADM_TYPE;REGION_CODE;IPD_NO;MR_NO;"
                + "ADM_DATE;DEPT_CODE;DR_CODE;DS_DATE";
            this.setValueForParm(likeNames, parm);
            getTextField("CASE_NO").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            action = "save";
        }
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        int row = table.getTable().getSelectedRow();
        if (row < 0)
            return;
        table.removeRow(row);
        table.setSelectionMode(0);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "delete";
    }

    /**
     * 清空方法
     */
    public void onClear() {
        // 清空画面内容
        String clearString = "CASE_NO;ADM_TYPE;REGION_CODE;IPD_NO;MR_NO;"
            + "ADM_DATE;DEPT_CODE;DR_CODE;DS_DATE";
        clearValue(clearString);
        table.setSelectionMode(0);
        getTextField("CASE_NO").setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "save";
        this.setValue("REGION_CODE",Operator.getRegion());
    }

    /**
     * 门急住别事件
     */
    public void onADMTypeAction() {
        // 门急住别
        TComboADMType type = (TComboADMType) getComponent("ADM_TYPE");
        // 科室
        TComboDept dept = (TComboDept) getComponent("DEPT_CODE");
        dept.setOpdFitFlg("");
        dept.setEmgFitFlg("");
        dept.setIpdFitFlg("");
        dept.setSelectedIndex( -1);
        if ("O".equals(type.getValue())) {
            dept.setOpdFitFlg("Y");
        }
        else if ("E".equals(type.getValue())) {
            dept.setEmgFitFlg("Y");
        }
        else if ("I".equals(type.getValue())) {
            dept.setIpdFitFlg("Y");
        }
        else {
        }
        dept.onQuery();
        ( (TComboRegion) getComponent("REGION_CODE")).grabFocus();
    }

    /**
     * 科室事件
     */
    public void onDeptAction() {
        TComboDept dept = (TComboDept) getComponent("DEPT_CODE");
        TComboOperatorCode doctor = (TComboOperatorCode) getComponent("DR_CODE");
        doctor.setSelectedIndex( -1);
        doctor.setDept(dept.getValue());
        doctor.onQuery();
        doctor.grabFocus();
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 初始化Table
        table = getTable("TABLE");
        table.removeRowAll();
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSEmrIndex(Operator.getRegion()));//==========pangben modify 20110609
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
    }

    /**
     * 检查数据
     */
    private boolean CheckData() {
        if ("".equals(getValueString("CASE_NO"))) {
            this.messageBox("就诊序号不能为空");
            return false;
        }
        return true;
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

}
