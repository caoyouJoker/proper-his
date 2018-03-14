package com.javahis.ui.mro;

import com.dongyang.control.*;
import jdo.sys.SystemTool;
import java.util.Calendar;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import jdo.sys.Operator;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: 药库药品收益占总收入的比例</p>
 *
 * <p>Description: 药库药品收益占总收入的比例</p>
 *
 * <p>Copyright: Copyright (c) 深圳中航 2011</p>
 *
 * <p>Company: javahis </p>
 * com.javahis.ui.mro.MROPhaProfitControl
 * @author ZhenQin
 * @version 4.0
 */
public class MROPhaProfitControl
    extends TControl {


    /**
     * 用于显示数据的TAble
     */
    private TTable table = null;

    private List headerList = null;
    /**
     * 构造方法
     */
    public MROPhaProfitControl() {
        super();
    }

    /**
     * 初始化,时间
     * 开始日期是前一月的1号,结束日期是当前月的1号
     */
    public void onInit(){
        initDate();
        table = (TTable)this.getComponent("tTable_1");
        String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'STA_CHARGE' ORDER BY ID ";
        //查询
        TParm parm = new TParm(
              TJDODBTool.getInstance().select(sql)
        );
        //表头
        StringBuilder header = new StringBuilder();
        //表列的对应的TParm数据类型
        StringBuilder parmMap = new StringBuilder();
        //对其方式
        StringBuilder alignment = new StringBuilder();
        //表列标题
        header.append("科室,100;医师,85");
        //表列数据
        parmMap.append("DEPT_CHN_DESC;USER_NAME");
        //#####0.000%
        alignment.append("0,left;1,left");
        headerList = new ArrayList();
        if(parm.getCount("ID") > 0){
            int i = 0;
            for(i = 0; i < parm.getCount("ID"); i++){
                //添加一个分号用来隔离前一个
                header.append(";");
                //添加一个head,同时对应一个map
                header.append(parm.getValue("CHN_DESC", i));
                //添加表格列显示宽度
                header.append(",80");
                alignment.append(";" + (i * 2 + 2) + ",right");

                //给map添加一个数据类型
                parmMap.append(";DATA" + parm.getValue("ID", i));
                headerList.add("DATA" + parm.getValue("ID", i));

                //添加一个分号用来隔离前一个
                header.append(";");
                //添加一个head,同时对应一个map
                header.append(parm.getValue("CHN_DESC", i));
                header.append("占总收益比例");
                //添加表格列显示宽度
                header.append(",120");
                alignment.append(";" + (i * 2 + 3) + ",right");

                //给map添加一个数据类型
                parmMap.append(";DATA_" + parm.getValue("ID", i));
                headerList.add("DATA_" + parm.getValue("ID", i));
            }
            alignment.append(";" + (i * 2 + 2) + ",right");
        }
        header.append(";总收益,60");
        parmMap.append(";TOT_AMT");
        table.setHeader(header.toString());
        table.setParmMap(parmMap.toString());
        table.setColumnHorizontalAlignmentData(alignment.toString());
    }

    /**
     * 查询
     */
    public void onQuery(){
        int subdate = getSubDate((Timestamp)getValue("START_DATE"), (Timestamp)getValue("END_DATE"));
        if(subdate < 0){
            this.messageBox("请选择一个有意义的时间间隔!");
            return ;
        }
        //查询时间
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //取得开始时间,该报表开始时间是必须的
        String startDate = format.format((Timestamp)this.getValue("START_DATE"));
        //取得结束时间,结束时间也是必须的
        String endDate = format.format((Timestamp)this.getValue("END_DATE"));
        //取得构造的SQL
        String sql = getQuerySQL(startDate, endDate);
        //查询
        TParm parm = new TParm(
              TJDODBTool.getInstance().select(sql)
        );
        //如果没有查询到数据,则提示并返回
        if(parm.getErrCode() < 0 || parm.getCount("DEPT_CODE") == 0){
            this.messageBox("没有查询到数据!");
            return ;
        }
        TParm result = addCleanParm(parm);
        cleanParm(result);
        table.setParmValue(result);

    }

    public void onExport(){
    if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "药库药品收益占总收入的比例");

}
    /**
     * 清空
     */
    public void onClear(){
        this.clearValue("DEPT_CODE;USER_CODE");
        initDate();
        table.setParmValue(new TParm());
    }


    /**
    * 初始化时间,起始日期是上一月的1号,结束日期是当前月的1号
    */
   private void initDate(){
       Timestamp currentDate = SystemTool.getInstance().getDate();
       Calendar calendar = Calendar.getInstance();
       calendar.setTime(currentDate);
       //初始化结束时间,当前年月日时分秒
       this.setValue("END_DATE", new Timestamp(calendar.getTimeInMillis()));
       int year = calendar.get(Calendar.YEAR);
       //注意: 从calendar中取得的月份是从0开始的,最大是11
       int month = calendar.get(Calendar.MONTH);

       //初始化起始时间,前一个月第一天
       int startyear = year;
       int startmonth = month;
       //初始化起始时间,本月的第一天,如果当天是1日,则起始月应该是上一月.如果是1月1日,则起始月应该是上一年的12月1日
       if(calendar.get(Calendar.DATE) == 1){
           //判断当前月是不是1月,如果是1月,则起始年应该是上一年
           startyear = month == 0 ? (year - 1) : year;
           //如果是1月,则起始月应该是上一年的最后一个月
           startmonth = month == 0 ? 11 : month;
       }
       calendar.set(startyear, startmonth, 1, 0, 0, 0);
       this.setValue("START_DATE",new Timestamp(calendar.getTimeInMillis()));
   }


    /**
     * 计算两个时间段的相差天数,这个其实天数必须大于结束天数,否则返回-1
     * @param startDate Timestamp
     * @param endDate Timestamp
     * @return int 返回相差天数
     */
    private int getSubDate(Timestamp startDate, Timestamp endDate){
        if(startDate == null || endDate == null){
            return -1;
        }
        //起始时间要小于结束时间
        if(startDate.getTime() >= endDate.getTime()){
            return -1;
        }
        //一天的毫秒数
        long date = 24 * 60 * 60 * 1000;
        //用时间差处于一天的毫秒数,就是相差的天数
        return (int)((endDate.getTime() - startDate.getTime()) / date);
    }


    /**
     * 查询一段时期药品收入的情况,构造需要的SQL
     * @return String 返回查询SQL
     */
    private String getQuerySQL(String startDate, String endDate) {
        TParm parm = this.getParmForTag("DEPT_CODE;USER_CODE");
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT DEPT_CODE, DEPT_CHN_DESC, DR_CODE, USER_NAME, ID, SUM(TOT_AMT) AS TOT_AMT ");
        sql.append(" FROM (SELECT   B.DEPT_CODE, E.DEPT_CHN_DESC, B.DR_CODE, F.USER_NAME, C.ID, ");
        sql.append(" SUM (B.AR_AMT) AS TOT_AMT ");
        sql.append(" FROM SYS_CHARGE_HOSP A, OPD_ORDER B, SYS_DICTIONARY C, SYS_DEPT E, SYS_OPERATOR F ");
        sql.append(" WHERE A.CHARGE_HOSP_CODE = B.HEXP_CODE ");
        sql.append(" AND B.DR_CODE = F.USER_ID ");
        sql.append(" AND B.DEPT_CODE = E.DEPT_CODE ");
        sql.append(" AND A.STA_CHARGE_CODE = C.ID ");
        sql.append(" AND C.GROUP_ID = 'STA_CHARGE' ");

        if(!parm.getValue("DEPT_CODE").equals("")){
            sql.append(" AND B.DEPT_CODE = '");
            sql.append(parm.getValue("DEPT_CODE"));
            sql.append("' ");
        }
        if(!parm.getValue("USER_CODE").equals("")){
            sql.append(" AND B.DR_CODE = '");
            sql.append(parm.getValue("USER_CODE"));
            sql.append("' ");
        }
        sql.append(" AND B.REGION_CODE = '" + Operator.getRegion() + "'");
        sql.append(" AND E.REGION_CODE = '" + Operator.getRegion() + "'");
        sql.append(" AND B.BILL_DATE BETWEEN TO_DATE('");
        sql.append(startDate);
        sql.append("', 'YYYY-MM-DD') ");
        sql.append(" AND TO_DATE('");
        sql.append(endDate);
        sql.append("', 'YYYY-MM-DD') ");
        sql.append("GROUP BY B.DEPT_CODE, E.DEPT_CHN_DESC, B.DR_CODE, C.ID, F.USER_NAME  ");
        //连接两个查询
        sql.append("UNION ALL ");

        sql.append(" SELECT C.DEPT_CODE, E.DEPT_CHN_DESC, C.EXE_DR_CODE AS DR_CODE, ");
        sql.append(" F.USER_NAME, G.STA_CHARGE_CODE AS ID, ");
        sql.append(" SUM (C.TOT_AMT) AS TOT_AMT ");
        sql.append(" FROM IBS_ORDD C, SYS_FEE D, SYS_DEPT E, SYS_OPERATOR F, SYS_CHARGE_HOSP G ");
        sql.append(" WHERE C.ORDER_CODE = D.ORDER_CODE ");
        sql.append(" AND C.DEPT_CODE = E.DEPT_CODE ");
        sql.append("  AND C.EXE_DR_CODE = F.USER_ID ");
        sql.append(" AND C.HEXP_CODE = G.CHARGE_HOSP_CODE ");
        sql.append(" AND E.REGION_CODE = '" + Operator.getRegion() + "'");
        //如果选择可科室,则提供按照科室查询
        if(!parm.getValue("DEPT_CODE").equals("")){
            sql.append(" AND C.DEPT_CODE = '");
            sql.append(parm.getValue("DEPT_CODE"));
            sql.append("' ");
        }
        //如果选择了医师,则把医师code当做一个查询条件
        if(!parm.getValue("USER_CODE").equals("")){
            sql.append(" AND C.EXE_DR_CODE = '");
            sql.append(parm.getValue("USER_CODE"));
            sql.append("' ");
        }
        sql.append(" AND C.BILL_DATE BETWEEN TO_DATE('");
        sql.append(startDate);
        sql.append("', 'YYYY-MM-DD') ");
        sql.append(" AND TO_DATE('");
        sql.append(endDate);
        sql.append("', 'YYYY-MM-DD') ");

        sql.append(" GROUP BY C.DEPT_CODE, ");
        sql.append(" E.DEPT_CHN_DESC, ");
        sql.append(" C.EXE_DR_CODE, ");
        sql.append(" F.USER_NAME, ");
        sql.append("  G.STA_CHARGE_CODE) A ");
        sql.append(
            " GROUP BY DEPT_CODE, DEPT_CHN_DESC, DR_CODE, USER_NAME, ID ");
        sql.append(" ORDER BY DEPT_CODE, DR_CODE, ID ");


        return sql.toString();
    }

    /**
     * 整理数据,从数据库中查询出的数据费用是纵向排列的,在这需要把纵向排列的可用数据构造成横向排列<br>
     * 如:DEPT_CODE  DR_CODE  ID   TOT_AMT
     *    1010101   1001      01   100.0
     *    1010101   1001      02   200.0
     *
     * 当DEPT_CODE和DR_CODE相同时,我们需要构造成这样,ID中的数字是指药品费,检查费等
     * DEPT_CODE  DR_CODE  药品费 药品费占总费用的比例 检查费 检查费占总费用的比例 总费用
     * 1010101    1001     100   0                200    0                300
     * @param data TParm 原数据
     * @return TParm 返回经过整理的数据
     */
    private TParm addCleanParm(TParm data) {
        TParm result = new TParm();
        result.addData("DEPT_CODE", null);
        result.addData("DR_CODE", null);
        result.addData("DEPT_CHN_DESC", null);
        result.addData("USER_NAME", null);
        //这里需要记录数据头(表头)
        for (int i = 0; i < headerList.size(); i++) {
            result.addData(headerList.get(i).toString(), null);
        }
        result.addData("TOT_AMT", null);
        // 查询到数据,需要计算该医师用药的收益占总收益的百分比
        for (int i = 0; i < data.getCount("DEPT_CODE"); i++) {
            //取得原数据的单行数据
            TParm rowParm = data.getRow(i);
            //标志位,当DEPT_CODE和DR_CODE值flg为false
            boolean flg = true;
            for (int j = 0; j < result.getCount("DEPT_CODE"); j++) {
                //当DEPT_CODE和DR_CODE则是同一条数据
                if (result.getValue("DEPT_CODE", j).equals(
                    rowParm.getValue("DEPT_CODE"))
                    && result.getValue("DR_CODE", j).equals(
                        rowParm.getValue("DR_CODE"))) {
                    result.setData("DATA" + rowParm.getValue("ID"), j,
                                   rowParm.getValue("TOT_AMT"));
                    result.setData("DATA_" + rowParm.getValue("ID"), j, 0);
                    double tot_amt = 0;
                    try {
                        //防止空数据
                        tot_amt = Double.parseDouble(result.getValue("TOT_AMT", j));
                    }
                    catch (NumberFormatException e) {
                        //发生空数据,则值数据默认0
                        tot_amt = 0;
                    }
                    result.setData("TOT_AMT", j, tot_amt + rowParm.getDouble("TOT_AMT"));
                    flg = false;
                    break;
                }
            }
            //放flg=true,则需要添加一行新数据
            if (flg) {
                result.addData("DEPT_CODE", rowParm.getValue("DEPT_CODE"));
                result.addData("DR_CODE", rowParm.getValue("DR_CODE"));
                result.addData("DEPT_CHN_DESC", rowParm.getValue("DEPT_CHN_DESC"));
                result.addData("USER_NAME", rowParm.getValue("USER_NAME"));
                result.addData("TOT_AMT", rowParm.getDouble("TOT_AMT"));
                //数据头是DATA01,则对应的百分比是DATA_01
                int k = 0;
                //这里循环需要每两次循环一次
                while (k < headerList.size()) {
                    if ( ("DATA" +
                        rowParm.getValue("ID")).equals(headerList.get(k).
                        toString())) {
                        result.addData(headerList.get(k).toString(),
                                       rowParm.getValue("TOT_AMT"));
                        result.addData(headerList.get(k + 1).toString(), 0);
                    }
                    else {
                        result.addData(headerList.get(k).toString(), 0);
                        result.addData(headerList.get(k + 1).toString(), 0);
                    }

                    k += 2;
                }

            }

        }
        //除去第一行数据
        result.removeRow(0);
        return result;
    }

    /**
     * 整理数据,计算百分比等
     * @param parm TParm
     */
    private void cleanParm(TParm parm){
        for(int i = 0; i < parm.getCount("DEPT_CODE"); i++){
            double tot_amt = parm.getDouble("TOT_AMT", i);
            for(int j = 1; j < headerList.size(); ){
                double data = parm.getDouble(headerList.get(j - 1).toString(), i);
                try{
                    double tmp = data / tot_amt * 100;
                    //保留2为小数
                    parm.setData(headerList.get(j).toString(), i, (StringTool.round(tmp, 2)) + "%");
                }catch(NumberFormatException e){
                    parm.setData(headerList.get(j).toString(), i, "0%");
                }
                j += 2;
            }
        }
    }
}
