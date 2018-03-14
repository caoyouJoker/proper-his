package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTree;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TCheckBox;

/**
 *
 * <p>Title: 打开文件对话框</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2004,14,25
 * @version 1.0
 */
public class OpenDialogControl extends TControl{

    private TTree tree;
    private TTextField text;
    private String dir;
    private TCheckBox edit;
    /**
     * 初始化
     */
    public void onInit()
    {
        tree = (TTree)getComponent("TREE");
        text = (TTextField)getComponent("FILENAME");
        edit = (TCheckBox)getComponent("CB_EDIT");
        //初始化树
        initTree();
        addEventListener("TREE->" + TTreeEvent.CLICKED,"onTreeClicked");
        addEventListener("TREE->" + TTreeEvent.DOUBLE_CLICKED,"onTreeDoubleClicked");
    }
    /**
     * 初始化树
     */
    public void initTree()
    {
        //得到树根
        TTreeNode root = tree.getRoot();
        root.setText("JavaHis");
        root.setType("Root");
        root.removeAllChildren();
        root.setValue("%ROOT%\\config\\prt");
        downloadProjectTree(root,"%ROOT%\\config\\prt");
        tree.setSelectNode(root);
        tree.update();
    }
    /**
     * 确定
     */
    public void onOK()
    {
        if(text.getText().trim().length() == 0)
        {
            messageBox_("请选择文件名!");
            text.grabFocus();
            return;
        }
        setReturnValue(new Object[]{dir,text.getValue(),edit.isSelected()});
        closeWindow();
    }
    /**
     * 单击树
     * @param parm Object
     */
    public void onTreeClicked(Object parm)
    {
        TTreeNode node = (TTreeNode)parm;
        if(node == null)
            return;
        if(node.getType().equals("UI"))
        {
            text.setValue(node.getText());
            dir = node.getID().substring(0,node.getID().length() - node.getText().length() - 1);
            return;
        }
        text.setValue("");
        dir = node.getID();
    }
    /**
     * 双击树
     * @param parm Object
     */
    public void onTreeDoubleClicked(Object parm)
    {
        onOK();
    }
    /**
    * 装载工程树
    * @param root TTreeNode
    * @param dir String
    */
   public void downloadProjectTree(TTreeNode root,String dir)
   {
       if(root == null)
           return;
       String dirs[] = TIOM_AppServer.listDir(dir);
       for(int i = 0;i < dirs.length;i ++)
       {
           TTreeNode node = new TTreeNode(dirs[i],"Path");
           node.setID(dir + "\\" + dirs[i]);
           downloadProjectTree(node,dir + "\\" + dirs[i]);
           root.add(node);
       }
       String files[] = TIOM_AppServer.listFile(dir);
       for(int i = 0;i < files.length;i ++)
       {
           String type = "UI";
           if(!files[i].endsWith(".jhw"))
               continue;
           TTreeNode node = new TTreeNode(files[i],type);
           node.setID(dir + "\\" + files[i]);
           root.add(node);
       }
   }
   /**
     * 取消
     */
    public void onCancel()
    {
        closeWindow();
    }
    public static void main(String args[])
    {
        Object value[] = (Object[])com.javahis.util.JavaHisDebug.runDialog("database\\OpenDialog.x");
        if(value == null)
            return;
        for(int i = 0;i < value.length;i++)
            System.out.println(value[i]);
    }
}
