package com.javahis.ui.sta;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_Database;
import java.awt.event.KeyEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>Title: 诊断ICD（STA3.0专用）</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-9-23
 * @version 3.0
 */
public class STAICDPopupControl
    extends TControl {
    private String oldText = "";
    private  String icdType;
    private TTable table;

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
        String text = parm.getValue("TEXT");
        setEditText(text);
        icdType=parm.getValue("ICD_TYPE");
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
        oldText = s.toUpperCase();
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
    public boolean filter(TParm parm,int row)
    {
        boolean result=parm.getValue("SEQ",row).toUpperCase().startsWith(oldText) ||
        parm.getValue("SD_DESC",row).toUpperCase().indexOf(oldText) > 0 ||
        parm.getValue("PY",row).toUpperCase().startsWith(oldText);
        return result;
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
        int count = (Integer)callFunction("UI|TABLE|getRowCount");
        if(count <= 0)
            return;
        switch(e.getKeyCode()){
        case KeyEvent.VK_UP:
            int row = (Integer)callFunction("UI|TABLE|getSelectedRow") - 1;
            if(row < 0)
                row = 0;
            table.getTable().grabFocus();
            table.setSelectedRow(row);
//            callFunction("UI|TABLE|setSelectedRow",row);
            break;
        case KeyEvent.VK_DOWN:
            row = (Integer)callFunction("UI|TABLE|getSelectedRow") + 1;
            if(row >= count)
                row = count - 1;
            table.getTable().grabFocus();
            table.setSelectedRow(row);
//            callFunction("UI|TABLE|setSelectedRow",row);
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
        int row = (Integer)callFunction("UI|TABLE|getSelectedRow");
        if(row < 0)
            return;
        TDataStore dataStore = (TDataStore)callFunction("UI|TABLE|getDataStore");
        TParm parm = dataStore.getRowParm(row);
        setReturnValue(parm);
    }
//    /**
//     * 更新本地
//     */
//    public void onResetDW()
//    {
//        TIOM_Database.removeLocalTable("SYS_DIAGNOSIS",false);
//        table.retrieve();
//    }
//    /**
//     * 重新下载全部
//     */
//    public void onResetFile()
//    {
//        TIOM_Database.removeLocalTable("SYS_DIAGNOSIS",true);
//        table.retrieve();
//    }

}
