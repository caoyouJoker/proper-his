package com.javahis.ui.inf;

import com.dongyang.control.TControl;
import jdo.inf.INFReportTool;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;

/**
 * <p>Title: 医院感染病例检测汇总表1</p>
 *
 * <p>Description: 医院感染病例检测汇总表1</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFCaseForReportControl extends TControl {

    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        initUI();
    }


    /**
     * 初始化方法
     */
    private TParm parm;
    public void initUI(){
        setValue("INF_DATE",SystemTool.getInstance().getDate());
    }

    /**
     * 查询方法
     */
    public void onQuery(){
        if(getValueString("INF_DATE").length() == 0)
            return;
        parm = new TParm();
        parm.setData("INF_DATE",getValueString("INF_DATE").replace("-","").substring(0,6));
        //=============pangben modify 20110629 start
        if(null!=Operator.getRegion() && Operator.getRegion().length()>0){
            parm.setData("REGION_CODE",Operator.getRegion());
        }
        //=============pangben modify 20110629 stop
        parm = INFReportTool.getInstance().selestInfCaseForReport(parm);
        getTable("TABLE").removeRowAll();
        if(parm.getParm("TABLE").getCount("DEPT_DESC")<=0){
            messageBox("查无资料");
            return;
        }
        getTable("TABLE").setParmValue(parm.getParm("TABLE"));
    }

    /**
     * 打印事件
     */
    public void onPrint(){
        if(getValueString("INF_DATE").length() == 0)
            return;
//        parm.setData("INF_DATE",getValueString("INF_DATE").replace("-","").substring(0,6));
//        parm = INFReportTool.getInstance().selestInfCaseForReport(parm);
//        getTable("TABLE").removeRowAll();
        if(parm.getParm("TABLE").getCount("DEPT_DESC")<=0){
            messageBox("没有需要打印的数据");
            return;
        }
        getTable("TABLE").setParmValue(parm.getParm("TABLE"));
        openPrintWindow("%ROOT%\\config\\prt\\inf\\INFCaseForReport.jhw",parm);
    }

    /**
     * 导出Excel表格
     */
    public void onExcel(){
       TTable mainTable = getTable("TABLE");
       if(mainTable.getRowCount() <= 0){
           messageBox("无导出资料");
           return;
       }
       ExportExcelUtil.getInstance().exportExcel(mainTable, "医院感染病例检测汇总表1");
   }

    /**
     * 清空方法
     */
    public void  onClear(){
        setValue("INF_DATE","");
        getTable("TABLE").removeRowAll();
    }
    /**
     * 取得Table控件
     * @param tableTag String
     * @return TTable
     */
    private TTable getTable(String tableTag){
        return ((TTable)getComponent(tableTag));
    }
}
