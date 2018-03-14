package com.javahis.ui.udd;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.config.TConfig;
import com.dongyang.util.TypeTool;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.sys.SYSOperatorTool;
import com.dongyang.data.TParm;
import jdo.udd.UddMedDispenseTool;
import com.dongyang.manager.TCM_Transform;
import java.util.ArrayList;
import java.util.List;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TRadioButton;

/**
 * <p>Title: 住院药房西药配药</p>
 *
 * <p>Description: 住院药房西药配药</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class UddMedDispenseUIControl
    extends TControl {
    //配置文件 DOSAGE：西成药调配；DISPENSE：西成药发药
    private String controlName = "DOSAGE";
    //配置文件:是否启用管制药品判断
    private boolean isCtrl;
    //药房扣库计费点
    private String charge;
    //住院药房流程是否有审核流程
    private boolean isCheckNeeded;
    //住院药房流程是否有配药流程
    private boolean isDosage;
    //病患清单
    private TTable tablePat;
    //统药单
    private TTable tableSumMed;
    //药品明细
    private TTable tableMed;
    //缺药明细
    private TTable tablelShtMed;
    //管制药品权限
    private boolean ctrl_flg = true;

    public UddMedDispenseUIControl() {
    }

    public void onInitParameter() {
        charge = TConfig.getSystemValue("CHARGE_POINT");
        isDosage = TypeTool.getBoolean(TConfig.getSystemValue("IS_DOSAGE"));
        isCheckNeeded = TypeTool.getBoolean(TConfig.getSystemValue("IS_CHECK"));
        isCtrl = TypeTool.getBoolean(TConfig.getSystemValue("IS_CTRL"));
        controlName = getParameter().toString();
        if ("DOSAGE".equalsIgnoreCase(controlName))
            setTitle("西成药调配");
        else
            setTitle("西成药发药");
    }

    public void onInit() {
        tablePat = (TTable) getComponent("TABLE_PAT");
        tableSumMed = (TTable) getComponent("TABLE_SUM_MED");
        tableMed = (TTable) getComponent("TABLE_MED");
        tablelShtMed = (TTable) getComponent("TABLE_SHT_MED");

        Timestamp datetime = TJDODBTool.getInstance().getDBTime();
        setValue("START_DATE", datetime);
        setValue("END_DATE", datetime);

        //判断登录人员是否有管制药品权限
        if (isCtrl) {
            TParm opertor = SYSOperatorTool.getInstance().getOperator(Operator.
                getID(),Operator.getRegion());
            if ("Y".equals(opertor.getValue("CTRL_FLG", 0))) {
                ctrl_flg = true;
            }
            else {
                ctrl_flg = false;
            }
        }

        // 给病患清单(TABLE_PAT)中的CHECKBOX添加侦听事件
        callFunction("UI|TABLE_PAT|addEventListener", TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTablePatCheckBoxClicked");
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        //查询数据检核
        if (!checkData()) {
            return;
        }
        //取得病患清单查询条件
        TParm parm = getQueryPatCondition();
        TParm result = UddMedDispenseTool.getInstance().onQueryPat(parm);
        if (result == null || result.getCount("PAT_NAME") <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        tablePat.setParmValue(result);
    }

    /**
     * 清空方法
     */
    public void onClear() {

    }

    /**
     * 保存方法
     */
    public void onSave() {

    }

    /**
     * 删除方法
     */
    public void onDelete() {

    }

    /**
     * 给病患清单(TABLE_PAT)中的CHECKBOX添加侦听事件
     *
     * @param obj
     */
    public void onTablePatCheckBoxClicked(Object obj) {
        tablePat.acceptText();
        int column = tablePat.getSelectedColumn();
        if (column == 0) {
            onQueryTableSumMed();
            onQueryTableMed();
        }
    }

    /**
     * 统药单查询
     */
    private void onQueryTableSumMed() {
        //取得查询条件
        TParm parm = getQueryTableMedCondition();
        //按药品显示统药单或按病患显示统药单
        parm.setData("ORDER_BY",
                     this.getRadioButton("ORDER_BY_ORDER").isSelected() ?
                     "ORDER_BY_ORDER" : "ORDER_BY_MRNO");
        TParm result = UddMedDispenseTool.getInstance().onQueryTableSumMed(parm);
        tableSumMed.setParmValue(result);
    }

    /**
     * 药品细项查询
     */
    private void onQueryTableMed() {
        //取得查询条件
        TParm parm = getQueryTableMedCondition();
        TParm result = UddMedDispenseTool.getInstance().onQueryTableMed(parm);
        tableMed.setParmValue(result);
    }

    /**
     * 取得查询条件
     * @return TParm
     */
    private TParm getQueryTableMedCondition(){
        TParm parm = new TParm();
        String startDate = StringTool.getString(TCM_Transform.getTimestamp(
            getValue("START_DATE")), "yyyyMMddHHmm").substring(0, 8) + "0000";
        String endDate = StringTool.getString(TCM_Transform.getTimestamp(
            getValue("END_DATE")), "yyyyMMddHHmm").substring(0, 8) + "2359";
        //开始时间
        parm.setData("START_DATE", startDate);
        //结束时间
        parm.setData("END_DATE", endDate);
        //配药种类
        if (TypeTool.getBoolean(getValue("ST"))) {
            parm.setData("ST", "ST");
        }
        else if (TypeTool.getBoolean(getValue("UD"))) {
            parm.setData("UD", "UD");
        }
        else {
            parm.setData("DS", "DS");
        }
        //剂型分类
        String getDoseType = "";
        List list = new ArrayList();
        if ("Y".equals(this.getValueString("DOSE_TYPEO"))) {
            list.add("O");
        }
        if ("Y".equals(this.getValueString("DOSE_TYPEE"))) {
            list.add("E");
        }
        if ("Y".equals(this.getValueString("DOSE_TYPEI"))) {
            list.add("I");
        }
        if ("Y".equals(this.getValueString("DOSE_TYPEF"))) {
            list.add("F");
        }

        if (list == null || list.size() == 0) {
            getDoseType = "";
        }
        else {
            for (int i = 0; i < list.size(); i++) {
                getDoseType = getDoseType + "'" + list.get(i) + "' ,";
            }
            getDoseType = getDoseType.substring(0, getDoseType.length() - 1);
        }
        parm.setData("DOSE_TYPE", getDoseType);
        //住院药房流程是否有审核流程
        if (isCheckNeeded) {
            parm.setData("CHECK", "Y");
        }
        //管制药品权限
        if (!ctrl_flg) {
            parm.setData("CTRL_FLG", "Y");
        }
        //取得CASE_NO集合
        parm.setData("CASE_NO", getCaseNos());
        //取得配药单号
        String dispnese_no_list = getPhaDispenseNos();
        if (!"".equals(dispnese_no_list) && !"''".equals(dispnese_no_list) &&
            !"''".equals(dispnese_no_list)) {
            parm.setData("PHA_DISPENSE_NO", dispnese_no_list);
        }

        //完成状态
        if (TypeTool.getBoolean(getValue("UNCHECK"))) {
            //未完成
            if ("DOSAGE".equals(controlName)) {
                //配药
                parm.setData("UNCHECK_DOSAGE", "Y");
            }
            else {
                //发药
                parm.setData("UNCHECK_DISPENSE", "Y");
            }
        }
        else {
            //完成
            if ("DOSAGE".equals(controlName)) {
                //配药
                parm.setData("CHECK_DOSAGE", "Y");
            }
            else {
                //发药
                parm.setData("CHECK_DISPENSE", "Y");
            }
        }
        return parm;
    }

    /**
     * 取得PAT table里选中的CASE_NO，为其他SQL拼WHERE用
     * @return
     */
    public String getCaseNos() {
        TParm parm = tablePat.getParmValue();
        StringBuffer caseNos = new StringBuffer();
        if (parm.getCount() < 1)
            return "''";
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            if (StringTool.getBoolean(parm.getValue("EXEC", i)))
                caseNos.append("'").append(parm.getValue("CASE_NO", i)).append(
                    "',");
        }
        if (caseNos.length() < 1) {
            return "''";
        }
        else {
            caseNos.deleteCharAt(caseNos.length() - 1);
            return caseNos.toString();
        }
    }

    /**
     * 取得配药单号
     * @return String
     */
    private String getPhaDispenseNos() {
        TParm parm = tablePat.getParmValue();
        StringBuffer phaDispenseNo = new StringBuffer();
        if (parm.getCount() < 1)
            return "";
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            if (StringTool.getBoolean(parm.getValue("EXEC", i)) &&
                !"".equals(parm.getValue("PHA_DISPENSE_NO", i)))
                phaDispenseNo.append("'").append(parm.getValue(
                    "PHA_DISPENSE_NO", i)).append("',");
        }
        if (phaDispenseNo.length() < 1) {
            return "";
        }
        else {
            phaDispenseNo.deleteCharAt(phaDispenseNo.length() - 1);
            return phaDispenseNo.toString();
        }
    }

    /**
     * 查询数据检核
     * @return boolean
     */
    private boolean checkData() {
        if ("".equals(this.getValueString("EXEC_DEPT_CODE"))) {
            this.messageBox("请选择执行科室");
            return false;
        }
        return true;
    }

    /**
     * 取得病患清单查询条件
     * @return TParm
     */
    private TParm getQueryPatCondition() {
        TParm parm = new TParm();
        String startDate = StringTool.getString(TCM_Transform.getTimestamp(
            getValue("START_DATE")), "yyyyMMddHHmm").substring(0, 8) + "0000";
        String endDate = StringTool.getString(TCM_Transform.getTimestamp(
            getValue("END_DATE")), "yyyyMMddHHmm").substring(0, 8) + "2359";
        //开始时间
        parm.setData("START_DATE", startDate);
        //结束时间
        parm.setData("END_DATE", endDate);
        //执行科室
        parm.setData("EXEC_DEPT_CODE", this.getValueString("EXEC_DEPT_CODE"));
        //代理科室
        if (!"".equals(this.getValueString("AGENCY_ORG_CODE"))) {
            parm.setData("AGENCY_ORG_CODE",
                         this.getValueString("AGENCY_ORG_CODE"));
        }
        //配药种类
        if (TypeTool.getBoolean(getValue("ST"))) {
            parm.setData("ST", "ST");
        }
        else if (TypeTool.getBoolean(getValue("UD"))) {
            parm.setData("UD", "UD");
        }
        else {
            parm.setData("DS", "DS");
        }
        //病区
        if (TypeTool.getBoolean(getValue("STATION")) &&
            !"".equals(this.getValueString("STATION_CODE"))) {
            parm.setData("STATION_CODE", getValueString("STATION_CODE"));
        }
        //病案号
        if (TypeTool.getBoolean(getValue("MR")) &&
            !"".equals(this.getValueString("MR_NO"))) {
            parm.setData("MR_NO", getValueString("MR_NO"));
        }
        //床位
        if (TypeTool.getBoolean(getValue("BED")) &&
            !"".equals(this.getValueString("BED_NO"))) {
            parm.setData("BED_NO", getValueString("BED_NO"));
        }
        //配药单号
        if (!"".equals(this.getValueString("PHA_DISPENSE_NO"))) {
            parm.setData("PHA_DISPENSE_NO", getValueString("PHA_DISPENSE_NO"));
        }
        //住院药房流程是否有审核流程
        if (isCheckNeeded) {
            parm.setData("CHECK", "Y");
        }
        //完成状态
        if (TypeTool.getBoolean(getValue("UNCHECK"))) {
            //未完成
            if ("DOSAGE".equals(controlName)) {
                //配药
                parm.setData("UNCHECK_DOSAGE", "Y");
            }
            else {
                //发药
                parm.setData("UNCHECK_DISPENSE", "Y");
            }
        }
        else {
            //完成
            if ("DOSAGE".equals(controlName)) {
                //配药
                parm.setData("CHECK_DOSAGE", "Y");
            }
            else {
                //发药
                parm.setData("CHECK_DISPENSE", "Y");
            }
        }
        return parm;
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

}
