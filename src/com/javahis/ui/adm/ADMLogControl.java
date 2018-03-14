package com.javahis.ui.adm;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import jdo.adm.ADMLogTool;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title: 住院日志 报表</p>
 *
 * <p>Description: 住院日志 报表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-10-29
 * @version 4.0
 */
public class ADMLogControl
    extends TControl {
    TTable TABLE;
    TComboBox TypeCombo;
    TParm printData;//记录打印信息
    String printDept = "";
    String printStation = "";
    String printDate = "";
    public void onInit(){
        super.onInit();
        TABLE = (TTable)this.getComponent("Table");
        TypeCombo = (TComboBox)this.getComponent("TYPE");
        TypeCombo.setSelectedIndex(5);
        this.setValue("DATE",SystemTool.getInstance().getDate());//初始化日期为当前时间
        //========pangben modify 20110510 start 权限添加
        setValue("REGION_CODE", Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110510 stop

    }
    /**
     * 报表类型选择事件
     */
    public void onTypeChoose(){
        String type = TypeCombo.getSelectedID();
        //==========pangben modfiy 20110510 start 表格表头添加区域
        if ("1".equals(type)) { //入院病人
            TABLE.setHeader("区域,120;床位,80;姓名,80;性别,50;住院号,100;诊断,400");
            TABLE.setColumnHorizontalAlignmentData(
                    "0,left;1,left;2,left;3,left;4,left;5,left");
        } else if ("2".equals(type)) { //出院病人
            TABLE.setHeader("区域,120;床位,80;姓名,80;性别,50;住院号,100;诊断,400");
            TABLE.setColumnHorizontalAlignmentData(
                    "0,left;1,left;2,left;3,left;4,left;5,left");
        } else if ("3".equals(type)) { //死亡病人
            TABLE.setHeader("区域,120;床位,80;姓名,80;性别,50;住院号,100;诊断,400");
            TABLE.setColumnHorizontalAlignmentData(
                    "0,left;1,left;2,left;3,left;4,left;5,left");
        } else if ("4".equals(type)) { //转入病人
            TABLE.setHeader("区域,120;床位,80;姓名,80;性别,50;住院号,100;何科转来,100;诊断,400");
            TABLE.setColumnHorizontalAlignmentData(
                    "0,left;1,left;2,left;3,left;4,left;5,left;6,left");
        } else if ("5".equals(type)) { //转出病人
            TABLE.setHeader("区域,120;床位,80;姓名,80;性别,50;住院号,100;转向科室,100;诊断,400");
            TABLE.setColumnHorizontalAlignmentData(
                    "0,left;1,left;2,left;3,left;4,left;5,left;6,left");
        } else if ("6".equals(type)) { //汇总
            TABLE.setHeader(
                    "区域,120;固定床位数,110;住院人数,80;出院人数,80;出院死亡人数,120;原有人数,90;实有人数,90;转入人数,90;转出人数,90");
            TABLE.setColumnHorizontalAlignmentData(
                    "0,left;1,right;2,right;3,right;4,right;5,right;6,right;7,right;8,right");
        }
         //==========pangben modfiy 20110510 stop

    }
    /**
     * 查询
     */
    public void onQuery(){
        if(this.getValue("DATE")==null){
            this.messageBox_("请选择查询日期！");
            this.grabFocus("DATE");
            return;
        }
        String type = TypeCombo.getSelectedID();
        if("1".equals(type)){//入院病人
            selectInHosp();
        }else if("2".equals(type)){//出院病人
            selectOutHosp();
        }else if("3".equals(type)){//死亡病人
            selectDead();
        }else if("4".equals(type)){//转入病人
            selectINPR();
        }else if("5".equals(type)){//转出病人
            selectOUPR();
        }else if("6".equals(type)){//汇总
            onSelectSum();
        }
        printDept = this.getText("DEPT");
        printStation = this.getText("STATION");
        printDate = StringTool.getString((Timestamp)this.getValue("DATE"),"yyyy年MM月dd日");
    }
    /**
     * 生成入院病人信息
     */
    private void selectInHosp(){
        TParm parm = new TParm();
        //=============pangben modify 20110510 start 添加区域参数
        if(null!=this.getValueString("REGION_CODE")&&this.getValueString("REGION_CODE").length()!=0)
            parm.setData("REGION_CODE",this.getValueString("REGION_CODE"));
        //=============pangben modify 20110510 stop
        parm.setData("DATE",this.getText("DATE").replace("/",""));
        if(this.getValueString("DEPT").length()>0){
            parm.setData("IN_DEPT_CODE",this.getValue("DEPT"));
        }
        if(this.getValueString("STATION").length()>0){
            parm.setData("IN_STATION_CODE",this.getValue("STATION"));
        }
        TParm result = ADMLogTool.getInstance().selectInHosp(parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        if(result.getCount()<=0){
            this.messageBox("E0008");
            TABLE.removeRowAll();
            return;
        }
        //=============pangben modify 20110510 start 添加区域参数
        TABLE.setParmValue(result,"REGION_CHN_DESC;BED_NO_DESC;PAT_NAME;CHN_DESC;IPD_NO;ICD_CHN_DESC");
        //=============pangben modify 20110510 stop
        printData = result;
    }
    /**
     * 生成 出院病人信息
     */
    private void selectOutHosp(){
        TParm parm = new TParm();
        //=============pangben modify 20110510 start 添加区域参数
        if(null!=this.getValueString("REGION_CODE")&&this.getValueString("REGION_CODE").length()!=0)
            parm.setData("REGION_CODE",this.getValueString("REGION_CODE"));
        //=============pangben modify 20110510 stop

        parm.setData("DATE",this.getText("DATE").replace("/",""));
        if(this.getValueString("DEPT").length()>0){
            parm.setData("DS_DEPT_CODE",this.getValue("DEPT"));
        }
        if(this.getValueString("STATION").length()>0){
            parm.setData("DS_STATION_CODE",this.getValue("STATION"));
        }
        TParm result = ADMLogTool.getInstance().selectOutHosp(parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        if(result.getCount()<=0){
            this.messageBox("E0008");
            TABLE.removeRowAll();
            return;
        }
        //=============pangben modify 20110510 start 添加区域参数
        TABLE.setParmValue(result,"REGION_CHN_DESC;BED_NO_DESC;PAT_NAME;CHN_DESC;IPD_NO;ICD_CHN_DESC");
        //=============pangben modify 20110510 stop
        printData = result;
    }
    /**
     * 查询死亡病患信息
     */
    private void selectDead(){
        TParm parm = new TParm();
        //=============pangben modify 20110510 start 添加区域参数
       if(null!=this.getValueString("REGION_CODE")&&this.getValueString("REGION_CODE").length()!=0)
           parm.setData("REGION_CODE",this.getValueString("REGION_CODE"));
       //=============pangben modify 20110510 stop

        parm.setData("DATE",this.getText("DATE").replace("/",""));
        if(this.getValueString("DEPT").length()>0){
            parm.setData("DS_DEPT_CODE",this.getValue("DEPT"));
        }
        if(this.getValueString("STATION").length()>0){
            parm.setData("DS_STATION_CODE",this.getValue("STATION"));
        }
        TParm result = ADMLogTool.getInstance().selectDead(parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        if(result.getCount()<=0){
            this.messageBox("E0008");
            TABLE.removeRowAll();
            return;
        }
        //=============pangben modify 20110510 start 添加区域参数
        TABLE.setParmValue(result,"REGION_CHN_DESC;BED_NO_DESC;PAT_NAME;CHN_DESC;IPD_NO;ICD_CHN_DESC");
        //=============pangben modify 20110510 stop
        printData = result;
    }
    /**
     * 查询转入病患信息
     */
    private void selectINPR(){
        TParm parm = new TParm();
        //=============pangben modify 20110510 start 添加区域参数
      if(null!=this.getValueString("REGION_CODE")&&this.getValueString("REGION_CODE").length()!=0)
          parm.setData("REGION_CODE",this.getValueString("REGION_CODE"));
      //=============pangben modify 20110510 stop

        parm.setData("DATE",this.getText("DATE").replace("/",""));
        if(this.getValueString("DEPT").length()>0){
            parm.setData("DEPT_CODE",this.getValue("DEPT"));
        }
        if(this.getValueString("STATION").length()>0){
            parm.setData("STATION_CODE",this.getValue("STATION"));
        }
        TParm result = ADMLogTool.getInstance().selectINPR(parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        if(result.getCount()<=0){
            this.messageBox("E0008");
            TABLE.removeRowAll();
            return;
        }
        //=============pangben modify 20110510 start 添加区域参数
        TABLE.setParmValue(result,"REGION_CHN_DESC;BED_NO_DESC;PAT_NAME;CHN_DESC;IPD_NO;DEPT_CHN_DESC;ICD_CHN_DESC");
        //=============pangben modify 20110510 stop
        printData = result;
    }
    /**
     * 查询转出病患信息
     */
    private void selectOUPR(){
        TParm parm = new TParm();
        //=============pangben modify 20110510 start 添加区域参数
        if (null != this.getValueString("REGION_CODE") &&
            this.getValueString("REGION_CODE").length() != 0)
            parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        //=============pangben modify 20110510 stop

        parm.setData("DATE",this.getText("DATE").replace("/",""));
        if(this.getValueString("DEPT").length()>0){
            parm.setData("DEPT_CODE",this.getValue("DEPT"));
        }
        if(this.getValueString("STATION").length()>0){
            parm.setData("STATION_CODE",this.getValue("STATION"));
        }
        TParm result = ADMLogTool.getInstance().selectOUPR(parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        if(result.getCount()<=0){
            this.messageBox("E0008");
            TABLE.removeRowAll();
            return;
        }
         //=============pangben modify 20110510 start 添加区域参数
        TABLE.setParmValue(result,"REGION_CHN_DESC;BED_NO_DESC;PAT_NAME;CHN_DESC;IPD_NO;DEPT_CHN_DESC;ICD_CHN_DESC");
        //=============pangben modify 20110510 stop
        printData = result;
    }
    /**
     * 计算汇总数据
     */
    private void onSelectSum(){
        if(this.getValueString("DEPT").length()<=0){
            this.messageBox_("请选择统计科室!");
            this.grabFocus("DEPT");
            return;
        }
        TParm parm = new TParm();
        //=============pangben modify 20110510 start 添加区域参数
        if (null != this.getValueString("REGION_CODE") &&
            this.getValueString("REGION_CODE").length() != 0)
            parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        //=============pangben modify 20110510 stop
        parm.setData("DEPT",this.getValue("DEPT"));
        if(this.getValueString("STATION").length()>0){
            parm.setData("STATION",this.getValueString("STATION"));
        }
        TParm bed = ADMLogTool.getInstance().selectBedSum(parm);
        if(bed.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        TParm seParm = new TParm();
        //=============pangben modify 20110510 start 添加区域参数
       if (null != this.getValueString("REGION_CODE") &&
           this.getValueString("REGION_CODE").length() != 0)
           seParm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
       //=============pangben modify 20110510 stop
        seParm.setData("DATE",this.getText("DATE").replace("/",""));
        if(this.getValueString("DEPT").length()>0){
            seParm.setData("DEPT_CODE",this.getValue("DEPT"));
        }
        if(this.getValueString("STATION").length()>0){
            seParm.setData("STATION_CODE",this.getValue("STATION"));
        }
        TParm inHosp = ADMLogTool.getInstance().selectInHosp(seParm);
        TParm outHosp = ADMLogTool.getInstance().selectOutHosp(seParm);
        TParm INPR = ADMLogTool.getInstance().selectINPR(seParm);
        TParm OUPR = ADMLogTool.getInstance().selectOUPR(seParm);
        TParm dead = ADMLogTool.getInstance().selectDead(seParm);
        TParm patSum = ADMLogTool.getInstance().selectHave(seParm);//获取查询日期的实有病人数
        TParm yesteday = new TParm();
        Timestamp ye = StringTool.rollDate((Timestamp)this.getValue("DATE"),-1);
        yesteday.setData("DATE",StringTool.getString(ye,"yyyyMMdd"));
        if(this.getValueString("DEPT").length()>0){
            yesteday.setData("DEPT_CODE",this.getValue("DEPT"));
        }
        if (this.getValueString("STATION").length() > 0) {
            yesteday.setData("STATION_CODE", this.getValue("STATION"));
        }
        //=============pangben modify 20110510 start 添加区域参数
        if (null != this.getValueString("REGION_CODE") &&
            this.getValueString("REGION_CODE").length() != 0)
            yesteday.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        //=============pangben modify 20110510 stop

        TParm yesPatSum = ADMLogTool.getInstance().selectHave(yesteday);//获取查询日期前一天的实有病人数
        TParm result = new TParm();
        result.addData("REGION_CHN_DESC",null!=this.getValueString("REGION_CODE")&&this.getValueString("REGION_CODE").length()!=0?Operator.getHospitalCHNShortName():"所有院区");
        result.addData("BED",bed.getValue("NUM",0));
        result.addData("IN",inHosp.getCount());
        result.addData("OUT",outHosp.getCount());
        result.addData("DEAD",dead.getCount());
        result.addData("YESTEDAY",yesPatSum.getValue("NUM",0));
        result.addData("PATNUM",patSum.getValue("NUM",0));
        result.addData("INPR",INPR.getCount());
        result.addData("OUPR",OUPR.getCount());
        //=============pangben modify 20110510 start 添加区域参数
        TABLE.setParmValue(result,"REGION_CHN_DESC;BED;IN;OUT;DEAD;YESTEDAY;PATNUM;INPR;OUPR");
        //=============pangben modify 20110510 stop
        printData = result;
    }
    /**
     * 科室选择事件
     */
    public void onDEPT(){
        this.clearValue("STATION");
    }
    /**
     * 清空
     */
    public void onClear(){
        this.clearValue("DEPT;STATION");
        this.setValue("DATE",SystemTool.getInstance().getDate());
        TABLE.removeRowAll();
//        TypeCombo.setSelectedIndex(5);
        //=======pangben modify 20110510 start
        this.setValue("REGION_CODE",Operator.getRegion());
    }
    /**
     * 打印
     */
    public void onPrint(){
        //===========pangben modify 20110510 start
        if (null==TABLE.getParmValue()||TABLE.getParmValue().getCount("REGION_CHN_DESC") < 1){
            this.messageBox("没有要打印的数据");
            return ;
        }
        //===========pangben modify 20110510 stop
        String type = TypeCombo.getSelectedID();
        if("1".equals(type)){//入院病人
            printInHosp();
        }else if("2".equals(type)){//出院病人
            printOutHosp();
        }else if("3".equals(type)){//死亡病人
            printDead();
        }else if("4".equals(type)){//转入病人
            printINPR();
        }else if("5".equals(type)){//转出病人
            printOUPR();
        }else if("6".equals(type)){//汇总
            printSum();
        }
    }
    /**
     * 打印入院病人信息
     */
    private void printInHosp(){
        TParm print = new TParm();
        int count = 0;
        for(int i=0;i<printData.getCount();i++){
            //=========pangben modify 201110510 start
            print.addData("REGION_CHN_DESC", printData.getValue("REGION_CHN_DESC", i));
            //=========pangben modify 201110510 stop
            print.addData("BED_NO_DESC", printData.getValue("BED_NO_DESC", i));
            print.addData("PAT_NAME", printData.getValue("PAT_NAME", i));
            print.addData("CHN_DESC", printData.getValue("CHN_DESC", i));
            print.addData("IPD_NO",printData.getValue("IPD_NO",i));
            print.addData("ICD_CHN_DESC",printData.getValue("ICD_CHN_DESC",i));
            count++;
        }
        print.setCount(count);
        //=========pangben modify 201110510 start
        print.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
        //=========pangben modify 201110510 stop
        print.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
        print.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        print.addData("SYSTEM", "COLUMNS", "CHN_DESC");
        print.addData("SYSTEM", "COLUMNS", "IPD_NO");
        print.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");
        TParm Basic = new TParm();
        Basic.setData("P_USER",Operator.getName());
        Basic.setData("P_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy年MM月dd日"));
        TParm printParm = new TParm();
        printParm.setData("T1", print.getData());
        //========pangben modify 20110510 start
       String region= printData.getRow(0).getValue("REGION_CHN_DESC");
       printParm.setData("TITLE", "TEXT",
                    ( this.getValue("REGION_CODE")!=null&&!this.getValue("REGION_CODE").equals("")?region:"所有医院") + "入院病人日报表");
       //========pangben modify 20110510 stop
        printParm.setData("DATE","TEXT",printDate);
        printParm.setData("DEPT","TEXT",printDept);
        printParm.setData("STATION","TEXT",printStation);
        printParm.setData("Basic",Basic.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMLog1.jhw",printParm);
    }
    /**
     * 打印出院病人信息
     */
    private void printOutHosp(){
        TParm print = new TParm();
        int count = 0;
        for(int i=0;i<printData.getCount();i++){
            //=========pangben modify 201110510 start
            print.addData("REGION_CHN_DESC", printData.getValue("REGION_CHN_DESC", i));
            //=========pangben modify 201110510 stop
            print.addData("BED_NO_DESC",printData.getValue("BED_NO_DESC",i));
            print.addData("PAT_NAME",printData.getValue("PAT_NAME",i));
            print.addData("CHN_DESC",printData.getValue("CHN_DESC",i));
            print.addData("IPD_NO",printData.getValue("IPD_NO",i));
            print.addData("ICD_CHN_DESC",printData.getValue("ICD_CHN_DESC",i));
            count++;
        }
        print.setCount(count);
        //=========pangben modify 201110510 start
        print.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
        //=========pangben modify 201110510 stop
        print.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
        print.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        print.addData("SYSTEM", "COLUMNS", "CHN_DESC");
        print.addData("SYSTEM", "COLUMNS", "IPD_NO");
        print.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");
        TParm Basic = new TParm();
        Basic.setData("P_USER",Operator.getName());
        Basic.setData("P_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy年MM月dd日"));
        TParm printParm = new TParm();
        printParm.setData("T1", print.getData());
        //========pangben modify 20110510 start
        String region = printData.getRow(0).getValue("REGION_CHN_DESC");
        printParm.setData("TITLE", "TEXT",
                          (this.getValue("REGION_CODE") != null &&
                           !this.getValue("REGION_CODE").equals("") ? region :
                           "所有医院") + "出院病人日报表");
        //========pangben modify 20110510 stop
        printParm.setData("DATE", "TEXT", printDate);
        printParm.setData("DEPT", "TEXT", printDept);
        printParm.setData("STATION", "TEXT", printStation);
        printParm.setData("Basic",Basic.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMLog1.jhw",printParm);

    }
    /**
     * 打印死亡病人信息
     */
    private void printDead(){
        TParm print = new TParm();
        int count = 0;
        for(int i=0;i<printData.getCount();i++){
            //=========pangben modify 201110510 start
            print.addData("REGION_CHN_DESC", printData.getValue("REGION_CHN_DESC", i));
            //=========pangben modify 201110510 stop
            print.addData("BED_NO_DESC",printData.getValue("BED_NO_DESC",i));
            print.addData("PAT_NAME",printData.getValue("PAT_NAME",i));
            print.addData("CHN_DESC",printData.getValue("CHN_DESC",i));
            print.addData("IPD_NO",printData.getValue("IPD_NO",i));
            print.addData("ICD_CHN_DESC",printData.getValue("ICD_CHN_DESC",i));
            count++;
        }
        print.setCount(count);
        //=========pangben modify 201110510 start
        print.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
        //=========pangben modify 201110510 stop
        print.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
        print.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        print.addData("SYSTEM", "COLUMNS", "CHN_DESC");
        print.addData("SYSTEM", "COLUMNS", "IPD_NO");
        print.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");
        TParm Basic = new TParm();
        Basic.setData("P_USER",Operator.getName());
        Basic.setData("P_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy年MM月dd日"));
        TParm printParm = new TParm();
        printParm.setData("T1", print.getData());
        //========pangben modify 20110510 start
        String region = printData.getRow(0).getValue("REGION_CHN_DESC");
        printParm.setData("TITLE", "TEXT",
                          (this.getValue("REGION_CODE") != null &&
                           !this.getValue("REGION_CODE").equals("") ? region :
                           "所有医院") + "死亡病人日报表");
        //========pangben modify 20110510 stop
        printParm.setData("DATE","TEXT",printDate);
        printParm.setData("DEPT","TEXT",printDept);
        printParm.setData("STATION","TEXT",printStation);
        printParm.setData("Basic",Basic.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMLog1.jhw",printParm);

    }
    /**
     * 打印转入病人信息
     */
    private void printINPR(){
        TParm print = new TParm();
        int count = 0;
        for(int i=0;i<printData.getCount();i++){
            //=========pangben modify 201110510 start
            print.addData("REGION_CHN_DESC", printData.getValue("REGION_CHN_DESC", i));
            //=========pangben modify 201110510 stop
            print.addData("BED_NO_DESC",printData.getValue("BED_NO_DESC",i));
            print.addData("PAT_NAME",printData.getValue("PAT_NAME",i));
            print.addData("CHN_DESC",printData.getValue("CHN_DESC",i));
            print.addData("IPD_NO",printData.getValue("IPD_NO",i));
            print.addData("DEPT_CHN_DESC",printData.getValue("DEPT_CHN_DESC",i));
            print.addData("ICD_CHN_DESC",printData.getValue("ICD_CHN_DESC",i));
            count++;
        }
        print.setCount(count);
        //=========pangben modify 201110510 start
       print.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
       //=========pangben modify 201110510 stop
        print.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
        print.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        print.addData("SYSTEM", "COLUMNS", "CHN_DESC");
        print.addData("SYSTEM", "COLUMNS", "IPD_NO");
        print.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        print.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");
        TParm Basic = new TParm();
        Basic.setData("P_USER",Operator.getName());
        Basic.setData("P_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy年MM月dd日"));
        TParm printParm = new TParm();
        printParm.setData("T1", print.getData());
        //========pangben modify 20110510 start
       String region = printData.getRow(0).getValue("REGION_CHN_DESC");
       printParm.setData("TITLE", "TEXT",
                         (this.getValue("REGION_CODE") != null &&
                          !this.getValue("REGION_CODE").equals("") ? region :
                          "所有医院") + "转入病人日报表");
       //========pangben modify 20110510 stop
        printParm.setData("DATE","TEXT",printDate);
        printParm.setData("DEPT","TEXT",printDept);
        printParm.setData("STATION","TEXT",printStation);
        printParm.setData("Basic",Basic.getData());
        printParm.setData("TRAN_DEPT","TEXT","何科转来");
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMLog2.jhw",printParm);

    }
    /**
     * 打印转出病人信息
     */
    private void printOUPR(){
        TParm print = new TParm();
       int count = 0;
       for(int i=0;i<printData.getCount();i++){
           //=========pangben modify 201110510 start
            print.addData("REGION_CHN_DESC", printData.getValue("REGION_CHN_DESC", i));
            //=========pangben modify 201110510 stop
           print.addData("BED_NO_DESC",printData.getValue("BED_NO_DESC",i));
           print.addData("PAT_NAME",printData.getValue("PAT_NAME",i));
           print.addData("CHN_DESC",printData.getValue("CHN_DESC",i));
           print.addData("IPD_NO",printData.getValue("IPD_NO",i));
           print.addData("DEPT_CHN_DESC",printData.getValue("DEPT_CHN_DESC",i));
           print.addData("ICD_CHN_DESC",printData.getValue("ICD_CHN_DESC",i));
           count++;
       }
       print.setCount(count);
       //=========pangben modify 201110510 start
       print.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
       //=========pangben modify 201110510 stop
       print.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
       print.addData("SYSTEM", "COLUMNS", "PAT_NAME");
       print.addData("SYSTEM", "COLUMNS", "CHN_DESC");
       print.addData("SYSTEM", "COLUMNS", "IPD_NO");
       print.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
       print.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");
       TParm Basic = new TParm();
       Basic.setData("P_USER",Operator.getName());
       Basic.setData("P_DATE",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy年MM月dd日"));
       TParm printParm = new TParm();
       printParm.setData("T1", print.getData());
       //========pangben modify 20110510 start
      String region = printData.getRow(0).getValue("REGION_CHN_DESC");
      printParm.setData("TITLE", "TEXT",
                        (this.getValue("REGION_CODE") != null &&
                         !this.getValue("REGION_CODE").equals("") ? region :
                         "所有医院") + "转出病人日报表");
      //========pangben modify 20110510 stop
       printParm.setData("DATE","TEXT",printDate);
       printParm.setData("DEPT","TEXT",printDept);
       printParm.setData("STATION","TEXT",printStation);
       printParm.setData("Basic",Basic.getData());
       printParm.setData("TRAN_DEPT","TEXT","转向科室");
       this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMLog2.jhw",printParm);
    }
    /**
     * 打印汇总信息
     */
    private void printSum() {
        TParm print = new TParm();
        int count = 0;
        for (int i = 0; i < printData.getCount("BED"); i++) {
            //=========pangben modify 201110510 start
            print.addData("REGION_CHN_DESC", printData.getValue("REGION_CHN_DESC", i));
            //=========pangben modify 201110510 stop
            print.addData("BED", printData.getValue("BED", i));
            print.addData("IN", printData.getValue("IN", i));
            print.addData("OUT", printData.getValue("OUT", i));
            print.addData("DEAD", printData.getValue("DEAD", i));
            print.addData("YESTEDAY",
                          printData.getValue("YESTEDAY", i));
            print.addData("PATNUM", printData.getValue("PATNUM", i));
            print.addData("INPR", printData.getValue("INPR", i));
            print.addData("OUPR", printData.getValue("OUPR", i));
            count++;
        }
        print.setCount(count);
        //=========pangben modify 201110510 start
        print.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
        //=========pangben modify 201110510 stop
        print.addData("SYSTEM", "COLUMNS", "BED");
        print.addData("SYSTEM", "COLUMNS", "IN");
        print.addData("SYSTEM", "COLUMNS", "OUT");
        print.addData("SYSTEM", "COLUMNS", "DEAD");
        print.addData("SYSTEM", "COLUMNS", "YESTEDAY");
        print.addData("SYSTEM", "COLUMNS", "PATNUM");
        print.addData("SYSTEM", "COLUMNS", "INPR");
        print.addData("SYSTEM", "COLUMNS", "OUPR");
        TParm Basic = new TParm();
        Basic.setData("P_USER", Operator.getName());
        Basic.setData("P_DATE",
                      StringTool.getString(SystemTool.getInstance().getDate(),
                                           "yyyy年MM月dd日"));
        TParm printParm = new TParm();
        printParm.setData("T1", print.getData());
        //========pangben modify 20110510 start
        String region = printData.getRow(0).getValue("REGION_CHN_DESC");
        printParm.setData("TITLE", "TEXT",
                          (this.getValue("REGION_CODE") != null &&
                           !this.getValue("REGION_CODE").equals("") ? region :
                           "所有医院") + "住院日志汇总表");
        //========pangben modify 20110510 stop
        printParm.setData("DATE", "TEXT", printDate);
        printParm.setData("DEPT", "TEXT", printDept);
        printParm.setData("STATION","TEXT",printStation);
        printParm.setData("Basic", Basic.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMLog3.jhw", printParm);
    }
}
