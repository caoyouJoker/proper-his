package action.spc.services.dto;

import java.io.Serializable;

public class SpcInwCheckDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * ҽ������
	 */
	private String orderCode ;
	
	/**
	 * ���
	 */
	private double qty ;
	
	public SpcInwCheckDto(){
		
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public double getQty() {
		return qty;
	}

	public void setQty(double qty) {
		this.qty = qty;
	}
	
	
}
