package com.javahis.ui.hrm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * <p> Title: 病患健检信息选择窗口 </p>
 * 
 * <p> Description: 病患健检信息选择窗口  </p>
 * 
 * <p> Copyright: Copyright (c) 2012 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 * @author wanglong 2012.12.17
 * @version 1.0
 */
public class HRMPatRecordControl extends TControl {
    private static String TABLE = "TABLE";
    private TTable table;

    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent(TABLE);
        TParm parm = (TParm) this.getParameter();
        if ((parm.getErrCode() != 0) || parm.getCount() <= 0) {
            messageBox("E0024");// 初始化参数失败
            return;
        }
        this.callFunction("UI|" + TABLE + "|setParmValue", parm);
        parm=parm.getRow(0);
//        this.setValueForParm("MR_NO;PAT_NAME;SEX_CODE;TEL;IDNO",
//                parm);
//        this.setValue("BIRTH_DATE", StringTool.getString(parm.getTimestamp("BIRTH_DATE"),"yyyy/MM/dd"));
    }

    /**
     * TABLE双击事件
     */
    public void onTableDoubleCliecked() {
        if ((table.getShowCount() > 0) && (table.getSelectedRow() < 0)) {
            messageBox("请选择一条记录");
            return;
        }
        int row = table.getSelectedRow();
        TParm parm = table.getParmValue();
        TParm selparm = parm.getRow(row);
        TParm result = new TParm();
        for (int i = 0; i < selparm.getNames().length; i++) {
            result.addData(selparm.getNames()[i], selparm.getData(selparm.getNames()[i]));
        }
        result.setCount(1);
        this.setReturnValue(result);
        this.closeWindow();
    }
    
    /**
     * TABLE单击事件
     */
    public void onTableClicked() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        TParm tableParm = table.getParmValue().getRow(row);
        this.setValueForParm("MR_NO;PAT_NAME;SEX_CODE;TEL;IDNO", tableParm);
        this.setValue("BIRTH_DATE", StringTool.getString(tableParm.getTimestamp("BIRTH_DATE"),"yyyy/MM/dd"));
    }
    
    /**
     * 回传
     */
    public void onReturn() {
        onTableDoubleCliecked();
    }

    /**
     * 取消
     */
    public void onCancel() {
        this.closeWindow();
    }
}
