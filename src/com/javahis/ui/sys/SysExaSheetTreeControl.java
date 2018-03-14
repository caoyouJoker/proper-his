package com.javahis.ui.sys;

import com.dongyang.control.*;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TTreeNode;
import jdo.sys.SYSRuleTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TButton;
import com.dongyang.util.RunClass;
import com.dongyang.util.TypeTool;
import jdo.sys.Operator;

/**
 * <p>Title: 引用表单</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author WangM
 * @version 1.0
 */
public class SysExaSheetTreeControl
    extends TControl {
    /**
     * 树
     */
    private static String TREE = "TREE";
    /**
     * 数据表1
     */
    private static String TABLE1 = "TABLE1";
    /**
     * 数据表2
     */
    private static String TABLE2 = "TABLE2";
    /**
     * 树根
     */
    private TTreeNode treeRoot;
    /**
     * 树的数据放入datastore用于对树的数据管理
     */
    private TDataStore treeDataStore = new TDataStore();
    /**
     * 编号规则类别工具
     */
    private SYSRuleTool ruleTool;
    /**
     * 接收参数
     */
    private Object main;
    /**
     * 当前页
     */
    private int onlyPage = 1;
    /**
     * 树ID
     */
    private String treeId;
    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        getInitParameter();
        onInitTree();
        initEven();
        onInitNode();
    }

    /**
     * 初始化事件
     */
    public void initEven() {
        //给tree添加监听事件
        addEventListener(TREE + "->" + TTreeEvent.CLICKED, "onTreeClicked");
    }

    /**
     * 初始化参数
     */
    public void getInitParameter() {
        main = this.getParameter();
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
        treeRoot.setText("检验检查分类");
        treeRoot.setEnText("Inspection Inspection Classification");
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
            "SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='EXM_RULE'");
        //如果从dataStore中拿到的数据小于0
        if (treeDataStore.retrieve() <= 0)
            return;
        //过滤数据,是编码规则中的科室数据
        ruleTool = new SYSRuleTool("EXM_RULE");
        if (ruleTool.isLoad()) { //给树篡节点参数:datastore，节点代码,节点显示文字,,节点排序
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                "CATEGORY_CODE",
                "CATEGORY_CHN_DESC", "Path", "SEQ","CATEGORY_ENG_DESC");
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
     * 树事件
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
        //清空
        if (parm == null)
            return;
        onClear();
        TTreeNode node = (TTreeNode) parm;
        if ("Root".equals(node.getType())) {
            return;
        }
        
        //$$ ====2012/03/16 add by lx start 开检查时 一项检查如果点过下一页，再开其他检查时仍在下一页，需要再点上一页才行===$$//
        onlyPage = 1;
        //$$ ====2012/03/16 add by lx start===$$//
        treeId = node.getID();
        queryTabelData();
    }

    /**
     * 查询结果
     */
    public void queryTabelData() {
        int row = (onlyPage - 1) * 20;
        TParm table1Parm = this.queryExa(treeId, row, row + 19);
        //全部行数
        int rowCount = (int) ( (double) table1Parm.getInt("SYSTEM", "COUNT") /
                              20.0 + 0.999);
        this.getTTable(TABLE1).setParmValue(table1Parm);
        if("en".equals(this.getLanguage()))
            this.setValue("PAGECOUNT", "Page" + onlyPage + "/" + rowCount);
        else
            this.setValue("PAGECOUNT", "第" + onlyPage + "页/共" + rowCount + "页");
        ( (TButton)this.getComponent("BP")).setEnabled(onlyPage > 1);
        ( (TButton)this.getComponent("BN")).setEnabled(onlyPage < rowCount);
        //TABLE2
        table1Parm = this.queryExa(treeId, row + 20, row + 39);
        this.getTTable(TABLE2).setParmValue(table1Parm);
    }

    /**
     * 下一页
     */
    public void nextPage() {
        onlyPage++;
        queryTabelData();
    }

    /**
     * 上一页
     */
    public void upPage() {
        onlyPage--;
        queryTabelData();
    }

    /**
     *
     * @param code String
     * @return 查询sys_fee
     */
    public TParm queryExa(String code, int startRow, int endRow) {
        //=========pangben modify 20110607 start
       String region="";
       if(null!=Operator.getRegion()&&!"".equals(Operator.getRegion()))
           region = " AND (REGION_CODE='" + Operator.getRegion() + "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
       //=========pangben modify 20110607 stop
        String sql =
            "SELECT 'N' AS EXEC ,ORDER_CODE,ORDER_DESC,PY1,PY2," +
            "	SEQ,DESCRIPTION,TRADE_ENG_DESC,GOODS_DESC,GOODS_PYCODE," +
            "	ALIAS_DESC,ALIAS_PYCODE,SPECIFICATION,NHI_FEE_DESC,HABITAT_TYPE," +
            "	MAN_CODE,HYGIENE_TRADE_CODE,ORDER_CAT1_CODE,CHARGE_HOSP_CODE,OWN_PRICE,NHI_PRICE," +
            "	GOV_PRICE,UNIT_CODE,LET_KEYIN_FLG,DISCOUNT_FLG,EXPENSIVE_FLG," +
            "	OPD_FIT_FLG,EMG_FIT_FLG,IPD_FIT_FLG,HRM_FIT_FLG,DR_ORDER_FLG," +
            "	INTV_ORDER_FLG,LCS_CLASS_CODE,TRANS_OUT_FLG,TRANS_HOSP_CODE,USEDEPT_CODE," +
            "	EXEC_ORDER_FLG,EXEC_DEPT_CODE,INSPAY_TYPE,ADDPAY_RATE,ADDPAY_AMT," +
            "	NHI_CODE_O,NHI_CODE_E,NHI_CODE_I,CTRL_FLG,CLPGROUP_CODE," +
            "	ORDERSET_FLG,INDV_FLG,SUB_SYSTEM_CODE,RPTTYPE_CODE,DEV_CODE," +
            "	OPTITEM_CODE,MR_CODE,DEGREE_CODE,CIS_FLG,OPT_USER," +
            "	OPT_DATE,OPT_TERM,CAT1_TYPE " +
            " FROM SYS_FEE " +
            " WHERE ORDER_CODE LIKE '%" + code +
            "%' AND ACTIVE_FLG='Y' " + region+" ORDER BY SEQ";//新添加ORDER_DESC排序 20130722 caoyong
        TParm result = new TParm(TJDODBTool.getInstance().select("", sql, true,
            startRow, endRow));
//        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        //System.out.println("RESULT===============" + result);
        return result;
    }

    /**
     * 回传事件
     */
    public void onFetchBack(String tableName) {
        TTable table = (TTable)this.getComponent(tableName);
        int row = table.getSelectedRow();
        int column = table.getSelectedColumn();
        if ("ORDER_DESC".equalsIgnoreCase(table.getParmMap(column))) {
            return;
        }
        table.setValueAt("Y", row, column);
        TParm parm = table.getParmValue().getRow(row);
        boolean result = TypeTool.getBoolean(RunClass.runMethod(main,
            "onQuoteSheet", new Object[] {parm}));
        if (result) {
            table.setValueAt("N", row, column);
        }
    }
    /**
     * 右键
     */
    public void showPopMenu(String tableName){
        TTable table = (TTable)this.getComponent(tableName);
        table.setPopupMenuSyntax("显示集合医嘱细相 \n Display collection details with your doctor,openRigthPopMenu|"+tableName+"");
    }
    /**
     * 细项
     */
    public void openRigthPopMenu(String tableName){
        TTable table = (TTable)this.getComponent(tableName);
        TParm parm = table.getParmValue().getRow(table.getSelectedRow());
//        System.out.println("选中行:"+parm);
        TParm result = this.getOrderSetDetails(parm.getValue("ORDER_CODE"));
        this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", result);
    }
    /**
     * 返回集合医嘱细相的TParm形式
     * @return result TParm
     */
    public TParm getOrderSetDetails(String orderCode) {
        TParm result = new TParm();
        String sql = "SELECT B.*,A.DOSAGE_QTY FROM SYS_ORDERSETDETAIL A,SYS_FEE B  WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORDERSET_CODE='"+orderCode+"'";
        TParm parm = new TParm(this.getDBTool().select(sql));
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            //ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
            result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
            result.addData("SPECIFICATION",
                           parm.getValue("SPECIFICATION", i));
            result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
            result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
            //计算总价格
            double ownPrice = parm.getDouble("OWN_PRICE", i) *
                parm.getDouble("DOSAGE_QTY", i);
            result.addData("OWN_PRICE", parm.getDouble("OWN_PRICE", i));
            result.addData("OWN_AMT", ownPrice);
            result.addData("EXEC_DEPT_CODE",
                           parm.getValue("EXEC_DEPT_CODE", i));
            result.addData("OPTITEM_CODE", parm.getValue("OPTITEM_CODE", i));
            result.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE", i));
        }
        return result;
    }

    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    /**
     * 关闭
     */
    public boolean onClosing() {
        TParm parm = new TParm();
        parm.setData("YYLIST","Y");
        TypeTool.getBoolean(RunClass.runMethod(main,
            "setYYlist", new Object[] {parm}));
        return true;
    }

    /**
     * 清空操作
     */
    public void onClear() {
        this.getTTree(TREE).update();
        this.getTTable(TABLE1).removeRowAll();
        this.getTTable(TABLE2).removeRowAll();
    }

    /**
     * 树
     * @param tag String
     * @return TTree
     */
    public TTree getTTree(String tag) {
        return (TTree)this.getComponent(tag);
    }

    /**
     * 数据表
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
}
