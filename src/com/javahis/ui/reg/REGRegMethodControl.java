package com.javahis.ui.reg;

import java.util.Vector;

import jdo.reg.RegMethodTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;
import com.dongyang.ui.event.TPopupMenuEvent;
import jdo.sys.SysFee;
import jdo.sys.SystemTool;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * <p>
 * Title: 挂号方式
 * </p>
 *
 * <p>
 * Description:挂号方式
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author wangl 2008.08.21
 * @version 1.0
 */
public class REGRegMethodControl
    extends TControl {
    TParm data;
    int selectRow = -1;

    public void onInit() {
        super.onInit();
        ( (TTable) getComponent("Table")).addEventListener("Table->"
            + TTableEvent.CLICKED, this, "onTableClicked");
        //只有text有这个方法，调用sys_fee弹出框
        callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
                     "%ROOT%\\config\\sys\\SYSFeePopup.x");
        //接受回传值
        callFunction("UI|ORDER_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        onClear();
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
            "REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ;"+
            "DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG;PRINT_FLG;ORDER_CODE",
            data, row);
        this.setValue("FEE", onOrderCode(data.getValue("ORDER_CODE",row)));
        this.setValue("ORDER_DESC", getOrderDesc(data.getValue("ORDER_CODE", row)));
        selectRow = row;
        // 不可编辑
        ( (TTextField) getComponent("REGMETHOD_CODE")).setEnabled(false);
        // 设置删除按钮状态
        ( (TMenuItem) getComponent("delete")).setEnabled(true);
    }
    /**
     * 根据ORDER_CODE查询费用
     * @param orderCode String
     * @return double
     */
    public double onOrderCode(String orderCode) {
        if (orderCode.equals("") || orderCode == null)
            return 0.00;
        double own_price = SysFee.getFee(orderCode);
        return own_price;
    }
    /**
     * 通过orderCode得到医嘱名称
     * @param orderCode String
     * @return String
     */
    public String getOrderDesc(String orderCode) {
        String orderDesc = "";
        String selOrderDesc =
            " SELECT ORDER_DESC FROM SYS_FEE WHERE ORDER_CODE = '" + orderCode +
            "' ";
        TParm orderDescParm = new TParm(TJDODBTool.getInstance().select(
            selOrderDesc));
        if (orderDescParm.getErrCode() < 0) {
            err(orderDescParm.getErrName() + " " + orderDescParm.getErrName());
            return orderDesc;
        }
        orderDesc = orderDescParm.getValue("ORDER_DESC", 0);
        return orderDesc;
    }
    /**
     * 费用代码下拉列表选择
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE"));
        this.setValue("FEE", parm.getValue("OWN_PRICE"));
        this.setValue("ORDER_DESC", parm.getValue("ORDER_DESC"));
        this.grabFocus("FEE");
    }

    /**
     * 新增
     */
    public void onInsert() {
        if (!emptyTextCheck("REGMETHOD_CODE,REGMETHOD_DESC")) {
            return;
        }
        TParm parm = getParmForTag("REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ:int;"+
                                   "DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG;PRINT_FLG;ORDER_CODE");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = RegMethodTool.getInstance().insertdata(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        // 显示新增数据
//        callFunction("UI|Table|addRow", parm,
//                     "REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ;"+
//                     "DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG;"+
//                     "ORDER_CODE;OPT_USER;OPT_DATE;OPT_TERM");
        ( (TTable) getComponent("Table"))
            .addRow(
                parm,
                "REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ;"+
                "DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG;PRINT_FLG;ORDER_CODE;"+
                "OPT_USER;OPT_DATE;OPT_TERM");

//        String regMethod = this.getValue("REGMETHOD_CODE").toString();
//        data = RegMethodTool.getInstance().selectdata(regMethod);
//        int row = data.insertRow();
//        data.setRowData(row, parm);
        this.messageBox("P0001");
    }

    /**
     * 更新
     */
    public void onUpdate() {
        TParm parm = getParmForTag("REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ:int;"+
                                   "DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG;PRINT_FLG;ORDER_CODE");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = RegMethodTool.getInstance().updatedata(parm);
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
     * 删除
     */
    public void onDelete() {
        if (this.messageBox("提示", "是否删除", 2) == 0) {
            if (selectRow == -1)
                return;
            String regMethod = getValue("REGMETHOD_CODE").toString();
            TParm result = RegMethodTool.getInstance().deletedata(regMethod);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            TTable table = ( (TTable) getComponent("TABLE"));
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
        }
        else {
            return;
        }
    }

    /**
     * 查询
     */
    public void onQuery() {
        String regMethod = getText("REGMETHOD_CODE");
        data = RegMethodTool.getInstance().selectdata(regMethod);
        // 判断错误值
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ( (TTable) getComponent("TABLE")).setParmValue(data);
    }

    /**
     * 清空
     */
    public void onClear() {
        clearValue("REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ;"+
                   "DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG;"+
                   "PRINT_FLG;ORDER_CODE;ORDER_DESC;FEE");
        ( (TTable) getComponent("TABLE")).clearSelection();
        selectRow = -1;
        ( (TTextField) getComponent("REGMETHOD_CODE")).setEnabled(true);
        ( (TTextField) getComponent("REGMETHOD_DESC")).setEnabled(true);
        // 设置删除按钮状态
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        onQuery();
        long seq = 0;
        // 取SEQ最大值
        if (data.existData("SEQ")) {
            Vector vct = data.getVectorValue("SEQ");
            for (int i = 0; i < vct.size(); i++) {
                long a = Long.parseLong( (vct.get(i)).toString().trim());
                if (a > seq)
                    seq = a;
            }
            this.setValue("SEQ", seq + 1);
        }
    }

    /**
     * 根据汉字输出拼音首字母
     *
     * @return Object
     */

    public Object onCode() {
        if (TCM_Transform.getString(this.getValue("REGMETHOD_DESC")).length() <
            1) {
            return null;
        }
        String value = TMessage.getPy(this.getValueString("REGMETHOD_DESC"));
        if (null == value || value.length() < 1) {
            return null;
        }
        this.setValue("PY1", value);
        // 光标下移
        ( (TTextField) getComponent("PY1")).grabFocus();
        return null;
    }
}
