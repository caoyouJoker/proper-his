package com.javahis.ui.adm;

import jdo.sys.Operator;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import jdo.reg.RegMethodTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import java.util.Vector;
import com.dongyang.control.TControl;
import com.dongyang.ui.event.TPopupMenuEvent;

/**
 * <p>Title:护理等级 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ADMNursingClassControl extends TControl {
    public ADMNursingClassControl() {
    }
    TParm data;
    int selectRow = -1;

    public void onInit() {
            super.onInit();
            ((TTable) getComponent("Table")).addEventListener("Table->"
                            + TTableEvent.CLICKED, this, "onTableClicked");
            //只有text有这个方法，调用sys_fee弹出框
        callFunction("UI|ORDER_CODE|setPopupMenuParameter","aaa","%ROOT%\\config\\sys\\SYSICDPopup.x");

        //textfield接受回传值
        callFunction("UI|ORDER_CODE|addEventListener",TPopupMenuEvent.RETURN_VALUE,this,"popReturn");

            onClear();
    }
    /**
 * 诊断事件
 * @param tag String
 * @param obj Object
 */
public void popReturn(String tag,Object obj)
       {
           TParm parm=(TParm)obj;
           parm.getValue("ICD_CHN_DESC");
           this.setValue("ORDER_CODE",parm.getValue("ICD_CODE"));
           this.setValue("ORDER_DESC",parm.getValue("ICD_CHN_DESC"));

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
                            "NURSING_CLASS_CODE;NURSING_CLASS_DESC;ORDER_CODE;COLOUR_RED;COLOUR_GREEN;COLOUR_BLUE;PSFKIN_CODE",
                            data, row);
            selectRow = row;
            // 不可编辑
            ((TTextField) getComponent("NURSING_CLASS_CODE")).setEnabled(false);
            // 设置删除按钮状态
            ((TMenuItem) getComponent("delete")).setEnabled(true);
    }

    /**
     * 新增
     */
    public void onInsert() {
            if (!emptyTextCheck("NURSING_CLASS_CODE,NURSING_CLASS_DESC")) {
                    return;
            }
            TParm parm = getParmForTag("NURSING_CLASS_CODE;NURSING_CLASS_DESC;ORDER_CODE;COLOUR_RED;COLOUR_GREEN:COLOUR_BLUE;PSFKIN_CODE");
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_TERM", Operator.getIP());
            TParm result = RegMethodTool.getInstance().insertdata(parm);
            // 判断错误值
            if (result.getErrCode() < 0) {
                    messageBox(result.getErrText());
                    return;
            }
            // 显示新增数据
            ((TTable) getComponent("TABLE"))
                            .addRow(
                                            parm,
                                            "REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ;DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG;OPT_USER;OPT_DATE;OPT_TERM");
            int row = data.insertRow();
            data.setRowData(row, parm);
            this.messageBox("P0001");
    }

    /**
     * 更新
     */
    public void onUpdate() {
            TParm parm = getParmForTag("REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ:int;DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG");
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_TERM", Operator.getIP());
            TParm result = RegMethodTool.getInstance().updatedata(parm);
            // 判断错误值
            if (result.getErrCode() < 0) {
                    messageBox(result.getErrText());
                    return;
            }
            // 选中行
            int row = ((TTable) getComponent("TABLE")).getSelectedRow();
            if (row < 0)
                    return;
            // 刷新，设置末行某列的值
            data.setRowData(row, parm);
            ((TTable) getComponent("TABLE")).setRowParmValue(row, data);
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
            ((TTable) getComponent("TABLE")).setParmValue(data);
    }

    /**
     * 清空
     */
    public void onClear() {
            clearValue("REGMETHOD_CODE;REGMETHOD_DESC;PY1;PY2;SEQ;DESCRIPTION;APPT_WEEK;MISSVST_FLG;COMBO_FLG;READIC_FLG");
            ((TTable) getComponent("TABLE")).clearSelection();
            selectRow = -1;
            ((TTextField) getComponent("REGMETHOD_CODE")).setEnabled(true);
            ((TTextField) getComponent("REGMETHOD_DESC")).setEnabled(true);
            // 设置删除按钮状态
            ((TMenuItem) getComponent("delete")).setEnabled(false);
            onQuery();
            long seq = 0;
            // 取SEQ最大值
            if (data.existData("SEQ")) {
                    Vector vct = data.getVectorValue("SEQ");
                    for (int i = 0; i < vct.size(); i++) {
                            long a = Long.parseLong((vct.get(i)).toString().trim());
                            if (a > seq)
                                    seq = a;
                    }
                    this.setValue("SEQ", seq + 1);
            }
    }

}
