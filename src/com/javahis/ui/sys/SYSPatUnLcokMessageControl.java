package com.javahis.ui.sys;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.sys.OperatorTool;
import jdo.sys.Operator;
import com.javahis.system.root.RootClientListener;

/**
 * <p>Title: 病患被强制解锁提示界面</p>
 *
 * <p>Description: 病患被强制解锁提示界面</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-12-28
 * @version 1.0
 */
public class SYSPatUnLcokMessageControl
    extends TControl implements Runnable{
    String mrNo;
    String optUser;
    String prgId;
    String prgIdU;
    Thread thread;
    public void onInit(){
        super.onInit();
        Object obj = this.getParameter();
        if(obj != null)
        {
            TParm parm = (TParm) obj;
            optUser = parm.getValue("USE_ID");
            prgId = parm.getValue("PRG_ID");
            mrNo = parm.getValue("MR_NO");
            prgIdU = parm.getValue("PRG_ID_U");
            String userName = OperatorTool.getInstance().getOperatorName(
                optUser);
            setValue("MESSAGE", getPrgName(prgIdU) + userName + "请求解锁病患,是否同意?");
        }
        setText("TIME","15");
        start();
    }
    public void start()
    {
        thread = new Thread(this);
        thread.start();
    }
    public void stop()
    {
        thread = null;
    }
    public void run()
    {
        while(thread != null)
        {
            sleep();
            int x = Integer.parseInt(getText("TIME"));
            x--;
            if(x >= 0)
            {
                setText("TIME","" + x);
                continue;
            }
            thread = null;
            RootClientListener.getInstance().sendMessage(optUser,
            "#ACTION#UI|onListenRm|" + prgIdU +"|"+"TimeOut");
            this.setReturnValue("OK");
            this.closeWindow();
        }
    }
    public void sleep()
    {
        if(thread == null)
            return;
        try{
            thread.sleep(1000);
        }catch(Exception e)
        {

        }
    }
    /**
     * 得到系统名称
     * @param prgId String
     * @return String
     */
    public String getPrgName(String prgId) {

        if ("ODO".equalsIgnoreCase(prgId))
            return "门诊医生站";
        if ("ODE".equalsIgnoreCase(prgId))
            return "急诊医生站";
        if ("OPB".equalsIgnoreCase(prgId))
            return "门诊收费";
        if ("ODI".equalsIgnoreCase(prgId))
            return "住院医生站";
        if ("ONW".equalsIgnoreCase(prgId))
            return "门诊护士站";
        return "";
    }

    public void onOK() {
        stop();
        boolean result = RootClientListener.getInstance().sendMessage(optUser,
            "#ACTION#UI|onListenRm|" + prgIdU +"|"+"OK");
        this.setReturnValue("OK");
        this.closeWindow();
    }

    public void onCancel() {
        stop();
        boolean result = RootClientListener.getInstance().sendMessage(optUser,
            "#ACTION#UI|onListenRm|" + prgIdU +"|"+"CANCEL");
        this.closeWindow();

    }

    public boolean onClosing(){
        if(thread != null){
            stop();
            boolean result = RootClientListener.getInstance().sendMessage(
                optUser,
                "#ACTION#UI|onListenRm|" + prgIdU + "|" + "CANCEL");
        }
        return true;
    }
}
