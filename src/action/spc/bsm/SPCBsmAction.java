package action.spc.bsm;
import jdo.spc.bsm.ConsisServiceSoap_ConsisServiceSoap_Client;
import jdo.spc.bsm.SPCBsmTool;
import jdo.spc.bsm.bean.SPCOdiSendMachine;
import jdo.spc.bsm.bean.XmlUtils;
import jdo.spc.bsm.client.Service;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;


/**
 * <p>Title:��װ��ҩ����̨������
 *
 * <p>Description: 
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx 
 * @version 4.0
 */
public class SPCBsmAction extends TAction{
	
	/**
	 * �����װ��ҩ��001
	 * @param parm
	 * @return
	 */
	@SuppressWarnings("static-access")
	public TParm  onInsert(TParm parm){
		  TConnection connection = getConnection();
		  TParm rxNoParm = new TParm() ;
		  rxNoParm.setData("CASE_NO", parm.getValue("CASE_NO", 0));
		  TParm result = new TParm();
		  TParm outParm = new TParm() ;
		  result =   SPCBsmTool.getInstance().deleteData(rxNoParm, connection);
	        if (result.getErrCode() < 0) {
	        	result.setData("STATUS", "0") ;
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
	      result =   SPCBsmTool.getInstance().insertData(parm, connection);
	        if (result.getErrCode() < 0) {
	        	result.setData("STATUS", "0") ;
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
	        connection.commit();
	        connection.close();
	        TParm data = SPCBsmTool.getInstance().query(rxNoParm) ;
	        if(data.getCount()<=0){
	        	result.setData("STATUS", "0") ;
	        	result.setErrCode(-1);
	        	result.setErrName("������������") ;
	        	return result ;
	        }
	        TParm rxNoData = this.getRxNoData(parm) ;
	        try {
	        	 String inxml =XmlUtils.onCreate201XmlDispense(data,rxNoData).toString() ;
	 			ConsisServiceSoap_ConsisServiceSoap_Client client =  new ConsisServiceSoap_ConsisServiceSoap_Client()  ;
	 			String outxml =client.onTransConsisData(inxml) ;  //  ���ص�xml
	 			if(outxml.equals("err")){
	 				result.setErrCode(-1);
					result.setData("STATUS", "1") ;
					result.setErrName("webservices ���Ӵ���") ;
					return  result;
				}
	 			 outParm =	XmlUtils.createXmltoParm(outxml) ;
		 		if(outParm.getErrCode()!=1){
		 			result.setErrCode(-1);
					result.setData("STATUS", "1") ;
					result.setErrName("����==="+outParm.getValue("MESSAGE")) ;
		 			return  result;
		 		}
			} catch (Exception e) {
				System.out.println("������Ϣ======"+e.toString());
				result.setErrCode(-1);
				result.setData("STATUS", "1") ;
				result.setErrName("webservices ���Ӵ���") ;
				return  result;
			}
			result.setData("MESSAGE", outParm.getValue("MESSAGE")) ;
	        return result;
	}

	/**
	 * ���£���ҩ״̬ 003 /005
	 * @param parm
	 * @return
	 */
	@SuppressWarnings("static-access")
	public TParm  onUpdate(TParm parm){
		  TConnection connection = getConnection();
		  TParm result = new TParm();
		  TParm outParm = new TParm() ;
		  TParm selectParm = new TParm() ;
		  selectParm =   SPCBsmTool.getInstance().selectOpdOrderFlg(parm);
		  if(selectParm.getCount()<=0){
			  result.setData("STATUS", "1") ;
	        	result.setData("MESSAGE", "�ɹ����ظ�����") ;
	        	connection.close() ;
	        	return result ;
		  }
	        for(int i=0;i<selectParm.getCount();i++){
	        	selectParm.setData("WAY", i, parm.getData("WAY")) ;
	        	selectParm.setData("DATE", i, parm.getData("DATE")) ;
	        	selectParm.setData("OPWINID", i, parm.getData("OPWINID")) ;
	        }
	       try {
	  		 String inxml =XmlUtils.onCreate202And203XmlSend(selectParm).toString() ;
	  	
	  			ConsisServiceSoap_ConsisServiceSoap_Client client =  new ConsisServiceSoap_ConsisServiceSoap_Client()  ;
	  			String outxml =client.onTransConsisData(inxml) ;  //  ���ص�xml
	  			if(outxml.equals("err")){
	  				 System.out.println("202����xml==��==err===="+inxml);
	 				result.setErrCode(-1);
					result.setData("STATUS", "0") ;
					result.setErrName("webservices ���Ӵ���") ;
					connection.close() ;
					return  result;
				}
	  			outParm =	XmlUtils.createXmltoParm(outxml) ;
		 		if(outParm.getErrCode()!=1){
		 			 System.out.println("202����xml==��======"+inxml);
		 			result.setErrCode(-1);
					result.setData("STATUS", "0") ;
					result.setErrName("����==="+outParm.getValue("MESSAGE")) ;
					connection.close() ;
		 			return  result;
		 		}
		} catch (Exception e) {
			System.out.println("message===="+e.toString());
			result.setErrCode(-1);
			result.setData("STATUS", "0") ;
			result.setErrName("webservices ���Ӵ���") ;
			connection.close() ;
			return result ;
		}
		  result =   SPCBsmTool.getInstance().updateData(parm,connection);
	        if (result.getErrCode() < 0) {
	        	result.setData("STATUS", "0") ;
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
	        connection.commit();
	        connection.close();
	        return result;
	}
	/**
	 * ͬ��������Ϣ��his
	 * @param parm
	 * @return
	 */
	@SuppressWarnings("static-access")
	public TParm  onDispenseOut(TParm parm){ 
		TParm parmM = SPCBsmTool.getInstance().getDispenseM(parm) ;
		TParm parmD = SPCBsmTool.getInstance().getDispenseD(parm) ;
		  TParm result = new TParm();
		  if(parmM.getCount()<=0 || parmD.getCount()<=0){
			  result.setErrCode(-1) ;
				return result ; 	
			}
		  for(int i=0;i<parmD.getCount();i++){
			  parmD.setData("MAN_CODE", i, SPCBsmTool.getInstance().getManCode(parmD.getValue("ORDER_CODE", i)));
			  if(parmD.getValue("SUP_CODE", i).equals("00076"))
				  parmD.setData("REPLIBFLAG", i, 1) ;
			  else   parmD.setData("REPLIBFLAG", i, 0) ;
				  
		  }
		  String xml = ""  ;
	  try {
		 xml = XmlUtils.onCreate010XmlSend(parmD, parmM);
		 String outXml= new Service().getServiceSoap().drugToHisAccount(xml);
		 System.out.println("outxml====="+outXml);
		result = XmlUtils.createHisXmlToSpcParm(outXml) ;
	} catch (Exception e) {
		System.out.println("xml======="+xml);
		System.out.println("err====="+e.toString());
		result.setErrCode(-1) ;
		return result ;
	}
	        if (result.getErrCode() < 0) {
	        	System.out.println("xml======="+xml);
	          System.out.println("������Ϣ====="+result.getValue("MESSAGE"));
	            return result;
	        }
	        if (!result.getValue("STATUS").equals("1")) {
	        	System.out.println("xml======="+xml);
		          System.out.println("������Ϣ====="+result.getValue("MESSAGE"));
		          result.setErrCode(-1) ;
		            return result;
		        }
	        TConnection connection = getConnection();
	        result = SPCBsmTool.getInstance().updateDispense(parmM, connection);
	        if(result.getErrCode()<0){
	        	connection.rollback();
	        	connection.close();
	        	return result ;
	        }
	        connection.commit();
	        connection.close();
	        return result;
	}
	/**
	 * 007��סԺ��ҩ���ӿ�
	 * @param parm
	 * @return
	 */
	public TParm  insertOdiDspnm(TParm parm){
		  TConnection connection = getConnection();
		  TParm  result = new TParm() ;
		  TParm rxNoParm = new TParm() ;
		  rxNoParm.setData("CASE_NO", parm.getValue("CASE_NO", 0));
		  rxNoParm.setData("ORDER_NO", parm.getValue("ORDER_NO", 0));
		  result =   SPCBsmTool.getInstance().deleteOdiDspnm(rxNoParm, connection);	  
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
		  result =   SPCBsmTool.getInstance().insertOdiDspnm(parm, connection);	  
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
	        connection.commit();
	        connection.close();
	        TParm data = SPCBsmTool.getInstance().selectOdiDspnm(rxNoParm) ;
	        if(data.getCount()<=0){
	        	result.setErrCode(-1);
	        	result.setErrName("������������") ;
	        	return result ;
	        }
	        try {
	        	SPCOdiSendMachine.createTxt(data) ;
			} catch (Exception e) {
				System.out.println("������Ϣ======"+e.toString());
				result.setErrCode(-1);
	        	result.setErrName("������������") ;
	        	return result ;
			}
	        return result;
	}
	/**
	 * 009��������Ϣ�ӿ�
	 * @param parm
	 * ��ɾ�ӱ���ɾ����
	 * �Ȳ��������ڲ���ϸ��
	 * @return
	 */
	public TParm  insertRequest(TParm parm){
		  TConnection connection = getConnection();
		  TParm  result = new TParm() ;
		  TParm main = parm.getParm("MAIN") ;
		  main.setCount(main.getCount("REQUEST_NO")) ;
		  TParm detail = parm.getParm("DETAIL") ;
		  detail.setCount(detail.getCount("REQUEST_NO")) ;
		  result =   SPCBsmTool.getInstance().deleteRequestD(main, connection);  
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
		  result =   SPCBsmTool.getInstance().deleteRequestM(main, connection);	  
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
	        result =   SPCBsmTool.getInstance().insertRequestM(main, connection);	  
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
	        result =   SPCBsmTool.getInstance().insertRequestD(detail, connection);	  
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
	        connection.commit();
	        connection.close();
	        return result;
	}
	/**
	 *011��ͬ��ҩƷ�ֵ�his�ӿ�
	 * @param parm
	 * @return
	 */
	public TParm  SpcHisPhaBase(TParm parm){
		  TConnection connection = getConnection();
		  TParm  result = new TParm() ;
		  result =   SPCBsmTool.getInstance().deletePhaBase(parm, connection);	  
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
		  result =   SPCBsmTool.getInstance().insertPhaBase(parm, connection);	  
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
	        result =   SPCBsmTool.getInstance().deleteSysfee(parm, connection);	  
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
		  result =   SPCBsmTool.getInstance().insertSysfee(parm, connection);	  
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
	        result =   SPCBsmTool.getInstance().deletePhaTransUnit(parm, connection);	  
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
		  result =   SPCBsmTool.getInstance().insertPhaTransUnit(parm, connection);	  
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
	        connection.commit();
	        connection.close();
	        return result;
	}
	/**
	 *012��ͬ�������ֵ�his�ӿ�
	 * @param parm
	 * @return
	 */
	public TParm  SpcHisSysDept(TParm parm){
		  TConnection connection = getConnection();
		  TParm  result = new TParm() ;
		  result =   SPCBsmTool.getInstance().deleteSysDept(parm, connection);	  
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
		  result =   SPCBsmTool.getInstance().insertSysDept(parm, connection);	  
	        if (result.getErrCode() < 0) {
	            err(result.getErrName() + " " + result.getErrText());
	            connection.rollback() ;
	            connection.close();
	            return result;
	        }
	        connection.commit();
	        connection.close();
	        return result;
	}
	/**
	 * ��ȡһ�����������д�����
	 * @param parm
	 * @return
	 */
	public TParm getRxNoData(TParm parm){
		int count = parm.getCount("ORDER_CODE") ;
		for(int i=0;i<parm.getCount("ORDER_CODE");i++){
			for(int j=i+1;j<parm.getCount("ORDER_CODE");j++){
				if(parm.getValue("RX_NO", i).equals(parm.getValue("RX_NO", j))){
					  parm.removeRow(j); 
					    j-- ;
					    count--; 
				}
			}
		}
		parm.setCount(parm.getCount("ORDER_CODE")) ;
		return parm  ;
	}
}
