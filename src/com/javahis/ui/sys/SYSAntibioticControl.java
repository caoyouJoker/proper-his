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
import com.dongyang.util.TMessage;

/**
 * <p>
 * Title:抗生素等级
 * </p>
 *
 * <p>
 * Description:抗生素等级
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
 * @author zhangy 2009.6.04
 * @version 1.0
 */
public class SYSAntibioticControl
    extends TControl {
    private String action = "save";
    // 主项表格
    private TTable table;

    public SYSAntibioticControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        initPage();
    }

    /**
     * 保存方法
     */
    public void onSave() {
        int row = 0;
        Timestamp date = StringTool.getTimestamp(new Date());
        if ("save".equals(action)) {
            TTextField combo = getTextField("ANTIBIOTIC_CODE");
            boolean flg = combo.isEnabled();
            if (flg) {
                if (!CheckData())
                    return;
                row = table.addRow();
            }
            else {
                row = table.getSelectedRow();
            }
            table.setItem(row, "ANTIBIOTIC_CODE",
                          getValueString("ANTIBIOTIC_CODE"));
            table.setItem(row, "ANTIBIOTIC_DESC",
                          getValueString("ANTIBIOTIC_DESC"));
            table.setItem(row, "ENG_DESC", getValueString("ENG_DESC"));
            table.setItem(row, "PY1", getValueString("PY1"));
            table.setItem(row, "PY2", getValueString("PY2"));
            table.setItem(row, "TAKE_DAYS", getValueDouble("TAKE_DAYS"));
            String mr_code = getValueString("MR_CODE");
            table.setItem(row, "MR_CODE", mr_code);
            table.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));
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
     * 查询方法
     */
    public void onQuery() {
        // 初始化Table
        table = getTable("TABLE");
        table.removeRowAll();
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSAntibiotic());
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();

        String code = getValueString("ANTIBIOTIC_CODE");
        String desc = getValueString("ANTIBIOTIC_DESC");
        String filterString = "";
        if (code.length() > 0 && desc.length() > 0)
            filterString += "ANTIBIOTIC_CODE like '" + code
                + "%' AND ANTIBIOTIC_DESC like '" + desc + "%'";
        else if (code.length() > 0)
            filterString += "ANTIBIOTIC_CODE like '" + code + "%'";
        else if (desc.length() > 0)
            filterString += "ANTIBIOTIC_DESC like '" + desc + "%'";
        table.setFilter(filterString);
        table.filter();
    }

    /**
     * 清空方法
     */
    public void onClear() {
        // 清空画面内容
        String clearString =
            "ANTIBIOTIC_CODE;ANTIBIOTIC_DESC;ENG_DESC;PY1;PY2;TAKE_DAYS;"
            + "MR_CODE;DESCRIPTION";
        clearValue(clearString);

        table.setSelectionMode(0);
        getTextField("ANTIBIOTIC_CODE").setEnabled(true);
        //getTextField("ANTIBIOTIC_DESC").setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "save";
    }

    /**
     * TABLE单击事件
     */
    public void onTableClicked() {
        int row = table.getSelectedRow();
        if (row != -1) {
            TParm parm = table.getDataStore().getRowParm(row);
            String likeNames =
                "ANTIBIOTIC_CODE;ANTIBIOTIC_DESC;ENG_DESC;PY1;PY2;TAKE_DAYS;"
                + "MR_CODE;DESCRIPTION";
            this.setValueForParm(likeNames, parm);
            getTextField("ANTIBIOTIC_CODE").setEnabled(false);
            //getTextField("ANTIBIOTIC_DESC").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            action = "save";
        }
    }

    /**
     * AntibioticDesc回车事件
     */
    public void onAntibioticDescAction() {
        String py = TMessage.getPy(this.getValueString("ANTIBIOTIC_DESC"));
        setValue("PY1", py);
        getTextField("ENG_DESC").grabFocus();
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 初始化Table
        table = getTable("TABLE");
        table.removeRowAll();
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSAntibiotic());
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
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

    /**
     * 检查数据
     */
    private boolean CheckData() {
        if ("".equals(getValueString("ANTIBIOTIC_CODE"))) {
            this.messageBox("等级代码不能为空");
            return false;
        }
        if ("".equals(getValueString("ANTIBIOTIC_DESC"))) {
            this.messageBox("抗生素描述不能为空");
            return false;
        }
        if (getValueDouble("TAKE_DAYS") <= 0) {
            this.messageBox("最大使用天数不能小于或等于O");
            return false;
        }
        return true;
    }
}
