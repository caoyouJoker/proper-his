package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.dev.DevInStorageTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;

/**
 * <p>Title: 生成入库单对话框</p>
 *
 * <p>Description:生成入库单对话框 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>    
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class RecceiptControl  extends TControl {
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
        setValue("RECEIPT_DATE_BEGIN",timestamp);
        setValue("RECEIPT_DATE_END",timestamp);
    }
    /**
     * 查询动作  
     */ 
    public void onQuery(){
        ((TTable)getComponent("TABLE")).removeRowAll();
        ((TTable)getComponent("TABLE")).resetModify();
        TParm parm = new TParm();
        if(getValueString("RECEIPT_NO").length() != 0) 
            parm.setData("RECEIPT_NO",getValueString("RECEIPT_NO"));
        if(getValueString("INVOICE_NO").length() != 0)
            parm.setData("INVOICE_NO",getValueString("INVOICE_NO")); 
        if(getValueString("RECEIPT_DATE_BEGIN").length() != 0)
            parm.setData("RECEIPT_DATE_BEGIN",StringTool.getTimestamp(getValueString("RECEIPT_DATE_BEGIN"),"yyyy-MM-dd"));
        if(getValueString("RECEIPT_DATE_END").length() != 0)
            parm.setData("RECEIPT_DATE_END",StringTool.getTimestamp(getValueString("RECEIPT_DATE_END"),"yyyy-MM-dd"));
        if(getValueString("RECEIPT_DEPT").length() != 0)
            parm.setData("RECEIPT_DEPT",getValueString("RECEIPT_DEPT"));  
        if(getValueString("RECEIPT_USER").length() != 0)
            parm.setData("RECEIPT_USER",getValueString("RECEIPT_USER"));
        if(getValueString("SUP_CODE").length() != 0)
            parm.setData("SUP_CODE",getValueString("SUP_CODE"));    
            //默认查询未完成状态        
            parm.setData("FINAL_FLG",0);   
        parm = DevInStorageTool.getInstance().selectDevReceipt(parm);
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
        setValue("RECEIPT_NO","");
        setValue("INVOICE_NO","");
        setValue("RECEIPT_DATE_BEGIN","");
        setValue("RECEIPT_DATE_END","");
        setValue("RECEIPT_DEPT","");
        setValue("RECEIPT_USER","");
        setValue("SUP_CODE","");
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
            messageBox("请选中一笔验收单");
            return;
        }
        TParm parmTable = table.getParmValue();
        parm.setData("RECEIPT_NO",parmTable.getValue("RECEIPT_NO",table.getSelectedRow()));
        parm.setData("SUP_CODE",parmTable.getValue("SUP_CODE",table.getSelectedRow()));
        parm.setData("SUP_SALES1",parmTable.getValue("SUP_SALES1",table.getSelectedRow()));
        parm.setData("SUP_SALES1_TEL",parmTable.getValue("SUP_SALES1_TEL",table.getSelectedRow()));
        setReturnValue(parm);
        closeWindow();
    }
}
