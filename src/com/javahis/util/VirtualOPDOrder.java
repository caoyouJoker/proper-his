package com.javahis.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.dongyang.data.TParm;

import jdo.hl7.Hl7Communications;

/**
 * <p>
 * Title: 虚拟医嘱API
 * </p>
 * 
 * <p>
 * Description: 虚拟医嘱API
 * </p>
 * 
 * <p>
 * Copyright: Copyright JavaHis (c) 2009年1月
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
	 * 以捡伤号发出虚拟检验医嘱(因为急诊捡伤时还没有挂号相关信息)
	 * 
	 * @param triageNo
	 *            捡伤号
	 * @param orderCode
	 *            医技医嘱代码
	 * @param patName
	 *            病患名称
	 * @param optUser
	 *            操作者代码
	 * @throws Exception
	 */
	public void insertVirtualOPDOrder(String triageNo, String orderCode, String patName, String optUser)
			throws Exception {
		// insert OPD_ORDER,新增一笔虚拟医嘱记录,以捡伤号对应
		// insert MED_APPLY,新增一笔医检申请记录,以捡伤号对应
		// 发送HL7消息给心电仪器,HIS发送给ECG新开医嘱信息(已经有对应API)

	}

	/**
	 * 更新虚拟检验医嘱成为正式医嘱(急诊医生站合并到正式医嘱)
	 * 
	 * @param triageNo
	 *            捡伤号
	 * @param orderCode
	 *            医技医嘱代码
	 * @param mrNo
	 *            病案号
	 * @param caseNo
	 *            就诊号
	 * @param optUser
	 *            操作者代码
	 * @throws Exception
	 */
	public void updateVirtualOPDOrder(String triageNo, String orderCode, String mrNo, String caseNo, String optUser)
			throws Exception {
		// 由mrNo读取病患基本资讯,由caseNo读取挂号资讯
		// update OPD_ORDER,更新虚拟医嘱记录,以捡伤号triageNo找出虚拟医嘱,更新正式为医嘱信息
		// sendHL7Mes() : 发送HL7消息给心电仪器,HIS发送给ECG修正医嘱信息?(似乎还没有API,flg 要加状态?
		// 更新后报告回传路径是否一起异动?)
		// MED_APPLY是否需要更新?或是发送给ECG修正医嘱信息后,会自动更新
	}

	/**
	 * 发送HL7消息
	 * 
	 * @param admType
	 *            门急住别
	 * @param catType
	 *            医令分类
	 * @param patName
	 *            病患姓名
	 * @param caseNo
	 *            就诊号
	 * @param orderNo
	 *            医令号(处方签号)
	 * @param seqNo
	 *            序号
	 * @param labNo
	 *            检验申请号
	 * @param flg
	 *            状态(0,发送1,取消)
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

		// 调用接口
		TParm resultParm = Hl7Communications.getInstance().Hl7Message(list);
		// System.out.println("resultParm::::"+resultParm);
		if (resultParm.getErrCode() < 0)
			throw new Exception(resultParm.getErrText());

	}

}
