package jdo.spc.bsm.bean;
/**
*
* <p>Title: xml������</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.05.14 
* @version 4.0
*/
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.bind.JAXB;

import jdo.spc.bsm.SPCBsmTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
public class XmlUtils extends TJDOTool{
	/**
	 * ����ʵ��
	 */
  public static XmlUtils instanceObject ;
    /**
     * �õ�ʵ��
     * @return
     */
  public static XmlUtils getInstance(){
	  if(instanceObject==null)
		  instanceObject = new XmlUtils() ;
	  return instanceObject ;
  }
  
	/**
	 * ������ʵ�֣�����������ת��Ϊxml������202��203����ʱʹ��(��ҩ)
	 * @return 
	 */
	public static  StringWriter  onCreateXmlSend(TParm parm){
		Prescription prescription = new Prescription() ; //��������
		//���ñ�ͷ
		 prescription.setOPIP(((TParm)parm.getData("Data", "TITLE")).getValue("OPT_TERM")) ;      //����ip
		 prescription.setOPMANNAME(((TParm)parm.getData("Data", "TITLE")).getValue("OPT_USER")) ; //����name
		 prescription.setOPMANNO(((TParm)parm.getData("Data", "TITLE")).getValue("OPT_CODE")) ;  //������Ա����
		 prescription.setOPTYPE(((TParm)parm.getData("Data", "TITLE")).getValue("WAY")) ;        //��������
		 prescription.setOPWINID(((TParm)parm.getData("Data", "TITLE")).getValue("OPWINID")) ;   //�����ն˴��ں�
		 //���ô�������
		 List<PrescriptionTableMain> listMain  = prescription.getMain() ;
		 PrescriptionTableMain main = null ;
			 main = new PrescriptionTableMain() ;
			 String date = ((TParm)parm.getData("Data", "MAIN")).getValue("PHA_DOSAGE_DATE") ;
			 if(date.length()>0)
			 main.setPRESC_DATE(date.substring(0, 19)) ;//������ҩ���ʱ��
			 else  main.setPRESC_DATE("") ;//������ҩ���ʱ��
			 main.setPRESC_NO(((TParm)parm.getData("Data", "MAIN")).getValue("RX_NO")) ;//�������
			 listMain.add(main) ;
			   StringWriter xml = new StringWriter() ;
			   try {
				   JAXB.marshal(prescription, xml); 
				   return xml ;
			} catch (Exception e) {
				System.out.println("err======"+e.toString());
			}
	            
	            return xml ;	        
	}
	/**
	 * ����xml����xmlת��Ϊ����
	 * @return
	 */
	public static TParm createXmltoParm(String returnString){
		TParm  result = new  TParm() ;
		try {
			StringReader read = new StringReader(returnString) ;
			ReturnBean returnValue = JAXB.unmarshal(read, ReturnBean.class) ;
			result.setErrCode(returnValue.getRETCODE()) ;
			result.setData("RETVAL", returnValue.getRETVAL()) ;
			result.setData("MESSAGE", returnValue.getRETMSG()) ;
		} catch (Exception e) {
			System.out.println("err======"+e.toString());
		}	
		
		return  result ;
	}
	/**
	 * ����xml����xmlת��Ϊ����301������������������xml
	 * @return
	 */
	public static TParm createSendxmltoParm(String sendString){
		TParm  result = new  TParm() ;
		try {
			StringReader read = new StringReader(sendString) ;
			Prescription sendValue = JAXB.unmarshal(read, Prescription.class) ;
			result.setData("PRESC_NO", sendValue.getReturn().get(0).getPRESC_NO()) ;
			result.setData("LOCATION", sendValue.getReturn().get(0).getLOCATION()) ;
		} catch (Exception e) {
			System.out.println("err======"+e.toString());
		}	
		
		return  result ;
	}
	/**
	 * ����xml����xmlת��Ϊ����305������������������xml,���쵥
	 * @return
	 */
	public static TParm createSendxml305toParm(String sendString){
//		System.out.println("send=============="+sendString);
		TParm  result = new  TParm() ;
		try {
			StringReader read = new StringReader(sendString) ;
			Prescription sendValue = JAXB.unmarshal(read, Prescription.class) ;
			for(int i=0;i<sendValue.getRequest().size();i++){
				result.addData("ORDER_CODE", sendValue.getRequest().get(i).getBARCODE()) ;
				result.addData("APPQUANTITY", sendValue.getRequest().get(i).getAPPQUANTITY()) ;
				result.addData("CURQUANTITY", sendValue.getRequest().get(i).getCURQUANTITY()) ;
			}
		
		} catch (Exception e) {
			System.out.println("err======"+e.toString());
		}	
		
		return  result ;
	}
	/**
	 * �������󣬽�����ת��Ϊxml,����ר��
	 * @return
	 */
	public static StringWriter createSendParmtoXml(){
			SendBean bean = new SendBean() ;
			bean.setPRESC_NO("1305290002") ;
			bean.setLOCATION("9") ;
			 StringWriter xml = new StringWriter() ;
			 JAXB.marshal(bean, xml); 
			   return xml ;
	}
	/**
	 * �������󣬽�����ת��Ϊxml,301�������������ص�xml
	 * @return
	 */
	public static StringWriter createParmtoXml(String message,int status){
			ReturnBean bean = new ReturnBean() ;
			bean.setRETCODE(status);
			bean.setRETMSG(message);
			bean.setRETVAL(0+"") ;
			 StringWriter xml = new StringWriter() ;
			 JAXB.marshal(bean, xml); 
			   return xml ;
	}
	/**
	 * �������͵�ת�� 0��7������Ƽ�
          1������ҩ
          2������ҩƷ
          3����ҩ��Ƭ
          4��������Ŀ
          5����������Ŀ
	 */

	@SuppressWarnings("unchecked")
	public static  String onRxtype(String code){
		String typeDesc = "" ;
		Map<String, String>  map = new HashMap<String, String>();
		map.put("0", "����Ƽ�") ;
		map.put("7", "����Ƽ�") ;
		map.put("1", "����ҩ") ;
		map.put("2", "����ҩƷ") ;
		map.put("3", "��ҩ��Ƭ") ;
		map.put("4", "������Ŀ") ;
		map.put("5", "��������Ŀ") ;
		Iterator  ite = map.entrySet().iterator();
		  while(ite.hasNext()){
			  Map.Entry entry = (Entry) ite.next() ;
			  if(entry.getKey().equals(code)){
				  typeDesc =entry.getValue().toString() ;
			  }
		  }
		return typeDesc ;
	}
	/**
	 * ͬ���ֵ�()ҩƷ��sys_fee ����101����
	 * @param parm
	 * @return
	 */
	public static StringWriter onSysFeeDictionary(TParm parm){
		StringWriter xml = new StringWriter() ;
		Prescription prescription = new Prescription() ; //��������
		//���ñ�ͷ
		 prescription.setOPIP(parm.getValue("OPT_TERM")) ;      //����ip
		 prescription.setOPMANNAME(parm.getValue("OPT_USER")) ; //����name
		 prescription.setOPMANNO(parm.getValue("OPT_CODE")) ;  //������Ա����
		 prescription.setOPTYPE(parm.getValue("WAY")) ;        //��������
		 prescription.setOPWINID(parm.getValue("OPWINID")) ;   //�����ն˴��ں�
		 //�����ֵ���ϸ
		 List<DictionarySysFee> listSysFee = prescription.getSysFee() ;
		 DictionarySysFee sysFee = null ;
		 for(int i=0;i<parm.getCount();i++){
			 sysFee = new DictionarySysFee() ;
			 sysFee.setDRUG_CODE(parm.getValue("ORDER_CODE", i)); //HISҩƷ���
			 sysFee.setDRUG_NAME(parm.getValue("ORDER_DESC", i)) ;//ҩƷ����
			 sysFee.setTRADE_NAME(parm.getValue("GOODS_DESC", i));//ҩƷ��Ʒ��
			 sysFee.setDRUG_SPEC(parm.getValue("SPECIFICATION", i));//ҩƷ���
			 sysFee.setDRUG_PACKAGE(parm.getValue("SPECIFICATION", i));//ҩƷ��װ���
			 sysFee.setDRUG_UNIT(parm.getValue("UNIT_CHN_DESC", i)) ;//ҩƷ��λ
			 sysFee.setFIRM_ID(parm.getValue("MAN_CHN_DESC", i)) ;//ҩƷ����
			 sysFee.setDRUG_PRICE(parm.getDouble("OWN_PRICE", i)+"");//ҩƷ�۸�
			 sysFee.setDRUG_FORM(parm.getValue("CHN_DESC", i)) ;//ҩƷ����
			 sysFee.setDRUG_SORT(parm.getValue("CTRLDRUGCLASS_CHN_DESC", i)) ;//ҩƷ����
			 sysFee.setBARCODE(parm.getValue("BAR_CODE", i));//ҩƷ������????????????????????????
			 sysFee.setLAST_DATE(parm.getValue("OPT_DATE", i)) ;//������ʱ��
			 sysFee.setPINYIN(parm.getValue("PY1", i)) ;//ҩƷƴ��
			 sysFee.setDRUG_CONVERTATION(parm.getValue("DOSAGE_QTY",i));//������?????????????????????????????????
			 sysFee.setALLOWIND(parm.getValue("ACTIVE_FLG", i)) ;//�Ƿ����� 
			 listSysFee.add(sysFee) ;
		 }
		 try {
			JAXB.marshal(prescription, xml) ;
			
//			
			return xml ;
		} catch (Exception e) {
		    System.out.println("������Ϣ=========="+e.toString());
		}
		
		return xml ;
	}
	
	/**
	 * ͬ���ֵ�()ҩƷ��� ����102����
	 * @param parm
	 * @return
	 */
	public static StringWriter onStockDictionary(TParm parm){
		StringWriter xml = new StringWriter() ;
		Prescription prescription = new Prescription() ; //��������
		//���ñ�ͷ
		 prescription.setOPIP(parm.getValue("OPT_TERM")) ;      //����ip
		 prescription.setOPMANNAME(parm.getValue("OPT_USER")) ; //����name
		 prescription.setOPMANNO(parm.getValue("OPT_CODE")) ;  //������Ա����
		 prescription.setOPTYPE(parm.getValue("WAY")) ;        //��������
		 prescription.setOPWINID(parm.getValue("OPWINID")) ;   //�����ն˴��ں�
		 //�����ֵ���ϸ
		 List<DictionaryStock> listStock = prescription.getStock();
		 DictionaryStock stock = null ;
		 for(int i=0;i<parm.getCount();i++){
			 stock = new DictionaryStock() ;
			 stock.setDISPENSARY("�ż���ҩ��") ;//��ҩҩ�ִ���
			 stock.setDRUG_CODE(parm.getValue("ORDER_CODE", i)) ;//ҩƷ���
			 stock.setDRUG_QUANTITY("0") ;//ҩƷ����
			 stock.setLOCATIONINFO(parm.getValue("MATERIAL_LOC_CODE", i)) ;//ҩƷ��λ
			 listStock.add(stock) ;
		
		 }
		 try {
			JAXB.marshal(prescription, xml) ;
			return xml ;
		} catch (Exception e) {
		    System.out.println("������Ϣ=========="+e.toString());
		}
		
		return xml ;
	}
	/**
	 * ͬ���ֵ�()�����ֵ� ����104����
	 * @param parm
	 * @return
	 */
	public static StringWriter onDeptDictionary(TParm parm){
		StringWriter xml = new StringWriter() ;
		Prescription prescription = new Prescription() ; //��������
		//���ñ�ͷ
		 prescription.setOPIP(parm.getValue("OPT_TERM")) ;      //����ip
		 prescription.setOPMANNAME(parm.getValue("OPT_USER")) ; //����name
		 prescription.setOPMANNO(parm.getValue("OPT_CODE")) ;  //������Ա����
		 prescription.setOPTYPE(parm.getValue("WAY")) ;        //��������
		 prescription.setOPWINID(parm.getValue("OPWINID")) ;   //�����ն˴��ں�
		 //�����ֵ���ϸ
		 List<DictionaryDept> listDept = prescription.getDept();
		 DictionaryDept dept = null ;
		 for(int i=0;i<parm.getCount();i++){
			 dept = new DictionaryDept() ;
			dept.setDEPT_CODE(parm.getValue("DEPT_CODE", i)) ;
			dept.setDEPT_NAME(parm.getValue("DEPT_CHN_DESC", i)) ;
			 listDept.add(dept) ;
		
		 }
		 try {
			JAXB.marshal(prescription, xml) ;
			return xml ;
		} catch (Exception e) {
		    System.out.println("������Ϣ=========="+e.toString());
		}
		
		return xml ;
	}
	/**
	 * ����������Ϣ��his �Ĵ�����Ϣ 001����
	 * @return
	 */
	public static TParm createHisxml001toParm(String sendString){
		TParm  result = new  TParm() ;
		try {
			StringReader read = new StringReader(sendString) ;
			SPCHisPrescription sendValue = JAXB.unmarshal(read, SPCHisPrescription.class) ;
			for(int i=0;i<sendValue.getOrderDetail().size();i++){
				//  ====ҽ��ϸ��
				result.setData("SEQ_NO", i, sendValue.getOrderDetail().get(i).getSeqNo()) ;//�Ƿ�ɷ�
				result.setData("BILL_FLG", i, sendValue.getOrderDetail().get(i).getBillFlg()) ;//�Ƿ�ɷ�
				result.setData("CTRL_FLG", i, sendValue.getOrderDetail().get(i).getCtrlFlg()) ;//�Ƿ��龫ҩƷ
				result.setData("DISPENSE_QTY", i, sendValue.getOrderDetail().get(i).getDispenseQty()) ;//��ҩ����
				result.setData("DISPENSE_UNIT", i, sendValue.getOrderDetail().get(i).getDispenseUnit()) ;
				result.setData("DOSAGE_QTY", i, sendValue.getOrderDetail().get(i).getDosageQty()) ;//��ҩ����
				result.setData("DOSAGE_UNIT", i, sendValue.getOrderDetail().get(i).getDosageUnit()) ;
				result.setData("FREQ_CODE", i, sendValue.getOrderDetail().get(i).getFreqCode()) ;//Ƶ��
				result.setData("GIVEBOX_FLG", i, sendValue.getOrderDetail().get(i).getGiveBoxFlg()) ;//�̶�ΪY
				result.setData("GOODS_DESC", i, sendValue.getOrderDetail().get(i).getGoodsDesc()) ;
				result.setData("MEDI_QTY", i, sendValue.getOrderDetail().get(i).getMediQty()) ;//��ҩ����
				result.setData("MEDI_UNIT", i, sendValue.getOrderDetail().get(i).getMediUnit()) ;
				result.setData("ORDER_CAT1_CODE", i, sendValue.getOrderDetail().get(i).getOrderCatCode()) ;//ҽ������
				result.setData("ORDER_CODE", i, sendValue.getOrderDetail().get(i).getOrderCode()) ;
				result.setData("ORDER_DESC", i, sendValue.getOrderDetail().get(i).getOrderDesc()) ;
				result.setData("OWN_AMT", i, sendValue.getOrderDetail().get(i).getOwnAmt()) ;//���
				result.setData("OWN_PRICE", i, sendValue.getOrderDetail().get(i).getOwnPrice()) ;//����
				result.setData("PHA_TYPE", i, sendValue.getOrderDetail().get(i).getPhaType()) ;//ҩƷ����
				result.setData("ROUTE_CODE", i, sendValue.getOrderDetail().get(i).getRouteCode()) ;//�÷�����
				result.setData("SPECIFICATION", i, sendValue.getOrderDetail().get(i).getSpecification()) ;//���
				result.setData("TAKE_DAYS", i, sendValue.getOrderDetail().get(i).getTakeDays()) ;//����
				result.setData("RX_NO", i, sendValue.getOrderDetail().get(i).getRxNo()) ;
				result.setData("ORDER_DATE", i, sendValue.getOrderDetail().get(i).getOrderDate()) ;
				//ҽ������
				result.setData("CASE_NO", i, sendValue.getCaseNo()) ;
				result.setData("BOX_TYPE", i, "0") ;//��ҩ״̬
				result.setData("MR_NO", i, sendValue.getMrNo()) ;
				result.setData("ADM_TYPE", i, sendValue.getAdmType()) ;
				result.setData("EXEC_DEPT_CODE", i, sendValue.getExecDeptCode()) ;
				result.setData("DC_DEPT_CODE", i, sendValue.getDeptCode()) ;
				result.setData("PAT_NAME", i, sendValue.getPatName()) ;
				result.setData("SEX_TYPE", i, sendValue.getPatSex()) ;
				result.setData("BIRTH_DATE", i, sendValue.getBirthDate()) ;
				result.setData("DR_CODE", i, sendValue.getDrName()) ;
				result.setData("COUNTER_NO", i, sendValue.getOpWinId()) ;
				result.setData("OPT_USER", i, sendValue.getOptUser()) ;
				result.setData("OPT_TERM", i, sendValue.getOptTerm()) ;
				
			}
		
		} catch (Exception e) {
			System.out.println("err======"+e.toString());
		}	
		return  result ;
	}
	/**
	 * �������󣬽�����ת��Ϊxml,��������his��
	 * ��������his���еĹ̶���ʽ
	 * @return
	 */
	public static StringWriter createSpcParmtoHisXml(String message,String status){
			ReturnHisBean bean = new ReturnHisBean() ;
			bean.setRTMSG(message) ;
			bean.setRTSTATUS(status);
			 StringWriter xml = new StringWriter() ;
			 JAXB.marshal(bean, xml); 
			   return xml ;
	}
	/**
	 * ����xml����xmlת��Ϊ����
	 * his���ظ���������xml��ʽ��
	 * @return
	 */
	public static TParm createHisXmlToSpcParm(String sendString){
		TParm  result = new  TParm() ;
		try {
			StringReader read = new StringReader(sendString) ;
			ReturnHisBean sendValue = JAXB.unmarshal(read, ReturnHisBean.class) ;	
				result.setData("MESSAGE", sendValue.getRTMSG()) ;
				result.setData("STATUS", sendValue.getRTSTATUS()) ;		
		} catch (Exception e) {
			System.out.println("err======"+e.toString());
			result.setErrCode(-1) ;
			return result ;
		}	
		
		return  result ;
	}
	/**
	 * ������ʵ�֣�����������ת��Ϊxml������201����ʱʹ��
	 * ������������������ı��ҽԺ��his
	 * @return 
	 */
	public static  StringWriter  onCreate201XmlDispense(TParm parm,TParm rxParm){
		double allMoney = 0.00 ;
		Prescription prescription = new Prescription() ; //��������
		//���ñ�ͷ
		 prescription.setOPIP(parm.getValue("OPT_TERM",0)) ;         //����ip
		 prescription.setOPMANNAME(parm.getValue("OPT_USER",0)) ;   //����name
		 prescription.setOPMANNO(parm.getValue("OPT_USER",0)) ;     //������Ա����
		 prescription.setOPTYPE("201") ;           //��������
		 prescription.setOPWINID(parm.getValue("COUNTER_NO",0)) ;     //�����ն�,���ں�
	
		 //���ô�������
		 List<PrescriptionTableMain> listMain  = prescription.getMain() ;
		 PrescriptionTableMain main = null ;
		 for(int j=0;j<rxParm.getCount();j++){
		 main = new PrescriptionTableMain() ;
		 main.setPRESC_DATE(parm.getValue("ORDER_DATE",0).substring(0, 19)) ;//����ʱ��
		 main.setPRESC_NO(rxParm.getValue("RX_NO",j)) ;   //�������
		 main.setRCPT_INFO("") ;//�����Ϣ
		 main.setPRESCRIBED_BY(rxParm.getValue("DR_CODE",j)) ;//����ҽ��
		 main.setPRESCRIBED_ID(rxParm.getValue("DR_CODE",j)) ;//����ҽ������
		 main.setCHARGE_TYPE("") ;//  ҽ������
		 
		 main.setDATE_OF_BIRTH(parm.getValue("BIRTH_DATE",0).substring(0, 10)) ;//���߳�������
		 main.setDISPENSARY("01") ;//��ҩҩ��
		 main.setDISPENSE_PRI("") ;//��ҩ���ȼ�
		 main.setENTERED_BY(rxParm.getValue("DR_CODE",j)) ;//¼����
		 main.setORDERED_BY(rxParm.getValue("DC_DEPT_CODE",j)) ;//��������
		 main.setORDERED_ID(rxParm.getValue("DC_DEPT_CODE",j)) ;//�������Ҵ���
		 main.setPATIENT_ID(rxParm.getValue("MR_NO",j)) ;//���￨��
		 main.setPATIENT_NAME(rxParm.getValue("PAT_NAME",j)) ;//��������
		 main.setPATIENT_TYPE("00") ;//��������
		
		 main.setPRESC_ATTR("") ;//��������
		 main.setPRESC_IDENTITY("") ;//�������
		 main.setPRESC_INFO("") ;//��������(�Ʒѷ�ʽ)
		 main.setRCPT_REMARK("") ;//������ע
		 main.setREPETITION("") ;//���� 
		 main.setSEX(rxParm.getValue("SEX_TYPE",j)) ;//�����Ա�
		
		 //���ô���ϸ��
		 List<PrescriptionTableDetail> detailList = main.getDetail() ;
		 PrescriptionTableDetail detail = null  ;
		 TParm selectResult = SPCBsmTool.getInstance().query(rxParm.getRow(j)) ;
		 for(int i=0;i<selectResult.getCount();i++){
			 detail = new PrescriptionTableDetail() ;
			 detail.setADMINISTRATION(selectResult.getValue("ROUTE_CODE", i)) ;//ҩƷ�÷�
			 detail.setADVICE_CODE(selectResult.getValue("SEQ_NO", i)) ;//ҽ�����
			 detail.setCOSTS(selectResult.getDouble("OWN_AMT", i)+"") ;//����
			 detail.setDOSAGE(selectResult.getValue("MEDI_QTY", i)) ;//ҩƷ����
			 detail.setDOSAGE_UNITS(selectResult.getValue("MEDI_UNIT", i)) ;//������λ
			 detail.setDRUG_CODE(selectResult.getValue("ORDER_CODE", i)) ;//ҩƷ���
			 detail.setBAT_CODE(selectResult.getValue("ORDER_CODE", i)) ;//ҩƷ���
			 detail.setDRUG_NAME(selectResult.getValue("ORDER_DESC", i)) ;//ҩƷ����
			 detail.setFLG(selectResult.getValue("GIVEBOX_FLG", i)) ;//��ҩ���Ƿ��ҩ���
			 detail.setDRUG_PACKAGE(selectResult.getValue("SPECIFICATION", i)) ;//ҩƷ��װ���
			 detail.setDRUG_PRICE(selectResult.getDouble("OWN_PRICE", i)+"") ;//ҩƷ�۸�
			 detail.setDRUG_SPEC(selectResult.getValue("SPECIFICATION", i)) ;//ҩƷ���
			 detail.setDRUG_UNIT(selectResult.getValue("DISPENSE_UNIT", i)) ;//ҩƷ��λ
			 detail.setQUANTITY(selectResult.getValue("DISPENSE_QTY", i)) ;//����
			 detail.setPRESC_NO(selectResult.getValue("RX_NO", i)) ;///�������
			 detail.setPRESC_DATE(selectResult.getValue("ORDER_DATE", i).substring(0, 19)) ;//����ʱ��
			 detail.setPAYMENTS(selectResult.getDouble("OWN_AMT", i)+"");//	ʵ������
			 detail.setITEM_NO(selectResult.getValue("SEQ_NO", i)) ;//ҩƷ���
			 detail.setFREQUENCY(selectResult.getValue("FREQ_CODE", i)) ;//	ҩƷ����
			 detail.setTRADE_NAME(selectResult.getValue("GOODS_DESC", i)) ;//ҩƷ��Ʒ��
			 detail.setFIRM_ID("") ;//ҩƷ����
			 detail.setDISPENSE_DAYS(selectResult.getValue("TAKE_DAYS", i)) ;   //��ҩ����
			 detail.setDISPENSEDTOTALDOSE(selectResult.getValue("DISPENSE_QTY", i)) ;//��ҩ������
			 detail.setDISPENSEDUNIT(selectResult.getValue("DISPENSE_UNIT", i)) ;//��ҩ��λ
//			 detail.setAMOUNT_PER_PACKAGE("");//һƬ�ļ��� (��ֵ)
//			double qty = Double.parseDouble(parm.getValue("MEDI_QTY", i));
//			double qtybyone = Double.parseDouble(parm.getValue("QTY_BUONE", i));
//			 detail.setDISPESEDDOSE("");//һ����İ�ҩ��
//			 detail.setFREQ_DESC_DETAIL("") ;//����ʱ��
//			 detail.setFREQ_DESC_DETAIL_CODE("") ;//����ʱ�����
			 detailList.add(detail) ;
			 allMoney += selectResult.getDouble("OWN_AMT", i) ;
		 }
		 main.setPAYMENTS(StringTool.round(allMoney,2)+"");//ʵ������
		 main.setCOSTS(StringTool.round(allMoney,2)+"") ;       //����
		 allMoney = 0.00 ;
		 listMain.add(main) ;
		 }
		       StringWriter xml = new StringWriter() ;
		       try {
		    	   JAXB.marshal(prescription, xml); 
		    	   return xml ;
			} catch (Exception e) {
				System.out.println("err===="+e.toString());
			}	            
	            return xml ;	     
	}
	/**
	 * ֪ͨ��ҩ��his �Ĵ�����Ϣ 003��305����
	 * @return
	 */
	public static TParm createHisxml003And305toParm(String sendString){
		TParm  result = new  TParm() ;
		try {
			StringReader read = new StringReader(sendString) ;
			SPCHisPrepare sendValue = JAXB.unmarshal(read, SPCHisPrepare.class) ;
			result.setData("OPWINID", sendValue.getOPWINID()) ;
			result.setData("CASE_NO", sendValue.getPRESC_NO()) ;
			result.setData("DATE", sendValue.getPRESC_DATE()) ;
		
		} catch (Exception e) {
			System.out.println("err======"+e.toString());
		}	
		
		return  result ;
	}
	/**
	 * ������ʵ�֣�����������ת��Ϊxml������202��203����ʱʹ��(��ҩ)
	 * @return 
	 */
	public static  StringWriter  onCreate202And203XmlSend(TParm parm){
		Prescription prescription = new Prescription() ; //��������
		//���ñ�ͷ
		 prescription.setOPIP("SPCIP") ;      //����ip
		 prescription.setOPMANNAME("SPCNAME") ; //����name
		 prescription.setOPMANNO("SPCCODE") ;  //������Ա����
		 prescription.setOPTYPE(parm.getValue("WAY",0)) ;        //��������
		 prescription.setOPWINID(parm.getValue("OPWINID",0)) ;   //�����ն˴��ں�
		 //���ô�������
		 List<PrescriptionTableMain> listMain  = prescription.getMain() ;
		 PrescriptionTableMain main = null ;
		 for(int i=0;i<parm.getCount();i++){
			 main = new PrescriptionTableMain() ;
			 String date = parm.getValue("DATE",i) ;
			 if(date.length()>0)
			 main.setPRESC_DATE(date) ;//������ҩ���ʱ��
			 else  main.setPRESC_DATE("") ;//������ҩ���ʱ��
			 main.setPRESC_NO(parm.getValue("RX_NO",i)) ;//�������
			 listMain.add(main) ; 
		 }	
			   StringWriter xml = new StringWriter() ;
			   try {
				   JAXB.marshal(prescription, xml); 
				   return xml ;
			} catch (Exception e) {
				System.out.println("err======"+e.toString());
			}
	            
	            return xml ;	        
	}
	/**
	 * ����������Ϣ��his �Ĵ�����Ϣ 007����
	 * ͬ��סԺ����
	 * @return
	 */
	public static TParm createHisxml007toParm(String sendString){
		TParm  result = new  TParm() ;
		try {
			StringReader read = new StringReader(sendString) ;
			SPCHisOdiPrescription sendValue = JAXB.unmarshal(read, SPCHisOdiPrescription.class) ;
			for(int i=0;i<sendValue.getOrderDetail().size();i++){
				//  ====ҽ��ϸ��
				result.setData("ORDER_NO", i, sendValue.getOrderDetail().get(i).getORDERNO()) ;//
				result.setData("ORDER_SEQ", i, sendValue.getOrderDetail().get(i).getORDERSEQ()) ;//
				result.setData("START_DTTM", i, sendValue.getOrderDetail().get(i).getSTARTDTTM()) ;//ҽ��չ����������
				result.setData("END_DTTM", i, sendValue.getOrderDetail().get(i).getENDDTTM()) ;//ҽ��չ����������
				result.setData("DSPN_KIND", i, sendValue.getOrderDetail().get(i).getDSPNKIND()) ;//��ҩ����
				result.setData("ORDER_CAT1_CODE", i, sendValue.getOrderDetail().get(i).getORDERCATCODE()) ;//
				result.setData("LINKMAIN_FLG", i, sendValue.getOrderDetail().get(i).getLINKMAINFLG()) ;
				result.setData("LINK_NO", i, sendValue.getOrderDetail().get(i).getLINKNO()) ;//
				result.setData("BAR_CODE", i, sendValue.getOrderDetail().get(i).getIVABARCODE()) ;//ƿǩ�����
				result.setData("ORDER_CODE", i, sendValue.getOrderDetail().get(i).getORDERCODE()) ;
				result.setData("ORDER_DESC", i, sendValue.getOrderDetail().get(i).getORDERDESC()) ;//
				result.setData("SPECIFICATION", i, sendValue.getOrderDetail().get(i).getSPECIFICATION()) ;
				result.setData("MEDI_QTY", i, sendValue.getOrderDetail().get(i).getMEDIQTY()) ;//��ҩ����
				result.setData("MEDI_UNIT", i, sendValue.getOrderDetail().get(i).getMEDIUNIT()) ;
				result.setData("FREQ_CODE", i, sendValue.getOrderDetail().get(i).getFREQCODE()) ;
				result.setData("ROUTE_CODE", i, sendValue.getOrderDetail().get(i).getROUTECODE()) ;//
				result.setData("DOSAGE_QTY", i, sendValue.getOrderDetail().get(i).getDOSAGEQTY()) ;//��ҩ����
				result.setData("DISPENSE_UNIT", i, sendValue.getOrderDetail().get(i).getDOSAGEUNIT()) ;//
				result.setData("ORDER_DATE", i, sendValue.getOrderDetail().get(i).getORDERDTTM()) ;//
				result.setData("ATC_FLG", i, sendValue.getOrderDetail().get(i).getSENDACTFLG()) ;//�Ƿ��Ͱ�ҩ��
				result.setData("DR_NOTE", i, sendValue.getOrderDetail().get(i).getDRNOTE()) ;//ҽ����ע
				//ҽ������
				result.setData("CASE_NO", i, sendValue.getOrderDetail().get(i).getCASENO()) ;
				result.setData("STATION_CODE", i, sendValue.getOrderDetail().get(i).getSTATIONCODE()) ;
				result.setData("STATION_DESC", i, sendValue.getOrderDetail().get(i).getSTATIONDESC()) ;
				result.setData("MR_NO", i, sendValue.getOrderDetail().get(i).getMRNO()) ;
				result.setData("ORDER_DR_CODE", i, sendValue.getOrderDetail().get(i).getDRCODE()) ;
				result.setData("ORDER_DR_DESC", i, sendValue.getOrderDetail().get(i).getDRNAME()) ;
				result.setData("PAT_NAME", i, sendValue.getOrderDetail().get(i).getPATNAME()) ;
				result.setData("SEX_TYPE", i, sendValue.getOrderDetail().get(i).getSEX()) ;
				result.setData("BIRTH_DATE", i, sendValue.getOrderDetail().get(i).getBIRTHDAY()) ;
				result.setData("BED_NO", i, sendValue.getOrderDetail().get(i).getBEDNO()) ;
				result.setData("EXEC_DEPT_CODE", i, sendValue.getOrderDetail().get(i).getEXECDEPTCODE()) ;
				result.setData("OPT_USER", i, "SPCUSER") ;
				result.setData("OPT_TERM", i, "SPCIP") ;
				
			}
		
		} catch (Exception e) {
			System.out.println("err======"+e.toString());
			result.setErrCode(-1) ;
			return result ;
		}	
		return  result ;
	}
	/**
	 * �����������ݣ�his �� 009����
	 * ͬ��ҩ����ҩ����������
	 * @return
	 */
	public static TParm createHisxml009toParm(String sendString){
		TParm  resultDetail = new  TParm() ;
		TParm  result = new  TParm() ;
		TParm  resultMain= new  TParm() ;
		try {
			StringReader read = new StringReader(sendString) ;
			Prescription sendValue = JAXB.unmarshal(read, Prescription.class) ;
			for(int j=0;j<sendValue.getRequestHis().size();j++){
				resultMain.addData("APP_ORG_CODE", sendValue.getRequestHis().get(j).getAPPORGCODE());//���벿�Ŵ���
				resultMain.addData("DRUG_CATEGORY", sendValue.getRequestHis().get(j).getDRUGCATEGORY());//����ҩƷ����
				resultMain.addData("OPT_DATE", sendValue.getRequestHis().get(j).getOPTDATE());//����ʱ��
				resultMain.addData("OPT_TERM", sendValue.getRequestHis().get(j).getOPTTERM());//������ĩIP
				resultMain.addData("OPT_USER", sendValue.getRequestHis().get(j).getOPTUSER());//������Ա
				resultMain.addData("REASON_CHN_DESC", sendValue.getRequestHis().get(j).getREASONCHNDESC());//����ԭ��
				resultMain.addData("REGION_CODE", sendValue.getRequestHis().get(j).getREGIONCODE());//Ժ������
				resultMain.addData("REQTYPE_CODE", sendValue.getRequestHis().get(j).getREQTYPECODE());//�������
				resultMain.addData("REQUEST_DATE", sendValue.getRequestHis().get(j).getREQUESTDATE());//��������
				resultMain.addData("REQUEST_NO", sendValue.getRequestHis().get(j).getREQUESTNO());//���뵥��
				resultMain.addData("REQUEST_USER", sendValue.getRequestHis().get(j).getREQUESTUSER());//������Ա
				resultMain.addData("TO_ORG_CODE", sendValue.getRequestHis().get(j).getTOORGCODE());//���ܲ��Ŵ���
				resultMain.addData("URGENT_FLG", sendValue.getRequestHis().get(j).getURGENTFLG());//�Ƿ����
				resultMain.addData("UNIT_TYPE", sendValue.getRequestHis().get(j).getUNITTYPE());//��λ���
				for(int i=0;i<sendValue.getRequestHis().get(j).getRequestDetail().size();i++){
					//  ====������������
					resultDetail.addData("ORDER_CODE",  sendValue.getRequestHis().get(j).getRequestDetail().get(i).getORDERCODE()) ;
					resultDetail.addData("QTY",  sendValue.getRequestHis().get(j).getRequestDetail().get(i).getQTY()) ;
					resultDetail.addData("REQUEST_NO",  sendValue.getRequestHis().get(j).getRequestDetail().get(i).getREQUESTNO()) ;
					resultDetail.addData("RETAIL_PRICE",  sendValue.getRequestHis().get(j).getRequestDetail().get(i).getRETAILPRICE()) ;
					resultDetail.addData("SEQ_NO",  sendValue.getRequestHis().get(j).getRequestDetail().get(i).getSEQNO()) ;
					resultDetail.addData("UNIT_CODE",  sendValue.getRequestHis().get(j).getRequestDetail().get(i).getUNITCODE()) ;
					resultDetail.addData("OPT_USER",  "SPCUSER") ;
					resultDetail.addData("OPT_TERM",  "SPCIP") ;
					
				}
			}
		
		result.setData( "DETAIL", resultDetail.getData());
		result.setData("MAIN", resultMain.getData()) ;
		} catch (Exception e) {
			System.out.println("err======"+e.toString());
			result.setErrCode(-1) ;
			return result ;
		}	
		return  result ;
	}
	/**
	 * ����ҩƷhis�ֵ���Ϣ��his  011����
	 * 
	 * @return
	 */
	public static TParm createHisxml011toParm(String sendString){
		TParm  result = new  TParm() ;
		try {
			StringReader read = new StringReader(sendString) ;
			Prescription sendValue = JAXB.unmarshal(read, Prescription.class) ;
			for(int i=0;i<sendValue.getHisPhaBase().size();i++){
				//  ====ҽ��ϸ��
				result.setData("ALIAS_DESC", i, sendValue.getHisPhaBase().get(i).getALIASDESC()) ;//��Ʒ��
				result.setData("DOSE_CODE", i, sendValue.getHisPhaBase().get(i).getDOSECODE()) ;//����
				result.setData("DOSAGE_QTY", i, sendValue.getHisPhaBase().get(i).getDOSRATIO()) ;//��ҩ��λת����
				result.setData("DOSAGE_UNIT", i, sendValue.getHisPhaBase().get(i).getDOSUNIT()) ;//��ҩ��λ����
				result.setData("ORDER_CODE", i, sendValue.getHisPhaBase().get(i).getDRUGCODE()) ;//ҩƷ����
				result.setData("ORDER_DESC", i, sendValue.getHisPhaBase().get(i).getDRUGDESC()) ;//ҩƷ����
				result.setData("ACTIVE_FLG", i, sendValue.getHisPhaBase().get(i).getISACTIVE()) ;//�Ƿ�����
				result.setData("MAN_CODE", i, sendValue.getHisPhaBase().get(i).getMANUF()) ;//��������
				result.setData("PHA_TYPE", i, sendValue.getHisPhaBase().get(i).getPHATYPE()) ;//ҩƷ����
				result.setData("STOCK_PRICE", i, sendValue.getHisPhaBase().get(i).getPURPRICE()) ;//�ɹ���(Ƭ)
				result.setData("PURCH_QTY", i, sendValue.getHisPhaBase().get(i).getPURRATIO()) ;//�ɹ���λת����
				result.setData("PURCH_UNIT", i, sendValue.getHisPhaBase().get(i).getPURUNIT()) ;//�ɹ���λ����
				result.setData("PY1", i, sendValue.getHisPhaBase().get(i).getPYCODE()) ;//ƴ����
				result.setData("RETAIL_PRICE", i, sendValue.getHisPhaBase().get(i).getRTLPRICE()) ;//���ۼ�
				result.setData("SPECIFICATION", i, sendValue.getHisPhaBase().get(i).getSPEC()) ;//���
				result.setData("STOCK_QTY", i, sendValue.getHisPhaBase().get(i).getSTKRATIO()) ;//��浥λת����
				result.setData("STOCK_UNIT", i, sendValue.getHisPhaBase().get(i).getSTKUNIT()) ;//��浥λ����
				result.setData("CTRLDRUGCLASS_CODE", i, sendValue.getHisPhaBase().get(i).getTOXCODE()) ;//�龫
				result.setData("CAT1_TYPE", i, "PHA") ;//ҩƷ����
				if(sendValue.getHisPhaBase().get(i).getTOXCODE().length()>0)
				result.setData("CTRL_FLG", i, "Y") ;//�龫���
				else
					result.setData("CTRL_FLG", i, "N") ;//�龫���
				if(sendValue.getHisPhaBase().get(i).getPHATYPE().equals("W"))
				result.setData("ORDER_CAT1_CODE", i, "PHA_W") ;//ҩƷϸ����
				else result.setData("ORDER_CAT1_CODE", i, "PHA_C") ;//ҩƷϸ����
			}
		
		} catch (Exception e) {
			System.out.println("err======"+e.toString());
			result.setErrCode(-1) ;
			return result ;
		}	
		return result ;
	}
	/**
	 * ��������his�ֵ���Ϣ��his  012����
	 * 
	 * @return
	 */
	public static TParm createHisxml012toParm(String sendString){
		TParm  result = new  TParm() ;
		try {
			StringReader read = new StringReader(sendString) ;
			Prescription sendValue = JAXB.unmarshal(read, Prescription.class) ;
			for(int i=0;i<sendValue.getHisDept().size();i++){
				//  ====ҽ��ϸ��
				result.setData("COST_CENTER_CODE", i, sendValue.getHisDept().get(i).getCOSTCENTER()) ;//�ɱ����Ĵ���
				result.setData("DEPT_CODE", i, sendValue.getHisDept().get(i).getDEPTCODE()) ;//���Ҵ���
				result.setData("DEPT_CHN_DESC", i, sendValue.getHisDept().get(i).getDEPTNAME()) ;//��������
				result.setData("ACTIVE_FLG", i, sendValue.getHisDept().get(i).getISACTIVE()) ;//�Ƿ�����
			}
		
		} catch (Exception e) {
			System.out.println("err======"+e.toString());
			result.setErrCode(-1) ;
			return result ;
		}	
		return result ;
	}
	/**
	 * ��������������Ϣͬ��HIS
	 * @return 
	 */
	public static  String  onCreate010XmlSend(TParm parmD,TParm parmM){
		 StringBuffer  out = new StringBuffer() ;
			out.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
			out.append("<requestBean>");
			out.append("<businessType>");
			out.append("drugToHisAccount");
			out.append("</businessType>");
			out.append("<requestsDataSet>");
			for(int j=0;j<parmM.getCount();j++){  
				out.append("<drugToHisAccount>");	
				out.append("<saleBillCode>"+parmM.getValue("DISPENSE_NO", j)+"</saleBillCode>");//�������Ĺ�������
				out.append("<receBillCode>"+parmM.getValue("REQUEST_NO", j)+"</receBillCode>");//���뵥��
				out.append("<depCode>"+parmM.getValue("APP_ORG_CODE", j)+"</depCode>");//�������
				if(parmM.getValue("DISPENSE_DATE", j).length()>0)
				out.append("<confirmDate>"+parmM.getValue("DISPENSE_DATE", j).substring(0, 19)+"</confirmDate>");//��ҩʱ��	
				else 
				out.append("<confirmDate></confirmDate>");//��ҩʱ��	
				out.append("<remark></remark>");//��ע
				out.append("<drugParticular>");	
				for(int i = 0 ; i <parmD.getCount() ; i++ ){
					out.append("<drug " +
							" rowNum=\""+parmD.getValue("REQUEST_SEQ", i)+"\"" +//�кţ�ԭ���뵥��ϸ��ţ�
							" drugID=\""+parmD.getValue("ORDER_CODE", i)+"\"" +//HIS�е�ҩƷID
							" factoryID=\"\"" +//��������ID
							" batchNum=\""+parmD.getValue("BATCH_NO", i)+"\"" +//ҩƷ����
							" batchLot=\"\"" +//ҩƷ����
							" authorNum=\"\"" +//��׼�ĺ�
							" regiNum=\"\"" +//ע��֤��
							" produceDate=\"\"" +//��������
							" usefulDate=\""+parmD.getValue("VALID_DATE", i).substring(0, 10)+"\"" +//��Ч��
							" unit=\""+parmD.getValue("UNIT_CODE", i)+"\"" +//��λ
							" factQuantity=\""+parmD.getValue("QTY", i)+"\"" +//��������
							" price=\""+parmD.getValue("INVENT_PRICE", i)+"\"" +//�����۸�
							" retailPrice=\""+parmD.getValue("RETAIL_PRICE", i)+"\"" +//�ο����ۼ۸�
							" repLibFlag=\""+parmD.getValue("REPLIBFLAG", i)+"\"" +//�Ƿ���ҪҽԺ�빩Ӧ�̽��� 0:����ҩƷ�������㣩 1:�������
							" supCode=\""+parmD.getValue("SUP_CODE", i)+"\"" +//��������ID
							"/>");
				}
				out.append("</drugParticular>");	
				out.append("</drugToHisAccount>");
			}
			out.append("</requestsDataSet>");
			out.append("</requestBean>");
			return out.toString() ;
	
	}
	@SuppressWarnings("static-access")
	public static void main(String[] args){
		/*Testxml xml = new Testxml() ;	
		String sendString = xml.onCreateXmlDispense(null).toString() ;
		System.out.println("sendString====="+sendString);
	 	createHisxml001toParm(xml.sendString1) ;*/
	}
}
