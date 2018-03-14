package com.javahis.ui.ind;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import java.awt.event.KeyEvent;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextFormat;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import java.util.Calendar;
import java.util.Date;
import com.dongyang.util.TypeTool;
import jdo.ind.IndValidAndQtyWarnTool;
import jdo.sys.SystemTool;
import jdo.sys.Operator;

/**
 * <p>Title: 近效期及库存量提示</p>
 *
 * <p>Description: 近效期及库存量提示</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangy 2010.10.28
 * @version 1.0
 */
public class INDValidAndQtyWarnControl
    extends TControl {

    private TPanel panel_0;

    private TTable table_a;

    private TTable table_b;

    public INDValidAndQtyWarnControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 注册激发SYSFeePopup弹出的事件
        callFunction("UI|ORDER_CODE_A|addEventListener", "ORDER_CODE_A->"
                     + TKeyListener.KEY_PRESSED, this,
                     "onCreateEditComoponentUD_A");
        // 注册激发SYSFeePopup弹出的事件
        callFunction("UI|ORDER_CODE_B|addEventListener", "ORDER_CODE_B->"
                     + TKeyListener.KEY_PRESSED, this,
                     "onCreateEditComoponentUD_B");
        panel_0 = getPanel("TPanel_0");
        table_a = getTable("TABLE_A");
        table_b = getTable("TABLE_B");
        String dept_code = Operator.getDept();
        this.setValue("ORG_CODE_A", dept_code);
        this.setValue("ORG_CODE_B", dept_code);
        //onQuery(); by liyh 20120810 去掉初始化查询
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        TParm result = new TParm();
        String org_code = "";
        String order_code = "";

        if (panel_0.isShowing()) {
            //部门代码
            org_code = getValueString("ORG_CODE_A");
            if (org_code == null || org_code.length() <= 0) {
                this.messageBox("请选择查询部门");
                return;
            }
            parm.setData("ORG_CODE", org_code);
            String date = StringTool.getString(SystemTool.getInstance().getDate(),
                                           "yyyyMMdd");
            //期限限制
            if (getRadioButton("VALID_DATE_A").isSelected()) {
                parm.setData("VALID_DATE",
                             rollMonth(date.substring(0, 6), date.substring(6, 8), 3));
            }
            else if (getRadioButton("VALID_DATE_B").isSelected()) {
                parm.setData("VALID_DATE",
                             rollMonth(date.substring(0, 6), date.substring(6, 8), 6));
            }
            else {
                String valid_date = getValueString("VALID_DATE");
                parm.setData("VALID_DATE", valid_date.substring(0, 4) +
                             valid_date.substring(5, 7) + valid_date.substring(8, 10));
            }
            //药品代码
            order_code = getValueString("ORDER_CODE_A");
            if (order_code != null && order_code.length() > 0) {
                parm.setData("ORDER_CODE", order_code);
            }
            //近效期查询
            result = IndValidAndQtyWarnTool.getInstance().onQueryValid(parm);
            if (result == null || result.getCount("ORDER_CODE") <= 0) {
                this.messageBox("没有查询数据");
                table_a.removeRowAll();
                return;
            }
            table_a.setParmValue(result);
        }
        else {
            //部门代码
            org_code = getValueString("ORG_CODE_B");
            if (org_code == null || org_code.length() <= 0) {
                this.messageBox("请选择查询部门");
                return;
            }
            parm.setData("ORG_CODE", org_code);
            //药品代码
            order_code = getValueString("ORDER_CODE_B");
            if (order_code != null && order_code.length() > 0) {
                parm.setData("ORDER_CODE", order_code);
            }
            //库存量
            if (getRadioButton("STOCK_QTY_A").isSelected()) {
                parm.setData("STOCK_QTY_A", "STOCK_QTY_A");
            }
            else if (getRadioButton("STOCK_QTY_B").isSelected()) {
                parm.setData("STOCK_QTY_B", "STOCK_QTY_B");
            }
            else {
                parm.setData("STOCK_QTY_C", getValue("STOCK_QTY_C"));
            }
            //库存量查询
            result = IndValidAndQtyWarnTool.getInstance().onQueryQty(parm);
            if (result == null || result.getCount("ORDER_CODE") <= 0) {
                this.messageBox("没有查询数据");
                table_b.removeRowAll();
                return;
            }
            table_b.setParmValue(result);
        }
    }

    /**
     * 清空方法
     */
    public void onClear() {
        if (panel_0.isShowing()) {
            getRadioButton("VALID_DATE_A").setSelected(true);
            getTextFormat("VALID_DATE").setEnabled(false);
            this.clearValue("VALID_DATE;ORG_CODE_A;ORDER_CODE_A;ORDER_DESC_A");
            table_a.removeRowAll();
        }
        else {
            getRadioButton("STOCK_QTY_C").setSelected(true);
            this.clearValue("ORG_CODE_B;ORDER_CODE_B;ORDER_DESC_B");
            table_b.removeRowAll();
        }
    }

    /**
     * 变更单选框
     */
    public void onChangeRadioButton() {
        if (getRadioButton("VALID_DATE_C").isSelected()) {
            getTextFormat("VALID_DATE").setEnabled(true);
        }
        else {
            getTextFormat("VALID_DATE").setEnabled(false);
            this.clearValue("VALID_DATE");
        }
    }

    /**
     * 当TextField创建编辑控件时长期
     *
     * @param com
     */
    public void onCreateEditComoponentUD_A(KeyEvent obj) {
        TTextField textFilter = getTextField("ORDER_CODE_A");
        textFilter.onInit();
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // 设置弹出菜单
        textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // 定义接受返回值方法
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn_A");
    }

    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn_A(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE_A").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC_A").setValue(order_desc);
    }

    /**
     * 当TextField创建编辑控件时长期
     *
     * @param com
     */
    public void onCreateEditComoponentUD_B(KeyEvent obj) {
        TTextField textFilter = getTextField("ORDER_CODE_B");
        textFilter.onInit();
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // 设置弹出菜单
        textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // 定义接受返回值方法
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn_B");
    }

    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn_B(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE_B").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC_B").setValue(order_desc);
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

    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }


    /**
     * 得到TPanel对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TPanel getPanel(String tagName) {
        return (TPanel) getComponent(tagName);
    }

    /**
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * 根据指定的月份和天数加减计算需要的月份和天数
     * @param Month String 制定月份 格式:yyyyMM
     * @param Day String 制定月份 格式:dd
     * @param num String 加减的数量 以月为单位
     * @return String
     */
    public String rollMonth(String Month, String Day,int num){
        if(Month.trim().length()<=0){
            return "";
        }
        Timestamp time = StringTool.getTimestamp(Month,"yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time.getTime()));
        // 当前月＋num
        cal.add(cal.MONTH, num);
        // 将下个月1号作为日期初始值
        cal.set(cal.DATE, 1);
        Timestamp month = new Timestamp(cal.getTimeInMillis());
        String result = StringTool.getString(month, "yyyyMM");
        String lastDayOfMonth = getLastDayOfMonth(result);
        if (TypeTool.getInt(Day) > TypeTool.getInt(lastDayOfMonth)) {
            result += lastDayOfMonth;
        }
        else {
            result += Day;
        }
        return result;
    }

    /**
     * 获取指定月份的最后一天的日期
     * @param date String 格式 YYYYMM
     * @return Timestamp
     */
    public String getLastDayOfMonth(String date) {
        if (date.trim().length() <= 0) {
            return "";
        }
        Timestamp time = StringTool.getTimestamp(date, "yyyyMM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(time.getTime()));
        // 当前月＋1，即下个月
        cal.add(cal.MONTH, 1);
        // 将下个月1号作为日期初始值
        cal.set(cal.DATE, 1);
        // 下个月1号减去一天，即得到当前月最后一天
        cal.add(cal.DATE, -1);
        Timestamp result = new Timestamp(cal.getTimeInMillis());
        return StringTool.getString(result, "dd");
    }

}
