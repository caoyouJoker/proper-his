package com.javahis.ui.mro;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import java.util.Calendar;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: 电子病历,门诊次均费用统计报表</p>
 *
 * <p>Description: 电子病历,门诊次均费用统计查询报表</p>
 *
 * <p>Copyright: Copyright (c) 2011 </p>
 *
 * <p>Company: JavaHis </p>
 * com.javahis.ui.mro.MRODiagCostControl
 * @author ZhenQin
 * @version 4.0
 */
public class MRODiagCostControl
    extends TControl {

    /**
     * 显示数据的Table
     */
    private TTable table = null;

    /**
     * 构造方法
     */
    public MRODiagCostControl() {
        super();
    }

    /**
     * 初始化
     */
    public void onInit(){
        super.onInit();
        table = (TTable)this.getComponent("tTable_1");
        initDate();
        //ICD10弹出窗口
        callFunction("UI|ICD_CODE|setPopupMenuParameter", "ICD10",
                     "%ROOT%\\config\\sys\\SYSICDPopup.x");
        //接受ICD10弹窗回传值
        callFunction("UI|ICD_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "ICD10Return");

    }

    /**
     * 诊断ICD10选择返回数据处理
     * @param tag String
     * @param obj Object
     */
    public void ICD10Return(String tag, Object obj) {
        if (obj == null){
            return;
        }
        TParm returnParm = (TParm) obj;
        this.setValue("ICD_CODE",returnParm.getValue("ICD_CODE"));
        this.setValue("ICD_DESC",returnParm.getValue("ICD_CHN_DESC"));
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
        TParm param = this.getParmForTag("START_DATE;END_DATE;ICD_CODE;ICD_DESC");
        param.setData("REGION_CODE", Operator.getRegion());
        String sql = getSQL(param);
        //取得构造的SQL,并查询数据库
        TParm result = new TParm(
              TJDODBTool.getInstance().select(sql)
        );
        //查询异常,或者没有查询到数据库,提示
        if(result.getErrCode() < 0 || result.getCount("ICD_CODE") <= 0){
            this.messageBox("没有查询到数据!");
            this.err(result.getErrName() + "    " + result.getErrText());
            table.removeRowAll();
            return ;
        }
        //=========pangben modify 20110704 start
        double sumAMT=0.00;//汇总价格
        int sumCount=0;//患者人数
        for (int i = 0; i < result.getCount("ICD_CODE"); i++) {
            result.addData("AVG_AMT",
                           StringTool.round(result.getDouble("AR_AMT", i) /
                                            result.getDouble("COUNT_PAT", i), 3));
            sumAMT+=StringTool.round(result.getDouble("AR_AMT", i) /
                                            result.getDouble("COUNT_PAT", i), 3);
            sumCount+=result.getInt("COUNT_PAT", i);
        }
        result.addData("COUNT_PAT",sumCount);
        result.addData("AVG_AMT",sumAMT);
        result.addData("ICD_CODE","合计:");
        //=========pangben modify 20110704 start
        table.setParmValue(result);

    }

    /**
     * 打印报表
     */
    public void onPrint(){

    }

    /**
     * 清空数据
     */
    public void onClear(){
        this.clearValue("START_DATE;END_DATE;ICD_CODE;ICD_DESC");
        initDate();

    }

    /**
     * 导出到Xls
     */
    public void onExport(){
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "门诊次均费用统计");

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
     * 构造查询SQL
     * @param data TParm 查询参数
     * @return String 返回查询SQL
     */
    private String getSQL(TParm data){
        //时间格式化format
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT   ICD_CODE, ICD_TYPE, ADM_TYPE,ICD_CHN_DESC, SUM (AR_AMT) AS AR_AMT, ");
        sql.append("  COUNT (CASE_NO) AS COUNT_PAT ");
        sql.append(" FROM (SELECT   A.CASE_NO, B.ADM_TYPE, B.ICD_CODE, B.ICD_TYPE, C.ICD_CHN_DESC,  ");

        sql.append("   SUM (A.AR_AMT) AS AR_AMT ");
        sql.append("  FROM OPD_ORDER A, OPD_DIAGREC B, sys_diagnosis C ");
        sql.append("  WHERE A.CASE_NO = B.CASE_NO ");
        sql.append("    AND B.ICD_CODE = C.ICD_CODE ");
        sql.append("    AND B.ICD_TYPE = C.ICD_TYPE ");
        sql.append("    AND B.MAIN_DIAG_FLG = 'Y' ");
        sql.append("    AND A.REGION_CODE = '");
        sql.append(data.getValue("REGION_CODE"));
        sql.append("'  ");
        if(!data.getValue("ICD_CODE").equals("") &&
              !data.getValue("ICD_DESC").equals("")){
            sql.append("    AND B.ICD_CODE = '");
            sql.append(data.getValue("ICD_CODE"));
            sql.append("'  ");
        }
        try {
            sql.append("  AND A.BILL_DATE BETWEEN TO_DATE ('");
            //入院时间构造统计起始时间
            sql.append(format.format(format.parse(data.getValue("START_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");
            sql.append("  AND TO_DATE ('");
            //入院时间构造统计结束时间
            sql.append(format.format(format.parse(data.getValue("END_DATE"))));
            sql.append("', 'YYYY-MM-DD HH24:MI:SS') ");

        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        sql.append("  GROUP BY A.CASE_NO, B.ADM_TYPE, B.ICD_CODE, B.ICD_TYPE, C.ICD_CHN_DESC) ");
        sql.append(" GROUP BY ICD_CODE, ICD_TYPE, ADM_TYPE,ICD_CHN_DESC    ");


        return sql.toString();

    }
}
