package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import java.util.Date;
import jdo.bms.BMSBldCodeTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.javahis.system.combo.TComboBMSBldsubcat;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import jdo.bms.BMSBloodTool;

/**
 * <p>
 * Title: 血品库存状态查询
 * </p>
 *
 * <p>
 * Description: 血品库存状态查询
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
public class BMSBldStockStatusControl
    extends TControl {

    public BMSBldStockStatusControl() {
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        if (!"".equals(this.getValueString("BLD_CODE"))) {
            parm.setData("BLD_CODE", this.getValue("BLD_CODE"));
        }
        if (!"".equals(this.getValueString("SUBCAT_CODE"))) {
            parm.setData("SUBCAT_CODE", this.getValue("SUBCAT_CODE"));
        }
        if (!"".equals(this.getValueString("BLD_TYPE"))) {
            parm.setData("BLD_TYPE", this.getValue("BLD_TYPE"));
        }
        if (!"".equals(this.getValueString("END_DATE"))) {
            String valid_date = this.getValueString("END_DATE").substring(0, 10).
                replace('-', '/');
            parm.setData("START_DATE",
                         StringTool.getTimestamp(valid_date + " 00:00:00",
                                                 "yyyy/MM/dd HH:mm:ss"));
            parm.setData("END_DATE",
                         StringTool.getTimestamp(valid_date + " 23:59:59",
                                                 "yyyy/MM/dd HH:mm:ss"));
        }
        if (!"".equals(this.getValueString("BLD_STATE"))) {
            parm.setData("BLD_STATE", this.getValue("BLD_STATE"));
        }
        if (!"".equals(this.getValueString("BLOOD_NO"))) {
            parm.setData("BLOOD_NO", this.getValue("BLOOD_NO"));
        }
        getTable("TABLE").removeRowAll();
        TParm result = BMSBloodTool.getInstance().onQueryBloodStockStatus(parm);
        if (result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        getTable("TABLE").setParmValue(result);
    }

    /**
     * 清空方法
     */
    public void onClear() {
        String clearString =
            "BLD_CODE;SUBCAT_CODE;BLD_TYPE;END_DATE;BLD_STATE;BLOOD_NO";
        this.clearValue(clearString);
        getTable("TABLE").removeRowAll();
    }

    /**
     * 变更血品
     */
    public void onChangeBld() {
        String bld_code = getComboBox("BLD_CODE").getSelectedID();
        ( (TComboBMSBldsubcat)this.getComponent("SUBCAT_CODE")).setBldCode(
            bld_code);
        ( (TComboBMSBldsubcat)this.getComponent("SUBCAT_CODE")).onQuery();
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
