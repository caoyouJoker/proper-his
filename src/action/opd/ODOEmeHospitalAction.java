package action.opd;

import java.sql.Timestamp;

import jdo.adm.ADMChgTool;
import jdo.adm.ADMInpTool;
import jdo.adm.ADMResvTool;
import jdo.adm.ADMTool;
import jdo.adm.ADMTransLogTool;
import jdo.adm.ADMWaitTransTool;
import jdo.mro.MROQueueTool;
import jdo.mro.MROTool;
import jdo.odi.OdiMainTool;
import jdo.opd.ODOEmeHospitalTool;
import jdo.sys.Operator;
import jdo.sys.SYSBedTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

public class ODOEmeHospitalAction extends TAction {

	public TParm doSave(TParm parm) {
		TConnection conn = getConnection();
		TParm result=new TParm();
//		TParm result = ODOEmeHospitalTool.getInstance().doSave(parm);
//		if (result.getErrCode() < 0) {
//			conn.close();
//			return result;
//		}
//		conn.close();
//		conn = getConnection();
		// 查詢門診的手術數據
		String oper = " SELECT OPBOOK_SEQ,ADM_TYPE,MR_NO,IPD_NO,CASE_NO,BED_NO,URGBLADE_FLG,OP_DATE,TF_FLG,TIME_NEED,"
				+ "ROOM_NO,TYPE_CODE,ANA_CODE,OP_DEPT_CODE,OP_STATION_CODE,DIAG_CODE1,DIAG_CODE2,DIAG_CODE3,BOOK_DEPT_CODE,"
				+ "OP_CODE1,OP_CODE2,BOOK_DR_CODE,MAIN_SURGEON,BOOK_AST_1,BOOK_AST_2,BOOK_AST_3,BOOK_AST_4,REMARK,STATE,CANCEL_FLG,"
				+ "OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE,PART_CODE,ISO_FLG,GDVAS_CODE,GRANT_AID FROM OPE_OPBOOK WHERE CASE_NO='"
				+ parm.getValue("OPD_CASE_NO")
				+ "' AND ADM_TYPE IN('O','E') AND CANCEL_FLG<>'Y' ORDER BY OP_DATE DESC ";
		TParm operParm1 = new TParm(TJDODBTool.getInstance().select(oper));
		if (operParm1.getCount() > 0) {
			TParm operParm = operParm1.getRow(0);
			result = ODOEmeHospitalTool.getInstance().cancelOpBook(operParm,
					conn);
			if (result.getErrCode() < 0) {
				conn.close();
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			String operSql = " SELECT OPBOOK_SEQ,ADM_TYPE,MR_NO,IPD_NO,CASE_NO,BED_NO,URGBLADE_FLG,OP_DATE,TF_FLG,TIME_NEED,"
					+ "ROOM_NO,TYPE_CODE,ANA_CODE,OP_DEPT_CODE,OP_STATION_CODE,DIAG_CODE1,DIAG_CODE2,DIAG_CODE3,BOOK_DEPT_CODE,"
					+ "OP_CODE1,OP_CODE2,BOOK_DR_CODE,MAIN_SURGEON,BOOK_AST_1,BOOK_AST_2,BOOK_AST_3,BOOK_AST_4,REMARK,STATE,CANCEL_FLG,"
					+ "OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE,PART_CODE,ISO_FLG,GDVAS_CODE,GRANT_AID FROM OPE_OPBOOK WHERE CASE_NO='"
					+ parm.getValue("OPD_CASE_NO")
					+ "' AND ADM_TYPE IN('O','E') AND STATE='1' AND CANCEL_FLG<>'Y' ORDER BY OP_DATE DESC ";
			TParm parmStatus1 = new TParm(TJDODBTool.getInstance().select(
					operSql));// 校驗手術排程
			if (parmStatus1.getCount() > 0) {
				TParm parmStatus = parmStatus1.getRow(0);
				parmStatus.setData("OPT_USER2", parm.getValue("OPT_USER"));
				parmStatus.setData("OPT_DATE2", parm.getValue("RESV_DATE"));
				parmStatus.setData("OPT_TERM2", parm.getValue("OPT_TERM"));
				result = ODOEmeHospitalTool.getInstance().updateOperSql(
						parmStatus, conn);
				if (result.getErrCode() < 0) {
					conn.close();
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					return result;
				}
			}
			result = insertAdmOpeSum(operParm1, parm, conn);
			if (result.getErrCode() < 0) {
				conn.close();
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		// String stateFlg = parm.getValue("STATE_FLG");
		result = ADMInpTool.getInstance().insertADMData(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
//		String updateSysSql = "UPDATE SYS_PATINFO SET IPD_NO='"
//				+ parm.getValue("IPD_NO") + "',RCNT_IPD_DATE=to_date('"
//				+ parm.getValue("RESV_DATE")
//				+ "','yyyy-mm-dd hh24-mi-ss') WHERE MR_NO='"
//				+ parm.getValue("MR_NO") + "'";
//		result = (TParm) TJDODBTool.getInstance().update(updateSysSql, conn);
		result = ODOEmeHospitalTool.getInstance().updateSysSql(parm,conn);
		if (result.getErrCode() < 0) {
			conn.close();
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
//		String updateInpSql = "UPDATE ADM_INP SET URG_FLG='Y' WHERE CASE_NO='"
//				+ parm.getValue("CASE_NO") + "'";
//		result = (TParm) TJDODBTool.getInstance().update(updateInpSql, conn);
		result = ODOEmeHospitalTool.getInstance().updateInpSql(parm,conn);
		if (result.getErrCode() < 0) {
			conn.close();
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
//		String hospid = "";
//		TParm regionParm = SYSRegionTool.getInstance().selectdata(
//				Operator.getRegion());
//		if (regionParm.getCount() > 0) {
//			hospid = regionParm.getValue("NHI_NO", 0);
//		}
		TParm creat = new TParm();
		creat.setData("MR_NO", parm.getValue("MR_NO"));
		creat.setData("CASE_NO", parm.getValue("CASE_NO"));
		creat.setData("OPT_USER", parm.getValue("OPT_USER"));
		creat.setData("OPT_TERM", parm.getValue("OPT_TERM"));
//		if (null != Operator.getRegion()
//				&& Operator.getRegion().length() > 0)
			creat.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		TParm p = SYSRegionTool.getInstance().selectdata(parm.getValue("REGION_CODE"));
		creat.setData("HOSP_ID", p.getValue("NHI_NO",0));
//		result = MROTool.getInstance().insertMRO(creat);
		result = ODOEmeHospitalTool.getInstance().insertMRO(creat,conn);
		if (result.getErrCode() < 0) {
			conn.close();
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// 判断该病案号是否已经存在病案主档 如果存在就不再插入
		if (!MROQueueTool.getInstance().checkHasMRO_MRV(parm.getValue("MR_NO"))) {
			TParm mro_mrv = new TParm();
//			String region = Operator.getRegion();
			mro_mrv.setData("MR_NO", parm.getValue("MR_NO"));
			mro_mrv.setData("IPD_NO", parm.getValue("IPD_NO"));
			mro_mrv.setData("CREATE_HOSP", parm.getValue("REGION_CODE"));
			mro_mrv.setData("IN_FLG", "2");
			mro_mrv.setData("CURT_HOSP", parm.getValue("REGION_CODE"));
			mro_mrv.setData("CURT_LOCATION", parm.getValue("REGION_CODE"));
			mro_mrv.setData("TRAN_HOSP", parm.getValue("REGION_CODE"));
			mro_mrv.setData("BOX_CODE", "");
			mro_mrv.setData("OPT_USER", parm.getValue("OPT_USER"));
			mro_mrv.setData("OPT_TERM", parm.getValue("OPT_TERM"));
//			result = MROQueueTool.getInstance().insertMRO_MRV(mro_mrv);
			result = ODOEmeHospitalTool.getInstance().insertMRO_MRV(mro_mrv, conn);
			if (result.getErrCode() < 0) {
				conn.close();
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		// 查询病患的预约信息 用来获取门急诊诊断
		TParm resv = ADMResvTool.getInstance().selectNotIn(parm.getValue("MR_NO"));
		String OE_DIAG_CODE = "";
		// 如果病患有预约信息
		if (resv.getCount() > 0) {
			OE_DIAG_CODE = resv.getValue("DIAG_CODE", 0);
		}
		// 修改病案 住院信息
		TParm adm = new TParm();
		adm.setData("IPD_NO", parm.getValue("IPD_NO"));
		adm.setData("IN_DATE",parm.getValue("RESV_DATE"));
		adm.setData("IN_DEPT", parm.getValue("DEPT_CODE"));
		adm.setData("IN_STATION", parm.getValue("STATION_CODE"));
		adm.setData("IN_ROOM_NO", ""); // 入院病室
		// 根据床位号
		// 查询出
		adm.setData("OE_DIAG_CODE", OE_DIAG_CODE); // 门急诊诊断
		adm.setData("IN_CONDITION", parm.getValue("PATIENT_CONDITION")); // 入院状态
		adm.setData("IN_COUNT", ""); // 住院次数
		adm.setData("PG_OWNER", Operator.getID()); // 首页建立者
		adm.setData("STATUS", "0"); // 状态 0 在院；1 出院未完成；2 出院已完成
		adm.setData("CASE_NO", parm.getValue("CASE_NO"));
		adm.setData("ADM_SOURCE", parm.getValue("ADM_SOURCE")); // 病患来源
		adm.setData("AGN_CODE",""); // 31天再住院
		adm.setData("AGN_INTENTION", ""); // 31天再住院原因
		// System.out.println("-=-------------------" + adm);
//		result = MROTool.getInstance().updateADMData(adm);
		result = ODOEmeHospitalTool.getInstance().updateADMData(adm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// 修改病案 患者基本信息
		TParm opt = new TParm();
		opt.setData("MR_NO", parm.getValue("MR_NO"));
		opt.setData("CASE_NO", parm.getValue("CASE_NO"));
		opt.setData("OPT_USER", parm.getValue("OPT_USER"));
		opt.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		opt.setData("AGE", parm.getValue("AGE"));
		result = ODOEmeHospitalTool.getInstance().updateMROPatInfo(opt,conn);
		if (result.getErrCode() < 0) {
			conn.close();
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		result = ODOEmeHospitalTool.getInstance().insertEmrFileIndex(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		result = onCheckInBed(parm,conn);
		if (result.getErrCode() < 0) {
			conn.close();
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
//		System.out.println("BED_NODDDDsd::::::"+parm.getValue("BED_NO_OLD"));
		if(parm.getValue("BED_NO_OLD") != null){
//			System.out.println("----------取消预约床位");
			String sql2="UPDATE SYS_BED SET APPT_FLG='N',PRETREAT_DATE='',PRE_MRNO=''," +
					" PRETREAT_TYPE='',PRE_PATNAME='',PRE_SEX='',PRETREAT_NO='' WHERE BED_NO='"+parm.getValue("BED_NO_OLD")+"'";
			result =new TParm(TJDODBTool.getInstance().update(sql2,conn));
			if (result.getErrCode() < 0) {
				conn.close();
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
			String sql3=" DELETE FROM ADM_PRETREAT WHERE PRETREAT_NO='"+parm.getValue("PRETREAT_NO")+"'";
			result =new TParm(TJDODBTool.getInstance().update(sql3,conn));
			if (result.getErrCode() < 0) {
				conn.close();
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}

		conn.commit();
		conn.close();
		return result;

	}
	
	public TParm onCheckInBed(TParm parm,TConnection conn){
		TParm updata = new TParm();
		
		// 床位号
		updata.setData("BED_NO",parm.getValue("BED_NO"));
		// 预约注记
		updata.setData("APPT_FLG", "N");
		// 占床注记
		updata.setData("ALLO_FLG", "Y");
		// 待转入病案号
		updata.setData("MR_NO", parm.getValue("MR_NO"));
		// 待转入就诊号
		updata.setData("CASE_NO", parm.getValue("CASE_NO"));
		// 待转入住院号
		updata.setData("IPD_NO", parm.getValue("IPD_NO"));
		// 占床注记
		updata.setData("BED_STATUS", "1");
		// 床位所在病区
		updata.setData("STATION_CODE", parm.getValue("STATION_CODE"));
		// 床位号
	//	updata.setData("BED_NO",checkIn.getParmValue().getValue("BED_NO",checkIn.getSelectedRow()));
				//checkIn.getValueAt(checkIn.getSelectedRow(), 1));
		// 科室
		updata.setData("DEPT_CODE", parm.getValue("DEPT_CODE"));
		// dataStore
		updata.setData("OCCU_FLG", "N");
		updata.setData("UPDATE", "");
		updata.setData("OPT_USER", parm.getValue("OPT_USER"));
		updata.setData("OPT_TERM", parm.getValue("OPT_TERM"));
		updata.setData("REGION_CODE", parm.getValue("REGION_CODE"));
		
//		System.out.println("OPT_USER============="+updata.getValue("OPT_USER"));
		
		// 查询chgLog
		TParm occuFlg = new TParm();
		occuFlg.setRowData(updata);
		// 删除转科表记录
		TParm wait = new TParm();
		wait.setRowData(updata);
		// 更新床位档
		TParm sysBed = new TParm();
		sysBed.setRowData(updata);
		// 更新在院病患信息
		TParm admInp = new TParm();
		admInp.setRowData(updata);
		// 查询transWait
		TParm transWait = new TParm();
		transWait.setRowData(updata);
		// 查询chgLog
		TParm chgLog = new TParm();
		chgLog.setRowData(updata);
		// 插入translog
		TParm transLog = new TParm();
		// 查询病床所在病房信息
		TParm bed = ADMInpTool.getInstance().selectRoomInfo(
				parm.getValue("BED_NO"));
		TParm result = new TParm();
		
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
//		System.out.println("//////////"+transrs);
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
			transLog.setData("CASE_NO", parm.getData("CASE_NO"));
			transLog.setData("MR_NO", parm.getData("MR_NO"));
			transLog.setData("IPD_NO", parm.getData("IPD_NO"));
			transLog.setData("IN_DATE", tranLogDept.getData("IN_DATE", 0));
			transLog.setData("OUT_DEPT_CODE", "");
			transLog.setData("OUT_STATION_CODE", "");
			transLog.setData("OUT_DATE", "");
			transLog.setData("IN_DEPT_CODE",
					parm.getData("DEPT_CODE") == null ? "" : parm
							.getData("DEPT_CODE"));
			transLog.setData("IN_STATION_CODE", parm.getData(
					"STATION_CODE") == null ? "" : parm.getData(
					"STATION_CODE"));
			transLog.setData("PSF_KIND", "");
			transLog.setData("OPT_USER", parm.getData("OPT_USER"));
			transLog.setData("OPT_TERM", parm.getData("OPT_TERM"));
			TParm cancelParm = new TParm();
			cancelParm.setData("CASE_NO", parm.getData("CASE_NO"));
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
//			System.out.println("caseNo::::::"+transrs.getData("CASE_NO",0));
			transLog.setData("CASE_NO", parm.getData("CASE_NO"));
			transLog.setData("MR_NO", parm.getData("MR_NO"));
			transLog.setData("IPD_NO", parm.getData("IPD_NO"));
			transLog.setData("OUT_DEPT_CODE", "");
			transLog.setData("OUT_STATION_CODE", "");
			transLog.setData("OUT_DATE", "");
			transLog.setData("IN_DEPT_CODE",
					parm.getData("DEPT_CODE") == null ? "" : parm
							.getData("DEPT_CODE"));
			transLog.setData("IN_STATION_CODE", parm.getData(
					"STATION_CODE") == null ? "" : parm.getData(
					"STATION_CODE"));
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
		chgLog.setData("SEQ_NO", SEQ+1);
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

	private TParm insertAdmOpeSum(TParm operParm1, TParm parm, TConnection conn) {
		TParm operParm = new TParm();
		TParm result = new TParm();
		for (int i = 0; i < operParm1.getCount(); i++) {
			operParm = operParm1.getRow(i);
			String opbookSeq = SystemTool.getInstance().getNo("ALL", "OPE",
					"OPBOOK_SEQ", "OPBOOK_SEQ");// 调用取号原则
			operParm.setData("OPBOOK_SEQ2", opbookSeq);
			operParm.setData("IPD_NO2", parm.getValue("IPD_NO"));
			operParm.setData("CASE_NO2", parm.getValue("CASE_NO"));
			operParm.setData("BED_NO2", parm.getValue("BED_NO"));
			operParm.setData("ADM_TYPE2", "I");
			operParm.setData("OPT_USER2", parm.getValue("OPT_USER"));
			operParm.setData("OPT_DATE2", parm.getValue("RESV_DATE"));
			operParm.setData("OPT_TERM2", parm.getValue("OPT_TERM"));
			operParm.setData("REGION_CODE2", parm.getValue("REGION_CODE"));
			result = ODOEmeHospitalTool.getInstance().insertOperSql(operParm,
					conn);
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				return result;
			}
		}
		return result;
	}

	public TParm doCancel(TParm parm) {
		TConnection conn = getConnection();
		TParm result = ODOEmeHospitalTool.getInstance().doCancel(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}
		// String stateFlg = parm.getValue("STATE_FLG");
		result = ADMTool.getInstance().ADM_CANCEL_INP(parm, conn);
		if (result.getErrCode() < 0) {
			conn.close();
			return result;
		}

		conn.commit();
		conn.close();
		return result;
	}
}
