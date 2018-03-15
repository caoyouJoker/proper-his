package com.javahis.ui.ins;

import java.awt.event.KeyEvent;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
/**
*
* <p>Title: SYS Fee 下拉选择框</p>
*
* <p>Description: 医保三目字典单档使用</p>
*
* <p>Copyright: Copyright (c) 2011</p>
*
* <p>Company: ProperSoft</p>
*
* @author pangb 2011-12-10
* @version  2.0
*/
public class INSFeePopupControl extends TControl {
	private TTable table;
	private String oldText = "";
    private int page = 0;
    private int index = 0;
  //  private String sql="SELECT * FROM INS_RULE ";

	  /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        table = (TTable) callFunction("UI|TABLE|getThis");
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
        Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        TParm parm = (TParm) obj;
        String text = parm.getValue("TEXT");
        setEditText(text);
    }
    /**
     * 设置输入文字
     * @param s String
     */
    public void setEditText(String s) {
        page = 0;
        index = 0;
        setValue("L_PAGE","" + (page + 1));
        callFunction("UI|EDIT|setText", s);
        int x = s.length();
        callFunction("UI|EDIT|select", x, x);
        onKeyReleased(s);
    }
    /**
     * 按键事件
     * @param s String
     */
    public void onKeyReleased(String s) {
        page = 0;
        index = 0;
        setValue("L_PAGE","" + (page + 1));
        s = s.toUpperCase();
        if (oldText.equals(s))
            return;
        oldText = s;
        table.filterObject(this, "filter");
        int count = table.getRowCount();
        if (count > 0)
            table.setSelectedRow(0);
    }

    /**
     * 过滤方法
     * @param parm TParm
     * @param row int
     * @return boolean
     */
    public boolean filter(TParm parm, int row) {
        boolean result =
        	//医保项目编码
            (parm.getValue("XMBM", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             //医保项目名称
             parm.getValue("XMMC", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             //医保项目类型
             parm.getValue("XMLB", row).toUpperCase().indexOf(oldText) !=
             -1 ||
             //医保拼音
             parm.getValue("XMRJ", row).toUpperCase().indexOf(oldText) != -1 );
        if(result)
        {
            index++;
            if(index < (page) * 19 || index > (page + 1) * 19)
                return false;
        }
        return result;
    }

    /**
     * 按键事件
     * @param e KeyEvent
     */
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            callFunction("UI|setVisible", false);
            return;
        }
        int count = table.getRowCount();
        if (count <= 0)
            return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                int row = table.getSelectedRow() - 1;
                if (row < 0)
                    row = 0;
                table.getTable().grabFocus();
                table.setSelectedRow(row);
                break;
            case KeyEvent.VK_DOWN:
                row = table.getSelectedRow() + 1;
                if (row >= count)
                    row = count - 1;
                table.getTable().grabFocus();
                table.setSelectedRow(row);
                break;
            case KeyEvent.VK_ENTER:
                callFunction("UI|setVisible", false);
                onSelected();
                break;
        }
    }

    /**
     * 行双击事件
     * @param row int
     */
    public void onDoubleClicked(int row) {
        if (row < 0)
            return;
        
        callFunction("UI|setVisible", false);
        onSelected();
    }

    /**
     * 选中
     */
    public void onSelected() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        TDataStore dataStore = table.getDataStore();
        String orderCode = dataStore.getItemString(row,"XMBM");
//        TParm parm = new TParm(TJDODBTool.getInstance().select(sql+
//            " AND A.ORDER_CODE='" + orderCode +
//            "' AND A.ACTIVE_FLG = 'Y' "));
//        System.out.println("PARM:::"+sql+
//                " AND A.ORDER_CODE='" + orderCode +
//        "' AND A.ACTIVE_FLG = 'Y' ");
//        if (parm.getErrCode() < 0 || parm.getCount() <= 0)
//            return;
       // parm = parm.getRow(0);
        TParm parm =new TParm();
        parm.setData("ORDER_CODE",orderCode);
        setReturnValue(parm);
    }

    /**
     * 更新本地
     */
    public void onResetDW() {
        TIOM_Database.removeLocalTable("INS_RULE", false);
        table.retrieve();
    }

    /**
     * 重新下载全部
     */
    public void onResetFile() {
        TIOM_Database.removeLocalTable("INS_RULE", true);
        table.retrieve();
    }
    public void onUp()
    {
        page--;
        index = 0;
        if(page < 0)
        {
            page = 0;
            return;
        }
        setValue("L_PAGE","" + (page + 1));
        table.filterObject(this, "filter");
        int count = table.getRowCount();
        if (count > 0)
            table.setSelectedRow(0);
    }
    public void onDown()
    {
        page ++;
        index = 0;
        setValue("L_PAGE","" + (page + 1));
        table.filterObject(this, "filter");
        int count = table.getRowCount();
        if (count > 0)
            table.setSelectedRow(0);
    }
}
