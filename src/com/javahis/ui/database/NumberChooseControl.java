package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.ENumberChoose;
import java.text.DecimalFormat;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextField;

/**
 *
 * <p>Title: 数字组件属性控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavHis</p>
 *
 * @author lzk 2010.2.6
 * @version 1.0
 */
public class NumberChooseControl
    extends TControl {
    /**
     * 数字组件
     */
    private ENumberChoose numberChoose;
    /**
     * 初始化
     */
    public void onInit() {
        numberChoose = (ENumberChoose) getParameter();
        if (numberChoose == null) {
            return;
        }
        setValue("NAME", numberChoose.getName());
        setValue("TEXT", numberChoose.getText());
        setValue("NUMBER_STEP", numberChoose.getNumberStep());
        setValue("MAX_VALUE_CHECK", numberChoose.isCheckMaxValue());
        setValue("MIN_VALUE_CHECK", numberChoose.isCheckMinValue());
        setValue("MAX_VALUE", numberChoose.getNumberMaxValue());
        setValue("MIN_VALUE", numberChoose.getNumberMinValue());
        setValue("NUMBER_TYPE_1", numberChoose.getNumberType() == 1);
        setValue("NUMBER_TYPE_2", numberChoose.getNumberType() == 2);
        setValue("NEGATIVE_NUMBER", numberChoose.canInputNegativeNumber());
        setValue("NUMBER_FORMAT", numberChoose.getNumberFormat());
        setValue("NUMBER_ENABLED", numberChoose.isEnabled());

        setValue("GROUP_NAME", numberChoose.getGroupName());

        //是元素则不能修改
        setValue("ALLOW_NULL", numberChoose.isAllowNull() ? "Y" : "N");
        setValue("CHK_ISCDA", numberChoose.isIsDataElements() ? "Y" : "N");
        setValue("CHK_LOCKED",numberChoose.isLocked()?"Y" : "N");

        /**if(numberChoose.isIsDataElements()){
            TTextField tf_group = (TTextField)getComponent("GROUP_NAME");
            TTextField tf_name = (TTextField)getComponent("NAME");
            TTextField tf_text = (TTextField)getComponent("TEXT");
            TCheckBox ck_text = (TCheckBox)getComponent("ALLOW_NULL");
            tf_name.setEnabled(false);
            tf_group.setEnabled(false);
            //tf_text.setEnabled(false);
            ck_text.setEnabled(false);
                 }**/


    }

    /**
     * 确定
     */
    public void onOK() {
        if (getText("NAME").length() == 0) {
            messageBox("请输入名称");
            return;
        }
        String format = getText("NUMBER_FORMAT");
        if (format.length() > 0) {
            try {
                String s = new DecimalFormat(format).format(100.01);
                double v = Double.parseDouble(s);
            }
            catch (Exception e) {
                messageBox("格式输入错误!");
                return;
            }
        }
        numberChoose.setName(getText("NAME"));
        numberChoose.setText(getText("TEXT"));
        numberChoose.setNumberStep(getValueDouble("NUMBER_STEP"));
        numberChoose.setCheckMaxValue(getValueBoolean("MAX_VALUE_CHECK"));
        numberChoose.setCheckMinValue(getValueBoolean("MIN_VALUE_CHECK"));
        numberChoose.setNumberMaxValue(getValueDouble("MAX_VALUE"));
        numberChoose.setNumberMinValue(getValueDouble("MIN_VALUE"));
        numberChoose.setNumberType(getValueBoolean("NUMBER_TYPE_1") ? 1 : 2);
        numberChoose.setCanInputNegativeNumber(getValueBoolean(
            "NEGATIVE_NUMBER"));
        numberChoose.setNumberFormat(format);
        numberChoose.setEnabled(getValueBoolean("NUMBER_ENABLED"));
        numberChoose.setGroupName(getText("GROUP_NAME"));
        if (getValue("CHK_ISCDA").equals("Y")) {
            numberChoose.setDataElements(true);
        }
        else {
            numberChoose.setDataElements(false);
        }
        numberChoose.setLocked(getValueBoolean("CHK_LOCKED"));

        closeWindow();
    }
}
