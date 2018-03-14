package jdo.adm;

import com.dongyang.jdo.TJDOTool;

import java.sql.Timestamp;
import java.util.Date;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.SYSBedTool;
import com.dongyang.jdo.TJDODBTool;

import jdo.ibs.IBSOrdermTool;
import jdo.mro.MROTool;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import jdo.mro.MRORecordTool;
import jdo.mro.MROQueueTool;

/**
 * <p>
 * Title: ADM 业务tool
 * </p>
 *
 * <p>
 * Description: ADM 业务tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 *
 * <p>
 * Company: Javahis
 * </p>
 *
 * @author JiaoY 2009.05.14
 * @version 1.0
 */
public class ADMTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static ADMTool instanceObject;

	/**
	 * 得到实例
	 *
	 * @return SchWeekTool
	 */
	public static ADMTool getInstance() {
		if (instanceObject == null)
			instanceObject = new ADMTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public ADMTool() {
		setModuleName("");
		onInit();
	}

	/**
	 * 出科
	 *
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm ADM_OUT_DEPT(TParm parm, TConnection conn) {
		TParm result = new TParm();
		// 出科保存
		result = this.ADM_OUT_DEPT_SAVE(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			result.setData("CHECK", "T");
			return result;
		}
		// 修改病案首页的转科科室字段
		TParm mro = new TParm();
		// mro.setData("TRANS_DEPT",parm.getData("IN_DEPT_CODE"));
		mro.setData("OUT_DEPT", parm.getData("IN_DEPT_CODE"));// 当前所在科室
		mro.setData("OUT_STATION", parm.getData("IN_STATION_CODE"));// 当前所在病区
		mro.setData("OUT_ROOM_NO", "");// 当前所在病房(因为不知道转入到哪个病床，所以病房号修改为空)
		mro.setData("OPT_USER", parm.getData("OPT_USER"));
		mro.setData("OPT_TERM", parm.getData("OPT_TERM"));
		mro.setData("CASE_NO", parm.getData("CASE_NO"));
		result = MROTool.getInstance().updateTransDept(mro, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			result.setData("CHECK", "T");
			return result;
		}
		result.setData("CHECK", "T");
		return result;
	}

	/**
	 * 出科保存
	 *
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm ADM_OUT_DEPT_SAVE(TParm parm, TConnection conn) {
		TParm result = new TParm();

		// 更新在院病患信息
		TParm admInp = new TParm();
		// 清床
		TParm clearBed = new TParm();
		// 插入转科表记录
		TParm tranWait = new TParm();
		
		//插入病患动态档   出床
		TParm admChg0 = new TParm();
		// 插入病患动态档 出科
		TParm admChg1 = new TParm();
		// 插入病患动态档 入科
		TParm admChg2 = new TParm();

		// 更新ADM_INP
		admInp.setData("DEPT_CODE", parm.getData("OUT_DEPT_CODE"));
		admInp.setData("STATION_CODE", parm.getData("OUT_STATION_CODE"));
		admInp.setData("CASE_NO", parm.getData("CASE_NO"));
		admInp.setData("OPT_USER", parm.getData("OPT_USER"));
		admInp.setData("OPT_TERM", parm.getData("OPT_TERM"));
		result = ADMInpTool.getInstance().updateForOutDept(admInp, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 请床
		clearBed.setData("CASE_NO", parm.getData("CASE_NO"));
		result = SYSBedTool.getInstance().clearAllForadmin(clearBed, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
				
		// 插入ADM_CHG(病患动态档)
		TParm seqMax = new TParm();
		seqMax.setData("CASE_NO", parm.getData("CASE_NO"));
		// 获取排序号的最大号
		TParm seqParm = ADMChgTool.getInstance().ADMQuerySeq(seqMax);
		String seq = seqParm.getData("SEQ_NO", 0).toString();
		
		//插入ADM_CHG(病患动态档) 出床
		admChg0.setData("CASE_NO", parm.getData("CASE_NO"));
		admChg0.setData("SEQ_NO", seq);
		admChg0.setData("IPD_NO", parm.getData("IPD_NO"));
		admChg0.setData("MR_NO", parm.getData("MR_NO"));
		admChg0.setData("PSF_KIND", "OUBD");// 转出床
		admChg0.setData("PSF_HOSP", "");
		admChg0.setData("CANCEL_FLG", "N");
		admChg0.setData("CANCEL_DATE", "");
		admChg0.setData("CANCEL_USER", "");
		admChg0.setData("DEPT_CODE", parm.getData("OUT_DEPT_CODE"));
		admChg0.setData("STATION_CODE", parm.getData("OUT_STATION_CODE"));
		admChg0.setData("BED_NO", parm.getData("BED_NO"));
		admChg0.setData("VS_CODE_CODE", "");
		admChg0.setData("ATTEND_DR_CODE", "");
		admChg0.setData("DIRECTOR_DR_CODE", "");
		admChg0.setData("OPT_USER", parm.getData("OPT_USER"));
		admChg0.setData("OPT_TERM", parm.getData("OPT_TERM"));
		admChg0.setData("REGION_CODE", parm.getData("REGION_CODE"));
		result = ADMChgTool.getInstance().insertAdmChg(admChg0, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		
		// 插入出科记录
		seq = (Integer.parseInt(seq) + 1) + "";
		admChg1.setData("CASE_NO", parm.getData("CASE_NO"));
		admChg1.setData("SEQ_NO", seq);
		admChg1.setData("IPD_NO", parm.getData("IPD_NO"));
		admChg1.setData("MR_NO", parm.getData("MR_NO"));
		admChg1.setData("PSF_KIND", "OUDP");// 转出科
		admChg1.setData("PSF_HOSP", "");
		admChg1.setData("CANCEL_FLG", "N");
		admChg1.setData("CANCEL_DATE", "");
		admChg1.setData("CANCEL_USER", "");
		admChg1.setData("DEPT_CODE", parm.getData("OUT_DEPT_CODE"));
		admChg1.setData("STATION_CODE", parm.getData("OUT_STATION_CODE"));
		admChg1.setData("BED_NO", parm.getData("BED_NO"));
		admChg1.setData("VS_CODE_CODE", "");
		admChg1.setData("ATTEND_DR_CODE", "");
		admChg1.setData("DIRECTOR_DR_CODE", "");
		admChg1.setData("OPT_USER", parm.getData("OPT_USER"));
		admChg1.setData("OPT_TERM", parm.getData("OPT_TERM"));
		// ===========pangben modify 20110617 start
		admChg1.setData("REGION_CODE", parm.getData("REGION_CODE"));
		result = ADMChgTool.getInstance().insertAdmChg(admChg1, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 插入 入科记录
		// 重新获取最大号
		seq = (Integer.parseInt(seq) + 1) + "";
		admChg2.setData("CASE_NO", parm.getData("CASE_NO"));
		admChg2.setData("SEQ_NO", seq);
		admChg2.setData("IPD_NO", parm.getData("IPD_NO"));
		admChg2.setData("MR_NO", parm.getData("MR_NO"));
		admChg2.setData("PSF_KIND", "INDP");// 转出科
		admChg2.setData("PSF_HOSP", "");
		admChg2.setData("CANCEL_FLG", "N");
		admChg2.setData("CANCEL_DATE", "");
		admChg2.setData("CANCEL_USER", "");
		admChg2.setData("DEPT_CODE", parm.getData("IN_DEPT_CODE"));
		admChg2.setData("STATION_CODE", parm.getData("IN_STATION_CODE"));
		admChg2.setData("BED_NO", "");
		admChg2.setData("VS_CODE_CODE", "");
		admChg2.setData("ATTEND_DR_CODE", "");
		admChg2.setData("DIRECTOR_DR_CODE", "");
		admChg2.setData("OPT_USER", parm.getData("OPT_USER"));
		admChg2.setData("OPT_TERM", parm.getData("OPT_TERM"));
		// ===========pangben modify 20110617 start
		admChg2.setData("REGION_CODE", parm.getData("REGION_CODE"));
		result = ADMChgTool.getInstance().insertAdmChg(admChg2, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 更新ADM_TRANS_LOG
		TParm transLogParm = ADMTransLogTool.getInstance()
				.getTranDeptData(parm);
		TParm updatetransLogParm = new TParm();
		updatetransLogParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		updatetransLogParm.setData("IN_DATE",
				transLogParm.getValue("IN_DATE", 0));
		updatetransLogParm.setData("OUT_DATE", StringTool.getTimestamp(parm
				.getValue("OUT_DATE").substring(0, 19), "yyyy-MM-dd HH:mm:ss"));
		updatetransLogParm.setData("OUT_DEPT_CODE", parm.getValue("IN_DEPT_CODE"));
		updatetransLogParm.setData("OUT_STATION_CODE", parm.getValue("IN_STATION_CODE"));
		updatetransLogParm.setData("PSF_KIND", "OUDP");
		updatetransLogParm.setData("DEPT_ATTR_FLG", parm.getValue("DEPT_ATTR_FLG"));
		updatetransLogParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		updatetransLogParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		result = ADMTransLogTool.getInstance().updateAdm(updatetransLogParm,
				conn); // 插入ADM_Trans_Log
		if (result.getErrCode() < 0) {
			return result;
		}
		// 插入ADM_TRANSWAIT
		tranWait.setRowData(parm);
		result = ADMWaitTransTool.getInstance()
				.saveForInOutDept(tranWait, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 生成中结账单
		result = ADMInpTool.getInstance().insertIBSBiilData(parm, "N", conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * 取消住院
	 *
	 * @param parm
	 *            TParm 传参 case_no;PSF_KIND
	 *            ;OPT_USER;OPT_TERM;PSF_HOSP;CANCEL_FLG;CANCEL_DATE;CANCEL_USER
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm ADM_CANCEL_INP(TParm parm, TConnection conn) {
		TParm clearBed = new TParm();
		clearBed.setData("CASE_NO", parm.getValue("CASE_NO"));
		clearBed.setData("OPT_USER", parm.getValue("OPT_USER"));
		clearBed.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		TParm result = new TParm();
		// 更新ADM_INP
		TParm inpData = new TParm();
		inpData.setRowData(parm);
		result = ADMInpTool.getInstance().updateForCancel(inpData, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 插入ADM_CHG
		TParm chgData = new TParm();
		chgData.setRowData(parm);
		// =========pangben modify 20110617 start 添加区域参数
		chgData.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		result = ADMChgTool.getInstance().insertChg(chgData, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 删除ADM_WAIT_TRANS
		TParm transData = new TParm();
		transData.setRowData(parm);
		result = ADMWaitTransTool.getInstance().deleteIn(transData, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 清除床位信息
		result = SYSBedTool.getInstance().clearForAdm(clearBed, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 清除转科信息
		result = ADMTransLogTool.getInstance().deleteAdmData(clearBed, conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 删除病案首页信息
		result = MRORecordTool.getInstance().deleteMRO(
				parm.getValue("CASE_NO"), conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		// 取消该CASE_NO的借阅病历
		TParm queueParm = new TParm();
		queueParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		queueParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		queueParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		result = MROQueueTool.getInstance().cancelQueueByCASE_NO(queueParm,
				conn);
		if (result.getErrCode() < 0) {
			return result;
		}
		return result;
	}

	/**
	 * 插入床日记录档 传参 CASE_NO,MR_NO,IPD_NO,DEPT_CODE,STATION_CODE,BED_NO,VS_DR_CODE,
	 * NURSING_CLASS,PATIENT_CONDITITION
	 *
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	// public TParm INSERT_ADM_DAILY_REC(TParm parm,TConnection conn){
	// TParm result = new TParm();
	// //插入前删除当天已有的记录
	// TParm checkRec = new TParm();
	// checkRec.setRowData(parm);
	// result = ADMDailyRecTool.getInstance().delDailRec(checkRec,conn);
	// if(result.getErrCode()<0){
	// conn.close();
	// return result ;
	// }
	// //插入记录档
	// TParm insertRec = new TParm();
	// insertRec.setRowData(parm);
	// result = ADMChgTool.getInstance().MROQueryChgLog(parm);
	// if (result.getErrCode() < 0) {
	// conn.close();
	// return result;
	// }
	// return result;
	// }
	/**
	 * 传CASE_NO查询住院天
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm QUERY_ADM_DAYS(TParm parm) {
		TParm result = new TParm();
		TParm data = ADMInpTool.getInstance().selectall(parm);
		if (data.getErrCode() < 0) {
			err("ERR:" + data.getErrCode() + data.getErrText()
					+ data.getErrName());
			return data;
		}
		result.setData("ADM_DAYS", data.getData("ADM_DAYS", 0));
		return result;
	}

	/**
	 * 根据CASE_NO查询一条住院病患信息
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getADM_INFO(TParm parm) {
		TParm result = ADMInpTool.getInstance().selectall(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据CASE_NO获得该病患的实际住院天数
	 *
	 * @param CASE_NO
	 *            String
	 * @return int
	 */
	public int getAdmDays(String CASE_NO) {
		int days = 0;
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm result = ADMInpTool.getInstance().selectall(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return 0;
		}
		// 无此病患信息 返回0
		if (result.getCount("CASE_NO") <= 0) {
			return 0;
		}
		// 注意 住院天数算入不算出 出院当天不算住院日
		// 如果病人已经出院用出院日期-入院日期
		if (result.getData("DS_DATE", 0) != null) {
			days = StringTool.getDateDiffer(StringTool.getTimestampDate(result
					.getTimestamp("DS_DATE", 0)), StringTool
					.getTimestampDate(result.getTimestamp("IN_DATE", 0)));
			days = days == 0 ? 1 : days;
		}
		// 如果病人在院 当前日期-入院日期
		else {
			days = StringTool.getDateDiffer(StringTool
					.getTimestampDate(SystemTool.getInstance().getDate()),
					StringTool.getTimestampDate(result.getTimestamp("IN_DATE",
							0)));
			days = days == 0 ? 1 : days;
		}
		return days;
	}

	/**
	 * 修改病患身高 体重 传参 CASE_NO,WEIGHT,HEIGHT,OPT_USER,OPT_ITEM
	 *
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm UPDATE_HIGH_WEIGHT(TParm parm, TConnection conn) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "参数异常!");
			return result;
		}
		result = ADMInpTool.getInstance().upDateWeightHigh(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.close();
			return result;
		}
		return result;
	}

	/**
	 * 检核是否允许取消住院 TRUE 允许取消 false 不允许
	 *
	 * @param parm
	 *            TParm
	 * @return boolean
	 */     
	public boolean checkCancelOutInp(TParm parm) {
		boolean check = true;
		// fux modify 20150731 只要产生费用就不能取消住院  
		// 检核是否产生费用
//		 TParm checkBill = new TParm();  
//		 checkBill.setRowData(parm);                           
//		 check=IBSOrdermTool.getInstance().existFee(checkBill);
//		 if(!check){//产生费用
//		 return false;    
//		 }
		// 检核病患是否已经入住到床上
		TParm checkBed = new TParm();
		checkBed.setRowData(parm);
		TParm result = SYSBedTool.getInstance().checkInBed(checkBed);
		int count = result.getCount("BED_STATUS");
		if (count == -1 || count == 0)  
			return true;
		// 如果已经入住到床上那么不允许取消住院 因为一旦入住到床上就应该在出院时产生床位费用
		for (int i = 0; i < count; i++) {
			if (result.getData("BED_STATUS", i) == null
					|| "".equals(result.getData("BED_STATUS", i))
					|| "0".equals(result.getData("BED_STATUS", i))) {
				check = true;
			} else {
				check = false;
				break;
			}
		}

		return check;
	}

	/**
	 * 住院登记修改
	 *
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updataAdmInp(TParm parm, TConnection conn) {
		TParm result = new TParm();
		// 修改床位号
		if ("Y".equals(parm.getData("UPDATE_BED"))) {
			// 删除待转档
			TParm waitDelParm = new TParm();
			waitDelParm.setRowData(parm);
			result = ADMWaitTransTool.getInstance().deleteIn(waitDelParm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}

			// 插入代转病患档
			TParm waitParm = new TParm();
			waitParm.setRowData(parm);
			result = ADMWaitTransTool.getInstance().saveForInp(waitParm, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			// 清床
			TParm clearBed = new TParm();
			clearBed.setRowData(parm);
			result = SYSBedTool.getInstance().clearAllForadmin(clearBed, conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			// 更新床位档
			TParm bedParm = new TParm();
			bedParm.setRowData(parm);

			if (!bedParm.getData("BED_NO").equals("")) {
				bedParm.setData("APPT_FLG", "Y");
				bedParm.setData("ALLO_FLG", "Y");
				bedParm.setData("BED_STATUS", "0");
				result = SYSBedTool.getInstance().upDate(bedParm, conn);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
			}
		}
		// 修改黄色警戒，红色警戒,入院日期，住院次数,床位号
		TParm updata = new TParm();
		updata.setRowData(parm);
		result = ADMInpTool.getInstance().updateForAdmInp(updata, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}

		return result;
	}

	/**
	 * 根据婴儿的CASE_NO
	 *
	 * @param parm
	 *            TParm 必须参数 CASE_NO(婴儿CASE_NO)
	 * @return TParm 返回母亲的CASE_NO，IPD_NO，MR_NO，PAT_NAME
	 */
	public TParm getMotherInfo(TParm parm) {
		String sql = "SELECT A.CASE_NO, A.IPD_NO, A.MR_NO, B.PAT_NAME "
				+ " FROM ADM_INP A, SYS_PATINFO B "
				+ " WHERE CASE_NO = (SELECT M_CASE_NO " + " FROM ADM_INP "
				+ " WHERE CASE_NO = '" + parm.getValue("CASE_NO") + "') "
				+ " AND A.MR_NO = B.MR_NO";
		TParm result = new TParm();
		result.setData(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据CASE_NO查询某一病人的动态记录（MRO病案首页使用）
	 *
	 * @param parm
	 *            TParm 必须参数：CASE_NO
	 * @return TParm
	 */
	public TParm queryChgForMRO(TParm parm) {
		TParm result = ADMChgTool.getInstance().queryChgForMRO(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据CASE_NO查询某一病患的所有诊断信息（病案首页使用）
	 *
	 * @param parm
	 *            TParm 必须参数：CASE_NO
	 * @return TParm
	 */
	public TParm queryDiagForMro(TParm parm) {
		TParm result = ADMDiagTool.getInstance().queryDiagForMro(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 修改adm_inp最新诊断
	 *
	 * @param parm
	 *            TParm 参数列表 MAINDIAG:最新诊断 （出院主诊断>入院主诊断>门急诊主诊断） CASE_NO:就诊序号
	 *            OPT_USER:操作员 OPT_TERM:操作终端
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateNewDaily(TParm parm, TConnection conn) {
		TParm result = ADMInpTool.getInstance().updateNewDaily(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询诊断全字段
	 *
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryDailyData(TParm parm) {
		TParm result = ADMDiagTool.getInstance().queryData(parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 修改医疗总金额
	 *
	 * @param TOTAL
	 *            String 金额 保留两位小数
	 * @param CASE_NO
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateTOTAL_AMT(String TOTAL, String CASE_NO, TConnection conn) {
		String sql = "UPDATE ADM_INP SET TOTAL_AMT='" + TOTAL
				+ "' WHERE CASE_NO='" + CASE_NO + "'";
		TParm result = new TParm();
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 修改预交金字段
	 *
	 * @param TOTAL
	 *            String 金额 保留两位小数
	 * @param CASE_NO
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateTOTAL_BILPAY(String TOTAL, String CASE_NO,
			TConnection conn) {
		String sql = "UPDATE ADM_INP SET TOTAL_BILPAY='" + TOTAL
				+ "' WHERE CASE_NO='" + CASE_NO + "'";
		TParm result = new TParm();
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 修改“目前余额”字段
	 *
	 * @param TOTAL
	 *            String 金额 保留两位小数
	 * @param CASE_NO
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateCUR_AMT(String TOTAL, String CASE_NO, TConnection conn) {
		String sql = "UPDATE ADM_INP SET CUR_AMT='" + TOTAL
				+ "' WHERE CASE_NO='" + CASE_NO + "'";
		TParm result = new TParm();
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 修改“绿色通道”字段
	 *
	 * @param TOTAL
	 *            String 金额 保留两位小数
	 * @param CASE_NO
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateGREENPATH_VALUE(String TOTAL, String CASE_NO,
			TConnection conn) {
		String sql = "UPDATE ADM_INP SET GREENPATH_VALUE='" + TOTAL
				+ "' WHERE CASE_NO='" + CASE_NO + "'";
		TParm result = new TParm();
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 修改财务状态
	 *
	 * @param status
	 *            String 账务状态: 1 出院无账单 2 出院未缴费 3 医保已审核 4 出院已结算
	 * @param CASE_NO
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateBillStatus(String status, String CASE_NO,
			TConnection conn) {
		String sql = "UPDATE ADM_INP SET BILL_STATUS='" + status
				+ "' WHERE CASE_NO='" + CASE_NO + "'";
		TParm result = new TParm();
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 修改停止费用标记
	 *
	 * @param FLG
	 *            String （Y：停止费用；N：不停止费用）
	 * @param CASE_NO
	 *            String
	 * @return TParm
	 */
	public TParm updateStopBillFlg(String FLG, String CASE_NO) {
		String sql = "UPDATE ADM_INP SET STOP_BILL_FLG='" + FLG
				+ "' WHERE CASE_NO='" + CASE_NO + "'";
//		System.out.println("修改停止费用标记" + sql);
		TParm result = new TParm();
		result.setData(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 校验停止划价
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	public TParm checkStopFee(String caseNo) {
		TParm result = new TParm();
		TParm inSelADMAllData = new TParm();
		inSelADMAllData.setData("CASE_NO", caseNo);
		TParm selADMAllData = ADMInpTool.getInstance().selectall(
				inSelADMAllData);
		// System.out.println("停止划价ADM数据》》》》》" + selADMAllData);
		double totAmt3 = 0.00;
		double totAmt2 = 0.00;
		Timestamp date = StringTool.getTimestamp(new Date());
		String endDate = date.toString().substring(0, 19).replace("-", "");
		// 取红黄警戒线
		String sqlSign = "SELECT YELLOW_SIGN,RED_SIGN FROM ODI_SYSPARM ";
		TParm resultSign = new TParm(TJDODBTool.getInstance().select(sqlSign));
		// 身份比例
		String sqlD = "SELECT A.CASE_NO,CASE WHEN B.DISCOUNT_RATE IS NULL THEN 1 ELSE B.DISCOUNT_RATE END "
				+ "DISCOUNT_RATE "
				+ "FROM ADM_INP A, SYS_CTZ_REBATE B "
				+ "WHERE A.CTZ1_CODE=B.CTZ_CODE(+) "
				+ "AND A.CASE_NO='"
				+ caseNo + "' ";
		TParm resultD = new TParm(TJDODBTool.getInstance().select(sqlD));
		// 消费总金额
		String sql2 = "SELECT SUM(D.TOT_AMT) TOT_AMT FROM ADM_INP A,IBS_ORDD D,SYS_CTZ Z "
				+ "WHERE A.CASE_NO=D.CASE_NO "
				+ "AND A.CASE_NO='"
				+ caseNo
				+ "' "
				+ "AND A.CTZ1_CODE = Z.CTZ_CODE "
				+ "AND Z.MAIN_CTZ_FLG = 'Y' "
				// + "AND Z.NHI_CTZ_FLG = 'N' "
				+ "AND D.BILL_DATE BETWEEN A.IN_DATE "
				+ "AND TO_DATE('"
				+ endDate + "','YYYY/MM/DD HH24:MI:SS')";
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		if (result2.getCount() > 0
				&& result2.getValue("TOT_AMT", 0).length() > 0) {
			totAmt2 = StringTool.round(result2.getDouble("TOT_AMT", 0), 2);
		}

		// 预交金
		String sql3 = " SELECT CASE_NO, SUM (PRE_AMT) PRE_AMT "
				+ "FROM BIL_PAY WHERE REFUND_FLG = 'N' "
				+ "AND TRANSACT_TYPE IN ('01', '03', '04') "
				+ "AND CHARGE_DATE < TO_DATE ('" + endDate
				+ "','YYYY/MM/DD HH24:MI:SS') " + "AND CASE_NO = '" + caseNo
				+ "' GROUP BY CASE_NO";
		TParm result3 = new TParm(TJDODBTool.getInstance().select(sql3));
		if (result3.getCount() > 0
				&& result3.getValue("PRE_AMT", 0).length() > 0) {
			totAmt3 = StringTool.round(result3.getDouble("PRE_AMT", 0), 2);
		}
		double redSign = resultSign.getData("RED_SIGN", 0) == null ? 0.00
				: resultSign.getDouble("RED_SIGN", 0); // 红色警戒

		double greenPath = selADMAllData.getData("GREENPATH_VALUE", 0) == null ? 0.00
				: selADMAllData.getDouble("GREENPATH_VALUE", 0); // 绿色通道

		if (greenPath
				+ (totAmt3 - (totAmt2 * resultD.getDouble("DISCOUNT_RATE", 0))) <= redSign) {
			// result = this.updateStopBillFlg("Y", caseNo); // 更新停止划价注记
		} else {
			result = this.updateStopBillFlg("N", caseNo); // 更新停止划价注记
		}
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 护士执行更新三级检诊 更新“护理等级”字段(住院护士站使用)
	 *
	 * @param parm
	 *            TParm 参数：NURSING_CLASS:护理等级 CASE_NO:就诊序号
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateNURSING_CLASS(TParm parm, TConnection conn) {
		TParm result = ADMInpTool.getInstance().updateNURSING_CLASS(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 护士执行更新三级检诊 更新“病情状态”字段(住院护士站使用)
	 *
	 * @param parm
	 *            TParm 参数：PATIENT_STATUS:护理等级 CASE_NO:就诊序号
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updatePATIENT_STATUS(TParm parm, TConnection conn) {
		TParm result = ADMInpTool.getInstance()
				.updatePATIENT_STATUS(parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 出科更新ADM_INP中bill_date
	 *
	 * @param CASE_NO
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateBillDate(String CASE_NO, TConnection conn) {
		String sql = "UPDATE ADM_INP SET BILL_DATE= SYSDATE WHERE CASE_NO='"
				+ CASE_NO + "'";
		TParm result = new TParm();
		result.setData(TJDODBTool.getInstance().update(sql, conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 出科更新ADM_INP中charge_date
	 *
	 * @param CASE_NO
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
        public TParm updateChargeDate(String CASE_NO, TConnection conn) {
                String sql = "UPDATE ADM_INP SET CHARGE_DATE=SYSDATE WHERE CASE_NO='"
                                + CASE_NO + "'";
                TParm result = new TParm();
                result.setData(TJDODBTool.getInstance().update(sql, conn));
                if (result.getErrCode() < 0) {
                        err("ERR:" + result.getErrCode() + result.getErrText()
                                        + result.getErrName());
                        return result;
                }
                return result;
	}

	/**
	 * 
	 * @Title: updateAdmTotAmt
	 * @Description: TODO(修改ADM_INP表總金額)
	 * @author pangben
	 * @param parm
	 * @return
	 * @throws
	 */
	public double updateAdmTotAmt(TParm parm) {
		// 更新ADM_INP TOTAL_AMT、 CUR_AMT字段 解决预交金界面显示问题===pangben 2014-6-9
		String sql = "SELECT SUM(TOT_AMT) TOT_AMT FROM IBS_ORDD WHERE CASE_NO='"
				+ parm.getValue("CASE_NO") + "'";
		TParm ibsOrddParm = new TParm(TJDODBTool.getInstance().select(sql));
		sql = "SELECT SUM(PRE_AMT) PRE_AMT FROM BIL_PAY WHERE CASE_NO='"
				+ parm.getValue("CASE_NO")
				+ "' AND TRANSACT_TYPE IN('01','02')";
		TParm bilPayParm = new TParm(TJDODBTool.getInstance().select(sql));
		double totAmt = 0.00;
		double ibsOrddAmt = 0.00;
		if (ibsOrddParm.getCount() > 0) {
			ibsOrddAmt = ibsOrddParm.getDouble("TOT_AMT", 0);// 总价
		}
		if (bilPayParm.getCount() > 0) {
			totAmt = bilPayParm.getDouble("PRE_AMT", 0) - ibsOrddAmt;// 预交金剩余
		}
//		sql = " UPDATE ADM_INP SET TOTAL_AMT = '" + ibsOrddAmt
//				+ "',CUR_AMT = '" + totAmt + "' " + "  WHERE CASE_NO = '"
//				+ parm.getValue("CASE_NO") + "'";
//		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
//		if (result.getErrCode() < 0) {
//			result.setErr(-1, "更新住院表金额出现问题");
//			return result;
//		}
		return totAmt;
	}
}
