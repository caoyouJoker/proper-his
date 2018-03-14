package com.javahis.util;

import com.dongyang.util.TDebug;
import jdo.sys.Operator;
import com.dongyang.config.TConfig;
import jdo.sys.MessageTool;
import com.dongyang.config.TConfigParm;
import com.dongyang.util.TSystem;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TDialog;
import javax.swing.UIManager;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TWindow;

/**
 *
 * <p>Title: 调试类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class JavaHisDebug extends TDebug{
    /**
     * 是否初始化
     */
    private static boolean isInit;
    /**
     * 客户端测试初始化(测试程序专用)
     */
    public static void initClient()
    {
        if(isInit)
            return;
        setWindowFeel();
        TDebug.initClient();
        TConfig config = new TConfig("Debug.ini",null);
        String userID = config.getString("","Opt.UserID");
        String IP = config.getString("","Opt.IP");
        String region = config.getString("","Opt.Region");
        String dept = config.getString("","Opt.Dept");
        String station = config.getString("","Opt.Station");
        //创建用户对象
        Operator.setData(userID,region,IP,dept,station);
        TSystem.setObject("MessageObject",new MessageTool());
        isInit = true;

    }
    /**
     * 服务器端测试初始化(测试程序专用)
     */
    public static void initServer()
    {
        TDebug.initServer();
    }
    /**
     * 设置Window风格
     */
    private static void setWindowFeel()
    {
        try {
            UIManager.setLookAndFeel(
                "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }
        catch (Exception e) {
        }
    }
    /**
     * 运行TBuilder
     */
    public static void TBuilder()
    {
        runFrame("tbuilder\\main\\TBuilder.x");
    }
    /**
     * 运行窗口
     * @param fileName String 文件名
     * @return TFrame
     */
    public static TFrame runFrame(String fileName)
    {
        return TFrame.openWindow(getConfigParm(fileName));
    }
    /**
     * 运行窗口
     * @param fileName String 文件名
     * @param parmValue Object 参数
     * @return TFrame
     */
    public static TFrame runFrame(String fileName,Object parmValue)
    {
        return TFrame.openWindow(getConfigParm(fileName),parmValue);
    }
    /**
     * 运行对话框
     * @param fileName String 文件名
     * @return Object
     */
    public static Object runDialog(String fileName)
    {
        return TDialog.openWindow(getConfigParm(fileName));
    }
    /**
     * 运行对话框
     * @param fileName String 文件名
     * @param parmValue Object 参数
     * @return Object
     */
    public static Object runDialog(String fileName,Object parmValue)
    {
        return TDialog.openWindow(getConfigParm(fileName),parmValue);
    }
    /**
     * 运行浮动窗口
     * @param fileName String 文件名
     * @param parent TComponent
     * @return TWindow
     */
    public static TWindow runWindow(String fileName,TComponent parent)
    {
        return runWindow(fileName,parent,null);
    }
    /**
     * 运行浮动窗口
     * @param fileName String 文件名
     * @param parent TComponent
     * @param parmValue Object
     * @return TWindow
     */
    public static TWindow runWindow(String fileName,TComponent parent,Object parmValue)
    {
        return TWindow.openWindow(getConfigParm(fileName),parent,parmValue);
    }
    /**
     * 得到配置对象
     * @param fileName String
     * @return TConfigParm
     */
    private static TConfigParm getConfigParm(String fileName)
    {
        JavaHisDebug.initClient();
        TConfigParm parm = new TConfigParm();
        parm.setSocket(TIOM_AppServer.SOCKET);
        parm.setSystemGroup("");
        parm.setConfig("%ROOT%\\config\\" + fileName);
        parm.setConfigColor("%ROOT%\\config\\system\\TColor.x");
        parm.setConfigClass("%ROOT%\\config\\system\\TClass.x");
        return parm;
    }
}
