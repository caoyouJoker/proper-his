package com.javahis.ui.opd;

import com.dongyang.control.TControl;
import com.dongyang.ui.TMenuItem;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: 常用模板</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OPDCommTempletControl extends TControl{

    private String action = "save";
    // 主项表格
    private TTable table;

    private String type = "2";

    public OPDCommTempletControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        initPage();
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        type = (String)this.getParameter();
        // 初始化Table
        table = getTable("TABLE");
        String sql = "SELECT DEPT_OR_DR, DEPTORDR_CODE, SUBCLASS_CODE, "
            + " SEQ, MAIN_FLG, OPT_USER, OPT_DATE, OPT_TERM "
            + " FROM OPD_COMTEMPLET "
            + " WHERE DEPT_OR_DR = '" + type + "' ";
        String where = "";
        // 1：科室模板；2： 医生模板
        if ("1".equals(type)) {
            where = " AND DEPTORDR_CODE = '" + Operator.getDept() + "' ";
            ( (TTextFormat)this.getComponent("DEPT_CODE")).setVisible(true);
            ( (TTextFormat)this.getComponent("DR_CODE")).setVisible(false);
            this.setValue("DEPT_CODE", Operator.getDept());
            this.setValue("LAB_TEXT", "科室：");
        }
        else {
            where = " AND DEPTORDR_CODE = '" + Operator.getID() + "' ";
            ( (TTextFormat)this.getComponent("DEPT_CODE")).setVisible(false);
            ( (TTextFormat)this.getComponent("DR_CODE")).setVisible(true);
            this.setValue("DR_CODE", Operator.getID());
            this.setValue("LAB_TEXT", "医生：");
        }
        String order_by =
            " ORDER BY DEPTORDR_CODE, MAIN_FLG DESC, SUBCLASS_CODE, SEQ ";

        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(sql + where + order_by);
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
    }

    /**
     * 保存方法
     */
    public void onSave(){
        int row = 0;
        Timestamp date = StringTool.getTimestamp(new Date());
        String main_sql  = "";
        if ("1".equals(type)) {
            main_sql = "SELECT MAIN_FLG FROM OPD_COMTEMPLET " +
                "WHERE DEPT_OR_DR = '1' AND DEPTORDR_CODE = '" +
                getValueString("DEPT_CODE") + "' AND MAIN_FLG = 'Y'";
        }
        else {
            main_sql = "SELECT MAIN_FLG FROM OPD_COMTEMPLET " +
                "WHERE DEPT_OR_DR = '2' AND DEPTORDR_CODE = '" +
                getValueString("DR_CODE") + "' AND MAIN_FLG = 'Y' ";
        }

        TParm result = new TParm(TJDODBTool.getInstance().select(main_sql));
        if (result != null && result.getCount() > 0 &&
            "Y".equals(this.getValueString("MAIN_FLG"))) {
            this.messageBox("已存在柱模板，请重新选择！");
            return;
        }

        if ("save".equals(action)) {
            TTextFormat combo = getTextFormat("SUBCLASS_CODE");
            boolean flg = combo.isEnabled();
            if (flg) {
                if (!CheckData())
                    return;
                row = table.addRow();
            }
            else {
                row = table.getSelectedRow();
            }
            if ("1".equals(type)) {
                table.getDataStore().setItem(row, "DEPT_OR_DR", "1");
                table.getDataStore().setItem(row, "DEPTORDR_CODE",
                                             getValueString("DEPT_CODE"));
            }
            else {
                table.getDataStore().setItem(row, "DEPT_OR_DR", "2");
                table.getDataStore().setItem(row, "DEPTORDR_CODE",
                                             getValueString("DR_CODE"));
            }
            table.setItem(row, "SUBCLASS_CODE",
                          getValueString("SUBCLASS_CODE"));
            String sql = "SELECT SEQ FROM EMR_TEMPLET WHERE SUBCLASS_CODE = '" +
                getValueString("SUBCLASS_CODE") + "' AND OPD_FLG = 'Y' ";
            TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
            table.setItem(row, "SEQ", parm.getInt("SEQ",0));
            table.setItem(row, "MAIN_FLG", getValueString("MAIN_FLG"));
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
    }

    /**
     * TABLE单击事件
     */
    public void onTableClicked() {
        int row = table.getSelectedRow();
        if (row != -1) {
            TParm parm = table.getDataStore().getRowParm(row);
            String likeNames = "SUBCLASS_CODE;MAIN_FLG";
            this.setValueForParm(likeNames, parm);
            getTextFormat("SUBCLASS_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            action = "save";
        }
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        // 初始化Table
        table = getTable("TABLE");
        String sql = "SELECT DEPT_OR_DR, DEPTORDR_CODE, SUBCLASS_CODE, "
            + " SEQ, MAIN_FLG, OPT_USER, OPT_DATE, OPT_TERM "
            + " FROM OPD_COMTEMPLET "
            + " WHERE DEPT_OR_DR = '" + type + "' ";
        String where = "";
        // 1：科室模板；2： 医生模板
        if ("1".equals(type)) {
            where = " AND DEPTORDR_CODE = '" + Operator.getDept() + "' ";
            ( (TTextFormat)this.getComponent("DEPT_CODE")).setVisible(true);
            ( (TTextFormat)this.getComponent("DR_CODE")).setVisible(false);
            this.setValue("DEPT_CODE", Operator.getDept());
            this.setValue("LAB_TEXT", "科室：");
        }
        else {
            where = " AND DEPTORDR_CODE = '" + Operator.getID() + "' ";
            ( (TTextFormat)this.getComponent("DEPT_CODE")).setVisible(false);
            ( (TTextFormat)this.getComponent("DR_CODE")).setVisible(true);
            this.setValue("DR_CODE", Operator.getID());
            this.setValue("LAB_TEXT", "医生：");
        }
        String order_by =
            " ORDER BY DEPTORDR_CODE, MAIN_FLG DESC, SUBCLASS_CODE, SEQ ";
        // System.out.println(sql + where + order_by);
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(sql + where + order_by);
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
        String code = getValueString("SUBCLASS_CODE");
        if (code.length() > 0) {
            String filterString = "SUBCLASS_CODE = '" + code + "'";
            table.setFilter(filterString);
            table.filter();
        }
    }

    /**
     * 清空方法
     */
    public void onClear() {
        this.setValue("SUBCLASS_CODE", "");
        this.setValue("MAIN_FLG", "N");
        table.setSelectionMode(0);
        ( (TTextFormat)this.getComponent("SUBCLASS_CODE")).setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "save";
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
     * 检查数据
     */
    private boolean CheckData() {
        if ("".equals(getValueString("SUBCLASS_CODE"))) {
            this.messageBox("模板编号不能为空");
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
     * 得到TextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

}
