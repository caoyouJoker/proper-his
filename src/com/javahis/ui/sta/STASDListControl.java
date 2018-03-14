package com.javahis.ui.sta;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TMenuItem;
import jdo.sys.Operator;
import java.util.Vector;
import jdo.sta.STASDListTool;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.util.TMessage;
import com.dongyang.manager.TCM_Transform;

/**
 * <p>Title: 单病种维护</p>
 *
 * <p>Description: 单病种维护</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-11-6
 * @version 3.0
 */
public class STASDListControl extends TControl {
    TParm data;
    int selectRow = -1;
    public void onInit(){
        super.onInit();
        ((TTable) getComponent("TABLE")).addEventListener("TABLE->"
            + TTableEvent.CLICKED, this, "onTableClicked");
        onClear();

    }
    /**
     * 增加对Table的监听
     *
     * @param row
     * int
     */
    public void onTableClicked(int row) {
        // 选中行
        if (row < 0)
            return;
        setValueForParm(
            "SEQ;SD_DESC;PY;CONDITION",
            data, row);
        selectRow = row;
        // 不可编辑
        ( (TTextField) getComponent("SEQ")).setEnabled(false);
        // 设置删除按钮状态
        ( (TMenuItem) getComponent("delete")).setEnabled(true);
    }

    /**
     * 新增
     */
    public void onInsert() {
        if (this.getText("SEQ").trim().length()<=0) {
            this.messageBox_("序号不能为空!");
            return;
        }
        TParm parm = getParmForTag("SEQ;SD_DESC;PY;CONDITION");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = STASDListTool.getInstance().insertData(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox_(result.getErrText());
            return;
        }
        // 显示新增数据
        int row = ( (TTable) getComponent("TABLE"))
            .addRow(
                parm,
                "SEQ;SD_DESC;PY;CONDITION;OPT_USER;OPT_DATE;OPT_TERM");
        data.setRowData(row, parm);
        this.clearValue("SEQ;SD_DESC;PY;CONDITION");
        setSEQ();//设置最大排序号
        this.messageBox_("保存成功！");
    }

    /**
     * 更新
     */
    public void onUpdate() {
        TParm parm = getParmForTag("SEQ;SD_DESC;PY;CONDITION");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = STASDListTool.getInstance().updateData(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox_(result.getErrText());
            return;
        }
        // 选中行
        int row = ( (TTable) getComponent("TABLE")).getSelectedRow();
        if (row < 0)
            return;
        // 刷新，设置末行某列的值
        data.setRowData(row, parm);
        ( (TTable) getComponent("TABLE")).setRowParmValue(row, data);
        this.messageBox_("修改成功！");
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
            TParm parm = new TParm();
            parm.setData("SEQ",getText("SEQ"));
            TParm result = STASDListTool.getInstance().deleteData(parm);
            if (result.getErrCode() < 0) {
                messageBox_(result.getErrText());
                return;
            }
            TTable table = ( (TTable) getComponent("TABLE"));
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            this.messageBox_("删除成功！");
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
        TParm parm = new TParm();
        if(getText("SEQ").trim().length()>0)
            parm.setData("SEQ", getText("SEQ"));
        if(getText("SD_DESC").trim().length()>0)
            parm.setData("SD_DESC", "%"+ getText("SD_DESC") + "%");
        data = STASDListTool.getInstance().selectData(parm);
        // 判断错误值
        if (data.getErrCode() < 0) {
            messageBox_(data.getErrText());
            return;
        }
        ( (TTable) getComponent("TABLE")).setParmValue(data);
    }

    /**
     * 清空
     */
    public void onClear() {
        clearValue("SEQ;SD_DESC;PY;CONDITION");
        ( (TTable) getComponent("TABLE")).clearSelection();
        selectRow = -1;
        ( (TTextField) getComponent("SEQ")).setEnabled(true);
        // 设置删除按钮状态
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        onQuery();
        setSEQ();//设置最大排序号
    }
    /**
     * 设置最大排序号 SEQ
     */
    public void setSEQ() {
        long seq = 0;
        // 取SEQ最大值
        if (data.existData("SEQ")) {
            Vector vct = data.getVectorValue("SEQ");
            for (int i = 0; i < vct.size(); i++) {
                long a = Long.parseLong( (vct.get(i)).toString().trim());
                if (a > seq)
                    seq = a;
            }
            this.setValue("SEQ", seq + 1 + "");
        }
    }
    /**
     * 根据汉字输出拼音首字母
     *
     * @return Object
     */
    public Object onCode() {
        if (TCM_Transform.getString(this.getValue("SD_DESC")).length() <
            1) {
            return null;
        }
        String value = TMessage.getPy(this.getValueString("SD_DESC"));
        if (null == value || value.length() < 1) {
            return null;
        }
        this.setValue("PY", value);
        // 光标下移
        ( (TTextField) getComponent("PY")).grabFocus();
        return null;
    }

}
