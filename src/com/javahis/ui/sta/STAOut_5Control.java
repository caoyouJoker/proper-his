package com.javahis.ui.sta;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import jdo.sta.STAOut_5Tool;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.jdo.TDataStore;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.dongyang.ui.TCheckBox;
import jdo.sta.STATool;
import com.dongyang.ui.TLabel;
import java.util.Vector;
import jdo.sta.STASQLTool;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import java.text.DecimalFormat;

/**
 * <p>Title: 卫生部门医院部分病种住院医疗费用年报（卫统5表3）</p>
 *
 * <p>Description: 卫生部门医院部分病种住院医疗费用年报（卫统5表3）</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-15
 * @version 1.0
 */
public class STAOut_5Control
    extends TControl {
    private boolean STA_CONFIRM_FLG = false; //记录是否提交
    private String DATA_StaDate = ""; //记录目前显示的数据的主键(日期)
    private String S_TYPE = "";//记录当前统计的类型  month:月统计   day:日期段统计
    private String DATE_Start = "";//记录起始日期
    private String DATE_End = "";//记录截止日期
    private TParm Day_Data;//记录日期段查询出的数据
    private String LEADER = "";//记录是否是组长权限  如果LEADER=2那么就是组长权限
    /**
     * 页面初始化
     */
    public void onInit() {
        super.init();
        //设置初始时间
        this.setValue("STA_DATE",STATool.getInstance().getLastMonth());
        initDate();
        //表格中将SEQ替换为对应的病种名称
        TDataStore store = new TDataStore();
        store.setSQL("SELECT   SEQ, DEPT_DESC, ICD_DESC,  CONDITION FROM STA_30_LIST ");
        store.retrieve();
        OrderList orderDesc = new OrderList(store);
        TTable table = (TTable)this.getComponent("Table");
        table.addItem("ORDER_LIST", orderDesc);
        TTable table2 = (TTable)this.getComponent("Table_Read");
        table2.addItem("ORDER_LIST", orderDesc);
        //初始化权限
        if(this.getPopedem("LEADER")){
            LEADER = "2";
        }
    }

    /**
     * 清空
     */
    public void onClear() {
        //设置初始时间
        this.setValue("STA_DATE",STATool.getInstance().getLastMonth());
        initDate();
        this.clearValue("Submit");
        TTable table1 = (TTable)this.getComponent("Table");
        table1.removeRowAll();
        table1.resetModify();
        TTable table2 = (TTable)this.getComponent("Table_Read");
        table2.removeRowAll();
        STA_CONFIRM_FLG = false; //记录是否提交
        DATA_StaDate = ""; //记录目前显示的数据的主键(日期)
        S_TYPE = "";//记录当前统计的类型  month:月统计   day:日期段统计
        DATE_Start = "";//记录起始日期
        DATE_End = "";//记录截止日期
    }

    /**
     * 查询数据
     */
    public void onQuery() {
        if("Y".equals(this.getValueString("R_MONTH"))){//生成需要数据
            //注意：月报数据是正规数据 保存在STA_IN_02表中
            generate_MonthData();
        }
        else if("Y".equals(this.getValueString("R_DAYS"))){//生成日期时段数据
            //时间段查询的报表数据是非正规数据，只有查询显示功能，不进行保存不可修改
            generate_DayData();
        }
    }
    /**
     * 生成月报数据
     */
    private void generate_MonthData() {
        S_TYPE = "month";//记录统计类型
        STA_CONFIRM_FLG = false; //初始默认数据没有提交
        //生成数据的日期
        String STADATE = this.getText("STA_DATE").replace("/", "");
        if (STADATE.trim().length() <= 0) {
            this.messageBox_("请选则统计的年月！");
            return;
        }
        //检查是否可以重新生成数据
        if (!canGeneration(STADATE)) {
            //如果不能重新生成数据，绑定原有数据
            gridBind(STADATE); //数据绑定
            return;
        }
        String StartDate = STADATE + "01"; //每月第一天
        //获取此月份的最后一天
        String EndDate = StringTool.getString(STATool.getInstance().
                                              getLastDayOfMonth(STADATE),
                                              "yyyyMMdd");
        TParm parm = new TParm();
        parm.setData("DATE_S", StartDate);
        parm.setData("DATE_E", EndDate);
        //========pangben modify 20110523 start
        parm.setData("REGION_CODE", Operator.getRegion());
        //========pangben modify 20110523 stop
        //生成的结果集
        TParm result = STAOut_5Tool.getInstance().selectDiseaseSum(parm);
        for (int i = 0; i < result.getCount("STA_DATE"); i++) {
            result.setData("STA_DATE", i, STADATE);
            result.setData("CONFIRM_FLG", i, "N");
            result.setData("CONFIRM_USER", i, "");
            result.setData("CONFIRM_DATE", i, "");
            result.setData("OPT_USER", i, Operator.getID());
            result.setData("OPT_TERM", i, Operator.getIP());
            //=========pangben modify 20110523 start
            result.setData("REGION_CODE", i, Operator.getRegion());
            //=========pangben modify 20110523 stop
        }
        TParm re = TIOM_AppServer.executeAction(
            "action.sta.STAOut_5Action",
            "insertSTA_OUT_05", result);
        if (re.getErrCode() < 0) {
            this.messageBox_("生成失败！");
            return;
        }
        this.messageBox_("生成成功！");
        gridBind(STADATE); //绑定新生成的数据
    }
    /**
     * 生成日期段报表数据
     */
    private void generate_DayData() {
        S_TYPE = "day"; //记录统计类型
        String DATE_S = this.getText("DATE_S").replace("/", "");
        String DATE_E = this.getText("DATE_E").replace("/", "");
        TParm parm = new TParm();
        if (DATE_S.length() <= 0 || DATE_E.length() <= 0) {
            this.messageBox_("请选择日期范围");
            ( (TTextFormat)this.getComponent("DATE_S")).grabFocus();
            return;
        }
        parm.setData("DATE_S", DATE_S);
        parm.setData("DATE_E", DATE_E);
        //=======pangben modify 20110523 start
        parm.setData("REGION_CODE", Operator.getRegion());
        //=======pangben modify 20110523 stop
        TParm result = STAOut_5Tool.getInstance().selectDiseaseSum(parm);
        if (result.getErrCode() < 0) {
            this.messageBox_("生成失败 " + result.getErrText());
            return;
        }
        Day_Data = result;//记录日期段查询出的数据作为报表数据使用
        gridBind(result);
    }

    /**
     * 表格数据绑定
     */
    private void gridBind(String STADATE) {
        TTable table = (TTable)this.getComponent("Table");
        String sql = STASQLTool.getInstance().getSTA_OUT_05(STADATE,Operator.getRegion());//======pangben modify 20110523
        table.setSQL(sql);
        table.retrieve();
        table.setDSValue();
        DATA_StaDate = STADATE;
    }
    /**
     * 表格数据绑定(日期段报表)
     */
    private void gridBind(TParm data) {
        TTable table = (TTable)this.getComponent("Table_Read");
        table.setParmValue(data);
        //记录查询日期段 用于报表打印
        DATE_Start = this.getText("DATE_S");
        DATE_End = this.getText("DATE_E");
    }
    /**
     * 保存
     */
    public void onSave() {
        //如果不是组长权限  那么已经提交的数据不可再修改
        if(!LEADER.equals("2")){
            if (STA_CONFIRM_FLG) {
                this.messageBox_("数据已经提交，不能修改");
                return;
            }
        }
        TTable table = (TTable)this.getComponent("Table");
        table.acceptText();
        TDataStore ds = table.getDataStore();
        String optUser = Operator.getID();
        String optIp = Operator.getIP();
        //获取服务器时间
        Timestamp CONFIRM_DATE = SystemTool.getInstance().getDate();
        //是否提交
        boolean submit = ( (TCheckBox)this.getComponent("Submit")).isSelected();
        String message = "修改成功！"; //提示语
        if (submit)
            message = "提交成功！";
        for (int i = 0; i < ds.rowCount(); i++) {
            //判断是否提交
            if (submit) {
                ds.setItem(i, "CONFIRM_FLG", "Y");
                ds.setItem(i, "CONFIRM_USER", optUser);
                ds.setItem(i, "CONFIRM_DATE", CONFIRM_DATE);
            }
            else {
                ds.setItem(i, "CONFIRM_FLG", "N");
                ds.setItem(i, "CONFIRM_USER", "");
                ds.setItem(i, "CONFIRM_DATE", "");
            }
            ds.setItem(i, "OPT_USER", optUser);
            ds.setItem(i, "OPT_TERM", optIp);
            ds.setItem(i, "OPT_DATE", CONFIRM_DATE);
        }
        if (ds.update()){
            this.messageBox_(message);
            if(submit)
                STA_CONFIRM_FLG = true;
        }
        else
            this.messageBox_("操作失败！");
    }

    /**
     * 打印
     */
    public void onPrint() {
        //检查数据的日期
        if (DATA_StaDate.trim().length() <= 0&&(DATE_Start.length()<=0||DATE_End.length()<=0)) {
            this.messageBox("没有需要打印的数据");
            return;
        }
        //检查数据是否是提交状态
//        if (!STA_CONFIRM_FLG) {
//            this.messageBox_("数据没有提交，不能打印");
//            return;
//        }
        DecimalFormat df = new DecimalFormat("0.00");
        TParm print = new TParm();
        if("month".equals(S_TYPE)){//如果是月统计 查询数据库 取得该月统计信息
            print = STAOut_5Tool.getInstance().selectPrint(DATA_StaDate,Operator.getRegion());//===pangben modify 20110523
        }else if("day".equals(S_TYPE)){//如果是日期段统计 获取table上的数据进行打印
            print = Day_Data;
        }
        int printCount = print.getCount("STA_DATE");
        if (printCount <= 0) {
            return;
        }
        TParm printDate = new TParm();
        String dept = "";
        for(int i=0;i<printCount;i++){
            if(!dept.equals(print.getValue("DEPT_DESC",i))){
                dept = print.getValue("DEPT_DESC",i);
                printDate.addData("DEPT_DESC",dept);
            }else{
                printDate.addData("DEPT_DESC","");
            }
            printDate.addData("ICD_DESC",print.getValue("ICD_DESC",i));
            printDate.addData("SEQ",print.getValue("SEQ",i));
            printDate.addData("DATA_01",print.getValue("DATA_01",i));
            printDate.addData("DATA_02",print.getValue("DATA_02",i));
            printDate.addData("DATA_03",df.format(print.getData("DATA_03",i)));
            printDate.addData("DATA_04",df.format(print.getData("DATA_04",i)));
            printDate.addData("DATA_05",df.format(print.getData("DATA_05",i)));
            printDate.addData("DATA_06",df.format(print.getData("DATA_06",i)));
            printDate.addData("DATA_07",df.format(print.getData("DATA_07",i)));
            printDate.addData("DATA_08",df.format(print.getData("DATA_08",i)));
        }
        printDate.setCount(printCount);
        printDate.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
        printDate.addData("SYSTEM", "COLUMNS", "ICD_DESC");
        printDate.addData("SYSTEM", "COLUMNS", "SEQ");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_01");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_02");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_03");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_04");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_05");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_06");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_07");
        printDate.addData("SYSTEM", "COLUMNS", "DATA_08");
        //打印参数
        String dataDate = "";
        if("month".equals(S_TYPE)){
            dataDate = DATA_StaDate.substring(0, 4) + "年" +
                DATA_StaDate.subSequence(4, 6) + "日";
        }else if("day".equals(S_TYPE)){
            dataDate = DATE_Start+"~"+DATE_End;
        }
        TParm parm = new TParm();
        //日期
        parm.setData("date","TEXT",dataDate);
        parm.setData("T1", printDate.getData());
        parm.setData("unit","TEXT",Operator.getHospitalCHNFullName());
        this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_OUT_05.jhw", parm);
    }


    /**
     * 检查是否可以生成数据
     * 检核是否已经该月份的数据 数据是否已经提交  true:可以生成  false:不可以生成
     * @param STADATE String
     * @return boolean
     */
    private boolean canGeneration(String STADATE) {
        boolean can = false;
        //检核数据状态
        int reFlg = STATool.getInstance().checkCONFIRM_FLG("STA_OUT_05",
            STADATE,Operator.getRegion());//======pangben modify 20110523 添加参数
        //数据已经提交
        if (reFlg == 2) {
            this.messageBox_("数据已经提交，不能重新生成！");
            can = false;
            STA_CONFIRM_FLG = true; //标识数据已经提交
        }
        //数据存在但没有提交
        else if (reFlg == 1) {
            switch (this.messageBox("提示信息",
                                    "数据已存在，是否重新生成？", this.YES_NO_OPTION)) {
                case 0: //生成
                    can = true;
                    break;
                case 1: //不生成
                    can = false;
                    break;
            }
        }
        else if (reFlg == 0) { //没有数据
            can = true;
        }
        else if (reFlg == -1) { //数据检核错误
            can = false;
        }
        if(STA_CONFIRM_FLG==true)
            this.setValue("Submit",true);
        else
            this.setValue("Submit",false);
        return can;
    }

    /**
     * SEQ序号替换对应的病重名称
     */
    public class OrderList
        extends TLabel {
        TDataStore dataStore;
        public OrderList(TDataStore dataStore1){
            dataStore = dataStore1;
        }
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("SEQ");
            Vector d = (Vector) parm.getData("ICD_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i)))
                    return "" + d.get(i);
            }
            return s;
        }
    }
    /**
     * 根据统计方式 显示或者隐藏月份或者时间段控件
     * @param type String "month":月统计  "days":日期统计
     */
    private void setDateVisble(String type){
        if ("month".equals(type)) { //月统计
            this.setText("tLabel_0", "统计月份");
            ( (TTextFormat)this.getComponent("STA_DATE")).setVisible(true); //显示月份控件
            ( (TTextFormat)this.getComponent("DATE_S")).setVisible(false); //隐藏日期段控件
            ( (TTextFormat)this.getComponent("DATE_E")).setVisible(false); //隐藏日期段控件
            ( (TLabel)this.getComponent("tLabel_4")).setVisible(false);
            callFunction("UI|save|setEnabled", true); //保存按钮设置
            callFunction("UI|Table_Read|setVisible", false); //隐藏日统计表格
            callFunction("UI|Table|setVisible", true); //显示月统计表格
        }
        else if ("days".equals(type)) {
            this.setText("tLabel_0", "统计日期");
            ( (TTextFormat)this.getComponent("STA_DATE")).setVisible(false); //显示月份控件
            ( (TTextFormat)this.getComponent("DATE_S")).setVisible(true); //隐藏日期段控件
            ( (TTextFormat)this.getComponent("DATE_E")).setVisible(true); //隐藏日期段控件
            ( (TLabel)this.getComponent("tLabel_4")).setVisible(true);
            callFunction("UI|save|setEnabled", false); //保存按钮设置
            callFunction("UI|Table_Read|setVisible", true); //显示日统计表格
            callFunction("UI|Table|setVisible", false); //隐藏月统计表格
        }
        onClear();//清空
    }
    /**
     * 月统计Radio 选择事件
     */
    public void onR_MONTH_Click() {
        this.setDateVisble("month");
    }

    /**
     * 日期段统计Radio 选择事件
     */
    public void onR_DAYS_Click() {
        this.setDateVisble("days");
    }
    /**
     * 设置 日期段查询的初始时间
     */
    private void initDate(){
        //设置初始时间
        Timestamp lastMonth = STATool.getInstance().getLastMonth();//获取上个月的月份
        //上月第一天
        this.setValue("DATE_S",StringTool.getTimestamp(StringTool.getString(lastMonth,"yyyyMM")+"01","yyyyMMdd"));
        //上月最后一天
        this.setValue("DATE_E",STATool.getInstance().getLastDayOfMonth(StringTool.getString(lastMonth,"yyyyMM")));
    }
}
