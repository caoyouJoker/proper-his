package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import jdo.sys.Operator;
import jdo.reg.PanelAreaTool;
/**
 *
 * <p>Title:诊区维护控制类 </p>
 *
 * <p>Description:诊区维护控制类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.25
 * @version 1.0
 */
public class REGPanelAreaControl extends TControl{
    int selectRow = -1;
    public void onInit() {
        super.onInit();
        callFunction("UI|TABLEAREA|addEventListener",
                     "TABLEAREA->" + TTableEvent.CLICKED, this, "onTABLEAREAClicked");
        onQuery();
    }
    /**
     *增加对TABLEAREA的监听
     * @param row int
     */
    public void onTABLEAREAClicked(int row) {

        if (row < 0)
            return;
        TParm data=(TParm)callFunction("UI|TABLEAREA|getParmValue");
        setValueForParm("CLINICAREA_CODE;CLINIC_DESC;PY1;PY2;SEQ;DESCRIPTION;REGION_CODE",
                        data, row);
        selectRow = row;
    }
    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = getParmForTag("CLINICAREA_CODE", true);
        TParm data = PanelAreaTool.getInstance().selectdata(parm);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        this.callFunction("UI|TABLEAREA|setParmValue", data);
    }

    /**
     * 新增
     */
    public void onInsert() {
        if (!this.emptyTextCheck("CLINICAREA_CODE"))
            return;
        TParm parm = getParmForTag("CLINICAREA_CODE;CLINIC_DESC;PY1;PY2;SEQ:int;DESCRIPTION;REGION_CODE");

        parm.setData("OPT_USER", Operator.getID());
//        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = PanelAreaTool.getInstance().insertdata(parm);

        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //2008.09.05 --------start--------table上加入新增的数据显示
        callFunction("UI|TABLEAREA|addRow", parm,
                     "CLINICAREA_CODE;CLINIC_DESC;PY1;PY2;SEQ;DESCRIPTION;REGION_CODE;OPT_USER;OPT_DATE;OPT_TERM");

        //弹出新增成功提示框
        this.messageBox("P0002");

    }

    /**
     * 更新
     */
    public void onUpdate() {
        TParm parm = getParmForTag("CLINICAREA_CODE;CLINIC_DESC;PY1;PY2;SEQ:int;DESCRIPTION;REGION_CODE");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = PanelAreaTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }

        //刷新，设置末行某列的值
        int row = (Integer) callFunction("UI|TABLEAREA|getSelectedRow");
        if (row < 0)
            return;
        TParm data=(TParm)callFunction("UI|TABLEAREA|getParmValue");
        data.setRowData(row, parm);
        callFunction("UI|TABLEAREA|setRowParmValue", row, data);

        this.messageBox("P0001");

    }

    /**
     * 保存
     */
    public void onSave() {
        if (selectRow == -1) {
            onInsert();
            return;
        }
        onUpdate();
    }


    /**
     * 删除
     */
    public void onDelete() {
        if (this.messageBox("询问", "是否删除", 2) == 0) {
            if (selectRow == -1)
                return;
            String clinicAreaCode = getValue("CLINICAREA_CODE").toString();
            TParm result = PanelAreaTool.getInstance().deletedata(clinicAreaCode);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //删除单行显示
            int row = (Integer) callFunction("UI|TABLEAREA|getSelectedRow");
            if (row < 0)
                return;
            this.callFunction("UI|TABLEAREA|removeRow", row);
            this.callFunction("UI|TABLEAREA|setSelectRow", row);

            this.messageBox("P0003");
        }
        else {
            return;
        }
    }


    /**
     *清空
     */
    public void onClear() {
        clearValue("CLINICAREA_CODE;CLINIC_DESC;PY1;PY2;SEQ;DESCRIPTION;REGION_CODE");
        this.callFunction("UI|TABLEAREA|clearSelection");
        selectRow = -1;

    }
}
