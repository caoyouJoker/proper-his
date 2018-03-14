package jdo.sta;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import java.text.DecimalFormat;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 工作报表
 * </p>
 * 
 * <p>
 * Description: 工作报表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangk 2009-5-21
 * @version 1.0
 */
public class STAWorkLogTool extends TJDOTool {

	private String selectDayData = "SELECT "
			+ " SUM(B.OUYCHK_OI_NUM) AS OUYCHK_OI_NUM,SUM(B.OUYCHK_RAPA_NUM) AS OUYCHK_RAPA_NUM,SUM(B.OUYCHK_INOUT) AS OUYCHK_INOUT,SUM(B.OUYCHK_OPBFAF) AS OUYCHK_OPBFAF,SUM(B.HEAL_LV_I_CASE) AS HEAL_LV_I_CASE, "
			+ " SUM(B.HEAL_LV_BAD) AS HEAL_LV_BAD,SUM(B.GET_TIMES) AS GET_TIMES,SUM(B.SUCCESS_TIMES) AS SUCCESS_TIMES,SUM(A.DATA_01) AS DATA_01, SUM(A.DATA_02) AS DATA_02, SUM(A.DATA_03) AS DATA_03, SUM(A.DATA_04) AS DATA_04, "
			+ " SUM(A.DATA_05) AS DATA_05, SUM(A.DATA_06) AS DATA_06, SUM(A.DATA_06_1) AS DATA_06_1, SUM(A.DATA_07) AS DATA_07, SUM(A.DATA_08) AS DATA_08, "
			+ " SUM(A.DATA_08_1) AS DATA_08_1, SUM(A.DATA_09) AS DATA_09, SUM(A.DATA_10) AS DATA_10, SUM(A.DATA_11) AS DATA_11, SUM(A.DATA_12) AS DATA_12, "
			+ " SUM(A.DATA_13) AS DATA_13, SUM(A.DATA_14) AS DATA_14, SUM(A.DATA_15) AS DATA_15, SUM(A.DATA_15_1) AS DATA_15_1, SUM(A.DATA_16) AS DATA_16,"
			+ " SUM(A.DATA_19) AS DATA_19, SUM(A.DATA_20) AS DATA_20, SUM(A.DATA_21) AS DATA_21, "
			+ " SUM(A.DATA_22) AS DATA_22, A.DEPT_CODE,A.DATA_17,A.DATA_18,A.CONFIRM_FLG"
			+ " FROM STA_DAILY_01 A,STA_STATION_DAILY B "
			+ " WHERE  A.DEPT_CODE=B.DEPT_CODE AND A.STATION_CODE=B.STATION_CODE " + " AND A.STA_DATE=B.STA_DATE";

	private String selectStationMData = "SELECT "
			+ " SUM(B.OUYCHK_OI_NUM) AS OUYCHK_OI_NUM,SUM(B.OUYCHK_RAPA_NUM) AS OUYCHK_RAPA_NUM,SUM(B.OUYCHK_INOUT) AS OUYCHK_INOUT,SUM(B.OUYCHK_OPBFAF) AS OUYCHK_OPBFAF,SUM(B.HEAL_LV_I_CASE) AS HEAL_LV_I_CASE, "
			+ " SUM(B.HEAL_LV_BAD) AS HEAL_LV_BAD,SUM(B.GET_TIMES) AS GET_TIMES,SUM(B.SUCCESS_TIMES) AS SUCCESS_TIMES,SUM(A.DATA_01) AS DATA_01, SUM(A.DATA_02) AS DATA_02, SUM(A.DATA_03) AS DATA_03, SUM(A.DATA_04) AS DATA_04, "
			+ " SUM(A.DATA_05) AS DATA_05, SUM(A.DATA_06) AS DATA_06, SUM(A.DATA_06_1) AS DATA_06_1, SUM(A.DATA_07) AS DATA_07, SUM(A.DATA_08) AS DATA_08, "
			+ " SUM(A.DATA_08_1) AS DATA_08_1, SUM(A.DATA_09) AS DATA_09, SUM(A.DATA_10) AS DATA_10, SUM(A.DATA_11) AS DATA_11, SUM(A.DATA_12) AS DATA_12, "
			+ " SUM(A.DATA_13) AS DATA_13, SUM(A.DATA_14) AS DATA_14, SUM(A.DATA_15) AS DATA_15, SUM(A.DATA_15_1) AS DATA_15_1, SUM(A.DATA_16) AS DATA_16,"
			+ " SUM(A.DATA_19) AS DATA_19, SUM(A.DATA_20) AS DATA_20, SUM(A.DATA_21) AS DATA_21, "
			+ " SUM(A.DATA_22) AS DATA_22, A.DEPT_CODE,A.DATA_17,A.DATA_18,A.CONFIRM_FLG"
			+ " FROM STA_DAILY_01 A,STA_STATION_DAILY B "
			+ " WHERE  A.DEPT_CODE=B.DEPT_CODE  AND A.STATION_CODE=B.STATION_CODE " 
			+ " AND A.STA_DATE=B.STA_DATE";

	private String selectOpdMData = "SELECT SUM(OUTP_NUM) AS OUTP_NUM,SUM(ERD_NUM) AS ERD_NUM,SUM(ERD_DIED_NUM) AS ERD_DIED_NUM,SUM(OBS_NUM) AS OBS_NUM,SUM(OBS_DIED_NUM) AS OBS_DIED_NUM, "
			+ " DEPT_CODE " + " FROM STA_OPD_DAILY ";
	/**
	 * 实例
	 */
	public static STAWorkLogTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return RegMethodTool
	 */
	public static STAWorkLogTool getInstance() {
		if (instanceObject == null)
			instanceObject = new STAWorkLogTool();
		return instanceObject;
	}

	public STAWorkLogTool() {
		setModuleName("sta\\STAWorkLogModule.x");
		onInit();
	}

	/**
	 * 插入 STA_DAILY_02(工作报表) 数据 单行插入
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertdate(TParm parm, TConnection conn) {
		TParm result = this.update("insertData", parm, conn);
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 插入 STA_DAILY_02(工作报表) 数据 多行插入
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertDaily(TParm Dailyparm, TConnection conn) {
		String STA_DATE = Dailyparm.getValue("STA_DATE"); // 删除数据参数
		TParm parm = Dailyparm.getParm("Daily"); // 插入数据参数
		TParm result = new TParm();
		// ==================pangben modify 20110520 添加区域参数
		result = deleteSTA_DAILY_02(STA_DATE,
				Dailyparm.getValue("REGION_CODE"), conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// 检核门诊审核数据状态
		// =======pangben modify 20110519 start 添加区域参数
		int reFlg = STATool.getInstance().checkCONFIRM_FLG("STA_OPD_DAILY",
				STA_DATE, Dailyparm.getValue("REGION_CODE"));
		// =======pangben modify 20110519 stop
		TParm parm1 = new TParm();
		for (int i = 0; i < parm.getCount("STA_DATE"); i++) {
			parm1.setData("STA_DATE", parm.getValue("STA_DATE", i));
			parm1.setData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
			parm1.setData("STATION_CODE", parm.getValue("STATION_CODE", i));
			parm1.setData("DATA_01", parm.getValue("DATA_01", i));
			parm1.setData("DATA_02", parm.getValue("DATA_02", i));
			parm1.setData("DATA_03", parm.getValue("DATA_03", i));
			parm1.setData("DATA_04", parm.getValue("DATA_04", i));
			parm1.setData("DATA_05", parm.getValue("DATA_05", i));
			parm1.setData("DATA_06", parm.getValue("DATA_06", i));
			parm1.setData("DATA_07", parm.getValue("DATA_07", i));
			parm1.setData("DATA_08", parm.getValue("DATA_08", i));
			parm1.setData("DATA_08_1", parm.getValue("DATA_08_1", i));
			parm1.setData("DATA_09", parm.getValue("DATA_09", i));
			parm1.setData("DATA_10", parm.getValue("DATA_10", i));
			parm1.setData("DATA_11", parm.getValue("DATA_11", i));
			parm1.setData("DATA_12", parm.getValue("DATA_12", i));
			parm1.setData("DATA_13", parm.getValue("DATA_13", i));
			parm1.setData("DATA_14", parm.getValue("DATA_14", i));
			parm1.setData("DATA_15", parm.getValue("DATA_15", i));
			parm1.setData("DATA_15_1", parm.getValue("DATA_15_1", i));
			parm1.setData("DATA_16", parm.getValue("DATA_16", i));
			parm1.setData("DATA_17", parm.getValue("DATA_17", i));
			parm1.setData("DATA_18", parm.getValue("DATA_18", i));
			parm1.setData("DATA_19", parm.getValue("DATA_19", i));
			parm1.setData("DATA_20", parm.getValue("DATA_20", i));
			parm1.setData("DATA_21", parm.getValue("DATA_21", i));
			parm1.setData("DATA_22", parm.getValue("DATA_22", i));
			parm1.setData("DATA_23", parm.getValue("DATA_23", i));
			parm1.setData("DATA_24", parm.getValue("DATA_24", i));
			parm1.setData("DATA_25", parm.getValue("DATA_25", i));
			parm1.setData("DATA_26", parm.getValue("DATA_26", i));
			parm1.setData("DATA_27", parm.getValue("DATA_27", i));
			parm1.setData("DATA_28", parm.getValue("DATA_28", i));
			parm1.setData("DATA_29", parm.getValue("DATA_29", i));
			parm1.setData("DATA_30", parm.getValue("DATA_30", i));
			parm1.setData("DATA_31", parm.getValue("DATA_31", i));
			parm1.setData("DATA_32", parm.getValue("DATA_32", i));
			parm1.setData("DATA_33", parm.getValue("DATA_33", i));
			parm1.setData("DATA_34", parm.getValue("DATA_34", i));
			parm1.setData("DATA_35", parm.getValue("DATA_35", i));
			parm1.setData("DATA_36", parm.getValue("DATA_36", i));
			parm1.setData("DATA_37", parm.getValue("DATA_37", i));
			parm1.setData("DATA_38", parm.getValue("DATA_38", i));
			parm1.setData("DATA_39", parm.getValue("DATA_39", i));
			parm1.setData("DATA_40", parm.getValue("DATA_40", i));
			parm1.setData("DATA_41", parm.getValue("DATA_41", i));
			parm1.setData("DATA_41_1", parm.getValue("DATA_41_1", i));
			parm1.setData("DATA_41_2", parm.getValue("DATA_41_2", i));
			parm1.setData("CONFIRM_FLG", parm.getValue("CONFIRM_FLG", i));
			parm1.setData("CONFIRM_USER", parm.getValue("CONFIRM_USER", i));
			parm1.setData("OPT_USER", parm.getValue("OPT_USER", i));
			parm1.setData("OPT_TERM", parm.getValue("OPT_TERM", i));
			// ============pangben modify 20110519 start
			parm1.setData("REGION_CODE", Dailyparm.getValue("REGION_CODE"));
			// ============pangben modify 20110519 stop
			if (reFlg == 2 && parm.getValue("SUBMIT_FLG", i).equals("Y"))// 判断病区日志和门诊日志都已经提交
				parm1.setData("SUBMIT_FLG", "Y");
			else
				// 其中有一种未提交的就将提交标记设置为N
				parm1.setData("SUBMIT_FLG", "N");
			result = this.insertdate(parm1, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * 获取中间表的所有3级部门
	 * 
	 * @return TParm ===========pangben modify 20110525 添加区域参数
	 */
	public TParm selectDEPT(String regionCode) {
		// ===========pangben modify 20110525 start
		TParm parm = new TParm();
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ===========pangben modify 20110525 stop
		TParm result = this.query("selectDEPT", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 获取中间表的所有3和4级部门
	 * 
	 * @return TParm
	 */
	public TParm selectDEPT_4(TParm parm) {
		TParm result = this.query("selectDEPT_4", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据STA_DATE 查询 STA_DAILY_02表中是否已经存在该日期的数据
	 * 
	 * @param STA_DATE
	 *            String
	 * @param regionCode
	 *            String
	 * @return TParm =====pangben modify 20110519 添加区域参数
	 */
	public TParm checkNum(String STA_DATE, String regionCode) {
		TParm parm = new TParm();
		parm.setData("STA_DATE", STA_DATE);
		// =====pangben modify 20110519 strat
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// =====pangben modify 20110519 stop
		TParm result = this.query("checkNum", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 删除STA_DAILY_02表数据
	 * 
	 * @param STA_DATE
	 *            String
	 * @return TParm ========pangben modify 20110520 添加区域参数
	 */
	public TParm deleteSTA_DAILY_02(String STA_DATE, String regionCode,
			TConnection conn) {
		TParm parm = new TParm();
		// ========pangben modify 20110520 start
		parm.setData("STA_DATE", STA_DATE);
		parm.setData("REGION_CODE", regionCode);
		// ========pangben modify 20110520 stop
		TParm result = this.update("deleteSTA_DAILY_02", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 删除STA_DAILY_02表数据
	 * 
	 * @param STA_DATE
	 *            String
	 * @return TParm ========pangben modify 20110520 添加区域参数
	 */
	public TParm updateSTA_DAILY_02(TParm  parm) {
		// ========pangben modify 20110520 stop
		TParm result = this.update("updateSTA_DAILY_02", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}


	/**
	 * 查询住院相关数据(日报)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectStation(TParm parm) {
		TParm result = new TParm();
		if (parm.getValue("STA_DATE").trim().length() <= 0) {
			result.setErr(-1, "缺少必要参数！STA_DATE");
			return result;
		}
		// ============pangben modify 20110520 start 添加区域参数
		String region = "";
		if (null != parm.getValue("REGION_CODE")
				&& parm.getValue("REGION_CODE").length() > 0)
			region = " AND B.REGION_CODE='" + parm.getValue("REGION_CODE")
					+ "' ";
		String deptCode = "";
		if (null != parm.getValue("DEPT_CODE")
				&& parm.getValue("DEPT_CODE").length() > 0)
			deptCode = " AND A.DEPT_CODE='" + parm.getValue("DEPT_CODE")
					+ "' ";
		// ============pangben modify 20110520 stop

		String sql = this.selectDayData + " AND A.STA_DATE='"
				+ parm.getValue("STA_DATE") + "' " + region
				+ " GROUP BY A.DEPT_CODE,A.DATA_17,A.DATA_18,A.CONFIRM_FLG";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 合并相同科室不同病区的部分数据
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSetStation(TParm parm) {
		if (parm.getCount() <= 0)
			return parm;
		for (int i = 0; i < parm.getCount(); i++) {
			TParm parmRow = parm.getRow(i);
			double BED_RETUEN = countBedReturn(parmRow.getInt("DATA_09"),
					parmRow.getInt("DATA_17")); // 周转次数 平均开放病床数(等于等于期末实有病床数)
			double i_BED_WORK_DAY = parmRow.getInt("DATA_17");// 病床工作日
																// 每床每天为1个工作日，每天导入的工作日应该等于期末实有病床数<17>
			double i_BED_USE_RATE = 0;
			if (parmRow.getInt("DATA_17") != 0)
				i_BED_USE_RATE = (double) parmRow.getInt("DATA_16")
						/ (double) parmRow.getInt("DATA_17") * 100; // 病床使用率=实际占用病床数/开放病床数
			else
				i_BED_USE_RATE = 0;
			parm.setData("BED_RETUEN", i, BED_RETUEN);
			parm.setData("BED_WORK_DAY", i, i_BED_WORK_DAY);
			parm.setData("BED_USE_RATE", i, i_BED_USE_RATE);

		}
		return parm;
	}

	/**
	 * 查询门急诊相关数据(日报)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOPD(TParm parm) {
		TParm result = this.query("selectOPD", parm);
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询住院相关数据(月报)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectStation_M(TParm parm) {
		TParm result = new TParm();
		if (parm.getValue("STA_DATE").trim().length() <= 0) {
			result.setErr(-1, "缺少必要参数！STA_DATE");
			return result;
		}
		// ============pangben modify 20110520 start 添加区域参数
		String region = "";
		if (null != parm.getValue("REGION_CODE")
				&& parm.getValue("REGION_CODE").length() > 0)
			region = " AND B.REGION_CODE='" + parm.getValue("REGION_CODE")
					+ "' ";
		// ============pangben modify 20110520 stop

		String sql = selectStationMData + " AND A.STA_DATE LIKE '"
				+ parm.getValue("STA_DATE") + "%' " + region
				+ " GROUP BY A.DEPT_CODE,A.DATA_17,A.DATA_18,A.CONFIRM_FLG";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询门急诊相关数据(月报)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOPD_M(TParm parm) {
		TParm result = new TParm();
		if (parm.getValue("STA_DATE").trim().length() <= 0) {
			result.setErr(-1, "缺少必要参数！STA_DATE");
			return result;
		}
		// ============pangben modify 20110520 start 添加区域参数
		String region = "";
		if (null != parm.getValue("REGION_CODE")
				&& parm.getValue("REGION_CODE").length() > 0)
			region = " AND REGION_CODE='" + parm.getValue("REGION_CODE") + "' ";
		// ============pangben modify 20110520 stop
		String sql = selectOpdMData + " WHERE STA_DATE LIKE '"
				+ parm.getValue("STA_DATE") + "%' " + region
				+ " GROUP BY DEPT_CODE";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询日报表数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectDataDay(TParm parm) {
		TParm reOPD = this.selectOPD(parm); // 获取所有门急诊中间表数据
		// System.out.println("所有门急诊中间表数据------------"+reOPD);
		if (reOPD.getErrCode() < 0) {
			err("ERR:" + reOPD.getErrCode() + reOPD.getErrText()
					+ reOPD.getErrName());
			return reOPD;
		}
		TParm reStation = onSetStation(selectStation(parm)); // 获取所有病区日志数据
		// System.out.println("所有病区日志数据------------"+reStation);
		if (reStation.getErrCode() < 0) {
			err("ERR:" + reStation.getErrCode() + reStation.getErrText()
					+ reStation.getErrName());
			return reStation;
		}
		// ========pangben modify 20110519 start
		TParm result = selectdataParm(reOPD, reStation, parm
				.getValue("REGION_CODE"));
		// System.out.println("-=result---------"+result);
		// ========pangben modify 20110519 stop
		return result;
	}

	/**
	 * 查询月报表数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectDataMonth(TParm parm) {
		TParm reOPD = this.selectOPD_M(parm); // 获取所有门急诊中间表数据
		if (reOPD.getErrCode() < 0) {
			err("ERR:" + reOPD.getErrCode() + reOPD.getErrText()
					+ reOPD.getErrName());
			return reOPD;
		}
		TParm reStation = this.selectStation_M(parm); // 获取所有病区日志数据
		if (reStation.getErrCode() < 0) {
			err("ERR:" + reStation.getErrCode() + reStation.getErrText()
					+ reStation.getErrName());
			return reStation;
		}
		TParm reFirstStation = this.selectStationFirstDay(parm); // 查询本月期初数据
		// System.out.println("reFirstStation:" + reFirstStation);
		if (reFirstStation.getErrCode() < 0) {
			err("ERR:" + reFirstStation.getErrCode()
					+ reFirstStation.getErrText() + reFirstStation.getErrName());
			return reFirstStation;
		}
		TParm reLastStation = this.selectStationLastDay(parm); // 查询本月期末数据
		// System.out.println("reLastStation:" + reLastStation);
		if (reLastStation.getErrCode() < 0) {
			err("ERR:" + reLastStation.getErrCode()
					+ reLastStation.getErrText() + reLastStation.getErrName());
			return reLastStation;
		}
		TParm result = selectdataParm(reOPD, reStation, reFirstStation,
				reLastStation, parm);
		return result;
	}

	/**
	 * 查询 日结工作报表
	 * 
	 * @param parm
	 *            TParm
	 * @param regionCode
	 *            TParm 区域参数
	 * @return TParm ============pangben modify 20110519 添加区域参数
	 */
	public TParm selectdataParm(TParm reOPD, TParm reStation, String regionCode) {
		TParm result = new TParm();
		// ============pangben modify 20110519 添加区域条件
		TParm DEPT_4Parm = new TParm();
		if (null != regionCode && regionCode.length() > 0)
			DEPT_4Parm.setData("REGION_CODE", regionCode);
		TParm dept = this.selectDEPT_4(DEPT_4Parm); // 获取所有4级中间部门
		// ============pangben modify 20110519 stop
		// 遍历所有部门 筛选出各部门的相关数据 填充到 TParm 中
		for (int i = 0; i < dept.getCount(); i++) {
			String deptcode = dept.getValue("DEPT_CODE", i);
			String stationcode = "";// 病区CODE
			String isSubmit = "Y";// 判断病区日志是否提交(之判断病区的科室) 只有门急诊的科室 默认为已经提交
			// 判断此科室是否是住院科室，如果是 isSubmit 默认为N(没有提交)
			if (dept.getValue("IPD_DEPT_CODE", i).trim().length() > 0) {
				isSubmit = "N";
			}
			int opdIndex = -1;
			int stationIndex = -1;
			// 从门急诊中间表中筛选该部门的数据 记录索引
			for (int j = 0; j < reOPD.getCount(); j++) {
				if (deptcode.equals(reOPD.getValue("DEPT_CODE", j))) {
					opdIndex = j;
				}
			}
			// 从病区表中筛选部门的数据 记录索引
			for (int h = 0; h < reStation.getCount(); h++) {
				if (deptcode.equals(reStation.getValue("DEPT_CODE", h))) {
					stationIndex = h;
				}
			}
			// 如果索引存在 就将相应的信息填充到Parm中
			if (opdIndex != -1) {
				result.addData("OUTP_NUM", reOPD.getData("OUTP_NUM", opdIndex));
				result.addData("ERD_NUM", reOPD.getData("ERD_NUM", opdIndex));
				result.addData("ERD_DIED_NUM", reOPD.getData("ERD_DIED_NUM",
						opdIndex));
				result.addData("OBS_NUM", reOPD.getData("OBS_NUM", opdIndex));
				result.addData("OBS_DIED_NUM", reOPD.getData("OBS_DIED_NUM",
						opdIndex));
				stationcode = dept.getValue("OE_DEPT_CODE", i); // 门急诊科室记录的是
																// 线上科室的CODE
			} else {
				result.addData("OUTP_NUM", null);
				result.addData("ERD_NUM", null);
				result.addData("ERD_DIED_NUM", null);
				result.addData("OBS_NUM", null);
				result.addData("OBS_DIED_NUM", null);
			}
			// 如果病区表索引存在就将相应的信息填充到Parm中
			if (stationIndex != -1) {
				result.addData("OUYCHK_OI_NUM", reStation.getData(
						"OUYCHK_OI_NUM", stationIndex));
				result.addData("OUYCHK_RAPA_NUM", reStation.getData(
						"OUYCHK_RAPA_NUM", stationIndex));
				result.addData("OUYCHK_INOUT", reStation.getData(
						"OUYCHK_INOUT", stationIndex));
				result.addData("OUYCHK_OPBFAF", reStation.getData(
						"OUYCHK_OPBFAF", stationIndex));
				result.addData("HEAL_LV_I_CASE", reStation.getData(
						"HEAL_LV_I_CASE", stationIndex));
				result.addData("HEAL_LV_BAD", reStation.getData("HEAL_LV_BAD",
						stationIndex));
				result.addData("GET_TIMES", reStation.getData("GET_TIMES",
						stationIndex));
				result.addData("SUCCESS_TIMES", reStation.getData(
						"SUCCESS_TIMES", stationIndex));
				result.addData("BED_RETUEN", reStation.getData("BED_RETUEN",
						stationIndex));
				result.addData("BED_WORK_DAY", reStation.getData(
						"BED_WORK_DAY", stationIndex));
				result.addData("BED_USE_RATE", reStation.getData(
						"BED_USE_RATE", stationIndex));
				result.addData("DATA_01", reStation.getData("DATA_01",
						stationIndex));
				result.addData("DATA_02", reStation.getData("DATA_02",
						stationIndex));
				result.addData("DATA_03", reStation.getData("DATA_03",
						stationIndex));
				result.addData("DATA_04", reStation.getData("DATA_04",
						stationIndex));
				result.addData("DATA_05", reStation.getData("DATA_05",
						stationIndex));
				result.addData("DATA_06", reStation.getData("DATA_06",
						stationIndex));
				result.addData("DATA_06_1", reStation.getData("DATA_06_1",
						stationIndex));
				result.addData("DATA_07", reStation.getData("DATA_07",
						stationIndex));
				result.addData("DATA_08", reStation.getData("DATA_08",
						stationIndex));
				result.addData("DATA_08_1", reStation.getData("DATA_08_1",
						stationIndex));
				result.addData("DATA_09", reStation.getData("DATA_09",
						stationIndex));
				result.addData("DATA_10", reStation.getData("DATA_10",
						stationIndex));
				result.addData("DATA_11", reStation.getData("DATA_11",
						stationIndex));
				result.addData("DATA_12", reStation.getData("DATA_12",
						stationIndex));
				result.addData("DATA_13", reStation.getData("DATA_13",
						stationIndex));
				result.addData("DATA_14", reStation.getData("DATA_14",
						stationIndex));
				result.addData("DATA_15", reStation.getData("DATA_15",
						stationIndex));
				result.addData("DATA_15_1", reStation.getData("DATA_15_1",
						stationIndex));
				result.addData("DATA_16", reStation.getData("DATA_16",
						stationIndex));
				result.addData("DATA_17", reStation.getData("DATA_17",
						stationIndex));
				result.addData("DATA_18", reStation.getData("DATA_18",
						stationIndex));
				result.addData("DATA_19", reStation.getData("DATA_19",
						stationIndex));
				result.addData("DATA_20", reStation.getData("DATA_20",
						stationIndex));
				result.addData("DATA_21", reStation.getData("DATA_21",
						stationIndex));
				result.addData("DATA_22", reStation.getData("DATA_22",
						stationIndex));
				stationcode = dept.getValue("IPD_DEPT_CODE", i); // 住院科室记录的病区CODE是线上的科室CODE
				if (reStation.getValue("CONFIRM_FLG", stationIndex).trim()
						.length() > 0)// 月报不存在提交字段
					isSubmit = reStation.getValue("CONFIRM_FLG", stationIndex);// 记录病区日志是否提交
			} else {
				result.addData("OUYCHK_OI_NUM", null);
				result.addData("OUYCHK_RAPA_NUM", null);
				result.addData("OUYCHK_INOUT", null);
				result.addData("OUYCHK_OPBFAF", null);
				result.addData("HEAL_LV_I_CASE", null);
				result.addData("HEAL_LV_BAD", null);
				result.addData("GET_TIMES", null);
				result.addData("SUCCESS_TIMES", null);
				result.addData("BED_RETUEN", null);
				result.addData("BED_WORK_DAY", null);
				result.addData("BED_USE_RATE", null);
				result.addData("DATA_01", null);
				result.addData("DATA_02", null);
				result.addData("DATA_03", null);
				result.addData("DATA_04", null);
				result.addData("DATA_05", null);
				result.addData("DATA_06", null);
				result.addData("DATA_06_1", null);
				result.addData("DATA_07", null);
				result.addData("DATA_08", null);
				result.addData("DATA_08_1", null);
				result.addData("DATA_09", null);
				result.addData("DATA_10", null);
				result.addData("DATA_11", null);
				result.addData("DATA_12", null);
				result.addData("DATA_13", null);
				result.addData("DATA_14", null);
				result.addData("DATA_15", null);
				result.addData("DATA_15_1", null);
				result.addData("DATA_16", null);
				result.addData("DATA_17", null);
				result.addData("DATA_18", null);
				result.addData("DATA_19", null);
				result.addData("DATA_20", null);
				result.addData("DATA_21", null);
				result.addData("DATA_22", null);
			}
			// 如果本部门即不存在门急诊数据 也不存在 住院数据 那么会插入空信息
			if (opdIndex == -1 && stationIndex == -1) {
				// 如果该部门是住院部门 那么 StationCode 存放住院部门的Code
				if (dept.getValue("IPD_DEPT_CODE", i).trim().length() > 0) {
					stationcode = dept.getValue("IPD_DEPT_CODE", i);
				}
				// 如果不是住院部门 那么 StationCode 存放门诊部门的CODE
				else if (dept.getValue("OE_DEPT_CODE", i).trim().length() > 0) {
					stationcode = dept.getValue("OE_DEPT_CODE", i);
				} else {
					stationcode = dept.getValue("DEPT_CODE", i);
				}
			}
			result.addData("DEPT_CODE", deptcode); // 记录中间表部门CODE
			result.addData("STATION_CODE", stationcode); // 记录中间表StationCode
			result.addData("SUBMIT_FLG", isSubmit);// 记录病区日志是否提交
		}
		return result;
	}

	/**
	 * 查询 日结工作报表(月报表)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectdataParm(TParm reOPD, TParm reStation,
			TParm reFirstStation, TParm reLastStation, TParm parm) {
		TParm result = new TParm();
		// ===========pangben modify 20110519 start 添加参数 没有使用此方法
		TParm dept = this.selectDEPT_4(parm); // 获取所有4级中间部门
		// ===========pangben modify 20110519 stop
		// 遍历所有部门 筛选出各部门的相关数据 填充到 TParm 中
		for (int i = 0; i < dept.getCount(); i++) {
			String deptcode = dept.getValue("DEPT_CODE", i);
			String stationcode = "";// 病区CODE
			String isSubmit = "Y";// 判断病区日志是否提交(之判断病区的科室) 只有门急诊的科室 默认为已经提交
			// 判断此科室是否是住院科室，如果是 isSubmit 默认为N(没有提交)
			if (dept.getValue("IPD_DEPT_CODE", i).trim().length() > 0) {
				isSubmit = "N";
			}
			int opdIndex = -1;
			int stationIndex = -1;
			int firstIndex = -1;
			int lastIndex = -1;
			// 从门急诊中间表中筛选该部门的数据 记录索引
			for (int j = 0; j < reOPD.getCount(); j++) {
				if (deptcode.equals(reOPD.getValue("DEPT_CODE", j))) {
					opdIndex = j;
				}
			}
			// 从病区表中筛选部门的数据 记录索引
			for (int h = 0; h < reStation.getCount(); h++) {
				if (deptcode.equals(reStation.getValue("DEPT_CODE", h))) {
					stationIndex = h;
				}
			}
			// 从期初值中筛选对应的部门信息
			for (int h = 0; h < reFirstStation.getCount(); h++) {
				if (deptcode.equals(reFirstStation.getValue("DEPT_CODE", h))) {
					firstIndex = h;
				}
			}
			// 从期末值中筛选对应的部门信息
			for (int h = 0; h < reLastStation.getCount(); h++) {
				if (deptcode.equals(reFirstStation.getValue("DEPT_CODE", h))) {
					lastIndex = h;
				}
			}
			// System.out.println("firstIndex:"+firstIndex);
			// System.out.println("lastIndex:"+lastIndex);
			// 如果索引存在 就将相应的信息填充到Parm中
			if (opdIndex != -1) {
				result.addData("OUTP_NUM", reOPD.getData("OUTP_NUM", opdIndex));
				result.addData("ERD_NUM", reOPD.getData("ERD_NUM", opdIndex));
				result.addData("ERD_DIED_NUM", reOPD.getData("ERD_DIED_NUM",
						opdIndex));
				result.addData("OBS_NUM", reOPD.getData("OBS_NUM", opdIndex));
				result.addData("OBS_DIED_NUM", reOPD.getData("OBS_DIED_NUM",
						opdIndex));
				stationcode = dept.getValue("OE_DEPT_CODE", i); // 门急诊科室记录的是
																// 线上科室的CODE
			} else {
				result.addData("OUTP_NUM", null);
				result.addData("ERD_NUM", null);
				result.addData("ERD_DIED_NUM", null);
				result.addData("OBS_NUM", null);
				result.addData("OBS_DIED_NUM", null);
			}
			// 如果病区表索引存在就将相应的信息填充到Parm中
			if (stationIndex != -1) {
				result.addData("OUYCHK_OI_NUM", reStation.getData(
						"OUYCHK_OI_NUM", stationIndex));
				result.addData("OUYCHK_RAPA_NUM", reStation.getData(
						"OUYCHK_RAPA_NUM", stationIndex));
				result.addData("OUYCHK_INOUT", reStation.getData(
						"OUYCHK_INOUT", stationIndex));
				result.addData("OUYCHK_OPBFAF", reStation.getData(
						"OUYCHK_OPBFAF", stationIndex));
				result.addData("HEAL_LV_I_CASE", reStation.getData(
						"HEAL_LV_I_CASE", stationIndex));
				result.addData("HEAL_LV_BAD", reStation.getData("HEAL_LV_BAD",
						stationIndex));
				result.addData("GET_TIMES", reStation.getData("GET_TIMES",
						stationIndex));
				result.addData("SUCCESS_TIMES", reStation.getData(
						"SUCCESS_TIMES", stationIndex));
				result.addData("BED_RETUEN", reStation.getData("BED_RETUEN",
						stationIndex));
				result.addData("BED_WORK_DAY", reStation.getData(
						"BED_WORK_DAY", stationIndex));
				result.addData("BED_USE_RATE", reStation.getData(
						"BED_USE_RATE", stationIndex));
				result.addData("DATA_01", reStation.getData("DATA_01",
						stationIndex));
				result.addData("DATA_02", reStation.getData("DATA_02",
						stationIndex));
				result.addData("DATA_03", reStation.getData("DATA_03",
						stationIndex));
				result.addData("DATA_04", reStation.getData("DATA_04",
						stationIndex));
				result.addData("DATA_05", reStation.getData("DATA_05",
						stationIndex));
				result.addData("DATA_06", reStation.getData("DATA_06",
						stationIndex));
				result.addData("DATA_06_1", reStation.getData("DATA_06_1",
						stationIndex));

				result.addData("DATA_08", reStation.getData("DATA_08",
						stationIndex));
				result.addData("DATA_08_1", reStation.getData("DATA_08_1",
						stationIndex));
				result.addData("DATA_09", reStation.getData("DATA_09",
						stationIndex));
				result.addData("DATA_10", reStation.getData("DATA_10",
						stationIndex));
				result.addData("DATA_11", reStation.getData("DATA_11",
						stationIndex));
				result.addData("DATA_12", reStation.getData("DATA_12",
						stationIndex));
				result.addData("DATA_13", reStation.getData("DATA_13",
						stationIndex));
				result.addData("DATA_14", reStation.getData("DATA_14",
						stationIndex));
				result.addData("DATA_15", reStation.getData("DATA_15",
						stationIndex));
				result.addData("DATA_15_1", reStation.getData("DATA_15_1",
						stationIndex));

				result.addData("DATA_18", reStation.getData("DATA_18",
						stationIndex));
				result.addData("DATA_19", reStation.getData("DATA_19",
						stationIndex));
				result.addData("DATA_20", reStation.getData("DATA_20",
						stationIndex));
				result.addData("DATA_21", reStation.getData("DATA_21",
						stationIndex));
				result.addData("DATA_22", reStation.getData("DATA_22",
						stationIndex));
				stationcode = dept.getValue("IPD_DEPT_CODE", i); // 住院科室记录的病区CODE是线上的科室CODE
				if (reStation.getValue("CONFIRM_FLG", stationIndex).trim()
						.length() > 0)// 月报不存在提交字段
					isSubmit = reStation.getValue("CONFIRM_FLG", stationIndex);// 记录病区日志是否提交
			} else {
				result.addData("OUYCHK_OI_NUM", null);
				result.addData("OUYCHK_RAPA_NUM", null);
				result.addData("OUYCHK_INOUT", null);
				result.addData("OUYCHK_OPBFAF", null);
				result.addData("HEAL_LV_I_CASE", null);
				result.addData("HEAL_LV_BAD", null);
				result.addData("GET_TIMES", null);
				result.addData("SUCCESS_TIMES", null);
				result.addData("BED_RETUEN", null);
				result.addData("BED_WORK_DAY", null);
				result.addData("BED_USE_RATE", null);
				result.addData("DATA_01", null);
				result.addData("DATA_02", null);
				result.addData("DATA_03", null);
				result.addData("DATA_04", null);
				result.addData("DATA_05", null);
				result.addData("DATA_06", null);
				result.addData("DATA_06_1", null);
				result.addData("DATA_08", null);
				result.addData("DATA_08_1", null);
				result.addData("DATA_09", null);
				result.addData("DATA_10", null);
				result.addData("DATA_11", null);
				result.addData("DATA_12", null);
				result.addData("DATA_13", null);
				result.addData("DATA_14", null);
				result.addData("DATA_15", null);
				result.addData("DATA_15_1", null);
				result.addData("DATA_18", null);
				result.addData("DATA_19", null);
				result.addData("DATA_20", null);
				result.addData("DATA_21", null);
				result.addData("DATA_22", null);
			}
			if (firstIndex != -1) {
				result.addData("DATA_07", reFirstStation.getData("DATA_07",
						firstIndex));
			} else
				result.addData("DATA_07", null);
			if (lastIndex != -1) {
				result.addData("DATA_16", reLastStation.getData("DATA_16",
						lastIndex));
				result.addData("DATA_17", reLastStation.getData("DATA_17",
						lastIndex));
			} else {
				result.addData("DATA_16", null);
				result.addData("DATA_17", null);
			}
			// 如果本部门即不存在门急诊数据 也不存在 住院数据 那么会插入空信息
			if (opdIndex == -1 && stationIndex == -1) {
				// 如果该部门是住院部门 那么 StationCode 存放住院部门的Code
				if (dept.getValue("IPD_DEPT_CODE", i).trim().length() > 0) {
					stationcode = dept.getValue("IPD_DEPT_CODE", i);
				}
				// 如果不是住院部门 那么 StationCode 存放门诊部门的CODE
				else if (dept.getValue("OE_DEPT_CODE", i).trim().length() > 0) {
					stationcode = dept.getValue("OE_DEPT_CODE", i);
				} else {
					stationcode = dept.getValue("DEPT_CODE", i);
				}
			}
			result.addData("DEPT_CODE", deptcode); // 记录中间表部门CODE
			result.addData("STATION_CODE", stationcode); // 记录中间表StationCode
			result.addData("SUBMIT_FLG", isSubmit);// 记录病区日志是否提交
		}
		return result;
	}

	/**
	 * 在中间科室表中查询属于住院科室的CODE
	 * 
	 * @return TParm
	 */
	public TParm selectIPD_DEPT(TParm parm) {
		TParm result = this.query("selectIPD_DEPT", parm);
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询 STA_DAILY_01表中已经提交的部门CODE
	 * 
	 * @param STADATE
	 *            String
	 * @param regionCode
	 *            String
	 * @return TParm ===========pangben modify 20110520 添加区域参数
	 */
	public TParm selectSubmitDept(String STADATE, String regionCode) {
		TParm parm = new TParm();
		parm.setData("STA_DATE", STADATE);
		// ===========pangben modify 20110520 start
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ===========pangben modify 20110520 stop
		TParm result = this.query("selectSubmitDept", parm);
		// 判断错误值
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 审核STA_DAILY_01表中的数据是否已经提交
	 * 
	 * @return TParm ===============pangben modify 20110520 添加区域参数
	 */
	public TParm checkSubmit(String STADATE, String regionCode) {
		// ===============pangben modify 20110520 start
		TParm parm = new TParm();
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ===============pangben modify 20110520 stop
		TParm dept = this.selectIPD_DEPT(parm); // 查询出所有 住院部门
		TParm submit = this.selectSubmitDept(STADATE, regionCode); // 查询STA_DAILY_01表中已提交的部门
		for (int i = 0; i < submit.getCount(); i++) {
			String dept_code = submit.getValue("DEPT_CODE", i);
			for (int j = dept.getCount() - 1; j >= 0; j--) {
				if (dept_code.equals(dept.getValue("DEPT_CODE", j))) {
					dept.removeRow(j);
				}
			}
		}
		return dept;
	}

	/**
	 * 查询某3级科室下的所有下级科室（包括三级科室本身）
	 * 
	 * @param deptList_4
	 *            TParm 三级和四级科室 TParm
	 * @param dept3_code
	 *            String 三级科室CODE
	 * @return TParm
	 */
	private TParm checkChildDept(TParm deptList_4, String dept3_code) {
		TParm result = new TParm();
		// 循环比较 判断四级科室属于哪个三级科室
		for (int i = 0; i < result.getCount(); i++) {
			// 四级科室CODE 截取前五位是三级科室的CODE
			if (deptList_4.getValue("DEPT_CODE", i).substring(0, 5).equals(
					dept3_code)) {
				result
						.addData("DEPT_CODE", deptList_4.getValue("DEPT_CODE",
								i));
			}
		}
		return result;
	}

	/**
	 * 获取某一3级部门（以及其4级部门）的统计数据
	 * 
	 * @param reOPD
	 *            TParm 各部门的门诊数据
	 * @param reStation
	 *            TParm 各部门的住院数据
	 * @param deptcode
	 *            TParm 部门code
	 * @param dept
	 *            TParm 3,4级科室TParm
	 * @return TParm
	 */
	private TParm getDeptData(TParm reOPD, TParm reStation, String deptcode,
			TParm dept) {
		TParm result = new TParm();
		TParm deptList = checkChildDept(dept, deptcode);// 查询某3级科室下的所有下级科室（包括三级科室本身）
		String stationcode = ""; // 病区CODE
		int opdIndex = -1;// 记录门诊统计信息的指定索引
		int stationIndex = -1;// 记录住院统计信息的指定索引
		int OUTP_NUM = -1;
		int ERD_NUM = -1;
		int OBS_NUM = -1;
		int OBS_DIED_NUM = -1;
		int ERD_DIED_NUM = -1;
		int OUYCHK_OI_NUM = -1;
		int OUYCHK_RAPA_NUM = -1;
		int OUYCHK_INOUT = -1;
		int OUYCHK_OPBFAF = -1;
		int HEAL_LV_I_CASE = -1;
		int HEAL_LV_BAD = -1;
		int GET_TIMES = -1;
		int SUCCESS_TIMES = -1;
		int BED_RETUEN = -1;
		int BED_WORK_DAY = -1;
		int BED_USE_RATE = -1;
		int DATA_01 = -1;
		int DATA_02 = -1;
		int DATA_03 = -1;
		int DATA_04 = -1;
		int DATA_05 = -1;
		int DATA_06 = -1;
		int DATA_06_1 = -1;
		int DATA_07 = -1;
		int DATA_08 = -1;
		int DATA_08_1 = -1;
		int DATA_09 = -1;
		int DATA_10 = -1;
		int DATA_11 = -1;
		int DATA_12 = -1;
		int DATA_13 = -1;
		int DATA_14 = -1;
		int DATA_15 = -1;
		int DATA_15_1 = -1;
		int DATA_16 = -1;
		int DATA_17 = -1;
		int DATA_18 = -1;
		int DATA_19 = -1;
		int DATA_20 = -1;
		int DATA_21 = -1;
		int DATA_22 = -1;
		for (int i = 0; i < deptList.getCount(); i++) {
			// 从门急诊中间表中筛选该部门的数据 记录索引
			for (int j = 0; j < reOPD.getCount(); j++) {
				if (deptcode.equals(reOPD.getValue("DEPT_CODE", j))) {
					opdIndex = j;
				}
			}
			// 从病区表中筛选部门的数据 记录索引
			for (int h = 0; h < reStation.getCount(); h++) {
				if (deptcode.equals(reStation.getValue("DEPT_CODE", h))) {
					stationIndex = h;
				}
			}
			// 如果索引存在 就将相应的信息 累加
			if (opdIndex != -1) {
				OUTP_NUM = OUTP_NUM == -1 ? 0 : OUTP_NUM
						+ reOPD.getInt("OUTP_NUM", opdIndex);
				ERD_NUM = ERD_NUM == -1 ? 0 : ERD_NUM
						+ reOPD.getInt("ERD_NUM", opdIndex);
				ERD_DIED_NUM = ERD_DIED_NUM == -1 ? 0 : ERD_DIED_NUM
						+ reOPD.getInt("ERD_DIED_NUM", opdIndex);
				OBS_NUM = OBS_NUM == -1 ? 0 : OBS_NUM
						+ reOPD.getInt("OBS_NUM", opdIndex);
				OBS_DIED_NUM = OBS_DIED_NUM == -1 ? 0 : OBS_DIED_NUM
						+ reOPD.getInt("OBS_DIED_NUM", opdIndex);
				stationcode = dept.getValue("OE_DEPT_CODE", i); // 门急诊科室记录的是
																// 线上科室的CODE
			}
			// 如果病区表索引存在就将相应的信息 累加
			if (stationIndex != -1) {
				OUYCHK_OI_NUM = OUYCHK_OI_NUM == -1 ? 0 : OUYCHK_OI_NUM
						+ reStation.getInt("OUYCHK_OI_NUM", stationIndex);
				OUYCHK_RAPA_NUM = OUYCHK_RAPA_NUM == -1 ? 0 : OUYCHK_RAPA_NUM
						+ reStation.getInt("OUYCHK_RAPA_NUM", stationIndex);
				OUYCHK_INOUT = OUYCHK_INOUT == -1 ? 0 : OUYCHK_INOUT
						+ reStation.getInt("OUYCHK_INOUT", stationIndex);
				OUYCHK_OPBFAF = OUYCHK_OPBFAF == -1 ? 0 : OUYCHK_OPBFAF
						+ reStation.getInt("OUYCHK_OPBFAF", stationIndex);
				HEAL_LV_I_CASE = HEAL_LV_I_CASE == -1 ? 0 : HEAL_LV_I_CASE
						+ reStation.getInt("HEAL_LV_I_CASE", stationIndex);
				HEAL_LV_BAD = HEAL_LV_BAD == -1 ? 0 : HEAL_LV_BAD
						+ reStation.getInt("HEAL_LV_BAD", stationIndex);
				GET_TIMES = GET_TIMES == -1 ? 0 : GET_TIMES
						+ reStation.getInt("GET_TIMES", stationIndex);
				SUCCESS_TIMES = SUCCESS_TIMES == -1 ? 0 : SUCCESS_TIMES
						+ reStation.getInt("SUCCESS_TIMES", stationIndex);
				BED_RETUEN = BED_RETUEN == -1 ? 0 : BED_RETUEN
						+ reStation.getInt("BED_RETUEN", stationIndex);
				BED_WORK_DAY = BED_WORK_DAY == -1 ? 0 : BED_WORK_DAY
						+ reStation.getInt("BED_WORK_DAY", stationIndex);
				BED_USE_RATE = BED_USE_RATE == -1 ? 0 : BED_USE_RATE
						+ reStation.getInt("BED_USE_RATE", stationIndex);
				DATA_01 = DATA_01 == -1 ? 0 : DATA_01
						+ reStation.getInt("DATA_01", stationIndex);
				DATA_02 = DATA_02 == -1 ? 0 : DATA_02
						+ reStation.getInt("DATA_02", stationIndex);
				DATA_03 = DATA_03 == -1 ? 0 : DATA_03
						+ reStation.getInt("DATA_03", stationIndex);
				DATA_04 = DATA_04 == -1 ? 0 : DATA_04
						+ reStation.getInt("DATA_04", stationIndex);
				DATA_05 = DATA_05 == -1 ? 0 : DATA_05
						+ reStation.getInt("DATA_05", stationIndex);
				DATA_06 = DATA_06 == -1 ? 0 : DATA_06
						+ reStation.getInt("DATA_06", stationIndex);
				DATA_06_1 = DATA_06_1 == -1 ? 0 : DATA_06_1
						+ reStation.getInt("DATA_06_1", stationIndex);
				DATA_07 = DATA_07 == -1 ? 0 : DATA_07
						+ reStation.getInt("DATA_07", stationIndex);
				DATA_08 = DATA_08 == -1 ? 0 : DATA_08
						+ reStation.getInt("DATA_08", stationIndex);
				DATA_08_1 = DATA_08_1 == -1 ? 0 : DATA_08_1
						+ reStation.getInt("DATA_08_1", stationIndex);
				DATA_09 = DATA_09 == -1 ? 0 : DATA_09
						+ reStation.getInt("DATA_09", stationIndex);
				DATA_10 = DATA_10 == -1 ? 0 : DATA_10
						+ reStation.getInt("DATA_10", stationIndex);
				DATA_11 = DATA_11 == -1 ? 0 : DATA_11
						+ reStation.getInt("DATA_11", stationIndex);
				DATA_12 = DATA_12 == -1 ? 0 : DATA_12
						+ reStation.getInt("DATA_12", stationIndex);
				DATA_13 = DATA_13 == -1 ? 0 : DATA_13
						+ reStation.getInt("DATA_13", stationIndex);
				DATA_14 = DATA_14 == -1 ? 0 : DATA_14
						+ reStation.getInt("DATA_14", stationIndex);
				DATA_15 = DATA_15 == -1 ? 0 : DATA_15
						+ reStation.getInt("DATA_15", stationIndex);
				DATA_15_1 = DATA_15_1 == -1 ? 0 : DATA_15_1
						+ reStation.getInt("DATA_15_1", stationIndex);
				DATA_16 = DATA_16 == -1 ? 0 : DATA_16
						+ reStation.getInt("DATA_16", stationIndex);
				DATA_17 = DATA_17 == -1 ? 0 : DATA_17
						+ reStation.getInt("DATA_17", stationIndex);
				DATA_18 = DATA_18 == -1 ? 0 : DATA_18
						+ reStation.getInt("DATA_18", stationIndex);
				DATA_19 = DATA_19 == -1 ? 0 : DATA_19
						+ reStation.getInt("DATA_19", stationIndex);
				DATA_20 = DATA_20 == -1 ? 0 : DATA_20
						+ reStation.getInt("DATA_20", stationIndex);
				DATA_21 = DATA_21 == -1 ? 0 : DATA_21
						+ reStation.getInt("DATA_21", stationIndex);
				DATA_22 = DATA_22 == -1 ? 0 : DATA_22
						+ reStation.getInt("DATA_22", stationIndex);
				stationcode = dept.getValue("IPD_DEPT_CODE", i); // 住院科室记录的病区CODE是线上的科室CODE
			}
		}
		result.addData("OUYCHK_OI_NUM", OUYCHK_OI_NUM == -1 ? null
				: OUYCHK_OI_NUM);
		result.addData("OUYCHK_RAPA_NUM", OUYCHK_RAPA_NUM == -1 ? null
				: OUYCHK_RAPA_NUM);
		result
				.addData("OUYCHK_INOUT", OUYCHK_INOUT == -1 ? null
						: OUYCHK_INOUT);
		result.addData("OUYCHK_OPBFAF", OUYCHK_OPBFAF == -1 ? null
				: OUYCHK_OPBFAF);
		result.addData("HEAL_LV_I_CASE", HEAL_LV_I_CASE == -1 ? null
				: HEAL_LV_I_CASE);
		result.addData("HEAL_LV_BAD", HEAL_LV_BAD == -1 ? null : HEAL_LV_BAD);
		result.addData("GET_TIMES", GET_TIMES == -1 ? null : GET_TIMES);
		result.addData("SUCCESS_TIMES", SUCCESS_TIMES == -1 ? null
				: SUCCESS_TIMES);
		result.addData("BED_RETUEN", BED_RETUEN == -1 ? null : BED_RETUEN);
		result
				.addData("BED_WORK_DAY", BED_WORK_DAY == -1 ? null
						: BED_WORK_DAY);
		result
				.addData("BED_USE_RATE", BED_USE_RATE == -1 ? null
						: BED_USE_RATE);
		result.addData("DATA_01", DATA_01 == -1 ? null : DATA_01);
		result.addData("DATA_02", DATA_02 == -1 ? null : DATA_02);
		result.addData("DATA_03", DATA_03 == -1 ? null : DATA_03);
		result.addData("DATA_04", DATA_04 == -1 ? null : DATA_04);
		result.addData("DATA_05", DATA_05 == -1 ? null : DATA_05);
		result.addData("DATA_06", DATA_06 == -1 ? null : DATA_06);
		result.addData("DATA_06_1", DATA_06_1 == -1 ? null : DATA_06_1);
		result.addData("DATA_07", DATA_07 == -1 ? null : DATA_07);
		result.addData("DATA_08", DATA_08 == -1 ? null : DATA_08);
		result.addData("DATA_08_1", DATA_08_1 == -1 ? null : DATA_08_1);
		result.addData("DATA_09", DATA_09 == -1 ? null : DATA_09);
		result.addData("DATA_10", DATA_10 == -1 ? null : DATA_10);
		result.addData("DATA_11", DATA_11 == -1 ? null : DATA_11);
		result.addData("DATA_12", DATA_12 == -1 ? null : DATA_12);
		result.addData("DATA_13", DATA_13 == -1 ? null : DATA_13);
		result.addData("DATA_14", DATA_14 == -1 ? null : DATA_14);
		result.addData("DATA_15", DATA_15 == -1 ? null : DATA_15);
		result.addData("DATA_15_1", DATA_15_1 == -1 ? null : DATA_15_1);
		result.addData("DATA_16", DATA_16 == -1 ? null : DATA_16);
		result.addData("DATA_17", DATA_17 == -1 ? null : DATA_17);
		result.addData("DATA_18", DATA_18 == -1 ? null : DATA_18);
		result.addData("DATA_19", DATA_19 == -1 ? null : DATA_19);
		result.addData("DATA_20", DATA_20 == -1 ? null : DATA_20);
		result.addData("DATA_21", DATA_21 == -1 ? null : DATA_21);
		result.addData("DATA_22", DATA_22 == -1 ? null : DATA_22);
		result.addData("DEPT_CODE", deptcode); // 记录中间表部门CODE
		result.addData("STATION_CODE", stationcode); // 记录中间表StationCode
		return result;
	}

	/**
	 * 查询所有1，2，3级部门列表
	 * 
	 * @return TParm
	 */
	public TParm selectDeptList() {
		TParm result = this.query("selectDeptList");
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询STA_DAILY_02某一天的数据
	 * 
	 * @param STADATE
	 *            String 数据日期
	 * @param regionCode
	 *            String 区域
	 * @return TParm ============pangben modify 20110519 添加区域参数
	 */
	public TParm selectSTA_DAILY_02(String STADATE, String regionCode) {
		TParm parm = new TParm();
		parm.setData("STA_DATE", STADATE);
		// ============pangben modify 20110519 start
		if (null != regionCode && regionCode.length() > 0)
			parm.setData("REGION_CODE", regionCode);
		// ============pangben modify 20110519 stop
		TParm result = this.query("selectSTA_DAILY_02", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 检核传入的部门CODE是不是住院科室（病区） ture:是住院科室 false:不是住院科室
	 * 
	 * @param dept_code
	 *            String
	 * @return boolean
	 */
	public boolean checkIPDDept(String dept_code) {
		TParm parm = new TParm();
		parm.setData("DEPT_CODE", dept_code);
		TParm result = this.query("checkIPDDept", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return false;
		}
		// 如果IPD_DEPT_CODE 住院科室代码不存在 返回 false
		if (result.getValue("IPD_DEPT_CODE", 0).trim().length() <= 0)
			return false;
		else
			return true;
	}

	/**
	 * 计算 病床周转（次）
	 * 
	 * @param outNum
	 *            int 期内出院人数
	 * @param openBedNum
	 *            int 同期平均开放病床数
	 * @return double
	 */
	public double countBedReturn(int outNum, int openBedNum) {
		DecimalFormat df = new DecimalFormat("0.00"); // 设定Double类型格式
		// 公式：病床周转(次) = 期内出院人数/ 同期平均开放病床数
		double bedReturn = 0;
		if (openBedNum != 0) {
			bedReturn = (double) outNum / (double) openBedNum;
		}
		return bedReturn;
	}

	/**
	 * 计算 病床使用率
	 * 
	 * @param real_bed_day_num
	 *            int
	 * @param daysOfMonth
	 *            int
	 * @param END_BED_NUM
	 *            int
	 * @return double
	 */
	public double countBED_USE_RATE(int real_bed_day_num, int daysOfMonth,
			int END_BED_NUM) {
		// 公式：病床使用率 = 实际占用总床日数 / 月天数 / 编制床位数（期末实有病床数）
		double BED_USE_RATE = 0;
		if (daysOfMonth != 0 && END_BED_NUM != 0) {
			BED_USE_RATE = (double) real_bed_day_num / (double) daysOfMonth
					/ (double) END_BED_NUM;
		}
		return BED_USE_RATE;
	}

	/**
	 * 查询月报表期初值（月第一天值） 查询期初实有病人数
	 * 
	 * @param parm
	 *            TParm
	 * @return double
	 */
	public TParm selectStationFirstDay(TParm parm) {
		String STA_DATE = parm.getValue("STA_DATE");
		String StartDate = STA_DATE + "01";// 每月第一天
		TParm p = new TParm();
		p.setData("STA_DATE", StartDate);
		// ============pangben modify 20110520 start
		if (null != parm.getValue("REGION_CODE")
				&& parm.getValue("REGION_CODE").length() > 0)
			p.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		// ============pangben modify 20110520 stop
		TParm result = this.query("selectStationFirstDay", p);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 *查询月报表期末值（月最后一天值）
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectStationLastDay(TParm parm) {
		String STA_DATE = parm.getValue("STA_DATE");
		String StartDate = STA_DATE + "01";// 每月第一天
		// 获取此月份的最后一天
		String EndDate = StringTool.getString(STATool.getInstance()
				.getLastDayOfMonth(STA_DATE), "yyyyMMdd");
		TParm p = new TParm();
		p.setData("DATE_S", StartDate);
		p.setData("DATE_E", EndDate);
		// ============pangben modify 20110520 start
		if (null != parm.getValue("REGION_CODE")
				&& parm.getValue("REGION_CODE").length() > 0)
			p.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		// ============pangben modify 20110520 stop

		TParm result = this.query("selectStationLastDay", p);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
}
