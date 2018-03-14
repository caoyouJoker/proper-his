package com.javahis.ui.nss;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TRadioButton;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import jdo.adm.ADMInpTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import com.javahis.util.StringUtil;
import java.sql.Timestamp;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.event.TTableEvent;
import jdo.nss.NSSOrderTool;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.data.TNull;
import jdo.adm.ADMTool;

/**
 * <p>Title: 收费</p>
 *
 * <p>Description: 收费</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 * 
 * @author zhangy 2010.11.18
 * @version 1.0
 */
public class NSSChargeControl
    extends TControl {
    public NSSChargeControl() {
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
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        if (case_no.length() <= 0) {
            this.messageBox("请选择订餐病患");
            return;
        }
        TParm parm = new TParm();
        parm.setData("CASE_NO", case_no);
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
            parm.setData("BILL_FLG", "N");
        }
        else {
            parm.setData("BILL_FLG", "Y");
        }
        int start_date = StringTool.getWeek( (Timestamp)this.getValue(
            "START_DATE"));
        int end_date = StringTool.getWeek( (Timestamp)this.getValue(
            "END_DATE"));
        parm.setData("START_DATE", "0" + start_date);
        if (end_date == 0) {
            end_date = 7;
        }
        parm.setData("END_DATE", "0" + end_date);
        result = NSSOrderTool.getInstance().getNSSCharge(parm);
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
        table.setParmValue(result);
    }
    /**
     * 保存方法
     */
    public void onSave() {
        if (!checkData()) {
            return;
        }
        TParm parm = new TParm();
        TParm result = new TParm();
        TParm tableParm = table.getParmValue();
        Timestamp now = SystemTool.getInstance().getDate();
        //查询病患身份
        TParm admParm = new TParm();
        admParm.setData("CASE_NO", case_no);
        result = ADMTool.getInstance().getADM_INFO(admParm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("病患身份错误");
            return;
        }

        //zhangyong20110516 添加区域REGION_CODE
        parm.setData("REGION_CODE", Operator.getRegion());

        for (int i = 0; i < tableParm.getCount("SELECT_FLG"); i++) {
            if ("Y".equals(tableParm.getValue("SELECT_FLG", i))) {
                parm.addData("CASE_NO", case_no);
                parm.addData("DIET_DATE", StringTool.getString(tableParm.
                    getTimestamp("DIET_DATE", i), "yyyyMMdd"));
                parm.addData("MEAL_CODE", tableParm.getValue("MEAL_CODE", i));
                parm.addData("BILL_FLG", "Y");
                parm.addData("BILL_DATE", now);
                parm.addData("CASHIER_CODE", Operator.getID());
                parm.addData("OPT_USER", Operator.getID());
                parm.addData("OPT_DATE", now);
                parm.addData("OPT_TERM", Operator.getIP());
                parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
                parm.addData("ORDER_CAT1_CODE",
                             tableParm.getValue("ORDER_CAT1_CODE", i));
                parm.addData("CAT1_TYPE", tableParm.getValue("CAT1_TYPE", i));
                parm.addData("HIDE_FLG", tableParm.getValue("HIDE_FLG", i));
                parm.addData("MEDI_QTY", 1);
                parm.addData("MEDI_UNIT", tableParm.getValue("UNIT_CODE", i));
                parm.addData("TAKE_DAYS", 1);
                parm.addData("DOSAGE_UNIT", tableParm.getValue("UNIT_CODE", i));
                parm.addData("OWN_PRICE", tableParm.getDouble("OWN_PRICE", i));
                parm.addData("NHI_PRICE", tableParm.getDouble("NHI_PRICE", i));
                parm.addData("DOSAGE_QTY", 1);
                parm.addData("CTZ1_CODE", result.getValue("CTZ1_CODE", 0));
                parm.addData("CTZ2_CODE", result.getValue("CTZ3_CODE", 0));
                parm.addData("CTZ3_CODE", result.getValue("CTZ3_CODE", 0));
                parm.addData("IPD_NO", result.getValue("IPD_NO", 0));
                parm.addData("MR_NO", result.getValue("MR_NO", 0));
                parm.addData("DEPT_CODE", result.getValue("DEPT_CODE", 0));
                parm.addData("ORDER_DEPT_CODE", result.getValue("DEPT_CODE", 0));
                parm.addData("STATION_CODE", result.getValue("STATION_CODE", 0));
                parm.addData("BED_NO", result.getValue("BED_NO", 0));
                parm.addData("DISPENSE_EFF_DATE", now);
                parm.addData("DISPENSE_END_DATE", now);
                parm.addData("FREQ_CODE", "STAT");
                parm.addData("ORDER_DR_CODE", result.getValue("VS_DR_CODE", 0));
                parm.addData("EXEC_DEPT_CODE", Operator.getDept());
            }
        }

        result = TIOM_AppServer.executeAction(
            "action.nss.NSSOrderAction", "onUpdateNSSChagre", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("保存失败");
        }
        else {
            this.messageBox("保存成功");
            onQuery();
        }
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("请选择退费的套餐");
        }
        TParm parm = table.getParmValue().getRow(row);
        String message = "病患：" + this.getValueString("PAT_NAME") + "  " +
            parm.getValue("DIET_DATE").substring(0, 10) + "  " +
            parm.getValue("PACK_CHN_DESC");
        // 检核套餐是否已过退餐时间
        Timestamp now = SystemTool.getInstance().getDate();
//        String stop = StringTool.getString(parm.getTimestamp("DIET_DATE"),
//                                           "yyyyMMdd") +
//            parm.getValue("STOP_ORDER_TIME");
//        Timestamp stop_time = StringTool.getTimestamp(stop, "yyyyMMddHHmm");
//        if (now.compareTo(stop_time) > 0) {
//            this.messageBox(message + "已过退餐时间，不可退费");
//            return;
//        }

        //查询病患身份
        TParm admParm = new TParm();
        admParm.setData("CASE_NO", case_no);
        TParm result = ADMTool.getInstance().getADM_INFO(admParm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("病患身份错误");
            return;
        }

        // 退费
        TNull timeNull = new TNull(Timestamp.class);
        TParm inparm = new TParm();
        inparm.addData("DIET_DATE",
                     StringTool.getString(parm.getTimestamp("DIET_DATE"),
                                          "yyyyMMdd"));
        inparm.addData("BILL_FLG", "N");
        inparm.addData("BILL_DATE", timeNull);
        inparm.addData("CASHIER_CODE", "");

        inparm.addData("CASE_NO", case_no);
        inparm.addData("MEAL_CODE", parm.getValue("MEAL_CODE"));
        inparm.addData("OPT_USER", Operator.getID());
        inparm.addData("OPT_DATE", now);
        inparm.addData("OPT_TERM", Operator.getIP());
        inparm.addData("ORDER_CODE", parm.getValue("ORDER_CODE"));
        inparm.addData("ORDER_CAT1_CODE", parm.getValue("ORDER_CAT1_CODE"));
        inparm.addData("CAT1_TYPE", parm.getValue("CAT1_TYPE"));
        inparm.addData("HIDE_FLG", parm.getValue("HIDE_FLG"));
        inparm.addData("MEDI_QTY", 1);
        inparm.addData("MEDI_UNIT", parm.getValue("UNIT_CODE"));
        inparm.addData("TAKE_DAYS", 1);
        inparm.addData("DOSAGE_UNIT", parm.getValue("UNIT_CODE"));
        inparm.addData("OWN_PRICE", parm.getDouble("OWN_PRICE"));
        inparm.addData("NHI_PRICE", parm.getDouble("NHI_PRICE"));
        inparm.addData("DOSAGE_QTY", 1);
        inparm.addData("CTZ1_CODE", result.getValue("CTZ1_CODE", 0));
        inparm.addData("CTZ2_CODE", result.getValue("CTZ3_CODE", 0));
        inparm.addData("CTZ3_CODE", result.getValue("CTZ3_CODE", 0));
        inparm.addData("IPD_NO", result.getValue("IPD_NO", 0));
        inparm.addData("MR_NO", result.getValue("MR_NO", 0));
        inparm.addData("DEPT_CODE", result.getValue("DEPT_CODE", 0));
        inparm.addData("ORDER_DEPT_CODE", result.getValue("DEPT_CODE", 0));
        inparm.addData("STATION_CODE", result.getValue("STATION_CODE", 0));
        inparm.addData("BED_NO", result.getValue("BED_NO", 0));
        inparm.addData("DISPENSE_EFF_DATE", now);
        inparm.addData("DISPENSE_END_DATE", now);
        inparm.addData("FREQ_CODE", "STAT");
        inparm.addData("ORDER_DR_CODE", result.getValue("VS_DR_CODE", 0));
        inparm.addData("EXEC_DEPT_CODE", Operator.getDept());
        //System.out.println("-----"+inparm);

        //zhangyong20110516 添加区域REGION_CODE
        inparm.setData("REGION_CODE", Operator.getRegion());

        result = TIOM_AppServer.executeAction(
            "action.nss.NSSOrderAction", "onUpdateNSSUnChagre", inparm);
        if (result.getErrCode() < 0) {
            this.messageBox("退费失败");
        }
        else {
            this.messageBox("退费成功");
            table.removeRow(row);
        }
    }

    /**
     * 清空方法
     */
    public void onClear() {
        this.clearValue("DIET_TYPE;PACK_CODE;SELECT_ALL");
        table.removeRowAll();
        this.getRadioButton("RadioButton_1").setSelected(true);
        onChangeRadionButton();
        //case_no = "";
    }

    /**
     * 全选事件
     */
    public void onSelectAll() {
        if (this.getRadioButton("RadioButton_1").isSelected()) {
            String select_flg = this.getValueString("SELECT_ALL");
            for (int i = 0; i < table.getRowCount(); i++) {
                table.setItem(i, "SELECT_FLG", select_flg);
            }
            table.acceptText();
        }
    }

    /**
     * 单选按钮改变事件
     */
    public void onChangeRadionButton() {
        if (this.getRadioButton("RadioButton_1").isSelected()) {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
            table.setLockColumns("1,2,3,4,5,6,7,8,9,10,11");
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
    }

    /**
     * MR_NO回车事件
     */
    public void onMrNoAction() {
        String mr_no = this.getValueString("MR_NO");
        this.setValue("MR_NO", StringTool.fill0(mr_no, PatTool.getInstance().getMrNoLength()));//====cehnxi 
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
        for (int i = 0; i < parm.getCount("SELECT_FLG"); i++) {
            if ("Y".equals(parm.getValue("SELECT_FLG", i))) {
                flg = true;
            }
        }
        if (flg) {
            return true;
        }
        this.messageBox("没有收费数据");
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
