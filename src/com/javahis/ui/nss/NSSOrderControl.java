package com.javahis.ui.nss;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import jdo.adm.ADMInpTool;
import jdo.sys.Pat;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import jdo.nss.NSSOrderTool;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title: 膳食订餐</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSOrderControl
    extends TControl {
    public NSSOrderControl() {
        super();
    }

    private TTable table;

    private String case_no = "";

    /**
     * 初始化方法
     */
    public void onInit() {
        // 给TABLEDEPT中的CHECKBOX添加侦听事件
        callFunction("UI|TABLE_ORDER|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        // 默认订餐一周
        Timestamp now = SystemTool.getInstance().getDate();
        int week = StringTool.getWeek(now);
        this.setValue("START_DATE", now);
        this.setValue("END_DATE", StringTool.rollDate(now, 7 - week));
        table = getTable("TABLE_ORDER");
        ( (TMenuItem) getComponent("delete")).setEnabled(false);

        Object obj = this.getParameter();
        if (obj != null) {
            String mr_no = (String) ( (TParm) obj).getData("NSS", "MR_NO");
            this.setValue("MR_NO", mr_no);
            onMrNoAction();
        }
        onQuery();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        if (case_no.length() <= 0 && getRadioButton("RadioButton_2").isSelected()) {
            this.messageBox("请选择订餐病患");
            return;
        }
        TParm parm = new TParm();
        String diet_type = this.getValueString("DIET_TYPE");
        if (diet_type != null && diet_type.length() > 0) {
            parm.setData("DIET_TYPE", diet_type);
        }
        String pack_Code = this.getValueString("PACK_CODE");
        if (pack_Code != null && pack_Code.length() > 0) {
            parm.setData("PACK_CODE", pack_Code);
        }

        TParm result = new TParm();
        if (getRadioButton("RadioButton_1").isSelected()) {
            int start_date = StringTool.getWeek( (Timestamp)this.getValue(
                "START_DATE"));
            int end_date = StringTool.getWeek( (Timestamp)this.getValue(
                "END_DATE"));
            parm.setData("START_DATE", "0" + start_date);
            if (end_date == 0) {
                end_date = 7;
            }
            parm.setData("END_DATE", "0" + end_date);
            result = NSSOrderTool.getInstance().getNSSWeeklyOrder(parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("没有查询信息");
            }
            // 根据星期几确定日期
            int weekly_code = 0;
            Timestamp now = SystemTool.getInstance().getDate();
            int week = StringTool.getWeek(now);
            for (int i = 0; i < result.getCount(); i++) {
                weekly_code = result.getInt("WEEKLY_CODE", i);
                if (weekly_code == 0) {
                    weekly_code = 7;
                }
                result.setData("DIET_DATE", i,
                               StringTool.rollDate(now, weekly_code - week));
            }
        }
        else {
            parm.setData("START_DATE", this.getValue("START_DATE"));
            parm.setData("END_DATE", this.getValue("END_DATE"));
            parm.setData("CASE_NO", case_no);
            result = NSSOrderTool.getInstance().getNSSOrder(parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("没有查询信息");
            }
            // 根据日期确定星期几
            for (int i = 0; i < result.getCount(); i++) {
                Timestamp diet_date = StringTool.getTimestamp(result.getValue(
                    "DIET_DATE", i), "yyyyMMdd");
                result.setData("DIET_DATE", i, diet_date);
                int week = StringTool.getWeek(diet_date);
                if (week == 0) {
                    week = 7;
                }
                result.setData("WEEKLY_CODE", i, "0" + week);
            }
        }
        table.setParmValue(result);
    }

    /**
     * 保存方法(订餐)
     */
    public void onSave() {
        if (!checkData()) {
            return;
        }
        TParm parm = new TParm();
        TParm result = new TParm();
        TParm tableParm = table.getParmValue();
        TParm rowParm = new TParm();
        Timestamp now = SystemTool.getInstance().getDate();
        for (int i = 0; i < tableParm.getCount("SELECT_FLG"); i++) {
            if ("Y".equals(tableParm.getValue("CHECK_FLG", i))) {
                parm.addData("CASE_NO", case_no);
                parm.addData("DIET_DATE", StringTool.getString(tableParm.
                    getTimestamp("DIET_DATE", i), "yyyyMMdd"));
                parm.addData("MEAL_CODE", tableParm.getValue("MEAL_CODE", i));
                parm.addData("MEAL_TIME", tableParm.getValue("MEAL_TIME", i));
                parm.addData("ADD_ON", tableParm.getValue("ADD_ON", i));
                parm.addData("DIET_TYPE", tableParm.getValue("DIET_TYPE", i));
                parm.addData("DIET_KIND", tableParm.getValue("DIET_KIND", i));
                parm.addData("PACK_CODE", tableParm.getValue("PACK_CODE", i));
                parm.addData("PACK_CHN_DESC",
                             tableParm.getValue("PACK_CHN_DESC", i));
                parm.addData("PACK_ENG_DESC",
                             tableParm.getValue("PACK_ENG_DESC", i));
                parm.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION", i));
                parm.addData("PRICE", tableParm.getDouble("PRICE", i));
                parm.addData("BILL_FLG", "N");
                parm.addData("STOP_FLG", "N");
                parm.addData("IPD_NO", this.getValueString("IPD_NO"));
                parm.addData("MR_NO", this.getValueString("MR_NO"));
                parm.addData("OPT_USER", Operator.getID());
                parm.addData("OPT_DATE", now);
                parm.addData("OPT_TERM", Operator.getIP());

                //检核是否已经订了该套餐
                rowParm = parm.getRow(parm.getCount("CASE_NO") - 1);
                result = NSSOrderTool.getInstance().queryNSSOrder(rowParm);
                if (result != null && result.getCount() > 0) {
                    this.messageBox("病患：" + this.getValueString("PAT_NAME") +
                                    "  " + tableParm.getValue("DIET_DATE",i).
                                    substring(0, 10) + "  " +
                                    tableParm.getValue("MEAL_CHN_DESC", i) +
                                    "已定餐，不可重复");
                    return;
                }
            }
        }
        result = TIOM_AppServer.executeAction(
            "action.nss.NSSOrderAction", "onInsertNSSOrder", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("保存失败");
        }
        else {
            this.messageBox("保存成功");
            onQuery();
        }
    }

    /**
     * 删除方法(取消订餐)
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("请选择取消的套餐");
        }
        TParm parm = table.getParmValue().getRow(row);
        String message = "病患：" + this.getValueString("PAT_NAME") + "  " +
            parm.getValue("DIET_DATE").substring(0, 10) + "  " +
            parm.getValue("PACK_CHN_DESC");
        // 检核套餐是否已收费
        if ("Y".equals(parm.getValue("BILL_FLG"))) {
            this.messageBox(message + "已收费，不可取消");
            return;
        }
//        // 检核套餐是否已过退餐时间
//        Timestamp now = SystemTool.getInstance().getDate();
//        String stop = StringTool.getString(parm.getTimestamp("DIET_DATE"),
//                                           "yyyyMMdd") +
//            parm.getValue("STOP_ORDER_TIME");
//        Timestamp stop_time = StringTool.getTimestamp(stop, "yyyyMMddHHmm");
//        if (now.compareTo(stop_time) > 0) {
//            this.messageBox(message + "已过退餐时间，不可取消");
//            return;
//        }

        // 取消订餐
        parm.setData("DIET_DATE",
                     StringTool.getString(parm.getTimestamp("DIET_DATE"),
                                          "yyyyMMdd"));
        TParm result = NSSOrderTool.getInstance().onDeleteNSSOrder(parm);
        if (result.getErrCode() < 0) {
            this.messageBox("取消失败");
        }
        else {
            this.messageBox("取消成功");
            table.removeRow(row);
        }
    }

    /**
     * 清空方法
     */
    public void onClear() {
        this.clearValue("DIET_TYPE;PACK_CODE");
        table.removeRowAll();
        this.getRadioButton("RadioButton_1").setSelected(true);
        onChangeRadionButton();
        //case_no = "";
    }

    /**
     * MR_NO回车事件
     */
    public void onMrNoAction() {
        String mr_no = this.getValueString("MR_NO");
        this.setValue("MR_NO", StringTool.fill0(mr_no, PatTool.getInstance().getMrNoLength())); //====chenxi
        TParm parm = new TParm();
        parm.setData("MR_NO", this.getValue("MR_NO"));
        TParm result = ADMInpTool.getInstance().selectInHosp(parm);
        if (result == null || result.getCount("CASE_NO") <= 0) {
            this.messageBox("该病患当前不在院");
        }
        else {
            setPatInfo(result.getRow(0));
        }
    }

    /**
     * IPD_NO回车事件
     */
    public void onIpdNoAction() {
        String mr_no = this.getValueString("IPD_NO");
        this.setValue("IPD_NO", StringTool.fill0(mr_no, PatTool.getInstance().getIpdNoLength()));//=====chenxi
        TParm parm = new TParm();
        parm.setData("IPD_NO", this.getValue("IPD_NO"));
        TParm result = ADMInpTool.getInstance().selectInHosp(parm);
        if (result == null || result.getCount("CASE_NO") <= 0) {
            this.messageBox("该病患当前不在院");
        }
        else {
            setPatInfo(result.getRow(0));
        }
    }

    /**
     * 单选按钮改变事件
     */
    public void onChangeRadionButton() {
        if (this.getRadioButton("RadioButton_1").isSelected()) {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
            table.setLockColumns("1,2,3,4,8,9");
        }
        else {
            ( (TMenuItem) getComponent("save")).setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            table.setLockColumns("all");
        }
        table.removeRowAll();
        onQuery();
    }

    /**
     * 表格(TABLE)复选框改变事件
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        table.acceptText();
        int row = table.getSelectedRow();
        // 获得选中的列
        int column = table.getSelectedColumn();
        if (column == 0) {
            table.setItem(row, "CHECK_FLG",
                          table.getItemString(row, "SELECT_FLG"));
        }
        else if (column == 5) {
            String check_flg = table.getItemString(row, "CHECK_FLG");
            if ("Y".equals(check_flg)) {
                table.setItem(row, "SELECT_FLG", check_flg);
            }
        }
    }

    /**
     * 输出病患信息
     * @param parm TParm
     */
    private void setPatInfo(TParm parm) {
        this.setValue("MR_NO", parm.getValue("MR_NO"));
        this.setValue("IPD_NO", parm.getValue("IPD_NO"));
        this.setValue("DEPT_CODE", parm.getValue("DEPT_CODE"));
        this.setValue("STATION_CODE", parm.getValue("STATION_CODE"));
        Pat pat = Pat.onQueryByMrNo(parm.getValue("MR_NO"));
        this.setValue("PAT_NAME", pat.getName());
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("AGE",
                      StringUtil.getInstance().showAge(pat.getBirthday(), date));
        this.setValue("SEX", pat.getSexCode());
        case_no = parm.getValue("CASE_NO");
    }

    /**
     * 数据检核
     * @return boolean
     */
    private boolean checkData() {
        if (case_no == null || case_no.length() <= 0) {
            this.messageBox("就诊序号不能为空");
            return false;
        }
        String mr_no = this.getValueString("MR_NO");
        if (mr_no == null || mr_no.length() <= 0) {
            this.messageBox("病案号不能为空");
            return false;
        }
        String ipd_no = this.getValueString("IPD_NO");
        if (ipd_no == null || ipd_no.length() <= 0) {
            this.messageBox("住院号不能为空");
            return false;
        }
        TParm parm = table.getParmValue();
        boolean flg = false;
        // 检核套餐是否已过退餐时间
        Timestamp now = SystemTool.getInstance().getDate();

        for (int i = 0; i < parm.getCount("SELECT_FLG"); i++) {
            if ("Y".equals(parm.getValue("CHECK_FLG", i))) {
                flg = true;
                String stop = StringTool.getString(parm.getTimestamp("DIET_DATE", i),
                                                   "yyyyMMdd") +
                    parm.getValue("STOP_ORDER_TIME", i);
                Timestamp stop_time = StringTool.getTimestamp(stop, "yyyyMMddHHmm");
                if (now.compareTo(stop_time) > 0) {
                    this.messageBox("病患：" + this.getValueString("PAT_NAME") +
                                    "  " + parm.getValue("DIET_DATE", i).
                                    substring(0, 10) + "  " +
                                    parm.getValue("PACK_CHN_DESC", i)
                                    + "已过订餐时间，不可订餐");
                    return false;
                }
            }
        }
        if (flg) {
            return true;
        }
        this.messageBox("病患未定餐");
        return false;
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

}
