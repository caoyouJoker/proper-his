package jdo.spc.bsm.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* <p>Title: ͬ��his��������</p>
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
public class SPCHisDispens {
	private String DISPENSENO ; //���ⵥ��
	  @XmlElement(name = "DISPENSE_DTL")
	private List<SPCHisDispensDetail>  despensDetail ;

	public String getDISPENSENO() {
		return DISPENSENO;
	}

	public void setDISPENSENO(String dISPENSENO) {
		DISPENSENO = dISPENSENO;
	}

	public List<SPCHisDispensDetail> getDespensDetail() {
		if(despensDetail==null){
			despensDetail = new ArrayList<SPCHisDispensDetail>() ;
		}
		return despensDetail;
	}

	public void setDespensDetail(List<SPCHisDispensDetail> despensDetail) {
		if(despensDetail==null){
			despensDetail = new ArrayList<SPCHisDispensDetail>() ;
		}
		this.despensDetail = despensDetail;
	}

}
