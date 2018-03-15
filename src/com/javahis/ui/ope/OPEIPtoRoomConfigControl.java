package com.javahis.ui.ope;

import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p> Title: 手术室IP对照设置 </p>
 * 
 * <p> Description: 手术室IP对照设置 </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author wanglong 2014-7-1
 * @version 1.0
 */
public class OPEIPtoRoomConfigControl extends TControl {

    private TTable table;
    private int selectRow = -1;// 选中行
    private TParm parmValue;// table数据

    /**
     * 初始化
     */
    public void onInit() {
        table = (TTable) getComponent("TABLE");
        callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        onClear();
    
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
        this.setValueForParm("IP;ROOM_NO", parmValue, row);
        selectRow = row;
    }

    /**
     * 查询操作
     */
    public void onQuery() {
        String sql = "SELECT * FROM OPE_IPROOM WHERE 1=1 ";
        if (!Operator.getRole().equals("ADMIN")) {
            sql += " AND IP = '" + this.getValueString("IP").trim() + "' ";
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        this.callFunction("UI|TABLE|setParmValue",result);
        parmValue = table.getParmValue();
        selectRow = -1;
        this.clearValue("ROOM_NO");
        this.setValue("IP", Operator.getIP());
    }

    /**
     * 保存
     */
    public void onSave() {
        if (this.getValueString("IP").trim().equals("")) {
            this.messageBox("请填写终端机IP");
            return;
        }
        if (this.getValueString("ROOM_NO").trim().equals("")) {
            this.messageBox("请选择手术室");
            return;
        }
        selectRow = table.getSelectedRow();
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
                "INSERT INTO OPE_IPROOM (IP, ROOM_NO, OPT_USER, OPT_DATE, OPT_TERM ) "
                        + " VALUES ('#', '#', '@', SYSDATE, '@')  ";
        sql = sql.replaceFirst("#", this.getValueString("IP").trim());
        sql = sql.replaceFirst("#", this.getValueString("ROOM_NO").trim());
        sql = sql.replaceFirst("@", Operator.getID());
        sql = sql.replaceFirst("@", Operator.getIP());
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
                "UPDATE OPE_IPROOM SET IP = '#', "
                        + "            ROOM_NO = '#', "
                        + "            OPT_USER = '@',     "
                        + "            OPT_DATE = SYSDATE, "
                        + "            OPT_TERM = '@'      "
                        + " WHERE IP = '&'             ";
        sql = sql.replaceFirst("#", this.getValueString("IP").trim());
        sql = sql.replaceFirst("#", this.getValueString("ROOM_NO").trim());
        sql = sql.replaceFirst("@", Operator.getID());
        sql = sql.replaceFirst("@", Operator.getIP());
        sql = sql.replaceFirst("&", parmValue.getValue("IP", selectRow));
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
        String printerTerm = parmValue.getValue("IP", selectRow);
        if (this.messageBox("提示", "P0010", 2) == 0) {// 确认删除
            String sql = "DELETE FROM OPE_IPROOM WHERE IP = '#' ";
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
        onQuery();
    }
    

}
