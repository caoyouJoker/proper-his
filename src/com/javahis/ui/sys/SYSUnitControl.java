/**
 *
 */
package com.javahis.ui.sys;

import jdo.sys.Operator;
import jdo.sys.SYSUnitTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;

/**
 *
 * <p>
 * Title: 药品单位
 * </p>
 *
 * <p>
 * Description:药品单位
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
public class SYSUnitControl
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

        String UNIT_CODE = getText("UNIT_CODE");
        if (UNIT_CODE == null || "".equals(UNIT_CODE)) {
            // System.out.println("UNIT_CODE"+UNIT_CODE);
            init();
        }
        else {
            data = SYSUnitTool.getInstance().selectdata(UNIT_CODE);
            // System.out.println("result:" + data);
            if (data.getErrCode() < 0) {
                messageBox(data.getErrText());
                return;
            }
            this
                .callFunction(
                    "UI|TABLE|setParmValue",
                    data,
                    "SEQ;UNIT_CODE;UNIT_CHN_DESC;PY1;PY2;UNIT_ENG_DESC;DESCRIPTION;MEDI_FLG;PACKAGE_FLG;DISPOSE_FLG;OTHER_FLG;OPT_USER;OPT_DATE");
        }

    }

    public void onTABLEClicked(int row) {

        // System.out.println("row=" + row);

        if (row < 0) {
            return;
        }
        // System.out.println("data"+data);
        setValueForParm(
            "SEQ;UNIT_CODE;UNIT_CHN_DESC;PY1;PY2;UNIT_ENG_DESC;DESCRIPTION;MEDI_FLG;PACKAGE_FLG;DISPOSE_FLG;OTHER_FLG",
            data, row);

        selectRow = row;
        callFunction("UI|UNIT_CODE|setEnabled", false);
        if (selectRow >= 0) {
            this.callFunction("delete|setVisible", true);
        }

    }

    /**
     * 清空
     */
    public void onClear() {
        this
            .clearValue("SEQ;UNIT_CODE;UNIT_CHN_DESC;PY1;PY2;UNIT_ENG_DESC;DESCRIPTION;MEDI_FLG;PACKAGE_FLG;DISPOSE_FLG;OTHER_FLG");
        callFunction("UI|TABLE|removeRowAll");
        this.setValue("SEQ", "0");
        this.setValue("MEDI_FLG", "Y");
        this.setValue("PACKAGE_FLG", "N");
        this.setValue("DISPOSE_FLG", "N");
        this.setValue("OTHER_FLG", "Y");
        selectRow = -1;
        callFunction("UI|UNIT_CODE|setEnabled", true);
    }

    /**
     * 初始化界面，查询所有的数据
     *
     * @return TParm
     */
    public void init() {
        data = SYSUnitTool.getInstance().selectall();
        // System.out.println("result:" + data);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        if (selectRow == -1) {
            this.callFunction("delete|setVisible", false);
        }
        this
            .callFunction(
                "UI|TABLE|setParmValue",
                data,
                "SEQ;UNIT_CODE;UNIT_CHN_DESC;PY1;PY2;UNIT_ENG_DESC;DESCRIPTION;MEDI_FLG;PACKAGE_FLG;DISPOSE_FLG;OTHER_FLG;OPT_USER;OPT_DATE");
    }

    /**
     * 新增
     */
    public void onInsert() {

        if (!this.emptyTextCheck("UNIT_CODE,UNIT_CHN_DESC")) {
            return;
        }
        TParm parm = getParmForTag("SEQ:int;UNIT_CODE;UNIT_CHN_DESC;PY1;PY2;UNIT_ENG_DESC;DESCRIPTION;MEDI_FLG;PACKAGE_FLG;DISPOSE_FLG;OTHER_FLG");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        SystemTool st = new SystemTool();
        parm.setData("OPT_DATE", st.getDate());
        // System.out.println("pram" + parm);
        TParm result = SYSUnitTool.getInstance().insertdata(parm);
        if (result.getErrCode() < 0) {
            this.messageBox("E0002");
            onClear();
            init();
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
        if (!this.emptyTextCheck("UNIT_CODE,UNIT_CHN_DESC")) {
            return;
        }
        TParm parm = getParmForTag("SEQ:int;UNIT_CODE;UNIT_CHN_DESC;PY1;PY2;UNIT_ENG_DESC;DESCRIPTION;MEDI_FLG;PACKAGE_FLG;DISPOSE_FLG;OTHER_FLG");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        SystemTool st = new SystemTool();
        parm.setData("OPT_DATE", st.getDate());
        // System.out.println("pram" + parm);
        TParm result = SYSUnitTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
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
        // System.out.println("selectRow:"+selectRow);

        // System.out.println("UNIT_CODE"+this.getText("UNIT_CODE"));
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
        // if(selectRow == -1)
        // return;
        if (!this.emptyTextCheck("UNIT_CODE,UNIT_CHN_DESC")) {
            return;
        }
        String UNIT_CODE = getText("UNIT_CODE");
        TParm result;
        if (this.messageBox("提示信息", "确定删除", 2) == 0) {
            result = SYSUnitTool.getInstance().deletedata(UNIT_CODE);
        }
        else {
            return;
        }

        if (result.getErrCode() < 0) {
            this.messageBox("E0003");
            onClear();
            init();
            return;
        }
        int row = (Integer) callFunction("UI|TABLE|getSelectedRow");
        if (row < 0) {
            return;
        }
        else {
            this.messageBox("P0003");
            onClear();
            init();
        }
        // 删除单行显示
        this.callFunction("UI|TABLE|removeRow", row);
        this.callFunction("UI|TABLE|setSelectRow", row);

    }

    public void onCode() {
        String desc = this.getValueString("UNIT_CHN_DESC");
        String py = TMessage.getPy(desc);
        this.setValue("PY1", py);
        ( (TTextField) getComponent("PY1")).grabFocus();
    }
}
