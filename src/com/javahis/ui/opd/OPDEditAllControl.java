package com.javahis.ui.opd;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

public class OPDEditAllControl extends TControl {
	public void onInit() {
		super.onInit();
        String sql = 
        	"SELECT DS_MED_DAY FROM OPD_SYSPARM";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if(result.getCount()<0){
        	return;
        }
        setValue("TAKE_DAYS", result.getInt("DS_MED_DAY", 0));
	}
	public void onOk(){
		TParm parm=new TParm();
		parm.setData("FREQ_CODE",this.getValueString("FREQ_CODE"));
		parm.setData("ROUTE_CODE",this.getValueString("ROUTE_CODE"));
		parm.setData("MEDI_QTY",this.getValueDouble("MEDI_QTY"));
		parm.setData("TAKE_DAYS",this.getValueInt("TAKE_DAYS"));
		this.setReturnValue(parm);
		this.closeWindow();
	}
	
}
