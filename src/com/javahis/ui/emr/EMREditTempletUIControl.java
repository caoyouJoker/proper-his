package com.javahis.ui.emr;

import com.dongyang.control.*;
import com.dongyang.ui.TMovePane;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TMouseListener;
import com.dongyang.util.StringTool;
import java.util.Date;
import jdo.sys.Operator;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_FileServer;
import java.awt.event.MouseEvent;
import javax.swing.tree.TreePath;
import com.dongyang.data.TSocket;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TFontCombo;
import com.dongyang.ui.TFontSizeCombo;
import com.dongyang.ui.TToolButton;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.EMacroroutine;
import com.dongyang.tui.text.IBlock;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.ETable;
import com.dongyang.tui.text.EImage;
import jdo.emr.EMRCreateXMLTool;

import com.dongyang.tui.text.ETextFormat;
import com.dongyang.tui.text.EInputText;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.tui.text.EMultiChoose;
import com.dongyang.tui.text.EHasChoose;
import com.dongyang.tui.text.EMicroField;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.ENumberChoose;
import com.dongyang.util.ImageTool;
import com.dongyang.ui.TWindow;
import com.dongyang.tui.text.CopyOperator;

/**
 * <p>Title: 结构化病历模板制作==》模板编辑/打开界面</p>
 *
 * <p>Description: 结构化病历模板制作==》模板编辑/打开界面</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EMREditTempletUIControl
    extends TControl {
    /**
     * 树名称
     */
    private static final String TREE = "TREE";
    /**
     * 拿到TWORD对象
     */
    private static final String WORD = "WORD";
    /**
     * 表格面板对象
     */
    private static final String TABLEPANEL = "PANEL2";
    /**
     * 树根
     */
    private TTreeNode treeRoot;
    /**
     * WORD对象
     */
    private TWord word;
    /**
     * 当前模版状态
     */
    private String onlyType;
    /**
     * 要保存的模版信息
     */
    private TParm saveParm;
    /**
     * 当前编辑文件
     */
    private String onlyEditfileName = "";
    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        //初始化树
        this.loadTree();
        //初始化WORD
        initWord();
        //注册树事件
        initEven();

    }

    /**
     * 初始化WORD
     */
    public void initWord() {
        word = this.getTWord(WORD);
        //缩放
        word.setShowZoomComboTag("ShowZoom");
        //字体
        word.setFontComboTag("ModifyFontCombo");
        //字体
        word.setFontSizeComboTag("ModifyFontSizeCombo");
        //变粗
        word.setFontBoldButtonTag("FontBMenu");
        //斜体
        word.setFontItalicButtonTag("FontIMenu");
        //取消编辑
        this.word.setCanEdit(false);
        //初始化菜单
        initMenu();
    }

    /**
     * 初始化菜单
     */
    public void initMenu() {
        if (!this.word.canEdit()) {
            this.getTFontCombo("ModifyFontCombo").setEnabled(false);
            this.getTFontSizeCombo("ModifyFontSizeCombo").setEnabled(false);
            this.getTToolButton("AlignmentLeft").setEnabled(false);
            this.getTToolButton("AlignmentCenter").setEnabled(false);
            this.getTToolButton("AlignmentRight").setEnabled(false);
            this.getTMenuItem("insertFixText").setEnabled(false);
            this.getTMenuItem("FixTextProperty").setEnabled(false);
            this.getTMenuItem("InsertSingleChoose").setEnabled(false);
            this.getTMenuItem("InsertRelateChoose").setEnabled(false);
            this.getTMenuItem("InsertMultiChoose").setEnabled(false);
            this.getTMenuItem("InsertHasChoose").setEnabled(false);
            this.getTMenuItem("InsertMicroField").setEnabled(false);
            this.getTMenuItem("MicroFieldProperty").setEnabled(false);
            this.getTMenuItem("InsertTable").setEnabled(false);
            this.getTMenuItem("DelTable").setEnabled(false);
            this.getTMenuItem("InsertTableRow").setEnabled(false);
            this.getTMenuItem("AddTableRow").setEnabled(false);
            this.getTMenuItem("DelTableRow").setEnabled(false);
            this.getTMenuItem("delFixText").setEnabled(false);
            this.getTMenuItem("addDLText").setEnabled(false);
            this.getTMenuItem("TableProperty").setEnabled(false);
            this.getTMenuItem("addInputMessage").setEnabled(false);
            this.getTMenuItem("InsertCaptureObject").setEnabled(false);
            this.getTMenuItem("CaptureDataProperty").setEnabled(false);
            this.getTMenuItem("InsertCheckBoxChooseObject").setEnabled(false);
            this.getTMenuItem("CustomScriptDialog").setEnabled(false);
            this.getTMenuItem("InsertPictureObject").setEnabled(false);
            this.getTMenuItem("InsertNumberChooseObject").setEnabled(false);
            this.getTMenuItem("CutMenu").setEnabled(false);
            this.getTMenuItem("CopyMenu").setEnabled(false);
            this.getTMenuItem("PasteMenu").setEnabled(false);
            this.getTMenuItem("DeleteMenu").setEnabled(false);
            this.getTMenuItem("DIVProperty").setEnabled(false);
            this.getTMenuItem("insertElementData").setEnabled(false);
            this.getTMenuItem("insertHideElement").setEnabled(false);

        }
        else {
            this.getTFontCombo("ModifyFontCombo").setEnabled(true);
            this.getTFontSizeCombo("ModifyFontSizeCombo").setEnabled(true);
            this.getTToolButton("AlignmentLeft").setEnabled(true);
            this.getTToolButton("AlignmentCenter").setEnabled(true);
            this.getTToolButton("AlignmentRight").setEnabled(true);
            this.getTMenuItem("insertFixText").setEnabled(true);
            this.getTMenuItem("FixTextProperty").setEnabled(true);
            this.getTMenuItem("InsertSingleChoose").setEnabled(true);
            this.getTMenuItem("InsertRelateChoose").setEnabled(true);
            this.getTMenuItem("InsertMultiChoose").setEnabled(true);
            this.getTMenuItem("InsertHasChoose").setEnabled(true);
            this.getTMenuItem("InsertMicroField").setEnabled(true);
            this.getTMenuItem("MicroFieldProperty").setEnabled(true);
            this.getTMenuItem("InsertTable").setEnabled(true);
            this.getTMenuItem("DelTable").setEnabled(true);
            this.getTMenuItem("InsertTableRow").setEnabled(true);
            this.getTMenuItem("AddTableRow").setEnabled(true);
            this.getTMenuItem("DelTableRow").setEnabled(true);
            this.getTMenuItem("delFixText").setEnabled(true);
            this.getTMenuItem("addDLText").setEnabled(true);
            this.getTMenuItem("TableProperty").setEnabled(true);
            this.getTMenuItem("addInputMessage").setEnabled(true);
            this.getTMenuItem("InsertCaptureObject").setEnabled(true);
            this.getTMenuItem("CaptureDataProperty").setEnabled(true);
            this.getTMenuItem("InsertCheckBoxChooseObject").setEnabled(true);
            this.getTMenuItem("CustomScriptDialog").setEnabled(true);
            this.getTMenuItem("InsertPictureObject").setEnabled(true);
            this.getTMenuItem("InsertNumberChooseObject").setEnabled(true);
            this.getTMenuItem("CutMenu").setEnabled(true);
            this.getTMenuItem("CopyMenu").setEnabled(true);
            this.getTMenuItem("PasteMenu").setEnabled(true);
            this.getTMenuItem("DeleteMenu").setEnabled(true);
            this.getTMenuItem("DIVProperty").setEnabled(true);
            this.getTMenuItem("insertElementData").setEnabled(true);
            this.getTMenuItem("insertHideElement").setEnabled(true);
        }
    }

    /**
     * 拿到菜单
     * @param tag String
     * @return TMenuItem
     */
    public TMenuItem getTMenuItem(String tag) {
        return (TMenuItem)this.getComponent(tag);
    }

    /**
     * 拿到TToolButton
     * @param tag String
     * @return TToolButton
     */
    public TToolButton getTToolButton(String tag) {
        return (TToolButton)this.getComponent(tag);
    }

    /**
     * 拿到字体Combo
     * @param tag String
     * @return TFontCombo
     */
    public TFontCombo getTFontCombo(String tag) {
        return (TFontCombo)this.getComponent(tag);
    }

    /**
     * 拿到字体大小Combo
     * @param tag String
     * @return TFontSizeCombo
     */
    public TFontSizeCombo getTFontSizeCombo(String tag) {
        return (TFontSizeCombo)this.getComponent(tag);
    }

    /**
     * 得到WORD对象
     * @param tag String
     * @return TWord
     */
    public TWord getTWord(String tag) {
        return (TWord)this.getComponent(tag);
    }

    /**
     * 注册事件
     */
    public void initEven() {
        //单击选中树项目
        addEventListener(TREE + "->" + TTreeEvent.DOUBLE_CLICKED,
                         "onTreeDoubleClicked");
        //鼠标右键
        this.callFunction("UI|" + TREE + "|addEventListener",
                          TREE + "->" + TMouseListener.MOUSE_RIGHT_CLICKED, this,
                          "mouseRightPressed");
    }

    /**
     * 单击选中树事件
     * @param obj Object
     */
    public void onTreeDoubleClicked(Object obj) {
        if (obj == null) {
            return;
        }
        TTreeNode node = this.getTTree(TREE).getSelectNode();
        //如果是模版才可打开;
        if (node.getType().equals("4")) {
            onOpenTemplet();
        }
    }

    /**
     * 树右键事件
     * @param e MouseEvent
     */
    public void mouseRightPressed(MouseEvent e) {
        //得到树路径
        TreePath path = this.getTTree(TREE).getTree().getPathForLocation(e.getX(),
            e.getY());
        //得到树节点
        TTreeNode node = (TTreeNode) path.getLastPathComponent();
    }

    /**
     * 初始化树
     */
    public void loadTree() {
        treeRoot = this.getTTree(TREE).getRoot();
        //取根节点;
        TParm rootParm = this.getTreeRoot();

        treeRoot.setText(rootParm.getValue("CLASS_DESC", 0));
        treeRoot.setType("Root");
        treeRoot.setID(rootParm.getValue("CLASS_CODE", 0));
        //初始化界面前清理掉所有叶子节点
        treeRoot.removeAllChildren();
        //得到EMR分级后的数据集合
        TParm mainRootParm = this.getMainRootTree();

        if (mainRootParm == null) {
            return;
        }
        int rowCount = mainRootParm.getCount();
        //this.messageBox("dd");
        for (int i = 0; i < rowCount; i++) {
            // add by lx start
            TParm temp = mainRootParm.getRow(i);
            //根据节点类型设置是否为目录节点
            String noteType = "Root";
            //叶节点
            if (mainRootParm.getValue("LEAF_FLG", i).equals("Y")) {
                noteType = "1";
            }

            //建立新节点
            TTreeNode EMRClass = new TTreeNode("EMRCLASS" + i, noteType);
            //将ERM分类信息设置到节点当中
            EMRClass.setText(mainRootParm.getValue("CLASS_DESC", i));
            EMRClass.setID(mainRootParm.getValue("CLASS_CODE", i));
            EMRClass.setGroup(mainRootParm.getValue("CLASS_STYLE", i));
            // TParm leafParm=new TParm();
            //EMRClass.setUserObject(new VerifyError());


            //第一级的节点放入根结点
            if (mainRootParm.getValue("PARENT_CLASS_CODE", i).equals("ROOT")) {
                //this.messageBox("EMRClass name"+EMRClass.getText());
                treeRoot.addSeq(EMRClass);
            }
            //其他级别的节点放入相应的父节点下面
            else {
                if (mainRootParm.getValue("PARENT_CLASS_CODE", i).length() != 0) {
                    //this.messageBox("EMRClass name11111"+EMRClass.getText());
                	//lx
                	TTreeNode treeNode=treeRoot.findNodeForID(mainRootParm.getValue(
                            "PARENT_CLASS_CODE", i));
                	if(treeNode!=null){
                    treeRoot.findNodeForID(mainRootParm.getValue(
                        "PARENT_CLASS_CODE", i)).add(EMRClass);
                	}
                }
            }
            //判断是否为叶节点，是则加入对应的模版
            if (mainRootParm.getValue("LEAF_FLG", i).equals("Y")) {

                //便利下面的树节点
                TParm childrenTreeParm = this.getChildTreeType(temp.getValue(
                    "CLASS_CODE"));
                int childRowCount = childrenTreeParm.getCount();
                for (int j = 0; j < childRowCount; j++) {
                    TParm childrenTemp = childrenTreeParm.getRow(j);
                    TTreeNode childrenNode = new TTreeNode();
                    childrenNode.setID(childrenTemp.getValue("SUBCLASS_CODE"));
                    childrenNode.setText(childrenTemp.getValue("SUBCLASS_DESC"));
                    childrenNode.setValue(childrenTemp.getValue("CLASS_CODE"));
                    childrenNode.setGroup(temp.getValue("CLASS_STYLE"));
                    childrenNode.setType("4");
                    childrenNode.setData(childrenTemp);
                    //模版加入叶节点
                    EMRClass.add(childrenNode);
                }

            }
            this.getTTree(TREE).update();
        }

        /**treeRoot = this.getTTree(TREE).getRoot();
                 treeRoot.setText("模版分类树");
                 treeRoot.setType("Root");
                 treeRoot.removeAllChildren();
                 TParm mainRootParm =this.getMainRootTree();
                 int rowCount = mainRootParm.getCount();
                 for(int i=0;i<rowCount;i++){
            TParm temp = mainRootParm.getRow(i);
            TTreeNode node = new TTreeNode();
            node.setID(temp.getValue("CLASS_CODE"));
            node.setText(temp.getValue("CLASS_DESC"));
            node.setGroup(temp.getValue("CLASS_STYLE"));
            node.setType("1");
            node.setData(temp);
            //便利下面的树节点
         TParm childrenTreeParm = this.getChildTreeType(temp.getValue("CLASS_CODE"));
            int childRowCount = childrenTreeParm.getCount();
            for(int j=0;j<childRowCount;j++){
                TParm childrenTemp = childrenTreeParm.getRow(j);
                TTreeNode childrenNode = new TTreeNode();
                childrenNode.setID(childrenTemp.getValue("SUBCLASS_CODE"));
                childrenNode.setText(childrenTemp.getValue("SUBCLASS_DESC"));
                childrenNode.setValue(childrenTemp.getValue("CLASS_CODE"));
                childrenNode.setGroup(temp.getValue("CLASS_STYLE"));
                childrenNode.setType("4");
                childrenNode.setData(childrenTemp);
                node.add(childrenNode);
            }
            //加入树根
            treeRoot.add(node);
                 }
                 this.getTTree(TREE).update();**/
    }

    /**
     * 新建病例类别
     */
    public void onCreatEmrType() {
        //this.messageBox("come in.");
        Object obj = this.openDialog("%ROOT%\\config\\emr\\EMRClassUI.x");
        this.loadTree();
    }

    /**
     * 新建病例树
     */
    public void onCreatEmrTreeType() {
        Object obj = this.openDialog("%ROOT%\\config\\emr\\EMRTreeUI.x");
        this.loadTree();
    }

    /**
     * 保存
     */
    public boolean onSave() {
        //保存
        if (onlyEditfileName.length() != 0) {
        	//
        	word.setMarkNone();
        	//       	
            word.setMessageBoxSwitch(false);
            //设置文件作者
            word.setFileAuthor(Operator.getID());
            //日期
            String dateStr = StringTool.getString(StringTool.getTimestamp(new
                Date()), "yyyy年MM月dd日 HH时mm分ss秒");
            //设置创建日期
            word.setFileCreateDate(dateStr);
            //设置公司信息
            word.setFileCo("JAVAHIS");
            //设置文件标题
            word.setFileTitle(this.saveParm.getValue("EMT_FILENAME"));
            //设置备注
            word.setFileRemark(this.saveParm.getValue("CLASS_CODE") + "|模版文件|" +
                               this.saveParm.getValue("TEMPLET_PATH") + "|" +
                               this.saveParm.getValue("EMT_FILENAME"));
            if (word.onSaveAs(this.saveParm.getValue("TEMPLET_PATH"),
                              this.saveParm.getValue("EMT_FILENAME"), 2)) {
                EMRCreateXMLTool.getInstance().createXML(saveParm.getValue(
                    "TEMPLET_PATH"),
                    saveParm.getValue("EMT_FILENAME"),
                    "EmrTemplet",
                    word);
                //this.messageBox("=========start exc new========");
                if (this.getOnlyType().equals("NEW")) {
                    TDataStore data = new TDataStore();
                    data.setSQL("SELECT * FROM EMR_TEMPLET");
                    data.retrieve();
                    int rowNumber = data.insertRow();
                    data.setItem(rowNumber, "CLASS_CODE",
                                 this.saveParm.getValue("CLASS_CODE"));
                    data.setItem(rowNumber, "SUBCLASS_CODE",
                                 this.saveParm.getValue("SUBCLASS_CODE"));
                    data.setItem(rowNumber, "SEQ", this.saveParm.getValue("SEQ"));
                    data.setItem(rowNumber, "OPT_USER", Operator.getID());
                    data.setItem(rowNumber, "OPT_DATE",
                                 StringTool.getTimestamp(new Date()));
                    data.setItem(rowNumber, "OPT_TERM", Operator.getIP());
                    data.setItem(rowNumber, "DEPT_CODE",
                                 this.saveParm.getValue("DEPT_CODE"));
                    data.setItem(rowNumber, "EMT_FILENAME",
                                 this.saveParm.getValue("EMT_FILENAME"));
                    data.setItem(rowNumber, "SUBCLASS_DESC",
                                 this.saveParm.getValue("SUBCLASS_DESC"));
                    data.setItem(rowNumber, "RUN_PROGARM",
                                 this.saveParm.getValue("RUN_PROGARM"));
                    data.setItem(rowNumber, "SUBTEMPLET_CODE",
                                 this.saveParm.getValue("SUBTEMPLET_CODE"));
                    data.setItem(rowNumber, "CLASS_STYLE",
                                 this.saveParm.getValue("CLASS_STYLE"));
                    data.setItem(rowNumber, "IPD_FLG",
                                 this.saveParm.getValue("IPD_FLG"));
                    data.setItem(rowNumber, "HRM_FLG",
                                 this.saveParm.getValue("HRM_FLG"));
                    data.setItem(rowNumber, "OPD_FLG",
                                 this.saveParm.getValue("OPD_FLG"));
                    data.setItem(rowNumber, "EMG_FLG",
                                 this.saveParm.getValue("EMG_FLG"));
                    data.setItem(rowNumber, "OIDR_FLG",
                                 this.saveParm.getValue("OIDR_FLG"));
                    data.setItem(rowNumber, "NSS_FLG",
                                 this.saveParm.getValue("NSS_FLG"));
                    data.setItem(rowNumber, "SYSTEM_CODE",
                                 this.saveParm.getValue("SYSTEM_CODE"));
                    data.setItem(rowNumber, "TEMPLET_PATH",
                                 this.saveParm.getValue("TEMPLET_PATH"));
                    data.setItem(rowNumber, "PY1", this.saveParm.getValue("PY1"));

                    data.setItem(rowNumber, "DOCUMENT_TYPE",
                                 this.saveParm.getValue("DOCUMENT_TYPE"));
                    
                    data.setItem(rowNumber, "REF_FLG",
                            this.saveParm.getValue("REF_FLG"));
                    
                    data.setItem(rowNumber, "SEALED_TYPE",
                             this.saveParm.getValue("SEALED_TYPE")); //add by huangtt 20161102 病历封存
                    data.setItem(rowNumber, "PATIENT_SIGN_FLG",
                            this.saveParm.getValue("PATIENT_SIGN_FLG")); //add by Eric 20170621 是否需要患者签名
                    data.setItem(rowNumber, "INTERNSHIP_FLG",
                            this.saveParm.getValue("INTERNSHIP_FLG")); //add by wangqing 20170804 规培适用
                    data.setItem(rowNumber, "PATIENT_SIGN_TWO_FLG",
                            this.saveParm.getValue("PATIENT_SIGN_TWO_FLG")); //add by wangqing 20170926 患者可否二次签名

                    if (!data.update()) {
                        this.messageBox("保存失败！");
                        //设置提示框WORD
                        word.setMessageBoxSwitch(true);
                        //删除文件
                        delFileTempletFile(word.getFileOpenPath(),
                                           this.
                                           saveParm.getValue("EMT_FILENAME"));
                        return false;
                    }
                }
                //this.messageBox("=========level exc new========");
                word.setMessageBoxSwitch(true);
                this.messageBox("保存成功！");
                this.loadTree();
                this.setOnlyType("EDIT");
                return true;
            }
            else {
                this.messageBox("保存失败！");
                word.setMessageBoxSwitch(true);
                return false;
            }
        }
        else {
            this.messageBox("没有需要保存的模版！");
            word.setMessageBoxSwitch(true);
            return true;
        }
    }

    /**
     * 删除模版文件
     * @param templetPath String
     * @param templetName String
     * @return boolean
     */
    public boolean delFileTempletFile(String templetPath, String templetName) {
        //目录表第一个根目录FILESERVER
        String rootName = TIOM_FileServer.getRoot();
        //模板路径服务器
        String templetPathSer = TIOM_FileServer.getPath("EmrTemplet");
        //拿到Socket通讯工具
        TSocket socket = TIOM_FileServer.getSocket();
        //删除文件
        return TIOM_FileServer.deleteFile(socket,
                                          rootName + templetPathSer +
                                          templetPath +
                                          "\\" + templetName + ".jhw");
    }

    /**
     * 递归路径
     * @param node TTreeNode
     * @param fullPath String
     * @return String
     */
    private String getPathFullName(TTreeNode node, String fullPath) {
        //String pathName="";
        if (node.getParent() != null) {
            //this.messageBox("node text==="+((TTreeNode)node.getParent()).getText());
            fullPath += ( (TTreeNode) node.getParent()).getText() + ";";

            return this.getPathFullName( (TTreeNode) node.getParent(), fullPath);
        }
        else {
            //this.messageBox("fullPath result==="+fullPath);
            return fullPath;
        }

    }

    /**
     * 新建模版
     */
    public void onNewTemplet() {
        TTreeNode node = this.getTTree(TREE).getSelectNode();
        String pathName = node.getText();
        String fullPath = "";
        fullPath = this.getPathFullName(node, fullPath);
        String dir[] = fullPath.split(";");
        //this.messageBox("fullPath===="+fullPath);
        String fullpathName = "";
        //this.messageBox("dir.length"+dir.length);
        //跳过根节点
        for (int i = dir.length - 2; i >= 0; i--) {
            fullpathName += dir[i] + "\\";
        }
        pathName = "JHW\\" + fullpathName + "\\" + pathName;

        //this.messageBox("===fullpathName==="+pathName);

        String classCode = node.getID();
        TParm subParm = creatSubClassCode(classCode);

        //this.messageBox("classCode===="+classCode);
        //this.messageBox("className"+node.getText());
        //this.messageBox("SEQ===="+subParm.getValue("SEQ"));
        TParm inParm = new TParm();
        inParm.setData("OP", "ADD");
        //SUBCLASS_CODE
        inParm.setData("SUBCLASS_CODE", subParm.getData("SUBCLASS_CODE"));
        Object obj = this.openDialog("%ROOT%\\config\\emr\\EMRTempInfoUI.x",
                                     inParm);
        if (obj == null) {
            return;
        }
        TParm temp = (TParm) obj;
        String templetMName = "空模版";
        //本地文件保存路径
        String emrLocal = TIOM_FileServer.getPath("EmrLocalTempFileName");
        if (temp.getValue("TITLE_TYPE").equals("1")) {
            templetMName = "空白模版";
        }
        if (temp.getValue("TITLE_TYPE").equals("2")) {
            templetMName = "标题模版";
        }
        if (temp.getValue("TITLE_TYPE").equals("3")) {
            templetMName = "图片模版";
        }
        if (temp.getValue("TITLE_TYPE").equals("4")) {
            templetMName = "A5空模版";
        }
        if (temp.getValue("TITLE_TYPE").equals("5")) {
            templetMName = "A5标题模版";
        }
        if (temp.getValue("TITLE_TYPE").equals("6")) {
            templetMName = "A5图片模版";
        }
        if (word.onOpen(emrLocal, templetMName, 2, false)) {
            //要保存的数据
            TParm saveEmrData = new TParm();
            saveEmrData.setData("CLASS_CODE", classCode);
            saveEmrData.setData("SUBCLASS_CODE",
                                temp.getValue("SUBCLASS_CODE"));
            saveEmrData.setData("SEQ", subParm.getValue("SEQ"));
            saveEmrData.setData("DEPT_CODE", temp.getData("DEPT_CODE"));
            saveEmrData.setData("EMT_FILENAME", temp.getData("EMT_FILENAME"));
            saveEmrData.setData("SUBCLASS_DESC", temp.getData("SUBCLASS_DESC"));
            saveEmrData.setData("RUN_PROGARM", temp.getData("RUN_PROGARM"));
            saveEmrData.setData("SUBTEMPLET_CODE",
                                temp.getData("SUBTEMPLET_CODE"));
            saveEmrData.setData("CLASS_STYLE", temp.getData("CLASS_STYLE"));
            saveEmrData.setData("IPD_FLG", temp.getData("IPD_FLG"));
            saveEmrData.setData("HRM_FLG", temp.getData("HRM_FLG"));
            saveEmrData.setData("OPD_FLG", temp.getData("OPD_FLG"));
            saveEmrData.setData("EMG_FLG", temp.getData("EMG_FLG"));
            saveEmrData.setData("OIDR_FLG", temp.getData("OIDR_FLG"));
            saveEmrData.setData("NSS_FLG", temp.getData("NSS_FLG"));
            saveEmrData.setData("SYSTEM_CODE", temp.getData("SYSTEM_CODE"));
            saveEmrData.setData("PY1", temp.getData("PY1"));
            saveEmrData.setData("TEMPLET_PATH", pathName);
            //this.messageBox("DOCUMENT_TYPE============="+temp.getData("DOCUMENT_TYPE"));
            saveEmrData.setData("DOCUMENT_TYPE", temp.getData("DOCUMENT_TYPE"));
            saveEmrData.setData("REF_FLG", temp.getData("REF_FLG"));
            saveEmrData.setData("SEALED_TYPE", temp.getData("SEALED_TYPE")); //add by huangtt 20161112 病历封存
            saveEmrData.setData("PATIENT_SIGN_FLG", temp.getData("PATIENT_SIGN_FLG")); //add by Eric 20170621 是否需要患者签名
            saveEmrData.setData("INTERNSHIP_FLG", temp.getData("INTERNSHIP_FLG")); //add by wangqing 20170804 规培适用
            saveEmrData.setData("PATIENT_SIGN_TWO_FLG", temp.getData("PATIENT_SIGN_TWO_FLG")); //add by wangqing 20170926 患者可否二次签名
            
            
            this.word.setCanEdit(true);
            this.setOnlyType("NEW");
            this.saveParm = saveEmrData;
            onlyEditfileName = templetMName;
        }
        else {
            this.messageBox("加载模版失败！");
            return;
        }
        //初始化菜单
        initMenu();
    }

    /**
     * 打开模版
     */
    public void onOpenTemplet() {
        TTreeNode node = this.getTTree(TREE).getSelectNode();
        //this.messageBox("node  type======"+node.getType());

        String templetPath = ( (TParm) node.getData()).getValue("TEMPLET_PATH");
        String templetName = ( (TParm) node.getData()).getValue("EMT_FILENAME");
        word.onOpen(templetPath, templetName, 2, false);
        //
        word.setMarkNone();
        //
        this.word.setCanEdit(false);
        onlyEditfileName = templetName;
        this.saveParm = (TParm) node.getData();
        this.setOnlyType("OPEN");
        //初始化菜单
        initMenu();

    }

    /**
     * 编辑模版
     */
    public void onEditTemplet() {
        //打开模版
        this.onOpenTemplet();
        //编辑状态(非整洁)
        this.word.onEditWord();
        //设置可编辑
        this.word.setCanEdit(true);
        //编辑状态
        this.setOnlyType("EDIT");
        //初始化菜单
        initMenu();

    }

    /**
     * 删除模版
     */
    public void onDelTemplet() {
        //加入询问删除框
        if (this.messageBox("询问", "是否删除？", 2) == 0) {

            TTreeNode node = this.getTTree(TREE).getSelectNode();
            String subclassCode = node.getID();
            TParm delParm = (TParm) node.getData();
            TDataStore data = new TDataStore();
            String templetPath = delParm.getValue("TEMPLET_PATH");
            String seq = delParm.getValue("SEQ");
            data.setSQL("SELECT * FROM EMR_TEMPLET");
            data.retrieve();
            int rowNumber = data.rowCount();
            for (int i = 0; i < rowNumber; i++) {
                if (data.getRowParm(i).getValue("SUBCLASS_CODE").equals(
                    subclassCode) &&
                    data.getRowParm(i).getValue("SEQ").equals(seq)) {
                    data.deleteRow(i);
                }
            }
            if (data.update() &&
                delFileTempletFile(templetPath, delParm.getValue("EMT_FILENAME"))) {
                this.messageBox("删除成功！");
                if (this.word.getFileOpenName() != null &&
                    this.word.
                    getFileOpenName().equals(delParm.getValue("EMT_FILENAME"))) {
                    //本地文件保存路径
                    String emrLocal = TIOM_FileServer.getPath(
                        "EmrLocalTempFileName");
                    this.word.onOpen(emrLocal, "初始化", 2, false);
                    //取消编辑
                    this.word.setCanEdit(false);
                }
                this.loadTree();
                this.setOnlyType("");
                onlyEditfileName = "";
            }
            else {
                this.messageBox("删除失败！");
                return;
            }
            //初始化菜单
            initMenu();
        }
        else {
            return;
        }
    }

    /**
     * 比较新老值是否有修改
     * @param oldParm TParm
     * @param newParm TParm
     * @return boolean
     */
    public boolean isOldNewEmrEdit(TParm oldParm, TParm newParm) {
        boolean falg = false;
        String coumn[] = oldParm.getNames();
        for (String temp : coumn) {
            if (!oldParm.getValue(temp).equals(newParm.getValue(temp))) {
                falg = true;
                break;
            }
        }
        return falg;
    }

    /**
     * 模版属性
     */
    public void onPropertyTemplet() {
        TTreeNode node = this.getTTree(TREE).getSelectNode();
        Object obj = this.openDialog("%ROOT%\\config\\emr\\EMRTempInfoUI.x",
                                     node.getData());
//        this.messageBox_(obj);
        if (obj == null) {
            return;
        }
        TParm temp = (TParm) obj;
//        this.messageBox_(temp);
        String subclassCode = node.getID();
//        String subclassCode = temp.getValue("SUBCLASS_CODE");
        TParm oldParm = (TParm) node.getData();
        String seq = oldParm.getValue("SEQ");
        if (oldParm.getValue("EMT_FILENAME").equals(temp.getValue(
            "EMT_FILENAME"))) {
            if (this.word.onOpen(oldParm.getValue("TEMPLET_PATH"),
                                 oldParm.getValue("EMT_FILENAME"), 2, false)) {
            }
            else {
                this.messageBox("文件下载失败！");
                return;
            }
            this.word.setCanEdit(false);
        }
        else {
            if (this.word.onOpen(oldParm.getValue("TEMPLET_PATH"),
                                 oldParm.getValue("EMT_FILENAME"), 2, false)) {
                this.word.setMessageBoxSwitch(false);
                if (!delFileTempletFile(oldParm.getValue("TEMPLET_PATH"),
                                        oldParm.getValue("EMT_FILENAME"))) {
                    this.messageBox("文件下载失败！");
                    this.word.setMessageBoxSwitch(false);
                    return;
                }
            }
            else {
                this.messageBox("文件下载失败！");
                this.word.setMessageBoxSwitch(false);
                return;
            }
            this.word.setMessageBoxSwitch(true);
            this.word.setCanEdit(false);
        }
        TDataStore data = new TDataStore();
        data.setSQL("SELECT * FROM EMR_TEMPLET");
        data.retrieve();
        int rowNumber = data.rowCount();
        TParm onlyParm = new TParm();
        for (int i = 0; i < rowNumber; i++) {
            if (data.getRowParm(i).getValue("SUBCLASS_CODE").equals(
                subclassCode) && data.getRowParm(i).getValue("SEQ").equals(seq)) {
                onlyParm = data.getRowParm(i);
                break;
            }
        }
//        this.messageBox_(onlyParm);
        for (int i = 0; i < rowNumber; i++) {
            if (data.getRowParm(i).getValue("SUBCLASS_CODE").equals(
                subclassCode) && data.getRowParm(i).getValue("SEQ").equals(seq)) {
                data.setItem(i, "OPT_USER", Operator.getID());
                data.setItem(i, "OPT_DATE", StringTool.getTimestamp(new Date()));
                data.setItem(i, "OPT_TERM", Operator.getIP());
                data.setItem(i, "DEPT_CODE", temp.getData("DEPT_CODE"));
                data.setItem(i, "EMT_FILENAME", temp.getData("EMT_FILENAME"));
                data.setItem(i, "SUBCLASS_DESC", temp.getData("SUBCLASS_DESC"));
                data.setItem(i, "RUN_PROGARM", temp.getData("RUN_PROGARM"));
                data.setItem(i, "SUBTEMPLET_CODE",
                             temp.getData("SUBTEMPLET_CODE"));
                data.setItem(i, "CLASS_STYLE", temp.getData("CLASS_STYLE"));
                data.setItem(i, "PY1", temp.getData("PY1"));
                data.setItem(i, "IPD_FLG", temp.getData("IPD_FLG"));
                data.setItem(i, "HRM_FLG", temp.getData("HRM_FLG"));
                data.setItem(i, "OPD_FLG", temp.getData("OPD_FLG"));
                data.setItem(i, "EMG_FLG", temp.getData("EMG_FLG"));
                data.setItem(i, "OIDR_FLG", temp.getData("OIDR_FLG"));
                data.setItem(i, "NSS_FLG", temp.getData("NSS_FLG"));
                data.setItem(i, "SUBCLASS_CODE", temp.getData("SUBCLASS_CODE"));
                data.setItem(i, "SYSTEM_CODE", temp.getData("SYSTEM_CODE"));
                data.setItem(i, "DOCUMENT_TYPE", temp.getData("DOCUMENT_TYPE"));
                data.setItem(i, "REF_FLG", temp.getData("REF_FLG"));
                data.setItem(i, "SEALED_TYPE", temp.getData("SEALED_TYPE")); //add by huangtt 20161112 病历封存
                data.setItem(i, "PATIENT_SIGN_FLG", temp.getData("PATIENT_SIGN_FLG")); //add by Eric 20170621 是否需要患者签名
                data.setItem(i, "INTERNSHIP_FLG", temp.getData("INTERNSHIP_FLG")); //add by wangqing 20170804 规培适用
                data.setItem(i, "PATIENT_SIGN_TWO_FLG", temp.getData("PATIENT_SIGN_TWO_FLG")); //add by wangqing 20170926 患者可否二次签名

//                this.messageBox_(temp.getData("SYSTEM_CODE"));
            }
        }
//        String[] str = data.getUpdateSQL();
//        for(String temps:str){
//            System.out.println(""+temps);
//        }
        
        if (data.update()) {
            if (this.word.onSaveAs(oldParm.getValue("TEMPLET_PATH"),
                                   temp.getValue("EMT_FILENAME"), 2)) {
                this.word.setCanEdit(false);
                onlyEditfileName = temp.getValue("EMT_FILENAME");
                this.saveParm = onlyParm;
                this.setOnlyType("OPEN");
            }
            else {
                return;
            }
        }
        else {
//            System.out.println("DATA"+data.getErrText());
            this.messageBox("保存失败！");
            return;
        }
//        System.out.println(""+data.getErrText());
        this.loadTree();
    }

    /**
     * 得到模版编号
     * @param classCode String
     * @return String
     */
    public TParm creatSubClassCode(String classCode) {
        TParm result = new TParm();
        TParm parm = new TParm(this.getDBTool().select(
            "SELECT NVL(MAX(SEQ)+1,1) AS SEQ FROM EMR_TEMPLET WHERE CLASS_CODE='" +
            classCode + "'"));
        String temp = parm.getValue("SEQ", 0);
        result.setData("SEQ", parm.getData("SEQ", 0));
        if (temp.length() == 1) {
            temp = "0" + temp;
        }
        temp = classCode + temp;
        result.setData("SUBCLASS_CODE", temp);
        return result;
    }

    /**
     * 找到主树
     * @return TParm
     */
    /**public TParm getMainRootTree() {
        TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE FROM EMR_CLASS ORDER BY CLASS_CODE,SEQ"));
        return result;
         }**/

	/**
	 * 便利子节点
	 * 
	 * @param systemCode
	 *            String
	 * @return TParm
	 */
	public TParm getChildTreeType(String classCode) {
		TParm result = new TParm(
				this
						.getDBTool()
						.select(
								"SELECT SUBCLASS_CODE,EMT_FILENAME,SUBCLASS_DESC,CLASS_CODE,TEMPLET_PATH,DEPT_CODE,IPD_FLG,HRM_FLG,OPD_FLG,EMG_FLG,OIDR_FLG,NSS_FLG,RUN_PROGARM,SUBTEMPLET_CODE,CLASS_STYLE,PY1,SEQ,SYSTEM_CODE,DOCUMENT_TYPE,SEALED_TYPE "
								+ ",PATIENT_SIGN_FLG "// add by Eric 20170621 是否需要患者签名
								+ ",INTERNSHIP_FLG "// add by wangqing 20170804 规培适用
								+ ",PATIENT_SIGN_TWO_FLG "// add by wangqing 20170926 患者可否二次签名
								+ "FROM EMR_TEMPLET "
								+ "WHERE CLASS_CODE='"
										+ classCode
										+ "' ORDER BY SUBCLASS_CODE,CLASS_CODE,SEQ"));
		return result;
	}

    /**
     * 拿到树
     * @param tag String
     * @return TTree
     */
    public TTree getTTree(String tag) {
        return (TTree)this.getComponent(tag);
    }

    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * 常用选项关闭事件
     */
    public void onCloseChickedCY() {
        TMovePane mp = (TMovePane)this.getComponent("EMRMOVEPANE");
        mp.onDoubleClicked(true);
    }

    /**
     * 关闭事件
     * @return boolean
     */
    public boolean onClosing() {
        if (onlyEditfileName.length() == 0) {
            return true;
        }
        else {
            switch (messageBox("提示信息", "是否保存?", this.YES_NO_CANCEL_OPTION)) {
                case 0:
                    if (word.isFileEditLock()) {
                        //模版文件加锁
                        word.setFileEditLock(false);
                        //设置锁定IP
                        word.setFileLockIP("");
                        //设置锁定日期
                        word.setFileLockDate("");
                        //设置锁定用户
                        word.setFileLockUser("");
                    }
                    if (!onSave()) {
                        return false;
                    }
                    break;
                case 1:
                    break;
                case 2:
                    return false;
            }
        }
        return true;

    }

    public String getOnlyType() {
        return onlyType;
    }

    public void setOnlyType(String onlyType) {
        this.onlyType = onlyType;
    }

    /**
     * 插入固定文本
     */
    public void onInsertFixText() {
        if (onlyEditfileName.length() != 0) {
            word.insertFixed();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 固定文本属性
     */
    public void onFixTextProperty() {
        if (onlyEditfileName.length() != 0) {
            word.onOpenFixedProperty();
        }
        else {
            this.messageBox("请选择模版！");
        }
        //泰心问题
        //setCapName();
    }

    private void setCapName() {
        EComponent com = word.getFocusManager().getFocus();
        if (! (com instanceof ECapture)) {
            return;
        }
        ECapture capture = (ECapture) com;
        if (capture.getCaptureType() == 0) {
            setNCapName(capture);
        }
        else {
            setPCapName(capture);
        }
        word.getFileManager().update();
    }

    private void setPCapName(ECapture capture) {
        IBlock block = capture.getPreviousTrueBlock();
        while (block != null) {
            if (isSetStartCapture(block)) {
                ( (ECapture) block).setName(capture.getName());
                return;
            }
            IBlock preBlock = block.getPreviousTrueBlock();
            if (preBlock != null) {
                block = preBlock;
                continue;
            }
            EPanel panel = block.getPanel().getPreviousTruePanel();
            if (panel == null || panel.size() == 0) {
                return;
            }
            block = panel.get(0);
        }
    }

    private void setNCapName(ECapture capture) {
        IBlock block = capture.getNextTrueBlock();
        while (block != null) {
            if (isSetEndCapture(block)) {
                ( (ECapture) block).setName(capture.getName());
                return;
            }
            IBlock nextBlock = block.getNextTrueBlock();
            if (nextBlock != null) {
                block = nextBlock;
                continue;
            }
            EPanel panel = block.getPanel().getNextTruePanel();
            if (panel == null || panel.size() == 0) {
                return;
            }
            block = panel.get(0);
        }
    }

    private boolean isSetEndCapture(IBlock block) {
        if (block == null) {
            return false;
        }
        if (block.getObjectType() != EComponent.CAPTURE_TYPE) {
            return false;
        }
        ECapture capture = (ECapture) block;
        if (capture.getCaptureType() != 1) {
            return false;
        }
        return true;
    }

    private boolean isSetStartCapture(IBlock block) {
        if (block == null) {
            return false;
        }
        if (block.getObjectType() != EComponent.CAPTURE_TYPE) {
            return false;
        }
        ECapture capture = (ECapture) block;
        if (capture.getCaptureType() != 0) {
            return false;
        }
        return true;
    }

    /**
     * 删除固定文本
     */
    public void onDelFixText() {
        if (onlyEditfileName.length() != 0) {
            word.deleteFixed();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    public void onDeleteCap() {
        if (onlyEditfileName.length() == 0) {
            messageBox("请选择模版！");
            return;
        }
        EComponent com = word.getFocusManager().getFocus();
        if (! (com instanceof ECapture)) {
            messageBox("选取组件非抓取框");
            return;
        }
        if ( ( (ECapture) com).getCaptureType() == 1) {
            messageBox("请选择开始抓取框");
            return;
        }
        deleteCap(word.findCapture( ( (ECapture) com).getName()));
        deleteCapCom(com);
    }


    public void deleteCap(ECapture capture) {
        IBlock block = capture.getNextTrueBlock();
        while (block != null) {
            if (isEndCapture(block, capture)) {
                deleteCapCom( (EComponent) block);
                return;
            }
            if (block instanceof ETable) {
                deleteCapCom( (EComponent) block);
                block = capture.getNextTrueBlock();
                continue;
            }
            IBlock nextBlock = block.getNextTrueBlock();
            if (nextBlock != null) {
                deleteCapCom( (EComponent) block);
                block = nextBlock;
                continue;
            }
            EPanel panel = block.getPanel().getNextTruePanel();
            deleteCapCom( (EComponent) block);
            if (panel == null || panel.size() == 0) {
                return;
            }
            block = panel.get(0);
        }
    }

    private boolean isEndCapture(IBlock block, ECapture captureStart) {
        if (block == null) {
            return false;
        }
        if (block.getObjectType() != EComponent.CAPTURE_TYPE) {
            return false;
        }
        ECapture capture = (ECapture) block;
        if (capture.getCaptureType() != 1) {
            return false;
        }
        if (capture.getName() == null || capture.getName().length() == 0) {
            return false;
        }
        if (capture.getName().equals(captureStart.getName())) {
            return true;
        }
        return false;
    }

    private void deleteCapCom(EComponent com) {
        if (com == null) {
            return;
        }
        if (com instanceof EFixed) {
            ( (EFixed) com).deleteFixed();
        }
        else if (com instanceof EMacroroutine) {
            ( (EMacroroutine) com).delete();
        }
        else if (com instanceof ETable) {
            ( (ETable) com).removeThisAll();
        }
        else if (com instanceof EImage) {
            ( (EImage) com).removeThis();
        }
        else {
            return;
        }
        word.update();
    }

    /**
     * 插入单选
     */
    public void onInsertSingleChoose() {
        if (onlyEditfileName.length() != 0) {
            word.insertSingleChoose();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入关联选项
     */
    public void onInsertRelateChoose() {
        if (onlyEditfileName.length() != 0) {
            word.insertAssociateChoose();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }


    /**
     * 插入多选
     */
    public void onInsertMultiChoose() {
        if (onlyEditfileName.length() != 0) {
            word.insertMultiChoose();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 有无选择
     */
    public void onInsertHasChoose() {
        if (onlyEditfileName.length() != 0) {
            word.insertHasChoose();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入宏
     */
    public void onInsertMicroField() {
        if (onlyEditfileName.length() != 0) {
            word.insertMicroField();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入表格
     */
    public void onInsertTable() {
        if (onlyEditfileName.length() != 0) {
            ETable etable = word.insertBaseTableDialog();
            //System.out.println("===etable ==="+etable.isShowBorder());
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 删除表格
     */
    public void onDelTable() {
        if (onlyEditfileName.length() != 0) {
            word.deleteTable();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入表格行
     */
    public void onInsertTableRow() {
        if (onlyEditfileName.length() != 0) {
            word.insertTR();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 追加表格行
     */
    public void onAddTableRow() {
        if (onlyEditfileName.length() != 0) {
            word.appendTR();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 删除表格行
     */
    public void onDelTableRow() {
        if (onlyEditfileName.length() != 0) {
            word.deleteTR();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 宏属性
     */
    public void onMicroFieldProperty() {
        if (onlyEditfileName.length() != 0) {
            word.onOpenMicroFieldProperty();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 打印设置
     */
    public void onPrintSetup() {
        if (onlyEditfileName.length() != 0) {
            word.printSetup();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 页面设置
     */
    public void onPrintPageSet() {
        if (onlyEditfileName.length() != 0) {
            this.openDialog("%ROOT%\\config\\database\\PageSetupDialog.x",
                            word.getPageManager());
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 打印
     */
    public void onPrint() {
        if (onlyEditfileName.length() != 0) {
            word.print();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 居左
     */
    public void onAlignmentLeft() {
        if (onlyEditfileName.length() != 0) {
            word.setAlignment(0);
        }
        else {
            return;
        }

    }

    /**
     * 居中
     */
    public void onAlignmentCenter() {
        if (onlyEditfileName.length() != 0) {
            word.setAlignment(1);
        }
        else {
            return;
        }

    }

    /**
     * 居右
     */
    public void onAlignmentRight() {
        if (onlyEditfileName.length() != 0) {
            word.setAlignment(2);
        }
        else {
            return;
        }
    }

    /**
     * 加入段落
     */
    public void onAddDLText() {
        if (onlyEditfileName.length() != 0) {
            word.onParagraphDialog();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入输入提示组件
     */
    public void onAddInputMessage() {
        if (onlyEditfileName.length() != 0) {
            word.insertInputText();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 表格属性
     */
    public void onTableProperty() {
        if (onlyEditfileName.length() != 0) {
            word.onTablePropertyDialog();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入抓取
     */
    public void onInsertCaptureObject() {
        if (onlyEditfileName.length() != 0) {
            ECapture obj = word.insertCaptureObject();
            word.insertCaptureObject();
            obj.setCaptureType(1);
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 抓取测试对话框//String word.getCaptureValue(String name)抓取方法
     */
    public void onCaptureDataProperty() {
        openDialog("%ROOT%\\config\\database\\CaptureDataDialog.x", word);
    }

    /**
     * 选择框
     */
    public void onInsertCheckBoxChooseObject() {
        if (onlyEditfileName.length() != 0) {
            word.insertCheckBoxChoose();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 整洁预览
     */
    public void onPrintClear() {
        if (onlyEditfileName.length() != 0) {
            word.onPreviewWord();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 自定义脚本
     */
    public void onCustomScriptDialog() {
        if (onlyEditfileName.length() != 0) {
            word.onCustomScriptDialog();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入图片
     */
    public void onInsertPictureObject() {
        if (onlyEditfileName.length() != 0) {
            word.insertPicture();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 数字选择
     */
    public void onInsertNumberChooseObject() {
        if (onlyEditfileName.length() != 0) {
            word.insertNumberChoose();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 剪切
     */
    public void onCutMenu() {
        if (onlyEditfileName.length() != 0) {
            word.onCut();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 复制
     */
    public void onCopyMenu() {
        if (onlyEditfileName.length() != 0) {
            word.onCopy();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 粘贴
     */
    public void onPasteMenu() {
        if (onlyEditfileName.length() != 0) {
            word.onPaste();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 删除
     */
    public void onDeleteMenu() {
        if (onlyEditfileName.length() != 0) {
            word.onDelete();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入图片编辑区
     */
    public void onInsertImageEdit() {
        if (onlyEditfileName.length() != 0) {
            word.insertImage();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 删除图片编辑区
     */
    public void onDeleteImageEdit() {
        if (onlyEditfileName.length() != 0) {
            word.deleteImage();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入块
     */
    public void onInsertGBlock() {
        if (onlyEditfileName.length() != 0) {
            word.insertGBlock();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 插入线
     */
    public void onInsertGLine() {
        if (onlyEditfileName.length() != 0) {
            word.insertGLine();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 调整块尺寸
     * @param index int
     */
    public void onSizeBlockMenu(String index) {
        if (onlyEditfileName.length() != 0) {
            word.onSizeBlockMenu(StringTool.getInt(index));
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 图层控制
     */
    public void onDIVProperty() {
        if (onlyEditfileName.length() != 0) {
            word.onDIVProperty();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 续印
     */
    public void onPrintXDDialog() {
        if (onlyEditfileName.length() != 0) {
            word.onPreviewWord();
            word.printXDDialog();
        }
        else {
            //请选择病历
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 显示行号开关
     */
    public void onShowRowIDSwitch() {
        if (onlyEditfileName.length() != 0) {
            word.setShowRowID(!word.isShowRowID());
            word.update();
        }
        else {
            //请选择病历
            this.messageBox("请选择模版！");
        }
    }

    /**
     * 下拉框
     */
    public void onInsertETextFormatObject() {
        if (onlyEditfileName.length() != 0) {
            word.insertTextFormat();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }


    /**
     * add by lx
     * @return TParm
     */
    public TParm getTreeRoot() {
        TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE FROM EMR_CLASS WHERE PARENT_CLASS_CODE IS NULL"));
        return result;

    }


    /**add by lx
     * 找到主树
     * @return TParm
     */
    public TParm getMainRootTree() {
        //m by lx
        TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE FROM EMR_CLASS ORDER BY CLASS_CODE,SEQ"));
        return result;
    }

    /**
     * 插入病历元素;
     */
    public void onInserElement() {
        //传入文档类型参数；
        TTreeNode node = this.getTTree(TREE).getSelectNode();
        if (node == null) {
            this.messageBox("请先选择模版！");
            return;
        }
        String documentType = ( (TParm) node.getData()).getValue(
            "DOCUMENT_TYPE");
        //this.messageBox("documentType==="+documentType);
        TParm inParm = new TParm();
        inParm.setData("DOCUMENT_TYPE", documentType);

        //打开插入病历元素窗体；
        TParm reParm = (TParm)this.openDialog(
            "%ROOT%\\config\\database\\InsertElementDialog.x", inParm);

        //System.out.println("reParm-============"+reParm);
        //是否必须
        //this.setValue("REPEAT_COUNT", reParm.getData("REPEAT_COUNT", 0));
        //是什么控件;
        // this.setValue("COMPONENT_TYPE", reParm.getData("COMPONENT_TYPE", 0));
        //数据编号 名子
        //this.setValue("DATA_CODE", reParm.getData("DATA_CODE", 0));
        //数据值
        //this.setValue("DATA_DESC", reParm.getData("DATA_DESC", 0));

        //返回参数是否  必须的|类型
        insertElement(reParm);

    }

    /**
     * 搜入隐藏元素
     */
    public void onInserHideElement() {
        //
        TParm inParam = new TParm();
        inParam.addData("theWord", word);

        //打开隐藏元素窗体
        TParm reParm = (TParm)this.openDialog(
            "%ROOT%\\config\\database\\InsertHideElementDialog.x", inParam);
        //返回word
        //word=reParm.getData("REPEAT_COUNT", 0)

    }

    /**
     * 通过类型插入对应元素；
     * 文本 0
     图区 2
     固定文本 3
     单选 4
     多选 5
     有无选择 6
     输入提示 7
     输入宏 8
     抓取对象 9
     选择框 10
     图片编辑 11
     图片 13
     数字 14
     下拉框 15
     */
    private void insertElement(TParm reParm) {

        //控件类型;
        String componentType = (String) reParm.getData(
            "SELECTED_COMPONENT_TYPE", 0);
        //大类名子
        String groupCode = (String) reParm.getData("GROUP_CODE", 0);

        //数据编号 名子
        String dataCode = (String) reParm.getData("DATA_CODE", 0);
        //数据值
        String dataDesc = (String) reParm.getData("DATA_DESC", 0);
        //宏名
        String macroName = (String) reParm.getData("MACRO_NAME", 0);

        //是否为必要
        boolean isAllowNull = isAllowNull(reParm);

        //this.messageBox("==groupCode=="+groupCode);
        //this.messageBox("==macroName=="+macroName);

        //codesystem
        String codesystem = (String) reParm.getData("CODE_SYSTEM", 0);

        //取出textFormat类型
        if (componentType.equals("0")) {

        }
        else if (componentType.equals("2")) {

        }
        else if (componentType.equals("3")) {
            if (onlyEditfileName.length() != 0) {
                EFixed eword = word.insertFixed();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("请选择模版！");
            }

        }
        else if (componentType.equals("4")) {
            if (onlyEditfileName.length() != 0) {
                ESingleChoose eword = word.insertSingleChoose();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("请选择模版！");
            }
        }
        else if (componentType.equals("5")) {
            if (onlyEditfileName.length() != 0) {
                EMultiChoose eword = word.insertMultiChoose();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("请选择模版！");
            }

        }
        else if (componentType.equals("6")) {
            if (onlyEditfileName.length() != 0) {
                EHasChoose eword = word.insertHasChoose();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("请选择模版！");
            }

        }
        else if (componentType.equals("7")) {
            if (onlyEditfileName.length() != 0) {
                EInputText eword = word.insertInputText();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("请选择模版！");
            }

        }
        else if (componentType.equals("8")) {
            if (onlyEditfileName.length() != 0) {
                EMicroField eword = word.insertMicroField();
                eword.setName(dataCode);
                eword.setText(dataDesc);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

            }
            else {
                this.messageBox("请选择模版！");
            }

        }
        //抓取
        else if (componentType.equals("9")) {
            if (onlyEditfileName.length() != 0) {
                ECapture obj = word.insertCaptureObject();
                obj.setName(dataCode);
                //obj.setText(dataDesc);

                ECapture obj1 = word.insertCaptureObject();
                obj1.setName(dataCode);
                //obj1.setText(dataDesc);
                obj1.setAllowNull(isAllowNull);
                obj1.setDataElements(true);
                obj1.setGroupName(groupCode);
                //this.messageBox("macroName=========="+macroName);
                obj1.setMicroName(macroName);

                obj.setCaptureType(1);
                obj.setAllowNull(isAllowNull);
                obj.setDataElements(true);
                obj.setGroupName(groupCode);
                obj.setMicroName(macroName);

            }
            else {
                this.messageBox("请选择模版！");
            }

        }
        else if (componentType.equals("10")) {
            if (onlyEditfileName.length() != 0) {
                // word.inserc.insertCheckBoxChoose();
                ECheckBoxChoose eword = word.insertCheckBoxChoose();
                eword.setName(dataCode);
                eword.setText(dataDesc);

                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("请选择模版！");
            }

        }
        else if (componentType.equals("11")) {

        }
        else if (componentType.equals("13")) {

        }
        //数字选择
        else if (componentType.equals("14")) {
            if (onlyEditfileName.length() != 0) {
                ENumberChoose eword = word.insertNumberChoose();
                eword.setName(dataCode);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);
            }
            else {
                this.messageBox("请选择模版！");
            }

        }
        //下拉框
        else if (componentType.equals("15")) {
            if (onlyEditfileName.length() != 0) {
                ETextFormat eword = word.insertTextFormat();
                eword.setName(dataCode);
                //this.messageBox("dataDesc"+dataDesc);
                eword.setText(dataDesc);
                eword.setAllowNull(isAllowNull);
                eword.setDataElements(true);
                //this.messageBox(codesystem);
                eword.setData(codesystem);

                eword.setGroupName(groupCode);
                eword.setMicroName(macroName);

            }
            else {
                this.messageBox("请选择模版！");
            }

        }

    }

    /**
     * 插入模版片语
     */
    public void onInsertTemplatePY() {
        TParm inParm = new TParm();
        inParm.setData("TYPE", "2");
        //this.messageBox("this.getDeptCode()"+this.getDeptCode());
        inParm.setData("DEPT_CODE", "");
        inParm.setData("TWORD", this.word);
        inParm.addListener("onReturnTemplateContent", this,
                           "onReturnTemplateContent");
        TWindow window = (TWindow)this.openWindow(
            "%ROOT%\\config\\emr\\EMRTemplateComPhraseQuote.x", inParm, true);
        window.setX(ImageTool.getScreenWidth() - window.getWidth());
        window.setY(0);
        window.setVisible(true);

    }


    /**
     * 是允许空
     * @param reParm TParm
     * @return boolean
     */
    private boolean isAllowNull(TParm reParm) {
        String strRequire = (String) reParm.getData("REPEAT_COUNT", 0);
        String flag = strRequire.substring(0, 1);
        //this.messageBox("==flag==" + flag);
        //不必填  允许空；
        if (flag.equals("0")) {
            return true;
            //必填   不允许空；
        }
        else {
            return false;
        }

    }

    /**
     * 清空系统剪贴板
     */
    public void onClearMenu() {
        CopyOperator.clearComList();
    }
    
    /**
     * 合并单元格
     */
    public void onMergerCell(){
		if (word.getFileOpenName() != null) {
			word.mergerCell();
		} else {
			this.messageBox("请选择模版！");
		}
    }
    
    
    /**
     * 设置格式刷
     */
    public void onFormatSet(){
		if (this.word.getFileOpenName() != null) {
			this.word.onFormatSet();
		} else {
			this.messageBox("请选择模版！");
		}
    }

    /**
     * 使用格式刷
     */
    public void onFormatUse(){
		if (this.word.getFileOpenName() != null) {
			this.word.onFormatUse();
		} else {
			this.messageBox("请选择模版！");
		}
    }
    

    
    /**
     * 添加表达式
     */
    public void onInsertExpression(){

        if (onlyEditfileName.length() != 0) {

        	EFixed ef = word.insertFixed("无名表达式","表达式");
        	ef.setExpression(true);
        	word.getExpressionList().add(ef);

		} else {
			this.messageBox("请选择模版！");
		}
    }

    /**
     * 设置表达式
     */
    public void onSettingExpression(){

    	if (onlyEditfileName.length() != 0) {

	       	 EComponent ec = word.getFocusManager().getFocus();

	       	 if( this.onSettingExpression(ec) ) return;

	    	 //
	    	 this.messageBox("请选择表达式组件！");
    	}else {
			this.messageBox("请选择模版！");
		}
    }

    /**
     *
     * @param ec
     * @return
     */
    private boolean onSettingExpression(EComponent ec) {

		if (ec instanceof EFixed) {
			EFixed ef = (EFixed) ec;
			//this.messageBox("---11111---"+ef.getName());
			if (ef.isExpression()) {
				openDialog( "%ROOT%\\config\\emr\\EMRExpressionDialog.x", new Object[]{this.word,ef} );
				return true;
			}
		}

		//
		return false;
	}

    /**
	 * 计算表达式
	 */
    public void onCalculateExpression(){

        if (onlyEditfileName.length() != 0) {
             word.onCalculateExpression();
        }
        else {
            this.messageBox("请选择模版！");
        }
    }

    /**
     *添加新抓取
     */
    public void onInsertExpressionCapture(){

        if (onlyEditfileName.length() != 0) {
        	openDialog( "%ROOT%\\config\\emr\\EMRECaptureDialog.x", this.word );
        }
        else {
            this.messageBox("请选择模版！");
        }
    }
    /**
     * 插入上下标功能
     */
	public void onInsertMarkText() {
		//
		if (onlyEditfileName.length() != 0) {
			word.insertFixed();
			word.onOpenMarkProperty();
			//
		} else {
			this.messageBox("请选择病历！");
		}
	}
    
    /**
     * 上下标文本属性
     */
    public void onMarkTextProperty() {
        if (onlyEditfileName.length() != 0) {
        	word.onOpenMarkProperty();
        }
        else {
            this.messageBox("请选择病历！");
        }
    }
    
    
    
    /**
     * 别存副本
     */
	public void onSaveTemplet() {
		TTreeNode node = this.getTTree(TREE).getSelectNode();
		String templetPath = ((TParm) node.getData()).getValue("TEMPLET_PATH");
		String templetName = ((TParm) node.getData()).getValue("EMT_FILENAME");
		String oldSubclass = ((TParm) node.getData()).getValue("SUBCLASS_CODE");
		String oldSeq = ((TParm) node.getData()).getValue("SEQ");
		String classCode = ((TParm) node.getData()).getValue("CLASS_CODE");
		//
		// this.messageBox("111subclass111==="+subclass);
		// this.messageBox("111seq111==="+seq);
		// 写文件服务器文个把

		TSocket socket = TIOM_FileServer.getSocket();
		// 目录表第一个根目录FILESERVER
		String rootName = TIOM_FileServer.getRoot();
		// 模板路径服务器
		String templetPathSer = TIOM_FileServer.getPath("EmrTemplet");
		//
		byte[] bfile = TIOM_FileServer.readFile(socket, rootName
				+ templetPathSer + templetPath + "\\" + templetName + ".jhw");

		boolean flg = TIOM_FileServer.writeFile(socket,
				rootName + templetPathSer + templetPath + "\\" + templetName
						+ "_副本.jhw", bfile);

		//this.messageBox("1111flg1111" + flg);
		// 成功则
		if (flg) {
			//
			TParm oldParm = new TParm(this.getDBTool().select(
					"SELECT * FROM EMR_TEMPLET WHERE SUBCLASS_CODE='"
							+ oldSubclass + "' and SEQ='" + oldSeq + "'"));
			//
			TParm parm = new TParm(this.getDBTool().select(
		            "SELECT NVL(MAX(SEQ)+1,1) AS SEQ FROM EMR_TEMPLET WHERE CLASS_CODE='" +
		            classCode + "'"));
		        String temp = parm.getValue("SEQ", 0);
		        //
		        if (temp.length() == 1) {
		            temp = "0" + temp;
		        }
		        //
		        String  subClassCode = classCode + temp;
				//
                TDataStore data = new TDataStore();
                data.setSQL("SELECT * FROM EMR_TEMPLET");
                data.retrieve();
                int rowNumber = data.insertRow();
                data.setItem(rowNumber, "CLASS_CODE",
                		oldParm.getValue("CLASS_CODE",0));
                data.setItem(rowNumber, "SUBCLASS_CODE",subClassCode);
                data.setItem(rowNumber, "SEQ", temp);
                data.setItem(rowNumber, "OPT_USER", Operator.getID());
                data.setItem(rowNumber, "OPT_DATE",
                             StringTool.getTimestamp(new Date()));
                data.setItem(rowNumber, "OPT_TERM", Operator.getIP());
                data.setItem(rowNumber, "DEPT_CODE","");
                data.setItem(rowNumber, "EMT_FILENAME",
                		oldParm.getValue("EMT_FILENAME",0)+"_副本");
                data.setItem(rowNumber, "SUBCLASS_DESC",
                		oldParm.getValue("SUBCLASS_DESC",0)+"_副本");
                data.setItem(rowNumber, "RUN_PROGARM","");
                data.setItem(rowNumber, "SUBTEMPLET_CODE","");
                data.setItem(rowNumber, "CLASS_STYLE",
                		oldParm.getValue("CLASS_STYLE",0));
                data.setItem(rowNumber, "IPD_FLG","N");
                data.setItem(rowNumber, "HRM_FLG","N");
                data.setItem(rowNumber, "OPD_FLG","N");
                data.setItem(rowNumber, "EMG_FLG","N");
                data.setItem(rowNumber, "OIDR_FLG","N");
                data.setItem(rowNumber, "NSS_FLG","N");
                data.setItem(rowNumber, "SYSTEM_CODE",
                		oldParm.getValue("SYSTEM_CODE",0));
                data.setItem(rowNumber, "TEMPLET_PATH",
                		oldParm.getValue("TEMPLET_PATH",0));
                data.setItem(rowNumber, "PY1", oldParm.getValue("PY1",0));

                data.setItem(rowNumber, "DOCUMENT_TYPE","");
                
                data.setItem(rowNumber, "REF_FLG","N");
                
                data.setItem(rowNumber, "SEALED_TYPE","N"); 

                if (!data.update()) {
                	//
                    this.messageBox("另存副本失败！");

                }else{	            
		            //this.setOnlyType("EDIT");			
					// 数据库新建一个,记录
					this.messageBox("另存副本成功！");
					//this.loadTree();
					/*
					childrenNode.setID(childrenTemp.getValue("SUBCLASS_CODE"));
                    childrenNode.setText(childrenTemp.getValue("SUBCLASS_DESC"));
                    childrenNode.setValue(childrenTemp.getValue("CLASS_CODE"));
                    childrenNode.setGroup(temp.getValue("CLASS_STYLE"));
                    childrenNode.setType("4");
                    childrenNode.setData(childrenTemp);*/
					
					TParm newParm = new TParm(this.getDBTool().select(
							"SELECT * FROM EMR_TEMPLET WHERE SUBCLASS_CODE='"
									+ subClassCode + "' and SEQ='" + temp + "'"));
					
					//新增直接展开，刷新分类树
					TTreeNode nodeChlid = new TTreeNode();
					nodeChlid
							.setID(subClassCode);
					nodeChlid.setText(newParm.getValue("SUBCLASS_DESC",0));
					nodeChlid.setType("4");
					nodeChlid.setGroup(newParm.getValue("CLASS_STYLE",0));
					nodeChlid.setValue(newParm.getValue("CLASS_CODE",0));
					nodeChlid.setData(newParm.getRow(0));				
					//父节点
					TTreeNode faterNode=treeRoot.findNodeForID(
							newParm.getValue("CLASS_CODE",0));
							
					faterNode.add(nodeChlid);
					this.getTTree(TREE).update();
					//节点是否是展开的
					this.getTTree(TREE).expandRow(faterNode);	
	           }
			// 刷新树
		}

	}


}
