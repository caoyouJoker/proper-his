package com.javahis.ui.adm;


import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.sysfee.sysOdrPackDObserver;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TCheckBox;

/**
 * <p>
 * Title: 修改身份
 * </p>
 * 
 * <p>
 * Description: 修改身份
 * 
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) BlueCore 
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author caowl 2012.07.04
 * @version 1.0
 */
public class ADMCTZModifyControl extends TControl {

	
	private static final String actionName = "action.ibs.IBSAction";

	String caseNo;
	/**
	 * 初始化
	 * */
	public void onInit() {
		TParm initParm = new TParm();
		Object obj = this.getParameter();
		if(obj == null || obj.equals("")){
			return ;
		}
		if (obj != null || obj != "") {	
			
			initParm = (TParm) obj;
			caseNo = initParm.getData("CASE_NO").toString();					
			TParm queryParm = queryByCaseNo(caseNo);
			// 初始化
			this.setValue("MR_NO", queryParm.getData("MR_NO", 0));
			this.setValue("PAT_NAME", queryParm.getData("PAT_NAME", 0));
			this.setValue("IDNO", queryParm.getData("IDNO", 0));
			this.setValue("SEX_CODE", queryParm.getData("SEX_CODE", 0));
			this.setValue("CTZ1_CODE", queryParm.getData("CTZ1_CODE", 0));
			this.setValue("CTZ2_CODE", queryParm.getData("CTZ2_CODE", 0));
			this.setValue("CTZ3_CODE", queryParm.getData("CTZ3_CODE", 0));
			this.setValue("CONTACTS_TEL", queryParm.getData("CONTACTS_TEL", 0));
			this.setValue("IPD_NO", queryParm.getData("IPD_NO", 0));

			// 灰色显示		
			callFunction("UI|PAT_NAME|setEnabled", false);
			callFunction("UI|IDNO|setEnabled", false);
			callFunction("UI|SEX_CODE|setEnabled", false);
			callFunction("UI|IPD_NO|setEnabled", false);
			callFunction("UI|CONTACTS_TEL|setEnabled", false);
			
			onQuery();
		}
	}

	/**
	 * 根据Case_no查询病人信息
	 * */
	public TParm queryByCaseNo(String case_no) {

		TParm selParm = new TParm();
		String sql = "SELECT A.MR_NO, A.PAT_NAME, A.IDNO,  A.SEX_CODE, B.CTZ1_CODE;A.CONTACTS_TEL;B.IPD_NO"
				+ ", B.CTZ2_CODE, B.CTZ3_CODE "
				+ "FROM SYS_PATINFO A, ADM_INP B "
				+ "WHERE A.MR_NO = B.MR_NO "
				+ "AND B.CASE_NO = '" + case_no + "'";

		selParm = new TParm(TJDODBTool.getInstance().select(sql));

		return selParm;

	}
	
	
	/**
	 * 根据MR_NO查询病患信息
	 * */
	public void onQuery() {
		
		TParm selParm = queryByMrno();
		
		if (selParm.getCount() < 0) {
			// 查无数据
			this.messageBox("E0008");	
			this.clearValue("MR_NO;PAT_NAME;IDNO;SEX_CODE;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;LOCK_CTZ_FLG;CONTACTS_TEL;IPD_NO");
			TTable table = (TTable) this.getComponent("Table");
	        table.removeRowAll();
	        return;
		}
		 
		caseNo = selParm.getData("CASE_NO",0).toString();
		this.setValue("MR_NO", selParm.getData("MR_NO", 0));		
		this.setValue("PAT_NAME", selParm.getData("PAT_NAME", 0));
		this.setValue("IDNO", selParm.getData("IDNO", 0));
		this.setValue("SEX_CODE", selParm.getValue("SEX_CODE", 0));
		this.setValue("CTZ1_CODE", selParm.getData("CTZ1_CODE", 0));
		this.setValue("CTZ2_CODE", selParm.getData("CTZ2_CODE", 0));
		this.setValue("CTZ3_CODE", selParm.getData("CTZ3_CODE", 0));
		this.setValue("CTZ11_CODE", selParm.getData("CTZ1_CODE", 0));
		this.setValue("CTZ22_CODE", selParm.getData("CTZ2_CODE", 0));
		this.setValue("CTZ33_CODE", selParm.getData("CTZ3_CODE", 0));
		this.setValue("IPD_NO", selParm.getValue("IPD_NO", 0));
		this.setValue("CONTACTS_TEL", selParm.getData("CONTACTS_TEL", 0));
		
		// 灰色显示		
		callFunction("UI|PAT_NAME|setEnabled", false);
		callFunction("UI|IDNO|setEnabled", false);
		callFunction("UI|SEX_CODE|setEnabled", false);
		callFunction("UI|IPD_NO|setEnabled", false);
		callFunction("UI|CONTACTS_TEL|setEnabled", false);
		TParm tableParm = queryTableByMrno();
		this.callFunction("UI|Table|setParmValue", tableParm);
		
		
	}
	
	/**
	 * 根据MR_NO查询历史身份修改记录
	 * */
	public TParm queryTableByMrno() {
		
		String mr_no =PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
		
		// modify by huangtt 20160928 EMPI患者查重提示 start
		Pat pat = Pat.onQueryByMrNo(mr_no);
		if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mr_no + " 已合并至 " + "" + pat.getMrNo());
			setValue("MR_NO", pat.getMrNo());
			mr_no = pat.getMrNo();
		}
		// modify by huangtt 20160928 EMPI患者查重提示 end
		
		
		TParm selParm = new TParm();
		String sql = 
				"SELECT A.MR_NO ,A.IPD_NO,A.PAT_NAME,A.IDNO,A.CONTACTS_TEL,"
				+ "A.BIRTH_DATE,B.CTZ_CODE1_O,B.CTZ_CODE2_O,B.CTZ_CODE3_O,A.SEX_CODE ,"
				+ "B.OPT_DATE,B.OPT_USER,B.OPT_TERM " +
				" FROM ADM_CTZ_LOG B, SYS_PATINFO A, ADM_INP P " +				
				" WHERE  B.MR_NO = '"+mr_no+"' " +
				" AND B.MR_NO = A.MR_NO  "
				+ "AND B.MR_NO = P.MR_NO" +
				" AND P.DS_DATE IS NULL " +
				" AND P.IN_DATE IS NOT NULL " +
				" AND P.CANCEL_FLG <> 'Y' "
				+ "ORDER BY OPT_DATE DESC";
		
		selParm = new TParm(TJDODBTool.getInstance().select(sql));						
		return selParm;
		
	}
	
	/**
	 * 根据MR_NO查询病患信息
	 * */
	public TParm queryByMrno() {
		
		String mr_no =PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
		
		// modify by huangtt 20160928 EMPI患者查重提示 start
		Pat pat = Pat.onQueryByMrNo(mr_no);
		if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mr_no + " 已合并至 " + "" + pat.getMrNo());
			setValue("MR_NO", pat.getMrNo());
			mr_no = pat.getMrNo();
		}
		// modify by huangtt 20160928 EMPI患者查重提示 end
		
		
		
		TParm selParm = new TParm();
		String sql = "SELECT A.MR_NO ,A.IPD_NO,A.PAT_NAME,A.IDNO,A.CONTACTS_TEL,A.BIRTH_DATE,B.CTZ1_CODE,B.CTZ2_CODE,B.CTZ3_CODE,A.SEX_CODE ,B.CASE_NO,B.OPT_DATE,B.OPT_USER,B.OPT_TERM " +
				" FROM ADM_INP B, SYS_PATINFO A" +				
				" WHERE  A.MR_NO = '"+mr_no+"' " +
				" AND A.MR_NO = B.MR_NO  " +
				" AND B.DS_DATE IS NULL " +
				" AND B.IN_DATE IS NOT NULL " +
				" AND B.CANCEL_FLG <> 'Y' ";
		
		selParm = new TParm(TJDODBTool.getInstance().select(sql));						
		return selParm;
		
	}
	
//	 private TCheckBox getCheckBx(String tagNam) {				
//			return (TCheckBox) getComponent("LOCK_CTZ_FLG");
//		}

	/**
	 * 保存
	 * */
	public void onSave() {
		
		// 从页面获取身份信息
		String CTZ1_CODE = (String) this.getValue("CTZ1_CODE");
		double totAmt3 = 0.00;
		double totAmt2 = 0.00;
		String sysDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 19).replace("-", "/");
		
		//主身份不能为空
		if(CTZ1_CODE == null || CTZ1_CODE.equals("")){
			this.messageBox("身份一不能为空！");
			return;
		}
		
		String CTZ2_CODE = (String) this.getValue("CTZ2_CODE");
		String CTZ3_CODE = (String) this.getValue("CTZ3_CODE");

		// 设置参数信息
		TParm parm = new TParm();

		TParm selParm = queryByMrno();
		if (selParm.getCount() < 0) {
			// 查无数据
			this.messageBox("E0008");	
			this.clearValue("MR_NO;PAT_NAME;IDNO;SEX_CODE;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;CTZ11_CODE;CTZ22_CODE;CTZ33_CODE;CONTACTS_TEL;IPD_NO");
			TTable table = (TTable) this.getComponent("Table");
	        table.removeRowAll();
		}else{
			parm.setData("CASE_NO", selParm.getData("CASE_NO",0).toString());
			parm.setData("CTZ1_CODE", CTZ1_CODE);
			parm.setData("CTZ2_CODE", CTZ2_CODE);
			parm.setData("CTZ3_CODE", CTZ3_CODE);
			parm.setData("CTZ11_CODE", CTZ1_CODE);
			parm.setData("CTZ22_CODE", CTZ2_CODE);
			parm.setData("CTZ33_CODE", CTZ3_CODE);
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			// 调用action中的方法
			TParm result = TIOM_AppServer
					.executeAction(actionName, "updBill", parm);
			if (result.getErrCode() == 0) {
				messageBox("P0005");
			} else {
				messageBox("E0005");
			}
			
			String sql1 = "SELECT LOCK_CTZ_FLG  FROM SYS_CTZ_REBATE  WHERE  CTZ_CODE IN ('"+CTZ1_CODE+"','"+CTZ2_CODE+"')";
			TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
			String sql2 = "SELECT LOCK_CTZ_FLG  FROM SYS_CTZ_REBATE  WHERE  LOCK_CTZ_FLG ='N' "
					+ "AND CTZ_CODE IN ('"+CTZ1_CODE+"','"+CTZ2_CODE+"')";
			TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
			if(result1.getCount() == result2.getCount()){
				
				// 身份比例
    			String sqlD = "SELECT A.CASE_NO,B.LOCK_CTZ_FLG AS LOCK_CTZO,C.LOCK_CTZ_FLG AS LOCK_CTZT,CASE WHEN B.DISCOUNT_RATE IS NULL THEN 1 ELSE B.DISCOUNT_RATE END "
    					+ "DISCOUNT_RATE "
    					+ "FROM ADM_INP A, SYS_CTZ_REBATE B, SYS_CTZ_REBATE C   "
    					+ "WHERE A.CTZ1_CODE=B.CTZ_CODE(+)  AND A.CTZ2_CODE = C.CTZ_CODE(+) "
    					+ "AND A.CASE_NO='"
    					+ selParm.getValue("CASE_NO", 0) + "' ";
    			TParm resultD = new TParm(TJDODBTool.getInstance().select(sqlD));
    			
    			
    			// 取红黄警戒线
    			String sqlSign = "SELECT YELLOW_SIGN,RED_SIGN FROM ODI_SYSPARM ";
    			TParm resultSign = new TParm(TJDODBTool.getInstance().select(sqlSign));

    			// 消费总金额
    			String sqlX = "SELECT SUM(TOT_AMT) TOT_AMT FROM IBS_ORDD "
    					+ "WHERE CASE_NO='" + selParm.getValue("CASE_NO", 0) + "' AND BILL_DATE <= TO_DATE('"
    					+ sysDate + "','YYYY/MM/DD HH24:MI:SS')";
    			TParm resultX = new TParm(TJDODBTool.getInstance().select(sqlX));
    			if (resultX.getCount() > 0
    					&& resultX.getValue("TOT_AMT", 0).length() > 0) {
    				totAmt2 = StringTool.round(resultX.getDouble("TOT_AMT", 0), 2);
    			}

    			// 预交金
    			String sql3 = " SELECT CASE_NO, SUM (PRE_AMT) PRE_AMT "
    					+ "FROM BIL_PAY WHERE REFUND_FLG = 'N' "
    					+ "AND TRANSACT_TYPE IN ('01', '03', '04') "
    					+ "AND CHARGE_DATE <= TO_DATE ('" + sysDate
    					+ "','YYYY/MM/DD HH24:MI:SS') AND CASE_NO = '" + selParm.getValue("CASE_NO", 0)
    					+ "' GROUP BY CASE_NO";
    			TParm result3 = new TParm(TJDODBTool.getInstance().select(sql3));
    			if (result3.getCount() > 0
    					&& result3.getValue("PRE_AMT", 0).length() > 0) {
    				totAmt3 = StringTool.round(result3.getDouble("PRE_AMT", 0), 2);
    			}

    			// 余额
    			double balance = StringTool.round(totAmt3
    					- (totAmt2 * resultD.getDouble("DISCOUNT_RATE", 0)), 2);
    			if (balance > resultSign.getDouble("RED_SIGN", 0)) {
					String sqlR = "UPDATE ADM_INP SET SMS_COUNT='0',STOP_BILL_FLG='N',UNLOCKED_FLG='0' WHERE CASE_NO='"
							+ selParm.getValue("CASE_NO", 0) + "' ";
					TJDODBTool.getInstance().update(sqlR);
				}else{
					String sqlNR = "UPDATE ADM_INP SET SMS_COUNT='0',STOP_BILL_FLG='Y',UNLOCKED_FLG='0' WHERE CASE_NO='"
							+ selParm.getValue("CASE_NO", 0) + "' ";
					TJDODBTool.getInstance().update(sqlNR);
				}
				
			}else{
				String updSql = "UPDATE ADM_INP SET  SMS_COUNT='0',STOP_BILL_FLG = 'N',UNLOCKED_FLG = '2' WHERE  CASE_NO = '"
						+ selParm.getValue("CASE_NO", 0) + "' ";
				result = new TParm(TJDODBTool.getInstance().update(
						updSql));
			}
			
		}
		onQuery();

	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.clearValue("MR_NO;PAT_NAME;IDNO;SEX_CODE;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;IPD_NO;CONTACTS_TEL");
		TTable table = (TTable) this.getComponent("Table");
        table.removeRowAll();
	}

}
