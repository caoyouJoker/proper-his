package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.dev.DevInStorageTool;
import jdo.dev.DevOutRequestMTool;

import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;  
  
/**
 * <p>Title: 生成请领单</p>  
 *
 * <p>Description:生成请领单 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class RequestMControl  extends TControl {
   /**
     * 初始化方法 
     */
    public void onInit() {
        super.onInit();
        initComponent();
        onQuery();
    }
    /**
     * 初始化界面空间默认值  
     */
    public void initComponent(){
        Timestamp timestamp = SystemTool.getInstance().getDate();
        setValue("QSTART_DATE",timestamp);
        setValue("QEND_DATE",timestamp);  
    }
    /**
     * 查询动作
     */
    public void onQuery(){ 
        ((TTable)getComponent("TABLE")).removeRowAll();
        ((TTable)getComponent("TABLE")).resetModify();
        TParm parm = new TParm();
        if(getValueString("REQUEST_NO").length() != 0) 
            parm.setData("REQUEST_NO",getValueString("REQUEST_NO"));
        if(getValueString("QSTART_DATE").length() != 0)
            parm.setData("REQUEST_DATE_BEGIN",StringTool.getTimestamp(getValueString("QSTART_DATE"),"yyyy-MM-dd"));
        if(getValueString("QEND_DATE").length() != 0)
            parm.setData("REQUEST_DATE_END",StringTool.getTimestamp(getValueString("QEND_DATE"),"yyyy-MM-dd"));
        if(getValueString("REQUEST_DEPT").length() != 0) //请领科室 
            parm.setData("TO_ORG_CODE",getValueString("REQUEST_DEPT"));
        if(getValueString("REQUEST_USER").length() != 0)
            parm.setData("REQUEST_USER",getValueString("REQUEST_USER"));
            parm.setData("FINAL_FLG","N"); 
        parm = DevOutRequestMTool.getInstance().queryRequestM(parm);  
        System.out.println("查询动作parm"+parm);
        if(parm.getErrCode()<0)
            return;         
        if(parm.getCount() < 0)
            return;
        ((TTable)getComponent("TABLE")).setParmValue(parm);
    }

    /**
     * 清空方法
     */
    public void onCLear(){
        setValue("REQUEST_NO","");
        setValue("QSTART_DATE","");
        setValue("QEND_DATE","");
        setValue("REQUEST_DEPT","");
        setValue("REQUEST_USER","");
        ((TTable)getComponent("TABLE")).removeRowAll();
        ((TTable)getComponent("TABLE")).resetModify();
    }

    /**
     * 生成入库单
     */
    public void onGenerateReceipt(){
        TParm parm = new TParm();
        TTable table = (TTable)getComponent("TABLE");
        if(table.getSelectedRow() < 0){
            messageBox("请选中一笔请领单");
            return; 
        } 
        TParm parmTable = table.getParmValue();
        //请领单号
        parm.setData("REQUEST_NO",parmTable.getValue("REQUEST_NO",table.getSelectedRow()));
        //请领科室
        parm.setData("INWAREHOUSE_DEPT",parmTable.getValue("REQUEST_DEPT",table.getSelectedRow()));
        setReturnValue(parm);
        closeWindow();
    }
}
