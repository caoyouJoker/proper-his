package com.javahis.ui.pha;

import com.dongyang.control.TControl;
import com.javahis.util.JavaHisDebug;
import com.dongyang.data.TParm;
import jdo.pha.PhaStatisticsTool;
import jdo.sys.Operator;
import com.dongyang.util.StringTool;
import java.text.DecimalFormat;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;
import com.dongyang.ui.TComboBox;
import java.util.Map;
import java.util.HashMap;
import jdo.sys.SYSRegionTool;

/**
 *
 * <p>Title: 门诊药房药师处方签工作量统计报表主档
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JAVAHIS
 *
 * @author ZangJH 2009.01.20
 * @version 1.0
 */


public class PHAApothecaryLoadChartControl
    extends TControl {

    //统计类型
    private String type = "";


    public void onInit() {
        super.onInit();
        myInit();
    }

    public void myInit() {
        //初始化工作
        this.callFunction("UI|mainTable|setLockColumns", "0,1,2,3,4,5,6,7");
        ((TComboBox)this.getComponent("REGION_CODE")).setValue(Operator.getRegion());
        ((TComboBox)this.getComponent("EXEC_DEPT_CODE")).setValue(Operator.getDept());
        //========pangben modify 20110421 start 权限添加
         this.callFunction("UI|REGION_CODE|setEnabled",SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110422 stop

    }


    //获得查询/打印需要显示在table上的数据
    public TParm getQueryDate() {
        TParm inParm = new TParm();
        //获得查询参数
        //门急诊查询，如果是全院就不挂ADM_TYPE条件
        if (this.getValueString("RadioO").equals("Y")) {
            //门诊标记是--O
            inParm.setData("ADM_TYPE", "O");
        }
        else if (this.getValueString("RadioE").equals("Y")) {
            //急诊标记是--E
            inParm.setData("ADM_TYPE", "E");
        }

        //获得起(00:00:00)/迄时间(23:59:59)
        Timestamp fromDate = (Timestamp) this.getValue("START_DATE");
        inParm.setData("START_DATE", fromDate);

        String tempEnd=this.getValue("END_DATE").toString();
        Timestamp toDate=StringTool.getTimestamp(tempEnd.substring(0,4)+tempEnd.substring(5,7)+tempEnd.substring(8,10)+"235959","yyyyMMddHHmmss");
        inParm.setData("END_DATE", toDate);

        String drugRoom = this.getValueString("EXEC_DEPT_CODE");
        if (!drugRoom.equals("")) {
            inParm.setData("EXEC_DEPT_CODE", drugRoom);
        }
        //================pangben modify 20110406 start 添加区域查询参数
        if(this.getValueString("REGION_CODE").length()!=0)
            inParm.setData("REGION_CODE",this.getValueString("REGION_CODE"));
        //================pangben modify 20110406 stop
        //调用后台查询相应的数据
        TParm result = PhaStatisticsTool.getInstance().getPhaStatisticsMainDate(
            inParm, type);
        countDctNum(result);
        return result;
    }



    private void countDctNum(TParm result){
        String admType = "";
        if(getValueString("RadioO").equals("Y"))
            admType = " AND ADM_TYPE = 'O'";
        else if (getValueString("RadioE").equals("Y"))
            admType = " AND ADM_TYPE = 'E'";
        String region = "";
        if(getValueString("REGION_CODE").length() != 0)
            region = " AND REGION_CODE = '"+getValueString("REGION_CODE")+"'";
        String data = "";
        //审核查询
        if (type.equals("审核")) {
            data = "CHECK";
        } //配药查询
        else if (type.equals("配药")) {
            data = "DOSAGE";
        } //发药查询
        else if (type.equals("发药")) {
            data = "DISPENSE";
        } //退药查询
        else if (type.equals("退药")) {
            data = "RETN";
        }
        for(int i = 0;i < result.getCount();i++){
            String SQL =
                    " SELECT SUM(A.TAKE_DAYS) TAKE_DAYS "+
                    " FROM (SELECT DISTINCT B.TAKE_DAYS,B.RX_NO " +
                    "       FROM OPD_ORDER B" +
                    "       WHERE TO_CHAR(B.PHA_"+data+"_DATE,'YYYY/MM/DD') = '"+result.getValue("QDATE",i)+"'"+
                    "       AND   EXEC_DEPT_CODE = '"+result.getValue("DEPT",i)+"'"+
                    "       AND   PHA_"+data+"_CODE = '"+result.getValue("PERSON",i)+"'"+
                    "       AND   DECOCT_DATE IS NOT NULL"+
                    "       " + admType + region+ "   ) A";
            TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
            result.setDataN("DCT_RXNUM",i,parm.getValue("TAKE_DAYS",0));
        }
    }



    public void onQuery() {

        //获得查询的类型
        type = this.getValueString("type");
        if (type.equals("")) {
            this.messageBox("请输入统计类型");
            return;
        }

        TParm tableDate = getQueryDate();
        if (tableDate.getCount() <= 0) {
            onClear();
            this.messageBox("该查询条件内无数据！");
            return;
        }
        //总金额
        double totalAmt = 0.0;
        //===========pangben modify 20110426 start
        int sumCount=0;//处方数
        int sumDctRxnum=0;//煎药人次
        int sumDctCasenum=0;//煎药付数
        //===========pangben modify 20110426 stop
        //int
        int count = tableDate.getCount();
        //循环累加
        for (int i = 0; i < count; i++) {
            double temp = tableDate.getDouble("CHARGE", i);
            //===========pangben modify 20110426 start
            sumCount +=tableDate.getInt("COUNT", i);
            sumDctRxnum+=tableDate.getInt("DCT_RXNUM", i);
            sumDctCasenum+=tableDate.getInt("DCT_CASENUM", i);
            //===========pangben modify 20110426 stop
            totalAmt += temp;
        }
        this.setValue("TOTFEE", totalAmt);
        //===========pangben modify 20110426 start
        tableDate.setData("QDATE", count, "");
        tableDate.setData("REGION_CHN_DESC", count, "总计:");
        tableDate.setData("DEPT", count, "");
        tableDate.setData("PERSON", count, "");
        tableDate.setData("COUNT", count, sumCount);
        tableDate.setData("DCT_RXNUM", count, sumDctRxnum);
        tableDate.setData("DCT_CASENUM", count, sumDctCasenum);
        tableDate.setData("CHARGE", count, totalAmt);
         //===========pangben modify 20110426 start
        //加载table上的数据
        this.callFunction("UI|mainTable|setParmValue", tableDate);

    }


    /**
     * 清空动作
     */
    public void onClear() {

        this.clearValue("drugRoom;type;TOTFEE");
        //清空table
        this.callFunction("UI|mainTable|removeRowAll");
        //选择‘全院’
        this.callFunction("UI|RadioALL|setSelected", true);
        //=========pangben modify 20110422 start
        this.setValue("REGION_CODE",Operator.getRegion());
         //=========pangben modify 20110422 stop
    }

    /**
     * 打印
     */
    public void onPrint() {
        //从table上获得打印的数据
        TParm printData = new TParm();
        //把数据装进parm
        TParm result = (TParm)this.callFunction("UI|mainTable|getShowParmValue");

        if (result.getCount() <= 0) {
            this.messageBox("无打印数据！");
            return;
        }
        //整理需要打印的数据（格式，大小...）
        printData = arrangeData(result);

        //获得当前时间
        Timestamp nowTime = TJDODBTool.getInstance().getDBTime();
        //==============pangben modify 20110419 start
        String region = this.getValueString("REGION_CODE");
        //报表名字
        String prtName = (region.equals("") || region == null ? "所有医院" :
                         printData.getValue("REGION_CHN_DESC", 0) )+ type +
                         "处方工作量统计表";
        //==============pangben modify 20110419 stop
        //程序名
        String proName = "【PHAApothecaryLoadChartControl】";
        //打印时间
        String prtTime = StringTool.getString(nowTime,"yyyy/MM/dd HH:mm:ss");
        //医院简称
//        String HospName = Manager.getOrganization().getHospitalCHNShortName(Operator.getRegion());

        Timestamp startDate = (Timestamp) this.getValue("START_DATE");
        Timestamp endDate = (Timestamp) this.getValue("END_DATE");
        //统计区间
        String staSection = "统计区间: " + StringTool.getString(startDate,
            "yyyy/MM/dd") + " ～ " + StringTool.getString(endDate,
            "yyyy/MM/dd");
        //制表时间
        String prtDate = "制表时间:" + StringTool.getString(nowTime,
            "yyyy/MM/dd HH:mm:ss");
        TParm parm = new TParm();
        parm.setData("prtName","TEXT", prtName);
        parm.setData("proName", "TEXT", proName);
        parm.setData("prtTime", "TEXT", prtTime);
        parm.setData("staSection", "TEXT", staSection);
        parm.setData("prtDate", "TEXT", prtDate);
        parm.setData("TABLE", printData.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\pha\\PHALoadChart.jhw", parm);


    }

    public TParm arrangeData(TParm parm) {
        //整理后的数据
        TParm reDate = new TParm();
        DecimalFormat df = new DecimalFormat("#############0.00");
        int rowCount = parm.getCount();

        //循环装载主数据
        for (int i = 0; i < rowCount; i++) {
            //=============pangben modify 20110418 start
            reDate.addData("REGION_CHN_DESC", parm.getData("REGION_CHN_DESC", i));
            //=============pangben modify 20110418 stop
            reDate.addData("DATE", parm.getData("QDATE", i));
            reDate.addData("DEPT", parm.getData("DEPT", i));
            reDate.addData("PERSON", parm.getData("PERSON", i));
            reDate.addData("COUNT", parm.getData("COUNT", i));
            double charge = (double) parm.getDouble("CHARGE", i);
            reDate.addData("CHARGE", df.format(StringTool.round(charge, 2)));
           // System.out.println("charge:"+df.format(StringTool.round(charge, 2)));
            reDate.addData("DCT_CASENUM", parm.getData("DCT_CASENUM", i));
            reDate.addData("DCT_RXNUM", parm.getData("DCT_RXNUM", i));
        }
        reDate.setCount(rowCount);
        //=============pangben modify 20110418 start
        reDate.addData("SYSTEM", "COLUMNS", "REGION_CHN_DESC");
        //=============pangben modify 20110418 stop
        reDate.addData("SYSTEM", "COLUMNS", "DATE");
        reDate.addData("SYSTEM", "COLUMNS", "DEPT");
        reDate.addData("SYSTEM", "COLUMNS", "PERSON");
        reDate.addData("SYSTEM", "COLUMNS", "COUNT");
        reDate.addData("SYSTEM", "COLUMNS", "CHARGE");
        reDate.addData("SYSTEM", "COLUMNS", "DCT_CASENUM");
        reDate.addData("SYSTEM", "COLUMNS", "DCT_RXNUM");
        return reDate;
    }

//-----------------------------导出EXCEL的方法---Start---------------------------
    /**
     * 导出EXCEL的方法
     */

    public void onExcel() {
        //得到UI对应控件对象的方法（UI|XXTag|getThis）
        TTable mainTable = (TTable) callFunction("UI|mainTable|getThis");
        ExportExcelUtil.getInstance().exportExcel(mainTable, "门急诊药师处方工作量统计报表");
    }

//-----------------------------导出EXCEL的方法---end-----------------------------


    //测试用例
    public static void main(String[] args) {

        //JavaHisDebug.initClient();
        //JavaHisDebug.initServer();
//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("pha\\PHAApothecaryLoadChart.x");
    }

}
