package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.tui.text.EAssociateChoose;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TButton;
import java.util.Vector;
import com.dongyang.tui.text.EFixed;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTextArea;

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
public class AssociateChooseControl
    extends TControl {

    private EAssociateChoose associateChoose;
    private TTable table;
    private TTextField elmName;
    private TTextField elmValue;
    private TTextFormat emrMainCate;
    private TTextFormat subTemplate;
    private TTextArea  templateDesc;
    private int selectedID = -1;

    /**
     * 初始化
     */
    public void onInit() {
        associateChoose = (EAssociateChoose) getParameter();
        if (associateChoose == null) {
            return;
        }
        setValue("NAME", associateChoose.getName());
        setValue("TEXT", associateChoose.getText());
        setValue("ALLOW_NULL", associateChoose.isAllowNull() ? "Y" : "N");
        setValue("GROUP_NAME", associateChoose.getGroupName());
        setValue("START_TAG", associateChoose.getStartTag());
        setValue("END_TAG", associateChoose.getEndTag());

        setValue("CHK_ISCDA",associateChoose.isIsDataElements()?"Y" : "N");
        setValue("CHK_LOCKED",associateChoose.isLocked()?"Y" : "N");

        elmName = (TTextField) getComponent("TXT_ELM_NAME");
        elmValue = (TTextField) getComponent("TXT_ELM_VALUE");
        emrMainCate = (TTextFormat) getComponent("EMR_MAIN_CATE");
        subTemplate = (TTextFormat) getComponent("SUB_TEMPLATE");
        templateDesc= (TTextArea) getComponent("LAB_DESC");

        table = (TTable) getComponent("TABLE");
        table.setValue(newVector(associateChoose.getData()));
        if (table.getRowCount() > 0) {
            table.setSelectedRow(0);
        }

        /**if (associateChoose.isIsDataElements()) {

            TTextField tf_name = (TTextField) getComponent("NAME");
            TTextField tf_text = (TTextField) getComponent("TEXT");
            TButton btn0 = (TButton) getComponent("BTN_ADD");
            TButton btn1 = (TButton) getComponent("BTN_INS");
            TButton btn2 = (TButton) getComponent("BTN_DEL");
            TCheckBox ck_text = (TCheckBox) getComponent("ALLOW_NULL");
            TTextField tf_groupname = (TTextField) getComponent("GROUP_NAME");

            tf_name.setEditable(false);
            //tf_text.setEditable(false);
            //btn0.setEnabled(false);
            //btn1.setEnabled(false);
            //btn2.setEnabled(false);
            ck_text.setEnabled(false);
            tf_groupname.setEditable(false);

        }**/
        callFunction("UI|Table|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");

    }


    /**
     * Vector 克隆
     * @param v Vector
     * @return Vector
     */
    public Vector newVector(Vector v) {
        Vector data = new Vector();
        for (int i = 0; i < v.size(); i++) {
            Vector t = (Vector) v.get(i);
            Vector t1 = new Vector();
            data.add(t1);
            for (int j = 0; j < t.size(); j++) {
                t1.add(t.get(j));
            }
        }
        return data;
    }

    /**
     * 新增
     */
    public void onNew() {
        //全清
        elmName.setValue("");
        elmValue.setValue("");
        emrMainCate.setValue("");
        subTemplate.setValue("");
        selectedID = -1;
        //
        /**Vector v = new Vector();
                 v.add("");
                 v.add("");
                 v.add("");
                 v.add("");
                 table.insertRowValue(table.getRowCount(), v);
                 table.setSelectedRow(table.getRowCount() - 1);**/
    }

    /**
     * 保存
     */
    public void onSave() {
        //check
        /**if(elmName.getValue().equals("")){
            this.messageBox("");
            return;
                 }**/

        Vector v = new Vector();
        v.add(elmName.getValue());
        v.add(elmValue.getValue());
        v.add(emrMainCate.getValue());
        v.add(subTemplate.getValue());
        //this.messageBox("elmrMainCat===="+emrMainCate.getValue());
        //this.messageBox("subTemplate===="+subTemplate.getValue());
        //新增;
        if (selectedID == -1) {
            table.insertRowValue(table.getRowCount(), v);
            //table.setSelectedRow(table.getRowCount() - 1);
            table.clearSelection();
            onNew();
        }
        else {
            //修改;
            table.setRowValue(selectedID, v);
        }

    }

    /**
     * 插入
     */
    public void onInsert() {
        int row = table.getSelectedRow();
        if (row == -1) {
            //onNew();
            onSave();
            return;
        }
        Vector v = new Vector();
        v.add(elmName.getValue());
        v.add(elmValue.getValue());
        v.add(emrMainCate.getValue());
        v.add(subTemplate.getValue());

        table.insertRowValue(row, v);
    }

    /**
     * 删除
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }
        table.removeRow(row);
        if (table.getRowCount() == 0) {
            return;
        }
        if (row >= table.getRowCount()) {
            row = table.getRowCount() - 1;
        }
        table.setSelectedRow(row);
    }

    /**
     * 上移
     */
    public void onUp() {
        int row = table.getSelectedRow();
        if (row <= 0) {
            return;
        }
        Vector v = new Vector();
        v.add(table.getValueAt(row - 1, 0));
        v.add(table.getValueAt(row - 1, 1));
        v.add(table.getValueAt(row - 1, 2));
        v.add(table.getValueAt(row - 1, 3));
        table.insertRowValue(row + 1, v);
        table.removeRow(row - 1);
        table.setSelectedRow(row - 1);
    }

    /**
     * 下移
     */
    public void onDown() {
        int row = table.getSelectedRow();
        if (row == -1 || row == table.getRowCount() - 1) {
            return;
        }
        Vector v = new Vector();
        v.add(table.getValueAt(row + 1, 0));
        v.add(table.getValueAt(row + 1, 1));
        v.add(table.getValueAt(row + 1, 2));
        v.add(table.getValueAt(row + 1, 3));
        table.removeRow(row + 1);
        table.insertRowValue(row, v);
        table.setSelectedRow(row + 1);
    }

    /**
     * 确定
     */
    public void onOK() {
        table.acceptText();
        if (getText("TEXT").length() == 0) {
            this.messageBox_("文本不能为空!");
            return;
        }

        if (getText("START_TAG").length() == 0) {
            this.messageBox_("开始元素不能为空!");
            return;
        }

        if (getText("END_TAG").length() == 0) {
            this.messageBox_("结束元素不能为空!");
            return;
        }

        associateChoose.setGroupName(getText("GROUP_NAME"));
        associateChoose.setName(getText("NAME"));
        associateChoose.setText(getText("TEXT"));
        associateChoose.setModify(true);
        associateChoose.setData(table.getValue());
        associateChoose.setAllowNull(getValueBoolean("ALLOW_NULL"));
        associateChoose.setStartTag(getText("START_TAG"));
        associateChoose.setEndTag(getText("END_TAG"));
        if(getValue("CHK_ISCDA").equals("Y")){
            associateChoose.setDataElements(true);
        }else{
             associateChoose.setDataElements(false);
        }
        associateChoose.setLocked(getValueBoolean("CHK_LOCKED"));

        setReturnValue("OK");
        closeWindow();
    }

    /**
     * 双击
     * @param row int
     */
    public void onTableClicked(int row) {

        if (row < 0) {
            return;
        }
        elmName.setValue( (String) table.getValueAt(row, 0));
        elmValue.setValue( (String) table.getValueAt(row, 1));
        emrMainCate.setValue( (String) table.getValueAt(row, 2));
        subTemplate.setValue( (String) table.getValueAt(row, 3));

        //this.messageBox("row"+row);
        selectedID = row;

    }
    /**
     * 关联子模版变化，显示模版备注;
     */
    public void onSubTemplateChangeValue(){
        String id=(String)subTemplate.getValue();
        //this.messageBox("==id=="+id);
        this.getTemplateDesc(id);
    }

    /**
     * 通过ID获得设置子模版备注信息;
     * @param templateID String
     * @return String
     */
    private void getTemplateDesc(String templateID) {
        String sql =
            "SELECT TEMPLATE_DESC FROM OPD_COMTEMPLATE_PHRASE WHERE 1=1";
        sql += " AND CLASS_CODE='" + templateID + "'";
        TParm result = new TParm(this.getDBTool().select(sql));
        templateDesc.setText(result.getValue("TEMPLATE_DESC",0));
    }

    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }


}
