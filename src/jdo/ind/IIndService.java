package jdo.ind;

import javax.jws.WebService;

@WebService
public interface IIndService {

	/**
	 * ±£¥ÊDispensenM,D ±Ì
	 * @param indDispensem
	 * @return
	 */
	public String onSaveDispenseDM(IndDispensem indDispensem);
	
}
