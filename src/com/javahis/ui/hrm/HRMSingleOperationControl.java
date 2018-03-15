package com.javahis.ui.hrm;

import java.awt.Component;
import java.sql.Timestamp;
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
import jdo.sys.Operator;
import jdo.util.Personal;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
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
 * <p> Title: 单人增删医嘱 </p>
 * 
 * <p> Description: </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author wanglong 2014.08.29
 * @version 1.0
 */
public class HRMSingleOperationControl extends TControl {

    private TTable table;
    private TextFormatHRMPackageD packageDetail;// 套餐细项（下拉框）
    private HRMOrder order;// 医嘱对象
    private TParm inParm;
    private String mrNo;
    private String caseNo;
    private String ctz;// 身份
    private String dept;// 科室

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        this.initComponent();// 初始化控件
        inParm = this.getInputParm();
        // System.out.println("-------inParm-------" + inParm);
        if (StringUtil.isNullString(inParm.getValue("CASE_NO"))) {
            this.messageBox("E0024");// 初始化参数错误
            this.closeWindow();
        }
        mrNo = inParm.getValue("MR_NO");
        caseNo = inParm.getValue("CASE_NO");
        ctz = Personal.getDefCtz();
        dept = Operator.getDept();
        this.initData();// 初始化数据
    }

    /**
     * 初始化控件
     */
    public void initComponent() {
        table = (TTable) this.getComponent("TABLE");
        table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this, "onOrderEditComponent");
        table.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE, this, "onValueChanged");
        packageDetail = (TextFormatHRMPackageD) this.getComponent("ORDER_CODE");
    }

    /**
     * 初始化数据
     */
    public void initData() {
        order = new HRMOrder();
        order.onQuery(caseNo, mrNo);
        order.retrieve();
        int row = table.getSelectedRow();
        row = order.insertRow();
        order.setItem(row, "SEQ", order.getItemInt(row, "#ID#"));
        order.setItem(row, "SETMAIN_FLG", "Y");
        order.setItem(row, "HIDE_FLG", "N");
        order.setItem(row, "BILL_FLG", "N");
        order.setItem(row, "EXEC_FLG", "N");
        order.setActive(row, false);
        order.setFilter(" SETMAIN_FLG='Y' ");
        order.filter();
        table.setDataStore(order);
        table.setDSValue();
        this.setValue("AR_AMT", order.getArAmt());// wanglong add 20140909
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
            table.setDSValue(row);
            return false;
        }
        if ("OWN_PRICE_MAIN".equals(colName)) {
            TParm parm = tNode.getTable().getDataStore().getRowParm(tNode.getRow());
            if (parm.getValue("CAT1_TYPE").equals("PHA")
                    || (parm.getValue("ORDER_CAT1_CODE").equals("MAT") && "N".equals(parm
                            .getValue("HIDE_FLG")))) {
                order.setItem(row, "OWN_PRICE", tNode.getValue());
                table.setDSValue(row);
                this.setValue("AR_AMT", order.getArAmt());// wanglong add 20140909
            } else if (parm.getBoolean("SETMAIN_FLG")) {
                this.messageBox("集合医嘱不能更改单价");
                return true;
            }
        }
        if ("DISPENSE_QTY".equals(colName)) {
            if (!tNode.getValue().toString().matches("\\d+")) {
                return true;
            }
//            double oldQty = TypeTool.getDouble(tNode.getOldValue().toString());
            double newQty = TypeTool.getDouble(tNode.getValue().toString());
            TParm parm = tNode.getTable().getDataStore().getRowParm(tNode.getRow());
            if (parm.getValue("CAT1_TYPE").equals("PHA")
                    || (parm.getValue("ORDER_CAT1_CODE").equals("MAT") && "N".equals(parm
                            .getValue("HIDE_FLG")))) {
                order.setItem(row, "DISPENSE_QTY", newQty);
                order.setItem(row, "OWN_AMT",
                              StringTool.round(order.getItemDouble(row, "OWN_PRICE") * newQty, 2));
                order.setItem(row, "AR_AMT", StringTool.round(order.getItemDouble(row, "OWN_AMT")
                        * order.getItemDouble(row, "DISCOUNT_RATE"), 2));
                table.setDSValue(row);
                this.setValue("AR_AMT", order.getArAmt());// wanglong add 20140909
            } else {
                if (parm.getBoolean("SETMAIN_FLG")) {
                    this.messageBox("集合医嘱不能更改数量");
                    return true;
//                    if ((parm.getValue("CAT1_TYPE").equals("LIS") || parm.getValue("CAT1_TYPE").equals("RIS")) && parm.getValue("SETMAIN_FLG").equals("Y")) {
//                        this.messageBox("检验检查不能更改数量");
//                        return true;
//                    }
//                    int groupNo = parm.getInt("ORDERSET_GROUP_NO");
//                    String orderSetCode = parm.getValue("ORDERSET_CODE");
//                    String filterString = order.getFilter();
//                    order.setFilter("CASE_NO='" + caseNo + "' AND ORDERSET_GROUP_NO=" + groupNo
//                            + " AND ORDERSET_CODE'" + orderSetCode + "'");
//                    order.filter();
//                    for (int j = 0; j < order.rowCount(); j++) {
//                        if (order.getItemString(j, "SETMAIN_FLG").equals("Y")) {
//                            order.setItem(j, "DISPENSE_QTY", newQty);
//                            order.setItem(j, "OWN_AMT", order.getItemDouble(j, "OWN_PRICE")
//                                    * newQty);
//                        } else {
//                            double oldValue =
//                                    TypeTool.getDouble(order.getItemData(j, "DISPENSE_QTY"));
//                            order.setItem(j, "DISPENSE_QTY", oldValue / oldQty * newQty);
//                            order.setItem(j,
//                                          "OWN_AMT",
//                                          order.getItemDouble(j, "OWN_PRICE")
//                                                  * order.getItemDouble(j, "DISPENSE_QTY"));
//                        }
//                    }
//                    table.setItem(row,
//                                  "AR_AMT_MAIN",
//                                  TypeTool.getDouble(table.getItemData(row, "OWN_PRICE_MAIN"))
//                                          * TypeTool.getDouble(table.getItemData(row,
//                                                                                 "DISCOUNT_RATE")));
//                    order.setFilter(filterString);
//                    order.filter();
//                    table.setDSValue();
//                    this.setValue("AR_AMT", order.getArAmt());// wanglong add 20140909
                }
            }
            return false;
        }
        if ("DISCOUNT_RATE".equals(colName)) {// wanglong add 20140909
            if (!tNode.getValue().toString().matches("\\d+(\\056?\\d+)?")) {
                return true;
            }
            double rate = TypeTool.getDouble(tNode.getValue());
            if (rate < 0.01 || rate > 1) {
                this.messageBox_("折扣率应在0.01~1之间");
                return true;
            }
            TParm parm = tNode.getTable().getDataStore().getRowParm(tNode.getRow());
            if (parm.getValue("CAT1_TYPE").equals("PHA")) {
                this.messageBox("药品不能修改折扣");
                return true;
            } else {
                if (parm.getBoolean("SETMAIN_FLG")
                        && !parm.getValue("ORDER_CAT1_CODE").equals("MAT")) {
                    int groupNo = parm.getInt("ORDERSET_GROUP_NO");
                    String orderSetCode = parm.getValue("ORDERSET_CODE");
                    String filterString = order.getFilter();
                    order.setFilter("CASE_NO='" + caseNo + "' AND ORDERSET_GROUP_NO=" + groupNo
                            + " AND ORDERSET_CODE'" + orderSetCode + "'");
                    order.filter();
                    for (int j = 0; j < order.rowCount(); j++) {
                        order.setItem(j, "DISCOUNT_RATE", rate);
                        order.setItem(j, "AR_AMT",
                                      StringTool.round(order.getItemDouble(j, "OWN_AMT") * rate, 2));
                    }
                    table.setItem(row, "AR_AMT_MAIN",
                                  StringTool.round(TypeTool.getDouble(table
                                                           .getItemData(row, "OWN_PRICE_MAIN"))
                                                           * rate, 2));
                    order.setFilter(filterString);
                    order.filter();
                    table.setDSValue();
                    this.setValue("AR_AMT", order.getArAmt());// wanglong add 20140909
                } else {
                    int id =
                            TCM_Transform.getInt(tNode.getTable().getDataStore()
                                    .getItemData(tNode.getRow(), "#ID#", order.PRIMARY));
                    table.setItem(row, "DISCOUNT_RATE", rate);
                    table.setItem(row, "AR_AMT_MAIN",
                                  StringTool.round(TypeTool.getDouble(table
                                                           .getItemData(id, "OWN_PRICE_MAIN"))
                                                           * rate, 2));
                    String filterString = order.getFilter();
                    order.setFilter("");
                    order.filter();
                    tNode.getTable().getDataStore().setItem(id, "DISCOUNT_RATE", rate);
                    tNode.getTable()
                            .getDataStore()
                            .setItem(id,
                                     "AR_AMT",
                                     StringTool.round(TypeTool.getDouble(table
                                                              .getItemData(row, "OWN_PRICE_MAIN"))
                                                              * rate, 2));
                    order.setFilter(filterString);
                    order.filter();
                    table.setDSValue();
                    this.setValue("AR_AMT", order.getArAmt());// wanglong add 20140909
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
                table.setDSValue(row);
                this.setValue("AR_AMT", order.getArAmt());// wanglong add 20140909
            } else {
                if (parm.getBoolean("SETMAIN_FLG")) {
                    if (parm.getValue("CAT1_TYPE").equals("LIS")
                            || parm.getValue("CAT1_TYPE").equals("RIS")) {
                        this.messageBox("检验检查不能修改执行科室");
                        return true;
                    }
                    int groupNo = parm.getInt("ORDERSET_GROUP_NO");
                    String buff = order.isFilter() ? TDataStore.FILTER : TDataStore.PRIMARY;
                    int newRow[] = order.getNewRows(buff);
                    for (int i : newRow) {
                        TParm linkParm = order.getRowParm(i, buff);
                        if (!order.isActive(i, buff)) continue;
                        // 找到过滤缓冲区中此医嘱的唯一ID
                        int filterId = (Integer) order.getItemData(i, "#ID#", buff);
                        // 找到过主冲区中此医嘱的唯一ID
                        int primaryId =
                                (Integer) order.getItemData(tNode.getRow(), "#ID#",
                                                            TDataStore.PRIMARY);
                        if (filterId == primaryId) continue;
                        if (linkParm.getInt("ORDERSET_GROUP_NO") == groupNo) {
                            order.setItem(i, "EXEC_DEPT_CODE", tNode.getValue());
                            table.setDSValue(i);
                        }
                    }
                    this.setValue("AR_AMT", order.getArAmt());// wanglong add 20140909
                }
            }
            return false;
        }
        return false;
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
        if (!StringUtil.isNullString(table.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        table.acceptText();
        int[] newRows = order.getNewRows();
        int count = newRows.length;
        for (int i = count - 1; i >= 0; i--) {
            if (order.getItemString(newRows[i], "SETMAIN_FLG").equals("Y")) {
                if (!order.getItemString(newRows[i], "CAT1_TYPE").equals("PHA")
                        && !order.getItemString(newRows[i], "CAT1_TYPE").equals("OTH")) {
                    if (order.getItemString(newRows[i], "ORDERSET_CODE")
                            .equals(parm.getValue("ORDER_CODE"))) {
                        this.messageBox("集合医嘱不能重复添加，一次只能添加一个");
                        return;
                    }
                }
            }
        }
        int nextGroupNo = order.getMaxGroupNo();
        order.setItem(row, "SEQ_NO", order.getMaxSeq());
        order.setItem(row, "MED_APPLY_NO", "");
        order.setItem(row, "ORDERSET_GROUP_NO", nextGroupNo);
        order.setItem(row, "MR_NO", mrNo);
        order.setItem(row, "CASE_NO", caseNo);
        order.setItem(row, "CONTRACT_CODE", inParm.getValue("CONTRACT_CODE"));
        order.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE"));
        order.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC"));
        order.setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC"));
        order.setItem(row, "DISPENSE_QTY", 1.0);
        double disCount = inParm.getDouble("DISCNT");
        if (parm.getValue("CAT1_TYPE").equals("PHA")// 药品和卫材不打折
        // || parm.getValue("ORDER_CAT1_CODE").equals("MAT")//泰心材料能打折
        ) {// modify by wanglong 20130306
            disCount = 1;
        }
        order.setItem(row, "DISCOUNT_RATE", disCount);
        order.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION"));
        order.setItem(row, "DISPENSE_UNIT", parm.getValue("UNIT_CODE"));
        order.setItem(row, "ORIGINAL_PRICE", parm.getDouble("OWN_PRICE"));
        order.setItem(row, "OWN_PRICE", parm.getDouble("OWN_PRICE"));
        order.setItem(row, "NHI_PRICE", parm.getDouble("NHI_PRICE"));// add by wanglong 20130316
        order.setItem(row, "OWN_AMT", StringTool.round(parm.getDouble("OWN_PRICE") * 1.0, 2));
        order.setItem(row, "AR_AMT",
                      StringTool.round(parm.getDouble("OWN_PRICE") * 1.0 * disCount, 2));
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
        order.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
        order.setItem(row, "OPT_TERM", Operator.getIP());
        order.setItem(row, "ORDER_DATE", TJDODBTool.getInstance().getDBTime());
        order.setItem(row, "REGION_CODE", Operator.getRegion());
        order.setItem(row, "DR_CODE", Operator.getID());
        order.setItem(row, "DEPT_CODE", dept);
        order.setItem(row, "SETMAIN_FLG", "Y");
        order.setItem(row, "ORDERSET_CODE", parm.getValue("ORDER_CODE"));
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
            order.setFilter(" SETMAIN_FLG='Y' ");
            order.filter();
            table.setDSValue();
            table.getTable().grabFocus();
            table.setSelectedRow(table.getRowCount() - 1);
            table.setSelectedColumn(1);
            return;
        }
        String sql = HRMFeePackTool.QUERY_ORDERSET.replace("#", parm.getValue("ORDER_CODE"));
        TParm orderSet = new TParm(TJDODBTool.getInstance().select(sql));
        if (orderSet == null || orderSet.getErrCode() != 0) {
            this.messageBox("查询医嘱细项失败");
            return;
        }
        int setCount = orderSet.getCount("ORDER_CODE");
        for (int i = 0; i < setCount; i++) {// 集合医嘱细项
            order.setItem(row, "NEW_FLG", "Y");// add by wanglong 20130425
            order.setItem(row, "SEQ_NO", order.getMaxSeq());
            order.setItem(row, "MED_APPLY_NO", "");
            order.setItem(row, "ORDERSET_GROUP_NO", nextGroupNo);
            order.setItem(row, "MR_NO", mrNo);
            order.setItem(row, "CASE_NO", caseNo);
            order.setItem(row, "CONTRACT_CODE", inParm.getValue("CONTRACT_CODE"));
            order.setItem(row, "ORDER_CODE", orderSet.getValue("ORDER_CODE", i));
            order.setItem(row, "ORDERSET_CODE", orderSet.getValue("ORDERSET_CODE", i));
            order.setItem(row, "ORDER_DESC", orderSet.getValue("ORDER_DESC", i));
            order.setItem(row, "GOODS_DESC", orderSet.getValue("GOODS_DESC", i));
            order.setItem(row, "DISPENSE_QTY", orderSet.getDouble("DOSAGE_QTY", i));
            order.setItem(row, "DISCOUNT_RATE", disCount);
            order.setItem(row, "SPECIFICATION", orderSet.getValue("SPECIFICATION", i));
            order.setItem(row, "DISPENSE_UNIT", orderSet.getValue("UNIT_CODE", i));
            order.setItem(row, "ORIGINAL_PRICE", orderSet.getDouble("OWN_PRICE", i));
            order.setItem(row, "OWN_PRICE", orderSet.getDouble("OWN_PRICE", i));
            order.setItem(row, "NHI_PRICE", orderSet.getDouble("NHI_PRICE", i));
            order.setItem(row,
                          "OWN_AMT",
                          StringTool.round(orderSet.getDouble("OWN_PRICE", i)
                                                   * orderSet.getDouble("DOSAGE_QTY", i), 2));
            order.setItem(row, "AR_AMT", StringTool.round(orderSet.getDouble("OWN_PRICE", i)
                    * orderSet.getDouble("DOSAGE_QTY", i) * disCount, 2));
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
            order.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
            order.setItem(row, "OPT_TERM", Operator.getIP());
            order.setItem(row, "ORDER_DATE", TJDODBTool.getInstance().getDBTime());
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
            order.setItem(row, "SETMAIN_FLG", "Y");
            order.setItem(row, "HIDE_FLG", "N");
            order.setItem(row, "BILL_FLG", "N");
            order.setItem(row, "EXEC_FLG", "N");
            order.setActive(row, false);
        }
        order.setFilter(" SETMAIN_FLG='Y' ");
        order.filter();
        table.setDSValue();
        this.setValue("AR_AMT", order.getArAmt());// wanglong add 20140909
        table.getTable().grabFocus();
        table.setSelectedRow(table.getRowCount() - 1);
        table.setSelectedColumn(1);
    }

    /**
     * 点击套餐细项
     */
    public void onChooseDetail() {// add by wanglong 20130423
        String orderCode = this.getValueString("ORDER_CODE");
        TParm parm = ((TTextFormat) this.getComponent("ORDER_CODE")).getPopupMenuData();
        int row =
                ((TTextFormat) this.getComponent("ORDER_CODE")).getTablePopupMenu()
                        .getSelectedRow();
        String seq = parm.getValue("SEQ", row);
        String packageCode = this.getValueString("PACKAGE_CODE");
        String sql =
                "SELECT A.ORDER_CODE,A.ORDERSET_CODE,A.ORDER_DESC,B.GOODS_DESC,A.SPECIFICATION,A.DISPENSE_UNIT,A.DISPENSE_QTY,"
                        + "A.ORIGINAL_PRICE,A.PACKAGE_PRICE,A.SETMAIN_FLG,A.HIDE_FLG,A.OPTITEM_CODE,A.EXEC_DEPT_CODE,A.DEPT_ATTRIBUTE,"
                        + "B.NHI_PRICE,B.RPTTYPE_CODE,B.DEV_CODE,B.MR_CODE,B.CHARGE_HOSP_CODE,B.CAT1_TYPE,B.ORDER_CAT1_CODE "
                        + "  FROM HRM_PACKAGED A, SYS_FEE B,"
                        + "(SELECT ORDERSET_GROUP_NO FROM HRM_PACKAGED WHERE ORDER_CODE = '&' AND SEQ = @ AND PACKAGE_CODE = '#') CC "
                        + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                        + "   AND A.ORDERSET_GROUP_NO = CC.ORDERSET_GROUP_NO "
                        + "   AND A.PACKAGE_CODE = '#' ORDER BY A.SEQ";
        sql = sql.replaceFirst("&", orderCode);
        sql = sql.replaceFirst("@", seq);
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
    public void comboOrderReturn(TParm parm) {
        int row = table.getRowCount() - 1;
        if (!StringUtil.isNullString(table.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        table.acceptText();
        int[] newRows = order.getNewRows();
        int count = newRows.length;
        for (int i = count - 1; i >= 0; i--) {
            if (order.getItemString(newRows[i], "SETMAIN_FLG").equals("Y")) {
                if (!order.getItemString(newRows[i], "CAT1_TYPE").equals("PHA")
                        && !order.getItemString(newRows[i], "CAT1_TYPE").equals("OTH")) {
                    if (order.getItemString(newRows[i], "ORDERSET_CODE")
                            .equals(parm.getValue("ORDER_CODE"))) {
                        this.messageBox("集合医嘱不能重复添加，一次只能添加一个");
                        return;
                    }
                }
            }
        }
        double originalAmt=0;
        double packageAmt=0;
        for (int i = 0; i < parm.getCount(); i++) {
            originalAmt += parm.getDouble("ORIGINAL_PRICE", i);
            packageAmt += parm.getDouble("PACKAGE_PRICE", i);
        }
        double orderSetQty = parm.getDouble("DISPENSE_QTY", 0);
        int nextGroupNo = order.getMaxGroupNo();
        for (int i = 0; i < parm.getCount(); i++) {
            order.setItem(row, "NEW_FLG", "Y");//=================add by wanglong 20130425
            order.setItem(row, "SEQ_NO", order.getMaxSeq());
            order.setItem(row, "MED_APPLY_NO", "");
            order.setItem(row, "ORDERSET_GROUP_NO", nextGroupNo);
            order.setItem(row, "MR_NO", mrNo);
            order.setItem(row, "CASE_NO", caseNo);
            order.setItem(row, "CONTRACT_CODE", inParm.getValue("CONTRACT_CODE"));
            order.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE", i));
            order.setItem(row, "ORDERSET_CODE", parm.getValue("ORDERSET_CODE", i));
            order.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC", i));
            order.setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC", i));
            int qty = TCM_Transform.getInt(parm.getDouble("DISPENSE_QTY", i) / orderSetQty);
            order.setItem(row, "DISPENSE_QTY", qty);
            double disCount = StringTool.round(packageAmt / originalAmt, 2);//计算折扣
            order.setItem(row, "DISCOUNT_RATE", disCount);
            order.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION", i));
            order.setItem(row, "DISPENSE_UNIT", parm.getValue("DISPENSE_UNIT", i));
            order.setItem(row, "OWN_PRICE", parm.getDouble("PACKAGE_PRICE", i));
            order.setItem(row, "NHI_PRICE", 0);
            order.setItem(row, "OWN_AMT", StringTool.round(parm.getDouble("PACKAGE_PRICE", i) * qty, 2));
            order.setItem(row, "AR_AMT",
                          StringTool.round(parm.getDouble("PACKAGE_PRICE", i) * qty * disCount, 2));
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
            order.setItem(row, "OPT_USER", Operator.getID());
            order.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
            order.setItem(row, "OPT_TERM", Operator.getIP());
            order.setItem(row, "ORDER_DATE", TJDODBTool.getInstance().getDBTime());
            order.setItem(row, "REGION_CODE", Operator.getRegion());
            order.setItem(row, "DR_CODE", Operator.getID());
            order.setItem(row, "DEPT_CODE", dept);
            order.setItem(row, "SETMAIN_FLG", parm.getData("SETMAIN_FLG", i));
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
            order.setItem(row, "DEPT_ATTRIBUTE", parm.getValue("DEPT_ATTRIBUTE", i));
            order.setItem(row, "HIDE_FLG", parm.getData("HIDE_FLG", i));
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
        }
        order.setFilter(" SETMAIN_FLG='Y' ");
        order.filter();
        table.setDSValue();
        this.setValue("AR_AMT", order.getArAmt());// wanglong add 20140909
        table.getTable().grabFocus();
        table.setSelectedRow(table.getRowCount() - 1);
        table.setSelectedColumn(1);
    }

    /**
     * 删除医嘱
     */
    public void onDelRow() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        if (row == table.getRowCount() - 1) {
            return;
        }
        String filter = order.getFilter();
        TParm parm = table.getDataStore().getRowParm(row);
        String orderCode = parm.getValue("ORDER_CODE");
        String orderSetCode = parm.getValue("ORDERSET_CODE");
        int groupNo = parm.getInt("ORDERSET_GROUP_NO");
        String cat1Type = parm.getValue("CAT1_TYPE");
        String orderCat1Code = parm.getValue("ORDER_CAT1_CODE");
        String hideFlg = parm.getValue("HIDE_FLG");
        String medApplyNo = parm.getValue("MED_APPLY_NO");
        String execFlg = parm.getValue("EXEC_FLG");
        if (StringUtil.isNullString(orderCode)) {
            return;
        }
        if (execFlg.equals("Y")) {
            this.messageBox("该医嘱已执行，不能删除");
        }
        if (cat1Type.equals("PHA") || (orderCat1Code.equals("MAT") && hideFlg.equals("N"))) {// 非集合医嘱
            if (!order.getItemString(row, "BILL_NO").equals("")) {
                this.messageBox("该医嘱已结算，不能删除");
                return;
            }
            if(!order.getItemString(row, "ORDER_CODE").equals(parm.getValue("ORDER_CODE"))){
                this.messageBox("操作异常2");
                return;
            }
            order.deleteRow(row);
            table.setDSValue();
        } else {// 集合医嘱
            if (!medApplyNo.equals("")) {// 已开立的医嘱
                String status =
                        StringUtil.getDesc("MED_APPLY",
                                           "STATUS",
                                           "CAT1_TYPE='#' AND APPLICATION_NO='#' AND ORDER_CODE='#'"
                                                   .replaceFirst("#", cat1Type)
                                                   .replaceFirst("#", medApplyNo)
                                                   .replaceFirst("#", orderCode));
                if (!status.equals("") && !status.equals("0") && !status.equals("1")) {
                    this.messageBox("该医嘱已执行，不能删除");
                    return;
                }
                String groupSql =
                        "SELECT DISTINCT B.ORDER_CODE,B.ORDER_DESC,B.ORDERSET_GROUP_NO "
                                + "  FROM HRM_ORDER A, HRM_ORDER B "
                                + " WHERE A.CASE_NO = B.CASE_NO    "
                                + "   AND A.MED_APPLY_NO = B.MED_APPLY_NO "
                                + "   AND A.CASE_NO = '#'             "
                                + "   AND A.ORDER_CODE = '#'        "
                                + "   AND A.MED_APPLY_NO = '#'    ";
                groupSql = groupSql.replaceFirst("#", caseNo);
                groupSql = groupSql.replaceFirst("#", orderCode);
                groupSql = groupSql.replaceFirst("#", medApplyNo);
                TParm groupParm = new TParm(TJDODBTool.getInstance().select(groupSql));
                if (groupParm.getErrCode() != 0) {
                    this.messageBox("查询条码共用信息失败 " + groupParm.getErrText());
                    return;
                }
                Set<Integer> groupNoSet = new HashSet<Integer>();
                groupNoSet.add(groupNo);
                if (status.equals("1")) {
                    if (groupParm.getCount() > 1) {
                        String orderStr = "";
                        for (int i = 0; i < groupParm.getCount(); i++) {
                            if (!groupParm.getValue("ORDERSET_GROUP_NO", i).equals(groupNo)) {
                                orderStr += groupParm.getValue("ORDER_DESC", i) + "，";
                                groupNoSet.add(groupParm.getInt("ORDERSET_GROUP_NO", i));
                            }
                        }
                        if (orderStr.length() > 0) {
                            orderStr = orderStr.substring(0, orderStr.length() - 1);
                        }
                        int type =
                                this.messageBox("提示", "该医嘱和 " + orderStr
                                        + " 共用条码号！\n删除它，将会同时删除和它共用条码的其他医嘱！\n是否继续？", 2);
                        if (type == 2) {
                            return;
                        }
                    }
                }
                order.setFilter("");
                order.filter();
                int count = order.rowCount();
                for (int i = count - 1; i >= 0; i--) {
                    if (groupNoSet.contains(order.getItemInt(i, "ORDERSET_GROUP_NO"))) {
                        if (!order.getItemString(i, "BILL_NO").equals("")) {
                            this.messageBox("该医嘱已结算，不能删除");
                            order.setFilter(filter);
                            order.filter();
                            table.setDSValue();
                            this.setValue("AR_AMT", order.getArAmt());// wanglong add 20140909
                            return;
                        }
                        order.deleteRow(i);
                    }
                }
                order.setFilter(filter);
                order.filter();
                table.setDSValue();
                this.setValue("AR_AMT", order.getArAmt());// wanglong add 20140909
            } else {// 界面上新增的医嘱，或者非检验检查
                order.setFilter("");
                order.filter();
                int count = order.rowCount();
                for (int i = count - 1; i >= 0; i--) {
                    if (orderSetCode.equalsIgnoreCase(order.getItemString(i, "ORDERSET_CODE"))) {
                        if (groupNo == order.getItemInt(i, "ORDERSET_GROUP_NO")) {
                            if (!order.getItemString(i, "BILL_NO").equals("")) {
                                this.messageBox("该医嘱已结算，不能删除");
                                order.setFilter(filter);
                                order.filter();
                                table.setDSValue();
                                this.setValue("AR_AMT", order.getArAmt());// wanglong add 20140909
                                return;
                            }
                            if(!order.getItemString(i, "ORDERSET_CODE").equals(parm.getValue("ORDER_CODE"))){
                                this.messageBox("操作异常3");
                                return;
                            }
                            order.deleteRow(i);
                        } else {
                            break;
                        }
                    }
                }
                order.setFilter(filter);
                order.filter();
                table.setDSValue();
                this.setValue("AR_AMT", order.getArAmt());// wanglong add 20140909
            }
            this.setValue("AR_AMT", order.getArAmt());// wanglong add 20140909
        }
    }

    /**
     * 保存
     */
    public void onSave() {
        table.acceptText();
        int pageCount = order.rowCount();
        if (pageCount <= 0) {
            this.messageBox("没有保存数据");
            return;
        }
        order.setFilter("");
        order.filter();
        String buff = order.isFilter() ? TDataStore.FILTER : TDataStore.PRIMARY;
        int[] newRows = order.getNewRows(buff);// 记录新增的医嘱的行号 add by wanglong 20130226
        int seqNo = order.getOrderMaxSeqNo(caseNo);// 下一序号
        int orderGroupNo = order.getOrderMaxGroupNo(caseNo);// 当前最大GroupNo
        boolean flag = false;// 记录新增的医嘱中是否包含检验检查
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        for (int insertRow : newRows) {// ================处理新增的医嘱
            if (order.getItemString(insertRow, "ORDER_CODE").equals("")) {
                continue;
            }
            order.setItem(insertRow, "OPT_DATE", now);
            order.setItem(insertRow, "ORDER_DATE", now);
//            if (order.getItemString(insertRow, "SEQ_NO").equals("")) {
                order.setItem(insertRow, "SEQ_NO", seqNo);
                seqNo++;
//            }
            if (order.getItemString(insertRow, "SETMAIN_FLG").equals("Y")) {
                orderGroupNo++;
            }
//            if (order.getItemInt(insertRow, "ORDERSET_GROUP_NO") == -1) {
                order.setItem(insertRow, "ORDERSET_GROUP_NO", orderGroupNo);
//            }
            if ("Y".equals(order.getItemString(insertRow, "SETMAIN_FLG"))
                    && ("LIS".equals(order.getItemString(insertRow, "CAT1_TYPE")) || "RIS"
                            .equals(order.getItemString(insertRow, "CAT1_TYPE")))
                    && "".equals(order.getItemString(insertRow, "MED_APPLY_NO"))) {
                // System.out.println("=====sysFee===="+sysFee);
                flag = true;
                String labNo = order.getLabNo(insertRow, inParm);
                order.setItem(insertRow, "MED_APPLY_NO", labNo);
            }
        }
        List<String> deleteMedApplySqlList = new ArrayList<String>();
        String[] deleteMedApplySql = new String[]{};
        List<TParm> hl7DelList = new ArrayList<TParm>();
        TParm deleteParm = order.getBuffer(TDataStore.DELETE);// ================处理删除的检验检查医嘱
        for (int i = 0; i < deleteParm.getCount(); i++) {
            if (deleteParm.getValue("SETMAIN_FLG", i).equals("Y")) {
                if (deleteParm.getValue("CAT1_TYPE", i).equals("LIS")
                        || deleteParm.getValue("CAT1_TYPE", i).equals("RIS")
                        || !deleteParm.getValue("MED_APPLY_NO", i).equals("")) {
                    String where =
                            "CAT1_TYPE='#' AND APPLICATION_NO='#' AND ORDER_CODE='#'"
                                    .replaceFirst("#", deleteParm.getValue("CAT1_TYPE", i))
                                    .replaceFirst("#", deleteParm.getValue("MED_APPLY_NO", i))
                                    .replaceFirst("#", deleteParm.getValue("ORDER_CODE", i));
                    String status = StringUtil.getDesc("MED_APPLY", "STATUS", where);
                    if (status.equals("") || status.equals("0")) {// 未发送HL7消息的医嘱
                        String sql =
                                "DELETE FROM MED_APPLY WHERE CAT1_TYPE='#' AND APPLICATION_NO='#' AND ORDER_CODE='#'";
                        sql =
                                sql.replaceFirst("#", deleteParm.getValue("CAT1_TYPE", i))
                                        .replaceFirst("#", deleteParm.getValue("MED_APPLY_NO", i))
                                        .replaceFirst("#", deleteParm.getValue("ORDER_CODE", i));
                        deleteMedApplySqlList.add(sql);
                    } else {// 发送过HL7消息的医嘱
                        TParm delTemp = new TParm();
                        delTemp.setData("ADM_TYPE", "H");
                        delTemp.setData("PAT_NAME", inParm.getValue("PAT_NAME"));
                        delTemp.setData("CAT1_TYPE", deleteParm.getValue("CAT1_TYPE", i));
                        delTemp.setData("CASE_NO", caseNo);
                        delTemp.setData("LAB_NO", deleteParm.getValue("MED_APPLY_NO", i));
                        delTemp.setData("ORDER_NO", caseNo);
                        delTemp.setData("SEQ_NO", deleteParm.getValue("SEQ_NO", i));
                        delTemp.setData("FLG", "1");
                        // try {
                        // if (Hl7Communications.getInstance().IsExeOrder(delTemp, "H")) {
                        // continue;
                        // }
                        // }
                        // catch (Exception ex) {
                        // System.err.print("检查已执行判断失败。");
                        // ex.printStackTrace();
                        // }
                        hl7DelList.add(delTemp);
                    }
                }
            }
        }
        deleteMedApplySql = (String[]) deleteMedApplySqlList.toArray(new String[0]);
        String[] updateSql = order.getUpdateSQL();
        String[] sql = new String[]{};
        sql = StringTool.copyArray(sql, updateSql);
        sql = StringTool.copyArray(sql, deleteMedApplySql);
        sql = StringTool.copyArray(sql, order.getMedApply().getUpdateSQL());
//        for (int i = 0; i < sql.length; i++) {
//            System.out.println("------------sql[" + i + "]---------" + sql[i]);
//        }
        TParm parm = new TParm();
        Map<String, String[]> inMap = new HashMap<String, String[]>();
        inMap.put("SQL", sql);
        parm.setData("IN_MAP", inMap);
        TParm result =
                TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "onSave", parm);
        if (result.getErrCode() != 0) {
            this.messageBox("保存失败 " + result.getErrText());
            return;
        }
        // 发送HL7消息
        if (hl7DelList.size() > 0) {
            // System.out.println("----------------hl7DelList---------" + hl7DelList);
            TParm hl7Parm = Hl7Communications.getInstance().Hl7Message(hl7DelList);
            if (hl7Parm.getErrCode() < 0) {
                this.messageBox("发送HL7删除消息失败 " + hl7Parm.getErrText());
                onClear();
            }
        }
        List<TParm> hl7ReportList = new ArrayList<TParm>();
        if (inParm.getValue("COVER_FLG").equals("Y")) {
            if (flag == true) {
                this.getHl7List(hl7ReportList, caseNo);
            }
            if (hl7ReportList.size() > 0) {
                // System.out.println("-------------hl7ReportList---------" + hl7ReportList);
                TParm hl7Parm = Hl7Communications.getInstance().Hl7Message(hl7ReportList);
                if (hl7Parm.getErrCode() < 0) {
                    this.messageBox("发送HL7报到消息失败 " + hl7Parm.getErrText());
                    onClear();
                    return;
                }
            }
        }
        // add by wanglong 20130412 增加日志记录功能
        String orderList = "";// add by wanglong 20130412
        for (int i = 0; i < deleteParm.getCount(); i++) {
            if (deleteParm.getValue("SETMAIN_FLG", i).equals("Y")) {
                String orderDesc = deleteParm.getValue("ORDER_DESC", i);
                orderList = orderList + orderDesc + "\r\n";
            }
        }
        if (orderList.length() > 0) {
            String nowStr = StringTool.getString(now, "yyyy/MM/dd HH:mm:ss");
            String log = "---------------------------------------------\r\n";
            log = log + "****健检单人操作日志(" + nowStr + ")****\r\n";
            log = log + "操作人员：" + Operator.getName() + "(" + Operator.getID() + ")\r\n";
            log = log + "操作终端地址：" + Operator.getIP() + "\r\n";
            log = log + "被操作团体代码：" + inParm.getValue("COMPANY_CODE") + "\r\n";
            log = log + "被操作合同：" + inParm.getValue("CONTRACT_CODE") + "\r\n";
            log = log + "被操作人员：" + inParm.getValue("PAT_NAME") + "\r\n";
            log = log + "***************删除医嘱列表******************\r\n";
            log = log + orderList.substring(0, orderList.length() - 1) + "\r\n";
            log = log + "---------------------------------------------\r\n";
            TParm logParm = new TParm();
            logParm.setData("LOG", log);
            TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction", "writeLog", logParm);
        }
        // add end
        this.messageBox("P0001");// 保存成功
        this.onClear();
        // this.setReturnValue("SUCCESS");
        // this.closeWindow();
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
    public List<TParm> getHl7List(List<TParm> listHl7, String caseNo) {// add by wanglong 20130312
        String sql =
                "SELECT CAT1_TYPE,PAT_NAME,CASE_NO,MR_NO,APPLICATION_NO AS LAB_NO,ORDER_NO,SEQ_NO "
                        + "FROM MED_APPLY WHERE ADM_TYPE='H' AND CASE_NO='" + caseNo
                        + "' AND SEND_FLG < 2 AND STATUS <> 9";
        // System.out.println("SQLMED==" + sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        int rowCount = parm.getCount();
//        String preLabNo = "";
        for (int i = 0; i < rowCount; i++) {
            TParm temp = parm.getRow(i);
            String labNo = temp.getValue("LAB_NO");
            // if (!preLabNo.equals(labNo)) {//delete by wanglong 20130402
            temp.setData("ADM_TYPE", "H");
            temp.setData("FLG", "0");
            // System.out.println("PAT_NAME----:" + temp.getValue("PAT_NAME") + ":LAB_NO----:" +
            // temp.getValue("LAB_NO"));
            listHl7.add(temp);
//            preLabNo = labNo;
            // }
        }
        // System.out.println("listHl7.size----:" + listHl7.size());
        return listHl7;
    }

    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("ORDER_CODE;PACKAGE_CODE");
        packageDetail.getPopupMenuData().getData().clear();
        packageDetail.filter();
        initData();
    }
    
    /**
	 * 手术室补充计费
	 */
	public void onOperation() {
		
		TParm operationParm = new TParm();
		TParm dataParm = new TParm();
		dataParm.setData("CASE_NO",caseNo);
		dataParm.setData("PACK", "DEPT", Operator.getDept());
		operationParm = (TParm) this.openDialog(
				"%ROOT%\\config\\sys\\sys_fee\\SYSFEE_ORDSETOPTION.x", dataParm,
				false);
		
		if (null==operationParm) {//==pangben  2013-08-05
			return;
		}
		TParm parm_obj = new TParm();
		TParm result=new TParm();
		for (int i = 0; i < operationParm.getCount("ORDER_CODE"); i++) {
			String sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE = '"
				+ operationParm.getValue("ORDER_CODE", i) + "' AND ACTIVE_FLG = 'Y'";
			parm_obj = new TParm(TJDODBTool.getInstance().select(sql));
			if(!parm_obj.getValue("CAT1_TYPE",0).toString().equals("PHA")){
				this.messageBox(""+parm_obj.getValue("ORDER_CODE",0)+" "+parm_obj.getValue("ORDER_DESC",0)+" 为非药品,不可传回");
				continue;
			}
			if (parm_obj == null || parm_obj.getCount() <= 0 ) {
				continue;
			}
			
			int dispenseQty=operationParm.getInt("DOSAGE_QTY",i);
			String dispenseUnit=operationParm.getValue("DOSAGE_UNIT",i);
			result.setData("ORDER_CODE",i,parm_obj.getValue("ORDER_CODE",0));
			result.setData("ORDERSET_CODE",i,parm_obj.getValue("ORDER_CODE",0));
			result.setData("ORDER_DESC",i,parm_obj.getValue("ORDER_DESC",0));
			result.setData("GOODS_DESC",i,parm_obj.getValue("GOODS_DESC",0));
			result.setData("DISPENSE_QTY",i,dispenseQty);
			result.setData("SPECIFICATION",i,parm_obj.getValue("SPECIFICATION",0));
			result.setData("DISPENSE_UNIT",i,dispenseUnit);
			result.setData("PACKAGE_PRICE",i,parm_obj.getDouble("OWN_PRICE",0));
			result.setData("ORIGINAL_PRICE",i,parm_obj.getDouble("OWN_PRICE",0));
			result.setData("RPTTYPE_CODE",i,parm_obj.getValue("RPTTYPE_CODE",0));
			result.setData("OPTITEM_CODE",i,parm_obj.getValue("OPTITEM_CODE",0));
			result.setData("DEV_CODE",i,parm_obj.getValue("DEV_CODE",0));
			result.setData("MR_CODE",i,parm_obj.getValue("MR_CODE",0));
			result.setData("CHARGE_HOSP_CODE",i,parm_obj.getValue("CHARGE_HOSP_CODE",0));
			result.setData("CAT1_TYPE",i,parm_obj.getValue("CAT1_TYPE",0));
			result.setData("ORDER_CAT1_CODE",i,parm_obj.getValue("ORDER_CAT1_CODE",0));
			result.setData("EXEC_DEPT_CODE",i,Operator.getDept());
			result.setData("HIDE_FLG",i,"Y");
			result.setData("SETMAIN_FLG",i,"Y");
			
	        
		}
		result.setCount(result.getCount("PACKAGE_PRICE"));
		operationOrderReturn(result);
	}
	
	 /**
     * 套餐细相新增医嘱方法,如有细相，则也新增细相信息，并只显示主项
     * 
     * @param parm
     */
    public void operationOrderReturn(TParm parm) {
        int row = table.getRowCount() - 1;
        if (!StringUtil.isNullString(table.getItemString(row, "ORDER_CODE"))) {
            return;
        }
        table.acceptText();
        
       
        double originalAmt=0;
        double packageAmt=0;
        for (int i = 0; i < parm.getCount(); i++) {
            originalAmt += parm.getDouble("ORIGINAL_PRICE", i);
            packageAmt += parm.getDouble("PACKAGE_PRICE", i);
        }
        //double orderSetQty = parm.getInt("DISPENSE_QTY", 0);
        int nextGroupNo = order.getMaxGroupNo();
        for (int i = 0; i < parm.getCount(); i++) {
            order.setItem(row, "NEW_FLG", "Y");//=================add by wanglong 20130425
            order.setItem(row, "SEQ_NO", order.getMaxSeq());
            order.setItem(row, "MED_APPLY_NO", "");
            order.setItem(row, "ORDERSET_GROUP_NO", nextGroupNo);
            order.setItem(row, "MR_NO", mrNo);
            order.setItem(row, "CASE_NO", caseNo);
            order.setItem(row, "CONTRACT_CODE", inParm.getValue("CONTRACT_CODE"));
            order.setItem(row, "ORDER_CODE", parm.getValue("ORDER_CODE", i));
            order.setItem(row, "ORDERSET_CODE", parm.getValue("ORDERSET_CODE", i));
            order.setItem(row, "ORDER_DESC", parm.getValue("ORDER_DESC", i));
            order.setItem(row, "GOODS_DESC", parm.getValue("GOODS_DESC", i));
            int qty = parm.getInt("DISPENSE_QTY", i);
            order.setItem(row, "DISPENSE_QTY", qty);
            double disCount = StringTool.round(packageAmt / originalAmt, 2);//计算折扣
            order.setItem(row, "DISCOUNT_RATE", disCount);
            order.setItem(row, "SPECIFICATION", parm.getValue("SPECIFICATION", i));
            order.setItem(row, "DISPENSE_UNIT", parm.getValue("DISPENSE_UNIT", i));
            order.setItem(row, "OWN_PRICE", parm.getDouble("PACKAGE_PRICE", i));
            order.setItem(row, "NHI_PRICE", 0);
            order.setItem(row, "OWN_AMT", StringTool.round(parm.getDouble("PACKAGE_PRICE", i) * qty, 2));
            order.setItem(row, "AR_AMT",
                          StringTool.round(parm.getDouble("PACKAGE_PRICE", i) * qty * disCount, 2));
            order.setItem(row, "RPTTYPE_CODE", parm.getData("RPTTYPE_CODE", i));
            order.setItem(row, "OPTITEM_CODE", parm.getData("OPTITEM_CODE", i));
            order.setItem(row, "DEV_CODE", parm.getData("DEV_CODE", i));
            
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
            order.setItem(row, "OPT_USER", Operator.getID());
            order.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
            order.setItem(row, "OPT_TERM", Operator.getIP());
            order.setItem(row, "ORDER_DATE", TJDODBTool.getInstance().getDBTime());
            order.setItem(row, "REGION_CODE", Operator.getRegion());
            order.setItem(row, "DR_CODE", Operator.getID());
            order.setItem(row, "DEPT_CODE", dept);
            order.setItem(row, "SETMAIN_FLG", parm.getData("SETMAIN_FLG", i));
            
            order.setItem(row, "DEPT_ATTRIBUTE", "");
            order.setItem(row, "HIDE_FLG", parm.getData("HIDE_FLG", i));
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
        }
        order.setFilter(" SETMAIN_FLG='Y' ");
        order.filter();
        table.setDSValue();
        this.setValue("AR_AMT", order.getArAmt());// wanglong add 20140909
        table.getTable().grabFocus();
        table.setSelectedRow(table.getRowCount() - 1);
        table.setSelectedColumn(1);
    }
}
