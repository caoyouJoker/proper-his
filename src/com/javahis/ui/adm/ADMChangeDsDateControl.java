package com.javahis.ui.adm;

import com.dongyang.control.*;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import java.sql.Timestamp;

/**
 * <p>Title: 出院日期选择</p>
 *
 * <p>Description: 出院日期选择</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-18
 * @version 1.0
 */
public class ADMChangeDsDateControl
    extends TControl {
    public void onInit(){
        super.onInit();
        this.setValue("T_Now",SystemTool.getInstance().getDate());
        Timestamp last;
        Object obj = this.getParameter();
        if (obj instanceof Timestamp) {
            last = (Timestamp)obj;
            this.setValue("T_Last",last);
        }
    }
    /**
     * 返回选中时间
     */
    public void onSave(){
        if(this.getValueString("R1").equals("Y")){
            this.setReturnValue(this.getValue("T_Last"));
            this.closeWindow();
        }else if(this.getValueString("R2").equals("Y")){
            this.setReturnValue(this.getValue("T_Now"));
            this.closeWindow();
        }
    }
}
