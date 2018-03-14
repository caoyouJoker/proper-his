package com.javahis.ui.ctr;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.ui.TMenuItem;
import jdo.ctr.CTREFFECTCLASSTool;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CTREFFECTCLASSControl
    extends TControl {
    private TTable table;
    private CTREFFECTCLASSTool jdo = null;
    public void onInit() {
        //JDO类一般用于操作数据库,这个类封装了对数据库操作的大多数方法
        super.init();
        table = getTable("TABLE");
        callFunction("UI|table|addEventListener",
                     table + "->" + TTableEvent.CLICKED, this, "onTableClicked");
       onQuery();

    }
    /**
     * 保存数据<br>
     *
     */
    public void onSave() {
        if (!checkData()) {
            return;
        }
        int row = table.getRowCount();
        TParm parm = new TParm();
        String code = this.getValueString("EFFECTCLAS_CODE");
        String type = this.getValueString("CTRL_TYPE");
        parm.setData("EFFECTCLAS_CODE", code);
        parm.setData("CHN_DESC", this.getValueString("CHN_DESC"));
        parm.setData("ENG_DESC", this.getValueString("ENG_DESC"));
        parm.setData("PY1", this.getValueString("PY1"));
        parm.setData("PY2", this.getValueString("PY2"));
        parm.setData("CTRL_TYPE", type);
        parm.setData("MESSAGE_TEXT", this.getValueString("MESSAGE_TEXT"));
        parm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        //System.out.println("parm----"+parm);
        TParm result = new TParm();
        TParm checkresult = CTREFFECTCLASSTool.getNewInstance().onQuery(
            parm);
        if (checkresult.getCount() <= 0) {
            // 新增数据
            parm.setData("SEQ", row);
            result = CTREFFECTCLASSTool.getNewInstance().onInsert(parm);
        }
        else {
            // 更新数据
            result = CTREFFECTCLASSTool.getNewInstance().onUpdate(parm);
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
     * 删除数据<br>
     *
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("请选择删除数据");
            return;
        }
        TParm parm = table.getParmValue().getRow(row);
        TParm result = CTREFFECTCLASSTool.getNewInstance().onDelete(parm);
        if (result.getErrCode() < 0) {
            this.messageBox("删除失败");
        }
        else {
            this.messageBox("删除成功");
            table.removeRow(row);
        }
        this.clearValue("EFFECTCLAS_CODE;CHN_DESC; ENG_DESC;PY1;PY2;SEQ;CTRL_TYPE;MESSAGE_TEXT;DESCRIPTION;");
        ( (TMenuItem) getComponent("new")).setEnabled(true);
        ( (TTextField) getComponent("EFFECTCLAS_CODE")).setEnabled(true);
    }

    /**
     * 查询数据<br>
     *
     */

    public void onQuery() {
        TParm parm = new TParm();
        String code = this.getValueString("EFFECTCLAS_CODE");
        if (!"".equals(code)) {
            parm.setData("EFFECTCLAS_CODE", code);
        }
        TParm result = CTREFFECTCLASSTool.getNewInstance().onQuery(parm);

        // 判断错误值
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;

        }
//        int i = result.getCount();
//        int a = 0;
//        while (i > 0) {
//            int row = table.addRow();
//            table.setItem(row, "EFFECTCLAS_CODE",
//                          result.getValue("EFFECTCLAS_CODE", a).trim());
//            table.setItem(row, "CHN_DESC", result.getValue("CHN_DESC", a).trim());
//            table.setItem(row, "ENG_DESC", result.getValue("ENG_DESC", a).trim());
//            table.setItem(row, "PY1", result.getValue("PY1", a).trim());
//            table.setItem(row, "PY2", result.getValue("PY2", a).trim());
//            table.setItem(row, "SEQ", result.getValue("SEQ", a).trim());
//            table.setItem(row, "CTRL_TYPE",
//                          result.getValue("CTRL_TYPE", a).trim());
//            table.setItem(row, "MESSAGE_TEXT",
//                          result.getValue("MESSAGE_TEXT", a).trim());
//            table.setItem(row, "DESCRIPTION",
//                          result.getValue("DESCRIPTION", a).trim());
//            i--;
//            a++;
//
//        }
        table.setParmValue(result);
        ( (TMenuItem) getComponent("new")).setEnabled(true);
    }

    /**
     * 清空数据<br>
     *
     */
    public void onClear() {
        if (table.getRowCount() > 0) {
            table.removeRowAll();
        }
        this.clearValue("EFFECTCLAS_CODE;CHN_DESC; ENG_DESC;PY1;PY2;CTRL_TYPE;MESSAGE_TEXT;DESCRIPTION;");
        table.removeRowAll();
        ( (TMenuItem) getComponent("new")).setEnabled(true);
        ( (TTextField) getComponent("EFFECTCLAS_CODE")).setEnabled(true);
    }

    /**
     * 单击表数据时间<br>
     *
     */
    public void onTableClicked() {
        int row = table.getClickedRow();
        TParm data = (TParm) callFunction("UI|Table|getParmValue");
        setValueForParm(
            "EFFECTCLAS_CODE;CHN_DESC;PY1;PY2;ENG_DESC;CTRL_TYPE;MESSAGE_TEXT;DESCRIPTION",
            data, row);
        ( (TMenuItem) getComponent("new")).setEnabled(false);
        ( (TTextField) getTextField("EFFECTCLAS_CODE")).setEnabled(false);

    }

    /**
     * 增添数据数据时间<br>
     *
     */
    public void onAdd() {
//        if (!checkData()) {
//            return;
//        }
        int row = table.addRow();
        table.setItem(row, "EFFECTCLAS_CODE",
                      this.getTextField("EFFECTCLAS_CODE").getValue());
        table.setItem(row, "CHN_DESC", "");
        table.setItem(row, "ENG_DESC", "");
        table.setItem(row, "PY1", "");
        table.setItem(row, "PY2", "");
        table.setItem(row, "SEQ", "");
        table.setItem(row, "MESSAGE_TEXT", "");
        table.setItem(row, "DESCRIPTION", "");
        table.setItem(row, "CTRL_TYPE", "");
        ( (TTextField) getTextField("EFFECTCLAS_CODE")).setEnabled(false);

    }

    //parm @getTable
    //parm tagName
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    // @getTextField
    //parm tagName
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

//    //判断检验数据
    private boolean checkData() {
        String code = this.getValueString("EFFECTCLAS_CODE");
        if (code == null || code.length() == 0) {
            this.messageBox("请先填写内部标识");
            return false;
        }
        String type = this.getValueString("CTRL_TYPE");
        if (type == null || type.length() == 0) {
            this.messageBox("请先填写数据元标识");
            return false;
        }

        return true;
    }

}
