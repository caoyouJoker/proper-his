package com.javahis.ui.ope;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.ope.OPEDeptOpTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>Title: 科室常用手术代码选择</p>
 *
 * <p>Description: 科室常用手术代码选择</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-26
 * @version 4.0
 */
public class OPEDeptOpShowControl
    extends TControl {
    String DEPT_CODE = "";//部门
    String ICD = "";
    public void onInit(){
        super.onInit();
        //获取参数
        Object obj = this.getParameter();
        if(obj instanceof String){
            DEPT_CODE = (String)obj;
        }else{
            this.messageBox_("获取参数失败");
            return;
        }
        this.setValue("DEPT",DEPT_CODE);
        TParm parm = new TParm();
        parm.setData("DEPT_CODE",DEPT_CODE);
        TParm result = OPEDeptOpTool.getInstance().selectData(parm);
        TTable table= (TTable)this.getComponent("Table");
        table.setParmValue(result);
    }
    /**
     * 回传事件
     */
    public void onBack(){
        TTable table= (TTable)this.getComponent("Table");
        int row = table.getSelectedRow();
        if(row>=0){
            ICD = table.getValueAt(row,0).toString();
            this.setReturnValue(table.getValueAt(row,0));
            this.closeWindow();
        }
    }
    /**
     * TABLE双击事件
     * @param row int
     */
    public void onDoubleTableClicked(int row) {
        TTable table = (TTable)this.getComponent("Table");
        ICD = table.getValueAt(row,0).toString();
        this.setReturnValue(table.getValueAt(row, 0));
        //关闭界面
        this.closeWindow();
    }
    /**
     * 关闭事件
     * @return boolean
     */
    public boolean onClosing(){
        if(ICD.length()==0){
            this.setReturnValue("");
        }
        return true;
    }
}
