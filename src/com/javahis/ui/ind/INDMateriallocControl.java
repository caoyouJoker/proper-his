package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.util.Date;

import jdo.ind.INDSQL;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: 药品料位
 * </p>
 *
 * <p>
 * Description: 药品料位
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */

public class INDMateriallocControl
    extends TControl {

    private String action = "save";

    public INDMateriallocControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        initPage();
    }

    /**
     * 保存方法
     */
    public void onSave() {
        TTable table = getTable("TABLE");
        int row = 0;
        if ("save".equals(action)) {
            TTextFormat combo = this.getTextFormat("ORG_CODE");
            boolean flg = combo.isEnabled();
            if (flg) {
                if (!CheckData())
                    return;
                row = table.addRow();
            }
            else {
                row = table.getSelectedRow();
            }
            table.setItem(row, "ORG_CODE", getValueString("ORG_CODE"));
            table.setItem(row, "MATERIAL_LOC_CODE",
                          getValueString("MATERIAL_LOC_CODE"));
            table.setItem(row, "MATERIAL_CHN_DESC",
                          getValueString("MATERIAL_CHN_DESC"));
            table.setItem(row, "MATERIAL_ENG_DESC",
                          getValueString("MATERIAL_ENG_DESC"));
            table.setItem(row, "PY1", getValueString("PY1"));
            table.setItem(row, "PY2", getValueString("PY2"));
            table.setItem(row, "SEQ", getValueString("SEQ"));
            table.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));
            table.setItem(row, "OPT_USER", Operator.getID());
            Timestamp date = StringTool.getTimestamp(new Date());
            table.setItem(row, "OPT_DATE", date);
            table.setItem(row, "OPT_TERM", Operator.getIP());
        }
        TDataStore dataStore = table.getDataStore();
        if (dataStore.isModified()) {
            table.acceptText();
            if (!table.update()) {
                messageBox("E0001");
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
        TTable table = getTable("TABLE");
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
        String org = getValueString("ORG_CODE");
        String material = getValueString("MATERIAL_LOC_CODE");
        String filterString = "";
        if (org.length() > 0 && material.length() > 0) {
            filterString = "ORG_CODE = '" + org
                + "' AND MATERIAL_LOC_CODE like '" + material + "%'";
        }
        else if (org.length() > 0) {
            filterString = "ORG_CODE = '" + org + "'";
        }
        else if (material.length() > 0) {
            filterString = "MATERIAL_LOC_CODE like '" + material + "%'";
        }
        else {
            filterString = "";
        }
        TTable table = getTable("TABLE");
        table.setFilter(filterString);
        table.filter();
    }

    /**
     * 清空方法
     */
    public void onClear() {
        getTextFormat("ORG_CODE").setEnabled(true);
        getTextField("MATERIAL_LOC_CODE").setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        String tags =
            "ORG_CODE;MATERIAL_LOC_CODE;MATERIAL_CHN_DESC;MATERIAL_ENG_DESC;"
            + "PY1;PY2;DESCRIPTION";
        clearValue(tags);
        // 最大号+1(SEQ)
        TDataStore dataStroe = getTable("TABLE").getDataStore();
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);
        TTable table = getTable("TABLE");
        table.setSelectionMode(0);
        action = "save";
    }

    /**
     * TABLE单击事件
     */
    public void onTableClicked() {
        TTable table = getTable("TABLE");
        int row = table.getSelectedRow();
        if (row != -1) {
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            TParm parm = table.getDataStore().getRowParm(row);
            String likeNames =
                "ORG_CODE;MATERIAL_LOC_CODE;MATERIAL_CHN_DESC;MATERIAL_ENG_DESC;"
                + "PY1;PY2;SEQ;DESCRIPTION";
            this.setValueForParm(likeNames, parm);
            getTextFormat("ORG_CODE").setEnabled(false);
            getTextField("MATERIAL_LOC_CODE").setEnabled(false);
            action = "save";
        }
    }

    /**
     * 料位名称回车事件
     */
    public void onMaterialAction() {
        String name = getValueString("MATERIAL_CHN_DESC");
        if (name.length() > 0)
            setValue("PY1", TMessage.getPy(name));
        ( (TTextField) getComponent("MATERIAL_ENG_DESC")).grabFocus();
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 初始化Table
        TTable table = getTable("TABLE");
        table.removeRowAll();
        TDataStore dataStroe = new TDataStore();
        dataStroe.setSQL(INDSQL.getMaterialloc());
        dataStroe.retrieve();
        table.setDataStore(dataStroe);
        table.setDSValue();

        // 最大号+1(SEQ)
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
    }

    /**
     * 检查数据
     */
    private boolean CheckData() {
        String org_code = getValueString("ORG_CODE");
        if ("".equals(org_code)) {
            this.messageBox("部门代码不能为空");
            return false;
        }
        String material_loc_code = getValueString("MATERIAL_LOC_CODE");
        if ("".equals(material_loc_code)) {
            this.messageBox("料位代码不能为空");
            return false;
        }
        String sql = INDSQL.getMaterialloc(org_code, material_loc_code);
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(sql);
        dataStore.retrieve();
        if (dataStore.rowCount() > 0) {
            this.messageBox("部门和料位重复，无法保存");
            return false;
        }
        return true;
    }

    /**
     * 得到ComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }

    /**
     * 得到TextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
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
     * 得到最大的编号 +1
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */
    private int getMaxSeq(TDataStore dataStore, String columnName,
                          String dbBuffer) {
        if (dataStore == null)
            return 0;
        // 保存数据量
        int count = dataStore.getBuffer(dbBuffer).getCount();
        // 保存最大号
        int max = 0;
        for (int i = 0; i < count; i++) {
            int value = TCM_Transform.getInt(dataStore.getItemData(i,
                columnName, dbBuffer));
            // 保存最大值
            if (max < value) {
                max = value;
                continue;
            }
        }
        // 最大号加1
        max++;
        return max;
    }
}
