package jdo.sys;

import com.dongyang.jdo.TStrike;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;

/**
 *
 * <p>Title: 用户加锁记录</p>
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
public class PATLockTool extends TStrike{
    /**
     * 唯一实例
     */
    private static PATLockTool instance;
    /**
     * 日期时间格式
     */
    public static DateFormat dateFormat = new SimpleDateFormat(
            "yyyy/MM/dd hh:mm:ss.SS");
    /**
     * 得到实例
     * @return TJDODBTool
     */
    public static PATLockTool getInstance()
    {
        if(instance == null)
            instance = new PATLockTool();
        return instance;
    }
    /**
     * 得到当前的时间
     * @return String
     */
    public static String getTime() {
        return dateFormat.format(new Date());
    }
    /**
     * 加锁记录
     * @param text String
     * @return boolean
     */
    public boolean log(String text) {
        if(isClientlink())
            return (Boolean)callServerMethod(text);
//        System.out.println("-----------------------");
//        System.out.println("PatLockLog " + getTime());
//        System.out.println(text);
//        System.out.println("-----------------------");
        return true;
    }
}
