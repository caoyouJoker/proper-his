package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import jdo.reg.SchWeekTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 *
 * <p>Title:医师周班表控制类 </p>
 *
 * <p>Description:医师周班表控制类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.08.26
 * @version 1.0
 */
public class REGSchWeekControl
    extends TControl {
//    TParm data;
    int selectRow = -1;
    public void onInit() {
        super.onInit();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
        //初始化REGION登陆默认登录区域
        //======pangben modify 20110410
        callFunction("UI|REGION_CODE|setValue", Operator.getRegion());
        //========pangben modify 20110421 start 权限添加
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop

        onQuery();
    }

    /**
     *增加对Table的监听
     * @param row int
     */
    public void onTableClicked(int row) {

        if (row < 0)
            return;
        TParm data=(TParm)callFunction("UI|TABLE|getParmValue");
        setValueForParm(
            "REGION_CODE;ADM_TYPE;DAYOFWEEK;SESSION_CODE;CLINICTYPE_CODE;" +
            "DEPT_CODE;DR_CODE;CLINICROOM_NO;QUEGROUP_CODE;WEST_MEDI_FLG;" +
            "CREAT_DATE;CLINICTMP_FLG;EXP_DATE;EXP_DATE_E;REG_SPECIAL_NUMBER",
            data, row);
        selectRow = row;
        TextFormatSYSOperator operatorText = (TextFormatSYSOperator)this.getComponent("DR_CODE");
        operatorText.onQuery();
        setValue("DR_CODE",data.getValue("DR_CODE",row));
//        //初始化Combo不可编辑
//        callFunction("UI|ADM_TYPE|setEnabled", false);

    }

    /**
     * 新增
     */
    public void onInsert() {
        //校验是否输入空值,并弹出提示信息
//        if (!this.emptyTextCheck(
//            "CLINICTYPE_CODE,DEPT_CODE,DR_CODE,QUEGROUP_CODE,WEST_MEDI_FLG"))
//            return;
        if (getValue("CLINICTYPE_CODE") == null ||
            this.getValueString("CLINICTYPE_CODE").length() == 0) {
            this.messageBox("号别不能为空!");
            return;
        }
        if (getValue("DEPT_CODE") == null ||
            this.getValueString("DEPT_CODE").length() == 0) {
            this.messageBox("科室不能为空!");
            return;
        }
        if (getValue("DR_CODE") == null ||
            this.getValueString("DR_CODE").length() == 0) {
            this.messageBox("医生不能为空!");
            return;
        }
        if (getValue("QUEGROUP_CODE") == null ||
            this.getValueString("QUEGROUP_CODE").length() == 0) {
            this.messageBox("给号组别不能为空!");
            return;
        }
        if (getValue("WEST_MEDI_FLG") == null ||
            this.getValueString("WEST_MEDI_FLG").length() == 0) {
            this.messageBox("中西医注记不能为空!");
            return;
        }

        TParm parm = getParmForTag(
            "REGION_CODE;ADM_TYPE;DAYOFWEEK;SESSION_CODE;REG_SPECIAL_NUMBER;CLINICTYPE_CODE;" +
            "DEPT_CODE;DR_CODE;CLINICROOM_NO;QUEGROUP_CODE;WEST_MEDI_FLG;" +
            "CREAT_DATE:Timestamp;CLINICTMP_FLG;EXP_DATE:Timestamp;EXP_DATE_E:Timestamp;REG_SPECIAL_NUMBER");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = SchWeekTool.getInstance().insertdata(parm);

        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //显示新增数据
        callFunction("UI|TABLE|addRow", parm,
                     "REGION_CODE;ADM_TYPE;DAYOFWEEK;SESSION_CODE;REG_SPECIAL_NUMBER;CLINICTYPE_CODE;" +
                     "DEPT_CODE;DR_CODE;CLINICROOM_NO;QUEGROUP_CODE;WEST_MEDI_FLG;" +
                     "CREAT_DATE;CLINICTMP_FLG;EXP_DATE;EXP_DATE_E;OPT_USER;OPT_DATE;OPT_TERM;REG_SPECIAL_NUMBER");
        // 把数据上翻到控件显示
        this.messageBox("P0002");

    }

    /**
     * 更新
     */
    public void onUpdate() {
        TParm parm = getParmForTag(
            "REGION_CODE;ADM_TYPE;DAYOFWEEK;SESSION_CODE;CLINICTYPE_CODE;" +
            "DEPT_CODE;DR_CODE;CLINICROOM_NO;QUEGROUP_CODE;WEST_MEDI_FLG;" +
            "CREAT_DATE:Timestamp;CLINICTMP_FLG;EXP_DATE:Timestamp;EXP_DATE_E:Timestamp;REG_SPECIAL_NUMBER");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        //old value 拿到更新前的旧值
        TTable table = (TTable)this.getComponent("Table");
        int selRow = table.getSelectedRow();
        String oldRegionCode = table.getItemString(selRow,"REGION_CODE");
        String oldAdmType = table.getItemString(selRow,"ADM_TYPE");
        int oldDayOfWeek = TypeTool.getInt(table.getItemString(selRow,"DAYOFWEEK")) ;
//        this.messageBox_(oldDayOfWeek);
        String oldSessionCode = table.getItemString(selRow,"SESSION_CODE");
        String oldClinicRoomNo = table.getItemString(selRow,"CLINICROOM_NO");
        parm.setData("OLD_REGION_CODE",oldRegionCode);
        parm.setData("OLD_ADM_TYPE",oldAdmType);
        parm.setData("OLD_DAYOFWEEK",oldDayOfWeek);
        parm.setData("OLD_SESSION_CODE",oldSessionCode);
        parm.setData("OLD_CLINICROOM_NO",oldClinicRoomNo);

        TParm result = SchWeekTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //刷新，设置末行某列的值
        int row = (Integer) callFunction("UI|Table|getSelectedRow");
        if (row < 0)
            return;
        TParm data=(TParm)callFunction("UI|TABLE|getParmValue");
        data.setRowData(row, parm);
        callFunction("UI|Table|setRowParmValue", row, data);
        this.messageBox("P0001");

    }

    /**
     * 保存
     */
    public void onSave() {
        if (selectRow == -1) {
            onInsert();
            return;
        }
        onUpdate();
    }


    /**
     * 删除
     */
    public void onDelete() {
        if (this.messageBox("是否删除", "询问", 2) == 0) {
            if (selectRow == -1)
                return;
            String region = getValue("REGION_CODE").toString();
            String admType = getValue("ADM_TYPE").toString();
            String dayofWeek = getValue("DAYOFWEEK").toString();
            String sessionCode = getValue("SESSION_CODE").toString();
            String clinicroomNo = getValue("CLINICROOM_NO").toString();
            TParm result = SchWeekTool.getInstance().deletedata(region, admType,
                dayofWeek, sessionCode, clinicroomNo);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //删除单行显示
            int row = (Integer) callFunction("UI|Table|getSelectedRow");
            if (row < 0)
                return;
            this.callFunction("UI|Table|removeRow", row);
            this.callFunction("UI|Table|setSelectRow", row);

            this.messageBox("P0003");
        }
        else {
            return;
        }
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = getParmForTag(
            "REGION_CODE;ADM_TYPE;DAYOFWEEK;SESSION_CODE;CLINICROOM_NO;DEPT_CODE;DR_CODE", true);

        TParm data = SchWeekTool.getInstance().selectdata(parm);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        this.callFunction("UI|Table|setParmValue", data);

    }

    /**
     *展开
     */
    public void onSpreadout() {
        this.openDialog("%ROOT%\\config\\reg\\REGSchWeekForDay.x");
    }
    /**
     *周转日
     */
    public void onSpreadoutNew() {
        this.openDialog("%ROOT%\\config\\reg\\REGSchWeekForDayNew.x");
    }

    /**
     *清空
     */
    public void onClear() {
        clearValue(
            "ADM_TYPE;DAYOFWEEK;SESSION_CODE;CLINICTYPE_CODE;" +
            "DEPT_CODE;DR_CODE;CLINICROOM_NO;QUEGROUP_CODE;WEST_MEDI_FLG;" +
            "CREAT_DATE;CLINICTMP_FLG;EXP_DATE;EXP_DATE_E;REG_SPECIAL_NUMBER");
        this.callFunction("UI|Table|clearSelection");
        setValue("REGION_CODE",Operator.getRegion());
        selectRow = -1;

    }

}
