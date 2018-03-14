package action.erd;

import java.sql.Timestamp;
import java.util.Map;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.StringUtil;

import jdo.erd.ErdDynamicRcdTool;
import jdo.erd.ErdForBedAndRecordTool;
import jdo.reg.PatAdmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p>Title: �������۶�̬��¼Action</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-11-12
 * @version 1.0
 */
public class ERDDynamicRcdAction
extends TAction {
	public ERDDynamicRcdAction() {
	}

	/**
	 * ת��
	 * @param parm
	 * @return TParm
	 */
	public TParm onTransfer(TParm parm) {

		TParm result = new TParm();
		//����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
		TConnection connection = getConnection();

		result = ErdDynamicRcdTool.getInstance().onTransfer(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}

		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ���ô�
	 * @param parm
	 * @return TParm
	 */
	public TParm setBed(TParm parm) {

		TParm result = new TParm();
		//����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
		TConnection connection = getConnection();

		result = ErdDynamicRcdTool.getInstance().setErdBed(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		if(!parm.getBoolean("IORU_FLG")){
			result = ErdForBedAndRecordTool.getInstance().insertErdRecord(parm.
					getParm("PAT_INFO"), connection);
		}else{
			result = ErdForBedAndRecordTool.getInstance().updateErdRecord(parm.
					getParm("PAT_INFO"), connection);
		}
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}


		result = ErdDynamicRcdTool.getInstance().updateAdmStatus(parm,
				connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}

		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ת��
	 * @param parm
	 * @return TParm
	 */
	public TParm setPatRecord(TParm parm) {

		TParm result = new TParm();
		//����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
		TConnection connection = getConnection();

		result = ErdDynamicRcdTool.getInstance().setErdRecord(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		result = ErdDynamicRcdTool.getInstance().updateAdmStatus(parm,
				connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}

		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ���²���ERD_RECORD
	 * @param parm
	 * @return TParm
	 */
	public TParm updatePatRecord(TParm parm) {

		TParm result = new TParm();
		//����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
		TConnection connection = getConnection();

		result = ErdDynamicRcdTool.getInstance().updateErdRecord(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		result = ErdDynamicRcdTool.getInstance().updateAdmStatus(parm,
				connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		//add by huangtt 20151026 ����Erd_Evalution���е���Ժʱ��OutDate
		result = PatAdmTool.getInstance().updateErdEvalutionOutDate(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}

		//���������������ۺ��崲
		if (parm.getBoolean("OUT_FLG")) {
			result = ErdDynamicRcdTool.getInstance().clearErdBed(parm,
					connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
		}
		if (parm.getBoolean("RETURN_FLG")) {
			TParm inData = new TParm();
			inData.setData("ERD_REGION_CODE", parm.getData("ERD_REGION_CODE_R"));
			inData.setData("BED_NO", parm.getData("BED_NO_R"));
			inData.setData("CASE_NO",  parm.getData("CASE_NO"));
			inData.setData("MR_NO", parm.getData("MR_NO"));
			inData.setData("OCCUPY_FLG", "Y");
			inData.setData("OPT_USER", parm.getData("OPT_USER"));
			inData.setData("OPT_TERM", parm.getData("OPT_TERM"));
			result = ErdForBedAndRecordTool.getInstance().updateErdBed(inData,connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}




	//-----------add by wanging start ����ķ����Ѿ�����-----------------------------------
	/**
	 * �󶨴�λ 
	 * TRIAGE_NO��ERD_REGION_CODE��BED_NO
	 * @author wangqing 20171124
	 */
	public TParm setBedFinal(TParm parm){
		TConnection connection = getConnection();
		TParm result = new TParm();
		String sql;
		// ����ERD_BED
		sql = "UPDATE ERD_BED "
				+ "SET OCCUPY_FLG='Y', TRIAGE_NO='"+parm.getValue("TRIAGE_NO")+"' "
				// ����CASE_NO��MR_NO
				+ ", CASE_NO='"+parm.getValue("CASE_NO")+"', MR_NO='"+parm.getValue("MR_NO")+"' "
				+ "WHERE ERD_REGION_CODE='"+parm.getValue("ERD_REGION_CODE")+"' "
				+ "AND BED_NO='"+parm.getValue("BED_NO")+"' ";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// ����ERD_EVALUTION
		sql = "UPDATE ERD_EVALUTION "
				+ "SET ERD_REGION_CODE='"+parm.getValue("ERD_REGION_CODE")+"', BED_NO='"+parm.getValue("BED_NO")+"' "
				+ "WHERE TRIAGE_NO='"+parm.getValue("TRIAGE_NO")+"' ";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// ����AMI_E_S_RECORD
		sql = "INSERT INTO AMI_E_S_RECORD (TRIAGE_NO, ERD_REGION_CODE, BED_NO,S_M_TIME) "
				+ "VALUES('"+parm.getValue("TRIAGE_NO")+"', '"+parm.getValue("ERD_REGION_CODE")+"', '"+parm.getValue("BED_NO")+"', SYSDATE) ";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}		
		// add by wangqing 20180201 start
		// �봲����ʱ�������ʱ�����Ѿ��Һţ����ڸ���ERD_BED��ʱ��Ҳ�ø���CASE_NO��MR_NO��ͬʱ��ERD_RECORD���в���һ�����ݣ��������û�йҺţ�ά��ԭ�߼���
		Object o = parm.getData("erdP");
		if(o!=null && o instanceof Map){
			System.out.println("erdP="+new TParm((Map) o));
			result = ErdForBedAndRecordTool.getInstance().insertErdRecord(new TParm((Map) o), connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
		}
		// add by wangqing 20180201 end
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ת��
	 * @author wangqing 20171124
	 * @param parm
	 * @return
	 */
	public TParm onTransferFinal(TParm parm){
		TConnection connection = getConnection();
		TParm result = new TParm();
		String sql;	
		// ����ERD_BED
		sql = "UPDATE ERD_BED "
				+ "SET OCCUPY_FLG='N', TRIAGE_NO='' "
				// ת��ʱ������ERD_BED��CASE_NO��MR_NOΪnull
				+ ", CASE_NO='', MR_NO='' "
				+ "WHERE ERD_REGION_CODE='"+parm.getValue("ERD_REGION_CODE_FROM")+"' "
				+ "AND BED_NO='"+parm.getValue("BED_NO_FROM")+"' "
				+ "AND TRIAGE_NO='"+parm.getValue("TRIAGE_NO")+"' ";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		sql = "UPDATE ERD_BED "
				+ "SET OCCUPY_FLG='Y', TRIAGE_NO='"+parm.getValue("TRIAGE_NO")+"' "			
				// ����CASE_NO��MR_NO
				+ ", CASE_NO='"+parm.getValue("CASE_NO")+"', MR_NO='"+parm.getValue("MR_NO")+"' "				
				+ "WHERE ERD_REGION_CODE='"+parm.getValue("ERD_REGION_CODE_TO")+"' "
				+ "AND BED_NO='"+parm.getValue("BED_NO_TO")+"' ";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// ����ERD_EVALUTION
		sql = "UPDATE ERD_EVALUTION "
				+ "SET ERD_REGION_CODE='"+parm.getValue("ERD_REGION_CODE_TO")+"', BED_NO='"+parm.getValue("BED_NO_TO")+"' "
				+ "WHERE TRIAGE_NO='"+parm.getValue("TRIAGE_NO")+"' ";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// ����AMI_E_S_RECORD
		sql = "UPDATE AMI_E_S_RECORD "
				+ "SET E_M_TIME=SYSDATE "
				+ "WHERE TRIAGE_NO='"+parm.getValue("TRIAGE_NO")+"' "
				+ "AND ERD_REGION_CODE='"+parm.getValue("ERD_REGION_CODE_FROM")+"' "
				+ "AND BED_NO='"+parm.getValue("BED_NO_FROM")+"' "
				+ "AND E_M_TIME IS NULL ";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}    
		// ����AMI_E_S_RECORD
		sql = "INSERT INTO AMI_E_S_RECORD (TRIAGE_NO, ERD_REGION_CODE, BED_NO,S_M_TIME) "
				+ "VALUES('"+parm.getValue("TRIAGE_NO")+"', '"+parm.getValue("ERD_REGION_CODE_TO")+"', '"+parm.getValue("BED_NO_TO")+"', SYSDATE) ";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}		
		// add by wangqing 20180201 start
		// ת��ʱ������ԭERD_BED��CASE_NO��MR_NOΪnull;�������Ѿ��Һţ�������´�λERD_BED��CASE_NO��MR_NO����ERD_RECORDû�����ݣ������
		Object o = parm.getData("erdP");
		if(o!=null && o instanceof Map){
			result = ErdForBedAndRecordTool.getInstance().insertErdRecord(new TParm((Map) o), connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
		}
		// add by wangqing 20180201 end	
		connection.commit();
		connection.close();
		return result;      
	}

	/**
	 * ����ERD_RECORD
	 * @param parm
	 * @return
	 */
	public TParm insertErdRecordFinal(TParm parm){
		TParm result = new TParm();
		TConnection connection = getConnection();
		result = ErdForBedAndRecordTool.getInstance().insertErdRecord(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ���λ���ٻ�
	 * @param parm
	 * @return
	 */
	public TParm cancelBedFinal(TParm parm){
		TParm result = new TParm();
		String sql;
		TConnection connection = getConnection();
		if(parm.getValue("OUT_FLG")!=null && parm.getValue("OUT_FLG").equals("Y")){// ת������תסԺ
			// ����ERD_BED
			sql = "UPDATE ERD_BED "
					+ "SET OCCUPY_FLG='N', TRIAGE_NO='' "
					// ����ʱ������ERD_BED��CASE_NO��MR_NOΪnull
					+ ", CASE_NO='', MR_NO='' "
					+ "WHERE ERD_REGION_CODE='"+parm.getValue("ERD_REGION_CODE")+"' "
					+ "AND BED_NO='"+parm.getValue("BED_NO")+"' "
					+ "AND TRIAGE_NO='"+parm.getValue("TRIAGE_NO")+"' ";
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
			// ����ERD_EVALUTION
			sql = "UPDATE ERD_EVALUTION "
					+ "SET OUT_DATE=to_date('"+parm.getData("OUT_DATE_2")+"', 'yyyy/MM/dd HH24:MI:SS') "
					+ "WHERE TRIAGE_NO='"+parm.getValue("TRIAGE_NO")+"' ";
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
			// ����AMI_E_S_RECORD
			sql = "UPDATE AMI_E_S_RECORD "
					+ "SET E_M_TIME=to_date('"+parm.getData("OUT_DATE_2")+"', 'yyyy/MM/dd HH24:MI:SS') "
					+ "WHERE TRIAGE_NO='"+parm.getValue("TRIAGE_NO")+"' "
					+ "AND ERD_REGION_CODE='"+parm.getValue("ERD_REGION_CODE")+"' "
					+ "AND BED_NO='"+parm.getValue("BED_NO")+"' "
					+ "AND E_M_TIME IS NULL ";
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}   
		}
		else if(parm.getValue("RETURN_FLG")!=null && parm.getValue("RETURN_FLG").equals("Y")){// �ٻ�
			// ����ERD_BED
			sql = "UPDATE ERD_BED "
					+ "SET OCCUPY_FLG='Y', TRIAGE_NO='"+parm.getValue("TRIAGE_NO")+"' "
					// �ٻ�ʱ������ERD_BED��CASE_NO��MR_NO
					+ ", CASE_NO='"+parm.getValue("CASE_NO")+"', MR_NO='"+parm.getValue("MR_NO")+"' "
					+ "WHERE ERD_REGION_CODE='"+parm.getValue("ERD_REGION_CODE")+"' "
					+ "AND BED_NO='"+parm.getValue("BED_NO")+"' ";
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
			// ����ERD_EVALUTION
			sql = "UPDATE ERD_EVALUTION "
					+ "SET ERD_REGION_CODE='"+parm.getValue("ERD_REGION_CODE")+"', BED_NO='"+parm.getValue("BED_NO")+"', OUT_DATE='' "
					+ "WHERE TRIAGE_NO='"+parm.getValue("TRIAGE_NO")+"' ";
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
			// ����AMI_E_S_RECORD
			sql = "INSERT INTO AMI_E_S_RECORD (TRIAGE_NO, ERD_REGION_CODE, BED_NO,S_M_TIME) "
					+ "VALUES('"+parm.getValue("TRIAGE_NO")+"', '"+parm.getValue("ERD_REGION_CODE")+"', '"+parm.getValue("BED_NO")+"', to_date('"+parm.getValue("RETURN_DATE_2")+"', 'yyyy/MM/dd HH24:MI:SS'))";
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}	
		}
		// ����ERD_RECORD
		result = ErdDynamicRcdTool.getInstance().updateErdRecord(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}

	/**
	 * ����ERD_RECORD���Ҹ���ERD_BED
	 * �Һű���ʱ�����ERD_BED���ж�Ӧ��TRIAGE_NO�����ݣ������ERD_BED���CASE_NO��MR_NO��
	 * ͬʱ��ERD_RECORD���в���һ�����ݣ��൱�ڲ����ݣ�
	 * @param parm
	 * @return
	 */
	public TParm insertErdRecordAndUpdateErdBed(TParm parm){
		TParm result = new TParm();
		String sql;
		TConnection connection = getConnection();
		// ����ERD_RECORD
		result = ErdForBedAndRecordTool.getInstance().insertErdRecord(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}	
		// ����ERD_BED
		sql = "UPDATE ERD_BED "
				+ "SET CASE_NO='"+parm.getValue("CASE_NO")+"', MR_NO='"+parm.getValue("MR_NO")+"' "
				+ "WHERE ERD_REGION_CODE='"+parm.getValue("ERD_REGION")+"' "
				+ "AND BED_NO='"+parm.getValue("BED_NO")+"' ";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}		
		connection.commit();
		connection.close();
		return result;
	}


	//-----------add by wanging end-----------------------------------









}
