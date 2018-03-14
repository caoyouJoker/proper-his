package com.javahis.ui.mro;

import com.dongyang.control.*;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import jdo.mro.MROChrtvetstdTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.TMessage;
import com.dongyang.manager.TCM_Transform;
import jdo.sys.SYSRuleTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TTree;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title:病案审核标准 </p>
 *
 * <p>Description: 病案审核标准</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author zhangk 2009-4-29
 * @version 1.0
 */
public class MROChrtvetstdControl extends TControl {
    TParm data;
    int selectRow = -1;
    TParm  selParm ;//所有子节点的默认值
    /**
     * 树根
     */
    private TTreeNode treeRoot;
    /**
     * 编号规则类别工具
     */
    SYSRuleTool ruleTool;
    //添加数据之后显示此类型的最大编号父节点
     private String id = null;
    /**
     * 树的数据放入datastore用于对树的数据管理
     */
//
    TDataStore treeDataStore = new TDataStore();
    public void onInit() {
        super.onInit();
        ((TTable) getComponent("Table")).addEventListener("Table->"
                + TTableEvent.CLICKED, this, "onTableClicked");
        // 初始化树
        onInitSelectTree();
        // 给tree添加监听事件
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        // 给Table添加侦听事件
        // 种树
        onCreatTree();
        onClear();
        showTable();
    }

    /**
     * 初始化树
     */
    public void onInitSelectTree() {
        // 得到树根
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        if (treeRoot == null)
            return;
        // 给根节点添加文字显示
        treeRoot.setText("质量考评标准");
        // 给根节点赋tag
        treeRoot.setType("Root");
        // 设置根节点的id
        treeRoot.setID("");
        // 清空所有节点的内容
        treeRoot.removeAllChildren();
        // 调用树点初始化方法
        callMessage("UI|TREE|update");
    }

    /**
     * 初始化树上的节点
     */
    public void onCreatTree() {
        // 给dataStore赋值
        StringBuffer sql=new StringBuffer();
        sql.append("SELECT RULE_TYPE, CATEGORY_CODE,"
                             + " CATEGORY_CHN_DESC, CATEGORY_ENG_DESC,"
                             + " PY1, PY2, SEQ, DESCRIPTION, DETAIL_FLG,"
                             + " OPT_USER, OPT_DATE, OPT_TERM"
                             +
                             " FROM SYS_CATEGORY WHERE RULE_TYPE='MRO_CHRTVETSTD'");
        treeDataStore.setSQL(sql.toString());
        sql.append(" AND LENGTH(CATEGORY_CODE)=3");
        selParm = new TParm(TJDODBTool.getInstance().select(sql.toString()));
        // 如果从dataStore中拿到的数据小于0
        if (treeDataStore.retrieve() <= 0)
            return;
        // 过滤数据,是质量考评标准数据
        ruleTool = new SYSRuleTool("MRO_CHRTVETSTD");

        if (ruleTool.isLoad()) { // 给树篡节点参数:datastore，节点代码,节点显示文字,,节点排序
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                    "CATEGORY_CODE", "CATEGORY_CHN_DESC", "Path", "SEQ");
            // 循环给树安插节点

            for (int i = 0; i < node.length; i++) {
                treeRoot.addSeq(node[i]);
            }
        }
        // 得到界面上的树对象
        TTree tree = (TTree) callMessage("UI|TREE|getThis");
        // 更新树
        tree.update();
        // 设置树的默认选中节点
        tree.setSelectNode(treeRoot);
    }
    /**
     * 单击树
     *
     * @param parm
     *            Object
     */
    public void onTreeClicked(Object parm) { // 新增按钮不能用
        //callFunction("UI|new|setEnabled", false);
        // 得到点击树的节点对象
        onClear();
        showTable();
        TTreeNode node = (TTreeNode) parm;

        if (node == null)
            return;
        // 得到table对象

        // table接收所有改变值
        //  table.acceptText();
        // 如果点击的是树的根结点
        if (node.getType().equals("Root")) {
        }
        else { // 如果点的不是根结点
            // 拿到当前选中的节点的id值
            String scode="";
            id = node.getID();
            for(int i=0;i<selParm.getCount();i++ ){
                if(id.equals(selParm.getValue("CATEGORY_CODE",i))){
                    scode=selParm.getValue("DESCRIPTION",i);//获得当前节点的分数值
                    break;
                }
            }

            //默认分值显示
            this.setValue("SUMPRICE", scode);
        }
       showExamine(id);
    }
    /**
     * 添加数据显示此类型的最大编号
     */
    public void showExamine(String id){
        // 根据过滤条件过滤Tablet上的数据
           data = MROChrtvetstdTool.getInstance().selectdata(id);
           TTable table = (TTable) callFunction("UI|Table|getThis");
           table.setParmValue(data);
           // 得到父ID
           String maxCode = getMaxCode(data, "EXAMINE_CODE");
           String parentID =id;
           int classify = 1;
           if (parentID.length() > 0)
               classify = ruleTool.getNumberClass(parentID) + 1;
           // 如果是最小节点,可以增加一行
           if (classify > ruleTool.getClassifyCurrent()) {
               String no = ruleTool.getNewCode(maxCode, classify);
               callFunction("UI|EXAMINE_CODE|setEnabled", false);
               this.setValue("EXAMINE_CODE", parentID + no);//显示最大编号
           }

    }
    /**
     * 得到最大的编号
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */

    public String getMaxCode(TParm parm, String columnName) {
        if (parm == null)
            return "";
        int count = parm.getCount();
        String s = "";
        for (int i = 0; i < count; i++) {
            String value = parm.getValue(columnName, i);
            if (StringTool.compareTo(s, value) < 0)
                s = value;
        }
        return s;
    }

    /**
     * 增加对Table的监听
     *
     * @param row
     *            int
     */
    public void onTableClicked(int row) {
        // 选中行
        if (row < 0)
            return;
        setValueForParm(
                "EXAMINE_CODE;TYPE_CODE;EXAMINE_DESC;PY1;PY2;SCORE;SPCFY_DEPT;DESCRIPTION;CHECK_FLG;METHOD_CODE;METHOD_PARM;CHECK_SQL",
                data, row);
        selectRow = row;
        //是否急件
        if (data.getValue("URG_FLG", row).equals("Y"))
            ((TRadioButton)this.getComponent("URG_YES")).setSelected(true);
        else
            ((TRadioButton)this.getComponent("URG_NO")).setSelected(true);
        //在院/出院单选
        if (data.getValue("CHECK_RANGE", row).equals("1"))
            ((TRadioButton)this.getComponent("CHECK_RANGE_1")).setSelected(true);
        else if (data.getValue("CHECK_RANGE", row).equals("2"))
            ((TRadioButton)this.getComponent("CHECK_RANGE_2")).setSelected(true);
        else {
        }
        //门急诊/住院单选
        if (data.getValue("CHECK_RANGE1", row).equals("1"))
            ((TRadioButton)this.getComponent("CHECK_RANGE1_1")).setSelected(true);
        else if (data.getValue("CHECK_RANGE1", row).equals("2"))
            ((TRadioButton)this.getComponent("CHECK_RANGE1_2")).setSelected(true);

        // 不可编辑
        ((TTextField) getComponent("EXAMINE_CODE")).setEnabled(false);
        // 设置删除按钮状态
        ((TMenuItem) getComponent("delete")).setEnabled(true);

        onCheckFlg();
    }

    /**
     * 新增
     */
    public boolean onInsert() {
        if (this.getText("EXAMINE_CODE").trim().length() <= 0 ||
            this.getText("EXAMINE_DESC").trim().length() <= 0) {
            this.messageBox("病案审核规则编码和名称不能为空！");
            return false;
        }
        if (((TComboBox)this.getComponent("TYPE_CODE")).getSelectedID().trim().
            length() <= 0) {
            this.messageBox("请选择所属类别！");
            return false;
        }

        TParm parm = this.getParmForTag("EXAMINE_CODE;TYPE_CODE;EXAMINE_DESC;PY1;PY2;SCORE;SPCFY_DEPT;DESCRIPTION;CHECK_FLG;METHOD_CODE;METHOD_PARM;CHECK_SQL");
        //是否急件
        if (((TRadioButton)this.getComponent("URG_YES")).isSelected())
            parm.setData("URG_FLG", "Y");
        else
            parm.setData("URG_FLG", "N");
        //在院/出院单选
        if (((TRadioButton)this.getComponent("CHECK_RANGE_1")).isSelected())
            parm.setData("CHECK_RANGE", "1");
        else
            parm.setData("CHECK_RANGE", "2");
        //门急诊/住院单选
        if (((TRadioButton)this.getComponent("CHECK_RANGE1_1")).isSelected())
            parm.setData("CHECK_RANGE1", "1");
        else
            parm.setData("CHECK_RANGE1", "2");

        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = MROChrtvetstdTool.getInstance().insertdata(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return false;
        }
        // 显示新增数据
        int row = ((TTable) getComponent("TABLE"))
                  .addRow(
                          parm,
                          "EXAMINE_CODE;TYPE_CODE;EXAMINE_DESC;PY1;PY2;URG_FLG;SCORE;SPCFY_DEPT;CHECK_FLG;CHECK_RANGE;METHOD_CODE;METHOD_PARM;OPT_USER;OPT_DATE;OPT_TERM;CHECK_SQL");

//         int row = data.insertRow();
        data.setRowData(row, parm);

        this.messageBox("添加成功！");
        return true;
    }

    /**
     * 更新
     */
    public boolean onUpdate() {
        if (this.getText("EXAMINE_CODE").trim().length() <= 0 ||
            this.getText("EXAMINE_DESC").trim().length() <= 0) {
            this.messageBox("病案审核规则编码和名称不能为空！");
            return false;
        }
        if (((TComboBox)this.getComponent("TYPE_CODE")).getSelectedID().trim().
            length() <= 0) {
            this.messageBox("请选择所属类别！");
            return false;
        }
        TParm parm = this.getParmForTag("EXAMINE_CODE;TYPE_CODE;EXAMINE_DESC;PY1;PY2;SCORE;SPCFY_DEPT;DESCRIPTION;CHECK_FLG;METHOD_CODE;METHOD_PARM;CHECK_SQL");
        //是否急件
        if (((TRadioButton)this.getComponent("URG_YES")).isSelected())
            parm.setData("URG_FLG", "Y");
        else
            parm.setData("URG_FLG", "N");
        //在院/出院单选
        if (((TRadioButton)this.getComponent("CHECK_RANGE_1")).isSelected())
            parm.setData("CHECK_RANGE", "1");
        else
            parm.setData("CHECK_RANGE", "2");
        //门急诊/住院单选
        if (((TRadioButton)this.getComponent("CHECK_RANGE1_1")).isSelected())
            parm.setData("CHECK_RANGE1", "1");
        else
            parm.setData("CHECK_RANGE1", "2");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());

        TParm result = MROChrtvetstdTool.getInstance().updatedata(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return false;
        }
        // 选中行
        int row = ((TTable) getComponent("Table")).getSelectedRow();
        if (row < 0)
            return false;
        // 刷新，设置末行某列的值
        data.setRowData(row, parm);
        ((TTable) getComponent("Table")).setRowParmValue(row, data);
        this.messageBox("修改成功！");
        return true;
    }

    /**
     * 保存
     */
    public void onSave() {
        boolean istrue = false;
        if (selectRow == -1) {
            istrue = onInsert();
            if (istrue)
                onClear();
            showExamine(id);
            return;
        }
        istrue=onUpdate();
        if (istrue)
            onClear();
    }

    /**
     * 查询
     */
    public void onQuery() {
        if (!"Y".equals(this.getValueString("CHECK_FLG"))) {
            data = MROChrtvetstdTool.getInstance().selectdata(getText(
                    "EXAMINE_CODE").trim());
        } else {
            data = MROChrtvetstdTool.getInstance().selectdata(getText(
                    "EXAMINE_CODE").trim(),
                    this.getRadioButton("CHECK_RANGE_1").isSelected() ? "1" :
                    "2",
                    this.getValueString("METHOD_CODE"));
        }

        // 判断错误值
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ((TTable) getComponent("Table")).setParmValue(data);
    }

    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("EXAMINE_CODE;TYPE_CODE;EXAMINE_DESC;PY1;PY2;SCORE;SPCFY_DEPT;DESCRIPTION;METHOD_CODE;METHOD_PARM;CHECK_SQL");
        this.setValue("CHECK_FLG", "Y");
        this.getRadioButton("CHECK_RANGE_1").setSelected(true);
        this.getRadioButton("CHECK_RANGE1_1").setSelected(true);
        ((TTable) getComponent("Table")).clearSelection();
        selectRow = -1;
        // 设置删除按钮状态
        ((TRadioButton)this.getComponent("URG_NO")).setSelected(true);
        ((TMenuItem) getComponent("delete")).setEnabled(false);
        ((TTextField) getComponent("EXAMINE_CODE")).setEnabled(true);
    }
    public void showTable(){

        data = MROChrtvetstdTool.getInstance().selectdata(getText(
                "EXAMINE_CODE").
                trim());
        // 判断错误值
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ((TTable) getComponent("Table")).setParmValue(data);

}
    /**
     * 删除
     */
    public void onDelete() {
        if (this.messageBox("提示", "是否删除", 2) == 0) {
            if (selectRow == -1)
                return;
            String code = getValue("EXAMINE_CODE").toString();
            TParm result = MROChrtvetstdTool.getInstance().deletedata(code);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            TTable table = ((TTable) getComponent("Table"));
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            this.messageBox("删除成功！");
            onClear();
            showTable();
        } else {
            return;
        }
    }

    /**
     * 启动自动质控
     */
    public void onCheckFlg() {
        if ("Y".equals(this.getValueString("CHECK_FLG"))) {
//            //在院/出院单选
//            getRadioButton("CHECK_RANGE_1").setEnabled(true);
//            getRadioButton("CHECK_RANGE_2").setEnabled(true);
//            //门急诊/住院单选
//            getRadioButton("CHECK_RANGE1_1").setEnabled(true);
//            getRadioButton("CHECK_RANGE1_2").setEnabled(true);
            getComboBox("METHOD_CODE").setEnabled(true);
            getTextField("METHOD_PARM").setEnabled(true);
            getTextField("CHECK_SQL").setEnabled(true);
        } else {
//            //在院/出院单选
//            getRadioButton("CHECK_RANGE_1").setEnabled(false);
//            getRadioButton("CHECK_RANGE_2").setEnabled(false);
//            //门急诊/住院单选
//            getRadioButton("CHECK_RANGE1_1").setEnabled(false);
//            getRadioButton("CHECK_RANGE1_2").setEnabled(false);
            getComboBox("METHOD_CODE").setEnabled(false);
            getTextField("METHOD_PARM").setEnabled(false);
            getTextField("CHECK_SQL").setEnabled(false);
        }
    }

    /**
     * 根据汉字输出拼音首字母
     *
     * @return Object
     */
    public Object onCode() {
        if (TCM_Transform.getString(this.getValue("EXAMINE_DESC")).length() <
            1) {
            return null;
        }
        String value = TMessage.getPy(this.getValueString("EXAMINE_DESC"));
        if (null == value || value.length() < 1) {
            return null;
        }
        this.setValue("PY1", value);
        // 光标下移
        ((TTextField) getComponent("PY1")).grabFocus();
        return null;
    }

    /**
     *
     * @param tag String
     * @return TRadioButton
     */
    public TRadioButton getRadioButton(String tag) {
        return (TRadioButton)this.getComponent(tag);
    }

    /**
     *
     * @param tag String
     * @return TTextFormat
     */
    public TComboBox getComboBox(String tag) {
        return (TComboBox)this.getComponent(tag);
    }

    /**
     *
     * @param tag String
     * @return TTextField
     */
    public TTextField getTextField(String tag) {
        return (TTextField)this.getComponent(tag);
    }
}
