package jdo.cdss;

public class OpPojo {
	
	private String id;
	
	private String code;
	
	private String mainFlg;
	
	private String isOpDept;

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
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the mainFlg
	 */
	public String getMainFlg() {
		return mainFlg;
	}

	/**
	 * @param mainFlg the mainFlg to set
	 */
	public void setMainFlg(String mainFlg) {
		this.mainFlg = mainFlg;
	}

	/**
	 * @return the isOpDept
	 */
	public String getIsOpDept() {
		return isOpDept;
	}

	/**
	 * @param isOpDept the isOpDept to set
	 */
	public void setIsOpDept(String isOpDept) {
		this.isOpDept = isOpDept;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OpPojo [id=" + id + ", code=" + code + ", mainFlg=" + mainFlg
				+ ", isOpDept=" + isOpDept + "]";
	}

}
