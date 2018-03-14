package com.javahis.ui.opd;

import com.dongyang.control.TControl;
import com.dongyang.data.TSocket;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.util.TMessage;
import jdo.sys.Operator;
import com.dongyang.ui.event.TTreeEvent;
import jdo.sys.SystemTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TPanel;
import java.util.Vector;
import com.dongyang.ui.TTextFormat;
import com.dongyang.tui.text.CopyOperator;

/**
 * <p>Title: 结构化子模版(泰心专用)</p>
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
public class OPDComStructurePhraseTXControl
    extends TControl {
    public OPDComStructurePhraseTXControl() {
    }

    private static final String PHRASE_FILE_PATH = "JHW/片语";
    /**
     * 树名称
     */
    private static final String TREE = "TREE";
    /**
     * 科室下拉列表
     */
    private static final String DEPT_CODE = "DEPT_CODE";

    //TABLE_PHRASE
    private static final String TABLE_PHRASE = "TABLE_PHRASE";

    //进参中科室医师区别
    private String deptOrDr = "";

    //进参中科室、医师代码
    private String deptOrDrCode = "";


    /**
     * 片语代码（主键）
     */
    private static final String CLASS_CODE = "CODE";

    /**
     * 片语名称;
     */
    private static final String PHRASE_CODE = "PHRASE_NAME";
    /**
     * 拼音码;
     */
    private static final String PHRASE_PY1 = "PY1";

    /**
     * 助记码
     */
    private static final String PHRASE_PY2 = "PY2";

    /**
     * 顺序号
     */
    private static final String PHRASE_SEQ = "SEQ";

    /**
     * 是片语标记
     */
    private static final String IS_PHRASE_FLG = "CHK_ISPhrase";
    /**
     * 主片语标记
     */
    private static final String IS_PRM_FLG = "CHK_ISPRIMARY";


    private TTree tree;
    //COMBOBOX 部门
    private TTextFormat deptCombo;
    //
    private TTable table;
    //
    private TTextField classCode;

    //片语分类或片语名称
    private TTextField phraseCode;

    private TTextField py1;

    private TTextField py2;

    private TTextField seq;

    private TCheckBox chkIsPhrase;
    private TCheckBox chkIsPri;

    String userID;
    String userIP;

    public void onInit() {
        super.onInit();
        // this.messageBox("=======com in!==========");
        TPanel p = (TPanel) getComponent();
        //this.messageBox("===deptOrDr==="+deptOrDr);
        if (p.getParameter() != null && !p.getParameter().equals("")) {
            Vector VParm = (Vector) p.getParameter();
            deptOrDr = (String) VParm.get(0);
            if ("1".equalsIgnoreCase(deptOrDr)) {
                deptOrDrCode = (String) VParm.get(1);
            }
            userID = (String) VParm.get(2);
            userIP = (String) VParm.get(3);
            //测试
        }
        else {
            Vector VParm = new Vector();
            VParm.add("1");
            VParm.add("31300");
            VParm.add("000211");
            VParm.add("127.0.0.1");

            deptOrDr = (String) VParm.get(0);
            if ("1".equalsIgnoreCase(deptOrDr)) {
                deptOrDrCode = (String) VParm.get(1);
            }

            userID = (String) VParm.get(2); ;
            userIP = (String) VParm.get(3);
        }

        //初始化树
        onInitTree();
        //初始化科室;
        deptCombo = (TTextFormat)this.getComponent(DEPT_CODE);
        deptCombo.setValue(deptOrDrCode); //.setSelectedID(deptOrDrCode);
        deptCombo.setVisible(true);
        deptCombo.setEnabled(false);

        //
        classCode = (TTextField)this.getComponent(CLASS_CODE);
        phraseCode = (TTextField)this.getComponent(PHRASE_CODE);
        py1 = (TTextField)this.getComponent(PHRASE_PY1);
        py2 = (TTextField)this.getComponent(PHRASE_PY2);
        seq = (TTextField)this.getComponent(PHRASE_SEQ);
        chkIsPhrase = (TCheckBox)this.getComponent(IS_PHRASE_FLG);
        chkIsPri = (TCheckBox)this.getComponent(IS_PRM_FLG);

        //及列表
        table = (TTable)this.getComponent(TABLE_PHRASE);
        //加入事件
        //给TREE添加监听事件
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");

        callFunction("UI|TABLE_PHRASE|addEventListener",
                     "TABLE_PHRASE->" + TTableEvent.CLICKED, this,
                     "onTableClicked");

        //加入双击打开片语编辑
        table.addEventListener("TABLE_PHRASE->" + TTableEvent.DOUBLE_CLICKED, this,
                               "onTableDoubleClicked");

    }

    /**
     * 初始化树
     */
    private void onInitTree() {
        //this.messageBox("==onInitTree==");
        tree = (TTree) callMessage("UI|TREE|getThis");
        tree.getRoot().removeAllChildren();
        TTreeNode treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        //取根节点;
        TParm rootParam = this.getTreeRoot();
        treeRoot.setText(rootParam.getValue("PHRASE_CODE", 0));
        treeRoot.setType("Root");
        treeRoot.setID(rootParam.getValue("CLASS_CODE", 0));
        //
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
                    // System.out.println("phrase class========================"+PhraseClass.getText());
                    treeRoot.findNodeForID(nodesParam.getValue(
                        "PARENT_CLASS_CODE", i)).add(PhraseClass);
                }
            }

        }
        tree.update();

    }

    private TParm getTreeRoot() {
        TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,PHRASE_CODE,LEAF_FLG,PARENT_CLASS_CODE FROM OPD_COMTEMPLATE_PHRASE WHERE PARENT_CLASS_CODE IS NULL"));
        return result;
    }

    private TParm getNodes() {
        TParm result = new TParm(this.getDBTool().select("SELECT CLASS_CODE,PHRASE_CODE,LEAF_FLG,PARENT_CLASS_CODE,seq FROM OPD_COMTEMPLATE_PHRASE ORDER BY CLASS_CODE,SEQ"));
        return result;
    }

    /**
     * 树的单击事件
     */
    public void onTreeClicked() {
        //this.messageBox("==come in=="+getAutoClassCode());
        if (tree.getSelectNode() == null) {
            return;
        }
        //this.messageBox(" select id" + tree.getSelectNode().getID());
        this.clearValue(
            "CODE;PHRASE_NAME;PY1;PY2;SEQ;CHK_ISPhrase;CHK_ISPRIMARY");
        String sql = "SELECT * FROM OPD_COMTEMPLATE_PHRASE WHERE 1=1";
        sql += " AND PARENT_CLASS_CODE='" + tree.getSelectNode().getID() + "'";
        sql += " ORDER BY SEQ";
        //查询选中节点下的，片语分类或片语;
        TParm result = new TParm(this.getDBTool().select(sql));
        //this.messageBox("result=="+result);

        //表格赋值;
        if (result == null || result.getCount() <= 0) {
            //this.messageBox("没有查询数据");
        }
        else {
            table.setParmValue(result);
        }

    }

    /**
     * 单击修改
     * @param row int
     */
    public void onTableClicked(int row) {
        //// System.out.println("=========onTableClicked============" + row);
        if (row < 0) {
            return;
        }
        //赋值；
        //片语ID
        classCode.setValue(table.getParmValue().getValue("CLASS_CODE", row));
        phraseCode.setValue(table.getParmValue().getValue("PHRASE_CODE", row));
        py1.setValue(table.getParmValue().getValue("PY1", row));
        py2.setValue(table.getParmValue().getValue("PY2", row));
        seq.setValue(table.getParmValue().getValue("SEQ", row));
        chkIsPhrase.setValue(table.getParmValue().getValue("LEAF_FLG", row));
        chkIsPri.setValue(table.getParmValue().getValue("MAIN_FLG", row));
    }

    /**public void onEntryGetPY(){
        String py1 = TMessage.getPy(phraseCode.getValue());
        py1.split(py1);
         }**/
    /**
     * 拼音事件
     */
    public void onPY() {
        String py1 = StringUtil.onCode(this.getValueString("PHRASE_NAME"));
        this.setValue("PY1", py1);
    }

    /**
     * 保存操作;
     */
    public void onSave() {
        int selected = table.getSelectedRow();
        //校检;
        if (phraseCode.getValue() == null || phraseCode.getValue().equals("")) {
            this.messageBox("请填写名称！");
            return;
        }
        String selectedId = tree.getSelectionNode().getID();
        if (selectedId == null || selectedId.equals("")) {
            this.messageBox("请选择模版分类！");
            return;
        }
        //是叶节点(是片语)
        if (isPhrase(selectedId)) {
            this.messageBox("模版节点下不能增加！");
            return;
        }
        //this.messageBox("===selectedId==="+selectedId);

        String mainClass = getMainCode(selectedId);
        //this.messageBox("===mainClass==="+mainClass);


        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy/MM/dd hh:mm:ss");

        //this.messageBox("py"+TMessage.getPy(phraseCode.getValue()));

        //String py1 = TMessage.getPy(phraseCode.getValue());
        //新增
        if (selected == -1) {
            String id = this.getAutoClassCode();
            String sql = "INSERT INTO OPD_COMTEMPLATE_PHRASE(CLASS_CODE,PHRASE_CODE,DEPT_OR_DR,DEPTORDR_CODE,";
            sql +=
                "PY1,PY2,SEQ,LEAF_FLG,MAIN_FLG,PARENT_CLASS_CODE,OPT_USER,OPT_DATE,OPT_TERM,MAIN_CLASS_CODE)";
            sql += " VALUES('" +
                id + "','" +
                phraseCode.getValue() + "','" +
                deptOrDr + "','" +
                deptOrDrCode + "','" +
                py1.getValue() + "','" +
                py2.getValue() + "','" +
                seq.getValue() + "','" +
                chkIsPhrase.getValue() + "','" +
                chkIsPri.getValue() + "','" +
                tree.getSelectionNode().getID() + "','" +
                userID + "',TO_DATE('" + sysDate +
                "','YYYY/MM/DD HH24:MI:SS') ,'" + userIP + "','" + mainClass +
                "')";

            //// System.out.println("=====INSERT INTO sql======" + sql);
            TParm parm = new TParm(this.getDBTool().update(sql));
            if (parm.getErrCode() < 0) {
                this.messageBox("保存失败！");
                return;

            }
            else {
                this.messageBox("保存成功！");
            }
            //修改;
        }
        else {
            //this.messageBox("chkIsPri.getValue()" + chkIsPri.getValue());
            String sql = "UPDATE OPD_COMTEMPLATE_PHRASE SET PHRASE_CODE='" +
                phraseCode.getValue() + "',";
            sql += "PY1='" + py1.getValue() + "',";
            sql += "PY2='" + py2.getValue() + "',";
            sql += "SEQ='" + seq.getValue() + "',";
            sql += "LEAF_FLG='" + chkIsPhrase.getValue() + "',";
            sql += "MAIN_FLG='" + chkIsPri.getValue() + "',";
            sql += "OPT_USER='" + userID + "',";
            sql += "OPT_DATE=TO_DATE('" + sysDate +
                "','YYYY/MM/DD HH24:MI:SS') ,";
            sql += "OPT_TERM='" + userIP + "',";
            sql += "MAIN_CLASS_CODE='" + mainClass + "'";
            sql += " WHERE CLASS_CODE='" + classCode.getValue() + "'";
            //// System.out.println("====update sql=====" + sql);
            TParm parm = new TParm(this.getDBTool().update(sql));
            if (parm.getErrCode() < 0) {
                this.messageBox("保存失败！");
                return;

            }
            else {
                this.messageBox("保存成功！");
            }

        }
        //刷新表格；
        onTreeClicked();
        //刷新树节点;
        //tree.update();
        TTreeNode selectNode = tree.getSelectNode();
        onInitTree();
        tree.expandRow(selectNode);
    }

    /**
     *编辑事件
     */
    public void onEdit() {
        int row = table.getSelectedRow();
        onTableDoubleClicked(row);
    }

    /**
     * 清空操作;
     */
    public void onClear() {
        this.clearValue(
            "CODE;PHRASE_NAME;PY1;PY2;SEQ;CHK_ISPhrase;CHK_ISPRIMARY");
        table.clearSelection();
    }

    /**
     * 删除功能
     */
    public void onDelete() {
        //删除数据库
        if (this.messageBox("询问", "是否删除", 2) == 0) {

            if (table.getSelectedRow() == -1) {
                return;
            }
            int selRow = table.getSelectedRow();
            TParm tableParm = table.getParmValue();
            String id = tableParm.getValue("CLASS_CODE", selRow);

            //判断是否有子节点，如果有子节点， 不能删除
            String selectSQL =
                "SELECT CLASS_CODE FROM OPD_COMTEMPLATE_PHRASE WHERE PARENT_CLASS_CODE='" +
                id + "'";
            TParm childs = new TParm(this.getDBTool().select(selectSQL));
            //有子节点
            if (childs.getCount() > 0) {
                this.messageBox("包含子模版，不能删除！");
                return;
            }

            //FILE_PATH;FILE_NAME
            String filePath = tableParm.getValue("FILE_PATH", selRow);
            String fileName = tableParm.getValue("FILE_NAME", selRow);

            String sql =
                "DELETE FROM  OPD_COMTEMPLATE_PHRASE  WHERE CLASS_CODE ='" + id +
                "'";

            TParm result = new TParm(this.getDBTool().update(sql));
            if (result.getErrCode() < 0) {
                //messageBox(result.getErrText());
                this.messageBox("删除失败！");
                return;
            }
            //成功则删除对应片语文件;
            this.delFileTempletFile(filePath, fileName);
            //删除单行显示
            int row = (Integer) callFunction("UI|TABLE_PHRASE|getSelectedRow");
            if (row < 0) {
                return;
            }
            this.callFunction("UI|TABLE_PHRASE|removeRow", row);
            this.callFunction("UI|TABLE_PHRASE|setSelectRow", row);

            this.messageBox("删除成功！");
            this.onClear();
            onInitTree();

        }
        else {
            return;
        }

    }


    /**
     * 表格双击事件，打开EMR片语编辑窗体
     * @param row int
     */
    public void onTableDoubleClicked(int row) {
        //打开结构化片语编辑窗体;
        if (row < 0) {
            return;
        }
        //不是片语
        if (!table.getParmValue().getValue(
            "LEAF_FLG", row).equals("Y")) {
            return;
        }
        //是否存在已保存路径
        //NEW|EDIT
        String seletedPhraseFilePath = table.getParmValue().getValue(
            "FILE_PATH", row);
        String seletedPhraseFileName = table.getParmValue().getValue(
            "FILE_NAME", row);
        /**this.messageBox("===onTableDoubleClicked seletedPhraseFilePath===" +
                         seletedPhraseFilePath);**/

        TParm inParm = new TParm();
        //修改
        if (seletedPhraseFilePath != null && !seletedPhraseFilePath.equals("")) {
            inParm.setData("opType", "EDIT");
            inParm.setData("phraseFilePath", seletedPhraseFilePath);
            inParm.setData("phraseFileName", seletedPhraseFileName);
        }
        else {
            //新增;
            inParm.setData("opType", "NEW");
            inParm.setData("phraseFilePath", PHRASE_FILE_PATH);
            inParm.setData("phraseFileName",
                           table.getParmValue().getValue("CLASS_CODE", row));
        }

        inParm.setData("classCode",
                       table.getParmValue().getValue("CLASS_CODE", row));
        /**     inParm.setData("deptOrDrCode", deptOrDrCode);
             inParm.setData("phraseCode",
                       table.getParmValue().getValue("PHRASE_CODE", row));**/

        this.openDialog("%ROOT%\\config\\emr\\EMREditComPhrase.x", inParm);
        //刷一下表
        onTreeClicked();

    }

    /**
     * 是否是片语节点;
     * @param nodeID String
     * @return boolean
     */
    private boolean isPhrase(String nodeID) {
        boolean flg = false;
        String sql =
            "SELECT LEAF_FLG FROM OPD_COMTEMPLATE_PHRASE WHERE CLASS_CODE='" +
            nodeID + "'";
        TParm parm = new TParm(this.getDBTool().update(sql));
        if (parm.getValue("LEAF_FLG", 0).equals("Y")) {
            return true;
        }
        return flg;
    }


    /**
     * 自动取classCode;
     * @return String
     */
    private synchronized String getAutoClassCode() {
        /**String s = UUID.randomUUID().toString();
                 //去掉“-”符号
                 return s.substring(0, 8) + s.substring(9, 13) + s.substring
            (14, 18) + s.substring(19, 23) + s.substring(24);**/
        //改成通过自动编码生成
        //利用取号器取值；
        String id = SystemTool.getInstance().getNo("ALL", "ODO",
            "EMRTEMPLATE_NO",
            "EMRTEMPLATE_NO");
        return id;

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
     * 递归取主分类;
     * @param selectedId String
     * @return String
     */
    private String getMainCode(String selectedId) {
        //取selectedId节点下的父ID
        //
        String sql =
            "SELECT CLASS_CODE,PARENT_CLASS_CODE FROM OPD_COMTEMPLATE_PHRASE WHERE 1=1";
        sql += " AND CLASS_CODE=+'" + selectedId + "'";
        TParm result = new TParm(this.getDBTool().select(sql));
        //this.messageBox("PARENT_CLASS_CODE"+result.getValue("PARENT_CLASS_CODE", 0));
        if (result.getValue("PARENT_CLASS_CODE", 0).equals("ROOT") ||
            result.getValue("PARENT_CLASS_CODE", 0).equals("")) {
            return result.getValue("CLASS_CODE", 0);
        }
        else {
            return this.getMainCode(result.getValue("PARENT_CLASS_CODE", 0));
        }
        //return "";
    }


    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    private TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * 清空系统剪贴板
     */
    public void onClearMenu() {
        CopyOperator.clearComList();
    }


}
