package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import jdo.inv.InvSupRequestMTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.inv.INVSQL;
import com.dongyang.ui.TCheckBox;

/**
 * <p>Title: 供应室出库引用请领单</p>
 *
 * <p>Description: 供应室出库引用请领单</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.3.8
 * @version 1.0
 */
public class INVSuprequestChooseControl extends TControl{

    // 出库主表
    private TTable tableM;
    // 耗材出库
    private TTable tableD;
    // 出库方式
    private String pack_mode = "0";


    public INVSuprequestChooseControl() {
    }

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
        parm.setData("UPDATE_FLG_B", "Y");
        if (!"".equals(this.getValueString("SUPTYPE_CODE"))) {
            parm.setData("SUPTYPE_CODE", getValueString("SUPTYPE_CODE"));
        }
        if (!"".equals(this.getValueString("REQUEST_NO"))) {
            parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
        }
        if (!"".equals(this.getValueString("TO_ORG_CODE"))) {
            parm.setData("TO_ORG_CODE", getValueString("TO_ORG_CODE"));
        }
        if (!"".equals(this.getValueString("APP_ORG_CODE"))) {
            parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        }
        TParm result = InvSupRequestMTool.getInstance().onQuery(parm);
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
    	//INVSupPackStockDDChooseControl 
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
        result.setData("REQUEST_M",
                       tableM.getParmValue().getRow(tableM.getSelectedRow()).
                       getData());
        TParm tableDParm = tableD.getParmValue();
        for (int i = tableDParm.getCount("INV_CODE") - 1; i >= 0; i--) {
            if (!"Y".equals(tableDParm.getValue("SELECT_FLG", i))) {
                tableDParm.removeRow(i);
            }
        }
        result.setData("REQUEST_D", tableDParm.getData());
        result.setData("PACK_MODE", pack_mode);
        //System.out.println("result==="+result);
        setReturnValue(result);
        this.closeWindow();
    }

    /**
     * 清空方法
     */
    public void onClear() {
        this.clearValue(
            "SUPTYPE_CODE;APP_ORG_CODE;REQUEST_NO;TO_ORG_CODE;SELECT_ALL");
        // 请领日期
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
        String request_no = tableM.getItemString(tableM.getSelectedRow(),
                                                 "REQUEST_NO");
        String suptype_code = tableM.getItemString(tableM.getSelectedRow(),
                                                   "SUPTYPE_CODE");
        TParm pack_mode_parm = new TParm(TJDODBTool.getInstance().select(INVSQL.
            getINVSupType(suptype_code)));
        pack_mode = pack_mode_parm.getValue("PACK_MODE", 0);
        TParm result = new TParm();
        if ("0".equals(pack_mode)) {
            result = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvSupRequestDInv(request_no)));
        }
        else {
            result = new TParm(TJDODBTool.getInstance().select(INVSQL.
                getInvSupRequestDPack(request_no)));
        }
        tableD.setParmValue(result);
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
