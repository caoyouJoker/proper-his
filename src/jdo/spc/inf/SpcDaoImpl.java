package jdo.spc.inf;

import java.util.List;

import org.apache.poi.hssf.record.PageBreakRecord.Break;
import org.omg.CORBA.OBJ_ADAPTER;

import jdo.spc.SPCSQL;
import jdo.pha.inf.client.SpcOpdDiagrecDto;
import jdo.pha.inf.client.SpcOpdDrugAllergyDto;
import jdo.pha.inf.client.SpcOpdOrderDto;
import jdo.pha.inf.client.SpcOpdOrderDtos;
import jdo.pha.inf.client.SpcRegPatadmDto;
import jdo.pha.inf.client.SpcSysPatinfoDto;
import jdo.spc.inf.dto.SpcOpdOrderReturnDto;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.ui.spc.util.StringUtils;


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
public class SpcDaoImpl extends TJDOTool {
	/**
	 * 实例
	 */
	public static SpcDaoImpl instanceObject;

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
	public static SpcDaoImpl getInstance() {
		if (instanceObject == null)
			instanceObject = new SpcDaoImpl();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SpcDaoImpl() {
		onInit();
	}

	/**
	 * 门急诊药房审核（HIS执行门急诊药房审核后、将处方信息传送至物联网）
	 * 
	 * @param list
	 * @return
	 */
	public String onSavePhaOrder(SpcOpdOrderDtos dtos) {
		String result = RESULT_FLAG_TRUE;
		if (null != dtos) {
			// 得到OpdOrder门急诊List
		//保存门急诊医嘱明细档
		List<SpcOpdOrderDto> opdOrderDto = dtos.getSpcOpdOrderDtos();
		String isBillFlg = "0";
		if(null != opdOrderDto && opdOrderDto.size()>0){//如果为收费 不保存
			for(SpcOpdOrderDto obj : opdOrderDto){
				String billFlg = obj.getBillFlg();
				String releaseFlg = obj.getReleaseFlg();
//				System.out.println("------billFlg:"+billFlg+",releaseFlg:"+releaseFlg);
				if("N".equals(billFlg) && "N".equals(releaseFlg)){//如果是未收费，且不是自备药
					isBillFlg = "-1";
					break;
				}
			}
/*			String billFlg = opdOrderDto.get(0).getBillFlg();
//			System.out.println("------billFlg:"+billFlg);
			if(StringUtils.equals("N", billFlg)){
				return RESULT_FLAG_FALSE;
			}*/
		}
//		System.out.println("-----------isBillFlg: "+isBillFlg);
		if("-1".equals(isBillFlg)){
			return RESULT_FLAG_FALSE;
		}
		boolean flg = saveOpdOrder(dtos.getSpcOpdOrderDtos());
		//保存病患基本信息
		 flg = saveSysPationInfo(dtos.getSpcSysPatinfoDtos());
		 //保存-门急诊诊断档
		 flg = saveOpdDiagrec(dtos.getSpcOpdDiagrecDtos());
		 //保存-病患过敏记录
		 flg = saveOpdDrugallergy(dtos.getSpcOpdDrugAllergyDtos());
		 //保存-挂号主档
		 flg = saveRegPatadm(dtos.getSpcRegPatadmDtos());
		} else {
			result = RESULT_FLAG_FALSE;
		}
		return result;
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
//			System.out.println("SpcDaoImpl-125-------------sql:" + SPCSQL.deleteOPDOrder(spcOpdOrderDtosList.get(0)));
			parm =  new TParm(TJDODBTool.getInstance().update(SPCSQL.deleteOPDOrder(spcOpdOrderDtosList.get(0))));
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
//				System.out.println("SpcDaoImpl-92-------------sql:" + SPCSQL.saveOpdOrderFromHisBySpc(obj));
				parm = new TParm(TJDODBTool.getInstance().update(SPCSQL.saveOpdOrderFromHisBySpc(obj)));
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
				String sql = SPCSQL.deleteSysPationInfo(obj);
//				System.out.println("病患基本信息-----deleteSysPationInfo.sql:"+sql);
				//先删
				TParm delParm = new TParm(TJDODBTool.getInstance().update(sql));
				if(delParm.getErrCode()<0){
					return false;
				}
				sql = SPCSQL.saveSysPationInfo(obj);
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
				String sql = SPCSQL.deleteOpdDrugallergyById(obj);
//				System.out.println("病患过敏记录-----deleteOpdDrugallergyById:"+sql);
				//先删
				TParm delParm = new TParm(TJDODBTool.getInstance().update(sql));
				if(delParm.getErrCode()<0){
					return false;
				}
				sql = SPCSQL.saveOpdDrugallergy(obj);
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
				String sql = SPCSQL.deleteOpdDiagrec(obj);
//				System.out.println("门急诊诊断档------deleteOpdDiagrec:"+sql);
				//先删
				TParm delParm = new TParm(TJDODBTool.getInstance().update(sql));
				if(delParm.getErrCode()<0){
					return false;
				}
				sql = SPCSQL.saveOpdDiagrec(obj);
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
				String sql = SPCSQL.deleteRegPatadm(obj);
//				System.out.println("挂号主档-----deleteRegPatadm:"+sql);
				//先删
				TParm delParm = new TParm(TJDODBTool.getInstance().update(sql));
				if(delParm.getErrCode()<0){
					return false;
				}
				sql = SPCSQL.saveRegPatadm(obj);
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
//		System.out.println("---------115--sql:"+SPCSQL.getSpcOrderCodeByHisOrderCode("", "", obj.getOrderCode()));
		TParm parm = new TParm(TJDODBTool.getInstance().select(SPCSQL.getSpcOrderCodeByHisOrderCode("", "", obj.getOrderCode())));
		if(null != parm && parm.getCount() > 0){
			vo.setOrderCode(parm.getValue("ORDER_CODE", 0));
		}
		return vo;
	}
/*	*//**
	 * 门急诊药房发药(于HIS门急诊发药界面点中处方时、相应药筐需闪烁提示)
	 * 
	 * @param SpcCommonDto
	 *            数据传输对象
	 * @return
	 *//*
	public String phaSend(SpcCommonDtos dtos) {
		// 1 成功；0失败
		String result = RESULT_FLAG_TRUE;
		if (null != dtos) {
			// 得到OpdOrder门急诊List
			List<SpcCommonDto> spcCommonDtoList = dtos.getSpcCommonDtos();
			if (null != spcCommonDtoList && spcCommonDtoList.size() > 0) {
				TParm parm = new TParm();
				// 电子标签登陆
				// if (Constant.parameters == null || Constant.parameters.get(0)
				// != null || Constant.parameters.get(1) != null
				// || Constant.parameters.get(2) != null) {// 如果Token不为空空
				// 重新登陆
				// ElectronicTagUtil.login();
				// }
				for (SpcCommonDto obj : spcCommonDtoList) {
					// 处方号
					String rxNo = obj.getRxNo();
					// 病案号
					String mrNo = obj.getMrNo();
					// 电子标签
					String basketId = "";
					// 病患性别
					String sexStr = "";
					// 病患年龄
					String ageStr = "";
					// 病患名称
					String patName = "";

					if (null != rxNo && rxNo.length() > 0) {
						// 查询电子标签显示
//						System.out.println("phaSend-- 111--------------sql: " + PhaSQL.getBasketInfo(rxNo));
						TParm basketParm = new TParm(TJDODBTool.getInstance().select(PhaSQL.getBasketInfo(rxNo)));
						if (null != basketParm) {
							basketId = basketParm.getValue("BASKET_ID", 0);
							ageStr = basketParm.getValue("AGE", 0);
							patName = basketParm.getValue("PAT_NAME", 0);
							sexStr = basketParm.getValue("SEX_TYPE", 0);
							// 更新电子标签
							// ElectronicTagUtil.getInstance().sendEleTag(basketId,
							// patName + " " + ageStr, mrNo, sexStr, LIGHT_NUM);
						} else {
							result = RESULT_FLAG_FALSE;
						}
					}
				}
			}
		}
		return result;
	}
*/

	/**
	 * HIS退药查询医嘱状态
	 * 
	 * @param mrNo
	 *            病案号
	 * @param rxNo
	 *            处方号
	 * @param orderCode
	 *            药品编码            
	 * @return
	 */
	public SpcOpdOrderReturnDto getPhaStateReturn(String rxNo,String caseNo, String seqNo) {
		SpcOpdOrderReturnDto result = null;
		// TODO Auto-generated method stub
		String sql =SPCSQL.getPhaStateReturn(rxNo,caseNo,seqNo);
//		System.out.println("---------getPhaStateReturn:"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (null != parm && parm.getCount() > 0) {
			result = new SpcOpdOrderReturnDto(parm.getValue("PHA_CHECK_DATE", 0), parm.getValue("PHA_CHECK_CODE", 0), parm.getValue("PHA_DOSAGE_DATE", 0),
					parm.getValue("PHA_DOSAGE_CODE", 0), parm.getValue("PHA_DISPENSE_DATE", 0), parm.getValue("PHA_DISPENSE_CODE", 0)
					, parm.getValue("PHA_RETN_DATE", 0), parm.getValue("PHA_RETN_CODE", 0));
		}
//		System.out.println("--------getPhaStateReturn: "+parm);
		return result;
	}

}
