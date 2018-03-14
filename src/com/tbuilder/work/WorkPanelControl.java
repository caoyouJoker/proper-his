package com.tbuilder.work;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.config.TConfigParse;
import com.dongyang.ui.TTreeNode;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.base.JTreeBase;
import javax.swing.tree.TreePath;
import com.dongyang.ui.edit.TUIEditView;
import java.util.ArrayList;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import java.util.Vector;
import com.dongyang.util.MemoryMonitor;
import com.dongyang.util.TSystem;

public class WorkPanelControl extends TControl{
    public static final String INPUT_PARM_DIALOG_CONFIG="InputParmDialogConfig";
    /**
     * 结构树Tag
     */
    public static final String STRUCTURE_TREE = "StructureTree";
    /**
     * 选择树Tag
     */
    public static final String SELECT_TREE = "TComponentSelection";
    /**
     * 属性Table
     */
    public static final String UI_PROPERTY = "UIProperty";
    /**
     * 文件名
     */
    private String filename;
    /**
     * 名称
     */
    private String name;
    /**
     * 数据
     */
    private String data;
    /**
     * 数据解析对象
     */
    private TConfigParse configParse;
    /**
     * 结构树树根
     */
    private TTreeNode structureNodeRoot;
    /**
     * 选中树树根
     */
    private TTreeNode selectNodeRoot;
    /**
     * 阅览参数
     */
    private String previewParm;
    /**
     * 初始化
     */
    public void onInit()
    {
        super.onInit();
        TParm parm = (TParm)getParameter();
        //得到名称
        name = parm.getValue("NAME");
        //得到文件名
        filename = parm.getValue("FILE_NAME");
        //设置名称
        callMessage("UI|setTitle|" + name);
        //读取文件数据
        data = new String(TIOM_AppServer.readFile(filename));
        //解析文件
        setConfigParse(new TConfigParse(data));
        //设置选中监听
        getView().addEventListener("Selected",this,"onSelected");
        //设置取消选中监听
        getView().addEventListener("undoSelected",this,"onUndoSelected");
        //设置新增组件的监听
        getView().addEventListener("createTComponent",this,"onCreateTComponent");
        //设置删除组件的监听
        getView().addEventListener("deleteTComponent",this,"onDeleteTComponent");
        //组件修改Tag
        getView().addEventListener("modifiedTag",this,"onModifiedTag");
        //初始化结构树
        onInitStructureTree();
        //初始化选中树
        onInitSelectTree();
        //单击选中树项目
        addEventListener(SELECT_TREE + "->" + TTreeEvent.CLICKED,"onSelectTreeClicked");
        //属性Table改变值
        addEventListener(UI_PROPERTY + "->" + TTableEvent.CHANGE_VALUE,"onPropertyTableChangeValue");
        //属性Table双击
        callFunction("UI|" + UI_PROPERTY + "|addEventListener",UI_PROPERTY + "->" + TTableEvent.DOUBLE_CLICKED,this,"onPropertyTableDoubleClicked");
        SwingUtilities.invokeLater(new Runnable(){
            public void run()
            {
                callFunction("UI|UIDesign|updateUI",getConfigParse().getRoot());
            }
        });
    }
    /**
     * 得到编辑视图
     * @return TUIEditView
     */
    public TUIEditView getView()
    {
        if(getConfigParse() == null)
            return null;
        return getConfigParse().getView();
    }
    /**
     * 设置解析对象
     * @param configParse TConfigParse
     */
    public void setConfigParse(TConfigParse configParse)
    {
        this.configParse = configParse;
    }
    /**
     * 得到解析对象
     * @return TConfigParse
     */
    public TConfigParse getConfigParse()
    {
        return configParse;
    }
    /**
     * 初始化选中树
     */
    public void onInitSelectTree()
    {
        out("begin");
        selectNodeRoot = (TTreeNode)callMessage("UI|" + SELECT_TREE + "|getRoot");
        if(selectNodeRoot == null)
            return;
        selectNodeRoot.setText("TComponent List");
        selectNodeRoot.setType("Path");
        selectNodeRoot.removeAllChildren();
        TTreeNode noneNode = new TTreeNode("<none>","TComponent");
        selectNodeRoot.add(noneNode);
        TTreeNode baseNode = new TTreeNode("TComponent","Path");
        selectNodeRoot.add(baseNode);
        String items[] = StringTool.parseLine(getConfigString("TComponentList"), ';', "[]{}()");
        for(int i = 0;i < items.length;i++)
            baseNode.add(new TTreeNode(items[i], "TComponent"));
        TTreeNode comboNode = new TTreeNode("TCombo","Path");
        TTreeNode textFormatNode = new TTreeNode("TTextFormat","Path");
        selectNodeRoot.add(comboNode);
        selectNodeRoot.add(textFormatNode);
        loadComboNode(comboNode);
        loadTextFormatNode(textFormatNode);
        callMessage("UI|" + SELECT_TREE + "|update");
        out("end");
    }
    public void loadComboNode(TTreeNode parent)
    {
        String item = getConfigString("TComboList");
        loadComboNode(parent,"TComboList",item);
    }
    public void loadComboNode(TTreeNode parent,String parentItem,String item)
    {
        String items[] = StringTool.parseLine(item, ';', "[]{}()");
        for(int i = 0;i < items.length;i++)
        {
            TTreeNode node = new TTreeNode(items[i], "TComponent");
            String ls = getConfigString(parentItem + "." + items[i]);
            if(ls.length() > 0)
                loadComboNode(node,parentItem + "." + items[i],ls);
            parent.add(node);
        }
    }
    public void loadTextFormatNode(TTreeNode parent)
    {
        String item = getConfigString("TTextFormatList");
        loadComboNode(parent,"TTextFormatList",item);
    }
    /**
     * 初始化结构树
     */
    public void onInitStructureTree()
    {
        out("begin");
        structureNodeRoot = (TTreeNode)callMessage("UI|" + STRUCTURE_TREE + "|getRoot");
        if(structureNodeRoot == null)
            return;
        if(getConfigParse() == null)
            return;
        structureNodeRoot.setText(name);
        structureNodeRoot.setType("<Root>");
        structureNodeRoot.removeAllChildren();
        downloadStructureTree(structureNodeRoot,getConfigParse().getRoot());
        callMessage("UI|" + STRUCTURE_TREE + "|update");
        out("end");
    }
    /**
     * 装载结构树
     * @param root TTreeNode
     * @param object TObject
     */
    private void downloadStructureTree(TTreeNode root,TObject object)
    {
        if(root == null)
            return;
        if(object == null)
            return;
        String text = object.getType() + " [" + object.getTag() + "]";
        if(object.getPosition() != null)
            text += " " + object.getPosition();
        //创建节点
        TTreeNode node = new TTreeNode(text,object.getType());
        //设置数据对象
        node.setData(object);
        //将树节点对象保存到数据对象
        object.setData(node);
        int count = object.getItemCount();
        for(int i = 0;i < count;i++)
            downloadStructureTree(node,object.getItem(i));
        root.add(node);
    }
    /**
     * 顶对齐
     */
    public void onAlignUP()
    {
        getView().onAlignUP();
    }
    /**
     * 底对齐
     */
    public void onAlignDown()
    {
        getView().onAlignDown();
    }
    /**
     * 左对齐
     */
    public void onAlignLeft()
    {
        getView().onAlignLeft();
    }
    /**
     * 右对齐
     */
    public void onAlignRight()
    {
        getView().onAlignRight();
    }
    /**
     * 水平对齐
     */
    public void onAlignWCenter()
    {
        getView().onAlignWCenter();
    }
    /**
     * 垂直对齐
     */
    public void onAlignHCenter()
    {
        getView().onAlignHCenter();
    }
    /**
     * 同宽
     */
    public void onAlignWidth()
    {
        getView().onAlignWidth();
    }
    /**
     * 同高
     */
    public void onAlignHeight()
    {
        getView().onAlignHeight();
    }
    /**
     * 横向同间隔
     */
    public void onAlignWSpace()
    {
        getView().onAlignWSpace();
    }
    /**
     * 纵向同间隔
     */
    public void onAlignHSpace()
    {
        getView().onAlignHSpace();
    }
    /**
     * 单击结构树项目
     */
    public void onStructureTreeClicked()
    {
        JTreeBase tree = (JTreeBase)callMessage("UI|" + STRUCTURE_TREE + "|getTree");
        if(tree == null)
            return;
        TreePath treePath[] = tree.getSelectionPaths();
        if(treePath == null)
            return;
        ArrayList list = new ArrayList();
        for(int i = 0;i < treePath.length;i ++)
        {
            TTreeNode node = (TTreeNode) treePath[i].getLastPathComponent();
            if (node == null)
                continue;
            if ("<Root>".equals(node.getType()))
                continue;
            TObject object = (TObject) node.getData();
            if (object == null)
                return;
            list.add(object);
        }
        TObject selectObject[] = (TObject[])list.toArray(new TObject[]{});
        getView().setSelected(selectObject);
    }
    /**
     * 选中组件
     * @param tobject TObject
     */
    public void onSelected(TObject tobject)
    {
        //同步结构树选中
        synchronizationStructureNodeSelected();
        //同步属性区
        synchronizationProperty(tobject);
    }
    /**
     * 取消选中组件
     * @param tobject TObject
     */
    public void onUndoSelected(TObject tobject)
    {
        //同步结构树选中
        synchronizationStructureNodeSelected();
    }
    /**
     * 同步属性区
     * @param tobject TObject
     */
    public void synchronizationProperty(TObject tobject)
    {
        if(tobject == null)
            return;
        TAttributeList attributeList = tobject.getAttributeList();
        if(attributeList == null)
            return;
        callFunction("UI|" + UI_PROPERTY + "|acceptText");
        String value = attributeList.getValue();
        String editType = attributeList.getEditType();
        String alignment = attributeList.getEditAlignment();
        callFunction("UI|" + UI_PROPERTY + "|value=" + value);
        callFunction("UI|" + UI_PROPERTY + "|RowColumnTypeData=" + editType);
        callFunction("UI|" + UI_PROPERTY + "|HorizontalAlignmentData=" + alignment);
        callFunction("UI|" + UI_PROPERTY + "|setData",tobject);
        //焦点转移到键盘监听控件上
        callFunction("UI|UIDesign|onKeyGrabFocus");
    }
    /**
     * 同步结构树选中
     */
    public void synchronizationStructureNodeSelected()
    {
        if(structureNodeRoot == null)
            return;
        int count = getView().getSelectedCount();
        TTreeNode node[] = new TTreeNode[count];
        for(int i = 0;i < count;i++)
            node[i] = (TTreeNode)getView().getSelected(i).getData();
        JTreeBase tree = (JTreeBase)callMessage("UI|" + STRUCTURE_TREE + "|getTree");
        if(tree == null)
            return;
        tree.setSelectionNode(node);
    }
    /**
     * 单击选中数
     * @param parm Object
     */
    public void onSelectTreeClicked(Object parm)
    {
        TTreeNode node = (TTreeNode)parm;
        if(node == null)
            return;
        if("Path".equals(node.getType()))
            return;
        String name = node.getText();
        if("<none>".equals(name))
            name = null;
        getView().setCreateTComponentName(name);
    }
    /**
     * 新增组件事件
     * @param parent TObject
     * @param newObj TObject
     */
    public void onCreateTComponent(TObject parent,TObject newObj)
    {
        out("begin");
        if(parent == null||newObj == null)
            return;
        if (structureNodeRoot == null)
            return;
        TTreeNode node = (TTreeNode)parent.getData();
        if(node == null)
            return;
        String text = newObj.getType() + " [" + newObj.getTag() + "]";
        if(newObj.getPosition() != null)
            text += " " + newObj.getPosition();
        //创建节点
        TTreeNode newNode = new TTreeNode(text,newObj.getTag());
        //设置数据对象
        newNode.setData(newObj);
        //将树节点对象保存到数据对象
        newObj.setData(newNode);
        //添加新节点
        node.add(newNode);
        //刷新结构树
        callMessage("UI|" + STRUCTURE_TREE + "|update");

        //取消新建状态
        node = selectNodeRoot.findNodeForText("<none>");
        if(node == null)
            return;
        JTreeBase tree = (JTreeBase)callMessage("UI|" + SELECT_TREE + "|getTree");
        if(tree == null)
            return;
        tree.setSelectionNode(new TTreeNode[]{node});
        out("end");
    }
    /**
     * 删除组件事件
     * @param obj TObject
     */
    public void onDeleteTComponent(TObject obj)
    {
        out("begin");
        if(obj == null || structureNodeRoot == null)
            return;
        TTreeNode node = (TTreeNode)obj.getData();
        if(node == null)
            return;
        TTreeNode parent = (TTreeNode)node.getParent();
        parent.remove(node);
        callMessage("UI|" + STRUCTURE_TREE + "|update");
        callFunction("UI|" + UI_PROPERTY + "|setValue",new Vector());
        //焦点转移到键盘监听控件上
        callFunction("UI|UIDesign|onKeyGrabFocus");

        out("end");
    }
    /**
     * 组件修改Tag事件
     * @param obj TObject
     */
    public void onModifiedTag(TObject obj)
    {
        out("begin");
        if(obj == null || structureNodeRoot == null)
            return;
        TTreeNode node = (TTreeNode)obj.getData();
        String text = obj.getType() + " [" + obj.getTag() + "]";
        node.setText(text);
        callMessage("UI|" + STRUCTURE_TREE + "|update");
    }
    /**
     * 属性Table改变值
     * @param obj Object
     */
    public void onPropertyTableChangeValue(Object obj)
    {
        TTableNode node = (TTableNode)obj;
        if(node == null)
            return;
        TObject object = (TObject)node.getTable().getData();
        if(object == null)
            return;
        String name = (String)node.getTable().getValueAt(node.getRow(),0);
        String value = (String)node.getValue();
        TComponent component = object.getComponent();
        if(component == null)
            return;
        component.callFunction("setAttribute",name,value);
    }
    /**
     * 属性Table双击
     * @param row int
     */
    public void onPropertyTableDoubleClicked(int row)
    {
        if(row < 0)
            return;
        TObject tobject = (TObject)callFunction("UI|" + UI_PROPERTY + "|getData");
        if(tobject == null)
            return;
        String name = (String)callFunction("UI|" + UI_PROPERTY + "|getValueAt",row,0);
        String value = (String)callFunction("UI|" + UI_PROPERTY + "|getValueAt",row,1);
        //得到属性列表
        TAttributeList attributeList = tobject.getAttributeList();
        if(attributeList == null)
            return;
        //得到属性对象
        TAttributeList.TAttribute attribute = attributeList.get(name);
        if(attribute == null)
            return;
        if(attribute.getEditDialog() == null || attribute.getEditDialog().length() == 0)
            return;
        TParm parm = StringTool.getParm(attribute.getEditDialog());
        parm.setData("TITLE",tobject.getTag() + "." +  parm.getValue("TITLE"));
        parm.setData("VALUE",value);
        String fileName = getConfigString("editDialog." + parm.getData("TYPE"));
        if(fileName == null || fileName.length() == 0)
            return;
        String outValue = (String)openDialog(fileName,parm);
        if(outValue == null)
            return;
        outValue = StringTool.replaceAll(outValue,"\n"," ");
        callFunction("UI|" + UI_PROPERTY + "|setValueAt",outValue,row,1);
        TComponent component = tobject.getComponent();
        if(component == null)
            return;
        component.callFunction("setAttribute",name,outValue);
    }
    /**
     * 删除
     */
    public void onDelete()
    {
        getView().onDelete();
    }
    /**
     * 保存
     */
    public void onSave()
    {
        callFunction("UI|" + UI_PROPERTY + "|acceptText");
        String newData = getConfigParse().save();
        if (data.equals(newData))
            return;
        TIOM_AppServer.writeFile(filename,newData.getBytes());
        data = newData;
    }
    /**
     * 阅览
     */
    public void onPreview()
    {
        String type = getConfigParse().getRoot().getType();
        if("TFrame".equalsIgnoreCase(type))
        {
            openWindow(filename,previewParm);
            return;
        }
    }
    public void onMEMAction()
    {
        MemoryMonitor.run();
    }
    public static void p()
    {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long userMemory = totalMemory - freeMemory;
        //System.out.println("availableProcessors=" + Runtime.getRuntime().availableProcessors());
        //System.out.println("totalMemory=" + totalMemory);
        //System.out.println("maxMemory=" + Runtime.getRuntime().maxMemory());
        //System.out.println("freeMemory=" + freeMemory);
        System.out.println("userMemory=" + userMemory);
    }
    /**
     * 传入参数对话框
     */
    public void onInputParm()
    {
        String value = (String)openDialog(getConfigString(INPUT_PARM_DIALOG_CONFIG),previewParm);
        if(value == null)
            return;
        if(value.length() == 0)
            previewParm = null;
        else
            previewParm = value;
    }
    /**
     * 选择语种
     */
    public void onChangeLanguage()
    {
        String language = (String)getValue("LanguageComboTool");
        TSystem.setObject("Language",language);
        callFunction("UI|UIDesign|onChangeLanguage",language);
    }
}
