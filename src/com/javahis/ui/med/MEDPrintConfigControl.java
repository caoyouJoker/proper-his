package com.javahis.ui.med;

import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p> Title: 条码打印机设置 </p>
 * 
 * <p> Description: 条码打印机设置 </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company: BlueCore </p>
 * 
 * @author wanglong
 * @version 1.0
 */
public class MEDPrintConfigControl extends TControl {

    private TTable table;
    private int selectRow = -1;// 选中行
    private TParm parmValue;// table数据

    /**
     * 初始化
     */
    public void onInit() {
        table = (TTable) getComponent("TABLE");
        this.setValue("PRINTER_TERM", Operator.getIP());
        callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        onClear();
        if (Operator.getRole().equalsIgnoreCase("ADMIN")) {
            this.callFunction("UI|ZEBRA_FLG|setEnabled", true);
            this.callFunction("UI|ZEBRA_FLG|setSelected", true);
            onChooseType();
        }
    }

    /**
     * 行点击事件
     * 
     * @param row
     */
    public void onTableClicked(int row) {
        if (row < 0) {
            return;
        }
        this.setValueForParm("PRINTER_TERM;ZEBRA_FLG;PRINTER_PORT;NOFONT_FLG;OFFSET_X;OFFSET_Y", parmValue, row);
        selectRow = row;
    }
    
    /**
     * “控制码方式打印”勾选/取消勾选事件
     */
    public void onChooseType() {
        if ((Boolean) this.callFunction("UI|ZEBRA_FLG|isSelected")) {
            this.callFunction("UI|PRINTER_PORT|setEnabled", true);
        } else {
            this.callFunction("UI|PRINTER_PORT|setEnabled", false);
        }
    }

    /**
     * 查询
     */
    public void onQuery() {
        String sql = "SELECT * FROM MED_PRINTER_LIST WHERE PRINTER_TERM = '#' ";
        sql = sql.replaceFirst("#", this.getValueString("PRINTER_TERM"));
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        table.setParmValue(result);
        parmValue = table.getParmValue();
        selectRow = -1;
    }

    /**
     * 保存
     */
    public void onSave() {
        if (this.getValueString("PRINTER_TERM").trim().equals("")) {
            this.messageBox("请填写终端机IP");
            return;
        }
        if (this.getValueString("PRINTER_PORT").trim().equals("")) {
            this.messageBox("请选择打印端口");
            return;
        }
        if (selectRow == -1) {
            onInsert();
        } else {
            onUpdate();
        }
    }

    /**
     * 新增
     */
    public void onInsert() {
        String sql =
                "INSERT INTO MED_PRINTER_LIST (PRINTER_TERM, ZEBRA_FLG, PRINTER_PORT, OPT_USER, OPT_DATE, OPT_TERM, NOFONT_FLG, OFFSET_X, OFFSET_Y ) "
                        + " VALUES ('#', '#', '#', '@', SYSDATE, '@', '#', #, #)  ";
        sql = sql.replaceFirst("#", this.getValueString("PRINTER_TERM").trim());
        sql = sql.replaceFirst("#", this.getValueString("ZEBRA_FLG").trim());
        sql = sql.replaceFirst("#", this.getValueString("PRINTER_PORT").trim());
        sql = sql.replaceFirst("@", Operator.getID());
        sql = sql.replaceFirst("@", Operator.getIP());
        sql = sql.replaceFirst("#", this.getValueString("NOFONT_FLG").trim());
        sql = sql.replaceFirst("#", this.getValueString("OFFSET_X").trim());
        sql = sql.replaceFirst("#", this.getValueString("OFFSET_Y").trim());
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("保存失败 " + result.getErrText());
            return;
        } else {
            this.messageBox("P0001");// 保存成功
            onClear();
        }
    }

    /**
     * 更新
     */
    public void onUpdate() {
        String sql =
                "UPDATE MED_PRINTER_LIST SET PRINTER_TERM = '&', "
                        + "                  ZEBRA_FLG = '#',    "
                        + "                  PRINTER_PORT = '#', "
                        + "                  OPT_USER = '@',     "
                        + "                  OPT_DATE = SYSDATE, "
                        + "                  OPT_TERM = '@',     "
                        + "                  NOFONT_FLG = '#',   "
                        + "                  OFFSET_X = #,       "
                        + "                  OFFSET_Y = #        "
                        + " WHERE PRINTER_TERM = '&'             ";
        sql = sql.replaceAll("&", this.getValueString("PRINTER_TERM").trim());
        sql = sql.replaceFirst("#", this.getValueString("ZEBRA_FLG").trim());
        sql = sql.replaceFirst("#", this.getValueString("PRINTER_PORT").trim());
        sql = sql.replaceFirst("@", Operator.getID());
        sql = sql.replaceFirst("@", Operator.getIP());
        sql = sql.replaceFirst("#", this.getValueString("NOFONT_FLG").trim());
        sql = sql.replaceFirst("#", this.getValueString("OFFSET_X").trim());
        sql = sql.replaceFirst("#", this.getValueString("OFFSET_Y").trim());
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("修改失败 " + result.getErrText());
            return;
        } else {
            this.messageBox("修改成功");
            onClear();
        }
    }

    /**
     * 删除
     */
    public void onDelete() {
        if (selectRow < 0) {
            this.messageBox("请选择某条记录");
            return;
        }
        String printerTerm = parmValue.getValue("PRINTER_TERM", selectRow);
        if (this.messageBox("提示", "P0010", 2) == 0) {// 确认删除
            String sql = "DELETE FROM MED_PRINTER_LIST WHERE PRINTER_TERM = '#' ";
            sql = sql.replaceFirst("#", printerTerm);
            TParm result = new TParm(TJDODBTool.getInstance().update(sql));
            if (result.getErrCode() < 0) {
                this.messageBox("删除失败 " + result.getErrText());
                return;
            } else {
                this.messageBox("P0003");// 删除成功
                onClear();
            }
        }
    }

    /**
     * 清空
     */
    public void onClear() {
        this.setValue("PRINTER_TERM", Operator.getIP());
        this.clearValue("NOFONT_FLG;OFFSET_X;OFFSET_Y");
        onQuery();
    }
    

}
