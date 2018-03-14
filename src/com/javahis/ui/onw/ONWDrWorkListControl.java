package com.javahis.ui.onw;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.onw.ONWDrWorkListTool;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title: 门急诊医师工作量统计</p>
 *
 * <p>Description: 门急诊医师工作量统计</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-2-4
 * @version 1.0
 */
public class ONWDrWorkListControl
    extends TControl {
    TParm DATA;//记录查询数据
    TTable table;
    String DEPT_DESC ="";//记录查询的科室
    String QUERY_DATE="";//记录查询日期
    public void onInit(){
        super.onInit();
        //================pangben modify 20110406 start 区域锁定
        setValue("REGION_CODE", Operator.getRegion());
        //================pangben modify 20110406 stop
        //========pangben modify 20110421 start 权限添加
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop

        table = (TTable)this.getComponent("Table");
        this.setValue("ADM_DATE",SystemTool.getInstance().getDate());
    }
    /**
     * 查询
     */
    public void onQuery(){
        if(this.getValue("ADM_DATE")==null){
            this.messageBox_("请选择查询日期");
            this.grabFocus("ADM_DATE");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ADM_DATE",this.getValueString("ADM_DATE").substring(0,10).replace("-",""));
        if(this.getValue("ADM_TYPE")!=null&&!this.getValueString("ADM_TYPE").equals("")){
            parm.setData("ADM_TYPE",this.getValue("ADM_TYPE"));
        }
        if(this.getValue("SESSION_CODE")!=null&&!this.getValueString("SESSION_CODE").equals("")){
            parm.setData("SESSION_CODE",this.getValue("SESSION_CODE"));
        }
        if(this.getValue("DEPT_CODE")!=null&&!this.getValueString("DEPT_CODE").equals("")){
            parm.setData("DEPT_CODE",this.getValue("DEPT_CODE"));
        }
        if(this.getValue("DR_CODE")!=null&&!this.getValueString("DR_CODE").equals("")){
            parm.setData("DR_CODE",this.getValue("DR_CODE"));
        }
        //=================pangben modify 20110406 start 添加区域查询条件语句
        if(this.getValueString("REGION_CODE")!=null&&!this.getValueString("REGION_CODE").equals("")){
            parm.setData("REGION_CODE",this.getValue("REGION_CODE"));
        }
        //=================pangben modify 20110406 stop
        DATA = ONWDrWorkListTool.getInstance().selectData(parm);
        if(DATA.getCount()<=0){
            this.messageBox("E0008");
            table.removeRowAll();
            return;
        }
        DEPT_DESC = this.getText("DEPT_CODE").equals("")?"全院":this.getText("DEPT_CODE");
        QUERY_DATE =StringTool.getString((Timestamp)this.getValue("ADM_DATE"),"yyyy年MM月dd日");
        TParm showData = new TParm();
        //整理数据 计算年龄
        for(int i=0;i<DATA.getCount();i++){
            //======pangben modify 20110418 start
            showData.addData("REGION_CHN_DESC",DATA.getData("REGION_CHN_DESC",i));
            //======pangben modify 20110418 stop
            showData.addData("USER_NAME",DATA.getData("USER_NAME",i));
            showData.addData("PAT_NAME",DATA.getData("PAT_NAME",i));
            showData.addData("CHN_DESC",DATA.getData("CHN_DESC",i));
            showData.addData("AGE",StringUtil.showAge(DATA.getTimestamp("BIRTH_DATE",i),DATA.getTimestamp("ADM_DATE",i)));
            showData.addData("ADDRESS",DATA.getData("ADDRESS",i));
            showData.addData("ICD_CHN_DESC",DATA.getData("ICD_CHN_DESC",i));
            showData.addData("ILLNESS_DATE",DATA.getData("ILLNESS_DATE",i));
            showData.addData("ADM_DATE",DATA.getData("ADM_DATE",i));
            showData.addData("VISIT_CODE",DATA.getValue("VISIT_CODE",i).equals("0")?"Y":"N");
            showData.addData("PAD_DATE",DATA.getData("PAD_DATE",i));
        }
        table.setParmValue(showData);
    }
    /**
     * 清空
     */
    public void onClear(){
        table.removeRowAll();
        this.clearValue("ADM_TYPE;SESSION_CODE;DEPT_CODE;DR_CODE");
        this.setValue("ADM_DATE",SystemTool.getInstance().getDate());
    }
    /**
     * 打印
     */
    public void onPrint(){
        if(DATA.getCount()<=0){
            return;
        }
        TParm printData = new TParm();
        //整理数据 计算年龄
        for(int i=0;i<DATA.getCount();i++){
            //=======pangben modify 20110418 start
            printData.addData("REGION_CHN_DESC",DATA.getValue("REGION_CHN_DESC",i));
             //=======pangben modify 20110418 stop
            printData.addData("USER_NAME",DATA.getValue("USER_NAME",i));
            printData.addData("PAT_NAME",DATA.getValue("PAT_NAME",i));
            printData.addData("CHN_DESC",DATA.getValue("CHN_DESC",i));
            printData.addData("AGE",StringUtil.showAge(DATA.getTimestamp("BIRTH_DATE",i),DATA.getTimestamp("ADM_DATE",i)));
            printData.addData("ADDRESS",DATA.getValue("ADDRESS",i));
            printData.addData("ICD_CHN_DESC",DATA.getValue("ICD_CHN_DESC",i));
            printData.addData("ILLNESS_DATE",DATA.getValue("ILLNESS_DATE",i));
            printData.addData("ADM_DATE",StringTool.getString(DATA.getTimestamp("ADM_DATE",i),"yyyy-MM-dd"));
            printData.addData("VISIT_CODE0",DATA.getValue("VISIT_CODE",i).equals("0")?"√":"");
            printData.addData("VISIT_CODE1",DATA.getValue("VISIT_CODE",i).equals("1")?"√":"");
            printData.addData("PAD_DATE",StringTool.getString(DATA.getTimestamp("PAD_DATE",i),"yyyy-MM-dd"));
            printData.addData("DEPT",DATA.getValue("DEPT_CHN_DESC",i));
        }
        printData.setCount(DATA.getCount());
        //=======pangben modify 20110418 start
        printData.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
        //=======pangben modify 20110418 stop
        printData.addData("SYSTEM", "COLUMNS", "USER_NAME");
        printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        printData.addData("SYSTEM", "COLUMNS", "CHN_DESC");
        printData.addData("SYSTEM", "COLUMNS", "AGE");
        printData.addData("SYSTEM", "COLUMNS", "ADDRESS");
        printData.addData("SYSTEM", "COLUMNS", "ICD_CHN_DESC");
        printData.addData("SYSTEM", "COLUMNS", "ILLNESS_DATE");
        printData.addData("SYSTEM", "COLUMNS", "ADM_DATE");
        printData.addData("SYSTEM", "COLUMNS", "VISIT_CODE0");
        printData.addData("SYSTEM", "COLUMNS", "VISIT_CODE1");
        printData.addData("SYSTEM", "COLUMNS", "PAD_DATE");
        printData.addData("SYSTEM", "COLUMNS", "DEPT");
        TParm parm = new TParm();
        parm.setData("T1",printData.getData());
        parm.setData("DEPT","TEXT",DEPT_DESC);
        parm.setData("DATE","TEXT",QUERY_DATE);
        parm.setData("printUser","TEXT",Operator.getName());
        //========pangben modify 20110418 start
        String region= ((TTable)this.getComponent("Table")).getParmValue().getRow(0).getValue("REGION_CHN_DESC");
        parm.setData("title","TEXT",( this.getValue("REGION_CODE")!=null&&!this.getValue("REGION_CODE").equals("")?region:"所有医院")+"门急诊医师工作日志表");
       //========pangben modify 20110418 stop
        this.openPrintDialog("%ROOT%\\config\\prt\\ONW\\ONWDrWorkList.jhw",parm);
    }
    /**
     * 门急住别事件
     */
    public void onADM_TYPE(){
        this.clearValue("DEPT_CODE;DR_CODE");
    }
    /**
     * 科室事件
     */
    public void onDEPT_CODE(){
        this.clearValue("DR_CODE");
    }
}
