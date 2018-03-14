package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TCheckBox;
import com.dongyang.data.TParm;
import jdo.inv.InvPackStockMTool;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.jdo.TJDODBTool;
import jdo.inv.INVSQL;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import java.util.Map;
import java.util.HashMap;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TypeTool;
import com.dongyang.manager.TIOM_AppServer;
import java.util.Vector;

/**
 * <p>Title: 消毒与回收</p>
 *
 * <p>Description: 消毒与回收</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.3.12
 * @version 1.0
 */
public class INVBackPackAndDisnfectionControl
    extends TControl {

    // 主表
    private TTable tableM;
    // 细表
    private TTable tableD;
    // 存储表更记录
    private Map map;

    public INVBackPackAndDisnfectionControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        tableM = getTable("TABLEM");
        tableD = getTable("TABLED");
        // 添加侦听事件
        addEventListener("TABLEM->" + TTableEvent.CHANGE_VALUE,
                         "onTableMChangeValue");
        // 添加侦听事件
        addEventListener("TABLED->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // 给TABLEDEPT中的CHECKBOX添加侦听事件
        callFunction("UI|TABLEM|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");

        map = new HashMap();

        TParm packParm = new TParm();
        String[] reqPackD = {
            "SELECT_FLG", "PACK_DESC", "PACK_SEQ_NO", "QTY", "STATUS",
            "DISINFECTION_DATE", "VALUE_DATE", "DISINFECTION_USER",
            "USE_COST", "ONCE_USE_COST", "PACK_CODE"};
        for (int i = 0; i < reqPackD.length; i++) {
            packParm.setData(reqPackD[i], new Vector());
        }
        tableD.setParmValue(packParm);

    }

    /**
     *
     */
    public void onOrgCodeAction() {
        TParm parm = new TParm();
        parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        //parm.setData("STATUS", "1");
        // 设置弹出菜单
        getTextField("PACK_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\inv\\INVPackStockMPopup.x"), parm);
        // 定义接受返回值方法
        getTextField("PACK_CODE").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn");
    }

    /**
     *
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("请选择供应室部门");
            getTextField("PACK_CODE").setValue("");
            return;
        }

        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        this.setValue("PACK_CODE", parm.getValue("PACK_CODE"));
        this.setValue("PACK_DESC", parm.getValue("PACK_DESC"));
        this.setValue("PACK_SEQ_NO", parm.getValue("PACK_SEQ_NO"));
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("请选择供应室部门");
            return;
        }
        if ("".equals(this.getValueString("PACK_CODE"))) {
            this.messageBox("请选择手术包代码");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        parm.setData("PACK_CODE", this.getValueString("PACK_CODE"));
        parm.setData("PACK_SEQ_NO", this.getValueInt("PACK_SEQ_NO"));

        TParm result = InvPackStockMTool.getInstance().onQueryStockM(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        for (int i = 0; i < result.getCount("PACK_CODE"); i++) {
            result.addData("SELECT_FLG", "N");
            result.setData("QTY", i, 1);
        }
        tableM.setParmValue(result);
        map = new HashMap();
    }

    /**
     * 保存方法
     */
    public void onSave() {
        //检查消毒有效期
        if (!checkValueDate()) {
            return;
        }
        tableM.acceptText();
        TParm tableParm = tableM.getParmValue();
        int pack_seq_no = 0;
        String status = "1";
        for (int i = 0; i < tableParm.getCount("PACK_CODE"); i++) {
            pack_seq_no = tableParm.getInt("PACK_SEQ_NO", i);
            status = tableParm.getValue("STATUS", i);
            if (pack_seq_no != 0 && !"1".equals(status)) {
                this.messageBox("手术包状态不正确不可回收");
                return;
            }
        }

        for (int i = tableParm.getCount("PACK_CODE") - 1; i >= 0; i--) {
            if (!"Y".equals(tableM.getItemString(i, "SELECT_FLG"))) {
                tableParm.removeRow(i);
            }
        }
        if (tableParm.getCount("PACK_CODE") <= 0) {
            this.messageBox("没有保存数据");
            return;
        }

        // 检核一般物资库存
        if (!checkData()) {
            return;
        }

        String org_code = this.getValueString("ORG_CODE");
        Timestamp datetime = SystemTool.getInstance().getDate();
        String disinfection = datetime.toString();
        disinfection = disinfection.substring(0, 4) + disinfection.substring(5, 7) +
            disinfection.substring(8, 10) + disinfection.substring(11, 13) +
            disinfection.substring(14, 16) + disinfection.substring(17, 19);
        // 消毒回收
        TParm disinfectionParm = new TParm();
        for (int i = 0; i < tableParm.getCount("PACK_CODE"); i++) {
            if (!"Y".equals(tableM.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            disinfectionParm.addData("ORG_CODE", org_code);
            disinfectionParm.addData("PACK_CODE",
                                     tableParm.getValue("PACK_CODE", i));
            disinfectionParm.addData("PACK_SEQ_NO",
                                     tableParm.getInt("PACK_SEQ_NO", i));
            disinfectionParm.addData("DISINFECTION_DATE", disinfection);
            disinfectionParm.addData("DISINFECTION", datetime);
            disinfectionParm.addData("QTY", tableParm.getDouble("QTY", i));
            disinfectionParm.addData("VALUE_DATE",
                                     tableM.getItemTimestamp(i, "VALUE_DATE"));
            disinfectionParm.addData("DISINFECTION_USER", Operator.getID());
            disinfectionParm.addData("OPT_USER", Operator.getID());
            disinfectionParm.addData("OPT_DATE", datetime);
            disinfectionParm.addData("OPT_TERM", Operator.getIP());
            if (this.getValueInt("PACK_SEQ_NO") == 0) {
                // 无序号管理手术包回收
                disinfectionParm.addData("STOCK_QTY",
                                         tableParm.getDouble("QTY", i));
            }
            else {
                //序号管理手术包回收
                disinfectionParm.addData("STOCK_QTY", 0);
            }
            disinfectionParm.addData("STATUS", "0");
        }

        TParm parm = new TParm();
        parm.setData("INV_DISINFECTION", disinfectionParm.getData());
        parm.setData("MAP", map);
        //System.out.println("parm===" + parm);
        TParm result = TIOM_AppServer.executeAction(
            "action.inv.INVBackPackAndDisnfectionAction", "onInsert", parm);
        // 保存判断
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        this.onPrint();
        this.onClear();
    }

    /**
     * 检查消毒有效期
     * @return boolean
     */
    private boolean checkValueDate() {
        for (int i = 0; i < tableM.getRowCount(); i++) {
            if (!"Y".equals(tableM.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            if ("".equals(tableM.getItemString(i, "VALUE_DATE"))) {
                this.messageBox("请填写消毒有效期");
                return false;
            }
        }
        return true;
    }

    /**
     * 检核一般物资库存
     * @return boolean
     */
    private boolean checkData() {
        for (int i = 0; i < tableM.getRowCount(); i++) {
            if (!"Y".equals(tableM.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            TParm parm = tableM.getParmValue().getRow(i); 
            String sql = INVSQL.getINVPackStockDInfo(
                getValueString("ORG_CODE"), parm.getValue("PACK_CODE"),
                parm.getInt("PACK_SEQ_NO"), parm.getDouble("QTY"),parm.getInt("BATCH_NO"));
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            for (int j = 0; j < result.getCount("INV_CODE"); j++) {
                TParm stockMParm = new TParm(TJDODBTool.getInstance().select(
                    INVSQL.getINVStockQty(getValueString("ORG_CODE"),
                                          result.getValue("INV_CODE", j))));
                if (stockMParm.getDouble("STOCK_QTY", 0) <
                    result.getDouble("QTY", j)) {
                    this.messageBox("物资" +
                                    stockMParm.getValue("INV_CHN_DESC", 0) +
                                    "库存不足");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 清空方法
     */
    public void onClear() {
        onOrgCodeAction();
        this.clearValue("ORG_CODE;PACK_CODE;PACK_SEQ_NO;PACK_DESC;SELECT_ALL");
        this.setValue("Disnfection", "Y");
        this.setValue("BACK", "Y");
        tableM.removeRowAll();
        tableD.removeRowAll();
        map = new HashMap();
    }

    /**
     * 引入出库单
     */
    public void onExport() {
        Object obj = this.openDialog(
            "%ROOT%\\config\\inv\\INVSupDispenseChoose.x");
        if (obj == null)
            return;
        TParm parm = (TParm) obj;
        //System.out.println("parm===" + parm);
        TParm dispense_M = parm.getParm("DISPENSE_M");
        TParm dispense_D = parm.getParm("DISPENSE_D");
        this.setValue("ORG_CODE", dispense_M.getValue("FROM_ORG_CODE"));

        TParm parmD = new TParm();
        for (int i = 0; i < dispense_D.getCount("PACK_CODE"); i++) {
            parmD.addData("SELECT_FLG", "N");
            parmD.addData("PACK_DESC", dispense_D.getValue("PACK_DESC", i));
            parmD.addData("PACK_SEQ_NO", dispense_D.getInt("PACK_SEQ_NO", i));
            parmD.addData("QTY", dispense_D.getDouble("QTY", i));
            parmD.addData("STATUS", dispense_D.getValue("STATUS", i));
            parmD.addData("DISINFECTION_DATE", "");
            parmD.addData("VALUE_DATE", "");
            parmD.addData("DISINFECTION_USER", "");
            parmD.addData("USE_COST", dispense_D.getDouble("USE_COST", i));
            parmD.addData("ONCE_USE_COST", dispense_D.getDouble("ONCE_USE_COST", i));
            parmD.addData("PACK_CODE", dispense_D.getValue("PACK_CODE", i));
        }
        tableM.setParmValue(parmD);
    }

    /**
     * 打印方法
     */
    public void onPrint() {
        if (tableM.getRowCount() <= 0) {
            this.messageBox("没有打印数据");
            return;
        }
        TParm parm = tableM.getParmValue();
        ////////////////////////////////////
    }

    /**
     * 全选方法
     */
    public void onSelectAll() {
        tableM.acceptText();
        Timestamp datetime = SystemTool.getInstance().getDate();
        String user = Operator.getID();
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < tableM.getRowCount(); i++) {
                tableM.setItem(i, "SELECT_FLG", true);
                tableM.setItem(i, "DISINFECTION_DATE", datetime);
                tableM.setItem(i, "DISINFECTION_USER", user);
                tableM.setItem(i, "VALUE_DATE", StringTool.rollDate(datetime, 1));
            }
        }
        else {
            for (int i = 0; i < tableM.getRowCount(); i++) {
                tableM.setItem(i, "SELECT_FLG", false);
                tableM.setItem(i, "DISINFECTION_DATE", "");
                tableM.setItem(i, "DISINFECTION_USER", "");
                tableM.setItem(i, "VALUE_DATE", "");
            }
        }
    }

    /**
     * 主表单击事件
     */
    public void onTableMClicked() {
        TParm parm = tableM.getParmValue().getRow(tableM.getSelectedRow());
        String sql = INVSQL.getINVPackStockDInfo(   
            getValueString("ORG_CODE"), parm.getValue("PACK_CODE"),
            parm.getInt("PACK_SEQ_NO"), parm.getDouble("QTY"),parm.getInt("BATCH_NO"));
        //System.out.println("sql=="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        tableD.setParmValue(result);
        // 显示表更记录
        String org_code = this.getValueString("ORG_CODE");
        String pack_code = parm.getValue("PACK_CODE");
        int pack_seq_no = parm.getInt("PACK_SEQ_NO");
        String inv_code = "";
        String key = "";
        int value = 0;
        for (int i = 0; i < result.getCount("INV_CODE"); i++) {
            inv_code = result.getValue("INV_CODE", i);
            key = org_code + "|" + pack_code + "|" + pack_seq_no + "|" +
                inv_code;
            if (!map.containsKey(key)) {
                continue;
            }
            value = TypeTool.getInt(map.get(key));
            tableD.setItem(i, "RECOUNT_TIME", value);
            tableD.getParmValue().setData("RECOUNT_TIME", i, value);
        }
    }

    /**
     * 表格值改变事件
     *
     * @param obj
     *            Object
     */
    public boolean onTableMChangeValue(Object obj) {
        double pack_seq_no = tableM.getItemDouble(tableM.getSelectedRow(),
                                                  "PACK_SEQ_NO");
        // 值改变的单元格
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // 判断数据改变
        if (node.getValue().equals(node.getOldValue()))
            return true;
        int column = node.getColumn();
        if (column == 3) {
            double qty = TypeTool.getDouble(node.getValue());
            if (pack_seq_no > 0) {
                if (qty != 1) {
                    this.messageBox("消毒回收数量只能等于为1");
                    return true;
                }
            }
            else {
                if (qty <= 0) {
                    this.messageBox("消毒回收数量不能小于或等于0");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 表格值改变事件
     *
     * @param obj
     *            Object
     */
    public boolean onTableDChangeValue(Object obj) {
        // 值改变的单元格
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // 判断数据改变
        if (node.getValue().equals(node.getOldValue()))
            return true;
        int column = node.getColumn();
        int row_m = tableM.getSelectedRow();
        int row_d = node.getRow();
        if (column == 6) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty < 0) {
                this.messageBox("折损次数不能小于0");
                return true;
            }
            //存储表更记录
            TParm tableMParm = tableM.getParmValue();
            TParm tableDParm = tableD.getParmValue();
            String org_code = this.getValueString("ORG_CODE");
            String pack_code = tableMParm.getValue("PACK_CODE", row_m);
            int pack_seq_no = tableMParm.getInt("PACK_SEQ_NO", row_m);
            String inv_code = tableDParm.getValue("INV_CODE", row_d);
            String key = org_code + "|" + pack_code + "|" + pack_seq_no + "|" +
                inv_code;
            map.put(key, node.getValue());
            return false;
        }
        return true;
    }

    /**
     * 表格(TABLE)复选框改变事件
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        tableM.acceptText();
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
}
