package com.javahis.ui.nss;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.nss.NSSMenuTool;
import com.dongyang.ui.TTextField;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.ui.TTree;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTreeNode;
import jdo.nss.NSSSQL;

/**
 * <p>Title: 菜单设定</p>
 *
 * <p>Description: 菜单设定</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSMenuControl
    extends TControl {

    public NSSMenuControl() {
        super();
    }

    private TTable table;

    private TTree tree;

    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        // 初始化树结构
        onInitTree();
        tree = (TTree) callMessage("UI|TREE|getThis");
        //给TREE添加监听事件
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        table = getTable("TABLE");
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        String menu_code = this.getValueString("MENU_CODE");
        if (menu_code != null && menu_code.length() > 0) {
            parm.setData("MENU_CODE", menu_code);
        }

        if (! ( (TTree) getComponent("TREE")).getSelectNode().isRoot()) {
            parm.setData("NSSTYPE_CODE",
                         ( (TTree) getComponent("TREE")).getSelectNode().getID());
        }

        TParm result = NSSMenuTool.getInstance().onQueryNSSMenu(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
        }
        else {
            table.setParmValue(result);
        }
    }

    /**
     * 新增方法
     */
    public void onNew() {
        if (tree.getSelectNode().isRoot())
            return;
        if (tree.getSelectNode() == null) {
            this.messageBox("请选择菜品类别");
            return;
        }
        this.setValue("MENU_CODE",
                      getNssMaxSerialNumber(tree.getSelectNode().getID()));
        getTextField("MENU_CODE").setEnabled(false);
    }

    /**
     * 保存方法
     */
    public void onSave() {
        if (!checkData()) {
            return;
        }
        TParm parm = new TParm();
        parm.setData("MENU_CODE", this.getValueString("MENU_CODE"));
        parm.setData("MENU_CHN_DESC", this.getValueString("MENU_CHN_DESC"));
        parm.setData("MENU_ENG_DESC", this.getValueString("MENU_ENG_DESC"));
        parm.setData("PY1", this.getValueString("PY1"));
        parm.setData("PY2", this.getValueString("PY2"));
        parm.setData("SEQ", this.getValueInt("SEQ"));
        parm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
        parm.setData("CALORIES", this.getValueDouble("CALORIES"));
        parm.setData("TABOO_CODE", this.getValueString("TABOO_CODE"));
        parm.setData("PRICE", this.getValueDouble("PRICE"));
        parm.setData("NSSTYPE_CODE", tree.getSelectNode().getID());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        //System.out.println("parm----"+parm);
        TParm result = new TParm();
        if (table.getSelectedRow() < 0) {
            // 新增数据
            result = NSSMenuTool.getInstance().onInsertNSSMenu(parm);
        }
        else {
            // 更新数据
            result = NSSMenuTool.getInstance().onUpdateNSSMenu(parm);
        }
        if (result.getErrCode() < 0) {
            this.messageBox("E0001");
        }
        else {
            this.messageBox("P0001");
            onQuery();
        }
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("请选择删除数据");
            return;
        }
        //table.getDataStore().showDebug();
        TParm parm = table.getParmValue().getRow(row);
        //System.out.println("parm---"+parm);
        TParm result = NSSMenuTool.getInstance().onDeleteNSSMenu(parm);
        if (result.getErrCode() < 0) {
            this.messageBox("删除失败");
        }
        else {
            this.messageBox("删除成功");
            table.removeRow(row);
        }
    }

    /**
     * 清空方法
     */
    public void onClear() {
        if (table.getRowCount() > 0)
            table.removeRowAll();
        this.clearValue("MENU_CODE;MENU_CHN_DESC;MENU_ENG_DESC;PY1;PY2;"
                        + "SEQ;DESCRIPTION;CALORIES;TABOO_CODE;PRICE");
        //table.removeRowAll();
        ( (TTextField) getComponent("MENU_CODE")).setEnabled(true);
    }

    /**
     * 表格单击事件
     */
    public void onTableClick() {
        String menu_code = table.getParmValue().getRow(table.getSelectedRow()).
            getValue("MENU_CODE");
        TParm parm = new TParm();
        parm.setData("MENU_CODE", menu_code);
        TParm result = NSSMenuTool.getInstance().onQueryNSSMenu(parm);
        String parmStr = "MENU_CODE;MENU_CHN_DESC;MENU_ENG_DESC;PY1;PY2;"
            + "SEQ;DESCRIPTION;CALORIES;TABOO_CODE;PRICE";
        TParm baseParm = result.getRow(0);
        this.setValueForParm(parmStr, baseParm);
        this.getTextField("MENU_CODE").setEnabled(false);
    }

    /**
     * 树的单击事件
     */
    public void onTreeClicked() {
        //检核是否得到树的节点
        if ( ( (TTree) getComponent("TREE")).getSelectNode() == null)
            return;
//        if ( ( (TTree) getComponent("TREE")).getSelectNode().isRoot())
//            return;
        this.clearValue("MENU_CODE;MENU_CHN_DESC;MENU_ENG_DESC;PY1;PY2;"
                        + "SEQ;DESCRIPTION;CALORIES;TABOO_CODE;PRICE");
        //table.removeRowAll();
        ( (TTextField) getComponent("MENU_CODE")).setEnabled(true);

//        queryNssTableInfo( ( (TTree) getComponent("TREE")).getSelectNode().
//                          getID());
        this.onQuery();
    }

    /**
     * 菜单中文说明回车事件
     */
    public void onMenuDescAction() {
        String py = TMessage.getPy(this.getValueString("MENU_CHN_DESC"));
        setValue("PY1", py);
    }

    /**
     * 初始化树
     */
    private void onInitTree() {
        TParm parmRule = new TParm(TJDODBTool.getInstance().select(NSSSQL.
            getNSSMeunRule()));
        //得到界面上的树对象
        tree = (TTree) callMessage("UI|TREE|getThis");
        tree.getRoot().removeAllChildren();
        TTreeNode treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        treeRoot.setText("菜品类别");
        treeRoot.setType("Root");
        TParm parm = analyzeINVType();
        if (parm.getCount("NSSTYPE_GROUP") <= 0)
            return;
        for (int i = 0; i < parm.getCount("NSSTYPE_GROUP"); i++) {
            TParm parmI = (TParm) parm.getData("NSSTYPE_GROUP", i);
            for (int j = 0; j < parmI.getCount("CATEGORY_CODE"); j++) {
                TTreeNode treeNode = new TTreeNode();
                treeNode.setText(parmI.getValue("CATEGORY_CHN_DESC", j));
                treeNode.setID(parmI.getValue("CATEGORY_CODE", j));
                if (parmI.getValue("DETAIL_FLG", j).equals("Y"))
                    treeNode.setType("UI");
                else
                    treeNode.setType("Path");
                if (getParentCode(treeNode.getID(), parmRule).length() == 0)
                    treeRoot.addSeq(treeNode);
                else
                    treeRoot.findNodeForID(getParentCode(treeNode.getID(),
                        parmRule)).addSeq(treeNode);
            }
        }
        tree.update();
    }

    /**
     * 得到父节点编码
     * @param code String
     * @param parm TParm
     * @return String
     */
    private String getParentCode(String code, TParm parm) {
        int classify1 = parm.getInt("CLASSIFY1", 0);
        int classify2 = parm.getInt("CLASSIFY2", 0);
        int classify3 = parm.getInt("CLASSIFY3", 0);
        int classify4 = parm.getInt("CLASSIFY4", 0);
        int classify5 = parm.getInt("CLASSIFY5", 0);
        int serialNumber = parm.getInt("SERIAL_NUMBER", 0);
        if (code.length() == classify1)
            return "";
        if (code.length() == classify1 + classify2)
            return code.substring(0, classify1);
        if (code.length() == classify1 + classify2 + classify3)
            return code.substring(0, classify1 + classify2);
        if (code.length() == classify1 + classify2 + classify3 + classify4)
            return code.substring(0, classify1 + classify2 + classify3);
        if (code.length() ==
            classify1 + classify2 + classify3 + classify4 + classify5)
            return code.substring(0,
                                  classify1 + classify2 + classify3 + classify4);
        if (code.length() ==
            classify1 + classify2 + classify3 + classify4 + classify5 +
            serialNumber)
            return code.substring(0,
                                  classify1 + classify2 + classify3 + classify4 +
                                  classify5);
        return "";
    }

    /**
     * 整理菜单分类信息
     * @return TParm
     */
    private TParm analyzeINVType() {
        TParm parm = new TParm();
        TParm parmInf = selectNSSType();
        TParm parmLength = getNssTypeLength();
        for (int i = 0; i < parmLength.getCount(); i++) {
            int lenght = parmLength.getInt("CATEGORY_LENGTH", i);
            TParm parmI = new TParm();
            for (int j = 0; j < parmInf.getCount(); j++) {
                if (lenght == parmInf.getValue("CATEGORY_CODE", j).length()) {
                    copyTParm(parmInf, parmI, j);
                }
            }
            parm.addData("NSSTYPE_GROUP", parmI);
        }
        return parm;
    }

    /**
     * 得到菜单分类信息
     * @return TParm
     */
    private TParm selectNSSType() {
        return new TParm(TJDODBTool.getInstance().select(NSSSQL.
            getNSSMeunCategory()));
    }

    /**
     * 得到菜单分类编码长度信息
     * @return TParm
     */
    private TParm getNssTypeLength() {
        return new TParm(TJDODBTool.getInstance().select(NSSSQL.
            getNSSMeunCategoryLength()));
    }

    /**
     * 拷贝参数类
     * @param fromTParm TParm
     * @param toTParm TParm
     * @param row int
     */
    private void copyTParm(TParm fromTParm, TParm toTParm, int row) {
        for (int i = 0; i < fromTParm.getNames().length; i++) {
            toTParm.addData(fromTParm.getNames()[i],
                            fromTParm.getValue(fromTParm.getNames()[i], row));
        }
    }

    /**
     * 得到最大流水号
     * @param typeCode String
     * @return String
     */
    private String getNssMaxSerialNumber(String typeCode) {
        TParm parmRule = new TParm(TJDODBTool.getInstance().select(NSSSQL.
            getNSSMeunRule()));
        int serialNumber = parmRule.getInt("SERIAL_NUMBER", 0);
        int totNumber = parmRule.getInt("TOT_NUMBER", 0);
        TParm parm = new TParm(TJDODBTool.getInstance().select(NSSSQL.
            getNSSMaxSerialNumber(typeCode)));
        String numString = "";
        if (parm.getCount("MENU_CODE") <= 0 ||
            parm.getValue("MENU_CODE", 0).length() == 0)
            numString = "0";
        else
            numString = parm.getValue("MENU_CODE", 0).substring(typeCode.length(),
                parm.getValue("MENU_CODE", 0).length());
        int num = Integer.parseInt(numString) + 1;
        numString = num + "";
        for (int i = 0; numString.length() < serialNumber; i++)
            numString = "0" + numString;
        String zeroType = "";
        for (int i = 0;
             zeroType.length() <
             (totNumber - typeCode.length() - numString.length());
             i++) {
            zeroType += "0";
        }
        return typeCode + zeroType + numString;
    }

    /**
     * 数据检核
     * @return boolean
     */
    private boolean checkData() {
        if (getTextField("MENU_CODE").isEnabled()) {
            this.messageBox("请选择新增功能添加菜单代码");
            return false;
        }
        String meal_code = this.getValueString("MENU_CODE");
        if (meal_code == null || meal_code.length() <= 0) {
            this.messageBox("菜单代码不能为空");
            return false;
        }
        String meal_chn_desc = this.getValueString("MENU_CHN_DESC");
        if (meal_chn_desc == null || meal_chn_desc.length() <= 0) {
            this.messageBox("菜单中文名不能为空");
            return false;
        }
        String price = this.getValueString("PRICE");
        if (price == null || price.length() <= 0) {
            this.messageBox("价格不能为空");
            return false;
        }
        if (StringTool.getDouble(price) <= 0) {
            this.messageBox("价格不能小于或等于0");
            return false;
        }

        return true;
    }


    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }


}
