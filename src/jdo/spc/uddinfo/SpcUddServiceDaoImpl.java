package jdo.spc.uddinfo;

import java.util.ArrayList;
import java.util.List;


import jdo.pha.inf.dto.SpcOpdDiagrecDto;
import jdo.pha.inf.dto.SpcOpdDrugAllergyDto;
import jdo.pha.inf.dto.SpcOpdOrderDto;
import jdo.pha.inf.dto.SpcOpdOrderDtos;
import jdo.pha.inf.dto.SpcRegPatadmDto;
import jdo.pha.inf.dto.SpcSysPatinfoDto;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.ui.odi.ODISingleExeControl;


/**
 * <p>
 * Title: 物联网-护士单词执行- werbservice接口</p>
 * </p>
 * 
 * <p>
 * Description: 物联网-护士单词执行- werbservice接口</p>
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author Liyh
 * @version 1.0
 */
public class SpcUddServiceDaoImpl extends TJDOTool {
	/**
	 * 实例
	 */
	public static SpcUddServiceDaoImpl instanceObject;

 

	/**
	 * 得到实例
	 * 
	 * @return INDTool
	 */
	public static SpcUddServiceDaoImpl getInstance() {
		if (instanceObject == null)
			instanceObject = new SpcUddServiceDaoImpl();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SpcUddServiceDaoImpl() {
		onInit();
	}

	
	/**
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unused")
	public static List<OdiDspndPkVo> getOdiDspndInfo(String caseNo, String barCode) {
//		System.out.println(">>>护士单次执行----------getOdiDspndInfo(caseNo:"+caseNo+"---barcode:"+barCode+")");
		List<OdiDspndPkVo> list = new ArrayList<OdiDspndPkVo>();
		String sql = getOdiDspndInfoSql(caseNo, barCode);
//		System.out.println("--------sql:"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (null != parm && parm.getCount()>0) {
			int count = parm.getCount();
			for (int i = 0; i <count; i++) {
				OdiDspndPkVo vo = new OdiDspndPkVo();
				vo.setCaseNo(parm.getValue("CASE_NO", i));
				vo.setOrderNo(parm.getValue("ORDER_NO", i));
				vo.setOrderSeq(parm.getValue("ORDER_SEQ", i));
				vo.setStartDttm(parm.getValue("START_DTTM", i));
				vo.setEndDttm(parm.getValue("END_DTTM", i));
				vo.setBarCode1(parm.getValue("TOXIC_ID1", i));
				vo.setBarCode2(parm.getValue("TOXIC_ID2", i));
				vo.setBarCode3(parm.getValue("TOXIC_ID3", i));
				vo.setOrderCode(parm.getValue("ORDER_CODE", i));
				list.add(vo);
			}
		}
		for(OdiDspndPkVo vo :list){
			System.out.println("getCaseNo:"+vo.getCaseNo()+",getOrderNo:"+vo.getOrderNo()+",getOrderSeq："+vo.getOrderSeq()+"getOrderDate："
					+vo.getOrderDate()+"，getOrderDateTime："+vo.getOrderDateTime()+",ordercode:"+vo.getOrderCode()+",barcode1:"
					+vo.getBarCode1()+"--"+vo.getBarCode2()+"--"+vo.getBarCode3());
		}
		return list;
	}
	
	/**
	 * 根据caseno,麻精编码查询 odi主键
	 * @param caseNo
	 * @param barCode
	 * @return
	 */
	private static String  getOdiDspndInfoSql(String caseNo,String barCode){
		String sql= " SELECT CASE_NO,ORDER_NO,ORDER_SEQ,START_DTTM,END_DTTM,ORDER_CODE,TOXIC_ID1,TOXIC_ID2,TOXIC_ID3  " +                                                                                                  
					" FROM IND_CABDSPN " +
					" WHERE CASE_NO='" +caseNo + "' AND (TOXIC_ID1='" + barCode + "' OR TOXIC_ID2='" + barCode + "' OR TOXIC_ID3='" + barCode + "') ";          
		return sql;
	}
	
}
