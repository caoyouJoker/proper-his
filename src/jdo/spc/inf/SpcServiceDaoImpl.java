package jdo.spc.inf;

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


/**
 * <p>
 * Title: 物联网 werbservice接口-DAO实现类
 * </p>
 * 
 * <p>
 * Description: 物联网 werbservice接口DAO实现类
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
public class SpcServiceDaoImpl extends TJDOTool {
	/**
	 * 实例
	 */
	public static SpcServiceDaoImpl instanceObject;

	// 标签闪烁次数30
	private static final Integer LIGHT_NUM = 30;

	// 标签闪烁次数0
	private static final Integer LIGHT_NUM_SEND = 0;

	// 返回结果-正确
	private static final String RESULT_FLAG_TRUE = "1";

	// 返回结果-错误
	private static final String RESULT_FLAG_FALSE = "0";

	/**
	 * 得到实例
	 * 
	 * @return INDTool
	 */
	public static SpcServiceDaoImpl getInstance() {
		if (instanceObject == null)
			instanceObject = new SpcServiceDaoImpl();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SpcServiceDaoImpl() {
		onInit();
	}

	
	/**
	 * 保存-门急诊医嘱明细档
	 * @param obj
	 * @return
	 */
	private static boolean saveOpdOrder(List<SpcOpdOrderDto> spcOpdOrderDtosList){
		if (null != spcOpdOrderDtosList && spcOpdOrderDtosList.size() > 0) {
			TParm parm = new TParm();
			//根据 case_no,rx_no 删除病患医嘱信息
//			System.out.println("SpcDaoImpl-125-------------sql:" + SpcServiceSQL.deleteOPDOrder(spcOpdOrderDtosList.get(0)));
			parm =  new TParm(TJDODBTool.getInstance().update(SpcServiceSQL.deleteOPDOrder(spcOpdOrderDtosList.get(0))));
			for (SpcOpdOrderDto obj : spcOpdOrderDtosList) {
				obj = getSpcOrderCodeByHisOrderCode(obj);
				obj.setPhaCheckCode("");
				obj.setPhaCheckDate("");
				obj.setPhaDispenseCode("");
				obj.setPhaDispenseDate("");
				obj.setPhaDosageCode("");
				obj.setPhaDosageDate("");
				//obj.setExecDeptCode("040102");
//				System.out.println("-----rx_no:"+obj.getRxNo()+",atc_flg:"+obj.getAtcFlg()+",region_code:"+obj.getRegionCode());
//				System.out.println("SpcDaoImpl-92-------------sql:" + SpcServiceSQL.saveOpdOrderFromHisBySpc(obj));
				parm = new TParm(TJDODBTool.getInstance().update(SpcServiceSQL.saveOpdOrderFromHisBySpc(obj)));
//				System.out.println("--------parm: "+parm);
				if (parm.getErrCode() < 0) {
					return false;
				}
			}
		} 
		return true;
	}
	
	/**
	 * 保存病患基本信息-SYS_PATIONINFO
	 * @return
	 */
	private static boolean saveSysPationInfo(List<SpcSysPatinfoDto> list){
		if (null != list && list.size() > 0) {
			for(SpcSysPatinfoDto obj : list){
				String sql = SpcServiceSQL.deleteSysPationInfo(obj);
//				System.out.println("病患基本信息-----deleteSysPationInfo.sql:"+sql);
				//先删
				TParm delParm = new TParm(TJDODBTool.getInstance().update(sql));
				if(delParm.getErrCode()<0){
					return false;
				}
				sql = SpcServiceSQL.saveSysPationInfo(obj);
//				System.out.println("病患基本信息-----saveSysPationInfo:"+sql);
				//后保存
				TParm saveParm = new TParm(TJDODBTool.getInstance().update(sql));
				if (saveParm.getErrCode()<0) {
					return false;
				}
			}
			return true;
		}
		return true;
	}
	
	/**
	 * 保存-病患过敏记录-OPD_DRUGALLERGY 
	 * @return
	 */
	private static boolean saveOpdDrugallergy(List<SpcOpdDrugAllergyDto> list){
		if (null != list && list.size() > 0) {
			for(SpcOpdDrugAllergyDto obj : list){
				String sql = SpcServiceSQL.deleteOpdDrugallergyById(obj);
//				System.out.println("病患过敏记录-----deleteOpdDrugallergyById:"+sql);
				//先删
				TParm delParm = new TParm(TJDODBTool.getInstance().update(sql));
				if(delParm.getErrCode()<0){
					return false;
				}
				sql = SpcServiceSQL.saveOpdDrugallergy(obj);
//				System.out.println("病患过敏记录-----saveOpdDrugallergy:"+sql);
				//后保存
				TParm saveParm = new TParm(TJDODBTool.getInstance().update(sql));
				if (saveParm.getErrCode()<0) {
					return false;
				}
			}
			return true;
		}		
		return true;
	}

	
	/**
	 * 保存-门急诊诊断档-OPD_DIAGREC
	 * @return
	 */
	@SuppressWarnings("unused")
	private static boolean saveOpdDiagrec(List<SpcOpdDiagrecDto> list){
		if (null != list && list.size() > 0) {
			for(SpcOpdDiagrecDto obj : list){
				String sql = SpcServiceSQL.deleteOpdDiagrec(obj);
//				System.out.println("门急诊诊断档------deleteOpdDiagrec:"+sql);
				//先删
				TParm delParm = new TParm(TJDODBTool.getInstance().update(sql));
				if(delParm.getErrCode()<0){
					return false;
				}
				sql = SpcServiceSQL.saveOpdDiagrec(obj);
//				System.out.println("门急诊诊断档------saveOpdDiagrec:"+sql);
				//后保存
				TParm saveParm = new TParm(TJDODBTool.getInstance().update(sql));
				if (saveParm.getErrCode()<0) {
					return false;
				}
			}
			return true;
		}		
		return true;
	}

	
	/**
	 * 保存-挂号主档-REG_PATADM
	 * @return
	 */
	private static boolean saveRegPatadm(List<SpcRegPatadmDto> list){
		if (null != list && list.size() > 0) {
			for(SpcRegPatadmDto obj : list){
				String sql = SpcServiceSQL.deleteRegPatadm(obj);
//				System.out.println("挂号主档-----deleteRegPatadm:"+sql);
				//先删
				TParm delParm = new TParm(TJDODBTool.getInstance().update(sql));
				if(delParm.getErrCode()<0){
					return false;
				}
				sql = SpcServiceSQL.saveRegPatadm(obj);
//				System.out.println("挂号主档-----saveRegPatadm:"+sql);
				//后保存
				TParm saveParm = new TParm(TJDODBTool.getInstance().update(sql));
				if (saveParm.getErrCode()<0) {
					return false;
				}
			}
			return true;
		}		
		return true;
	}


	/**
	 * 转换药品编码
	 * @param obj
	 * @return
	 */
	private static SpcOpdOrderDto getSpcOrderCodeByHisOrderCode(SpcOpdOrderDto obj){
		SpcOpdOrderDto vo = obj;
//		System.out.println(vo.getOrderCode()+"----116---orderCode:"+obj.getOrderCode());
//		System.out.println("---------115--sql:"+SpcServiceSQL.getSpcOrderCodeByHisOrderCode("", "", obj.getOrderCode()));
		TParm parm = new TParm(TJDODBTool.getInstance().select(SpcServiceSQL.getSpcOrderCodeByHisOrderCode("", "", obj.getOrderCode())));
		if(null != parm && parm.getCount() > 0){
			vo.setOrderCode(parm.getValue("ORDER_CODE", 0));
		}
		return vo;
	}


	/**
	 * webServices服务端-保存HIS传送过来的医嘱医院
	 * @param list
	 * @return
	 */
	public String onSavePhaOrderFromHis(SpcOpdOrderDtos dtos) {
		String result = RESULT_FLAG_TRUE;
		boolean flg = saveOpdOrder(dtos.getSpcOpdOrderDtos());
		//保存病患基本信息
		 flg = saveSysPationInfo(dtos.getSpcSysPatinfoDtos());
		 //保存-门急诊诊断档
		 flg = saveOpdDiagrec(dtos.getSpcOpdDiagrecDtos());
		 //保存-病患过敏记录
		 flg = saveOpdDrugallergy(dtos.getSpcOpdDrugAllergyDtos());
		 //保存-挂号主档
		 flg = saveRegPatadm(dtos.getSpcRegPatadmDtos());
		return result;
	}	
}
