package com.javahis.ui.pha;

import com.dongyang.control.TControl;
import com.dongyang.ui.TRadioButton;

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

public class PHAOptChoose extends TControl {
    /**
     * 构造函数
     */
    public PHAOptChoose() {
    }
    /**
     * 初始化函数
     */
    public void onInit() {
        super.onInit();
    }

    /**
     * 确定
     */
    public void onConfirm(){
        int optCount = 22;
        String chooseOpt = "";
        for(int i = 0;i < optCount;i++){
            if(!((TRadioButton)getComponent("tRadioButton_"+i)).isSelected())
                continue;
            chooseOpt = getOptMap(i);
        }
        if(chooseOpt.length() == 0)
            return;
        setReturnValue(chooseOpt);
        closeWindow();
    }

    /**
     * 操作编码对照
     * @param i int
     * @return String
     */
    private String getOptMap(int i){
        String chooseOpt = "";
        switch (i) {
        case 0:
            chooseOpt = "101";
            break;
        case 1:
            chooseOpt = "102";
            break;
        case 2:
            chooseOpt = "107";
            break;
        case 3:
            chooseOpt = "103";
            break;
        case 4:
            chooseOpt = "104";
            break;
        case 5:
            chooseOpt = "220";
            break;
        case 6:
            chooseOpt = "106";
            break;
        case 7:
            chooseOpt = "13";
            break;
        case 8:
            chooseOpt = "14";
            break;
        case 9:
            chooseOpt = "105";
            break;
        case 10:
            chooseOpt = "11";
            break;
        case 11:
            chooseOpt = "12";
            break;
        case 12:
            chooseOpt = "201";
            break;
        case 13:
            chooseOpt = "202";
            break;
        case 14:
            chooseOpt = "203";
            break;
        case 15:
            chooseOpt = "204";
            break;
        case 16:
            chooseOpt = "205";
            break;
        case 17:
            chooseOpt = "206";
            break;
        case 18:
            chooseOpt = "207";
            break;
        case 19:
            chooseOpt = "208";
            break;
        case 20:
            chooseOpt = "209";
            break;
        case 21:
            chooseOpt = "210";
            break;
        }
        return chooseOpt;
    }


    /**
     * 取消
     */
    public void onCancel(){
       setReturnValue("");
       closeWindow();
    }
}
