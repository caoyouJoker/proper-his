package com.javahis.ui.bms;

import com.dongyang.ui.TRadioButton;
import jdo.bms.BMSApplyMTool;
import com.dongyang.ui.TTable;
import java.util.Date;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.control.TControl;

/**
 * <p>
 * Title: 备血申请单查询
 * </p>
 *
 * <p>
 * Description: 备血申请单查询(病患检查记录调用)
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.24
 * @version 1.0
 */
public class BMSPatApplyNoControl
    extends TControl {
    public BMSPatApplyNoControl() {
    }

    private TTable table;

    /**
     * 初始化方法
     */
    public void onInit() {
        initPage();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        // 门急住别
        String adm_type = "O";
        if (this.getRadioButton("ADM_TYPE_E").isSelected()) {
            adm_type = "E";
        }
        else if (this.getRadioButton("ADM_TYPE_I").isSelected()) {
            adm_type = "I";
        }
        parm.setData("ADM_TYPE", adm_type);
        // 病案号
        if (!"".equals(this.getValueString("MR_NO"))) {
            parm.setData("MR_NO", getValueString("MR_NO"));
        }
        // 住院号
        if (!"".equals(this.getValueString("IPD_NO"))) {
            parm.setData("IPD_NO", getValueString("IPD_NO"));
        }
        // 备血单号
        if (!"".equals(this.getValueString("APPLY_NO"))) {
            parm.setData("APPLY_NO", getValueString("APPLY_NO"));
        }
        // 备血日期
        parm.setData("START_DATE", getValue("START_DATE"));
        parm.setData("END_DATE", getValue("END_DATE"));
        TParm result = BMSApplyMTool.getInstance().onApplyNoQuery(parm);
        if (result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        table.setParmValue(result);
    }

    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "MR_NO;IPD_NO;PAT_NAME;APPLY_NO";
        this.clearValue(clearStr);
        table.removeRowAll();
        this.getRadioButton("ADM_TYPE_O").setSelected(true);
        Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
    }

    /**
     * 表格双击事件
     */
    public void onTableDoubleClicked() {
        TParm parm = table.getParmValue().getRow(table.getSelectedRow());
        this.setReturnValue(parm);
        this.closeWindow();
    }


    /**
     * 初始画面数据
     */
    private void initPage() {
        table = getTable("TABLE");
        Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
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
