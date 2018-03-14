package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.OperatorTool;
import com.javahis.system.root.RootClientListener;
import jdo.sys.Operator;
import com.dongyang.ui.TButton;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SYSPatLcokMessageControl
    extends TControl implements Runnable{
    TParm parm;
    String optUser;
    String prgId;
    String mrNo;
    String prgIdU;
    Thread thread;
    String userName;
    String windowId;
    /**
     * 初始化
     */
    public void onInit() {
        parm = (TParm)this.getParameter();
//        System.out.println("初始化入参"+parm);
        optUser = parm.getValue("OPT_USER", 0);
        prgId = parm.getValue("PRG_ID", 0);
        mrNo = parm.getValue("MR_NO");
        prgIdU = parm.getValue("PRGID_U");
        userName = OperatorTool.getInstance().getOperatorName(optUser);
        windowId =  parm.getValue("WINDOW_ID");
        setValue("MESSAGE", getPrgName(prgId) + userName + "锁定该病患");
       // RootClientListener.getInstance().addListener(this);
    }
    public void start()
    {
        setText("TIME","15");
        if(thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
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
            TButton button = (TButton)this.getComponent("OK");
            button.setEnabled(true);
            button = (TButton)this.getComponent("SEND");
            button.setEnabled(false);
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
    public boolean onClosing(){
        //RootClientListener.getInstance().removeListener(this);
        if(thread != null)
            thread = null;
        return true;
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
        if ("ONW".equalsIgnoreCase(prgId))//====pangben 2013-5-14 添加护士站解锁管控
            return "门诊护士站";
        if ("ENW".equalsIgnoreCase(prgId))//====pangben 2013-5-14 添加护士站解锁管控
            return "急诊护士站";
        return "";
    }

    /**
     * 通知按钮
     */
    public void toldButton() {
        this.start();
        boolean result =  RootClientListener.getInstance().sendMessage(optUser,
            "#ACTION#UI|"+windowId+"|onListenPm|" + prgId + "|" + mrNo + "|" + prgIdU + "|" +Operator.getID());
       TButton button = (TButton)this.getComponent("SEND");
        button.setEnabled(false);
//        System.out.println("通知返回"+result);
    }

    public void onCancel(){
        stop();
        this.closeWindow();
    }
    public void onOK(){
        stop();
        this.setReturnValue("OK");
        this.closeWindow();
    }
    /**
     * 回传结果
     * @param prgId String
     * @param flg Object
     * @return Object
     */
    public Object onListenRm(String prgId, String flg) {
        stop();
        if (!prgIdU.equalsIgnoreCase(prgId))
            return null;
        if ("OK".equals(flg)) {
            this.messageBox(userName + "已解锁");
            this.setReturnValue("UNLOCKING");
            this.closeWindow();
            return "OK";
        }
        if ("CANCEL".equals(flg)) {
            this.messageBox(userName + "拒绝解锁");
            this.setReturnValue("LOCKING");
            this.closeWindow();
            return "OK";
        }
        if ("TimeOut".equals(flg)) {
            this.messageBox(userName + "超时未响应");
            TButton button = (TButton)this.getComponent("OK");
            button.setEnabled(true);
            button = (TButton)this.getComponent("SEND");
            button.setEnabled(false);


            return "OK";
        }
        return "OK";
    }
}
