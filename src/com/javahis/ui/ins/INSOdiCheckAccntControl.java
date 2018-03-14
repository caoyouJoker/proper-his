package com.javahis.ui.ins;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.text.DecimalFormat;
import java.util.Vector;

import jdo.ins.INSTJTool;
import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 住院医保对账
 * </p>
 * 
 * <p>
 * Description: 住院医保对账
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
 * @author zhangp 20120210
 * @version 1.0
 */
public class INSOdiCheckAccntControl extends TControl {
	private TTable table;
	private TTable table2;
	private Compare compare = new Compare();
	private Compare compareOne = new Compare();
	private int sortColumnOne = -1;
	private boolean ascendingOne = false;
	private int sortColumn = -1;
	private boolean ascending = false;
	DecimalFormat dfm=new DecimalFormat("###0.00"); 
	/**
	 * 初始化方法
	 */
	public void onInit() {
		table=(TTable)this.getComponent("TABLE1");	
		table2=(TTable)this.getComponent("TABLE2");
		setValue("CTZ_CODE", 1);
		setValue("LOCAL_FLG", "Y");
		setValue("START_DATE", SystemTool.getInstance().getDate());
		setValue("END_DATE", SystemTool.getInstance().getDate());
		addListener(table);
		addListenerOne(table2);
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		String regionCode = Operator.getRegion();// 区域
		String startDate = "";
		String endDate = "";
		if (!"".equals(this.getValueString("START_DATE"))
				&& !"".equals(this.getValueString("END_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7)
					+ startDate.substring(8, 10) + "000000";
			endDate = endDate.substring(0, 4) + endDate.substring(5, 7)
					+ endDate.substring(8, 10) + "235959";
		} else {
			messageBox("请输入查询条件");
			return;
		}
		int ctz = getValueInt("CTZ_CODE");
		String localFlg = getValueString("LOCAL_FLG");//zhangs 20160908 add
		String sql = "SELECT B.ADM_SEQ, B.CONFIRM_NO, B.YEAR_MON, A.PAT_NAME, D.CTZ_DESC,C.CHN_DESC, "
				+ " (B.PHA_AMT + B.EXM_AMT + B.TREAT_AMT + B.OP_AMT + B.BED_AMT + B.MATERIAL_AMT + B.OTHER_AMT"
				+ " + B.BLOODALL_AMT + B.BLOOD_AMT) HEJI1, "
				+ " (B.PHA_NHI_AMT + B.EXM_NHI_AMT + B.TREAT_NHI_AMT + B.OP_NHI_AMT + B.BED_NHI_AMT + B.MATERIAL_NHI_AMT"
				+ " + B.OTHER_NHI_AMT + B.BLOODALL_NHI_AMT + B.BLOOD_NHI_AMT) HEJI2, "
				+ " B.OWN_AMT, B.ADD_AMT, B.NHI_PAY, B.NHI_COMMENT, B.ARMYAI_AMT,B.ILLNESS_SUBSIDY_AMT "
				+ " FROM INS_ADM_CONFIRM A, INS_IBS B, SYS_DICTIONARY C, SYS_CTZ D "
				+ " WHERE A.CONFIRM_NO = B.CONFIRM_NO "
				+ " AND A.CASE_NO = B.CASE_NO "
				+ " AND A.ADM_CATEGORY = C.ID "
				+ " AND C.GROUP_ID ='INS_JYLB' "
				+ " AND A.IN_STATUS IN('2','4') "
				+ " AND A.LOCAL_FLG='"+localFlg//zhangs 20160908 add localFlg
				+ "' AND B.REGION_CODE = '"
				+ regionCode
				+ "' "
				+ " AND B.UPLOAD_DATE BETWEEN TO_DATE ('"
				+ startDate
				+ "', 'YYYYMMDDHH24MISS') "
				+ " AND TO_DATE ('"
				+ endDate
				+ "','YYYYMMDDHH24MISS') "
				+ " AND A.HIS_CTZ_CODE = D.CTZ_CODE AND D.NHI_CTZ_FLG = 'Y' ";
		if (ctz == 1) {
			sql = sql + " AND D.INS_CROWD_TYPE= '1'";// 城职
		}
		if (ctz == 2) {
			sql = sql + " AND D.INS_CROWD_TYPE= '2'";// 城居
		}
//		System.out.println("onQuery:"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		if (result.getCount() < 0) {
			messageBox("查无数据");
		}
		this.callFunction("UI|TABLE1|setParmValue", result);
	}

	/**
	 * 对总账
	 */
	public void onCheckAll() {
//		System.out.println("onCheckAll");
		String regionCode = Operator.getRegion();// 区域
		String startDate = "";
		String endDate = "";
		int ctz = getValueInt("CTZ_CODE");
	    String localFlg = getValueString("LOCAL_FLG");//zhangs 20160908 add
//	    System.out.println("LOCAL_FLG:"+localFlg);
		if (!"".equals(this.getValueString("START_DATE"))
				&& !"".equals(this.getValueString("END_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7)
					+ startDate.substring(8, 10) + "000000";
			endDate = endDate.substring(0, 4) + endDate.substring(5, 7)
					+ endDate.substring(8, 10) + "235959";
		} else {
			messageBox("请输入查询条件");
			return;
		}
		TParm result = queryCheckAll(regionCode, startDate, endDate, ctz,localFlg);//zhangs 20160908 add localFlg
//		System.out.println("queryCheckAll:"+result);
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		if (result.getCount() < 0) {
			messageBox("无数据");
			return;
		}
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		String hospital = regionParm.getData("NHI_NO", 0).toString();// 获取HOSP_NHI_NO
		TParm parm = new TParm();
		parm.addData("START_DATE", startDate.substring(0,8));// 开始时间
		parm.addData("END_DATE", endDate.substring(0,8));// 结束时间
		// 传参
//		System.out.println(dfm.format(result.getData("HEJI1", 0)));
//		System.out.println(dfm.format(result.getDouble("HEJI1", 0)));
//		System.out.println(result.getDouble("HEJI2", 0));
		
		parm.addData("TOTAL_AGENT_AMT", dfm.format(result.getData(
						"NHI_PAY", 0)));// 基本医疗社保申请支付金额
		parm.addData("TOTAL_AMT", dfm.format(result.getData("HEJI1", 0)));// 发生金额
		parm.addData("TOTAL_NHI_AMT", dfm.format(result.getData("HEJI2", 0)));// 申报金额
		parm.addData("OWN_AMT", dfm.format(result.getData("OWN_AMT", 0)));// 全自费金额
		parm.addData("ADDPAY_AMT", dfm.format(result.getData("ADD_AMT", 0)));// 增负金额
		parm.addData("ALL_TIME", result.getData("COUNT", 0));// 总人次
		parm.addData("FLG_AGENT_AMT", dfm.format(result.getData(
				"NHI_COMMENT", 0)));// 医疗救助社保申请支付金额
		parm.addData("AGENT_AMT", dfm.format(result.getData("ARMYAI_AMT", 0)));// 补助金额
		if (ctz == 1) {// 城职
			parm.addData("HOSP_NHI_NO", hospital+(localFlg.equals("Y")?"@1":"@3"));// 医院编码@异地标记			
			parm.addData("ACCOUNT_PAY_AMT", dfm.format(result.getData(
					"ACCOUNT_PAY_AMT", 0)));// 个人帐户实际支付金额
			parm.setData("PIPELINE", "DataDown_yb");
			parm.setData("PLOT_TYPE", "A");
			parm.addData("PARM_COUNT", 12);
		}
		if (ctz == 2) {// 城居
			parm.addData("HOSP_NHI_NO", hospital);// 医院编码@异地标记
			//城乡大病补助金额
			parm.addData("ILLNESS_SUBSIDY_AMT", dfm.format(result.getData("ILLNESS_SUBSIDY_AMT", 0)));
			parm.setData("PIPELINE", "DataDown_czys");
			parm.setData("PLOT_TYPE", "K");
			parm.addData("PARM_COUNT", 12);
		}
//		System.out.println("safe:"+parm);
		TParm resultSafe = InsManager.getInstance().safe(parm);// 医保卡接口方法
//		System.out.println("对总账:::PIPELINE=DataDown_yb:::PLOT_TYPE=K:::出参===="
//				+ result+result.getErrCode());
		if(resultSafe.getErrCode()==-1){
			messageBox(resultSafe.getErrText());
		}else{
			String sql=
//				" UPDATE INS_ADM_CONFIRM A, INS_IBS B, SYS_CTZ C SET B.INSAMT_FLG='Y' "
//			+ " WHERE  A.CONFIRM_NO = B.CONFIRM_NO "
//			+ " AND A.CASE_NO = B.CASE_NO "
//			+ " AND B.REGION_CODE = '"
//			+ regionCode
//			+ "' AND A.IN_STATUS IN( '2','4','7') "
//			+ " AND A.LOCAL_FLG='"+localFlg//zhangs 20160908 add localFlg
//			+ "' AND B.UPLOAD_DATE BETWEEN TO_DATE ('"
//			+ startDate
//			+ "', 'YYYYMMDDHH24MISS') "
//			+ " AND TO_DATE ('"
//			+ endDate
//			+ "', 'YYYYMMDDHH24MISS') "
//
//			+ " AND A.HIS_CTZ_CODE = C.CTZ_CODE " + " AND C.NHI_CTZ_FLG = 'Y' AND B.STATUS='S'";
//	if (ctz == 1) {
//		sql = sql + " AND C.INS_CROWD_TYPE= '1'";// 城职
//	}
//	if (ctz == 2) {
//		sql = sql + " AND C.INS_CROWD_TYPE= '2'";// 城居
//	}
				" UPDATE  INS_IBS B SET B.INSAMT_FLG='Y' "+
				" WHERE  EXISTS ( SELECT 1 FROM INS_ADM_CONFIRM A,SYS_CTZ C "+
				" WHERE A.CONFIRM_NO = B.CONFIRM_NO "+ 
				" AND A.CASE_NO = B.CASE_NO "+ 
				" AND B.REGION_CODE = '"+regionCode+"' "+ 
				" AND A.IN_STATUS IN( '2','4') "+ 
				" AND A.LOCAL_FLG='"+localFlg+"' "+ 
				" AND B.UPLOAD_DATE BETWEEN TO_DATE ('"+startDate+"', 'YYYYMMDDHH24MISS') AND TO_DATE ('"+endDate+"', 'YYYYMMDDHH24MISS') "+  
				" AND A.HIS_CTZ_CODE = C.CTZ_CODE AND C.NHI_CTZ_FLG = 'Y' ";
		if (ctz == 1) {
			sql = sql + " AND C.INS_CROWD_TYPE= '1'";// 城职
		}
		if (ctz == 2) {
			sql = sql + " AND C.INS_CROWD_TYPE= '2'";// 城居
		}
		sql = sql +") ";
	        TParm resultUpdate = new TParm(TJDODBTool.getInstance().update(sql));
	        if(resultUpdate.getErrCode()==-1){
	        	messageBox(resultUpdate.getErrText());
	        }
			messageBox(resultSafe.getValue("PROGRAM_MESSAGE"));
		}
		
		
	}

	/**
	 * 对总账查询
	 * 
	 * @param regionCode
	 * @param startDate
	 * @param endDate
	 * @param ctz
	 * @return
	 */
	public TParm queryCheckAll(String regionCode, String startDate,
			String endDate, int ctz,String localFlg) {//zhangs 20160908 add localFlg
		String sql = "SELECT SUM (  B.PHA_AMT + B.EXM_AMT + B.TREAT_AMT + B.OP_AMT + B.BED_AMT + B.MATERIAL_AMT"
				+ " + B.OTHER_AMT + B.BLOODALL_AMT + B.BLOOD_AMT ) HEJI1, "
				+ " SUM (  B.PHA_NHI_AMT + B.EXM_NHI_AMT + B.TREAT_NHI_AMT + B.OP_NHI_AMT + B.BED_NHI_AMT"
				+ " + B.MATERIAL_NHI_AMT + B.OTHER_NHI_AMT + B.BLOODALL_NHI_AMT + B.BLOOD_NHI_AMT ) HEJI2, "
				+ " SUM (B.OWN_AMT) AS OWN_AMT, SUM (B.ADD_AMT) AS ADD_AMT, COUNT (A.CONFIRM_NO) AS COUNT, "
				+ " SUM (B.ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT, SUM (B.NHI_PAY) AS NHI_PAY, " 
				+ " SUM (B.NHI_COMMENT) AS NHI_COMMENT, SUM (B.ARMYAI_AMT) AS ARMYAI_AMT, " 
				+ " SUM (B.ILLNESS_SUBSIDY_AMT) AS ILLNESS_SUBSIDY_AMT "
				+ " FROM INS_ADM_CONFIRM A, INS_IBS B, SYS_CTZ C "
				+ " WHERE  A.CONFIRM_NO = B.CONFIRM_NO "
				+ " AND A.CASE_NO = B.CASE_NO "
				+ " AND B.REGION_CODE = '"
				+ regionCode
				+ "' AND A.IN_STATUS IN( '2','4') "
				+ " AND A.LOCAL_FLG='"+localFlg//zhangs 20160908 add localFlg
				+ "' AND B.UPLOAD_DATE BETWEEN TO_DATE ('"
				+ startDate
				+ "', 'YYYYMMDDHH24MISS') "
				+ " AND TO_DATE ('"
				+ endDate
				+ "', 'YYYYMMDDHH24MISS') "

				+ " AND A.HIS_CTZ_CODE = C.CTZ_CODE " + " AND C.NHI_CTZ_FLG = 'Y' ";
		if (ctz == 1) {
			sql = sql + " AND C.INS_CROWD_TYPE= '1'";// 城职
		}
		if (ctz == 2) {
			sql = sql + " AND C.INS_CROWD_TYPE= '2'";// 城居
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * 对明细账
	 */
	public void onCheckDetailAccnt() {
		String startDate = "";
		String endDate = "";
		int ctz = getValueInt("CTZ_CODE");
		String localFlg = getValueString("LOCAL_FLG");//zhangs 20160908 add
		if (!"".equals(this.getValueString("START_DATE"))
				&& !"".equals(this.getValueString("END_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7)
					+ startDate.substring(8, 10) + "000000";
			endDate = endDate.substring(0, 4) + endDate.substring(5, 7)
					+ endDate.substring(8, 10) + "235959";
		}
		//onQuery();// 查询table1
		TParm parm = null;
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		String hospital = regionParm.getData("NHI_NO", 0).toString();// 获取HOSP_NHI_NO
		TTable table2 = (TTable) this.getComponent("TABLE2");// TABLE2
		if (ctz == 1) {
			parm = new TParm();
			// 传参
			parm.setData("PIPELINE", "DataDown_yb");
			parm.setData("PLOT_TYPE", "B");
			parm.addData("HOSP_NHI_NO", hospital+(localFlg.equals("Y")?"@1":"@3"));// 医院编码@异地标记
			table2
					.setHeader("就诊序号,100;资格确认书号,100;期号,100;姓名,100;人员类别,100;支付类别,100;"
							+ "发生金额,100,double,#########0.00;申报金额,100,double,#########0.00;"
							+ "全自费金额,100,double,#########0.00;增负金额,100,double,#########0.00;"
							+ "个人账户支付金额,100,double,#########0.00");
			table2
					.setParmMap("ADM_SEQ;CONFIRM_NO;ISSUE;NAME;CTZ1_CODE;PAY_TYPE;"
							+ "TOTAL_AMT;TOTAL_NHI_AMT;"
							+ "OWN_AMT;ADDPAY_AMT;" + "ACCOUNT_PAY_AMT");
		}
		if (ctz == 2) {
			parm = new TParm();
			// 传参
			parm.setData("PIPELINE", "DataDown_czyd");
			parm.setData("PLOT_TYPE", "J");
			parm.addData("HOSP_NHI_NO", hospital);// 医院编码
			table2
					.setHeader("就诊序号,100;资格确认书号,100;期号,100;姓名,100;人员类别,100;支付类别,100;"
							+ "发生金额,100,double,#########0.00;申报金额,100,double,#########0.00;"
							+ "全自费金额,100,double,#########0.00;增负金额,100,double,#########0.00");
			table2
					.setParmMap("ADM_SEQ;CONFIRM_NO;ISSUE;NAME;CTZ1_CODE;PAY_TYPE;"
							+ "TOTAL_AMT;TOTAL_NHI_AMT;" + "OWN_AMT;ADDPAY_AMT");
		}
//		parm.addData("HOSP_NHI_NO", hospital+(localFlg.equals("Y")?"@1":"@3"));// 医院编码
		parm.addData("START_DATE", startDate.substring(0,8));// 开始时间
		parm.addData("END_DATE", endDate.substring(0,8));// 结束时间
		parm.addData("PARM_COUNT", 3);
		TParm result = InsManager.getInstance().safe(parm, "");// 医保卡接口方法(复数)
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}else{
			this.messageBox("明细下载成功");
		}
		table2.setParmValue(result);
		//this.callFunction("UI|TABLE2|setParmValue", result);
		countDetail();
	}

	/**
	 * 计算明细账差别
	 */
	public void countDetail() {
		TTable table1 = (TTable) this.getComponent("TABLE1");// TABLE1
		TTable table2 = (TTable) this.getComponent("TABLE2");// TABLE2
		if (table1.getParmValue() == null || table2.getParmValue() == null) {
			messageBox("对账数据不能为空");
			return;
		}
		// ADM_SEQ;CONFIRM_NO;YEAR_MON;PAT_NAME;CTZ_DESC;CATEGORY_CHN_DESC;HEJI1;HEJI2;OWN_AMT;ADD_AMT
		TParm tableParm1 = table1.getParmValue();
		TParm tableParm2 = table2.getParmValue();
		TParm parm = new TParm();
		for (int i = 0; i < tableParm1.getCount(); i++) {
			String confirmNoLocal = tableParm1.getData("CONFIRM_NO", i)
					.toString();
			boolean canfind = false;
			for (int j = 0; j < tableParm2.getCount(); j++) {
				String confirmNoCenter = tableParm2.getData("CONFIRM_NO", j)
						.toString();
				if (!confirmNoLocal.equals(confirmNoCenter))
					continue;
				canfind = true;
				// 本地金额
				double totAmtLocal = tableParm1.getDouble("HEJI1", i);// 发生金额
				double nhiAmtLocal = tableParm1.getDouble("HEJI2", i);// 申报金额
				double ownAmtLocal = tableParm1.getDouble("OWN_AMT", i);// 全自费金额
				double addAmtLocal = tableParm1.getDouble("ADD_AMT", i);// 增付金额
				// 中心端金额
				double totAmtCenter = tableParm2.getDouble("TOTAL_AMT", j);// 发生金额
				double nhiAmtCenter = tableParm2.getDouble("TOTAL_NHI_AMT", j);// 申报金额
				double ownAmtCenter = tableParm2.getDouble("OWN_AMT", j);// 全自费金额
				double addAmtCenter = tableParm2.getDouble("ADDPAY_AMT", j);// 增付金额
				if (totAmtLocal != totAmtCenter || nhiAmtLocal != nhiAmtCenter
						|| ownAmtLocal != ownAmtCenter
						|| addAmtLocal != addAmtCenter) {
					parm.addData("STATUS_ONE", "Y");
					parm.addData("STATUS_TWO", "N");
					parm.addData("STATUS_THREE", "N");
					parm.addData("ADM_SEQ", tableParm1.getData("ADM_SEQ", i));
					parm.addData("CONFIRN_NO", tableParm1.getData("CONFIRM_NO",
							i));
					parm.addData("YEAR_MON", tableParm1.getData("YEAR_MON", i));
					parm.addData("NAME", tableParm1.getData("PAT_NAME", i));
					parm.addData("TOT_AMT_LOCAL", tableParm1
							.getData("HEJI1", i));
					parm.addData("TOT_AMT_CENTER", tableParm2.getData(
							"TOTAL_AMT", j));
					parm.addData("NHI_AMT_LOCAL", tableParm1
							.getData("HEJI2", i));
					parm.addData("NHI_AMT_CENTER", tableParm2.getData(
							"TOTAL_NHI_AMT", j));
					parm.addData("OWN_AMT_LOCAL", tableParm1.getData("OWN_AMT",
							i));
					parm.addData("OWN_AMT_CENTER", tableParm2.getData(
							"OWN_AMT", j));
					parm.addData("ADD_AMT_LOCAL", tableParm1.getData("ADD_AMT",
							i));
					parm.addData("ADD_AMT_CENTER", tableParm2.getData(
							"ADDPAY_AMT", j));
				}
			}
			if (!canfind) {
				parm.addData("STATUS_ONE", "N");
				parm.addData("STATUS_TWO", "Y");
				parm.addData("STATUS_THREE", "N");
				parm.addData("ADM_SEQ", tableParm1.getData("ADM_SEQ", i));
				parm.addData("CONFIRN_NO", tableParm1.getData("CONFIRM_NO", i));
				parm.addData("YEAR_MON", tableParm1.getData("YEAR_MON", i));
				parm.addData("NAME", tableParm1.getData("PAT_NAME", i));
				parm.addData("TOT_AMT_LOCAL", tableParm1.getData("HEJI1", i));
				parm.addData("TOT_AMT_CENTER", 0);
				parm.addData("NHI_AMT_LOCAL", tableParm1.getData("HEJI2", i));
				parm.addData("NHI_AMT_CENTER", 0);
				parm.addData("OWN_AMT_LOCAL", tableParm1.getData("OWN_AMT", i));
				parm.addData("OWN_AMT_CENTER", 0);
				parm.addData("ADD_AMT_LOCAL", tableParm1.getData("ADD_AMT", i));
				parm.addData("ADD_AMT_CENTER", 0);
			}
		}
		for (int i = 0; i < tableParm2.getCount(); i++) {
			String confirmNoCenter = tableParm2.getData("CONFIRM_NO", i)
					.toString();
			boolean canfind = false;
			for (int j = 0; j < tableParm1.getCount(); j++) {
				String confirmNoLocal = tableParm1.getData("CONFIRM_NO", i)
						.toString();
				if (!confirmNoLocal.equals(confirmNoCenter))
					continue;
				canfind = true;
			}
			if (!canfind) {
				parm.addData("STATUS_ONE", "N");
				parm.addData("STATUS_TWO", "N");
				parm.addData("STATUS_THREE", "Y");
				parm.addData("ADM_SEQ", tableParm2.getData("ADM_SEQ", i));
				parm.addData("CONFIRN_NO", tableParm2.getData("CONFIRN_NO", i));
				parm.addData("YEAR_MON", tableParm2.getData("ISSUE", i));
				parm.addData("NAME", tableParm2.getData("NAME", i));
				parm.addData("TOT_AMT_LOCAL", 0);
				parm.addData("TOT_AMT_CENTER", tableParm2.getData("TOTAL_AMT",
						i));
				parm.addData("NHI_AMT_LOCAL", 0);
				parm.addData("NHI_AMT_CENTER", tableParm2.getData(
						"TOTAL_NHI_AMT", i));
				parm.addData("OWN_AMT_LOCAL", 0);
				parm
						.addData("OWN_AMT_CENTER", tableParm2.getData(
								"OWN_AMT", i));
				parm.addData("ADD_AMT_LOCAL", 0);
				parm.addData("ADD_AMT_CENTER", tableParm2.getData("ADDPAY_AMT",
						i));
			}
		}
		if (parm.getCount("ADM_SEQ") <= 0) {
			messageBox("对明细帐成功");
			return;
		}
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INSOdiCheckDetail.x", parm);
	}

	/**
	 * 清空
	 */
	public void onclear() {
		clearValue("CTZ_CODE;START_DATE;END_DATE");
	}
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = table.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = table.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * vectory转成param
	 */
	private void cloneVectoryParamOne(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table2.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}

	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
	 * @return Vector
	 */
	private Vector getVectorOne(TParm parm, String group, String names, int size) {
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
	private int tranParmColIndexOne(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}

	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListenerOne(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumnOne) {
					ascendingOne = !ascendingOne;
				} else {
					ascendingOne = true;
					sortColumnOne = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = table2.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVectorOne(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = table2.getParmMap(sortColumnOne);
				// 转成parm中的列
				int col = tranParmColIndexOne(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compareOne.setDes(ascendingOne);
				compareOne.setCol(col);
				java.util.Collections.sort(vct, compareOne);
				// 将排序后的vector转成parm;
				cloneVectoryParamOne(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		table.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}

	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
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
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}

}
