package com.javahis.ui.ekt;

import jdo.ekt.EKTIO;
import com.dongyang.control.TControl;
import jdo.sys.Pat;
import com.dongyang.data.TParm;
import com.dongyang.util.TypeTool;
import jdo.ekt.EKTTool;
import jdo.sys.OperatorTool;

/**
 * <p>Title: 医疗卡密码修改</p>
 *
 * <p>Description: 医疗卡密码修改</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20111010
 * @version 1.0
 */
public class EKTUpdatePassWordControl extends TControl{
    private Pat pat; //病患信息
    private TParm parmEKT;
    private boolean ektFlg = false;
    public EKTUpdatePassWordControl() {
    }
    /**
    * 初始化方法
    */
   public void onInit() {
   }

    /**
    * 读医疗卡
    */
   public void onReadEKT() {
       //读取医疗卡
       parmEKT = EKTIO.getInstance().TXreadEKT();
       if (null == parmEKT || parmEKT.getErrCode() < 0 ||
           parmEKT.getValue("MR_NO").length() <= 0) {
           this.messageBox(parmEKT.getErrText());
           parmEKT = null;
           return;
       }
       //卡片余额
       this.setValue("OLD_EKTFEE", parmEKT.getDouble("CURRENT_BALANCE"));
       //卡片序号
       this.setValue("SEQ", parmEKT.getValue("SEQ"));
       //callFunction("UI|CARD_CODE|setEnabled", false); //卡号不可编辑
       this.setValue("MR_NO", parmEKT.getValue("MR_NO"));


       onQuery();
   }
   /**
        * 查询病患信息
        */
       private void onQuery() {
           pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
           if (pat == null) {
               this.messageBox("无此病案信息");
               this.setValue("MR_NO", "");
               return;
           }
           TParm parm = new TParm();
           parm.setData("CARD_NO",pat.getMrNo()+ parmEKT.getValue("SEQ"));
           parmEKT = EKTTool.getInstance().selectPWDEKTIssuelog(parm);
           if (parmEKT.getCount() <= 0) {
               this.messageBox("此病患没有医疗卡信息");
               //不存在医疗卡，写卡时操作创建EKT_MASTER表信息
               ektFlg = true;
           }
           //病案号
           this.setValue("MR_NO", pat.getMrNo());
           //姓名
           this.setValue("PAT_NAME", pat.getName());
           //出生日期
           setValue("BIRTH_DATE", pat.getBirthday());
           //性别
           setValue("SEX_CODE", pat.getSexCode());
           callFunction("UI|MR_NO|setEnabled", false); //病案号不编辑
    }
    /**
     * 修改密码
     */
    public void onSave(){
        if(null == parmEKT || parmEKT.getErrCode() < 0 || null == pat){
            this.messageBox("没有获得医疗卡信息");
            return;
        }
        if(ektFlg){
            this.messageBox("没有保存医疗卡信息不可以修改密码");
            return;
        }

        if(this.getValue("NEW_PWD").toString().length()<=0){
            this.messageBox("新密码不可以为空");
            return;
        }
        if(!this.getValue("NEW_PWD").equals(this.getValue("CHECK_PASSWORD"))){
            this.messageBox("两次输入的新密码不符请重新输入");
            this.setValue("CHECK_PASSWORD","");
            this.grabFocus("CHECK_PASSWORD");
            return;
        }

        //====zhangp 20120413 start
        //旧密码比较是否相同
//        String oldPwd = parmEKT.getValue("PASSWORD",0);
//        String old_pwd= OperatorTool.getInstance().decrypt(oldPwd);//11
//        if(old_pwd.equals(this.getValue("OLD_PWD").toString())){
            TParm parm=new TParm();
            String new_pwd= OperatorTool.getInstance().encrypt(this.getValue("NEW_PWD").toString());
            parm.setData("PASSWORD",new_pwd);
            parm.setData("CARD_NO",parmEKT.getValue("CARD_NO",0));
            parm=EKTTool.getInstance().updateEKTPWD(parm);//修改密码
            if(parm.getErrCode()<0){
                this.messageBox("密码修改失败");
            }else{
                this.messageBox("密码修改成功");
                onClear();
            }
//        }else{
//            this.messageBox("旧密码输入错误");
//        }
            ///===zhangp 20120413 end
    }
    /**
     * 清空操作
     */
    public void onClear() {
        clearValue(" MR_NO;PAT_NAME;SEQ; " +
                   " BIRTH_DATE;SEX_CODE; " +
                   " OLD_PWD;NEW_PWD;CHECK_PASSWORD ");
        parmEKT = null;
        ektFlg = false;
        pat = null;
    }
}
