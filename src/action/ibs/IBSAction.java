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
 * Title: סԺ�Ƽ۶�����
 * </p>
 * 
 * <p>
 * Description: סԺ�Ƽ۶�����
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
	// * ����ҽ��
	// * @param parm TParm
	// * @return TParm
	// */
	// public TParm onNewIBSOrder(String dataType, TParm parm) {
	// TConnection connection = getConnection();
	// TParm result = new TParm();
	// //IBS(�������۲���)
	// if (dataType.equals("1")) {
	// result = IBSOrdermTool.getInstance().insertdata(parm, connection);
	// result = IBSOrderdTool.getInstance().insertdata(parm,connection);
	// }
	// //UDD(ҩ����ҩ)
	// if (dataType.equals("2")) {
	// }
	// //INW(��ʿִ�мƷ�)
	// if (dataType.equals("3")) {
	// }
	// //ҽ���Ʒ�
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
	 * ����Ƽ۵�
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
	 * ִ�нɷ���ҵ
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
	 * �����˵�
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
	 * �޸����
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
	 * add by yanmm 201709 ��ɫͨ��
	 * ������
	 * @param parm
	 * @return
	 */
	public TParm unlockedTellK(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "��������Ϊ��");
			return result;
		}
		StringBuilder sendResult = new StringBuilder("");
		sendResult.append("��ͨ���ŷ��Ϳ�ʼ ===== START\n");
		// ����ҽ��(���͵ĺ���)
		TParm telParm = new TParm();
		String content = "";
		telParm = MedSmsTool.getInstance().getVsDirectorDrCode(
				parm.getValue("CASE_NO"));
		String msIf ="";
		if(parm.getValue("UNLOCK_CASE").equals("03")){
			msIf = "("+parm.getValue("UNLOCK_CASE_TEXT")+")"; 
		}
		content ="����:"
				+ parm.getValue("BED_NO_DESC") + ",����:"
				+ parm.getValue("DEPT_CHN_DESC") + ",��:"
				+ parm.getValue("CHN_DESC")+ msIf + ",����:"
				+ parm.getValue("USER_NAME")
				+ "ҽʦ���¿����շѴ���,�뾡��֪ͨ���߻��������סԺԤ����!";
		// ���Ͷ���
		Timestamp sendTs = StringTool.getTimestamp(
				parm.getValue("SEND_DATE"), "yyyyMMddHHmmss");
		String sendDate = StringTool.getString(sendTs, "yyyy��MM��dd�� HHʱmm��");
		parm.setData("SEND_DATE", sendDate);
		writeXml(parm, telParm, content);
		sendResult.append("��ͨ���ŷ��ͽ��� ===== END\n");
		TIOM_FileServer.writeFile("C:/JavaHis/logs/��ͨ���ŷ��ͽ��"
				+ StringTool.getTimestamp(new Date()).toString(), sendResult
				.toString().getBytes());
		return result;
	}
	
	
	/**
	 * add by yanmm 201707 ��ɫͨ��
	 * ҽ��
	 * @param parm
	 * @return
	 */
	public TParm unlockedTell(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "��������Ϊ��");
			return result;
		}
		StringBuilder sendResult = new StringBuilder("");
		sendResult.append("��ͨ���ŷ��Ϳ�ʼ ===== START\n");
		// ����ҽ��(���͵ĺ���)
		TParm telParm = new TParm();
		String content = "";
		telParm = MedSmsTool.getInstance().getVsDrCode(
				parm.getValue("CASE_NO"));
		String msIf ="";
		if(parm.getValue("UNLOCK_CASE").equals("03")){
			msIf = "("+parm.getValue("UNLOCK_CASE_TEXT")+")"; 
		}
		content =  "����:"
				+ parm.getValue("BED_NO_DESC") + ",����:"
				+ parm.getValue("DEPT_CHN_DESC") + ",��:"
				+ parm.getValue("CHN_DESC")+ msIf + ",����:"
				+ parm.getValue("USER_NAME")
				+ "ҽʦ���¿����շѴ���,�뾡��֪ͨ���߻��������סԺԤ����!";
		// ���Ͷ���
		Timestamp sendTs = StringTool.getTimestamp(
				parm.getValue("SEND_DATE"), "yyyyMMddHHmmss");
		String sendDate = StringTool.getString(sendTs, "yyyy��MM��dd�� HHʱmm��");
		parm.setData("SEND_DATE", sendDate);
		writeXml(parm, telParm, content);
		sendResult.append("��ͨ���ŷ��ͽ��� ===== END\n");
		TIOM_FileServer.writeFile("C:/JavaHis/logs/��ͨ���ŷ��ͽ��"
				+ StringTool.getTimestamp(new Date()).toString(), sendResult
				.toString().getBytes());
		return result;
	} 
	/**
	 * add by yanmm 201707 ��ɫͨ��
	 * ��ʿ
	 * @param parm
	 * @return
	 */
	public TParm unlockedTellR(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "��������Ϊ��");
			return result;
		}
		StringBuilder sendResult = new StringBuilder("");
		sendResult.append("��ͨ���ŷ��Ϳ�ʼ ===== START\n");
		// ���ܻ�ʿ(���͵ĺ���)
		TParm telParm = new TParm();
		String content = "";
		telParm = MedSmsTool.getInstance().getVsNurseCode(
				parm.getValue("CASE_NO"));
		String msIf ="" ;
		if(parm.getValue("UNLOCK_CASE").equals("03")){
			msIf = "("+parm.getValue("UNLOCK_CASE_TEXT")+")"; 
		}
		content =  "����:"
				+ parm.getValue("BED_NO_DESC") + ",����:"
				+ parm.getValue("DEPT_CHN_DESC") + ",��:"
				+ parm.getValue("CHN_DESC")+ msIf + ",����"
				+ parm.getValue("USER_NAME")
				+ "ҽʦ���¿����շѴ���,�뾡��֪ͨ���߻��������סԺԤ����!";

		// ���Ͷ���
		Timestamp sendTs = StringTool.getTimestamp(
				parm.getValue("SEND_DATE"), "yyyyMMddHHmmss");
		String sendDate = StringTool.getString(sendTs, "yyyy��MM��dd�� HHʱmm��");
		parm.setData("SEND_DATE", sendDate);
		writeXml(parm, telParm, content);
		sendResult.append("��ͨ���ŷ��ͽ��� ===== END\n");
		TIOM_FileServer.writeFile("C:/JavaHis/logs/��ͨ���ŷ��ͽ��"
				+ StringTool.getTimestamp(new Date()).toString(), sendResult
				.toString().getBytes());
		return result;
	}
	

  
	
	private void writeXml(TParm parmRow, TParm telParm, String content) {
		// д�ļ�
		TParm xmlParm = new TParm();
		xmlParm.setData("Content", content);
		xmlParm.setData("MrNo", parmRow.getValue("MR_NO").replace("[", "")
				.replace("]", ""));
		// �õ�����,�ż�ס���
		String deptChnCode = parmRow.getValue("DEPT_CHN_DESC");
	//	String admType = parmRow.getValue("ADM_TYPE");
	//	String admTypeChn = "סԺ";
		//admTypeChn = getAdmType(admType);
		
		xmlParm.setData("Name", parmRow.getValue("PAT_NAME") + ","
				+ deptChnCode );
		xmlParm.setData("SysNo", "IBS");
		// ����ʱ��
		xmlParm.setData("ReportDate", parmRow.getValue("REPORT_DATE"));

		XmlUtilMS.createSmsFile(xmlParm, telParm);
	}
	
//	
//	  private String getAdmType(String admType) {
//			String admTypeChn = "";
//			if (admType != null) {
//				if (admType.equals("O")) {
//					admTypeChn = "����";
//				} else if (admType.equals("I")) {
//					admTypeChn = "סԺ";
//				} else if (admType.equals("E")) {
//					admTypeChn = "����";
//				} else if (admType.equals("H")) {
//					admTypeChn = "�������";
//				}
//			}
//			return admTypeChn;
//		}
	

}
























