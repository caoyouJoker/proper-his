package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import java.sql.Timestamp;
/**
 *
 * <p>Title:练习 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author wangl 2008.12.18
 * @version 1.0
 */
public class TextControl extends TControl{
    /**
     * 保存事件
     */
    public void onUpdate()
    {
        Timestamp date = SystemTool.getInstance().getDate();
        TTable table = (TTable)callFunction("UI|TABLE|getThis");
        //接收文本
        table.acceptText();
        int rows[] = table.getModifiedRows();
        for(int i = 0;i < rows.length;i++)
        {
            table.setItem(rows[i],"OPT_USER",Operator.getID());
            table.setItem(rows[i],"OPT_DATE",date);
            table.setItem(rows[i],"OPT_TERM",Operator.getIP());
        }
        if(!table.update())
        {
            messageBox("E0001");
            return;
        }
        messageBox("P0001");
    }
}
