package com.javahis.ui.mro;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.bil.BILComparator;
import jdo.mro.MRORT001TypeTool;
import com.dongyang.ui.TTable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.dongyang.ui.TComboBox;
import com.javahis.util.ExportExcelUtil;

import jdo.sys.SYSRegionTool;

/**
 * <p>Title: 问题类型统计表</p>
 *
 * <p>Description: 问题类型统计表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangk 2009.09.13
 * @version 4.0
 */
public class MRORT001TypeControl
    extends TControl {
	
    private String DATE_S="";
    private String DATE_E="";
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
            parm.setData("DEPT",this.getValue("DEPT"));
        }
        if(null!=this.getValueString("REGION_CODE")&&this.getValueString("REGION_CODE").length()>0)
             parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        TParm result = MRORT001TypeTool.getInstance().selectParm(parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");//执行失败
            return;
        }
        if(result.getCount()<=0){
            this.messageBox("没有要查询的数据");
        }else{
            int sumCount = 0; //统计总数量
            for (int i = 0; i < result.getCount(); i++) {
                sumCount += result.getInt("EXAMINE_COUNT", i);
            }
            result.setData("REGION_CODE", result.getCount(), "");
            result.setData("DEPT_CODE", result.getCount(), "");
            result.setData("STATION_CODE", result.getCount(), "");
            result.setData("TYPE_CODE", result.getCount(), "");
            result.setData("EXAMINE_CODE", result.getCount(), "总计");
            result.setData("EXAMINE_COUNT", result.getCount(), sumCount);
            result.setCount(result.getCount() + 1);
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
        data.addData("SYSTEM", "COLUMNS", "TYPE_CODE");
        data.addData("SYSTEM", "COLUMNS", "EXAMINE_CODE");
        data.addData("SYSTEM", "COLUMNS", "EXAMINE_COUNT");
        data.setCount(data.getCount()+1);
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
        printData.setData("basic", basic.getData());
        
        String region = data.getValue("REGION_CODE",0);
        printData.setData("TITLE", "TEXT",
                          (this.getValue("REGION_CODE") != null &&
                           !this.getValue("REGION_CODE").equals("") ? region :
                           "所有医院") + "问题类型统计表");//添加表头

        //制表时间
        printData.setData("printDate","TEXT",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy年MM月dd日"));
        this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MRORT001_Type.jhw",
                             printData);
    }
    
	/**
	 * 汇出Excel
	 */
	public void onExport() {//add by wanglong 20121108
		TTable table = (TTable) callFunction("UI|Table|getThis");
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(table, "问题类型统计表");
	}
	
    /**
     * 科室选择事件
     */
    public void onDEPT(){
        this.clearValue("STATION");
        this.callFunction("UI|STATION|onQuery");
    }
    
    /**
     * 清空
     */
    public void onClear(){
        this.clearValue("STATION;DEPT");
        //comboInit();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
        this.setValue("REGION_CODE",Operator.getRegion());
    }
    
    // ====================排序功能begin======================//add by wanglong 20130909
    /**
     * 加入表格排序监听方法
     * 
     * @param table
     */
    public void addListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                // 调用排序方法;
                // 转换出用户想排序的列和底层数据的列，然后判断
                if (j == sortColumn) {
                    ascending = !ascending;
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                TParm tableData = table.getParmValue();
                TParm totAmtRow = tableData.getRow(tableData.getCount() - 1);// add by wanglong 20130108
                tableData.removeRow(tableData.getCount() - 1);// add by wanglong 20130108
                tableData.removeGroupData("SYSTEM");
                String columnName[] = tableData.getNames("Data");
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                String tblColumnName = table.getParmMap(sortColumn);
                int col = tranParmColIndex(columnName, tblColumnName);
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // 将排序后的vector转成parm;
                TParm lastResultParm = new TParm();// 记录最终结果
                lastResultParm = cloneVectoryParam(vct, new TParm(), strNames);// 加入中间数据
                for (int k = 0; k < columnName.length; k++) {// add by wanglong 20130108
                    lastResultParm.addData(columnName[k], totAmtRow.getData(columnName[k]));
                }
                lastResultParm.setCount(lastResultParm.getCount(columnName[0]));// add by wanglong 20130108
                table.setParmValue(lastResultParm);
            }
        });
    }

    /**
     * 列名转列索引值
     * 
     * @param columnName
     * @param tblColumnName
     * @return
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                // System.out.println("tmp相等");
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * 得到 Vector 值
     * 
     * @param group
     *            String 组名
     * @param names
     *            String "ID;NAME"
     * @param size
     *            int 最大行数
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size) count = size;
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
     * vectory转成param
     */
    private TParm cloneVectoryParam(Vector vectorTable, TParm parmTable, String columnNames) {
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        // 行数据;
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        return parmTable;
    }
    // ====================排序功能end======================
}
