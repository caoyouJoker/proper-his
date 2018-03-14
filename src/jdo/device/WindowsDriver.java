package jdo.device;

import com.dongyang.util.StringTool;
import com.dongyang.config.TRegistry;
import java.util.Date;

/**
 *
 * <p>Title: Window系统底层接口</p>
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
public class WindowsDriver {
    static {
        System.loadLibrary("WindowsDriver"); //载入dll
    }
    /**
     * 得到机器名
     * @return String
     */
    public native static String getComputer();
    /**
     * 得到网卡端口序号
     * @return String 2|7|5 个数|端口1|端口2
     */
    private native static String getMacIndex();
    /**
     * 得到网卡地址
     * @return String 2|00-21-00-20-E1-42|00-1D-72-58-7C-7A 个数|地址1|地址2
     */
    private native static String getMac();
    /**
     * 得到当前时间
     * @return String 2010|9|28|11|42|12 年|月|日|时|分|秒
     */
    private native static String getDate();
    /**
     * 得到网卡序号
     * @return String
     * 00210020E142001D72587C7A
     * 00000000000000210020E142
     */
    private static String getMacNo()
    {
        String s = getMac();
        if(s.startsWith("ERR"))
            return StringTool.fill0("",48);
        StringBuffer sb = new StringBuffer();
        String ss[] = StringTool.parseLine(s,"|");
        int count = StringTool.getInt(ss[0]);
        for(int i = 0;i < count;i++)
        {
            String s1 = ss[i + 1];
            String s2[] = StringTool.parseLine(s1,"-");
            for(int j = 0;j < s2.length;j++)
                sb.append(s2[j]);
        }
        return StringTool.fill0(sb.toString(),48);
    }
    private static String[] getMacList()
    {
        String s = getMac();
        if(s.startsWith("ERR"))
            return null;
        String ss[] = StringTool.parseLine(s,"|");
        int count = StringTool.getInt(ss[0]);
        String values[] = new String[count];
        for(int i = 0;i < count;i++)
        {
            String s1 = ss[i + 1];
            StringBuffer sb = new StringBuffer();
            String s2[] = StringTool.parseLine(s1,"-");
            for(int j = 0;j < s2.length;j++)
                sb.append(s2[j]);
            values[i] = sb.toString();
        }
        return values;
    }
    private static String P1(String s,int l)
    {
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i < s.length();i++)
        {
            int x = s.charAt(i);
            sb.append(Integer.toHexString(x));
        }
        return StringTool.fill0(sb.toString().toUpperCase(),l);
    }
    private static String P3(String s)
    {
        int x = 0;
        byte b[] = s.getBytes();
        for(int i = 0;i < b.length;i++)
        {
            if(b[i] >= 0)
                x += b[i];
            else
                x += b[i] & 255;
        }
        return "" + x;
    }
    private static String P2(String s)
    {
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i < s.length() - 1;i+=2)
        {
            String s1 = s.substring(i,i + 2);
            char x = (char)(int)Integer.valueOf(s1,16);
            if(x == 0)
                continue;
            sb.append(x);
        }
        return sb.toString();
    }
    private static String fill(String s,int i)
    {
        String computer = s;
        if(computer.length() > i)
            return computer.substring(0,i);
        return StringTool.fill0(computer,i);
    }
    public static String getOutNo(String coname)
    {
        String computer = P1(getComputer(),20);
        String mac = getMacNo();
        String s = computer + mac + fill(P3(coname),8);
        return II(I(s));
    }
    private static boolean checkConame(String coname,String code)
    {
        return fill(P3(coname),8).equals(code);
    }
    private static String[] getInNo(String no)
    {
        if(no == null || no.length() != 92)
            return null;
        String s = I(OO(no));
        String s1 = P2(s.substring(0,20));
        String s2 = s.substring(20,32);
        String s3 = s.substring(32,44);
        String s4 = s.substring(44,56);
        String s5 = s.substring(56,68);
        String s6 = s.substring(68,76);
        String s7 = s.substring(76,84);
        String s8 = s.substring(84);
        return new String[]{s1,s2,s3,s4,s5,s6,s7,s8};
    }
    private static String I(String s)
    {
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i < s.length();i++)
        {
            char c = s.charAt(i);
            if((i % 2) == 1)
            {
                sb.append(c);
                continue;
            }
            if(c >= '0' && c <= '9')
            {
                sb.append((char) (9 - c + '0' + '0'));
                continue;
            }
            if(c >= 'A' && c <= 'Z')
            {
                sb.append((char) (25 - c + 'A' + 'A'));
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }
    private static String II(String s)
    {
        int l = (int)(s.length() / 2.0 + 0.99);
        String s1 = s.substring(0,l);
        String s2 = s.substring(l);
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i < s1.length();i++)
        {
            sb.append(s1.charAt(i));
            if(s2.length() > i)
                sb.append(s2.charAt(s2.length() - i - 1));
        }
        return sb.toString();
    }
    private static String OO(String s)
    {
        StringBuffer s1 = new StringBuffer();
        StringBuffer s2 = new StringBuffer();
        for(int i = 0;i < s.length();i++)
        {
            char c = s.charAt(i);
            if ( (i % 2) == 1)
                s2.insert(0,c);
            else
                s1.append(c);
        }
        return s1.toString() + s2.toString();
    }
    /**
     * 得到注册号码
     * @return String
     */
    public static String getKey()
    {
        return TRegistry.get("HKEY_CURRENT_USER\\Software\\JavaHis\\1.0\\Key");
    }
    /**
     * 写入注册号码
     * @param key String
     */
    public static void setKey(String key)
    {
        TRegistry.set("HKEY_CURRENT_USER\\Software\\JavaHis\\1.0\\Key",key);
    }
    /**
     * 得到公司名称
     * @return String
     */
    public static String getConame()
    {
        return TRegistry.get("HKEY_CURRENT_USER\\Software\\JavaHis\\1.0\\Coname");
    }
    /**
     * 设置公司名称
     * @param coname String
     */
    public static void setConame(String coname)
    {
        TRegistry.set("HKEY_CURRENT_USER\\Software\\JavaHis\\1.0\\Coname",coname);
    }
    public static String checkKey()
    {
        return check();
    }
    /**
     * 测试注册
     * @return String
     */
    private static String check()
    {
        String coname = getConame();
        if(coname == null || coname.length() == 0)
            return "没有注册";
        String key = getKey();
        if(key == null || key.length() == 0)
            return "没有注册";
        String s1[] = getInNo(key);
        if(s1 == null)
            return "没有注册";
        String computer = getComputer();
        if(computer == null || s1[0] == null || !computer.endsWith(s1[0]))
            return "计算机名称与注册不符合";
        String mac[] = getMacList();
        if(mac != null)
            for(int i = 0;i < mac.length;i++)
                if(!s1[1].equals(mac[i]) && !s1[2].equals(mac[i]) && !s1[3].equals(mac[i]) && !s1[4].equals(mac[i]))
                    return "网卡地址与注册不符合";
        String cono = fill(P3(coname),8);
        if(!cono.equals(s1[5]))
            return "公司名称与注册不符合";
        if(!checkDate(s1[6],s1[7]))
            return "超过注册使用期限";
        return "OK";
    }
    public static boolean checkDate(String s1,String s2)
    {
        Date d = new Date();
        String sd = StringTool.getString(d,"yyyyMMdd");
        if(sd.compareTo(s1) < 0)
            return false;
        if(sd.compareTo(s2) > 0)
            return false;
        return true;
    }
    public static void main(String args[])
    {
        //String s = II("123456789");
        //System.out.println(s);
        //System.out.println(OO(s));
        //System.out.println(getMacNo());
        /*String s = getOutNo("深圳中航软件有限公司");
        System.out.println(s);
        s = "99059105900990099205910V9007900921A730A9900990099009900990099009900990099009";
        System.out.println(s);*/
        //String s1[] = getInNo("9109910990029908910991099002990829A531A5900990099205910V900790099107900990099009900990099009");
        //for(int i = 0;i < s1.length;i++)
        //    System.out.println(s1[i]);
        WindowsDriver.setConame("深圳中航软件有限公司");
        WindowsDriver.setKey("9409910891089007940991089008900729A531A5900990099205910V900790099107900990099009900990099009");
        System.out.println(check());
        //System.out.println(checkConame("深圳中航软件有限公司",s1[3]));
        //System.out.println(checkDate("20101028","20101029"));
    }
    //9006900890089007980790089008900729A531A5900990099205910V900790099107
}


//99059105900990099A029C02980492022DA830A99205711V9007
//9006900890089007980790089008900729A531A5900970199A027C02V81452229D08
