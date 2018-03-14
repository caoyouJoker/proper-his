package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import jdo.clp.CLPEVLStandmTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTable;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import java.sql.Timestamp;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.dongyang.ui.TButton;
import jdo.clp.CLPEVLCat3Tool;
import java.util.Vector;
import java.util.Iterator;
import jdo.clp.CLPEVLStanddTool;

/**
 * <p>Title: 临床路径评估项目维护</p>
 *
 * <p>Description:临床路径评估项目维护 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author luhai
 * @version 1.0
 */
public class CLPEVLStandmControl extends TControl {
    //记录选中行数
    private int selectRow = -1;
    private int selectTypeLeft = -1;
    private int selectTypeRight = -1;
    public CLPEVLStandmControl() {
    }

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        initPage();
    }

    /**
     * 页面初始化
     */
    public void initPage() {
        callFunction("UI|CLP_EVL_STANDM_TABLE|addEventListener",
                     "CLP_EVL_STANDM_TABLE->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        callFunction("UI|CLP_EVL_STANDD_TABLE_LEFT|addEventListener",
                     "CLP_EVL_STANDD_TABLE_LEFT->" + TTableEvent.CLICKED, this,
                     "onTableLeftClicked");
        callFunction("UI|CLP_EVL_STANDD_TABLE_RIGHT|addEventListener",
                     "CLP_EVL_STANDD_TABLE_RIGHT->" + TTableEvent.CLICKED, this,
                     "onTableRightClicked");
        //绑定评估细分类按钮方法
        TButton TYPE_BTN = (TButton)this.getComponent("TYPE_BTN");

        callFunction("UI|CLP_EVL_STANDM_TABLE|addEventListener",
                     "CLP_EVL_STANDM_TABLE->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        onQuery();
    }

    /**
     *增加对Table的监听
     * @param row int
     */
    public void onTableClicked(int row) {
        if (row < 0) {
            return;
        }
        TParm data = (TParm) callFunction(
                "UI|CLP_EVL_STANDM_TABLE|getParmValue");
        setValueForParm(
                "CLNCPATH_CODE;EVL_CODE;EVL_CHN_DESC;DESCRIPTION;EVL_ENG_DESC;SEQ;PY1",
                data, row);
        String str = data.getValue("SEQ", row);
        this.setValue("SEQ", str);
        setPrimaryKeyEnabled(false);
        //查询对应的类型信息
        TParm typeListTParm = CLPEVLStanddTool.getInstance().selectData(data.
                getRow(row));
        TParm rightmoveParm = new TParm();
        int totalrowcount = typeListTParm.getCount();
        for (int i = 0; i < totalrowcount; i++) {
            rightmoveParm.setData("CAT3_CODE", i,
                                  typeListTParm.getData("CAT3_CODE", i));
            rightmoveParm.setData("CAT3_CHN_DESC", i,
                                  typeListTParm.getData("CAT3_CHN_DESC", i));
            rightmoveParm.setData("SCORE", i, typeListTParm.getData("SCORE", i));
            rightmoveParm.setData("CAT1_CODE", i,
                                  typeListTParm.getData("CAT1_CODE", i));
            rightmoveParm.setData("CAT2_CODE", i,
                                  typeListTParm.getData("CAT2_CODE", i));
        }

        //SYSTEM.OUT.PRINTln("向右侧赋的parm:" + typeListTParm);
        this.callFunction("UI|CLP_EVL_STANDD_TABLE_RIGHT|setParmValue",
                          rightmoveParm);
        //初始化类型基本信息
        typeListInfoInit();
        selectRow = row;
    }

    /**
     *增加对Table的监听
     * @param row int
     */
    public void onTableLeftClicked(int row) {
        if (row < 0) {
            return;
        }
//        TParm data = (TParm) callFunction(
//                "UI|CLP_EVL_STANDD_TABLE_LEFT|getParmValue");
        selectTypeLeft = row;
    }

    /**
     *增加对Table的监听
     * @param row int
     */
    public void onTableRightClicked(int row) {
        if (row < 0) {
            return;
        }
//        TParm data = (TParm) callFunction(
//                "UI|CLP_EVL_STANDD_TABLE_RIGHT|getParmValue");
        selectTypeRight = row;
    }

    /**
     * 初始化评估细分类方法
     */
    public void showTypeList() {
        //SYSTEM.OUT.PRINTln("加载评估细分类");
        TParm parm = new TParm();
        this.putBasicSysInfoIntoParm(parm);
        TParm result = CLPEVLCat3Tool.getInstance().selectData(parm);
        this.callFunction("UI|CLP_EVL_STANDD_TABLE_LEFT|setParmValue", result);
    }

    /**
     * 页面查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        putParamWithObjNameForQuery("CLNCPATH_CODE", parm);
        putParamLikeWithObjName("EVL_CODE", parm);
        putParamLikeWithObjName("EVL_CHN_DESC", parm);
//        putParamLikeWithObjName("EVL_ENG_DESC", parm);
//        putParamLikeWithObjName("DESCRIPTION", parm);
//        putParamLikeWithObjName("PY1", parm);
//        putParamWithObjNameForQuery("SEQ", parm);
        this.putBasicSysInfoIntoParm(parm);
        TParm result = CLPEVLStandmTool.getInstance().selectData(parm);
        this.callFunction("UI|CLP_EVL_STANDM_TABLE|setParmValue", result);
        setPrimaryKeyEnabled(true);
    }

    /**
     * 清空方法
     */
    public void onClear() {
        this.setValue("CLNCPATH_CODE", "");
        this.setValue("EVL_CODE", "");
        this.setValue("EVL_CHN_DESC", "");
        this.setValue("EVL_ENG_DESC", "");
        this.setValue("DESCRIPTION", "");
        this.setValue("SEQ", "");
        this.setValue("PY1", "");
        this.callFunction("UI|CLP_EVL_STANDD_TABLE_RIGHT|setParmValue",
                          new TParm());
        setPrimaryKeyEnabled(true);
        //初始化类型信息
        typeListInfoInit();
        onQuery();
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        selectRow = this.getSelectedRow("CLP_EVL_STANDM_TABLE");
        if (selectRow == -1) {
            this.messageBox("请选择需要删除的数据");
            return;
        }
        if (this.messageBox("询问", "是否删除", 2) == 0) {
            TTable table = (TTable)this.getComponent("CLP_EVL_STANDM_TABLE");
            int selRow = table.getSelectedRow();
            TParm tableParm = table.getParmValue();
            String CLNCPATH_CODE = tableParm.getValue("CLNCPATH_CODE", selRow);
            String EVL_CODE = tableParm.getValue("EVL_CODE", selRow);
            TParm parm = new TParm();
            parm.setData("CLNCPATH_CODE", CLNCPATH_CODE);
            parm.setData("EVL_CODE", EVL_CODE);
            //SYSTEM.OUT.PRINTln("delete operation ----------------");

            TParm result = TIOM_AppServer.executeAction(
                    "action.clp.CLPEVLStandmAction",
                    "deleteCLPEVLStandm", parm);
            // TParm result = CLPEVLStandmTool.getInstance().deleteData(parm);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //删除单行显示
            int row = (Integer) callFunction(
                    "UI|CLP_EVL_STANDM_TABLE|getSelectedRow");
            if (row < 0) {
                return;
            }
            this.callFunction("UI|CLP_EVL_STANDM_TABLE|removeRow", row);
            this.callFunction("UI|CLP_EVL_STANDM_TABLE|setSelectRow", row);
            this.messageBox("P0003");
        } else {
            return;
        }
    }

    /**
     * 保存方法
     */
    public void onSave() {
        //SYSTEM.OUT.PRINTln("保存方法执行");
        if (!validData()) {
            return;
        }
        TParm param = getBasicDataTparm();
        TParm rightTableParm = getTypeListTableData(param);
        TParm allParm = new TParm();
        allParm.setData("basicInfoParm", param.getData());
        allParm.setData("typeList", rightTableParm.getData());
        TParm returnParam = CLPEVLStandmTool.getInstance().checkDataExist(param);
        int dataCount = returnParam.getInt("DATACOUNT", 0);
        //SYSTEM.OUT.PRINTln("--------查询数据数：" + dataCount + "------------");
        if (dataCount > 0) {
            //SYSTEM.OUT.PRINTln("data exist sys will update");
            //SYSTEM.OUT.PRINTln("执行更新数据方法开始调用");
            TParm result = TIOM_AppServer.executeAction(
                    "action.clp.CLPEVLStandmAction",
                    "updateCLPEVLStandm", allParm);
            boolean flag = false;
            if (result.getErrCode() >= 0) {
                flag = true;
            }
            String msg = flag ? "编辑成功" : "编辑失败";
            this.messageBox("提示", msg, -1);
        } else {
            //SYSTEM.OUT.PRINTln("data not exist sys will insert data ");
            //添加时对数据进行处理
            param.setData("REGION_CODE", Operator.getRegion());
            param.setData("OPT_USER", Operator.getID());
            Timestamp today = SystemTool.getInstance().getDate();
            String datestr = StringTool.getString(today, "yyyyMMdd");
            param.setData("OPT_DATE", datestr);
            param.setData("OPT_TERM", Operator.getIP());
            TParm result = TIOM_AppServer.executeAction(
                    "action.clp.CLPEVLStandmAction",
                    "insertCLPEVLStandm", allParm);
//            TParm resultParam = CLPCHKItemTool.getInstance().insertData(param);
            TParm resultParam = TIOM_AppServer.executeAction(
                    "action.clp.CLPEVLStandmAction",
                    "insertCLPEVLStandm", allParm);
            boolean flag = false;
            if (result.getErrCode() >= 0) {
                flag = true;
            }
            String msg = flag ? "添加成功" : "添加失败";
            this.messageBox("提示", msg, -1);
        }
        //刷新数据
        onQuery();
    }

    /**
     * 得到类型集合TParm
     * @param basicDataTParm TParm
     * @return TParm
     */
    private TParm getTypeListTableData(TParm basicDataTParm) {
        TTable rightTable = (TTable)this.getComponent(
                "CLP_EVL_STANDD_TABLE_RIGHT");
        TParm rightTableParm = rightTable.getParmValue();
        if (rightTableParm == null) {
            rightTableParm = new TParm();
        }
        //处理右侧表格数据
        int rowcount = rightTableParm.getCount();
        for (int i = 0; i < rowcount; i++) {
            //添加时对数据进行处理
            rightTableParm.setData("OPT_USER", i, Operator.getID());
            rightTableParm.setData("OPT_TERM", i, Operator.getIP());
            Timestamp today = SystemTool.getInstance().getDate();
            String currentTime = StringTool.getString(today, "yyyyMMdd");
            rightTableParm.setData("OPT_DATE", i, currentTime);
            //加入基础数据内容
            rightTableParm.setData("CLNCPATH_CODE", i,
                                   basicDataTParm.getData("CLNCPATH_CODE"));
            rightTableParm.setData("EVL_CODE", i,
                                   basicDataTParm.getData("EVL_CODE"));
        }
        //SYSTEM.OUT.PRINTln("处理后的表格数据-----------:" + rightTableParm);
        //处理右侧表格数据结束
        return rightTableParm;
    }

    /**
     * 得到基础数据信息
     * @return TParm
     */
    private TParm getBasicDataTparm() {
        TParm param = new TParm();
        putParamWithObjName("CLNCPATH_CODE", param);
        putParamWithObjName("EVL_CODE", param);
        putParamWithObjName("EVL_CHN_DESC", param);
        putParamWithObjName("EVL_ENG_DESC", param);
        putParamWithObjName("DESCRIPTION", param);
        putParamWithObjName("PY1", param);
        putParamWithObjName("SEQ", param);
        //判断顺序号是否为空，如果为空需要进行处理，给入数据库SEQ最大值加1
        //SYSTEM.OUT.PRINTln("form SEQ " + this.getValueString("SEQ"));
        if (this.getValueString("SEQ") == null ||
            this.getValueString("SEQ").length() <= 0) {
            TParm maxSEQResult = CLPEVLStandmTool.getInstance().getMaxSEQ();
            int SEQNumber = Integer.parseInt(maxSEQResult.getData("MAXSEQ",
                    0) +
                                             "") + 1;
            param.setData("SEQ", SEQNumber);
        }
        return param;

    }

    /**
     * 评估细分类左移
     */
    public void TypeLeftMove() {
        selectTypeRight = getSelectedRow("CLP_EVL_STANDD_TABLE_RIGHT");
        if (selectTypeRight < 0) {
            this.messageBox("提示", "请选择移动内容", -1);
            return;
        }
        TParm rightParm = (TParm) callFunction(
                "UI|CLP_EVL_STANDD_TABLE_RIGHT|getParmValue");
        TTable leftTable = (TTable)this.getComponent(
                "CLP_EVL_STANDD_TABLE_LEFT");
        TParm paramLeft = leftTable.getParmValue();
        if (paramLeft == null) {
            paramLeft = new TParm();
        }
        boolean flag = false;
        Vector leftNameVector = paramLeft.getVector("CAT3_CODE");
        Iterator itr = leftNameVector.iterator();
        while (itr.hasNext()) {
            Vector rowVector = (Vector) itr.next();
            boolean flag2 = rowVector.contains(rightParm.getValue("CAT3_CODE",
                    selectTypeRight));
            if (flag2)
                flag = true;
        }
        //SYSTEM.OUT.PRINTln("结果:" + flag);
        //左侧列表不做更新修改begin
//        if (!flag) {
//            paramLeft.addRowData(rightParm, selectTypeRight);
//            leftTable.setParmValue(paramLeft);
//        }
        //左侧列表不做更新修改end
        //右侧table删除该列
        TTable tTableRight = (TTable)this.getComponent(
                "CLP_EVL_STANDD_TABLE_RIGHT");
        //tTableRight.removeRow(selectTypeRight);
        TParm parm = tTableRight.getParmValue();
        selectTypeRight = getSelectedRow("CLP_EVL_STANDD_TABLE_RIGHT");
        //SYSTEM.OUT.PRINTln("右侧parm" + parm + "行号：" + selectTypeRight);
        parm.removeRow(selectTypeRight);
        //SYSTEM.OUT.PRINTln("---------------测试  1");
        tTableRight.setParmValue(parm);
        //SYSTEM.OUT.PRINTln("右侧删除后的Tparam:" + tTableRight.getParmValue());
        //选择框置-1
        clearTypeTableSelectedRow();
        //初始化类型信息
        typeListInfoInit();
    }

    /**
     * 清除表格显示选中行信息
     */
    private void clearTypeTableSelectedRow() {
        selectTypeLeft = -1;
        selectTypeRight = -1;
        TTable rightTableview = (TTable)this.getComponent(
                "CLP_EVL_STANDD_TABLE_RIGHT");
//        rightTableview.setSelectedRow(-1);
        TTable leftTableview = (TTable)this.getComponent(
                "CLP_EVL_STANDD_TABLE_LEFT");
//        leftTableview.setSelectedRow(-1);
        //清除选中方法
        leftTableview.clearSelection();
        rightTableview.clearSelection();

    }

    /**
     * 评估细分类右移
     */
    public void TypeRightMove() {
        selectTypeLeft = getSelectedRow("CLP_EVL_STANDD_TABLE_LEFT");
        if (selectTypeLeft < 0) {
            this.messageBox("提示", "请选择移动内容", -1);
            return;
        }
        TParm leftParm = (TParm) callFunction(
                "UI|CLP_EVL_STANDD_TABLE_LEFT|getParmValue");
        TTable rightTable = (TTable)this.getComponent(
                "CLP_EVL_STANDD_TABLE_RIGHT");
        TParm paramRight = rightTable.getParmValue();
        if (paramRight == null) {
            paramRight = new TParm();
        }
        boolean flag = false;
        Vector rightNameVector = paramRight.getVector("CAT3_CODE");
        Iterator itr = rightNameVector.iterator();
        while (itr.hasNext()) {
            Vector rowVector = (Vector) itr.next();
            boolean flag2 = rowVector.contains(leftParm.getValue("CAT3_CODE",
                    selectTypeLeft));
            if (flag2)
                flag = true;
        }
        //SYSTEM.OUT.PRINTln("结果:" + flag);
        if (!flag) {
            TParm moveData = new TParm();
            moveData.setData("CAT3_CODE", 0,
                             leftParm.getValue("CAT3_CODE", selectTypeLeft));
            moveData.setData("CAT3_CHN_DESC", 0,
                             leftParm.getValue("CAT3_CHN_DESC", selectTypeLeft));
            moveData.setData("SCORE", 0,
                             leftParm.getValue("SCORE", selectTypeLeft));
            moveData.setData("CAT1_CODE", 0,
                             leftParm.getValue("CAT1_CODE", selectTypeLeft));
            moveData.setData("CAT2_CODE", 0,
                             leftParm.getValue("CAT2_CODE", selectTypeLeft));
            paramRight.addRowData(moveData, 0);
            rightTable.setParmValue(paramRight);
//            //左侧table删除该列
//            TTable tTableLeft = (TTable)this.getComponent(
//                    "CLP_EVL_STANDD_TABLE_LEFT");
//            TParm parm = tTableLeft.getParmValue();
//            parm.removeRow(selectTypeLeft);
//            tTableLeft.setParmValue(parm);
//           // tTableLeft.removeRow(selectTypeLeft);
//            //SYSTEM.OUT.PRINTln("左侧删除后的Tparam:" + tTableLeft.getParmValue());
        }
        clearTypeTableSelectedRow();
        //初始化类型信息
        typeListInfoInit();
    }

    /**
     * 表格基本信息初始化
     */
    public void typeListInfoInit() {

        TTable table = (TTable)this.getComponent("CLP_EVL_STANDD_TABLE_RIGHT");
        //SYSTEM.OUT.PRINTln("表格基本信息初始化");
        int rowcount = table.getParmValue().getCount();
        rowcount = rowcount < 0 ? 0 : rowcount;
        this.setValue("TOTAL_INPUT", rowcount + "");
        //计算总分值
        double total = 0;
        TParm rightParm = table.getParmValue() == null ? new TParm() :
                          table.getParmValue();
        Vector scoreVector = rightParm.getVector("SCORE");
        Iterator itr = scoreVector.iterator();
        while (itr.hasNext()) {
            Vector scoreVectortmp = (Vector) itr.next();
            //SYSTEM.OUT.PRINTln("doubless" +
                         //      Double.parseDouble(scoreVectortmp.get(0) + ""));
            total += Double.parseDouble(scoreVectortmp.get(0) + "");
        }
        this.setValue("TOTAL_SCORE_INPUT", total + "");
    }

    /**
     * 参数验证方法
     * @return boolean
     */
    private boolean validData() {
        if (!checkComponentNullOrEmpty("CLNCPATH_CODE")) {
            this.messageBox("请选择适用临床路径!");
            return false;
        }
        if (!this.emptyTextCheck("EVL_CODE,EVL_CHN_DESC")) {
            return false;
        }
        String seq = this.getValueString("SEQ");
        if (seq != null && seq.length() > 0) {
            Pattern p = Pattern.compile("[0-9]{1,}");
            Matcher match = p.matcher(seq);
            if (!match.matches()) {
                this.messageBox("顺序号请输入数字!");
                return false;
            }
        }
        return true;
    }

    /**
     * 设置主键是否启用
     * @param flag boolean
     */
    private void setPrimaryKeyEnabled(boolean flag) {
        TTextFormat tTextFormat = (TTextFormat)this.getComponent(
                "CLNCPATH_CODE");
        tTextFormat.setEnabled(flag);
        TTextField EVL_CODE_TTextField = (TTextField)this.getComponent(
                "EVL_CODE");
        EVL_CODE_TTextField.setEnabled(flag);
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
            objstr = "%" + objstr + "%";
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
            parm.setData(paramName, objstr);
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
        //SYSTEM.OUT.PRINTln("total" + total);
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM",  Operator.getIP());
    }


}


