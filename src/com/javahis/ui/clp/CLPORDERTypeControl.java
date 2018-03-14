package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import jdo.sys.SYSRuleTool;
import java.util.regex.Pattern;
import jdo.clp.CLPORDERTypeTool;
import com.dongyang.data.TNull;
import com.dongyang.ui.TTable;
import java.util.Map;
import com.dongyang.jdo.TDataStore;
import java.util.HashMap;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import com.dongyang.ui.event.TTreeEvent;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import com.dongyang.ui.TTree;
import com.dongyang.util.StringTool;
import java.util.regex.Matcher;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import java.text.DecimalFormat;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;

/**
 * <p>Title: 临床路径医嘱对照</p>
 *
 * <p>Description: 临床路径医嘱对照</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author luhai
 * @version 1.0
 */
public class CLPORDERTypeControl extends TControl{
    public CLPORDERTypeControl() {

    }
    /**
     * 树根
     */
    private TTreeNode treeRoot;
    //树
    TTree tree;
    /**
     * 树的数据放入datastore用于对树的数据管理
     */
    private TDataStore treeDataStore = new TDataStore();
    /**
     * 编号规则类别工具
     */
    private SYSRuleTool ruleTool;
    //页面类型
    private String systemSign;
    //收费分类
    private final String feeType="FEE";
    //药品分类
    private final String medType="MED";
    //检验检查分类
    private final String exmType="EXM";
    //医嘱套餐分类
    private final String packType="PACK";
    /**
     * 页面初始化方法
     */
    public void onInit() {
        super.onInit();
        initPage();
        //初始化树
        onInitTree();
        //给tree添加监听事件
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        //初始化结点
        onInitNode();
        //监听表格值变动
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE, this,
                         "onChangeTableValue");

    }
    /**
     * 监听表格值变动方法
     * @param obj Object
     * @return boolean
     */
    public boolean onChangeTableValue(Object obj) {
        //拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return true;
        //如果改变的节点数据和原来的数据相同就不改任何数据
        if (node.getValue().equals(node.getOldValue()))
            return true;
        //判断频次是否符合要求
        int selectedColumn = node.getTable().getSelectedColumn();
        int selectedRow = node.getTable().getSelectedRow();
        String nodeValue = (String) node.getValue();
        boolean freqFlag = false;
        if (selectedColumn == 5) {
            if(!validDouble(nodeValue)&&this.checkNullAndEmpty(nodeValue)){
                this.messageBox("临床路径数量输入错误");
                freqFlag=true;
            }
            //计算转换率
            setRate(selectedRow,Double.parseDouble(nodeValue));
        }
        return freqFlag;
    }
    private void setRate(int row,double clpQTY){
        TTable table= (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm tableParm = table.getParmValue();
        TParm rowParm = tableParm.getRow(row);
        double medQTY=rowParm.getDouble("MEDI_QTY");
        tableParm.setData("CLP_RATE",row,getRate(medQTY,clpQTY));
        table.setParmValue(tableParm);
    }
    /**
     * 得到转换率
     * @param mediQty double
     * @param clpQTY double
     * @return double
     */
    private double getRate(double mediQty,double clpQTY){
        double medQTY=mediQty;
        double rate=clpQTY/medQTY;
        DecimalFormat formater = new DecimalFormat("0.##");
        try{
            rate = Double.parseDouble(formater.format(rate));
        }catch(Exception e){
            rate=0.0;
        }
        return rate;
    }
    /**
     * 初始化树
     */
    public void onInitTree() {
        //得到树根
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        if (treeRoot == null)
            return;
        //给根节点添加文字显示
        String treeTitle="";
        if(systemSign.equalsIgnoreCase(this.exmType)){
            treeTitle="检验检查分类";
        }else if(systemSign.equalsIgnoreCase(this.packType)){
            treeTitle="医嘱套餐分类";
        }else if(systemSign.equalsIgnoreCase(this.medType)){
            treeTitle="药品分类";
        }else if(systemSign.equalsIgnoreCase(this.feeType)){
            treeTitle="收费分类";
        }
        treeRoot.setText(treeTitle);
        //给根节点赋tag
        treeRoot.setType("Root");
        //设置根节点的id
        treeRoot.setID("");
        //清空所有节点的内容
        treeRoot.removeAllChildren();
        //调用树点初始化方法
        callMessage("UI|TREE|update");
    }
    /**
     * 初始化树的结点
     */

    public void onInitNode() {
        //检验检查
        if (this.systemSign.equalsIgnoreCase(this.exmType)) {
            //给dataStore赋值
            treeDataStore.setSQL(
                    "SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='EXM_RULE'");
            //如果从dataStore中拿到的数据小于0
            if (treeDataStore.retrieve() <= 0)
                return;
            //过滤数据,是编码规则中的科室数据
            ruleTool = new SYSRuleTool("EXM_RULE");
        }
        //医嘱套餐
        if (this.systemSign.equalsIgnoreCase(this.packType)) {
            //给dataStore赋值
            treeDataStore.setSQL(
                    "SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='ORDPACK_RULE'");
            //如果从dataStore中拿到的数据小于0
            if (treeDataStore.retrieve() <= 0)
                return;
            //过滤数据,是编码规则中的科室数据
            ruleTool = new SYSRuleTool("ORDPACK_RULE");
        }
        //收费分类
        if (this.systemSign.equalsIgnoreCase(this.feeType)) {
            //给dataStore赋值
            treeDataStore.setSQL(
                    "SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='SYS_FEE_RULE'");
            //如果从dataStore中拿到的数据小于0
            if (treeDataStore.retrieve() <= 0)
                return;
            //过滤数据,是编码规则中的科室数据
            ruleTool = new SYSRuleTool("SYS_FEE_RULE");
        }
        //药品分类
        if (this.systemSign.equalsIgnoreCase(this.medType)) {
            //给dataStore赋值
            treeDataStore.setSQL(
                    "SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='PHA_RULE'");
            //如果从dataStore中拿到的数据小于0
            if (treeDataStore.retrieve() <= 0)
                return;
            //过滤数据,是编码规则中的科室数据
            ruleTool = new SYSRuleTool("PHA_RULE");
        }
        if (ruleTool.isLoad()) { //给树篡节点参数:datastore，节点代码,节点显示文字,,节点排序
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                "CATEGORY_CODE",
                "CATEGORY_CHN_DESC", "Path", "SEQ");
            //循环给树安插节点
            for (int i = 0; i < node.length; i++)
                treeRoot.addSeq(node[i]);
        }
        //得到界面上的树对象
         this.tree = (TTree) callMessage("UI|TREE|getThis");
        //更新树
        tree.update();
        //设置树的默认选中节点
        tree.setSelectNode(treeRoot);

    }

    /**
     * 单击树
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
        this.onQuery();

    }
    /**
     * 查询
     */
    public void onQuery(){
        TParm result = CLPORDERTypeTool.getInstance().
                       selectOrderCodeFromSysFeeData(getSelectCondition());
//        System.out.println("查询出的医嘱" + result);
        addDefaultValueToTable(result);
        this.callFunction("UI|TABLE|setParmValue", result);
    }
    /**
     * 加入默认值到数据库
     */
    public void addDefaultValueToTable(TParm result){
        for(int i=0;i<result.getCount();i++){
            TParm row=result.getRow(i);
            String unit=row.getValue("CLP_UNIT");
            if(!this.checkNullAndEmpty(unit)){
                result.setData("CLP_UNIT",i,row.getValue("MEDI_UNIT"));
            }
            String qty=row.getValue("CLP_QTY");
            if(!this.checkNullAndEmpty(qty)||"0.0".equals(qty)){
                result.setData("CLP_QTY",i,row.getValue("MEDI_QTY"));
                double rate= getRate(row.getDouble("MEDI_QTY"),result.getDouble("CLP_QTY",i));
                result.setData("CLP_RATE",i,rate);
            }
            if (!systemSign.equalsIgnoreCase(this.medType)) {
            	result.setData("MEDI_UNIT",i,row.getValue("UNIT_CODE"));
            	result.setData("MEDI_QTY",i,1);
			}
        }
    }

    /**
     * 清空方法
     */
    public void onClear(){
        this.clearValue("ORDER_CODE;ORDER_DESC");
    }
    /**
     * 保存方法
     */
    public void onSave(){
        TTable table =(TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm parm = table.getParmValue();
        TParm savaParm=new TParm();
        int rownum=0;
        if(!this.validSaveData()){
            return;
        }
        if (parm.getCount() <= 0) {
            return;
        }
        for(int i=0;i<parm.getCount();i++){
            TParm rowParm= parm.getRow(i);
//            if(!"".equals(rowParm.getValue("ORDER_CODE"))&&!"".equals(rowParm.getValue("TYPE_CODE"))){
                savaParm.addData("ORDER_CODE",rowParm.getValue("ORDER_CODE"));
                savaParm.addData("TYPE_CODE",null==rowParm.getValue("TYPE_CODE")?"":rowParm.getValue("TYPE_CODE"));
                savaParm.addData("ORDER_FLG","Y");
                savaParm.addData("CLP_UNIT",rowParm.getValue("CLP_UNIT"));
                savaParm.addData("CLP_RATE","".equals(rowParm.getValue("CLP_RATE"))?"0":rowParm.getValue("CLP_RATE"));
                savaParm.addData("CLP_QTY","".equals(rowParm.getValue("CLP_QTY"))?"0":rowParm.getValue("CLP_QTY"));
                Map basicInfo=getBasicOperatorMap();
                savaParm.addData("OPT_USER",basicInfo.get("OPT_USER"));
                savaParm.addData("OPT_DATE",basicInfo.get("OPT_DATE"));
                savaParm.addData("OPT_TERM",basicInfo.get("OPT_TERM"));
//            }
        }
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPORDERTypeAction", "saveORDERType", savaParm);
        if (result.getErrCode() <= 0) {
            this.messageBox("P0001");
            this.onQuery();
        } else {
            this.messageBox("E0001");
        }

    }
    /**
     * 数据验证
     * @return boolean
     */
    private boolean validSaveData(){
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        TParm parm = table.getParmValue();
        for (int i = 0; i < parm.getCount(); i++) {
            TParm rowParm = parm.getRow(i);
            if (!"".equals(rowParm.getValue("ORDER_CODE")) &&
                !"".equals(rowParm.getValue("TYPE_CODE"))) {
                String rate= rowParm.getValue("CLP_RATE");
                if(!"".equals(rate)&&!this.validDouble(rate)){
                    this.messageBox("转换率请输入合法数值!");
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * 根据实际情况判断表格的选择列是否可以编辑|
     *
     */
    public void setTableEnabledWithValidColumn() {
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        int column=table.getSelectedColumn();
        int row=table.getSelectedRow();
        if(column==2||column==5||column==6){
            setTableEnabled("TABLE", table.getSelectedRow(),
                        table.getSelectedColumn());
        }
    }

    /**
     * 得到查询的TParm
     * @return TParm
     */
    private TParm getSelectCondition(){
        TTreeNode node = tree.getSelectNode();
        TParm selectTParm = new TParm();
        //判断点击的是否是树的根结点
        if (!node.getType().equals("Root")&&node!=null) {
            //拿到当前选中的节点的id值
            String id = node.getID();
            selectTParm.setData("ORDER_CODE", id + "%");
        }
        this.putParamLikeWithObjName("ORDER_CODE",selectTParm,"ORDER_CODE_CONDITION");
        this.putParamLikeWithObjName("ORDER_DESC",selectTParm);
        this.putBasicSysInfoIntoParm(selectTParm);
        return selectTParm;
    }
    /**
     * 初始化页面
     */
    private void initPage() {
        //得到页面传入参数
        Object obj = this.getParameter();
        if(obj!=null){
            systemSign=this.getParameter().toString();
        }
        //初始化医嘱选择控件ORDER_CODE
        TTextField orderDesc=(TTextField)this.getComponent("ORDER_DESC");
        orderDesc.setPopupMenuParameter("UD",getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"));
        //定义医嘱接受返回值方法
        orderDesc.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popOrderReturn");
    }
    /**
     * 医嘱接受返回值方法
     * @param tag String
     * @param obj Object
     */
    public void popOrderReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String orderCode = parm.getValue("ORDER_CODE");
        String orderDesc = parm.getValue("ORDER_DESC");
        this.setValue("ORDER_CODE", orderCode);
        this.setValue("ORDER_DESC", orderDesc);
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
        //System.out.println("列总数：" + totalColumnMaxLength + "行总数:" +
          //                 totalRowMaxLength);
        //锁列
        String lockColumnStr = "";
        for (int i = 0; i < totalColumnMaxLength; i++) {
            if (!(i + "").equals(columnNum + "")) {
                lockColumnStr += i + ",";
            }
        }
        lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
        //System.out.println("锁列串：" + lockColumnStr);
        table.setLockColumns(lockColumnStr);
        //锁行
        String lockRowStr = "";
        for (int i = 0; i < totalRowMaxLength; i++) {
            if (!(i + "").equals(rowNum + "")) {
                lockRowStr += i + ",";
            }
        }
        //System.out.println("锁行串前：" + lockRowStr + "总行" + totalRowMaxLength);
        lockRowStr = lockRowStr.substring(0,
                                          ((lockRowStr.length() - 1) < 0 ? 0 :
                                           (lockRowStr.length() - 1)));
        //System.out.println("锁行串：" + lockRowStr);
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
        //System.out.println(objstr);
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
        //System.out.println("total" + total);
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
                //System.out.println("处理为空情况");
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
            //System.out.println("处理为空情况");
            TNull tnull = new TNull(type);
            parm.setData(keyStr, tnull);
        }
    }




}
