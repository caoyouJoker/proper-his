package com.javahis.ui.mro;

import com.dongyang.control.*;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import java.util.Calendar;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.*;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.ui.TTextFormat;
import com.javahis.system.combo.TComboDept;
import com.javahis.system.textFormat.TextFormatDept;

/**
 * <p>Title: 提供对患者诊疗相关费用支出情况实时监控，对贵重药品,高值耗材管理功能</p>
 *
 * <p>Description: 提供对患者诊疗相关费用支出情况实时监控，对贵重药品管理功能</p>
 *
 * <p>Copyright: Copyright (c) Javahis 2011 </p>
 *
 * <p>Company: Javahis </p>
 * com.javahis.ui.mro.MROExpDrugsHighConsControl
 * @author ZhenQin
 * @version 4.0
 */
public class MROExpDrugsHighConsControl
    extends TControl {

    /**
     * 用于显示数据的TAble
     */
    private TTable table = null;

    /**
     * 贵重药品,高值耗材的金额
     */
    private double price = 800;

    /**
     * 查询种类贵重药品,高值耗材
     */
    private String kind = null;

    /**
     * 构造方法HighCons
     */
    public MROExpDrugsHighConsControl() {
        super();
    }

    /**
     * 初始化
     */
    public void onInit() {
        Object obj = this.getParameter();
        table = (TTable)this.getComponent("tTable_0");
        //没有参数则不初始化
        if(obj == null){
            this.kind = null;
            initN();
            initKind(this.kind);
            initDate();
        } else if(obj instanceof String) {
            this.kind = obj.toString();
            //贵重药品
            if(this.kind.equalsIgnoreCase("PHA")){
                //初始化药品
                initExpDrug();
                //查询种类是贵重药品
                initKind(this.kind);
                //初始化时间
                initDate();
                //高值耗材
            }else if(this.kind.equalsIgnoreCase("INV")){
                ////初始化耗材
                initHighCons();
                //初始化高值耗材
                initKind("inv");
                //初始化时间
                initDate();
                //其他则不初始化
            }else{
                this.kind = null;
                initN();
                initKind(this.kind);
                initDate();

            }
            //参数是其他类型,不初始化
        } else {
            this.kind = null;
            initN();
            initKind(this.kind);
            initDate();

        }
    }

    /**
     * 没有参数,初始化默认,默认的price是800;
     */
    private void initN(){
        TTextField priceTxt = (TTextField)this.getComponent("price");
        priceTxt.setText("" + price);
    }

    /**
     * 初始化药品
     */
    private void initExpDrug(){

        String sql = "SELECT HIGH_VALUE_PHA FROM PHA_SYSPARM WHERE HIGH_VALUE_PHA IS NOT NULL ORDER BY HIGH_VALUE_PHA";
        //查询
        TParm data = new TParm(
            TJDODBTool.getInstance().select(sql)
        );
        price = data.getDouble("HIGH_VALUE_PHA", 0);
        TTextField priceTxt = (TTextField)this.getComponent("price");
        priceTxt.setText("" + price);
    }

    /**
     * 初始化耗材
     */
    private void initHighCons(){

        String sql = "SELECT HIGH_VALUE_INV FROM INV_SYSPARM WHERE HIGH_VALUE_INV IS NOT NULL ORDER BY HIGH_VALUE_INV";
        //查询
        TParm data = new TParm(
            TJDODBTool.getInstance().select(sql)
        );
        price = data.getDouble("HIGH_VALUE_INV", 0);
        TTextField priceTxt = (TTextField)this.getComponent("price");
        priceTxt.setText("" + price);

    }


    /**
     * 初始化查询种类,贵重药品,高值耗材.传入null则设置查询种类为pha
     * @param id String
     */
    private void initKind(String id){
        initTable(id);
        if(id == null){
            this.setValue("kind", "pha");
            initExpDrug();
            return ;
        }
        TComboBox kindCombo = (TComboBox)this.getComponent("kind");
        this.setValue("kind", id);
        kindCombo.setEnabled(false);
    }


    /**
     * 查询条件的转换
     */
    public void changeKind(){
        String kind = this.getValueString("kind");
        table.removeRowAll();
        if(kind.equalsIgnoreCase("pha")){
            initExpDrug();
        } else {
            this.initHighCons();
        }
    }

    /**
     * 初始化Table,当初传入参数初始化默认,默认是查询贵重药品<br>
     * 若是程序在初始化传入参数,则以相应的方式初始化<br>
     * 若是没有传入参数,这个方法是为了让查询时动态的现实查询贵重药品还是高值耗材<br>
     *
     * @param kind String 查询种类,贵重药品还是高值耗材(pha,inv)
     */
    private void initTable(String kind){
        String header = null;
        String parmMap = "DEPT_CHN_DESC;CASE_NO;MR_NO;PAT_NAME;IN_DATE;DS_DATE;ORDER_DESC;TOT_AMT;BILL_DATE;USER_NAME";
        if(kind == null || kind.equalsIgnoreCase("pha")){
            header = "科室,120;就诊序号,100;病案号,120;姓名,80;入院时间,100,Timestamp;出院时间,100,Timestamp;药品名称,180;价格,80,double;执行时间,120,Timestamp;开立医生,100";
        }else if(kind.equalsIgnoreCase("inv")){
            header = "科室,120;就诊序号,100;病案号,120;姓名,80;入院时间,100,Timestamp;出院时间,100,Timestamp;耗材名称,180;价格,80,double;执行时间,120,Timestamp;开立医生,100";
        }else{
            header = "科室,120;就诊序号,100;病案号,120;姓名,80;入院时间,100,Timestamp;出院时间,100,Timestamp;药品名称,180;价格,80,double;执行时间,120,Timestamp;开立医生,100";
        }
        table.setHeader(header);
        table.setParmMap(parmMap);
        table.setLockColumns("All");
        table.setColumnHorizontalAlignmentData("0,left;3,left;6,left;7,left;9,left");

    }

    /**
     * 查询
     */
    public void onQuery() {
        int subDate = getSubDate((Timestamp)this.getValue("START_DATE"), (Timestamp)this.getValue("END_DATE"));
        if(subDate < 0){
            this.messageBox("请选择合理的时间间隔!");
            return;
        }
        TParm param = this.getParmForTag("price;kind;DEPT_CODE;START_DATE;END_DATE");

        //当初始化没有传入参数的情况,这样可以提供查询两个
        if(kind == null){
            //每次查询都初始化一次列表
            initTable(param.getValue("kind"));
        }
        //当查询的是贵重药品
        if(param.getValue("kind").equalsIgnoreCase("pha")){
            try{
                //检验输入的贵重药品的价格,如果输入错误,则提示,并用执行默认的初始化方法
                Double.parseDouble(param.getValue("price"));
            }catch(NumberFormatException e){
                this.messageBox("请选择合理的金额!");
                //执行默认的初始化方法
                initExpDrug();
                return;
            }
            //查询相应的查询方法
            queryExpDrug(param);
            //否则是查询高值耗材
        }else{
            try{
                //检验输入的高值耗材的价格,若输入错误,则提示并调用相应的默认初始化方法
                Double.parseDouble(param.getValue("price"));
            }catch(NumberFormatException e){
                this.messageBox("请选择合理的金额!");
                //执行默认的初始化方法
                initHighCons();
                return;
            }
            //调用高值耗材的查询方法
            queryHighCons(param);
        }
    }

    /**
     * 查询贵重药品
     * @param data TParm
     */
    private void queryExpDrug(TParm data){
        //时间格式化format
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //查询SQL
        StringBuilder sqlExpDrug = new StringBuilder();
        sqlExpDrug.append(" SELECT A.DEPT_CODE, E.DEPT_CHN_DESC, A.CASE_NO, D.MR_NO, ");
        sqlExpDrug.append("        (SELECT PAT_NAME ");
        sqlExpDrug.append("           FROM SYS_PATINFO ");
        sqlExpDrug.append("          WHERE MR_NO = D.MR_NO) ");
        sqlExpDrug.append(" AS PAT_NAME, D.IN_DATE, D.DS_DATE,A.ORDER_CODE, B.ORDER_DESC, ");
        sqlExpDrug.append("        A.TOT_AMT, A.BILL_DATE, A.DR_CODE, C.USER_NAME ");
        sqlExpDrug.append("    FROM IBS_ORDD A, PHA_BASE B, SYS_OPERATOR C, ADM_INP D, SYS_DEPT E ");
        sqlExpDrug.append("   WHERE A.ORDER_CODE = B.ORDER_CODE ");
        sqlExpDrug.append("     AND A.DEPT_CODE = E.DEPT_CODE ");
        sqlExpDrug.append("     AND A.DR_CODE = C.USER_ID ");
        sqlExpDrug.append("     AND A.CASE_NO = D.CASE_NO ");
        if(!data.getValue("DEPT_CODE").equals("")){
            sqlExpDrug.append("     AND A.DEPT_CODE = '");
            sqlExpDrug.append(data.getValue("DEPT_CODE"));
            sqlExpDrug.append("' ");
        }
        sqlExpDrug.append("     AND A.TOT_AMT > " + data.getValue("price"));
        sqlExpDrug.append("     AND A.ORDER_CAT1_CODE LIKE 'PHA%' ");
        sqlExpDrug.append("     AND D.REGION_CODE = '" + Operator.getRegion() + "' ");
        try {
            sqlExpDrug.append("     AND A.BILL_DATE BETWEEN TO_DATE ('");
            //构造统计起始时间
            sqlExpDrug.append(format.format(format.parse(data.getValue("START_DATE"))));
            sqlExpDrug.append("', 'YYYY-MM-DD HH24:MI:SS') ");
            sqlExpDrug.append("     AND TO_DATE ('");
            //构造统计结束时间
            sqlExpDrug.append(format.format(format.parse(data.getValue("END_DATE"))));
            sqlExpDrug.append("', 'YYYY-MM-DD HH24:MI:SS') ");
        }catch(ParseException e){
            e.printStackTrace();
        }
        //查询数据
        TParm result = new TParm(
            TJDODBTool.getInstance().select(sqlExpDrug.toString()));
        //如果查询异常,或者没有查询到数据,则提示没有查询到数据
        if(result.getErrCode() < 0 || result.getCount() <= 0){
            this.messageBox("没有查询到数据!");
            table.removeRowAll();
            return;
        }
        table.setParmValue(result);
    }

    /**
     * 查询高值耗材
     * @param data TParm
     */
    private void queryHighCons(TParm data) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder sqlHighCons = new StringBuilder();
        sqlHighCons.append(" SELECT A.CASE_NO, A.DEPT_CODE, E.DEPT_CHN_DESC, A.CASE_NO,D.MR_NO,  ");
        sqlHighCons.append(" (SELECT PAT_NAME ");
        sqlHighCons.append("  FROM SYS_PATINFO ");
        sqlHighCons.append(
            " WHERE MR_NO = D.MR_NO) AS PAT_NAME, D.IN_DATE, D.DS_DATE, ");
        sqlHighCons.append(
            " A.ORDER_CODE, B.ORDER_DESC, A.ORDER_CAT1_CODE, A.BILL_DATE, A.TOT_AMT, ");
        sqlHighCons.append(" A.DR_CODE, C.USER_NAME ");
        sqlHighCons.append(
            " FROM IBS_ORDD A, SYS_FEE B, SYS_OPERATOR C, ADM_INP D, SYS_DEPT E ");
        sqlHighCons.append(" WHERE A.ORDER_CODE = B.ORDER_CODE ");
        sqlHighCons.append("  AND A.DR_CODE = C.USER_ID ");
        sqlHighCons.append("  AND A.DEPT_CODE = E.DEPT_CODE ");
        sqlHighCons.append(" AND A.CASE_NO = D.CASE_NO ");
        //高值耗材的价格
        sqlHighCons.append(" AND A.TOT_AMT > " + data.getValue("price"));
        sqlHighCons.append(" AND (A.ORDER_CAT1_CODE = 'MAT' OR A.ORDER_CAT1_CODE = 'INV') ");
        sqlHighCons.append(" AND D.REGION_CODE = '" + Operator.getRegion() + "' ");
        //科室
        if (!data.getValue("DEPT_CODE").equals("")) {
            sqlHighCons.append("     AND A.DEPT_CODE = '");
            sqlHighCons.append(data.getValue("DEPT_CODE"));
            sqlHighCons.append("' ");
        }
        try {
            //初始化起始时间和结束时间
            sqlHighCons.append("     AND A.BILL_DATE BETWEEN TO_DATE ('");
            sqlHighCons.append(format.format(format.parse(data.getValue(
                "START_DATE"))));
            sqlHighCons.append("', 'YYYY-MM-DD HH24:MI:SS') ");
            sqlHighCons.append("     AND TO_DATE ('");
            sqlHighCons.append(format.format(format.parse(data.getValue(
                "END_DATE"))));
            sqlHighCons.append("', 'YYYY-MM-DD HH24:MI:SS') ");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println("sqlHighCons==>>  " + sqlHighCons);
        //如果查询异常,或者没有查询到数据,则提示没有查询到数据
        TParm result = new TParm(
            TJDODBTool.getInstance().select(sqlHighCons.toString()));
        if (result.getErrCode() < 0 || result.getCount() <= 0) {
            this.messageBox("没有查询到数据!");
            table.removeRowAll();
            return;
        }
        table.setParmValue(result);
    }

    /**
     * 清除
     */
    public void onClear() {
        this.setValue("DEPT_CODE", Operator.getDept());
        //当初始化没有传入参数的情况,这样可以提供查询两个
        changeKind();
        initDate();
    }

    /**
     * 打印
     */
    public void onPrint() {
        if(table.getRowCount() <= 0){
            this.messageBox("没有需要打印的数据!");
            return ;
        }
        TParm param = this.getParmForTag("price;kind;DEPT_CODE;START_DATE;END_DATE");
        //初始化没有加入参数
        if(kind == null){
            //判断查询是贵重药品,
            if(param.getValue("kind").equals("pha")){
                //执行打印贵重药品
                printExpDrug(param);
                //高值耗材
            }else{
                printHighCons(param);
            }
        }else{
            //初始化数据有传入参数,贵重药品
            if(kind.equalsIgnoreCase("pha")){
                printExpDrug(param);
            }else{
                //高值耗材
                printHighCons(param);
            }
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
        ExportExcelUtil.getInstance().exportExcel(table, "贵重药品,高值耗材诊疗费用情况统计");

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
     * @param startDate Timestamp 起始时间
     * @param endDate Timestamp 结束时间
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
     * 打印贵重药品
     * @param data TParm
     */
    private void printExpDrug(TParm param){
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //TITLE;TIMEINTERVAL;DEPT;OPERATOR;DATE;EXPDRUG
        try {
            TParm data = new TParm();
            Timestamp START_DATE = (Timestamp)((TTextFormat)this.getComponent("START_DATE")).getValue();
            Timestamp END_DATE = (Timestamp)((TTextFormat)this.getComponent("END_DATE")).getValue();
            // 表头数据
            data.setData("TITLE", "TEXT", Operator.getHospitalCHNFullName() + "贵重药品费用支出情况");
            data.setData("TIMEINTERVAL", "TEXT", "统计区间: "
                     + format.format(START_DATE)
                     + " ～ "
                     + format.format(END_DATE));

            TextFormatDept dept = (TextFormatDept)this.getComponent("DEPT_CODE");
            if(!param.getValue("DEPT_CODE").equals("")){
                data.setData("DEPT", "TEXT", "科室: " + dept.getText());
            }else{
                data.setData("DEPT", "TEXT", "科室: " + "所有科室");
            }
            data.setData("EXPDRUG", "TEXT", "贵重药品: >= " + param.getValue("price"));
            // 表格数据
            TParm parm = new TParm();
            TParm tableParm = table.getShowParmValue();
            // 便利表格中的元素
            //DISEASES_CODE;TOTAL_AMT;AVG_AMT;AVG_AMT_1
            for (int i = 0; i < table.getRowCount(); i++) {
                parm.addData("DEPT_CHN_DESC", tableParm.getValue("DEPT_CHN_DESC", i));
                parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
                parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
                parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
                parm.addData("IN_DATE", tableParm.getValue("IN_DATE", i));
                parm.addData("DS_DATE", tableParm.getValue("DS_DATE", i));
                parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
                parm.addData("TOT_AMT", tableParm.getValue("TOT_AMT", i));
                parm.addData("BILL_DATE", tableParm.getValue("BILL_DATE", i));
                parm.addData("USER_NAME", tableParm.getValue("USER_NAME", i));
            }
            // 总行数
            parm.setCount(parm.getCount("DEPT_CHN_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
            parm.addData("SYSTEM", "COLUMNS", "CASE_NO");
            parm.addData("SYSTEM", "COLUMNS", "MR_NO");
            parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
            parm.addData("SYSTEM", "COLUMNS", "IN_DATE");
            parm.addData("SYSTEM", "COLUMNS", "DS_DATE");
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
            parm.addData("SYSTEM", "COLUMNS", "BILL_DATE");
            parm.addData("SYSTEM", "COLUMNS", "USER_NAME");
            // 将表格放到容器中
            data.setData("TABLE", parm.getData());
            // 表尾数据
            data.setData("DATE", "TEXT", "制表时间: " + format.format(SystemTool.getInstance().getDate()));
            data.setData("OPERATOR", "TEXT", "制表人: " + Operator.getName());
            openPrintDialog("%ROOT%\\config\\prt\\MRO\\ExpDrugPrint.jhw",
                                        data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印高值耗材
     * @param data TParm
     */
    private void  printHighCons(TParm param){
        //price;kind;DEPT_CODE;START_DATE;END_DATE,HighConsPrint.jhw
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //TITLE;TIMEINTERVAL;DEPT;OPERATOR;DATE;EXPDRUG
        try {
            TParm data = new TParm();
            Timestamp START_DATE = (Timestamp)((TTextFormat)this.getComponent("START_DATE")).getValue();
            Timestamp END_DATE = (Timestamp)((TTextFormat)this.getComponent("END_DATE")).getValue();
            // 表头数据
            data.setData("TITLE", "TEXT", Operator.getHospitalCHNFullName() + "高值耗材费用支出情况");
            data.setData("TIMEINTERVAL", "TEXT", "统计区间: "
                     + format.format(START_DATE)
                     + " ～ "
                     + format.format(END_DATE));

            TextFormatDept dept = (TextFormatDept)this.getComponent("DEPT_CODE");
            if(!param.getValue("DEPT_CODE").equals("")){
                data.setData("DEPT", "TEXT", "科室: " + dept.getText());
            }else{
                data.setData("DEPT", "TEXT", "科室: " + "所有科室");
            }
            data.setData("HIGHCONS", "TEXT", "高值耗材: >= " + param.getValue("price"));
            // 表格数据
            TParm parm = new TParm();
            TParm tableParm = table.getShowParmValue();
            // 便利表格中的元素
            //DISEASES_CODE;TOTAL_AMT;AVG_AMT;AVG_AMT_1
            for (int i = 0; i < table.getRowCount(); i++) {
                parm.addData("DEPT_CHN_DESC", tableParm.getValue("DEPT_CHN_DESC", i));
                parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
                parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
                parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
                parm.addData("IN_DATE", tableParm.getValue("IN_DATE", i));
                parm.addData("DS_DATE", tableParm.getValue("DS_DATE", i));
                parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
                parm.addData("TOT_AMT", tableParm.getValue("TOT_AMT", i));
                parm.addData("BILL_DATE", tableParm.getValue("BILL_DATE", i));
                parm.addData("USER_NAME", tableParm.getValue("USER_NAME", i));
            }
            // 总行数
            parm.setCount(parm.getCount("DEPT_CHN_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
            parm.addData("SYSTEM", "COLUMNS", "CASE_NO");
            parm.addData("SYSTEM", "COLUMNS", "MR_NO");
            parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
            parm.addData("SYSTEM", "COLUMNS", "IN_DATE");
            parm.addData("SYSTEM", "COLUMNS", "DS_DATE");
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
            parm.addData("SYSTEM", "COLUMNS", "BILL_DATE");
            parm.addData("SYSTEM", "COLUMNS", "USER_NAME");
            // 将表格放到容器中
            data.setData("TABLE", parm.getData());
            // 表尾数据
            data.setData("DATE", "TEXT", "制表时间: " + format.format(SystemTool.getInstance().getDate()));
            data.setData("OPERATOR", "TEXT", "制表人: " + Operator.getName());
            openPrintDialog("%ROOT%\\config\\prt\\MRO\\HighConsPrint.jhw",
                                        data);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
