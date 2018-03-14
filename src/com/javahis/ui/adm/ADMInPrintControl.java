package com.javahis.ui.adm;

import com.dongyang.control.*;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import jdo.adm.ADMInPrintTool;
import jdo.sys.Operator;
import com.dongyang.ui.TComboBox;
import com.javahis.util.ExportExcelUtil;

import jdo.sys.SYSRegionTool;

/**
 * <p>Title: 入院统计报表</p>
 *
 * <p>Description: 入院统计报表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-18
 * @version 1.0
 */
public class ADMInPrintControl
    extends TControl {
    TTable TABLE;
    TParm DATA;
    String P_DATE="";//记录查询日期
    String P_CTZ="全部";//记录查询的身份别
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
        parm.setData("DATE_E",
                     StringTool.getString((Timestamp)this.getValue("DATE_E"),
                                          "yyyyMMddHHmmss"));
        P_CTZ = "全部";
        if (this.getValueString("CTZ").length() > 0) {
            parm.setData("CTZ1_CODE",this.getValueString("CTZ"));
            TComboBox box =(TComboBox)this.getComponent("CTZ");
            P_CTZ = box.getSelectedName();
        }
        DATA = ADMInPrintTool.getInstance().selectInHosp(parm);
        if(DATA.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        String countPeo = String.valueOf(DATA.getCount());
        this.setValue("COUNT", countPeo);
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
        this.clearValue("CTZ");
        this.clearValue("COUNT");
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
            //=======chenxi modify 20120306 start
            printData.addData("REGION_CHN_ABN",DATA.getValue("REGION_CHN_ABN",i));
            //=======chenxi modify 20120306 stop
            printData.addData("MR_NO",DATA.getValue("MR_NO",i));
            printData.addData("IPD_NO",DATA.getValue("IPD_NO",i));
            printData.addData("DEPT_CHN_DESC",DATA.getValue("DEPT_CHN_DESC",i));
            printData.addData("STATION_DESC",DATA.getValue("STATION_DESC",i));
            printData.addData("CTZ_DESC",DATA.getValue("CTZ_DESC",i));
            printData.addData("PAT_NAME",DATA.getValue("PAT_NAME",i));
            printData.addData("CHN_DESC",DATA.getValue("CHN_DESC",i));
            printData.addData("ADDRESS",DATA.getValue("ADDRESS",i));
            printData.addData("CONTACTS_NAME",DATA.getValue("CONTACTS_NAME",i));
            printData.addData("CELL_PHONE",DATA.getValue("CELL_PHONE",i));
            printData.addData("IN_DATE",StringTool.getString(DATA.getTimestamp("IN_DATE",i),"yyyy/MM/dd"));
            printData.addData("ADM_CLERK",DATA.getValue("ADM_CLERK",i));
            printData.addData("OPD_DR_CODE",DATA.getValue("OPD_DR_CODE",i));
            count++;
        }
        printData.setCount(count);
        //=======chenxi modify 20120306 start
        printData.addData("SYSTEM","COLUMNS","REGION_CHN_ABN");
        //=======chenxi modify 20120306 stop
        printData.addData("SYSTEM","COLUMNS","MR_NO");
        printData.addData("SYSTEM","COLUMNS","IPD_NO");
        printData.addData("SYSTEM","COLUMNS","DEPT_CHN_DESC");
        printData.addData("SYSTEM","COLUMNS","STATION_DESC");
        printData.addData("SYSTEM","COLUMNS","CTZ_DESC");
        printData.addData("SYSTEM","COLUMNS","PAT_NAME");
        printData.addData("SYSTEM","COLUMNS","CHN_DESC");
        printData.addData("SYSTEM","COLUMNS","ADDRESS");
        printData.addData("SYSTEM","COLUMNS","CONTACTS_NAME");
        printData.addData("SYSTEM","COLUMNS","CELL_PHONE");
        printData.addData("SYSTEM","COLUMNS","IN_DATE");
        printData.addData("SYSTEM","COLUMNS","ADM_CLERK");
        printData.addData("SYSTEM","COLUMNS","OPD_DR_CODE");

        TParm Basic = new TParm();
        Basic.setData("P_USER", Operator.getName());
        Basic.setData("P_DATE",
                      StringTool.getString(SystemTool.getInstance().getDate(),
                                           "yyyy年MM月dd日"));

        TParm printParm = new TParm();
        printParm.setData("T1",printData.getData());
        //========chenxi modify 20120306 start
       String region= DATA.getRow(0).getValue("REGION_CHN_ABN");
       //========chenxi modify 20120306 stop
       printParm.setData("TITLE", "TEXT",
                    ( this.getValue("REGION_CODE")!=null&&!this.getValue("REGION_CODE").equals("")?region:"所有医院") + "入院统计报表");
       //========pangben modify 20110510 stop
        printParm.setData("DATE","TEXT",P_DATE);
        printParm.setData("CTZ","TEXT",P_CTZ);
        printParm.setData("Basic", Basic.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\ADM\\ADMInPrint.jhw",printParm);
    }
    /**
     * 汇出
     */
    public void onExport() {
    	String title ="入院统计报表";
    	if (TABLE.getRowCount() > 0)   		
			ExportExcelUtil.getInstance().exportExcel(TABLE,title);			
    }
    
}
