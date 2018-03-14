package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.data.TParm;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import java.util.Map;
import com.dongyang.data.TNull;
import java.util.HashMap;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.util.regex.Matcher;
import java.sql.Timestamp;
import jdo.sys.PatTool;
import jdo.clp.CLPEvaluateTool;

/**
 * <p>Title: 临床路径评估执行</p>
 *
 * <p>Description: 临床路径评估执行</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author luhai
 * @version 1.0
 */
public class CLPEvaluateSelectPatientControl extends TControl {
    public CLPEvaluateSelectPatientControl() {

    }

    /**
     * 页面初始化方法
     */
    public void onInit() {
        super.onInit();
        initPage();
    }
    /**
     * 初始化页面
     */
    private void initPage(){
        //绑定控件事件
        callFunction("UI|MR_NO|addEventListener",
             "MR_NO->" + TKeyListener.KEY_RELEASED, this,
             "onKeyReleased");

    }
    //控件点击事件
    public void onKeyReleased(KeyEvent e){
        if (e.getKeyCode() != 10) {
            return;
        }
        //查找病人信息
        this.selectPatientBasicInfo();
        //查询病人的评估执行信息
        this.onQuery();
    }

    /**
     * 查询病人基本信息
     */
    private void selectPatientBasicInfo(){
        String mr_no = this.getValueString("MR_NO");
        //自动补齐mr_no
        mr_no = PatTool.getInstance().checkMrno(mr_no);
        TParm selectParm = new TParm();
        selectParm.setData("MR_NO",mr_no);
        selectParm.setData("REGION_CODE",this.getBasicOperatorMap().get("REGION_CODE"));
        TParm patientTParm=CLPEvaluateTool.getInstance().selectPatientInfo(selectParm);
        if(patientTParm.getCount()>0){
            this.setValue("MR_NO",patientTParm.getValue("MR_NO",0));
            this.setValue("CLNCPATH_CODE",patientTParm.getValue("CLNCPATH_CODE",0));
            this.setValue("DEPT_CODE",patientTParm.getValue("DEPT_CODE",0));
            this.setValue("STATION_CODE",patientTParm.getValue("STATION_CODE",0));
            this.setValue("BED_NO",patientTParm.getValue("BED_NO",0));
            this.setValue("VS_DR_CODE",patientTParm.getValue("VS_DR_CODE",0));
            this.setValue("PAT_NAME",patientTParm.getValue("PAT_NAME",0));
        }
    }

    public void onClear(){
        this.clearText("MR_NO;CLNCPATH_CODE;DEPT_CODE;STATION_CODE;BED_NO;VS_DR_CODE;PAT_NAME");
        //清空后查询
        this.onQuery();
    }
    /**
     * 查询
     */
    public void onQuery(){
        TParm selectParm = getSelectPatientParm();
        TParm result=CLPEvaluateTool.getInstance().selectManagemWithPatientInfo(selectParm);
        //查询出来的评估执行信息
        this.callFunction("UI|TABLE|setParmValue", result);
    }

    /**
     * 评估
     */
    public void onEvaluation(){
        TParm sendParm = new TParm();
        //得到case_no
        int selectedRow= this.getSelectedRow("TABLE");
        if(selectedRow<0){
            this.messageBox("请选择评估执行内容");
            return ;
        }
        TTable table = (TTable) this.getComponent("TABLE");
        String case_no= table.getParmValue().getRow(selectedRow).getValue("CASE_NO");
        sendParm.setData("CASE_NO",case_no);
        String mr_no= table.getParmValue().getRow(selectedRow).getValue("MR_NO");
        sendParm.setData("MR_NO",mr_no);
        String pat_name= table.getParmValue().getRow(selectedRow).getValue("PAT_NAME");
        sendParm.setData("PAT_NAME",pat_name);
        String sex_desc= table.getParmValue().getRow(selectedRow).getValue("SEX_DESC");
        sendParm.setData("SEX_DESC",sex_desc);
        String bed_no= table.getParmValue().getRow(selectedRow).getValue("BED_NO");
        sendParm.setData("BED_NO",bed_no);
        String VSDRCode= table.getParmValue().getRow(selectedRow).getValue("VS_DR_CODE");
        sendParm.setData("VS_DR_CODE",VSDRCode);
        String clncPathCode= table.getParmValue().getRow(selectedRow).getValue("CLNCPATH_CODE");
        sendParm.setData("CLNCPATH_CODE",clncPathCode);
        String inDate= table.getParmValue().getRow(selectedRow).getValue("IN_DATE");
        sendParm.setData("IN_DATE",inDate);
        String dsDate= table.getParmValue().getRow(selectedRow).getValue("DS_DATE");
        sendParm.setData("DS_DATE",dsDate);
        String resultstr = (String)this.openDialog(
                "%ROOT%\\config\\clp\\CLPEvaluation.x", sendParm);
    }
    /**
     * 查询病人基本信息
     * @return TParm
     */
    private TParm getSelectPatientParm(){
        TParm selectParm = new TParm();
        this.putParamWithObjNameForQuery("MR_NO",selectParm);
        this.putParamWithObjNameForQuery("DEPT_CODE",selectParm);
        this.putParamWithObjNameForQuery("STATION_CODE",selectParm);
        this.putParamLikeWithObjName("BED_NO",selectParm);
        this.putParamWithObjNameForQuery("CLNCPATH_CODE",selectParm);
        this.putParamWithObjNameForQuery("VS_DR_CODE",selectParm);
        return selectParm;
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
        String objstr = this.getValueString(objName).trim();
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
     * 数字验证方法
     * @param validData String
     * @return boolean
     */
    private boolean validDouble(String validData) {
        Pattern p = Pattern.compile("[0-9]{1,2}([.][0-9]{1,2}){0,1}");
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
