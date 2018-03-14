package com.javahis.ui.sys;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import java.sql.Timestamp;
import com.dongyang.jdo.TDataStore;
import com.dongyang.data.TParm;
import jdo.sys.SYSSQL;

/**
 * <p>Title:费用代码管理 </p>
 *
 * <p>Dription:费用代码管理 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author fudw
 * @version 1.0
 */
public class SYSChargeHospCodeControl
    extends TControl {
    TTable table;
    /**
     * 初始化界面
     *  @return TParm
     */
    public void onInit() {
        super.onInit();
        table = (TTable)this.getComponent("TABLE");
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked"); //table 的侦听事件
        //给Table添加侦听事件
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                         "onTableChangeValue");

        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSChargeHosp());
        dataStore.retrieve();
        table.setDataStore(dataStore);
        table.setDSValue();
    }

    /**
     * 根据条件查询数据
     * @return TParm
     */
    public void onQuery() {
        String filter = "";
        //院内费用代码
        String value = getValueString("CHARGE_HOSP_CODE");
        if (value != null && value.length() > 0)
            filter = "CHARGE_HOSP_CODE ='" + value + "'";
        //门诊收据费用
        value = getValueString("OPD_CHARGE_CODE");
        if (value != null && value.length() > 0) {
            if (filter.length() > 0)
                filter += " AND ";
            filter += "OPD_CHARGE_CODE ='" + value + "'";
        }
        //住院收据费用
        value = getValueString("IPD_CHARGE_CODE");
        if (value != null && value.length() > 0) {
            if (filter.length() > 0)
                filter += " AND ";
            filter += "IPD_CHARGE_CODE ='" + value + "'";
        }
        //首页代码
        value = getValueString("MRO_CHARGE_CODE");
        if (value != null && value.length() > 0) {
            if (filter.length() > 0)
                filter += " AND ";
            filter += " MRO_CHARGE_CODE ='" + value + "'";
        }
        //统计代码
        value = getValueString("STA_CHARGE_CODE");
        if (value != null && value.length() > 0) {
            if (filter.length() > 0)
                filter += " AND ";
            filter += " STA_CHARGE_CODE ='" + value + "'";
        }
        //院内费用名称
        value = getValueString("CHARGE_HOSP_DESC");
        if (value != null && value.length() > 0) {
            if (filter.length() > 0)
                filter += " AND ";
            filter += " CHARGE_HOSP_DESC like '" + value + "%'";
        }
        //英文
        value = getValueString("ENG_DESC");
        if (value != null && value.length() > 0) {
            if (filter.length() > 0)
                filter += " AND ";
            filter += " ENG_DESC like '" + value + "%'";
        }

        table.setFilter(filter);
        table.filter();
    }

    /**
     * table点击
     */
    public void onTableCtzClicked() {
        int row = table.getSelectedRow();
        //数据上翻
        TParm parm = table.getDataStore().getRowParm(row);
        setTextValue(parm);
        //身份代码不能用
        setTextEnabled(false);
    }

    /**
     * 界面上不可编辑的控件
     * @param boo boolean
     */
    public void setTextEnabled(boolean boo) {
        callFunction("UI|CHARGE_HOSP_CODE|setEnabled", boo);
    }

    /**
     * 清空
     */
    public void onClear() {
        clearText();
        clearTable(table);
        setTextEnabled(true);
    }

    /**
     * 清空属性
     */
    public void clearText() {
        clearText("CHARGE_HOSP_CODE;CHARGE_HOSP_DESC;ENG_DESC;"
                        + "OPD_CHARGE_CODE;IPD_CHARGE_CODE;MRO_CHARGE_CODE;"
                        + "STA_CHARGE_CODE;PY1;PY2;DESCRIPT");
    }

    /**
     * 清空表
     * @param table TTable
     */
    public void clearTable(TTable table) {
        table.retrieve();
        table.setDSValue();
    }

    /**
     * 数据上翻
     * @param parm TParm
     * @param row int
     */
    public void setTextValue(TParm parm) {
        setValueForParm("CHARGE_HOSP_CODE;CHARGE_HOSP_DESC;ENG_DESC;"
                        + "OPD_CHARGE_CODE;IPD_CHARGE_CODE;MRO_CHARGE_CODE;"
                        + "STA_CHARGE_CODE;PY1;PY2;DESCRIPT;"
                        + "OPT_DATE;OPT_USER;OPT_TERM",
                        parm);
    }

    /**
     * 新增入口
     */
    public boolean onNew() { //拿到table对象
        int row = table.getSelectedRow();
        //选中则更新
        if (row >= 0) {
            return updateData(row);
        }
        //没选中则新增
        return newData();
    }

    /**
     * 已有的更新
     * @param row int
     * @return boolean
     */
    public boolean updateData(int row) {
        //代码名称
        String chargeHospDesc = getValueString("CHARGE_HOSP_DESC").trim();
        String oldChargeHospDesc = table.getItemString(row, "CHARGE_HOSP_DESC");
        if (!oldChargeHospDesc.endsWith(chargeHospDesc)) {
            if (!checkChargeHospDesc(chargeHospDesc))
                return false;
            table.setItem(row, "CHARGE_HOSP_DESC", chargeHospDesc);
            table.setItem(row, "PY1", getValueString("PY1"));
        }

        //收据费用不能为空
        String opdchargeCode = getValueString("OPD_CHARGE_CODE").trim();
        String oldopdChargeCode = table.getItemString(row, "OPD_CHARGE_CODE");
        if (!oldopdChargeCode.equals(opdchargeCode)) {
            //if (!checkChargeCode(opdchargeCode))
            //    return false;
            table.setItem(row, "OPD_CHARGE_CODE", opdchargeCode);
        }

        //收据费用不能为空
        String ipdchargeCode = getValueString("IPD_CHARGE_CODE").trim();
        String oldipdChargeCode = table.getItemString(row, "IPD_CHARGE_CODE");
        if (!oldipdChargeCode.equals(ipdchargeCode)) {
            //if (!checkChargeCode(ipdchargeCode))
            //    return false;
            table.setItem(row, "IPD_CHARGE_CODE", ipdchargeCode);
        }

        //首页费用
        String mroChargeCode = getValueString("MRO_CHARGE_CODE").trim();
        String oldMroChargeCode = table.getItemString(row, "MRO_CHARGE_CODE");
        if (!oldMroChargeCode.equals(mroChargeCode)) {
            if (!checkChargeCode(mroChargeCode))
                return false;
            table.setItem(row, "MRO_CHARGE_CODE", mroChargeCode);
        }
        //统计费用
        String staChargeCode = getValueString("STA_CHARGE_CODE").trim();
        String oldStaChargeCode = table.getItemString(row, "STA_CHARGE_CODE");
        if (!oldStaChargeCode.equals(staChargeCode)) {
            if (!checkChargeCode(staChargeCode))
                return false;
            table.setItem(row, "STA_CHARGE_CODE", staChargeCode);
        }

        table.setItem(row, "DESCRIPT", this.getValueString("DESCRIPT"));
        table.setItem(row, "PY1", this.getValueString("PY1"));
        table.setItem(row, "ENG_DESC", this.getValueString("ENG_DESC"));

        //序号
        int seq = getValueInt("SEQ");
        int oldSeq = table.getItemInt(row, "SEQ");
        if (oldSeq != seq)
            table.setItem(row, "SEQ", seq);
        //助记码
        String py2 = getValueString("PY2");
        String oldPy2 = table.getItemString(row, "PY2");
        if (!oldPy2.equals(py2))
            table.setItem(row, "PY2", py2);
        return true;
    }

    /**
     * 检核统计费用不能为空
     * @param staChargeCode String
     * @return boolean
     */
    public boolean checkStaChargeCode(String staChargeCode) {
        if (staChargeCode == null || staChargeCode.length() <= 0) {
            messageBox_("统计费用不能为空!");
            return false;
        }
        return true;
    }

    /**
     * 检核首页费用
     * @param mroChargeCode String
     * @return boolean
     */
    public boolean checkMroChargeCode(String mroChargeCode) {
        if (mroChargeCode == null || mroChargeCode.length() <= 0) {
            messageBox_("首页费用不能为空!");
            return false;
        }
        return true;
    }

    /**
     * 检核收据费用代码
     * @param chargeCode String
     * @return boolean
     */
    public boolean checkChargeCode(String chargeCode) {
        if (chargeCode == null || chargeCode.length() <= 0) {
            messageBox_("收据费用不能为空!");
            return false;
        }
        return true;
    }

    /**
     * 检核名称是否有重复
     * @param chargeHospDesc String
     * @return boolean
     */
    public boolean checkChargeHospDesc(String chargeHospDesc) {
        if (chargeHospDesc == null || chargeHospDesc.length() <= 0) {
            messageBox_("费用名称不能为空!");
            return false;
        }
        if (table.getDataStore().exist("CHARGE_HOSP_DESC='" + chargeHospDesc +
                                       "'")) {
            switch (this.messageBox("提示信息",
                                    "名称重复是否继续!", this.YES_NO_OPTION)) {
                //保存
                case 0:
                    return true;
                    //不保存
                case 1:
                    return false;
            }
        }
        return true;
    }

    /**
     * 检核费用代码
     * @param chargeHospCode String
     * @return boolean
     */
    public boolean checkChargeHospCode(String chargeHospCode) {
        if (chargeHospCode == null || chargeHospCode.length() <= 0) {
            messageBox_("费用代码不能为空!");
            return false;
        }
        if (table.getDataStore().exist("CHARGE_HOSP_CODE='" + chargeHospCode +
                                       "'")) {
            messageBox_("代码" + chargeHospCode + "已存在!");
            return false;
        }
        return true;
    }

    /**
     * 新增数据
     * @return boolean
     */
    public boolean newData() {
        //接收文本
        table.acceptText();
        //费用代码
        String chargeHospCode = getValueString("CHARGE_HOSP_CODE").trim();
        if (!checkChargeHospCode(chargeHospCode))
            return false;
        //代码名称
        String chargeHospDesc = getValueString("CHARGE_HOSP_DESC").trim();
        if (!checkChargeHospDesc(chargeHospDesc))
            return false;

        //门诊收据费用不能为空
        String opdchargeCode = getValueString("OPD_CHARGE_CODE").trim();
//        if (!checkChargeCode(opdchargeCode))
//            return false;

        //住院收据费用不能为空
        String ipdchargeCode = getValueString("IPD_CHARGE_CODE").trim();
//        if (!checkChargeCode(ipdchargeCode))
//            return false;

        //首页费用
        String mroChargeCode = getValueString("MRO_CHARGE_CODE").trim();
        if (!checkChargeCode(mroChargeCode))
            return false;

        //统计费用
        String staChargeCode = getValueString("STA_CHARGE_CODE").trim();
        if (!checkChargeCode(staChargeCode))
            return false;
        //得到新添加的table数据行号
        int row = table.addRow();
        table.setItem(row, "CHARGE_HOSP_CODE", chargeHospCode);
        table.setItem(row, "CHARGE_HOSP_DESC", chargeHospDesc);
        table.setItem(row, "OPD_CHARGE_CODE", opdchargeCode);
        table.setItem(row, "IPD_CHARGE_CODE", ipdchargeCode);
        table.setItem(row, "MRO_CHARGE_CODE", mroChargeCode);
        table.setItem(row, "STA_CHARGE_CODE", staChargeCode);
        table.setItem(row, "DESCRIPT", this.getValueString("DESCRIPT"));
        table.setItem(row, "ENG_DESC", this.getValueString("ENG_DESC"));

        //序号
        int seq = getValueInt("SEQ");
        if (seq <= 0)
            seq = getMaxSeq(table.getDataStore(), "SEQ");
        table.setItem(row, "SEQ", seq);
        table.setItem(row, "PY1", getValueString("PY1"));
        //助记码
        table.setItem(row, "PY2", getValueString("PY2"));
        //设置当前选中行为添加的行
        table.setSelectedRow(row);
        return true;
    }

    /**
     *删除选中的行
     * @param row int
     */
    public void onDelete() {
        switch (this.messageBox("提示信息",
                                "确认删除!", this.YES_NO_OPTION)) {
            //保存
            case 0:
                break;
                //不保存
            case 1:
                return;
        }
        //接收文本
        table.acceptText();
        table.removeRow(table.getSelectedRow());
        saveData();
    }

    /**
     * 保存入口
     */
    public boolean onUpdate() {
        if (!onNew())
            return false;
        return saveData();
    }

    /**
     * 保存数据
     * @return boolean
     */
    public boolean saveData() {
        //this.messageBox("");
        Timestamp date = SystemTool.getInstance().getDate();

        //接收文本
        table.acceptText();
        TDataStore dataStore = table.getDataStore();
        if (dataStore.isFilter()) {
            table.setFilter("");
            table.filter();
        }

        //获得全部改动的行号
        int rows[] = table.getModifiedRows();
        //给固定数据配数据
        for (int i = 0; i < rows.length; i++) {
            table.setItem(rows[i], "OPT_USER", Operator.getID());
            table.setItem(rows[i], "OPT_DATE", date);
            table.setItem(rows[i], "OPT_TERM", Operator.getIP());
        }
        table.getDataStore().setItem(table.getSelectedRow(), "DESCRIPT",
                                     this.getValueString("DESCRIPT"));
        table.getDataStore().showDebug();
        if (!table.update()) {
            messageBox("E0001");
            return false;
        }
        messageBox("P0001");
        afterSave();
        return true;
    }

    /**
     * 保存完成重置界面
     */
    public void afterSave() {
        onClear();
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
                    if (!saveData())
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
     * 得到最大的序号
     * @param dataStore TDataStore
     * @param columnName String
     * @return String
     */
    public int getMaxSeq(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return 0;
        //保存数据量
        int count = dataStore.rowCount();
        //保存最大号
        int s = 0;
        for (int i = 0; i < count; i++) {
            int value = dataStore.getItemInt(i, columnName);
            //保存最大值
            if (s < value) {
                s = value;
                continue;
            }
        }
        //最大号加1
        s++;
        return s;
    }

    /**
     * 检核是否有数据变更
     * @return boolean
     */
    public boolean CheckChange() {
        //检查数据变更
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        if (table.isModified())
            return true;
        return false;
    }

    /**
     *
     * @param args String[]
     */
    public static void main(String args[]) {
        com.javahis.util.JavaHisDebug.TBuilder();
    }

}
