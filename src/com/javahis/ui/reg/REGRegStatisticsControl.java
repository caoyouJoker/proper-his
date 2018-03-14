package com.javahis.ui.reg;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.reg.PanelTypeTool;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import jdo.reg.REGRegStatisticsTool;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import java.text.DecimalFormat;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TRadioButton;

/**
 * <p>Title: 挂号信息统计</p>
 *
 * <p>Description: 挂号信息统计</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-03-18
 * @version 4.0
 */
public class REGRegStatisticsControl
    extends TControl {
private TTable TABLE;
  private TParm CLINICTYPE;//记录号别
  private String ParmMap = "";//记录表格绑定数据的顺序
  private String STA_DATE = "";//记录查询时间 用于显示在打印中
  private TParm result;//记录结果集
  DecimalFormat df = new DecimalFormat();//规定格式 “人数”查询不用带小数点，“费用”查询保留2位小数
  public void onInit(){
//      ID,1;NAME,1;PY1,1

              super.onInit();
      TABLE = (TTable)this.getComponent("Table");
      pageInit();
      tableInit();
      //=========pangben modify 20110422 start
      TTextFormat  tftDept =(TTextFormat)this.getComponent("DEPT_CODE");
      String sql="SELECT DEPT_CODE AS ID,DEPT_CHN_DESC AS NAME,DEPT_ENG_DESC,PY1 FROM SYS_DEPT WHERE (OPD_FIT_FLG='Y' OR EMG_FIT_FLG='Y') AND DEPT_GRADE='3'";
      if(null!=Operator.getRegion()&&!"".equals(Operator.getRegion()))
          sql+=" AND REGION_CODE ='"+Operator.getRegion()+"'";
      tftDept.setPopupMenuSQL(sql);
      tftDept.setPopupMenuFilter("ID,1;NAME,1;PY1,1");
      //==========pangben modify 20110422 stop
      //========pangben modify 20110421 start 权限添加
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop

  }
  /**
   * 设置页面起始状态
   */
  private void pageInit(){
      //this.setValue("DEPT_LEAVE","3");//默认选择三级科室
      //默认区域
      //===========pangben modify 20110325
      setValue("REGION_CODE", Operator.getRegion());
      //设置起止日期
      Timestamp now = SystemTool.getInstance().getDate();
      this.setValue("DATE_S",now);
      this.setValue("DATE_E",now);
  }
  /**
   *  表格初始化
   */
  private void tableInit(){
      CLINICTYPE = PanelTypeTool.getInstance().queryTree();
      String tableHead = "";
      //循环号别 指定表格的表头 和 表格绑定数据的 字段名
      for (int i = 0; i < CLINICTYPE.getCount(); i++) {
          tableHead += CLINICTYPE.getValue("CLINICTYPE_DESC",i) + ",100;";
          ParmMap += CLINICTYPE.getValue("CLINICTYPE_CODE",i) + ";";
      }
      ParmMap += "SUM";
      ParmMap = "DEPT_DESC;"+ParmMap;
      ParmMap = "REGION_CHN_ABN;"+ParmMap;//========pangben modify 20110410,  modify 20120305
      TABLE.setHeader("区域,120;科室名称,100;"+tableHead+"合计,100");
  }
  /**
   * 科室等级下拉框事件
   */
  public void onDEPT_LEAVE(){
      String leave = this.getValueString("DEPT_LEAVE");
      if("2".equals(leave)){
          this.clearValue("DEPT_CODE;USER_ID");
          this.callFunction("UI|DEPT_CODE|setEnabled",false);
          this.callFunction("UI|USER_ID|setEnabled",false);
      }else if("3".equals(leave)){
          this.callFunction("UI|DEPT_CODE|setEnabled",true);
          this.callFunction("UI|USER_ID|setEnabled",true);
      }
  }
  /**
   * 清空事件
   */
  public void onClear(){
      pageInit();
      this.clearValue("DEPT_CODE;USER_ID");
      TABLE.removeRowAll();
  }
  /**
   * 查询
   */
  public void onQuery(){
      TParm data = new TParm();
      TParm parm = new TParm();
      parm.setData("DATE_S",StringTool.getString((Timestamp)this.getValue("DATE_S"),"yyyyMMdd")+"000000");
      parm.setData("DATE_E",StringTool.getString((Timestamp)this.getValue("DATE_E"),"yyyyMMdd")+"235959");
      //添加查询条件
      //========pangben modify 20110325 start
      if(this.getValue("REGION_CODE")!=null&&!this.getValue("REGION_CODE").equals(""))
          parm.setData("REGION_CODE",this.getValue("REGION_CODE"));
       //========pangben modify 20110325 stop
      //人数统计
      //===========pangben modify 20110413 start 修改不需要科室条件查询
      if (this.getValueString("DEPT_CODE").length() > 0)
          parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
      if (this.getValueString("DR_CODE").length() > 0)
          parm.setData("DR_CODE", this.getValueString("DR_CODE"));
      if ("Y".equals(this.getValueString("type1"))) {
          data = REGRegStatisticsTool.getInstance().selectNumForDept3(parm);
          df = new DecimalFormat("0");
//          //判断 二级科室
//          if("2".equals(getValueString("DEPT_LEAVE"))){
//              data = REGRegStatisticsTool.getInstance().selectNumForDept2(parm);
//          }
//          else if("3".equals(getValueString("DEPT_LEAVE"))){
//              if(this.getValueString("DEPT_CODE").length()>0)
//                  parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
//              if(this.getValueString("DR_CODE").length()>0)
//                  parm.setData("DR_CODE",this.getValueString("DR_CODE"));
//              data = REGRegStatisticsTool.getInstance().selectNumForDept3(parm);
//          }
      } else if ("Y".equals(this.getValueString("type2"))) {
          df = new DecimalFormat("0.00");

          data = REGRegStatisticsTool.getInstance().selectNumForDept3_M(parm);
//          //判断 二级科室
//          if("2".equals(getValueString("DEPT_LEAVE"))){
//              data = REGRegStatisticsTool.getInstance().selectNumForDept2_M(parm);
//          }
//          else if("3".equals(getValueString("DEPT_LEAVE"))){
//              if(this.getValueString("DEPT_CODE").length()>0)
//                  parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
//              if(this.getValueString("DR_CODE").length()>0)
//                  parm.setData("DR_CODE",this.getValueString("DR_CODE"));
//              data = REGRegStatisticsTool.getInstance().selectNumForDept3_M(parm);
//          }
      }
      //===========pangben modify 20110413 stop
      if ( null==data ||data.getCount()==0){
          this.messageBox("没有需要查询的数据");
          return;
      }
      //汇总整理数据
      result = getResult(data);
      //计算总计行
      String[] code = ParmMap.split(";");
      TParm sumRow = new TParm();
      for(int i=2;i<code.length;i++){
          String CLINICTYPE_CODE = code[i];
          double num = 0;
          for (int j = 0; j < result.getCount("DEPT_DESC"); j++) {
              num += result.getDouble(CLINICTYPE_CODE,j);
              sumRow.setData(CLINICTYPE_CODE,0,df.format(num));
          }
      }
      sumRow.setData("DEPT_DESC",0,"总  计");
      //=============pangben modify 20110426 start
      int count=result.getCount("DEPT_DESC");
      result.setRowData(count,sumRow,0);
      result.setData("DEPT_DESC", count, "");
      result.setData("REGION_CHN_ABN", count, "总计:");               //======fuxin  modify 20120305
      if (((TRadioButton)this.getComponent("type1")).isSelected()) {
          getValueFormat("N", result, count);//格式化方法显示不同数据

      } else if(((TRadioButton)this.getComponent("type2")).isSelected()){
          getValueFormat("Y", result, count);//格式化方法显示不同数据
      }
      TABLE.setParmValue(result, ParmMap);
      STA_DATE = StringTool.getString((Timestamp)this.getValue("DATE_S"),
                                      "yyyy/MM/dd") + " " +
                 this.getValue("S_TIME") + "～" +
          StringTool.getString( (Timestamp)this.getValue("DATE_E"), "yyyy/MM/dd")+" "+this.getValue("E_TIME");
      //=============pangben modify 20110410 stop
  }
  /**
   * 格式化显示数据，通过选择单选按钮可以显示不同的格式
   * @param type String
   * @param result TParm
   * @return TParm
   * ========pangben modify 20110426
   */
  public TParm getValueFormat(String type, TParm result, int count) {

      if(type.equals("Y")){
          double sumZRYS = 0.00; //主任医师
          double sumFZRYS = 0.00; //副主任医师
          double sumZZYS = 0.00; //主治医师
          double sumClinic = 0.00; //门诊
          double sumEmergency = 0.00; //急诊
          double sumExpert = 0.00; //专家
          double sumAdvClinic = 0.00; //便民门诊
          double sumCount = 0.00; //总计

          for (int i = 0; i < count; i++) {
              sumZRYS += StringTool.round(result.getDouble("1", i), 2);
              sumFZRYS += StringTool.round(result.getDouble("2", i), 2);
              sumZZYS += StringTool.round(result.getDouble("3", i), 2);
              sumClinic += StringTool.round(result.getDouble("4", i), 2);
              sumEmergency += StringTool.round(result.getDouble("5", i), 2);
              sumExpert += StringTool.round(result.getDouble("6", i), 2);
              sumAdvClinic += StringTool.round(result.getDouble("7", i), 2);
              sumCount += StringTool.round(result.getDouble("SUM", i), 2);
          }

          df = new DecimalFormat("0.00");
          result.setData("1", count, df.format(StringTool.round(sumZRYS, 2)));
          result.setData("2", count, df.format(StringTool.round(sumFZRYS, 2)));
          result.setData("3", count, df.format(StringTool.round(sumZZYS, 2)));
          result.setData("4", count, df.format(StringTool.round(sumClinic, 2)));
          result.setData("5", count, df.format(StringTool.round(sumEmergency, 2)));
          result.setData("6", count, df.format(StringTool.round(sumExpert, 2)));
          result.setData("7", count, df.format(StringTool.round(sumAdvClinic, 2)));
          result.setData("SUM", count, df.format(StringTool.round(sumCount, 2)));
      }else{
         int sumZRYS = 0; //主任医师
         int sumFZRYS = 0; //副主任医师
         int sumZZYS = 0; //主治医师
         int sumClinic = 0; //门诊
         int sumEmergency = 0; //急诊
         int sumExpert = 0; //专家
         int sumAdvClinic = 0; //便民门诊
         int sumCount = 0; //总计
          for (int i = 0; i < count; i++) {
              sumZRYS += result.getInt("1", i);
              sumFZRYS += result.getInt("2", i);
              sumZZYS += result.getInt("3", i);
              sumClinic += result.getInt("4", i);
              sumEmergency += result.getInt("5", i);
              sumExpert += result.getInt("6", i);
              sumAdvClinic += result.getInt("7", i);
              sumCount += result.getInt("SUM", i);
              for(int j=1;j<8;j++){
                  result.setData(j+"", i, result.getInt(j+"", i));
              }
              result.setData("SUM", i, result.getInt("SUM", i));
          }
          result.setData("1", count, sumZRYS);
          result.setData("2", count, sumFZRYS);
          result.setData("3", count, sumZZYS);
          result.setData("4", count,sumClinic);
          result.setData("5", count, sumEmergency);
          result.setData("6", count,sumExpert);
          result.setData("7", count, sumAdvClinic);
          result.setData("SUM", count, sumCount);
      }
      return result;
  }
  /**
   * 整理用于绑定表格的数据
   * @param parm TParm
   * @return TParm
   */
  private TParm getResult(TParm parm){
      //整理要查询的科室信息

      TParm dept = new TParm();
      //========pangben modify 20110325 start
      TParm parm1 = new TParm();
      if (this.getValue("REGION_CODE") != null &&
          !this.getValue("REGION_CODE").equals(""))
          parm1.setData("REGION_CODE", this.getValue("REGION_CODE"));
      //========pangben modify 20110325 stop
      //===============pangben modify 20110413 修改不需要可以条件查询
//      if ("2".equals(getValueString("DEPT_LEAVE"))) {
//          dept = REGRegStatisticsTool.getInstance().selectDept2();//获取二级部门列表
//      }else if ("3".equals(getValueString("DEPT_LEAVE"))) {
//          if("".equals(this.getValueString("DEPT_CODE")))
//              //========pangben modify 20110325 start 添加参数
//              dept = REGRegStatisticsTool.getInstance().selectOEDpet(parm1);//获取三级门急诊部门列表
//              //========pangben modify 20110325 stop
//          else if(this.getValueString("DEPT_CODE").length()>0){
//              dept.addData("ID",this.getValue("DEPT_CODE"));
//              dept.addData("NAME",this.getText("DEPT_CODE"));
//              dept.setCount(1);
//          }
//      }
      //===============pangben modify 20110413 stop
       dept = REGRegStatisticsTool.getInstance().selectOEDpet(parm1);//获取三级门急诊部门列表
      TParm re = new TParm();
      int row = 0;
      //循环查询每一个科室的数据
      for(int i=0;i<dept.getCount();i++){
          TParm rowData = getRowParm(parm,dept.getValue("ID",i),dept.getValue("REGION_CHN_ABN",i)); //======fuxin  modify 20120305
          if(rowData.getValue("DEPT_DESC",0).length()>0){
              re.setRowData(row,rowData,0);
              row++;
          }
      }

      return re;
  }
  /**
   * 获取每个科室的数据
   * @param parm TParm 全部数据
   * @param DEPT_CODE String  科室CODE
   * @return TParm
   * ========pangben modify 20110413 添加区域参数
   */
  private TParm getRowParm(TParm parm,String DEPT_CODE,String REGION_CODE){
      int row = 0;
      TParm deptData = new TParm();
      for(int i=0;i<parm.getCount();i++){
          if(DEPT_CODE.equals(parm.getValue("DEPT_CODE",i))&&parm.getValue("REGION_CHN_ABN",i).equals(REGION_CODE)){ //======fuxin  modify 20120305
              deptData.setRowData(row,parm.getRow(i));
              row++;
          }
      }
      TParm result = new TParm();
      boolean flg = false;//记录是否有该科室的数据
      double sum = 0.00;
      //从汇总的一个科室的数据中 分离出每种号别的数据
      for(int i=0;i<CLINICTYPE.getCount();i++){
          boolean flg2 = false;//记录该科室是否包括此号别的信息
          for(int j=0;j<deptData.getCount("DEPT_CODE");j++){
              if(CLINICTYPE.getValue("CLINICTYPE_CODE",i).equals(deptData.getValue("CLINICTYPE_CODE",j))){
                  result.setData(CLINICTYPE.getValue("CLINICTYPE_CODE",i), 0,df.format(deptData.getData("NUM", j)));
                  sum +=StringTool.round(deptData.getDouble("NUM", j),2);
                  flg = true;
                  flg2 = true;
              }
          }
          //如果不包括该号别的挂号信息 那么赋默认值0
          if(!flg2){
              result.setData(CLINICTYPE.getValue("CLINICTYPE_CODE",i), 0,df.format(0));
          }
      }

      //如果有该科室的数据 那么为该行数据写入科室名称 写入合计列
      if(flg){
          //===============pangben modify 20110410 start
          result.setData("REGION_CHN_ABN", 0, deptData.getValue("REGION_CHN_ABN", 0)); //======fuxin  modify 20120305
          //===============pangben modify 20110410 stop
          result.setData("DEPT_DESC", 0, deptData.getValue("DEPT_CHN_DESC", 0));
          result.setData("SUM", 0,df.format(sum));
      }
      return result;
  }
  /**
   * 科室选择事件
   */
  public void onDEPT_CODE(){
      this.clearValue("DR_CODE");
      this.callFunction("UI|DR_CODE|onQuery");
  }
  /**
   * 打印
   */
  public void onPrint(){
      if(result.getCount("DEPT_DESC")<=0){
          return;
      }
      TParm t1 = new TParm();
      TParm headerT=new TParm();
      String header=TABLE.getHeader().replace(",100","");
      String[] titleValue =  (header.replace(",120","")).split(";");
      String[] title = ParmMap.split(";");
      for (int j = 0; j < 10; j++) {

          if (j < title.length)
              headerT.addData(title[j], titleValue[j]);
          else
              headerT.addData(""+(j-2), "");
      }
      headerT.setCount(1);
      headerT.addData("SYSTEM","COLUMNS","REGION_CHN_ABN");//======pangben modify 20110410,fuxin  modify 20120305
      headerT.addData("SYSTEM","COLUMNS","DEPT_DESC");

//      for (int i = 1; i < 8; i++) {
//          if (i < result.getCount("DEPT_DESC") + 1){
//               System.out.println("t1:"+i+":"+t1);
//              t1.setRowData(i, result.getRow(i - 1));
//          }
//          else{
////              TParm parm=new TParm();
////              parm.setData("","");
//              System.out.println("i:"+i);
//              for(int k=0;k<result.getCount();k++){
//                  t1.addData(""+i,"");
//              }
//            //  t1.setRowData(i, parm);
//            System.out.println("t1:"+i+":"+t1);
//          }
//      }
      t1.setCount(result.getCount("DEPT_DESC"));
      t1.addData("SYSTEM","COLUMNS","REGION_CHN_ABN");//======pangben modify 20110410,fuxin  modify 20120305
      t1.addData("SYSTEM","COLUMNS","DEPT_DESC");
      //==============报表表格列数写死了
      for(int i=0;i<8;i++){
          if(i==CLINICTYPE.getCount()){
             headerT.addData("SYSTEM","COLUMNS","SUM");
             t1.addData("SYSTEM","COLUMNS","SUM");
             continue;
         }
          if(i<CLINICTYPE.getCount()){
              t1.addData("SYSTEM", "COLUMNS",
                         CLINICTYPE.getValue("CLINICTYPE_CODE", i));
              headerT.addData("SYSTEM", "COLUMNS",
                              CLINICTYPE.getValue("CLINICTYPE_CODE", i));
          }else{
              t1.addData("SYSTEM", "COLUMNS",
                         ""+i);
              headerT.addData("SYSTEM", "COLUMNS",
                              ""+i);
          }
      }
      //===========pangben modify 20110701 start 将没有数据的列显示“”
      for (int i = 0; i < result.getCount("DEPT_DESC"); i++) {
          for (int j = 0; j < 8; j++) {
              if(j<CLINICTYPE.getCount()){
                  t1.addData(CLINICTYPE.getValue("CLINICTYPE_CODE", j),
                             result.getValue(CLINICTYPE.getValue(
                                     "CLINICTYPE_CODE", j), i));
              }else
                  t1.addData(""+j,"");

          }
          t1.addData("SUM", result.getValue("SUM", i));
          t1.addData("REGION_CHN_ABN",
                     result.getValue("REGION_CHN_ABN", i)); //======fuxin  modify 20120305
          t1.addData("DEPT_DESC", result.getValue("DEPT_DESC", i));
      }
      //===========pangben modify 20110701 stop
      t1.setData("REGION_CHN_ABN", result.getCount("DEPT_DESC"), "总计:"); //======pangben modify 20110410
      TParm printData = new TParm();
      printData.setData("headerT",headerT.getData());
      printData.setData("T1",t1.getData());
      //========pangben modify 20110329 start
      TTable table=  ((TTable)this.getComponent("Table"));
      String region=table.getParmValue().getRow(0).getValue("REGION_CHN_ABN");//======fuxin modify 20120305
      printData.setData("TITLE", "TEXT",
                  (this.getValue("REGION_CODE")!=null&&!this.getValue("REGION_CODE").equals("")?region:"所有医院" ) + "挂号信息统计表");
     //========pangben modify 20110329 stop
      printData.setData("date","TEXT",STA_DATE);
      printData.setData("printDate","TEXT",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd"));
      printData.setData("printUser","TEXT",Operator.getName());
      this.openPrintDialog("%ROOT%\\config\\prt\\REG\\REGRegStatistics.jhw",printData);
  }
  /**
   * 导出Excel表格
   */
  public void onExport() {
      TTable table = (TTable) callFunction("UI|Table|getThis");
      if (table.getRowCount() <= 0) {
          messageBox("无导出资料");
          return;
      }
      ExportExcelUtil.getInstance().exportExcel(table, "挂号信息统计报表");
  }

}
