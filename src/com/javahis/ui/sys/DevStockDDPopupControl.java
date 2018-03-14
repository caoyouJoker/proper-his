package com.javahis.ui.sys;

import java.awt.event.KeyEvent;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;

/**
 * <p>Title: 请领（出库）设备选框</p>
 *
 * <p>Description: 请领（出库）设备选框</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>  
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DevStockDDPopupControl extends TControl {
    private String oldText = "";
    private TTable table;
    private TParm dataParm;
    /**
     * 查询SQL
     */
    private String SQL="SELECT "+
        " CASE WHEN E.BDEPTCODE IS NULL THEN E.DEPT_CODE ELSE E.BDEPTCODE END AS DEPT_CODE,"+
        " CASE WHEN E.BDEVCODE IS NULL THEN E.DEV_CODE ELSE E.BDEVCODE END AS DEV_CODE,"+
        " CASE WHEN E.BBATCHSEQ IS NULL THEN E.BATCH_SEQ ELSE E.BBATCHSEQ END AS BATCH_SEQ,"+
        " CASE WHEN E.BQTY IS NULL THEN E.QTY ELSE E.BQTY END AS QTY,"+
        " CASE WHEN E.BUNITPRICE IS NULL THEN E.UNIT_PRICE ELSE E.BUNITPRICE END AS UNIT_PRICE,"+
        " CASE WHEN E.BMANDATE IS NULL THEN E.MAN_DATE ELSE E.BMANDATE END AS MAN_DATE,"+
        " CASE WHEN E.BSCRAPVALUE IS NULL THEN E.SCRAP_VALUE ELSE E.BSCRAPVALUE END AS SCRAP_VALUE,"+
        " CASE WHEN E.BGUAREPDATE IS NULL THEN E.GUAREP_DATE ELSE E.BGUAREPDATE END AS GUAREP_DATE,"+
        " CASE WHEN E.BDEPDATE IS NULL THEN E.DEP_DATE ELSE E.BDEPDATE END AS DEP_DATE,"+
        " CASE WHEN E.BCAREUSER IS NULL THEN E.CARE_USER ELSE E.BCAREUSER END AS CARE_USER,"+
        " CASE WHEN E.BUSEUSER IS NULL THEN E.USE_USER ELSE E.BUSEUSER END AS USE_USER,"+
        " NVL(E.DEVSEQ_NO,0) AS DEVSEQ_NO,"+
        " E.SETDEV_CODE,E.MANSEQ_NO,E.LOC_CODE,E.INWAREHOUSE_DATE,"+
        " C.DEVKIND_CODE,C.DEVTYPE_CODE,C.DEVPRO_CODE,"+
        " C.DEV_CHN_DESC,C.DEV_ENG_DESC,C.DEV_ABS_DESC,"+
        " C.DESCRIPTION,C.UNIT_CODE,C.MAN_CODE,"+
        " C.MAN_NATION,C.BUYWAY_CODE,C.SEQMAN_FLG,"+
        " C.MEASURE_FLG,C.USE_DEADLINE,C.BENEFIT_FLG,"+
        " C.PY1,C.PY2,C.SEQ,C.SPECIFICATION,C.DEPR_METHOD,"+
        " C.MEASURE_FREQ,C.DEV_CLASS,E.RFID"+
        " FROM (SELECT A.DEPT_CODE,B.DEPT_CODE AS BDEPTCODE,A.DEV_CODE,B.DEV_CODE AS BDEVCODE,A.BATCH_SEQ,"+
        " B.BATCH_SEQ AS BBATCHSEQ,A.QTY,B.QTY AS BQTY,"+
        " A.UNIT_PRICE,B.UNIT_PRICE AS BUNITPRICE,A.MAN_DATE,B.MAN_DATE AS BMANDATE,A.SCRAP_VALUE,B.SCRAP_VALUE AS BSCRAPVALUE,"+
        " A.GUAREP_DATE,B.GUAREP_DATE AS BGUAREPDATE,A.DEP_DATE,B.DEP_DATE AS BDEPDATE,A.CARE_USER,B.CARE_USER AS BCAREUSER,"+
        " A.USE_USER,B.USE_USER AS BUSEUSER,B.SETDEV_CODE,B.MANSEQ_NO,B.LOC_CODE,A.INWAREHOUSE_DATE,B.DEVSEQ_NO,B.RFID"+
        " FROM DEV_STOCKM A LEFT JOIN DEV_STOCKD B ON A.DEPT_CODE=B.DEPT_CODE AND A.DEV_CODE=B.DEV_CODE AND A.BATCH_SEQ=B.BATCH_SEQ) E"+
        " LEFT JOIN DEV_BASE C ON E.DEV_CODE=C.DEV_CODE ";  
    //DEV_STOCKM A
    //DEV_STOCKD B  
    //DEV_BASE C
    //DEV_STOCKM A和DEV_STOCKD B关联为E 
    /**
     * 启用注记
     */
    private String activeFlg;
    /**
     * 设备种类
     */
    private String devKindCode;
    /**
     * 设备类别
     */
    private String devTypeCode;
    /**
     * 设备属性
     */
    private String devProCode;
    /**
     * 购入途径
     */
    private String buyWayCode;
    /**
     * 科室
     */
    private String deptCode;
    /**
     * 前台传入数据
     */
    private TParm tableData;
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
        //初始化数据
        onResetDW();
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
        String sql = " WHERE ";
        int andCount = 0;
        //启用注记
        activeFlg = parm.getValue("ACTIVE_FLG");
        if(activeFlg.length()!=0){ 
            sql+="C.ACTIVE_FLG='"+activeFlg+"'";
            andCount++;
        }
        //设备种类
        devKindCode = parm.getValue("DEVKIND_CODE");
        if(devKindCode.length()!=0){
            if(andCount>0){
                sql+= " AND ";
            }
            sql+="C.DEVKIND_CODE='"+devKindCode+"'";
            andCount++;
        }
        //设备类别
        devTypeCode = parm.getValue("DEVTYPE_CODE");
        if(devTypeCode.length()!=0){
            if(andCount>0){
                sql+= " AND ";
            }
            sql+="C.DEVTYPE_CODE='"+devTypeCode+"'";
            andCount++;
        }
        //设备属性
        devProCode = parm.getValue("DEVPRO_CODE");
        if(devProCode.length()!=0){
            if(andCount>0){
                sql+= " AND ";
            }
            sql+="C.DEVPRO_CODE='"+devProCode+"'";
            andCount++;
        }
        //购入途径
        buyWayCode = parm.getValue("BUYWAY_CODE");
        if(buyWayCode.length()!=0){
            if(andCount>0){
                sql+= " AND ";
            }
            sql+="C.BUYWAY_CODE='"+buyWayCode+"'";
            andCount++;
        }
        //科室
        deptCode = parm.getValue("DEPT_CODE");
        if(deptCode.length()!=0){
            if(andCount>0){
                sql+= " AND ";
            }
            sql+="E.DEPT_CODE='"+deptCode+"'";
            andCount++;
        }
        if(parm.getData("TABLEDATA")!=null){
            tableData = (TParm)parm.getData("TABLEDATA");
        }else{
            tableData = new TParm();
        }
        this.SQL+=sql;
        System.out.println(this.SQL);
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
            parm.getValue("DEV_CHN_DESC").toUpperCase().indexOf(oldText) > 0 ||
            parm.getValue("DEV_ENG_DESC").toUpperCase().startsWith(oldText) ||
            parm.getValue("DEV_ABS_DESC").toUpperCase().startsWith(oldText) ||
            parm.getValue("PY1").toUpperCase().startsWith(oldText) ||
            parm.getValue("PY2").toUpperCase().startsWith(oldText);
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
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    /**
     * 更新本地
     */
    public void onResetDW() {
//        System.out.println("查询SQL："+SQL);
        dataParm = new TParm(this.getDBTool().select(SQL));
        //过滤数据
        if(tableData!=null){
            int rowCount = tableData.getCount("DEV_CODE");
//            System.out.println("行数:" + rowCount);
            for (int i = 0; i < rowCount; i++) {
                TParm temp = tableData.getRow(i);
                int rowMainCount = dataParm.getCount("DEV_CODE");
                for (int j = rowMainCount-1; j >= 0; j--) {
//                    System.out.println(rowMainCount + " -----  " + j);
                    TParm tempMain = dataParm.getRow(j);
                    if (temp.getValue("DEV_CODE").equals(tempMain.getValue(
                        "DEV_CODE")) &&
                        temp.getValue("BATCH_SEQ").equals(
                        tempMain.getValue("BATCH_SEQ")) &&  
                        temp.getValue(
                        "DEVSEQ_NO").equals(tempMain.getValue("DEVSEQ_NO"))) {
                        dataParm.removeRow(j);
                    }
                }
            }
        }
        table.setParmValue(dataParm);
    }

    /**
     * 重新下载全部
     */
    public void onResetFile() {
        TParm parm = new TParm(this.getDBTool().select(SQL));
        table.setParmValue(parm);
    }

}
