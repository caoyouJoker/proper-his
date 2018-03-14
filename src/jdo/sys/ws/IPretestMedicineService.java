package jdo.sys.ws;

import javax.jws.WebService;

/**
 * <p>Title: 供盒装包药机呼叫接口301、305</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS</p>
 *
 * <p>Company:  </p>
 *
 * @author chenx  2013-05-22
 * @version 4.0
 */
@WebService
public interface IPretestMedicineService {

	public String onPrepareFinish(String xml);
	public String onRequestBox(String xml);
}
