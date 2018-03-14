package jdo.reg;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jdo.bil.BIL;
import jdo.device.CallNoTx;
import jdo.hl7.Hl7Communications;
import jdo.ins.INSOpdOrderTJTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSTJFlow;
import jdo.ins.INSTJReg;
import jdo.opb.OPB;
import jdo.opd.OrderList;
import jdo.opd.OrderTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.root.client.SocketLink;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import java.lang.Character;

/**
 * <p>
 * Title:建行接口tool
 * 
 * <p>
 * Description:建行接口tool
 * </p>
 * 
 * Copyright: Copyright (c) 2008 </p>
 * 
 * <p>
 * Company:BuleCore
 * </p>
 * 
 * @author fuwj attributable
 * @version 1.0
 */
public class REGCcbTool extends TJDOTool {

	public static REGCcbTool instanceObject;

	public static String ISSCUESS = "0"; // 接口调用成功标示

	public static String ISERROR = "1"; // 接口调用失败标示

	public static String TRANTYPEYY = "5"; // 交易确认预约挂号

	public static String TRANTYPEDR = "1"; // 交易确认挂号

	public static String ISVIP = "Y"; // VIP诊

	public static String YJZ = "3"; // 查询已就诊挂号数据

	public static String GQ = "2"; // 查询已过期挂号数据

	public static String ZC = "0"; // 查询正常的挂号数据

	public static String YYGH = "1"; // 预约挂号交易确认

	public static String BD = "2"; // 医保报道交易确认

	public static String GRGH = "3"; // 当日挂号交易确认

	public static String INSPASSWORD = "111111"; // 医保卡默认密码

	public static String PRINTNO = "111111"; // 传给医保票据号，写死

	public static String CZ = "1"; // 暂只支持城职
	
	public String phaRxNo;// 药品审核界面添加处方签数据 
	
	private SocketLink client1;

	/**
	 * 得到实例
	 * 
	 * @return REGCcbTool
	 */
	public static REGCcbTool getInstance() {
		if (instanceObject == null)
			instanceObject = new REGCcbTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public REGCcbTool() {
		setModuleName("reg\\REGCcbModule.x");
		onInit();
	}

	/**
	 * 读取 TConfig.x
	 * 
	 * @return TConfig
	 */
	public static TConfig getProp() {
		TConfig config = TConfig
				.getConfig("WEB-INF\\config\\system\\TConfig.x");
		if (config == null) {
			return null;
		}
		return config;
	}

	/**
	 * 得到对账文件读取路径
	 * 
	 * @return
	 */
	public String getPath() {
		String path = "";
		path = getProp().getString("", "CHECKACCOUNTFTP.PATH");
		if (path == null || path.trim().length() <= 0) {
			return null;
		}
		return path;
	}

	/***
	 * 院内病人信息简历
	 *
	 * @param parm
	 * @param tConnection 
	 * @return
	 */
	public TParm signTran(TParm parm, TConnection connection) {
		// 实例化返回参数对象returnParm
		TParm returnParm = new TParm();
		String ccbId = (String) parm.getData("IDNO");
		String id_no = "";//身份证最后一位判断
//		 System.out.println("1=====" +ccbId);
		if(ccbId.length()==18) {	 
			char a = ccbId.charAt(17);
			if(Character.isLetter(a)){
				if(Character.isLowerCase(a))
					id_no = ccbId.substring(0, 17)+
					ccbId.substring(17, 18).toUpperCase();
				else if(Character.isUpperCase(a))
					id_no = ccbId.substring(0, 17)+
					ccbId.substring(17, 18).toLowerCase();	
			}
			ccbId = ccbId.substring(0, 6)+ccbId.substring(8,17);
	        //身份证最后一位判断			 
		}
		parm.setData("CCB_ID",ccbId);
		parm.setData("CCB_ID_NO",id_no);
		TParm result = query("getPatInfoByidNo", parm);
		returnParm.setData("EXECUTE_FLAG", ISERROR);
		if (result.getErrCode() < 0) {
			err("ERR:signTran" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			returnParm.setData("PATIENT_ID", "");
			returnParm.setData("EXECUTE_MESSAGE", "病患信息查询失败");
			return returnParm;
		}
		// 获取病患号
		String mrNo = (String) result.getData("MR_NO", 0);
		if (isNull(mrNo)) {
			parm.setData("MR_NO", mrNo);
			// 更新病患信息
			result = this.updatePatInfo(parm, connection);
			if (result.getErrCode() < 0) {
				err("ERR:updatePatInfo" + result.getErrCode()
						+ result.getErrText() + result.getErrName());
				returnParm.setData("PATIENT_ID", "");
				returnParm.setData("EXECUTE_MESSAGE", "更新病患信息失败");
				return returnParm;
			}
		} else {
			returnParm.setData("PATIENT_ID", "");
			returnParm.setData("EXECUTE_MESSAGE", "身份信息不匹配,请至柜台人工办理");
			return returnParm;
		}
		returnParm.setData("PATIENT_ID", mrNo);
		returnParm.setData("EXECUTE_MESSAGE", "建立成功");
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		return returnParm;
	}

	/***
	 * 更新病患信息
	 * 
	 * @param parm
	 * @param tConnection
	 * @return
	 */
	public TParm updatePatInfo(TParm parm, TConnection connection) {
		String name = (String) parm.getData("PAT_NAME"); // 姓名
		String idNo = (String) parm.getData("IDNO"); // 证件号
		String mrNo = (String) parm.getData("MR_NO"); // 病患号
		String sql = "UPDATE SYS_PATINFO set PAT_NAME='" + name + "'";
		if (parm.getData("CCB_PERSON_NO") != null) {
			String personNo = (String) parm.getData("CCB_PERSON_NO");
			sql = sql + ",CCB_PERSON_NO='" + personNo + "'";
		}
		if (parm.getData("TEL_HOME") != null) {
			String tel = (String) parm.getData("TEL_HOME");
			sql = sql + ",CELL_PHONE='" + tel + "'";
		}
		if (parm.getData("ADDRESS") != null) {
			String address = (String) parm.getData("ADDRESS");
			sql = sql + ",RESID_ADDRESS='" + address + "'";
		}
		sql = sql + " WHERE MR_NO='" + mrNo + "'";

		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		return result;
	}

	/**
	 * 保存当日或预约挂号数据
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm saveRegister(TParm parm, TConnection connection) {

		// 返回的parm对象
		TParm returnParm = new TParm();
		// 转化VIP挂号starttime格式
		String establish_time = (String) parm.getData("ESTABLISH_TIME");
		String start_time = this.getStartTime((String) establish_time);
		parm.setData("START_TIME", start_time);
		parm.setData("CCB_PERSON_NO", (String) parm.getData("PERSON_NO"));
		// 查询是否存在病案号
		TParm result = query("getPatInfoByPersonNo", parm);
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err(result.getErrCode() + " " + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "无此人病案信息");
			return returnParm;
		}
		String mr_no = (String) result.getData("MR_NO", 0);
		String pat_name = (String) result.getData("PAT_NAME", 0);
		parm.setData("PAT_NAME", pat_name);
		parm.setData("MR_NO", mr_no);
		String session_code = getSessionCode(establish_time);
		parm.setData("SESSION_CODE", (String) session_code);
		String ADM_DATE = ((String) parm.getData("DATE")).replace("-", "");
		parm.setData("ADM_DATE", ADM_DATE);
		// 查询是否有该排班表信息
		result = this.getRegAchDay(parm);
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR:getRegAchDay" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "无此排班信息");
			return returnParm;
		}
		String case_no = SystemTool.getInstance().getNo("ALL", "REG",
				"CASE_NO", "CASE_NO");
		parm.setData("CASE_NO", case_no);
		parm.setData("ADM_TYPE", "O");
		String region_code = (String) result.getData("REGION_CODE", 0);
		parm.setData("REGION_CODE", region_code);
		parm.setData("REG_DATE", SystemTool.getInstance().getDate());
		// 转化sessioncode格式：上午:01 下午:02
		String clinicroomNo = (String) result.getData("CLINICROOM_NO", 0);
		parm.setData("CLINICAREA_CODE", (PanelRoomTool.getInstance()
				.getAreaByRoom(TypeTool.getString(clinicroomNo))).getValue(
				"CLINICAREA_CODE", 0));
		parm.setData("CLINICROOM_NO", clinicroomNo);
		String vip_flg = (String) result.getData("VIP_FLG", 0);
		parm.setData("VIP_FLG", vip_flg);
		String clinictype_code = (String) result.getData("CLINICTYPE_CODE", 0);
		parm.setData("CLINICTYPE_CODE", clinictype_code);
		String reg_adm_time = this.getStartTime((String) establish_time);
		parm.setData("REG_ADM_TIME", "");

		String realdept_code = (String) result.getData("REALDEPT_CODE", 0);
		parm.setData("REALDEPT_CODE", realdept_code);
		String realdr_code = (String) result.getData("REALDR_CODE", 0);
		parm.setData("REALDR_CODE", realdr_code);
		parm.setData("APPT_CODE", "Y");
		parm.setData("VISIT_CODE", "1");
		parm.setData("REGMETHOD_CODE", "B");
		parm.setData("CTZ1_CODE", "99");
		parm.setData("HEAT_FLG", "N");
		parm.setData("ADM_STATUS", "1");
		parm.setData("REPORT_STATUS", "1");
		parm.setData("WEIGHT", 0);
		parm.setData("HEIGHT", 0);
		parm.setData("ARRIVE_FLG", "N");
		// 参保类型
		parm.setData("INS_PAT_TYPE","");
		if (parm.getData("SS_TYPE") != null
				&& parm.getData("SS_TYPE").toString().length() > 0) {
			String ssType = (String) parm.getData("SS_TYPE");
			if ("0".equals(ssType)) {
				parm.setData("INS_PAT_TYPE", "1");
			}
		}
		// 待确认
		parm.setData("ADM_REGION", region_code);
		String admType = (String) parm.getData("ADM_TYPE");
		// 挂号费
		double reg_fee = BIL.getRegDetialFee(admType, (String) parm
				.getData("CLINICTYPE_CODE"), "REG_FEE", "99", "", "", "");
		parm.setData("REG_FEE", reg_fee);
		// 诊查费
		double clinic_fee = BIL.getRegDetialFee(admType, (String) parm
				.getData("CLINICTYPE_CODE"), "CLINIC_FEE", "99", "", "", "");
		parm.setData("CLINIC_FEE", clinic_fee);

		// 判断是否VIP
		String isVip = (String) result.getData("VIP_FLG", 0);
		// VIP号
		if (ISVIP.equals(isVip)) {
			// 设置vip的sessionCode
			result = this.getVipQueNo(parm);
			if (result.getErrCode() != 0 || result.getCount() <= 0) {
				err("ERR:getVipQueNo" + result.getErrCode()
						+ result.getErrText() + result.getErrName());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "VIP诊号已经过期或被占用");
				return returnParm;
			}
			parm.setData("REG_ADM_TIME", result.getData("START_TIME", 0));
			Long vip_que_no = (Long) result.getData("QUE_NO", 0);
			parm.setData("QUE_NO", vip_que_no);
			// 更新vip序号
			result = this.updateVipqueno(parm, connection);
			if (result.getErrCode() != 0) {
				err("ERR:REG.updateVipqueno " + result.getErrCode()
						+ result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "更新VIP诊号失败");
				return returnParm;
			}
			// 更新主流水序号
			result = this.updatequeno(parm, connection);
			if (result.getErrCode() != 0) {
				err("ERR:updatequeno" + result.getErrCode()
						+ result.getErrText() + result.getErrName());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "更新诊号失败");
				return returnParm;
			}
		} else {
			// 普通诊号
			Long que_no = (Long) this.getRegAchDay(parm).getData("QUE_NO", 0);
			if (que_no == null) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "获取诊号失败");
				return returnParm;
			}
			parm.setData("QUE_NO", que_no);
			result = this.updatequeno(parm, connection);
			if (result.getErrCode() != 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "更新诊号失败");
				return returnParm;
			}
		}
		Date adm_date = StringTool.getDate((String) parm.getData("DATE"),
				"yyyy-MM-dd");
		parm.setData("ADM_DATE", adm_date);

		boolean iscurrent = (Boolean) parm.getData("iscurrent");
		TParm insParm = new TParm(); // 读医保卡返回参数
		TParm insFeeParm = new TParm();
		// 判断是否是当日挂号，如果是继续走当日挂号流程
		if (iscurrent) {
			// 判断医保卡号是否为空，如不为空需走医保
			if (parm.getData("SSCARD_NO") != null
					&& !"".equals((String) parm.getData("SSCARD_NO"))) {
				TParm regionParm = null;// 获得医保区域代码
				regionParm = SYSRegionTool.getInstance().selectdata("H01");// 获得医保区域代码
				parm.setData("STATE", "9"); // 9：医保卡扣费
				parm.setData("CARD_TYPE", 2); // 9：类型
				parm.setData("TEXT", ";"+(String) parm.getData("SSCARD_NO")+"=");
				parm.setData("NHI_REGION_CODE", regionParm
						.getValue("NHI_NO", 0));
				parm.setData("NHI_NO", (String) parm.getData("SSCARD_NO"));		
				// 读取医保卡信息
				insParm = this.readCard(parm);
				// 医保确认书编号
				if (null == insParm || insParm.getErrCode() < 0
						|| null == insParm.getValue("CARD_NO")
						|| insParm.getValue("CARD_NO").length() <= 0) {
			    	  System.out.println("医保========"); 
					err("ERR:readCard" + result.getErrCode()
							+ result.getErrText() + result.getErrName());
					returnParm.setData("EXECUTE_FLAG", ISERROR);
					returnParm.setData("EXECUTE_MESSAGE", "医保卡读取失败");
					return returnParm;
				}
				String crowdType = (String) insParm.getData("CROWD_TYPE");
				if (!CZ.equals(crowdType)) {
					returnParm.setData("EXECUTE_FLAG", ISERROR);
					returnParm.setData("EXECUTE_MESSAGE", "执行医保失败，暂只支持城职");
					return returnParm;
				}

				String confirmNo = (String) insParm.getData("CONFIRM_NO");
				parm.setData("CONFIRM_NO", confirmNo); // 设置医保确认号
				parm.setData("CTZ1_CODE", insParm.getData("CTZ_CODE")); // 设置身份
				// 分割金额前操作
				insFeeParm = this.saveCardBefore(parm, insParm);
				if (result.getErrCode() != 0) {
					returnParm.setData("EXECUTE_FLAG", ISERROR);
					returnParm.setData("EXECUTE_MESSAGE", "医保卡读取失败");
					return returnParm;
				}
				// 分割金额
				result = this.insExeFee(true, insFeeParm, parm);
				if (result == null || result.getErrCode() < 0) {
					err("ERR:insExeFee" + result.getErrCode()
							+ result.getErrText() + result.getErrName());
					returnParm.setData("EXECUTE_FLAG", ISERROR);
					returnParm.setData("EXECUTE_MESSAGE", "医保分割失败");
					return returnParm;
				}
				returnParm.setData("PER_PAY_AMOUNT", result // 医保金额
						.getDouble("UACCOUNT_AMT"));
				returnParm.setData("S_ACCOUNT_AMOUNT", result // 自费金额
						.getDouble("ACCOUNT_AMT"));
				returnParm.setData("S_GOC_AMOUNT", 0.0);
			} else {
				// 当日不走医保
				parm.setData("CONFIRM_NO", ""); // 不走医保，医保确认号为空
				parm.setData("NHI_NO", "");
				returnParm.setData("PER_PAY_AMOUNT", reg_fee + clinic_fee);// 不走医保，医保卡号为空
				returnParm.setData("S_ACCOUNT_AMOUNT", 0.0);
				returnParm.setData("S_GOC_AMOUNT", 0.0);
			}
			parm.setData("APPT_CODE", "N");
			result = this.insertRegPatAdm(parm, connection);
			if (result.getErrCode() != 0) {
				err("ERR:REG.insertRegPatAdm " + result.getErrCode()
						+ result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "添加挂号主档失败");
				return returnParm;
			}
		} else {
			parm.setData("CONFIRM_NO", ""); // 预约不走医保，医保确认号为空
			parm.setData("NHI_NO", "");
			result = this.insertRegPatAdm(parm, connection); // 插入票据表
			if (result.getErrCode() != 0) {
				err("ERR:REG.insertRegPatAdm " + result.getErrCode()
						+ result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "添加挂号主表失败");
				return returnParm;
			}
			//预约挂号保存时不扣费，不再记录EKT_CCB_TRADE表数据
			// 预约挂号添加交易表不涉及医保
//			parm.setData("BUSINESS_TYPE", "REG");
//			parm.setData("AMT", reg_fee + clinic_fee);
//			parm.setData("TRADE_NO", SystemTool.getInstance().getNo("ALL",
//					"EKT", "CCB_TRADE_NO", "CCB_TRADE_NO"));
//			parm.setData("STATE", "1");
//			parm.setData("RECEIPT_NO", "");
//			result = this.insertCcbTrade(parm);
//			if (result.getErrCode() < 0) {
//				err("ERR:insertCcbTrade" + result.getErrCode()
//						+ result.getErrText() + result.getErrName());
//				returnParm.setData("EXECUTE_FLAG", ISERROR);
//				returnParm.setData("EXECUTE_MESSAGE", "添加交易信息失败");
//				return returnParm;
//			}
			// 预约部分不涉及医保(与建行确认)
			returnParm.setData("PER_PAY_AMOUNT", reg_fee + clinic_fee);
			returnParm.setData("S_ACCOUNT_AMOUNT", 0.0);
			returnParm.setData("S_GOC_AMOUNT", 0.0);
		}
		returnParm = this.setReturnParm(parm, returnParm);
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "保存挂号数据成功");
		return returnParm;
	}

	/***
	 * 查询排班信息
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getRegAchDay(TParm parm) {
		TParm result = query("getRegAchDay", parm);
		if (result.getErrCode() < 0) {
			err("ERR:getRegAchDay" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/***
	 * 获取Vip的queno
	 * 
	 * @param parm
	 * @param tConnection
	 * @return
	 */
	public TParm getVipQueNo(TParm parm) {
		TParm result = query("getVipQueNo", parm);
		if (result.getErrCode() < 0) {
			err("ERR:getVipQueNo" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/***
	 * 更新就诊号
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updatequeno(TParm parm, TConnection connection) {
		TParm result = update("updatequeno", parm, connection);
		String ADM_DATE = ((String) parm.getData("DATE")).replace("-", "");
		parm.setData("ADM_DATE", ADM_DATE);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/***
	 * 更新VIP就诊号
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateVipqueno(TParm parm, TConnection connction) {
		TParm result = update("updateVipqueno", parm, connction);
		String ADM_DATE = ((String) parm.getData("DATE")).replace("-", "");
		parm.setData("ADM_DATE", ADM_DATE);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/***
	 * 更新挂号主档表
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertRegPatAdm(TParm parm, TConnection connection) {

		TParm result = this.update("insertInfo", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * 转化sessioncode格式VIP号
	 */
	public String getSessionCodeVip(String code) {
		String session_code = "";
		int flagNum = code.indexOf(" ");
		String subString = code.substring(0, flagNum);
		if ("上午".equals(subString)) {
			session_code = "01";
		} else if ("下午".equals(subString)) {
			session_code = "02";
		}
		return session_code;
	}

	/**
	 * 转化sessioncode格式普通号
	 */
	public String getSessionCode(String code) {
		String session_code = "";
		if (code.contains("上午")) {
			session_code = "01";
		} else if (code.contains("下午")) {
			session_code = "02";
		}
		return session_code;
	}

	/**
	 * 转化starttime格式
	 * 
	 * @param code
	 * @return
	 */
	public String getStartTime(String code) {
		int flagNum = code.indexOf(" ");
		String subString = code.substring(flagNum + 1, code.length());
		return subString.replace(":", "");
	}

	/***
	 * 添加建行交易费用
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertCcbTrade(TParm parm) {
		parm.setData("BANK_FLG", "Y");
		TParm result = this.update("insertCcbTrade", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * 门诊缴费添加交易表
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertOpbCcbTrade(TParm parm) {
		TParm result = this.update("insertCcbTrade", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/***
	 * 设置预约挂号返回值
	 * 
	 * @param parm
	 * @return
	 */
	public TParm setReturnParm(TParm parm, TParm returnParm) {
		returnParm.setData("PATIENT_ID", parm.getData("MR_NO"));
		returnParm.setData("TRAN_NO", parm.getData("CASE_NO"));
		returnParm.setData("SEQ", parm.getData("QUE_NO"));
		return returnParm;
	}

	/***
	 * 保存当日挂号信息
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm saveCurrnetDay(TParm parm, TConnection connection) {
		TParm reslut = this.saveRegister(parm, connection);
		return reslut;
	}

	/***
	 * 添加门诊收据
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertBilRegRecp(TParm parm, TConnection connection) {
		// 与invrcp表的receipt_no关联
		parm.setData("BILL_DATE", parm.getData("OPT_DATE"));
		parm.setData("CHARGE_DATE", parm.getData("OPT_DATE"));
		parm.setData("PRINT_DATE", parm.getData("OPT_DATE"));
		parm.setData("PAY_CASH", 0);
		TParm result = this.update("insertBilRegRecp", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/***
	 * 添加门诊收据
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertBilOpbRecp(TParm parm, TConnection connection) {
		// 与invrcp表的receipt_no关联
		parm.setData("BILL_DATE", parm.getData("OPT_DATE"));
		parm.setData("CHARGE_DATE", parm.getData("OPT_DATE"));
		parm.setData("PRINT_DATE", parm.getData("OPT_DATE"));
		parm.setData("PAY_CASH", 0);
		TParm result = this.update("insertBilOpbRecp", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/***
	 * 添加票据主档
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertInvrcp(TParm parm, TConnection connection) {
		parm.setData("RECP_TYPE", "REG");
		parm.setData("INV_NO", parm.getData("PRINT_NO"));
		parm.setData("CANCEL_FLG", "0");
		TParm result = this.update("insertInvrcp", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * 添加门诊票据
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertOpbInvrcp(TParm parm, TConnection connection) {
		parm.setData("RECP_TYPE", "OPB");
		parm.setData("STATUS", "0");
		parm.setData("INV_NO", parm.getData("PRINT_NO"));
		parm.setData("CANCEL_FLG", "0");
		TParm result = this.update("insertInvrcp", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/***
	 * 查询预约挂号信息
	 * 
	 * @param parmu
	 * @param connection
	 * @return
	 */
	public TParm bankQueryRegister(TParm parm, TConnection connection) {
		TParm result = this.getPatInfoByPersonNo(parm, connection);
		return result;

	}

	/***
	 * 根据person_no查询病患信息
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm getPatInfoByPersonNo(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		String ccbPersonNo = (String) parm.getData("PERSON_NO");
		String startDate = (String) parm.getData("START_DATE");
		String endDate = (String) parm.getData("END_DATE");
		String cardNo = (String) parm.getData("CARD_NO");
		String mrNo = (String) parm.getData("PATIENT_ID");
		String type = (String) parm.getData("QUERY_TYPE");
		String sql = "SELECT  "
				+ "B.CASE_NO AS TRAN_NO,"
				+ "D.DEPT_CODE AS DEPT_NO,"
				+ "D.DEPT_CHN_DESC AS DEPT_NAME,"
				+ "B.CLINICROOM_NO AS DEPT_STATION,"
				+ "(CASE WHEN B.CLINICTYPE_CODE='03' THEN '0' WHEN B.CLINICTYPE_CODE='01' THEN '1' ELSE '2' END) "
				+ "AS ESTABLISH_TYPE,"
				+ "E.USER_NAME AS DOCTOR_NAME,"
				+ "E.USER_ID AS DOCTOR_NO,"
				+ "(SELECT POS_CHN_DESC FROM SYS_POSITION P WHERE E.POS_CODE=P.POS_CODE AND E.USER_ID=B.DR_CODE) "
				+ "AS DOCTOR_RANK,"
				+ "A.PAT_NAME AS NAME,"
				+ "TO_CHAR(B.ADM_DATE,'yyyy-MM-dd') AS ADM_DATE,"
				+ "'' AS WEEK,"
				+ "B.REG_ADM_TIME AS ESTABLISH_TIME,"
				+ "B.QUE_NO,"
				+ "B.CLINICTYPE_CODE,A.MR_NO AS PATIENT_ID,1 AS COUNT,"
				+ "TO_CHAR(B.REGCAN_DATE,'yyyy-MM-dd') AS CANCEL_DATE,TO_CHAR(B.OPT_DATE,'yyyy-MM-dd') "
				+ "AS OPT_DATE,B.ARRIVE_FLG AS ARRIVE_FLG,"
				+ "(CASE WHEN B.ADM_DATE<TO_DATE(TO_CHAR(sysdate,'YYYY-MM-DD'),'YYYY-MM-DD') THEN 'Y' ELSE 'N' END) AS DATE_FLG,"
				+ "B.ADM_STATUS AS STATUS,A.CCB_PERSON_NO FROM SYS_PATINFO A,REG_PATADM B,SYS_DEPT D,SYS_OPERATOR E "
				+ "WHERE B.REGCAN_USER IS NULL AND B.REGCAN_DATE IS NULL AND"
				+ " D.DEPT_CODE=B.DEPT_CODE AND B.DR_CODE = E.USER_ID AND A.MR_NO=B.MR_NO";
		if (ccbPersonNo != null && !"".equals(ccbPersonNo)) {
			sql = sql + " AND A.CCB_PERSON_NO='" + ccbPersonNo + "'";
		}
		if (mrNo != null && !"".equals(mrNo)) {
			sql = sql + " AND A.MR_NO='" + mrNo + "'";
		}
		if (startDate != null && endDate != null && !"".equals(startDate)
				&& !"".equals(endDate)) {
			sql = sql + " AND B.ADM_DATE BETWEEN TO_DATE('" + startDate
					+ "','YYYY-MM-DD') " + "AND TO_DATE('" + endDate
					+ "','YYYY-MM-DD') ";
		}
		// 正常
		if (ZC.equals(type)) {
			sql = sql
					+ " AND B.ADM_DATE>=to_date(to_char(sysdate,'YYYY-MM-DD'),'YYYY-MM-DD') AND B.ARRIVE_FLG='N'";
		}
		// 未就诊（以报到,当日挂号）
		if ("1".equals(type)) {
			sql = sql + " AND B.ARRIVE_FLG='Y'";
		}
		TParm returnResult = new TParm(TJDODBTool.getInstance().select(sql));
		if (returnResult.getErrCode() < 0 || returnResult.getCount() <= 0) {
			err("ERR:" + returnResult.getErrCode() + returnResult.getErrText()
					+ returnResult.getErrName());
			returnResult.setData("EXECUTE_FLAG", ISERROR);
			returnResult.setData("EXECUTE_MESSAGE", "没有查询到相关记录");
			return returnResult;
		}
		List list = (List) returnResult.getData("CLINICTYPE_CODE");
		for (int i = 0; i < list.size(); i++) {
			String clinicTypeCode = (String) list.get(i);
			double reg_fee = BIL.getRegDetialFee("O", clinicTypeCode,
					"REG_FEE", "99", "", "", "");
			double clinic_fee = BIL.getRegDetialFee("O", clinicTypeCode,
					"CLINIC_FEE", "99", "", "", "");
			double totalFee = reg_fee + clinic_fee;
			returnResult.addData("AMOUNT", reg_fee);
			returnResult.addData("CLINIC_FEE", clinic_fee);
			returnResult.addData("OTHER_FEE", 0);
			returnResult.addData("TOTAL_FEE", reg_fee + clinic_fee);
		}
		returnResult.setData("EXECUTE_FLAG", ISSCUESS);
		return returnResult;
	}

	/**
	 * 获取交易信息记录
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm getCcbTrade(TParm parm, TConnection connection) {
		TParm result = this.query("getCcbTrade", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/***
	 * 退挂更新挂号档主表
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm cancelPatadm(TParm parm, TConnection connection) {

		TParm result = this.update("cancelPatadm", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * 判断是否为空
	 * 
	 * @param str
	 * @return
	 */
	public boolean isNull(String str) {
		if (!"".equals(str) && str != null) {
			return true;
		}
		return false;
	}

	/***
	 * 判断性别
	 * 
	 * @param sex
	 * @return
	 */
	public String isSex(String sex) {
		if (sex == null) {
			return "";
		}
		if ("M".equals(sex)) {
			return "1";
		} else {
			return "2";
		}
	}

	/**
	 * 医保读卡操作
	 * 
	 * @param parm
	 * @return
	 */
	public TParm readCard(TParm parm) {
		// 走医保
		TParm regionParm = null;// 获得医保区域代码
		regionParm = SYSRegionTool.getInstance().selectdata("H01");// 获得医保区域代码
		TParm readParm = new TParm();
		TParm skParm = new TParm();
		skParm.setData("MR_NO", parm.getData("MR_NO"));
		// 读卡请求类型（1：购卡，2：挂号，3：收费，4：住院,5 :门特登记）
		skParm.setData("CARD_TYPE", parm.getData("CARD_TYPE"));
		skParm = SYSRegionTool.getInstance().selectdata("H01");
		parm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO", 0));
		skParm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO", 0));
		skParm.setData("TEXT", parm.getData("TEXT"));
		skParm.setData("OPT_USER", parm.getData("OPT_USER"));
		skParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
		skParm.setData("PASSWORD", INSPASSWORD); // 测试默认

		readParm = new TParm(INSTJReg.getInstance().readINSCard(
				skParm.getData()));// 读卡动作
		if (null == readParm || readParm.getErrCode() < 0
				|| null == readParm.getValue("CARD_NO")
				|| readParm.getValue("CARD_NO").length() <= 0) {
			return null;
		}
		readParm.setData("RETURN_TYPE", 1);// 返回执行状态
		readParm.setData("PASSWORD", INSPASSWORD);
		int insType = 0;
		if (parm.getData("SS_TYPE") == null) {
			return null;
		}
		String ssType = (String) parm.getData("SS_TYPE");
		TParm testParm = new TParm();
		if ("0".equals(ssType)) {
			insType = 1;
			testParm = INSTJFlow.getInstance().insIdentificationChZPt(readParm);
			String ctzCode = testParm.getValue("CTZ_CODE");
			TParm ctzParm = sysCtzParm(1, ctzCode);
			if (ctzParm.getErrCode() < 0) {
				return null;
			}
			readParm.setData("CTZ_CODE", ctzParm.getValue("CTZ_CODE", 0));// 人员类别
			if (testParm.getErrCode() < 0) {
				readParm.setData("RETURN_TYPE", 0);// 返回执行状态
				return null;
			}
		}
		if (null == testParm || null == testParm.getValue("CONFIRM_NO")) {
			return null;
		}
		// testParm.setData("CTZ1_CODE", testParm.getValue("CTZ_CODE"));
		testParm.setData("BED_FEE", regionParm.getValue("TOP_BEDFEE", 0));// 床位费
		testParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));// 医保区域代码
		readParm.setData("opbReadCardParm", testParm.getData());// 资格确认书出参
		readParm.setData("CONFIRM_NO", testParm.getValue("CONFIRM_NO"));// 门特就医顺序号
		readParm.setData("INS_TYPE", insType);
		readParm.setData("RETURN_TYPE", 1);// 返回数据 1.成功 2.失败
		readParm.setData("INS_PAT_TYPE", 1);// 1.普通
		readParm.setData("DISEASE_CODE", "");// 门特病种
		// 2.门特
		readParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));// 医保区域代码

		return readParm;
	}

	private TParm sysCtzParm(int type, String ctzCode) {
		String ctzSql = "SELECT CTZ_CODE FROM SYS_CTZ WHERE NHI_NO='" + ctzCode
				+ "' AND NHI_CTZ_FLG='Y'";
		if (type == 1) {
			ctzSql += " AND CTZ_CODE IN('11','12','13')";
		}
		if (type == 2) {
			ctzSql += " AND CTZ_CODE IN('21','22','23')";
		}
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(ctzSql));
		if (ctzParm.getErrCode() < 0) {
			return ctzParm;
		}
		return ctzParm;
	}

	/***
	 * 执行医保前操作
	 * 
	 * @param parm
	 * @return
	 */
	public TParm saveCardBefore(TParm parm, TParm returnParm) {
		TParm tparm = new TParm();
		tparm = PatAdmTool.getInstance().selEKTByMrNo(parm);

		if (tparm.getErrCode() < 0) {
			return null;
		}

		if (tparm.getDouble("GREEN_BALANCE", 0) > 0) {
			return null;
		}
		String clinictypeCode = (String) parm.getData("CLINICTYPE_CODE");
		String regFeesql = "SELECT A.ORDER_CODE,B.ORDER_DESC,B.NHI_CODE_O, "
				+ "B.NHI_CODE_E, B.NHI_CODE_I,B.OWN_PRICE ,"
				+ "B.OWN_PRICE AS AR_AMT ,'1' AS DOSAGE_QTY, "
				+ "'0' AS TAKE_DAYS, '' AS NS_NOTE, '' AS SPECIFICATION,'' AS DR_CODE,A.RECEIPT_TYPE,"
				+ "C.DOSE_CODE FROM REG_CLINICTYPE_FEE A,SYS_FEE B,PHA_BASE C WHERE A.ORDER_CODE=B.ORDER_CODE(+) "
				+ "AND A.ORDER_CODE=C.ORDER_CODE(+) AND  A.ADM_TYPE='O'"
				+ " AND A.CLINICTYPE_CODE='" + clinictypeCode + "'";
		// 挂号费
		double reg_fee = parm.getDouble("REG_FEE");
		// 诊查费 计算折扣
		double clinic_fee = parm.getDouble("CLINIC_FEE");
		TParm regFeeParm = new TParm(TJDODBTool.getInstance().select(regFeesql));
		if (regFeeParm.getErrCode() < 0) {
			err(regFeeParm.getErrCode() + " " + regFeeParm.getErrText());
			return null;
		}
		for (int i = 0; i < regFeeParm.getCount(); i++) {
			if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("REG_FEE")) {
				regFeeParm.setData("RECEIPT_TYPE", i, reg_fee);
				regFeeParm.setData("AR_AMT", i, reg_fee);
			}
			if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("CLINIC_FEE")) {
				regFeeParm.setData("RECEIPT_TYPE", i, clinic_fee);
				regFeeParm.setData("AR_AMT", i, clinic_fee);
			}
		}
		returnParm.setData("REG_PARM", regFeeParm.getData()); // 医嘱信息
		returnParm.setData("DEPT_CODE", parm.getData("DEPT_CODE")); // 科室代码
		returnParm.setData("MR_NO", parm.getData("MR_NO")); // 病患号

		returnParm.setData("RECP_TYPE", "REG"); // 类型：REG / OPB
		returnParm.setData("CASE_NO", parm.getData("CASE_NO"));
		returnParm.setData("REG_TYPE", "1"); // 挂号标志:1 挂号0 非挂号
		returnParm.setData("OPT_USER", parm.getData("OPT_NAME"));
		if (parm.getData("OPT_NAME") == null) {
			returnParm.setData("OPT_USER", parm.getData("OPT_USER"));
		}
		returnParm.setData("OPT_TERM", parm.getData("OPT_IP"));
		if (parm.getData("OPT_IP") == null) {
			returnParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
		}
		returnParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
		returnParm.setData("DR_CODE", parm.getData("DR_CODE"));// 医生代码
		returnParm.setData("EREG_FLG", "0"); // 普通
		returnParm.setData("PRINT_NO", PRINTNO); // 票号
		returnParm.setData("QUE_NO", parm.getData("QUE_NO"));
		returnParm.setData("FeeY", reg_fee + clinic_fee);
		return returnParm;
	}

	/**
	 * 医保卡执行费用显示操作 flg 是否执行退挂 false： 执行退挂 true： 正流程操作
	 * 
	 * @param flg
	 *            boolean
	 * @return TParm
	 */
	private TParm insExeFee(boolean flg, TParm insParm, TParm parm) {
		TParm insFeeParm = new TParm();
		if (flg) {
			insFeeParm.setData("insParm", insParm.getData()); // 医嘱信息
			insFeeParm.setData("INS_TYPE", insParm.getValue("INS_TYPE")); // 医保就医类别
		} else {
			insFeeParm.setData("INS_TYPE", insParm.getData("INS_TYPE")); // 退挂使用
			insFeeParm.setData("RECP_TYPE", "REG"); // 退挂使用
		}
		insFeeParm.setData("CONFIRM_NO", insParm.getData("CONFIRM_NO"));
		insFeeParm.setData("CASE_NO", insParm.getData("CASE_NO"));
		insFeeParm.setData("NAME", parm.getData("PAT_NAME"));
		insFeeParm.setData("MR_NO", parm.getData("MR_NO")); // 病患号

		insFeeParm.setData("FeeY", insParm.getData("FeeY")); // 应收金额
		insFeeParm.setData("PAY_TYPE", false); // 支付方式
		TParm regionParm = new TParm();// 获得医保区域代码
		regionParm = SYSRegionTool.getInstance().selectdata("H01");// 获得医保区域代码
		insFeeParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0)); // 区域代码
		insFeeParm.setData("FEE_FLG", flg); // 判断此次操作是执行退费还是收费 ：true 收费 false 退费
		TParm returnParm = new TParm();
		if (flg) { // 正流程
			returnParm = this.saveCard(flg, insFeeParm);
			if (returnParm == null
					|| null == returnParm.getValue("RETURN_TYPE")) {
				// || returnParm.getInt("RETURN_TYPE") == 0) {
				return null;
			}
		} else {
			// 退费流程
			TParm returnIns = reSetExeFee(insFeeParm);
			if (null == returnIns) {
				return null;
			} else {
				double accountAmt = 0.00;// 医保金额
				if (returnIns.getValue("INS_CROWD_TYPE").equals("1")) {// 城职
					accountAmt = StringTool.round((returnIns.getDouble("TOT_AMT") - 
							returnIns.getDouble("UNACCOUNT_PAY_AMT")-
							returnIns.getDouble("UNREIM_AMT")), 2);
				} else if (returnIns.getValue("INS_CROWD_TYPE").equals("2")) {// 城居
					double payAmt = returnIns.getDouble("TOT_AMT")
							- returnIns.getDouble("TOTAL_AGENT_AMT")
							- returnIns.getDouble("FLG_AGENT_AMT")
							- returnIns.getDouble("ILLNESS_SUBSIDY_AMT")
							- returnIns.getDouble("ARMY_AI_AMT")
					        + returnIns.getDouble("UNREIM_AMT");// 现金金额
					accountAmt = StringTool.round((returnIns
							.getDouble("TOT_AMT") - payAmt), 2);
				}
				returnParm.setData("RETURN_TYPE", 1); // 执行成功
				returnParm.setData("ACCOUNT_AMT", accountAmt);// 医保金额
			}

		}
		return returnParm;
	}

	/***
	 * 医保卡费用分割
	 * 
	 * @param flag
	 * @param parm
	 * @return
	 */
	public TParm saveCard(boolean flag, TParm inparm) {
		boolean exeError = false;
		boolean exeSplit = false;
		TParm insParm = inparm.getParm("insParm");
		if (flag) {
			if (null == insParm
					|| insParm.getParm("REG_PARM").getCount("ORDER_CODE") <= 0) {
				return null;
			}
		}
		insParm.setData("NEW_REGION_CODE", "H01");// 区域代码
		insParm.setData("FeeY", inparm.getDouble("FeeY"));
		insParm.setData("PRINT_NO", PRINTNO); // 票据号
		TParm result = INSTJFlow.getInstance().comminuteFeeAndInsOrder(insParm);// 费用分割
		exeError = true;// 错误累计
		// 所有医嘱
		if (result.getErrCode() < 0) {
			exeSplit = false;
			return null;
		} else {
			if (null != result.getValue("MESSAGE")
					&& result.getValue("MESSAGE").length() > 0) {
				exeSplit = false;
			} else {
				exeSplit = true;// 执行费用分割操作
			}
		}
		// TParm settlementDetailsParm =
		// result.getParm("settlementDetailsParm");// 费用结算参数
		TParm parm = INSOpdTJTool.getInstance().queryForPrint(inparm);
		TParm accountParm = getAmt(inparm.getInt("INS_TYPE"), parm);
		return accountParm;
	}

	/**
	 * 获取金额
	 * 
	 * @param insType
	 * @param returnParm
	 * @return
	 */
	public TParm getAmt(int insType, TParm returnParm) {
		// 取得医保专项基金支付金额
		double sOTOT_Amt = 0.00;
		// 取得现金支付金额
		double sUnaccount_pay_amt = 0.00;

		if (insType == 1) {
			sOTOT_Amt = returnParm.getDouble("TOT_AMT", 0) - // 总金额
					returnParm.getDouble("UNACCOUNT_PAY_AMT", 0) - // 非账户支付
					returnParm.getDouble("UNREIM_AMT", 0);// 基金未报销
			sUnaccount_pay_amt = returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					+ returnParm.getDouble("UNREIM_AMT", 0);// 现金支付金额
		}
		// 城职门特
		if (insType == 2) {
			sOTOT_Amt = returnParm.getDouble("TOT_AMT", 0)
					- returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					- returnParm.getDouble("UNREIM_AMT", 0);
			sUnaccount_pay_amt = returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					+ returnParm.getDouble("UNREIM_AMT", 0);
		}
		// 城居门特
		if (insType == 3) {
			if (null != returnParm.getValue("REIM_TYPE", 0)
					&& returnParm.getInt("REIM_TYPE", 0) == 1) {
				sOTOT_Amt = returnParm.getDouble("TOTAL_AGENT_AMT", 0)
						+ returnParm.getDouble("ARMY_AI_AMT", 0)
						+ returnParm.getDouble("FLG_AGENT_AMT", 0)
						+ returnParm.getDouble("ILLNESS_SUBSIDY_AMT", 0)//城乡大病金额
						- returnParm.getDouble("UNREIM_AMT", 0);

			} else {
				sOTOT_Amt = returnParm.getDouble("TOTAL_AGENT_AMT", 0)
						+ returnParm.getDouble("FLG_AGENT_AMT", 0)
						+ returnParm.getDouble("ARMY_AI_AMT", 0)
				        + returnParm.getDouble("ILLNESS_SUBSIDY_AMT", 0);//城乡大病金额

			}

			// 现金支付
			sUnaccount_pay_amt = returnParm.getDouble("TOT_AMT", 0)
					- returnParm.getDouble("TOTAL_AGENT_AMT", 0)
					- returnParm.getDouble("FLG_AGENT_AMT", 0)
					- returnParm.getDouble("ARMY_AI_AMT", 0)
					- returnParm.getDouble("ILLNESS_SUBSIDY_AMT", 0)//城乡大病金额
					+ returnParm.getDouble("UNREIM_AMT", 0);
		}
		TParm parm = new TParm();
		parm.setData("ACCOUNT_AMT", sOTOT_Amt);
		parm.setData("UACCOUNT_AMT", sUnaccount_pay_amt);
		return parm;
	}

	/**
	 * 医保退挂操作
	 * 
	 * @param invNo
	 *            String
	 * @return boolean
	 */
	private boolean reSetInsSave(String invNo, TParm tparm) {
		TParm reSetInsParm = new TParm();
		TParm regionParm = null;// 获得医保区域代码
		regionParm = SYSRegionTool.getInstance().selectdata("H01");// 获得医保区域代码
		if (null != tparm.getData("CONFIRM_NO")
				&& ((String) tparm.getData("CONFIRM_NO")).length() > 0) {
			tparm.setData("INV_NO", invNo);
			tparm.setData("RECP_TYPE", "REG");
			TParm parm = insExeFee(false, null, tparm);
			int returnType = parm.getInt("RETURN_TYPE");
			if (returnType == 0 || returnType == -1) { // 取消
				return false;
			}
			reSetInsParm.setData("CASE_NO", (String) tparm.getData("CASE_NO")); // 就诊号
			reSetInsParm.setData("CONFIRM_NO", (String) tparm
					.getData("CONFIRM_NO")); // 医保就诊号
			reSetInsParm.setData("INS_TYPE", tparm.getData("INS_TYPE")); // 医保就诊号
			reSetInsParm.setData("RECP_TYPE", "REG"); // 收费类型
			reSetInsParm.setData("UNRECP_TYPE", "REGT"); // 退费类型
			reSetInsParm.setData("OPT_USER", Operator.getID()); // id
			reSetInsParm.setData("OPT_TERM", Operator.getIP()); // ip
			reSetInsParm.setData("REGION_CODE", regionParm
					.getValue("NHI_NO", 0)); // 医保区域代码
			reSetInsParm
					.setData("PAT_TYPE", (String) tparm.getData("REG_CTZ1")); // 身份
			reSetInsParm.setData("INV_NO", invNo); // 票据号
			TParm result = INSTJReg.getInstance().insResetCommFunction(
					reSetInsParm.getData());
			if (result.getErrCode() < 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 医保执行退费操作
	 * 
	 * @param parm
	 *            TParm
	 * @return double
	 */
	public TParm reSetExeFee(TParm parm) {
		TParm result = INSTJFlow.getInstance().selectResetFee(parm);
		if (result.getErrCode() < 0) {
			return null;
		}
		return result;

	}

	/**
	 * 当日报道
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm confirmReg(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		parm.setData("CASE_NO", (String) parm.getData("CASE_NO"));
		parm.setData("CCB_PERSON_NO", (String) parm.getData("PERSON_NO"));
		TParm result1 = query("getPatInfoByPersonNo", parm);
		if (result1.getErrCode() < 0 || result1.getCount() <= 0) {
			err(result1.getErrCode() + " " + result1.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "无此人病案信息");
			return returnParm;
		}
		TParm result = query("selectConfirmReg", parm);
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err(result.getErrName() + " " + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "无此预约信息");
			return returnParm;
		}
		parm.setData("OPT_USER", parm.getData("OPT_NAME"));
		Long seq = (Long) result.getData("QUE_NO", 0);
		String mrNo = (String) result.getData("MR_NO", 0);
		parm.setData("ADM_TYPE", "O");
		String admDate = result.getData("ADM_DATE", 0).toString();
		parm.setData("ADM_DATE", admDate.replace("-", ""));
		parm.setData("DR_CODE", result.getData("DR_CODE", 0));
		String caseNo = (String) parm.getData("CASE_NO");
		parm.setData("CLINICTYPE_CODE", result.getData("CLINICTYPE_CODE", 0));
		String date = SystemTool.getInstance().getDate().toString();
		date = date.substring(0, 10);
		if (!date.equals(admDate)) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "非当日,不能报道");
			return returnParm;
		}
		double reg_fee = BIL.getRegDetialFee("O", (String) parm
				.getData("CLINICTYPE_CODE"), "REG_FEE", "99", "", "", "");
		parm.setData("REG_FEE", reg_fee);
		// 诊查费
		double clinic_fee = BIL.getRegDetialFee("O", (String) parm
				.getData("CLINICTYPE_CODE"), "CLINIC_FEE", "99", "", "", "");
		parm.setData("CLINIC_FEE", clinic_fee);
		parm.setData("AMT", reg_fee + clinic_fee);
		parm.setData("MR_NO", result.getData("MR_NO", 0));
		parm.setData("SESSION_CODE", result.getData("SESSION_CODE", 0));
		result = this.getRegAchDay(parm);
		if (result.getErrCode() != 0 || result == null) {
			err("ERR:getRegAchDay" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "获取班表信息失败");
			return returnParm;
		}
		// returnParm.setData("SEQ", result.getData("QUE_NO", 0));
		String region_code = (String) result.getData("REGION_CODE", 0);
		parm.setData("REGION_CODE", region_code);
		TParm insParm = new TParm();
		TParm insFeeParm = new TParm();
		// 走医保
		if (parm.getData("SSCARD_NO") != null
				&& parm.getData("SSCARD_NO").toString().length() > 0) {
			parm.setData("TEXT", ";"+(String) parm.getData("SSCARD_NO")+"=");
			parm.setData("SS_TYPE", "0");
			insParm = this.readCard(parm);
			String crowdType = (String) insParm.getData("CROWD_TYPE");
			if (!CZ.equals(crowdType)) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "执行医保失败，暂只支持城职");
				return returnParm;
			}
			parm.setData("CTZ1_CODE", insParm.getData("CTZ_CODE"));
			// 医保确认书编号
			String confirmNo = (String) insParm.getData("CONFIRM_NO");
			parm.setData("CONFIRM_NO", confirmNo);
			parm.setData("NHI_NO",(String) parm.getData("SSCARD_NO"));
			if (null == insParm || insParm.getErrCode() < 0
					|| null == insParm.getValue("CARD_NO")
					|| insParm.getValue("CARD_NO").length() <= 0) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "医保卡读取失败");
				return returnParm;
			}
			insFeeParm = this.saveCardBefore(parm, insParm);
			if (insFeeParm.getErrCode() != 0) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "医保卡读取失败");
				return returnParm;
			}
			// 医保分割
			result = this.insExeFee(true, insFeeParm, parm);
			if (result.getErrCode() != 0 || result == null) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "医保分割失败");
				return returnParm;
			}
			// 报道医保部分更新医保号，医保确认号
			String sql = "UPDATE REG_PATADM SET ";
			String nhiNo = (String) parm.getData("NHI_NO");
			if (parm.getData("SSCARD_NO") != null
					&& parm.getData("SSCARD_NO").toString().length() > 0) {
				sql = sql + "CONFIRM_NO='" + confirmNo + "',NHI_NO='" + nhiNo
						+ "'";
			}
			if (parm.getData("CTZ1_CODE") != null
					&& parm.getData("CTZ1_CODE").toString().length() > 0) {
				sql = sql + ",CTZ1_CODE='" + (String) parm.getData("CTZ1_CODE")
						+ "'";
			}
			sql = sql + " WHERE CASE_NO='" + caseNo + "'";
			TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql,
					connection));
			if (updateParm.getErrCode() < 0) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "更新挂号主档失败");
				return returnParm;
			}
			returnParm
					.setData("PER_PAY_AMOUNT", result.getData("UACCOUNT_AMT"));
			returnParm.setData("S_ACCOUNT_AMOUNT", result
					.getData("ACCOUNT_AMT"));
			returnParm.setData("S_GOC_AMOUNT", result.getData("S_GOC_AMOUNT"));
		} else {
			// 非医保自费部分
//			TParm bilParm = new TParm();
//			String receiptNo =SystemTool.getInstance().getNo("ALL",
//					"BIL", "CCB_RECEIPT_NO", "CCB_RECEIPT_NO");
//			bilParm.setData("RECEIPT_NO", receiptNo);
//			bilParm.setData("CASE_NO", caseNo);
//			bilParm.setData("ADM_TYPE", "O");
//			bilParm.setData("REGION_CODE", region_code);
//			bilParm.setData("MR_NO", mrNo);
//			bilParm.setData("PRINT_NO", SystemTool.getInstance().getNo("ALL",
//					"BIL", "CCB_INV_NO", "CCB_INV_NO"));
//			bilParm.setData("REG_FEE", reg_fee);
//			bilParm.setData("CLINIC_FEE", clinic_fee);
//			bilParm.setData("REG_FEE_REAL", reg_fee);
//			bilParm.setData("CLINIC_FEE_REAL", clinic_fee);
//			bilParm.setData("AR_AMT", reg_fee + clinic_fee);
//			bilParm.setData("PAY_INS_CARD", 0);
//			bilParm.setData("OTHER_FEE2", reg_fee + clinic_fee);
//			bilParm.setData("OPT_USER", parm.getData("OPT_NAME"));
//			bilParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
//			bilParm.setData("OPT_TERM", parm.getData("OPT_IP"));
//			bilParm.setData("CASHIER_CODE", parm.getData("OPT_NAME"));
//			bilParm.setData("CASH_CODE", parm.getData("OPT_NAME"));
//			bilParm.setData("PRINT_DATE", parm.getData("OPT_DATE"));
//			bilParm.setData("PRINT_USER", parm.getData("OPT_NAME"));
//			//扣费所需数据
//			String patName = (String)result1.getData("PAT_NAME", 0);
//			parm.setData("CLINIC_FEE", clinic_fee);
//			parm.setData("CASE_NO", caseNo);
//			parm.setData("MR_NO", mrNo);
//			parm.setData("BUSINESS_NO", (String) parm.getData("STREAM_NO"));
//			parm.setData("PAT_NAME", patName);
//			parm.setData("OPT_USER", (String) parm.getData("OPT_NAME"));
//			parm.setData("OPT_TERM", parm.getData("OPT_IP"));
//			// 预约报道非医保直接打票
//			result = this.insertBilRegRecp(bilParm, connection);
//			if (result.getErrCode() != 0) {
//				err("ERR:REG.insertBilRegRecp " + result.getErrCode()
//						+ result.getErrText());
//				returnParm.setData("EXECUTE_FLAG", ISERROR);
//				returnParm.setData("EXECUTE_MESSAGE", "添加票据档失败");
//				return returnParm;
//			}
//			result = this.insertInvrcp(bilParm, connection);
//			if (result.getErrCode() != 0) {
//				err("ERR:REG.insertInvrcp " + result.getErrCode()
//						+ result.getErrText());
//				returnParm.setData("EXECUTE_FLAG", ISERROR);
//				returnParm.setData("EXECUTE_MESSAGE", "添加挂号主表失败");
//				return returnParm;
//			}
//			//自费患者报到时，进行扣费操作，记录EKT_CCB_TRADE表数据
//			// 添加交易表不涉及医保
//			parm.setData("AMT", reg_fee + clinic_fee);
//			parm.setData("TRADE_NO", SystemTool.getInstance().getNo("ALL",
//				"EKT", "CCB_TRADE_NO", "CCB_TRADE_NO"));
//			parm.setData("STATE", "1");
//			parm.setData("BUSINESS_TYPE", "REG");
//			parm.setData("RECEIPT_NO", receiptNo);
//			result = this.insertCcbTrade(parm);
//			if (result.getErrCode() < 0) {
//				err("ERR: " + result.getErrCode() + result.getErrText());
//				returnParm.setData("EXECUTE_FLAG", ISERROR);
//				returnParm.setData("EXECUTE_MESSAGE", "添加交易信息失败");
//				return returnParm;
//			}
//			// 预约报道非医保直接修改报道状态
//			String sql = "UPDATE REG_PATADM SET ARRIVE_FLG='Y' WHERE CASE_NO='"
//					+ caseNo + "'";
//			TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql,
//					connection));
//			if (updateParm.getErrCode() < 0) {
//				returnParm.setData("EXECUTE_FLAG", ISERROR);
//				returnParm.setData("EXECUTE_MESSAGE", "更新挂号主档失败");
//				return returnParm;
//			}
//			callNo("REG", caseNo);
			returnParm.setData("PER_PAY_AMOUNT", reg_fee + clinic_fee);
			returnParm.setData("S_ACCOUNT_AMOUNT", 0);
			returnParm.setData("S_GOC_AMOUNT", 0);
			returnParm.setData("BANK_STREAM_NO", "");
			returnParm.setData("BANK_RET_STREAM_NO", ""); 
//			returnParm.setData("PAY_SEQ", receiptNo);
			returnParm.setData("TRADE_STATUS", "0");
		}
		returnParm.setData("SEQ", seq);
		returnParm.setData("PATIENT_ID", mrNo);
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "报道成功");
		return returnParm;
	}

	/**
	 * 预约挂号交易确认 类型：1
	 * 
	 * @param parm
	 * @return
	 */
	public TParm confirmTranYy(TParm parm) {
		TParm returnParm = new TParm();
		if (parm.getData("TRAN_NO") == null
				|| parm.getData("TRAN_NO").toString().length() <= 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
		}
//		String sql = "SELECT CASE_NO FROM EKT_CCB_TRADE WHERE CASE_NO='" + caseNo
//				+ "'";
//		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		if (result.getErrCode() < 0 || result.getCount() <= 0) {
//			returnParm.setData("EXECUTE_FLAG", ISERROR);
//			returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
//			return returnParm;
//		}
//		if (result.getData("CASE_NO", 0) == null
//				|| result.getData("CASE_NO", 0).toString().length() <= 0) {
//			returnParm.setData("EXECUTE_FLAG", ISERROR);
//			returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
//		}
		String caseNo = (String)parm.getData("TRAN_NO");
		String sql = "SELECT CASE_NO FROM REG_PATADM WHERE CASE_NO='" + caseNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
			return returnParm;
		}
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "交易成功");
		return returnParm;
	}

	/**
	 * 自费报道交易确认 类型：2
	 * 
	 * @param parm
	 * @return
	 */
	public TParm confirmBd(TParm parm) {
		TParm returnParm = new TParm();
		if (parm.getData("TRAN_NO") == null
				|| parm.getData("TRAN_NO").toString().length() <= 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
		}
		String caseNo = (String) parm.getData("TRAN_NO");
		String sql = "SELECT CASE_NO FROM EKT_CCB_TRADE WHERE CASE_NO='" + caseNo
				+ "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
			return returnParm;
		}
		if (result.getData("CASE_NO", 0) == null
				|| result.getData("CASE_NO", 0).toString().length() <= 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
		}
		sql = "SELECT CASE_NO FROM REG_PATADM WHERE CASE_NO='" + caseNo
				+ "' AND ARRIVE_FLG='Y'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
			return returnParm;
		}
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "交易成功");
		return returnParm;
	}
	/**
	 * 当日挂号交易确认 类型：3
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm confirmTran(TParm parm, TConnection connection) {
		TParm insParm = null;
		TParm returnParm = new TParm();
		TParm bilParm = new TParm();
		String caseNo = (String) parm.getData("TRAN_NO");
		String tranType = (String) parm.getData("TRAN_TYPE");
		String streamNo = (String) parm.getData("STREAM_NO");
		parm.setData("CASE_NO", caseNo);
		// 查询挂号主档信息
		TParm result = query("selectTradeByCaseno", parm);
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
			return returnParm;
		}
		// 挂号费
		double reg_fee = BIL.getRegDetialFee("O", (String) result.getData(
				"CLINICTYPE_CODE", 0), "REG_FEE", "99", "", "", "");
		parm.setData("REG_FEE", reg_fee);
		// 诊查费
		String receiptNo = SystemTool.getInstance().getNo("ALL", "BIL",
				"CCB_RECEIPT_NO", "CCB_RECEIPT_NO");
		double clinic_fee = BIL.getRegDetialFee("O", (String) result.getData(
				"CLINICTYPE_CODE", 0), "CLINIC_FEE", "99", "", "", "");
		bilParm.setData("RECEIPT_NO", receiptNo);
		bilParm.setData("CASE_NO", caseNo);
		bilParm.setData("ADM_TYPE", "O");
		bilParm.setData("REGION_CODE", (String) result
				.getData("REGION_CODE", 0));
		bilParm.setData("MR_NO", (String) result.getData("MR_NO", 0));
		String invNo = SystemTool.getInstance().getNo("ALL", "BIL",
				"CCB_INV_NO", "CCB_INV_NO");
		bilParm.setData("PRINT_NO", invNo);
		bilParm.setData("REG_FEE", reg_fee);
		bilParm.setData("CLINIC_FEE", clinic_fee);
		bilParm.setData("AR_AMT", reg_fee + clinic_fee);
		bilParm.setData("OPT_USER", parm.getData("OPT_NAME"));
		bilParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
		bilParm.setData("OPT_TERM", parm.getData("OPT_IP"));
		bilParm.setData("CASHIER_CODE", parm.getData("OPT_NAME"));
		bilParm.setData("CASH_CODE", parm.getData("OPT_NAME"));
		bilParm.setData("REG_FEE_REAL", reg_fee);
		bilParm.setData("CLINIC_FEE_REAL", clinic_fee);
		bilParm.setData("PRINT_DATE", parm.getData("OPT_DATE"));
		bilParm.setData("PRINT_USER", parm.getData("OPT_NAME"));

		parm.setData("CLINIC_FEE", clinic_fee);
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", result.getData("MR_NO", 0));
		parm.setData("BUSINESS_NO", (String) parm.getData("STREAM_NO"));
		parm.setData("PAT_NAME", result.getData("PAT_NAME", 0));
		parm.setData("BUSINESS_TYPE", "REG");
		parm.setData("OPT_USER", (String) parm.getData("OPT_NAME"));
		parm.setData("OPT_TERM", parm.getData("OPT_IP"));
		parm.setData("BANK_FLG", "Y");

		String confirmNo = (String) result.getData("CONFIRM_NO", 0);
		// 判断是否走医保
		if (confirmNo != null && !"".equals(confirmNo)) {
			TParm regionParm = null;// 获得医保区域代码
			regionParm = SYSRegionTool.getInstance().selectdata("H01");// 获得医保区域代码
			parm.setData("STATE", "9");
			parm.setData("CONFIRM_NO", confirmNo);
			String regionCode = (String) result.getData("REGION_CODE", 0);
			insParm = new TParm();
			insParm.setData("CONFIRM_NO", confirmNo);
			insParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));
			insParm.setData("INS_TYPE", "1");
			insParm.setData("CASE_NO", caseNo);
			insParm.setData("OPT_USER", (String) parm.getData("OPT_NAME"));
			insParm.setData("OPT_TERM", parm.getData("OPT_IP"));
			insParm.setData("RECP_TYPE", "REG");
			// 获取医保数据
			result = query("getInsOpd", parm);
			if (result.getErrCode() < 0 || result.getCount() <= 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "医保支付失败");
				return returnParm;
			}
			double insPay = result.getDouble("INS_TOT_AMT", 0)
					- result.getDouble("UNACCOUNT_PAY_AMT", 0);
			double personPay = result.getDouble("UNACCOUNT_PAY_AMT", 0);
			parm.setData("AMT", -insPay);
			bilParm.setData("PAY_INS_CARD", insPay);
			bilParm.setData("OTHER_FEE2", personPay);
			TParm opbReadCardParm = new TParm();		
			opbReadCardParm.setData("TOT_AMT", result.getDouble("TOT_AMT", 0));
			TParm settlementDetailsParm = new TParm();
			settlementDetailsParm.setData("INS_PAY_AMT", result.getDouble(
					"INS_PAY_AMT", 0));
			settlementDetailsParm.setData("UNREIM_AMT", result.getDouble(
					"UNREIM_AMT", 0));
			settlementDetailsParm.setData("OINSTOT_AMT", result.getDouble(
					"OINSTOT_AMT", 0));
			settlementDetailsParm.setData("OWN_AMT", result.getDouble(
					"OWN_AMT", 0));
			insParm.setData("opbReadCardParm", opbReadCardParm.getData());
			insParm.setData("settlementDetailsParm", settlementDetailsParm
					.getData());
			result = INSTJReg.getInstance().insCommFunction(insParm.getData());
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + "" + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "医保支付失败");
				return returnParm;
			}
			String sql = "UPDATE INS_OPD SET INV_NO='" + invNo
					+ "' WHERE CONFIRM_NO='" + confirmNo + "'";
			// 添加交易表走医保部分
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
				return returnParm;
			}
			sql = "UPDATE INS_OPD_ORDER SET INV_NO='" + invNo
					+ "' WHERE CONFIRM_NO='" + confirmNo + "'";
			// 添加交易表走医保部分
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				err("ERR:getRegAchDay" + result.getErrCode()
						+ result.getErrText() + result.getErrName());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
				return returnParm;
			}
			parm.setData("RECEIPT_NO", receiptNo);
			parm.setData("TRADE_NO", parm.getData("tradeNOINS"));
			parm.setData("BUSINESS_TYPE", "REG");
			result = this.insertCcbTrade(parm);
			if (result.getErrCode() < 0) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "添加交易信息失败");
				return returnParm;
			}
		} else {
			// 不走医保，医保支付为0,建行支付所有费用
			bilParm.setData("PAY_INS_CARD", 0);
			bilParm.setData("OTHER_FEE2", reg_fee + clinic_fee);
			parm.setData("RECEIPT_NO", receiptNo);
		}
		result = this.insertBilRegRecp(bilParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:REG.insertBilRegRecp " + result.getErrCode()
					+ result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "添加票据档失败");
			return returnParm;
		}
		result = this.insertInvrcp(bilParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:REG.insertInvrcp " + result.getErrCode()
					+ result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "添加票据主档失败");
			return returnParm;
		}
		// 添加交易表不涉及医保
		parm.setData("AMT", reg_fee + clinic_fee);
		parm.setData("TRADE_NO", parm.getData("tradeNOCCB"));
		parm.setData("STATE", "1");
		parm.setData("BUSINESS_TYPE", "REG");
		result = this.insertCcbTrade(parm);
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "添加交易信息失败");
			return returnParm;
		}
		// 交易确认修改挂号主档为报道状态
		String sql = "UPDATE REG_PATADM SET ARRIVE_FLG='Y' WHERE CASE_NO='"
				+ caseNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
			return returnParm;
		}
		callNo("REG", caseNo);
		returnParm.setData("BANK_STREAM_NO", "");
		returnParm.setData("BANK_RET_STREAM_NO", "");
		returnParm.setData("PAY_SEQ", receiptNo);
		returnParm.setData("TRADE_STATUS", "0");
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "交易成功");
		return returnParm;
	}

	/**
	 * 报道交易确认 类型：5
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm confirmRegTran(TParm parm, TConnection connection) {
		TParm insParm = null;
		TParm returnParm = new TParm();
		TParm bilParm = new TParm();
		String caseNo = (String) parm.getData("TRAN_NO");
		String tranType = (String) parm.getData("TRAN_TYPE");
		String streamNo = (String) parm.getData("STREAM_NO");
		String resetStreamNo = (String) parm.getData("RET_STREAM_NO");
		parm.setData("CASE_NO", caseNo);	
		// 查询挂号主档信息
		TParm result = query("selectTradeByCaseno", parm);
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
			return returnParm;
		}
		//用于判断是否是医保
		String confirmNo = (String) result.getData("CONFIRM_NO", 0);
		if (confirmNo != null && !"".equals(confirmNo)) {
			if ("".equals(streamNo)){
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
				return returnParm;
			}
		}
		// 挂号费
		double reg_fee = BIL.getRegDetialFee("O", (String) result.getData(
				"CLINICTYPE_CODE", 0), "REG_FEE", "99", "", "", "");
		parm.setData("REG_FEE", reg_fee);
		// 诊查费
		double clinic_fee = BIL.getRegDetialFee("O", (String) result.getData(
				"CLINICTYPE_CODE", 0), "CLINIC_FEE", "99", "", "", "");
		String receiptNo = SystemTool.getInstance().getNo("ALL", "BIL",
				"CCB_RECEIPT_NO", "CCB_RECEIPT_NO");
		bilParm.setData("RECEIPT_NO", receiptNo);
		bilParm.setData("CASE_NO", caseNo);
		bilParm.setData("ADM_TYPE", "O");
		bilParm.setData("REGION_CODE", (String) result
				.getData("REGION_CODE", 0));
		bilParm.setData("MR_NO", (String) result.getData("MR_NO", 0));
		String invNo = SystemTool.getInstance().getNo("ALL", "BIL",
				"CCB_INV_NO", "CCB_INV_NO");
		bilParm.setData("PRINT_NO", invNo);
		bilParm.setData("REG_FEE", reg_fee);
		bilParm.setData("CLINIC_FEE", clinic_fee);
		bilParm.setData("AR_AMT", reg_fee + clinic_fee);
		bilParm.setData("OPT_USER", parm.getData("OPT_NAME"));
		bilParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
		bilParm.setData("OPT_TERM", parm.getData("OPT_IP"));
		bilParm.setData("CASHIER_CODE", parm.getData("OPT_NAME"));
		bilParm.setData("CASH_CODE", parm.getData("OPT_NAME"));
		bilParm.setData("REG_FEE_REAL", reg_fee);
		bilParm.setData("CLINIC_FEE_REAL", clinic_fee);
		bilParm.setData("PRINT_DATE", parm.getData("OPT_DATE"));
		bilParm.setData("PRINT_USER", parm.getData("OPT_NAME"));

		parm.setData("CLINIC_FEE", clinic_fee);
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", result.getData("MR_NO", 0));
		parm.setData("BUSINESS_NO", streamNo);
		parm.setData("PAT_NAME", result.getData("PAT_NAME", 0));
		parm.setData("BUSINESS_TYPE", "REG");
		parm.setData("OPT_USER", (String) parm.getData("OPT_NAME"));
		parm.setData("OPT_DATE", parm.getData("OPT_DATE"));
		parm.setData("OPT_TERM", parm.getData("OPT_IP"));
		parm.setData("BANK_FLG", "Y");
		parm.setData("RECEIPT_NO", receiptNo);
		// 判断是否走医保
		if (confirmNo != null && !"".equals(confirmNo)) {
			TParm regionParm = null;// 获得医保区域代码
			regionParm = SYSRegionTool.getInstance().selectdata("H01");// 获得医保区域代码
			parm.setData("STATE", "9");
			parm.setData("CONFIRM_NO", confirmNo);
			String regionCode = (String) result.getData("REGION_CODE", 0);
			insParm = new TParm();
			insParm.setData("CONFIRM_NO", confirmNo);
			insParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));
			insParm.setData("INS_TYPE", "1");
			insParm.setData("CASE_NO", caseNo);
			insParm.setData("OPT_USER", (String) parm.getData("OPT_NAME"));
			insParm.setData("OPT_TERM", parm.getData("OPT_IP"));
			insParm.setData("RECP_TYPE", "REG");
			// 获取医保数据
			result = query("getInsOpd", parm);
			if (result.getErrCode() < 0 || result.getCount() <= 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "医保支付失败");
				return returnParm;
			}
			double insPay = result.getDouble("INS_TOT_AMT", 0)
					- result.getDouble("UNACCOUNT_PAY_AMT", 0);
			double personPay = result.getDouble("UNACCOUNT_PAY_AMT", 0);
			parm.setData("AMT", -insPay);
			bilParm.setData("PAY_INS_CARD", insPay);
			bilParm.setData("OTHER_FEE2", personPay);
			TParm opbReadCardParm = new TParm();
			opbReadCardParm.setData("TOT_AMT", result.getDouble("TOT_AMT", 0));
			TParm settlementDetailsParm = new TParm();
			settlementDetailsParm.setData("INS_PAY_AMT", result.getDouble(
					"INS_PAY_AMT", 0));
			settlementDetailsParm.setData("UNREIM_AMT", result.getDouble(
					"UNREIM_AMT", 0));
			settlementDetailsParm.setData("OINSTOT_AMT", result.getDouble(
					"OINSTOT_AMT", 0));
			settlementDetailsParm.setData("OWN_AMT", result.getDouble(
					"OWN_AMT", 0));
			insParm.setData("opbReadCardParm", opbReadCardParm.getData());
			insParm.setData("settlementDetailsParm", settlementDetailsParm
					.getData());
			result = INSTJReg.getInstance().insCommFunction(insParm.getData());
			if (result.getErrCode() < 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "医保支付失败");
				return returnParm;
			}
			String sql = "UPDATE INS_OPD SET INV_NO='" + invNo
					+ "' WHERE CONFIRM_NO='" + confirmNo + "'";
			// 添加交易表走医保部分
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
				return returnParm;
			}
			sql = "UPDATE INS_OPD_ORDER SET INV_NO='" + invNo
					+ "' WHERE CONFIRM_NO='" + confirmNo + "'";
			// 添加交易表走医保部分
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
				return returnParm;
			}
			// 添加交易表走医保部分
			parm.setData("TRADE_NO", parm.getData("tradeNOINS"));
			result = this.insertCcbTrade(parm);
			if (result.getErrCode() < 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "添加交易信息失败");
				return returnParm;
			}
		} else {
			// 不走医保，医保支付为0,建行支付所有费用
			bilParm.setData("PAY_INS_CARD", 0);
			bilParm.setData("OTHER_FEE2", reg_fee + clinic_fee);
		}
		result = this.insertBilRegRecp(bilParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:REG.insertBilRegRecp " + result.getErrCode()
					+ result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "添加票据档失败");
			return returnParm;
		}
		result = this.insertInvrcp(bilParm, connection);
		if (result.getErrCode() != 0) {
			err("ERR:REG.insertInvrcp " + result.getErrCode()
					+ result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "添加挂号主表失败");
			return returnParm;
		}
		// 交易确认修改挂号主档为报道状态
		String sql = "UPDATE REG_PATADM SET ARRIVE_FLG='Y' WHERE CASE_NO='"
				+ caseNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
			return returnParm;
		}
		parm.setData("AMT", (reg_fee + clinic_fee));
		parm.setData("BUSINESS_NO", streamNo);
		parm.setData("TRADE_NO", parm.getData("tradeNOCCB"));
		parm.setData("STATE", "1");
		parm.setData("BUSINESS_TYPE", "REG");
		result = this.insertCcbTrade(parm);
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "添加交易信息失败");
			return returnParm;
		}
		//由于预约挂号不扣费,无需回冲数据
		// 添加交易表不涉及医保
//		parm.setData("AMT", -(reg_fee + clinic_fee));
//		parm.setData("BUSINESS_NO", resetStreamNo);
//		parm.setData("TRADE_NO", parm.getData("tradeResetCCB"));
//		parm.setData("STATE", "2");
//		parm.setData("BUSINESS_TYPE", "REGT");
//		result = this.insertCcbTrade(parm);
//		if (result.getErrCode() < 0) {
//			err("ERR: " + result.getErrCode() + result.getErrText());
//			returnParm.setData("EXECUTE_FLAG", ISERROR);
//			returnParm.setData("EXECUTE_MESSAGE", "添加交易信息失败");
//			return returnParm;
//		}
		callNo("REG", caseNo);
		returnParm.setData("BANK_STREAM_NO", "");
		returnParm.setData("BANK_RET_STREAM_NO", "");
		returnParm.setData("PAY_SEQ", receiptNo);
		returnParm.setData("TRADE_STATUS", "0");
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "交易成功");
		return returnParm;
	}

	/**
	 * 取消删除医保数据
	 * 
	 * @param insParm
	 */
	public TParm deleteINSOpd(TParm insParm) {
		TParm result = INSOpdTJTool.getInstance().deleteINSOpd(insParm);// 删除数据
		if (result.getErrCode() < 0) {
			return result;
		}
		result = INSOpdOrderTJTool.getInstance().deleteSumINSOpdOrder(insParm);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}
	/**
	 * 缴费信息查询
	 * 
	 * @param tparm
	 * @return
	 */
	public TParm getCharge(TParm tparm, TConnection connection) {
		String queryType = (String) tparm.getData("QUERY_TYPE");
		String caseNo = (String) tparm.getData("TRAN_NO");
		TParm returnParm = new TParm();
		// 查询是否存在当日挂号信息
		String sql = "SELECT CASE_NO,MR_NO,ADM_DATE "
				+ "FROM REG_PATADM "
				+ "WHERE ADM_DATE=TO_DATE(TO_CHAR(SYSDATE,'yyyy-mm-dd'),'yyyy-mm-dd')"
				+ " AND CASE_NO='" + caseNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "无当日挂号信息");
			return returnParm;
		}
		// 查询缴费信息
		String opdSql = "SELECT B.MR_NO AS PATIENT_ID,A.CASE_NO AS TRAN_NO,B.PRESRT_NO AS CLINIC_COST_ITEM_NO,"
				+ "B.ORDER_CODE AS ITEM_CODE,B.ORDER_DESC AS ITEM_NAME,B.SPECIFICATION AS ITEM_SPEC,"
				+ "B.DOSAGE_QTY AS AMOUNT,B.DISPENSE_UNIT AS UNITS,B.OWN_PRICE AS PRICES,B.EXEC_DEPT_CODE AS PERFORMED_BY,"
				+ "(SELECT C.DEPT_CHN_DESC FROM SYS_DEPT C WHERE C.DEPT_CODE=B.DEPT_CODE ) AS ORDERED_BY,"
				+ "(SELECT D.USER_NAME FROM SYS_OPERATOR D WHERE D.USER_ID =B.DR_CODE ) AS ORDERED_DOCTOR,"
				+ "B.RECEIPT_NO AS PAY_SEQ,"
				+ "B.AR_AMT AS COSTS,B.OWN_AMT AS CHARGES,B.ORDER_CAT1_CODE AS REQ_CLASS "
				+ "FROM REG_PATADM A,OPD_ORDER B "
				+ "WHERE A.CASE_NO='"
				+ caseNo
				+ "' "
				+ " AND TO_CHAR(B.ORDER_DATE,'yyyy-MM-dd')=TO_CHAR(SYSDATE,'yyyy-MM-dd') "
				+ " AND A.CASE_NO=B.CASE_NO ";
		if ("2".equals(queryType)) { // 未缴费
			opdSql = opdSql + " AND B.BILL_FLG='N'";
		} else if ("1".equals("queryType")) {
			opdSql = opdSql + " AND B.BILL_FLG='Y'"; // 已缴费
		}
		result = new TParm(TJDODBTool.getInstance().select(opdSql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "无缴费信息");
			return returnParm;
		}
		returnParm.setData(result.getData());
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "查询成功");
		return returnParm;
	}

	// 保存缴费信息
	public TParm saveCharge(TParm tparm, TConnection connection) {

		TParm returnParm = new TParm();
		String caseNo = (String) tparm.getData("CASE_NO");
		String sql = "SELECT MR_NO,NHI_NO,REALDR_CODE FROM REG_PATADM WHERE CASE_NO='"
				+ caseNo + "' AND ARRIVE_FLG='Y'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "无当日挂号信息");
			return returnParm;
		}
		// String nhiNo = (String) result.getData("NHI_NO", 0);
		String nhiNo = (String) tparm.getData("SSCARD_NO");
		String mrNo = (String) result.getData("MR_NO", 0);
		String drCode = (String) result.getData("REALDR_CODE", 0);
		// 查询是否存在当日就诊信息
		sql = "SELECT SUM(AR_AMT) AS AMTCOUNT FROM OPD_ORDER "
				+ "WHERE CASE_NO='"
				+ caseNo
				+ "' AND BILL_FLG='N' AND RECEIPT_NO IS NULL AND RELEASE_FLG='N' "
				+ " AND TO_CHAR(ORDER_DATE,'yyyy-MM-dd')=TO_CHAR(SYSDATE,'yyyy-MM-dd') ";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getDouble("AMTCOUNT", 0) <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "无当日就诊信息");
			return returnParm;
		}
		Double amt = result.getDouble("AMTCOUNT", 0);
		// 走医保
		TParm insParm = new TParm();
		if (!"".equals(nhiNo) && nhiNo.length() > 0) {
			nhiNo = ";"+(String) tparm.getData("SSCARD_NO")+"=";
			TParm skParm = new TParm();
			skParm.setData("MR_NO", mrNo);
			skParm.setData("CASE_NO", caseNo);
			skParm.setData("INS_TYPE", "1");
			skParm.setData("CARD_TYPE", 3);
			skParm.setData("SS_TYPE", "0");
			skParm.setData("TEXT", nhiNo);
			// 读取医保卡
			insParm = this.readCard(skParm);
			String crowdType = (String) insParm.getData("CROWD_TYPE");
			if (!CZ.equals(crowdType)) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "执行医保失败，暂只支持城职");
				return returnParm;
			}
			if (null == insParm || insParm.getErrCode() < 0
					|| null == insParm.getValue("CARD_NO")
					|| insParm.getValue("CARD_NO").length() <= 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "医保卡读取失败");
				return returnParm;
			}
			Pat pat = Pat.onQueryByMrNo(mrNo);
			Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
			// CCBOPB opb = CCBOPB.onQueryByCaseNo(reg);
			OPB opb = new OPB();
			// TParm orderParm1 = OrderTool.getInstance().getOrder(caseNo);
			TParm selectParm = this.getOrder(caseNo);
//			 System.out.println("selectParm============"+selectParm);
			if (selectParm.getErrCode() < 0 || selectParm.getCount() <= 0) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "无当日挂号就诊信息");
				return returnParm;
			}
			TParm orderParm = opb.getInsCcbParm(true, selectParm);
//			 System.out.println("orderParm============"+orderParm);
			if (orderParm.getCount("ORDER_CODE") <= 0) {
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "没有要执行的数据");
				return returnParm;
			}
			insParm.setData("ID_NO", pat.getIdNo());
			insParm.setData("NAME", pat.getName());
			insParm.setData("REG_PARM", orderParm.getData()); // 所有要分割的医嘱
			insParm.setData("MR_NO", pat.getMrNo()); // 病患号
			insParm.setData("PAY_KIND", "11"); // 4 支付类别:11门诊、药店21住院//支付类别12
			insParm.setData("CASE_NO", reg.caseNo()); // 就诊号
			insParm.setData("RECP_TYPE", "OPB"); // 就诊类别			
			insParm.setData("OPT_USER", (String) tparm.getData("OPT_NAME")); // 区域代码
			// insParm.setData("DEPT_CODE", this.getValue("DEPT_CODE")); // 科室代码
			insParm.setData("REG_TYPE", "0"); // 挂号标志:1 挂号0 非挂号
			insParm.setData("OPT_TERM", (String) tparm.getData("OPT_IP"));
			insParm.setData("OPBEKTFEE_FLG", "Y"); // 门诊医疗卡收费注记----扣款打票时使用用来操作
//			System.out.println("insParm============"+insParm);
			// 收据表
			// BIL_OPB_RECP 医保金额修改
			// insParm.setData("PRINT_NO", this.getValue("UPDATE_NO")); // 票号
			// insParm.setData("DR_CODE", this.getValue("DR_CODE")); // 医生代码
			if (reg.getAdmType().equals("E")) {
				insParm.setData("EREG_FLG", "1"); // 急诊
			} else {
				insParm.setData("EREG_FLG", "0"); // 普通
			}
			// 费用分割传入参数
			TParm insFeeParm = new TParm();
			insParm.setData("RECP_TYPE", "OPB"); // 收费使用
			insParm.setData("CONFIRM_NO", insParm.getValue("CONFIRM_NO")); // 医保就诊号
			// insFeeParm.setData("insParm", insParm.getData());
			insParm.setData("INS_TYPE", insParm.getValue("INS_TYPE"));
			insParm.setData("FeeY", orderParm.getDouble("sumAmt")); // 未收费医嘱金额
			insParm.setData("parmSum", orderParm.getParm("parmSum").getData());// 所有的医嘱
			insParm.setData("billAmt", orderParm.getDouble("billAmt"));// 所有医嘱金额
			// 包括收费未收费
			insParm.setData("orderParm", orderParm.getData());// 需要收费的医嘱
			insParm.setData("ADM_TYPE", "O");
			insParm.setData("PRINT_NO", PRINTNO);// 票号暂时写死
			insParm.setData("DR_CODE",drCode);              
			// 分割金额										
			result = this.insExeFee(true, insParm, tparm);
			if (null == result || result.getErrCode() != 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "医保分割失败");
				return returnParm;
			}
			returnParm.setData("PER_PAY_AMOUNT", StringTool.round(result
					.getDouble("UACCOUNT_AMT"),2));
			returnParm.setData("S_ACCOUNT_AMOUNT", StringTool.round(result
					.getDouble("ACCOUNT_AMT"),2)); 
			returnParm
					.setData("S_GOC_AMOUNT", StringTool.round(result.getDouble("S_GOC_AMOUNT"),2));
		} else {
			returnParm.setData("PER_PAY_AMOUNT", amt);
			returnParm.setData("S_ACCOUNT_AMOUNT", 0);
			returnParm.setData("S_GOC_AMOUNT", 0); 
		}
		returnParm.setData("PATIENT_ID", mrNo);
		returnParm.setData("TRAN_NO", caseNo);
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "保存成功");
		return returnParm;
	}

	/**
	 * 门诊缴费交易确认 类型：4
	 * 
	 * @param tparm
	 * @param connection
	 * @return
	 */
	public TParm confirmTranOpb(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		TParm bilParm = new TParm();
		String caseNo = (String) parm.getData("TRAN_NO");
		String cardNo = (String) parm.getData("CARD_NO");
		String receiptNo = SystemTool.getInstance().getNo("ALL", "BIL",
				"CCB_RECEIPT_NO", "CCB_RECEIPT_NO");
		// 查询是否存在当日挂号信息
		String sql = "SELECT A.CASE_NO,A.MR_NO,A.ADM_DATE,A.NHI_NO,A.CONFIRM_NO,A.REGION_CODE "
				+ "FROM REG_PATADM A " + "WHERE" + " CASE_NO='" + caseNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "无当日挂号就诊信息");
			return returnParm;
		}
		parm.setData("MR_NO", (String) result.getData("MR_NO", 0));
		parm.setData("CASE_NO", (String) result.getData("CASE_NO", 0));
		parm.setData("BUSINESS_NO", parm.getData("STREAM_NO"));
		parm.setData("BUSINESS_TYPE", "OPB");
		parm.setData("BANK_FLG", "Y");
		parm.setData("OPT_USER", parm.getData("OPT_NAME"));
		parm.setData("OPT_DATE", parm.getData("OPT_DATE"));
		parm.setData("OPT_TERM", parm.getData("OPT_IP"));

		bilParm.setData("REGION_CODE", (String) result
				.getData("REGION_CODE", 0));
		bilParm.setData("MR_NO", (String) result.getData("MR_NO", 0));
		String mrNo = (String) result.getData("MR_NO", 0);
		sql = "SELECT PAT_NAME FROM SYS_PATINFO WHERE MR_NO='" + mrNo + "'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
			return returnParm;
		}
		String parName = (String) result.getData("PAT_NAME", 0);
		parm.setData("PAT_NAME", parName);
		sql = "SELECT CONFIRM_NO FROM INS_OPD WHERE CASE_NO='" + caseNo
				+ "' AND INSAMT_FLG='1' AND RECP_TYPE='OPB'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
			return returnParm;
		}
		String confirmNo = (String) result.getData("CONFIRM_NO", 0);

		// String receiptNo = (String) parm.getData("tradeNOCCB");
		// String receiptInsNO = (String) parm.getData("tradeNOINS");
		bilParm.setData("CASE_NO", caseNo);
		bilParm.setData("RECEIPT_NO", receiptNo);
		bilParm.setData("ADM_TYPE", "O");
		String printNo = SystemTool.getInstance().getNo("ALL", "BIL",
				"CCB_INV_NO", "CCB_INV_NO");
		parm.setData("PRINT_NO", printNo);
		bilParm.setData("PRINT_NO", printNo);
		// 判断是否走医保
		TParm insParm = new TParm();
		TParm selOpdParm = new TParm();
		TParm opdParm = new TParm();
		sql = "SELECT CASE_NO,REXP_CODE,AR_AMT,ORDER_CODE,ORDER_CAT1_CODE,TEMPORARY_FLG,"
				+ "ADM_TYPE,RX_NO,SEQ_NO,MED_APPLY_NO,CAT1_TYPE,SETMAIN_FLG FROM OPD_ORDER "
				+ "WHERE CASE_NO='"
				+ caseNo
				+ "' AND BILL_FLG='N' AND RECEIPT_NO IS NULL AND RELEASE_FLG='N' "
				+ "AND TO_CHAR(ORDER_DATE,'yyyy-MM-dd')=TO_CHAR(SYSDATE,'yyyy-MM-dd')";
		opdParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (opdParm.getErrCode() < 0 || opdParm.getCount() <= 0) {
			err("ERR:" + opdParm.getErrCode() + opdParm.getErrText()
					+ opdParm.getErrName());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
			return returnParm;
		}
		for (int i = 0; i <opdParm.getCount("ORDER_CODE"); i++) {
			if (!opdParm.getValue("CAT1_TYPE", i).equals("RIS")
					&& !opdParm.getValue("CAT1_TYPE", i).equals("LIS")) {
				continue;
			}
			if(opdParm.getData("MED_APPLY_NO",i)!=null) {
				String medApplyNo = opdParm.getValue("MED_APPLY_NO",i);
				String rxNo = opdParm.getValue("RX_NO",i);
				int seqNo = opdParm.getInt("SEQ_NO",i);
				String cat1Type = opdParm.getValue("CAT1_TYPE",i);
				String updateSql ="UPDATE MED_APPLY SET BILL_FLG='Y' WHERE APPLICATION_NO='"+medApplyNo+"'" +
						" AND ORDER_NO='"+rxNo+"' AND SEQ_NO="+seqNo+" AND CAT1_TYPE='"+cat1Type+"'";
				result = new TParm(TJDODBTool.getInstance().update(updateSql, connection));
				if (result.getErrCode() < 0) {         
					err("ERR: " + result.getErrCode() + result.getErrText());
					returnParm.setData("EXECUTE_FLAG", ISERROR);
					returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
					return returnParm;
				}  
		//		applyParm.setData("MED_APPLY_NO",opdParm.getValue("MED_APPLY_NO",0));
		//		applyParm.setData("RX_NO",opdParm.getValue("RX_NO",0));
		//		applyParm.setData("SEQ_NO",opdParm.getValue("SEQ_NO",0));
		//		applyParm.setData("CAT1_TYPE",opdParm.getValue("CAT1_TYPE",0));
			}
		}
		TParm hl7ParmEnd = new TParm();
		int hl7Count = opdParm.getCount("ORDER_CODE");
		for (int i = 0; i < hl7Count; i++) {
			hl7ParmEnd.addData("ORDER_CAT1_CODE", opdParm.getData(
					"ORDER_CAT1_CODE", i));
			hl7ParmEnd.addData("TEMPORARY_FLG", opdParm.getData(
					"TEMPORARY_FLG", i));
			hl7ParmEnd.addData("ADM_TYPE", opdParm.getData("ADM_TYPE", i));
			hl7ParmEnd.addData("RX_NO", opdParm.getData("RX_NO", i));
			hl7ParmEnd.addData("SEQ_NO", opdParm.getData("SEQ_NO", i));
			hl7ParmEnd.addData("SETMAIN_FLG", opdParm.getData("SETMAIN_FLG", i));
		}
		hl7ParmEnd.setData("PAT_NAME", parName);
		hl7ParmEnd.setData("CASE_NO", caseNo);

		double[] chargeDouble = new double[30];
		double[] sumAmt = chargeDouble(opdParm, chargeDouble, 2);
		int index = 1;
		// 写入数据
		for (int i = 0; i < chargeDouble.length; i++) {
			String chargeTemp = "CHARGE";
			if (i < 9) {
				bilParm.setData(chargeTemp + "0" + index, chargeDouble[i]);
			} else {
				bilParm.setData(chargeTemp + index, chargeDouble[i]);
			}
			index++;
		}
		if (!"".equals(confirmNo) && confirmNo != null) {
			TParm regionParm = null;// 获得医保区域代码
			regionParm = SYSRegionTool.getInstance().selectdata("H01");// 获得医保区域代码
			parm.setData("CONFIRM_NO", confirmNo);
			parm.setData("CASE_NO", caseNo);
			insParm = new TParm();
			insParm.setData("CONFIRM_NO", confirmNo);
			insParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));
			insParm.setData("INS_TYPE", "1");
			insParm.setData("CASE_NO", caseNo);
			insParm.setData("OPT_USER", parm.getData("OPT_NAME"));
			insParm.setData("OPT_TERM", parm.getData("OPT_IP"));
			insParm.setData("RECP_TYPE", "OPB");
			// 获取医保数据
			result = query("getInsOpd", parm);		
			if (result.getErrCode() < 0 || result.getCount() <= 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "医保支付失败,请联系信息中心执行自动对账");
				return returnParm;
			}
			double insPay = result.getDouble("INS_TOT_AMT", 0)
					- result.getDouble("UNACCOUNT_PAY_AMT", 0);
			double personPay = result.getDouble("UNACCOUNT_PAY_AMT", 0); 
				bilParm.setData("PAY_INS_CARD", insPay);        
			bilParm.setData("PAY_OTHER2", personPay);
			bilParm.setData("AR_AMT", result.getDouble("INS_TOT_AMT", 0)); // 非医保金额
			bilParm.setData("TOT_AMT", result.getDouble("INS_TOT_AMT", 0));

			parm.setData("AMT", -insPay);
			TParm opbReadCardParm = new TParm();
			opbReadCardParm.setData("TOT_AMT", result.getDouble("TOT_AMT", 0));
			TParm settlementDetailsParm = new TParm();
			settlementDetailsParm.setData("INS_PAY_AMT", result.getDouble(
					"INS_PAY_AMT", 0));
			settlementDetailsParm.setData("UNREIM_AMT", result.getDouble(
					"UNREIM_AMT", 0));
			settlementDetailsParm.setData("OINSTOT_AMT", result.getDouble(
					"OINSTOT_AMT", 0));
			settlementDetailsParm.setData("OWN_AMT", result.getDouble(
					"OWN_AMT", 0));
			insParm.setData("opbReadCardParm", opbReadCardParm.getData());
			insParm.setData("settlementDetailsParm", settlementDetailsParm
					.getData());
			result = INSTJReg.getInstance().insCommFunction(insParm.getData());
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + "" + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "医保支付失败");
				return returnParm;
			}
			// 更新医保票据号
			sql = "UPDATE INS_OPD SET INV_NO='" + printNo
					+ "' WHERE CONFIRM_NO='" + confirmNo + "'";
			// 添加交易表走医保部分
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
				return returnParm;
			}
			sql = "UPDATE INS_OPD_ORDER SET INV_NO='" + printNo
					+ "' WHERE CONFIRM_NO='" + confirmNo + "'";
			// 添加交易表走医保部分
			result = new TParm(TJDODBTool.getInstance().update(sql, connection));
			if (result.getErrCode() < 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
				return returnParm;
			}
			parm.setData("AMT", -insPay);
			parm.setData("TRADE_NO", (String) parm.getData("tradeNOINS"));
			parm.setData("STATE", "9");
			parm.setData("RECEIPT_NO", receiptNo);
			result = this.insertOpbCcbTrade(parm);
			if (result.getErrCode() < 0) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "插入交易表失败");
				return returnParm;
			}
		} else {
			parm.setData("TRADE_NO", (String) parm.getData("tradeNOCCB"));
			parm.setData("STATE", "1");
			bilParm.setData("PAY_INS_CARD", 0);
			bilParm.setData("PAY_OTHER2", sumAmt[0]);
			bilParm.setData("AR_AMT", sumAmt[0]); // 非医保金额
			bilParm.setData("TOT_AMT", sumAmt[0]);
			parm.setData("AMT", sumAmt[0]);
			parm.setData("AR_AMT", sumAmt[0]);
		}
		bilParm.setData("REDUCE_AMT", 0);
		bilParm.setData("PAY_CASH", 0);
		bilParm.setData("PAY_MEDICAL_CARD", 0);
		bilParm.setData("PAY_BANK_CARD", 0);
		bilParm.setData("PAY_CHECK", 0);
		bilParm.setData("PAY_DEBIT", 0);
		bilParm.setData("PAY_BILPAY", 0);
		bilParm.setData("PAY_INS", 0);
		bilParm.setData("PAY_OTHER1", 0);
		bilParm.setData("BILL_DATE", parm.getData("OPT_DATE"));
		bilParm.setData("CHARGE_DATE", parm.getData("OPT_DATE"));
		bilParm.setData("PRINT_DATE", parm.getData("OPT_DATE"));
		bilParm.setData("OPT_USER", (String) parm.getData("OPT_NAME"));
		bilParm.setData("OPT_TERM", (String) parm.getData("OPT_IP"));
		bilParm.setData("OPT_DATE", (Date) parm.getData("OPT_DATE"));
		bilParm.setData("CASHIER_CODE", (String) parm.getData("OPT_NAME"));
		bilParm.setData("PRINT_DATE", parm.getData("OPT_DATE"));
		bilParm.setData("PRINT_USER", parm.getData("OPT_NAME"));
		// 添加票据档
		result = this.insertBilOpbRecp(bilParm, connection);
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "插入票据表失败");
			return returnParm;
		}
		result = this.insertOpbInvrcp(bilParm, connection);
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "插入票据表失败");
			return returnParm;
		}
		parm.setData("STATE", "1");
		parm.setData("AMT", sumAmt[0]);
		parm.setData("TRADE_NO", (String) parm.getData("tradeNOCCB"));
		parm.setData("RECEIPT_NO", receiptNo);
		result = this.insertOpbCcbTrade(parm);
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "插入交易表失败");
			return returnParm;
		}
		String optUser = (String) parm.getData("OPT_NAME");
		sql = "UPDATE OPD_ORDER SET PRINT_FLG='Y', BILL_FLG='Y',RECEIPT_NO='"
				+ receiptNo
				+ "',BILL_DATE=SYSDATE,BILL_USER='"
				+ optUser
				+ "' WHERE CASE_NO='"
				+ caseNo
				+ "' AND BILL_FLG='N' AND TO_CHAR(ORDER_DATE,'yyyy-MM-dd')=TO_CHAR(SYSDATE,'yyyy-MM-dd')";
		result = result = new TParm(TJDODBTool.getInstance().update(sql,
				connection));
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + "" + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易确认失败");
			return returnParm;
		}
		System.out.println("confirmTranOpb==:"+hl7ParmEnd.getData());
		//物联网,HL7消息使用数据----------------------begin
		returnParm.setData("CASENO", caseNo);
		returnParm.setData("MRNO", mrNo);
		returnParm.setData("PATNAME", parName);
		returnParm.setData("HL7PARM", hl7ParmEnd.getData());
		//物联网,HL7消息使用数据----------------------end
		returnParm.setData("BANK_STREAM_NO", "");
		returnParm.setData("BANK_RET_STREAM_NO", "");
		returnParm.setData("PAY_SEQ", receiptNo);
		returnParm.setData("TRADE_STATUS", "0");
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "交易成功");
		System.out.println("confirmTranOpb==111111:"+returnParm);
		return returnParm;
	}
	/**
	 *  发送物联网
	 * OPD_ORDER发送物联网
	 * @param orderParm
	 */
	public void opdOrderWLW(String caseNo,String mrNo,String parName) {
		System.out.println("opdOrderWLW==1:");
		TParm SPC = new TParm();
		String sql1 = "SELECT SPC_FLG FROM SYS_REGION";
		SPC = new TParm(TJDODBTool.getInstance().select(sql1));	
		//物联网
		if (SPC.getData("SPC_FLG", 0).equals("Y")) {
			System.out.println("opdOrderWLW==2:");
		opdOrderSpc(caseNo,mrNo,parName);
		}
		return;		
	}		
	/**
	 *  添加物联网
	 * OPD_ORDER表添加数据 
	 * @param orderParm
	 */
	private void opdOrderSpc(String caseNo,String mrNo,String parName){
		System.out.println("opdOrderSpc==1:");
			// 物联网获得此次操作的医嘱，通过处方签获得
				String sql= " SELECT A.CASE_NO,A.RX_NO,A.SEQ_NO,A.PRESRT_NO,A.REGION_CODE,A.MR_NO,A.ADM_TYPE,A.RX_TYPE,A.TEMPORARY_FLG,"+
				" A.RELEASE_FLG,A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_CODE,A.ORDER_DESC,A.GOODS_DESC,A.SPECIFICATION,"+
				" A.ORDER_CAT1_CODE,A.MEDI_QTY,A.MEDI_UNIT,A.FREQ_CODE,A.ROUTE_CODE,A.TAKE_DAYS,A.DOSAGE_QTY,A.DOSAGE_UNIT,"+
				" A.DISPENSE_QTY,A.DISPENSE_UNIT,A.GIVEBOX_FLG,A.OWN_PRICE,A.NHI_PRICE,A.DISCOUNT_RATE,A.OWN_AMT,A.AR_AMT,"+
				" A.DR_NOTE,A.NS_NOTE,A.DR_CODE,A.ORDER_DATE,A.DEPT_CODE,A.DC_DR_CODE,A.DC_ORDER_DATE,A.DC_DEPT_CODE,A.EXEC_DEPT_CODE,"+
				" A.EXEC_DR_CODE,A.SETMAIN_FLG,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,A.HIDE_FLG,A.RPTTYPE_CODE,A.OPTITEM_CODE,A.DEV_CODE,"+
				" A.MR_CODE,A.FILE_NO,A.DEGREE_CODE,A.URGENT_FLG,A.INSPAY_TYPE,A.PHA_TYPE,A.DOSE_TYPE,A.EXPENSIVE_FLG,A.PRINTTYPEFLG_INFANT,"+
				" A.CTRLDRUGCLASS_CODE,A.PRESCRIPT_NO,A.ATC_FLG,A.SENDATC_DATE,A.RECEIPT_NO,A.BILL_FLG,A.BILL_DATE,A.BILL_USER,A.PRINT_FLG,"+
				" A.REXP_CODE,A.HEXP_CODE,A.CONTRACT_CODE,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.PHA_CHECK_CODE,A.PHA_CHECK_DATE,"+
				" A.PHA_DOSAGE_CODE,A.PHA_DOSAGE_DATE,A.PHA_DISPENSE_CODE,A.PHA_DISPENSE_DATE,A.PHA_RETN_CODE,A.PHA_RETN_DATE,"+
				" A.NS_EXEC_CODE,A.NS_EXEC_DATE,A.NS_EXEC_DEPT,A.DCTAGENT_CODE,A.DCTEXCEP_CODE,A.DCT_TAKE_QTY,A.PACKAGE_TOT,A.AGENCY_ORG_CODE,"+
				" A.DCTAGENT_FLG,A.DECOCT_CODE,A.REQUEST_FLG,A.REQUEST_NO,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.MED_APPLY_NO,A.CAT1_TYPE,"+
				" A.TRADE_ENG_DESC,A.PRINT_NO,A.COUNTER_NO,A.PSY_FLG,A.EXEC_FLG,A.RECEIPT_FLG,A.BILL_TYPE,A.FINAL_TYPE,A.DECOCT_REMARK,"+
				" A.SEND_DCT_USER,A.SEND_DCT_DATE,A.DECOCT_USER,A.DECOCT_DATE,A.SEND_ORG_USER,A.SEND_ORG_DATE,A.EXM_EXEC_END_DATE,A.EXEC_DR_DESC,"+
				" A.COST_AMT,A.COST_CENTER_CODE,A.BATCH_SEQ1,A.VERIFYIN_PRICE1,A.DISPENSE_QTY1,A.BATCH_SEQ2,A.VERIFYIN_PRICE2,A.DISPENSE_QTY2,"+
				" A.BATCH_SEQ3,A.VERIFYIN_PRICE3,A.DISPENSE_QTY3,A.BUSINESS_NO,B.PAT_NAME,CASE "+
				" WHEN B.SEX_CODE ='1' "+
				" THEN '男' WHEN B.SEX_CODE='2' THEN '女' "+
				" ELSE '未知' "+
				" END AS SEX_TYPE,B.BIRTH_DATE FROM OPD_ORDER A,SYS_PATINFO B WHERE A.MR_NO=B.MR_NO AND A.BILL_FLG='Y'"+
                " AND A.CASE_NO= '"+caseNo+"'"+
                " AND A.CAT1_TYPE='PHA'"+			
				" AND A.RX_TYPE<>'7'";		
			TParm spcResult = new TParm(TJDODBTool.getInstance().select(sql));
			System.out.println("opdOrderSpc==2:"+spcResult);
			if (spcResult.getErrCode() < 0) {
				err(spcResult.getErrCode() + "" + spcResult.getErrText());
			} else {
				System.out.println("opdOrderSpc==3:"+spcResult);
				StringBuffer phaRxNoBuffer=new StringBuffer();//获得所有操作的处方签号码 发送数据
				int count =spcResult.getCount("ORDER_CODE");
			    if(spcResult.getData("ORDER_CODE")==null)
				return;
				for (int i = 0; i < count; i++) {
					System.out.println("opdOrderSpc==4:");
					if (!phaRxNoBuffer.toString().contains(spcResult.getValue("RX_NO", i))) {
						phaRxNoBuffer.append(spcResult.getValue("RX_NO", i)).append(",");
					}	
				}
				spcResult.setData("SUM_RX_NO", phaRxNoBuffer.length()>0? phaRxNoBuffer.toString().substring(0,
						phaRxNoBuffer.toString().lastIndexOf(",")):"");
				System.out.println("opdOrderSpc==5:"+spcResult);
				TSocket socket = new TSocket("127.0.0.1", 8080, "web");
				spcResult = TIOM_AppServer.executeAction(socket,
						"action.opd.OpdOrderSpcCAction", "saveSpcOpdOrder",
						spcResult);
				if (spcResult.getErrCode() < 0) {
					err(spcResult.getErrCode() + "" + spcResult.getErrText());
				} else {
					System.out.println("opdOrderSpc==6:");
					phaRxNo = phaRxNoBuffer.length()>0? phaRxNoBuffer.toString().substring(0,
							phaRxNoBuffer.toString().lastIndexOf(",")):"";
					sendMedMessages(mrNo,parName);
				}
			}
	}
	/**
	 * 向对应的门诊药房发送消息
	 *
	 */
	public void sendMedMessages(String mrNo,String parName) {
		System.out.println("sendMedMessages==1:");
		client1 = SocketLink
				.running("","ODO", "ODO");
		if (client1.isClose()) {
			out(client1.getErrText());
			return;
		}
		System.out.println("sendMedMessages==2:");
		String [] phaArray=new String [0];
		if(phaRxNo.length()>0){//获得所有操作的处方签号码 发送数据
			phaArray=phaRxNo.split(",");
		}
		System.out.println("sendMedMessages==3:");
		for (int i = 0; i < phaArray.length; i++) {
			System.out.println("sendMedMessages==4:");
			client1.sendMessage("PHAMAIN", "RX_NO:"//PHAMAIN :SKT_USER 表添加数据
					+ phaArray[i] + "|MR_NO:" + mrNo+ "|PAT_NAME:" + parName);
		}
		System.out.println("sendMedMessages==5:");
		if (client1 == null)
			return;
		client1.close();
	}
	/**
	 * 获得金额分类数据
	 * 
	 * @param opdParm
	 *            TParm
	 * @param chargeDouble
	 *            double[]
	 * @param type
	 *            int 1 .医疗卡操作 2.现金操作
	 * @return double
	 */
	private double[] chargeDouble(TParm opdParm, double[] chargeDouble, int type) {
		int opdCount = opdParm.getCount("CASE_NO");
		String rexpCode = "";
		double arAmt = 0.00;
		double[] sumAmt = new double[3]; // 获得总金额 医疗卡金额 绿色通道金额
		double allArAmt = 0.00;
		String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_CHARGE' ORDER BY SEQ";
		TParm sysChargeParm = new TParm(TJDODBTool.getInstance().select(sql));
		sql = "SELECT CHARGE01, CHARGE02, CHARGE03, CHARGE04, CHARGE05, CHARGE06, CHARGE07,"
				+ " CHARGE08, CHARGE09, CHARGE10, CHARGE11, CHARGE12, CHARGE13, CHARGE14,CHARGE15, "
				+ " CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24, "
				+ " CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29,CHARGE30 "
				+ " FROM BIL_RECPPARM WHERE ADM_TYPE ='O'";
		TParm bilRecpParm = new TParm(TJDODBTool.getInstance().select(sql));
		int chargeCount = sysChargeParm.getCount("ID");
		String[] chargeName = new String[30];
		int index = 1;
		for (int i = 0; i < 30; i++) {
			String chargeTemp = "CHARGE";
			if (i < 9) {
				chargeName[i] = bilRecpParm
						.getData(chargeTemp + "0" + index, 0).toString();
			} else {
				chargeName[i] = bilRecpParm.getData(chargeTemp + index, 0)
						.toString();
			}
			index++;
		}
		TParm p = new TParm();
		for (int i = 0; i < chargeCount; i++) {
			String sysChargeId = sysChargeParm.getData("ID", i).toString();
			for (int j = 0; j < chargeName.length; j++) {
				if (sysChargeId.equals(chargeName[j])) {
					p.setData("CHARGE", i, j);
					p.setData("ID", i, sysChargeParm.getData("ID", i));
					p.setData("CHN_DESC", i, sysChargeParm.getData("CHN_DESC",
							i));
					break;
				}
			}
		}
		String idCharge = "";
		int charge = 0;
		double greenAmt = 0.00; // 绿色通道金额
		double ektAmt = 0.00;
		double tempAmt = 0.00;
		for (int i = 0; i < opdCount; i++) {
			rexpCode = opdParm.getValue("REXP_CODE", i);
			arAmt = opdParm.getDouble("AR_AMT", i);
			allArAmt = allArAmt + arAmt;
			for (int j = 0; j < p.getCount("ID"); j++) {
				idCharge = p.getData("ID", j).toString();
				charge = p.getInt("CHARGE", j);
				if (rexpCode.equals(idCharge))
					chargeDouble[charge] = chargeDouble[charge] + arAmt;
				// ========20120220 zhangp modify end
			}
		}
		sumAmt[0] = allArAmt;
		// sumAmt[1] = ektAmt; // 医疗卡金额
		// sumAmt[2] = greenAmt; // 绿色通道金额
		return sumAmt;
	}

	/**
	 * 添加回冲票据交易信息
	 * 
	 * @param caseNo
	 * @param connection
	 * @return
	 */
	public TParm backFlush(TParm parm, TConnection connection) {
		// 添加负的票据
		TParm bilparm = new TParm();
		String caseNo = (String) parm.getData("caseNo");
		String cancelUser = (String) parm.getData("OPT_NAME");
		Date cancelDate = (Date) parm.getData("OPT_DATE");
		bilparm.setData("CASE_NO", caseNo);
		String receiptNo = SystemTool.getInstance().getNo("ALL", "BIL",
				"CCB_RECEIPT_NO", "CCB_RECEIPT_NO");
		bilparm.setData("RECEIPT_NO", receiptNo);
		bilparm.setData("ADM_TYPE", "O");
		String sql = "SELECT RECEIPT_NO,PRINT_NO,REGION_CODE,MR_NO,BILL_DATE,CHARGE_DATE,PRINT_DATE,"
				+ "REG_FEE,CLINIC_FEE,OTHER_FEE1,OTHER_FEE2,AR_AMT,PAY_CASH,PAY_INS_CARD"
				+ " FROM BIL_REG_RECP WHERE CASE_NO='" + caseNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		bilparm.setData("REGION_CODE", result.getData("REGION_CODE", 0));
		bilparm.setData("MR_NO", result.getData("MR_NO", 0));
		bilparm.setData("PRINT_NO", result.getData("PRINT_NO", 0));
		// bilparm.setData("BILL_DATE",result.getData("BILL_DATE",0));
		// bilparm.setData("CHARGE_DATE",result.getData("CHARGE_DATE",0));
		// bilparm.setData("PRINT_DATE",result.getData("PRINT_DATE",0));
		bilparm.setData("REG_FEE", -(Double) (result.getData("REG_FEE", 0)));
		bilparm.setData("CLINIC_FEE", -(Double) (result
				.getData("CLINIC_FEE", 0)));
		bilparm.setData("OTHER_FEE1", result.getData("OTHER_FEE1", 0));
		bilparm.setData("OTHER_FEE2", result.getData("OTHER_FEE2", 0));
		bilparm.setData("AR_AMT", -(Double) (result.getData("AR_AMT", 0)));
		// bilparm.setData("PAY_CASH",result.getData("PAY_CASH",0));
		// bilparm.setData("PAY_INS_CARD",result.getData("PAY_INS_CARD",0));
		String oldRecepitNo = (String) result.getData("RECEIPT_NO", 0);
		// 插入一条负的数据
		result = this.insertBilRegRecp(bilparm, connection);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 更新RESET_RECEIPT_NO
		sql = "UPDATE BIL_REG_RECP SET RESET_RECEIPT_NO='" + receiptNo
				+ "' WHERE CASE_NO='" + caseNo + "' " + "AND RECEIPT_NO='"
				+ oldRecepitNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			return result;
		}
		// 取消票据
		sql = "UPDATE BIL_INVRCP SET CANCEL_FLG='1',CANCEL_USER='" + cancelUser
				+ "',CANCEL_DATE=" + cancelDate + " WHERE RECEIPT_NO='"
				+ oldRecepitNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			return result;
		}
		sql = "SELECT CARD_NO,MR_NO,BUSINESS_NO,PAT_NAME,AMT,STATE,BUSINESS_TYPE,BANK_FLG "
				+ "FROM EKT_CCB_TRADE "
				+ "WHERE CASE_NO='"
				+ caseNo
				+ "' AND BUSINESS_TYPE='REG' AND STATE='1'";
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		// 交易表退款
		TParm tradeParm = new TParm();
		tradeParm.setData("TRADE_NO", SystemTool.getInstance().getNo("ALL",
				"EKT", "CCB_TRADE_NO", "CCB_TRADE_NO"));
		tradeParm.setData("CARD_NO", result.getData("CARD_NO", 0));
		tradeParm.setData("MR_NO", result.getData("MR_NO", 0));
		tradeParm.setData("CASE_NO", caseNo);
		tradeParm.setData("BUSINESS_NO", result.getData("BUSINESS_NO", 0));
		tradeParm.setData("PAT_NAME", result.getData("PAT_NAME", 0));
		tradeParm.setData("AMT", -(Double) result.getData("AMT", 0));
		tradeParm.setData("STATE", result.getData("STATE", 0));
		// tradeParm.setData("BUSINESS_TYPE",result.getData("BUSINESS_TYPE",0));
		result = this.insertCcbTrade(tradeParm);
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			return result;
		}
		return result;
	}

	/**
	 * 报道取消撤销医保，取消接口1
	 * 
	 * @param parm
	 * @return
	 */
	public TParm backIns(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		String guid = (String) parm.getData("GUID");
		String optUser = (String) parm.getData("OPT_NAME");
		String optIp = (String) parm.getData("OPT_IP");
		String caseNo = (String) parm.getData("TRAN_NO");
		String sql = "SELECT CONFIRM_NO FROM INS_OPD WHERE CASE_NO='" + caseNo
				+ "' AND INSAMT_FLG='1'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0
				|| result.getData("CONFIRM_NO", 0) == null) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易取消失败");
			return returnParm;
		}
		TParm regionParm = null;// 获得医保区域代码
		regionParm = SYSRegionTool.getInstance().selectdata("H01");// 获得医保区域代码
		String confirmNo = (String) result.getData("CONFIRM_NO", 0);
		parm.setData("CASE_NO", caseNo);
		parm.setData("RECP_TYPE", "REG");
		parm.setData("CONFIRM_NO", confirmNo);
		TParm insParm = new TParm();
		insParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));
		insParm.setData("INS_TYPE", 1);
		insParm.setData("RECP_TYPE", "REG");
		insParm.setData("CONFIRM_NO", confirmNo);
		insParm.setData("CASE_NO", caseNo);
		TParm opbReadCardParm = new TParm();
		opbReadCardParm.setData("CONFIRM_NO", confirmNo);
		insParm.setData("opbReadCardParm", opbReadCardParm.getData());
		result = INSTJFlow.getInstance().cancelBalance(insParm);// 取消费用结算操作
		if (result.getErrCode() < 0) {
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易取消失败");
			return returnParm;
		} else {
			INSOpdTJTool.getInstance().deleteINSOpd(insParm);// 删除数据
			INSOpdOrderTJTool.getInstance().deleteSumINSOpdOrder(insParm);
		}
		// 退挂操作
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "交易取消成功");
		return returnParm;
	}

	/**
	 * 当日挂号交易取消
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm bankCancelCurrent(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		String guid = (String) parm.getData("GUID");
		String optUser = (String) parm.getData("OPT_USER");
		String optIp = (String) parm.getData("OPT_IP");
		String caseNo = (String) parm.getData("TRAN_NO");
		String sql = "SELECT CASE_NO,CLINICROOM_NO,SESSION_CODE,QUE_NO,TO_CHAR(ADM_DATE,'yyyy-mm-dd') AS ADM_DATE FROM REG_PATADM WHERE APPT_CODE='N' AND ARRIVE_FLG='N' AND CASE_NO='"
				+ caseNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));    
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "保存挂号信息交易取消失败");
			return returnParm;
		}
		String sessionCode = (String) result.getData("SESSION_CODE", 0);     
		String clinicroomNo = (String) result.getData("CLINICROOM_NO", 0);
		Long queNo = (Long) result.getData("QUE_NO", 0);
		String admDate = (String) result.getData("ADM_DATE", 0);
		admDate = admDate.substring(0, 4) + admDate.substring(5, 7)
				+ admDate.substring(8, 10);
		sql = "UPDATE REG_CLINICQUE SET QUE_STATUS = 'N' WHERE ADM_TYPE = 'O' AND ADM_DATE = '"
			+ admDate
			+ "' AND "
			+ "SESSION_CODE = '"
			+ sessionCode
			+ "' AND "
			+ "CLINICROOM_NO = '"
			+ clinicroomNo
			+ "' AND "
			+ "QUE_NO = " + queNo;
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE _MESSAGE", "当日挂号交易取消失败");
			return returnParm;
		}
		sql = "UPDATE REG_PATADM SET REGCAN_DATE = SYSDATE,REGCAN_USER='"
				+ optUser + "',OPT_USER='" + optUser + "',OPT_DATE = SYSDATE,"
				+ "OPT_TERM='" + optIp + "' WHERE CASE_NO='" + caseNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "保存挂号信息交易取消失败");
			return returnParm;
		}
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "交易取消成功");
		return returnParm;
	}

	/**
	 * 当日挂号医保结算取消 取消类型:4
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm bankCancelCurrentIns(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		String optUser = (String) parm.getData("OPT_USER");
		String optIp = (String) parm.getData("OPT_IP");
		String caseNo = (String) parm.getData("TRAN_NO");
		parm.setData("CASE_NO", caseNo);
		String sql = "SELECT MR_NO,NHI_NO,CONFIRM_NO,TO_CHAR(ADM_DATE,'yyyy-mm-dd') AS ADM_DATE,SESSION_CODE,CLINICROOM_NO,"
				+ "QUE_NO,APPT_CODE,"
				+ "ADM_TYPE,CLINICTYPE_CODE"
				+ " FROM REG_PATADM WHERE CASE_NO='"
				+ caseNo
				+ "' "
				+ " AND REGCAN_USER IS NULL AND REGCAN_DATE IS NULL";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE _MESSAGE", "交易取消失败");  
			return returnParm;
		}
		String sessionCode = (String) result.getData("SESSION_CODE", 0);     
		String clinicroomNo = (String) result.getData("CLINICROOM_NO", 0);
		Long queNo = (Long) result.getData("QUE_NO", 0);
		String admDate = (String) result.getData("ADM_DATE", 0);
		admDate = admDate.substring(0, 4) + admDate.substring(5, 7)
				+ admDate.substring(8, 10);
		//释放VIP号
		sql = "UPDATE REG_CLINICQUE SET QUE_STATUS = 'N' WHERE ADM_TYPE = 'O' AND ADM_DATE = '"
			+ admDate
			+ "' AND "
			+ "SESSION_CODE = '"
			+ sessionCode
			+ "' AND "
			+ "CLINICROOM_NO = '"
			+ clinicroomNo
			+ "' AND "
			+ "QUE_NO = " + queNo;
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE _MESSAGE", "交易取消失败");
			return returnParm;
		}
		sql = "SELECT CONFIRM_NO FROM INS_OPD WHERE CASE_NO='" + caseNo
				+ "' AND INSAMT_FLG='1'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0
				|| result.getData("CONFIRM_NO", 0) == null) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE _MESSAGE", "交易取消失败");
			return returnParm;
		}
		sql = "UPDATE REG_PATADM SET REGCAN_DATE = SYSDATE,REGCAN_USER='"
			+ optUser + "',OPT_USER='" + optUser + "',OPT_DATE = SYSDATE,"
			+ "OPT_TERM='" + optIp + "' WHERE CASE_NO='" + caseNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "保存挂号信息交易取消失败");
			return returnParm;
		}
		TParm regionParm = null;// 获得医保区域代码
		regionParm = SYSRegionTool.getInstance().selectdata("H01");// 获得医保区域代码
		String confirmNo = (String) result.getData("CONFIRM_NO", 0);
		parm.setData("CASE_NO", caseNo);
		parm.setData("RECP_TYPE", "REG");
		parm.setData("CONFIRM_NO", confirmNo);
		TParm insParm = new TParm();
		insParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));        
		insParm.setData("INS_TYPE", 1);
		insParm.setData("RECP_TYPE", "REG");
		insParm.setData("CONFIRM_NO", confirmNo);
		insParm.setData("CASE_NO", caseNo);
		TParm opbReadCardParm = new TParm();
		opbReadCardParm.setData("CONFIRM_NO", confirmNo);
		insParm.setData("opbReadCardParm", opbReadCardParm.getData());
		result = INSTJFlow.getInstance().cancelBalance(insParm);// 取消费用结算操作
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易取消失败");
			return returnParm;
		} else {
			INSOpdTJTool.getInstance().deleteINSOpd(insParm);// 删除数据
			INSOpdOrderTJTool.getInstance().deleteSumINSOpdOrder(insParm);
		}
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "交易取消成功");
		return returnParm;
	}

	/**
	 * 医保缴费取消，类型6
	 * 
	 * @param parm
	 * @return
	 */
	public TParm bankCancelChargeIns(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		String optUser = (String) parm.getData("OPT_NAME");
		String optIp = (String) parm.getData("OPT_IP");
		String caseNo = (String) parm.getData("TRAN_NO");
		String sql = "SELECT CONFIRM_NO FROM INS_OPD WHERE CASE_NO='" + caseNo
				+ "' AND INSAMT_FLG='1'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0
				|| result.getData("CONFIRM_NO", 0) == null) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易取消失败");
			return returnParm;
		}
		TParm regionParm = null;// 获得医保区域代码
		regionParm = SYSRegionTool.getInstance().selectdata("H01");// 获得医保区域代码
		String confirmNo = (String) result.getData("CONFIRM_NO", 0);
		parm.setData("CASE_NO", caseNo);
		parm.setData("RECP_TYPE", "REG");
		parm.setData("CONFIRM_NO", confirmNo);
		TParm insParm = new TParm();
		insParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));
		insParm.setData("INS_TYPE", 1);
		insParm.setData("RECP_TYPE", "REG");
		insParm.setData("CONFIRM_NO", confirmNo);
		insParm.setData("CASE_NO", caseNo);
		TParm opbReadCardParm = new TParm();
		opbReadCardParm.setData("CONFIRM_NO", confirmNo);
		insParm.setData("opbReadCardParm", opbReadCardParm.getData());
		result = INSTJFlow.getInstance().cancelBalance(insParm);// 取消费用结算操作
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "交易取消失败");
			return returnParm;
		} else {
			INSOpdTJTool.getInstance().deleteINSOpd(insParm);// 删除数据
			INSOpdOrderTJTool.getInstance().deleteSumINSOpdOrder(insParm);
		}
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "交易取消成功");
		return returnParm;
	}

	/**
	 * 自费缴费交易取消
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm bankCancelCharge(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "交易取消成功");
		return returnParm;
	}

	/**
	 * 预约挂号撤销 类型：8
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm bankReturnTran(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		String caseNo = (String) parm.getData("TRAN_NO");
		String optUser = (String) parm.getData("OPT_NAME");
		String optIp = (String) parm.getData("OPT_IP");
		String guid = (String) parm.getData("O_GUID");
		parm.setData("CASE_NO", caseNo);
		String sql = "SELECT CASE_NO,MR_NO FROM REG_PATADM WHERE CASE_NO='"
				+ caseNo
				+ "' "
				+ "AND REGCAN_USER IS NULL AND REGCAN_DATE IS NULL AND ARRIVE_FLG='N' "
				+ "AND APPT_CODE='Y' AND ADM_STATUS='1'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0
				|| result.getData("CASE_NO", 0) == null) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "退挂失败");
			return returnParm;
		}
		String mrNo = (String) result.getData("MR_NO", 0);
		caseNo = (String) result.getData("CASE_NO", 0);
		sql = "SELECT CCB_PERSON_NO FROM SYS_PATINFO WHERE MR_NO='" + mrNo
				+ "'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "退挂失败");
			return returnParm;
		}
		String personNo = (String) result.getData("CCB_PERSON_NO", 0);
//		sql = "SELECT BUSINESS_NO,AMT,OPT_DATE,CARD_NO FROM EKT_CCB_TRADE "
//				+ "WHERE CASE_NO='" + caseNo
//				+ "' AND STATE='1' AND BUSINESS_TYPE='REG'";
//		result = new TParm(TJDODBTool.getInstance().select(sql));
//		if (result.getErrCode() < 0 || result.getCount() <= 0) {
//			err("ERR: " + result.getErrCode() + result.getErrText());
//			returnParm.setData("EXECUTE_FLAG", ISERROR);
//			returnParm.setData("EXECUTE_MESSAGE", "退挂失败");
//			return returnParm;
//		}
//		String cardNo = (String) result.getData("CARD_NO", 0);
//		String streamNo = (String) result.getData("BUSINESS_NO", 0);
//		double amt = result.getDouble("AMT", 0);
//		Date optDate = (Date) result.getData("OPT_DATE", 0);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		returnParm.setData("OPT_ID", Operator.getID());
		returnParm.setData("OPT_IP", Operator.getIP());
		returnParm.setData("OPT_NAME", Operator.getName());
		returnParm.setData("HIS_CODE", "000551");
//		returnParm.setData("CARD_NO", cardNo);
		returnParm.setData("PERSON_NO", personNo);			
		returnParm.setData("O_TRAN_NO", caseNo);
//		returnParm.setData("O_STREAM_NO", streamNo);
		returnParm.setData("O_PAY_SEQ", ""); // 预约挂号不生成票据
		returnParm.setData("TRAN_NO", caseNo);
//		returnParm.setData("AMOUNT", String.valueOf(amt));
//		returnParm.setData("DATE", sf.format(optDate));
		returnParm.setData("RETURN_TYPE", "1"); // 预约挂号退费
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		return returnParm;
	}

	/**
	 * 预约挂号撤销占号
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm cancelReg(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		String caseNo = (String) parm.getData("TRAN_NO");
		String optUser = (String) parm.getData("OPT_USER");
		String optIp = (String) parm.getData("OPT_TERM");
		parm.setData("CASE_NO", caseNo);
		String sql = "SELECT CASE_NO,MR_NO,TO_CHAR(ADM_DATE,'yyyy-mm-dd') AS ADM_DATE,ADM_TYPE," +
				" SESSION_CODE,CLINICROOM_NO,QUE_NO FROM REG_PATADM WHERE CASE_NO='"
				+ caseNo
				+ "' "
				+ "AND REGCAN_USER IS NULL AND REGCAN_DATE IS NULL AND ARRIVE_FLG='N'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0
				|| result.getData("CASE_NO", 0) == null) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "预约挂号退挂失败");
			return returnParm;
		}
		//撤销占号
		String admDate =(String)result.getData("ADM_DATE",0);
		String admType =(String)result.getData("ADM_TYPE",0);
		String sessionCode =(String)result.getData("SESSION_CODE",0);
		String clinicroomNo =(String)result.getData("CLINICROOM_NO",0);
		Long queNo =(Long)result.getData("QUE_NO",0);
//		 System.out.println("admType========"+admType);
//		 System.out.println("sessionCode========"+sessionCode);
//		 System.out.println("clinicroomNo========"+clinicroomNo);
//		 System.out.println("queNo========"+queNo);
		sql = "UPDATE REG_PATADM SET REGCAN_DATE = SYSDATE,REGCAN_USER='"
				+ optUser + "',OPT_USER='" + optUser + "',OPT_DATE = SYSDATE,"
				+ "OPT_TERM='" + optIp + "' WHERE CASE_NO='" + caseNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE_MESSAGE", "预约挂号退挂失败");
			return returnParm;
			}
			
	    admDate = admDate.substring(0, 4) + admDate.substring(5, 7)
			+ admDate.substring(8, 10);
//	    System.out.println("admDate========"+admDate);
	    sql = "UPDATE REG_CLINICQUE SET QUE_STATUS = 'N' WHERE ADM_TYPE = '"
			+ admType
			+ "'AND ADM_DATE = '"
			+ admDate
			+ "' AND "
			+ "SESSION_CODE = '"
			+ sessionCode
			+ "' AND "
			+ "CLINICROOM_NO = '"
			+ clinicroomNo
			+ "' AND "
			+ "QUE_NO = " + queNo;
	    result = new TParm(TJDODBTool.getInstance().update(sql, connection));
	       if (result.getErrCode() < 0) {
	    	   returnParm.setData("EXECUTE_FLAG", ISERROR);
			   returnParm.setData("EXECUTE_MESSAGE", "预约挂号退挂失败");
		   return returnParm;
		}	      
//		sql = "SELECT CARD_NO,MR_NO,BUSINESS_NO,PAT_NAME,AMT,STATE,BUSINESS_TYPE,BANK_FLG "
//				+ "FROM EKT_CCB_TRADE "
//				+ "WHERE CASE_NO='"
//				+ caseNo
//				+ "' AND BUSINESS_TYPE='REG' AND STATE='1'";
//		result = new TParm(TJDODBTool.getInstance().select(sql));
//		if (result.getErrCode() < 0 || result.getCount() <= 0) {
//			err("ERR: " + result.getErrCode() + result.getErrText());
//			returnParm.setData("EXECUTE_FLAG", ISERROR);
//			returnParm.setData("EXECUTE_MESSAGE", "退费失败");
//			return returnParm;
//		}
//		// 交易表退款
//		TParm tradeParm = new TParm();
//		tradeParm.setData("TRADE_NO", SystemTool.getInstance().getNo("ALL",
//				"EKT", "CCB_TRADE_NO", "CCB_TRADE_NO"));
//		tradeParm.setData("CARD_NO", result.getData("CARD_NO", 0));
//		tradeParm.setData("MR_NO", result.getData("MR_NO", 0));
//		tradeParm.setData("CASE_NO", caseNo);
//		tradeParm.setData("BUSINESS_NO", parm.getData("BUSINESS_NO"));
//		tradeParm.setData("PAT_NAME", result.getData("PAT_NAME", 0));
//		tradeParm.setData("AMT", -(Double) result.getData("AMT", 0));
//		tradeParm.setData("STATE", "2");
//		tradeParm.setData("BUSINESS_TYPE", "REGT");
//		tradeParm.setData("RECEIPT_NO", "");
//		tradeParm.setData("OPT_USER", parm.getData("OPT_USER"));
//		tradeParm.setData("OPT_DATE", parm.getData("OPT_DATE"));
//		tradeParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
//		tradeParm.setData("TOKEN", parm.getData("TOKEN"));
//		tradeParm.setData("GUID", parm.getData("GUID"));
//		result = this.insertCcbTrade(tradeParm);
//		if (result.getErrCode() < 0) {
//			err("ERR: " + result.getErrCode() + result.getErrText());
//			returnParm.setData("EXECUTE_FLAG", ISERROR);
//			returnParm.setData("EXECUTE_MESSAGE", "退费失败");
//			return returnParm;
//		}
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "预约挂号撤销成功");
		return returnParm;
	}

	/**
	 * 挂号取消，废弃
	 * 
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm bankCancelTranCurrent(TParm parm, TConnection connection) {
		TParm returnParm = new TParm();
		String caseNo = (String) parm.getData("TRAN_NO");
		parm.setData("CASE_NO", caseNo);
		String sql = "SELECT MR_NO,NHI_NO,CONFIRM_NO,TO_CHAR(ADM_DATE,'yyyy-mm-dd') AS "
				+ "ADM_DATE,SESSION_CODE,CLINICROOM_NO,"
				+ "QUE_NO,APPT_CODE,"
				+ "ADM_TYPE,CLINICTYPE_CODE"
				+ "FROM REG_PATADM WHERE CASE_NO='"
				+ caseNo
				+ "' "
				+ "AND REGCAN_USER IS NULL AND REGCAN_DATE IS NULL";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			result.setData("EXECUTE_FLAG", ISERROR);
			result.setData("EXECUTE _MESSAGE", "交易取消失败");
			return returnParm;
		}
		String mrNo = (String) result.getData("MR_NO");
		String confirmNo = (String) result.getData("CONFIRM_NO", 0);
		String nhiNo = (String) result.getData("NHI_NO", 0);
		String optUser = (String) parm.getData("OPT_NAME");
		String optIp = (String) parm.getData("OPT_IP");
		String admDate = (String) result.getData("ADM_DATE", 0);
		admDate = admDate.substring(0, 4) + admDate.substring(5, 7)
				+ admDate.substring(8, 10);
		String sessionCode = (String) result.getData("SESSION_CODE", 0);     
		String clinicroomNo = (String) result.getData("CLINICROOM_NO", 0);
		Long queNo = (Long) result.getData("QUE_NO", 0);
		// 如果vip的话释放VIP号
		sql = "UPDATE REG_CLINICQUE SET QUE_STATUS = 'N' WHERE ADM_TYPE = 'O' AND ADM_DATE = '"
				+ admDate
				+ "' AND "
				+ "SESSION_CODE = '"
				+ sessionCode
				+ "' AND "
				+ "CLINICROOM_NO = '"
				+ clinicroomNo
				+ "' AND "
				+ "QUE_NO = " + queNo;
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE _MESSAGE", "保存挂号信息交易取消失败");
			return returnParm;
		}
		// 退挂操作
		sql = "UPDATE REG_PATADM SET REGCAN_DATE = SYSDATE,REGCAN_USER='"
				+ optUser + "',OPT_USER='" + optUser + "',OPT_DATE = SYSDATE,"
				+ "OPT_TERM='" + optIp + "' WHERE CASE_NO='" + caseNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE _MESSAGE", "保存挂号信息交易取消失败");
			return returnParm;
		}
		// 回冲票据表,交易表退费
		result = this.backFlush(parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR: " + result.getErrCode() + result.getErrText());
			returnParm.setData("EXECUTE_FLAG", ISERROR);
			returnParm.setData("EXECUTE _MESSAGE", "保存挂号信息交易取消失败");
			return returnParm;
		}
		// 走医保取消医保
		if (confirmNo != null && !"".equals(confirmNo)) {
			TParm insParm = new TParm();
			TParm readCardParm = new TParm();
			readCardParm.setData("STATE", "9"); // 9：医保卡扣费
			readCardParm.setData("CARD_TYPE", 2); // 9：类型
			readCardParm.setData("SSCARD_NO", (String) parm
					.getData("SSCARD_NO"));
			readCardParm.setData("MR_NO", mrNo);
			readCardParm.setData("OPT_USER", parm.getData("OPT_NAME"));
			readCardParm.setData("OPT_TERM", parm.getData("OPT_IP"));
			// 读取医保卡信息
			insParm = this.readCard(readCardParm);
			String crowdType = (String) insParm.getData("CROWD_TYPE");
			if (!CZ.equals(crowdType)) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "执行医保失败，暂只支持城职");
				return returnParm;
			}
			// 分割金额
			TParm insFeeParm = new TParm();
			result = this.insExeFee(false, insFeeParm, parm);
			if (result.getErrCode() != 0 || result == null) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "医保退款失败");
				return returnParm;
			}
			// 分割金额
			result = this.insExeFee(true, insFeeParm, parm);
			if (result.getErrCode() != 0 || result == null) {
				err("ERR: " + result.getErrCode() + result.getErrText());
				returnParm.setData("EXECUTE_FLAG", ISERROR);
				returnParm.setData("EXECUTE_MESSAGE", "医保退款失败");
				return returnParm;
			}

		}
		returnParm.setData("EXECUTE_FLAG", ISSCUESS);
		returnParm.setData("EXECUTE_MESSAGE", "交易取消成功");
		return returnParm;
	}

	/**
	 * 读取FTP对账文件
	 * 
	 * @param tparm
	 * @return
	 */
	public TParm readFile(TParm tparm) {
		String fileName = (String) tparm.getData("filName");
		String fileUrl = this.getPath() + fileName;
		TParm parm = new TParm();
		Map map = new HashMap();
		List<String> cardList = new ArrayList();
		List<String> caseList = new ArrayList();
		List<String> receiptList = new ArrayList();
		List<String> businessList = new ArrayList();
		List<String> amtList = new ArrayList();
		List<String> dateList = new ArrayList();
		List<String> idList = new ArrayList();
		List<String> ipList = new ArrayList();
		List<String> guidList = new ArrayList();
		List<String> typeList = new ArrayList();
		try {
			URL url = new URL(fileUrl);
			URLConnection conn = url.openConnection();
			DataInputStream input = new DataInputStream(conn.getInputStream());
			String line = null;
			while ((line = input.readLine()) != null) {
				String[] str = line.split("	");
				cardList.add(str[0]);
				caseList.add(str[1]);
				receiptList.add(str[2]);
				businessList.add(str[3]);
				amtList.add(str[4]);
				dateList.add(str[5]);
				idList.add(str[8]);
				ipList.add(str[9]);
				guidList.add(str[10]);
				typeList.add(str[6]);
			}
			map.put("CARD_NO", cardList);
			map.put("CASE_NO", caseList);
			map.put("RECEIPT_NO", receiptList);
			map.put("BUSINESS_NO", businessList);
			map.put("AMT", amtList);
			map.put("OPT_DATE", dateList);
			map.put("OPT_USER", idList);
			map.put("OPT_TERM", ipList);
			map.put("GUID", guidList);
			map.put("BUSINESS_TYPE", typeList);
			input.close();
		} catch (FileNotFoundException e) {
			parm.setErr(-1, "读取文件失败");
		} catch (IOException e) {
			parm.setErr(-1, "读取文件失败");
		}
		return new TParm(map);
	}

	/**
	 * 调用HL7
	 */
	public void sendHL7Mes(TParm sendHL7Parm) {
//		System.out.println("sendHL7Mes==1:");
		/**
		 * 发送HL7消息
		 * 
		 * @param admType
		 *            String 门急住别
		 * @param catType
		 *            医令分类
		 * @param patName
		 *            病患姓名
		 * @param caseNo
		 *            String 就诊号
		 * @param applictionNo
		 *            String 条码号
		 * @param flg
		 *            String 状态(0,发送1,取消)
		 */
		int count = 0;
		if (null != sendHL7Parm && null != sendHL7Parm.getData("ADM_TYPE"))
			count = ((Vector) sendHL7Parm.getData("ADM_TYPE")).size();
		List list = new ArrayList();
		String patName = (String) sendHL7Parm.getData(("PAT_NAME"));
		for (int i = 0; i < count; i++) {
			TParm temp = sendHL7Parm.getRow(i);
			if (temp.getValue("TEMPORARY_FLG").length() == 0) {
				continue;
			}
//			System.out.println("sendHL7Mes==2:");
			String cat1Type = temp.getValue("ORDER_CAT1_CODE");
			TParm cat1TypeParm = new TParm(TJDODBTool.getInstance().select(
					"SELECT CAT1_TYPE FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE='"
							+ cat1Type + "'"));
			if ((cat1TypeParm.getValue("CAT1_TYPE", 0).equals("RIS")&& 
					temp.getValue("SETMAIN_FLG").equals("Y"))
			  ||(cat1TypeParm.getValue("CAT1_TYPE", 0).equals("LIS")&& 
					temp.getValue("SETMAIN_FLG").equals("Y"))) {
			String admType = temp.getValue("ADM_TYPE");
			String caseNo = (String) sendHL7Parm.getData("CASE_NO");
			String sql = " SELECT CASE_NO,MED_APPLY_NO,CAT1_TYPE FROM OPD_ORDER "
					+ "  WHERE CASE_NO ='"
					+ caseNo
					+ "' "
					+ "    AND RX_NO='"
					+ temp.getValue("RX_NO")
					+ "' "
					+ "    AND ORDERSET_CODE IS NOT NULL "
					+ "    AND ORDERSET_CODE = ORDER_CODE "
					+ "    AND SEQ_NO='"
					+ temp.getValue("SEQ_NO")
					+ "' AND ADM_TYPE='" + admType + "'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//			System.out.println("sendHL7Mes==3:"+result);
			TParm parm = new TParm();
			parm.setData("PAT_NAME", patName);
			parm.setData("ADM_TYPE", admType);
			parm.setData("FLG", 0);
			parm.setData("CASE_NO", result.getValue("CASE_NO", 0));
			parm.setData("LAB_NO", result.getValue("MED_APPLY_NO", 0));
			parm.setData("CAT1_TYPE", result.getValue("CAT1_TYPE", 0));
			parm.setData("ORDER_NO", temp.getValue("RX_NO"));
			parm.setData("SEQ_NO", temp.getValue("SEQ_NO"));
			list.add(parm);
//			System.out.println("sendHL7Mes==4:");
		}
	}
		TParm resultParm = Hl7Communications.getInstance().Hl7Message(list);
//		System.out.println("sendHL7Mes==5:");
		if (resultParm.getErrCode() < 0) {

		}
	}

	/**
	 * 泰心挂号排队叫号
	 * 
	 * @param type
	 *            String
	 * @param caseNo
	 *            String
	 * @return String
	 */
	public String callNo(String type, String caseNo) {
		TParm inParm = new TParm();
		String sql = "SELECT CASE_NO, A.MR_NO,A.CLINICROOM_NO,A.ADM_TYPE,A.QUE_NO,A.REGION_CODE,";
		sql += "TO_CHAR (ADM_DATE, 'YYYY-MM-DD') ADM_DATE,A.SESSION_CODE,";
		sql += "A.CLINICAREA_CODE, A.CLINICROOM_NO, QUE_NO, REG_ADM_TIME,";
		sql += "B.DEPT_CHN_DESC, DR_CODE, REALDEPT_CODE, REALDR_CODE, APPT_CODE,";
		sql += "VISIT_CODE, REGMETHOD_CODE, A.CTZ1_CODE, A.CTZ2_CODE, A.CTZ3_CODE,";
		sql += "C.USER_NAME,D.CLINICTYPE_DESC, F.CLINICROOM_DESC, E.PAT_NAME,";
		sql += "TO_CHAR (E.BIRTH_DATE, 'YYYY-MM-DD') BIRTH_DATE, G.CHN_DESC SEX,H.SESSION_DESC";
		sql += " FROM REG_PATADM A,";
		sql += "SYS_DEPT B,";
		sql += "SYS_OPERATOR C,";
		sql += "REG_CLINICTYPE D,";
		sql += "SYS_PATINFO E,";
		sql += "REG_CLINICROOM F,";
		sql += "(SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX') G,";
		sql += "REG_SESSION H";
		sql += " WHERE CASE_NO = '" + caseNo + "'";
		sql += " AND A.DEPT_CODE = B.DEPT_CODE(+)";
		sql += " AND A.DR_CODE = C.USER_ID(+)";
		sql += " AND A.CLINICTYPE_CODE = D.CLINICTYPE_CODE(+)";
		sql += " AND A.MR_NO = E.MR_NO(+)";
		sql += " AND A.CLINICROOM_NO = F.CLINICROOM_NO(+)";
		sql += " AND E.SEX_CODE = G.ID";
		sql += " AND A.SESSION_CODE=H.SESSION_CODE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		// 挂号日期
		String sendString = result.getValue("ADM_DATE", 0) + "|";
		// 看诊科室
		sendString += result.getValue("DEPT_CHN_DESC", 0) + "|";
		// 医师代码
		sendString += result.getValue("DR_CODE", 0) + "|";
		// 医师姓名
		sendString += result.getValue("USER_NAME", 0) + "|";
		// 号别
		sendString += result.getValue("CLINICTYPE_DESC", 0) + "|";

		// 诊间
		sendString += result.getValue("CLINICROOM_DESC", 0) + "|";

		// 患者病案号
		sendString += result.getValue("MR_NO", 0) + "|";

		// 患者姓名
		sendString += result.getValue("PAT_NAME", 0) + "|";
		// 患者性别

		sendString += result.getValue("SEX", 0) + "|";
		// 患者生日
		sendString += result.getValue("BIRTH_DATE", 0) + "|";

		// 看诊序号
		sendString += result.getValue("QUE_NO", 0) + "|";

		String noSql = "SELECT QUE_NO,MAX_QUE FROM REG_SCHDAY";
		noSql += " WHERE REGION_CODE ='" + result.getValue("REGION_CODE", 0)
				+ "'";
		noSql += " AND ADM_TYPE ='" + result.getValue("ADM_TYPE", 0) + "'";
		noSql += " AND ADM_DATE ='"
				+ result.getValue("ADM_DATE", 0).replaceAll("-", "").substring(
						0, 8) + "'";
		noSql += " AND SESSION_CODE ='" + result.getValue("SESSION_CODE", 0)
				+ "'";
		noSql += " AND CLINICROOM_NO ='" + result.getValue("CLINICROOM_NO", 0)
				+ "'";
		//
		TParm noParm = new TParm(TJDODBTool.getInstance().select(noSql));
		// 限挂人数
		sendString += noParm.getValue("MAX_QUE", 0) + "|";
		// 已挂人数
		sendString += (Integer.valueOf(noParm.getValue("QUE_NO", 0)) - 1) + "|";

		// 时间段
		sendString += result.getValue("SESSION_DESC", 0);

		String timeSql = "SELECT START_TIME FROM REG_CLINICQUE";
		timeSql += " WHERE ADM_TYPE ='" + result.getValue("ADM_TYPE", 0) + "'";
		timeSql += " AND ADM_DATE ='"
				+ result.getValue("ADM_DATE", 0).replaceAll("-", "").substring(
						0, 8) + "'";
		timeSql += " AND SESSION_CODE ='" + result.getValue("SESSION_CODE", 0)
				+ "'";
		timeSql += " AND CLINICROOM_NO ='"
				+ result.getValue("CLINICROOM_NO", 0) + "'";
		timeSql += " AND QUE_NO ='" + result.getValue("QUE_NO", 0) + "'";
		TParm startTimeParm = new TParm(TJDODBTool.getInstance()
				.select(timeSql));

		// 退挂叫号
		if ("UNREG".equals(type)) {

			// 预约处理
			if (startTimeParm.getValue("START_TIME", 0) != null
					&& !startTimeParm.getValue("START_TIME", 0).equals("")) {
				sendString += "|" + startTimeParm.getValue("START_TIME", 0)
						+ "00";
			}

			inParm.setData("msg", sendString);

			TIOM_AppServer.executeAction("action.device.CallNoAction",
					"doUNReg", inParm);

		} else if ("REG".equals(type)) {
			sendString += "|";
			// 预约处理
			if (startTimeParm.getValue("START_TIME", 0) != null
					&& !startTimeParm.getValue("START_TIME", 0).equals("")) {
				sendString += startTimeParm.getValue("START_TIME", 0) + "00";
			} else {
				sendString += "";
			}
			// 2012-04-02|内分泌代谢科|000875|葛焕琦|主任医师|06诊室|000000001009|谷绍明|女|1936-01-05|2|60|2|上午|
			inParm.setData("msg", sendString);
			inParm.setData("msg", sendString);
			TSocket socket = new TSocket("127.0.0.1", 8080, "web");
			TIOM_AppServer.executeAction(socket,"action.device.CallNoAction",
					"doReg", inParm);
										
		}

		return "true";

	}

	/**
	 * 门诊缴费退费用到
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getTradeInfo(TParm parm) {
		TParm returnParm = new TParm();
		String receiptNo = (String) parm.getData("RECEIPT_NO");
		String caseNo = (String) parm.getData("CASE_NO");
		String sql = "SELECT CARD_NO,"
				+ "OPT_DATE,"
				+ "GUID,MR_NO,PAT_NAME,RECEIPT_NO,"
				+ "(select CCB_PERSON_NO FROM SYS_PATINFO B WHERE  A.MR_NO=B.MR_NO) AS PERSON_NO, "
				+ "BUSINESS_NO " + "FROM EKT_CCB_TRADE A "
				+ "WHERE RECEIPT_NO='" + receiptNo
				+ "' AND STATE='1' AND CASE_NO='" + caseNo + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 获取医嘱信息
	 * 
	 * @param caseNo
	 * @return
	 */
	public TParm getOrder(String caseNo) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm data = query("query", parm);  
		if (data.getErrCode() < 0) {
			err("Order+ERR:" + data.getErrCode() + data.getErrText()
					+ data.getErrName());
			return data;
		}
		return data;  
	}
	/**
	 * 通过就诊号查询是否建行数据，医生站使用
	 * @param parm
	 * @return
	 * =====pangben 2014-1-20
	 */
	public TParm getEktCcbTradeInfo(TParm parm){
		String sql="SELECT TRADE_NO, CARD_NO, MR_NO, CASE_NO, BUSINESS_NO, PAT_NAME, AMT, STATE, " +
				"BUSINESS_TYPE, OPT_USER, OPT_DATE, OPT_TERM, BANK_FLG, TOKEN, GUID, RECEIPT_NO " +
				"FROM EKT_CCB_TRADE WHERE CASE_NO='"+parm.getValue("CASE_NO")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
}
