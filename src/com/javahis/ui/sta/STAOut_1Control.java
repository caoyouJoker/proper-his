package com.javahis.ui.sta;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.sta.STAOut_1Tool;
import com.dongyang.ui.TTable;
import jdo.sta.STASQLTool;
import java.text.DecimalFormat;
import jdo.sys.Operator;
import com.dongyang.ui.TCheckBox;
import com.dongyang.jdo.TDataStore;
import jdo.sys.SystemTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import jdo.sta.STATool;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TLabel;
import java.sql.Timestamp;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;

/**
 * <p>Title: 医院、卫生院病床使用及病患动态（卫统2表1）</p>
 *
 * <p>Description: 医院、卫生院病床使用及病患动态（卫统2表1）</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-8
 * @version 1.0
 */
public class STAOut_1Control
    extends TControl {
    private boolean CONFIRM_FLG = false;//记录是否提交
    private String DATA_StaDate = "";//记录目前显示的数据的主键(日期)
    private String S_TYPE = "";//记录当前统计的类型  month:月统计   day:日期段统计
    private String DATE_Start = "";//记录起始日期
    private String DATE_End = "";//记录截止日期
    private TParm DATA_PrintDay=null;//记录日期统计的数据用于打印
    private String LEADER = "";//记录是否是组长权限  如果LEADER=2那么就是组长权限

    public void onInit(){
        super.init();
        //设置初始时间
        this.setValue("STA_DATE",STATool.getInstance().getLastMonth());
        this.addEventListener("Table1->" + TTableEvent.CHANGE_VALUE,
                              "onCellChange");
        initDate();
        //初始化权限
        if(this.getPopedem("LEADER")){
            LEADER = "2";
        }
    }
    /**
     * 生成数据
     */
    public void onQuery(){
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
        CONFIRM_FLG = false;//初始默认数据没有提交
        String STADATE = this.getText("STA_DATE").replace("/","");
        if(STADATE.trim().length()<=0){
            this.messageBox_("请选择统计的年月！");
            return;
        }
        //检查是否可以重新生成数据
        if(!canGeneration(STADATE)){
            //如果不能重新生成数据，绑定原有数据
            gridBind(STADATE);//数据绑定
            return;
        }
        insertData(STADATE);
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
        //===========pangben modify 20110520 start
        parm.setData("REGION_CODE", Operator.getRegion());
         //===========pangben modify 20110520 stop
        DATA_PrintDay = STAOut_1Tool.getInstance().getDataByDate(parm);
        if (DATA_PrintDay.getErrCode() < 0) {
            this.messageBox_("生成失败 " + DATA_PrintDay.getErrText());
            return;
        }
        gridBind(DATA_PrintDay);
    }
    /**
     * 检查是否可以生成数据
     * 检核是否已经该月份的数据 数据是否已经提交  true:可以生成  false:不可以生成
     * @param STADATE String
     * @return boolean
     */
    private boolean canGeneration(String STADATE){
        boolean can=false;
        TParm out_1Parm = new TParm();
        out_1Parm.setData("STA_DATE",STADATE);
        //=============pangben modify 20110520 start 添加区域条件
        if(null!=Operator.getRegion()&&Operator.getRegion().length()>0)
            out_1Parm.setData("REGION_CODE",Operator.getRegion());
         //=============pangben modify 20110520 stop
        //查询是否存在该月份的数据
        TParm out_1 = STAOut_1Tool.getInstance().selectSTA_OUT_01(out_1Parm);
        if(out_1.getCount()>0){
            //如果存在数据并且已经提交，则不能重新生成
            if(out_1.getValue("CONFIRM_FLG",0).equals("Y")){
                this.messageBox_("数据已经提交，不能重新生成！");
                can = false;
                CONFIRM_FLG = true;//标识数据已经提交
            }else{
                switch (this.messageBox("提示信息",
                                        "数据已存在，是否重新生成？", this.YES_NO_OPTION)) {
                    case 0://生成
                        can = true;
                        break;
                    case 1://不生成
                        can = false;
                        break;
                }
            }
        }
        else
            can = true;
        if(CONFIRM_FLG==true)
            this.setValue("Submit",true);
        else
            this.setValue("Submit",false);
        return can;
    }
    /**
     * 给两个Table初始化SQL语句，绑定数据
     */
    private void gridBind(String STADATE){
        TTable table1 = (TTable)this.getComponent("Table1");
        TTable table2 = (TTable)this.getComponent("Table2");
        //==============pangben modify 20110520 添加参数
        table1.setSQL(STASQLTool.getInstance().getSTA_OUT_01_1(STADATE,Operator.getRegion()));
        table1.retrieve();
        table1.setDSValue();
        //==============pangben modify 20110520 添加参数
        table2.setSQL(STASQLTool.getInstance().getSTA_OUT_01_2(STADATE,Operator.getRegion()));
        table2.retrieve();
        table2.setDSValue();
        DATA_StaDate = table1.getDataStore().getItemString(0,0);
    }
    /**
     * 给两个Table绑定数据
     */
    private void gridBind(TParm data){
        //复制data 以便绑定第二个Table 如果两个表格绑定同一个TParm 清空时会报错
        TParm data1 = new TParm();
        data1.addRowData(data,0);
        TTable table1 = (TTable)this.getComponent("Table1_Read");
        TTable table2 = (TTable)this.getComponent("Table2_Read");
        table1.setParmValue(data);
        table2.setParmValue(data1);
        //记录查询日期段 用于报表打印
        DATE_Start = this.getText("DATE_S");
        DATE_End = this.getText("DATE_E");
    }

    /**
     * 生成新数据 并插入到STA_OUT_01表中
     * @param STADATE String
     */
    private void insertData(String STADATE){
        DecimalFormat df = new DecimalFormat("0.00"); //设定Double类型格式
        String StartDate = STADATE + "01"; //每月第一天
        //获取此月份的最后一天
        String EndDate = StringTool.getString(STATool.getInstance().
                                              getLastDayOfMonth(STADATE),
                                              "yyyyMMdd");
        TParm p = new TParm();
        p.setData("DATE_S", StartDate);
        p.setData("DATE_E", EndDate);
        //==========pangben modify 20110520 添加区域参数
        p.setData("REGION_CODE", Operator.getRegion());
        //获取 门诊中间表 的月数据和
        TParm opd_Daily = STAOut_1Tool.getInstance().selectSTA_OPD_DAILY_Sum(p);
        //获取 工作报表 的月数据和
        TParm daily02 = STAOut_1Tool.getInstance().selectSTA_DAILY_02_Sum(p);
        //获取工作日报的月最后一天的数据  为了获取 实有病床数等只能按照天来统计的数据
        TParm daily02_day = STAOut_1Tool.getInstance().getSTA_DAILY_02_DAY_SUM(
            EndDate,Operator.getRegion());//==========pangben modify 20110520 添加区域参数
        if (opd_Daily.getErrCode() < 0 || daily02.getErrCode() < 0 ||
            daily02_day.getErrCode() < 0) {
            this.messageBox_("数据汇总错误！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("DATA_01", "1");
        parm.setData("DATA_02", daily02.getInt("DATA_02", 0) +
                     daily02.getInt("DATA_03", 0) +
                     opd_Daily.getInt("OTHER_NUM", 0)); //总计=门诊+急诊+其他
        parm.setData("DATA_03",
                     daily02.getInt("DATA_02", 0) + daily02.getInt("DATA_03", 0)); //门急诊计
        parm.setData("DATA_04", daily02.getData("DATA_02", 0)); //门诊人次
        parm.setData("DATA_05",daily02.getData("DATA_03",0));//急诊计
        parm.setData("DATA_06",daily02.getData("DATA_04",0));//急诊死亡
        parm.setData("DATA_07",daily02.getData("DATA_05",0));//观察人数
        parm.setData("DATA_08",daily02.getData("DATA_06",0));//观察死亡
        parm.setData("DATA_09",opd_Daily.getData("HRM_NUM",0));//健康检查人数
        parm.setData("DATA_10",daily02.getData("DATA_08",0));//入院人数
        parm.setData("DATA_11",daily02.getData("DATA_09",0));//出院人数总计
        parm.setData("DATA_12",daily02.getData("DATA_10",0));//其中病人数，计
        parm.setData("DATA_13",daily02.getData("DATA_11",0));//治愈
        parm.setData("DATA_14",daily02.getData("DATA_12",0));//好转
        parm.setData("DATA_15",daily02.getData("DATA_13",0));//未愈
        parm.setData("DATA_16",daily02.getData("DATA_14",0));//死亡
        parm.setData("DATA_17","");//手术人次  暂无
        parm.setData("DATA_18",daily02_day.getData("DATA_17",0));//实有病床数
        parm.setData("DATA_19",daily02.getData("DATA_18",0));//实际开放总床日数
        parm.setData("DATA_20",daily02_day.getData("DATA_19",0));//平均开放床位数
        parm.setData("DATA_21",daily02.getData("DATA_20",0));//实际占用总床日数
        parm.setData("DATA_22",daily02.getData("DATA_21",0));//出院者占用总床日数
        parm.setData("DATA_23",daily02.getInt("DATA_10",0)==0?"":df.format(daily02.getDouble("DATA_11",0)/daily02.getDouble("DATA_10",0)*100));//治愈率
        parm.setData("DATA_24",daily02.getInt("DATA_10",0)==0?"":df.format(daily02.getDouble("DATA_12",0)/daily02.getDouble("DATA_10",0)*100));//好转率
        parm.setData("DATA_25",daily02.getInt("DATA_10",0)==0?"":df.format(daily02.getDouble("DATA_14",0)/daily02.getDouble("DATA_10",0)*100));//病死率
        parm.setData("DATA_26", daily02_day.getInt("DATA_17", 0) == 0 ? "" :
                     df.format((daily02.getDouble("DATA_11", 0) +
                                daily02.getDouble("DATA_12", 0) +
                                daily02.getDouble("DATA_13", 0) +
                                daily02.getDouble("DATA_14", 0) +
                                daily02.getDouble("DATA_15", 0) +
                                daily02.getDouble("DATA_15_1", 0)) /
                               daily02_day.getDouble("DATA_17", 0))
            ); //床位周转次数
        parm.setData("DATA_27",STATool.getInstance().getDaysOfMonth(STADATE));//病床工作日
        if (daily02.getInt("DATA_09", 0) != 0)
            parm.setData("DATA_29",
                         daily02.getInt("DATA_21", 0) / daily02.getInt("DATA_09", 0)); //出院者平均住院日
        else
            parm.setData("DATA_29", 0);
        //每床与每日门、急诊诊次之比
        if (daily02.getDouble("DATA_02", 0) + daily02.getDouble("DATA_03", 0) != 0)
            parm.setData("DATA_30",
            		StringTool.round((daily02.getDouble("DATA_03", 0) /
                            (daily02.getDouble("DATA_02", 0) +
                                    daily02.getDouble("DATA_03", 0))), 2));
        else
            parm.setData("DATA_30", "");
        //计算每百门急诊入院人数  与文档不同
        if (daily02.getInt("DATA_02", 0) + daily02.getInt("DATA_03", 0) != 0) {
            double chu = daily02.getDouble("DATA_02", 0) +
                daily02.getDouble("DATA_03", 0);
            double DATA_31 = daily02.getDouble("DATA_08", 0) / (chu / 100);
            parm.setData("DATA_31", DATA_31);
        }
        else
            parm.setData("DATA_31", 0);
        //门急诊诊次占总诊次
        double sumZc = opd_Daily.getDouble("OTHER_NUM",0)+daily02.getDouble("DATA_02",0)+daily02.getDouble("DATA_03",0);//总诊次=门诊＋急诊＋其他
        if(sumZc!=0)
            parm.setData("DATA_32",(daily02.getDouble("DATA_02",0)+daily02.getDouble("DATA_03",0))/sumZc*100);
        else
            parm.setData("DATA_32","");
        //急诊病死率
        if(daily02.getDouble("DATA_03",0)!=0)
            parm.setData("DATA_33",df.format(daily02.getDouble("DATA_04",0)/daily02.getDouble("DATA_03",0)*100));
        else
            parm.setData("DATA_33",0);
        //观察室病死率
        if(daily02.getDouble("DATA_05",0)!=0)
            parm.setData("DATA_34",df.format(daily02.getDouble("DATA_06",0)/daily02.getDouble("DATA_05",0)*100));
        else
            parm.setData("DATA_34",0);
        //病床使用率
        if(daily02.getDouble("DATA_18",0)!=0){
            parm.setData("DATA_28",daily02.getDouble("DATA_20",0)/daily02.getDouble("DATA_18",0)*100);//病床使用率
        }else{
            parm.setData("DATA_28","");
        }
        //必要参数
        parm.setData("STA_DATE",STADATE);
        parm.setData("CONFIRM_FLG","N");
        parm.setData("CONFIRM_USER",Operator.getID());
        parm.setData("CONFIRM_DATE","");
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        //==============pangben modify 20110520 start 删除使用
        parm.setData("REGION_CODE",Operator.getRegion());
        //==============pangben modify 20110520 stop
        TParm result = TIOM_AppServer.executeAction(
            "action.sta.STAOut_1Action",
            "insertSTA_OUT_01", parm);
        if(result.getErrCode()<0){
            this.messageBox_("数据生成失败！");
            return;
        }
        this.gridBind(STADATE);//绑定已经生成的数据
    }

    /**
     * 保存
     */
    public void onSave(){
        //判断数据是否已经提交
        //如果不是组长权限  那么已经提交的数据不可再修改
        if(!LEADER.equals("2")){
            if (CONFIRM_FLG) {
                this.messageBox_("数据已经提交，不可再次修改！");
                return;
            }
        }
        String message = "保存成功！";//提示信息
        TTable table1 = (TTable)this.getComponent("Table1");
        TTable table2 = (TTable)this.getComponent("Table2");
        table1.acceptText();
        table2.acceptText();
        //检查表中是否有数据
        if(table1.getRowCount()<=0||table2.getRowCount()<=0)
            return;
        TDataStore ds = table2.getDataStore();
        ds.showDebug();
        ds.setItem(0,"OPT_USER",Operator.getID());//设置修改人员信息
        ds.setItem(0,"OPT_TERM",Operator.getIP());//设置修改IP
        ds.setItem(0,"OPT_DATE",SystemTool.getInstance().getDate());//设置修改时间
        //判断是否提交
        if(((TCheckBox)this.getComponent("Submit")).isSelected()){
            ds.setItem(0,"CONFIRM_FLG","Y");//设置状态为 提交
            ds.setItem(0,"CONFIRM_USER",Operator.getID());//设置提交人
            ds.setItem(0,"CONFIRM_DATE",SystemTool.getInstance().getDate());//提交时间
            message = "提交成功!";
        }
        TParm parm = new TParm();
        parm.setData("SQL1",table1.getUpdateSQL());
        parm.setData("SQL2",ds.getUpdateSQL());
        TParm re = TIOM_AppServer.executeAction(
            "action.sta.STAOut_1Action",
            "updateSTA_OUT_01", parm);
        if(re.getErrCode()<0){
            this.messageBox_("操作失败！");
            return;
        }
        this.messageBox_(message);
        if(((TCheckBox)this.getComponent("Submit")).isSelected())
                CONFIRM_FLG = true;
    }
    /**
     * 清空
     */
    public void onClear(){
        //设置初始时间
        this.setValue("STA_DATE",STATool.getInstance().getLastMonth());
        initDate();
        this.clearValue("Submit");
        TTable table1 = (TTable)this.getComponent("Table1");
        TTable table2 = (TTable)this.getComponent("Table2");
        table1.removeRowAll();
        table1.resetModify();
        table2.removeRowAll();
        table2.resetModify();
        TTable table1R = (TTable)this.getComponent("Table1_Read");
        TTable table2R = (TTable)this.getComponent("Table2_Read");
        table2R.removeRowAll();
        table1R.removeRowAll();
        CONFIRM_FLG = false; //记录是否提交
        DATA_StaDate = ""; //记录目前显示的数据的主键(日期)
        S_TYPE = "";//记录当前统计的类型  month:月统计   day:日期段统计
        DATE_Start = "";//记录起始日期
        DATE_End = "";//记录截止日期
        DATA_PrintDay = null;
    }
    /**
     * 打印
     */
    public void onPrint(){
        if (DATA_StaDate.trim().length() <= 0&&(DATE_Start.length()<=0||DATE_End.length()<=0)) {
            return;
        }
//        if(!CONFIRM_FLG){
//            this.messageBox_("数据提交后才能生成报表");
//            return;
//        }
        TParm printParm = new TParm();
        //报表显示的年月
        String dataDate = "";
        if("month".equals(S_TYPE)){
            dataDate = DATA_StaDate.substring(0, 4) + "年" +
                DATA_StaDate.subSequence(4, 6) + "日";
        }else if("day".equals(S_TYPE)){
            dataDate = DATE_Start+"~"+DATE_End;
        }
        String printDate = StringTool.getString(SystemTool.getInstance().getDate(),"yyyy年MM月dd日");
        TParm Basic = new TParm();//报表基本参数
        Basic.setData("Date",dataDate);//数据日期
        Basic.setData("Units",Operator.getHospitalCHNFullName());//填报单位
        Basic.setData("printDate",printDate);
        TParm data = new TParm();
        if("month".equals(S_TYPE)){//如果是月统计 查询数据库 取得该月统计信息
            TParm select = new TParm();
            select.setData("STA_DATE", DATA_StaDate);
            //===========pangben modify 20110523 start
            select.setData("REGION_CODE", Operator.getRegion());
             //===========pangben modify 20110523 stop
            data = STAOut_1Tool.getInstance().selectSTA_OUT_01(select);
        }else if("day".equals(S_TYPE)){//如果是日期段统计 获取table上的数据进行打印
            data = DATA_PrintDay;
        }
        if(data==null)
            return;
        if(data.getErrCode()<0){
            return;
        }
        Basic.setData("BC","补充资料：医院全年开设家庭病床 "+data.getValue("DATA_35",0)+" 张");
        printParm.setData("Basic",Basic.getData());
        printParm.setData("Data",data.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_OUT_01.jhw", printParm);
    }
    /**
     * 根据统计方式 显示或者隐藏月份或者时间段控件
     * @param type String "month":月统计  "days":日期统计
     */
    private void setDateVisble(String type){
        if("month".equals(type)){//月统计
            this.setText("tLabel_0","统计月份");
            ((TTextFormat)this.getComponent("STA_DATE")).setVisible(true);//显示月份控件
            ((TTextFormat)this.getComponent("DATE_S")).setVisible(false);//隐藏日期段控件
            ((TTextFormat)this.getComponent("DATE_E")).setVisible(false);//隐藏日期段控件
            ((TLabel)this.getComponent("tLabel_2")).setVisible(false);
            callFunction("UI|save|setEnabled", true); //保存按钮设置
            callFunction("UI|Table1_Read|setVisible", false);//隐藏日统计表格
            callFunction("UI|Table2_Read|setVisible", false);
            callFunction("UI|Table1|setVisible", true);//显示月统计表格
            callFunction("UI|Table2|setVisible", true);
        }
        else if("days".equals(type)){
            this.setText("tLabel_0","统计日期");
            ((TTextFormat)this.getComponent("STA_DATE")).setVisible(false);//显示月份控件
            ((TTextFormat)this.getComponent("DATE_S")).setVisible(true);//隐藏日期段控件
            ((TTextFormat)this.getComponent("DATE_E")).setVisible(true);//隐藏日期段控件
            ((TLabel)this.getComponent("tLabel_2")).setVisible(true);
            callFunction("UI|save|setEnabled", false); //保存按钮设置
            callFunction("UI|Table1_Read|setVisible", true);//显示日统计表格
            callFunction("UI|Table2_Read|setVisible", true);
            callFunction("UI|Table1|setVisible", false);//隐藏月统计表格
            callFunction("UI|Table2|setVisible", false);
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
    /**
     * 表格值改变事件
     */
    public void onCellChange(Object obj) {
        TTable table1 = (TTable)this.getComponent("Table1");
        TTable table2 = (TTable)this.getComponent("Table2");
        //当前编辑的单元格
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return;
        int colunm = node.getColumn();//获取选中列
        DecimalFormat df = new DecimalFormat("0.00");
        double ban = 0;//记录观察室死亡率
        if(node.getTable().getTag().equals("Table1")&&colunm==7){
            if(table1.getItemDouble(0,6)>0){
                ban = Double.valueOf(node.getValue().toString())/table1.getItemDouble(0,6)*100;
                table2.setItem(0,"DATA_34",df.format(ban));
            }
        }
        if(node.getTable().getTag().equals("Table1")&&colunm==6){
            if(Integer.valueOf(node.getValue().toString())>0){
                ban = table1.getItemDouble(0,7)/Integer.valueOf(node.getValue().toString())*100;
                table2.setItem(0,"DATA_34",df.format(ban));
            }
        }
    }
}
