package com.javahis.ui.dev;

import java.awt.event.KeyEvent;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;



/**
 *
 * <p>Title: DEV_BASE 设备 下拉选择框</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: BlueCore 2015</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangjc
 * @version 1.0
 */
public class DEVBasePopupControl extends TControl {
	
	private String sql = "SELECT DEV_CODE,DEV_CHN_DESC,PY1 FROM DEV_BASE";
	private TTable table;
	private TParm dataParm;
	private String oldText = "";
	private TParm tableData;
    
	public void onInit(){
		super.onInit();
        table = (TTable) this.getComponent("TABLE");
        callFunction("UI|EDIT|addEventListener", TTextFieldEvent.KEY_RELEASED, this,
                     "onKeyReleased");
        callFunction("UI|EDIT|addEventListener",
                     "EDIT->" + TKeyListener.KEY_PRESSED, this, "onKeyPressed");
        table.addEventListener("TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                               "onDoubleClicked");
        this.tableData = this.table.getParmValue();
        initParamenter();
        onResetDW();
	}
	
    /**
     * 重新加载
     */
    public void onInitReset() {
        Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        TParm parm = (TParm) obj;
        String text = parm.getValue("TEXT");
        String oldText = (String) callFunction("UI|EDIT|getText");
        if (oldText.equals(text)) {
            return;
        }
        setEditText(text);
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
//        String org_code = parm.getValue("ORG_CODE");
//        String sup_code = parm.getValue("SUP_CODE");
//        if(!org_code.equals("")){
//        	sql += " AND A.ORG_CODE='"+org_code+"' ";
//        }
//        if(!sup_code.equals("")){
//        	sql += " AND A.SUP_CODE='"+sup_code+"' ";
//        }
        
    }

    /**
     * 设置输入文字
     * @param s String
     */
    public void setEditText(String s) {
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
        s = s.toUpperCase();
        if (oldText.equals(s))
            return;
        oldText = s;
        int count = dataParm.getCount("DEV_CODE");
        String names[] = dataParm.getNames();
        TParm temp = new TParm();
        for(int i=0;i<count;i++){
            TParm rowParm = dataParm.getRow(i);
            if(this.filter(rowParm)){
                for(String tempData:names){
                    temp.addData(tempData,rowParm.getData(tempData));
                }
            }
        }
        table.setParmValue(temp);
        int rowConunt = temp.getCount("DEV_CODE");
        if (rowConunt > 0)
            table.setSelectedRow(0);
    }

    /**
     * 过滤方法
     * @param parm TParm
     * @param row int
     * @return boolean
     */
    public boolean filter(TParm parm) {
        boolean falg = parm.getValue("DEV_CODE").toUpperCase().
            startsWith(oldText) ||
            parm.getValue("PY1").toUpperCase().startsWith(oldText);
        return falg;
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
        setReturnValue(table.getParmValue().getRow(row));
    }
    
    /**
     * 更新本地
     */
    public void onResetDW() {
//        System.out.println("查询SQL："+sql);
        dataParm = new TParm(TJDODBTool.getInstance().select(sql));
        //过滤数据
//        if(tableData!=null){
//            int rowCount = tableData.getCount("DEV_CODE");
////            System.out.println("行数:" + rowCount);
//            for (int i = 0; i < rowCount; i++) {
//                TParm temp = tableData.getRow(i);
//                int rowMainCount = dataParm.getCount("DEV_CODE");
//                for (int j = rowMainCount-1; j >= 0; j--) {
////                    System.out.println(rowMainCount + " -----  " + j);
//                    TParm tempMain = dataParm.getRow(j);
//                    if (temp.getValue("DEV_CODE").equals(tempMain.getValue("DEV_CODE"))) {
//                        dataParm.removeRow(j);
//                    }
//                }
//            }
//        }
//        System.out.println("--------"+dataParm);
        table.setParmValue(dataParm);
    }
    
    /**
     * 重新下载全部
     */
    public void onResetFile() {
        TParm parm = new TParm(this.getDBTool().select(sql));
        table.setParmValue(parm);
    }
    
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

}
