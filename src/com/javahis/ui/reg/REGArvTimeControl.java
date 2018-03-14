package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import jdo.reg.ArvTimeTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;

/**
 *
 * <p>Title:到院时间控制类 </p>
 *
 * <p>Description:到院时间控制类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.08.21
 * @version 1.0
 */
public class REGArvTimeControl
    extends TControl {
    TParm data;
    int selectRow = -1;
    public void onInit() {
        super.onInit();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.CLICKED, this, "onTableClicked");
        onQuery();

    }

    /**
     *增加对Table的监听
     * @param row int
     */
    public void onTableClicked(int row) {

        if (row < 0)
            return;
        setValueForParm("QUE_GROUP;START_TIME;INTERAL_TIME",
                        data, row);
        selectRow = row;
        //初始化Combo不可编辑
        callFunction("UI|QUE_GROUP|setEnabled", false);
        callFunction("UI|START_TIME|setEnabled", false);
    }

    /**
     * 查询
     */
    public void onQuery() {
//        TParm parm = getParmForTag("QUE_GROUP;START_TIME", true);
//        data = ArvTimeTool.getInstance().selectdata(parm);
//        if (data.getErrCode() < 0) {
//            messageBox(data.getErrText());
//            return;
//        }
//        this.callFunction("UI|Table|setParmValue", data);
//        System.out.println("1111111111111111111111");
        String queGroup = (String)getValue("QUE_GROUP");
        String startTime = (String)getValue("START_TIME");
        String sql = "SELECT * FROM REG_ARVTIME";
        String where = "";
        if(queGroup.length() > 0)
            where = "QUE_GROUP='" + queGroup + "'";
        if(startTime.length() > 0)
        {
            if(where.length() > 0)
                where += " and ";
            where += "START_TIME='" + startTime + "'";
        }
        if(where.length() > 0)
            sql += " WHERE "+where;
        sql += " order by QUE_GROUP,START_TIME";
//        System.out.println("sqlsqlsqlsql"+sql);
        TTable table = (TTable)callFunction("UI|Table|getThis");
        table.setSQL(sql);
        table.retrieve();
    }

    /**
     * 新增
     */
    public void onInsert() {
        //不能输入空值
        if (!this.emptyTextCheck("START_TIME,INTERAL_TIME"))
            return;
        TParm parm = getParmForTag("QUE_GROUP;START_TIME;INTERAL_TIME");

        parm.setData("OPT_USER", Operator.getID());
//        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = ArvTimeTool.getInstance().insertdata(parm);

        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //2008.09.05 --------start--------table上加入新增的数据显示
        callFunction("UI|TABLE|addRow", parm,
                     "QUE_GROUP;START_TIME;INTERAL_TIME;OPT_USER;OPT_DATE;OPT_TERM");
        // 把数据上翻到控件显示
        int row = data.insertRow();
        data.setRowData(row,parm);

        //弹出新增成功提示框
        this.messageBox("P0002");

    }

    /**
     * 更新
     */
    public void onUpdate() {
        TParm parm = getParmForTag("QUE_GROUP;START_TIME;INTERAL_TIME");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = ArvTimeTool.getInstance().updatedata(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }

        //刷新，设置末行某列的值
        int row = (Integer) callFunction("UI|Table|getSelectedRow");
        if (row < 0)
            return;
        data.setRowData(row, parm);
        callFunction("UI|Table|setRowParmValue", row, data);

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
            String queGrop = getValue("QUE_GROUP").toString();
            String startTime = getValue("START_TIME").toString();
            TParm result = ArvTimeTool.getInstance().deletedata(queGrop,
                startTime);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            //删除单行显示
            int row = (Integer) callFunction("UI|Table|getSelectedRow");
            if (row < 0)
                return;
            this.callFunction("UI|Table|removeRow", row);
            this.callFunction("UI|Table|setSelectRow", row);

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
        clearValue("QUE_GROUP;START_TIME;INTERAL_TIME");
        this.callFunction("UI|Table|clearSelection");
        selectRow = -1;
        callFunction("UI|QUE_GROUP|setEnabled", true);
        callFunction("UI|START_TIME|setEnabled", true);

    }

}
