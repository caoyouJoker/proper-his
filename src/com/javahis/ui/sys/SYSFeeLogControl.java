package com.javahis.ui.sys;

import java.awt.event.KeyEvent;
import java.util.Date;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
/**
 *
 * <p>Title:SYS Fee 下拉选择框(3.0调4.0适用) </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class SYSFeeLogControl extends TControl{
    private String oldText = "";
    private TTable table;
    //住院中医医生站使用
    private String rxType;
    /**
     * 同步SYS_FEE数据
     */
    public void onInitParameter(){
        TIOM_Database.logTableAction("SYS_FEE");

    }
    /**
     * 初始化
     */
    public void onInit()
    {
        super.onInit();
        table = (TTable)callFunction("UI|TABLE|getThis");
        callFunction("UI|EDIT|addEventListener",TTextFieldEvent.KEY_RELEASED,this,"onKeyReleased");
        callFunction("UI|EDIT|addEventListener","EDIT->" + TKeyListener.KEY_PRESSED,this,"onKeyPressed");
        table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED,this,"onDoubleClicked");
        initParamenter();
    }
    /**
     * 初始化参数
     */
    public void initParamenter()
    {
        Object obj = getParameter();
        if(obj == null)
            return;
        if(!(obj instanceof TParm))
            return;
        TParm parm = (TParm)obj;
        rxType=parm.getValue("RX_TYPE");
        String text = parm.getValue("TEXT");
        setEditText(text);

    }
    /**
     * 重新加载
     */
    public void onInitReset()
    {
        Object obj = getParameter();
        if(obj == null)
            return;
        if(!(obj instanceof TParm))
            return;
        TParm parm = (TParm)obj;
        String text = parm.getValue("TEXT");
        String oldText = (String)callFunction("UI|EDIT|getText");
        if(oldText.equals(text))
            return;
        setEditText(text);
    }
    /**
     * 设置输入文字
     * @param s String
     */
    public void setEditText(String s)
    {
        callFunction("UI|EDIT|setText",s);
        int x = s.length();
        callFunction("UI|EDIT|select",x,x);
        onKeyReleased(s);
    }
    /**
     * 按键事件
     * @param s String
     */
    public void onKeyReleased(String s)
    {
        s = s.toUpperCase();
        if(oldText.equals(s))
            return;
        oldText = s;
        table.filterObject(this,"filter");
        int count = table.getRowCount();
        if(count > 0)
            table.setSelectedRow(0);
    }
    /**
     * 过滤方法
     * @param parm TParm
     * @param row int
     * @return boolean
     */
    public boolean filter(TParm parm, int row) {
        Date date = new Date();
        Long today = date.getTime();
        Long startDate = parm.getTimestamp("START_DATE", row).getTime();
        Long endDate = parm.getTimestamp("END_DATE", row).getTime();
        if (startDate > today || today > endDate){
        	return false;
        }
        boolean original=parm.getValue("ORDER_CODE", row).toUpperCase().
        startsWith(oldText) ||
        parm.getValue("ORDER_DESC", row).toUpperCase().indexOf(oldText) > 0 ||
        parm.getValue("ORD_PYCODE", row).toUpperCase().startsWith(oldText) ||
        parm.getValue("NOMENCLATURE",
                      row).toUpperCase().startsWith(oldText) ;
        if(rxType==null||rxType.trim().length()<1){
        	return original;
        }
        	
        if("3".equalsIgnoreCase(rxType)){
        	original=parm.getValue("ORDER_CODE",row).startsWith(rxType) && original;
        }
        return original;

    }
    /**
     * 按键事件
     * @param e KeyEvent
     */
    public void onKeyPressed(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
        {
            callFunction("UI|setVisible",false);
            return;
        }
        int count = table.getRowCount();
        if(count <= 0)
            return;
        switch(e.getKeyCode()){
        case KeyEvent.VK_UP:
            int row = table.getSelectedRow() - 1;
            if(row < 0)
                row = 0;
            table.setSelectedRow(row);
            break;
        case KeyEvent.VK_DOWN:
            row = table.getSelectedRow() + 1;
            if(row >= count)
                row = count - 1;
            table.setSelectedRow(row);
            break;
        case KeyEvent.VK_ENTER:
            callFunction("UI|setVisible",false);
            onSelected();
            break;
        }
    }
    /**
     * 行双击事件
     * @param row int
     */
    public void onDoubleClicked(int row)
    {
        if(row < 0)
            return;
        callFunction("UI|setVisible",false);
        onSelected();
    }
    /**
     * 选中
     */
    public void onSelected()
    {
        int row = table.getSelectedRow();
        if(row < 0)
            return;
        TDataStore dataStore = table.getDataStore();
        TParm parm = dataStore.getRowParm(row);
        setReturnValue(parm);
    }
    /**
     * 更新本地
     */
    public void onResetDW()
    {
        TIOM_Database.removeLocalTable("SYS_FEE",false);
        table.retrieve();
    }
    /**
     * 重新下载全部
     */
    public void onResetFile()
    {
        TIOM_Database.removeLocalTable("SYS_FEE",true);
        table.retrieve();
    }
    public static void main(String args[])
    {
        com.javahis.util.JavaHisDebug.initClient();
//        //TIOM_Database.logTableAction("SYS_FEE");
//        com.dongyang.util.DebugUsingTime.start();
//        System.out.println("");
//        TDataStore ds = TIOM_Database.getLocalTable("SYS_FEE:HOSP_AREA,ORDER_CODE,START_DATE,END_DATE,ORDER_DESC:01");
//        System.out.println(ds.rowCount());
//        com.dongyang.util.DebugUsingTime.add("END");
        TIOM_Database.logTableAction("SYS_FEE");
    }
}
