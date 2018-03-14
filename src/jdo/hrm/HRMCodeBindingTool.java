/**
 * 
 */
package jdo.hrm;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title: ��װ���չ���</p>
 *
 * <p>Description: ��װ����</p>
 *
 * <p>Copyright: Copyright (c) 2016</p>
 *
 * <p>Company: JavaHis </p>
 *
 *
 * @author Guangl
 * @version 1.0
 */
public class HRMCodeBindingTool extends TJDOTool {
	
	private static HRMCodeBindingTool instance = null;
	
	public static HRMCodeBindingTool getInstance() {
		if (instance == null) {
			instance = new HRMCodeBindingTool();
		}
		return instance;
	}
	
	//ͨ��ɨ���his���룬��ȡ���������������߱���Լ���������
	public TParm getOPDCodeByApplicationCode(String application_code){
		String sql = "SELECT (A.CASE_NO) AS CASE_NO , (A.MR_NO) AS MR_NO , (A.PAT_NAME) AS PAT_NAME , " +
					"(B.OPD_CASE_NO) AS OPD_CASE_NO , (B.RECRUIT_NO) AS RECRUIT_NO , (A.OSC1) AS OSC1 , (A.OSC2) AS OSC2 " +
					"FROM MED_APPLY A , ADM_RESV B " +
					"WHERE A.CASE_NO = B.IN_CASE_NO " +
					"AND A.CAT1_TYPE = 'LIS' " +
					"AND A.APPLICATION_NO = '" + application_code +"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		
		return result;
		
	}
	//ͨ���������Ż�ȡ��������Լ���������
	public TParm getPlanInfoByOPDCode(String case_no){
		String sql = "SELECT A.PLAN_NO ,A.PLAN_DESC " +
					"FROM HRM_CONTRACTD A ,HRM_PATADM B " +
					"WHERE A.MR_NO = B.MR_NO " +
					"AND A.CONTRACT_CODE = B.CONTRACT_CODE " +
					"AND B.CASE_NO = '" + case_no + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		
		return result; 
	}
	
	/**
	 * ȡ�Ļ�ʿ�ƻ�ִ��ʱ��
	 * @param application_no ���뵥��
	 * @param case_no �����
	 * @return result
	 */
	public TParm getPlanExecTime(String application_no , String case_no){
		String sql = "SELECT DR_NOTE FROM ODI_ORDER WHERE CASE_NO = '"+case_no+"' AND MED_APPLY_NO = '"+application_no+"' AND SETMAIN_FLG = 'Y'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result; 
	}
	
	public boolean updateOSC(String application_no , String osc1 , String osc2 ){
		String sql = "UPDATE MED_APPLY SET " +
				"OSC1 = '" + osc1 + "' , " +
						"OSC2 = '" + osc2 + "' " +
								"WHERE APPLICATION_NO = '" + application_no + "' " +
										"AND CAT1_TYPE = 'LIS' " ;
		TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
		if("1".equals(parm.getValue("RETURN"))){
			return true;
		}else{
			return false;
		}
		
	}
	
	public boolean insertOSCLog(String application_no , String osc1 , String osc2 ){
		String user = Operator.getID();
		String ip = Operator.getIP();
		String sql = "INSERT INTO ODI_CODEBINDING_LOG " +
				"VALUES ('"+ application_no +"','" + osc1 +"','" + osc2 + "','"+ user+"',(SELECT SYSDATE FROM DUAL),'"+ip+"')";
		TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
		if("1".equals(parm.getValue("RETURN"))){
			return true;
		}else{
			return false;
		}
	}
	
}
