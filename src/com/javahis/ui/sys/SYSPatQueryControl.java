package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * <p>Title: 病患查询</p>
 *
 * <p>Description: 病患查询</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2011.5.10
 * @version 1.0
 */
public class SYSPatQueryControl
    extends TControl {
    public SYSPatQueryControl() {
    }

    /**
     * 初始化
     */
    public void onInit() {
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            TParm parm = (TParm) obj;
            String sql =
                "SELECT MR_NO, PAT_NAME FROM SYS_PATINFO WHERE PY1 LIKE '%" +
                parm.getValue("PY1") + "%' ORDER BY MR_NO";
            TParm queryParm = new TParm(TJDODBTool.getInstance().select(sql));
            ( (TTable)this.getComponent("TABLE")).setParmValue(queryParm);
        }
    }

    /**
     * 回传PAT信息
     */
    public void onReturn() {
        TTable table = ( (TTable)this.getComponent("TABLE"));
        TParm returnParm = table.getParmValue().getRow(table.getSelectedRow());
        this.setReturnValue(returnParm);
        this.closeWindow();
    }
}
