package com.javahis.ui.phl;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TMenuItem;
import java.util.Date;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import java.sql.Timestamp;
import jdo.phl.PhlBedTool;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>
 * Title: 静点室床位
 * </p>
 *
 * <p>
 * Description: 静点室床位
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class PHLBedControl
    extends TControl {

    private String action = "insert";

    private TTable table;


    public PHLBedControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        initPage();
    }

    /**
     * 保存方法
     */
    public void onSave() {
        if (!CheckData()) {
            return;
        }
        TParm parm = new TParm();
        parm.setData("REGION_CODE", getValueString("REGION_CODE"));
        //====pangben modify 20110623 start
        if (null != getValueString("REGION_CODE_ALL") &&
            getValueString("REGION_CODE_ALL").length() > 0)
            parm.setData("REGION_CODE_ALL", getValueString("REGION_CODE_ALL"));
        //====pangben modify 20110623 stop
        parm.setData("BED_NO", getValueString("BED_NO"));
        parm.setData("BED_DESC", getValueString("BED_DESC"));
        parm.setData("BED_STATUS",
                     "Y".equals(getValueString("BED_STATUS")) ? "1" : "0");
        parm.setData("TYPE", getValueString("TYPE"));
        parm.setData("OPT_USER", Operator.getID());
        Timestamp date = StringTool.getTimestamp(new Date());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = new TParm();
        if ("insert".equals(action)) {
            //=======pangben modify 20110622 start
            TParm parmQuery=new TParm();
            parmQuery.setData("BED_NO", getValueString("BED_NO"));
            parmQuery.setData("REGION_CODE", getValueString("REGION_CODE"));
            //=======pangben modify 20110622 stop
            result = PhlBedTool.getInstance().onQuery(parmQuery);
            if (result.getCount() > 0) {
                this.messageBox("病床已存在");
                return;
            }
            result = PhlBedTool.getInstance().onInsert(parm);
        }
        else {
            result = PhlBedTool.getInstance().onUpdate(parm);
        }
        if (result.getErrCode() < 0) {
            this.messageBox("保存失败");
            return;
        }
        this.messageBox("保存成功");
        getTable("TABLE").setSelectionMode(0);
        this.onQuery();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        String region_code = getValueString("REGION_CODE");
        if (!"".equals(region_code)) {
            parm.setData("REGION_CODE", region_code);
        }
        //=====pangben modify 20110622 start
        String region_code_all = getValueString("REGION_CODE_ALL");
        if (!"".equals(region_code_all)) {
            parm.setData("REGION_CODE_ALL", region_code_all);
        }
        //=====pangben modify 20110622 stop
        String bed_no = getValueString("BED_NO");
        if (!"".equals(bed_no)) {
            parm.setData("BED_NO", bed_no);
        }
        TParm result = PhlBedTool.getInstance().onQuery(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        table.setParmValue(result);
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        if (this.messageBox("提示", "是否删除", 2) == 0) {
            int row = table.getSelectedRow();
            if (row == -1) {
                return;
            }
            TParm parm = new TParm();
            parm.setData("REGION_CODE", getValueString("REGION_CODE"));
            parm.setData("REGION_CODE_ALL", getValueString("REGION_CODE_ALL"));//=======pangben modify 20110622
            parm.setData("BED_NO", getValueString("BED_NO"));
            TParm result = PhlBedTool.getInstance().onDelete(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("删除失败");
                return;
            }
            table.removeRow(row);
            table.setSelectionMode(0);
            this.messageBox("删除成功");
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
        }
        action = "insert";
    }

    /**
     * 清空方法
     */
    public void onClear() {
        // 清空VALUE
        String clear =
            "REGION_CODE;BED_NO;BED_DESC;BED_STATUS;TYPE";
        this.clearValue(clear);
        getTable("TABLE").setSelectionMode(0);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        action = "insert";
        this.setValue("REGION_CODE_ALL",Operator.getRegion());//========pangben modfiy 20110622
    }

    /**
     * 表格(CLNDIAG_TABLE)单击事件
     */
    public void onTableClicked() {
        int row = table.getSelectedRow();
        if (row != -1) {
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            this.setValue("REGION_CODE", table.getItemString(row, "REGION_CODE"));
            this.setValue("REGION_CODE_ALL", table.getItemString(row, "REGION_CODE_ALL"));//pangben modify 20110622
            this.setValue("BED_NO", table.getItemString(row, "BED_NO"));
            this.setValue("BED_DESC", table.getItemString(row, "BED_DESC"));
            this.setValue("BED_STATUS", table.getItemString(row, "BED_STATUS"));
            this.setValue("TYPE", table.getItemString(row, "TYPE"));
            action = "update";
        }
    }


    /**
     * 初始画面数据
     */
    private void initPage() {
        // 初始化
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        table = this.getTable("TABLE");
        //========pangben modify 20110622 start 权限添加
        this.setValue("REGION_CODE_ALL",Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE_ALL");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE_ALL")));
        //===========pangben modify 20110622 stop

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
     * 数据检验
     *
     * @return
     */
    private boolean CheckData() {
        if ("".equals(getValueString("REGION_CODE_ALL"))) {
            this.messageBox("区域代码不能为空");
            return false;
        }
        if ("".equals(getValueString("REGION_CODE"))) {
            this.messageBox("静点区域代码不能为空");
            return false;
        }
        if ("".equals(getValueString("BED_NO"))) {
            this.messageBox("病床代码不能为空");
            return false;
        }
        if ("".equals(getValueString("BED_DESC"))) {
            this.messageBox("病床名称不能为空");
            return false;
        }
        if ("".equals(getValueString("TYPE"))) {
            this.messageBox("床类类型不能为空");
            return false;
        }
        return true;
    }
}
