package com.javahis.ui.odi;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OdiRxEditAllControl
    extends TControl {
    public void onInit() {
                super.onInit();
                String sql = 
                	"SELECT DS_MED_DAY FROM ODI_SYSPARM";
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
