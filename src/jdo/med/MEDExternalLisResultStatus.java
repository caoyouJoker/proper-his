package jdo.med;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Title: ���ͼ�����״̬����
 * </p>
 * 
 * <p>
 * Description: ���ͼ�����״̬����
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
	 * ����
	 */
	private String Code;
	/**
	 * ��ʾ��Ϣ
	 */
	private String Descript;
	/**
	 * ȡ�ñ���
	 * @return code
	 */
	public String getCode() {
		return Code;
	}
	/**
	 * �趨����
	 * @param code ����
	 */
	public void setCode(String code) {
		Code = code;
	}
	/**
	 * ȡ����ʾ��Ϣ
	 * @return descript
	 */
	public String getDescript() {
		return Descript;
	}
	/**
	 * �趨��ʾ��Ϣ
	 * @param descript ��ʾ��Ϣ
	 */
	public void setDescript(String descript) {
		Descript = descript;
	}
}
