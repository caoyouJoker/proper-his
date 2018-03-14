package com.javahis.ui.mro;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;
import jdo.sys.SystemTool;
import jdo.bil.BILComparator;
import jdo.mro.MRORT002ScoreTool;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import com.dongyang.ui.TComboBox;
import com.javahis.util.ExportExcelUtil;

import jdo.sys.SYSRegionTool;

/**
 * <p>Title: 医师分值统计表</p>
 *
 * <p>Description: 医师分值统计表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-9-13
 * @version 4.0
 */
public class MRORT002ScoreControl
    extends TControl {
	
    private String DATE_S="";//统计起始时间
    private String DATE_E="";//统计截止时间 
    private BILComparator compare = new BILComparator();//modify by wanglong 20130909
    private boolean ascending = false;
    private int sortColumn =-1;
    /**
     * 初始化
     */
    public void onInit(){
        super.onInit();
        comboInit();//时间初始化
        this.setValue("REGION_CODE",Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        addListener((TTable) this.getComponent("Table"));
    }
    
    /**
     * 时间初始化
     */
    private void comboInit(){
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("DATE_S",StringTool.rollDate(date,-7));
        this.setValue("DATE_E",date);
    }
    
    /**
     * 查询
     */
    public void onQuery(){
        DATE_S = this.getText("DATE_S");
        DATE_E = this.getText("DATE_E");
        if(DATE_S.trim().length()<=0){
            this.messageBox_("请填写查询起日");
            callFunction("UI|DATE_S|grabFocus");//获取焦点
            return;
        }
        if(DATE_E.trim().length()<=0){
            this.messageBox_("请填写查询讫日");
            callFunction("UI|DATE_E|grabFocus");//获取焦点
            return;
        }
        TParm parm = new TParm();
        parm.setData("DATE_S",DATE_S.replace("/",""));
        parm.setData("DATE_E",DATE_E.replace("/",""));
        String station = this.getValueString("STATION");
        if(station.length()>0){
            parm.setData("STATION",station);
        }
        if(this.getValueString("DEPT").length()>0){
            parm.setData("DEPT",this.getValueString("DEPT"));
        }
        String vs_code = this.getValueString("VS_CODE");
        if(vs_code.length()>0){
            parm.setData("VS_CODE",vs_code);
        }
        if(null!=this.getValueString("REGION_CODE")&&this.getValueString("REGION_CODE").length()>0)
             parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        TParm result = new TParm();
        if("Y".equals(this.getValueString("OUT"))){
            result = MRORT002ScoreTool.getInstance().selectOUT(parm);
        }else if("Y".equals(this.getValueString("IN"))){
            result = MRORT002ScoreTool.getInstance().selectIN(parm);
        }
        if(result.getErrCode()<0){
            this.messageBox("E0005");//执行失败
            return;
        }
        if (result.getCount() <= 0) {
            this.messageBox("没有要查询的数据");
        }

        TTable table = (TTable)this.getComponent("Table");
        table.setParmValue(result);
    }
    
    /**
     * 打印
     */
    public void onPrint(){
        TTable table = (TTable)this.getComponent("Table");
        TParm data = table.getShowParmValue();
        if(null==data||data.getCount()<=0){
            this.messageBox("没有需要打印的数据");
            return;
        }
        data.addData("SYSTEM", "COLUMNS", "REGION_CODE");
        data.addData("SYSTEM", "COLUMNS", "DEPT_CODE");
        data.addData("SYSTEM", "COLUMNS", "STATION_CODE");
        data.addData("SYSTEM", "COLUMNS", "DR_CODE");
        data.addData("SYSTEM", "COLUMNS", "MR_NO");
        data.addData("SYSTEM", "COLUMNS", "IPD_NO");
        data.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        data.addData("SYSTEM", "COLUMNS", "SCORE");
        TParm printData = new TParm();
        printData.setData("T1", data.getData());//打印数据
        //起止日期
        printData.setData("date", "TEXT",
                          DATE_S.substring(0, 4) + "年" +
                          DATE_S.subSequence(5, 7) + "月" +
                          DATE_S.substring(8, 10) + "日 到 " +
                          DATE_E.substring(0, 4) + "年" +
                          DATE_E.subSequence(5, 7) + "月" +
                          DATE_E.substring(8, 10) + "日");
        TParm basic = new TParm();
        basic.addData("PrintUser",Operator.getName());//制表人
        printData.setData("basic",basic.getData());
       
        String region = data.getValue("REGION_CODE",0);
        printData.setData("TITLE", "TEXT",
                          (this.getValue("REGION_CODE") != null &&
                           !this.getValue("REGION_CODE").equals("") ? region :
                           "所有医院") + "医师分值统计表"); // 添加表头
        //制表时间
        printData.setData("printDate","TEXT",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy年MM月dd日"));
        this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MRORT002_Score.jhw",
                             printData);
    }
    
	/**
	 * 汇出Excel
	 */
	public void onExport() {//add by wanglong 20121108
		TTable table = (TTable) callFunction("UI|Table|getThis");
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(table, "医师分值统计表");
	}
	
    /**
     * 科室选择事件
     */
    public void onDEPT(){
        this.clearValue("STATION;VS_CODE");
        this.callFunction("UI|VS_CODE|onQuery");
        this.callFunction("UI|STATION|onQuery");
    }
    
    /**
     * 清空
     */
    public void onClear(){
        this.clearValue("STATION;DEPT;VS_CODE");
        //comboInit();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
        this.setValue("OUT",true);
        this.setValue("REGION_CODE",Operator.getRegion());
    }
    
    // ====================排序功能begin======================//add by wanglong 20130909
    /**
     * 加入表格排序监听方法
     * @param table
     */
    public void addListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                if (j == sortColumn) {
                    ascending = !ascending;// 点击相同列，翻转排序
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                TParm tableData = table.getParmValue();// 取得表单中的数据
                String columnName[] = tableData.getNames("Data");// 获得列名
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
                int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // 将排序后的vector转成parm;
                cloneVectoryParam(vct, new TParm(), strNames, table);
            }
        });
    }

    /**
     * 根据列名数据，将TParm转为Vector
     * @param parm
     * @param group
     * @param names
     * @param size
     * @return
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size)
            count = size;
        for (int i = 0; i < count; i++) {
            Vector row = new Vector();
            for (int j = 0; j < nameArray.length; j++) {
                row.add(parm.getData(group, nameArray[j], i));
            }
            data.add(row);
        }
        return data;
    }

    /**
     * 返回指定列在列名数组中的index
     * @param columnName
     * @param tblColumnName
     * @return int
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * 根据列名数据，将Vector转成Parm
     * @param vectorTable
     * @param parmTable
     * @param columnNames
     * @param table
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
            String columnNames, final TTable table) {
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        table.setParmValue(parmTable);
    }
    // ====================排序功能end======================
	
}
