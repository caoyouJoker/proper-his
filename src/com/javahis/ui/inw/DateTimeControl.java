package com.javahis.ui.inw;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;

/**
 * <p>Title: 初始化当前时间使用，应用于时间控件所在的pannel的Contorl</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author ZangJH 2009-10-30
 *
 * @version 1.0
 */

public class DateTimeControl
    extends TControl {
    public DateTimeControl() {
    }

    public void onInit() {
        super.onInit();

        Timestamp date = TJDODBTool.getInstance().getDBTime();
        //用当前时间初始化时间控件
        TTextFormat stratDate = (TTextFormat)this.getComponent("START_DATE");
        TTextFormat endDate = (TTextFormat)this.getComponent("END_DATE");
        stratDate.setValue(date);
        endDate.setValue(date);
    }

}
