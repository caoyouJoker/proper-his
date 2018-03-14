package com.javahis.ui.phl;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.javahis.system.combo.TComboPHLBed;
import jdo.phl.PhlExecuteTool;
import com.dongyang.ui.TRadioButton;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import com.javahis.util.StringUtil;
import java.sql.Timestamp;
import jdo.sys.PatTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.Operator;
import com.dongyang.data.TNull;
import jdo.util.Manager;
import com.dongyang.util.StringTool;
import jdo.phl.PhlRegionTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.phl.PHLSQL;
import com.dongyang.ui.TTextField;

/**
 * <p>
 * Title: 静点室执行
 * </p>
 *
 * <p>
 * Description: 静点室执行
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
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class PHLExecuteControl
    extends TControl {

    private TTable table;
    //就诊序号
    private String case_no = "";
    //开始时间
    private String start_date = "";

    public PHLExecuteControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始化权限管控
        initPopedem();
        // 初始化
        table = this.getTable("TABLE");
        getTextField("MR_NO").grabFocus();
    }

    /**
     * 初始化权限管控
     */
    private void initPopedem() {
        // 权限管控
        if (!this.getPopedem("deptAll")) {
            this.getComboBox("REGION_CODE").setEnabled(false);
            String ip = Operator.getIP();
            TParm parm = new TParm();
            TParm result = PhlRegionTool.getInstance().onQuery(parm);
            String start_ip = "";
            String end_ip = "";
            if (result.getCount() > 0) {
                for (int i = 0; i < result.getCount(); i++) {
                    start_ip = result.getValue("START_IP", i);
                    end_ip = result.getValue("END_IP", i);
                    if (StringTool.isIPBetween(start_ip, end_ip, ip)) {
                        this.setValue("REGION_CODE",
                                      result.getValue("REGION_CODE", i));
                        onChangeRegion();
                        break;
                    }
                }
            }
        }
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        table.removeRowAll();
        if ("".equals(this.getValueString("MR_NO")) &&
            "".equals(this.getValueString("BED_NO"))) {
            this.messageBox("病案号和床位号不能同时为空");
            return;
        }

        TParm parm = new TParm();
//        if (!"".equals(getValueString("BAR_CODE"))) {
//            parm.setData("BAR_CODE", getValueString("BAR_CODE"));
//        }
//        if (!"".equals(getValueString("ADM_TYPE"))) {
//            parm.setData("ADM_TYPE", getValueString("ADM_TYPE"));
//        }
        if (!"".equals(getValueString("MR_NO"))) {
            parm.setData("MR_NO", getValueString("MR_NO"));
        }
        if (!"".equals(getValueString("REGION_CODE"))) {
            parm.setData("REGION_CODE", getValueString("REGION_CODE"));
        }
        if (!"".equals(getValueString("BED_NO"))) {
            parm.setData("BED_NO", getValueString("BED_NO"));
        }
        if (getRadioButton("RadioButton1").isSelected()) {
            parm.setData("EXEC_STATUS", "0");
        }
        else {
            parm.setData("EXEC_STATUS", "1");
        }
        if (case_no.length() > 0)
            parm.setData("CASE_NO", case_no);
        if (start_date.length() > 0)
            parm.setData("START_DATE", start_date);
        //System.out.println("parm===" + parm);
        TParm result = PhlExecuteTool.getInstance().onQuery(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
//        this.setValue("BAR_CODE", result.getValue("BAR_CODE", 0));
        this.setValue("ADM_TYPE", result.getValue("ADM_TYPE", 0));
//        this.setValue("MR_NO", result.getValue("MR_NO", 0));
        this.setValue("REGION_CODE", result.getValue("REGION_CODE", 0));
        this.setValue("BED_NO", result.getValue("BED_NO", 0));

        Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
        this.setValue("PAT_NAME", pat.getName());
        this.setValue("SEX", pat.getSexString());
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("AGE",
                      StringUtil.getInstance().showAge(pat.getBirthday(), date));
        table.setParmValue(result);
    }

    /**
     * 单选按钮变更事件
     */
    public void onChangeRadioAction() {
        table.setSelectionMode(0);
        table.removeRowAll();
    }

    /**
     * 清空方法
     */
    public void onClear() {
        // 清空VALUE
        String clear =
            "BAR_CODE;MR_NO;ADM_TYPE;PAT_NAME;SEX;AGE;BED_NO;"
            + "SELECT_ALL";
        this.clearValue(clear);
        this.getRadioButton("RadioButton1").setSelected(true);
        table.setSelectionMode(0);
        table.removeRowAll();
        case_no = "";
        start_date = "";
    }

    /**
     * 保存方法
     */
    public void onSave() {
        if (table.getRowCount() == 0) {
            this.messageBox("没有执行数据");
        }
        boolean flg = true;
        table.acceptText();
        for (int i = 0; i < table.getRowCount(); i++) {
            //this.messageBox(table.getItemString(i, "EXEC_STATUS"));
            if ("Y".equals(table.getItemString(i, "EXEC_STATUS"))) {
                flg = false;
                break;
            }
        }
        if (flg) {
            this.messageBox("没有执行数据");
            return;
        }

        //调用操作员输入密码界面
        Object resultData = openDialog("%ROOT%\\config\\phl\\PHLOPTCheck.x");
        String operator_id = "";
        if (resultData != null) {
            TParm resultParm = (TParm) resultData;
            //System.out.println("resultParm=="+resultParm);
            operator_id = resultParm.getValue("USER_ID");
        }
        else {
            return;
        }

        TParm parm = table.getParmValue();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        TParm orderParm = new TParm();
        TParm bedParm = new TParm();
        if (this.getRadioButton("RadioButton1").isSelected()) {
            // 保存执行医嘱
            for (int i = 0; i < parm.getCount(); i++) {
                if ("N".equals(table.getItemString(i, "EXEC_STATUS"))) {
                    continue;
                }
                orderParm.addData("START_DATE", parm.getValue("START_DATE", i));
                orderParm.addData("ADM_TYPE", parm.getValue("ADM_TYPE", i));
                orderParm.addData("CASE_NO", parm.getValue("CASE_NO", i));
                orderParm.addData("ORDER_NO", parm.getValue("ORDER_NO", i));
                orderParm.addData("SEQ_NO", parm.getValue("SEQ_NO", i));
                orderParm.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
                orderParm.addData("EXEC_STATUS", "1");
                orderParm.addData("EXEC_USER", operator_id);
                orderParm.addData("EXEC_DATE", date);
                orderParm.addData("NS_NOTE", parm.getValue("NS_NOTE", i));
                orderParm.addData("OPT_USER", operator_id);
                orderParm.addData("OPT_DATE", date);
                orderParm.addData("OPT_TERM", Operator.getIP());
            }
            bedParm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
            bedParm.setData("BED_NO", this.getValueString("BED_NO"));
            bedParm.setData("PAT_STATUS", "2");
            bedParm.setData("OPT_USER", operator_id);
            bedParm.setData("OPT_DATE", date);
            bedParm.setData("OPT_TERM", Operator.getIP());
        }
        else {
            // 更新已执行医嘱
            String patStatus = "1";
            for (int i = 0; i < parm.getCount(); i++) {
                table.acceptText();
                if (!"Y".equals(table.getItemString(i, "EXEC_STATUS"))) {
                    orderParm.addData("EXEC_STATUS", "0");
                    orderParm.addData("EXEC_USER", "");
                    orderParm.addData("EXEC_DATE", tnull);
                }
                else {
                    orderParm.addData("EXEC_STATUS", "1");
                    orderParm.addData("EXEC_USER", operator_id);
                    orderParm.addData("EXEC_DATE", date);
                    patStatus = "2";
                }
                orderParm.addData("START_DATE", parm.getValue("START_DATE", i));
                orderParm.addData("ADM_TYPE", parm.getValue("ADM_TYPE", i));
                orderParm.addData("CASE_NO", parm.getValue("CASE_NO", i));
                orderParm.addData("ORDER_NO", parm.getValue("ORDER_NO", i));
                orderParm.addData("SEQ_NO", parm.getValue("SEQ_NO", i));
                orderParm.addData("ORDER_CODE", parm.getValue("ORDER_CODE", i));
                orderParm.addData("NS_NOTE", parm.getValue("NS_NOTE", i));
                orderParm.addData("OPT_USER", operator_id);
                orderParm.addData("OPT_DATE", date);
                orderParm.addData("OPT_TERM", Operator.getIP());
            }
            bedParm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
            bedParm.setData("BED_NO", this.getValueString("BED_NO"));
            bedParm.setData("PAT_STATUS", patStatus);
            bedParm.setData("OPT_USER", operator_id);
            bedParm.setData("OPT_DATE", date);
            bedParm.setData("OPT_TERM", Operator.getIP());
        }
        TParm parmData = new TParm();
        parmData.setData("ORDER_PARM", orderParm.getData());
        parmData.setData("BED_PARM", bedParm.getData());
        TParm result = TIOM_AppServer.executeAction("action.phl.PHLAction",
            "onPhlExecute", parmData);
        if (result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        onPrint();
        this.messageBox("P0001");
        this.onClear();
    }

    /**
     * 床头卡
     */
    public void onCard() {
        if ("".equals(getValueString("REGION_CODE"))) {
            this.messageBox("请选择静点区域");
            return;
        }
        TParm parm = new TParm();
        parm.setData("REGION_CODE", getValueString("REGION_CODE"));
        Object resultData = openDialog("%ROOT%\\config\\phl\\PHLCard.x",
                                       parm);
        if (resultData != null) {
            TParm resultParm = (TParm) resultData;
            //System.out.println("resultParm==="+resultParm);
            this.setValue("MR_NO", resultParm.getValue("MR_NO"));
            this.setValue("BED_NO", resultParm.getValue("BED_NO"));
            TParm inparm = new TParm();
            inparm.setData("MR_NO", resultParm.getValue("MR_NO"));
            inparm.setData("BED_NO", resultParm.getValue("BED_NO"));
            onMrNoAction();
            if (getRadioButton("RadioButton1").isSelected()) {
                inparm.setData("EXEC_STATUS", "0");
            }
            else {
                inparm.setData("EXEC_STATUS", "1");
            }
            //System.out.println("inparm==="+inparm);
            TParm result = PhlExecuteTool.getInstance().onQuery(inparm);
            //System.out.println("result===" + result);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("没有查询数据");
                return;
            }
            this.setValue("BAR_CODE", result.getValue("BAR_CODE", 0));
            this.setValue("ADM_TYPE", result.getValue("ADM_TYPE", 0));
            this.setValue("MR_NO", result.getValue("MR_NO", 0));
            this.setValue("REGION_CODE", result.getValue("REGION_CODE", 0));
            this.setValue("BED_NO", result.getValue("BED_NO", 0));

            Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
            this.setValue("PAT_NAME", pat.getName());
            this.setValue("SEX", pat.getSexString());
            Timestamp date = SystemTool.getInstance().getDate();
            this.setValue("AGE",
                          StringUtil.getInstance().showAge(pat.getBirthday(),
                date));
            table.setParmValue(result);
        }
    }

    /**
     * 离院方法
     */
    public void onBedOut() {
        String region_code = this.getValueString("REGION_CODE");
        String bed_no = this.getValueString("BED_NO");
        String mr_no = this.getValueString("MR_NO");
        if ("".equals(region_code)) {
            this.messageBox("静点区不能为空");
            return;
        }
        if ("".equals(bed_no) || "".equals(mr_no)) {
            this.messageBox("床位和病案号不可同时为空不可为空！");
            return;
        }
        if (this.messageBox("提示", "确定是否离院", 2) == 0) {
            TParm parm = new TParm();
            Timestamp date = SystemTool.getInstance().getDate();
            TNull tnull = new TNull(Timestamp.class);
            parm.setData("REGION_CODE", region_code);
            parm.setData("BED_NO", bed_no);
            parm.setData("BED_STATUS", "0");
            parm.setData("MR_NO", "");
            parm.setData("CASE_NO", "");
            parm.setData("PAT_STATUS", "");
            parm.setData("REGISTER_DATE", tnull);
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());

            TParm result = TIOM_AppServer.executeAction("action.phl.PHLAction",
                "onPhlBedOut", parm);
            if (result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
            this.onClear();
        }
    }

    /**
     * 打印执行单
     */
    public void onPrint() {
        table.acceptText();
        if (table.getRowCount() <= 0) {
            this.messageBox("没有打印数据");
            return;
        }
        Timestamp datetime = SystemTool.getInstance().getDate();
        // 打印数据
        TParm date = new TParm();
        // 表头数据
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "静点执行单");
        date.setData("REGION_CODE", "TEXT",
                     "静点区域:" + this.getComboBox("REGION_CODE").getSelectedName());
        date.setData("NAME", "TEXT", "姓名:" + this.getValueString("PAT_NAME"));
        date.setData("BED_NO", "TEXT",
                     "床/座号:" + this.getComboBox("BED_NO").getSelectedName());
        date.setData("DATE", "TEXT",
                     "打印时间:" +
                     datetime.toString().substring(0, 10).replace('-', '/'));
        // 表格数据
        TParm parm = new TParm();
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!"Y".equals(table.getItemString(i, "EXEC_STATUS"))) {
                continue;
            }
            parm.addData("LINK_NO",
                         table.getItemString(i, "LINK_NO") + "  " +
                         ("Y".equals(table.getItemString(i, "LINK_MAIN_FLG")) ?
                          "主" : ""));
            parm.addData("ORDER_DESC", table.getItemString(i, "ORDER_DESC"));
            parm.addData("SPECIFICATION",
                         table.getItemString(i, "SPECIFICATION"));
            parm.addData("USE_QTY",
                         table.getParmValue().getValue("DISPENSE_QTY", i));
            parm.addData("UNIT",
                         table.getParmValue().getValue("UNIT_CHN_DESC", i));
            parm.addData("FREQ_CODE",
                         table.getParmValue().getValue("FREQ_CODE", i));
            parm.addData("ROUTE_CODE",
                         table.getParmValue().getValue("ROUTE_CODE", i));
            parm.addData("EXEC_DATE",
                         table.getItemString(i, "EXEC_DATE").substring(0, 10).
                         replace('-', '/'));
            parm.addData("EXEC_USER", table.getItemString(i, "EXECUTE_NAME"));
            parm.addData("NS_NOTE", table.getItemString(i, "NS_NOTE"));
        }

        if (parm.getCount("ORDER_DESC") == 0) {
            this.messageBox("没有打印数据");
            return;
        }
        parm.setCount(parm.getCount("ORDER_DESC"));
        parm.addData("SYSTEM", "COLUMNS", "LINK_NO");
        parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        parm.addData("SYSTEM", "COLUMNS", "USE_QTY");
        parm.addData("SYSTEM", "COLUMNS", "UNIT");
        parm.addData("SYSTEM", "COLUMNS", "FREQ_CODE");
        parm.addData("SYSTEM", "COLUMNS", "ROUTE_CODE");
        parm.addData("SYSTEM", "COLUMNS", "EXEC_DATE");
        parm.addData("SYSTEM", "COLUMNS", "EXEC_USER");
        parm.addData("SYSTEM", "COLUMNS", "NS_NOTE");

        date.setData("TABLE", parm.getData());
        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\PHL\\PHLOrder.jhw",
                             date);
    }

    /**
     * 瓶签回车事件
     */
    public void onBarCodeAction() {
        String bar_code = this.getValueString("BAR_CODE");
        //this.onClear();
        TParm parm = new TParm();
        parm.setData("BAR_CODE", bar_code);
        if (getRadioButton("RadioButton1").isSelected()) {
            parm.setData("EXEC_STATUS", "0");
        }
        else {
            parm.setData("EXEC_STATUS", "1");
        }
//        TParm result = PhlExecuteTool.getInstance().onQuery(parm);
//        if (result == null || result.getCount() <= 0) {
//            this.messageBox("没有查询数据");
//            return;
//        }
//        this.setValue("BAR_CODE", result.getValue("BAR_CODE", 0));
//        this.setValue("ADM_TYPE", result.getValue("ADM_TYPE", 0));
//        this.setValue("MR_NO", result.getValue("MR_NO", 0));
//        this.setValue("REGION_CODE", result.getValue("REGION_CODE", 0));
//        this.setValue("BED_NO", result.getValue("BED_NO", 0));
//
//        Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
//        this.setValue("PAT_NAME", pat.getName());
//        this.setValue("SEX", pat.getSexString());
//        Timestamp date = SystemTool.getInstance().getDate();
//        this.setValue("AGE",
//                      StringUtil.getInstance().showAge(pat.getBirthday(), date));
//
//        table.setParmValue(result);

        for (int i = 0; i < table.getRowCount(); i++) {
            if (bar_code.equals(table.getParmValue().getValue("BAR_CODE", i))) {
                table.setItem(i, "EXEC_STATUS", "Y");
            }
            else {
                table.setItem(i, "EXEC_STATUS", "N");
            }
        }
    }

    /**
     * 区域变更事件
     */
    public void onChangeRegion() {
        this.setValue("BED_NO", "");
        ( (TComboPHLBed)this.getComponent("BED_NO")).setRegionCode(this.
            getValueString("REGION_CODE"));
        ( (TComboPHLBed)this.getComponent("BED_NO")).onQuery();
    }

    /**
     * 完成状态变更事件
     */
    public void onChangeStatus() {
        onQuery();
    }

    /**
     * 病案号查询(回车事件)
     */
    public void onMrNoAction() {
        String mr_no = PatTool.getInstance().checkMrno(this.getValueString(
            "MR_NO"));
        this.setValue("MR_NO", mr_no);
        
		// modify by huangtt 20160929 EMPI患者查重提示 start
		Pat pat = Pat.onQueryByMrNo(mr_no);
		if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mr_no + " 已合并至 " + "" + pat.getMrNo());
			mr_no = pat.getMrNo();
			this.setValue("MR_NO", mr_no);// 病案号
		}
		// modify by huangtt 20160929 EMPI患者查重提示 end
        
//        Pat pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
//        this.setValue("PAT_NAME", pat.getName());
//        this.setValue("SEX", pat.getSexString());
//        Timestamp date = SystemTool.getInstance().getDate();
//        this.setValue("AGE", StringUtil.getInstance().showAge(pat.getBirthday(),
//            date));
        TParm parm = new TParm(TJDODBTool.getInstance().select(PHLSQL.
            getPHLRegisterList(mr_no)));
        if (parm == null || parm.getCount("START_DATE") <= 0) {
            this.messageBox("没有报道信息");
            return;
        }
        case_no = parm.getValue("CASE_NO", 0);
        start_date = parm.getValue("START_DATE", 0);
        this.setValue("BED_NO", parm.getValue("BED_NO", 0));
        onQuery();
        getTextField("BAR_CODE").grabFocus();
    }

    /**
     * 全选事件
     */
    public void onSelectAllAction() {
        String flg = "N";
        if ("N".equals(this.getValueString("SELECT_ALL"))) {
            flg = "N";
        }
        else {
            flg = "Y";
        }
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setItem(i, "EXEC_STATUS", flg);
        }
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
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
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
