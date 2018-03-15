package com.javahis.ui.aci;

import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import jdo.aci.ACIBadEventTool;
import jdo.aci.ACISMSPackageD;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 不良事件短信套餐设定 </p>
 *
 * <p>Description: 不良事件短信套餐设定 </p>
 *
 * <p> Copyright: Copyright (c) 2013 </p>
 *
 * <p>Company: ProperSoft </p>
 *
 * @author wanglong 2013.11.01
 * @version 1.0
 */
public class ACISMSPackageControl extends TControl{

    // 短信套餐主档初始化SQL
    public static final String INIT_PACKAGE_MAIN = " SELECT * FROM ACI_SMSPACKAGEM ";
    // 短信套餐细档初始化SQL
    public static final String INIT_PACKAGE_DETAIL = " SELECT * FROM ACI_SMSPACKAGED WHERE PACKAGE_CODE ='#' ";
    private TTable tableM, tableD;
    private TDataStore main; // 套餐主项datastore
    private ACISMSPackageD detail; // 套餐细相datastore
    private String tableName;// 删除按钮用的表名变量
    private int mainRow; // 套餐主表单击行数
	
	/**
	 * 初始化事件
	 */
    public void onInit() {
        super.onInit();
        initComponent(); // 初始化控件
        initData(); // 初始化数据
    }

	/**
	 * 初始化控件
	 */
    private void initComponent() {
        tableM = (TTable) this.getComponent("TABLE_M");
        tableM.addEventListener("TABLE_M->" + TTableEvent.CHANGE_VALUE, this, "onMainValueChanged"); // 套餐主项值改变事件
        tableM.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onMainCheckClicked"); // 套餐主项CHECK_BOX事件
        tableD = (TTable) this.getComponent("TABLE_D");
        tableD.addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE, this,
                                "onDetailValueChanged"); // 套餐细相值改变事件
    }
    
	/**
	 * 初始化数据
	 */
    private void initData() {
        main = new TDataStore();
        main.setSQL(INIT_PACKAGE_MAIN);
        main.retrieve();
        main.setSort("PACKAGE_CODE");
        main.sort();
        tableM.setDataStore(main);
        tableM.setDSValue();
        detail = new ACISMSPackageD();
        detail.setSQL(INIT_PACKAGE_DETAIL.replace("#", ""));
        detail.retrieve();
    }
	
	/**
	 * 主TABLE点击事件,根据选定的套餐代码，初始化套餐细相信息
	 */
    public void onMainClick() {
        tableName = "TABLE_M";
        int row = tableM.getSelectedRow();
        String packCode = main.getItemString(row, "PACKAGE_CODE");
        if (main.getItemString(row, "PACKAGE_DESC").equals("")) {
            main.setActive(row, false);
        }
        detail.setSQL(INIT_PACKAGE_DETAIL.replace("#", packCode));
        detail.retrieve();
        String temp = detail.getItemString(detail.rowCount() - 1, "USER_ID");
        if (!StringUtil.isNullString(temp) || detail.rowCount() < 1) {
            row = detail.insertRow();
            detail.setItem(row, "PACKAGE_CODE", packCode);
            detail.setItem(row, "USER_ID", null);
            detail.setItem(row, "TEL", null);
            detail.setItem(row, "DEPT_CODE", null);
            detail.setItem(row, "POS_CODE", null);
            detail.setItem(row, "OPT_USER", Operator.getID());
            detail.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
            detail.setItem(row, "OPT_TERM", Operator.getIP());
            detail.setActive(row, false);
        }
        tableD.setDataStore(detail);
        tableD.setDSValue();
        mainRow = tableM.getSelectedRow();
    }
    
	/**
	 * 细TABLE点击事件
	 */
    public void onDetailClick() {
        tableName = "TABLE_D";
    }
    
	/**
	 * 主表值改变事件,套餐主项套餐名称修改，自动生成简拼代码保存
	 * @param tNode
	 */
    public boolean onMainValueChanged(TTableNode tNode) {
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String colName = tableM.getParmMap(column);
        // String packCode = main.getItemString(row, "PACKAGE_CODE");
        if ("PACKAGE_DESC".equalsIgnoreCase(colName)) {
            String packDesc = tNode.getValue() + "";
            main.setItem(row, "PY", SystemTool.getInstance().charToCode(packDesc));
            main.setActive(row, true);
            return false;
        } else {
            main.setItem(row, colName, tNode.getValue() + "");
            main.setActive(row, true);
        }
        return false;
    }
    
	/**
	 * 套餐主项的CHECK_BOX事件
	 * @param obj
	 * @return
	 */
    public boolean onMainCheckClicked(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        table.setDSValue();
        return false;
    }
	
	/**
	 * 细表值改变事件
	 * @param tNode
	 */
    public boolean onDetailValueChanged(TTableNode tNode) {
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String packCode = detail.getItemString(row, "PACKAGE_CODE");
        String colName = tNode.getTable().getParmMap(column);
        if ("USER_ID".equalsIgnoreCase(colName)) {
            String userId = tNode.getValue() + "";
            if (StringUtil.isNullString(userId)) {
                return true;
            }
            for (int i = 0; i < tableD.getDataStore().rowCount(); i++) {
                if (i == tableD.getSelectedRow()) {
                    continue;
                }
                if (userId.equals(tableD.getDataStore().getItemData(i, "USER_ID"))) {
                    this.messageBox("已录入人员不得重复录入");
                    return true;
                }
            }
            TParm result = ACIBadEventTool.getInstance().getOperatorInfo(userId);
            detail.setItem(row, "TEL", result.getValue("TEL1", 0));
            detail.setItem(row, "DEPT_CODE", result.getValue("COST_CENTER_CODE", 0));
            detail.setItem(row, "POS_CODE", result.getValue("POS_CODE", 0));
            if ((row + 1 == detail.rowCount())
                    && !StringUtil.isNullString(result.getValue("TEL1", 0))) {
                row = detail.insertRow();
                detail.setItem(row, "PACKAGE_CODE", detail.getItemString(0, "PACKAGE_CODE"));
                detail.setItem(row, "PACKAGE_CODE", packCode);
                detail.setItem(row, "USER_ID", null);
                detail.setItem(row, "TEL", null);
                detail.setItem(row, "DEPT_CODE", null);
                detail.setItem(row, "POS_CODE", null);
                detail.setItem(row, "OPT_USER", Operator.getID());
                detail.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
                detail.setItem(row, "OPT_TERM", Operator.getIP());
                detail.setActive(row, false);
                tableD.setDSValue();
                tableD.getTable().grabFocus();
                tableD.setSelectedRow(tableD.getRowCount() - 1);
                tableD.setSelectedColumn(1);
            }
        }
        if ("TEL".equalsIgnoreCase(colName)) {
            detail.setActive(row, true);
            if ((row + 1 == detail.rowCount()) && !detail.getItemString(row, "USER_ID").equals("")) {
                row = detail.insertRow();
                detail.setItem(row, "PACKAGE_CODE", detail.getItemString(0, "PACKAGE_CODE"));
                detail.setItem(row, "PACKAGE_CODE", packCode);
                detail.setItem(row, "USER_ID", null);
                detail.setItem(row, "TEL", null);
                detail.setItem(row, "DEPT_CODE", null);
                detail.setItem(row, "POS_CODE", null);
                detail.setItem(row, "OPT_USER", Operator.getID());
                detail.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
                detail.setItem(row, "OPT_TERM", Operator.getIP());
                detail.setActive(row, false);
                tableD.setDSValue();
                tableD.getTable().grabFocus();
                tableD.setSelectedRow(tableD.getRowCount() - 1);
                tableD.setSelectedColumn(1);
            }
            return false;
        }
        detail.setActive(tNode.getRow(), true);
        return false;
    }
    
	/**
	 * 新增一条套餐主表数据
	 */
    public void onNew() {
        if (main.rowCount() > 0
                && StringUtil.isNullString(main.getItemString(main.rowCount() - 1, "PACKAGE_DESC"))) {
            return;
        }
        String packCode = ACIBadEventTool.getInstance().getNewPackCode();
        if (StringUtil.isNullString(packCode)) {
            this.messageBox_("取得套餐编码失败");
            return;
        }
        int row = tableM.addRow();
        tableM.setSelectedRow(row);
        main.setItem(row, "OPT_USER", Operator.getID());
        main.setItem(row, "OPT_TERM", Operator.getIP());
        main.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        main.setItem(row, "PACKAGE_CODE", packCode);
        main.setItem(row, "ACTIVE_FLG", "Y");
        main.setActive(row, false);
        tableM.setDSValue();
    }
    
	/**
	 * 删除一条数据
	 */
    public void onDelete() {
        String packCode = main.getItemString(mainRow, "PACKAGE_CODE");
        if ("TABLE_M".equalsIgnoreCase(tableName)) {
            if (this.messageBox("提示信息", "确认删除吗", JOptionPane.YES_NO_OPTION) == 1) {
                return;
            }
            main.setActive(mainRow, true);
            main.deleteRow(mainRow);
            String[] sql = main.getUpdateSQL();
            detail.setFilter("PACKAGE_CODE='" + packCode + "'");
            detail.filter();
            int count = detail.rowCount();
            for (int i = count - 1; i > -1; i--) {
                detail.deleteRow(i);
            }
            String[] detailSql = detail.getUpdateSQL();
            sql = StringTool.copyArray(sql, detailSql);
            TParm inParm = new TParm();
            Map inMap = new HashMap();
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm result = TIOM_AppServer.executeAction("action.aci.ACIBadEventAction", "onSave", inParm);
            if (result.getErrCode() != 0) {
                this.messageBox(result.getErrText());
                return;
            }
            detail.resetModify();
            main.resetModify();
            tableM.setDSValue();
            tableD.setDSValue();
            return;
        } else {
            int row = -1;
            row = tableD.getSelectedRow();
            if (StringUtil.isNullString(detail.getItemString(row, "USER_ID"))) {
                return;
            }
            detail.deleteRow(row);
            String[] sql = detail.getUpdateSQL();
            TParm inParm = new TParm();
            Map inMap = new HashMap();
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm result = TIOM_AppServer.executeAction("action.aci.ACIBadEventAction", "onSave", inParm);
            if (result.getErrCode() != 0) {
                this.messageBox(result.getErrText());
                return;
            } else {
                this.messageBox("P0003");// 删除成功
            }
            detail.resetModify();
            detail.setFilter("PACKAGE_CODE='" + packCode + "'");
            detail.filter();
            tableD.setDSValue();
            main.setActive(mainRow, true);
            tableM.setDSValue();
        }
    }
    
	/**
	 * 保存
	 */
    public void onSave() {
        tableM.acceptText();
        tableD.acceptText();
        for (int i = 0; i < detail.rowCount(); i++) {
            String userId = detail.getItemString(i, "USER_ID");
            if (userId == null || userId.equals("")) {
                detail.setActive(i, false);
            }
        }
        String[] sql = main.getUpdateSQL();
        String[] detailSql = detail.getUpdateSQL();
        sql = StringTool.copyArray(sql, detailSql);
//        for (int i = 0; i < sql.length; i++) {
//            System.out.println("----------sql[" + i + "]------" + sql[i]);
//        }
        if (sql == null || sql.length < 1) {
            this.messageBox_("没有找到保存的数据");
            return;
        }
        TParm inParm = new TParm();
        Map inMap = new HashMap();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        TParm result = TIOM_AppServer.executeAction("action.aci.ACIBadEventAction", "onSave", inParm);
        if (result.getErrCode() < 0) {}
        this.messageBox("P0001");// 保存成功
        onClear();
        return;
    }
    
    /**
     * 清空事件
     */
    public void onClear() {
        tableM.removeRowAll();
        tableD.removeRowAll();
        initData();
    }
}
