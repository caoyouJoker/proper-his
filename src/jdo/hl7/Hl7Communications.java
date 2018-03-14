package jdo.hl7;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import sun.misc.BASE64Encoder;

import jdo.emr.EMRAMITool;
import jdo.sys.Operator;
import jdo.sys.SYSUnitTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.exception.HL7Exception;
import com.javahis.hl7interface.JavaHisAbstractInterFace;
import com.javahis.message.MessageBase;
import com.javahis.util.JavaHisDebug;

/**
 * <p>
 * Title: HL7������
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author Miracle
 * @version 1.0
 */
public class Hl7Communications extends JavaHisAbstractInterFace {
	/**
	 * HL7������
	 */
	private MessageBase hl7Message;
	/**
	 * ʵ��
	 */
	public static Hl7Communications instanceObject;
	/**
	 * �س���
	 */
	private String enter = "" + (char) 13 + (char) 10;
	/**
	 * �ļ����
	 */
	private int count = 0;
	/**
	 * ҽ��map
	 */
	private Map orderMap = new HashMap();
	/**
	 * һ���ٴ���ɫע��
	 */
	private boolean picFlg = false;

	/**
	 * ������
	 */
	public Hl7Communications() {
		hl7Message = MessageBase.getInstance();
	}

	/**
	 * �õ�ʵ��
	 * 
	 * @return RegMethodTool
	 */
	public static synchronized Hl7Communications getInstance() {
		if (instanceObject == null)
			instanceObject = new Hl7Communications();
		return instanceObject;
	}

	public static void main(String[] args) {
		JavaHisDebug.initClient();
		// TParm parm = new TParm();
		// parm.setData("INDEX_ID","10012900001");
		// parm.setData("FILE_NAME","10012900001.HL7");
		// parm.setData("FILE_DATE","KKKKKKKKKKKKKFADGADGADGADGA");
		TParm parm = new TParm();
		parm.setData("ADM_TYPE", "I");
		parm.setData("CAT1_TYPE", "RIS");
		parm.setData("PAT_NAME", "����");
		parm.setData("CASE_NO", "100407000009");
		parm.setData("LAB_NO", "100412000012");
		parm.setData("ORDER_NO", "100412000008");
		parm.setData("SEQ_NO", "1");
		parm.setData("FLG", "0");
		List orderList = new ArrayList();
		orderList.add(parm);
		Hl7Communications.getInstance().Hl7Message(orderList);
		// Hl7Communications.getInstance().Hl7Message("I","RIS","����","100113000025","100129000003","0");
	}

	/**
	 * ����HL7��Ϣ
	 * 
	 * @param orderList
	 *            List
	 * @return boolean
	 */
	public synchronized TParm Hl7Message(List orderList) {
		TParm result = new TParm();
		boolean falg = false;
		int rowCount = orderList.size();
		StringBuffer errStr = new StringBuffer();
		if (orderList.isEmpty()) {
			errStr.append("����Ϊ�գ�");
			result.setErrCode(0);
			result.setErrText(errStr.toString());
			return result;
		}
		
		// һ���ٴ���¼��ɫ�ж�ע��
		if ("PIC".equals(((TParm) orderList.get(0)).getValue("ROLE_TYPE"))) {
			picFlg = true;
		} else {
			picFlg = false;
		}
		
		Map ordM = new HashMap();
		Map checkHl7 = new HashMap();
		for (int i = 0; i < rowCount; i++) {
			TParm temp = (TParm) orderList.get(i);
			// System.out.println("HL7����ǰ:"+temp);
			String strTemp = "";
			if (temp.getValue("FLG").equals("0")) {
				strTemp = temp.getValue("ADM_TYPE")
						+ temp.getValue("CAT1_TYPE")
						+ temp.getValue("PAT_NAME") + temp.getValue("CASE_NO")
						+ temp.getValue("LAB_NO") + temp.getValue("FLG");
			} else {
				strTemp = temp.getValue("ADM_TYPE")
						+ temp.getValue("CAT1_TYPE")
						+ temp.getValue("PAT_NAME") + temp.getValue("CASE_NO")
						+ temp.getValue("LAB_NO") + temp.getValue("FLG");// shibl
																			// modify																		// 20130319
			}
			if (ordM.get(strTemp) == null) {
				ordM.put(strTemp, temp);
				Map liMap=new HashMap();
				for (int j = 0; j < rowCount; j++) {
					TParm tempParm = (TParm) orderList.get(j);
					String strTempline = "";
					if (temp.getValue("FLG").equals("0")) {
						strTempline = tempParm.getValue("ADM_TYPE")
								+ tempParm.getValue("CAT1_TYPE")
								+ tempParm.getValue("PAT_NAME") + tempParm.getValue("CASE_NO")
								+ tempParm.getValue("LAB_NO") + tempParm.getValue("FLG");
					} else {
						strTempline = tempParm.getValue("ADM_TYPE")
								+ tempParm.getValue("CAT1_TYPE")
								+ tempParm.getValue("PAT_NAME") + tempParm.getValue("CASE_NO")
								+ tempParm.getValue("LAB_NO") + tempParm.getValue("FLG");// shibl
																					// modify																		// 20130319
					}
					if(!strTemp.equals(strTempline))
						continue;
					if (liMap.get(strTempline) == null) {
						liMap.put(strTempline, tempParm);
						orderMap.clear();
						orderMap.put(tempParm.getValue("ORDER_NO")
								+ tempParm.getValue("SEQ_NO"), tempParm.getValue("ORDER_NO")
								+ tempParm.getValue("SEQ_NO"));
					}else {
						orderMap.put(tempParm.getValue("ORDER_NO")
								+ tempParm.getValue("SEQ_NO"), tempParm.getValue("ORDER_NO")
								+ tempParm.getValue("SEQ_NO"));
					}
				
				}
				// ������Ϣ
				falg = Hl7Message(temp.getValue("ADM_TYPE"), temp
						.getValue("CAT1_TYPE"), temp.getValue("PAT_NAME"), temp
						.getValue("CASE_NO"), temp.getValue("LAB_NO"), temp
						.getValue("ORDER_NO"), temp.getValue("SEQ_NO"), temp
						.getValue("FLG"));
				// System.out.println("falg"+falg);
				String checkStr = temp.getValue("FLG").equals("0") ? temp
						.getValue("CASE_NO")
						+ temp.getValue("LAB_NO") : temp.getValue("CASE_NO")
						+ temp.getValue("LAB_NO") + temp.getValue("ORDER_NO")
						+ temp.getValue("SEQ_NO");// shibl
				if (checkHl7.get(checkStr) == null) {
					if (!falg) {
						errStr.append(temp.getValue("PAT_NAME") + "�����Ϊ:"
								+ temp.getValue("LAB_NO") + "����ʧ�ܣ�");
						errStr.append(enter);
					} else {
						if (!Hl7Tool.getInstance().updateMedApply(
								temp.getValue("CASE_NO"),
								temp.getValue("LAB_NO"), temp.getValue("FLG"),
								temp.getValue("ADM_TYPE"), orderMap)) {// SHIBL
							errStr.append(temp.getValue("PAT_NAME") + "�����Ϊ:"
									+ temp.getValue("LAB_NO") + "����״̬ʧ�ܣ�");
							errStr.append(enter);
						}
					}
					checkHl7.put(checkStr, checkStr);
				}
			} 
		}
		// System.out.println(errStr.toString().length());
		if (errStr.toString().length() != 0) {
			result.setErrCode(-1);
			result.setErrText(errStr.toString());
		} else {
			result.setErrCode(1);
		}
		return result;
	}

	/**
	 * ����HL7CIS��Ϣ
	 * 
	 * @param orderList
	 *            List
	 * @return TParm
	 */
	public synchronized TParm Hl7MessageCIS(List orderList, String type) {
		TParm result = new TParm();
		StringBuffer errStr = new StringBuffer();
		if (orderList.isEmpty()) {
			errStr.append("����Ϊ�գ�");
			result.setErrCode(0);
			result.setErrText(errStr.toString());
			return result;
		}
		// ͬ��NIS zhanglei 2017.04.20\
		if ("NIS".equals(type)) {
			//errStr.append("1����NIS�ӿ�");
			if (createICUOrderNIS(orderList, type) < 0) {
				errStr.append("����ʧ�ܣ�NIS");
			}
		}
		if ("OPE".equals(type)) {
			if (createICUOrder(orderList, type) < 0) {
				errStr.append("����ʧ�ܣ�");
			}
		}
		if ("NBW".equals(type)) {
			if (createICUOrderCIS(orderList, type) < 0) {
				errStr.append("����ʧ�ܣ�");
			}
		}
		//////////////////////////////////////////zhangs add start
		if ("CS5".equals(type)) {
			if (createICUOrderCS5(orderList, type) < 0) {
				errStr.append("����ʧ�ܣ�");
			}
		}
        //////////////////////////////////////////zhangs add end
		if ("ADM_IN".equals(type) || "ADM_OUT".equals(type)
				|| "ADM_TRAN".equals(type) || "ADM_TRAN_BED".equals(type) || "ADM_REF".equals(type)) {
			if (createAdmHl7(orderList, type) < 0) {
				errStr.append("����ʧ�ܣ�");
			}
		}
		if (errStr.toString().length() != 0) {
			result.setErrCode(-1);
			result.setErrText(errStr.toString());
		} else {
			result.setErrCode(1);
		}
		return result;
	}

	/**
	 * ����HL7��Ϣ
	 * 
	 * @param admType
	 *            String �ż�ס��
	 * @param catType
	 *            ҽ�����
	 * @param patName
	 *            ��������
	 * @param caseNo
	 *            String �����
	 * @param applictionNo
	 *            String �����
	 * @param flg
	 *            String ״̬(0,����/1,ȡ��/)
	 */
	public synchronized boolean Hl7Message(String admType, String catType,
			String patName, String caseNo, String applictionNo, String orderNo,
			String seqNo, String flg) {
		boolean falg = false;
		int errCode = 0;
		TParm parm = new TParm();
		// ���鷢��
		if ("LIS".equals(catType) && "0".equals(flg)) {
			parm.setData("ADM_TYPE", admType);
			parm.setData("CAT1_TYPE", catType);
			parm.setData("PAT_NAME", patName);
			parm.setData("CASE_NO", caseNo);
			parm.setData("ORDER_NO", orderNo);
			parm.setData("SEQ_NO", seqNo);
			parm.setData("LAB_NO", applictionNo);
			parm.setData("FLG", flg);
			errCode = createLisOrderHl7(parm);
		}
		/**
		 * ����ȡ��
		 */
		if ("LIS".equals(catType) && "1".equals(flg)) {
			parm.setData("ADM_TYPE", admType);
			parm.setData("CAT1_TYPE", catType);
			parm.setData("PAT_NAME", patName);
			parm.setData("CASE_NO", caseNo);
			parm.setData("ORDER_NO", orderNo);
			parm.setData("SEQ_NO", Integer.parseInt(seqNo));
			parm.setData("LAB_NO", applictionNo);
			parm.setData("FLG", flg);
			errCode = releaseLisOrderHl7(parm);
		}
		/**
		 * ��鷢��
		 */
		if ("RIS".equals(catType) && "0".equals(flg)) {
			parm.setData("ADM_TYPE", admType);
			parm.setData("CAT1_TYPE", catType);
			parm.setData("PAT_NAME", patName);
			parm.setData("CASE_NO", caseNo);
			parm.setData("ORDER_NO", orderNo);
			parm.setData("SEQ_NO", seqNo);
			parm.setData("LAB_NO", applictionNo);
			parm.setData("FLG", flg);
			errCode = createRisOrderHl7(parm);
		}
		/**
		 * ���ȡ��
		 */
		if ("RIS".equals(catType) && "1".equals(flg)) {
			parm.setData("ADM_TYPE", admType);
			parm.setData("CAT1_TYPE", catType);
			parm.setData("PAT_NAME", patName);
			parm.setData("CASE_NO", caseNo);
			parm.setData("ORDER_NO", orderNo);
			parm.setData("SEQ_NO", Integer.parseInt(seqNo));
			parm.setData("LAB_NO", applictionNo);
			parm.setData("FLG", flg);
			errCode = releaseRisOrderHl7(parm);
		}
		if (errCode > 0)
			falg = true;
		return falg;
	}
	
	/**
	 * ����NISҽ����Ϣ
	 * zhanglei 2017.4.20
	 * @param obj
	 * @param type
	 * @return
	 */
	public int createICUOrderNIS(Object obj, String type) {
		List orderList = (List) obj;
		TParm orderParm = (TParm) orderList.get(0);
		String caseNo = orderParm.getValue("CASE_NO", 0);
		StringBuffer hl7Str = new StringBuffer();
		if (caseNo.length() <= 0) {
			return -1;
		}
		// ����MSH��Ϣ
		try {
			hl7Str.append(this.createMsh(new TParm(), "NIS"));
			hl7Str.append(enter);
		} catch (HL7Exception ex) {
			return -1;
		}
		TParm userInfo = Hl7Tool.getInstance().getODIParm(orderParm.getRow(0));
		String mrNo = userInfo.getValue("MR_NO", 0);
		TParm patP = new TParm();
		patP.setData("MR_NO", mrNo);
		TParm patInfoParm = Hl7Tool.getInstance().getPatInfo(patP);
		// ����PID��Ϣ
		try {
			hl7Str.append(createPid(patInfoParm.getRow(0), "CIS"));
			hl7Str.append(enter);
		} catch (HL7Exception ex1) {
			return -1;
		}
		TParm pv1Parm = userInfo.getRow(0);
		pv1Parm.setData("ADM_TYPE", "I");
		// ����PV1��Ϣ
		try {
			hl7Str.append(createPv1(pv1Parm, "CIS"));
			hl7Str.append(enter);
		} catch (HL7Exception ex2) {
			return -1;
		}
		// ҽ������ORC��Ϣ������OBR��Ϣ
		String orcStr = "";
		TParm orcParm = new TParm();
		String obrStr = "";
		TParm obrParm = new TParm();
		for (int i = 0; i < orderParm.getCount("CASE_NO"); i++) {
			orcParm = orderParm.getRow(i);
					orcParm.setData("TYPE", "NW");
				orcParm.setData("ORDER_TYPE", type);
				try {
					orcStr = createOrc(orcParm, "ORDER");
				} catch (HL7Exception e) {
					return -1;
				}
				hl7Str.append(orcStr);
				hl7Str.append(enter);
				obrParm = orderParm.getRow(i);
				obrParm.setData("TYPE", type);
				try {
					obrStr = createObr(obrParm, "CIS");
				} catch (HL7Exception e) {
					return -1;
				}
				hl7Str.append(obrStr);
				hl7Str.append(enter);
			
		}
		TParm netP = new TParm();
		netP.setData("MR_NO", mrNo);
		// ����NET��Ϣ
		try {
			hl7Str.append(createNet(netP, "CIS"));
			hl7Str.append(enter);
		} catch (Exception ex3) {
			return -1;
		}
		// ����DG1��Ϣ
		try {
			TParm dg1Parm = orderParm.getRow(0);
			dg1Parm.setData("ADM_TYPE", "I");
			hl7Str.append(createDg1(dg1Parm, "CIS"));
			hl7Str.append(enter);
		} catch (Exception ex3) {
			return -1;
		}
		try {
			return hl7Message.sendMessage(this.getSendBaseInfo(orderParm
					.getRow(0), hl7Str));
		} catch (HL7Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	/**
	 * ���Ժ (Ѫ��)ת�ƣ�ICU��
	 * 
	 * @param obj
	 *            Object
	 * @return int
	 */
	public int createAdmHl7(Object obj, String type) {
		List orderList = (List) obj;
		int rowCount = orderList.size();
		TParm userParm = (TParm) orderList.get(0);
		String caseNo = userParm.getValue("CASE_NO");
		TParm userInfo = Hl7Tool.getInstance().getODIParm(userParm);
		String mrNo = userInfo.getValue("MR_NO", 0);
		TParm patP = new TParm();
		patP.setData("MR_NO", mrNo);
		TParm patInfoParm = Hl7Tool.getInstance().getPatInfo(patP);
		for (int i = 0; i < patInfoParm.getCount(); i++) {
			patInfoParm.addData("CASE_NO", caseNo);
		}
		StringBuffer hl7Data = new StringBuffer();
		String mshStr = "";
		String evnStr = "";
		String pidStr = "";
		String pv1Str = "";
		String dg1Str = "";
		// add by wangb 2015/08/25 ת������
		String rf1Str = "";
		
		try {
			mshStr = createMsh(userParm, type);
			pidStr = createPid(patInfoParm.getRow(0), type);
			pv1Str = createPv1(userParm, type);
			evnStr = createEvn(userInfo.getRow(0), type);
			dg1Str = createDg1(userParm, type);
			
			// ת������
			if ("ADM_REF".equals(type)) {
				TParm refParm = (TParm) orderList.get(1);
				rf1Str = createRf1(refParm);
				pv1Str = createPv1(refParm, type);
				evnStr = createEvn(refParm, type);
			}
		} catch (HL7Exception ex1) {
			ex1.printStackTrace();
			return -1;
		}
		// ����MSHͷ
		if (mshStr.length() == 0) {
			return -1;
		}
		hl7Data.append(mshStr);
		hl7Data.append(enter);
		// ����EVNͷ
		if (evnStr.length() == 0) {
			return -1;
		}
		hl7Data.append(evnStr);
		hl7Data.append(enter);
		// ����PIDͷ
		if (pidStr.length() == 0) {
			return -1;
		}
		hl7Data.append(pidStr);
		hl7Data.append(enter);
		// ����PV1ͷ
		if (pv1Str.length() == 0) {
			return -1;
		}
		hl7Data.append(pv1Str);
		hl7Data.append(enter);

		// ����Dg1ͷ
		if (dg1Str.length() == 0) {
			return -1;
		}
		hl7Data.append(dg1Str);
		hl7Data.append(enter);
		
		// ת������
		if ("ADM_REF".equals(type)) {
			// ����rf1Strͷ
			if (rf1Str.length() == 0) {
				return -1;
			}
			hl7Data.append(rf1Str);
			hl7Data.append(enter);
		}

		try {
			// ת������
			if ("ADM_REF".equals(type)) {
				TParm refParm = (TParm) orderList.get(1);
				return hl7Message.sendMessage(this.getSendBaseInfo(refParm, hl7Data));
			}
			return hl7Message.sendMessage(getSendADMInfo(userParm, hl7Data));
		} catch (HL7Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	/**
	 * ICUҽ��
	 * 
	 * @param obj
	 *            Object
	 * @return int
	 */
	public int createICUOrder(Object obj, String type) {
		List orderList = (List) obj;
		TParm orderParm = (TParm) orderList.get(0);
		// System.out.println("-=============---" + orderParm);
		String caseNo = orderParm.getValue("CASE_NO", 0);
		StringBuffer hl7Str = new StringBuffer();
		if (caseNo.length() <= 0) {
			return -1;
		}
		// ����MSH��Ϣ
		try {
			hl7Str.append(this.createMsh(new TParm(), "ORDER"));
			hl7Str.append(enter);
		} catch (HL7Exception ex) {
			return -1;
		}
		TParm userInfo = Hl7Tool.getInstance().getODIParm(orderParm.getRow(0));
		String mrNo = userInfo.getValue("MR_NO", 0);
		TParm patP = new TParm();
		patP.setData("MR_NO", mrNo);
		TParm patInfoParm = Hl7Tool.getInstance().getPatInfo(patP);
		// ����PID��Ϣ
		try {
			hl7Str.append(createPid(patInfoParm.getRow(0), "CIS"));
			hl7Str.append(enter);
		} catch (HL7Exception ex1) {
			return -1;
		}
		TParm pv1Parm = userInfo.getRow(0);
		pv1Parm.setData("ADM_TYPE", "I");
		// ����PV1��Ϣ
		try {
			hl7Str.append(createPv1(pv1Parm, "CIS"));
			hl7Str.append(enter);
		} catch (HL7Exception ex2) {
			return -1;
		}
		TParm drugP = new TParm();
		drugP.setData("MR_NO", mrNo);
		// ����Al1��Ϣ
		try {
			hl7Str.append(createAL1(drugP));
			hl7Str.append(enter);
		} catch (Exception ex3) {
			return -1;
		}
		// ҽ������ORC��Ϣ������OBR��Ϣ
		String orcStr = "";
		TParm orcParm = new TParm();
		String obrStr = "";
		TParm obrParm = new TParm();
		for (int i = 0; i < orderParm.getCount("CASE_NO"); i++) {
			orcParm = orderParm.getRow(i);
			orcParm.setData("TYPE", "NW");
			try {
				orcStr = createOrc(orcParm, "CIS");
			} catch (HL7Exception e) {
				return -1;
			}
			hl7Str.append(orcStr);
			hl7Str.append(enter);
			obrParm = orderParm.getRow(i);
			obrParm.setData("TYPE", type);
			try {
				obrStr = createObr(obrParm, "CIS");
			} catch (HL7Exception e) {
				return -1;
			}
			hl7Str.append(obrStr);
			hl7Str.append(enter);
		}

		TParm netP = new TParm();
		netP.setData("MR_NO", mrNo);
		// ����NET��Ϣ
		try {
			hl7Str.append(createNet(netP, "CIS"));
			hl7Str.append(enter);
		} catch (Exception ex3) {
			return -1;
		}
		// ����DG1��Ϣ
		try {
			TParm dg1Parm = orderParm.getRow(0);
			dg1Parm.setData("ADM_TYPE", "I");
			hl7Str.append(createDg1(dg1Parm, "CIS"));
			hl7Str.append(enter);
		} catch (Exception ex3) {
			return -1;
		}
		// ����OBX��Ϣ
		try {
			hl7Str.append(createObx(patInfoParm.getRow(0), "CIS"));
			hl7Str.append(enter);
		} catch (Exception ex3) {
			return -1;
		}

		try {
			return hl7Message.sendMessage(this.getSendBaseInfo(orderParm
					.getRow(0), hl7Str));
		} catch (HL7Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	/**
	 * ����CISҽ����Ϣ
	 * 
	 * @param obj
	 * @param type
	 * @return
	 */
	public int createICUOrderCIS(Object obj, String type) {
		List orderList = (List) obj;
		TParm orderParm = (TParm) orderList.get(0);
		String caseNo = orderParm.getValue("CASE_NO", 0);
		StringBuffer hl7Str = new StringBuffer();
		if (caseNo.length() <= 0) {
			return -1;
		}
		// ����MSH��Ϣ
		try {
			hl7Str.append(this.createMsh(new TParm(), "ORDER"));
			hl7Str.append(enter);
		} catch (HL7Exception ex) {
			return -1;
		}
		TParm userInfo = Hl7Tool.getInstance().getODIParm(orderParm.getRow(0));
		String mrNo = userInfo.getValue("MR_NO", 0);
		TParm patP = new TParm();
		patP.setData("MR_NO", mrNo);
		TParm patInfoParm = Hl7Tool.getInstance().getPatInfo(patP);
		// ����PID��Ϣ
		try {
			hl7Str.append(createPid(patInfoParm.getRow(0), "CIS"));
			hl7Str.append(enter);
		} catch (HL7Exception ex1) {
			return -1;
		}
		TParm pv1Parm = userInfo.getRow(0);
		pv1Parm.setData("ADM_TYPE", "I");
		// ����PV1��Ϣ
		try {
			hl7Str.append(createPv1(pv1Parm, "CIS"));
			hl7Str.append(enter);
		} catch (HL7Exception ex2) {
			return -1;
		}
		// ҽ������ORC��Ϣ������OBR��Ϣ
		String orcStr = "";
		TParm orcParm = new TParm();
		String obrStr = "";
		TParm obrParm = new TParm();
		for (int i = 0; i < orderParm.getCount("CASE_NO"); i++) {
			orcParm = orderParm.getRow(i);
			if (orderParm.getValue("CAT1_TYPE", i).equals("PHA")) {
				if (orderParm.getValue("LINK_NO", i).equals("")
						|| (!orderParm.getValue("LINK_NO", i).equals("") && orderParm
								.getValue("LINKMAIN_FLG", i).equals("Y")))
					orcParm.setData("TYPE", "NW");
				else if (!orderParm.getValue("LINK_NO", i).equals("")
						&& orderParm.getValue("LINKMAIN_FLG", i).equals("N"))
					orcParm.setData("TYPE", "CH");
				orcParm.setData("ORDER_TYPE", type);
				try {
					orcStr = createOrc(orcParm, "ORDER");
				} catch (HL7Exception e) {
					return -1;
				}
				hl7Str.append(orcStr);
				hl7Str.append(enter);
				obrParm = orderParm.getRow(i);
				obrParm.setData("TYPE", type);
				try {
					obrStr = createObr(obrParm, "CIS");
				} catch (HL7Exception e) {
					return -1;
				}
				hl7Str.append(obrStr);
				hl7Str.append(enter);
			}
		}
		TParm netP = new TParm();
		netP.setData("MR_NO", mrNo);
		// ����NET��Ϣ
		try {
			hl7Str.append(createNet(netP, "CIS"));
			hl7Str.append(enter);
		} catch (Exception ex3) {
			return -1;
		}
		// ����DG1��Ϣ
		try {
			TParm dg1Parm = orderParm.getRow(0);
			dg1Parm.setData("ADM_TYPE", "I");
			hl7Str.append(createDg1(dg1Parm, "CIS"));
			hl7Str.append(enter);
		} catch (Exception ex3) {
			return -1;
		}
		try {
			return hl7Message.sendMessage(this.getSendBaseInfo(orderParm
					.getRow(0), hl7Str));
		} catch (HL7Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	/**
	 * ����Obx��Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            String
	 * @return String
	 * @throws HL7Exception
	 */
	public String createObx(TParm parm, String type) throws HL7Exception {
		String result = "";
		// ICU
		if (type.equals("CIS")) {
			hl7Message.getObx().setMSH(hl7Message.getMsh());
			hl7Message.getObx().setSeqNo("1");
			hl7Message.getObx().setValueType("NM");
			hl7Message.getObx().setObservationIdentifier("BODY WEIGHT");
			hl7Message.getObx().setObservationValue("111");
			hl7Message.getObx().setUnit("KG");
			hl7Message.getObx().setMethodType("");
			result = hl7Message.getObx().toString();
		}
		return result;
	}

	/**
	 * ������¼
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            String
	 * @return String
	 */
	public String createAL1(TParm parm) {
		TParm drugParm = Hl7Tool.getInstance().getDrugAllErgy(
				parm.getValue("MR_NO"));
		if (drugParm.getCount() <= 0) {
			hl7Message.getAl1().setMsh(hl7Message.getMsh());
			hl7Message.getAl1().setDrugId("");
			hl7Message.getAl1().setDrugTypeCode("");
			hl7Message.getAl1().setDrugNote("");
			hl7Message.getAl1().setDrugSeverity("");
			hl7Message.getAl1().setDrugName("");
			hl7Message.getAl1().setDrugCode("");
			hl7Message.getAl1().setConfDate("");
			return hl7Message.getAl1().toString();
		}
		String str = "";
		int row = drugParm.getCount();
		for (int i = 0; i < row; i++) {
			TParm temp = drugParm.getRow(i);
			hl7Message.getAl1().setMsh(hl7Message.getMsh());
			hl7Message.getAl1().setDrugId(temp.getValue("ID"));
			hl7Message.getAl1().setDrugTypeCode(temp.getValue("DRUG_TYPE"));
			hl7Message.getAl1().setDrugNote(temp.getValue("ALLERGY_NOTE"));
			hl7Message.getAl1().setDrugSeverity("");
			if ("A".equals(temp.getValue("DRUG_TYPE"))) {
				hl7Message.getAl1().setDrugName(
						Hl7Tool.getInstance().getDrugTypeName("A",
								temp.getValue("DRUGORINGRD_CODE")));
			}
			if ("B".equals(temp.getValue("DRUG_TYPE"))) {
				hl7Message.getAl1().setDrugName(
						Hl7Tool.getInstance().getDrugTypeName("B",
								temp.getValue("DRUGORINGRD_CODE")));
			}
			if ("C".equals(temp.getValue("DRUG_TYPE"))) {
				hl7Message.getAl1().setDrugName(
						Hl7Tool.getInstance().getDrugTypeName("C",
								temp.getValue("DRUGORINGRD_CODE")));
			}
			hl7Message.getAl1().setDrugCode(temp.getValue("DRUGORINGRD_CODE"));
			hl7Message.getAl1().setConfDate(temp.getValue("ADM_DATE"));
			str += hl7Message.getAl1().toString();
			if (i != row - 1) {
				str += enter;
			}
		}
		return str;
	}

	/**
	 * ���ͼ���ҽ��
	 * 
	 * @param obj
	 *            Object
	 * @return int
	 */
	public int createLisOrderHl7(Object obj) {
		if (obj == null)
			return -1;
		TParm parm = (TParm) obj;
		TParm medData = Hl7Tool.getInstance().getMedData(parm, orderMap);
		// System.out.println("�ӿڱ�����:"+medData);
		if (medData.getCount() <= 0)
			return -1;
		StringBuffer sendData = new StringBuffer();
		TParm patP = new TParm();
		patP.setData("MR_NO", medData.getValue("MR_NO", 0));
		TParm patInfoParm = Hl7Tool.getInstance().getPatInfo(patP);
		// System.out.println("������Ϣ����:"+patInfoParm);
		if (medData.getCount() <= 0 || patInfoParm.getCount() <= 0) {
			return -1;
		}
		patInfoParm = patInfoParm.getRow(0);
		String mshStr = "";
		String pidStr = "";
		String pv1Str = "";
		String orcStr = "";
		String obrStr = "";
		String netStr = "";
		String dg1Str = "";
		String orcAndobr = "";
		try {
			mshStr = createMsh(medData.getRow(0), "LIS");
			// System.out.println("msh"+mshStr);
			pidStr = createPid(patInfoParm, "LIS");
			// System.out.println("pidStr"+pidStr);
			pv1Str = createPv1(medData.getRow(0), "LIS");
			// System.out.println("pv1Str"+pv1Str);
			int rowCount = medData.getCount();
			for (int i = 0; i < rowCount; i++) {
				TParm orcParm = medData.getRow(i);
				orcParm.setData("TYPE", "NW");
				orcStr = createOrc(orcParm, "LIS");
				orcAndobr += orcStr + enter;
				obrStr = createObr(orcParm, "LIS");
				orcAndobr += obrStr + enter;
			}
			// System.out.println("orcAndobr"+orcAndobr);
			netStr = createNet(medData.getRow(0), "LIS");
			// System.out.println("netStr"+netStr);
			dg1Str = createDg1(medData.getRow(0), "LIS");
			// System.out.println("dg1Str"+dg1Str);
		} catch (HL7Exception ex1) {
			ex1.printStackTrace();
			return -1;
		}
		// ����MSHͷ
		if (mshStr.length() == 0)
			return -1;
		sendData.append(mshStr);
		sendData.append(enter);
		// ����PIDͷ
		if (pidStr.length() == 0)
			return -1;
		sendData.append(pidStr);
		sendData.append(enter);
		// ����PV1ͷ
		if (pv1Str.length() == 0)
			return -1;
		sendData.append(pv1Str);
		sendData.append(enter);
		// ����ORC��OBR���ͷ
		if (orcAndobr.length() == 0)
			return -1;
		sendData.append(orcAndobr);
		// ����NETͷ
		if (netStr.length() == 0)
			return -1;
		sendData.append(netStr);
		sendData.append(enter);
		// ����DG1ͷ
		if (dg1Str.length() == 0)
			return -1;
		sendData.append(dg1Str);
		try {
			return hl7Message.sendMessage(getSendBaseInfo(medData.getRow(0),
					sendData));
		} catch (HL7Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	/**
	 * ȡ������ҽ��
	 * 
	 * @param obj
	 *            Object
	 * @return int
	 */
	public int releaseLisOrderHl7(Object obj) {
		if (obj == null)
			return -1;
		TParm parm = (TParm) obj;
		TParm medData = Hl7Tool.getInstance().getMedData(parm, orderMap);
		if (medData.getCount() <= 0)
			return -1;
		StringBuffer sendData = new StringBuffer();
		TParm patP = new TParm();
		patP.setData("MR_NO", medData.getValue("MR_NO", 0));
		TParm patInfoParm = Hl7Tool.getInstance().getPatInfo(patP);
		if (medData.getCount() <= 0 || patInfoParm.getCount() <= 0) {
			return -1;
		}
		patInfoParm = patInfoParm.getRow(0);
		String mshStr = "";
		String pidStr = "";
		String pv1Str = "";
		String orcStr = "";
		String obrStr = "";
		String netStr = "";
		String dg1Str = "";
		String orcAndobr = "";
		try {
			mshStr = createMsh(medData.getRow(0), "LIS");
			pidStr = createPid(patInfoParm, "LIS");
			pv1Str = createPv1(medData.getRow(0), "LIS");
			int rowCount = medData.getCount();
			for (int i = 0; i < rowCount; i++) {
				TParm orcParm = medData.getRow(i);
				orcParm.setData("TYPE", "CA");
				orcStr = createOrc(orcParm, "LIS");
				orcAndobr += orcStr + enter;
				obrStr = createObr(orcParm, "LIS");
				orcAndobr += obrStr + enter;
			}
			netStr = createNet(medData.getRow(0), "LIS");
			dg1Str = createDg1(medData.getRow(0), "LIS");
		} catch (HL7Exception ex1) {
			ex1.printStackTrace();
			return -1;
		}
		// ����MSHͷ
		if (mshStr.length() == 0)
			return -1;
		sendData.append(mshStr);
		sendData.append(enter);
		// ����PIDͷ
		if (pidStr.length() == 0)
			return -1;
		sendData.append(pidStr);
		sendData.append(enter);
		// ����PV1ͷ
		if (pv1Str.length() == 0)
			return -1;
		sendData.append(pv1Str);
		sendData.append(enter);
		// ����ORC��OBR���ͷ
		if (orcAndobr.length() == 0)
			return -1;
		sendData.append(orcAndobr);
		// ����NETͷ
		if (netStr.length() == 0)
			return -1;
		sendData.append(netStr);
		sendData.append(enter);
		// ����DG1ͷ
		if (dg1Str.length() == 0)
			return -1;
		sendData.append(dg1Str);
		try {
			return hl7Message.sendMessage(getSendBaseInfo(medData.getRow(0),
					sendData));
		} catch (HL7Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	/**
	 * ���ͼ��ҽ��
	 * 
	 * @param obj
	 *            Object
	 * @return int
	 */
	public int createRisOrderHl7(Object obj) {
		if (obj == null)
			return -1;
		TParm parm = (TParm) obj;
		TParm medData = Hl7Tool.getInstance().getMedData(parm,orderMap);
		if (medData.getCount() <= 0)
			return -1;
		StringBuffer sendData = new StringBuffer();
		TParm patP = new TParm();
		patP.setData("MR_NO", medData.getValue("MR_NO", 0));
		TParm patInfoParm = Hl7Tool.getInstance().getPatInfo(patP);
		if (medData.getCount() <= 0 || patInfoParm.getCount() <= 0) {
			return -1;
		}
		patInfoParm = patInfoParm.getRow(0);
		String mshStr = "";
		String pidStr = "";
		String pv1Str = "";
		String orcStr = "";
		String obrStr = "";
		String netStr = "";
		String dg1Str = "";
		try {
			TParm orcParm = medData.getRow(0);
			orcParm.setData("TYPE", "NW");
			mshStr = createMsh(medData.getRow(0), "RIS");
			pidStr = createPid(patInfoParm, "RIS");
			pv1Str = createPv1(orcParm, "RIS");
			orcStr = createOrc(orcParm, "RIS");
			obrStr = createObr(medData.getRow(0), "RIS");
			netStr = createNet(medData.getRow(0), "RIS");
			dg1Str = createDg1(medData.getRow(0), "RIS");
		} catch (HL7Exception ex1) {
			ex1.printStackTrace();
			return -1;
		}
		// ����MSHͷ
		if (mshStr.length() == 0)
			return -1;
		sendData.append(mshStr);
		sendData.append(enter);
		// ����PIDͷ
		if (pidStr.length() == 0)
			return -1;
		sendData.append(pidStr);
		sendData.append(enter);
		// ����PV1ͷ
		if (pv1Str.length() == 0)
			return -1;
		sendData.append(pv1Str);
		sendData.append(enter);
		// ����ORCͷ
		if (orcStr.length() == 0)
			return -1;
		sendData.append(orcStr);
		sendData.append(enter);
		// ����OBRͷ
		if (obrStr.length() == 0)
			return -1;
		sendData.append(obrStr);
		sendData.append(enter);
		// ����NETͷ
		if (netStr.length() == 0)
			return -1;
		sendData.append(netStr);
		sendData.append(enter);
		// ����DG1ͷ
		if (dg1Str.length() == 0)
			return -1;
		sendData.append(dg1Str);
		try {
			this.CreatRisHtml(medData.getRow(0));
			return hl7Message.sendMessage(getSendBaseInfo(medData.getRow(0),
					sendData));
		} catch (HL7Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	/**
	 * �õ��������ݻ�������
	 * 
	 * @param fileData
	 *            StringBuffer
	 * @return TParm
	 */
	public TParm getSendBaseInfo(TParm parm, StringBuffer fileData) {
		TParm result = new TParm();
		Timestamp sysDateS = SystemTool.getInstance().getDate();
		// ��ǰʱ��
		String sysDate = StringTool.getString(sysDateS, "yyyyMMddHHmmssss");
		result.setData("INDEX_ID", parm.getValue("CASE_NO"));
		result.setData("FILE_NAME", sysDate + "_"
				+ parm.getValue("APPLICATION_NO") + "_" + count + ".HL7");
		result.setData("FILE_DATE", fileData.toString());
		count++;
		return result;
	}

	/**
	 * �õ��������ݻ�������
	 * 
	 * @param fileData
	 *            StringBuffer
	 * @return TParm
	 */
	public TParm getSendADMInfo(TParm parm, StringBuffer fileData) {
		TParm result = new TParm();
		Timestamp sysDateS = SystemTool.getInstance().getDate();
		// ��ǰʱ��
		String sysDate = StringTool.getString(sysDateS, "yyyyMMddHHmmssss");
		result.setData("INDEX_ID", parm.getValue("CASE_NO"));
		result.setData("FILE_NAME", sysDate + parm.getValue("IPD_NO")
				+ parm.getValue("BED_NO") + count + ".HL7");
		result.setData("FILE_DATE", fileData.toString());
		count++;
		return result;
	}

	/**
	 * ����MSH��Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return String
	 */
	public String createMsh(TParm parm, String type) throws HL7Exception {
		String result = "";
		Timestamp sysDateS = SystemTool.getInstance().getDate();
		// ���ͳ���
		String sendComp = parm.getValue("SEND_COMP");
		TParm tParm = Hl7Tool.getInstance().getRegionCode();
		String sendingApplication = tParm.getValue("REGION_CODE", 0);
		// System.out.println("sendComp:"+sendComp);
		// ��ǰʱ��
		String sysDate = StringTool.getString(sysDateS, "yyyyMMddHHmmss");
		// RIS���
		if ("RIS".equals(type)) {
			hl7Message.getMsh().setSendingApplication(sendingApplication);
			hl7Message.getMsh().setReceivingApplication(
					parm.getValue("ORDER_CAT1_CODE"));
			hl7Message.getMsh().setDateTimeOfMessage(sysDate);
			hl7Message.getMsh().setMessageControlID(
					sysDate + parm.getValue("ORDER_NO")
							+ parm.getValue("SEQ_NO"));
			result = hl7Message.getMsh().toString();
		}
		// ����
		if ("LIS".equals(type)) {
			hl7Message.getMsh().setSendingApplication(sendingApplication);
			hl7Message.getMsh().setReceivingApplication(
					parm.getValue("ORDER_CAT1_CODE"));
			hl7Message.getMsh().setDateTimeOfMessage(sysDate);
			hl7Message.getMsh().setMessageControlID(
					sysDate + parm.getValue("ORDER_NO")
							+ parm.getValue("SEQ_NO"));
			result = hl7Message.getMsh().toString();
		}
		// ͬ��NIS zhanglei 2017.04.20
		if ("NIS".equals(type)) {
			hl7Message.getMsh().setSendingApplication(sendingApplication);
			hl7Message.getMsh().setMessageType("OMG^O19");
			hl7Message.getMsh().setReceivingApplication("NIS");
			hl7Message.getMsh().setDateTimeOfMessage(sysDate);
			hl7Message.getMsh().setMessageControlID(
					sysDate + parm.getValue("ORDER_NO")
							+ parm.getValue("SEQ_NO"));
			result = hl7Message.getMsh().toString();
		}
		// ת��
		if ("ADM_TRAN".equals(type) || "ADM_TRAN_BED".equals(type)) {
			hl7Message.getMsh().setSendingApplication(sendingApplication);
			hl7Message.getMsh().setMessageType("ADT^A02");
			hl7Message.getMsh().setReceivingApplication(sendComp);
			hl7Message.getMsh().setDateTimeOfMessage(sysDate);
			hl7Message.getMsh().setMessageControlID(
					sysDate + parm.getValue("IPD_NO"));
			result = hl7Message.getMsh().toString();
		}
		// ��Ժ
		if ("ADM_IN".equals(type)) {
			hl7Message.getMsh().setSendingApplication(sendingApplication);
			hl7Message.getMsh().setMessageType("ADT^A01");
			hl7Message.getMsh().setReceivingApplication(sendComp);
			hl7Message.getMsh().setDateTimeOfMessage(sysDate);
			hl7Message.getMsh().setMessageControlID(
					sysDate + parm.getValue("IPD_NO"));
			result = hl7Message.getMsh().toString();
		}
		// ��Ժ
		if ("ADM_OUT".equals(type) || "ADM_REF".equals(type)) {
			hl7Message.getMsh().setSendingApplication(sendingApplication);
			hl7Message.getMsh().setMessageType("ADT^A03");
			hl7Message.getMsh().setReceivingApplication(sendComp);
			hl7Message.getMsh().setDateTimeOfMessage(sysDate);
			hl7Message.getMsh().setMessageControlID(
					sysDate + parm.getValue("IPD_NO"));
			result = hl7Message.getMsh().toString();
		}
		// �ٴ�ҽ��
		if ("ORDER".equals(type)) {
			hl7Message.getMsh().setSendingApplication(sendingApplication);
			hl7Message.getMsh().setMessageType("OMG^O19");
			hl7Message.getMsh().setReceivingApplication("CIS");
			hl7Message.getMsh().setDateTimeOfMessage(sysDate);
			hl7Message.getMsh().setMessageControlID(
					sysDate + parm.getValue("ORDER_NO")
							+ parm.getValue("SEQ_NO"));
			result = hl7Message.getMsh().toString();
		}
		// �����ͼ��嵥
		if ("LISSEND".equals(type)) {
			hl7Message.getMsh().setSendingApplication(sendingApplication);
			hl7Message.getMsh().setMessageType("SSU^U03");
			hl7Message.getMsh().setReceivingApplication("LIS");
			hl7Message.getMsh().setDateTimeOfMessage(sysDate);
			hl7Message.getMsh().setMessageControlID(
					sysDate + parm.getValue("ORDER_NO")
							+ parm.getValue("SEQ_NO"));
			result = hl7Message.getMsh().toString();
		}
		// ��������

		if ("OPE_APPLY".equals(type)) {
			hl7Message.getMsh().setSendingApplication(sendingApplication);
			hl7Message.getMsh().setSendingFacility(
					parm.getValue("OP_DEPT_CODE") + "^"
							+ parm.getValue("OP_DEPT_DESC"));
			hl7Message.getMsh().setReceivingApplication("AIMS");
			hl7Message.getMsh().setReceivingFacility("030503^������");
			hl7Message.getMsh().setDateTimeOfMessage(
					sysDate.substring(0, 4) + "-" + sysDate.substring(4, 6)
							+ "-" + sysDate.substring(6, 8) + " "
							+ sysDate.substring(8, 10) + ":"
							+ sysDate.substring(10, 12) + ":"
							+ sysDate.substring(12, 14));
			hl7Message.getMsh().setMessageType("SRM^S01");
			hl7Message.getMsh().setMessageControlID(
					sysDate + parm.getValue("OPBOOK_SEQ"));
			result = hl7Message.getMsh().toString();
		}
		// �����ų�

		if ("OPE_SCHEDULE".equals(type)) {
			hl7Message.getMsh().setSendingApplication(sendingApplication);
			hl7Message.getMsh().setSendingFacility("030503^������");
			hl7Message.getMsh().setReceivingApplication("AIMS");
			hl7Message.getMsh().setReceivingFacility(
					parm.getValue("OP_DEPT_CODE", 0)
							+ "^"
							+ Hl7Tool.getInstance().getDeptDesc(
									parm.getValue("OP_DEPT_CODE", 0)));
			hl7Message.getMsh().setDateTimeOfMessage(
					sysDate.substring(0, 4) + "-" + sysDate.substring(4, 6)
							+ "-" + sysDate.substring(6, 8) + " "
							+ sysDate.substring(8, 10) + ":"
							+ sysDate.substring(10, 12) + ":"
							+ sysDate.substring(12, 14));
			hl7Message.getMsh().setMessageType("SRR^S01");
			hl7Message.getMsh().setMessageControlID(
					sysDate + parm.getValue("OPBOOK_SEQ", 0));
			result = hl7Message.getMsh().toString();
		}
		// �ٴ�ҽ��_��Һ��ҩƷ
		if ("ORDERCS5".equals(type)) {
			hl7Message.getMsh().setSendingApplication(sendingApplication);
			hl7Message.getMsh().setMessageType("OMG^O19");
			hl7Message.getMsh().setReceivingApplication("CS5");
			hl7Message.getMsh().setDateTimeOfMessage(sysDate);
			hl7Message.getMsh().setMessageControlID(
					sysDate + parm.getValue("ORDER_NO")
							+ parm.getValue("SEQ_NO"));
			result = hl7Message.getMsh().toString();
		}

		return result;
	}

	/**
	 * ����Evn��Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            String
	 * @return String
	 * @throws HL7Exception
	 */
	public String createEvn(TParm parm, String type) throws HL7Exception {
		String result = "";
		Timestamp sysDateS = SystemTool.getInstance().getDate();
		String userID = parm.getValue("OPT_USER");
		String userName = Hl7Tool.getInstance().getOperatorName(userID);
		// ��ǰʱ��
		String sysDate = StringTool.getString(sysDateS, "yyyyMMddHHmmss");
		if ("ADM_TRAN".equals(type)) {
			hl7Message.getEvn().setMsh(hl7Message.getMsh());
			// ADT_A02(ת��)|ADT_A09(ȡ��ת��)
			hl7Message.getEvn().setEventType("A02");
			hl7Message.getEvn().setDateTime(sysDate);
			hl7Message.getEvn().setPlanTime("");
			// 01 ��������
			// 02 ҽ��/ҽ�ƴ�ҵ��
			// 03 �˿��ղ����
			hl7Message.getEvn().setReasonCode("02");
			hl7Message.getEvn().setUserId(userID);
			hl7Message.getEvn().setUserName(userName);
			hl7Message.getEvn().setActTranTime("");
			hl7Message.getEvn().setAgenciesCode("");// ICU
			result = hl7Message.getEvn().toString();
		}
		if ("ADM_IN".equals(type) || "ADM_TRAN_BED".equals(type)) {
			hl7Message.getEvn().setMsh(hl7Message.getMsh());
			// ADT_A02(ת��)|ADT_A09(ȡ��ת��)
			hl7Message.getEvn().setEventType("A02");
			hl7Message.getEvn().setDateTime(sysDate);
			hl7Message.getEvn().setPlanTime("");
			// 01 ��������
			// 02 ҽ��/ҽ�ƴ�ҵ��
			// 03 �˿��ղ����
			hl7Message.getEvn().setReasonCode("");
			hl7Message.getEvn().setUserId(userID);
			hl7Message.getEvn().setUserName(userName);
			hl7Message.getEvn().setActTranTime("");
			hl7Message.getEvn().setAgenciesCode("");
			result = hl7Message.getEvn().toString();
		}
		if ("ADM_OUT".equals(type) || "ADM_REF".equals(type)) {
			hl7Message.getEvn().setMsh(hl7Message.getMsh());
			// ADT_A02(ת��)|ADT_A09(ȡ��ת��)
			hl7Message.getEvn().setEventType("A03");
			hl7Message.getEvn().setDateTime(sysDate);
			hl7Message.getEvn().setPlanTime("");
			// 01 ��������
			// 02 ҽ��/ҽ�ƴ�ҵ��
			// 03 �˿��ղ����
			hl7Message.getEvn().setReasonCode("");
			hl7Message.getEvn().setUserId(userID);
			hl7Message.getEvn().setUserName(userName);
			hl7Message.getEvn().setActTranTime("");
			hl7Message.getEvn().setAgenciesCode("");
			result = hl7Message.getEvn().toString();
		}
		return result;
	}

	/**
	 * ����PID��Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            String
	 * @return String
	 */
	public String createPid(TParm parm, String type) throws HL7Exception {
		String result = "";
		hl7Message.getPid().setMSH(hl7Message.getMsh());
		hl7Message.getPid().setMrNo(parm.getValue("MR_NO"));
		hl7Message.getPid().setName(parm.getValue("PAT_NAME"));
		hl7Message.getPid().setOtherName(parm.getValue("PAT_NAME1"));
		// liuf
		if ("CIS".equals(type) || "ADM_IN".equals(type)
				|| "ADM_OUT".equals(type) || "ADM_TRAN".equals(type)
				|| "ADM_TRAN_BED".equals(type)) {
			String admDate = StringTool.getString(parm
					.getTimestamp("BIRTH_DATE"), "yyyyMMdd");
			hl7Message.getPid().setBirthDate(admDate);
		} else {
			hl7Message.getPid().setBirthDate(parm.getValue("BIRTH_DATE"));
		}
		if ("OPE_APPLY".equals(type)) {
			String admDate = StringTool.getString(parm
					.getTimestamp("BIRTH_DATE"), "yyyy-MM-dd");
			hl7Message.getPid().setBirthDate(admDate);
			
			// add by wangb 2015/11/05 ��������ʱ�����鴫�Ͳ����������  START
			TParm sumVitalsignParm = Hl7Tool.getInstance().getSumVitalsignInfo(
					parm.getValue("CASE_NO"));
			String height = "";
			String weight = "";
			for (int i = 0; i < sumVitalsignParm.getCount(); i++) {
				// ��߶�ʱ����һ�㲻��仯������ȡ���һ������һ�ʴ���0�����ݼ���
				if (StringUtils.isEmpty(height)
						&& sumVitalsignParm.getInt("HEIGHT", i) > 0) {
					height = sumVitalsignParm.getValue("HEIGHT", i);
				}
				// ���ػ��б仯��ȡ���һ�β����Ĳ�Ϊ�յ�ֵ
				if (StringUtils.isEmpty(weight)
						&& StringUtils.isNotEmpty(sumVitalsignParm.getValue(
								"WEIGHT", i))) {
					weight = sumVitalsignParm.getValue("WEIGHT", i);
				}
				if (StringUtils.isNotEmpty(height)
						&& StringUtils.isNotEmpty(weight)) {
					break;
				}
			}
			hl7Message.getPid().setWeight(
					height + hl7Message.getMsh().getColSeparator() + weight);
			// add by wangb 2015/11/05 ��������ʱ�����鴫�Ͳ���������� END
		}
		// liuf

		hl7Message.getPid().setIdNo(parm.getValue("IDNO"));
		hl7Message.getPid().setMarriageCode(
				this.getMarriageCode(parm.getValue("MARRIAGE_CODE")));
		hl7Message.getPid().setSexCode(
				this.getSexDescHl7(parm.getValue("SEX_CODE")));
		hl7Message.getPid().setTel(parm.getValue("TEL_HOME"));
		// hl7Message.getPid().setAddress(parm.getValue("RESID_ADDRESS"));
		// liuf
		hl7Message.getPid().setAddress(
				parm.getValue("RESID_ADDRESS").startsWith("null") ? "" : parm
						.getValue("RESID_ADDRESS"));
		if ("OPE_APPLY".equals(type)) {
			hl7Message.getPid().setCaseNo(parm.getValue("CASE_NO"));
			if ("T".equals(parm.getValue("BLOOD_TYPE")))
				hl7Message.getPid().setloodType("");
			else
				hl7Message.getPid().setloodType(parm.getValue("BLOOD_TYPE"));
		}
		if ("OPE_SCHEDULE".equals(type)) {
			hl7Message.getPid().setCaseNo(parm.getValue("CASE_NO"));
		}
		if ("ADM_REF".equals(type)) {
			String birthDate = parm.getValue("BIRTH_DATE");
			if (null != birthDate && !"".equals(birthDate)) {
				birthDate = birthDate.substring(0, 10);
			}
			hl7Message.getPid().setBirthDate(birthDate);
			hl7Message.getPid().setContactsName(parm.getValue("CONTACTS_NAME"));
			hl7Message.getPid().setContactsTel(parm.getValue("CONTACTS_TEL"));
			hl7Message.getPid().setCaseNo(parm.getValue("CASE_NO"));
		}
		// if("LIS".equals(type)){
		// hl7Message.getPid().setMSH(hl7Message.getMsh());
		// hl7Message.getPid().setMrNo(parm.getValue("MR_NO"));
		// hl7Message.getPid().setName(parm.getValue("PAT_NAME"));
		// hl7Message.getPid().setOtherName(parm.getValue("PAT_NAME1"));
		// hl7Message.getPid().setBirthDate(parm.getValue("BIRTH_DATE"));
		// hl7Message.getPid().setIdNo(parm.getValue("IDNO"));
		// hl7Message.getPid().setMarriageCode(
		// this.getMarriageCode(parm.getValue("MARRIAGE_CODE")));
		// hl7Message.getPid().setSexCode(
		// this.getSexDescHl7(parm.getValue("SEX_CODE")));
		// hl7Message.getPid().setTel(parm.getValue("TEL_HOME"));
		// hl7Message.getPid().setAddress(parm.getValue("RESID_ADDRESS"));
		// }
		// if("RIS".equals(type)){
		// hl7Message.getPid().setMSH(hl7Message.getMsh());
		// hl7Message.getPid().setMrNo(parm.getValue("MR_NO"));
		// hl7Message.getPid().setName(parm.getValue("PAT_NAME"));
		// hl7Message.getPid().setOtherName(parm.getValue("PAT_NAME1"));
		// hl7Message.getPid().setBirthDate(parm.getValue("BIRTH_DATE"));
		// hl7Message.getPid().setIdNo(parm.getValue("IDNO"));
		// hl7Message.getPid().setMarriageCode(
		// this.getMarriageCode(parm.getValue("MARRIAGE_CODE")));
		// hl7Message.getPid().setSexCode(
		// this.getSexDescHl7(parm.getValue("SEX_CODE")));
		// hl7Message.getPid().setTel(parm.getValue("TEL_HOME"));
		// hl7Message.getPid().setAddress(parm.getValue("RESID_ADDRESS"));
		// }
		// if("CIS".equals(type)){
		// hl7Message.getPid().setMSH(hl7Message.getMsh());
		// hl7Message.getPid().setMrNo(parm.getValue("MR_NO"));
		// hl7Message.getPid().setName(parm.getValue("PAT_NAME"));
		// hl7Message.getPid().setOtherName(parm.getValue("PAT_NAME1"));
		// hl7Message.getPid().setBirthDate(parm.getValue("BIRTH_DATE"));
		// hl7Message.getPid().setIdNo(parm.getValue("IDNO"));
		// hl7Message.getPid().setMarriageCode(
		// this.getMarriageCode(parm.getValue("MARRIAGE_CODE")));
		// hl7Message.getPid().setSexCode(
		// this.getSexDescHl7(parm.getValue("SEX_CODE")));
		// hl7Message.getPid().setTel(parm.getValue("TEL_HOME"));
		// hl7Message.getPid().setAddress(parm.getValue("RESID_ADDRESS"));
		// }
		
		// add by wangb 2016/09/26 һ���ٴ��������֤�ź͵绰����
		if (picFlg) {
			hl7Message.getPid().setIdNo(null);
			hl7Message.getPid().setTel(null);
		}
		
		result = hl7Message.getPid().toString();
		return result;
	}

	/**
	 * ����PV1��Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            String
	 * @return String
	 */
	public String createPv1(TParm parm, String type) throws HL7Exception {
		String result = "";
		String sDeptCode = parm.getValue("DEPT_CODE");
		String sBedNo = parm.getValue("BED_NO");
		String admType = parm.getValue("ADM_TYPE");
		TParm hl7Parm = new TParm();
		String hospAreaName = Manager.getOrganization().getHospitalCHNFullName(
				Operator.getRegion());
		// ������ò���
		if ("O".equals(admType)) {
			hl7Parm = Hl7Tool.getInstance().getODOParm(parm);
			if (hl7Parm.getCount() <= 0) {
				return result;
			}
			parm.setData("DEPT_CODE", Hl7Tool.getInstance().getDeptDesc(
					hl7Parm.getRow(0).getValue("REALDEPT_CODE")));
			parm.setData("CLINICROOM_NO", Hl7Tool.getInstance()
					.getClinicRoomDesc(
							hl7Parm.getRow(0).getValue("CLINICROOM_NO")));
			parm.setData("CTZ_CODE", hl7Parm.getRow(0).getData("CTZ1_CODE"));
			parm.setData("CTZ_DESC", hl7Parm.getRow(0).getData("CTZ_DESC"));
			String admDate = StringTool.getString(hl7Parm.getRow(0)
					.getTimestamp("ADM_DATE"), "yyyyMMddHHmmss");
			parm.setData("IN_DATE", admDate);
			parm.setData("OUT_DATE", "");
			parm.setData("BED_NO", "");
			parm.setData("DR_CODE", hl7Parm.getRow(0).getData("REALDR_CODE"));
			parm.setData("CASE_NO", hl7Parm.getRow(0).getData("CASE_NO"));
		}
		// ������ò���
		if ("E".equals(admType)) {
			// �״��ĵ�ע��
			if (!"Y".equals(parm.getValue("FIRST_ECG_FLG"))) {
				hl7Parm = Hl7Tool.getInstance().getODOParm(parm);
				if (hl7Parm.getCount() <= 0) {
					return result;
				}
				parm.setData("DEPT_CODE", Hl7Tool.getInstance().getDeptDesc(
						hl7Parm.getRow(0).getValue("REALDEPT_CODE")));
				parm.setData("CLINICROOM_NO", Hl7Tool.getInstance()
						.getClinicRoomDesc(
								hl7Parm.getRow(0).getValue("CLINICROOM_NO")));
				parm.setData("CTZ_CODE", hl7Parm.getRow(0).getData("CTZ1_CODE"));
				parm.setData("CTZ_DESC", hl7Parm.getRow(0).getData("CTZ_DESC"));
				String admDate = StringTool.getString(hl7Parm.getRow(0)
						.getTimestamp("ADM_DATE"), "yyyyMMddHHmmss");
				parm.setData("IN_DATE", admDate);
				parm.setData("OUT_DATE", "");
				parm.setData("BED_NO", "");
				parm.setData("DR_CODE", hl7Parm.getRow(0).getData("REALDR_CODE"));
				parm.setData("CASE_NO", hl7Parm.getRow(0).getData("CASE_NO"));
			}
		}
		// סԺ���ò���
		if ("I".equals(admType)) {
			hl7Parm = Hl7Tool.getInstance().getODIParm(parm);
			if (hl7Parm.getCount() <= 0) {
				return result;
			}
			parm.setData("DR_CODE", hl7Parm.getRow(0).getData("VS_DR_CODE"));
			// parm.setData("BED_NO", hl7Parm.getRow(0).getData("BED_NO"));
			// liuf
			parm.setData("BED_NO", hl7Parm.getRow(0).getData("BED_NO_DESC"));

			parm.setData("CTZ_CODE", hl7Parm.getRow(0).getData("CTZ1_CODE"));
			parm.setData("CTZ_DESC", hl7Parm.getRow(0).getData("CTZ_DESC"));
			parm.setData("CLINICROOM_NO", Hl7Tool.getInstance().getStationDesc(
					hl7Parm.getRow(0).getValue("STATION_CODE")));
			parm.setData("DEPT_CODE", Hl7Tool.getInstance().getDeptDesc(
					hl7Parm.getRow(0).getValue("DEPT_CODE")));
			String admDate = StringTool.getString(hl7Parm.getRow(0)
					.getTimestamp("IN_DATE"), "yyyyMMddHHmmss");
			parm.setData("IN_DATE", admDate);
			String outDate = "";
			if (!hl7Parm.getRow(0).getValue("DS_DATE").equals("")) {
				outDate = StringTool.getString(hl7Parm.getRow(0).getTimestamp(
						"DS_DATE"), "yyyyMMddHHmmss");
			}
			parm.setData("OUT_DATE", outDate);
			parm.setData("CASE_NO", hl7Parm.getRow(0).getData("CASE_NO"));
		}
		// ���������ò���
		if ("H".equals(admType)) {
			hl7Parm = Hl7Tool.getInstance().getHRMParm(parm);
			if (hl7Parm.getCount() <= 0) {
				return result;
			}
			parm.setData("DEPT_CODE", Hl7Tool.getInstance().getDeptDesc(
					hl7Parm.getRow(0).getValue("DEPT_CODE")));
			parm.setData("CLINICROOM_NO", "");
			parm.setData("CTZ_CODE", "99");
			parm.setData("CTZ_DESC", "�Է�");
			String admDate = StringTool.getString(hl7Parm.getRow(0)
					.getTimestamp("REPORT_DATE"), "yyyyMMddHHmmss");
			parm.setData("IN_DATE", admDate);
			parm.setData("OUT_DATE", "");
			parm.setData("BED_NO", "");
			parm.setData("CASE_NO", hl7Parm.getRow(0).getData("CASE_NO"));
			parm.setData("DR_CODE", hl7Parm.getRow(0).getData("OPT_USER"));
		}
		hl7Message.getPv1().setMSH(hl7Message.getMsh());
		hl7Message.getPv1().setHospArea(hospAreaName);
		hl7Message.getPv1().setCaseNo(parm.getValue("CASE_NO"));
		hl7Message.getPv1().setAdmType(parm.getValue("ADM_TYPE"));
		hl7Message.getPv1().setAdmDoctor(parm.getValue("DR_CODE"));
		// hl7Message.getPv1().setBedNo(parm.getValue("BED_NO"));
		hl7Message.getPv1().setCtzCode(parm.getValue("CTZ_CODE"));
		hl7Message.getPv1().setCtzDesc(parm.getValue("CTZ_DESC"));
		hl7Message.getPv1().setInDate(parm.getValue("IN_DATE"));
		if ("ADM_TRAN".equals(type)) {
			hl7Message.getPv1().setDeptCode(
					Hl7Tool.getInstance().getDeptDesc(
							parm.getValue("IN_DEPT_CODE")));
			hl7Message.getPv1().setRoomNo( Hl7Tool.getInstance().getStationDesc(
					parm.getValue("IN_STATION_CODE")));//yanmm
			hl7Message.getPv1().setOldDeptCode(
					Hl7Tool.getInstance().getDeptDesc(
							parm.getValue("OUT_DEPT_CODE")));
			hl7Message.getPv1().setOldRoomNo(parm.getValue("CLINICROOM_NO"));
			hl7Message.getPv1().setOldBedNo(parm.getValue("BED_NO"));
		} else if ("ADM_TRAN_BED".equals(type)) { //ת��
			hl7Message.getPv1().setDeptCode(parm.getValue("DEPT_CODE"));
			hl7Message.getPv1().setRoomNo(parm.getValue("CLINICROOM_NO"));
			hl7Message.getPv1().setBedNo(parm.getValue("BED_NO"));
			hl7Message.getPv1().setOldDeptCode(parm.getValue("DEPT_CODE"));
			hl7Message.getPv1().setOldRoomNo(parm.getValue("CLINICROOM_NO"));
			hl7Message.getPv1().setOldBedNo(parm.getValue("BED_NO_DESC"));
		} else if("ADM_OUT".equals(type)){
			hl7Message.getPv1().setDeptCode(parm.getValue("DEPT_CODE"));
			hl7Message.getPv1().setRoomNo(parm.getValue("CLINICROOM_NO"));
			hl7Message.getPv1().setBedNo(parm.getValue("BED_NO"));
			hl7Message.getPv1().setOldDeptCode(parm.getValue("DEPT_CODE"));
			hl7Message.getPv1().setOldRoomNo(parm.getValue("CLINICROOM_NO"));
			hl7Message.getPv1().setOldBedNo(parm.getValue("BED_NO"));
		}else {
			// �봲 yanmm
			hl7Message.getPv1().setDeptCode(parm.getValue("DEPT_CODE"));
			hl7Message.getPv1().setRoomNo(parm.getValue("CLINICROOM_NO"));
			hl7Message.getPv1().setBedNo(parm.getValue("BED_NO"));
			
			
			hl7Message.getPv1().setOldDeptCode(Hl7Tool.getInstance().getDeptDesc(
					parm.getValue("OUT_DEPT_CODE")));
			hl7Message.getPv1().setOldRoomNo(Hl7Tool.getInstance().getStationDesc(
					parm.getValue("OUT_CLINICROOM_NO")));
			hl7Message.getPv1().setOldBedNo(Hl7Tool.getInstance().getBedDesc(
					parm.getValue("OUT_BED_NO")));

		}
		hl7Message.getPv1().setHosptital("TEDAICH");
		hl7Message.getPv1().setOutDate(parm.getValue("OUT_DATE"));
		// modify by wangb 217/04/12 �������ʹ�ü��˺ŷ����״��ĵ���Ϣ
		if ("E".equals(admType)) {
			if ("Y".equals(parm.getValue("FIRST_ECG_FLG"))) {
				hl7Message.getPv1().setipdNo(parm.getValue("TRIAGE_NO"));
			} else {
				// add by wangb 2017/06/23 ����ҽ��վֻ�п����ĵ�һ�������ĵ��18�����ĵ�ҽ��ʱ�����͵���Ϣ�а������˺�
				// ��ѯҽ�������Ƿ��Ѿ����������ĵ�ҽ��
				TParm firstEcgOrderResult = EMRAMITool.getInstance()
						.getVirtualOrderByCaseNo(parm.getValue("CASE_NO"));
				// �������ļ���ȡ����ʹ�����ĵ�ҽ��
				String orderCode = TConfig.getSystemValue("CPC_ECG_ORDER");
				if (firstEcgOrderResult.getCount() <= 1
						&& orderCode.contains(parm.getValue("ORDER_CODE"))
						&& "NW".equals(parm.getValue("TYPE"))) {
					// add by wangb 2017/05/18 �����ʹ���ķ��ﲡ�������⴦�� START
					// ȡ�ü��������Ϣ
					TParm triageNoResult = EMRAMITool.getInstance()
							.getErdEvalutionDataByCaseNo(
									parm.getValue("CASE_NO"));
					if (triageNoResult.getCount() > 0) {
						hl7Message.getPv1().setipdNo(
								triageNoResult.getValue("TRIAGE_NO", 0));
					}
					// add by wangb 2017/05/18 �����ʹ���ķ��ﲡ�������⴦�� END
				} else {
					hl7Message.getPv1().setipdNo("");
				}
			}
		} else {
			hl7Message.getPv1().setipdNo("");
		}
		if ("OPE_APPLY".equals(type)) {
			hl7Message.getPv1().setHosptital(sDeptCode);
			hl7Message.getPv1().setBedNo(sBedNo);
		} else if ("ADM_REF".equals(type)) {
			hl7Message.getPv1().setDeptCode(
					parm.getValue("DEPT_ID")
							+ hl7Message.getMsh().getColSeparator()
							+ parm.getValue("DEPT_DESC"));
			hl7Message.getPv1().setRoomNo(
					parm.getValue("STATION_CODE")
							+ hl7Message.getMsh().getColSeparator()
							+ parm.getValue("STATION_DESC"));
			hl7Message.getPv1().setInDate(parm.getValue("IN_DATE"));
			hl7Message.getPv1().setOutDate(parm.getValue("OUT_DATE"));
			hl7Message.getPv1().setVsDrCode(
					parm.getValue("VS_DR_CODE")
							+ hl7Message.getMsh().getColSeparator()
							+ parm.getValue("VS_DR_NAME"));
			hl7Message.getPv1().setAttendDrCode(
					parm.getValue("ATTEND_DR_CODE")
							+ hl7Message.getMsh().getColSeparator()
							+ parm.getValue("ATTEND_DR_NAME"));
		}
		// if ("RIS".equals(type)) {
		// hl7Message.getPv1().setMSH(hl7Message.getMsh());
		// hl7Message.getPv1().setHospArea(hospAreaName);
		// hl7Message.getPv1().setCaseNo(parm.getValue("CASE_NO"));
		// hl7Message.getPv1().setAdmType(parm.getValue("ADM_TYPE"));
		// hl7Message.getPv1().setAdmDoctor(parm.getValue("DR_CODE"));
		// hl7Message.getPv1().setBedNo(parm.getValue("BED_NO"));
		// hl7Message.getPv1().setCtzCode(parm.getValue("CTZ_CODE"));
		// hl7Message.getPv1().setCtzDesc(parm.getValue("CTZ_DESC"));
		// hl7Message.getPv1().setRoomNo(parm.getValue("CLINICROOM_NO"));
		// hl7Message.getPv1().setInDate(parm.getValue("IN_DATE"));
		// hl7Message.getPv1().setDeptCode(parm.getValue("DEPT_CODE"));
		// result = hl7Message.getPv1().toString();
		// }
		// if ("LIS".equals(type)) {
		// hl7Message.getPv1().setMSH(hl7Message.getMsh());
		// hl7Message.getPv1().setHospArea(hospAreaName);
		// hl7Message.getPv1().setCaseNo(parm.getValue("CASE_NO"));
		// hl7Message.getPv1().setAdmType(parm.getValue("ADM_TYPE"));
		// hl7Message.getPv1().setAdmDoctor(parm.getValue("DR_CODE"));
		// hl7Message.getPv1().setBedNo(parm.getValue("BED_NO"));
		// hl7Message.getPv1().setCtzCode(parm.getValue("CTZ_CODE"));
		// hl7Message.getPv1().setCtzDesc(parm.getValue("CTZ_DESC"));
		// hl7Message.getPv1().setRoomNo(parm.getValue("CLINICROOM_NO"));
		// hl7Message.getPv1().setInDate(parm.getValue("IN_DATE"));
		// hl7Message.getPv1().setDeptCode(parm.getValue("DEPT_CODE"));
		// result = hl7Message.getPv1().toString();
		// }
		// if ("ADM_TRAN".equals(type)) {
		// hl7Message.getPv1().setMSH(hl7Message.getMsh());
		// hl7Message.getPv1().setHospArea(hospAreaName);
		// hl7Message.getPv1().setCaseNo(parm.getValue("CASE_NO"));
		// hl7Message.getPv1().setAdmType(parm.getValue("ADM_TYPE"));
		// hl7Message.getPv1().setAdmDoctor(parm.getValue("DR_CODE"));
		// hl7Message.getPv1().setBedNo(parm.getValue("BED_NO"));
		// hl7Message.getPv1().setCtzCode(parm.getValue("CTZ_CODE"));
		// hl7Message.getPv1().setCtzDesc(parm.getValue("CTZ_DESC"));
		// hl7Message.getPv1().setRoomNo(parm.getValue("CLINICROOM_NO"));
		// hl7Message.getPv1().setInDate(parm.getValue("IN_DATE"));
		// hl7Message.getPv1().setDeptCode(parm.getValue("DEPT_CODE"));
		// result = hl7Message.getPv1().toString();
		// }
		// if("ADM_IN".equals(type)||"ADM_OUT".equals(type)){
		// hl7Message.getPv1().setMSH(hl7Message.getMsh());
		// hl7Message.getPv1().setHospArea(hospAreaName);
		// hl7Message.getPv1().setCaseNo(parm.getValue("CASE_NO"));
		// hl7Message.getPv1().setAdmType(parm.getValue("ADM_TYPE"));
		// hl7Message.getPv1().setAdmDoctor(parm.getValue("DR_CODE"));
		// hl7Message.getPv1().setBedNo(parm.getValue("BED_NO"));
		// hl7Message.getPv1().setCtzCode(parm.getValue("CTZ_CODE"));
		// hl7Message.getPv1().setCtzDesc(parm.getValue("CTZ_DESC"));
		// hl7Message.getPv1().setRoomNo(parm.getValue("CLINICROOM_NO"));
		// hl7Message.getPv1().setInDate(parm.getValue("IN_DATE"));
		// hl7Message.getPv1().setDeptCode(parm.getValue("DEPT_CODE"));
		// hl7Message.getPv1().setHosptital("TEDAICH");
		// hl7Message.getPv1().setOldDeptCode(parm.getValue("DEPT_CODE"));
		// hl7Message.getPv1().setOldRoomNo(parm.getValue("CLINICROOM_NO"));
		// hl7Message.getPv1().setOldBedNo(parm.getValue("BED_NO"));
		// hl7Message.getPv1().setOutDate(parm.getValue("OUT_DATE"));
		// result = hl7Message.getPv1().toString();
		// }
		result = hl7Message.getPv1().toString();

		return result;
	}

	/**
	 * ����ORC��Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 * @return String type == NW ���� type == CA ɾ�� type == CH ��ҽ��
	 */
	public String createOrc(TParm parm, String type) throws HL7Exception {
		String result = "";
		Timestamp sysDateS = SystemTool.getInstance().getDate();
		String userName = Hl7Tool.getInstance().getOperatorName(
				parm.getValue("ORDER_DR_CODE"));
		// ��ǰʱ��
		String sysDate = StringTool.getString(sysDateS, "yyyyMMddHHmmss");
		// ���
		if ("RIS".equals(type)) {
			hl7Message.getOrc().setMSH(hl7Message.getMsh());
			hl7Message.getOrc().setOrderControl(parm.getValue("TYPE"));
			hl7Message.getOrc().setOrderDrCode(parm.getValue("ORDER_DR_CODE"));
			hl7Message.getOrc().setOrderDrName(parm.getValue("USER_NAME"));
			hl7Message.getOrc().setApplyNo(parm.getValue("APPLICATION_NO"));
			// ���䡢������ȥ��˳���β�ţ�
			hl7Message.getOrc().setOrderNo(parm.getValue("ORDER_NO"));
			hl7Message.getOrc().setOrderSeqNo(parm.getValue("SEQ_NO"));
			hl7Message.getOrc().setReserveDate(sysDate);
			hl7Message.getOrc().setResponseFlg("N");
			// modify by wangb 2017/08/22 ��RIS���Ϳ���
			hl7Message.getOrc().setDeptCode(parm.getValue("ORDER_DEPT_CODE"));
			String deptDesc = Hl7Tool.getInstance().getDeptDesc(
					parm.getValue("ORDER_DEPT_CODE"));
			if (StringUtils.isEmpty(deptDesc)) {
				deptDesc = parm.getValue("DEPT_CODE");
			}
			hl7Message.getOrc().setDeptDesc(deptDesc);
			result = hl7Message.getOrc().toString();
		}
		// ����
		if ("LIS".equals(type)) {
			hl7Message.getOrc().setMSH(hl7Message.getMsh());
			hl7Message.getOrc().setOrderControl(parm.getValue("TYPE"));
			hl7Message.getOrc().setOrderDrCode(parm.getValue("ORDER_DR_CODE"));
			hl7Message.getOrc().setOrderDrName(parm.getValue("USER_NAME"));
			hl7Message.getOrc().setApplyNo(parm.getValue("APPLICATION_NO"));
			hl7Message.getOrc().setOrderNo(
					parm.getValue("ORDER_NO") + parm.getValue("SEQ_NO"));
			hl7Message.getOrc().setOrderSeqNo(parm.getValue("SEQ_NO"));
			hl7Message.getOrc().setReserveDate(sysDate);
			hl7Message.getOrc().setResponseFlg("N");
			result = hl7Message.getOrc().toString();
		}
		// ICU
		if ("ORDER".equals(type)) {
			hl7Message.getOrc().setMSH(hl7Message.getMsh());
			hl7Message.getOrc().setOrderControl(parm.getValue("TYPE"));
			hl7Message.getOrc().setOrderDrCode(parm.getValue("ORDER_DR_CODE"));
			hl7Message.getOrc().setOrderDrName(userName);
			hl7Message.getOrc().setApplyNo(parm.getValue("BAR_CODE"));
			if (parm.getValue("ORDER_TYPE").equals("OPE")) {
				hl7Message.getOrc().setOrderNo("");
				hl7Message.getOrc().setOrderSeqNo("");
			} else {
				/*
				 * hl7Message.getOrc().setOrderNo( parm.getValue("ORDER_NO") +
				 * parm.getValue("SEQ_NO"));
				 */
				// liuf
				hl7Message.getOrc().setOrderNo(parm.getValue("ORDER_NO"));
				//
				hl7Message.getOrc().setOrderSeqNo(parm.getValue("SEQ_NO"));
			}
			hl7Message.getOrc().setReserveDate(sysDate);
			hl7Message.getOrc().setResponseFlg("N");
			result = hl7Message.getOrc().toString();
		}
		return result;
	}

	/**
	 * ����OBR��Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            String
	 * @return String
	 * @throws HL7Exception
	 */
	public String createObr(TParm parm, String type) throws HL7Exception {
		String result = "";
		// ���
		if ("RIS".equals(type)) {
			double allPrict = 0.0;
			hl7Message.getObr().setObjectType("RIS");
			hl7Message.getObr().setMSH(hl7Message.getMsh());
			hl7Message.getObr().setPalcerOrderNumber(
					parm.getValue("ORDER_NO") + parm.getValue("SEQ_NO"));
			hl7Message.getObr().setOrderCode(parm.getValue("ORDER_CODE"));
			hl7Message.getObr().setOrderDesc(parm.getValue("ORDER_DESC"));
			hl7Message.getObr().setSubSystemCode(
					parm.getValue("ORDER_CAT1_CODE"));
			hl7Message.getObr().setSubSystemDesc(
					Hl7Tool.getInstance().getSysOrderCat1(
							parm.getValue("ORDER_CAT1_CODE")).getValue(
							"ORDER_CAT1_DESC"));
			hl7Message.getObr().setRptTypeCode(parm.getValue("RPTTYPE_CODE"));
			hl7Message.getObr().setRptTypeDesc(
					Hl7Tool.getInstance().getEXMRule("EXM_RULE",
							parm.getValue("RPTTYPE_CODE")).getValue(
							"CATEGORY_CHN_DESC"));
			hl7Message.getObr().setPartCode(parm.getValue("OPTITEM_CODE"));
			hl7Message.getObr().setPartDesc(parm.getValue("OPTITEM_CHN_DESC"));
			hl7Message.getObr().setDeviceTypeCode(parm.getValue("DEV_CODE"));
			hl7Message.getObr().setDeviceTypeDesc(
					Hl7Tool.getInstance().getEXMRule("DEV_TYPE",
							parm.getValue("DEV_CODE")).getValue(
							"CATEGORY_CHN_DESC"));
			hl7Message.getObr().setXmlData(parm.getValue("XML_DATE"));
			// add by wangb 2015/07/07 ����ִ�п�����Ϣ START
			hl7Message.getObr().setExeDeptCode(parm.getValue("EXEC_DEPT_CODE"));
			hl7Message.getObr().setExeDeptDesc(
					Hl7Tool.getInstance().getDeptDesc(
							(parm.getValue("EXEC_DEPT_CODE"))));
			// add by wangb 2015/07/07 ����ִ�п�����Ϣ END
			
			// �����������
			if ("OPE_RIS".equals(parm.getValue("MSG_TYPE"))) {
				String[] opCodeArray = new String[] {""};
				if (StringUtils.isNotEmpty(parm.getValue("OP_CODE2"))) {
					opCodeArray = new String[] { parm.getValue("OP_CODE2")
							+ "~~" + parm.getValue("OP_DESC2") };
				}
				hl7Message.getObr().setOrderCodeArray(opCodeArray);
				hl7Message.getObr().setStatusType("N");
				result = hl7Message.getObr().toString();
				return result;
			}
			
			// ����ҽ��ϸ��
			TParm orderSetParm = Hl7Tool.getInstance().getOrderSet(
					parm.getValue("ORDER_CODE"));
			int rowCount = orderSetParm.getCount();
			String[] orderArry = null;
			if (rowCount <= 0) {
				orderArry = new String[] { "" };
				hl7Message.getObr().setOrderCodeArray(orderArry);
			} else {
				orderArry = new String[rowCount];
				for (int i = 0; i < rowCount; i++) {
					TParm temp = orderSetParm.getRow(i);
					double totPrice = temp.getDouble("OWN_PRICE")
							* temp.getDouble("DOSAGE_QTY");
					orderArry[i] = temp.getValue("ORDER_CODE") + "~" + totPrice
							+ "~" + temp.getDouble("DOSAGE_QTY") + "~"
							+ temp.getValue("ORDER_DESC");
					allPrict += totPrice;
				}
				hl7Message.getObr().setOrderCodeArray(orderArry);
			}
			hl7Message.getObr().setStatusType("N");
			hl7Message.getObr().setTotAmt("" + allPrict);
			result = hl7Message.getObr().toString();
		}
		// ����
		if ("LIS".equals(type)) {
			double allPrict = 0.0;
			hl7Message.getObr().setObjectType("LIS");
			hl7Message.getObr().setMSH(hl7Message.getMsh());
			hl7Message.getObr().setPalcerOrderNumber(
					parm.getValue("ORDER_NO") + parm.getValue("SEQ_NO"));
			hl7Message.getObr().setOrderCode(parm.getValue("ORDER_CODE"));
			hl7Message.getObr().setOrderDesc(parm.getValue("ORDER_DESC"));
			hl7Message.getObr().setSubSystemCode(
					parm.getValue("ORDER_CAT1_CODE"));
			hl7Message.getObr().setSubSystemDesc(
					Hl7Tool.getInstance().getSysOrderCat1(
							parm.getValue("ORDER_CAT1_CODE")).getValue(
							"ORDER_CAT1_DESC"));
			hl7Message.getObr().setRptTypeCode(parm.getValue("RPTTYPE_CODE"));
			hl7Message.getObr().setRptTypeDesc(
					Hl7Tool.getInstance().getEXMRule("EXM_RULE",
							parm.getValue("RPTTYPE_CODE")).getValue(
							"CATEGORY_CHN_DESC"));
			hl7Message.getObr().setPartCode(parm.getValue("OPTITEM_CODE"));
			hl7Message.getObr().setPartDesc(parm.getValue("OPTITEM_CHN_DESC"));
			hl7Message.getObr().setDeviceTypeCode(parm.getValue("DEV_CODE"));
			hl7Message.getObr().setDeviceTypeDesc(
					Hl7Tool.getInstance().getEXMRule("DEV_TYPE",
							parm.getValue("DEV_CODE")).getValue(
							"CATEGORY_CHN_DESC"));
			hl7Message.getObr().setTotAmt(parm.getValue("OWN_AMT"));
			hl7Message.getObr().setXmlData(parm.getValue("XML_DATE"));
			// ����ҽ��ϸ��
			TParm orderSetParm = Hl7Tool.getInstance().getOrderSet(
					parm.getValue("ORDER_CODE"));
			int rowCount = orderSetParm.getCount();
			String[] orderArry = null;
			if (rowCount <= 0) {
				orderArry = new String[] { "" };
				hl7Message.getObr().setOrderCodeArray(orderArry);
			} else {
				orderArry = new String[rowCount];
				for (int i = 0; i < rowCount; i++) {
					TParm temp = orderSetParm.getRow(i);
					double totPrice = temp.getDouble("OWN_PRICE")
							* temp.getDouble("DOSAGE_QTY");
					orderArry[i] = temp.getValue("ORDER_CODE") + "~" + totPrice
							+ "~" + temp.getDouble("DOSAGE_QTY") + "~"
							+ temp.getValue("ORDER_DESC");
					allPrict += totPrice;
				}
				hl7Message.getObr().setOrderCodeArray(orderArry);
			}
			hl7Message.getObr().setStatusType("N");
			hl7Message.getObr().setTotAmt("" + allPrict);
			result = hl7Message.getObr().toString();
		}
		if ("CIS".equals(type)) {
			// ����
			if (parm.getValue("TYPE").equals("OPE")) {
				TParm opeParm = Hl7Tool.getInstance().getOpeCisData(
						parm.getValue("OPBOOK_SEQ")).getRow(0);
				// System.out.println("-------opeparm--------------" + opeParm);
				hl7Message.getObr().setObjectType("OPE");
				hl7Message.getObr().setMSH(hl7Message.getMsh());
				hl7Message.getObr().setOperecordNo(
						opeParm.getValue("OP_RECORD_NO"));
				hl7Message.getObr().setOpebookNo(opeParm.getValue("OPBOOK_NO"));
				hl7Message.getObr().setOptCode(opeParm.getValue("OP_CODE1"));
				hl7Message.getObr().setOptChnDesc(
						opeParm.getValue("OPT_CHN_DESC"));
				hl7Message.getObr().setDose("1");
				hl7Message.getObr().setLabGridNumber("");
				hl7Message.getObr().setDoseUnit("̨");
				hl7Message.getObr().setDoseCode("");
				hl7Message.getObr().setDoseDesc("");
				hl7Message.getObr().setAnaCode(opeParm.getValue("ANA_CODE"));
				hl7Message.getObr().setAnaDesc(opeParm.getValue("ANA_DESC"));
				hl7Message.getObr().setAnaUser1(opeParm.getValue("ANA_USER1"));
				hl7Message.getObr().setAnaUser1Desc(
						opeParm.getValue("ANA_USER1DESC"));
				hl7Message.getObr().setAnaUser2(opeParm.getValue("ANA_USER2"));
				hl7Message.getObr().setAnaUser2Desc(
						opeParm.getValue("ANA_USER2DESC"));
				hl7Message.getObr().setCirCuleUser1(
						opeParm.getValue("CIRCULE_USER1"));
				hl7Message.getObr().setCirCuleUser1Desc(
						opeParm.getValue("CIRCULE_USER1DESC"));
				hl7Message.getObr().setCirCuleUser2(
						opeParm.getValue("CIRCULE_USER2"));
				hl7Message.getObr().setCirCuleUser2Desc(
						opeParm.getValue("CIRCULE_USER2DESC"));
				hl7Message.getObr().setCirCuleUser3(
						opeParm.getValue("CIRCULE_USER3"));
				hl7Message.getObr().setCirCuleUser3Desc(
						opeParm.getValue("CIRCULE_USER3DESC"));
				hl7Message.getObr().setCirCuleUser4(
						opeParm.getValue("CIRCULE_USER4"));
				hl7Message.getObr().setCirCuleUser4Desc(
						opeParm.getValue("CIRCULE_USER4DESC"));
				hl7Message.getObr().setScrubUser1(
						opeParm.getValue("SCRUB_USER1"));
				hl7Message.getObr().setScrubUser1Desc(
						opeParm.getValue("SCRUB_USER1DESC"));
				hl7Message.getObr().setScrubUser2(
						opeParm.getValue("SCRUB_USER2"));
				hl7Message.getObr().setScrubUser2Desc(
						opeParm.getValue("SCRUB_USER2DESC"));
				hl7Message.getObr().setScrubUser3(
						opeParm.getValue("SCRUB_USER3"));
				hl7Message.getObr().setScrubUser3Desc(
						opeParm.getValue("SCRUB_USER3DESC"));
				hl7Message.getObr().setScrubUser4(
						opeParm.getValue("SCRUB_USER4"));
				hl7Message.getObr().setScrubUser4Desc(
						opeParm.getValue("SCRUB_USER4DESC"));
				// ��������ģ���޸������
				hl7Message.getObr().setFlg("");
				hl7Message.getObr().setTypeCode("S^");
				hl7Message.getObr().setOpeTime(opeParm.getValue("OPE_TIME"));
			} else if (parm.getValue("TYPE").equals("NBW")) {
				TParm nbwparm = Hl7Tool.getInstance().getodiOrderData(parm)
						.getRow(0);
				hl7Message.getObr().setObjectType("COMMON");
				hl7Message.getObr().setMSH(hl7Message.getMsh());
				hl7Message.getObr().setOrderNo(nbwparm.getValue("ORDER_NO"));
				hl7Message.getObr().setSeq(nbwparm.getValue("ORDER_SEQ"));
				hl7Message.getObr()
						.setOrderCode(nbwparm.getValue("ORDER_CODE"));
				hl7Message.getObr()
						.setOrderDesc(nbwparm.getValue("ORDER_DESC"));
				hl7Message.getObr().setTotAmt(
						TypeTool.getString(nbwparm.getDouble("ACUMDSPN_QTY")));
				hl7Message.getObr().setRoutCode(nbwparm.getValue("ROUTE_CODE"));
				hl7Message.getObr().setFreCode(nbwparm.getValue("FREQ_CODE"));
				hl7Message.getObr().setRxKind(nbwparm.getValue("RX_KIND"));
				hl7Message.getObr().setLinkNo(nbwparm.getValue("LINK_NO"));
				hl7Message.getObr().setDose(
						TypeTool.getString(nbwparm.getDouble("MEDI_QTY")));
				hl7Message.getObr().setLabGridNumber(
						nbwparm.getValue("MED_APPLY_NO"));
				TParm unitParm = SYSUnitTool.getInstance().selectdata(
						nbwparm.getValue("MEDI_UNIT"));
				hl7Message.getObr().setDoseUnit(
						unitParm.getValue("UNIT_CHN_DESC", 0));
				hl7Message.getObr().setDoseCode(nbwparm.getValue("DOSE_TYPE"));
				hl7Message.getObr().setDoseDesc(
						nbwparm.getValue("DOSE_TYPEDESC"));
				String orderDate = StringTool.getString(nbwparm
						.getTimestamp("ORDER_DATE"), "yyyyMMddHHmmss");								
				String cfmDttm = StringTool.getString(parm
						.getTimestamp("NS_EXEC_DATE_REAL"), "yyyyMMddHHmmss");
				String dcDate = StringTool.getString(nbwparm
						.getTimestamp("DC_DATE"), "yyyyMMddHHmmss");
				hl7Message.getObr().setOrderDate(orderDate);
				hl7Message.getObr().setEffDate(orderDate);
				hl7Message.getObr().setCfmDttm(cfmDttm);
				hl7Message.getObr().setDcDate(dcDate);
				hl7Message.getObr().setTypeCode(
						getTypeCode(nbwparm.getValue("CAT1_TYPE")));
				hl7Message.getObr().setOpeTime("");
				hl7Message.getObr().setNsExecDate(parm.getValue("NS_EXEC_DATE"));
			} else if (parm.getValue("TYPE").equals("NIS")) {  //zhanglei ͬ��NIS 20170418
				TParm nbwparm = Hl7Tool.getInstance().getodiOrderDataNIS(parm)
						.getRow(0);
				hl7Message.getObr().setObjectType("COMMON");
				hl7Message.getObr().setMSH(hl7Message.getMsh());
				hl7Message.getObr().setOrderNo(nbwparm.getValue("ORDER_NO"));
				
				hl7Message.getObr().setSeq(nbwparm.getValue("ORDER_SEQ"));
				hl7Message.getObr()
						.setOrderCode(nbwparm.getValue("ORDER_CODE"));
				hl7Message.getObr()
						.setOrderDesc(nbwparm.getValue("ORDER_DESC"));
				hl7Message.getObr().setTotAmt(
						TypeTool.getString(nbwparm.getDouble("ACUMDSPN_QTY")));
				hl7Message.getObr().setRoutCode(nbwparm.getValue("ROUTE_CODE"));
				hl7Message.getObr().setFreCode(nbwparm.getValue("FREQ_CODE"));
				hl7Message.getObr().setRxKind(nbwparm.getValue("RX_KIND"));
				hl7Message.getObr().setLinkNo(nbwparm.getValue("LINK_NO"));
				hl7Message.getObr().setDose(
						TypeTool.getString(nbwparm.getDouble("MEDI_QTY")));
				hl7Message.getObr().setLabGridNumber(
						nbwparm.getValue("MED_APPLY_NO"));
				TParm unitParm = SYSUnitTool.getInstance().selectdata(
						nbwparm.getValue("MEDI_UNIT"));
				hl7Message.getObr().setDoseUnit(
						unitParm.getValue("UNIT_CHN_DESC", 0));
				hl7Message.getObr().setDoseCode(nbwparm.getValue("DOSE_TYPE"));
				hl7Message.getObr().setDoseDesc(
						nbwparm.getValue("DOSE_TYPEDESC"));
				String orderDate = StringTool.getString(nbwparm
						.getTimestamp("ORDER_DATE"), "yyyyMMddHHmmss");								
				String cfmDttm = StringTool.getString(parm
						.getTimestamp("NS_EXEC_DATE_REAL"), "yyyyMMddHHmmss");
				String dcDate = StringTool.getString(nbwparm
						.getTimestamp("DC_DATE"), "yyyyMMddHHmmss");
				hl7Message.getObr().setOrderDate(orderDate);
				hl7Message.getObr().setEffDate(orderDate);
				hl7Message.getObr().setCfmDttm(cfmDttm);
				hl7Message.getObr().setDcDate(dcDate);
				hl7Message.getObr().setTypeCode(
						getTypeCode(nbwparm.getValue("CAT1_TYPE")));
				hl7Message.getObr().setOpeTime("");
				hl7Message.getObr().setNsExecDate(parm.getValue("NS_EXEC_DATE"));
			}
			result = hl7Message.getObr().toString();
		}
			if ("CS5".equals(type)) {				
					TParm nbwparm = Hl7Tool.getInstance().getodiOrderData(parm)
							.getRow(0);
					System.out.println("CS5:"+nbwparm);
					hl7Message.getObr().setObjectType("COMMON");
					hl7Message.getObr().setMSH(hl7Message.getMsh());
					hl7Message.getObr().setOrderNo(nbwparm.getValue("ORDER_NO"));
					hl7Message.getObr().setSeq(nbwparm.getValue("ORDER_SEQ"));
					hl7Message.getObr()
							.setOrderCode(nbwparm.getValue("ORDER_CODE"));
					hl7Message.getObr()
							.setOrderDesc(nbwparm.getValue("ORDER_DESC"));
					hl7Message.getObr().setTotAmt(
							TypeTool.getString(nbwparm.getDouble("ACUMDSPN_QTY")));
//					hl7Message.getObr().setRoutCode(nbwparm.getValue("ROUTE_CODE"));
					hl7Message.getObr().setRoutCode(nbwparm.getValue("PUMP_CODE"));
					hl7Message.getObr().setFreCode(nbwparm.getValue("FREQ_CODE"));
					hl7Message.getObr().setRxKind(nbwparm.getValue("RX_KIND"));
					hl7Message.getObr().setLinkNo(nbwparm.getValue("LINK_NO"));
					hl7Message.getObr().setDose(
							TypeTool.getString(nbwparm.getDouble("MEDI_QTY")));
//					hl7Message.getObr().setDose(nbwparm.getValue("INFLUTION_RATE"));
					hl7Message.getObr().setLabGridNumber("0");
					TParm unitParm = SYSUnitTool.getInstance().selectdata(
							nbwparm.getValue("MEDI_UNIT"));
					hl7Message.getObr().setDoseUnit(
							unitParm.getValue("UNIT_CHN_DESC", 0));
					hl7Message.getObr().setDoseCode(nbwparm.getValue("DOSE_TYPE"));
					hl7Message.getObr().setDoseDesc(
							nbwparm.getValue("DOSE_TYPEDESC"));
					String orderDate = StringTool.getString(nbwparm
							.getTimestamp("ORDER_DATE"), "yyyyMMddHHmmss");	
					// ��ǰʱ��20171128 zhangs add 
					Timestamp sysDateS = SystemTool.getInstance().getDate();
					String cfmDttm = StringTool.getString(sysDateS, "yyyyMMddHHmmss");
//					String cfmDttm = StringTool.getString(parm
//							.getTimestamp("NS_EXEC_DATE_REAL"), "yyyyMMddHHmmss");
					String dcDate = StringTool.getString(nbwparm
							.getTimestamp("DC_DATE"), "yyyyMMddHHmmss");
					hl7Message.getObr().setOrderDate(orderDate);
					hl7Message.getObr().setEffDate(orderDate);
					hl7Message.getObr().setCfmDttm(cfmDttm);
					hl7Message.getObr().setDcDate(dcDate);
					///////////////////////////////////////////////////////
					hl7Message.getObr().setInflutionRate(nbwparm.getValue("INFLUTION_RATE"));
					hl7Message.getObr().setVtbi("-1");
					////////////////////////////////////////////////////////
					hl7Message.getObr().setTypeCode(
							getTypeCode(nbwparm.getValue("CAT1_TYPE")));
					hl7Message.getObr().setOpeTime("");
					hl7Message.getObr().setNsExecDate(parm.getValue("NS_EXEC_DATE"));
				
			result = hl7Message.getObr().toString();
		}
		// ����걾�ͼ콻��
		if ("LISSEND".equals(type)) {
			hl7Message.getObr().setObjectType("LIS");
			hl7Message.getObr().setMSH(hl7Message.getMsh());
			// �����
			hl7Message.getObr().setPalcerOrderNumber(
					parm.getValue("ORDER_NO") + parm.getValue("ORDER_SEQ"));
			// ����ʱ��
			hl7Message.getObr().setReceiveDate(
					StringTool.getString(parm.getTimestamp("LIS_RE_DATE"),
							"yyyyMMddHHmmss"));
			// ������
			hl7Message.getObr().setReceiveUser(
					Hl7Tool.getInstance().getOperatorName(
							parm.getValue("LIS_RE_USER")));
			// hl7Message.getObr().setReceiveUser(parm.getValue("LIS_RE_USER"));
			// ����ʱ��
			hl7Message.getObr().setSamplingdate(
					StringTool.getString(
							parm.getTimestamp("NS_EXEC_DATE_REAL"),
							"yyyyMMddHHmmss"));
			// ������
			hl7Message.getObr().setSamplinger(
					Hl7Tool.getInstance().getOperatorName(
							parm.getValue("NS_EXEC_CODE_REAL")));
			// hl7Message.getObr().setSamplinger(parm.getValue("NS_EXEC_CODE_REAL"));

			// �����
			hl7Message.getObr().setLabGridNumber(
					parm.getValue("APPLICATION_NO"));
			result = hl7Message.getObr().toString();
		}
		return result;
		
	}

	/**
	 * ҽ����Ϊ�� A ����ҽ�� A^ G RISҽ�� G^O L LISҽ�� L^O O������ķ����ṩ������Դ S����ҽ��:S^
	 * 
	 * @param cat1_type
	 * @return
	 */
	private String getTypeCode(String cat1_type) {
		String typecode = "";
		if (cat1_type.equals("LIS")) {
			typecode = " L^O";
		} else if (cat1_type.equals("RIS")) {
			typecode = "G^O";
		} else if (cat1_type.equals("OPC")) {
			typecode = "S^";
		} else {
			typecode = "A^";
		}
		return typecode;
	}

	/**
	 * ����NET��Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            String
	 * @return String
	 * @throws HL7Exception
	 */
	public String createNet(TParm parm, String type) throws HL7Exception {
		String result = "";
		// ���
		if ("RIS".equals(type)) {
			hl7Message.getNte().setMSH(hl7Message.getMsh());
			String remark = parm.getValue("REMARK");
			if (remark.equals("null") || remark == null || remark.length() == 0)
				remark = "";
			hl7Message.getNte().setRemark(remark);
			hl7Message.getNte().setSeqNo("1");
			hl7Message.getNte().setSourceComment("P");
			// ��ѯ����
			TParm opeParm = Hl7Tool.getInstance().getOpeData(
					parm.getValue("CASE_NO"));
			if (opeParm.getCount() != 0)
				hl7Message.getNte().setOp(opeParm.getValue("OPT_CHN_DESC", 0));
			else
				hl7Message.getNte().setOp("");
			result = hl7Message.getNte().toString();
		}
		// ����
		if ("LIS".equals(type)) {
			hl7Message.getNte().setMSH(hl7Message.getMsh());
			String remark = parm.getValue("REMARK");
			if (remark.equals("null") || remark == null || remark.length() == 0)
				remark = "";
			hl7Message.getNte().setRemark(remark);
			hl7Message.getNte().setSeqNo("1");
			hl7Message.getNte().setSourceComment("P");
			// ��ѯ����
			TParm opeParm = Hl7Tool.getInstance().getOpeData(
					parm.getValue("CASE_NO"));
			if (opeParm.getCount() != 0)
				hl7Message.getNte().setOp(opeParm.getValue("OPT_CHN_DESC", 0));
			else
				hl7Message.getNte().setOp("");
			result = hl7Message.getNte().toString();
		}
		// ICU
		if ("CIS".equals(type)) {
			hl7Message.getNte().setMSH(hl7Message.getMsh());
			String remark = parm.getValue("REMARK");
			if (remark.equals("null") || remark == null || remark.length() == 0)
				remark = "";
			hl7Message.getNte().setRemark(remark);
			hl7Message.getNte().setSeqNo("1");
			hl7Message.getNte().setSourceComment("P");
			// ��ѯ����
			TParm opeParm = Hl7Tool.getInstance().getOpeData(
					parm.getValue("CASE_NO"));
			if (opeParm.getCount() != 0)
				hl7Message.getNte().setOp(opeParm.getValue("OPT_CHN_DESC", 0));
			else
				hl7Message.getNte().setOp("");
			result = hl7Message.getNte().toString();
		}
		return result;
	}

	/**
	 * ����DG1��Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            String
	 * @return String
	 * @throws HL7Exception
	 */
	public String createDg1(TParm parm, String type) throws HL7Exception {
		String result = "";
		String admType = parm.getValue("ADM_TYPE");
		TParm hl7Parm = new TParm();
		// ����
		if ("O".equals(admType)) {
			hl7Parm = Hl7Tool.getInstance().opdDiagrec(
					parm.getValue("CASE_NO"), "O", "Y");
			parm.setData("ICD_MAIN", hl7Parm.getValue("ICD_CODE", 0));
			parm.setData("ICD_MAIN_NAME", Hl7Tool.getInstance().getICDData(
					hl7Parm.getValue("ICD_CODE", 0)).getValue("ICD_CHN_DESC"));
			parm.setData("ICD_DR", hl7Parm.getValue("DR_CODE", 0));
			parm.setData("ICD_DR_NAME", Hl7Tool.getInstance().getOperatorName(
					hl7Parm.getValue("DR_CODE", 0)));
			hl7Parm = Hl7Tool.getInstance().opdDiagrec(
					parm.getValue("CASE_NO"), "O", "N");
			StringBuffer diagBufferStr = new StringBuffer();
			int rowCount = hl7Parm.getCount();
			for (int i = 0; i < rowCount; i++) {
				diagBufferStr.append(hl7Parm.getValue("ICD_CODE", i));
				diagBufferStr.append(hl7Message.getMsh().getColSeparator());
				diagBufferStr.append(Hl7Tool.getInstance().getICDData(
						hl7Parm.getValue("ICD_CODE", i)).getValue(
						"ICD_CHN_DESC"));
				if (i != rowCount - 1) {
					diagBufferStr.append(hl7Message.getMsh().getColSeparator());
				}
			}
			parm.setData("ICD_OTHER", diagBufferStr.toString());
		}
		// ����
		if ("E".equals(admType)) {
			hl7Parm = Hl7Tool.getInstance().opdDiagrec(
					parm.getValue("CASE_NO"), "E", "Y");
			parm.setData("ICD_MAIN", hl7Parm.getValue("ICD_CODE", 0));
			parm.setData("ICD_MAIN_NAME", Hl7Tool.getInstance().getICDData(
					hl7Parm.getValue("ICD_CODE", 0)).getValue("ICD_CHN_DESC"));
			parm.setData("ICD_DR", hl7Parm.getValue("DR_CODE", 0));
			parm.setData("ICD_DR_NAME", Hl7Tool.getInstance().getOperatorName(
					hl7Parm.getValue("DR_CODE", 0)));
			hl7Parm = Hl7Tool.getInstance().opdDiagrec(
					parm.getValue("CASE_NO"), "E", "N");
			StringBuffer diagBufferStr = new StringBuffer();
			int rowCount = hl7Parm.getCount();
			for (int i = 0; i < rowCount; i++) {
				diagBufferStr.append(hl7Parm.getValue("ICD_CODE", i));
				diagBufferStr.append(hl7Message.getMsh().getColSeparator());
				diagBufferStr.append(Hl7Tool.getInstance().getICDData(
						hl7Parm.getValue("ICD_CODE", i)).getValue(
						"ICD_CHN_DESC"));
				if (i != rowCount - 1) {
					diagBufferStr.append(hl7Message.getMsh().getColSeparator());
				}
			}
			parm.setData("ICD_OTHER", diagBufferStr.toString());

		}
		// סԺ
		if ("I".equals(admType)) {
			/*
			 * hl7Parm = Hl7Tool.getInstance().odiDiagrec(
			 * parm.getValue("CASE_NO"), "M", "Y");
			 */
			// liuf
			// �����
			if ("ADM_IN".equals(type)) {
				hl7Parm = Hl7Tool.getInstance().odiDiagrec(
						parm.getValue("CASE_NO"), "I", "Y");
			} else if ("ADM_OUT".equals(type)) {
				hl7Parm = Hl7Tool.getInstance().odiDiagrec(
						parm.getValue("CASE_NO"), "O", "Y");
			}

			else {
				hl7Parm = Hl7Tool.getInstance().odiDiagrec(
						parm.getValue("CASE_NO"), "M", "Y");
			}
			// liuf
			parm.setData("ICD_MAIN", hl7Parm.getValue("ICD_CODE", 0));
			parm.setData("ICD_MAIN_NAME", Hl7Tool.getInstance().getICDData(
					hl7Parm.getValue("ICD_CODE", 0)).getValue("ICD_CHN_DESC"));
			parm.setData("ICD_DR", hl7Parm.getValue("OPT_USER", 0));
			parm.setData("ICD_DR_NAME", Hl7Tool.getInstance().getOperatorName(
					hl7Parm.getValue("OPT_USER", 0)));
			/*
			 * hl7Parm = Hl7Tool.getInstance().odiDiagrec(
			 * parm.getValue("CASE_NO"), "", "N");
			 */

			// �����
			TParm hl7ParmOther = new TParm();
			if ("ADM_IN".equals(type)) {
				hl7ParmOther = Hl7Tool.getInstance().odiDiagrec(
						parm.getValue("CASE_NO"), "I", "N");
			} else if ("ADM_OUT".equals(type)) {
				hl7ParmOther = Hl7Tool.getInstance().odiDiagrec(
						parm.getValue("CASE_NO"), "O", "N");
			} else if ("ADM_TRAN".equals(type) || "CIS".equals(type)
					|| "ADM_TRAN_BED".equals(type)) {
				hl7ParmOther = Hl7Tool.getInstance().odiDiagrec(
						parm.getValue("CASE_NO"), "M", "N");
			} else {
				hl7ParmOther = Hl7Tool.getInstance().odiDiagrec(
						parm.getValue("CASE_NO"), "", "N");
			}

			StringBuffer diagBufferStr = new StringBuffer();
			int rowCount = hl7ParmOther.getCount();
			for (int i = 0; i < rowCount; i++) {
				diagBufferStr.append(hl7ParmOther.getValue("ICD_CODE", i));
				diagBufferStr.append(hl7Message.getMsh().getColSeparator());
				diagBufferStr.append(Hl7Tool.getInstance().getICDData(
						hl7ParmOther.getValue("ICD_CODE", i)).getValue(
						"ICD_CHN_DESC"));
				if (i != rowCount - 1) {
					diagBufferStr.append(hl7Message.getMsh().getColSeparator());
				}
			}
			parm.setData("ICD_OTHER", diagBufferStr.toString());
		}
		// �������
		if ("H".equals(admType)) {
			parm.setData("ICD_MAIN", "");
			parm.setData("ICD_MAIN_NAME", "");
			parm.setData("ICD_DR", "");
			parm.setData("ICD_DR_NAME", "");
			parm.setData("ICD_OTHER", "");
		}
		// ���
		if ("RIS".equals(type)) {
			hl7Message.getDg1().setMSH(hl7Message.getMsh());
			hl7Message.getDg1().setDiagType("A");
			hl7Message.getDg1().setDiagUserCode(parm.getValue("ICD_DR"));
			hl7Message.getDg1().setDiagUserDesc(parm.getValue("ICD_DR_NAME"));
			hl7Message.getDg1().setIcdCode(parm.getValue("ICD_MAIN"));
			hl7Message.getDg1().setIcdDesc(parm.getValue("ICD_MAIN_NAME"));
			hl7Message.getDg1().setDiagString(parm.getValue("ICD_OTHER"));
			result = hl7Message.getDg1().toString();
		}
		// ����
		if ("LIS".equals(type)) {
			hl7Message.getDg1().setMSH(hl7Message.getMsh());
			hl7Message.getDg1().setDiagType("A");
			hl7Message.getDg1().setDiagUserCode(parm.getValue("ICD_DR"));
			hl7Message.getDg1().setDiagUserDesc(parm.getValue("ICD_DR_NAME"));
			hl7Message.getDg1().setIcdCode(parm.getValue("ICD_MAIN"));
			hl7Message.getDg1().setIcdDesc(parm.getValue("ICD_MAIN_NAME"));
			hl7Message.getDg1().setDiagString(parm.getValue("ICD_OTHER"));
			result = hl7Message.getDg1().toString();
		}
		// CIS
		if ("CIS".equals(type) || "ADM_IN".equals(type)
				|| "ADM_OUT".equals(type) || "ADM_TRAN".equals(type)
				|| "ADM_TRAN_BED".equals(type) || "ADM_REF".equals(type)) {
			hl7Message.getDg1().setMSH(hl7Message.getMsh());
			hl7Message.getDg1().setDiagType("A");
			hl7Message.getDg1().setDiagUserCode(parm.getValue("ICD_DR"));
			hl7Message.getDg1().setDiagUserDesc(parm.getValue("ICD_DR_NAME"));
			hl7Message.getDg1().setIcdCode(parm.getValue("ICD_MAIN"));
			hl7Message.getDg1().setIcdDesc(parm.getValue("ICD_MAIN_NAME"));
			hl7Message.getDg1().setDiagString(parm.getValue("ICD_OTHER"));
			result = hl7Message.getDg1().toString();
		}
		// OPE_APPLY
		if ("OPE_APPLY".equals(type)) {
			hl7Message.getDg1().setMSH(hl7Message.getMsh());
			hl7Message.getDg1().setIcdCode(parm.getValue("MAIN_DIAG_CODE"));
			hl7Message.getDg1().setIcdDesc(parm.getValue("MAIN_DIAG_DESC"));
			hl7Message.getDg1().setDiagString(parm.getValue("OTHER_DIAG"));
			result = hl7Message.getDg1().toString();
		}
		return result;
	}

	/**
	 * �����Ա�HL7���� �Ա�(1-��,2-Ů,9-����) M: ���� F: Ů�� O: ���� U: ��֪�� A: ����ȷ��
	 * 
	 * @param sexCode
	 *            String
	 * @return String
	 */
	public String getSexDescHl7(String sexCode) {
		String result = "";
		if ("1".equals(sexCode)) {
			result = "M";
		} else if ("2".equals(sexCode)) {
			result = "F";
		} else {
			result = "O";
		}
		return result;
	}

	/**
	 * ���ػ���HL7���� ����״̬(1-�ѻ�,2-δ��,3-�־�) B: δ�� M:�ѻ� A:�־� O:����
	 * 
	 * @param marriageCode
	 *            String
	 * @return String
	 */
	public String getMarriageCode(String marriageCode) {
		String result = "";
		if ("1".equals(marriageCode))
			result = "B";
		else if ("2".equals(marriageCode))
			result = "M";
		else if ("3".equals(marriageCode))
			result = "A";
		else
			result = "O";
		return result;
	}

	/**
	 * ȡ�����ҽ��
	 * 
	 * @param obj
	 *            Object
	 * @return int
	 */
	public int releaseRisOrderHl7(Object obj) {
		if (obj == null)
			return -1;
		TParm parm = (TParm) obj;
		TParm medData = Hl7Tool.getInstance().getMedData(parm, orderMap);
		// System.out.println("-----------medData-----------" + medData);
		if (medData.getCount() <= 0)
			return -1;
		StringBuffer sendData = new StringBuffer();
		TParm patP = new TParm();
		patP.setData("MR_NO", medData.getValue("MR_NO", 0));
		TParm patInfoParm = Hl7Tool.getInstance().getPatInfo(patP);
		// System.out.println("----------patInfoParm------------" +
		// patInfoParm);
		if (medData.getCount() <= 0 || patInfoParm.getCount() <= 0) {
			return -1;
		}
		patInfoParm = patInfoParm.getRow(0);
		String mshStr = "";
		String pidStr = "";
		String pv1Str = "";
		String orcStr = "";
		String obrStr = "";
		String netStr = "";
		String dg1Str = "";
		try {
			mshStr = createMsh(medData.getRow(0), "RIS");
			pidStr = createPid(patInfoParm, "RIS");
			pv1Str = createPv1(medData.getRow(0), "RIS");
			TParm orcParm = medData.getRow(0);
			orcParm.setData("TYPE", "CA");
			orcStr = createOrc(orcParm, "RIS");
			obrStr = createObr(medData.getRow(0), "RIS");
			netStr = createNet(medData.getRow(0), "RIS");
			dg1Str = createDg1(medData.getRow(0), "RIS");
		} catch (HL7Exception ex1) {
			ex1.printStackTrace();
			return -1;
		}
		// ����MSHͷ
		if (mshStr.length() == 0)
			return -1;
		sendData.append(mshStr);
		sendData.append(enter);
		// ����PIDͷ
		if (pidStr.length() == 0)
			return -1;
		sendData.append(pidStr);
		sendData.append(enter);
		// ����PV1ͷ
		if (pv1Str.length() == 0)
			return -1;
		sendData.append(pv1Str);
		sendData.append(enter);
		// ����ORCͷ
		if (orcStr.length() == 0)
			return -1;
		sendData.append(orcStr);
		sendData.append(enter);
		// ����OBRͷ
		if (obrStr.length() == 0)
			return -1;
		sendData.append(obrStr);
		sendData.append(enter);
		// ����NETͷ
		if (netStr.length() == 0)
			return -1;
		sendData.append(netStr);
		sendData.append(enter);
		// ����DG1ͷ
		if (dg1Str.length() == 0)
			return -1;
		sendData.append(dg1Str);
		try {
			return hl7Message.sendMessage(getSendBaseInfo(medData.getRow(0),
					sendData));
		} catch (HL7Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	/**
	 * ����HL7CIS��Ϣ
	 * 
	 * @param orderList
	 *            List
	 * @return TParm
	 */
	public synchronized TParm Hl7SendLis(List orderList) {
		TParm result = new TParm();
		int rowCount = orderList.size();
		StringBuffer errStr = new StringBuffer();
		if (orderList.isEmpty()) {
			errStr.append("����Ϊ�գ�");
			result.setErrCode(0);
			result.setErrText(errStr.toString());
			return result;
		}
		Map ordM = new HashMap();
		for (int i = 0; i < rowCount; i++) {
			String strTemp = "";
			TParm temp = (TParm) orderList.get(i);
			strTemp = temp.getValue("CAT1_TYPE") + temp.getValue("CASE_NO")
					+ temp.getValue("LAB_NO");
			if (ordM.get(strTemp) != null)
				continue;
			ordM.put(strTemp, strTemp);
			if (createLisRecevieHl7(temp) < 0) {
				errStr.append("����ʧ�ܣ�");
			}
		}
		if (errStr.toString().length() != 0) {
			result.setErrCode(-1);
			result.setErrText(errStr.toString());
		} else {
			result.setErrCode(1);
		}
		return result;
	}

	/**
	 * �����걾������Ϣ
	 * 
	 * @param obj
	 * @return
	 */
	public int createLisRecevieHl7(Object obj) {
		if (obj == null)
			return -1;
		TParm parm = (TParm) obj;
		TParm SendParm = Hl7Tool.getInstance().getLisSendData(parm);
		if (SendParm.getCount() <= 0)
			return -1;
		StringBuffer sendData = new StringBuffer();
		if (SendParm.getCount() <= 0) {
			return -1;
		}
		String mshStr = "";
		String obrStr = "";
		// String orcStr="";
		String orcAndobr = "";
		Map map = new HashMap();
		try {
			mshStr = createMsh(SendParm.getRow(0), "LISSEND");
			int rowCount = SendParm.getCount();
			for (int i = 0; i < rowCount; i++) {
				TParm orcParm = SendParm.getRow(i);
				// shibl 20120830 modify
				if (map.get(orcParm.getValue("APPLICATION_NO", i)) != null) {
					continue;
				}
				map.put(orcParm.getValue("APPLICATION_NO", i), orcParm
						.getValue("APPLICATION_NO", i));
				// orcParm.setData("TYPE", "NW");
				// orcStr = createOrc(orcParm, "LIS");
				// orcAndobr += orcStr + enter;
				obrStr = createObr(orcParm, "LISSEND");
				orcAndobr += obrStr + enter;
			}
		} catch (HL7Exception ex1) {
			ex1.printStackTrace();
			return -1;
		}
		// ����MSHͷ
		if (mshStr.length() == 0)
			return -1;
		sendData.append(mshStr);
		sendData.append(enter);
		// ����OBR
		if (orcAndobr.length() == 0)
			return -1;
		sendData.append(orcAndobr);
		try {
			return hl7Message.sendMessage(getSendBaseInfo(SendParm.getRow(0),
					sendData));
		} catch (HL7Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	/**
	 * �Ƿ���ȡ��������ҽ��(DCҽ��)
	 * 
	 * @param parmRow
	 * @return
	 */
	public boolean IsCancleOrder(TParm parmRow) {
		boolean iscancleFlg = false;
		TParm medData = Hl7Tool.getInstance().getMedData(parmRow,new HashMap());
		String cat1Type = medData.getRow(0).getValue("CAT1_TYPE");
		String status = medData.getRow(0).getValue("STATUS");
		/**
		 * HIS������ҽ��Ĭ��״̬0,��DCҽ�� LIS���պ��д״̬1,��DCҽ�� LISԤԼ���д״̬2,��DCҽ�� ��������
		 * LISȡ��ԤԼ���д״̬3,��DCҽ�� �������� ������д״̬4��HISɾ��ҽ������ʾ���߼����������ɾ��
		 * ȡ��������д״̬5,��DCҽ�� �����ɺ��д״̬6��HISɾ��ҽ������ʾ���߼����������ɾ�� ��������
		 * ������д״̬7��HISɾ��ҽ������ʾ���߼����������ɾ�� ��˺��д״̬8��HISɾ��ҽ������ʾ���߼����������ɾ�� ��������
		 * LIS����DCҽ����д״̬9
		 */
		if (cat1Type.equals("LIS")) {
			if (status.equals("0") || status.equals("1") || status.equals("2")
					|| status.equals("3") || status.equals("5"))
				iscancleFlg = true;
		}
		/**
		 * HIS������ҽ��Ĭ��״̬0,��DCҽ�� GE���պ��д״̬1,��DCҽ�� GEԤԼ���д״̬2,��DCҽ��
		 * GEȡ��ԤԼ���д״̬3,��DCҽ�� ������д״̬4��HISɾ��ҽ���ʾ���߼����������ɾ�� ȡ��������д״̬5,��DCҽ��
		 * �����д״̬6��HISɾ��ҽ���ʾ���߼����������ɾ�� ������д״̬7��HISɾ��ҽ���ʾ���߼����������ɾ��
		 * ��˺��д״̬8��HISɾ��ҽ���ʾ���߼����������ɾ�� GE����DCҽ����д״̬9 GE����DCҽ����д״̬10
		 */
		if (cat1Type.equals("RIS")) {
			if (status.equals("0") || status.equals("1") || status.equals("2")
					|| status.equals("3") || status.equals("5"))
				iscancleFlg = true;
		}
		return iscancleFlg;
	}

	/**
	 * �������Ƿ�ִ��
	 * 
	 * @param parm
	 * @return
	 */
	public boolean IsExeOrder(TParm parm, String admType) {
		boolean ExeFlg = false;
		TParm orderParm = new TParm();
		// �ż���ҽ��
		if (admType.equals("O") || admType.equals("E")) {
			orderParm.setData("ADM_TYPE", admType);
			orderParm.setData("CASE_NO", parm.getValue("CASE_NO"));
			orderParm.setData("RX_NO", parm.getValue("RX_NO"));
			orderParm.setData("SEQ_NO", parm.getValue("SEQ_NO"));
			orderParm = Hl7Tool.getInstance().getopdorderParm(orderParm);
			ExeFlg = orderParm.getBoolean("EXEC_FLG", 0);
		}
		// סԺҽ��
		if (admType.equals("I")) {
			orderParm.setData("CASE_NO", parm.getValue("CASE_NO"));
			orderParm.setData("ORDER_NO", parm.getValue("ORDER_NO"));
			orderParm.setData("ORDER_SEQ", parm.getValue("ORDER_SEQ"));
			orderParm = Hl7Tool.getInstance().getodiorderParm(orderParm);
			ExeFlg = orderParm.getBoolean("EXEC_FLG", 0);
		}
		// ����ҽ��
		if (admType.equals("H")) {
			orderParm.setData("CASE_NO", parm.getValue("CASE_NO"));
			orderParm.setData("SEQ_NO", parm.getValue("SEQ_NO"));
			orderParm = Hl7Tool.getInstance().gethrmorderParm(orderParm);
			ExeFlg = orderParm.getBoolean("EXEC_FLG", 0);

		}
		return ExeFlg;
	}

	/**
	 * RIS html
	 * 
	 * @param parm
	 */
	public void CreatRisHtml(TParm parm) {
		String caseNo = parm.getValue("CASE_NO");
		String mrNo = parm.getValue("MR_NO");
		String applicationNo = parm.getValue("APPLICATION_NO");
		TConfig config = TConfig.getConfig("WEB-INF/config/system/THl7.x");
		String outpath = config.getString("Rishtmlpath.LocalPath");
		String inpath = config.getString("Rishtmlpath.RootDir");
		// modify by wangb 2015/10/12 �����ɵ�HTML���뵥���ճ��̷�������Ӧ���̵�OUT�ļ��� START
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_CAT1_CODE"))) {
			inpath = inpath.replaceAll("RIS", parm.getValue("ORDER_CAT1_CODE"));
		}
		// modify by wangb 2015/10/12 �����ɵ�HTML���뵥���ճ��̷�������Ӧ���̵�OUT�ļ��� END
		String fileServerIP = config.getString("File.IP");
		String port = config.getString("File.PORT");
		Timestamp timestamp = SystemTool.getInstance().getDate();
		String timestampStr = timestamp.toString().replaceAll("-", "");
		timestampStr = timestampStr.replaceAll(" ", "");
		timestampStr = timestampStr.replaceAll(":", "");
		timestampStr = timestampStr.substring(0, 14);
		TSocket socket = new TSocket(fileServerIP, Integer.parseInt(port));
		String filePath = outpath + "/" + caseNo.substring(0, 2) + "/"
				+ caseNo.substring(2, 4) + "/" + mrNo + "/" + applicationNo
				+ ".html";
		byte[] fileByte = TIOM_FileServer.readFile(socket, filePath);
		if (fileByte == null)
			return;
		TIOM_FileServer.writeFile(socket, inpath + "\\" + applicationNo
				+ ".html", fileByte);
	}
	// $$==========zhangs =============$$//
	/**
	 * ����HL7OPE��Ϣ
	 * 
	 * @param orderList
	 *            List
	 * @return TParm
	 */
	public synchronized TParm Hl7MessageOPE(List opeList, String type) {
		TParm result = new TParm();
		StringBuffer errStr = new StringBuffer();
		if (opeList.isEmpty()) {
			errStr.append("����Ϊ�գ�");
			result.setErrCode(0);
			result.setErrText(errStr.toString());
			return result;
		}
		if ("OPE_APPLY".equals(type)) {
			if (createOPEApply(opeList, type) < 0) {
				errStr.append("����ʧ�ܣ�");
			}
		}
		if ("OPE_SCHEDULE".equals(type)) {
			if (createOPESchedule(opeList, type) < 0) {
				errStr.append("����ʧ�ܣ�");
			}
		}
		if (errStr.toString().length() != 0) {
			result.setErrCode(-1);
			result.setErrText(errStr.toString());
		} else {
			result.setErrCode(1);
		}
		return result;
	}

	/**
	 * ��������
	 * 
	 * @param obj
	 *            Object
	 * @return int
	 */
	public int createOPEApply(Object obj, String type) {
		List opeList = (List) obj;
		TParm opeParm = (TParm) opeList.get(0);
		// System.out.println("-=============---" + opeParm);
		String caseNo = opeParm.getValue("CASE_NO");
		// System.out.println("caseNo==="+caseNo);
		StringBuffer hl7Str = new StringBuffer();
		if (caseNo.length() <= 0) {
			return -1;
		}
		// jar cvf javahishl7.jar com
		// ����MSH��Ϣ
		try {
			hl7Str.append(this.createMsh(opeParm, type));
			hl7Str.append(enter);
		} catch (HL7Exception ex) {
			ex.printStackTrace();
			return -1;
		}
		// ����ARQ��Ϣ
		try {
			hl7Str.append(this.createARQ(opeParm, type));
			hl7Str.append(enter);
		} catch (HL7Exception ex) {
			return -1;
		}
		TParm userInfo = Hl7Tool.getInstance().getODIParm(opeParm);
		String mrNo = userInfo.getValue("MR_NO", 0);
		TParm patP = new TParm();
		patP.setData("MR_NO", mrNo);
		TParm patInfoParm = Hl7Tool.getInstance().getPatInfo(patP);
		// System.out.println("patInfoParm===" + patInfoParm);
		TParm patInfoParmPid = patInfoParm.getRow(0);
		patInfoParmPid.setData("CASE_NO", opeParm.getValue("CASE_NO"));

		// ����PID��Ϣ
		try {
			hl7Str.append(createPid(patInfoParmPid, type));
			hl7Str.append(enter);
		} catch (HL7Exception ex1) {
			return -1;
		}
		TParm pv1Parm = userInfo.getRow(0);
		pv1Parm.setData("ADM_TYPE", "I");
		// ����PV1��Ϣ
		try {
			hl7Str.append(createPv1(pv1Parm, type));
			hl7Str.append(enter);
		} catch (HL7Exception ex2) {
			return -1;
		}
		// ����AIS��Ϣ
		Vector Op_Table = (Vector) opeParm.getVector("OP_TABLE");
		// System.out.println("Op_Table===" + Op_Table);
		TParm aisTParm = new TParm();

		for (int i = 0; i < Op_Table.size(); i++) {
			Vector vTempAis = ((Vector) ((Vector) Op_Table.get(i)).get(0));
			if (String.valueOf(vTempAis.get(2)).trim().length() > 0) {
				aisTParm.setData("OP_CODE", String.valueOf(vTempAis.get(2))
						.trim());
				aisTParm.setData("OP_DESC", Hl7Tool.getInstance().getOpeDESC(
						String.valueOf(vTempAis.get(2)).trim()));
				aisTParm.setData("OP_DATE", opeParm.getValue("OP_DATE"));
				aisTParm.setData("TIME_NEED", opeParm.getValue("TIME_NEED"));
				aisTParm.setData("UNIT_OF_TIME", "HOUR");
				aisTParm.setData("ANA_CODE", opeParm.getValue("ANA_CODE"));
				aisTParm.setData("ANA_DESC", opeParm.getValue("ANA_DESC"));
				try {
					hl7Str.append(createAIS(aisTParm, type));
					hl7Str.append(enter);
				} catch (Exception ex3) {
					return -1;
				}

			}

		}

		// ����AIL��Ϣ
		try {
			hl7Str.append(createAIL(opeParm, type));
			hl7Str.append(enter);
		} catch (HL7Exception ex2) {
			return -1;
		}
		// ����DG1��Ϣ
		TParm dg1Parm = new TParm();
		Vector Daily_Table = (Vector) opeParm.getVector("DAILY_TABLE");
		String diagTemp = "";
		for (int i = 0; i < Daily_Table.size(); i++) {
			Vector vTempDg1 = ((Vector) ((Vector) Daily_Table.get(i)).get(0));
			if (String.valueOf(vTempDg1.get(2)).trim().length() > 0) {
				if (String.valueOf(vTempDg1.get(1)).trim().equals("Y")) {
					dg1Parm.setData("MAIN_DIAG_CODE", String.valueOf(
							vTempDg1.get(2)).trim());
					dg1Parm.setData("MAIN_DIAG_DESC", Hl7Tool.getInstance()
							.getICDData(String.valueOf(vTempDg1.get(2)).trim())
							.getValue("ICD_CHN_DESC"));
				} else {
					diagTemp = diagTemp
							+ String.valueOf(vTempDg1.get(2)).trim()
							+ "^"
							+ Hl7Tool.getInstance().getICDData(
									String.valueOf(vTempDg1.get(2)).trim())
									.getValue("ICD_CHN_DESC");
					if (i < (Daily_Table.size() - 2))
						diagTemp = diagTemp + "~";
				}
			}

		}
		// dg1Parm.setData("ADM_TYPE", "I");
		// dg1Parm.setData("MAIN_DIAG_CODE", "");
		// dg1Parm.setData("MAIN_DIAG_DESC",
		// Hl7Tool.getInstance().getICDData("").getValue("ICD_CHN_DESC"));
		dg1Parm.setData("OTHER_DIAG", diagTemp);
		dg1Parm.setData("ADM_TYPE", "I");
		dg1Parm.setData("CASE_NO", opeParm.getValue("CASE_NO"));
		try {

			hl7Str.append(createDg1(dg1Parm, type));
			hl7Str.append(enter);
		} catch (Exception ex3) {
			return -1;
		}
		// ����AIP��Ϣ
		Vector AST_Table = (Vector) opeParm.getVector("TABLE_BOOK_AST");
		// System.out.println("AST_Table===" + AST_Table);
		// ����ҽ��
		TParm aipTParm = new TParm();
		aipTParm.setData("BOOK_AST_ID", opeParm.getValue("MAIN_SURGEON"));
		aipTParm.setData("BOOK_AST_NAME", Hl7Tool.getInstance().getDrName(
				opeParm.getValue("MAIN_SURGEON").trim()));
		aipTParm.setData("ROLE", "1");
		try {

			hl7Str.append(createAIP(aipTParm, type));
			hl7Str.append(enter);
		} catch (Exception ex3) {
			return -1;
		}
		// ����
		for (int i = 0; i < AST_Table.size(); i++) {
			Vector vTemp = ((Vector) ((Vector) AST_Table.get(i)).get(0));
			if (String.valueOf(vTemp.get(2)).trim().length() > 0) {
				aipTParm.setData("BOOK_AST_ID", String.valueOf(vTemp.get(2))
						.trim());
				aipTParm.setData("BOOK_AST_NAME", Hl7Tool.getInstance()
						.getDrName(String.valueOf(vTemp.get(2)).trim()));
				aipTParm.setData("ROLE", "3");
				try {

					hl7Str.append(createAIP(aipTParm, type));
					hl7Str.append(enter);
				} catch (Exception ex3) {
					return -1;
				}

			}

		}

		try {

			opeParm.setData("APPLICATION_NO", opeParm.getValue("OPBOOK_SEQ"));
			// System.out.println("hl7Str===");
			// System.out.println(this.getSendBaseInfo(opeParm, hl7Str));
			return hl7Message
					.sendMessage(this.getSendBaseInfo(opeParm, hl7Str));
		} catch (HL7Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	/**
	 * �����ų�
	 * 
	 * @param obj
	 *            Object
	 * @return int
	 */
	public int createOPESchedule(Object obj, String type) {
		List opeList = (List) obj;
		TParm opeParm = (TParm) opeList.get(0);
		// System.out.println("-=============---" + opeParm);
		String caseNo = opeParm.getValue("OPBOOK_SEQ");
		// System.out.println("caseNo==="+caseNo);
		StringBuffer hl7Str = new StringBuffer();
		if (caseNo.length() <= 0) {
			return -1;
		}
		// ����MSH��Ϣ
		try {
			hl7Str.append(this.createMsh(opeParm, type));
			hl7Str.append(enter);
		} catch (HL7Exception ex) {
			ex.printStackTrace();
			return -1;
		}

		String mrNo = opeParm.getValue("MR_NO", 0);
		TParm patP = new TParm();
		patP.setData("MR_NO", mrNo);
		TParm patInfoParm = Hl7Tool.getInstance().getPatInfo(patP);
		// System.out.println("patInfoParm===" + patInfoParm);
		TParm patInfoParmPid = patInfoParm.getRow(0);
		patInfoParmPid.setData("CASE_NO", opeParm.getValue("CASE_NO", 0));

		// ����PID��Ϣ
		try {
			hl7Str.append(createPid(patInfoParmPid, type));
			hl7Str.append(enter);
		} catch (HL7Exception ex1) {
			return -1;
		}
		// System.out.println("hl7Str===" + hl7Str);
		// ����SCH��Ϣ
		try {
			hl7Str.append(createSCH(opeParm, type));
			hl7Str.append(enter);
		} catch (HL7Exception ex1) {
			return -1;
		}
		// System.out.println("hl7Str===" + hl7Str);
		// //����AIS��Ϣ
		Vector Op_Table = new Vector();
		Op_Table.add(opeParm.getValue("OP_CODE1", 0));
		Op_Table.add(opeParm.getValue("OP_CODE2", 0));
		TParm aisTParm = new TParm();
		TParm ANA_DESC = getANADesc();
		for (int i = 0; i < Op_Table.size(); i++) {
			if (String.valueOf(Op_Table.get(i)).trim().length() > 0) {
				aisTParm.setData("OP_CODE", String.valueOf(Op_Table.get(i))
						.trim());
				aisTParm.setData("OP_DESC", Hl7Tool.getInstance().getOpeDESC(
						String.valueOf(Op_Table.get(i)).trim()));
				aisTParm.setData("OP_DATE", opeParm.getValue("OP_DATE", 0));
				aisTParm.setData("TIME_NEED", opeParm.getValue("TIME_NEED", 0));
				aisTParm.setData("UNIT_OF_TIME", "HOUR");
				aisTParm.setData("ANA_CODE", opeParm.getValue("ANA_CODE", 0));
				aisTParm.setData("ANA_DESC", ANA_DESC.getValue(opeParm
						.getValue("ANA_CODE", 0)));
				try {
					hl7Str.append(createAIS(aisTParm, type));
					hl7Str.append(enter);
				} catch (Exception ex3) {
					return -1;
				}

			}
		}
		// System.out.println("hl7Str===" + hl7Str);
		// ����AIL��Ϣ
		try {
			hl7Str.append(createAIL(opeParm, type));
			hl7Str.append(enter);
		} catch (HL7Exception ex2) {
			return -1;
		}
		// System.out.println("hl7Str===" + hl7Str);
		// ����AIP��Ϣ
		// 1������ҽ����2������ҽ����3�� �������֣�4���������֣�5����е��ʿ��6��Ѳ�ػ�ʿ��7������ѭ��ʦ
		// ����ҽ��
		TParm aipTParm = new TParm();
		aipTParm.setData("BOOK_AST_ID", opeParm.getValue("MAIN_SURGEON", 0));
		aipTParm.setData("BOOK_AST_NAME", Hl7Tool.getInstance().getDrName(
				opeParm.getValue("MAIN_SURGEON", 0).trim()));
		aipTParm.setData("ROLE", "1");
		try {

			hl7Str.append(createAIP(aipTParm, type));
			hl7Str.append(enter);
		} catch (Exception ex3) {
			return -1;
		}

		// ����
		Vector AST_Table = new Vector();
		AST_Table.add(opeParm.getValue("BOOK_AST_1", 0));
		AST_Table.add(opeParm.getValue("BOOK_AST_2", 0));
		AST_Table.add(opeParm.getValue("BOOK_AST_3", 0));
		AST_Table.add(opeParm.getValue("BOOK_AST_4", 0));
		for (int i = 0; i < AST_Table.size(); i++) {

			if (String.valueOf(AST_Table.get(i)).trim().length() > 0) {
				aipTParm.setData("BOOK_AST_ID", String
						.valueOf(AST_Table.get(i)).trim());
				aipTParm.setData("BOOK_AST_NAME", Hl7Tool.getInstance()
						.getDrName(String.valueOf(AST_Table.get(i)).trim()));
				aipTParm.setData("ROLE", "3");
				try {

					hl7Str.append(createAIP(aipTParm, type));
					hl7Str.append(enter);
				} catch (Exception ex3) {
					return -1;
				}

			}

		}
		// Ѳ�ػ�ʿ
		Vector CIRCULE_Table = new Vector();
		CIRCULE_Table.add(opeParm.getValue("CIRCULE_USER1", 0));
		CIRCULE_Table.add(opeParm.getValue("CIRCULE_USER2", 0));
		CIRCULE_Table.add(opeParm.getValue("CIRCULE_USER3", 0));
		CIRCULE_Table.add(opeParm.getValue("CIRCULE_USER4", 0));
		for (int i = 0; i < CIRCULE_Table.size(); i++) {

			if (String.valueOf(CIRCULE_Table.get(i)).trim().length() > 0) {
				aipTParm.setData("BOOK_AST_ID", String.valueOf(
						CIRCULE_Table.get(i)).trim());
				aipTParm
						.setData("BOOK_AST_NAME", Hl7Tool.getInstance()
								.getDrName(
										String.valueOf(CIRCULE_Table.get(i))
												.trim()));
				aipTParm.setData("ROLE", "6");
				try {

					hl7Str.append(createAIP(aipTParm, type));
					hl7Str.append(enter);
				} catch (Exception ex3) {
					return -1;
				}

			}

		}
		// ��е��ʿ
		Vector SCRUB_Table = new Vector();
		SCRUB_Table.add(opeParm.getValue("SCRUB_USER1", 0));
		SCRUB_Table.add(opeParm.getValue("SCRUB_USER2", 0));
		SCRUB_Table.add(opeParm.getValue("SCRUB_USER3", 0));
		SCRUB_Table.add(opeParm.getValue("SCRUB_USER4", 0));
		for (int i = 0; i < SCRUB_Table.size(); i++) {

			if (String.valueOf(SCRUB_Table.get(i)).trim().length() > 0) {
				aipTParm.setData("BOOK_AST_ID", String.valueOf(
						SCRUB_Table.get(i)).trim());
				aipTParm.setData("BOOK_AST_NAME", Hl7Tool.getInstance()
						.getDrName(String.valueOf(SCRUB_Table.get(i)).trim()));
				aipTParm.setData("ROLE", "5");
				try {

					hl7Str.append(createAIP(aipTParm, type));
					hl7Str.append(enter);
				} catch (Exception ex3) {
					return -1;
				}

			}

		}
		// ����ҽ��
		if (opeParm.getValue("ANA_USER1", 0).trim().length() > 0) {
			aipTParm.setData("BOOK_AST_ID", opeParm.getValue("ANA_USER1", 0));
			aipTParm.setData("BOOK_AST_NAME", Hl7Tool.getInstance().getDrName(
					opeParm.getValue("ANA_USER1", 0).trim()));
			aipTParm.setData("ROLE", "2");

			try {

				hl7Str.append(createAIP(aipTParm, type));
				hl7Str.append(enter);
			} catch (Exception ex3) {
				return -1;
			}
		}
		// ��������
		if (opeParm.getValue("ANA_USER2", 0).trim().length() > 0) {
			aipTParm.setData("BOOK_AST_ID", opeParm.getValue("ANA_USER2", 0));
			aipTParm.setData("BOOK_AST_NAME", Hl7Tool.getInstance().getDrName(
					opeParm.getValue("ANA_USER2", 0).trim()));
			aipTParm.setData("ROLE", "4");
			try {

				hl7Str.append(createAIP(aipTParm, type));
				hl7Str.append(enter);
			} catch (Exception ex3) {
				return -1;
			}
		}
		// ����ѭ��ʦ
		Vector EXTRA_Table = new Vector();
		EXTRA_Table.add(opeParm.getValue("EXTRA_USER1", 0));
		EXTRA_Table.add(opeParm.getValue("EXTRA_USER2", 0));
		for (int i = 0; i < EXTRA_Table.size(); i++) {
			if (String.valueOf(EXTRA_Table.get(i)).trim().length() > 0) {
				aipTParm.setData("BOOK_AST_ID", String.valueOf(
						EXTRA_Table.get(i)).trim());
				aipTParm.setData("BOOK_AST_NAME", Hl7Tool.getInstance()
						.getDrName(String.valueOf(EXTRA_Table.get(i)).trim()));
				aipTParm.setData("ROLE", "7");
				try {
					hl7Str.append(createAIP(aipTParm, type));
					hl7Str.append(enter);
				} catch (Exception ex3) {
					return -1;
				}
			}
		}
		
		try {

			opeParm.setData("APPLICATION_NO", opeParm.getValue("OPBOOK_SEQ"));
//			opeParm.setData("CASE_NO", opeParm.getValue("CASE_NO", 0));
			// System.out.println("hl7Str===");
			// System.out.println(this.getSendBaseInfo(opeParm, hl7Str));
			return hl7Message
					.sendMessage(this.getSendBaseInfo(opeParm, hl7Str));
		} catch (HL7Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	/**
	 * ����ARQ��Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return String
	 */
	public String createARQ(TParm parm, String type) throws HL7Exception {
		// ��������
		String result = "";
		if ("OPE_APPLY".equals(type)) {
			hl7Message.getArq().setMSH(hl7Message.getMsh());
			hl7Message.getArq().setOpeBookSeq(parm.getValue("OPBOOK_SEQ"));
			hl7Message.getArq().setPursueReason(parm.getValue("REMARK"));
			String UrgBladeFlg = "";
			if (parm.getValue("URGBLADE_FLG").equals("Y")) {
				UrgBladeFlg = "EMERGENCY";
			} else {
				UrgBladeFlg = "ROUTINE";
			}

			hl7Message.getArq().setUrgBladeFlg(UrgBladeFlg);
			result = hl7Message.getArq().toString();
		}
		return result;
	}

	/**
	 * ����AIS��Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return String
	 */
	public String createAIS(TParm parm, String type) throws HL7Exception {
		// ��������
		String result = "";
		hl7Message.getAis().setMSH(hl7Message.getMsh());
		hl7Message.getAis().setOpCode(
				parm.getValue("OP_CODE") + "^" + parm.getValue("OP_DESC"));

		if ("OPE_APPLY".equals(type)) {
			String date = parm.getValue("OP_DATE");
			hl7Message.getAis().setOpDate(
					date.substring(0, 4) + "-" + date.substring(4, 6) + "-"
							+ date.substring(6, 8) + " "
							+ date.substring(8, 10) + ":"
							+ date.substring(10, 12) + ":"
							+ date.substring(12, 14));

		}
		if ("OPE_SCHEDULE".equals(type)) {
			hl7Message.getAis().setOpDate(parm.getValue("OP_DATE"));
		}
		hl7Message.getAis().setTimeNeed(parm.getValue("TIME_NEED"));
		hl7Message.getAis().setUnitOfTime(parm.getValue("UNIT_OF_TIME"));
		hl7Message.getAis().setANACode(parm.getValue("ANA_CODE"));
		hl7Message.getAis().setANADesc(parm.getValue("ANA_DESC"));
		result = hl7Message.getAis().toString();
		return result;
	}

	/**
	 * ����AIL��Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return String
	 */
	public String createAIL(TParm parm, String type) throws HL7Exception {
		// ��������
		String result = "";
		if ("OPE_APPLY".equals(type)) {

			hl7Message.getAil().setMSH(hl7Message.getMsh());
			hl7Message.getAil().setRoomName(parm.getValue("ROOM_NAME"));
			hl7Message.getAil().setDeptCode("030503");
			hl7Message.getAil().setDeptDesc("������");
			result = hl7Message.getAil().toString();
		}
		if (type.equals("OPE_SCHEDULE")) {

			hl7Message.getAil().setMSH(hl7Message.getMsh());
			hl7Message.getAil().setRoomName(parm.getValue("ROOM_NAME"));
			hl7Message.getAil().setDeptCode("030503");
			hl7Message.getAil().setDeptDesc("������");
			result = hl7Message.getAil().toString();
		}
		return result;
	}

	/**
	 * ����AIP��Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return String
	 */
	public String createAIP(TParm parm, String type) throws HL7Exception {
		// ��������
		String result = "";
		if ("OPE_APPLY".equals(type) || "OPE_SCHEDULE".equals(type)) {

			hl7Message.getAip().setMSH(hl7Message.getMsh());
			hl7Message.getAip().setID(parm.getValue("BOOK_AST_ID"));
			hl7Message.getAip().setName(parm.getValue("BOOK_AST_NAME"));
			hl7Message.getAip().setRole(parm.getValue("ROLE"));
			result = hl7Message.getAip().toString();
		}
		return result;
	}

	/**
	 * ����SCH��Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return String
	 */
	public String createSCH(TParm parm, String type) throws HL7Exception {
		// ��������
		String result = "";
		if ("OPE_SCHEDULE".equals(type)) {
			// BOOKED�����ų̣�COMPLETE����������ɣ�
			// CANCELLED��������ȡ����DC���������ж�

			String status[] = { "", "BOOKED", "COMPLETE", "CANCELLED", "DC" };
			hl7Message.getSch().setMSH(hl7Message.getMsh());
			hl7Message.getSch().setOpeBookSeq(parm.getValue("OPBOOK_SEQ", 0));
			if ((!parm.getValue("STATE").equals(""))
					&& (!parm.getValue("STATE").equals("null"))
					&& (!parm.getValue("STATE").equals("NULL")))
				hl7Message.getSch().setSchedulingStatus(
						status[parm.getInt("STATE", 0)]);
			result = hl7Message.getSch().toString();
		}
		return result;
	}

	public TParm getANADesc() {
		TParm ANADesc = new TParm();
		ANADesc.setData("01", "ȫ������");
		ANADesc.setData("02", "������������");
		ANADesc.setData("03", "ӲĤ������");
		ANADesc.setData("04", "��������");
		ANADesc.setData("05", "�ֲ���������");
		ANADesc.setData("06", "������������");
		ANADesc.setData("07", "��������");
		ANADesc.setData("08", "����Ĥ��ǻ����");
		ANADesc.setData("09", "ȫƾ��������");
		ANADesc.setData("10", "�����Խ�ѹ����");
		ANADesc.setData("11", "�������");
		ANADesc.setData("12", "֧��������");
		ANADesc.setData("13", "��������");
		return ANADesc;
	}
	// $$==========zhangs =============$$//
	
	/**
	 * ����Rf1��Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @throws HL7Exception
	 */
	public String createRf1(TParm parm) throws HL7Exception {
		hl7Message.getRf1().setMSH(hl7Message.getMsh());
		// ת�����
		hl7Message.getRf1().setReferralCategory(parm.getValue("ADM_TYPE"));
		// ת�ﵥ��
		hl7Message.getRf1().setReferralNo(parm.getValue("REFERRAL_NO"));
		// ��Ч����
		hl7Message.getRf1().setEffectiveDate(parm.getValue("REFERRAL_DATE").replaceAll("/", "-"));
		// ��������
		hl7Message.getRf1().setProcessDate(parm.getValue("APPLY_DATE").replaceAll("/", "-"));
		// ת��ԭ��
		hl7Message.getRf1().setReferralReason("O");
		// ת��ת��Ժ��
		hl7Message.getRf1().setReferralInHosptitalCode(parm.getValue("APPLY_HOSP_CODE"));
		hl7Message.getRf1().setReferralInHosptitalName(parm.getValue("APPLY_HOSP_DESC"));
		// ת��ת��Ժ��
		hl7Message.getRf1().setReferralOutHosptitalCode(parm.getValue("ACCEPT_HOSP_CODE"));
		hl7Message.getRf1().setReferralOutHosptitalName(parm.getValue("ACCEPT_HOSP_DESC"));
		// Ϊ������л��е���hl7���������������base64����
		String referralGrounds = (new BASE64Encoder()).encode(
				parm.getValue("REFERRAL_GROUNDS").getBytes()).replaceAll(
				"\r\n", "");
		// ת������
		hl7Message.getRf1().setReferralGrounds(referralGrounds);
		// Ϊ������л��е���hl7���������������base64����
		String diseaseSummary = (new BASE64Encoder()).encode(
				parm.getValue("DISEASE_SUMMARY").getBytes()).replaceAll("\r\n",
				"");
		// ����ժҪ
		hl7Message.getRf1().setDiseaseSummary(diseaseSummary);
		
		String result = hl7Message.getRf1().toString();
		return result;
	}
	/**
	 * ����CS5ҽ����Ϣ
	 * 
	 * @param obj
	 * @param type
	 * @return
	 */
	public int createICUOrderCS5(Object obj, String type) {
		List orderList = (List) obj;
		TParm orderParm = (TParm) orderList.get(0);
		String caseNo = orderParm.getValue("CASE_NO", 0);
		StringBuffer hl7Str = new StringBuffer();
		if (caseNo.length() <= 0) {
			return -1;
		}
		// ����MSH��Ϣ
		try {
			hl7Str.append(this.createMsh(new TParm(), "ORDERCS5"));
			hl7Str.append(enter);
		} catch (HL7Exception ex) {
			return -1;
		}
		TParm userInfo = Hl7Tool.getInstance().getODIParm(orderParm.getRow(0));
		String mrNo = userInfo.getValue("MR_NO", 0);
		TParm patP = new TParm();
		patP.setData("MR_NO", mrNo);
		TParm patInfoParm = Hl7Tool.getInstance().getPatInfo(patP);
		// ����PID��Ϣ
		try {
			hl7Str.append(createPid(patInfoParm.getRow(0), "CIS"));
			hl7Str.append(enter);
		} catch (HL7Exception ex1) {
			return -1;
		}
		TParm pv1Parm = userInfo.getRow(0);
		pv1Parm.setData("ADM_TYPE", "I");
		// ����PV1��Ϣ
		try {
			hl7Str.append(createPv1(pv1Parm, "CIS"));
			hl7Str.append(enter);
		} catch (HL7Exception ex2) {
			return -1;
		}
		// ҽ������ORC��Ϣ������OBR��Ϣ
		String orcStr = "";
		TParm orcParm = new TParm();
		String obrStr = "";
		TParm obrParm = new TParm();
		for (int i = 0; i < orderParm.getCount("CASE_NO"); i++) {
			orcParm = orderParm.getRow(i);
			if (orderParm.getValue("CAT1_TYPE", i).equals("PHA")) {
				if (orderParm.getValue("LINK_NO", i).equals("")
						|| (!orderParm.getValue("LINK_NO", i).equals("") && orderParm
								.getValue("LINKMAIN_FLG", i).equals("Y")))
					orcParm.setData("TYPE", "NW");
				else if (!orderParm.getValue("LINK_NO", i).equals("")
						&& orderParm.getValue("LINKMAIN_FLG", i).equals("N"))
					orcParm.setData("TYPE", "CH");
				orcParm.setData("ORDER_TYPE", type);
				try {
					orcStr = createOrc(orcParm, "ORDER");
				} catch (HL7Exception e) {
					return -1;
				}
				hl7Str.append(orcStr);
				hl7Str.append(enter);
				obrParm = orderParm.getRow(i);
				obrParm.setData("TYPE", type);
				try {
					obrStr = createObr(obrParm, "CS5");
				} catch (HL7Exception e) {
					return -1;
				}
				hl7Str.append(obrStr);
				hl7Str.append(enter);
			}
		}
		TParm netP = new TParm();
		netP.setData("MR_NO", mrNo);
		// ����NET��Ϣ
		try {
			hl7Str.append(createNet(netP, "CIS"));
			hl7Str.append(enter);
		} catch (Exception ex3) {
			return -1;
		}
		// ����DG1��Ϣ
		try {
			TParm dg1Parm = orderParm.getRow(0);
			dg1Parm.setData("ADM_TYPE", "I");
			hl7Str.append(createDg1(dg1Parm, "CIS"));
			hl7Str.append(enter);
		} catch (Exception ex3) {
			return -1;
		}
		try {
			return hl7Message.sendMessage(this.getSendBaseInfo(orderParm
					.getRow(0), hl7Str));
		} catch (HL7Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * �����״��ĵ�HL7��Ϣ
	 * 
	 * @param triageNo ���˺�
	 * @return result
	 */
	public synchronized TParm sendFirstEcg(String triageNo) {
		TParm result = new TParm();
		if (StringUtils.isEmpty(triageNo)) {
			result.setErr(-1, "���˺�Ϊ��");
			return result;
		}

		StringBuffer sendData = new StringBuffer();
		TParm parm = new TParm();
		parm.setData("FIRST_ECG_FLG", "Y");// �״��ĵ�ע��
		parm.setData("ADM_TYPE", "E");
		parm.setData("TRIAGE_NO", triageNo);// ������˺�
		parm.setData("CAT1_TYPE", "RIS");
		parm.setData("ORDER_CAT1_CODE", "ECC");
		parm.setData("SEND_COMP", "ECC");
		parm.setData("TYPE", "NW");// NW_����ҽ��,NA_ȡ��ҽ��

		// ȡ�������ĵ�ҽ��
		String firstEcgOrderCode = TConfig.getSystemValue("FIRST_ECG_ORDER");
		if (StringUtils.isEmpty(firstEcgOrderCode)) {
			result.setErr(-1, "�����ļ���δ���������ĵ�ҽ��,��Ϣ����ʧ��");
			return result;
		}

		parm.setData("ORDER_CODE", firstEcgOrderCode);
		// ��ѯ�շ��ֵ�����
		TParm sysfeeResult = Hl7Tool.getInstance().getSysFeeData(parm);
		if (sysfeeResult.getErrCode() < 0) {
			result.setErr(-1, "��ѯ�շ��ֵ����ݴ���");
			return result;
		} else if (sysfeeResult.getCount() < 1) {
			result.setErr(-1, "�շ��ֵ�������δ��ѯ�������ĵ�ҽ��");
			return result;
		}

		String mshStr = "";
		String pidStr = "";
		String pv1Str = "";
		String orcStr = "";
		String obrStr = "";

		try {
			mshStr = createMsh(parm, "RIS");
			pidStr = createPid(parm, "RIS");
			pv1Str = createPv1(parm, "RIS");
			orcStr = createOrc(parm, "RIS");
			obrStr = createObr(sysfeeResult.getRow(0), "RIS");
		} catch (HL7Exception ex) {
			ex.printStackTrace();
			result.setErr(-1, "��Ϣ����ʧ��");
			System.out.println(ex.getMessage());
			return result;
		}

		// ����MSHͷ
		if (mshStr.length() == 0) {
			result.setErr(-1, "����MSHͷʧ��");
			return result;
		}
		sendData.append(mshStr);
		sendData.append(enter);
		// ����PIDͷ
		if (pidStr.length() == 0) {
			result.setErr(-1, "����PIDͷʧ��");
			return result;
		}
		sendData.append(pidStr);
		sendData.append(enter);
		// ����PV1ͷ
		if (pv1Str.length() == 0) {
			result.setErr(-1, "����PV1ͷʧ��");
			return result;
		}
		sendData.append(pv1Str);
		sendData.append(enter);
		// ����ORCͷ
		if (orcStr.length() == 0) {
			result.setErr(-1, "����ORCͷʧ��");
			return result;
		}
		sendData.append(orcStr);
		sendData.append(enter);
		// ����OBRͷ
		if (obrStr.length() == 0) {
			result.setErr(-1, "����OBRͷʧ��");
			return result;
		}
		sendData.append(obrStr);
		sendData.append(enter);

		try {
			int sendResult = hl7Message.sendMessage(getSendBaseInfo(parm,
					sendData));
			if (sendResult < 0) {
				result.setErr(-1, "��Ϣ����ʧ��");
				return result;
			}
		} catch (HL7Exception ex) {
			ex.printStackTrace();
			result.setErr(-1, "��Ϣ����ʧ��");
			System.out.println(ex.getMessage());
			return result;
		}

		return result;
	}
	
	/**
	 * ���ͽ������������Ϣ
	 * 
	 * @param obj obj����
	 * @param type ����
	 * @return result �����
	 */
	public TParm Hl7MessageOPERis(TParm parm) {
		TParm result = new TParm();
		String opbookSeq = parm.getValue("OPBOOK_SEQ");
		if (opbookSeq.length() <= 0) {
			result.setErr(-1, "�������뵥��Ϊ��");
			return result;
		}
		
		TParm sysOpeIcd = new TParm(TJDODBTool.getInstance().select(
				"SELECT * FROM SYS_OPERATIONICD"));
		
		parm.setData("ADM_TYPE", "I");
		parm.setData("SEND_COMP", "RIS");
		parm.setData("APPLICATION_NO", opbookSeq);
		parm.setData("ORDER_CAT1_CODE", "RIS");
		parm.setData("ORDER_NO", opbookSeq);
		parm.setData("SEQ_NO", "1");
		parm.setData("ORDER_CODE", parm.getValue("OP_CODE1"));
		parm.setData("TYPE", "NW");
		parm.setData("MSG_TYPE", "OPE_RIS");
		// �������
		parm.setData("ORDER_DEPT_CODE", parm.getValue("BOOK_DEPT_CODE"));
		
		String opeDesc1 = "";
		for (int i = 0; i < sysOpeIcd.getCount(); i++) {
			if (StringUtils.equals(parm.getValue("OP_CODE1"), sysOpeIcd
					.getValue("OPERATION_ICD", i))) {
				opeDesc1 = sysOpeIcd.getValue("OPT_CHN_DESC", i);
			} else if (StringUtils.equals(parm.getValue("OP_CODE2"), sysOpeIcd
					.getValue("OPERATION_ICD", i))) {
				parm.setData("OP_DESC2", sysOpeIcd
						.getValue("OPT_CHN_DESC", i));
			}
		}
		parm.setData("ORDER_DESC", opeDesc1);
		
		StringBuffer sendData = new StringBuffer();
		
		// �õ�����������Ϣ
		TParm patInfoParm = Hl7Tool.getInstance().getPatInfo(parm);
		if (patInfoParm.getCount() <= 0) {
			result.setErr(-1, "δ�ҵ�����������Ϣ");
			return result;
		}
		
		patInfoParm = patInfoParm.getRow(0);
		String mshStr = "";
		String pidStr = "";
		String pv1Str = "";
		String orcStr = "";
		String obrStr = "";
		String netStr = "";
		String dg1Str = "";
		try {
			mshStr = createMsh(parm, "RIS");
			pidStr = createPid(patInfoParm, "RIS");
			pv1Str = createPv1(parm, "RIS");
			orcStr = createOrc(parm, "RIS");
			obrStr = createObr(parm, "RIS");
			netStr = createNet(parm, "RIS");
			dg1Str = createDg1(parm, "RIS");
		} catch (HL7Exception ex1) {
			result.setErr(-1, ex1.getMessage());
			return result;
		}
		// ����MSHͷ
		if (mshStr.length() == 0) {
			result.setErr(-1, "����MSHͷ����");
			return result;
		}
		sendData.append(mshStr);
		sendData.append(enter);
		// ����PIDͷ
		if (pidStr.length() == 0) {
			result.setErr(-1, "����PIDͷ����");
			return result;
		}
		sendData.append(pidStr);
		sendData.append(enter);
		// ����PV1ͷ
		if (pv1Str.length() == 0) {
			result.setErr(-1, "����PV1ͷ����");
			return result;
		}
		sendData.append(pv1Str);
		sendData.append(enter);
		// ����ORCͷ
		if (orcStr.length() == 0) {
			result.setErr(-1, "����ORCͷ����");
			return result;
		}
		sendData.append(orcStr);
		sendData.append(enter);
		// ����OBRͷ
		if (obrStr.length() == 0) {
			result.setErr(-1, "����OBRͷ����");
			return result;
		}
		sendData.append(obrStr);
		sendData.append(enter);
		// ����NETͷ
		if (netStr.length() == 0) {
			result.setErr(-1, "����NETͷ����");
			return result;
		}
		sendData.append(netStr);
		sendData.append(enter);
		// ����DG1ͷ
		if (dg1Str.length() == 0) {
			result.setErr(-1, "����DG1ͷ����");
			return result;
		}
		sendData.append(dg1Str);
		try {
			hl7Message.sendMessage(getSendBaseInfo(parm, sendData));
		} catch (HL7Exception ex) {
			result.setErr(-1, ex.getMessage());
			return result;
		}
		
		return result;
	}
}
