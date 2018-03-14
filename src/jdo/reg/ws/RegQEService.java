package jdo.reg.ws;

import javax.jws.WebService;

@WebService
public interface RegQEService {
	public String Process(String TransCode,String InXml );
	
	
	
//	public String getPatInfo(String cardNo,String cardType,String terminalCode);
//	
//	
//	public String getDrTodayRestCount(String inXml);
//	
//	public String regOrUnReg(String inXml);
//	
//	public String getPatRegPatadm(String inXml);
//	
//	public String regApp(String inXml);
	
//	public String unRegApp(String inXml);
}
