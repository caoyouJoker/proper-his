package jdo.bms;

import org.apache.commons.lang.StringUtils;

import jdo.sys.Operator;
import jdo.sys.PatTool;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: Ѫ�����
 * </p>
 *
 * <p>
 * Description: Ѫ�����
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
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class BMSTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static BMSTool instanceObject;

	/**
	 * �õ�ʵ��
	 *
	 * @return BMSBloodTool
	 */
	public static BMSTool getInstance() {
		if (instanceObject == null)
			instanceObject = new BMSTool();
		return instanceObject;
	}

	/**
	 * ѪҺ���(����)
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onBMSBloodInInsert(TParm parm, TConnection conn) {
		// ����ѪҺ��Ϣ
		TParm result = BMSBloodTool.getInstance().onInsert(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		// ����ѪҺ���
		TParm inparm = BMSBldStockTool.getInstance().onQuery(parm);
		if (inparm.getCount("BLD_CODE") > 0) {
			parm.setData("SAFE_QTY", inparm.getData("SAFE_QTY", 0));
			parm.setData("CURR_QTY", inparm.getDouble("CURR_QTY", 0) + 1);
			parm.setData("ACC_QTY", inparm.getDouble("ACC_QTY", 0) + 1);
			parm.setData("SAFE_VOL", inparm.getData("SAFE_VOL", 0));
			parm.setData(
					"TOT_VOL",
					inparm.getDouble("TOT_VOL", 0)
							+ parm.getDouble("BLOOD_VOL"));
			parm.setData(
					"ACC_VOL",
					inparm.getDouble("ACC_VOL", 0)
							+ parm.getDouble("BLOOD_VOL"));

			result = BMSBldStockTool.getInstance().onUpdate(parm, conn);
		} else {
			parm.setData("SAFE_QTY", 0);
			parm.setData("CURR_QTY", 1);
			parm.setData("ACC_QTY", 1);
			parm.setData("SAFE_VOL", 0);
			parm.setData("TOT_VOL", parm.getData("BLOOD_VOL"));
			parm.setData("ACC_VOL", parm.getData("BLOOD_VOL"));

			result = BMSBldStockTool.getInstance().onInsert(parm, conn);
		}
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ɾ��ѪҺ
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onBMSBloodInDelete(TParm parm, TConnection conn) {
		// ɾ��ѪҺ��Ϣ
		TParm result = BMSBloodTool.getInstance().onDelete(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		// ����ѪҺ���
		TParm inparm = BMSBldStockTool.getInstance().onQuery(parm);
		parm.setData("SAFE_QTY", inparm.getData("SAFE_QTY", 0));
		parm.setData("CURR_QTY", inparm.getDouble("CURR_QTY", 0) - 1);
		parm.setData("ACC_QTY", inparm.getDouble("ACC_QTY", 0) - 1);
		parm.setData("SAFE_VOL", inparm.getData("SAFE_VOL", 0));
		parm.setData("TOT_VOL",
				inparm.getDouble("TOT_VOL", 0) - parm.getDouble("BLOOD_VOL"));
		parm.setData("ACC_VOL",
				inparm.getDouble("ACC_VOL", 0) - parm.getDouble("BLOOD_VOL"));

		result = BMSBldStockTool.getInstance().onUpdate(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * �����¼��ѯ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQueryPat(TParm parm) {
		TParm result = new TParm();
		TParm resultOE = BMSQueryPatTool.getInstance().onQueryOE(parm);
		TParm resultI = BMSQueryPatTool.getInstance().onQueryI(parm);
		String status_desc = "";
		if ("O".equals(parm.getValue("IO_TYPE"))
				|| "E".equals(parm.getValue("IO_TYPE"))) {
			for (int i = 0; i < resultOE.getCount("ADM_TYPE"); i++) {
				result.addData("ADM_TYPE", resultOE.getValue("ADM_TYPE", i));
				result.addData("IN_DATE", resultOE.getValue("ADM_DATE", i));
				result.addData("CASE_NO", resultOE.getValue("CASE_NO", i));
				result.addData("USER_NAME", resultOE.getValue("USER_NAME", i));
				result.addData("DEPT_DESC",
						resultOE.getValue("DEPT_CHN_DESC", i));
				result.addData("PAT_NAME", resultOE.getValue("PAT_NAME", i));
				result.addData("CTZ_DESC", resultOE.getValue("CTZ_DESC", i));
				result.addData("OUT_DATE", resultOE.getValue("ADM_DATE", i));
				result.addData("ID_NO", resultOE.getValue("IDNO", i));
				result.addData("MR_NO", resultOE.getValue("MR_NO", i));
				if (!"".equals(resultOE.getValue("REGCAN_DATE", i))) {
					status_desc = "���˹�";
				} else if ("Y".equals(resultOE.getValue("SEE_DR_FLG", i))) {
					status_desc = "�ѿ���";
				} else if ("T".equals(resultOE.getValue("SEE_DR_FLG", i))) {
					status_desc = "����ݴ�";
				} else {
					status_desc = "δ����";
				}
				result.addData("STATUS", status_desc);
				result.addData("DEPT_CODE", resultOE.getValue("DEPT_CODE", i));
				result.addData("CTZ1_CODE", resultOE.getValue("CTZ1_CODE", i));
				result.addData("BED_NO", "");
				result.addData("IPD_NO", "");
				result.addData("BLOOD_RH_TYPE",
						resultOE.getValue("BLOOD_RH_TYPE", i));
				result.addData("BLOOD_TYPE", resultOE.getValue("BLOOD_TYPE", i));
			}
		} else if ("I".equals(parm.getValue("IO_TYPE"))) {
			for (int i = 0; i < resultI.getCount("ADM_TYPE"); i++) {
				result.addData("ADM_TYPE", resultI.getValue("ADM_TYPE", i));
				result.addData("IN_DATE", resultI.getValue("IN_DATE", i));
				result.addData("CASE_NO", resultI.getValue("CASE_NO", i));
				result.addData("USER_NAME", resultI.getValue("USER_NAME", i));
				result.addData("DEPT_DESC",
						resultI.getValue("DEPT_CHN_DESC", i));
				result.addData("PAT_NAME", resultI.getValue("PAT_NAME", i));
				result.addData("CTZ_DESC", resultI.getValue("CTZ_DESC", i));
				result.addData("OUT_DATE", resultI.getValue("DS_DATE", i));
				result.addData("ID_NO", resultI.getValue("IDNO", i));
				result.addData("MR_NO", resultI.getValue("MR_NO", i));
				if ("Y".equals(resultI.getValue("CANCEL_FLG", i))) {
					status_desc = "ȡ��סԺ";
				} else if ("2".equals(resultI.getValue("DS_DATE", i))) {
					status_desc = "סԺ��";
				} else {
					status_desc = "��Ժ";
				}
				result.addData("STATUS", status_desc);
				result.addData("DEPT_CODE", resultI.getValue("DEPT_CODE", i));
				result.addData("CTZ1_CODE", resultI.getValue("CTZ1_CODE", i));
				result.addData("BED_NO", resultI.getValue("BED_NO", i));
				result.addData("IPD_NO", resultI.getValue("IPD_NO", i));
				result.addData("BLOOD_RH_TYPE",
						resultI.getValue("BLOOD_RH_TYPE", i));
				result.addData("BLOOD_TYPE", resultI.getValue("BLOOD_TYPE", i));
			}
		} else {
			for (int i = 0; i < resultOE.getCount("ADM_TYPE"); i++) {
				result.addData("ADM_TYPE", resultOE.getValue("ADM_TYPE", i));
				result.addData("IN_DATE", resultOE.getValue("ADM_DATE", i));
				result.addData("CASE_NO", resultOE.getValue("CASE_NO", i));
				result.addData("USER_NAME", resultOE.getValue("USER_NAME", i));
				result.addData("DEPT_DESC",
						resultOE.getValue("DEPT_CHN_DESC", i));
				result.addData("PAT_NAME", resultOE.getValue("PAT_NAME", i));
				result.addData("CTZ_DESC", resultOE.getValue("CTZ_DESC", i));
				result.addData("OUT_DATE", resultOE.getValue("ADM_DATE", i));
				result.addData("ID_NO", resultOE.getValue("IDNO", i));
				result.addData("MR_NO", resultOE.getValue("MR_NO", i));
				if (!"".equals(resultOE.getValue("REGCAN_DATE", i))) {
					status_desc = "���˹�";
				} else if ("Y".equals(resultOE.getValue("SEE_DR_FLG", i))) {
					status_desc = "�ѿ���";
				} else if ("T".equals(resultOE.getValue("SEE_DR_FLG", i))) {
					status_desc = "����ݴ�";
				} else {
					status_desc = "δ����";
				}
				result.addData("STATUS", status_desc);
				result.addData("DEPT_CODE", resultOE.getValue("DEPT_CODE", i));
				result.addData("CTZ1_CODE", resultOE.getValue("CTZ1_CODE", i));
				result.addData("BED_NO", "");
				result.addData("IPD_NO", "");
				result.addData("BLOOD_RH_TYPE",
						resultOE.getValue("BLOOD_RH_TYPE", i));
				result.addData("BLOOD_TYPE", resultOE.getValue("BLOOD_TYPE", i));
			}
			for (int i = 0; i < resultI.getCount("ADM_TYPE"); i++) {
				result.addData("ADM_TYPE", resultI.getValue("ADM_TYPE", i));
				result.addData("IN_DATE", resultI.getValue("IN_DATE", i));
				result.addData("CASE_NO", resultI.getValue("CASE_NO", i));
				result.addData("USER_NAME", resultI.getValue("USER_NAME", i));
				result.addData("DEPT_DESC",
						resultI.getValue("DEPT_CHN_DESC", i));
				result.addData("PAT_NAME", resultI.getValue("PAT_NAME", i));
				result.addData("CTZ_DESC", resultI.getValue("CTZ_DESC", i));
				result.addData("OUT_DATE", resultI.getValue("DS_DATE", i));
				result.addData("ID_NO", resultI.getValue("IDNO", i));
				result.addData("MR_NO", resultI.getValue("MR_NO", i));
				if ("Y".equals(resultI.getValue("CANCEL_FLG", i))) {
					status_desc = "ȡ��סԺ";
				} else if ("2".equals(resultI.getValue("DS_DATE", i))) {
					status_desc = "סԺ��";
				} else {
					status_desc = "��Ժ";
				}
				result.addData("STATUS", status_desc);
				result.addData("DEPT_CODE", resultI.getValue("DEPT_CODE", i));
				result.addData("CTZ1_CODE", resultI.getValue("CTZ1_CODE", i));
				result.addData("BED_NO", resultI.getValue("BED_NO", i));
				result.addData("IPD_NO", resultI.getValue("IPD_NO", i));
				result.addData("BLOOD_RH_TYPE",
						resultI.getValue("BLOOD_RH_TYPE", i));
				result.addData("BLOOD_TYPE", resultI.getValue("BLOOD_TYPE", i));
			}
		}
		return result;
	}

	/**
	 * ��Ѫ���뵥(����)
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onInsertBMSApply(TParm parm, TConnection conn) {
		// ������Ѫ���뵥����
		TParm result = BMSApplyMTool.getInstance().onApplyInsert(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// ������Ѫ���뵥ϸ��
		TParm parmD = parm.getParm("BMS_APPLYD");
		for (int i = 0; i < parmD.getCount("BLD_CODE"); i++) {
			TParm inparm = new TParm();
			inparm.setData("APPLY_NO", parm.getData("APPLY_NO"));
			inparm.setData("BLD_CODE", parmD.getData("BLD_CODE", i));
			inparm.setData("APPLY_QTY", parmD.getData("APPLY_QTY", i));
			inparm.setData("UNIT_CODE", parmD.getData("UNIT_CODE", i));
			inparm.setData("PRE_DATE", parm.getData("PRE_DATE"));
			inparm.setData("OPT_USER", parm.getData("OPT_USER"));
			inparm.setData("OPT_DATE", parm.getData("OPT_DATE"));
			inparm.setData("OPT_TERM", parm.getData("OPT_TERM"));
			result = BMSApplyDTool.getInstance().onApplyInsert(inparm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		// ���²���Ѫ��.
		result = PatTool.getInstance().updatePatBldType(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��Ѫ���뵥(����)
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdateBMSApply(TParm parm, TConnection conn) {
		// ���±�Ѫ���뵥����
		TParm result = BMSApplyMTool.getInstance().onApplyUpdate(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// ɾ����Ѫ���뵥ϸ��
		result = BMSApplyDTool.getInstance().onApplyDelete(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// ������Ѫ���뵥ϸ��
		TParm parmD = parm.getParm("BMS_APPLYD");
		for (int i = 0; i < parmD.getCount("BLD_CODE"); i++) {
			TParm inparm = new TParm();
			inparm.setData("APPLY_NO", parm.getData("APPLY_NO"));
			inparm.setData("BLD_CODE", parmD.getData("BLD_CODE", i));
			inparm.setData("APPLY_QTY", parmD.getData("APPLY_QTY", i));
			inparm.setData("UNIT_CODE", parmD.getData("UNIT_CODE", i));
			inparm.setData("PRE_DATE", parm.getData("PRE_DATE"));
			inparm.setData("OPT_USER", parm.getData("OPT_USER"));
			inparm.setData("OPT_DATE", parm.getData("OPT_DATE"));
			inparm.setData("OPT_TERM", parm.getData("OPT_TERM"));
			result = BMSApplyDTool.getInstance().onApplyInsert(inparm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		// ���²���Ѫ��.
		result = PatTool.getInstance().updatePatBldType(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��Ѫ���뵥(ɾ��)
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onDeleteBMSApply(TParm parm, TConnection conn) {
		// ɾ����Ѫ���뵥����
		TParm result = BMSApplyMTool.getInstance().onApplyDelete(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// ɾ����Ѫ���뵥ϸ��
		result = BMSApplyDTool.getInstance().onApplyDelete(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ��Ѫ����ѯ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQueryBMSApply(TParm parm) {
		TParm result = new TParm();
		TParm resultM = BMSApplyMTool.getInstance().onApplyQuery(parm);
		result.setData("BMS_APPLYM", resultM.getData());
		TParm resultD = BMSApplyDTool.getInstance().onApplyQuery(parm);
		result.setData("BMS_APPLYD", resultD.getData());
		return result;
	}

	/**
	 * ���±�Ѫ����Ϣ,���²���Ѫ��
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdatePatCheckInfo(TParm parm, TConnection conn) {
		// ���±�Ѫ����Ϣ(��������)
		TParm result = BMSApplyMTool.getInstance().onUpdatePatCheckInfo(parm,
				conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// ���²���Ѫ��
		String mr_no = parm.getValue("MR_NO");
		String blood_type = parm.getValue("BLOOD_TYPE");
		String rh = parm.getValue("RH_FLG");
		String sql = "UPDATE SYS_PATINFO SET BLOOD_TYPE='" + blood_type
				+ "' , BLOOD_RH_TYPE='" + rh + "' WHERE MR_NO = '" + mr_no
				+ "'";
		result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// System.out.println("��Ѫ��"+parm.getData("BLOOD_TYPE"));
		// System.out.println("��Ѫ��"+parm.getData("BLOOD_TYPE_OLD"));
		if (parm.getData("BLOOD_TYPE") != null
				&& parm.getValue("BLOOD_TYPE").length() > 0
				&& !parm.getValue("BLOOD_TYPE").equals(
						parm.getValue("BLOOD_TYPE_OLD"))) {
			// String oldBlood = "";
			// TNull tnull = new TNull(String.class);
			// if(parm.getData("BLOOD_TYPE_OLD")!=null)
			// oldBlood= parm.getValue("BLOOD_TYPE_OLD");
			// else
			// oldBlood = tnull;
			String patSql = "INSERT INTO SYS_PATLOG "
					+ "            (MR_NO, OPT_DATE, MODI_ITEM, ITEM_OLD, ITEM_NEW, "
					+ "            OPT_USER, OPT_TERM ) "
					+ "     VALUES ('"
					+ mr_no
					+ "', TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("OPT_DATE"),
							"yyyyMMddHHmmss") + "','yyyyMMddHH24miss') , "
					+ "             'Ѫ��', '" + parm.getValue("BLOOD_TYPE_OLD")
					+ "', '" + parm.getValue("BLOOD_TYPE") + "', '"
					+ parm.getValue("OPT_USER") + "', '"
					+ parm.getValue("OPT_TERM") + "')";
			// System.out.println("patSql"+patSql);
			result = new TParm(TJDODBTool.getInstance().update(patSql, conn));
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * ������ҳ�ӿ� ���ݾ������,�����ź�סԺ�Ż�ò�����Ѫ��Ϣ(��ϸ��,ѪС��,Ѫ��,ȫѪ,����,��Ѫ��Ӧ)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getApplyInfo(TParm parm) {
		TParm result = new TParm();
		result.setData("RBC", 0);
		result.setData("PLATE", 0);
		result.setData("PLASMA", 0);
		result.setData("WHOLE_BLOOD", 0);
		result.setData("OTH_BLOOD", 0);
		result.setData("TRANS_REACTION", "3");
		// ��ϸ��,ѪС��,Ѫ��,ȫѪ,����
		TParm infoParm = BMSBloodTool.getInstance().getApplyInfo(parm);
		if (infoParm == null || infoParm.getCount() <= 0) {
			return result;
		}
		for (int i = 0; i < infoParm.getCount(); i++) {
			if ("01".equals(infoParm.getValue("FRONTPG_TYPE", i))) {
				result.setData("RBC", infoParm.getDouble("BLOOD_VOL", i));
			} else if ("02".equals(infoParm.getValue("FRONTPG_TYPE", i))) {
				result.setData("PLATE", infoParm.getDouble("BLOOD_VOL", i));
			} else if ("03".equals(infoParm.getValue("FRONTPG_TYPE", i))) {
				result.setData("PLASMA", infoParm.getDouble("BLOOD_VOL", i));
			} else if ("04".equals(infoParm.getValue("FRONTPG_TYPE", i))) {
				result.setData("WHOLE_BLOOD",
						infoParm.getDouble("BLOOD_VOL", i));
			} else if ("05".equals(infoParm.getValue("FRONTPG_TYPE", i))) {
				result.setData("OTH_BLOOD", infoParm.getDouble("BLOOD_VOL", i));
			}
		}
		// ��Ѫ��Ӧ
		TParm transParm = BMSSplrectTool.getInstance().onQueryTransReaction(
				parm);
		if (transParm == null || transParm.getCount() <= 0) {
			result.setData("TRANS_REACTION", "3");
		} else if (!"".equals(transParm.getValue("REACTION_CODE", 0))) {
			result.setData("TRANS_REACTION", "1");
		} else {
			result.setData("TRANS_REACTION", "2");
		}
		return result;
	}

	/**
	 * ���±�Ѫ����Ϣ(������Ѫ)
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdateBloodCross(TParm parm, TConnection conn) {
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("BLOOD_NO"); i++) {
			result = BMSBloodTool.getInstance().onUpdateBloodCross(
					parm.getRow(i), conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * ���±�Ѫ����Ϣ(ѪƷ����)
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onUpdateBloodOut(TParm parm, TConnection conn) {
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("BLOOD_NO"); i++) {
			// ���±�Ѫ����Ϣ
			result = BMSBloodTool.getInstance().onUpdateBloodOut(
					parm.getRow(i), conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			// ���¿��
			result = BMSBldStockTool.getInstance().onUpdateOut(parm.getRow(i),
					conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * ����ѪƷ��������ѪƷ���
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onSaveBldSubcat(TParm parm, TConnection conn) {
		TParm result = new TParm();
		String[] tableM = (String[]) parm.getData("TABLE_M");
		result = new TParm(TJDODBTool.getInstance().update(tableM, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		if (parm.existData("TABLE_D")) {
			result = new TParm(TJDODBTool.getInstance().update(
					(String[]) parm.getData("TABLE_D"), conn));
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * ����ѪƷ���
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onUpdateBldSubcat(TParm parm, TConnection conn) {
		TParm result = new TParm();

		result = new TParm(TJDODBTool.getInstance().update(
				(String[]) parm.getData("TABLE_D"), conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * ɾ��ѪƷ
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onDeleteBldSubcat(TParm parm, TConnection conn) {
		TParm result = new TParm();
		String[] tableM = (String[]) parm.getData("TABLE_M");
		result = new TParm(TJDODBTool.getInstance().update(tableM, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		if (parm.existData("TABLE_D")) {
			result = new TParm(TJDODBTool.getInstance().update(
					(String[]) parm.getData("TABLE_D"), conn));
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	/**
	 * ҽ��չ��
	 * 
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm onBmsTranOrderDspn(TParm parm, TConnection conn) {
		TParm result = new TParm();
		String caseNo=parm.getValue("CASE_NO");
		String admsql="SELECT REGION_CODE,STATION_CODE,DEPT_CODE,VS_DR_CODE,BED_NO,"
				+ "IPD_NO,MR_NO FROM ADM_INP WHERE CASE_NO='"+caseNo+"'";
		TParm admParm=new TParm(TJDODBTool.getInstance().select(admsql));
		if(admParm.getCount()<=0){
			result.setErr(-1, "û�в�ѯ������");
		}
		TParm orderParm=parm.getParm("BLOOD_LIST");
		
		return result;
	}

	/**
	 * ҽ������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getBmsOrder(TParm parm, TParm sysFeeParm) {
		TParm order = new TParm();
		order.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		// ��������
		order.setData("STATION_CODE", parm.getValue("STATION_CODE"));
		// ����
		order.setData("DEPT_CODE", parm.getValue("DEPT_CODE"));
		// ����ҽʦ
		order.setData("VS_DR_CODE", parm.getValue("VS_DR_CODE"));
		// ��λ��
		order.setData("BED_NO", parm.getValue("BED_NO"));
		// סԺ��
		order.setData("IPD_NO", parm.getValue("IPD_NO"));
		// ������
		order.setData("MR_NO", parm.getValue("MR_NO"));
		// �ݴ�ע��
		order.setData("TEMPORARY_FLG", "N");
		// ҽ��״̬
		order.setData("ORDER_STATE", "N");
		// ����ҽ������
		order.setData("LINKMAIN_FLG", "N");
		// Ӣ������
		order.setData("ORDER_ENG_DESC", sysFeeParm.getValue("TRADE_ENG_DESC"));
		// ����ҽ��
		order.setData("LINK_NO", "");
		// ҽ������
		order.setData("ORDER_CODE", sysFeeParm.getValue("ORDER_CODE"));
		// ҽ������
		order.setData("ORDER_DESC", sysFeeParm.getValue("ORDER_DESC"));
		// ҽ�����ƣ���ʾ��
		order.setData(
				"ORDER_DESCCHN",
				sysFeeParm.getValue("ORDER_DESC")
						+ sysFeeParm.getValue("GOODS_DESC")
						+ sysFeeParm.getValue("DESCRIPTION")
						+ sysFeeParm.getValue("SPECIFICATION"));
		// ��Ʒ��
		order.setData("GOODS_DESC", sysFeeParm.getValue("GOODS_DESC"));
		// ���
		order.setData("SPECIFICATION", sysFeeParm.getValue("SPECIFICATION"));
		// ��ҩ����
		if (!("PHA".equals(sysFeeParm.getValue("CAT1_TYPE")))) {
			order.setData("MEDI_QTY", 1);
		} else {
			order.setData("MEDI_QTY", sysFeeParm.getData("MEDI_QTY"));
		}
		order.setData("ROUTE_CODE", sysFeeParm.getValue("ROUTE_CODE"));
		order.setData("TAKE_DAYS", 1);
		order.setData("DOSAGE_QTY", 0);
		// ��ҩ��λ
		order.setData("DOSAGE_UNIT", sysFeeParm.getData("UNIT_CODE"));
		// ��ҩ����
		order.setData("DISPENSE_QTY", 0);
		// ��ҩ��λ
		order.setData("DISPENSE_UNIT", sysFeeParm.getData("DISPENSE_UNIT"));
		// �з�ҩע��
		order.setData("GIVEBOX_FLG", "N");
		// ����ע��
		order.setData("CONTINUOUS_FLG", "N");
		// ������
		order.setData("ACUMDSPN_QTY", 0);
		// �����ҩ��
		order.setData("LASTDSPN_QTY", 0);
		// ҽ��Ԥ����������

		order.setData("EFF_DATE", TJDODBTool.getInstance().getDBTime());
		// ��������
		order.setData("ORDER_DEPT_CODE", parm.getValue("DEPT_CODE"));
		// ����ҽʦ
		order.setData("ORDER_DR_CODE", parm.getValue("VS_DR_CODE"));
		// ִ�п���
		order.setData("EXEC_DEPT_CODE", parm.getValue("DEPT_CODE"));
		// ִ�м�ʦ
		order.setData("EXEC_DR_CODE", "");
		// ͣ�ÿ���
		order.setData("DC_DEPT_CODE", "");
		// ͣ��ҽʦ
		order.setData("DC_DR_CODE", "");
		// ͣ��ʱ��
		order.setData("DC_DATE", "");
		// ͣ��ԭ�����
		order.setData("DC_RSN_CODE", "");
		// ҽʦ��ע
		order.setData("DR_NOTE", "");
		// ��ʿ��ע
		order.setData("NS_NOTE", "");
		// �������
		order.setData("INSPAY_TYPE", sysFeeParm.getData("INSPAY_TYPE"));
		// ����ҩƷ����
		order.setData("CTRLDRUGCLASS_CODE", "");
		// �����ش���
		order.setData("ANTIBIOTIC_CODE", "");
		// ����ǩ��(��ҩʹ��)
		order.setData("RX_NO", "");
		// ����(��ҩʹ��)
		order.setData("PRESRT_NO", 0);
		// ҩƷ����
		order.setData("PHA_TYPE", "");
		// �������
		order.setData("DOSE_TYPE", "");
		// ��Ƭ������
		order.setData("DCT_TAKE_QTY", 0);
		// ��ҩ��ʽ(��ҩ����)
		order.setData("DCTAGENT_CODE", "");
		// ���������(��ҩ����)
		order.setData("PACKAGE_AMT", 0);
		// ����ҽ������ע��
		order.setData("SETMAIN_FLG", sysFeeParm.getData("ORDERSET_FLG"));
		// ��˻�ʿ
		order.setData("NS_CHECK_CODE", "");
		// ��������ע��
		order.setData("INDV_FLG", sysFeeParm.getData("INDV_FLG"));
		// ����ע��(����ҽ��ϸ���)
		Object objHide = parm.getData("HIDE_FLG");
		if (objHide != null) {
			order.setData("HIDE_FLG", sysFeeParm.getData("HIDE_FLG"));
		} else {
			order.setData("HIDE_FLG", "N");
		}
		// ����ҽ��˳���
		order.setData("ORDERSET_GROUP_NO", 0);
		// ����ҽ���������
		order.setData("ORDERSET_CODE", "");
		// ҽ��ϸ����
		order.setData("ORDER_CAT1_CODE", sysFeeParm.getData("ORDER_CAT1_CODE"));
		// ҽ��������
		order.setData("CAT1_TYPE", sysFeeParm.getData("CAT1_TYPE"));
		// �������
		order.setData("RPTTYPE_CODE", sysFeeParm.getData("RPTTYPE_CODE"));
		// ������
		order.setData("OPTITEM_CODE", sysFeeParm.getData("OPTITEM_CODE"));
		// ���뵥���
		order.setData("MR_CODE", sysFeeParm.getData("MR_CODE"));
		// FILE_NO
		order.setData("FILE_NO", "");
		// ��Ч����
		order.setData("DEGREE_CODE", sysFeeParm.getData("DEGREE_CODE"));
		// ��ʿ���ʱ��
		order.setData("NS_CHECK_DATE", "");
		// ��˻�ʿDCȷ��
		order.setData("DC_NS_CHECK_CODE", "");
		// ��˻�ʿDCȷ��ʱ��
		order.setData("DC_NS_CHECK_DATE", "");
		// �ײ�����ʱ��
		order.setData("START_DTTM", StringTool.getTimestamp(StringTool
				.getString(TJDODBTool.getInstance().getDBTime(),
						"yyyyMMddHHmmss"), "yyyyMMddHHmmss"));
		// ҽ��ִ��ʱ��(����)
		order.setData("ORDER_DATETIME", StringTool.getString(
				order.getTimestamp("START_DTTM"), "HHmmss"));
		// �����ҩ����
		order.setData("LAST_DSPN_DATE", "");
		// �����ҩ��(�������������)
		order.setData("FRST_QTY", 0);
		// ���ҩʦ
		order.setData("PHA_CHECK_CODE", "");
		// ���ʱ��
		order.setData("PHA_CHECK_DATE", "");
		// ������Һ����
		order.setData("INJ_ORG_CODE", "");
		// ����
		order.setData("URGENT_FLG", "N");
		// �ۼƿ�ҩ��
		if ("PHA".equals(sysFeeParm.getValue("CAT1_TYPE"))) {
			order.setData("ACUMMEDI_QTY", 0);
		} else {
			order.setData("ACUMMEDI_QTY", 1);
		}
		// ҽ������
		order.setData("DEV_CODE", sysFeeParm.getData("DEV_CODE"));
		// ��ҩע��
		order.setData("DISPENSE_FLG", "N");
		// ҽ����ע,ҩƷ��ע
		order.setData("IS_REMARK", sysFeeParm.getData("IS_REMARK"));
		return order;
	}
    
    /*
     * ��Ѫִ��
     * */
    public TParm onTranBlood(TParm parm, TConnection conn){
    	TParm result = new TParm();  
    	
    	String bloodNo = parm.getValue("BLOOD_NO");
    	String time = parm.getValue("BLDTRANS_TIME");
    	String user = parm.getValue("BLDTRANS_USER");
    	int factVol = parm.getInt("FACT_VOL");
    	String sql = " UPDATE " +
    					" BMS_BLOOD " +
    				" SET " +
    					" BLDTRANS_USER = '"+user+"', " + 
    					" BLDTRANS_TIME = TO_DATE('" +time+ "', 'YYYYMMDDHH24MISS')," +
    					" FACT_VOL = '"+factVol+"' " + 
    				" WHERE " +  
    					" BLOOD_NO = '"+bloodNo+"'";  

        result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        if (result.getErrCode() < 0) {
        	err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
            return result;
        }
        return result ;
    }
    
    /*
     * ��Ѫִ��
     * */
    public TParm onTranBlood2(TParm parm, TConnection conn){
    	TParm result = new TParm();  
    	
    	String bloodNo = parm.getValue("BLOOD_NO");
//    	String time = parm.getValue("BLDTRANS_TIME");
    	String user = parm.getValue("BLDTRANS_END_USER");
    	int factVol = parm.getInt("FACT_VOL");
    	String sql = " UPDATE " +
    					" BMS_BLOOD " +
    				" SET " +
    					" BLDTRANS_END_USER = '"+user+"', " + 
    					" BLDTRANS_END_TIME = SYSDATE," +
    					" FACT_VOL = '"+factVol+"', " + 
    					" OPT_USER = '"+user+"', " +
    					" OPT_TERM = '"+parm.getValue("OPT_TERM")+"', " +
    					" OPT_DATE = SYSDATE, " +
    					" TRANSFUSION_REACTION = '"+parm.getValue("TRANSFUSION_REACTION")+"' " +
    				" WHERE " +  
    					" BLOOD_NO = '"+bloodNo+"'";  

        result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        if (result.getErrCode() < 0) {
        	err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
            return result;
        }
        return result ;
    }
    
    /*
     * ����order��
     * */
    public TParm onUpdateOrderForBL(TParm parm, TConnection conn){
    	TParm result = new TParm();  
    	
    	String bloodNo = parm.getValue("BLOOD_NO");
    	String user = parm.getValue("BLDTRANS_USER");
    	int factVol = parm.getInt("FACT_VOL");
    	String sql = " UPDATE ODI_ORDER SET " +
//    					" CASE_NO = '"+parm.getValue("CASE_NO")+"', " + 
//    					" ORDER_NO = '" +parm.getValue("ORDER_NO")+ "'," +
//    					" ORDER_SEQ = '"+parm.getValue("ORDER_SEQ")+"', " + 
//    					" REGION_CODE = '"+parm.getValue("REGION_CODE")+"', " + 
//    					" STATION_CODE = '"+parm.getValue("STATION_CODE")+"', " + 
//    					" DEPT_CODE = '"+parm.getValue("DEPT_CODE")+"', " + 
//    					" BED_NO = '"+parm.getValue("BED_NO")+"', " + 
//    					" RX_KIND = '"+parm.getValue("RX_KIND")+"', " + 
//    					" IPD_NO = '"+parm.getValue("IPD_NO")+"', " +
//    					" MR_NO = '"+parm.getValue("MR_NO")+"', " +
//    					" ORDER_CODE = '"+parm.getValue("ORDER_CODE")+"', " +
//    					" ORDER_DESC = '"+parm.getValue("ORDER_DESC")+"', " +
    					" DISPENSE_QTY = '"+factVol+"', " +
    					" DISPENSE_UNIT = '"+parm.getValue("DISPENSE_UNIT")+"', " +
    					" OPT_USER = '"+user+"', " +
    					" OPT_TERM = '"+parm.getValue("OPT_TERM")+"', " +
    					" OPT_DATE = SYSDATE" +
    				" WHERE " +  
    					" BLOOD_NO = '"+bloodNo+"' AND CASE_NO = '"+parm.getValue("CASE_NO")+"' ";  

        result = new TParm(TJDODBTool.getInstance().update(sql, conn));
        if (result.getErrCode() < 0) {
        	err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
            return result;
        }
        return result ;
    }
    /*
     * ����order��
     * */
    public TParm onInsertOrderForBL(TParm parm, TConnection conn){
    	TParm result = new TParm();  
    	String user = parm.getValue("BLDTRANS_USER");
    	int factVol = parm.getInt("FACT_VOL");
    	
    	String insertSql = "Insert into JAVAHIS.ODI_ORDER "
    			+ " (CASE_NO, ORDER_NO, ORDER_SEQ, REGION_CODE, STATION_CODE, "
    			+ " DEPT_CODE, BED_NO, IPD_NO, MR_NO, RX_KIND, "
    			+ " TEMPORARY_FLG, ORDER_STATE, LINKMAIN_FLG,ORDER_CODE,ORDER_DESC, DISPENSE_QTY, DISPENSE_UNIT, "
    			+ " GIVEBOX_FLG, CONTINUOUS_FLG, ORDER_DR_CODE, INSPAY_TYPE, PHA_TYPE, "
    			+ " DOSE_TYPE, URGENT_FLG, OPT_USER, OPT_DATE, OPT_TERM, "
    			+ " DISPENSE_FLG, RELEASE_FLG, EXEC_FLG, TAKEMED_ORG, BLOOD_NO) "
    			+ " Values "
    			+ " ('"+ parm.getValue("CASE_NO")+"', '"+ parm.getValue("ORDER_NO")+ "', '"+parm.getValue("ORDER_SEQ")+"', '"+parm.getValue("REGION_CODE")+"', '"+parm.getValue("STATION_CODE")+"', "
    			+ " '"+parm.getValue("DEPT_CODE")+"', '"+parm.getValue("BED_NO")+"', '"+parm.getValue("IPD_NO")+"', '"+parm.getValue("MR_NO")+"', '"+parm.getValue("RX_KIND")+"', "
    			+ " 'N', 'N', 'N', '"+parm.getValue("ORDER_CODE")+"', '"+parm.getValue("ORDER_DESC")+"', '"+factVol+"', '"+parm.getValue("DISPENSE_UNIT")+"', "
    			+ " 'N', 'N', '1', 'C', 'W', "
    			+ " 'O', 'N', '"+user+"', SYSDATE, '"+parm.getValue("OPT_TERM")+"', "
    			+ " 'N', 'N', 'N', '2', '"+parm.getValue("BLOOD_NO")+"')";
    	
        result = new TParm(TJDODBTool.getInstance().update(insertSql, conn));
        if (result.getErrCode() < 0) {
        	err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
            return result;
        }
        return result ;
    }
    
    /**
     * ��ѯѪ�ͼ������
     * 
     * @param parm
     * @return result
     */
    public TParm queryBmsLisData(TParm parm) {
    	StringBuffer sbSql = new StringBuffer();
    	sbSql.append("SELECT A.CASE_NO,A.MR_NO,B.TESTITEM_CODE,B.TESTITEM_CHN_DESC,B.TEST_VALUE ");
    	sbSql.append(" FROM MED_APPLY A, MED_LIS_RPT B ");
    	sbSql.append(" WHERE A.CAT1_TYPE = 'LIS' AND A.STATUS <> 9 AND A.STATUS <> 10 ");
    	sbSql.append(" AND A.CAT1_TYPE = B.CAT1_TYPE AND A.APPLICATION_NO = B.APPLICATION_NO ");
    	sbSql.append(" AND B.TEST_VALUE IS NOT NULL AND A.CASE_NO = '");
    	sbSql.append(parm.getValue("CASE_NO"));
    	sbSql.append("' AND B.TESTITEM_CODE IN ('1000081','1000082') ");
    	sbSql.append(" ORDER BY A.APPLICATION_NO DESC,B.TESTITEM_CODE ");
    	
    	TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
        if (result.getErrCode() < 0) {
        	err("ERR:" + result.getErrCode() + result.getErrText()
            + result.getErrName());
            return result;
        }
        return result ;
    }
    
    // -------------------------add by wangqing start--------------------------
    /**
     * �޸����״̬
     * @param parm
     * @param conn
     * @return
     */
    public TParm onCheckBMSApply(TParm parm, TConnection conn){
    	String sql = " UPDATE BMS_APPLYM SET CHECK_FLG='"+parm.getValue("CHECK_FLG")+"', CHECK_USER='"+parm.getValue("CHECK_USER")+"', CHECK_DATE=SYSDATE WHERE APPLY_NO='"+parm.getValue("APPLY_NO")+"' ";
    	TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
    	return result; 	
    }
    
    // ------------------------ add by wangqing end----------------------------
    
    /**
     * ����LISѪ�ͼ������ͬ��������Ϣ��Ѫ��
     * 
     * @param mrNo ������
     * @return result
     */
    public TParm updatePatBloodByLisData(String mrNo) {
    	StringBuffer sbSql = new StringBuffer();
    	sbSql.append("SELECT A.CASE_NO,A.MR_NO,B.TESTITEM_CODE,B.TESTITEM_CHN_DESC,B.TEST_VALUE,C.BLOOD_TYPE,C.BLOOD_RH_TYPE ");
    	sbSql.append(" FROM MED_APPLY A, MED_LIS_RPT B, SYS_PATINFO C");
    	sbSql.append(" WHERE A.CAT1_TYPE = 'LIS' AND A.STATUS <> 9 AND A.STATUS <> 10 ");
    	sbSql.append(" AND A.CAT1_TYPE = B.CAT1_TYPE AND A.APPLICATION_NO = B.APPLICATION_NO ");
    	sbSql.append(" AND A.MR_NO = C.MR_NO AND B.TEST_VALUE IS NOT NULL AND A.CASE_NO IN (");
    	sbSql.append(" SELECT CASE_NO FROM ADM_INP WHERE CANCEL_FLG = 'N' AND MR_NO = '");
    	sbSql.append(mrNo);
    	sbSql.append("') AND B.TESTITEM_CODE IN ('1000081','1000082') ");
    	sbSql.append(" ORDER BY A.APPLICATION_NO DESC,B.TESTITEM_CODE ");
    	
    	TParm bmsResult = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
        if (bmsResult.getErrCode() < 0) {
        	err("ERR:" + bmsResult.getErrCode() + bmsResult.getErrText()
            + bmsResult.getErrName());
            return bmsResult;
        }
        
        // ABOѪ��
        String bmsAbo = "";
        // RHѪ��
        String bmsRh = "";
		if (bmsResult.getErrCode() > -1 && bmsResult.getCount() > 0) {
			for (int i = 0; i < bmsResult.getCount(); i++) {
				if (StringUtils.isNotEmpty(bmsAbo)
						&& StringUtils.isNotEmpty(bmsRh)) {
					break;
				}

				// ABOѪ��
				if (StringUtils.equals("1000081", bmsResult.getValue(
						"TESTITEM_CODE", i))
						&& StringUtils.isEmpty(bmsAbo)) {
					bmsAbo = bmsResult.getValue("TEST_VALUE", i).replace("��",
							"").trim();
				} else if (StringUtils.equals("1000082", bmsResult.getValue(
						"TESTITEM_CODE", i))
						&& StringUtils.isEmpty(bmsRh)) {
					bmsRh = bmsResult.getValue("TEST_VALUE", i);
					if (bmsRh.contains("��")) {
						bmsRh = "+";
					} else if (bmsRh.contains("��")) {
						bmsRh = "-";
					}
				}
			}

			if (StringUtils.isNotEmpty(bmsAbo) && StringUtils.isNotEmpty(bmsRh)) {
				// ���������Ϣ���ԭʼѪ��ֵ��LIS����Ѫ�ͼ��������һ�£�����²�����Ϣ��
				if (!StringUtils.equals(bmsAbo, bmsResult.getValue(
						"BLOOD_TYPE", 0))
						|| !StringUtils.equals(bmsRh, bmsResult.getValue(
								"BLOOD_RH_TYPE", 0))) {
					String optTerm = Operator.getIP();
					String sql = "UPDATE SYS_PATINFO SET BLOOD_TYPE = '"
							+ bmsAbo + "',BLOOD_RH_TYPE = '" + bmsRh
							+ "' WHERE MR_NO = '" + mrNo + "'";

					TParm result = new TParm(TJDODBTool.getInstance().update(
							sql));
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						return result;
					}

					// ���²�����Ϣ�޸ļ�¼
					sql = "INSERT INTO SYS_PATLOG "
							+ "            (MR_NO, OPT_DATE, MODI_ITEM, ITEM_OLD, ITEM_NEW, "
							+ "            OPT_USER, OPT_TERM ) "
							+ "     VALUES ('" + mrNo
							+ "', SYSDATE, '#', '#', '#', 'MRO_RECORD', '"
							+ optTerm + "')";
					result = new TParm(TJDODBTool.getInstance().update(
							sql.replaceFirst("#", "ABOѪ��").replaceFirst("#",
									bmsResult.getValue("BLOOD_TYPE", 0))
									.replaceFirst("#", bmsAbo)));
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						return result;
					}

					result = new TParm(TJDODBTool.getInstance().update(
							sql.replaceFirst("#", "RHѪ��").replaceFirst("#",
									bmsResult.getValue("BLOOD_RH_TYPE", 0))
									.replaceFirst("#", bmsRh)));
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						return result;
					}
				}
			}
		}
        
        return bmsResult ;
    }
    
}
