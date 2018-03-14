package com.javahis.ui.adm;

import com.dongyang.control.*;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TTable;
import java.sql.Timestamp;
import jdo.adm.ADMOpdDrPrintTool;
import jdo.sys.Operator;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title: 门急医师办理入院统计报表</p>
 *
 * <p>Description: 门急医师办理入院统计报表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-11-19
 * @version 1.0
 */
public class ADMOpdDrPrintControl
    extends TControl {
    TTable TABLE;
    TParm DATA;
    String P_DATE="";//记录查询日期
    String P_DEPT="";//记录查询的科室名称
    String P_USER="";//记录查询的医师姓名
    public void onInit(){
        super.onInit();
        TABLE = (TTable)this.getComponent("TABLE");
        onClear();
        //========pangben modify 20110510 start 权限添加
        setValue("REGION_CODE", Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110510 stop

    }
    /**
     * 查询
     */
    public void onQuery(){
        if(this.getValue("DATE_S")==null){
            this.messageBox_("请选择起始日期！");
            this.grabFocus("DATE_S");
            return;
        }
        if(this.getValue("DATE_E")==null){
            this.messageBox_("请选择截止日期！");
            this.grabFocus("DATE_E");
            return;
        }

        TParm parm = new TParm();
        parm.setData("DATE_S",StringTool.getString((Timestamp)this.getValue("DATE_S"),"yyyyMMddHHmmss"));
        parm.setData("DATE_E",StringTool.getString((Timestamp)this.getValue("DATE_E"),"yyyyMMddHHmmss"));
        P_DEPT = "";
        P_USER = "";
        //=============pangben modify 20110510 start 添加区域参数
         if (null != this.getValueString("REGION_CODE") &&
             this.getValueString("REGION_CODE").length() != 0)
             parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
         //=============pangben modify 20110510 stop
        if(this.getValueString("IN_DEPT_CODE").length()>0){
            parm.setData("IN_DEPT_CODE",this.getValueString("IN_DEPT_CODE"));
            P_DEPT = this.getText("IN_DEPT_CODE");
        }
        if(this.getValueString("OPD_DR_CODE").length()>0){
            parm.setData("OPD_DR_CODE",this.getValueString("OPD_DR_CODE"));
            P_USER = "医师："+this.getText("OPD_DR_CODE");
        }
        DATA = ADMOpdDrPrintTool.getInstance().selectOpdDrCount(parm);
        if(DATA.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        int sum = 0;
        for(int i=0;i<DATA.getCount("DEPT_CHN_DESC");i++){
            sum = sum+DATA.getInt("NUM",i);
        }
        //=============chenxi modify 20120306 start
        DATA.addData("REGION_CHN_ABN","合计：");
        //=============chenxi modify 20120306 stop
        DATA.addData("NUM",sum);
        TABLE.setParmValue(DATA);
        P_DATE = StringTool.getString((Timestamp)this.getValue("DATE_S"),"yyyy/MM/dd") +
            " 至 "+StringTool.getString((Timestamp)this.getValue("DATE_E"),"yyyy/MM/dd");
    }
    /**
     * 清空
     */
    public void onClear(){
        //获取当前日期
        String date = StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMdd");
        //初始化统计区间
        this.setValue("DATE_S",StringTool.getTimestamp(date,"yyyyMMdd"));
        this.setValue("DATE_E",StringTool.getTimestamp(date+"235959","yyyyMMddHHmmss"));
        this.clearValue("IN_DEPT_CODE;OPD_DR_CODE");
        TABLE.removeRowAll();
        //=======pangben modify 20110510 start
        this.setValue("REGION_CODE",Operator.getRegion());
    }
    /**
     * 打印
     */
    public void onPrint(){
        TParm printData = new TParm();
        int count = 0;
        for(int i=0;i<=DATA.getCount("DEPT_CHN_DESC");i++){
             //========chenxi modify 20120306 start
            printData.addData("REGION_CHN_ABN",DATA.getValue("REGION_CHN_ABN",i));//chenxi 
             //========chenxi modify 20120306 stop
            printData.addData("DEPT_CHN_DESC",DATA.getValue("DEPT_CHN_DESC",i));
            printData.addData("USER_NAME",DATA.getValue("USER_NAME",i));
            printData.addData("NUM",DATA.getValue("NUM",i));
            count++;
        }
        printData.setCount(count);
         //========chenxi modify 20120306 start
        printData.addData("SYSTEM","COLUMNS","REGION_CHN_ABN");
         //========chenxi modify 20120306 stop
        printData.addData("SYSTEM","COLUMNS","DEPT_CHN_DESC");
        printData.addData("SYSTEM","COLUMNS","USER_NAME");
        printData.addData("SYSTEM","COLUMNS","NUM");
        TParm Basic = new TParm();
        Basic.setData("P_USER", Operator.getName());
        Basic.setData("P_DATE",
                      StringTool.getString(SystemTool.getInstance().getDate(),
                                           "yyyy年MM月dd日"));

        TParm printParm = new TParm();
        printParm.setData("T1",printData.getData());
        //========pangben modify 20110510 start
        String region= DATA.getRow(0).getValue("REGION_CHN_ABN");//chenxi
        printParm.setData("TITLE", "TEXT",
                     ( this.getValue("REGION_CODE")!=null&&!this.getValue("REGION_CODE").equals("")?region:"所有医院") + "门急医师办理入院统计报表");
        //========pangben modify 20110510 stop
        printParm.setData("DATE","TEXT",P_DATE);
        printParm.setData("DEPT","TEXT",P_DEPT);
        printParm.setData("DR","TEXT",P_USER);
        printParm.setData("Basic", Basic.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMOpdDrPrint.jhw",printParm);
    }
}
