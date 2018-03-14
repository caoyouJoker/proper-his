package com.javahis.ui.udd;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jdo.adm.ADMInpTool;
import jdo.odi.OdiObject;
import jdo.opd.TotQtyTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import jdo.udd.UddDispatchTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 住院药房长期摆药
 * </p>
 * 
 * <p>
 * Description: 住院药房长期摆药
 * </p>
 * 
 * <p>
 * Copyright: javahis 20090311
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ehui
 * @version 1.0
 */

public class UddDistributeBatch extends TControl {

	// 字符串常量
	private static final String Y = "Y";
	// 字符串常量
	private static final String N = "N";
	// luhai modify 2012-04-26 未审核人数去除自备药的数据
	// 未审核人数SQL
	private static final String TBL_PAT_SQL = " SELECT COUNT(TABLE1.DISPENSE) AS DIS "
			+ "	FROM (SELECT DISTINCT A.CASE_NO AS DISPENSE FROM ODI_ORDER A, ADM_INP B "
			+ "	WHERE  A.CASE_NO=B.CASE_NO "
			+ "	AND B.CANCEL_FLG='N' AND B.DS_DATE IS NULL AND A.RX_KIND = 'UD' ";
//			+ "	AND A.DISPENSE_FLG <> 'Y' "; // shibl 20121015 modify
										    // 修改连接ADM_INP 去掉床号的限制
	// 在床人数SQL
	private static final String TBL_BED_SQL = "SELECT COUNT(CASE_NO) AS DIS "
			+ "	FROM SYS_BED B"
			+ "	WHERE (B.ALLO_FLG IS NOT NULL AND B.ALLO_FLG='Y') "
			+ "	AND (B.BED_OCCU_FLG IS  NULL OR B.BED_OCCU_FLG='N')"
			+ "	AND  B.STATION_CODE=";
	// 摆药人数SQL
	private static final String TBL_GIVING_SQL = " SELECT COUNT(TABLE1.CASE_NO) AS DIS "
			+ " FROM ( SELECT  DISTINCT A.CASE_NO  "
			+ "  FROM ODI_ORDER A, ADM_INP B"
			+ "	WHERE  A.CASE_NO=B.CASE_NO "
			+ "	AND B.CANCEL_FLG='N' AND B.DS_DATE IS NULL AND A.RX_KIND = 'UD' "
//			+ "	AND DISPENSE_FLG <> 'Y' "
			+ "         AND A.STATION_CODE = '#'"
			+ "         AND (A.ORDER_CAT1_CODE = 'PHA_C'"
			+ "         OR   A.ORDER_CAT1_CODE = 'PHA_W')"
			+ "         AND (A.DC_DATE IS NULL OR A.DC_DATE >= #)"
			+ "		  AND A.EXEC_DEPT_CODE='#'";
	// 总人数SQL
	private static final String TBL_PAT_STAT_SQL = "SELECT COUNT(CASE_NO) AS DIS "
			+ "	FROM ADM_INP B"
			+ "	WHERE B.CANCEL_FLG='N' AND B.DS_DATE IS NULL "
			+ "	AND  B.STATION_CODE=";
	// 连,30,boolean;组号,40;药品代码,80;药品名称,200;用量,60,int;开药单位,100;频率,100;用法,100;启用时间,120;停用时间,120;医生备注,200;开立医生,100
	private static final String TBL_DTL = "SELECT LINKMAIN_FLG,LINK_NO,ORDER_DESC,MEDI_QTY,MEDI_UNIT,"
			+ "		FREQ_CODE,ROUTE_CODE,EFF_DATE,(CASE WHEN TO_CHAR(sysdate,'YYYY/MM/DD') >= TO_CHAR(DC_DATE,'YYYY/MM/DD') THEN TO_CHAR(DC_DATE,'YYYY/MM/DD HH24:MI') ELSE '' END) AS DC_DATE,DR_NOTE,"
			+ "		ORDER_DR_CODE,USER_NAME "
			+ "	FROM ODI_ORDER A,SYS_OPERATOR B "
			+ "	WHERE B.USER_ID=A.ORDER_DR_CODE AND ";
	private static final String TBL_DIS_SQL_ORDER_GROUP = " GROUP BY A.CASE_NO, A.STATION_CODE,A.BED_NO,A.MR_NO,A.IPD_NO,B.PAT_NAME  ORDER BY A.STATION_CODE,A.CASE_NO ";
	// 查询ODI_ORDER表
	private static final String OTDS_SQL = "SELECT * FROM ODI_ORDER";
	// 查询ODI_DSPNM表
	private static final String MTDS_SQL = "SELECT * FROM ODI_DSPNM";
	// 查询ODI_DSPND表
	private static final String DTDS_SQL = "SELECT * FROM ODI_DSPND";
	// 控件
	private TTable tblStation;
	private TTable tblDispense;
	private TTable tblDtl;
	// TABLE用的数据
	private TParm tblstationParm;
	private TParm tblDispenseParm;
	private TParm tblDtlParm;
	// 每次查询取得的按病区下没有未审核药品的CASE_NO
	private TParm caseNoParm;
	// 是否需要护士审核，是否需要药房审核常量
	private boolean isNsCheck;
	private boolean isPhaCheck;
	// 摆药时间，开始时间，摆药区间
	private String dspnTime;
	private String startTime;
	private String arrangeDays;
	// 摆药起讫时间
	private Timestamp schDateFrom;
	private Timestamp schDateTo;
	// 住院主对象
	private OdiObject odiObject;
	// 所有药房列表
	private Vector orgStation;
	// 医生站系统数据,病区数据,要保存的数据,病区查询数据
	private TParm sysparm;
	private TParm stationParm;
	private TParm saveParm;
	private TParm stationQueryParm;
	// 总量计算接口
	private TotQtyTool qtyTool;
	// 当前时间
	private Timestamp now;
	// 上次摆药时间
	private List lastDspnParm;

	public UddDistributeBatch() {
		odiObject = new OdiObject();
		orgStation = new Vector();
		qtyTool = new TotQtyTool();
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		tblStation = (TTable) getComponent("TBL_STATION");
		tblDispense = (TTable) getComponent("TBL_DISPENSE");
		tblDtl = (TTable) getComponent("TBL_DTL");
		tblstationParm = new TParm();
		tblDispenseParm = new TParm();
		tblDtlParm = new TParm();
		callFunction("UI|TBL_DISPENSE|addEventListener", new Object[] {
				"table.checkBoxClicked", this, "onTableCheckBoxClicked" });
		gainSysParm();
		setstationQueryParm();
		onClear();
	}

	public void onTableCheckBoxClicked(Object obj) {
		tblDispense.acceptText();
	}

	/**
	 * 获取系统参数
	 */
	public void gainSysParm() {
		sysparm = new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM ODI_SYSPARM"));
		isNsCheck = sysparm.getBoolean("NS_CHECK_FLG", 0);
		// ===========pangben modify 20110512 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " WHERE REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ===========pangben modify 20110512 stop
		stationParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM SYS_STATION " + region));
		String date = (new StringBuilder())
				.append(StringTool.getString(TJDODBTool.getInstance()
						.getDBTime(), "yyyyMMdd")).append("000000").toString();
		dspnTime = sysparm.getValue("DSPN_TIME", 0);
		List parm = TotQtyTool.getInstance().getDispenseDttmArrange(
				date.substring(0, 12));
		lastDspnParm = TotQtyTool.getInstance().getLastDspnDttm(
				date.substring(0, 12));
		if (StringUtil.isNullString(parm.get(0) + "")
				|| StringUtil.isNullString(parm.get(1) + "")) {
			this.messageBox_("取得配药日期失败,请在药房参数单档里选定配药日期");
			return;
		}
		schDateFrom = StringTool.getTimestamp(
				TCM_Transform.getString(parm.get(0)), "yyyyMMddHHmm");
		schDateTo = StringTool.getTimestamp(
				TCM_Transform.getString(parm.get(1)), "yyyyMMddHHmm");
		if (schDateFrom == null || schDateTo == null) {
			this.messageBox_("取得配药日期失败");
			this.closeWindow();
		}

		arrangeDays = TCM_Transform.getString(Integer.valueOf(StringTool
				.getDateDiffer(schDateTo, schDateFrom)));
		dspnTime = sysparm.getValue("DSPN_TIME", 0);
		startTime = sysparm.getValue("START_TIME", 0);
		// onStationClick();
		if (StringUtil.isNullString(dspnTime)
				|| StringUtil.isNullString(startTime)) {
			this.messageBox_("取得配药日期失败,请在药房参数单档里选定配药日期");
			return;
		}
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		setValue("EXEC_DEPT_CODE", Operator.getDept());
		setValue("OPT_USER", Operator.getName());
		setValue("DAYS", String.valueOf(arrangeDays));
		setValue("SCH_DATE", schDateFrom);
		setValue("START_DATE", schDateFrom);
		setValue("END_DATE", schDateTo);
		this.setValue("DSPN_TIME",
				StringTool.getTimestamp("20000101" + dspnTime, "yyyyMMddHHmm"));
		this.setValue("START_TIME",
				StringTool.getTimestamp("20000101" + startTime, "yyyyMMddHHmm"));
		setValue("STATION", "");
		setValue("IPD_NO", "");
		setValue("BED_NO", "");
		setValue("MR_NO", "");
		setValue("NAME", "");
		setValue("TOTNO", "");
		tblStation.removeRowAll();
		tblDispense.removeRowAll();
		tblDtl.removeRowAll();
		// ===========pangben modify 20110512 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " WHERE REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ===========pangben modify 20110512 stop
		stationParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM SYS_STATION " + region));
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		onQueryPat();
		onQueryDispense();
	}

	/**
	 * 贴纸打印
	 */
	public void onPrint() {
		this.tblStation.acceptText();
		TParm parm = tblStation.getParmValue();
		int count = parm.getCount("STATION_CODE");
		Map map = UddDispatchTool.getInstance().getIntgMedNoMap();
		for (int i = 0; i < count; i++) {
			TParm printParm = new TParm();
			String stationCode = parm.getValue("STATION_CODE", i);
			String stationDesc = StringUtil.getDesc("SYS_STATION",
					"STATION_DESC", "STATION_CODE='" + stationCode + "'");
			printParm.setData("STATION_CODE","TEXT", stationDesc);
			String medNo = String.valueOf(map.get(stationCode));
			printParm.setData("APPLICATION_NO","TEXT", medNo);
			this.openPrintDialog("%ROOT%\\config\\prt\\UDD\\UddBarPrint.jhw",
					printParm);
		}
	}

	/**
	 * 以病区为单位查询
	 */
	public void onQueryPat() {
		tblStation.removeRowAll();
		tblDispense.removeRowAll();
		if (tblDtl != null) {
			tblDtl.removeRowAll();
		}
		String stationCode = getValueString("STATION");
		String orgCode = getValueString("EXEC_DEPT_CODE");
		StringBuffer sqlAppender = new StringBuffer();
		int tot = 0;
		int count = stationQueryParm.getCount("STATION_CODE");
		String startDate = "TO_DATE('"
				+ StringTool.getString(schDateFrom, "yyyyMMdd") + startTime
				+ "','YYYYMMDDHH24MI')";
		String sql = "";
		// ===========pangben modify 20110512 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND B.REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ===========pangben modify 20110512 stop
		for (int i = 0; i < count; i++) {
			stationCode = stationQueryParm.getValue("STATION_CODE", i);
			sqlAppender = new StringBuffer();
			// 未审核人数SQL
			sql = sqlAppender
					.append(TBL_PAT_SQL)
					.append(region)
					.append("  AND A.STATION_CODE='"
							+ stationCode
							+ "' AND A.PHA_CHECK_CODE IS NULL AND A.CAT1_TYPE='PHA' "
							+ " AND A.PHA_TYPE <>'G' AND A.EXEC_DEPT_CODE='"
							+ orgCode + "' AND A.NS_CHECK_CODE IS NOT NULL "
							+ "GROUP BY A.CASE_NO ) TABLE1").toString();
			// System.out.println("sql1111:" + sql);
			TParm parmUnCheck = new TParm(TJDODBTool.getInstance().select(sql));
			// 总人数SQL
			sqlAppender = new StringBuffer();
			sql = sqlAppender.append(TBL_PAT_STAT_SQL).append("'")
					.append(stationCode + "'").append(region).toString();
			// System.out.println("sql22222:" + sql);
			TParm parmTot = new TParm(TJDODBTool.getInstance().select(sql));
			tot += parmTot.getInt("DIS", 0);
			// 在床人数SQL
			sqlAppender = new StringBuffer();
			sql = sqlAppender.append(this.TBL_BED_SQL).append("'")
					.append(stationCode + "'").append(region).toString();
			// System.out.println("sql44444:" + sql);
			TParm parmBed = new TParm(TJDODBTool.getInstance().select(sql));
			// 摆药人数SQL
			sql = this.TBL_GIVING_SQL.replaceFirst("#", stationCode)
					.replaceFirst("#", startDate)
					+ " AND A.LAST_DSPN_DATE=TO_DATE('"
					+ this.lastDspnParm.get(1)
					+ "','YYYYMMDDHH24MI') AND A.NS_CHECK_DATE<TO_DATE('"
					+ this.lastDspnParm.get(1)
					+ "','YYYYMMDDHH24MI')";
//					+ " AND A.DISPENSE_FLG = 'N' ";
			sql = sql.replaceFirst("#", orgCode) + region + " )TABLE1";
			// System.out.println("sql333333:" + sql);
			TParm parmGiving = new TParm(TJDODBTool.getInstance().select(sql));
			tblstationParm.addData("EXEC", "Y");
			tblstationParm.addData("STATION_DESC",
					stationQueryParm.getValue("STATION_DESC", i));
			tblstationParm
					.addData("DISPENSE", parmGiving.getInt("DIS", 0) + "");
			tblstationParm.addData("TOT_UNCHECK",
					parmUnCheck.getValue("DIS", 0));
			tblstationParm.addData("TOT_NO", parmTot.getValue("DIS", 0));
			tblstationParm.addData("TOT_BED", parmBed.getValue("DIS", 0));
			tblstationParm.addData("STATION_CODE", stationCode);
		}
		tblstationParm.setData("ACTION_COUNT",
				stationParm.getCount("STATION_CODE"));
		tblStation.setParmValue(tblstationParm);
		setValue("TOTNO", tot + "");
	}

	/**
	 * 取得病区CODE，为拼SQL提供参数
	 * 
	 * @return String
	 */
	public String getStationCode() {
		StringBuffer station = new StringBuffer();
		String stationCode = getValueString("STATION");
		if (!StringUtil.isNullString(stationCode))
			return "'" + stationCode + "'";
		if (stationQueryParm.getCount() < 1)
			return "''";
		int count = stationQueryParm.getCount("STATION_CODE");
		for (int i = 0; i < count; i++) {
			station.append("'" + stationQueryParm.getValue("STATION_CODE", i)
					+ "',");
		}
		if (station.length() > 1)
			station.deleteCharAt(station.length() - 1);
		return station.toString();
	}

	/**
	 * 查询配药病人的信息
	 */
	public void onQueryDispense() {
		tblDispense.removeRowAll();
		tblDtl.removeRowAll();
		String station = getStationCode();
		String orgCode = getValueString("EXEC_DEPT_CODE");
		if (StringUtil.isNullString(station)) {
			messageBox_("查无数据");
			return;
		} else {
			String startDate = "TO_DATE('"
					+ StringTool.getString(schDateFrom, "yyyyMMdd") + startTime
					+ "','YYYYMMDDHH24MI')";
			// caseNoParm = UddDispatchTool.getInstance().getCaseNoParm(
			// stationQueryParm, orgCode, startDate);
			TParm parm = UddDispatchTool.getInstance().getDispensePat(
					stationQueryParm, orgCode, startDate);
			tblDispense.setParmValue(parm);
			this.setValue("PAT_NUM", parm.getCount("CASE_NO") + "");
			return;
		}
	}

	/**
	 * 查询某个病人的药品详细
	 */
	public void onQueryDtl() {
		TParm parmTable = tblDispense.getParmValue();
		StringBuffer sqlAppender = new StringBuffer(TBL_DTL);
		if (parmTable == null || parmTable.getCount("CASE_NO") < 1) {
			messageBox_("取得数据失败");
			return;
		}
		String startDate = "TO_DATE('"
			+ StringTool.getString(schDateFrom, "yyyyMMdd") + startTime
			+ "','YYYYMMDDHH24MI')";
		String fromDate = StringTool.getString(schDateFrom, "yyyyMMdd")
				+ dspnTime;
		String endDate = StringTool.getString(schDateTo, "yyyyMMdd")
				+ startTime;
		tblDtl.removeRowAll();
		int row = tblDispense.getSelectedRow();
		String date = StringTool.getString(schDateFrom, "yyyyMMdd") + dspnTime;
		Timestamp dspnDate = StringTool.getTimestamp(date, "yyyyMMddHHmm");
		String caseNo = parmTable.getValue("CASE_NO", row);
		Timestamp lastDspnTime = parmTable.getTimestamp("PHA_DOSAGE_DATE", row);
		String phaCheckDate = parmTable.getValue("PHA_CHECK_DATE", row);
		sqlAppender.append("CASE_NO='").append(caseNo).append("' ");
		if (StringUtil.isNullString(phaCheckDate)) {
			sqlAppender.append(" AND PHA_CHECK_DATE IS NULL ");
		} else {
			sqlAppender
					.append(" AND  A.NS_CHECK_DATE<=TO_DATE('")
					.append(fromDate)
					.append("','YYYYMMDDHH24MISS')")
					.append(" AND  A.EFF_DATE<=TO_DATE('")
					.append(endDate)
					.append("','YYYYMMDDHH24MISS')  AND PHA_CHECK_DATE IS NOT NULL AND (ORDER_CAT1_CODE='PHA_W' OR ORDER_CAT1_CODE='PHA_C')"
									+ " AND (A.DC_DATE IS NULL OR A.DC_DATE >=TO_DATE('"
									+ fromDate + "','YYYYMMDDHH24MISS')) AND A.RX_KIND='UD'");

		}
		// 备药不发药 begin luhai 2012-03-06
//		sqlAppender.append(" AND A.DISPENSE_FLG='N' ");
		// 备药不发药 begin luhai 2012-03-06
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				sqlAppender.toString()));
		tblDtl.setParmValue(parm);
	}

	/**
	 * 拼装WHERE条件
	 * 
	 * @return String
	 */
	public String getWhere() {
		StringBuffer result = new StringBuffer();
		if (!StringUtil.isNullString(getValueString("IPD_NO"))) {
			result.append(" AND IPD_NO='" + this.getValueString("IPD_NO")
					+ "' ");
			return result.toString();
		}
		if (!StringUtil.isNullString(getValueString("BED_NO"))) {
			result.append(" AND BED_NO='" + this.getValueString("BED_NO")
					+ "' ");
			return result.toString();
		}
		if (!StringUtil.isNullString(getValueString("MR_NO"))) {
			String mrNo = StringTool.fill0(getValueString("MR_NO"), PatTool
					.getInstance().getMrNoLength());
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					"SELECT A.CASE_NO,B.PAT_NAME FROM ADM_INP A,SYS_PATINFO B WHERE A.MR_NO='"
							+ mrNo
							+ "' AND A.DS_DATE IS NULL AND B.MR_NO(+)=A.MR_NO"));
			String caseNo = parm.getValue("CASE_NO", 0);
			this.setValue("NAME", parm.getValue("PAT_NAME", 0));
			result.append(" AND CASE_NO='" + caseNo + "'");
		}
		if (!StringUtil.isNullString(getValueString("STATION"))) {
			result.append(" AND STATION_CODE='"
					+ this.getValueString("STATION") + "' ");
			return result.toString();
		} else {
			result.append("GROUP BY CASE_NO");
			return result.toString();
		}
	}

	/**
	 * 保存
	 */
	public void onSave() {
		int countDispense = 0;
		// luhai modify 2012-04-18 begin
		for (int i = 0; i < tblStation.getParmValue().getCount("TOT_UNCHECK"); i++) {
			if (tblStation.getParmValue().getInt("TOT_UNCHECK", i) > 0
					&& "Y".equals(tblStation.getParmValue().getValue("EXEC", i))) {
				this.messageBox(tblStation.getParmValue().getValue(
						"STATION_DESC", i)
						+ "存在长期医嘱未审核！");
				return;
			}
		}
		// luhai modify 2012-04-18 end
		if (tblDispense.getParmValue().getCount("CASE_NO") == 0) {
			messageBox("没有摆药数据");
			return;
		}
		TParm parm = new TParm();
		for (int i = 0; i < tblDispense.getParmValue().getCount("CASE_NO"); i++)
			if (!"N".equals(tblDispense.getParmValue()
					.getValue("SELECT_FLG", i))) {
				countDispense++;
				parm.addData("CASE_NO",
						tblDispense.getParmValue().getValue("CASE_NO", i));
				parm.addData("STATION_CODE", tblDispense.getParmValue()
						.getValue("STATION_CODE", i));
				// 是否最后一次摆药时间大于等于今天，若是弹出提示
				String lastPHADate = tblDispense.getParmValue().getValue(
						"PHA_DOSAGE_DATE", i);
				if (lastPHADate.length() > 8) {
					lastPHADate = lastPHADate.replace("-", "").substring(0, 8);
				} else {
					lastPHADate = "0";
				}
				String today = StringTool.getString(SystemTool.getInstance()
						.getDate(), "yyyyMMdd");
				// if(Integer.parseInt(lastPHADate)>=Integer.parseInt(today)){
				// String
				// message="病患："+tblDispense.getParmValue().getValue("PAT_NAME",i)+" 今天已经摆药！";
				// int value =
				// messageBox("提示信息",message+"是否继续?",YES_NO_CANCEL_OPTION);
				// if(value != 0)
				// {
				// return;
				// }
				// }
			}

		parm.setCount(countDispense);
		if (countDispense == 0) {
			messageBox("没有摆药数据");
			return;
		}
		now = TJDODBTool.getInstance().getDBTime();
		StringBuffer saveSql = new StringBuffer();
		String today = StringTool.getString(now, "yyyyMMddHHmmss");
		String fromDate = StringTool.getString(schDateFrom, "yyyyMMdd")
				+ dspnTime;
		String endDate = StringTool.getString(schDateTo, "yyyyMMdd")
				+ startTime;
		String effDate = today.substring(0, 8) + dspnTime;
		if (Integer.parseInt(today.substring(8, 12)) < Integer
				.parseInt(startTime)) {
			messageBox("未到摆药时间");
			return;
		}
		String station = getStationCode();
		TParm inParm = new TParm();
		inParm.setData("CASE_NO", parm.getData());
		inParm.setData("TODAY", today);
		inParm.setData("FROM_DATE", fromDate);
		inParm.setData("END_DATE", endDate);
		inParm.setData("EFF_DATE", effDate);
		inParm.setData("ID", Operator.getID());
		inParm.setData("IP", Operator.getIP());
		TParm mapParm = new TParm();
		mapParm.setData("IN_MAP", inParm.getData());
		TParm failedCase = TIOM_AppServer.executeAction(
				"action.udd.UddDispatchAction", "saveDispatch", mapParm);
		int count = failedCase.getCount("CASE_NO");
		String dir = (new StringBuilder())
				.append(TConfig.getSystemValue("UDD_DISBATCH_LocalPath"))
				.append("\\").toString();
		if (count > 0) {
			this.messageBox_("摆药失败，还有 " + count + " 人失败,详见前台" + dir + "Log.txt");
		} else {
			messageBox_("摆药成功");
		}
		writeLocalFile(failedCase, dir);
		onClear();
	}

	/**
	 * 护士站点选事件
	 */
	public void onStationClick() {
		setstationQueryParm();
		onQuery();
	}

	private void setstationQueryParm() {
		String stationCode = getValueString("STATION");
		String stationDesc = StringUtil.getDesc("SYS_STATION", "STATION_DESC",
				"STATION_CODE='" + stationCode + "'");
		if (StringUtil.isNullString(stationCode)) {
			stationQueryParm = stationParm;
		} else {
			stationQueryParm = new TParm();
			stationQueryParm.addData("STATION_CODE", stationCode);
			stationQueryParm.addData("STATION_DESC", stationDesc);
			// =========pangben modify 20110513 start
			stationQueryParm.addData("REGION_CODE", Operator.getRegion());
			// ========pangben modify 20110513 stop
			stationQueryParm
					.setCount(stationQueryParm.getCount("STATION_CODE"));
		}
	}

	public TParm copyParm(TParm source) {
		TParm result = new TParm();
		int count = source.getCount();
		Vector namesVect = (Vector) source.getData("SYSTEM", "COLUMNS");
		if (StringUtil.isNullList(namesVect))
			return result;
		int name = namesVect.size();
		for (int j = 0; j < name; j++)
			result.setData((new StringBuilder()).append(namesVect.get(j))
					.append("").toString(), new Vector());

		for (int j = 0; j < name; j++) {
			for (int i = 0; i < count; i++)
				result.addData(
						(new StringBuilder()).append(namesVect.get(j))
								.append("").toString(),
						source.getData(
								(new StringBuilder()).append(namesVect.get(j))
										.append("").toString(), i));

		}

		return result;
	}

	/**
	 * 住院号查询事件
	 */
	public void onIpdNo() {
		clearTbl();
		TParm inParm = new TParm();
		inParm.setData("IPD_NO", getValue("IPD_NO"));
		inParm.setData("CANCEL_FLG", "N"); // shibl 20120928 add 查询在院病人
		inParm.setData("DS_DATE", "Y");
		TParm parm = ADMInpTool.getInstance().selectall(inParm);
		setValue("STATION", parm.getValue("STATION_CODE", 0));
		TParm caseNoParm = new TParm();
		caseNoParm.addData("CASE_NO", parm.getValue("CASE_NO", 0));
		onCheckorder(caseNoParm);
		onQueryDtlByCaseNo(caseNoParm);
	}

	/**
	 * 
	 * @param parm
	 */
	public void onCheckorder(TParm parm) {
		tblstationParm = new TParm();
		tblStation.removeRowAll();
		String stationcode = getValueString("STATION");
		String stationDesc = StringUtil.getDesc("SYS_STATION", "STATION_DESC",
				"STATION_CODE='" + stationcode + "'");
		String orgCode = getValueString("EXEC_DEPT_CODE");
		StringBuffer sqlAppender = new StringBuffer();
		int tot = 0;
		String startDate = "TO_DATE('"
				+ StringTool.getString(schDateFrom, "yyyyMMdd") + startTime
				+ "','YYYYMMDDHH24MI')";
		String sql = "";
		// ===========pangben modify 20110512 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND B.REGION_CODE='" + Operator.getRegion() + "' ";
		}
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			sqlAppender = new StringBuffer();
			// 未审核人数SQL
			sql = sqlAppender
					.append(TBL_PAT_SQL)
					.append(region)
					.append(" AND A.CASE_NO='" + parm.getValue("CASE_NO", i))
					.append("' AND A.STATION_CODE='"
							+ stationcode
							+ "' AND A.PHA_CHECK_CODE IS NULL AND A.CAT1_TYPE='PHA' "
							+ " AND A.PHA_TYPE <>'G' AND A.EXEC_DEPT_CODE='"
							+ orgCode + "' AND A.NS_CHECK_CODE IS NOT NULL "
							+ "GROUP BY A.CASE_NO ) TABLE1").toString();
			// System.out.println("未审核人数SQL---------"+sql);
			TParm parmUnCheck = new TParm(TJDODBTool.getInstance().select(sql));
			// 总人数SQL
			sqlAppender = new StringBuffer();
			sql = sqlAppender
					.append(TBL_PAT_STAT_SQL)
					.append("'")
					.append(stationcode + "' AND B.CASE_NO='"
							+ parm.getValue("CASE_NO", i) + "'").append(region)
					.toString();
			// System.out.println("总人数SQL---------"+sql);
			TParm parmTot = new TParm(TJDODBTool.getInstance().select(sql));
			// 在床人数SQL
			sqlAppender = new StringBuffer();
			sql = sqlAppender
					.append(this.TBL_BED_SQL)
					.append("'")
					.append(stationcode + "' AND B.CASE_NO='"
							+ parm.getValue("CASE_NO", i) + "'").append(region)
					.toString();
			// System.out.println("sql44444:" + sql);
			TParm parmBed = new TParm(TJDODBTool.getInstance().select(sql));
			tot += parmTot.getInt("DIS", 0);
			// 摆药人数SQL
			sql = this.TBL_GIVING_SQL.replaceFirst("#", stationcode)
					.replaceFirst("#", startDate)
					+ " AND A.CASE_NO='"
					+ parm.getValue("CASE_NO", i)
					+ "'"
					+ " AND A.LAST_DSPN_DATE=TO_DATE('"
					+ this.lastDspnParm.get(1)
					+ "','YYYYMMDDHH24MI') ";
//					+ "AND A.DISPENSE_FLG = 'N' ";
			sql = sql.replaceFirst("#", orgCode) + region + " )TABLE1";
			// System.out.println("摆药人数SQL--------"+sql);
			TParm parmGiving = new TParm(TJDODBTool.getInstance().select(sql));
			tblstationParm.addData("EXEC", "Y");
			tblstationParm.addData("STATION_DESC", stationDesc);
			tblstationParm
					.addData("DISPENSE", parmGiving.getInt("DIS", 0) + "");
			tblstationParm.addData("TOT_UNCHECK",
					parmUnCheck.getValue("DIS", 0));
			tblstationParm.addData("TOT_NO", parmBed.getValue("DIS", 0));
			tblstationParm.addData("TOT_BED", parmTot.getValue("DIS", 0));
		}
		tblstationParm.setData("ACTION_COUNT", parm.getCount("CASE_NO"));
		tblStation.setParmValue(tblstationParm);
		setValue("TOTNO", tot + "");
	}

	/**
	 * 病案号查询事件
	 */
	public void onMrNo() {
		Pat pat = new Pat();
		pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
		if (pat == null || "".equals(pat.getMrNo())) {
			messageBox("没有该病患信息");
			return;
		} else {
//			String mr_no = pat.getMrNo();
//			setValue("MR_NO", mr_no);
			
			 // modify by huangtt 20160928 EMPI患者查重提示 start
			String mrNo = PatTool.getInstance().checkMrno(TCM_Transform.getString(getValue("MR_NO")));
			this.setValue("MR_NO", mrNo);
			
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
				setValue("MR_NO", pat.getMrNo());
				mrNo =  pat.getMrNo();
			}
			// modify by huangtt 20160928 EMPI患者查重提示 end
			
			
			clearTbl();
			TParm inParm = new TParm();
			inParm.setData("MR_NO", mrNo);
			inParm.setData("CANCEL_FLG", "N");
			inParm.setData("DS_DATE", "Y");// shibl 20120928 add 查询在院病人
			TParm parm = ADMInpTool.getInstance().selectall(inParm);
			setValue("STATION", parm.getValue("STATION_CODE", 0));
			TParm caseNoParm = new TParm();
			caseNoParm.addData("CASE_NO", parm.getValue("CASE_NO", 0));
			onCheckorder(caseNoParm);
			onQueryDtlByCaseNo(caseNoParm);
			return;
		}
	}

	/**
	 * 床号查询事件
	 */
	public void onBedNo() {
		clearTbl();
		TParm inParm = new TParm();
		inParm.setData("BED_NO", getValue("BED_NO"));
		inParm.setData("CANCEL_FLG", "N");
		inParm.setData("DS_DATE", "Y");// shibl 20120928 add 查询在院病人
		TParm parm = ADMInpTool.getInstance().selectall(inParm);
		setValue("STATION", parm.getValue("STATION_CODE", 0));
		TParm caseNoParm = new TParm();
		caseNoParm.addData("CASE_NO", parm.getValue("CASE_NO", 0));
		onCheckorder(caseNoParm);
		onQueryDtlByCaseNo(caseNoParm);
	}

	/**
	 * 根据给入CASE_NO的TParm，查询病患信息
	 * 
	 * @param parm
	 *            TParm
	 */
	public void onQueryDtlByCaseNo(TParm parm) {
		String startDate = (new StringBuilder()).append("TO_DATE('")
				.append(StringTool.getString(schDateFrom, "yyyyMMdd"))
				.append(startTime).append("','YYYYMMDDHH24MI')").toString();
		TParm result = UddDispatchTool.getInstance().getDispenseSinglePat(parm,
				startDate);
		if (result.getErrCode() != 0) {
			messageBox_("查无数据");
			return;
		} else {
			tblDispense.setParmValue(result);
			this.setValue("PAT_NUM", result.getCount("CASE_NO") + "");
			return;
		}
	}

	/**
	 * 清空病患TABLE和药品详细TABLE
	 */
	private void clearTbl() {
		if (tblDtl == null || tblDispense == null) {
			return;
		} else {
			tblDtl.removeRowAll();
			tblDispense.removeRowAll();
			return;
		}
	}

	/**
	 * 摆药失败写到本地LOG
	 * 
	 * @param failedParm
	 *            TParm
	 * @param dir
	 *            String
	 * @return boolean
	 */
	private boolean writeLocalFile(TParm failedParm, String dir) {
		if (failedParm == null)
			return false;
		int count = failedParm.getCount("CASE_NO");
		if (count < 1)
			return false;
		String fileName = "摆药Log"
				+ StringTool.getString(TJDODBTool.getInstance().getDBTime(),
						"yyyyMMddHHmm") + ".txt";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < count; i++) {
			sb.append(failedParm.getValue("CASE_NO", i)).append("\r\n");
		}
		try {
			FileTool.setString(dir + fileName, sb.toString());
		} catch (IOException e) {
		}
		return true;
	}

	/**
	 * 全选
	 */
	public void onSelectAll() {
		tblDispense.acceptText();
		if (tblDispense.getRowCount() < 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		if (getCheckBox("SELECT_ALL").isSelected()) {
			for (int i = 0; i < tblDispense.getRowCount(); i++)
				tblDispense.setItem(i, "SELECT_FLG", "Y");

		} else {
			for (int i = 0; i < tblDispense.getRowCount(); i++)
				tblDispense.setItem(i, "SELECT_FLG", "N");
		}
	}

	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

}
