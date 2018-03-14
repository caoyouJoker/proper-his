package jdo.med;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * Title: 外送检验结果对象
 * </p>
 * 
 * <p>
 * Description: 外送检验结果对象
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
	 * 外送检验结果明细对象
	 */
	@XmlElement(name = "Table")
	private List<MEDExternalLisResultDetail> lisResultDetail;

	/**
	 * 取得外送检验结果明细对象
	 * 
	 * @return lisResultDetail
	 */
	public List<MEDExternalLisResultDetail> getLisResultDetail() {
		return lisResultDetail;
	}

	/**
	 * 设定外送检验结果明细对象
	 * 
	 * @param lisResultDetail
	 *            外送检验结果明细对象
	 */
	public void setLisResultDetail(List<MEDExternalLisResultDetail> lisResultDetail) {
		this.lisResultDetail = lisResultDetail;
	}
}
