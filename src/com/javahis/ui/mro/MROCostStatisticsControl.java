package com.javahis.ui.mro;

import com.dongyang.control.*;
import jdo.sys.SystemTool;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import java.text.ParseException;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.ui.TTextFormat;

/**
 * <p>Title: 指定时期单病种费用统计</p>
 *
 * <p>Description: 指定时期单病种费用统计</p>
 *
 * <p>Copyright: Copyright (c) Javahis 2011</p>
 *
 * <p>Company: JavaHis</p>
 * com.javahis.ui.mro.MROCostStatisticsControl
 *
 * @author ZhenQin
 * @version 4.0
 */
public class MROCostStatisticsControl
    extends TControl {

    /**
     * 显示数据的Table
     */
    private TTable table = null;

    /**
     * 构造方法
     */
    public MROCostStatisticsControl() {
        super();
    }


    /**
     * 初始化,时间
     * 开始日期是前一月的1号,结束日期是当前月的1号
     */
    public void onInit(){
        initDate();
        table = (TTable)this.getComponent("tTable_0");
    }

    /**
     * 查询
     */
    public void onQuery(){
        int subdate = getSubDate((Timestamp)getValue("START_DATE"), (Timestamp)getValue("END_DATE"));
        //检查时间选择,开始时间若是比结束时间迟,这样是不合理的.提示并初始化
        if(subdate < 0){
            this.messageBox("请选择一个有意义的时间区间!");
            initDate();
            return ;
        }
        TParm param = this.getParmForTag("START_DATE;END_DATE;DISEASES_CODE");
        param.setData("REGION_CODE", Operator.getRegion());
        //取得构造的SQL,并查询数据库
        TParm result = new TParm(
              TJDODBTool.getInstance().select(getInpatientSQL(param))
        );

        //查询异常,或者没有查询到数据库,提示
        if(result.getErrCode() < 0 || result.getCount("DISEASES_CODE") <= 0){
            this.messageBox("没有查询到数据!");
            this.err(result.getErrName() + "    " + result.getErrText());
            table.removeRowAll();
            return ;
        }

        //取得构造的SQL,查询数据库
        TParm result_1 = new TParm(
              TJDODBTool.getInstance().select(getOutPatientSQL(param))
        );
        if(result_1.getErrCode() < 0 || result_1.getCount("DISEASES_CODE") <= 0){
            this.messageBox("没有查询到数据!");
            this.err(result_1.getErrName() + "    " + result_1.getErrText());
            table.removeRowAll();
            return ;
        }
        TParm data = new TParm();
        //遍历数据,并提取需要的数据
        for(int i = 0; i < result.getCount("DISEASES_CODE"); i++){
            data.addData("DISEASES_CODE", result.getValue("DISEASES_CODE", i));
            data.addData("TOTAL_AMT", StringTool.round(result.getDouble("TOTAL_AMT", i), 2));
            data.addData("AVG_AMT",  StringTool.round(result.getDouble("AVG_AMT", i), 2));
            for(int j = 0; j < result_1.getCount("DISEASES_CODE"); j++){
                if (result.getValue("DISEASES_CODE",
                    i).equals(result_1.getValue("DISEASES_CODE", j))) {
                    data.addData("AVG_AMT_1", StringTool.round(result_1.getDouble("AVG_AMT", j), 2));
                }
            }
        }
        table.setParmValue(data);
    }

    /**
     * 清空
     */
    public void onClear(){
        this.clearValue("DISEASES_CODE");
        initDate();
    }

    /**
     * 打印
     */
    public void onPrint(){
        //TIMEINTERVAL,STATIS;.jhw
        if (table.getRowCount() <= 0) {
            this.messageBox("没有需要打印的数据!");
            return;
        }
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        TParm tmp = this.getParmForTag("START_DATE;END_DATE;DISEASES_CODE");
        try {
            TParm data = new TParm();
            Timestamp START_DATE = (Timestamp)((TTextFormat)this.getComponent("START_DATE")).getValue();
            Timestamp END_DATE = (Timestamp)((TTextFormat)this.getComponent("END_DATE")).getValue();
            // 表头数据
            data.setData("TITLE", "TEXT", Operator.getHospitalCHNFullName() + "指定时期单病种费用统计");
            data.setData("TIMEINTERVAL", "TEXT", "统计区间: "
                     + format.format(START_DATE)
                     + " ～ "
                     + format.format(END_DATE));


            if(!tmp.getValue("DISEASES_CODE").equals("")){
                data.setData("STATIS", "TEXT", "病种分类: " + ((TTextFormat)this.getComponent("DISEASES_CODE")).getText());
            }else{
                data.setData("STATIS", "TEXT", "病种分类: " + "所有病种");
            }

            // 表格数据
            TParm parm = new TParm();
            TParm tableParm = table.getShowParmValue();
            // 便利表格中的元素
            //DISEASES_CODE;TOTAL_AMT;AVG_AMT;AVG_AMT_1
            for (int i = 0; i < table.getRowCount(); i++) {
                parm.addData("DISEASES_CODE", tableParm.getValue("DISEASES_CODE", i));
                parm.addData("TOTAL_AMT", tableParm.getValue("TOTAL_AMT", i));
                parm.addData("AVG_AMT", tableParm.getValue("AVG_AMT", i));
                parm.addData("AVG_AMT_1", tableParm.getValue("AVG_AMT_1", i));
            }
            // 总行数
            parm.setCount(parm.getCount("DISEASES_CODE"));
            parm.addData("SYSTEM", "COLUMNS", "DISEASES_CODE");
            parm.addData("SYSTEM", "COLUMNS", "TOTAL_AMT");
            parm.addData("SYSTEM", "COLUMNS", "AVG_AMT");
            parm.addData("SYSTEM", "COLUMNS", "AVG_AMT_1");
            // 将表格放到容器中
            data.setData("TABLE", parm.getData());
            // 表尾数据
            data.setData("OPERATOR", "TEXT", "制表人: " + Operator.getName());
            openPrintDialog("%ROOT%\\config\\prt\\MRO\\MROCostStatistics.jhw",
                                        data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 导出到Xls
     */
    public void onExport(){
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "指定时期单病种费用统计");

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
     * 返回床位费查询SQL
     * @param data TParm
     * @return String
     */
    private String getOutPatientSQL(TParm data){
        //时间格式化format
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sql = new StringBuilder();
        sql.append("  SELECT A.DISEASES_CODE, AVG(TOT_AMT) AS AVG_AMT ");
        sql.append("  FROM MRO_RECORD A, IBS_ORDD C ");
        sql.append("  WHERE A.CASE_NO = C.CASE_NO ");
        sql.append("  AND A.DISEASES_CODE IS NOT NULL ");
        try {
            sql.append("     AND A.IN_DATE BETWEEN TO_DATE ('");
            //入院时间构造统计起始时间
            sql.append(format.format(format.parse(data.getValue("START_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");
            sql.append("     AND TO_DATE ('");
            //入院时间构造统计结束时间
            sql.append(format.format(format.parse(data.getValue("END_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");

            //出院时间构造统计起始时间
            sql.append("     AND A.OUT_DATE BETWEEN TO_DATE ('");
            sql.append(format.format(format.parse(data.getValue("START_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");
            sql.append("     AND TO_DATE ('");
            //出院时间构造统计结束时间
            sql.append(format.format(format.parse(data.getValue("END_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");

        } catch( ParseException e) {
            e.printStackTrace();
        }
        sql.append(" GROUP BY A.DISEASES_CODE ");
        return sql.toString();
    }

    /**
     * 构造需要的SQL
     * @return String
     */
    private String getInpatientSQL(TParm data){
        //时间格式化format
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sql = new StringBuilder();
        //DISEASES_CODE;TOTAL_AMT
        sql.append("  SELECT A.DISEASES_CODE, SUM(C.TOTAL_AMT) AS TOTAL_AMT, ");
        sql.append("  AVG(C.TOTAL_AMT) AS AVG_AMT ");
        sql.append("  FROM MRO_RECORD A, ADM_INP C ");
        sql.append("  WHERE A.CASE_NO = C.CASE_NO ");
        sql.append("  AND A.DISEASES_CODE IS NOT NULL ");
        if(!data.getValue("DISEASES_CODE").equals("")){
            sql.append("  AND A.DISEASES_CODE = '");
            sql.append(data.getValue("DISEASES_CODE"));
            sql.append("'");
        }
       try {
            sql.append("     AND A.IN_DATE BETWEEN TO_DATE ('");
            //入院时间构造统计起始时间
            sql.append(format.format(format.parse(data.getValue("START_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");
            sql.append("     AND TO_DATE ('");
            //入院时间构造统计结束时间
            sql.append(format.format(format.parse(data.getValue("END_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");

            //出院时间构造统计起始时间
            sql.append("     AND A.OUT_DATE BETWEEN TO_DATE ('");
            sql.append(format.format(format.parse(data.getValue("START_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");
            sql.append("     AND TO_DATE ('");
            //出院时间构造统计结束时间
            sql.append(format.format(format.parse(data.getValue("END_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");
            sql.append(" GROUP BY A.DISEASES_CODE ");
        }catch(ParseException e){
            e.printStackTrace();
        }
        return sql.toString();
    }
}
