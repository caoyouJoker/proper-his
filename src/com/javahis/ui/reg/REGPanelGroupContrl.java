package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.dongyang.manager.TCM_Transform;
import jdo.reg.PanelGroupTool;
/**
 *
 * <p>Title:给号组别控制类 </p>
 *
 * <p>Description:给号组别控制类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.17
 * @version 1.0
 */
public class REGPanelGroupContrl extends TControl{
    int selectRow = -1;
    public void onInit() {
        super.onInit();
        callFunction("UI|TABLEGROUP|addEventListener",
                     "TABLEGROUP->" + TTableEvent.CLICKED, this, "onTABLEGROUPClicked");
        onQuery();
    }
    /**
     *增加对TABLEGROUP的监听
     * @param row int
     */
    public void onTABLEGROUPClicked(int row) {

        if (row < 0)
            return;
        TParm data=(TParm)callFunction("UI|TABLEGROUP|getParmValue");
        setValueForParm("QUEGROUP_CODE;QUEGROUP_DESC;PY1;PY2;SEQ;DESCRIPTION;MAX_QUE;VIP_FLG",
                        data, row);
        selectRow = row;
    }
    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = getParmForTag("QUEGROUP_CODE", true);
        TParm data = PanelGroupTool.getInstance().selectdata(parm);
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        this.callFunction("UI|TABLEGROUP|setParmValue", data);
    }

    /**
     * 新增
     */
    public void onInsert() {
        if (!this.emptyTextCheck("QUEGROUP_CODE"))
            return;
        TParm parm = getParmForTag("QUEGROUP_CODE;QUEGROUP_DESC;PY1;PY2;SEQ;DESCRIPTION;MAX_QUE;VIP_FLG");

        parm.setData("OPT_USER", Operator.getID());
//        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = PanelGroupTool.getInstance().insertdata(parm);

        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //2008.09.05 --------start--------table上加入新增的数据显示
        callFunction("UI|TABLEGROUP|addRow", parm,
                     "QUEGROUP_CODE;QUEGROUP_DESC;PY1;PY2;SEQ;DESCRIPTION;MAX_QUE;VIP_FLG;OPT_USER;OPT_DATE;OPT_TERM");

        //弹出新增成功提示框
        this.messageBox("P0002");

    }

    /**
     * 更新
     */
    public void onUpdate() {
        TParm parm = getParmForTag("QUEGROUP_CODE;QUEGROUP_DESC;PY1;PY2;SEQ;DESCRIPTION;MAX_QUE;VIP_FLG");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = PanelGroupTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }

        //刷新，设置末行某列的值
        int row = (Integer) callFunction("UI|TABLEGROUP|getSelectedRow");
        if (row < 0)
            return;
        TParm data=(TParm)callFunction("UI|TABLETYPE|getParmValue");
        data.setRowData(row, parm);
        callFunction("UI|TABLEGROUP|setRowParmValue", row, data);
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
            String quegroupCode = getValue("QUEGROUP_CODE").toString();
            TParm result = PanelGroupTool.getInstance().deletedata(quegroupCode);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //删除单行显示
            int row = (Integer) callFunction("UI|TABLEGROUP|getSelectedRow");
            if (row < 0)
                return;
            this.callFunction("UI|TABLEGROUP|removeRow", row);
            this.callFunction("UI|TABLEGROUP|setSelectRow", row);

            this.messageBox("P0003");
        }
        else {
            return;
        }
    }

    /**
     * 根据汉字输出拼音首字母
     * @return Object
     */
    public Object onCode() {
        if (TCM_Transform.getString(this.getValue("QUEGROUP_DESC")).length() <
            1) {
            return null;
        }
        SystemTool st = new SystemTool();
        String value = st.charToCode(String.valueOf(this.getValue(
            "QUEGROUP_DESC")));
        if (null == value || value.length() < 1) {
            return null;
        }
        this.setValue("PY1", value);
        //光标下移
        this.callFunction("UI|afterFocus","QUEGROUP_DESC");

        return null;
    }
}
