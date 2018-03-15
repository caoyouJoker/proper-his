package action.pes;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dongyang.action.TAction;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

import jdo.pes.PESTool;
import jdo.sys.SystemTool;

import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: PES
 * </p>
 * 
 * <p>
 * Description:PES
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author zhangp 20121010
 * @version 2.0
 */
public class PESAction extends TAction {
	public PESAction() {
	}

	/**
	 * 选取rx_no后写入pes的表
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm saveSelectRx(TParm parm) {
		TConnection connection = getConnection();
		String case_no = parm.getValue("CASE_NO");
		String rx_no = parm.getValue("RX_NO");
		TParm tmp = new TParm();
		tmp.setData("CASE_NO", case_no);
		tmp.setData("RX_NO", rx_no);
		TParm parmD = PESTool.getInstance().selectRxD(tmp);
		TParm seqParm = new TParm();
		seqParm.setData("SEQ", 1);
		//add by huangtt 20131203 start
		TParm zdParm = new TParm();
		zdParm.setData("CASE_NO", case_no);
		TParm parmZD = PESTool.getInstance().selectZD(zdParm);
		String zd = parmD.getValue("ICD_CODE", 0);
		for(int i=0;i<parmZD.getCount();i++){
			zd =zd +";"+ parmZD.getValue("ICD_CODE", i);
		}
//		System.out.println("zd=="+zd);
		parm.setData("ICD_CODE", zd);
		//add by huangtt 20131203 end 
		TParm result = insirtPesMD(parmD, parm, connection, seqParm);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	
	

	/**
	 * 选取CASE_NO后写入pes的表
	 * ADD CAOYONG 20140217
	 * 
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm saveSelectIpdRx(TParm parm) {
		TConnection connection = getConnection();
		String case_no = parm.getValue("CASE_NO");
		TParm tmp = new TParm();
		tmp.setData("CASE_NO", case_no);
		TParm parmD = PESTool.getInstance().selectIpdQ(tmp);
		//TParm seqParm = new TParm();
		//seqParm.setData("SEQ", 1);
		//add by huangtt 20131203 start
		//TParm zdParm = new TParm();
		//zdParm.setData("CASE_NO", case_no);
		/*TParm parmZD = PESTool.getInstance().selectZD(zdParm);
		String zd = parmD.getValue("ICD_CODE", 0);
		for(int i=0;i<parmZD.getCount();i++){
			zd =zd +";"+ parmZD.getValue("ICD_CODE", i);
		}*/
//		System.out.println("zd=="+zd);
		//parm.setData("ICD_CODE", zd);
		//add by huangtt 20131203 end 
		TParm result = insirtIpdPesMD(parmD, parm, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	

	/**
	 * 递归插入PESM和PESD
	 * 
	 * @param parmD
	 * @param parm
	 * @param connection
	 * @param seqParm
	 * @return
	 */
	private TParm insirtPesMD(TParm parmD, TParm parm, TConnection connection,
			TParm seqParm) {
		int order_qty = 0;// 处方药品数量
		int antibiotic_qty = 0;// 抗菌药品数量
		int inject_qty = 0;// 注射药品数量
		int goods_qty = 0;// 药品通用名数量
		int base_qty = 0;// 药品通用名数量
		double rx_total = 0;// 处方金额
		int count = 5;
//		if (parmD.getCount("CASE_NO") < 5) {
			count = parmD.getCount("CASE_NO");
//		}
		for (int i = 0; i < count; i++) {
			order_qty++;
			if (!parmD.getValue("ANTIBIOTIC_CODE", i).equals("")) {
				antibiotic_qty++;
			}
			if (!parmD.getValue("GOODS_DESC", i).equals("")) {
				goods_qty++;
			}
			if (parmD.getValue("SYS_GRUG_CLASS", i).equals("1")) {
				base_qty++;
			}
			if (parmD.getValue("DOSE_TYPE", i).equals("I") || parmD.getValue("DOSE_TYPE", i).equals("F")) {
				inject_qty++;
			}
			rx_total += parmD.getDouble("AR_AMT", i);
		}
		TParm insertPesM = new TParm();
		insertPesM.setData("TYPE_CODE",
				parm.getValue("TYPE_CODE") == null ? new TNull(String.class)
						: parm.getValue("TYPE_CODE"));
		insertPesM.setData("PES_NO",
				parm.getValue("PES_NO") == null ? new TNull(String.class)
						: parm.getValue("PES_NO"));
		insertPesM.setData("CASE_NO",
				parm.getValue("CASE_NO") == null ? new TNull(String.class)
						: parm.getValue("CASE_NO"));
		insertPesM.setData("SEQ", parm.getValue("SEQ_NO") == null ? new TNull(
				String.class) : parm.getValue("SEQ_NO"));
		insertPesM.setData("RX_NO",
				parmD.getValue("RX_NO", 0) == null ? new TNull(String.class)
						: parmD.getValue("RX_NO", 0));
		insertPesM.setData("PES_RX_NO", parmD.getValue("RX_NO", 0) + "_"
				+ seqParm.getValue("SEQ"));
		insertPesM.setData("EVAL_CODE",
				parm.getValue("EVAL_CODE") == null ? new TNull(String.class)
						: parm.getValue("EVAL_CODE"));
		insertPesM.setData("MR_NO", parm.getValue("MR_NO") == null ? new TNull(
				String.class) : parm.getValue("MR_NO"));
		insertPesM.setData("PAT_NAME",
				parm.getValue("PAT_NAME") == null ? new TNull(String.class)
						: parm.getValue("PAT_NAME"));
		insertPesM.setData("ORDER_DATE",
				parmD.getTimestamp("ORDER_DATE", 0) == null ? new TNull(
						Timestamp.class) : parmD.getTimestamp("ORDER_DATE", 0));
		insertPesM.setData("AGE", parm.getValue("AGE") == null ? new TNull(
				String.class) : parm.getValue("AGE"));
		insertPesM.setData("DIAG",
				parm.getValue("ICD_CODE") == null ? new TNull(String.class)
						: parm.getValue("ICD_CODE"));// 诊断
		insertPesM.setData("ORDER_QTY", order_qty);
		insertPesM.setData("ANTIBIOTIC_QTY", antibiotic_qty);
		insertPesM.setData("BASE_QTY", base_qty);
		insertPesM.setData("GOODS_QTY", goods_qty);
		insertPesM.setData("INJECT_QTY", inject_qty);
		insertPesM.setData("RX_TOTAL", StringTool.round(rx_total, 2));
		insertPesM.setData("DR_CODE",
				parm.getValue("DR_CODE") == null ? new TNull(String.class)
						: parm.getValue("DR_CODE"));
		insertPesM.setData("PHA_DOSAGE_CODE", parmD.getValue("PHA_DOSAGE_CODE",
				0) == null ? new TNull(String.class) : parmD.getValue(
				"PHA_DOSAGE_CODE", 0));// 配药药师
		insertPesM.setData("PHA_DISPENSE_CODE", parmD.getValue(
				"PHA_DISPENSE_CODE", 0) == null ? new TNull(String.class)
				: parmD.getValue("PHA_DISPENSE_CODE", 0));// 发药药师
		insertPesM.setData("OPT_USER", parm.getValue("OPT_USER"));
		insertPesM.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
		insertPesM.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		TParm result = PESTool.getInstance().insertPESOPDM(insertPesM,
				connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		TParm insertPesD;
		for (int i = 0; i < count; i++) {
			insertPesD = getInsertPesD(parmD, i);
			insertPesD.setData("OPT_USER", parm.getValue("OPT_USER"));
			insertPesD.setData("PES_NO", parm.getValue("PES_NO"));
			insertPesD.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
			insertPesD.setData("OPT_TERM", parm.getValue("OPT_TERM"));
			insertPesD.setData("PES_RX_NO", parmD.getValue("RX_NO", 0) + "_"
					+ seqParm.getValue("SEQ"));
			result = PESTool.getInstance()
					.insertPESOPDD(insertPesD, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		for (int i = 0; i < count; i++) {
			parmD.removeRow(0);
		}
		if (parmD.getCount("CASE_NO") > 0) {
			seqParm.setData("SEQ", seqParm.getInt("SEQ") + 1);
			parm.setData("SEQ_NO", parm.getInt("SEQ_NO") + 1);
			insirtPesMD(parmD, parm, connection, seqParm);
		} else {
			seqParm.setData("SEQ_NO", parm.getInt("SEQ_NO") + 1);
		}
		return seqParm;
	}

	/**
	 * 插入pesResult
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertPESResult(TParm parm) {
		TParm insertPesResult = new TParm();
		insertPesResult.setData("TYPE_CODE",
				parm.getValue("TYPE_CODE") == null ? new TNull(String.class)
						: parm.getValue("TYPE_CODE"));
		insertPesResult.setData("PES_NO",
				parm.getValue("PES_NO") == null ? new TNull(String.class)
						: parm.getValue("PES_NO"));
		insertPesResult.setData("EVAL_CODE",
				parm.getValue("EVAL_CODE") == null ? new TNull(String.class)
						: parm.getValue("EVAL_CODE"));
		insertPesResult.setData("OPT_USER",
				parm.getValue("OPT_USER") == null ? new TNull(String.class)
						: parm.getValue("OPT_USER"));
		insertPesResult
				.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
		insertPesResult.setData("OPT_TERM",
				parm.getValue("OPT_TERM") == null ? new TNull(String.class)
						: parm.getValue("OPT_TERM"));
		insertPesResult
				.setData("PES_DATE", StringTool.getTimestamp(new Date()));
		TParm result = PESTool.getInstance().insertPESRESULT(insertPesResult);
		return result;
	}
	/**
	 * 插入住院pesResult
	 * add caoyong 20140214
	 * @param parm
	 * @return
	 */
	public TParm insertIDPPESResult(TParm parm) {
		TParm insertPesResult = new TParm();
		//seq=this.getMaxRSeq(parm,"PES_IPDRESULT");
		insertPesResult.setData("TYPE_CODE",
				parm.getValue("TYPE_CODE") == null ? new TNull(String.class)
						: parm.getValue("TYPE_CODE"));
		insertPesResult.setData("PES_NO",
				parm.getValue("PES_NO") == null ? new TNull(String.class)
						: parm.getValue("PES_NO"));
		
		//insertPesResult.setData("SEQ",seq+1);
		
		
		insertPesResult.setData("EVAL_CODE",
				parm.getValue("EVAL_CODE") == null ? new TNull(String.class)
						: parm.getValue("EVAL_CODE"));
		insertPesResult.setData("OPT_USER",
				parm.getValue("OPT_USER") == null ? new TNull(String.class)
						: parm.getValue("OPT_USER"));
		insertPesResult
				.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
		insertPesResult.setData("OPT_TERM",
				parm.getValue("OPT_TERM") == null ? new TNull(String.class)
						: parm.getValue("OPT_TERM"));
		insertPesResult
				.setData("PES_DATE", StringTool.getTimestamp(new Date()));
		TParm result = PESTool.getInstance().insertIpdPESRESULT(insertPesResult);
		return result;
	}
	
	/**
	 * 取得要插入pesdd的数据
	 * 
	 * @param parmD
	 * @param i
	 * @return
	 */
	private TParm getInsertPesD(TParm parmD, int i) {
		TParm insertPesD = new TParm();
		insertPesD.setData("CASE_NO",
				parmD.getValue("CASE_NO", i) == null ? new TNull(String.class)
						: parmD.getValue("CASE_NO", i));
		insertPesD.setData("RX_NO",
				parmD.getValue("RX_NO", i) == null ? new TNull(String.class)
						: parmD.getValue("RX_NO", i));
		insertPesD.setData("SEQ_NO",
				parmD.getValue("SEQ_NO", i) == null ? new TNull(Integer.class)
						: parmD.getValue("SEQ_NO", i));
		insertPesD.setData("REGION_CODE",
				parmD.getValue("REGION_CODE", i) == null ? new TNull(
						String.class) : parmD.getValue("REGION_CODE", i));
		insertPesD.setData("MR_NO",
				parmD.getValue("MR_NO", i) == null ? new TNull(String.class)
						: parmD.getValue("MR_NO", i));
		insertPesD.setData("ADM_TYPE",
				parmD.getValue("ADM_TYPE", i) == null ? new TNull(String.class)
						: parmD.getValue("ADM_TYPE", i));
		insertPesD.setData("PHSY_FLG",
				parmD.getValue("PHSY_FLG", i) == null ? new TNull(String.class)
						: parmD.getValue("PHSY_FLG", i));
		insertPesD.setData("RX_TYPE",
				parmD.getValue("RX_TYPE", i) == null ? new TNull(String.class)
						: parmD.getValue("RX_TYPE", i));
		insertPesD.setData("RELEASE_FLG",
				parmD.getValue("RELEASE_FLG", i) == null ? new TNull(
						String.class) : parmD.getValue("RELEASE_FLG", i));
		insertPesD.setData("LINKMAIN_FLG",
				parmD.getValue("LINKMAIN_FLG", i) == null ? new TNull(
						String.class) : parmD.getValue("LINKMAIN_FLG", i));
		insertPesD.setData("LINK_NO",
				parmD.getValue("LINK_NO", i) == null ? new TNull(String.class)
						: parmD.getValue("LINK_NO", i));
		insertPesD.setData("ORDER_CODE",
				parmD.getValue("ORDER_CODE", i) == null ? new TNull(
						String.class) : parmD.getValue("ORDER_CODE", i));
		insertPesD.setData("ORDER_DESC",
				parmD.getValue("ORDER_DESC", i) == null ? new TNull(
						String.class) : parmD.getValue("ORDER_DESC", i));
		insertPesD.setData("GOODS_DESC",
				parmD.getValue("GOODS_DESC", i) == null ? new TNull(
						String.class) : parmD.getValue("GOODS_DESC", i));
		insertPesD.setData("SPECIFICATION",
				parmD.getValue("SPECIFICATION", i) == null ? new TNull(
						String.class) : parmD.getValue("SPECIFICATION", i));
		insertPesD.setData("ORDER_CAT1_CODE", parmD.getValue("ORDER_CAT1_CODE",
				i) == null ? new TNull(String.class) : parmD.getValue(
				"ORDER_CAT1_CODE", i));
		insertPesD
				.setData("CAT1_TYPE",
						parmD.getValue("CAT1_TYPE", i) == null ? new TNull(
								String.class) : parmD.getValue("CAT1_TYPE", i));
		insertPesD.setData("MEDI_QTY",
				parmD.getValue("MEDI_QTY", i) == null ? new TNull(Double.class)
						: parmD.getValue("MEDI_QTY", i));
		insertPesD
				.setData("MEDI_UNIT",
						parmD.getValue("MEDI_UNIT", i) == null ? new TNull(
								String.class) : parmD.getValue("MEDI_UNIT", i));
		insertPesD
				.setData("FREQ_CODE",
						parmD.getValue("FREQ_CODE", i) == null ? new TNull(
								String.class) : parmD.getValue("FREQ_CODE", i));
		insertPesD.setData("ROUTE_CODE",
				parmD.getValue("ROUTE_CODE", i) == null ? new TNull(
						String.class) : parmD.getValue("ROUTE_CODE", i));
		insertPesD.setData("TAKE_DAYS",
				parmD.getValue("TAKE_DAYS", i) == null ? new TNull(
						Integer.class) : parmD.getValue("TAKE_DAYS", i));
		insertPesD.setData("DOSAGE_QTY",
				parmD.getValue("DOSAGE_QTY", i) == null ? new TNull(
						Double.class) : parmD.getValue("DOSAGE_QTY", i));
		insertPesD.setData("DOSAGE_UNIT",
				parmD.getValue("DOSAGE_UNIT", i) == null ? new TNull(
						String.class) : parmD.getValue("DOSAGE_UNIT", i));
		insertPesD.setData("DISPENSE_QTY",
				parmD.getValue("DISPENSE_QTY", i) == null ? new TNull(
						Double.class) : parmD.getValue("DISPENSE_QTY", i));
		insertPesD.setData("DISPENSE_UNIT",
				parmD.getValue("DISPENSE_UNIT", i) == null ? new TNull(
						String.class) : parmD.getValue("DISPENSE_UNIT", i));
		insertPesD.setData("GIVEBOX_FLG",
				parmD.getValue("GIVEBOX_FLG", i) == null ? new TNull(
						String.class) : parmD.getValue("GIVEBOX_FLG", i));
		insertPesD.setData("DISPENSE_FLG",
				parmD.getValue("DISPENSE_FLG", i) == null ? new TNull(
						String.class) : parmD.getValue("DISPENSE_FLG", i));
		insertPesD
				.setData("OWN_PRICE",
						parmD.getValue("OWN_PRICE", i) == null ? new TNull(
								Double.class) : parmD.getValue("OWN_PRICE", i));
		insertPesD
				.setData("NHI_PRICE",
						parmD.getValue("NHI_PRICE", i) == null ? new TNull(
								Double.class) : parmD.getValue("NHI_PRICE", i));
		insertPesD.setData("DISCOUNT_RATE",
				parmD.getValue("DISCOUNT_RATE", i) == null ? new TNull(
						Double.class) : parmD.getValue("DISCOUNT_RATE", i));
		insertPesD.setData("OWN_AMT",
				parmD.getValue("OWN_AMT", i) == null ? new TNull(Double.class)
						: parmD.getValue("OWN_AMT", i));
		insertPesD.setData("AR_AMT",
				parmD.getValue("AR_AMT", i) == null ? new TNull(Double.class)
						: parmD.getValue("AR_AMT", i));
		insertPesD.setData("DR_NOTE",
				parmD.getValue("DR_NOTE", i) == null ? new TNull(String.class)
						: parmD.getValue("DR_NOTE", i));
		insertPesD.setData("DR_CODE",
				parmD.getValue("DR_CODE", i) == null ? new TNull(String.class)
						: parmD.getValue("DR_CODE", i));
		insertPesD.setData("ORDER_DATE",
				parmD.getTimestamp("ORDER_DATE", i) == null ? new TNull(
						Timestamp.class) : parmD.getTimestamp("ORDER_DATE", i));
		insertPesD
				.setData("DEPT_CODE",
						parmD.getValue("DEPT_CODE", i) == null ? new TNull(
								String.class) : parmD.getValue("DEPT_CODE", i));
		insertPesD.setData("EXEC_DEPT_CODE",
				parmD.getValue("EXEC_DEPT_CODE", i) == null ? new TNull(
						String.class) : parmD.getValue("EXEC_DEPT_CODE", i));
		insertPesD.setData("EXEC_DR_CODE",
				parmD.getValue("EXEC_DR_CODE", i) == null ? new TNull(
						String.class) : parmD.getValue("EXEC_DR_CODE", i));
		insertPesD.setData("INSPAY_TYPE",
				parmD.getValue("INSPAY_TYPE", i) == null ? new TNull(
						String.class) : parmD.getValue("INSPAY_TYPE", i));
		insertPesD.setData("PHA_TYPE",
				parmD.getValue("PHA_TYPE", i) == null ? new TNull(String.class)
						: parmD.getValue("PHA_TYPE", i));
		insertPesD
				.setData("DOSE_TYPE",
						parmD.getValue("DOSE_TYPE", i) == null ? new TNull(
								String.class) : parmD.getValue("DOSE_TYPE", i));
		insertPesD.setData("EXPENSIVE_FLG",
				parmD.getValue("EXPENSIVE_FLG", i) == null ? new TNull(
						String.class) : parmD.getValue("EXPENSIVE_FLG", i));
		insertPesD.setData("PRINTTYPEFLG_INFANT", parmD.getValue(
				"PRINTTYPEFLG_INFANT", i) == null ? new TNull(String.class)
				: parmD.getValue("PRINTTYPEFLG_INFANT", i));
		insertPesD.setData("CTRLDRUGCLASS_CODE", parmD.getValue(
				"CTRLDRUGCLASS_CODE", i) == null ? new TNull(String.class)
				: parmD.getValue("CTRLDRUGCLASS_CODE", i));
		insertPesD
				.setData("PRESRT_NO",
						parmD.getValue("PRESRT_NO", i) == null ? new TNull(
								String.class) : parmD.getValue("PRESRT_NO", i));
		insertPesD.setData("PRESCRIPT_NO",
				parmD.getValue("PRESCRIPT_NO", i) == null ? new TNull(
						Integer.class) : parmD.getValue("PRESCRIPT_NO", i));
		insertPesD.setData("RECEIPT_NO",
				parmD.getValue("RECEIPT_NO", i) == null ? new TNull(
						String.class) : parmD.getValue("RECEIPT_NO", i));
		insertPesD
				.setData("CTZ1_CODE",
						parmD.getValue("CTZ1_CODE", i) == null ? new TNull(
								String.class) : parmD.getValue("CTZ1_CODE", i));
		insertPesD.setData("PHA_CHECK_CODE",
				parmD.getValue("PHA_CHECK_CODE", i) == null ? new TNull(
						String.class) : parmD.getValue("PHA_CHECK_CODE", i));
		insertPesD.setData("PHA_CHECK_DATE", parmD.getTimestamp(
				"PHA_CHECK_DATE", i) == null ? new TNull(Timestamp.class)
				: parmD.getTimestamp("PHA_CHECK_DATE", i));
		insertPesD.setData("PHA_DOSAGE_CODE", parmD.getValue("PHA_DOSAGE_CODE",
				i) == null ? new TNull(String.class) : parmD.getValue(
				"PHA_DOSAGE_CODE", i));
		insertPesD.setData("PHA_DOSAGE_DATE", parmD.getTimestamp(
				"PHA_DOSAGE_DATE", i) == null ? new TNull(Timestamp.class)
				: parmD.getTimestamp("PHA_DOSAGE_DATE", i));
		insertPesD.setData("PHA_DISPENSE_CODE", parmD.getValue(
				"PHA_DISPENSE_CODE", i) == null ? new TNull(String.class)
				: parmD.getValue("PHA_DISPENSE_CODE", i));
		insertPesD.setData("PHA_DISPENSE_DATE", parmD.getTimestamp(
				"PHA_DISPENSE_DATE", i) == null ? new TNull(Timestamp.class)
				: parmD.getTimestamp("PHA_DISPENSE_DATE", i));
		insertPesD.setData("PHA_RETN_CODE",
				parmD.getValue("PHA_RETN_CODE", i) == null ? new TNull(
						String.class) : parmD.getValue("PHA_RETN_CODE", i));
		insertPesD.setData("PHA_RETN_DATE", parmD.getTimestamp("PHA_RETN_DATE",
				i) == null ? new TNull(Timestamp.class) : parmD.getTimestamp(
				"PHA_RETN_DATE", i));
		insertPesD.setData("DCTEXCEP_CODE",
				parmD.getValue("DCTEXCEP_CODE", i) == null ? new TNull(
						String.class) : parmD.getValue("DCTEXCEP_CODE", i));
		insertPesD.setData("DCT_TAKE_QTY",
				parmD.getValue("DCT_TAKE_QTY", i) == null ? new TNull(
						Double.class) : parmD.getValue("DCT_TAKE_QTY", i));
		insertPesD.setData("PACKAGE_TOT",
				parmD.getValue("PACKAGE_TOT", i) == null ? new TNull(
						Double.class) : parmD.getValue("PACKAGE_TOT", i));
		return insertPesD;
	}
	/**
	 * 住院取得要插入pesidpdd的数据
	 * add caoyong 20130218
	 * @param parmD
	 * @param i
	 * @return
	 */
	private TParm getInsertPesIpdD(TParm parmD, int i) {
		TParm insertPesD = new TParm();
	insertPesD.setData("CASE_NO",
			parmD.getValue("CASE_NO", i) == null ? new TNull(String.class)
					: parmD.getValue("CASE_NO", i));
	
	insertPesD.setData("ORDER_NO",
			parmD.getValue("ORDER_NO", i) == null ? new TNull(String.class)
					: parmD.getValue("ORDER_NO", i));
	
	insertPesD.setData("ORDER_SEQ",
			parmD.getValue("ORDER_SEQ", i) == null ? new TNull(Integer.class)
					: parmD.getValue("ORDER_SEQ", i));
	
	insertPesD.setData("REGION_CODE",
			parmD.getValue("REGION_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("REGION_CODE", i));
	
	insertPesD.setData("MR_NO",
			parmD.getValue("MR_NO", i) == null ? new TNull(String.class)
					: parmD.getValue("MR_NO", i));
	insertPesD.setData("ADM_TYPE",
			parmD.getValue("ADM_TYPE", i) == null ? new TNull(String.class)
					: parmD.getValue("ADM_TYPE", i));
	
	insertPesD.setData("STATION_CODE",
			parmD.getValue("STATION_CODE", i) == null ? new TNull(String.class)
					: parmD.getValue("STATION_CODE", i));
	
	insertPesD.setData("RX_KIND",
			parmD.getValue("RX_KIND", i) == null ? new TNull(String.class)
					: parmD.getValue("RX_KIND", i));
	
	insertPesD.setData("DEPT_CODE",
			parmD.getValue("DEPT_CODE", i) == null ? new TNull(String.class)
					: parmD.getValue("DEPT_CODE", i));
	
	insertPesD.setData("ADM_TYPE",
			parmD.getValue("ADM_TYPE", i) == null ? new TNull(
					String.class) : parmD.getValue("ADM_TYPE", i));
	/*insertPesD.setData("TEMPORARY_FLG",
			parmD.getValue("TEMPORARY_FLG", i) == null ? new TNull(
					String.class) : parmD.getValue("TEMPORARY_FLG", i));*/
	
	insertPesD.setData("PRESRT_NO",
			parmD.getValue("PRESRT_NO", i) == null ? new TNull(
					String.class) : parmD.getValue("PRESRT_NO", i));
	
	insertPesD.setData("VS_DR_CODE",
			parmD.getValue("VS_DR_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("VS_DR_CODE", i));
	
	insertPesD.setData("BED_NO",
			parmD.getValue("BED_NO", i) == null ? new TNull(String.class)
					: parmD.getValue("BED_NO", i));
	
	
	insertPesD.setData("ORDER_CODE",
			parmD.getValue("ORDER_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("ORDER_CODE", i));
	insertPesD.setData("ORDER_DESC",
			parmD.getValue("ORDER_DESC", i) == null ? new TNull(
					String.class) : parmD.getValue("ORDER_DESC", i));
	insertPesD.setData("GOODS_DESC",
			parmD.getValue("GOODS_DESC", i) == null ? new TNull(
					String.class) : parmD.getValue("GOODS_DESC", i));
	insertPesD.setData("SPECIFICATION",
			parmD.getValue("SPECIFICATION", i) == null ? new TNull(
					String.class) : parmD.getValue("SPECIFICATION", i));
	insertPesD.setData("IPD_NO", parmD.getValue("IPD_NO",
			i) == null ? new TNull(String.class) : parmD.getValue(
			"IPD_NO", i));
	insertPesD
	
			.setData("CAT1_TYPE",
					parmD.getValue("CAT1_TYPE", i) == null ? new TNull(
							String.class) : parmD.getValue("CAT1_TYPE", i));
	insertPesD.setData("MEDI_QTY",
			parmD.getValue("MEDI_QTY", i) == null ? new TNull(Double.class)
					: parmD.getValue("MEDI_QTY", i));
	insertPesD
			.setData("MEDI_UNIT",
					parmD.getValue("MEDI_UNIT", i) == null ? new TNull(
							String.class) : parmD.getValue("MEDI_UNIT", i));
	insertPesD
			.setData("FREQ_CODE",
					parmD.getValue("FREQ_CODE", i) == null ? new TNull(
							String.class) : parmD.getValue("FREQ_CODE", i));
	insertPesD.setData("ROUTE_CODE",
			parmD.getValue("ROUTE_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("ROUTE_CODE", i));
	insertPesD.setData("TAKE_DAYS",
			parmD.getValue("TAKE_DAYS", i) == null ? new TNull(
					Integer.class) : parmD.getValue("TAKE_DAYS", i));
	insertPesD.setData("DOSAGE_QTY",
			parmD.getValue("DOSAGE_QTY", i) == null ? new TNull(
					Double.class) : parmD.getValue("DOSAGE_QTY", i));
	insertPesD.setData("DOSAGE_UNIT",
			parmD.getValue("DOSAGE_UNIT", i) == null ? new TNull(
					String.class) : parmD.getValue("DOSAGE_UNIT", i));
	insertPesD.setData("DISPENSE_QTY",
			parmD.getValue("DISPENSE_QTY", i) == null ? new TNull(
					Double.class) : parmD.getValue("DISPENSE_QTY", i));
	insertPesD.setData("DISPENSE_UNIT",
			parmD.getValue("DISPENSE_UNIT", i) == null ? new TNull(
					String.class) : parmD.getValue("DISPENSE_UNIT", i));
	insertPesD.setData("GIVEBOX_FLG",
			parmD.getValue("GIVEBOX_FLG", i) == null ? new TNull(
					String.class) : parmD.getValue("GIVEBOX_FLG", i));
	insertPesD.setData("CONTINUOUS_FLG",
			parmD.getValue("CONTINUOUS_FLG", i) == null ? new TNull(
					String.class) : parmD.getValue("CONTINUOUS_FLG", i));
	insertPesD
			.setData("ACUMDSPN_QTY",
					parmD.getValue("ACUMDSPN_QTY", i) == null ? new TNull(
							Double.class) : parmD.getValue("ACUMDSPN_QTY", i));
	insertPesD
			.setData("LASTDSPN_QTY",
					parmD.getValue("LASTDSPN_QTY", i) == null ? new TNull(
							Double.class) : parmD.getValue("LASTDSPN_QTY", i));
	insertPesD.setData("ORDER_STATE",
			parmD.getValue("ORDER_STATE", i) == null ? new TNull(
					String.class) : parmD.getValue("ORDER_STATE", i));
	
	insertPesD.setData("LINKMAIN_FLG",
			parmD.getValue("LINKMAIN_FLG", i) == null ? new TNull(String.class)
					: parmD.getValue("LINKMAIN_FLG", i));
	insertPesD.setData("LINK_NO",
			parmD.getValue("LINK_NO", i) == null ? new TNull(Double.class)
					: parmD.getValue("LINK_NO", i));
	insertPesD.setData("ORDER_DEPT_CODE",
			parmD.getValue("ORDER_DEPT_CODE", i) == null ? new TNull(String.class)
					: parmD.getValue("ORDER_DEPT_CODE", i));
	insertPesD.setData("ORDER_DR_CODE",
			parmD.getValue("ORDER_DR_CODE", i) == null ? new TNull(String.class)
					: parmD.getValue("ORDER_DR_CODE", i));
	
	insertPesD.setData("EFF_DATE",
			parmD.getTimestamp("EFF_DATE", i) == null ? new TNull(Timestamp.class)
					: parmD.getTimestamp("EFF_DATE", i));
	insertPesD.setData("ORDER_DATE",
			parmD.getTimestamp("ORDER_DATE", i) == null ? new TNull(
					Timestamp.class) : parmD.getTimestamp("ORDER_DATE", i));
	insertPesD
			.setData("DEPT_CODE",
					parmD.getValue("DEPT_CODE", i) == null ? new TNull(
							String.class) : parmD.getValue("DEPT_CODE", i));
	insertPesD.setData("EXEC_DEPT_CODE",
			parmD.getValue("EXEC_DEPT_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("EXEC_DEPT_CODE", i));
	insertPesD.setData("EXEC_DR_CODE",
			parmD.getValue("EXEC_DR_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("EXEC_DR_CODE", i));
	insertPesD.setData("DC_DEPT_CODE",
			parmD.getValue("DC_DEPT_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("DC_DEPT_CODE", i));
	insertPesD.setData("DC_DR_CODE",
			parmD.getValue("DC_DR_CODE", i) == null ? new TNull(String.class)
					: parmD.getValue("DC_DR_CODE", i));
	insertPesD
			.setData("DC_DATE",
					parmD.getTimestamp("DC_DATE", i) == null ? new TNull(
							Timestamp.class) : parmD.getTimestamp("DC_DATE", i));
	insertPesD.setData("DC_RSN_CODE",
			parmD.getValue("DC_RSN_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("DC_RSN_CODE", i));
	insertPesD.setData("DR_NOTE", parmD.getValue(
			"DR_NOTE", i) == null ? new TNull(String.class)
			: parmD.getValue("DR_NOTE", i));
	insertPesD.setData("NS_NOTE", parmD.getValue(
			"NS_NOTE", i) == null ? new TNull(String.class)
			: parmD.getValue("NS_NOTE", i));
	insertPesD
			.setData("INSPAY_TYPE",
					parmD.getValue("INSPAY_TYPE", i) == null ? new TNull(
							String.class) : parmD.getValue("INSPAY_TYPE", i));
	insertPesD.setData("CTRLDRUGCLASS_CODE",
			parmD.getValue("CTRLDRUGCLASS_CODE", i) == null ? new TNull(
					Integer.class) : parmD.getValue("CTRLDRUGCLASS_CODE", i));
	insertPesD.setData("RX_NO",
			parmD.getValue("RX_NO", i) == null ? new TNull(
					String.class) : parmD.getValue("RX_NO", i));
	insertPesD
			.setData("CTZ1_CODE",
					parmD.getValue("CTZ1_CODE", i) == null ? new TNull(
							String.class) : parmD.getValue("CTZ1_CODE", i));
	insertPesD.setData("PHA_CHECK_CODE",
			parmD.getValue("PHA_CHECK_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("PHA_CHECK_CODE", i));
	insertPesD.setData("PHA_TYPE", parmD.getValue(
			"PHA_TYPE", i) == null ? new TNull(String.class)
			: parmD.getValue("PHA_TYPE", i));
	insertPesD.setData("DOSE_TYPE", parmD.getValue("DOSE_TYPE",
			i) == null ? new TNull(String.class) : parmD.getValue(
			"DOSE_TYPE", i));
	insertPesD.setData("DCT_TAKE_QTY", parmD.getValue(
			"DCT_TAKE_QTY", i) == null ? new TNull(Double.class)
			: parmD.getValue("DCT_TAKE_QTY", i));
	insertPesD.setData("DCTAGENT_CODE", parmD.getValue(
			"DCTAGENT_CODE", i) == null ? new TNull(String.class)
			: parmD.getValue("DCTAGENT_CODE", i));
	insertPesD.setData("PACKAGE_AMT", parmD.getValue(
			"PACKAGE_AMT", i) == null ? new TNull(Double.class)
			: parmD.getValue("PACKAGE_AMT", i));
	insertPesD.setData("SETMAIN_FLG",
			parmD.getValue("SETMAIN_FLG", i) == null ? new TNull(
					String.class) : parmD.getValue("SETMAIN_FLG", i));
	insertPesD.setData("NS_CHECK_CODE", parmD.getValue("NS_CHECK_CODE",
			i) == null ? new TNull(String.class) : parmD.getValue(
			"NS_CHECK_CODE", i));
	insertPesD.setData("INDV_FLG",
			parmD.getValue("INDV_FLG", i) == null ? new TNull(
					String.class) : parmD.getValue("INDV_FLG", i));
	insertPesD.setData("HIDE_FLG",
			parmD.getValue("HIDE_FLG", i) == null ? new TNull(
					String.class) : parmD.getValue("HIDE_FLG", i));
	insertPesD.setData("ORDERSET_GROUP_NO",
			parmD.getValue("ORDERSET_GROUP_NO", i) == null ? new TNull(
					String.class) : parmD.getValue("ORDERSET_GROUP_NO", i));
	
	
	insertPesD.setData("ORDERSET_CODE",
			parmD.getValue("ORDERSET_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("ORDERSET_CODE", i));
	insertPesD.setData("ORDER_CAT1_CODE",
			parmD.getValue("ORDER_CAT1_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("ORDER_CAT1_CODE", i));
	insertPesD.setData("RPTTYPE_CODE",
			parmD.getValue("RPTTYPE_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("RPTTYPE_CODE", i));
	insertPesD.setData("OPTITEM_CODE",
			parmD.getValue("OPTITEM_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("OPTITEM_CODE", i));
	insertPesD.setData("MR_CODE",
			parmD.getValue("MR_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("MR_CODE", i));
	insertPesD.setData("FILE_NO",
			parmD.getValue("FILE_NO", i) == null ? new TNull(
					String.class) : parmD.getValue("FILE_NO", i));
	insertPesD.setData("DEGREE_CODE",
			parmD.getValue("DEGREE_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("DEGREE_CODE", i));
	insertPesD.setData("NS_CHECK_DATE",
			parmD.getTimestamp("NS_CHECK_DATE", i) == null ? new TNull(
					Timestamp.class) : parmD.getTimestamp("NS_CHECK_DATE", i));
	insertPesD.setData("DC_NS_CHECK_CODE",
			parmD.getValue("DC_NS_CHECK_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("DC_NS_CHECK_CODE", i));
	insertPesD.setData("DC_NS_CHECK_DATE",
			parmD.getTimestamp("DC_NS_CHECK_DATE", i) == null ? new TNull(
					Timestamp.class) : parmD.getTimestamp("DC_NS_CHECK_DATE", i));
	/*insertPesD.setData("START_DTTM",
			parmD.getTimestamp("START_DTTM", i) == null ? new TNull(
					Timestamp.class) : parmD.getTimestamp("START_DTTM", i));*/
	
	
	insertPesD.setData("LAST_DSPN_DATE",
			parmD.getTimestamp("LAST_DSPN_DATE", i) == null ? new TNull(
					Timestamp.class) : parmD.getTimestamp("LAST_DSPN_DATE", i));
	insertPesD.setData("FRST_QTY",
			parmD.getValue("FRST_QTY", i) == null ? new TNull(
					Double.class) : parmD.getValue("FRST_QTY", i));
	insertPesD.setData("PHA_CHECK_CODE",
			parmD.getValue("PHA_CHECK_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("PHA_CHECK_CODE", i));
	insertPesD.setData("PHA_CHECK_DATE",
			parmD.getTimestamp("PHA_CHECK_DATE", i) == null ? new TNull(
					Timestamp.class) : parmD.getTimestamp("PHA_CHECK_DATE", i));
	insertPesD.setData("INJ_ORG_CODE",
			parmD.getValue("INJ_ORG_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("INJ_ORG_CODE", i));
	insertPesD.setData("URGENT_FLG",
			parmD.getValue("URGENT_FLG", i) == null ? new TNull(
					String.class) : parmD.getValue("URGENT_FLG", i));
	insertPesD.setData("DCTEXCEP_CODE",
			parmD.getValue("DCTEXCEP_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("DCTEXCEP_CODE", i));
	insertPesD.setData("ACUMMEDI_QTY",
			parmD.getValue("ACUMMEDI_QTY", i) == null ? new TNull(
					Double.class) : parmD.getValue("ACUMMEDI_QTY", i));
	insertPesD.setData("REQUEST_NO",
			parmD.getValue("REQUEST_NO", i) == null ? new TNull(
					String.class) : parmD.getValue("REQUEST_NO", i));
	
	
	insertPesD.setData("DEV_CODE",
			parmD.getValue("DEV_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("DEV_CODE", i));
	insertPesD.setData("DISPENSE_FLG",
			parmD.getValue("DISPENSE_FLG", i) == null ? new TNull(
					String.class) : parmD.getValue("DISPENSE_FLG", i));
	insertPesD.setData("ANTIBIOTIC_CODE",
			parmD.getValue("ANTIBIOTIC_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("ANTIBIOTIC_CODE", i));
	insertPesD.setData("MED_APPLY_NO",
			parmD.getValue("MED_APPLY_NO", i) == null ? new TNull(
					String.class) : parmD.getValue("MED_APPLY_NO", i));
	insertPesD.setData("RELEASE_FLG",
			parmD.getValue("RELEASE_FLG", i) == null ? new TNull(
					String.class) : parmD.getValue("RELEASE_FLG", i));
	insertPesD.setData("DECOCT_CODE",
			parmD.getValue("DECOCT_CODE", i) == null ? new TNull(
					String.class) : parmD.getValue("DECOCT_CODE", i));
	
	
	insertPesD.setData("ORDER_ENG_DESC",
			parmD.getValue("ORDER_ENG_DESC", i) == null ? new TNull(
					String.class) : parmD.getValue("ORDER_ENG_DESC", i));
	insertPesD.setData("EXEC_FLG",
			parmD.getValue("EXEC_FLG", i) == null ? new TNull(
					String.class) : parmD.getValue("EXEC_FLG", i));
	insertPesD.setData("FINAL_TYPE",
			parmD.getValue("FINAL_TYPE", i) == null ? new TNull(
					String.class) : parmD.getValue("FINAL_TYPE", i));
	insertPesD.setData("EXM_EXEC_END_DATE",
			parmD.getTimestamp("EXM_EXEC_END_DATE", i) == null ? new TNull(
					Timestamp.class) : parmD.getTimestamp("EXM_EXEC_END_DATE", i));
	insertPesD.setData("EXEC_DR_DESC",
			parmD.getValue("EXEC_DR_DESC", i) == null ? new TNull(
					String.class) : parmD.getValue("EXEC_DR_DESC", i));
	insertPesD.setData("ANTIBIOTIC_WAY",
			parmD.getValue("ANTIBIOTIC_WAY", i) == null ? new TNull(
					Double.class) : parmD.getValue("ANTIBIOTIC_WAY", i));
	
	
	insertPesD.setData("TAKEMED_ORG",
			parmD.getValue("TAKEMED_ORG", i) == null ? new TNull(
					String.class) : parmD.getValue("TAKEMED_ORG", i));
	
	    return insertPesD;
	}

	/**
	 * 更新PESOPDM和PESOPDD
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatePESOPDMD(TParm parm) {
		TConnection connection = getConnection();
		TParm parmM = parm.getParm("OPDM");
		TParm parmD = parm.getParm("OPDD");
		TParm updatePESOPDMparm = new TParm();
		updatePESOPDMparm.setData("TYPE_CODE", parmM.getValue("TYPE_CODE"));
		updatePESOPDMparm.setData("PES_NO", parmM.getValue("PES_NO"));
		updatePESOPDMparm.setData("CASE_NO", parmM.getValue("CASE_NO"));
		updatePESOPDMparm.setData("PES_RX_NO", parmM.getValue("PES_RX_NO"));
		updatePESOPDMparm.setData("SEQ", parmM.getValue("SEQ"));
		updatePESOPDMparm.setData("QUESTION_CODE", parmM
				.getValue("QUESTION_CODE") == null ? new TNull(String.class)
				: parmM.getValue("QUESTION_CODE"));
		updatePESOPDMparm.setData("REASON_FLG",
				parmM.getValue("REASON_FLG") == null ? new TNull(String.class)
						: parmM.getValue("REASON_FLG"));
		updatePESOPDMparm.setData("REMARK",
				parmM.getValue("REMARK") == null ? new TNull(String.class)
						: parmM.getValue("REMARK"));
		updatePESOPDMparm.setData("OPT_USER",
				parm.getValue("OPT_USER") == null ? new TNull(String.class)
						: parm.getValue("OPT_USER"));
		updatePESOPDMparm.setData("OPT_TERM",
				parm.getValue("OPT_TERM") == null ? new TNull(String.class)
						: parm.getValue("OPT_TERM"));
		updatePESOPDMparm.setData("OPT_DATE", StringTool
				.getTimestamp(new Date()));
		TParm result = PESTool.getInstance().updatePESOPDM(updatePESOPDMparm,
				connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		for (int i = 0; i < parmD.getCount("SEQ_NO"); i++) {
			TParm updatePESOPDDparm = new TParm();
			updatePESOPDDparm.setData("PES_NO", parmM.getValue("PES_NO"));
			updatePESOPDDparm.setData("SEQ_NO", parmD.getValue("SEQ_NO", i));
			updatePESOPDDparm.setData("CASE_NO", parmD.getValue("CASE_NO", i));
			updatePESOPDDparm.setData("PES_RX_NO", parmD.getValue("PES_RX_NO",
					i));
			updatePESOPDDparm.setData("RX_NO", parmD.getValue("RX_NO", i));
			updatePESOPDDparm.setData("QUESTION_CODE", parmD.getValue(
					"QUESTION_CODE", i) == null ? new TNull(String.class)
					: parmD.getValue("QUESTION_CODE", i));
			updatePESOPDDparm.setData("REASON_FLG", parmD.getValue(
					"REASON_FLG", i) == null ? new TNull(String.class) : parmD
					.getValue("REASON_FLG", i));
			updatePESOPDDparm.setData("REMARK",
					parmD.getValue("REMARK", i) == null ? new TNull(
							String.class) : parmD.getValue("REMARK", i));
			updatePESOPDDparm.setData("OPT_USER",
					parm.getValue("OPT_USER") == null ? new TNull(String.class)
							: parm.getValue("OPT_USER"));
			updatePESOPDDparm.setData("OPT_TERM",
					parm.getValue("OPT_TERM") == null ? new TNull(String.class)
							: parm.getValue("OPT_TERM"));
			updatePESOPDDparm.setData("OPT_DATE", StringTool
					.getTimestamp(new Date()));
			result = PESTool.getInstance().updatePESOPDD(updatePESOPDDparm,
					connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 插入pesResult
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updatePESResult(TParm parm) {
		TParm updatePESResult = new TParm();
		updatePESResult.setData("TYPE_CODE", parm.getValue("TYPE_CODE"));
		updatePESResult.setData("PES_NO", parm.getValue("PES_NO"));
		// updatePESResult.setData("EVAL_CODE",
		// parm.getValue("EVAL_CODE") == null ? new TNull(String.class)
		// : parm.getValue("EVAL_CODE"));
		updatePESResult.setData("OPT_USER",
				parm.getValue("OPT_USER") == null ? new TNull(String.class)
						: parm.getValue("OPT_USER"));
		updatePESResult
				.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
		updatePESResult.setData("OPT_TERM",
				parm.getValue("OPT_TERM") == null ? new TNull(String.class)
						: parm.getValue("OPT_TERM"));
		updatePESResult
				.setData("PES_DATE", StringTool.getTimestamp(new Date()));
		updatePESResult.setData("PES_A",
				parm.getValue("PES_A") == null ? new TNull(String.class) : parm
						.getValue("PES_A"));
		updatePESResult.setData("PES_B",
				parm.getValue("PES_B") == null ? new TNull(String.class) : parm
						.getValue("PES_B"));
		updatePESResult.setData("PES_C",
				parm.getValue("PES_C") == null ? new TNull(String.class) : parm
						.getValue("PES_C"));
		updatePESResult.setData("PES_D",
				parm.getValue("PES_D") == null ? new TNull(String.class) : parm
						.getValue("PES_D"));
		updatePESResult.setData("PES_E",
				parm.getValue("PES_E") == null ? new TNull(String.class) : parm
						.getValue("PES_E"));
		updatePESResult.setData("PES_F",
				parm.getValue("PES_F") == null ? new TNull(String.class) : parm
						.getValue("PES_F"));
		updatePESResult.setData("PES_G",
				parm.getValue("PES_G") == null ? new TNull(String.class) : parm
						.getValue("PES_G"));
		updatePESResult.setData("PES_H",
				parm.getValue("PES_H") == null ? new TNull(String.class) : parm
						.getValue("PES_H"));
		updatePESResult.setData("PES_I",
				parm.getValue("PES_I") == null ? new TNull(String.class) : parm
						.getValue("PES_I"));
		updatePESResult.setData("PES_J",
				parm.getValue("PES_J") == null ? new TNull(String.class) : parm
						.getValue("PES_J"));
		updatePESResult.setData("PES_K",
				parm.getValue("PES_K") == null ? new TNull(String.class) : parm
						.getValue("PES_K"));
		updatePESResult.setData("PES_L",
				parm.getValue("PES_L") == null ? new TNull(String.class) : parm
						.getValue("PES_L"));
		updatePESResult.setData("PES_O",
				parm.getValue("PES_O") == null ? new TNull(String.class) : parm
						.getValue("PES_O"));
		updatePESResult.setData("PES_P",
				parm.getValue("PES_P") == null ? new TNull(String.class) : parm
						.getValue("PES_P"));
		TParm result = PESTool.getInstance().updatePESResult(updatePESResult);
		return result;
	}
	
	
	/**
	 * 住院插入pes_ipdopdm
	 * ADD CAOYONG 20140218
	 * @param parmD
	 * @param parm
	 * @param connection
	 * @return
	 */
	private TParm insirtIpdPesMD(TParm parmD, TParm parm, TConnection connection) {
		//int order_qty = 0;// 处方药品数量
		int antibiotic_qty = 0;// 抗菌药品数量
		int inject_qty = 0;// 注射药品数量
		int goods_qty = 0;// 药品通用名数量
		int base_qty = 0;// 药品通用名数量
		double rx_total = 0;// 处方金额
		int seq=0;
		int count = 0;
		seq=this.getMaxPMSeq(parm,"PES_IPDOPDM");
		
		
//		if (parmD.getCount("CASE_NO") < 5) {
			count = parmD.getCount("CASE_NO");
//		}
		for (int i = 0; i < count; i++) {
			//order_qty++;
			if (!parmD.getValue("ANTIBIOTIC_CODE", i).equals("")) {
				antibiotic_qty++;
			}
			if (!parmD.getValue("GOODS_DESC", i).equals("")) {
				goods_qty++;
			}
			if (parmD.getValue("SYS_GRUG_CLASS", i).equals("1")) {
				base_qty++;
			}
			if (parmD.getValue("DOSE_TYPE", i).equals("I") || parmD.getValue("DOSE_TYPE", i).equals("F")) {
				inject_qty++;
			}
			rx_total += parmD.getDouble("TOT_AMT", i);
		}
		TParm insertPesM = new TParm();
		
		String indate=parm.getValue("IN_DATE").replace("/", "");
        Timestamp inDate = StringTool.getTimestamp(indate,"yyyyMMdd");

		insertPesM.setData("TYPE_CODE",
				parm.getValue("TYPE_CODE") == null ? new TNull(String.class)
						: parm.getValue("TYPE_CODE"));
		insertPesM.setData("PES_NO",
				parm.getValue("PES_NO") == null ? new TNull(String.class)
						: parm.getValue("PES_NO"));
		insertPesM.setData("CASE_NO",
				parm.getValue("CASE_NO") == null ? new TNull(String.class)
						: parm.getValue("CASE_NO"));
		
		insertPesM.setData("IPD_NO",
				parm.getValue("IPD_NO") == null ? new TNull(String.class)
		: parm.getValue("IPD_NO"));
		
		
		insertPesM.setData("SEQ", seq+1);
		
		insertPesM.setData("EVAL_CODE",
				parm.getValue("EVAL_CODE") == null ? new TNull(String.class)
						: parm.getValue("EVAL_CODE"));
		insertPesM.setData("MR_NO", parm.getValue("MR_NO") == null ? new TNull(
				String.class) : parm.getValue("MR_NO"));
		insertPesM.setData("PAT_NAME",
				parm.getValue("PAT_NAME") == null ? new TNull(String.class)
						: parm.getValue("PAT_NAME"));
		insertPesM.setData("ORDER_DATE",
				parmD.getTimestamp("ORDER_DATE", 0) == null ? new TNull(
						Timestamp.class) : parmD.getTimestamp("ORDER_DATE", 0));
		insertPesM.setData("IN_DATE",inDate== null ? new TNull(
				Timestamp.class) : inDate);
		
		insertPesM.setData("AGE", parm.getValue("AGE") == null ? new TNull(
				String.class) : parm.getValue("AGE"));
		insertPesM.setData("DIAG","");// 诊断
	    insertPesM.setData("ANTIBIOTIC_QTY", antibiotic_qty);
		insertPesM.setData("BASE_QTY", base_qty);
		insertPesM.setData("GOODS_QTY", goods_qty);
		insertPesM.setData("INJECT_QTY", inject_qty);
		insertPesM.setData("RX_TOTAL", StringTool.round(rx_total, 2));
		insertPesM.setData("VS_DR_CODE",
				parm.getValue("VS_DR_CODE") == null ? new TNull(String.class)
						: parm.getValue("VS_DR_CODE"));
		
		insertPesM.setData("OPT_USER", parm.getValue("OPT_USER"));
		insertPesM.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
		insertPesM.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		
		TParm result = PESTool.getInstance().insertIPDPESOPDM(insertPesM,
				connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		
		     //return result;
		     
		TParm insertPesD;
		//int seqQ=0;
		
		for (int i = 0; i < count; i++) {
			//seqQ=this.getMaxOpddSeq(parmD, i,parm,"PES_IPDOPDD");
			insertPesD = getInsertPesIpdD(parmD, i);
			insertPesD.setData("OPT_USER", parm.getValue("OPT_USER"));
			insertPesD.setData("PES_NO", parm.getValue("PES_NO"));
			//insertPesD.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
			insertPesD.setData("OPT_TERM", parm.getValue("OPT_TERM"));
			insertPesD.setData("SEQ",getSeq());
			
			result = PESTool.getInstance()
					.insertPESIPDOPDD(insertPesD, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		     return result;
	}
	/**
	 * 查询seq最大号
	 * ADD CAOYONG 20140217
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	
	public int getMaxPMSeq(TParm parm,String table){
		TParm result=PESTool.getInstance().selectMaxsql(parm,table);
		return  result.getInt("SEQ",0);
		
	}
	
	
	
	
    /**
     * add caoyong 取SEQ
     */
    public String getSeq(){
    	int i=(int)(Math.random()*900)+100;
    	SimpleDateFormat formt= new SimpleDateFormat("yyyyMMddHHmmss");
    	String time=formt.format(new Date());
    	
    	String str=time+String.valueOf(i);
    	
    	return str;

    }
    
    
    /**
	 * 更新PESOPDM和PESOPDD
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatePESIPDOPDMD(TParm parm) {
		TConnection connection = getConnection();
		TParm parmM = parm.getParm("OPDM");
		TParm parmD = parm.getParm("OPDD");
		TParm updatePESOPDMparm = new TParm();
		updatePESOPDMparm.setData("TYPE_CODE", parmM.getValue("TYPE_CODE"));
		updatePESOPDMparm.setData("PES_NO", parmM.getValue("PES_NO"));
		updatePESOPDMparm.setData("CASE_NO", parmM.getValue("CASE_NO"));
		updatePESOPDMparm.setData("SEQ", parmM.getValue("SEQ"));
		updatePESOPDMparm.setData("QUESTION_CODE", parmM
				.getValue("QUESTION_CODE") == null ? new TNull(String.class)
				: parmM.getValue("QUESTION_CODE"));
		updatePESOPDMparm.setData("REASON_FLG",
				parmM.getValue("REASON_FLG") == null ? new TNull(String.class)
						: parmM.getValue("REASON_FLG"));
		updatePESOPDMparm.setData("REMARK",
				parmM.getValue("REMARK") == null ? new TNull(String.class)
						: parmM.getValue("REMARK"));
		updatePESOPDMparm.setData("OPT_USER",
				parm.getValue("OPT_USER") == null ? new TNull(String.class)
						: parm.getValue("OPT_USER"));
		updatePESOPDMparm.setData("OPT_TERM",
				parm.getValue("OPT_TERM") == null ? new TNull(String.class)
						: parm.getValue("OPT_TERM"));
		updatePESOPDMparm.setData("OPT_DATE", StringTool
				.getTimestamp(new Date()));
		TParm result = PESTool.getInstance().updatePESIPDOPDM(updatePESOPDMparm,
				connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		
		//System.out.println("parmD;;;;;;;;;;"+parmD);
		for (int i = 0; i < parmD.getCount("SEQ_NO"); i++) {
			TParm updatePESOPDDparm = new TParm();
			updatePESOPDDparm.setData("PES_NO", parmD.getValue("PES_NO",i));
			updatePESOPDDparm.setData("ORDER_NO", parmD.getValue("ORDER_NO", i));
			updatePESOPDDparm.setData("CASE_NO", parmD.getValue("CASE_NO", i));
			updatePESOPDDparm.setData("ORDER_SEQ", parmD.getValue("ORDER_SEQ",i));
			updatePESOPDDparm.setData("SEQ", parmD.getValue("SEQ", i));
			updatePESOPDDparm.setData("QUESTION_CODE", parmD.getValue(
					"QUESTION_CODE", i) == null ? new TNull(String.class)
					: parmD.getValue("QUESTION_CODE", i));
			updatePESOPDDparm.setData("REASON_FLG", parmD.getValue(
					"REASON_FLG", i) == null ? new TNull(String.class) : parmD
					.getValue("REASON_FLG", i));
			updatePESOPDDparm.setData("REMARK",
					parmD.getValue("REMARK", i) == null ? new TNull(
							String.class) : parmD.getValue("REMARK", i));
			
			updatePESOPDDparm.setData("OPT_USER",
					parm.getValue("OPT_USER") == null ? new TNull(String.class)
							: parm.getValue("OPT_USER"));
			updatePESOPDDparm.setData("OPT_TERM",
					parm.getValue("OPT_TERM") == null ? new TNull(String.class)
							: parm.getValue("OPT_TERM"));
			updatePESOPDDparm.setData("OPT_DATE", StringTool
					.getTimestamp(new Date()));
			result = PESTool.getInstance().updatePESIDPOPDD(updatePESOPDDparm,
					connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}
	
	
	/**
	 * 插入PES_IPDResult
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updatePESIPDResult(TParm parm) {
		TParm updatePESResult = new TParm();
		updatePESResult.setData("TYPE_CODE", parm.getValue("TYPE_CODE"));
		updatePESResult.setData("PES_NO", parm.getValue("PES_NO"));
		// updatePESResult.setData("EVAL_CODE",
		// parm.getValue("EVAL_CODE") == null ? new TNull(String.class)
		// : parm.getValue("EVAL_CODE"));
		updatePESResult.setData("OPT_USER",
				parm.getValue("OPT_USER") == null ? new TNull(String.class)
						: parm.getValue("OPT_USER"));
		updatePESResult
				.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
		updatePESResult.setData("OPT_TERM",
				parm.getValue("OPT_TERM") == null ? new TNull(String.class)
						: parm.getValue("OPT_TERM"));
		updatePESResult
				.setData("PES_DATE", StringTool.getTimestamp(new Date()));
		/*updatePESResult.setData("PES_A",
				parm.getValue("PES_A") == null ? new TNull(String.class) : parm
						.getValue("PES_A"));
		updatePESResult.setData("PES_B",
				parm.getValue("PES_B") == null ? new TNull(String.class) : parm
						.getValue("PES_B"));*/
		updatePESResult.setData("PES_C",
				parm.getValue("PES_C") == null ? new TNull(String.class) : parm
						.getValue("PES_C"));
		updatePESResult.setData("PES_D",
				parm.getValue("PES_D") == null ? new TNull(String.class) : parm
						.getValue("PES_D"));
		updatePESResult.setData("PES_E",
				parm.getValue("PES_E") == null ? new TNull(String.class) : parm
						.getValue("PES_E"));
		updatePESResult.setData("PES_F",
				parm.getValue("PES_F") == null ? new TNull(String.class) : parm
						.getValue("PES_F"));
		updatePESResult.setData("PES_G",
				parm.getValue("PES_G") == null ? new TNull(String.class) : parm
						.getValue("PES_G"));
		/*updatePESResult.setData("PES_H",
				parm.getValue("PES_H") == null ? new TNull(String.class) : parm
						.getValue("PES_H"));*/
		updatePESResult.setData("PES_I",
				parm.getValue("PES_I") == null ? new TNull(String.class) : parm
						.getValue("PES_I"));
		/*updatePESResult.setData("PES_J",
				parm.getValue("PES_J") == null ? new TNull(String.class) : parm
						.getValue("PES_J"));*/
		updatePESResult.setData("PES_K",
				parm.getValue("PES_K") == null ? new TNull(String.class) : parm
						.getValue("PES_K"));
		updatePESResult.setData("PES_L",
				parm.getValue("PES_L") == null ? new TNull(String.class) : parm
						.getValue("PES_L"));
		updatePESResult.setData("PES_O",
				parm.getValue("PES_O") == null ? new TNull(String.class) : parm
						.getValue("PES_O"));
		updatePESResult.setData("PES_P",
				parm.getValue("PES_P") == null ? new TNull(String.class) : parm
						.getValue("PES_P"));
		TParm result = PESTool.getInstance().updatePESIPDResult(updatePESResult);
		return result;
	}
}
