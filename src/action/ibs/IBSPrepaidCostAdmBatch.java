package action.ibs;

import java.sql.Timestamp;
import java.util.Date;

//import jdo.adm.ADMAutoBillTool;
//import jdo.sys.Operator;
//import jdo.sys.SystemTool;

import jdo.med.MedSmsTool;
import jdo.sys.SystemTool;
import jdo.util.XmlUtilMS;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
//import com.dongyang.db.TConnection;
//import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.patch.Patch;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 预交金控费管理批次
 * </p>
 * 
 * <p>
 * Description: 预交金控费管理批次
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author yanmm 2017/07/03
 * @version 1.0
 */
public class IBSPrepaidCostAdmBatch extends Patch {
	public IBSPrepaidCostAdmBatch() {
	}

	/**
	 * 
	 * @return boolean
	 */
	public boolean run() {
		TConnection connection = TDBPoolManager.getInstance().getConnection();
		TParm result = patch_I(null, connection);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return false;
		}
		connection.commit();
		connection.close();
		return true;
	}

	public TParm patch_I(TParm parmDate, TConnection connection) {
		String endDate;
		if (parmDate == null) {
			Timestamp date = StringTool.getTimestamp(new Date());
			endDate = date.toString().substring(0, 19).replace("-", "");
		} else {
			endDate = parmDate.getValue("DATE", 0) + "070000";
		}


		// 取红黄警戒线
		String sqlSign = "SELECT YELLOW_SIGN,RED_SIGN FROM ODI_SYSPARM ";
		TParm resultSign = new TParm(TJDODBTool.getInstance().select(sqlSign));
		if (resultSign.getErrCode() < 0) {
			return resultSign;
		}
		//查询所有在院 不是永久解锁的病人
		String sql1 = "SELECT A.CASE_NO,A.UNLOCKED_FLG,A.MES_COUNT,B.LOCK_CTZ_FLG AS LOCK_CTZ1, "
				+ "C.LOCK_CTZ_FLG AS LOCK_CTZ2,CASE WHEN B.DISCOUNT_RATE IS NULL THEN 1 ELSE B.DISCOUNT_RATE END "
				+ "DISCOUNT_RATE "
				+ "FROM ADM_INP A, SYS_CTZ_REBATE B, SYS_CTZ_REBATE C  "
				+ "WHERE A.CTZ1_CODE=B.CTZ_CODE(+)  AND A.CTZ2_CODE = C.CTZ_CODE(+) "
				+ "AND  A.DS_DATE IS NULL AND A.CANCEL_FLG<>'Y'  AND A.UNLOCKED_FLG <> '2' ";   
		TParm result = new TParm(TJDODBTool.getInstance().select(sql1));
		if (result.getErrCode() < 0) {
			return result;
		}
		double totAmt2 = 0.00;
		double totAmt3 = 0.00;
		int j = 0;
		TParm reusltParm = new TParm();
		for (int i = 0; i < result.getCount(); i++) {
			if(!result.getValue("LOCK_CTZ1", i).equals("Y") && 
					!result.getValue("LOCK_CTZ2", i).equals("Y")){
			// 短信发送次数
			if (null == result.getValue("MES_COUNT", i)
					|| result.getValue("MES_COUNT", i).length() <= 0) {
				j = 1; 
			} else {
				j = Integer.parseInt(result.getValue("MES_COUNT", i)) + 1;
			}

			// 消费总金额
			String sql2 = "SELECT SUM(D.TOT_AMT) TOT_AMT FROM ADM_INP A,IBS_ORDD D,SYS_CTZ Z "
					+ "WHERE A.CASE_NO=D.CASE_NO "
					+ "AND A.CASE_NO='"
					+ result.getValue("CASE_NO", i)
					+ "' "
					+ "AND A.CTZ1_CODE = Z.CTZ_CODE "
					+ "AND Z.MAIN_CTZ_FLG = 'Y' "
					// + "AND Z.NHI_CTZ_FLG = 'N' "
					+ "AND D.BILL_DATE BETWEEN A.IN_DATE "
					+ "AND TO_DATE('"
					+ endDate + "','YYYY/MM/DD HH24:MI:SS')";
			TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
			if (result2.getCount() > 0
					&& result2.getValue("TOT_AMT", 0).length() > 0) {
				totAmt2 = StringTool.round(result2.getDouble("TOT_AMT", 0), 2);
			}

			// 预交金
			String sql3 = " SELECT CASE_NO, SUM (PRE_AMT) PRE_AMT "
					+ "FROM BIL_PAY WHERE REFUND_FLG = 'N' "
					+ "AND TRANSACT_TYPE IN ('01', '03', '04') "
					+ "AND CHARGE_DATE < TO_DATE ('" + endDate
					+ "','YYYY/MM/DD HH24:MI:SS') " + "AND CASE_NO = '"
					+ result.getValue("CASE_NO", i) + "' GROUP BY CASE_NO";
			TParm result3 = new TParm(TJDODBTool.getInstance().select(sql3));
			if (result3.getCount() > 0
					&& result3.getValue("PRE_AMT", 0).length() > 0) {
				totAmt3 = StringTool.round(result3.getDouble("PRE_AMT", 0), 2);
			}
			//查询预交金无数据的病人
			String sql4 =" SELECT CASE_NO FROM BIL_PAY WHERE CASE_NO ='"+result.getValue("CASE_NO", i)+"' ";
			TParm result4 = new TParm(TJDODBTool.getInstance().select(sql4));
			//算法:预交金总额-费用总额*身份比例
			if ( result4.getCount()<=0 || 
					totAmt3 - (totAmt2 * result.getDouble("DISCOUNT_RATE", i)) <= resultSign.getDouble("RED_SIGN", 0)) {
				reusltParm=onStop(result.getValue("CASE_NO", i),connection);
				if (reusltParm.getErrCode()<0) {
					return reusltParm;
				}
				onTelMR(result.getValue("CASE_NO", i));
				onTelMRR(result.getValue("CASE_NO", i));
			}
			if (totAmt3 - (totAmt2 * result.getDouble("DISCOUNT_RATE", i)) <= resultSign
					.getDouble("YELLOW_SIGN", 0)
					&& totAmt3 - (totAmt2
							* result.getDouble("DISCOUNT_RATE", i)) > resultSign
								.getDouble("RED_SIGN", 0)) {
				onTelM(result.getValue("CASE_NO", i));
				System.out.println("金额>红色且<=黄色"+result.getValue("CASE_NO", i));
			}
			
			if (totAmt3 - (totAmt2 * result.getDouble("DISCOUNT_RATE", i)) > resultSign
					.getDouble("RED_SIGN", 0)
					&& result.getValue("UNLOCKED_FLG", i).equals("1")) {
				reusltParm=onChank(result.getValue("CASE_NO", i),connection);
				if (reusltParm.getErrCode()<0) {
					return reusltParm;
				}
			}
//			if (totAmt3 - (totAmt2 * result.getDouble("DISCOUNT_RATE", i)) > resultSign
//					.getDouble("RED_SIGN", 0)
//					&& result.getValue("UNLOCKED_FLG", i).equals("0")) {
//				reusltParm=onChank(result.getValue("CASE_NO", i),connection);
//				if (reusltParm.getErrCode()<0) {
//					return reusltParm;
//				}
//			}
			if (totAmt3 - (totAmt2 * result.getDouble("DISCOUNT_RATE", i)) > resultSign
					.getDouble("RED_SIGN", 0)
					&& result.getValue("UNLOCKED_FLG", i).equals("3")) {
				reusltParm=onChank(result.getValue("CASE_NO", i),connection);
				if (reusltParm.getErrCode()<0) {
					return reusltParm;
				}
			}
			
			
			String sqlMC = "UPDATE ADM_INP SET MES_COUNT='" + j
					+ "' WHERE CASE_NO='" + result.getValue("CASE_NO", i)
					+ "'";
			reusltParm = new TParm(TJDODBTool.getInstance().update(sqlMC,
					connection));
			if (reusltParm.getErrCode()<0) {
				return reusltParm;
			}
			totAmt2 = 0.00;
			totAmt3 = 0.00;
			
		}
	}
	
	
		
		return result;
	}

	// 若预交金金额大于红色警戒,改为未锁
	public TParm onChank(String case_no,TConnection connection) {
		String sqlMSBF = "UPDATE ADM_INP SET STOP_BILL_FLG='N',UNLOCKED_FLG = '0' WHERE CASE_NO='"
				+ case_no + "'";
		TParm result= new TParm(TJDODBTool.getInstance().update(sqlMSBF, connection));
		return result;
	}

	// 若预交金金额小于红色警戒则自动停止划价
	public TParm onStop(String case_no,TConnection connection) {
		String sqlMSBF = "UPDATE ADM_INP SET STOP_BILL_FLG='Y',UNLOCKED_FLG ='0' WHERE CASE_NO='"
				+ case_no + "'";
		TParm result= new TParm(TJDODBTool.getInstance().update(sqlMSBF, connection));
		return result;
	}

	public TParm getParm(String case_no) {
		TParm parm = new TParm();
		String sql6 = "SELECT A.CASE_NO,A.MR_NO,A.VS_DR_CODE,A.DEPT_CODE,D.BED_NO_DESC,B.PAT_NAME,C.DEPT_CHN_DESC "
				+ "FROM ADM_INP A,SYS_DEPT C,SYS_PATINFO B,SYS_BED D "
				+ "WHERE  A.CASE_NO = '"
				+ case_no
				+ "' AND A.MR_NO = B.MR_NO AND A.BED_NO=D.BED_NO(+) AND A.DEPT_CODE = C.DEPT_CODE";
		TParm result6 = new TParm(TJDODBTool.getInstance().select(sql6));
		parm.setData("SEND_DATE", SystemTool.getInstance().getDate());
		parm.setData("CASE_NO", result6.getValue("CASE_NO", 0));
		parm.setData("MR_NO", result6.getValue("MR_NO", 0));
		//parm.setData("DEPT_CODE", result6.getValue("DEPT_CODE", 0));
		if(result6.getValue("DEPT_CODE", 0) == null 
				|| (result6.getValue("DEPT_CODE", 0).length()<=0)){
		parm.setData("BED_NO_DESC", " ");
		}else{
		parm.setData("BED_NO_DESC", result6.getValue("BED_NO_DESC", 0));
		}
		parm.setData("PAT_NAME", result6.getValue("PAT_NAME", 0));
		parm.setData("DEPT_CHN_DESC", result6.getValue("DEPT_CHN_DESC", 0));
		parm.setData("REPORT_DATE", SystemTool.getInstance().getDate());
		return parm;
	}

	public void onTelM(String case_no) {// 黄色
		TParm parm = getParm(case_no);
		StringBuilder sendResult = new StringBuilder("");
		sendResult.append("沟通短信发送开始 ===== START\n");
		// 经治医生(发送的号码)
		TParm telParm = new TParm();
		String content = "";
		telParm = MedSmsTool.getInstance()
				.getVsDrCode(parm.getValue("CASE_NO"));
		content = "病床:" + parm.getValue("BED_NO_DESC")
				+ ",科室:" + parm.getValue("DEPT_CHN_DESC")
				+ ",预交金已超过黄色警戒,请尽快通知患者或家属补交住院预交金。";
		// 发送短信
		Timestamp sendTs = StringTool.getTimestamp(parm.getValue("SEND_DATE"),
				"yyyyMMddHHmmss");
		String sendDate = StringTool.getString(sendTs, "yyyy年MM月dd日 HH时mm分");
		parm.setData("SEND_DATE", sendDate);
		writeXml(parm, telParm, content);
		sendResult.append("沟通短信发送结束 ===== END\n");
		TIOM_FileServer.writeFile("C:/JavaHis/logs/沟通短信发送结果"
				+ StringTool.getTimestamp(new Date()).toString(), sendResult
				.toString().getBytes());
	}

	public void onTelMR(String case_no) {// 红色
		TParm parm = getParm(case_no);
		StringBuilder sendResult = new StringBuilder("");
		sendResult.append("沟通短信发送开始 ===== START\n");
		// 经治医生(发送的号码)
		TParm telParm = new TParm();
		String content = "";
		telParm = MedSmsTool.getInstance()
				.getVsDrCode(parm.getValue("CASE_NO"));
		content ="病床:" + parm.getValue("BED_NO_DESC")
				+ ",科室:" + parm.getValue("DEPT_CHN_DESC")
				+ ",预交金已超过红色警戒,系统已关闭医嘱录入窗口禁止开立医嘱。请尽快通知患者或家属补交住院预交金。 ";

		// 发送短信
		Timestamp sendTs = StringTool.getTimestamp(parm.getValue("SEND_DATE"),
				"yyyyMMddHHmmss");
		String sendDate = StringTool.getString(sendTs, "yyyy年MM月dd日 HH时mm分");
		parm.setData("SEND_DATE", sendDate);
		writeXml(parm, telParm, content);
		sendResult.append("沟通短信发送结束 ===== END\n");
		TIOM_FileServer.writeFile("C:/JavaHis/logs/沟通短信发送结果"
				+ StringTool.getTimestamp(new Date()).toString(), sendResult
				.toString().getBytes());
	}

	public void onTelMRR(String case_no) {// 红色护士长
		TParm parm = getParm(case_no);
		StringBuilder sendResult = new StringBuilder("");
		sendResult.append("沟通短信发送开始 ===== START\n");
		// 护士(发送的号码)
		TParm telParm = new TParm();
		String content = "";
		telParm = MedSmsTool.getInstance().getVsNurseCode(
				parm.getValue("CASE_NO"));

		content = "病床:" + parm.getValue("BED_NO_DESC")
				+ ",科室:" + parm.getValue("DEPT_CHN_DESC")
				+ ",预交金已超过红色警戒,系统已关闭医嘱录入窗口禁止开立医嘱。请尽快通知患者或家属补交住院预交金。";
		// 发送短信
		Timestamp sendTs = StringTool.getTimestamp(parm.getValue("SEND_DATE"),
				"yyyyMMddHHmmss");
		String sendDate = StringTool.getString(sendTs, "yyyy年MM月dd日 HH时mm分");
		parm.setData("SEND_DATE", sendDate);
		writeXml(parm, telParm, content);
		sendResult.append("沟通短信发送结束 ===== END\n");
		TIOM_FileServer.writeFile("C:/JavaHis/logs/沟通短信发送结果"
				+ StringTool.getTimestamp(new Date()).toString(), sendResult
				.toString().getBytes());
	}

	private void writeXml(TParm parmRow, TParm telParm, String content) {
		// 写文件
		TParm xmlParm = new TParm();
		xmlParm.setData("Content", content);
		xmlParm.setData("MrNo", parmRow.getValue("MR_NO").replace("[", "")
				.replace("]", ""));

		// 得到科室,门急住类别
		String deptChnCode = parmRow.getValue("DEPT_CHN_DESC");

		xmlParm.setData("Name", parmRow.getValue("PAT_NAME") + ","
				+ deptChnCode);
		xmlParm.setData("SysNo", "IBS");
		// 报告时间
		xmlParm.setData("ReportDate", parmRow.getValue("REPORT_DATE"));

		XmlUtilMS.createSmsFile(xmlParm, telParm);
	}

}
