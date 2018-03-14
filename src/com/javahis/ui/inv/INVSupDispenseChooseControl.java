package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.ui.TCheckBox;
import com.dongyang.data.TParm;
import jdo.inv.InvSupDispenseMTool;
import jdo.inv.INVSQL;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: 供应室回收引用出库单</p>
 *
 * <p>Description: 供应室回收引用出库单</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.3.16
 * @version 1.0
 */
public class INVSupDispenseChooseControl
    extends TControl {
    public INVSupDispenseChooseControl() {
    }

    // 出库主表
    private TTable tableM;
    // 耗材出库
    private TTable tableD;

    /**
     * 初始化方法
     */
    public void onInit() {
        tableM = getTable("TABLEM");
        tableD = getTable("TABLED");

        // 请领日期
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setData("START_DATE", this.getValue("START_DATE"));
        parm.setData("END_DATE", this.getValue("END_DATE"));
        parm.setData("PACK_MODE", "1");
        parm.setData("UPDATE_FLG_A", "Y");
        if (!"".equals(this.getValueString("DISPENSE_NO"))) {
            parm.setData("DISPENSE_NO", getValueString("DISPENSE_NO"));
        }
        if (!"".equals(this.getValueString("TO_ORG_CODE"))) {
            parm.setData("TO_ORG_CODE", getValueString("TO_ORG_CODE"));
        }
        if (!"".equals(this.getValueString("FROM_ORG_CODE"))) {
            parm.setData("FROM_ORG_CODE", getValueString("FROM_ORG_CODE"));
        }
        TParm result = InvSupDispenseMTool.getInstance().onQuery(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        tableM.setParmValue(result);
    }


    /**
     * 返回方法
     */
    public void onReturn() {
        tableD.acceptText();
        boolean flg = true;
        for (int i = 0; i < tableD.getRowCount(); i++) {
            if ("Y".equals(tableD.getItemString(i, "SELECT_FLG"))) {
                flg = false;
            }
        }
        if (flg) {
            this.messageBox("没有回传数据");
            return;
        }
        TParm result = new TParm();
        result.setData("DISPENSE_M",
                       tableM.getParmValue().getRow(tableM.getSelectedRow()).
                       getData());
        TParm tableDParm = tableD.getParmValue();
        for (int i = tableDParm.getCount("PACK_CODE") - 1; i >= 0; i--) {
            if (!"Y".equals(tableDParm.getValue("SELECT_FLG", i))) {
                tableDParm.removeRow(i);
            }
        }
        result.setData("DISPENSE_D", tableDParm.getData());
        setReturnValue(result);
        this.closeWindow();
    }

    /**
     * 清空方法
     */
    public void onClear() {
        this.clearValue(
            "FROM_ORG_CODE;DISPENSE_NO;TO_ORG_CODE;SELECT_ALL");
        // 出库日期
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        tableM.removeRowAll();
        tableD.removeRowAll();
    }

    /**
     * 表格单击事件
     */
    public void onTableMClicked() {
        String dispense_no = tableM.getItemString(tableM.getSelectedRow(),
                                                  "DISPENSE_NO");
        String org_code = tableM.getItemString(tableM.getSelectedRow(),
                                               "FROM_ORG_CODE");
        TParm pack_mode_parm = new TParm(TJDODBTool.getInstance().select(INVSQL.
            getIncSupDispenseChoose(dispense_no, org_code)));
        tableD.setParmValue(pack_mode_parm);
    }

    /**
     * 全选事件
     */
    public void onSelectAll() {
        tableD.acceptText();
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < tableD.getRowCount(); i++) {
                tableD.setItem(i, "SELECT_FLG", true);
            }
        }
        else {
            for (int i = 0; i < tableD.getRowCount(); i++) {
                tableD.setItem(i, "SELECT_FLG", false);
            }
        }
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
     * 得到CheckBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }


}
