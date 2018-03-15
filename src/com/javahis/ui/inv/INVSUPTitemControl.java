package com.javahis.ui.inv;

import com.dongyang.ui.TTable;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.sys.SYSHzpyTool;
import jdo.inv.INVSQL;


/**
 * <p>Title: 灭菌记账字典维护</p>
 *
 * <p>Description: 灭菌记账字典维护</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author wangm 2013-07-08
 * @version 1.0
 */
public class INVSUPTitemControl
    extends TControl {

    /**
     * 物资主表
     */
    private TTable table;


    public void onInit() {
        super.onInit();
        //项目主表
        table = (TTable) getComponent("TABLE");
        initTable();
    }

    /**
     * 初始化table
     */
    public void initTable() {
        table.setSQL("SELECT * FROM INV_SUPTITEM WHERE SUPITEM_CODE IS NULL");
        table.retrieve();
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = getQueryValue();
        String sql = INVSQL.getQuerySuptitemSql(parm);
        table.setSQL(sql);
        table.retrieve();
        table.setDSValue();
    }

    /**
     * 删除
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("没有选择删除数据");
            return;
        }
        table.removeRow(row);
        if (!table.update()) {
            messageBox("删除失败!");
            return;
        }
        messageBox("删除成功!");
        this.onClear();
    }

    /**
     * 得到查询条件
     * @return TParm
     */
    public TParm getQueryValue() {
        TParm parm = new TParm();
        parm.setData("SUPITEM_CODE", getValueString("SUPITEM_CODE"));
        parm.setData("SUPITEM_DESC", getValueString("SUPITEM_DESC"));
        parm.setData("PY1", getValueString("PY1").toUpperCase());
        return parm;
    }

    /**
     * table的点击事件
     */
    public void onClickedTable() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        //table里面的数据
        TParm tableValue = table.getDataStore().getBuffer(table.getDataStore().
            PRIMARY);
        setTextValue(tableValue.getRow(row));
        setTextEnabled(false);
    }

    /**
     * 数据上翻
     * @param parm TParm
     */
    public void setTextValue(TParm parm) {
        this.setValueForParm(
            "SUPITEM_CODE;SUPITEM_DESC;PY1;COST_PRICE;ADD_PRICE;DESCRIPTION",
            parm);
    }

    /**
     * 项目代码的可编辑
     * @param boo boolean
     */
    public void setTextEnabled(boolean boo) {
        this.callFunction("UI|SUPITEM_CODE|setEnabled", boo);
    }

    /**
     * 清空
     */
    public void onClear() {
        setTextEnabled(true);
        this.clearValue(
            "SUPITEM_CODE;SUPITEM_DESC;PY1;COST_PRICE;ADD_PRICE;DESCRIPTION");
        table.clearSelection();
    }

    /**
     * 项目名称回车事件
     */
    public void onDescEnter() {
        String py = SYSHzpyTool.getInstance().charToCode(getValueString(
            "SUPITEM_DESC"));
        setValue("PY1", py);
    }

    /**
     * 保存
     * @return boolean
     */
    public boolean onSave() {
        int row = table.getSelectedRow();
        boolean saveFlg = false;
        //如果有选中则为更新
        if (row >= 0) {
            //处理更新数据
            if (!dealUpdate(row, saveFlg))
                return false;
        }
        else {
            //处理新增数据
            if (!dealNewData())
                return false;
        }
        //保存数据方法
        return saveData();
    }

    /**
     * 调用保存方法
     * @return boolean
     */
    public boolean saveData() {
        if (!table.update()) {
            messageBox("保存失败!");
            return false;
        }
        messageBox("保存成功!");
        table.resetModify();
        onClear();
        return true;
    }

    /**
     * 处理新增
     * @return boolean
     */
    public boolean dealNewData() {
        String supitemCode = getValueString("SUPITEM_CODE");
        if (!checkSupItemCode(supitemCode))
            return false;
        //名称
        String supitemDesc = getValueString("SUPITEM_DESC");
        //检核名称
        if (!checkItemDesc(supitemDesc))
            return false;
        //得到结成本价格
        double costPrice = getValueDouble("COST_PRICE");
        if (costPrice < 0) {
            messageBox("成本不能小于零!");
            return false;
        }
        //得到附加金额
        double addPrice = getValueDouble("ADD_PRICE");
        if (addPrice < 0) {
            messageBox("附加金额不能小于零!");
            return false;
        }
        //处理新增数据
        setNewData();
        return true;
    }

    /**
     * 检核代码
     * @param supItemCode String
     * @return boolean
     */
    public boolean checkSupItemCode(String supItemCode) {
        if (supItemCode == null || supItemCode.length() == 0) {
            messageBox("代码不能为空");
            return false;
        }
        if (table.getDataStore().exist("SUPITEM_CODE='" + supItemCode + "'")) {
            messageBox("代码" + supItemCode + "重复");
            return false;
        }
        return true;
    }

    /**
     * 处理新增数据
     */
    public void setNewData() {
        int row = table.addRow();
        table.setItem(row, "SUPITEM_CODE", getValueString("SUPITEM_CODE"));
        table.setItem(row, "SUPITEM_DESC", getValueString("SUPITEM_DESC"));
        table.setItem(row, "PY1", getValueString("PY1"));
        table.setItem(row, "COST_PRICE", getValueDouble("COST_PRICE"));
        table.setItem(row, "ADD_PRICE", getValueDouble("ADD_PRICE"));
        table.setItem(row, "DESCRIPTION", getValue("DESCRIPTION"));
        //添加固定参数
        setTableData(row);
    }

    /**
     * 处理更新数据
     * @param row int
     * @param saveFlg boolean
     * @return boolean
     */
    public boolean dealUpdate(int row, boolean saveFlg) {
        //名称
        String supitemDesc = getValueString("SUPITEM_DESC");
        //检核名称
        if (!checkItemDesc(supitemDesc))
            return false;
        String desc = table.getItemString(row, "SUPITEM_DESC");
        //如果有修改则更新
        if (!supitemDesc.equals(desc)) {
            table.setItem(row, "SUPITEM_DESC", supitemDesc);
            //记录是否有修改
            saveFlg = true;
        }
        //得到结成本价格
        double costPrice = getValueDouble("COST_PRICE");
        if (costPrice < 0) {
            messageBox("成本不能小于零!");
            return false;
        }
        double price = table.getItemDouble(row, "COST_PRICE");
        if (costPrice != price) {
            table.setItem(row, "COST_PRICE", costPrice);
            //记录是否有修改
            saveFlg = true;
        }

        //得到附加金额
        double addPrice = getValueDouble("ADD_PRICE");
        if (addPrice < 0) {
            messageBox("附加金额不能小于零!");
            return false;
        }
        price = table.getItemDouble(row, "ADD_PRICE");
        if (addPrice != price) {
            table.setItem(row, "ADD_PRICE", addPrice);
            //记录是否有修改
            saveFlg = true;
        }
        //名称
        String decription = getValueString("DESCRIPTION");
        String decript = table.getItemString(row, "DESCRIPTION");
        //如果有修改则更新
        if (!decription.equals(decript)) {
            table.setItem(row, "DESCRIPTION", decription);
            //记录是否有修改
            saveFlg = true;
        }
        //如果有修改则修改固定数据
        if (saveFlg)
            setTableData(row);
        return true;
    }

    /**
     * 修固定数据
     * @param row int
     */
    public void setTableData(int row) {
        table.setItem(row, "OPT_USER", Operator.getID());
        table.setItem(row, "OPT_DATE", SystemTool.getInstance().getDate());
        table.setItem(row, "OPT_TERM", Operator.getIP());
    }

    /**
     * 检核名称
     * @param supitemItem String
     * @return boolean
     */
    public boolean checkItemDesc(String supitemItem) {
        if (supitemItem == null || supitemItem.length() == 0) {
            messageBox("名称不能为空");
            return false;
        }
        return true;
    }

}
