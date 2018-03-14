package com.javahis.ui.mro;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TMenuItem;
import com.dongyang.jdo.TDataStore;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.TTextField;
import com.dongyang.util.TMessage;

/**
 * <p>Title: 方法类型档</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2011.4.27
 * @version 1.0
 */
public class MROMethodTypeControl
    extends TControl {

    private String action = "save";
    // 主项表格
    private TTable table;

    public MROMethodTypeControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        initPage();
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 初始化Table
        table = getTable("TABLE");
        String sql = " SELECT METHOD_TYPE_CODE, METHOD_TYPE_DESC, "
            + " METHOD_TYPE_ENG_DESC, PY1, PY2, SEQ, DESCRIPTION, "
            + " EMR_CHECK_NULL, TABLE_CHECK_NULL, EMR_CHECK_TIME, "
            + " TABLE_CHECK_TIME, TIME_VALUE, OPT_USER, OPT_DATE, OPT_TERM "
            + " FROM MRO_METHOD_TYPE ORDER BY METHOD_TYPE_CODE, SEQ";
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(sql);
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        // 初始化Table
        table = getTable("TABLE");
        String sql = " SELECT METHOD_TYPE_CODE, METHOD_TYPE_DESC, "
            + " METHOD_TYPE_ENG_DESC, PY1, PY2, SEQ, DESCRIPTION, "
            + " EMR_CHECK_NULL, TABLE_CHECK_NULL, EMR_CHECK_TIME, "
            + " TABLE_CHECK_TIME, TIME_VALUE, OPT_USER, OPT_DATE, OPT_TERM "
            + " FROM MRO_METHOD_TYPE ORDER BY METHOD_TYPE_CODE, SEQ";
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(sql);
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
        String code = getValueString("METHOD_TYPE_CODE");
        if (code.length() > 0) {
            String filterString = "METHOD_TYPE_CODE = '" + code + "'";
            table.setFilter(filterString);
            table.filter();
        }
    }

    /**
     * 保存方法
     */
    public void onSave() {
        int row = 0;
        Timestamp date = StringTool.getTimestamp(new Date());
        if ("save".equals(action)) {
            TTextField combo = (TTextField) getComponent("METHOD_TYPE_CODE");
            boolean flg = combo.isEnabled();
            if (flg) {
                if (!CheckData())
                    return;
                row = table.addRow();
            }
            else {
                row = table.getSelectedRow();
            }

            table.getDataStore().setItem(row, "METHOD_TYPE_CODE",
                                         getValueString("METHOD_TYPE_CODE"));
            table.getDataStore().setItem(row, "METHOD_TYPE_DESC",
                                         getValueString("METHOD_TYPE_DESC"));
            table.setItem(row, "METHOD_TYPE_ENG_DESC",
                          getValueString("METHOD_TYPE_ENG_DESC"));
            table.setItem(row, "PY1", getValueString("PY1"));
            table.setItem(row, "PY2", getValueString("PY2"));
            table.setItem(row, "SEQ", getValueInt("SEQ"));
            table.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));
            table.setItem(row, "EMR_CHECK_NULL",
                          "Y".equals(getValueString("EMR_CHECK_NULL")) ? "Y" :
                          "N");
            table.setItem(row, "TABLE_CHECK_NULL",
                          "Y".equals(getValueString("TABLE_CHECK_NULL")) ? "Y" :
                          "N");
            table.setItem(row, "EMR_CHECK_TIME",
                          "Y".equals(getValueString("EMR_CHECK_TIME")) ? "Y" :
                          "N");
            table.setItem(row, "TABLE_CHECK_TIME",
                          "Y".equals(getValueString("TABLE_CHECK_TIME")) ? "Y" :
                          "N");
            table.setItem(row, "TIME_VALUE",
                          getValueInt("TIME_VALUE"));
            table.setItem(row, "OPT_USER", Operator.getID());
            table.setItem(row, "OPT_DATE", date);
            table.setItem(row, "OPT_TERM", Operator.getIP());
        }
        TDataStore dataStore = table.getDataStore();
        if (dataStore.isModified()) {
            table.acceptText();
            if (!table.update()) {
                messageBox("E0001");
                table.removeRow(row);
                table.setDSValue();
                onClear();
                return;
            }
            table.setDSValue();
        }
        messageBox("P0001");
        table.setDSValue();
    }

    /**
     * 检查数据
     */
    private boolean CheckData() {
        if ("".equals(getValueString("METHOD_TYPE_CODE"))) {
            this.messageBox("类型代码不能为空");
            return false;
        }
        if ("".equals(getValueString("METHOD_TYPE_DESC"))) {
            this.messageBox("类型名称不能为空");
            return false;
        }
        return true;
    }


    /**
     * 清空方法
     */
    public void onClear() {
        this.clearValue("METHOD_TYPE_CODE;METHOD_TYPE_DESC;" +
                        "METHOD_TYPE_ENG_DESC;PY1;PY2;SEQ;DESCRIPTION;TIME_VALUE");
        table.setSelectionMode(0);
        ( (TTextField)this.getComponent("METHOD_TYPE_CODE")).setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "save";
        this.getRadioButton("EMR_CHECK_NULL").setSelected(true);
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        int row = table.getTable().getSelectedRow();
        if (row < 0)
            return;
        table.removeRow(row);
        table.setSelectionMode(0);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "delete";
    }

    /**
     * 单击事件
     */
    public void onTableClick() {
        int row = table.getSelectedRow();
        if (row != -1) {
            TParm parm = table.getDataStore().getRowParm(row);
            String likeNames = "METHOD_TYPE_CODE;METHOD_TYPE_DESC;"
                +"METHOD_TYPE_ENG_DESC;PY1;PY2;SEQ;DESCRIPTION;"
                +"EMR_CHECK_NULL;TABLE_CHECK_NULL;EMR_CHECK_TIME;"
                +"TABLE_CHECK_TIME;TIME_VALUE";
            this.setValueForParm(likeNames, parm);
            ( (TTextField) getComponent("METHOD_TYPE_CODE")).setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            action = "save";
            onChangType();
        }
    }

    /**
     * 单选按钮变换事件
     */
    public void onChangType() {
        if (this.getRadioButton("EMR_CHECK_TIME").isSelected() ||
            this.getRadioButton("TABLE_CHECK_TIME").isSelected()) {
            ( (TNumberTextField)this.getComponent("TIME_VALUE")).setEnabled(true);
        }
        else {
            ( (TNumberTextField)this.getComponent("TIME_VALUE")).setEnabled(false);
        }
    }

    /**
     * 回车事件
     */
    public void onDescAction(){
        String py = TMessage.getPy(this.getValueString("METHOD_TYPE_DESC"));
        setValue("PY1", py);
        ( (TTextField) getComponent("METHOD_TYPE_ENG_DESC")).grabFocus();
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到RadioButton对象
     * @param tagName String
     * @return TRadionButton
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

}
