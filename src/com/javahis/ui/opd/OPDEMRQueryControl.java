/**
 * 
 */
package com.javahis.ui.opd;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Calendar;

import jdo.ekt.EKTIO;
import jdo.odo.ODO;
import jdo.odo.OpdRxSheetTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSOperatorTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
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
 * @author wu 2012-7-9上午11:18:02
 * @version 1.0
 */
public class OPDEMRQueryControl extends TControl {
	Pat pat;
	String MR_NO = ""; // 病案号

	public void onPatQuery() {
		TParm sendParm = new TParm();
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\adm\\ADMPatQuery.x", sendParm);
		if (reParm == null)
			return;
		this.setValue("MR_NO", reParm.getValue("MR_NO"));
		this.onQueryMrNo();
		// this.onMrno();
	}

	/**
	 * 查询病案号 ===zhangp 20120326
	 */
	public void onQueryMrNo() {
		String mrNo = getValueString("MR_NO");
		Pat pat = Pat.onQueryByMrNo(mrNo);
		
		//add by huangtt 20160927 EMPI患者查重提示  start  
		String srcMrNo = PatTool.getInstance().checkMrno(mrNo.trim());
		if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {
	          this.messageBox("病案号" + srcMrNo + " 已合并至 " + "" + pat.getMrNo());
	    }
		//add by huangtt 20160927 EMPI患者查重提示  end
		
		mrNo = pat.getMrNo();
		setValue("PAT_NAME", pat.getName());
		setValue("MR_NO", pat.getMrNo());
		onQuery();
		// TParm parm = new TParm();

		// REGION_CHN_ABN;ADM_DATE;MR_NO;PAT_NAME;CLINICTYPE_DESC;DEPT_ABS_DESC;USER_NAME;QUE_NO;CTZ_DESC;AR_AMT
		// parm.addData("REGION_CHN_ABN", pat.get);//=============pangben modify
		// 20110408,fuxin modify 20120306
		// parm.addData("ADM_DATE", admDateStr);
		// parm.addData("MR_NO", mrNo);
		// parm.addData("PAT_NAME", patName);
		// parm.addData("CLINICTYPE_DESC", clinicDesc);
		// parm.addData("DEPT_ABS_DESC", deptDesc);
		// parm.addData("USER_NAME", useName);
		// parm.addData("QUE_NO", queNo);
		// parm.addData("CTZ_DESC", ctzDesc);
		// parm.addData("AR_AMT", df.format(ar_amt));
	}

	public void onInit() {
		super.onInit();
		//初始化个人权限  == zhanglei 20171201 add
		onInitPopemed();
		callFunction("UI|Table|addEventListener", "Table->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		initPage();
		// ========pangben modify 20110421 start 权限添加
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ===========pangben modify 20110421 stop

	}
	/** 
	* 初始化个人权限   == zhanglei 20171201 add
	*/ 
	public void onInitPopemed(){ 
		TParm parm = SYSOperatorTool.getUserPopedem(Operator.getID(), getUITag()); 
		for (int i = 0; i < parm.getCount(); i++) { 
			this.setPopedem(parm.getValue("AUTH_CODE", i), true); 
		} 
	}

	/**
	 * 行单击事件
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		if (row < 0)
			return;
		setPageValue();
	}

	/**
	 * 初始化界面
	 */
	public void initPage() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(SystemTool.getInstance().getDate());
		calendar.add(Calendar.MONTH, -3);
		Timestamp threeMonthAgo = new Timestamp(calendar.getTimeInMillis());
		setValue("S_DATE", threeMonthAgo);
		setValue("E_DATE", SystemTool.getInstance().getDate());
		setValue("CLINICTYPE_CODE", "");
		setValue("DEPT_CODE", "");
		// 默认区域
		setValue("REGION_CODE", Operator.getRegion());
		setValue("DR_CODE", "");
		setValue("MR_NO", "");
		setValue("PAT_NAME", "");
		this.callFunction("UI|Table|removeRowAll");
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		print();
	}

	/**
	 * 调用报表打印预览界面
	 */
	private void print() {
		TTable table = (TTable) this.getComponent("Table");
		int row = table.getRowCount();
		if (row < 1) {
			this.messageBox("先查询数据!");
			return;
		}
		String startTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd");
		String sysDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyy/MM/dd hh:mm:ss");
		TParm printData = this.getPrintDate(startTime, endTime);
		String sDate = StringTool.getString(
				TypeTool.getTimestamp(getValue("S_DATE")), "yyyy/MM/dd")
				+ " " + this.getValue("S_TIME");
		String eDate = StringTool.getString(
				TypeTool.getTimestamp(getValue("E_DATE")), "yyyy/MM/dd")
				+ " " + this.getValue("E_TIME");
		TParm parm = new TParm();
		// ========pangben modify 20110329 start,fuxin modify 20120306
		String region = ((TTable) this.getComponent("Table")).getParmValue()
				.getRow(0).getValue("REGION_CHN_ABN");
		parm.setData("TITLE", "TEXT", (this.getValue("REGION_CODE") != null
				&& !this.getValue("REGION_CODE").equals("") ? region : "所有医院")
				+ "挂号历史信息报表");
		// ========pangben modify 20110329 stop
		parm.setData("S_DATE", "TEXT", sDate);
		parm.setData("E_DATE", "TEXT", eDate);
		parm.setData("OPT_USER", Operator.getName());
		parm.setData("OPT_DATE", "TEXT", sysDate);
		parm.setData("historyQuerytable", printData.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\REG\\REGHistoryQuery.jhw",
				parm);

	}

	/**
	 * 整理打印数据
	 * 
	 * @param startTime
	 *            String
	 * @param endTime
	 *            String
	 * @return TParm
	 */
	private TParm getPrintDate(String startTime, String endTime) {
		DecimalFormat df = new DecimalFormat("##########0.00");
		TParm selParm = new TParm();
		String clinicTypeCodeWhere = "";
		if (getValue("CLINICTYPE_CODE").toString().length() != 0)
			clinicTypeCodeWhere = " AND A.CLINICTYPE_CODE = '"
					+ getValue("CLINICTYPE_CODE") + "'  ";
		String deptCodeWhere = "";
		if (getValue("DEPT_CODE").toString().length() != 0)
			deptCodeWhere = " AND A.REALDEPT_CODE = '" + getValue("DEPT_CODE")
					+ "'  ";
		String drCodeWhere = "";
		if (getValue("DR_CODE").toString().length() != 0)
			drCodeWhere = " AND A.REALDR_CODE = '" + getValue("DR_CODE")
					+ "'  ";
		String mrNoWhere = "";
		if (getValue("MR_NO").toString().trim().length() != 0){
//			mrNoWhere = " AND A.MR_NO = '" + getValue("MR_NO") + "'  ";
			
			String mrNos = PatTool.getInstance().getMrRegMrNos(getValue("MR_NO").toString());			
			mrNoWhere = " AND A.MR_NO IN (" + mrNos+ ")  ";
			
			
		}
		//zhanglei 增加权限 20171010
		String a = "";
		boolean vvipFlg = this.getPopedem("SPECIAL_FLG");
		if(!vvipFlg){
        	a = " AND G.SPECIAL_FLG = 'N' ";
        }
		// ================pangben modify 20110408 start
		String reqion = "";
		if (this.getValueString("REGION_CODE").length() != 0)
			reqion = " AND A.REGION_CODE= '" + this.getValue("REGION_CODE")
					+ "' ";
		// ================pangben modify 20110408 stop,fuxin modify 20120306
		String sql = " SELECT H.REGION_CHN_ABN,A.ADM_TYPE, A.ADM_DATE, A.CASE_NO, A.MR_NO,C.PAT_NAME, A.CLINICTYPE_CODE, A.REALDEPT_CODE, A.REALDR_CODE,"
				+ "        A.QUE_NO, A.CTZ1_CODE, B.AR_AMT, D.CLINICTYPE_DESC,"
				+ "        E.USER_NAME, F.DEPT_ABS_DESC,G.CTZ_DESC,A.REGION_CODE"
				+ "   FROM REG_PATADM A,BIL_REG_RECP B,SYS_PATINFO C,REG_CLINICTYPE D,"
				+ "        SYS_OPERATOR E,SYS_DEPT F,SYS_CTZ G,SYS_REGION H  "
				+ "  WHERE A.ADM_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "000000"
				+ "', 'yyyyMMddHH24miss') "
				+ "                       AND TO_DATE ('"
				+ endTime
				+ "235959"
				+ "', 'yyyyMMddHH24miss') "
				//===zhangp 20120714 start
				+ "    AND A.SEE_DR_FLG IN ('Y','T') "
				//===zhangp 20120714 end
				//===zhanglei 20171205 start VVIP医生可以查询自己的患者
//				+ "    OR A.REALDR_CODE='" +  Operator.getID() + "'"
				+ a
				//===zhanglei 20171205 end
				+ "    AND A.MR_NO = C.MR_NO "
				+ "    AND A.CASE_NO = B.CASE_NO(+) "
				+ "    AND A.REALDEPT_CODE = F.DEPT_CODE "
				+ "    AND A.REGION_CODE = H.REGION_CODE(+) "
				+ // =========pangben modify 20110408
				"    AND A.CLINICTYPE_CODE = D.CLINICTYPE_CODE "
				+ clinicTypeCodeWhere
				+ deptCodeWhere
				+ drCodeWhere
				+ mrNoWhere
				+ reqion
				+ // ======pangben modify 20110325
				"    AND A.REALDR_CODE = E.USER_ID "
				+ "    AND A.CTZ1_CODE = G.CTZ_CODE ORDER BY H.REGION_CHN_ABN,A.ADM_DATE DESC"; // =====fuxin
																								// modify
																								// 20120306
//		System.out.println("sql!!!!!!!!!!!!!!!::::::" + sql);
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selParm.getCount("MR_NO") < 1) {
			this.messageBox("查无数据");
			this.initPage();
			return selParm;
		}
		// ==========pangben modify 20110425 start 累计
		double sumAramt = 0.00;
		// ==========pangben modify 20110425 stop
		TParm endDate = new TParm();
		int count = selParm.getCount("MR_NO");
		// ADM_DATE;MR_NO;CLINICTYPE_DESC;DEPT_ABS_DESC;USER_NAME;QUE_NO;CTZ_DESC;AR_AMT
		for (int i = 0; i < count; i++) {
			// =============pangben modify 20110408 start 在打印的报表中添加区域
			String reqionTemp = selParm.getValue("REGION_CHN_ABN", i); // ====fuxin
																		// modify
																		// 20120306
			// =============pangben modify 20110408 stop
			Timestamp admDate = selParm.getTimestamp("ADM_DATE", i);
			String admDateStr = StringTool.getString(admDate, "yyyy/MM/dd");
			String mrNo = selParm.getValue("MR_NO", i);
			String patName = selParm.getValue("PAT_NAME", i);
			String clinicDesc = selParm.getValue("CLINICTYPE_DESC", i);
			String deptDesc = selParm.getValue("DEPT_ABS_DESC", i);
			String useName = selParm.getValue("USER_NAME", i);
			int queNo = selParm.getInt("QUE_NO", i);
			String ctzDesc = selParm.getValue("CTZ_DESC", i);
			String drCode = selParm.getValue("REALDR_CODE", i);
			double ar_amt = selParm.getDouble("AR_AMT", i);
			// ==========pangben modify 20110425 start
			sumAramt += StringTool.round(ar_amt, 2);
			// ==========pangben modify 20110425 stop
			endDate.addData("REGION_CHN_ABN", reqionTemp);// =============pangben
															// modify
															// 20110408,fuxin
															// modify 20120306
			endDate.addData("ADM_DATE", admDateStr);
			endDate.addData("MR_NO", mrNo);
			endDate.addData("PAT_NAME", patName);
			endDate.addData("CLINICTYPE_DESC", clinicDesc);
			endDate.addData("DEPT_ABS_DESC", deptDesc);
			endDate.addData("USER_NAME", useName);
			endDate.addData("QUE_NO", queNo);
			endDate.addData("CTZ_DESC", ctzDesc);
			endDate.addData("AR_AMT", df.format(ar_amt));
			endDate.addData("DR_CODE", drCode);
		}
		endDate.setCount(count);
		// =============pangben modify 20110408 start
		endDate.addData("SYSTEM", "COLUMNS", "REGION_CHN_ABN"); // fuxin modify
																// 20120306
		// =============pangben modify 20110408 stop
		endDate.addData("SYSTEM", "COLUMNS", "ADM_DATE");
		endDate.addData("SYSTEM", "COLUMNS", "MR_NO");
		endDate.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		endDate.addData("SYSTEM", "COLUMNS", "CLINICTYPE_DESC");
		endDate.addData("SYSTEM", "COLUMNS", "DEPT_ABS_DESC");
		endDate.addData("SYSTEM", "COLUMNS", "USER_NAME");
		endDate.addData("SYSTEM", "COLUMNS", "QUE_NO");
		endDate.addData("SYSTEM", "COLUMNS", "CTZ_DESC");
		endDate.addData("SYSTEM", "COLUMNS", "AR_AMT");
		endDate.addData("SYSTEM", "COLUMNS", "DR_CODE");
		// ==========pangben modify 20110425 start
		selParm.setData("REGION_CHN_ABN", count, "总计:");
		selParm.setData("ADM_DATE", count, "");
		selParm.setData("ADM_TYPE", count, "");
		selParm.setData("CASE_NO", count, "");
		selParm.setData("MR_NO", count, "");
		selParm.setData("PAT_NAME", count, "");
		selParm.setData("CLINICTYPE_DESC", count, "");
		selParm.setData("DEPT_ABS_DESC", count, "");
		selParm.setData("USER_NAME", count, "");
		selParm.setData("QUE_NO", count, "");
		selParm.setData("CTZ_DESC", count, "");
		selParm.setData("AR_AMT", count, df.format(sumAramt));
		selParm.setData("DR_CODE", count, "");
		// ==========pangben modify 20110425 start
		this.callFunction("UI|Table|setParmValue", selParm);
		return endDate;
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		String startTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd");
		TParm printData = this.getPrintDate(startTime, endTime);
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {

		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|Table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "挂号历史数据报表");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		initPage();
		TTable table = (TTable) this.getComponent("Table");
		table.removeRowAll();

	}

	/**
	 * 点选grid数据给界面翻值
	 */
	public void setPageValue() {
		TTable table = (TTable) this.getComponent("Table");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("请点选行数据!");
		}
		TParm tableParm = table.getParmValue();
		String clinicTypeCode = tableParm.getValue("CLINICTYPE_CODE", selRow);
		String deptCode = tableParm.getValue("REALDEPT_CODE", selRow);
		String drCode = tableParm.getValue("REALDR_CODE", selRow);
		String mrNo = tableParm.getValue("MR_NO", selRow);
		String patName = tableParm.getValue("PAT_NAME", selRow);
		String region_code = tableParm.getValue("REGION_CODE", selRow);// ==pangben
																		// modify
																		// 20110413
		setValue("CLINICTYPE_CODE", clinicTypeCode);
		setValue("DEPT_CODE", deptCode);
		setValue("DR_CODE", drCode);
		setValue("MR_NO", mrNo);
		setValue("PAT_NAME", patName);
		setValue("REGION_CODE", region_code);// ==pangben modify 20110413
	}

	/**
	 * 读医疗卡
	 */
	public void onReadEKT() {
		// 读取医疗卡
		TParm parmEKT = EKTIO.getInstance().TXreadEKT();
		if (null == parmEKT || parmEKT.getErrCode() < 0
				|| parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox(parmEKT.getErrText());
			parmEKT = null;
			return;
		}
		String mrNo = parmEKT.getValue("MR_NO");
		
		
		Pat pat = Pat.onQueryByMrNo(mrNo);
		//add by huangtt 20160927 EMPI患者查重提示  start
		String srcMrNo = PatTool.getInstance().checkMrno(mrNo.trim());
		if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {
	          this.messageBox("病案号" + srcMrNo + " 已合并至 " + "" + pat.getMrNo());
	    }
		//add by huangtt 20160927 EMPI患者查重提示  end
		this.setValue("MR_NO", pat.getMrNo());	
		setValue("PAT_NAME", pat.getName());
		onQuery();
	}

	// /**
	// * 调用留观病历
	// */
	// public void onErdSheet() {
	// TTable table = (TTable)this.getComponent("Table");
	// int selRow = table.getSelectedRow();
	// if (selRow < 0) {
	// this.messageBox("请点选行数据!");
	// return;
	// }
	// TParm tableParm = table.getParmValue();
	// TParm parm = new TParm();
	// if ("O".equalsIgnoreCase(tableParm.getValue("ADM_TYPE", selRow))) {
	// parm.setData("SYSTEM_TYPE", "ODO");
	// parm.setData("ADM_TYPE", "O");
	// } else {
	// parm.setData("SYSTEM_TYPE", "EMG");
	// parm.setData("ADM_TYPE", "E");
	// }
	// parm.setData("CASE_NO", tableParm.getValue("CASE_NO", selRow));
	// parm.setData("PAT_NAME", tableParm.getValue("PAT_NAME", selRow));
	// parm.setData("MR_NO", tableParm.getValue("MR_NO", selRow));
	// parm.setData("IPD_NO", "");
	// parm.setData("ADM_DATE", Timestamp.valueOf(tableParm.getValue("ADM_DATE",
	// selRow)));
	// parm.setData("DEPT_CODE", tableParm.getValue("DEPT_ABS_DESC", selRow));
	// // parm.setData("STYLETYPE","1");
	// parm.setData("RULETYPE", "2");
	// parm.setData("EMR_DATA_LIST", new TParm());
	// this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
	// }

	/**
	 * 打印病历
	 * 
	 * @return Object
	 */
	public void onErdSheet() {
		TParm parm = new TParm();
		TTable table = (TTable) this.getComponent("Table");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("请点选行数据!");
			return;
		}
		TParm tableParm = table.getParmValue();
		parm.setData("CASE_NO", tableParm.getValue("CASE_NO", selRow));
		parm.setData("MR_NO", tableParm.getValue("MR_NO", selRow));
		parm.setData("MR", "TEXT", "病案号：" + tableParm.getValue("MR_NO", selRow));
		// if (isEng) {
		// parm
		// .setData("HOSP_NAME", "TEXT", Operator
		// .getHospitalENGFullName());
		// } else {
		parm.setData("HOSP_NAME", "TEXT", Operator.getHospitalCHNFullName());
		// }
		parm.setData(
				"DR_NAME",
				"TEXT",
				"医师签字:"
						+ OpdRxSheetTool.getInstance().GetRealRegDr(
								tableParm.getValue("CASE_NO", selRow)));
		parm.setData("REALDEPT_CODE",
				tableParm.getValue("REALDEPT_CODE", selRow));
		Object obj = new Object();
		if ("O".equals(tableParm.getValue("ADM_TYPE", selRow))) {
			obj = this.openPrintDialog(
					"%ROOT%\\config\\prt\\OPD\\OPDCaseSheet1010.jhw", parm,
					false);
			// 加入EMR保存 beign
			// this.saveEMR(obj, "门诊病历记录", "EMR020001", "EMR02000106");
			// 加入EMR保存 end
		} else if ("E".equals(tableParm.getValue("ADM_TYPE", selRow))) {
			 TParm sparm=this.getParmSeeDrTime(tableParm.getValue("MR_NO", selRow), tableParm.getValue("CASE_NO", selRow));
				if(sparm.getCount()>0){
					parm.setData("DATE", sparm.getValue("SEEN_DR_TIME",0).substring(0,16).replace("-","/"));
					parm.setData("ARRIVE_DATE", sparm.getValue("ARRIVE_DATE",0).substring(0,16).replace("-","/"));
					parm.setData("LEVEL_DESC", sparm.getValue("LEVEL_DESC",0));
				} 
			obj = this.openPrintDialog("%ROOT%\\config\\prt\\OPD\\EMG.jhw",
					parm, false);

		}

	}
	
	public TParm  getParmSeeDrTime(String mrno,String caseno){
		
		 String sql="SELECT A.SEEN_DR_TIME,A.ARRIVE_DATE,B.LEVEL_DESC FROM REG_PATADM A,REG_ERD_LEVEL B WHERE " +
	 		        "A.MR_NO='"+mrno+"' AND A.CASE_NO='"+caseno+"'AND  A.ADM_TYPE='E'" +
	 		        " AND A.ERD_LEVEL=B.LEVEL_CODE";
		 //System.out.println("sql::"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	/**
	 * 处方签打印
	 * ======zhangp 20121210
	 */
	public void onCaseSheet(){
		TTable table = (TTable) this.getComponent("Table");
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("请点选行数据!");
			return;
		}
		TParm tableParm = table.getParmValue();
		String caseNo = tableParm.getValue("CASE_NO", selRow);
		String mrNo = tableParm.getValue("MR_NO", selRow);
		String deptCode = tableParm.getValue("REALDEPT_CODE", selRow);
		String drCode = tableParm.getValue("DR_CODE", selRow);
		String admType = tableParm.getValue("ADM_TYPE", selRow);
		Timestamp admDate = tableParm.getTimestamp("ADM_DATE", selRow);
		ODO odo = new ODO(caseNo, mrNo, deptCode, drCode, admType);
		odo.onQuery();
		pat = Pat.onQueryByMrNo(mrNo);
		TParm inParm = new TParm();
		if (odo == null || pat == null)
			return;
		inParm.setData("MR_NO", odo.getMrNo());
		inParm.setData("CASE_NO", odo.getCaseNo());
		inParm.setData("DEPT_CODE", deptCode);
		if ("en".equals(this.getLanguage())) // 判断是否是英文界面
			inParm.setData("PAT_NAME", pat.getName1());
		else
			inParm.setData("PAT_NAME", pat.getName());
		inParm.setData("OPD_ORDER", odo.getOpdOrder());
		inParm.setData("ADM_DATE", admDate);
		inParm.setData("ODO", odo);
		int[] mainDiag = new int[1];
		if (odo.getDiagrec().haveMainDiag(mainDiag)) {
			String icdCode = odo.getDiagrec().getItemString(mainDiag[0],
					"ICD_CODE");
			inParm.setData("ICD_CODE", icdCode);
			inParm.setData("ICD_DESC", odo.getDiagrec().getIcdDesc(icdCode,
					this.getLanguage()));
		}
		this.openDialog("%ROOT%\\config\\opd\\ODOCaseSheet.x", inParm, false);
	}

}
