package action.adm;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.SYSBedTool;
import jdo.sys.SystemTool;
import jdo.adm.ADMInpTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import jdo.adm.ADMTransLogTool;
import jdo.adm.ADMWaitTransTool;
import jdo.adm.ADMChgTool;
import jdo.adm.ADMTool;
import jdo.odi.OdiMainTool;
import jdo.mro.MROTool;
import jdo.mro.MRORecordTool;
import jdo.adm.ADMAutoBillTool;
import jdo.bil.BILTool;
import jdo.ibs.IBSOrderdTool;
import jdo.ibs.IBSBillmTool;
import jdo.ibs.IBSOrdermTool;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * Title: 入出转管理 Action
 * </p>
 * 
 * <p>
 * Description: 入出转管理 Action
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author JiaoY
 * @version 1.0
 */
public class ADMWaitTransAction extends TAction {
	/**
	 * 入科保存
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @throws Throwable 
	 */
	public TParm onInSave(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		// 查询chgLog
		TParm occuFlg = new TParm();
		occuFlg.setRowData(parm);
		// 删除转科表记录
		TParm wait = new TParm();
		wait.setRowData(parm);
		// 更新床位档
		TParm sysBed = new TParm();
		sysBed.setRowData(parm);
		// 更新在院病患信息
		TParm admInp = new TParm();
		admInp.setRowData(parm);
		// 查询transWait
		TParm transWait = new TParm();
		transWait.setRowData(parm);
		// 查询chgLog
		TParm chgLog = new TParm();
		chgLog.setRowData(parm);
		// 插入translog
		TParm transLog = new TParm();
		// 查询病床所在病房信息
		TParm bed = ADMInpTool.getInstance().selectRoomInfo(
				parm.getValue("BED_NO"));
		// System.out.println("bed------------------w1----------------:"+bed);
		// 删除转科档记录
        String[] ds = (String[]) wait.getData("UPDATE");
        List<String> dslist = Arrays.asList(ds);
        if(ds.length>1){
        	System.out.println("删除转科档记录生成sql:"+dslist);
        }
		// 判断该病患是否进行过包床 OCCU_FLG是前台传入的表示病患是否包床的助记
		if ("Y".equals(occuFlg.getData("OCCU_FLG"))) {
			// 如果病患包床了 那么要判断病患是否入住到指定的床位
			if ("Y".equals(occuFlg.getData("CHANGE_FLG"))) {
				// CHANGE_FLG=Y表示病患重新选择了其他床位 那么要清空原有的包床信息
				TParm clear = new TParm();
				clear.setData("CASE_NO", sysBed.getData("CASE_NO"));
				result = SYSBedTool.getInstance().clearAllForadmin(occuFlg,
						conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
		} else {
			// 如果病人没有包床，入住前把此病的其他床位的预约清除前先清床（住院处安排床位）
			TParm clear = new TParm();
			clear.setData("CASE_NO", sysBed.getData("CASE_NO"));
			result = SYSBedTool.getInstance().clearForadmin(clear, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		// System.out.println("admInp------------------w13----------------:"+result);
		// 更新床位档
		result = SYSBedTool.getInstance().upDate(sysBed, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		
		// 更新在院病患床位号
		admInp.setData("RED_SIGN", bed.getData("RED_SIGN", 0));
		admInp.setData("YELLOW_SIGN", bed.getData("YELLOW_SIGN", 0));
		// System.out.println("result---upDate(sysBed, conn);----------:"+result);
		result = ADMInpTool.getInstance().updateForWait(admInp, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		// 修改病案首页的目前所在科室,病区,病房字段
		TParm mro = new TParm();
		mro.setData("OUT_DEPT", parm.getData("DEPT_CODE")); // 当前所在科室
		mro.setData("OUT_STATION", parm.getData("STATION_CODE")); // 当前所在病区
		mro.setData("OUT_ROOM_NO", bed.getValue("ROOM_CODE", 0)); // 当前所在病房
		mro.setData("OPT_USER", parm.getData("OPT_USER"));
		mro.setData("OPT_TERM", parm.getData("OPT_TERM"));
		mro.setData("CASE_NO", parm.getValue("CASE_NO"));
		mro.setData("OPT_USER", parm.getData("OPT_USER"));
		mro.setData("OPT_TERM", parm.getData("OPT_TERM"));
		// System.out.println("ADMWaitTransAction:" + mro);
		result = MROTool.getInstance().updateTransDept(mro, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		// 修改首页的入院病房字段
		result = MROTool.getInstance().updateInRoom(
				bed.getValue("ROOM_CODE", 0), parm.getValue("CASE_NO"), conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();// shibl 20130104 add
			return result;
		}
		// 查询transWait
		TParm transrs = ADMWaitTransTool.getInstance().queryDate(transWait);
		if (transrs.getErrCode() < 0) {
			conn.rollback();
			conn.close();// shibl 20130104 add
			return result;
		}
		// 查询transLOG
		TParm tranLogDept = ADMTransLogTool.getInstance().getTranDeptData(
				transWait);
		if (tranLogDept.getErrCode() < 0) {
			System.out.println("查询ADMTransLogTool最新科室数据错误！");
			conn.rollback();
			conn.close();
			return result;
		}
		// 之前做过取消转科操作（先删除再插入）
		if (tranLogDept.getValue("PSF_KIND", 0).equals("CANCEL")
				|| tranLogDept.getValue("PSF_KIND", 0).equals("OUT")) {
			// 插入translog
			transLog.setData("CASE_NO", transrs.getData("CASE_NO", 0));
			transLog.setData("MR_NO", transrs.getData("MR_NO", 0));
			transLog.setData("IPD_NO", transrs.getData("IPD_NO", 0));
			transLog.setData("IN_DATE", tranLogDept.getData("IN_DATE", 0));
			transLog.setData("OUT_DEPT_CODE", "");
			transLog.setData("OUT_STATION_CODE", "");
			transLog.setData("OUT_DATE", "");
			transLog.setData("IN_DEPT_CODE",
					transrs.getData("IN_DEPT_CODE", 0) == null ? "" : transrs
							.getData("IN_DEPT_CODE", 0));
			transLog.setData("IN_STATION_CODE", transrs.getData(
					"IN_STATION_CODE", 0) == null ? "" : transrs.getData(
					"IN_STATION_CODE", 0));
			transLog.setData("PSF_KIND", "");
			transLog.setData("OPT_USER", parm.getData("OPT_USER"));
			transLog.setData("OPT_TERM", parm.getData("OPT_TERM"));
			TParm cancelParm = new TParm();
			cancelParm.setData("CASE_NO", transrs.getData("CASE_NO", 0));
			cancelParm.setData("IN_DATE", tranLogDept.getData("IN_DATE", 0));
			result = ADMTransLogTool.getInstance().deleteAdmData(cancelParm,
					conn); // 删除ADM_Trans_Log
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			result = ADMTransLogTool.getInstance().insertDateForCancel(
					transLog, conn); // 插入ADM_Trans_Log
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}

		} else {
			// 插入translog
			transLog.setData("CASE_NO", transrs.getData("CASE_NO", 0));
			transLog.setData("MR_NO", transrs.getData("MR_NO", 0));
			transLog.setData("IPD_NO", transrs.getData("IPD_NO", 0));
			transLog.setData("OUT_DEPT_CODE", "");
			transLog.setData("OUT_STATION_CODE", "");
			transLog.setData("OUT_DATE", "");
			transLog.setData("IN_DEPT_CODE",
					transrs.getData("IN_DEPT_CODE", 0) == null ? "" : transrs
							.getData("IN_DEPT_CODE", 0));
			transLog.setData("IN_STATION_CODE", transrs.getData(
					"IN_STATION_CODE", 0) == null ? "" : transrs.getData(
					"IN_STATION_CODE", 0));
			transLog.setData("OPT_USER", parm.getData("OPT_USER"));
			transLog.setData("OPT_TERM", parm.getData("OPT_TERM"));
			result = ADMTransLogTool.getInstance().insertDate(transLog, conn); // 插入ADM_Trans_Log
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		// System.out.println("-------------result--------1---------------------"+result);
		// 插入chgLog
		chgLog.setData("PSF_KIND", "INBD");
		TParm seqParm = new TParm();
		seqParm.setData("CASE_NO", parm.getData("CASE_NO"));
		TParm seqMax = ADMChgTool.getInstance().ADMQuerySeq(seqParm);
		int SEQ = 0;
		if (seqMax.getErrCode() < 0) {
			SEQ = 0;
		} else {
			if (seqMax.getValue("SEQ_NO", 0).trim().length() > 0) {
				SEQ = seqMax.getInt("SEQ_NO", 0);
			}
		}
		chgLog.setData("SEQ_NO", SEQ);
		// =============pangben modify 20110617
		chgLog.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		result = ADMChgTool.getInstance()
				.insertAdmChg(loadAdmChg(chgLog), conn); // 插入插入chgLog
		// System.out.println("-------------result-2----------------------------"+result);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		//-----------------shibl add 20140805-----------------------------------------
		
		String sql="DELETE FROM ADM_WAIT_TRANS WHERE CASE_NO='"+parm.getData("CASE_NO")+"'";
		
		// 删除adm_trans_Wait
		TParm badParm = new TParm(TJDODBTool.getInstance().update(sql, conn));
		if (badParm.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		// 更新 ODI_ORDER 中长期遗嘱 Bed_NO
		TParm changeOdiOrderBed = new TParm();
		changeOdiOrderBed.setData("CASE_NO", parm.getValue("CASE_NO"));
		changeOdiOrderBed.setData("BED_NO", parm.getValue("BED_NO"));
		result = OdiMainTool.getInstance()
				.modifBedNoUD(changeOdiOrderBed, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * 转科保存
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onInOutSave(TParm parm) {
		TParm result = new TParm();
		TConnection conn = getConnection();
		result = ADMTool.getInstance().ADM_OUT_DEPT(parm, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * 出院
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm outAdmSave(TParm parm) {
		// System.out.println("再次出院后台接参" + parm);
		// 更新在院病患信息
		TParm admInp = new TParm();
		// 更新床位档
		TParm sysBed = new TParm();
		TConnection conn = getConnection();
		TParm result = new TParm();
		parm.setData("DATE", parm.getData("OUT_DATE"));
		//===zhangp 20130105 start
		TParm orderm = new TParm();
		if(parm.getValue("ADMS").length() == 0){
			orderm = IBSOrderdTool.getInstance().selectdataAll(parm);
			if (orderm.getErrCode() < 0) {
				err(orderm.getErrText());
				return orderm;
			}
		}else{
			String sql = 
				" SELECT   D.CASE_NO, D.REXP_CODE, ROUND (SUM (D.OWN_AMT), 2) AS OWN_AMT," +
				" SUM (D.TOT_AMT) AS AR_AMT, M.BED_NO" +
				" FROM IBS_ORDM M, IBS_ORDD D" +
				" WHERE M.CASE_NO = D.CASE_NO" +
				" AND M.CASE_NO_SEQ = D.CASE_NO_SEQ" +
				" AND M.MR_NO = '" + parm.getValue("MR_NO") + "'" +
				" AND D.CASE_NO = '" + parm.getValue("CASE_NO") + "'" +
				" AND M.IPD_NO = '" + parm.getValue("IPD_NO") + "'" +
				" GROUP BY D.CASE_NO, D.REXP_CODE, M.BED_NO" +
				" ORDER BY D.CASE_NO, D.REXP_CODE, M.BED_NO";
			orderm = new TParm(TJDODBTool.getInstance().select(sql));
			if (orderm.getErrCode() < 0) {
				err(orderm.getErrText());
				return orderm;
			}
		}
		if (orderm.getCount() > 0 && parm.getValue("ADMS").length() > 0) {
			// 置为出院未缴费状态
			result = ADMTool.getInstance().updateBillStatus("2",
					parm.getValue("CASE_NO"), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();// shibl add 20130104 保证数据同步
				return result;
			}
		}
		//===zhangp 20130105 end
		TParm backBillParm = IBSBillmTool.getInstance().selBackBill(parm);
		if (backBillParm.getErrCode() < 0) {
			err(backBillParm.getErrText());
			return backBillParm;
		}
		if (orderm.getCount() <= 0) {//zhangp
			// 置为出院无账单状态
			result = ADMTool.getInstance().updateBillStatus("1",
					parm.getValue("CASE_NO"), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();// shibl add 20130104 保证数据同步
				return result;
			}
		}
		//===zhangp 20130110 start
		//===caowl 20130122 start
		Timestamp date = StringTool.getTimestamp(new Date());
		if(parm.getData("BILL_DATE")==null || parm.getData("BILL_DATE").equals("")){
			parm.setData("BILL_DATE",date);
		}
		//===caowl 20130122 end
		TParm ordermm = IBSOrdermTool.getInstance().selectdate(parm);
		if(ordermm.getCount()<=0){
			// 置为出院无账单状态
			result = ADMTool.getInstance().updateBillStatus("1",
					parm.getValue("CASE_NO"), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();// shibl add 20130104 保证数据同步
				return result;
			}
		}
		//===zhangp 20130110 end
		int backBillCount = backBillParm.getCount("CASE_NO");
		TParm admInpParm = new TParm();
		admInpParm.setData("CASE_NO", parm.getData("CASE_NO"));
		TParm admParm = ADMInpTool.getInstance().selectall(admInpParm);
		if (admParm.getErrCode() < 0) {
			err(admParm.getErrText());
			conn.rollback();
			conn.close();// shibl add 20130104 保证数据同步
			return admParm;
		}
		Timestamp today = SystemTool.getInstance().getDate();
		int dayConut = StringTool.getDateDiffer(admParm.getTimestamp("IN_DATE",
				0), today);
		Timestamp yesterday = StringTool.rollDate(today, -1);
		String yesDayStr = StringTool.getString(yesterday, "yyyyMMdd");
		String todayStr = StringTool.getString(today, "yyyyMMdd");
		String countDaySql = " SELECT SUM(D.TOT_AMT) AS TOT_AMT "
				+ "   FROM IBS_ORDM M,IBS_ORDD D " + "  WHERE D.CASE_NO = '"
				+ parm.getData("CASE_NO") + "' "
				+ "    AND M.CASE_NO = D.CASE_NO "
				+ "    AND M.DATA_TYPE = '0' "
				+ "    AND D.BILL_DATE BETWEEN TO_DATE('" + yesDayStr
				+ "','YYYYMMDD') " + "                      AND TO_DATE('"
				+ todayStr + "','YYYYMMDD') ";
		TParm countDayParm = new TParm(TJDODBTool.getInstance().select(
				countDaySql));
		double autoFee = 0.00;   
		if (countDayParm.getErrCode() < 0) {
			err(-1, countDayParm.getErrName() + countDayParm.getErrText());
			conn.rollback();
			conn.close();// shibl add 20130104 保证数据同步
			return countDayParm;
		}   
		if (countDayParm.getCount() > 0)
			autoFee = countDayParm.getDouble("TOT_AMT", 0);
		//加入判断是否是召回病患  
//		//fux modify 20140910
//		String WaitSql = " SELECT COUNT(M.CASE_NO) AS AR  "
//			+ "   FROM ADM_WAIT_TRANS M " + "  WHERE M.CASE_NO = '"
//			+ parm.getData("CASE_NO") + "' ";
//	    TParm WaitParm = new TParm(TJDODBTool.getInstance().select(
//			WaitSql));
//	    Double wart = countDayParm.getDouble("AR", 0); 
		//当日入当日出标记(只有当天入当日出,且前台确认计算固定费用的才执行手工床位费)
		String todayFlg = parm.getValue("TODAY");     
		if (parm.getInt("IN_DAYS") <= 0 && backBillCount <= 0 && autoFee == 0 && "Y".equals(todayFlg) ) { 
			// 产生床位费用                     
			result = ADMAutoBillTool.getInstance()  
					.postAutoBillOfMen(parm, conn); 
			if (result.getErrCode() < 0) {  
				conn.rollback();
				conn.close();
				return result;  
			}
			 conn.commit();// shibl 20130104 modify
		}
		// 更新ADM_INP
		admInp.setData("DS_DEPT_CODE", parm.getData("OUT_DEPT_CODE"));
		admInp.setData("DS_STATION_CODE", parm.getData("OUT_STATION_CODE"));
		admInp.setData("DS_DATE", parm.getData("OUT_DATE"));
		admInp.setData("CASE_NO", parm.getData("CASE_NO"));
		admInp.setData("OPT_USER", parm.getData("OPT_USER"));
		admInp.setData("OPT_TERM", parm.getData("OPT_TERM"));
		result = ADMInpTool.getInstance().outAdmInp(admInp, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		// System.out.println("更新adm_inp");
		// 清床
		sysBed.setData("CASE_NO", parm.getData("CASE_NO"));
		sysBed.setData("OPT_USER", parm.getData("OPT_USER"));
		sysBed.setData("OPT_TERM", parm.getData("OPT_TERM"));
		result = SYSBedTool.getInstance().clearForAdm(sysBed, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		// System.out.println("清床");
		// 插入ADM_CHG病患动态档
		TParm adm_chg = new TParm();
		// 查询最大SEQ
		TParm seqQuery = new TParm();
		seqQuery.setData("CASE_NO", parm.getValue("CASE_NO"));
		TParm seqParm = ADMChgTool.getInstance().ADMQuerySeq(seqQuery);
		String seq = seqParm.getData("SEQ_NO", 0).toString();
		adm_chg.setData("CASE_NO", parm.getValue("CASE_NO"));
		adm_chg.setData("SEQ_NO", seq);
		adm_chg.setData("IPD_NO", parm.getValue("IPD_NO"));
		adm_chg.setData("MR_NO", parm.getValue("MR_NO"));
		adm_chg.setData("PSF_KIND", "DSCH"); // 护士出院清床
		adm_chg.setData("PSF_HOSP", "");
		adm_chg.setData("CANCEL_FLG", "N");
		adm_chg.setData("CANCEL_DATE", "");
		adm_chg.setData("CANCEL_USER", "");
		adm_chg.setData("DEPT_CODE", parm.getValue("OUT_DEPT_CODE"));
		adm_chg.setData("STATION_CODE", parm.getValue("OUT_STATION_CODE"));
		adm_chg.setData("BED_NO", parm.getValue("BED_NO"));
		adm_chg.setData("VS_CODE_CODE", "");
		adm_chg.setData("ATTEND_DR_CODE", "");
		adm_chg.setData("DIRECTOR_DR_CODE", "");
		adm_chg.setData("OPT_USER", parm.getValue("OPT_USER"));
		adm_chg.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		// ==========pangben modify 20110617 start
		adm_chg.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		result = ADMChgTool.getInstance().insertAdmChg(adm_chg, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();// shibl add 20130104 保证数据同步
			return result;
		}
		// 更新ADM_TRANS_LOG
		TParm transLogParm = ADMTransLogTool.getInstance()
				.getTranDeptData(parm);
		TParm updatetransLogParm = new TParm();
		updatetransLogParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		updatetransLogParm.setData("IN_DATE", transLogParm.getValue("IN_DATE",
				0));
		updatetransLogParm.setData("OUT_DATE", StringTool.getTimestamp(parm
				.getValue("OUT_DATE").substring(0, 19), "yyyy-MM-dd HH:mm:ss"));
		updatetransLogParm.setData("OUT_DEPT_CODE", parm
				.getValue("OUT_DEPT_CODE"));
		updatetransLogParm.setData("OUT_STATION_CODE", parm
				.getValue("OUT_STATION_CODE"));
		updatetransLogParm.setData("PSF_KIND", "OUT");
		updatetransLogParm.setData("DEPT_ATTR_FLG", parm
				.getValue("DEPT_ATTR_FLG"));
		updatetransLogParm.setData("OPT_USER", parm.getValue("OPT_USER"));
		updatetransLogParm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		result = ADMTransLogTool.getInstance().updateAdm(updatetransLogParm,conn); // 插入ADM_Trans_Log
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();// shibl add 20130104 保证数据同步
			return result;
		}
		// System.out.println("动态记录《》《》》《》》《");
		// System.out.println("新账单入参"+parm);
		// 生成出院账单
		result = ADMInpTool.getInstance().insertIBSBiilData(parm, "Y", conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		// System.out.println("再次出院《》《》《》生成出院账单《》《》");
		// 查询病患所在的病室
		TParm bedForRoom = new TParm();
		bedForRoom.setData("BED_NO", parm.getData("BED_NO"));
		TParm room = SYSBedTool.getInstance().queryAll(parm);
		String roomCode = room.getValue("ROOM_CODE", 0);
		// 更新病案首页信息
		TParm mro = new TParm();
		mro.setData("OUT_DEPT", parm.getData("OUT_DEPT_CODE"));
		mro.setData("OUT_STATION", parm.getData("OUT_STATION_CODE"));
		mro.setData("OUT_DATE", parm.getData("OUT_DATE"));
		mro.setData("OUT_ROOM_NO", roomCode);
		mro.setData("REAL_STAY_DAYS", parm.getData("IN_DAYS")); // 住院天数
		mro.setData("VS_NURSE_CODE", parm.getValue("VS_NURSE_CODE")); // 责任护士
		mro.setData("DIRECTOR_DR_CODE", parm.getValue("DIRECTOR_DR_CODE")); // 科主任
		mro.setData("ATTEND_DR_CODE", parm.getValue("ATTEND_DR_CODE")); // 主治医师
		mro.setData("VS_DR_CODE", parm.getValue("VS_DR_CODE")); // 经治医师
		mro.setData("CASE_NO", parm.getData("CASE_NO"));
		mro.setData("CLNCPATH_CODE", admParm.getData("CLNCPATH_CODE",0));//wanglong add 20140604
		mro.setData("OPT_USER", parm.getData("OPT_USER"));
		mro.setData("OPT_TERM", parm.getData("OPT_TERM"));
		result = MROTool.getInstance().updateTransDept(mro, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		// System.out.println("更新MRO>>>>>>>");
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * 三级检诊医师，饮食状况 转床位 保存
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm changeDcBed(TParm parm) {
//		System.out.println("dsadad");
		TConnection conn = getConnection();
		TParm result = new TParm();
		// 原始数据
		TParm oldParm = new TParm();
		oldParm = parm.getParm("OLD_DATA");
		// 准备更新的数据
		TParm newParm = new TParm();
		newParm = parm.getParm("NEW_DATA");
		//==========   chenxi add 20130222= bigin====检索事物未提交前多人抢床的bug
		TParm sysbedParm = new TParm() ;
		sysbedParm.setRowData(newParm) ;
		if ("Y".equals(sysbedParm.getData("BED"))) {
			sysbedParm.setData("BED_NO", sysbedParm.getData("TRAN_BED"));
			result = SYSBedTool.getInstance().changeLockBed(sysbedParm, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			conn.commit();
		}
		//==========   chenxi add 20130222= end====检索事物未提交前多人抢床的bug
		// 三级检诊医师，饮食状况 ，陪护人数 更新ADM_INP保存
		TParm doc = new TParm();
		doc.setRowData(newParm);
		result = ADMInpTool.getInstance().updateForWaitPat(doc, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		// 同时更新首页的相关字段
		TParm mroParm = new TParm();
		mroParm.setData("IN_CONDITION", newParm.getValue("PATIENT_CONDITION")); // 入院情况
		mroParm.setData("ATTEND_DR_CODE", newParm.getValue("ATTEND_DR_CODE")); // 主治医师
		mroParm.setData("VS_DR_CODE", newParm.getValue("VS_DR_CODE")); // 经治医师
		mroParm.setData("DIRECTOR_DR_CODE", newParm
				.getValue("DIRECTOR_DR_CODE")); // 科主任
		mroParm.setData("CASE_NO", newParm.getValue("CASE_NO"));
		result = MRORecordTool.getInstance().updateForWaitPat(mroParm, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		// 查询seq
		int seq = 0;
		TParm queryseqParm = new TParm();
		queryseqParm.setRowData(newParm);
		TParm seqParm = ADMChgTool.getInstance().ADMQuerySeq(queryseqParm);
		if (seqParm.getData("SEQ_NO", 0) == null
				|| "".equals(seqParm.getData("SEQ_NO", 0)))
			seq = 0;
		else
			seq = seqParm.getInt("SEQ_NO", 0) - 1; // 因为方法中获取的序号已经自动加1了，所以要减去1，防止后面的程序++造成跳号
		// 插入ADM_CHG 经治医师
		if (!(oldParm.getValue("VS_DR_CODE", 0).equals(newParm
				.getValue("VS_DR_CODE")))) {
			// 转出医师 OUDR
			if (oldParm.getValue("VS_DR_CODE", 0).length() > 0) {
				seq++;
				TParm insertvsInDrData = new TParm();
				insertvsInDrData.setRowData(oldParm.getRow(0));
				insertvsInDrData.setData("PSF_KIND", "OUDR");
				insertvsInDrData
						.setData("BED_NO", oldParm.getData("BED_NO", 0));
				insertvsInDrData.setData("SEQ_NO", seq);
				// ============pangben modify 20110617 start
				insertvsInDrData.setData("REGION_CODE", parm
						.getValue("REGION_CODE"));
				TParm vsInDrParm = loadAdmChg(insertvsInDrData);
				result = ADMChgTool.getInstance()
						.insertAdmChg(vsInDrParm, conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			// 转入医师 INDR
			seq++;
			TParm insertvsInDrData = new TParm();
			insertvsInDrData.setRowData(newParm);
			insertvsInDrData.setData("PSF_KIND", "INDR");
			insertvsInDrData.setData("BED_NO", newParm.getData("BED_NO"));
			insertvsInDrData.setData("SEQ_NO", seq);
			// ============pangben modify 20110617 start
			insertvsInDrData.setData("REGION_CODE", parm
					.getValue("REGION_CODE"));
			TParm vsInDrParm = loadAdmChg(insertvsInDrData);
			result = ADMChgTool.getInstance().insertAdmChg(vsInDrParm, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		// 插入ADM_CHG 主治医师(上级医师)
		if (!(oldParm.getValue("ATTEND_DR_CODE", 0).equals(newParm
				.getValue("ATTEND_DR_CODE")))) {
			// 转出主治医师(上级医师) OUDR1
			if (oldParm.getValue("VS_DR_CODE", 0).length() > 0) {
				seq++;
				TParm insertvsInDrData = new TParm();
				insertvsInDrData.setRowData(oldParm.getRow(0));
				insertvsInDrData.setData("PSF_KIND", "OUDR1");
				insertvsInDrData
						.setData("BED_NO", oldParm.getData("BED_NO", 0));
				insertvsInDrData.setData("SEQ_NO", seq);
				// ============pangben modify 20110617 start
				insertvsInDrData.setData("REGION_CODE", parm
						.getValue("REGION_CODE"));
				TParm vsInDrParm = loadAdmChg(insertvsInDrData);
				result = ADMChgTool.getInstance()
						.insertAdmChg(vsInDrParm, conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			// 转入主治医师(上级医师) INDR1
			seq++;
			TParm insertvsInDrData = new TParm();
			insertvsInDrData.setRowData(newParm);
			insertvsInDrData.setData("PSF_KIND", "INDR1");
			insertvsInDrData.setData("BED_NO", newParm.getData("BED_NO"));
			insertvsInDrData.setData("SEQ_NO", seq);
			// ============pangben modify 20110617 start
			insertvsInDrData.setData("REGION_CODE", parm
					.getValue("REGION_CODE"));
			TParm vsInDrParm = loadAdmChg(insertvsInDrData);
			result = ADMChgTool.getInstance().insertAdmChg(vsInDrParm, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		// 插入ADM_CHG 主任医师
		if (!(oldParm.getValue("DIRECTOR_DR_CODE", 0).equals(newParm
				.getValue("DIRECTOR_DR_CODE")))) {
			// 转出主任医师 OUDR2
			if (oldParm.getValue("VS_DR_CODE", 0).length() > 0) {
				seq++;
				TParm insertvsInDrData = new TParm();
				insertvsInDrData.setRowData(oldParm.getRow(0));
				insertvsInDrData.setData("PSF_KIND", "OUDR2");
				insertvsInDrData
						.setData("BED_NO", oldParm.getData("BED_NO", 0));
				insertvsInDrData.setData("SEQ_NO", seq);
				// ============pangben modify 20110617 start
				insertvsInDrData.setData("REGION_CODE", parm
						.getValue("REGION_CODE"));
				TParm vsInDrParm = loadAdmChg(insertvsInDrData);
				result = ADMChgTool.getInstance()
						.insertAdmChg(vsInDrParm, conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
			// 转入主任医师 INDR2
			seq++;
			TParm insertvsInDrData = new TParm();
			insertvsInDrData.setRowData(newParm);
			insertvsInDrData.setData("PSF_KIND", "INDR2");
			insertvsInDrData.setData("BED_NO", newParm.getData("BED_NO"));
			insertvsInDrData.setData("SEQ_NO", seq);
			// ============pangben modify 20110617 start
			insertvsInDrData.setData("REGION_CODE", parm
					.getValue("REGION_CODE"));
			TParm vsInDrParm = loadAdmChg(insertvsInDrData);
			result = ADMChgTool.getInstance().insertAdmChg(vsInDrParm, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		// 转床
		TParm changeBed = new TParm();
		changeBed.setRowData(newParm);
		if ("Y".equals(changeBed.getData("BED"))) {
			// 转床保存
			TParm bed = new TParm();
			bed.setRowData(changeBed);
			bed.setData("APPT_FLG", "N");
			bed.setData("ALLO_FLG", "Y");
			bed.setData("BED_STATUS", "1");
			// 修改床位档
			bed.setData("BED_NO", changeBed.getData("TRAN_BED"));
			result = SYSBedTool.getInstance().changBed(bed, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			// 修改住院信息
			TParm inpParm = new TParm();
			// 查询病床所在病房信息
			TParm room = ADMInpTool.getInstance().selectRoomInfo(
					changeBed.getValue("TRAN_BED"));
			inpParm.setRowData(changeBed);
			inpParm.setData("BED_NO", changeBed.getData("TRAN_BED"));
			inpParm.setData("RED_SIGN", room.getData("RED_SIGN", 0));
			inpParm.setData("YELLOW_SIGN", room.getData("YELLOW_SIGN", 0));
			result = ADMInpTool.getInstance().updateForWait(inpParm, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			// 插入动态档 (转出床) 
			seq = seq + 1;
			TParm chgOutParm = new TParm();
			chgOutParm.setRowData(changeBed);
			chgOutParm.setData("PSF_KIND", "OUBD");
			chgOutParm.setData("SEQ_NO", seq);
			// ========pangben modify 20110617
			chgOutParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
			result = ADMChgTool.getInstance().insertChgDr(
					loadAdmChg(chgOutParm), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			// 插入动态档 (转入床)
			seq = seq + 1;
			TParm chgInParm = new TParm();
			chgInParm.setRowData(changeBed);
			chgInParm.setData("PSF_KIND", "INBD");
			chgInParm.setData("SEQ_NO", seq);
			chgInParm.setData("BED_NO", changeBed.getData("TRAN_BED"));
			// ========pangben modify 20110617
			chgInParm.setData("REGION_CODE", parm.getValue("REGION_CODE"));
			result = ADMChgTool.getInstance().insertChgDr(
					loadAdmChg(chgInParm), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			// 更新 ODI_ORDER 中长期遗嘱 Bed_NO
			// 调王猛的接口
			TParm changeOdiOrderBed = new TParm();
			changeOdiOrderBed.setRowData(changeBed);
			changeOdiOrderBed.setData("BED_NO", changeBed.getData("TRAN_BED"));
			result = OdiMainTool.getInstance().modifBedNoUD(changeOdiOrderBed,
					conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * 为adm_chg准备参数
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm loadAdmChg(TParm parm) {
		TParm result = new TParm();
		result.setData("CASE_NO", parm.getData("CASE_NO"));
		result.setData("SEQ_NO", parm.getData("SEQ_NO"));
		result.setData("IPD_NO", parm.getData("IPD_NO"));
		result.setData("MR_NO", parm.getData("MR_NO"));
		result.setData("CHG_DATE", parm.getData("DATE"));
		result.setData("PSF_KIND", parm.getData("PSF_KIND"));
		result.setData("PSF_HOSP", "");
		result.setData("CANCEL_FLG", "N");
		result.setData("CANCEL_DATE", "");
		result.setData("CANCEL_USER", "");
		result.setData("BED_NO", parm.getData("BED_NO") == null ? "" : parm
				.getData("BED_NO"));
		result.setData("DEPT_CODE", parm.getData("DEPT_CODE") == null ? ""
				: parm.getData("DEPT_CODE"));
		result.setData("STATION_CODE", parm.getData("STATION_CODE"));
		result.setData("VS_CODE_CODE", parm.getData("VS_DR_CODE") == null ? ""
				: parm.getData("VS_DR_CODE"));
		result.setData("ATTEND_DR_CODE",
				parm.getData("ATTEND_DR_CODE") == null ? "" : parm
						.getData("ATTEND_DR_CODE"));
		result.setData("DIRECTOR_DR_CODE",
				parm.getData("DIRECTOR_DR_CODE") == null ? "" : parm
						.getData("DIRECTOR_DR_CODE"));
		result.setData("OPT_USER", parm.getData("OPT_USER"));
		result.setData("OPT_TERM", parm.getData("OPT_TERM"));
		// =========pangben modify 20110617
		result.setData("REGION_CODE", parm.getData("REGION_CODE"));
		return result;
	}

	/**
	 * 住院无账单召回保存
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm admReturn(TParm parm) {
		TParm result = new TParm();
		TParm actionParm = parm.getParm("DATA");
		TConnection conn = getConnection();
		// System.out.println("住院无账单召回保存入参》》》》》" + actionParm);
		result = BILTool.getInstance().admReturn(
				actionParm.getValue("CASE_NO"),
				actionParm.getValue("OPT_USER"),
				actionParm.getValue("OPT_TERM"), conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}

	/**
	 * 取消转科Action
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onUpdateTransAndLog(TParm parm) {
		TParm searchParm = new TParm();
		searchParm.setData("CASE_NO", parm.getData("CASE_NO"));
		TConnection conn = getConnection();
		TParm searchResult = ADMWaitTransTool.getInstance().queryDate(
				searchParm);
		TParm result = new TParm();
		if (searchResult.getCount() > 0) {
			TParm deleteParm = new TParm();
			deleteParm.setData("CASE_NO", searchResult.getData("CASE_NO", 0));
			result = ADMWaitTransTool.getInstance().deleteIn(deleteParm, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			TParm insertInfo = new TParm();
			insertInfo.setData("CASE_NO", searchResult.getData("CASE_NO", 0));
			insertInfo.setData("MR_NO", searchResult.getData("MR_NO", 0));
			insertInfo.setData("IPD_NO", searchResult.getData("IPD_NO", 0));
			insertInfo.setData("DEPT_CODE", searchResult.getData(
					"OUT_DEPT_CODE", 0) == null ? "" : searchResult.getData(
					"OUT_DEPT_CODE", 0));
			insertInfo.setData("STATION_CODE", searchResult.getData(
					"OUT_STATION_CODE", 0) == null ? "" : searchResult.getData(
					"OUT_STATION_CODE", 0));
			insertInfo.setData("OPT_USER", parm.getData("OPT_USER"));
			insertInfo.setData("OPT_TERM", parm.getData("OPT_TERM"));
			result = ADMWaitTransTool.getInstance().saveForCancelTrans(
					insertInfo, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			// 更新ADM_TRANS_LOG
			TParm transdeptParm = ADMTransLogTool.getInstance()
					.getTranDeptData(parm);
			if (transdeptParm.getErrCode() < 0) {
				System.out.println("查询ADMTransLogTool转科数据错误！");
				conn.rollback();
				conn.close();
				return result;
			}
			// TParm updatetransLogParm = new TParm();
			// updatetransLogParm.setData("CASE_NO", parm.getValue("CASE_NO"));
			// updatetransLogParm.setData("IN_DATE",
			// transLogParm.getValue("IN_DATE", 0));
			// updatetransLogParm.setData("OUT_DATE","");
			// updatetransLogParm.setData("OUT_DEPT_CODE","");
			// updatetransLogParm.setData("OUT_STATION_CODE","");
			// updatetransLogParm.setData("PSF_KIND", "");
			// updatetransLogParm.setData("DEPT_ATTR_FLG","");
			// updatetransLogParm.setData("OPT_USER",
			// parm.getValue("OPT_USER"));
			// updatetransLogParm.setData("OPT_TERM",
			// parm.getValue("OPT_TERM"));
			TParm transLogParm = new TParm();
			transLogParm.setData("CASE_NO", searchResult.getData("CASE_NO", 0));
			transLogParm
					.setData("IN_DATE", transdeptParm.getData("IN_DATE", 0));
			transLogParm.setData("MR_NO", searchResult.getData("MR_NO", 0));
			transLogParm.setData("IPD_NO", searchResult.getData("IPD_NO", 0));
			transLogParm.setData("OUT_DEPT_CODE", searchResult.getData(
					"IN_DEPT_CODE", 0) == null ? "" : searchResult.getData(
					"IN_DEPT_CODE", 0));
			transLogParm.setData("OUT_STATION_CODE", searchResult.getData(
					"IN_STATION_CODE", 0) == null ? "" : searchResult.getData(
					"IN_STATION_CODE", 0));
			transLogParm
					.setData("OUT_DATE", SystemTool.getInstance().getDate());
			transLogParm.setData("IN_DEPT_CODE", searchResult.getData(
					"OUT_DEPT_CODE", 0) == null ? "" : searchResult.getData(
					"OUT_DEPT_CODE", 0));
			transLogParm.setData("IN_STATION_CODE", searchResult.getData(
					"OUT_STATION_CODE", 0) == null ? "" : searchResult.getData(
					"OUT_STATION_CODE", 0));
			transLogParm.setData("OPT_USER", parm.getData("OPT_USER"));
			transLogParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
			transLogParm.setData("PSF_KIND", "CANCEL");
			transLogParm.setData("DEPT_ATTR_FLG", "");
			result = ADMTransLogTool.getInstance()
					.updateAdm(transLogParm, conn); // 插入ADM_Trans_Log
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			TParm queryMaxSeq = new TParm();
			queryMaxSeq.setData("CASE_NO", searchResult.getData("CASE_NO", 0));
			result = ADMChgTool.getInstance().ADMQuerySeq(queryMaxSeq);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			TParm admChgParm = new TParm();
			admChgParm.setData("CASE_NO", searchResult.getData("CASE_NO", 0));
			admChgParm.setData("SEQ_NO", result.getInt("SEQ_NO", 0));
			admChgParm.setData("IPD_NO", searchResult.getData("IPD_NO", 0));
			admChgParm.setData("MR_NO", searchResult.getData("MR_NO", 0));
			admChgParm.setData("DATE", parm.getData("DATE"));
			admChgParm.setData("PSF_KIND", "CANCEL");
			admChgParm.setData("OPT_USER", parm.getData("OPT_USER"));
			admChgParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
			admChgParm.setData("DEPT_CODE", searchResult.getData(
					"IN_DEPT_CODE", 0));
			admChgParm.setData("STATION_CODE", searchResult.getData(
					"IN_STATION_CODE", 0));
			admChgParm.setData("REGION_CODE", "H01");
			result = ADMChgTool.getInstance().insertAdmChg(
					loadAdmChg(admChgParm), conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
			conn.commit();
			conn.close();
			return result;
		} else {
			TParm parm1 = new TParm();
			parm1.setErrCode(-1);
			// modified by WangQing at 2017/02/09  -start
			// 增加connection的commit和close方法，回收connection资源
			conn.commit();
			conn.close();
			// modified by WangQing at 2017/02/09  -end
			return parm1;
		}
	}
	/**
	 * 取消入科
	 * @param parm
	 * @return
	 */
	public TParm onCancleInDP(TParm parm){
		
		TConnection conn = getConnection();
		TParm result = new TParm();
		// 查询chgLog
		TParm occuFlg = new TParm();
		occuFlg.setRowData(parm);
		// 更新床位档
		TParm sysBed = new TParm();
		sysBed.setRowData(parm);
		// 判断该病患是否进行过包床 OCCU_FLG是前台传入的表示病患是否包床的助记
		if ("Y".equals(occuFlg.getData("OCCU_FLG"))) {
			// 如果病患包床了 那么要判断病患是否入住到指定的床位
			if ("Y".equals(occuFlg.getData("CHANGE_FLG"))) {
				// CHANGE_FLG=Y表示病患重新选择了其他床位 那么要清空原有的包床信息
				TParm clear = new TParm();
				clear.setData("CASE_NO", sysBed.getData("CASE_NO"));
				result = SYSBedTool.getInstance().clearAllForadmin(occuFlg,
						conn);
				if (result.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					return result;
				}
			}
		} else {
			// 如果病人没有包床，入住前把此病的其他床位的预约清除前先清床（住院处安排床位）
			TParm clear = new TParm();
			clear.setData("CASE_NO", sysBed.getData("CASE_NO"));
			result = SYSBedTool.getInstance().clearForadmin(clear, conn);
			if (result.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				return result;
			}
		}
		String caseNo=sysBed.getValue("CASE_NO");
		String chgOUDPsql="SELECT DEPT_CODE,STATION_CODE FROM ADM_CHG WHERE CASE_NO='"+caseNo+"'" +
				" AND PSF_KIND='OUDP'  ORDER BY CHG_DATE DESC ";
		String chgINDPsql="SELECT DEPT_CODE,STATION_CODE FROM ADM_CHG WHERE CASE_NO='"+caseNo+"'" +
		" AND PSF_KIND='INBD'  ORDER BY CHG_DATE DESC ";
		TParm OUDPparm=new TParm(TJDODBTool.getInstance().select(chgOUDPsql));
		TParm INDPparm=new TParm(TJDODBTool.getInstance().select(chgINDPsql));
		if(INDPparm.getCount()<=0){
			conn.rollback();
			conn.close();
			result.setErr(-1, "未查询到最新的动态记录");
			return result;
		}
		TParm insertInfo = new TParm();
		insertInfo.setData("CASE_NO", parm.getData("CASE_NO"));
		insertInfo.setData("MR_NO", parm.getData("MR_NO"));
		insertInfo.setData("IPD_NO", parm.getData("IPD_NO"));
		insertInfo.setData("OUT_DEPT_CODE", OUDPparm.getData(
				"DEPT_CODE", 0) == null ? "" : OUDPparm.getData(
				"DEPT_CODE", 0));
		insertInfo.setData("OUT_STATION_CODE", OUDPparm.getData(
				"STATION_CODE", 0) == null ? "" : OUDPparm.getData(
				"STATION_CODE", 0));
		insertInfo.setData("IN_DEPT_CODE", INDPparm.getData(
				"DEPT_CODE", 0) == null ? "" : INDPparm.getData(
				"DEPT_CODE", 0));
		insertInfo.setData("IN_STATION_CODE", INDPparm.getData(
				"STATION_CODE", 0) == null ? "" : INDPparm.getData(
				"STATION_CODE", 0));
		insertInfo.setData("OPT_USER", parm.getData("OPT_USER"));
		insertInfo.setData("OPT_TERM", parm.getData("OPT_TERM"));
		
		// add by wangqing 20180302 解决不能取消入科的问题
		// 转出床号
		insertInfo.setData("OUT_BED_NO", parm.getData("BED_NO"));
		
		result=ADMWaitTransTool.getInstance().saveForInOutDept(insertInfo, conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		String translogsql="DELETE FROM ADM_TRANS_LOG WHERE CASE_NO='"+caseNo+"' AND OUT_DEPT_CODE IS NULL";
		result=new TParm(TJDODBTool.getInstance().update(translogsql, conn));
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		TParm queryMaxSeq = new TParm();
		queryMaxSeq.setData("CASE_NO", parm.getData("CASE_NO"));
		result = ADMChgTool.getInstance().ADMQuerySeq(queryMaxSeq);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		TParm admChgParm = new TParm();
		admChgParm.setData("CASE_NO", parm.getData("CASE_NO"));
		admChgParm.setData("SEQ_NO", result.getInt("SEQ_NO", 0));
		admChgParm.setData("IPD_NO", parm.getData("IPD_NO"));
		admChgParm.setData("MR_NO", parm.getData("MR_NO"));
		admChgParm.setData("DATE", SystemTool.getInstance().getDate());
		admChgParm.setData("PSF_KIND", "CANCELINDP");
		admChgParm.setData("OPT_USER", parm.getData("OPT_USER"));
		admChgParm.setData("OPT_TERM", parm.getData("OPT_TERM"));
		admChgParm.setData("DEPT_CODE", INDPparm.getData(
				"DEPT_CODE", 0) == null ? "" : INDPparm.getData(
						"DEPT_CODE", 0));
		admChgParm.setData("STATION_CODE",INDPparm.getData(
				"STATION_CODE", 0) == null ? "" : INDPparm.getData(
						"STATION_CODE", 0));
		admChgParm.setData("REGION_CODE", "H01");
		result = ADMChgTool.getInstance().insertAdmChg(
				loadAdmChg(admChgParm), conn);
		if (result.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			return result;
		}
		conn.commit();
		conn.close();
		return result;
	}
}