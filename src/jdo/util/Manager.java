package jdo.util;

/**
 * 
 * <p>
 * Title: 共用管理类
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author lzk 2009.6.2
 * @version JavaHis 1.0
 */
public class Manager {
	/**
	 * 得到系统共用
	 * 
	 * @return Systems
	 */
	public static Systems getSystem() {
		return new Systems();
	}

	/**
	 * 得到医嘱共用
	 * 
	 * @return Order
	 */
	public static Order getOrder() {
		return new Order();
	}

	/**
	 * 得到组织共用
	 * 
	 * @return Organization
	 */
	public static Organization getOrganization() {
		return new Organization();
	}

	/**
	 * 得到药品共用
	 * 
	 * @return Medicine
	 */
	public static Medicine getMedicine() {
		return new Medicine();
	}

	/**
	 * 得到费用共用
	 * 
	 * @return Charge
	 */
	public static Charge getCharge() {
		return new Charge();
	}

	/**
	 * 得到人员共用
	 * 
	 * @return Personal
	 */
	public static Personal getPersonal() {
		return new Personal();
	}

	/**
	 * 得到其他共用
	 * 
	 * @return Other
	 */
	public static Other getOther() {
		return new Other();
	}

	public static void main(String args[]) {
		com.javahis.util.JavaHisDebug.initClient();
		com.dongyang.manager.TIOM_AppServer.resetAction();
		System.out.println(Manager.getPersonal().getLockParmString("1"));
	}
}
