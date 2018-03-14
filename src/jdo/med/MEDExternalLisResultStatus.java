package jdo.med;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Title: 外送检验结果状态对象
 * </p>
 * 
 * <p>
 * Description: 外送检验结果状态对象
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangb 2017.1.19
 * @version 1.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Error")
public class MEDExternalLisResultStatus {

	/**
	 * 编码
	 */
	private String Code;
	/**
	 * 提示信息
	 */
	private String Descript;
	/**
	 * 取得编码
	 * @return code
	 */
	public String getCode() {
		return Code;
	}
	/**
	 * 设定编码
	 * @param code 编码
	 */
	public void setCode(String code) {
		Code = code;
	}
	/**
	 * 取得提示信息
	 * @return descript
	 */
	public String getDescript() {
		return Descript;
	}
	/**
	 * 设定提示信息
	 * @param descript 提示信息
	 */
	public void setDescript(String descript) {
		Descript = descript;
	}
}
