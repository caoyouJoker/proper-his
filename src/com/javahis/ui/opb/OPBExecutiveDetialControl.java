package com.javahis.ui.opb;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TTable;
import jdo.opb.OPBExecutiveDeptListTool;

/**
 * <p>Title: 执行科室明细</p>
 *
 * <p>Description: 执行科室明细</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-4-10
 * @version 4.0
 */
public class OPBExecutiveDetialControl
    extends TControl {
    public void onInit(){
        super.onInit();
        pageInit();
    }
    /**
     * 页面初始化
     */
    public void pageInit(){
        TParm obj = (TParm)this.getParameter();
        this.setValue("DATE_S",StringTool.getTimestamp( obj.getValue("DATE_S"),"yyyyMMdd"));
        this.setValue("DATE_E",StringTool.getTimestamp( obj.getValue("DATE_E"),"yyyyMMdd"));
        this.setValue("DEPT_CODE",obj.getValue("DEPT_CODE"));
        this.setValue("DR_CODE",obj.getValue("DR_CODE"));
        TParm parm = new TParm();
        parm.setData("DATE_S",obj.getValue("DATE_S"));
        parm.setData("DATE_E",obj.getValue("DATE_E")+"235959");
        parm.setData("DEPT_CODE",obj.getValue("DEPT_CODE"));
        parm.setData("DR_CODE",obj.getValue("DR_CODE"));
        TParm result = OPBExecutiveDeptListTool.getInstance().selectDetial(parm);
        TTable table = (TTable)this.getComponent("TABLE");
        table.setParmValue(result);
    }
}
