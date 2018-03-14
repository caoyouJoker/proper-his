package com.javahis.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.dongyang.data.TParm;

import jdo.hl7.Hl7Communications;

/**
 * <p>
 * Title: ����ҽ��API
 * </p>
 * 
 * <p>
 * Description: ����ҽ��API
 * </p>
 * 
 * <p>
 * Copyright: Copyright JavaHis (c) 2009��1��
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author Kevin
 * @version JavaHis 1.0
 */
public class VirtualOPDOrder {

	/**
	 * �Լ��˺ŷ����������ҽ��(��Ϊ�������ʱ��û�йҺ������Ϣ)
	 * 
	 * @param triageNo
	 *            ���˺�
	 * @param orderCode
	 *            ҽ��ҽ������
	 * @param patName
	 *            ��������
	 * @param optUser
	 *            �����ߴ���
	 * @throws Exception
	 */
	public void insertVirtualOPDOrder(String triageNo, String orderCode, String patName, String optUser)
			throws Exception {
		// insert OPD_ORDER,����һ������ҽ����¼,�Լ��˺Ŷ�Ӧ
		// insert MED_APPLY,����һ��ҽ�������¼,�Լ��˺Ŷ�Ӧ
		// ����HL7��Ϣ���ĵ�����,HIS���͸�ECG�¿�ҽ����Ϣ(�Ѿ��ж�ӦAPI)

	}

	/**
	 * �����������ҽ����Ϊ��ʽҽ��(����ҽ��վ�ϲ�����ʽҽ��)
	 * 
	 * @param triageNo
	 *            ���˺�
	 * @param orderCode
	 *            ҽ��ҽ������
	 * @param mrNo
	 *            ������
	 * @param caseNo
	 *            �����
	 * @param optUser
	 *            �����ߴ���
	 * @throws Exception
	 */
	public void updateVirtualOPDOrder(String triageNo, String orderCode, String mrNo, String caseNo, String optUser)
			throws Exception {
		// ��mrNo��ȡ����������Ѷ,��caseNo��ȡ�Һ���Ѷ
		// update OPD_ORDER,��������ҽ����¼,�Լ��˺�triageNo�ҳ�����ҽ��,������ʽΪҽ����Ϣ
		// sendHL7Mes() : ����HL7��Ϣ���ĵ�����,HIS���͸�ECG����ҽ����Ϣ?(�ƺ���û��API,flg Ҫ��״̬?
		// ���º󱨸�ش�·���Ƿ�һ���춯?)
		// MED_APPLY�Ƿ���Ҫ����?���Ƿ��͸�ECG����ҽ����Ϣ��,���Զ�����
	}

	/**
	 * ����HL7��Ϣ
	 * 
	 * @param admType
	 *            �ż�ס��
	 * @param catType
	 *            ҽ�����
	 * @param patName
	 *            ��������
	 * @param caseNo
	 *            �����
	 * @param orderNo
	 *            ҽ���(����ǩ��)
	 * @param seqNo
	 *            ���
	 * @param labNo
	 *            ���������
	 * @param flg
	 *            ״̬(0,����1,ȡ��)
	 * @throws Exception
	 */
	public static void sendHL7Mes(String admType, String catType, String patName, String caseNo, String orderNo,
			String seqNo, String labNo, int flg) throws Exception {

		List list = new ArrayList();
		TParm parm = new TParm();
		parm.setData("PAT_NAME", patName);
		parm.setData("ADM_TYPE", admType);
		parm.setData("FLG", flg);
		parm.setData("CASE_NO", caseNo);
		parm.setData("LAB_NO", labNo);
		parm.setData("CAT1_TYPE", catType);
		parm.setData("ORDER_NO", orderNo);
		parm.setData("SEQ_NO", seqNo);
		list.add(parm);

		// ���ýӿ�
		TParm resultParm = Hl7Communications.getInstance().Hl7Message(list);
		// System.out.println("resultParm::::"+resultParm);
		if (resultParm.getErrCode() < 0)
			throw new Exception(resultParm.getErrText());

	}

}
