package com.javahis.ui.hrm;

import java.awt.Component;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jdo.bil.BIL;
import jdo.hl7.Hl7Communications;
import jdo.hrm.HRMFeePackTool;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMPatAdm;
import jdo.hrm.HRMPatInfo;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Personal;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatHRMPackageD;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 医嘱批量新增，删除
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author Yuanxm
 * @version 1.0
 */
public class HRMOrderBatchControl
        extends TControl {

    private TTable table;
    private HRMOrder order;// 医嘱对象
    private HRMPatAdm adm;// 报到对象
    private HRMPatInfo pat;// 病患对象
    private TParm inParm;
    private String ctz;// 身份
    private String dept;// 科室
    private int[] commonInsertRows = new int[]{};// 记录新增的医嘱的行号 add by wanglong 20130226
    private String packageCode = "";// 套餐代码 add by wanglong 20130423
    private TextFormatHRMPackageD packageDetail;// 套餐细项（下拉框） add by wanglong 20130423
    private TParm risParm = new TParm();// add by wanglong 20140214 记录检查类医嘱信息，用于打印检查申请单

    /**
     * 初始化事件
     * 
     */
    public void onInit() {
        super.onInit();
        /**
         * 初始化控件
         */
        initComponent();
        initData();
        inParm = (TParm) this.getParameter();
        if (inParm.getValue("METHOD", 0).equals("ADD")) {
            this.setTitle("批量新增");
        } else if (inParm.getValue("METHOD", 0).equals("DELETE")) {
            this.setTitle("批量删除");
        }
        int patCount = inParm.getCount("MR_NO");
        if (patCount >= 1000) {
            this.messageBox("同时操作的人员数不要超过1000！");
            this.closeWindow();
        }
        ctz = Personal.getDefCtz();
        dept = Operator.getDept();
    }

    public void initComponent() {
        table = (TTable) this.getComponent("TABLE");
        table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this, "onOrderEditComponent");
        table.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE, this, "onValueChanged");
        packageDetail = (TextFormatHRMPackageD) this.getComponent("ORDER_CODE");// add by wanglong
                                                                                // 20130423
    }

    /**
     * 值改变事件
     * 
     * @param tNode
     *            TTableNode
     * @return boolean
     */
    public boolean onValueChanged(TTableNode tNode) {
        table.acceptText();
        int column = tNode.getColumn();
        int row = tNode.getRow();
        String colName = table.getParmMap(column);
        if (tNode.getValue().equals(tNode.getOldValue())) return true;
        // 科别属性
        if ("DEPT_ATTRIBUTE".equals(colName)) {
            order.setItem(row, "DEPT_ATTRIBUTE", tNode.getValue());
            this.getTTable("TABLE").setDSValue(row);
            return false;
        }
        if ("OWN_PRICE_MAIN".equals(colName)) {// add by wanglong 20130409
            TParm parm = tNode.getTable().getDataStore().getRowParm(tNode.getRow());
            if (parm.getValue("CAT1_TYPE").equals("PHA")
                    || (parm.getValue("ORDER_CAT1_CODE").equals("MAT") && "N".equals(parm
                            .getValue("HIDE_FLG")))) {
                order.setItem(row, "OWN_PRICE", tNode.getValue());
                this.getTTable("TABLE").setDSValue(row);
            } else if (this.isOrderSet(parm)) {
                messageBox("集合医嘱不能更改单价");
                return true;
            }
        }
        if ("DISPENSE_QTY".equals(colName)) {
            if (!tNode.getValue().toString().matches("\\d+")) {// add by wanglong 20130416
                return true;
            }
            TParm parm = tNode.getTable().getDataStore().getRowParm(tNode.getRow());
            if (parm.getValue("CAT1_TYPE").equals("PHA")
                    || (parm.getValue("ORDER_CAT1_CODE").equals("MAT") && "N".equals(parm
                            .getValue("HIDE_FLG")))) {
                order.setItem(row, "DISPENSE_QTY", tNode.getValue());
                this.getTTable("TABLE").setDSValue(row);
            } else {
                if (this.isOrderSet(parm)) {// modify by wanglong 20130408
                    if ((parm.getValue("CAT1_TYPE").equals("LIS") || parm.getValue("CAT1_TYPE")
                            .equals("RIS")) && parm.getValue("SETMAIN_FLG").equals("Y")) {// modify
                                                                                          // by
                                                                                          // wanglong
                                                                                          // 20130412
                        messageBox("检验检查不能更改数量");
                        return true;
                    }
                    int groupNo = parm.getInt("ORDERSET_GROUP_NO");
                    String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
                    int newRow[] = order.getNewRows(buff);
                    for (int i : newRow) {
                        TParm linkParm = order.getRowParm(i, buff);
                        if (!order.isActive(i, buff)) continue;
                        // 找到过滤缓冲区中此医嘱的唯一ID
                        int filterId = (Integer) order.getItemData(i, "#ID#", buff);
                        // 找到过主冲区中此医嘱的唯一ID
                        int primaryId =
                                (Integer) order.getItemData(tNode.getRow(), "#ID#", order.PRIMARY);
                        double newQty = TypeTool.getDouble(tNode.getValue().toString());
                        if (filterId == primaryId) {
                            order.setItem(i, "DISPENSE_QTY", newQty, buff);// modify by wanglong
                                                                           // 20130416
                            continue;// 集合医嘱主项
                        }
                        table.setItem(row, "OWN_AMT_MAIN",
                                      TypeTool.getDouble(table.getItemData(row, "OWN_PRICE_MAIN"))
                                              * newQty);
                        if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
                            double oldValue =
                                    TypeTool.getDouble(order.getItemData(i, "DISPENSE_QTY", buff));
                            order.setItem(i, "DISPENSE_QTY", oldValue * newQty, buff);// modify by
                                                                                      // wanglong
                                                                                      // 20130416
                            this.getTTable("TABLE").setDSValue(i);
                        }
                    }
                }
            }
            return false;
        }
        // 开立科室
        if ("DEPT_CODE".equals(colName)) {
            TParm parm = tNode.getTable().getDataStore().getRowParm(tNode.getRow());
            if (parm.getValue("CAT1_TYPE").equals("PHA")
                    || (parm.getValue("ORDER_CAT1_CODE").equals("MAT") && "N".equals(parm
                            .getValue("HIDE_FLG")))) {
                order.setItem(row, "DEPT_CODE", tNode.getValue());
                this.getTTable("TABLE").setDSValue(row);
            } else {
                if (this.isOrderSet(parm)) {
                    int groupNo = parm.getInt("ORDERSET_GROUP_NO");
                    String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
                    int newRow[] = order.getNewRows(buff);
                    for (int i : newRow) {
                        TParm linkParm = order.getRowParm(i, buff);
                        if (!order.isActive(i, buff)) continue;
                        // 找到过滤缓冲区中此医嘱的唯一ID
                        int filterId = (Integer) order.getItemData(i, "#ID#", buff);
                        // 找到过主冲区中此医嘱的唯一ID
                        int primaryId =
                                (Integer) order.getItemData(tNode.getRow(), "#ID#", order.PRIMARY);
                        if (filterId == primaryId) continue;
                        if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
                            order.setItem(i, "DEPT_CODE", tNode.getValue());
                            this.getTTable("TABLE").setDSValue(i);
                        }
                    }
                }
            }
            return false;
        }
        // 执行科室
        if ("EXEC_DEPT_CODE".equals(colName)) {
            TParm parm = tNode.getTable().getDataStore().getRowParm(tNode.getRow());
            if (parm.getValue("CAT1_TYPE").equals("PHA")
                    || (parm.getValue("ORDER_CAT1_CODE").equals("MAT") && "N".equals(parm
                            .getValue("HIDE_FLG")))) {
                order.setItem(row, "EXEC_DEPT_CODE", tNode.getValue());
                this.getTTable("TABLE").setDSValue(row);
            } else {
                if (this.isOrderSet(parm)) {
                    int groupNo = parm.getInt("ORDERSET_GROUP_NO");
                    String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
                    int newRow[] = order.getNewRows(buff);
                    for (int i : newRow) {
                        TParm linkParm = order.getRowParm(i, buff);
                        if (!order.isActive(i, buff)) continue;
                        // 找到过滤缓冲区中此医嘱的唯一ID
                        int filterId = (Integer) order.getItemData(i, "#ID#", buff);
                        // 找到过主冲区中此医嘱的唯一ID
                        int primaryId =
                                (Integer) order.getItemData(tNode.getRow(), "#ID#", order.PRIMARY);
                        if (filterId == primaryId) continue;
                        if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
                            order.setItem(i, "EXEC_DEPT_CODE", tNode.getValue());
                            this.getTTable("TABLE").setDSValue(i);
                        }
                    }
                }
            }
            return false;
        }
        return false;
    }

    public void initData() {
        order = new HRMOrder();
        order.onQuery();
        int row = table.getSelectedRow();
        row = order.insertRow();
        order.setItem(row, "SEQ", order.getItemInt(row, "#ID#"));
        order.setItem(row, "CHECK_SEQ", null);
        order.setItem(row, "OPT_USER", Operator.getID());
        order.setItem(row, "OPT_TERM", Operator.getIP());
        order.setItem(row, "SETMAIN_FLG", "Y");
        order.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        order.setActive(row, false);
        table.setDataStore(order);
        table.setDSValue();
        pat = new HRMPatInfo();
        pat.onQuery();
        adm = new HRMPatAdm();
        adm.onQuery();
        risParm = new TParm();// add by wanglong 20140214
    }

    /**
     * 添加SYS_FEE弹出窗口
     * 
     * @param com
     *            Component
     * @param row
     *            int
     * @param column
     *            int
     */
    public void onOrderEditComponent(Component com, int row, int column) {
        // 求出当前列号
        column = table.getColumnModel().getColumnIndex(column);
        String columnName = table.getParmMap(column);
        if (!"ORDER_DESC".equalsIgnoreCase(columnName)) {
            return;
        }
        if (!(com instanceof TTextField)) {
            return;
        }
        if (!StringUtil.isNullString(order.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        TParm parm = new TParm();
        parm.setData("HRM_TYPE", "ANYCHAR");
        // 给table上的新text增加sys_fee弹出窗口
        textfield.setPopupMenuParameter("ORDER",
                                        getConfigParm()
                                                .newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
                                        parm);
        // 给新text增加接受sys_fee弹出窗口的回传值
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popOrderReturn");
    }

    /**
     * 套餐细相新增医嘱方法,如有细相，则也新增细相信息，并只显示主项
     * 
     * @param tag
     * @param obj
     */
    public void popOrderReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        int row = table.getSelectedRow();
        int oldRow = row;
        if (!StringUtil.isNullString(table.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        table.acceptText();
        order.setItem(row, "SEQ", order.getItemInt(row, "#ID#"));
        order.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
        order.setItem(row, "ORDERSET_CODE", parm.getValue("ORDERSET_CODE"));
        order.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
        order.setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC"));
        order.setItem(row, "DISPENSE_QTY", 1.0);
        order.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION"));
        order.setItem(row, "DISPENSE_UNIT", parm.getValue("UNIT_CODE"));
        order.setItem(row, "ORIGINAL_PRICE", parm.getDouble("OWN_PRICE"));
        order.setItem(row, "OWN_PRICE", parm.getDouble("OWN_PRICE"));
        order.setItem(row, "NHI_PRICE", parm.getDouble("NHI_PRICE"));// add by wanglong 20130316
        order.setItem(row, "RPTTYPE_CODE", parm.getData("RPTTYPE_CODE"));
        order.setItem(row, "OPTITEM_CODE", parm.getData("OPTITEM_CODE"));
        order.setItem(row, "DEV_CODE", parm.getData("DEV_CODE"));
        order.setItem(row, "MR_CODE", parm.getData("MR_CODE"));
        order.setItem(row, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
        order.setItem(row, "REXP_CODE", BIL.getRexpCode(parm.getValue("CHARGE_HOSP_CODE"), "H"));
        order.setItem(row, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
        order.setItem(row, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE"));
        if (StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE"))) {
            order.setItem(row, "EXEC_DEPT_CODE", Operator.getDept());
        } else {
            order.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE"));
        }
        // ====================add by wangong 20130422 自动带出科别属性
        String attrSql =
                "SELECT DISTINCT DEPT_ATTRIBUTE FROM HRM_PACKAGED WHERE ORDER_CODE = '"
                        + parm.getValue("ORDER_CODE") + "' ORDER BY DEPT_ATTRIBUTE";
        TParm attrParm = new TParm(TJDODBTool.getInstance().select(attrSql));
        if (attrParm.getErrCode() != 0) {
            this.messageBox("查询科别属性失败");
        }
        if (attrParm.getCount() > 0) {
            parm.setData("DEPT_ATTRIBUTE", attrParm.getValue("DEPT_ATTRIBUTE", 0));
        }
        // ====================add end
        order.setItem(row, "DEPT_ATTRIBUTE", parm.getData("DEPT_ATTRIBUTE"));
        order.setItem(row, "OPT_USER", Operator.getID());
        order.setItem(row, "OPT_TERM", Operator.getIP());
        order.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        order.setItem(row, "REGION_CODE", Operator.getRegion());
        order.setItem(row, "DR_CODE", Operator.getID());
        order.setItem(row, "DEPT_CODE", dept);
        order.setItem(row, "SETMAIN_FLG", "Y");
        order.setItem(row, "HIDE_FLG", "N");
        order.setItem(row, "CTZ1_CODE", ctz);
        order.setItem(row, "CTZ2_CODE", "");
        order.setItem(row, "CTZ3_CODE", "");
        order.setItem(row, "BILL_FLG", "N");
        order.setItem(row, "EXEC_FLG", "N");
        order.setActive(row, true);
        row = order.insertRow();
        order.setItem(row, "SETMAIN_FLG", "Y");
        order.setItem(row, "HIDE_FLG", "N");
        order.setItem(row, "BILL_FLG", "N");
        order.setItem(row, "EXEC_FLG", "N");
        order.setActive(row, false);
        if (!parm.getBoolean("ORDERSET_FLG")) {
            table.setDSValue();
            return;
        }
        String sql = HRMFeePackTool.QUERY_ORDERSET.replace("#", parm.getValue("ORDER_CODE"));
        order.setItem(oldRow, "ORDERSET_CODE", parm.getValue("ORDER_CODE"));
        order.setItem(oldRow, "SETMAIN_FLG", "Y");// 显示
        int groupNo = order.getMaxGroupNo();
        order.setItem(oldRow, "ORDERSET_GROUP_NO", groupNo);
        TParm orderSet = new TParm(TJDODBTool.getInstance().select(sql));
        if (orderSet == null || orderSet.getErrCode() != 0) {
            this.messageBox_("选择数据错误");
            return;
        }
        int count = orderSet.getCount("ORDER_CODE");
        for (int i = 0; i < count; i++) {
            order.setItem(row, "SEQ", order.getItemInt(row, "#ID#"));
            order.setItem(row, "ORDER_CODE", orderSet.getValue("ORDER_CODE", i));
            order.setItem(row, "ORDERSET_CODE", orderSet.getValue("ORDERSET_CODE", i));
            order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
            order.setItem(row, "ORDER_DESC", orderSet.getValue("ORDER_DESC", i));
            order.setItem(row, "GOODS_DESC", orderSet.getValue("GOODS_DESC", i));
            order.setItem(row, "DISPENSE_QTY", orderSet.getDouble("DOSAGE_QTY", i));
            order.setItem(row, "SPECIFICATION", orderSet.getValue("SPECIFICATION", i));
            order.setItem(row, "DISPENSE_UNIT", orderSet.getValue("UNIT_CODE", i));
            order.setItem(row, "ORIGINAL_PRICE", orderSet.getDouble("OWN_PRICE", i));
            order.setItem(row, "OWN_PRICE", orderSet.getDouble("OWN_PRICE", i));
            order.setItem(row, "NHI_PRICE", orderSet.getDouble("NHI_PRICE", i));// add by wanglong
                                                                                // 20130316
            order.setItem(row, "RPTTYPE_CODE", orderSet.getData("RPTTYPE_CODE", i));
            order.setItem(row, "OPTITEM_CODE", orderSet.getData("OPTITEM_CODE", i));
            order.setItem(row, "DEV_CODE", orderSet.getData("DEV_CODE", i));
            order.setItem(row, "MR_CODE", orderSet.getData("MR_CODE", i));
            order.setItem(row, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE"));
            order.setItem(row, "REXP_CODE", BIL.getRexpCode(parm.getValue("CHARGE_HOSP_CODE"), "H"));
            order.setItem(row, "CAT1_TYPE", orderSet.getValue("CAT1_TYPE", i));
            order.setItem(row, "ORDER_CAT1_CODE", orderSet.getValue("ORDER_CAT1_CODE", i));
            if (StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE"))) {
                order.setItem(row, "EXEC_DEPT_CODE", dept);
            } else {
                order.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE"));
            }
            order.setItem(row, "DEPT_ATTRIBUTE", "");
            order.setItem(row, "OPT_USER", Operator.getID());
            order.setItem(row, "OPT_TERM", Operator.getIP());
            order.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
            order.setItem(row, "REGION_CODE", Operator.getRegion());
            order.setItem(row, "DR_CODE", Operator.getID());
            order.setItem(row, "DEPT_CODE", dept);
            order.setItem(row, "SETMAIN_FLG", "N");// 隐藏
            order.setItem(row, "HIDE_FLG", "Y");
            order.setItem(row, "CTZ1_CODE", ctz);
            order.setItem(row, "CTZ2_CODE", "");
            order.setItem(row, "CTZ3_CODE", "");
            order.setItem(row, "BILL_FLG", "N");
            order.setItem(row, "EXEC_FLG", "N");
            order.setActive(row, true);
            row = order.insertRow();
            order.setItem(row, "SETMAIN_FLG", "Y");// modify by wanglong 20130423
            order.setItem(row, "HIDE_FLG", "N");// modify by wanglong 20130423
            order.setItem(row, "BILL_FLG", "N");
            order.setItem(row, "EXEC_FLG", "N");
            order.setActive(row, false);
        }
        // row = order.insertRow();//delete by wanglong 20130423
        // order.setItem(row, "SETMAIN_FLG", "Y");
        // order.setItem(row, "HIDE_FLG", "N");
        // order.setItem(row, "BILL_FLG", "N");
        // order.setItem(row, "EXEC_FLG", "N");
        // order.setActive(row, false);
        order.setFilter(" SETMAIN_FLG='Y' ");
        order.filter();
        table.setDSValue();
        table.getTable().grabFocus();
        table.setSelectedRow(table.getRowCount() - 1);
        table.setSelectedColumn(1);
        if (inParm.getValue("METHOD", 0).equals("DELETE")
                && parm.getValue("CAT1_TYPE").equals("LIS")) {
            String groupSql = "";
            if (inParm.getCount("CASE_NO") > 0) {
                groupSql =
                        "SELECT DISTINCT B.ORDER_CODE,B.ORDER_DESC,B.ORDERSET_GROUP_NO "
                                + "  FROM HRM_ORDER A, HRM_ORDER B "
                                + " WHERE A.CASE_NO = B.CASE_NO    "
                                + "   AND A.MED_APPLY_NO = B.MED_APPLY_NO "
                                + "   AND A.CASE_NO = '#'   " + "   AND A.ORDER_CODE = '#'";
                groupSql = groupSql.replaceFirst("#", inParm.getValue("CASE_NO", 0));
                groupSql = groupSql.replaceFirst("#", parm.getValue("ORDER_CODE"));
            } else {
                groupSql =
                        "SELECT DISTINCT B.ORDER_CODE, B.ORDER_DESC, B.ORDERSET_GROUP_NO "
                                + "  FROM HRM_ORDER A, HRM_ORDER B, HRM_PATADM C "
                                + " WHERE A.CASE_NO = B.CASE_NO "
                                + "   AND A.MED_APPLY_NO = B.MED_APPLY_NO "
                                + "   AND A.CASE_NO = C.CASE_NO " + "   AND C.CONTRACT_CODE = '#' "
                                + "   AND C.MR_NO = '#'         " + "   AND A.ORDER_CODE = '#'";
                groupSql = groupSql.replaceFirst("#", inParm.getValue("CONTRACT_CODE", 0));
                groupSql = groupSql.replaceFirst("#", inParm.getValue("MR_NO", 0));
                groupSql = groupSql.replaceFirst("#", parm.getValue("ORDER_CODE"));
            }
            TParm groupParm = new TParm(TJDODBTool.getInstance().select(groupSql));
            if (groupParm.getErrCode() != 0) {
                this.messageBox(groupParm.getErrText());
                return;
            }
            if (groupParm.getCount() > 1) {
                String orderStr = "";
                for (int i = 0; i < groupParm.getCount(); i++) {
                    if (!groupParm.getValue("ORDER_CODE", i).equals(parm.getValue("ORDER_CODE"))) {
                        orderStr += groupParm.getValue("ORDER_DESC", i) + "，";
                    }
                }
                orderStr = orderStr.substring(0, orderStr.length() - 1);
                this.messageBox("该医嘱和 " + orderStr + " 共用条码号！\n已报到的人删除它，将会同时删除和它共用条码的其他医嘱！请注意！");
                return;
            }
        }// add-end
    }

    /**
     * 删除医嘱
     */
    public void onDelRow() {
        if (table.getSelectedRow() < 0) {
            return;
        }
        String filter = order.getFilter();
        // String buff = order.isFilter() ? order.FILTER : order.PRIMARY;
        // TParm newParm = order.getBuffer(buff);
        int[] newRows = order.getNewRows();
        int row = -1;
        row = table.getSelectedRow();
        String orderCode = order.getItemString(newRows[row], "ORDER_CODE");
        String orderSetCode = order.getItemString(newRows[row], "ORDERSET_CODE");
        int groupNo = order.getItemInt(newRows[row], "ORDERSET_GROUP_NO");
        // String setMainFlg = order.getItemString(newRows[row], "SETMAIN_FLG");
        if (StringUtil.isNullString(orderCode)) {
            return;
        }
        if (order.getItemString(newRows[row], "CAT1_TYPE").equals("PHA")
                || (order.getItemString(newRows[row], "ORDER_CAT1_CODE").equals("MAT") && "N"
                        .equals(order.getItemString(newRows[row], "HIDE_FLG")))) {
            order.deleteRow(newRows[row]);
        } else {
            order.setFilter("");
            order.filter();
            // String buff2 = order.isFilter() ? order.FILTER : order.PRIMARY;
            // TParm newParm2 = order.getBuffer(buff2);
            int count = order.rowCount();
            for (int i = count - 1; i > -1; i--) {
                if ((orderSetCode.equalsIgnoreCase(order.getItemString(i, "ORDERSET_CODE")) && groupNo == order
                        .getItemInt(i, "ORDERSET_GROUP_NO"))) {
                    order.deleteRow(i);
                }
            }
        }
        int count = order.rowCount();
        for (int i = count - 1; i > -1; i--) {
            if (order.getItemString(i, "ORDER_CODE").equals("")) {
                order.deleteRow(i);
            }
        }
        row = order.insertRow();
        order.setItem(row, "SEQ", order.getItemInt(row, "#ID#"));
        order.setItem(row, "CHECK_SEQ", null);
        order.setItem(row, "OPT_USER", Operator.getID());
        order.setItem(row, "OPT_TERM", Operator.getIP());
        order.setItem(row, "SETMAIN_FLG", "Y");
        order.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        order.setActive(row, false);
        // String buff3 = order.isFilter() ? order.FILTER : order.PRIMARY;
        // TParm newParm3 = order.getBuffer(buff3);
        order.setFilter(filter);
        order.filter();
        table.setDSValue();
    }

    public void onSave() {
        table.acceptText();
        int pageCount = order.rowCount();
        if (pageCount <= 0) {
            this.messageBox("没有保存数据");
            return;
        } else {
            String method = inParm.getValue("METHOD", 0);
            if (method != null && method.equals("ADD")) {
                batchAdd();
            } else {
                batchDelete();
            }
        }
    }

    /**
     * 批量新增
     */
    public void batchAdd() {// refactor by wanglong 20130311
        order.setFilter("");
        order.filter();
        int[] commonInsertRows = order.getNewRows();// 记录新增的医嘱的行号 add by wanglong 20130226
        Set<String> orderSet = new HashSet<String>();
        int length = 0;
        for (int i : commonInsertRows) {
            if (order.getItemString(i, "SETMAIN_FLG").equals("Y")) {
                length++;
                orderSet.add(order.getItemString(i, "ORDER_CODE"));
            }
        }
        if (orderSet.size() != length) {
            this.messageBox("不允许重复添加某个医嘱\n对于检验检查，每次只能添加一个\n对于药嘱和卫材，通过更改数量来添加多个");
//            onClear();
            return;
        }
        String mrList = "";
        int patCount = inParm.getCount("MR_NO");
        if (patCount >= 1000) {
            this.messageBox("同时操作的人员数不要超过1000！");
            return;
        }
        String patSql = "SELECT * FROM SYS_PATINFO WHERE MR_NO IN (#)";// add by wanglong 20130304
        for (int i = 0; i < patCount; i++) {
            mrList += "'" + inParm.getValue("MR_NO", i) + "',";
        }
        mrList = mrList.substring(0, mrList.length() - 1);
        patSql = patSql.replaceFirst("#", mrList);
        TParm patInfoTparm = new TParm(TJDODBTool.getInstance().select(patSql));
        if (patInfoTparm.getErrCode() != 0 || patInfoTparm.getCount() < 1) {
            this.messageBox("查询人员信息出错");
            return;
        }
        int errCount = 0;// 错误个数
        List listHl7 = new ArrayList();
        boolean flag = false;// 记录新增的医嘱中是否包含检验检查
        for (int insertRow : commonInsertRows) {// add by wanglong 20130415
            TParm parm = order.getRowParm(insertRow);
            if (parm.getValue("SETMAIN_FLG").equals("Y")
                    && (parm.getValue("CAT1_TYPE").equals("LIS") || parm.getValue("CAT1_TYPE")
                            .equals("RIS"))) {
                flag = true;
                break;
            }
        }
        //校验药品医嘱 add by huangjw 20160419 start 
        for (int insertRow : commonInsertRows) {
            TParm parm = order.getRowParm(insertRow);
        	if("PHA".equals(parm.getValue("CAT1_TYPE")) && parm.getValue("SETMAIN_FLG").equals("Y")){
        		String orderCode = parm.getValue("ORDER_CODE");
                String orgCode = parm.getValue("EXEC_DEPT_CODE");
				TParm orgParm = new TParm(TJDODBTool.getInstance().select(
						"SELECT ORG_TYPE,SUP_ORG_CODE,ORG_CHN_DESC FROM IND_ORG WHERE ORG_CODE = '"
								+ orgCode + "' "));
				
				if(orgParm.getCount() <= 0){
					this.messageBox(parm.getValue("ORDER_DESC")+"的执行科室没有归属药房，请修改");
					return;
				}
				if("C".equals(orgParm.getValue("ORG_TYPE",0))){
					orgCode = orgParm.getValue("SUP_ORG_CODE",0);
				}
        		String sqlM = "SELECT ORG_CODE FROM IND_STOCKM WHERE ORG_CODE = '"+orgCode+"' AND  ORDER_CODE = '"
					+ orderCode + "' AND ACTIVE_FLG = 'N'  ";
        		//System.out.println("sqlM::::"+sqlM);
        		TParm checkPhaParmM = new TParm(TJDODBTool.getInstance()
						.select(sqlM));
        		if(checkPhaParmM.getCount() > 0){//判断主库
        			String sqlD = "SELECT VALID_DATE,STOCK_QTY,ACTIVE_FLG FROM IND_STOCK WHERE ORG_CODE = '"+orgCode+"' AND  ORDER_CODE = '"
						+ orderCode + "' AND  STOCK_QTY > 0   ORDER BY VALID_DATE DESC ";
        			
        			//System.out.println("sqlD::::"+sqlD);
        			TParm checkPhaParmD = new TParm(TJDODBTool.getInstance()
							.select(sqlD));
        			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        			Timestamp sysDate = TJDODBTool.getInstance().getDBTime();
        			
        			if(checkPhaParmD.getCount() > 0){
        				boolean flg = false;
        				for(int i = 0; i < checkPhaParmD.getCount(); i++){
        					if(sysDate.before(checkPhaParmD.getTimestamp("VALID_DATE",i)) &&
        									checkPhaParmD.getValue("ACTIVE_FLG",i).equals("Y")){
        						flg = true;
        						break;
        					}
        				}
        				if(!flg){
        					this.messageBox(parm.getValue("ORDER_DESC")+"已经过效期，请与药房联系");
        					return;
        				}
        				
        			}
        		}else{
        			this.messageBox(parm.getValue("ORDER_DESC")+"已经过效期，请与药房联系");
					return;
        		}
				
        	}
        }
        //校验药品医嘱add by huangjw 20160419 end 
        for (int i = 0; i < patCount; i++) {
            TParm patRow = (TParm) inParm.getRow(i);
            String mrNo = patRow.getValue("MR_NO");
            String seqNo = patRow.getValue("SEQ_NO");
            String patName = patRow.getValue("PAT_NAME");
            if (StringUtil.isNullString(mrNo)) {
                this.messageBox_("序号:" + seqNo + "  姓名：" + patName + " 病案号为空");
                errCount++;
                continue;
            }
            String caseNo = "";
            String contractCode = patRow.getValue("CONTRACT_CODE");
            if (patRow.getValue("CASE_NO").length() == 0) {
                caseNo = adm.getLatestCaseNoBy(mrNo, contractCode);
            } else {
                caseNo = patRow.getValue("CASE_NO");
            }
            if (StringUtil.isNullString(caseNo)) {
                this.messageBox("序号:" + seqNo + "  姓名：" + patName + " 的医嘱未展开，不允许增项");
                errCount++;
                continue;
            }
            // add by wangong 20130415 增加结算状态的检查
            String billSql =
                    "SELECT DISTINCT BILL_NO FROM HRM_ORDER WHERE CASE_NO = '" + caseNo + "'";
            TParm billParm = new TParm(TJDODBTool.getInstance().select(billSql));
            if (billParm.getErrCode() != 0) {
                this.messageBox("检查结算信息出错");
                return;
            }
            if (billParm.getCount() > 1
                    || (billParm.getCount() > 0 && !StringUtil.isNullString(billParm
                            .getValue("BILL_NO", 0).trim()))) {
                this.messageBox("序号:" + seqNo + "  姓名：" + patName + " 已结算，不允许增项");
                errCount++;
                continue;
            }
            // add end
            HRMOrder everyPatOrder;// 医嘱对象
            everyPatOrder = new HRMOrder();
            everyPatOrder.onQuery(caseNo, mrNo);
            everyPatOrder.retrieve();
            // 如果是已报到，seq取数据库中最大值+1
            int orderSeqNo = everyPatOrder.getOrderMaxSeqNo(caseNo);
            int orderGroupNo = everyPatOrder.getOrderMaxGroupNo(caseNo);// add by wanglong 20130321
            /**
             * 批量新增
             */
            for (int insertRow : commonInsertRows) {// modify by wanglong 20130226
                TParm parm = order.getRowParm(insertRow);
                String orderCode = parm.getValue("ORDER_CODE");
                if (!orderCode.equals("")) {
                    if (parm.getValue("SETMAIN_FLG").equals("Y")) {
                        orderGroupNo++;
                    }
                    everyPatOrder =
                            addHRMOrder(everyPatOrder, insertRow, orderSeqNo, orderGroupNo, parm,
                                        caseNo, patRow);
                    orderSeqNo++;
                }
            }
            String[] sql = new String[]{};
            sql = StringTool.copyArray(sql, everyPatOrder.getUpdateSQL());
            sql = StringTool.copyArray(sql, everyPatOrder.getMedApply().getUpdateSQL());
            // 配置送入后台保存方法的参数，并验证后台保存方法的返回值是否成功
            TParm inParm = new TParm();
            Map inMap = new HashMap();
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm result =
                    TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave",
                                                 inParm);
            if (result.getErrCode() != 0) {
                errCount++;
                this.messageBox("序号:" + seqNo + "  姓名：" + patName + " 医嘱保存失败" + result.getErrText());
                continue;
            }
            if (patRow.getValue("COVER_FLG").equals("Y")) {
                if (flag == true) {// add by wanglong 20130415
                    this.getHl7List(listHl7, caseNo);
                }
            }
        }
        // 发送HL7消息
        if (listHl7.size() > 0) {
            TParm hl7Parm = Hl7Communications.getInstance().Hl7Message(listHl7);// add by wanglong
                                                                                // 20130312
            if (hl7Parm.getErrCode() < 0) {
                errCount = patCount;
                this.messageBox("发送HL7消息失败" + hl7Parm.getErrText());
            }
        }
        // add by wanglong 20140214 打印检查申请单
//        risParm.setCount(risParm.getCount("CASE_NO"));
//        if (risParm.getCount() > 0) {
//            onPrintRIS(risParm);
//        }
        // add end
        if (errCount > 0) {// add by wanglong 20130403
            onClear();
        }
        if (errCount != patCount) {
            this.messageBox("新增成功");
            this.setReturnValue("SUCCESS");
            this.closeWindow();
        } else {
            onClear();
            return;
        }
    }

    private HRMOrder addHRMOrder(HRMOrder order, int row, int seqNo, int groupNo, TParm parm,
                                 String caseNo, TParm patParm) {// modify by wanglong 20130311
        int row1 = order.rowCount() - 1;// add by wanglong 20130408
        if (row1 < 0) {
            row1 = order.insertRow(-1, caseNo);
            order.setActive(row, false);
        }
        // int row;
        // 如果小于等于0未报道
        if (seqNo <= 0) {
            seqNo = order.getMaxSeqNo(caseNo);
        }
        if (!order.getItemString(order.rowCount() - 1, "CASE_NO").equals("")) {// modify by wanglong
                                                                               // 20130408
            // if (!order.getItemString(row, "CASE_NO").equals("")) {//add by wanglong 20130225
            row = order.insertRow();
        }
        // String orderCode = ;
        // row=order.insertRow();
        order.setItem(row, "CASE_NO", caseNo);
        order.setItem(row, "MR_NO", patParm.getValue("MR_NO"));
        order.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
        order.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
        order.setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC"));
        order.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION"));
        order.setItem(row, "SEQ_NO", seqNo);
        order.setItem(row, "ORDERSET_CODE", parm.getValue("ORDERSET_CODE"));
        order.setItem(row, "DISPENSE_QTY", parm.getDouble("DISPENSE_QTY"));
        order.setItem(row, "DISPENSE_UNIT", parm.getValue("DISPENSE_UNIT"));
        order.setItem(row, "ORIGINAL_PRICE", parm.getDouble("OWN_PRICE"));
        order.setItem(row, "OWN_PRICE", parm.getDouble("OWN_PRICE"));
        order.setItem(row, "OPTITEM_CODE", parm.getValue("OPTITEM_CODE"));
        order.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE"));
        order.setItem(row, "OPT_USER", Operator.getID());
        order.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        order.setItem(row, "OPT_TERM", Operator.getIP());
        order.setItem(row, "DR_CODE", Operator.getID());
        order.setItem(row, "ORDER_DATE", TJDODBTool.getInstance().getDBTime());
        order.setItem(row, "DEPT_CODE", parm.getValue("DEPT_CODE"));
        order.setItem(row, "REGION_CODE", Operator.getRegion());
        order.setItem(row, "NHI_PRICE", parm.getData("NHI_PRICE"));// add by wanglong 20130316
        double disCount = 1;// add by wanglong 20130527
        if (parm.getValue("CAT1_TYPE").equals("PHA")// 药品和卫材不打折
        // || parm.getValue("ORDER_CAT1_CODE").equals("MAT")//泰心材料能打折
        ) {// modify by wanglong 20130306
            disCount = 1;
        } else {
            disCount = patParm.getDouble("DISCNT");
        }
        order.setItem(row, "DISCOUNT_RATE", disCount);
        double ownAmt = 0.0;
        double arAmt = 0.0;
        ownAmt = StringTool.round(parm.getDouble("OWN_PRICE") * parm.getDouble("DISPENSE_QTY"), 2);
        arAmt = StringTool.round(ownAmt * disCount, 2);// modify by wanglong 20130527
        order.setItem(row, "OWN_AMT", ownAmt);
        order.setItem(row, "AR_AMT", arAmt);
        if (parm.getValue("NEW_FLG").equals("Y")) {// 假如是套餐中选择的医嘱add by wanglong 20130425
            order.setItem(row, "DISCOUNT_RATE", 1);
            order.setItem(row, "OWN_AMT", ownAmt);
            order.setItem(row, "AR_AMT", ownAmt);
        }
        order.setItem(row, "RPTTYPE_CODE", parm.getData("RPTTYPE_CODE"));
        // OPTITEM_CODE
        order.setItem(row, "OPTITEM_CODE", parm.getData("OPTITEM_CODE"));
        order.setItem(row, "DEV_CODE", parm.getData("DEV_CODE"));
        // MR_CODE
        order.setItem(row, "MR_CODE", parm.getData("MR_CODE"));
        order.setItem(row, "CTZ1_CODE", ctz);
        order.setItem(row, "CTZ2_CODE", "");
        order.setItem(row, "CTZ3_CODE", "");
        order.setItem(row, "HEXP_CODE", parm.getValue("HEXP_CODE"));
        order.setItem(row, "REXP_CODE", parm.getValue("REXP_CODE"));
        order.setItem(row, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE"));
        // CAT1_TYPE
        order.setItem(row, "CAT1_TYPE", parm.getValue("CAT1_TYPE"));
        order.setItem(row, "CONTRACT_CODE", patParm.getValue("CONTRACT_CODE"));
        if (StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE"))) {
            // EXEC_DEPT_CODE
            order.setItem(row, "EXEC_DEPT_CODE", Operator.getDept());
        } else {
            // EXEC_DEPT_CODE
            order.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE"));
        }
        order.setItem(row, "DEPT_ATTRIBUTE", parm.getData("DEPT_ATTRIBUTE"));
        order.setItem(row, "SETMAIN_FLG", parm.getData("SETMAIN_FLG"));
        order.setItem(row, "HIDE_FLG", parm.getData("HIDE_FLG"));
        order.setItem(row, "BILL_FLG", parm.getData("BILL_FLG"));
        order.setItem(row, "EXEC_FLG", parm.getData("EXEC_FLG"));
        // order.setItem(row, "ORDERSET_GROUP_NO", groupNo);// add by wanglong 20130321
        // // 如果是药品或卫材
        // if ("PHA_W".equals(parm.getValue("ORDER_CAT1_CODE"))
        // || "MAT".equals(parm.getValue("ORDER_CAT1_CODE"))) {
        // order.setItem(row, "ORDERSET_CODE", parm.getData("ORDER_CODE"));
        // }
        boolean isSetMain = parm.getBoolean("SETMAIN_FLG");
        if (isSetMain) {// add by wanglong 20130510
            order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
            order.setItem(row, "ORDERSET_CODE", parm.getData("ORDER_CODE"));
        } else {
            // 如果是药品或卫材
            // if ("PHA_W".equals(parm.getValue("ORDER_CAT1_CODE"))//delete by wanglong 20131029
            // || ("MAT".equals(parm.getValue("ORDER_CAT1_CODE")) && "N".equals(parm
            // .getValue("HIDE_FLG")))) {
            // order.setItem(row, "ORDERSET_GROUP_NO", 0);
            // order.setItem(row, "ORDERSET_CODE", parm.getData("ORDER_CODE"));
            // } else {
            order.setItem(row, "ORDERSET_GROUP_NO", order.getItemData(row - 1, "ORDERSET_GROUP_NO"));
            order.setItem(row, "ORDERSET_CODE", order.getItemData(row - 1, "ORDERSET_CODE"));
            // }
        }
        // 如果是药嘱 不需要条码
        // if(!"PHA_W".equals(parm.getValue("ORDER_CAT1_CODE"))){
        // if("Y".equals(parm.getData("SETMAIN_FLG"))){
        // String labMapKey=order.getLabNo(row, patParm);
        // order.setItem(row, "MED_APPLY_NO", labMapKey);
        // }
        // }
        if ("Y".equalsIgnoreCase(parm.getValue("SETMAIN_FLG"))
                && ("LIS".equals(parm.getValue("CAT1_TYPE")) || "RIS".equals(parm
                        .getValue("CAT1_TYPE")))) {// modify by wanglong 20130228
            // System.out.println("=====sysFee===="+sysFee);
            String labNo = order.getLabNo(row, patParm);
            order.setItem(row, "MED_APPLY_NO", labNo);
        }
        if ("RIS".equals(parm.getValue("CAT1_TYPE"))
                && !order.getItemString(row, "MED_APPLY_NO").equals("")) {// add by wanglong 20140214
            risParm.addData("CASE_NO", caseNo);
            risParm.addData("MR_NO", patParm.getValue("MR_NO"));
            risParm.addData("ORDER_CODE", parm.getValue("ORDER_CODE"));
            risParm.addData("ORDER_DESC", parm.getValue("ORDER_DESC"));
            risParm.addData("CAT1_TYPE", parm.getValue("CAT1_TYPE"));
            risParm.addData("SEQ_NO", seqNo);
            risParm.addData("LAB_NO", order.getItemString(row, "MED_APPLY_NO"));
            risParm.addData("ORDER_NO", caseNo);
            risParm.addData("EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE"));
        }
        order.setActive(row, true);
        // row++;
        return order;
    }

    /**
     * 得到HL7数据
     * 
     * @param listHl7
     *            List
     * @param caseNo
     *            String
     * @return List
     */
    public List getHl7List(List listHl7, String caseNo) {// add by wanglong 20130312
        String sql =
                "SELECT CAT1_TYPE,PAT_NAME,CASE_NO,MR_NO,APPLICATION_NO AS LAB_NO,ORDER_NO,SEQ_NO "
                        + "FROM MED_APPLY WHERE ADM_TYPE='H' AND CASE_NO='" + caseNo
                        + "' AND SEND_FLG < 2 AND STATUS <> 9";
        // System.out.println("SQLMED==" + sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        int rowCount = parm.getCount();
        String preLabNo = "";
        for (int i = 0; i < rowCount; i++) {
            TParm temp = parm.getRow(i);
            String labNo = temp.getValue("LAB_NO");
            // if (!preLabNo.equals(labNo)) {//delete by wanglong 20130402
            temp.setData("ADM_TYPE", "H");
            temp.setData("FLG", "0");
            // System.out.println("PAT_NAME----:" + temp.getValue("PAT_NAME") + ":LAB_NO----:" +
            // temp.getValue("LAB_NO"));
            listHl7.add(temp);
            preLabNo = labNo;
            // }
        }
        // System.out.println("listHl7.size----:" + listHl7.size());
        return listHl7;
    }

    /**
     * 取得最大集合医嘱序号
     * 
     * @return
     */
    public int getMaxGroupNo() {
        String filter = order.getFilter();
        order.setFilter("SETMAIN_FLG='Y'");
        order.filter();
        int count = order.rowCount();
        int no = -1;
        for (int i = 0; i < count; i++) {
            int temp = order.getItemInt(i, "ORDERSET_GROUP_NO");
            if (temp > no) no = temp;
        }
        order.setFilter(filter);
        order.filter();
        return (no + 1);
    }

    /**
     * 批量删除
     */
    public void batchDelete() {// refactor by wanglong 20130311
        order.setFilter("");
        order.filter();
        int[] commonInsertRows = order.getNewRows();// 记录新增的医嘱的行号 add by wanglong 20130226
        Set<String> orderSet = new HashSet<String>();
        int length = 0;
        for (int i : commonInsertRows) {
            if (order.getItemString(i, "SETMAIN_FLG").equals("Y")) {
                length++;
                orderSet.add(order.getItemString(i, "ORDER_CODE"));
            }
        }
        if (orderSet.size() != length) {
            this.messageBox("不允许重复添加某个医嘱，并且，每次只能删除一个");
//            onClear();
            return;
        }
        int patCount = inParm.getCount("MR_NO");
        if (patCount >= 1000) {
            this.messageBox("同时操作的人员数不要超过1000！");
            return;
        }
        int rightCount = 0;// 正确个数
        String orderList = "";// add by wanglong 20130412
        for (int insertRow : commonInsertRows) {
            TParm parm = this.order.getRowParm(insertRow);
            if (parm.getValue("SETMAIN_FLG").equals("Y")) {
                String orderDesc = parm.getValue("ORDER_DESC");
                orderList = orderList + orderDesc + "\r\n";
            }
        }
        String patList = "";// add by wanglong 20130412
        for (int i = 0; i < patCount; i++) {
            TParm patRow = (TParm) inParm.getRow(i);
            String mrNo = patRow.getValue("MR_NO");
            String seqNo = patRow.getValue("SEQ_NO");
            String patName = patRow.getValue("PAT_NAME");
            patList = patList + patName + ",";
            if (StringUtil.isNullString(mrNo)) {
                this.messageBox_("序号:" + seqNo + "  姓名：" + patName + " 病案号为空");
                continue;
            }
            String caseNo = "";
            String contractCode = patRow.getValue("CONTRACT_CODE");
            if (patRow.getValue("CASE_NO").length() == 0) {
                caseNo = adm.getLatestCaseNoBy(mrNo, contractCode);
            } else {
                caseNo = patRow.getValue("CASE_NO");
            }
            if (StringUtil.isNullString(caseNo)) {
                this.messageBox_("序号:" + seqNo + "  姓名：" + patName + " 医嘱未展开，不允许删项");
                continue;
            }
            // 如果已结算，应先取消结算，才能删项
            String billStateSql =
                    "SELECT DISTINCT BILL_NO FROM HRM_ORDER WHERE CASE_NO = '" + caseNo
                            + "' AND BILL_NO IS NOT NULL";
            TParm billStateParm = new TParm(TJDODBTool.getInstance().select(billStateSql));
            if (billStateParm.getErrCode() != 0) {
                this.messageBox("序号:" + seqNo + "  姓名：" + patName + " 查询结算信息出错");
                continue;
            }
            if (billStateParm.getCount("BILL_NO") > 0) {
                this.messageBox("序号:" + seqNo + "  姓名：" + patName + " 已结算，不能删项，请先执行取消结算操作");
                continue;
            }
            /**
             * 批量删除
             */
            List hl7ParmDel = new ArrayList();
            String[] sql = new String[]{};
            for (int insertRow : commonInsertRows) {
                TParm parm = order.getRowParm(insertRow);
                String orderCode = parm.getValue("ORDER_CODE");
                if (!StringUtil.isNullString(orderCode)) {
                    String setMainFlg = parm.getValue("SETMAIN_FLG");
                    if (setMainFlg.equalsIgnoreCase("Y")) {
                        // 已执行的医嘱不能删除
                        String execFlgSql =
                                "SELECT * FROM HRM_ORDER WHERE CASE_NO = '" + caseNo
                                        + "' AND ORDER_CODE = '" + orderCode + "' AND ROWNUM=1";
                        TParm orderParm = new TParm(TJDODBTool.getInstance().select(execFlgSql));
                        if (orderParm.getErrCode() != 0) {
                            this.messageBox("序号:" + seqNo + "  姓名：" + patName
                                    + " 查询该人员医嘱执行情况出错，将不会删除其任何医嘱");
                            break;
                        }
                        String orderDesc = parm.getValue("ORDER_DESC");
                        if (orderParm.getValue("EXEC_FLG", 0).equals("Y")) {
                            this.messageBox("序号:" + seqNo + "  姓名：" + patName + " " + orderDesc
                                    + " 已执行，不能删除");
                            continue;
                        }
                        // 发送Hl7消息
                        if (!StringUtil.isNullString(orderParm.getValue("MED_APPLY_NO", 0))
                                && patRow.getValue("COVER_FLG").equals("Y")) {// modify by wanglong 20130402
                            TParm delTemp = new TParm();
                            delTemp.setData("ADM_TYPE", "H");
                            delTemp.setData("PAT_NAME", patName);
                            delTemp.setData("CAT1_TYPE", orderParm.getValue("CAT1_TYPE", 0));
                            delTemp.setData("CASE_NO", caseNo);
                            delTemp.setData("LAB_NO", orderParm.getValue("MED_APPLY_NO", 0));
                            delTemp.setData("ORDER_NO", caseNo);
                            delTemp.setData("SEQ_NO", orderParm.getValue("SEQ_NO", 0));
                            delTemp.setData("FLG", "1");
//                            try {
//                                if (Hl7Communications.getInstance().IsExeOrder(delTemp, "H")) {
//                                    continue;
//                                }
//                            }
//                            catch (Exception ex) {
//                                System.err.print("检查已执行判断失败。");
//                                ex.printStackTrace();
//                            }
                            hl7ParmDel.add(delTemp);
                        }
                        if (orderParm.getValue("CAT1_TYPE", 0).equals("LIS")
                                && !StringUtil.isNullString(orderParm.getValue("MED_APPLY_NO", 0))
                                && patRow.getValue("COVER_FLG").equals("Y")) {// modify by wanglong 20131125
                            // 删除HRM_ORDER表（对于相同条码号的医嘱，要一并删除）
                            String deleteOrderSql =
                                    "DELETE FROM HRM_ORDER WHERE (CASE_NO, ORDERSET_GROUP_NO) IN ( "
                                            + " SELECT B.CASE_NO, MAX(A.ORDERSET_GROUP_NO) "
                                            + "   FROM HRM_ORDER A, HRM_PATADM B "
                                            + "  WHERE A.CASE_NO = B.CASE_NO "
                                            + "    AND A.MED_APPLY_NO = '#' "
                                            + "    AND B.CASE_NO = '@'        "
                                            + " GROUP BY B.CASE_NO, A.ORDERSET_CODE) "
                                            + "   AND CASE_NO = '@'         "
                                            + "   AND BILL_NO IS NULL";
                            deleteOrderSql =
                                    deleteOrderSql.replaceFirst("#", orderParm
                                            .getValue("MED_APPLY_NO", 0));
                            deleteOrderSql = deleteOrderSql.replaceAll("@", caseNo);
                            sql = StringTool.copyArray(sql, new String[]{deleteOrderSql });
                            String otherSeqSql =//add by wanglong 20140303
                                    "SELECT SEQ_NO FROM HRM_ORDER  WHERE SETMAIN_FLG = 'Y' "
                                            + "   AND MED_APPLY_NO = '#' AND CASE_NO = '@' AND SEQ_NO NOT IN (&)";
                            otherSeqSql =
                                    otherSeqSql.replaceFirst("#",
                                                             orderParm.getValue("MED_APPLY_NO", 0));
                            otherSeqSql = otherSeqSql.replaceAll("@", caseNo);
                            otherSeqSql =
                                    otherSeqSql.replaceAll("&", orderParm.getValue("SEQ_NO", 0));
                            TParm seqParm = new TParm(TJDODBTool.getInstance().select(otherSeqSql));
                            if (seqParm.getErrCode() < 0) {
                                this.messageBox("序号:" + seqNo + "  姓名：" + patName
                                        + " 查询该人员医嘱执行情况出错，将不会删除其任何医嘱");
                                break;
                            }
                            for (int j = 0; j < seqParm.getCount(); j++) {
                                TParm delTemp = new TParm();
                                delTemp.setData("ADM_TYPE", "H");
                                delTemp.setData("PAT_NAME", patName);
                                delTemp.setData("CAT1_TYPE", "LIS");
                                delTemp.setData("CASE_NO", caseNo);
                                delTemp.setData("LAB_NO", orderParm.getValue("MED_APPLY_NO", 0));
                                delTemp.setData("ORDER_NO", caseNo);
                                delTemp.setData("SEQ_NO", seqParm.getValue("SEQ_NO", j));
                                delTemp.setData("FLG", "1");
//                                try {//上面已经判断时候执行了。相同条码的，一条执行，则全都执行，不用重复判断
//                                    if (Hl7Communications.getInstance().IsExeOrder(delTemp, "H")) {
//                                        continue;
//                                    }
//                                }
//                                catch (Exception ex) {
//                                    System.err.print("检查已执行判断失败。");
//                                    ex.printStackTrace();
//                                }
                                hl7ParmDel.add(delTemp);
                            }
                        } else {
                            if (!patRow.getValue("COVER_FLG").equals("Y")) {
                              //未报到前要删除med_apply表,以来order表数据，所以在order表数据被删前删除 add by wanglong 20140303
                                String deleteMedSql =
                                        "DELETE FROM MED_APPLY WHERE (CAT1_TYPE, APPLICATION_NO, ORDER_NO, SEQ_NO) IN "
                                                + "           (SELECT CAT1_TYPE, MED_APPLY_NO, CASE_NO, SEQ_NO "
                                                + "              FROM HRM_ORDER "
                                                + "             WHERE (CASE_NO, ORDERSET_GROUP_NO) IN (SELECT B.CASE_NO, MAX(A.ORDERSET_GROUP_NO) "
                                                + "                                                      FROM HRM_ORDER A, HRM_PATADM B "
                                                + "                                                     WHERE A.CASE_NO = B.CASE_NO "
                                                + "                                                       AND A.ORDERSET_CODE = '#' "
                                                + "                                                       AND B.CASE_NO = '@' "
                                                + "                                                    GROUP BY B.CASE_NO) "
                                                + "               AND CASE_NO = '@' "
                                                + "               AND BILL_NO IS NULL "
                                                + "               AND MED_APPLY_NO IS NOT NULL) "
                                                + "   AND CASE_NO = '@' AND STATUS=0 ";
                                deleteMedSql = deleteMedSql.replaceFirst("#", orderCode);
                                deleteMedSql = deleteMedSql.replaceAll("@", caseNo);
                                sql = StringTool.copyArray(sql, new String[]{deleteMedSql });
                            }
                            // 删除HRM_ORDER表（只删第一个匹配项，多个的话，要多次删除）
                            String deleteOrderSql =
                                    "DELETE FROM HRM_ORDER WHERE (CASE_NO, ORDERSET_GROUP_NO) IN ( "
                                            + " SELECT B.CASE_NO, MAX(A.ORDERSET_GROUP_NO) "
                                            + "   FROM HRM_ORDER A, HRM_PATADM B "
                                            + "  WHERE A.CASE_NO = B.CASE_NO     "
                                            + "    AND A.ORDERSET_CODE = '#' "
                                            + "    AND B.CASE_NO = '@'  "
                                            + " GROUP BY B.CASE_NO)           "
                                            + "    AND CASE_NO = '@' "
                                            + "    AND BILL_NO IS NULL";
                            deleteOrderSql = deleteOrderSql.replaceFirst("#", orderCode);
                            deleteOrderSql = deleteOrderSql.replaceAll("@", caseNo);
                            sql = StringTool.copyArray(sql, new String[]{deleteOrderSql });
                        }// add-end
                    }
                }
            }
            TParm hl7Parm = Hl7Communications.getInstance().Hl7Message(hl7ParmDel);
            if (hl7Parm.getErrCode() < 0) {
                this.messageBox("序号:" + seqNo + "  姓名：" + patName + " 删除医嘱失败（发送HL7消息失败）");
                continue;
            }
            if (sql.length <= 0) {
                continue;
            }
            TParm inParm = new TParm();
            Map inMap = new HashMap();
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm result =
                    TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave",
                                                 inParm);
            if (result.getErrCode() != 0) {
                this.messageBox("序号:" + seqNo + "  姓名：" + patName + " 医嘱删除失败" + result.getErrText());
                continue;
            }
            rightCount++;
        }
        // add by wanglong 20130412 增加日志记录功能
        String now =
                StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd HH:mm:ss");
        String log = "---------------------------------------------\r\n";
        log = log + "****健检批量删除日志(" + now + ")****\r\n";
        log = log + "操作人员：" + Operator.getName() + "(" + Operator.getID() + ")\r\n";
        log = log + "操作终端地址：" + Operator.getIP() + "\r\n";
        log = log + "被操作团体代码：" + this.inParm.getValue("COMPANY_CODE", 0) + "\r\n";
        log = log + "被操作合同：" + this.inParm.getValue("CONTRACT_CODE", 0) + "\r\n";
        log = log + "被操作人员：" + patList.substring(0, patList.length() - 1) + "\r\n";
        log = log + "***************删除医嘱列表******************\r\n";
        log = log + orderList.substring(0, orderList.length() - 1) + "\r\n";
        log = log + "---------------------------------------------\r\n";
        TParm logParm = new TParm();
        logParm.setData("LOG", log);
        TParm parm =
                TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "writeLog",
                                             logParm);
        // add end
        if (rightCount > 0) {
            this.messageBox("删除成功");
            this.setReturnValue("SUCCESS");
            this.closeWindow();
        } else {
            onClear();
            return;
        }
    }

    /**
     * 是否是集合医嘱
     * 
     * @param row
     *            int
     * @param buff
     *            String
     * @return boolean
     */
    public boolean isOrderSet(TParm orderParm) {
        boolean falg = false;
        if (orderParm.getBoolean("SETMAIN_FLG")) {
            falg = true;
        }
        return falg;
    }

    /**
     * 得到TTable
     * 
     * @param tag
     *            String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable) this.getComponent(tag);
    }

    /**
     * 清空事件
     */
    public void onClear() {// add by wanglong 20130224
        order.filt("#");
        table.setDSValue();
        this.clearValue("ORDER_CODE;PACKAGE_CODE");// add by wanglong 20130423
        packageDetail.getPopupMenuData().getData().clear();
        packageDetail.filter();
        initData();
    }

    /**
     * 套餐-细项联动
     */
    public void onChoosePackage() {// add by wanglong 20130423
        packageCode = this.getValueString("PACKAGE_CODE");
        packageDetail.setPackageCode(packageCode);
        packageDetail.onQuery();
    }

    /**
     * 点击套餐细项
     */
    public void onChooseDetail() {// add by wanglong 20130423
    	String orderCode = this.getValueString("ORDER_CODE");
    	//去掉SEQ源程序重新获取ORDER_CODE后不更新parm内容
//        TParm parm =  ((TTextFormat) this.getComponent("ORDER_CODE")).getPopupMenuData();
//        int row = ((TTextFormat) this.getComponent("ORDER_CODE")).getTablePopupMenu().getSelectedRow();
//        String seq= parm.getValue("SEQ", row);
        String sql =
                "SELECT A.ORDER_CODE,A.ORDERSET_CODE,A.ORDER_DESC,B.GOODS_DESC,A.SPECIFICATION,A.DISPENSE_UNIT,A.DISPENSE_QTY,"
                        + "A.ORIGINAL_PRICE,A.PACKAGE_PRICE,A.SETMAIN_FLG,A.HIDE_FLG,A.OPTITEM_CODE,A.EXEC_DEPT_CODE,A.DEPT_ATTRIBUTE,"
                        + "B.NHI_PRICE,B.RPTTYPE_CODE,B.DEV_CODE,B.MR_CODE,B.CHARGE_HOSP_CODE,B.CAT1_TYPE,B.ORDER_CAT1_CODE "
                        + "  FROM HRM_PACKAGED A, SYS_FEE B,"
                        + "(SELECT ORDERSET_GROUP_NO FROM HRM_PACKAGED WHERE ORDER_CODE = '&' AND PACKAGE_CODE = '#') CC "//去掉SEQ源程序重新获取ORDER_CODE后不更新parm内容
//                        + "(SELECT ORDERSET_GROUP_NO FROM HRM_PACKAGED WHERE ORDER_CODE = '&' AND SEQ = @ AND PACKAGE_CODE = '#') CC "//源程序
                        + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                        + "   AND A.ORDERSET_GROUP_NO = CC.ORDERSET_GROUP_NO "
                        + "   AND A.PACKAGE_CODE = '#' ORDER BY A.SEQ";
        sql = sql.replaceFirst("&", orderCode);
//        sql = sql.replaceFirst("@", seq);
        sql = sql.replaceFirst("#", packageCode);
        sql = sql.replaceFirst("#", packageCode);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            this.messageBox("查询医嘱信息失败 " + result.getErrText());
            return;
        }
        if (result.getCount() == 0) {
            this.messageBox("系统中不存在该医嘱");
            return;
        }
        comboOrderReturn(result);
    }

    /**
     * 套餐细相新增医嘱方法,如有细相，则也新增细相信息，并只显示主项
     * 
     * @param parm
     */
    public void comboOrderReturn(TParm parm) {// add by wanglong 20130423
        int row = table.getRowCount() - 1;
        if (!StringUtil.isNullString(table.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        table.acceptText();
        double orderSetQty = parm.getDouble("DISPENSE_QTY", 0);
        int groupNo = -1;
        for (int i = 0; i < parm.getCount(); i++) {
            order.setItem(row, "NEW_FLG", "Y");// add by wanglong 20130425
            order.setItem(row, "SEQ", order.getItemInt(row, "#ID#"));
            order.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE", i));
            order.setItem(row, "ORDERSET_CODE", parm.getValue("ORDERSET_CODE", i));
            order.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC", i));
            order.setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC", i));
            order.setItem(row, "DISPENSE_QTY", parm.getDouble("DISPENSE_QTY", i) / orderSetQty);
            order.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION", i));
            order.setItem(row, "DISPENSE_UNIT", parm.getValue("DISPENSE_UNIT", i));
            order.setItem(row, "ORIGINAL_PRICE", parm.getDouble("ORIGINAL_PRICE", i));
            order.setItem(row, "OWN_PRICE", parm.getDouble("PACKAGE_PRICE", i));
            order.setItem(row, "NHI_PRICE", parm.getDouble("NHI_PRICE", i));
            order.setItem(row, "RPTTYPE_CODE", parm.getData("RPTTYPE_CODE", i));
            order.setItem(row, "OPTITEM_CODE", parm.getData("OPTITEM_CODE", i));
            order.setItem(row, "DEV_CODE", parm.getData("DEV_CODE", i));
            if (parm.getData("SETMAIN_FLG", i).equals("Y")
                    && parm.getData("DEPT_ATTRIBUTE", i).equals("04")) {
                String mrCodeSql = "SELECT TOT_MR_CODE FROM HRM_PACKAGEM WHERE PACKAGE_CODE = '#'";
                mrCodeSql = mrCodeSql.replaceFirst("#", this.getValueString("PACKAGE_CODE"));
                TParm mrCodeParm = new TParm(TJDODBTool.getInstance().select(mrCodeSql));
                if (mrCodeParm.getErrCode() != 0) {
                    this.messageBox(mrCodeParm.getErrText());
                    return;
                }
                if (mrCodeParm.getCount() < 1) {
                    this.messageBox("套餐不存在");
                    return;
                }
                order.setItem(row, "MR_CODE", mrCodeParm.getData("TOT_MR_CODE", 0));
            } else
            order.setItem(row, "MR_CODE", parm.getData("MR_CODE", i));
            order.setItem(row, "HEXP_CODE", parm.getValue("CHARGE_HOSP_CODE", i));
            order.setItem(row, "REXP_CODE",
                          BIL.getRexpCode(parm.getValue("CHARGE_HOSP_CODE", i), "H"));
            order.setItem(row, "CAT1_TYPE", parm.getValue("CAT1_TYPE", i));
            order.setItem(row, "ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE", i));
            if (StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE", i))) {
                order.setItem(row, "EXEC_DEPT_CODE", Operator.getDept());
            } else {
                order.setItem(row, "EXEC_DEPT_CODE", parm.getValue("EXEC_DEPT_CODE", i));
            }
            if (parm.getData("SETMAIN_FLG", i).equals("Y")
                    && parm.getValue("DEPT_ATTRIBUTE", i).equals("")) { // add by wangong 20130422 自动带出科别属性
                String attrSql =
                        "SELECT DISTINCT DEPT_ATTRIBUTE FROM HRM_PACKAGED WHERE ORDER_CODE = '"
                                + parm.getValue("ORDER_CODE") + "' ORDER BY DEPT_ATTRIBUTE";
                TParm attrParm = new TParm(TJDODBTool.getInstance().select(attrSql));
                if (attrParm.getErrCode() != 0) {
                    this.messageBox("查询科别属性失败");
                }
                if (attrParm.getCount() > 0) {
                    order.setItem(row, "DEPT_ATTRIBUTE", attrParm.getValue("DEPT_ATTRIBUTE", 0));
                }
            } else
            order.setItem(row, "DEPT_ATTRIBUTE", parm.getData("DEPT_ATTRIBUTE", i));
            order.setItem(row, "OPT_USER", Operator.getID());
            order.setItem(row, "OPT_TERM", Operator.getIP());
            order.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
            order.setItem(row, "REGION_CODE", Operator.getRegion());
            order.setItem(row, "DR_CODE", Operator.getID());
            order.setItem(row, "DEPT_CODE", dept);
            order.setItem(row, "SETMAIN_FLG", parm.getData("SETMAIN_FLG", i));
            order.setItem(row, "HIDE_FLG", parm.getData("HIDE_FLG", i));
            order.setItem(row, "CTZ1_CODE", ctz);
            order.setItem(row, "CTZ2_CODE", "");
            order.setItem(row, "CTZ3_CODE", "");
            order.setItem(row, "BILL_FLG", "N");
            order.setItem(row, "EXEC_FLG", "N");
            if (groupNo < 0) {
                groupNo = order.getMaxGroupNo();
            }
            order.setItem(row, "ORDERSET_GROUP_NO", groupNo);
            order.setActive(row, true);
            row = order.insertRow();
            order.setItem(row, "SETMAIN_FLG", "Y");
            order.setItem(row, "HIDE_FLG", "N");
            order.setItem(row, "BILL_FLG", "N");
            order.setItem(row, "EXEC_FLG", "N");
            order.setActive(row, false);
        }
        order.setFilter(" SETMAIN_FLG='Y' ");
        order.filter();
        table.setDSValue();
        table.getTable().grabFocus();
        table.setSelectedRow(table.getRowCount() - 1);
        table.setSelectedColumn(1);
        parm = parm.getRow(0);
        if (inParm.getValue("METHOD", 0).equals("DELETE")
                && parm.getValue("CAT1_TYPE").equals("LIS")) {
            String groupSql = "";
            if (inParm.getCount("CASE_NO") > 0) {
                groupSql =
                        "SELECT DISTINCT B.ORDER_CODE,B.ORDER_DESC,B.ORDERSET_GROUP_NO "
                                + "  FROM HRM_ORDER A, HRM_ORDER B "
                                + " WHERE A.CASE_NO = B.CASE_NO    "
                                + "   AND A.MED_APPLY_NO = B.MED_APPLY_NO "
                                + "   AND A.CASE_NO = '#'   " + "   AND A.ORDER_CODE = '#'";
                groupSql = groupSql.replaceFirst("#", inParm.getValue("CASE_NO", 0));
                groupSql = groupSql.replaceFirst("#", parm.getValue("ORDER_CODE"));
            } else {
                groupSql =
                        "SELECT DISTINCT B.ORDER_CODE, B.ORDER_DESC, B.ORDERSET_GROUP_NO "
                                + "  FROM HRM_ORDER A, HRM_ORDER B, HRM_PATADM C "
                                + " WHERE A.CASE_NO = B.CASE_NO "
                                + "   AND A.MED_APPLY_NO = B.MED_APPLY_NO "
                                + "   AND A.CASE_NO = C.CASE_NO " + "   AND C.CONTRACT_CODE = '#' "
                                + "   AND C.MR_NO = '#'         " + "   AND A.ORDER_CODE = '#'";
                groupSql = groupSql.replaceFirst("#", inParm.getValue("CONTRACT_CODE", 0));
                groupSql = groupSql.replaceFirst("#", inParm.getValue("MR_NO", 0));
                groupSql = groupSql.replaceFirst("#", parm.getValue("ORDER_CODE"));
            }
            TParm groupParm = new TParm(TJDODBTool.getInstance().select(groupSql));
            if (groupParm.getErrCode() != 0) {
                this.messageBox(groupParm.getErrText());
                return;
            }
            if (groupParm.getCount() > 1) {
                String orderStr = "";
                for (int i = 0; i < groupParm.getCount(); i++) {
                    if (!groupParm.getValue("ORDER_CODE", i).equals(parm.getValue("ORDER_CODE"))) {
                        orderStr += groupParm.getValue("ORDER_DESC", i) + "，";
                    }
                }
                orderStr = orderStr.substring(0, orderStr.length() - 1);
                this.messageBox("该医嘱和 " + orderStr + " 共用条码号！\n已报到的人删除它，将会同时删除和它共用条码的其他医嘱！请注意！");
                return;
            }
        }// add-end
    }

    /**
     * 打印检查申请单
     */
    public void onPrintRIS(TParm parm) {// add by wanglong 20140214
        String sql = "SELECT * FROM HRM_SYSPARM";
        TParm sysParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (sysParm.getErrCode() < 0) {
            return;
        }
        String deptCode = "";
        if (sysParm.getValue("RIS_PRINT", 0).equals("N")) {
            return;
        } else if (!sysParm.getValue("RIS_PRINT", 0).equals("Y")) {
            deptCode = sysParm.getValue("RIS_PRINT", 0);
        }
        pat = new HRMPatInfo();
        Timestamp now = SystemTool.getInstance().getDate();
        for (int i = 0; i < parm.getCount(); i++) {
            TParm inParam = parm.getRow(i);
            String seq = inParam.getValue("SEQ_NO");
            String where = "CASE_NO='" + inParam.getValue("CASE_NO") + "' AND SEQ_NO=" + seq;
            inParam.setData("ORDER_SEQ", seq);
            inParam.setData("EXEC_DEPT_DESC",
                            StringUtil.getDesc("SYS_DEPT", "DEPT_CHN_DESC", "DEPT_CODE='" + inParam.getValue("EXEC_DEPT_CODE") + "'"));
            inParam.setData("ORDER_CODE", StringUtil.getDesc("HRM_ORDER", "ORDER_CODE", where));
            inParam.setData("ORDER_DESC", StringUtil.getDesc("HRM_ORDER", "ORDER_DESC", where));
            String execDeptCode = StringUtil.getDesc("HRM_ORDER", "EXEC_DEPT_CODE", where);
            if (!deptCode.equals("") && !deptCode.equals(execDeptCode)) {
                continue;
            }
            TParm patInfo = pat.getHRMPatInfo(inParam.getValue("MR_NO"), inParam.getValue("CASE_NO"));
            inParam.setData("MR_NO", inParam.getValue("MR_NO"));
            inParam.setData("MR_NO", "TEXT", inParam.getValue("MR_NO"));
            inParam.setData("CASE_NO", inParam.getValue("CASE_NO"));
            inParam.setData("PAT_NAME", patInfo.getValue("PAT_NAME", 0));
            inParam.setData("SEX_CODE", patInfo.getValue("SEX_CODE", 0));
            inParam.setData("SEX_DESC", patInfo.getValue("SEX_DESC", 0));
            inParam.setData("BIRTHDAY", StringTool.getString(patInfo.getTimestamp("BIRTHDAY", 0), "yyyy-MM-dd"));
            inParam.setData("AGE", StringTool.CountAgeByTimestamp(patInfo.getTimestamp("BIRTHDAY", 0), now)[0]);
            inParam.setData("COMPANY_CODE", patInfo.getValue("COMPANY_CODE", 0));
            inParam.setData("COMPANY_DESC", patInfo.getValue("COMPANY_DESC", 0));
            inParam.setData("CONTRACT_CODE", patInfo.getValue("CONTRACT_CODE", 0));
            inParam.setData("CONTRACT_DESC", patInfo.getValue("CONTRACT_DESC", 0));
            inParam.setData("SEQ_NO", patInfo.getValue("SEQ_NO", 0));
            inParam.setData("TEL", patInfo.getValue("TEL", 0));
            inParam.setData("DEPT_CODE", patInfo.getValue("DEPT_CODE", 0));
            inParam.setData("DEPT_DESC", patInfo.getValue("DEPT_DESC", 0));
            inParam.setData("ORDER_DATE", StringTool.getString(now, "yyyy/MM/dd"));
            this.openPrintDialog("%ROOT%\\config\\prt\\HRM\\HRMRisSheet.jhw", inParam, true);
        }
    }
}
