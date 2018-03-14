package com.javahis.device;

/**
 * <p>Title: </p>
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
public class ChangeDriver {
    static {
      System.loadLibrary("ChangeDriver");
    }
    /**
     * 打开端口
     * @param port int 4,57600
     * @param baud long
     * @return int
     */
    public native int OpenCom(int port, long baud);
    /**
     * 关闭端口
     * @return int
     */
    public native int CloseCom();
    /**
     * 寻卡
     * @return int
     */
    public native int CapNBQueryCard();
    /**
     * 读卡
     * @return String
     * long	lCustomerID;	//3			//账号序号
     * long	lCardNO;	//2			//卡号
     * long	lStatus;	//1			//卡状态 F1H=正常 F3H=挂失
     * long	lKlb;		//1			//卡类别
     * long	strPWD;		//2			//个人密码
     * long	lZe;		//3			//总额	单位：分
     * long	lYe;		//3			//自费余额	单位：分
     * long	lOpCount;	//2			//操作计数
     * long	lCardSN;	//1			//个人持卡序号
     * long	lSubYe;		//3			//个人补助余额	单位：分
     */
    public native String CapGetNBCardInfo();
    public static void main(String args[])
    {
        ChangeDriver d = new ChangeDriver();
        //打开端口
        System.out.println(d.OpenCom(4,57600));
        //寻卡
        while(d.CapNBQueryCard()!=0)
            System.out.println("寻卡");
        System.out.println("寻卡成功");
        System.out.println(d.CapGetNBCardInfo());
        //关闭端口
        System.out.println(d.CloseCom());
    }
}
