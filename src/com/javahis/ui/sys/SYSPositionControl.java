package com.javahis.ui.sys;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TMessage;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TDataStore;
import jdo.sys.Operator;
import java.sql.Timestamp;
import java.util.Date;
import com.dongyang.manager.TCM_Transform;
import jdo.sys.SYSSQL;

/**
 * <p>
 * Title: 公用系统=>人员管理=>人员职别
 * </p>
 *
 * <p>
 * Description: 人员职别
 * </p>
 *
 * <p>
 * Copyright: Copyright JavaHis (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009-02-11
 * @version JavaHis 1.0
 */
public class SYSPositionControl
    extends TControl {

    public SYSPositionControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 添加侦听事件
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                         "onTableChangeValue");

        // 添加单击事件
        ( (TTable) getComponent("TABLE")).addEventListener("TABLE->"
            + TTableEvent.CLICKED, this, "onTableClecked");
        // 设置删除按钮属性
        ( (TMenuItem) getComponent("delete")).setEnabled(false);

        TTable table = ( (TTable) getComponent("TABLE"));
        table.removeRowAll();
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSPosition());
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
    }

    /**
     * 表格值改变事件
     *
     * @param obj
     *            Object
     * @return boolean
     */
    public boolean onTableChangeValue(Object obj) {
        // 值改变的单元格
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // 判断数据改变
        if (node.getValue().equals(node.getOldValue()))
            return true;
        // Table的列名
        String columnName = node.getTable().getDataStoreColumnName(
            node.getColumn());
        // 改变后的数据
        String value = "" + node.getValue();
        int row = node.getRow();
        // 名称改变带拼音
        if ("POS_CHN_DESC".equals(columnName)) {
            if (value.length() == 0) {
                this.messageBox("职称说明不能为空");
                return true;
            }
            // 给拼音
            String py = TMessage.getPy(value);
            node.getTable().setItem(row, "PY1", py);
            return false;
        }
        if ("POS_CODE".equals(columnName)) {
            if (value.length() == 0) {
                this.messageBox("职称代码不能为空");
                return true;
            }
            // 不能重复
            if (node.getTable().getDataStore()
                .exist("POS_CODE='" + value + "'")) {
                messageBox_("编号" + node.getValue() + "重复!");
                return true;
            }
            return false;
        }
        if ("POS_TYPE".equals(columnName)) {
            if (value.length() == 0) {
                this.messageBox("职称类别不能为空");
                return true;
            }
        }
        return false;
    }

    /**
     * 清空方法
     */
    public void onClear() {
        // 清空POS_CODE，POS_CHN_DESC和POS_TYPE
        this.setValue("POS_CODE", "");
        this.setValue("POS_CHN_DESC", "");
        ( (TComboBox) getComponent("POS_TYPE")).setSelectedIndex(0);
        // 设置删除按钮属性
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
    }

    /**
     * 查询初始化自动执行事件
     */
    public void onQuery() {
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        // 过滤条件
        String value = "";
        String str = getValueString("POS_CODE");
        if (str.length() != 0)
            value += "POS_CODE like '" + str + "%'";
        str = getValueString("POS_CHN_DESC");
        if (str.length() != 0) {
            if (value.length() != 0)
                value += " AND ";
            value += " POS_CHN_DESC like '" + str + "%'";
        }
        str = "" + callFunction("UI|POS_TYPE|getValue");
        if (str.length() != 0) {
            if (value.length() != 0)
                value += " AND ";
            value += "POS_TYPE = '" + str + "'";
        }
        TTable table = (TTable) getComponent("TABLE");
        if (value.length() > 0) {
            table.setFilter(value);
            table.filter();
            return;
        }
        table.setFilter("");
        table.filter();
    }

    /**
     * 新增方法
     */
    public void onNew() {
        TTable table = (TTable) getComponent("TABLE");
        String maxCode = "";
        TDataStore dataStore = table.getDataStore();
        maxCode = getMaxCode(table.getDataStore(), "POS_CODE", dataStore
                             .isFilter() ? dataStore.FILTER : dataStore.PRIMARY);
        // 新添加数据的顺序编号
        int seq = getMaxSeq(table.getDataStore(), "SEQ");
        // 新添加的行号
        int row = table.addRow();
        // 当前选中的行
        table.setSelectedRow(row);
        // 默认职称代码
        table.setItem(row, "POS_CODE", maxCode);
        // 默认顺序编号
        table.setItem(row, "SEQ", seq);
    }

    /**
     * 保存方法
     *
     * @return boolean
     */
    public boolean onSave() {
        Timestamp date = StringTool.getTimestamp(new Date());
        TTable table = (TTable) getComponent("TABLE");
        // 接收文本
        table.acceptText();
        TDataStore dataStore = table.getDataStore();

        // 数据检核
        for (int i = 0; i < dataStore.rowCount(); i++) {
            if ("".equals(dataStore.getItemString(i, "POS_CHN_DESC"))) {
                this.messageBox("职称说明不能为空");
                return false;
            }
            else if ("".equals(dataStore.getItemString(i, "POS_TYPE"))) {
                this.messageBox("职称类别不能为空");
                return false;
            }
        }

        // 获得全部改动的行号
        int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
        // 给固定数据配数据
        for (int i = 0; i < rows.length; i++) {
            //System.out.println(dataStore.getItemData(rows[i], 1));

            dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
            dataStore.setItem(rows[i], "OPT_DATE", date);
            dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
        }
        if (!table.update()) {
            messageBox("E0001");
            return false;
        }
        messageBox("P0001");
        table.setDSValue();
        return true;
    }

    /**
     * 表格单击事件
     */
    public void onTableClecked() {
        TTable table = (TTable) getComponent("TABLE");
        // 获得选中行
        int row = table.getSelectedRow();
        if (row < 0) {
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
            return;
        }
        ( (TMenuItem) getComponent("delete")).setEnabled(true);
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        TTable table = (TTable) getComponent("TABLE");
        if (table.getTable().getSelectedRow() < 0)
            return;
        // 删除指定行
        table.removeRow(table.getTable().getSelectedRow());
        if (table.getRowCount() > 0)
            table.setSelectedRow(0);
        else
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
    }

    /**
     * 得到最大的编号
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @param dbBuffer
     *            String
     * @return String
     */
    public String getMaxCode(TDataStore dataStore, String columnName,
                             String dbBuffer) {
        if (dataStore == null)
            return "";
        // 保存数据量
        int count = dataStore.getBuffer(dbBuffer).getCount();
        // 保存最大号
        String s = "";
        for (int i = 0; i < count; i++) {
            String value = TCM_Transform.getString(dataStore.getItemData(i,
                columnName, dbBuffer));
            // 比较长度
            if (s.length() < value.length()) {
                s = value;
                continue;
            }
            // 长度相等判断大小
            if (s.length() == value.length()) {
                if (StringTool.compareTo(s, value) < 0)
                    s = value;
            }
        }
        String newStr = s;
        // 最大号加1
        s = StringTool.addString(s);
        if (newStr.equals(s)) {
            s = "1" + s;
            return s;
        }
        if (StringTool.compareTo(s, newStr) < 0)
            s = "1" + s;
        return s;
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
    public int getMaxSeq(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return 0;
        // 保存数据量
        int count = dataStore.rowCount();
        // 保存最大号
        int s = 0;
        for (int i = 0; i < count; i++) {
            int value = dataStore.getItemInt(i, columnName);
            // 保存最大值
            if (s < value) {
                s = value;
                continue;
            }
        }
        // 最大号加1
        s++;
        return s;
    }
}
