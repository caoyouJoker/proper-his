package com.javahis.ui.ekt;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.ekt.EKTTool;
import jdo.sys.OperatorTool;

/**
 * <p>Title: 医疗卡密码确认</p>
 *
 * <p>Description: 医疗卡密码确认</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20111007
 * @version 1.0
 */
public class EKTPassWordSureControl extends TControl{
    public EKTPassWordSureControl() {
    }
    private TParm ektParm;//医疗卡信息
    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        onInitParm((TParm)getParameter());
        //zhangp 20121219 加焦点
        this.grabFocus("CARD_PWD");
    }
    /**
     * 获得参数
     * @param parm TParm
     */
    private void onInitParm(TParm parm){
        if(parm == null)
            return;
        ektParm = parm;
    }
    /**
     * 执行方法
     */
    public void onSave(){
        if(this.getValue("CARD_PWD").toString().length()<=0){
            this.messageBox("密码不可以为空");
            this.grabFocus("CARD_PWD");
            return ;
        }
        ektParm.setData("CARD_NO",ektParm.getValue("MR_NO")+ektParm.getValue("SEQ"));
        TParm result = EKTTool.getInstance().selectPWDEKTIssuelog(ektParm);
        String old_pwd=OperatorTool.getInstance().decrypt(result.getValue("PASSWORD",0));//解密
        if(result.getCount()<=0 || !this.getValue("CARD_PWD").toString().equals(old_pwd)){
            this.messageBox("密码不正确");
            this.grabFocus("CARD_PWD");
        }else{
            TParm parm =new TParm();
            parm.setData("FLG","Y");
            setReturnValue(parm);
            closeWindow();
        }
    }

    /**
     * 取消键
     */
    public void onCancel() {
        closeWindow();
    }

}
