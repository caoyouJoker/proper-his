package com.javahis.ui.sta;

import com.dongyang.control.*;
import com.dongyang.ui.TCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;
import jdo.sta.STAIn_04TTool;
import java.text.DecimalFormat;
import jdo.sta.STATool;
import jdo.sta.STADeptListTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWord;
import java.awt.Color;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;
import com.dongyang.ui.TTextFormat;

/**
 * <p>Title: STA_IN_04诊断质量台帐</p>
 *
 * <p>Description: STA_IN_04诊断质量台帐</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-26
 * @version 1.0
 */
public class STAIn_04TControl
        extends TControl {
    public void onInit() {
        super.init();
        showFormat();
    }

    /**
     *清空
     */
    public void onClear() {
        this.clearValue(
                "B_YEAR;B_MONTH;STA_DEPT1;QY1;QY2;Q_Month1;Q_Month2;STA_DEPT2;Check2");
        this.setValue("Check1", true);
        this.setValue("B_Radio1",true);
        this.setValue("QS_1",true);
    }

    /**
     * 添加区域过滤
     *====== pangben modify 20110525
     */
    public void showFormat() {
        TTextFormat combo_dept = (TTextFormat)this.getComponent("STA_DEPT1");
        TTextFormat combo_dept1 = (TTextFormat)this.getComponent("STA_DEPT2");
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            combo_dept.setPopupMenuSQL(combo_dept.getPopupMenuSQL() +
                                       " WHERE REGION_CODE='" +
                                       Operator.getRegion() +
                                       "' ORDER BY DEPT_CODE");
            combo_dept1.setPopupMenuSQL(combo_dept1.getPopupMenuSQL() +
                                        " WHERE REGION_CODE='" +
                                        Operator.getRegion() +
                                        "' ORDER BY DEPT_CODE");
        } else {
            combo_dept.setPopupMenuSQL(combo_dept.getPopupMenuSQL() +
                                       " ORDER BY DEPT_CODE");
            combo_dept1.setPopupMenuSQL(combo_dept1.getPopupMenuSQL() +
                                        " ORDER BY DEPT_CODE");

        }

        combo_dept.onQuery();
        combo_dept1.onQuery();
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm result;
        //本期
        if (((TCheckBox)this.getComponent("Check1")).isSelected()) {
            result = bQuery(); //获取数据
            if (result.getErrCode() < 0) {
                this.messageBox_(result.getErrName() + result.getErrText());
                return;
            }
            gridBind(result,0);//数据绑定
        }
        //趋势
        else if(((TCheckBox)this.getComponent("Check2")).isSelected()){
            result = q_Query();//获取趋势数据
            if(result.getErrCode()<0){
                this.messageBox_(result.getErrName()+result.getErrText());
                return;
            }
            gridBind(result,1);//数据绑定
        }
    }
    /**
     * 查询本期的结果集
     * @return TParm
     */
    private TParm bQuery(){
        TParm result = new TParm();//结果
        String STA_DATE = "";
        String DEPT_CODE = this.getValueString("STA_DEPT1"); //科室
        if (DEPT_CODE.trim().length() <= 0) {
            result.setErr( -1, "请选择科室");
            return result;
        }
        //获取该科室的信息
        TParm DeptList = STADeptListTool.getInstance().selectDeptByCode(DEPT_CODE,"",Operator.getRegion());//===========pangben modify 20110525
        if(DeptList.getErrCode()<0){
            return DeptList;
        }
        String DEPT_DESC = DeptList.getValue("DEPT_DESC", 0); //中间部门名称
        TParm parm = new TParm();//参数
        parm.setData("Dept",DEPT_CODE);
        //年
        if(((TRadioButton)this.getComponent("B_Radio1")).isSelected()){
            STA_DATE = this.getText("B_YEAR");
            if (STA_DATE.trim().length() <= 0) {
                result.setErr( -1, "请填写要查询的日期");
                return result;
            }
            parm.setData("Year",STA_DATE);//查询一年的参数
        }else if(((TRadioButton)this.getComponent("B_Radio2")).isSelected()){//月
            STA_DATE = this.getText("B_MONTH").replace("/","");
            if (STA_DATE.trim().length() <= 0) {
                result.setErr( -1, "请填写要查询的日期");
                return result;
            }
            parm.setData("Month",STA_DATE);//查询一月的参数
        }
        //===========pangben modify 20110523 start
       if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
           parm.setData("REGION_CODE", Operator.getRegion());
       //===========pangben modify 20110523 stop

        TParm data = STAIn_04TTool.getInstance().selectBQ(parm);
        if(data.getErrCode()<0){
            return data;
        }
        //查询的数据为空
        if(data.getCount("STA_DATE")<=0){
            result.setErr(-1,"没有符合条件的数据");
            ((TTable)this.getComponent("Table")).removeRowAll();
            return result;
        }
        result = sumResult(data,DEPT_DESC);//对查询结果进行汇总
        return result;
    }
    /**
     * 趋势查询
     * @return TParm
     */
    public TParm q_Query() {
        TParm result = new TParm(); //结果
        String DEPT_CODE = this.getValueString("STA_DEPT2"); //科室
        if (DEPT_CODE.trim().length() <= 0) {
            result.setErr( -1, "请选择科室");
            return result;
        }
        //获取该科室的信息
        TParm DeptList = STADeptListTool.getInstance().selectDeptByCode(DEPT_CODE,"",Operator.getRegion());//======pangben modify 20110525
        if(DeptList.getErrCode()<0){
            return DeptList;
        }
        String DEPT_DESC = DeptList.getValue("DEPT_DESC", 0); //中间部门名称
        //判断日期顺序
        Timestamp a =null;
        Timestamp b =null;
        int type = 0;

        String DATE_S = ""; //起始日期
        String DATE_E = ""; //结束日期
        if ( ( (TRadioButton)this.getComponent("QS_1")).isSelected()) { //年
            DATE_S = this.getText("QY1").replace("/","");
            DATE_E = this.getText("QY2").replace("/","");
            a = (Timestamp)this.getValue("QY1");
            b = (Timestamp)this.getValue("QY2");
            type = 1;
        }
        else if ( ( (TRadioButton)this.getComponent("QS_2")).isSelected()) { //月
            DATE_S = this.getText("Q_Month1").replace("/","");
            DATE_E = this.getText("Q_Month2").replace("/","");
            a = (Timestamp)this.getValue("Q_Month1");
            b = (Timestamp)this.getValue("Q_Month2");
            type = 0;
        }
        if (DATE_S.trim().length() <= 0 || DATE_E.trim().length() <= 0) {
            result.setErr( -1, "请填写日期范围");
            return result;
        }
        if(a.compareTo(b)==1){
            result.setErr( -1, "统计起日日期不能晚于截止日期");
            return result;
        }
        if(type==1){//根据 年份统计 将年份加入月份，以便SQL查询数据库
            DATE_S = DATE_S + "01";
            DATE_E = DATE_E + "12";
        }
        TParm parm = new TParm();
        parm.setData("Dept",DEPT_CODE);
        parm.setData("DATE_S",DATE_S);
        parm.setData("DATE_E", DATE_E);
        //===========pangben modify 20110523 start
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            parm.setData("REGION_CODE", Operator.getRegion());
        //===========pangben modify 20110523 stop

        TParm data = STAIn_04TTool.getInstance().selectQS(parm);
        if (data.getErrCode() < 0) {
            return data;
        }
        //查询的数据为空
        if (data.getCount("STA_DATE") <= 0) {
            result.setErr( -1, "没有符合条件的数据");
            ((TTable)this.getComponent("Table")).removeRowAll();
            return result;
        }
        result = sumQResult(data,DEPT_DESC,DATE_S,DATE_E,type);
        return result;
    }
    /**
     * 汇总趋势数据
     * @param data TParm  数据
     * @param DEPT_DESC String 科室名称
     * @param DATE_S String 起始时间
     * @param DATE_E String 截止时间
     * @param Type int 汇总类型 0：月；1：年
     * @return TParm
     */
    private TParm sumQResult(TParm data,String DEPT_DESC,String DATE_S,String DATE_E,int Type){
        TParm result = new TParm();
        TParm rowData = new TParm();
        TParm dateData = null;
        if(Type==0){//月趋势
            String month = DATE_S;
            //循环累加初始月份，等于截止月份时结束
            do {
                dateData = getDataByDate(data,month,0);//筛选指定日期的数据
                rowData = sumResult(dateData,DEPT_DESC);//汇总数据
                rowData.addData("STA_DATE",month.substring(0,4)+"年"+month.substring(4)+"月");//加入日期
                result.addRowData(rowData,0);//提取行数据加入到结果集中
                month = STATool.getInstance().rollMonth(month, 1);
            }
            while (!month.equals(STATool.getInstance().rollMonth(DATE_E, 1)));
        }else if(Type==1){//年汇总
            int year = Integer.parseInt(DATE_S.substring(0,4));
            int end = Integer.parseInt(DATE_E.substring(0,4));
            //循环累加初始年份，等于截止年份时结束
            do{
                rowData = sumResult(getDataByDate(data,String.valueOf(year),1),DEPT_DESC);//汇总数据
                rowData.addData("STA_DATE",year+"年");//加入日期
                result.addRowData(rowData,0);//提取行数据加入到结果集中
                year++;
            }while(year!=(end+1));
        }
        return result;
    }
    /**
     * 根据日期筛选数据
     * @param data TParm  要进行筛选的数据
     * @param Date String 日期格式：yyyyMM 或者 yyyy
     * @param type int 0:月；1：年
     * @return TParm
     */
    private TParm getDataByDate(TParm data,String Date,int type){
        TParm result = new TParm();
        String STA_DATE = "";
        for(int i=0;i<data.getCount("STA_DATE");i++){
            if(type==0)//按月分统计
                STA_DATE = data.getValue("STA_DATE",i);
            else if(type==1)//按年统计
                STA_DATE = data.getValue("STA_DATE",i).substring(0,4);
            if(STA_DATE.equals(Date)){
                result.addRowData(data,i);
            }
        }
        return result;
    }

    /**
     * 根据部门汇总结果
     * @param data TParm  数据
     * @param DEPT_DESC String  部门名称
     * @return TParm
     */
    private TParm sumResult(TParm data, String DEPT_DESC) {
        DecimalFormat df = new DecimalFormat("0.00");
        TParm printData = new TParm(); //结果数据
        int DATA_01 = 0;
        int DATA_02 = 0;
        int DATA_03 = 0;
        int DATA_04 = 0;
        double DATA_05 = 0;
        double DATA_06 = 0;
        int DATA_07 = 0;
        int DATA_08 = 0;
        int DATA_09 = 0;
        int DATA_10 = 0;
        double DATA_11 = 0;
        double DATA_12 = 0;
        int DATA_13 = 0;
        int DATA_14 = 0;
        int DATA_15 = 0;
        int DATA_16 = 0;
        double DATA_17 = 0;
        double DATA_18 = 0;
        int DATA_19 = 0;
        int DATA_20 = 0;
        int DATA_21 = 0;
        int DATA_22 = 0;
        double DATA_23 = 0;
        double DATA_24 = 0;
        int count = data.getCount("STA_DATE");
        //循环遍历数据 取出符合条件的部门的数据进行累加
        for (int j = 0; j < count; j++) {
            DATA_01 += data.getInt("DATA_01", j);
            DATA_02 += data.getInt("DATA_02", j);
            DATA_03 += data.getInt("DATA_03", j);
            DATA_04 += data.getInt("DATA_04", j);
            DATA_07 += data.getInt("DATA_07", j);
            DATA_08 += data.getInt("DATA_08", j);
            DATA_09 += data.getInt("DATA_09", j);
            DATA_10 += data.getInt("DATA_10", j);
            DATA_13 += data.getInt("DATA_13", j);
            DATA_14 += data.getInt("DATA_14", j);
            DATA_15 += data.getInt("DATA_15", j);
            DATA_16 += data.getInt("DATA_16", j);
            DATA_19 += data.getInt("DATA_19", j);
            DATA_20 += data.getInt("DATA_20", j);
            DATA_21 += data.getInt("DATA_21", j);
            DATA_22 += data.getInt("DATA_22", j);
            DATA_23 += data.getDouble("DATA_23", j);
            DATA_24 += data.getDouble("DATA_24", j);
        }
        if (DATA_01 != 0) {
            DATA_05 = (double) DATA_02 / (double) DATA_01 * 100; //计算门诊与住院符合率
            DATA_06 = (double) DATA_03 / (double) DATA_01 * 100; //计算门诊与住院不符合率
        }
        if (DATA_07 != 0) {
            DATA_11 = (double) DATA_08 / (double) DATA_07 * 100; //计算“入院与出院”符合率
            DATA_12 = (double) DATA_09 / (double) DATA_07 * 100; //计算“入院与出院”不符合率
        }
        if (DATA_13 != 0) {
            DATA_17 = (double) DATA_14 / (double) DATA_13 * 100; //计算临床与病理符合率
            DATA_18 = (double) DATA_15 / (double) DATA_13 * 100; //计算临床与病理不符合率
        }
        if (DATA_19 != 0) {
            DATA_23 = (double) DATA_20 / (double) DATA_19 * 100; //计算“手术前与手术后”符合率
            DATA_24 = (double) DATA_21 / (double) DATA_19 * 100; //计算“手术前与手术后”不符合率
        }
        printData.addData("DEPT_DESC", DEPT_DESC);
        printData.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
        printData.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
        printData.addData("DATA_03", DATA_03 == 0 ? "" : DATA_03);
        printData.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
        printData.addData("DATA_05", DATA_05 == 0 ? "" : df.format(DATA_05));
        printData.addData("DATA_06", DATA_06 == 0 ? "" : df.format(DATA_06));
        printData.addData("DATA_07", DATA_07 == 0 ? "" : DATA_07);
        printData.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
        printData.addData("DATA_09", DATA_09 == 0 ? "" : DATA_09);
        printData.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
        printData.addData("DATA_11", DATA_11 == 0 ? "" : df.format(DATA_11));
        printData.addData("DATA_12", DATA_12 == 0 ? "" : df.format(DATA_12));
        printData.addData("DATA_13", DATA_13 == 0 ? "" : DATA_13);
        printData.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
        printData.addData("DATA_15", DATA_15 == 0 ? "" : DATA_15);
        printData.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
        printData.addData("DATA_17", DATA_17 == 0 ? "" : df.format(DATA_17));
        printData.addData("DATA_18", DATA_18 == 0 ? "" : df.format(DATA_18));
        printData.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
        printData.addData("DATA_20", DATA_20 == 0 ? "" : DATA_20);
        printData.addData("DATA_21", DATA_21 == 0 ? "" : DATA_21);
        printData.addData("DATA_22", DATA_22 == 0 ? "" : DATA_22);
        printData.addData("DATA_23", DATA_23 == 0 ? "" : df.format(DATA_23));
        printData.addData("DATA_24", DATA_24 == 0 ? "" : df.format(DATA_24));
        return printData;
    }

    /**
     * 绑定GRID
     * @param data TParm  数据
     * @param type int  数据类型 0:本期数据 1:趋势数据
     */
    private void gridBind(TParm data,int type){
        TTable table = (TTable)this.getComponent("Table");
        DecimalFormat df = new DecimalFormat("0.00");
        if(type==0){
            //设置表头
            table.setHeader("科室,80;门诊与出院合计,100;符合,60;不符合,60;未确诊,60;符合率,60;不符合率,80;入院与出院合计,100;符合,60;不符合,60;未确诊,60;符合率,60;不符合率,80;临床与病理合计,100;符合,60;不符合,60;未确诊,60;符合率,60;不符合率,80;术前与术后合计,100;符合,60;不符合,60;未确诊,60;符合率,60;不符合率,80");
            table.setLockColumns("all");//设置所有列不可编辑
            //设置焦点顺序
            table.setFocusIndexList("0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25");
            //设置对其方式
            table.setColumnHorizontalAlignmentData("0,left;1,right;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,right;23,right;24,right");
            //设定数据对应项  parmMap
            table.setParmMap("DEPT_DESC;DATA_01;DATA_02;DATA_03;DATA_04;DATA_05;DATA_06;DATA_07;DATA_08;DATA_09;DATA_10;DATA_11;DATA_12;DATA_13;DATA_14;DATA_15;DATA_16;DATA_17;DATA_18;DATA_19;DATA_20;DATA_21;DATA_22;DATA_23;DATA_24");
            table.setParmValue(data);//数据绑定
            table.retrieve();
        }else if(type==1){
            //设置表头
            table.setHeader("日期,80;科室,80;门诊与出院合计,100;符合,60;不符合,60;未确诊,60;符合率,60;不符合率,80;入院与出院合计,100;符合,60;不符合,60;未确诊,60;符合率,60;不符合率,80;临床与病理合计,100;符合,60;不符合,60;未确诊,60;符合率,60;不符合率,80;术前与术后合计,100;符合,60;不符合,60;未确诊,60;符合率,60;不符合率,80");
            table.setLockColumns("all");//设置所有列不可编辑
            //设置焦点顺序
            table.setFocusIndexList("0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26");
            //设置对其方式
            table.setColumnHorizontalAlignmentData("0,left;1,left;2,right;3,right;4,right;5,right;6,right;7,right;8,right;9,right;10,right;11,right;12,right;13,right;14,right;15,right;16,right;17,right;18,right;19,right;20,right;21,right;22,right;23,right;24,right;25,right");
            //设定数据对应项  parmMap
            table.setParmMap("STA_DATE;DEPT_DESC;DATA_01;DATA_02;DATA_03;DATA_04;DATA_05;DATA_06;DATA_07;DATA_08;DATA_09;DATA_10;DATA_11;DATA_12;DATA_13;DATA_14;DATA_15;DATA_16;DATA_17;DATA_18;DATA_19;DATA_20;DATA_21;DATA_22;DATA_23;DATA_24");
            int DATA_01 = 0;
            int DATA_02 = 0;
            int DATA_03 = 0;
            int DATA_04 = 0;
            double DATA_05 = 0;
            double DATA_06 = 0;
            int DATA_07 = 0;
            int DATA_08 = 0;
            int DATA_09 = 0;
            int DATA_10 = 0;
            double DATA_11 = 0;
            double DATA_12 = 0;
            int DATA_13 = 0;
            int DATA_14 = 0;
            int DATA_15 = 0;
            int DATA_16 = 0;
            double DATA_17 = 0;
            double DATA_18 = 0;
            int DATA_19 = 0;
            int DATA_20 = 0;
            int DATA_21 = 0;
            int DATA_22 = 0;
            double DATA_23 = 0;
            double DATA_24 = 0;
            int count = data.getCount("STA_DATE");
            if(data.getCount("STA_DATE")<=0)
            	return;
            //循环遍历数据 取出符合条件的部门的数据进行累加
            for (int j = 0; j < count; j++) {
                DATA_01 += data.getInt("DATA_01", j);
                DATA_02 += data.getInt("DATA_02", j);
                DATA_03 += data.getInt("DATA_03", j);
                DATA_04 += data.getInt("DATA_04", j);
                DATA_07 += data.getInt("DATA_07", j);
                DATA_08 += data.getInt("DATA_08", j);
                DATA_09 += data.getInt("DATA_09", j);
                DATA_10 += data.getInt("DATA_10", j);
                DATA_13 += data.getInt("DATA_13", j);
                DATA_14 += data.getInt("DATA_14", j);
                DATA_15 += data.getInt("DATA_15", j);
                DATA_16 += data.getInt("DATA_16", j);
                DATA_19 += data.getInt("DATA_19", j);
                DATA_20 += data.getInt("DATA_20", j);
                DATA_21 += data.getInt("DATA_21", j);
                DATA_22 += data.getInt("DATA_22", j);
                DATA_23 += data.getDouble("DATA_23", j);
                DATA_24 += data.getDouble("DATA_24", j);
            }
            if (DATA_01 != 0) {
                DATA_05 = (double) DATA_02 / (double) DATA_01 * 100; //计算门诊与住院符合率
                DATA_06 = (double) DATA_03 / (double) DATA_01 * 100; //计算门诊与住院不符合率
            }
            if (DATA_07 != 0) {
                DATA_11 = (double) DATA_08 / (double) DATA_07 * 100; //计算“入院与出院”符合率
                DATA_12 = (double) DATA_09 / (double) DATA_07 * 100; //计算“入院与出院”不符合率
            }
            if (DATA_13 != 0) {
                DATA_17 = (double) DATA_14 / (double) DATA_13 * 100; //计算临床与病理符合率
                DATA_18 = (double) DATA_15 / (double) DATA_13 * 100; //计算临床与病理不符合率
            }
            if (DATA_19 != 0) {
                DATA_23 = (double) DATA_20 / (double) DATA_19 * 100; //计算“手术前与手术后”符合率
                DATA_24 = (double) DATA_21 / (double) DATA_19 * 100; //计算“手术前与手术后”不符合率
            }
            data.addData("STA_DATE", "总计:");
            data.addData("DEPT_DESC", data.getValue("DEPT_DESC", 0));
            data.addData("DATA_01", DATA_01 == 0 ? "" : DATA_01);
            data.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
            data.addData("DATA_03", DATA_03 == 0 ? "" : DATA_03);
            data.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
            data.addData("DATA_05", DATA_05 == 0 ? "" : df.format(DATA_05));
            data.addData("DATA_06", DATA_06 == 0 ? "" : df.format(DATA_06));
            data.addData("DATA_07", DATA_07 == 0 ? "" : DATA_07);
            data.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
            data.addData("DATA_09", DATA_09 == 0 ? "" : DATA_09);
            data.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
            data.addData("DATA_11", DATA_11 == 0 ? "" : df.format(DATA_11));
            data.addData("DATA_12", DATA_12 == 0 ? "" : df.format(DATA_12));
            data.addData("DATA_13", DATA_13 == 0 ? "" : DATA_13);
            data.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
            data.addData("DATA_15", DATA_15 == 0 ? "" : DATA_15);
            data.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
            data.addData("DATA_17", DATA_17 == 0 ? "" : df.format(DATA_17));
            data.addData("DATA_18", DATA_18 == 0 ? "" : df.format(DATA_18));
            data.addData("DATA_19", DATA_19 == 0 ? "" : DATA_19);
            data.addData("DATA_20", DATA_20 == 0 ? "" : DATA_20);
            data.addData("DATA_21", DATA_21 == 0 ? "" : DATA_21);
            data.addData("DATA_22", DATA_22 == 0 ? "" : DATA_22);
            data.addData("DATA_23", DATA_23 == 0 ? "" : df.format(DATA_23));
            data.addData("DATA_24", DATA_24 == 0 ? "" : df.format(DATA_24));
            table.setParmValue(data);//数据绑定
            table.retrieve();
        }
    }

    /**
     * check1选中事件
     */
    public void check1Selected(){
        TCheckBox check1  = (TCheckBox)this.getComponent("Check1");
        TCheckBox check2  = (TCheckBox)this.getComponent("Check2");
        check1.setSelected(true);
        check2.setSelected(false);
    }
    /**
     * check2选中事件
     */
    public void check2Selected(){
        TCheckBox check1  = (TCheckBox)this.getComponent("Check1");
        TCheckBox check2  = (TCheckBox)this.getComponent("Check2");
        check1.setSelected(false);
        check2.setSelected(true);
    }
//    public void onSave(){
//        int year = Integer.parseInt("2006");
//            int end = Integer.parseInt("2009");
//            //循环累加初始年份，等于截止年份时结束
//            do{
//                this.messageBox_(""+year);
//                year++;
//            }while(year!=(end+1));
//
//    }
    /**
     * 设置word控件的初始化状态
     */
    private void wordInit(){
        //设置word控件初始格式
        TWord word = (TWord)this.getComponent("Word");
//        word.getWordText().getViewManager().PAGE_STYLE_H = 0; //设置纸张的上边距为0
        word.getWordText().getViewManager().backColor = new Color(255, 255, 255); //将背景设置为白色
        word.getWordText().getViewManager().PAGE_RECT = false; //将纸张的边线去除
        word.setFileName("%ROOT%\\config\\prt\\tu.jhw"); //设置路径
        word.setPreview(true); //设置为预览状态
        //图形参数 2维数组 默认为零
            double[][] tu = new double[][] {
                {0,0,0,0}, {0,0,0,0}, {0,0,0,0}
            };
            TParm obj = new TParm();
            obj.setData("Tu1","DATA",tu);
            word.setWordParameter(obj);
    }
    /**
     * 设置图形报表
     * @param parm TParm 数据
     * @param type int 数据类型  0:本期数据 1:趋势数据
     */
    public void setWordImg(TParm parm, int type) {
        if (type == 0) {
            TWord word = (TWord)this.getComponent("Word");
//            word.getWordText().getViewManager().PAGE_STYLE_H = 0;//设置纸张的上边距为0
            word.getWordText().getViewManager().backColor = new Color(255,255,255);//将背景设置为白色
            word.getWordText().getViewManager().PAGE_RECT = false;//将纸张的边线去除
            word.setFileName("%ROOT%\\config\\prt\\tu.jhw"); //设置路径
            word.setPreview(true); //设置为预览状态
            //将数据转化为参数
            //诊断符合数 数组  0:门诊与出院 符合;1:入院与出院 符合;2:临床与病理 符合;3:手术前与手术后 符合
            double[] a1 = new double[] {
                parm.getDouble("DATA_02", 0), parm.getDouble("DATA_08", 0),
                parm.getDouble("DATA_14", 0), parm.getDouble("DATA_20", 0)};
            //诊断不符合数 数组
            double[] a2 = new double[] {
                parm.getDouble("DATA_03", 0), parm.getDouble("DATA_09", 0),
                parm.getDouble("DATA_15", 0), parm.getDouble("DATA_21", 0)};

            //未确诊数  数组
            double[] a3 = new double[] {
                parm.getDouble("DATA_04", 0), parm.getDouble("DATA_10", 0),
                parm.getDouble("DATA_16", 0), parm.getDouble("DATA_22", 0)};
            //图形参数 2维数组
            double[][] tu = new double[][] {
                a1, a2, a3
            };
            TParm obj = new TParm();
            obj.setData("Tu1","DATA",tu);
            word.setWordParameter(obj);
        }

    }

    /**
     * 打印
     */
    public void onPrint() {
        if ( ( (TTable)this.getComponent("Table")).getRowCount() <= 0) {
            this.messageBox("没有需要打印的数据");
            return;
        }
        String colName = ""; //记录首列的字段名
        String dataDate = ""; //日期
        TParm printParm = new TParm();
        TParm printData = ( (TTable)this.getComponent("Table")).getParmValue(); //获取打印数据
        //检查是否有日期列,没有日期列打印的就是本期数据
        if (printData.getValue("STA_DATE").trim().length() <= 0) {
            if ( ( (TRadioButton)this.getComponent("B_Radio1")).isSelected()) { //年
                dataDate = this.getText("B_YEAR") + "年";
            }
            else if ( ( (TRadioButton)this.getComponent("B_Radio2")).isSelected()) { //月
                dataDate = this.getText("B_MONTH").replace("/", "年") + "月";
            }
            //本期数据显示部门
            printParm.setData("TableHeader", "TEXT", "科        别");
            colName = "DEPT_DESC";
        }
        else { //趋势数据
            if ( ( (TRadioButton)this.getComponent("QS_1")).isSelected()) { //年
                dataDate = this.getText("QY1") + "年 ~ " + this.getText("QY2") +
                    "年";
            }
            else if ( ( (TRadioButton)this.getComponent("QS_2")).isSelected()) { //月
                dataDate = this.getText("Q_Month1").replace("/", "年") + "月 ~ " +
                    this.getText("Q_Month2").replace("/", "年") + "月";
            }
            //趋势数据显示日期
            printParm.setData("TableHeader", "TEXT", "时        段");
            colName = "STA_DATE";
        }
        printData.setCount(printData.getCount("DEPT_DESC"));
        printData.addData("SYSTEM", "COLUMNS", colName);
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
        printData.addData("SYSTEM", "COLUMNS", "DATA_21");
        printData.addData("SYSTEM", "COLUMNS", "DATA_22");
        printData.addData("SYSTEM", "COLUMNS", "DATA_23");
        printData.addData("SYSTEM", "COLUMNS", "DATA_24");

        //基本信息
        TParm Basic = new TParm(); //报表基本参数
        Basic.setData("Date", dataDate); //数据日期
       // Basic.setData("unit", Operator.getHospitalCHNFullName()); //填报单位=====pangben modify 20110525
        Basic.setData("DeptDesc",
                      "（" + printData.getValue("DEPT_DESC", 0) + "）"); //统计的科室
        printParm.setData("Basic", Basic.getData());
        printParm.setData("T1", printData.getData());
        printParm.setData("unit","TEXT", Operator.getHospitalCHNFullName());
        this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_IN_04.jhw",
                             printParm);
    }
    /**
     * 汇出Excel
     */
    public void onExcel() {
        //得到UI对应控件对象的方法（UI|XXTag|getThis）
        TTable table = (TTable) callFunction("UI|Table|getThis");
        ExportExcelUtil.getInstance().exportExcel(table, "诊断质量台帐");
    }
}
