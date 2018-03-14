package jdo.spc.bsm.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.xfire.aegis.type.java5.XmlAttribute;

/**
*
* <p>Title: 同步his出库数据</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.10.25
* @version 4.0
*/
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlRootElement(name = "ROOT") 
public class SPCHisDispensDetail {
	private String DISPENSENO ; //出库单号

	public String getDISPENSENO() {
		return DISPENSENO;
	}

	public void setDISPENSENO(String dISPENSENO) {
		DISPENSENO = dISPENSENO;
	}

}
