package com.javahis.ui.inv;

import com.dongyang.ui.TMenuItem;
import jdo.inv.INVSQL;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.control.TControl;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTextField;
import java.util.Date;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.util.TMessage;

/**
 * <p>Title: 物资原因裆</p>
 *
 * <p>Description: 物资原因裆</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangy 2010.04.23
 * @version 1.0
 */
public class INVReasonControl
    extends TControl {
    private String action = "save";

    public INVReasonControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        initPage();
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 初始化Table
        TTable table = getTable("TABLE");
        table.removeRowAll();
        TDataStore dataStroe = new TDataStore();
        dataStroe.setSQL(INVSQL.getReason());
        dataStroe.retrieve();
        table.setDataStore(dataStroe);
        table.setDSValue();

        // 最大号+1(SEQ)
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
    }

    /**
     * 保存方法
     */
    public void onSave() {
        TTable table = getTable("TABLE");
        int row = 0;
        if ("save".equals(action)) {
            TTextField combo = (TTextField) getComponent("REN_CODE");
            boolean flg = combo.isEnabled();
            if (flg) {
                if (!CheckData())
                    return;
                row = table.addRow();
            }
            else {
                row = table.getSelectedRow();
            }
            table.setItem(row, "REN_CODE", getValueString("REN_CODE"));
            table.setItem(row, "REN_DESC", getValueString("REN_DESC"));
            table.setItem(row, "ENNAME", getValueString("ENNAME"));
            table.setItem(row, "DESCRIPTION",
                          getValueString("DESCRIPTION"));
            table.setItem(row, "PY1", getValueString("PY1"));
            table.setItem(row, "PY2", getValueString("PY2"));
            table.setItem(row, "SEQ", getValueString("SEQ"));
            table.setItem(row, "PUR_FLG", getValueString("PUR_FLG"));
            table.setItem(row, "VER_FLG", getValueString("VER_FLG"));
            table.setItem(row, "REG_FLG", getValueString("REG_FLG"));
            table.setItem(row, "REQ_FLG", getValueString("REQ_FLG"));
            table.setItem(row, "GIF_FLG", getValueString("GIF_FLG"));
            table.setItem(row, "RET_FLG", getValueString("RET_FLG"));
            table.setItem(row, "WAS_FLG", getValueString("WAS_FLG"));
            table.setItem(row, "DEL_FLG", getValueString("DEL_FLG"));
            table.setItem(row, "OPT_USER", Operator.getID());
            Timestamp date = StringTool.getTimestamp(new Date());
            table.setItem(row, "OPT_DATE", date);
            table.setItem(row, "OPT_TERM", Operator.getIP());
        }
        TDataStore dataStore = table.getDataStore();
        if (dataStore.isModified()) {
            table.acceptText();
            if (!table.update()) {
                messageBox("E0001");
                return;
            }
            table.setDSValue();
        }
        messageBox("P0001");
        table.setDSValue();
        onClear();
    }

    /**
     * 检查数据
     */
    private boolean CheckData() {
        String reason_code = getValueString("REN_CODE");
        if ("".equals(reason_code)) {
            this.messageBox("原因代码不能为空");
            return false;
        }
        String reason_desc = getValueString("REN_DESC");
        if ("".equals(reason_desc)) {
            this.messageBox("原因说明不能为空");
            return false;
        }
        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(INVSQL.getReason(reason_code));
        dataStore.retrieve();
        if (dataStore.rowCount() > 0) {
            this.messageBox("原因代码重复，无法保存");
            return false;
        }
        return true;
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        String code = getValueString("REN_CODE");
        String filterString = "REN_CODE like '" + code + "%'";
        TDataStore dataStroe = new TDataStore();
        dataStroe.setSQL(INVSQL.getReason());
        dataStroe.retrieve();
        TTable table = getTable("TABLE");
        table.setDataStore(dataStroe);
        table.setDSValue();
        table.setFilter(filterString);
        table.filter();
    }

    /**
     * 清空方法
     */
    public void onClear() {
        getTextField("REN_CODE").setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        String tags =
            "REN_CODE;REN_DESC;ENNAME;DESCRIPTION;PY1;PY2;SEQ;PUR_FLG;"
            + "VER_FLG;REG_FLG;REQ_FLG;GIF_FLG;RET_FLG;WAS_FLG;DEL_FLG";
        clearValue(tags);
        // 最大号+1(SEQ)
        TDataStore dataStroe = getTable("TABLE").getDataStore();
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);
        TTable table = getTable("TABLE");
        table.setSelectionMode(0);
        action = "save";
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        TTable table = getTable("TABLE");
        int row = table.getTable().getSelectedRow();
        if (row < 0)
            return;
        table.removeRow(row);
        table.setSelectionMode(0);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "delete";
    }

    /**
     * TABLE单击事件
     */
    public void onTableClicked() {
        TTable table = getTable("TABLE");
        int row = table.getSelectedRow();
        if (row != -1) {
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            TParm parm = table.getDataStore().getRowParm(row);
            String likeNames =
                "REN_CODE;REN_DESC;ENNAME;DESCRIPTION;PY1;PY2;SEQ;PUR_FLG;"
                + "VER_FLG;REG_FLG;REQ_FLG;GIF_FLG;RET_FLG;WAS_FLG;DEL_FLG";
            this.setValueForParm(likeNames, parm);
            getTextField("REN_CODE").setEnabled(false);
            action = "save";
        }
    }

    /**
     * 原因说明回车事件
     */
    public void onReasonDescAction() {
        String name = getValueString("REN_DESC");
        if (name.length() > 0)
            setValue("PY1", TMessage.getPy(name));
        ( (TTextField) getComponent("ENNAME")).grabFocus();
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
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * 得到最大的编号 +1
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */
    private int getMaxSeq(TDataStore dataStore, String columnName,
                          String dbBuffer) {
        if (dataStore == null)
            return 0;
        // 保存数据量
        int count = dataStore.getBuffer(dbBuffer).getCount();
        // 保存最大号
        int max = 0;
        for (int i = 0; i < count; i++) {
            int value = TCM_Transform.getInt(dataStore.getItemData(i,
                columnName, dbBuffer));
            // 保存最大值
            if (max < value) {
                max = value;
                continue;
            }
        }
        // 最大号加1
        max++;
        return max;
    }

}
