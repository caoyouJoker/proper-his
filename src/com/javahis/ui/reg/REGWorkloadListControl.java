package com.javahis.ui.reg;

import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.dongyang.control.TControl;
import java.text.DecimalFormat;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.jdo.TJDODBTool;
import java.util.Vector;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title: 挂号员工作量统计查询</p>
 *
 * <p>Description: 挂号员工作量统计查询</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.08.27
 * @version 1.0
 */
public class REGWorkloadListControl extends TControl {
    TParm printData; //===========pangben modify 20110425
    public void onInit() {
        super.onInit();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
        initPage();
        //========pangben modify 20110421 start 权限添加
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop

    }

    /**
     * 行双击事件
     * @param row int
     */
    public void onTableDoubleClicked(int row) {
        if (row < 0)
            return;
        onDetial();
    }

    /**
     * 初始化界面
     */
    public void initPage() {

        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        setValue("S_DATE", yesterday);
        setValue("E_DATE", SystemTool.getInstance().getDate());
        this.setValue("S_TIME", StringTool.getTimestamp("00:00:00", "hh:mm:ss")); //add by huangtt 20150821
        this.setValue("E_TIME", StringTool.getTimestamp("23:59:59", "hh:mm:ss"));//add by huangtt 20150821
        setValue("CASH_CODE", Operator.getID());
        setValue("REGION_CODE", Operator.getRegion());
        TParm parm = new TParm();
        this.callFunction("UI|Table|setParmValue", parm);
//        this.callFunction("UI|Table|removeRowAll");
    }

    /**
     * 打印
     */
    public void onPrint() {
        print();
    }

    /**
     * 调用报表打印预览界面
     */
    private void print() {
        int rowCount = printData.getCount("REGION_CHN_ABN");
        if (rowCount < 1) {
            this.messageBox("先查询数据!");
            return;
        }
        TParm T1 = new TParm(); //表格数据
        TTable table = ((TTable)this.getComponent("Table"));
        for (int i = 0; i < rowCount; i++) {
            T1.addRowData(printData, i);
        }
        T1.setCount(rowCount);
        String[] chage = table.getParmMap().split(";");
        for (int i = 0; i < chage.length; i++) {
            T1.addData("SYSTEM", "COLUMNS", chage[i]);
        }

        //===========pangben modify 20110425 start 将表格中的数据打印出来不需要再次查询一次
//        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
//            "S_DATE")), "yyyyMMdd");
//        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
//            "E_DATE")), "yyyyMMdd");
        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy/MM/dd hh:mm:ss");
//        TParm printData = this.getPrintDate(startTime, endTime);
        //===========pangben modify 20110425 stop
        String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
                "S_DATE")), "yyyy/MM/dd") + " " + this.getValue("S_TIME");
        String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
                "E_DATE")), "yyyy/MM/dd") + " " + this.getValue("E_TIME");
        TParm parm = new TParm();
        //========pangben modify 20110328 start
        String region = table.getParmValue().getRow(0).getValue(
                "REGION_CHN_ABN");
        parm.setData("TITLE", "TEXT",
                     (this.getValue("REGION_CODE") != null &&
                      !this.getValue("REGION_CODE").equals("") ? region :
                      "所有医院") + "挂号员工作量统计报表");
        //========pangben modify 20110328 stop
        parm.setData("S_DATE", "TEXT", sDate);
        parm.setData("E_DATE", "TEXT", eDate);
        parm.setData("OPT_USER", "TEXT", Operator.getName());
        parm.setData("OPT_DATE", "TEXT", sysDate);
        //System.out.println("table:"+table.getData());
        parm.setData("workloadtable", T1.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\REG\\REGWorkloadList.jhw",
                             parm);

    }

    /**
     * 整理打印数据
     * @param startTime String
     * @param endTime String
     * @return TParm
     */
    private TParm getPrintDate(String startTime, String endTime) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        TParm selParm = new TParm();
        String admTypeWhere = "";
        if (getValue("ADM_TYPE").toString().length() != 0)
            admTypeWhere = " AND A.ADM_TYPE = '" + getValue("ADM_TYPE") +
                           "'  ";
        String cashCodeWhere = "";
        String cashCodeWhereR ="";
        if (getValue("CASH_CODE").toString().length() != 0)
            cashCodeWhere = " AND A.CASH_CODE = '" + getValue("CASH_CODE") +
                            "'  ";
        	cashCodeWhereR = " AND B.CASH_CODE = '" + getValue("CASH_CODE") +
                "'  ";
        //================pangben modify 20110408 start
        String reqion = "";
        if (this.getValueString("REGION_CODE").length() != 0)
            reqion = " AND A.REGION_CODE= '" + this.getValue("REGION_CODE") +
                     "' ";
        //================pangben modify 20110408 stop  
        String sql =
                " SELECT REGTABLE.REGION_CHN_ABN,REGTABLE.CASH_CODE, (SUM (REGTABLE.UN_REG_COUNT)+SUM (REGTABLE.REG_COUNT)) REG_COUNT, " +
                "        SUM (REGTABLE.REG_FEE) REG_FEE, " +
                "        SUM (REGTABLE.CLINIC_FEE) CLINIC_FEE, SUM (REGTABLE.AR_AMT) AR_AMT, " +
                "        SUM (REGTABLE.UN_REG_COUNT) UN_REG_COUNT, " +
                "        SUM (REGTABLE.UN_REG_FEE) UN_REG_FEE," +
                "        SUM (REGTABLE.UN_CLINIC_FEE) UN_CLINIC_FEE," +
                "        SUM (REGTABLE.UN_AR_AMT) UN_AR_AMT, " +
                "        (SUM (REGTABLE.AR_AMT)+SUM (REGTABLE.UN_AR_AMT)) TOT_AMT,REGTABLE.USER_NAME " +
                "   FROM (SELECT B.REGION_CHN_ABN,A.CASH_CODE, COUNT (A.CASE_NO) REG_COUNT, SUM (A.REG_FEE) REG_FEE," +
                "                SUM (A.CLINIC_FEE) CLINIC_FEE, SUM (A.AR_AMT) AS AR_AMT," +
                "                0 AS UN_REG_COUNT, 0 AS UN_REG_FEE, 0 AS UN_CLINIC_FEE," +
                "                0 AS UN_AR_AMT,C.USER_NAME " +
                "           FROM BIL_REG_RECP A,SYS_REGION B,SYS_OPERATOR C,REG_PATADM D " + //shibl 20121012 add 过滤退挂人数
                "          WHERE A.CASH_CODE=C.USER_ID AND A.AR_AMT >=0 AND A.REGION_CODE=B.REGION_CODE AND A.CASE_NO=D.CASE_NO AND D.REGCAN_USER IS NULL " +
                reqion + //======pangben modify 20110408"
                "            AND A.BILL_DATE BETWEEN TO_DATE('" + startTime +
//                "000000" + //===delete by huangtt 20150821
                "','yyyyMMddHH24miss')  " +
                "            AND TO_DATE('" + endTime + 
//                "235959" + //===delete by huangtt 20150821
                "','yyyyMMddHH24miss') " + cashCodeWhere + " " +
                admTypeWhere +
                "          GROUP BY A.CASH_CODE,B.REGION_CHN_ABN,C.USER_NAME " +
                "          UNION ALL" +
                "         SELECT B.REGION_CHN_ABN,A.CASH_CODE, 0, 0, 0, 0, COUNT (A.CASE_NO) UN_REG_COUNT," +
                "                SUM (A.REG_FEE) UN_REG_FEE, SUM (A.CLINIC_FEE) UN_CLINIC_FEE," +
                "                SUM (A.AR_AMT) AS UN_AR_AMT,C.USER_NAME " +
                "           FROM BIL_REG_RECP A,SYS_REGION B,SYS_OPERATOR C,REG_PATADM D  " +//shibl 20121012 add 退挂人数条件修改
                "          WHERE A.CASH_CODE=C.USER_ID AND A.AR_AMT <=0 AND A.REGION_CODE=B.REGION_CODE AND A.CASE_NO=D.CASE_NO AND D.REGCAN_USER IS NOT NULL  " +
                reqion + //======pangben modify 20110408"
                "            AND A.BILL_DATE BETWEEN TO_DATE('" + startTime +
//                "000000" + 
                "','yyyyMMddHH24miss') " +
                "            AND TO_DATE('" + endTime + 
//                "235959" +
                "','yyyyMMddHH24miss') " + cashCodeWhere + " " +
                admTypeWhere +
                "       GROUP BY C.USER_NAME,A.CASH_CODE,B.REGION_CHN_ABN) REGTABLE " +
                " GROUP BY REGTABLE.USER_NAME,REGTABLE.CASH_CODE,REGTABLE.REGION_CHN_ABN ORDER BY REGTABLE.REGION_CHN_ABN";

//        System.out.println("sql::::::::::::::::::::::::::" + sql);
        selParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (selParm.getCount("CASH_CODE") < 1) {
            this.messageBox("查无数据");
            this.initPage();
            return selParm;
        }
        //===========pangben modify 20110425 start 添加合计显示
        // 挂号次数
        int sumRegCount = 0;
        //挂号费
        double sumRegFee = 0.00;
        //诊查费
        double sumClinicFee = 0.00;
        //挂号收入小计
        double sumArAmt = 0.00;
        //退挂次数
        double sumUnRegCount = 0.00;
        //挂号退费
        double sumUnRegFee = 0.00;
        //诊查费退费
        double sumUnClinicFee = 0.00;
        //挂号退费小计
        double sumUnArAmt = 0.00;
        //总计
        double sumUnTotAmt = 0.00;
        //初诊次数
        int initClinicSum =0;
        //复诊次数
        int twiceClinicSum =0;
        TParm initCountParm= new TParm();
        TParm twiceCountParm= new TParm();
        String countSql = "";
        //===========pangben modify 20110425 stop
        int count = selParm.getCount("CASH_CODE");
        //CASH_CODE;REG_COUNT;REG_FEE;CLINIC_FEE;AR_AMT;UN_REG_COUNT;UN_REG_FEE;UN_CLINIC_FEE;UN_AR_AMT;TOT_AMT
        for (int i = 0; i < count; i++) {
            int regCount = selParm.getInt("REG_COUNT", i);
            double regFee = selParm.getDouble("REG_FEE", i);
            double clinicFee = selParm.getDouble("CLINIC_FEE", i);
            double arAmt = selParm.getDouble("AR_AMT", i);
            int unRegCount = selParm.getInt("UN_REG_COUNT", i);
            double unRegFee = selParm.getDouble("UN_REG_FEE", i);
            double unClinicFee = selParm.getDouble("UN_CLINIC_FEE", i);
            double unArAmt = selParm.getDouble("UN_AR_AMT", i);
            double unTotAmt = selParm.getDouble("TOT_AMT", i);
            countSql =
                    " SELECT COUNT (A.CASE_NO) AS COUNT "+
                    "   FROM REG_PATADM A, BIL_REG_RECP B "+
                    "  WHERE B.CASH_CODE = '"+selParm.getValue("CASH_CODE",i)+"' "+
                  //add by huangtt 20140929 start  
                    "	 AND A.ARRIVE_FLG = 'Y' " +  
                    "    AND A.REGCAN_USER IS NULL" +
                    "    AND B.BILL_DATE BETWEEN TO_DATE('" + startTime +
//                    "000000" + 
                    "','yyyyMMddHH24miss') " +
                    "        AND TO_DATE('" + endTime + 
//                    "235959" +
                    "','yyyyMMddHH24miss') " +
            		//add by huangtt 20140929 end
                    "    AND A.CASE_NO = B.CASE_NO "
                    + reqion +" "+cashCodeWhereR + " " +
                    admTypeWhere ;
//                    "    AND B.AR_AMT >= 0 ";
            String where1 = "    AND A.VISIT_CODE = 0 ";
            String where2 = "    AND A.VISIT_CODE = 1 ";
            initCountParm = new TParm(TJDODBTool.getInstance().select(countSql+where1));
            int initClinic =initCountParm.getInt("COUNT",0);
            twiceCountParm= new TParm(TJDODBTool.getInstance().select(countSql+where2));
            int twiceClinic =twiceCountParm.getInt("COUNT",0);
            //============pangben modify 20110425 start 累计
            sumRegCount += regCount;
            sumRegFee += StringTool.round(regFee, 2);
            sumClinicFee += StringTool.round(clinicFee, 2);
            sumArAmt += StringTool.round(arAmt, 2);
            sumUnRegCount += unRegCount;
            sumUnRegFee += StringTool.round(unRegFee, 2);
            sumUnClinicFee += StringTool.round(unClinicFee, 2);
            sumUnArAmt += StringTool.round(unArAmt, 2);
            sumUnTotAmt += StringTool.round(unTotAmt, 2);
            initClinicSum=initClinicSum+initClinic;
            twiceClinicSum = twiceClinicSum +twiceClinic;
            //==格式化
            selParm.setData("REG_FEE", i, df.format(StringTool.round(regFee, 2)));
            selParm.setData("CLINIC_FEE", i,
                            df.format(StringTool.round(clinicFee, 2)));
            selParm.setData("AR_AMT", i, df.format(StringTool.round(arAmt, 2)));
            selParm.setData("UN_REG_FEE", i,
                            df.format(StringTool.round(unRegFee, 2)));
            selParm.setData("UN_CLINIC_FEE", i,
                            df.format(StringTool.round(unClinicFee, 2)));
            selParm.setData("UN_AR_AMT", i,
                            df.format(StringTool.round(unArAmt, 2)));
            selParm.setData("TOT_AMT", i,
                            df.format(StringTool.round(unTotAmt, 2)));
            selParm.setData("INIT_CLINIC", i,initClinic);
            selParm.setData("TWICE_CLINIC", i,twiceClinic);
//            //============pangben modify 20110425 stop
        }
        //===========pangben modify 20110425 start
        selParm.setData("REGION_CHN_ABN", count, "总计：");
        selParm.setData("USER_NAME", count, "");
        selParm.setData("REG_COUNT", count, sumRegCount);
        selParm.setData("REG_FEE", count, df.format(sumRegFee));
        selParm.setData("CLINIC_FEE", count, df.format(sumClinicFee));
        selParm.setData("AR_AMT", count, df.format(sumArAmt));
        selParm.setData("UN_REG_COUNT", count, sumUnRegCount);
        selParm.setData("UN_REG_FEE", count, df.format(sumUnRegFee));
        selParm.setData("UN_CLINIC_FEE", count, df.format(sumUnClinicFee));
        selParm.setData("UN_AR_AMT", count, df.format(sumUnArAmt));
        selParm.setData("TOT_AMT", count, df.format(sumUnTotAmt));
        selParm.setData("INIT_CLINIC", count,""+initClinicSum);
        selParm.setData("TWICE_CLINIC", count,""+twiceClinicSum);
        //===========pangben modify 20110425 stop
        this.callFunction("UI|Table|setParmValue", selParm);
        return selParm;
    }

    /**
     * 查询
     */
    public void onQuery() {
        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                "S_DATE")), "yyyyMMdd")+StringTool.getString( (Timestamp)this.getValue("S_TIME"),"HHmmss");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                "E_DATE")), "yyyyMMdd")+StringTool.getString( (Timestamp)this.getValue("E_TIME"),"HHmmss");
        printData = this.getPrintDate(startTime, endTime);
    }

    /**
     * 汇出Excel
     */
    public void onExport() {

        //得到UI对应控件对象的方法（UI|XXTag|getThis）
        TTable table = (TTable) callFunction("UI|Table|getThis");
        ExportExcelUtil.getInstance().exportExcel(table, "挂号工作量统计报表");
    }

    /**
     * 清空
     */
    public void onClear() {
        initPage();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();

    }

    /**
     * 明细
     */
    public void onDetial() {
        TTable table = (TTable)this.getComponent("Table");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            this.messageBox("请点选行数据!");
        }
        TParm tableParm = table.getParmValue();
        String count = tableParm.getValue("REG_COUNT", selRow);
        String cashCode = tableParm.getValue("CASH_CODE", selRow);
        Vector vct = new Vector();
        vct.add(0, getValue("S_DATE"));
        vct.add(1, getValue("E_DATE"));
        vct.add(2, cashCode);
        vct.add(3, count) ;
        this.openDialog("%ROOT%\\config\\reg\\REGWorkloadDetial.x", vct);
    }


}
