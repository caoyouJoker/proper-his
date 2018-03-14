package com.javahis.ui.iva;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.ekt.EKTIO;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDSObject;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

import jdo.sys.PatTool;
import jdo.sys.Pat;

/**
 * <p>
 * Title: 静脉配液中心退药申请
 * </p>
 * 
 * <p>
 * Description: 静脉配液中心退药申请
 * </p>
 * 
 * <p>
 * Copyright: javahis 2015
 * </p>
 * 
 * <p>
 * Company:javahis
 * </p>
 * 
 * @author wangjc
 * @version 1.0
 */
public class IvaReturnRegistControl extends TControl {
	// 病患TABLE;退药TABLE；医嘱详细TABLE
	private TTable tblPat, tblRtn, tblRtnDtl;
	// Y常量
	public static final String Y = "Y";
	// N常量
	public static final String N = "N";
	// 空字符串
	public static final String NULL = "";
	// 退药对象
	private TDS detail = new TDS();
	// 退药医嘱详细对象
	private TDS detailD = new TDS();
	// 监听对象
	private TDSObject jdoObj = new TDSObject();
	// 执行科室
	public String execOrg;
	// 是否审核
	public boolean nsCheck;
	// 执行列表
	public List execList = new ArrayList();
	// 数据对象
	public TDS tds;
	// 是否保存注记
	public boolean isSave = false;
	// 是否编辑注记
	public boolean isEdit = false;
	// 首次单击注记
	public boolean firstClick = true;
	// 上面或者下面table那个被点击了，0为上面的table，1为下面的table
	public int upOrDown = 0;
	// 旧值
	private double oldValue = 0.0;

	/**
	 * 初始化界面
	 */
	public void onInit() {

		super.onInit();
		tblPat = (TTable) this.getComponent("TBL_PAT");
		tblRtn = (TTable) this.getComponent("TBL_RTN");
		tblRtnDtl = (TTable) this.getComponent("TBL_RTNDTL");
		String sql = "SELECT * FROM SYS_STATION WHERE STATION_CODE='"
				+ Operator.getStation() + "' ";
		execOrg = (new TParm(TJDODBTool.getInstance().select(sql))).getValue(
				"ORG_CODE", 0);
		nsCheck = (new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM ODI_SYSPARM"))).getBoolean("NS_CHECK_FLG", 0);
		tblRtnDtl.addEventListener("TBL_RTNDTL->" + TTableEvent.CHANGE_VALUE,
				this, "onClickDtl");
		tblRtnDtl.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxClick");
		onClear();
	}

	/**
	 * 清空动作
	 */
	public void onClear() {
		this.setValue("START_DATE", StringTool.rollDate(SystemTool
				.getInstance().getDate(), -7));
		this.setValue("END_DATE", SystemTool.getInstance().getDate());
		this.setValue("RTN_IND", execOrg);
		this.setValue("STATION", Operator.getStation());
		this.setValue("STA", Y);
		this.setValue("NO", NULL);
		this.setValue("NAME", NULL);
		this.setValue("UN_DONE", Y);
        this.callFunction("UI|DC_RDBTN|setEnabled", true);// add by wanglong 20130628
        this.callFunction("UI|ALL_RDBTN|setEnabled", true);
        this.callFunction("UI|DC_RDBTN|setSelected", true);
		this.callFunction("UI|LBL_RT|setVisible", false);
		this.setValue("RTN_NO", "");
		this.callFunction("UI|RTN_NO|setVisible", false);
		this.callFunction("UI|BED_NO|setVisible", false);
		this.callFunction("UI|NO|setVisible", true);
		tblPat.removeRow();
		tblRtn.removeRow();
		tblRtnDtl.removeRow();
		// this.setValue("RTN_IND", execOrg);
		// TTextFormat start=(TTextFormat)this.getComponent("START_DATE");
		// start.setEnabled(true);
		// start=(TTextFormat)this.getComponent("END_DATE");
		// start.setEnabled(true);
		execList = new ArrayList();
		upOrDown = 0;
	}

	/**
	 * 查询动作
	 */
	public void onQuery() {
		// 处理mr_no 自动补充0 luhai 2012-04-16 begin
		TRadioButton tb = (TRadioButton) this.getComponent("MR");
		if ("true".equals(tb.getValue())) {
			this.setValue("NO", PatTool.getInstance().checkMrno(
					this.getValue("NO") + ""));
		}
		// 处理mr_no 自动补充0 luhai 2012-04-16 end
		// luhai 2012-04-07 add 验证 begin
		if ("".equals(this.getValueString("RTN_IND"))) {
			this.messageBox("请选择退药药房！");
			return;
		}
		// luhai 2012-04-07 add 验证 end
		/*
		 * 执,40,boolean;床号,80,left;姓名,80,left;
		 * 处方号,100,left;护士站,120,STATION,left;病案号,120,left;住院号,120,left
		 */
		String where = getWhere();
		//System.out.println("退药登记:"+where);
		TParm parm = new TParm(TJDODBTool.getInstance().select(where));
		tblPat.setParmValue(parm);
		tblRtnDtl.removeRowAll();
		tblRtn.removeRowAll();
		execList = new ArrayList();
		isEdit = false;
		isSave = false;
		upOrDown = 0;
	}

	/**
	 * 拼装SQL的WHERE条件
	 * 
	 * @return String
	 */
	public String getWhere() {
		StringBuffer result = new StringBuffer();
		String no = "";
		String startDate = this.getValueString("START_DATE");
		startDate = startDate.substring(0, startDate.lastIndexOf(" "));
		String endDate = this.getValueString("END_DATE");
		endDate = endDate.substring(0, endDate.lastIndexOf(" "));
		if (TypeTool.getBoolean(this.getValue("UN_DONE"))) {// 未完成
			result
					.append(" (A.BILL_DATE >=TO_DATE('"
							+ startDate
							+ " 000000','YYYY-MM-DD HH24MISS') AND A.BILL_DATE <=TO_DATE('"
							+ endDate + " 235959','YYYY-MM-DD HH24MISS'))");
			result.append(" AND A.EXE_DEPT_CODE='"
					+ this.getValueString("RTN_IND") + "' ");// 退药单号
		}
		// 退药完成和未完成 都要加上时间限制 by liyh 20120806
		else {
			result
					.append(" AND  (A.DSPN_DATE >=TO_DATE('"
							+ startDate
							+ " 000000','YYYY-MM-DD HH24MISS') AND A.DSPN_DATE <=TO_DATE('"
							+ endDate + " 235959','YYYY-MM-DD HH24MISS'))");
			result.append(" AND A.EXEC_DEPT_CODE='"
					+ this.getValueString("RTN_IND") + "' ");
		}

		// 选取护士站
		if (Y.equalsIgnoreCase(this.getValueString("STA"))) {
			String stationCode = this.getValueString("STATION");
			if (!StringUtil.isNullString(stationCode)) {
				result.append(" AND A.STATION_CODE='" + stationCode + "' ");
			}
		} else {
			// 选取病案号
			if (Y.equalsIgnoreCase(this.getValueString("MR"))) {
				no = this.getValueString("NO");
				if (!StringUtil.isNullString(no))
					no = StringTool.fill0(no, PatTool.getInstance()
							.getMrNoLength()); // ======cehnxi
				result.append(" AND C.MR_NO='" + no + "' ");
			}
			// 选取病床号
			else {
				// this.messageBox_(this.getValueString("BED_NO"));
				result.append(" AND C.BED_NO='" + this.getValueString("BED_NO")
						+ "' ");
			}
		}
		String sql = "";
		if (TypeTool.getBoolean(this.getValue("UN_DONE"))) {
			// ===========pangben modify 20110511 start
			String region = "";
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				region = " AND C.REGION_CODE='" + Operator.getRegion() + "' ";
			}
			// ===========pangben modify 20110511 stop

			// luhai 2012-04-13 begin 加入床位排序
			// sql =
			// "SELECT DISTINCT C.CASE_NO,'N' AS EXEC,C.BED_NO,B.PAT_NAME,A.STATION_CODE,C.MR_NO,C.IPD_NO"
			// +
			// "  FROM IBS_ORDD A,SYS_PATINFO B,SYS_BED C" +
			// " WHERE # " +
			// "	AND A.CASE_NO=C.CASE_NO" +
			// " 	AND B.MR_NO=C.MR_NO  " +
			// "   AND (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG='Y') " +
			// "	AND (C.BED_OCCU_FLG IS  NULL OR C.BED_OCCU_FLG='N') " +
			// "   AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')";
			// sql += region; //=pangben modify 20110512
			// sql = sql.replaceFirst("#", result.toString());
			sql = "SELECT DISTINCT C.CASE_NO,'N' AS EXEC,C.BED_NO_DESC,B.PAT_NAME,A.STATION_CODE,C.MR_NO,C.IPD_NO,C.BED_NO"
					+ "  FROM IBS_ORDD A,SYS_PATINFO B,SYS_BED C"
					+ " WHERE # "
					+ "	AND A.CASE_NO=C.CASE_NO"
					+ " 	AND B.MR_NO=C.MR_NO  "
					+ "   AND (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG='Y') "
					+ "	AND (C.BED_OCCU_FLG IS  NULL OR C.BED_OCCU_FLG='N') "
					+ "   AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')";

			sql += region; // =pangben modify 20110512
			sql = sql.replaceFirst("#", result.toString());
			sql += " ORDER BY C.BED_NO  ";
			// luhai 2012-04-13 end
			// System.out.println("unDone.sql=" + sql);
		} else {
			// ===========pangben modify 20110511 start
			String region = "";
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
			}
			// ===========pangben modify 20110511 stop
			// luhai 2012-04-13 modify 加入床位排序 begin
			// sql =
			// "SELECT DISTINCT 'Y' AS EXEC,A.BED_NO,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO,A.CASE_NO,A.RTN_NO,A.RTN_NO_SEQ"
			// +
			// "	FROM ODI_DSPNM A, SYS_PATINFO B,SYS_BED C" +
			// "	WHERE A.CASE_NO=C.CASE_NO" +
			// "		  AND A.MR_NO=B.MR_NO" +
			// "   	  AND (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG='Y') " +
			// "		  AND (C.BED_OCCU_FLG IS  NULL OR C.BED_OCCU_FLG='N') " +
			// "   	  AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')"
			// +
			// "		  AND A.RTN_NO IS NOT NULL " +
			// "		  AND (A.PHA_RETN_CODE IS NULL OR A.PHA_RETN_CODE='')" +
			// region + //=pangben modify 20110512
			// "	ORDER BY A.RTN_NO_SEQ";
			sql = "SELECT DISTINCT 'Y' AS EXEC,C.BED_NO_DESC,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO,A.CASE_NO,'' AS RTN_NO ,'' AS RTN_NO_SEQ,A.BED_NO "
					+ // 查询退药清单 是按照case_no来查的，这里就没有必要查 RTN_NO_SEQ, by liyh
						// 20120803
					"	FROM ODI_DSPNM A, SYS_PATINFO B,SYS_BED C"
					+ "	WHERE A.CASE_NO=C.CASE_NO"
					+ "		  AND A.MR_NO=B.MR_NO"
					+
					/*
					 * "   	  AND (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG='Y') "
					 * +
					 * "		  AND (C.BED_OCCU_FLG IS  NULL OR C.BED_OCCU_FLG='N') "
					 * +
					 * "   	  AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')"
					 * + "		  AND A.RTN_NO IS NOT NULL " +
					 * "		  AND (A.PHA_RETN_CODE IS NULL OR A.PHA_RETN_CODE='')"
					 * +
					 */
					"   	  AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')"
					+ "       AND A.DSPN_KIND= 'RT' AND A.IVA_FLG='Y' " + result.toString() + // 退药完成和未完成
																			// 都要加上时间限制
																			// by
																			// liyh
																			// 20120806
					region + // =pangben modify 20110512
					"	ORDER BY A.BED_NO ";
			// luhai 2012-04-13 modify 加入床位排序 end
			// System.out.println("done.sql=" + sql);
		}
		//System.out.println("查询sql:"+sql);
		return sql;
	}

	/**
	 * 病患table点击事件，如果未有退药单，从IBS_ORDD中查询明细，如果有退药单，则从ODI_DSPNM中查询退药单
	 */
	public void onPatClick() {
		for (int i = 0; i < tblPat.getRowCount(); i++) {
			tblPat.setValueAt(false, i, 0);
		}
		tblPat.setValueAt(true, tblPat.getSelectedRow(), 0);
		String startDate = this.getValueString("START_DATE");
		startDate = startDate.substring(0, startDate.lastIndexOf(" "))
				+ " 000000";
		String endDate = this.getValueString("END_DATE");
		endDate = endDate.substring(0, endDate.lastIndexOf(" ")) + " 235959";
		String caseNo = TCM_Transform.getString(tblPat.getParmValue().getValue(
				"CASE_NO", tblPat.getSelectedRow()));
		//测试--------数据
		//System.out.println("选中的caseno为："+caseNo);
		
		String sql = "";
		// 病区,床号SQL 限制条件 by liyh 20120529 start
		String stationCodeSQL = " ";
		String stationCode = TCM_Transform.getString(tblPat.getParmValue()
				.getValue("STATION_CODE", tblPat.getSelectedRow()));
		if (null != stationCode && !"".equals(stationCode)) {
			stationCodeSQL = " AND A.STATION_CODE='" + stationCode + "' ";
		}
		// 病区SQL 限制条件 by liyh 20120529 end
		if (TypeTool.getBoolean(this.getValue("UN_DONE"))) {
			// ===========pangben modify 20110512 start
			String region = "";
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";//modify by wanglong 20130628
			}
			// ===========pangben modify 201105121 stop
			// ***************************************************************************************************
			// luhai 2012-2-1 将退药的明细定位到每一次配药，病患退药时需要根据病患的退药信息定位到没一个batchSeq
			// begin
			// ***************************************************************************************************
			// luhai modify 2012-04-13 begin 不连接odi_dspnm begin
			// sql =
			// "SELECT   'N' EXEC,B.ORDER_DESC || B.GOODS_DESC|| ' (' || B.SPECIFICATION || ')' ORDER_DESC,SUM (A.DOSAGE_QTY) RTN_DOSAGE_QTY,A.ORDER_CODE,"
			// +
			// "           A.DOSAGE_UNIT UNIT_DESC,'' TRANSMIT_RSN_CODE,0 CANCEL_DOSAGE_QTY,'' CANCELRSN_CODE,A.CASE_NO,A.OWN_PRICE,A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,"
			// +
			// "           C.VS_DR_CODE , C.DEPT_CODE, D.DOSE_CODE, D.GIVEBOX_FLG, D.PHA_TYPE, D.ROUTE_CODE "
			// +
			// "    FROM   IBS_ORDD A, SYS_FEE B,ADM_INP C, PHA_BASE D, ODI_DSPNM E "
			// +
			// "   WHERE   A.CASE_NO = '" + caseNo + "'" +
			// "           AND A.ORDER_CODE = B.ORDER_CODE" +
			// "	    AND A.CASE_NO=C.CASE_NO" +
			// "	    AND C.DS_DATE IS NULL" +
			// "	    AND (C.CANCEL_FLG IS NULL OR C.CANCEL_FLG ='N')" +
			// "	    AND A.ORDER_CAT1_CODE IN('PHA_W','PHA_C')" +
			// "           AND A.ORDER_CODE = D.ORDER_CODE " +
			// "           AND A.CASE_NO = E.CASE_NO(+) " +//luhai 2012-04-13
			// 加入左连接
			// "           AND A.ORDER_NO = E.ORDER_NO(+) " +//luhai 2012-04-13
			// 加入左连接
			// "           AND A.ORDER_SEQ = E.ORDER_SEQ(+) " +//luhai
			// 2012-04-13 加入左连接
			// //"           AND E.DC_DATE IS NOT NULL "
			// // region + //======pangben modify 20110512 //删region
			// "	GROUP BY   A.ORDER_CODE," +
			// "           A.DOSAGE_UNIT," +
			// "           B.ORDER_DESC," +
			// "           B.SPECIFICATION," +
			// "	    B.GOODS_DESC," +
			// "           A.DOSAGE_UNIT," +
			// "           A.CASE_NO," +
			// "	    A.OWN_PRICE," +
			// "	    A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE, "
			// +
			// "           C.VS_DR_CODE, C.DEPT_CODE, D.DOSE_CODE, D.GIVEBOX_FLG,D.PHA_TYPE,D.ROUTE_CODE ";
			// System.out.println("me.sql=" + sql);
//			sql = "SELECT   'N' EXEC,B.ORDER_DESC || B.GOODS_DESC|| ' (' || B.SPECIFICATION || ')' ORDER_DESC,SUM (A.DOSAGE_QTY) RTN_DOSAGE_QTY,A.ORDER_CODE,"
//					+ "           A.DOSAGE_UNIT UNIT_DESC,'' TRANSMIT_RSN_CODE,0 CANCEL_DOSAGE_QTY,'' CANCELRSN_CODE,A.CASE_NO,A.OWN_PRICE,A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,"
//					+ "           C.VS_DR_CODE , C.DEPT_CODE, D.DOSE_CODE, D.GIVEBOX_FLG, D.PHA_TYPE, D.ROUTE_CODE, B.NHI_PRICE,  B.ADDPAY_RATE,a.SEQ_NO,a.CASE_NO_SEQ,e.DOSE_TYPE  "
//					+ // update by liyh 20120525 :add cloumn
//						// NHI_PRICE，DISCOUNT_RATE ， DOSE_TYPE ， IBS_CASE_NO_SEQ
//						// ，IBS_SEQ_NO
//					"    FROM   IBS_ORDD A, SYS_FEE B,ADM_INP C, PHA_BASE D,pha_dose e "
//					+ // , ODI_DSPNM E
//					"   WHERE   A.CASE_NO = '"
//					+ caseNo
//					+ "'"
//					+ "           AND A.ORDER_CODE = B.ORDER_CODE"
//					+ "	    AND A.CASE_NO=C.CASE_NO"
//					+ "	    AND C.DS_DATE IS NULL"
//					+ "     and d.DOSE_CODE=e.DOSE_CODE "
//					+ "	    AND (C.CANCEL_FLG IS NULL OR C.CANCEL_FLG ='N')"
//					+ "	    AND A.ORDER_CAT1_CODE IN('PHA_W','PHA_C')"
//					+ " "
//					+ stationCodeSQL
//					+ " "
//					+ "           AND A.ORDER_CODE = D.ORDER_CODE and d.DOSE_CODE=e.DOSE_CODE "
//					+
//					// "           AND A.CASE_NO = E.CASE_NO(+) " +//luhai
//					// 2012-04-13 加入左连接
//					// "           AND A.ORDER_NO = E.ORDER_NO(+) " +//luhai
//					// 2012-04-13 加入左连接
//					// "           AND A.ORDER_SEQ = E.ORDER_SEQ(+) " +//luhai
//					// 2012-04-13 加入左连接
//					// "           AND E.DC_DATE IS NOT NULL "
//					// region + //======pangben modify 20110512 //删region
//					"	GROUP BY   A.ORDER_CODE,"
//					+ "           A.DOSAGE_UNIT,"
//					+ "           B.ORDER_DESC,"
//					+ "           B.SPECIFICATION,"
//					+ "	    B.GOODS_DESC,"
//					+ "           A.DOSAGE_UNIT,"
//					+ "           A.CASE_NO,"
//					+ "	    A.OWN_PRICE,"
//					+ "	    A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE, "
//					+ "           C.VS_DR_CODE, C.DEPT_CODE, D.DOSE_CODE, D.GIVEBOX_FLG,D.PHA_TYPE,D.ROUTE_CODE, B.NHI_PRICE,  B.ADDPAY_RATE,a.SEQ_NO,a.CASE_NO_SEQ,e.DOSE_TYPE  ";
			// luhai modify 2012-04-13 begin 不连接odi_dspnm end
            if (this.getValueBoolean("DC_RDBTN")) {// DC医嘱 add by wanglong 20130628
                sql =
                        "SELECT 'N' EXEC,B.ORDER_DESC||B.GOODS_DESC||' ('||B.SPECIFICATION||')' ORDER_DESC,SUM(A.DOSAGE_QTY-A.RT_REQTY) RTN_DOSAGE_QTY,"
                                + "A.ORDER_CODE,A.DOSAGE_UNIT UNIT_DESC,'' TRANSMIT_RSN_CODE,0 CANCEL_DOSAGE_QTY,'' CANCELRSN_CODE,A.CASE_NO,"
                                + "A.OWN_PRICE,A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,C.VS_DR_CODE,C.DEPT_CODE,"
                                + "D.DOSE_CODE,D.GIVEBOX_FLG,D.PHA_TYPE,A.ORDER_NO,A.ORDER_SEQ,D.ROUTE_CODE,B.NHI_PRICE,B.ADDPAY_RATE,E.DOSE_TYPE,"
                                + "CASE A.DSPN_KIND WHEN 'F' THEN 'UD' ELSE A.DSPN_KIND END AS DSPN_KIND,A.START_DTTM "
                                + "  FROM ODI_DSPNM A, SYS_FEE B, ADM_INP C, PHA_BASE D, PHA_DOSE E "
                                + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                                + "   AND A.CASE_NO = C.CASE_NO "
                                + "   AND C.DS_DATE IS NULL "
                                + "   AND D.DOSE_CODE = E.DOSE_CODE "
                                + "   AND A.PHA_DOSAGE_DATE IS NOT NULL "
                                + "   AND (A.DC_DATE IS NOT NULL AND (A.DSPN_KIND = 'F' OR A.DSPN_KIND = 'UD')) "
                                + "   AND (A.DOSAGE_QTY > A.RT_REQTY OR A.RT_REQTY IS NULL) "
                                + "   AND A.IVA_FLG = 'Y' "//20150506 wangjingchun add PIVAs配制
                                + "   AND (B.IS_REMARK <> 'Y' OR B.IS_REMARK IS NULL) "// wanglong add 20140725 过滤掉为备注的医嘱（该字段无默认值，空则表示非备注）
                                + "   AND (C.CANCEL_FLG IS NULL OR C.CANCEL_FLG = 'N') "
                                + "   AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C') "
                                + "   AND A.CASE_NO = '#'  #  #  "
                                + "   AND A.ORDER_CODE = D.ORDER_CODE "
                                + "   AND D.DOSE_CODE = E.DOSE_CODE "
                                + "GROUP BY A.ORDER_CODE,A.DOSAGE_UNIT,B.ORDER_DESC,B.SPECIFICATION,B.GOODS_DESC,A.DOSAGE_UNIT,A.CASE_NO,A.OWN_PRICE,"
                                + "         A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,C.VS_DR_CODE,C.DEPT_CODE,D.DOSE_CODE,"
                                + "         D.GIVEBOX_FLG,D.PHA_TYPE,D.ROUTE_CODE,B.NHI_PRICE,B.ADDPAY_RATE,E.DOSE_TYPE,A.ORDER_NO,A.ORDER_SEQ,A.DSPN_KIND,A.START_DTTM "
                                + " UNION "
                                + "SELECT 'N' EXEC,B.ORDER_DESC||B.GOODS_DESC||' ('||B.SPECIFICATION||')' ORDER_DESC,SUM(A.DOSAGE_QTY-A.RT_REQTY) RTN_DOSAGE_QTY,"
                                + "       A.ORDER_CODE,A.DOSAGE_UNIT UNIT_DESC,'' TRANSMIT_RSN_CODE,0 CANCEL_DOSAGE_QTY,'' CANCELRSN_CODE,A.CASE_NO,A.OWN_PRICE,"
                                + "       A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,C.VS_DR_CODE,C.DEPT_CODE,D.DOSE_CODE,D.GIVEBOX_FLG,D.PHA_TYPE,"
                                + "       A.ORDER_NO,A.ORDER_SEQ,D.ROUTE_CODE,B.NHI_PRICE,B.ADDPAY_RATE,E.DOSE_TYPE,A.DSPN_KIND,A.START_DTTM "
                                + "  FROM ODI_DSPNM A, SYS_FEE B, ADM_INP C, PHA_BASE D, PHA_DOSE E "
                                + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                                + "   AND A.CASE_NO = C.CASE_NO "
                                + "   AND C.DS_DATE IS NULL "
                                + "   AND D.DOSE_CODE = E.DOSE_CODE "
                                + "   AND A.PHA_DOSAGE_DATE IS NOT NULL "
                                + "   AND (A.DC_NS_CHECK_DATE IS NOT NULL AND (A.DSPN_KIND = 'ST' OR A.DSPN_KIND = 'DS')) "
                                + "   AND (A.DOSAGE_QTY > A.RT_REQTY OR A.RT_REQTY IS NULL) "
                                + "   AND A.IVA_FLG = 'Y' "//20150506 wangjingchun add PIVAs配制
                                + "   AND (B.IS_REMARK <> 'Y' OR B.IS_REMARK IS NULL) "// wanglong add 20140725 过滤掉为备注的医嘱（该字段无默认值，空则表示非备注）
                                + "   AND (C.CANCEL_FLG IS NULL OR C.CANCEL_FLG = 'N') "
                                + "   AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C') "
                                + "   AND A.CASE_NO = '#'  #  #  "
                                + "   AND A.ORDER_CODE = D.ORDER_CODE "
                                + "   AND D.DOSE_CODE = E.DOSE_CODE "
                                + "GROUP BY A.ORDER_CODE,A.DOSAGE_UNIT,B.ORDER_DESC,B.SPECIFICATION,B.GOODS_DESC,A.DOSAGE_UNIT,A.CASE_NO,A.OWN_PRICE,"
                                + "         A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,C.VS_DR_CODE,C.DEPT_CODE,D.DOSE_CODE,D.GIVEBOX_FLG,"
                                + "         D.PHA_TYPE,D.ROUTE_CODE,B.NHI_PRICE,B.ADDPAY_RATE,E.DOSE_TYPE,A.DSPN_KIND,A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM";
                sql = sql.replaceFirst("#", caseNo);
                sql = sql.replaceFirst("#", stationCodeSQL);
                sql = sql.replaceFirst("#", region);
                sql = sql.replaceFirst("#", caseNo);
                sql = sql.replaceFirst("#", stationCodeSQL);
                sql = sql.replaceFirst("#", region);
            } else {// 全部医嘱
                sql =
                        "SELECT EXEC,ORDER_DESC,SUM(RTN_DOSAGE_QTY) RTN_DOSAGE_QTY,ORDER_CODE,UNIT_DESC,TRANSMIT_RSN_CODE,CANCEL_DOSAGE_QTY,"
                                + "CANCELRSN_CODE,CASE_NO,OWN_PRICE,ORDER_CAT1_CODE,CAT1_TYPE,IPD_NO,MR_NO,BED_NO,STATION_CODE,VS_DR_CODE,DEPT_CODE,"
                                + "DOSE_CODE,GIVEBOX_FLG, PHA_TYPE,ROUTE_CODE,NHI_PRICE,ADDPAY_RATE,DOSE_TYPE,DSPN_KIND "
                                + "  FROM (SELECT 'N' EXEC,B.ORDER_DESC||B.GOODS_DESC||' ('||B.SPECIFICATION||')' ORDER_DESC,SUM(A.DOSAGE_QTY) RTN_DOSAGE_QTY,"
                                + "               A.ORDER_CODE,A.DOSAGE_UNIT UNIT_DESC,'' TRANSMIT_RSN_CODE,0 CANCEL_DOSAGE_QTY,'' CANCELRSN_CODE,A.CASE_NO,"
                                + "               A.OWN_PRICE,A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,C.VS_DR_CODE,C.DEPT_CODE,"
                                + "               D.DOSE_CODE,D.GIVEBOX_FLG,D.PHA_TYPE,D.ROUTE_CODE,B.NHI_PRICE,B.ADDPAY_RATE,E.DOSE_TYPE,"
                                + "               CASE A.DSPN_KIND WHEN 'F' THEN 'UD' ELSE A.DSPN_KIND END AS DSPN_KIND "
                                + "          FROM ODI_DSPNM A, SYS_FEE B, ADM_INP C, PHA_BASE D, PHA_DOSE E "
                                + "         WHERE A.ORDER_CODE = B.ORDER_CODE "
                                + "           AND A.CASE_NO = C.CASE_NO "
                                + "           AND C.DS_DATE IS NULL "
                                + "           AND D.DOSE_CODE = E.DOSE_CODE "
                                + "           AND A.DSPN_KIND <> 'RT' "
                                + "           AND A.PHA_DOSAGE_DATE IS NOT NULL "
                                + "   		  AND A.IVA_FLG = 'Y' "//20150506 wangjingchun add PIVAs配制
                                + "           AND (C.CANCEL_FLG IS NULL OR C.CANCEL_FLG = 'N') "
                                + "           AND (B.IS_REMARK <> 'Y' OR B.IS_REMARK IS NULL) "// wanglong add 20140725 过滤掉为备注的医嘱（该字段无默认值，空则表示非备注）
                                + "           AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C') "
                                + "           AND A.CASE_NO = '#'  #  #  "
                                + "           AND A.ORDER_CODE = D.ORDER_CODE "
                                + "           AND D.DOSE_CODE = E.DOSE_CODE "
                                + "      GROUP BY A.ORDER_CODE,A.DOSAGE_UNIT,B.ORDER_DESC,B.SPECIFICATION,B.GOODS_DESC,A.DOSAGE_UNIT,A.CASE_NO,A.OWN_PRICE,A.ORDER_CAT1_CODE,"
                                + "               B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,C.VS_DR_CODE,C.DEPT_CODE,D.DOSE_CODE,D.GIVEBOX_FLG,D.PHA_TYPE,"
                                + "               D.ROUTE_CODE,B.NHI_PRICE,B.ADDPAY_RATE,E.DOSE_TYPE,A.DSPN_KIND "
                                + "        UNION "
                                + "        SELECT 'N' EXEC,B.ORDER_DESC||B.GOODS_DESC||' ('||B.SPECIFICATION||')' ORDER_DESC,SUM(-A.RTN_DOSAGE_QTY) RTN_DOSAGE_QTY,"
                                + "               A.ORDER_CODE,A.RTN_DOSAGE_UNIT UNIT_DESC,'' TRANSMIT_RSN_CODE,0 CANCEL_DOSAGE_QTY,'' CANCELRSN_CODE,"
                                + "               A.CASE_NO,A.OWN_PRICE,A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,C.VS_DR_CODE,"
                                + "               C.DEPT_CODE,D.DOSE_CODE,D.GIVEBOX_FLG,D.PHA_TYPE,D.ROUTE_CODE,B.NHI_PRICE,B.ADDPAY_RATE,E.DOSE_TYPE,"
                                + "               CASE A.RT_KIND WHEN 'F' THEN 'UD' ELSE A.RT_KIND END AS DSPN_KIND "
                                + "          FROM ODI_DSPNM A, SYS_FEE B, ADM_INP C, PHA_BASE D, PHA_DOSE E "
                                + "         WHERE A.ORDER_CODE = B.ORDER_CODE "
                                + "           AND A.CASE_NO = C.CASE_NO "
                                + "           AND C.DS_DATE IS NULL "
                                + "           AND D.DOSE_CODE = E.DOSE_CODE "
                                + "           AND A.DSPN_KIND = 'RT' "
                                + "   		  AND A.IVA_FLG = 'Y' "//20150506 wangjingchun add PIVAs配制
                                + "           AND (C.CANCEL_FLG IS NULL OR C.CANCEL_FLG = 'N') "
                                + "           AND (B.IS_REMARK <> 'Y' OR B.IS_REMARK IS NULL) "// wanglong add 20140725 过滤掉为备注的医嘱（该字段无默认值，空则表示非备注）
                                + "           AND A.ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C') "
                                + "           AND A.CASE_NO = '#'  #  #  "
                                + "           AND A.ORDER_CODE = D.ORDER_CODE "
                                + "           AND D.DOSE_CODE = E.DOSE_CODE "
                                + "        GROUP BY A.ORDER_CODE,A.DOSAGE_UNIT,B.ORDER_DESC,B.SPECIFICATION,B.GOODS_DESC,A.RTN_DOSAGE_UNIT,A.CASE_NO,"
                                + "                 A.OWN_PRICE,A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE,C.VS_DR_CODE,"
                                + "                 C.DEPT_CODE,D.DOSE_CODE,D.GIVEBOX_FLG,D.PHA_TYPE,D.ROUTE_CODE,B.NHI_PRICE,B.ADDPAY_RATE,"
                                + "                 E.DOSE_TYPE,A.RT_KIND) TAB "
                                + " WHERE TAB.RTN_DOSAGE_QTY > 0 "
                                + "GROUP BY EXEC,ORDER_DESC,ORDER_CODE,UNIT_DESC,TRANSMIT_RSN_CODE,CANCEL_DOSAGE_QTY,CANCELRSN_CODE,CASE_NO,OWN_PRICE,"
                                + "         ORDER_CAT1_CODE,CAT1_TYPE,IPD_NO,MR_NO,BED_NO,STATION_CODE,VS_DR_CODE,DEPT_CODE,DOSE_CODE,GIVEBOX_FLG,"
                                + "         PHA_TYPE,ROUTE_CODE,NHI_PRICE,ADDPAY_RATE,DOSE_TYPE,DSPN_KIND";
                sql = sql.replaceFirst("#", caseNo);
                sql = sql.replaceFirst("#", stationCodeSQL);
                sql = sql.replaceFirst("#", region);
                sql = sql.replaceFirst("#", caseNo);
                sql = sql.replaceFirst("#", stationCodeSQL);
                sql = sql.replaceFirst("#", region);
            }
			//System.out.println("--22-me.sql=" + sql);
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			tblRtnDtl.removeRowAll();
			tblRtnDtl.setParmValue(parm);
			
			// TParm parm =new TParm();
			// //得到退药的TParm
			// parm=getRtnRarmWithoutReturn(caseNo);
			// tblRtnDtl.removeRowAll();
			// tblRtnDtl.setParmValue(parm);
			// ***************************************************************************************************
			// luhai 2012-2-1 将退药的明细定位到每一次配药，病患退药时需要根据病患的退药信息定位到没一个batchSeq end
			// ***************************************************************************************************
			return;
		} else {
			// ===========pangben modify 20110512 start

			// ===========pangben modify 20110512 stop

			// 药房确认,80,boolean;退药单号,100;退药时间,100;退药人员,100;登录时间,100;登录人员,100,OPERATOR
			// EXEC;ORDER_NO;PHA_RTN_DATE;PHA_RTN_CODE;DSPN_DATE;DSPN_USER
			sql = "SELECT   CASE WHEN PHA_RETN_CODE = '' THEN 'N' ELSE 'Y' END EXEC,RTN_NO,PHA_RETN_DATE,PHA_RETN_CODE,CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,START_DTTM,STATION_CODE"
					+ "  FROM   ODI_DSPNM"
					+ " WHERE   CASE_NO = '"
					+ caseNo
					+ "' AND RTN_NO IS NOT NULL AND IVA_FLG='Y' " + getRegionWhereStr(); // ======pangben
																		// modify
																		// 20110512
			// 处理配药单号表格显示，防止出现重复行
			StringBuffer sqlbf = new StringBuffer();
			sqlbf
					.append("SELECT DISTINCT CASE WHEN (PHA_RETN_CODE = '' or PHA_RETN_CODE is null) THEN 'N' ELSE 'Y' END EXEC,RTN_NO,TO_CHAR(DSPN_DATE,'yyyy-MM-dd') AS  PHA_RETN_DATE, ");
			sqlbf.append("       DSPN_USER AS PHA_RETN_CODE,CASE_NO ,B.USER_NAME ");
			sqlbf.append(" FROM  ODI_DSPNM A,SYS_OPERATOR B");
			sqlbf.append(" WHERE  A.DSPN_USER=B.USER_ID AND CASE_NO = '"
					+ caseNo + "' AND RTN_NO IS NOT NULL AND A.IVA_FLG='Y' ");
			sqlbf.append(" AND  (DSPN_DATE >=TO_DATE('" + startDate
					+ "','YYYY-MM-DD HH24MISS') AND DSPN_DATE <=TO_DATE('"
					+ endDate + "','YYYY-MM-DD HH24MISS'))");
			sqlbf.append(" AND A.REGION_CODE='" + Operator.getRegion() + "' ");
			sqlbf.append(" ORDER BY RTN_NO DESC ");
			// sql+="  AND RTN_NO IN (SELECT DISTINCT RTN_NO FROM ODI_DSPNM "+" WHERE   CASE_NO = '"
			// + caseNo + "' AND RTN_NO IS NOT NULL"+")";
			// luhai modify
			//System.out.println("your.sql=========" + sqlbf.toString());
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					sqlbf.toString()));
			tblRtn.removeRowAll();
			tblRtn.setParmValue(parm);
			
		}
	}

	/**
	 * 
	 * 得到院区的查询条件 luhai 2012-2-2 add
	 * 
	 * @return
	 */
	private String getRegionWhereStr() {
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND REGION_CODE='" + Operator.getRegion() + "' ";
		}
		return region;
	}

	/**
	 * 
	 * 得到未退药列表的sqls
	 * 
	 * @param regionStr
	 * @param caseNo
	 *            luhai 2012-2-2
	 * @return
	 */
	private String getSelectRtnSql(String caseNo, int batchIndex) {
		// ************************************************************************************************************
		// luhai 2012-04-97 退药时查odi_dspm 表 不查询账务表，解决查询账务表示与odi_dspm
		// 数据联查出现多比重复医嘱的情况
		// ************************************************************************************************************
		// String dispenseStr="E.DISPENSE_QTY"+batchIndex;
		// String returnQtyStr="E.RETURN_QTY"+batchIndex;
		// String diffQtyStr="("+dispenseStr+"-"+returnQtyStr+")";
		// StringBuffer sqlbf = new StringBuffer();
		// sqlbf.append(" SELECT   'N' EXEC,B.ORDER_DESC || B.GOODS_DESC|| ' (' || B.SPECIFICATION || ')' ORDER_DESC,"+diffQtyStr+" AS RTN_DOSAGE_QTY,A.ORDER_CODE, ");
		// sqlbf.append(" A.DOSAGE_UNIT UNIT_DESC,'' TRANSMIT_RSN_CODE,0 CANCEL_DOSAGE_QTY,'' CANCELRSN_CODE,A.CASE_NO,A.OWN_PRICE,A.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,A.STATION_CODE, ");
		// sqlbf.append(" C.VS_DR_CODE , C.DEPT_CODE, D.DOSE_CODE, D.GIVEBOX_FLG, D.PHA_TYPE, D.ROUTE_CODE, ");
		// sqlbf.append(" E.BATCH_SEQ"+batchIndex+" BATCH_SEQ ,E.VERIFYIN_PRICE"+batchIndex+" VERIFYIN_PRICE,E.DISPENSE_QTY"+batchIndex+" DISPENSE_QTY, ");
		// sqlbf.append(" E.ORDER_NO,E.ORDER_SEQ,E.START_DTTM,"+batchIndex+" AS \"INDEX\",F.BATCH_NO,F.VALID_DATE ");
		// sqlbf.append("  FROM  IBS_ORDD A, SYS_FEE B,ADM_INP C, PHA_BASE D, ODI_DSPNM E,IND_STOCK F ");
		// sqlbf.append(" WHERE   A.CASE_NO = '" + caseNo + "' ");
		// sqlbf.append(" AND A.ORDER_CODE = B.ORDER_CODE ");
		// sqlbf.append("  AND A.CASE_NO=C.CASE_NO ");
		// sqlbf.append(" AND C.DS_DATE IS NULL ");
		// sqlbf.append(" AND C.DS_DATE IS NULL ");
		// sqlbf.append(" AND (C.CANCEL_FLG IS NULL OR C.CANCEL_FLG ='N') ");
		// sqlbf.append(" AND A.ORDER_CAT1_CODE IN('PHA_W','PHA_C') ");
		// sqlbf.append(" AND A.ORDER_CODE = D.ORDER_CODE ");
		// sqlbf.append(" AND A.CASE_NO = E.CASE_NO ");
		// sqlbf.append(" AND A.ORDER_NO = E.ORDER_NO ");
		// sqlbf.append(" AND A.ORDER_SEQ = E.ORDER_SEQ ");
		// sqlbf.append(" AND E.DISPENSE_QTY"+batchIndex+">0 ");
		// sqlbf.append(" AND E.REGION_CODE='"+Operator.getRegion()+"' ");
		// sqlbf.append(" AND E.DSPN_KIND <>'RT' ");
		// sqlbf.append( " AND "+diffQtyStr+">0 ");
		// sqlbf.append(" AND F.ORDER_CODE=A.ORDER_CODE AND E.EXEC_DEPT_CODE=F.ORG_CODE AND F.BATCH_SEQ=E.BATCH_SEQ"+batchIndex);
		String dispenseStr = "E.DISPENSE_QTY" + batchIndex;
		String returnQtyStr = "E.RETURN_QTY" + batchIndex;
		String diffQtyStr = "(" + dispenseStr + "-" + returnQtyStr + ")";
		StringBuffer sqlbf = new StringBuffer();
		sqlbf
				.append(" SELECT   'N' EXEC,B.ORDER_DESC || B.GOODS_DESC|| ' (' || B.SPECIFICATION || ')' ORDER_DESC,"
						+ diffQtyStr + " AS RTN_DOSAGE_QTY,E.ORDER_CODE, ");
		sqlbf
				.append(" E.DOSAGE_UNIT UNIT_DESC,'' TRANSMIT_RSN_CODE,0 CANCEL_DOSAGE_QTY,'' CANCELRSN_CODE,E.CASE_NO,E.OWN_PRICE,E.ORDER_CAT1_CODE,B.CAT1_TYPE,C.IPD_NO,C.MR_NO,C.BED_NO,E.STATION_CODE, ");
		sqlbf
				.append(" C.VS_DR_CODE , C.DEPT_CODE, D.DOSE_CODE, D.GIVEBOX_FLG, D.PHA_TYPE, D.ROUTE_CODE, ");
		sqlbf.append(" E.BATCH_SEQ" + batchIndex
				+ " BATCH_SEQ ,E.VERIFYIN_PRICE" + batchIndex
				+ " VERIFYIN_PRICE,E.DISPENSE_QTY" + batchIndex
				+ " DISPENSE_QTY, ");
		sqlbf.append(" E.ORDER_NO,E.ORDER_SEQ,E.START_DTTM," + batchIndex
				+ " AS \"INDEX\",F.BATCH_NO,F.VALID_DATE ");
		sqlbf
				.append("  FROM  SYS_FEE B,ADM_INP C, PHA_BASE D, ODI_DSPNM E,IND_STOCK F ");
		sqlbf.append(" WHERE   E.CASE_NO = '" + caseNo + "' ");
		sqlbf.append(" AND E.ORDER_CODE = B.ORDER_CODE ");
		sqlbf.append("  AND E.CASE_NO=C.CASE_NO ");
		sqlbf.append(" AND C.DS_DATE IS NULL ");
		sqlbf.append(" AND C.DS_DATE IS NULL ");
		sqlbf.append(" AND (C.CANCEL_FLG IS NULL OR C.CANCEL_FLG ='N') ");
		sqlbf.append(" AND E.ORDER_CAT1_CODE IN('PHA_W','PHA_C') ");
		sqlbf.append(" AND E.ORDER_CODE = D.ORDER_CODE ");
		// sqlbf.append(" AND A.CASE_NO = E.CASE_NO ");
		// sqlbf.append(" AND A.ORDER_NO = E.ORDER_NO ");
		// sqlbf.append(" AND A.ORDER_SEQ = E.ORDER_SEQ ");
		sqlbf.append(" AND E.DISPENSE_QTY" + batchIndex + ">0 ");
		sqlbf.append(" AND E.REGION_CODE='" + Operator.getRegion() + "' ");
		sqlbf.append(" AND E.DSPN_KIND <>'RT' ");
		sqlbf.append(" AND " + diffQtyStr + ">0 ");
		sqlbf
				.append(" AND F.ORDER_CODE=E.ORDER_CODE AND E.EXEC_DEPT_CODE=F.ORG_CODE AND F.BATCH_SEQ=E.BATCH_SEQ"
						+ batchIndex);
		// System.out.println("sql:"+sqlbf.toString());
		// ************************************************************************************************************
		// luhai 2012-04-97 退药时查odi_dspm 表 不查询账务表，解决查询账务表示与odi_dspm
		// 数据联查出现多比重复医嘱的情况
		// ************************************************************************************************************
		return sqlbf.toString();
	}

	/**
	 * 
	 * 得到未退药的Parm
	 * 
	 * @return
	 */
	private TParm getRtnRarmWithoutReturn(String caseNo) {
		TParm returnParm = new TParm();
		String sql = "";
		for (int i = 1; i <= 3; i++) {
			sql = getSelectRtnSql(caseNo, i);
			// System.out.println("me.sql=" + sql);
			TParm parmtmp = new TParm(TJDODBTool.getInstance().select(sql));
			for (int j = 0; j < parmtmp.getCount(); j++) {
				returnParm.addData("EXEC", parmtmp.getData("EXEC", j));
				returnParm.addData("ORDER_DESC", parmtmp.getData("ORDER_DESC",
						j));
				returnParm.addData("RTN_DOSAGE_QTY", parmtmp.getData(
						"RTN_DOSAGE_QTY", j));
				returnParm.addData("ORDER_CODE", parmtmp.getData("ORDER_CODE",
						j));
				returnParm
						.addData("UNIT_DESC", parmtmp.getData("UNIT_DESC", j));
				returnParm.addData("TRANSMIT_RSN_CODE", parmtmp.getData(
						"TRANSMIT_RSN_CODE", j));
				returnParm.addData("CANCEL_DOSAGE_QTY", parmtmp.getData(
						"CANCEL_DOSAGE_QTY", j));
				returnParm.addData("CANCELRSN_CODE", parmtmp.getData(
						"CANCELRSN_CODE", j));
				returnParm.addData("CASE_NO", parmtmp.getData("CASE_NO", j));
				returnParm
						.addData("OWN_PRICE", parmtmp.getData("OWN_PRICE", j));
				returnParm.addData("ORDER_CAT1_CODE", parmtmp.getData(
						"ORDER_CAT1_CODE", j));
				returnParm
						.addData("CAT1_TYPE", parmtmp.getData("CAT1_TYPE", j));
				returnParm.addData("IPD_NO", parmtmp.getData("IPD_NO", j));
				returnParm.addData("MR_NO", parmtmp.getData("MR_NO", j));
				returnParm.addData("BED_NO", parmtmp.getData("BED_NO", j));
				returnParm.addData("STATION_CODE", parmtmp.getData(
						"STATION_CODE", j));
				returnParm.addData("VS_DR_CODE", parmtmp.getData("VS_DR_CODE",
						j));
				returnParm
						.addData("DOSE_CODE", parmtmp.getData("DOSE_CODE", j));
				returnParm.addData("GIVEBOX_FLG", parmtmp.getData(
						"GIVEBOX_FLG", j));
				returnParm.addData("PHA_TYPE", parmtmp.getData("PHA_TYPE", j));
				returnParm.addData("ROUTE_CODE", parmtmp.getData("ROUTE_CODE",
						j));
				returnParm
						.addData("BATCH_SEQ", parmtmp.getData("BATCH_SEQ", j));
				returnParm.addData("VERIFYIN_PRICE", parmtmp.getData(
						"VERIFYIN_PRICE", j));
				returnParm.addData("DISPENSE_QTY", parmtmp.getData(
						"DISPENSE_QTY", j));
				returnParm.addData("ORDER_NO", parmtmp.getData("ORDER_NO", j));
				returnParm
						.addData("ORDER_SEQ", parmtmp.getData("ORDER_SEQ", j));
				returnParm.addData("START_DTTM", parmtmp.getData("START_DTTM",
						j));
				returnParm.addData("INDEX", parmtmp.getData("INDEX", j));
				returnParm.addData("BATCH_NO", parmtmp.getData("BATCH_NO", j));
				returnParm.addData("VALID_DATE", parmtmp.getData("VALID_DATE",
						j));
			}
		}
		returnParm.setCount(returnParm.getCount("ORDER_DESC"));
		return returnParm;
	}

	/**
	 * 以保存退药单明细TABLE点击事件
	 */
	public void onClickRtn(Object ttableNode) {
		if (isEdit) {
			int yesOrNo = this.messageBox("提示", "已修改数据，点击确定保存", 0);
			if (0 == yesOrNo) {
				onSave();
			}
		}
		int row = tblRtn.getSelectedRow();
		String caseNo = tblRtn.getParmValue().getValue("CASE_NO", row);
		String orderNo = tblRtn.getParmValue().getValue("RTN_NO", row);
		// ===========pangben modify 20110512 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ===========pangben modify 20110512 stop

		// 退药单明细SQL
		String sql = " SELECT NULL AS EXEC, ORDER_SEQ,ORDER_DESC,RTN_DOSAGE_QTY,RTN_DOSAGE_UNIT UNIT_DESC, "
				+ "	CANCEL_DOSAGE_QTY,TRANSMIT_RSN_CODE,CANCELRSN_CODE,ORDER_CODE,CASE_NO,"
				+ "	ORDER_NO,ORDER_SEQ AS ORDER_SEQ_TEMP,START_DTTM,"
				+ " DSPN_KIND,PARENT_CASE_NO,PARENT_ORDER_NO,PARENT_ORDER_SEQ,PARENT_START_DTTM,REGION_CODE,RTN_NO,DSPN_KIND,STATION_CODE,RT_KIND,DISPENSE_QTY,PHA_RETN_CODE,PHA_RETN_DATE "// add by wanglong 20130628   //add by duzhw 20130923
				+ " FROM  ODI_DSPNM A WHERE CASE_NO='"
				+ caseNo
				+ "'"
				+ region
				+ " AND RTN_NO='"
				+ orderNo
				+ "' AND DSPN_KIND='RT' "
				+ " AND IVA_FLG = 'Y' "//20150506 wangjingchun add
				+ " ORDER BY ORDER_SEQ";
		//System.out.println("退药单明细SQLrtn----->" + sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		tblRtnDtl.setParmValue(parm);
		execList = new ArrayList();
		firstClick = true;
		upOrDown = 0;
	}

	/**
	 * 
	 * @param tNode
	 * @return 假：成功，真：失败
	 */
	public boolean onClickDtl(TTableNode tNode) {
		String columnName = tblRtnDtl.getParmMap(tNode.getColumn());
		if (!"EXEC".equalsIgnoreCase(columnName)
				&& !"RTN_DOSAGE_QTY".equalsIgnoreCase(columnName)
				&& !"TRANSMIT_RSN_CODE".equalsIgnoreCase(columnName)) {
			return true;
		}
		if ("EXEC".equalsIgnoreCase(columnName)) {
			return false;
		}
		if ("TRANSMIT_RSN_CODE".equalsIgnoreCase(columnName)) {
			return false;
		}
		if (!tblRtnDtl.getParmValue().getBoolean("EXEC", tNode.getRow())) {
			this.messageBox_("请先点选执行");
			return true;
		}

		if (firstClick) {
			oldValue = TCM_Transform.getDouble(tNode.getOldValue());
		}
		double value = TCM_Transform.getDouble(tNode.getValue());
		if (value < 0) {
			this.messageBox_("退药数量不能为负数，请重新输入");
			return true;
		}
		if (oldValue < value) {
		}
		isEdit = true;
		firstClick = false;
		upOrDown = 1;
		return false;
	}

	/**
	 * 详细信息TABLECHECK_BOX点选事件
	 * 
	 * @param obj
	 */
	public void onCheckBoxClick(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		boolean value = TCM_Transform.getBoolean(table.getValueAt(table
				.getSelectedRow(), table.getSelectedColumn()));
		if (value) {
			execList.add(table.getSelectedRow());
		} else {
			execList.remove((Object) table.getSelectedRow());
		}
		isEdit = true;
		firstClick = true;
		upOrDown = 1;
	}

	/**
	 * 保存事件
	 */
	public void onSave() {
		// luhai 2012-04-16 加入接收表格中正在编辑的值的代码
		// 解决 保存时table还存在编辑状态，用户输入的新值没有保存上的问题begin
		this.tblRtnDtl.acceptText();
		// luhai 2012-04-16 加入接收表格中正在编辑的值的代
		// 解决 保存时table还存在编辑状态，用户输入的新值没有保存上的问题 end
		tblRtnDtl.acceptText();
		if (execList == null || execList.size() < 1)
			return;
		String orderNo = SystemTool.getInstance().getNo("ALL", "ODI",
				"ORDER_NO", "ORDER_NO");
		TParm parm = tblRtnDtl.getParmValue();
		// System.out.println("tblRtnDtl============"+parm);
		String caseNo, sttDttm;
		double rtnDosageQty;
		int orderSeq;
		Timestamp t = TJDODBTool.getInstance().getDBTime();
		sttDttm = StringTool.getString(t, "yyyyMMddHHmm");
		TParm result = new TParm();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		String nowStr = StringTool.getString(now, "yyyyMMddHHmmss");
		String orderDate = nowStr.substring(0, 8);
		String orderDateTime = nowStr.substring(8);
		// System.out.println("PARM=" + parm);
		boolean save = TypeTool.getBoolean(this.getValue("UN_DONE"));
		if (save) {
			// 2012-2-2 luhai modify 退药逻辑修改 begin
			for (int i = 0; i < execList.size(); i++) {
				int j = (Integer) execList.get(i);
				caseNo = parm.getValue("CASE_NO", j);

				orderSeq = i + 1;
				rtnDosageQty = parm.getDouble("RTN_DOSAGE_QTY", j);
				if(!checkOrderData(parm.getRow(j)))//检核数据 shibl add 20130312 
					return;
				result.addData("START_DTTM", sttDttm);
				/******************
				 * update by liyh 20120525:add cloumn NHI_PRICE，DISCOUNT_RATE ，
				 * IBS_CASE_NO_SEQ ，IBS_SEQ_NO start
				 ******/
				result.addData("NHI_PRICE", parm.getDouble("NHI_PRICE", j));
				result.addData("DISCOUNT_RATE", parm
						.getDouble("ADDPAY_RATE", j));
				result.addData("IBS_SEQ_NO", parm.getDouble("SEQ_NO", j));
				result.addData("IBS_CASE_NO_SEQ", parm.getDouble("CASE_NO_SEQ",
						j));
				/******************
				 * update by liyh 20120525:add cloumn
				 * NHI_PRICE，DISCOUNT_RATEIBS_CASE_NO_SEQ ，IBS_SEQ_NO end
				 ******/
				result.addData("END_DTTM", sttDttm);
				result.addData("CASE_NO", caseNo);
				result.addData("ORDER_NO", orderNo);
				result.addData("ORDER_SEQ", orderSeq);
				result.addData("ORDER_CODE", parm.getValue("ORDER_CODE", j));
				result.addData("RTN_NO", orderNo);
				result.addData("RTN_NO_SEQ", orderSeq);
				result.addData("ORDER_DATE", orderDate);
				result.addData("DEPT_CODE", parm.getValue("DEPT_CODE", i));
				result.addData("REGION_CODE", Operator.getRegion());
				result.addData("ORDER_DATETIME", orderDateTime);
				result.addData("ORDER_CAT1_CODE", parm.getValue(
						"ORDER_CAT1_CODE", j));
				result.addData("CAT1_TYPE", parm.getValue("CAT1_TYPE", j));
				result.addData("DOSE_TYPE", parm.getValue("DOSE_TYPE", j));
				result.addData("GIVEBOX_FLG", parm.getValue("GIVEBOX_FLG", j));
				// result.addData("LINKMAIN_FLG", parm.getValue("LINKMAIN_FLG",
				// j));
				result.addData("OWN_PRICE", parm.getData("OWN_PRICE", j));
				result.addData("PHA_TYPE", parm.getValue("PHA_TYPE", j));
				result.addData("ROUTE_CODE", parm.getValue("ROUTE_CODE", j));
				result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", j));
				result.addData("MR_NO", parm.getValue("MR_NO", j));
				result
						.addData("STATION_CODE", parm.getValue("STATION_CODE",
								j));
				result.addData("BED_NO", parm.getValue("BED_NO", j));
				result.addData("IPD_NO", parm.getValue("IPD_NO", j));
				result.addData("EXEC_DEPT_CODE", this.getValue("RTN_IND"));
				result.addData("RX_KIND", "RT");
				result.addData("DC_TOT", rtnDosageQty
						* parm.getDouble("OWN_PRICE", j));
				result.addData("RTN_DOSAGE_QTY", rtnDosageQty);
				result
						.addData("RTN_DOSAGE_UNIT", parm.getValue("UNIT_DESC",
								j));

				result.addData("DISPENSE_QTY", rtnDosageQty);
				result.addData("DISPENSE_UNIT", parm.getValue("UNIT_DESC", j));

				result.addData("PHA_RETN_CODE", Operator.getID());
				result.addData("PHA_RETN_DATE", TJDODBTool.getInstance()
						.getDBTime());
				result.addData("TRANSMIT_RSN_CODE", parm.getValue(
						"TRANSMIT_RSN_CODE", j));
				// zhangyong20100720 begin
				result.addData("VS_DR_CODE", parm.getValue("VS_DR_CODE", j));
				result.addData("ORDER_DR_CODE", parm.getValue("VS_DR_CODE", j));
				result.addData("ORDER_DEPT_CODE", this
						.getValueString("RTN_IND"));
				// zhangyong20100720 end
				//20150710 wangjc add start 写入BATCH_SEQ1按照科室与药品代码在IND_STOCK中查询最大BATCH_SEQ
//				String maxSeqSql = "SELECT MAX(BATCH_SEQ) AS BATCH_SEQ FROM IND_STOCK WHERE ORG_CODE='"
//										+this.getValue("RTN_IND")
//										+ "' AND ORDER_CODE='"
//										+parm.getValue("ORDER_CODE", j)
//										+ "' AND ACTIVE_FLG='Y' "
//										+ " AND VALID_DATE > SYSDATE ";
////				System.out.println(maxSeqSql);
//				TParm maxSeqParm = new TParm(TJDODBTool.getInstance().select(maxSeqSql));
//				result.addData("BATCH_SEQ1", maxSeqParm.getValue("BATCH_SEQ", 0));
				//20150710 wangjc add end

				/**
				 * 新增
				 */
				result.addData("DSPN_USER", Operator.getID());
				result.addData("OPT_USER", Operator.getID());
				result.addData("OPT_TERM", Operator.getIP());
                
                result.addData("RT_KIND", parm.getValue("DSPN_KIND", j));//add by wanglong 20130628
                result.addData("RT_REQTY", rtnDosageQty);
                if (TypeTool.getBoolean(this.getValue("DC_RDBTN"))) {
                    result.addData("RT_TYPE", "DC");
                } else {
                    result.addData("RT_TYPE", "ALL");
                }
                result.addData("PARENT_CASE_NO", caseNo);
                result.addData("PARENT_ORDER_NO", parm.getValue("ORDER_NO", j));
                result.addData("PARENT_ORDER_SEQ", parm.getValue("ORDER_SEQ", j));
                result.addData("PARENT_START_DTTM", parm.getValue("START_DTTM", j));
			}
			// System.out.println("----------------result:"+result);
			result = TIOM_AppServer.executeAction("action.iva.IvaRtnRgsAction",
					"onInsert", result);
			// 2012-2-2 luhai modify 退药逻辑修改 end
		} else {
			for (int i = 0; i < execList.size(); i++) {

				// EXEC;ORDER_SEQ;ORDER_DESC;RTN_DOSAGE_QTY;UNIT_DESC;TRANSMIT_RSN_CODE;CANCEL_DOSAGE_QTY;CANCELRSN_CODE

				int j = (Integer) execList.get(i);
				caseNo = parm.getValue("CASE_NO", j);
				orderSeq = i + 1;
				rtnDosageQty = parm.getDouble("RTN_DOSAGE_QTY", j);
				result.addData("START_DTTM", parm.getValue("START_DTTM", j));
				result.addData("CASE_NO", caseNo);
				result.addData("ORDER_NO", parm.getValue("ORDER_NO", j));
				result.addData("ORDER_SEQ", parm.getData("ORDER_SEQ", j));
				result.addData("ORDER_CODE", parm.getValue("ORDER_CODE", j));
				if (TypeTool.getBoolean(this.getValue("UN_DONE"))) {
					result.addData("ORDER_DATE", orderDate);
				} else {
					result
							.addData("ORDER_DATE", parm.getValue("ORDER_DATE",
									j));
					result
							.addData("START_DTTM", parm.getValue("ORDER_DATE",
									j));
				}

				result.addData("ORDER_DATETIME", orderDateTime);
				result.addData("DC_TOT", rtnDosageQty);
				result
						.addData("EXEC_DEPT_CODE", this
								.getValueString("RTN_IND"));
				result.addData("DOSAGE_QTY", rtnDosageQty);
				result.addData("DOSAGE_UNIT", parm.getValue("UNIT_DESC", j));
				result.addData("RTN_DOSAGE_QTY", rtnDosageQty);
				result.addData("OPT_USER", Operator.getID());
				result.addData("OPT_TERM", Operator.getIP());
				result.addData("TRANSMIT_RSN_CODE", parm.getValue(
						"TRANSMIT_RSN_CODE", j));
			}
			result = TIOM_AppServer.executeAction("action.iva.IvaRtnRgsAction",
					"onUpdate", result);
		}
		if (result.getErrCode() != 0) {
			this.messageBox("E0001");
			// this.messageBox_(result.getErrText());
			return;
		} else {
			this.messageBox("P0001");
			this.onQuery();
			return;
		}

	}

	/**
	 * 检查药品数量合理
	 * 
	 * @param parm
	 * @return
	 */
	public boolean checkOrderData(TParm parm){
    	String caseNo=parm.getValue("CASE_NO");//就诊号
    	String orderCode=parm.getValue("ORDER_CODE");//药品编码
    	String phaDisSql="SELECT SUM(A.DOSAGE_QTY) AS QTY,A.DOSAGE_UNIT,A.ORDER_CODE, " +//wanglong modify 20150129 
    	                                                  //解决相同order_code不同order_desc导致退药登记失败的问题
    			" (SELECT B.ORDER_DESC FROM SYS_FEE B WHERE A.ORDER_CODE = B.ORDER_CODE AND ROWNUM = 1) ORDER_DESC " +
    			" FROM ODI_DSPNM A " +
    			" WHERE A.CASE_NO='"+caseNo+"'" +
    			" AND A.ORDER_CODE='"+orderCode+"'" +
    			" AND A.DSPN_KIND<>'RT' AND A.PHA_DOSAGE_CODE IS NOT NULL" +
    			" AND A.IVA_FLG='Y' "+
    			" GROUP BY A.DOSAGE_UNIT,A.ORDER_CODE";
    	TParm disParm=new TParm(TJDODBTool.getInstance().select(phaDisSql));
    	double qty=0;
    	if(disParm.getCount()<=0){
    		this.messageBox("没有查询到发药信息");
    		return false;
    	}
    	qty=disParm.getDouble("QTY", 0);//已发药数量
    	String phaRenSql="SELECT SUM(RTN_DOSAGE_QTY) AS QTY,RTN_DOSAGE_UNIT,ORDER_CODE " +
		" FROM ODI_DSPNM " +
		" WHERE CASE_NO='"+caseNo+"'" +
		" AND ORDER_CODE='"+orderCode+"'" +
		" AND DSPN_KIND='RT'" +
		" AND IVA_FLG='Y' "+
		" GROUP BY RTN_DOSAGE_UNIT,ORDER_CODE ";
        TParm renParm=new TParm(TJDODBTool.getInstance().select(phaRenSql));
        double renQty=0;
        if(disParm.getCount()>0)
        	renQty=renParm.getDouble("QTY", 0);//已退药登记数量
    	if(qty-(renQty+parm.getDouble("RTN_DOSAGE_QTY"))<0){
    		this.messageBox(disParm.getValue("ORDER_DESC", 0)+"退药数量不能大于发药数量\n 可退药数量"+(qty-renQty));
    		return false;
    	}
    	return true;
    }

	/**
	 * 删除事件
	 */
	public void onDelete() {
		if (!isSave) {
			this.messageBox_("新登记退药单的医嘱不可删除");
			return;
		}
		int row;
		TParm parm;
		// 退药单TABLE点击
		if (0 == upOrDown) {
			row = tblRtn.getSelectedRow();
			parm = tblRtnDtl.getParmValue();
			// System.out.println("0:parm->"+parm);
			for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
				String rtnUser = parm.getValue("PHA_RETN_CODE", i);
				if (!StringUtil.isNullString(rtnUser)) {
					this.messageBox_("已确认的退药单不能修改");
					return;
				}
			}
			TParm result = TIOM_AppServer.executeAction(
					"action.iva.IvaRtnRgsAction", "onDelete", parm);
			if (result.getErrCode() != 0) {
				this.messageBox("E0003");
				return;
			} else {
				this.messageBox("P0003");
				this.onQuery();
				return;
			}
		}
		// 退药明细TABLE点击
		else {
			row = tblRtnDtl.getSelectedRow();
			parm = tblRtnDtl.getParmValue().getRow(row);
			String rtnUser = parm.getValue("PHA_RETN_CODE", 0);
			if (!StringUtil.isNullString(rtnUser)) {
				this.messageBox_("已确认的退药单不能修改");
				return;
			}
			TParm result = TIOM_AppServer.executeAction(
					"action.iva.IvaRtnRgsAction", "onDeleteSingle", parm);
			if (result.getErrCode() != 0) {
				this.messageBox("E0003");
				return;
			} else {
				this.messageBox("P0003");
				this.onQuery();
				return;
			}
		}
	}

	/**
	 * 未完成事件
	 */
	public void onUnDone() {
		this.callFunction("UI|LBL_RT|setVisible", false);
		this.setValue("RTN_NO", "");
		this.callFunction("UI|RTN_NO|setVisible", false);
        this.callFunction("UI|DC_RDBTN|setEnabled", true);// add by wanglong 20130628
        this.callFunction("UI|ALL_RDBTN|setEnabled", true);
        this.callFunction("UI|DC_RDBTN|setSelected", true);
	}

	/**
	 * 完成事件
	 */
	public void onDone() {
		this.callFunction("UI|LBL_RT|setVisible", true);
		this.setValue("RTN_NO", "");
		this.callFunction("UI|RTN_NO|setVisible", true);
        this.callFunction("UI|DC_RDBTN|setEnabled", false);// add by wanglong 20130628
        this.callFunction("UI|ALL_RDBTN|setEnabled", false);
        this.callFunction("UI|ALL_RDBTN|setSelected", true);
	}

	/**
	 * 护士站、病案号、病床号RADIO点击事件
	 */
	public void onRegion() {
		boolean isBed = TypeTool.getBoolean(this.getValue("BED"));
		if (isBed) {
			this.callFunction("UI|BED_NO|setVisible", true);
			this.callFunction("UI|NO|setVisible", false);
		} else {
			this.callFunction("UI|BED_NO|setVisible", false);
			this.callFunction("UI|NO|setVisible", true);
		}
	}

	/**
	 * 病床号选择事件
	 */
	public void onBedNo() {
		onQuery();
	}

	/**
	 * 退药单号查询事件
	 */
	public void onRtnNo() {
		String rtnNo = this.getValueString("RTN_NO");
		// ===========pangben modify 20110516 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ===========pangben modify 20110516 stop

		String sql = "SELECT DISTINCT 'Y' AS EXEC,A.BED_NO,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO,A.CASE_NO,A.RTN_NO,A.RTN_NO_SEQ"
				+ "	FROM ODI_DSPNM A, SYS_PATINFO B,SYS_BED C"
				+ "	WHERE A.CASE_NO=C.CASE_NO"
				+ "		  AND A.MR_NO=B.MR_NO"
				+ "		  AND A.RTN_NO='" + rtnNo + "'" + region;
		// System.out.println("rtnNo=========" + rtnNo);

		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		tblPat.setParmValue(parm);
		tblRtnDtl.removeRowAll();
		tblRtn.removeRowAll();
		execList = new ArrayList();
		isEdit = false;
		isSave = false;
		upOrDown = 0;
	}

	/**
	 * 退药明细TABLE点击事件
	 */
	public void onClickRtnDtl() {
		upOrDown = 1;
	}

	/**
	 * 病案号回车事件
	 */
	public void onMrNoAction() {
		String mr_no = PatTool.getInstance().checkMrno(getValueString("NO"));
		this.setValue("NO", mr_no);
		Pat pat = Pat.onQueryByMrNo(mr_no);
		//modify by huangtt 20160930 EMPI患者查重提示  start

		if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
	            this.messageBox("病案号" + mr_no + " 已合并至 " + "" + pat.getMrNo());
	            setValue("NOs", pat.getMrNo());
	    }
		//modify by huangtt 20160930 EMPI患者查重提示  end
		
		this.setValue("NAME", pat.getName());
		onQuery();
	}

	/**
	 * 医疗卡读卡 2012-2-27 luhai
	 */
	public void onEKT() {
		TParm parm = EKTIO.getInstance().TXreadEKT();
		// System.out.println("parm==="+parm);
		if (null == parm || parm.getValue("MR_NO").length() <= 0) {
			this.messageBox("请查看医疗卡是否正确使用");
			return;
		}
		// zhangp 20120130
		if (parm.getErrCode() < 0) {
			messageBox(parm.getErrText());
		}
		setValue("NO", parm.getValue("MR_NO"));
		
		//modify by huangtt 20160930 EMPI患者查重提示  start
		String mrNo = parm.getValue("MR_NO");
		Pat pat = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
	            this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
	            setValue("NO", pat.getMrNo());
	    }
		this.setValue("NAME", pat.getName());
		//modify by huangtt 20160930 EMPI患者查重提示  end
		
		TRadioButton td = (TRadioButton) this.getComponent("MR");
		td.setSelected(true);
		this.onQuery();
		// 修改读医疗卡功能 end luhai 2012-2-27
	}
	/**
	 * 取消登记-退药登记的药品，如果未进行退药确认则可以取消登记操作  add by duzhw 20130918
	 */
	public void cancelReturnRegist() {
		boolean flag = false;		//是否选中退药明细选项
		boolean success = false;	//是否成功取消退药登记
		if (TypeTool.getBoolean(this.getValue("UN_DONE"))) {//未完成 页面提示取消按钮不可用
			messageBox("请在	'已完成'页面取消退药登记记录！");
			return;
		}else {
			if(tblRtnDtl.getSelectedRow() < 0){
				messageBox("请选择退药明细！");
				return;
			}
			for (int i = 0; i < tblRtnDtl.getRowCount(); i++) {
				//System.out.println("是否选中："+tblRtnDtl.getValueAt(i, 0));
				String result = (String)tblRtnDtl.getValueAt(i, 0);
				if("Y".equals(result)){
					String phaRetnCode = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("PHA_RETN_CODE", i)); 
					if(!"".equals(phaRetnCode)){
						messageBox("已经退药确认，不能取消退药登记操作！");
						return;
					}
					String caseNo = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("CASE_NO", i));
					String prtNo = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("RTN_NO", i));
					String regionCode = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("REGION_CODE", i));
					String dspnKind = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("DSPN_KIND", i));
					String orderNo = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("ORDER_NO", i));
					String orderSeq = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("ORDER_SEQ", i));
					double rtReqty = TCM_Transform.getDouble(tblRtnDtl.getParmValue().getValue("DISPENSE_QTY", i));		//退药数量
					String parentOrderNo = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("PARENT_ORDER_NO", i));
					String parentOrderSeq = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("PARENT_ORDER_SEQ", i));
					String stationCode = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("STATION_CODE", i));
					String rtKind = TCM_Transform.getString(tblRtnDtl.getParmValue().getValue("RT_KIND", i));
//					System.out.println("退药明细点选 caseNo="+caseNo+"  prtNo="+prtNo+" regionCode="+regionCode+" dspnKind="+dspnKind+"  orderSeq="+orderSeq
//							+" orderNo="+orderNo+" rtReqty="+rtReqty+" parentOrderNo="+parentOrderNo+"  parentOrderSeq="+parentOrderSeq+" stationCode="+stationCode+" rtKind="+rtKind);
					
					//判断是否经过退药确认操作-大官人检测出的非正常操作bug
					String sql_flag = confirmODISql( caseNo, orderNo, orderSeq, parentOrderNo, 
							prtNo, dspnKind, regionCode, stationCode);
					TParm result_flag = new TParm(TJDODBTool.getInstance().select(sql_flag.toString()));
					if(result_flag.getCount("CASE_NO") > 0){
						messageBox("已经完成退药确认操作，不能取消退药，请刷新！");
						return;
					}
					
					boolean DC_flag = false;
					if(!"".equals(parentOrderNo) && (parentOrderNo != null)){
						DC_flag = true;
					}
					if(DC_flag){//UD医嘱 
						//修改退药数量
						String sql2 = getUpdateODISql(caseNo, rtReqty, parentOrderNo, parentOrderSeq, regionCode, stationCode);
						TParm result2 = new TParm(TJDODBTool.getInstance().update(sql2.toString()));
						if(result2.getErrCode()==0) {
							//删除取消的退药记录信息
							String sql3 = getDeleteODISql(DC_flag, caseNo, orderNo, orderSeq, parentOrderNo, 
									prtNo, dspnKind, regionCode, stationCode);
							TParm result3 = new TParm(TJDODBTool.getInstance().update(sql3.toString()));
							if(result3.getErrCode()==0) {
								success = true;
							}
						}
						
					}else {//全部医嘱
						//删除取消的退药记录信息
						String sql4 = getDeleteODISql(DC_flag, caseNo, orderNo, orderSeq, parentOrderNo, 
								prtNo, dspnKind, regionCode, stationCode);
						TParm result4 = new TParm(TJDODBTool.getInstance().update(sql4.toString()));
						if(result4.getErrCode()==0) {
							success = true;
						}
					}
					flag = true;
				}
				
				//tblRtnDtl.setValueAt(false, i, 0);
			}
			if(!flag){
				messageBox("请选择退药明细！");
				return;
			}
			if(success){
				messageBox("成功取消退药登记！");
			}
			//重新查询操作，更新数据
			onQuery();
			
		}
		
	}
	
	
	//取消退药登记修改退药数量sql
	public String getUpdateODISql(String caseNo, double num, String parentOrderNo, String parentOrderSeq, String regionCode, String stationCode){
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE ODI_DSPNM SET RT_REQTY = RT_REQTY -").append(num)
			.append(" WHERE CASE_NO = '").append(caseNo).append("' AND ORDER_NO = '")
			.append(parentOrderNo).append("' AND ORDER_SEQ = '").append(parentOrderSeq).append("' AND PHA_DOSAGE_DATE IS NOT NULL")
			.append(" AND DC_DATE IS NOT NULL AND (DSPN_KIND = 'F' OR DSPN_KIND = 'UD')")
			.append(" AND (DOSAGE_QTY >= RT_REQTY OR RT_REQTY IS NULL)")
			.append(" AND ORDER_CAT1_CODE IN ('PHA_W', 'PHA_C')")
			.append(" AND STATION_CODE = '").append(stationCode).append("' AND REGION_CODE = '").append(regionCode).append("'");
		//System.out.println("修改操作sql--->"+sql.toString());
		return sql.toString();
	} 
	
	//取消退药登记删除退药记录sql
	public String getDeleteODISql (boolean DC_flag, String caseNo, String orderNo, String orderSeq, String parentOrderNo, 
			String prtNo,String dspnKind, String regionCode, String stationCode) {
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE FROM ODI_DSPNM WHERE CASE_NO = '")
				.append(caseNo).append("' AND ORDER_NO = '").append(orderNo).append("' ");
		if(DC_flag){//UD医嘱
			sql.append(" AND PARENT_ORDER_NO = '").append(parentOrderNo).append("'");
		}else{//全部医嘱
			sql.append(" AND (PARENT_ORDER_NO IS NULL OR PARENT_ORDER_NO = '') ");
		}
		sql.append(" AND ORDER_SEQ = '").append(orderSeq).append("' AND STATION_CODE = '").append(stationCode)
				.append("' AND REGION_CODE = '").append(regionCode).append("' ")
				.append(" AND RTN_NO = '").append(prtNo).append("' AND DSPN_KIND = '").append(dspnKind).append("'");
		
		//System.out.println("删除操作sql--->"+sql.toString());
		return sql.toString();
	}
	//判断退药数据是否确认
	public String confirmODISql (String caseNo, String orderNo, String orderSeq, String parentOrderNo, 
			String prtNo,String dspnKind, String regionCode, String stationCode) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT CASE_NO FROM ODI_DSPNM WHERE CASE_NO = '")
				.append(caseNo).append("' AND ORDER_NO = '").append(orderNo).append("' ")
				.append(" AND ORDER_SEQ = '").append(orderSeq).append("' AND STATION_CODE = '").append(stationCode)
				.append("' AND REGION_CODE = '").append(regionCode).append("' ")
				.append(" AND RTN_NO = '").append(prtNo).append("' AND DSPN_KIND = '").append(dspnKind).append("'")
				.append(" AND PHA_RETN_CODE IS NOT NULL AND PHA_RETN_DATE IS NOT NULL ");
		
		//System.out.println("判断退药数据是否确认sql--->"+sql.toString());
		return sql.toString();
	}
}
