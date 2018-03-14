package com.javahis.device;

import java.io.File;
import com.dongyang.util.FileTool;

/**
 *
 * <p>Title: 二代身份证接口</p>
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
public class SIDDriver {
    static {
      System.loadLibrary("sidDriver"); //载入dll
    }
    public native static int init();
    public native static int initComm(int port);
    public native static int closeComm();
    public native static int authenticate();
    public native static int readContent(int active);
    public native static String getDir();
    public static String read()
    {
        if(init() != 1)
            return "ERR:没有找到DLL文件!";
        if(initComm(1001) != 1)
        {
            closeComm();
            return "ERR:设备没有准备好!";
        }
        if(authenticate() != 1)
        {
            closeComm();
            return "ERR:无卡!";
        }
        if(readContent(1) != 1)
        {
            closeComm();
            return "ERR:卡片无效!";
        }

        return "OK";
    }
    public static void main(String args[])
    {
        System.out.println(read());
        System.out.println(getDir());
    }
}
