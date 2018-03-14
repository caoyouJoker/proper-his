package action.ibs;

import java.sql.Timestamp;
import java.util.Date;

import com.dongyang.action.TAction;
import com.dongyang.config.TConfig;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;

import jdo.ibs.IBSTool;
import jdo.inf.INFSmsTool;
import jdo.med.MedSmsTool;
import jdo.util.XmlUtil;
import jdo.util.XmlUtilMS;

/**
 * 
 * <p>
 * Title: 住院计价动作类
 * </p>
 * 
 * <p>
 * Description: 住院计价动作类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangl
 * @version 1.0
 */
public class IBSAction extends TAction {
	// /**
	// * 新增医嘱
	// * @param parm TParm
	// * @return TParm
	// */
	// public TParm onNewIBSOrder(String dataType, TParm parm) {
	// TConnection connection = getConnection();
	// TParm result = new TParm();
	// //IBS(补充批价产生)
	// if (dataType.equals("1")) {
	// result = IBSOrdermTool.getInstance().insertdata(parm, connection);
	// result = IBSOrderdTool.getInstance().insertdata(parm,connection);
	// }
	// //UDD(药房发药)
	// if (dataType.equals("2")) {
	// }
	// //INW(护士执行计费)
	// if (dataType.equals("3")) {
	// }
	// //医技计费
	// if (dataType.equals("4")) {
	//
	// }
	//
	// connection.commit();
	// connection.close();
	// return result;
	//
	// }
	/**
	 * 插入计价档
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onNewIBSBill(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		result = IBSTool.getInstance().insertIBSBillData(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 执行缴费作业
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onSaveIBSCharge(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		result = IBSTool.getInstance().insertIBSChargeData(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 作废账单
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onSaveBillReturn(TParm parm) {
		TConnection connection = getConnection();
		TParm result = new TParm();
		result = IBSTool.getInstance().insertBillReturn(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * 修改身份
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author caowl
	 */
	public TParm updBill(TParm parm) {

		TConnection connection = getConnection();
		TParm result = new TParm();
		result = IBSTool.getInstance().updBill(parm, connection);

		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();

		return result;
	}
	
	/**
	 * add by yanmm 201709 绿色通道
	 * 科主任
	 * @param parm
	 * @return
	 */
	public TParm unlockedTellK(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "参数不能为空");
			return result;
		}
		StringBuilder sendResult = new StringBuilder("");
		sendResult.append("沟通短信发送开始 ===== START\n");
		// 经治医生(发送的号码)
		TParm telParm = new TParm();
		String content = "";
		telParm = MedSmsTool.getInstance().getVsDirectorDrCode(
				parm.getValue("CASE_NO"));
		String msIf ="";
		if(parm.getValue("UNLOCK_CASE").equals("03")){
			msIf = "("+parm.getValue("UNLOCK_CASE_TEXT")+")"; 
		}
		content ="病床:"
				+ parm.getValue("BED_NO_DESC") + ",科室:"
				+ parm.getValue("DEPT_CHN_DESC") + ",因:"
				+ parm.getValue("CHN_DESC")+ msIf + ",已由:"
				+ parm.getValue("USER_NAME")
				+ "医师重新开启收费窗口,请尽快通知患者或家属补交住院预交金!";
		// 发送短信
		Timestamp sendTs = StringTool.getTimestamp(
				parm.getValue("SEND_DATE"), "yyyyMMddHHmmss");
		String sendDate = StringTool.getString(sendTs, "yyyy年MM月dd日 HH时mm分");
		parm.setData("SEND_DATE", sendDate);
		writeXml(parm, telParm, content);
		sendResult.append("沟通短信发送结束 ===== END\n");
		TIOM_FileServer.writeFile("C:/JavaHis/logs/沟通短信发送结果"
				+ StringTool.getTimestamp(new Date()).toString(), sendResult
				.toString().getBytes());
		return result;
	}
	
	
	/**
	 * add by yanmm 201707 绿色通道
	 * 医生
	 * @param parm
	 * @return
	 */
	public TParm unlockedTell(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "参数不能为空");
			return result;
		}
		StringBuilder sendResult = new StringBuilder("");
		sendResult.append("沟通短信发送开始 ===== START\n");
		// 经治医生(发送的号码)
		TParm telParm = new TParm();
		String content = "";
		telParm = MedSmsTool.getInstance().getVsDrCode(
				parm.getValue("CASE_NO"));
		String msIf ="";
		if(parm.getValue("UNLOCK_CASE").equals("03")){
			msIf = "("+parm.getValue("UNLOCK_CASE_TEXT")+")"; 
		}
		content =  "病床:"
				+ parm.getValue("BED_NO_DESC") + ",科室:"
				+ parm.getValue("DEPT_CHN_DESC") + ",因:"
				+ parm.getValue("CHN_DESC")+ msIf + ",已由:"
				+ parm.getValue("USER_NAME")
				+ "医师重新开启收费窗口,请尽快通知患者或家属补交住院预交金!";
		// 发送短信
		Timestamp sendTs = StringTool.getTimestamp(
				parm.getValue("SEND_DATE"), "yyyyMMddHHmmss");
		String sendDate = StringTool.getString(sendTs, "yyyy年MM月dd日 HH时mm分");
		parm.setData("SEND_DATE", sendDate);
		writeXml(parm, telParm, content);
		sendResult.append("沟通短信发送结束 ===== END\n");
		TIOM_FileServer.writeFile("C:/JavaHis/logs/沟通短信发送结果"
				+ StringTool.getTimestamp(new Date()).toString(), sendResult
				.toString().getBytes());
		return result;
	} 
	/**
	 * add by yanmm 201707 绿色通道
	 * 护士
	 * @param parm
	 * @return
	 */
	public TParm unlockedTellR(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "参数不能为空");
			return result;
		}
		StringBuilder sendResult = new StringBuilder("");
		sendResult.append("沟通短信发送开始 ===== START\n");
		// 主管护士(发送的号码)
		TParm telParm = new TParm();
		String content = "";
		telParm = MedSmsTool.getInstance().getVsNurseCode(
				parm.getValue("CASE_NO"));
		String msIf ="" ;
		if(parm.getValue("UNLOCK_CASE").equals("03")){
			msIf = "("+parm.getValue("UNLOCK_CASE_TEXT")+")"; 
		}
		content =  "病床:"
				+ parm.getValue("BED_NO_DESC") + ",科室:"
				+ parm.getValue("DEPT_CHN_DESC") + ",因:"
				+ parm.getValue("CHN_DESC")+ msIf + ",已由"
				+ parm.getValue("USER_NAME")
				+ "医师重新开启收费窗口,请尽快通知患者或家属补交住院预交金!";

		// 发送短信
		Timestamp sendTs = StringTool.getTimestamp(
				parm.getValue("SEND_DATE"), "yyyyMMddHHmmss");
		String sendDate = StringTool.getString(sendTs, "yyyy年MM月dd日 HH时mm分");
		parm.setData("SEND_DATE", sendDate);
		writeXml(parm, telParm, content);
		sendResult.append("沟通短信发送结束 ===== END\n");
		TIOM_FileServer.writeFile("C:/JavaHis/logs/沟通短信发送结果"
				+ StringTool.getTimestamp(new Date()).toString(), sendResult
				.toString().getBytes());
		return result;
	}
	

  
	
	private void writeXml(TParm parmRow, TParm telParm, String content) {
		// 写文件
		TParm xmlParm = new TParm();
		xmlParm.setData("Content", content);
		xmlParm.setData("MrNo", parmRow.getValue("MR_NO").replace("[", "")
				.replace("]", ""));
		// 得到科室,门急住类别
		String deptChnCode = parmRow.getValue("DEPT_CHN_DESC");
	//	String admType = parmRow.getValue("ADM_TYPE");
	//	String admTypeChn = "住院";
		//admTypeChn = getAdmType(admType);
		
		xmlParm.setData("Name", parmRow.getValue("PAT_NAME") + ","
				+ deptChnCode );
		xmlParm.setData("SysNo", "IBS");
		// 报告时间
		xmlParm.setData("ReportDate", parmRow.getValue("REPORT_DATE"));

		XmlUtilMS.createSmsFile(xmlParm, telParm);
	}
	
//	
//	  private String getAdmType(String admType) {
//			String admTypeChn = "";
//			if (admType != null) {
//				if (admType.equals("O")) {
//					admTypeChn = "门诊";
//				} else if (admType.equals("I")) {
//					admTypeChn = "住院";
//				} else if (admType.equals("E")) {
//					admTypeChn = "急诊";
//				} else if (admType.equals("H")) {
//					admTypeChn = "健康检查";
//				}
//			}
//			return admTypeChn;
//		}
	

}
























