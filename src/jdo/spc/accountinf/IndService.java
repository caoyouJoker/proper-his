package jdo.spc.accountinf;

import javax.jws.WebService;

@WebService
public interface IndService {

	
	
	/**
	 * 药库出库保存DispensenM,D 表
	 * @param indDispensem
	 * @return
	 */
	/**
	public String onSaveDispenseDM(IndDispensem indDispensem);
	*/
	/**
	 * 药库终止单据
	 * @param indDispensem
	 * @return
	 */
	//public String onStop(IndDispensem indDispensem);
	

	public String onSaveDispense(String xml);
	
}
