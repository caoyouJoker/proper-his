package com.javahis.ui.mro;

import jdo.mro.MROLendTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;

/**
 * <p>Title: 病案借阅字典</p>
 *
 * <p>Description: 病案借阅字典</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-6
 * @version 1.0
 */
public class MROLendControl
    extends TControl {
    TParm data;
    int selectRow = -1;
    public void onInit() {
        super.onInit();
        ( (TTable) getComponent("Table")).addEventListener("Table->"
            + TTableEvent.CLICKED, this, "onTableClicked");
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
            "LEND_CODE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION",
            data, row);
        selectRow = row;
        // 不可编辑
        ((TTextField) getComponent("LEND_CODE")).setEnabled(false);
        // 设置删除按钮状态
        ((TMenuItem) getComponent("delete")).setEnabled(true);
    }
    /**
     * 新增
     */
    public void onInsert() {
        if(this.getText("LEND_CODE").trim().length()<=0||this.getText("LEND_DESC").trim().length()<=0){
            this.messageBox("病案借阅原因编码和说明不能为空！");
            return;
        }
        TParm parm = this.getParmForTag("LEND_CODE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = MROLendTool.getInstance().insertdata(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        // 显示新增数据
        int row = ( (TTable) getComponent("TABLE"))
            .addRow(
                parm,
                "LEND_CODE;LEND_DESC;PY1;PY2;DESCRIPTION;LEND_DAY;VALID_DAY;OPT_USER;OPT_DATE;OPT_TERM");

        data.setRowData(row, parm);
        this.messageBox("添加成功！");
    }
    /**
     * 更新
     */
    public void onUpdate() {
        if(this.getText("LEND_CODE").trim().length()<=0||this.getText("LEND_DESC").trim().length()<=0){
            this.messageBox("病案借阅原因编码和说明不能为空！");
            return;
        }
        TParm parm = this.getParmForTag("LEND_CODE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());

        TParm result = MROLendTool.getInstance().updatedata(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        // 选中行
        int row = ( (TTable) getComponent("Table")).getSelectedRow();
        if (row < 0)
            return;
        // 刷新，设置末行某列的值
        data.setRowData(row, parm);
        ( (TTable) getComponent("Table")).setRowParmValue(row, data);
        this.messageBox("修改成功！");
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
     * 查询
     */
    public void onQuery() {
        data = MROLendTool.getInstance().selectdata(getText("LEND_CODE").trim(),getText("LEND_DESC").trim());
        // 判断错误值
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ((TTable) getComponent("Table")).setParmValue(data);
        ((TTextField)this.getComponent("LEND_CODE")).setEnabled(true);
        this.clearValue("LEND_CODE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION");
    }
    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("LEND_CODE;LEND_DESC;PY1;PY2;LEND_DAY;VALID_DAY;DESCRIPTION");
        ((TTable) getComponent("Table")).clearSelection();
        selectRow = -1;
        // 设置删除按钮状态
        ((TMenuItem) getComponent("delete")).setEnabled(false);
        ((TTextField) getComponent("LEND_CODE")).setEnabled(true);
        onQuery();
    }
    /**
     * 删除
     */
    public void onDelete() {
        if (this.messageBox("提示", "是否删除", 2) == 0) {
            if (selectRow == -1)
                return;
            String code = getValue("LEND_CODE").toString();
            TParm result = MROLendTool.getInstance().deletedata(code);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
//            TTable table = ( (TTable) getComponent("Table"));
//            int row = table.getSelectedRow();
//            if (row < 0)
//                return;
            this.messageBox("删除成功！");
            onClear();
        }
        else {
            return;
        }
    }
    /**
     * 根据汉字输出拼音首字母
     *
     * @return Object
     */
    public Object onCode() {
        if (TCM_Transform.getString(this.getValue("LEND_DESC")).length() <
            1) {
            return null;
        }
        String value = TMessage.getPy(this.getValueString("LEND_DESC"));
        if (null == value || value.length() < 1) {
            return null;
        }
        this.setValue("PY1", value);
        // 光标下移
        ( (TTextField) getComponent("PY1")).grabFocus();
        return null;
    }
}
