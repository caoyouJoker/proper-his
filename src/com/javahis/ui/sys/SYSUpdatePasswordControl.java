package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TLabel;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 用户修改密码</p>
 *
 * <p>Description: 用户修改密码</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHIS</p>
 *
 * @author zhangy 2010.4.10
 * @version 1.0
 */
public class SYSUpdatePasswordControl
    extends TControl {

    private String user_id;

    private String old_user_password;

    private String loginUI = "";
    private TParm parm = null;//===pangben modify 20110608
    public SYSUpdatePasswordControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        user_id = Operator.getID();
        old_user_password = OperatorTool.getInstance().decrypt(OperatorTool.
            getInstance().getOperatorPassword(user_id));
        //================pangben modify 20110531 start
        Object obj = this.getParameter();
        if (obj instanceof String) {
            loginUI = (String) obj;
        }
        if (null != loginUI && loginUI.equals("Y")) {
            TButton button = (TButton)this.getComponent("BTN_CANCEL");
            button.setEnabled(false);
        }
        //================pangben modify 20110531 stop
        //============pangben modify 20110607 start
        //查询此区域密码是否显示校验强度 。如果数据库PWD_STRENGTH字段为'N'将不显示校验，否则显示
        if (null != Operator.getRegion() && !"".equals(Operator.getRegion())) {

            String pwdSQL =
                    "SELECT PWD_STRENGTH,DETECTPWDTIME FROM SYS_REGION WHERE REGION_CODE='" +
                    Operator.getRegion() + "'";
            parm = new TParm(TJDODBTool.getInstance().select(pwdSQL));
        } else {
            parm = new TParm();
            parm.setData("PWD_STRENGTH",0, "N");
            parm.setData("DETECTPWDTIME",0, "999");//默认天数
        }
        if ("N".equals(parm.getValue("PWD_STRENGTH", 0))) {
            callFunction("UI|pwd_LBL|setVisible", false);
            callFunction("UI|PWD_POOFSTH|setVisible", false);
        } else {
            callFunction("UI|pwd_LBL|setVisible", true);
            callFunction("UI|PWD_POOFSTH|setVisible", true);
        }
        this.setValue("pwd_LBL", ""); //出师界面不显示
        //============pangben modify 20110607 stop

    }
    /**
     * 确定事件
     */
    public void onOK() {
        //============pangben modify 20110608 start
        TLabel pwdLbl = (TLabel)this.getComponent("PWD_POOFSTH");
        if (pwdLbl.isVisible())
            pwdPoofSth();
        //============pangben modify 20110608 stop
        String old_password = this.getValueString("OLD_PASSWORD");
        String new_password = this.getValueString("NEW_PASSWORD");
        String check_password = this.getValueString("CHECK_PASSWORD");
        if ("".equals(old_password)) {
            this.messageBox("旧密码不能为空");
            return;
        }

        //=======pangben modify 20110631 start
         //验证密码强度，若强度过低不可以保存
         TLabel lbl = (TLabel)this.getComponent("pwd_LBL");
         if (pwdLbl.isVisible() && lbl.getValue().equals("低")) {
             this.messageBox("密码强度太低,不可以保存!");
             return;
         }
         //=======pangben modify 20110631 stop

        if ("".equals(new_password)) {
            this.messageBox("新密码不能为空");
            return;
        }
        if ("".equals(check_password)) {
            this.messageBox("确认密码不能为空");
            return;
        }
        if (!check_password.equals(new_password)) {
            this.messageBox("新密码与确认密码不相同");
            return;
        }
        if (!old_user_password.equals(old_password)) {
            this.messageBox("旧密码输入不正确");
            return;
        }
        boolean flg = OperatorTool.getInstance().setOperatorPassword(user_id,
            OperatorTool.getInstance().encrypt(check_password));
        if (flg) {
            if (!OperatorTool.getInstance().updatePwdstartDate(user_id,parm)) {
               err("第一次修改密码错误");
               return;
           }
            this.messageBox("密码更新成功");
            this.closeWindow();
        }
        else {
            this.messageBox("密码更新失败");
        }
    }
    /**
     * 取消事件
     */
    public void onCancel() {
        this.closeWindow();
    }


    /**
     * 密码文本框显示密码强度方法
     * pangben modify 20110607
     */
    public void pwdPoofSth() {
        //pwdMessageShow = true;
        //只有存在用户才能显示校验强度
        String sPW = this.getValueString("NEW_PASSWORD");
        TLabel lbl = (TLabel)this.getComponent("pwd_LBL");
        if (sPW.length() <= 4) {
            this.setValue("pwd_LBL", "低");
            lbl.setColor("RED");
            return; //密码太短
        }
        String modes = "";
        for (int i = 0; i < sPW.length(); i++) {
            //测试每一个字符的类别并统计一共有多少种模式.
            modes = modes + getCharCode(sPW.charAt(i));
        }
        int result = bitTotal(modes);
        switch (result) {
        case 0:
            this.setValue("pwd_LBL", "低");
            lbl.setColor("RED");
            break;
        case 1:
            this.setValue("pwd_LBL", "中");
            lbl.setColor("绿");
            break;
        case 2:
            this.setValue("pwd_LBL", "强");
            lbl.setColor("BLUE");
            break;
        default:
            this.setValue("pwd_LBL", "低");
            lbl.setColor("RED");
            break;
        }

    }

    /**
     * 验证密码强度返回显示强度的数字：0 低，1 中，2 强
     * @param modes String
     * @return int
     * pangben modify 20110607
     */
    public int bitTotal(String modes) {
        int ans = 0;
        if (modes.contains("1") && modes.contains("2") && modes.contains("3"))
            ans = 2;
        else if (modes.contains("1") && modes.contains("2"))
            ans = 1;
        else if (modes.contains("2") && modes.contains("3"))
            ans = 1;
        else if (modes.contains("1") && modes.contains("3"))
            ans = 1;
        else
            ans = 0;
        return ans;
    }

    /**
     * 通过验证每一个字符的ASCII码值获得需要的数值
     * @param iN char
     * @return String
     * pangben modify 20110607
     */
    public String getCharCode(char iN) {
        if (iN >= 48 && iN <= 57) //数字
            return "1";
        if (iN >= 65 && iN <= 90) //大写字母
            return "2";
        if (iN >= 97 && iN <= 122) //小写
            return "2";
        else
            return "3"; //特殊字符
    }
}
