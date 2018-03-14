package com.javahis.ui.sta;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.sta.STATool;
import com.dongyang.ui.TTable;
import jdo.sta.STAIn_05Tool;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.jdo.TDataStore;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.dongyang.ui.TCheckBox;
import java.text.DecimalFormat;
import jdo.sta.STASQLTool;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TLabel;
import jdo.sta.STADeptListTool;
import java.util.Map;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: STA_IN_05出院者来源及其他项目报表</p>
 *
 * <p>Description: STA_IN_05出院者来源及其他项目报表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-24
 * @version 1.0
 */
public class STAIn_05Control
    extends TControl {
    private boolean STA_CONFIRM_FLG = false; //记录是否提交
    private String DATA_StaDate = ""; //记录目前显示的数据的主键(日期)
    private String S_TYPE = "";//记录当前统计的类型  month:月统计   day:日期段统计
    private String DATE_Start = "";//记录起始日期
    private String DATE_End = "";//记录截止日期
    private String DAY_DEPT = "";//记录按照日期段统计时统计的科室
    private String LEADER = "";//记录是否是组长权限  如果LEADER=2那么就是组长权限
    DecimalFormat df = new DecimalFormat("0.00");

    /**
     * 页面初始化
     */
    public void onInit() {
        super.init();
        //设置初始时间
        this.setValue("STA_DATE",STATool.getInstance().getLastMonth());
        initDate();
        this.setValue("PRINT_TYPE","3");
        //初始化权限
        if(this.getPopedem("LEADER")){
            LEADER = "2";
        }
        //===============pangben modify 20110526 start
        //科室
        TTextFormat DEPT_CODE = (TTextFormat)this.getComponent(
                "DEPT_CODE");
        //区域条件添加
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            DEPT_CODE.setPopupMenuSQL(DEPT_CODE.getPopupMenuSQL() +
                                      " AND REGION_CODE='" +
                                      Operator.getRegion() +
                                      "' ORDER BY DEPT_CODE");
        else
            DEPT_CODE.setPopupMenuSQL(DEPT_CODE.getPopupMenuSQL()+" ORDER BY DEPT_CODE");
        DEPT_CODE.onQuery();
        //===============pangben modify 20110526 stop
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
        //table1.resetModify();
        TTable table2 = (TTable)this.getComponent("Table_Read");
        table2.removeRowAll();
        TTextFormat dept = (TTextFormat)this.getComponent("DEPT_CODE");
        dept.setText("");
        dept.setValue("");
        this.setValue("PRINT_TYPE","3");//初始化打印模式
        STA_CONFIRM_FLG = false; //记录是否提交
        DATA_StaDate = ""; //记录目前显示的数据的主键(日期)
        S_TYPE = "";//记录当前统计的类型  month:月统计   day:日期段统计
        DATE_Start = "";//记录起始日期
        DATE_End = "";//记录截止日期
        DAY_DEPT = "";//记录按照日期段统计时统计的科室
    }
    /**
     * 查询
     */
    public void onQuery() {
       // TTable table = (TTable)this.getComponent("Table");
        String dept="";
        if (DATA_StaDate.length() > 0) {
            dept = this.getValueString("DEPT_CODE");
        }
        String STA_DATE = this.getText("STA_DATE").replace("/", "");
        gridBind(STA_DATE, dept);
    }
    /**
     * 生成数据
     */
    public void onGenerate(){
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
        //清空部门Combo
        TTextFormat dept = (TTextFormat)this.getComponent("DEPT_CODE");
        dept.setText("");
        dept.setValue("");

        STA_CONFIRM_FLG = false; //初始默认数据没有提交
        String STA_DATE = this.getText("STA_DATE").replace("/", "");
        if (STA_DATE.trim().length() <= 0) {
            this.messageBox_("请选择统计年月!");
            return;
        }
        //检查是否可以重新生成数据
        if (!canGeneration(STA_DATE)) {
            //如果不能重新生成数据，绑定原有数据
            gridBind(STA_DATE,""); //数据绑定
            return;
        }
        String StartDate = STA_DATE + "01";//每月第一天
        //获取此月份的最后一天
        String EndDate = StringTool.getString(STATool.getInstance().getLastDayOfMonth(STA_DATE),"yyyyMMdd");
        TParm parm = new TParm();
        parm.setData("DATE_S",StartDate);
        parm.setData("DATE_E",EndDate);
        parm.setData("REGION_CODE",Operator.getRegion());//==============pangben modify 20110524
        TParm data = STAIn_05Tool.getInstance().selectData(parm);
        if (data.getErrCode() < 0) {
            this.messageBox_("统计数据错误" + data.getErrName() + data.getErrText());
            return;
        }
        String userID = Operator.getID();
        String IP = Operator.getIP();
        for (int i = 0; i < data.getCount("STA_DATE"); i++) {
            data.setData("STA_DATE", i, STA_DATE);
            data.setData("CONFIRM_FLG", i, "N");
            data.setData("CONFIRM_USER", i, "");
            data.setData("CONFIRM_DATE", i, "");
            data.setData("OPT_USER", i, userID);
            data.setData("OPT_TERM", i, IP);
            data.setData("REGION_CODE", i,Operator.getRegion());//==============pangben modify 20110525
        }
        TParm re = TIOM_AppServer.executeAction(
            "action.sta.STAIn_05Action",
            "insertSTA_IN_05", data);
        if (re.getErrCode() < 0) {
            this.messageBox_("生成失败！");
            return;
        }
        this.messageBox_("生成成功！");
        gridBind(STA_DATE,""); //绑定新生成的数据
    }
    /**
    * 生成日期段报表数据
    */
   private void generate_DayData() {
       S_TYPE = "day"; //记录统计类型
       String Dept = getValueString("DEPT_CODE");
       String IPD_DEPT_CODE = ""; //住院科室CODE
       String STATION_CODE = "";//住院病区CODE
       //如果选择了统计科室 获取该科室的 IPD_DEPT_CODE
       if (Dept.trim().length() > 0) {
           //根据选中的科室CODE查询科室详细信息
           TParm deptInfo = STADeptListTool.getInstance().selectNewIPDDeptCode(this.
               getValueString("DEPT_CODE"),Operator.getRegion());//=========pangben modify 20110525
           if (deptInfo.getErrCode() < 0) {
               return;
           }
           IPD_DEPT_CODE = deptInfo.getValue("IPD_DEPT_CODE", 0);
       }
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
       if (IPD_DEPT_CODE.length() > 0) {
           parm.setData("DEPT_CODE", IPD_DEPT_CODE);
       }
       if(STATION_CODE.length()>0){
           parm.setData("OUT_STATION", STATION_CODE);
       }
       if(Dept.length()>0){
           parm.setData("DEPT", Dept);//中间科室CODE
       }
       parm.setData("REGION_CODE",Operator.getRegion());//=========pangben modify 20110525
       TParm result = STAIn_05Tool.getInstance().selectData(parm);
       if (result.getErrCode() < 0) {
           this.messageBox_("生成失败 " + result.getErrText());
           return;
       }
       DAY_DEPT = Dept;//记录统计的科室
       gridBind(result);
   }

    /**
     * 保存
     * ===============pangben modify 20110526 修改保存方法 之前的DataStore 有问题
     */
    public void onSave() {
        TTable table = (TTable)this.getComponent("Table");
        String STADATE=this.getText("STA_DATE").replace("/", "");
        if(table.getRowCount()<=0)
            return;
        //如果不是组长权限  那么已经提交的数据不可再修改
        if(!LEADER.equals("2")){
            if (STA_CONFIRM_FLG) {
                this.messageBox_("数据已经提交，不能修改");
                return;
            }
        }
        table.acceptText();
//        TDataStore ds = table.getDataStore();
        String optUser = Operator.getID();
        String optIp = Operator.getIP();
        //获取服务器时间
        Timestamp CONFIRM_DATE = SystemTool.getInstance().getDate();
        //是否提交
        boolean submit = ( (TCheckBox)this.getComponent("Submit")).isSelected();
        String message = "修改成功！"; //提示语
        if (submit)
            message = "提交成功！";
        TParm parm=new TParm();
        TParm tableParm = table.getParmValue();
		for (int i = 0; i< tableParm.getCount("DEPT_CODE"); i++) {
			// 判断是否提交
			if (submit) {
				tableParm.setData("CONFIRM_FLG", i, "Y");
				tableParm.setData("CONFIRM_USER", i, optUser);
				tableParm.setData("CONFIRM_DATE", i, CONFIRM_DATE);
			} else {
				tableParm.setData("CONFIRM_FLG", i, "N");
				tableParm.setData("CONFIRM_USER", i, "");
				tableParm.setData("CONFIRM_DATE", i, "");
			}
			tableParm.setData("OPT_USER", i, optUser);
			tableParm.setData("OPT_TERM", i, optIp);
			tableParm.setData("OPT_DATE", i, CONFIRM_DATE);
			if(!tableParm.getValue("STA_DATE", i).equals("")){
				parm.addRowData(tableParm, i);
			}
		}
//		System.out.println("------------parm----------"+parm);
        TParm re = TIOM_AppServer.executeAction(
           "action.sta.STAIn_05Action",
           "updateSTA_IN_05", parm);

        if (re.getErrCode()<0) {
            this.messageBox_("操作失败！");
            return;
        } else {
            this.messageBox_(message);
           if (submit)
               STA_CONFIRM_FLG = true;
        }
        gridBind(STADATE,"");
    }
    /**
     * 打印
     */
    public void onPrint(){
    	if ("Y".equals(this.getValueString("R_MONTH"))) {// 生成需要数据
			// 注意：月报数据是正规数据 保存在STA_IN_02表中
			S_TYPE="month";
			DATA_StaDate= this.getText("STA_DATE").replace("/", "");
		} else if ("Y".equals(this.getValueString("R_DAYS"))) {// 生成日期时段数据
			// 时间段查询的报表数据是非正规数据，只有查询显示功能，不进行保存不可修改
			S_TYPE="day";
			DATE_Start = this.getText("DATE_S").replace("/", "");
			DATE_End = this.getText("DATE_E").replace("/", "");
		}
        if (DATA_StaDate.trim().length() <= 0&&(DATE_Start.length()<=0||DATE_End.length()<=0)) {
            this.messageBox("没有需要打印的数据");
            return;
        }
//        if (!STA_CONFIRM_FLG) {
//            this.messageBox_("数据提交后才能生成报表");
//            return;
//        }
        TParm printParm = new TParm();
        String dataDate = "";
        if("month".equals(S_TYPE)){
            dataDate = DATA_StaDate.substring(0, 4) + "年" +
                DATA_StaDate.subSequence(4, 6) + "日";
        }else if("day".equals(S_TYPE)){
            dataDate = DATE_Start+"~"+DATE_End;
        }
        TParm data = this.getPrintData(DATA_StaDate);
//        System.out.println("data:"+data);
        if (data.getErrCode() < 0) {
            return;
        }
        printParm.setData("T1", data.getData());
        printParm.setData("TableHeader","TEXT","科        别");
        printParm.setData("unit","TEXT",Operator.getHospitalCHNFullName());//填报单位
        printParm.setData("date","TEXT",dataDate);//数据日期
        this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_IN_05.jhw", printParm);
    }

    /**
     * 获取打印数据
     * @param STA_DATE String
     * @return TParm
     */
    private TParm getPrintData(String STA_DATE){
        DecimalFormat df = new DecimalFormat("0.00");
        TParm printData = new TParm();//打印数据
        String printType = this.getValueString("PRINT_TYPE");//打印类型
        TParm DeptList = new TParm();
        Map deptIPD = STADeptListTool.getInstance().getIPDDeptMap(Operator.getRegion());//===========pangben modify 20110525
        //如果是日期段统计 并且选择了某一个科室进行统计，那么打印的时候只显示该科室信息 否则按照报表正常形式打印
        if ("day".equals(S_TYPE) && DAY_DEPT.length() > 0) {
            DeptList = STADeptListTool.getInstance().selectNewDeptByCode(DAY_DEPT,Operator.getRegion());//===========pangben modify 20110525
        }
        else {
            if (printType.equals("3")) { //按照三级科室打印
                //获取1，2，3级科室
                DeptList = STATool.getInstance().getDeptByLevel(new String[] {
                    "1", "2", "3"},Operator.getRegion());//===========pangben modify 20110525
            }
            else if (printType.equals("4")) { //按照四级科室打印
                //获取1，2，3,4级科室
                DeptList = STATool.getInstance().getDeptByLevel(new String[] {
                    "1", "2", "3", "4"},Operator.getRegion());//===========pangben modify 20110525
            }
        }
        if (DeptList.getErrCode() < 0) {
            return DeptList;
        }
        //获取数据
        TParm data = new TParm();
        if ("month".equals(S_TYPE)) { //如果是月统计 查询数据库 取得该月统计信息
            data = STAIn_05Tool.getInstance().selectSTA_IN_05(STA_DATE,Operator.getRegion());//===========pangben modify 20110525
        }
        else if ("day".equals(S_TYPE)) { //如果是日期段统计 获取table上的数据进行打印
            data = ( (TTable)this.getComponent("Table_Read")).getParmValue();
        }
        if (data.getErrCode() < 0) {
            return data;
        }
        boolean isOE = false; //记录科室是否包含门诊科室信息
        int rowCount = 0; //记录打印数据的行数
        for (int i = 0; i < DeptList.getCount(); i++) {
            isOE = false;
            String d_LEVEL = DeptList.getValue("DEPT_LEVEL", i); //部门等级
            String d_CODE = DeptList.getValue("DEPT_CODE", i); //中间部门CODE
            String d_DESC = DeptList.getValue("DEPT_DESC", i); //中间部门名称
            int subIndex = 0; //记录根据科室级别要截取CODE的长度
            //如果是一级科室 code长度为1
            if (d_LEVEL.equals("1")) {
                subIndex = 1;
            }
            //如果是二级科室 code长度为3
            else if (d_LEVEL.equals("2")) {
                subIndex = 3;
//                d_DESC = " " + d_DESC; //加入前方空格
            }
            //如果是三级科室 code长度为5
            else if (d_LEVEL.equals("3")) {
                subIndex = 5;
//                d_DESC = "  " + d_DESC; //加入前方空格
            }else if(d_LEVEL.equals("4")){
                subIndex = 7;
            }

            //定义变量 用来累加子部门的数值
            int DATA_01 = 0;
            int DATA_02 = 0;
            double DATA_03 = 0;
            int DATA_04 = 0;
            double DATA_05 = 0;
            int DATA_06 = 0;
            double DATA_07 = 0;
            int DATA_08 = 0;
            double DATA_09 = 0;
            int DATA_10 = 0;
            double DATA_11 = 0;
            int DATA_12 = 0;
            double DATA_13 = 0;
            int DATA_14 = 0;
            double DATA_15 = 0;
            int DATA_16 = 0;
            double DATA_17 = 0;
            int DATA_18 = 0;
            int DATA_19 = 0;
            double DATA_20 = 0;

            int deptCount = 0;//记录每个部门下的子部门，概率方面的数据需要取平均值
            //循环遍历数据 取出符合条件的部门的数据进行累加
            for(int j=0;j<data.getCount("STA_DATE");j++){
                String subDept = data.getValue("DEPT_CODE",j).substring(0,subIndex);
                //如果部门id截取了指定长度后 等于外层循环中的部门CODE那么就是外层循环的子部门，就进行累加
                if(subDept.equals(d_CODE)){
                    //判断该子科室是否是门急诊科室如果不是 那么继续下一次循环
                    if(deptIPD.get(data.getValue("DEPT_CODE",j)).toString().length()>0){
                        isOE = true; //包含门急诊科室  显示在报表上
                        DATA_01 += data.getInt("DATA_01", j);
                        DATA_02 += data.getInt("DATA_02", j);
                        DATA_04 += data.getInt("DATA_04", j);
                        DATA_06 += data.getInt("DATA_06", j);
                        DATA_08 += data.getInt("DATA_08", j);
                        DATA_10 += data.getInt("DATA_10", j);
                        DATA_12 += data.getInt("DATA_12", j);
                        DATA_14 += data.getInt("DATA_14", j);
                        DATA_16 += data.getInt("DATA_16", j);
                        DATA_17 += data.getDouble("DATA_17", j);
                        DATA_18 += data.getInt("DATA_18", j);
                        DATA_19 += data.getInt("DATA_19", j);
                        deptCount++; //累计共汇总的多少个4级部门的数据
                    }
                }
            }
            if(DATA_18!=0){
                DATA_20 = (double)DATA_19/(double)DATA_18*100;//成功率
            }
            if(DATA_01!=0){
                DATA_03 = (double)DATA_02/(double)DATA_01*100;//本市本区，百分比
                DATA_05 = (double)DATA_04/(double)DATA_01*100;//本市外区，百分比
                DATA_07 = (double)DATA_06/(double)DATA_01*100;//本省外市，百分比
                DATA_09 = (double)DATA_08/(double)DATA_01*100;//外省市
                DATA_11 = (double)DATA_10 / (double)DATA_01 *100;//三日确诊率
                DATA_13 = (double)DATA_12 / (double)DATA_01 *100;//24小时入院死亡率
                DATA_15 = (double)DATA_14 / (double)DATA_01 *100;//48小时入院死亡率
            }
            if(isOE){
                printData.addData("DEPT_DESC", d_DESC);
                printData.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
                printData.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
                printData.addData("DATA_03",
                                  DATA_03 == 0 ? "" : df.format(DATA_03));
                printData.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
                printData.addData("DATA_05",
                                  DATA_05 == 0 ? "" : df.format(DATA_05));
                printData.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
                printData.addData("DATA_07",
                                  DATA_07 == 0 ? "" : df.format(DATA_07));
                printData.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
                printData.addData("DATA_09",
                                  DATA_09 == 0 ? "" : df.format(DATA_09));
                printData.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
                printData.addData("DATA_11",
                                  DATA_11 == 0 ? "" : df.format(DATA_11));
                printData.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
                printData.addData("DATA_13",
                                  DATA_13 == 0 ? "" : df.format(DATA_13));
                printData.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
                printData.addData("DATA_15",
                                  DATA_15 == 0 ? "" : df.format(DATA_15));
                printData.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
                printData.addData("DATA_17",
                                  DATA_17 == 0 ? "" : df.format(DATA_17));
                printData.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
                printData.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
                printData.addData("DATA_20",
                                  DATA_20 == 0 ? "" : df.format(DATA_20));
                rowCount++;
            }
        }
        printData.setCount(rowCount);
        printData.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
        printData.addData("SYSTEM", "COLUMNS", "DATA_01");
        printData.addData("SYSTEM", "COLUMNS", "DATA_02");
        printData.addData("SYSTEM", "COLUMNS", "DATA_03");
        printData.addData("SYSTEM", "COLUMNS", "DATA_04");
        printData.addData("SYSTEM", "COLUMNS", "DATA_05");
        printData.addData("SYSTEM", "COLUMNS", "DATA_06");
        printData.addData("SYSTEM", "COLUMNS", "DATA_07");
        printData.addData("SYSTEM", "COLUMNS", "DATA_08");
        printData.addData("SYSTEM", "COLUMNS", "DATA_09");
        printData.addData("SYSTEM", "COLUMNS", "DATA_10");
        printData.addData("SYSTEM", "COLUMNS", "DATA_11");
        printData.addData("SYSTEM", "COLUMNS", "DATA_12");
        printData.addData("SYSTEM", "COLUMNS", "DATA_13");
        printData.addData("SYSTEM", "COLUMNS", "DATA_14");
        printData.addData("SYSTEM", "COLUMNS", "DATA_15");
        printData.addData("SYSTEM", "COLUMNS", "DATA_16");
        printData.addData("SYSTEM", "COLUMNS", "DATA_17");
        printData.addData("SYSTEM", "COLUMNS", "DATA_18");
        printData.addData("SYSTEM", "COLUMNS", "DATA_19");
        printData.addData("SYSTEM", "COLUMNS", "DATA_20");
        return printData;
    }
    /**
     * 表格数据绑定
     */
    private void gridBind(String STADATE,String deptCode) {
        TTable table = (TTable)this.getComponent("Table");
        String sql = STASQLTool.getInstance().getSTA_IN_05(STADATE,Operator.getRegion(),deptCode);//============pangben modify 20110524
       //=======pangben modify 20110526 start
        TParm result = new TParm(TJDODBTool.getInstance().select(
            sql));
        if (result.getCount() <= 0) {
			table.removeRowAll();
			return;
		}
        int DATA_01 = 0;
        int DATA_02 = 0;
        double DATA_03 = 0;
        int DATA_04 = 0;
        double DATA_05 = 0;
        int DATA_06 = 0;
        double DATA_07 = 0;
        int DATA_08 = 0;
        double DATA_09 = 0;
        int DATA_10 = 0;
        double DATA_11 = 0;
        int DATA_12 = 0;
        double DATA_13 = 0;
        int DATA_14 = 0;
        double DATA_15 = 0;
        int DATA_16 = 0;
        double DATA_17 = 0;
        int DATA_18 = 0;
        int DATA_19 = 0;
        double DATA_20 = 0;
		for (int i = 0; i < result.getCount(); i++) {
			DATA_01 += result.getInt("DATA_01", i);
			DATA_02 += result.getInt("DATA_02", i);
			DATA_04 += result.getInt("DATA_04", i);
			DATA_06 += result.getInt("DATA_06", i);
//			DATA_07 += result.getInt("DATA_07", i);
			DATA_08 += result.getInt("DATA_08", i);
//			DATA_09 += result.getInt("DATA_09", i);
			DATA_10 += result.getInt("DATA_10", i);	
			DATA_12 += result.getInt("DATA_12", i);
			DATA_14 += result.getInt("DATA_14", i);
//			DATA_15 += result.getInt("DATA_15", i);
			DATA_16 += result.getInt("DATA_16", i);
			DATA_17 += result.getInt("DATA_17", i);
			DATA_18 += result.getInt("DATA_18", i);
			DATA_19 += result.getDouble("DATA_19", i);
			DATA_20 += result.getDouble("DATA_20", i);
		}
		if (DATA_18 != 0) {
            DATA_20 = (double) DATA_19 / (double) DATA_18 * 100; //成功率
        }
        if (DATA_01 != 0) {
            DATA_03 = (double) DATA_02 / (double) DATA_01 * 100; //本市本区，百分比
            DATA_05 = (double) DATA_04 / (double) DATA_01 * 100; //本市外区，百分比
            DATA_07 = (double) DATA_06 / (double) DATA_01 * 100; //本省外市，百分比
            DATA_09 = (double) DATA_08 / (double) DATA_01 * 100; //外省市
            DATA_11 = (double) DATA_10 / (double) DATA_01 * 100; //三日确诊率
            DATA_13 = (double) DATA_12 / (double) DATA_01 * 100; //24小时入院死亡率
            DATA_15 = (double) DATA_14 / (double) DATA_01 * 100; //48小时入院死亡率
        }
		result.addData("DEPT_CODE", "合计:");
		result.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
        result.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
        result.addData("DATA_03", DATA_03 == 0 ? "" : df.format(DATA_03));
        result.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
        result.addData("DATA_05", DATA_05 == 0 ? "" : df.format(DATA_05));
        result.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
        result.addData("DATA_07", DATA_07 == 0 ? "" : df.format(DATA_07));
        result.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
        result.addData("DATA_09", DATA_09 == 0 ? "" : df.format(DATA_09));
        result.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
        result.addData("DATA_11", DATA_11 == 0 ? "" : df.format(DATA_11));
        result.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
        result.addData("DATA_13", DATA_13 == 0 ? "" : df.format(DATA_13));
        result.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
        result.addData("DATA_15", DATA_15 == 0 ? "" : df.format(DATA_15));
        result.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
        result.addData("DATA_17", DATA_17 == 0 ? "" : df.format(DATA_17));
        result.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
        result.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
        result.addData("DATA_20", DATA_20 == 0 ? "" : df.format(DATA_20));
        result.addData("STA_DATE", "");
        result.addData("STATION_CODE", "");
        result.addData("REGION_CODE", "");
		table.setParmValue(result);
       // table.retrieve();
      //  table.setDSValue();
      //=======pangben modify 20110526 stop
        DATA_StaDate = STADATE;
    }
    /**
     * 表格数据绑定
     */
    private void gridBind(TParm result) {
        TTable table = (TTable)this.getComponent("Table_Read");
        if (result.getCount("DEPT_CODE") <= 0) {
			table.removeRowAll();
			return;
		}
        int DATA_01 = 0;
        int DATA_02 = 0;
        double DATA_03 = 0;
        int DATA_04 = 0;
        double DATA_05 = 0;
        int DATA_06 = 0;
        double DATA_07 = 0;
        int DATA_08 = 0;
        double DATA_09 = 0;
        int DATA_10 = 0;
        double DATA_11 = 0;
        int DATA_12 = 0;
        double DATA_13 = 0;
        int DATA_14 = 0;
        double DATA_15 = 0;
        int DATA_16 = 0;
        double DATA_17 = 0;
        int DATA_18 = 0;
        int DATA_19 = 0;
        double DATA_20 = 0;
		for (int i = 0; i < result.getCount("DEPT_CODE"); i++) {
			DATA_01 += result.getInt("DATA_01", i);
			DATA_02 += result.getInt("DATA_02", i);
			DATA_04 += result.getInt("DATA_04", i);
			DATA_06 += result.getInt("DATA_06", i);
//			DATA_07 += result.getInt("DATA_07", i);
			DATA_08 += result.getInt("DATA_08", i);
//			DATA_09 += result.getInt("DATA_09", i);
			DATA_10 += result.getInt("DATA_10", i);	
			DATA_12 += result.getInt("DATA_12", i);
			DATA_14 += result.getInt("DATA_14", i);
//			DATA_15 += result.getInt("DATA_15", i);
			DATA_16 += result.getInt("DATA_16", i);
			DATA_17 += result.getInt("DATA_17", i);
			DATA_18 += result.getInt("DATA_18", i);
			DATA_19 += result.getInt("DATA_19", i);
			DATA_20 += result.getDouble("DATA_20", i);
		}
		if (DATA_18 != 0) {
            DATA_20 = (double) DATA_19 / (double) DATA_18 * 100; //成功率
        }
        if (DATA_01 != 0) {
            DATA_03 = (double) DATA_02 / (double) DATA_01 * 100; //本市本区，百分比
            DATA_05 = (double) DATA_04 / (double) DATA_01 * 100; //本市外区，百分比
            DATA_07 = (double) DATA_06 / (double) DATA_01 * 100; //本省外市，百分比
            DATA_09 = (double) DATA_08 / (double) DATA_01 * 100; //外省市
            DATA_11 = (double) DATA_10 / (double) DATA_01 * 100; //三日确诊率
            DATA_13 = (double) DATA_12 / (double) DATA_01 * 100; //24小时入院死亡率
            DATA_15 = (double) DATA_14 / (double) DATA_01 * 100; //48小时入院死亡率
        }
		result.addData("DEPT_CODE", "合计:");
		result.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
        result.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
        result.addData("DATA_03", DATA_03 == 0 ? "" : df.format(DATA_03));
        result.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
        result.addData("DATA_05", DATA_05 == 0 ? "" : df.format(DATA_05));
        result.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
        result.addData("DATA_07", DATA_07 == 0 ? "" : df.format(DATA_07));
        result.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
        result.addData("DATA_09", DATA_09 == 0 ? "" : df.format(DATA_09));
        result.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
        result.addData("DATA_11", DATA_11 == 0 ? "" : df.format(DATA_11));
        result.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
        result.addData("DATA_13", DATA_13 == 0 ? "" : df.format(DATA_13));
        result.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
        result.addData("DATA_15", DATA_15 == 0 ? "" : df.format(DATA_15));
        result.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
        result.addData("DATA_17", DATA_17 == 0 ? "" : df.format(DATA_17));
        result.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
        result.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
        result.addData("DATA_20", DATA_20 == 0 ? "" : df.format(DATA_20));
		table.setParmValue(result);
        //记录查询日期段 用于报表打印
        DATE_Start = this.getText("DATE_S");
        DATE_End = this.getText("DATE_E");
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
        int reFlg = STATool.getInstance().checkCONFIRM_FLG("STA_IN_05",
            STADATE,Operator.getRegion());//=============pangben modify 20110524
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
     * 根据统计方式 显示或者隐藏月份或者时间段控件
     * @param type String "month":月统计  "days":日期统计
     */
    private void setDateVisble(String type){
        if("month".equals(type)){//月统计
            this.setText("tLabel_0","统计月份");
            ((TTextFormat)this.getComponent("STA_DATE")).setVisible(true);//显示月份控件
            ((TTextFormat)this.getComponent("DATE_S")).setVisible(false);//隐藏日期段控件
            ((TTextFormat)this.getComponent("DATE_E")).setVisible(false);//隐藏日期段控件
            ((TLabel)this.getComponent("tLabel_1")).setVisible(false);
            callFunction("UI|query|setEnabled", true); //生成按钮设置
            callFunction("UI|save|setEnabled", true); //保存按钮设置
            callFunction("UI|Table_Read|setVisible", false);//隐藏日统计表格
            callFunction("UI|Table|setVisible", true);//显示月统计表格
        }
        else if("days".equals(type)){
            this.setText("tLabel_0","统计日期");
            ((TTextFormat)this.getComponent("STA_DATE")).setVisible(false);//显示月份控件
            ((TTextFormat)this.getComponent("DATE_S")).setVisible(true);//隐藏日期段控件
            ((TTextFormat)this.getComponent("DATE_E")).setVisible(true);//隐藏日期段控件
            ((TLabel)this.getComponent("tLabel_1")).setVisible(true);
            callFunction("UI|query|setEnabled", false); //生成按钮设置
            callFunction("UI|save|setEnabled", false); //保存按钮设置
            callFunction("UI|Table_Read|setVisible", true);//显示日统计表格
            callFunction("UI|Table|setVisible", false);//隐藏月统计表格
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
