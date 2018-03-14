package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;

/**
 * <p>Title: 病患选择</p>
 *
 * <p>Description:病患选择 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw 2009803
 * @version 1.0
 */
public class BILPationChooseControl
    extends TControl {

    /**
     * 物资主表
     */
    private TTable table;


    public void onInit() {
        super.onInit();
        //项目主表
        table = (TTable) getComponent("TABLE");
        //放入部分参数
        getInitParm();

    }
    /**
     * 得到上级传入的数据
     */
    public void getInitParm() {
        if(this.getParameter()==null)
            return ;
        TParm parm = (TParm)this.getParameter();
        //数据放入界面
        table.setParmValue(parm);
        if(table.getRowCount()==0)
            return;
//        //默认选择第一行
        table.grabFocus();
        table.setSelectedRow(0);
        //数据上翻
        setTextValue(parm.getRow(0));
    }
    /**
     * 把parm中数据显示在界面上
     * @param parm TParm
     */
    public void setTextValue(TParm parm){
        this.setValueForParm("MR_NO;IPD_NO;PAT_NAME;SEX_CODE;IN_DATE",parm);
        this.setValue("OUT_DATE",parm.getData("DS_DATE"));
    }
    /**
     * table点击事件
     */
    public void onTableClicked(){
        int row=table.getSelectedRow();
        if(row<0)
            return;
        TParm parm=table.getParmValue().getRow(row);
        //数据上翻
        setTextValue(parm);
    }
    /**
     * 双击事件
     */
    public void onTableDoubleCliecked(){
        int row=table.getSelectedRow();
        if(row<0){
           messageBox_("请选择病患信息!");
           return;
       }
       TParm parm=table.getParmValue().getRow(row);
       this.setReturnValue(parm);
       this.callFunction("UI|onClose");
    }
    /**
     * 取消
     */
    public void onCancle(){
        this.callFunction("UI|onClose");
    }
    /**
     * 传回
     */
    public void onChoose(){
        onTableDoubleCliecked();
    }
}
