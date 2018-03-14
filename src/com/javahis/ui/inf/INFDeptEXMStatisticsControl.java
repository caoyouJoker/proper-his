package com.javahis.ui.inf;

import jdo.inf.INFReportTool;
import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: 科室检测统计表</p>
 *
 * <p>Description: 科室检测统计表</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class INFDeptEXMStatisticsControl  extends TControl {

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
        Timestamp timestamp = SystemTool.getInstance().getDate();
        setValue("START_DATE",timestamp);
        setValue("END_DATE",timestamp);
        setValue("YEAR",timestamp);
        setValue("DEPT_CODE",Operator.getDept());
        setValue("YEAR_MONTH",timestamp);
    }

    /**
     * 打印事件
     */
    public void onQuery(){
        if(getValue("DEPT_RESULT").equals("Y"))
            deptEXMStatisticsDate();
        if(getValue("DEPT_YEAR").equals("Y"))
            selectDeptMonCount();
        if(getValue("DEPT_MONTH").equals("Y"))
            selectYearMonEXMStatistics();
    }

    /**
     * 科室统计查询按日期
     */
    private void deptEXMStatisticsDate(){
        if(getValueString("START_DATE").length() == 0 ||
           getValueString("END_DATE").length() == 0){
            messageBox("请输入开始日期及结束日期");
            return;
        }
        if(getValueString("START_DATE").compareTo(getValueString("END_DATE"))>0){
            messageBox("输入的开始日期及结束日期有误");
            return;
        }
        TParm parm = new TParm();
        parm.setData("START_DATE",getValue("START_DATE"));
        parm.setData("END_DATE",getValue("END_DATE"));
        //=============pangben modify 20110629 start
        if(null!=Operator.getRegion() && Operator.getRegion().length()>0){
            parm.setData("REGION_CODE",Operator.getRegion());
        }
        //=============pangben modify 20110629 stop
        parm = INFReportTool.getInstance().deptEXMStatisticsDate(parm);
        getTable("TABLE").setHeader("科室,120;检测日期,100;检测项目,200;检测结果,80;总分,66;备注,200");
        getTable("TABLE").setLockColumns("0,1,2,3,4,5");
        getTable("TABLE").setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,right;5,left");
        getTable("TABLE").setParmMap("DEPT_CHN_DESC;EXAM_DATE;CHN_DESC;PASS_FLG;ITEM_GAINPOINT;REMARK");
        getTable("TABLE").setParmValue(parm);
    }

    /**
     * 取得科室年度检测信息
     */
    private void selectDeptMonCount(){
       if(getValueString("YEAR").length() == 0){
           messageBox("请输入年份");
           return;
       }
       if(getValueString("DEPT_CODE").length() == 0){
           messageBox("请输入科室");
           return;
       }
       TParm parm = new TParm();
       parm.setData("YEAR",getValueString("YEAR").substring(0,4));
       parm.setData("DEPT_CODE",getValue("DEPT_CODE"));
       //=============pangben modify 20110629 start
        if(null!=Operator.getRegion() && Operator.getRegion().length()>0){
            parm.setData("REGION_CODE",Operator.getRegion());
        }
        //=============pangben modify 20110629 stop
       parm = INFReportTool.getInstance().selectDeptMonCount(parm);
       getTable("TABLE").setHeader("月份,80;检测日期,90;总分,66;平均,80");
       getTable("TABLE").setLockColumns("0,1,2,3");
       getTable("TABLE").setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,right");
       getTable("TABLE").setParmMap("EXAM_MONTH;EXAM_DATE;ITEM_GAINPOINT;ITEM_GAINPOINT_AVERAGE");
       getTable("TABLE").setParmValue(parm);
   }

   /**
    * 取得医院检测月统计信息
    */
   private void selectYearMonEXMStatistics(){
      if(getValueString("YEAR_MONTH").length() == 0){
          messageBox("请输入年月");
          return;
      }
      TParm parm = new TParm();
      parm.setData("YEAR_MONTH",getValueString("YEAR_MONTH").replace("-","").substring(0,6));
      //=============pangben modify 20110629 start
        if(null!=Operator.getRegion() && Operator.getRegion().length()>0){
            parm.setData("REGION_CODE",Operator.getRegion());
        }
        //=============pangben modify 20110629 stop
      parm = INFReportTool.getInstance().selectYearMonEXMStatistics(parm);
      getTable("TABLE").setHeader("科室,120;检测日期,160;平均分,90;备注,80");
      getTable("TABLE").setLockColumns("0,1,2");
      getTable("TABLE").setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,left");
      getTable("TABLE").setParmMap("DEPT_CHN_DESC;DATE_ALL;ITEM_GAINPOINT;REMARK");
      getTable("TABLE").setParmValue(parm);
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
      String tag = "";
      if(getValueString("DEPT_RESULT").equals("Y"))
          tag = "科室检测统计表-" + "科室检测结果统计";
      else if(getValueString("DEPT_YEAR").equals("Y"))
          tag = "科室检测统计表-" + "科室检测年统计";
      else if(getValueString("DEPT_MONTH").equals("Y"))
          tag = "科室检测统计表-" + "医院检测月统计";
      ExportExcelUtil.getInstance().exportExcel(mainTable, tag);
   }
    /**
     * 得到界面表格
     * @param tag String
     * @return TTable
     */
    private TTable getTable(String tag){
        return (TTable)getComponent(tag);
    }

    /**
     * 清空方法
     */
    public void onClear(){
        onInit();
        setValue("DEPT_RESULT","Y");
        getTable("TABLE").removeRowAll();
    }
}
