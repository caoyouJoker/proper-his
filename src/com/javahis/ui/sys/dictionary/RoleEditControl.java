package com.javahis.ui.sys.dictionary;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TTreeNode;
import com.dongyang.data.TParm;
import jdo.sys.DictionaryTool;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.manager.TCM_Transform;
import jdo.sys.SYSRolePopedomTool;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;

public class RoleEditControl extends TControl{
    private static String ROLE_TREE="RoleTree";
    private TParm insertData = new TParm();
    private TParm deleteData = new TParm();
    /**
     * 上级组编号
     */
    private String groupID;
    /**
     * 上级编号
     */
    private String parentID;
    private TTreeNode treeRoot;
    public void initRoot()
    {
        String s = (String)getParameter();
        if(s == null)
            s = "PERSON|SYS_SEX";
        String s1[] = StringTool.parseLine(s,"|");
        groupID = s1[0];
        parentID = s1[1];
        //初始化树
        onInitTree();
        insertData = new TParm();
        deleteData = new TParm();
    }
    public void onInit()
    {
        super.onInit();
        //单击选中树项目
        addEventListener(ROLE_TREE + "->" + TTreeEvent.CLICKED,"onTreeClicked");
        initRoot();
    }
    /**
     * 初始化树
     */
    public void onInitTree()
    {
        out("begin");
        treeRoot = (TTreeNode)callMessage("UI|" + ROLE_TREE + "|getRoot");
        if(treeRoot == null)
            return;
        treeRoot.setText("系统结构");
        treeRoot.setGroup("POPEDOM");
        treeRoot.setID("SYS_SUBSYSTEM");
        treeRoot.setType("PATH");
        treeRoot.setValue("POPEDOM:SYS_SUBSYSTEM");
        treeRoot.removeAllChildren();
        TParm parm = new TParm();
        parm.setData("ROLE_CODE",parentID);
        TParm result = TIOM_AppServer.executeAction("action.sys.SYSRolePopedomAction","queryRoleTree",parm);
        downloadRootTree(treeRoot,result,"SYS_SUBSYSTEM");
        callMessage("UI|" + ROLE_TREE + "|update");
        out("end");
    }
    /**
     * 下载树数据
     * @param parentNode TTreeNode
     * @param parm TParm
     * @param group String
     */
    public void downloadRootTree(TTreeNode parentNode,TParm parm,String group)
    {
        if(parentNode == null)
            return;
        int count = parm.getCount();
        for(int i = 0;i < count;i++)
        {
            String id = parm.getValue("ID",i);
            String name = parm.getValue("NAME",i);
            String type = parm.getValue("TYPE",i);
            String data = parm.getValue("DATA",i);
            String value = parm.getValue("VALUE",i);
            TParm child = parm.getParm("CHILD",i);
            TTreeNode node = new TTreeNode(name,type);
            node.setGroup(group);
            node.setID(id);
            node.setValue(group + ":" + id);
            node.setData(data);
            node.setShowType("checkbox");
            node.setValue(value);
                //SYSRolePopedomTool.getInstance().getPopedom(parentID,group,id)?"Y":"N");
            parentNode.add(node);
            downloadRootTree(node,child,id);
        }
    }
    /**
     * 得到字典工具类
     * @return DictionaryTool
     */
    public DictionaryTool getDicTool()
    {
        return DictionaryTool.getInstance();
    }
    public void onInitReset()
    {
        initRoot();
    }
    public void onTreeClicked(Object parm)
    {
        TTreeNode node = (TTreeNode) parm;
        if (node == null)
            return;
        String group = node.getGroup();
        String id = node.getID();
        if(TCM_Transform.getBoolean(node.getValue()))
            addPopedom(group,id);
        else
            removePopedom(group,id);
    }
    public void addPopedom(String group,String id)
    {
        deleteData(deleteData,group,id);
        if(SYSRolePopedomTool.getInstance().getPopedom(parentID,group,id))
            return;
        if(existData(insertData,group,id))
            return;
        insertData.addData("ROLE_CODE",parentID);
        insertData.addData("GROUP_CODE",group);
        insertData.addData("CODE",id);
        insertData.addData("OPT_USER",Operator.getID());
        insertData.addData("OPT_TERM",Operator.getIP());
        insertData.setData("ACTION","COUNT",insertData.getCount("ROLE_CODE"));
    }
    public void removePopedom(String group,String id)
    {
        deleteData(insertData,group,id);
        if(!SYSRolePopedomTool.getInstance().getPopedom(parentID,group,id))
            return;
        if(existData(deleteData,group,id))
            return;
        deleteData.addData("ROLE_CODE",parentID);
        deleteData.addData("GROUP_CODE",group);
        deleteData.addData("CODE",id);
        deleteData.setData("ACTION","COUNT",deleteData.getCount("ROLE_CODE"));
    }
    /**
     * 存在要更新的数据
     * @param parm TParm
     * @param group String
     * @param id String
     * @return boolean
     */
    public boolean existData(TParm parm,String group,String id)
    {
        if(parm == null)
            return false;
        int count = parm.getCount();
        for(int i = 0;i< count;i ++)
            if(group.equals(parm.getValue("GROUP_CODE",i)) && id.equals(parm.getValue("CODE",i)))
                return true;
        return false;
    }
    /**
     * 删除数据
     * @param parm TParm
     * @param group String
     * @param id String
     */
    public void deleteData(TParm parm,String group,String id)
    {
        if(parm == null)
            return;
        int count = parm.getCount();
        for(int i = 0;i< count;i ++)
            if(group.equals(parm.getValue("GROUP_CODE",i)) && id.equals(parm.getValue("CODE",i)))
            {
                parm.removeRow(i);
                return;
            }
    }
    /**
     * 保存
     */
    public void onSave()
    {
        TParm parm = new TParm();
        parm.setData("INSERT",insertData.getData());
        parm.setData("DELETE",deleteData.getData());
        TParm result = TIOM_AppServer.executeAction("action.sys.SYSRolePopedomAction","onSave",parm);
        if(result.getErrCode() < 0)
        {
            err(result.getErrCode() + result.getErrText());
            messageBox("E0001");
            return;
        }
        insertData = new TParm();
        deleteData = new TParm();
        messageBox("P0001");
    }
}
