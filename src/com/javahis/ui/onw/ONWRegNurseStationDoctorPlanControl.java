package com.javahis.ui.onw;

import com.dongyang.control.TControl;
import com.dongyang.ui.TRadioButton;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.event.TTableEvent;

public class ONWRegNurseStationDoctorPlanControl extends TControl {

    public void onInit() {
        super.onInit();

        callFunction("UI|Panel|addItem", "PANEL1",
                         "%ROOT%\\config\\onw\\ONWDoctorPlan_sys.x");
          init();
    }
    public void onRadioButtonClicked(Object parm) {

        String sys = TCM_Transform.getString(this.getValue(
                "SYS"));
        String report = TCM_Transform.getString(this.getValue(
                "REPORT"));
        String check = TCM_Transform.getString(this.getValue(
                "CHECK"));
        String dept = TCM_Transform.getString(this.getValue(
                "DEPT"));

        if (sys.equals("Y")) {
            callFunction("UI|Panel|addItem", "PANEL1",
                         "%ROOT%\\config\\onw\\ONWDoctorPlan_sys.x");
            return;
        }else

        if (report.equals("Y")) {
            callFunction("UI|Panel|addItem", "PANEL2",
                         "%ROOT%\\config\\onw\\ONWDoctorPlan_report.x");
            return;
        }else

        if (check.equals("Y")) {
            callFunction("UI|Panel|addItem", "PANEL3",
                         "%ROOT%\\config\\onw\\ONWDoctorPlan_check.x");
            return;
        }else

        if (dept.equals("Y")) {
            callFunction("UI|Panel|addItem", "PANEL4",
                         "%ROOT%\\config\\onw\\ONWDoctorPlan_dept.x");
            return;
        }





    }


}
