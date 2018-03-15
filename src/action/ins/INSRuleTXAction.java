package action.ins;

import java.sql.Timestamp;
import java.util.Map;

import jdo.ins.INSRuleTXTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 医保卡三目字典:支付标准档
 * </p>
 * 
 * <p>
 * Description:医保卡三目字典:支付标准档
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
 * @author pangben 2011-12-08
 * @version 2.0
 */
public class INSRuleTXAction extends TAction {

	public TParm insertINSRule(TParm parm) {
		String regionCode = parm.getValue("REGION_CODE");
		String XMLB = parm.getValue("XMLB");
		TParm result = null;
		TConnection connection = getConnection();
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			result = INSRuleTXTool.getInstance().insertINSRule(
					getTemp(parm, regionCode, i, XMLB), connection);
			if (result.getErrCode() < 0) {
				connection.close();
				return result;
			}
		}
		connection.commit();
		connection.close();
		return result;
	}

	private TParm getTemp(TParm orderParm, String regionCode, int i, String XMLB) {
		TParm parm = new TParm();
		parm.setData("SFDLBM", regionCode);// 区域
		parm.setData("SFXMBM", orderParm.getValue("ORDER_CODE", i));// 项目编码
		parm.setData("XMBM", SystemTool.getInstance().getNo("ALL", "INS",
				"INSRULE_NO", "INSRULE_NO"));// 服务编码
		parm.setData("XMMC", orderParm.getValue("ORDER_DESC", i));// 服务名称
		parm.setData("XMRJ", orderParm.getValue("PY1", i));// 拼音
		parm.setData("BZJG", orderParm.getDouble("OWN_PRICE", i));// 标准金额
		parm.setData("SJJG", orderParm.getDouble("OWN_PRICE", i));// 实收金额
		parm.setData("XMLB", XMLB);// 项目类别
		parm.setData("JX", orderParm.getValue("DOSE_DESC", i)); // 剂型
		parm.setData("GG", orderParm.getValue("SPECIFICATION", i)); // 规格
		parm.setData("DW", orderParm.getValue("MEDI_UNIT_DESC", i)); // 单位:开药单位
		parm.setData("YF", orderParm.getValue("ROUTE_DESC", i)); // 用法
		// result.setData("YL",updateNull(parmReturn.getValue("DOSE_CODE")));
		// //用量
		parm.setData("SL", 1);
		parm.setData("BZ", orderParm.getValue("BZ", i));
		return parm;
	}

	/**
	 * 完全匹配数据单选按钮选中操作
	 * 
	 * @return
	 */
	public TParm updateSameSave(TParm executParm) {
		// TParm ruleParm =new TParm((Map)executParm.getData("ruleParm"));
		TParm result = null;
		TParm tempParm = null;
		TParm tempHisParm = null;
		String optUser = executParm.getValue("OPT_USER");
		String optTerm = executParm.getValue("OPT_TERM");
		String kssj = executParm.getValue("START_DAY");
		String kssjIn=executParm.getValue("END_DATE_IN");//新添加数据的开始时间
		String insCode=executParm.getValue("INS_CODE");//新医保码
		String nhiFeeDesc=executParm.getValue("NHI_FEE_DESC",0);//医保名称
		double nhiPrice=executParm.getDouble("NHI_PRICE",0);//医保金额
		String jssj = "99991231125959";
		// 历史记录
		TParm tableHistoryParm = executParm.getParm("tableHistoryParm");
		TConnection connection = null;
		connection = getConnection();
		//更改SYS_FEE表数据
		
		for (int i = 0; i < executParm.getCount("ORDER_CODE"); i++) {
			tempParm = executParm.getRow(i);
			tempParm.setData("OPT_USER", optUser);
			tempParm.setData("OPT_TERM", optTerm);
			if (tempParm.getBoolean("FLG")) {
				result = INSRuleTXTool.getInstance().updateSysFeeNhi(tempParm,
						connection);
				if (result.getErrCode() < 0) {
					connection.close();
					return result;
				}

			}
		}
		//修改SYS_FEE_HISTORY 表数据
		for (int i = 0; i < tableHistoryParm.getCount("ORDER_CODE"); i++) {
			tempHisParm = tableHistoryParm.getRow(i);
			if (tempHisParm.getBoolean("FLG")) {
				String sql = "UPDATE SYS_FEE_HISTORY SET END_DATE='"
						+ tempHisParm.getValue("END_DATE") + "'"
						+ " WHERE ORDER_CODE='"
						+ tempHisParm.getValue("ORDER_CODE")
						+ "' AND START_DATE='"
						+ tempHisParm.getValue("START_DATE") + "'";
				result = new TParm(TJDODBTool.getInstance().update(sql,
						connection));
				if (result.getErrCode() < 0) {
					connection.close();
					return result;
				}
			}
		}
		//添加SYS_FEE_HISTORY 表数据
		for (int i = 0; i < tableHistoryParm.getCount("ORDER_CODE"); i++) {
			tempHisParm = tableHistoryParm.getRow(i);
			if (tempHisParm.getBoolean("MAIN_FLG")) {
				// 如果最近一条的结束时间不在修改的开始时间以内,如 (开始时间:START_DATE :20110101
				// 修改的开始时间20101101)
				// 操作逻辑最近一条的结束时间改成今天的235959 创建新的开始时间为转天的零点
				String feeSql = "SELECT  S.ORDER_CODE,  "
						+ "  S.ORDER_DESC, S.ACTIVE_FLG, S.PY1, S.PY2, S.SEQ, "
						+ "  S.DESCRIPTION, S.TRADE_ENG_DESC, S.GOODS_DESC, "
						+ "  S.GOODS_PYCODE, S.ALIAS_DESC, S.ALIAS_PYCODE, "
						+ "  S.SPECIFICATION, S.NHI_FEE_DESC, S.HABITAT_TYPE, "
						+ "  S.MAN_CODE, S.HYGIENE_TRADE_CODE, S.ORDER_CAT1_CODE, "
						+ "  S.CHARGE_HOSP_CODE, S.OWN_PRICE, S.NHI_PRICE, "
						+ "  S.GOV_PRICE, S.UNIT_CODE, S.LET_KEYIN_FLG, "
						+ "  S.DISCOUNT_FLG, S.EXPENSIVE_FLG, S.OPD_FIT_FLG, "
						+ "  S.EMG_FIT_FLG, S.IPD_FIT_FLG, S.HRM_FIT_FLG, "
						+ "  S.DR_ORDER_FLG, S.INTV_ORDER_FLG, S.LCS_CLASS_CODE, "
						+ "  S.TRANS_OUT_FLG, S.TRANS_HOSP_CODE, S.USEDEPT_CODE, "
						+ "  S.EXEC_ORDER_FLG, S.EXEC_DEPT_CODE, S.INSPAY_TYPE, "
						+ "  S.ADDPAY_RATE, S.ADDPAY_AMT, S.NHI_CODE_O, "
						+ "  S.NHI_CODE_E, S.NHI_CODE_I, S.CTRL_FLG,  S.CLPGROUP_CODE, S.ORDERSET_FLG, S.INDV_FLG, "
						+ "  S.SUB_SYSTEM_CODE, S.RPTTYPE_CODE, S.DEV_CODE,  S.OPTITEM_CODE, S.MR_CODE, S.DEGREE_CODE, "
						+ "  S.CIS_FLG, S.OPT_USER, S.OPT_DATE,  S.OPT_TERM,  "
						+ "  S.ACTION_CODE,  S.ATC_FLG, S.OWN_PRICE2, S.OWN_PRICE3, "
						+ "  S.TUBE_TYPE, S.CAT1_TYPE, S.IS_REMARK, "
						+ "  S.ATC_FLG_I, S.REMARK_1, S.REMARK_2, "
						+ "  S.REGION_CODE, S.SYS_GRUG_CLASS, S.NOADDTION_FLG,   S.SYS_PHA_CLASS"
						+ " FROM  SYS_FEE S    " + " WHERE  S.ORDER_CODE='"
						+ tempHisParm.getValue("ORDER_CODE") + "' ";
				TParm feeParm = new TParm(TJDODBTool.getInstance().select(
						feeSql));
				feeParm = feeParm.getRow(0);
				feeParm.setData("OPT_USER", optUser);
				feeParm.setData("OPT_TERM", optTerm);
				if (tempHisParm.getDouble("START_DATE") > Double
						.parseDouble(kssj)) {
					//结束时间为当天的235959
					String sql = "UPDATE SYS_FEE_HISTORY SET END_DATE='"
							+ StringTool.getString(SystemTool.getInstance()
									.getDate(), "yyyyMMDD") + "235959" + "'"
							+ " WHERE ORDER_CODE='"
							+ tempHisParm.getValue("ORDER_CODE")
							+ "' AND START_DATE='"
							+ tempHisParm.getValue("START_DATE") + "'";
					result = new TParm(TJDODBTool.getInstance().update(sql,
							connection));

					String start = StringTool.getString(StringTool.rollDate(
							SystemTool.getInstance().getDate(), 1), "yyyyMMDD")
							+ "000000";
					feeParm.setData("START_DATE", start);//添加的开始时间为转天的凌晨零点
					feeParm.setData("END_DATE", jssj);
					result = INSRuleTXTool.getInstance().saveFeeHistory(
							feeParm, connection);
					if (result.getErrCode() < 0) {
						connection.close();
						return result;
					}
				} else {
					//正常数据添加 最近一条数据结束时间为医保开始时间上一天的235959
					String sql = "UPDATE SYS_FEE_HISTORY SET END_DATE='"
						+  kssjIn + "'"
						+ " WHERE ORDER_CODE='"
						+ tempHisParm.getValue("ORDER_CODE")
						+ "' AND START_DATE='"
						+ tempHisParm.getValue("START_DATE") + "'";
					result = new TParm(TJDODBTool.getInstance().update(sql,
							connection));
					if (result.getErrCode() < 0) {
						connection.close();
						return result;
					}
					feeParm.setData("START_DATE", kssj);//添加数据开始时间为医保开始时间
					feeParm.setData("END_DATE", jssj);
					feeParm.setData("NHI_CODE_O", insCode);
					feeParm.setData("NHI_CODE_I", insCode);
					feeParm.setData("NHI_CODE_E", insCode);
					feeParm.setData("NHI_FEE_DESC", nhiFeeDesc);//医保名称
					feeParm.setData("NHI_PRICE", nhiPrice);//医保金额
					result = INSRuleTXTool.getInstance().saveFeeHistory(
							feeParm, connection);
					if (result.getErrCode() < 0) {
						connection.close();
						return result;
					}
				}
			}
		}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * 保存操作* 
	 * @return
	 */
	public TParm Save(TParm Parm) {
//		System.out.println("parm======action"+Parm);
		TParm result = new TParm();
		TParm tempParm = new TParm();
	    String type =Parm.getValue("TYPE");	
	    TConnection connection = null;
		connection = getConnection();
		tempParm.setData("OPT_USER", Parm.getValue("OPT_USER"));
		tempParm.setData("OPT_TERM", Parm.getValue("OPT_TERM"));
		tempParm.setData("ORDER_CODE", Parm.getValue("ORDER_CODE"));
		tempParm.setData("NHI_CODE_O", Parm.getValue("NHI_CODE_O"));
		tempParm.setData("NHI_CODE_I", Parm.getValue("NHI_CODE_I"));
		tempParm.setData("NHI_CODE_E", Parm.getValue("NHI_CODE_E"));
		tempParm.setData("NHI_FEE_DESC",Parm.getValue("NHI_FEE_DESC"));//医保名称
		tempParm.setData("UPDATESTARTDATE",Parm.getValue("UPDATESTARTDATE"));//开始日期
		tempParm.setData("ADDPAY_RATE",Parm.getValue("ADDPAY_RATE"));//增付比率
//		System.out.println("tempParm======"+tempParm);
		//更改SYS_FEE表数据
		result = INSRuleTXTool.getInstance().updateSysFee(tempParm,
				connection);
//		System.out.println("result:"+result);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
//		 System.out.println("result======0000"+result); 
		//修改SYS_FEE_HISTORY 表数据
		//正常添加修改一笔有效数据
		 if(type.equals("update")){
		//更改SYS_FEE_HISTORY表数据
		result = INSRuleTXTool.getInstance().updateSysFeeHistory(tempParm,
				connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		    }   
	     }else if(type.equals("insert")){
//	    	 System.out.println("parm======1"); 
	    	 result= insert(Parm,connection,result);
	     }	
		connection.commit();
		connection.close();
		return result;	
	}
	/**
	 * 正常添加修改一笔有效数据* 
	 * @return
	 */
	public TParm insert(TParm Parm,TConnection connection,TParm result) {
//		 System.out.println("parm======2");
	    String jssj = "99991231125959";
		String optUser =Parm.getValue("OPT_USER");
		String optTerm =Parm.getValue("OPT_TERM");
		String kssj =Parm.getValue("INSDATE");//医保开始时间
		kssj+="000000";
//		 System.out.println("kssj======"+kssj);		
        String kssjyesterday =Parm.getValue("YESTERDAYDATE");//医保开始时间的前一天
        kssjyesterday+="235959";
//        System.out.println("kssjyesterday======"+kssjyesterday);
	    //更改SYS_FEE_HISTORY表数据并添加一笔有效数据	 
   	 String feeSql = "SELECT  S.ORDER_CODE,  "
				+ "  S.ORDER_DESC, S.ACTIVE_FLG, S.PY1, S.PY2, S.SEQ, "
				+ "  S.DESCRIPTION, S.TRADE_ENG_DESC, S.GOODS_DESC, "
				+ "  S.GOODS_PYCODE, S.ALIAS_DESC, S.ALIAS_PYCODE, "
				+ "  S.SPECIFICATION, S.NHI_FEE_DESC, S.HABITAT_TYPE, "
				+ "  S.MAN_CODE, S.HYGIENE_TRADE_CODE, S.ORDER_CAT1_CODE, "
				+ "  S.CHARGE_HOSP_CODE, S.OWN_PRICE, S.NHI_PRICE, "
				+ "  S.GOV_PRICE, S.UNIT_CODE, S.LET_KEYIN_FLG, "
				+ "  S.DISCOUNT_FLG, S.EXPENSIVE_FLG, S.OPD_FIT_FLG, "
				+ "  S.EMG_FIT_FLG, S.IPD_FIT_FLG, S.HRM_FIT_FLG, "
				+ "  S.DR_ORDER_FLG, S.INTV_ORDER_FLG, S.LCS_CLASS_CODE, "
				+ "  S.TRANS_OUT_FLG, S.TRANS_HOSP_CODE, S.USEDEPT_CODE, "
				+ "  S.EXEC_ORDER_FLG, S.EXEC_DEPT_CODE, S.INSPAY_TYPE, "
				+ "  S.ADDPAY_RATE, S.ADDPAY_AMT, S.NHI_CODE_O, "
				+ "  S.NHI_CODE_E, S.NHI_CODE_I, S.CTRL_FLG,  S.CLPGROUP_CODE, S.ORDERSET_FLG, S.INDV_FLG, "
				+ "  S.SUB_SYSTEM_CODE, S.RPTTYPE_CODE, S.DEV_CODE,  S.OPTITEM_CODE, S.MR_CODE, S.DEGREE_CODE, "
				+ "  S.CIS_FLG, S.OPT_USER, S.OPT_DATE,  S.OPT_TERM,  "
				+ "  S.ACTION_CODE,  S.ATC_FLG, S.OWN_PRICE2, S.OWN_PRICE3, "
				+ "  S.TUBE_TYPE, S.CAT1_TYPE, S.IS_REMARK, "
				+ "  S.ATC_FLG_I, S.REMARK_1, S.REMARK_2, "
				+ "  S.REGION_CODE, S.SYS_GRUG_CLASS, S.NOADDTION_FLG,   S.SYS_PHA_CLASS"
				+ " FROM  SYS_FEE S    " + " WHERE  S.ORDER_CODE='"
				+ Parm.getValue("ORDER_CODE") + "' ";
		TParm feeParm = new TParm(TJDODBTool.getInstance().select(
				feeSql));
		feeParm = feeParm.getRow(0);
//		 System.out.println("feeParm======333333333"+feeParm); 
		feeParm.setData("OPT_USER", optUser);
		feeParm.setData("OPT_TERM", optTerm);
		//正常数据添加 最近一条数据结束时间为医保开始时间上一天的235959
		String sql = "UPDATE SYS_FEE_HISTORY SET END_DATE='" +kssjyesterday +"'"
			+ " WHERE ORDER_CODE='"
			+ Parm.getValue("ORDER_CODE")
			+ "' AND START_DATE ='"+Parm.getValue("UPDATESTARTDATE")+"'";
		result = new TParm(TJDODBTool.getInstance().update(sql,
				connection));
//		 System.out.println("result======111"+result); 
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		feeParm.setData("START_DATE", kssj);//添加数据开始时间为医保开始时间
		feeParm.setData("END_DATE", jssj);
		feeParm.setData("NHI_CODE_O", Parm.getValue("NHI_CODE_O"));
		feeParm.setData("NHI_CODE_I", Parm.getValue("NHI_CODE_I"));
		feeParm.setData("NHI_CODE_E", Parm.getValue("NHI_CODE_E"));
		feeParm.setData("NHI_FEE_DESC",Parm.getValue("NHI_FEE_DESC"));//医保名称
//		 System.out.println("feeParm======5555555555"+feeParm); 
		result = INSRuleTXTool.getInstance().saveFeeHistory(
				feeParm, connection);
//		 System.out.println("result======2222"+result); 
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		return result;
	}
}
