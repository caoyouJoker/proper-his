package com.javahis.ui.onw;

import com.dongyang.control.*;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import jdo.onw.ONWMEDProgressTool;
import com.dongyang.ui.TTable;

/**
 * <p>Title: 医技进度</p>
 *
 * <p>Description: 医技进度</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-26
 * @version 1.0
 */
public class ONWMEDProgressControl
    extends TControl {
    TTable TABLE;
    public void onInit(){
        super.onInit();
        this.setValue("DATE",SystemTool.getInstance().getDate());
        TABLE= (TTable)this.getComponent("Table");
        onQuery();
    }
    /**
     * 查询
     */
    public void onQuery(){
        String Q_DATE = this.getText("DATE").replace("/","");//查询日期
        String RPTTYPE_CODE = "";
        if(this.getValue("RPTTYPE_CODE")!=null&&!this.getValueString("RPTTYPE_CODE").equals("")){
            RPTTYPE_CODE = this.getValueString("RPTTYPE_CODE");
            TABLE.setHeader("检查项目,150,RPT_TYPE;执行科室,150;开单人数,150;等待人数,150");
        }else if(this.getValue("SUB_SYSTEM_CODE")!=null&&!this.getValueString("SUB_SYSTEM_CODE").equals("")){
            RPTTYPE_CODE = this.getValueString("SUB_SYSTEM_CODE");
            TABLE.setHeader("检查项目,150,RPT_TYPE;执行科室,150;开单人数,150;等待人数,150");
        }else{
            RPTTYPE_CODE = this.getValueString("SUB_SYSTEM_CODE");
            TABLE.setHeader("系统名称,150,RPT_TYPE;执行科室,150;开单人数,150;等待人数,150");
        }
        TParm result = ONWMEDProgressTool.getInstance().selectData(RPTTYPE_CODE,this.getValueString("EXEC_DEPT_CODE"),Q_DATE);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            TABLE.removeRowAll();
            return;
        }
        if(result.getCount()<=0){
            this.messageBox("E0008");
            TABLE.removeRowAll();
            return;
        }
        TABLE.setParmValue(result);
    }
    /**
     * 清空
     */
    public void onClear(){
        this.clearValue("SUB_SYSTEM_CODE;RPTTYPE_CODE;EXEC_DEPT_CODE");
        this.onQuery();
    }
    /**
     * 系统名称 选择事件
     */
    public void onSelect(){
        this.clearValue("RPTTYPE_CODE");
    }
}
