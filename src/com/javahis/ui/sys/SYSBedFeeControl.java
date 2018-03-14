package com.javahis.ui.sys;

import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SYSSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.SYSBedFeeObserver;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:床位费用设定
 * </p>
 *
 * <p>
 * Description:床位费用设定
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.6.04
 * @version 1.0
 */
public class SYSBedFeeControl
    extends TControl {

    private String action = "save";

    // 主项表格
    private TTable table;

    public SYSBedFeeControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        TParm parm = new TParm();
       parm.setData("CAT1_TYPE", "OTH");
       // 设置弹出菜单
       getTextField("ORDER_CODE").setPopupMenuParameter("UD",
           getConfigParm().newConfig(
               "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
       // 定义接受返回值方法
       getTextField("ORDER_CODE").addEventListener(
           TPopupMenuEvent.RETURN_VALUE, this, "popReturn");


        // 初始画面数据
        initPage();
    }

    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");

        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
        double own_price = parm.getDouble("OWN_PRICE");
        this.setValue("OWN_PRICE", own_price);
    }


    /**
     * 保存方法
     */
    public void onSave() {
        int row = 0;
        Timestamp date = StringTool.getTimestamp(new Date());
        if ("save".equals(action)) {
            TComboBox combo = getComboBox("BED_CLASS_CODE");
            boolean flg = combo.isEnabled();
            if (flg) {
                if (!CheckData())
                    return;
                row = table.addRow();
            }
            else {
                row = table.getSelectedRow();
            }
            table.setItem(row, "BED_CLASS_CODE",
                          getValueString("BED_CLASS_CODE"));
            table.setItem(row, "CODE_CALC_KIND",
                          getValueString("CODE_CALC_KIND"));
            table.setItem(row, "PY1", getValueString("PY1"));
            table.setItem(row, "PY2", getValueString("PY2"));
            table.setItem(row, "SEQ", getValueInt("SEQ"));
            table.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));
            Timestamp time = TypeTool.getTimestamp(getValue("CHK_OUT_TIME"));
            table.setItem(row, "CHK_OUT_TIME", StringTool.getString(time,
                "HH:mm:ss"));
            //============pangben modify 20110607 start 注释
//            time = TypeTool.getTimestamp(getValue("START_DATE"));
//            table.setItem(row, "START_DATE", StringTool.getString(time,
//                "yyyy/MM/dd"));
//            time = TypeTool.getTimestamp(getValue("END_DATE"));
//            String end_date = StringTool.getString(time, "yyyy/MM/dd");
//            table.setItem(row, "END_DATE", end_date);
            //============pangben modify 20110607 stop
            table.setItem(row, "OPT_USER", Operator.getID());
            table.setItem(row, "OPT_DATE", date);
            table.setItem(row, "OPT_TERM", Operator.getIP());
            boolean bedOccuFlg = this.getValueBoolean("BED_OCCU_FLG");
            if(bedOccuFlg) {
            	table.setItem(row, "BED_OCCU_FLG", 'Y');
            }else {
            	table.setItem(row, "BED_OCCU_FLG", 'N');
            }
            table.getDataStore().setItem(row, "ORDER_CODE",
                                         getValueString("ORDER_CODE"));
        }
        TDataStore dataStore = table.getDataStore();
        if (dataStore.isModified()) {
            table.acceptText();
            if (!table.update()) {
                messageBox("E0001");
                table.removeRow(row);
                table.setDSValue();
                onClear();
                return;
            }
            table.setDSValue();
        }
        messageBox("P0001");
        table.setDSValue();
        onClear();
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        int row = table.getTable().getSelectedRow();
        if (row < 0)
            return;
        table.removeRow(row);
        table.setSelectionMode(0);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "delete";
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        table = getTable("TABLE");
        table.removeRowAll();
        TDS tds = new TDS();
        tds.setSQL(SYSSQL.getSYSBedFee());
        tds.retrieve();
        // 观察者
        SYSBedFeeObserver obser = new SYSBedFeeObserver();
        tds.addObserver(obser);
        table.setDataStore(tds);
        table.setDSValue();

        String bed_class = getValueString("BED_CLASS_CODE");
        String order = getValueString("ORDER_CODE");
        String filterString = "";
        boolean bedOccuFlg = this.getValueBoolean("BED_OCCU_FLG");
        if (bed_class.length() > 0 && order.length() > 0)
            filterString += "BED_CLASS_CODE = '" + bed_class
                + "' AND ORDER_CODE = '" + order + "'";
        else if (bed_class.length() > 0)
            filterString += "BED_CLASS_CODE = '" + bed_class + "'";
        else if (order.length() > 0)
            filterString += "ORDER_CODE = '" + order + "'";
        if(bedOccuFlg) {
        	filterString += "BED_OCCU_FLG='Y'";
        }
        else {
        	filterString += "BED_OCCU_FLG='N'";
        }
        table.setFilter(filterString);
        table.filter();
    }

    /**
     * 清空方法
     */
    public void onClear() {
        // 清空画面内容
        String clearString =
            "BED_CLASS_CODE;ORDER_CODE;CODE_CALC_KIND;PY1;PY2;"
            + "SEQ;DESCRIPTION;ORDER_DESC;OWN_PRICE";
        clearValue(clearString);
        // 序号
        TDataStore dataStroe = table.getDataStore();
        int seq = getMaxSeq(dataStroe, "SEQ",
                            dataStroe.isFilter() ? dataStroe.FILTER :
                            dataStroe.PRIMARY);
        setValue("SEQ", seq);
        table.setSelectionMode(0);
        getComboBox("BED_CLASS_CODE").setEnabled(true);
        getTextField("ORDER_CODE").setEnabled(true);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        getCheckBox("BED_OCCU_FLG").setSelected(false);
        action = "save";
    }

    /**
     * TABLE单击事件
     */
    public void onTableClicked() {
        int row = table.getSelectedRow();
        if (row != -1) {
            TParm parm = table.getDataStore().getRowParm(row);
            String likeNames =
                "BED_CLASS_CODE;ORDER_CODE;CODE_CALC_KIND;PY1;PY2;SEQ;"
                + "DESCRIPTION;CHK_OUT_TIME";
            this.setValueForParm(likeNames, parm);
            String order_code = parm.getValue("ORDER_CODE");
            TParm desc = new TParm(TJDODBTool.getInstance().select(
                SYSSQL.getSYSFee(order_code)));
            this.setValue("ORDER_DESC", desc.getValue("ORDER_DESC", 0));
            getComboBox("BED_CLASS_CODE").setEnabled(false);
            getTextField("ORDER_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);

            TParm parmOwnPrice = new TParm(TJDODBTool.getInstance().select(
                SYSSQL.getSYSFee(this.getValueString("ORDER_CODE"))));
            this.setValue("OWN_PRICE", parmOwnPrice.getDouble("OWN_PRICE", 0));
            boolean BED_OCCU_FLG =  parm.getBoolean("BED_OCCU_FLG");
            if(BED_OCCU_FLG) {
            	getCheckBox("BED_OCCU_FLG").setSelected(true);
            }
            else {
            	getCheckBox("BED_OCCU_FLG").setSelected(false);
            }
            action = "save";
        }
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 初始化Table
        table = getTable("TABLE");
        table.removeRowAll();
        TDS tds = new TDS();
        tds.setSQL(SYSSQL.getSYSBedFee());
        tds.retrieve();

        // 观察者
        SYSBedFeeObserver obser = new SYSBedFeeObserver();
        tds.addObserver(obser);
        table.setDataStore(tds);
        table.setDSValue();

        // 最大号+1(SEQ)
        int seq = getMaxSeq(tds, "SEQ", tds.isFilter() ? tds.FILTER
                            : tds.PRIMARY);
        setValue("SEQ", seq);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
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
     * 得到ComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }
    
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * 检查数据
     */
    private boolean CheckData() {
        if ("".equals(getValueString("BED_CLASS_CODE"))) {
            this.messageBox("床位登记不能为空");
            return false;
        }
        if ("".equals(getValueString("ORDER_CODE"))) {
            this.messageBox("收费代码不能为空");
            return false;
        }
        if ("".equals(getValueString("CODE_CALC_KIND"))) {
            this.messageBox("进出方式不能为空");
            return false;
        }
        //===========pangben modify 20110607 start 注释
//        Timestamp begin = (Timestamp) getValue("START_DATE");
//        if (begin == null) {
//            messageBox("开始日期不能为空");
//            return false;
//        }
//        Timestamp end = (Timestamp) getValue("END_DATE");
//        if (end == null) {
//            messageBox("截止日期不能为空");
//            return false;
//        }
//        if (end.compareTo(begin) <= 0) {
//            this.messageBox("开始日期不能大于截止日期");
//            return false;
//        }
         //===========pangben modify 20110607 stop
        return true;
    }

    /**
     * 得到最大的编号 +1
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */
    public int getMaxSeq(TDataStore dataStore, String columnName,
                         String dbBuffer) {
        if (dataStore == null)
            return 0;
        // 保存数据量
        int count = dataStore.getBuffer(dbBuffer).getCount();
        // 保存最大号
        int max = 0;
        for (int i = 0; i < count; i++) {
            int value = TCM_Transform.getInt(dataStore.getItemData(i,
                columnName, dbBuffer));
            // 保存最大值
            if (max < value) {
                max = value;
                continue;
            }
        }
        // 最大号加1
        max++;
        return max;
    }

}
