package com.javahis.bsm;
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
import javax.xml.bind.Marshaller;

import jdo.sys.Pat;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
public class XmlUtils {
	/**
	 * 方法的实现，将处方对象转换为xml，呼叫201方法时使用（配药）
	 * @return 
	 */

	public static  StringWriter  onCreateXmlDispense(TParm parm){
		
		Pat pat = Pat.onQueryByMrNo(((TParm)parm.getData("Data", "MAIN")).getValue("MR_NO"));
		Prescription prescription = new Prescription() ; //处方对象
		//设置表头
		 prescription.setOPIP(((TParm)parm.getData("Data", "TITLE")).getValue("OPT_TERM")) ;         //操作ip
		 prescription.setOPMANNAME(((TParm)parm.getData("Data", "TITLE")).getValue("OPT_USER")) ;   //操作name
		 prescription.setOPMANNO(((TParm)parm.getData("Data", "TITLE")).getValue("OPT_CODE")) ;     //操作人员编码
		 prescription.setOPTYPE(((TParm)parm.getData("Data", "TITLE")).getValue("WAY")) ;           //操作代码
		 prescription.setOPWINID(((TParm)parm.getData("Data", "TITLE")).getValue("OPWINID")) ;     //操作终端,窗口号
		 //设置处方主表
		 List<PrescriptionTableMain> listMain  = prescription.getMain() ;
		 PrescriptionTableMain main = new PrescriptionTableMain() ;
		 main.setPRESC_DATE(((TParm)parm.getData("Data", "DETAIL")).getValue("ORDER_DATE",0).substring(0, 19)) ;//处方时间
		 main.setPRESC_NO(((TParm)parm.getData("Data", "MAIN")).getValue("RX_NO")) ;   //处方编号
		 main.setRCPT_INFO("") ;//诊断信息???????????????????????????
		 main.setPRESCRIBED_BY(((TParm)parm.getData("Data", "MAIN")).getValue("DR_NAME")) ;//开方医生
		 main.setPRESCRIBED_ID(((TParm)parm.getData("Data", "MAIN")).getValue("DR_CODE")) ;//开方医生代码
		 main.setCHARGE_TYPE("") ;//  医保类型====?????????????????????????
		 main.setCOSTS(((TParm)parm.getData("Data", "MAIN")).getDouble("SUM_FEE")+"") ;       //费用
		 main.setDATE_OF_BIRTH(pat.getBirthday().toString()) ;//患者出生日期
		 main.setDISPENSARY("门急诊药房") ;//发药药局
		 main.setDISPENSE_PRI("") ;//配药优先级?????????????????????/////
		 main.setENTERED_BY(((TParm)parm.getData("Data", "MAIN")).getValue("DR_NAME")) ;//录方人
		 main.setORDERED_BY(((TParm)parm.getData("Data", "MAIN")).getValue("DEPT_DESC")) ;//开单科室
		 main.setORDERED_ID(((TParm)parm.getData("Data", "MAIN")).getValue("DEPT_CODE")) ;//开单科室代码
		 main.setPATIENT_ID(((TParm)parm.getData("Data", "MAIN")).getValue("MR_NO")) ;//就诊卡号
		 main.setPATIENT_NAME(((TParm)parm.getData("Data", "MAIN")).getValue("PAT_NAME")) ;//患者姓名
		 main.setPATIENT_TYPE("00") ;//患者类型
		 main.setPAYMENTS(((TParm)parm.getData("Data", "MAIN")).getDouble("SUM_FEE")+"");//实付费用
		 main.setPRESC_ATTR("") ;//处方属性?????????????????????????????????????????
		 main.setPRESC_IDENTITY(((TParm)parm.getData("Data", "DETAIL")).getValue("CTZ1_CODE",0)) ;//患者身份
		 main.setPRESC_INFO(onRxtype(((TParm)parm.getData("Data", "MAIN")).getValue("RX_TYPE"))) ;//处方类型(计费方式)
		 main.setRCPT_REMARK("") ;//处方备注???????????????????????????????
		 main.setREPETITION("") ;//剂数      ?????????????????????????????????
		 main.setSEX(pat.getSexString()) ;//患者性别
		 listMain.add(main) ;
		 //设置处方细表
		 List<PrescriptionTableDetail> detailList = main.getDetail() ;
		 PrescriptionTableDetail detail = null  ;
		 for(int i=0;i<((TParm)parm.getData("Data", "DETAIL")).getCount();i++){
			 detail = new PrescriptionTableDetail() ;
			 detail.setADMINISTRATION(((TParm)parm.getData("Data", "DETAIL")).getValue("ROUTE_CODE", i)) ;//药品用法
			 detail.setADVICE_CODE(((TParm)parm.getData("Data", "DETAIL")).getValue("SEQ_NO", i)) ;//医嘱编号
			 detail.setCOSTS(((TParm)parm.getData("Data", "DETAIL")).getDouble("OWN_AMT", i)+"") ;//费用
			 detail.setDOSAGE(((TParm)parm.getData("Data", "DETAIL")).getValue("MEDI_QTY", i)) ;//药品剂量
			 detail.setDOSAGE_UNITS(((TParm)parm.getData("Data", "DETAIL")).getValue("MEDI_UNIT", i)) ;//剂量单位
			 detail.setDRUG_CODE(((TParm)parm.getData("Data", "DETAIL")).getValue("BAR_CODE", i)) ;//药品编号HIS
			 detail.setBAT_CODE(((TParm)parm.getData("Data", "DETAIL")).getValue("ORDER_CODE", i)) ;//药品编号
			 detail.setDRUG_NAME(((TParm)parm.getData("Data", "DETAIL")).getValue("ORDER_DESC", i)) ;//药品名称
			 detail.setFLG(((TParm)parm.getData("Data", "DETAIL")).getValue("FLG", i)) ;//包药机是否包药标记
			 detail.setDRUG_PACKAGE(((TParm)parm.getData("Data", "DETAIL")).getValue("SPECIFICATION", i)) ;//药品包装规格
			 detail.setDRUG_PRICE(((TParm)parm.getData("Data", "DETAIL")).getDouble("OWN_PRICE", i)+"") ;//药品价格
			 detail.setDRUG_SPEC(((TParm)parm.getData("Data", "DETAIL")).getValue("SPECIFICATION", i)) ;//药品规格
			 detail.setDRUG_UNIT(((TParm)parm.getData("Data", "DETAIL")).getValue("DISPENSE_UNIT", i)) ;//药品单位
			 detail.setQUANTITY(((TParm)parm.getData("Data", "DETAIL")).getValue("DISPENSE_QTY", i)) ;//数量
			 detail.setPRESC_NO(((TParm)parm.getData("Data", "DETAIL")).getValue("RX_NO", i)) ;///处方编号
			 detail.setPRESC_DATE(((TParm)parm.getData("Data", "DETAIL")).getValue("ORDER_DATE", i).substring(0, 19)) ;//处方时间
			 detail.setPAYMENTS(((TParm)parm.getData("Data", "DETAIL")).getDouble("OWN_AMT", i)+"");//	实付费用
			 detail.setITEM_NO(((TParm)parm.getData("Data", "DETAIL")).getValue("SEQ_NO", i)) ;//药品序号
			 detail.setFREQUENCY(((TParm)parm.getData("Data", "DETAIL")).getValue("FREQ_DESC", i)) ;//	药品用量
			 detail.setTRADE_NAME(((TParm)parm.getData("Data", "DETAIL")).getValue("GOODS_DESC", i)) ;//药品商品名
			 detail.setFIRM_ID(((TParm)parm.getData("Data", "DETAIL")).getValue("FIRM_ID", i)) ;//药品厂家
			 detail.setDISPENSE_DAYS(((TParm)parm.getData("Data", "DETAIL")).getValue("TAKE_DAYS", i)) ;   //摆药天数
			 detail.setDISPENSEDTOTALDOSE(((TParm)parm.getData("Data", "DETAIL")).getValue("DISPENSE_QTY", i)) ;//摆药量总数
			 detail.setDISPENSEDUNIT(((TParm)parm.getData("Data", "DETAIL")).getValue("DISPENSE_UNIT", i)) ;//摆药单位
			 detail.setAMOUNT_PER_PACKAGE(((TParm)parm.getData("Data", "DETAIL")).getValue("QTY_BUONE", i));//一片的剂量 (数值)
			double qty = Double.parseDouble(((TParm)parm.getData("Data", "DETAIL")).getValue("MEDI_QTY", i));
			double qtybyone = Double.parseDouble(((TParm)parm.getData("Data", "DETAIL")).getValue("QTY_BUONE", i));
			 detail.setDISPESEDDOSE(qty/qtybyone+"");//一包里的摆药量
			 detail.setFREQ_DESC_DETAIL(((TParm)parm.getData("Data", "DETAIL")).getValue("FREQ_DESC_DETAIL", i)) ;//服用时间
			 detail.setFREQ_DESC_DETAIL_CODE(((TParm)parm.getData("Data", "DETAIL")).getValue("FREQ_DESC_DETAIL_CODE", i)) ;//服用时间编码
			 detail.setATC_FLG(((TParm)parm.getData("Data", "DETAIL")).getValue("ATC_FLG", i)) ;//区分是否送口服包药机的标志
			 detailList.add(detail) ;
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
		System.out.println("send=============="+sendString);
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
	public static void main(String[] args){
		String sendString = "<ROOT>" +
		        "<CONSIS_STORAGE_APPVW> " +
				"<BARCODE>12345</BARCODE>" +
				"<APPQUANTITY>10</APPQUANTITY>" +
				"<CURQUANTITY>10</CURQUANTITY>" +
				 "</CONSIS_STORAGE_APPVW> " +
				 "<CONSIS_STORAGE_APPVW> " +
					"<BARCODE>12345</BARCODE>" +
					"<APPQUANTITY>10</APPQUANTITY>" +
					"<CURQUANTITY>10</CURQUANTITY>" +
					 "</CONSIS_STORAGE_APPVW> " +
				"</ROOT>" ;
		
	TParm parm = 	createSendxml305toParm(sendString) ;
	System.out.println("parm======1======"+parm);
	}
	/**
	 * 解析对象，将对象转化为xml,测试专用
	 * @return
	 */
	public static StringWriter createSendParmtoXml(){
		TParm  result = new  TParm() ;
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
	
	
	
}
