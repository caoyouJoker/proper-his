package com.javahis.ui.sys;

import jdo.sys.Operator;
import jdo.sys.SYSPhaDoseTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;

/**
 *
 * <p>
 * Title:药品剂型
 * </p>
 *
 * <p>
 * Description: 处理有关药品剂型的全部数据
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author JiaoY 2008-09-5
 * @version 1.0
 */

public class SYSPhaDoseControl
    extends TControl {
    TParm data, comboldata;
    int selectRow = -1;

    public void onInit() {
        super.onInit();
        callFunction("UI|TABLE|addEventListener", "TABLE->"
                     + TTableEvent.CLICKED, this, "onTABLEClicked");
        callFunction("UI|delete|setEnabled", false);
        onQuery();
        init();

    }

    /**
     * 初始化查询&按照CTRLDRUGCLASS_CODE查询
     */
    public void onQuery() {
        String doseCode = this.getValueString("DOSE_CODE"); // 剂型代码
        data = SYSPhaDoseTool.getInstance().selectdata(doseCode,
            this.getValueString("DOSE_TYPE"));
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        if (data.getCount() == 0)
            this.messageBox("E0008");
        // System.out.println("data-->" + data);
        callFunction(
            "UI|TABLE|setParmValue",
            data,
            "SEQ;DOSE_CODE;DOSE_TYPE;DOSE_CHN_DESC;ENG_DESC;DESCRIPTION;OPT_USER;OPT_DATE;OPT_TERM;PY1;PY2 ");

    }

    /**
     * TABLE点击时间
     *
     * @param row
     *            int
     */
    public void onTABLEClicked(int row) {

        // System.out.println("row=" + row);
        // callFunction("UI|CTRLDRUGCLASS_CODE|setEnabled", false);
        if (row < 0) {
            return;
        }
        // System.out.println("data" + data);
        setValueForParm(
            "SEQ;DOSE_CODE;DOSE_TYPE;DOSE_CHN_DESC;ENG_DESC;DESCRIPTION;OPT_USER;OPT_DATE;OPT_TERM;PY1;PY2",
            data, row);
        selectRow = row;
        callFunction("UI|delete|setEnabled", true); // 删除可见
        callFunction("UI|DOSE_CODE|setEnabled", false);
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
        if (!this.emptyTextCheck("DOSE_CODE"))
            return;
        if (this.messageBox("P0009", "是否新增", 2) == 0) {

            TParm parm = new TParm();
            TParm doseCode = getParmForTag("DOSE_CODE");
            TParm exist = SYSPhaDoseTool.getInstance().existsData(doseCode); // 验证数据

            if (exist.getCount() != 0) {
                this.messageBox("E0006");
                return;
            }
            parm = getParmForTag(
                "SEQ;DOSE_CODE;DOSE_CHN_DESC;ENG_DESC;DESCRIPTION;DOSE_TYPE;PY1;PY2");

            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_TERM", Operator.getIP());
            TParm result = SYSPhaDoseTool.getInstance().insertData(parm);
            if (result.getErrCode() < 0) {
                this.messageBox(result.getErrText());
                return;
            }
            this.messageBox("P0002");
            callFunction(
                "UI|TABLE|addRow",
                parm,
                "SEQ;DOSE_CODE;DOSE_TYPE;DOSE_CHN_DESC;ENG_DESC;DESCRIPTION;OPT_USER;OPT_DATE;OPT_TERM;PY1;PY2 ");

        }
        else {
            return;
        }

    }

    /**
     * 修改
     */
    public void onUpdate() {
        TParm parm = getParmForTag(
            "SEQ;DOSE_CODE;DOSE_CHN_DESC;ENG_DESC;DESCRIPTION;DOSE_TYPE;PY1;PY2");

        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = SYSPhaDoseTool.getInstance().updataData(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }

        int row = (Integer) callFunction("UI|Table|getSelectedRow");
        if (row < 0)
            return;
        // 刷新，设置末行某列的值
        data.setRowData(row, parm);
        callFunction("UI|Table|setRowParmValue", row, data);

        this.messageBox("P0005");
    }

    /**
     * 删除
     */
    public void onDelete() {
        if (this.messageBox("P0009", "是否新增", 2) == 0) {

            TParm parm = getParmForTag("DOSE_CODE");
            TParm result = SYSPhaDoseTool.getInstance().deleteData(parm);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            int row = (Integer) callFunction("UI|Table|getSelectedRow");
            if (row < 0)
                return;
            // 删除单行显示
            this.callFunction("UI|Table|removeRow", row);
            this.callFunction("UI|Table|setSelectRow", row);
            clearValue(
                "SEQ;DOSE_CODE;DOSE_CHN_DESC;ENG_DESC;DESCRIPTION;DOSE_TYPE;PY1;PY2");
            this.messageBox("P0003");
        }
        else {
            return;
        }

    }

    /**
     * 清空
     */
    public void onClear() {
        clearValue("SEQ;DOSE_CODE;DOSE_TYPE;DOSE_CHN_DESC;ENG_DEC;DESCRIPTION;OPT_USER;OPT_DATE;OPT_TERM;PY1;PY2");
        // this.callFunction("UI|Table|removeRowAll");
        selectRow = -1;
        callFunction("UI|delete|setEnabled", false); // 删除不可见
        callFunction("UI|DOSE_CODE|setEnabled", true);

    }

    /**
     * 简拼
     */
    public void onCode() {
        if (String.valueOf(this.getValue("DOSE_CHN_DESC")).length() < 1) {
            return;
        }
        SystemTool st = new SystemTool();
        String value = st.charToCode(String.valueOf(this
            .getValue("DOSE_CHN_DESC")));
        if (null == value || "".equals(value)) {
            return;
        }
        this.setValue("PY1", value);

    }

}
