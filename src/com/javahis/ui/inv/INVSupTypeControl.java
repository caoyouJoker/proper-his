package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.inv.InvSupTypeTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.util.TMessage;

/**
 * <p>Title: 供应类别</p>
 *
 * <p>Description: 供应类别</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.2.22
 * @version 1.0
 */
public class INVSupTypeControl
    extends TControl {

    /**
     * 表格
     */
    private TTable table;

    public INVSupTypeControl() {
    }

    /**
     * 初始化
     */
    public void onInit() {
        table = getTable("TABLE");
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        if (!"".equals(getValueString("SUPTYPE_CODE"))) {
            parm.setData("SUPTYPE_CODE", this.getValueString("SUPTYPE_CODE"));
        }
        if (!"".equals(getValueString("PACK_MODE"))) {
            parm.setData("PACK_MODE", this.getValueString("PACK_MODE"));
        }

        TParm result = InvSupTypeTool.getInstance().onQuery(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        table.setParmValue(result);
    }

    /**
     * 保存方法
     */
    public void onSave() {
        TParm parm = new TParm();
        if ("".equals(getValueString("SUPTYPE_CODE"))) {
            this.messageBox("类别代码不能为空");
            return;
        }
        if ("".equals(getValueString("SUPTYPE_DESC"))) {
            this.messageBox("类别名称不能为空");
            return;
        }
        if ("".equals(getValueString("PACK_MODE"))) {
            this.messageBox("出库方式不能为空");
            return;
        }
        parm.setData("SUPTYPE_CODE",this.getValueString("SUPTYPE_CODE"));
        parm.setData("SUPTYPE_DESC",this.getValueString("SUPTYPE_DESC"));
        parm.setData("PY1",this.getValueString("PY1"));
        parm.setData("PY2",this.getValueString("PY2"));
        parm.setData("PACK_MODE",this.getValueString("PACK_MODE"));
        parm.setData("TYPE_FLG",
                     !"Y".equals(this.getValueString("TYPE_FLG")) ? "N" : "Y");
        parm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());

        TParm result = new TParm();
        if (table.getSelectedRow() < 0) {
            // 新增方法
            result = InvSupTypeTool.getInstance().onInsert(parm);
        }
        else {
            // 更新方法
            result = InvSupTypeTool.getInstance().onUpdate(parm);
        }
        if (result.getErrCode() < 0) {
            this.messageBox("保存失败");
            return;
        }
        this.messageBox("保存成功");
        onQuery();
    }
    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "SUPTYPE_CODE;SUPTYPE_DESC;PACK_MODE;TYPE_FLG;" +
            "PY1;PY2;DESCRIPTION";
        this.clearValue(clearStr);
        table.removeRowAll();
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        if (table.getSelectedRow() < 0) {
            this.messageBox("请选择删除行");
            return;
        }
        TParm parm = table.getParmValue().getRow(table.getSelectedRow());
        TParm result = InvSupTypeTool.getInstance().onDelete(parm);
        if (result.getErrCode() < 0) {
            this.messageBox("删除失败");
            return;
        }
        table.removeRow(table.getSelectedRow());
        this.messageBox("删除成功");
    }

    /**
     * onSupDescAction回车事件
     */
    public void onSupDescAction() {
        String py = TMessage.getPy(this.getValueString("SUPTYPE_DESC"));
        setValue("PY1", py);
    }

    /**
     * 表格单击事件
     */
    public void onTableClick() {
        TParm parm = table.getParmValue().getRow(table.getSelectedRow());
        this.setValueForParm("SUPTYPE_CODE;SUPTYPE_DESC;PACK_MODE;TYPE_FLG;" +
                             "PY1;PY2;DESCRIPTION", parm);
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

}
