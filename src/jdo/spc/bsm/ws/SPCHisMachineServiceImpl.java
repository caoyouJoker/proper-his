package jdo.spc.bsm.ws;
import javax.jws.WebService;
import jdo.spc.bsm.ConsisServiceSoap_ConsisServiceSoap_Client;
import jdo.spc.bsm.bean.XmlUtils;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_AppServer;
@WebService
public class SPCHisMachineServiceImpl implements SPCHisMachineService{

	/**
	 * ��hisͬ��������Ϣ��֮��ͬ����������Ϣ
	 */
	public String onTransSpcData(String xml) {
		TParm result = new TParm() ;
		String returnxml = ""  ;
		TParm parm = XmlUtils.createHisxml001toParm(xml);
		if(parm.getCount("ORDER_CODE")<0){
			returnxml = this.messageReturn("������ϢΪ��", "0");
			return returnxml ;
		}
		 TSocket socket = new TSocket("127.0.0.1", 8080, "webgy");
	        result = TIOM_AppServer.executeAction(socket,
	                "action.spc.bsm.SPCBsmAction", "onInsert", parm);  
	        if(result.getErrCode()<0){
				returnxml = this.messageReturn(result.getErrName()+result.getErrText(), result.getValue("STATUS")) ;
				System.out.println("001XML=��======"+xml);
				 System.out.println("001����xml===����==="+returnxml);
				 return  returnxml ;
					}

	        returnxml = this.messageReturn(result.getValue("MESSAGE"), "2") ;
		return returnxml;
	}

	/**
	 * ֪ͨ��װ��ҩ����ҩ
	 */
	@SuppressWarnings("static-access")
	public String onPrepareSpcData(String xml) {
		TParm result = new TParm() ;
		String returnxml = ""  ;
		TParm parm = XmlUtils.createHisxml003And305toParm(xml);
		parm.setData("WAY", "202") ;
		parm.setData("BOX_TYPE", "0") ;
		parm.setData("BOXUPDATE_TYPE", "1") ;
		 TSocket socket = new TSocket("127.0.0.1", 8080, "webgy");
	        result = TIOM_AppServer.executeAction(socket,
	                "action.spc.bsm.SPCBsmAction", "onUpdate", parm);  
	        if(result.getErrCode()<0){
				returnxml = this.messageReturn(result.getErrName()+result.getErrText(), result.getValue("STATUS")) ;
				System.out.println("xml==��==003====="+xml);
				 System.out.println("003����xml===����==="+returnxml);
				 return  returnxml ;
					}
	        returnxml = this.messageReturn(result.getValue("MESSAGE"), "1") ;
		return returnxml;
	}

	/**
	 * ��ҩ����
	 */
	@SuppressWarnings("static-access")
	public String onSendSpcData(String xml) {
		TParm result = new TParm() ;
		String returnxml = ""  ;
		TParm parm = XmlUtils.createHisxml003And305toParm(xml);
		parm.setData("WAY", "203") ;
		parm.setData("BOX_TYPE", "1") ;
		parm.setData("BOXUPDATE_TYPE", "2") ;
		 TSocket socket = new TSocket("127.0.0.1", 8080, "webgy");
	        result = TIOM_AppServer.executeAction(socket,
	                "action.spc.bsm.SPCBsmAction", "onUpdate", parm);  
	        if(result.getErrCode()<0){
				returnxml = this.messageReturn(result.getErrName()+result.getErrText(), result.getValue("STATUS")) ;
				System.out.println("xml=��===005===="+xml);
				 System.out.println("005����xml===����==="+returnxml);
				 return  returnxml ;
					}
	        returnxml = this.messageReturn(result.getValue("MESSAGE"), "1") ;
		return returnxml;
	}
	/**
	 * ������Ϣ
	 * @param status
	 * @return
	 */
	private String messageReturn(String message,String status) {
		String xml = XmlUtils.createSpcParmtoHisXml(message, status).toString() ;
		return xml ;
	}
	/**
	 * סԺ��ҩ���ӿڣ�������Դ
	 * ֱ��������ODI_DSPNM
	 */
	@Override
	public String onTransOdiSpcData(String xml) {
		TParm result = new TParm() ;
		String returnxml = ""  ;
		TParm parm = XmlUtils.createHisxml007toParm(xml);
		if(parm.getCount("ORDER_CODE")<0){
			returnxml = this.messageReturn("������ϢΪ��", "0");
			return returnxml ;
		}
		 TSocket socket = new TSocket("127.0.0.1", 8080, "webgy");
	        result = TIOM_AppServer.executeAction(socket,
	                "action.spc.bsm.SPCBsmAction", "insertOdiDspnm", parm);  
	        if(result.getErrCode()<0){
				returnxml = this.messageReturn(result.getErrName()+result.getErrText(), "0") ;
				System.out.println("007XML==��====="+xml);
				System.out.println("007XML==��====="+returnxml);
				 return  returnxml ;
					}
	        returnxml = this.messageReturn("�ɹ�", "1") ;
		return returnxml;
	}

	/**
	 * ��������һ������������
	 */
	@Override
	public String onRequestSpcData(String xml) {
		TParm result = new TParm() ;
		String returnxml = ""  ;
		TParm parm = XmlUtils.createHisxml009toParm(xml);
		TParm main = parm.getParm("MAIN");
		if(main.getCount("REQUEST_NO")<0){
			returnxml = this.messageReturn("��������Ϊ��", "0");
			return returnxml ;
		}
		 TSocket socket = new TSocket("127.0.0.1", 8080, "webgy");
	        result = TIOM_AppServer.executeAction(socket,
	                "action.spc.bsm.SPCBsmAction", "insertRequest", parm);  
	        if(result.getErrCode()<0){
				returnxml = this.messageReturn(result.getErrName()+result.getErrText(), "0") ;
				System.out.println("009XML==��====="+xml);
				System.out.println("009XML==��====="+returnxml);
				 return  returnxml ;
					}
	        returnxml = this.messageReturn("�ɹ�", "1") ;
		return returnxml;
	}
}
