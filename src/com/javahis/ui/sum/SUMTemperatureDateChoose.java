package com.javahis.ui.sum;

import com.dongyang.control.TControl;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

/**
 * <p>Title: 体温单选择界面控制类</p>
 *
 * <p>Description: 体温单选择界面控制类</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */

public class SUMTemperatureDateChoose extends TControl {
    /**
     * 构造函数
     */
    public SUMTemperatureDateChoose() {
    }
    /**
     * 初始化函数
     */
    public void onInit() {
        super.onInit();
        setDefultValue();
    }
    /**
     * 设置默认值
     */
    public void setDefultValue(){
        setValue("DATE",TJDODBTool.getInstance().getDBTime());
    }
    /**
     * 确定
     */
    public void onConfirm(){
        if(getValue("DATE") == null ||
           ("" + getValue("DATE")).length() == 0||
           ("" + getValue("DATE")).equalsIgnoreCase("null"))
            return;
        setReturnValue(StringTool.getString((Timestamp)getValue("DATE"),"yyyyMMdd"));
        closeWindow();
    }
    /**
     * 取消
     */
    public void onCancel(){
       setReturnValue("");
       closeWindow();
    }
}
