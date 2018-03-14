package com.javahis.ui.mro;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTextField;
import com.dongyang.util.TMessage;
import com.dongyang.manager.TCM_Transform;
import jdo.mro.MROSeqTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p>Title: 病历排序维护</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author liuzhen  2012-8-2
 * @version 1.0
 */
public class MROSeqControl
    extends TControl {
    TParm data;
    int selectRow = -1;

    public void onInit() {
        super.onInit();
        ( (TTable) getComponent("Table")).addEventListener("Table->"
            + TTableEvent.CLICKED, this, "onTableClicked");
        onClear();
    }

    /**
     * 增加对Table的监听
     *
     * @param row
     *            int
     */
    public void onTableClicked(int row) {
        // 选中行
        if (row < 0)
            return;
        setValueForParm(
            "SEQ;FILE_TYPE",
            data, row);
        selectRow = row;
        // 不可编辑
        ((TTextField) getComponent("SEQ")).setEnabled(false);
        // 设置删除按钮状态
        ((TMenuItem) getComponent("delete")).setEnabled(true);
    }

    /**
     * 新增
     */
    public void onInsert() {
        if(this.getText("SEQ").trim().length()<=0||this.getText("FILE_TYPE").trim().length()<=0){
            this.messageBox("顺序号和病历类型不能为空！");
            return;
        }
        TParm parm = this.getParmForTag("SEQ;FILE_TYPE");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = MROSeqTool.getInstance().insertdata(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        // 显示新增数据
        int row = ( (TTable) getComponent("TABLE"))
            .addRow(
                parm,
                "SEQ;FILE_TYPE;OPT_USER;OPT_DATE;OPT_TERM");
        data.setRowData(row, parm);
        this.messageBox("添加成功！");
    }

    /**
     * 更新
     */
    public void onUpdate() {
        TParm parm = this.getParmForTag("SEQ;FILE_TYPE");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
                
        TParm result = MROSeqTool.getInstance().updatedata(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        // 选中行
        int row = ( (TTable) getComponent("Table")).getSelectedRow();
        if (row < 0)
            return;
        // 刷新，设置末行某列的值
        data.setRowData(row, parm);
        ( (TTable) getComponent("Table")).setRowParmValue(row, data);
        this.messageBox("修改成功！");
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
     * 查询
     */
    public void onQuery() {
        //String seq = getText("SEQ").trim();
        //String fileType = getText("FILE_TYPE").trim();
    	
        TParm parm = new TParm();
        //parm.setData("SEQ",seq);
        //parm.setData("FILE_TYPE",fileType);
        data = MROSeqTool.getInstance().selectType(parm);
        // 判断错误值
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ((TTable) getComponent("Table")).setParmValue(data);
    }
    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("SEQ;FILE_TYPE");
        ((TTable) getComponent("Table")).clearSelection();
        selectRow = -1;
        // 设置删除按钮状态
        ((TMenuItem) getComponent("delete")).setEnabled(false);
        ((TTextField) getComponent("SEQ")).setEnabled(true);
        onQuery();
    }

    /**
     * 删除
     */
    public void onDelete() {
        
    	if (this.messageBox("提示", "是否删除", 2) == 0) {            
        	if (selectRow == -1)
                return;            
            String seq = getValue("SEQ").toString();
                        
            int seqInt = Integer.parseInt(seq);
                 
            TParm result = MROSeqTool.getInstance().deletedata(seqInt);
            
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            
            TTable table = ( (TTable) getComponent("Table"));
            
            int row = table.getSelectedRow();
            
            if (row < 0)
                return;
            
            this.messageBox("删除成功！");
            onClear();
        }
        else {
            return;
        }
    }

    /**
     * 根据汉字输出拼音首字母
     *
     * @return Object
     */

    public Object onCode() {
//        if (TCM_Transform.getString(this.getValue("TYPE_DESC")).length() <
//            1) {
//            return null;
//        }
//        String value = TMessage.getPy(this.getValueString("TYPE_DESC"));
//        if (null == value || value.length() < 1) {
//            return null;
//        }
//        this.setValue("PY1", value);
//        // 光标下移
//        ((TTextField) getComponent("PY1")).grabFocus();
        return null;
    }
}
