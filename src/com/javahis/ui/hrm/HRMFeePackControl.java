package com.javahis.ui.hrm;

import java.awt.Component;
import javax.swing.JOptionPane;

import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMFeePackTool;
import jdo.hrm.HRMPackageD;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
 * <p>Title: 健康检查套餐设定控制类</p>
 *
 * <p>Description: 健康检查套餐设定控制类</p>
 *
 * <p>Copyright: javahis 20090922</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMFeePackControl extends TControl{
	//主、细TABLE
	private TTable mainTable,detailTable;
	//套餐主项对象
	private TDataStore main;
	//套餐细相对象
	private HRMPackageD detail;
	//删除按钮用的表名变量
	private String tableName;
	//套餐主表单击行数
	private int mainRow;
	//套餐下拉框
	private TTextFormat packageCombo;
	
	/**
	 * 初始化事件
	 */
	public void onInit() {
		super.onInit();
		//初始化控件
		initComponent();
		//初始化数据
		initData();
	}
	
	/**
	 * 清空事件
	 */
    public void onClear() {
        mainTable.removeRowAll();
        detailTable.removeRowAll();
        // 初始化套餐主项
        main = new TDataStore();
        main.setSQL(HRMFeePackTool.INIT_PACK_MAIN);
        main.retrieve();
        main.setSort("PACKAGE_DESC ASC");// add by wanglong 20130325
        main.sort();
        // 初始化套餐细相
        detail = new HRMPackageD();
        detail.setSQL(HRMFeePackTool.INIT_PACK_DETAIL.replace("#", ""));
        detail.retrieve();
        mainTable.setDataStore(main);
        mainTable.setDSValue();
        detailTable.setDataStore(detail);
        detailTable.setDSValue();
        this.setValue("PACKAGE_DESC", "");
        
        // add by wangb 2016/07/5 健检套餐需要根据不同登录角色筛选
        String filter = "";
        if (this.getPopedem("PIC")) {
        	filter = " ROLE_TYPE = 'PIC'";
        } else if (this.getPopedem("H")) {
        	filter = " ROLE_TYPE = 'H'";
        }
        mainTable.setFilter(filter);
        mainTable.filter();
    }
    
	/**
	 * 初始化控件
	 */
    private void initComponent() {
        mainTable = (TTable) this.getComponent("MAIN_TABLE");
        // 套餐主项值改变事件
        mainTable.addEventListener("MAIN_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                                   "onMainValueChanged");
        // 套餐主项CHECK_BOX事件
        mainTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onMainCheckClicked");
        detailTable = (TTable) this.getComponent("DETAIL_TABLE");
        // 套餐细相新增医嘱事件
        detailTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                     "onDetailCreateEditComponent");
        // 套餐细相值改变事件
        detailTable.addEventListener("DETAIL_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                                     "onDetailValueChanged");
        packageCombo = (TTextFormat) this.getComponent("PACKAGE_DESC");
    }
    
	/**
	 * 初始化数据
	 */
    private void initData() {
        main = new TDataStore();
        detail = new HRMPackageD();
        main.setSQL(HRMFeePackTool.INIT_PACK_MAIN);
        main.retrieve();
        main.setSort("PACKAGE_DESC");// add by wanglong 20130224
        main.sort();// add by wanglong 20130224
        mainTable.setDataStore(main);
        mainTable.setDSValue();
        detail.setSQL(HRMFeePackTool.INIT_PACK_DETAIL.replace("#", ""));
        detail.retrieve();
        
        // add by wangb 2016/07/5 健检套餐需要根据不同登录角色筛选
        String filter = "";
        if (this.getPopedem("PIC")) {
        	filter = "ROLE_TYPE = 'PIC'";
        } else if (this.getPopedem("H")) {
        	filter = "ROLE_TYPE = 'H'";
        }
        main.setFilter(filter);
        main.filter();
        
		String roleType = "";
		if (this.getPopedem("SYSDBA")) {
			roleType = HRMCompanyTool.ALL_ROLE_TYPE.replace(",", "','");
		} else {
			roleType = this.getPopedemParm().getValue("ID", 0);
		}
        
		// 查询套餐信息
		TParm pakcageData = HRMFeePackTool.getInstance().selectHrmPackageByRoleType(roleType);
        packageCombo.setPopupMenuData(pakcageData);
		packageCombo.setComboSelectRow();
		packageCombo.popupMenuShowData();
    }
	
	/**
	 * 主TABLE点击事件,根据选定的套餐代码，初始化套餐细相信息
	 */
    public void onMainClick() {
        tableName = "MAIN_TABLE";
        int row = mainTable.getSelectedRow();
        String packCode = main.getItemString(row, "PACKAGE_CODE");
        main.setActive(row, false);
        detail.setSQL(HRMFeePackTool.INIT_PACK_DETAIL.replace("#", packCode));
        detail.retrieve();
        detail.setFilter("PACKAGE_CODE='" + packCode + "' AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='')");
        detail.filter();
        String temp = detail.getItemString(detail.rowCount() - 1, "ORDER_CODE");
        if (!StringUtil.isNullString(temp) || detail.rowCount() < 1) {
            row = detail.insertRow();
            detail.setItem(row, "PACKAGE_CODE", packCode);
            detail.setItem(row, "SEQ", detail.getItemInt(row, "#ID#"));
            detail.setItem(row, "CHECK_SEQ", null);
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
     * 右击MENU弹出事件
     * @param tableName
     */
    public void showPopMenu() {
        detailTable.setPopupMenuSyntax("显示集合医嘱细相,onOrderSetShow");
    }
    
    /**
     * 修改医嘱细相，套餐细相TABLE右击事件，调出细相列表，允许修改细相信息
     */
    public void onOrderSetShow() {
        TParm parm = new TParm();
        int row = detailTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        String filterString = detail.getFilter();
        String orderSetCode = detail.getItemString(row, "ORDERSET_CODE");
        int groupNo = detail.getItemInt(row, "ORDERSET_GROUP_NO");
        if (StringUtil.isNullString(orderSetCode)) {
            return;
        }
        String packCode = main.getItemString(this.mainRow, "PACKAGE_CODE");
        parm.setData("PACKAGE_CODE", packCode);
        parm.setData("ORDERSET_CODE", orderSetCode);
        parm.setData("ORDERSET_GROUP_NO", groupNo);
        Object obj = this.openDialog("%ROOT%\\config\\hrm\\HRMOrderSetShow.x", parm);
        // ============xueyf modify 20120228 start
        // 设置体检套餐明细未保存时查看集合医嘱细项，关闭细项窗口后体检套餐明细被清空BUG
        if (obj == null) {
            return;
        }
        // ============xueyf modify 20120228 stop
        detail.setSQL(HRMFeePackTool.INIT_PACK_DETAIL.replace("#", packCode));
        detail.retrieve();
        detail.setFilter(filterString);
        detail.filter();
        String temp = detail.getItemString(detail.rowCount() - 1, "ORDER_CODE");
        if (!StringUtil.isNullString(temp) || detail.rowCount() < 1) {
            row = detail.insertRow();
            detail.setItem(row, "PACKAGE_CODE", packCode);
            detail.setItem(row, "SEQ", detail.getItemInt(row, "#ID#"));
            detail.setItem(row, "OPT_USER", Operator.getID());
            detail.setItem(row, "OPT_TERM", Operator.getIP());
            detail.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
            detail.setActive(row, false);
        }
        detailTable.setDSValue();
        main.setItem(mainRow, "TC_TOT_AMT", detail.getPackgeTotAmt());// 原价
        main.setItem(mainRow, "TC_AR_AMT", detail.getPackgeArAmt());// 套餐价
        main.setActive(mainRow, true);
        mainTable.setDSValue();
    }
    
	/**
	 * 主表值改变事件,套餐主项套餐名称修改，自动生成简拼代码保存。
	 * @param tNode
	 */
    public boolean onMainValueChanged(TTableNode tNode) {
        int row = tNode.getRow();
        int column = tNode.getColumn();
        String colName = mainTable.getParmMap(column);
        // String packCode = main.getItemString(row, "PACKAGE_CODE");
        if ("PACKAGE_DESC".equalsIgnoreCase(colName)) {
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
	 * 添加SYS_FEE弹出窗口
	 * @param com
	 * @param row
	 * @param column
	 */
    public void onDetailCreateEditComponent(Component com, int row, int column) {
        // 求出当前列号，只允许在医嘱名称列新增一条医嘱
        column = detailTable.getColumnModel().getColumnIndex(column);
        String columnName = detailTable.getParmMap(column);
        if (!"ORDER_DESC".equalsIgnoreCase(columnName)) {
            return;
        }
        if (!(com instanceof TTextField)) {
            return;
        }
        // String packCode = main.getItemString(this.mainRow, "PACKAGE_CODE");
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        TParm parm = new TParm();
        parm.setData("HRM_TYPE", "ANYCHAR");
        // 给table上的新text增加sys_fee弹出窗口
        textfield.setPopupMenuParameter("ORDER",
                                        getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
                                        parm);
        // 给新text增加接受sys_fee弹出窗口的回传值
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popOrderReturn");
    }
    
	/**
	 * 套餐细相新增医嘱方法,如有细相，则也新增细相信息，并只显示主项
	 * @param tag
	 * @param obj
	 */
    public void popOrderReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        // 判断是否是药品(PHA_W药品)
        String orderCat1Code = parm.getValue("ORDER_CAT1_CODE");
        int row = detailTable.getSelectedRow();
        int oldRow = row;
        if (!StringUtil.isNullString(detail.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        String order_code = parm.getValue("ORDER_CODE");
        String order_desc = parm.getValue("ORDER_DESC");
        // 判断是否有重复数据
        for (int i = 0; i < detailTable.getDataStore().rowCount(); i++) {
            if (i == detailTable.getSelectedRow()) {
                continue;
            }
            if (order_code.equals(detailTable.getDataStore().getItemData(i, "ORDER_CODE"))
                    && detailTable.getDataStore().getItemData(i, "SETMAIN_FLG").equals("Y")) {
                this.messageBox(order_desc + "(" + order_code + ") 已存在");
                return;
            }
        }
        detailTable.acceptText();
        detail.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
        detail.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
        detail.setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC"));
        detail.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION"));
        detail.setItem(row, "SEQ", detail.getItemInt(row, "#ID#"));
        detail.setItem(row, "ORDERSET_CODE", parm.getValue("ORDERSET_CODE"));
        detail.setItem(row, "DISPENSE_QTY", 1.0);
        detail.setItem(row, "DISPENSE_UNIT", parm.getValue("UNIT_CODE"));
        detail.setItem(row, "ORDER_CAT1_CODE", orderCat1Code);// modify by wanglong 20130523
        if ("PHA_W".equals(orderCat1Code) || "MAT".equals(orderCat1Code)) {
            // detail.setItem(row, "ORDER_CAT1_CODE", orderCat1Code);
            detail.setItem(row, "MEDI_QTY", 1.0);
            detail.setItem(row, "MEDI_UNIT", parm.getValue("UNIT_CODE"));
            detail.setItem(row, "FREQ_CODE", "STAT");
            detail.setItem(row, "ROUTE_CODE", "PO");
            detail.setItem(row, "TAKE_DAYS", 1);
            detail.setItem(row, "ORDERSET_CODE", parm.getValue("ORDER_CODE"));
            detail.setItem(row, "SETMAIN_FLG", "Y");
        }
        detail.setItem(row, "ORIGINAL_PRICE", parm.getValue("OWN_PRICE"));
        detail.setItem(row, "PACKAGE_PRICE", parm.getValue("OWN_PRICE"));
        detail.setItem(row, "OPTITEM_CODE", parm.getValue("OPTITEM_CODE"));
        detail.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE"));
        detail.setItem(row, "OPT_USER", Operator.getID());
        detail.setItem(row, "OPT_TERM", Operator.getIP());
        detail.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        detail.setActive(row, true);
        row = detail.insertRow();
        detail.setItem(row, "PACKAGE_CODE", detail.getItemString(0, "PACKAGE_CODE"));
        detail.setActive(row, false);
        if (!parm.getBoolean("ORDERSET_FLG")) {
            double totAmt = detail.getPackgeTotAmt();// 原价
            double arAmt = detail.getPackgeArAmt();// 套餐价
            main.setItem(mainRow, "TC_TOT_AMT", totAmt);
            main.setItem(mainRow, "TC_AR_AMT", arAmt);
            main.setActive(mainRow, true);
            mainTable.setDSValue();
            detailTable.getTable().grabFocus();
            detailTable.setSelectedRow(oldRow);
            detailTable.setDSValue();
        } else {
            String sql = HRMFeePackTool.QUERY_ORDERSET.replace("#", parm.getValue("ORDER_CODE"));
            // System.out.println("sql===ffff========"+sql);
            detail.setItem(oldRow, "ORDERSET_CODE", parm.getValue("ORDER_CODE"));
            detail.setItem(oldRow, "SETMAIN_FLG", "Y");
            int groupNo = detail.getMaxGroupNo();
            detail.setItem(oldRow, "ORDERSET_GROUP_NO", groupNo);
            TParm orderSet = new TParm(TJDODBTool.getInstance().select(sql));
            if (orderSet == null || orderSet.getErrCode() != 0) {
                this.messageBox_("选择数据错误");
                return;
            }
            int count = orderSet.getCount("ORDER_CODE");
            for (int i = 0; i < count; i++) {
                detail.setItem(row, "ORDER_CODE", orderSet.getValue("ORDER_CODE", i));
                detail.setItem(row, "SEQ", detail.getItemInt(row, "#ID#"));
                detail.setItem(row, "ORDER_DESC", orderSet.getValue("ORDER_DESC", i));
                detail.setItem(row, "GOODS_DESC", orderSet.getValue("GOODS_DESC", i));
                detail.setItem(row, "SPECIFICATION", orderSet.getValue("SPECIFICATION", i));
                detail.setItem(row, "ORDERSET_CODE", orderSet.getValue("ORDERSET_CODE", i));
                detail.setItem(row, "DISPENSE_QTY", orderSet.getDouble("DOSAGE_QTY", i));
                detail.setItem(row, "DISPENSE_UNIT", orderSet.getValue("UNIT_CODE", i));
                detail.setItem(row, "ORIGINAL_PRICE", orderSet.getDouble("OWN_PRICE", i));
                detail.setItem(row, "PACKAGE_PRICE", orderSet.getDouble("OWN_PRICE", i));
                detail.setItem(row, "OPTITEM_CODE", orderSet.getValue("OPTITEM_CODE", i));
                detail.setItem(row, "ORDER_CAT1_CODE", orderSet.getValue("ORDER_CAT1_CODE", i));// add by wanglong 20130523
                detail.setItem(row, "SETMAIN_FLG", "N");
                detail.setItem(row, "HIDE_FLG", "Y");
                detail.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE"));
                detail.setItem(row, "ORDERSET_GROUP_NO", groupNo);
                detail.setItem(row, "OPT_USER", Operator.getID());
                detail.setItem(row, "OPT_TERM", Operator.getIP());
                detail.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
                detail.setActive(row, true);
                row = detail.insertRow();
                detail.setItem(row, "PACKAGE_CODE", detail.getItemString(0, "PACKAGE_CODE"));
                detail.setActive(row, false);
            }
            String packCode = main.getItemString(this.mainRow, "PACKAGE_CODE");
            detail.setFilter("PACKAGE_CODE='" + packCode
                    + "' AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='')");
            detail.filter();
            // System.out.println("after pop");
            detailTable.setDSValue();
            double totAmt = detail.getPackgeTotAmt();// 原价
            double arAmt = detail.getPackgeArAmt();// 套餐价
            main.setItem(mainRow, "TC_TOT_AMT", totAmt);
            main.setItem(mainRow, "TC_AR_AMT", arAmt);
            main.setActive(mainRow, true);
            mainTable.setDSValue();
            detailTable.getTable().grabFocus();
            detailTable.setSelectedRow(detailTable.getRowCount() - 1);
            detailTable.setSelectedColumn(1);
        }
        parm = null;
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
        if (!StringUtil.isNullString(detail.getItemString(row, "ORDER_CODE"))) {
            if ("ORDER_DESC".equalsIgnoreCase(colName)) {
                this.messageBox_("已开立医嘱不得重复开立");
                return true;
            }
        } else {
            return true;
        }
        if ("DEPT_ATTRIBUTE".equalsIgnoreCase(colName)) {
            String orderCode = detail.getItemString(row, "ORDER_CODE");
            int groupNo = detail.getItemInt(row, "ORDERSET_GROUP_NO");
            String value = tNode.getValue() + "";
            // System.out.println();
            detail.updateDeptAttribute(value, packCode, orderCode, groupNo);
            detailTable.acceptText();
            return false;
        }
        if ("PACKAGE_PRICE_MAIN".equalsIgnoreCase(colName)) {
            double price = StringTool.getDouble(tNode.getValue() + "");
            if (price < 0) {
                return true;
            }
            double oldPrice = StringTool.getDouble(tNode.getOldValue() + "");
            double nowPrice = main.getItemDouble(mainRow, "TC_TOT_AMT") - oldPrice + price;
            main.setItem(this.mainRow, "TC_TOT_AMT", nowPrice);
            main.setActive(row, true);
            mainTable.setDSValue(mainRow);
            return false;
        }
        if ("CHECK_SEQ".equalsIgnoreCase(colName)) {
            if (detail.getItemString(tNode.getRow(), "ORDER_CODE").length() == 0) {
                return true;
            }
        }
        if ("DISPENSE_QTY".equalsIgnoreCase(colName)) {// add by wanglong 20130523
            String orderCat1Code = detail.getItemString(tNode.getRow(), "ORDER_CAT1_CODE");
            if (!orderCat1Code.matches("PHA.*") && !"MAT".equals(orderCat1Code)) {
                this.messageBox("集合医嘱不能更改数量");
                return true;
            }
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
        String packCode = HRMFeePackTool.getInstance().getNewPackCode();
        if (StringUtil.isNullString(packCode)) {
            this.messageBox_("取得套餐编码失败");
            return;
        }
        int row = mainTable.addRow();
        mainTable.setSelectedRow(row);
        main.setItem(row, "OPT_USER", Operator.getID());
        main.setItem(row, "OPT_TERM", Operator.getIP());
        main.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        main.setItem(row, "PACKAGE_CODE", packCode);
        // ===================add by wanglong 20130408
        String sql =
                "SELECT SUBCLASS_CODE AS ID,SUBCLASS_DESC AS NAME,PY1 FROM EMR_TEMPLET "
                        + " WHERE HRM_FLG='Y' AND SYSTEM_CODE='HRM' AND SUBCLASS_DESC LIKE '%总检%'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            this.messageBox("查询总检模板信息出错");
            return;
        }
        main.setItem(row, "TOT_MR_CODE", result.getValue("ID", 0));
        // ===================add end
        main.setItem(row, "ACTIVE_FLG", "Y");
        
        // add by wangb 2016/07/5 登录角色
        String roleType = this.getPopedemParm().getValue("ID", 0);
        if (this.getPopedem("SYSDBA")) {
        	roleType = "H";
        }
        main.setItem(row, "ROLE_TYPE", roleType);
        
        main.setActive(row, false);
        mainTable.setDSValue();
    }
    
	/**
	 * 删除一条数据
	 */
    public void onDelete() {
        String packCode = main.getItemString(mainRow, "PACKAGE_CODE");
        // this.messageBox_(tableName);
        if ("MAIN_TABLE".equalsIgnoreCase(tableName)) {
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
            TParm result = new TParm(TJDODBTool.getInstance().update(sql));
            if (result.getErrCode() != 0) {
                // this.messageBox_(result.getErrText());
                return;
            }
            detail.resetModify();
            main.resetModify();
            mainTable.setDSValue();
            detailTable.setDSValue();
            return;
        }
        int row = -1;
        row = detailTable.getSelectedRow();
        if (StringUtil.isNullString(detail.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        if (!TypeTool.getBoolean(detail.getItemData(row, "SETMAIN_FLG"))) {
            detail.deleteRow(row);
        } else {
            String orderSetCode = detail.getItemString(row, "ORDERSET_CODE");
            int groupNo = detail.getItemInt(row, "ORDERSET_GROUP_NO");
            String filterString = detail.getFilter();
            detail.setFilter("PACKAGE_CODE='" + packCode + "'");
            detail.filter();
            int count = detail.rowCount();
            for (int i = count - 1; i > -1; i--) {
                if (orderSetCode.equalsIgnoreCase(detail.getItemString(i, "ORDERSET_CODE"))
                        && groupNo == detail.getItemInt(i, "ORDERSET_GROUP_NO")) {
                    detail.deleteRow(i);
                }
            }
            detail.setFilter(filterString);
            detail.filter();
        }
        String[] sql = detail.getUpdateSQL();
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() != 0) {
            this.messageBox_(result.getErrText());
            return;
        }
        detail.resetModify();
        detail.setFilter("PACKAGE_CODE='" + packCode + "' AND (SETMAIN_FLG='Y' OR SETMAIN_FLG='')");
        detail.filter();
        detailTable.setDSValue();
        double totAmt = detail.getPackgeTotAmt();// 原价
        double arAmt = detail.getPackgeArAmt();// 套餐价
        main.setItem(mainRow, "TC_TOT_AMT", totAmt);
        main.setItem(mainRow, "TC_AR_AMT", arAmt);
        main.setActive(mainRow, true);
        mainTable.setDSValue();
    }
    
	/**
	 * 保存
	 */
    public void onSave() {
        mainTable.acceptText();
        detailTable.acceptText();
        for (int i = 0; i < detail.rowCount(); i++) {
            String orderCode = detail.getItemString(i, "ORDER_CODE");
            if (orderCode == null || orderCode.equals("")) {
                detail.setActive(i, false);
            }
        }
        String[] sql = main.getUpdateSQL();
        String[] detailSql = detail.getUpdateSQL();
        String updateTotAmtArAmt =
                "UPDATE HRM_PACKAGEM A SET (A.TC_TOT_AMT,A.TC_AR_AMT) = "
                        + "  (SELECT NVL(ROUND(SUM(B.DISPENSE_QTY*B.ORIGINAL_PRICE),2),0) TC_TOT_AMT, "
                        + "          NVL(ROUND(SUM(B.DISPENSE_QTY*B.PACKAGE_PRICE),2),0) TC_AR_AMT "
                        + "     FROM HRM_PACKAGED B "
                        + "    WHERE A.PACKAGE_CODE = B.PACKAGE_CODE)";
        sql = StringTool.copyArray(sql, new String[]{updateTotAmtArAmt });
        sql = StringTool.copyArray(sql, detailSql);
        if (sql == null || sql.length < 1) {
            this.messageBox_("没有找到保存的数据");
            return;
        }
        TParm result;
        for (int i = 0; i < sql.length; i++) {
            // System.out.println("sql============" + sql[i]);
            result = new TParm(TJDODBTool.getInstance().update(sql[i]));
            if (result.getErrCode() != 0) {
                this.messageBox("E0001");// 保存失败
                return;
            }
        }
        this.messageBox("P0001");// 保存成功
        onClear();
        return;
    }
    
	/**
	 * 模板名称查询事件
	 */
    public void onQueryByCode() {
        String packCode = this.getValueString("PACKAGE_DESC");
        main.setFilter("PACKAGE_CODE='" + packCode + "'");
        main.filter();
        mainTable.setDSValue();
        detailTable.removeRowAll();
    }
	
    
    //=====================  chenxi modify 20130219 添加复制功能
    /**
     * 复制
     */
    public void onNewCopy() {
        int selectRow = mainTable.getSelectedRow();
        if (selectRow < 0) {
            messageBox("选择要复制的主项");
            return;
        }
        // 取主项的主键值 PACKAGE_CODE;PACKAGE_DESC;TC_TOT_AMT
        String packCode = mainTable.getItemString(selectRow, "PACKAGE_CODE");
        String packageDesc = mainTable.getItemString(selectRow, "PACKAGE_DESC");
        this.onNew();
        int addRow = mainTable.getRowCount() - 1;
        String packageCode = mainTable.getItemString(addRow, "PACKAGE_CODE");
        // 保存主表
        mainTable.setItem(addRow, 2, "复制" + packageDesc);
        main.setItem(addRow, "TC_TOT_AMT", mainTable.getItemDouble(selectRow, "TC_TOT_AMT"));
        main.setItem(addRow, "TC_AR_AMT", mainTable.getItemDouble(selectRow, "TC_AR_AMT"));// add by wanglong 20130523
        main.setItem(addRow, "PY1", SystemTool.getInstance().charToCode("复制" + packageDesc));
        main.setActive(addRow, true);
        // 细表保存
        String sql = "SELECT *  FROM HRM_PACKAGED WHERE PACKAGE_CODE ='" + packCode + "' ";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        for (int i = 0; i < parm.getCount(); i++) {
            int row = detail.insertRow();
            detail.setItem(row, "PACKAGE_CODE", packageCode);
            detail.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE", i));
            detail.setItem(row, "SEQ", parm.getInt("SEQ", i));
            detail.setItem(row, "ORDERSET_CODE", parm.getValue("ORDERSET_CODE", i));
            detail.setItem(row, "DISPENSE_QTY", parm.getDouble("DISPENSE_QTY", i));
            detail.setItem(row, "ORIGINAL_PRICE", parm.getDouble("ORIGINAL_PRICE", i));
            detail.setItem(row, "PACKAGE_PRICE", parm.getDouble("PACKAGE_PRICE", i));
            detail.setItem(row, "DISPENSE_UNIT", parm.getValue("DISPENSE_UNIT", i));
            detail.setItem(row, "OPTITEM_CODE", parm.getValue("OPTITEM_CODE", i));
            detail.setItem(row, "OPT_USER", Operator.getID());
            detail.setItem(row, "OPT_TERM", Operator.getIP());
            detail.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
            detail.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE", i));
            detail.setItem(row, "ORDERSET_GROUP_NO", parm.getInt("ORDERSET_GROUP_NO", i));
            detail.setItem(row, "SETMAIN_FLG", parm.getValue("SETMAIN_FLG", i));
            detail.setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC", i));
            detail.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC", i));
            detail.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION", i));
            detail.setItem(row, "DEPT_ATTRIBUTE", parm.getValue("DEPT_ATTRIBUTE", i));
            detail.setItem(row, "CHECK_SEQ", parm.getInt("CHECK_SEQ", i));
            detail.setItem(row, "HIDE_FLG", parm.getValue("HIDE_FLG", i));
            detail.setItem(row, "MEDI_QTY", parm.getDouble("MEDI_QTY", i));
            detail.setItem(row, "MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
            detail.setItem(row, "FREQ_CODE", parm.getValue("FREQ_CODE", i));
            detail.setItem(row, "ROUTE_CODE", parm.getValue("ROUTE_CODE", i));
            detail.setItem(row, "TAKE_DAYS", parm.getInt("TAKE_DAYS", i));
            detail.setItem(row, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE", i));
            detail.setActive(row, true);
        }
        this.onSaveCopy();
    }

	/**
	 * 复制保存
	 */
    public void onSaveCopy() {
        mainTable.acceptText();
        detailTable.acceptText();
        for (int i = 0; i < detail.rowCount(); i++) {
            String orderCode = detail.getItemString(i, "ORDER_CODE");
            if (orderCode == null || orderCode.equals("")) {
                detail.setActive(i, false);
            }
        }
        String[] sql = main.getUpdateSQL();
        String[] detailSql = detail.getUpdateSQL();
        sql = StringTool.copyArray(sql, detailSql);
        if (sql == null || sql.length < 1) {
            this.messageBox_("没有找到复制的数据");
            return;
        }
        TParm result;
        for (int i = 0; i < sql.length; i++) {
            result = new TParm(TJDODBTool.getInstance().update(sql[i]));
            if (result.getErrCode() != 0) {
                this.messageBox("复制失败");
                return;
            }
        }
        this.messageBox("复制成功");
        onClear();
        return;
    }
	//================  add by chenxi end
}
