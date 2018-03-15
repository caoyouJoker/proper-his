package jdo.ins;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOObject;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.taglibs.standard.tag.el.sql.SetDataSourceTag;

import jdo.sys.SystemTool;

/**
 * <p>
 * Title: 挂号医保管理
 * </p>
 * 
 * <p>
 * Description: 挂号医保管理
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author pangben 2011-11-07
 * @version 1.0
 */
public class INSTJReg extends TJDODBTool {
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	DateFormat df1 = new SimpleDateFormat("yyyyMMdd");
	public INSTJReg() {
	}

	/**
	 * 实例
	 */
	public static INSTJReg instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return
	 */
	public static INSTJReg getInstance() {
		if (instanceObject == null)
			instanceObject = new INSTJReg();
		return instanceObject;
	}

	/**
	 * 城职普通
	 * 
	 * @param parm
	 *            TParm
	 */
	public Map cityStaffCommon(Map mapParm) {
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		TParm result = new TParm();
		// 判断是否存在在途数据
//		if (!INSTJFlow.getInstance().queryRun(parm)) {
//			result.setErr(-1, "有在途数据不可以执行");
//			// parm.setData("EXE_TYPE", "REG");
//			// if (!INSTJFlow.getInstance().deleteInsRun(parm)) {
//			// result.setErr(-1, "删除在途记录信息操作有问题");
//			// return result;
//			// }
//			return result.getData();
//		}
		//System.out.println("--------城职普通-------------------" + parm);

		// 执行共用的费用分割、添加ins_opd_order 表、明细上传操作
		// 执行费用分割 函数：DataDown_sp1 方法 B
		// 执行上传明细 函数: 函数DataUpload,（B）方法
		//TParm comminuteFeeParm = parm.getParm("comminuteFeeParm");// 费用分割 所有医嘱
		
		////System.out.println("费用分割:::" + comminuteFeeParm);
		// 费用结算-------获得出参 执行医嘱金额分割操作
		// 函数DataDown_sp, 门诊结算上传交易（M）方法
		//TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// 费用结算出参
		// 门诊添加特殊情况
		if (!INSTJFlow.getInstance().insSpcUpload(parm)) {
			err(result.getErrText());
			return result.getData();
		}
		// 结算确认 函数DataDown_sp，门诊结算确认交易（R）方法 成功修改INS_OPD 表INSAMT_FLG对账标志3
		result = INSTJFlow.getInstance()
				.insSettleConfirmChZPt(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrText());
			return result.getData();
		}
		return new TParm().getData();
	}
	
	/**
	 * 出现问题现金支付
	 * 
	 * @return
	 */
	public Map resultParm(TParm parm, String errName, TConnection connection) {
//		if (null != parm && null != parm.getValue("MESSAGE")
//				&& parm.getValue("MESSAGE").length() > 0) {
//			TParm returnParm = new TParm();
//			returnParm.setData("MESSAGE", parm.getValue("MESSAGE"));
//			if (null != parm.getValue("FLG")
//					&& parm.getValue("FLG").equals("Y")) {
//				returnParm.setData("FLG", "Y");
//			}
//			return returnParm.getData();
//		}
		TParm result = new TParm();
		result.setErr(-1, errName);
		connection.close();
		return result.getData();
	}

	/**
	 * 城职门特
	 * 
	 * @param parm
	 *            TParm
	 */
	public Map cityStaffClement(Map mapParm) {
		// TConnection connection = getConnection();
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		TParm result = new TParm();
		// 判断是否存在在途数据
//		if (!INSTJFlow.getInstance().queryRun(parm)) {
//			result.setErr(-1, "有在途数据不可以执行");
//			// parm.setData("EXE_TYPE", "REG");
//			// if (!INSTJFlow.getInstance().deleteInsRun(parm)) {
//			// result.setErr(-1, "删除在途记录信息操作有问题");
//			// return result;
//			// }
//			return result.getData();
//		}
		//System.out.println("----------城职门特--------------------" + parm);
		
		// 执行共用的费用分割、添加ins_opd_order 表、明细上传操作
		// 执行费用分割 函数：DataDown_sp1 方法 G
		// 执行上传明细 函数: 函数DataUpload,（C）方法
		//TParm comminuteFeeParm = parm.getParm("comminuteFeeParm");// 费用分割 所有医嘱
		// 执行共用的费用分割、添加ins_opd_order 表、明细上传操作
		////System.out.println("费用分割:::" + comminuteFeeParm);

		// 特殊信息上传:挂号不会执行特殊信息上传---门诊收费添加
		if (!INSTJFlow.getInstance().insSpcUpload(parm)) {
			result.setErr(-1, "城职门特,特殊信息上传错误");
			err(result.getErrText());
			return result.getData();
		}
		// 费用结算 函数DataDown_mts, 门诊结算上传交易（F）方法
		//TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// 费用结算出参
//		// 城职门特返回参数：统筹支付确认交易：函数DataDown_mts, 门特统筹支付确认交易（G）
//		result = INSTJFlow.getInstance().insPayAccountChZMt(parm);
//		if (result.getErrCode() < 0) {
//			err(result.getErrText());
//			return result.getData();
//		}
		// 结算确认:城职门特 函数DataDown_mts，门诊结算确认交易（I）方法
		result = INSTJFlow.getInstance()
				.insSettleConfirmChZMt(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrText());
			return result.getData();
		}
		return new TParm().getData();
	}

	/**
	 * 执行医保操作之后返回参数
	 * 
	 * @return
	 */
	private Map returnParm(TParm parm, TParm comminuteFeeParm) {
		TParm result = new TParm();
		result.setData("INS_PARM", parm.getData());// 费用结算
		result.setData("comminuteFeeParm", comminuteFeeParm.getData());// 费用分割返回参数用来添加收据分割信息
		return result.getData();
	}

	/**
	 * 城居门特
	 * 
	 * @param parm
	 *            TParm
	 */
	public Map specialCityDenizen(Map mapParm) {
		// TConnection connection = getConnection();
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		TParm result = new TParm();
		// 判断是否存在在途数据
//		if (!INSTJFlow.getInstance().queryRun(parm)) {
//			result.setErr(-1, "有在途数据不可以执行");
//			// parm.setData("EXE_TYPE", "REG");
//			// if (!INSTJFlow.getInstance().deleteInsRun(parm)) {
//			// result.setErr(-1, "删除在途记录信息操作有问题");
//			// return result;
//			// }
//			return result.getData();
//		}
		
		// 执行共用的费用分割、添加ins_opd_order 表、明细上传操作
		// 执行费用分割 函数：DataDown_sp1 方法 G
		// 执行上传明细 函数: 函数DataUpload,（F）方法
	//	TParm comminuteFeeParm = parm.getParm("comminuteFeeParm");// 费用分割 所有医嘱
		// 执行共用的费用分割、添加ins_opd_order 表、明细上传操作
	//	//System.out.println("费用分割:::" + comminuteFeeParm);
		// 费用结算 函数DataDown_cmts, 门诊结算上传交易（F）方法
		//TParm settlementDetailsParm = parm.getParm("settlementDetailsParm");// 费用结算出参
		// 门诊添加特殊情况
		// 特殊信息上传:挂号不会执行特殊信息上传---门诊收费添加
		if (!INSTJFlow.getInstance().insSpcUpload(parm)) {
			result.setErr(-1, "城居门特,特殊信息上传错误");
			err(result.getErrText());
			return result.getData();
		}
		// 结算确认:城居门特 函数DataDown_cmts，门诊结算确认交易（I）方法
		result = INSTJFlow.getInstance()
				.insSettleConfirmChJMt(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrText());
			return result.getData();
		}
		return new TParm().getData();

	}

	/**
	 * 刷卡操作:执行 刷卡 调用险种识别交易操作
	 * 
	 * @param parm
	 *            界面参数
	 * @return
	 */
	public TParm insConfirmApply(TParm parm) {
		TParm readParm = INSTJTool.getInstance().DataDown_sp_U(
				parm.getValue("TEXT"));// U方法
		if (!INSTJTool.getInstance().getErrParm(readParm)) {
			readParm.setErr(-1, "刷卡操作有问题 ");
			return readParm;
		}
		// returnParm.setData("readParm", readParm.getData());
		parm.setData("CARD_NO",readParm.getValue("CARD_NO"));//医保卡号
		TParm result = INSTJTool.getInstance().DataDown_czys_A(parm);// A方法
		if (!INSTJTool.getInstance().getErrParm(result)) {
			result.setErr(-1, "刷卡操作有问题");
			return result;
		}
		// returnParm.setData("result", result.getData());
		return readParm;
	}

	/**
	 * 城职普通 退费操作
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public Map insUNCityStaffCommonChZPt(Map mapParm) {
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		//System.out.println("城职普通parm::::::"+parm);
		// 查询要退费记录信息
		TParm result = new TParm();
		if (!INSTJFlow.getInstance().queryRun(parm)) {
			result.setErr(-1, "城职普通,有在途数据不可以执行");
			return result.getData();
			// parm.setData("EXE_TYPE", "REGT");
			// if (!INSTJFlow.getInstance().deleteInsRun(parm)) {
			// result.setErr(-1, "删除在途记录信息操作有问题");
			// return result;
			// }
		}
		System.out.println("--------------城职普通 退费操作----------------");
		TConnection connection = getConnection();
		// 退费明细生成本地对账标志置0
		TParm insOPDParm = INSTJFlow.getInstance().resetInsetOpdandOpdOrder(
				parm, connection);
		if (insOPDParm.getErrCode() < 0) {
			return resultParm(result, insOPDParm.getErrText(), connection);
		}
		// 门诊退费
		TParm resetInsOPDParm = INSTJFlow.getInstance().resetFeeChZPt(parm,
				insOPDParm);
		
		//System.out.println("------------------------城职普通 退费操作:::::::"+resetInsOPDParm);
		if (resetInsOPDParm.getErrCode()<0) {
			// 退费取消操作
			if (!INSTJFlow.getInstance().resetConcelFee(parm,
					parm.getInt("INS_TYPE"))) {
				return resultParm(result, "城职普通,退费取消操作失败", connection);
			}
			return resultParm(result, resetInsOPDParm.getErrText(), connection);
		}
		//System.out.println("resetInsOPDParm::::::"+resetInsOPDParm);
		int flg = resetInsOPDParm.getInt("INSAMT_FLG");
		//System.out.println("flg::::::::::::::::::::::" + flg);
		if (flg <= 0) {
			// 退费取消操作
			if (!INSTJFlow.getInstance().resetConcelFee(parm,
					parm.getInt("INS_TYPE"))) {
				return resultParm(result, "城职普通,退费取消操作失败", connection);
			}
			return resultParm(result, "城职普通,门诊退费操作有问题", connection);
		}
		if (flg == 1) {
			// 修改本地对账标志为1
			//System.out.println("修改本地对账标志为1parm::::::"+parm);
			if (!INSTJFlow.getInstance().resetUpdateInsOpd(parm, "1",
					parm.getValue("UNRECP_TYPE"), parm.getInt("INS_TYPE"),
					connection)) {
				return resultParm(result, "城职普通,退费取消操作失败", connection);
			}
		}
		insOPDParm.setData("NHI_REGION_CODE",parm.getValue("REGION_CODE"));
		// 退费确认交易
		result = INSTJFlow.getInstance().resetFeeInsSettleChZPt(insOPDParm,
				resetInsOPDParm);
		if (result.getErrCode()<0) {
			return resultParm(result, "城职普通,退费取消操作失败", connection);
		}
		//System.out.println("城职普通,退费操作result::::"+result);
		flg = result.getInt("INS_FLG");
		if (flg <= 0) {
			return resultParm(result, "城职普通,退费取消操作失败", connection);
		}
		if (flg == 3) {
			// 修改本地对账标志为3
			//System.out.println("修改本地对账标志为parm::::::"+parm);
			if (!INSTJFlow.getInstance().resetUpdateInsOpd(parm, "3",
					parm.getValue("UNRECP_TYPE"), parm.getInt("INS_TYPE"),
					connection)) {
				return resultParm(result, "城职普通,退费取消操作失败", connection);
			}
		}
		// 在途状态添加
		parm.setData("EXE_TYPE", parm.getValue("UNRECP_TYPE"));
		if (!INSTJFlow.getInstance().insertRun(parm, "DataDown_yb_D",connection)) {
			return resultParm(result, "城职普通,在途状态添加数据有问题", connection);
		}
		//System.out.println("完成MMMMMMMMMMMMMMMMMMMMMMMM"+result);
		connection.commit();
		connection.close();
		return result.getData();

	}
	/**
	 * 退费使用
	 * 医保获得数据
	 * 
	 * @param parm
	 * @param insOPDParm
	 */
	private boolean resetGetInsParm(TParm parm,TParm insOPDParm){
		//查询医保病患信息
		TParm mzParm= INSMZConfirmTool.getInstance().queryMZConfirmOne(parm);
		if (mzParm.getErrCode()<0) {
			return false;
		}
		insOPDParm.setData("DISEASE_CODE",mzParm.getValue("DISEASE_CODE",0));//门特病种
		insOPDParm.setData("PAY_KIND",mzParm.getValue("PAY_KIND",0));//支付类别
		return true;
	}
	/**
	 * 城职门特 退费操作
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public Map insUNCityStaffClementChZMt(Map mapParm) {
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		// 查询要退费记录信息
		TParm result = new TParm();
		if (!INSTJFlow.getInstance().queryRun(parm)) {
			result.setErr(-1, "城职门特,有在途数据不可以执行");
			return result.getData();
			// parm.setData("EXE_TYPE", "REGT");
			// if (!INSTJFlow.getInstance().deleteInsRun(parm)) {
			// result.setErr(-1, "删除在途记录信息操作有问题");
			// return result;
			// }
		}
		TConnection connection = getConnection();
		// 退费明细生成本地对账标志置0
		TParm insOPDParm = INSTJFlow.getInstance().resetInsetOpdandOpdOrder(
				parm, connection);
		if (insOPDParm.getErrCode() < 0) {
			return resultParm(result, insOPDParm.getErrText(), connection);
		}
		if (!resetGetInsParm(parm, insOPDParm)) {
			connection.close();
			result.setErr(-1, "城职门特,医保数据查询有问题");
			return result.getData();
		}
		
		// 门诊退费
		TParm resetInsOPDParm = INSTJFlow.getInstance().resetFeeChZMt(parm,
				insOPDParm);
		if (resetInsOPDParm.getErrCode()<0) {
			return resultParm(result, "城职门特,退费取消操作失败", connection);
		}
		int flg = resetInsOPDParm.getInt("SOCIAL_FLG");
		//System.out.println("FLG:::::::::::::::::::::::::::::" + flg);
		if (flg <= 0) {
			// 退费取消操作
			if (!INSTJFlow.getInstance().resetConcelFee(parm,
					parm.getInt("INS_TYPE"))) {
				return resultParm(result, "城职门特,退费取消操作失败", connection);
			}
			return resultParm(result, "城职门特,门诊退费操作有问题", connection);
		}
		if (flg == 1) {
			// 修改本地对账标志为1
			//System.out.println("insOPDParm:::::::" + insOPDParm);
			if (!INSTJFlow.getInstance().resetUpdateInsOpd(insOPDParm, "1",
					parm.getValue("UNRECP_TYPE"), parm.getInt("INS_TYPE"),
					connection)) {
				return resultParm(result, "城职门特，退费取消操作失败", connection);
			}
		}
		insOPDParm.setData("NHI_REGION_CODE",parm.getValue("REGION_CODE"));
		// 退费确认交易
		result = INSTJFlow.getInstance().resetFeeInsSettleChZMt(insOPDParm);
		if (result.getErrCode()<0) {
			return resultParm(result, "城职门特，退费取消操作失败", connection);
		}
		flg = result.getInt("SOCIAL_FLG");
		if (flg <= 0) {
			return resultParm(result, "城职门特，退费取消操作失败", connection);
		}
		if (flg == 3) {
			// 修改本地对账标志为3
			if (!INSTJFlow.getInstance().resetUpdateInsOpd(insOPDParm, "3",
					parm.getValue("UNRECP_TYPE"), parm.getInt("INS_TYPE"),
					connection)) {
				return resultParm(result, "城职门特，退费取消操作失败", connection);
			}
		}
		// 在途状态添加
		parm.setData("EXE_TYPE", parm.getValue("UNRECP_TYPE"));
		if (!INSTJFlow.getInstance().insertRun(parm, "DataDown_mts_L",connection)) {
			return resultParm(result, "城职门特,在途状态添加数据有问题", connection);
		}
		connection.commit();
		connection.close();
		return result.getData();// 返回值需要修改
	}

	/**
	 * 城居门特 退费操作
	 * 
	 * @param parm
	 * @param connection
	 */
	public Map insUNCityStaffClementChJMt(Map mapParm) {
		TParm parm = new TParm(mapParm);
		if (isClientlink())
			return (Map) callServerMethod(mapParm);
		// 查询要退费记录信息
		TParm result = new TParm();
		if (!INSTJFlow.getInstance().queryRun(parm)) {
			result.setErr(-1, "城居门特,有在途数据不可以执行");
			return result.getData();
		}
		TConnection connection = getConnection();
		// 退费明细生成本地对账标志置0
		TParm insOPDParm = INSTJFlow.getInstance().resetInsetOpdandOpdOrder(
				parm, connection);
		if (insOPDParm.getErrCode() < 0) {
			return resultParm(result, insOPDParm.getErrText(), connection);
		}
		if (!resetGetInsParm(parm, insOPDParm)) {
			connection.close();
			result.setErr(-1, "城职门特,医保数据查询有问题");
			return result.getData();
		}
		// 门诊退费
		TParm resetInsOPDParm = INSTJFlow.getInstance().resetFeeChJMt(parm,
				insOPDParm);
		if (resetInsOPDParm.getErrCode()<0) {
			return resultParm(result, "城居门特,退费取消操作失败", connection);
		}
		int flg = resetInsOPDParm.getInt("SOCIAL_FLG");
		//System.out.println("FLG:::::::::::::::" + flg);
		if (flg <= 0) {
			// 退费取消操作
			if (!INSTJFlow.getInstance().resetConcelFee(insOPDParm,
					parm.getInt("INS_TYPE"))) {
				return resultParm(resetInsOPDParm, "城居门特,退费取消操作失败", connection);
			}
			return resultParm(resetInsOPDParm, "城居门特,门诊退费操作有问题", connection);
		}
		if (flg == 1) {
			// 修改本地对账标志为1
			if (!INSTJFlow.getInstance().resetUpdateInsOpd(insOPDParm, "1",
					parm.getValue("UNRECP_TYPE"), parm.getInt("INS_TYPE"),
					connection)) {
				return resultParm(resetInsOPDParm, "城居门特,退费取消操作失败", connection);
			}
		}
		insOPDParm.setData("NHI_REGION_CODE",parm.getValue("REGION_CODE"));
		// 退费确认交易
		result = INSTJFlow.getInstance().resetFeeInsSettleChJMt(insOPDParm);
		if (result.getErrCode()<0) {
			return resultParm(result, "城居门特,退费取消操作失败", connection);
		}
		flg = result.getInt("SOCIAL_FLG");
		if (flg <= 0) {
			return resultParm(resetInsOPDParm, "城居门特,退费取消操作失败", connection);
		}
		if (flg == 3) {
			// 修改本地对账标志为3
			if (!INSTJFlow.getInstance().resetUpdateInsOpd(insOPDParm, "3",
					parm.getValue("UNRECP_TYPE"), parm.getInt("INS_TYPE"),
					connection)) {
				return resultParm(resetInsOPDParm, "城居门特,退费取消操作失败", connection);
			}
		}
		// 在途状态添加
		parm.setData("EXE_TYPE", parm.getValue("UNRECP_TYPE"));
		if (!INSTJFlow.getInstance().insertRun(parm, "DataDown_cmts_L",connection)) {
			return resultParm(result, "城居门特,在途状态添加数据有问题", connection);
		}
		connection.commit();
		connection.close();
		return result.getData();
	}

	/**
	 * 公共方法 执行：1.城职普通 2.城职门特 3.城居门特
	 * 
	 * @param mapPam
	 *            调用接口返回出参 U 方法 A 方法 2011-11-25
	 * @return
	 */
	
	public TParm insCommFunction(Map mapPam) {
		TParm parm = new TParm(mapPam);
		int insType = parm.getInt("INS_TYPE"); // 1.城职 普通 2.城职门特 3.城居门特
		Map result = new HashMap();
		// TConnection connection = getConnection();
		switch (insType) {

		case 1:// 城职普通
			result = cityStaffCommon(mapPam);
			break;
		case 2:// 城职门特
			result = cityStaffClement(mapPam);
			break;
		case 3:// 城居门特
			result = specialCityDenizen(mapPam);
			break;
		}
		return new TParm(result);
	}

	/**
	 * 公共退费方法1.城职普通 2.城职门特 3.城居门特
	 * 
	 * @param mapPam
	 *            获得就诊号码:CASE_NO 医保就诊号 CONFIRM_NO 就医类别 INS_TYPE
	 */
	public TParm insResetCommFunction(Map mapPam) {
		TParm parm = new TParm(mapPam);
		int insType = parm.getInt("INS_TYPE"); // 1.城职 普通 2.城职门特 3.城居门特
		Map result = new HashMap();
		//System.out.println("mapPam::::"+mapPam);
		switch (insType) {
		case 1:// 城职普通
			result = insUNCityStaffCommonChZPt(mapPam);
			break;
		case 2:// 城职门特
			result = insUNCityStaffClementChZMt(mapPam);
			break;
		case 3:// 城居门特
			result = insUNCityStaffClementChJMt(mapPam);
			break;
		}
		return new TParm(result);
	}

	/**
	 * 门诊自动对账
	 * 
	 * @return
	 */
	public Map insOPBAutoAccount(Map mapPam) {
		TParm parm = new TParm(mapPam);
		if (isClientlink())
			return (Map) callServerMethod(mapPam);
		return INSTJFlow.getInstance().autoAccountComm(parm);
	}

	/**
	 * 门诊自动对账 城职普通
	 * 
	 * @param mapPam
	 * @return
	 */
//	public Map insCfCommAutoChZPt(Map mapPam) {
//		TParm parm = new TParm(mapPam);
//		if (isClientlink())
//			return (Map) callServerMethod(mapPam);
//		INSTJFlow.getInstance().autoAccountComm(parm);
//		return new HashMap();
//	}

	/**
	 * 门诊对总账
	 * 
	 * @param mapPam
	 * @return
	 */
	public Map insOpdAccount(Map mapPam) {
		TParm parm = new TParm(mapPam);
		if (isClientlink())
			return (Map) callServerMethod(mapPam);

		TParm result = INSTJFlow.getInstance().opdAccountComm(parm);
		if (result.getErrCode() < 0) {
			return result.getData();
		} else {
			return result.getData();
		}
	}

	/**
	 * 门诊对总账保存
	 * 
	 * @param parm
	 * @return
	 */
	public Map insOpdAccountSave(Map mapPam) {
		if (isClientlink())
			return (Map) callServerMethod(mapPam);
		TParm parm = new TParm(mapPam);
		TConnection connection = getConnection();
		// 修改 INS_OPD \INS_OPD_ORDER
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			TParm tempParm = new TParm();
			tempParm.setRowData(-1, parm, i);// 获得一条要对账的数据
			if (!INSTJFlow.getInstance().updateInsAmtFlg(tempParm, "5",
					tempParm.getValue("RECP_TYPE"))) {
				tempParm.setErr(-1, "修改数据出错");
				connection.close();
				return tempParm.getData();
			}
		}
		connection.commit();
		connection.close();
		return new HashMap();
	}
	/**
	 * 门诊对明细账保存
	 * 
	 * @param parm
	 * @return
	 */
	public Map insOpdOrderAccountSave(Map mapPam){
		if (isClientlink())
			return (Map) callServerMethod(mapPam);
		TParm parm = new TParm(mapPam);
		TConnection connection = getConnection();
		// 修改 INS_OPD \INS_OPD_ORDER
		TParm result = null;
		for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
			TParm tempParm = new TParm();
			tempParm.setRowData(-1, parm, i);// 获得一条要对账的数据
			result=INSTJFlow.getInstance().updateINSOpdOrder(tempParm, "5",
					tempParm.getValue("RECP_TYPE"));
			if (result.getErrCode()<0) {
				tempParm.setErr(-1, "修改数据出错");
				connection.close();
				return tempParm.getData();
			}
		}
		connection.commit();
		connection.close();
		return new HashMap();
	}
	/**
	 * 对明细账
	 * 
	 * @param mapPam
	 * @return
	 */
	private Map insOrderAccount(Map mapPam) {
		TParm parm = new TParm(mapPam);
//		if (isClientlink())
//			return (Map) callServerMethod(mapPam);
		TParm result = new TParm();
		String caseNo = "";
		String recpType = "";
		// for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
		TParm tempParm = new TParm();
		tempParm.setRowData(-1, parm, 0);
		caseNo = tempParm.getValue("CASE_NO");
		recpType = tempParm.getValue("RECP_TYPE");
		if (tempParm.getValue("RECP_TYPE").equals("REGT")
				|| tempParm.getValue("RECP_TYPE").equals("OPBT")) {
			// 退费操作
			tempParm.setData("UN_FLG", 1);
			result = INSTJFlow.getInstance()
					.opdOrderAccountComm(tempParm, parm);
		} else {
			// 正常
			tempParm.setData("UN_FLG", 0);
			result = INSTJFlow.getInstance()
					.opdOrderAccountComm(tempParm, parm);
		}
		// 汇总数据
		if (result.getErrCode() < 0) {
			return result.getData();
		}
		result.setData("CASE_NO", caseNo);
		result.setData("RECP_TYPE", recpType);
		int insType = parm.getInt("INS_TYPE");
		String pipeline = "";// INS_IO 函数名称
		String plotType = "";// INS_IO 方法名称
		TParm opdReturnParm = INSOpdTJTool.getInstance().selectResetFee(tempParm)
				.getRow(0);
		// TParm
		// opdOrderReturnParm=INSOpdOrderTJTool.getInstance().selectResetOpdOrder(parm).getRow(0);
		TParm mzConfirmParm = INSMZConfirmTool.getInstance().queryMZConfirmOne(
				tempParm).getRow(0);
		TParm resultTwoParm = new TParm();// 本地数据
		switch (insType) {
		case 1:
			resultTwoParm = INSTJFlow.getInstance().getOpdOrderChZPtParm(
					result, opdReturnParm, mzConfirmParm);
			pipeline = "DataDown_rs";
			plotType = "M";
			break;
		case 2:
			resultTwoParm = INSTJFlow.getInstance().getOpdOrderChZMtParm(
					result, opdReturnParm, mzConfirmParm);
			pipeline = "DataDown_mtd";
			plotType = "E";
			break;
		case 3:
			resultTwoParm = INSTJFlow.getInstance().getOpdOrderChJMtParm(
					result, opdReturnParm, mzConfirmParm);
			pipeline = "DataDown_cmtd";
			plotType = "E";
			break;
		}
		result.setData("PIPELINE", pipeline);
		result.setData("PLOT_TYPE", plotType);
		result.setData("IN_OUT", "OUT");
		result.setData("ownParm", resultTwoParm.getData());// 本地数据
		return result.getData();
	}
	/**
	 * 对明细账
	 * 
	 * @param mapPam
	 * @return
	 */
	public TParm insOpdOrderAccount(Map mapPam){
		Map map=insOrderAccount(mapPam);
		return new TParm(map);
	}
	/**
	 * 门特登记开具保存操作
	 * @param mapPam
	 * @return
	 */
	public Map onCommandButSave(Map mapPam){
		if (isClientlink())
			return (Map) callServerMethod(mapPam);
		TParm parm = new TParm(mapPam);
		return getCommandButSave(parm).getData();
	}
	private TParm getCommandButSave(TParm parm){
		TParm result=new TParm();
		TParm loadDownParm=new TParm();//存在门特信息下载数据
		TParm saveRegisterParm=new TParm();//保存数据
		TParm returnParm=new TParm();//返回数据
		TParm commParm =new TParm();//门特登记共享资源
		//门特登记开具封锁信息
		//System.out.println("查看是否执行：："+parm);
		parm.setData("CROWD_TYPE",parm.getValue("INS_CROWD_TYPE"));//人群类别
		result=getLockInfo(parm);
		if (result.getErrCode()<0) {//封锁信息失败返回
			return result;
		}
		//System.out.println("result:::"+result);
		parm.setData("REGISTER_NO",result.getValue("REGISTER_NO"));//门特登记编号
		parm.setData("REGISTER_DATE","");//接续时间
	//	result.setData("CROWD_TYPE",parm.getValue("CROWD_TYPE"));
		//==============pangben 2012-4-9 start 医保更新 添加鉴定时间
		String message="";
	    Timestamp nowTime = SystemTool.getInstance().getDate();//当前时间
	    if (null!=result.getValue("LAST_JUDGE_DATE") && result.getValue("LAST_JUDGE_DATE").length()>0) {
	    	Timestamp  oldTime= StringTool.rollDate(result.getTimestamp("LAST_JUDGE_DATE"),180);//上次鉴定时间+半年
			if (nowTime.after(oldTime)) {//大于6个月可以执行
				
			}else{
				message="上次鉴定时间已经还未超过半年,请到第一鉴定中心（一中心医院）申请再次鉴定";
			}
		}
		
		//上次鉴定结论LAST_JUDGE_END:0.可认定1不予认定
	    String messageEnd="";
	    if (null!=result.getValue("LAST_JUDGE_END") &&result.getValue("LAST_JUDGE_END").length()>0) {	    	
			if (result.getValue("LAST_JUDGE_END").equals("1")) {
				messageEnd="上次鉴定结论:不予认定";
			}
		}
		
		//==============pangben 2012-4-9 stop 
		if (result.getInt("REGISTER_SERIAL_NO")==1) {//1 首次 2 接续3 逾期首次
		}else if(result.getInt("REGISTER_SERIAL_NO")==2){//接续
			//获得病患共享数据
			commParm=getCommpatInfo(parm);
			if (commParm.getErrCode()<0) {//病患共享数据返回
				return commParm;
			}
			//医院下载上次门特信息
			parm.setData("BEGIN_DATE",result.getValue("BEGIN_DATE").replace("-", "").substring(0,8));
			loadDownParm=loadDownInfo(parm);//------????有问题 存在没有数据状态怎么解决
			if (loadDownParm.getErrCode()<0) {
				return loadDownParm;
			}
			loadDownParm.setData("MR_NO",parm.getValue("MR_NO"));
			loadDownParm.setData("REGION_CODE",parm.getValue("REGION_CODE"));
			loadDownParm.setData("CASE_NO",parm.getValue("CASE_NO"));
			loadDownParm.setData("NHI_REGION_CODE",parm.getValue("NHI_REGION_CODE"));//门特诊断医院编码
			loadDownParm.setData("PAY_KIND",parm.getValue("PAY_KIND"));//支付类别
			loadDownParm.setData("OPT_USER",parm.getValue("OPT_USER"));//操作人
			loadDownParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));//操作ip	
			loadDownParm.setData("INS_CROWD_TYPE",parm.getValue("INS_CROWD_TYPE"));//人群类别
			//电话
			loadDownParm.setData("PAT_PHONE",parm.getValue("PAT_PHONE"));
			loadDownParm.setData("PAT_ZIP_CODE",parm.getValue("PAT_ZIP_CODE"));
			loadDownParm.setData("PAT_ADDRESS",parm.getValue("PAT_ADDRESS"));
			loadDownParm.setData("OWN_NO",parm.getValue("OWN_NO"));
			
			//保存门特登记信息
			//getParm(parm, loadDownParm);
			returnParm.setData("commParm",commParm.getData());//获得病患共享数据
			returnParm.setData("MESSAGE","存在门特信息");
			loadDownParm.setData("FLG","Y");
			parm=loadDownParm;
		}
		parm.setData("REGISTER_CENTER_USER",null==parm.getValue("REGISTER_CENTER_USER")?"":parm.getValue("REGISTER_CENTER_USER"));
		parm.setData("MED_HELP_COMPANY",null==parm.getValue("MED_HELP_COMPANY")?"":parm.getValue("MED_HELP_COMPANY"));		
		saveRegisterParm=saveRegister(parm);
		if (saveRegisterParm.getErrCode()<0) {
			return saveRegisterParm;
		}
		//保存数据
		parm.setData("STATUS_TYPE",0);//未审核状态
		//System.out.println("执行操作：：："+parm);
		//parm.setData("OWN_NO",loadDownParm.getValue("PERSONAL_NO"));//个人编码
		parm.setData("DR_CODE1",parm.getValue("OPT_USER"));//记医生1
		parm.setData("DR_CODE2",parm.getValue("OPT_USER"));//医生2
		parm.setData("AUDIT_CENTER_USER","");//
		parm.setData("UNPASS_REASON","");//不通过原因
		//===============pangben 2012-4-10 医保添加新数据
	    parm.setData("NUMBER_PAY_AMT",saveRegisterParm.getDouble("NUMBER_PAY_AMT"));//人头付费支付金额(糖尿病)
	    parm.setData("JUDGE_SEQ",saveRegisterParm.getValue("JUDGE_SEQ"));//鉴定顺序号(糖尿病)
	    parm.setData("LAST_JUDGE_END",result.getValue("LAST_JUDGE_END"));//
	    parm.setData("BEGIN_DATE",SystemTool.getInstance().getDateReplace(saveRegisterParm.getValue("BEGIN_DATE"), true));
	    parm.setData("END_DATE",SystemTool.getInstance().getDateReplace(saveRegisterParm.getValue("END_DATE"),true));
	    //===============pangben 2012-4-10 stop
		result=INSMTRegisterTool.getInstance().saveINSMTRegister(parm);
		if (result.getErrCode()<0) {	
			return result;
		}
		returnParm.setData("MTRegisterParm",result.getData());
		returnParm.setData("message",message);
		returnParm.setData("messageEnd",messageEnd);
		return returnParm;
	}
	/**
	 * 校验是否没有数据
	 * @param parm
	 * @param commParm
	 */
	private void getParm(TParm parm,TParm commParm){
//		//电话
		parm.setData("PAT_PHONE",getNameValue(parm.getValue("PAT_PHONE"),commParm.getValue("PAT_PHONE")));
		//选定医保治疗医院(一级)
		parm.setData("HOSP_CODE_LEVEL1",commParm.getValue("HOSP_CODE_LEVEL1"));
		//选定医保治疗医院(二级)
		parm.setData("HOSP_CODE_LEVEL2",commParm.getValue("HOSP_CODE_LEVEL2"));
		//选定医保治疗医院(三级)
		parm.setData("HOSP_CODE_LEVEL3",commParm.getValue("HOSP_CODE_LEVEL3"));
		//选定医保治疗医院(三级专?
		parm.setData("HOSP_CODE_LEVEL3_PRO",commParm.getValue("HOSP_CODE_LEVEL3_PRO"));
		//选定医保治疗医院(定点零
		parm.setData("DRUGSTORE_CODE",commParm.getValue("DRUGSTORE_CODE"));
		//辅助检查
		parm.setData("ASSISTANT_EXAMINE",commParm.getValue("ASSISTANT_EXAMINE"));
		//门特诊断医院编码
		parm.setData("DIAG_HOSP_CODE",commParm.getValue("DIAG_HOSP_CODE"));
		//门特登记编码
		parm.setData("REGISTER_NO",commParm.getValue("MT_REGISTER_CODE"));	
		
		parm.setData("REGISTER_SERIAL_NO",commParm.getValue("REGISTER_SERIAL_NO"));//	门特登记序号		
		
		parm.setData("DISEASE_CODE",commParm.getValue("DISEASE_CODE"));//	门特病种编码		
		parm.setData("PERSONAL_NO",commParm.getValue("PERSONAL_NO"));//个人编码		
		parm.setData("PAT_NAME",commParm.getValue("PAT_NAME"));//姓名	
		parm.setData("SEX_CODE",commParm.getValue("SEX_CODE"));	//性别		1男2女		
		parm.setData("REGISTER_TYPE",commParm.getValue("REGISTER_TYPE"));	//门特登记类别 1 首次 2 接续	3 逾期首次	
		parm.setData("DISEASE_HISTORY",commParm.getValue("DISEASE_HISTORY"));	//病史
		parm.setData("DIAG_CODE",commParm.getValue("DIAG_CODE"));	//临床诊断		
		parm.setData("DISEASE_HISTORY",commParm.getValue("DISEASE_HISTORY"));	//病史
		parm.setData("BEGIN_DATE",SystemTool.getInstance().getDateReplace(commParm.getValue("BEGIN_DATE"), true));	//门特登记开始时间	YYYY-MM-DD HH24:MI:SS		
		parm.setData("END_DATE",SystemTool.getInstance().getDateReplace(commParm.getValue("END_DATE"), true));	//门特登记结束时间		YYYY-MM-DD HH24:MI:SS		
		parm.setData("MED_HISTORY",commParm.getValue("MED_HISTORY"));	//既往史(糖尿病)			
		parm.setData("ASSSISTANT_STUFF",commParm.getValue("ASSSISTANT_STUFF"));	//辅助材料(糖尿病)			
		parm.setData("JUDGE_CONTER_I",commParm.getValue("JUDGE_CONTER_I"));//	鉴定中心意见(糖尿病)
		parm.setData("JUDGE_END",commParm.getValue("JUDGE_END"));//	鉴定结论(糖尿病)
		
	}
	private String getNameValue(String name,String newName){
		if (null==name || name.length()<=0) {
			return null==newName || newName.length()<=0?"":newName;
		}
		return name;
	}
	/**
	 * 门特登记开具封锁信息
	 * @param parm
	 * @return
	 */
	private TParm getLockInfo(TParm parm){
		TParm result=new TParm();
		// 人群类别:1.城职 2.城居
		if (parm.getInt("CROWD_TYPE")==1) {//城职
			result=INSTJTool.getInstance().DataDown_mts_A(parm);
		}else if (parm.getInt("CROWD_TYPE")==2) {//城居
			result=INSTJTool.getInstance().DataDown_cmts_A(parm);
		}
		if (!INSTJTool.getInstance().getErrParm(result)) {
			return result;
		}
		return result;
	}
	/**
	 * 保存门特登记信息
	 * @return
	 */
	private TParm saveRegister(TParm parm){
		TParm result=new TParm();
		if (parm.getInt("CROWD_TYPE")==1) {//城职	
			result=INSTJTool.getInstance().DataDown_mts_B(parm);
		}else if (parm.getInt("CROWD_TYPE")==2) {//城居
			result=INSTJTool.getInstance().DataDown_cmts_B(parm);
		}
		if (!INSTJTool.getInstance().getErrParm(result)) {
			return result;
		}
		return result;
	}
	/**
	 * 获得病患共享数据
	 * @param parm
	 * @return
	 */
	private TParm getCommpatInfo(TParm parm){
		TParm result=new TParm();
		if (parm.getInt("CROWD_TYPE")==1) {//城职	
		result=INSTJTool.getInstance().DataDown_mtd_H(parm);
		}
		else if (parm.getInt("CROWD_TYPE")==2) {//城居
			result=INSTJTool.getInstance().DataDown_cmtd_H(parm);
		}
		if (!INSTJTool.getInstance().getErrParm(result)) {
			return result;
		}
		return result;
	}
	/**
	 * 共享信息
	 */
	public Map onShare(Map mapPam){
		TParm parm = new TParm(mapPam);
		if (isClientlink())
			return (Map) callServerMethod(mapPam);
		return getCommpatInfo(parm).getData();
	}
	/**
	 * 医院下载上次门特信息 
	 * @param parm
	 * @return
	 */
	private TParm loadDownInfo(TParm parm){
		TParm loadDownParm=new TParm();
		if (parm.getInt("CROWD_TYPE")==1) {//城职		
			loadDownParm=INSTJTool.getInstance().DataDown_mts_C(parm);
		}else if (parm.getInt("CROWD_TYPE")==2) {//城居
			loadDownParm=INSTJTool.getInstance().DataDown_cmts_C(parm);
		}
		if (!INSTJTool.getInstance().getErrParm(loadDownParm)) {
			return loadDownParm;
		}
		return loadDownParm;
	}
	/**
	 * 下载操作
	 * @param mapPam
	 * @return
	 */
	public Map onLoadDown(Map mapPam){
		TParm parm = new TParm(mapPam);
		if (isClientlink())
			return (Map) callServerMethod(mapPam);
		TParm result=new TParm();
		//医院下载上次门特信息
		result=loadDownInfo(parm);
		if (result.getErrCode()<0) {
		
			return result.getData();
		}
		result.setData("PAY_KIND",parm.getValue("PAY_KIND"));//支付类别
		result.setData("REGION_CODE",parm.getValue("REGION_CODE"));//区域
		result.setData("CASE_NO",parm.getValue("CASE_NO"));
		result.setData("MR_NO",parm.getValue("MR_NO"));
		result.setData("REGION_CODE",parm.getValue("REGION_CODE"));
		result.setData("OWN_NO",result.getValue("PERSONAL_NO"));//个人编码
		result.setData("OPT_USER",parm.getValue("OPT_USER"));//ID
		result.setData("OPT_TERM",parm.getValue("OPT_TERM"));//IP
		result.setData("STATUS_TYPE",parm.getValue("STATUS_TYPE"));//本地审核状态
		result.setData("NHI_REGION_CODE",parm.getValue("NHI_REGION_CODE"));
		result.setData("UNPASS_REASON",parm.getValue("UNPASS_REASON"));
		result.setData("DR_CODE1",parm.getValue("OPT_USER"));
		result.setData("DR_CODE2",parm.getValue("OPT_USER"));
		result.setData("LAST_JUDGE_END",result.getValue("JUDGE_END"));
		result.setData("AUDIT_CENTER_USER",result.getValue("REGISTER_CENTER_USER"));
		result.setData("INS_CROWD_TYPE","2");
		//保存数据
		result=INSMTRegisterTool.getInstance().saveINSMTRegister(result);
		if (result.getErrCode()<0) {	
			return result.getData();
		}
		return result.getData();
	} 
	public Map readINSCard(Map mapPam) {
		if (isClientlink())
			return (Map) callServerMethod(mapPam);
		TParm parm=new TParm(mapPam);
		TParm result=INSTJFlow.getInstance().readINSCard(parm);

		return result.getData();
	}
	/**
	 * INSFEE.X界面显示
	 * @param parm
	 * @param control
	 * @return
	 */
	public TParm onInsFee(TParm parm,TControl control){
		TParm r = (TParm) control.openDialog(
				"%ROOT%\\config\\ins\\INSFee.x", parm);
		return r;
	}
}
