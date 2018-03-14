package jdo.opd.ws;

import javax.jws.WebService;

import jdo.opd.OrderTool;

import com.dongyang.data.TParm;

@WebService
public class OpdOrderService implements IOpdOrderService {

	@Override
	public String saveOpdOrder(OpdOrderList opdOrderList) {
		String message=OrderTool.getInstance().deleteOpdOrderPhaSpc(opdOrderList.getRxNo());
		if(!message.equals("SUCCESS"))
			return message;
		OpdOrder order=null;
		
		for (int i = 0; i < opdOrderList.getOpdOrderList().size(); i++) {
			order=opdOrderList.getOpdOrderList().get(i);
			message=OrderTool.getInstance().insertOpdOrderPhaSpc(getOpdOrderParm(order));
			if(!message.equals("SUCCESS"))
				return message;
		}
		return "SUCCESS";
	}
	/**
	 * 医嘱全部参数，门诊使用，门急诊医生站\门诊收费
	 * @param parm
	 * @param order
	 */
	private TParm getOpdOrderParm(OpdOrder order) {
		TParm parm=new TParm();
		parm.setData("CASE_NO", order.getCASE_NO());
		parm.setData("RX_NO", order.getRX_NO());
		parm.setData("SEQ_NO", order.getSEQ_NO());
		parm.setData("PRESRT_NO", order.getPRINT_NO());
		parm.setData("REGION_CODE", order.getREGION_CODE());
		parm.setData("MR_NO", order.getMR_NO());
		parm.setData("ADM_TYPE", order.getADM_TYPE());
		parm.setData("RX_TYPE", order.getRX_TYPE());
		parm.setData("TEMPORARY_FLG", order.getTEMPORARY_FLG());
		parm.setData("RELEASE_FLG", order.getRELEASE_FLG());
		parm.setData("LINKMAIN_FLG", order.getLINKMAIN_FLG());
		parm.setData("LINK_NO", order.getLINK_NO());
		
		/**编码对照
		//====查询物联网医嘱编码
		String sql=SPCSQL.getSpcOrderCodeByHisOrderCode(null, null, order.getORDER_CODE());
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount()<=0) {
			System.out.println("OpdOrderService.java getOpdOrderParm 方法物联网校验医嘱码sql::::"+sql);
		}
		parm.setData("ORDER_CODE", result.getValue("ORDER_CODE",0));
		*/
		parm.setData("ORDER_CODE", order.getORDER_CODE());
		parm.setData("ORDER_DESC", order.getORDER_DESC());
		parm.setData("GOODS_DESC", order.getGOODS_DESC());
		parm.setData("SPECIFICATION", order.getSPECIFICATION());
		parm.setData("ORDER_CAT1_CODE", order.getORDER_CAT1_CODE());
		parm.setData("MEDI_QTY", order.getMEDI_QTY());
		parm.setData("MEDI_UNIT", order.getMEDI_UNIT());
		parm.setData("FREQ_CODE", order.getFREQ_CODE());
		parm.setData("ROUTE_CODE", order.getROUTE_CODE());
		parm.setData("TAKE_DAYS", order.getTAKE_DAYS());
		parm.setData("DOSAGE_QTY", order.getDOSAGE_QTY());
		parm.setData("DOSAGE_UNIT", order.getDOSAGE_UNIT());
		parm.setData("DISPENSE_QTY", order.getDISPENSE_QTY());
		parm.setData("DISPENSE_UNIT", order.getDISPENSE_UNIT());
		parm.setData("GIVEBOX_FLG", order.getGIVEBOX_FLG());
		parm.setData("OWN_PRICE", order.getOWN_PRICE());
		parm.setData("NHI_PRICE", order.getNHI_PRICE());
		parm.setData("DISCOUNT_RATE", order.getDISCOUNT_RATE());
		parm.setData("OWN_AMT", order.getOWN_AMT());
		parm.setData("AR_AMT", order.getAR_AMT());
		parm.setData("DR_NOTE", order.getDR_CODE());
		parm.setData("NS_NOTE", order.getNS_NOTE());
		parm.setData("DR_CODE", order.getDR_CODE());
		parm.setData("ORDER_DATE", order.getORDER_DATE());
		parm.setData("DEPT_CODE", order.getDEPT_CODE());
		parm.setData("DC_DR_CODE", order.getDC_DR_CODE());
		parm.setData("DC_ORDER_DATE", order.getDC_ORDER_DATE());
		parm.setData("DC_DEPT_CODE", order.getDC_DEPT_CODE());
		parm.setData("EXEC_DEPT_CODE", order.getEXEC_DEPT_CODE());
		parm.setData("EXEC_DR_CODE", order.getEXEC_DR_CODE());
		parm.setData("SETMAIN_FLG", order.getSETMAIN_FLG());
		parm.setData("ORDERSET_GROUP_NO", order.getORDERSET_GROUP_NO());
		parm.setData("ORDERSET_CODE", order.getORDERSET_CODE());
		parm.setData("HIDE_FLG", order.getHIDE_FLG());
		parm.setData("RPTTYPE_CODE", order.getRPTTYPE_CODE());
		parm.setData("OPTITEM_CODE", order.getOPTITEM_CODE());
		parm.setData("DEV_CODE", order.getDEV_CODE());
		parm.setData("MR_CODE", order.getMR_CODE());
		parm.setData("FILE_NO", order.getFILE_NO());
		parm.setData("DEGREE_CODE", order.getDECOCT_CODE());
		parm.setData("URGENT_FLG", order.getURGENT_FLG());
		parm.setData("INSPAY_TYPE", order.getINSPAY_TYPE());
		parm.setData("PHA_TYPE", order.getPHA_TYPE());
		parm.setData("DOSE_TYPE", order.getDOSE_TYPE());
		parm.setData("EXPENSIVE_FLG", order.getEXPENSIVE_FLG());
		parm.setData("PRINTTYPEFLG_INFANT", order.getPRINTTYPEFLG_INFANT());
		parm.setData("CTRLDRUGCLASS_CODE", order.getCTRLDRUGCLASS_CODE());
		parm.setData("PRESCRIPT_NO", order.getPRESCRIPT_NO());
		parm.setData("ATC_FLG", order.getATC_FLG());
		parm.setData("SENDATC_DATE", order.getSENDATC_DATE());
		parm.setData("RECEIPT_NO", order.getRECEIPT_NO());
		parm.setData("BILL_FLG", order.getBILL_FLG());
		parm.setData("BILL_DATE", order.getBILL_DATE());
		parm.setData("BILL_USER", order.getBILL_USER());
		parm.setData("PRINT_FLG", order.getPRINT_FLG());
		parm.setData("REXP_CODE", order.getREXP_CODE());
		parm.setData("HEXP_CODE", order.getHEXP_CODE());
		parm.setData("CONTRACT_CODE", order.getCONTRACT_CODE());
		parm.setData("CTZ1_CODE", order.getCTZ1_CODE());
		parm.setData("CTZ2_CODE", order.getCTZ2_CODE());
		parm.setData("CTZ3_CODE", order.getCTZ3_CODE());
		parm.setData("PHA_CHECK_CODE", order.getPHA_CHECK_CODE());
		parm.setData("PHA_CHECK_DATE", order.getPHA_CHECK_DATE());
		parm.setData("PHA_DOSAGE_CODE", order.getPHA_DOSAGE_CODE());
		parm.setData("PHA_DOSAGE_DATE", order.getPHA_DOSAGE_DATE());
		parm.setData("PHA_DISPENSE_CODE", order.getPHA_DISPENSE_CODE());
		parm.setData("PHA_DISPENSE_DATE", order.getPHA_DISPENSE_DATE());
		parm.setData("PHA_RETN_CODE", order.getPHA_RETN_CODE());
		parm.setData("PHA_RETN_DATE", order.getPHA_RETN_DATE());
		parm.setData("NS_EXEC_CODE", order.getNS_EXEC_CODE());
		parm.setData("NS_EXEC_DATE", order.getNS_EXEC_DATE());
		parm.setData("NS_EXEC_DEPT", order.getNS_EXEC_DEPT());
		parm.setData("DCTAGENT_CODE", order.getDCTAGENT_CODE());
		parm.setData("DCTEXCEP_CODE", order.getDCTEXCEP_CODE());
		parm.setData("DCT_TAKE_QTY", order.getDCT_TAKE_QTY());
		parm.setData("PACKAGE_TOT", order.getPACKAGE_TOT());
		parm.setData("AGENCY_ORG_CODE", order.getAGENCY_ORG_CODE());
		parm.setData("DCTAGENT_FLG", order.getDCTAGENT_FLG());
		parm.setData("DECOCT_CODE", order.getDECOCT_CODE());
		parm.setData("REQUEST_FLG", order.getREQUEST_FLG());
		parm.setData("REQUEST_NO", order.getREQUEST_NO());
		parm.setData("OPT_USER", order.getOPT_USER());
		parm.setData("OPT_TERM", order.getOPT_TERM());
		parm.setData("MED_APPLY_NO", order.getMED_APPLY_NO());
		parm.setData("CAT1_TYPE", order.getCAT1_TYPE());
		parm.setData("TRADE_ENG_DESC", order.getTRADE_ENG_DESC());
		parm.setData("PRINT_NO", order.getPRINT_NO());
		parm.setData("COUNTER_NO", order.getCOUNTER_NO());
		parm.setData("PSY_FLG", order.getPSY_FLG());
		parm.setData("EXEC_FLG", order.getEXEC_FLG());
		parm.setData("RECEIPT_FLG", order.getRECEIPT_FLG());
		parm.setData("BILL_TYPE", order.getBILL_TYPE());
		parm.setData("FINAL_TYPE", order.getFINAL_TYPE());
		parm.setData("DECOCT_REMARK", order.getDECOCT_REMARK());
		parm.setData("SEND_DCT_USER", order.getSEND_DCT_USER());
		parm.setData("SEND_DCT_DATE", order.getSEND_DCT_DATE());
		parm.setData("DECOCT_USER", order.getDECOCT_USER());
		parm.setData("DECOCT_DATE", order.getDECOCT_DATE());
		parm.setData("SEND_ORG_USER", order.getSEND_ORG_USER());
		parm.setData("SEND_ORG_DATE", order.getSEND_ORG_DATE());
		parm.setData("EXM_EXEC_END_DATE", order.getEXM_EXEC_END_DATE());
		parm.setData("EXEC_DR_DESC", order.getEXEC_DR_DESC());
		parm.setData("COST_AMT", order.getCOST_AMT());
		parm.setData("COST_CENTER_CODE", order.getCOST_CENTER_CODE());
		parm.setData("BATCH_SEQ1", order.getBATCH_SEQ1());
		parm.setData("VERIFYIN_PRICE1", order.getVERIFYIN_PRICE1());
		parm.setData("DISPENSE_QTY1", order.getDISPENSE_QTY1());
		parm.setData("BATCH_SEQ2", order.getBATCH_SEQ2());
		parm.setData("VERIFYIN_PRICE2", order.getVERIFYIN_PRICE2());
		parm.setData("DISPENSE_QTY2", order.getDISPENSE_QTY2());
		parm.setData("BATCH_SEQ3", order.getBATCH_SEQ3());
		parm.setData("VERIFYIN_PRICE3", order.getVERIFYIN_PRICE3());
		parm.setData("DISPENSE_QTY3", order.getDISPENSE_QTY3());
		parm.setData("BUSINESS_NO", order.getBUSINESS_NO());
		parm.setData("PAT_NAME", order.getPAT_NAME());
		parm.setData("SEX_TYPE", order.getSEX_TYPE());
		parm.setData("BIRTH_DATE", order.getBIRTH_DATE());
		return parm;
	}

}
