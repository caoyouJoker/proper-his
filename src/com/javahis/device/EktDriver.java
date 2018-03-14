package com.javahis.device;

import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title: 医疗卡接口</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 * 出现问题
 * 1 consume方法的用户编号不能使用int
 * @author lzk 2010.8.18
 * @version 1.0
 */
public class EktDriver {
    static {
        System.loadLibrary("EktDriver"); //载入dll
    }
    /**
     * 连接医疗卡的DLL
     * 华科安 苟工 133-7167-9169 你跟他联络
     * QQ:77051970
     * @return int
     */
    public native static int init();
    /**
     * 打开设备
     * 返回值：“00” 表示成功；非“00”表示失败
     * @return String
     */
    public native static String open();
    /**
     * 查询客户资料
     * "返回状态|卡片ID|卡片种类|持卡人身份|卡内余额|姓名|性别|身份证号|电话|地址"
     * "00|000000001|00|00|9000.00|张三丰|男性|100234191102040217|010-84357689|北京市海淀区"
     * @return String
     */
    public native static String readUser();
    /**
     * 消费扣款
     * @param sysID int 系统ID；1000 :HIS系统；1001：护理中心
     * @param opt String 操作人员代码
     * @param value double 消费金额；必须大于0
     * @param sdatetime String 消费日期时间（YYYY-MM-DD HH:MM:SS）
     * @return String 返回值：“返回状态|一卡通消费流水号” ，状态“00” 表示执行成功; 非”00”表示失败
     */
    public native static String consume(int sysID,String opt,double value,String sdatetime);
    /**
     * 消费冲正
     * @param sysID int 系统ID；1000 :HIS系统；1001：护理中心
     * @param opt String 操作人员代码
     * @param ID String 要冲正的消费记录流水号
     * @param sdatetime String 消费日期时间（YYYY-MM-DD HH:MM:SS）
     * @return String 返回状态|一卡通消费流水号 ，状态“00” 表示执行成功; 非”00”表示失败
     */
    public native static String unConsume(int sysID,String opt,String ID,String sdatetime);
    /**
     * 查询上一笔交易情况
     * @param sysID int 系统ID；1000 :HIS系统；1001：护理中心
     * @return String 返回查询内容；格式“状态|一卡通消费流水号|卡片ID|系统ID|操作人员ID|消费额|消费后余额|消费日期时间”
     */
    public native static String queryLastConusme(int sysID);
    /**
     * 查询指定交易流水号的交易情况
     * @param id String ID :交易流水号
     * @return String 返回查询内容；格式“状态|一卡通消费流水号|卡片ID|系统ID|操作人员ID|流水号|消费额|消费后余额|消费日期时间”
     */
    public native static String queryConusmeByID(String id);
    /**
     * 判断读写器上是否有卡
     * @return String 返回值：“00”表示有卡；“01”表示无卡；其他表示失败
     */
    public native static String hasCard();
    /**
     * 加载密码
     * @param key String 要加载的密码；例如“FFFFFFFFFFFF”
     * @return String “00” 表示成功；非“00”表示失败
     */
    public native static String loadKey(String key);
    /**
     * 按块读卡
     * @param index int 块号
     * @return String 状态|读出的数据；” 状态“00” 表示成功；非“00”表示失败
     */
    public native static String readCard(int index);
    /**
     * 按块写卡
     * @param index int 块号
     * @param data String 写入的数据；注意写入的为HEX的字符串（即字符串中只能包括“0”~”9”,”A”~”F”之间的字符）
     * @return String “00” 表示成功；非“00”表示失败
     */
    public native static String writeCard(int index,String data);
    /**
     * 关闭设备
     * @return String
     */
    public native static String close();
    /**
     * 读写器发出蜂鸣声音
     * @param times int 蜂鸣次数
     * @return String
     */
    public native static String beep(int times);


    public static void main(String args[])
    {
        System.out.println("init -> " + EktDriver.init());
        System.out.println("open -> " + EktDriver.open());
        System.out.println("readUser -> " + EktDriver.readUser());
        System.out.println("consume -> " + EktDriver.consume(1000,"123",100,"2010-08-18 11:18:10"));
        System.out.println("unConsume -> " + EktDriver.unConsume(1000,"123","0123456789012345","2010-08-18 11:18:10"));
        /*System.out.println("queryLastConusme -> " + EktDriver.queryLastConusme(1000));
        System.out.println("queryConusmeByID -> " + EktDriver.queryConusmeByID("0123456789012345"));
        System.out.println("hasCard -> " + EktDriver.hasCard());
        System.out.println("loadKey -> " + EktDriver.loadKey("FFFFFFFFFFFF"));*/
        System.out.println("readCard -> " + EktDriver.readCard(4));
        System.out.println("writeCard -> " + EktDriver.writeCard(4,"0"));
        System.out.println("close -> " + EktDriver.close());
    }
}
