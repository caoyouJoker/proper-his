package com.javahis.ui.adm;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMDailyRecControl extends TControl {
    public ADMDailyRecControl() {
    }
    public void onInit(){
        super.onInit();
    }
    public void onDeptAll(){
        if(("Y").equals(getValue("DEPT_ALL"))){
            TTable deptTable = (TTable)this.callFunction(
                    "UI|DEPT_Table|getThis");
            int deptTableRow = deptTable.getRowCount();
            for (int i = 0; i < deptTableRow; i++) {
                this.messageBox_(i);
                deptTable.setValueAt("Y", i, 0);
            }
        }
    }
    /**
     * 科室TABLE点选事件
     */
    public void onStationTable(){
//        this.messageBox("aa");
        TTable stationTable=(TTable)this.callFunction("UI|DEPT_Table|getThis");
        String deptCode=stationTable.getItemString(stationTable.getSelectedRow(),"DEPT_CODE");
        TTable deptTable=(TTable)this.callFunction("UI|STATION_Table|getThis");

        int stationRow = stationTable.getSelectedRow();
        String  fillter = "DEPT_CODE = '"+deptCode+"'";
        deptTable.setFilter(fillter);
        deptTable.filter();
    }
}
