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
	 * 与his同步处方信息，之后同步物联网信息
	 */
	public String onTransSpcData(String xml) {
		TParm result = new TParm() ;
		String returnxml = ""  ;
		TParm parm = XmlUtils.createHisxml001toParm(xml);
		if(parm.getCount("ORDER_CODE")<0){
			returnxml = this.messageReturn("处方信息为空", "0");
			return returnxml ;
		}
		 TSocket socket = new TSocket("127.0.0.1", 8080, "webgy");
	        result = TIOM_AppServer.executeAction(socket,
	                "action.spc.bsm.SPCBsmAction", "onInsert", parm);  
	        if(result.getErrCode()<0){
				returnxml = this.messageReturn(result.getErrName()+result.getErrText(), result.getValue("STATUS")) ;
				System.out.println("001XML=入======"+xml);
				 System.out.println("001返回xml===错误==="+returnxml);
				 return  returnxml ;
					}

	        returnxml = this.messageReturn(result.getValue("MESSAGE"), "2") ;
		return returnxml;
	}

	/**
	 * 通知盒装发药机发药
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
				System.out.println("xml==入==003====="+xml);
				 System.out.println("003返回xml===错误==="+returnxml);
				 return  returnxml ;
					}
	        returnxml = this.messageReturn(result.getValue("MESSAGE"), "1") ;
		return returnxml;
	}

	/**
	 * 发药结束
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
				System.out.println("xml=入===005===="+xml);
				 System.out.println("005返回xml===错误==="+returnxml);
				 return  returnxml ;
					}
	        returnxml = this.messageReturn(result.getValue("MESSAGE"), "1") ;
		return returnxml;
	}
	/**
	 * 返回信息
	 * @param status
	 * @return
	 */
	private String messageReturn(String message,String status) {
		String xml = XmlUtils.createSpcParmtoHisXml(message, status).toString() ;
		return xml ;
	}
	/**
	 * 住院包药机接口，数据来源
	 * 直插入主表ODI_DSPNM
	 */
	@Override
	public String onTransOdiSpcData(String xml) {
		TParm result = new TParm() ;
		String returnxml = ""  ;
		TParm parm = XmlUtils.createHisxml007toParm(xml);
		if(parm.getCount("ORDER_CODE")<0){
			returnxml = this.messageReturn("处方信息为空", "0");
			return returnxml ;
		}
		 TSocket socket = new TSocket("127.0.0.1", 8080, "webgy");
	        result = TIOM_AppServer.executeAction(socket,
	                "action.spc.bsm.SPCBsmAction", "insertOdiDspnm", parm);  
	        if(result.getErrCode()<0){
				returnxml = this.messageReturn(result.getErrName()+result.getErrText(), "0") ;
				System.out.println("007XML==入====="+xml);
				System.out.println("007XML==出====="+returnxml);
				 return  returnxml ;
					}
	        returnxml = this.messageReturn("成功", "1") ;
		return returnxml;
	}

	/**
	 * 二级库向一级库请领数据
	 */
	@Override
	public String onRequestSpcData(String xml) {
		TParm result = new TParm() ;
		String returnxml = ""  ;
		TParm parm = XmlUtils.createHisxml009toParm(xml);
		TParm main = parm.getParm("MAIN");
		if(main.getCount("REQUEST_NO")<0){
			returnxml = this.messageReturn("请领数据为空", "0");
			return returnxml ;
		}
		 TSocket socket = new TSocket("127.0.0.1", 8080, "webgy");
	        result = TIOM_AppServer.executeAction(socket,
	                "action.spc.bsm.SPCBsmAction", "insertRequest", parm);  
	        if(result.getErrCode()<0){
				returnxml = this.messageReturn(result.getErrName()+result.getErrText(), "0") ;
				System.out.println("009XML==入====="+xml);
				System.out.println("009XML==出====="+returnxml);
				 return  returnxml ;
					}
	        returnxml = this.messageReturn("成功", "1") ;
		return returnxml;
	}
}
