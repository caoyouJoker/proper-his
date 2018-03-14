package com.javahis.ui.phl;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import jdo.phl.PHLSQL;

/**
 * <p>Title: 病患报到列表</p>
 *
 * <p>Description: 病患报到列表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.3.30
 * @version 1.0
 */
public class PHLRegisterListControl extends TControl {
    // TABLE
    private TTable table;
    // 病案号
    private String mr_no;

    public PHLRegisterListControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        table = (TTable) getComponent("TABLE");
        Object obj = getParameter();
        if (obj != null) {
            mr_no = ( (TParm) obj).getValue("MR_NO");
        }
        // 查询报道列表
        TParm parm = new TParm(TJDODBTool.getInstance().select(PHLSQL.
            getPHLRegisterList(mr_no)));
        if (parm == null || parm.getCount("START_DATE") <= 0) {
            this.messageBox("没有报道记录");
            return;
        }
        table.setParmValue(parm);
    }

    /**
     * TABLE双击事件
     */
    public void onTableDoubleClicked() {
        int row = table.getSelectedRow();
        setReturnValue(table.getParmValue().getRow(row));
        this.closeWindow();
    }

}
