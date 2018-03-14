package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;

/**
 * <p>Title: 催账单</p>
 *
 * <p>Description: 催账单</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author zhangy 2010.5.12
 * @version 1.0
 */
public class BILReminderQueryControl
    extends TControl {

    private TTable table;

    public BILReminderQueryControl() {
    }

    /*
     * 初始化
     */
    public void onInit() {
        table = (TTable)this.getComponent("TABLE");
    }

    /**
     * 查询方法
     */
    public void onQuery() {

    }

    /**
     * 清空方法
     */
    public void onClear() {
        table.removeRowAll();
    }

    /**
     * 打印方法
     */
    public void onPrint(){

    }


}
