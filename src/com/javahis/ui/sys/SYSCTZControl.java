package com.javahis.ui.sys;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.jdo.TDataStore;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.sys.SYSChargeHospCodeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSSQL;
import com.dongyang.manager.TIOM_AppServer;
import action.sys.SYSCTZAction;

/**
 * <p>Title:身份明细 </p>
 *
 * <p>Description:身份明细 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author TParm
 * @version 1.0
 */
public class SYSCTZControl
    extends TControl {

    /**
     * 当前选中的表
     */
    TTable tableNow = null;
    /**
     * 身份表
     */
    TTable tableCtz;
    /**
     * 身份折扣表
     */
    TTable tableCharge;
    /**
     * 拿到所有的收据类别
     */
    TParm allCode = SYSChargeHospCodeTool.getInstance().selectalldata();

    /**
     * 树
     */
    TTree tree;

    private int row;

    /**
     * 初始化界面
     */
    public void onInit() {
        super.onInit();
        //给Table添加侦听事件
        addEventListener("TABLEHOSPCHARGEDETIAL->" + TTableEvent.CHANGE_VALUE,
                         "onTableHspChargeChangeValue");
        //当前选中的table
        tableCtz = (TTable)this.getComponent("TABLECTZ");

        TDataStore dataStore = new TDataStore();
        dataStore.setSQL(SYSSQL.getSYSCtz());
        dataStore.retrieve();
        tableCtz.setDataStore(dataStore);
        tableCtz.setDSValue();

        tableNow = tableCtz;
        tableCharge = (TTable)this.getComponent("TABLEHOSPCHARGEDETIAL");

        TDataStore dataStore2 = new TDataStore();
        dataStore2.setSQL(SYSSQL.getSYSChargeDetail());
        dataStore2.retrieve();
        tableCharge.setDataStore(dataStore2);
        tableCharge.setDSValue();

        //所有数据处理
        equsDate();
        initCombox();
        this.tableClearValue(tableCharge);
    }

    /**
     * 身份表的点击时间
     */
    public void onTableCtzClicked() {
        row = tableCtz.getSelectedRow();
        //数据上翻
        TParm tableCtzParm = tableCtz.getDataStore().getRowParm(row);
        setTextValue(tableCtzParm);
        //身份代码不能用
        setTextEnabled(false);
        //过滤明细
        String ctzCode = tableCtz.getItemString(row, "CTZ_CODE");
        String str = "CTZ_CODE='" + ctzCode + "'";
        tableCharge.setFilter(str);
        tableCharge.filter();
    }

    /**
     * 界面上不可编辑的控件
     * @param boo boolean
     */
    public void setTextEnabled(boolean boo) {
        callFunction("UI|CTZ_CODE|setEnabled", boo);
    }

    /**
     * 清空
     */
    public void onClear() {
        clearQuery();
        clearText();
        clearTable(tableCtz);
        clearTable(tableCharge);
        tableClearValue(tableCharge);
        setTextEnabled(true);
        row = -1;
    }

    /**
     * 清空查询条件
     */
    public void clearQuery() {
        clearText("CTZCODE;COMPANYCODE");
        this.setValue("MAINCTZFLG", "N");
        this.setValue("NHICTZFLG", "N");
    }

    /**
     * 清空属性
     */
    public void clearText() {
        clearText("CTZ_CODE;CTZ_DESC;NHI_COMPANY_CODE;PY1;SEQ;NHI_NO;DESCRIPT;MRO_CTZ");
        this.setValue("NHI_CTZ_FLG", "N");
        this.setValue("MAIN_CTZ_FLG", "N");
        this.setValue("MRCTZ_UPD_FLG", "N");
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
     * 清空显示
     * @param table TTable
     */
    public void tableClearValue(TTable table) {
        table.setFilter("CTZ_CODE='" + null +"'");
        table.filter();
    }

    /**
     * 数据上翻
     * @param parm TParm
     */
    public void setTextValue(TParm parm) {
        setValueForParm("CTZ_CODE;CTZ_DESC;NHI_COMPANY_CODE;NHI_CTZ_FLG;MAIN_CTZ_FLG;MRCTZ_UPD_FLG;PY1;SEQ;NHI_NO;DESCRIPT;MRO_CTZ",
                        parm);
    }

    /**
     * 初始化界面上的combox
     */
    public void initCombox() {
        TParm parm = new TParm();
        //护士站parm
        tableCtz.acceptText();
        TDataStore dateStore = tableCtz.getDataStore();
        String name = dateStore.isFilter() ? dateStore.FILTER :
            dateStore.PRIMARY;
        parm = dateStore.getBuffer(name);
        TComboBox comboBox = new TComboBox();
        comboBox.setParmMap("id:CTZ_CODE;name:CTZ_DESC");
        comboBox.setParmValue(parm);
        comboBox.setShowID(true);
        comboBox.setShowName(true);
        comboBox.setExpandWidth(30);
        comboBox.setTableShowList("name");
        tableCharge.addItem("CTZ", comboBox);

        comboBox = (TComboBox)this.getComponent("CTZCODE");
        comboBox.setParmValue(parm);
    }

    /**
     * 查询
     */
    public void onQuery() {
        String filterSql = "";
        String value = getValueString("CTZ_CODE").trim();
        //身份代码
        if (value != null && value.length() > 0) {
            if (filterSql.length() > 0)
                filterSql += " AND ";
            filterSql += " CTZ_CODE='" + value + "' ";
        }
        //请款单位
        value = getValueString("COMPANYCODE").trim();
        if (value != null && value.length() > 0) {
            if (filterSql.length() > 0)
                filterSql += " AND ";
            filterSql += " NHI_COMPANY_CODE='" + value + "' ";
        }
        //医保身份
        value = getValueString("NHICTZFLG").trim();
        if (value != null && value.equals("Y")) {
            if (filterSql.length() > 0)
                filterSql += " AND ";
            filterSql += " NHI_CTZ_FLG='" + value + "' ";
        }
        //主身份
        value = getValueString("MAINCTZFLG").trim();
        if (value != null && value.equals("Y")) {
            if (filterSql.length() > 0)
                filterSql += " AND ";
            filterSql += " MAIN_CTZ_FLG='" + value + "' ";
        }
        tableCtz.setFilter(filterSql);
        tableCtz.filter();
        tableCtz.setDSValue();

    }

    /**
     * 初始化时把所有身份上添加全部的收费类别
     */
    public void equsDate() {
        //取全部数据
        TDataStore dataStoreCtz = tableCtz.getDataStore();
        //去数据数量
        int ctzCount = dataStoreCtz.rowCount();

        //折扣数据
        TDataStore dataStoreHospCharge = tableCharge.getDataStore();
        //折扣数据量
        int hospChargeCount = dataStoreHospCharge.rowCount();
        //增加新行
        int addCount = hospChargeCount;
        //收费类别数据量
        int chargeCount = allCode.getCount();
        //记录是否有
        String have = "N";
        for (int i = 0; i < ctzCount; i++) {
            //身份代码
            String ctzCodeCtz = dataStoreCtz.getItemString(i, "CTZ_CODE");

            for (int z = 0; z < chargeCount; z++) {
                String chargeHospCode = allCode.getValue("CHARGE_HOSP_CODE",
                    z);
                //身份档中的身份+费用代码
                String strCtz = ctzCodeCtz + chargeHospCode;
                //清空标记
                have = "N";
                for (int j = 0; j < hospChargeCount; j++) {
                    //折扣档中的身份代码
                    String ctzCodeHosp = dataStoreHospCharge.getItemString(j,
                        "CTZ_CODE");
                    String chargeCode = dataStoreHospCharge.getItemString(j,
                        "CHARGE_HOSP_CODE");
                    //折扣档中的身份+费用
                    String strHosp = ctzCodeHosp + chargeCode;

                    if (strCtz.equals(strHosp))
                        //记录有了
                        have = "Y";
                }
                //如果没有
                if (have.equals("N")) {
                    //得到新添加的table数据行号
                    addCount = tableCharge.addRow();
                    //设置当前选中行为添加的行
                    tableCharge.setSelectedRow(addCount);
                    //默认身份
                    tableCharge.setItem(addCount, "CTZ_CODE", ctzCodeCtz);
                    //默认院内费用
                    tableCharge.setItem(addCount, "CHARGE_HOSP_CODE",
                                        chargeHospCode);
                    //默认折扣
                    tableCharge.setItem(addCount, "DISCOUNT_RATE", 1.0);
                }
            }
        }
    }

    /**
     * 得到界面上输入的数据
     * @return TParm
     */
    public TParm getTextParm() {
        TParm parm = getParmForTag(
            "CTZ_CODE;CTZ_DESC;NHI_COMPANY_CODE;NHI_CTZ_FLG;" +
            "MAIN_CTZ_FLG;MRCTZ_UPD_FLG;PY1;SEQ;NHI_NO;DESCRIPT;MRO_CTZ");
        return parm;

    }

    /**
     * 更改身份
     * @param selectedRow int
     * @return boolean
     */
    public boolean updateCtz(int selectedRow) {
        //检核身份名称
        String ctzDesc = getValueString("CTZ_DESC").trim();
        String oldCtzdesc = tableCtz.getItemString(selectedRow, "CTZ_DESC");
        //如果换了ctzDesc
        if (!oldCtzdesc.equals(ctzDesc)) {
            if (!checkCtzDesc(ctzDesc))
                return false;
            tableCtz.setItem(selectedRow, "CTZ_DESC", ctzDesc);
            tableCtz.setItem(selectedRow, "PY1", getValueString("PY1"));
        }
        //检核请款单位不能为空
        String companyCode = getValueString("NHI_COMPANY_CODE");
        String oldCompanyCode = tableCtz.getItemString(selectedRow,
            "NHI_COMPANY_CODE");
        //如果换了请款单位
        if (!oldCompanyCode.equals(companyCode)) {
            if (!checkCompanyCode(companyCode))
                return false;
            tableCtz.setItem(selectedRow, "NHI_COMPANY_CODE", companyCode);
        }
        //拿到最大编号
        int seq = getValueInt("SEQ");
        int oldSeq = tableCtz.getItemInt(selectedRow, "SEQ");
        if (seq != oldSeq)
            tableCtz.setItem(selectedRow, "SEQ", seq);
        //医保代码==pangben 2012-2-7
        String nhiNo = getValueString("NHI_NO");
        String oldNhiNo = tableCtz.getItemString(selectedRow, "NHI_NO");
        if (nhiNo != null && !nhiNo.equals(oldNhiNo))
            tableCtz.setItem(selectedRow, "NHI_NO", nhiNo);
        //备注
        String descript = getValueString("DESCRIPT");
        String oldDescript = tableCtz.getItemString(selectedRow, "DESCRIPT");
        if (descript != null && !descript.equals(oldDescript))
            tableCtz.setItem(selectedRow, "DESCRIPT", descript);
        //首页身份
        String mroCtz = getValueString("MRO_CTZ");
        String oldmroCtz = tableCtz.getItemString(selectedRow, "MRO_CTZ");
        if (mroCtz != null && !mroCtz.equals(oldmroCtz))
            tableCtz.setItem(selectedRow, "MRO_CTZ", mroCtz);
        initCombox();
        return true;
    }

    /**
     * 新增身份
     * @return boolean
     */
    public boolean addCtz() {

        String ctzCode = getValueString("CTZ_CODE").trim();
        //检核身份代码
        if (!checkCtzCode(ctzCode))
            return false;
        //检核身份名称
        String ctzDesc = getValueString("CTZ_DESC").trim();
        if (!checkCtzDesc(ctzDesc))
            return false;
        //        //检核请款单位不能为空
        //        String companyCode = this.getValueString("NHI_COMPANY_CODE");
        //        if (!checkCompanyCode(companyCode))
        //            return false;
        //拿到最大编号
        int seq = this.getValueInt("SEQ");
        if (seq <= 0)
            seq = getMaxSeq(tableCtz.getDataStore(), "SEQ");
        //得到新添加的table数据行号
        int row = tableCtz.addRow();
        //设置当前选中行为添加的行
        tableCtz.setSelectedRow(row);
        //身份代码
        tableCtz.setItem(row, "CTZ_CODE", ctzCode);
        //身份名称
        tableCtz.setItem(row, "CTZ_DESC", ctzDesc);
        tableCtz.setItem(row, "PY1", getValueString("PY1"));
        //添加序号
        tableCtz.setItem(row, "SEQ", seq);
        tableCtz.setItem(row, "NHI_COMPANY_CODE", this.getValue("NHI_COMPANY_CODE"));
        tableCtz.setItem(row, "NHI_NO", this.getValue("NHI_NO"));//医保代码
        tableCtz.setItem(row, "DESCRIPT", this.getValue("DESCRIPT"));
        tableCtz.setItem(row, "MAIN_CTZ_FLG", this.getValue("MAIN_CTZ_FLG"));
        tableCtz.setItem(row, "NHI_CTZ_FLG", this.getValue("NHI_CTZ_FLG"));
        tableCtz.setItem(row, "MRCTZ_UPD_FLG", this.getValue("MRCTZ_UPD_FLG"));
        /****************************病案首页费用身份 shiblmodify20120105*******************************************/
        tableCtz.setItem(row, "MRO_CTZ", this.getValue("MRO_CTZ"));
        tableCtz.getDataStore().showDebug();

        for (int i = 0; i < allCode.getCount(); i++) {
            String hospChargeCode = allCode.getValue("CHARGE_HOSP_CODE", i);
            //得到新添加的table数据行号
            row = tableCharge.addRow();
            //设置当前选中行为添加的行
            tableCharge.setSelectedRow(row);
            //默认身份
            tableCharge.setItem(row, "CTZ_CODE", ctzCode);
            //默认院内费用
            tableCharge.setItem(row, "CHARGE_HOSP_CODE", hospChargeCode);
            //默认折扣
            tableCharge.setItem(row, "DISCOUNT_RATE", 1.0);
        }
        initCombox();
        return true;
    }

    /**
     * 检核身份不能为空,也不能重复
     * @param ctzCode String
     * @return boolean
     */
    public boolean checkCtzCode(String ctzCode) {
        //不能为空
        if (ctzCode.equals("") || ctzCode == null) {
            messageBox_("代码不能为空!");
            return false;
        }
        //不能重复
        if (tableCtz.getDataStore().exist("CTZ_CODE='" + ctzCode +
                                          "'")) {
            messageBox_("身份" + ctzCode + "重复!");
            return false;
        }
        return true;
    }

    /**
     * 检核身份名称
     * @param ctzDesc String
     * @return boolean
     */

    public boolean checkCtzDesc(String ctzDesc) {
        if (ctzDesc == null || ctzDesc.trim().length() == 0) {
            messageBox_("身份名称不能为空!");
            return false;
        }
        return true;
    }

    /**
     * 检核请款单位
     * @param companyCode String
     * @return boolean
     */
    public boolean checkCompanyCode(String companyCode) {
        if (companyCode == null || companyCode.length() == 0) {
            messageBox_("请款单位不能为空!");
            return false;
        }
        return true;
    }

    /**
     * 新增与更改身份入口
     * @return boolean
     */
    public boolean newCtz() {
        int selectedRow = tableCtz.getSelectedRow();
        //如果有选中则是更改身份
        if (selectedRow < 0) {
            //如果没选中身份则是新增加身份
            return addCtz();
        }
        return updateCtz(selectedRow);
    }


    /**
     * 删除方法
     */
    public void onDelete() {
        //接收文本
        if (tableNow != null)
            tableNow.acceptText();
        int row = tableCtz.getSelectedRow();
        //如果没有选中行
        if (row < 0) {
            messageBox("请选择身份!");
            return;
        }
        else {
            switch (messageBox("提示信息", "确认删除", this.YES_NO_OPTION)) {
                //保存
                case 0:
                    break;
                    //不保存
                case 1:
                    return;
            }

        }
        //删除折扣比例的方法
        deleteCtz(row);
        initCombox();
        this.onClear();
    }

    /**
     * 删除折扣
     * @param row int
     */
    public void deleteCtz(int row) {
        //拿到当前删除的身份
        String code = tableCtz.getItemString(row, 0);
        TDataStore dataStoreHospCharge = tableCharge.getDataStore();
        if (dataStoreHospCharge.isFilter()) {
            tableCharge.setFilter("");
            tableCharge.filter();
        }
        int countHospCharge = dataStoreHospCharge.rowCount();
        for (int i = countHospCharge - 1; i >= 0; i--) {
            //拿到身份代码
            String ctzCode = dataStoreHospCharge.getItemString(i, "CTZ_CODE");
            //如果是本身份
            if (ctzCode.equals(code)) {
                dataStoreHospCharge.deleteRow(i);
            }
        }
        //删除table上的当前行
        tableCtz.removeRow(row);
        //保存数据
        //this.saveData();
        TDataStore dataStoreCharge = tableCtz.getDataStore();
        String[] sql = dataStoreCharge.getUpdateSQL();
        TParm parm = new TParm();
        parm.setData("SQL_M", sql);
        parm.setData("CTZ_CODE", this.getValue("CTZ_CODE"));
        TParm result = TIOM_AppServer.executeAction("action.sys.SYSCTZAction",
                                                    "onDelete", parm);
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("删除失败");
            return;
        }
        this.messageBox("删除成功");
    }

    /**
     * 保存数据入口
     * @return boolean
     */
    public boolean onUpdate() {
        //更改的数据
        if (!newCtz())
            return false;
        return saveData();

    }

    /**
     * 保存数据
     * @return boolean
     */
    public boolean saveData() {
        Timestamp date = SystemTool.getInstance().getDate();
        //接收文本
        tableCharge.acceptText();
        tableCharge.setFilter("");
        tableCharge.filter();
        TDataStore dataStoreCharge = tableCharge.getDataStore();
        String name = dataStoreCharge.PRIMARY;
        //获得全部改动的行号
        int rowsCtz[] = dataStoreCharge.getModifiedRows(name);
        //给固定数据配数据
        for (int i = 0; i < rowsCtz.length; i++) {
            dataStoreCharge.setItem(rowsCtz[i], "OPT_USER", Operator.getID());
            dataStoreCharge.setItem(rowsCtz[i], "OPT_DATE", date);
            dataStoreCharge.setItem(rowsCtz[i], "OPT_TERM", Operator.getIP());
        }

        //dataStoreCharge.showDebug();
        String[] hopCharge = dataStoreCharge.getUpdateSQL();

        //接收文本
        tableCtz.acceptText();
        tableCtz.setFilter("");
        tableCtz.filter();
        TDataStore dataStore = tableCtz.getDataStore();
        name = dataStore.PRIMARY;
        //获得全部改动的行号
        int rowsHosp[] = dataStore.getModifiedRows(name);
        //给固定数据配数据
        for (int i = 0; i < rowsHosp.length; i++) {
            dataStore.setItem(rowsHosp[i], "OPT_USER", Operator.getID());
            dataStore.setItem(rowsHosp[i], "OPT_DATE", date);
            dataStore.setItem(rowsHosp[i], "OPT_TERM", Operator.getIP());
        }

        //dataStore.showDebug();
        dataStore.setItem(row, "CTZ_DESC", this.getValue("CTZ_DESC"));
        dataStore.setItem(row, "SEQ", this.getValue("SEQ"));
        dataStore.setItem(row, "NHI_COMPANY_CODE", this.getValue("NHI_COMPANY_CODE"));
        dataStore.setItem(row, "NHI_NO", this.getValue("NHI_NO"));//医保代码==pangb 2012-2-7
        dataStore.setItem(row, "DESCRIPT", this.getValue("DESCRIPT"));
        dataStore.setItem(row, "MAIN_CTZ_FLG", this.getValue("MAIN_CTZ_FLG"));
        dataStore.setItem(row, "NHI_CTZ_FLG", this.getValue("NHI_CTZ_FLG"));
        dataStore.setItem(row, "MRCTZ_UPD_FLG", this.getValue("MRCTZ_UPD_FLG"));
        //
        dataStore.setItem(row, "MRO_CTZ", this.getValue("MRO_CTZ"));

        //dataStore.showDebug();
        String[] ctz = dataStore.getUpdateSQL();
        ctz = StringTool.copyArray(ctz, hopCharge);
        //拿到保存sql的对象
        TJDODBTool dbTool = new TJDODBTool();
        //保存数据
        TParm result = new TParm(dbTool.update(ctz));
        if (result.getErrCode() < 0) {
            out("update err " + result.getErrText());
            this.messageBox("E0001");
            return false;
        }
        messageBox("P0001");
        afterSave();
        return true;

    }

    /**
     * 保存后清空痕迹
     */
    public void afterSave() {
        tableCtz.resetModify();
        tableCharge.resetModify();
        clearText();
        clearQuery();
        this.setTextEnabled(true);
        row = -1;
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
     * 检核是否有数据变更
     * @return boolean
     */
    public boolean CheckChange() {
        //变更身份
        TTable tableBed = (TTable) callFunction("UI|TABLECTZ|getThis");
        if (tableBed.isModified())
            return true;
        //变更收费
        TTable tableStation = (TTable) callFunction(
            "UI|TABLEHOSPCHARGEDETIAL|getThis");
        if (tableStation.isModified())
            return true;
        return false;
    }

    /**
     *
     * @param args String[]
     */
    public static void main(String args[]) {
        com.javahis.util.JavaHisDebug.TBuilder();
//      Operator.setData("admin", "HIS", "127.0.0.1", "C00101");
    }

}
