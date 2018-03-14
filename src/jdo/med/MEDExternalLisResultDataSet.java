package jdo.med;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Title: ���ͼ���������
 * </p>
 * 
 * <p>
 * Description: ���ͼ���������
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
@XmlRootElement(name = "ResultsDataSet")
public class MEDExternalLisResultDataSet {
	
	/**
	 * ���ͼ�������ϸ����
	 */
	@XmlElement(name = "Table")
	private List<MEDExternalLisResultDetail> lisResultDetail;

	/**
	 * ȡ�����ͼ�������ϸ����
	 * 
	 * @return lisResultDetail
	 */
	public List<MEDExternalLisResultDetail> getLisResultDetail() {
		return lisResultDetail;
	}

	/**
	 * �趨���ͼ�������ϸ����
	 * 
	 * @param lisResultDetail
	 *            ���ͼ�������ϸ����
	 */
	public void setLisResultDetail(List<MEDExternalLisResultDetail> lisResultDetail) {
		this.lisResultDetail = lisResultDetail;
	}
}
