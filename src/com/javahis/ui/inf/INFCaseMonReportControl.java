package com.javahis.ui.inf;

import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import jdo.inf.INFReportTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;

/**
 * <p>Title: 感染病历月报表</p>
 *
 * <p>Description: 感染病历月报表</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFCaseMonReportControl  extends TControl {

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
    public void initUI(){
        setValue("DATE",SystemTool.getInstance().getDate());
    }

    /**
     * 查询方法
     */
    public void onQuery(){
        if(getValueString("DATE").length() == 0)
            return;
        TParm parm = new TParm();
        parm.setData("DATE", getValueString("DATE").substring(0, 7).replace("-", ""));
        //===========pangben modify 20110629 start
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            parm.setData("REGION_CODE", Operator.getRegion());
        }
        //===========pangben modify 20110629 stop
        parm = INFReportTool.getInstance().countInfCaseMonReport(parm);
        getTable("TABLE").removeRowAll();
        if(parm.getParm("INF_DEPT_COUNT").getCount("DEPT_CHN_DESC")<=0){
            messageBox("查无资料");
            return;
        }
        getTable("TABLE").setParmValue(parm.getParm("INF_DEPT_COUNT"));
    }
    /**
     * 打印事件
     */
    public void onPrint(){
        if(getValueString("DATE").length() == 0)
            return;
        TParm parm = new TParm();
        parm.setData("DATE",getValueString("DATE").substring(0,7).replace("-",""));
        //===========pangben modify 20110629 start
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            parm.setData("REGION_CODE", Operator.getRegion());
        }
        //===========pangben modify 20110629 stop
        parm = INFReportTool.getInstance().countInfCaseMonReport(parm);
        getTable("TABLE").removeRowAll();
        if(parm.getParm("INF_DEPT_COUNT").getCount("DEPT_CHN_DESC")<=0){
            messageBox("没有需要打印的数据");
            return;
        }
        getTable("TABLE").setParmValue(parm.getParm("INF_DEPT_COUNT"));
        openPrintWindow("%ROOT%\\config\\prt\\inf\\CountInfCaseMonReport.jhw",parm);
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
       ExportExcelUtil.getInstance().exportExcel(mainTable, "感染病历月报表");
   }

    /**
     * 清空方法
     */
    public void  onClear(){
        setValue("DATE","");
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
