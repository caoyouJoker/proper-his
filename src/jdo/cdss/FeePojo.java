package jdo.cdss;

public class FeePojo {
	
	private String id;
	
	private String orderCode;
	
	private String cavityNum;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the orderCode
	 */
	public String getOrderCode() {
		return orderCode;
	}

	/**
	 * @param orderCode the orderCode to set
	 */
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	/**
	 * @return the cavityNum
	 */
	public String getCavityNum() {
		return cavityNum;
	}

	/**
	 * @param cavityNum the cavityNum to set
	 */
	public void setCavityNum(String cavityNum) {
		this.cavityNum = cavityNum;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FeePojo [id=" + id + ", orderCode=" + orderCode
				+ ", cavityNum=" + cavityNum + "]";
	}

}
