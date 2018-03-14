package com.javahis.ui.ope;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TMenuItem;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.ope.OPEDeptOpTool;
import java.util.Vector;
import com.dongyang.ui.event.TPopupMenuEvent;

/**
 * <p>Title: 科常用手术</p>
 *
 * <p>Description: 科常用手术</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-24
 * @version 4.0
 */
public class OPEDeptOpControl
    extends TControl {
    TParm data;
    int selectRow = -1;

    public void onInit() {
        super.onInit();
        ( (TTable) getComponent("Table")).addEventListener("Table->"
            + TTableEvent.CLICKED, this, "onTableClicked");
        //手术弹出窗口
        callFunction("UI|OP_CODE|setPopupMenuParameter", "OPICD",
                     "%ROOT%\\config\\sys\\SYSOpICD.x");
        //接受手术弹窗回传值
        callFunction("UI|OP_CODE|addEventListener",
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
            "DEPT_CODE;OP_CODE;OPT_CHN_DESC;SEQ",
            data, row);
        selectRow = row;
        // 不可编辑
        callFunction("UI|DEPT_CODE|setEnabled", false);
        callFunction("UI|OP_CODE|setEnabled", false);
        // 设置删除按钮状态
        ((TMenuItem) getComponent("delete")).setEnabled(true);
    }

    /**
     * 新增
     */
    public void onInsert() {
        if(this.getValueString("DEPT_CODE").trim().length()<=0){
            this.messageBox_("请选择科室");
            return;
        }
        if(this.getText("OP_CODE").trim().length()<=0){
            this.messageBox_("请填写手术ICD");
            return;
        }
        TParm parm = this.getParmForTag("DEPT_CODE;OP_CODE;OPT_CHN_DESC;SEQ");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = OPEDeptOpTool.getInstance().insertdata(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            this.messageBox("E0005");
            return;
        }
        // 显示新增数据
        int row = ( (TTable) getComponent("Table"))
            .addRow(
                parm,
                "DEPT_CODE;OP_CODE;OPT_CHN_DESC;SEQ;OPT_USER;OPT_DATE;OPT_TERM");
        data.setRowData(row, parm);
        this.messageBox("P0005"); //成功
        this.clearValue("DEPT_CODE;OP_CODE;OPT_CHN_DESC;SEQ");
        ((TTable) getComponent("Table")).clearSelection();
        selectRow = -1;
        // 设置删除按钮状态
        ((TMenuItem) getComponent("delete")).setEnabled(false);
        callFunction("UI|DEPT_CODE|setEnabled", true);
        callFunction("UI|OP_CODE|setEnabled", true);
        setSEQ();//设置最大序号

    }

    /**
     * 更新
     */
    public void onUpdate() {
        TParm parm = this.getParmForTag("DEPT_CODE;OP_CODE;OPT_CHN_DESC;SEQ");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = OPEDeptOpTool.getInstance().updatedata(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            this.messageBox("E0005");
            return;
        }
        // 选中行
        int row = ( (TTable) getComponent("Table")).getSelectedRow();
        if (row < 0)
            return;
        // 刷新，设置末行某列的值
        data.setRowData(row, parm);
        ( (TTable) getComponent("Table")).setRowParmValue(row, data);
        this.messageBox("P0005"); //成功

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
        String DEPT_CODE = getValue("DEPT_CODE").toString().trim();
        String OP_CODE = getText("OP_CODE").trim();
        TParm parm = new TParm();
        if(DEPT_CODE.trim().length()>0)
            parm.setData("DEPT_CODE",DEPT_CODE);
        if(OP_CODE.trim().length()>0)
            parm.setData("OP_CODE",OP_CODE);
        data = OPEDeptOpTool.getInstance().selectData(parm);
        // 判断错误值
        if (data.getErrCode() < 0) {
            this.messageBox("E0005");
            return;
        }
        ((TTable) getComponent("Table")).setParmValue(data);
    }
    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("DEPT_CODE;OP_CODE;OPT_CHN_DESC;SEQ");
        ((TTable) getComponent("Table")).clearSelection();
        selectRow = -1;
        // 设置删除按钮状态
        ((TMenuItem) getComponent("delete")).setEnabled(false);
        callFunction("UI|DEPT_CODE|setEnabled", true);
        callFunction("UI|OP_CODE|setEnabled", true);
        onQuery();
        setSEQ();//设置最大序号
    }

    /**
     * 删除
     */
    public void onDelete() {
        if (this.messageBox("提示", "是否删除", 2) == 0) {
            if (selectRow == -1)
                return;
            String code = getValue("DEPT_CODE").toString();
            String opICD = getText("OP_CODE");
            TParm result = OPEDeptOpTool.getInstance().deletedata(code,opICD);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            TTable table = ( (TTable) getComponent("Table"));
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            this.messageBox("P0005"); //成功
            onClear();
        }
        else {
            return;
        }
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
            this.setValue("SEQ", seq + 1);
        }
    }
    /**
     * 手术ICD选择返回数据处理
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        if (obj == null){
            this.clearValue("OPT_CHN_DESC;OP_CODE");
            return;
        }
        TParm returnParm = (TParm) obj;
        this.setValue("OPT_CHN_DESC",returnParm.getValue("OPT_CHN_DESC"));
        this.setValue("OP_CODE",returnParm.getValue("OPERATION_ICD"));
        if(this.getValueString("OP_CODE").trim().length()<=0){
            this.setValue("OP_CODE","");
        }
    }
}
