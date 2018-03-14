package action.opd;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jdo.opd.client.IOpdOrderService_OpdOrderServicePort_Client;
import jdo.opd.client.OpdOrder;
import jdo.opd.client.OpdOrderList;
import jdo.spc.SPCSQL;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
/**
 * 
 * <p>
 * Title: 医生站、门诊收费物联网收费药品医嘱添加
 * </p>
 * 
 * <p>
 * Description:医生站、门诊收费物联网收费药品医嘱添加
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author pangben 2013.5.20
 * @version 1.0
 */
public class OpdOrderSpcCAction extends TAction{
	 /**
	 * 操作添加物联网药品
	 * @param parm
	 * @return
	 */
	public TParm saveSpcOpdOrder(TParm parm) {
		String message="";
		TParm result=new TParm();
		String rxNo=parm.getValue("SUM_RX_NO");
		IOpdOrderService_OpdOrderServicePort_Client client =new IOpdOrderService_OpdOrderServicePort_Client();
		List<OpdOrder>  order =new ArrayList<OpdOrder>();
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			order.add(getOpdOrder(parm.getRow(i)));	
		}
		OpdOrderList opdOrderList=new OpdOrderList();
		opdOrderList.setOpdOrderList(order);
		opdOrderList.setRxNo(rxNo);
		message=client.saveOpdOrder(opdOrderList);
		if(!message.equals("SUCCESS")){//返回消息
			result.setErr(-1,message);
			return result;
		}
		return result;
	}
	/**
	 * 获得医嘱属性对象
	 * @param parm
	 * @return
	 */
	private OpdOrder getOpdOrder(TParm parm){
		OpdOrder opd=new OpdOrder();
		getOpdOrderParm(opd, parm);
		return opd;
	}
	/**
	 * 医嘱全部参数，门诊使用，门急诊医生站\门诊收费
	 * @param parm
	 * @param order
	 */
	private void getOpdOrderParm(jdo.opd.client.OpdOrder order,TParm parm) {
		//DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		order.setCASENO(parm.getValue("CASE_NO"));
		order.setRXNO(parm.getValue("RX_NO"));
		order.setSEQNO(parm.getInt("SEQ_NO"));
		order.setPRESRTNO(parm.getValue("PRESRT_NO"));
		order.setREGIONCODE(parm.getValue("REGION_CODE"));
		order.setMRNO(parm.getValue("MR_NO"));
		order.setADMTYPE(parm.getValue("ADM_TYPE"));
		order.setRXTYPE(parm.getValue("RX_TYPE"));
		order.setTEMPORARYFLG(parm.getValue("TEMPORARY_FLG"));
		order.setRELEASEFLG(parm.getValue("RELEASE_FLG"));
		order.setLINKMAINFLG(parm.getValue("LINKMAIN_FLG"));
		order.setLINKNO(parm.getValue("LINK_NO"));
		order.setORDERCODE(parm.getValue("ORDER_CODE"));
		order.setORDERDESC(parm.getValue("ORDER_DESC"));
		order.setGOODSDESC(parm.getValue("GOODS_DESC"));
		order.setSPECIFICATION(parm.getValue("SPECIFICATION"));
		order.setORDERCAT1CODE(parm.getValue("ORDER_CAT1_CODE"));
		order.setMEDIQTY(parm.getDouble("MEDI_QTY"));
		order.setMEDIUNIT(parm.getValue("MEDI_UNIT"));
		order.setFREQCODE(parm.getValue("FREQ_CODE"));
		order.setROUTECODE(parm.getValue("ROUTE_CODE"));
		order.setTAKEDAYS(parm.getInt("TAKE_DAYS"));
		order.setDOSAGEQTY(parm.getDouble("DOSAGE_QTY"));
		order.setDOSAGEUNIT(parm.getValue("DOSAGE_UNIT"));
		order.setDISPENSEQTY(parm.getDouble("DISPENSE_QTY"));
		order.setDISPENSEUNIT(parm.getValue("DISPENSE_UNIT"));
		order.setGIVEBOXFLG(parm.getValue("GIVEBOX_FLG"));
		order.setOWNPRICE(parm.getDouble("OWN_PRICE"));
		order.setNHIPRICE(parm.getDouble("NHI_PRICE"));
		order.setDISCOUNTRATE(parm.getDouble("DISCOUNT_RATE"));
		order.setOWNAMT(parm.getDouble("OWN_AMT"));
		order.setARAMT(parm.getDouble("AR_AMT"));
		order.setDRNOTE(parm.getValue("DR_NOTE"));
		order.setNSNOTE(parm.getValue("NS_NOTE"));
		order.setDRCODE(parm.getValue("DR_CODE"));
		order.setORDERDATE(SystemTool.getInstance().getDateReplace(parm.getValue("ORDER_DATE"), true).toString());
		order.setDEPTCODE(parm.getValue("DEPT_CODE"));
		order.setDCDRCODE(parm.getValue("DC_DR_CODE"));
		order.setDCORDERDATE(SystemTool.getInstance().getDateReplace(parm.getValue("DC_ORDER_DATE"), true).toString());
		order.setDCDEPTCODE(parm.getValue("DC_DEPT_CODE"));
		order.setEXECDEPTCODE(parm.getValue("EXEC_DEPT_CODE"));
		order.setEXECDRCODE(parm.getValue("EXEC_DR_CODE"));
		order.setSETMAINFLG(parm.getValue("SETMAIN_FLG"));
		order.setORDERSETGROUPNO(parm.getDouble("ORDERSET_GROUP_NO"));
		order.setORDERSETCODE(parm.getValue("ORDERSET_CODE"));
		order.setHIDEFLG(parm.getValue("HIDE_FLG"));
		order.setRPTTYPECODE(parm.getValue("RPTTYPE_CODE"));
		order.setOPTITEMCODE(parm.getValue("OPTITEM_CODE"));
		order.setDEVCODE(parm.getValue("DEV_CODE"));
		order.setMRCODE(parm.getValue("MR_CODE"));
		order.setFILENO(parm.getDouble("FILE_NO"));
		order.setDEGREECODE(parm.getValue("DEGREE_CODE"));
		order.setURGENTFLG(parm.getValue("URGENT_FLG"));
		order.setINSPAYTYPE(parm.getValue("INSPAY_TYPE"));
		order.setPHATYPE(parm.getValue("PHA_TYPE"));
		order.setDOSETYPE(parm.getValue("DOSE_TYPE"));
		order.setEXPENSIVEFLG(parm.getValue("EXPENSIVE_FLG"));
		order.setPRINTTYPEFLGINFANT(parm.getValue("PRINTTYPEFLG_INFANT"));
		order.setCTRLDRUGCLASSCODE(parm.getValue("CTRLDRUGCLASS_CODE"));
		order.setPRESCRIPTNO(parm.getDouble("PRESCRIPT_NO"));
		order.setATCFLG(parm.getValue("ATC_FLG"));
		order.setSENDATCDATE(SystemTool.getInstance().getDateReplace(parm.getValue("SENDATC_DATE"), true).toString());
		order.setRECEIPTNO(parm.getValue("RECEIPT_NO"));
		order.setBILLFLG(parm.getValue("BILL_FLG"));
		order.setBILLDATE(SystemTool.getInstance().getDateReplace(parm.getValue("BILL_DATE"), true).toString());
		order.setBILLUSER(parm.getValue("BILL_USER"));
		order.setPRINTFLG(parm.getValue("PRINT_FLG"));
		order.setREXPCODE(parm.getValue("REXP_CODE"));
		order.setHEXPCODE(parm.getValue("HEXP_CODE"));
		order.setCONTRACTCODE(parm.getValue("CONTRACT_CODE"));
		order.setCTZ1CODE(parm.getValue("CTZ1_CODE"));
		order.setCTZ2CODE(parm.getValue("CTZ2_CODE"));
		order.setCTZ3CODE(parm.getValue("CTZ3_CODE"));
		order.setPHACHECKCODE(parm.getValue("PHA_CHECK_CODE"));
		order.setPHACHECKDATE(SystemTool.getInstance().getDateReplace(parm.getValue("PHA_CHECK_DATE"), true).toString());
		order.setPHADOSAGECODE(parm.getValue("PHA_DOSAGE_CODE"));
		order.setPHADOSAGEDATE(SystemTool.getInstance().getDateReplace(parm.getValue("PHA_DOSAGE_DATE"), true).toString());
		order.setPHADISPENSECODE(parm.getValue("PHA_DISPENSE_CODE"));
		order.setPHADISPENSEDATE(SystemTool.getInstance().getDateReplace(parm.getValue("PHA_DISPENSE_DATE"), true).toString());
		order.setPHARETNCODE(parm.getValue("PHA_RETN_CODE"));
		order.setPHARETNDATE(SystemTool.getInstance().getDateReplace(parm.getValue("PHA_RETN_DATE"), true).toString());
		order.setNSEXECCODE(parm.getValue("NS_EXEC_CODE"));
		order.setNSEXECDATE(SystemTool.getInstance().getDateReplace(parm.getValue("NS_EXEC_DATE"), true).toString());
		order.setNSEXECDEPT(parm.getValue("NS_EXEC_DEPT"));
		order.setDCTAGENTCODE(parm.getValue("DCTAGENT_CODE"));
		order.setDCTEXCEPCODE(parm.getValue("DCTEXCEP_CODE"));
		order.setDCTTAKEQTY(parm.getDouble("DCT_TAKE_QTY"));
		order.setPACKAGETOT(parm.getDouble("PACKAGE_TOT"));
		order.setAGENCYORGCODE(parm.getValue("AGENCY_ORG_CODE"));
		order.setDCTAGENTFLG(parm.getValue("DCTAGENT_FLG"));
		order.setDECOCTCODE(parm.getValue("DECOCT_CODE"));
		order.setREQUESTFLG(parm.getValue("REQUEST_FLG"));
		order.setREQUESTNO(parm.getValue("REQUEST_NO"));
		order.setOPTUSER(parm.getValue("OPT_USER"));
		order.setOPTTERM(parm.getValue("OPT_TERM"));
		order.setMEDAPPLYNO(parm.getValue("MED_APPLY_NO"));
		order.setCAT1TYPE(parm.getValue("CAT1_TYPE"));
		order.setTRADEENGDESC(parm.getValue("TRADE_ENG_DESC"));
		order.setPRINTNO(parm.getValue("PRINT_NO"));
		order.setCOUNTERNO(parm.getDouble("COUNTER_NO"));
		order.setPSYFLG(parm.getValue("PSY_FLG"));
		order.setEXECFLG(parm.getValue("EXEC_FLG"));
		order.setRECEIPTFLG(parm.getValue("RECEIPT_FLG"));
		order.setBILLTYPE(parm.getValue("BILL_TYPE"));
		order.setFINALTYPE(parm.getValue("FINAL_TYPE"));
		order.setDECOCTREMARK(parm.getValue("DECOCT_REMARK"));
		order.setSENDDCTUSER(parm.getValue("SEND_DCT_USER"));
		order.setSENDDCTDATE(SystemTool.getInstance().getDateReplace(parm.getValue("SEND_DCT_DATE"), true).toString());
		order.setDECOCTUSER(parm.getValue("DECOCT_USER"));
		order.setDECOCTDATE(SystemTool.getInstance().getDateReplace(parm.getValue("DECOCT_DATE"), true).toString());
		order.setSENDORGUSER(parm.getValue("SEND_ORG_USER"));
		order.setSENDORGDATE(SystemTool.getInstance().getDateReplace(parm.getValue("SEND_ORG_DATE"), true).toString());		
		order.setEXMEXECENDDATE(SystemTool.getInstance().getDateReplace(parm.getValue("EXM_EXEC_END_DATE"), true).toString());
		order.setEXECDRDESC(parm.getValue("EXEC_DR_DESC"));
		order.setCOSTAMT(parm.getDouble("COST_AMT"));
		order.setCOSTCENTERCODE(parm.getValue("COST_CENTER_CODE"));
		order.setBATCHSEQ1(parm.getDouble("BATCH_SEQ1"));
		order.setVERIFYINPRICE1(parm.getDouble("VERIFYIN_PRICE1"));
		order.setBATCHSEQ2(parm.getDouble("BATCH_SEQ2"));
		order.setVERIFYINPRICE2(parm.getDouble("VERIFYIN_PRICE2"));
		order.setDISPENSEQTY2(parm.getDouble("DISPENSE_QTY2"));
		order.setVERIFYINPRICE3(parm.getDouble("VERIFYIN_PRICE3"));
		order.setDISPENSEQTY1(parm.getDouble("DISPENSE_QTY1"));
		order.setBATCHSEQ3(parm.getDouble("BATCH_SEQ3"));
		order.setDISPENSEQTY3(parm.getDouble("DISPENSE_QTY3"));
		order.setBUSINESSNO(parm.getValue("BUSINESS_NO"));
		order.setPATNAME(parm.getValue("PAT_NAME"));
		order.setSEXTYPE(parm.getValue("SEX_TYPE"));
		order.setBIRTHDATE(SystemTool.getInstance().getDateReplace(parm.getValue("BIRTH_DATE"), true).toString());
	}
}
