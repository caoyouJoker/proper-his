package action.cbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_AppServer;

public class CBCServerAction extends TAction {

	public CBCServerAction() {
	} 

	//院内病人信息建立
	public TParm SignTran(TParm p) {
		Map<String, Object> map = (HashMap)p.getData();
		Map<String, Object> CommitData = (HashMap)map.get("CommitData");
		Map<String, Object> OperateInfo = (HashMap)map.get("OperateInfo");

		// 输出建行传入参数
		// print(map);

		TParm parm = new TParm();
		// parm.setData(CommitData);
		//CommitData
		parm.setData("PERSON_NO", CommitData.get("PERSON_NO"));
		parm.setData("NAME", CommitData.get("NAME"));
		parm.setData("ID_NO", CommitData.get("ID_NO"));
		parm.setData("SEX", CommitData.get("SEX"));
		parm.setData("BIRTHDATE", CommitData.get("BIRTHDATE"));
		parm.setData("TEL", CommitData.get("TEL"));
		parm.setData("ADDRESS", CommitData.get("ADDRESS"));
		//OperateInfo
		parm.setData("OPT_ID", OperateInfo.get("OPT_ID"));
		parm.setData("OPT_NAME", OperateInfo.get("OPT_NAME"));
		parm.setData("OPT_IP", OperateInfo.get("OPT_IP"));
		parm.setData("HIS_DATE", OperateInfo.get("HIS_DATE"));
		parm.setData("GUID", OperateInfo.get("GUID"));
		parm.setData("TOKEN", OperateInfo.get("TOKEN"));

		// 调用JAVAHIS业务逻辑
		TSocket socket = new TSocket("192.168.1.113", 8081, "web");
		TParm resultData = new TParm();
		resultData = TIOM_AppServer.executeAction(socket,
				"action.reg.REGCcbAction", "signTran", parm);

		Map<String, Object> rCommitData = new HashMap<String, Object>();

		Map<String, Object> rReturnData = new HashMap<String, Object>();
		rReturnData.put("PATIENT_ID", resultData.getValue("PATIENT_ID"));

		Map<String, Object> rOperateInfo = new HashMap<String, Object>();
		rOperateInfo.put("METHOD", "SignTran");
		rOperateInfo.put("OPT_ID", "");
		rOperateInfo.put("OPT_NAME", "");
		rOperateInfo.put("OPT_IP", "");
		rOperateInfo.put("HIS_DATE", "");
		rOperateInfo.put("GUID", "");
		rOperateInfo.put("TOKEN", "");

		Map<String, Object> rResult = new HashMap<String, Object>();
		rResult.put("EXECUTE_FLAG", resultData.getValue("EXECUTE_FLAG"));
		rResult.put("EXECUTE_MESSAGE", resultData.getValue("EXECUTE_MESSAGE"));

		Map<String, Map<String, Object>> returnMap = new HashMap<String, Map<String, Object>>();
		returnMap.put("CommitData", rCommitData);
		returnMap.put("ReturnData", rReturnData);
		returnMap.put("OperateInfo", rOperateInfo);
		returnMap.put("Result", rResult);

		TParm returnParm = new TParm();
		returnParm.setData(returnMap);
		return returnParm;

	}

	//保存当日或预约挂号数据
	public TParm SaveRegister(TParm p) {
		Map<String, Object> map = (HashMap)p.getData();
		Map<String, Object> CommitData = (HashMap)map.get("CommitData");
		Map<String, Object> OperateInfo = (HashMap)map.get("OperateInfo");

		// 输出建行传入参数
		// print(map);

		TParm parm = new TParm();
		// parm.setData(CommitData);
		parm.setData("HIS_CODE", CommitData.get("HIS_CODE"));
		parm.setData("DEPT_NO", CommitData.get("DEPT_NO"));
		parm.setData("ESTABLISH_TYPE", CommitData.get("ESTABLISH_TYPE"));
		parm.setData("SS_TYPE", CommitData.get("SS_TYPE"));
		parm.setData("DOCTOR_NAME", CommitData.get("DOCTOR_NAME"));
		parm.setData("DOCTOR_NO", CommitData.get("DOCTOR_NO"));
		parm.setData("PATIENT_ID", CommitData.get("PATIENT_ID"));
		parm.setData("CARD_NO", CommitData.get("CARD_NO"));
		parm.setData("NAME", CommitData.get("NAME"));
		parm.setData("ID_NO", CommitData.get("ID_NO"));
		parm.setData("PERSON_NO", CommitData.get("PERSON_NO"));
		parm.setData("DATE", CommitData.get("DATE"));
		parm.setData("WEEK", CommitData.get("WEEK"));
		parm.setData("ESTABLISH_TIME", CommitData.get("ESTABLISH_TIME"));
		parm.setData("AMOUNT", CommitData.get("AMOUNT"));
		parm.setData("CLINIC_FEE", CommitData.get("CLINIC_FEE"));
		parm.setData("OTHER_FEE", CommitData.get("OTHER_FEE"));
		parm.setData("TOTAL_FEE", CommitData.get("TOTAL_FEE"));
		parm.setData("TRAN_TYPE", CommitData.get("TRAN_TYPE"));
		parm.setData("STREAM_NO", CommitData.get("STREAM_NO"));
		parm.setData("TRADE_CHANNEL", CommitData.get("TRADE_CHANNEL"));
		parm.setData("SSCARD_NO", CommitData.get("SSCARD_NO"));
		parm.setData("OPT_ID", OperateInfo.get("OPT_ID"));
		parm.setData("OPT_NAME", OperateInfo.get("OPT_NAME"));
		parm.setData("OPT_IP", OperateInfo.get("OPT_IP"));
		parm.setData("HIS_DATE", OperateInfo.get("HIS_DATE"));
		parm.setData("GUID", OperateInfo.get("GUID"));
		parm.setData("TOKEN", OperateInfo.get("TOKEN"));

		// 调用JAVAHIS业务逻辑
		TSocket socket = new TSocket("192.168.1.113", 8081, "web");
		TParm resultData = new TParm();  
		if (CommitData.get("TRAN_TYPE").toString().equals("0")) {
			resultData = TIOM_AppServer.executeAction(socket,
					"action.reg.REGCcbAction", "saveRegister", parm);
		} else {
			resultData = TIOM_AppServer.executeAction(socket,
					"action.reg.REGCcbAction", "当日挂号", parm);
		}

		Map<String, Object> rCommitData = new HashMap<String, Object>();

		Map<String, Object> rReturnData = new HashMap<String, Object>();
		rReturnData.put("PATIENT_ID", resultData.getValue("PATIENT_ID"));
		rReturnData.put("TRAN_NO", resultData.getValue("TRAN_NO"));
		rReturnData.put("SEQ", resultData.getValue("SEQ"));
		rReturnData
				.put("PER_PAY_AMOUNT", resultData.getValue("PER_PAY_AMOUNT"));
		rReturnData.put("S_ACCOUNT_AMOUNT", resultData
				.getValue("S_ACCOUNT_AMOUNT"));
		rReturnData.put("S_GOC_AMOUNT", resultData.getValue("S_GOC_AMOUNT"));

		Map<String, Object> rOperateInfo = new HashMap<String, Object>();
		rOperateInfo.put("METHOD", "SaveRegister");
		rOperateInfo.put("OPT_ID", "");
		rOperateInfo.put("OPT_NAME", "");
		rOperateInfo.put("OPT_IP", "");
		rOperateInfo.put("HIS_DATE", "");
		rOperateInfo.put("GUID", "");
		rOperateInfo.put("TOKEN", "");

		Map<String, Object> rResult = new HashMap<String, Object>();
		rResult.put("EXECUTE_FLAG", resultData.getValue("EXECUTE_FLAG"));
		rResult.put("EXECUTE_MESSAGE", resultData.getValue("EXECUTE_MESSAGE"));

		Map<String, Map<String, Object>> returnMap = new HashMap<String, Map<String, Object>>();
		returnMap.put("CommitData", rCommitData);
		returnMap.put("ReturnData", rReturnData);
		returnMap.put("OperateInfo", rOperateInfo);
		returnMap.put("Result", rResult);

		TParm returnParm = new TParm(returnMap);
		return returnParm;

	}

	//预约挂号信息查询
	public TParm BankQueryRegister(TParm p) {
System.out.println("tparm:===================================="+p);
		Map<String, Object> map = (HashMap)p.getData();
		Map<String, Object> CommitData = (HashMap)map.get("CommitData");
		Map<String, Object> OperateInfo = (HashMap)map.get("OperateInfo");

		// 输出建行传入参数
		// print(map);

		TParm parm = new TParm();
		// parm.setData(CommitData);
		// CommitData
		parm.setData("HIS_CODE", CommitData.get("HIS_CODE"));
		parm.setData("CARD_NO", CommitData.get("CARD_NO"));
		parm.setData("PERSON_NO", CommitData.get("PERSON_NO"));
		parm.setData("START_DATE", CommitData.get("START_DATE"));
		parm.setData("END_DATE", CommitData.get("END_DATE"));
		parm.setData("QUERY_TYPE", CommitData.get("QUERY_TYPE"));
		// OperateInfo
		parm.setData("OPT_ID", OperateInfo.get("OPT_ID"));
		parm.setData("OPT_NAME", OperateInfo.get("OPT_NAME"));
		parm.setData("OPT_IP", OperateInfo.get("OPT_IP"));
		parm.setData("HIS_DATE", OperateInfo.get("HIS_DATE"));
		parm.setData("GUID", OperateInfo.get("GUID"));
		parm.setData("TOKEN", OperateInfo.get("TOKEN"));

		// 调用JAVAHIS业务逻辑  
		TSocket socket = new TSocket("192.168.0.111", 8081, "web");
		TParm resultData = new TParm();              
		resultData = TIOM_AppServer.executeAction(socket,
				"action.reg.REGCcbAction", "bankQueryRegister", parm);
  System.out.println("resultData:==========================================="+resultData);
		Map<String, Object> rCommitData = new HashMap<String, Object>();

		Map<String, Object> rReturnData = new HashMap<String, Object>();
		rReturnData.put("PATIENT_ID", resultData.getValue("PATIENT_ID", 0));
		rReturnData.put("COUNT", resultData.getCount()+"");

		Map<String, Object> rOperateInfo = new HashMap<String, Object>();
		rOperateInfo.put("METHOD", "SaveRegister");
		rOperateInfo.put("OPT_ID", "");
		rOperateInfo.put("OPT_NAME", "");
		rOperateInfo.put("OPT_IP", "");
		rOperateInfo.put("HIS_DATE", "");
		rOperateInfo.put("GUID", "");
		rOperateInfo.put("TOKEN", "");

		Map<String, Object> rResult = new HashMap<String, Object>();
		rResult.put("EXECUTE_FLAG", resultData.getValue("EXECUTE_FLAG"));
		rResult.put("EXECUTE_MESSAGE", resultData.getValue("EXECUTE_MESSAGE"));

		Map<String, Map<String, Object>> returnMap = new HashMap<String, Map<String, Object>>();
		returnMap.put("CommitData", rCommitData);
		returnMap.put("ReturnData", rReturnData);
		returnMap.put("OperateInfo", rOperateInfo);
		returnMap.put("Result", rResult);

		List<Map> list = new ArrayList<Map>();
		resultData.removeData("PATIENT_ID");
		resultData.removeData("COUNT");
		int count = resultData.getCount();
		for (int i = 0; i < count; i++) {
			TParm tp = (TParm) resultData.getRow(i);
			System.out.println("tp:=========================================="+tp);
			Map m = tp.getData();
			//System.out.println(m.get("Data"));
			list.add((Map)m.get("Data"));
		}
		
		Map<String, Object> map5 = new HashMap<String, Object>();
		map5.put("return",returnMap);
		map5.put("list",list);
		
		return new TParm(map5);

	}
	
	//交易取消
	public TParm BankCancelTran(TParm p) {
		Map<String, Object> map = (HashMap)p.getData();
		Map<String, Object> CommitData = (HashMap)map.get("CommitData");
		Map<String, Object> OperateInfo = (HashMap)map.get("OperateInfo");

		// 输出建行传入参数
		// print(map);

		TParm parm = new TParm();
		// parm.setData(CommitData);
		//CommitData
		parm.setData("HIS_CODE", CommitData.get("HIS_CODE"));
		parm.setData("TRAN_NO", CommitData.get("TRAN_NO"));
		parm.setData("TRAN_TYPE", CommitData.get("TRAN_TYPE"));
		//OperateInfo
		parm.setData("OPT_ID", OperateInfo.get("OPT_ID"));
		parm.setData("OPT_NAME", OperateInfo.get("OPT_NAME"));
		parm.setData("OPT_IP", OperateInfo.get("OPT_IP"));
		parm.setData("HIS_DATE", OperateInfo.get("HIS_DATE"));
		parm.setData("GUID", OperateInfo.get("GUID"));
		parm.setData("TOKEN", OperateInfo.get("TOKEN"));

		// 调用JAVAHIS业务逻辑
		TSocket socket = new TSocket("192.168.1.113", 8081, "web");
		TParm resultData = new TParm();
		if (CommitData.get("TRAN_TYPE").toString().equals("0")) {
			resultData = TIOM_AppServer.executeAction(socket,
					"action.reg.REGCcbAction", "预约挂号撤销登记", parm);
		} else if (CommitData.get("TRAN_TYPE").toString().equals("1")) {
			resultData = TIOM_AppServer.executeAction(socket,
					"action.reg.REGCcbAction", "当日挂号取消", parm);
		} else if (CommitData.get("TRAN_TYPE").toString().equals("2")) {
			resultData = TIOM_AppServer.executeAction(socket,
					"action.reg.REGCcbAction", "bankCancelTran", parm);        
		} else {
			resultData = TIOM_AppServer.executeAction(socket,
					"action.reg.REGCcbAction", "缴费取消", parm);
		}

		Map<String, Object> rCommitData = new HashMap<String, Object>();

		Map<String, Object> rReturnData = new HashMap<String, Object>();

		Map<String, Object> rOperateInfo = new HashMap<String, Object>();
		rOperateInfo.put("METHOD", "BankCancelTran");
		rOperateInfo.put("OPT_ID", "");
		rOperateInfo.put("OPT_NAME", "");
		rOperateInfo.put("OPT_IP", "");
		rOperateInfo.put("HIS_DATE", "");
		rOperateInfo.put("GUID", "");
		rOperateInfo.put("TOKEN", "");

		Map<String, Object> rResult = new HashMap<String, Object>();
		rResult.put("EXECUTE_FLAG", resultData.getValue("EXECUTE_FLAG"));
		rResult.put("EXECUTE_MESSAGE", resultData.getValue("EXECUTE_MESSAGE"));

		Map<String, Map<String, Object>> returnMap = new HashMap<String, Map<String, Object>>();
		returnMap.put("CommitData", rCommitData);
		returnMap.put("ReturnData", rReturnData);
		returnMap.put("OperateInfo", rOperateInfo);
		returnMap.put("Result", rResult);

		TParm returnParm = new TParm(returnMap);
		return returnParm;

	}
	
	

}
