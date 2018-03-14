package jdo.opd;

import com.dongyang.data.TModifiedData;
/**
*
* <p>
* Title: 处方
* </p>
*
* <p>
* Description:处方
* </p>
*
* <p>
* Copyright: Copyright (c) 2008
* </p>
*
* <p>
* Company:Javahis
* </p>
*
* @author ehui 200800911
* @version 1.0
*/
public class Prescription extends TModifiedData {

	/**
	 * 医嘱
	 */
	private OrderList orderlist;
	/**
	 * 设置医嘱
	 * @param order
	 */
	public void setOrderList(OrderList orderlist){
		this.orderlist=orderlist;
	}
	/**
	 * 得到医嘱
	 * @return
	 */
	public OrderList getOrderList(){
		return orderlist;
	}
	
}
