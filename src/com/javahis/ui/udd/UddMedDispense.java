package com.javahis.ui.udd;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import jdo.adm.ADMInpTool;
import jdo.bil.BILComparator;
import jdo.bil.BILTool;
import jdo.ekt.EKTIO;
import jdo.ind.INDTool;
import jdo.inw.InwOrderExecTool;
import jdo.opd.TotQtyTool;
import jdo.pha.TXNewATCTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSPhaFreqTool;
import jdo.sys.SysPhaBarTool;
import jdo.sys.SystemTool;
import jdo.udd.UDDTool;
import jdo.udd.UddChnCheckTool;
import jdo.udd.UddDispatchTool;
import action.udd.client.SpcOdiDspnm;
import action.udd.client.SpcOdiDspnms;
import action.udd.client.SpcOdiService_SpcOdiServiceImplPort_Client;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: סԺҩ����ҩ��ҩ
 * </p>
 * 
 * <p>
 * Description: סԺҩ����ҩ��ҩ
 * </p>
 * 
 * <p>
 * Copyright: javahis 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ehui
 * @version 1.0
 */
public class UddMedDispense extends TControl {
	public static final String Y = "Y";
	public static final String N = "N";
	public static final String NULL = "";
	private TTable tblPat;
	private TTable tblMed;
	private TTable tblDtl;
	private TTable tblSht;
	private List execList;
	private TParm saveParm;
	private String startTime;
	private Timestamp schDateFrom;
	// liuyalin 20170505 ���� add start
	private BILComparator compare = new BILComparator();
	private boolean ascending = false;
	private int sortColumn = -1;
	// liuyalin 20170505 ���� add end
	// ��ҩ��ϸ������
	private int detailCount = 0;
	// ��ҩ��ϸ��ӡ����
	private int printCount = 0;
	// private static final String INIT_PAT_SQL =
	// "SELECT 'N' AS EXEC,A.CASE_NO,A.BED_NO,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO, A.PHA_DISPENSE_NO "
	// + " FROM ODI_DSPNM A , SYS_PATINFO B ,SYS_BED C "
	// + " WHERE B.MR_NO=A.MR_NO "
	// + " AND C.BED_NO=A.BED_NO      "
	// + " AND (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG='Y') "
	// +
	// " AND (C.BED_OCCU_FLG  IS  NULL OR C.BED_OCCU_FLG='N')      AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')      AND A.DISPENSE_FLG='N'";
	private boolean isDosage;
	private String controlName;
	private String charge;
	private boolean isCheckNeeded;
	private TTextFormat queryBed;
	private TParm sysparm;

	public UddMedDispense() {
		execList = new ArrayList();
		saveParm = new TParm();
	}

	public void onInitParameter() {
		sysparm = new TParm(TJDODBTool.getInstance().select(
		"SELECT * FROM ODI_SYSPARM"));
		startTime = sysparm.getValue("START_TIME", 0);
		String date = (new StringBuilder())
		.append(StringTool.getString(TJDODBTool.getInstance()
				.getDBTime(), "yyyyMMdd")).append("000000").toString();
		List dateparm = TotQtyTool.getInstance().getDispenseDttmArrange(
				date.substring(0, 12));
		schDateFrom = StringTool.getTimestamp(
				TCM_Transform.getString(dateparm.get(0)), "yyyyMMddHHmm");
		controlName = (new StringBuilder()).append(getParameter()).append("")
				.toString();
		// ===zhangp 20120809 start
		String sql = "SELECT CTRL_FLG FROM SYS_OPERATOR WHERE USER_ID = '"
				+ Operator.getID() + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		TComboBox pha_ctrlcodeCombo = (TComboBox) getComponent("PHA_CTRLCODE");
		if (!parm.getValue("CTRL_FLG", 0).equals("Y")) {
			pha_ctrlcodeCombo.setEnabled(false);
		}
		// ===zhangp 20120809 end
		if ("DOSAGE".equalsIgnoreCase(controlName)) {
			setTitle("����ҩ����");
			// �����Ͱ�ҩ��ѡ��
			callFunction("UI|ALLATCDO|setVisible", true);
			this.callFunction("UI|ATC_MACHINENO_L|setVisible", true);

			this.callFunction("UI|ATC_MACHINENO|setVisible", true);

			this.callFunction("UI|ATC_TYPE_L|setVisible", true);

			this.callFunction("UI|ATC_TYPE|setVisible", true);
		} else {
			setTitle("����ҩ��ҩ");
			this.callFunction("UI|ATC|setVisible", false);
		}
	}

	public void onInit() {
		super.onInit();
		callFunction("UI|TBL_PAT|addEventListener", new Object[] {
				"table.checkBoxClicked", this, "onTableCheckBoxClicked" });
		callFunction("UI|TBL_DTL|addEventListener", new Object[] {
				"table.checkBoxClicked", this, "onTable2CheckBoxClicked" });
		// �������tableע��CHECK_BOX_CLICKED��������¼�
//		this.callFunction("UI|TBL_DTL|addEventListener",
//				TTableEvent.CHECK_BOX_CLICKED, this,
//				"onDownTableCheckBoxChangeValue");
		//˫���¼�
		this.callFunction("UI|TBL_DTL|addEventListener",
				TTableEvent.DOUBLE_CLICKED, this,
				"onDoubleClickUpdateBatchNo");
		// // ���tableע��CHECK_BOX_CLICKED��������¼�
		// this.callFunction("UI|TBL_PAT|addEventListener",
		// TTableEvent.CHECK_BOX_CLICKED, this,
		// "onPatTableCheckBoxChangeValue");
		isDosage = TypeTool.getBoolean(TConfig.getSystemValue("IS_DOSAGE"));
		isCheckNeeded = TypeTool.getBoolean(TConfig.getSystemValue("IS_CHECK"));
		tblPat = (TTable) getComponent("TBL_PAT");
		tblMed = (TTable) getComponent("TBL_MED");
		tblDtl = (TTable) getComponent("TBL_DTL");
		tblDtl.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onDownTableCheckBoxChangeValue");
		if (!"DOSAGE".equalsIgnoreCase(controlName)) {
			tblDtl.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17");
		}
		tblSht = (TTable) getComponent("TBL_SHT");
		queryBed = (TTextFormat) getComponent("QUERY_BED");
		charge = TConfig.getSystemValue("CHARGE_POINT");
		// 20170505 liuyalin ���� add
		addListener(getTable("TBL_PAT"));
		// 20170505 liuyalin add end
		onClear();
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		java.sql.Timestamp t = TJDODBTool.getInstance().getDBTime();
		setValue("START_DATE", t);
		setValue("END_DATE", StringTool.rollDate(t, 1L));
		setValue("ST", "Y");
		setValue("EXEC_DEPT_CODE", Operator.getDept());
		setValue("AGENCY_ORG_CODE", "");
		setValue("STA", "Y");
		setValue("STATION", "");
		setValue("UNCHECK", "Y");
		setValue("NO", "");
		setValue("NAME", "");
		// setValue("DOSE", "N");
		// TTextFormat cblDose = (TTextFormat) getComponent("CBL_DOSE");
		// setValue("CBL_DOSE", "");
		// cblDose.setEnabled(false);
		setValue("PHA_DISPENSE_NO", "");
		setValue("UNDONE", "Y");
		setValue("PPLT", "");
		setValue("EXEC_ALL", Boolean.valueOf(false));
		setValue("QUERY_BED", "");
		this.setValue("ATC_MACHINENO", "");
		this.setValue("ATC_TYPE", "");
		queryBed.setVisible(false);
		callFunction("UI|NO|setVisible", new Object[] { Boolean.valueOf(true) });
		//fux modify 20150818
//		tblPat.removeRowAll();
//		tblDtl.removeRowAll();
//		tblMed.removeRowAll();
//		tblSht.removeRowAll();
		TParm parmNull = new TParm();
		tblPat.setParmValue(parmNull);
		tblDtl.setParmValue(parmNull);
		tblMed.setParmValue(parmNull);
		tblSht.setParmValue(parmNull);
		if (TypeTool.getBoolean(getValue("UNCHECK"))) {
			callFunction("UI|save|setEnabled", new Object[] { Boolean
					.valueOf(true) });
			callFunction("UI|delete|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			callFunction("UI|dispense|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			callFunction("UI|CONFIRM_BUT|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			//20170228 liuyalin add
			callFunction("UI|ALL_BUT|setEnabled", new Object[] { Boolean
					.valueOf(false) });
		} else {
			callFunction("UI|save|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			callFunction("UI|delete|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			callFunction("UI|CONFIRM_BUT|setEnabled", new Object[] { Boolean
					.valueOf(true) });
			callFunction("UI|dispense|setEnabled", new Object[] { Boolean
					.valueOf(true) });
			//20170228 liuyalin add
			callFunction("UI|ALL_BUT|setEnabled", new Object[] { Boolean
					.valueOf(false) });
		}

		this.setValue("DOSE_TYPEALL", "Y");
		this.setValue("DOSE_TYPEO", "Y");
		this.setValue("DOSE_TYPEE", "Y");
		this.setValue("DOSE_TYPEI", "Y");
		this.setValue("DOSE_TYPEF", "Y");
		
		this.setValue("PS_FLG", "N");
		
		this.setValue("LINK_NO", "N");
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		String getDoseType = "";
		List list = new ArrayList();
		if ("Y".equals(this.getValueString("DOSE_TYPEO"))) {
			list.add("O");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEE"))) {
			list.add("E");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEI"))) {
			list.add("I");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEF"))) {
			list.add("F");
		}
		if (list == null || list.size() == 0) {
			this.messageBox("��ѡ����ͷ���");
			return;
		} else {
			getDoseType = " AND F.CLASSIFY_TYPE IN (";
			for (int i = 0; i < list.size(); i++) {
				getDoseType = getDoseType + "'" + list.get(i) + "' ,";
			}
			getDoseType = getDoseType.substring(0, getDoseType.length() - 1)
					+ ")";
		}
		if (TypeTool.getBoolean(getValue("UNCHECK"))) {
			callFunction("UI|save|setEnabled", new Object[] { Boolean
					.valueOf(true) });
			callFunction("UI|delete|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			callFunction("UI|dispense|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			callFunction("UI|CONFIRM_BUT|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			//20170228 liuyalin add
			callFunction("UI|ALL_BUT|setEnabled", new Object[] { Boolean
					.valueOf(false) });
		} else {
			callFunction("UI|save|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			callFunction("UI|delete|setEnabled", new Object[] { Boolean
					.valueOf(false) });
			callFunction("UI|CONFIRM_BUT|setEnabled", new Object[] { Boolean
					.valueOf(true) });
			callFunction("UI|dispense|setEnabled", new Object[] { Boolean
					.valueOf(true) });
			//20170228 liuyalin add
			callFunction("UI|ALL_BUT|setEnabled", new Object[] { Boolean
					.valueOf(true) });
		}
		//20150405 wangjingchun add start
		String sqlStart = "SELECT 'N' AS EXEC,A.CASE_NO,A.BED_NO,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO, A.PHA_DISPENSE_NO, "
							+ " A.PHA_DOSAGE_CODE, A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE,G.USER_NAME AS USER_NAME1 "
							+ " FROM ODI_DSPNM A , SYS_PATINFO B ,SYS_BED C, SYS_PHAROUTE F,SYS_OPERATOR G "
							+ " WHERE B.MR_NO=A.MR_NO "
							+ " AND C.BED_NO=A.BED_NO "
							+ " AND A.ORDER_DR_CODE=G.USER_ID "
							+ " AND A.ROUTE_CODE=F.ROUTE_CODE "
							+ " AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C') AND A.DISPENSE_FLG='N' ";
		//20150405 wangjingchun add end
		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT 'N' AS EXEC,A.CASE_NO,A.BED_NO,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO, A.PHA_DISPENSE_NO, "
//						+ "A.PHA_DOSAGE_CODE, A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE,G.USER_NAME AS USER_NAME1 "
//						+ "FROM ODI_DSPNM A , SYS_PATINFO B ,SYS_BED C, SYS_PHAROUTE F,SYS_OPERATOR G "
//						+ "WHERE B.MR_NO=A.MR_NO "
//						+ "AND C.BED_NO=A.BED_NO "
//						+ "AND A.ORDER_DR_CODE=G.USER_ID "
//						+ "AND A.ROUTE_CODE=F.ROUTE_CODE "
//						// +
//						// " AND (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG='Y') "
//						// +
//						// "  AND (C.BED_OCCU_FLG IS  NULL OR C.BED_OCCU_FLG='N') "
//						+ " AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C') AND A.DISPENSE_FLG='N'")
//				.append(getWhere()).append(getDoseType);
		sql.append(sqlStart).append(getWhere()).append(getDoseType);
		String sqlStr = sql.toString();
		if (!"".equals(getValueString("DGT_NO")))
			sqlStr = (new StringBuilder()).append(sqlStr).append(
					" AND A.PHA_DISPENSE_NO = '").append(
					getValueString("DGT_NO")).append("'").toString();
		// ===========pangben modify 20110511 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			sqlStr += " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ===========pangben modify 20110511 stop
		// ====zhangp 20121115 start
//		if ("Y".equals(this.getValueString("LINK_NO"))
//				|| "Y".equals(this.getValueString("STATION_PZ"))) {
//			sqlStr += " AND A.LINK_NO IS NOT NULL AND F.IVA_FLG='Y' ";//20150319 wangjingchun modify
//		}
		//20150319 wangjingchun add start
		//Ƥ��
		if ("Y".equals(this.getValueString("PS_FLG"))) {
			sqlStr += " AND F.PS_FLG='Y' ";
		}
		//PIVAs����
		if("Y".equals(this.getValueString("LINK_NO"))){
			sqlStr += " AND A.LINK_NO IS NOT NULL AND A.IVA_FLG='Y' ";
		}else{
			sqlStr +=" AND (A.IVA_FLG IS NULL OR A.IVA_FLG = 'N') ";
		}
		//���ӿ�������ѡ�񣬹������Ҳ�ѯ��
		if(!this.getValueString("ORDER_DEPT_CODE").equals("")){
			sqlStr += " AND A.ORDER_DEPT_CODE='"+this.getValueString("ORDER_DEPT_CODE")+"' ";
		}
		//20150319 wangjingchun add end
		/*****************update by liyh ���ӷ�ҩ���� 20130603 start********************/
		//ȫԺ��0��סԺ��2��������ҩ��1
/*	    if (getRadioButton("IN_HOSPITAL").isSelected()) {//סԺ��ҩ
	    	sqlStr += " AND A.TAKEMED_ORG='2' ";
		}
	    if (getRadioButton("IN_STATION").isSelected()) {//������ҩ
	    	sqlStr += " AND A.TAKEMED_ORG='1' ";
		}*/
	    /*****************update by liyh ���ӷ�ҩ���� 20130603 end********************/	
		// ====zhangp 20121115 end
		sqlStr = (new StringBuilder())
				.append(sqlStr)
				// .append(" GROUP BY A.CASE_NO,A.BED_NO,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO,A.PHA_DISPENSE_NO,A.PHA_DOSAGE_CODE, A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE ORDER BY A.BED_NO,A.CASE_NO, A.PHA_DISPENSE_NO")
				.append(
						" GROUP BY A.CASE_NO,A.BED_NO,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO,A.PHA_DISPENSE_NO,"
								+ "A.PHA_DOSAGE_CODE, A.PHA_DOSAGE_DATE, A.PHA_DISPENSE_CODE, A.PHA_DISPENSE_DATE,G.USER_NAME "
								+ " ORDER BY A.BED_NO,A.CASE_NO, A.PHA_DISPENSE_NO")
				.toString();

//		System.out.println("��ҩ��ѯsqlStr---" + sqlStr);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sqlStr));
		tblPat.setParmValue(parm);
		if (StringTool.getBoolean(getValueString("MR"))
				|| StringTool.getBoolean(getValueString("BED")))
			setValue("NAME", parm.getValue("PAT_NAME", 0));
		tblPat.setParmValue(parm);
		Map map=new HashMap();
		if(parm.getCount()>0){
			for(int i=0;i<parm.getCount();i++){
				TParm parmRow=parm.getRow(i);
				map.put(parmRow.getValue("MR_NO"), parmRow.getValue("MR_NO"));
			}
			this.setValue("SUM_TOT", ""+map.size());
		}else{
			this.setValue("SUM_TOT",""+0);
		}
		onQueryDtl();
		onQueryMed();
	}

	/**
	 * ��CASE_NO����ҩƷ����ʼ��ͳҩ��TABLE
	 */
	public void onQueryMed() {
		String startDate = "TO_DATE('"
			+ StringTool.getString(schDateFrom, "yyyyMMdd") + startTime
			+ "','YYYYMMDDHH24MI')";
		String doseType = getDoseType();
		if ("".equals(doseType)) {
			return;
		}
		TParm parm = new TParm();
		// ===========pangben modify 20110511 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ====zhangp 20120803 start
		String tables = "";
		String conditions = "";
		String pha_ctrlcode = getValueString("PHA_CTRLCODE");
		if (!pha_ctrlcode.equals("")) {
			tables = " , PHA_BASE H, SYS_CTRLDRUGCLASS I ";
			conditions = " AND C.ORDER_CODE = H.ORDER_CODE"
					+ " AND H.CTRLDRUGCLASS_CODE = I.CTRLDRUGCLASS_CODE"
					+ " AND I.CTRL_FLG = '" + pha_ctrlcode + "'";
		}
		// ====zhangp 20120803 end
		// ===zhangp 20121115 start
		if ("Y".equals(this.getValueString("LINK_NO"))) {
			conditions += " AND A.LINK_NO IS NOT NULL ";
		}
		/*****************update by liyh ���ӷ�ҩ���� 20130603 start********************/
		//ȫԺ��0��סԺ��2��������ҩ��1
	    String dispenseOrgCodeSql = " ";
	    if (getRadioButton("IN_HOSPITAL_ONE").isSelected()) {//סԺ��ҩ
	    	dispenseOrgCodeSql = " AND A.TAKEMED_ORG='2' ";
		}
	    if (getRadioButton("IN_STATION_ONE").isSelected()) {//������ҩ
	    	dispenseOrgCodeSql = " AND A.TAKEMED_ORG='1' ";
		}
	    /*****************update by liyh ���ӷ�ҩ���� 20130603 end********************/		
//		if (TypeTool.getBoolean(getValue("ST"))) {
//			conditions += " AND A.TAKEMED_ORG = '2' ";
//		}
		// ===zhangp 20121115 end
		// ===========pangben modify 20110511 stop
	  //20150405 wangjingchun add start
	    //��ҩ�� ���������ƻ�PIVAs����
	    String piavsSql = "";
	    if("Y".equals(this.getValueString("LINK_NO"))){
	    	piavsSql += " AND A.IVA_FLG='Y' ";
	    }else if("N".equals(this.getValueString("LINK_NO"))){
	    	piavsSql += " AND (A.IVA_FLG IS NULL OR A.IVA_FLG='N') ";
	    }
//	    //20170329 lij add start*******
//	    String ownPriceSql = "";
//	    String ownAmtSql = "";
//	    if (TypeTool.getBoolean(getValue("UNCHECK"))) {
//	    	ownPriceSql = " CASE WHEN B.SERVICE_LEVEL = 2 THEN C.OWN_PRICE2 WHEN B.SERVICE_LEVEL = 3 THEN C.OWN_PRICE3 ELSE C.OWN_PRICE END AS OWN_PRICE, ";
//	    }else{
//	    	ownPriceSql = " A.OWN_PRICE, ";
//	    }
//	    if(TypeTool.getBoolean(getValue("UNCHECK"))){
//	    	ownAmtSql = " CASE WHEN B.SERVICE_LEVEL = 2 THEN SUM (DOSAGE_QTY * C.OWN_PRICE2) WHEN B.SERVICE_LEVEL = 3 THEN SUM (DOSAGE_QTY * C.OWN_PRICE3) ELSE SUM (DOSAGE_QTY * C.OWN_PRICE) END AS OWN_AMT, ";
//	    }else{
//	    	ownAmtSql = " A.OWN_AMT, ";
//	    }
//	    //20170329 lij add end******
	    //20150405 wangjingchun add end
		if (this.getRadioButton("BY_ORDER").isSelected()) {
			// ��ҩƷ��ʾͳҩ��
			String sql = (new StringBuilder())
					.append(
							"  SELECT A.ORDER_DESC || ' ' || C.SPECIFICATION AS ORDER_DESC, "// ¬��2012-4-6
							// ɾ����Ʒ��
									// A.GOODS_DESC
									// ||
									+ "SUM( A.DOSAGE_QTY) AS DISPENSE_QTY,A.DOSAGE_UNIT AS DISPENSE_UNIT, "
//									+ "SUM("+dosageSql+") AS DISPENSE_QTY,A.DOSAGE_UNIT AS DISPENSE_UNIT, "
//									+ "CASE WHEN B.SERVICE_LEVEL = 2 THEN C.OWN_PRICE2 WHEN B.SERVICE_LEVEL = 3 THEN C.OWN_PRICE3 ELSE C.OWN_PRICE END AS OWN_PRICE, "
//									+ ownPriceSql  
									+ " A.OWN_PRICE, "//20170329 lij �� 
									+ "A.ORDER_CODE ,  "
									+ " SUM(A.TOT_AMT) AS OWN_AMT, " //20170329 lij ��
//									+ ownAmtSql 
//									+ "CASE WHEN B.SERVICE_LEVEL = 2 THEN SUM (DOSAGE_QTY * C.OWN_PRICE2) WHEN B.SERVICE_LEVEL = 3 THEN SUM (DOSAGE_QTY * C.OWN_PRICE3) ELSE SUM (DOSAGE_QTY * C.OWN_PRICE) END AS OWN_AMT, " 
//									+ "CASE WHEN B.SERVICE_LEVEL = 2 THEN SUM ("+ dosageSql+" * C.OWN_PRICE2) WHEN B.SERVICE_LEVEL = 3 THEN SUM ("+ dosageSql+" * C.OWN_PRICE3) ELSE SUM ("+ dosageSql+" * C.OWN_PRICE) END AS OWN_AMT ," 
//									+" CASE WHEN A.TAKEMED_ORG=2 THEN 'סԺҩ��' ELSE '����' END AS  TAKEMED_ORG "
									+ " A.TAKEMED_ORG " 
									// ===zhangp 20120802 start
									// + 
									// "FROM   ODI_DSPNM A, ADM_INP B ,SYS_FEE C, SYS_PATINFO D, SYS_PHAROUTE F  WHERE   A.CASE_NO = B.CASE_NO   AND A.MR_NO = D.MR_NO AND A.ROUTE_CODE=F.ROUTE_CODE AND A.CASE_NO IN (")
									+ "FROM   ODI_DSPNM A, ADM_INP B ,SYS_FEE C, SYS_PATINFO D, SYS_PHAROUTE F  "
									+ tables
									+ "WHERE   A.CASE_NO = B.CASE_NO   AND A.MR_NO = D.MR_NO AND A.ROUTE_CODE=F.ROUTE_CODE "
									+ piavsSql//20150405 wangjingchun add
									+ conditions + "AND A.CASE_NO IN ("
									)
					.append(getCaseNos())
					.append(")")
					.append("  AND A.ORDER_CODE=C.ORDER_CODE ")
					// BY liyh 20120905 ���˵��Ѿ�ͣҽ����ҩƷ
					.append(
							"  AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C') ")
					.append("  AND A.DISPENSE_FLG='N' #").append("  AND #")
					//update by liyh ���ӷ�ҩ���� 20130603
					.append(dispenseOrgCodeSql)
					.toString();
			// ===zhangp 20120802 end
			//20170329 lij �� start******
			String group_by = "";
//			if (TypeTool.getBoolean(getValue("UNCHECK"))) {
//				group_by = " GROUP BY A.ORDER_CODE, A.ORDER_DESC, A.DOSAGE_UNIT, "
//						+ "C.OWN_PRICE,A.GOODS_DESC,B.SERVICE_LEVEL, C.OWN_PRICE2, "
//						+ "C.OWN_PRICE3,C.SPECIFICATION ,A.TAKEMED_ORG "
//						//20150405 wangjingchun add
////					+ ",A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM,A.END_DTTM "
//						+ "ORDER BY A.TAKEMED_ORG,A.ORDER_CODE ";
//			}else{
				group_by = " GROUP BY A.ORDER_CODE, A.ORDER_DESC, A.DOSAGE_UNIT, "
						+ "A.OWN_PRICE,A.GOODS_DESC, "
						+ "C.SPECIFICATION ,A.TAKEMED_ORG "
						+ "ORDER BY A.TAKEMED_ORG,A.ORDER_CODE ";
//			}
			//20170329 lij �� end******
			// ===zhangp 20121115 start
//			if ("Y".equals(this.getValueString("LINK_NO"))) {
//				group_by = " GROUP BY A.ORDER_CODE, A.ORDER_DESC, A.DOSAGE_UNIT, "
//						+ "C.OWN_PRICE,A.GOODS_DESC,B.SERVICE_LEVEL, C.OWN_PRICE2, "
//						+ "C.OWN_PRICE3,C.SPECIFICATION,"
////						+ "A.LINK_NO,A.LINKMAIN_FLG ,"
//						+ "A.TAKEMED_ORG "
//						//20150405 wangjingchun add
////						+ ",A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM,A.END_DTTM "
//						+ "ORDER BY  A.TAKEMED_ORG,A.ORDER_CODE ASC ";
////						+ "A.LINK_NO ASC , A.LINKMAIN_FLG DESC ";
//			}
				//20170330 lij ��
			if ("Y".equals(this.getValueString("LINK_NO"))) {
				group_by = " GROUP BY A.ORDER_CODE, A.ORDER_DESC, A.DOSAGE_UNIT, "
						+ "A.OWN_PRICE,A.TOT_AMT,A.GOODS_DESC, "
						+ "C.SPECIFICATION,"
						+ "A.TAKEMED_ORG "
						+ "ORDER BY  A.TAKEMED_ORG,A.ORDER_CODE ASC ";
			}
			// ===zhangp 20121115 end
			if (TypeTool.getBoolean(getValue("ST"))){
				//20151106 WANGJC ADD
				if(!this.getValueString("ORDER_DEPT_CODE").equals("")){
					sql = sql.replaceFirst("#",
							" AND A.DSPN_KIND='OP' ");
				}else{
					sql = sql.replaceFirst("#",
							" AND (A.DSPN_KIND='ST' OR A.DSPN_KIND='F' OR A.DSPN_KIND='OP') ");
				}
			}else if (TypeTool.getBoolean(getValue("UD"))){
				sql = sql.replaceFirst("#", " AND A.DSPN_KIND='UD'");
			}else{
				sql = sql.replaceFirst("#", " AND A.DSPN_KIND='DS'");
			}
			if (TypeTool.getBoolean(getValue("UNCHECK"))) {
				if ("DOSAGE".equalsIgnoreCase(controlName)) {
					sql = sql
							.replaceFirst(
									"#",
									"A.PHA_DOSAGE_CODE IS NULL AND A.PHA_CHECK_CODE IS NOT NULL AND (A.DC_DATE IS NULL OR A.DC_DATE >="+startDate+") AND A.DC_NS_CHECK_DATE IS NULL");
					// String bed_no = getBedNo();
					// if (!"".equals(bed_no) && !"''".equals(bed_no)) {
					// sql += "  AND A.BED_NO IN (" + bed_no + ") ";
					// }
				} else {
					sql = sql
							.replaceFirst(
									"#",
									"A.PHA_DISPENSE_CODE IS NULL AND A.PHA_DOSAGE_CODE IS NOT NULL AND (A.DC_DATE IS NULL OR A.DC_DATE >="+startDate+") AND A.DC_NS_CHECK_DATE IS NULL");
					String dispense_no = getPhaDispenseNo();
					if (!"".equals(dispense_no) && !"''".equals(dispense_no))
						sql = (new StringBuilder()).append(sql).append(
								"  AND A.PHA_DISPENSE_NO IN (").append(
								dispense_no).append(")").toString();
				}
			} else if ("DOSAGE".equalsIgnoreCase(controlName)) {
				sql = sql
						.replaceFirst(
								"#",
								(new StringBuilder())
										.append(
												"A.PHA_DOSAGE_CODE IS NOT NULL  AND (A.PHA_DOSAGE_DATE IS NULL OR (A.PHA_DOSAGE_DATE>=TO_DATE('")
										.append(
												StringTool
														.getString(
																TypeTool
																		.getTimestamp(getValue("START_DATE")),
																"yyyyMMdd"))
										.append(
												"','YYYYMMDD') AND A.PHA_DOSAGE_DATE<=TO_DATE('")
										.append(
												StringTool
														.getString(
																TypeTool
																		.getTimestamp(getValue("END_DATE")),
																"yyyyMMdd"))
										.append("','YYYYMMDD')))").toString());
				String dispense_no = getPhaDispenseNo();
				if (!"".equals(dispense_no) && !"''".equals(dispense_no))
					sql = (new StringBuilder()).append(sql).append(
							"  AND A.PHA_DISPENSE_NO IN (").append(dispense_no)
							.append(")").toString();
			} else {
				sql = sql
						.replaceFirst(
								"#",
								(new StringBuilder())
										.append(
												"A.PHA_DISPENSE_CODE IS NOT NULL AND (A.PHA_DISPENSE_DATE IS NULL OR (A.PHA_DISPENSE_DATE>=TO_DATE('")
										.append(
												StringTool
														.getString(
																TypeTool
																		.getTimestamp(getValue("START_DATE")),
																"yyyyMMdd"))
										.append(
												"','YYYYMMDD') AND A.PHA_DISPENSE_DATE<=TO_DATE('")
										.append(
												StringTool
														.getString(
																TypeTool
																		.getTimestamp(getValue("END_DATE")),
																"yyyyMMdd"))
										.append("','YYYYMMDD')))").toString());
				String dispense_no = getPhaDispenseNo();
				if (!"".equals(dispense_no) && !"''".equals(dispense_no))
					sql = (new StringBuilder()).append(sql).append(
							"  AND A.PHA_DISPENSE_NO IN (").append(dispense_no)
							.append(")").toString();
			}
			// ���ͷ���
			sql = sql + doseType + region;
            if ("DOSAGE".equalsIgnoreCase(controlName)) {// wanglong add 20140725 ���˵�Ϊ��ע��ҽ�������ֶ���Ĭ��ֵ�������ʾ�Ǳ�ע��
                sql = (new StringBuilder()).append(sql).append(" AND (C.IS_REMARK <> 'Y' OR C.IS_REMARK IS NULL) ").toString();
            }
//			 System.out.println("sqlsssss:"+(sql + group_by));
//			 System.out.println("��ҩƷ��ʾͳҩ��====" + sql + group_by);
			parm = new TParm(TJDODBTool.getInstance().select(sql + group_by));
			// ========================= modify by chenxi 20120704 ����ҩƷ��ɫ�仯
			Color blue = new Color(0, 0, 255);
			TTable table = (TTable) this.getComponent("TBL_MED");
			for (int i = 0; i < parm.getCount(); i++) {
				String orderCode = parm.getValue("ORDER_CODE", i);
				String parmsql = "SELECT ORDER_CODE,DRUG_NOTES_DR FROM SYS_FEE WHERE ORDER_CODE = '"
						+ orderCode + "' ";
				TParm sqlParm = new TParm(TJDODBTool.getInstance().select(
						parmsql));
				sqlParm = sqlParm.getRow(0);
				if (sqlParm.getValue("DRUG_NOTES_DR").length() != 0)
					table.setRowTextColor(i, blue);
				// ================ chenxi modigy 20120704
			}

		} else {
			// ��������ʾͳҩ��
			String sql = (new StringBuilder())
					.append(
							" SELECT A.ORDER_DESC || '  ' ||  C.SPECIFICATION AS ORDER_DESC,"// A.GOODS_DESC
							// ||
									// ¬��
									// ɾ����Ʒ��
									// 2012-04-06
									+ "SUM( DOSAGE_QTY) AS DISPENSE_QTY,DOSAGE_UNIT AS DISPENSE_UNIT,  "
//									+ dosageSql+" AS DISPENSE_QTY,DOSAGE_UNIT AS DISPENSE_UNIT,  "
//									+ "CASE WHEN B.SERVICE_LEVEL = 2 THEN C.OWN_PRICE2 WHEN B.SERVICE_LEVEL = 3 THEN C.OWN_PRICE3 ELSE C.OWN_PRICE END AS OWN_PRICE, "
//									+ ownPriceSql  
									+ " A.OWN_PRICE, "//20170329 lij �� 
									+ "A.CASE_NO,  A.ORDER_CODE ,  "
									+ " A.TOT_AMT AS OWN_AMT, "//20170329 lij �� 
//									+ ownAmtSql 
//									+ "CASE WHEN B.SERVICE_LEVEL = 2 THEN SUM (DOSAGE_QTY * C.OWN_PRICE2) WHEN B.SERVICE_LEVEL = 3 THEN SUM (DOSAGE_QTY * C.OWN_PRICE3) ELSE SUM (DOSAGE_QTY * C.OWN_PRICE) END AS OWN_AMT, "
//									+ "CASE WHEN B.SERVICE_LEVEL = 2 THEN SUM ("+ dosageSql+" * C.OWN_PRICE2) WHEN B.SERVICE_LEVEL = 3 THEN SUM ("+ dosageSql+" * C.OWN_PRICE3) ELSE SUM ("+ dosageSql+" * C.OWN_PRICE) END AS OWN_AMT, "
									+ "A.BED_NO, A.MR_NO, D.PAT_NAME   "
									+ "FROM   ODI_DSPNM A, ADM_INP B ,SYS_FEE C, SYS_PATINFO D, SYS_PHAROUTE F  WHERE   A.CASE_NO = B.CASE_NO   AND A.MR_NO = D.MR_NO AND A.ROUTE_CODE=F.ROUTE_CODE "
									+ piavsSql//20150405 wangjingchun add
									+ "AND A.CASE_NO IN (")
					.append(getCaseNos())
					.append(")")
					.append(
							"  AND A.ORDER_CODE=C.ORDER_CODE AND (A.DC_DATE IS NULL OR A.DC_DATE >="+startDate+") ")
					// BY liyh 20120905 ���˵��Ѿ�ͣҽ����ҩƷ
					.append(
							"  AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C') ")
					.append("  AND A.DISPENSE_FLG='N' #").append("  AND #")
					//update by liyh ���ӷ�ҩ���� 20130603
					.append(dispenseOrgCodeSql)
					.toString();
			//20150406 wangjingchun modify
			//20170329 lij �� start******
			String group_by = "";
//			if (TypeTool.getBoolean(getValue("UNCHECK"))) {
//				group_by = " GROUP BY A.ORDER_CODE, A.ORDER_DESC, A.DOSAGE_UNIT, C.OWN_PRICE,A.CASE_NO,A.GOODS_DESC, A.BED_NO, A.MR_NO, D.PAT_NAME, B.SERVICE_LEVEL, C.OWN_PRICE2, C.OWN_PRICE3,C.SPECIFICATION, A.ORDER_DATE,A.ORDER_NO,A.ORDER_SEQ,"
//						+ "A.START_DTTM,A.END_DTTM ORDER BY A.MR_NO,A.ORDER_DATE, A.BED_NO";
//			}else{
				group_by = " GROUP BY A.ORDER_CODE, A.ORDER_DESC, A.DOSAGE_UNIT, A.OWN_PRICE,A.TOT_AMT,A.CASE_NO,A.GOODS_DESC, A.BED_NO, A.MR_NO, D.PAT_NAME,C.SPECIFICATION, A.ORDER_DATE,A.ORDER_NO,A.ORDER_SEQ,"
						+ "A.START_DTTM,A.END_DTTM ORDER BY A.MR_NO,A.ORDER_DATE, A.BED_NO";
//			}
			//20170329 lij �� end******
			// ===zhangp 20121115 start
//			if ("Y".equals(this.getValueString("LINK_NO"))) {
////				group_by = " GROUP BY A.ORDER_CODE, A.ORDER_DESC, A.DOSAGE_UNIT, C.OWN_PRICE,A.CASE_NO,A.GOODS_DESC, A.BED_NO, A.MR_NO, D.PAT_NAME, B.SERVICE_LEVEL, C.OWN_PRICE2, C.OWN_PRICE3,C.SPECIFICATION, A.ORDER_DATE ORDER BY A.MR_NO,A.ORDER_DATE,A.LINK_NO , A.LINKMAIN_FLG ORDER BY A.BED_NO ASC, A.LINK_NO ASC , A.LINKMAIN_FLG DESC";
////				group_by += " ASC, A.LINK_NO ASC , A.LINKMAIN_FLG DESC ";
//				//20150406 wangjingchun modify
//				group_by = " GROUP BY A.ORDER_CODE, A.ORDER_DESC, A.DOSAGE_UNIT, "
//						+ "C.OWN_PRICE,A.CASE_NO,A.GOODS_DESC, A.BED_NO, A.MR_NO, "
//						+ "D.PAT_NAME, B.SERVICE_LEVEL, C.OWN_PRICE2, C.OWN_PRICE3,"
//						+ "C.SPECIFICATION, A.ORDER_DATE,A.ORDER_NO,A.ORDER_SEQ,"
//						+ "A.START_DTTM,A.END_DTTM ORDER BY A.MR_NO,"
//						+ "A.ORDER_DATE,A.BED_NO ASC ";
//			}
				//20170330 lij ��
			if ("Y".equals(this.getValueString("LINK_NO"))) {
				group_by = " GROUP BY A.ORDER_CODE, A.ORDER_DESC, A.DOSAGE_UNIT, "
						+ "A.OWN_PRICE,A.CASE_NO,A.GOODS_DESC, A.BED_NO, A.MR_NO, "
						+ "D.PAT_NAME, A.TOT_AMT"
						+ "C.SPECIFICATION, A.ORDER_DATE,A.ORDER_NO,A.ORDER_SEQ,"
						+ "A.START_DTTM,A.END_DTTM ORDER BY A.MR_NO,"
						+ "A.ORDER_DATE,A.BED_NO ASC ";
			}
			// ===zhangp 20121115 end
			if (TypeTool.getBoolean(getValue("ST")))
				sql = sql.replaceFirst("#",
						" AND (A.DSPN_KIND='ST' OR A.DSPN_KIND='F') ");
			else if (TypeTool.getBoolean(getValue("UD")))
				sql = sql.replaceFirst("#", " AND A.DSPN_KIND='UD'");
			else
				sql = sql.replaceFirst("#", " AND A.DSPN_KIND='DS'");
			if (TypeTool.getBoolean(getValue("UNCHECK"))) {
				if ("DOSAGE".equalsIgnoreCase(controlName)) {
					sql = sql
							.replaceFirst(
									"#",
									"A.PHA_DOSAGE_CODE IS NULL AND A.PHA_CHECK_CODE IS NULL AND (A.DC_DATE IS NULL OR A.DC_DATE >="+startDate+") AND A.DC_NS_CHECK_DATE IS NULL ");
				} else {
					sql = sql
							.replaceFirst(
									"#",
									"A.PHA_DISPENSE_CODE IS  NULL AND A.PHA_DOSAGE_CODE IS NOT NULL AND (A.DC_DATE IS NULL OR A.DC_DATE >="+startDate+") AND A.DC_NS_CHECK_DATE IS NULL ");
					String dispense_no = getPhaDispenseNo();
					if (!"".equals(dispense_no) && !"''".equals(dispense_no))
						sql = (new StringBuilder()).append(sql).append(
								"  AND A.PHA_DISPENSE_NO IN (").append(
								dispense_no).append(")").toString();
				}
			} else if ("DOSAGE".equalsIgnoreCase(controlName)) {
				sql = sql
						.replaceFirst(
								"#",
								(new StringBuilder())
										.append(
												"A.PHA_DOSAGE_CODE IS NOT NULL  AND (A.PHA_DOSAGE_DATE IS NULL OR (A.PHA_DOSAGE_DATE>=TO_DATE('")
										.append(
												StringTool
														.getString(
																TypeTool
																		.getTimestamp(getValue("START_DATE")),
																"yyyyMMdd"))
										.append(
												"','YYYYMMDD') AND A.PHA_DOSAGE_DATE<=TO_DATE('")
										.append(
												StringTool
														.getString(
																TypeTool
																		.getTimestamp(getValue("END_DATE")),
																"yyyyMMdd"))
										.append("','YYYYMMDD')))").toString());
				String dispense_no = getPhaDispenseNo();
				if (!"".equals(dispense_no) && !"''".equals(dispense_no))
					sql = (new StringBuilder()).append(sql).append(
							"  AND A.PHA_DISPENSE_NO IN (").append(dispense_no)
							.append(")").toString();
			} else {
				sql = sql
						.replaceFirst(
								"#",
								(new StringBuilder())
										.append(
												"A.PHA_DISPENSE_CODE IS NOT NULL AND (A.PHA_DISPENSE_DATE IS NULL OR (A.PHA_DISPENSE_DATE>=TO_DATE('")
										.append(
												StringTool
														.getString(
																TypeTool
																		.getTimestamp(getValue("START_DATE")),
																"yyyyMMdd"))
										.append(
												"','YYYYMMDD') AND A.PHA_DISPENSE_DATE<=TO_DATE('")
										.append(
												StringTool
														.getString(
																TypeTool
																		.getTimestamp(getValue("END_DATE")),
																"yyyyMMdd"))
										.append("','YYYYMMDD')))").toString());
			}
			// ���ͷ���
			sql = sql + doseType + region;
//			 System.out.println("��������ʾͳҩ��====" + sql + group_by);
			parm = new TParm(TJDODBTool.getInstance().select(
					sql + " " + group_by));
		}
		if (parm.getErrCode() != 0) {
			return;
		} else {
			tblMed.removeRowAll();
			tblMed.setParmValue(parm);
			return;
		}
	}

	// ******************************************
	// add by chenxi 20120704
	// *****************************************
	/**
	 * ״̬����ʾҩƷ��ʾ����
	 */
	public void MedtableClick() {
		TTable table = (TTable) this.getComponent("TBL_MED");
		TParm action = table.getParmValue();
		int row = table.getSelectedRow();
		String orderCode = action.getValue("ORDER_CODE", row);
		String sql = " SELECT ORDER_CODE,ORDER_DESC,GOODS_DESC,"
				+ "DESCRIPTION,SPECIFICATION,REMARK_1,REMARK_2,DRUG_NOTES_DR FROM SYS_FEE"
				+ " WHERE ORDER_CODE = '" + orderCode + "'";
		TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql));
		sqlparm = sqlparm.getRow(0);
		// ״̬����ʾҽ����ʾ
		callFunction("UI|setSysStatus", sqlparm.getValue("ORDER_CODE") + " "
				+ sqlparm.getValue("ORDER_DESC") + " "
				+ sqlparm.getValue("GOODS_DESC") + " "
				+ sqlparm.getValue("DESCRIPTION") + " "
				+ sqlparm.getValue("SPECIFICATION") + " "
				+ sqlparm.getValue("REMARK_1") + " "
				+ sqlparm.getValue("REMARK_2") + " "
				+ sqlparm.getValue("DRUG_NOTES_DR"));

	}

	// **************************************
	// chenxi add 20120704
	// *************************************

	/**
	 * ҽ�ƿ����� luhai 2012-2-27
	 */
	public void onEKT() {
		TParm parm = EKTIO.getInstance().TXreadEKT();
		// System.out.println("parm==="+parm);
		if (null == parm || parm.getValue("MR_NO").length() <= 0) {
			this.messageBox("��鿴ҽ�ƿ��Ƿ���ȷʹ��");
			return;
		}
		// zhangp 20120130
		if (parm.getErrCode() < 0) {
			messageBox(parm.getErrText());
		}
		setValue("NO", parm.getValue("MR_NO"));
		TRadioButton tb = (TRadioButton) this.getComponent("MR");
		tb.setSelected(true);
		this.onQuery();
		// �޸Ķ�ҽ�ƿ����� end luhai 2012-2-27
	}

	/**
	 * ����ҩƷϸ��
	 */
	public void onQueryDtl() {
		String startDate = "TO_DATE('"
			+ StringTool.getString(schDateFrom, "yyyyMMdd") + startTime
			+ "','YYYYMMDDHH24MI')";
		String doseType = getDoseType();
		if ("".equals(doseType)) {
			return;
		}
		// ====zhangp 20120803 start
		String tables = "";
		String conditions = "";
		// ===zhangp 20121118 start
		String orderBy = " ORDER BY A.TAKEMED_ORG,A.BED_NO ASC, B.MR_NO ASC,B.PAT_NAME ASC,A.CASE_NO ASC, A.ORDER_NO ASC,A.ORDER_SEQ ASC, A.LINKMAIN_FLG DESC ";
		// ===zhangp 20121118 end
		String pha_ctrlcode = getValueString("PHA_CTRLCODE");
		if (!pha_ctrlcode.equals("")) {
			tables = " , SYS_CTRLDRUGCLASS I ";
			conditions = " AND G.CTRLDRUGCLASS_CODE = I.CTRLDRUGCLASS_CODE"
					+ " AND I.CTRL_FLG = '" + pha_ctrlcode + "'";
		}
		// ====zhangp 20121115 start
		if ("Y".equals(this.getValueString("LINK_NO"))) {
			conditions += " AND A.LINK_NO IS NOT NULL ";
			orderBy = " ORDER BY A.TAKEMED_ORG,A.BED_NO ASC, B.MR_NO ASC,B.PAT_NAME ASC,A.CASE_NO ASC, A.ORDER_NO ASC,A.ORDER_SEQ ASC, A.LINK_NO ASC , A.LINKMAIN_FLG DESC ";
		}
		/*****************update by liyh ���ӷ�ҩ���� 20130603 start********************/
		//ȫԺ��0��סԺ��2��������ҩ��1
	    String dispenseOrgCodeSql = " ";
	    if (getRadioButton("IN_HOSPITAL_TWO").isSelected()) {//סԺ��ҩ
	    	dispenseOrgCodeSql = " AND A.TAKEMED_ORG='2' ";
		}
	    if (getRadioButton("IN_STATION_TWO").isSelected()) {//������ҩ
	    	dispenseOrgCodeSql = " AND A.TAKEMED_ORG='1' ";
		}
	    /*****************update by liyh ���ӷ�ҩ���� 20130603 end********************/
	  //20150405 wangjingchun add start
	    //��ҩ�� ���������ƻ�PIVAs����
	    String pivasSql = "";
	    if("Y".equals(this.getValueString("LINK_NO"))){
	    	pivasSql += " AND A.IVA_FLG='Y' ";
	    }else if("N".equals(this.getValueString("LINK_NO"))){
	    	pivasSql += " AND (A.IVA_FLG IS NULL OR A.IVA_FLG='N') ";
	    }
	  //�������ҚH��ѯ��������������
	    if(!this.getValueString("ORDER_DEPT_CODE").equals("")){
	    	pivasSql += " AND A.ORDER_DEPT_CODE='"+this.getValueString("ORDER_DEPT_CODE")+"' ";
	    }
	    //20170329 lij add start
//	    String ownPriceSql = "";
//	    String ownAmtSql = "";
//	    if (TypeTool.getBoolean(getValue("UNCHECK"))) {
//	    	ownPriceSql = " CASE WHEN B.SERVICE_LEVEL = 2 THEN C.OWN_PRICE2 WHEN B.SERVICE_LEVEL = 3 THEN C.OWN_PRICE3 ELSE C.OWN_PRICE END AS OWN_PRICE, ";
//	    }else{
//	    	ownPriceSql = " A.OWN_PRICE, ";
//	    }
//	    if(TypeTool.getBoolean(getValue("UNCHECK"))){
//	    	ownAmtSql = " CASE WHEN B.SERVICE_LEVEL = 2 THEN SUM (DOSAGE_QTY * C.OWN_PRICE2) WHEN B.SERVICE_LEVEL = 3 THEN SUM (DOSAGE_QTY * C.OWN_PRICE3) ELSE SUM (DOSAGE_QTY * C.OWN_PRICE) END AS OWN_AMT, ";
//	    }else{
//	    	ownAmtSql = " A.OWN_AMT, ";
//	    }
	    //20170329 lij add end
	    //20150405 wangjingchun add end
		String sql = (new StringBuilder())
				.append(
						"SELECT 'Y' AS EXEC,'N' AS SENDATC_FLG,B.PAT_NAME, A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,"
								+ "A.START_DTTM,A.END_DTTM,A.REGION_CODE,A.STATION_CODE,A.DEPT_CODE,"
								+ "A.VS_DR_CODE,A.BED_NO,A.IPD_NO,A.MR_NO,DSPN_KIND,A.CAT1_TYPE,"
								+ "A.DSPN_DATE,A.DSPN_USER,A.DISPENSE_EFF_DATE,A.DISPENSE_END_DATE,A.EXEC_DEPT_CODE,"
								+ "A.AGENCY_ORG_CODE,A.RX_NO,A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_CODE,"
								+ "A.ORDER_DESC || ' (' || A.SPECIFICATION || ')' ORDER_DESC,A.GOODS_DESC,A.SPECIFICATION,A.MEDI_QTY,A.MEDI_UNIT,"
								+ "A.FREQ_CODE,A.ROUTE_CODE,A.TAKE_DAYS,A.DOSAGE_QTY,A.DOSAGE_UNIT,"
//								+ "A.FREQ_CODE,A.ROUTE_CODE,A.TAKE_DAYS,"+dosageSql+" AS DOSAGE_QTY,A.DOSAGE_UNIT,"
								+ "A.DISPENSE_QTY,A.DISPENSE_UNIT,A.GIVEBOX_FLG,"
								+ "A.DISCOUNT_RATE,TOT_AMT,A.ORDER_DATE,A.ORDER_DEPT_CODE,"
								+ "A.ORDER_DR_CODE,A.DR_NOTE,A.ATC_FLG,A.SENDATC_FLG,A.SENDATC_DTTM,"
								+ "A.INJPRAC_GROUP,A.DC_DATE,A.DC_TOT,A.RTN_NO,A.RTN_NO_SEQ,"
								+ "A.RTN_DOSAGE_QTY,A.RTN_DOSAGE_UNIT,A.CANCEL_DOSAGE_QTY,A.CANCELRSN_CODE,A.TRANSMIT_RSN_CODE,"
								+ "A.PHA_RETN_CODE,A.PHA_RETN_DATE,A.PHA_CHECK_CODE,A.PHA_CHECK_DATE,A.PHA_DISPENSE_NO,"
								+ "A.PHA_DOSAGE_CODE,A.PHA_DOSAGE_DATE,A.PHA_DISPENSE_CODE,A.PHA_DISPENSE_DATE,A.NS_EXEC_CODE,"
								+ "A.NS_EXEC_DATE,A.NS_EXEC_DC_CODE,A.NS_EXEC_DC_DATE,A.NS_USER,A.CTRLDRUGCLASS_CODE,"
								+ "A.PHA_TYPE,A.DOSE_TYPE,A.DCTAGENT_CODE,A.DCTEXCEP_CODE,A.DCT_TAKE_QTY,"
								+ "A.PACKAGE_AMT,A.DCTAGENT_FLG,A.DECOCT_CODE,A.URGENT_FLG,A.SETMAIN_FLG,"
								+ "A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,A.RPTTYPE_CODE,A.OPTITEM_CODE,A.HIDE_FLG,"
								+ "A.DEGREE_CODE,BILL_FLG,A.CASHIER_USER,A.CASHIER_DATE,A.IBS_CASE_NO_SEQ,"
								+ "A.IBS_SEQ_NO,A.ORDER_CAT1_CODE ,'"
								+ Operator.getID()
								+ "' AS OPT_USER ,'"
								+ Operator.getIP()
								+ "' AS OPT_TERM ,"// SHIBL 20120404
//								+ ownPriceSql 
								+ " A.OWN_PRICE, "//20170329 lij ��
//								+ "CASE WHEN E.SERVICE_LEVEL=2 THEN C.OWN_PRICE2 WHEN E.SERVICE_LEVEL=3 THEN C.OWN_PRICE3 ELSE C.OWN_PRICE END AS OWN_PRICE ,"
//								+ "CASE WHEN E.SERVICE_LEVEL=2 THEN "+dosageSql+" * C.OWN_PRICE2 WHEN E.SERVICE_LEVEL=3 THEN "+dosageSql+" * C.OWN_PRICE3 ELSE "+dosageSql+" * C.OWN_PRICE END AS OWN_AMT, "
//								+ "D.BED_NO_DESC,D.ROOM_CODE,E.SERVICE_LEVEL, G.STOCK_PRICE * "+dosageSql+" AS COST_AMT,F.CLASSIFY_TYPE, "// luhai
//								+ "CASE WHEN E.SERVICE_LEVEL=2 THEN A.DOSAGE_QTY * C.OWN_PRICE2 WHEN E.SERVICE_LEVEL=3 THEN A.DOSAGE_QTY * C.OWN_PRICE3 ELSE A.DOSAGE_QTY * C.OWN_PRICE END AS OWN_AMT, "
//								+ ownAmtSql 
								+ " A.TOT_AMT AS OWN_AMT, "//20170329 lij ��
								+ "D.BED_NO_DESC,D.ROOM_CODE,E.SERVICE_LEVEL, G.STOCK_PRICE * A.DOSAGE_QTY AS COST_AMT,F.CLASSIFY_TYPE, "// luhai
								// //
								// add
								// //
								// F.CLASSIFY_TYPE
								+ " TO_CHAR(A.ORDER_DATE,'YYYY/MM/DD HH24:MI') AS ORDER_DATE_SHOW,TO_CHAR(A.PHA_CHECK_DATE,'YYYY/MM/DD HH24:MI') AS PHA_CHECK_DATE_SHOW,TO_CHAR(PHA_DOSAGE_DATE,'YYYY/MM/DD HH24:MI:SS')AS PHA_DOSAGE_DATE_SHOW,"
								// ==============add by lx
								// 2012/06/04��Ժ��ҩ��IBS_ORDD���ӳ�Ժ��ҩע���ֶΣ��ڼƷѵ㴫��start================$$//

								+ "CASE WHEN DSPN_KIND='DS' THEN 'Y' ELSE 'N' END AS DS_FLG,(CASE WHEN TO_CHAR(sysdate,'YYYY/MM/DD') >= TO_CHAR(DC_DATE,'YYYY/MM/DD') THEN TO_CHAR(DC_DATE,'YYYY/MM/DD HH24:MI') ELSE '' END) AS DC_DATE_SHOW,TO_CHAR(A.DC_NS_CHECK_DATE,'YYYY/MM/DD HH24:MI') AS DC_NS_CHECK_DATE_SHOW,A.INTGMED_NO" // SHIBL

//								+ ",CASE WHEN A.TAKEMED_ORG=2 THEN 'סԺҩ��' ELSE '����' END AS  TAKEMED_ORG " // SHIBL
								+ ",A.TAKEMED_ORG,CASE WHEN (A.GIVEBOX_FLG = 'N' OR A.GIVEBOX_FLG IS NULL) AND C.ATC_FLG_I = 'Y'  THEN 'Y'  ELSE 'N' END AS SHOULD_SEND_ATC_FLG " // add by wangb 2016/2/29 Ӧ�Ͱ�ҩ��
								// ADD
								// 20120928
								// DCʱ��
								// ==============add by lx
								// 2012/06/04��Ժ��ҩ��IBS_ORDD���ӳ�Ժ��ҩע���ֶΣ��ڼƷѵ㴫��end================$$//
								+ "FROM ODI_DSPNM A,SYS_PATINFO B,SYS_FEE C, SYS_BED D, ADM_INP E, SYS_PHAROUTE F, PHA_BASE G "
								// ===ZHANGP 20120806 START
								+ tables
								// ===ZHANGP 20120806 END
								+ "WHERE "
								+ " A.CASE_NO IN (")
				.append(getCaseNos())
				.append(") "+pivasSql+" AND B.MR_NO=A.MR_NO AND A.ROUTE_CODE=F.ROUTE_CODE ")//20150507 wangjc modify
				.append("  AND A.ORDER_CODE=C.ORDER_CODE ")
				//
				.append(
						"  AND A.BED_NO =  D.BED_NO AND A.CASE_NO = E.CASE_NO AND A.ORDER_CODE = G.ORDER_CODE ")
				.append("  AND A.DISPENSE_FLG='N'")
				// ===zhangp 20120806 start
				.append(conditions)
				// ===zhangp 20120806 end
				.append(
						"  AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C') ")
				// update by liyh 20130603 ���ӷ�ҩ ����
			    .append(dispenseOrgCodeSql)
				.append("  AND #").toString();
		/* end update by guoyi 20120504 for ƿǩ���ֵ��� */
		// luhai 2012-5-2 modify �����б���뿪��ʱ�����Ϣ end
		if (TypeTool.getBoolean(getValue("ST"))){
			//20151106 WANGJC ADD
			if(!this.getValueString("ORDER_DEPT_CODE").equals("")){
				sql = (new StringBuilder()).append(sql).append(
						" AND A.DSPN_KIND='OP' ").toString();
			}else{
				sql = (new StringBuilder()).append(sql).append(
						" AND (A.DSPN_KIND='ST' OR A.DSPN_KIND='F' OR A.DSPN_KIND='OP')").toString();
			}
//			sql = (new StringBuilder()).append(sql).append(
//					" AND (A.DSPN_KIND='ST' OR A.DSPN_KIND='F')").toString();
		}else if (TypeTool.getBoolean(getValue("UD"))){
			sql = (new StringBuilder()).append(sql).append(
					" AND A.DSPN_KIND='UD'").toString();
		}else{
			sql = (new StringBuilder()).append(sql).append(
					" AND A.DSPN_KIND='DS'").toString();
		}
		// ===========pangben modify 20110511 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ===========pangben modify 20110511 stop

		// ���ͷ���
		sql = sql + doseType + region;
		if (TypeTool.getBoolean(getValue("UNCHECK"))) {
			if ("DOSAGE".equalsIgnoreCase(controlName)) {
				sql = sql
						.replaceFirst(
								"#",
								"A.PHA_DOSAGE_CODE IS NULL AND A.PHA_CHECK_CODE IS NOT NULL AND (A.DC_DATE IS NULL OR A.DC_DATE >="+startDate+") AND A.DC_NS_CHECK_DATE IS NULL ");// SHBL
			} else {
				sql = sql
						.replaceFirst(
								"#",
								"A.PHA_DISPENSE_CODE IS  NULL AND A.PHA_DOSAGE_CODE IS NOT NULL AND (A.DC_DATE IS NULL OR A.DC_DATE >="+startDate+")  AND A.DC_NS_CHECK_DATE IS NULL ");// SHBL
				// 20120927  ADD  dcҽ������
				String dispense_no = getPhaDispenseNo();
				if (!"".equals(dispense_no) && !"''".equals(dispense_no)) {
					sql += "  AND A.PHA_DISPENSE_NO IN (" + dispense_no + ") ";
				}
			}
		} else if ("DOSAGE".equalsIgnoreCase(controlName)) {
			sql = sql
					.replaceFirst(
							"#",
							(new StringBuilder())
									.append(
											"A.PHA_DOSAGE_CODE IS NOT NULL  AND (A.PHA_DOSAGE_DATE IS NULL OR (A.PHA_DOSAGE_DATE>=TO_DATE('")
									.append(
											StringTool
													.getString(
															TypeTool
																	.getTimestamp(getValue("START_DATE")),
															"yyyyMMdd"))
									.append(
											"','YYYYMMDD') AND A.PHA_DOSAGE_DATE<=TO_DATE('")
									.append(
											StringTool
													.getString(
															TypeTool
																	.getTimestamp(getValue("END_DATE")),
															"yyyyMMdd"))
									.append("','YYYYMMDD')))").toString());
			String dispense_no = getPhaDispenseNo();
			if (!"".equals(dispense_no) && !"''".equals(dispense_no))
				sql = (new StringBuilder()).append(sql).append(
						"  AND A.PHA_DISPENSE_NO IN (").append(dispense_no)
						.append(")").toString();
		} else {
			sql = sql
					.replaceFirst(
							"#",
							(new StringBuilder())
									.append(
											"A.PHA_DISPENSE_CODE IS NOT NULL AND (A.PHA_DISPENSE_DATE IS NULL OR (A.PHA_DISPENSE_DATE>=TO_DATE('")
									.append(
											StringTool
													.getString(
															TypeTool
																	.getTimestamp(getValue("START_DATE")),
															"yyyyMMdd"))
									.append(
											"','YYYYMMDD') AND A.PHA_DISPENSE_DATE<=TO_DATE('")
									.append(
											StringTool
													.getString(
															TypeTool
																	.getTimestamp(getValue("END_DATE")),
															"yyyyMMdd"))
									.append("','YYYYMMDD')))").toString());
			String dispense_no = getPhaDispenseNo();
			if (!"".equals(dispense_no) && !"''".equals(dispense_no))
				sql = (new StringBuilder()).append(sql).append(
						"  AND A.PHA_DISPENSE_NO IN (").append(dispense_no)
						.append(")").toString();
		}
        if ("DOSAGE".equalsIgnoreCase(controlName)) {//wanglong add 20140725 ���˵�Ϊ��ע��ҽ�������ֶ���Ĭ��ֵ�������ʾ�Ǳ�ע��
            sql = (new StringBuilder()).append(sql).append(" AND (C.IS_REMARK <> 'Y' OR C.IS_REMARK IS NULL) ").toString();
        }
		// ====zhangp 20121115 start
		sql = (new StringBuilder()).append(sql).append(orderBy)// A.MR_NO,
		// ====zhangp 20121115 end
				// ����B.MR_NO,
				// 20120407
				.toString();
//		System.out.println("��ҩ�б��ѯsql��" + sql);
		saveParm = new TParm(TJDODBTool.getInstance().select(sql));
		//�ϲ��汾===pangben 2015-4-3 �����ʾ����
		for(int i=0;i<saveParm.getCount();i++) {
			String dspnkind = saveParm.getValue("DSPN_KIND",i);
			String orderCode = saveParm.getValue("ORDER_CODE",i);
			String caseNo = saveParm.getValue("CASE_NO",i); 
			if(TypeTool.getBoolean(getValue("ST"))&&TypeTool.getBoolean(getValue("UNCHECK"))&&"DOSAGE".equalsIgnoreCase(controlName)){                      //��ʱ������
				if("Y".equals(Operator.getSpcFlg())) {//��������ѯ����
					TParm parmIn = new TParm();
					parmIn.setData("ORDER_CODE",orderCode);
					parmIn.setData("ORG_CODE", this.getValueString("EXEC_DEPT_CODE"));
					TParm result = TIOM_AppServer.executeAction("action.udd.UddAction",
							"getBatchNo", parmIn);
					String batchNo = result.getValue("BATCH_NO");
					//String batchNo=UDDTool.getInstance().getBatchNo(orderCode, this.getValueString("EXEC_DEPT_CODE"));
					saveParm.setData("BATCH_NO",i,batchNo);	    
				} else {
					//alert by wukai on 20170412
					String search="SELECT BATCH_NO FROM PHA_ANTI WHERE ORDER_CODE='"+orderCode+"' AND CASE_NO='"+caseNo+"' AND BATCH_NO IS NOT NULL ORDER BY ORDER_DATE DESC";
					TParm searchParm = new TParm(TJDODBTool.getInstance().select(search));
					if(searchParm.getCount("BATCH_NO") <= 0) {
						search = "SELECT A.BATCH_NO FROM IND_STOCK A,PHA_BASE B WHERE A.ORDER_CODE=B.ORDER_CODE AND SYSDATE < A.VALID_DATE AND B.ANTIBIOTIC_CODE IS NOT NULL AND SKINTEST_FLG='Y' AND A.STOCK_QTY>0 AND A.ORG_CODE='"+
								this.getValueString("EXEC_DEPT_CODE")+"' AND A.ORDER_CODE='"+orderCode+"' ORDER BY A.VALID_DATE DESC";
						searchParm = new TParm(TJDODBTool.getInstance().select(search));
								saveParm.setData("BATCH_NO", i, searchParm.getValue("BATCH_NO", 0) );	
						
					}
					saveParm.setData("BATCH_NO", i, searchParm.getValue("BATCH_NO",0) );		
					//alert by wukai on 20170412
					
					
				}
				if(saveParm.getValue("BATCH_NO",0)==null||"".equals(saveParm.getValue("BATCH_NO",0))) {
					tblDtl.setLockCell(i, 23, true);
				}				
				
			}
			if(TypeTool.getBoolean(getValue("ST"))&&TypeTool.getBoolean(getValue("UNCHECK"))&&"DISPENSE".equalsIgnoreCase(controlName)){                      //����
				String search=" SELECT BATCH_NO FROM PHA_ANTI WHERE ORDER_CODE='"+orderCode+"' AND CASE_NO='"+caseNo+"' AND BATCH_NO IS NOT NULL ORDER BY ORDER_DATE DESC";
				TParm searchParm = new TParm(TJDODBTool.getInstance().select(search));
				saveParm.setData("BATCH_NO",i,searchParm.getValue("BATCH_NO",0) );						
			}
			if(TypeTool.getBoolean(getValue("UD"))){                      //����
				String search=" SELECT BATCH_NO, PHA_SEQ FROM PHA_ANTI WHERE ORDER_CODE='"+orderCode+"' AND CASE_NO='"+caseNo+"' AND BATCH_NO IS NOT NULL ORDER BY ORDER_DATE DESC";
				TParm searchParm = new TParm(TJDODBTool.getInstance().select(search));
				saveParm.setData("BATCH_NO",i,searchParm.getValue("BATCH_NO",0) );		
			}
			if(TypeTool.getBoolean(getValue("ST"))&&TypeTool.getBoolean(getValue("CHECK"))){                      //��ʱ������
				String  search = "SELECT BATCH_NO FROM PHA_ANTI WHERE ORDER_CODE='"+orderCode+"' AND CASE_NO='"+caseNo+"' AND BATCH_NO IS NOT NULL ORDER BY ORDER_DATE DESC";   
				TParm searchParm = new TParm(TJDODBTool.getInstance().select(search));
				saveParm.setData("BATCH_NO",i,searchParm.getValue("BATCH_NO",0) );		
			}			
			if("F".equals(dspnkind)) {					
				String  search = "SELECT BATCH_NO FROM PHA_ANTI WHERE ORDER_CODE='"+orderCode+"' AND CASE_NO='"+caseNo+"' AND BATCH_NO IS NOT NULL ORDER BY ORDER_DATE DESC";   
				TParm searchParm = new TParm(TJDODBTool.getInstance().select(search));
				saveParm.setData("BATCH_NO",i,searchParm.getValue("BATCH_NO",0) );	
			}
		/*	if(TypeTool.getBoolean(getValue("UD"))&&TypeTool.getBoolean(getValue("CHECK"))){                      //����
				String search=" SELECT BATCH_NO FROM PHA_ANTI WHERE ORDER_CODE='"+orderCode+"' AND CASE_NO='"+caseNo+"'";
				TParm searchParm = new TParm(TJDODBTool.getInstance().select(search));
				saveParm.setData("BATCH_NO",i,searchParm.getValue("BATCH_NO",0) );		
			}*/
		}
		if ("DOSAGE".equalsIgnoreCase(controlName)) {
			saveParm.setData("ADM_TYPE", "I");
			saveParm = SysPhaBarTool.getInstance().getaddBarParm(saveParm,
					"UDD");
		}
		// System.out.println("======"+saveParm);
		tblDtl.removeRowAll();
		tblDtl.setParmValue(saveParm);  
	}

	/**
	 * ȡ��PAT table��ѡ�е�CASE_NO��Ϊ����SQLƴWHERE��
	 * 
	 * @return
	 */
	public String getCaseNos() {
		TParm parm = tblPat.getParmValue();
		// System.out.println( (new
		// StringBuilder()).append("parm111---").append(
		// parm).toString());
		StringBuffer caseNos = new StringBuffer();
		if (parm.getCount() < 1)
			return "''";
		int count = parm.getCount();
		for (int i = 0; i < count; i++)
			if (StringTool.getBoolean(parm.getValue("EXEC", i)))
				caseNos.append("'").append(parm.getValue("CASE_NO", i)).append(
						"',");

		if (caseNos.length() < 1) {
			return "''";
		} else {
			caseNos.deleteCharAt(caseNos.length() - 1);
			return caseNos.toString();
		}
	}

	private String getPhaDispenseNo() {
		TParm parm = tblPat.getParmValue();
		// System.out.println("-------"+parm);

		StringBuffer phaDispenseNo = new StringBuffer();
		if (parm.getCount() < 1)
			return "";
		int count = parm.getCount();
		for (int i = 0; i < count; i++)
			if (StringTool.getBoolean(parm.getValue("EXEC", i)))
				phaDispenseNo.append("'").append(
						parm.getValue("PHA_DISPENSE_NO", i)).append("',");
		// System.out.println("-------"+phaDispenseNo);
		if (phaDispenseNo.length() < 1) {
			return "";
		} else {
			phaDispenseNo.deleteCharAt(phaDispenseNo.length() - 1);
			return phaDispenseNo.toString();
		}
	}
	private String getPhaDispenseNoWhere2() {
		TParm parm = tblDtl.getParmValue();
//		 System.out.println("923-tblDtl------parm:"+tblDtl.getParmValue());
		StringBuffer phaDispenseNo = new StringBuffer();
		if (parm.getCount() < 1)
			return "";
		int count = parm.getCount();
		for (int i = 0; i < count; i++)
			if (StringTool.getBoolean(parm.getValue("EXEC", i)))
				phaDispenseNo.append("'").append(
						parm.getValue("PHA_DISPENSE_NO", i)).append("',");
		// System.out.println("-------"+phaDispenseNo);
		if (phaDispenseNo.length() < 1) {
			return "";
		} else {
			phaDispenseNo.deleteCharAt(phaDispenseNo.length() - 1);
			return phaDispenseNo.toString();
		}
	}	

	// private String getBedNo() {
	// TParm parm = tblPat.getParmValue();
	// //System.out.println("-------"+parm);
	//
	// StringBuffer phaBedNo = new StringBuffer();
	// if (parm.getCount() < 1)
	// return "";
	// int count = parm.getCount();
	// for (int i = 0; i < count; i++)
	// if (StringTool.getBoolean(parm.getValue("EXEC", i)))
	// phaBedNo.append("'").append(parm.getValue(
	// "BED_NO", i)).append("',");
	// //System.out.println("-------"+phaDispenseNo);
	// if (phaBedNo.length() < 1) {
	// return "";
	// }
	// else {
	// phaBedNo.deleteCharAt(phaBedNo.length() - 1);
	// return phaBedNo.toString();
	// }
	// }

	// /**
	// * ����CHECK_BOX����¼�
	// */
	// public void onDose() {
	// boolean value = TCM_Transform.getBoolean(getValue("DOSE"));
	// TTextFormat t = (TTextFormat) getComponent("CBL_DOSE");
	// if (value) {
	// t.setEnabled(true);
	// t.setValue("");
	// return;
	// }
	// else {
	// t.setValue("");
	// t.setEnabled(false);
	// return;
	// }
	// }

	/**
	 * ѡ�����
	 */
	public void onSelectDoseTypeAll() {
		String flg = this.getValueString("DOSE_TYPEALL");
		this.setValue("DOSE_TYPEO", flg);
		this.setValue("DOSE_TYPEE", flg);
		this.setValue("DOSE_TYPEI", flg);
		this.setValue("DOSE_TYPEF", flg);
//		this.setValue("PS_FLG", flg);
	}
	
	/**
	 * ȡ��SQL����WHERE����
	 * 
	 * @return
	 */
	public String getWhere() {
		String startDate1 = "TO_DATE('"
			+ StringTool.getString(schDateFrom, "yyyyMMdd") + startTime
			+ "','YYYYMMDDHH24MI')";
		StringBuffer result = new StringBuffer();
		String startDate = StringTool.getString(TCM_Transform
				.getTimestamp(getValue("START_DATE")), "yyyyMMddHHmm");
		String endDate = (new StringBuilder()).append(
				StringTool.getString(
						TCM_Transform.getTimestamp(getValue("END_DATE")),
						"yyyyMMddHHmm").substring(0, 8)).append("2359")
				.toString();
		if (StringTool.getBoolean(getValueString("UNCHECK"))) {
			if ("DOSAGE".equalsIgnoreCase(controlName)) {
				// ��ҩ��ɲ�ѯ
				if (isCheckNeeded)
					result
							.append((new StringBuilder())
									.append(
											" AND A.PHA_CHECK_CODE IS NOT NULL AND A.START_DTTM >='")
									.append(startDate)
									.append("' AND A.START_DTTM<='")
									.append(endDate)
									.append(
											"' AND A.PHA_DOSAGE_CODE IS NULL AND A.PHA_CHECK_CODE IS NOT NULL AND (A.DC_DATE IS NULL OR A.DC_DATE >="+startDate1+") AND A.DC_NS_CHECK_DATE IS NULL")// shibl
									// 20120927
									// add
									// ����DCҽ��
									.toString());
				else
					result
							.append((new StringBuilder())
									.append(" AND A.START_DTTM >='")
									.append(startDate)
									.append("' AND A.START_DTTM<='")
									.append(endDate)
									.append(
											"' AND A.PHA_DOSAGE_CODE IS NULL AND A.PHA_CHECK_CODE IS NOT NULL AND (A.DC_DATE IS NULL OR A.DC_DATE >="+startDate1+") ")// shibl
									// 20120927
									// add
									// ����DCҽ��
									.toString());
			} else {
				// ��ҩδ��ɲ�ѯ
				result
						.append((new StringBuilder())
								.append(
										" AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('")
								.append(startDate)
								.append("', 'YYYYMMDDHH24MI') AND TO_DATE ('")
								.append(endDate)
								.append(
										"', 'YYYYMMDDHH24MI') AND A.PHA_DISPENSE_CODE IS NULL  AND (A.DC_DATE IS NULL OR A.DC_DATE >="+startDate1+") ")// shibl
								// 20120927
								// add
								// ����DCҽ��
								.toString());
			}
			// else if (isDosage)
			// result.append( (new StringBuilder()).append(
			// " AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.START_DTTM >='").
			// append(startDate).append("' AND A.START_DTTM<='").

			// append(endDate).append(
			// "' AND A.PHA_DISPENSE_CODE IS NULL ").
			// toString());
			// else
			// if (isCheckNeeded)
			// result.append( (new StringBuilder()).append(
			// " AND A.PHA_CHECK_CODE IS NOT NULL AND A.START_DTTM >='").
			// append(startDate).append("' AND A.START_DTTM<='").
			// append(endDate).append(
			// "' AND A.PHA_DISPENSE_CODE IS NULL ").
			// toString());
			// else
			// result.append( (new StringBuilder()).append(
			// " AND A.START_DTTM >='").append(startDate).append(
			// "' AND A.START_DTTM<='").append(endDate).append(
			// "' AND A.PHA_DISPENSE_CODE IS NULL").toString());
		} else {
			if ("DOSAGE".equalsIgnoreCase(controlName))
				// ��ҩ��ɲ�ѯ
				result
						.append((new StringBuilder())
								.append(
										" AND A.PHA_DOSAGE_CODE IS NOT NULL AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('")
								.append(startDate).append(
										"', 'YYYYMMDDHH24MI') AND TO_DATE ('")
								.append(endDate)
								.append("', 'YYYYMMDDHH24MI') ").toString());
			else
				// ��ҩ��ɲ�ѯ
				result
						.append((new StringBuilder())
								.append(
										" AND A.PHA_DISPENSE_CODE IS NOT NULL AND A.PHA_DISPENSE_DATE BETWEEN TO_DATE ('")
								.append(startDate).append(
										"', 'YYYYMMDDHH24MI') AND TO_DATE ('")
								.append(endDate)
								.append("', 'YYYYMMDDHH24MI') ").toString());
		}

		result.append((new StringBuilder())
				.append(" AND (A.EXEC_DEPT_CODE='")
				.append(getValueString("EXEC_DEPT_CODE"))
				.append("'")  
				//fux modify 20150831 ����ִ�п��� Ϊҩ���İ�ҩ����ѯ   
				.append(" OR A.EXEC_DEPT_CODE IN (SELECT ORG_CODE FROM IND_ORG WHERE ATC_ORG_CODE = '"+getValueString("EXEC_DEPT_CODE")+"') ) ")
				.toString()  );
		if (!StringUtil.isNullString(getValueString("AGENCY_ORG_CODE")))
			result.append((new StringBuilder()).append(
					" AND A.AGENCY_ORG_CODE='").append(  
					getValueString("AGENCY_ORG_CODE")).append("'").toString());
		if (StringTool.getBoolean(getValueString("STA"))) {
			if (!StringUtil.isNullString(getValueString("COMBO")))
				result
						.append((new StringBuilder()).append(
								" AND A.STATION_CODE='").append(
								getValueString("COMBO")).append("'").toString());
		} else if (TypeTool.getBoolean(getValue("MR"))) {
			String mrNo = StringTool.fill0(getValueString("NO"), PatTool
					.getInstance().getMrNoLength()); // ====chenxi
			setValue("NO", mrNo);
			
			// modify by huangtt 20160928 EMPI���߲�����ʾ start
			Pat pat = Pat.onQueryByMrNo(getValueString("NO"));
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
				setValue("NO", pat.getMrNo());
				mrNo =  pat.getMrNo();
			}
			// modify by huangtt 20160928 EMPI���߲�����ʾ end
			
			
			result.append((new StringBuilder()).append(" AND A.MR_NO='")
					.append(mrNo).append("'").toString());
		} else {
			result.append((new StringBuilder()).append(" AND A.BED_NO='")
					.append(getValueString("QUERY_BED")).append("' ")
					.toString());
		}
		if (TypeTool.getBoolean(getValue("ST"))){
			//20151106 WANGJC ADD
			if(!this.getValueString("ORDER_DEPT_CODE").equals("")){
				result.append(" AND A.DSPN_KIND='OP' ");
			}else{
				result.append(" AND (A.DSPN_KIND='ST' OR A.DSPN_KIND='F' OR A.DSPN_KIND='OP')");
			}
		}else if (TypeTool.getBoolean(getValue("UD"))){
			result.append(" AND A.DSPN_KIND='UD'");
		}else{
			result.append(" AND A.DSPN_KIND='DS'");
		}
		// if (TypeTool.getBoolean(getValue("DOSE")))
		// result.append( (new StringBuilder()).append(" AND A.DOSE_TYPE='").
		// append(getValueString("CBL_DOSE")).append("'").
		// toString());
		if (!StringUtil.isNullString(getValueString("PHA_DISPENSE_NO")))
			result.append((new StringBuilder()).append(
					" AND A.PHA_DISPENSE_NO='").append(
					getValueString("PHA_DISPENSE_NO")).append("'").toString());
		return result.toString();
	}

	// luhai delete �뵥ѡ��ť�¼���ͻ
	public void onTblPatClick() {
		// boolean value = TCM_Transform.getBoolean(tblPat.getValueAt(
		// tblPat.getSelectedRow(), 0));
		// int allRow = tblPat.getRowCount();
		//
		// for (int i = 0; i < allRow; i++) {
		// tblPat.setValueAt(false, i, 0);
		// tblPat.getParmValue().setData("EXEC", i, false);
		// }
		// tblPat.setValueAt(true, tblPat.getSelectedRow(), 0);
		// tblPat.getParmValue().setData("EXEC", tblPat.getSelectedRow(), true);
		// // System.out.println("click parm======"+tblPat.getParmValue());
		// tblDtl.removeRowAll();
		// onQueryDtl();
	}
	
	public void onQueryByDispenseOrg(){
		onQueryDtl();
		onQueryMed();
	}

	/**
	 * ���������嵥�е�ִ����
	 * 
	 * @param obj
	 *            Object
	 */
	public void onTableCheckBoxClicked(Object obj) {
		tblPat.acceptText();
		int column = tblPat.getSelectedColumn();
		int row = this.tblPat.getSelectedRow();
		TParm tblPatparm = tblPat.getParmValue();
		if (column == 0) {
			// =============pangben 2012-5-25 start ��Ժ���˲������ٴβ�����ҩ
			if (TypeTool.getBoolean(getValue("UNCHECK"))) {
				if (StringTool.getBoolean(tblPatparm.getValue("EXEC", row))) {
					if (!BILTool.getInstance().checkRecp(
							tblPatparm.getValue("CASE_NO", row))) {
						callFunction("UI|save|setEnabled", false);
						this.messageBox("�˲�����Ժ�ѽ���,����ִ����ҩ����");
						// return ;
					} else {
						callFunction("UI|save|setEnabled", true);
					}
				}
			}
			// =============pangben 2012-5-25 stop
            // wanglong add 20150226
            String caseNo = tblPatparm.getValue("CASE_NO", row);
            String sql =
                    "SELECT * FROM ADM_INP WHERE CASE_NO = '" + caseNo
                            + "' AND DS_DATE IS NOT NULL";
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0) {
                this.messageBox("ִ�г��� " + result.getErrText());
                return;
            }
            if (result.getCount() > 0) {
                this.messageBox(tblPatparm.getValue("PAT_NAME", row) + "�Ѿ���Ժ��");
                tblPatparm.setData("EXEC", row, false);
                tblPat.setValueAt(false, row, 0);
                return;
            }
            // add end
			onQueryDtl();
			onQueryMed();
		}
		// callFunction("UI|ALLATCDO|isSelected", true);

		// ��������table�ϵ��
		if (column == 0) {
			if ("DOSAGE".equalsIgnoreCase(controlName)) {
				// this.messageBox_("fafa");
				// this.messageBox_(tblPat.getValueAt(row, col)+"");
				if (TCM_Transform.getBoolean(tblPat.getValueAt(row, column))) {
					String stationCode = getTableSelectRowData("STATION_CODE",
							"TBL_PAT");
					// this.messageBox_(stationCode);
					String machineNo = this.getStationData(stationCode)
							.getValue("MACHINENO", 0);
					String atcType = this.getStationData(stationCode).getValue(
							"ATC_TYPE", 0);
					// this.messageBox_(atcType);
					this.setValue("ATC_MACHINENO", machineNo);
					this.setValue("ATC_TYPE", atcType);
					this.setValue("ALLATCDO", "Y");
					onATCDo();
				} else {
					// this.setValue("ALLATCDO", "N");
					onATCDo();
					this.setValue("ATC_MACHINENO", "");
					this.setValue("ATC_TYPE", "");
					
					// add by wangb 2016/2/29 START
					if (StringUtils.isNotEmpty(getValueString("COMBO"))) {
						TParm stationParm = this.getStationData(getValueString("COMBO"));
						this.setValue("ATC_MACHINENO", stationParm.getValue("MACHINENO", 0));
						this.setValue("ATC_TYPE", stationParm.getValue("ATC_TYPE", 0));
					}
					// add by wangb 2016/2/29 END
				}
			}
		}

	}

	public void onTable2CheckBoxClicked(Object obj) {
		tblDtl.acceptText();
	}

	/**
	 * ����
	 */
	public void onSave() {
		String userID = Operator.getID();
		// System.out.println("====onSave come in11111111111111111...=====");
		if (saveParm == null) {
			messageBox_("û�б�������");
			return;
		}
		// ��ȫ��parm��ֵ�� ������ʱ��tblDtl
		TParm saveParmNew = saveParm;
		int count = saveParm.getCount("ORDER_SEQ");
		// ��ҩ��ϸ������
		detailCount = count;
		if (count < 1) {
			messageBox_("û�б�������");
			return;
		}
		// �������ҩȷ�ϱ���
		if ("DOSAGE".equalsIgnoreCase(controlName)) {
			String sql = "";
			String case_no = "";
			String order_no = "";
			int order_seq = 0;
			String start_dttm = "";
			// ===========pangben modify 20110512 start
			String region = "";
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				region = " AND REGION_CODE='" + Operator.getRegion() + "' ";
			}
			// ===========pangben modify 20110512 stop
			boolean drugFlg = false;
			for (int i = 0; i < saveParm.getCount("ORDER_CODE"); i++) {
				String orderCode = saveParm.getValue("ORDER_CODE", i);
				boolean exec = saveParm.getBoolean("EXEC",i);
				if(exec) {
					String drugSql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE='"+orderCode+"' AND CTRL_FLG='Y'";
					TParm drugParm = new TParm(TJDODBTool.getInstance().select(drugSql)) ;
					if(drugParm.getCount()>0) {
						drugFlg = true;
					}			
				}
				case_no = saveParm.getValue("CASE_NO", i);
				order_no = saveParm.getValue("ORDER_NO", i);
				order_seq = saveParm.getInt("ORDER_SEQ", i);
				start_dttm = saveParm.getValue("START_DTTM", i);
				sql = "SELECT PHA_DOSAGE_DATE FROM ODI_DSPNM WHERE CASE_NO = '"
						+ case_no + "' AND ORDER_NO = '" + order_no
						+ "' AND ORDER_SEQ = " + order_seq
						+ " AND START_DTTM = '" + start_dttm + "'" + region;

				TParm checkParm = new TParm(TJDODBTool.getInstance()
						.select(sql));
				// System.out.println("checkParm---"+checkParm);
				if (checkParm.getCount("PHA_DOSAGE_DATE") > 0
						&& checkParm.getData("PHA_DOSAGE_DATE", 0) != null
						&& !"".equals(checkParm.getValue("PHA_DOSAGE_DATE", 0))) {
					this.messageBox("ҩƷ�Ѿ���ҩ�������²�ѯ");
					return;
				}
			}
			if(drugFlg) {	
				String searchSql = "SELECT * FROM SYS_OPERATOR WHERE USER_ID='"+userID+"' AND CTRL_FLG='Y'";  
				TParm drugParm = new TParm(TJDODBTool.getInstance().select(searchSql)) ;   
				if(drugParm.getCount()<=0) {
					this.messageBox("���龫ҩƷ����Ȩ��");			
					return;
				}
			}
		}
		for (int i = 0; i < count; i++) {
			TParm parm = new TParm();
			parm.setData("CASE_NO", saveParm.getValue("CASE_NO", i));
			TParm admInp = ADMInpTool.getInstance().selectall(parm);
			saveParm.addData("CTZ1_CODE", admInp.getValue("CTZ1_CODE", 0));
			saveParm.addData("CTZ2_CODE", admInp.getValue("CTZ2_CODE", 0));
			saveParm.addData("CTZ3_CODE", admInp.getValue("CTZ3_CODE", 0));
		}
		//fuwj ����ж�������his����		
		TParm result = new TParm();
		saveParm.setData("FLG", "ADD");
		// zhangyong20110516 �������REGION_CODE
		saveParm.setData("REGION_CODE", Operator.getRegion());
		//fuwj �������������
		if("Y".equals(Operator.getSpcFlg())) {
			saveParm.setData("SPC_FLG", "Y");// ��ҩ���� shibl add 20130423
		}else {
			saveParm.setData("SPC_FLG", "N");		
		}
		saveParm.setData("CHARGE", charge);
//		System.out.println("cont1"+saveParm.getCount("CASE_NO"));
		for (int i = saveParm.getCount("CASE_NO") - 1; i >= 0; i--)
			if (!"Y".equals(saveParm.getValue("EXEC", i)))
				saveParm.removeRow(i);

		Map map = new HashMap();
		String case_no = "";
		for (int i = 0; i < saveParm.getCount("CASE_NO"); i++) {
			case_no = saveParm.getValue("CASE_NO", i);
			if (map == null) {
				map.put(case_no, case_no);
				continue;
			}
			if (!map.containsValue(case_no))
				map.put(case_no, case_no);
		}

		Set set = map.keySet();
		Iterator iterator = set.iterator();
		String pha_dispense_no = "";
		String pah_dispense_no_list = "";
		printCount = 0;
		if ("DOSAGE".equalsIgnoreCase(controlName)) {// ����ҩ����
			while (iterator.hasNext()) {
				printCount++;
				pha_dispense_no = SystemTool.getInstance().getNo("ALL", "UDD",
						"UDDDspn", "No");
				pah_dispense_no_list = (new StringBuilder()).append(
						pah_dispense_no_list).append("'").append(
						pha_dispense_no).append("'").append(",").toString();
				case_no = TypeTool.getString(iterator.next());
				// ����ȼ�
				saveParm.addData("SERVICE_LEVEL", getServiceLevel(case_no));
				int i = 0;
				while (i < saveParm.getCount("CASE_NO")) {
					if (case_no.equals(saveParm.getValue("CASE_NO", i)))
						saveParm.setData("PHA_DISPENSE_NO", i, pha_dispense_no);
					i++;
				}
			}
		}
//		System.out.println("cont2"+saveParm.getCount("CASE_NO"));
		if ("DOSAGE".equalsIgnoreCase(controlName)) {// ����ҩ����
			//20150316 wangjingchun add start
			//������ʱ/������ͳҩ����
//			Map MedoNmap=this.getIntgMedNoMap();
			Map<String,String> MedoNmap=new HashMap<String,String>();
			for(int n=0;n<saveParm.getCount("CASE_NO");n++){
				String dspnKind = saveParm.getValue("DSPN_KIND", n);
				if(!dspnKind.equals("UD")){
					String stationCode=saveParm.getValue("STATION_CODE", n);
					String MedNo="";
					if(MedoNmap.get(stationCode)==null || MedoNmap.get(stationCode).equals("")){
						MedoNmap.put(stationCode, UddDispatchTool.getInstance().getStMedNo());
					}
					MedNo=String.valueOf(MedoNmap.get(stationCode));
//					System.out.println("INTGMED_NO====>"+MedNo);
					saveParm.setData("INTGMED_NO",n, MedNo);
				}else{
					String intgmedSql = "SELECT B.INTGMED_NO FROM ODI_DSPND A,ODI_DSPNM B WHERE A.CASE_NO=B.CASE_NO "
							+ " AND A.ORDER_NO=B.ORDER_NO "
							+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
							+ " AND A.ORDER_DATE || A.ORDER_DATETIME  BETWEEN  B.START_DTTM AND  B.END_DTTM "
							+ " AND A.CASE_NO='"+saveParm.getValue("CASE_NO",n)
							+ "' AND A.ORDER_NO='"+saveParm.getValue("ORDER_NO",n)
							+ "' AND A.ORDER_SEQ='"+saveParm.getValue("ORDER_SEQ",n)
                            + "' AND TO_DATE (A.ORDER_DATE || A.ORDER_DATETIME, 'YYYYMMDDHH24MISS') "
                            + "BETWEEN TO_DATE('"+saveParm.getValue("START_DTTM",n)
                            + "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
                            +saveParm.getValue("END_DTTM",n)+"', 'YYYYMMDDHH24MISS') ";
//					System.out.println("intgmedSql>>>>>>>"+intgmedSql);
					TParm intgmedParm = new TParm(TJDODBTool.getInstance().select(intgmedSql));
					saveParm.setData("INTGMED_NO",n, intgmedParm.getValue("INTGMED_NO", 0));
				}
				if("UD".equals(dspnKind)||"F".equals(dspnKind)) {
					String sql="SELECT DOSAGE_QTY FROM ODI_DSPNM WHERE CASE_NO = '"+saveParm.getValue("CASE_NO",n)+
							"' AND ORDER_NO='"+saveParm.getValue("ORDER_NO",n)+"'AND  DISPENSE_FLG='N' AND ORDER_SEQ='"
							+saveParm.getValue("ORDER_SEQ",n)+"' AND START_DTTM='"+saveParm.getValue("START_DTTM",n)+"'";
					TParm searchResult  = new TParm(TJDODBTool.getInstance().select(sql));
					saveParm.setData("DOSAGE_QTY", n, searchResult.getDouble("DOSAGE_QTY",0));
				}
			}
			
			//20150316 wangjingchun add end
//			System.out.println("saveParm====="+saveParm);
			result = TIOM_AppServer.executeAction("action.udd.UddAction",
					"onUpdateMedDosage", saveParm);
			printCount = saveParm.getCount();
		} else {// ����ҩ��ҩ
			result = TIOM_AppServer.executeAction("action.udd.UddAction",
					"onUpdateMedDispense", saveParm);
		}
		Object list = result.getData("ERR_LIST");
		StringBuffer sbErr = new StringBuffer();
		if (list != null) {
			TParm errList = result.getParm("ERR_LIST");
			if (errList != null) {
				int countErr = errList.getCount();
				if (countErr > 0) {
					for (int i = 0; i < countErr; i++) {
						String err = errList.getValue("MSG", i);
						messageBox_(err);
						sbErr.append(err).append("\r\n");
					}

				}
				String fileNmae = (new StringBuilder()).append(
						TConfig.getSystemValue("UDD_DISBATCH_LocalPath"))
						.append("\\��ҩ������־").append(
								StringTool.getString(TJDODBTool.getInstance()
										.getDBTime(), "yyyyMMddHHmmss"))
						.append(".txt").toString();
				messageBox_((new StringBuilder()).append(
						"��ϸ�����C:/JavaHis/logs/��ҩ������־").append(
						StringTool.getString(TJDODBTool.getInstance()
								.getDBTime(), "yyyyMMddHHmmss")).append(
						".txt�ļ�").toString());
				try {
					FileTool.setString(fileNmae, sbErr.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (result.getErrCode() != 0) {
				messageBox("E0001");
				return;
			}
			messageBox("P0001");
			// �Ͱ�ҩ��
			try {
				if ("DOSAGE".equalsIgnoreCase(controlName)) {
//					String stationCode = getTableSelectRowData("STATION_CODE",
//							"TBL_PAT");
					boolean nowFlag = (Boolean) this
							.callFunction("UI|ALLATCDO|isSelected");
					if (nowFlag) {
						onGenATCFile();
					}
					// System.out.println("-==--------------"+stationCode);
					// TParm atcParm = this.getStationData(stationCode);
					// // System.out.println("12--------------------"+atcParm);
					// // String type = atcParm.getValue("ATC_TYPE", 0);
					// String type="2";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// ��ӡ��ҩȷ�ϵ�
			if ("DOSAGE".equalsIgnoreCase(controlName))
				onDispenseSheetByPhaDispenseNo(pah_dispense_no_list.substring(0,
						pah_dispense_no_list.length() - 1));

			if (this.printCount < this.detailCount) {// �����ӡ������ С�� ��ϸ������ ��
				// ֻ����ұ�����
				tblDtl.setParmValue(saveParmNew);
				TParm parmNull = new TParm();
//				tblDtl.removeRowAll();  
//				this.tblMed.removeRowAll();      
				tblDtl.setParmValue(parmNull);  
				this.tblMed.setParmValue(parmNull);  
				/*
				 * for(int i = 0 ; i < this.detailCount; i++){
				 * this.messageBox("ai"+i); this.tblDtl.remove(i); }
				 */
			} else {
				onClear();
			}
			return;
		}
	}
	
	/**
	 * �жϾ�����Һ�����Ƿ�����
	 * 20150316 wangjingchun add
	 */
	public boolean checkIvaActive(){
		boolean flg = false;
		String pivas_sql = "SELECT PIVAS_FLG FROM SYS_REGION";
		String sql = "SELECT IVA_UD,IVA_STAT,IVA_FIRST FROM ODI_SYSPARM";
		TParm pivasParm = new TParm(TJDODBTool.getInstance().select(pivas_sql));
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(pivasParm.getValue("PIVAS_FLG",0).equals("Y") 
				&& parm.getValue("IVA_STAT",0).equals("Y") 
				&& parm.getValue("IVA_FIRST",0).equals("Y")){
			flg = true;
		}
		return flg;
		/*
		 * 3.д��ODI_DSPNM & ODI_DSPND��IVA_FLG��ODI_DSPND.BATCH_CODE��
			IF SYS_REGION.PIVAS_FLG='Y' AND SYS_PHAROUTE.IVA_FLG='Y' 
			AND ((DSPN_KIND='UD' AND ODI_SYSPARM.IVA_UD='Y') OR 
			(DSPN_KIND='ST' AND ODI_SYSPARM.IVA_STAT='Y') OR 
			(DSPN_KIND='F' AND ODI_SYSPARM.IVA_FIRST='Y'))
      				
      		IVA_FLG='Y'
		 */
	}
	
	/**
	 * �õ�ͳҩ���ż���
	 * 20150316 wangjingchun add
	 * @return
	 */
	public Map getIntgMedNoMap(){
		Map map=new HashMap();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		String timeStr=String.valueOf(now).substring(0, 10).replace("-", "");
		String sql=" SELECT DISTINCT INTGMED_NO,STATION_CODE FROM ODI_DSPNM WHERE PHA_DOSAGE_DATE " +
				" BETWEEN TO_DATE('"+timeStr+"','YYYYMMDD') "+
				" AND TO_DATE('"+timeStr+"235959','YYYYMMDDHH24MISS') AND DSPN_KIND IN ('ST','F')";
//		System.out.println("sql==="+sql);
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		for(int i=0;i<parm.getCount();i++){
			String stationCode=parm.getValue("STATION_CODE", i);
			if(map.get(stationCode)==null){
				map.put(stationCode, parm.getValue("INTGMED_NO", i));
			}else{
				continue;
			}
		}
		return map;
	}

	/**
	 * ȡ����ҩ����ҩ
	 */
	public void onDelete() {
		if (TypeTool.getBoolean(getValue("UNCHECK"))) {
			messageBox_("δ�������ݲ���ȡ��");
			return;
		}
		if (saveParm == null) {
			messageBox_("û�б�������");
			return;
		}
		int count = saveParm.getCount("ORDER_SEQ");
		if (count < 1) {
			messageBox_("û�б�������");
			return;
		}
		String caseNos = getCaseNos();
		String caseNo[] = StringTool.parseLine(caseNos, ",");
		if (caseNo == null) {
			messageBox_("û�б�������");
			return;
		}
		if (caseNo.length != 1) {
			messageBox_("û�б�������");
			return;
		}

		Timestamp date = SystemTool.getInstance().getDate();
		for (int i = 0; i < count; i++) {
			TParm parm = new TParm();
			parm.setData("CASE_NO", saveParm.getValue("CASE_NO", i));
			TParm admInp = ADMInpTool.getInstance().selectall(parm);
			saveParm.addData("CTZ1_CODE", admInp.getValue("CTZ1_CODE", 0));
			saveParm.addData("CTZ2_CODE", admInp.getValue("CTZ2_CODE", 0));
			saveParm.addData("CTZ3_CODE", admInp.getValue("CTZ3_CODE", 0));
			saveParm.addData("SERVICE_LEVEL", admInp.getValue("SERVICE_LEVEL",
					0));
			saveParm.addData("OPT_USER", Operator.getID());
			saveParm.addData("OPT_DATE", date);
			saveParm.addData("OPT_TERM", Operator.getIP());
		}

		TParm result = new TParm();
		saveParm.setData("CHARGE", charge);
		saveParm.setData("FLG", "DELETE");
		// zhangyong20110516 �������REGION_CODE
		saveParm.setData("REGION_CODE", Operator.getRegion());
		// System.out.println("---------" + saveParm);
		TParm patParm = tblPat.getParmValue();
		if ("DOSAGE".equalsIgnoreCase(controlName)) {
			for (int i = 0; i < patParm.getCount("EXEC"); i++) {
				if ("Y".equals(patParm.getValue("EXEC", i))
						&& !"".equals(patParm.getValue("PHA_DISPENSE_CODE", i))) {
					this.messageBox("��ҩƷ�ѷ�ҩ������ȡ����ҩ");
					return;
				}
			}

			result = TIOM_AppServer.executeAction("action.udd.UddAction",
					"onCnclUpdateMedDosage", saveParm);
		} else
			result = TIOM_AppServer.executeAction("action.udd.UddAction",
					"onCnclUpdateMedDispense", saveParm);
		if (result.getErrCode() != 0) {
			messageBox("E0001");
			return;
		} else {
			messageBox("P0001");
			onClear();
			return;
		}
	}

	/**
	 * ��ӡ��ҩȷ�ϵ�
	 */
	public void onDispenseSheet() {
		if(getRadioButton("ALL_DISPENSE_ORG_TWO").isSelected()){
			this.messageBox("���ڲ���ҩƷ��ϸҳ��ѡ��ҩ���ţ�סԺҩ������");
			return;
		}
		onQueryDtl();
		onQueryMed();
		TParm inParm = new TParm();
		// luhai delete 2012-2-23 ɾ�� ���е���ҩȷ�ϵ���Ϊ��һ�δ�ӡ begin
		// inParm.setData("FIRST_PRINT", Boolean.valueOf(false));
		inParm.setData("FIRST_PRINT", Boolean.valueOf(true));
		// luhai delete 2012-2-23 ɾ�� ���е���ҩȷ�ϵ���Ϊ��һ�δ�ӡ end
		TTextFormat station = (TTextFormat) getComponent("COMBO");
		String stationName = station.getText();
		// String stationCode = getValueString("COMBO");
		if (StringUtil.isNullString(stationName))
			stationName = "ȫԺ";
		inParm.setData("STATION_NAME", stationName);
		inParm.setData("START_DATE", TypeTool
				.getTimestamp(getValue("START_DATE")));
		if (getRadioButton("IN_STATION_TWO").isSelected()) {
			inParm.setData("DISPENSE_ORG_TWO","����");
			inParm.setData("WHERE_5", "'1'");
		}else {
			inParm.setData("WHERE_5", "'2'");
			inParm.setData("DISPENSE_ORG_TWO","סԺҩ��");
		}
		inParm.setData("END_DATE", TypeTool.getTimestamp(getValue("END_DATE")));
		inParm.setData("DONE", Boolean.valueOf(TypeTool
				.getBoolean(getValue("UNCHECK"))));
		boolean isStation = TypeTool.getBoolean(getValue("STA"));
		if (!isStation) {
			messageBox_("������ҩƷ��Ϣ���ܰ�������ʾ");
			return;
		}
		inParm.setData("IS_STATION", Boolean.valueOf(isStation));
		String caseNos = getCaseNos();
		inParm.setData("WHERE_1", caseNos);
		String phaDispenseNo = getPhaDispenseNoWhere2();
//		 System.out.println("phaDispenseNo---"+phaDispenseNo);
		inParm.setData("WHERE_2", phaDispenseNo);
		// ====zhangp 20121118 start
		String ctrl = "";
		String bar_code = "";
		String pha_ctrlcode = getValueString("PHA_CTRLCODE");
		if (!pha_ctrlcode.equals("") && this.getRadioButton("ST").isSelected()) {
			ctrl = "�龫";
			// ===zhangp 20130225 start
			tblDtl.acceptText();
			TParm parm = tblDtl.getParmValue();
			if (parm.getCount() > 0) {
				int count = parm.getCount();
				for (int i = 0; i < count; i++) {
					if (StringTool.getBoolean(parm.getValue("EXEC", i)) && parm.getValue("TAKEMED_ORG", i).equals("2"))
						if(phaDispenseNo.length()>0){
							bar_code = phaDispenseNo.replace("'", "");
						}
					break;
				}
			}
		}
		// ===zhangp 20130225 end
		if (this.getRadioButton("ST").isSelected()) {
			//20151106 WANGJC ADD
			if(!this.getValueString("ORDER_DEPT_CODE").equals("")){
				inParm.setData("TYPE_T", "����ҽ��" + ctrl + "��ҩȷ�ϵ�");
				inParm.setData("WHERE_3", " 'OP'");
			}else{
				inParm.setData("TYPE_T", "��ʱҽ��" + ctrl + "��ҩȷ�ϵ�");
				inParm.setData("WHERE_3", " 'ST','F','OP'");
			}
		} else if (this.getRadioButton("UD").isSelected()) {
			inParm.setData("TYPE_T", "����ҽ��" + ctrl + "��ҩȷ�ϵ�");
			inParm.setData("WHERE_3", " 'UD'");
		} else {
			inParm.setData("TYPE_T", "��Ժ��ҩ" + ctrl + "��ҩȷ�ϵ�");
			inParm.setData("WHERE_3", " 'DS'");
		}
		// ====zhangp 20121118 end
		if ("''".equalsIgnoreCase(caseNos)) {
			messageBox_("û������");
			return;
		}
		// ���ͷ���
		inParm.setData("WHERE_4", getDoseTypeByWhere());
		// �÷�
		inParm.setData("DOSE_TYPE", getDoseTypeText());

		TParm parmData = tblPat.getParmValue();
		TParm parmRowData = new TParm();
		for (int i = 0; i < parmData.getCount("EXEC"); i++) {
			if (StringTool.getBoolean(parmData.getValue("EXEC", i))) {
				parmRowData = parmData.getRow(i);
				break;
			}
		}
		// ������Ա��ʱ��
		/**
		 * String datetime = parmRowData.getValue("PHA_DOSAGE_DATE")
		 * .substring(0, 19).replace("-", "/"); String pha_dosage_code =
		 * parmRowData.getValue("PHA_DOSAGE_CODE"); String sql =
		 * "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '" +
		 * pha_dosage_code + "'"; parmRowData = new
		 * TParm(TJDODBTool.getInstance().select(sql));
		 **/
		// luhai modify 2012-05-09 add begin ��ҩ��Ա����ҩʱ��ֿ����� begin
		// String datetime = TJDODBTool.getInstance().getDBTime().toString()
		// .substring(0, 19).replace("-", "/");
		// inParm.setData("USER_NAME", "������: " + Operator.getName() + "  "
		// + datetime);
		// String datetime = TJDODBTool.getInstance().getDBTime().toString()
		// .substring(0, 19).replace("-", "/");
		// inParm.setData("USER_NAME", "������: " + Operator.getName() + "");
		// inParm.setData("CUR_DATE", datetime + "");
		
		inParm.setData("BATCH_NO",saveParm.getValue("BATCH_NO",0));
//		messageBox(saveParm.getValue("BATCH_NO",0));
		inParm.setData("USER_NAME", Operator.getName());
		String check_user_sql = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID ='"
			+ saveParm.getValue("PHA_CHECK_CODE",0)+"' ";
		TParm checkUserParm = new TParm(TJDODBTool.getInstance().select(check_user_sql));
		inParm.setData("S_USER_NAME", checkUserParm.getData("USER_NAME",0));	
		// ===zhangp 20121120 start
		inParm.setData("BAR_CODE", phaDispenseNo.replace("'", ""));
		// ===zhangp 20121120 end
		inParm.setData("DR_NAME", parmRowData.getData("USER_NAME1")); // lirui
		// 2012-6-8
		// �ڱ�����չʾ����ҽʦ
//		System.out.println(check_user_sql);
//		TParm checkUserParm = new TParm(TJDODBTool.getInstance().select(check_user_sql));
//		inParm.setData("CHECK_USER_NAME", checkUserParm.getData("USER_NAME",0));
//		inParm.setData("CHECK_USER_NAME", parmRowData.getData("USER_NAME1"));
//		// luhai modify 2012-05-09 add begin ��ҩ��Ա����ҩʱ��ֿ����� end
//		System.out.println("inParm===="+inParm);
		if ("''".equalsIgnoreCase(phaDispenseNo)) {
			messageBox_("û������");
			return;
		} else {
			 if (!pha_ctrlcode.equals("") && this.getRadioButton("ST").isSelected()) {
					String barCodes = inParm.getValue("BAR_CODE");
					Set<String> set= distinctPhaDispenseNo(phaDispenseNo);
					if (null != set && set.size()>1) {
						this.messageBox("�龫��ҩ����ӡ��һ��ֻ�ܴ�ӡһ����ҩ��");
						return;
					}	
					if (null != barCodes && barCodes.length()>12) {
						barCodes = barCodes.substring(0, 12);
					}
					inParm.setData("BAR_CODE",barCodes);				 
					openPrintWindow(
							"%ROOT%\\config\\prt\\UDD\\UddDispenseConfirmListOfDrug.jhw",
							inParm, false);
					return;
			 }else {
//				 String notPIVAsSql = "SELECT SUM (G.DOSAGE_QTY) AS DOSAGE_QTY "
//				 		+ " FROM ODI_DSPNM A,SYS_PHAROUTE F,ODI_DSPND G "
//				 		+ " WHERE A.CASE_NO IN ("+inParm.getValue("WHERE_1")+") "
//				 		+ " AND (A.ORDER_CAT1_CODE = 'PHA_W'  OR A.ORDER_CAT1_CODE = 'PHA_C') "
//				 		+ " AND A.DISPENSE_FLG = 'N' "
//				 		+ " AND A.PHA_DISPENSE_NO IN ("+inParm.getValue("WHERE_2")+")"
//				 		+ " AND A.DSPN_KIND IN ("+inParm.getValue("WHERE_3")+")"
//				 		+ " AND A.TAKEMED_ORG IN ("+inParm.getValue("WHERE_5")+")"
//				 		+ " AND A.ROUTE_CODE = F.ROUTE_CODE"
//				 		+ " AND F.CLASSIFY_TYPE IN ("+inParm.getValue("WHERE_4")+")"
//				 		+ " AND A.CASE_NO=G.CASE_NO "
//				 		+ " AND A.ORDER_NO=G.ORDER_NO "
//				 		+ " AND A.ORDER_SEQ=G.ORDER_SEQ "
//				 		+ " AND G.ORDER_DATE || G.ORDER_DATETIME BETWEEN A.START_DTTM AND A.END_DTTM "
//				 		+ " AND (G.IVA_FLG IS NULL OR G.IVA_FLG = 'N') ";
//				 String isPIVAsSql = "SELECT SUM (G.DOSAGE_QTY) AS DOSAGE_QTY "
//					 		+ " FROM ODI_DSPNM A,SYS_PHAROUTE F,ODI_DSPND G "
//					 		+ " WHERE A.CASE_NO IN ("+inParm.getValue("WHERE_1")+") "
//					 		+ " AND (A.ORDER_CAT1_CODE = 'PHA_W'  OR A.ORDER_CAT1_CODE = 'PHA_C') "
//					 		+ " AND A.DISPENSE_FLG = 'N' "
//					 		+ " AND A.PHA_DISPENSE_NO IN ("+inParm.getValue("WHERE_2")+") "
//					 		+ " AND A.DSPN_KIND IN ("+inParm.getValue("WHERE_3")+")"
//					 		+ " AND A.TAKEMED_ORG IN ("+inParm.getValue("WHERE_5")+") "
//					 		+ " AND A.LINK_NO IS NOT NULL "
//					 		+ " AND A.ROUTE_CODE = F.ROUTE_CODE "
//					 		+ " AND F.IVA_FLG='Y' "
//					 		+ " AND F.CLASSIFY_TYPE IN ("+inParm.getValue("WHERE_4")+") "
//					 		+ " AND A.CASE_NO=G.CASE_NO "
//					 		+ " AND A.ORDER_NO=G.ORDER_NO "
//					 		+ " AND A.ORDER_SEQ=G.ORDER_SEQ "
//					 		+ " AND G.ORDER_DATE || G.ORDER_DATETIME BETWEEN A.START_DTTM AND A.END_DTTM "
//					 		+ " AND G.IVA_FLG='Y' ";
//				 TParm notPIVAsParm = new TParm(TJDODBTool.getInstance().select(notPIVAsSql));
//				 TParm isPIVAsParm = new TParm(TJDODBTool.getInstance().select(isPIVAsSql));
//				 int n = 0;
//				 boolean isPIVAs = false;
//				 if(notPIVAsParm.getInt("DOSAGE_QTY", 0) !=0 && isPIVAsParm.getInt("DOSAGE_QTY", 0) !=0){
//					 n = 2;
//				 }else{
//					 n = 1;
//					 if(isPIVAsParm.getInt("DOSAGE_QTY", 0) != 0){
//						 isPIVAs = true;
//					 }
//				 }
////				 System.out.println("is>>>>>>>>>"+isPIVAsParm.getInt("DOSAGE_QTY", 0)+"    "+isPIVAsSql);
////				 System.out.println("not>>>>>>>>"+notPIVAsParm.getInt("DOSAGE_QTY", 0)+"   "+notPIVAsSql);
////				 System.out.println(inParm);
//				 for(int k=0;k<n;k++){
//					 if(n>1){
//						 if(k==0){
//							 inParm.setData("WHERE_6", " AND A.LINK_NO IS NOT NULL AND F.IVA_FLG='Y' "
//												 		+ " AND A.IVA_FLG='Y' "); 
//							 inParm.setData("PIVAS", "PIVAs����");
//						 }else if(k==1){
//							 inParm.setData("WHERE_6", " AND (A.IVA_FLG IS NULL OR A.IVA_FLG = 'N') ");
//							 inParm.setData("PIVAS", "��������");
//						 }
//					 }else{
//						 if(isPIVAs){
//							 inParm.setData("WHERE_6", " AND A.LINK_NO IS NOT NULL AND F.IVA_FLG='Y' "
//									 + " AND A.IVA_FLG='Y' "); 
//							 inParm.setData("PIVAS", "PIVAs����");
//						 }else{
//							 inParm.setData("WHERE_6", " AND (A.IVA_FLG IS NULL OR A.IVA_FLG = 'N') ");
//							 inParm.setData("PIVAS", "��������");
//						 }
//					 }
				if (Boolean.valueOf(TypeTool.getBoolean(getValue("LINK_NO")))) {
					 inParm.setData("WHERE_6", " AND A.LINK_NO IS NOT NULL AND F.IVA_FLG='Y' "
							 						+ " AND A.IVA_FLG='Y' "); 
					 inParm.setData("PIVAS", "PIVAs����");
				} else {
					 inParm.setData("WHERE_6", " AND (A.IVA_FLG IS NULL OR A.IVA_FLG = 'N') ");
					 inParm.setData("PIVAS", "��������");
				}
//					 System.out.println("inParm=="+inParm);
//					 messageBox_(inParm);
					 openPrintWindow(
							 "%ROOT%\\config\\prt\\UDD\\UddDispenseConfirmList.jhw",
							 inParm, false);
//				 }
					return;
			}
	
		}
	}

	/**
	 * ��ӡ��ҩȷ�ϵ�
	 */
	public void onDispenseSheetByPhaDispenseNo(String pha_dispense_no) {
		TParm inParm = new TParm();
		tblDtl = (TTable) getComponent("TBL_DTL");
		inParm.setData("FIRST_PRINT", Boolean.valueOf(true));
		TTextFormat station = (TTextFormat) getComponent("COMBO");
		String stationName = station.getText();
		String stationCode = getValueString("COMBO");
		
		if (StringUtil.isNullString(stationName))
			stationName = "ȫԺ";
		inParm.setData("STATION_NAME", stationName);
		if (getRadioButton("IN_STATION_TWO").isSelected()) {
			inParm.setData("DISPENSE_ORG_TWO","����");
			inParm.setData("WHERE_5", "1");
		}else {
			inParm.setData("WHERE_5", "2");
			inParm.setData("DISPENSE_ORG_TWO","סԺҩ��");
		}
		inParm.setData("START_DATE", TypeTool
				.getTimestamp(getValue("START_DATE")));
		inParm.setData("END_DATE", TypeTool.getTimestamp(getValue("END_DATE")));
		inParm.setData("DONE", Boolean.valueOf(TypeTool
				.getBoolean(getValue("UNCHECK"))));
		boolean isStation = TypeTool.getBoolean(getValue("STA"));
		if (!isStation) {
			messageBox_("������ҩƷ��Ϣ���ܰ�������ʾ");
			return;
		}

		inParm.setData("IS_STATION", Boolean.valueOf(isStation));
		String caseNos = getCaseNos();
		inParm.setData("WHERE_1", caseNos);
		inParm.setData("WHERE_2", pha_dispense_no);
		// ====zhangp 20121118 start
		String ctrl = "";
		String bar_code = "";
		String pha_ctrlcode = getValueString("PHA_CTRLCODE");
		if (!pha_ctrlcode.equals("") && this.getRadioButton("ST").isSelected()) {
			ctrl = "�龫";
			// ===zhangp 20130225 start
			tblDtl.acceptText();
			TParm parm = tblDtl.getParmValue();
			if (parm.getCount() > 0) {
				int count = parm.getCount();
				for (int i = 0; i < count; i++) {
					if (StringTool.getBoolean(parm.getValue("EXEC", i)) && parm.getValue("TAKEMED_ORG", i).equals("2"))
						if(pha_dispense_no.length()>0){
							bar_code = pha_dispense_no.replace("'", "");
						}
					break;
				}
			}
		}
/*		Set<String> set= distinctPhaDispenseNo(pha_dispense_no);
		if (null != set && set.size()>1) {
			this.messageBox("�龫��ҩ����ӡ��һ��ֻ�ܴ�ӡһ����ҩ��");
			return;
		}	
		String barCodes = inParm.getValue("BAR_CODE");
		if (null != barCodes && barCodes.length()>12) {
			barCodes = barCodes.substring(0, 12);
		}
		inParm.setData("BAR_CODE",barCodes);	*/		
		// ===zhangp 20130225 end
		if (this.getRadioButton("ST").isSelected()) {
			//20151106 WANGJC ADD
			if(!this.getValueString("ORDER_DEPT_CODE").equals("")){
				inParm.setData("TYPE_T", "����ҽ��" + ctrl + "��ҩȷ�ϵ�");
				inParm.setData("WHERE_3", "'OP'");
			}else{
				inParm.setData("TYPE_T", "��ʱҽ��" + ctrl + "��ҩȷ�ϵ�");
				inParm.setData("WHERE_3", "'ST','F','OP'");
			}
		} else if (this.getRadioButton("UD").isSelected()) {
			inParm.setData("TYPE_T", "����ҽ��" + ctrl + "��ҩȷ�ϵ�");
			inParm.setData("WHERE_3", "'UD'");
		} else {
			inParm.setData("TYPE_T", "��Ժ��ҩ" + ctrl + "��ҩȷ�ϵ�");
			inParm.setData("WHERE_3", "'DS'");
		}
		// ====zhangp 20121118 end

		if ("''".equalsIgnoreCase(caseNos)) {
			messageBox_("û������");
			return;
		}

		// ���ͷ���
		inParm.setData("WHERE_4", getDoseTypeByWhere());
		// �÷�
		inParm.setData("DOSE_TYPE", getDoseTypeText());
		// ===zhangp 20120709 start
		TParm parmData = tblPat.getParmValue();
		TParm parmRowData = new TParm();
		for (int i = 0; i < parmData.getCount("EXEC"); i++) {
			if (StringTool.getBoolean(parmData.getValue("EXEC", i))) {
				parmRowData = parmData.getRow(i);
				break;
			}
		}
		// ===zhangp 20120709 end
		// ������Ա��ʱ��
		// luhai modify 2012-05-09 add begin ��ҩ��Ա����ҩʱ��ֿ����� begin
		// String datetime = TJDODBTool.getInstance().getDBTime().toString()
		// .substring(0, 19).replace("-", "/");
		// inParm.setData("USER_NAME", "������: " + Operator.getName() + "  "
		// + datetime);
		String datetime = TJDODBTool.getInstance().getDBTime().toString()
				.substring(0, 19).replace("-", "/");
		inParm.setData("USER_NAME", "������: " + Operator.getName() + "");
		inParm.setData("CUR_DATE", datetime + "");
		// luhai modify 2012-05-09 add begin ��ҩ��Ա����ҩʱ��ֿ����� end
		// luhai 2012004-07 add ������ݴ�ǰ̨���� begin
		// luhai 2012004-07 add ������ݴ�ǰ̨���� end
		// ===zhangp 20120709 start
		inParm.setData("USER_NAME", Operator.getName());
		String check_user_sql = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID ='"
			+ saveParm.getValue("PHA_CHECK_CODE",0)+"' ";
		TParm checkUserParm = new TParm(TJDODBTool.getInstance().select(check_user_sql));
		inParm.setData("S_USER_NAME", checkUserParm.getData("USER_NAME",0));
		// ===zhangp 20121120 start
		inParm.setData("BAR_CODE", bar_code);
		// ===zhangp 20121120 end
		inParm.setData("DR_NAME", parmRowData.getData("USER_NAME1")); // lirui
		// 2012-6-8
		// �ڱ�����չʾ����ҽʦ
		// ===zhangp 20120709 end
//		System.out.println("inParm====="+inParm);
		if ("''".equalsIgnoreCase(pha_dispense_no)) {
			messageBox_("û������");
			return;
		} else {
			String[] dispenseOrgArr = new String[]{"1","2"};//1������2סԺҩ��
			String[] dispenseOrgDescArr = new String[]{"����","סԺҩ��"};//1������2סԺҩ��
			for (int i = 0; i < dispenseOrgArr.length; i++) {//һ�δ�ӡ������ҩ��
				inParm.setData("WHERE_5", dispenseOrgArr[i]);
				inParm.setData("DISPENSE_ORG_TWO", dispenseOrgDescArr[i]);
//				String notPIVAsSql = "SELECT SUM (G.DOSAGE_QTY) AS DOSAGE_QTY "
//				 		+ " FROM ODI_DSPNM A,SYS_PHAROUTE F,ODI_DSPND G "
//				 		+ " WHERE A.CASE_NO IN ("+inParm.getValue("WHERE_1")+") "
//				 		+ " AND (A.ORDER_CAT1_CODE = 'PHA_W'  OR A.ORDER_CAT1_CODE = 'PHA_C') "
//				 		+ " AND A.DISPENSE_FLG = 'N' "
//				 		+ " AND A.PHA_DISPENSE_NO IN ("+inParm.getValue("WHERE_2")+")"
//				 		+ " AND A.DSPN_KIND IN ("+inParm.getValue("WHERE_3")+")"
//				 		+ " AND A.TAKEMED_ORG IN ("+inParm.getValue("WHERE_5")+")"
//				 		+ " AND A.ROUTE_CODE = F.ROUTE_CODE"
//				 		+ " AND F.CLASSIFY_TYPE IN ("+inParm.getValue("WHERE_4")+")"
//				 		+ " AND A.CASE_NO=G.CASE_NO "
//				 		+ " AND A.ORDER_NO=G.ORDER_NO "
//				 		+ " AND A.ORDER_SEQ=G.ORDER_SEQ "
//				 		+ " AND G.ORDER_DATE || G.ORDER_DATETIME BETWEEN A.START_DTTM AND A.END_DTTM "
//				 		+ " AND (G.IVA_FLG IS NULL OR G.IVA_FLG = 'N') ";
//				 String isPIVAsSql = "SELECT SUM (G.DOSAGE_QTY) AS DOSAGE_QTY "
//					 		+ " FROM ODI_DSPNM A,SYS_PHAROUTE F,ODI_DSPND G "
//					 		+ " WHERE A.CASE_NO IN ("+inParm.getValue("WHERE_1")+") "
//					 		+ " AND (A.ORDER_CAT1_CODE = 'PHA_W'  OR A.ORDER_CAT1_CODE = 'PHA_C') "
//					 		+ " AND A.DISPENSE_FLG = 'N' "
//					 		+ " AND A.PHA_DISPENSE_NO IN ("+inParm.getValue("WHERE_2")+") "
//					 		+ " AND A.DSPN_KIND IN ("+inParm.getValue("WHERE_3")+")"
//					 		+ " AND A.TAKEMED_ORG IN ("+inParm.getValue("WHERE_5")+") "
//					 		+ " AND A.LINK_NO IS NOT NULL "
//					 		+ " AND A.ROUTE_CODE = F.ROUTE_CODE "
//					 		+ " AND F.IVA_FLG='Y' "
//					 		+ " AND F.CLASSIFY_TYPE IN ("+inParm.getValue("WHERE_4")+") "
//					 		+ " AND A.CASE_NO=G.CASE_NO "
//					 		+ " AND A.ORDER_NO=G.ORDER_NO "
//					 		+ " AND A.ORDER_SEQ=G.ORDER_SEQ "
//					 		+ " AND G.ORDER_DATE || G.ORDER_DATETIME BETWEEN A.START_DTTM AND A.END_DTTM "
//					 		+ " AND G.IVA_FLG='Y' ";
//				 TParm notPIVAsParm = new TParm(TJDODBTool.getInstance().select(notPIVAsSql));
//				 TParm isPIVAsParm = new TParm(TJDODBTool.getInstance().select(isPIVAsSql));
//				 int n = 0;
//				 boolean isPIVAs = false;
//				 if(notPIVAsParm.getInt("DOSAGE_QTY", 0) !=0 && isPIVAsParm.getInt("DOSAGE_QTY", 0) !=0){
//					 n = 2;
//				 }else{
//					 n = 1;
//					 if(isPIVAsParm.getInt("DOSAGE_QTY", 0) != 0){
//						 isPIVAs = true;
//					 }
//				 }
//				 for(int k=0;k<n;k++){
//					 if(n>1){
//						 if(k==0){
//							 inParm.setData("WHERE_6", " AND A.LINK_NO IS NOT NULL AND F.IVA_FLG='Y' "
//												 		+ " AND A.IVA_FLG='Y' "); 
//							 inParm.setData("PIVAS", "PIVAs����");
//						 }else if(k==1){
//							 inParm.setData("WHERE_6", " AND (A.IVA_FLG IS NULL OR A.IVA_FLG = 'N') ");
//							 inParm.setData("PIVAS", "��������");
//						 }
//					 }else{
//						 if(isPIVAs){
//							 inParm.setData("WHERE_6", " AND A.LINK_NO IS NOT NULL AND F.IVA_FLG='Y' "
//									 + " AND A.IVA_FLG='Y' "); 
//							 inParm.setData("PIVAS", "PIVAs����");
//						 }else{
//							 inParm.setData("WHERE_6", " AND (A.IVA_FLG IS NULL OR A.IVA_FLG = 'N') ");
//							 inParm.setData("PIVAS", "��������");
//						 }
//					 }
//				System.out.println("----------inparm:"+inParm);
				if (Boolean.valueOf(TypeTool.getBoolean(getValue("LINK_NO")))) {
					 inParm.setData("WHERE_6", " AND A.LINK_NO IS NOT NULL AND F.IVA_FLG='Y' "
							 						+ " AND A.IVA_FLG='Y' "); 
					 inParm.setData("PIVAS", "PIVAs����");
				} else {
					 inParm.setData("WHERE_6", " AND (A.IVA_FLG IS NULL OR A.IVA_FLG = 'N') ");
					 inParm.setData("PIVAS", "��������");
				}
				openPrintWindow(
						"%ROOT%\\config\\prt\\UDD\\UddDispenseConfirmList.jhw",
						inParm, false);
//				 }
			}
			return;
		}
	}
	
	/**
	 * �ж��м�����ҩ����
	 * @param phaDispenseNo
	 * @return true,����1����false 1��
	 */
	public Set<String> distinctPhaDispenseNo(String phaDispenseNo){
//		System.out.println(">>>>distinctPhaDispenseNo("+phaDispenseNo+")");
		boolean flg = false;
		Set<String> set = new HashSet<String>();
		if(null != phaDispenseNo && phaDispenseNo.length()>1){
			phaDispenseNo = phaDispenseNo.replaceAll("'", "");
			String[] strs = phaDispenseNo.split(",");
			if (null != strs && strs.length>1) {
				for (int i = 0; i < strs.length; i++) {
					set.add(strs[i]+"");
				}
			}
			
		}
		return set;
	}

	/**
	 * ��ӡͳҩ��
	 */
	public void onUnDispense() {
		if (getRadioButton("ALL_DISPENSE_ORG_ONE").isSelected()) {
			this.messageBox("��ѡ��ҩ���ţ�סԺҩ������");
			return;
		}
		// add
		onQueryDtl();
		onQueryMed();
		TParm inParm = new TParm();
		// luhai modify 2012-2-23 begin ��ͳҩ���Ƿ��ǵ�һ�δ�ӡ�Ĺ���ɾ����֮ǰһֱ����ֵ��begin
		// inParm.setData("FIRST_PRINT", Boolean.valueOf(false));
		// ǿ�����ó�true����һ�δ�ӡ��ǣ�
		inParm.setData("FIRST_PRINT", Boolean.valueOf(true));
		// luhai modify 2012-2-23 begin ��ͳҩ���Ƿ��ǵ�һ�δ�ӡ�Ĺ���ɾ����֮ǰһֱ����ֵ��end
		TTextFormat station = (TTextFormat) getComponent("COMBO");
		String stationName = station.getText();
		String stationCode = getValueString("COMBO");
		String dispenseOrg = "";
		if (StringUtil.isNullString(stationName))
			stationName = "ȫԺ";
		inParm.setData("STATION_NAME", stationName);
		if (getRadioButton("IN_HOSPITAL_ONE").isSelected()) {
			inParm.setData("DISPENSE_ORG", "TEXT","סԺҩ��");
		}
		if (getRadioButton("IN_STATION_ONE").isSelected()) {
			inParm.setData("DISPENSE_ORG", "TEXT","����");
		}
		
		inParm.setData("STATION_NAME", "TEXT", stationName);
		inParm.setData("START_DATE", TypeTool
				.getTimestamp(getValue("START_DATE")));
		inParm.setData("START_DATE", "TEXT", (TypeTool
				.getTimestamp(getValue("START_DATE")) + "").replace(".0", ""));
		inParm.setData("END_DATE", TypeTool.getTimestamp(getValue("END_DATE")));
		inParm.setData("END_DATE", "TEXT", (TypeTool
				.getTimestamp(getValue("END_DATE")) + "").replace(".0", ""));
		inParm.setData("DONE", Boolean.valueOf(TypeTool
				.getBoolean(getValue("UNCHECK"))));
		// ������ҩȷ�������Ϣ luhai 2012-03-16
		if (Boolean.valueOf(TypeTool.getBoolean(getValue("UNCHECK")))) {
			inParm.setData("DONE_MSG", "TEXT", "��ҩδȷ��");
		} else {
			inParm.setData("DONE_MSG", "TEXT", "��ҩ��ȷ��");
		}
		if (Boolean.valueOf(TypeTool.getBoolean(getValue("LINK_NO")))) {
			inParm.setData("PIVAS", "TEXT", "PIVAs����");
		} else {
			inParm.setData("PIVAS", "TEXT", "��������");
		}
		TParm tableParm = tblMed.getParmValue();
		boolean isStation = TypeTool.getBoolean(getValue("STA"));
		if (StringTool.getBoolean(getValueString("BY_ORDER"))) {
			if (!isStation) {
				messageBox_("������ҩƷ��Ϣ���ܰ�������ʾ");
				return;
			}
			TParm parm = new TParm();
			for (int i = 0; i < tableParm.getCount("ORDER_CODE"); i++) {
				parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
				parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
				parm.addData("DISPENSE_QTY", Double.valueOf(tableParm
						.getDouble("DISPENSE_QTY", i)));
				parm.addData("DISPENSE_UNIT", tableParm.getValue(
						"DISPENSE_UNIT", i));
				parm.addData("OWN_PRICE", Double.valueOf(tableParm.getDouble(
						"OWN_PRICE", i)));
				parm.addData("OWN_AMT", Double.valueOf(tableParm.getDouble(
						"OWN_PRICE", i)
						* tableParm.getDouble("DISPENSE_QTY", i)));
			}
			parm.setCount(parm.getCount("ORDER_CODE"));
			parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
			parm.addData("SYSTEM", "COLUMNS", "DISPENSE_UNIT");
			parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
			inParm.setData("TABLE", parm.getData());
			inParm.setData("IS_STATION", Boolean.valueOf(isStation));
//			System.out.println("----UddUndispenseOrderSum.jhw==inParm:"+inParm);
			openPrintWindow(
					"%ROOT%\\config\\prt\\UDD\\UddUndispenseOrderSum.jhw",
					inParm, false);
		} else {
			if (tableParm.getCount("ORDER_CODE") <= 0) {
				messageBox_("û������");
				return;
			}
			String patName = "";
			TParm parm = new TParm();
			for (int i = 0; i < tableParm.getCount("ORDER_CODE"); i++) {
				parm.addData("BED_NO", tableParm.getValue("BED_NO", i));
				// parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
				parm.addData("MR_NO", "");// ����mr_no ����ʾ
				if (patName.equals(tableParm.getValue("PAT_NAME", i))) {
					parm.addData("PAT_NAME", "");
				} else {
					parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
				}
				patName = tableParm.getValue("PAT_NAME", i);
				parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
				parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
				parm.addData("DISPENSE_QTY", Double.valueOf(tableParm
						.getDouble("DISPENSE_QTY", i)));
				parm.addData("DISPENSE_UNIT", tableParm.getValue(
						"DISPENSE_UNIT", i));
				parm.addData("OWN_PRICE", Double.valueOf(tableParm.getDouble(
						"OWN_PRICE", i)));
				parm.addData("OWN_AMT", Double.valueOf(tableParm.getDouble(
						"OWN_PRICE", i)
						* tableParm.getDouble("DISPENSE_QTY", i)));
			}

			parm.setCount(parm.getCount("ORDER_CODE"));
			parm.addData("SYSTEM", "COLUMNS", "BED_NO");
			parm.addData("SYSTEM", "COLUMNS", "MR_NO");
			parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "DISPENSE_QTY");
			parm.addData("SYSTEM", "COLUMNS", "DISPENSE_UNIT");
			parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
			inParm.setData("TABLE", parm.getData());
//			System.out.println("UddDispenseOrderDetail.jhw=inParm:"+inParm);
			openPrintWindow(
					"%ROOT%\\config\\prt\\UDD\\UddDispenseOrderDetail.jhw",
					inParm, false);
			// luhai modify 2012-04-05 end
		}
	}

	/**
	 * ��ʿվcombo�ĵ���¼�
	 */
	public void onStationQuery() {
		onQuery();
	}

	/**
	 * ������ѡ�¼�
	 */
	public void onStation() {
		setValue("QUERY_BED", "");
		queryBed.setVisible(false);
		callFunction("UI|NO|setVisible", new Object[] { Boolean.valueOf(true) });
	}

	/**
	 * �����ŵ�ѡ�¼�
	 */
	public void onMrNo() {
		setValue("QUERY_BED", "");
		queryBed.setVisible(false);
		callFunction("UI|NO|setVisible", new Object[] { Boolean.valueOf(true) });
	}

	/**
	 * ������ѡ�¼�
	 */
	public void onBedNo() {
		setValue("QUERY_BED", "");
		queryBed.setVisible(true);
		callFunction("UI|NO|setVisible",
				new Object[] { Boolean.valueOf(false) });
	}

	/**
	 * �����б�ȫ��ִ��check_box����¼�
	 */
	public void onExecAll() {
		if (tblPat == null)
			return;
		TParm parm = tblPat.getParmValue();
		if (parm == null)
			return;
		int count = parm.getCount("EXEC");
		boolean value = TypeTool.getBoolean(getValue("EXEC_ALL"));
		for (int i = 0; i < count; i++) {
            // wanglong add 20150226
            String caseNo = parm.getValue("CASE_NO", i);
            String sql =
                    "SELECT * FROM ADM_INP WHERE CASE_NO = '" + caseNo
                            + "' AND DS_DATE IS NOT NULL";
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0) {
                this.messageBox("ִ�г��� " + result.getErrText());
                return;
            }
            if (result.getCount() > 0) {
                this.messageBox(parm.getValue("PAT_NAME", i) + "�Ѿ���Ժ��");
                continue;
            }
            // add end
			parm.setData("EXEC", i, Boolean.valueOf(value));
			tblPat.setValueAt(Boolean.valueOf(value), i, 0);
		}

		onQueryMed();
		onQueryDtl();
		
		// add by wangb 2016/2/29 START
		if (value) {
			if (StringUtils.isNotEmpty(getValueString("COMBO"))) {
				TParm stationParm = this.getStationData(getValueString("COMBO"));
				this.setValue("ATC_MACHINENO", stationParm.getValue("MACHINENO", 0));
				this.setValue("ATC_TYPE", stationParm.getValue("ATC_TYPE", 0));
				this.setValue("ALLATCDO", "Y");
				onATCDo();
			}
		} else {
			this.setValue("ATC_MACHINENO", "");
			this.setValue("ATC_TYPE", "");
			this.setValue("ALLATCDO", "N");
			onATCDo();
		}
		// add by wangb 2016/2/29 START
	}

	/**
	 * ȱҩ��ϸ��ѯ
	 */
	public void onLackStore() {
		if (saveParm == null) {
			messageBox_("û����ҩ����");
			return;
		}
		int count = saveParm.getCount();
		if (count < 1) {
			messageBox_("û����ҩ����");
			return;
		}
		TParm parm = new TParm();
		String names[] = saveParm.getNames();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < names.length; i++)
			if (i < names.length - 1)
				sb.append(names[i]).append(";");

		String name = sb.toString();
		for (int i = 0; i < count; i++)
			parm.addRowData(saveParm, i, name);

		parm = UddChnCheckTool.getInstance().groupByStockParm(parm);
		// ====zhangp 20120803 start
		String tables = "";
		String conditions = "";
		String pha_ctrlcode = getValueString("PHA_CTRLCODE");
		if (!pha_ctrlcode.equals("")) {
			tables = " ,SYS_FEE G, PHA_BASE H, SYS_CTRLDRUGCLASS I ";
			conditions = " AND A.ORDER_CODE = G.ORDER_CODE"
					+ " AND G.ORDER_CODE = H.ORDER_CODE"
					+ " AND H.CTRLDRUGCLASS_CODE = I.CTRLDRUGCLASS_CODE"
					+ " AND I.CTRL_FLG = '" + pha_ctrlcode + "'";
            if ("DOSAGE".equalsIgnoreCase(controlName)) {// wanglong add 20140725 ���˵�Ϊ��ע��ҽ�������ֶ���Ĭ��ֵ�������ʾ�Ǳ�ע��
                conditions += " AND (G.IS_REMARK <> 'Y' OR G.IS_REMARK IS NULL) ";
            }
		} else if ("DOSAGE".equalsIgnoreCase(controlName)) {// wanglong add 20140725 ���˵�Ϊ��ע��ҽ�������ֶ���Ĭ��ֵ�������ʾ�Ǳ�ע��
            tables = " ,SYS_FEE G ";
            conditions +=
                    " AND A.ORDER_CODE = G.ORDER_CODE AND (G.IS_REMARK <> 'Y' OR G.IS_REMARK IS NULL) ";
        }
		parm.setData("TABLES", tables);
		parm.setData("CONDITIONS", conditions);
		// ====zhangp 20120803 end
		TParm result = INDTool.getInstance().defectIndStockQTY(parm);
		count = result.getCount("ORDER_CODE");
		if (count < 0) {
			messageBox_("������ҩ��������");
			return;
		} else {
			tblSht.setParmValue(result);
			return;
		}
	}

	// public void onDtlClick() {
	// int row = tblDtl.getSelectedRow();
	// if (row < 0)
	// return;
	// if (saveParm == null)
	// return;
	// int count = saveParm.getCount();
	// if (count < 1)
	// return;
	// String colName = "EXEC";
	// saveParm.setData(colName, row, Boolean.valueOf(!TypeTool
	// .getBoolean(saveParm.getData("EXEC", row))));
	// tblDtl.setParmValue(saveParm);
	// for (int i = 0; i < count; i++)
	// if (!TypeTool.getBoolean(saveParm.getData("EXEC", i))) {
	// setValue("EXEC_ALL_DTL", Boolean.valueOf(false));
	// return;
	// }
	//
	// setValue("EXEC_ALL_DTL", Boolean.valueOf(true));
	// }
	// ȫ��ִ��
	public void onDoEXE() {
		// �õ���ǰִ������״̬
		boolean nowFlag = (Boolean) this
				.callFunction("UI|ALLEXECUTE|isSelected");
		// �õ�����
		int ordCount = (Integer) this.callFunction("UI|TBL_DTL|getRowCount");
		for (int i = 0; i < ordCount; i++) {
			// ѭ��ȡ���Թ�������
			this.callFunction("UI|TBL_DTL|setValueAt", nowFlag, i, 0);
			saveParm.setData("EXEC", i, nowFlag);
			// ѭ������ÿһ�����ݵĵ�һ�е�ֵ�����ʣ�
		}
	}

	// ��ҩ��ȫ��
	public void onATCDo() {
		// �õ���ǰִ������״̬
		boolean nowFlag = (Boolean) this.callFunction("UI|ALLATCDO|isSelected");
		// �õ�����
		int ordCount = (Integer) this.callFunction("UI|TBL_DTL|getRowCount");
		for (int i = 0; i < ordCount; i++) {
			String ATCFlg = getATCFlgFromSYSFee(saveParm.getRow(i).getValue(
					"ORDER_CODE"));
			String orderNo = saveParm.getValue("ORDER_NO", i);
			int orderSeq = saveParm.getInt("ORDER_SEQ", i);
			String caseNo = saveParm.getValue("CASE_NO", i);
			String orderCode = saveParm.getValue("ORDER_CODE", i);
			String boxFlg = getBoxFlgFromOdiorder(orderNo, caseNo, orderSeq);
			if (ATCFlg.length() == 0 || ATCFlg.equals("N")
					|| boxFlg.equals("Y"))
				continue;
			// ѭ��ȡ���Թ�������
			this.callFunction("UI|TBL_DTL|setValueAt", nowFlag, i, 4);
			// ѭ������ÿһ�����ݵĵ�һ�е�ֵ�����ʣ�
			saveParm.setData("SENDATC_FLG", i, nowFlag);
		}
	}

	/**
	 * ����CASE_NOȡ�÷���ȼ�
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

	/**
	 * �õ�RadioButton����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	// /**
	// * ������ҩ����XML�ļ����ϳ���
	// */
	// public void onSendACT() {
	// TParm pat_parm = tblPat.getParmValue();
	// String pat_sql =
	// " SELECT B.PAT_NAME, A.MR_NO, A.IPD_NO, A.BED_NO, A.DEPT_CODE, "
	// + " C.DEPT_CHN_DESC, A.STATION_CODE, D.STATION_DESC "
	// + " FROM ADM_INP A, SYS_PATINFO B, SYS_DEPT C, SYS_STATION D "
	// + " WHERE A.MR_NO = B.MR_NO "
	// + " AND A.DEPT_CODE = C.DEPT_CODE "
	// + " AND A.STATION_CODE = D.STATION_CODE ";
	// for (int i = 0; i < pat_parm.getCount("CASE_NO"); i++) {
	// if (!"Y".equals(pat_parm.getValue("EXEC", i))) {
	// continue;
	// }
	// String case_no = pat_parm.getValue("CASE_NO", i);
	// TParm patInfo = new TParm(TJDODBTool.getInstance().select(
	// pat_sql + " AND A.CASE_NO='" + case_no + "'"));
	// if (patInfo == null || patInfo.getCount("PAT_NAME") <= 0) {
	// // System.out.println(case_no + "������Ϣ����");
	// continue;
	// }
	// TParm parm = new TParm();
	// // ��������
	// parm.setData("NAME", patInfo.getValue("PAT_NAME", 0));
	// // ������
	// parm.setData("MRNO", patInfo.getValue("MR_NO", 0));
	// // סԺ��
	// parm.setData("IPDNO", patInfo.getValue("IPD_NO", 0));
	// // ������
	// parm.setData("BED_NO", patInfo.getValue("BED_NO", 0));
	// // ����
	// parm.setData("DEPT", patInfo.getValue("DEPT_CODE", 0));
	// // ��������
	// parm.setData("DEPT_DESC", patInfo.getValue("DEPT_CHN_DESC", 0));
	// // ����
	// parm.setData("STATION_CODE", patInfo.getValue("STATION_CODE", 0));
	// // ��������
	// parm.setData("STATION_DESC", patInfo.getValue("STATION_DESC", 0));
	// // ��ҩ����
	// parm.setData("DATE", SystemTool.getInstance().getDate().toString()
	// .substring(0, 19));
	// // סԺע��
	// parm.setData("TYPE", "2");
	//
	// // ҩƷ�б�
	// TParm orderListParm = new TParm();
	// int seq = 1;
	// String order_sql =
	// " SELECT B.ORDER_CODE, B.GOODS_DESC, B.ALIAS_DESC, B.TRADE_ENG_DESC, C.ROUTE_CHN_DESC "
	// + " FROM PHA_BASE A, SYS_FEE B, SYS_PHAROUTE C "
	// + " WHERE A.ORDER_CODE = B.ORDER_CODE "
	// + " AND A.ROUTE_CODE = C.ROUTE_CODE"
	// + " AND B.ATC_FLG_I = 'Y'";
	// TParm order_parm = tblDtl.getParmValue();
	// for (int j = 0; j < order_parm.getCount("ORDER_CODE"); j++) {
	// // �ж��Ƿ�ѡ
	// if (!"Y".equals(order_parm.getValue("EXEC", j))) {
	// continue;
	// }
	// // �ж��Ƿ�Ϊ���˵�ҩƷ
	// if (!case_no.equals(order_parm.getValue("CASE_NO", j))) {
	// continue;
	// }
	// // ���ݰ�ҩ��ע���жϸ�ҩƷ�Ƿ��Ͱ�ҩ��
	// String order_code = order_parm.getValue("ORDER_CODE", j);
	// // System.out.println("order_parm---" + order_parm);
	// // System.out.println("orderInfo---" + order_sql +
	// // " AND A.ORDER_CODE='" + order_code + "'");
	// TParm orderInfo = new TParm(TJDODBTool.getInstance().select(
	// order_sql + " AND A.ORDER_CODE='" + order_code + "'"));
	// if (orderInfo == null || orderInfo.getCount("ORDER_CODE") <= 0) {
	// // System.out.println(order_code+"------------");
	// continue;
	// }
	// // ���
	// orderListParm.addData("SEQ", seq);
	// // ҩƷ����
	// orderListParm.addData("ORDER_CODE",
	// orderInfo.getValue("ORDER_CODE", 0));
	// // ҩƷ��Ʒ��
	// orderListParm.addData("ORDER_GOODS_DESC",
	// orderInfo.getValue("GOODS_DESC", 0));
	// // ҩƷ��ѧ��
	// orderListParm.addData("ORDER_CHEMICAL_DESC",
	// orderInfo.getValue("ALIAS_DESC", 0));
	// // ҩƷӢ����
	// orderListParm.addData("ORDER_ENG_DESC",
	// orderInfo.getValue("TRADE_ENG_DESC", 0));
	// // ����
	// orderListParm.addData("QTY",
	// order_parm.getDouble("DISPENSE_QTY", j));
	// // ��ҩƵ��
	// orderListParm.addData("FREQ",
	// order_parm.getValue("FREQ_CODE", j));
	// // Ͷҩ����ʱ��
	// orderListParm.addData("DAY",
	// order_parm.getValue("ORDER_DATE", j).substring(0, 19));
	// // ��ҩʱ��
	// orderListParm.addData(
	// "DRUG_DATETIME",
	// order_parm.getValue("DISPENSE_EFF_DATE", j).substring(
	// 0, 19));
	// // ��ҩ;��(�ڷ�/����)
	// orderListParm.addData("ROUTE",
	// orderInfo.getValue("ROUTE_CHN_DESC", 0));
	// // ��ǰ����(0:��;1:��ǰ;2:����)
	// orderListParm.addData("MEAL_FLG", "");
	// // �ײ�ʱ��
	// // System.out.println(order_parm.getValue("START_DTTM", j));
	// orderListParm.addData(
	// "START_DTTM",
	// StringTool
	// .getTimestamp(
	// order_parm.getValue("START_DTTM", j),
	// "yyyyMMddHHmm").toString()
	// .substring(0, 19));
	// // �Ͱ�ע��
	// orderListParm.addData("FLG", "");
	// // ��ʱ����ҽ��
	// orderListParm.addData("OrderType", getRadioButton("ST")
	// .isSelected() ? "ST" : "UD");
	// seq++;
	// }
	// if (orderListParm == null || orderListParm.getCount("SEQ") <= 0) {
	// return;
	// }
	// parm.setData("DRUG_LIST_PARM", orderListParm.getData());
	// parm = TIOM_AppServer.executeAction("action.pha.PHAATCAction",
	// "onATCI", parm);
	// if (parm.getErrCode() < 0) {
	// messageBox("�Ͱ�ҩ��ʧ��");
	// return;
	// }
	// messageBox("�Ͱ�ҩ���ɹ�");
	// }
	// }
	/**
	 * ��ҩ������
	 */
	public void onGenATCFile() {
		String type = this.getValueString("ATC_TYPE");
		String machineNo = this.getValueString("ATC_MACHINENO");
		if (type.equals("1"))
			this.onOldATCFile();
		else if (type.equals("2")) {
			if (machineNo.equals("")) {
				this.messageBox("��ҩ��̨�Ų���Ϊ��");
				return;
			}
			try {
				this.onNewATCInsert(machineNo);
			} catch (Exception e) {
				System.out.println("�Ͱ�ҩ�������쳣");
			}
		}
	}

	/**
	 * ȡ�ñ��ѡ��������
	 * 
	 * @param rowName
	 *            String
	 * @param tableName
	 *            String
	 * @return String
	 */
	private String getTableSelectRowData(String rowName, String tableName) {
		return getTableRowData(getTable(tableName).getSelectedRow(), rowName,
				tableName);
	}

	/**
	 * ���к�ȡ�ø�������
	 * 
	 * @param row
	 *            int
	 * @param rowName
	 *            String
	 * @param tableName
	 *            String
	 * @return String
	 */
	private String getTableRowData(int row, String rowName, String tableName) {
		return getTable(tableName).getParmValue().getValue(rowName, row);
	}

	/**
	 * �����Ͱ�ҩ����txt�ļ�
	 */
	public void onOldATCFile() {
		// System.out.println("-----------------------1");
		if (getTable("TBL_PAT").getSelectedRow() < 0) {
			messageBox("��ѡ�񲡻���Ϣ");
			return;
		}
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", "I");
		int count = 0;
		// ҩƷ�б�
		TParm drugListParm = new TParm();
		for (int i = 0; i < saveParm.getCount(); i++) {
			TParm nowOrder = saveParm.getRow(i);
			// �Ͱ�ҩ��ע��
			if (!nowOrder.getBoolean("SENDATC_FLG"))
				continue;
			TParm desc = getOrderData(nowOrder.getValue("ORDER_CODE"));
			TParm ransRate = getPHAOrderTransRate(nowOrder
					.getValue("ORDER_CODE"));
			if (nowOrder.getValue("DSPN_KIND").equals("DS")
					|| nowOrder.getValue("DSPN_KIND").equals("ST")) {
				// ����
				drugListParm.addData("DSPN_KIND", nowOrder
						.getValue("DSPN_KIND"));
				// ��ҩ��
				drugListParm.addData("PRESCRIPT_NO", "0");
				// ����
				drugListParm.addData("PAT_NAME", nowOrder.getValue("PAT_NAME"));
				// ������
				drugListParm.addData("MR_NO", nowOrder.getValue("MR_NO"));
				// �Ͱ�ҩ��ʱ��
				drugListParm.addData("DATE", ("" + SystemTool.getInstance()
						.getDate()).substring(0, 19));
				// ҩƷ�б����
				drugListParm.addData("SEQ", i + 1);
				// ҩƷ����
				drugListParm.addData("ORDER_CODE", nowOrder
						.getValue("ORDER_CODE"));
				// ҩƷ��Ʒ��
				drugListParm.addData("ORDER_GOODS_DESC", desc.getData(
						"TRADE_ENG_DESC", 0));
				int time = getFreqData(nowOrder.getValue("FREQ_CODE")).getInt(
						"FREQ_TIMES", 0);
				double qty = nowOrder.getDouble("DOSAGE_QTY")
						/ nowOrder.getInt("TAKE_DAYS");
				double Minqty = (double) (qty) / time;
				// ҩƷ����
				drugListParm.addData("QTY", Minqty);
				// ҩƷƵ��
				drugListParm.addData("FREQ", nowOrder.getValue("FREQ_CODE"));
				// ��Ժ��ҩ
				if (nowOrder.getValue("DSPN_KIND").equals("DS")) {
					// �ײ�ʱ�䴫��Ĭ��ֵ
					drugListParm.addData("START_DTTM", "000000000000");
					// ��ҩ����
					drugListParm.addData("DAY", nowOrder.getInt("TAKE_DAYS"));
				}
				// ��ʱ
				else if (nowOrder.getValue("DSPN_KIND").equals("ST")) {
					// �ײ�ʱ�䴫��Ĭ��ֵ
					drugListParm.addData("START_DTTM", nowOrder
							.getValue("START_DTTM"));
					drugListParm.addData("DAY", "");
				}
				// �Ͱ�ע�Ǵ���Ĭ��ֵ
				drugListParm.addData("FLG", "Y");
				count++;
			} else {
				String start = nowOrder.getValue("START_DTTM");
				String end = nowOrder.getValue("END_DTTM");
				// ��ѯϸ���SQL
				String sql = "SELECT CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,ORDER_DATETIME,"
						+ "DC_DATE,EXEC_NOTE,EXEC_DEPT_CODE,NS_EXEC_CODE,NS_EXEC_DATE_REAL,NS_EXEC_DATE_REAL,DOSAGE_QTY FROM ODI_DSPND "
						+ "WHERE CASE_NO='"
						+ nowOrder.getValue("CASE_NO")
						+ "' AND ORDER_NO='"
						+ nowOrder.getValue("ORDER_NO")
						+ "' AND ORDER_SEQ='"
						+ nowOrder.getValue("ORDER_SEQ")
						+ "' "
						+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
						+ start
						+ "','YYYYMMDDHH24MISS') "
						+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
						+ end
						+ "','YYYYMMDDHH24MISS')"
						+ " ORDER BY ORDER_DATE||ORDER_DATETIME";

				// ����ϸ���TDS,����������
				TParm result = new TParm(TJDODBTool.getInstance().select(sql));
				if (result.getCount() <= 0 || result.getErrCode() < 0) {
					continue;
				}
				for (int j = 0; j < result.getCount(); j++) {
					// ����
					drugListParm.addData("DSPN_KIND", nowOrder
							.getValue("DSPN_KIND"));
					// ��ҩ��
					drugListParm.addData("PRESCRIPT_NO", "0");
					// ����
					drugListParm.addData("PAT_NAME", nowOrder
							.getValue("PAT_NAME"));
					// ������
					drugListParm.addData("MR_NO", nowOrder.getValue("MR_NO"));
					// �Ͱ�ҩ��ʱ��
					drugListParm.addData("DATE", ("" + SystemTool.getInstance()
							.getDate()).substring(0, 19));
					// ҩƷ�б����
					drugListParm.addData("SEQ", i + 1);
					// ҩƷ����
					drugListParm.addData("ORDER_CODE", nowOrder
							.getValue("ORDER_CODE"));
					// ҩƷ��Ʒ��
					drugListParm.addData("ORDER_GOODS_DESC", desc.getData(
							"TRADE_ENG_DESC", 0));
					// ҩƷ����
					drugListParm.addData("QTY", result.getDouble("DOSAGE_QTY",
							j));
					// ҩƷƵ��
					drugListParm
							.addData("FREQ", nowOrder.getValue("FREQ_CODE"));
					drugListParm.addData("START_DTTM", result.getValue(
							"ORDER_DATE", j)
							+ result.getValue("ORDER_DATETIME", j));
					drugListParm.addData("DAY", "");

					// �Ͱ�ע�Ǵ���Ĭ��ֵ
					drugListParm.addData("FLG", "Y");
					count++;
				}
			}
		}
		if (count > 0) {
			if (drugListParm.getCount("SEQ") <= 0)
				return;
			parm.setData("DRUG_LIST_PARM", drugListParm.getData());
			parm.setData("TYPE", "1");
			parm = TIOM_AppServer.executeAction("action.pha.PHAATCAction",
					"onATCI", parm);
			if (parm.getErrCode() < 0) {
				messageBox("�Ͱ�ҩ��ʧ��");
				return;
			}
			messageBox("�Ͱ�ҩ���ɹ�");
		}
	}

	/**
	 * ��ҩ�����ݲ���
	 */
	public void onNewATCInsert(String machineNo) {
		TParm parm = new TParm();
		Pat pat = new Pat();
		String stationCode = "";
		int count = 0;
		String rxStr = "";
		String strtool1 = "";
		// ȥ���ظ�BarCode��Map
		Set setConst = new HashSet();
		// �ظ�BarCode�ĸ���Map
		Map setIsConstCount = new HashMap();
		// �ظ�seq��Map
		Map setIsConst = new HashMap();
		// �ظ�����
		int isConst = 1;
		for (int i = 0; i < saveParm.getCount(); i++) {
			TParm inparm = saveParm.getRow(i);
			// add by wangb 2015/06/10 ����ȡҩ���߰�ҩ�� START
			if (StringUtils.equals("1", inparm.getValue("TAKEMED_ORG"))) {
				continue;
			}
			// add by wangb 2015/06/10 ����ȡҩ���߰�ҩ�� END
			// �Ͱ�ҩ��ע��
			if (!inparm.getBoolean("SENDATC_FLG"))
				continue;
			strtool1 = inparm.getValue("BAR_CODE");
			// ȥ���ظ�
			if (!setConst.contains(strtool1)) {
				if (rxStr.length() > 0)
					rxStr += ",";
				rxStr += strtool1;
				setConst.add(strtool1);
			} else {
				isConst++;
				setIsConstCount.put(strtool1, isConst);
			}
		}
		// System.out.println("�ظ�������" + isConst);
		// setConst=null;
		// System.out.println("-=-------------" + rxStr);
		Map seqMap = new HashMap();
		if (!rxStr.equals("") || rxStr.length() > 0)
			seqMap = TXNewATCTool.getInstance().executeQuery(rxStr, "I");
		else
			return;
		// System.out.println("-=---Map----------" + seqMap);
		String preStr1 = "";
		String preStr2 = "";
		int seq = 0;
		Map map = new HashMap();
		Map map1 = new HashMap();
		String preNo = "";
		// ҩƷ�б�
		TParm drugListParm = new TParm();
		TParm HISParm = new TParm();
		StringBuffer sbErrLog = new StringBuffer();
		String errLog = "";
		// Ӧ�Ͱ�ҩ������
		int shouldSendAtcCount = 0;
		// ��ѡ�Ͱ�ҩ������
		int selSendAtcCount = 0;
		
		// add by wangb 2016/12/27 ���μ��ÿ�������Ƿ�������װ��BAR_CODE start
		int dataCount = saveParm.getCount();
		Map<String, String> keyMap = new HashMap<String, String>();
		List<Integer> noBarCodeList = new ArrayList<Integer>();
		String key = "";
		String barCode = "";
		for (int i = 0; i < dataCount; i++) {
			// ����ȡҩ���߰�ҩ��
			if (StringUtils.equals("1", saveParm.getValue("TAKEMED_ORG", i))) {
				continue;
			}
			
			// �Ͱ�ҩ��ע��
			if (!saveParm.getBoolean("SENDATC_FLG", i)) {
				continue;
			}
				
			key = saveParm.getValue("CASE_NO", i) + saveParm.getValue("CLASSIFY_TYPE", i);
			barCode = saveParm.getValue("BAR_CODE", i);
			
			// �ҵ������Ϊ�յ�����
			if (StringUtils.isEmpty(barCode)) {
				noBarCodeList.add(i);
				sbErrLog.append("��װ��ҩ���ݳ���,�����Ϊ��!");
				sbErrLog.append("\r\n");
				sbErrLog.append("����Դparm=" + saveParm.getRow(i));
				sbErrLog.append("\r\n");
				continue;
			}
			
			if (!keyMap.containsKey(key)) {
				keyMap.put(key, barCode);
			}
		}
		
		int index = 0;
		for (int k = 0; k < noBarCodeList.size(); k++) {
			index = noBarCodeList.get(k);
			key = saveParm.getValue("CASE_NO", index)
					+ saveParm.getValue("CLASSIFY_TYPE", index);
			if (StringUtils.isEmpty(saveParm.getValue("BAR_CODE", index))) {
				// ʹ�þ����+�÷�������Ϊkeyֵ
				if (StringUtils.isEmpty(keyMap.get(key))) {
					// �����ǰ�������ͬ���������ݣ����½������
					saveParm.setData("BAR_CODE", index, SysPhaBarTool
							.getInstance().getBarCode());
				} else {
					saveParm.setData("BAR_CODE", index, keyMap.get(key));
				}
			}
		}
		// add by wangb 2016/12/27 ���μ��ÿ�������Ƿ�������װ��BAR_CODE end
		
		for (int i = 0; i < saveParm.getCount(); i++) {
			errLog = "";
			TParm inparm = saveParm.getRow(i);
			// add by wangb 2016/02/29 ����ȡҩ���߰�ҩ�� START
			if (StringUtils.equals("1", inparm.getValue("TAKEMED_ORG"))) {
				continue;
			}
			// add by wangb 2016/02/29 ����ȡҩ���߰�ҩ�� END
			
			if (StringUtils.equals("Y", inparm.getValue("SHOULD_SEND_ATC_FLG"))) {
				shouldSendAtcCount++;
			}
			
			// �Ͱ�ҩ��ע��
			if (!inparm.getBoolean("SENDATC_FLG")) {
				continue;
			} else {
				selSendAtcCount++;
			}
			String sendTime = TXNewATCTool.getInstance().getSendAtcFlg(inparm);
			if (!sendTime.equals("")) {
				switch (this.messageBox("��ʾ��Ϣ", "������: "
						+ inparm.getValue("MR_NO") + " ���� :"
						+ inparm.getValue("PAT_NAME") + " ҽ�� : "
						+ inparm.getValue("ORDER_DESC") + "\r\n"
						+ "���͹���ҩ�����ϴ�ʱ��:" + sendTime + "���Ƿ����ͣ�",
						this.YES_NO_OPTION)) {
				case 0: // ����
					break;
				case 1: // ������
					continue;
				}
			}
			String BarCode = inparm.getValue("BAR_CODE");
			if (BarCode.equals("")) {
				errLog = "�Ͱ�ҩ��ҽ���쳣BAR_CODE���: ������ "
					+ inparm.getValue("MR_NO") + " ���� "
					+ inparm.getValue("PAT_NAME") + "ҽ��  "
					+ inparm.getValue("ORDER_CODE");
				sbErrLog.append(errLog);
				sbErrLog.append("\r\n");
				System.out.println(errLog);
				// modify by wangb 2016/12/26 δ������������ŵ�����Ҳ�������ݲ�����У�����������������û���ʾ��Ϣ���ڷ�������
//				continue;
			}
			// ��ҩ������
			drugListParm.setData("TYPE", 2);
			// ������ 1
			drugListParm.addData("PRESCRIPTIONNO", inparm.getValue("BAR_CODE"));
			preStr1 = BarCode;
			// ��ͬ����ǩ��ȡ˳���
			if (map1.get(preStr1) == null) {
				// if (!preStr1.equals(preStr2)) {
				// // �˴���ǩ�Ŵ��ڿ���ȡ����+1
				seq = (Integer) seqMap.get(BarCode) + 1;
				// System.out.println("���ظ���:" + BarCode + "    seq:" + seq);
				// ˳��� 2
				drugListParm.addData("SEQNO", seq);
				preStr2 = preStr1;
				map.put(preStr1, seq);
			} else {
				// int iseq = (Integer)setIsConstCount.get(preStr1);
				// System.out.println("�ظ���:" + BarCode + "    seq:" + seq +
				// "   iseq:" + iseq);
				// if(!setIsConst.containsKey(preStr1)){
				// setIsConst.put(preStr1, seq);
				// }
				seq = (Integer) map.get(preStr1) + 1;
				// System.out.println("-----seq------" + seq);
				// ˳��� 2
				drugListParm.addData("SEQNO", seq);
				map.put(preStr1, seq);
			}
			map1.put(preStr1, preStr1);
			// ��ţ�Ĭ�ϣ�3
			drugListParm.addData("GROUP_NO", 1);
			// �����ţ�ҩ����̨���ã� 4
			drugListParm.addData("MACHINENO", TypeTool.getInt(machineNo));
			// ����״̬��Ĭ�ϣ� 5
			drugListParm.addData("PROCFLG", 0);
			// ����ID 6
			drugListParm.addData("PATIENTID", inparm.getValue("MR_NO"));
			// �������� 7
			drugListParm.addData("PATIENTNAME", inparm.getValue("PAT_NAME"));
			pat = pat.onQueryByMrNo(inparm.getValue("MR_NO"));
			// ��������ƴ��8
			drugListParm.addData("ENGLISHNAME", "");
			// �������� 9
			drugListParm.addData("BIRTHDAY", pat.getBirthday());
			// �Ա� 10
			drugListParm.addData("SEX", pat.getSexCode());
			// ��� ���� �� 1:���� 2:סԺ[����] 3:סԺ[��ʱ] ��Ժ��ҩ�������ﴦ��
			String ioFlg = "";
			if (inparm.getValue("DSPN_KIND").equals("UD")
					|| inparm.getValue("DSPN_KIND").equals("F"))
				ioFlg = "2";
			else if (inparm.getValue("DSPN_KIND").equals("ST"))
				ioFlg = "3";
			else if (inparm.getValue("DSPN_KIND").equals("DS"))
				ioFlg = "1";
			drugListParm.addData("IOFLG", ioFlg);
			// �������� 12
			drugListParm.addData("WARDCD", inparm.getValue("STATION_CODE"));
			// �������� 13
			drugListParm.addData("WARDNAME", this.getStationData(
					inparm.getValue("STATION_CODE"))
					.getValue("STATION_DESC", 0));
			// ������14
			drugListParm.addData("ROOMNO", this.getRoomDesc(inparm
					.getValue("ROOM_CODE")));
			// ������ 15
			drugListParm.addData("BEDNO", inparm.getValue("BED_NO_DESC"));
			// ҽʦ���� 16
			drugListParm.addData("DOCTORCD", inparm.getValue("ORDER_DR_CODE")
					.length() > 7 ? new String(inparm.getValue("ORDER_DR_CODE")
					.getBytes(), 0, 7) : inparm.getValue("ORDER_DR_CODE"));
			// ҽʦ���� 17
			drugListParm.addData("DOCTORNAME", getDrDesc(inparm
					.getValue("ORDER_DR_CODE")));
			// ����ʱ�� 18
			drugListParm.addData("PRESCRIPTIONDATE", inparm
					.getTimestamp("ORDER_DATE"));
			int day = inparm.getInt("TAKE_DAYS");
			int time = getFreqData(inparm.getValue("FREQ_CODE")).getInt(
					"FREQ_TIMES", 0);
			// ����ҽ��
			if (ioFlg.equals("2")) {
				String start = inparm.getValue("START_DTTM");
				TParm sysparm = new TParm(TJDODBTool.getInstance().select(
						"SELECT * FROM ODI_SYSPARM"));
				// �[ˎ�r�g
				String udDispTime = ("" + SystemTool.getInstance().getDate())
						.substring(0, 10).replaceAll("-", "")
						+ sysparm.getValue("DSPN_TIME", 0);
				if ((StringTool.getTimestamp(start.substring(0, 12),
						"yyyyMMddHHmm").getTime() < StringTool.getTimestamp(
						udDispTime, "yyyyMMddHHmm").getTime())
						&& inparm.getValue("DSPN_KIND").equals("UD")) {
					System.out.println("�Ͱ�ҩ������ҽ�������쳣�t���_ʼ�r�gС춮�ǰ�[ˎ�r�g: ������ "
							+ inparm.getValue("MR_NO") + " ���� "
							+ inparm.getValue("PAT_NAME") + "ҽ��  "
							+ inparm.getValue("ORDER_CODE") + " �_ʼ�r�g��" + start);
				}
				String end = inparm.getValue("END_DTTM");
				// ��ѯϸ���SQL
				String sql = "SELECT CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,ORDER_DATETIME,"
						+ "DC_DATE,EXEC_NOTE,EXEC_DEPT_CODE,NS_EXEC_CODE,NS_EXEC_DATE_REAL,NS_EXEC_DATE_REAL FROM ODI_DSPND "
						+ "WHERE CASE_NO='"
						+ inparm.getValue("CASE_NO")
						+ "' AND ORDER_NO='"
						+ inparm.getValue("ORDER_NO")
						+ "' AND ORDER_SEQ='"
						+ inparm.getValue("ORDER_SEQ")
						+ "' "
						+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
						+ start
						+ "','YYYYMMDDHH24MISS') "
						+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
						+ end
						+ "','YYYYMMDDHH24MISS')"
						+ " ORDER BY ORDER_DATE||ORDER_DATETIME";

				// ����ϸ���TDS,����������
				TParm result = new TParm(TJDODBTool.getInstance().select(sql));
				if (result.getCount() <= 0) {
					System.out.println("�Ͱ�ҩ������ҽ���쳣����]�Д���: ������ "
							+ inparm.getValue("MR_NO") + " ���� "
							+ inparm.getValue("PAT_NAME") + "ҽ��  "
							+ inparm.getValue("ORDER_CODE"));
					// ��һ����ҩʱ�� 19
					drugListParm.addData("TAKEDATE", "");
					// ��ʼ���õ�ʱ����� 20
					drugListParm.addData("TAKETIME", "");
					// �����õ�ʱ����� 21
					drugListParm.addData("LASTTIME", "");
				} else {
					// ��һ����ҩʱ�� 19
					drugListParm.addData("TAKEDATE", StringTool.getTimestamp(
							result.getValue("ORDER_DATE", 0), "yyyyMMdd"));
					// ��ʼ���õ�ʱ����� 20
					drugListParm.addData("TAKETIME", 
							result.getValue("ORDER_DATETIME", 0));
					// �����õ�ʱ����� 21
					drugListParm.addData("LASTTIME",
							result.getValue("ORDER_DATETIME", result.getCount() - 1));
				}
				int datecount=result.getCount()<=0?1:result.getCount();
				day=(int) Math.ceil((double)datecount/time);
				// �������� 33
				drugListParm.addData("DISPENSE_DAYS", day);
			}
			// סԺ��ʱ
			else if (ioFlg.equals("3")) {
				// ��һ����ҩʱ�� 19
				drugListParm.addData("TAKEDATE", SystemTool.getInstance()
						.getDate());
				// ��ʼ���õ�ʱ����� 20
				drugListParm.addData("TAKETIME", "2355");
				// �����õ�ʱ����� 21
				drugListParm.addData("LASTTIME", "2355");
				// �������� 33
				drugListParm.addData("DISPENSE_DAYS", day);
			}
			// ��Ժ��ҩ
			else if (ioFlg.equals("1")) {
				// ��һ����ҩʱ�� 19
				drugListParm.addData("TAKEDATE", SystemTool.getInstance()
						.getDate());
				// ��ʼ���õ�ʱ����� 20
				drugListParm.addData("TAKETIME", "");
				// �����õ�ʱ����� 21
				drugListParm.addData("LASTTIME", "");
				// �������� 33
				drugListParm.addData("DISPENSE_DAYS", day);

			}
			// �������Ĭ��Ϊ1�� 22
			drugListParm.addData("PRESC_CLASS", 0);
			// ҩƷ���� 23
			drugListParm.addData("DRUGCD", inparm.getValue("ORDER_CODE"));
			TParm order = getOrderData(inparm.getValue("ORDER_CODE"));
			// ҩƷ�� 24
			drugListParm.addData("DRUGNAME", order.getValue("ORDER_DESC", 0)
					+ "" + order.getValue("SPECIFICATION", 0));
			// ҩƷ����(Ĭ��Ϊ��) 25
			drugListParm.addData("DRUGSHAPE", "");
			double mediQty = inparm.getDouble("MEDI_QTY");
			// ��ҩ���� 26
			drugListParm.addData("PRESCRIPTIONDOSE", mediQty);
			// ��ҩ��λ 27
			drugListParm.addData("PRESCRIPTIONUNIT", getUnitDesc(inparm
					.getValue("MEDI_UNIT")));
			double dispenQty = inparm.getDouble("DISPENSE_QTY");
			double qty = (double) (mediQty / this.getPHAOrderTransRate(
					inparm.getValue("ORDER_CODE")).getDouble("MEDI_QTY", 0));
			BigDecimal sf = new BigDecimal(String.valueOf(qty));
			BigDecimal data = sf.setScale(2, RoundingMode.HALF_UP);
			// System.out.println("---------------------------------------------4");
			// ��ҩ���� 28
			drugListParm.addData("DISPENSEDDOSE", data.doubleValue());
			// ��ҩ������ 29
			drugListParm.addData("DISPENSEDTOTALDOSE", dispenQty);
			// ��ҩ��λ 30
			drugListParm.addData("DISPENSEDUNIT", getUnitDesc(inparm
					.getValue("DISPENSE_UNIT")));
			// ÿ�������� 31
			drugListParm.addData("AMOUNT_PER_PACKAGE", this
					.getPHAOrderTransRate(inparm.getValue("ORDER_CODE"))
					.getDouble("MEDI_QTY", 0));
			String mandesc = this.getManDesc(getOrderData(
					inparm.getValue("ORDER_CODE")).getValue("MAN_CODE", 0));
			// ������ 32
			drugListParm.addData("FIRM_ID",
					mandesc.getBytes().length > 20 ? new String(mandesc
							.getBytes(), 0, 20) : mandesc);
			// Ƶ�� 34
			// drugListParm
			// .setData("FREQ_DESC_CODE", inparm.getValue("FREQ_CODE"));
			drugListParm.addData("FREQ_DESC_CODE", "");
			// Ƶ������ 35
			drugListParm.addData("FREQ_DESC", getFreqData(
					inparm.getValue("FREQ_CODE")).getValue("FREQ_CHN_DESC", 0));
			// һ����ô������գ� 36
			drugListParm.addData("FREQ_COUNTER", "");
			String timeCode = TXNewATCTool.getTimeLine(inparm
					.getValue("FREQ_CODE"));
			// סԺ���� �� ��Ժ��ҩ
			if (ioFlg.equals("2") || ioFlg.equals("1")) {
				// ����ʱ����� 37
				drugListParm.addData("FREQ_DESC_DETAIL_CODE", timeCode);
				String timeDetail = TXNewATCTool.getTimeDetail(inparm
						.getValue("FREQ_CODE"));
				// ����ʱ����ϸ 38
				drugListParm.addData("FREQ_DESC_DETAIL", timeDetail);
			}
			// סԺ��ʱ
			else if (ioFlg.equals("3")) {
				drugListParm.addData("FREQ_DESC_DETAIL_CODE", "2355");
				drugListParm.addData("FREQ_DESC_DETAIL", "����");
			}
			// ��ҩ˵������ 39
			drugListParm.addData("EXPLANATION_CODE", "");
			// ��ҩ˵�� 40
			drugListParm.addData("EXPLANATION", "");
			// ��ҩ;�� 41
			drugListParm.addData("ADMINISTRATION_NAME", this
					.getRouteDesc(inparm.getValue("ROUTE_CODE")));
			// ��ע 42
			drugListParm.addData("DOCTORCOMMENT", "");
			// ��ҩ˳�� 43
			drugListParm.addData("BAGORDERBY", "");
			// ����ʱ�� 44
			drugListParm.addData("MAKERECTIME", ("" + SystemTool.getInstance()
					.getDate()).substring(0, 19));
			// �Է�����ʱ�� 45
			drugListParm.addData("UPDATERECTIME", "");
			// Ԥ�� 46
			drugListParm.addData("FILLER", "");
			// ҽ���� 47
			drugListParm.addData("ORDER_NO", Long.parseLong(inparm
					.getValue("ORDER_NO")));
			// ˳��� 48
			drugListParm.addData("ORDER_SUB_NO", inparm.getValue("ORDER_SEQ"));
			// ������ʱ����
			// �����ӡ��ʽ 49
			drugListParm.addData("BAGPRINTFMT", "");
			// �ߴ� 50
			drugListParm.addData("BAGLEN", "");
			// ��ҩ�� 51
			drugListParm.addData("TICKETNO", "");
			// ҩ����ӡ�ò����� 52
			drugListParm.addData("BAGPRINTPATIENTNM", "");
			// Ԥ���ô�ӡ���ݣ��������� 53
			drugListParm.addData("FREEPRINTITEM_PRESC1", "");
			// Ԥ���ô�ӡ���ݣ�����2�� 54
			drugListParm.addData("FREEPRINTITEM_PRESC2", "");
			// Ԥ���ô�ӡ���ݣ�����3�� 55
			drugListParm.addData("FREEPRINTITEM_PRESC3", "");
			// Ԥ���ô�ӡ���ݣ�����4�� 56
			drugListParm.addData("FREEPRINTITEM_PRESC4", "");
			// Ԥ���ô�ӡ���ݣ�����5�� 57
			drugListParm.addData("FREEPRINTITEM_PRESC5", "");
			// Ԥ���ô�ӡ���ݣ�ҩƷ1�� 58
			drugListParm.addData("FREEPRINTITEM_DRUG1", inparm
					.getValue("ORDER_NO"));
			// Ԥ���ô�ӡ���ݣ�ҩƷ2�� 59
			drugListParm.addData("FREEPRINTITEM_DRUG2", inparm
					.getValue("ORDER_SEQ"));
			// Ԥ���ô�ӡ���ݣ�ҩƷ3�� 60
			drugListParm.addData("FREEPRINTITEM_DRUG3", "");
			// Ԥ���ô�ӡ���ݣ�ҩƷ4�� 61
			drugListParm.addData("FREEPRINTITEM_DRUG4", "");
			// Ԥ���ô�ӡ���ݣ�ҩƷ5�� 62
			drugListParm.addData("FREEPRINTITEM_DRUG5", "");
			// �ۺϰ�ҩ�ñ�־λ(���й���ʹ�� 63
			drugListParm.addData("SYNTHETICFLG", "");
			// 0:����ֽ��1:�ڴ˴�����׷��һ������ֽ 64
			drugListParm.addData("CUTFLG", "");
			// ����ʱ�䣨number�͡��޷����뺺�֣� 65
			drugListParm.addData("PHARMACYTIME", "");
			// ҩƷ�ϵĿ�ӡ 66
			drugListParm.addData("CARVEDSEAL", "");
			// ҩƷ��ӡ��� 67
			drugListParm.addData("CARVEDSEALABB", "");
			// ������Ϣ�����룱 68
			drugListParm.addData("PREBARCODE1", "");
			// ������Ϣ�����룲 69
			drugListParm.addData("PREBARCODE2", "");
			// ҩƷ��Ϣ������ 70
			drugListParm.addData("PREDRUGBARCODE", "");
			// �������ʽ 71
			drugListParm.addData("PREBARCODEFMT", "");
			// ===========================================================
			drugListParm.addData("CASE_NO", inparm.getValue("CASE_NO"));
			drugListParm.addData("HISORDER_NO", inparm.getValue("ORDER_NO"));
			drugListParm.addData("ORDER_SEQ", inparm.getValue("ORDER_SEQ"));
			drugListParm.addData("START_DTTM", inparm.getValue("START_DTTM"));
			drugListParm.addData("BAR_CODE", inparm.getValue("BAR_CODE"));
			drugListParm.addData("END_DTTM", inparm.getValue("END_DTTM"));
			drugListParm.addData("OPT_USER", Operator.getID());
			drugListParm.addData("OPT_TERM", Operator.getIP());
			count++;
		}
		drugListParm.setCount(count);
		
		// add by wangb ���ݴ�����־�г�����Ϊ���ֶΣ�������ɸ�� start
		boolean errFlg = false;
		for (int j = 0; j < count; j++) {
			errFlg = false;
			if (StringUtils.isEmpty(drugListParm.getValue("FREQ_DESC", j))) {
				errFlg = true;
				sbErrLog.append("��װ��ҩ���ݳ���,Ƶ��Ϊ��");
				sbErrLog.append("\r\n");
				drugListParm.setData("FREQ_DESC", j, SYSPhaFreqTool
						.getInstance().selectdata(
								saveParm.getValue("FREQ_CODE", j)).getValue(
								"FREQ_CHN_DESC", 0));
			}
			
			if (StringUtils.isEmpty(drugListParm.getValue("FREQ_DESC_DETAIL", j))) {
				errFlg = true;
				sbErrLog.append("��װ��ҩ���ݳ���,Ƶ����ϸΪ��");
				sbErrLog.append("\r\n");
				drugListParm.setData("FREQ_DESC_DETAIL", j, TXNewATCTool
						.getTimeDetail(saveParm.getValue("FREQ_CODE", j)));
			}
			
			if (StringUtils.isEmpty(drugListParm.getValue("DRUGNAME", j))) {
				errFlg = true;
				sbErrLog.append("��װ��ҩ���ݳ���,ҩƷ����Ϊ��");
				sbErrLog.append("\r\n");
				drugListParm.setData("DRUGNAME", j, saveParm.getValue(
						"ORDER_DESC", j).replace("(", "").replace(")", "")
						.replace(" ", ""));
			}
			
			if (StringUtils.isEmpty(drugListParm.getValue("DISPENSEDUNIT", j))) {
				errFlg = true;
				sbErrLog.append("��װ��ҩ���ݳ���,��ҩ��λΪ��");
				sbErrLog.append("\r\n");
				drugListParm.setData("DISPENSEDUNIT", j, getUnitDesc(saveParm
						.getValue("DISPENSE_UNIT", j)));
			}
			
			if (errFlg) {
				sbErrLog.append("����Դparm=" + saveParm.getRow(j));
				sbErrLog.append("\r\n");
			}
		}
		// add by wangb ���ݴ�����־�г�����Ϊ���ֶΣ�������ɸ�� end
		
		if (count > 0) {
			parm.setData("DRUG_LIST_PARM", drugListParm.getData());
			parm.setData("TYPE", "2");
			parm = TIOM_AppServer.executeAction("action.pha.PHAATCAction",
					"onATCI", parm);
			if (parm.getErrCode() == -1) {
				messageBox("�Ͱ�ҩ��ʧ��");
//				return;
			} else if (parm.getErrCode() == -2) {
				messageBox("��������ʧ��");
//				return;
			} else {
				messageBox("�Ͱ�ҩ���ɹ�");
			}
		}
		
		int insertCount = parm.getInt("INSERT_COUNT");
		sbErrLog.append("Ӧ�Ͱ�ҩ������:" + shouldSendAtcCount);
		sbErrLog.append(",��ѡ�Ͱ�ҩ������:" + selSendAtcCount);
		sbErrLog.append(",�Ͱ�ҩ���ӿ�����:" + count);
		sbErrLog.append(",�����ҩ�����ݿ�����:" + insertCount);
		TParm errLogParm = new TParm();
		errLogParm.setData("MSG", sbErrLog.toString());
		TIOM_AppServer.executeAction("action.pha.PHAATCAction",
				"printLog", errLogParm);
	}

	/**
	 * ȡ��ҩƷ��ҩ��λ�Ϳ�浥λת����
	 * 
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getPHAOrderTransRate(String orderCode) {
		return new TParm(getDBTool().select(
				" SELECT DOSAGE_QTY/STOCK_QTY TRANS_RATE,MEDI_QTY "
						+ " FROM PHA_TRANSUNIT " + " WHERE ORDER_CODE='"
						+ orderCode + "'"));
	}

	/**
	 * ȡ�ò�������
	 * 
	 * @param stationCode
	 * @return
	 */
	public TParm getStationData(String stationCode) {
		return new TParm(getDBTool().select(
				" SELECT STATION_DESC,MACHINENO,ATC_TYPE "
						+ " FROM SYS_STATION " + " WHERE STATION_CODE='"
						+ stationCode + "'"));
	}

	/**
	 * ȡ��ҩƷ����
	 * 
	 * @param orderCode
	 *            String
	 * @return TParm
	 */
	public TParm getOrderData(String orderCode) {
		return new TParm(
				getDBTool()
						.select(
								" SELECT ORDER_DESC,GOODS_DESC,ALIAS_DESC,TRADE_ENG_DESC,DESCRIPTION,MAN_CODE,SPECIFICATION"
										+ " FROM SYS_FEE"
										+ " WHERE ORDER_CODE='"
										+ orderCode
										+ "'"));
	}

	/**
	 * ȡ�ÿ�������
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String deptCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT DEPT_CHN_DESC" + " FROM SYS_DEPT "
						+ " WHERE DEPT_CODE='" + deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}

	/**
	 * ȡ����Ա����
	 * 
	 * @param deptCode
	 * @return
	 */
	public String getDrDesc(String userId) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT USER_NAME " + " FROM SYS_OPERATOR "
						+ " WHERE USER_ID='" + userId + "'"));
		String userName = "";
		if (parm.getCount() > 0)
			userName = parm.getValue("USER_NAME", 0);
		return userName;
	}

	/**
	 * ȡ�õ�λ����
	 * 
	 * @param deptCode
	 * @return
	 */
	public String getUnitDesc(String unitCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT UNIT_CHN_DESC " + " FROM SYS_UNIT "
						+ " WHERE UNIT_CODE='" + unitCode + "'"));
		String unitDesc = "";
		if (parm.getCount() > 0)
			unitDesc = parm.getValue("UNIT_CHN_DESC", 0);
		return unitDesc;
	}

	/**
	 * ȡ����ҩ;������
	 * 
	 * @param deptCode
	 * @return
	 */
	public String getRouteDesc(String routeCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT ROUTE_CHN_DESC " + " FROM SYS_PHAROUTE "
						+ " WHERE ROUTE_CODE='" + routeCode + "'"));
		String routeDesc = "";
		if (parm.getCount() > 0)
			routeDesc = parm.getValue("ROUTE_CHN_DESC", 0);
		return routeDesc;
	}

	/**
	 * ȡ��Ƶ������
	 * 
	 * @param freqCode
	 * @return
	 */
	public TParm getFreqData(String freqCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT FREQ_CHN_DESC,FREQ_TIMES " + " FROM SYS_PHAFREQ "
						+ " WHERE FREQ_CODE='" + freqCode + "'"));
		return parm;
	}

	/**
	 * ȡ��������������
	 * 
	 * @param freqCode
	 * @return
	 */
	public String getManDesc(String manCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT MAN_CHN_DESC " + " FROM SYS_MANUFACTURER "
						+ " WHERE MAN_CODE='" + manCode + "'"));
		String manDesc = "";
		if (parm.getCount() > 0)
			manDesc = parm.getValue("MAN_CHN_DESC", 0);
		return manDesc;
	}

	/**
	 * ȡ�÷�������
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getRoomDesc(String roomCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT ROOM_DESC" + " FROM SYS_ROOM " + " WHERE ROOM_CODE='"
						+ roomCode + "'"));
		return parm.getValue("ROOM_DESC", 0);
	}

	/**
	 * ��������table���޸������¼�
	 * 
	 * @param obj
	 *            Object
	 */
	public void onDownTableCheckBoxChangeValue(Object obj) {

		// ��õ����table����
		TTable tableDown = (TTable) obj;
		// ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
		tableDown.acceptText();
		// ���ѡ�е���/��
		int col = tableDown.getSelectedColumn();
		int row = tableDown.getSelectedRow();
		// ��������table�ϵ��
		// ���ѡ�е��ǵ����оͼ���ִ�ж���--�Ͱ�ҩ��
		if (col == 0) {
			boolean exeFlg;
			// ��õ��ʱ��ֵ
			exeFlg = TCM_Transform.getBoolean(tableDown.getValueAt(row, col));
			saveParm.setData("EXEC", row, exeFlg);
		}
		if (col == 4) {
			String orderNo = saveParm.getValue("ORDER_NO", row);
			int orderSeq = saveParm.getInt("ORDER_SEQ", row);
			String caseNo = saveParm.getValue("CASE_NO", row);
			String orderCode = saveParm.getValue("ORDER_CODE", row);
			String ATCFlg = getATCFlgFromSYSFee(orderCode);
			String boxFlg = getBoxFlgFromOdiorder(orderNo, caseNo, orderSeq);
			if (tableDown.getValueAt(row, col).equals("Y")
					&& (ATCFlg.length() == 0 || ATCFlg.equals("N"))) {
				callFunction("UI|TBL_DTL|setValueAt", "N", row, 0);
				tableDown.acceptText();
				this.tblDtl.setItem(row, "SENDATC_FLG", "N");
				messageBox("��ҩƷ�޷��Ͱ�ҩ��");
				return;
			}
			if (tableDown.getValueAt(row, col).equals("Y")
					&& boxFlg.equals("Y")) {
				callFunction("UI|TBL_DTL|setValueAt", "N", row, 0);
				tableDown.acceptText();
				this.tblDtl.setItem(row, "SENDATC_FLG", "N");
				messageBox("�м�ҩƷ�޷��Ͱ�ҩ��");
				return;
			}
			// ��õ��ʱ��ֵ
			ATCFlg = TCM_Transform.getString(tableDown.getValueAt(row, col));
			// ����ִ�б��
			saveParm.setData("SENDATC_FLG", row, TypeTool.getBoolean(ATCFlg));
		}
		// ===zhangp 20120802 start
		TTabbedPane TTabbedPane = (TTabbedPane) getComponent("TTabbedPane");
		if (TTabbedPane.getSelectedIndex() == 1) {
			TParm tableParm = tableDown.getParmValue();
			String link_no = tableParm.getValue("LINK_NO", row);
			String case_no = tableParm.getValue("CASE_NO", row);
			String exec = tableParm.getValue("EXEC", row);
			for (int i = 0; i < tableParm.getCount("EXEC"); i++) {
				if (tableParm.getValue("LINK_NO", i).equals(link_no)
						&& !link_no.equals("")
						&& tableParm.getValue("CASE_NO", i).equals(case_no)) {
					tableParm.setData("EXEC", i, exec);
				}
			}
			tblDtl.setParmValue(tableParm);
		}
		// ===zhangp 20120802 end
	}
	public boolean onDownTableBatchNoChangeValue(TTableNode tNode) {
		int row = tNode.getRow();
		int column = tNode.getColumn();
		String batchNo = "";
		String colName = tblDtl.getParmMap(column);
		if(column==23) {
			if ("BATCH_NO".equalsIgnoreCase(colName)) {
				 batchNo = (String) tNode.getValue();			
			}
			saveParm.setData("BATCH_NO",row,batchNo);	
		}	
		/*TTable tableDown = (TTable) obj;
		int col = tableDown.getSelectedColumn();
		int row = tableDown.getSelectedRow();
		this.messageBox(col+"=="+row);		*/
		return false;
	}
	// /**
	// * ��������table���޸������¼�
	// *
	// * @param obj
	// * Object
	// */
	// public void onPatTableCheckBoxChangeValue(Object obj) {
	//
	// // ��õ����table����
	// TTable tableDown = (TTable) obj;
	// // ֻ��ִ�и÷�����ſ����ڹ���ƶ�ǰ���ܶ���Ч���������Ҫ��
	// tableDown.acceptText();
	// int col = tableDown.getSelectedColumn();
	// int row=this.tblPat.getSelectedRow();
	// // ��������table�ϵ��
	// if (col == 0) {
	// if ("DOSAGE".equalsIgnoreCase(controlName)) {
	// // this.messageBox_("fafa");
	// // this.messageBox_(tblPat.getValueAt(row, col)+"");
	// if (TCM_Transform.getBoolean(tblPat.getValueAt(row, col))) {
	// String stationCode = getTableSelectRowData("STATION_CODE",
	// "TBL_PAT");
	// // this.messageBox_(stationCode);
	// String machineNo = this.getStationData(stationCode)
	// .getValue("MACHINENO", 0);
	// String atcType = this.getStationData(stationCode).getValue(
	// "ATC_TYPE", 0);
	// // this.messageBox_(atcType);
	// callFunction("UI|ALLATCDO|isSelected",false);
	// this.setValue("ATC_MACHINENO", machineNo);
	// this.setValue("ATC_TYPE", atcType);
	// } else {
	// // this.messageBox_(tblPat.getValueAt(row, col)+"");
	// callFunction("UI|ALLATCDO|isSelected",false);
	// this.setValue("ATC_MACHINENO", "");
	// this.setValue("ATC_TYPE", "");
	// }
	// }
	// }
	// }

	/**
	 * סԺ��ҩ��ע��
	 * 
	 * @param orderCode
	 * @return
	 */
	private String getATCFlgFromSYSFee(String orderCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT ATC_FLG_I " + " FROM SYS_FEE" + " WHERE ORDER_CODE='"
						+ orderCode + "'"));
		if (parm.getCount() <= 0)
			return "";
		return parm.getValue("ATC_FLG_I", 0);
	}

	/**
	 * ��ҩ��װע��
	 * 
	 * @param orderNo
	 * @param caseNo
	 * @param Seq
	 * @return
	 */
	private String getBoxFlgFromOdiorder(String orderNo, String caseNo, int Seq) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT GIVEBOX_FLG " + " FROM ODI_ORDER"
						+ " WHERE ORDER_NO='" + orderNo + "' AND CASE_NO='"
						+ caseNo + "' AND ORDER_SEQ=" + Seq));
		if (parm.getCount() <= 0)
			return "";
		return parm.getValue("GIVEBOX_FLG", 0);
	}

	/**
	 * ȡ��ѡ��ļ��ͷ���
	 * 
	 * @return String
	 */
	private String getDoseType() {
		String getDoseType = "";
		List list = new ArrayList();
		if ("Y".equals(this.getValueString("DOSE_TYPEO"))) {
			list.add("O");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEE"))) {
			list.add("E");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEI"))) {
			list.add("I");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEF"))) {
			list.add("F");
		}

		if (list == null || list.size() == 0) {
			return "";
		} else {
			getDoseType = " AND F.CLASSIFY_TYPE IN (";
			for (int i = 0; i < list.size(); i++) {
				getDoseType = getDoseType + "'" + list.get(i) + "' ,";
			}
			getDoseType = getDoseType.substring(0, getDoseType.length() - 1)
					+ ")";
		}
		return getDoseType;
	}

	/**
	 * ȡ��ѡ��ļ��ͷ���
	 * 
	 * @return String
	 */
	private String getDoseTypeByWhere() {
		String getDoseType = "";
		List list = new ArrayList();
		if ("Y".equals(this.getValueString("DOSE_TYPEO"))) {
			list.add("O");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEE"))) {
			list.add("E");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEI"))) {
			list.add("I");
		}
		if ("Y".equals(this.getValueString("DOSE_TYPEF"))) {
			list.add("F");
		}

		if (list == null || list.size() == 0) {
			return "";
		} else {
			for (int i = 0; i < list.size(); i++) {
				getDoseType = getDoseType + "'" + list.get(i) + "' ,";
			}
			getDoseType = getDoseType.substring(0, getDoseType.length() - 1);
		}
		return getDoseType;
	}

	/**
	 * ȡ��ѡ��ļ��ͷ������ڱ�����ʾ
	 * 
	 * @return String
	 */
	private String getDoseTypeText() {
		String getDoseType = "";
		if ("N".equals(this.getValueString("DOSE_TYPEO"))
				|| "N".equals(this.getValueString("DOSE_TYPEE"))
				|| "N".equals(this.getValueString("DOSE_TYPEI"))
				|| "N".equals(this.getValueString("DOSE_TYPEF"))) {
			List list = new ArrayList();
			if ("Y".equals(this.getValueString("DOSE_TYPEO"))) {
				list.add("�ڷ�");
			}
			if ("Y".equals(this.getValueString("DOSE_TYPEE"))) {
				list.add("����");
			}
			if ("Y".equals(this.getValueString("DOSE_TYPEI"))) {
				list.add("���");
			}
			if ("Y".equals(this.getValueString("DOSE_TYPEF"))) {
				list.add("���");
			}

			if (list == null || list.size() == 0) {
				return "";
			} else {
				for (int i = 0; i < list.size(); i++) {
					getDoseType = getDoseType + list.get(i) + " ,";
				}
				getDoseType = "�÷�: "
						+ getDoseType.substring(0, getDoseType.length() - 1);
			}
		} else {
			getDoseType = "�÷�: ȫ��";
		}
		return getDoseType;

	}

	/**
	 * �õ����ݿ����Tool
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * ȡ�ñ��ؼ�
	 * 
	 * @param tableName
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tableName) {
		return (TTable) getComponent(tableName);
	}

	// ***************************
	// luhai 2012-3-7 add ƿǩ��ӡ
	// ***************************
	/**
	 * 
	 * ƿǩ��ӡ luhai 2012-2-24
	 */
	//20170228 liuyalin add 
	boolean printFlg = false;
	public void onPrintPasterBottle() {
		Vector vct = new Vector();
		TParm parm = this.tblDtl.getParmValue();
		for (int i = 0; i < 24; i++) {
			vct.add(new Vector());
		}
		String cat1Type = "";
		String orderCode = "";
		String orderDesc = "";
		String Dosetype = "";
		// System.out.println("=====���ݣ�"+parm);
		for (int i = 0; i < parm.getCount("MR_NO"); i++) {
			cat1Type = parm.getData("CAT1_TYPE", i) + "";
			orderCode = (String) parm.getData("ORDER_CODE", i);
			orderDesc = (String) parm.getData("ORDER_DESC", i);
			if (TypeTool.getBoolean(tblDtl.getValueAt(i, 0))) {
				if (cat1Type.equals("PHA")) {
					Dosetype = SysPhaBarTool.getInstance().getDoseType(
							orderCode);
					/*if (!Dosetype.equals("I") && !Dosetype.equals("F")) {
						this.messageBox(orderDesc + "����������Σ����ܴ�ӡ��");
						return;
					}*/
				}
			}
			if (!("true".equals(this.tblDtl.getValueAt(i, 0) + "") || ("Y"
					.equals(this.tblDtl.getValueAt(i, 0) + "")))) {
				continue;
			}
			((Vector) vct.get(0)).add(parm.getData("BED_NO", i));
			((Vector) vct.get(1)).add(parm.getData("MR_NO", i));
			((Vector) vct.get(2)).add(parm.getData("PAT_NAME", i));
			((Vector) vct.get(3)).add(parm.getData("LINKMAIN_FLG", i));
			((Vector) vct.get(4)).add(parm.getData("LINK_NO", i));
			((Vector) vct.get(5)).add(parm.getData("ORDER_DESC", i));
			((Vector) vct.get(6)).add(parm.getData("MEDI_QTY", i));
			((Vector) vct.get(7)).add(parm.getData("MEDI_UNIT", i));
			((Vector) vct.get(8)).add(parm.getData("ORDER_CODE", i));
			((Vector) vct.get(9)).add(parm.getData("ORDER_NO", i));
			((Vector) vct.get(10)).add(parm.getData("ORDER_SEQ", i));
			((Vector) vct.get(11)).add(parm.getData("START_DTTM", i));
			((Vector) vct.get(12)).add(parm.getData("FREQ_CODE", i));
			((Vector) vct.get(13)).add(getStationDesc(parm.getValue(
					"STATION_CODE", i)));
			TParm patParm = new TParm(TJDODBTool.getInstance().select(
					" SELECT *" + " FROM SYS_PATINFO A" + " WHERE MR_NO ='"
							+ parm.getData("MR_NO", i) + "'"));
			TParm sexDescParm = new TParm(TJDODBTool.getInstance().select(
					" SELECT CHN_DESC" + " FROM SYS_DICTIONARY A "
							+ " WHERE GROUP_ID = 'SYS_SEX'" + " AND  ID = '"
							+ patParm.getData("SEX_CODE", 0) + "'"));
			((Vector) vct.get(14)).add(sexDescParm.getData("CHN_DESC", 0));
			((Vector) vct.get(15)).add(StringUtil.getInstance().showAge(
					(Timestamp) patParm.getData("BIRTH_DATE", 0),
					SystemTool.getInstance().getDate()));
			String udSt = "";
			if (parm.getData("DSPN_KIND", i).equals("UD")
					|| parm.getData("DSPN_KIND", i).equals("F"))
				udSt = "����ҽ��";
			else if (parm.getData("DSPN_KIND", i).equals("ST"))
				udSt = "��ʱҽ��";
			((Vector) vct.get(16)).add(udSt);
			((Vector) vct.get(17)).add(parm.getValue("ROUTE_CODE", i));
			((Vector) vct.get(18)).add(getOperatorName(parm.getValue(
					"ORDER_DR_CODE", i)));
			((Vector) vct.get(19)).add(parm.getData("DISPENSE_QTY", i));
			((Vector) vct.get(20)).add(parm.getData("DISPENSE_UNIT", i));
			// �������
			((Vector) vct.get(21)).add(parm.getData("CLASSIFY_TYPE", i));// ------------------------------
			// ((Vector) vct.get(21)).add("I");//------------------------------
			// ����CASE_NO
			((Vector) vct.get(22)).add(parm.getData("CASE_NO", i));
			// ���� END_DTTM
			((Vector) vct.get(23)).add(parm.getData("END_DTTM", i));
		}
		vct.add(getUnitMap());
		// openWindow("%ROOT%\\config\\inw\\INWPrintBottonUI.x", vct);
		// ��ӡƿǩ��ֱ�Ӵ�ӡ��
		printBottle(vct);
	}

	/**
	 * 
	 * ��ӡƿǩ���� luhai 2012-2-28
	 * 
	 * @param buttonVct
	 */
	public void printBottle(Vector buttonVct) {
		parm = initPageData((Vector) buttonVct);
		Object objPha = (buttonVct).get((buttonVct).size() - 1);
		if (objPha != null) {
			phaMap = (Map) (buttonVct).get((buttonVct).size() - 1);
		}
		// ��ӡƿǩ
		// ѡ����
		int row = 0;
		// ѡ����
		int column = 0;

		int count = parm.getCount("BED_NO");
		if (count <= 0) {
			this.messageBox_("û��Ҫ��ӡ��ҽ����");
			return;
		}
		TParm actionParm = creatPrintData();
		int rowCount = actionParm.getCount("PRINT_DATAPQ");
		if (rowCount <= 0) {
			this.messageBox_("��ӡ���ݴ���");
			return;
		}
		// ***************************************************
		// ���´����ӡƿǩ���������������ϵ�һ���н��д�ӡ luhai 2012-2-29 begin
		// ***************************************************
		TParm printDataPQParm = new TParm();
		int pRow = row;
		int pColumn = column;
		int cnt = 0;
		int rowNull = 0;
		for (int i = 0; i < 30; i++) {
			if (i % 3 == 0 && i != 0) {
				// cnt = 0 ;
				rowNull++;
			}
			if (i < pRow * 3 + pColumn) {
				printDataPQParm.addData("ORDER_DATE_" + (cnt + 1), "");
				printDataPQParm.addData("BED_" + (cnt + 1), "");
				printDataPQParm.addData("PAT_NAME_" + (cnt + 1), "");
				printDataPQParm.addData("BAR_CODE_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_1_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_1_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_1_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_2_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_2_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_2_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_3_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_3_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_3_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_4_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_4_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_4_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_5_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_5_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_5_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_6_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_6_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_6_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_7_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_7_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_7_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_8_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_8_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_8_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_9_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_9_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_9_" + (cnt + 1), "");
				printDataPQParm.addData("ORDER_10_" + (cnt + 1), "");
				printDataPQParm.addData("QTY_10_" + (cnt + 1), "");
				printDataPQParm.addData("TOT_QTY_10_" + (cnt + 1), "");
				printDataPQParm.addData("STATION_DESC_" + (cnt + 1), "");
				printDataPQParm.addData("MR_NO_" + (cnt + 1), "");
				printDataPQParm.addData("SEX_" + (cnt + 1), "");
				printDataPQParm.addData("AGE_" + (cnt + 1), "");
				printDataPQParm.addData("RX_TYPE_" + (cnt + 1), "");
				printDataPQParm.addData("FREQ_CODE_" + (cnt + 1), "");
				printDataPQParm.addData("DOCTOR_" + (cnt + 1), "");
				printDataPQParm.addData("ROUT_" + (cnt + 1), "");
				printDataPQParm.addData("PAGE_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_NAME_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_QTY_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_TOT_" + (cnt + 1), "");
				// add by liyh 20121217������ҩʦ
				printDataPQParm.addData("TITLE_CHECK_DR_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_CHECK_DATE_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_DR_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_CHECK_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_EXE_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_PAGEF_" + (cnt + 1), "");
				printDataPQParm.addData("TITLE_PAGEB_" + (cnt + 1), "");
				// cnt++;
				continue;
			} else {
				break;
			}
		}
		// ���ñ�������ִ��ʱ��ʵ��������Ŀ
		int realtotCount = 0;
		for (int i = 0; i < rowCount; i++) {
			TParm temp = (TParm) actionParm.getData("PRINT_DATAPQ", i);
			printDataPQParm.addData("ORDER_DATE_" + (pColumn + 1), temp
					.getData("DATETIME"));
			printDataPQParm.addData("BED_" + (pColumn + 1), temp
					.getData("BED_NO"));
			printDataPQParm.addData("PAT_NAME_" + (pColumn + 1), temp
					.getData("PAT_NAME"));
			// this.messageBox( temp.getData("BAR_CODE")+"");
			printDataPQParm.addData("BAR_CODE_" + (pColumn + 1), temp
					.getData("BAR_CODE"));
			printDataPQParm.addData("STATION_DESC_" + (pColumn + 1), temp
					.getData("STATION_DESC"));
			printDataPQParm.addData("MR_NO_" + (pColumn + 1), temp
					.getData("MR_NO"));
			printDataPQParm
					.addData("SEX_" + (pColumn + 1), temp.getData("SEX"));
			printDataPQParm
					.addData("AGE_" + (pColumn + 1), temp.getData("AGE"));
			printDataPQParm.addData("RX_TYPE_" + (pColumn + 1), temp
					.getData("RX_TYPE"));
			printDataPQParm.addData("FREQ_CODE_" + (pColumn + 1), temp
					.getData("FREQ"));
			printDataPQParm.addData("DOCTOR_" + (pColumn + 1), temp
					.getData("DOCTOR"));
			printDataPQParm.addData("ROUT_" + (pColumn + 1), temp
					.getData("ROUTE"));
			printDataPQParm.addData("PAGE_" + (pColumn + 1), temp
					.getData("PAGE"));

			printDataPQParm.addData("TITLE_NAME_" + (pColumn + 1), "ҩ��");
			printDataPQParm.addData("TITLE_QTY_" + (pColumn + 1), "����");
			printDataPQParm.addData("TITLE_TOT_" + (pColumn + 1), "����");
			// modify by lim 2012/04/29 begin
			printDataPQParm.addData("TITLE_CHECK_DR_" + (pColumn + 1), "���ҩʦ:"
					+ Operator.getName());
			printDataPQParm.addData("TITLE_CHECK_DATE_" + (pColumn + 1),
					"����ʱ��:");
			printDataPQParm.addData("TITLE_DR_" + (pColumn + 1), "����ҩʦ:");
			printDataPQParm.addData("TITLE_CHECK_" + (pColumn + 1), "�˶�ҩʦ:");
			// printDataPQParm.addData("TITLE_DR_" + (pColumn + 1), "ҽ��:");
			// printDataPQParm.addData("TITLE_CHECK_" + (pColumn + 1), "���:");
			// printDataPQParm.addData("TITLE_EXE_" + (pColumn + 1), "ִ��:");
			// modify by lim 2012/04/29 end
			printDataPQParm.addData("TITLE_PAGEF_" + (pColumn + 1), "��");
			printDataPQParm.addData("TITLE_PAGEB_" + (pColumn + 1), "ҳ");
			int rowOrderCount = temp.getCount("ORDER_DESC");
			for (int j = 0; j < 6; j++) {
				if (j > rowOrderCount - 1) {
					printDataPQParm.addData("ORDER_" + (j + 1) + "_"
							+ (pColumn + 1), "");
					printDataPQParm.addData("QTY_" + (j + 1) + "_"
							+ (pColumn + 1), "");
					printDataPQParm.addData("TOT_QTY_" + (j + 1) + "_"
							+ (pColumn + 1), "");
					continue;
				}
				// modify by lim 2012/04/29 begin
				String order = "";
				if (null != temp.getValue("ORDER_DESC", j)
						&& !"".equals(temp.getValue("ORDER_DESC", j))) {
					String[] orderDescArray = temp.getValue("ORDER_DESC", j)
							.trim().split(" ");

					/* end update by guoyi 20120504 for ƿǩ���ֵ��� */
					if (orderDescArray.length == 1) {
						order = orderDescArray[0];
					} else if (orderDescArray.length == 2) {
						order = orderDescArray[0] + orderDescArray[1] + " "
								+ numDot(temp.getDouble("QTY", j))
								+ temp.getData("UNIT_CODE", j);
					} else if (orderDescArray.length == 3) {
						order = orderDescArray[0] + orderDescArray[2] + ""
								+ numDot(temp.getDouble("QTY", j))
								+ temp.getData("UNIT_CODE", j);
					}
					/* end update by guoyi 20120511 for ƿǩȥ�������������͹���������ո������ڽ�ȡ */
				}

				printDataPQParm.addData("ORDER_" + (j + 1) + "_"
						+ (pColumn + 1), order);
				// printDataPQParm.addData("QTY_" + (j + 1) + "_" + (pColumn +
				// 1),numDot(temp.getDouble("QTY", j)) + ""+
				// temp.getData("UNIT_CODE", j));
				// printDataPQParm.addData("TOT_QTY_" + (j + 1) + "_" + (pColumn
				// + 1),numDot(temp.getDouble("DOSAGE_QTY", j)) + ""+
				// temp.getData("DOSAGE_UNIT", j));
				printDataPQParm.addData("QTY_" + (j + 1) + "_" + (pColumn + 1),
						"");
				printDataPQParm.addData("TOT_QTY_" + (j + 1) + "_"
						+ (pColumn + 1), "");
				// modify by lim 2012/04/29 end

			}
			// pColumn++;
			if (pColumn == 3) {
				// pColumn = 0;
				pRow++;
			}
		}
		// printDataPQParm.setCount(pRow+rowNull+1);
		printDataPQParm.setCount(rowCount);
		// --------modify
		printDataPQParm.addData("SYSTEM", "COLUMNS", "A1");
		printDataPQParm.addData("SYSTEM", "COLUMNS", "A2");
		printDataPQParm.addData("SYSTEM", "COLUMNS", "A3");
		TParm parmForPrint = new TParm();
		parmForPrint.setData("PRINT_PQ", printDataPQParm.getData());
		// System.out.println("======="+parmForPrint);
		// luhai 2012-2-13 modify ��ƿǩ���ݲ𿪣�ÿҳ�����д�ӡ
		// ����ͼ�㷽ʽ�������⣬��ȡ����ӡԤ�������÷�������ÿҳ���ݷ�ʽʵ�֣�
		TParm pqParm = new TParm((Map) parmForPrint.getData("PRINT_PQ"));
		for (int i = 0; i < pqParm.getCount(); i++) {
			printBottleForEach(pqParm, i);
		}
		// luhai 2012-2-13 modify modify ��ƿǩ���ݲ𿪣�ÿҳ�����д�ӡ
		// ***************************************************
		// ���´����ӡƿǩ���������������ϵ�һ���н��д�ӡ luhai 2012-2-29 end
		// ***************************************************
	}

	/**
	 * 
	 * ����ƿǩTparm ��ӡ����
	 * 
	 * @param pqTParm
	 *            ƿǩTParm
	 * @param index
	 *            ��ӡ���� luhai 2012-3-13
	 */
	private void printBottleForEach(TParm pqTParm, int index) {
		TParm printTParm = new TParm();
		TParm newPqTParm = new TParm();
		String[] names = pqTParm.getNames();
		for (String key : names) {
			newPqTParm.addData(key, pqTParm.getValue(key, index));
		}
		newPqTParm.setCount(1);
		printTParm.setData("PRINT_PQ", newPqTParm.getData());
		String sql = "SELECT IVA_FLG FROM ODI_DSPND WHERE BAR_CODE = '"
				+ new TParm((Map)printTParm.getData("PRINT_PQ")).getValue("BAR_CODE_1",0)+"' ";
//		System.out.println(sql);
		//����������PIVAs���Ʒֿ���ӡ���� 20150505 wngjingchun add
		TParm iva_flg = new TParm(TJDODBTool.getInstance().select(sql));
		if(iva_flg.getValue("IVA_FLG", 0).equals("") || iva_flg.getValue("IVA_FLG", 0) == null){
			if(this.getValue("LINK_NO").equals("Y")){
				return;
			}
		}else if(!iva_flg.getValue("IVA_FLG", 0).equals(this.getValue("LINK_NO"))){
			return;
		}
		//����������PIVAs���Ʒֿ���ӡ���� 20150505 wngjingchun end
		//д���ӡ������Ա��ʱ�� wangjingchun 20150319 add
		TParm resultD = UddDispatchTool.getInstance().updatePrintBottleUser(
				new TParm((Map) printTParm.getData("PRINT_PQ")).getValue("BAR_CODE_1",0));
    	if (resultD.getErrCode()<0) {
			return;
		}
    	//20170228 liuyalin modify
    	if (printFlg == false){
    		openPrintWindow("%ROOT%\\config\\prt\\inw\\INWPrintBottle1.jhw",
    				printTParm, false); 
    	}else{
    		openPrintWindow("%ROOT%\\config\\prt\\inw\\INWPrintBottle1.jhw",
    				printTParm, true);  		
    	}
//    	System.out.println("printTParm=="+printTParm);
		// openPrintWindow("%ROOT%\\config\\prt\\inw\\INWPrintBottle.jhw",parmForPrint);
	}
	
    //20170228 liuyalin add
	public void onPrintAll(){
		printFlg = true;
		onPrintPasterBottle();
		//messageBox("�Ѵ�ӡ��Ԥ��ƿǩ");
		printFlg = false;
	}
	
	// ȫ������
	TParm parm = new TParm();
	// ȫ�ֵ�λ
	Map phaMap;

	/**
	 * �����ӡ����
	 * 
	 * @return TParm
	 */
	public TParm creatPrintData() {
		TParm result = new TParm();
		Set linkSet = new HashSet();
		Map linkMap = new HashMap();
		int rowCount = parm.getCount("PAT_NAME");
		// ��ӡ���ٸ�ƿǩ
		for (int i = 0; i < rowCount; i++) {
			TParm temp = parm.getRow(i);
			if (!"".equals(temp.getValue("LINK_NO"))) {
				String tempStr = temp.getValue("MR_NO")
						+ temp.getValue("LINK_NO")
						+ temp.getValue("START_DTTM") + temp.getValue("BAR_CODE")
						+ temp.getValue("ORDER_NO");
				linkSet.add(tempStr);
			}
		}
		Iterator linkIterator = linkSet.iterator();
		// ÿ��ƿǩ�Ļ�����Ϣ
		while (linkIterator.hasNext()) {
			String tempLinkStr = "" + linkIterator.next();
			for (int j = 0; j < rowCount; j++) {
				TParm tempParm = parm.getRow(j);
				// if(tempLinkStr.equals(tempParm.getValue("MR_NO")+tempParm.getValue("LINK_NO")+tempParm.getValue("START_DTTM")+tempParm.getValue("SEQ_NO")+
				// tempParm.getValue("ORDER_NO"))){
				if (tempLinkStr.equals(tempParm.getValue("MR_NO")
						+ tempParm.getValue("LINK_NO")
						+ tempParm.getValue("START_DTTM")
						+ tempParm.getValue("BAR_CODE")
						+ tempParm.getValue("ORDER_NO"))
						&& !"".equals(tempParm.getValue("LINK_NO"))) {
					TParm temp = new TParm();
					// String dateTime = tempParm.getValue("START_DTTM")
					// .substring(4, 6)
					// + "/"
					// + tempParm.getValue("START_DTTM").substring(6, 8);
					String dateTime = "";
					try {
						dateTime = tempParm.getValue("ORDER_DATE").substring(4,
								6)
								+ "/"
								+ tempParm.getValue("ORDER_DATE").substring(6,
										8)
								+ " "
								+ tempParm.getValue("ORDER_DATETIME")
										.substring(0, 2)
								+ ":"
								+ tempParm.getValue("ORDER_DATETIME")
										.substring(2, 4);
					} catch (Exception e) {
						e.printStackTrace();
					}
					temp.setData("DATETIME", dateTime);
					temp.setData("BED_NO", tempParm.getValue("BED_NO"));
					temp.setData("PAT_NAME", tempParm.getValue("PAT_NAME"));
					temp.setData("MR_NO", tempParm.getValue("MR_NO"));
					temp.setData("FREQ", tempParm.getValue("FREQ"));
					temp.setData("STATION_DESC", tempParm
							.getValue("STATION_DESC"));
					temp.setData("SEX", tempParm.getValue("SEX"));
					temp.setData("AGE", tempParm.getValue("AGE"));
					temp.setData("RX_TYPE", tempParm.getValue("RX_TYPE"));
					temp.setData("ROUTE", tempParm.getValue("ROUTE"));
					temp.setData("DOCTOR", tempParm.getValue("DOCTOR"));
					temp.setData("BAR_CODE", tempParm.getValue("BAR_CODE"));
					linkMap.put(tempLinkStr, temp);
					break;
				}
			}
			TParm temp = (TParm) linkMap.get(tempLinkStr);
			for (int j = 0; j < rowCount; j++) {
				TParm tempParm = parm.getRow(j);
				if (tempLinkStr.equals(tempParm.getValue("MR_NO")
						+ tempParm.getValue("LINK_NO")
						+ tempParm.getValue("START_DTTM")
						+ tempParm.getValue("BAR_CODE")
						+ tempParm.getValue("ORDER_NO"))) {
					String orderDesc = tempParm.getValue("ORDER_DESC");
					/*
					 * begin update by guoyi 20120511 for
					 * ƿǩȥ�������������͹���������ո������ڽ�ȡ
					 */
					String desc[] = breakNFixRow(orderDesc, 35, 1);
					/* end update by guoyi 20120511 for ƿǩȥ�������������͹���������ո������ڽ�ȡ */
					for (int k = 0; k < desc.length; k++) {
						if (k == 0) {
							temp.addData("ORDER_DESC", desc[k]);
							// ����
							temp.addData("QTY", tempParm.getValue("QTY"));
							// ��λ
							temp.addData("UNIT_CODE", phaMap.get(tempParm
									.getValue("UNIT_CODE")));
							// ����
							temp.addData("DOSAGE_QTY", tempParm
									.getValue("DOSAGE_QTY"));
							// ������λ
							temp.addData("DOSAGE_UNIT", phaMap.get(tempParm
									.getValue("DOSAGE_UNIT")));
							// ������
							temp.addData("LINK_MAIN_FLG", tempParm
									.getValue("LINK_MAIN_FLG"));
							continue;
						}
						temp.addData("ORDER_DESC", desc[k]);
						// ����
						temp.addData("QTY", "");
						// ��λ
						temp.addData("UNIT_CODE", "");
						// ����
						temp.addData("DOSAGE_QTY", "");
						// ������λ
						temp.addData("DOSAGE_UNIT", "");
						// ������
						temp.addData("LINK_MAIN_FLG", "");
					}
				}
			}
			linkMap.put(tempLinkStr, temp);
			result.addData("PRINT_DATAPQ", linkMap.get(tempLinkStr));
		}
		Set onlySet = new HashSet();
		for (int i = 0; i < result.getCount("PRINT_DATAPQ"); i++) {
			onlySet.add(((TParm) result.getRow(i).getData("PRINT_DATAPQ"))
					.getValue("BED_NO"));
		}
		TParm resultTemp = new TParm();
		Iterator iter = onlySet.iterator();
		while (iter.hasNext()) {
			String temp = iter.next().toString();
			for (int j = 0; j < result.getCount("PRINT_DATAPQ"); j++) {
				if (temp.equals(((TParm) result.getRow(j).getData(
						"PRINT_DATAPQ")).getValue("BED_NO"))) {
					resultTemp.addData("PRINT_DATAPQ", result.getRow(j)
							.getData("PRINT_DATAPQ"));
				}
			}
		}
		// **************************************************************************************************
		// ���������ҽ�������Ҳ��ʾ����begin
		// **************************************************************************************************
		result = new TParm();
		linkSet = new LinkedHashSet();
		linkMap = new HashMap();
		rowCount = parm.getCount("PAT_NAME");
		// ��ӡ���ٸ�ƿǩ
		for (int i = 0; i < rowCount; i++) {
			TParm temp = parm.getRow(i);
//			boolean classifyFlg = "F".equals(temp.getValue("CLASSIFY_TYPE"))
//					|| "I".equals(temp.getValue("CLASSIFY_TYPE"));
			if ("".equals(temp.getValue("LINK_NO"))) {
				String tempStr = temp.getValue("MR_NO")
						+ temp.getValue("LINK_NO")
						+ temp.getValue("START_DTTM")
						+ temp.getValue("BAR_CODE")
						+ temp.getValue("ORDER_NO");
				linkSet.add(tempStr);
			}
		}
		linkIterator = linkSet.iterator();
		// ÿ��ƿǩ�Ļ�����Ϣ
		while (linkIterator.hasNext()) {
			String tempLinkStr = "" + linkIterator.next();
			for (int j = 0; j < rowCount; j++) {
				TParm tempParm = parm.getRow(j);
				if (tempLinkStr.equals(tempParm.getValue("MR_NO")
						+ tempParm.getValue("LINK_NO")
						+ tempParm.getValue("START_DTTM")
						+ tempParm.getValue("BAR_CODE")
						+ tempParm.getValue("ORDER_NO"))
						&& "".equals(tempParm.getValue("LINK_NO"))) {
					TParm temp = new TParm();
					// String dateTime = tempParm.getValue("START_DTTM")
					// .substring(4, 6)
					// + "/"
					// + tempParm.getValue("START_DTTM").substring(6, 8);
					String dateTime = "";
					try {
						dateTime = tempParm.getValue("ORDER_DATE").substring(4,
								6)
								+ "/"
								+ tempParm.getValue("ORDER_DATE").substring(6,
										8)
								+ " "
								+ tempParm.getValue("ORDER_DATETIME")
										.substring(0, 2)
								+ ":"
								+ tempParm.getValue("ORDER_DATETIME")
										.substring(2, 4);
					} catch (Exception e) {
						e.printStackTrace();
					}
					temp.setData("DATETIME", dateTime);
					temp.setData("BED_NO", tempParm.getValue("BED_NO"));
					temp.setData("PAT_NAME", tempParm.getValue("PAT_NAME"));
					temp.setData("MR_NO", tempParm.getValue("MR_NO"));
					temp.setData("FREQ", tempParm.getValue("FREQ"));
					temp.setData("STATION_DESC", tempParm
							.getValue("STATION_DESC"));
					temp.setData("SEX", tempParm.getValue("SEX"));
					temp.setData("AGE", tempParm.getValue("AGE"));
					temp.setData("RX_TYPE", tempParm.getValue("RX_TYPE"));
					temp.setData("ROUTE", tempParm.getValue("ROUTE"));
					temp.setData("DOCTOR", tempParm.getValue("DOCTOR"));
					temp.setData("BAR_CODE", tempParm.getValue("BAR_CODE"));
					linkMap.put(tempLinkStr, temp);
					break;
				}
			}
			TParm temp = (TParm) linkMap.get(tempLinkStr);
			for (int j = 0; j < rowCount; j++) {
				TParm tempParm = parm.getRow(j);
				if (tempLinkStr.equals(tempParm.getValue("MR_NO")
						+ tempParm.getValue("LINK_NO")
						+ tempParm.getValue("START_DTTM")
						+ tempParm.getValue("BAR_CODE")
						+ tempParm.getValue("ORDER_NO"))) {
					String orderDesc = tempParm.getValue("ORDER_DESC");
					/*
					 * begin update by guoyi 20120511 for
					 * ƿǩȥ�������������͹���������ո������ڽ�ȡ
					 */
					String desc[] = breakNFixRow(orderDesc, 35, 1);
					/* end update by guoyi 20120511 for ƿǩȥ�������������͹���������ո������ڽ�ȡ */
					for (int k = 0; k < desc.length; k++) {
						if (k == 0) {
							temp.addData("ORDER_DESC", desc[k]);
							// ����
							temp.addData("QTY", tempParm.getValue("QTY"));
							// ��λ
							temp.addData("UNIT_CODE", phaMap.get(tempParm
									.getValue("UNIT_CODE")));
							// ����
							temp.addData("DOSAGE_QTY", tempParm
									.getValue("DOSAGE_QTY"));
							// ������λ
							temp.addData("DOSAGE_UNIT", phaMap.get(tempParm
									.getValue("DOSAGE_UNIT")));
							// ������
							temp.addData("LINK_MAIN_FLG", tempParm
									.getValue("LINK_MAIN_FLG"));
							continue;
						}
						temp.addData("ORDER_DESC", desc[k]);
						// ����
						temp.addData("QTY", "");
						// ��λ
						temp.addData("UNIT_CODE", "");
						// ����
						temp.addData("DOSAGE_QTY", "");
						// ������λ
						temp.addData("DOSAGE_UNIT", "");
						// ������
						temp.addData("LINK_MAIN_FLG", "");
					}
				}
			}
			linkMap.put(tempLinkStr, temp);
			result.addData("PRINT_DATAPQ", linkMap.get(tempLinkStr));
		}
		onlySet = new LinkedHashSet();
		for (int i = 0; i < result.getCount("PRINT_DATAPQ"); i++) {
			onlySet.add(((TParm) result.getRow(i).getData("PRINT_DATAPQ"))
					.getValue("BED_NO"));
		}
		// TParm resultTemp = new TParm();
		iter = onlySet.iterator();
		while (iter.hasNext()) {
			String temp = iter.next().toString();
			for (int j = 0; j < result.getCount("PRINT_DATAPQ"); j++) {
				if (temp.equals(((TParm) result.getRow(j).getData(
						"PRINT_DATAPQ")).getValue("BED_NO"))) {
					resultTemp.addData("PRINT_DATAPQ", result.getRow(j)
							.getData("PRINT_DATAPQ"));
				}
			}
		}
		// **************************************************************************************************
		// ���������ҽ�������Ҳ��ʾ����end
		// **************************************************************************************************

		return configParm(resultTemp);
	}

	/**
	 * ��ʼ��ҳ���ӡ���� luhai 2012-2-28
	 * 
	 * @param parm
	 *            Vector
	 * @return TParm
	 */
	public TParm initPageData(Vector parm) {
		TParm result = new TParm();
		int rowCount = ((Vector) parm.get(0)).size();
		for (int i = 0; i < rowCount; i++) {
			// luhai 2012-2-29 modify ����Ƶ��Ϊ�յ���� begin
			String freqCode = "";
			if (((Vector) (parm.get(12))).get(i) == null) {
				freqCode = "STAT";
			} else {
				freqCode = ((Vector) parm.get(12)).get(i).toString();
			}
			// luhai 2012-2-29 modify ����Ƶ��Ϊ�յ���� end
			TParm freqParm = new TParm(this.getDBTool().select(
					"SELECT FREQ_TIMES FROM SYS_PHAFREQ WHERE FREQ_CODE='"
							+ freqCode + "'"));
			int countFreq = freqParm.getInt("FREQ_TIMES", 0);
			// luhai 2012-3-1 ����ִ�д����ļ��㣬֮ǰ�߼�����Ƶ�μ���������������ִ��������������Ҫÿ��ִ�ж���ӡƿǩ begin
			String caseNo = ((Vector) parm.get(22)).get(i) + "";
			String orderNo = ((Vector) parm.get(9)).get(i) + "";
			String orderSeq = ((Vector) parm.get(10)).get(i) + "";
			String startDttm = ((Vector) parm.get(11)).get(i) + "";
			String endDttm = ((Vector) parm.get(23)).get(i) + "";
			// ��ѯϸ���SQL
			String sql = " SELECT "
					+ " BAR_CODE,ORDER_DATE,ORDER_DATETIME "
					+ " FROM ODI_DSPND  WHERE CASE_NO='"
					+ caseNo
					+ "' AND ORDER_NO='"
					+ orderNo
					+ "' AND ORDER_SEQ='"
					+ orderSeq
					+ "' "
					+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
					+ startDttm
					+ "','YYYYMMDDHH24MISS') "
					+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
					+ endDttm + "','YYYYMMDDHH24MISS')";
			// + " GROUP BY BAR_CODE  ";
			// + " ORDER BY ORDER_DATE||ORDER_DATETIME ";
			// System.out.println("sql=========="+sql);
			// System.out.println(":::::::::::::::::::::::::" + sql);
			TParm resultDspnCnt = new TParm(TJDODBTool.getInstance()
					.select(sql));
			int totCount = resultDspnCnt.getCount();
			String barCode = "";
			// System.out.println("BAR_CODE-------"+barCode);
			// luhai 2012-3-1 ����ִ�д����ļ��㣬֮ǰ�߼�����Ƶ�μ���������������ִ��������������Ҫÿ��ִ�ж���ӡƿǩ end
			// ����1��
			int seqNo = 1;
			for (int j = 0; j < totCount; j++) {
				result.addData("BED_NO", getbedDesc((String) ((Vector) parm
						.get(0)).get(i)));
				result.addData("MR_NO", ((Vector) parm.get(1)).get(i));
				result.addData("PAT_NAME", ((Vector) parm.get(2)).get(i));
				result.addData("LINK_MAIN_FLG", ((Vector) parm.get(3)).get(i));
				result.addData("LINK_NO", ((Vector) parm.get(4)).get(i));
				result.addData("ORDER_DESC", ((Vector) parm.get(5)).get(i));
				result.addData("QTY", ((Vector) parm.get(6)).get(i));
				result.addData("UNIT_CODE", ((Vector) parm.get(7)).get(i));
				result.addData("ORDER_CODE", ((Vector) parm.get(8)).get(i));
				result.addData("ORDER_NO", ((Vector) parm.get(9)).get(i));
				result.addData("ORDER_SEQ", ((Vector) parm.get(10)).get(i));
				result.addData("START_DTTM", ((Vector) parm.get(11)).get(i));
				result.addData("SEQ_NO", seqNo);

				result.addData("FREQ", ((Vector) parm.get(12)).get(i));
				result.addData("STATION_DESC", ((Vector) parm.get(13)).get(i));
				result.addData("SEX", ((Vector) parm.get(14)).get(i));
				result.addData("AGE", ((Vector) parm.get(15)).get(i));
				result.addData("RX_TYPE", ((Vector) parm.get(16)).get(i));
				result.addData("ROUTE", ((Vector) parm.get(17)).get(i));
				result.addData("DOCTOR", ((Vector) parm.get(18)).get(i));
				result.addData("DOSAGE_QTY", ((Vector) parm.get(19)).get(i));
				result.addData("DOSAGE_UNIT", ((Vector) parm.get(20)).get(i));
				// �������
				result.addData("CLASSIFY_TYPE", ((Vector) parm.get(21)).get(i));
				// ����case_no
				result.addData("CASE_NO", ((Vector) parm.get(22)).get(i));
				// ����END_DTTM
				result.addData("END_DTTM", ((Vector) parm.get(23)).get(i));
				// shibl 20120415 add
				barCode = resultDspnCnt.getValue("BAR_CODE", j);
				// barCode
				result.addData("BAR_CODE", barCode);
				result.addData("ORDER_DATE", resultDspnCnt.getValue(
						"ORDER_DATE", j));
				result.addData("ORDER_DATETIME", resultDspnCnt.getValue(
						"ORDER_DATETIME", j));
				seqNo++;
			}
			// }
		}
		return result;
	}

	public String getStationDesc(String stationCode) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT STATION_DESC " + " FROM SYS_STATION "
						+ " WHERE STATION_CODE = '" + stationCode + "'"));
		return parm.getValue("STATION_DESC", 0);
	}

	/**
	 * ȡ�õ�λ�ֵ�
	 * 
	 * @return Map
	 */
	public Map getUnitMap() {
		Map map = new HashMap();
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT UNIT_CODE,UNIT_CHN_DESC FROM SYS_UNIT"));
		for (int i = 0; i < parm.getCount(); i++) {
			map.put(parm.getData("UNIT_CODE", i), parm.getData("UNIT_CHN_DESC",
					i));
		}
		return map;
	}

	/**
	 * 
	 * 
	 */
	public String getbedDesc(String bedNo) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT BED_NO,BED_NO_DESC FROM SYS_BED WHERE BED_NO = '"
						+ bedNo + "'"));
		return parm.getValue("BED_NO_DESC", 0);
	}

	public String getOperatorName(String userID) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT USER_NAME " + " FROM SYS_OPERATOR "
						+ " WHERE USER_ID = '" + userID + "'"));
		return parm.getValue("USER_NAME", 0);
	}

	private String numDot(double medQty) {
		if (medQty == 0)
			return "";
		if ((int) medQty == medQty)
			return "" + (int) medQty;
		else
			return "" + medQty;
	}

	public String[] breakNFixRow(String src, int bre, int fix) {
		return fixRow(breakRow(src, bre), fix);
	}

	public String[] fixRow(String string, int size) {
		Vector splitVector = new Vector();
		int index = 0;
		int separatorCount = 0;
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if ("\n".equals(String.valueOf(c))) {
				if (++separatorCount >= size) {
					splitVector.add(string.substring(index, i));
					index = i + 1;
					separatorCount = 0;
				}
			}
		}

		splitVector.add(string.substring(index, string.length()));
		String splitArray[] = new String[splitVector.size()];
		for (int j = 0; j < splitVector.size(); j++)
			splitArray[j] = (String) splitVector.get(j);

		return splitArray;
	}

	public String breakRow(String src, int size) {
		return breakRow(src, size, 0);
	}

	public String breakRow(String src, int size, int shift) {
		StringBuffer tmp = new StringBuffer("");
		tmp.append(space(shift));
		int i = 0;
		int len = 0;
		for (; i < src.length(); i++) {
			char c = src.charAt(i);
			len += getCharSize(c);
			/* begin update by guoyi 20120504 for ƿǩ���ֵ��� */
			/*
			 * if ("\n".equals(String.valueOf(c))) { tmp.append(c);
			 * tmp.append(space(shift)); len = 0; } else if (size >= len) {
			 * tmp.append(c); } else { tmp.append("\n");
			 * tmp.append(space(shift)); tmp.append(c); len = getCharSize(c); }
			 */
			/* begin update by guoyi 20120511 for ƿǩȥ�������������͹���������ո������ڽ�ȡ */
			/*
			 * if ("\n".equals(String.valueOf(c))) { tmp.append(c);
			 * tmp.append(space(shift)); len = 0; } if (size >= len) {
			 * tmp.append(c); if(size == len){ break; } }else if
			 * ("\n".equals(String.valueOf(c))) { tmp.append(c);
			 * tmp.append(space(shift)); len = 0; }
			 */
			if ("\n".equals(String.valueOf(c))) {
				tmp.append(c);
				tmp.append(space(shift));
				len = 0;
			}
			if (size > len) {
				tmp.append(c);
			} else if (size == len) {
				tmp.append(")");
				break;
			}
			/* end update by guoyi 20120511 for ƿǩȥ�������������͹���������ո������ڽ�ȡ */
			/* begin update by guoyi 20120504 for ƿǩ���ֵ��� */
		}

		return tmp.toString();
	}

	public int getCharSize(char c) {
		return (new String(new char[] { c })).getBytes().length;
	}

	public String space(int n) {
		StringBuffer tmp = new StringBuffer("");
		for (int i = 0; i < n; i++)
			tmp.append(' ');

		return tmp.toString();
	}

	private TParm configParm(TParm parm) {
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("PRINT_DATAPQ"); i++) {
			TParm parmI = (TParm) parm.getData("PRINT_DATAPQ", i);
			;
			int rowCount = parmI.getCount("ORDER_DESC");
			int pageCount = 1;
			/* begin update by guoyi 20120504 for ƿǩ���ֵ�����ҳ */
			int pageSize = 6;
			if (rowCount % pageSize == 0)
				pageCount = rowCount / pageSize;
			else
				pageCount = rowCount / pageSize + 1;
			int page = 1;
			for (int j = 0; j < rowCount; j++) {
				if ((j + 1) % pageSize == 0) {
					result.addData("PRINT_DATAPQ", cloneParm(parmI, j
							- pageSize + 1, j));
					((TParm) result.getData("PRINT_DATAPQ", result
							.getCount("PRINT_DATAPQ") - 1)).setData("PAGE",
							page + "/" + pageCount);
					page++;
				} else if ((j + 1) == rowCount) {
					result.addData("PRINT_DATAPQ", cloneParm(parmI, rowCount
							- rowCount % pageSize, j));
					((TParm) result.getData("PRINT_DATAPQ", result
							.getCount("PRINT_DATAPQ") - 1)).setData("PAGE",
							page + "/" + pageCount);
					page++;
				}
			}
			/* end update by guoyi 20120504 for ƿǩ���ֵ�����ҳ */
		}
		return result;
	}

	private TParm cloneParm(TParm parm, int startIndex, int endIndex) {
		TParm result = new TParm();
		String[] names = parm.getNames();
		for (int i = 0; i < names.length; i++) {
			if (parm.getData(names[i]) instanceof String)
				result.setData(names[i], parm.getData(names[i]));
			else if (parm.getData(names[i]) instanceof Vector) {
				for (int j = startIndex; j <= endIndex; j++)
					result.addData(names[i], parm.getData(names[i], j));
			}
		}
		return result;
	}

	// ***************************
	// luhai 2012-3-7 end ƿǩ��ӡ
	// ***************************
	/**
	 * ������Һ����
	 */
	public void GeneratPhaBarcode() {
		TParm dspndParm = new TParm();
		TParm tablValue = tblDtl.getParmValue();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		int rowCount = tblDtl.getRowCount();
		int count = 0;
		TParm linkparm = new TParm();
		Map mapBarCode = new HashMap();
		Map linkmap = new HashMap();
		String caseNo = "";
		String orderNo = "";
		String orderSeq = "";
		String cat1Type = "";
		String orderCode = "";
		String startDttm = "";
		String endDttm = "";
		String orderDesc = "";
		String Dosetype = "";
		String barCode = "";
		String linkNo = "";
		String dspnKind = "";
		String linkStr = "";
		String dcFlg = "";
		String routeCode = "";
		// ���������
		for (int i = 0; i < rowCount; i++) {
			caseNo = (String) tablValue.getData("CASE_NO", i);
			orderNo = (String) tablValue.getData("ORDER_NO", i);
			orderSeq = tablValue.getData("ORDER_SEQ", i) + "";
			cat1Type = tablValue.getData("CAT1_TYPE", i) + "";
			orderCode = (String) tablValue.getData("ORDER_CODE", i);
			startDttm = (String) tablValue.getData("START_DTTM", i);
			endDttm = (String) tablValue.getData("END_DTTM", i);
			orderDesc = (String) tablValue.getData("ORDER_DESC", i);
			Dosetype = "";
			linkNo = tablValue.getValue("LINK_NO", i);
			dspnKind = (String) tablValue.getData("DSPN_KIND", i);
			routeCode = (String) tablValue.getData("ROUTE_CODE", i);
			if (TypeTool.getBoolean(tblDtl.getValueAt(i, 0))) {
				if (cat1Type.equals("PHA")) {
					if (routeCode.equals("")) {
						this.messageBox(orderDesc + "�÷�Ϊ�գ������������룡");
						return;
					}
					Dosetype = SysPhaBarTool.getInstance().getClassifyType(
							routeCode);
					// ====zhangp 20121120 start
					TRadioButton dc = (TRadioButton) getComponent("DC");
//					if ((!Dosetype.equals("I") && !Dosetype.equals("F"))
//							|| dc.isSelected()) {
//						this.messageBox(orderDesc + "����������Σ������������룡");
//						return;
//					}
					// ====zhangp 20121120 end
					// �ж�����ҽ����һ��һ�룩
					if (!linkNo.equals("")) {
						linkStr = caseNo + orderNo + dspnKind + startDttm
								+ linkNo;
						if (linkmap.get(linkStr) == null) {
							// ȡ��
							barCode = SysPhaBarTool.getInstance().getBarCode();
							mapBarCode.put(linkStr, barCode);
						}
						linkmap.put(linkStr, linkStr);
						// ��ѯϸ���SQL
						String sql = "SELECT CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,ORDER_DATETIME,"
								+ "DC_DATE,EXEC_NOTE,EXEC_DEPT_CODE,NS_EXEC_CODE,NS_EXEC_DATE_REAL,NS_EXEC_CODE_REAL,BAR_CODE FROM ODI_DSPND "
								+ "WHERE CASE_NO='"
								+ caseNo
								+ "' AND ORDER_NO='"
								+ orderNo
								+ "' AND ORDER_SEQ='"
								+ orderSeq
								+ "' "
								+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
								+ startDttm
								+ "','YYYYMMDDHH24MISS') "
								+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
								+ endDttm
								+ "','YYYYMMDDHH24MISS')"
								+ " ORDER BY ORDER_DATE||ORDER_DATETIME";
						// ����ϸ���TDS,����������
						TParm result = new TParm(TJDODBTool.getInstance()
								.select(sql));
						if (result.getCount() <= 0)
							continue;
						for (int j = 0; j < result.getCount(); j++) {
							if (!result.getValue("BAR_CODE", j).equals(""))
								continue;
							dspndParm.addData("CASE_NO", result.getValue(
									"CASE_NO", j));
							dspndParm.addData("ORDER_NO", result.getValue(
									"ORDER_NO", j));
							dspndParm.addData("ORDER_SEQ", result.getValue(
									"ORDER_SEQ", j));
							dspndParm.addData("ORDER_DATE", result.getValue(
									"ORDER_DATE", j));
							dspndParm.addData("ORDER_DATETIME", result
									.getValue("ORDER_DATETIME", j));
							dspndParm.addData("BAR_CODE", (String) mapBarCode
									.get(linkStr)
									+ j);
							dspndParm.addData("OPT_USER", Operator.getID());
							dspndParm.addData("OPT_DATE", now);
							dspndParm.addData("OPT_TERM", Operator.getIP());
							count++;
						}
					} else {
						// ȡ��
						barCode = SysPhaBarTool.getInstance().getBarCode();
						// ��ѯϸ���SQL
						String sql = "SELECT CASE_NO,ORDER_NO,ORDER_SEQ,ORDER_DATE,ORDER_DATETIME,"
								+ "DC_DATE,EXEC_NOTE,EXEC_DEPT_CODE,NS_EXEC_CODE,NS_EXEC_DATE_REAL,NS_EXEC_CODE_REAL,BAR_CODE FROM ODI_DSPND "
								+ "WHERE CASE_NO='"
								+ caseNo
								+ "' AND ORDER_NO='"
								+ orderNo
								+ "' AND ORDER_SEQ='"
								+ orderSeq
								+ "' "
								+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') >= TO_DATE ('"
								+ startDttm
								+ "','YYYYMMDDHH24MISS') "
								+ " AND TO_DATE (ORDER_DATE||ORDER_DATETIME, 'YYYYMMDDHH24MISS') <= TO_DATE ('"
								+ endDttm
								+ "','YYYYMMDDHH24MISS')"
								+ " ORDER BY ORDER_DATE||ORDER_DATETIME";
						// ����ϸ���TDS,����������
						TParm result = new TParm(TJDODBTool.getInstance()
								.select(sql));
						if (result.getCount() <= 0)
							continue;
						for (int j = 0; j < result.getCount(); j++) {
							if (!result.getValue("BAR_CODE", j).equals(""))
								continue;
							dspndParm.addData("CASE_NO", result.getValue(
									"CASE_NO", j));
							dspndParm.addData("ORDER_NO", result.getValue(
									"ORDER_NO", j));
							dspndParm.addData("ORDER_SEQ", result.getValue(
									"ORDER_SEQ", j));
							dspndParm.addData("ORDER_DATE", result.getValue(
									"ORDER_DATE", j));
							dspndParm.addData("ORDER_DATETIME", result
									.getValue("ORDER_DATETIME", j));
							dspndParm.addData("BAR_CODE", barCode + j);
							dspndParm.addData("OPT_USER", Operator.getID());
							dspndParm.addData("OPT_DATE", now);
							dspndParm.addData("OPT_TERM", Operator.getIP());
							count++;
						}
					}
				}
			}
		}
		dspndParm.setCount(count);
		if (count > 0) {
			TParm result = InwOrderExecTool.getInstance().GeneratIFBarcode(
					dspndParm);
			if (result.getErrCode() < 0) {
				this.messageBox("��������ʧ�ܣ�");
				return;
			}

			this.messageBox("��������ɹ���");
		} else {
			this.messageBox("�������������ҩƷ");
		}
	}

	/**
	 * �龫��ѯ ===zhangp 20120807
	 */
	public void onQueryTable() {
		int count = tblPat.getParmValue().getCount("EXEC");
		for (int i = 0; i < count; i++) {
			if (tblPat.getParmValue().getValue("EXEC", i).equals("Y")) {
				onQueryDtl();
				onQueryMed();
				// onLackStore();
				break;
			}
		}

	}

	/**
	 * ��ӡ�龫��ҩ�� ===zhangp 20121118
	 */
	public void CtrlDispenseSheet() {
		String pha_ctrlcode = getValueString("PHA_CTRLCODE");
		if (!pha_ctrlcode.equals("") && this.getRadioButton("ST").isSelected()) {
			onDispenseSheet();
		}
	}
	/**
	 * 
	* @Title: onUpdateBatchNo
	* @Description: TODO(˫������ҩƷ��ϸ�������Ƥ��ҩƷ�����޸�����)
	* @author pangben
	* @throws
	 */
	public void onDoubleClickUpdateBatchNo(int row){
		TParm parm = this.tblDtl.getParmValue();
		if (this.tblDtl.getSelectedRow()<0) {
			this.messageBox("��ѡ����Ҫ�޸ĵ�����");
			return;
		}
		//����ҽ��δ��ɿ����޸�Ƥ������
		if(TypeTool.getBoolean(getValue("ST"))&&TypeTool.getBoolean(getValue("UNCHECK"))){   
			TParm result = new TParm();
			// ��ѯѡ��ҩ���Ƿ�ΪƤ��ҩƷ
			String sql = "SELECT A.SKINTEST_FLG, A.ANTIBIOTIC_CODE,MAX(B.OPT_DATE),"
					+ "B.BATCH_NO,B.SKINTEST_NOTE"
					+ " FROM PHA_BASE A,PHA_ANTI B  WHERE A.ORDER_CODE = B.ORDER_CODE "
					+ "AND A.ORDER_CODE = '"
					+ parm.getValue("ORDER_CODE",this.tblDtl.getSelectedRow())
					+ "' AND B.CASE_NO = '"
					+ parm.getValue("CASE_NO",this.tblDtl.getSelectedRow())
					+ "' "
					+ "GROUP BY B.BATCH_NO ,B.SKINTEST_NOTE,B.OPT_DATE,A.SKINTEST_FLG, A.ANTIBIOTIC_CODE "
					+ "ORDER BY B.OPT_DATE DESC";
			TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
			TParm parmValue=new TParm();
			if (result1.getCount() <= 0) {
				this.messageBox("��ҩƷ���ǿ���ҩƷ��");
				return;
			} else if (result1.getValue("SKINTEST_FLG", 0).equals("N")) {
				this.messageBox("��Ƥ��ҩƷ��");
				return;
			} else if (result1.getValue("BATCH_NO", 0).equals(null)  
					|| "".equals(result1.getValue("BATCH_NO", 0))) {
				parmValue.setData("BATCH_NO", "");// ����
				//parmValue.setData("SKINTEST_NOTE", "");// Ƥ�Խ��
			} else {
				parmValue.setData("BATCH_NO", result1.getValue("BATCH_NO", 0));// ����
				//parmValue.setData("SKINTEST_FLG", result1.getValue("SKINTEST_NOTE", 0));// Ƥ�Խ��
			}
			parmValue.setData("CASE_NO", parm.getValue("CASE_NO",this.tblDtl.getSelectedRow()));// �����
			parmValue.setData("ORDER_CODE", parm.getValue("ORDER_CODE",this.tblDtl.getSelectedRow()));// ҽ������
			parmValue.setData("ORDER_NO", parm.getValue("ORDER_NO",this.tblDtl.getSelectedRow()));
			parmValue.setData("ORDER_SEQ", parm.getValue("ORDER_SEQ",this.tblDtl.getSelectedRow()));
			parmValue.setData("OPT_USER", Operator.getID());//
			parmValue.setData("OPT_TERM", Operator.getIP());//
			parmValue.setData("ORG_CODE", parm.getValue("EXEC_DEPT_CODE", this.tblDtl.getSelectedRow()));//add by wukai on 20170412 ��ӿ���
			result = (TParm) this.openDialog("%ROOT%\\config\\UDD\\UDDSkiResult.x",
					parmValue, true);
			if (result!=null) {
				parm.setData("BATCH_NO",this.tblDtl.getSelectedRow(),result.getData("BATCH_NO",0));
				this.tblDtl.setParmValue(parm);
			}
			
		}else{
			this.messageBox("״̬������,�����Բ���");
		}
	}
	
	/**
     * Ѫ�Ǳ���
     */
    public void getXTReport(){
    	TTable table = (TTable) this.getComponent("TBL_PAT");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("���ѡ������!");
			return;
		}
    	SystemTool.getInstance().OpenTnbWeb(table.getParmValue().getValue("MR_NO",selRow));
    }
    /**
     * �ĵ籨��
     */
    public void getPdfReport(){
    	TTable table = (TTable) this.getComponent("Table_UP");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("���ѡ������!");
			return;
		}
		String sql = "SELECT  DISTINCT MED_APPLY_NO  FROM ODI_ORDER WHERE CASE_NO = '"+this.getCaseNos()+"' AND ORDER_CAT1_CODE = 'ECC'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getCount() <= 0){
    		this.messageBox("�ò���û���ĵ���ҽ��");
    		return;
    	}
    	// ���������pdf
		TParm parm = new TParm();
		String opbBookNo = "";
    	for(int i = 0; i < result.getCount(); i++){
    		opbBookNo += "'"+result.getValue("MED_APPLY_NO", i)+"'"+",";
    	}
    	parm.setData("OPE_BOOK_NO",opbBookNo.substring(0, opbBookNo.length()-1));
    	parm.setData("CASE_NO",this.getCaseNos());
    	parm.setData("TYPE","3");
    	this.openDialog("%ROOT%\\config\\odi\\ODIOpeMr.x", parm);
    }
    
    /**
	 * ���鱨��
	 */
	public void onLis() {
		TTable table = (TTable) this.getComponent("Table_UP");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("���ѡ������!");
			return;
		}
		SystemTool.getInstance().OpenLisWeb(table.getParmValue().getValue("MR_NO",selRow));
	}

	/**
	 * ��鱨��
	 */
	public void onRis() {
		TTable table = (TTable) this.getComponent("Table_UP");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("���ѡ������!");
			return;
		}
		SystemTool.getInstance().OpenRisWeb(table.getParmValue().getValue("MR_NO",selRow));
	}
	
	/**
	 * ��ӡ��ҩ����(���ӵ�����)
	 */
	public void printDispenseBarCode() {
		TParm parm = tblPat.getParmValue();
		if (parm == null || parm.getCount() <= 0) {
			this.messageBox("�޴�ӡ����");
			return;
		}
		
		int selectedRow = tblPat.getSelectedRow();
		if (selectedRow < 0) {
			this.messageBox("��ѡ��Ҫ��ӡ��ҩ����Ĳ����嵥������");
			return;
		}
		
		TParm selectedParm = parm.getRow(selectedRow);
		// ��ҩ����
		String dispenseNo = selectedParm.getValue("PHA_DISPENSE_NO");
		String caseNo = selectedParm.getValue("CASE_NO");
		String stationCode = selectedParm.getValue("STATION_CODE");

		if (StringUtils.isEmpty(dispenseNo)) {
			this.messageBox("��ѡ����������ҩ����Ϊ��");
			return;
		}
		
		String sql = "SELECT B.DEPT_CHN_DESC,C.STATION_DESC FROM ODI_DSPNM A,SYS_DEPT B,SYS_STATION C "
				+ "WHERE A.CASE_NO = '"
				+ caseNo
				+ "' AND A.STATION_CODE = '"
				+ stationCode
				+ "' AND A.PHA_DISPENSE_NO = '"
				+ dispenseNo
				+ "' AND A.DEPT_CODE = B.DEPT_CODE AND A.STATION_CODE = C.STATION_CODE";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ��ҩ�������ݴ���");
			err("ERR:" + result.getErrText());
			return;
		} else if (result.getCount() < 1) {
			this.messageBox("������ҩ��������");
			return;
		}
		
		TParm printParm = new TParm();
		// ����
		printParm.setData("DISPENSE_BAR_CODE", "TEXT", dispenseNo);
		printParm.setData("DEPT_CODE", "TEXT", "����:"
				+ result.getValue("DEPT_CHN_DESC", 0));
		printParm.setData("STATION_CODE", "TEXT", "����:"
				+ result.getValue("STATION_DESC", 0));
		this.openPrintWindow("%ROOT%\\config\\prt\\UDD\\UddDispenseBarCodePrint.jhw", printParm, true);
	}
	
	/**
	 * ǿ��Ѫ�Ǳ���webչ��
	 */
	public void getBgReport() {
		TTable table = (TTable) this.getComponent("TBL_PAT");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("���ѡ������!");
			return;
		}
		// ǿ��Ѫ��webչ��
		TParm result = SystemTool.getInstance().OpenJNJWeb(
				table.getParmValue().getValue("CASE_NO", selRow));
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
		}
	}
	// liuyalin 20170505 ���� add start
	/**
	 * ��ҩ���䲡����ϸ�����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// �����parmֵһ��,
				// 1.ȡparmֵ;
				TParm tableData = getTable("TBL_PAT").getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = getTable("TBL_PAT")
						.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);
			}
		});
	}

	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTable("TBL_PAT").setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {
			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}
		return index;
	}
	// liuyalin 20170505 add end
}
