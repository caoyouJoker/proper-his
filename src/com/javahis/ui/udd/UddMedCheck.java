package com.javahis.ui.udd;

import java.awt.Color;
import java.sql.Timestamp;

import javax.swing.JOptionPane;

import jdo.ekt.EKTIO;
import jdo.inw.InwUtil;
import jdo.pha.PassTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSNewRegionTool;
import jdo.sys.SystemTool;
import jdo.udd.UddChnCheckTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
 
/**
 * <p>
 * Title: סԺҩ����ҩ���
 * </p>
 * 
 * <p>
 * Description: סԺҩ����ҩ���
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
public class UddMedCheck extends TControl {

	public static final String Y = "Y";
	public static final String N = "N";
	public static final String NULL = "";
	private String odiOrdercat = "";
	/**
	 * ��ʼ�������б��SQL
	 */
	private static final String PAT_SQL = "SELECT 'N' AS EXEC,A.CASE_NO,A.BED_NO,B.PAT_NAME,A.STATION_CODE,"
			+ "       A.MR_NO,A.IPD_NO, '' AS AGE, B.BIRTH_DATE, "
			+ "       D.USER_NAME AS PHA_CHECK_CODE, A.PHA_CHECK_DATE, A.ORDER_NO "
			+ "	FROM ODI_ORDER A , SYS_PATINFO B ,SYS_BED C, SYS_OPERATOR D "
			+ "	WHERE  B.MR_NO=A.MR_NO "
			+ "		  AND C.BED_NO=A.BED_NO "
			+
			// "		  AND (C.ALLO_FLG IS NOT NULL AND C.ALLO_FLG='Y') " +
			// "		  AND (C.BED_OCCU_FLG IS NULL OR C.BED_OCCU_FLG='N')  " +
			"		  AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C') "
			+ "                 AND A.PHA_CHECK_CODE = D.USER_ID(+) ";
//			+ " AND A.DISPENSE_FLG='N' "//luhai 2012-3-6 add DISPENSE_FLG='Y' ��ҩ�������䷢����
//			+ "       AND A.OPBOOK_SEQ IS NULL ";//wanglong add 20141114 ���˵�����ҽ��//wangjc modify
	
	TTable tblPat, tblDtl;
	/**
	 * ������Ҫ�ļ���
	 */
	TParm saveParm = new TParm();
	/**
	 * �Ƿ���Ҫ��ʿ���
	 */
	boolean isNsCheck = false;
	/**
	 * ������ҩ
	 */
	boolean passIsReady = false;
	private boolean enforcementFlg = false;
	private int warnFlg;

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		TParm stationParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM SYS_STATION WHERE ORG_CODE='"
						+ Operator.getDept() + "' "));
		// System.out.println("SELECT * FROM SYS_STATION WHERE ORG_CODE='"+Operator.getDept()+"' ");
		// System.out.println(stationParm);
		tblPat = (TTable) this.getComponent("TBL_PAT");
		tblDtl = (TTable) this.getComponent("TBL_MED");
		passIsReady = SYSNewRegionTool.getInstance().isIREASONABLEMED(
				Operator.getRegion());
		enforcementFlg = "Y".equals(TConfig.getSystemValue("EnforcementFlg"));
		warnFlg = Integer.parseInt(TConfig.getSystemValue("WarnFlg"));
		isNsCheck = InwUtil.getInstance().getNsCheckEXEFlg();
		odiOrdercat = "(A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C')";
		onClear();
		// ������ҩ
		if (passIsReady) {
			if (!PassTool.getInstance().init()) {
				this.messageBox("������ҩ��ʼ��ʧ�ܣ�");
			}
		}
		//fux modify 20150821   
		callFunction("UI|TBL_MED|addEventListener",
		TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBoxClicked");
	}

	/**
	 * ������ʾ checkBox
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public void onTableCheckBoxClicked(Object obj) {
		// �õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
		tblDtl.acceptText();
		TTable node = (TTable) obj;
		if (node == null)
			return;
		// ����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
		//int column = node.getSelectedColumn();
		int selectRow = node.getSelectedRow();
		TParm parm = new TParm();
		parm = tblDtl.getParmValue().getRow(selectRow);    
		if ("Y".equals(node.getParmValue().getValue("ASK_FLG", selectRow))){
			this.openDialog("%ROOT%\\config\\udd\\UddCdss.x", parm);
			tblDtl.setItem(selectRow, "ASK_FLG", "N");                      
//			node.getParmValue().setData("FLG", selectRow, "N");  
//			tblDtl.setParmValue(node.getParmValue());  
		}
	}     
	  
	/**  
	 * ���
	 */
	public void onClear() {
		Timestamp t = TJDODBTool.getInstance().getDBTime();
		this.setValue("START_DATE", StringTool.rollDate(t, -7));
		this.setValue("END_DATE", t);
		this.setValue("UDST", Y);
		this.setValue("EXEC_DEPT_CODE", Operator.getDept());
		// this.setValue("EXEC_DEPT_CODE", "308003");
		this.setValue("AGENCY_ORG_CODE", NULL);
		this.setValue("STA", Y);
		this.setValue("UNCHECK", Y);
		this.setValue("NO", NULL);
		this.setValue("NAME", NULL);
		tblPat.removeRowAll();
		tblDtl.removeRowAll();
		setEnableMenu();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		StringBuffer sql = new StringBuffer();
		sql.append(PAT_SQL).append(getWhere());
		/*
		 * 
		 * ִ,40,boolean;����,80;����,80;������,100;��ʿվ,120;������,120;סԺ��,120
		 * EXEC;BED_NO;PAT_NAME;RX_NO;STATION_CODE;MR_NO;IPD_NO
		 */
		tblPat.removeRowAll();
		String sqlStr = sql
				.append(" GROUP BY A.CASE_NO,A.BED_NO,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO, B.BIRTH_DATE, D.USER_NAME, A.PHA_CHECK_DATE, A.ORDER_NO ")
				.append(" ORDER BY A.CASE_NO").toString();
		//System.out.println("סԺ��ҩ��ѯ��䣺"+sqlStr);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sqlStr));
		Timestamp date = SystemTool.getInstance().getDate();
		for (int i = 0; i < parm.getCount("AGE"); i++) {
			parm.setData(
					"AGE",
					i,
					StringUtil.getInstance().showAge(
							parm.getTimestamp("BIRTH_DATE", i), date));
		}
		sql = null;  
		tblPat.setParmValue(parm);
		if (StringTool.getBoolean(this.getValueString("MR"))
				|| StringTool.getBoolean(this.getValueString("BED"))) {
			this.setValue("NAME", parm.getValue("PAT_NAME", 0));
		}
		if (tblDtl != null) {
			tblDtl.removeRowAll();
		}
		// onQueryDtl();
		setEnableMenu();
	}

	/**
	 * ��ѯҩƷ��ϸ
	 */
	public void onQueryDtl() {
		TParm parm = tblPat.getParmValue();
		StringBuffer caseNos = new StringBuffer();
		// System.out.println("tableParm=========="+parm);
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			// String exec=parm.getValue("EXEC",i);
			if (parm.getBoolean("EXEC", i))
				caseNos.append("'").append(parm.getValue("CASE_NO", i))
						.append("'");
		}
		// System.out.println("sql========="+caseNos);
		if (StringUtil.isNullString(caseNos.toString())) {
			this.messageBox_("��������");
			tblDtl.removeRowAll();
			return;
		}
		String order_no = " AND A.ORDER_NO = '"
				+ parm.getValue("ORDER_NO", tblPat.getSelectedRow()) + "'";
		// String bed_no = " AND A.BED_NO = '" +
		// parm.getValue("BED_NO", tblPat.getSelectedRow()) + "'";
		// ===========pangben modify 20110511 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ===========pangben modify 20110511 stop
		String pnFlgSql = "(SELECT PN_FLG FROM ODI_DSPNM B WHERE B.CASE_NO=A.CASE_NO AND B.ORDER_NO=A.ORDER_NO AND B.ORDER_SEQ=A.ORDER_SEQ AND B.DSPN_KIND <> 'UD')";
		String sql = " SELECT A.CASE_NO , A.ORDER_NO,A.ORDER_SEQ,A.ORDER_CODE,A.ORDER_DESC||' '||A.GOODS_DESC|| '('|| A.SPECIFICATION ||')' ORDER_DESC, "  //lirui  2012-6-6   ҩ���ӹ��
				+ "	 A.LINKMAIN_FLG,A.LINK_NO,A.MEDI_QTY,A.MEDI_UNIT,A.FREQ_CODE, "
				+ "	 A.ROUTE_CODE,A.TAKE_DAYS,A.DR_NOTE,TO_CHAR(A.EFF_DATE,'YYYY/MM/DD HH24:MI') AS EFF_DATE, "  //lirui  2012-6-6    ��ϸ������
				+ "	 (CASE WHEN TO_CHAR(sysdate,'YYYY/MM/DD') >= TO_CHAR(A.DC_DATE,'YYYY/MM/DD') THEN TO_CHAR(A.DC_DATE,'YYYY/MM/DD HH24:MI') ELSE '' END   ) AS DC_DATE ,A.GOODS_DESC,A.ORDER_DR_CODE,A.TAKEMED_ORG "
				+ "  ,A.RX_KIND,"+pnFlgSql+" AS PN_FLG" 
				//+ ",B.ADVISE,B.RISK_LEVEL,B.BYPASS_REASON "//20150505 wangjingchun add ����Ӫ����
				//fux modify 20150807
				+ " FROM ODI_ORDER A" 
			    //+ ",DSS_CKBLOG B"       
				+ " WHERE A.CASE_NO ="   
				+ caseNos.toString()  
				+ "	AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C') "  
				
//				+ " AND A.CASE_NO = B.CASE_NO(+)   "   
//				+ " AND A.ORDER_NO = B.ORDER_NO(+)  "    
//				+ " AND A.ORDER_SEQ = B.ORDER_SEQ(+)  "
				
//				+ " AND A.DISPENSE_FLG='N'"// luhai 2012-3-6 add ��ҩ���������䷢����  AND A.DISPENSE_FLG='N'
				+ getWhere() + order_no + region + // bed_no +  
//			     " AND  A.DC_DATE IS NULL " +//by liyh 20120905 �Ѿ�ͣ�õ�ҽ���������       �����ϳ�������Ӧ��ע��   shibl 20121017 modidfy
				" ORDER BY A.ORDER_NO,A.LINK_NO,A.LINKMAIN_FLG DESC,A.EFF_DATE";
				
//		System.out.println("�����ϸ��ѯ----------------------"+sql);
		saveParm = new TParm(TJDODBTool.getInstance().select(sql));
		tblDtl.setParmValue(saveParm);
		//===============  modify  by  chenxi  20120703  ����ҩƷ��ɫ��ʾ
		TParm tableParm = tblDtl.getParmValue() ;
		Color normalColor = new Color(0, 0, 0);
		Color blueColor  =  new Color(0, 0, 255);
		Color yellewColor = new Color(255,255,160);
		for (int i = 0; i < tableParm.getCount(); i++) {
			//=========ҩƷ��ʾ��Ϣ      modify  by  chenxi 
			String orderCode = tableParm.getValue("ORDER_CODE",i) ;
			String order_noSql = " AND A.ORDER_NO = '"
				+ tableParm.getValue("ORDER_NO",i) + "'";
		    String order_seqSql = " AND A.ORDER_SEQ = '"
			    + tableParm.getValue("ORDER_SEQ",i) + "'";
		// String bed_no = " AND A.BED_NO = '" +
		// parm.getValue("BED_NO", tblPat.getSelectedRow()) + "'";
		// ===========pangben modify 20110511 start
		String regionSql = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
		}
			String sqlNew = " SELECT A.CASE_NO , A.ORDER_NO,A.ORDER_SEQ,A.ORDER_CODE,A.ORDER_DESC||' '||A.GOODS_DESC|| '('|| A.SPECIFICATION ||')' ORDER_DESC, "  //lirui  2012-6-6   ҩ���ӹ��
				+ " B.ADVISE,B.RISK_LEVEL,B.BYPASS_REASON "
				+ " FROM ODI_ORDER A" 
			    + ",DSS_CKBLOG B"         
				+ " WHERE A.CASE_NO ="   
				+ caseNos.toString() 
				+ "	AND (A.ORDER_CAT1_CODE='PHA_W' OR A.ORDER_CAT1_CODE='PHA_C') "  
				+ " AND A.CASE_NO = B.CASE_NO   "   
				+ " AND A.ORDER_NO = B.ORDER_NO  "        
				+ " AND A.ORDER_SEQ = B.ORDER_SEQ  "
			    + order_noSql + regionSql + order_seqSql + // bed_no +          
				" ORDER BY B.RISK_LEVEL";  
			//System.out.println("sqlNew:"+sqlNew);
			TParm sqlparmNew = new TParm(TJDODBTool.getInstance().select(sqlNew)) ;
			//sqlparmNew = sqlparmNew.getRow(0) ;  
			if(sqlparmNew.getCount("CASE_NO")>0){   
				 tblDtl.setRowColor(i,yellewColor );
			}else{                                       
				 tblDtl.setRowColor(i,null);    
			}                                
			String  sql1 = "SELECT ORDER_CODE,DRUG_NOTES_DR FROM SYS_FEE WHERE ORDER_CODE = '" +orderCode+ "'" ;
			TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql1)) ;
			sqlparm = sqlparm.getRow(0) ;
			 if(sqlparm.getValue("DRUG_NOTES_DR").length()==0){   
				 tblDtl.setRowTextColor(i, normalColor);          
				 continue ;  
			}                                                           
			 tblDtl.setRowTextColor(i, blueColor) ;                         

			 
			 
			 
			
		}
	}

	/**
	 * ���ս���Ĳ�ѯ�����Ƿ�����ƴװWHERE����
	 * 
	 * @return where String
	 */
	public String getWhere() {
		StringBuffer result = new StringBuffer();
		String startDate = StringTool.getString(
				TCM_Transform.getTimestamp(this.getValue("START_DATE")),
				"yyyyMMddHHmmss");
		String endDate = StringTool.getString(
				TCM_Transform.getTimestamp(this.getValue("END_DATE")),
				"yyyyMMddHHmmss").substring(0, 8)
				+ "235959";
		// ===========pangben modify 20110511 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			result.append(" AND A.REGION_CODE='" + Operator.getRegion() + "'");
		}
		// ===========pangben modify 20110511 stop
		if (isNsCheck) {
			result.append(" AND A.NS_CHECK_CODE IS NOT NULL ");
		} else {
			result.append(" AND (A.DC_DATE >= A.START_DTTM OR A.DC_DATE IS NULL) ");
		}
		if (StringTool.getBoolean(this.getValueString("UNCHECK"))) {
			result.append(" AND A.EFF_DATE >=TO_DATE('" + startDate
					+ "','YYYYMMDDHH24MISS') AND A.EFF_DATE<=TO_DATE('"
					+ endDate
					+ "','YYYYMMDDHH24MISS') AND A.PHA_CHECK_DATE IS NULL ");
		} else {
			result.append(" AND A.PHA_CHECK_DATE >=TO_DATE('" + startDate
					+ "','YYYYMMDDHH24MISS') AND A.PHA_CHECK_DATE<=TO_DATE('"
					+ endDate + "','YYYYMMDDHH24MISS')");
		}
		result.append(" AND A.EXEC_DEPT_CODE='"
				+ this.getValueString("EXEC_DEPT_CODE") + "'");
		if (TypeTool.getBoolean(this.getValue("UDST"))) {
			//20151106 wangjc ADD
			if(this.getValueString("OPBOOK_SEQ").equals("Y")){
				result.append(" AND RX_KIND IN ('UD','ST','OP') ");
			}else{
				result.append(" AND RX_KIND IN ('UD','ST') ");
			}
		} else {
			result.append(" AND RX_KIND='DS'");
		}
		//20150625 wangjc add start
		if(this.getValueString("OPBOOK_SEQ").equals("Y")){
			result.append(" AND A.OPBOOK_SEQ IS NOT NULL ");
		}else{
			result.append(" AND A.OPBOOK_SEQ IS NULL ");
		}
		//20150625 wangjc add end
		if (!StringUtil.isNullString(this.getValueString("AGENCY_ORG_CODE"))) {
			result.append(" AND A.AGENCY_ORG_CODE='"
					+ this.getValueString("AGENCY_ORG_CODE") + "'");
		}
		if (StringTool.getBoolean(this.getValueString("STA"))) {
			if (!StringUtil.isNullString(this.getValueString("STATIONCOMBOL"))) {
				result.append(" AND A.STATION_CODE='"
						+ this.getValueString("STATIONCOMBOL") + "'");
			}
		} else if (StringTool.getBoolean(this.getValueString("MR"))) {
			String mrNo = StringTool.fill0(this.getValueString("NO"), PatTool.getInstance().getMrNoLength());//===cehnxi
			this.setValue("NO", mrNo);
			
			 // modify by huangtt 20160928 EMPI���߲�����ʾ start
			Pat pat = Pat.onQueryByMrNo(getValueString("NO"));
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
				setValue("NO", pat.getMrNo());
				mrNo =  pat.getMrNo();
			}
			// modify by huangtt 20160928 EMPI���߲�����ʾ end
			
			
			result.append(" AND A.MR_NO='" + mrNo + "'");
		} else {
			result.append(" AND A.BED_NO='" + this.getValueString("NO") + "' ");
		}
		return result.toString();
	}

	/**
	 * ����TABLE����¼�������TABLEֻ����һ������ѡ
	 */
	public void onTblPatClick() {
		boolean value = TCM_Transform.getBoolean(tblPat.getValueAt(
				tblPat.getSelectedRow(), 0));
		int allRow = tblPat.getRowCount();

		for (int i = 0; i < allRow; i++) {
			tblPat.setValueAt(false, i, 0);
			tblPat.getParmValue().setData("EXEC", i, false);
		}
		tblPat.setValueAt(true, tblPat.getSelectedRow(), 0);
		tblPat.getParmValue().setData("EXEC", tblPat.getSelectedRow(), true);
		// System.out.println("click parm======"+tblPat.getParmValue());
		tblDtl.removeRowAll();
		onQueryDtl();
	}

	/**
	 * ����
	 */
	public void onSave() {
		if (TypeTool.getBoolean(this.getValue("CHECK"))) {
			this.messageBox_("�ޱ�������");
			return;
		}
		TParm parm = new TParm();
		/*
		 * UPDATE ODI_ORDER SET
		 * PHA_CHECK_CODE=<OPT_USER>,PHA_CHECK_DATE=SYSDATE,
		 * OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> WHERE
		 * CASE_NO=<CASE_NO> AND ORDER_NO=<ORDER_NO> AND ORDER_SEQ=<ORDER_SEQ>
		 */
		/*
		 * UPDATE ODI_DSPNM SET
		 * PHA_DOSAGE_CODE=<OPT_USER>,PHA_DOSAGE_DATE=SYSDATE,
		 * OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> WHERE
		 * CASE_NO=<CASE_NO> AND ORDER_NO=<ORDER_NO> AND ORDER_SEQ=<ORDER_SEQ>
		 */
		// System.out.println("savePaarm==="+saveParm);
		for (int i = 0; i < saveParm.getCount("ORDER_SEQ"); i++) {
			parm.addData("OPT_USER", Operator.getID());
			parm.addData("OPT_TERM", Operator.getIP());
			parm.addData("CASE_NO", saveParm.getValue("CASE_NO", i));
			parm.addData("ORDER_NO", saveParm.getValue("ORDER_NO", i));
			parm.addData("ORDER_SEQ", saveParm.getInt("ORDER_SEQ", i));
			parm.addData("DCTAGENT_FLG", "N");
			parm.addData("RX_KIND", saveParm.getValue("RX_KIND", i));//20150505 wangjingchun add ����Ӫ����
			parm.addData("PN_FLG", saveParm.getValue("PN_FLG", i));//20150505 wangjingchun add ����Ӫ����
		}
		if (parm.getCount("OPT_USER") < 1) {
			this.messageBox_("�޿ɱ�������");
			return;
		}
		// ������ҩ
		if (!checkDrugAuto()) {
			return;
		}
//		System.out.println("parm===="+parm);
		parm = TIOM_AppServer.executeAction("action.udd.UddAction",
				"onUpdateMedCheck", parm);
		if (parm.getErrCode() != 0) {
			// this.messageBox_(parm.getErrText());
			this.messageBox("E0001");
		} else {
			this.messageBox("P0001");
		}
//		onClear();
//		Timestamp t = TJDODBTool.getInstance().getDBTime();
//		this.setValue("START_DATE", StringTool.rollDate(t, -7));
//		this.setValue("END_DATE", t);
//		this.setValue("UDST", Y);
//		this.setValue("EXEC_DEPT_CODE", Operator.getDept());
//		// this.setValue("EXEC_DEPT_CODE", "308003");
//		this.setValue("AGENCY_ORG_CODE", NULL);
//		this.setValue("STA", Y);
//		this.setValue("UNCHECK", Y);
//		this.setValue("NO", NULL);
//		this.setValue("NAME", NULL);
		//luhai modify 2012-3-19 �������ղ�ѯ״̬ begin
		//onClear();
		tblPat.removeRowAll();
		tblDtl.removeRowAll();
		setEnableMenu();
		this.onQuery();
		//luhai modify 2012-3-19 �������ղ�ѯ״̬ end
	}

	/**
	 * ȡ�����
	 */
	public void onDelete() {
		if (!TypeTool.getBoolean(this.getValue("CHECK"))) {
			this.messageBox_("�ޱ�������");
			return;
		}
		TParm parm = new TParm();
		// System.out.println("savePaarm==="+saveParm);
		for (int i = 0; i < saveParm.getCount("ORDER_SEQ"); i++) {
			parm.addData("OPT_USER", Operator.getID());
			parm.addData("OPT_TERM", Operator.getIP());
			parm.addData("CASE_NO", saveParm.getValue("CASE_NO", i));
			parm.addData("ORDER_NO", saveParm.getValue("ORDER_NO", i));
			parm.addData("ORDER_SEQ", saveParm.getInt("ORDER_SEQ", i));
			parm.addData("RX_KIND", saveParm.getValue("RX_KIND", i));//20150505 wangjingchun add ����Ӫ����
		}
		// System.out.println("parm========"+parm);
		if (parm.getCount("OPT_USER") < 1) {
			this.messageBox_("�޿ɱ�������");
			return;
		}
		if (!isDosage()) {
			return;
		}
		parm = TIOM_AppServer.executeAction("action.udd.UddAction",
				"onUpdateUnCheck", parm);
		if (parm.getErrCode() != 0) {
			// this.messageBox_(parm.getErrText());
			this.messageBox("E0001");
		} else {
			this.messageBox("P0001");
		}
		onClear();
	}

	/**
	 * ȡ��
	 * 
	 * @return
	 */
	private boolean isDosage() {
		if (saveParm == null) {
			return false;
		}
		int count = saveParm.getCount();
		if (count < 1) {
			return false;
		}
		for (int i = 0; i < count; i++) {
			if (!UddChnCheckTool.getInstance().isDosage(saveParm.getRow(i))) {
				this.messageBox_("ҩƷ����ҩ,����ȡ�����");
				return false;
			}
		}
		return true;
	}

	/**
	 * ������/���������Ļس���ѯ�¼�
	 */
	public void onNo() {
		onQuery();
	}

	/**
	 * ��ʿվcombo�����ѯ�¼�
	 */
	public void onQueryStation() {
		onQuery();
	}

	public TParm MedtableClick() {
		TParm parm = new TParm();
		int row = tblDtl.getSelectedRow();
		if (row < 0) {
		}
		//20150505 wangjingchun add start
		//ѡȡ����Ӫ��
		int column = tblDtl.getSelectedColumn();
		if (getRadioButton("UNCHECK").isSelected()) {
			if ("N".equals(tblDtl.getItemString(row, "PN_FLG"))
					&& column == 8 
//					&& "ST".equals(tblDtl.getParmValue().getValue("RX_KIND", row))
					&& !"".equals(tblDtl.getParmValue().getValue("LINK_NO", row))) {
//				tblDtl.setItem(row, "PN_FLG", "Y");
				for(int i=0;i<tblDtl.getParmValue().getCount("CASE_NO");i++){
					if(tblDtl.getParmValue().getValue("LINK_NO", i).equals(tblDtl.getParmValue().getValue("LINK_NO", row))){
						tblDtl.setItem(i, "PN_FLG", "Y");
					}
				}
			} else if ("Y".equals(tblDtl.getItemString(row, "PN_FLG"))
					&& column == 8 
//					&& "ST".equals(tblDtl.getParmValue().getValue("RX_KIND", row))
					&& !"".equals(tblDtl.getParmValue().getValue("LINK_NO", row))){
				for(int i=0;i<tblDtl.getParmValue().getCount("CASE_NO");i++){
					if(tblDtl.getParmValue().getValue("LINK_NO", i).equals(tblDtl.getParmValue().getValue("LINK_NO", row))){
						tblDtl.setItem(i, "PN_FLG", "N");
					}
				}
//				tblDtl.setItem(row, "PN_FLG", "N");
			} else if (column == 8){
				tblDtl.setItem(row, "PN_FLG", "N");
			}
		}else{
			if (column == 8 
					&& !"ST".equals(tblDtl.getParmValue().getValue("RX_KIND", row))) {
				tblDtl.setItem(row, "PN_FLG", "N");
			}
		}
		//20150505 wangjingchun add end
		String value = saveParm.getValue("ORDER_CODE", row);
		String orderNO = saveParm.getValue("ORDER_NO", row);
		int seq = saveParm.getInt("ORDER_SEQ", row);
		parm.setData("ORDER_CODE", value);
		parm.setData("ORDER_NO", orderNO);
		parm.setData("ORDER_SEQ", seq);
		//============== chenxi   modify  20120703 
		TParm  action = tblDtl.getParmValue() ;
		String orderCode =  action.getValue("ORDER_CODE", row) ;
		String sql = " SELECT ORDER_CODE,ORDER_DESC,GOODS_DESC," +
		"DESCRIPTION,SPECIFICATION,REMARK_1,REMARK_2,DRUG_NOTES_DR FROM SYS_FEE" +
		" WHERE ORDER_CODE = '" +orderCode+ "'" ;
          TParm sqlparm = new TParm(TJDODBTool.getInstance().select(sql)) ;
         sqlparm = sqlparm.getRow(0);
         //״̬����ʾҽ����ʾ
         callFunction(
  				"UI|setSysStatus",
  				sqlparm.getValue("ORDER_CODE") + " " + sqlparm.getValue("ORDER_DESC")
  						+ " " + sqlparm.getValue("GOODS_DESC") + " "
  						+ sqlparm.getValue("DESCRIPTION") + " "
  						+ sqlparm.getValue("SPECIFICATION") + " "
  						+ sqlparm.getValue("REMARK_1") + " "
  						+ sqlparm.getValue("REMARK_2") + " "
  						+ sqlparm.getValue("DRUG_NOTES_DR"));
         // ===============  modify  by   chenxi   20120703
		return parm;
	}

	/**
	 * ҩƷ��Ϣ��ѯ
	 */
	public void queryDrug() {
		if (!passIsReady) {
			messageBox("������ҩδ����");
			return;
		}
		if (!PassTool.getInstance().init()) {
			this.messageBox("������ҩ��ʼ��ʧ�ܣ��˹��ܲ���ʹ�ã�");
			return;
		}
		int row = getTable("TBL_MED").getSelectedRow();
		if (row < 0) {
			return;
		}
		String value = (String) this
				.openDialog("%ROOT%\\config\\pha\\PHAOptChoose.x");
		if (value == null || value.length() == 0) {
			return;
		}
		int conmmand = Integer.parseInt(value);
		if (conmmand != 6) {
			PassTool.getInstance().setQueryDrug(
					MedtableClick().getValue("ORDER_CODE"), conmmand);
		} else {
			PassTool.getInstance().setWarnDrug2(
					MedtableClick().getValue("ORDER_NO"),
					" " + MedtableClick().getValue("ORDER_SEQ"));
		}

	}

	/**
	 * �ֶ���������ҩ
	 */
	public void checkDrugHand() {
		if (!passIsReady) {
			messageBox("������ҩδ����");
			return;
		}
		if (!PassTool.getInstance().init()) {
			this.messageBox("������ҩ��ʼ��ʧ�ܣ��˹��ܲ���ʹ�ã�");
			return;
		}
		if (saveParm.getValue("CASE_NO", 0) == null) {
			return;
		}
		String type="";
		if (TypeTool.getBoolean(this.getValue("UDST"))) {
			type="UDST";
		} else {
			type="DS";
		}
		PassTool.getInstance().init();
		PassTool.getInstance().setadmPatientInfo(
				saveParm.getValue("CASE_NO", 0));
		PassTool.getInstance().setAllergenInfo(saveParm.getValue("MR_NO", 0));
		PassTool.getInstance().setadmMedCond(saveParm.getValue("CASE_NO", 0));
		TParm parm = PassTool.getInstance().setadmRecipeInfoHand(
				saveParm.getValue("CASE_NO", 0), odiOrdercat,type);
		isWarn(parm);
	}
	/**
	 * ҽ�ƿ�����
	 */
	public void onEKT() {
		TParm parm = EKTIO.getInstance().TXreadEKT();
        //System.out.println("parm==="+parm);
    	if (null == parm || parm.getValue("MR_NO").length() <= 0) {
            this.messageBox("��鿴ҽ�ƿ��Ƿ���ȷʹ��");
            return;
        } 
    	//zhangp 20120130
    	if(parm.getErrCode()<0){
    		messageBox(parm.getErrText());
    	}
		setValue("NO", parm.getValue("MR_NO"));
		TRadioButton td=(TRadioButton)this.getComponent("MR");
		td.setSelected(true);
		this.onQuery();
		//�޸Ķ�ҽ�ƿ�����  end luhai 2012-2-27 
	}
	/**
	 * �Զ���������ҩ
	 */
	private boolean checkDrugAuto() {
		if (!passIsReady) {
			return true;
		}
		if (!PassTool.getInstance().init()) {
			return true;
		}
		String type="";
		if (TypeTool.getBoolean(this.getValue("UDST"))) {
			type="UDST";
		} else {
			type="DS";
		}
		PassTool.getInstance().init();
		PassTool.getInstance().setadmPatientInfo(
				saveParm.getValue("CASE_NO", 0));
		PassTool.getInstance().setAllergenInfo(saveParm.getValue("MR_NO", 0));
		PassTool.getInstance().setadmMedCond(saveParm.getValue("CASE_NO", 0));
		TParm parm = PassTool.getInstance().setadmRecipeInfoAuto(
				saveParm.getValue("CASE_NO", 0), odiOrdercat,type);
		if (!isWarn(parm)) {
			return true;
		}
		if (enforcementFlg) {
			return false;
		}
		if (JOptionPane.showConfirmDialog(null, "��ҩƷʹ�ò�����,�Ƿ�浵?", "��Ϣ",
				JOptionPane.YES_NO_OPTION) != 0) {
			return false;
		}
		return true;
	}

	private boolean isWarn(TParm parm) {
		boolean warnFlg = false;
		for (int i = 0; i < parm.getCount("ORDER_NO"); i++) {
			int flg = parm.getInt("FLG", i);
			if (!warnFlg) {
				if (getWarn(flg)) {
					warnFlg = true;
				} else {
					warnFlg = false;
				}
			}
		}
		return warnFlg;
	}

	private boolean getWarn(int flg) {
		if (warnFlg != 3 && flg != 3) {
			if (warnFlg != 2 && flg != 2) {
				if (flg >= warnFlg) {
					return true;
				} else {
					return false;
				}
			} else if (warnFlg == 2 && flg != 2) {
				return false;
			} else if (warnFlg != 2 && flg == 2) {
				return true;
			} else if (warnFlg == 2 && flg == 2) {
				return true;
			}
		} else if (warnFlg == 3 && flg != 3) {
			return false;
		} else if (warnFlg != 3 && flg == 3) {
			return true;
		} else if (warnFlg == 3 && flg == 3) {
			return true;
		}
		return false;
	}

	/**
	 * ��ʾ��Ʒ��
	 */
	public void onShowGoodsName() {
		if (tblDtl == null)
			return;
		TParm parm = tblDtl.getParmValue();
		// System.out.println("before goods=============="+parm);
		int count = parm.getCount();
		if (count < 1)
			return;
		// LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;EFF_DATE;DC_DATE;DR_NOTE;ORDER_DR_CODE
		boolean showGoods = TypeTool.getBoolean(this.getValue("SHOW_DESC"));
		// tblDtl.removeRowAll();
		if (showGoods) {
			tblDtl.setParmMap("LINKMAIN_FLG;LINK_NO;GOODS_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;EFF_DATE;DC_DATE;DR_NOTE;ORDER_DR_CODE");
		} else {
			tblDtl.setParmMap("LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;ROUTE_CODE;EFF_DATE;DC_DATE;DR_NOTE;ORDER_DR_CODE");
		}
		tblDtl.setParmValue(parm);
		// System.out.println("goods parm======="+parm);
	}

	public void setEnableMenu() {
		if (TypeTool.getBoolean(this.getValue("UNCHECK"))) {
			callFunction("UI|save|setEnabled", true);
			callFunction("UI|delete|setEnabled", false);
		} else {
			callFunction("UI|save|setEnabled", false);
			callFunction("UI|delete|setEnabled", true);
		}
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

	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
	
	// �õ�RadioButton�ؼ�
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
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
    	TTable table = (TTable) this.getComponent("TBL_PAT");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("���ѡ������!");
			return;
		}
		String sql = "SELECT  DISTINCT MED_APPLY_NO  FROM ODI_ORDER WHERE CASE_NO = '"+table.getParmValue().getValue("CASE_NO",selRow)+"' AND ORDER_CAT1_CODE = 'ECC'";
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
    	parm.setData("CASE_NO",table.getParmValue().getValue("CASE_NO",selRow));
    	parm.setData("TYPE","3");
    	this.openDialog("%ROOT%\\config\\odi\\ODIOpeMr.x", parm);
    }
    
    /**
	 * ���鱨��
	 */
	public void onLis() {
		TTable table = (TTable) this.getComponent("TBL_PAT");
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
		TTable table = (TTable) this.getComponent("TBL_PAT");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("���ѡ������!");
			return;
		}
		SystemTool.getInstance().OpenRisWeb(table.getParmValue().getValue("MR_NO",selRow));
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
}
