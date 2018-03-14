package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.event.TTableEvent;
import jdo.reg.PanelTypeFeeTool;
import com.dongyang.manager.TCM_Transform;
import jdo.sys.SysFee;
import com.dongyang.ui.event.TPopupMenuEvent;

/**
 *
 * <p>Title:号别费用控制类 </p>
 *
 * <p>Description:号别费用控制类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.16
 * @version 1.0
 */
public class REGPanelTypeFeeContrl
    extends TControl {
    String order_code;
    int selectRow = -1;
    public void onInit() {
        super.onInit();
        callFunction("UI|TABLETYPEFEE|addEventListener",
                     "TABLETYPEFEE->" + TTableEvent.CLICKED, this,
                     "onTABLETYPEFEEClicked");
        //只有text有这个方法，调用sys_fee弹出框
        callFunction("UI|ORDER_CODE|setPopupMenuParameter", "ORDER_CODELIST",
                     "%ROOT%\\config\\sys\\SYSFeePopup.x");
        //接受回传值
        callFunction("UI|ORDER_CODE|addEventListener",
                     TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        //查询数据
        initData();
    }

    /**
     * 查询数据
     */
    public void initData() {
        String s[] = (String[]) getParameter();
        if (s != null) {
            setValue("CLINICTYPE_CODE", s[0]);
            setValue("ADM_TYPE", s[1]);
            callFunction("UI|CLINICTYPE_CODE|setEnabled", false);
            callFunction("UI|ADM_TYPE|setEnabled", false);
        }
        onQuery();
    }

    /**
     * 重新加载数据
     */
    public void onInitReset() {
        initData();
    }

    /**
     * 费用代码下拉列表选择
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE"));
        this.grabFocus("OWN_PRICE");
    }

    /**
     *增加对TABLETYPEFEE的监听
     * @param row int
     */
    public void onTABLETYPEFEEClicked(int row) {

        if (row < 0)
            return;
        //取data部分数据（根据点击树同步数据）-------start---------
        TParm data = (TParm) callFunction("UI|TABLETYPEFEE|getParmValue");
        //取data部分数据（根据点击树同步数据）-------end---------
        setValueForParm("ADM_TYPE;CLINICTYPE_CODE;ORDER_CODE;RECEIPT_TYPE",
                        data, row);
        selectRow = row;
        order_code = (String) getValue("ORDER_CODE");
        //初始化Combo不可编辑
        callFunction("UI|ADM_TYPE|setEnabled", false);
        callFunction("UI|CLINICTYPE_CODE|setEnabled", false);
        callFunction("UI|OWN_PRICE|setEnabled", false);
        //号别总费用
        selAllPrice();
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = getParmForTag("ADM_TYPE;CLINICTYPE_CODE;ORDER_CODE", true);
        TParm data = PanelTypeFeeTool.getInstance().selectdata(parm);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        int count = data.getCount("ORDER_CODE");
        for (int i = 0; i < count; i++) {
            double own_price = SysFee.getFee(TCM_Transform.getString(
                data.getData("ORDER_CODE", i)));
            //增加费用行
            data.addData("OWN_PRICE", own_price);
        }
        this.callFunction("UI|TABLETYPEFEE|setParmValue", data);
    }

    /**
     * 新增
     */
    public void onInsert() {
        if (!this.emptyTextCheck("ADM_TYPE,CLINICTYPE_CODE"))
            return;
        TParm parm = getParmForTag(
            "ADM_TYPE;CLINICTYPE_CODE;ORDER_CODE;RECEIPT_TYPE");

        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = PanelTypeFeeTool.getInstance().insertdata(parm);

        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //2008.09.05 --------start--------table上加入新增的数据显示
        callFunction("UI|TABLETYPEFEE|addRow", parm,
                     "ADM_TYPE;CLINICTYPE_CODE;ORDER_CODE;RECEIPT_TYPE;OWN_PRICE;OPT_USER;OPT_DATE;OPT_TERM");

        //弹出新增成功提示框
        this.messageBox("P0002");

    }

    /**
     * 更新
     */
    public void onUpdate() {
        TParm parm = getParmForTag(
            "ADM_TYPE;CLINICTYPE_CODE;ORDER_CODE;RECEIPT_TYPE");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("OLD_ORDER_CODE", order_code);
        TParm result = PanelTypeFeeTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }

        //刷新，设置末行某列的值
        int row = (Integer) callFunction("UI|TABLETYPEFEE|getSelectedRow");
        if (row < 0)
            return;
        //取data部分数据（根据点击树同步数据）-------start---------
        TParm data = (TParm) callFunction("UI|TABLETYPEFEE|getParmValue");
        data.setRowData(row, parm);
        //取data部分数据（根据点击树同步数据）-------end---------
        callFunction("UI|TABLETYPEFEE|setRowParmValue", row, data);
        this.messageBox("P0001");

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
        if (this.messageBox("询问", "是否删除", 2) != 0)
            return;

        if (selectRow == -1)
            return;
        String admType = getValue("ADM_TYPE").toString();
        String clinicTypeCode = getValue("CLINICTYPE_CODE").toString();
        String orderCode = getValue("ORDER_CODE").toString();
        TParm result = PanelTypeFeeTool.getInstance().deletedata(admType,
            clinicTypeCode, orderCode);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //删除单行显示
        int row = (Integer) callFunction("UI|TABLETYPEFEE|getSelectedRow");
        if (row < 0)
            return;
        this.callFunction("UI|TABLETYPEFEE|removeRow", row);
        this.callFunction("UI|TABLETYPEFEE|setSelectRow", row);

        this.messageBox("P0003");

    }

    /**
     *根据ORDER_CODE查询费用
     */
    public void onOrderCode() {
        double own_price = SysFee.getFee(TCM_Transform.getString(getValue(
            "ORDER_CODE")));
        this.setValue("OWN_PRICE", own_price);
    }

    /**
     * 号别总费用
     */
    public void selAllPrice() {
        String admType = TCM_Transform.getString(getValue("ADM_TYPE"));
        String clinicType = TCM_Transform.getString(getValue("CLINICTYPE_CODE"));
        TParm getOrderCode = PanelTypeFeeTool.getInstance().getOrderCode(
            admType, clinicType);
        int count = getOrderCode.getCount("ORDER_CODE");
        double allPrice = 0.00;
        for (int i = 0; i < count; i++) {
            double own_price = SysFee.getFee(TCM_Transform.getString(
                getOrderCode.getData("ORDER_CODE", i)));
            allPrice = allPrice + own_price;
        }
        this.setValue("ALL_PRICE", allPrice);
    }

    /**
     *清空
     */
    public void onClear() {
        clearValue(
            "ADM_TYPE;CLINICTYPE_CODE;ORDER_CODE;RECEIPT_TYPE;OWN_PRICE;ALL_PRICE");
        this.callFunction("UI|TABLETYPEFEE|clearSelection");
        selectRow = -1;
        callFunction("UI|ADM_TYPE|setEnabled", true);
        callFunction("UI|CLINICTYPE_CODE|setEnabled", true);

    }
}
