package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import java.util.regex.Pattern;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.util.regex.Matcher;
import java.sql.Timestamp;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import jdo.clp.CLPDurationTool;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.util.TMessage;
import com.dongyang.data.TNull;
import com.dongyang.ui.TCheckBox;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title:临床路径时程 </p>
 *
 * <p>Description:临床路径时程 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPDurationControl extends TControl {
    //记录选择行数
    int selectRow = -1;

    public CLPDurationControl() {

    }

    /**
     * 页面初始化方法
     */
    public void onInit() {
        super.onInit();
        initPage();
    }

    /**
     * 页面初始化方法
     */
    private void initPage() {
//        System.out.println("页面初始化开始");
        //绑定表格事件
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //初始化临床路径时程树形菜单
        initTree();
        //初始化数据
        onQuery();
    }

    /**
     * 初始化树形菜单
     */
    private void initTree() {
        TTree tree = (TTree) callMessage("UI|TTree|getThis");
        //初始化Tree的基本信息
        //得到Tree的基础数据
        TParm selectTParm = new TParm();
        this.putBasicSysInfoIntoParm(selectTParm);
        TParm result = CLPDurationTool.getInstance().selectData(selectTParm);
//        System.out.println("查询出的树的数据:" + result);
        TTreeNode root = (TTreeNode) callMessage("UI|TTree|getRoot");
        root.setText("临床路径时程");
        root.setType("Root");
        root.setID("");
        root.removeAllChildren();
        //开始初始化树的数据
        int count = result.getCount();
        for (int i = 0; i < count; i++) {
            TParm dataParm = result.getRow(i);
            putNodeInTree(dataParm,root);
        }
        tree.update();
    }

    private void putNodeInTree(TParm dataParm,TTreeNode root){
        String noteType = "Path"; //UI
        TTreeNode treeNode = new TTreeNode("KPILEAVE", noteType);
        treeNode.setText(dataParm.getValue("DURATION_CHN_DESC"));
        treeNode.setID(dataParm.getValue("DURATION_CODE"));
    //           treeNode.setName("name");
    //           treeNode.setValue("valuesss");
    String parentID = dataParm.getValue("PARENT_CODE");
//    System.out.println("parentID-------------:"+parentID);
    if (root.findNodeForID(dataParm.getValue("DURATION_CODE"))!=null){
        System.out.println("已经含有此节点不用执行添加操作");
    }else if (root.findNodeForID(parentID) != null) {
        root.findNodeForID(parentID).add(treeNode);
    } else {
        //root.add(treeNode);
        TParm parentTparmselect=new TParm();
        parentTparmselect.setData("DURATION_CODE",parentID);
        this.putBasicSysInfoIntoParm(parentTparmselect);
        TParm resultParm=CLPDurationTool.getInstance().selectData(parentTparmselect);
//        System.out.println("查询出的父节点:"+resultParm);
        if(resultParm.getCount()<=0){
            root.add(treeNode);
        }else{
            putNodeInTree(resultParm.getRow(0),root);
            root.findNodeForID(parentID).add(treeNode);
        }

    }
    }
    /**
     * 数据查询方法
     */
    public void onQuery() {
        TParm parmselect = new TParm();
        this.putBasicSysInfoIntoParm(parmselect);
        TParm parmresult = CLPDurationTool.getInstance().selectData(parmselect);
        this.callFunction("UI|TABLE|setParmValue", parmresult);
        initTree();
        setPrimaryKeyEnabled(true);
        TTextFormat parentText = (TTextFormat)this.getComponent("PARENT_CODE");
//        System.out.println("----更新下拉框值----");
        TTextFormat textFormatParentIds=(TTextFormat) this.getComponent("PARENT_CODE");
        textFormatParentIds.onQuery();

    }

    /**
     * 清除方法
     */
    public void onClear() {
        this.setValue("DURATION_CODE", "");
        this.setValue("PY2", "");
        this.setValue("LEAF_FLG", "0");
        this.setValue("DURATION_CHN_DESC", "");
        this.setValue("DURATION_ENG_DESC", "");
        this.setValue("PARENT_CODE", "");
        this.setValue("PY1", "");
        this.setValue("SEQ", "");
        this.setValue("DESCRIPTION","");
        initHasLeafVisiable();
        onQuery();
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
            TTable table = (TTable)this.getComponent("TABLE");
            int selRow = table.getSelectedRow();
            TParm tableParm = table.getParmValue();
            String durationCode = tableParm.getValue("DURATION_CODE", selRow);
            TParm parm = new TParm();
            parm.setData("DURATION_CODE", durationCode);
//            System.out.println("删除人ID"+durationCode);
            TParm result = TIOM_AppServer.executeAction(
                    "action.clp.CLPDuringAction",
                    "deleteCLPDuring", parm);
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
            //由于有删除父类别的子类型的情况，故必须刷新页面
            this.onQuery();
            this.messageBox("P0003");

        } else {
            return;
        }
    }

    /**
     * 表格点击方法
     */
    public void onTableClicked(int row) {
        if (row < 0) {
            return;
        }
        TParm data = (TParm) callFunction("UI|Table|getParmValue");
        setValueForParm(
                "DURATION_CODE;DURATION_CHN_DESC;PY1;PY2;DURATION_ENG_DESC;LEAF_FLG;PARENT_CODE;SEQ;DESCRIPTION",
                data, row);
        String str = data.getValue("SEQ", row);
        this.setValue("SEQ", str);
        setPrimaryKeyEnabled(false);
        initHasLeafVisiable();
        selectRow = row;
    }

    /**
     * 初始化是否可以让叶子节点多选框可选
     */
    private void initHasLeafVisiable(){
        TCheckBox checkbox=(TCheckBox) this.getComponent("LEAF_FLG");
        checkbox.setEnabled(true);
        String leafId= this.getValueString("DURATION_CODE");
        if(checkNodeIsHasLeaf(leafId)){
           checkbox.setEnabled(false);
        }
    }
    /**
     * 检查该节点是否含有叶节点
     * @return boolean
     */
    private boolean checkNodeIsHasLeaf(String nodeId){
        boolean flag = true;
        TParm checkParm = new TParm();
        checkParm.setData("PARENT_CODE",nodeId);
        this.putBasicSysInfoIntoParm(checkParm);
        checkParm=CLPDurationTool.getInstance().selectData(checkParm);
        if(checkParm.getCount()<=0){
            flag=false;
        }
        return flag;
    }
    private boolean checkParentIdIsValid(String durationCode,String parentId){
        boolean flag = true;
        TParm checkParm = new TParm();
        checkParm.setData("DURATION_CODE",durationCode);
        checkParm.setData("PARENT_CODE",parentId);
        TParm checkResult=CLPDurationTool.getInstance().checkParentId(checkParm);
        String totalCountstr= checkResult.getValue("TOTALCOUNT",0);
        int totalCount = Integer.parseInt(totalCountstr);
        if(totalCount>0){
            flag=false;
            return flag;
        }
        TParm parentTparmselect = new TParm();
        parentTparmselect.setData("PARENT_CODE", durationCode);
        this.putBasicSysInfoIntoParm(parentTparmselect);
        TParm resultParm = CLPDurationTool.getInstance().selectData(
                parentTparmselect);
//        System.out.println("子类型："+resultParm);
        for(int i=0;i<resultParm.getCount();i++){
            String durationCodevalid= resultParm.getValue("DURATION_CODE",i);
            if(!checkParentIdIsValid(durationCodevalid,parentId)){
                return false;
            }
        }
        return true;
    }
    /**
     * 保存方法
     */
    public void onSave() {
//        System.out.println("保存方法执行");
        if (!validData()) {
            return;
        }
        if(this.getValueString("DURATION_CODE").equals(this.getValueString("PARENT_CODE"))){
            this.messageBox("父节点代码不能和时程代码相同");
            return;
        }
        boolean checkParentIdIsValid= this.checkParentIdIsValid(this.getValueString("DURATION_CODE"),this.getValueString("PARENT_CODE"));
        if(!checkParentIdIsValid){
            this.messageBox("该临床路径时程的父类型不能是该临床路径的子类型");
            return;
        }
        TParm param = new TParm();
        putParamWithObjName("DURATION_CODE", param);
        putParamWithObjName("REGION_CODE", param);
        putParamWithObjName("DURATION_CHN_DESC", param);
        putParamWithObjName("DURATION_ENG_DESC", param);
        putParamWithObjName("PY1", param);
        putParamWithObjName("PY2", param);
        putParamWithObjName("SEQ", param);
        putParamWithObjName("PARENT_CODE", param);
        putParamWithObjName("LEAF_FLG",param);
        putParamWithObjName("DESCRIPTION",param);
        //处理拼音不能自动生成问题
        //设置拼音1
        setValue("PY1",TMessage.getPy(getValueString("DURATION_CHN_DESC")));
        putParamWithObjName("PY1", param);
        //处理seq
        if("".equals(param.getValue("SEQ"))){
            TNull tnull = new TNull(String.class);
//            System.out.println("放入----------------");
            param.setData("SEQ",tnull);
        }
//        //判断顺序号是否为空，如果为空需要进行处理，给入数据库SEQ最大值加1
//        if (this.getValueString("SEQ") == null ||
//            this.getValueString("SEQ").length() <= 0) {
//            TParm maxSEQResult = CLPDurationTool.getInstance().getMaxSEQ();
//            int SEQNumber = Integer.parseInt(maxSEQResult.getData("MAXSEQ",
//                    0) +
//                                             "") + 1;
//            param.setData("SEQ", SEQNumber);
//        }
        TParm returnParam = CLPDurationTool.getInstance().checkDataExist(param);
        int dataCount = returnParam.getInt("DATACOUNT", 0);
        if (dataCount > 0) {
            TParm resultParam = CLPDurationTool.getInstance().updateData(param);
            boolean flag = false;
            if (resultParam.getErrCode() >= 0) {
                flag = true;
            }
            String msg = flag ? "P0001" : "E0001";
            this.messageBox("提示", msg, -1);
        } else {
            //添加时对数据进行处理
            this.putBasicSysInfoIntoParm(param);
            TParm resultParam = CLPDurationTool.getInstance().insertData(param);
            boolean flag = false;
            if (resultParam.getErrCode() >= 0) {
                flag = true;
            }
            String msg = flag ? "P0002" : "E0002";
            this.messageBox("提示", msg, -1);
        }

        //刷新数据
        onQuery();
    }

    private boolean validData() {
        //验证可以采用封装的方法
        if (!this.emptyTextCheck(
                "DURATION_CODE,DURATION_CHN_DESC,OPT_DATE,OPT_TERM,PRG_ID")) {
            return false;
        }
        String seqstr = this.getValueString("SEQ");
        if (this.checkNullAndEmpty(seqstr)) {
            if (!this.validNumber(seqstr)) {
                this.messageBox("顺序号请输入数字");
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
        TTextField tTextField = (TTextField)this.getComponent("DURATION_CODE");
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
//        System.out.println("列总数：" + totalColumnMaxLength + "行总数:" +
//                           totalRowMaxLength);
        //锁列
        String lockColumnStr = "";
        for (int i = 0; i < totalColumnMaxLength; i++) {
            if (!(i + "").equals(columnNum + "")) {
                lockColumnStr += i + ",";
            }
        }
        lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
//        System.out.println("锁列串：" + lockColumnStr);
        table.setLockColumns(lockColumnStr);
        //锁行
        String lockRowStr = "";
        for (int i = 0; i < totalRowMaxLength; i++) {
            if (!(i + "").equals(rowNum + "")) {
                lockRowStr += i + ",";
            }
        }
//        System.out.println("锁行串前：" + lockRowStr + "总行" + totalRowMaxLength);
        lockRowStr = lockRowStr.substring(0,
                                          ((lockRowStr.length() - 1) < 0 ? 0 :
                                           (lockRowStr.length() - 1)));
//        System.out.println("锁行串：" + lockRowStr);
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
//        System.out.println(objstr);
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
//        System.out.println("total" + total);
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM", Operator.getIP());
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


}
