package com.javahis.ui.sta;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TMenuItem;
import com.dongyang.data.TParm;
import jdo.sta.STADeptInfoTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTextFormat;

/**
 * <p>Title: 对照科室信息</p>
 *
 * <p>Description: 对照科室信息</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-7-26
 * @version 1.0
 */
public class STADeptInfoControl
    extends TControl {
    TParm data;
    int selectRow = -1;
    public void onInit(){
        super.onInit();
        ((TTable) getComponent("Table")).addEventListener("Table->"
                + TTableEvent.CLICKED, this, "onTableClicked");
        onClear();
    }
    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("STA_DEPT_CODE;BED_NUM;BED_ACTIVE_NUM;DR_NUM;PROF_DR_NUM;ATTEND_DR_NUM;VS_DR_NUM;INDUCATION_DR_NUM;NS_NUM;VS_NURSE_NUM");
        ((TTable) getComponent("Table")).removeRowAll();
        selectRow = -1;
        TTextFormat STA_DEPT_CODE = ((TTextFormat) getComponent("STA_DEPT_CODE"));
        STA_DEPT_CODE.setEnabled(true);
        // 设置删除按钮状态
        ((TMenuItem) getComponent("delete")).setEnabled(false);
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            STA_DEPT_CODE.setPopupMenuSQL(STA_DEPT_CODE.getPopupMenuSQL() +
                                          " AND REGION_CODE='" +
                                          Operator.getRegion() + "' ORDER BY DEPT_CODE ");
        }
        STA_DEPT_CODE.onQuery();
        onQuery();
    }
    /**
     * 增加对Table的监听
     * @param row
     * int
     */
    public void onTableClicked(int row) {
        // 选中行
        if (row < 0)
            return;
        setValueForParm(
                "STA_DEPT_CODE;BED_NUM;BED_ACTIVE_NUM;DR_NUM;PROF_DR_NUM;ATTEND_DR_NUM;VS_DR_NUM;INDUCATION_DR_NUM;NS_NUM;VS_NURSE_NUM",
                data, row);
        selectRow = row;
        // 不可编辑
        ((TTextFormat) getComponent("STA_DEPT_CODE")).setEnabled(false);
        // 设置删除按钮状态
        ((TMenuItem) getComponent("delete")).setEnabled(true);
    }
    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = new TParm();
        if(!"".equals(this.getValueString("STA_DEPT_CODE")))
            parm.setData("STA_DEPT_CODE",this.getValue("STA_DEPT_CODE"));
        //========添加区域sssssss
        if(null!=Operator.getRegion() &&Operator.getRegion().length()>0){
            parm.setData("REGION_CODE",Operator.getRegion());
        }
        data = STADeptInfoTool.getInstance().selectdata(parm);
        //判断错误值
        if (data.getErrCode() < 0) {
            messageBox_(data.getErrText());
            return;
        }
        if(data.getCount("STA_DEPT_CODE")<=0){
            ((TTable) getComponent("Table")).removeRowAll();
            return;
        }
        ((TTable) getComponent("Table")).setParmValue(data);
        selectRow = -1;
    }
    /**
     * 新增
     */
    public void onInsert() {
        if (!emptyTextCheck("STA_DEPT_CODE")) {
            this.messageBox_("请选择科室");
            return;
        }
        TParm parm = getParmForTag("STA_DEPT_CODE;BED_NUM;BED_ACTIVE_NUM;DR_NUM;PROF_DR_NUM;ATTEND_DR_NUM;VS_DR_NUM;INDUCATION_DR_NUM;NS_NUM;VS_NURSE_NUM");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("REGION_CODE", Operator.getRegion());
        TParm result = STADeptInfoTool.getInstance().insertdata(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox_(result.getErrText());
            return;
        }
        // 显示新增数据
        int row =((TTable) getComponent("Table"))
                .addRow(
                        parm,
                        "STA_DEPT_CODE;BED_NUM;BED_ACTIVE_NUM;DR_NUM;PROF_DR_NUM;ATTEND_DR_NUM;VS_DR_NUM;INDUCATION_DR_NUM;NS_NUM;VS_NURSE_NUM;OPT_USER;OPT_DATE;OPT_TERM");
        data.setRowData(row, parm);
        clearValue("STA_DEPT_CODE;BED_NUM;BED_ACTIVE_NUM;DR_NUM;PROF_DR_NUM;ATTEND_DR_NUM;VS_DR_NUM;INDUCATION_DR_NUM;NS_NUM;VS_NURSE_NUM");
        this.messageBox("P0002");
    }

    /**
     * 更新
     */
    public void onUpdate() {
        TParm parm = getParmForTag("STA_DEPT_CODE;BED_NUM;BED_ACTIVE_NUM;DR_NUM;PROF_DR_NUM;ATTEND_DR_NUM;VS_DR_NUM;INDUCATION_DR_NUM;NS_NUM;VS_NURSE_NUM");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = STADeptInfoTool.getInstance().updatedata(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox_(result.getErrText());
            return;
        }
        // 选中行
        int row = ((TTable) getComponent("Table")).getSelectedRow();
        if (row < 0)
            return;
        // 刷新，设置末行某列的值
        data.setRowData(row, parm);
        ((TTable) getComponent("Table")).setRowParmValue(row, data);
        this.messageBox("P0005");
    }
    /**
     * 保存
     */
    public void onSave() {
        if(!checkData())
            return;
        if (selectRow == -1) {
            onInsert();
            return;
        }
        onUpdate();
    }
    /**
     * 审核数据
     * @return boolean
     */
    private boolean checkData(){
        if("".equals(this.getValueString("STA_DEPT_CODE"))){
            this.messageBox_("请选择科室");
            this.grabFocus("STA_DEPT_CODE");
            return false;
        }
        return true;
    }
    /**
     * 删除
     */
    public void onDelete() {
        if (selectRow == -1){
            this.messageBox_("请选中要删除的行！");
            return;
        }
        if (this.messageBox("提示", "是否删除", 2) == 0) {
            String dept_code = getValueString("STA_DEPT_CODE");
            TParm result = STADeptInfoTool.getInstance().deletedata(dept_code);
            if (result.getErrCode() < 0) {
                messageBox_(result.getErrText());
                return;
            }
            TTable table = ((TTable) getComponent("Table"));
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            // 删除单行显示
            table.removeRow(row);
            this.messageBox("P0003");
            onClear();
        } else {
            return;
        }
    }
}
