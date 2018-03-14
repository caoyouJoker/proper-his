package com.javahis.ui.sys;

import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.javahis.manager.sysfee.sysOdrsetDtlObserver;
import java.awt.Component;
import com.dongyang.jdo.TDSObject;
import com.dongyang.ui.TTreeNode;
import jdo.sys.SYSRuleTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TDS;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TTableNode;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import jdo.sys.SYSHzpyTool;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TTextFormat;
import java.util.Vector;
import com.dongyang.manager.TIOM_Database;
import com.javahis.util.StringUtil;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title: 集合医嘱字典档界面</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS 1.0 (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH
 */
public class SYSFee_OrdPackControl
    extends TControl {

    public SYSFee_OrdPackControl() {
    }

    /**
     * 大对象
     */
    private TDSObject bigObject = new TDSObject();
    /**
     * 树根
     */
    private TTreeNode treeRoot;
    /**
     * 编号规则类别工具
     */
    private SYSRuleTool ruleTool;
    /**
     * 树的数据放入datastore用于对树的数据管理
     */
    private TDataStore treeDataStore = new TDataStore();
    /**
     * 表格当前选中的行号
     */
    int selRow = -1;
    /**
     * 当前选中的项目代码(SYS_FEE_HISTORY中：ORDER_CODE/SYS_ORDERSETDETAI中：ORDERSET_CODE)
     */
    String orderCode = "";
    /**
     * SYS_FEE_HISTORY表的TDS
     */
    TDS sysFeeHisDS = new TDS();
    /**
     * SYS_ORDERSETDETAI表的TDS
     */
    TDS sysOrdSetDtlDS = new TDS();
	/**
	 * SYS_FEE表的TDS
	 */
	TDS sysFeeDS = new TDS();

    /**
     * 所有控件的名字
     */
    private String controlName =
        "USEDEPT_CODE;EXEC_DEPT_CODE;" +
        "ACTIVE_FLG;CHARGE_HOSP_CODE;ORDER_DESC;PY1;" +
        "DESCRIPTION;ORDER_CAT1_CODE;TRANS_OUT_FLG;" +
        "OPD_FIT_FLG;EMG_FIT_FLG;IPD_FIT_FLG;HRM_FIT_FLG;UNIT;IS_REMARK;ORD_SUPERVISION";


    /**
     * 界面的控件
     */
    //树
    TTree tree;
    //主表
    TTable upTable, downTable;

    //全部/启用/停用标记
    TRadioButton ALL;
    TRadioButton ACTIVE_Y;
    TRadioButton ACTIVE_N;


    //门,急,住,健,经医适用标记
    TCheckBox OPD_FIT_FLG;
    TCheckBox EMG_FIT_FLG;
    TCheckBox IPD_FIT_FLG;
    TCheckBox HRM_FIT_FLG;

    TTextFormat USEDEPT_CODE;
    TTextFormat EXEC_DEPT_CODE;
    TTextFormat CHARGE_HOSP_CODE;
    TTextFormat ORDER_CAT1_CODE;
    TTextFormat ORD_SUPERVISION;

    //启用注记
    TCheckBox ACTIVE_FLG;

    TTextField QUERY;
    TTextField ORDER_CODE;
    TTextField ORDER_DESC;
    TTextField PY1;
    TTextField DESCRIPTION;

    //转出注记
    TCheckBox TRANS_OUT_FLG;
    TComboBox TRANS_HOSP_CODE;

    TNumberTextField TOT_FEE;


    //集合医嘱注记(在界面上隐藏)
    TCheckBox ORDERSET_FLG;
    //启用时间(及时生效--当前时间)
    String START_DATE;
    //停用时间(99991231235959)
    String END_DATE;

    //--------------------
    TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");
    //根据order拿到单价
    public double getPrice(String code) {
        if (dataStore == null)
            return 0.0;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER :
            dataStore.PRIMARY;
        TParm parm = dataStore.getBuffer(bufferString);
        Vector vKey = (Vector) parm.getData("ORDER_CODE");
        Vector vPrice = (Vector) parm.getData("OWN_PRICE");
        int count = vKey.size();
        for (int i = 0; i < count; i++) {
            if (code.equals(vKey.get(i)))
                return TypeTool.getDouble(vPrice.get(i));
        }
        return 0.0;
    }

//--------------------



    public void onInit() { //初始化程序
        super.onInit();
        myInitControler();

        //初始化树
        onInitTree();
        //给tree添加监听事件
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        //初始化结点
        onInitNode();
        callFunction("UI|downTable|addEventListener",
                            TTableEvent.CHECK_BOX_CLICKED, this,"onTableCheckBoxClicked");
        canSave();
        //========pangben modify 20110427 start 权限添加
        //初始化院区
        setValue("REGION_CODE", Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110427 stop

    }


    private void canSave(){
        Object obj = this.getParameter();
        if(obj==null)
            return;
        if(obj.equals("QUERY"))
            callFunction("UI|save|setEnabled", false);
    }

       /**
        * 表格(TABLE)复选框改变事件
        *
        * @param obj
        */
       public void onTableCheckBoxClicked(Object obj) {
           TTable table = (TTable)obj;
           //=========pangben 2013-2-19 添加空行不能删除校验
           if(table.getShowParmValue().getValue("ORDER_DESC",table.getSelectedRow()).length()<=0){
        	   this.messageBox("空行不能删除");
        	   table.setItem(table.getSelectedRow(),"N_DEL","N");
        	   table.acceptText();
        	   return;
           }
           table.removeRow(table.getSelectedRow());
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
        treeRoot.setText("医嘱套餐分类");
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
        //给dataStore赋值
        treeDataStore.setSQL(
            "SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='ORDPACK_RULE'");
        //如果从dataStore中拿到的数据小于0
        if (treeDataStore.retrieve() <= 0)
            return;
        //过滤数据,是编码规则中的科室数据
        ruleTool = new SYSRuleTool("ORDPACK_RULE");
        if (ruleTool.isLoad()) { //给树篡节点参数:datastore，节点代码,节点显示文字,,节点排序
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                "CATEGORY_CODE",
                "CATEGORY_CHN_DESC", "Path", "SEQ");
            //循环给树安插节点
            for (int i = 0; i < node.length; i++)
                treeRoot.addSeq(node[i]);
        }
        //得到界面上的树对象
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
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
        //清空
        onClear();

        //新增按钮不能用
        callFunction("UI|new|setEnabled", false);
        //得到点击树的节点对象
        // TTreeNode node = (TTreeNode) parm;
        TTreeNode node = tree.getSelectNode();
        //TTreeNode node=tree.getSelectionNode();
        if (node == null)
            return;
        //得到table对象
        TTable table = (TTable)this.callFunction("UI|upTable|getThis");
        //table接收所有改变值
        table.acceptText();
        //判断点击的是否是树的根结点
        if (node.getType().equals("Root")) {
            //如果是树的根接点table上不显示数据
            table.removeRowAll();
        }
        else { //如果点的不是根结点
            //拿到当前选中的节点的id值
            String id = node.getID();
            //拿到查询TDS的SQL语句（通过上一层的ID去like表中的orderCode）
            //===pangben modify 20110427 添加区域参数
            String sql = getSQL(id,Operator.getRegion());
            //初始化table和TDS
            initTblAndTDS(sql);

        }
        //给table数据加排序条件
//        table.setSort("ORDER_CODE");
        //table排序后重新赋值
//        table.sort();
        //得到当前点击结点的ID
        String nowID = node.getID();

        int classify = 1;
        if (nowID.length() > 0)
            classify = ruleTool.getNumberClass(nowID) + 1;
        //如果是最小节点,可以增加一行(使新增按钮可用)
        if (classify > ruleTool.getClassifyCurrent()) {
            this.callFunction("UI|new|setEnabled", true);
        }
        //上面的TABLE计算出‘总费用’
        updateMastOwnPrice();
    }

    /**
 * 主表载入的时候根据细项现实价格跟新单价列
 */
private void updateMastOwnPrice() {
    String ordersetCode = "";
    int tblCount = upTable.getRowCount();
    TDataStore ds = upTable.getDataStore();
    //循环每一行
    for (int j = 0; j < tblCount; j++) {
        ordersetCode = (String) ds.getItemData(j, "ORDER_CODE");
        //得到该主项的对应细项列别
        String selordCodeForDtl =
            "SELECT ORDER_CODE,DOSAGE_QTY FROM SYS_ORDERSETDETAIL WHERE ORDERSET_CODE = '" +
            ordersetCode + "'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            selordCodeForDtl));
        int count = parm.getCount();
        double totFee = 0.0;
        for (int i = 0; i < count; i++) {
            String code = parm.getValue("ORDER_CODE", i);
            double qty = TypeTool.getDouble(parm.getValue("DOSAGE_QTY", i));
            totFee += getPrice(code) * qty;
        }
        upTable.setValueAt(totFee, j, 3);
    }

}


    /**
     * 得到初始化TDS的SQL语句(查找目前正在启用的项目列表：不卡START_DATE/END_DATE 只卡ACTIVE_FLG为‘Y’)
     * @return String
     */
    private String getSQL(String orderCode,String regionCode) {
        String active = "";
        if (ACTIVE_Y.isSelected()) { //启用
            active = " AND ACTIVE_FLG='Y'";
            setEnabledForCtl(true);
        }
        else if (ACTIVE_N.isSelected()) { //停用
            active = " AND ACTIVE_FLG='N'";
            setEnabledForCtl(false);
        }
        else { //全部
            setEnabledForCtl(false);
        }
        //======pangben modify 20110426 start
        String region = "";
        if (null != regionCode && !"".equals(regionCode))
            region = " AND (REGION_CODE='" + regionCode + "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
        //======pangben modify 20110426 stop

        String sql = "";
        //配过滤条件
        if (orderCode != null && orderCode.length() > 0)
        	sql = " SELECT  * "
				+ " FROM SYS_FEE_HISTORY"
				+ " WHERE "
				+ " ORDER_CODE like '"
				+ orderCode
				+ "%'"
				+ active
				+ region
				+ " ORDER BY ORDER_CODE";

        return sql;
    }

    /**
     * 初始化上面的表格还有所有的控件数据
     * @param sql String
     */
    public void initTblAndTDS(String sql) {
        sysFeeHisDS.setSQL(sql);
        sysFeeHisDS.retrieve();
        //如果没有数据清空表格上的数据
        if (sysFeeHisDS.rowCount() <= 0) {
            upTable.removeRowAll();
        }
        sysFeeHisDS.showDebug();

        upTable.setDataStore(sysFeeHisDS);
        upTable.setDSValue();

    }

    /**
     * 首先得到所有UI的控件对象/注册相应的事件
     * 设置
     */
    public void myInitControler() {

        tree = (TTree) callFunction("UI|TREE|getThis");

        //得到table控件
        upTable = (TTable)this.getComponent("upTable");
        downTable = (TTable)this.getComponent("downTable");

        ALL = (TRadioButton)this.getComponent("ALL");
        ACTIVE_Y = (TRadioButton)this.getComponent("ACTIVE_Y");
        ACTIVE_N = (TRadioButton)this.getComponent("ACTIVE_N");

        ACTIVE_FLG = (TCheckBox)this.getComponent("ACTIVE_FLG");
        QUERY = (TTextField)this.getComponent("QUERY");
        ORDER_CODE = (TTextField)this.getComponent("ORDER_CODE");
        ORDER_DESC = (TTextField)this.getComponent("ORDER_DESC");
        PY1 = (TTextField)this.getComponent("PY1");
        DESCRIPTION = (TTextField)this.getComponent("DESCRIPTION");
        TRANS_OUT_FLG = (TCheckBox)this.getComponent("TRANS_OUT_FLG");
        TRANS_HOSP_CODE = (TComboBox)this.getComponent("TRANS_HOSP_CODE");

        USEDEPT_CODE = (TTextFormat)this.getComponent("USEDEPT_CODE");
        EXEC_DEPT_CODE = (TTextFormat)this.getComponent("EXEC_DEPT_CODE");
        CHARGE_HOSP_CODE = (TTextFormat)this.getComponent("CHARGE_HOSP_CODE");
        ORDER_CAT1_CODE = (TTextFormat)this.getComponent("ORDER_CAT1_CODE");
        ORD_SUPERVISION=(TTextFormat)this.getComponent("ORD_SUPERVISION");
        
        //总费用
        TOT_FEE = (TNumberTextField)this.getComponent("TOT_FEE");

        OPD_FIT_FLG = (TCheckBox)this.getComponent("OPD_FIT_FLG");
        EMG_FIT_FLG = (TCheckBox)this.getComponent("EMG_FIT_FLG");
        IPD_FIT_FLG = (TCheckBox)this.getComponent("IPD_FIT_FLG");
        HRM_FIT_FLG = (TCheckBox)this.getComponent("HRM_FIT_FLG");

        //给上table注册单击事件监听
        this.callFunction("UI|upTable|addEventListener",
                          "upTable->" + TTableEvent.CLICKED, this,
                          "onUpTableClicked");
        //给下table注册监听事件
        downTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                   "onCreateSYSFEE");

        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "");
        // 设置弹出菜单
        QUERY.setPopupMenuParameter("UD", getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // 定义接受返回值方法
        QUERY.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

        //新增按钮不能用
        callFunction("UI|new|setEnabled", false);
    }

    /**
     * 接受返回值方法
     * @param tag String
     * @param obj Object
     */
    public void popReturnFEE(String tag, Object obj) {
        //判断对象是否为空和是否为TParm类型
        if (obj == null && ! (obj instanceof TParm)) {
            return;
        }
        //类型转换成TParm
        TParm result = (TParm) obj;
        String ordCode = result.getValue("ORDER_CODE");
        //根据返回的CODE设置table和TDS的值
        setDownTableAndTDS(ordCode);
    }
    /**
     * 接受返回值方法
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {

        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            ORDER_CODE.setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            ORDER_DESC.setValue(order_desc);
        //清空查询控件
        QUERY.setValue("");
        onQuery();
    }

    public void onQuery() {

        String selCode = ORDER_CODE.getValue();
        if ("".equals(selCode)) {
            this.messageBox("请输入要查询项目的编码！");
        }

        //拿到查询code的SQL语句
        //=====pangben modify 20110427 start
        String sql = getSQL(selCode,this.getValueString("REGION_CODE"));
        //=====pangben modify 20110427 stop
        //初始化table和TDS
        initTblAndTDS(sql);
        //当查询结果只有一条数据的时候，直接显示其详细信息
        if (upTable.getRowCount() == 1) {
            onUpTableClicked(0);
        }

    }


    /**
     * 当TABLE创建编辑控件时
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onCreateSYSFEE(Component com, int row, int column) {

        if (column != 1)
            return;
        if (! (com instanceof TTextField))
            return;
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        //查询检验检查细项--6
        TParm parm = new TParm();
        //parm.setData("RX_TYPE", 6);
        textFilter.setPopupMenuParameter("IG",
                                         getConfigParm().newConfig(
                                             "%ROOT%\\config\\sys\\SYSFeePopup.x"),
                                         parm);
        //定义接受返回值方法
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturnFEE");
    }


    public void setDownTableAndTDS(String code) {

        downTable.acceptText();
        int selrow = downTable.getSelectedRow();
        downTable.setItem(selrow, "ORDER_CODE", code);
        //当开完一个项目之后默认为1.0
        if (TypeTool.getDouble(downTable.getValueAt_(selrow, 5)) == 0.0) {
            downTable.setItem(selrow, "DOSAGE_QTY", 1.0);
        }

        //新增一行
        //sysOrdSetDtlDS.insertRow();
        if(downTable.getSelectedRow()+1==downTable.getRowCount())
            downTable.addRow();
        //刷新总费用控件
        onBrushTotFeeCtl();
    }

    /**
     * 用于初始化总费用和刷新总费用
     */
    public void onBrushTotFeeCtl() {
        //那到table的行数
        double totFee = 0.0;
        int rows = downTable.getRowCount();
        for (int i = 0; i < rows; i++) {
            totFee += TypeTool.getDouble(downTable.getValueAt(i, 6));
        }

        TOT_FEE.setValue(totFee);

    }

    /**
     * 监控第5列，如有值的改变刷新总费用
     * @param node TTableNode
     */
    public void onChangeFee(TTableNode node) {
        if (node.getColumn() == 5) {
            double ownPrice = TypeTool.getDouble(downTable.getValueAt(node.
                getRow(), 4));
            double oldValue = TypeTool.getDouble(node.getOldValue());
            double newValue = TypeTool.getDouble(node.getValue());
            //取得新老值得差
            double diffValue = newValue - oldValue;
            //用该项目单价*数量差额+原来的总费用=现在的总费用
            double totFee = diffValue * ownPrice +
                TypeTool.getDouble(TOT_FEE.getValue());
            TOT_FEE.setValue(totFee);
        }
    }


    /**
     * 单击上面的table事件
     */
    public void onUpTableClicked(int row) {
        //当前选中的行号
        selRow = row;
        //清空下面控件的值
        clearCtl();
        //得到表的parm
        TParm tableDate = ( (TDS) upTable.getDataStore()).getBuffer(TDS.
            PRIMARY).getRow(selRow);
        //给所有的控件值
        //setValueForDownCtl(tableDate, row);
        setValueForDownCtl(tableDate);
        //当是显示‘全部’的时候默认不可编辑(启用状态恢复编辑/停用状态不可编辑)
        if (ALL.isSelected()) {
            boolean activeFlg = ACTIVE_FLG.isSelected();
            setEnabledForCtl(activeFlg);
        }
        //得到当前项目代码(SYS_FEE_HISTORY中：ORDER_CODE/SYS_ORDERSETDETAI中：ORDERSET_CODE)
        orderCode = ORDER_CODE.getValue();
        //初始化下面的table
        initDownTable(orderCode);

        //刷新总费用控件（后于初始化下面的table）
        onBrushTotFeeCtl();
    }

    /**
     * 初始化下面的table
     * @param ordersetCode String
     */
    public void initDownTable(String ordersetCode) {

        downTable.acceptText();
        String sqlForDtl =
            "SELECT * FROM SYS_ORDERSETDETAIL WHERE ORDERSET_CODE = '" +
            ordersetCode + "'";
        sysOrdSetDtlDS.setSQL(sqlForDtl);
        sysOrdSetDtlDS.retrieve();
        //创建监听者
        sysOdrsetDtlObserver obser = new sysOdrsetDtlObserver();
        //把监听者设置到某个DS中
        sysOrdSetDtlDS.addObserver(obser);

        //如果没有数据清空表格上的数据
        if (sysOrdSetDtlDS.rowCount() <= 0) {
            downTable.removeRowAll();
        }

        sysOrdSetDtlDS.insertRow();

        downTable.setDataStore(sysOrdSetDtlDS);
        downTable.setDSValue();
    }

    /**
     * 清空除了table以外的控件的值
     */
    public void clearCtl() {

        this.clearValue(controlName + ";TRANS_HOSP_CODE;ORDER_CODE" +
                        ";TOT_FEE");
        //===========pangben modify 20110427
        this.setValue("REGION_CODE",Operator.getRegion());
    }

    /**
     * 清空操作
     */
    public void onClear() {
        clearCtl();
        //upTable.removeRowAll();
        downTable.removeRowAll();
        setValue("UNIT","");
    }

    /**
     * 新建
     */
    public void onNew() {

        clearCtl();
        cerateNewDate();
        //恢复编辑状态
        setEnabledForCtl(true);
        String nowOrdCode = ORDER_CODE.getValue();
        //初始化下面的table
        initDownTable(nowOrdCode);

    }

    /**
     * 保存
     */
    public boolean onSave() {

//        if (!ACTIVE_FLG.isSelected()) {
//            this.messageBox("请选择启用标记！");
//            return false;
//        }else{
//            onExe();
//        }
        if(onSaveCheck())
            return false;
        onExe();
        //拿到当前选中行的数据（要更改和新建的行）
        selRow = upTable.getSelectedRow();
        //==========pangben 2013-2-19 添加没有选中行不执行操作
        if(selRow<0){
        	this.messageBox("请选择要操作的数据");
        	return false;
        }
        //取table数据
        TDataStore dataStore = upTable.getDataStore();

        dataStore.showDebug();
        setDataInSysFeeTds();
        bigObject.addDS("SYS_FEE", sysFeeDS);
        //从界面上拿到数据，放到TDS中，用于更新保存
        setDataInTDS(dataStore, selRow, false);
        //用大对象保存，形成一个事务
        bigObject.addDS("SYS_FEE_HISTORY", (TDS) dataStore);
        getdownTableDS();
        bigObject.addDS("SYS_ORDERSETDETAIL", sysOrdSetDtlDS);
        if (!bigObject.update()) {
            messageBox("E0001");
            return false;
        }
        messageBox("P0001");
        //前台刷新
        TIOM_Database.logTableAction("SYS_FEE");
        return true;

    }

    public void getdownTableDS() {

        //orderCode = ORDER_CODE.getValue();
        //当前数据库时间
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        int detailCount = sysOrdSetDtlDS.rowCount();
        String orderSetCode = ORDER_CODE.getValue();
        for (int i = 0; i < detailCount; i++) {
            sysOrdSetDtlDS.setItem(i, "ORDERSET_CODE", orderSetCode);
            sysOrdSetDtlDS.setItem(i, "OPT_USER", Operator.getID());
            sysOrdSetDtlDS.setItem(i, "OPT_DATE", now);
            sysOrdSetDtlDS.setItem(i, "OPT_TERM", Operator.getIP());
            //==========pangben 2013-2-19 解决重复点击保存操作,删除一行已经存在的数据
            if(null==sysOrdSetDtlDS.getItemString(i, 1) || sysOrdSetDtlDS.getItemString(i, 1).length()<=0)
            	sysOrdSetDtlDS.deleteRow(i);
        }
        //删除最后一行，因为自动插入的那行没有数据
        //sysOrdSetDtlDS.deleteRow(detailCount);
    }


    /**
     * 收集界面上的值保存使用（更新保存）
     * @param dataStore TDataStore
     */
    public void setDataInTDS(TDataStore dataStore, int row, boolean dateFlg) {
        //当前数据库时间
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        //保存的数据
        dataStore.setItem(row, "OPT_USER", Operator.getID());
        dataStore.setItem(row, "OPT_DATE", date);
        dataStore.setItem(row, "OPT_TERM", Operator.getIP());

        dataStore.setItem(row, "START_DATE", START_DATE);
        dataStore.setItem(row, "END_DATE", END_DATE);
        //执行标记
        dataStore.setItem(row, "ACTIVE_FLG", getValue("ACTIVE_FLG"));

        dataStore.setItem(row, "ORDER_CODE", ORDER_CODE.getValue());
        dataStore.setItem(row, "ORDER_DESC", ORDER_DESC.getValue());
        dataStore.setItem(row, "PY1", PY1.getValue());
        dataStore.setItem(row, "USEDEPT_CODE", USEDEPT_CODE.getValue());
        dataStore.setItem(row, "EXEC_DEPT_CODE", EXEC_DEPT_CODE.getValue());

        dataStore.setItem(row, "CHARGE_HOSP_CODE", CHARGE_HOSP_CODE.getValue());
        dataStore.setItem(row, "ORDER_CAT1_CODE", ORDER_CAT1_CODE.getValue());
        dataStore.setItem(row, "DESCRIPTION", DESCRIPTION.getValue());

        dataStore.setItem(row, "OPD_FIT_FLG", OPD_FIT_FLG.getValue());
        dataStore.setItem(row, "EMG_FIT_FLG", EMG_FIT_FLG.getValue());
        dataStore.setItem(row, "IPD_FIT_FLG", IPD_FIT_FLG.getValue());
        dataStore.setItem(row, "HRM_FIT_FLG", HRM_FIT_FLG.getValue());
        dataStore.setItem(row, "TRANS_OUT_FLG", TRANS_OUT_FLG.getValue());
        dataStore.setItem(row, "UNIT_CODE", getValue("UNIT"));
        dataStore.setItem(row, "IS_REMARK", getValue("IS_REMARK"));
        dataStore.setItem(row, "ORDERSET_FLG", "Y");
        TParm parm = getPriceAction();
        dataStore.setItem(row, "OWN_PRICE", parm.getValue("OWN_PRICE",0));
        dataStore.setItem(row, "OWN_PRICE2", parm.getValue("OWN_PRICE2",0));
        dataStore.setItem(row, "OWN_PRICE3", parm.getValue("OWN_PRICE3",0));
        dataStore.setItem(row, "CAT1_TYPE",getCta1Type("" + getValue("ORDER_CAT1_CODE")));
        //=============pangben modify 20110427 start
        dataStore.setItem(row, "REGION_CODE", getValue("REGION_CODE"));
        //=============pangben modify 20110427 stop
        dataStore.setItem(row, "ORD_SUPERVISION", ORD_SUPERVISION.getValue());

    }

    public TParm getPriceAction(){
       double ownPrice = 0;
       double nhiPrice = 0;
       double govPrice = 0;
       /*for(int i = 0;i<downTable.getRowCount();i++){
           if(sysOrdSetDtlDS.getRowParm(i).getValue("ORDER_CODE").length() == 0)
               continue;
           String SQL = " SELECT (CASE WHEN OWN_PRICE IS NULL THEN 0 ELSE OWN_PRICE END) OWN_PRICE,"+
                        "        (CASE WHEN OWN_PRICE2 IS NULL THEN 0 ELSE OWN_PRICE2 END) OWN_PRICE2,"+
                        "        (CASE WHEN OWN_PRICE3 IS NULL THEN 0 ELSE OWN_PRICE3 END) OWN_PRICE3"+
                        " FROM SYS_FEE"+
                        " WHERE ORDER_CODE = '"+sysOrdSetDtlDS.getRowParm(i).getValue("ORDER_CODE")+"'";
           TParm price = new TParm(TJDODBTool.getInstance().select(SQL));
           ownPrice += (price.getDouble("OWN_PRICE",0) * sysOrdSetDtlDS.getRowParm(i).getDouble("DOSAGE_QTY"));
           nhiPrice += (price.getDouble("OWN_PRICE2",0) * sysOrdSetDtlDS.getRowParm(i).getDouble("DOSAGE_QTY"));
           govPrice += (price.getDouble("OWN_PRICE3",0) * sysOrdSetDtlDS.getRowParm(i).getDouble("DOSAGE_QTY"));
       }*/
       TParm parmPrice = new TParm();
       parmPrice.addData("OWN_PRICE",ownPrice);
       parmPrice.addData("OWN_PRICE2",nhiPrice);
       parmPrice.addData("OWN_PRICE3",govPrice);
       return parmPrice;
   }
    /**
     * 得到SYS_FEE的TDS
     */
    private void setDataInSysFeeTds() {
        if(upTable.getSelectedRow() < 0)
            return;
        String sql = "";
        String orderCode = ORDER_CODE.getValue();
        sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE='" + orderCode + "'";
        sysFeeDS.setSQL(sql);
        if (sysFeeDS.retrieve() <= 0)
            return;
        //当前数据库时间
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        //保存的数据
        sysFeeDS.setItem(0, "OPT_USER", Operator.getID());
        sysFeeDS.setItem(0, "OPT_DATE", date);
        sysFeeDS.setItem(0, "OPT_TERM", Operator.getIP());
        sysFeeDS.setItem(0, "ORDER_CAT1_CODE", ORDER_CAT1_CODE.getValue());
        sysFeeDS.setItem(0, "CAT1_TYPE",getCta1Type("" + ORDER_CAT1_CODE.getValue()));
        sysFeeDS.setItem(0, "ACTIVE_FLG",getValue("ACTIVE_FLG"));
        sysFeeDS.setItem(0, "UNIT_CODE", getValue("UNIT"));
        sysFeeDS.setItem(0, "IS_REMARK", getValue("IS_REMARK"));
        //==== wukai add 20170315 start
        sysFeeDS.setItem(0, "OPD_FIT_FLG", OPD_FIT_FLG.getValue());
        sysFeeDS.setItem(0, "EMG_FIT_FLG", EMG_FIT_FLG.getValue());
        sysFeeDS.setItem(0, "IPD_FIT_FLG", IPD_FIT_FLG.getValue());
        sysFeeDS.setItem(0, "HRM_FIT_FLG", HRM_FIT_FLG.getValue());
        //==== wukai add 20170315 end
        
        //====pangben modify 20110427 start
        sysFeeDS.setItem(0, "REGION_CODE", getValue("REGION_CODE"));
         //====pangben modify 20110427 stop
        sysFeeDS.setItem(0, "ORD_SUPERVISION", getValue("ORD_SUPERVISION"));
        return ;
    }



    /**
     * 取得医令细分类
     * @param orderCat1Type String
     * @return String
     */
    private String getCta1Type(String orderCat1Type){
        String SQL = "SELECT CAT1_TYPE FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE = '"+orderCat1Type+"'";
        TParm parm = new TParm(getDBTool().select(SQL));
        if(parm.getErrCode() != 0)
            return "";
        return parm.getValue("CAT1_TYPE",0);
    }

    /**
     * 取得数据库访问类
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool(){
       return TJDODBTool.getInstance();
    }
    /**
     * 当新建的时候自动生成编码号的主方法
     */
    public void cerateNewDate() {
        String newCode = "";

        //接收文本
        upTable.acceptText();
        //取table数据
       // TDataStore dataStore = upTable.getDataStore();
        //找到最大科室代码(dataStore,列名)
        //========pangben modify 20110427 start 注释去掉不需要查询表中的最大编号，通过查询数据库中的的数值显示最大编号
        // String maxCode = getMaxCode(dataStore, "ORDER_CODE");
        //System.out.println("maxCode:"+maxCode);
        //========pangben modify 20110427 start

        //找到选中的树节点
        TTreeNode node = tree.getSelectionNode();
        //如果没有选中的节点
        if (node == null)
            return;
        //得到当前ID
        String nowID = node.getID();
        int classify = 1;
        //如果点的是树的父节点存在,得到当前编码规则
        if (nowID.length() > 0)
            classify = ruleTool.getNumberClass(nowID) + 1;
        //如果是最小节点,可以增加一行
        if (classify > ruleTool.getClassifyCurrent()) {
            //得到默认的自动添加的医嘱代码
            //============查找此编号规则中编号最大值
            String sql =
                    "SELECT MAX(ORDER_CODE) AS ORDER_CODE FROM SYS_FEE_HISTORY WHERE ORDER_CODE LIKE '" +
                    nowID + "%'";
            TParm parm = new TParm(getDBTool().select(sql));
            String maxCode = parm.getValue("ORDER_CODE", 0);
            //===pangben modify 20110427 start

            String no = ruleTool.getNewCode(maxCode, classify);
            newCode = nowID + no;
            //得到新添加的table数据行号(相当于TD中的insertRow()方法)
            int row = upTable.addRow();
            //设置当前选中行为添加的行
            upTable.setSelectedRow(row);
            //给科室代码添加默认值
            upTable.setItem(row, "ORDER_CODE", newCode);
            //默认科室名称
            upTable.setItem(row, "ORDER_DESC", "(新建名称)");
            //默认科室简称
            upTable.setItem(row, "SPECIFICATION", null);
            //默认科室
            upTable.setItem(row, "OWN_PRICE", null);
            //默认最小科室注记
            upTable.setItem(row, "UNIT_CODE", null);
        }
        //把自动生成的orderCode设置到ORDER_CODE上
        ORDER_CODE.setText(newCode);
    }


    /**
     * 得到最大的编号
     * @param dataStore TDataStore
     * @param columnName String
     * @return String
     */
    public String getMaxCode(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return "";
        String s = "";
        if (dataStore.isFilter()) {
            TParm a = dataStore.getBuffer(TDataStore.FILTER);
            int count = a.getCount();
            for (int i = 0; i < count; i++) {
                String value = a.getValue(columnName, i);
                if (StringTool.compareTo(s, value) < 0)
                    s = value;
            }
        }
        else {
            int count = dataStore.rowCount();
            for (int i = 0; i < count; i++) {
                String value = dataStore.getItemString(i, columnName);
                if (StringTool.compareTo(s, value) < 0)
                    s = value;
            }
        }
        return s;
    }

    /**
     * 根据table上的某一行数据给下面的控件初始化值
     * @param date TParm
     */
    public void setValueForDownCtl(TParm date) {

        clearCtl();
        this.setValue("SUB_SYSTEM_CODE", date.getValue("SUB_SYSTEM_CODE"));
        this.setValue("SUB_SYSTEM_CODE", date.getValue("SUB_SYSTEM_CODE"));
        this.setValue("RPTTYPE_CODE", date.getValue("RPTTYPE_CODE"));
        this.setValue("DEV_CODE", date.getValue("DEV_CODE"));
        this.setValue("OPTITEM_CODE", date.getValue("OPTITEM_CODE"));
        this.setValue("MR_CODE", date.getValue("MR_CODE"));
        this.setValue("DEGREE_CODE", date.getValue("DEGREE_CODE"));

        this.setValue("ACTIVE_FLG", date.getValue("ACTIVE_FLG"));
        this.setValue("ORDER_CODE", date.getValue("ORDER_CODE"));
        this.setValue("ORDER_DESC", date.getValue("ORDER_DESC"));
        this.setValue("PY1", date.getValue("PY1"));
        this.setValue("DESCRIPTION", date.getValue("DESCRIPTION"));

        this.setValue("CHARGE_HOSP_CODE", date.getValue("CHARGE_HOSP_CODE"));
        this.setValue("ORDER_CAT1_CODE", date.getValue("ORDER_CAT1_CODE"));
        this.setValue("USEDEPT_CODE", date.getValue("USEDEPT_CODE"));
        this.setValue("EXEC_DEPT_CODE", date.getValue("EXEC_DEPT_CODE"));
        this.setValue("DESCRIPTION", date.getValue("DESCRIPTION"));

        this.setValue("OPD_FIT_FLG", date.getValue("OPD_FIT_FLG"));
        this.setValue("EMG_FIT_FLG", date.getValue("EMG_FIT_FLG"));
        this.setValue("IPD_FIT_FLG", date.getValue("IPD_FIT_FLG"));
        this.setValue("HRM_FIT_FLG", date.getValue("HRM_FIT_FLG"));
        this.setValue("TRANS_OUT_FLG", date.getValue("TRANS_OUT_FLG"));
        this.setValue("TRANS_HOSP_CODE", date.getValue("TRANS_HOSP_CODE"));
        this.setValue("UNIT", date.getValue("UNIT_CODE"));
        this.setValue("IS_REMARK", date.getValue("IS_REMARK"));
        //=============pangben modify 20110427 start
        this.setValue("REGION_CODE",date.getValue("REGION_CODE"));
        //=============pangben modify 20110427 stop
        this.setValue("ORD_SUPERVISION",date.getValue("ORD_SUPERVISION"));
    }


    /**
     * 选择TRANS_OUT_FLG标记
     */
    public void onOutHosp() {
        String value = TRANS_OUT_FLG.getValue();
        TRANS_HOSP_CODE.setEnabled(TypeTool.getBoolean(value));
        if (!TypeTool.getBoolean(value)) {
            TRANS_HOSP_CODE.setText("");
        }
    }

    /**
     * 得到名称拼音
     */
    public void onPY1() {
        String orderDesc = ORDER_DESC.getText();
        String orderPy = SYSHzpyTool.getInstance().charToCode(orderDesc);
        PY1.setText(orderPy);
        DESCRIPTION.grabFocus();
    }


    /**
     * 设置所有的控件是否可用（历史数据不可以修改）
     * @param flg boolean
     */
    public void setEnabledForCtl(boolean flg) {
        String tag[] = controlName.split(";");
        int count = tag.length;
        for (int i = 0; i < count; i++) {
            this.callFunction("UI|" + tag[i] + "|setEnabled", flg);
        }
        ACTIVE_FLG.setEnabled(true);
    }

    /**
     * ‘启用’只控制end_date不会控制start_date(新建：当前时间；查询出的：是旧的时间)
     */
    public void onExe() {

        Timestamp time = TJDODBTool.getInstance().getDBTime();
        String tempStartDate = StringTool.getString(time,
            "yyyy/MM/dd HH:mm:ss");
        START_DATE = tempStartDate.substring(0, 4) +
            tempStartDate.substring(5, 7) +
            tempStartDate.substring(8, 10) +
            tempStartDate.substring(11, 13) +
            tempStartDate.substring(14, 16) +
            tempStartDate.substring(17, 19);

        //决定启用的时候把失效日期写为"99991231235959"
        if (TypeTool.getBoolean(ACTIVE_FLG.getValue())) {
            END_DATE = "99991231235959";
        }
        else { //停用的时候end_date改为当前时间
            String date = StringTool.getString(time, "yyyy/MM/dd HH:mm:ss");
            END_DATE = date.substring(0, 4) +
                date.substring(5, 7) + date.substring(8, 10) +
                date.substring(11, 13) +
                date.substring(14, 16) +
                date.substring(17, 19);
        }
    }


    /**
     * table双击选中树
     * @param row int
     */
    public void onTableDoubleCleck() {

        upTable.acceptText();
        int row = upTable.getSelectedRow();
        String value = upTable.getItemString(row, 0);
        //得到上层编码
        String partentID = ruleTool.getNumberParent(value);
        TTreeNode node = treeRoot.findNodeForID(partentID);
        if (node == null)
            return;
        //得到界面上的树对象
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        //设置树的选中节点
        tree.setSelectNode(node);
        tree.update();
        //调用查询事件
        //=====pangben modify 20110427 start
        onCleckClassifyNode(partentID, upTable,Operator.getRegion());
        //=====pangben modify 20110427 stop
        //table中设置选中行
        int count = upTable.getRowCount(); //table的行数
        for (int i = 0; i < count; i++) {
            //拿到物资代码
            String invCode = upTable.getItemString(i, 0);
            if (value.equals(invCode)) {
                //设置选中行
                upTable.setSelectedRow(i);
                return;
            }
        }
    }

    /**
     * 选中对应树节点的事件
     * @param parentID String
     * @param table TTable
     */
    public void onCleckClassifyNode(String parentID, TTable table,String regionCode) {
        //===========pangben modify 20110427 start
        String region="";
        if(null!=regionCode&&!"".equals(regionCode))
            region=" AND (REGION_CODE='"+regionCode+"' OR REGION_CODE IS NULL OR REGION_CODE ='') ";
        //table中的datastore中查询数据sql
        table.setSQL("select * from SYS_FEE_HISTORY where ORDER_CODE like '" +
                     parentID +
                     "%'"+region);
          //===========pangben modify 20110427 stop
        //查找数据
        table.retrieve();
        //放置数据到table中
        table.setDSValue();
        //设置新增按钮
        callFunction("UI|new|setEnabled", true);

    }

    /**
     * 过滤
     * @param ob Object
     */
    public void onFilter(Object ob) {
        if ("ALL".equals(ob.toString())) {
            upTable.setFilter("");
            upTable.filter();
            //放置数据到table中
            upTable.setDSValue();
            //设置新增按钮
            callFunction("UI|new|setEnabled", true);
        }
        else if ("Y".equals(ob.toString())) {
            upTable.setFilter("ACTIVE_FLG='Y'");
            upTable.filter();
            //放置数据到table中
            upTable.setDSValue();
            //设置新增按钮
            callFunction("UI|new|setEnabled", true);
        }
        else {
            upTable.setFilter("ACTIVE_FLG='N'");
            upTable.filter();
            //放置数据到table中
            upTable.setDSValue();
            //设置新增按钮
            callFunction("UI|new|setEnabled", false);
        }
        updateMastOwnPrice();

    }
    public boolean onSaveCheck(){
//        if(getValueString("USEDEPT_CODE").length() == 0){
//            messageBox("适用科室不可为空");
//            return true;
//        }
//        if(getValueString("EXEC_DEPT_CODE").length() == 0){
//            messageBox("执行科室不可为空");
//            return true;
//        }
        if(getValueString("ORDER_CODE").length() == 0){
            messageBox("医嘱代码不可为空");
            return true;
        }
        if(getValueString("CHARGE_HOSP_CODE").length() == 0){
            messageBox("院内代码不可为空");
            return true;
        }
        if(getValueString("ORDER_DESC").length() == 0){
            messageBox("医嘱名称不可为空");
            return true;
        }
        if(getValueString("UNIT").length() == 0){
            messageBox("单位不可为空");
            return true;
        }
        if(getValueString("PY1").length() == 0){
            messageBox("医嘱拼音不可为空");
            return true;
        }
        if(getValueString("ORDER_CAT1_CODE").length() == 0){
            messageBox("医嘱分类不可为空");
            return true;
        }
        return false;
    }


    //测试用例
    public static void main(String[] args) {
        JavaHisDebug.initClient();
        //JavaHisDebug.TBuilder();

//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("sys\\SYS_FEE\\SYSFEE_ORDPACK.x");
    }

}
