package action.clp;

import com.dongyang.action.TAction;
import jdo.clp.CLPManagedTool;
import jdo.clp.CLPManagemTool;

import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.db.TConnection;
import com.dongyang.data.TNull;
import com.dongyang.jdo.TJDODBTool;

import jdo.opd.TotQtyTool;
import java.util.Map;

/**
 * <p>
 * Title: 临床路径展开
 * </p>
 * 
 * <p>
 * Description: 临床路径展开
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author 卢海
 * @version 1.0
 */
public class CLPManagedAction extends TAction {
	public CLPManagedAction() {
	}

	/**
	 * 还原实际
	 * 
	 * @param patientTParm
	 *            TParm
	 * @return TParm
	 */
	public TParm returnPractice(TParm basicParm) {
		Map operatorMap = (Map) basicParm.getData("operator");
		TParm patientTParm = new TParm((Map) basicParm.getData("patientTParm"));
		TConnection conn = getConnection();
		TParm result = new TParm();
		// =============pangben 2012-05-24 start
		TParm bilRecpParm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT ADM_TYPE, RECP_TYPE,CHARGE01, "
										+ "CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07, CHARGE08, CHARGE09, CHARGE10, "
										+ "CHARGE11, CHARGE12, CHARGE13, CHARGE14, CHARGE15, CHARGE16, CHARGE17, CHARGE18, CHARGE19,"
										+ " CHARGE20, CHARGE21, CHARGE22, CHARGE23, CHARGE24, CHARGE25, CHARGE26, CHARGE27, CHARGE28, "
										+ "CHARGE29, CHARGE30 FROM BIL_RECPPARM WHERE ADM_TYPE='I' AND RECP_TYPE='IBS'"));
		if (bilRecpParm.getErrCode() < 0) {
			return bilRecpParm;
		}
		// =============pangben 2012-05-24 stop
		for (int i = 0; i < patientTParm.getCount(); i++) {
			String regionCode = patientTParm.getValue("REGION_CODE", i);
			String case_no = patientTParm.getValue("CASE_NO", i);
			String clncpath_code = patientTParm.getValue("CLNCPATH_CODE", i);
			TParm parmToDel = new TParm();
			parmToDel.setData("CASE_NO", case_no);
			parmToDel.setData("CLNCPATH_CODE", clncpath_code);
			boolean isDelBillFlag = deleteCLPBill(regionCode, case_no,
					clncpath_code, conn, 3);
			if (!isDelBillFlag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
			}
			this.putBasicSysInfoIntoParm(parmToDel, operatorMap);
			result = CLPManagedTool.getInstance()
					.deleteCLPManagedWithPatientInfo(parmToDel, conn);
			if (result.getErrCode() < 0) {
				conn.close();
				return result;
			}
			CLPManagedTool.getInstance().updateCLPManagedWithPatientInfo(
					parmToDel, conn);
			if (result.getErrCode() < 0) {
				conn.close();
				return result;
			}
			// 展开账务资料
			conn.commit();
			boolean deployFlag = deployBill(bilRecpParm, regionCode, case_no,
					clncpath_code, conn, operatorMap);
			if (!deployFlag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
				return errorTparm;
			}
		}

		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * 展开标准
	 * 
	 * @param inputParm
	 *            TParm
	 * @return TParm
	 */
	public TParm deployStandard(TParm inputParm) {
		TParm patientTParm = new TParm((Map) inputParm.getData("patientTParm"));
		String deployDate = inputParm.getValue("deployDate");
		Map operatorMap = (Map) inputParm.getData("operator");
		TConnection conn = getConnection();
		TParm result = new TParm();
		// =============pangben 2012-05-24 start
		TParm bilRecpParm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT ADM_TYPE, RECP_TYPE,CHARGE01, "
										+ "CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07, CHARGE08, CHARGE09, CHARGE10, "
										+ "CHARGE11, CHARGE12, CHARGE13, CHARGE14, CHARGE15, CHARGE16, CHARGE17, CHARGE18, CHARGE19,"
										+ " CHARGE20, CHARGE21, CHARGE22, CHARGE23, CHARGE24, CHARGE25, CHARGE26, CHARGE27, CHARGE28, "
										+ "CHARGE29, CHARGE30 FROM BIL_RECPPARM WHERE ADM_TYPE='I' AND RECP_TYPE='IBS'"));
		if (bilRecpParm.getErrCode() < 0) {
			return bilRecpParm;
		}
		if (bilRecpParm.getCount() <= 0) {
			return bilRecpParm;
		}
		// =============pangben 2012-05-24 stop
		for (int i = 0; i < patientTParm.getCount(); i++) {
			String regionCode = patientTParm.getValue("REGION_CODE", i);
			String case_no = patientTParm.getValue("CASE_NO", i);
			String clncpath_code = patientTParm.getValue("CLNCPATH_CODE", i);
			String version = patientTParm.getValue("VERSION", i);// ========pangben
			// 2012-05-22
			// 版本号
			String deptCode = patientTParm.getValue("DEPT_CODE", i);// 科室代码
			boolean isdelflag = deleteManagedHistory(regionCode, case_no,
					clncpath_code, conn, true);
			if (!isdelflag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
			}
			boolean isDelBillFlag = deleteCLPBill(regionCode, case_no,
					clncpath_code, conn, 3);
			if (!isDelBillFlag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
			}
			conn.commit();
			// 将CLP_PACK 表中的数据插入
			boolean flag = insertManagedFromCLPPack(regionCode, case_no,
					clncpath_code, deployDate, version, deptCode, conn,
					operatorMap);
			if (!flag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
			}
			conn.commit();
			// 展开账务资料
			boolean deployFlag = deployBill(bilRecpParm, regionCode, case_no,
					clncpath_code, conn, operatorMap);
			if (!deployFlag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
			}
			conn.commit();
		}
		conn.close();
		return result;
	}

	/**
	 * 展开实际
	 * 
	 * @param patientTParm
	 *            TParm
	 * @return TParm
	 */
	public TParm deployPractice(TParm inputParm) {
		TParm patientTParm = inputParm.getParm("patientTParm");
		String deployDate = inputParm.getValue("deployDate");
		Map operatorMap = (Map) inputParm.getData("operator");
		TConnection conn = getConnection();
		TParm result = new TParm();
		// =============pangben 2012-05-24 start
		TParm bilRecpParm = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								"SELECT ADM_TYPE, RECP_TYPE,CHARGE01, "
										+ "CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07, CHARGE08, CHARGE09, CHARGE10, "
										+ "CHARGE11, CHARGE12, CHARGE13, CHARGE14, CHARGE15, CHARGE16, CHARGE17, CHARGE18, CHARGE19,"
										+ " CHARGE20, CHARGE21, CHARGE22, CHARGE23, CHARGE24, CHARGE25, CHARGE26, CHARGE27, CHARGE28, "
										+ "CHARGE29, CHARGE30 FROM BIL_RECPPARM WHERE ADM_TYPE='I' AND RECP_TYPE='IBS'"));
		if (bilRecpParm.getErrCode() < 0) {
			return bilRecpParm;
		}
		// =============pangben 2012-05-24 stop
		for (int i = 0; i < patientTParm.getCount(); i++) {
			String regionCode = patientTParm.getValue("REGION_CODE", i);
			String case_no = patientTParm.getValue("CASE_NO", i);
			String clncpath_code = patientTParm.getValue("CLNCPATH_CODE", i);
			// ===========pangben 2012-05-22 添加版本号优化sql
			String version = patientTParm.getValue("VERSION", i);// 版本号
			String deptCode = patientTParm.getValue("DEPT_CODE", i);// 科室代码
			// ===========pangben 2012-05-22 stop
			boolean isdelflag = deleteManagedHistory(regionCode, case_no,
					clncpath_code, conn);
			if (!isdelflag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
				return errorTparm;
			}
			boolean isDelBillFlag = deleteCLPBill(regionCode, case_no,
					clncpath_code, conn, 3);
			if (!isDelBillFlag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
				return errorTparm;
			}
			conn.commit();
			// 20110907 卢海删除 begin
			// conn = this.getConnection();
			// 20110907 卢海删除 end
			// 将CLP_PACK 表中的数据插入
			boolean flaginsert = insertManagedFromCLPPack(regionCode, case_no,
					clncpath_code, deployDate, version, deptCode, conn,
					operatorMap);
			if (!flaginsert) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
				return errorTparm;
			}
			conn.commit();
			// 2011-09-07 卢海删除 begin
			// conn = this.getConnection();
			// 2011-09-07 卢海删除 end
			// 将IBS_ORDD 表中的数据插入
			boolean flaginsertibs = insertManagedFromIBSORDD(regionCode,
					case_no, clncpath_code, deployDate, conn, operatorMap);
			if (!flaginsertibs) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
				return errorTparm;
			}
			conn.commit();
			// 展开账务资料
			boolean deployFlag = deployBill(bilRecpParm, regionCode, case_no,
					clncpath_code, conn, operatorMap);
			if (!deployFlag) {
				TParm errorTparm = new TParm();
				errorTparm.setErrCode(-1);
				conn.close();
				return errorTparm;
			}
			conn.commit();
		}
		conn.commit();
		conn.close();
		return result;
	}

	private boolean deployBill(TParm bilRecpParm, String regionCode,
			String case_no, String clncpath_code, TConnection conn,
			Map operatorMap) {
		boolean flag = true;

		TParm selectParm = new TParm();
		selectParm.setData("REGION_CODE", regionCode);
		selectParm.setData("CASE_NO", case_no);
		selectParm.setData("CLNCPATH_CODE", clncpath_code);
		TParm result = CLPManagedTool.getInstance()
				.selectCLPManagedWithCondition(selectParm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrText());
			return false;
		}
		// =============pangben 2012-6-1 start
		String[] billName = { "", "" };// 时程比较相同的累加金额使用
		double[] sum = { 0.00, 0.00 };// 时程累加的金额
		String[] sqlName = { "", "" };// sql 语句需要获得的费用项目
		String[] sqlTotName = { "", "" };// sql 语句需要获得的费用项目金额
		sqlName[0] = "";
		sqlTotName[0] = "";
		sum[0] = 0.00;
		sqlName[1] = "";
		sqlTotName[1] = "";
		sum[1] = 0.00;
		// =============pangben 2012-6-1 stop
		if (null != result && result.getValue("SCHD_CODE", 0).length() > 0) {
			billName[0] = result.getValue("SCHD_CODE", 0);
			billName[1] = result.getValue("SCHD_CODE", 0);
		}
		for (int i = 0; i < result.getCount(); i++) {
			TParm rowParm = result.getRow(i);
			flag = deployDuringBill(bilRecpParm, rowParm, conn, operatorMap,
					billName, sum, sqlName, sqlTotName, result, i);
			if (!flag) {
				return flag;
			}
		}

		return flag;
	}

	/**
	 * billName//时程比较相同的累加金额使用 sum//时程累加的金额 sqlName//sql 语句需要获得的费用项目
	 * sqlTotName//sql 语句需要获得的费用项目金额 将得到的时程信息添加或更新到CLP_BLL中 =========pangben
	 * 2012-5-24 添加参数 优化程序
	 */
	private boolean deployDuringBill(TParm bilRecpParm, TParm managedParm,
			TConnection conn, Map operatorMap, String[] billName, double[] sum,
			String[] sqlName, String[] sqlTotName, TParm sumParm, int index) {
		boolean flag = true;
		managedParm.setData("OPT_USER", operatorMap.get("OPT_USER"));
		managedParm.setData("OPT_DATE", operatorMap.get("OPT_DATE"));
		managedParm.setData("OPT_TERM", operatorMap.get("OPT_TERM"));
		managedParm.setData("REGION_CODE", operatorMap.get("REGION_CODE"));
		// 记录标准到clp_bill
		flag = recordCLPBill(bilRecpParm, managedParm, conn, "1", billName,
				sum, sqlName, sqlTotName, sumParm, index);
		if (!flag) {
			return false;
		}
		// 记录实际到clp_bill
		flag = recordCLPBill(bilRecpParm, managedParm, conn, "2", billName,
				sum, sqlName, sqlTotName, sumParm, index);
		if (!flag) {
			return false;
		}
		return flag;
	}

	/**
	 * 
	 * @param bilRecpParm
	 * @param managedParm
	 * @param conn
	 *            billName//时程比较相同的累加金额使用 sum//时程累加的金额 sqlName//sql 语句需要获得的费用项目
	 *            sqlTotName//sql 语句需要获得的费用项目金额
	 * @return =================pangben 2012-5-24 添加参数 优化程序
	 */
	private boolean recordCLPBill(TParm bilRecpParm, TParm managedParm,
			TConnection conn, String schdType, String[] billName, double[] sum,
			String[] sqlName, String[] sqlTotName, TParm sumParm, int index) {
		boolean flag = true;
		// 处理初始数据
		managedParm.setData("SCHD_TYPE", schdType);
		if ("1".equals(schdType)) {
			managedParm.setData("CHARGE", managedParm.getDouble("TOT_AMT"));
		} else {
			managedParm.setData("CHARGE", managedParm.getDouble("MAIN_AMT"));
		}
		// TParm stardardCLPBill = this.cloneTParm(managedParm);
		TParm existResult = CLPManagedTool.getInstance().checkCLPBillExist(
				managedParm);
		TParm result = null; // 执行结果parm
		// String columnName = getColumnNameWithCondition(stardardCLPBill
		// .getValue("ORDER_CODE"));
		String columnName = "";
		// ============pangben 2012-5-24 start 添加优化数据
		for (int i = 1; i <= 30; i++) {
			if (i < 10) {
				if (managedParm.getValue("IPD_CHARGE_CODE").equals(
						bilRecpParm.getValue("CHARGE0" + i, 0))) {
					columnName = "REXP_0" + i;
					break;
				}
			} else {
				if (managedParm.getValue("IPD_CHARGE_CODE").equals(
						bilRecpParm.getValue("CHARGE" + i, 0))) {
					columnName = "REXP_" + i;
					break;
				}
			}
		}
		String billNameTemp = "";
		if ("1".equals(schdType)) {
			billNameTemp = billName[0];
		} else {
			billNameTemp = billName[1];
		}
		// ========累计费用项目金额对比项目相同累加
		if (managedParm.getValue("SCHD_CODE").equals(billNameTemp)) {

			if (existResult.getDouble("TOTALCOUNT", 0) > 0) {
				// ============pangben 2012-6-26 start
				if (columnName.length() > 0) {
					if ("1".equals(schdType)) {

						// 存在相应的费用列不用累计数据
						if (!sqlName[0].contains(columnName)) {
							sqlName[0] += " , " + columnName + "="
									+ managedParm.getDouble("CHARGE");
							sum[0] += managedParm.getDouble("CHARGE");
						}
					} else {
						// 存在相应的费用列不用累计数据
						if (!sqlName[1].contains(columnName)) {
							sqlName[1] += " , " + columnName + "="
									+ managedParm.getDouble("CHARGE");
							sum[1] += managedParm.getDouble("CHARGE");
						}
					}
				}

			} else {
				if (columnName.length() > 0) {
					if ("1".equals(schdType)) {
						// 存在相应的费用列不用累计数据
						if (!sqlName[0].contains(columnName)) {
							sum[0] += managedParm.getDouble("CHARGE");
							sqlName[0] += columnName + ",";
							sqlTotName[0] += managedParm.getDouble("CHARGE")
									+ ",";
						}
					} else {
						// 存在相应的费用列不用累计数据
						if (!sqlName[1].contains(columnName)) {
							sum[1] += managedParm.getDouble("CHARGE");
							sqlName[1] += columnName + ",";
							sqlTotName[1] += managedParm.getDouble("CHARGE")
									+ ",";
						}
					}
				}
			}
			// ============pangben 2012-6-26 stop
		} else {
			// ============pangben 2012-5-24 stop

			String sqlNameTemp = "";
			if ("1".equals(schdType)) {
				sqlNameTemp = sqlName[0];
			} else {
				sqlNameTemp = sqlName[1];
			}
			if (this.checkNullAndEmpty(sqlNameTemp)) {
				if (existResult.getDouble("TOTALCOUNT", 0) > 0) {
					result = CLPManagedTool.getInstance().updateCLPBill(
							managedParm, conn, sum, sqlName, schdType,
							billNameTemp);
				} else {
					result = CLPManagedTool.getInstance().saveCLPBill(
							managedParm, conn, sum, sqlName, sqlTotName,
							schdType, billNameTemp);
				}
				if (result.getErrCode() < 0) {
					flag = false;
				}
			}
			// 标准、实际 操作添加CLP_BILL 通过不同的收费项目获得不同的金额
			if ("1".equals(schdType)) {
				billName[0] = managedParm.getValue("SCHD_CODE");
				sum[0] = managedParm.getDouble("CHARGE");
				if (existResult.getDouble("TOTALCOUNT", 0) > 0) {
					sqlName[0] = columnName + "="
							+ managedParm.getDouble("CHARGE");
				} else {
					sqlName[0] = columnName + ",";
					sqlTotName[0] = managedParm.getDouble("CHARGE") + ",";
				}
			} else {
				billName[1] = managedParm.getValue("SCHD_CODE");
				sum[1] = managedParm.getDouble("CHARGE");
				if (existResult.getDouble("TOTALCOUNT", 0) > 0) {
					sqlName[1] = columnName + "="
							+ managedParm.getDouble("CHARGE");
				} else {
					sqlName[1] = columnName + ",";
					sqlTotName[1] = managedParm.getDouble("CHARGE") + ",";
				}
			}

		}
		// 最后一个时程金额汇总
		if (sumParm.getCount() - 1 == index) {
			if (this.checkNullAndEmpty(managedParm.getValue("SCHD_CODE"))) {
				if (existResult.getDouble("TOTALCOUNT", 0) > 0) {
					result = CLPManagedTool.getInstance().updateCLPBill(
							managedParm, conn, sum, sqlName, schdType,
							managedParm.getValue("SCHD_CODE"));
				} else {
					result = CLPManagedTool.getInstance().saveCLPBill(
							managedParm, conn, sum, sqlName, sqlTotName,
							schdType, managedParm.getValue("SCHD_CODE"));
				}
				if (result.getErrCode() < 0) {
					flag = false;
				}
			}
		}
		return flag;
	}

	private String getColumnNameWithCondition(String orderCode) {
		String columnName = "";
		TParm selectParm = new TParm();
		selectParm.setData("ORDER_CODE", orderCode);
		TParm result = CLPManagedTool.getInstance().getCLPBillColumName(
				selectParm);
		if (result.getCount() > 0) {
			columnName = result.getValue("COLUMNNAME", 0);
		}
		return columnName;
	}

	/**
	 * 删除已展开账务资料
	 * 
	 * @param regionCode
	 *            String
	 * @param case_no
	 *            String
	 * @param clncpath_code
	 *            String
	 * @return type 1:标准 2：实际 3：全部
	 * @return boolean
	 * 
	 */
	private boolean deleteCLPBill(String regionCode, String case_no,
			String clncpath_code, TConnection conn, int type) {
		boolean flag = true;
		TParm parmDelete = new TParm();
		parmDelete.setData("REGION_CODE", regionCode);
		parmDelete.setData("CASE_NO", case_no);
		parmDelete.setData("CLNCPATH_CODE", clncpath_code);
		if (1 == type) {
			parmDelete.setData("SCHD_TYPE", 1);
		} else if (2 == type) {
			parmDelete.setData("SCHD_TYPE", 2);
		}
		TParm result = CLPManagedTool.getInstance().deleteCLPBill(parmDelete,
				conn);
		if (result.getErrCode() < 0) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 删除已经展开的历史资料
	 * 
	 * @param regionCode
	 *            String
	 * @param case_no
	 *            String
	 * @param mr_no
	 *            String
	 * @return boolean
	 */
	private boolean deleteManagedHistory(String regionCode, String case_no,
			String clncpath_code, TConnection conn) {
		boolean flag = true;
		TParm parmDelete = new TParm();
		parmDelete.setData("REGION_CODE", regionCode);
		parmDelete.setData("CASE_NO", case_no);
		parmDelete.setData("CLNCPATH_CODE", clncpath_code);
		TParm result = CLPManagedTool.getInstance().deleteCLPManaged(
				parmDelete, conn);
		if (result.getErrCode() < 0) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 删除CLPPack信息根据standardFlag
	 * 
	 * @param regionCode
	 *            String
	 * @param case_no
	 *            String
	 * @param clncpath_code
	 *            String
	 * @param conn
	 *            TConnection
	 * @param isFlag
	 *            boolean
	 * @return boolean
	 */
	private boolean deleteManagedHistory(String regionCode, String case_no,
			String clncpath_code, TConnection conn, boolean standardFlag) {
		boolean flag = true;
		TParm parmDelete = new TParm();
		parmDelete.setData("REGION_CODE", regionCode);
		parmDelete.setData("CASE_NO", case_no);
		parmDelete.setData("CLNCPATH_CODE", clncpath_code);
		parmDelete.setData("STANDARD_FLG", standardFlag ? "Y" : "N");
		TParm result = CLPManagedTool.getInstance().deleteCLPManaged(
				parmDelete, conn);
		if (result.getErrCode() < 0) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 插入CLPManaged From CLPPack
	 * 
	 * @return boolean ============pangben 20120522 添加版本号、科室代码参数
	 * 
	 */
	private boolean insertManagedFromCLPPack(String regionCode, String case_no,
			String clncpath_code, String deployDate, String version,
			String deptCode, TConnection conn, Map operatorMap) {
		boolean flag = true;
		TParm clpPackTParm = new TParm();
		clpPackTParm.setData("CLNCPATH_CODE", clncpath_code);
		clpPackTParm.setData("REGION_CODE", regionCode);
		clpPackTParm.setData("CASE_NO", case_no);
		clpPackTParm.setData("VERSION", version);

		// 展开时标准数据需要根据version 进行处理
		// TParm clpresult = CLPManagedTool.getInstance().
		// selectManagedInfoFromCLPPack(clpPackTParm);
		TParm clpresult = CLPManagedTool.getInstance()
				.selectManagedInfoFromCLPPackWithVersion(clpPackTParm);
		// pangben 2012-05-22 start 获得最大序号
		String sql = "SELECT MAX(ORDER_SEQ) AS ORDER_SEQ FROM CLP_MANAGED "
				+ " WHERE CASE_NO='" + case_no + "' AND CLNCPATH_CODE='"
				+ clncpath_code + "'";
		TParm maxSeqParm = new TParm(TJDODBTool.getInstance().select(sql));
		int seq = 0;
		if (null != maxSeqParm.getValue("ORDER_SEQ", 0)) {
			seq = maxSeqParm.getInt("ORDER_SEQ", 0);
		}
		// pangben 2012-05-22 stop
		for (int i = 0; i < clpresult.getCount(); i++) {
			TParm parm = clpresult.getRow(i);
			this.putBasicSysInfoIntoParm(parm, operatorMap);
			parm.setData("CASE_NO", case_no);
			parm.setData("CLNCPATH_CODE", clncpath_code);
			parm.setData("STANDING_DTTM", deployDate);
			// 处理EXEC_FLG
			if (parm.getData("EXEC_FLG") == null) {
				TNull tnull = new TNull(String.class);
				parm.setData("EXEC_FLG", tnull);
			}
			// 计算总量
			// 用量，单位，频次，日份
			// System.out.println("查询CLPPACK的数据");
			double dose = parm.getDouble("DOSE");
			int doseDays = parm.getInt("DOSE_DAYS");
			double freqTimes = parm.getDouble("FREQ_TIMES");// ======pangben
			double dosageQty =0.00; 
			//判断此医嘱是否是药品
			if (null!=parm.getValue("MEDI_QTY") &&null!=parm.getValue("DOSAGE_UNIT")&&parm.getValue("DOSAGE_UNIT").length()>0) {
				String tranSql = "SELECT MEDI_QTY FROM PHA_TRANSUNIT "
					+ " WHERE ORDER_CODE='"+parm.getValue("ORDER_CODE")+"'";
				TParm tranParm = new TParm(TJDODBTool.getInstance().select(tranSql));//获得字典用量===pangben 2015-7-8
				double mediQty=tranParm.getDouble("MEDI_QTY",0);//开药标准量
				if (mediQty==0) {
					dosageQty=0.00;
				}else{
					dosageQty=StringTool.round(dose/mediQty * freqTimes * doseDays, 4);
				}
				parm.setData("DOSE_UNIT",parm.getValue("DOSAGE_UNIT"));//药品发药单位
			}else{
				//集合医嘱操作
				parm.setData("DOSE_UNIT",parm.getValue("UNIT_CODE"));//集合医嘱单位
				dosageQty=StringTool.round(dose * freqTimes * doseDays, 4);
			}
//			if (null!=parm.getValue("ORDERSET_FLG")&&parm.getValue("ORDERSET_FLG").equals("Y")) {
//				
//			}else{
//				
//			}
			// 2012-05-22频次
			// System.out.println("查询出的用量:" + dose);
			// System.out.println("单位:" + doseUnit);
			// System.out.println("频次" + freqCode);
			// System.out.println("查询出的日份:" + doseDays);
			// TParm totalAndFeesParm = TotQtyTool.getIbsQty(dose, doseUnit,
			// freqCode,
			// doseDays);
			// System.out.println("计算出的总量:" +
			// totalAndFeesParm.getDouble("DOSAGE_QTY"));
			// parm.setData("TOT", totalAndFeesParm.getDouble("DOSAGE_QTY"));
			// TParm ownPriceParm = CLPManagedTool.getInstance().selectOwnPrice(
			// this.cloneTParm(parm), conn);//---

			
			double ownPrice = parm.getDouble("OWN_PRICE");// ======pangben
			// 2012-05-22 修改
			double totAmt = ownPrice * dosageQty;
			totAmt = StringTool.round(totAmt, 2);
			parm.setData("TOT", dosageQty);
			parm.setData("TOT_AMT", totAmt);
			// 处理ORDER_NO
			parm.setData("ORDER_NO", SystemTool.getInstance().getNo("ALL",
					"CLP", "CLP", "ORDERNO"));
			parm.setData("ORDER_SEQ", seq);// ====pangben 2012-05-22 最大序号
			parm.setData("DEPT_CODE", deptCode);// ====pangben 2012-05-22 科室代码
			TParm result = CLPManagedTool.getInstance()
					.inserttCLPManagedWithCLPPack(parm, conn);// 改动
			if (result.getErrCode() < 0) {
				flag = false;
			}
			seq++;
		}
		return flag;
	}

	/**
	 * 插入 IBSORDD From CLPPack
	 * 
	 * @param regionCode
	 *            String
	 * @param case_no
	 *            String
	 * @param clncpath_code
	 *            String
	 * @return boolean
	 */
	private boolean insertManagedFromIBSORDD(String regionCode, String case_no,
			String clncpath_code, String deployDate, TConnection conn,
			Map operatorMap) {
		boolean flag = true;
		TParm parm = new TParm();
		parm.setData("CASE_NO", case_no);
		parm.setData("CLNCPATH_CODE", clncpath_code);
		TParm result = CLPManagedTool.getInstance()
				.selectManagedInfoFromIBSOrdd(parm);
		// 第一次遍历循环，将查询出来需要写入实际项目中的数据进行完全匹配，只有orderCode=mainOrderCode的数据才能进行合并
		for (int i = 0; i < result.getCount(); i++) {
			TParm ibsParm = result.getRow(i);
			insertIntoManagedWithIBSOrdddSelectDataWithSameData(ibsParm,
					regionCode, case_no, clncpath_code, deployDate, conn,
					operatorMap);
		}
		// 第二次循环，将第一次orderCode=mainOrderCode
		// 还没有匹配上的数据进行处理，找出属于同一类型的数据进行合并，若实际数据与多条
		// 标准数据匹配则仅合并查询出的第一条数据
		conn.commit();
		result = CLPManagedTool.getInstance()
				.selectCLPManaedFromIBSOrddTwo(parm);
		if (result.getCount("CASE_NO")>0) {
			result = CLPManagedTool.getInstance()
			.selectManagedInfoFromIBSOrdd(parm);
		}else{
			return true;
		}
		for (int i = 0; i < result.getCount(); i++) {
			TParm ibsParm = result.getRow(i);
			insertIntoManagedWithIBSOrdddSelectDataWithNotSameData(ibsParm,
					regionCode, case_no, clncpath_code, deployDate, conn,
					operatorMap);
		}

		if (result.getErrCode() < 0) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 根据IBSOrdd查询出的数据根据不同的情况插入到clpManaged中 完全使用orderCode=mainCoderCode 进行匹配
	 * 
	 * @param selectData
	 *            TParm
	 */
	private boolean insertIntoManagedWithIBSOrdddSelectDataWithSameData(
			TParm selectData, String regionCode, String case_no,
			String clncpath_code, String deployDate, TConnection conn,
			Map operatorMap) {
		boolean isExist = CLPManagedTool.getInstance()
				.checkIBSOrddExistInCLPManaged(case_no, clncpath_code,
						selectData.getValue("SCHD_CODE"),
						selectData.getValue("ORDER_CODE"),
						selectData.getValue("DOSAGE_UNIT"),
						selectData.getValue("QTY"));
		// 处理数据begin
		selectData.setData("CASE_NO", case_no);
		selectData.setData("CLNCPATH_CODE", clncpath_code);
		selectData.setData("STANDING_DTTM", deployDate);
		this.putBasicSysInfoIntoParm(selectData, operatorMap);
		putTNull(selectData, "EXEC_USER", String.class);
		putTNull(selectData, "PROGRESS_CODE", String.class);
		putTNull(selectData, "MEDICAL_NOTE", String.class);
		// 处理数据end
		TParm result = null;
		if (isExist) {
			// 查找出需要更新的病患信息
			TParm patientParm = new TParm();
			patientParm.setData("CASE_NO", case_no);
			patientParm.setData("CLNCPATH_CODE", clncpath_code);
			patientParm.setData("SCHD_CODE", selectData.getValue("SCHD_CODE"));
			patientParm
					.setData("ORDER_CODE", selectData.getValue("ORDER_CODE"));
			patientParm.setData("MEDI_UNIT", selectData.getValue("DOSAGE_UNIT"));
			TParm updateData = CLPManagedTool.getInstance()
					.getUpdateDataInCLPManagedWithPatienInfo(patientParm);
			// System.out.println("需要更新的数据" + updateData);
			// 根据实际数据遍历标准且ordertype符合的数据若ordertype符合且ordercode符合的话优先更新
			// orderType和orderCode都相同的数据
			for (int i = 0; i < updateData.getCount(); i++) {
				TParm updateDatatmp = updateData.getRow(i);
				// 若两条记录的orderCode相同，则在orderERTYPE里对应的QTY和UNIT
				// 也是一样的。在比对相同orderCode进行合并时无需做判断
				// QTY和UNIT 的比对主要是用于在属于同一临床路径项目的不同order间的比对，不仅要orderCode属于同一个项目
				// 而且这两个orderCode的临床路径单位和临床路径数量也要相同。
				if (updateDatatmp.getValue("ORDER_CODE").equals(
						selectData.getValue("ORDER_CODE"))) {
					String mainCode = updateDatatmp.getValue("MAINORD_CODE");
					// 已经含有实际数据的managed表的记录不进行更新
					if (this.checkNullAndEmpty(mainCode)
							|| selectData.getValue("ORDER_CODE").equals(
									mainCode)) {
						continue;
					}
					// 处理需要更新的数据
					selectData.setData("ORDER_NO", updateDatatmp
							.getData("ORDER_NO"));
					selectData.setData("ORDER_SEQ", updateDatatmp
							.getData("ORDER_SEQ"));
					//集合医嘱没有细项 金额不显示问题 调整
					//==========pangben 2012-7-6 
					if (null!=updateDatatmp.getValue("ORDERSET_FLG") && "Y".equals(updateDatatmp.getValue("ORDERSET_FLG"))) {
						result = CLPManagedTool.getInstance()
						.updateCLPManagedWithIBSOrddOrderSet(selectData, conn);
					}else{
						result = CLPManagedTool.getInstance()
						.updateCLPManagedWithIBSOrdd(selectData, conn);
					}
					if (result.getErrCode() < 0) {
						return false;
					}
				}
				// conn.commit(); // 提交以便下次判断能够看到新加入的值
			}
		}
		if (result == null) {
			return true;
		}
		if (result.getErrCode() < 0) {
			// System.out.println("根据IBSOrdd查询出的数据根据不同的情况插入到clpmanaged失败");
			return false;
		} else {
			// System.out.println("根据IBSOrdd查询出的数据根据不同的情况插入到clpmanaged成功");
			return true;
		}
	}

	/**
	 * 根据IBSOrdd查询出的数据根据不同的情况插入到clpManaged中(处理OrderCode不用时根据临床路径项进行分析)
	 * 将orderCode=mainOrderCode 没有匹配的数据进行二次匹配，匹配条件为mainOrderCode
	 * 和orderCode同属于一个type
	 * 
	 * @param selectData
	 *            TParm
	 */
	private boolean insertIntoManagedWithIBSOrdddSelectDataWithNotSameData(
			TParm selectData, String regionCode, String case_no,
			String clncpath_code, String deployDate, TConnection conn,
			Map operatorMap) {
		boolean isExist = CLPManagedTool.getInstance()
				.checkIBSOrddExistInCLPManaged(case_no, clncpath_code,
						selectData.getValue("SCHD_CODE"),
						selectData.getValue("ORDER_CODE"),
						selectData.getValue("DOSAGE_UNIT"),
						selectData.getValue("QTY"));
		// 处理数据begin
		selectData.setData("CASE_NO", case_no);
		selectData.setData("CLNCPATH_CODE", clncpath_code);
		selectData.setData("STANDING_DTTM", deployDate);
		this.putBasicSysInfoIntoParm(selectData, operatorMap);
		putTNull(selectData, "EXEC_USER", String.class);
		putTNull(selectData, "PROGRESS_CODE", String.class);
		putTNull(selectData, "MEDICAL_NOTE", String.class);
		// 处理数据end
		TParm result = null;
		if (isExist) {
			// 查找出需要更新的病患信息
			TParm patientParm = new TParm();
			patientParm.setData("CASE_NO", case_no);
			patientParm.setData("CLNCPATH_CODE", clncpath_code);
			patientParm.setData("SCHD_CODE", selectData.getValue("SCHD_CODE"));
			patientParm
					.setData("ORDER_CODE", selectData.getValue("ORDER_CODE"));
			patientParm.setData("MEDI_UNIT", selectData.getValue("DOSAGE_UNIT"));
			TParm updateData = CLPManagedTool.getInstance()
					.getUpdateDataInCLPManagedWithPatienInfo(patientParm);
			// System.out.println("需要更新的数据" + updateData);
			// 根据实际数据遍历标准且ordertype符合的数据若ordertype符合且ordercode符合的话优先更新
			// orderType和orderCode都相同的数据
			boolean ishasSave = false;
			// 判断数据是否已经在上一次的循环中处理过，若处理过则不进行处理
			for (int i = 0; i < updateData.getCount(); i++) {
				TParm updateDatatmp = updateData.getRow(i);
				String mainCode = updateDatatmp.getValue("MAINORD_CODE");
				// 可以更新的标准数据中的mainOrderCode与更新实际的数据的orderCode相同时说明改记录已经在上一次
				// 展开时展开，不进行处理，并将标识记录为true。
				if (selectData.getValue("ORDER_CODE")
						.equalsIgnoreCase(mainCode)) {
					ishasSave = true;
					continue;
				}
			}
			if (!ishasSave) {
				for (int i = 0; i < updateData.getCount(); i++) {
					TParm updateDatatmp = updateData.getRow(i);
					// 卢海 2011-07-20 修改 将条件i==0 改动成ishasSave ==false
					// 即只有没有更新的数据，才进行更新，更新后将表示ishasSave设置成true
					// 防止仅更新i=0 而第一条记录mainCode存在值的情况
					if (ishasSave == false) {
						String mainCode = updateDatatmp
								.getValue("MAINORD_CODE");
						// 若标准数据中已经含有mainCode 虽更新的实际数据中与之匹配但该条标准数据已经记录完毕，不进行更新操作
						if (this.checkNullAndEmpty(mainCode)) {
							continue;
						}
						// 卢海 2011-07-20 加入判断 begin
						// 对于orderCode 不完全匹配时，不仅要验证替换的orderCode与原orderCode
						// 是否属于同一个临床路径项目
						// 对替换orderCode的临床路径单位和临床路径数量也要进行判断
						String orderCode = updateDatatmp.getValue("ORDER_CODE");
						String mainOrderCode = selectData
								.getValue("ORDER_CODE");
						boolean isSame = CLPManagedTool.getInstance()
								.checkOrderIsValid(orderCode, mainOrderCode);
						if (!isSame) {
							continue;
						}
						// 卢海 2011-07-20 加入判断 end
						// 处理需要更新的数据
						selectData.setData("ORDER_NO", updateDatatmp
								.getData("ORDER_NO"));
						selectData.setData("ORDER_SEQ", updateDatatmp
								.getData("ORDER_SEQ"));
						//==========pangben 2012-7-6 
						if (null!=updateDatatmp.getValue("ORDERSET_FLG") && "Y".equals(updateDatatmp.getValue("ORDERSET_FLG"))) {
							result = CLPManagedTool.getInstance()
							.updateCLPManagedWithIBSOrddOrderSet(selectData, conn);
						}else{
							result = CLPManagedTool.getInstance()
							.updateCLPManagedWithIBSOrdd(selectData, conn);
						}
						ishasSave = true;
					} else {
						// 多出的数据属于一条实际记录对应多条标准数据的情况，仅将实际数据合并到
						// 查询的第一条数据中，其余的标准不做处理
					}
					conn.commit(); // 提交以便下次判断能够看到新加入的值
				}
			}
			// 遍历相同orderCode进行更新和orderCode相同时更新都没有匹配的情况下，属于新增加的数据
			// 进入插入操作
			if (!ishasSave) {
				// System.out.println(
				// "遍历相同orderCode进行更新和orderCode相同时更新都没有匹配的情况下，属于新增加的数据进入插入操作");
				// 处理ORDER_NO
				selectData.setData("ORDER_NO", SystemTool.getInstance().getNo(
						"ALL", "CLP", "CLP", "ORDERNO"));
				// ===========pangben 2012-5-30 获得最大号
				TParm maxSeqParm = maxSEQ(case_no, clncpath_code);
				selectData.setData("TOT_AMT", selectData.getDouble("MAIN_AMT"));// 总金额

				if (maxSeqParm.getErrCode() < 0) {
					return false;
				}
				selectData.setData("ORDER_SEQ", maxSeqParm.getInt("ORDER_SEQ",
						0) + 1);
				result = CLPManagedTool.getInstance()
						.insertCLPManagedWithIBSOrdd(selectData, conn);
				conn.commit(); // 提交以便下次判断能够看到新加入的值
			}
		} else {
			// System.out.println("managed中数据不存在插入");
			// 处理ORDER_NO
			selectData.setData("ORDER_NO", SystemTool.getInstance().getNo(
					"ALL", "CLP", "CLP", "ORDERNO"));
			// ===========pangben 2012-5-30 获得最大号
			TParm maxSeqParm = maxSEQ(case_no, clncpath_code);
			if (maxSeqParm.getErrCode() < 0) {
				return false;
			}
			selectData.setData("TOT_AMT", selectData.getDouble("MAIN_AMT"));// 总金额
			selectData.setData("ORDER_SEQ",
					maxSeqParm.getInt("ORDER_SEQ", 0) + 1);
			result = CLPManagedTool.getInstance().insertCLPManagedWithIBSOrdd(
					selectData, conn);
		}
		if (result == null) {
			return true;
		}
		if (result.getErrCode() < 0) {
			// System.out.println("根据IBSOrdd查询出的数据根据不同的情况插入到clpmanaged失败");
			return false;
		} else {
			// System.out.println("根据IBSOrdd查询出的数据根据不同的情况插入到clpmanaged成功");
			return true;
		}
	}

	/**
	 * 获得最大号
	 * 
	 * @param case_no
	 * @param clncpath_code
	 * @return ===========pangben 2012-5-30
	 */
	private TParm maxSEQ(String case_no, String clncpath_code) {
		String sql = "SELECT MAX(ORDER_SEQ) AS ORDER_SEQ FROM CLP_MANAGED "
				+ " WHERE CASE_NO='" + case_no + "' AND CLNCPATH_CODE='"
				+ clncpath_code + "'";
		TParm maxSeqParm = new TParm(TJDODBTool.getInstance().select(sql));
		return maxSeqParm;
	}

	/**
	 * 向TParm中加入系统默认信息
	 * 
	 * @param parm
	 *            TParm
	 */
	private void putBasicSysInfoIntoParm(TParm parm, Map OperatorMap) {
		// System.out.println("total" + total);
		parm.setData("REGION_CODE", OperatorMap.get("REGION_CODE"));
		parm.setData("OPT_USER", OperatorMap.get("OPT_USER"));
		Timestamp today = SystemTool.getInstance().getDate();
		String datestr = StringTool.getString(today, "yyyyMMdd");
		parm.setData("OPT_DATE", datestr);
		parm.setData("OPT_TERM", OperatorMap.get("OPT_TERM"));
	}

	/**
	 * 处理TParm 里的null的方法
	 * 
	 * @param parm
	 *            TParm
	 * @param keyStr
	 *            String
	 * @param type
	 *            Class
	 */
	private void putTNullVector(TParm parm, String keyStr, Class type) {
		for (int i = 0; i < parm.getCount(); i++) {
			if (parm.getData(keyStr, i) == null) {
				// System.out.println("处理为空情况");
				TNull tnull = new TNull(type);
				parm.setData(keyStr, i, tnull);
			}
		}
	}

	/**
	 * 处理TParm 里null值方法
	 * 
	 * @param parm
	 *            TParm
	 * @param keyStr
	 *            String
	 * @param type
	 *            Class
	 */
	private void putTNull(TParm parm, String keyStr, Class type) {
		if (parm.getData(keyStr) == null) {
			// System.out.println("处理为空情况");
			TNull tnull = new TNull(type);
			parm.setData(keyStr, tnull);
		}
	}

	/**
	 * 检查是否为空或空串
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}

	/**
	 * 克隆对象
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	// private TParm cloneTParm(TParm from) {
	// TParm returnTParm = new TParm();
	// for (int i = 0; i < from.getNames().length; i++) {
	// returnTParm.setData(from.getNames()[i], from.getData(from
	// .getNames()[i]));
	// }
	// return returnTParm;
	// }

}
