package com.javahis.ui.adm;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.adm.ADMOutPrintTool;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ADMOutPrintControl
    extends TControl {
    TTable TABLE;
    TParm DATA;
    String P_DATE="";//记录查询日期
    String P_CTZ="全部";//记录查询的身份别
    //===pangben modify 20110510 start
    TParm printData;// 打印数据容器
   //===pangben modify 20110510 stop
    public void onInit(){
        super.onInit();
        TABLE = (TTable)this.getComponent("TABLE");
        onClear();
        //========pangben modify 20110510 start 权限添加
        setValue("REGION_CODE", Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        printData=new TParm();//打印数据容器
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
        double SUM=0.00;//总金额
        TParm parm = new TParm();
        //=============pangben modify 20110510 start 添加区域参数
        if (null != this.getValueString("REGION_CODE") &&
            this.getValueString("REGION_CODE").length() != 0)
            parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        //=============pangben modify 20110510 stop
        parm.setData("DATE_S",StringTool.getString((Timestamp)this.getValue("DATE_S"),"yyyyMMddHHmmss"));
        parm.setData("DATE_E",StringTool.getString((Timestamp)this.getValue("DATE_E"),"yyyyMMddHHmmss"));
        P_CTZ="全部";
        if(this.getValueString("CTZ").length()>0){
            parm.setData("CTZ1_CODE",this.getValueString("CTZ"));
            TComboBox box =(TComboBox)this.getComponent("CTZ");
            P_CTZ = box.getSelectedName();
        }
        DATA = ADMOutPrintTool.getInstance().selectOutHosp(parm);
        if(DATA.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        //=========pangben modify 20110510
       for(int i=0;i<DATA.getCount("MR_NO");i++){
           SUM += DATA.getDouble("TOTAL_AMT", i);
       }
       //显示总费用列
       //==========chenxi 20120306  start
       DATA.addData("REGION_CHN_ABN", "合计");  
       //===========chenxiu 20120306  stop
       DATA.addData("TOTAL_AMT", StringTool.round(SUM, 2));

       for (int i = 0; i <= DATA.getCount("MR_NO"); i++) {
           //=======chenxi modify 20120306 start
           printData.addData("REGION_CHN_ABN",DATA.getValue("REGION_CHN_ABN",i));  

           //======chenxi modify 20120306 stop
           printData.addData("MR_NO",DATA.getValue("MR_NO",i));
           printData.addData("IPD_NO",DATA.getValue("IPD_NO",i));
           printData.addData("DEPT_CHN_DESC",DATA.getValue("DEPT_CHN_DESC",i));
           //=======pangben modify 20110510 start
           //病区列
           printData.addData("STATION_DESC",DATA.getValue("STATION_DESC",i));
           //=======pangben modify 20110510 stop
           printData.addData("CTZ_DESC",DATA.getValue("CTZ_DESC",i));
           printData.addData("PAT_NAME",DATA.getValue("PAT_NAME",i));
           printData.addData("CHN_DESC",DATA.getValue("CHN_DESC",i));
           printData.addData("INS",DATA.getValue("INS",i));
           printData.addData("TOTAL_AMT",DATA.getValue("TOTAL_AMT",i));
           printData.addData("INS_USER",DATA.getValue("INS_USER",i));
           printData.addData("DS_DATE",StringTool.getString(DATA.getTimestamp("DS_DATE",i),"yyyy/MM/dd"));
           printData.addData("INS_DATE",StringTool.getString(DATA.getTimestamp("INS_DATE",i),"yyyy/MM/dd"));
       }
       TABLE.setParmValue(DATA);
       P_DATE = StringTool.getString((Timestamp)this.getValue("DATE_S"),
                                     "yyyy/MM/dd HH:mm:ss") +
                " 至 " +
                StringTool.getString((Timestamp)this.getValue("DATE_E"), "yyyy/MM/dd HH:mm:ss");
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
        this.clearValue("CTZ");
        TABLE.removeRowAll();
        //=======pangben modify 20110510 start
        this.setValue("REGION_CODE",Operator.getRegion());
    }
    /**
     * 打印
     */
    public void onPrint(){
        if(DATA.getCount()<1){
            this.messageBox("没有打印的数据");
            return ;
        }
        printData.setCount(DATA.getCount("MR_NO")+1);
        //=======chenxi modify 20120306 start
        printData.addData("SYSTEM", "COLUMNS", "REGION_CHN_ABN");// CHENXI
        //=======chenxi modify 20120306 stop
        printData.addData("SYSTEM","COLUMNS","MR_NO");
        printData.addData("SYSTEM","COLUMNS","IPD_NO");
        printData.addData("SYSTEM","COLUMNS","DEPT_CHN_DESC");
        //=======pangben modify 20110510 start
        printData.addData("SYSTEM", "COLUMNS", "STATION_DESC");
         //=======pangben modify 20110510 stop
        printData.addData("SYSTEM","COLUMNS","CTZ_DESC");
        printData.addData("SYSTEM","COLUMNS","PAT_NAME");
        printData.addData("SYSTEM","COLUMNS","CHN_DESC");
        printData.addData("SYSTEM","COLUMNS","INS");
        printData.addData("SYSTEM","COLUMNS","TOTAL_AMT");
        printData.addData("SYSTEM","COLUMNS","INS_USER");
        printData.addData("SYSTEM","COLUMNS","DS_DATE");
        printData.addData("SYSTEM","COLUMNS","INS_DATE");

        TParm Basic = new TParm();
        Basic.setData("P_USER", Operator.getName());
        Basic.setData("P_DATE",
                      StringTool.getString(SystemTool.getInstance().getDate(),
                                           "yyyy年MM月dd日"));
        TParm printParm = new TParm();
        printParm.setData("T1",printData.getData());
        //========chenxi modify 20120306 start
        String region = DATA.getRow(0).getValue("REGION_CHN_ABN");
        //========chenxi modify 20120306 stop
        printParm.setData("TITLE", "TEXT",
                          (this.getValue("REGION_CODE") != null &&
                           !this.getValue("REGION_CODE").equals("") ? region :
                           "所有医院") + "出院统计报表");
        //========pangben modify 20110510 stop
        printParm.setData("DATE", "TEXT", P_DATE);
        printParm.setData("CTZ","TEXT",P_CTZ);
        printParm.setData("Basic", Basic.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMOutPrint.jhw",printParm);
    }
}
