package com.javahis.ui.erd;

import java.sql.Timestamp;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTextField;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TDS;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import com.javahis.util.JavaHisDebug;
import jdo.sys.Operator;
import jdo.sys.SYSHzpyTool;

/**
 * <p>Title: 急诊留观床设定 </p>
 *
 * <p>Description: 急诊留观床设定 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH
 * @version 1.0
 */
public class ERDBedMaitainControl extends TControl {

    TTextFormat REGION;
    TTextField BED_NO;
    TTextField BED_DESC;
    TTextField PY;
    TTextField DES;
    TTable TABLE;

    TDS tableParm = new TDS();

    /**
     * 初始化
     */
    public void onInit() { //初始化程序
        super.onInit();
        myInitControler();
    }


    /**
     * 首先得到所有UI的控件对象/注册相应的事件
     * 设置
     */
    public void myInitControler() {
        REGION = (TTextFormat)this.getComponent("REGION");

        BED_NO = (TTextField)this.getComponent("BED_NO");
        BED_DESC = (TTextField)this.getComponent("BED_DESC");
        PY = (TTextField)this.getComponent("PY");
        DES = (TTextField)this.getComponent("DES");
        TABLE = (TTable)this.getComponent("Table");

        //给上下table注册单击事件监听
        this.callFunction("UI|Table|addEventListener",
                          "Table->" + TTableEvent.CLICKED, this,
                          "onTableClicked");
        onQuery();
    }

    /**
     * 单击上面的table事件
     */
    public void onTableClicked(int row) {
        TParm parm=tableParm.getRowParm(row);
        REGION.setValue(parm.getValue("ERD_REGION_CODE"));
        BED_NO.setValue(parm.getValue("BED_NO"));
        BED_DESC.setValue(parm.getValue("BED_DESC"));
        PY.setValue(parm.getValue("PY1"));
        DES.setValue(parm.getValue("DESCRIPTION"));
    }

    public void onQuery() {

        //拿到查询SQL语句
        String sql = getSQL();
        tableParm.setSQL(sql);
        tableParm.retrieve();
        tableParm.showDebug();
        TABLE.setDataStore(tableParm);
        TABLE.setDSValue();

    }


    private boolean onSaveCheck(){
        if(getValueString("REGION").length() == 0){
            messageBox("急诊诊区不可为空");
            return false;
        }
        if(getValueString("BED_NO").length() == 0){
            messageBox("床号不可为空");
            return false;
        }
        String SQL  = " SELECT BED_DESC FROM ERD_BED "+
                      " WHERE ERD_REGION_CODE='"+getValueString("REGION")+"'"+
                      " AND   BED_NO = '"+getValueString("BED_NO")+"'";
        TParm result = new TParm(TJDODBTool.getInstance().select(SQL));
        if(result.getCount() > 0){
            messageBox("此病床号已经存在");
            return false;
        }
        return true;
    }

    public void onSave() {
        if(!onSaveCheck())
            return;
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        int newRow = tableParm.insertRow();
        tableParm.setItem(newRow, 0, REGION.getValue());
        tableParm.setItem(newRow, 1, BED_NO.getValue());
        tableParm.setItem(newRow, 2, BED_DESC.getValue());
        tableParm.setItem(newRow, 3, PY.getValue());
        tableParm.setItem(newRow, 4, DES.getValue());
        tableParm.setItem(newRow, 5, Operator.getID());
        tableParm.setItem(newRow, 6, date);
        tableParm.setItem(newRow, 7, Operator.getIP());

        if (!tableParm.update()) {
            messageBox("E0001");
            return;
        }
        messageBox("P0001");

        onQuery();
    }

    public void onDelete() {
        int delrow = TABLE.getSelectedRow();
        tableParm.deleteRow(delrow);
        if (!tableParm.update()) {
            messageBox("删除失败");
            return;
        }
        messageBox("删除成功");

        onQuery();

    }

    public void onClear() {
        REGION.setValue("");

        BED_NO.setValue("");
        BED_DESC.setValue("");
        PY.setValue("");
        DES.setValue("");

//        onQuery();
        TParm resultParm = new TParm();
        TABLE.setParmValue(resultParm);
    }

    /**
     * 拿到查询SQL
     * @return String
     */
    private String getSQL() {

        String sql = "";
        String where = "";
        String region = (String) REGION.getValue();
        if (region != null && region.length() != 0)
            where = " WHERE ERD_REGION_CODE='" + region + "'";

        sql = " SELECT ERD_REGION_CODE,BED_NO,BED_DESC,PY1,DESCRIPTION,OPT_USER,OPT_DATE,OPT_TERM  FROM ERD_BED " +
            where +
            " ORDER BY ERD_REGION_CODE";

        return sql;
    }

    public void onPY(){
        String bedDesc = BED_DESC.getText();
        String bedPy = SYSHzpyTool.getInstance().charToCode(bedDesc);
        PY.setText(bedPy);
        DES.grabFocus();
    }

    public static void main(String[] args) {
        JavaHisDebug.initClient();
        //JavaHisDebug.TBuilder();

//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("erd\\ERDBedMaintain.x");
    }


}


