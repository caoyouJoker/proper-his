package com.javahis.ui.pha;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import java.text.DecimalFormat;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class PHADctWorkloadListControl extends TControl {

    public void onInit() {
        super.onInit();
        initUI();
    }

    private void initUI(){
        setValue("S_DATE",TJDODBTool.getInstance().getDBTime());
        setValue("E_DATE",TJDODBTool.getInstance().getDBTime());
        setValue("REGION_CODE",Operator.getRegion());
        setValue("ADM_TYPE","O");
        //========pangben modify 20110421 start 权限添加
        this.callFunction("UI|REGION_CODE|setEnabled",SYSRegionTool.getInstance().getRegionIsEnabled(this.
               getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop

    }

    public void onQuery(){
        //==============pangben modify 20110417 不限制区域
//        if(getValueString("REGION_CODE").length() == 0){
//            messageBox("请输入区域");
//            return;
//        }
        if(getValueString("ADM_TYPE").length() == 0){
            messageBox("请输入门急住别");
            return;
        }
        if(getValueString("S_DATE").length() == 0 ||
           getValueString("E_DATE").length() == 0){
            messageBox("请输入统计时间");
            return;
        }
        if(getValueString("E_DATE").compareTo(getValueString("S_DATE")) < 0){
            messageBox("输入统计时间有误");
            return;
        }
        //==================pangben modify 20110417 start
        String region="";
        if (getValueString("REGION_CODE").length() > 0) {
            region=" AND A.REGION_CODE='"+getValueString("REGION_CODE")+"'";
        }
        //==================pangben modify 20110417 stop
        String SQL = "";
        if(getValueString("ADM_TYPE").equals("I")){
            SQL = " SELECT B.REGION_CHN_DESC,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD') QDATE,A.DEPT_CODE DEPT,A.DR_CODE DR," +
                  "        SUM (CASE WHEN  A.DOSAGE_QTY < 0 THEN -1 ELSE 1 END) DCT_CASENUM," +
                  "        SUM (A.DOSAGE_QTY) DCT_RXNUM," +
                  "        SUM (A.TOT_AMT) DCT_AMT"+
                  " FROM   IBS_ORDD A,SYS_REGION B" +
                  " WHERE A.REGION_CODE = B.REGION_CODE" +//===========pangben modify 20110417
                  " AND A.BILL_DATE BETWEEN TO_DATE('"+StringTool.getString((Timestamp)getValue("S_DATE"),"yyyyMMdd")+"000000','YYYYMMDDHH24MISS') "+
                  "                      AND TO_DATE('"+StringTool.getString((Timestamp)getValue("E_DATE"),"yyyyMMdd")+"235959','YYYYMMDDHH24MISS') "+
                  " " + (getValueString("DEPT_CODE").length() == 0 ? "": " AND A.DEPT_CODE = '"+getValueString("DEPT_CODE")+"'")+
                  " " + (getValueString("DR_CODE").length() == 0 ? "": " AND A.DR_CODE = '"+getValueString("DR_CODE")+"'") +
                  "   AND  A.ORDER_CAT1_CODE = 'DCT'" +region+//===========pangben modify 20110417
                  " GROUP BY B.REGION_CHN_DESC,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.DEPT_CODE,A.DR_CODE"+
                  " ORDER BY B.REGION_CHN_DESC,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.DEPT_CODE,A.DR_CODE";
        }
        else{
            SQL = " SELECT B.REGION_CHN_DESC,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD') QDATE,A.DEPT_CODE DEPT,A.DR_CODE DR," +
                  "        COUNT (A.ORDER_CODE) DCT_CASENUM," +
                  "        SUM (A.DOSAGE_QTY) DCT_RXNUM," +
                  "        SUM (A.AR_AMT) DCT_AMT"+
                  " FROM   OPD_ORDER A,SYS_REGION B" +
                  " WHERE A.REGION_CODE = B.REGION_CODE" +//===========pangben modify 20110417
                  " AND A.BILL_DATE BETWEEN TO_DATE('"+StringTool.getString((Timestamp)getValue("S_DATE"),"yyyyMMdd")+"000000','YYYYMMDDHH24MISS') "+
                  "                      AND TO_DATE('"+StringTool.getString((Timestamp)getValue("E_DATE"),"yyyyMMdd")+"235959','YYYYMMDDHH24MISS')"+
                  "   AND  A.ADM_TYPE = '"+getValueString("ADM_TYPE")+"'" +
                  " " + (getValueString("DEPT_CODE").length() == 0 ? "": " AND A.DEPT_CODE = '"+getValueString("DEPT_CODE")+"'")+
                  " " + (getValueString("DR_CODE").length() == 0 ? "": " AND A.DR_CODE = '"+getValueString("DR_CODE")+"'") +
                  "   AND  A.ORDER_CAT1_CODE = 'DCT'" +region+//===========pangben modify 20110417
                  " GROUP BY B.REGION_CHN_DESC,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.DEPT_CODE,A.DR_CODE"+
                  " ORDER BY B.REGION_CHN_DESC,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.DEPT_CODE,A.DR_CODE";
        }
       // System.out.println("sql:::"+SQL);
        TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
        //==========pangben modify 20110426 start 表格显示汇总
        if (parm.getCount() <= 0) {
            onClear();
            this.messageBox("该查询条件无数据！");
            return;
        }

        int count = parm.getCount();
        int sumDctCasenum=0;//煎药人次
        int sumDctRxnum=0;//煎药付数
        double sumDctAmt=0.00;//煎药费
        for (int i = 0; i < count; i++) {
            sumDctCasenum += parm.getInt("DCT_CASENUM", i);
            sumDctRxnum += parm.getInt("DCT_RXNUM", i);
            sumDctAmt += parm.getDouble("DCT_AMT", i);
        }
        parm.setData("DCT_CASENUM", count, sumDctCasenum);
        parm.setData("DCT_RXNUM", count, sumDctRxnum);
        parm.setData("DCT_AMT", count, sumDctAmt);
        parm.setData("REGION_CHN_DESC", count, "总计:");
        parm.setData("QDATE", count, "");
        parm.setData("DEPT", count, "");
        parm.setData("DR", count, "");


        callFunction("UI|Table|setParmValue", parm);
    }

    public void onDetail(){
        TParm parm = getTable("Table").getParmValue();
        int row = getTable("Table").getSelectedRow();
        TParm sendParm = new TParm();
        sendParm.setData("DATE",parm.getValue("QDATE",row).replaceAll("/",""));
        sendParm.setData("DR_CODE",parm.getValue("DR",row));
        sendParm.setData("DEPT_CODE",parm.getValue("DEPT",row));
        sendParm.setData("REGION_CODE",getValueString("REGION_CODE"));
        sendParm.setData("ADM_TYPE",getValueString("ADM_TYPE"));
        openDialog("%ROOT%\\config\\PHA\\PHADctWorkloadDetial.x", sendParm);
    }

    private TTable getTable(String tableTag){
        return ((TTable)getComponent(tableTag));
    }

    public void onPrint() {
        TParm printData = new TParm();
        TParm result = (TParm)this.callFunction("UI|Table|getShowParmValue");
        if (result.getCount() <= 0) {
            this.messageBox("无打印数据！");
            return;
        }
        printData = arrangeData(result);
        Timestamp nowTime = TJDODBTool.getInstance().getDBTime();
        String prtName =  "煎药费统计表";
        String proName = "【PHADctWorkloadListControl】";
        String prtTime = StringTool.getString(nowTime,"yyyy/MM/dd HH:mm:ss");
        Timestamp startDate = (Timestamp) this.getValue("S_DATE");
        Timestamp endDate = (Timestamp) this.getValue("E_DATE");
        String staSection = "统计区间: " + StringTool.getString(startDate,
            "yyyy/MM/dd") + " ～ " + StringTool.getString(endDate,
            "yyyy/MM/dd");
        String prtDate = "制表时间:" + StringTool.getString(nowTime,
            "yyyy/MM/dd HH:mm:ss");
        TParm parm = new TParm();
        //=========pangben modify 20110418 start
        String region=this.getValueString("REGION_CODE");
        parm.setData("region","TEXT", region.equals("")||region==null?"区  域 :  所有医院":"区  域 :  "+printData.getValue("REGION_CHN_DESC",0));
        //=========pangben modify 20110418 stop
        parm.setData("prtName","TEXT", prtName);
        parm.setData("proName", "TEXT", proName);
        parm.setData("prtTime", "TEXT", prtTime);
        parm.setData("staSection", "TEXT", staSection);
        parm.setData("prtDate", "TEXT", prtDate);
        parm.setData("TABLE", printData.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\pha\\PHADctLoadChart.jhw", parm);
    }

    public TParm arrangeData(TParm parm) {
        TParm reDate = new TParm();
        DecimalFormat df = new DecimalFormat("#############0.00");
        int rowCount = parm.getCount();
        for (int i = 0; i < rowCount; i++) {
            //==========pangben modify 20110418 start
            reDate.addData("REGION_CHN_DESC", parm.getData("REGION_CHN_DESC", i));
            //==========pangben modify 20110418 start
            reDate.addData("QDATE", parm.getData("QDATE", i));
            reDate.addData("DEPT", parm.getData("DEPT", i));
            reDate.addData("DR", parm.getData("DR", i));
            reDate.addData("DCT_CASENUM", parm.getData("DCT_CASENUM", i));
            reDate.addData("DCT_RXNUM", parm.getData("DCT_RXNUM", i));
            double dctAmt = (double) parm.getDouble("DCT_AMT", i);
            reDate.addData("DCT_AMT", df.format(StringTool.round(dctAmt, 2)));
        }
        reDate.setCount(rowCount);
        //======pangben modify 20110418 start
        reDate.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
        //======pangben modify 20110418 start
        reDate.addData("SYSTEM", "COLUMNS", "QDATE");
        reDate.addData("SYSTEM", "COLUMNS", "DEPT");
        reDate.addData("SYSTEM", "COLUMNS", "DR");
        reDate.addData("SYSTEM", "COLUMNS", "DCT_CASENUM");
        reDate.addData("SYSTEM", "COLUMNS", "DCT_RXNUM");
        reDate.addData("SYSTEM", "COLUMNS", "DCT_AMT");
        return reDate;
    }
    public void onClear(){
        setValue("DEPT_CODE","");
        setValue("DR_CODE","");
        getTable("Table").removeRowAll();
    }
    public void onExcel() {
        ExportExcelUtil.getInstance().exportExcel(getTable("Table"), "门急诊药师处方工作量统计报表");
    }
}
