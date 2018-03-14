package com.javahis.ui.onw;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.med.MEDApplyTool;
import com.dongyang.ui.TTable;

/**
 * <p>Title: 报告进度查询</p>
 *
 * <p>Description: 报告进度查询</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-02-03
 * @version 1.0
 */
public class ONWPlanReportControl
    extends TControl {
    public void onInit(){
        super.onInit();
        pageInit();
    }
    /**
     * 页面初始化
     */
    private void pageInit(){
        Object obj = this.getParameter();
        if(obj==null){
            this.messageBox_("参数错误");
            return;
        }
        TParm parm = new TParm();
        if(obj instanceof TParm){
            parm = (TParm)obj;
        }
        TParm data = MEDApplyTool.getInstance().queryMedApply(parm.getValue("CASE_NO"));
        TTable table = (TTable)this.getComponent("Table");
        table.setParmValue(data);
        this.setValue("MR_NO",parm.getValue("MR_NO"));
        this.setValue("PAT_NAME",parm.getValue("PAT_NAME"));
        this.setValue("SEX",parm.getValue("SEX_CODE"));
        this.setValue("DEPT_CODE",parm.getValue("DEPT_CODE"));
        this.setValue("CLINICROOM_NO",parm.getValue("CLINICROOM_NO"));
        this.setValue("DR_CODE",parm.getValue("DR_CODE"));
    }
}
