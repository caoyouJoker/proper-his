package com.javahis.ui.sys;

import jdo.sys.Operator;
import jdo.sys.SYS_ORDER_CAT1_Tool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;

/**
 *
 * <p>
 * Title: 医嘱细分类
 * </p>
 *
 * <p>
 * Description:医嘱细分类
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author ehui 20080901
 * @version 1.0
 */
public class SYSOrderCat1Control
    extends TControl {
    TParm data;
    int selectRow = -1;

    public void onInit() {
        super.onInit();
        callFunction("UI|TABLE|addEventListener", "TABLE->"
                     + TTableEvent.CLICKED, this, "onTABLEClicked");
        init();
    }

    /**
     * 初始化界面，查询所有的数据
     *
     * @return TParm
     */
    public void onQuery() {
        String CODE = getText("ORDER_CAT1_CODE");
        if (CODE == null || "".equals(CODE)) {
            init();
        }
        else {
            data = SYS_ORDER_CAT1_Tool.getInstance().selectdata(CODE);
            // System.out.println("result:" + data);
            if (data.getErrCode() < 0) {
                messageBox(data.getErrText());
                return;
            }
            this.callFunction("UI|TABLE|setParmValue", data,
                              "SEQ;ORDER_CAT1_CODE;ORDER_CAT1_DESC;ENNAME;PY1;PY2;CAT1_TYPE;DEAL_SYSTEM;DEAL_SYSTEM2;DEAL_SYSTEM3;TREAT_FLG;DESCRIPTION;OPT_USER;OPT_DATE");
        }
    }

    public void onTABLEClicked(int row) {

        // System.out.println("row=" + row);

        if (row < 0) {
            return;
        }
        setValueForParm(
            "SEQ;ORDER_CAT1_CODE;ORDER_CAT1_DESC;ENNAME;PY1;PY2;CAT1_TYPE;DEAL_SYSTEM;DEAL_SYSTEM2;DEAL_SYSTEM3;TREAT_FLG;DESCRIPTION",
            data, row);
        selectRow = row;
        callFunction("UI|ORDER_CAT1_CODE|setEnabled", false);

    }

    /**
     * 清空
     */
    public void onClear() {
        // System.out.println("onclear");

        clearValue(
            "SEQ;ORDER_CAT1_CODE;ORDER_CAT1_DESC;PY1;PY2;CAT1_TYPE;DEAL_SYSTEM;DEAL_SYSTEM2;DEAL_SYSTEM3;TREAT_FLG;DESCRIPTION");
        callFunction("UI|TABLE|removeRowAll");
        selectRow = -1;
        callFunction("UI|ORDER_CAT1_CODE|setEnabled", true);
    }

    /**
     * 初始化界面，查询所有的数据
     *
     * @return TParm
     */
    public void init() {
        data = SYS_ORDER_CAT1_Tool.getInstance().selectall();
        //System.out.println("result:" + data);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        this.callFunction("UI|TABLE|setParmValue", data,
                          "SEQ;ORDER_CAT1_CODE;ORDER_CAT1_DESC;ENNAME;PY1;PY2;CAT1_TYPE;DEAL_SYSTEM;DEAL_SYSTEM2;DEAL_SYSTEM3;TREAT_FLG;DESCRIPTION;OPT_USER;OPT_DATE");
    }

    /**
     * 新增
     */
    public void onInsert() {
        if (!this.emptyTextCheck("ORDER_CAT1_CODE,ORDER_CAT1_DESC")) {
            return;
        }
        TParm parm = getParmForTag("ORDER_CAT1_CODE;ORDER_CAT1_DESC;ENNAME;CAT1_TYPE;DEAL_SYSTEM;DEAL_SYSTEM2;DEAL_SYSTEM3;SEQ:int;TREAT_FLG;DESCRIPTION;PY1;PY2");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        SystemTool st = new SystemTool();
        parm.setData("OPT_DATE", st.getDate());
        // parm.setData("POS_TYPE", callFunction("UI|POS_TYPE|getSelectedID"));
        //System.out.println("pram" + parm);
        data = SYS_ORDER_CAT1_Tool.getInstance().insertdata(parm);
        if (data.getErrCode() < 0) {
            this.messageBox("E0002");
            onClear();
            init();
            return;
        }
        else {
            this.messageBox("P0002");
            onClear();
            init();
        }
    }

    /**
     * 更新
     */
    public void onUpdate() {
        if (!this.emptyTextCheck("ORDER_CAT1_CODE,ORDER_CAT1_DESC")) {
            return;
        }
        TParm parm = getParmForTag("ORDER_CAT1_CODE;ORDER_CAT1_DESC;ENNAME;CAT1_TYPE;DEAL_SYSTEM;DEAL_SYSTEM2;DEAL_SYSTEM3;SEQ:int;TREAT_FLG;DESCRIPTION;PY1;PY2");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        SystemTool st = new SystemTool();
        parm.setData("OPT_DATE", st.getDate());
        //System.out.println("pram" + parm);
        data = SYS_ORDER_CAT1_Tool.getInstance().updatedata(parm);
        if (data.getErrCode() < 0) {
            this.messageBox("E0001");
            onClear();
            init();
            return;
        }
        int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
        if (row < 0) {
            this.messageBox("E0001");
            onClear();
            init();
        }
        else {
            this.messageBox("P0001");
            onClear();
            init();
        }
        // 设置末行某列的值
        // callFunction("UI|TABLE|setValueAt",getText("POS_DESC"),row,1);
        // callFunction("UI|TABLE|setValueAt",callFunction("UI|POS_TYPE|getSelectedID"),row,2);
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
        if (!this.emptyTextCheck("ORDER_CAT1_CODE")) {
            return;
        }
        String poscode = getText("ORDER_CAT1_CODE");
        if (this.messageBox("提示", "确定删除", 2) == 0) {
            data = SYS_ORDER_CAT1_Tool.getInstance().deletedata(poscode);
        }
        else {
            return;
        }

        if (data.getErrCode() < 0) {
            this.messageBox("E0003");
            onClear();
            init();
            return;
        }
        int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
        if (row < 0) {
            this.messageBox("E0003");
            onClear();
            init();
        }
        else {
            this.messageBox("P0003");
            onClear();
            init();
        }
        // //删除单行显示
        // this.callFunction("UI|TABLE|removeRow",row);
        // this.callFunction("UI|TABLE|setSelectRow",row);

    }

    /**
     * 根据汉字查询拼音首字母
     */
    public void onCode() {
        String desc = this.getValueString("ORDER_CAT1_DESC");
        String py = TMessage.getPy(desc);
        this.setValue("PY1", py);
        ( (TTextField) getComponent("PY1")).grabFocus();
    }

}
