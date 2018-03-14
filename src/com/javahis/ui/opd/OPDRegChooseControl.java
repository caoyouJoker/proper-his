package com.javahis.ui.opd;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;

/**
 * <p>Title: 挂号信息选择</p>
 *
 * <p>Description: 挂号信息选择</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-10-28
 * @version 1.0
 */
public class OPDRegChooseControl
    extends TControl {
    private TParm DATA;
    public void onInit(){
        Object obj = this.getParameter();
        if (obj == null || !(obj instanceof TParm)) {
            return;
        }
        DATA = (TParm)obj;
        ((TTable)this.getComponent("TABLE")).setParmValue(DATA);
    }
    /**
     * 表格双击事件
     */
    public void onDoubleClick(){
        TTable table = (TTable)this.getComponent("TABLE");
        int row = table.getSelectedRow();
        this.setReturnValue(DATA.getRow(row));
        this.closeWindow();
    }
}
