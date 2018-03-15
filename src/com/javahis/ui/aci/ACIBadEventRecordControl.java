package com.javahis.ui.aci;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import jdo.aci.ACIBadEventTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title: 不良事件登记 </p>
 *
 * <p> Description: 不良事件登记 </p>
 *
 * <p> Copyright: Copyright (c) 2012 </p>
 *
 * <p> Company: ProperSoft </p>
 *
 * @author WangLong 2012.11.22
 * @version 1.0
 */
public class ACIBadEventRecordControl extends TControl {
    
    TMenuItem insertMenu;// "保存"按钮
    TMenuItem updateMenu;// "更新"按钮
    TMenuItem examineMenu;// "审核"按钮
    TMenuItem unExamineMenu;// "取消审核"按钮
    TMenuItem newMenu;// "新增"按钮
    TMenuItem rateMenu;// "评级"按钮 add by wanglong 20131101
    TMenuItem smsMenu;// "发短信"按钮 add by wanglong 20131101
    TTextFormat queryReportDept;// 上报科室(查询用)
    TTextFormat queryDeptInCharge;// 责任科室(查询用)
    TTextFormat reportDept;// 上报科室
    TTextFormat reportStation;// 上报病区
    TTextFormat reportUser;// 上报人
    private TParm usersParm;// 联系人 add by wanglong 20140107
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        Object obj = this.getParameter();
        if (obj instanceof TParm) {// add by wanglong 20130428
            TParm parm = (TParm) obj;
            if (parm.getValue("POPEDEM").length() != 0) {
                if ("1".equals(parm.getValue("POPEDEM"))) { // 一般权限
                    this.setPopedem("NORMAL", true);
                    this.setPopedem("SYSOPERATOR", false);
                    this.setPopedem("SYSDBA", false);
                }
                if ("2".equals(parm.getValue("POPEDEM"))) { // 角色权限
                    this.setPopedem("NORMAL", false);
                    this.setPopedem("SYSOPERATOR", true);
                    this.setPopedem("SYSDBA", false);
                }
                if ("3".equals(parm.getValue("POPEDEM"))) { // 最高权限
                    this.setPopedem("NORMAL", false);
                    this.setPopedem("SYSOPERATOR", false);
                    this.setPopedem("SYSDBA", true);
                }
            }
        }
        callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        callFunction("UI|DIAG_CODE|setPopupMenuParameter", "DIAG_CODE",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");//add by wanglong 20140107
        callFunction("UI|DIAG_CODE|addEventListener", TPopupMenuEvent.RETURN_VALUE, this,
                     "popICDReturn");
        insertMenu = (TMenuItem) this.getComponent("insert");// 保存
        updateMenu = (TMenuItem) this.getComponent("update");// 更新
        examineMenu = (TMenuItem) this.getComponent("examine");// 审核
        unExamineMenu = (TMenuItem) this.getComponent("unexamine");// 取消审核
        newMenu = (TMenuItem) this.getComponent("new");// 新增
        rateMenu = (TMenuItem) this.getComponent("rate");// 新增
        smsMenu = (TMenuItem) this.getComponent("sms");// 发短信
        queryReportDept = (TTextFormat) this.getComponent("QUERY_REPORT_DEPT");// 上报科室(查询用)
        queryDeptInCharge = (TTextFormat) this.getComponent("QUERY_DEPT_IN_CHARGE");// 责任科室(查询用)
        reportDept = (TTextFormat) this.getComponent("REPORT_DEPT");// 上报科室
        reportStation = (TTextFormat) this.getComponent("REPORT_STATION");// 上报病区
        reportUser = (TTextFormat) this.getComponent("REPORT_USER");// 上报人
        initUI();
    }

    /**
     * 初始化界面信息
     */
    public void initUI() {
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -7);
        Timestamp today = SystemTool.getInstance().getDate();
        this.setValue("REPORT_USER", Operator.getID());// 上报人
        this.setValue("REPORT_DATE", today);// 上报日期
        this.setValue("START_DATE", yesterday);
        this.setValue("END_DATE", today);
        this.setValue("EVENT_DATE", null);// 发生日期
        this.setValue("H_START_DATE", null);
        this.setValue("H_END_DATE", null);
        if (this.getPopedem("NORMAL")) {
            String deptSql =
                    "SELECT A.DEPT_CODE AS ID, A.DEPT_ABS_DESC AS NAME, A.PY1 FROM SYS_DEPT A, SYS_OPERATOR_DEPT B "
                            + " WHERE A.DEPT_CODE = B.DEPT_CODE AND B.USER_ID = '#' AND A.ACTIVE_FLG = 'Y' ORDER BY A.DEPT_CODE, A.SEQ";
            deptSql = deptSql.replaceFirst("#", Operator.getID());
            queryReportDept.setPopupMenuSQL(deptSql);
            queryReportDept.setHisOneNullRow(false);// 普通用户不能进行全部查询
            queryReportDept.onQuery();
            String deptCode = StringUtil.getDesc("SYS_OPERATOR", "COST_CENTER_CODE", "USER_ID='" + Operator.getID() + "'");
            queryReportDept.setValue(deptCode);// 普通用户限定只能查询自己所在科室的记录
            queryDeptInCharge.setEnabled(false);// 普通用户不能使用责任科室
            reportDept.setPopupMenuSQL(deptSql);
            reportDept.onQuery();
            reportDept.setValue(deptCode);
            String stationSql =
                    "SELECT A.STATION_CODE AS ID, A.STATION_DESC AS NAME, A.PY1 FROM SYS_STATION A, SYS_OPERATOR_STATION B "
                            + " WHERE A.STATION_CODE = B.STATION_CLINIC_CODE AND B.USER_ID = '#' ORDER BY A.STATION_CODE, A.SEQ";
            stationSql = stationSql.replaceFirst("#", Operator.getID());
            reportStation.setPopupMenuSQL(stationSql);
            reportStation.onQuery();
//            String stationCode = StringUtil.getDesc("SYS_OPERATOR_STATION", "STATION_CLINIC_CODE", "USER_ID='" + Operator.getID() + "' AND TYPE='2' AND MAIN_FLG='Y'");
//            if(stationCode.equals("")){
//                stationCode = StringUtil.getDesc("SYS_OPERATOR_STATION", "STATION_CLINIC_CODE", "USER_ID='" + Operator.getID() + "' AND TYPE='2'");
//            }
//            reportStation.setValue(stationCode);
            reportUser.setPopupMenuSQL("SELECT USER_ID AS ID,USER_NAME AS NAME,USER_ENG_NAME AS ENNAME,PY1,PY2 FROM SYS_OPERATOR WHERE USER_ID='" + Operator.getID() + "'");
            reportUser.onQuery();
        }
        ((TRadioButton) this.getComponent("UNEXAMINED")).setSelected(true);
        insertMenu.setVisible(true);
        updateMenu.setVisible(false);
        examineMenu.setVisible(false);
        unExamineMenu.setVisible(false);
        rateMenu.setVisible(false);
        smsMenu.setVisible(false);
        this.callFunction("UI|EXAMINE_USER|setEnabled", false);
        this.callFunction("UI|EVENT_CLASS|setEnabled", false);
        this.callFunction("UI|EXAMINE_DATE|setEnabled", false);
        this.callFunction("UI|ASSESS_USER|setEnabled", false);
        this.callFunction("UI|SAC_CLASS|setEnabled", false);
        this.callFunction("UI|ASSESS_DATE|setEnabled", false);
        this.setValue("SMS_FLG", "N");
        this.callFunction("UI|SMS_FLG|setEnabled", false);
        onEnableSMS();
        if (this.getPopedem("SYSOPERATOR")) {// add by wanglong 20130428
            examineMenu.setVisible(true);
            this.callFunction("UI|EXAMINE_USER|setEnabled", true);
            this.callFunction("UI|EVENT_CLASS|setEnabled", true);
            this.callFunction("UI|EXAMINE_DATE|setEnabled", true);
            this.setValue("EXAMINE_USER", Operator.getID());// 审核人
            this.setValue("EXAMINE_DATE", today);// 审核日期
            this.callFunction("UI|SMS_FLG|setEnabled", true);
        } else if (this.getPopedem("SYSDBA")) {// add by wanglong 20131101
            rateMenu.setVisible(true);
            this.callFunction("UI|ASSESS_USER|setEnabled", true);
            this.callFunction("UI|SAC_CLASS|setEnabled", true);
            this.callFunction("UI|ASSESS_DATE|setEnabled", true);
            this.setValue("ASSESS_USER", Operator.getID());// 审核人
            this.setValue("ASSESS_DATE", today);// 审核日期
        }
        ((TTextArea) this.getComponent("SMS_USERS")).getTextArea().setEnabled(false);
    }
    
    /**
     *增加对Table的监听
     * @param row int
     */
    public void onTableClicked(int row) {
        if (row < 0) return;
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        setValueForParm("EVENT_TYPE;EVENT_DATE;"
                + "REPORT_DEPT;REPORT_STATION;REPORT_USER;"
                + "REPORT_DATE;REPORT_SECTION;"
                + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;"
                + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                + "EVENT_DESC;EVENT_RESULT;"
                + "EXAMINE_USER;EVENT_CLASS;EXAMINE_DATE;"
                + "ASSESS_USER;SAC_CLASS;ASSESS_DATE", data, row);
        if (this.getPopedem("SYSOPERATOR")) {// add by wanglong 20130428
            boolean isExamined = (Boolean) data.getData("EXAMINE_FLG", row);
            if (isExamined == true) {
                unExamineMenu.setVisible(true);
                examineMenu.setVisible(false);
            } else {
                examineMenu.setVisible(true);
                unExamineMenu.setVisible(false);
            }
            if(this.getValueString("EXAMINE_USER").equals("")){// add by wanglong 20131101
                setValue("EXAMINE_USER", Operator.getID());
            }
            setValue("EXAMINE_DATE", SystemTool.getInstance().getDate());
        }
        if (this.getPopedem("SYSDBA")) {// add by wanglong 20131101
            rateMenu.setVisible(true);
            if(this.getValueString("ASSESS_USER").equals("")){
                setValue("ASSESS_USER", Operator.getID());
            }
            setValue("ASSESS_DATE", SystemTool.getInstance().getDate());
        }
        insertMenu.setVisible(false);
        updateMenu.setVisible(true);
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = new TParm();
        Timestamp startDate = (Timestamp) this.getValue("START_DATE");
        Timestamp endDate = (Timestamp) this.getValue("END_DATE");
        if(startDate != null) {
        	parm.setData("START_DATE", startDate);
        }
        if(endDate != null) {
        	parm.setData("END_DATE", new Timestamp(endDate.getTime() + 24 * 60 * 60 * 1000 - 1));
        }
        //add by wukai on 20160902 start
        Timestamp hstartDate = (Timestamp) this.getValue("H_START_DATE");
        Timestamp hendDate = (Timestamp) this.getValue("H_END_DATE");
        if(hstartDate != null) {
        	parm.setData("H_START_DATE", hstartDate);
        }
        if(hendDate != null) {
        	parm.setData("H_END_DATE", new Timestamp(hendDate.getTime() + 24 * 60 * 60 * 1000 - 1));
        }
        if (!this.getValueString("QUERY_REPORT_SECTION").equals("")) {   //报送部门
        	parm.setData("REPORT_SECTION", this.getValue("QUERY_REPORT_SECTION"));
        }
        //add by wukai on 20160902 end
        if (this.getValue("EXAMINED").equals("Y")) {
            parm.setData("EXAMINED", "Y");// 审核
        } else {
            parm.setData("UNEXAMINED", "Y");// 未审核
        }
        if (!this.getValueString("QUERY_REPORT_DEPT").equals("")) {
            parm.setData("REPORT_DEPT", this.getValue("QUERY_REPORT_DEPT"));// 上报科室
        }
        if (!this.getValueString("QUERY_DEPT_IN_CHARGE").equals("")) {
            parm.setData("DEPT_IN_CHARGE", this.getValue("QUERY_DEPT_IN_CHARGE"));// 责任科室
        }
        if (!this.getValueString("QUERY_EVENT_TYPE").equals("")) {
            parm.setData("EVENT_TYPE", this.getValue("QUERY_EVENT_TYPE") + "%");// 不良事件分类
        }
        if (!this.getValueString("QUERY_EVENT_CLASS").equals("")) {
            parm.setData("EVENT_CLASS", this.getValue("QUERY_EVENT_CLASS"));// 不良事件等级
        }
        if (!this.getValueString("QUERY_SAC_CLASS").equals("")) {
            parm.setData("SAC_CLASS", this.getValue("QUERY_SAC_CLASS"));// SAC分级
        }
        TParm result = ACIBadEventTool.getInstance().selectBadEventData(parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + "" + result.getErrText());
        }
        clearValue("COUNT");// 结果数量
        this.callFunction("UI|TABLE|setParmValue", new TParm());
        if (result.getCount() <= 0) {
            messageBox("E0008");// 查无资料
            return;
        }
        ((TTextField) getComponent("COUNT")).setValue(result.getCount() + "");// 结果数量
        this.callFunction("UI|TABLE|setParmValue", result);
    }
    
    /**
     * 保存
     */
    public void onSave() {
        TTable table = (TTable) this.getComponent("TABLE");
        int selRow = table.getSelectedRow();
        if (selRow >= 0) {
            return;
        }
        TParm parm = getParmForTag("EVENT_TYPE;EVENT_DATE:Timestamp;"
                + "REPORT_DEPT;REPORT_STATION;REPORT_USER;"
                + "REPORT_DATE:Timestamp;REPORT_SECTION;"
                + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;"
                + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                + "EVENT_DESC;EVENT_RESULT");
        String mrNo = parm.getValue("MR_NO");
        mrNo = PatTool.getInstance().checkMrno(mrNo);// 病案号补齐长度 add by wanglong 20130428
        parm.setData("MR_NO", mrNo);
        if (parm.getValue("EVENT_TYPE").equals("")) {
            messageBox("请填写事件类型");
            return;
        }
        if (parm.getValue("EVENT_DATE").equals("") || parm.getValue("EVENT_DATE").equals("<TNULL>")) {
            messageBox("请填写发生日期");
            return;
        }
        if (parm.getValue("REPORT_DEPT").equals("")) {
            messageBox("请填写上报科室");
            return;
        }
//        if (parm.getValue("REPORT_STATION").equals("")) {
//            String sql =
//                    "SELECT * FROM SYS_STATION WHERE DEPT_CODE = '" + parm.getValue("REPORT_DEPT")
//                            + "'";
//            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//            if (result.getErrCode() != 0) {
//                this.messageBox("检查病区数据失败");
//                return;
//            }
//            if (result.getCount() > 0) {
//                messageBox("请填写病区");
//                return;
//            }
//        }
        if (parm.getValue("REPORT_USER").equals("")) {
            messageBox("请填写上报人");
            return;
        }
        if (parm.getTimestamp("REPORT_DATE").getTime() < parm.getTimestamp("EVENT_DATE").getTime()) {
            messageBox("上报日期不能早于发生日期");
            return;
        }
        if (parm.getValue("REPORT_SECTION").equals("")) {
            messageBox("请填写报送部门");
            return;
        }
//        if (parm.getValue("PAT_NAME").equals("")) {
//            messageBox("请填写病患姓名");
//            return;
//        }
//        if (parm.getValue("SEX_CODE").equals("")) {
//            messageBox("请填写性别");
//            return;
//        }
//        if (parm.getValue("AGE").equals("")) {
//            messageBox("请填写年龄");
//            return;
//        }
        if (parm.getValue("EVENT_DESC").equals("")) {
            messageBox("请填写不良事件描述");
            return;
        }
        if (parm.getValue("EVENT_RESULT").equals("")) {
            messageBox("请填写不良事件后果描述");
            return;
        }
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = ACIBadEventTool.getInstance().insertBadEventData(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        } else {
            clearValue("EVENT_TYPE;EVENT_DATE;"
//                    + "REPORT_DEPT;REPORT_STATION;REPORT_USER;"
                    + "REPORT_STATION;"
                    + "REPORT_DATE;REPORT_SECTION;"
                    + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;"
                    + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                    + "EVENT_DESC;EVENT_RESULT;");
            this.messageBox("P0002");//新增成功
        } 
    }

    /**
     * 更新
     */
    public void onUpdate() {
        TTable table = (TTable) this.getComponent("TABLE");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            return;
        }
        TParm data = table.getParmValue();
        if (this.getPopedem("NORMAL")) {
            if (!data.getValue("REPORT_USER", selRow).equals(Operator.getID())) {
                this.messageBox("只有上报人能更新此事件");
                return;
            }
        }
        String sacClass = data.getValue("SAC_CLASS", selRow);
        if (!sacClass.equals("")) {
            messageBox("已完成评级，不能更新信息");
            return;
        }
        boolean examineFlg = (Boolean) data.getData("EXAMINE_FLG", selRow);
        if (examineFlg == true) {
            messageBox("已完成审核，不能更新信息");
            return;
        }
        String aciNo = data.getValue("ACI_NO", selRow);
        TParm parm = getParmForTag("EVENT_TYPE;EVENT_DATE:Timestamp;"
                + "REPORT_DEPT;REPORT_STATION;REPORT_USER;REPORT_DATE:Timestamp;REPORT_SECTION;"
                + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;USER_IN_CHARGE;DEPT_IN_CHARGE;"
                + "EVENT_DESC;EVENT_RESULT");
        if (parm.getValue("EVENT_TYPE").equals("")) {
            messageBox("请填写事件类型");
            return;
        }
        if (parm.getValue("EVENT_DATE").equals("") || parm.getValue("EVENT_DATE").equals("<TNULL>")) {
            messageBox("请填写发生日期");
            return;
        }
        if (parm.getValue("REPORT_DEPT").equals("") || StringUtils.isEmpty(this.getText("REPORT_DEPT"))) {
            messageBox("请填写上报科室");
            return;
        }
//        if (parm.getValue("REPORT_STATION").equals("")) {
//            String sql =
//                    "SELECT * FROM SYS_STATION WHERE DEPT_CODE = '" + parm.getValue("REPORT_DEPT")
//                            + "'";
//            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//            if (result.getErrCode() != 0) {
//                this.messageBox("检查病区数据失败");
//                return;
//            }
//            if (result.getCount() > 0) {
//                messageBox("请填写病区");
//                return;
//            }
//        }
        if (parm.getValue("REPORT_USER").equals("")) {
            messageBox("请填写上报人");
            return;
        }
        if (parm.getTimestamp("REPORT_DATE").getTime() < parm.getTimestamp("EVENT_DATE").getTime()) {
            messageBox("上报日期不能早于发生日期");
            return;
        }
        if (parm.getValue("REPORT_SECTION").equals("")) {
            messageBox("请填写报送部门");
            return;
        }
//        if (parm.getValue("PAT_NAME").equals("")) {
//            messageBox("请填写病患姓名");
//            return;
//        }
//        if (parm.getValue("SEX_CODE").equals("")) {
//            messageBox("请填写性别");
//            return;
//        }
//        if (parm.getValue("AGE").equals("")) {
//            messageBox("请填写年龄");
//            return;
//        }
        if (parm.getValue("EVENT_DESC").equals("")) {
            messageBox("请填写不良事件描述");
            return;
        }
        if (parm.getValue("EVENT_RESULT").equals("")) {
            messageBox("请填写不良事件后果描述");
            return;
        }
        parm.setData("ACI_NO", aciNo);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = ACIBadEventTool.getInstance().updateBadEventData(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        parm.setData("EXAMINE_FLG", data.getData("EXAMINE_FLG", selRow));
        data.setRowData(selRow, parm);
        callFunction("UI|TABLE|setParmValue", data);
        callFunction("UI|TABLE|setSelectedRow", selRow);
        this.messageBox("更新成功");
    }
    
    /**
     * 新增
     */
    public void onNew() {
        clearValue("EVENT_TYPE;EVENT_DATE;"
                + "REPORT_DEPT;REPORT_STATION;REPORT_USER;REPORT_DATE;REPORT_SECTION;"
                + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;"
                + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                + "EVENT_DESC;EVENT_RESULT;"
                + "SMS_FLG;SMS_USERS;SMS_TEXT;"
                + "EVENT_CLASS;SAC_CLASS");
        TTable table = (TTable) this.getComponent("TABLE");
        table.clearSelection();
        if (this.getPopedem("SYSOPERATOR")) {// add by wanglong 20130428
            clearValue("ASSESS_USER;ASSESS_DATE");
        } 

        if (this.getPopedem("SYSDBA")) {// add by wanglong 20131101
            clearValue("EXAMINE_USER;EXAMINE_DATE");
        }
        initUI();
    }
    
    /**
     * 删除
     */
    public void onDelete() {
        TTable table = (TTable) this.getComponent("TABLE");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            messageBox("请选择一条记录");
            return;
        }
        TParm data = table.getParmValue();
        if (this.getPopedem("NORMAL")) {
            if (!data.getValue("REPORT_USER", selRow).equals(Operator.getID())) {
                this.messageBox("只有上报人能删除此事件");
                return;
            }
        }
        boolean examineFlg = data.getBoolean("EXAMINE_FLG", selRow);
        if (examineFlg == true) {
            messageBox("不能删除已完成审核的记录，请先取消审核");
            return;
        }
        if (!this.getPopedem("SYSOPERATOR")) {// add by wanglong 20130503
            if (!Operator.getID().equals(data.getValue("REPORT_USER", selRow))) {
                this.messageBox("需审核者权限或者上报人自己才能删除记录");
                return;
            }
        }
        if (this.messageBox("询问", "是否删除", 2) == 0) {
            // TParm data = table.getParmValue();
            String aciNo = data.getValue("ACI_NO", selRow);
            TParm result = ACIBadEventTool.getInstance().deleteBadEventData(aciNo);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            this.callFunction("UI|TABLE|removeRow", selRow);
            clearValue("EVENT_TYPE;EVENT_CLASS;EVENT_DATE;"
                    + "REPORT_DEPT;REPORT_STATION;REPORT_USER;REPORT_DATE;REPORT_SECTION;"
                    + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;"
                    + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                    + "EVENT_DESC;EVENT_RESULT;"
                    + "EXAMINE_USER;SAC_CLASS;EXAMINE_DATE;");
            table.clearSelection();
            insertMenu.setVisible(true);
            updateMenu.setVisible(false);
//            examineMenu.setVisible(true);
//            unExamineMenu.setVisible(false);
            this.messageBox("P0003");// 删除成功
        }
    }
    
    /**
     * 审核
     */
    public void onExamine() {
        TTable table = (TTable) this.getComponent("TABLE");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            messageBox("请选择一条记录");
            return;
        }
        TParm data = table.getParmValue();
        String aciNo = data.getValue("ACI_NO", selRow);
        boolean examineFlg = (Boolean) data.getData("EXAMINE_FLG", selRow);
        if (examineFlg == true) {
            messageBox("请不要多次审核");
            return;
        }
        TParm parm = getParmForTag("EXAMINE_USER;EVENT_CLASS;EXAMINE_DATE:Timestamp");
        if (parm.getValue("EXAMINE_USER").equals("")) {
            messageBox("请填写审核人");
            return;
        }
        if (parm.getValue("EVENT_CLASS").equals("")) {
            messageBox("请填写不良事件等级");
            return;
        }
        if (this.getValue("EXAMINE_DATE") == null) {// modify by wanglong 20130516
            messageBox("请填写审核日期");
            return;
        }
        if (parm.getTimestamp("EXAMINE_DATE").getTime() < data.getTimestamp("REPORT_DATE", selRow).getTime()) {
            messageBox("审核日期不能早于上报日期");
            return;
        }
        parm.setData("ACI_NO", aciNo);
        TParm result = ACIBadEventTool.getInstance().examineBadEventData(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
//        data.setData("EXAMINE_FLG", selRow, true);
//        data.setData("EXAMINE_USER", selRow, parm.getData("EXAMINE_USER"));
//        data.setData("EVENT_CLASS", selRow, parm.getData("EVENT_CLASS"));
//        data.setData("EXAMINE_DATE", selRow, parm.getData("EXAMINE_DATE"));
        data.removeRow(selRow);
        callFunction("UI|TABLE|setParmValue", data);
        callFunction("UI|TABLE|setSelectedRow", selRow);
//        examineMenu.setVisible(false);
//        unExamineMenu.setVisible(true);
        clearValue("EVENT_TYPE;EVENT_DATE;"
                + "REPORT_DEPT;REPORT_STATION;REPORT_USER;REPORT_DATE;REPORT_SECTION;"
                + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;"
                + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                + "EVENT_DESC;EVENT_RESULT;"
                + "EVENT_CLASS");
        this.setValue("SMS_FLG", "N");
        this.callFunction("UI|SMS_FLG|setEnabled", false);
        onEnableSMS();
        this.messageBox("P0001");// 保存成功
    }

    /**
     * 取消审核
     */
    public void onUnExamine() {
        TTable table = (TTable) this.getComponent("TABLE");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            messageBox("请选择一条记录");
            return;
        }
        TParm data = table.getParmValue();
        String aciNo = data.getValue("ACI_NO", selRow);
        boolean examineFlg = data.getBoolean("EXAMINE_FLG", selRow);
        if (examineFlg == false) {
            messageBox("未审核过，无需取消审核");
            return;
        }
        TParm result = ACIBadEventTool.getInstance().unExamineBadEventData(aciNo);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
//        data.setData("EXAMINE_FLG", selRow, false);
//        data.setData("EXAMINE_USER", selRow, null);
//        data.setData("SAC_CLASS", selRow, null);
//        data.setData("EXAMINE_DATE", selRow, null);
        data.removeRow(selRow);
        callFunction("UI|TABLE|setParmValue", data);
        //callFunction("UI|TABLE|setSelectedRow", selRow-1);
        clearValue("EVENT_TYPE;EVENT_DATE;"
                + "REPORT_DEPT;REPORT_STATION;REPORT_USER;REPORT_DATE;REPORT_SECTION;"
                + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;"
                + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                + "EVENT_DESC;EVENT_RESULT;"
                + "EXAMINE_USER;EVENT_CLASS;EXAMINE_DATE");
        this.setValue("SMS_FLG", "N");
        this.callFunction("UI|SMS_FLG|setEnabled", false);
        onEnableSMS();
        this.messageBox("P0001");// 保存成功
    }
    
    /**
     * 评级
     */
    public void onRate(){
        TTable table = (TTable) this.getComponent("TABLE");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            messageBox("请选择一条记录");
            return;
        }
        TParm data = table.getParmValue();
        if(data.getBoolean("EXAMINE_FLG",selRow)==false){
            this.messageBox("请先执行审核");
            return;
        }
        String aciNo = data.getValue("ACI_NO", selRow);
        TParm parm = getParmForTag("ASSESS_USER;SAC_CLASS;ASSESS_DATE:Timestamp");
        if (parm.getValue("ASSESS_USER").equals("")) {
            messageBox("请填写评估人");
            return;
        }
        if (parm.getValue("SAC_CLASS").equals("")) {
            messageBox("请填写SAC等级");
            return;
        }
        if (this.getValue("ASSESS_DATE") == null) {// modify by wanglong 20130516
            messageBox("请填写评估日期");
            return;
        }
        if (parm.getTimestamp("ASSESS_DATE").getTime() < data.getTimestamp("EXAMINE_DATE", selRow).getTime()) {
            messageBox("评估日期不能早于审核日期");
            return;
        }
        parm.setData("ACI_NO", aciNo);
        TParm result = ACIBadEventTool.getInstance().rateBadEventData(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        data.setData("SAC_CLASS", selRow, parm.getData("SAC_CLASS"));
        data.setData("ASSESS_USER", selRow, parm.getData("ASSESS_USER"));
        data.setData("ASSESS_DATE", selRow, parm.getData("ASSESS_DATE"));
        callFunction("UI|TABLE|setParmValue", data);
        callFunction("UI|TABLE|setSelectedRow", selRow);
        this.messageBox("P0001");// 保存成功
    }

    /**
     * 病案号查询
     */
    public void onMrNo() {
        String mrNo = this.getValueString("MR_NO");
        TParm parm = new TParm();
        if (mrNo.equals("")) {
            return;
        } else {
            mrNo = PatTool.getInstance().checkMrno(mrNo);
            // modify by huangtt 20160929 EMPI患者查重提示 start
            this.setValue("MR_NO", mrNo);
    		Pat pat = Pat.onQueryByMrNo(mrNo);
    		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
    			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
    			mrNo = pat.getMrNo();
    			this.setValue("MR_NO", mrNo);// 病案号
    		}
    		// modify by huangtt 20160929 EMPI患者查重提示 end
            
            parm = PatTool.getInstance().getInfoForMrno(mrNo);
        }
        if (parm.getCount() > 0) {
            this.setValue("MR_NO", mrNo);// 病案号
            this.setValue("PAT_NAME", parm.getValue("PAT_NAME", 0));// 姓名
            this.setValue("SEX_CODE", parm.getValue("SEX_CODE", 0));// 性别
            Timestamp birthday = parm.getTimestamp("BIRTH_DATE", 0);
            if (birthday != null) {
                String age = StringTool.CountAgeByTimestamp(birthday,
                        SystemTool.getInstance().getDate())[0];// 年龄
                this.setValue("AGE", age);
            }
            String diagSql =
                    "SELECT CASE_NO, MIN(SEQ_NO), ICD_CODE, ICD_DESC FROM MRO_RECORD_DIAG "
                            + " WHERE MAIN_FLG = 'Y' AND MR_NO = '#' "
                            + " GROUP BY CASE_NO, ICD_CODE, ICD_DESC "
                            + " ORDER BY CASE_NO DESC, MIN(SEQ_NO)";// add by wanglong 20140107
            diagSql = diagSql.replaceFirst("#", mrNo);
            TParm diagParm = new TParm(TJDODBTool.getInstance().select(diagSql));
            if (diagParm.getCount() >= 1) {
                this.setValue("DIAG_CODE", diagParm.getValue("ICD_CODE", 0));
                this.setValue("DIAG_DESC", diagParm.getValue("ICD_DESC", 0));
            }
        }
    }

    /**
     * 带入事件描述
     */
    public void onCopyEventDesc(){
        String smsText = "";
        if (!this.getValue("EVENT_TYPE").equals("")) {
            smsText += "不良事件类型：" + this.getText("EVENT_TYPE") + "\r\n";
        }
        if (!this.getValue("MR_NO").equals("")) {
            smsText += "病案号" + this.getValueString("MR_NO");
            if (!this.getValue("PAT_NAME").equals("")) {
                if (smsText.length() > 0) {
                    smsText += "，" + this.getValueString("PAT_NAME");
                } else {
                    smsText += this.getValueString("PAT_NAME");
                }
            }
            if (!this.getValue("SEX_CODE").equals("")) {
                if (smsText.length() > 0) {
                    smsText += "，" + this.getText("SEX_CODE");
                } else {
                    smsText += this.getText("SEX_CODE");
                }
            }
            if (!this.getValue("AGE").equals("")) {
                if (smsText.length() > 0) {
                    smsText += "，" + this.getValueString("AGE") + "岁";
                } else {
                    smsText += this.getValueString("AGE") + "岁";
                }
            }
            if (!this.getValue("DIAG_CODE").equals("")) {
                if (smsText.length() > 0) {
                    smsText += "，" + this.getValueString("DIAG_DESC");
                } else {
                    smsText += this.getValueString("DIAG_DESC");
                }
            }
            if (smsText.length() > 0) {
                smsText += "\r\n";
            }
        }
        smsText += this.getValue("EVENT_DESC") + "\r\n";
        if (!this.getValue("EVENT_RESULT").equals("")) {
            smsText += "结果：" + this.getValue("EVENT_RESULT");
        }
        this.setValue("SMS_TEXT", smsText);
        if (smsText.length() > 166) {
            this.messageBox("字数超过限制");
            return;
        }
    }
    
    /**
     * 发送短信
     */
    public void onSendSMS() {//add by wanglong 20131101
        TTable table = (TTable) this.getComponent("TABLE");
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("请先选择某个事件");
            return;
        }
        if (this.getValueString("SMS_USERS").equals("")) {
            this.messageBox("请选择联系人");
            return;
        }
        if (this.getValueString("SMS_TEXT").length() > 166) {
            this.messageBox("字数超过限制");
            return;
        }
        usersParm.setData("SMS_TEXT", this.getValueString("SMS_TEXT"));
        usersParm.setData("ACI_NO", table.getParmValue().getValue("ACI_NO", row));
        ACIBadEventTool.sendSms(usersParm);
    }
    
    /**
     * 汇出Excel
     */
    public void onExport() {
        if (((Integer) this.callFunction("UI|TABLE|getRowCount")) <= 0) {
            this.messageBox("没有汇出数据！请先查询");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel((TTable) this.getComponent("TABLE"), "不良事件汇总");
    }

    /**
     * 选择“审核/未审核”RadioButton
     */
    public void onExamineRdBtn() {
        if (((TRadioButton) this.getComponent("UNEXAMINED")).isSelected()) {
            if (this.getPopedem("SYSOPERATOR")) {
                examineMenu.setVisible(true);
                unExamineMenu.setVisible(false);
            }
            if (this.getPopedem("SYSDBA")) {
                this.callFunction("UI|ASSESS_USER|setEnabled", false);
                this.callFunction("UI|SAC_CLASS|setEnabled", false);
                this.callFunction("UI|ASSESS_DATE|setEnabled", false);
//                smsMenu.setVisible(false);
            }
        } else {
            if (this.getPopedem("SYSOPERATOR")) {
                examineMenu.setVisible(false);
                unExamineMenu.setVisible(true);
            }
            if (this.getPopedem("SYSDBA")) {
                this.callFunction("UI|ASSESS_USER|setEnabled", true);
                this.callFunction("UI|SAC_CLASS|setEnabled", true);
                this.callFunction("UI|ASSESS_DATE|setEnabled", true);
//                smsMenu.setVisible(true);
            }
        }
        clearValue("EVENT_TYPE;EVENT_DATE;"
                + "REPORT_DEPT;REPORT_STATION;REPORT_USER;REPORT_DATE;REPORT_SECTION;"
                + "MR_NO;PAT_NAME;SEX_CODE;AGE;"
                + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                + "EVENT_DESC;EVENT_RESULT;"
                + "SMS_FLG;SMS_USERS;SMS_TEXT;"
                + "EVENT_CLASS;SAC_CLASS");
        if (this.getPopedem("SYSOPERATOR")) {// add by wanglong 20130428
            clearValue("ASSESS_USER;ASSESS_DATE");
        } 

        if (this.getPopedem("SYSDBA")) {// add by wanglong 20131101
            clearValue("EXAMINE_USER;EXAMINE_DATE");
        }
        callFunction("UI|TABLE|setParmValue", new TParm());
    }
    
    /**
     * 启用/禁用短信
     */
    public void onEnableSMS() {//add by wanglong 20131101
        if (this.getValueString("SMS_FLG").equals("Y")) {
            callFunction("UI|WITH_DESC|setEnabled", true);
            callFunction("UI|CHOOSE_USER|setEnabled", true);
            ((TTextArea) this.getComponent("SMS_TEXT")).getTextArea().setEnabled(true);
            smsMenu.setVisible(true);
        } else {
            callFunction("UI|WITH_DESC|setEnabled", false);
            callFunction("UI|CHOOSE_USER|setEnabled", false);
            ((TTextArea) this.getComponent("SMS_TEXT")).getTextArea().setEnabled(false);
            smsMenu.setVisible(false);
        }
    }

    /**
     * 清空
     */
    public void onClear() {
        clearValue("QUERY_REPORT_DEPT;QUERY_DEPT_IN_CHARGE;QUERY_EVENT_TYPE;QUERY_EVENT_CLASS;QUERY_SAC_CLASS;COUNT;"
                + "EVENT_TYPE;EVENT_DATE;"
                + "REPORT_DEPT;REPORT_STATION;REPORT_USER;REPORT_DATE;REPORT_SECTION;"
                + "MR_NO;PAT_NAME;SEX_CODE;AGE;DIAG_CODE;DIAG_DESC;"
                + "USER_IN_CHARGE;DEPT_IN_CHARGE;"
                + "EVENT_DESC;EVENT_RESULT;"
                + "SMS_FLG;SMS_USERS;SMS_TEXT;"
                + "EXAMINE_USER;EVENT_CLASS;EXAMINE_DATE;"
                + "ASSESS_USER;SAC_CLASS;ASSESS_DATE");
        usersParm = new TParm();
        callFunction("UI|TABLE|setParmValue", new TParm());
        initUI();
    }
    
    /**
     * 诊断下拉框返回值
     * 
     * @param tag
     * @param obj
     */
    public void popICDReturn(String tag, Object obj) {// add by wanglong 20140107
        TParm parm = (TParm) obj;
        this.setValue("DIAG_CODE", parm.getValue("ICD_CODE"));
        this.setValue("DIAG_DESC", parm.getValue("ICD_CHN_DESC"));
    }
    
    /**
     * 选择联系人
     */
    public void onChooseUser() {// add by wanglong 20140107
        TParm result =
                (TParm) this.openDialog("%ROOT%\\config\\aci\\ACIPackageChoose.x", new TParm());
        if (result != null && result.getCount() > 0) {
            usersParm = result;
            String nameList = "";
            for (int i = 0; i < usersParm.getCount(); i++) {
                nameList += usersParm.getValue("USER_NAME", i)+";";
            }
            nameList = nameList.substring(0, nameList.length() - 1);
            this.setValue("SMS_USERS", nameList);
        }
    }

}
