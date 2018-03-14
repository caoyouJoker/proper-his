package com.javahis.ui.adm;

import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import java.sql.Timestamp;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.data.TParm;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTextField;
import jdo.adm.ADMAutoBillTool;
import com.dongyang.ui.TLabel;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_Database;
import java.util.Vector;
import jdo.bil.BIL;

/**
 *
 * <p>Title: 自动计费收费项目</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author JiaoY 2008.12.18 test
 * @version 1.0
 */
public class ADMAutoBillControl extends TControl {
    TParm data;
    int selectRow = -1;
    OrderList orderDesc;

    public void onInit() {
        super.onInit();
        ((TTable) getComponent("Table")).addEventListener("Table->"
                + TTableEvent.CLICKED, this, "onTableClicked");
        //模糊查询 -------start---------
        orderDesc = new OrderList();
        TTable table = (TTable)this.getComponent("Table");
        table.addItem("ORDER_LIST", orderDesc);
        //模糊查询 -------end---------

        //只有text有这个方法，调用sys_fee弹出框
        callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
                     "%ROOT%\\config\\sys\\SYSFeePopup.x");

        //textfield接受回传值
        callFunction("UI|ORDER_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        onClear();
    }

    /**
     * 诊断事件
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE"));
        this.setValue("ORDER_DESC", parm.getValue("ORDER_DESC"));
        this.setValue("UNIT_CODE", parm.getValue("UNIT_CODE"));
        this.setValue("ORDER_DESC", parm.getValue("ORDER_DESC"));
        this.setValue("DOSEAGE_QTY",1);
        //计算总费用
        onQty();
        this.grabFocus("DOSEAGE_QTY");
    }

    /**
     * 模糊查询
     */
    public class OrderList extends TLabel {
        TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                                  dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("ORDER_CODE");
            Vector d = (Vector) parm.getData("ORDER_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i)))
                    return "" + d.get(i);
            }
            return s;
        }
    }


    /**
     * 查核日期数据
     * @return boolean
     */
    public boolean checkdata() {
       String startMonth = getValue("START_MONTH").toString();
        if (getValue("START_MONTH") == null || "".equals(getValue("START_MONTH"))||Integer.parseInt(startMonth)==0) {
            this.messageBox_("开始月份不可为0！");
            return false;
        }
        if (getValue("START_DAY") == null || "".equals(getValue("START_DAY"))||Integer.parseInt(getValue("START_DAY").toString())==0) {
            this.messageBox_("开始日期不可为0！");
            return false;
        }
        if (getValue("END_MONTH") == null || "".equals(getValue("END_MONTH"))||Integer.parseInt(getText("END_MONTH"))==0) {
            this.messageBox_("结束月份不可为0！");
            return false;
        }
        if (getValue("END_DAY") == null || "".equals(getValue("END_DAY"))||Integer.parseInt(getText("END_DAY"))==0) {
            this.messageBox_("结束日期不可为0！");
            return false;
        }
        //核对日期的正确性
        int month_s = this.getValueInt("START_MONTH");//月份起始
        int day_s = this.getValueInt("START_DAY");//起日日期
        int month_e = this.getValueInt("END_MONTH");//截止月份
        int day_e = this.getValueInt("END_DAY");//截止日期
        if(month_s<1||month_s>12){
            this.messageBox_("开始月份不正确");
            return false;
        }
        if(month_e<1||month_e>12){
            this.messageBox_("截止月份不正确");
            return false;
        }
        //根据月份判断日期范围
        if(month_s==1||month_s==3||month_s==5||month_s==7||month_s==8||month_s==10||month_s==12){
            if(day_s<1||day_s>31){
                this.messageBox_("起始日期超出范围");
                return false;
            }
        }
        //根据月份判断日期范围
        if(month_s==4||month_s==6||month_s==9||month_s==11){
            if(day_s<1||day_s>30){
                this.messageBox_("起始日期超出范围");
                return false;
            }
        }
        if(month_s==2){
            if(day_s<1||day_s>29){
                this.messageBox_("起始日期超出范围");
                return false;
            }
        }
        //根据月份判断日期范围
        if(month_e==1||month_e==3||month_e==5||month_e==7||month_e==8||month_e==10||month_e==12){
            if(day_e<1||day_e>31){
                this.messageBox_("截止日期超出范围");
                return false;
            }
        }
        //根据月份判断日期范围
        if(month_e==4||month_e==6||month_e==9||month_e==11){
            if(day_e<1||day_e>30){
                this.messageBox_("截止日期超出范围");
                return false;
            }
        }
        if(month_e==2){
            if(day_e<1||day_e>29){
                this.messageBox_("截止日期超出范围");
                return false;
            }
        }
        if(Integer.parseInt(getText("DOSEAGE_QTY"))<=0){
            this.messageBox_("数量必须大于0！");
            return false;
        }
        return true;
    }

    /**
     * 增加对Table的监听
     *
     * @param row
     *            int
     */
    public void onTableClicked(int row) {
        // 选中行
        if (row < 0)
            return;
        setValueForParm(
                "ORDER_CODE;DOSEAGE_QTY;UNIT_CODE;SUM_PRICE;OCCUFEE_FLG;BABY_FLG",
                data, row);
        setValue("START_MONTH", data.getValue("START_DATE", row).substring(0, 2));
        setValue("START_DAY", data.getValue("START_DATE", row).substring(2));
        setValue("END_MONTH", data.getValue("END_DATE", row).substring(0, 2));
        setValue("END_DAY", data.getValue("END_DATE", row).substring(2));
        setValue("ORDER_DESC",
                 orderDesc.getTableShowValue(getValue("ORDER_CODE").toString()));
        selectRow = row;
        // 不可编辑
        ((TTextField) getComponent("ORDER_CODE")).setEnabled(false);
        // 设置删除按钮状态
        ((TMenuItem) getComponent("delete")).setEnabled(true);
    }

    /**
     * 计算总价
     */
    public void onQty() {
        String orderCode = this.getValueString("ORDER_CODE");
        double qty = Double.parseDouble(this.getValue("DOSEAGE_QTY").toString());
        BIL bil = new BIL();
//        double price = bil.getFee(orderCode);
        double sumPrice = bil.getFee(orderCode, qty);
        this.setValue("SUM_PRICE",sumPrice);
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("ORDER_CODE", getValue("ORDER_CODE"));
        data = ADMAutoBillTool.getInstance().queryData(parm);
        // 判断错误值
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ((TTable) getComponent("TABLE")).setParmValue(data);

    }

    /**
     * 保存
     */
    public void onSave() {
        if (selectRow == -1) {
            onInsert();
            return;
        }
        onUpdate();
    }

    /**
     * 新增
     */
    public void onInsert() {
        if (!emptyTextCheck("ORDER_CODE,ORDER_DESC")) {
            return;
        }
        if (!this.checkdata()) {
            return;
        }
        BIL bil = new BIL();
        TParm parm = getParmForTag(
                "ORDER_CODE;UNIT_CODE;DOSEAGE_QTY;OCCUFEE_FLG;BABY_FLG");
        String month = getValue("START_MONTH").toString().trim();
        String activeMonth = month.length()==1?"0"+month:month;
        String day = getValue("START_DAY").toString().trim();
        String activeDay=day.length()==1?"0"+day:day;
        parm.setData("START_DATE",
                     activeMonth+activeDay);
        month = getValue("END_MONTH").toString().trim();
        activeMonth = month.length()==1?"0"+month:month;
        day = getValue("END_DAY").toString().trim();
        activeDay=day.length()==1?"0"+day:day;
        parm.setData("END_DATE",activeMonth+activeDay);
        parm.setData("PRICE", bil.getFee(getValue("ORDER_CODE").toString()));
        parm.setData("SUM_PRICE",
                     bil.getFee(getValue("ORDER_CODE").toString(),
                                Double.
                                parseDouble(this.getValue("DOSEAGE_QTY").toString())));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = ADMAutoBillTool.getInstance().insertdata(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        this.onClear();
        this.messageBox("P0001");
    }

    /**
     * 更新
     */
    public void onUpdate() {
        if (!this.checkdata()) {
            return;
        }
        TParm parm = getParmForTag(
            "ORDER_CODE;DOSEAGE_QTY;OCCUFEE_FLG;BABY_FLG");
        String month = getValue("START_MONTH").toString().trim();
        String activeMonth = month.length() == 1 ? "0" + month : month;
        String day = getValue("START_DAY").toString().trim();
        String activeDay = day.length() == 1 ? "0" + day : day;

        parm.setData("START_DATE", activeMonth + activeDay);

        month = getValue("END_MONTH").toString().trim();
        activeMonth = month.length() == 1 ? "0" + month : month;
        day = getValue("END_DAY").toString().trim();
        activeDay = day.length() == 1 ? "0" + day : day;

        parm.setData("SUM_PRICE", this.getValue("SUM_PRICE"));
        parm.setData("END_DATE", activeMonth + activeDay);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = ADMAutoBillTool.getInstance().updatedata(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        // 选中行
        int row = ( (TTable) getComponent("TABLE")).getSelectedRow();
        if (row < 0)
            return;
        // 刷新，设置末行某列的值
        data.setRowData(row, parm);
        ( (TTable) getComponent("TABLE")).setRowParmValue(row, data);
        this.messageBox("P0005");
    }

    /**
     * 删除
     */
    public void onDelete() {
        if (this.messageBox("提示", "是否删除", 2) == 0) {
            if (selectRow == -1)
                return;
            String orderCode = getValue("ORDER_CODE").toString();
            TParm result = ADMAutoBillTool.getInstance().deletedata(orderCode);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            TTable table = ((TTable) getComponent("TABLE"));
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            // 删除单行显示
            table.removeRow(row);
            if (row == table.getRowCount())
                table.setSelectedRow(row - 1);
            else
                table.setSelectedRow(row);
            this.messageBox("P0003");
            onClear();
        } else {
            return;
        }
    }

    /**
     * 清空
     */
    public void onClear() {
        clearValue("ORDER_CODE;ORDER_DESC;DOSEAGE_QTY;UNIT_CODE;SUM_PRICE;OCCUFEE_FLG;BABY_FLG;START_MONTH;START_DAY;END_MONTH;END_DAY");
        ((TTable) getComponent("Table")).clearSelection();
        selectRow = -1;
        ((TTextField) getComponent("ORDER_CODE")).setEnabled(true);
        // 设置删除按钮状态
        ((TMenuItem) getComponent("delete")).setEnabled(false);
        onQuery();
    }
}

