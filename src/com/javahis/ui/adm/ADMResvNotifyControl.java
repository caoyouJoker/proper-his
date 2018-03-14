package com.javahis.ui.adm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import jdo.adm.ADMResvTool;
import jdo.sys.Operator;

/**
 * <p>Title:预约住院，住院通知 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis </p>
 *
 * @author: JiaoY 2009.05.04
 * @version 1.0
 */
public class ADMResvNotifyControl extends TControl {
      TParm acceptData = new TParm(); //接参
    public void onInit() {
        acceptData=(TParm)this.getParameter();

       this.setUI();
    }
    /**
     * 页面初始化
     */
    public void setUI(){
        TParm parm = new TParm();
        parm.setData("RESV_NO",acceptData.getData("RESV_NO"));
        TParm result = ADMResvTool.getInstance().selectAll(parm);
        int notifyTimes = 0 ;
        if(result.getData("NOTIFY_TIMES",0)==null||"".equals(result.getData("NOTIFY_TIMES",0))){
            notifyTimes = 0;
        }else{
             notifyTimes =Integer.valueOf(result.getValue("NOTIFY_TIMES", 0))+1 ;

        }
         setValue("NOTIFY_TIMES", notifyTimes);
         setValue("NOTIFY_DATE",SystemTool.getInstance().getDate());
    }
    /**
     * 通知
     */
    public void onNotify(){
        TParm parm = new TParm();
        parm.setData("RESV_NO",acceptData.getData("RESV_NO"));//updataResvNotify
    parm.setData("NOTIFY_TIMES",getValue("NOTIFY_TIMES"));
     parm.setData("NOTIFY_DATE",getValue("NOTIFY_DATE"));
     parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
      TParm result = ADMResvTool.getInstance().updataResvNotify(parm);
      if(result.getErrCode()<0){
          this.messageBox("E0005");
          return ;
      }else{
        this.messageBox("P0005");
        this.closeWindow();
      }
    }
    /**
     * 取消
     */
    public void onCancel(){
     this.closeWindow();
    }
}
