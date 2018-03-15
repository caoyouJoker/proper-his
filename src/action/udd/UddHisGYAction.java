package action.udd;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jdo.adm.ADMInpTool;
import jdo.ibs.IBSTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.udd.UddChnCheckTool;
import jdo.udd.UddRtnRgsTool;
import action.udd.client.SpcIndStock;
import action.udd.client.SpcOdiDspnd;
import action.udd.client.SpcOdiDspnm;
import action.udd.client.SpcOdiDspnms;
import action.udd.client.SpcOdiService_SpcOdiServiceImplPort_Client;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 * <p>Title:住院药房同步数据 </p>
 *
 * <p>Description: 住院药房同步数据</p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company:ProperSoft </p>
 *
 * @author shibl 2013.04.09
 * @version 1.0
 */
public class UddHisGYAction extends TAction {
	/**
	 * 
	 * @return
	 */
	public TParm onSave(TParm parm) {
		TParm result = new TParm();
//		System.out.println("======入参==========="+parm);
		TParm lackParm=new TParm();
		Map pat = UddChnCheckTool.getInstance().groupByPatParm(parm);
		Iterator it = pat.values().iterator();
		while (it.hasNext()) {
			TParm patParm = (TParm) it.next();
//			System.out.println("======patParm==========="+patParm);
			int count = patParm.getCount("CASE_NO");
			List<SpcOdiDspnm> spcOdiDspnmList = new ArrayList<SpcOdiDspnm>();
			List<SpcIndStock> spcIndStockList = new ArrayList<SpcIndStock>();
			for (int i = 0; i < count; i++) {
				TParm parmRow = patParm.getRow(i);
				String sql ="SELECT B.* "
						+ " FROM ODI_DSPNM A, ODI_DSPND B "
						+ " WHERE A.CASE_NO = B.CASE_NO "
						+ " AND A.ORDER_NO = B.ORDER_NO "
						+ " AND A.ORDER_SEQ = B.ORDER_SEQ "
						+ " AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN A.START_DTTM AND A.END_DTTM"
						+ " AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN '"
						+ parmRow.getValue("START_DTTM") // shibl 20130225
						// modidfy 改为具体餐次
						+ "' AND '"
						+ parmRow.getValue("END_DTTM")
						+ "'"
						+ " AND A.CASE_NO = '"
						+ parmRow.getValue("CASE_NO")
						+ "' "
						+ " AND A.ORDER_NO = '"
						+ parmRow.getValue("ORDER_NO")
						+ "' "
						+ " AND A.ORDER_SEQ = '"
						+ parmRow.getValue("ORDER_SEQ") + "'";
				TParm param = new TParm(TJDODBTool.getInstance().select(sql));
//				System.out.println("sql----:"+sql);
				List<SpcOdiDspnd> spcOdiDspndList = new ArrayList<SpcOdiDspnd>();
				for (int j = 0; j < param.getCount(); j++) {// M对应多笔D 循环 shibl
					// 20130225 modify
					TParm parmD = param.getRow(j);
					SpcOdiDspnd sodd = new SpcOdiDspnd();

					// 主键
					sodd.setCaseNo(parmD.getValue("CASE_NO"));
					sodd.setOrderNo(parmD.getValue("ORDER_NO"));

					// 取主表
					sodd.setOrderSeq(parmD.getInt("ORDER_SEQ"));
					sodd.setOrderDate(parmD.getValue("ORDER_DATE"));
					sodd.setOrderDatetime(parmD.getValue("ORDER_DATETIME"));

					sodd.setNurseDispenseFlg(parmD
							.getValue("NURSE_DISPENSE_FLG"));
					sodd.setOrderCode(parmD.getValue("ORDER_CODE"));
					sodd.setDosageQty(parmD.getDouble("DOSAGE_QTY"));
					sodd.setDosageUnit(parmD.getValue("DOSAGE_UNIT"));

					sodd.setBarCode(parmD.getValue("BAR_CODE"));
					// 配药默认为收费
					sodd.setBillFlg("Y");
					sodd.setCancelrsnCode(parmD.getValue("CANCELRSN_CODE"));
					sodd.setCashierCode(parmD.getValue("CASHIER_CODE"));
					sodd.setCashierDate(StringTool.getString(parmD
							.getTimestamp("CASHIER_DATE"),
							"yyyy-MM-dd HH:mm:ss"));
					sodd.setDcDate(StringTool.getString(parmD
							.getTimestamp("DC_DATE"), "yyyy-MM-dd HH:mm:ss"));
					sodd.setExecDeptCode(parmD.getValue("EXEC_DEPT_CODE"));
					sodd.setExecNote(parmD.getValue("EXEC_NOTE"));
					sodd.setIbsCaseNo(parmD.getValue("IBS_CASE_NO"));
					sodd.setIbsCaseNoSeq(parmD.getValue("IBS_CASE_NO_SEQ"));
					sodd.setTakemedOrg(parmD.getValue("TAKEMED_ORG"));
					sodd.setIntgmedNo(parmD.getValue("INTGMED_NO"));
					sodd.setInvCode(parmD.getValue("INV_CODE"));
					sodd.setMediQty(parmD.getDouble("MEDI_QTY"));
					sodd.setMediUnit(parmD.getValue("MEDI_UNIT"));
					sodd.setNsExecCode(parmD.getValue("NS_EXEC_CODE"));
					sodd.setNsExecCodeReal("");
					sodd.setNsExecDate(StringTool.getString(parmD
							.getTimestamp("NS_EXEC_DATE"),
							"yyyy-MM-dd HH:mm:ss"));
					sodd.setNsExecDateReal("");
					sodd.setNsExecDcCode(parmD.getValue("NS_EXEC_DC_CODE"));
					sodd.setNsExecDcDate(StringTool.getString(parmD
							.getTimestamp("NS_EXEC_DC_DATE"),
							"yyyy-MM-dd HH:mm:ss"));
					sodd.setNsUser(parmD.getValue("NS_USER"));
					sodd.setNurseDispenseFlg(parmD
							.getValue("NURSE_DISPENSE_FLG"));
					sodd.setOptDate(StringTool.getString(parmD
							.getTimestamp("OPT_DATE"), "yyyy-MM-dd HH:mm:ss"));
					sodd.setOptTerm(parmD.getValue("OPT_TERM"));
					sodd.setOptUser(parmD.getValue("OPT_USER"));

					sodd.setPhaDispenseCode(parmD
									.getValue("PHA_DISPENSE_CODE"));
					sodd.setPhaDispenseDate(parmD
									.getValue("PHA_DISPENSE_DATE"));
					sodd.setPhaDispenseNo(parmD.getValue("PHA_DISPENSE_NO"));
					sodd.setPhaDosageCode(parmD.getValue("OPT_USER"));
					sodd.setPhaDosageDate(StringTool.getString(parmD
							.getTimestamp("OPT_DATE"), "yyyy-MM-dd HH:mm:ss"));
					sodd.setPhaRetnCode("");
					sodd.setPhaRetnDate("");
					// sodd.(parmRow.getValue("SERVICE_LEVEL"));
					sodd.setStopcheckDate("");
					sodd.setStopcheckUser("");

					sodd.setTotAmt(parmD.getDouble("TOT_AMT"));

					sodd.setTakemedNo(parmD.getValue("TAKEMED_NO"));
					// 退药
					sodd.setTransmitRsnCode("");
					sodd.setTreatEndTime("");
					sodd.setTreatStartTime("");
					spcOdiDspndList.add(sodd);
				}
				//
				SpcOdiDspnm sodm = new SpcOdiDspnm();
				String msql ="SELECT A.* "
					+ " FROM ODI_DSPNM A "
					+ " WHERE A.START_DTTM='"
					+ parmRow.getValue("START_DTTM") // shibl 20130225
					// modidfy 改为具体餐次
					+ "'"
					+ " AND A.CASE_NO = '"
					+ parmRow.getValue("CASE_NO")
					+ "' "
					+ " AND A.ORDER_NO = '"
					+ parmRow.getValue("ORDER_NO")
					+ "' "
					+ " AND A.ORDER_SEQ = '"
					+ parmRow.getValue("ORDER_SEQ") + "'";
			    TParm paraMD = new TParm(TJDODBTool.getInstance().select(msql));
			    TParm paraM=paraMD.getRow(0);
				sodm.setAgencyOrgCode(paraM.getValue("AGENCY_ORG_CODE"));
				sodm.setAntibioticCode(paraM.getValue("ANTIBIOTIC_CODE"));
				sodm.setAtcFlg(paraM.getValue("ATC_FLG"));
				sodm.setBarCode(paraM.getValue("BAR_CODE"));
				sodm.setBatchSeq1(paraM.getInt("BATCH_SEQ1"));
				sodm.setBatchSeq2(paraM.getInt("BATCH_SEQ2"));
				sodm.setBatchSeq3(paraM.getInt("BATCH_SEQ3"));
				sodm.setBedNo(paraM.getValue("BED_NO"));
				sodm.setBillFlg(paraM.getValue("BILL_FLG"));
				// sodm.setBoxEslId( );
				sodm.setCancelDosageQty(paraM.getDouble("CANCEL_DOSAGE_QTY"));
				sodm.setCancelrsnCode(paraM.getValue("CANCELRSN_CODE"));
				sodm.setCaseNo(paraM.getValue("CASE_NO"));
				sodm.setCashierDate(StringTool.getString(paraM
						.getTimestamp("CASHIER_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setCashierUser(paraM.getValue("CASHIER_USER"));
				sodm.setCat1Type(paraM.getValue("CAT1_TYPE"));
				sodm.setCtrldrugclassCode(paraM
						.getValue("CTRLDRUGCLASS_CODE"));
				sodm.setDcDate(StringTool.getString(paraM
						.getTimestamp("DC_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setDcDrCode(paraM.getValue("DC_DR_CODE"));
				sodm.setDcNsCheckDate(StringTool.getString(paraM
						.getTimestamp("DC_NS_CHECK_DATE"),
						"yyyy-MM-dd HH:mm:ss"));
				sodm.setDcTot(paraM.getDouble("DC_TOT"));
				sodm.setDctTakeQty(paraM.getInt("DCT_TAKE_QTY"));
				sodm.setDctagentCode(paraM.getValue("DCTAGENT_CODE"));
				sodm.setDctagentFlg(paraM.getValue("DCTAGENT_FLG"));
				sodm.setDctexcepCode(paraM.getValue("DCTEXCEP_CODE"));
				sodm.setDecoctCode(paraM.getValue("DECOCT_CODE"));
				sodm.setDecoctDate(StringTool.getString(paraM
						.getTimestamp("DECOCT_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setDecoctRemark(paraM.getValue("DECOCT_REMARK"));
				sodm.setDecoctUser(paraM.getValue("DECOCT_USER"));
				sodm.setDegreeCode(paraM.getValue("DEGREE_CODE"));
				sodm.setDeptCode(paraM.getValue("DEPT_CODE"));
				sodm.setDiscountRate(paraM.getDouble("DISCOUNT_RATE"));
				sodm.setDispenseEffDate(StringTool.getString(paraM
						.getTimestamp("DISPENSE_EFF_DATE"),
						"yyyy-MM-dd HH:mm:ss"));
				sodm.setDispenseEndDate(StringTool.getString(paraM
						.getTimestamp("DISPENSE_END_DATE"),
						"yyyy-MM-dd HH:mm:ss"));
				sodm.setDispenseFlg(paraM.getValue("DISPENSE_FLG"));
				sodm.setDispenseQty(paraM.getDouble("DISPENSE_QTY"));
				sodm.setDispenseQty1(paraM.getDouble("DISPENSE_QTY1"));
				sodm.setDispenseQty2(paraM.getDouble("DISPENSE_QTY2"));
				sodm.setDispenseQty3(paraM.getDouble("DISPENSE_QTY3"));
				sodm.setDispenseUnit(paraM.getValue("DISPENSE_UNIT"));
				sodm.setDosageQty(paraM.getDouble("DOSAGE_QTY"));
				sodm.setDosageUnit(paraM.getValue("DOSAGE_UNIT"));
				sodm.setDoseType(paraM.getValue("DOSE_TYPE"));
				sodm.setDrNote(paraM.getValue("DR_NOTE"));
				sodm.setDspnDate(StringTool.getString(paraM
						.getTimestamp("DSPN_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setDspnKind(paraM.getValue("DSPN_KIND"));
				sodm.setDspnUser(paraM.getValue("DSPN_USER"));
				sodm.setEndDttm(paraM.getValue("END_DTTM"));
				sodm.setExecDeptCode(paraM.getValue("EXEC_DEPT_CODE"));
				sodm.setFinalType(paraM.getValue("FINAL_TYPE"));
				sodm.setFreqCode(paraM.getValue("FREQ_CODE"));
				sodm.setGiveboxFlg(paraM.getValue("GIVEBOX_FLG"));
				sodm.setGoodsDesc(paraM.getValue("GOODS_DESC"));
				sodm.setHideFlg(paraM.getValue("HIDE_FLG"));
				sodm.setIbsCaseNoSeq(paraM.getInt("IBS_CASE_NO_SEQ"));
				sodm.setIbsSeqNo(paraM.getInt("IBS_SEQ_NO"));
				sodm.setInjpracGroup(paraM.getInt("INJPRAC_GROUP"));
				sodm.setIntgmedNo(paraM.getValue("INTGMED_NO"));
				sodm.setIpdNo(paraM.getValue("IPD_NO"));
				sodm.setLinkNo(paraM.getValue("LINK_NO"));
				sodm.setLinkmainFlg(paraM.getValue("LINKMAIN_FLG"));
				sodm.setLisReDate(StringTool.getString(paraM
						.getTimestamp("LIS_RE_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setLisReUser(paraM.getValue("LIS_RE_USER"));
				sodm.setMediQty(paraM.getDouble("MEDI_QTY"));
				sodm.setMediUnit(paraM.getValue("MEDI_UNIT"));
				sodm.setMrNo(paraM.getValue("MR_NO"));
				sodm.setNhiPrice(paraM.getDouble("NHI_PRICE"));
				sodm.setNsExecCode(paraM.getValue("NS_EXEC_CODE"));
				sodm.setNsExecDate(StringTool.getString(paraM
						.getTimestamp("NS_EXEC_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setNsExecDcCode(paraM.getValue("NS_EXEC_DC_CODE"));
				sodm.setNsExecDcDate(StringTool
						.getString(paraM.getTimestamp("NS_EXEC_DC_DATE"),
								"yyyy-MM-dd HH:mm:ss"));
				sodm.setNsUser(paraM.getValue("NS_USER"));
				sodm.setOptDate(StringTool.getString(paraM
						.getTimestamp("OPT_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setOptTerm(paraM.getValue("OPT_TERM"));
				sodm.setOptUser(paraM.getValue("OPT_USER"));
				sodm.setOptitemCode(paraM.getValue("OPTITEM_CODE"));
				sodm.setOrderCat1Code(paraM.getValue("ORDER_CAT1_CODE"));
				sodm.setOrderCode(paraM.getValue("ORDER_CODE"));
				sodm.setOrderDate(StringTool.getString(paraM
						.getTimestamp("ORDER_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setOrderDeptCode(paraM.getValue("ORDER_DEPT_CODE"));
				sodm.setOrderDesc(paraM.getValue("ORDER_DESC"));
				sodm.setOrderDrCode(paraM.getValue("ORDER_DR_CODE"));
				sodm.setOrderNo(paraM.getValue("ORDER_NO"));
				sodm.setOrderSeq(paraM.getInt("ORDER_SEQ"));
				sodm.setOrdersetCode(paraM.getValue("ORDERSET_CODE"));
				sodm.setOrdersetGroupNo(paraM.getValue("ORDERSET_GROUP_NO"));
				// sodm.setOrgCode(parmRow.getValue("EXEC_DEPT_CODE"));///////////////////////////
				sodm.setOwnAmt(paraM.getDouble("OWN_AMT"));
				sodm.setOwnPrice(paraM.getDouble("OWN_PRICE"));
				sodm.setPackageAmt(parmRow.getInt("PACKAGE_AMT"));
				sodm.setParentCaseNo(paraM.getValue("PARENT_CASE_NO"));
				sodm.setParentOrderNo(paraM.getValue("PARENT_ORDER_NO"));
				sodm.setParentOrderSeq(paraM.getInt("PARENT_ORDER_SEQ"));
				sodm.setParentStartDttm(paraM.getValue("PARENT_START_DTTM"));
				sodm.setPhaCheckCode(paraM.getValue("PHA_CHECK_CODE"));
				sodm.setPhaCheckDate(StringTool.getString(paraM
								.getTimestamp("PHA_CHECK_DATE"),
								"yyyy-MM-dd HH:mm:ss"));
				sodm.setPhaDispenseCode(paraM.getValue("PHA_DISPENSE_CODE"));
				sodm.setPhaDispenseDate(StringTool.getString(paraM
						.getTimestamp("PHA_DISPENSE_DATE"),
						"yyyy-MM-dd HH:mm:ss"));
				sodm.setPhaDispenseNo(paraM.getValue("PHA_DISPENSE_NO"));
				sodm.setPhaDosageCode(paraM.getValue("PHA_DOSAGE_CODE"));
				sodm.setPhaDosageDate(StringTool.getString(paraM
						.getTimestamp("PHA_DOSAGE_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setPhaRetnCode(paraM.getValue("PHA_RETN_CODE"));
				sodm.setPhaRetnDate(StringTool.getString(paraM
						.getTimestamp("PHA_RETN_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setPhaType(paraM.getValue("PHA_TYPE"));
				sodm.setPrescriptNo(paraM.getValue("PRESCRIPT_NO"));
				sodm.setPresrtNo(paraM.getValue("PRESRT_NO"));
				sodm.setRegionCode(paraM.getValue("REGION_CODE"));
				sodm.setReturnQty1(paraM.getDouble("RETURN_QTY1"));
				sodm.setReturnQty2(paraM.getDouble("RETURN_QTY2"));
				sodm.setReturnQty3(paraM.getDouble("RETURN_QTY3"));
				sodm.setRouteCode(paraM.getValue("ROUTE_CODE"));
				sodm.setRpttypeCode(paraM.getValue("RPTTYPE_CODE"));
				sodm.setRtnDosageQty(paraM.getDouble("RTN_DOSAGE_QTY"));
				sodm.setRtnDosageUnit(paraM.getValue("RTN_DOSAGE_UNIT"));
				sodm.setRtnNo(paraM.getValue("RTN_NO"));
				sodm.setRtnNoSeq(paraM.getInt("RTN_NO_SEQ"));
				sodm.setRxNo(paraM.getValue("RX_NO"));
				sodm.setSendDctDate(StringTool.getString(paraM
						.getTimestamp("SEND_DCT_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setSendDctUser(paraM.getValue("SEND_DCT_USER"));
				sodm.setSendOrgDate(StringTool.getString(paraM
						.getTimestamp("SEND_ORG_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sodm.setSendOrgUser(paraM.getValue("SEND_ORG_USER"));
				sodm.setSendatcDttm(paraM.getValue("SENDATC_DTTM"));
				sodm.setSendatcFlg(paraM.getValue("SENDATC_FLG"));
				sodm.setSetmainFlg(paraM.getValue("SETMAIN_FLG"));
				sodm.setSpecification(paraM.getValue("SPECIFICATION"));
				sodm.setStartDttm(paraM.getValue("START_DTTM"));
				sodm.setStationCode(paraM.getValue("STATION_CODE"));
				sodm.setTakeDays(paraM.getInt("TAKE_DAYS"));
				sodm.setRegionCode(paraM.getValue("REGION_CODE"));
				// 取药单号
				// 送包药机注记
				sodm.setSendatcFlg(paraM.getValue("SENDATC_DTTM").equals("")?"N":"Y");
				sodm.setTakemedNo(paraM.getValue("TAKEMED_NO"));
				sodm.setTakemedOrg(paraM.getValue("TAKEMED_ORG"));

				sodm.setTotAmt(paraM.getDouble("TOT_AMT"));
				sodm.setTransmitRsnCode(paraM.getValue("TRANSMIT_RSN_CODE"));
				// sodm.setTurnEslId( );
				sodm.setUrgentFlg(paraM.getValue("URGENT_FLG"));
				sodm.setVerifyinPrice1(paraM.getDouble("VERIFYIN_PRICE1"));
				sodm.setVerifyinPrice2(paraM.getDouble("VERIFYIN_PRICE2"));
				sodm.setVerifyinPrice3(paraM.getDouble("VERIFYIN_PRICE3"));
				sodm.setVsDrCode(paraM.getValue("VS_DR_CODE"));
				sodm.setSpcOdiDspnds(spcOdiDspndList);
				SpcIndStock sis = new SpcIndStock();
				sis.setDosageQty(paraM.getDouble("DOSAGE_QTY"));
				sis.setOptDate(StringTool.getString(paraM
						.getTimestamp("OPT_DATE"), "yyyy-MM-dd HH:mm:ss"));
				sis.setOptTerm(paraM.getValue("OPT_TERM"));
				sis.setOptUser(paraM.getValue("OPT_USER"));
				sis.setOrderCode(paraM.getValue("ORDER_CODE"));
				sis.setOrderDesc(paraM.getValue("ORDER_DESC"));
				sis.setOrgCode(paraM.getValue("EXEC_DEPT_CODE"));
				sis.setServiceLevel("");//
				sis.setSendatcFlg(paraM.getValue("SENDATC_DTTM").equals("")?"N":"Y");
				spcOdiDspnmList.add(sodm);
				spcIndStockList.add(sis);
				
			}		
			SpcOdiDspnms odiDspnms = new SpcOdiDspnms();
			odiDspnms.setSpcOdiDspnms(spcOdiDspnmList);
			odiDspnms.setSpcIndStocks(spcIndStockList);
			String returnStr = "";
//			System.out.println("==============调用物联网Servicek开始=======================");
			try {
				returnStr = SpcOdiService_SpcOdiServiceImplPort_Client
						.examine(odiDspnms);
				if (("success").equals(returnStr)) {
//					System.out.println("==============调用物联网Service成功=======================");
					// 不做处理
					UddChnCheckTool.getInstance().updateGYWSSendFlg(patParm,"Y");
				} else {
//					System.out.println("==============调用物联网Service失败=======================");
					lackParm.addData("MSG",  patParm.getValue("PAT_NAME", 0)
							+ "（病案号：" + patParm.getValue("MR_NO", 0) + ")"+returnStr+",调用物联网Services失败");
					lackParm.setCount(lackParm.getCount("MSG"));
					continue;
//					result.setErr(-1, "调用物联网Services失败异常");
				}
			} catch (Exception e) {
				// TODO: handle exception
//				System.out.println("==============调用物联网Service异常=======================");
				lackParm.addData("MSG",  patParm.getValue("PAT_NAME", 0)
						+ "（病案号：" + patParm.getValue("MR_NO", 0) + ")调用物联网Services异常");
				lackParm.setCount(lackParm.getCount("MSG"));
				continue;
			}
//			System.out.println("==============调用物联网Servicek结束=======================");
		}
		if (lackParm.getCount() > 0) {
			result.setData("ERR_LIST", lackParm.getData());
		}
		return result;
	}
	/**
	 * 取消发药动作
	 * @return
	 */
	public TParm onCancleDosage(TParm parm){
		TParm  result=new TParm();
		TParm lackParm=new TParm();
		TConnection connection = getConnection();
		Map pat = UddChnCheckTool.getInstance().groupByPatParm(parm);
		Iterator it = pat.values().iterator();
		String  userId=parm.getValue("ID");
		String  ip=parm.getValue("IP");
		while (it.hasNext()) {
			TParm patParm = (TParm) it.next();
			String orderNo = SystemTool.getInstance().getNo("ALL", "ODI",
					"ORDER_NO", "ORDER_NO");
			Timestamp now = TJDODBTool.getInstance().getDBTime();
			String nowStr = StringTool.getString(now, "yyyyMMddHHmmss");
			String orderDate = nowStr.substring(0, 8);
			String orderDateTime = nowStr.substring(8,12);
			String sttDttm = StringTool.getString(now, "yyyyMMddHHmm");
			int seq=1;
			for (int i = 0; i < patParm.getCount("CASE_NO"); i++) {
				TParm parmRow = patParm.getRow(i);
				String msql ="SELECT B.NHI_PRICE,B.ADDPAY_RATE,A.CASE_NO,A.ORDER_CODE,A.DEPT_CODE," 
					+ " A.REGION_CODE,A.ORDER_CAT1_CODE,A.CAT1_TYPE,A.DOSE_TYPE,"
					+ " A.GIVEBOX_FLG,A.PHA_TYPE,A.ROUTE_CODE,A.ORDER_DESC,A.MR_NO,"
					+ " A.STATION_CODE,A.BED_NO,A.IPD_NO,A.EXEC_DEPT_CODE,A.DOSAGE_QTY,A.VS_DR_CODE,"
					+ " A.DOSAGE_UNIT,B.OWN_PRICE,A.DISPENSE_QTY,A.DISPENSE_UNIT,A.ORDER_DR_CODE,"
					+ " A.ORDER_DEPT_CODE,A.VS_DR_CODE"
					+ " FROM ODI_DSPNM A,SYS_FEE B"
					+ " WHERE A.START_DTTM='"
					+ parmRow.getValue("START_DTTM") // shibl 20130225
					// modidfy 改为具体餐次
					+ "'"
					+ " AND A.CASE_NO = '"
					+ parmRow.getValue("CASE_NO")
					+ "' "
					+ " AND A.ORDER_NO = '"
					+ parmRow.getValue("ORDER_NO")
					+ "' "
					+ " AND A.ORDER_SEQ = '"
					+ parmRow.getValue("ORDER_SEQ") + "'" 
					+ " AND A.ORDER_CODE=B.ORDER_CODE ";
			    TParm paraMD = new TParm(TJDODBTool.getInstance().select(msql));
			    TParm paraM=paraMD.getRow(0);
			    paraM.setData("DISCOUNT_RATE",paraM.getDouble("ADDPAY_RATE"));
			    paraM.setData("ORDER_NO", orderNo);
			    paraM.setData("ORDER_SEQ", seq);
			    paraM.setData("START_DTTM", sttDttm);
			    paraM.setData("END_DTTM", sttDttm);
			    paraM.setData("RTN_NO", orderNo);
			    paraM.setData("RTN_NO_SEQ", seq);
			    paraM.setData("ORDER_DATE", orderDate);
			    paraM.setData("ORDER_DATETIME", orderDateTime);
			    paraM.setData("RX_KIND", "RT");
			    paraM.setData("DC_TOT", paraM.getDouble("DOSAGE_QTY")* paraM.getDouble("OWN_PRICE"));
			    paraM.setData("OWN_AMT", paraM.getDouble("DOSAGE_QTY")* paraM.getDouble("OWN_PRICE"));
			    paraM.setData("TOT_AMT", paraM.getDouble("DOSAGE_QTY")* paraM.getDouble("OWN_PRICE"));
			    paraM.setData("RTN_DOSAGE_QTY", paraM.getDouble("DOSAGE_QTY"));
			    paraM.setData("RTN_DOSAGE_UNIT", paraM.getValue("DOSAGE_UNIT"));
			    paraM.setData("PHA_RETN_CODE", userId);
			    paraM.setData("PHA_RETN_DATE", TJDODBTool.getInstance().getDBTime());
			    paraM.setData("TRANSMIT_RSN_CODE","");//暂设为空
			    paraM.setData("DSPN_USER",userId);
			    paraM.setData("OPT_USER", userId);
			    paraM.setData("OPT_TERM", ip);
			    paraM.setData("IBS_SEQ_NO", "");//暂设为空
			    paraM.setData("IBS_CASE_NO_SEQ", "");//暂设为空
			    patParm.setData("ORDER_NOM", i, parmRow.getValue("ORDER_NO"));
			    patParm.setData("ORDER_SEQM", i, parmRow.getValue("ORDER_SEQ"));
			    patParm.setData("ORDER_NO", i, orderNo);
			    patParm.setData("ORDER_SEQ", i, seq);
			  //  System.out.println("====================================="+paraM);
				result = UddRtnRgsTool.getInstance().onCancleDisM(paraM, connection);
				if (result.getErrCode() != 0) {
					lackParm.addData("MSG",  patParm.getValue("PAT_NAME", 0)
							+ "（病案号：" + patParm.getValue("MR_NO", 0) + ")插入退药数据失败");
					lackParm.setCount(lackParm.getCount("MSG"));
					connection.rollback();
					connection.close();
					return result;
				}
				result = UddRtnRgsTool.getInstance().onCancleDisD(paraM, connection);
				if (result.getErrCode() != 0) {
					lackParm.addData("MSG",  patParm.getValue("PAT_NAME", 0)
							+ "（病案号：" + patParm.getValue("MR_NO", 0) + ")插入退药数据失败");
					lackParm.setCount(lackParm.getCount("MSG"));
					connection.rollback();
					connection.close();
					return result;
				}
				seq++;
			}
			// 退费
			TParm inParmIbs = new TParm();
			inParmIbs.setData("FLG", "RTN");
			// zhangyong20110516 添加区域REGION_CODE
			inParmIbs.setData("REGION_CODE", parm.getValue("REGION_CODE"));
			inParmIbs.setData("M", patParm.getData());
			TParm patInfo = new TParm();
			patInfo.setData("CASE_NO", patParm.getValue("CASE_NO", 0));
			patInfo = ADMInpTool.getInstance().selectall(patInfo);
			inParmIbs.setData("CTZ1_CODE", patInfo.getValue("CTZ1_CODE", 0));
			inParmIbs.setData("CTZ2_CODE", patInfo.getValue("CTZ2_CODE", 0));
			inParmIbs.setData("CTZ3_CODE", patInfo.getValue("CTZ3_CODE", 0));
			TParm resultIbs = IBSTool.getInstance().getIBSOrderData(inParmIbs);
			if (resultIbs.getErrCode() != 0) {
				System.out.println(resultIbs.getErrText());
				result.setErrCode(-1);
				result.setErrText(resultIbs.getErrText());
				lackParm.addData("MSG",  patParm.getValue("PAT_NAME", 0)
						+ "（病案号：" + patParm.getValue("MR_NO", 0) + ")计算退药费用失败");
				lackParm.setCount(lackParm.getCount("MSG"));
				connection.rollback();
				connection.close();
				return result;
			}
			TParm inIbs = new TParm();
			inIbs.setData("M", resultIbs.getData());
			inIbs.setData("FLG", "RTN");
			inIbs.setData("DATA_TYPE", "2");
			result = IBSTool.getInstance().insertIBSOrder(inIbs, connection);
			if (result.getErrCode() != 0) {
				// luhai add 2012-04-15 add rollback
				lackParm.addData("MSG",  patParm.getValue("PAT_NAME", 0)
						+ "（病案号：" + patParm.getValue("MR_NO", 0) + ")退药退费失败");
				lackParm.setCount(lackParm.getCount("MSG"));
				connection.rollback();
				connection.close();
				return result;
			}
			for(int i=0;i<patParm.getCount("CASE_NO");i++){
				TParm parmRow = patParm.getRow(i);
				patParm.setData("ORDER_NO", i, parmRow.getValue("ORDER_NOM"));
				patParm.setData("ORDER_SEQ", i, parmRow.getValue("ORDER_SEQM"));
			}
			UddChnCheckTool.getInstance().updateGYWSSendFlg(patParm,"C");//标记取消发药
			connection.commit();			
		}
		if(!connection.isClosed())
			connection.close();
		return result;
	}
}
