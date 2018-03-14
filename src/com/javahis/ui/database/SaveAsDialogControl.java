package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.config.TRegistry;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TCheckBox;

/**
 *
 * <p>Title: 另存为对话框控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.4.14
 * @version 1.0
 */
public class SaveAsDialogControl extends TControl{
    private TTree tree;
    private TTextField fileName;
    private TTextField author;
    private TTextField co;
    private TTextField date;
    private TTextArea remark;
    private TCheckBox debug;
    /**
     * 初始化
     */
    public void onInit()
    {
        tree = (TTree)getComponent("TREE");
        fileName = (TTextField)getComponent("FILENAME");
        author = (TTextField)getComponent("AUTHOR");
        co = (TTextField)getComponent("CO");
        date = (TTextField)getComponent("DATE");
        remark = (TTextArea)getComponent("REMARK");
        debug = (TCheckBox)getComponent("DEBUG");

        //初始化树
        initTree();
        date.setText(StringTool.getString(TJDODBTool.getInstance().getDBTime(),"yyyy.MM.dd HH:mm:ss") + " create");
        author.setText(TRegistry.get("HKEY_CURRENT_USER\\Software\\JavaHis\\TBuilder\\AUTHOR"));
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
            node.setValue(dir + "\\" + dirs[i]);
            downloadProjectTree(node,dir + "\\" + dirs[i]);
            root.add(node);
        }
    }
    /**
     * 确定
     */
    public void onOK()
    {
        if(fileName.getText().trim().length() == 0)
        {
            messageBox_("请输入文件名!");
            fileName.grabFocus();
            return;
        }
        if(author.getText().trim().length() == 0)
        {
            messageBox_("请输入作者!");
            author.grabFocus();
            return;
        }
        TRegistry.set("HKEY_CURRENT_USER\\Software\\JavaHis\\TBuilder\\AUTHOR",author.getText());

        String dir = tree.getSelectNode().getValue();
        setReturnValue(new Object[]{dir,
                       fileName.getText(),
                       author.getText(),
                       co.getText(),
                       date.getText(),
                       remark.getText(),
                       debug.isSelected()});
        closeWindow();
    }
    /**
     * 取消
     */
    public void onCancel()
    {
        closeWindow();
    }
    /**
     * 双击备注
     */
    public void onDoubleClickedRemark()
    {
    }
    public static void main(String args[])
    {
        Object value[] = (Object[])com.javahis.util.JavaHisDebug.runDialog("database\\SaveAsDialog.x");
        if(value == null)
            return;
        for(int i = 0;i < value.length;i++)
            System.out.println(value[i]);
    }
}
