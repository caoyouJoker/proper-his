package action.ind;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jdo.ibs.IBSOrderdTool;
import jdo.ind.INDIBSOPDAutoMedTool;
import jdo.ind.IndOrgTool;
import jdo.spc.INDSQL;
import jdo.spc.INDTool;
import jdo.spc.IndRequestDTool;
import jdo.spc.IndRequestMTool;
import jdo.spc.IndSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import psyg.graphic.SysObj;
import action.ind.client.SpcIndRequestd;
import action.ind.client.SpcIndRequestm;
import action.ind.client.SpcService_SpcServiceImplPort_Client;

import com.dongyang.config.TConfig;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.patch.Patch;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 住院、门急诊备药批次
 * </p>
 * 
 * <p>
 * Description: 住院、门急诊备药批次
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author wukai 2016.12.06
 * @version 1.0
 */
public class INDIBSOPDAutoMedBatch extends Patch {

	public INDIBSOPDAutoMedBatch() {

	}

	private String app_org_code; // 申请部门

	private String to_org_code; // 接受部门

	private String request_no; // 申请单号

	private String u_type = "1";

	private String request_type = "EXM"; // 补充计费

	private boolean out_flg = true; // 出库flg

	private boolean in_flg = false; // 入库flg

	private String dispense_no; // 出库单号

	// 细项序号
	private int seq;

	private String opt_user = ""; // 操作人员

	public static final int DISPENSE_ERR_CODE = 99; // 错误码

	private String batch_date; // 批次执行的财务月

	// private TConnection conn;
	
	private Timestamp requestAndDispenseDate = null;
	
	
	@Override
	public boolean run() {
		// 批次运行
		requestAndDispenseDate = SystemTool.getInstance().getDate();
		// 开始和结束时间 获取 start
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(SystemTool.getInstance().getDate().getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String endD = sdf.format(cal.getTime());
		cal.add(Calendar.MONTH, -1);// 回滚一个月
		String startD = sdf.format(cal.getTime());
		batch_date = endD.substring(0, 6);
		// 开始和结束时间 获取 end
		TParm queryParm = new TParm();

		// 获取所有的申请部门
		TConnection conn = TDBPoolManager.getInstance().getConnection();
		queryParm.setData("ORG_TYPE", "C");
		queryParm.setData("EXINV_FLG", "Y");
		TParm appOrgParm = IndOrgTool.getInstance().getOrgCode(queryParm, conn);
		conn.close();

		queryParm.removeData("EXINV_FLG");
		queryParm.removeData("ORG_TYPE");
		queryParm.setData("START_DATE", startD);
		queryParm.setData("END_DATE", endD);
		queryParm.setData("REQUEST_FLG_B", "N");

		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			queryParm.setData("REGION_CODE", Operator.getRegion());
		}
		
		StringBuilder message = new StringBuilder("");
		
		TParm result = null;
		String type = "IBS"; // OPD：门急症 IBS:住院
		this.to_org_code = "040103"; // to_org_code住院药房
		this.opt_user = "INDIBSAutoMed";
		TParm requestM = null;
		TParm requestD = null;
		//if (getOrgBatchFlg(to_org_code)) { //暂时去掉 BATCH_FLG判断
			for (int i = 0; i < appOrgParm.getCount("ID"); i++) {
				app_org_code = String.valueOf(appOrgParm.getData("ID", i));
				queryParm.setData("TYPE", type);
				queryParm.setData("APP_ORG_CODE", app_org_code);
				queryParm.setData("TO_ORG_CODE", to_org_code);
				result = INDTool.getInstance().onQueryDeptExm(queryParm);
				if (result.getErrCode() < 0) {
					continue;
				}
				requestM = result.getParm("RESULT_M", 0);
				requestD = result.getParm("RESULT_D", 0);
				if (requestM.getCount("REQUEST_NO") <= 0
						&& requestD.getCount("PAT_NAME") <= 0) {
					continue;
				}
				// 保存申请单 start 同时增加出库
				result = onSaveRequestAndDispense(result, type);
				if(result.getErrCode() < 0) {
					message.append("[" + i + "] " + appOrgParm.getData("NAME", i) + "向住院药房请领出库 失败！\n");
				} else {
					message.append("[" + i + "] " + appOrgParm.getData("NAME", i) + "向住院药房请领出库 成功！\n");
				}
				// 保存出库单 start 同时增加出库
			}
		
//		} else {
//			// 将批次信息打印到后台log中
//			sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//			String fileNmae = (new StringBuilder())
//					.append(TConfig.getSystemValue("UDD_DISBATCH_LocalPath"))
//					.append("\\财务月自动备药批次错误(住院药房)").append(batch_date).append(".txt")
//					.toString();
//			try {
//				FileTool.setString(fileNmae, "[" + sdf.format(new Date())
//						+ "] 住院药房处于盘点状态，住院药房自动备药失败！\n");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
			String fileNmae1 = (new StringBuilder())
					.append(TConfig.getSystemValue("UDD_DISBATCH_LocalPath"))
					.append("\\财务月自动备药批次结果(住院药房)").append(batch_date).append(".txt")
					.toString();
			try {
				FileTool.setString(fileNmae1, message.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}	
		
			
		StringBuilder message1 = new StringBuilder("");	
		result = null;
		requestM = null;
		requestD = null;
		type = "OPD";
		this.to_org_code = "040102"; // to_org_code门急诊药房 批次
		this.opt_user = "INDOPDAutoMed";
//		if (getOrgBatchFlg(to_org_code)) {
			for (int i = 0; i < appOrgParm.getCount("ID"); i++) {
				app_org_code = String.valueOf(appOrgParm.getData("ID", i));
				queryParm.setData("TYPE", type);
				queryParm.setData("APP_ORG_CODE", app_org_code);
				queryParm.setData("TO_ORG_CODE", to_org_code);
				result = INDTool.getInstance().onQueryDeptExm(queryParm);
				if (result.getErrCode() < 0) {
					continue;
				}
				requestM = result.getParm("RESULT_M", 0);
				requestD = result.getParm("RESULT_D", 0);
				if (requestM.getCount("REQUEST_NO") <= 0
						&& requestD.getCount("PAT_NAME") <= 0) {
					continue;
				}
				// 保存申请单 start 同时增加出库
				result = onSaveRequestAndDispense(result, type);
				if(result.getErrCode() < 0) {
					message1.append("[" + i + "] " + appOrgParm.getData("NAME", i) + "向门急诊药房请领出库 失败！\n");
				} else {
					message1.append("[" + i + "] " + appOrgParm.getData("NAME", i) + "向门急诊药房请领出库 成功！\n");
				}
				// 保存出库单 start 同时增加出库
			}
			String fileNmae2 = (new StringBuilder())
					.append(TConfig.getSystemValue("UDD_DISBATCH_LocalPath"))
					.append("\\财务月自动备药批次结果(门急诊药房)").append(batch_date).append(".txt")
					.toString();
			try {
				FileTool.setString(fileNmae2, message1.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}	
//		} else {
//			// 将批次信息打印到后台log中
//			sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//			String fileNmae = (new StringBuilder())
//					.append(TConfig.getSystemValue("UDD_DISBATCH_LocalPath"))
//					.append("\\财务月自动备药批次错误(门急诊)").append(batch_date).append(".txt")
//					.toString();
//
//			try {
//				FileTool.setString(fileNmae, "[" + sdf.format(new Date())
//						+ "] 门急诊药房处于盘点状态，门急诊药房自动备药失败！\n");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		return true;
	}

	/**
	 * 出库即入库作业
	 * 
	 * @param out_org_code
	 * @param in_org_code
	 * @param batchvalid
	 */
	private TParm getDispenseOutIn(TParm dispenseM, TParm dispenseD,
			String out_org_code, String in_org_code, String reuprice_flg,
			boolean out_flg, boolean in_flg, TConnection conn) {
		TParm parm = new TParm();
		in_flg = false;
		// 主项信息(OUT_M)
		parm = getDispenseMParm(parm, dispenseM, "3");
		// 细项信息(OUT_D)
		parm = getDispenseDParm(parm, dispenseD);
		// 使用单位
		parm.setData("UNIT_TYPE", u_type);
		// 申请单类型
		parm.setData("REQTYPE_CODE", request_type);
		// 出库部门
		parm.setData("OUT_ORG_CODE", out_org_code);
		// 入库部门
		parm.setData("IN_ORG_CODE", in_org_code);
		// 是否入库(IN_FLG)
		parm.setData("IN_FLG", in_flg);
		// 判断是否自动将成本价存回批发价
		parm.setData("REUPRICE_FLG", reuprice_flg);
		// 批次财务月
		parm.setData("BATCH_DATE", batch_date);
		// 执行数据新增
		// parm = INDTool.getInstance().onInsertDispenseOutIn(parm, conn);
		parm = INDIBSOPDAutoMedTool.getInstance().onInsertDispenseOutIn(parm,
				conn);
		// 保存判断
		return parm;
	}

	/**
	 * 出库在途作业/耗损、其它出库作业、卫耗材、科室备药(出库即入库)
	 */
	private TParm getDispenseOutOn(String org_code, TParm dispenseM,
			TParm dispenseD, TConnection conn) {
		TParm parm = new TParm();
		parm = getDispenseMParm(parm, dispenseM, "1");
		parm = getDispenseDParm(parm, dispenseD);
		// 使用单位
		parm.setData("UNIT_TYPE", u_type);
		// 申请单类型
		parm.setData("REQTYPE_CODE", request_type);
		// 出库部门
		parm.setData("ORG_CODE", org_code);
		// 批次财务月
		parm.setData("BATCH_DATE", batch_date);
		// 执行数据新增
		// parm = INDTool.getInstance().onInsertDispenseOutOn(parm, conn);
		parm = INDIBSOPDAutoMedTool.getInstance().onInsertDispenseOutOn(parm,
				conn);
		// 保存判断
		return parm;
	}

	/**
	 * 获得主项信息
	 * 
	 * @param parm
	 * @return
	 */
	private TParm getDispenseMParm(TParm parm, TParm dispenseM,
			String update_flg) {
		TParm parmM = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		TNull tnull = new TNull(Timestamp.class);
		// 出库单号
		dispense_no = "";
		if ("".equals(dispenseM.getValue("DISPENSE_NO"))) {
			dispense_no = SystemTool.getInstance().getNo("ALL", "IND",
					"IND_DISPENSE", "No");
		} else {
			dispense_no = dispenseM.getValue("DISPENSE_NO");
		}
		parmM.setData("DISPENSE_NO", dispense_no);
		parmM.setData("REQTYPE_CODE", dispenseM.getData("REQTYPE_CODE"));
		parmM.setData("REQUEST_NO", dispenseM.getData("REQUEST_NO"));
		parmM.setData("REQUEST_DATE", dispenseM.getData("REQUEST_DATE"));
		parmM.setData("APP_ORG_CODE", dispenseM.getData("APP_ORG_CODE"));
		parmM.setData("TO_ORG_CODE", dispenseM.getData("TO_ORG_CODE"));
		parmM.setData("URGENT_FLG", dispenseM.getData("URGENT_FLG"));
		parmM.setData("DESCRIPTION", "自动备药");
		parmM.setData("DISPENSE_DATE", requestAndDispenseDate);
		parmM.setData("DISPENSE_USER", opt_user);
		if (!"1".equals(update_flg)) {
			parmM.setData("WAREHOUSING_DATE", date);
			parmM.setData("WAREHOUSING_USER", "127.0.0.1");
		} else {
			parmM.setData("WAREHOUSING_DATE", tnull);
			parmM.setData("WAREHOUSING_USER", "");
		}
		if (dispenseM.getData("REASON_CHN_DESC") == null) {
			parmM.setData("REASON_CHN_DESC", "");
		} else {
			parmM.setData("REASON_CHN_DESC",
					dispenseM.getData("REASON_CHN_DESC"));
		}

		parmM.setData("UNIT_TYPE", u_type);
		if ("WAS".equals(dispenseM.getData("REQTYPE_CODE"))
				|| "THO".equals(dispenseM.getData("REQTYPE_CODE"))
				|| "COS".equals(dispenseM.getData("REQTYPE_CODE"))
				|| "SRD".equals(dispenseM.getData("REQTYPE_CODE"))
				|| "EXM".equals(dispenseM.getData("REQTYPE_CODE"))) {
			update_flg = "3";
		}
		parmM.setData("UPDATE_FLG", update_flg);
		parmM.setData("OPT_USER", opt_user);
		parmM.setData("OPT_DATE", date);
		parmM.setData("OPT_TERM", "127.0.0.1");
		// zhangyong20110517
		parmM.setData("REGION_CODE", "H01");

		parmM.setData("DRUG_CATEGORY", dispenseM.getData("DRUG_CATEGORY"));
		// 申请方式--全部:APP_ALL,人工:APP_ARTIFICIAL,请领建议:APP_PLE,自动拔补:APP_AUTO

		if (parmM != null) {
			parm.setData("OUT_M", parmM.getData());
		}
		return parm;
	}

	/**
	 * 获得明细信息
	 * 
	 * @param parm
	 * @return
	 */
	private TParm getDispenseDParm(TParm parm, TParm dispenseD) {
		TParm parmD = new TParm();
		TParm stockParm = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		TNull tnull = new TNull(Timestamp.class);
		int count = 0;
		String order_code = "";
		Object valid_date = "";
		TParm orderParm = null;
		for (int i = 0; i < dispenseD.getCount("ORDER_CODE"); i++) {
			if (dispenseD.getData("ORDER_CODE", i) != null) {
				order_code = dispenseD.getData("ORDER_CODE", i) + "";
			} else {
				order_code = "";
			}
			//获取药品信息
			stockParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getStockQtyInfo(order_code)));
			stockParm = stockParm.getRow(0);
			parmD.setData("RETAIL_PRICE", count,
					stockParm.getDouble("RETAIL_PRICE"));
			parmD.setData("STOCK_PRICE", count,
					stockParm.getDouble("STOCK_PRICE"));
			parmD.setData("PHA_TYPE", count, stockParm.getData("PHA_TYPE"));
			parmD.setData("DOSAGE_QTY", count,
					stockParm.getDouble("DOSAGE_QTY"));
			parmD.setData("ORDER_DESC", count, stockParm.getData("ORDER_DESC"));
			// 检查库存量
			parmD.setData("DISPENSE_NO", count, dispense_no);
			parmD.setData("SEQ_NO", count, seq + count);
			parmD.setData("REQUEST_SEQ", count, dispenseD.getInt("SEQ_NO", i));

			parmD.setData("ORDER_CODE", count, order_code);
			orderParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getPHAInfoByOrder(order_code)));
			parmD.setData("QTY", count, dispenseD.getDouble("QTY", i));
			parmD.setData("UNIT_CODE", count,
					orderParm.getData("DOSAGE_UNIT", 0));
			parmD.setData("ACTUAL_QTY", count, dispenseD.getDouble("QTY", i));
			parmD.setData("BATCH_SEQ", count, dispenseD.getInt("BATCH_SEQ", i));
			parmD.setData("BATCH_NO", count, dispenseD.getData("BATCH_NO", i));
			valid_date = dispenseD.getData("VALID_DATE", i);
			if (valid_date != null) {
				parmD.setData("VALID_DATE", count, tnull);
			} else {
				parmD.setData("VALID_DATE", count,
						dispenseD.getTimestamp("VALID_DATE", i));
			}

			parmD.setData("OPT_USER", count, opt_user);
			parmD.setData("OPT_DATE", count, date);
			parmD.setData("OPT_TERM", count, "127.0.0.1");

			// 是否下架
			parmD.setData("IS_BOXED", count, "Y");
			parmD.setData("BOXED_USER", count, "");
			parmD.setData("BOX_ESL_ID", count, "");

			// 电子标签应用
			if (dispenseD.getData("ELETAG_CODE", i) == null) {
				parmD.setData("ELETAG_CODE", count, "");
			} else {
				parmD.setData("ELETAG_CODE", count,
						dispenseD.getData("ELETAG_CODE", i));
			}
			count++;
		}
		if (parmD != null) {
			parm.setData("OUT_D", parmD.getData());
		}
		return parm;
	}

	/**
	 * 库存是否可异动状态判断
	 * 
	 * @param org_code
	 * @return
	 */
	private boolean getOrgBatchFlg(String org_code) {
		// 库存是否可异动状态判断
		if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
			return false;
		}
		return true;
	}

	/**
	 * 出入库作业状态判断
	 * 
	 * @return
	 */
	private String getDisCheckFlg(TParm parm) {
		// 出入库作业状态判断
		if ("Y".equals(parm.getValue("DISCHECK_FLG", 0))
				&& !"".equals(to_org_code) && !"".equals(app_org_code)) {
			// 需进行入库确认者且申请单状态入出库部门皆不为空-->在途状态
			return "1";
		} else if ("N".equals(parm.getValue("DISCHECK_FLG", 0))
				&& !"".equals(to_org_code) && !"".equals(app_org_code)) {
			// 不需进行入库确认者且申请单状态入出库部门皆不为空-->出库即入库
			return "2";
		}
		return "1";
	}

	// 保存申请单
	private TParm onSaveRequestAndDispense(TParm res, String type) {
		TConnection connection1 = TDBPoolManager.getInstance().getConnection();
		TParm result = new TParm();
		TParm requestM = res.getParm("RESULT_M", 0);
		TParm requestD = res.getParm("RESULT_D", 0);
		TParm parm = new TParm();
		getRequestExmParmM(parm, requestM);
		getRequestExmParmD(parm, requestD);
		if ("OPD".equals(type)) {
			parm.setData("TYPE", "E");
		} else if ("IBS".equals(type)) {
			parm.setData("TYPE", "I");
		}
		getDeptRequestUpdate(parm, requestM, requestD, type);
		result = onCreateDeptExmRequest(parm, connection1);
		// 保存判断
		if (result == null || result.getErrCode() < 0) {
			// connection1.rollback();
			connection1.close();
			return result;
		} else {
			connection1.commit();
			connection1.close();
		}

		/**** 整理数据 保存出库单 start */
		TConnection connection2 = TDBPoolManager.getInstance().getConnection();
		result = new TParm();
		// 药库参数信息
		TParm sysParm = IndSysParmTool.getInstance().onQuery();
		// 出入库作业状态判断(1-入库确认；2-出库即入库)
		String dis_check = getDisCheckFlg(sysParm);
		// 是否回写购入价格
		String reuprice_flg = sysParm.getValue("REUPRICE_FLG", 0);
		if ("1".equals(dis_check)) {
			// 出库部门库存是否可异动
//			if (!getOrgBatchFlg(to_org_code)) {
//				result.setErrCode(DISPENSE_ERR_CODE);
//				connection2.close();
//				return result;
//			}
			// 出库在途作业/耗损、其它出库作业(出库即入库)
			result = getDispenseOutOn(to_org_code, parm.getParm("REQUEST_M"),
					parm.getParm("REQUEST_D"), connection2);
			if (result == null || result.getErrCode() < 0) {
				connection2.close();
				return result;
			} else {
				connection2.commit();
				connection2.close();
			}
			return result;

		} else if ("2".equals(dis_check)) {
			// 出库部门库存是否可异动
//			if (!getOrgBatchFlg(to_org_code)) {
//				result.setErrCode(DISPENSE_ERR_CODE);
//				connection2.close();
//				return result;
//			}
//			// 入库部门库存是否可异动
//			if (!"".equals(app_org_code) && !getOrgBatchFlg(to_org_code)) {
//				result.setErrCode(DISPENSE_ERR_CODE);
//				connection2.close();
//				return result;
//			}
			// 出库即入库作业(出入库部门均不为空)
			result = getDispenseOutIn(parm.getParm("REQUEST_M"),
					parm.getParm("REQUEST_D"), to_org_code, app_org_code,
					reuprice_flg, out_flg, in_flg, connection2);
			if (result == null || result.getErrCode() < 0) {
				connection2.close();
				return result;
			} else {
				connection2.commit();
				connection2.close();
			}
			return result;
		}
		return result;
		/**** 整理数据 保存出库单 end */
	}

	/**
	 * 整理数据，申请单主项
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getRequestExmParmM(TParm parm, TParm requestM) {
		TParm inparm = new TParm();
		Timestamp date = StringTool.getTimestamp(new Date());
		request_no = SystemTool.getInstance().getNo("ALL", "IND",
				"IND_REQUEST", "No");
		inparm.setData("REQUEST_NO", request_no);
		inparm.setData("REQTYPE_CODE", "EXM");
		inparm.setData("APP_ORG_CODE", app_org_code);
		inparm.setData("TO_ORG_CODE", to_org_code);
		inparm.setData("REQUEST_DATE", requestAndDispenseDate);
		inparm.setData("REQUEST_USER", opt_user);
		inparm.setData("REASON_CHN_DESC", "自动备药");
		inparm.setData("DESCRIPTION", "自动备药");
		inparm.setData("UNIT_TYPE", "1");
		inparm.setData("URGENT_FLG", "N");
		inparm.setData("OPT_USER", opt_user);
		inparm.setData("OPT_DATE", date);
		inparm.setData("OPT_TERM", "127.0.0.1");
		// zhangyong20110517
		inparm.setData("REGION_CODE", "H01");
		// 药品管制
		inparm.setData("DRUG_CATEGORY", "1");
		inparm.setData("APPLY_TYPE", "1");
		parm.setData("REQUEST_M", inparm.getData());
		return parm;
	}

	/**
	 * 整理数据，申请单细项
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getRequestExmParmD(TParm parm, TParm resquestD) {
		TParm inparm = new TParm();
		TNull tnull = new TNull(Timestamp.class);
		Timestamp date = SystemTool.getInstance().getDate();

		int count = 0;
//		for (int i = 0; i < resquestD.getCount("PAT_NAME"); i++) {
//			inparm.addData("REQUEST_NO", request_no);
//			inparm.addData("SEQ_NO", count + 1);
//			inparm.addData("ORDER_CODE", resquestD.getData("ORDER_CODE", i));
//			inparm.addData("BATCH_NO", "");
//			inparm.addData("VALID_DATE", tnull);
//			inparm.addData("QTY", resquestD.getDouble("DOSAGE_QTY", i));
//			inparm.addData("ACTUAL_QTY", 0);
//			inparm.addData("UPDATE_FLG", "0");
//			inparm.addData("OPT_USER", opt_user);
//			inparm.addData("OPT_DATE", date);
//			inparm.addData("OPT_TERM", "127.0.0.1");
//			count++;
//		}
		//modify by wangjc 20171128   汇总相同药品   上面注释掉的是之前的写法
		for (int i = 0; i < resquestD.getCount("PAT_NAME"); i++) {
			boolean flg = true;
			for(int j=0;j<inparm.getCount();j++){
				if(inparm.getValue("ORDER_CODE", j).equals(resquestD.getValue("ORDER_CODE", i))){
					inparm.setData("QTY", j, resquestD.getDouble("DOSAGE_QTY", i)+inparm.getDouble("QTY", j));
					flg = false;
					break;
				}
			}
			if(flg){
				inparm.addData("REQUEST_NO", request_no);
				inparm.addData("SEQ_NO", count + 1);
				inparm.addData("ORDER_CODE", resquestD.getData("ORDER_CODE", i));
				inparm.addData("BATCH_NO", "");
				inparm.addData("VALID_DATE", tnull);
				inparm.addData("QTY", resquestD.getDouble("DOSAGE_QTY", i));
				inparm.addData("ACTUAL_QTY", 0);
				inparm.addData("UPDATE_FLG", "0");
				inparm.addData("OPT_USER", opt_user);
				inparm.addData("OPT_DATE", date);
				inparm.addData("OPT_TERM", "127.0.0.1");
				count++;
				inparm.setCount(count);
			}
		}
		parm.setData("REQUEST_D", inparm.getData());
		return parm;
	}

	/**
	 * 整理数据，更新申请状态
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getDeptRequestUpdate(TParm parm, TParm requestM,
			TParm requestD, String type) {
		TParm inparm = new TParm();
		int count = 0;
		if ("IBS".equals(type)) {
			for (int i = 0; i < requestD.getCount("PAT_NAME"); i++) {
				inparm.setData("CASE_NO", count,
						requestD.getValue("CASE_NO", i));
				inparm.setData("CASE_NO_SEQ", count,
						requestD.getInt("CASE_NO_SEQ", i));
				inparm.setData("SEQ_NO", count, requestD.getInt("SEQ_NO", i));
				inparm.setData("REQUEST_FLG", count, "Y");
				inparm.setData("REQUEST_NO", count, request_no);
				count++;
			}
		} else if ("OPD".equals(type)) {
			for (int i = 0; i < requestD.getCount("PAT_NAME"); i++) {
				inparm.setData("CASE_NO", count,
						requestD.getValue("CASE_NO", i));
				inparm.setData("RX_NO", count, requestD.getValue("RX_NO", i));
				inparm.setData("SEQ_NO", count, requestD.getInt("SEQ_NO", i));
				inparm.setData("REQUEST_FLG", count, "Y");
				inparm.setData("REQUEST_NO", count, request_no);
				count++;
			}
		}
		parm.setData("UPDATE", inparm.getData());
		return parm;
	}

	/**
	 * 生成请领单
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onCreateDeptExmRequest(TParm parm, TConnection conn) {
		// 数据检核
		if (parm == null)
			return null;
		// 结果集
		TParm result = new TParm();
		// 新增申请单主项
		TParm requestM = parm.getParm("REQUEST_M");
		result = IndRequestMTool.getInstance().onInsert(requestM, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 新增请领单细项
		TParm requestD = parm.getParm("REQUEST_D");
//		System.out.println("requestD>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+requestD);
		String order_code = "";
		for (int i = 0; i < requestD.getCount("REQUEST_NO"); i++) {
			TParm inparm = new TParm();
			inparm.setData("REQUEST_NO", requestD.getData("REQUEST_NO", i));
			inparm.setData("SEQ_NO", requestD.getData("SEQ_NO", i));
			order_code = requestD.getValue("ORDER_CODE", i);
			inparm.setData("ORDER_CODE", order_code);
			inparm.setData("BATCH_NO", requestD.getData("BATCH_NO", i));
			inparm.setData("VALID_DATE", requestD.getData("VALID_DATE", i));
			inparm.setData("QTY", requestD.getData("QTY", i));
			TParm orderParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getPHAInfoByOrder(order_code)));
			inparm.setData("UNIT_CODE", orderParm.getData("DOSAGE_UNIT", 0));
			inparm.setData("RETAIL_PRICE", orderParm.getData("RETAIL_PRICE", 0));
			inparm.setData("STOCK_PRICE", orderParm.getData("STOCK_PRICE", 0));
			inparm.setData("ACTUAL_QTY", requestD.getData("ACTUAL_QTY", i));
			inparm.setData("UPDATE_FLG", requestD.getData("UPDATE_FLG", i));
			inparm.setData("OPT_USER", requestD.getData("OPT_USER", i));
			inparm.setData("OPT_DATE", requestD.getData("OPT_DATE", i));
			inparm.setData("OPT_TERM", requestD.getData("OPT_TERM", i));
			result = IndRequestDTool.getInstance().onInsert(inparm, conn);
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		// 更新门急住状态
		String type = parm.getValue("TYPE");
		TParm update = parm.getParm("UPDATE");
		if ("I".equals(type)) {
			// 更新住院状态
			String caseNo = "";
			int caseNoSeq = 0;
			int seqNo = 0;
			String requestFlg = "";
			String requestNo = "";
			for (int i = 0; i < update.getCount("CASE_NO"); i++) {
				caseNo = update.getValue("CASE_NO", i);
				caseNoSeq = update.getInt("CASE_NO_SEQ", i);
				seqNo = update.getInt("SEQ_NO", i);
				requestFlg = update.getValue("REQUEST_FLG", i);
				requestNo = update.getValue("REQUEST_NO", i);
				result = IBSOrderdTool.getInstance().upForDeptMedic(caseNo,
						caseNoSeq, seqNo, requestFlg, requestNo, conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		} else if ("O".equals(type) || "E".equals(type)) {
			// 更新门急诊状态
			String caseNo = "";
			String rxNo = "";
			int seqNo = 0;
			String requestFlg = "";
			String requestNo = "";
			String sql = "";
			for (int i = 0; i < update.getCount("CASE_NO"); i++) {
				caseNo = update.getValue("CASE_NO", i);
				rxNo = update.getValue("RX_NO", i);
				seqNo = update.getInt("SEQ_NO", i);
				requestFlg = update.getValue("REQUEST_FLG", i);
				requestNo = update.getValue("REQUEST_NO", i);
				sql = "UPDATE OPD_ORDER SET REQUEST_NO='" + requestNo
						+ "' ,REQUEST_FLG='" + requestFlg + "' WHERE CASE_NO='"
						+ caseNo + "' AND RX_NO='" + rxNo + "' AND SEQ_NO="
						+ seqNo;
				result = new TParm(TJDODBTool.getInstance().update(sql, conn));
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		return result;
	}

	/**
	 * 生成请领单，传给物联网[科室备药生成]
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onCreateDeptExmRequestSpc(TParm parm, TConnection conn) {
		TParm result = new TParm();
		// TConnection connection =
		// TDBPoolManager.getInstance().getConnection();
		result = INDTool.getInstance().onCreateDeptExmRequest(parm, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		TParm reqParm = parm.getParm("REQUEST_M");
		SpcIndRequestm indRequestm = getInsertM(reqParm);
		// System.out.println("reqParm---------:"+reqParm);
		TParm reqDParm = parm.getParm("REQUEST_D");
		// System.out.println("reqDParm---------:"+reqDParm);
		indRequestm = getInsertD(indRequestm, reqDParm);
		try {
			String out = SpcService_SpcServiceImplPort_Client
					.onSaveSpcRequest(indRequestm);

			if (out != null && !out.equals("success")) {
				result.setErrCode(-1);
				result.setErrText(out);

				return result;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

			result.setErrCode(-1);
			result.setErrText("调用服务端保存失败");
			// conn.close();
			return result;
		}
		return result;
	}

	/**
	 * 组装传给物联网的IND_REQUESTM表数据
	 * 
	 * @param obj
	 * @return
	 */
	private SpcIndRequestm getInsertM(TParm parm) {
		SpcIndRequestm obj = new SpcIndRequestm();
		obj.setReqtypeCode(parm.getValue("REQTYPE_CODE"));
		obj.setAppOrgCode(parm.getValue("APP_ORG_CODE"));
		obj.setToOrgCode(parm.getValue("TO_ORG_CODE"));
		obj.setRequestDate(parm.getValue("REQUEST_DATE"));
		obj.setRequestUser(parm.getValue("REQUEST_USER"));
		obj.setReasonChnDesc(parm.getValue("REASON_CHN_DESC"));
		obj.setDescription(parm.getValue("DESCRIPTION"));

		obj.setUnitType(parm.getValue("UNIT_TYPE"));
		obj.setUrgentFlg(parm.getValue("URGENT_FLG"));
		obj.setOptUser(parm.getValue("OPT_USER"));

		obj.setOptDate(parm.getValue("OPT_DATE"));
		obj.setOptTerm(parm.getValue("OPT_TERM"));
		obj.setDrugCategory(parm.getValue("DRUG_CATEGORY"));
		obj.setRequestNo(parm.getValue("REQUEST_NO"));
		obj.setRegionCode(parm.getValue("REGION_CODE"));
		obj.setReqtypeCode(parm.getValue("REQTYPE_CODE"));
		obj.setApplyType("1");
		return obj;
	}

	/**
	 * 组装传给物联网的IND_REQUESTD表数据
	 * 
	 * @param obj
	 * @return
	 */
	private SpcIndRequestm getInsertD(SpcIndRequestm obj, TParm parm) {

		int count = parm.getCount("ORDER_CODE");
		List<SpcIndRequestd> list = new ArrayList<SpcIndRequestd>();
		for (int i = 0; i < count; i++) {
			TParm rowParm = parm.getRow(i);
			SpcIndRequestd objD = new SpcIndRequestd();

			String order_code = rowParm.getValue("ORDER_CODE");
			objD.setOrderCode(order_code);

			TParm orderParm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getPHAInfoByOrder(order_code)));

			String unitCode = (String) orderParm.getData("DOSAGE_UNIT", 0);
			double retailPrice = orderParm.getDouble("RETAIL_PRICE", 0);
			double stockPrice = orderParm.getDouble("STOCK_PRICE", 0);

			objD.setUnitCode(unitCode);
			objD.setStockPrice(stockPrice);
			objD.setRetailPrice(retailPrice);

			objD.setQty(rowParm.getDouble("QTY"));
			objD.setActualQty(rowParm.getDouble("ACTUAL_QTY"));
			objD.setRequestNo(rowParm.getValue("REQUEST_NO"));
			objD.setSeqNo(rowParm.getInt("SEQ_NO"));
			objD.setBatchNo("");
			objD.setValidDate("");

			objD.setUpdateFlg(rowParm.getValue("UPDATE_FLG"));
			objD.setOptDate(rowParm.getValue("OPT_DATE"));
			objD.setOptTerm(rowParm.getValue("OPT_TERM"));
			objD.setOptUser(rowParm.getValue("OPT_USER"));
			list.add(objD);
		}
		obj.setIndRequestds(list);
		return obj;
	}
}
