package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import com.javahis.system.textFormat.TextFormatINVOrg;
import com.javahis.system.textFormat.TextFormatINVReason;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TRadioButton;
import java.util.Date;
import com.dongyang.ui.TMenuItem;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.inv.INVSQL;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import java.awt.Component;
import com.dongyang.ui.TComboBox;
import com.javahis.util.StringUtil;
import java.util.Vector;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TypeTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import com.dongyang.data.TNull;
import jdo.inv.InvRequestDTool;

/**
 * <p>
 * Title: 物资申请作业Control
 * </p>
 *
 * <p>
 * Description: 物资申请作业Control
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.11.17
 * @version 1.0
 */

public class INVRequestControl
    extends TControl {
    public INVRequestControl() {
    }

    private TTable table_m;

    private TTable table_d;

    // 全部部门权限
    private boolean dept_flg = true;

    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        initPage();
    }

    /**
     * 查询
     */
    public void onQuery() {
        if (!dept_flg) {
            if ("".equals(this.getValueString("TO_ORG_CODE_Q"))) {
                this.messageBox("请选择查询部门");
                return;
            }
        }

        TParm parm = new TParm();
        // 订购状态
        if (this.getRadioButton("RadioButton1").isSelected()) {
            parm.setData("FINAL_FLG", "Y");
        }
        else {
            parm.setData("FINAL_FLG", "N");
        }
        // 申请类别
        if (!"".equals(this.getValueString("REQUEST_TYPE_Q"))) {
            parm.setData("REQUEST_TYPE", this.getValueString("REQUEST_TYPE_Q"));
        }
        // 申请部门
        if (!"".equals(this.getValueString("TO_ORG_CODE_Q"))) {
            parm.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE_Q"));
        }
        // 查询时间
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
            parm.setData("START_DATE", this.getValue("START_DATE"));
            parm.setData("END_DATE", this.getValue("END_DATE"));
        }
        // 申请单号
        if (!"".equals(this.getValueString("REQUEST_NO_Q"))) {
            parm.setData("REQUEST_NO", this.getValueString("REQUEST_NO_Q"));
        }
        TParm inparm = new TParm();
        inparm.setData("REQ_M", parm.getData());
        // 查询
        TParm result = TIOM_AppServer.executeAction(
            "action.inv.INVRequestAction", "onQueryM", inparm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            table_m.removeRowAll();
            return;
        }
        System.out.println("result:"+result);  
        table_m.setParmValue(result);

    }

    /**
     * 保存方法
     */
    public void onSave() {
        if (!checkData()) {
            return;
        }
        TParm parm = new TParm();
        getRequestMData(parm); // 取得申请单主项数据
        getRequestDData(parm); // 取得申请单细项数据
        TParm result = new TParm();
        if ("".equals(this.getValueString("REQUEST_NO"))) {
            // 新增申请单
            result = TIOM_AppServer.executeAction(
                "action.inv.INVRequestAction", "onInsert", parm);
        }
        else {
            if (!checkUpdateFlg()) {
                this.messageBox("申请单部分数据已出库不可更新");
                return;
            }
            // 更新申请单
            result = TIOM_AppServer.executeAction(
                "action.inv.INVRequestAction", "onUpdate", parm);
        }
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }

    /**
     * 清空方法
     */
    public void onClear(){
        getRadioButton("RadioButton2").setSelected(true);
        getComboBox("REQUEST_TYPE").setEnabled(true);
        getTextFormat("TO_ORG_CODE").setEnabled(true);
        getTextFormat("FROM_ORG_CODE").setEnabled(true);
        String clearString =
            "START_DATE;END_DATE;REQUEST_TYPE_Q;TO_ORG_CODE_Q;REQUEST_NO_Q;"
            + "REQUEST_NO;REQUEST_DATE;TO_ORG_CODE;FROM_ORG_CODE;"
            + "REN_CODE;REMARK;FINAL_FLG;URGENT_FLG;REQUEST_TYPE";
        this.clearValue(clearString);
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("REQUEST_DATE", date);
        // 画面状态
        //( (TMenuItem) getComponent("delete")).setEnabled(false);
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();
        createNewRow();
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        int row_m = table_m.getSelectedRow();
        int row_d = table_d.getSelectedRow();
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("REQUEST_NO", this.getValueString("REQUEST_NO"));
        if (row_d >= 0) {
            // 删除申请单细项
            if ("".equals(this.getValueString("REQUEST_NO")) &&
                !"".equals(table_d.getItemString(row_d, "INV_CHN_DESC"))) {
                table_d.removeRow(row_d);
                return;
            }
            else if ("".equals(this.getValueString("REQUEST_NO")) &&
                     "".equals(table_d.getItemString(row_d, "INV_CHN_DESC"))) {
                return;
            }
            else if (this.messageBox("删除", "确定是否删除申请单细项", 2) == 0) {
                if (!checkUpdateFlg()) {
                    this.messageBox("申请单单部分数据已出库不可删除");
                    return;
                }
                table_d.removeRow(row_d);
                this.onSave();
                this.messageBox("删除成功");
            }
        }
        else if (row_m >= 0) {
            // 删除申请单主项
            if (this.messageBox("删除", "确定是否删除申请单", 2) == 0) {
                if (!checkUpdateFlg()) {
                    this.messageBox("申请单部分数据已出库不可删除");
                    return;
                }
                result = TIOM_AppServer.executeAction(
                    "action.inv.INVRequestAction", "onDelete", parm);
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("删除失败");
                    return;
                }
                table_m.removeRow(row_m);
                table_d.removeRowAll();
                this.messageBox("删除成功");
            }
        }
        else {
            this.messageBox("没有选中项");
            return;
        }
    }


    /**
     * 检测申请单是否有出库数据
     * @return boolean
     */
    private boolean checkUpdateFlg() {
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!"".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                if (table_d.getItemDouble(i, "ACTUAL_QTY") > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 主项表格(TABLE_M)单击事件
     */
    public void onTableMClicked() {
        int row = table_m.getSelectedRow();
        if (row != -1) {
            getComboBox("REQUEST_TYPE").setEnabled(false);
            getTextFormat("TO_ORG_CODE").setEnabled(false);
            getTextFormat("FROM_ORG_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            table_d.setSelectionMode(0);
            // 主项信息(TABLE中取得)
            setValue("REQUEST_DATE", table_m.getItemTimestamp(row,
                "REQUEST_DATE"));
            setValue("REQUEST_NO", table_m.getItemString(row, "REQUEST_NO"));
            setValue("REQUEST_TYPE", table_m.getItemString(row, "REQUEST_TYPE"));
            setValue("TO_ORG_CODE", table_m.getItemString(row, "TO_ORG_CODE"));
            setValue("FROM_ORG_CODE",
                     table_m.getItemString(row, "FROM_ORG_CODE"));
            setValue("REN_CODE",
                     table_m.getItemString(row, "REN_CODE")); 
            setValue("REMARK", table_m.getItemString(row, "REMARK"));
            setValue("FINAL_FLG",
                     "Y".equals(table_m.getParmValue().getData("FINAL_FLG", row)) ? "完成" :
                     "未完成");
            setValue("URGENT_FLG", table_m.getItemString(row, "URGENT_FLG"));

            // 明细信息
            TParm parm = new TParm();
            parm.setData("REQUEST_NO",
                         table_m.getItemString(row, "REQUEST_NO"));

            TParm result = InvRequestDTool.getInstance().onQuery(parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("没有申请明细");
                return;
            }
            table_d.removeRowAll();  
            table_d.setParmValue(result);
            //createNewRow();
        }
    }


    /**
     * 数据检核
     * @return boolean
     */
    private boolean checkData() {
        if ("".equals(this.getValueString("REQUEST_TYPE"))) {
            this.messageBox("单据类别不能为空");
            return false;
        }
        if ("".equals(this.getValueString("TO_ORG_CODE"))) {
            this.messageBox("申请部门不能为空");
            return false;
        }
        if(!this.getValueString("REQUEST_TYPE").equals("WAS")){
        	 if ("".equals(this.getValueString("FROM_ORG_CODE"))) {
                 this.messageBox("接受部门不能为空");
                 return false;
             }	
        }
       
        if (table_d.getRowCount() <= 1) {
            this.messageBox("没有申请细项信息");
            return false; 
        }
        for (int i = 0; i < table_d.getRowCount(); i++) {
        	if (table_d.getRowCount()==1) {
        		this.messageBox("没有申请细项信息");
        		 return false;
			}
            if (!"".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                if (table_d.getItemDouble(i, "QTY") <= 0) {
                    this.messageBox("申请数量不能小于或等于0");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 取得申请单主项数据
     * @param parm TParm
     * @return TParm
     */
    private TParm getRequestMData(TParm parm) {
        TParm inparm = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        // 申请单号
        if ("".equals(this.getValueString("REQUEST_NO"))) {
            inparm.setData("REQUEST_NO",
                           SystemTool.getInstance().getNo("ALL", "INV",
                "INV_REQUEST", "No"));
        }
        else {
            inparm.setData("REQUEST_NO", this.getValueString("REQUEST_NO"));
        }
        // 单号类别
        inparm.setData("REQUEST_TYPE", this.getValueString("REQUEST_TYPE"));
        // 申请日期
        inparm.setData("REQUEST_DATE", this.getValue("REQUEST_DATE"));
        // 接受申请部门
        inparm.setData("FROM_ORG_CODE", this.getValueString("FROM_ORG_CODE"));
        // 申请部门
        inparm.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE"));
        // 申请原因
        inparm.setData("REN_CODE", this.getValueString("REN_CODE"));
        // 紧急注记
        inparm.setData("URGENT_FLG", this.getValueString("URGENT_FLG"));
        // 备注
        inparm.setData("REMARK", this.getValue("REMARK"));
        // 申请状态
        inparm.setData("FINAL_FLG", "N");
        // 实际金额
        inparm.setData("ACTUAL_AMT", this.getValue("SUM_MONEY"));
        // OPT
        inparm.setData("OPT_USER", Operator.getID());
        inparm.setData("OPT_DATE", date);
        inparm.setData("OPT_TERM", Operator.getIP());

        parm.setData("REQ_M", inparm.getData());
        return parm;
    }

    /**
     * 取得申请单细项数据
     * @param parm TParm
     * @return TParm
     */
    private TParm getRequestDData(TParm parm) {
        TParm inparm = new TParm();
        TNull tnull = new TNull(Timestamp.class);
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                continue;
            }
            // 1.申请单号
            inparm.addData("REQUEST_NO",
                           parm.getParm("REQ_M").getValue("REQUEST_NO"));
            // 2.序号
            inparm.addData("SEQ_NO", i+1);
            // 3.物资代码
            inparm.addData("INV_CODE",
                           table_d.getParmValue().getValue("INV_CODE", i));
            // 4.物资序号
            inparm.addData("INVSEQ_NO",
                           table_d.getParmValue().getInt("INVSEQ_NO", i));
            // 5.申请数量
            inparm.addData("QTY", table_d.getItemDouble(i, "QTY"));
            // 6.累计出库量
            inparm.addData("ACTUAL_QTY", table_d.getItemDouble(i, "ACTUAL_QTY"));
            // 10.出库注记
            inparm.addData("FINA_TYPE", table_d.getItemString(i, "FINA_TYPE"));
            // 11,12,13 OPT
            inparm.addData("OPT_USER", Operator.getID());
            inparm.addData("OPT_DATE", StringTool.getTimestamp(new Date()));
            inparm.addData("OPT_TERM", Operator.getIP());
        }
        parm.setData("REQ_D", inparm.getData());
        return parm;
    }

  
    /**
     * 申请类别变更事件
     */
    public void onChangeRequestType() {
        String request_type = this.getValueString("REQUEST_TYPE");
        this.setValue("TO_ORG_CODE", Operator.getDept());
        this.setValue("FROM_ORG_CODE", "");
        onChangeToOrgCode();
        TextFormatINVOrg inv_org = (TextFormatINVOrg)this.getTextFormat(
            "FROM_ORG_CODE");
        TextFormatINVReason inv_ren = (TextFormatINVReason)this.getTextFormat(
            "REN_CODE");
        if ("REQ".equals(request_type)) {
            inv_org.setEnabled(true);
            inv_ren.setReqFlg("Y");
            inv_ren.setGifFlg("N");
            inv_ren.setRetFlg("N");
            inv_ren.setWasFlg("N");
        }
        else if ("GIF".equals(request_type)) {
            inv_org.setEnabled(true);
            inv_ren.setReqFlg("N");
            inv_ren.setGifFlg("Y");
            inv_ren.setRetFlg("N");
            inv_ren.setWasFlg("N");
        }
        else if ("RET".equals(request_type)) {
            inv_org.setEnabled(true);
            inv_ren.setReqFlg("N");
            inv_ren.setGifFlg("N");
            inv_ren.setRetFlg("Y");
            inv_ren.setWasFlg("N");

        }
        else if ("WAS".equals(request_type)) {
            inv_org.setEnabled(false);
            inv_ren.setReqFlg("N");
            inv_ren.setGifFlg("N");
            inv_ren.setRetFlg("N");
            inv_ren.setWasFlg("Y");
        }
    }

    /**
     * 申请部门变更事件
     */
    public void onChangeToOrgCode() {
        this.setValue("FROM_ORG_CODE", "");
        String request_type = this.getValueString("REQUEST_TYPE");  
        TextFormatINVOrg inv_org = (TextFormatINVOrg)this.getTextFormat(
            "FROM_ORG_CODE");
        
        TParm parm = new TParm(TJDODBTool.getInstance().select(INVSQL.getINVOrg(this.
            getValueString("TO_ORG_CODE"))));
        String org_type = parm.getValue("ORG_TYPE", 0);    
        if ("REQ".equals(request_type)) {
        	
            if ("A".equals(org_type)) {
                org_type = "-";
            }
            else if ("B".equals(org_type)) {
                org_type = "A";
            }
            else if ("C".equals(org_type)) {
                org_type = "A";
            }
            inv_org.setOrgType(org_type);
        }
        else if ("GIF".equals(request_type)) {
            if ("A".equals(org_type)) {
                org_type = "A";
            }
            else if ("B".equals(org_type)) {
                org_type = "B";
            }
            else if ("C".equals(org_type)) {
                org_type = "C";
            }
            inv_org.setOrgType(org_type);
        }
        else if ("RET".equals(request_type)) {
            if ("A".equals(org_type)) {
                org_type = "-";
            }
            else if ("B".equals(org_type)) {
                org_type = "A";
            }
            else if ("C".equals(org_type)) {
                org_type = "B";
            }
            inv_org.setOrgType(org_type);
        }
        else if ("WAS".equals(request_type)) {
            this.setValue("FROM_ORG_CODE", "");
        }
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        /**
         * 权限控制
         * 权限1:一般个人只显示自已所属科室
         * 权限9:最大权限显示全院药库部门
         */
        // 显示全院药库部门
        TextFormatINVOrg inv_org = (TextFormatINVOrg)this.getTextFormat(
            "TO_ORG_CODE");
        TextFormatINVOrg inv_org_q = (TextFormatINVOrg)this.getTextFormat(
            "TO_ORG_CODE_Q");
        if (!this.getPopedem("deptAll")) {
            inv_org.setOperatorId(Operator.getID());
            inv_org_q.setOperatorId(Operator.getID());
            dept_flg = false;
        }
        
        else {
            inv_org.setOperatorId("");
            inv_org_q.setOperatorId("");
            dept_flg = true;
        }
        setValue("TO_ORG_CODE", Operator.getDept());
        setValue("TO_ORG_CODE_Q", Operator.getDept());
        Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        //( (TMenuItem) getComponent("delete")).setEnabled(false);
        setValue("REQUEST_DATE", date);

        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");

        // 初始化TABLE_M的Parm
        TParm parmD = new TParm();
        String[] purD = {
            "INV_CODE", "INV_CHN_DESC", "DESCRIPTION", "PY1",
            "STOCK_QTY", "STOCK_UNIT", "INVSEQ_NO", "DISPENSE_UNIT",
            "CONTRACT_PRICE", "MAN_CODE",  "SEQMAN_FLG", "VALIDATE_FLG", "INVKIND_CODE",
            "SEQ_NO"};
        for (int i = 0; i < purD.length; i++) {
            parmD.setData(purD[i], new Vector());
        }
        table_d.setParmValue(parmD);

        // 注册激发INDSupOrder弹出的事件
        table_d.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
                                 this, "onCreateEditComoponentUD");
        // TABLE_D值改变事件
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        createNewRow();
    }

    /**
     * 新增细项数据行
     * @return int
     */
    private int createNewRow() {
        int row = table_d.addRow();
        return row;
    }

    /**
     * 表格值改变事件
     *
     * @param obj
     *            Object
     */
    public boolean onTableDChangeValue(Object obj) {
        // 值改变的单元格
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // 判断数据改变
        if (node.getValue().equals(node.getOldValue()))
            return true;
        // Table的列名
        String columnName = node.getTable().getDataStoreColumnName(
            node.getColumn());
        int row = node.getRow();
        if ("QTY".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
                if (qty <= 0) {
                    this.messageBox("申请数量不能小于0");
                    return true;
                }
                else {
                    // 申请金额
                    table_d.setItem(row, "SUM_AMT", qty *
                                    table_d.getItemDouble(row, "CONTRACT_PRICE"));
                    return false;
                }
        }
        return false;
    }


    /**
     * 当TABLE创建编辑控件时长期
     *
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComoponentUD(Component com, int row, int column) {
        if (column != 0)
            return;
        if (! (com instanceof TTextField))
            return;
        if ("".equals(this.getValueString("REQUEST_TYPE"))) {
            this.messageBox("单据类别不能为空");
            return;
        }
        if ("".equals(this.getValueString("TO_ORG_CODE"))) {
            this.messageBox("申请部门不能为空");
            return;
        }
        if (!"WAS".equals(this.getValueString("REQUEST_TYPE"))
            && "".equals(this.getValueString("FROM_ORG_CODE"))) {
            this.messageBox("接受部门不能为空");
            return;
        }

        if (getTextFormat("TO_ORG_CODE").isEnabled()) {
            getTextFormat("TO_ORG_CODE").setEnabled(false);
        }
        if (getTextFormat("FROM_ORG_CODE").isEnabled()) {
            getTextFormat("FROM_ORG_CODE").setEnabled(false);
        }
        if (getComboBox("REQUEST_TYPE").isEnabled()) {
            getComboBox("REQUEST_TYPE").setEnabled(false);
        }

        TParm parm = new TParm();
        String org_code = "";
        if ("REQ".equals(this.getValueString("REQUEST_TYPE")) ||
            "GIF".equals(this.getValueString("REQUEST_TYPE"))) {
            org_code = this.getValueString("FROM_ORG_CODE");
        }
        else if ("RET".equals(this.getValueString("REQUEST_TYPE")) ||
                 "WAS".equals(this.getValueString("REQUEST_TYPE"))) {
            org_code = this.getValueString("TO_ORG_CODE");
        }
        parm.setData("ORG_CODE", org_code);
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        // 设置弹出菜单
        textFilter.setPopupMenuParameter("UI", getConfigParm().newConfig(
            "%ROOT%\\config\\inv\\INVBasePopup.x"), parm);
        // 定义接受返回值方法
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn");
    }

    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        table_d.acceptText();
        String inv_desc = parm.getValue("INV_CHN_DESC");
        String inv_code = parm.getValue("INV_CODE");
        
        parm.setData("CONTRACT_PRICE", parm.getValue("COST_PRICE"));
        int batch_seq = 100000000;
        int invseq_no = parm.getInt("INVSEQ_NO");
        String seqmain_flg = parm.getValue("SEQMAN_FLG");
        if (!StringUtil.isNullString(inv_desc)) {
            // 检查非序号管理的物资是否重复
            if ("N".equals(seqmain_flg)) {
                for (int i = 0; i < table_d.getRowCount(); i++) {
                    if ("".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                        continue;
                    }
                    else if (table_d.getSelectedRow() == i) {
                        continue;
                    }
                    else if ("Y".equals(table_d.getParmValue().getValue(
                        "SEQMAN_FLG", i))) {
                        continue;
                    }
                    else {
                        if (inv_code.equals(table_d.getParmValue().getValue(
                            "INV_CODE", i)) &&
                            batch_seq ==
                            table_d.getParmValue().getInt(" ", i)) {
                            this.messageBox("申请物资重复");
                            return;
                        }
                    }
                }
            }
            else {
                for (int i = 0; i < table_d.getRowCount(); i++) {
                    if ("".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
                        continue;
                    }
                    else if (table_d.getSelectedRow() == i) {
                        continue;
                    }
                    else if ("N".equals(table_d.getParmValue().getValue(
                        "SEQMAN_FLG", i))) {
                        continue;
                    }
                    else {
                        if (inv_code.equals(table_d.getParmValue().getValue(
                            "INV_CODE", i)) &&
                            invseq_no ==
                            table_d.getParmValue().getInt("INVSEQ_NO", i)) {
                            this.messageBox("申请物资重复");
                            return;
                        }
                    }
                }
            }
              parm.setData("INVSEQ_NO", table_d.getSelectedRow()+1);
            setTableDValue(parm, table_d.getSelectedRow());
            if (table_d.getRowCount() == table_d.getSelectedRow() + 1) {
                createNewRow();
            }
        }
    }

    /**
     * 传值给TABLE_D
     * @param parm TParm
     * @param row int
     */
    public void setTableDValue(TParm parm, int row) {
        table_d.setRowParmValue(row, parm);
        table_d.getParmValue().setRowData(row, parm);
        if ("Y".equals(parm.getValue("SEQMAN_FLG"))) {
            table_d.setItem(row, "QTY", 1);
            table_d.setItem(row, "SUM_AMT", parm.getDouble("COST_PRICE"));
        }
        else {
            table_d.setItem(row, "QTY", 0);
            table_d.setItem(row, "SUM_AMT", 0);
        }
        table_d.setItem(row, "ACTUAL_QTY", 0);
        table_d.setItem(row, "FINA_TYPE", "0");
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
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * 得到ComboBox对象
     * @param tagName String
     * @return TComboBox
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

}
