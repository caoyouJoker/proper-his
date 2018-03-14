package com.javahis.ui.adm;

import com.dongyang.control.*;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TTable;
import java.sql.Timestamp;
import jdo.adm.ADMDrCountPrintTool;
import jdo.sys.Operator;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title: 住院医师统计报表</p>
 * 
 * <p>Description: 住院医师统计报表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-19
 * @version 1.0
 */
public class ADMDrCountPrintControl
    extends TControl {
    TTable TABLE;
    TParm DATA;
    String P_DATE="";//记录查询日期
    String P_DEPT="";//记录查询的科室名称
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
        //=============pangben modify 20110510 start 添加区域参数
        if (null != this.getValueString("REGION_CODE") &&
            this.getValueString("REGION_CODE").length() != 0)
            parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
        //=============pangben modify 20110510 stop

        parm.setData("DATE_S",
                     StringTool.getString((Timestamp)this.getValue("DATE_S"),
                                          "yyyyMMddHHmmss"));
        parm.setData("DATE_E",StringTool.getString((Timestamp)this.getValue("DATE_E"),"yyyyMMddHHmmss"));
        P_DEPT= "";
        if(this.getValueString("IN_DEPT_CODE").length()>0){
            parm.setData("IN_DEPT_CODE",this.getValueString("IN_DEPT_CODE"));
            P_DEPT = this.getText("IN_DEPT_CODE");
        }
        DATA = ADMDrCountPrintTool.getInstance().selectDrCount(parm);
        if(DATA.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
		if (DATA.getCount() <= 0) {
			this.callFunction("UI|TABLE|setParmValue", new TParm());
			messageBox("查无数据");
			return;
		}
        TABLE.setParmValue(DATA);
        P_DATE = StringTool.getString((Timestamp)this.getValue("DATE_S"),"yyyy/MM/dd HH:mm:ss") +
            " 至 "+StringTool.getString((Timestamp)this.getValue("DATE_E"),"yyyy/MM/dd HH:mm:ss");
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
        this.clearValue("IN_DEPT_CODE");
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
        for(int i=0;i<DATA.getCount("MR_NO");i++){
            //=============pangben modify 20110510 start
            printData.addData("REGION_CHN_ABN",DATA.getValue("REGION_CHN_ABN",i));
            //=============pangben modify 20110510 stop
            printData.addData("USER_NAME",DATA.getValue("USER_NAME",i));
            printData.addData("MR_NO",DATA.getValue("MR_NO",i));
            printData.addData("IPD_NO",DATA.getValue("IPD_NO",i));
            printData.addData("PAT_NAME",DATA.getValue("PAT_NAME",i));
            printData.addData("CTZ_DESC",DATA.getValue("CTZ_DESC",i));
            count++;
        }
        printData.setCount(count);
       //=============pangben modify 20110510 start
        printData.addData("SYSTEM","COLUMNS","REGION_CHN_ABN");
        //=============pangben modify 20110510 stop
        printData.addData("SYSTEM","COLUMNS","USER_NAME");
        printData.addData("SYSTEM","COLUMNS","MR_NO");
        printData.addData("SYSTEM","COLUMNS","IPD_NO");
        printData.addData("SYSTEM","COLUMNS","PAT_NAME");
        printData.addData("SYSTEM","COLUMNS","CTZ_DESC");
        TParm Basic = new TParm();
        Basic.setData("P_USER", Operator.getName());
        Basic.setData("P_DATE",
                      StringTool.getString(SystemTool.getInstance().getDate(),
                                           "yyyy年MM月dd日"));

        TParm printParm = new TParm();
        printParm.setData("T1",printData.getData());
        //========pangben modify 20110510 start
        String region= DATA.getRow(0).getValue("REGION_CHN_ABN");
//        printParm.setData("TITLE", "TEXT",
//                     ( this.getValue("REGION_CODE")!=null&&!this.getValue("REGION_CODE").equals("")?region:"所有医院") + "门急医师办理入院统计报表");
        //liming modify 2012/02/24 start
        printParm.setData("TITLE","TEXT","住院医师统计报表") ;
        //liming modify 2012/02/24 end
        //========pangben modify 20110510 stop
        printParm.setData("DATE","TEXT",P_DATE);
        printParm.setData("DEPT","TEXT",P_DEPT);
        printParm.setData("Basic", Basic.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMDrCountPrint.jhw",printParm);

    }
}
