package jdo.spc.bsm.bean;
/**
*
* <p>Title: xml处理类</p>
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
	 * 构建实例
	 */
  public static XmlUtils instanceObject ;
    /**
     * 得到实例
     * @return
     */
  public static XmlUtils getInstance(){
	  if(instanceObject==null)
		  instanceObject = new XmlUtils() ;
	  return instanceObject ;
  }
  
	/**
	 * 方法的实现，将处方对象转换为xml，呼叫202、203方法时使用(发药)
	 * @return 
	 */
	public static  StringWriter  onCreateXmlSend(TParm parm){
		Prescription prescription = new Prescription() ; //处方对象
		//设置表头
		 prescription.setOPIP(((TParm)parm.getData("Data", "TITLE")).getValue("OPT_TERM")) ;      //操作ip
		 prescription.setOPMANNAME(((TParm)parm.getData("Data", "TITLE")).getValue("OPT_USER")) ; //操作name
		 prescription.setOPMANNO(((TParm)parm.getData("Data", "TITLE")).getValue("OPT_CODE")) ;  //操作人员编码
		 prescription.setOPTYPE(((TParm)parm.getData("Data", "TITLE")).getValue("WAY")) ;        //操作代码
		 prescription.setOPWINID(((TParm)parm.getData("Data", "TITLE")).getValue("OPWINID")) ;   //操作终端窗口号
		 //设置处方主表
		 List<PrescriptionTableMain> listMain  = prescription.getMain() ;
		 PrescriptionTableMain main = null ;
			 main = new PrescriptionTableMain() ;
			 String date = ((TParm)parm.getData("Data", "MAIN")).getValue("PHA_DOSAGE_DATE") ;
			 if(date.length()>0)
			 main.setPRESC_DATE(date.substring(0, 19)) ;//处方配药完成时间
			 else  main.setPRESC_DATE("") ;//处方配药完成时间
			 main.setPRESC_NO(((TParm)parm.getData("Data", "MAIN")).getValue("RX_NO")) ;//处方编号
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
	 * 解析xml，将xml转化为对象
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
	 * 解析xml，将xml转化为对象，301方法呼叫物联网传的xml
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
	 * 解析xml，将xml转化为对象，305方法呼叫物联网传的xml,请领单
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
	 * 解析对象，将对象转化为xml,测试专用
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
	 * 解析对象，将对象转化为xml,301方法物联网返回的xml
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
	 * 处方类型的转换 0、7：补充计价
          1：西成药
          2：管制药品
          3：中药饮片
          4：诊疗项目
          5：检验检查项目
	 */

	@SuppressWarnings("unchecked")
	public static  String onRxtype(String code){
		String typeDesc = "" ;
		Map<String, String>  map = new HashMap<String, String>();
		map.put("0", "补充计价") ;
		map.put("7", "补充计价") ;
		map.put("1", "西成药") ;
		map.put("2", "管制药品") ;
		map.put("3", "中药饮片") ;
		map.put("4", "诊疗项目") ;
		map.put("5", "检验检查项目") ;
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
	 * 同步字典()药品，sys_fee 呼叫101方法
	 * @param parm
	 * @return
	 */
	public static StringWriter onSysFeeDictionary(TParm parm){
		StringWriter xml = new StringWriter() ;
		Prescription prescription = new Prescription() ; //处方对象
		//设置表头
		 prescription.setOPIP(parm.getValue("OPT_TERM")) ;      //操作ip
		 prescription.setOPMANNAME(parm.getValue("OPT_USER")) ; //操作name
		 prescription.setOPMANNO(parm.getValue("OPT_CODE")) ;  //操作人员编码
		 prescription.setOPTYPE(parm.getValue("WAY")) ;        //操作代码
		 prescription.setOPWINID(parm.getValue("OPWINID")) ;   //操作终端窗口号
		 //设置字典明细
		 List<DictionarySysFee> listSysFee = prescription.getSysFee() ;
		 DictionarySysFee sysFee = null ;
		 for(int i=0;i<parm.getCount();i++){
			 sysFee = new DictionarySysFee() ;
			 sysFee.setDRUG_CODE(parm.getValue("ORDER_CODE", i)); //HIS药品编号
			 sysFee.setDRUG_NAME(parm.getValue("ORDER_DESC", i)) ;//药品名称
			 sysFee.setTRADE_NAME(parm.getValue("GOODS_DESC", i));//药品商品名
			 sysFee.setDRUG_SPEC(parm.getValue("SPECIFICATION", i));//药品规格
			 sysFee.setDRUG_PACKAGE(parm.getValue("SPECIFICATION", i));//药品包装规格
			 sysFee.setDRUG_UNIT(parm.getValue("UNIT_CHN_DESC", i)) ;//药品单位
			 sysFee.setFIRM_ID(parm.getValue("MAN_CHN_DESC", i)) ;//药品厂家
			 sysFee.setDRUG_PRICE(parm.getDouble("OWN_PRICE", i)+"");//药品价格
			 sysFee.setDRUG_FORM(parm.getValue("CHN_DESC", i)) ;//药品剂型
			 sysFee.setDRUG_SORT(parm.getValue("CTRLDRUGCLASS_CHN_DESC", i)) ;//药品分类
			 sysFee.setBARCODE(parm.getValue("BAR_CODE", i));//药品物联网????????????????????????
			 sysFee.setLAST_DATE(parm.getValue("OPT_DATE", i)) ;//最后更新时间
			 sysFee.setPINYIN(parm.getValue("PY1", i)) ;//药品拼音
			 sysFee.setDRUG_CONVERTATION(parm.getValue("DOSAGE_QTY",i));//换算率?????????????????????????????????
			 sysFee.setALLOWIND(parm.getValue("ACTIVE_FLG", i)) ;//是否启用 
			 listSysFee.add(sysFee) ;
		 }
		 try {
			JAXB.marshal(prescription, xml) ;
			
//			
			return xml ;
		} catch (Exception e) {
		    System.out.println("错误信息=========="+e.toString());
		}
		
		return xml ;
	}
	
	/**
	 * 同步字典()药品库存 呼叫102方法
	 * @param parm
	 * @return
	 */
	public static StringWriter onStockDictionary(TParm parm){
		StringWriter xml = new StringWriter() ;
		Prescription prescription = new Prescription() ; //处方对象
		//设置表头
		 prescription.setOPIP(parm.getValue("OPT_TERM")) ;      //操作ip
		 prescription.setOPMANNAME(parm.getValue("OPT_USER")) ; //操作name
		 prescription.setOPMANNO(parm.getValue("OPT_CODE")) ;  //操作人员编码
		 prescription.setOPTYPE(parm.getValue("WAY")) ;        //操作代码
		 prescription.setOPWINID(parm.getValue("OPWINID")) ;   //操作终端窗口号
		 //设置字典明细
		 List<DictionaryStock> listStock = prescription.getStock();
		 DictionaryStock stock = null ;
		 for(int i=0;i<parm.getCount();i++){
			 stock = new DictionaryStock() ;
			 stock.setDISPENSARY("门急诊药房") ;//发药药局代码
			 stock.setDRUG_CODE(parm.getValue("ORDER_CODE", i)) ;//药品编号
			 stock.setDRUG_QUANTITY("0") ;//药品数量
			 stock.setLOCATIONINFO(parm.getValue("MATERIAL_LOC_CODE", i)) ;//药品货位
			 listStock.add(stock) ;
		
		 }
		 try {
			JAXB.marshal(prescription, xml) ;
			return xml ;
		} catch (Exception e) {
		    System.out.println("错误信息=========="+e.toString());
		}
		
		return xml ;
	}
	/**
	 * 同步字典()科室字典 呼叫104方法
	 * @param parm
	 * @return
	 */
	public static StringWriter onDeptDictionary(TParm parm){
		StringWriter xml = new StringWriter() ;
		Prescription prescription = new Prescription() ; //处方对象
		//设置表头
		 prescription.setOPIP(parm.getValue("OPT_TERM")) ;      //操作ip
		 prescription.setOPMANNAME(parm.getValue("OPT_USER")) ; //操作name
		 prescription.setOPMANNO(parm.getValue("OPT_CODE")) ;  //操作人员编码
		 prescription.setOPTYPE(parm.getValue("WAY")) ;        //操作代码
		 prescription.setOPWINID(parm.getValue("OPWINID")) ;   //操作终端窗口号
		 //设置字典明细
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
		    System.out.println("错误信息=========="+e.toString());
		}
		
		return xml ;
	}
	/**
	 * 解析处方信息，his 的处方信息 001方法
	 * @return
	 */
	public static TParm createHisxml001toParm(String sendString){
		TParm  result = new  TParm() ;
		try {
			StringReader read = new StringReader(sendString) ;
			SPCHisPrescription sendValue = JAXB.unmarshal(read, SPCHisPrescription.class) ;
			for(int i=0;i<sendValue.getOrderDetail().size();i++){
				//  ====医嘱细项
				result.setData("SEQ_NO", i, sendValue.getOrderDetail().get(i).getSeqNo()) ;//是否缴费
				result.setData("BILL_FLG", i, sendValue.getOrderDetail().get(i).getBillFlg()) ;//是否缴费
				result.setData("CTRL_FLG", i, sendValue.getOrderDetail().get(i).getCtrlFlg()) ;//是否麻精药品
				result.setData("DISPENSE_QTY", i, sendValue.getOrderDetail().get(i).getDispenseQty()) ;//发药数量
				result.setData("DISPENSE_UNIT", i, sendValue.getOrderDetail().get(i).getDispenseUnit()) ;
				result.setData("DOSAGE_QTY", i, sendValue.getOrderDetail().get(i).getDosageQty()) ;//配药数量
				result.setData("DOSAGE_UNIT", i, sendValue.getOrderDetail().get(i).getDosageUnit()) ;
				result.setData("FREQ_CODE", i, sendValue.getOrderDetail().get(i).getFreqCode()) ;//频次
				result.setData("GIVEBOX_FLG", i, sendValue.getOrderDetail().get(i).getGiveBoxFlg()) ;//固定为Y
				result.setData("GOODS_DESC", i, sendValue.getOrderDetail().get(i).getGoodsDesc()) ;
				result.setData("MEDI_QTY", i, sendValue.getOrderDetail().get(i).getMediQty()) ;//开药剂量
				result.setData("MEDI_UNIT", i, sendValue.getOrderDetail().get(i).getMediUnit()) ;
				result.setData("ORDER_CAT1_CODE", i, sendValue.getOrderDetail().get(i).getOrderCatCode()) ;//医嘱分类
				result.setData("ORDER_CODE", i, sendValue.getOrderDetail().get(i).getOrderCode()) ;
				result.setData("ORDER_DESC", i, sendValue.getOrderDetail().get(i).getOrderDesc()) ;
				result.setData("OWN_AMT", i, sendValue.getOrderDetail().get(i).getOwnAmt()) ;//金额
				result.setData("OWN_PRICE", i, sendValue.getOrderDetail().get(i).getOwnPrice()) ;//单价
				result.setData("PHA_TYPE", i, sendValue.getOrderDetail().get(i).getPhaType()) ;//药品分类
				result.setData("ROUTE_CODE", i, sendValue.getOrderDetail().get(i).getRouteCode()) ;//用法代码
				result.setData("SPECIFICATION", i, sendValue.getOrderDetail().get(i).getSpecification()) ;//规格
				result.setData("TAKE_DAYS", i, sendValue.getOrderDetail().get(i).getTakeDays()) ;//天数
				result.setData("RX_NO", i, sendValue.getOrderDetail().get(i).getRxNo()) ;
				result.setData("ORDER_DATE", i, sendValue.getOrderDetail().get(i).getOrderDate()) ;
				//医嘱主项
				result.setData("CASE_NO", i, sendValue.getCaseNo()) ;
				result.setData("BOX_TYPE", i, "0") ;//发药状态
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
	 * 解析对象，将对象转化为xml,物联网给his的
	 * 物联网给his所有的固定格式
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
	 * 解析xml，将xml转化为对象，
	 * his返回给物联网的xml格式，
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
	 * 方法的实现，将处方对象转换为xml，呼叫201方法时使用
	 * 针对于所有上物联网的别家医院的his
	 * @return 
	 */
	public static  StringWriter  onCreate201XmlDispense(TParm parm,TParm rxParm){
		double allMoney = 0.00 ;
		Prescription prescription = new Prescription() ; //处方对象
		//设置表头
		 prescription.setOPIP(parm.getValue("OPT_TERM",0)) ;         //操作ip
		 prescription.setOPMANNAME(parm.getValue("OPT_USER",0)) ;   //操作name
		 prescription.setOPMANNO(parm.getValue("OPT_USER",0)) ;     //操作人员编码
		 prescription.setOPTYPE("201") ;           //操作代码
		 prescription.setOPWINID(parm.getValue("COUNTER_NO",0)) ;     //操作终端,窗口号
	
		 //设置处方主表
		 List<PrescriptionTableMain> listMain  = prescription.getMain() ;
		 PrescriptionTableMain main = null ;
		 for(int j=0;j<rxParm.getCount();j++){
		 main = new PrescriptionTableMain() ;
		 main.setPRESC_DATE(parm.getValue("ORDER_DATE",0).substring(0, 19)) ;//处方时间
		 main.setPRESC_NO(rxParm.getValue("RX_NO",j)) ;   //处方编号
		 main.setRCPT_INFO("") ;//诊断信息
		 main.setPRESCRIBED_BY(rxParm.getValue("DR_CODE",j)) ;//开方医生
		 main.setPRESCRIBED_ID(rxParm.getValue("DR_CODE",j)) ;//开方医生代码
		 main.setCHARGE_TYPE("") ;//  医保类型
		 
		 main.setDATE_OF_BIRTH(parm.getValue("BIRTH_DATE",0).substring(0, 10)) ;//患者出生日期
		 main.setDISPENSARY("01") ;//发药药局
		 main.setDISPENSE_PRI("") ;//配药优先级
		 main.setENTERED_BY(rxParm.getValue("DR_CODE",j)) ;//录方人
		 main.setORDERED_BY(rxParm.getValue("DC_DEPT_CODE",j)) ;//开单科室
		 main.setORDERED_ID(rxParm.getValue("DC_DEPT_CODE",j)) ;//开单科室代码
		 main.setPATIENT_ID(rxParm.getValue("MR_NO",j)) ;//就诊卡号
		 main.setPATIENT_NAME(rxParm.getValue("PAT_NAME",j)) ;//患者姓名
		 main.setPATIENT_TYPE("00") ;//患者类型
		
		 main.setPRESC_ATTR("") ;//处方属性
		 main.setPRESC_IDENTITY("") ;//患者身份
		 main.setPRESC_INFO("") ;//处方类型(计费方式)
		 main.setRCPT_REMARK("") ;//处方备注
		 main.setREPETITION("") ;//剂数 
		 main.setSEX(rxParm.getValue("SEX_TYPE",j)) ;//患者性别
		
		 //设置处方细表
		 List<PrescriptionTableDetail> detailList = main.getDetail() ;
		 PrescriptionTableDetail detail = null  ;
		 TParm selectResult = SPCBsmTool.getInstance().query(rxParm.getRow(j)) ;
		 for(int i=0;i<selectResult.getCount();i++){
			 detail = new PrescriptionTableDetail() ;
			 detail.setADMINISTRATION(selectResult.getValue("ROUTE_CODE", i)) ;//药品用法
			 detail.setADVICE_CODE(selectResult.getValue("SEQ_NO", i)) ;//医嘱编号
			 detail.setCOSTS(selectResult.getDouble("OWN_AMT", i)+"") ;//费用
			 detail.setDOSAGE(selectResult.getValue("MEDI_QTY", i)) ;//药品剂量
			 detail.setDOSAGE_UNITS(selectResult.getValue("MEDI_UNIT", i)) ;//剂量单位
			 detail.setDRUG_CODE(selectResult.getValue("ORDER_CODE", i)) ;//药品编号
			 detail.setBAT_CODE(selectResult.getValue("ORDER_CODE", i)) ;//药品编号
			 detail.setDRUG_NAME(selectResult.getValue("ORDER_DESC", i)) ;//药品名称
			 detail.setFLG(selectResult.getValue("GIVEBOX_FLG", i)) ;//包药机是否包药标记
			 detail.setDRUG_PACKAGE(selectResult.getValue("SPECIFICATION", i)) ;//药品包装规格
			 detail.setDRUG_PRICE(selectResult.getDouble("OWN_PRICE", i)+"") ;//药品价格
			 detail.setDRUG_SPEC(selectResult.getValue("SPECIFICATION", i)) ;//药品规格
			 detail.setDRUG_UNIT(selectResult.getValue("DISPENSE_UNIT", i)) ;//药品单位
			 detail.setQUANTITY(selectResult.getValue("DISPENSE_QTY", i)) ;//数量
			 detail.setPRESC_NO(selectResult.getValue("RX_NO", i)) ;///处方编号
			 detail.setPRESC_DATE(selectResult.getValue("ORDER_DATE", i).substring(0, 19)) ;//处方时间
			 detail.setPAYMENTS(selectResult.getDouble("OWN_AMT", i)+"");//	实付费用
			 detail.setITEM_NO(selectResult.getValue("SEQ_NO", i)) ;//药品序号
			 detail.setFREQUENCY(selectResult.getValue("FREQ_CODE", i)) ;//	药品用量
			 detail.setTRADE_NAME(selectResult.getValue("GOODS_DESC", i)) ;//药品商品名
			 detail.setFIRM_ID("") ;//药品厂家
			 detail.setDISPENSE_DAYS(selectResult.getValue("TAKE_DAYS", i)) ;   //摆药天数
			 detail.setDISPENSEDTOTALDOSE(selectResult.getValue("DISPENSE_QTY", i)) ;//摆药量总数
			 detail.setDISPENSEDUNIT(selectResult.getValue("DISPENSE_UNIT", i)) ;//摆药单位
//			 detail.setAMOUNT_PER_PACKAGE("");//一片的剂量 (数值)
//			double qty = Double.parseDouble(parm.getValue("MEDI_QTY", i));
//			double qtybyone = Double.parseDouble(parm.getValue("QTY_BUONE", i));
//			 detail.setDISPESEDDOSE("");//一包里的摆药量
//			 detail.setFREQ_DESC_DETAIL("") ;//服用时间
//			 detail.setFREQ_DESC_DETAIL_CODE("") ;//服用时间编码
			 detailList.add(detail) ;
			 allMoney += selectResult.getDouble("OWN_AMT", i) ;
		 }
		 main.setPAYMENTS(StringTool.round(allMoney,2)+"");//实付费用
		 main.setCOSTS(StringTool.round(allMoney,2)+"") ;       //费用
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
	 * 通知发药，his 的处方信息 003和305方法
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
	 * 方法的实现，将处方对象转换为xml，呼叫202、203方法时使用(发药)
	 * @return 
	 */
	public static  StringWriter  onCreate202And203XmlSend(TParm parm){
		Prescription prescription = new Prescription() ; //处方对象
		//设置表头
		 prescription.setOPIP("SPCIP") ;      //操作ip
		 prescription.setOPMANNAME("SPCNAME") ; //操作name
		 prescription.setOPMANNO("SPCCODE") ;  //操作人员编码
		 prescription.setOPTYPE(parm.getValue("WAY",0)) ;        //操作代码
		 prescription.setOPWINID(parm.getValue("OPWINID",0)) ;   //操作终端窗口号
		 //设置处方主表
		 List<PrescriptionTableMain> listMain  = prescription.getMain() ;
		 PrescriptionTableMain main = null ;
		 for(int i=0;i<parm.getCount();i++){
			 main = new PrescriptionTableMain() ;
			 String date = parm.getValue("DATE",i) ;
			 if(date.length()>0)
			 main.setPRESC_DATE(date) ;//处方配药完成时间
			 else  main.setPRESC_DATE("") ;//处方配药完成时间
			 main.setPRESC_NO(parm.getValue("RX_NO",i)) ;//处方编号
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
	 * 解析处方信息，his 的处方信息 007方法
	 * 同步住院处方
	 * @return
	 */
	public static TParm createHisxml007toParm(String sendString){
		TParm  result = new  TParm() ;
		try {
			StringReader read = new StringReader(sendString) ;
			SPCHisOdiPrescription sendValue = JAXB.unmarshal(read, SPCHisOdiPrescription.class) ;
			for(int i=0;i<sendValue.getOrderDetail().size();i++){
				//  ====医嘱细项
				result.setData("ORDER_NO", i, sendValue.getOrderDetail().get(i).getORDERNO()) ;//
				result.setData("ORDER_SEQ", i, sendValue.getOrderDetail().get(i).getORDERSEQ()) ;//
				result.setData("START_DTTM", i, sendValue.getOrderDetail().get(i).getSTARTDTTM()) ;//医嘱展开启用日期
				result.setData("END_DTTM", i, sendValue.getOrderDetail().get(i).getENDDTTM()) ;//医嘱展开结束日期
				result.setData("DSPN_KIND", i, sendValue.getOrderDetail().get(i).getDSPNKIND()) ;//配药种类
				result.setData("ORDER_CAT1_CODE", i, sendValue.getOrderDetail().get(i).getORDERCATCODE()) ;//
				result.setData("LINKMAIN_FLG", i, sendValue.getOrderDetail().get(i).getLINKMAINFLG()) ;
				result.setData("LINK_NO", i, sendValue.getOrderDetail().get(i).getLINKNO()) ;//
				result.setData("BAR_CODE", i, sendValue.getOrderDetail().get(i).getIVABARCODE()) ;//瓶签条码号
				result.setData("ORDER_CODE", i, sendValue.getOrderDetail().get(i).getORDERCODE()) ;
				result.setData("ORDER_DESC", i, sendValue.getOrderDetail().get(i).getORDERDESC()) ;//
				result.setData("SPECIFICATION", i, sendValue.getOrderDetail().get(i).getSPECIFICATION()) ;
				result.setData("MEDI_QTY", i, sendValue.getOrderDetail().get(i).getMEDIQTY()) ;//开药数量
				result.setData("MEDI_UNIT", i, sendValue.getOrderDetail().get(i).getMEDIUNIT()) ;
				result.setData("FREQ_CODE", i, sendValue.getOrderDetail().get(i).getFREQCODE()) ;
				result.setData("ROUTE_CODE", i, sendValue.getOrderDetail().get(i).getROUTECODE()) ;//
				result.setData("DOSAGE_QTY", i, sendValue.getOrderDetail().get(i).getDOSAGEQTY()) ;//配药数量
				result.setData("DISPENSE_UNIT", i, sendValue.getOrderDetail().get(i).getDOSAGEUNIT()) ;//
				result.setData("ORDER_DATE", i, sendValue.getOrderDetail().get(i).getORDERDTTM()) ;//
				result.setData("ATC_FLG", i, sendValue.getOrderDetail().get(i).getSENDACTFLG()) ;//是否送包药机
				result.setData("DR_NOTE", i, sendValue.getOrderDetail().get(i).getDRNOTE()) ;//医生备注
				//医嘱主项
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
	 * 解析请领数据，his 的 009方法
	 * 同步药房向药库请领数据
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
				resultMain.addData("APP_ORG_CODE", sendValue.getRequestHis().get(j).getAPPORGCODE());//申请部门代码
				resultMain.addData("DRUG_CATEGORY", sendValue.getRequestHis().get(j).getDRUGCATEGORY());//申请药品种类
				resultMain.addData("OPT_DATE", sendValue.getRequestHis().get(j).getOPTDATE());//操作时间
				resultMain.addData("OPT_TERM", sendValue.getRequestHis().get(j).getOPTTERM());//操作端末IP
				resultMain.addData("OPT_USER", sendValue.getRequestHis().get(j).getOPTUSER());//操作人员
				resultMain.addData("REASON_CHN_DESC", sendValue.getRequestHis().get(j).getREASONCHNDESC());//申请原因
				resultMain.addData("REGION_CODE", sendValue.getRequestHis().get(j).getREGIONCODE());//院区代码
				resultMain.addData("REQTYPE_CODE", sendValue.getRequestHis().get(j).getREQTYPECODE());//申请类别
				resultMain.addData("REQUEST_DATE", sendValue.getRequestHis().get(j).getREQUESTDATE());//申请日期
				resultMain.addData("REQUEST_NO", sendValue.getRequestHis().get(j).getREQUESTNO());//申请单号
				resultMain.addData("REQUEST_USER", sendValue.getRequestHis().get(j).getREQUESTUSER());//申请人员
				resultMain.addData("TO_ORG_CODE", sendValue.getRequestHis().get(j).getTOORGCODE());//接受部门代码
				resultMain.addData("URGENT_FLG", sendValue.getRequestHis().get(j).getURGENTFLG());//是否紧急
				resultMain.addData("UNIT_TYPE", sendValue.getRequestHis().get(j).getUNITTYPE());//单位类别
				for(int i=0;i<sendValue.getRequestHis().get(j).getRequestDetail().size();i++){
					//  ====请领数据主项
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
	 * 解析药品his字典信息，his  011方法
	 * 
	 * @return
	 */
	public static TParm createHisxml011toParm(String sendString){
		TParm  result = new  TParm() ;
		try {
			StringReader read = new StringReader(sendString) ;
			Prescription sendValue = JAXB.unmarshal(read, Prescription.class) ;
			for(int i=0;i<sendValue.getHisPhaBase().size();i++){
				//  ====医嘱细项
				result.setData("ALIAS_DESC", i, sendValue.getHisPhaBase().get(i).getALIASDESC()) ;//商品名
				result.setData("DOSE_CODE", i, sendValue.getHisPhaBase().get(i).getDOSECODE()) ;//剂型
				result.setData("DOSAGE_QTY", i, sendValue.getHisPhaBase().get(i).getDOSRATIO()) ;//配药单位转换率
				result.setData("DOSAGE_UNIT", i, sendValue.getHisPhaBase().get(i).getDOSUNIT()) ;//配药单位代码
				result.setData("ORDER_CODE", i, sendValue.getHisPhaBase().get(i).getDRUGCODE()) ;//药品代码
				result.setData("ORDER_DESC", i, sendValue.getHisPhaBase().get(i).getDRUGDESC()) ;//药品名称
				result.setData("ACTIVE_FLG", i, sendValue.getHisPhaBase().get(i).getISACTIVE()) ;//是否启用
				result.setData("MAN_CODE", i, sendValue.getHisPhaBase().get(i).getMANUF()) ;//生产厂商
				result.setData("PHA_TYPE", i, sendValue.getHisPhaBase().get(i).getPHATYPE()) ;//药品分类
				result.setData("STOCK_PRICE", i, sendValue.getHisPhaBase().get(i).getPURPRICE()) ;//采购价(片)
				result.setData("PURCH_QTY", i, sendValue.getHisPhaBase().get(i).getPURRATIO()) ;//采购单位转换率
				result.setData("PURCH_UNIT", i, sendValue.getHisPhaBase().get(i).getPURUNIT()) ;//采购单位代码
				result.setData("PY1", i, sendValue.getHisPhaBase().get(i).getPYCODE()) ;//拼音码
				result.setData("RETAIL_PRICE", i, sendValue.getHisPhaBase().get(i).getRTLPRICE()) ;//零售价
				result.setData("SPECIFICATION", i, sendValue.getHisPhaBase().get(i).getSPEC()) ;//规格
				result.setData("STOCK_QTY", i, sendValue.getHisPhaBase().get(i).getSTKRATIO()) ;//库存单位转换率
				result.setData("STOCK_UNIT", i, sendValue.getHisPhaBase().get(i).getSTKUNIT()) ;//库存单位代码
				result.setData("CTRLDRUGCLASS_CODE", i, sendValue.getHisPhaBase().get(i).getTOXCODE()) ;//麻精
				result.setData("CAT1_TYPE", i, "PHA") ;//药品分类
				if(sendValue.getHisPhaBase().get(i).getTOXCODE().length()>0)
				result.setData("CTRL_FLG", i, "Y") ;//麻精标记
				else
					result.setData("CTRL_FLG", i, "N") ;//麻精标记
				if(sendValue.getHisPhaBase().get(i).getPHATYPE().equals("W"))
				result.setData("ORDER_CAT1_CODE", i, "PHA_W") ;//药品细分类
				else result.setData("ORDER_CAT1_CODE", i, "PHA_C") ;//药品细分类
			}
		
		} catch (Exception e) {
			System.out.println("err======"+e.toString());
			result.setErrCode(-1) ;
			return result ;
		}	
		return result ;
	}
	/**
	 * 解析科室his字典信息，his  012方法
	 * 
	 * @return
	 */
	public static TParm createHisxml012toParm(String sendString){
		TParm  result = new  TParm() ;
		try {
			StringReader read = new StringReader(sendString) ;
			Prescription sendValue = JAXB.unmarshal(read, Prescription.class) ;
			for(int i=0;i<sendValue.getHisDept().size();i++){
				//  ====医嘱细项
				result.setData("COST_CENTER_CODE", i, sendValue.getHisDept().get(i).getCOSTCENTER()) ;//成本中心代号
				result.setData("DEPT_CODE", i, sendValue.getHisDept().get(i).getDEPTCODE()) ;//科室代号
				result.setData("DEPT_CHN_DESC", i, sendValue.getHisDept().get(i).getDEPTNAME()) ;//科室名称
				result.setData("ACTIVE_FLG", i, sendValue.getHisDept().get(i).getISACTIVE()) ;//是否启用
			}
		
		} catch (Exception e) {
			System.out.println("err======"+e.toString());
			result.setErrCode(-1) ;
			return result ;
		}	
		return result ;
	}
	/**
	 * 物联网将出库信息同步HIS
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
				out.append("<saleBillCode>"+parmM.getValue("DISPENSE_NO", j)+"</saleBillCode>");//物联网的供货单号
				out.append("<receBillCode>"+parmM.getValue("REQUEST_NO", j)+"</receBillCode>");//申请单号
				out.append("<depCode>"+parmM.getValue("APP_ORG_CODE", j)+"</depCode>");//申请科室
				if(parmM.getValue("DISPENSE_DATE", j).length()>0)
				out.append("<confirmDate>"+parmM.getValue("DISPENSE_DATE", j).substring(0, 19)+"</confirmDate>");//发药时间	
				else 
				out.append("<confirmDate></confirmDate>");//发药时间	
				out.append("<remark></remark>");//备注
				out.append("<drugParticular>");	
				for(int i = 0 ; i <parmD.getCount() ; i++ ){
					out.append("<drug " +
							" rowNum=\""+parmD.getValue("REQUEST_SEQ", i)+"\"" +//行号（原申请单明细序号）
							" drugID=\""+parmD.getValue("ORDER_CODE", i)+"\"" +//HIS中的药品ID
							" factoryID=\"\"" +//生产厂商ID
							" batchNum=\""+parmD.getValue("BATCH_NO", i)+"\"" +//药品批号
							" batchLot=\"\"" +//药品批次
							" authorNum=\"\"" +//批准文号
							" regiNum=\"\"" +//注册证号
							" produceDate=\"\"" +//生产日期
							" usefulDate=\""+parmD.getValue("VALID_DATE", i).substring(0, 10)+"\"" +//有效期
							" unit=\""+parmD.getValue("UNIT_CODE", i)+"\"" +//单位
							" factQuantity=\""+parmD.getValue("QTY", i)+"\"" +//发货数量
							" price=\""+parmD.getValue("INVENT_PRICE", i)+"\"" +//发货价格
							" retailPrice=\""+parmD.getValue("RETAIL_PRICE", i)+"\"" +//参考零售价格
							" repLibFlag=\""+parmD.getValue("REPLIBFLAG", i)+"\"" +//是否需要医院与供应商结算 0:代管药品（不结算） 1:（需结算
							" supCode=\""+parmD.getValue("SUP_CODE", i)+"\"" +//生产厂商ID
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
