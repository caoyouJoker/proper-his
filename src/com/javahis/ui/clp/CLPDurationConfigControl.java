package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import jdo.clp.CLPManagemTool;
import com.dongyang.ui.TTable;
import java.util.regex.Pattern;
import jdo.sys.Operator;
import java.util.Map;
import com.dongyang.data.TNull;
import java.util.HashMap;
import com.dongyang.ui.TTextField;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.util.regex.Matcher;
import java.sql.Timestamp;
import com.dongyang.manager.TIOM_AppServer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.*;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.DateTool;
import java.util.Date;

/**
 * <p>Title:实际时程设定 </p>
 *
 * <p>Description: 实际时程设定</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPDurationConfigControl extends TControl {
    public CLPDurationConfigControl() {

    }

    private String case_no = "";
    private String clncPathCode = "";
    private String version="";//=========pangben 2012-5-28 版本
    private String dept_code="";//=========pangben 2012-5-28 科室
    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    /**
     * 页面初始化方法
     */
    public void onInit() {
        super.onInit();
        initTableEdit();
        initPage();
    }

    /**
     * 初始化
     */
    private void initPage() {
        TParm sendParm = (TParm)this.getParameter();
        String mr_no = (String) sendParm.getData("MR_NO");
        this.setValue("MR_NO", mr_no);
        String patName = (String) sendParm.getData("PAT_NAME");
        this.setValue("PAT_NAME", patName);
        clncPathCode = (String) sendParm.getData("CLNCPATH_CODE");
        this.setValue("CLNCPATH_CODE", clncPathCode);
        case_no = (String) sendParm.getData("CASE_NO");
        version = (String) sendParm.getData("VERSION");//=========pangben 2012-5-28 
        dept_code = (String) sendParm.getData("DEPT_CODE");//=========pangben 2012-5-28 
        this.onQuery();
    }

    /**
     * 初始化表格编辑时的事件
     */
    private void initTableEdit() {
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                         "onEditTable");
    }

    /**
     * 表格编辑时弹框Component com, int row, int column
     */
    public boolean onEditTable(Object obj) throws ParseException {
        TTableNode node = (TTableNode) obj;
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm tableParm = table.getParmValue();
        int column = node.getColumn();
        if (column == 7 || column == 8) {
            //验证时间
            if (!validDate(node,tableParm)) {
                return true;
            }
        }
        //开始时间不能小于入院时间验证
        if(column==7){
            if(!startDateIsValid(node)){
                return true;
            }
        }
        //处理数据-治疗开始天数验证
        if (column == 5) {
            if (!validStardDay(node)) {
                return true;
            }
        }
        //处理数据-治疗天数
        if (column == 6) {
            if (!validSchdDay(node)) {
                return true;
            }
        }
        //时程天数验证
        //验证治疗天数和开始天数是否合法
        if(column==5||column==6){
            if(!schdDayAndStardDayValid(node)){
                return true;
            }
        }
        //修正方法返回true时editTable 方法返回false 反之亦然 ,onEditTable 方法
        //返回true 时框架会把改动的值恢复成原来的值。
        modifyTableData(node);
        return false;
    }
    /**
     * 验证开始时间是否合法
     * @return boolean
     */
    private boolean startDateIsValid(TTableNode node) throws ParseException {
        boolean flag=true;
        Date time= format.parse(node.getValue()+"");
        Calendar nodeTime=Calendar.getInstance();
        nodeTime.setTimeInMillis(time.getTime());
        Calendar inDate=this.getInDate();
        if(nodeTime.getTimeInMillis()<inDate.getTimeInMillis()){
            this.messageBox("时程开始时间不能小于入院日间！");
            flag=false;
        }
        return flag;
    }
    /**
     * 验证治疗天数是否合法
     * @param node TTableNode
     * @return boolean
     */
    private boolean schdDayAndStardDayValid(TTableNode node){
        int column = node.getColumn();
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm tableParm = table.getParmValue();
        int selectedRow = node.getRow();
        String sustainedDays=tableParm.getValue("SUSTAINED_DAYS",selectedRow);
        String schdDay=tableParm.getValue("SCHD_DAY",selectedRow);
        //该事件中得到的编辑框的值为原始值，需要使用node.getValue()才能得到实际编辑的值
        if(column==5){
            sustainedDays=node.getValue()+"";
        }
        if(column==6){
             schdDay=node.getValue()+"";
        }
        if(selectedRow<tableParm.getCount()&&sustainedDays.equals(tableParm.getValue("SUSTAINED_DAYS",selectedRow+1))){
            if(!"1".equals(schdDay)){
                this.messageBox("该条记录的住院开始天数与下一时程的住院开始天数相同，故该条记录的治疗天数必须为1！");
                return false;
            }
        }
        if (selectedRow > 0 &&
            sustainedDays.equals(
                    tableParm.getValue("SUSTAINED_DAYS", selectedRow - 1))) {
            if (!"1".equals(tableParm.getValue("SCHD_DAY", selectedRow - 1))) {
                this.messageBox("该条记录的住院开始天数与上一时程的住院开始天数相同，故上一时程的治疗天数必须为1！");
                return false;
            }
        }

        return true;
    }
    /**
     * 验证时间
     * @return boolean
     */
    public boolean validDate(TTableNode node,TParm tableParm) throws
            ParseException {
        if (!DateTool.checkDate(node.getValue().toString(),
                                "yyyy/MM/dd HH:mm:ss")) {
            //时间格式不正确！
            this.messageBox("E0160");
            return false;
        }
        int column =  node.getColumn();
        int selectedIndx=node.getRow();
//        Date curDate=format.parse(node.getValue()+"");
//        if(column==7&&selectedIndx!=0){
//            Date beforeDate=format.parse(tableParm.getValue("END_DATE",selectedIndx-1));
//            if(curDate.getTime()<beforeDate.getTime()){
//                this.messageBox("开始时间不能小于前一时程的结束时间！");
//                return false;
//            }
//        }
        Date startDate=null;
        Date endDate=null;
        if(column==7){
            startDate=format.parse(node.getValue()+"");
            endDate=format.parse(tableParm.getValue("END_DATE",selectedIndx));
        }
        if(column==8){
            startDate=format.parse(tableParm.getValue("START_DATE",selectedIndx));
            endDate=format.parse(node.getValue()+"");
        }
        //System.out.println("startDate:::::"+startDate);
        //System.out.println("endDate:::::"+endDate);
        if(startDate.getTime()>endDate.getTime()&&column==8){//只有结束时间才有该判断，时间不能往前调到开始时间之前
            this.messageBox("结束时间不能小于开始时间！");
            return false;
        }
//        if(column==8&&selectedIndx!=(tableParm.getCount()-1)){
//            Date afterDate = format.parse(tableParm.getValue("START_DATE",
//                    selectedIndx +1));
//            if (curDate.getTime() > afterDate.getTime()) {
//                this.messageBox("结束时间不能大于后一时程的开始时间！");
//                return false;
//            }
//
//        }
        //若下一时程的开始时间与本时程的开始时间相同，则结束时间必须小于开始时间+1天
        if(column==8&&selectedIndx!=(tableParm.getCount()-1)&&tableParm.getValue("SUSTAINED_DAYS",(selectedIndx+1)).equals(tableParm.getValue("SUSTAINED_DAYS",(selectedIndx)))){
            if(endDate.getTime()>(startDate.getTime()+1000*60*60*24)){
                this.messageBox("当前时程的开始时间与下一时程的开始时间相同，开始时间和结束时间差需小于1天!");
                return false;
            }
        }
        return true;
    }
    /**
     * 修正表格数据
     */
//    private boolean modifyTableData(TTableNode node) throws ParseException {
//
////        //处理数据-治疗开始天数验证
////        if (column == 5) {
////            if (!validStardDay(node)) {
////                return false;
////            }
////            modifyTableStartDayAndStartData(node, tableParm, inDate);
////        }
////        //处理数据-治疗天数
////        if (column == 6) {
////            if (!validSchdDay(node)) {
////                return false;
////            }
////            //更新治疗天数
////            modifyTableEndDate(node, tableParm, inDate, format);
////        }
////        if(column==7||column==8){
////            //更新时间
//            modifyTableDataWithStartAndEndDate(node);
////        }
//        return true;
//    }

    /**
     * 得到入院日期
     * @return Calendar
     */
    private Calendar getInDate(){
        SimpleDateFormat inDateformat = new SimpleDateFormat("yyyy/MM/dd");
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm tableParm = table.getParmValue();
        Calendar inDate = Calendar.getInstance();
        inDate.set(Calendar.HOUR_OF_DAY, 0);
        inDate.set(Calendar.MINUTE, 0);
        inDate.set(Calendar.SECOND, 0);
        if (tableParm.getCount() > 0) {
            if (tableParm.getRow(0).getValue("IN_DATE").length() > 0) {
                String currentDateStr = tableParm.getRow(0).getValue("IN_DATE").
                                        substring(0, 10);
                try {
                    inDate.setTime(inDateformat.parse(currentDateStr));
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return inDate;
    }
    private void modifyTableData(TTableNode node
        ) throws ParseException {
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm tableParm = table.getParmValue();
        int column = node.getColumn();
        //入院时间
        Calendar inDate = getInDate();
        int selectedRow = node.getRow();
        int selectedColumn=node.getColumn();
        long difDay=0;
        long difDayTime=0;//同一天的时程 ,开始时间 结束时间显示当天记录
        long millisecond=0;
        if(selectedColumn==7||selectedColumn==8){
            Date oldDate=format.parse(node.getOldValue()+"");
            Date newDate=format.parse( node.getValue()+ "");
            difDay = (newDate.getTime() - oldDate.getTime()) / 1000 / 60 /
                          60 / 24;
            //=======pangben 2012-6-12 修改开始时间从00：00：00 结束时间为 23：59：59 天数为当天
            difDayTime=difDay==0?1:difDay;//当天数据天数为0时添加1
            millisecond=(newDate.getTime() - oldDate.getTime())%(1000*60*60*24);
        }else if(selectedColumn==5||selectedColumn==6){
            int newValue=Integer.parseInt(node.getValue()+"");
            int oldValue=Integer.parseInt(node.getOldValue()+"");
            difDay=newValue-oldValue;
            difDayTime=newValue-oldValue;
        }
        for (int i = selectedRow; i < tableParm.getCount(); i++) {
//            TParm rowData = tableParm.getRow(i);
            Calendar startDay=Calendar.getInstance();
            startDay.setTimeInMillis(format.parse(tableParm.getValue("START_DATE",i)).getTime());
            Calendar endDay=Calendar.getInstance();
            endDay.setTimeInMillis(format.parse(tableParm.getValue("END_DATE",i)).getTime());
            //如果是当前行开始时间和结束时间要与修改的为准
            if(selectedRow==i&&selectedColumn==7){
               startDay.setTimeInMillis(format.parse(node.getValue()+"").getTime());
            }
            if(selectedRow==i&&selectedColumn==8){
                endDay.setTimeInMillis(format.parse(node.getValue()+"").getTime());
            }
            //如果是当前行开始时间和结束时间要与修改的为准end
            //如果当前的下一条时程的开始天数与本条记录的开始天数相同则为同一天开始的时程不进行更新操作
            if(selectedRow==i&&selectedRow!=(tableParm.getCount()-1)
               &&(tableParm.getValue("SUSTAINED_DAYS",(i+1)).equals(tableParm.getValue("SUSTAINED_DAYS",(i)))
               &&selectedColumn==8
               &&((endDay.getTimeInMillis()-startDay.getTimeInMillis())>1000*60*60*24)
               )){
                break;
            }
            //开始时间加入改动天数差//
            if(!(selectedRow==i&&selectedColumn==7||selectedRow==i&&selectedColumn==6||selectedRow==i&&selectedColumn==8)){//本行当前值改动不用参与标准差计算
            	startDay.add(Calendar.DAY_OF_YEAR, (int) difDayTime);//======pangben 2012-6-26 
            	//startDay.add(Calendar.DAY_OF_YEAR, (int) difDay+1);//======pangben 2012-6-26 
                //加入零碎的毫秒
                startDay.add(Calendar.MILLISECOND,(int)millisecond);
                tableParm.setData("START_DATE",i,format.format(startDay.getTime()));
            }
            //结束时间加入改动天数差||selectedRow==i&&selectedColumn==5||selectedRow==i&&selectedColumn==7
            if(!(selectedRow==i&&selectedColumn==8)){//本行当前值改动不用参与标准差计算
                endDay.add(Calendar.DAY_OF_YEAR, (int) difDayTime);
                //加入零碎的毫秒
                endDay.add(Calendar.MILLISECOND,(int)millisecond);
                tableParm.setData("END_DATE",i,format.format(endDay.getTime()));
            }
            //开始天数调整
//
            if(!(selectedRow==i&&selectedColumn==5||selectedRow==i&&selectedColumn==6||selectedRow==i&&selectedColumn==8)){//本行当前值改动不用参与标准差计算
                tableParm.setData("SUSTAINED_DAYS", i,
                                  tableParm.getInt("SUSTAINED_DAYS", i) +
                                  difDay);
            }
            //时程天数调整
            //只有当前行才可以进行时程调整
            //selectedRow==i&&selectedColumn==7||
            if(selectedRow==i&&selectedColumn==8){
                tableParm.setData("SCHD_DAY", i,
                                  tableParm.getInt("SCHD_DAY", i) + difDay);
            }
        }
        table.setParmValue(tableParm);
    }

    /**
     * 开始天数验证方法
     * @param node TTableNode
     * @return boolean
     */
    private boolean validStardDay(TTableNode node) {
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm tableParm = table.getParmValue();
        //开始天数验证
        boolean flag = true;
        int currentRow = node.getRow();
        int curStartDay = Integer.parseInt(node.getValue() + "");
        if (currentRow == 0) {
            if (curStartDay <= 0) {
                flag = false;
            }
        } else {
        	//System.out.println("SUSTAINED_DAYS:::"+tableParm.getRow(currentRow - 1).
             //                                  getValue("SUSTAINED_DAYS"));
            int preStartDay = Integer.parseInt(tableParm.getRow(currentRow - 1).
                                               getValue("SUSTAINED_DAYS"));
            int preSchdDay=Integer.parseInt(tableParm.getRow(currentRow - 1).
                                               getValue("SCHD_DAY"));
            //System.out.println("preStartDay:::"+preStartDay);
            //System.out.println("preSchdDay:::"+preSchdDay);
            //System.out.println("curStartDay:::"+curStartDay);
            if (curStartDay < (preStartDay+preSchdDay-1)){
                flag = false;
            }
        }
        if (!flag) {
            this.messageBox("开始天数输入错误");
            return false;
        }
        return true;
    }

    /**
     * 验证执行天数是否正确
     * @param node TTableNode
     * @return boolean
     */
    public boolean validSchdDay(TTableNode node) {
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm tableParm = table.getParmValue();
        //开始验证
        boolean flag = true;
        int curschdDay = Integer.parseInt(node.getValue() + "");
        if (curschdDay <= 0) {
            flag = false;
        }
        if (!flag) {
            this.messageBox("治疗天数输入错误");
            return false;
        }
        return true;
    }

//    //开始天数和开始结束时间处理方法
//    private void modifyTableStartDayAndStartData(TTableNode node,
//                                                 TParm tableParm,
//                                                 Calendar inDate) {
//        int selectedRow = node.getRow();
//        int differPre = Integer.parseInt("" + node.getValue()) -
//                        Integer.parseInt(node.getOldValue() + "");
//        for (int i = selectedRow; i < tableParm.getCount(); i++) {
//            TParm rowData = tableParm.getRow(i);
//            //开始第几天执行
//            int susTainedDays = 0;
//            //执行天数
//            int schdDay = 0;
//            if (i == selectedRow) {
//                susTainedDays = Integer.parseInt(node.getValue() + "");
//            } else {
//                susTainedDays = Integer.parseInt(rowData.getValue(
//                        "SUSTAINED_DAYS")) + differPre;
//            }
//            schdDay = Integer.parseInt(rowData.getValue("SCHD_DAY"));
//            Calendar startDay = (Calendar) inDate.clone();
//            startDay.add(Calendar.DAY_OF_YEAR, susTainedDays);
//            Calendar endDay = (Calendar) startDay.clone();
//            endDay.add(Calendar.DAY_OF_YEAR, schdDay);
//            tableParm.setData("SUSTAINED_DAYS", i, susTainedDays);
//            //修正开始时间和结束时间
//            modifyStartAndEndDate(startDay, endDay);
//            //实际存入的时间需要将新计算的时间取年月日然后加入原有时间的Time
//            tableParm.setData("START_DATE", i,getStartDayStrWithOldTime(startDay,tableParm.getValue("START_DATE",i)));
//            tableParm.setData("END_DATE", i,getEndDayStrWithOldTime(endDay,tableParm.getValue("END_DATE",i)));
//        }
//
//    }
    /**
     * 得到开始时间加入原有的time
     * @param startDay Calendar
     * @param oldStartDay String
     * @return String
     */
    private String getStartDayStrWithOldTime(Calendar startDay,String oldStartDay){
        //加入处理 begin
        //写入的时间在原来有时间的情况下，时间是不能变化的，仅做日期的变动
        //新的时间仅得到年和月日
        String newStartDay = format.format(startDay.getTime());
        newStartDay = newStartDay.substring(0, 11);
        //将原有的时间加入
        newStartDay += oldStartDay.length() >= 19 ?
                oldStartDay.substring(11, 19) : "00:00:00";
        //加入处理end
        return newStartDay;
    }
//    /**
//     * 得到结束时间加入原有的time
//     * @param startDay Calendar
//     * @param oldEndDay String
//     * @return String
//     */
//    private String getEndDayStrWithOldTime(Calendar endDay,String oldEndDay){
//    //加入处理 begin
//    //写入的时间在原来有时间的情况下，时间是不能变化的，仅做日期的变动
//    //新的时间仅得到年和月日
//    String newEndDay = format.format(endDay.getTime());
//    newEndDay = newEndDay.substring(0, 11);
//    //将原有的时间加入
//    newEndDay += oldEndDay.length() >= 19 ?
//            oldEndDay.substring(11, 19) : "23:59:59";
//    //加入处理end
//    return newEndDay;
//}

//    /**
//     * 治疗时间变更方法
//     * @param node TTableNode
//     * @param tableParm TParm
//     * @param inDate Calendar
//     * @param format SimpleDateFormat
//     */
//    private void modifyTableEndDate(TTableNode node,
//                                    TParm tableParm,
//                                    Calendar inDate,
//                                    SimpleDateFormat format
//            ) {
//        int selectedRow = node.getRow();
//        int differPre = Integer.parseInt("" + node.getValue()) -
//                        Integer.parseInt(node.getOldValue() + "");
//        for (int i = selectedRow; i < tableParm.getCount(); i++) {
//            TParm rowData = tableParm.getRow(i);
//            int susTainedDays = 0;
//            int schdDay = 0;
//            //处理变更数据 begin
//            if (i == selectedRow) {
//                susTainedDays = Integer.parseInt(rowData.getValue(
//                        "SUSTAINED_DAYS"));
//                schdDay = Integer.parseInt(node.getValue() + "");
//            } else {
//                susTainedDays = Integer.parseInt(rowData.getValue(
//                        "SUSTAINED_DAYS")) + differPre;
//                schdDay = Integer.parseInt(rowData.getValue(
//                        "SCHD_DAY"));
//            }
//            //处理变更数据 end
//            Calendar startDay = (Calendar) inDate.clone();
//            startDay.add(Calendar.DAY_OF_YEAR, susTainedDays);
//            Calendar endDay = (Calendar) startDay.clone();
//            endDay.add(Calendar.DAY_OF_YEAR, schdDay);
//            tableParm.setData("SUSTAINED_DAYS", i, susTainedDays);
//            //修正开始时间和结束时间
//            modifyStartAndEndDate(startDay, endDay);
////            tableParm.setData("START_DATE", i, format.format(startDay.getTime()));
////            tableParm.setData("END_DATE", i, format.format(endDay.getTime()));
//            //实际存入的时间需要将新计算的时间取年月日然后加入原有时间的Time
//            tableParm.setData("START_DATE", i,
//                              getStartDayStrWithOldTime(startDay,
//                    tableParm.getValue("START_DATE", i)));
//            tableParm.setData("END_DATE", i,
//                              getEndDayStrWithOldTime(endDay,
//                    tableParm.getValue("END_DATE", i)));
//
//        }
//
//    }

//    /**
//     * 修正开始时间和结束时间
//     * @param startDay Calendar
//     * @param endDay Calendar
//     */
//    private void modifyStartAndEndDate(Calendar startDay, Calendar endDay) {
//        //实际开始时间和结束时间如20110101 则他的开始时间为20110101 不能使开始时间+住院第几天 20110629 modify
//        //应为开始时间+住院第几天-1 故需整体调节  20110629 modify
//        startDay.add(Calendar.DAY_OF_YEAR, -1);
//        endDay.add(Calendar.DAY_OF_YEAR, -1);
//        //最后一天的计算公式为 开始时间+执行天数-1 故需要将结束时间-1
//        endDay.add(Calendar.DAY_OF_YEAR, -1);
//        //结束时间一般是23：59：59 处理
//        endDay.add(Calendar.HOUR_OF_DAY, 24);
//        endDay.add(Calendar.SECOND, -1);
//        //结束时间一般是23：59：59 处理end
//        //end
//    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm result = getSelectDuringInfo();
        this.callFunction("UI|TABLE|setParmValue", result);
    }

    /**
     * 根据case_no and clncPathCode 查询duration
     * @return TParm
     */
    private TParm getSelectDuringInfo() {
        TParm selectParm = new TParm();
        selectParm.setData("CASE_NO", case_no);
        selectParm.setData("CLNCPATH_CODE", clncPathCode);
        selectParm.setData("REGION_CODE",
                           this.getBasicOperatorMap().get("REGION_CODE"));
        TParm result = CLPManagemTool.getInstance().selectDurationData(
                selectParm);
        return result;
    }

    /**
     * 保存
     */
    public void onSave() {
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm parm = table.getParmValue();
        if (!validSaveData()) {
            return;
        }
        TParm saveParm = new TParm();
        saveParm.setData("tableParm", parm.getData());
        saveParm.setData("basicMap", this.getBasicOperatorMap());
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPManagemAction",
                "setDuring", saveParm);
        if (result.getErrCode() < 0) {
            this.messageBox("E0001");
            this.setReturnValue("FAILURE");
        } else {
            this.messageBox("P0001");
            this.setReturnValue("SUCCESS");
        }
        this.onQuery();

    }

    /**
     * 验证查询信息
     * @return boolean
     */
    public boolean validSaveData() {
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm parm = table.getParmValue();
        for (int i = 0; i < parm.getCount(); i++) {
            TParm rowParm = parm.getRow(i); //4567
            String SCHD_DAY = rowParm.getValue("SCHD_DAY");
            String SUSTAINED_DAYS = rowParm.getValue("SUSTAINED_DAYS");
            if (!this.validNumber(SUSTAINED_DAYS)) {
                this.messageBox("请输入合法住院天数");
                return false;
            }
            //卢海 2011-07-20 modify,加入验证begin
            int sustainedDays = rowParm.getInt("SUSTAINED_DAYS");
            if (sustainedDays <= 0) {
                this.messageBox("开始天数输入错误");
                return false;
            }
            //卢海 2011-07-20 modify 加入验证end
            if (!this.validNumber(SCHD_DAY)) {
                this.messageBox("请输入合法开始天数");
                return false;
            }
            //卢海 2011-07-20 修改，加入验证 begin
            int schdDay=rowParm.getInt("SCHD_DAY");
            if(schdDay<=0){
                this.messageBox("治疗天数输入错误");
                return false;
            }
            //卢海 2011-07-20 修改,加入验证  end
        }
        return true;
    }

    /**
     * 展开时程
     */
    public void openDuraction() {
        TParm parm = getSelectDuringInfo();
        TParm saveParm = new TParm();
        saveParm.setData("tableParm", parm.getData());
        saveParm.setData("basicMap", this.getBasicOperatorMap());
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPManagemAction",
                "openDuraction", saveParm);
        if (result.getErrCode() < 0) {
            this.messageBox("展开失败");
        } else {
            this.messageBox("展开成功");
        }
    }

    /**
     * 展开实际
     */
    public void openPracticeDuration() {
        TParm inputParm = new TParm();
        TParm patientTParm = this.getPatientParm();
        inputParm.setData("patientTParm", patientTParm.getData());
        inputParm.setData("deployDate", this.getDeployDate());
        inputParm.setData("operator", getBasicOperatorMap());
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPManagedAction",
                "deployPractice", inputParm);
        if (result.getErrCode() < 0) {
            this.messageBox("展开失败");
        } else {
            this.messageBox("展开成功");
        }
    }

    /**
     * 得到要展开的病人信息
     * @return TParm
     */
    private TParm getPatientParm() {
        TParm patientTParm = new TParm();
        patientTParm.addData("REGION_CODE",
                             this.getBasicOperatorMap().get("REGION_CODE"));
        patientTParm.addData("CASE_NO", this.case_no);
        patientTParm.addData("CLNCPATH_CODE", this.clncPathCode);
      //======pangben 2012-5-28 start
        patientTParm.addData("VERSION", version);
        patientTParm.addData("DEPT_CODE", dept_code);
      //======pangben 2012-5-28 stop
        patientTParm.setCount(1);
        return patientTParm;
    }

    /**
     * 得到展开时间
     * @return String
     */
    private String getDeployDate() {
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        String datestrtmp = this.getValueString("deployDate");
        if (!this.checkNullAndEmpty(datestrtmp)) {
            datestrtmp = datestr;
        } else {
            datestrtmp = datestrtmp.substring(0, 10).replace("-", "");
        }
        return datestrtmp;
    }

    /**
     * 设置控件是否可用方法
     * @param flag boolean
     */
    private void setPrimaryKeyEnabled(boolean flag) {
        TTextField tTextField = (TTextField)this.getComponent("TYPE_CODE");
        tTextField.setEnabled(flag);
    }

    /**
     * 从页面得到查询条件方法
     */
    private TParm getSelectedCondition() {
        TParm selectedCondition = new TParm();
        putParamLikeWithObjName("TYPE_CODE", selectedCondition);
        putParamLikeWithObjName("PY2", selectedCondition);
        putParamLikeWithObjName("TYPE_CHN_DESC", selectedCondition);
        putParamLikeWithObjName("TYPE_ENG_DESC", selectedCondition);
        putParamWithObjNameForQuery("CLASS_FLG", selectedCondition);
        putParamLikeWithObjName("DESCRIPTION", selectedCondition);
        putParamLikeWithObjName("SEQ", selectedCondition);
        selectedCondition.setData("REGION_CODE", Operator.getRegion());
        return selectedCondition;
    }

    /**
     * 将表格的对应单元格设置成可写，其他的设置成不可写
     * @param tableName String
     * @param rowNum int
     * @param columnNum int
     */
    private void setTableEnabled(String tableName, int rowNum, int columnNum) {
        TTable table = (TTable)this.getComponent(tableName);
        int totalColumnMaxLength = table.getColumnCount();
        int totalRowMaxLength = table.getRowCount();
        //锁列
        String lockColumnStr = "";
        for (int i = 0; i < totalColumnMaxLength; i++) {
            if (!(i + "").equals(columnNum + "")) {
                lockColumnStr += i + ",";
            }
        }
        lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
        table.setLockColumns(lockColumnStr);
        //锁行
        String lockRowStr = "";
        for (int i = 0; i < totalRowMaxLength; i++) {
            if (!(i + "").equals(rowNum + "")) {
                lockRowStr += i + ",";
            }
        }
        lockRowStr = lockRowStr.substring(0,
                                          ((lockRowStr.length() - 1) < 0 ? 0 :
                                           (lockRowStr.length() - 1)));
        if (lockRowStr.length() > 0) {
            table.setLockRows(lockRowStr);
        }

    }

    /**
     * 将控件值放入TParam方法(可以传入放置参数值)
     * @param objName String
     */
    private void putParamWithObjName(String objName, TParm parm,
                                     String paramName) {
        String objstr = this.getValueString(objName);
        objstr = objstr;
        parm.setData(paramName, objstr);
    }

    /**
     * 将控件值放入TParam方法(放置参数值与控件名相同)
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        objstr = objstr;
        //参数值与控件名相同
        parm.setData(objName, objstr);
    }

    /**
     * 将控件值放入TParam方法(可以传入放置参数值)
     * @param objName String
     */
    private void putParamLikeWithObjName(String objName, TParm parm,
                                         String paramName) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr + "%";
            parm.setData(paramName, objstr);
        }

    }

    /**
     * 将控件值放入TParam方法(放置参数值与控件名相同)
     * @param objName String
     * @param parm TParm
     */
    private void putParamLikeWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr.trim() + "%";
            //参数值与控件名相同
            parm.setData(objName, objstr);
        }
    }

    /**
     * 用于放置用于完全匹配进行查询的控件
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjNameForQuery(String objName, TParm parm) {
        putParamWithObjNameForQuery(objName, parm, objName);
    }

    /**
     * 用于放置用于完全匹配进行查询的控件
     * @param objName String
     * @param parm TParm
     * @param paramName String
     */
    private void putParamWithObjNameForQuery(String objName, TParm parm,
                                             String paramName) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            //参数值与控件名相同
            parm.setData(paramName, objstr.trim());
        }
    }

    /**
     * 检查控件是否为空
     * @param componentName String
     * @return boolean
     */
    private boolean checkComponentNullOrEmpty(String componentName) {
        if (componentName == null || "".equals(componentName)) {
            return false;
        }
        String valueStr = this.getValueString(componentName);
        if (valueStr == null || "".equals(valueStr)) {
            return false;
        }
        return true;
    }

    /**
     * 得到指定table的选中行
     * @param tableName String
     * @return int
     */
    private int getSelectedRow(String tableName) {
        int selectedIndex = -1;
        if (tableName == null || tableName.length() <= 0) {
            return -1;
        }
        Object componentObj = this.getComponent(tableName);
        if (!(componentObj instanceof TTable)) {
            return -1;
        }
        TTable table = (TTable) componentObj;
        selectedIndex = table.getSelectedRow();
        return selectedIndex;
    }

    /**
     * 数字验证方法
     * @param validData String
     * @return boolean
     */
    private boolean validNumber(String validData) {
        Pattern p = Pattern.compile("[0-9]{1,}");
        Matcher match = p.matcher(validData);
        if (!match.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 向TParm中加入系统默认信息
     * @param parm TParm
     */
    private void putBasicSysInfoIntoParm(TParm parm) {
        int total = parm.getCount();
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM",  Operator.getIP());
    }

    /**
     * 根据Operator得到map
     * @return Map
     */
    private Map getBasicOperatorMap() {
        Map map = new HashMap();
        map.put("REGION_CODE", Operator.getRegion());
        map.put("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        map.put("OPT_DATE", datestr);
        map.put("OPT_TERM", Operator.getIP());
        return map;
    }

    /**
     * 得到当前时间字符串方法
     * @param dataFormatStr String
     * @return String
     */
    private String getCurrentDateStr(String dataFormatStr) {
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, dataFormatStr);
        return datestr;
    }

    /**
     * 得到当前时间字符串方法
     * @return String
     */
    private String getCurrentDateStr() {
        return getCurrentDateStr("yyyyMMdd");
    }

    /**
     * 检查是否为空或空串
     * @return boolean
     */
    private boolean checkNullAndEmpty(String checkstr) {
        if (checkstr == null) {
            return false;
        }
        if ("".equals(checkstr)) {
            return false;
        }
        return true;
    }

    /**
     * 拷贝TParm
     * @param from TParm
     * @param to TParm
     * @param row int
     */
    private void cloneTParm(TParm from, TParm to, int row) {
        for (int i = 0; i < from.getNames().length; i++) {
            to.addData(from.getNames()[i],
                       from.getValue(from.getNames()[i], row));
        }
    }

    /**
     * 克隆对象
     * @param parm TParm
     * @return TParm
     */
    private TParm cloneTParm(TParm from) {
        TParm returnTParm = new TParm();
        for (int i = 0; i < from.getNames().length; i++) {
            returnTParm.setData(from.getNames()[i],
                                from.getValue(from.getNames()[i]));
        }
        return returnTParm;
    }

    /**
     * 处理TParm 里的null的方法
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNullVector(TParm parm, String keyStr, Class type) {
        for (int i = 0; i < parm.getCount(); i++) {
            if (parm.getData(keyStr, i) == null) {
                TNull tnull = new TNull(type);
                parm.setData(keyStr, i, tnull);
            }
        }
    }

    /**
     * 处理TParm 里null值方法
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNull(TParm parm, String keyStr, Class type) {
        if (parm.getData(keyStr) == null) {
            TNull tnull = new TNull(type);
            parm.setData(keyStr, tnull);
        }
    }

}
