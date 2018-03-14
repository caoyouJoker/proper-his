package com.javahis.ui.emr;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TWord;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TTree;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.tui.text.MEvent;
import com.dongyang.tui.text.EText;
import com.dongyang.tui.text.ETR;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EMacroroutine;
import com.dongyang.tui.text.ECapture;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EMRTemplateComPhraseQuoteControl
    extends TControl {
    private String deptCode;
    private TParm evenParm;
    //COMBOBOX 部门
    private TComboBox combo;
    /**
     * 树根
     */
    private TTreeNode treeRoot;

    /**
     * WORD对象
     */
    private TWord parmWord;

    private TWord word;
    private TTree tree;

    /**
     * WORD对象
     */
    private static final String TWORD_NAME = "WORD";
    /**
     * 树的名字
     */
    private static final String TREE_NAME = "TREE";

    private String phraseFilePath = "";
    private String phraseFileName = "";


    public EMRTemplateComPhraseQuoteControl() {
    }

    public void onInit() {
        super.onInit();
        Object obj = this.getParameter();
        combo = (TComboBox)this.getComponent("DEPT_CODE");
        word = (TWord)this.getComponent("WORD");
        tree = (TTree)this.getComponent(TREE_NAME);
        if (obj != null) {
            evenParm = (TParm) obj;
            //this.messageBox("evenParm++++"+evenParm);
            //this.messageBox("++DEPT_CODE++"+evenParm.getValue("DEPT_CODE"));
            parmWord = (TWord) evenParm.getData("TWORD");
            this.setDeptCode(evenParm.getValue("DEPT_CODE"));
            //this.messageBox("==DEPT_CODE=="+this.getDeptCode());
            combo.setSelectedID(evenParm.getValue("DEPT_CODE"));
            combo.setVisible(true);
            combo.setEnabled(false);

            loadTree();

            initEven();
        }

    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptCode() {
        return this.deptCode;
    }

    public void onClear() {
        onInit();
    }

    /**
     * 加载树
     */
    public void loadTree() {
        treeRoot = tree.getRoot();
        //拿到树根
        treeRoot.setText("片语分类");
        treeRoot.setType("Root");
        treeRoot.removeAllChildren();

        //拿到节点数据
        TParm rootParam = this.getRootNodeData();
        if (rootParam.getInt("ACTION", "COUNT") == 0) {
            //没有设置片语分类！请检查数据库设置
            this.messageBox("请先设置片语分类！");
            return;
        }

        treeRoot.setText(rootParam.getValue("PHRASE_CODE", 0));
        treeRoot.setType("Root");
        treeRoot.setID(rootParam.getValue("CLASS_CODE", 0));

        //得到数据集合
        TParm nodesParam = this.getNodes();

        if (nodesParam == null) {
            return;
        }
        int nodesCount = nodesParam.getCount();
        for (int i = 0; i < nodesCount; i++) {
            TParm temp = nodesParam.getRow(i);
            //根据节点类型设置是否为目录节点
            String noteType = "1";
            //叶节点(是结构化片语)
            if (nodesParam.getValue("LEAF_FLG", i).equals
                ("Y")) {
                noteType = "4";
            }
            //建立新节点
            TTreeNode PhraseClass = new TTreeNode("PHRASECLASS"
                                                  + i, noteType);
            //将ERM分类信息设置到节点当中
            PhraseClass.setText(nodesParam.getValue
                                ("PHRASE_CODE", i));
            PhraseClass.setID(nodesParam.getValue
                              ("CLASS_CODE", i));
            //设置所有类型
            PhraseClass.setData(temp);

            //第一级的节点放入根结点
            if (nodesParam.getValue("PARENT_CLASS_CODE",
                                    i).equals("ROOT")) {
                treeRoot.addSeq(PhraseClass);
            }
            //其他级别的节点放入相应的父节点下面
            else {
                if (nodesParam.getValue
                    ("PARENT_CLASS_CODE", i).length() != 0) {
                    //假如叶节点（片语节点）,是当前科室的加入；

                    //是叶节点
                    if(nodesParam.getValue("LEAF_FLG", i).equals("Y")){
                        //是主片语加入节点，补充的片语在此不加入
                         if(nodesParam.getValue("MAIN_FLG", i).equals("Y")){
                             treeRoot.findNodeForID(nodesParam.getValue(
                            "PARENT_CLASS_CODE", i)).add(PhraseClass);
                         }
                        //
                    }else{
                        treeRoot.findNodeForID(nodesParam.getValue(
                            "PARENT_CLASS_CODE", i)).add(PhraseClass);
                    }
                }
            }

        }
        tree.update();

        /**
                 int rowCount = parm.getInt("ACTION", "COUNT");
                 for (int i = 0; i < rowCount; i++) {
            TParm mainParm = parm.getRow(i);
            //加节点   EMR_CLASS表中的STYLE字段
            TTreeNode node = new TTreeNode();
            node.setID(mainParm.getValue("ID"));
            node.setText(mainParm.getValue("CHN_DESC"));
            //设置菜单显示样式
            node.setType("2");
            node.setGroup(mainParm.getValue("ID"));
            node.setData(mainParm);
            //加入树根
            treeRoot.add(node);
            //node.setInitExpanded(true);
            //子节点
            TParm childParms = this.getChildtNodeData(node.getID());
            //System.out.println("=======childParms========="+childParms);
            for (int j = 0; j < childParms.getCount(); j++) {
                TParm theChildParm = childParms.getRow(j);
                TTreeNode childNode = new TTreeNode();
                childNode.setID(theChildParm.getValue("PHRASE_FILE_NAME"));
                childNode.setText(theChildParm.getValue("PHRASE_CODE"));
                //设置菜单显示样式
                childNode.setType("4");
                childNode.setGroup(theChildParm.getValue("PHRASE_FILE_NAME"));
                childNode.setData(theChildParm);
                node.add(childNode);
            }

                 }**/

    }

    /**
     * 注册事件
     */
    public void initEven() {
        //叶节点双击事件，打开对应的结构化片语；
        //单击选中树项目
        addEventListener(TREE_NAME + "->" + TTreeEvent.DOUBLE_CLICKED,
                         "onTreeDoubled");

    }

    /**
     * 传回
     */
    public void onFetchBack() {
        //this.messageBox("come in");
        EComponent com=parmWord.getFocusManager().getFocus();
        ECapture firstCapture=null;
        if(com!=null){
           if(com instanceof ECapture)
           {
               //System.out.println(((ECapture)com).getName());
               //System.out.println(((ECapture)com).getCaptureType());
               firstCapture=(ECapture)com;
           }
        }

        parmWord.getFocusManager().onInsertFile(phraseFilePath, phraseFileName,
                                             2, false);
        //删除回车
        if(firstCapture!=null){
            firstCapture.setFocusLast();
            boolean b=firstCapture.deleteChar();
            //System.out.println("删除操作++++++"+b);
        }
        //System.out.println("EMRTemplateComPhrase test===="+parmWord.getFocus());
        parmWord.update();

    }

    /**
     * 得到事件管理器
     * @return MEvent
     */
    /** public MEvent getEventManager() {
         return word.getEventManager();
     }

     public void doRightMove() {
         this.messageBox("isSelected"+word.getFocusManager().isSelected());
         //设置选中开始
         if (!word.getFocusManager().isSelected()) {
             word.getFocusManager().setStartSelected();
         }
         onFocusToRight();
         //设置选中结束
         word.getFocusManager().setEndSelected();
         //setHasFocusPointX(false);

     }


     public void onFocusToRight() {
         ETR tr = word.getFocusManager().getFocusTRE();
         if (tr != null) {
             tr.onFocusToRight();
             return;
         }
         EText text = word.getFocusManager().getFocusText();
         if (text == null) {
             return;
         }
         text.onFocusToRight();
     }**/


    public void onTreeDoubled(Object parm) {

        TTreeNode node = (TTreeNode) parm;
        TParm dataParm = (TParm) node.getData();
        //this.messageBox("==dataParm==="+dataParm);
        //打开模版文件
        String isPhrase=dataParm.getValue("LEAF_FLG");
        //this.messageBox("==isPhrase==="+isPhrase);

        if(isPhrase.equals("Y")){
            phraseFilePath = dataParm.getValue("FILE_PATH");
            phraseFileName = dataParm.getValue("FILE_NAME");
            if (!this.word.onOpen(dataParm.getValue("FILE_PATH"),
                                  dataParm.getValue("FILE_NAME"), 2, false)) {
                return;
            }
            this.word.onEditWord();
            //设置不可编辑
            this.word.setCanEdit(true);
            //word.getFocusManager().onPreviewWord();
        }

    }

    public TParm getChildtNodeData(String parentID) {
        String sql =
            "SELECT * FROM OPD_COMTEMPLATEPHRASE WHERE PHRASE_TYPE='" +
            parentID + "'";
        sql += " AND DEPTORDR_CODE='" + this.getDeptCode() + "'";
        sql += " ORDER BY OPT_DATE";
        //this.messageBox("sql=="+sql);

        //this.messageBox("dept code"+this.getDeptCode());
        TParm result = new TParm(this.getDBTool().select(sql));
        return result;

    }

    /**
     * 拿到根节点数据
     * @return TParm
     */
    public TParm getRootNodeData() {
        /**String sql =
         "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='PHRASE_TYPE'";
                 sql += " ORDER BY SEQ";
                 TParm result = new TParm(this.getDBTool().select(sql));
                 return result;**/
        TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,PHRASE_CODE,LEAF_FLG,MAIN_FLG,PARENT_CLASS_CODE,FILE_PATH,FILE_NAME FROM OPD_COMTEMPLATE_PHRASE WHERE PARENT_CLASS_CODE IS NULL"));
        return result;
    }

    private TParm getNodes() {
        TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,PHRASE_CODE,LEAF_FLG,MAIN_FLG,PARENT_CLASS_CODE,FILE_PATH,FILE_NAME FROM OPD_COMTEMPLATE_PHRASE ORDER BY CLASS_CODE,SEQ"));
        return result;
    }


    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }


}
