package com.javahis.ui.sta;

import com.dongyang.control.*;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import java.awt.Component;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TLabel;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.data.TParm;
import java.util.Vector;
import jdo.sta.STAOut_ICDTool;
import jdo.sta.STATool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TComboBox;
import jdo.sta.STAStationLogTool;
import jdo.sys.SystemTool;
import com.javahis.util.ExportExcelUtil;
import jdo.sta.STASDListTool;
import jdo.sys.Operator;

/**
 * <p>Title: 单病种医疗质量综合统计表</p>
 *
 * <p>Description: 单病种医疗质量综合统计表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-7-3
 * @version 1.0
 */
public class STAOut_ICDControl
    extends TControl {
    String DATE_Start = "";//记录统计起始日期
    String DATE_End = "";//记录统计结束日期
    String selectDept = "";//要查询的部门
    String selectDeptDesc = "";//要查询的部门名称
    public void onInit(){
        super.init();
        //table专用的监听  ICD10
        ((TTable)this.getComponent("Table_ICD")).addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,this,"onCreateEditComponent");
//        comboboxInit();//combo初始化
        this.setValue("DATE_S",STATool.getInstance().getLastMonth());
        this.setValue("DATE_E",SystemTool.getInstance().getDate());
    }
    /**
     *病区combobox初始化
     */
//    private void comboboxInit(){
//        TParm station = STAStationLogTool.getInstance().selectSTAStation();
//        ((TComboBox)this.getComponent("DEPT_Combo")).setParmValue(station);
//    }

    /**
     *诊断弹出界面 ICD10
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComponent(Component com, int row, int column) {

        //弹出ICD10对话框的列
        if (column != 0)
            return;
        if (! (com instanceof TTextField))
            return;
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        //给table上的新text增加ICD10弹出窗口
        textfield.setPopupMenuParameter("ICD10",getConfigParm().newConfig("%ROOT%\\config\\sta\\STAICDPopup.x"));
        //给新text增加接受ICD10弹出窗口的回传值
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                   "newAgentOrder");
    }
    /**
     * 取得ICD10返回值
     * @param tag String
     * @param obj Object
     */
    public void newAgentOrder(String tag, Object obj) {
        TTable table = (TTable)this.callFunction("UI|Table_ICD|getThis");
        //sysfee返回的数据包
        TParm parm = (TParm) obj;
        String orderCode = parm.getValue("SEQ");
        table.setItem(table.getSelectedRow(), "code", orderCode);
        table.setItem(table.getSelectedRow(), "name", parm.getValue("SD_DESC"));
    }

    /**
     * 添加ICD按钮
     */
    public void onAdd(){
        TTable table = (TTable)this.getComponent("Table_ICD");
        table.addRow();
    }
    /**
     * 删除ICD
     */
    public void onRemove(){
        TTable table = (TTable)this.getComponent("Table_ICD");
        if(table.getSelectedRow()>-1)
            table.removeRow(table.getSelectedRow());
    }
    /**
     * 查询
     */
    public void onQuery(){
        String DATE_S = this.getText("DATE_S").replace("/","");
        String DATE_E = this.getText("DATE_E").replace("/","");
        if(DATE_S.trim().length()<=0||DATE_E.trim().length()<=0){
            this.messageBox_("请选择日期段");
            return;
        }
        DATE_Start = DATE_S;
        DATE_End = DATE_E;
//        DATE_S = DATE_S + "01";
        //获取月份的最后一天日期
//        DATE_E = StringTool.getString(STATool.getInstance().getLastDayOfMonth(DATE_E),"yyyyMMdd");
        String dept = this.getValueString("DEPT_Combo");//获取部门ID
        TTable table = (TTable)this.getComponent("Table_ICD");
        table.acceptText();
        TParm ICD = new TParm();
        //筛选选中的ICD
        for(int i=0;i<table.getRowCount();i++){
            if(table.getItemString(i,"name").trim().length()>0){
                ICD.addData("ICD",table.getItemString(i,"code"));
                ICD.addData("DESC",table.getItemString(i,"name"));
                TParm tp = new TParm();
                tp.setData("SEQ",table.getItemString(i,"code"));
                ICD.addData("CONDITION",STASDListTool.getInstance().selectData(tp).getValue("CONDITION",0));
            }
        }
        if(ICD.getCount("ICD")<=0){
            this.messageBox_("请选择病种");
            return;
        }
        TParm parm = new TParm();
        parm.setData("DATE_S",DATE_S);
        parm.setData("DATE_E",DATE_E);
        if(dept.length()>0){
            parm.setData("DEPTCODE",dept);
            selectDept = dept;
            selectDeptDesc = this.getText("DEPT_Combo");
        }
        parm.setData("ICD",ICD.getData());
//        System.out.println("-=------------------"+parm);
        TParm result = STAOut_ICDTool.getInstance().selectData(parm);
        if(result.getErrCode()<0){
            this.messageBox_("统计失败 "+result.getErrName()+result.getErrText());
            return;
        }
        ((TTable)this.getComponent("Table")).setParmValue(result);
    }
    /**
     * 打印
     */
    public void onPrint(){
        TParm printParm = new TParm();
        if(((TTable)this.getComponent("Table")).getRowCount()<=0){
            return;
        }
        TParm data = ((TTable)this.getComponent("Table")).getParmValue();
        data.setCount(data.getCount("ICD_DESC"));
        data.setData("SYSTEM","COLUMNS","ICD_DESC");
        data.setData("SYSTEM","COLUMNS","DATA_01");
        data.setData("SYSTEM","COLUMNS","DATA_02");
        data.setData("SYSTEM","COLUMNS","DATA_03");
        data.setData("SYSTEM","COLUMNS","DATA_04");
        data.setData("SYSTEM","COLUMNS","DATA_05");
        data.setData("SYSTEM","COLUMNS","DATA_06");
        data.setData("SYSTEM","COLUMNS","DATA_07");
        data.setData("SYSTEM","COLUMNS","DATA_08");
        data.setData("SYSTEM","COLUMNS","DATA_09");
        data.setData("SYSTEM","COLUMNS","DATA_10");
        data.setData("SYSTEM","COLUMNS","DATA_11");
        printParm.setData("T1",data.getData());
        //查询开放病床数
        TParm bedNo = STAOut_ICDTool.getInstance().selectDedNO(selectDept);
        //基本信息
        TParm Basic = new TParm(); //报表基本参数
        //设置日期格式
        String date = DATE_Start.substring(0, 4) + "年" + DATE_Start.substring(4,6) +
            "月" + DATE_Start.substring(6) + "日 ~ " + DATE_End.substring(0, 4) + "年" +
            DATE_End.substring(4,6) + "月" +DATE_End.substring(6) + "日";
        Basic.setData("DateSE", date); //数据日期
        Basic.setData("Date",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy年MM月dd日"));
        Basic.setData("Unit",Operator.getHospitalCHNFullName());//填报单位
        Basic.setData("Level", "");//医院等级
        Basic.setData("BEDNO",bedNo.getValue("NUM",0));//床位数
        if(selectDeptDesc.trim().length()>0)
            Basic.setData("DeptDesc","("+selectDeptDesc+")"); //统计的科室名称
        printParm.setData("Basic",Basic.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_OUT_ICD.jhw", printParm);
    }
    /**
    * 汇出Excel
    */
   public void onExcel() {
       //得到UI对应控件对象的方法（UI|XXTag|getThis）
       TTable table = (TTable) callFunction("UI|Table|getThis");
       ExportExcelUtil.getInstance().exportExcel(table,"单病种医疗质量综合统计表");
   }
}
