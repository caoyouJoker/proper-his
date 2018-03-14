package com.javahis.ui.ope;

import java.awt.Component;
import javax.swing.JOptionPane;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p> Title: 手术套餐设定</p>
 * 
 * <p> Description: 手术套餐设定</p>
 * 
 * <p> Copyright: Copyright (c) 2015</p>
 * 
 * <p> Company:BlueCore</p>
 * 
 * @author wanglong 20150225
 * @version 1.0
 */
public class OPEPackageControl
        extends TControl {

    // 主、细TABLE
    private TTable mainTable, detailTable;
    // 套餐主项对象
    private TDataStore main;
    // 套餐细项对象
    private TDataStore detail;
    // 删除按钮用的表名变量
    private String tableName;
    // 套餐主表单击行数
    private int mainRow;
    final static String INIT_MAIN_SQL = "SELECT * FROM OPE_PACKM";
    final static String INIT_DETAIL_SQL =
            "SELECT * FROM OPE_PACKD WHERE PACK_CODE ='#' ORDER BY SEQ_NO";

    /**
     * 初始化事件
     */
    public void onInit() {
        super.onInit();
        // 初始化控件
        initComponent();
        // 初始化数据
        initData();
    }

    /**
     * 初始化控件
     */
    private void initComponent() {
        mainTable = (TTable) this.getComponent("MAIN_TABLE");
        mainTable.addEventListener("MAIN_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                                   "onMainValueChanged");
        detailTable = (TTable) this.getComponent("DETAIL_TABLE");
        detailTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                     "onDetailCreateEditComponent");
        detailTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onDetailCheckClicked");
        detailTable.addEventListener("DETAIL_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                                     "onDetailValueChanged");
    }

    /**
     * 初始化数据
     */
    private void initData() {
        main = new TDataStore();
        detail = new TDataStore();
        main.setSQL(INIT_MAIN_SQL);
        main.retrieve();
        main.setSort("PACK_DESC");// add by wanglong 20130224
        main.sort();// add by wanglong 20130224
        mainTable.setDataStore(main);
        mainTable.setDSValue();
        detail.setSQL(INIT_DETAIL_SQL.replace("#", ""));
        detail.retrieve();
    }

    /**
     * 主TABLE点击事件,根据选定的套餐代码，初始化套餐细项
     */
    public void onMainClick() {
        tableName = "MAIN_TABLE";
        int row = mainTable.getSelectedRow();
        String packCode = main.getItemString(row, "PACK_CODE");
        // main.setActive(row, false);
        detail.setSQL(INIT_DETAIL_SQL.replace("#", packCode));
        detail.retrieve();
        // detail.setFilter("PACK_CODE='" + packCode + "'");
        // detail.filter();
        String opCode = detail.getItemString(detail.rowCount() - 1, "OP_CODE");
        // String opDesc = detail.getItemString(detail.rowCount() - 1, "OP_DESC");
        if (!StringUtil.isNullString(opCode) || detail.rowCount() < 1) {
            row = detail.insertRow();
            detail.setItem(row, "PACK_CODE", packCode);
            if (detail.rowCount() < 1) {
                detail.setItem(row, "SEQ_NO", detail.getItemInt(row, "#ID#"));
            } else {
                detail.setItem(row, "SEQ_NO", detail.getItemInt(row - 1, "SEQ_NO") + 1);
            }
            detail.setItem(row, "OP_CODE", "");
            detail.setItem(row, "OP_DESC", "");
            detail.setItem(row, "DESCRIPTION", "");
            detail.setItem(row, "OPT_USER", Operator.getID());
            detail.setItem(row, "OPT_TERM", Operator.getIP());
            detail.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
            detail.setActive(row, false);
        }
        detailTable.setDataStore(detail);
        detailTable.setDSValue();
        mainRow = mainTable.getSelectedRow();
    }

    /**
     * 细TABLE点击事件
     */
    public void onDetailClick() {
        tableName = "DETAIL_TABLE";
    }

    /**
     * 主表值改变事件,套餐主项套餐名称修改，自动生成简拼代码保存。
     * 
     * @param tNode
     */
    public boolean onMainValueChanged(TTableNode tNode) {
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String colName = mainTable.getParmMap(column);
        Object value = tNode.getValue();
//        Object oldValue = tNode.getOldValue();
        if (TCM_Transform.isNull(value)) {
            return true;
        }
        if ("PACK_DESC".equalsIgnoreCase(colName)) {
            String packDesc = tNode.getValue() + "";
            main.setItem(row, "PY1", SystemTool.getInstance().charToCode(packDesc));
            main.setActive(row, true);
            return false;
        } else {
            main.setItem(row, colName, tNode.getValue() + "");
            main.setActive(row, true);
        }
        return false;
    }

    /**
     * 细表值改变事件
     * 
     * @param tNode
     */
    public boolean onDetailValueChanged(TTableNode tNode) {
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String colName = tNode.getTable().getParmMap(column);
        Object value = tNode.getValue();
        // Object oldValue = tNode.getOldValue();
        if (TCM_Transform.isNull(value)) {
            return true;
        }
        if ("OP_CODE".equalsIgnoreCase(colName)) {
            if (!TCM_Transform.isNull(value) && row == detailTable.getRowCount() - 1) {
                newRow(row);
            }
            detailTable.getTable().grabFocus();
            detailTable.setSelectedRow(row);
            // detailTable.setSelectedColumn(column);
            return false;
        }
        return false;
    }

    /**
     * 诊断弹出界面 ICD10
     * 
     * @param com
     * @param row
     * @param column
     */
    public void onDetailCreateEditComponent(Component com, int row, int column) {
        column = detailTable.getColumnModel().getColumnIndex(column);
        String columnName = detailTable.getParmMap(column);
        if (!"OP_CODE".equalsIgnoreCase(columnName)) {
            return;
        }
        if (!(com instanceof TTextField)) return;
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        // 给table上的新text增加ICD10弹出窗口
        textfield.setPopupMenuParameter("OP_ICD",
                                        getConfigParm()
                                                .newConfig("%ROOT%\\config\\sys\\SYSOpICD.x"));
        // 给新text增加接受ICD10弹出窗口的回传值
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popICDReturn");
    }

    /**
     * 取得手术ICD返回值
     * 
     * @param tag
     *            String
     * @param obj
     *            Object
     */
    public void popICDReturn(String tag, Object obj) {
        detailTable.acceptText();
        TParm parm = (TParm) obj;
        String opCode = parm.getValue("OPERATION_ICD");
        String opDesc = parm.getValue("OPT_CHN_DESC");
        detailTable.setItem(detailTable.getSelectedRow(), "OP_CODE", opCode);
        detailTable.setItem(detailTable.getSelectedRow(), "OP_DESC", opDesc);
        detailTable.getTable().grabFocus();
        detailTable.setSelectedRow(detailTable.getSelectedRow());
        detailTable.setDSValue();
    }

    /**
     * 细项table监听事件
     * 
     * @param obj
     *            Object
     * @return boolean
     */
    public boolean onDetailCheckClicked(Object obj) {
        int row = detailTable.getSelectedRow();
        // 得到当前选中行数据
        for (int i = 0; i < detailTable.getRowCount(); i++) {
            detailTable.setValueAt("N", i, 0);
        }
        detailTable.setValueAt("Y", row, 0);
        detailTable.acceptText();
        return true;
    }

    /**
     * 细项table新增一行
     * 
     * @param row
     */
    public void newRow(int row) {
        String packCode = detail.getItemString(row, "PACK_CODE");
        int newRow = detail.insertRow();
        detail.setItem(newRow, "PACK_CODE", packCode);
        detail.setItem(newRow, "SEQ_NO", detail.getItemInt(row, "SEQ_NO") + 1);
        detail.setItem(newRow, "OP_CODE", "");
        detail.setItem(newRow, "OP_DESC", "");
        detail.setItem(newRow, "DESCRIPTION", "");
        detail.setItem(newRow, "OPT_USER", Operator.getID());
        detail.setItem(newRow, "OPT_TERM", Operator.getIP());
        detail.setItem(newRow, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        detail.setActive(newRow, false);
        detail.setActive(row, true);
        detailTable.setDSValue();
    }

    /**
     * 新增一条套餐主表数据
     */
    public void onNew() {
        if (main.rowCount() > 0
                && StringUtil.isNullString(main.getItemString(main.rowCount() - 1, "PACK_DESC"))) {
            return;
        }
        String packCode = SystemTool.getInstance().getNo("ALL", "OPE", "OPEPACK_NO", "OPEPACK_NO");
        if (StringUtil.isNullString(packCode)) {
            this.messageBox_("取得套餐编码失败");
            return;
        }
        int row = mainTable.addRow();
        mainTable.setSelectedRow(row);
        main.setItem(row, "PACK_CODE", packCode);
        main.setItem(row, "OPT_USER", Operator.getID());
        main.setItem(row, "OPT_TERM", Operator.getIP());
        main.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        main.setActive(row, false);
        mainTable.setDSValue();
    }

    /**
     * 删除一条数据
     */
    public void onDelete() {
        String packCode = main.getItemString(mainRow, "PACK_CODE");
        // this.messageBox_(tableName);
        if ("MAIN_TABLE".equalsIgnoreCase(tableName)) {
            if (this.messageBox("提示信息", "确认删除吗", JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            main.setActive(mainRow, true);
            main.deleteRow(mainRow);
            String[] sql = main.getUpdateSQL();
            detail.setFilter("PACK_CODE='" + packCode + "'");
            detail.filter();
            int count = detail.rowCount();
            for (int i = count - 1; i > -1; i--) {
                detail.deleteRow(i);
            }
            String[] detailSql = detail.getUpdateSQL();
            sql = StringTool.copyArray(sql, detailSql);
            TParm result = new TParm(TJDODBTool.getInstance().update(sql));
            if (result.getErrCode() < 0) {
                this.messageBox_(result.getErrText());
                return;
            }
            detail.resetModify();
            main.resetModify();
            mainTable.setDSValue();
            detailTable.setDSValue();
        } else {
            int row = -1;
            row = detailTable.getSelectedRow();
            if (StringUtil.isNullString(detail.getItemString(row, "OP_CODE"))) {
                return;
            }
            detail.deleteRow(row);
            String[] sql = detail.getUpdateSQL();
            TParm result = new TParm(TJDODBTool.getInstance().update(sql));
            if (result.getErrCode() < 0) {
                this.messageBox_(result.getErrText());
                return;
            }
            detail.resetModify();
            detailTable.setDSValue();
        }
    }

    /**
     * 保存
     */
    public void onSave() {
        mainTable.acceptText();
        detailTable.acceptText();
        String[] sql = main.getUpdateSQL();
        String[] detailSql = detail.getUpdateSQL();
        sql = StringTool.copyArray(sql, detailSql);
        if (sql == null || sql.length < 1) {
            this.messageBox_("没有找到保存的数据");
            return;
        }
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("E0001");// 保存失败
            return;
        }
        this.messageBox("P0001");// 保存成功
        onClear();
        return;
    }

    /**
     * 清空事件
     */
    public void onClear() {
        mainTable.removeRowAll();
        detailTable.removeRowAll();
        // 初始化套餐主项
        main = new TDataStore();
        main.setSQL(INIT_MAIN_SQL);
        main.retrieve();
        main.setSort("PACK_DESC ASC");// add by wanglong 20130325
        main.sort();
        // 初始化套餐细项
        detail = new TDataStore();
        detail.setSQL(INIT_DETAIL_SQL.replace("#", ""));
        detail.retrieve();
        mainTable.setDataStore(main);
        mainTable.setDSValue();
        detailTable.setDataStore(detail);
        detailTable.setDSValue();
    }
}
