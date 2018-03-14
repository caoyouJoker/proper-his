package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.util.Date;
import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
* <p>Title: 预约挂号月统计表</p>
*
* <p>Description:预约挂号月统计表 </p>
*
* <p>Copyright: Copyright (c) </p>
*
* <p>Company:bluecore </p>
*
* @author huangtt 20131104
* @version 1.0
*/
public class REGOrdAdmMothlyControl
        extends TControl {

    private static TTable table;

    /**
     * 初始化
     */
    public void onInit() {
        table = (TTable) getComponent("TABLE");
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("START_DATE", StringTool.rollDate(date, -30).toString().substring(0, 10)
                .replace('-', '/')
                + " 00:00:00");
        this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
    }

    /**
     * 查询
     */
    public void onQuery() {
        String deptCode = this.getValueString("DEPT_CODE");
//        if (StringUtil.isNullString(deptCode)) {
//            this.messageBox("请选择科室");
//            return;
//        }
        String date_s = getValueString("START_DATE");
        String date_e = getValueString("END_DATE");
        date_s =
                date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "").replace("-", "")
                        .replace(" ", "");
        date_e =
                date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "").replace("-", "")
                        .replace(" ", "");
        String regSql =
                "SELECT A.DR_CODE,A.CLINICTYPE_CODE,COUNT(A.MR_NO) REG_QUE,A.DEPT_CODE "
                        + " FROM REG_PATADM A,BIL_REG_RECP B WHERE 1=1 # AND B.BILL_DATE BETWEEN TO_DATE ('" + date_s
                        + "','YYYYMMDDHH24MISS') AND TO_DATE ('" + date_e
                        + "','YYYYMMDDHH24MISS') AND A.REGCAN_USER IS NULL AND A.REGCAN_DATE IS NULL "
                        + " AND A.CASE_NO(+) = B.CASE_NO"
                        + " GROUP BY A.DR_CODE,A.CLINICTYPE_CODE,A.DEPT_CODE ORDER BY A.CLINICTYPE_CODE";
        if (StringUtil.isNullString(deptCode)) {// wanglong add 20141104
            regSql = regSql.replaceFirst("#", "");
        } else {
            regSql = regSql.replaceFirst("#", " AND A.DEPT_CODE='" + deptCode + "' ");
        }
        // System.out.println("regSql==="+regSql);
        TParm regResult = new TParm(TJDODBTool.getInstance().select(regSql));
       
        String orderSql =
                "SELECT DR_CODE,CLINICTYPE_CODE,COUNT(MR_NO) ORDER_QUE,'' ORDER_RATE,DEPT_CODE "
                        + " FROM REG_PATADM WHERE 1=1 # AND ADM_DATE BETWEEN TO_DATE ('" + date_s
                        + "','YYYYMMDDHH24MISS') AND TO_DATE ('" + date_e
                        + "','YYYYMMDDHH24MISS') AND REGCAN_USER IS NULL "
                        + " AND REGCAN_DATE IS NULL AND APPT_CODE='Y' "
//                        + " AND ADM_TYPE='O' "
                        + " GROUP BY DR_CODE,CLINICTYPE_CODE,DEPT_CODE ORDER BY CLINICTYPE_CODE";
        if (StringUtil.isNullString(deptCode)) { // wanglong add 20141104
            orderSql = orderSql.replaceFirst("#", "");
        } else {
            orderSql = orderSql.replaceFirst("#", " AND DEPT_CODE='" + deptCode + "' ");
        }
        // System.out.println("orderSql==="+orderSql);
        TParm orderResult = new TParm(TJDODBTool.getInstance().select(orderSql));
       
        String missSql =
                "SELECT DR_CODE,CLINICTYPE_CODE,COUNT(MR_NO) MISS_QUE,'' MISS_RATE,DEPT_CODE "
                        + " FROM REG_PATADM WHERE 1=1 # AND ADM_DATE BETWEEN TO_DATE ('" + date_s
                        + "','YYYYMMDDHH24MISS') AND TO_DATE ('" + date_e
                        + "','YYYYMMDDHH24MISS') AND REGCAN_USER IS NULL AND REGCAN_DATE IS NULL "
                        + " AND APPT_CODE='Y' AND ARRIVE_FLG = 'N' "
//                        + " AND ADM_TYPE='O' "
                        + " GROUP BY DR_CODE,CLINICTYPE_CODE,DEPT_CODE ORDER BY CLINICTYPE_CODE";
        if (StringUtil.isNullString(deptCode)) {// wanglong add 20141104
            missSql = missSql.replaceFirst("#", "");
        } else {
            missSql = missSql.replaceFirst("#", " AND DEPT_CODE='" + deptCode + "' ");
        }
        
        TParm missResult = new TParm(TJDODBTool.getInstance().select(missSql));
        
        String visitSql = "SELECT A.VISIT_CODE, "+
                "B.VISIT_CODE_1, "+
                "A.DR_CODE, "+
                "A.CLINICTYPE_CODE, "+
                "A.DEPT_CODE "+
           "FROM (  SELECT COUNT (VISIT_CODE) VISIT_CODE, "+
                          "DR_CODE, "+
                          "CLINICTYPE_CODE, "+
                          "DEPT_CODE "+
                     "FROM REG_PATADM "+
                    "WHERE 1=1 # AND VISIT_CODE = '1' "+
                          "AND ADM_DATE BETWEEN TO_DATE ('"+date_s+"', "+
                                                        "'YYYYMMDDHH24MISS') "+
                                           "AND TO_DATE ('"+date_e+"', "+
                                                        "'YYYYMMDDHH24MISS') "+
                 "GROUP BY DR_CODE, CLINICTYPE_CODE, DEPT_CODE) A, "+
                "(  SELECT COUNT (VISIT_CODE) VISIT_CODE_1, "+
                          "DR_CODE, "+
                          "CLINICTYPE_CODE, "+
                          "DEPT_CODE "+
                     "FROM REG_PATADM "+
                    "WHERE 1=1 # AND VISIT_CODE = '1' "+
                          "AND ADM_DATE BETWEEN TO_DATE ('"+date_s+"', "+
                                                        "'YYYYMMDDHH24MISS') "+
                                           "AND TO_DATE ('"+date_e+"', "+
                                                        "'YYYYMMDDHH24MISS') "+
                          "AND APPT_CODE = 'Y' "+
                 "GROUP BY DR_CODE, CLINICTYPE_CODE, DEPT_CODE) B "+
          "WHERE     A.DR_CODE = B.DR_CODE(+) "+
                "AND A.CLINICTYPE_CODE = B.CLINICTYPE_CODE(+) "+
                "AND A.DEPT_CODE = B.DEPT_CODE(+) "+
       "ORDER BY CLINICTYPE_CODE ";
        if (StringUtil.isNullString(deptCode)) {
        	visitSql = visitSql.replaceAll("#", "");
        } else {
        	visitSql = visitSql.replaceAll("#", " AND DEPT_CODE='" + deptCode + "' ");
        }
        TParm visitResult = new TParm(TJDODBTool.getInstance().select(visitSql));
        //end wuxy 20170731
        TParm tabParm = new TParm();
        for (int i = 0; i < regResult.getCount(); i++) {
            tabParm.addData("DR_CODE", regResult.getValue("DR_CODE", i));
            tabParm.addData("CLINICTYPE_CODE", regResult.getValue("CLINICTYPE_CODE", i));
            tabParm.addData("REG_QUE", regResult.getValue("REG_QUE", i));
            tabParm.addData("DEPT_CODE", regResult.getValue("DEPT_CODE", i));
            tabParm.addData("ORDER_QUE", 0);
            tabParm.addData("ORDER_RATE", 0);
            tabParm.addData("MISS_QUE", 0);
            tabParm.addData("MISS_RATE", 0);
            tabParm.addData("VISIT_CODE", 0);
            tabParm.addData("VISIT_CODE_1", 0);
            // System.out.println(tabParm);
            
            //获取复诊人数，以及预约复诊人数 add BY wuxy 20170731
            for (int h = 0; h < visitResult.getCount(); h++) {
                if (regResult.getValue("DR_CODE", i).equals(visitResult.getValue("DR_CODE", h))
                		&& regResult.getValue("DEPT_CODE", i).equals(visitResult.getValue("DEPT_CODE", h))  //add by huangtt 20150403
                        && regResult.getValue("CLINICTYPE_CODE", i)
                                .equals(visitResult.getValue("CLINICTYPE_CODE", h))) {
                    tabParm.setData("VISIT_CODE", i, visitResult.getValue("VISIT_CODE", h));
                    tabParm.setData("VISIT_CODE_1", i, visitResult.getValue("VISIT_CODE_1", h));

                }
            }
            //end wuxy 20170731
            for (int j = 0; j < orderResult.getCount(); j++) {
                if (regResult.getValue("DR_CODE", i).equals(orderResult.getValue("DR_CODE", j))
                		&& regResult.getValue("DEPT_CODE", i).equals(orderResult.getValue("DEPT_CODE", j))  //add by huangtt 20150403
                        && regResult.getValue("CLINICTYPE_CODE", i)
                                .equals(orderResult.getValue("CLINICTYPE_CODE", j))) {
                    tabParm.setData("ORDER_QUE", i, orderResult.getValue("ORDER_QUE", j));
                    double orderQue = orderResult.getDouble("ORDER_QUE", j);
                    double regQue = regResult.getDouble("REG_QUE", i);
                    double orderRate = orderQue / regQue;
                    String oRate = StringTool.round(orderRate, 4) + "";
                    tabParm.setData("ORDER_RATE", i,
                                    oRate + StringTool.fill("0", 6 - oRate.length()));
                }
            }
            for (int k = 0; k < missResult.getCount(); k++) {
                if (regResult.getValue("DR_CODE", i).equals(missResult.getValue("DR_CODE", k))
                		&& regResult.getValue("DEPT_CODE", i).equals(missResult.getValue("DEPT_CODE", k)) //add by huangtt 20150403
                        && regResult.getValue("CLINICTYPE_CODE", i)
                                .equals(missResult.getValue("CLINICTYPE_CODE", k))) {
                	tabParm.setData("MISS_QUE", i, missResult.getValue("MISS_QUE", k));
                	//add by huangtt start 20141225
                    tabParm.setData("REG_QUE", i, tabParm.getInt("REG_QUE", i) + missResult.getInt("MISS_QUE", k)); //add by huangtt 20141225                    
                    double orderQue = tabParm.getDouble("ORDER_QUE", i);
                    double regQue = tabParm.getDouble("REG_QUE", i);
                    double orderRate = orderQue / regQue;
                    String oRate = StringTool.round(orderRate, 4) + "";
                    tabParm.setData("ORDER_RATE", i,
                                    oRate + StringTool.fill("0", 6 - oRate.length()));
                    //add by huangtt end 20141225
                    double missQue = missResult.getDouble("MISS_QUE", k);
//                    double orderQue = tabParm.getDouble("ORDER_QUE", i); 
                    if (orderQue > 0) {
                        double missRate = missQue / orderQue;
                        String mRate = StringTool.round(missRate, 4) + "";
                        tabParm.setData("MISS_RATE", i,
                                        mRate + StringTool.fill("0", 6 - mRate.length()));
                    } else {
                        tabParm.setData("MISS_RATE", i, 0);
                    }
                }
            }
        }
        if (tabParm.getCount("DR_CODE") < 0) {
            this.messageBox("没有要查询的数据！");
            onClear();
            return;
        }
        table.setParmValue(tabParm);
    }

    /**
     * 打印
     */
    public void onPrint() {
        String date_s = getValueString("START_DATE");
        String date_e = getValueString("END_DATE");
        date_s =
                date_s.substring(0, 10).replace(":", "").replace("-", "/").replace(" ", "")
                        + " 00:00:00";
        date_e =
                date_e.substring(0, 10).replace(":", "").replace("-", "/").replace(" ", "")
                        + " 23:59:59";
        table.acceptText();
        TParm tblParm = table.getShowParmValue();
        if (table.getRowCount() <= 0) {
            this.messageBox("没有打印数据");
            return;
        }
        TParm data = new TParm();
        data.setData("TITLE", "TEXT", "预约挂号月统计表");
        data.setData("DATE", "TEXT", "查询时间：" + date_s + "～" + date_e);
        TTextFormat dept = (TTextFormat) getComponent("DEPT_CODE");
        data.setData("DEPT", "TEXT", "科室：" + dept.getText());
        Timestamp date = StringTool.getTimestamp(new Date());
        data.setData("USER_DATE", "TEXT",
                     "制表时间：" + date.toString().substring(0, 10).replace('-', '/'));
        data.setData("USER", "TEXT", "制表人：" + Operator.getName());
        TParm tbl1 = new TParm();
        TParm tbl2 = new TParm();
        double reg = 0;
        double order = 0;
        double miss = 0;
        for (int i = 0; i < tblParm.getCount("DR_CODE"); i++) {
            if (tblParm.getValue("CLINICTYPE_CODE", i).equals("主任医师")) {
                tbl1.addData("CLINICTYPE_CODE", "");
                tbl1.addData("DR_CODE", tblParm.getValue("DR_CODE", i));
                tbl1.addData("REG_QUE", tblParm.getValue("REG_QUE", i));
                tbl1.addData("ORDER_QUE", tblParm.getValue("ORDER_QUE", i));
                tbl1.addData("ORDER_RATE", tblParm.getValue("ORDER_RATE", i));
                tbl1.addData("MISS_QUE", tblParm.getValue("MISS_QUE", i));
                tbl1.addData("MISS_RATE", tblParm.getValue("MISS_RATE", i));
            } else {
                tbl2.addData("CLINICTYPE_CODE", "");
                tbl2.addData("DR_CODE", tblParm.getValue("DR_CODE", i));
                tbl2.addData("REG_QUE", tblParm.getValue("REG_QUE", i));
                tbl2.addData("ORDER_QUE", tblParm.getValue("ORDER_QUE", i));
                tbl2.addData("ORDER_RATE", tblParm.getValue("ORDER_RATE", i));
                tbl2.addData("MISS_QUE", tblParm.getValue("MISS_QUE", i));
                tbl2.addData("MISS_RATE", tblParm.getValue("MISS_RATE", i));
            }
            reg = reg + tblParm.getDouble("REG_QUE", i);
            order = order + tblParm.getDouble("ORDER_QUE", i);
            miss = miss + tblParm.getDouble("MISS_QUE", i);
        }
        tbl1.setData("CLINICTYPE_CODE", 0, "专家号医生");
        tbl2.setData("CLINICTYPE_CODE", 0, "普通号医生");
        tbl2.addData("DR_CODE", "");
        tbl2.addData("CLINICTYPE_CODE", "科室月总计");
        tbl2.addData("REG_QUE", (int) reg);
        tbl2.addData("ORDER_QUE", (int) order);
        String orderRate = StringTool.round(order / reg, 4) + "";
        tbl2.addData("ORDER_RATE", orderRate + StringTool.fill("0", 6 - orderRate.length()));
        tbl2.addData("MISS_QUE", (int) miss);
        String missRate = StringTool.round(miss / reg, 4) + "";
        tbl2.addData("MISS_RATE", missRate + StringTool.fill("0", 6 - missRate.length()));
        tbl1.setCount(tbl1.getCount("DR_CODE"));
        tbl1.addData("SYSTEM", "COLUMNS", "CLINICTYPE_CODE");
        tbl1.addData("SYSTEM", "COLUMNS", "DR_CODE");
        tbl1.addData("SYSTEM", "COLUMNS", "REG_QUE");
        tbl1.addData("SYSTEM", "COLUMNS", "ORDER_QUE");
        tbl1.addData("SYSTEM", "COLUMNS", "ORDER_RATE");
        tbl1.addData("SYSTEM", "COLUMNS", "MISS_QUE");
        tbl1.addData("SYSTEM", "COLUMNS", "MISS_RATE");
        tbl2.setCount(tbl2.getCount("DR_CODE"));
        tbl2.addData("SYSTEM", "COLUMNS", "CLINICTYPE_CODE");
        tbl2.addData("SYSTEM", "COLUMNS", "DR_CODE");
        tbl2.addData("SYSTEM", "COLUMNS", "REG_QUE");
        tbl2.addData("SYSTEM", "COLUMNS", "ORDER_QUE");
        tbl2.addData("SYSTEM", "COLUMNS", "ORDER_RATE");
        tbl2.addData("SYSTEM", "COLUMNS", "MISS_QUE");
        tbl2.addData("SYSTEM", "COLUMNS", "MISS_RATE");
        data.setData("TABLE", tbl1.getData());
        data.setData("TABLE1", tbl2.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\reg\\REGOrdAdmMothlyPrint.jhw", data);
    }

    /**
     * 清空
     */
    public void onClear() {
        table.removeRowAll();
        clearValue("DEPT_CODE");
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("START_DATE", StringTool.rollDate(date, -30).toString().substring(0, 10)
                .replace('-', '/')
                + " 00:00:00");
        this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
    }

    /**
     * 导出Excel
     * */
    public void onExport() {
        // 得到UI对应控件对象的方法（UI|XXTag|getThis）
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        ExportExcelUtil.getInstance().exportExcel(table, "预约挂号月统计表");
    }
}
