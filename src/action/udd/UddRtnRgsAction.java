package action.udd;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jdo.adm.ADMInpTool;
import jdo.ibs.IBSOrderdTool;
import jdo.ibs.IBSTool;
import jdo.ind.INDTool;
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
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: 住院药房退药登记动作类
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ehui 20090307
 * @version 1.0
 */
public class UddRtnRgsAction extends TAction {

	public TParm onInsert(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		// System.out.println(parm.getCount("CASE_NO")+"getCount");
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			TParm parmRow = parm.getRow(i);
			result = UddRtnRgsTool.getInstance().onInsertM(parmRow, connection);
			if (result.getErrCode() != 0) {
				connection.close();
				return result;
			}
            if (parmRow.getValue("RT_TYPE").equals("DC")) {// add by wanglong 20130628
                // luhai add 2012-2-3 退药保存时更新原有发药数据的退药量
                parmRow.setData("OPT_TYPE", "ADD");// 加法
                result = UddRtnRgsTool.getInstance().onUpdateDispenseReturnQty(parmRow, connection);
                if (result.getErrCode() != 0) {
                    connection.close();
                    return result;
                }
                // luhai add 2012-2-3 退药保存时更新原有发药数据的退药量end
            }
			result = UddRtnRgsTool.getInstance().onInsertD(parmRow, connection);
			if (result.getErrCode() != 0) {
				connection.close();
				return result;
			}

		}
		connection.commit();
		connection.close();
		return result;
	}

	public TParm onUpdate(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			TParm parmRow = parm.getRow(i);
			result = UddRtnRgsTool.getInstance().onUpdateM(parmRow, connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
			result = UddRtnRgsTool.getInstance().onUpdateD(parmRow, connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

	public TParm onDelete(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		// System.out.println(parm.getCount("CASE_NO"));
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			TParm parmRow = parm.getRow(i);
			result = UddRtnRgsTool.getInstance().onDeleteM(parmRow, connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
            if (!parmRow.getValue("PARENT_ORDER_NO").equals("")) {// modify by wanglong 20130628
                // luhai add 2012-2-3 退药保存时更新原有发药数据的退药量
                parmRow.setData("OPT_TYPE", "MINUS");// 减法
                result = UddRtnRgsTool.getInstance().onUpdateDispenseReturnQty(parmRow, connection);
                if (result.getErrCode() != 0) {
                    connection.close();
                    return result;
                }
                // luhai add 2012-2-3 退药保存时更新原有发药数据的退药量end
            }
			result = UddRtnRgsTool.getInstance().onDeleteD(parmRow, connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

	public TParm onDeleteSingle(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		// System.out.println(parm.getCount("CASE_NO"));
		result = UddRtnRgsTool.getInstance().onDeleteM(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		result = UddRtnRgsTool.getInstance().onDeleteD(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 退药确认
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onUpdateRtnCfm(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		String spcflg = parm.getValue("SPC_FLG");// 国药开关 shibl add 20130423
		// //System.out.println(parm.getCount("CASE_NO"));
		if (spcflg.equals("N")) {
			for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
				TParm parmRow = parm.getRow(i);
				// //检验退药数量<=已计价数量,如此次退药超出已计费的药量则不允许退药
				// // System.out.println("caseNo="+parmRow.getValue("CASE_NO"));
				// //
				// System.out.println("orderCode="+parmRow.getValue("ORDER_CODE"));
				// //
				// System.out.println("execDeptCode="+parmRow.getValue("EXEC_DEPT_CODE"));
				result = IBSOrderdTool.getInstance().checkIBSQutryForUDD(
						parmRow.getValue("CASE_NO"),
						parmRow.getValue("ORDER_CODE"),
						parmRow.getValue("EXEC_DEPT_CODE"));
				// // System.out.println("result========"+result);
				double leftDosage = result.getDouble("DOSAGE_QTY", 0);
				// // System.out.println("leftDosage-="+leftDosage);
				// //luhai 2012-04-13 modify begin 得到实际的退药数量进行判断
				// // double thisDosage = parmRow.getDouble("RTN_DOSAGE_QTY");
				// // if (thisDosage > leftDosage || leftDosage <= 0) {
				// // result = new TParm();
				// // result.setErrCode( -1);
				// // result.setErrText("此次退药量超过已计费的药品总量，不能退药");
				// // return result;
				// // }
				double thisDosage = parm.getDouble("RTN_DOSAGE_QTY", i)
						- parm.getDouble("CANCEL_DOSAGE_QTY", i);
				// // double thisDosage = parmRow.getDouble("RTN_DOSAGE_QTY");
				// // if (thisDosage > leftDosage || leftDosage <= 0) {
				// // result = new TParm();
				// // result.setErrCode( -1);
				// // result.setErrText("此次退药量超过已计费的药品总量，不能退药");
				// // return result;
				// // }
				// //luhai 2012-04-13 modify end
				// // System.out.println("thisDosage="+thisDosage);
				// // System.out.println("parmRow----"+parmRow);
				// System.out.println("------"+parmRow.getValue("CASE_NO"));
				result = UddRtnRgsTool.getInstance().onUpdateRtnCfmM(parmRow,
						connection);
				if (result.getErrCode() < 0) {
					// luhai add 2012-04-15 add rollback
					System.out.println(result.getErrText());
					connection.rollback();
					connection.close();
					return result;
				}
				result = UddRtnRgsTool.getInstance().onUpdateRtnCfmD(parmRow,
						connection);
				if (result.getErrCode() < 0) {
					// luhai add 2012-04-15 add rollback
					System.out.println(result.getErrText());
					connection.rollback();
					connection.close();
					return result;
				}
				// //*********************************************************
				// //luhai 2012-1-30 修改住院退药 begin
				// //*********************************************************
				// //增库
				TParm parmInd = new TParm();
				parmInd.setData("ORG_CODE", parmRow.getValue("EXEC_DEPT_CODE"));
				parmInd.setData("ORDER_CODE", parmRow.getValue("ORDER_CODE"));
				// luhai modify 2012-04-13 begin 退药数量为退药登记量-取消量
				// parmInd.setData("DOSAGE_QTY",
				// parmRow.getValue("RTN_DOSAGE_QTY"));
				parmInd.setData("DOSAGE_QTY", thisDosage);
				// luhai modify 2012-04-13 end
				parmInd.setData("OPT_USER", parmRow.getValue("OPT_USER"));
				parmInd.setData("OPT_DATE", TJDODBTool.getInstance()
						.getDBTime());
				parmInd.setData("OPT_TERM", parmRow.getValue("OPT_TERM"));
				parmInd.setData("UPDATE_BACTH_SEQ1", parmRow.getData());//20150713 wangjc add
				result = INDTool.getInstance().regressIndStock(parmInd,
						getServiceLevel(parm.getValue("CASE_NO", i)),
						connection);
				if (result.getErrCode() < 0) {
					// luhai add 2012-04-15 add rollback
					System.out.println(result.getErrText());
					connection.rollback();
					connection.close();
					return result;
				}
				// // TParm uddRtnMedResult =
				// uddRtnMed(parmRow,getServiceLevel(parm.getValue("CASE_NO",
				// i)),connection);
				// // if (uddRtnMedResult.getErrCode() < 0) {
				// // connection.close();
				// // return result;
				// // }
				// //// System.out.println("parmRow:"+parmRow);
				// // //更新实际的退药量到主记录
				// // TParm updateRtn= updateRealReturnQty(parmRow,connection);
				// // if (updateRtn.getErrCode() < 0) {
				// // connection.close();
				// // return result;
				// // }
				// //*********************************************************
				// //luhai 2012-1-30 修改住院退药 end
				// //*********************************************************
				//            
			}
		}
		// zhangyong20100719 begin
		// 退费
		TParm inParmIbs = new TParm();
		inParmIbs.setData("FLG", "RTN");
		// zhangyong20110516 添加区域REGION_CODE
		inParmIbs.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			// CANCEL_DOSAGE_QTY
			// luhai modify 2012-3-8 add 退药数量为申请数量-取消数量 begin
			// parm.setData("DOSAGE_QTY", i, parm.getDouble("RTN_DOSAGE_QTY",
			// i));
			parm.setData("DOSAGE_QTY", i, parm.getDouble("RTN_DOSAGE_QTY", i)
					- parm.getDouble("CANCEL_DOSAGE_QTY", i));
			// System.out.println("实际的取消数量："+(parm.getDouble("RTN_DOSAGE_QTY",
			// i)-parm.getDouble("CANCEL_DOSAGE_QTY", i)));
			// luhai modify 2012-3-8 add 退药数量为申请数量-取消数量 end
			parm.setData("DOSAGE_UNIT", i, parm.getValue("RTN_DOSAGE_UNIT", i));
		}
		inParmIbs.setData("M", parm.getData());

		TParm patInfo = new TParm();
		patInfo.setData("CASE_NO", parm.getValue("CASE_NO", 0));
		patInfo = ADMInpTool.getInstance().selectall(patInfo);
		inParmIbs.setData("CTZ1_CODE", patInfo.getValue("CTZ1_CODE", 0));
		inParmIbs.setData("CTZ2_CODE", patInfo.getValue("CTZ2_CODE", 0));
		inParmIbs.setData("CTZ3_CODE", patInfo.getValue("CTZ3_CODE", 0));

		TParm resultIbs = IBSTool.getInstance().getIBSOrderData(inParmIbs);
		if (resultIbs.getErrCode() != 0) {
			// luhai add 2012-04-15 add rollback
			System.out.println(result.getErrText());
			connection.rollback();
			connection.close();
			return null;
		}

		// System.out.println("resultIbs---" + resultIbs);
		TParm inIbs = new TParm();
		inIbs.setData("M", resultIbs.getData());
		inIbs.setData("FLG", "RTN");
		inIbs.setData("DATA_TYPE", "2");
		result = IBSTool.getInstance().insertIBSOrder(inIbs, connection);
		if (result.getErrCode() != 0) {
			// luhai add 2012-04-15 add rollback
			System.out.println(result.getErrText());
			connection.rollback();
			connection.close();
			return null;
		}
		// zhangyong20100719 end
		// ================国药数据组装===================add by wanglong 20130111
		List<SpcOdiDspnm> spcOdiDspnmList = new ArrayList<SpcOdiDspnm>();// add
																			// by
																			// wanglong
																			// 20130111
		List<SpcIndStock> spcIndStockList = new ArrayList<SpcIndStock>();// add
																			// by
																			// wanglong
																			// 20130111
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {// 增库★
			TParm parmRow = parm.getRow(i);
			result = IBSOrderdTool.getInstance().checkIBSQutryForUDD(
					parmRow.getValue("CASE_NO"),
					parmRow.getValue("ORDER_CODE"),
					parmRow.getValue("EXEC_DEPT_CODE"));
			// System.out.println("result========"+result);
			double leftDosage = result.getDouble("DOSAGE_QTY", 0);
			double thisDosage = parm.getDouble("RTN_DOSAGE_QTY", i)
					- parm.getDouble("CANCEL_DOSAGE_QTY", i);
			//20150630 wangjc add start
			String seq1Sql = "SELECT * FROM ODI_DSPNM A, ODI_DSPND B "
					+ " WHERE A.CASE_NO = B.CASE_NO "
					+ " AND A.ORDER_NO = B.ORDER_NO "
					+ " AND A.ORDER_SEQ = B.ORDER_SEQ "
					+ " AND B.ORDER_DATE || SUBSTR(B.ORDER_DATETIME,0,4)  BETWEEN A.START_DTTM AND A.END_DTTM "
					+ " AND A.CASE_NO = '" + parmRow.getValue("CASE_NO") + "' "
					+ " AND A.ORDER_NO = '" + parmRow.getValue("ORDER_NO")
					+ "' " + " AND A.ORDER_SEQ = '"
					+ parmRow.getValue("ORDER_SEQ") + "'";
			// System.out.println("sql--------------:"+sql);
			TParm seq1Parm = new TParm(TJDODBTool.getInstance().select(seq1Sql));
			parmRow.setData("BATCH_SEQ1", seq1Parm.getValue("BATCH_SEQ1", 0));
			//20150630 wangjc add end
			result = UddRtnRgsTool.getInstance().onUpdateRtnCfmM(parmRow,
					connection);
			if (result.getErrCode() < 0) {
				// luhai add 2012-04-15 add rollback
				System.out.println(result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}
			result = UddRtnRgsTool.getInstance().onUpdateRtnCfmD(parmRow,
					connection);
			if (result.getErrCode() < 0) {
				// luhai add 2012-04-15 add rollback
				System.out.println(result.getErrText());
				connection.rollback();
				connection.close();
				return result;
			}
			if (spcflg.equals("N"))//非国药流程返回 shibl add 20130423
				continue;
			String sql = "SELECT * FROM ODI_DSPNM A, ODI_DSPND B "
					+ " WHERE A.CASE_NO = B.CASE_NO "
					+ " AND A.ORDER_NO = B.ORDER_NO "
					+ " AND A.ORDER_SEQ = B.ORDER_SEQ "
					+ " AND B.ORDER_DATE || SUBSTR(B.ORDER_DATETIME,0,4)  BETWEEN A.START_DTTM AND A.END_DTTM "
					+ " AND A.CASE_NO = '" + parmRow.getValue("CASE_NO") + "' "
					+ " AND A.ORDER_NO = '" + parmRow.getValue("ORDER_NO")
					+ "' " + " AND A.ORDER_SEQ = '"
					+ parmRow.getValue("ORDER_SEQ") + "'";
			// System.out.println("sql--------------:"+sql);
			TParm param = new TParm(TJDODBTool.getInstance().select(sql));
			param = param.getRow(0);
			SpcOdiDspnd sodd = new SpcOdiDspnd();
			sodd.setBarCode(param.getValue("BAR_CODE"));
			// sodd.setBarcode1( );
			// sodd.setBarcode2( );
			// sodd.setBarcode3( );
			// sodd.setBatchCode( );
			sodd.setBillFlg(param.getValue("BILL_FLG"));
			// sodd.setBoxEslId( );
			sodd.setCancelrsnCode(param.getValue("CANCELRSN_CODE"));
			sodd.setCaseNo(parmRow.getValue("CASE_NO"));
			sodd.setCashierCode(param.getValue("CASHIER_CODE"));
			sodd.setCashierDate(StringTool.getString(param
					.getTimestamp("CASHIER_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodd.setDcDate(StringTool.getString(param.getTimestamp("DC_DATE"),
					"yyyy-MM-dd HH:mm:ss"));
			sodd.setDcNsCheckDate(StringTool.getString(param
					.getTimestamp("DC_NS_CHECK_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodd.setDosageQty(param.getDouble("DOSAGE_QTY"));
			sodd.setDosageUnit(param.getValue("DOSAGE_UNIT"));
			sodd.setExecDeptCode(parmRow.getValue("EXEC_DEPT_CODE"));
			sodd.setExecNote(param.getValue("EXEC_NOTE"));
			sodd.setIbsCaseNo(param.getValue("IBS_CASE_NO"));
			sodd.setIbsCaseNoSeq(parmRow.getValue("IBS_CASE_NO_SEQ"));
			// sodd.setIntgmedNo( );
			sodd.setInvCode(param.getValue("INV_CODE"));
			sodd.setMediQty(param.getDouble("MEDI_QTY"));
			sodd.setMediUnit(param.getValue("MEDI_UNIT"));
			sodd.setNsExecCode(param.getValue("NS_EXEC_CODE"));
			sodd.setNsExecCodeReal(param.getValue("NS_EXEC_CODE_REAL"));
			sodd.setNsExecDate(StringTool.getString(param
					.getTimestamp("NS_EXEC_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodd.setNsExecDateReal(StringTool.getString(param
					.getTimestamp("NS_EXEC_DATE_REAL"), "yyyy-MM-dd HH:mm:ss"));
			sodd.setNsExecDcCode(param.getValue("NS_EXEC_DC_CODE"));
			sodd.setNsExecDcDate(StringTool.getString(param
					.getTimestamp("NS_EXEC_DC_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodd.setNsUser(param.getValue("NS_USER"));
			sodd.setNurseDispenseFlg(param.getValue("NURSE_DISPENSE_FLG"));
			sodd.setOptDate(StringTool.getString(
					param.getTimestamp("OPT_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodd.setOptTerm(parmRow.getValue("OPT_TERM"));
			sodd.setOptUser(parmRow.getValue("OPT_USER"));
			sodd.setOrderCode(parmRow.getValue("ORDER_CODE"));
			sodd.setOrderDate(parmRow.getValue("START_DTTM"));
			sodd.setOrderDatetime(param.getValue("ORDER_DATETIME"));
			sodd.setOrderNo(parmRow.getValue("ORDER_NO"));
			sodd.setOrderSeq(parmRow.getInt("ORDER_SEQ"));
			sodd.setPhaDispenseCode(param.getValue("PHA_DISPENSE_CODE"));
			sodd.setPhaDispenseDate(param.getValue("PHA_DISPENSE_DATE"));
			sodd.setPhaDispenseNo(param.getValue("PHA_DISPENSE_NO"));
			sodd.setPhaDosageCode(param.getValue("PHA_DOSAGE_CODE"));
			sodd.setPhaDosageDate(param.getValue("PHA_DOSAGE_DATE"));
			sodd.setPhaRetnCode(parmRow.getValue("PHA_RETN_CODE"));
			sodd.setPhaRetnDate(parmRow.getValue("PHA_RETN_DATE"));

			sodd.setStopcheckDate(StringTool.getString(param
					.getTimestamp("STOPCHECK_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodd.setStopcheckUser(param.getValue("STOPCHECK_USER"));
			// sodd.setTakemedOrg( );
			sodd.setTotAmt(param.getDouble("TOT_AMT"));
			sodd.setTransmitRsnCode(parmRow.getValue("TRANSMIT_RSN_CODE"));
			sodd.setTreatEndTime(param.getValue("TREAT_END_TIME"));
			sodd.setTreatStartTime(param.getValue("TREAT_START_TIME"));
			//
			SpcOdiDspnm sodm = new SpcOdiDspnm();
			// sodm.setAcumOutboundQty( );
			sodm.setAgencyOrgCode(param.getValue("AGENCY_ORG_CODE"));
			sodm.setAntibioticCode(param.getValue("ANTIBIOTIC_CODE"));
			sodm.setAtcFlg(param.getValue("ATC_FLG"));
			sodm.setBarCode(param.getValue("BAR_CODE"));
			sodm.setBatchSeq1(param.getInt("BATCH_SEQ1"));
			sodm.setBatchSeq2(param.getInt("BATCH_SEQ2"));
			sodm.setBatchSeq3(param.getInt("BATCH_SEQ3"));
			sodm.setBedNo(parmRow.getValue("BED_NO"));
			sodm.setBillFlg(parmRow.getValue("BILL_FLG"));
			// sodm.setBoxEslId( );
			sodm.setCancelDosageQty(parmRow.getDouble("CANCEL_DOSAGE_QTY"));
			sodm.setCancelrsnCode(param.getValue("CANCELRSN_CODE"));
			sodm.setCaseNo(parmRow.getValue("CASE_NO"));
			sodm.setCashierDate(StringTool.getString(param
					.getTimestamp("CASHIER_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodm.setCashierUser(param.getValue("CASHIER_USER"));
			sodm.setCat1Type(parmRow.getValue("CAT1_TYPE"));
			sodm.setCtrldrugclassCode(param.getValue("CTRLDRUGCLASS_CODE"));
			sodm.setDcDate(StringTool.getString(param.getTimestamp("DC_DATE"),
					"yyyy-MM-dd HH:mm:ss"));
			sodm.setDcDrCode(param.getValue("DC_DR_CODE"));
			sodm.setDcNsCheckDate(StringTool.getString(param
					.getTimestamp("DC_NS_CHECK_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodm.setDcTot(param.getDouble("DC_TOT"));
			sodm.setDctTakeQty(param.getInt("DCT_TAKE_QTY"));
			sodm.setDctagentCode(param.getValue("DCTAGENT_CODE"));
			sodm.setDctagentFlg(param.getValue("DCTAGENT_FLG"));
			sodm.setDctexcepCode(param.getValue("DCTEXCEP_CODE"));
			sodm.setDecoctCode(param.getValue("DECOCT_CODE"));
			sodm.setDecoctDate(StringTool.getString(param
					.getTimestamp("DECOCT_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodm.setDecoctRemark(param.getValue("DECOCT_REMARK"));
			sodm.setDecoctUser(param.getValue("DECOCT_USER"));
			sodm.setDegreeCode(param.getValue("DEGREE_CODE"));
			sodm.setDeptCode(parmRow.getValue("DEPT_CODE"));
			sodm.setDiscountRate(parmRow.getDouble("DISCOUNT_RATE"));
			sodm.setDispenseEffDate(StringTool.getString(param
					.getTimestamp("DISPENSE_EFF_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodm.setDispenseEndDate(StringTool.getString(param
					.getTimestamp("DISPENSE_END_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodm.setDispenseFlg(param.getValue("DISPENSE_FLG"));
			sodm.setDispenseQty(parmRow.getDouble("DISPENSE_QTY"));
			sodm.setDispenseQty1(param.getDouble("DISPENSE_QTY1"));
			sodm.setDispenseQty2(param.getDouble("DISPENSE_QTY2"));
			sodm.setDispenseQty3(param.getDouble("DISPENSE_QTY3"));
			sodm.setDispenseUnit(parmRow.getValue("DISPENSE_UNIT"));
			sodm.setDosageQty(param.getDouble("DOSAGE_QTY"));
			sodm.setDosageUnit(param.getValue("DOSAGE_UNIT"));
			sodm.setDoseType(parmRow.getValue("DOSE_TYPE"));
			sodm.setDrNote(param.getValue("DR_NOTE"));
			sodm.setDspnDate(StringTool.getString(param
					.getTimestamp("DSPN_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodm.setDspnKind(param.getValue("DSPN_KIND"));
			sodm.setDspnUser(parmRow.getValue("DSPN_USER"));
			sodm.setEndDttm(parmRow.getValue("END_DTTM"));
			sodm.setExecDeptCode(parmRow.getValue("EXEC_DEPT_CODE"));
			sodm.setFinalType(param.getValue("FINAL_TYPE"));
			sodm.setFreqCode(param.getValue("FREQ_CODE"));
			sodm.setGiveboxFlg(parmRow.getValue("GIVEBOX_FLG"));
			sodm.setGoodsDesc(param.getValue("GOODS_DESC"));
			sodm.setHideFlg(param.getValue("HIDE_FLG"));
			sodm.setIbsCaseNoSeq(parmRow.getInt("IBS_CASE_NO_SEQ"));
			sodm.setIbsSeqNo(parmRow.getInt("IBS_SEQ_NO"));
			sodm.setInjpracGroup(param.getInt("INJPRAC_GROUP"));
			// sodm.setIntgmedNo( );
			sodm.setIpdNo(parmRow.getValue("IPD_NO"));
			// sodm.setIsIntg( );
			sodm.setLinkNo(param.getValue("LINK_NO"));
			sodm.setLinkmainFlg(param.getValue("LINKMAIN_FLG"));
			sodm.setLisReDate(StringTool.getString(param
					.getTimestamp("LIS_RE_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodm.setLisReUser(param.getValue("LIS_RE_USER"));
			sodm.setMediQty(param.getDouble("MEDI_QTY"));
			sodm.setMediUnit(param.getValue("MEDI_UNIT"));
			sodm.setMrNo(parmRow.getValue("MR_NO"));
			sodm.setNhiPrice(parmRow.getDouble("NHI_PRICE"));
			sodm.setNsExecCode(param.getValue("NS_EXEC_CODE"));
			sodm.setNsExecDate(StringTool.getString(param
					.getTimestamp("NS_EXEC_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodm.setNsExecDcCode(parmRow.getValue("NS_EXEC_DC_CODE"));
			sodm.setNsExecDcDate(StringTool.getString(param
					.getTimestamp("NS_EXEC_DC_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodm.setNsUser(param.getValue("NS_USER"));
			sodm.setOptDate(StringTool.getString(
					param.getTimestamp("OPT_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodm.setOptTerm(parmRow.getValue("OPT_TERM"));
			sodm.setOptUser(parmRow.getValue("OPT_USER"));
			sodm.setOptitemCode(param.getValue("OPTITEM_CODE"));
			sodm.setOrderCat1Code(parmRow.getValue("ORDER_CAT1_CODE"));
			sodm.setOrderCode(parmRow.getValue("ORDER_CODE"));
			sodm.setOrderDate(StringTool.getString(parmRow
					.getTimestamp("ORDER_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodm.setOrderDeptCode(parmRow.getValue("ORDER_DEPT_CODE"));
			sodm.setOrderDesc(parmRow.getValue("ORDER_DESC"));
			sodm.setOrderDrCode(parmRow.getValue("ORDER_DR_CODE"));
			sodm.setOrderNo(parmRow.getValue("ORDER_NO"));
			sodm.setOrderSeq(parmRow.getInt("ORDER_SEQ"));
			sodm.setOrdersetCode(param.getValue("ORDERSET_CODE"));
			sodm.setOrdersetGroupNo(param.getValue("ORDERSET_GROUP_NO"));
			// sodm.setOrgCode(parmRow.getValue("EXEC_DEPT_CODE"));///////////////////////////
			sodm.setOwnAmt(param.getDouble("OWN_AMT"));
			sodm.setOwnPrice(parmRow.getDouble("OWN_PRICE"));
			sodm.setPackageAmt(param.getInt("PACKAGE_AMT"));
			sodm.setParentCaseNo(param.getValue("PARENT_CASE_NO"));
			sodm.setParentOrderNo(param.getValue("PARENT_ORDER_NO"));
			sodm.setParentOrderSeq(param.getInt("PARENT_ORDER_SEQ"));
			sodm.setParentStartDttm(parmRow.getValue("START_DTTM"));
			sodm.setPhaCheckCode(param.getValue("PHA_CHECK_CODE"));
			sodm.setPhaCheckDate(StringTool.getString(param
					.getTimestamp("PHA_CHECK_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodm.setPhaDispenseCode(param.getValue("PHA_DISPENSE_CODE"));
			sodm.setPhaDispenseDate(StringTool.getString(param
					.getTimestamp("PHA_DISPENSE_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodm.setPhaDispenseNo(param.getValue("PHA_DISPENSE_NO"));
			sodm.setPhaDosageCode(param.getValue("PHA_DOSAGE_CODE"));
			sodm.setPhaDosageDate(StringTool.getString(param
					.getTimestamp("PHA_DOSAGE_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodm.setPhaRetnCode(parmRow.getValue("PHA_RETN_CODE"));
			sodm.setPhaRetnDate(StringTool.getString(parmRow
					.getTimestamp("PHA_RETN_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodm.setPhaType(parmRow.getValue("PHA_TYPE"));
			sodm.setPrescriptNo(param.getValue("PRESCRIPT_NO"));
			sodm.setPresrtNo(param.getValue("PRESRT_NO"));
			sodm.setRegionCode(parmRow.getValue("REGION_CODE"));
			sodm.setReturnQty1(param.getDouble("RETURN_QTY1"));
			sodm.setReturnQty2(param.getDouble("RETURN_QTY2"));
			sodm.setReturnQty3(param.getDouble("RETURN_QTY3"));
			sodm.setRouteCode(parmRow.getValue("ROUTE_CODE"));
			sodm.setRpttypeCode(param.getValue("RPTTYPE_CODE"));
			sodm.setRtnDosageQty(parmRow.getDouble("RTN_DOSAGE_QTY"));
			sodm.setRtnDosageUnit(parmRow.getValue("RTN_DOSAGE_UNIT"));
			sodm.setRtnNo(parmRow.getValue("RTN_NO"));
			sodm.setRtnNoSeq(parmRow.getInt("RTN_NO_SEQ"));
			sodm.setRxNo(param.getValue("RX_NO"));
			sodm.setSendDctDate(StringTool.getString(param
					.getTimestamp("SEND_DCT_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodm.setSendDctUser(param.getValue("SEND_DCT_USER"));
			sodm.setSendOrgDate(StringTool.getString(param
					.getTimestamp("SEND_ORG_DATE"), "yyyy-MM-dd HH:mm:ss"));
			sodm.setSendOrgUser(param.getValue("SEND_ORG_USER"));
			sodm.setSendatcDttm(param.getValue("SENDATC_DTTM"));
			sodm.setSendatcFlg(param.getValue("SENDATC_FLG"));
			sodm.setSetmainFlg(param.getValue("SETMAIN_FLG"));
			sodm.setSpecification(param.getValue("SPECIFICATION"));
			sodm.setStartDttm(param.getValue("START_DTTM"));
			sodm.setStationCode(parmRow.getValue("STATION_CODE"));
			sodm.setTakeDays(param.getInt("TAKE_DAYS"));
			// sodm.setTakemedNo( );
			// sodm.setTakemedOrg( );
			sodm.setTotAmt(param.getDouble("TOT_AMT"));
			sodm.setTransmitRsnCode(parmRow.getValue("TRANSMIT_RSN_CODE"));
			// sodm.setTurnEslId( );
			sodm.setUrgentFlg(param.getValue("URGENT_FLG"));
			sodm.setVerifyinPrice1(param.getDouble("VERIFYIN_PRICE1"));
			sodm.setVerifyinPrice2(param.getDouble("VERIFYIN_PRICE2"));
			sodm.setVerifyinPrice3(param.getDouble("VERIFYIN_PRICE3"));
			sodm.setVsDrCode(parmRow.getValue("VS_DR_CODE"));
			sodm.setServiceLevel(param.getValue("SERVICE_LEVEL"));
			List<SpcOdiDspnd> spcOdiDspndList = new ArrayList<SpcOdiDspnd>();
			spcOdiDspndList.add(sodd);
			sodm.setSpcOdiDspnds(spcOdiDspndList);
			SpcIndStock sis = new SpcIndStock();
			sis.setDosageQty(param.getDouble("DOSAGE_QTY"));
			sis.setOptDate(StringTool.getString(TJDODBTool.getInstance()
					.getDBTime(), "yyyy-MM-dd HH:mm:ss"));
			sis.setOptTerm(parmRow.getValue("OPT_TERM"));
			sis.setOptUser(parmRow.getValue("OPT_USER"));
			sis.setOrderCode(parmRow.getValue("ORDER_CODE"));
			sis.setOrderDesc(parmRow.getValue("ORDER_DESC"));
			sis.setOrgCode(parmRow.getValue("EXEC_DEPT_CODE"));// /////////////////////////
			sis.setServiceLevel(param.getValue("SERVICE_LEVEL"));

			spcOdiDspnmList.add(sodm);
			spcIndStockList.add(sis);
		}
		if (spcflg.equals("N"))// 非国药流程返回 shibl add 20130423
			connection.commit();
		else {
			// ====================国药传送数据=================add by wanglong
			// 20130111
			SpcOdiDspnms odiDspnms = new SpcOdiDspnms();
			odiDspnms.setSpcOdiDspnms(spcOdiDspnmList);
			odiDspnms.setSpcIndStocks(spcIndStockList);
			String returnStr = "";
			try {
				returnStr = SpcOdiService_SpcOdiServiceImplPort_Client
						.onUpdateRtnCfm(odiDspnms);
				// System.out.println("国药回传结果"+returnStr);
				if (returnStr.equals("success")) {
					connection.commit();
				} else {
					result.setErrCode(-1);
					connection.rollback();
				}
			} catch (Exception e) {
				// TODO: handle exception
				connection.rollback();
				result.setErr(-1, "调用物联网Services失败");
			}
		}
		// connection.commit();//delete by wanglong 20130111
		// =================================================
		connection.close();
		return result;
	}

	/**
	 * 根据实际的退药量更新发药主记录中的库存数量
	 * 
	 */
	private TParm updateRealReturnQty(TParm parmRow, TConnection conn) {
		TParm result = new TParm();
		// PARENT_ORDER_SEQ=1 PARENT_CASE_NO=120227000036 CANCEL_DOSAGE_QTY=5.0
		// BATCH_SEQ1
		String parmtOrderSeq = parmRow.getValue("PARENT_ORDER_SEQ");
		String parentCaseNo = parmRow.getValue("PARENT_CASE_NO");
		String parentOrderNo = parmRow.getValue("PARENT_ORDER_NO");
		String parentStartDttm = parmRow.getValue("PARENT_START_DTTM");
		String changeDosageQty = parmRow.getValue("CANCEL_DOSAGE_QTY");
		String batchSeq1 = parmRow.getValue("BATCH_SEQ1");// 退药时仅记录一笔batchSeq的药品，故只取bacthSeq1
		for (int i = 1; i <= 3; i++) {
			StringBuffer sqlbf = new StringBuffer();
			sqlbf.append(" UPDATE ODI_DSPNM SET RETURN_QTY" + i + "=RETURN_QTY"
					+ i + "-" + changeDosageQty + "");
			sqlbf.append(" WHERE CASE_NO= '" + parentCaseNo + "' ");
			sqlbf.append(" AND ORDER_NO= '" + parentOrderNo + "' ");
			sqlbf.append(" AND ORDER_SEQ= '" + parmtOrderSeq + "' ");
			sqlbf.append(" AND START_DTTM= '" + parentStartDttm + "' ");
			sqlbf.append(" AND BATCH_SEQ" + i + "=" + batchSeq1 + " ");
			Map update = TJDODBTool.getInstance().update(sqlbf.toString());
			result = new TParm(update);
			if (result.getErrCode() < 0) {
				System.out.println(result.getErrText());
				return result;
			}
		}
		return result;

	}

	/**
	 * 
	 * 药房退药扣库逻辑
	 * 
	 * @param uddRegParm
	 * @param conn
	 * @return
	 */
	public TParm uddRtnMed(TParm uddRegParm, String serviceLevel,
			TConnection conn) {
		TParm returnParm = new TParm();
		TParm uddrtnData = UddRtnRgsTool.getInstance().getUDDRTNData(
				uddRegParm, conn);
		for (int i = 0; i < uddrtnData.getCount("BATCH_SEQ1"); i++) {
			String orgCode = uddRegParm.getValue("EXEC_DEPT_CODE");
			String orderCode = uddRegParm.getValue("ORDER_CODE");
			String optUser = uddRegParm.getValue("OPT_USER");
			Timestamp optDate = TJDODBTool.getInstance().getDBTime();
			String optTerm = uddRegParm.getValue("OPT_TERM");
			double dosageQty = 0;
			int batchSeq = 0;
			double realDosageQty = 0;
			double changQty = 0;
			// System.out.println("=========:"+uddrtnData);
			for (int j = 1; j <= 3; j++) {
				dosageQty = uddrtnData.getDouble("DISPENSE_QTY" + j, i);
				changQty = uddRegParm.getDouble("CANCEL_DOSAGE_QTY");
				realDosageQty = dosageQty - changQty;
				batchSeq = uddrtnData.getInt("BATCH_SEQ" + j, i);// 取字段错误，原：DISPENSE_QTY
																	// 现：BATCH_SEQ
																	// luhai
																	// 2012-3-3
				// System.out.println("batch_seq :"+batchSeq);
				// System.out.println("退药数量"+dosageQty);
				if (realDosageQty <= 0)
					continue;
				returnParm = INDTool.getInstance().regressIndStockWithBatchSeq(
						orgCode, orderCode, batchSeq, realDosageQty, optUser,
						optDate, optTerm, serviceLevel, conn);
				if (returnParm.getErrCode() < 0) {
					System.out.println(returnParm.getErrText());
					return returnParm;
				}
			}
		}
		return returnParm;
	}

	/**
	 * 根据CASE_NO取得服务等级
	 * 
	 * @param case_no
	 *            String
	 * @return String
	 */
	private String getServiceLevel(String case_no) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", case_no);
		TParm result = ADMInpTool.getInstance().selectall(parm);
		return result.getValue("SERVICE_LEVEL", 0);
	}

}
