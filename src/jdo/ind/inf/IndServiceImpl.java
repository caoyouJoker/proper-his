package jdo.ind.inf;

import javax.jws.WebService;

@WebService
public class IndServiceImpl implements IndService {

	/**
	 * public String onSaveDispenseDM(IndDispensem indDispensem){ return
	 * IndDispensDaoImpl.getInstance().onSave(indDispensem); }
	 */

	/**
	 * public String onStop(IndDispensem indDispensem) { // TODO Auto-generated
	 * method stub return null; }
	 */

	public String onSaveDispense(String xml) {
		System.out.println("物联网传入XML-------------:"+xml);
		return  IndDispensDaoImpl.getInstance().onSave(xml);
	}

}
