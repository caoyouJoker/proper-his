package jdo.mro;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import jdo.adm.ADMTransLogTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.javahis.util.OdiUtil;
import com.javahis.util.OdoUtil;

/**
 * <p>
 * Title: MRO�ӿڷ���(��̨Tool��ǰ��̨��Ҫ����)
 * </p>
 * 
 * <p>
 * Description: MRO�ӿڷ���(��̨Tool��ǰ��̨��Ҫ����)
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangk 2009-4-28
 * @version 1.0
 */
public class MROTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static MROTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return SYSRegionTool
	 */
	public static MROTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MROTool();
		return instanceObject;
	}

	public MROTool() {
		setModuleName("mro\\MROToolModule.x");
		onInit();
	}

	/**
	 * ת����ת����
	 * 
	 * @param parm
	 *            TParm ������Ϣ�� CASE_NO,OPT_USER,OPT_TERMΪ������������� ��ϲ�����ѡ ==>
	 *            TRANS_DEPT ת�ƿƱ� OUT_DATE ��Ժ���� OUT_DEPT ��Ժ�Ʊ� OUT_STATION ��Ժ����
	 *            OUT_ROOM_NO ��Ժ���� REAL_STAY_DAYS ʵ��סԺ����
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateTransDept(TParm parm, TConnection conn) {
		TParm result = new TParm();
		TParm nursParm=this.getIbsNusParm(parm);
		String sql = "UPDATE MRO_RECORD SET ";
		if (parm.getData("OUT_DATE") != null) // ��Ժ����
		{
			sql += " OUT_DATE=TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("OUT_DATE"),
							"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS'),";
			sql += " BILCHK_FLG='Y',";
		}
		if (parm.getData("IN_ROOM_NO") != null) // ��Ժ����
			sql += " IN_ROOM_NO='" + parm.getValue("IN_ROOM_NO") + "',";
		if (parm.getData("OUT_DEPT") != null) // ��Ժ�Ʊ�
			sql += " OUT_DEPT='" + parm.getValue("OUT_DEPT") + "',";
		if (parm.getData("OUT_STATION") != null) // ��Ժ����
			sql += " OUT_STATION='" + parm.getValue("OUT_STATION") + "',";
		if (parm.getData("OUT_ROOM_NO") != null) // ��Ժ����
			sql += " OUT_ROOM_NO='" + parm.getValue("OUT_ROOM_NO") + "',";
		if (parm.getData("REAL_STAY_DAYS") != null) // ʵ��סԺ����
			sql += " REAL_STAY_DAYS='" + parm.getValue("REAL_STAY_DAYS") + "',";
		if (!parm.getValue("VS_NURSE_CODE").equals("")) // ���λ�ʿ
			sql += " VS_NURSE_CODE='" + parm.getValue("VS_NURSE_CODE") + "',";
		if (!parm.getValue("DIRECTOR_DR_CODE").equals("")) // ������
			sql += " DIRECTOR_DR_CODE='" + parm.getValue("DIRECTOR_DR_CODE")+ "',";
		if (!parm.getValue("ATTEND_DR_CODE").equals("")) // ����ҽʦ
			sql += " ATTEND_DR_CODE='" + parm.getValue("ATTEND_DR_CODE") + "',";
		if (!parm.getValue("VS_DR_CODE").equals("")) // ����ҽʦ
			sql += " VS_DR_CODE='" + parm.getValue("VS_DR_CODE") + "',";
		if (!parm.getValue("CLNCPATH_CODE").equals("")) // �ٴ�·�� wanglong add 20140604
            sql += " CLNCPATH_CODE='" + parm.getValue("CLNCPATH_CODE") + "',";
		if (nursParm.getData("N0") != null) // �ؼ���������
			sql += " SPENURS_DAYS='" + nursParm.getValue("N0") + "',";
		if (nursParm.getData("N1") != null) // һ����������
			sql += " FIRNURS_DAYS='" + nursParm.getValue("N1") + "',";
		if (nursParm.getData("N2") != null) // ������������
			sql += " SECNURS_DAYS='" + nursParm.getValue("N2") + "',";
		if (nursParm.getData("N3") != null) // ������������
			sql += " THRNURS_DAYS='" + nursParm.getValue("N3") + "',";
		if (nursParm.getData("03") != null) // ������Сʱ
			sql += " VENTI_TIME=" + nursParm.getInt("03") + ",";
		sql += " OPT_USER='" + parm.getValue("OPT_USER") + "',OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "',OPT_DATE=SYSDATE ";
		if (parm.getData("CASE_NO") != null)
			sql += " WHERE CASE_NO='" + parm.getValue("CASE_NO") + "'";
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("jdo.mro.MROInterfaceTool.updateTransDept==>ERR:"
					+ result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * �õ��Ʒѷ��и�����������
	 * 
	 * @return
	 */
	public TParm getIbsNusParm(TParm inparm) {
		TParm result = new TParm();
		//��ʼ��
		TParm parm=new TParm();
		String caseNo = inparm.getValue("CASE_NO");
		String sql = " SELECT  SUM(A.TAKE_DAYS*A.DOSAGE_QTY) AS QTY,B.ORD_SUPERVISION FROM IBS_ORDD A,SYS_FEE B "
				+ " WHERE   A.CASE_NO='"
				+ caseNo
				+ "'"
				+ " AND A.ORDER_CODE=B.ORDER_CODE "
				+ " AND  B.ORD_SUPERVISION IN ('N0','N1','N2','N3','03')"
				+ " GROUP BY B.ORD_SUPERVISION";
		result=new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return parm;
		}
		if(result.getCount()<0)
			return parm;
		for(int i=0;i<result.getCount();i++){
			if(result.getValue("ORD_SUPERVISION", i).equals("N0")){
				parm.setData("N0", result.getInt("QTY", i));
			}else if(result.getValue("ORD_SUPERVISION", i).equals("N1")){
				parm.setData("N1", result.getInt("QTY", i));
			}else if(result.getValue("ORD_SUPERVISION", i).equals("N2")){
				parm.setData("N2", result.getInt("QTY", i));
			}else if(result.getValue("ORD_SUPERVISION", i).equals("N3")){
				parm.setData("N3", result.getInt("QTY", i));
			}else if(result.getValue("ORD_SUPERVISION", i).equals("03")){
				parm.setData("03", result.getInt("QTY", i));
			}
		}
		return parm;

	}
	/**
	 * �޸Ĳ������
	 * 
	 * @param parm
	 *            TParm ���������CASE_NO ����˵���� OE_DIAG_CODE:�ż������CODE
	 *            IN_DIAG_CODE����Ժ���CODE OUT_DIAG_CODE1����Ժ�����CODE
	 *            CODE1_REMARK����Ժ����ϱ�ע CODE1_STATUS����Ժ�����ת��״̬
	 *            OUT_DIAG_CODE2����Ժ���CODE CODE2_REMARK����Ժ��ϱ�ע
	 *            CODE2_STATUS����Ժ���ת��״̬ OUT_DIAG_CODE3����Ժ���CODE
	 *            CODE3_REMARK����Ժ��ϱ�ע CODE3_STATUS����Ժ���ת��״̬
	 *            OUT_DIAG_CODE4����Ժ���CODE CODE4_REMARK����Ժ��ϱ�ע
	 *            CODE4_STATUS����Ժ���ת��״̬ OUT_DIAG_CODE5����Ժ���CODE
	 *            CODE5_REMARK����Ժ��ϱ�ע CODE5_STATUS����Ժ���ת��״̬
	 *            OUT_DIAG_CODE6����Ժ���CODE CODE6_REMARK����Ժ��ϱ�ע
	 *            CODE6_STATUS����Ժ���ת��״̬ INTE_DIAG_CODE��Ժ�ڸ�Ⱦ���CODE
	 *            CASE_NO������case_no COMPLICATION_DIAG��Ժ�ڲ������CODE
	 * @return TParm
	 */
	public TParm updateMRODiag(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = MRORecordTool.getInstance().updateMRODiag(parm, conn);
		if (result.getErrCode() < 0) {
			err("jdo.mro.MROInterfaceTool.updateMRODiag==>ERR:"
					+ result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * �޸Ĳ������2 �����ٴ����ҳ���޸�����ͬ��������-duzhw add 20131017
	 * 
	 * @param parm
	 *            TParm ���������CASE_NO ����˵���� OE_DIAG_CODE:�ż������CODE
	 *            IN_DIAG_CODE����Ժ���CODE OUT_DIAG_CODE1����Ժ�����CODE
	 *            CODE1_REMARK����Ժ����ϱ�ע CODE1_STATUS����Ժ�����ת��״̬
	 *            OUT_DIAG_CODE2����Ժ���CODE CODE2_REMARK����Ժ��ϱ�ע
	 *            CODE2_STATUS����Ժ���ת��״̬ OUT_DIAG_CODE3����Ժ���CODE
	 *            CODE3_REMARK����Ժ��ϱ�ע CODE3_STATUS����Ժ���ת��״̬
	 *            OUT_DIAG_CODE4����Ժ���CODE CODE4_REMARK����Ժ��ϱ�ע
	 *            CODE4_STATUS����Ժ���ת��״̬ OUT_DIAG_CODE5����Ժ���CODE
	 *            CODE5_REMARK����Ժ��ϱ�ע CODE5_STATUS����Ժ���ת��״̬
	 *            OUT_DIAG_CODE6����Ժ���CODE CODE6_REMARK����Ժ��ϱ�ע
	 *            CODE6_STATUS����Ժ���ת��״̬ INTE_DIAG_CODE��Ժ�ڸ�Ⱦ���CODE
	 *            CASE_NO������case_no COMPLICATION_DIAG��Ժ�ڲ������CODE
	 * @return TParm
	 */
	public TParm updateMRODiag2(TParm parm, TConnection conn) {
		TParm result = new TParm();
		result = MRORecordTool.getInstance().updateMRODiag2(parm, conn);
		if (result.getErrCode() < 0) {
			err("jdo.mro.MROTool.updateMRODiag2==>ERR:"
					+ result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸� ҩƷ����
	 * 
	 * @param parm
	 *            TParm ������Ϣ�� CASE_NO,ALLEGIC,OPT_USER,OPT_TERMΪ�������
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateALLEGIC(TParm parm, TConnection conn) {
		TParm result = new TParm();
		String sql = "UPDATE MRO_RECORD SET ";
		if (parm.getData("ALLEGIC") != null)// ������¼
			sql += " ALLEGIC='" + parm.getValue("ALLEGIC") + "',";

		sql += " OPT_USER='" + parm.getValue("OPT_USER") + "',OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "',OPT_DATE=SYSDATE ";
		sql += " WHERE CASE_NO='" + parm.getValue("CASE_NO") + "'";
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("jdo.mro.MROInterfaceTool.updateALLEGIC==>ERR:"
					+ result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ���� ������Ϣ
	 * 
	 * @param <any>
	 *            TParm
	 * @return TParm
	 */
	public TParm insertOP(TParm parm, TConnection conn) {
		TParm result;
		result = MRORecordTool.getInstance().insertOP(parm, conn);
		return result;
	}

	/**
	 * �޸� ������Ϣ
	 * 
	 * @param parm
	 *            TParm ������Ϣ�� CASE_NO,SEQ_NO,OPT_USER,OPT_TERMΪ�������
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateOP(TParm parm, TConnection conn) {
		TParm result = new TParm();
		if (parm.getData("CASE_NO") == null || parm.getData("SEQ_NO") == null) {
			result.setErr(-1, "��Ҫ�������������ڣ�");
			return result;
		}
		String sql = "UPDATE MRO_RECORD_OP SET ";
		if (parm.getData("IPD_NO") != null)// סԺ��
			sql += " IPD_NO='" + parm.getValue("IPD_NO") + "',";
		if (parm.getData("MR_NO") != null)// ������
			sql += " MR_NO='" + parm.getValue("MR_NO") + "',";
		if (parm.getData("OP_CODE") != null)// ��������
			sql += " OP_CODE='" + parm.getValue("OP_CODE") + "',";
		if (parm.getData("OP_DESC") != null)// ��������
			sql += " OP_DESC='" + parm.getValue("OP_DESC") + "',";
		if (parm.getData("OP_REMARK") != null)// ������ע
			sql += " OP_REMARK='" + parm.getValue("OP_REMARK") + "',";
		if (parm.getData("OP_DATE") != null)// ����ʱ��
			sql += " OP_DATE=TO_DATE('" + parm.getValue("OP_DATE")
					+ "','YYYYMMDDHH24MISS'),";
		if (parm.getData("ANA_WAY") != null)// ����ʽ
			sql += " ANA_WAY='" + parm.getValue("ANA_WAY") + "',";
		if (parm.getData("ANA_DR") != null)// ����ҽʦ
			sql += " ANA_DR='" + parm.getValue("ANA_DR") + "',";
		if (parm.getData("MAIN_SUGEON") != null)// ����
			sql += " MAIN_SUGEON='" + parm.getValue("MAIN_SUGEON") + "',";
		if (parm.getData("AST_DR1") != null)// ����һ
			sql += " AST_DR1='" + parm.getValue("AST_DR1") + "',";
		if (parm.getData("AST_DR2") != null)// ������
			sql += " AST_DR2='" + parm.getValue("AST_DR2") + "',";
		if (parm.getData("HEALTH_LEVEL") != null)// �п����ϵȼ�
			sql += " HEALTH_LEVEL='" + parm.getValue("HEALTH_LEVEL") + "',";
		if (parm.getData("OP_LEVEL") != null)// �����ȼ�
			sql += " OP_LEVEL='" + parm.getValue("OP_LEVEL") + "',";
		if (parm.getData("OPT_USER") != null)
			sql += " OPT_USER='" + parm.getValue("OPT_USER") + "',";
		if (parm.getData("OPT_TERM") != null)
			sql += " OPT_TERM='" + parm.getValue("OPT_TERM") + "',";
		sql += " OPT_DATE=SYSDATE "; // �޸�ʱ��
		sql += " WHERE CASE_NO = '" + parm.getValue("CASE_NO")
				+ "' AND SEQ_NO = '" + parm.getValue("SEQ_NO") + "'";
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸� ��Ѫ��Ϣ
	 * 
	 * @param parm
	 *            TParm CASE_NO,OPT_USER,OPT_TERMΪ�������
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateBlood(TParm parm, TConnection conn) {
		TParm result = new TParm();
		String sql = "UPDATE MRO_RECORD SET ";
		if (parm.getData("BLOOD_TYPE") != null)// Ѫ��
			sql += " BLOOD_TYPE='" + parm.getValue("BLOOD_TYPE") + "',";
		if (parm.getData("RH_TYPE") != null)// RH
			sql += " RH_TYPE='" + parm.getValue("RH_TYPE") + "',";
		if (parm.getData("TRANS_REACTION") != null)// ��Ѫ��Ӧ
			sql += " TRANS_REACTION='" + parm.getValue("TRANS_REACTION") + "',";
		if (parm.getData("RBC") != null)// ��Ѫ��
			sql += " RBC='" + parm.getValue("RBC") + "',";
		if (parm.getData("PLATE") != null)// ѪС��
			sql += " PLATE='" + parm.getValue("PLATE") + "',";
		if (parm.getData("PLASMA") != null)// Ѫ��
			sql += " PLASMA='" + parm.getValue("PLASMA") + "',";
		if (parm.getData("WHOLE_BLOOD") != null)// ȫѪ
			sql += " WHOLE_BLOOD='" + parm.getValue("WHOLE_BLOOD") + "',";
		if (parm.getData("OTH_BLOOD") != null)// ����ѪƷ����
			sql += " OTH_BLOOD='" + parm.getValue("OTH_BLOOD") + "',";
		sql += " OPT_USER='" + parm.getValue("OPT_USER") + "',OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "',OPT_DATE=SYSDATE ";
		sql += " WHERE CASE_NO='" + parm.getValue("CASE_NO") + "'";
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸�������Ϣ
	 * 
	 * @param parm
	 *            TParm CASE_NO,OPT_USER,OPT_TERMΪ������� ����������ѡ==> CHARGE_01
	 *            ��λ��(ס);CHARGE_02 �����(ס);CHARGE_03 ��ҩ��(ס);CHARGE_04
	 *            �г�ҩ��(ס);CHARGE_05 �в�ҩ��(ס); CHARGE_06 �����(ס);CHARGE_07
	 *            �����(ס);CHARGE_08 ������(ס); CHARGE_09 ��Ѫ��(ס);CHARGE_10
	 *            ���Ʒ�(ס);CHARGE_11 ������(ס); CHARGE_12 ������(ס);CHARGE_13
	 *            ����(ס);CHARGE_14 �Ҵ���(ס); CHARGE_15 �����(ס);CHARGE_16
	 *            Ӥ����(ס);CHARGE_17 ����; CHARGE_18 �����ޣ�;CHARGE_19 �����ޣ�;CHARGE_20
	 *            �����ޣ�; ���������Ǹ��� SYS_DICTIONARY ������ GROUP_ID='MRO_CHARGE'
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateCharge(TParm parm, TConnection conn) {
		TParm result = new TParm();
		String sql = "UPDATE MRO_RECORD SET ";
		if (parm.getData("CHARGE_01") != null)// ��λ��(ס)
			sql += " CHARGE_01='" + parm.getValue("CHARGE_01") + "',";
		if (parm.getData("CHARGE_02") != null)// �����(ס)
			sql += " CHARGE_02='" + parm.getValue("CHARGE_02") + "',";
		if (parm.getData("CHARGE_03") != null)// ��ҩ��(ס)
			sql += " CHARGE_03='" + parm.getValue("CHARGE_03") + "',";
		if (parm.getData("CHARGE_04") != null)// �г�ҩ��(ס)
			sql += " CHARGE_04='" + parm.getValue("CHARGE_04") + "',";
		if (parm.getData("CHARGE_05") != null)// �в�ҩ��(ס)
			sql += " CHARGE_05='" + parm.getValue("CHARGE_05") + "',";
		if (parm.getData("CHARGE_06") != null)// �����(ס)
			sql += " CHARGE_06='" + parm.getValue("CHARGE_06") + "',";
		if (parm.getData("CHARGE_07") != null)// �����(ס)
			sql += " CHARGE_07='" + parm.getValue("CHARGE_07") + "',";
		if (parm.getData("CHARGE_08") != null)// ������(ס)
			sql += " CHARGE_08='" + parm.getValue("CHARGE_08") + "',";
		if (parm.getData("CHARGE_09") != null)// ��Ѫ��(ס)
			sql += " CHARGE_09='" + parm.getValue("CHARGE_09") + "',";
		if (parm.getData("CHARGE_10") != null)// ���Ʒ�(ס)
			sql += " CHARGE_10='" + parm.getValue("CHARGE_10") + "',";
		if (parm.getData("CHARGE_11") != null)// ������(ס)
			sql += " CHARGE_11='" + parm.getValue("CHARGE_11") + "',";
		if (parm.getData("CHARGE_12") != null)// ������(ס)
			sql += " CHARGE_12='" + parm.getValue("CHARGE_12") + "',";
		if (parm.getData("CHARGE_13") != null)// ����(ס)
			sql += " CHARGE_13='" + parm.getValue("CHARGE_13") + "',";
		if (parm.getData("CHARGE_14") != null)// �Ҵ���(ס)
			sql += " CHARGE_14='" + parm.getValue("CHARGE_14") + "',";
		if (parm.getData("CHARGE_15") != null)// �����(ס)
			sql += " CHARGE_15='" + parm.getValue("CHARGE_15") + "',";
		if (parm.getData("CHARGE_16") != null)// Ӥ����(ס)
			sql += " CHARGE_16='" + parm.getValue("CHARGE_16") + "',";
		if (parm.getData("CHARGE_17") != null)// ����
			sql += " CHARGE_17='" + parm.getValue("CHARGE_17") + "',";
		if (parm.getData("CHARGE_18") != null)
			sql += " CHARGE_18='" + parm.getValue("CHARGE_18") + "',";
		if (parm.getData("CHARGE_19") != null)
			sql += " CHARGE_19='" + parm.getValue("CHARGE_19") + "',";
		if (parm.getData("CHARGE_20") != null)
			sql += " CHARGE_20='" + parm.getValue("CHARGE_20") + "',";

		sql += " OPT_USER='" + parm.getValue("OPT_USER") + "',OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "',OPT_DATE=SYSDATE ";
		sql += " WHERE CASE_NO='" + parm.getValue("CASE_NO") + "'";
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * סԺ�Ǽ�ʱ���벡����ҳ���¼
	 * 
	 * @param parm
	 *            TParm MR_NO,IPD_NO,CASE_NO IN_DATE ��Ժ��; IN_DEPT ��Ժ�Ʊ�;
	 *            IN_STATION ��Ժ����; IN_ROOM_NO ��Ժ����; OE_DIAG_CODE �ż������;
	 *            IN_CONDITION ��Ժ���; PG_OWNER ��ҳ������;
	 * @return TParm
	 */
	public TParm insertMRORecord(TParm p, TConnection conn) {
		TParm result = new TParm();
		TParm parm = new TParm();
		Pat pat = Pat.onQueryByMrNo(p.getValue("MR_NO"));
		parm.setData("MR_NO", p.getValue("MR_NO"));
		parm.setData("IPD_NO", p.getValue("IPD_NO"));
		// Patinfoȡ�ò���
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("IDNO", pat.getIdNo());
		parm.setData("SEX", pat.getSexCode());
		parm.setData("BIRTH_DATE", pat.getBirthday());
		parm.setData("CASE_NO", p.getValue("CASE_NO"));
		parm.setData("MARRIGE", pat.getMarriageCode());
		parm.setData("AGE",
				OdoUtil.showAge(pat.getBirthday(), p.getTimestamp("IN_DATE")));// ��������
																				// �������պ���Ժʱ�����
		parm.setData("NATION", pat.getNationCode());
		parm.setData("FOLK", pat.getSpeciesCode());
		parm.setData("CTZ1_CODE", pat.getCtz1Code());
		parm.setData("H_TEL", pat.getTelHome());// ���ڵ绰 �õ� ��ͥ�绰
		parm.setData("H_ADDRESS", pat.getResidAddress());// ������ַ
		parm.setData("H_POSTNO", pat.getResidPostCode());// �����ʱ�
		parm.setData("OCCUPATION", pat.getOccCode());// ְҵ
		parm.setData("OFFICE", pat.getCompanyDesc());// ��λ
		parm.setData("O_TEL", pat.getTelCompany());// ��λ�绰
		parm.setData("O_ADDRESS", "");// ��λ��ַ
		parm.setData("O_POSTNO", "");// ��λ�绰
		parm.setData("CONTACTER", pat.getContactsName());// ��ϵ��
		parm.setData("RELATIONSHIP", pat.getRelationCode());// ��ϵ�˹�ϵ
		parm.setData("CONT_TEL", pat.getContactsTel());// ��ϵ�˵绰
		parm.setData("CONT_ADDRESS", pat.getContactsAddress());// ��ϵ�˵�ַ
		parm.setData("HOEMPLACE_CODE", pat.gethomePlaceCode());// �����ش���
		// סԺ�ǼǴ������
		parm.setData("IN_DATE", p.getData("IN_DATE"));// ��Ժ��
		parm.setData("IN_DEPT", p.getData("IN_DEPT"));// ��Ժ�Ʊ�
		parm.setData("IN_STATION", p.getData("IN_STATION"));// ��Ժ����
		parm.setData("IN_ROOM_NO", p.getData("IN_ROOM_NO"));// ��Ժ����
		parm.setData("OE_DIAG_CODE", p.getData("OE_DIAG_CODE"));// �ż������
		parm.setData("IN_CONDITION", p.getData("IN_CONDITION"));// ��Ժ���
		parm.setData("PG_OWNER", p.getData("PG_OWNER"));// ��ҳ������

		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());
		result = this.update("insertPatInfo", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ������ҳ��Ϣ
	 * 
	 * @param parm
	 *            TParm ������CASE_NO��MR_NO��OPT_USER,OPT_TERM;HOSP_ID
	 * @return TParm
	 */
	public TParm insertMRO(TParm parm) {
		TParm result = MRORecordTool.getInstance().insertMRO(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸�סԺ��Ϣ��סԺ�Ǽǽӿڣ�
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateADMData(TParm parm) {
		TParm result = MRORecordTool.getInstance().updateADMData(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸���ҳ����������Ϣ��סԺ����ӿڣ�
	 * 
	 * @param parm
	 *            TParm �������룺MR_NO��CASE_NO��OPT_USER��OPT_TERM
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateMROPatInfo(TParm p) {
		TParm result = MRORecordTool.getInstance().updateMROPatInfo(p);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �޸Ĳ�������Ժ����
	 * 
	 * @param roomCode
	 *            String
	 * @param caseNo
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateInRoom(String roomCode, String caseNo, TConnection conn) {
		String sql = "UPDATE MRO_RECORD SET IN_ROOM_NO = '" + roomCode
				+ "' WHERE CASE_NO='" + caseNo + "' AND IN_ROOM_NO IS NULL";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �õ�ת�ƿ���
	 * 
	 * @param parm
	 * @return TParm
	 */
	public TParm getTranDept(TParm parm) {
		TParm tranParm = new TParm();
		// ת������
		TParm opeParm = ADMTransLogTool.getInstance().getTranHospFormro(parm);
		// // TParm iNAmdParm = ADMTransLogTool.getInstance()
		// // .getOldTranDeptData(parm);
		// //��Ժ����
		// String ComPstr = opeParm.getValue("CASE_NO",0)
		// + iNAmdParm.getValue("IN_DATE", 0);
		int count = 0;
        for (int i = opeParm.getCount() - 1; i >= 0; i--) {//wanglong modify 20150115
			// String deptStr = opeParm.getValue("IN_DEPT_CODE", i);
			// String str = opeParm.getValue("CASE_NO", i)
			// + opeParm.getValue("IN_DATE", i);
			// if(str.equals(ComPstr))
			// continue;
			// ������ ת�� ������� ICU(��֢�໤����) NOR(�ٴ����ң� ע,OPE(��������)����ת�� ��δʵ�֣�
			tranParm.setData("TRAN_DEPT", count,
					opeParm.getData("OUT_DEPT_CODE", i));//wanglong modify 20150115
			count++;
		}
		tranParm.setCount(count);
		return tranParm;
	}

	/**
	 * �õ�����ҩ���������
	 * 
	 * @param mrNo
	 *            String
	 * @return TParm
	 */
	public TParm getDrugAllErgy(String mrNo) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT A.CASE_NO,A.MR_NO,A.DRUG_TYPE,");
		sbSql.append(" CASE A.DRUG_TYPE ");
		sbSql.append("   WHEN 'A' THEN (SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE B.GROUP_ID = 'PHA_INGREDIENT' AND B.ID = A.DRUGORINGRD_CODE) ");
		sbSql.append("   WHEN 'B' THEN (SELECT B.ORDER_DESC FROM SYS_FEE B WHERE B.ORDER_CODE = A.DRUGORINGRD_CODE) ");
		sbSql.append("   WHEN 'D' THEN (SELECT B.CATEGORY_CHN_DESC FROM SYS_CATEGORY B WHERE RULE_TYPE = 'PHA_RULE' AND B.CATEGORY_CODE = A.DRUGORINGRD_CODE) ");
		sbSql.append("   WHEN 'E' THEN (SELECT B.CATEGORY_CHN_DESC FROM SYS_CATEGORY B WHERE RULE_TYPE = 'PHA_RULE' AND B.CATEGORY_CODE = A.DRUGORINGRD_CODE) ");
		sbSql.append(" END AS ALLERGY_NAME,A.ALLERGY_NOTE,OPT_DATE ");
		sbSql.append(" FROM OPD_DRUGALLERGY A ");
		sbSql.append(" WHERE A.DRUG_TYPE IN ('A','B','D','E') ");
		sbSql.append(" AND A.MR_NO = '");
		sbSql.append(mrNo);
		sbSql.append("' ORDER BY A.ADM_DATE, A.OPT_DATE ");
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �õ���֢�໤��Ϣ
	 * 
	 * @param caseNo
	 * @return
	 */
	public TParm getICUParm(String caseNo) {
		String sql = "SELECT TO_DATE(A.IN_DATE,'YYYY/MM/DD HH24:MI:SS') IN_DATE,A.OUT_DATE,A.IN_DEPT_CODE AS DEPT_CODE "
				+ " FROM ADM_TRANS_LOG A, SYS_DEPT B "
				+ " WHERE A.IN_DEPT_CODE=B.DEPT_CODE "
				+ " AND B.ICU_TYPE IS NOT NULL"
				+ " AND A.CASE_NO='"
				+ caseNo
				+ "' AND A.PSF_KIND IS NOT NULL  " + " ORDER BY A.IN_DATE ASC ";
		TParm result = new TParm(this.getDBTool().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
	
	/**
	 * ��ѯ������ҳ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryHomePageInfo(TParm parm) {
		TParm result = new TParm();
		String sql = "SELECT * FROM EMR_FILE_INDEX WHERE 1 = 1 ";
		if (StringUtils.isEmpty(parm.getValue("CASE_NO"))
				&& StringUtils.isEmpty(parm.getValue("CASE_NO_LIST"))) {
			result.setErr(-1, "���Ϊ��");
			return result;
		}
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sql = sql + " AND CASE_NO = '" + parm.getValue("CASE_NO") + "' ";
		}
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO_LIST"))) {
			sql = sql + " AND CASE_NO IN (" + parm.getValue("CASE_NO_LIST")
					+ ") ";
		}

		sql = sql + " AND FILE_NAME like '%������ҳ%' ORDER BY CASE_NO,CREATOR_DATE DESC";

		result = new TParm(this.getDBTool().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ��ѯ������ҳ�ϲ���Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryMergeHomePageInfo(TParm parm) {
		TParm result = new TParm();
		String sql = "SELECT * FROM MRO_MRV_TECH WHERE HP_MERGE_CODE IS NOT NULL ";
		if (StringUtils.isEmpty(parm.getValue("CASE_NO"))
				&& StringUtils.isEmpty(parm.getValue("CASE_NO_LIST"))) {
			result.setErr(-1, "���Ϊ��");
			return result;
		}
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sql = sql + " AND CASE_NO = '" + parm.getValue("CASE_NO") + "' ";
		}
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO_LIST"))) {
			sql = sql + " AND CASE_NO IN (" + parm.getValue("CASE_NO_LIST") + ") ";
		}
		
		result = new TParm(this.getDBTool().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ���²�����ҳ�ϲ���Ϣ
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateMergeHomePageInfo(TParm parm) {
		TParm result = new TParm();
		StringBuffer sbSql = new StringBuffer();

		if (StringUtils.isEmpty(parm.getValue("CASE_NO"))
				&& StringUtils.isEmpty(parm.getValue("CASE_NO_LIST"))) {
			result.setErr(-1, "���Ϊ��");
			return result;
		}

		sbSql.append("UPDATE MRO_MRV_TECH SET HP_MERGE_CODE = '");
		sbSql.append(parm.getValue("HP_MERGE_CODE"));
		sbSql.append("',HP_MERGE_DATE = ");
		sbSql.append(parm.getData("HP_MERGE_DATE"));
		sbSql.append(",OPT_USER = '");
		sbSql.append(parm.getValue("OPT_USER"));
		sbSql.append("',OPT_DATE = SYSDATE,OPT_TERM = '");
		sbSql.append(parm.getValue("OPT_TERM"));
		sbSql.append("' WHERE 1 = 1 ");

		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ");
		}
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO_LIST"))) {
			sbSql.append(" AND CASE_NO IN (");
			sbSql.append(parm.getValue("CASE_NO_LIST"));
			sbSql.append(") ");
		}
		
		result = new TParm(this.getDBTool().update(sbSql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ��ѯ�⼮��ԱסԺ����
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm queryForeignerData(TParm parm) {
		TParm result = new TParm();
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT A.MR_NO,B.PAT_NAME,B.SEX_CODE,B.BIRTH_DATE,A.IN_DATE,");
		sbSql.append("A.DS_DATE,A.VS_DR_CODE,A.DEPT_CODE,C.ICD_CODE,D.ICD_CHN_DESC");
		sbSql.append(" FROM ADM_INP A,SYS_PATINFO B,ADM_INPDIAG C,SYS_DIAGNOSIS D ");
		sbSql.append(" WHERE A.MR_NO = B.MR_NO AND A.CANCEL_FLG = 'N' ");
		sbSql.append(" AND A.CASE_NO = C.CASE_NO(+) AND C.IO_TYPE(+) = 'O' AND C.MAINDIAG_FLG(+) = 'Y' ");
		sbSql.append(" AND C.ICD_CODE = D.ICD_CODE(+) AND NATION_CODE IS NOT NULL AND NATION_CODE <> '86' ");
		
		sbSql.append(" AND A.IN_DATE >= TO_DATE('");
		sbSql.append(parm.getValue("START_DATE"));
		sbSql.append(" 00:00:00','YYYY/MM/DD HH24:MI:SS') AND A.IN_DATE <= TO_DATE('");
		sbSql.append(parm.getValue("END_DATE"));
		sbSql.append(" 23:59:59','YYYY/MM/DD HH24:MI:SS')");
		
		if (StringUtils.isNotEmpty(parm.getValue("DEPT_CODE"))) {
			sbSql.append(" AND A.DEPT_CODE = '");
			sbSql.append(parm.getValue("DEPT_CODE"));
			sbSql.append("' ");
		}
		
		// סԺ״̬
		if ("IN".equals(parm.getValue("STATUS"))) {
			sbSql.append(" AND A.DS_DATE IS NULL ");
		} else if ("OUT".equals(parm.getValue("STATUS"))) {
			sbSql.append(" AND A.DS_DATE IS NOT NULL ");
		}
		
		sbSql.append(" ORDER BY IN_DATE,MR_NO ");
		
		result = new TParm(this.getDBTool().select(sbSql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		int count = result.getCount();
		// ����
		String age = "";
		// סԺ����
		String inDays = "";
		int days = 0;
		Timestamp sysDate = SystemTool.getInstance().getDate();
		
		for (int i = 0; i < count; i++) {
			// ��������
			if (null != result.getTimestamp("IN_DATE", i)
					&& null != result.getTimestamp("BIRTH_DATE", i)) {
				age = OdiUtil.showAge(result.getTimestamp("BIRTH_DATE", i),
						result.getTimestamp("IN_DATE", i));
			} else {
				age = "";
			}
			
			result.addData("AGE", age);
			
			// ������Ժ����
			if (null == result.getTimestamp("IN_DATE", i)) {
				inDays = "";
			} else if (null != result.getTimestamp("DS_DATE", i)) {
				days = StringTool
						.getDateDiffer(StringTool.setTime(result.getTimestamp(
								"DS_DATE", i), "00:00:00"), StringTool.setTime(
								result.getTimestamp("IN_DATE", i), "00:00:00"));
				if (days == 0) {
					inDays = "1";
				} else {
					inDays = String.valueOf(days);
				}
			} else if (null == result.getTimestamp("DS_DATE", i)) {
				days = StringTool.getDateDiffer(StringTool.setTime(sysDate,
						"00:00:00"), StringTool.setTime(result.getTimestamp(
						"IN_DATE", i), "00:00:00"));
				if (days == 0) {
					inDays = "1";
				} else {
					inDays = String.valueOf(days);
				}
			}
			
			result.addData("IN_DAYS", inDays);
		}
		return result;
	}
	
	/**
	 * ��ѯCCPC-AMI��Ժ����
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm queryCcpcAmiData(TParm parm) {
		TParm result = new TParm();
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT A.CASE_NO,A.MR_NO,B.PAT_NAME,B.SEX_CODE,B.BIRTH_DATE,A.IN_DATE,");
		sbSql.append("A.DS_DATE,A.VS_DR_CODE,A.DEPT_CODE,C.ICD_CODE,D.ICD_CHN_DESC,");
		sbSql.append("G.TOT_AMT,H.DRUG_TOT_AMT,I.MATERIAL_TOT_AMT ");
		sbSql.append(" FROM ADM_INP A,SYS_PATINFO B,ADM_INPDIAG C,SYS_DIAGNOSIS D, ADM_RESV E, REG_PATADM F, ");
		// ��ѯ�ܷ���
		sbSql.append("(SELECT CASE_NO, SUM (TOT_AMT) AS TOT_AMT FROM IBS_ORDD GROUP BY CASE_NO) G,");
		// ��ѯҩƷ����
		sbSql.append("(SELECT CASE_NO, SUM (TOT_AMT) AS DRUG_TOT_AMT FROM IBS_ORDD WHERE REXP_CODE IN ('022.01', '022.02', '023') GROUP BY CASE_NO) H,");
		// ��ѯ���Ϸ���
		sbSql.append("(SELECT CASE_NO, SUM (TOT_AMT) AS MATERIAL_TOT_AMT FROM IBS_ORDD WHERE REXP_CODE IN ('035') GROUP BY CASE_NO) I ");
		
		sbSql.append(" WHERE A.MR_NO = B.MR_NO AND A.DS_DATE IS NOT NULL AND A.CANCEL_FLG = 'N' ");
		sbSql.append(" AND A.CASE_NO = E.IN_CASE_NO AND E.OPD_CASE_NO = F.CASE_NO AND F.PATH_KIND = 'P01' ");
		sbSql.append(" AND A.CASE_NO = G.CASE_NO(+) AND A.CASE_NO = H.CASE_NO(+) AND A.CASE_NO = I.CASE_NO(+) ");
		sbSql.append(" AND A.CASE_NO = C.CASE_NO(+) AND C.IO_TYPE(+) = 'O' AND C.MAINDIAG_FLG(+) = 'Y' ");
		sbSql.append(" AND C.ICD_CODE = D.ICD_CODE(+) ");
		
		sbSql.append(" AND A.DS_DATE >= TO_DATE('");
		sbSql.append(parm.getValue("START_DATE"));
		sbSql.append(" 00:00:00','YYYY/MM/DD HH24:MI:SS') AND A.DS_DATE <= TO_DATE('");
		sbSql.append(parm.getValue("END_DATE"));
		sbSql.append(" 23:59:59','YYYY/MM/DD HH24:MI:SS')");
		
		if (StringUtils.isNotEmpty(parm.getValue("DEPT_CODE"))) {
			sbSql.append(" AND A.DEPT_CODE = '");
			sbSql.append(parm.getValue("DEPT_CODE"));
			sbSql.append("' ");
		}
		
		sbSql.append(" ORDER BY A.DS_DATE,A.CASE_NO ");
		
		result = new TParm(this.getDBTool().select(sbSql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		int count = result.getCount();
		// ����
		String age = "";
		// סԺ����
		String inDays = "";
		int days = 0;
		Timestamp sysDate = SystemTool.getInstance().getDate();
		
		for (int i = 0; i < count; i++) {
			// ��������
			if (null != result.getTimestamp("IN_DATE", i)
					&& null != result.getTimestamp("BIRTH_DATE", i)) {
				age = OdiUtil.showAge(result.getTimestamp("BIRTH_DATE", i),
						result.getTimestamp("IN_DATE", i));
			} else {
				age = "";
			}
			
			result.addData("AGE", age);
			
			// ������Ժ����
			if (null == result.getTimestamp("IN_DATE", i)) {
				inDays = "";
			} else if (null != result.getTimestamp("DS_DATE", i)) {
				days = StringTool
						.getDateDiffer(StringTool.setTime(result.getTimestamp(
								"DS_DATE", i), "00:00:00"), StringTool.setTime(
								result.getTimestamp("IN_DATE", i), "00:00:00"));
				if (days == 0) {
					inDays = "1";
				} else {
					inDays = String.valueOf(days);
				}
			} else if (null == result.getTimestamp("DS_DATE", i)) {
				days = StringTool.getDateDiffer(StringTool.setTime(sysDate,
						"00:00:00"), StringTool.setTime(result.getTimestamp(
						"IN_DATE", i), "00:00:00"));
				if (days == 0) {
					inDays = "1";
				} else {
					inDays = String.valueOf(days);
				}
			}
			
			result.addData("IN_DAYS", inDays);
		}
		return result;
	}
}
