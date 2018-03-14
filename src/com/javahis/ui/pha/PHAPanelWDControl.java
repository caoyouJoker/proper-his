package com.javahis.ui.pha;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;
import jdo.sys.Operator;

/**
 * <p>Title: 主面板内部的panel（西药）</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author ZangJH
 *
 * @version 1.0
 */
public class PHAPanelWDControl
    extends TControl {
    public PHAPanelWDControl() {
    }
    public void onInit() {
        super.onInit();
        //================pangben modify 20110405 start 区域锁定
       setValue("REGION_CODE", Operator.getRegion());
       //================pangben modify 20110405 stop

//        this.messageBox_("ppppppppp");
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        //用当前时间初始化时间控件
        TTextFormat fromDate = (TTextFormat)this.getComponent("from_ORDER_DATE");
        TTextFormat toDate = (TTextFormat)this.getComponent("to_ORDER_DATE");
        fromDate.setValue(date);
        toDate.setValue(date);
    }
}
