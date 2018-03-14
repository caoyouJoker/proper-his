package com.javahis.ui.nss;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.nss.NSSWeeklyMenuTool;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.TMessage;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.nss.NSSMenuTool;
import jdo.nss.NSSPackMTool;

/**
 * <p>Title: 每周套餐设定</p>
 *
 * <p>Description: 每周套餐设定</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSWeeklyMenuControl
    extends TControl {
    public NSSWeeklyMenuControl() {
        super();
    }

    private TTable table;

    /**
     * 初始化方法
     */
    public void onInit() {
        table = getTable("TABLE");
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        String weekly_code = this.getValueString("WEEKLY_CODE");
        if (weekly_code != null && weekly_code.length() > 0) {
            parm.setData("WEEKLY_CODE", weekly_code);
        }
        String diet_type = this.getValueString("DIET_TYPE");
        if (diet_type != null && diet_type.length() > 0) {
            parm.setData("DIET_TYPE", diet_type);
        }
        String pack_code = this.getValueString("PACK_CODE");
        if (pack_code != null && pack_code.length() > 0) {
            parm.setData("PACK_CODE", pack_code);
        }
        TParm result = NSSWeeklyMenuTool.getInstance().onQueryNSSWeeklyMenu(
            parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
        }
        else {
            table.setParmValue(result);
        }
    }

    /**
     * 保存方法
     */
    public void onSave() {
        if (!checkData()) {
            return;
        }
        TParm parm = new TParm();
        parm.setData("WEEKLY_CODE", this.getValueString("WEEKLY_CODE"));
        parm.setData("DIET_TYPE", this.getValueString("DIET_TYPE"));
        parm.setData("PACK_CODE", this.getValueString("PACK_CODE"));
        parm.setData("PACK_CHN_DESC", this.getValueString("PACK_CHN_DESC"));
        parm.setData("PACK_ENG_DESC", this.getValueString("PACK_ENG_DESC"));
        parm.setData("PY1", this.getValueString("PY1"));
        parm.setData("PY2", this.getValueString("PY2"));
        parm.setData("SEQ", this.getValueInt("SEQ"));
        parm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
        parm.setData("PRICE", this.getValueDouble("PRICE"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = new TParm();
        if ( ( (TTextFormat) getComponent("PACK_CODE")).isEnabled()) {
            // 新增数据
            result = NSSWeeklyMenuTool.getInstance().onInsertNSSWeeklyMenu(parm);
        }
        else {
            // 更新数据
            result = NSSWeeklyMenuTool.getInstance().onUpdateNSSWeeklyMenu(parm);
        }
        if (result.getErrCode() < 0) {
            this.messageBox("E0001");
        }
        else {
            this.messageBox("P0001");
            onQuery();
        }
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("请选择删除数据");
        }
        TParm parm = table.getParmValue().getRow(row);
        TParm result = NSSWeeklyMenuTool.getInstance().onDeleteNSSWeeklyMenu(
            parm);
        if (result.getErrCode() < 0) {
            this.messageBox("删除失败");
        }
        else {
            this.messageBox("删除成功");
            table.removeRow(row);
        }
    }

    /**
     * 清空方法
     */
    public void onClear() {
        this.clearValue("WEEKLY_CODE;DIET_TYPE;PACK_CODE;PACK_CHN_DESC;"
                        + "PACK_ENG_DESC;PY1;PY2;SEQ;DESCRIPTION;PRICE");
        table.removeRowAll();
        ( (TComboBox) getComponent("WEEKLY_CODE")).setEnabled(true);
        ( (TTextFormat) getComponent("DIET_TYPE")).setEnabled(true);
        ( (TTextFormat) getComponent("PACK_CODE")).setEnabled(true);
    }

    /**
     * 表格单击事件
     */
    public void onTableClick() {
        this.setValueForParm("WEEKLY_CODE;DIET_TYPE;PACK_CODE;PACK_CHN_DESC;"
                             + "PACK_ENG_DESC;PY1;PY2;SEQ;DESCRIPTION;PRICE",
                             table.getParmValue().getRow(table.getSelectedRow()));
        ( (TComboBox) getComponent("WEEKLY_CODE")).setEnabled(false);
        ( (TTextFormat) getComponent("DIET_TYPE")).setEnabled(false);
        ( (TTextFormat) getComponent("PACK_CODE")).setEnabled(false);
    }

    /**
     * 周套餐中文回车事件
     */
    public void onPackDescAction() {
        String py = TMessage.getPy(this.getValueString("PACK_CHN_DESC"));
        setValue("PY1", py);
    }

    /**
     * 套餐代码变更事件
     */
    public void onChangeNSSPack() {
        String pack_code = this.getValueString("PACK_CODE");
        String pack_chn_desc = "";
        String pack_eng_desc = "";
        String py1 = "";
        String py2 = "";
        double price = 0;
        if (pack_code != null && pack_code.length() > 0) {
            TParm parm = new TParm();
            parm.setData("PACK_CODE", pack_code);
            TParm result = NSSPackMTool.getInstance().onQueryNSSPackM(parm);
            if (result == null || result.getCount() <= 0) {
                this.messageBox("套餐代码设定错误");
                return;
            }
            pack_chn_desc = result.getValue("PACK_CHN_DESC", 0);
            pack_eng_desc = result.getValue("PACK_ENG_DESC", 0);
            py1 = result.getValue("PY1", 0);
            py2 = result.getValue("PY2", 0);
            price = result.getDouble("PRICE", 0);
        }
        this.setValue("PACK_CHN_DESC", pack_chn_desc);
        this.setValue("PACK_ENG_DESC", pack_eng_desc);
        this.setValue("PY1_3", py1);
        this.setValue("PY2_3", py2);
        this.setValue("PRICE", price);
    }

    /**
     * 数据检核
     * @return boolean
     */
    private boolean checkData() {
        String weekly_code = this.getValueString("WEEKLY_CODE");
        if (weekly_code == null || weekly_code.length() <= 0) {
            this.messageBox("日期不能为空");
            return false;
        }
        String meal_chn_desc = this.getValueString("DIET_TYPE");
        if (meal_chn_desc == null || meal_chn_desc.length() <= 0) {
            this.messageBox("饮食区分不能为空");
            return false;
        }
        String pack_code = this.getValueString("PACK_CODE");
        if (pack_code == null || pack_code.length() <= 0) {
            this.messageBox("套餐代码不能为空");
            return false;
        }
        String price = this.getValueString("PRICE");
        if (price == null || price.length() <= 0) {
            this.messageBox("价格不能为空");
            return false;
        }
        if (StringTool.getDouble(price) <= 0) {
            this.messageBox("价格不能小于或等于0");
            return false;
        }
        return true;
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
