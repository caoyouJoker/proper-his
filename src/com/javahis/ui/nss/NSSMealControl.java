package com.javahis.ui.nss;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.nss.NSSMealTool;
import com.dongyang.ui.TTextField;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.util.TMessage;

/**
 * <p>Title: 餐次设置档</p>
 *
 * <p>Description: 餐次设置档</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSMealControl
    extends TControl {
    public NSSMealControl() {
        super();
    }

    private TTable table;

    /**
     * 初始化方法
     */
    public void onInit() {
        table = getTable("TABLE");
        onQuery();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        String meal_code = this.getValueString("MEAL_CODE");
        if (meal_code != null && meal_code.length() > 0) {
            parm.setData("MEAL_CODE", meal_code);
        }
        TParm result = NSSMealTool.getInstance().onQueryNSSMeal(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
        }
        else {
            String meal_time = "";
            String stop_order_time = "";
            for (int i = 0; i < result.getCount(); i++) {
                meal_time = result.getValue("MEAL_TIME", i);
                result.setData("MEAL_TIME", i,
                               meal_time.substring(0, 2) + ":" +
                               meal_time.substring(2, 4));
                stop_order_time = result.getValue("STOP_ORDER_TIME", i);
                result.setData("STOP_ORDER_TIME", i,
                               stop_order_time.substring(0, 2) + ":" +
                               stop_order_time.substring(2, 4));
            }
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
        parm.setData("MEAL_CODE", this.getValueString("MEAL_CODE"));
        parm.setData("MEAL_CHN_DESC", this.getValueString("MEAL_CHN_DESC"));
        parm.setData("MEAL_ENG_DESC", this.getValueString("MEAL_ENG_DESC"));
        parm.setData("PY1", this.getValueString("PY1"));
        parm.setData("PY2", this.getValueString("PY2"));
        parm.setData("SEQ", this.getValueInt("SEQ"));
        parm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
        String meal_time = this.getValueString("MEAL_TIME");
        parm.setData("MEAL_TIME",
                     meal_time.substring(11, 13) + meal_time.substring(14, 16));
        String stop_order_time = this.getValueString("STOP_ORDER_TIME");
        parm.setData("STOP_ORDER_TIME",
                     stop_order_time.substring(11, 13) +
                     stop_order_time.substring(14, 16));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        //System.out.println("parm----"+parm);
        TParm result = new TParm();
        if ( ( (TTextField) getComponent("MEAL_CODE")).isEnabled()) {
            // 新增数据
            result = NSSMealTool.getInstance().onInsertNSSMeal(parm);
        }
        else {
            // 更新数据
            result = NSSMealTool.getInstance().onUpdateNSSMeal(parm);
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
        TParm result = NSSMealTool.getInstance().onDeleteNSSMeal(parm);
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
        this.clearValue("MEAL_CODE;MEAL_CHN_DESC;MEAL_ENG_DESC;PY1;"
                        + "PY2;SEQ;DESCRIPTION;MEAL_TIME;STOP_ORDER_TIME");
        table.removeRowAll();
        ( (TTextField) getComponent("MEAL_CODE")).setEnabled(true);
    }

    /**
     * 表格单击事件
     */
    public void onTableClick() {
        this.setValueForParm("MEAL_CODE;MEAL_CHN_DESC;MEAL_ENG_DESC;PY1;"
                             + "PY2;SEQ;DESCRIPTION;MEAL_TIME;STOP_ORDER_TIME",
                             table.getParmValue().getRow(table.getSelectedRow()));
        ( (TTextField) getComponent("MEAL_CODE")).setEnabled(false);
    }

    /**
     * 餐次中文说明回车事件
     */
    public void onMealDescAction(){
        String py = TMessage.getPy(this.getValueString("MEAL_CHN_DESC"));
        setValue("PY1", py);
    }

    /**
     * 数据检核
     * @return boolean
     */
    private boolean checkData() {
        String meal_code = this.getValueString("MEAL_CODE");
        if (meal_code == null || meal_code.length() <= 0) {
            this.messageBox("餐次代码不能为空");
            return false;
        }
        String meal_chn_desc = this.getValueString("MEAL_CHN_DESC");
        if (meal_chn_desc == null || meal_chn_desc.length() <= 0) {
            this.messageBox("餐次中文说明不能为空");
            return false;
        }
        String meal_time = this.getValueString("MEAL_TIME");
        if (meal_time == null || meal_time.length() <= 0) {
            this.messageBox("用餐时间不能为空");
            return false;
        }
        String stop_order_time = this.getValueString("STOP_ORDER_TIME");
        if (stop_order_time == null || stop_order_time.length() <= 0) {
            this.messageBox("停止订餐时间不能为空");
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
