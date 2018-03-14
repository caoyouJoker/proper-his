package com.javahis.ui.onw;

import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import com.dongyang.control.TControl;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import jdo.onw.ONWRegSchdayDrTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.dongyang.util.StringTool;
import java.util.Vector;
import com.dongyang.ui.TComboBox;
import com.javahis.system.combo.TComboDept;
import com.dongyang.ui.TComboBox;
import jdo.reg.SchDayTool;
import jdo.reg.PanelGroupTool;
import com.dongyang.ui.TTextFormat;
import jdo.sys.SYSRegionTool;

/**
 *
 * <p>Title: 普通诊出诊医生维护</p>
 *
 * <p>Description:普通诊出诊医生维护 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ONWRegSchdayDrControl
    extends TControl {
    TParm data, comboldata;
    int selectRow = -1;
    public void onInit() {
        super.onInit();
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTABLEClicked");
        callFunction("UI|save|setEnabled", false);

        setValue("ADM_DATE", SystemTool.getInstance().getDate());//初始化时间
        //初始化诊区Combo
        this.setValue("CLINIC_AREA",Operator.getStation());
        if(this.getValueString("CLINIC_AREA").length()<=0)
            ((TTextFormat)this.getComponent("CLINIC_AREA")).setEnabled(true);
        else{
            ( (TTextFormat)this.getComponent("CLINIC_AREA")).setEnabled(true);
        }
        //===========pangben modify 20110421 start
        this.setValue("REGION_CODE", Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop

        onQuery();
        init();

    }

    /**
     * 初始话查询，与日班表同步
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setData("CLINIC_AREA", Operator.getStation()); //获取护士所属的诊区  护士只能给本诊区排普通诊
        parm.setData("REGION_CODE", Operator.getRegion()); //区域
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm = TIOM_AppServer.executeAction("action.onw.ONWRegSchdayDrAction",
                                            "seldate", parm); //诊间，诊室同步
        if (parm.getErrCode() < 0) {
            messageBox_(parm.getErrText());
            return;
        }
        TParm admdate = new TParm();
        admdate.setData("ADM_DATE", getValue("ADM_DATE"));
        admdate.setData("REGION_CODE", Operator.getRegion());
        admdate.setData("OPT_USER", Operator.getID());
        admdate.setData("OPT_TERM", Operator.getIP());
        admdate = TIOM_AppServer.executeAction(
            "action.onw.ONWRegSchdayDrAction",
            "selReghday", admdate); //与日班表同步
        if (parm.getErrCode() < 0) {
            messageBox_(parm.getErrText());
            return;
        }
        this.onSel();
    }

    /**
     * 查询事件
     */
    public void onSel() {
        TParm selData = new TParm();
        selData = this.finishdata();
        data = new TParm();
        data = ONWRegSchdayDrTool.getInstance().initsel(selData);
        TParm values = new TParm();
        values.setData("CLINIC_AREA", data.getData("CLINIC_AREA"));
        values.setData("CLINICROOM_NO", data.getData("CLINICROOM_NO"));
        values.setData("DEPT_CODE", data.getData("DEPT_CODE"));
        values.setData("DR_CODE", data.getData("DR_CODE"));
        values.setData("SESSION_CODE", data.getData("SESSION_CODE"));
        values.setData("OPT_USER", data.getData("OPT_USER"));
        values.setData("OPT_DATE", data.getData("OPT_DATE"));
        values.setData("OPT_TERM", data.getData("OPT_TERM"));

        this.callFunction("UI|TABLE|setParmValue", values,
                          "CLINIC_AREA;CLINICROOM_NO;SESSION_CODE;DEPT_CODE;DR_CODE;OPT_USER;OPT_DATE;OPT_TERM");
    }

    /**
     * 检核空数据
     * @param parm TParm
     * @return TParm
     */
    public TParm checkEmpty(TParm parm) {
        TParm result = new TParm();
        String str[] = parm.getNames();
        int count = str.length;
        for (int i = 0; i < count; i++) {
            Vector vct = new Vector();
            Vector vct1 = new Vector();
            vct = (Vector) parm.getData(str[i]);
            for (int j = 0; j < vct.size(); j++) {
                vct1.add(vct.get(j) == null ? "" : vct.get(j));
            }
            result.setData(str[i], vct1);
        }
        return result;
    }

    /**
     * TABLE 点击事件
     * @param row int
     */
    public void onTABLEClicked(int row) {
        callFunction("UI|save|setEnabled", true);
        callFunction("UI|ADM_TYPE|setEnabled", false);
        callFunction("UI|SESSION_CODE|setEnabled", false);
        callFunction("UI|CLINICROOM_NO|setEnabled", false);
        callFunction("UI|CLINIC_AREA|setEnabled", false);
        if (row < 0) {
            return;
        }
        setValueForParm(
            "CLINIC_AREA;CLINICROOM_NO;DEPT_CODE;ADM_TYPE;SESSION_CODE",
            data, row);
        selectRow = row;
        //根据科室对人员下拉列表进行筛选  筛选后赋值
        onDEPT_CODE();
        callFunction("UI|DR_CODE|onQuery");
        this.setValue("DR_CODE",data.getValue("DR_CODE",row));
        //查询日班表 带出给号组别
        TParm parm = new TParm();
        parm.setData("ADM_DATE", getValue("ADM_DATE"));
        parm.setData("REGION_CODE",Operator.getRegion());
        parm.setData("ADM_TYPE",data.getValue("ADM_TYPE",row));
        parm.setData("SESSION_CODE",data.getValue("SESSION_CODE",row));
        parm.setData("CLINICROOM_NO",data.getValue("CLINICROOM_NO",row));
        TParm schDay = SchDayTool.getInstance().selectdata(parm); //查询日班表
        this.setValue("QUEGROUP_CODE",schDay.getValue("QUEGROUP_CODE",0));
        this.setValue("CLINICTYPE_CODE",schDay.getValue("CLINICTYPE_CODE",0));
        this.setValue("MAX_QUE",schDay.getValue("MAX_QUE",0));
        this.setValue("QUE_NO",schDay.getValue("QUE_NO",0));
        this.setValue("WEST_MEDI_FLG",schDay.getValue("WEST_MEDI_FLG",0));
        if(schDay.getValue("QUEGROUP_CODE",0).length()>0){
            callFunction("UI|QUEGROUP_CODE|setEnabled", false);
        }else{
            callFunction("UI|QUEGROUP_CODE|setEnabled", true);
        }
        if(schDay.getValue("CLINICTYPE_CODE",0).length()<=0){
            this.setValue("CLINICTYPE_CODE","3");
        }
        if(schDay.getValue("WEST_MEDI_FLG",0).length()>0){
            callFunction("UI|WEST_MEDI_FLG|setEnabled", false);
        }else{
            callFunction("UI|WEST_MEDI_FLG|setEnabled", true);
        }
    }

    /**
     * 保存事件
     */
    public void onSave() {
        if (getValueString("QUEGROUP_CODE").length() == 0) {
            this.messageBox_("给号组别不能为空！");
            return;
        }
        if (getValueString("DEPT_CODE").length() <= 0) {
            this.messageBox_("科别不能为空！");
            return;
        }
        TParm parm = getParmForTag("CLINICROOM_NO;DEPT_CODE;DR_CODE;ADM_TYPE;SESSION_CODE;QUEGROUP_CODE;CLINICTYPE_CODE;QUE_NO;MAX_QUE;WEST_MEDI_FLG");
        parm.setData("OPT_USER", Operator.getID());
        //============pangben modify 20110422 start
        parm.setData("REG_CLINICAREA",this.getValueString("CLINIC_AREA"));
         //============pangben modify 20110422 stop
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("CLINICTMP_FLG","N");//临时诊否  默认为‘N’
        parm.setData("VIP_FLG","N");//是否是VIP诊  默认为“N”
        parm.setData("STOP_SESSION","N");//是否停诊  默认为“N”
        parm.setData("REFRSN_CODE","");//停诊原因 默认为空
        parm.setData("REALDEPT_CODE",this.getValue("DEPT_CODE"));//实际看诊科室
        parm.setData("REALDR_CODE",this.getValue("DR_CODE"));//实际看诊医师
        String admDate = StringTool.getString(TCM_Transform.getTimestamp(
            getValue("ADM_DATE")), "yyyyMMdd");
        parm.setData("ADM_DATE", admDate);
        /**
         * 判断新增还是更新
         */
        if ("".equals(data.getValue("DEPT_CODE", selectRow))) {
            TParm result = TIOM_AppServer.executeAction(
                "action.onw.ONWRegSchdayDrAction",
                "onSave", parm); //诊间，诊室同步
            if (result.getErrCode() < 0) {
                messageBox_(result.getErrText());
                return;
            }
        }
        else {
            TParm result = TIOM_AppServer.executeAction(
                "action.onw.ONWRegSchdayDrAction",
                "onUpdata", parm); //诊间，诊室同步
            if (result.getErrCode() < 0) {
                messageBox_(result.getErrText());
                return;
            }
        }
        int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
        if (row < 0)
            return;
        //刷新，设置末行某列的值
        data.setRowData(row, parm);
        callFunction("UI|TABLE|setRowParmValue", row, data);
        callFunction("UI|QUEGROUP_CODE|setEnabled", false);
        this.messageBox("P0005");
    }

    /**
     * 清空事件
     */
    public void onClear() {
        clearValue(
            "ADM_TYPE;SESSION_CODE;CLINICROOM_NO;DEPT_CODE;DR_CODE;QUEGROUP_CODE;CLINIC_AREA");
        callFunction("UI|ADM_TYPE|setEnabled", true);
        callFunction("UI|SESSION_CODE|setEnabled", true);
        callFunction("UI|CLINICROOM_NO|setEnabled", true);
        callFunction("UI|save|setEnabled", false);
        //初始化诊区Combo
        this.setValue("CLINIC_AREA",Operator.getStation());
        if(this.getValueString("CLINIC_AREA").length()<=0)
            ((TTextFormat)this.getComponent("CLINIC_AREA")).setEnabled(true);
        else{
            ( (TTextFormat)this.getComponent("CLINIC_AREA")).setEnabled(false);
        }
        //===========pangben modify 20110421 start
       this.setValue("REGION_CODE",Operator.getRegion());
        //===========pangben modify 20110421 stop

        this.callFunction("UI|Table|removeRowAll");
        callFunction("UI|QUEGROUP_CODE|setEnabled", true);
        selectRow = -1;
    }

    /**
     * 整理查询条件
     * @return TParm
     */
    public TParm finishdata() {
        TParm parm = new TParm();
        String values = TCM_Transform.getString(this.getValue(
            "ADM_TYPE"));
        if (values.length() > 0)
            parm.setData("ADM_TYPE", values);
        values = TCM_Transform.getString(this.getValue(
            "SESSION_CODE"));
        if (values.length() > 0)
            parm.setData("SESSION_CODE", values);
        values = TCM_Transform.getString(this.getValue(
            "CLINIC_AREA"));
        if (values.length() > 0)
            parm.setData("CLINIC_AREA", values);
        values = TCM_Transform.getString(this.getValue(
            "CLINICROOM_NO"));
        if (values.length() > 0)
            parm.setData("CLINICROOM_NO", values);
        //=========pangben modify 20110421 start
        values = TCM_Transform.getString(this.getValue(
            "REGION_CODE"));
        if (values.length() > 0)
            parm.setData("REGION_CODE", values);
        //=========pangben modify 20110421 start
        return parm;
    }

    /**
     * 得到科室combo
     * @return TComboDept
     */
    public TComboDept getDeptCombo() {
        return (TComboDept)this.callFunction("UI|DEPT_CODE|getThis");
    }

    /**
     * 门急诊点选事件
     */
    public void onAdmType() {
        String admType = getValue("ADM_TYPE").toString();
        if ("O".equals(admType)) { //门诊适用
            getDeptCombo().setOpdFitFlg("Y");
            getDeptCombo().setEmgFitFlg("");
        }
        else { //急诊适用
            getDeptCombo().setOpdFitFlg("");
            getDeptCombo().setEmgFitFlg("Y");
        }
        getDeptCombo().onQuery();
        getDeptCombo().setSelectedIndex(0);
    }
    /**
     * 部门选择事件
     */
    public void onDEPT_CODE(){
        this.clearValue("DR_CODE");
    }
    /**
     * 号别改变事件
     */
    public void onClinicTypeCombo() {
        TParm data = PanelGroupTool.getInstance().getInfobyClinicType(this.
            getValueString("QUEGROUP_CODE"));
        if (this.getValueString("QUEGROUP_CODE").length()> 0) {
            if (data.getErrCode() < 0) {
                messageBox_(data.getErrText());
                return;
            }
            setValue("QUE_NO", 1);
            setValue("MAX_QUE", data.getInt("MAX_QUE", 0));
        }
        else {
            setValue("QUE_NO", 0);
            setValue("MAX_QUE", 0);
        }
    }
}
