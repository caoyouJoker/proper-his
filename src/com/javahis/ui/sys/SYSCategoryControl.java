package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import jdo.sys.SYSRuleTool;
import com.dongyang.ui.event.TTreeEvent;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.ui.TTree;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.util.TMessage;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSHzpyTool;

/**
 *
 * <p>Title: 编码规则控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.1.14
 * @version 1.0
 */
public class SYSCategoryControl extends TControl{
    /**
     * 树根
     */
    private TTreeNode treeRoot;
    /**
     * 编号规则类别工具
     */
    SYSRuleTool ruleTool;
    /**
     * 初始化
     */
    public void onInit()
    {
        super.onInit();
        onInitSelectTree();
        addEventListener("TREE->" + TTreeEvent.CLICKED,"onTreeClicked");
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,"onTableChangeValue");
        //默认选中
        defaultSelected();
        //初始化权限
        initPopedem();
    }
    /**
     * 初始化权限
     */
    public void initPopedem()
    {
        if(!getPopedem("comboAUT"))
        {
            TComboBox combo = (TComboBox) callFunction("UI|RULE_TYPE|getThis");
            combo.setEnabled(false);
        }
    }
    /**
     * 默认选中
     */
    public void defaultSelected()
    {
        TComboBox combo = (TComboBox)callFunction("UI|RULE_TYPE|getThis");
        if(combo.rowCount() <= 1)
            return;
        String id = combo.getItem(1).getID();
        combo.setSelectedID(id);
    }
    /**
     * 初始化树
     */
    public void onInitSelectTree()
    {
        out("begin");
        //得到树根
        treeRoot = (TTreeNode)callMessage("UI|TREE|getRoot");
        if(treeRoot == null)
            return;
        treeRoot.setText("分类规则");
        treeRoot.setType("Root");
        treeRoot.setID("");
        treeRoot.removeAllChildren();
        callMessage("UI|TREE|update");
        out("end");
    }
    /**
     * 选择类型
     */
    public void onSelectType()
    {
        String value = (String)getValue("RULE_TYPE");
        TTable table = (TTable)callFunction("UI|TABLE|getThis");
        if(value == null || value.length() == 0)
            table.setFilter("");
        else
            table.setFilter("RULE_TYPE = '" + value + "'");
        table.filter();
        treeRoot.removeAllChildren();
        TDataStore dataStore = table.getDataStore();
        ruleTool = new SYSRuleTool(value);
        if(ruleTool.isLoad())
        {
            TTreeNode node[] = ruleTool.getTreeNode(dataStore,"CATEGORY_CODE",
                "CATEGORY_CHN_DESC","Path","SEQ");
            for(int i = 0;i < node.length;i++)
                treeRoot.addSeq(node[i]);
        }
        TTree tree = getTree();
        tree.update();
        tree.setSelectNode(treeRoot);
        onTreeClicked(treeRoot);
    }
    /**
     * 得到树对象
     * @return TTree
     */
    public TTree getTree()
    {
        return (TTree)callFunction("UI|TREE|getThis");
    }
    /**
     * 得到Table
     * @return TTable
     */
    public TTable getTable()
    {
        return (TTable)callFunction("UI|TABLE|getThis");
    }
    /**
     * 单击树
     * @param parm Object
     */
    public void onTreeClicked(Object parm)
    {
        String value = (String)getValue("RULE_TYPE");
        if(value == null)
            return;
        TTreeNode node = (TTreeNode)parm;
        if(node == null)
            return;
        TTable table = (TTable)callFunction("UI|TABLE|getThis");
        table.acceptText();
        if(node.getType().equals("Root"))
            table.setFilter("RULE_TYPE = '" + value + "'"+
                            " AND length(CATEGORY_CODE)=" + ruleTool.getClassify(0));
        else
        {
            String id = node.getID();
            int length = ruleTool.getClassNumber(ruleTool.getNumberClass(id) + 1);
            table.setFilter("RULE_TYPE = '" + value + "'" +
                            " AND length(CATEGORY_CODE)=" + length +
                            " AND CATEGORY_CODE like '" + id + "%' ");
        }
        table.filter();
        table.setSort("SEQ");
        table.sort();
    }
    /**
     * 属性Table改变值
     * @param obj Object
     * @return boolean
     */
    public boolean onTableChangeValue(Object obj)
    {
        String ruleType = (String)getValue("RULE_TYPE");
        TTableNode node = (TTableNode)obj;
        if(node == null)
            return false;
        if(node.getValue().equals(node.getOldValue()))
            return false;
        String columnName = node.getTable().getDataStoreColumnName(node.getColumn());
        String id = node.getTable().getItemString(node.getRow(),"CATEGORY_CODE");
        TTree tree = getTree();
        TTreeNode treenode = tree.getSelectionNode();
        if(treenode == null)
            return false;
        if("CATEGORY_CHN_DESC".equals(columnName))
        {
            TTreeNode treenode1 = treenode.findNodeForID(id);
            String value = "" + node.getValue();
            if(treenode1 != null)
            {
                treenode1.setText(value);
                tree.update();
            }
            //modify by fudw
            String py =SYSHzpyTool.getInstance().charToCode(value);
//            String py = TMessage.getPy(value);
            node.getTable().setItem(node.getRow(),"PY1",py);
            return false;
        }
        if("CATEGORY_CODE".equals(columnName))
        {
            //得到父ID
            String parentID = treenode.getID();
            if((parentID.length() > 0) && !((String)node.getValue()).startsWith(parentID))
            {
                messageBox_("编号" + node.getValue() + "不包含上级编号" + parentID + "!");
                return true;
            }
            //效验编号长度
            int classify = 1;
            if(parentID.length() > 0)
                classify = ruleTool.getNumberClass(parentID) + 1;
            int length = ruleTool.getClassNumber(classify);
            if(((String)node.getValue()).length() != length)
            {
                messageBox_("编号" + node.getValue() + "长度不等于" + length + "!");
                return true;
            }
            if(!checkCode((String)node.getValue(),node.getRow()))
            {
                messageBox_("编号" + node.getValue() + "重复!");
                return true;
            }
            TTreeNode treenode1 = treenode.findNodeForID( (String) node.
                getOldValue());
            if (treenode1 != null)
                treenode1.setID((String)node.getValue());

            String newValue = (String)node.getValue();
            String oldValue = (String)node.getOldValue();
            TDataStore dataStore = node.getTable().getDataStore();
            int count = dataStore.rowCountFilter();
            for(int i = 0;i < count;i++)
            {
                String code = TCM_Transform.getString(dataStore.getItemData(i,"CATEGORY_CODE",TDataStore.FILTER));
                String t = TCM_Transform.getString(dataStore.getItemData(i,"RULE_TYPE",TDataStore.FILTER));
                if(t == null || !t.equals(ruleType))
                    continue;
                if(code.startsWith(oldValue))
                {
                    String v = code.substring(newValue.length());
                    dataStore.setItem(i,"CATEGORY_CODE",newValue + v,TDataStore.FILTER);
                    treenode1 = treenode.findNodeForID(code);
                    if (treenode1 != null)
                        treenode1.setID(newValue + v);
                }
            }
            return false;
        }
        if("SEQ".equals(columnName))
        {
            TTreeNode treenode1 = treenode.findNodeForID(id);
            if (treenode1 != null)
            {
                treenode1.setSeq(TCM_Transform.getInt(node.getValue()));
                treenode.remove(treenode1);
                treenode.addSeq(treenode1);
                tree.update();
            }
            return false;
        }
        return false;
    }
    /**
     * 验证编号是否重复
     * @param code String
     * @param row int
     * @return boolean
     */
    public boolean checkCode(String code,int row)
    {
        TDataStore dataStore = getTable().getDataStore();
        int count = dataStore.rowCount();
        for(int i = 0;i < count;i++)
        {
            if(i == row)
                continue;
            if(dataStore.getItemString(i,"CATEGORY_CODE").equals(code))
                return false;
        }
        return true;
    }
    /**
     * 新增方法
     */
    public void onNew()
    {
        String value = (String)getValue("RULE_TYPE");
        if(value == null)
            return;
        TTable table = (TTable)callFunction("UI|TABLE|getThis");
        //接收文本
        table.acceptText();
        TDataStore dataStore = table.getDataStore();
        String maxCode = getMaxCode(dataStore,"CATEGORY_CODE");

        TTree tree = getTree();
        TTreeNode node = tree.getSelectionNode();
        if(node == null)
            return;
        //得到父ID
        String parentID = node.getID();
        int classify = 1;
        if(parentID.length() > 0)
            classify = ruleTool.getNumberClass(parentID) + 1;
        if(classify > ruleTool.getClassifyCurrent())
        {
            messageBox_("超过最大层数,不能新增!");
            return;
        }
        String no = ruleTool.getNewCodeByString(maxCode,classify);
        int seq=getMaxSeq( dataStore,"SEQ");
        int row = table.addRow();
        table.setSelectedRow(row);
        table.setItem(row,"RULE_TYPE",value);
        table.setItem(row,"CATEGORY_CODE",parentID + no);
        table.setItem(row,"CATEGORY_CHN_DESC","(新建分类)");
        table.setItem(row,"SEQ",seq);
        if(classify == ruleTool.getClassifyCurrent())
            table.setItem(row,"DETAIL_FLG","Y");
        else
            table.setItem(row,"DETAIL_FLG","N");
        TTreeNode newNode = new TTreeNode("(新建分类)","Path");
        newNode.setID(parentID + no);
        newNode.setSeq(seq);
        node.addSeq(newNode);
        tree.update();
    }
    /**
     * 得到最大的编号
     * @param dataStore TDataStore
     * @param columnName String
     * @return String
     */
    public String getMaxCode(TDataStore dataStore,String columnName)
    {
        if(dataStore == null)
            return "";
        int count = dataStore.rowCount();
        String s = "";
        for(int i = 0;i < count;i++)
        {
            String value = dataStore.getItemString(i,columnName);
            if (StringTool.compareTo(s, value) < 0)
                s = value;
        }
        return s;
    }
    /**
     * 删除
     */
    public void onDelete()
    {
        String ruleType = (String)getValue("RULE_TYPE");
        if(ruleType == null || ruleType.length() == 0)
            return;
        TTable table = (TTable)callFunction("UI|TABLE|getThis");
        //接收文本
        table.acceptText();
        int row = table.getSelectedRow();
        if(row < 0)
            return;
        String code = table.getItemString(row,"CATEGORY_CODE");
        TDataStore dataStore = table.getDataStore();
        TTree tree = getTree();
        TTreeNode treenode = tree.getSelectionNode();
        int count = dataStore.rowCountFilter();
        table.removeRow(row);
        TTreeNode treenode1 = treenode.findNodeForID(code);
        if(treenode1 != null)
            treenode.remove(treenode1);
        for(int i = count - 1;i >= 0;i--)
        {
            String t = TCM_Transform.getString(dataStore.getItemData(i,"RULE_TYPE",TDataStore.FILTER));
            if(t == null || !t.equals(ruleType))
                continue;
            String id = TCM_Transform.getString(dataStore.getItemData(i,"CATEGORY_CODE",TDataStore.FILTER));
            if(id.startsWith(code))
                dataStore.deleteRow(i,TDataStore.FILTER);
        }
        tree.update();

    }
    /**
     * 保存事件
     * @return boolean true 成功 false 失败
     */
    public boolean onUpdate()
    {
        Timestamp date = SystemTool.getInstance().getDate();
        TTable table = (TTable)callFunction("UI|TABLE|getThis");
        //接收文本
        table.acceptText();
        //获得全部改动的行号
        int rows[] = table.getModifiedRowsFilter();
        for(int i = 0;i < rows.length;i++)
        {
            table.setItemFilter(rows[i],"OPT_USER",Operator.getID());
            table.setItemFilter(rows[i],"OPT_DATE",date);
            table.setItemFilter(rows[i],"OPT_TERM",Operator.getIP());
        }
        if(!table.update())
        {
            messageBox("保存失败");
            return false;
        }
        messageBox("保存成功");
        return true;
    }
    /**
     * 是否关闭窗口
     * @return boolean true 关闭 false 不关闭
     */
    public boolean onClosing()
    {
        TTable table = (TTable)callFunction("UI|TABLE|getThis");
        table.acceptText();
        if(table.getModifiedRows().length == 0 &&
           table.getModifiedRowsFilter().length == 0)
            return true;
        int value = messageBox("提示信息","是否保存?",YES_NO_CANCEL_OPTION);
        if(value == 0)
        {
            if(!onUpdate())
                return false;
            return true;
        }
        if(value == 1)
            return true;
        return false;
    }
    /**
     * 得到最大的序号
     * @param dataStore TDataStore
     * @param columnName String
     * @return String
     */
    public int getMaxSeq(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return 0;
        //保存数据量
        int count = dataStore.rowCount();
        //保存最大号
        int s = 0;
        for (int i = 0; i < count; i++) {
            int value = dataStore.getItemInt(i, columnName);
            //保存最大值
            if (s < value) {
                s = value;
                continue;
            }
        }
        //最大号加1
        s++;
        return s;
    }

}
