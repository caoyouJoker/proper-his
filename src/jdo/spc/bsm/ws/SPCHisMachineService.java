package jdo.spc.bsm.ws;

import javax.jws.WebService;

/**
 * <p>Title: ¹©hisºô½Ð½Ó¿Ú001¡¢003¡¢005</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS</p>
 *
 * <p>Company:  </p>
 *
 * @author chenx  2013-09-10
 * @version 4.0
 */
@WebService
public interface SPCHisMachineService {

	//001
	public String onTransSpcData(String xml);
	//003
	public String onPrepareSpcData(String xml);
	//005
	public String onSendSpcData(String xml);
	//007
	public String onTransOdiSpcData(String xml);
	//009 
	public String onRequestSpcData(String xml);
}
