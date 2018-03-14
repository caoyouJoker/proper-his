package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
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
import com.dongyang.ui.TCheckBox;
import jdo.clp.CLPNursOrderTool;
import java.util.List;
import java.util.ArrayList;
import com.dongyang.util.TMessage;

/**
 * <p>Title: 护嘱字典</p>
 *
 * <p>Description:护嘱字典 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPNursOrderControl extends TControl {
    //记录选择行数
    int selectRow = -1;
    public CLPNursOrderControl() {
    }

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        initPage();
        //SYSTEM.OUT.PRINTln("护嘱类别页面加载");
        this.onQuery();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
    }

    /**
     * 初始化页面
     */
    private void initPage() {

    }

    /**
     * 页面查询方法
     */
    public void onQuery() {
        setPrimaryKeyEnabled(true);
        if(!validQuery()){
            return;
        }
        TParm selectParm = getSelectedCondition();
        TParm result = CLPNursOrderTool.getInstance().selectData(selectParm);
        this.callFunction("UI|Table|setParmValue", result);
    }

    /**
     * 清空方法
     */
    public void onClear() {
        List<String> list = new ArrayList();
        list.add("ORDER_CODE");
        list.add("PY2");
        list.add("ORDER_CHN_DESC");
        list.add("PY1");
        list.add("ORDER_ENG_DESC");
        list.add("UNIT");
        list.add("FREQ");
        list.add("AMOUNT");
        list.add("TYPE_CODE");
        list.add("CHKTYPE_CODE");
        list.add("SEQ");
        list.add("DESCRIPTION");
        clearInput(list);
        this.callFunction("UI|DEL_FLG|setSelected",false);
        setPrimaryKeyEnabled(true);
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        selectRow = this.getSelectedRow("Table");
        if (selectRow == -1) {
            this.messageBox("请选择需要删除的数据");
            return;
        }
        if (this.messageBox("询问", "是否删除", 2) == 0) {
            TTable table = (TTable)this.getComponent("Table");
            int selRow = table.getSelectedRow();
            TParm tableParm = table.getParmValue();
            String order_code = tableParm.getValue("ORDER_CODE", selRow);
            TParm parm = new TParm();
            parm.setData("ORDER_CODE", order_code);
            TParm result = CLPNursOrderTool.getInstance().deleteData(parm);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //删除单行显示
            int row = (Integer) callFunction("UI|Table|getSelectedRow");
            if (row < 0) {
                return;
            }
            this.callFunction("UI|Table|removeRow", row);
            this.callFunction("UI|Table|setSelectRow", row);
            this.messageBox("P0003");
        } else {
            return;
        }
    }

    /**
     * 保存方法
     */
    public void onSave() {
        //SYSTEM.OUT.PRINTln("保存按钮执行");
        //验证数据
        if (!validBasicData()) {
            return;
        }
        //得到插入数据
        //处理生成拼音问题
        //处理拼音不能自动生成问题
        //设置拼音1
        setValue("PY1", TMessage.getPy(getValueString("ORDER_CHN_DESC")));
        TParm parm = new TParm();
        this.putParamWithObjName("ORDER_CODE", parm);
        this.putParamWithObjName("PY2", parm);
        this.putParamWithObjName("ORDER_CHN_DESC", parm);
        this.putParamWithObjName("PY1", parm);
        this.putParamWithObjName("ORDER_ENG_DESC", parm);
        this.putParamWithObjName("UNIT", parm);
        this.putParamWithObjName("FREQ", parm);
        this.putParamWithObjName("AMOUNT", parm);
        this.putParamWithObjName("TYPE_CODE", parm);
        this.putParamWithObjName("CHKTYPE_CODE", parm);
        this.putParamWithObjName("SEQ", parm);
        this.putParamWithObjName("DESCRIPTION", parm);
        boolean flag=(Boolean)this.callFunction("UI|DEL_FLG|isSelected");
        if(flag){
            parm.setData("DEL_FLG","Y");
        }else{
            parm.setData("DEL_FLG","N");
        }
        this.putBasicSysInfoIntoParm(parm);
        //判断数据是否存在
        TParm result = CLPNursOrderTool.getInstance().checkDataExist(parm);
        boolean isdataExist = Integer.parseInt(result.getValue("DATACOUNT", 0)) >
                              0 ? true : false;
        TParm resultopt = null;
        //处理seq
        if ("".equals(parm.getValue("SEQ"))) {
            TNull tnull = new TNull(String.class);
            parm.setData("SEQ", tnull);
        }
        if (isdataExist) {
            //SYSTEM.OUT.PRINTln("数据存在，更新----------");
            resultopt = CLPNursOrderTool.getInstance().updateData(parm);
            if (resultopt.getErrCode() >= 0) {
                this.messageBox("P0001");
            } else {
                this.messageBox("E0001");
            }
        } else {
            //SYSTEM.OUT.PRINTln("数据不存在，插入--------");
            resultopt = CLPNursOrderTool.getInstance().insertData(parm);
            if (resultopt.getErrCode() >= 0) {
                this.messageBox("P0002");
            } else {
                this.messageBox("E0002");
            }
        }
        this.onQuery();
    }
    /**
     * 验证方法
     * @return boolean
     */
    private boolean validBasicData(){
        if (!this.emptyTextCheck("ORDER_CODE,ORDER_CHN_DESC,AMOUNT")) {
            return false;
        }
        String amount = this.getValueString("AMOUNT");
        if (!this.validDouble(amount)) {
            this.messageBox("请输入合法用量!");
            return false;
        }
        if("".equals(getValue("UNIT"))){
            this.messageBox("请选择单位!");
            return false;
        }
        if ("".equals(getValue("FREQ"))) {
            this.messageBox("请选择频次!");
            return false;
        }
        if ("".equals(getValue("TYPE_CODE"))) {
            this.messageBox("请选择护嘱类别!");
            return false;
        }
        if ("".equals(getValue("CHKTYPE_CODE"))) {
            this.messageBox("请选择查核类别!");
            return false;
        }

        //在顺序号存在时验证数字
        if (this.checkNullAndEmpty(this.getValueString("SEQ")) &&
            !this.validNumber(this.getValueString("SEQ"))) {
            this.messageBox("顺序号请输入数字");
            return false;
        }
        return true;

    }
    /**
     * 查询时验证
     * @return boolean
     */
    private boolean validQuery(){
//        //在顺序号存在时验证数字
//        if (this.checkNullAndEmpty(this.getValueString("SEQ")) &&
//            !this.validNumber(this.getValueString("SEQ"))) {
//            this.messageBox("顺序号请输入数字");
//            return false;
//        }
//        String amount =this.getValueString("AMOUNT");
//        if(this.checkNullAndEmpty(amount)&&!this.validDouble(amount)){
//            this.messageBox("请输入合法用量");
//            return false;
//        }
        return true;
    }
    /**
     * 从页面得到查询条件方法
     */
    private TParm getSelectedCondition() {
        TParm selectedCondition = new TParm();
        putParamLikeWithObjName("ORDER_CODE", selectedCondition);
//        putParamLikeWithObjName("PY2", selectedCondition);
        putParamLikeWithObjName("ORDER_CHN_DESC", selectedCondition);
//        putParamLikeWithObjName("ORDER_ENG_DESC", selectedCondition);
//        putParamWithObjNameForQuery("UNIT", selectedCondition);
//        putParamWithObjNameForQuery("FREQ", selectedCondition);
//        putParamWithObjNameForQuery("AMOUNT", selectedCondition);
//        putParamWithObjNameForQuery("TYPE_CODE", selectedCondition);
//        TCheckBox b = new TCheckBox();
//        boolean delFlg=(Boolean) this.callFunction("UI|DEL_FLG|isSelected");
//        //SYSTEM.OUT.PRINTln("删除标记-----------"+delFlg);
//        if(delFlg){
//            selectedCondition.setData("DEL_FLG","Y");
//        }
//        putParamWithObjNameForQuery("CHKTYPE_CODE", selectedCondition);
//        putParamWithObjNameForQuery("ORDTYPE_CODE", selectedCondition);
//        putParamWithObjNameForQuery("CLP_RATE", selectedCondition);
//        putParamWithObjNameForQuery("CLP_UNIT", selectedCondition);
//        putParamWithObjNameForQuery("DESCRIPTION", selectedCondition);
//        putParamLikeWithObjName("SEQ", selectedCondition);
        selectedCondition.setData("REGION_CODE", Operator.getRegion());
        return selectedCondition;
    }

    /**
     * 设置控件是否可用方法
     * @param flag boolean
     */
    private void setPrimaryKeyEnabled(boolean flag) {
        TTextField tTextField = (TTextField)this.getComponent("ORDER_CODE");
        tTextField.setEnabled(flag);
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
        //SYSTEM.OUT.PRINTln("列总数：" + totalColumnMaxLength + "行总数:" +
         //                  totalRowMaxLength);
        //锁列
        String lockColumnStr = "";
        for (int i = 0; i < totalColumnMaxLength; i++) {
            if (!(i + "").equals(columnNum + "")) {
                lockColumnStr += i + ",";
            }
        }
        lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
        //SYSTEM.OUT.PRINTln("锁列串：" + lockColumnStr);
        table.setLockColumns(lockColumnStr);
        //锁行
        String lockRowStr = "";
        for (int i = 0; i < totalRowMaxLength; i++) {
            if (!(i + "").equals(rowNum + "")) {
                lockRowStr += i + ",";
            }
        }
        //SYSTEM.OUT.PRINTln("锁行串前：" + lockRowStr + "总行" + totalRowMaxLength);
        lockRowStr = lockRowStr.substring(0,
                                          ((lockRowStr.length() - 1) < 0 ? 0 :
                                           (lockRowStr.length() - 1)));
        //SYSTEM.OUT.PRINTln("锁行串：" + lockRowStr);
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
     *增加对Table的监听
     * @param row int
     */
    public void onTableClicked(int row) {
        if (row < 0) {
            return;
        }
        TParm data = (TParm) callFunction("UI|Table|getParmValue");
        setValueForParm(
                "ORDER_CODE;ORDER_CHN_DESC;PY1;PY2;ORDER_ENG_DESC;UNIT;FREQ;AMOUNT;TYPE_CODE;CHKTYPE_CODE;DESCRIPTION;SEQ;DEL_FLG",
                data, row);
        String str = data.getValue("SEQ", row);
        this.setValue("SEQ", str);
        String AMOUNT = data.getValue("AMOUNT", row);
        this.setValue("AMOUNT", AMOUNT);
        setPrimaryKeyEnabled(false);
        selectRow = row;
    }

    /**
     * 将控件值放入TParam方法(放置参数值与控件名相同)
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        //SYSTEM.OUT.PRINTln(objstr);
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
     * 数字验证方法
     * @param validData String
     * @return boolean
     */
    private boolean validDouble(String validData) {
        Pattern p = Pattern.compile("[0-9]{1,}([.][0-9]{1,}){0,1}");
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
        //SYSTEM.OUT.PRINTln("total" + total);
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM", Operator.getIP());
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
                //SYSTEM.OUT.PRINTln("处理为空情况");
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
            //SYSTEM.OUT.PRINTln("处理为空情况");
            TNull tnull = new TNull(type);
            parm.setData(keyStr, tnull);
        }
    }

    /**
     * 清空集合中对应的输入框的值
     * @param inputNames List
     */
    private void clearInput(List<String> inputNames){
        for(String inputstr : inputNames){
            this.setValue(inputstr,"");
        }
    }

}
