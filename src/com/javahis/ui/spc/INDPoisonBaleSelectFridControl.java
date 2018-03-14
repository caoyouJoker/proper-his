package com.javahis.ui.spc;

import java.awt.event.KeyEvent;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import jdo.sys.Operator;

/**
 *
 * <p>Title: SYS Fee 下拉选择毒麻容器框</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author liyh 20121011
 * @version 1.0
 */
public class INDPoisonBaleSelectFridControl
    extends TControl {
    private String oldText = "";
    private TTable table;
   
    private int page = 0;
    private int index = 0;
    /**
     * 初始化
     */
    public void onInit() {
        table =  this.getTable("TABLE");
/*        //,ORDER_CODE ASC
        table.setSort("SEQ DESC,ORDER_CODE ASC");
        table.sort();
        table.filter();*/
        /**String sql = table.getSQL().replace("ORDER BY SEQ DESC","ORDER BY ORDER_CODE ASC");
        table.setSQL(sql);
        table.filter();**/
        callFunction("UI|EDIT|addEventListener", TTextFieldEvent.KEY_RELEASED, this,
                     "onKeyReleased");
        callFunction("UI|EDIT|addEventListener",
                     "EDIT->" + TKeyListener.KEY_PRESSED, this, "onKeyPressed");
        table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                               "onDoubleClicked");
        initParamenter();
    }

    /**
     * 初始化参数
     */
    public void initParamenter() {
        TParm tParm = new TParm();
        tParm.setData("ORG_NAME", 0, "住院药房");
        tParm.setData("FRID_ID", 0, "1");
        tParm.setData("FRID_CODE", 0, "A0001");
        tParm.setData("FRID_DESC", 0, "容器1");
        tParm.setData("ALL_COUNT", 0, "0");
        tParm.setData("CURRENT_COUNT", 0, "10");
        
        tParm.setData("ORG_NAME", 1, "住院药房");
        tParm.setData("FRID_ID", 1, "2");
        tParm.setData("FRID_CODE", 1, "A0002");
        tParm.setData("FRID_DESC", 1, "容器2");
        tParm.setData("ALL_COUNT", 1, "0");
        tParm.setData("CURRENT_COUNT", 1, "10");

        tParm.setData("ORG_NAME", 2, "住院药房");
        tParm.setData("FRID_ID", 2, "3");
        tParm.setData("FRID_CODE", 2, "A0003");
        tParm.setData("FRID_DESC", 2, "容器3");
        tParm.setData("ALL_COUNT", 2, "0");
        tParm.setData("CURRENT_COUNT", 2, "10");
        
        table.setParmValue(tParm);
        
    }

  


    /**
     * 按键事件
     * @param s String
     */
    public void onKeyReleased(String s) {
        int count = table.getRowCount();
        if (count > 0)
            table.setSelectedRow(0);
    }


  

    /**
     * 行双击事件
     * @param row int
     */
    public void onDoubleClicked(int row) {
        if (row < 0)
            return;
        TParm selectParm = table.getParmValue();
        String fridDesc = selectParm.getValue("FRID_DESC", row);
        String fridCode =selectParm.getValue("FRID_CODE", row);
       System.out.println("-------------fridCode="+fridCode+","+fridDesc);
        TParm parm = new TParm();
        parm.setData("FRID_CODE", fridCode);
        parm.setData("FRID_DESC", fridDesc);
        setReturnValue(parm);
        this.closeWindow();
    }

    /**
     * 选中
     */
    public void onSelected() {
        int row = table.getSelectedRow();
        TParm selectParm = table.getParmValue();
        if (row < 0)
            return;
        String fridDesc = selectParm.getValue("FRID_DESC", row);
        String fridCode =selectParm.getValue("FRID_CODE", row);
        TParm parm = new TParm();
        parm.setData("FRID_CODE", fridCode);
        parm.setData("FRID_DESC", fridDesc);
        setReturnValue(parm);
    }


   

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
}
