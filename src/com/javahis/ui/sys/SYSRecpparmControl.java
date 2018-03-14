package com.javahis.ui.sys;

import com.dongyang.control.*;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.ui.TTableNode;
import jdo.sys.SYSSQL;

/**
 * <p>Title:票显费用类别 </p>
 *
 * <p>Description:费用类型 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author zhangy  2009.10.12
 * @version 1.0
 */
public class SYSRecpparmControl
    extends TControl {
    /**
     * 初始化界面
     *  @return TParm
     */
    public void onInit() {
        super.onInit();
        //table 的点击
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //给Table添加值改变
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                         "onTableStationChangeValue");
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        String sql = SYSSQL.getBillRecpparm();
        table.getDataStore().setSQL(sql);
        table.getDataStore().retrieve();
        table.setDSValue();
//        System.out.println("ssss"+table.getDataStore().getItemInt(0,0));
    }

    /**
     *增加对Table的点击
     * @param row int
     */
    public void onTableClicked(int row) {
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        callFunction("UI|ADM_TYPE|setValue",
                     table.getDataStore().getRowParm(row).getValue("ADM_TYPE"));
        callFunction("UI|RECPIPT_TYPE|setValue",
                     table.getValueAt(row, table.getSelectedColumn()));
    }

    /**
     * table 的值改变事件
     */
    public boolean onTableStationChangeValue(Object obj) {
        //拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        //如果改变的节点数据和原来的数据相同就不改任何数据
        if (node.getValue().equals(node.getOldValue()))
            return false;
        //拿到table上的parmmap的列名
        String columnName = node.getTable().getDataStoreColumnName(node.
            getColumn());
        //拿到当前改变后的数据
        String value = "" + node.getValue();
        if (value == "" || value.length() == 0)
            return false;
        //费用改变
        if (columnName.startsWith("CHARGE")) {
            String[] columns = node.getTable().getDataStore().getColumns();
            for (int i = 0; i < columns.length; i++) {
                if (!columns[i].startsWith("CHARGE"))
                    continue;
                if (columnName.equals(columns[i]))
                    continue;
                String s = node.getTable().getDataStore().getItemString(node.
                    getRow(),
                    columns[i]);
                if (s != null && s.equals(value)) {
                    messageBox_("编号" + node.getValue() + "重复!");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 保存数据
     * @return TParm
     */
    public boolean onSave() {
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        //接收文本
        table.acceptText();
        if (!CheckChange()) {
            return false;
        }
        Timestamp date = SystemTool.getInstance().getDate();

        //获得全部改动的行号
        int rowsBed[] = table.getModifiedRowsFilter();
        //给固定数据配数据
        for (int i = 0; i < rowsBed.length; i++) {
            table.setItemFilter(rowsBed[i], "OPT_USER", Operator.getID());
            table.setItemFilter(rowsBed[i], "OPT_DATE", date);
            table.setItemFilter(rowsBed[i], "OPT_TERM", Operator.getIP());
        }
        if (!table.update()) {
            messageBox("E0001");
            return false;
        }
        messageBox("P0001");
        return true;
    }

    /**
     * 检核是否有数据变更
     * @return boolean
     */
    public boolean CheckChange() {
        //检查数据变更
        TTable tableBed = (TTable) callFunction("UI|TABLE|getThis");
        if (tableBed.isModified())
            return true;
        return false;
    }

    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("RECP_TYPE;RECPIPT_TYPE");
    }

    /**
     * 是否关闭窗口
     * @return boolean true 关闭 false 不关闭
     */
    public boolean onClosing() {
        // 如果有数据变更
        if (CheckChange())
            switch (this.messageBox("提示信息",
                                    "是否保存", this.YES_NO_CANCEL_OPTION)) {
                //保存
                case 0:
                    if (!onSave())
                        return false;
                    break;
                    //不保存
                case 1:
                    return true;
                    //撤销
                case 2:
                    return false;
            }
        //没有变更的数据
        return true;

    }

    /**
     *
     * @param args String[]
     */
    public static void main(String args[]) {
        com.javahis.util.JavaHisDebug.TBuilder();
//       Operator.setData("admin","HIS","127.0.0.1","C00101");
    }

}
