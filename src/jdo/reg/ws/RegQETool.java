package jdo.reg.ws;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.ins.INSIbsTool;
import jdo.ins.INSMZConfirmTool;
import jdo.ins.INSOpdTJTool;
import jdo.ins.INSTJFlow;
import jdo.ins.INSTJReg;
import jdo.ins.INSTJTool;
import jdo.reg.PatAdmTool;
import jdo.reg.Reg;
import jdo.reg.services.BodyParams;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import action.reg.REGAction;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

public class RegQETool extends TJDODBTool{
	/**
	 *实例 
	 */
	public static RegQETool instanceObject;
	
	/**
	 * 得到实例
	 * @return
	 */
	public static RegQETool getInstance(){
		if(instanceObject == null)
			instanceObject = new RegQETool();
		return instanceObject;
	}
	
	
	
	public TParm getPatRegPatadm(TParm parm){
		
		
		String cardType=parm.getValue("CardType");
		String cardNo=parm.getValue("CardNo");
		String mrNo="";
		String sql = "";
		if(cardType.equals("01")){//医疗卡PatientID CureCardNo
			sql = "SELECT A.MR_NO " +
					" FROM EKT_MASTER A" +
					" WHERE A.CARD_NO = '"+cardNo+"'";
		}else if(cardType.equals("02")){//身份证 IDCardNo
			sql = "SELECT A.MR_NO" +
					" FROM SYS_PATINFO A" +
					" WHERE A.IDNO = '"+cardNo+"' " ;
		}else if(cardType.equals("03")){//医保卡 MedCardNo
			sql = "SELECT A.MR_NO" +
					" FROM (SELECT DISTINCT MR_NO,INSCARD_NO MEDCARDNO FROM INS_MZ_CONFIRM WHERE INSCARD_NO = '"+cardNo+"') A," +
					" SYS_PATINFO B" +
					" WHERE  A.MR_NO = B.MR_NO ";
		}else if(cardType.equals("04")){ //患者ID mr_no
			mrNo=cardNo;
		}
		if(sql.length() > 0){
			TParm mrNoParm = new TParm(TJDODBTool.getInstance().select(sql));
			mrNo = mrNoParm.getValue("MR_NO", 0);
		}
		
		String regSql = "SELECT A.CASE_NO,B.PAT_NAME,A.MR_NO,A.ADM_TYPE," +
				" J.INS_PAT_TYPE,J.INS_CROWD_TYPE,A.CONFIRM_NO," +
				" TO_CHAR(A.REG_DATE,'YYYY-MM-DD HH24:MM:SS') REG_DATE," +
				" C.USER_NAME,D.DEPT_CHN_DESC,E.SESSION_DESC, F.QUEGROUP_DESC " +
				" FROM REG_PATADM A, SYS_PATINFO B, SYS_OPERATOR C,SYS_DEPT D, " +
				" REG_SESSION E, REG_QUEGROUP F,INS_MZ_CONFIRM J" +
				" WHERE A.MR_NO = B.MR_NO AND A.ARRIVE_FLG='Y' AND A.REGCAN_USER IS  NULL" +
				" AND A.REALDR_CODE = C.USER_ID AND A.REALDEPT_CODE = D.DEPT_CODE" +
				" AND A.CASE_NO = J.CASE_NO(+) AND A.CONFIRM_NO = J.CONFIRM_NO(+)" +
				" AND A.SESSION_CODE = E.SESSION_CODE AND A.CLINICTYPE_CODE = F.QUEGROUP_CODE" +
				" AND A.MR_NO = '"+mrNo+"' AND A.ADM_TYPE='O' ";
		TParm regParm = new TParm(TJDODBTool.getInstance().select(regSql));
		return regParm;
		
		
		
	}
	
	public TParm getDrAllRestCount(TParm parm){		
		
		String deptCode = parm.getValue("DeptCode");
		String admDate = parm.getValue("VisitDate").replaceAll("-", "").substring(0,8);
		
		String sql = "SELECT MAX_QUE, QUE_NO, (MAX_QUE - QUE_NO + 1) REG_QUE, VIP_FLG, " +
		"REGION_CODE||'#'|| ADM_TYPE||'#'" +
		"||ADM_DATE||'#'|| SESSION_CODE|| '#'|| CLINICROOM_NO || '#'|| REALDR_CODE AS ID, REALDR_CODE,SESSION_CODE "+
				" FROM REG_SCHDAY" +
				" WHERE ADM_TYPE = 'O' AND  CLINICTMP_FLG='N' AND  ADM_DATE = '"+admDate+"'" +
				" AND REALDEPT_CODE = '"+deptCode+"'";
		RegQEServiceImpl.writerLog2(sql);
		TParm re = new TParm(TJDODBTool.getInstance().select(sql));
		if(re.getCount() > 0){
			for (int j = 0; j < re.getCount(); j++) {
				
				if(re.getBoolean("VIP_FLG",j)){
					String [] names = re.getValue("ID",j).split("#");
					sql = "SELECT COUNT (QUE_NO) REG_QUE" +
							" FROM REG_CLINICQUE" +
							" WHERE ADM_DATE = '"+names[2]+"'" +
							" AND SESSION_CODE = '"+names[3]+"'" +
							" AND CLINICROOM_NO = '"+names[4]+"'" +
							" AND ADM_TYPE = '"+names[1]+"'" +
							" AND QUE_STATUS = 'N'";
					TParm vipRe = new TParm(TJDODBTool.getInstance().select(sql));
					if(vipRe.getCount() > 0 ){
						re.setData("REG_QUE", j, vipRe.getInt("REG_QUE", 0));
					}
				}
				
			}
			
			
		}
		
		return re;
	}
	
	public TParm getDrTodayRestCount(TParm parm){

		//院区+就诊类别+就诊日期+时段+诊间
		String [] names = parm.getValue("VisitCode").split("#");
		String drCode = parm.getValue("DoctorCode");
		String deptCode = parm.getValue("DeptCode");
		String sql = "SELECT MAX_QUE, QUE_NO, (MAX_QUE - QUE_NO + 1) REG_QUE, VIP_FLG" +
				" FROM REG_SCHDAY" +
				" WHERE ADM_DATE = '"+names[2]+"'" +
				" AND SESSION_CODE = '"+names[3]+"'" +
				" AND CLINICROOM_NO = '"+names[4]+"'" +
				" AND REGION_CODE = '"+names[0]+"'" +
				" AND REALDR_CODE = '"+names[5]+"'" +
				" AND ADM_TYPE = '"+names[1]+"'";
		TParm re = new TParm(TJDODBTool.getInstance().select(sql));
		if(re.getCount() > 0){
			if(re.getBoolean("VIP_FLG",0)){
				sql = "SELECT COUNT (QUE_NO) REG_QUE" +
						" FROM REG_CLINICQUE" +
						" WHERE ADM_DATE = '"+names[2]+"'" +
						" AND SESSION_CODE = '"+names[3]+"'" +
						" AND CLINICROOM_NO = '"+names[4]+"'" +
						" AND ADM_TYPE = '"+names[1]+"'" +
						" AND QUE_STATUS = 'N'";
				TParm vipRe = new TParm(TJDODBTool.getInstance().select(sql));
				if(vipRe.getCount() > 0 ){
					re.setData("REG_QUE", 0, vipRe.getInt("REG_QUE", 0));
				}
			}
			
		}
		
		return re;
	}
	
	public TParm regApp(TParm parm){
		TParm re = new TParm();
		//院区+就诊类别+就诊日期+时段+诊间
		String [] names= parm.getValue("VisitCode").split("#");
		String clinicRoomNo=names[4];
		String regionCode=names[0];
		String admType=names[1];
		String date=names[2];
		String sessionCode=names[3];
		
		String drCode = names[5];
		String deptCode = parm.getValue("DeptCode");
		String mrNo = parm.getValue("PatientID");
		String businessFrom = parm.getValue("BusinessFrom");
		String TerminalCode = parm.getValue("TerminalCode");
		String periodCode = parm.getValue("PeriodCode");
		String regmethodCode =parm.getValue("AppointType"); //挂号方式
		String idNo = parm.getValue("IDNO"); //身份证号
		String orderNo = parm.getValue("orderNo"); //身份证号
		String ctz1Code ="99";
		String patName = "";
		String cellPhone = parm.getValue("phoneNo");
		
		if(mrNo.length() == 0){
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", "Err:病案号不能为空");
			return re;
		}
		
		//病人信息
		TParm patInfo = new TParm(TJDODBTool.getInstance().select("SELECT * FROM SYS_PATINFO WHERE MR_NO='"+mrNo+"'"));
		if(patInfo.getCount() < 0){
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", "Err:未取得病人信息");
			return re;
		}
		
		if(patInfo.getValue("BLACK_FLG", 0).equals("Y")){
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", "Err:此病人为黑名单用户，不可以进行预约");
			return re;
			
		}
		ctz1Code = patInfo.getValue("CTZ1_CODE", 0);
		if(ctz1Code.length() == 0){
			ctz1Code="99";
		}
		
		patName = patInfo.getValue("PAT_NAME", 0);
		if(cellPhone.length() == 0){
			cellPhone = patInfo.getValue("TEL_HOME", 0);
		}
		
		
		String apptSql = "SELECT COUNT(CASE_NO) COUNT FROM REG_PATADM " +
				" WHERE MR_NO='"+mrNo+"' AND APPT_CODE='Y' " +
				" AND ADM_DATE = TO_DATE('"+date+"','YYYYMMDD') AND REGCAN_USER IS NULL";
		TParm apptParm = new TParm(TJDODBTool.getInstance().select(apptSql));
		int apptCount = apptParm.getInt("COUNT", 0);
		if(apptCount >= 2){
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", "Err:此病人已预约2次，不可以再进行预约");
			return re;
		}
		
		
		

		// 取得诊间信息
		TParm parmClinicRoom = getClinicRoomInf(clinicRoomNo);
		if (parmClinicRoom.getErrCode() < 0){
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", parmClinicRoom);
			return re;
		}

		if (parmClinicRoom.getCount() <= 0){
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", "Err:未取得诊间信息");
			return re;
		}

		// 取得班表信息
		TParm parmSchDay =getREGSchdayInfo(clinicRoomNo, regionCode, admType,
				date, sessionCode,drCode);
		if (parmSchDay.getErrCode() < 0){
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", "Err:" +parmSchDay);
			return re;
		}
			
		if (parmSchDay.getCount() <= 0){
			
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", "Err:未取得班表信息");
			return re;
		}
		
		if(parmSchDay.getBoolean("STOP_SESSION", 0)){
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", "Err:该班表已停诊");
			return re;
		}
		
		
		String regSql = "SELECT COUNT(CASE_NO) REG_COUNT FROM REG_PATADM WHERE "
				+ " TO_CHAR( ADM_DATE,'YYYYMMDD') = '"+date+"'"
				+ " AND SESSION_CODE = '"+sessionCode+"'"
				+ " AND REALDR_CODE = '"+drCode+"'"
				+ " AND MR_NO = '"+mrNo+"'"
				+ " AND REGCAN_USER IS NULL";
		TParm regParm = new TParm(TJDODBTool.getInstance().select(regSql));
		int regCount = regParm.getInt("REG_COUNT", 0);
		if(regCount == 1){
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", "Err:此班表中已有该病人的挂号信息，不可以再进行预约");
			return re;
		}
		
		
		
		// 取得数据库连接
		TAction tAction = new TAction();
		TConnection conn = tAction.getConnection();
		if (conn == null){
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", "Err:未取得数据库连接");
			return re;
		}

		
		// 取得诊间序号
		String queNo = getQueNo(parmSchDay, clinicRoomNo, admType, date,
				sessionCode, regionCode,periodCode, conn);
		
		if (queNo.length() == 0) {
			conn.rollback();
			conn.close();
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", "Err:就诊号已满");
			return re;
		}
		
		// 取得门诊就诊顺序号
		String caseNo =SystemTool.getInstance().getNo("ALL", "REG",
                "CASE_NO", "CASE_NO");
		if (caseNo.length() == 0){
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", "Err:未取得门诊顺序号");
			return re;
		}
		
		
		String serviceLevel = "1";
		
		String ctz2Code=getCtz2Code(parmSchDay.getValue("CLINICTYPE_CODE", 0),patInfo);

		
		TParm parmReg = insertRegApp(mrNo, date, sessionCode, admType,
				deptCode, clinicRoomNo, drCode, regionCode, ctz1Code,ctz2Code,
				serviceLevel, queNo, caseNo, businessFrom,TerminalCode,
				regmethodCode,parmClinicRoom, parmSchDay,orderNo,conn);
		if (parmReg.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			parmReg.setData("IsSuccessed", "1");
			parmReg.setData("ErrorMsg", parmReg);
			return parmReg;
		}
		conn.commit();
		conn.close();
		
		re.setData("IsSuccessed", "0");
		re.setData("ErrorMsg", "");
		re.setData("AppointCode", caseNo);
		
		 String sessionSql =
             "SELECT SESSION_DESC FROM REG_SESSION WHERE SESSION_CODE = '" + sessionCode
                     + "' AND ADM_TYPE='" + admType + "'";
          TParm sc = new TParm(TJDODBTool.getInstance().select(sessionSql));
		String content = "病案号为"+mrNo+"的患者。您已预约成功"+StringTool.getString(StringTool.getTimestamp(date, "yyyyMMdd"),
        "yyyy/MM/dd")+sc.getValue("SESSION_DESC", 0)+" 第"+queNo+"号"+StringUtil.getDesc("SYS_OPERATOR", "USER_NAME", "USER_ID='" + drCode
                + "'")+"医生的门诊，仅限"+patName+"本人。如需取消，请至少提前一天到医院挂号柜台处理。为了保证您准时就诊，您需提前办理预约报道手续（泰达国际心血管病医院）";
		re.setData("Comment", content);
//		re.setData("Comment", "请于"+date+"到医院就诊");
		
		
		if(cellPhone.length() > 0){			
			TParm smsParm = new TParm();
	        smsParm.addData("MrNo", mrNo);
	        smsParm.addData("Name", patName);
	        smsParm.addData("Content", content);
	        smsParm.addData("TEL1", cellPhone);
	        REGAction reg = new REGAction();
	        reg.orderMessage(smsParm);// 发短信
	        String updateTelSQL =
                "UPDATE SYS_PATINFO SET TEL_HOME='#',OPT_USER='&',OPT_DATE=SYSDATE,OPT_TERM='&' WHERE MR_NO='@'";
	        updateTelSQL = updateTelSQL.replaceFirst("#", cellPhone);
	        updateTelSQL = updateTelSQL.replaceAll("&", "QeApp");
	        updateTelSQL = updateTelSQL.replaceFirst("@", mrNo);
	        TJDODBTool.getInstance().update(updateTelSQL);//更新sys_patinfo里的cell_phone
	        
	        
		}
		
	
		
		return re;
		
	}
	
	public TParm unRegApp(TParm parm) {
		TParm re = new TParm();
		String caseNo = parm.getValue("caseNo");
		String businessFrom = parm.getValue("BusinessFrom");
		String TerminalCode = parm.getValue("TerminalCode");

		TParm parmRegInf = new TParm(
				TJDODBTool
						.getInstance()
						.select(
								" SELECT ARRIVE_FLG,VIP_FLG,CLINICROOM_NO,ADM_TYPE,"
										+ "        TO_CHAR(ADM_DATE,'YYYYMMDD') ADM_DATE,SESSION_CODE,"
										+ "        QUE_NO,DEPT_CODE,REGION_CODE"
										+ " FROM REG_PATADM "
										+ " WHERE CASE_NO = '" + caseNo + "'"));
		if (parmRegInf.getErrCode() < 0) {
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", "Err" + parmRegInf);
			return re;
		}

		if (parmRegInf.getCount() <= 0) {
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", "Err:未找到病患预约信息");
			return re;

		}

		if (parmRegInf.getValue("ARRIVE_FLG", 0).equals("Y")) {
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", "Err:病患已经报道,请到收费柜台办理退挂业务");
			return re;

		}

		TAction tAction = new TAction();
		TConnection conn = tAction.getConnection();
		if (conn == null) {
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", "Err:未取得连接");
			return re;
		}

		TParm unRegparm = new TParm(TJDODBTool.getInstance().update(
				" UPDATE REG_PATADM" + " SET REGCAN_USER = '" + businessFrom
						+ "'," + "     REGCAN_DATE = SYSDATE"
						+ " WHERE CASE_NO = '" + caseNo + "'", conn));
		if (parm.getErrCode() < 0) {
			conn.rollback();
			conn.close();
			re.setData("IsSuccessed", "1");
			re.setData("ErrorMsg", "Err" + unRegparm);
			return re;
		}

		TParm parmQue = new TParm();
		if (parmRegInf.getValue("VIP_FLG", 0).equals("Y")) {
			parmQue = updateVIPQueNo(parmRegInf.getValue("CLINICROOM_NO", 0),
					parmRegInf.getValue("ADM_TYPE", 0), parmRegInf.getValue(
							"ADM_DATE", 0), parmRegInf.getValue("SESSION_CODE",
							0), parmRegInf.getValue("QUE_NO", 0), "N", conn);
			if (parmQue.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				re.setData("IsSuccessed", "1");
				re.setData("ErrorMsg", "Err" + parmQue);
				return re;

			}

		}else{
			
			parmQue = updateMaxQueNo(parmRegInf.getValue("CLINICROOM_NO", 0),
					parmRegInf.getValue("ADM_TYPE", 0), parmRegInf.getValue(
							"ADM_DATE", 0), parmRegInf.getValue("SESSION_CODE",
							0), parmRegInf.getValue("REGION_CODE", 0),  conn);
			if (parmQue.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				re.setData("IsSuccessed", "1");
				re.setData("ErrorMsg", "Err" + parmQue);
				return re;

			}
			
		}

		conn.commit();
		conn.close();
		re.setData("IsSuccessed", "0");
		re.setData("ErrorMsg", "成功");
		return re;

	}
	
	public TParm regOrUnReg(TParm parm){
		//院区+就诊类别+就诊日期+时段+诊间
		String [] names= parm.getValue("VisitCode").split("#");
		String clinicRoomNo=names[4];
		String regionCode=names[0];
		String admType=names[1];
		String date=names[2];
		String sessionCode=names[3];
		String drCode=names[5];
		

		String deptCode = parm.getValue("DeptCode");
		String mrNo = parm.getValue("PatientID");
		String regDate = parm.getValue("RegDate").replaceAll("-", "");
		String lockType = parm.getValue("LockType");
		String businessFrom = parm.getValue("BusinessFrom");
		String TerminalCode = "0.0.0.0";
		String caseNo = parm.getValue("caseNo");
		
		TParm re = new TParm();
		String remainsNum="";  //剩于挂号数
		boolean vipFlg = false;
		
		//01 锁  02 解锁
		if("01".equals(lockType)){
			//病人信息
			TParm patInfo = new TParm(TJDODBTool.getInstance().select("SELECT * FROM SYS_PATINFO WHERE MR_NO='"+mrNo+"'"));
			
			
			
			// 取得诊间信息
			TParm parmClinicRoom = getClinicRoomInf(clinicRoomNo);
			if (parmClinicRoom.getErrCode() < 0){
				re.setData("IsSuccessed", "1");
				re.setData("ErrorMsg", parmClinicRoom);
				return re;
			}

			if (parmClinicRoom.getCount() <= 0){
				re.setData("IsSuccessed", "1");
				re.setData("ErrorMsg", "Err:未取得诊间信息");
				return re;
			}

			// 取得班表信息
			TParm parmSchDay =getREGSchdayInfo(clinicRoomNo, regionCode, admType,
					date, sessionCode,drCode);
			if (parmSchDay.getErrCode() < 0)
				return parmSchDay;
			if (parmSchDay.getCount() <= 0){
				
				re.setData("IsSuccessed", "1");
				re.setData("ErrorMsg", "Err:未取得班表信息");
				return re;
			}
			
			if(parmSchDay.getBoolean("STOP_SESSION", 0)){
				re.setData("IsSuccessed", "1");
				re.setData("ErrorMsg", "Err:该班表已停诊");
				return re;
			}
			
			//查看该病人是否有预约信息
			String sql = "SELECT * FROM REG_PATADM" +
					" WHERE  MR_NO = '"+mrNo+"'" +
					" AND APPT_CODE = 'Y'" +
					" AND ARRIVE_FLG = 'N'" +
					" AND REGCAN_USER IS NULL" +
					" AND ADM_DATE = TO_DATE ('"+date+"', 'YYYYMMDD')" +
					" AND SESSION_CODE = '"+sessionCode+"'" +
					" AND CLINICROOM_NO = '"+clinicRoomNo+"'" +
					" AND DR_CODE = '"+drCode+"'" +
					" AND ADM_TYPE = '"+admType+"'";
			TParm apptParm = new TParm(TJDODBTool.getInstance().select(sql));
			if(apptParm.getCount() > 0){
				re.setData("IsSuccessed", "1");
				re.setData("ErrorMsg", "Err:该病人已有预约信息，请走预约报道流程");
				return re;
			}
			
			
			String regSql = "SELECT COUNT(CASE_NO) REG_COUNT FROM REG_PATADM WHERE "
					+ " TO_CHAR( ADM_DATE,'YYYYMMDD') = '"+date+"'"
					+ " AND SESSION_CODE = '"+sessionCode+"'"
					+ " AND REALDR_CODE = '"+drCode+"'"
					+ " AND MR_NO = '"+mrNo+"'"
					+ " AND REGCAN_USER IS NULL";
			TParm regParm = new TParm(TJDODBTool.getInstance().select(regSql));
			int regCount = regParm.getInt("REG_COUNT", 0);
			if(regCount >= 1){
				re.setData("IsSuccessed", "1");
				re.setData("ErrorMsg", "Err:此班表中已有该病人的挂号信息，不可以再进行挂号");
				return re;
			}
			
			
			
			// 取得数据库连接
			TAction tAction = new TAction();
			TConnection conn = tAction.getConnection();
			if (conn == null){
				re.setData("IsSuccessed", "1");
				re.setData("ErrorMsg", "Err:未取得数据库连接");
				return re;
			}

			if (parmSchDay.getValue("VIP_FLG", 0).equals("Y")) {
				vipFlg = true;
			}else{
				vipFlg = false;
			}
			
			// 取得诊间序号
			String queNo = getQueNo(parmSchDay, clinicRoomNo, admType, date,
					sessionCode, regionCode,"", conn);
			
			if (queNo.length() == 0) {
				conn.rollback();
				conn.close();
				re.setData("IsSuccessed", "1");
				re.setData("ErrorMsg", "Err:就诊号已满");
				return re;
			}
			
			// 取得门诊就诊顺序号
			if(caseNo.length() == 0){
				caseNo =SystemTool.getInstance().getNo("ALL", "REG",
		                "CASE_NO", "CASE_NO");
				if (caseNo.length() == 0){
					re.setData("IsSuccessed", "1");
					re.setData("ErrorMsg", "Err:未取得门诊顺序号");
					return re;
				}
			}
				
			String ctz1Code = patInfo.getValue("CTZ1_CODE", 0);
			
			if(ctz1Code.length() == 0){
				ctz1Code="99";
			}
			
			String serviceLevel = "1";		
			
			String ctz2Code=getCtz2Code(parmSchDay.getValue("CLINICTYPE_CODE", 0),patInfo);
			
			TParm parmReg = insertRegPatadm(mrNo, date, sessionCode, admType,
					deptCode, clinicRoomNo, drCode, regionCode, ctz1Code,ctz2Code,
					serviceLevel, queNo, caseNo, businessFrom,TerminalCode,parmClinicRoom, parmSchDay,conn);
			if (parmReg.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				parmReg.setData("IsSuccessed", "1");
				parmReg.setData("ErrorMsg", parmReg);
				return parmReg;
			}
			conn.commit();
			conn.close();
			
			
			

			re.setData("IsSuccessed", "0");
			re.setData("ErrorMsg", "");
			re.setData("RegId", caseNo);
			
			

			
		}else if("02".equals(lockType)){
			String sql1 = " SELECT REGION_CODE,CASE_NO,ARRIVE_FLG,VIP_FLG,CLINICROOM_NO,ADM_TYPE,"
				+ "        TO_CHAR(ADM_DATE,'YYYYMMDD') ADM_DATE,SESSION_CODE,"
				+ "        QUE_NO,DEPT_CODE,REGION_CODE,ARRIVE_FLG"
				+ " FROM REG_PATADM "
				+ " WHERE CASE_NO='"+caseNo+"'" 
//				+ " AND CLINICROOM_NO = '" + clinicRoomNo + "'"
//				+ " AND   REGION_CODE = '" + regionCode + "'"
//				+ " AND   ADM_TYPE = '" + admType + "'"
//				+ " AND   TO_CHAR(ADM_DATE,'YYYYMMDD') = '" + date + "'"
//				+ " AND   SESSION_CODE = '" + sessionCode + "'" 
				+ " AND REGCAN_USER IS NULL  ORDER BY CASE_NO";
			RegQEServiceImpl.writerLog2("退挂查询挂号信息==="+sql1);
			TParm parmRegInf = new TParm(
					TJDODBTool
							.getInstance()
							.select(sql1));
			if (parmRegInf.getErrCode() < 0) {
				re.setData("IsSuccessed", "1");
				re.setData("ErrorMsg", "Err" + parmRegInf);
				return re;
			}

			if (parmRegInf.getCount() <= 0) {
				re.setData("IsSuccessed", "1");
				re.setData("ErrorMsg", "Err:未找到病患预约信息");
				return re;

			}
			
			//判断该CASE_NO是否在bil_reg_recp表中有数据，有数数据则不能解锁该号
			String sql2 = "SELECT CASE_NO FROM BIL_REG_RECP WHERE CASE_NO='"+caseNo+"'";
			TParm bilRegParm = new TParm(TJDODBTool.getInstance().select(sql2));
			if(bilRegParm.getCount() > 0){
				re.setData("IsSuccessed", "1");
				re.setData("ErrorMsg", "Err:该病人已挂号完成，不能解锁");
				return re;
			}
			

			TAction tAction = new TAction();
			TConnection conn = tAction.getConnection();
			if (conn == null) {
				re.setData("IsSuccessed", "1");
				re.setData("ErrorMsg", "Err:未取得连接");
				return re;
			}
			

			TParm unRegparm = new TParm(TJDODBTool.getInstance().update(
					" DELETE FROM REG_PATADM " 
							+ " WHERE CASE_NO = '" + caseNo + "'", conn));
//			TParm unRegparm = updateUnReg(caseNo,businessFrom,conn);
			if (parm.getErrCode() < 0) {
				conn.rollback();
				conn.close();
				re.setData("IsSuccessed", "1");
				re.setData("ErrorMsg", "Err" + unRegparm);
				return re;
			}

			TParm parmQue = new TParm();
			if (parmRegInf.getValue("VIP_FLG", 0).equals("Y")) {
				vipFlg = true;
				parmQue = updateVIPQueNo(parmRegInf.getValue("CLINICROOM_NO", 0),
						parmRegInf.getValue("ADM_TYPE", 0), parmRegInf.getValue(
								"ADM_DATE", 0), parmRegInf.getValue("SESSION_CODE",
								0), parmRegInf.getValue("QUE_NO", 0), "N", conn);
				if (parmQue.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					re.setData("IsSuccessed", "1");
					re.setData("ErrorMsg", "Err" + parmQue);
					return re;

				}

			}else{
				
				vipFlg = false;
				parmQue = updateMaxQueNo(parmRegInf.getValue("CLINICROOM_NO", 0),
						parmRegInf.getValue("ADM_TYPE", 0), parmRegInf.getValue(
								"ADM_DATE", 0), parmRegInf.getValue("SESSION_CODE",
								0), parmRegInf.getValue("REGION_CODE", 0),  conn);
				if (parmQue.getErrCode() < 0) {
					conn.rollback();
					conn.close();
					re.setData("IsSuccessed", "1");
					re.setData("ErrorMsg", "Err" + parmQue);
					return re;

				}
				
			}

			conn.commit();
			conn.close();
			
			
			
			re.setData("IsSuccessed", "0");
			re.setData("ErrorMsg", "");
			re.setData("RegId", "");
			

			
		}
		
		if(vipFlg){
			TParm queParm = this.getVIPQueCount(clinicRoomNo, admType, regDate, sessionCode);
			if(queParm.getCount() > 0){
				remainsNum = queParm.getValue("QUE_NO", 0);
			}
			
		}else{
			TParm queParm = this.getQueCount(clinicRoomNo, admType, regDate, sessionCode);
			if(queParm.getCount() > 0){
				remainsNum = queParm.getValue("QUE_NO", 0);
			}
			
		}

		re.setData("RemainsNum", remainsNum);
		
		return re;
	}
	
	/**
	 * 取得诊间信息
	 * 
	 * @param clinicRoomNo
	 *            String
	 * @return TParm
	 */
	private TParm getClinicRoomInf(String clinicRoomNo) {
		TParm parmClinicRoom = new TParm(TJDODBTool.getInstance().select(
				" SELECT CLINICAREA_CODE " + " FROM REG_CLINICROOM "
						+ " WHERE CLINICROOM_NO = '" + clinicRoomNo + "'"));
		return parmClinicRoom;
	}
	
	/**
	 * 取得班表信息
	 * 
	 * @param clinicRoomNo
	 *            String
	 * @param regionCode
	 *            String
	 * @param admType
	 *            String
	 * @param date
	 *            String
	 * @param sessionCode
	 *            String
	 * @return TParm
	 */
	public TParm getREGSchdayInfo(String clinicRoomNo, String regionCode,
			String admType, String date, String sessionCode,String drCode) {
		TParm parmSchDay = new TParm(TJDODBTool.getInstance().select(
				" SELECT VIP_FLG,CLINICTYPE_CODE,QUE_NO,MAX_QUE,STOP_SESSION,REALDEPT_CODE " + " FROM REG_SCHDAY "
						+ " WHERE CLINICROOM_NO = '" + clinicRoomNo + "'"
						+ " AND   REGION_CODE = '" + regionCode + "'"
						+ " AND   ADM_TYPE = '" + admType + "'"
						+ " AND   ADM_DATE = '" + date + "'"
						+ " AND   REALDR_CODE = '" + drCode + "'"
						+ " AND   SESSION_CODE = '" + sessionCode + "'"
//						+ " AND   QUE_NO != MAX_QUE"
						));
		return parmSchDay;
	}
	
	/**
	 * 取得就诊序号
	 * 
	 * @param parmSchDay
	 *            TParm
	 * @param clinicRoomNo
	 *            String
	 * @param admType
	 *            String
	 * @param date
	 *            String
	 * @param sessionCode
	 *            String
	 * @param regionCode
	 *            String
	 * @param conn
	 *            TConnection
	 * @return String
	 */
	private String getQueNo(TParm parmSchDay, String clinicRoomNo,
			String admType, String date, String sessionCode, String regionCode,
			String periodCode,TConnection conn) {
		if (parmSchDay.getValue("VIP_FLG", 0).equals("Y")) {
			String startTimeS = "";
			String startTimeE = "";
			
			if(periodCode.length()>0){
				TParm perParm = getPeriodCode();			
				for (int i = 0; i < perParm.getCount(); i++) {
					if(periodCode.equals(perParm.getValue("CODE", i))){
						startTimeS=perParm.getValue("START_TIME_S", i);
						startTimeE=perParm.getValue("START_TIME_E", i);
						break;
					}
				}
			}

			TParm parmQueNo = getVIPQueNo(clinicRoomNo, admType, date,
					sessionCode,startTimeS,startTimeE);
			RegQEServiceImpl.writerLog2("parmQueNo==="+parmQueNo);
			if (parmQueNo.getErrCode() < 0)
				return "";
			if (parmQueNo.getCount() <= 0)
				return "";
			if (parmQueNo.getValue("QUE_NO", 0) == null
					|| parmQueNo.getInt("QUE_NO", 0) == 0
					|| parmQueNo.getValue("QUE_NO", 0).equalsIgnoreCase("null"))
				return "";
			TParm parm = updateVIPQueNo(clinicRoomNo, admType, date,
					sessionCode, parmQueNo.getValue("QUE_NO", 0), "Y",conn);
			if (parm.getErrCode() < 0)
				return "";
			//VIP也更新就诊序号
			TParm parm1 = updateQueNo(clinicRoomNo, admType, date, sessionCode,
					regionCode,conn);
			if (parm1.getErrCode() < 0)
				return "";
			//
			return parmQueNo.getValue("QUE_NO", 0);
			//
		} else {
			
			if(parmSchDay.getInt("QUE_NO", 0) > parmSchDay.getInt("MAX_QUE", 0)){
				return "";
			}
			
			TParm parm = updateQueNo(clinicRoomNo, admType, date, sessionCode,
					regionCode,conn);
			if (parm.getErrCode() < 0)
				return "";
			return parmSchDay.getValue("QUE_NO", 0);
		}
	}
	
	/**
	 * 取得VIP就诊序号
	 * 
	 * @param clinicRoomNo
	 *            String
	 * @param admType
	 *            String
	 * @param date
	 *            String
	 * @param sessionCode
	 *            String
	 * @return TParm
	 */
	private TParm getVIPQueNo(String clinicRoomNo, String admType, String date,
			String sessionCode,String startTimeS,String startTimeE) {
		
		String sql = " SELECT MIN(QUE_NO) QUE_NO " + " FROM REG_CLINICQUE "
				+ " WHERE CLINICROOM_NO = '" + clinicRoomNo + "'"
				+ " AND   ADM_TYPE = '" + admType + "'" + " AND   ADM_DATE = '"
				+ date + "'" + " AND   SESSION_CODE = '" + sessionCode + "'"
				+ " AND   QUE_STATUS = 'N'";
		
		if(startTimeS.length() > 0 && startTimeE.length() > 0){
			sql += " AND (START_TIME >= '"+startTimeS+"' AND START_TIME <'"+startTimeE+"')";
		}
		
		TParm parmQueNo = new TParm(TJDODBTool.getInstance().select(sql));
		return parmQueNo;
	}
	
	/**
	 * 取得VIP剩于的号数
	 * @param clinicRoomNo
	 * @param admType
	 * @param date
	 * @param sessionCode
	 * @return
	 */
	private TParm getVIPQueCount(String clinicRoomNo, String admType, String date,
			String sessionCode) {
		TParm parmQueNo = new TParm(TJDODBTool.getInstance().select(
				" SELECT COUNT(QUE_NO) QUE_NO " + " FROM REG_CLINICQUE "
						+ " WHERE CLINICROOM_NO = '" + clinicRoomNo + "'"
						+ " AND   ADM_TYPE = '" + admType + "'"
						+ " AND   ADM_DATE = '" + date + "'"
						+ " AND   SESSION_CODE = '" + sessionCode + "'"
						+ " AND   QUE_STATUS = 'N'"));
		return parmQueNo;
	}
	
	/**
	 * 普通号剩于多少号
	 * @param clinicRoomNo
	 * @param admType
	 * @param date
	 * @param sessionCode
	 * @return
	 */
	private TParm getQueCount(String clinicRoomNo, String admType, String date,
			String sessionCode) {
		TParm parmQueNo = new TParm(TJDODBTool.getInstance().select(
				" SELECT (MAX_QUE-QUE_NO+1) AS QUE_NO " + " FROM REG_SCHDAY "
						+ " WHERE CLINICROOM_NO = '" + clinicRoomNo + "'"
						+ " AND   ADM_TYPE = '" + admType + "'"
						+ " AND   ADM_DATE = '" + date + "'"
						+ " AND   SESSION_CODE = '" + sessionCode + "'"));
		return parmQueNo;
	}
	
	
	
	/**
	 * 更新VIP就诊顺序号
	 * 
	 * @param clinicRoomNo
	 *            String
	 * @param admType
	 *            String
	 * @param date
	 *            String
	 * @param sessionCode
	 *            String
	 * @param queNo
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	private TParm updateVIPQueNo(String clinicRoomNo, String admType,
			String date, String sessionCode, String queNo, String queStatus,TConnection conn) {
		TParm parm = new TParm(TJDODBTool.getInstance().update(
				" UPDATE REG_CLINICQUE" + " SET QUE_STATUS = '" + queStatus
						+ "'" + " WHERE CLINICROOM_NO = '" + clinicRoomNo + "'"
						+ " AND   ADM_TYPE = '" + admType + "'"
						+ " AND   ADM_DATE = '" + date + "'"
						+ " AND   SESSION_CODE = '" + sessionCode + "'"
						+ " AND   QUE_NO = '" + queNo + "'"));
		return parm;
	}
	
	/**
	 * 更新就诊序号
	 * 
	 * @param clinicRoomNo
	 *            String
	 * @param admType
	 *            String
	 * @param date
	 *            String
	 * @param sessionCode
	 *            String
	 * @param regionCode
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	private TParm updateQueNo(String clinicRoomNo, String admType, String date,
			String sessionCode, String regionCode,TConnection conn) {
		TParm parm = new TParm(TJDODBTool.getInstance()
				.update(
						" UPDATE REG_SCHDAY" + " SET QUE_NO = QUE_NO + 1"
								+ " WHERE CLINICROOM_NO = '" + clinicRoomNo
								+ "'" + " AND   REGION_CODE = '" + regionCode
								+ "'" + " AND   ADM_TYPE = '" + admType + "'"
								+ " AND   ADM_DATE = '" + date + "'"
								+ " AND   SESSION_CODE = '" + sessionCode + "'"
								+ " AND   CLINICROOM_NO = '" + clinicRoomNo
								+ "'"));
		return parm;
	}
	
	/**
	 * 还号时，普通 号要在最大号上加1
	 * @param clinicRoomNo
	 * @param admType
	 * @param date
	 * @param sessionCode
	 * @param regionCode
	 * @param conn
	 * @return
	 */
	private TParm updateMaxQueNo(String clinicRoomNo, String admType, String date,
			String sessionCode, String regionCode,TConnection conn) {
		TParm parm = new TParm(TJDODBTool.getInstance()
				.update(
						" UPDATE REG_SCHDAY" + " SET MAX_QUE = MAX_QUE + 1"
								+ " WHERE CLINICROOM_NO = '" + clinicRoomNo
								+ "'" + " AND   REGION_CODE = '" + regionCode
								+ "'" + " AND   ADM_TYPE = '" + admType + "'"
								+ " AND   ADM_DATE = '" + date + "'"
								+ " AND   SESSION_CODE = '" + sessionCode + "'"
								+ " AND   CLINICROOM_NO = '" + clinicRoomNo
								+ "'"));
		return parm;
	}
	
	
	
	/**
	 * 写挂号主档
	 * 
	 * @param mrNo
	 *            String
	 * @param date
	 *            String
	 * @param sessionCode
	 *            String
	 * @param admType
	 *            String
	 * @param deptCode
	 *            String
	 * @param clinicRoomNo
	 *            String
	 * @param drCode
	 *            String
	 * @param regionCode
	 *            String
	 * @param ctz1Code
	 *            String
	 * @param serviceLevel
	 *            String
	 * @param queNo
	 *            String
	 * @param caseNo
	 *            String
	 * @param parmClinicRoom
	 *            TParm
	 * @param parmSchDay
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	private TParm insertRegPatadm(String mrNo, String date, String sessionCode,
			String admType, String deptCode, String clinicRoomNo,
			String drCode, String regionCode, String ctz1Code,String ctz2Code,
			String serviceLevel, String queNo, String caseNo,
			String businessFrom,String TerminalCode,
			TParm parmClinicRoom, TParm parmSchDay,TConnection conn) {
		
		TParm parm = new TParm(
				TJDODBTool
						.getInstance()
						.update(
								" INSERT INTO REG_PATADM ("
								+ "       REALDEPT_CODE,OPT_TERM,DEPT_CODE,APPT_CODE,QUE_NO,"
								+ "       REGMETHOD_CODE,ADM_STATUS,DR_CODE,HEAT_FLG,OPT_USER,"
								+ "       MR_NO,CLINICROOM_NO,ADM_DATE,SESSION_CODE,VISIT_CODE,"
								+ "       CLINICAREA_CODE,VIP_FLG,CLINICTYPE_CODE,REGION_CODE,REPORT_STATUS,"
								+ "       CASE_NO,ADM_REGION,ARRIVE_FLG,CTZ1_CODE,SERVICE_LEVEL,"
								+ "       ADM_TYPE,REG_DATE,REALDR_CODE,OPT_DATE,CTZ2_CODE)"
								+ " VALUES('"
								+ deptCode
								+ "','"+TerminalCode+"','"
								+ deptCode
								+ "','N','"  
								+ queNo     //1-5
								+ "',"
								+ "       'N','1','" 
								+ drCode
								+ "','N','"+businessFrom+"'," //6-10
								+ "       '"
								+ mrNo
								+ "','"
								+ clinicRoomNo
								+ "',TO_DATE('"
								+ date
								+ "','YYYYMMDD'),'"
								+ sessionCode
								+ "','0',"    //11-15
								+ "       '"
								+ parmClinicRoom.getValue(
										"CLINICAREA_CODE", 0)
								+ "','"
								+ parmSchDay.getValue("VIP_FLG", 0)
								+ "','"
								+ parmSchDay.getValue(
										"CLINICTYPE_CODE", 0)
								+ "','"
								+ regionCode
								+ "','1',"  //16-20
								+ "       '"
								+ caseNo
								+ "','"
								+ regionCode
								+ "','N','"
								+ ctz1Code
								+ "','"
								+ serviceLevel   //21-25
								+ "',"
								+ "       '"
								+ admType
								+ "',SYSDATE,'"
								+ drCode + "',SYSDATE,'"+ctz2Code+"')", conn));
		//RegQEServiceImpl.writerLog2("=====sql=============="+sql);
		
		return parm;
	}
	
	
	/**
	 * 预约写挂号主档
	 * 
	 * @param mrNo
	 *            String
	 * @param date
	 *            String
	 * @param sessionCode
	 *            String
	 * @param admType
	 *            String
	 * @param deptCode
	 *            String
	 * @param clinicRoomNo
	 *            String
	 * @param drCode
	 *            String
	 * @param regionCode
	 *            String
	 * @param ctz1Code
	 *            String
	 * @param serviceLevel
	 *            String
	 * @param queNo
	 *            String
	 * @param caseNo
	 *            String
	 * @param parmClinicRoom
	 *            TParm
	 * @param parmSchDay
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	private TParm insertRegApp(String mrNo, String date, String sessionCode,
			String admType, String deptCode, String clinicRoomNo,
			String drCode, String regionCode, String ctz1Code, String ctz2Code,
			String serviceLevel, String queNo, String caseNo,String businessFrom,String TerminalCode,
			String regmethodCode,TParm parmClinicRoom, TParm parmSchDay,String orderNo,TConnection conn) {
		
		TParm parm = new TParm(
				TJDODBTool
						.getInstance()
						.update(
								" INSERT INTO REG_PATADM ("
								+ "       REALDEPT_CODE,OPT_TERM,DEPT_CODE,APPT_CODE,QUE_NO,"
								+ "       REGMETHOD_CODE,ADM_STATUS,DR_CODE,HEAT_FLG,OPT_USER,"
								+ "       MR_NO,CLINICROOM_NO,ADM_DATE,SESSION_CODE,VISIT_CODE,"
								+ "       CLINICAREA_CODE,VIP_FLG,CLINICTYPE_CODE,REGION_CODE,REPORT_STATUS,"
								+ "       CASE_NO,ADM_REGION,ARRIVE_FLG,CTZ1_CODE,SERVICE_LEVEL,"
								+ "       ADM_TYPE,REG_DATE,REALDR_CODE,OPT_DATE,QEAPP_FlG,CTZ2_CODE)"
								+ " VALUES('"
								+ parmSchDay.getValue("REALDEPT_CODE", 0)
								+ "','"+TerminalCode+"','"
								+ parmSchDay.getValue("REALDEPT_CODE", 0)
								+ "','Y','"  
								+ queNo     //1-5
								+ "',"
								+ " '"+regmethodCode+"','1','" 
								+ drCode
								+ "','N','"+businessFrom+"'," //6-10
								+ "       '"
								+ mrNo
								+ "','"
								+ clinicRoomNo
								+ "',TO_DATE('"
								+ date
								+ "','YYYYMMDD'),'"
								+ sessionCode
								+ "','0',"    //11-15
								+ "       '"
								+ parmClinicRoom.getValue(
										"CLINICAREA_CODE", 0)
								+ "','"
								+ parmSchDay.getValue("VIP_FLG", 0)
								+ "','"
								+ parmSchDay.getValue(
										"CLINICTYPE_CODE", 0)
								+ "','"
								+ regionCode
								+ "','1',"  //16-20
								+ "       '"
								+ caseNo
								+ "','"
								+ regionCode
								+ "','N','"
								+ ctz1Code
								+ "','"
								+ serviceLevel   //21-25
								+ "',"
								+ "       '"
								+ admType
								+ "',SYSDATE,'"
								+ drCode + "',SYSDATE,'Y','"+ctz2Code+"')", conn));
		//RegQEServiceImpl.writerLog2("=====sql=============="+sql);
		return parm;
	}
	
	
	
	
	/**
	 * 将入参的的大写字母变成小写字母
	 * @param inXml
	 * @return
	 */
	public String updateXml(String inXml) {
		String t, t2;
		List<String> sl = new ArrayList<String>();
		for (int i = 0; i < inXml.length(); i++) {
			t2 = inXml.charAt(i) + "";
			if (i > 0) {
				t = inXml.charAt(i - 1) + "";
				if ("<".equals(t) && t2.equals(t2.toUpperCase())) {
					sl.add(t2.toLowerCase());
				} else if ("/".equals(t) && t2.equals(t2.toUpperCase())) {
					sl.add(t2.toLowerCase());
				} else {
					sl.add(t2);
				}
			} else {
				sl.add(t2);
			}
		}

		StringBuilder sb = new StringBuilder();

		for (String string : sl) {
			sb.append(string);
		}
		
		return sb.toString();
	}
	
	public TParm getRegDept(){
		String sql = "SELECT DEPT_CODE, DEPT_CHN_DESC, DESCRIPTION,  SEQ," +
				" CASE WHEN ACTIVE_FLG = 'Y' THEN 1 ELSE 0 END ACTIVE_FLG FROM SYS_DEPT WHERE OPD_FIT_FLG = 'Y' AND CLASSIFY = '0'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	public TParm getRegDR() {
		String sql = " SELECT OP.USER_ID AS DR_CODE,OP.USER_NAME,C.POS_CODE,C.POS_CHN_DESC,OP.SEX_CODE CHN_DESC," +
				" OP.ID_NO, CASE WHEN SYSDATE BETWEEN OP.ACTIVE_DATE AND OP.END_DATE THEN '1' ELSE '0' END AS IS_ENABLED" +
				" FROM SYS_OPERATOR OP,SYS_POSITION B, SYS_POSITION C WHERE " +
				" OP.POS_CODE = B.POS_CODE AND B.POS_TYPE = '1' AND OP.POS_CODE = C.POS_CODE";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	public TParm getRegClinictype() {
		String sql = "SELECT CLINICTYPE_CODE, CLINICTYPE_DESC, (REG_PRICE + CLINIC_PRICE) AS SUM_PRICE," +
				" REG_PRICE,CLINIC_PRICE FROM (SELECT CASE" +
				"  WHEN A.CLINICTYPE_CODE IS NULL THEN B.CLINICTYPE_CODE ELSE A.CLINICTYPE_CODE END CLINICTYPE_CODE," +
				" CASE WHEN A.CLINICTYPE_DESC IS NULL THEN B.CLINICTYPE_DESC  ELSE A.CLINICTYPE_DESC END CLINICTYPE_DESC," +
				" CASE WHEN A.CLINICTYPE_CODE IS NULL THEN B.OWN_PRICE  ELSE 0 END REG_PRICE, " +
				" CASE WHEN A.CLINICTYPE_CODE IS NULL THEN 0 ELSE A.OWN_PRICE END CLINIC_PRICE" +
				" FROM (SELECT A.CLINICTYPE_CODE, A.CLINICTYPE_DESC, B.RECEIPT_TYPE, C.OWN_PRICE" +
				" FROM REG_CLINICTYPE A, REG_CLINICTYPE_FEE B, SYS_FEE C" +
				" WHERE A.CLINICTYPE_CODE = B.CLINICTYPE_CODE AND B.ORDER_CODE = C.ORDER_CODE" +
				" AND B.RECEIPT_TYPE = 'CLINIC_FEE') A FULL OUTER JOIN" +
				" (SELECT A.CLINICTYPE_CODE, A.CLINICTYPE_DESC, B.RECEIPT_TYPE, C.OWN_PRICE" +
				" FROM REG_CLINICTYPE A, REG_CLINICTYPE_FEE B, SYS_FEE C" +
				"  WHERE A.CLINICTYPE_CODE = B.CLINICTYPE_CODE" +
				" AND B.ORDER_CODE = C.ORDER_CODE" +
				" AND B.RECEIPT_TYPE = 'REG_FEE') B" +
				"  ON A.CLINICTYPE_CODE = B.CLINICTYPE_CODE)" +
				" ORDER BY CLINICTYPE_CODE";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	public TParm getRegDeptMT(){
		String sql = "SELECT B.DEPT_CODE, A.MT_DISEASE_CODE," +
				" CASE WHEN B.ACTIVE_FLG = 'Y' THEN '1' ELSE '0' END ISENABLED" +
				" FROM INS_MT_DISEASE A, SYS_DEPT B" +
				" WHERE A.DEPT_CODE = B.DEPT_CODE";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
		
	}
	
	public TParm getRegSchday(String sDate,String eDate){
//		String sql = " SELECT * FROM (SELECT A.REGION_CODE||'#'|| A.ADM_TYPE||'#'" +
//				"||A.ADM_DATE||'#'|| A.SESSION_CODE|| '#'|| A.CLINICROOM_NO AS ID," +
//				" TO_CHAR(TO_DATE( A.ADM_DATE,'YYYY-MM-DD'),'YYYY-MM-DD') ADM_DATE," +
//				" CASE (  7 - TO_NUMBER ( NEXT_DAY (TO_DATE (A.ADM_DATE, 'YYYY-MM-DD')," +
//				" 1) - TO_DATE (A.ADM_DATE, 'YYYY-MM-DD'))) WHEN 0 THEN 7  ELSE (  7" +
//				"  - TO_NUMBER (  NEXT_DAY (TO_DATE (A.ADM_DATE, 'YYYY-MM-DD'), 1)" +
//				"  - TO_DATE (A.ADM_DATE, 'YYYY-MM-DD'))) END AS WEEK_DAY," +
//				" CASE A.SESSION_CODE WHEN '01' THEN 'A' WHEN '02' THEN 'P' END SESSION_CODE," +
//				" A.REALDR_CODE,A.REALDEPT_CODE,A.CLINICTYPE_CODE,  A.MAX_QUE," +
//				" (SELECT COUNT (ADM_DATE)  FROM REG_CLINICQUE  WHERE REG_CLINICQUE.ADM_DATE = A.ADM_DATE" +
//				" AND A.SESSION_CODE = REG_CLINICQUE.SESSION_CODE AND A.CLINICROOM_NO = REG_CLINICQUE.CLINICROOM_NO" +
//				" AND REG_CLINICQUE.QUE_STATUS = 'N') AS QUE, '1' AS ISENABLED, CASE B.QUE_STATUS WHEN 'N' THEN 1 ELSE 0 END AS ISAPPOINT," +
//				" CASE A.STOP_SESSION WHEN 'N' THEN '' ELSE '0' END AS STOP_SESSION," +
//				"  B.START_TIME  FROM REG_SCHDAY A, REG_CLINICQUE B" +
//				" WHERE A.CLINICTMP_FLG='N'  AND   A.ADM_DATE = B.ADM_DATE AND A.SESSION_CODE = B.SESSION_CODE" +
//				" AND A.CLINICROOM_NO = B.CLINICROOM_NO AND A.VIP_FLG = 'Y'" +
//				" AND A.ADM_DATE BETWEEN '"+sDate+"' AND '"+eDate+"'" +
//				" UNION ALL" +
//				" SELECT    A.REGION_CODE || '#' || A.ADM_TYPE || '#' || A.ADM_DATE || '#' || A.SESSION_CODE" +
//				" || '#' || A.CLINICROOM_NO AS ID, TO_CHAR(TO_DATE( A.ADM_DATE,'YYYY-MM-DD'),'YYYY-MM-DD') ADM_DATE," +
//				" CASE (  7 - TO_NUMBER ( NEXT_DAY (TO_DATE (A.ADM_DATE, 'YYYY-MM-DD'), 1) - TO_DATE (A.ADM_DATE, 'YYYY-MM-DD')))" +
//				" WHEN 0  THEN  7 ELSE (  7 - TO_NUMBER ( NEXT_DAY (TO_DATE (A.ADM_DATE, 'YYYY-MM-DD'), 1)" +
//				" - TO_DATE (A.ADM_DATE, 'YYYY-MM-DD'))) END AS WEEK_DAY," +
//				" CASE A.SESSION_CODE WHEN '01' THEN 'A' WHEN '02' THEN 'P' END SESSION_CODE, A.REALDR_CODE," +
//				" A.REALDEPT_CODE, A.CLINICTYPE_CODE, A.MAX_QUE,(A.MAX_QUE - A.QUE_NO + 1) QUE, '1' AS ISENABLED," +
//				"  1 AS ISAPPOINT, CASE A.STOP_SESSION WHEN 'N' THEN '' ELSE '0' END  AS STOP_SESSION,  '' START_TIME" +
//				"  FROM REG_SCHDAY A WHERE A.CLINICTMP_FLG='N'  AND A.VIP_FLG = 'N' AND A.ADM_TYPE = 'O' " +
//				" AND A.ADM_DATE BETWEEN '"+sDate+"' AND '"+eDate+"')" +
//				" ORDER BY ID, ADM_DATE, SESSION_CODE,REALDR_CODE,REALDEPT_CODE,CLINICTYPE_CODE,START_TIME";
		
		//普通 号
		
		String sql = schdaySql(sDate, eDate, "N");
		
//		RegQEServiceImpl.writerLog2(sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()< 0){
			parm = new TParm();
		}
		
		//vip号
		String vipSql = schdaySql(sDate, eDate, "Y");
		TParm vipParm = new TParm(TJDODBTool.getInstance().select(vipSql));
		TParm periodCode = getPeriodCode();
		for (int i = 0; i < vipParm.getCount(); i++) {
			String []names = vipParm.getValue("ID", i).split("#");
			String clinicRoomNo=names[4];
			String date=names[2];
			String sessionCode=names[3];
			String sessionDesc = vipParm.getValue("SESSION_CODE", i);
			for (int j = 0; j < periodCode.getCount("CODE"); j++) {
				if(sessionDesc.equals(periodCode.getValue("SESSION_CODE", j))){
					
					String stratTimeS=periodCode.getValue("START_TIME_S", j);
					String stratTimeE=periodCode.getValue("START_TIME_E", j);
					
					parm.addData("ID", vipParm.getValue("ID", i));
					parm.addData("ADM_DATE", vipParm.getValue("ADM_DATE", i));
					parm.addData("WEEK_DAY", vipParm.getValue("WEEK_DAY", i));
					parm.addData("SESSION_CODE", vipParm.getValue("SESSION_CODE", i));
					parm.addData("REALDR_CODE", vipParm.getValue("REALDR_CODE", i));
					parm.addData("REALDEPT_CODE", vipParm.getValue("REALDEPT_CODE", i));
					parm.addData("CLINICTYPE_CODE", vipParm.getValue("CLINICTYPE_CODE", i));
					parm.addData("MAX_QUE", getVipQueNoCount(date, sessionCode, clinicRoomNo, stratTimeS, stratTimeE, ""));
					parm.addData("QUE", getVipQueNoCount(date, sessionCode, clinicRoomNo, stratTimeS, stratTimeE, "N"));
					parm.addData("ISENABLED", vipParm.getValue("ISENABLED", i));
					parm.addData("ISAPPOINT", vipParm.getValue("ISAPPOINT", i));
					parm.addData("STOP_SESSION", vipParm.getValue("STOP_SESSION", i));
					parm.addData("START_TIME", periodCode.getValue("CODE", j));
					parm.addData("REG_SPECIAL_NUMBER", vipParm.getValue("REG_SPECIAL_NUMBER", i));
				}
				
			}
			
			
		}
		
		parm.setCount(parm.getCount("ID"));
		
		return parm;
	}
	
	public int getVipQueNoCount(String admDate,String sessionCode,String clinicroomNo,String stratTimeS,String stratTimeE,String queStatus){
		
		String sql = "SELECT  COUNT(QUE_NO) QUE_NO FROM REG_CLINICQUE WHERE " +
				" ADM_DATE = '"+admDate+"'" +
				" AND SESSION_CODE = '"+sessionCode+"'" +
				" AND CLINICROOM_NO = '"+clinicroomNo+"'" +
				" AND (START_TIME >= '"+stratTimeS+"' AND START_TIME <'"+stratTimeE+"')";
		
		if(queStatus.length() > 0){
			sql+=" AND QUE_STATUS='N'";
		}
		
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getInt("QUE_NO", 0);
				
		
	}
	

	public String schdaySql(String sDate,String eDate,String vipFlg){
		String sql = " SELECT    A.REGION_CODE || '#' || A.ADM_TYPE || '#' || A.ADM_DATE || '#' || A.SESSION_CODE" +
		" || '#' || A.CLINICROOM_NO || '#' || A.REALDR_CODE  AS ID, TO_CHAR(TO_DATE( A.ADM_DATE,'YYYY-MM-DD'),'YYYY-MM-DD') ADM_DATE," +
		" CASE (  7 - TO_NUMBER ( NEXT_DAY (TO_DATE (A.ADM_DATE, 'YYYY-MM-DD'), 1) - TO_DATE (A.ADM_DATE, 'YYYY-MM-DD')))" +
		" WHEN 0  THEN  7 ELSE (  7 - TO_NUMBER ( NEXT_DAY (TO_DATE (A.ADM_DATE, 'YYYY-MM-DD'), 1)" +
		" - TO_DATE (A.ADM_DATE, 'YYYY-MM-DD'))) END AS WEEK_DAY," +
		" CASE A.SESSION_CODE WHEN '01' THEN 'A' WHEN '02' THEN 'P' END SESSION_CODE, A.REALDR_CODE," +
		" A.REALDEPT_CODE, A.CLINICTYPE_CODE, A.MAX_QUE,(A.MAX_QUE - A.QUE_NO + 1) QUE, '1' AS ISENABLED," +
		"  1 AS ISAPPOINT, CASE A.STOP_SESSION WHEN 'N' THEN '' ELSE '0' END  AS STOP_SESSION,"
		+ "  A.SESSION_CODE AS START_TIME " +
//		 ", B.CHN_DESC REG_SPECIAL_NUMBER" +
		"  FROM REG_SCHDAY A"+
//		 ",SYS_DICTIONARY B "+
		" WHERE A.CLINICTMP_FLG='N'  AND A.VIP_FLG = '"+vipFlg+"' AND A.ADM_TYPE = 'O' " +
		" AND A.ADM_DATE BETWEEN '"+sDate+"' AND '"+eDate+"'" +
//		" AND A.REG_SPECIAL_NUMBER = B.ID(+) AND B.GROUP_ID(+)='REG_SPECIAL_NUMBER'" +
		" ORDER BY ID, ADM_DATE, SESSION_CODE,REALDR_CODE,REALDEPT_CODE,CLINICTYPE_CODE";
		RegQEServiceImpl.writerLog2("SCHDAY----"+sql);
		return sql;
	}
	
	/**
	 * 得到病患信息
	 * @param cardNo
	 * @param cardType
	 * @param terminalCode
	 * @return
	 */
	public TParm getPatInfo(String cardType,String cardNo){
		
//		String cardType = bodyParams.getCardType().trim();
//		String cardNo = bodyParams.getCardNo().trim();
		
		if(cardType.equals("01")){//医疗卡PatientID CureCardNo
			String sql = "SELECT  A.CARD_NO CURECARDNO,B.IDNO IDCARDNO, '' MEDCARDNO,B.MR_NO,C.CURRENT_BALANCE, " +
			" CASE A.WRITE_FLG WHEN 'Y' THEN '1' WHEN 'N' THEN '0' ELSE '' END  CARDSTATUS ," +
			" FLOOR(MONTHS_BETWEEN(TO_DATE(CONCAT(EXTRACT(YEAR FROM SYSDATE),'-10-31'),'YYYY-MM-DD'),B.BIRTH_DATE)/12) AGE," +
			" B.PAT_NAME,B.SEX_CODE,B.BIRTH_DATE ,B.TEL_HOME,B.ADDRESS " +
			" FROM EKT_MASTER C,SYS_PATINFO B,EKT_ISSUELOG A WHERE A.CARD_NO = C.CARD_NO(+) AND A.MR_NO = B.MR_NO " +
			" AND A.WRITE_FLG='Y' ";
			sql +=  " AND A.CARD_NO = '"+cardNo+"' " ;
			RegQEServiceImpl.writerLog2("getPatInfo--01-sql::::"+sql);
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));		
			return parm;

		}else if(cardType.equals("02")){//身份证 IDCardNo
			String sql2= "SELECT MR_NO,MERGE_FLG,MERGE_TOMRNO FROM SYS_PATINFO WHERE TRIM(IDNO) = '"+cardNo+"'";
			RegQEServiceImpl.writerLog2("getPatInfo-02--sql::::"+sql2);			
			TParm idParm = new TParm(TJDODBTool.getInstance().select(sql2));
			RegQEServiceImpl.writerLog2("getPatInfo-02-返回Parm---"+idParm);
			if(idParm.getCount() > 1){
				List<String> mList = new ArrayList<String>();
				for (int i = 0; i < idParm.getCount(); i++) {
					String mrNO = idParm.getValue("MERGE_TOMRNO", i);
					if(mrNO.length() > 0 && !mList.contains(mrNO)){
						mList.add(mrNO);
						RegQEServiceImpl.writerLog2(mrNO);
					}
					
				}
				RegQEServiceImpl.writerLog2("mList.size()-----"+mList.size());
				if(mList.size() == 1){
					
					String sql1 = "SELECT A.CARD_NO CURECARDNO, B.IDNO IDCARDNO, '' MEDCARDNO, B.MR_NO, '' CURRENT_BALANCE," +
							" CASE A.WRITE_FLG WHEN 'Y' THEN '1' WHEN 'N' THEN '0' ELSE '' END CARDSTATUS," +
							" FLOOR(MONTHS_BETWEEN( TO_DATE( CONCAT( EXTRACT(YEAR FROM SYSDATE), '-10-31'), 'YYYY-MM-DD'), B.BIRTH_DATE) / 12) AGE," +
							" B.PAT_NAME, B.SEX_CODE, B.BIRTH_DATE, B.TEL_HOME, B.ADDRESS" +
							" FROM SYS_PATINFO B, EKT_ISSUELOG A" +
							" WHERE  B.MR_NO = A.MR_NO(+)" +
							" AND A.WRITE_FLG(+) = 'Y'" +
							" AND B.MR_NO='"+mList.get(0)+"'" +
							" ORDER BY A.CARD_NO DESC";
							RegQEServiceImpl.writerLog2("getPatInfo---sql1::::"+sql1);
							TParm parm = new TParm(TJDODBTool.getInstance().select(sql1));
							RegQEServiceImpl.writerLog2("getPatInfo-02-返回Parm---"+parm);
							if(parm.getCount() > 0){
								parm.setData("CURRENT_BALANCE", 0, getEktAmt(parm.getValue("MR_NO", 0)));
								parm.setCount(1);
							}
							return parm;
					
					
				}else{
					return idParm;
				}
				
			}else{
				String sql1 = "SELECT A.CARD_NO CURECARDNO, B.IDNO IDCARDNO, '' MEDCARDNO, B.MR_NO, '' CURRENT_BALANCE," +
						" CASE A.WRITE_FLG WHEN 'Y' THEN '1' WHEN 'N' THEN '0' ELSE '' END CARDSTATUS," +
						" FLOOR(MONTHS_BETWEEN( TO_DATE( CONCAT( EXTRACT(YEAR FROM SYSDATE), '-10-31'), 'YYYY-MM-DD'), B.BIRTH_DATE) / 12) AGE," +
						" B.PAT_NAME, B.SEX_CODE, B.BIRTH_DATE, B.TEL_HOME, B.ADDRESS" +
						" FROM SYS_PATINFO B, EKT_ISSUELOG A" +
						" WHERE  B.MR_NO = A.MR_NO(+)" +
						" AND A.WRITE_FLG(+) = 'Y'" +
						" AND TRIM(B.IDNO) = '"+cardNo+"'" +
						" ORDER BY A.CARD_NO DESC";
				RegQEServiceImpl.writerLog2("getPatInfo---sql::::"+sql1);
				TParm parm = new TParm(TJDODBTool.getInstance().select(sql1));			
				if(parm.getCount() > 0){
					parm.setData("CURRENT_BALANCE", 0, getEktAmt(parm.getValue("MR_NO", 0)));
					parm.setCount(1);
				}
				RegQEServiceImpl.writerLog2("getPatInfo-022-返回Parm---"+parm);
				return parm;
			}
			
			
			
		}else if(cardType.equals("04")){//病案号
			String sql1 = "SELECT A.CARD_NO CURECARDNO, B.IDNO IDCARDNO, '' MEDCARDNO, B.MR_NO, '' CURRENT_BALANCE," +
			" CASE A.WRITE_FLG WHEN 'Y' THEN '1' WHEN 'N' THEN '0' ELSE '' END CARDSTATUS," +
			" FLOOR(MONTHS_BETWEEN( TO_DATE( CONCAT( EXTRACT(YEAR FROM SYSDATE), '-10-31'), 'YYYY-MM-DD'), B.BIRTH_DATE) / 12) AGE," +
			" B.PAT_NAME, B.SEX_CODE, B.BIRTH_DATE, B.TEL_HOME, B.ADDRESS" +
			" FROM SYS_PATINFO B, EKT_ISSUELOG A" +
			" WHERE  B.MR_NO = A.MR_NO(+)" +
			" AND A.WRITE_FLG(+) = 'Y'" +
			" AND B.MR_NO='"+cardNo+"'" +
			" ORDER BY A.CARD_NO DESC";
			RegQEServiceImpl.writerLog2("getPatInfo--04-sql::::"+sql1);
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql1));
			if(parm.getCount() > 0){
				parm.setData("CURRENT_BALANCE", 0, getEktAmt(parm.getValue("MR_NO", 0)));
				parm.setCount(1);
			}
			return parm;

		}else{
			TParm result = new TParm();
			result.setErrCode(-1);
			result.setErrText("传入卡类型为"+cardType+",与HIS中类型不符，没有查询数据");
			return result;
		}
		
		
	}
	
	
	
	/**
	 * 查询指定医生出诊时间段的剩余号数
	 * @param bodyParams
	 * @return
	 */
	public TParm getRemainsSumBy(BodyParams bodyParams){
		
		String startDate = bodyParams.getStartDate().trim().replaceAll("-", "").replaceAll("/", "").substring(0,8);
		String endDate = bodyParams.getEndDate().trim().replaceAll("-", "").replaceAll("/", "").substring(0,8);
		String doctorCode = bodyParams.getDoctorCode().trim();
		String deptCode = bodyParams.getDeptCode().trim();
		
		String sql = "SELECT (MAX_QUE - QUE_NO + 1) REMAINSNUMBER, SESSION_CODE SXW,ADM_DATE, TO_DATE (ADM_DATE, 'YYYY-MM-DD') VISITDATE,VIP_FLG,ADM_TYPE,CLINICROOM_NO " +
				" FROM REG_SCHDAY WHERE CLINICTMP_FLG='N' AND ADM_DATE >= '"+startDate+"' AND  ADM_DATE <= '"+endDate+"' " +
				" AND REALDR_CODE = '"+doctorCode+"' AND REALDEPT_CODE = '"+deptCode+"' AND ADM_TYPE = 'O' ORDER BY ADM_DATE ";
		RegQEServiceImpl.writerLog2("sql0:::"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		for(int i = 0; i < parm.getCount(); i++){
			if(parm.getValue("VIP_FLG",i).equals("Y")){
				sql = " SELECT COUNT (QUE_NO) REMAINSNUMBER" +
						" FROM REG_CLINICQUE WHERE ADM_DATE = '"+parm.getValue("ADM_DATE",i)+"' " +
						" AND SESSION_CODE = '"+parm.getValue("SXW",i)+"' "+
						" AND CLINICROOM_NO = '"+parm.getValue("CLINICROOM_NO",i)+"' "+
						" AND ADM_TYPE = '"+parm.getValue("ADM_TYPE",i)+"' "+
						" AND QUE_STATUS = 'N' ";
				//RegQEServiceImpl.writerLog2("sql::::"+i+":::"+sql);
				
				TParm result = new TParm(TJDODBTool.getInstance().select(sql));
				if(result.getCount() > 0){
					parm.setData("REMAINSNUMBER", i, result.getInt("REMAINSNUMBER",0));
				}
			}
			if("01".equals(parm.getValue("SXW",i))){
				parm.setData("SXW", i, "A");
			}else if("02".equals(parm.getValue("SXW",i))){
				parm.setData("SXW", i, "P");
			}else{
				parm.setData("SXW", i, "");
			}
		}
		
		return parm;
	}
	
	/**
	 * 获取医生指定日期和时段的剩余号数
	 * @param bodyParams
	 * @return
	 */
	public TParm getRemaingSum(BodyParams bodyParams){
		String visitDate = bodyParams.getVisitDate().trim().replaceAll("-", "").replaceAll("/", "").substring(0,8);
		String sxw ="";
		if("A".equals(bodyParams.getSxw())){
			sxw = "01";
		}else if("P".equals(bodyParams.getSxw())){
			sxw = "02";
		}
		String doctorCode = bodyParams.getDoctorCode().trim();
		String deptCode = bodyParams.getDeptCode().trim();
		String terminalCode = bodyParams.getTerminalCode();
		//院区+就诊类别+就诊日期+时段+诊间
		//String visitCode = bodyParams.getVisitCode();//可空
		//String [] names = visitCode.split("#");
		String sql = "SELECT CLINICROOM_NO, ADM_TYPE,VIP_FLG,'' PERIODCODE, '' PERIODMEANING,(MAX_QUE - QUE_NO + 1) REMAINSNUMBER," +
		" CASE SESSION_CODE WHEN '01' THEN 'A' WHEN '02' THEN 'P' ELSE '' END SESSION_CODE " +
		" FROM REG_SCHDAY" +
		" WHERE ADM_DATE = '"+visitDate+"'" +
		" AND SESSION_CODE = '"+sxw+"'" +
		" AND REALDR_CODE = '"+doctorCode+"'" +
		" AND REALDEPT_CODE = '"+deptCode+"'" +
		" AND CLINICTMP_FLG='N'  AND ADM_TYPE = 'O' ORDER BY ADM_DATE ";
		RegQEServiceImpl.writerLog2("sql::"+sql); 
		TParm re = new TParm(TJDODBTool.getInstance().select(sql));
		TParm data = new TParm();
		int size = 0;
		TParm periodCode = getPeriodCode();
		for(int i = 0; i < re.getCount(); i++){
			if(re.getValue("VIP_FLG",i).equals("Y")){
				String sessionCode = re.getValue("SESSION_CODE",i);
				for(int m = 0; m < periodCode.getCount("CODE"); m++){
					if(sessionCode.equals(periodCode.getValue("SESSION_CODE",m))){
						String stratTimeS=periodCode.getValue("START_TIME_S", m);
						String stratTimeE=periodCode.getValue("START_TIME_E", m);
						data.addData("PERIODCODE", periodCode.getValue("CODE",m));
						data.addData("PERIODMEANING", stratTimeS.substring(0,2)+":"+stratTimeS.substring(2,4)+"-"+stratTimeE.substring(0,2)+":"+stratTimeE.substring(2,4));
						data.addData("REMAINSNUMBER", getVipQueNoCount(visitDate, sxw, re.getValue("CLINICROOM_NO",i), stratTimeS, stratTimeE, "N"));
						size++;
					}
				}
				
			}else{
				if("01".equals(sxw)){
					data.addData("PERIODCODE", sxw);
					data.addData("PERIODMEANING", "08:40-11:00");
					data.addData("REMAINSNUMBER", re.getInt("REMAINSNUMBER",i));
				}else if("02".equals(sxw)){
					data.addData("PERIODCODE", sxw);
					data.addData("PERIODMEANING", "13:00-16:30");
					data.addData("REMAINSNUMBER", re.getInt("REMAINSNUMBER",i));
				}
				size++;
			}
		}
		data.setCount(size);
		if(size > 0){
			return data;
		}
		
		return re;
	}
	
	
	/**
	 * 查询预约列表
	 * @param bodyParams
	 * @return
	 */
	public TParm getAppList(BodyParams bodyParams){//01手机  02自助机 ，03社区医生与HIS挂号方式关联
		
		String startDate = bodyParams.getStartDate().trim().replaceAll("-", "").replaceAll("/", "").substring(0,8);
		String endDate = bodyParams.getEndDate().trim().replaceAll("-", "").replaceAll("/", "").substring(0,8);
		String cardNo = bodyParams.getCardNo();//可空 
		String cardType = bodyParams.getCardType();//可空
		String terminalCode = bodyParams.getTerminalCode().trim();
		// PERIODCODE PERIODMEANING DEPTCODE DEPTNAME DOCTORCODE DOCTORNAME VISITTYPECODE VISITTYPENAME ISEMERGENCY ISCOST
		String sql = " SELECT B.PAT_NAME PATIENTNAME,A.CASE_NO APPOINTCODE, A.CASE_NO HISAPPOINTCODE,A.MR_NO HISPATIENTID ," +//患者姓名，预约编号，HIS中的唯一预约编号
				" A.REGMETHOD_CODE APPOINTTYPE, " +//预约方式
				" CASE A.ARRIVE_FLG WHEN 'Y' THEN '3' WHEN 'N' THEN '1' ELSE '' END APPOINTSTATUS," +//预约状态 Code
				" CASE A.ARRIVE_FLG WHEN 'Y' THEN '预约取号' WHEN 'N' THEN '预约确认' ELSE '' END APPOINTSTATUSMEANING, " +//预约状态含义
				" TO_CHAR(A.REG_DATE,'YYYY-MM-DD') APPOINTTIME,TO_CHAR(A.ADM_DATE,'YYYY-MM-DD') VISITDATE,CASE A.SESSION_CODE WHEN '01' THEN 'A' WHEN '02' THEN 'P' ELSE '' END SXW, "+//预约时间,就诊日期,上下午Code
				" CASE A.SESSION_CODE WHEN '01' THEN '上午' WHEN '02' THEN '下午' ELSE '' END APWMEANING,"+//上下午含义
				" '' PERIODCODE, '' PERIODMEANING,"+//时段Code,时段含义
				" A.REALDEPT_CODE DEPTCODE,D.DEPT_CHN_DESC DEPTNAME,"+//科室Code,科室名称
				" A.REALDR_CODE DOCTORCODE,C.USER_NAME DOCTORNAME," +//医生Code,医生姓名
				" A.CLINICTYPE_CODE VISITTYPECODE  ,E.ORDER_DESC  VISITTYPENAME," +//号别Code,号别名称
				" '0' ISEMERGENCY , CASE A.ARRIVE_FLG WHEN 'Y' THEN '1' WHEN 'N' THEN '0' ELSE '' END ISCOST, "+//是否急诊,是否已支付
				" A.VIP_FLG,A.SESSION_CODE,A.ADM_TYPE,A.CLINICROOM_NO,TO_CHAR(A.ADM_DATE,'yyyyMMdd') ADM_DATE,QUE_NO "+
				" FROM REG_PATADM A,SYS_PATINFO B,SYS_OPERATOR C,SYS_DEPT D,REG_CLINICTYPE_FEE E  @ " +
				" WHERE A.APPT_CODE = 'Y' AND REGCAN_USER IS NULL " +
				" AND A.ADM_DATE BETWEEN TO_DATE('"+startDate+"','yyyyMMdd') AND TO_DATE('"+endDate+"','yyyyMMdd')" +
				" AND A.MR_NO = B.MR_NO " +
				" AND A.REALDR_CODE = C.USER_ID " +
				" AND A.REALDEPT_CODE = D.DEPT_CODE" +
				" AND A.CLINICTYPE_CODE = E.CLINICTYPE_CODE" +
				"  ";
		if(cardNo!=null && !"".equals(cardNo) && cardType != null && !"".equals(cardType)){
			if("01".equals(cardType)){
				sql = sql.replaceFirst("@", " ,EKT_ISSUELOG F");
				sql += " AND A.MR_NO = F.MR_NO AND F.CARD_NO = '"+cardNo+"' AND F.WRITE_FLG = 'Y' ";
			}else if("02".equals(cardType)){
				sql = sql.replaceFirst("@", "");
				sql += " AND B.IDNO = '"+cardNo+"'";
			}else if("03".equals(cardType)){
				sql = sql.replaceFirst("@", ",(SELECT DISTINCT MR_NO  FROM INS_MZ_CONFIRM WHERE INSCARD_NO = '"+cardNo+"') F");
				sql += " AND A.MR_NO = F.MR_NO";
			}else {
				sql = sql.replaceFirst("@", "");
			}
		}else {
			sql = sql.replaceFirst("@", "");
		}
		RegQEServiceImpl.writerLog2("sql::"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		TParm periodCode = getPeriodCode();
		for(int i = 0; i < parm.getCount(); i++){
			if(parm.getValue("VIP_FLG",i).equals("Y")){
				String sessionCode = "";
				if("01".equals(parm.getValue("SESSION_CODE",i))){
					sessionCode = "A";
				}else if("02".equals(parm.getValue("SESSION_CODE",i))){
					sessionCode = "P";
				}
				for(int m = 0; m < periodCode.getCount("CODE"); m++){
					if(sessionCode.equals(periodCode.getValue("SESSION_CODE",m))){
						String stratTimeS=periodCode.getValue("START_TIME_S", m);
						String stratTimeE=periodCode.getValue("START_TIME_E", m);
						
						String vipSql = "SELECT  * FROM REG_CLINICQUE WHERE " +
						" ADM_DATE = '"+parm.getValue("ADM_DATE",i)+"'" +
						" AND SESSION_CODE = '"+parm.getValue("SESSION_CODE",i)+"'" +
						" AND CLINICROOM_NO = '"+parm.getValue("CLINICROOM_NO",i)+"'" +
						" AND QUE_NO = '"+parm.getValue("QUE_NO",i)+"'" +
						" AND (START_TIME >= '"+stratTimeS+"' AND START_TIME <'"+stratTimeE+"')";
						//RegQEServiceImpl.writerLog2(i+":::"+m+"::"+vipSql);
						TParm vipParm = new TParm(TJDODBTool.getInstance().select(vipSql));
						if(vipParm.getCount() > 0){
							parm.setData("PERIODCODE", i, periodCode.getValue("CODE",m));//
							parm.setData("PERIODMEANING", i, stratTimeS.substring(0,2)+":"+stratTimeS.substring(2,4)+"-"+stratTimeE.substring(0,2)+":"+stratTimeE.substring(2,4));
							continue;
						}
					}
				}
			}else{
				if("01".equals(parm.getValue("SESSION_CODE",i))){
					parm.setData("PERIODCODE", i, parm.getValue("SESSION_CODE",i));
					parm.setData("PERIODMEANING",i, "08:30-12:00");
					
				}else if("02".equals(parm.getValue("SESSION_CODE",i))){
					parm.setData("PERIODCODE",i, parm.getValue("SESSION_CODE",i));
					parm.setData("PERIODMEANING",i, "13:00-17:30");
				}
			}
			
		}
		return parm;
	}
	
	/**
	 * 向线下同步所有的医生的剩余号数
	 * @param bodyParams
	 * @return
	 */
	public TParm getAllDoctorNum(BodyParams bodyParams){
		String startDate = bodyParams.getStartDate().trim().replaceAll("-", "").replaceAll("/", "").substring(0,8);
		String endDate = bodyParams.getEndDate().trim().replaceAll("-", "").replaceAll("/", "").substring(0,8);
		//院区+就诊类别+就诊日期+时段+诊间
		String sql = "SELECT MAX_QUE TOTALNUM,(MAX_QUE - QUE_NO + 1) REMAINSNUM," +
				" REGION_CODE||'#'||ADM_TYPE||'#'||ADM_DATE||'#'||SESSION_CODE||'#'||CLINICROOM_NO ||'#'|| REALDR_CODE VISITCODE,'01' APPOINTTYPE, "+
				" SESSION_CODE  PERIOD," +
				" VIP_FLG,ADM_DATE,SESSION_CODE,ADM_TYPE,CLINICROOM_NO " +
				" FROM REG_SCHDAY WHERE CLINICTMP_FLG='N'  AND ADM_DATE >= '"+startDate+"' AND ADM_DATE <= '"+endDate+"' AND ADM_TYPE = 'O' " +
				" ORDER BY ADM_DATE " ;
		RegQEServiceImpl.writerLog2("sql:::"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		TParm data = new TParm();
		int size = 0;
		TParm periodCode = getPeriodCode();
		for(int i = 0; i < parm.getCount(); i++){
			if(parm.getValue("VIP_FLG",i).equals("Y")){
				String sessionCode = "";
				if("01".equals(parm.getValue("SESSION_CODE",i))){
					sessionCode = "A";
				}else if("02".equals(parm.getValue("SESSION_CODE",i))){
					sessionCode = "P";
				}
				for(int m = 0; m < periodCode.getCount("CODE"); m++){
					if(sessionCode.equals(periodCode.getValue("SESSION_CODE",m))){
						String stratTimeS=periodCode.getValue("START_TIME_S", m);
						String stratTimeE=periodCode.getValue("START_TIME_E", m);
						data.addData("PERIOD", stratTimeS.substring(0,2)+":"+stratTimeS.substring(2,4)+"-"+stratTimeE.substring(0,2)+":"+stratTimeE.substring(2,4));
						data.addData("TOTALNUM", getVipQueNoCount(parm.getValue("ADM_DATE",i), parm.getValue("SESSION_CODE",i),
								parm.getValue("CLINICROOM_NO",i), stratTimeS, stratTimeE, ""));
						data.addData("REMAINSNUM", getVipQueNoCount(parm.getValue("ADM_DATE",i), parm.getValue("SESSION_CODE",i),
								parm.getValue("CLINICROOM_NO",i), stratTimeS, stratTimeE, "N"));
						data.addData("VISITCODE", parm.getValue("VISITCODE",i));
						data.addData("APPOINTTYPE", parm.getValue("APPOINTTYPE",i));
						size++;
					}
				}
			}else{
				data.addData("VISITCODE", parm.getValue("VISITCODE",i));
				data.addData("APPOINTTYPE", parm.getValue("APPOINTTYPE",i));
				data.addData("PERIOD", parm.getValue("PERIOD",i));
				data.addData("TOTALNUM", parm.getValue("TOTALNUM",i));
				data.addData("REMAINSNUM", parm.getValue("REMAINSNUM",i));
				size++;
			}
		}
		data.setCount(size);
		if(data.getCount() > 0){
			return data;
		}
		return parm;
	}
	
	public TParm getRegInfo(String caseNo){
		String sql="SELECT * FROM REG_PATADM WHERE CASE_NO='"+caseNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	public TParm getRegFee(String clinictypeCode,String receiptType){
		String sql = "SELECT  B.OWN_PRICE FROM REG_CLINICTYPE_FEE A,SYS_FEE B " +
				" WHERE A.ORDER_CODE = B.ORDER_CODE AND " +
				" A.RECEIPT_TYPE='"+receiptType+"' " +
				" AND A.CLINICTYPE_CODE='"+clinictypeCode+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
		
	}

	
	public TParm saveBilRegRecp(TParm parm,TConnection conn){

		String sql = "INSERT INTO BIL_REG_RECP " +
				"(CASE_NO,RECEIPT_NO,ADM_TYPE,REGION_CODE,MR_NO," +
				"RESET_RECEIPT_NO,PRINT_NO,BILL_DATE,CHARGE_DATE,PRINT_DATE," +
				"REG_FEE,REG_FEE_REAL,CLINIC_FEE,CLINIC_FEE_REAL,SPC_FEE," +
				"OTHER_FEE1,OTHER_FEE2,OTHER_FEE3,AR_AMT,PAY_CASH," +
				"PAY_BANK_CARD,PAY_CHECK,PAY_MEDICAL_CARD,PAY_INS_CARD,PAY_DEBIT," +
				"PAY_INS,CASH_CODE," +
				"OPT_USER,OPT_DATE,OPT_TERM,ORDER_NO," +
				"ALIPAY,RE_SOURCE,QE_PAY_TYPE) " +
				" VALUES " +
				"('"+parm.getValue("CASE_NO")+"','"+parm.getValue("RECEIPT_NO")+"','"+
				parm.getValue("ADM_TYPE")+"','"+parm.getValue("REGION_CODE")+"','"+parm.getValue("MR_NO")+"'," +
				" '','',SYSDATE,SYSDATE,''," +
				"'"+parm.getValue("REG_FEE")+"','"+parm.getValue("REG_FEE")+"','"+
				parm.getValue("CLINIC_FEE")+"','"+parm.getValue("CLINIC_FEE")+"',0," +
				" 0,0,0,'"+parm.getValue("AR_AMT")+"','"+parm.getValue("PAY_CASH")+"'," +
				" '"+parm.getValue("PAY_BANK_CARD")+"','"+parm.getValue("PAY_CHECK")+"'," +
				"'"+parm.getValue("PAY_MEDICAL_CARD")+"','"+parm.getValue("PAY_INS_CARD")+"','"+parm.getValue("PAY_DEBIT")+"'," +
				" '"+parm.getValue("PAY_INS")+"','"+parm.getValue("CASH_CODE")+"'," +
				" '"+parm.getValue("OPT_USER")+"',SYSDATE,'"+parm.getValue("OPT_TERM")+"','"+parm.getValue("ORDER_NO")+"'," +
				" "+parm.getValue("ALIPAY")+",'"+parm.getValue("RE_SOURCE")+"','"+parm.getValue("QE_PAY_TYPE")+"')";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
		return result;

	}
	
	public TParm updateRegPatadm(String caseNo, String insPayType, String confirmNo,TConnection conn){
		String sql = "UPDATE REG_PATADM SET ARRIVE_FLG='Y',INS_PAT_TYPE='"+insPayType+"',CONFIRM_NO='"+confirmNo+"' WHERE CASE_NO='"+caseNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, conn));
		return result;
	}
	
	public TParm updateUnReg(String caseNo,String businessFrom ,TConnection conn){
	 TParm parm = new TParm(TJDODBTool.getInstance().update(
				" UPDATE REG_PATADM" + " SET REGCAN_USER = '" + businessFrom
						+ "'," + "     REGCAN_DATE = SYSDATE"
						+ " WHERE CASE_NO = '" + caseNo + "'", conn));
	 return parm;
	}
	
	public TParm updateBilRegRecp(String caseNo,String receiptNo,String resetReceiptNo,TConnection conn){
		 TParm parm = new TParm(TJDODBTool.getInstance().update(
					" UPDATE BIL_REG_RECP" + " SET RESET_RECEIPT_NO = '" + resetReceiptNo
							+ "' WHERE CASE_NO = '" + caseNo + "' AND RECEIPT_NO ='"+receiptNo+"'", conn));
		 return parm;
		
	}
	

	
	public TParm getBilRegRecp(String caseNo){
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT * FROM REG_PATADM" 
						+ " WHERE CASE_NO = '" + caseNo + "' AND RESET_RECEIPT_NO IS NULL AND AR_AMT >= 0"));
		
		return parm;
	}
	
	public TParm getRegPatadm(String caseNo){
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				" SELECT * FROM REG_" 
						+ " WHERE CASE_NO = '" + caseNo + "' AND REGCAN_USER IS NULL "));
		
		return parm;
	}
	
	public TParm insertEktTrade(TParm parm,TConnection conn){

		String sql="INSERT INTO EKT_TRADE(" +
				"TRADE_NO, CARD_NO, MR_NO," +
				"CASE_NO, PAT_NAME,OLD_AMT," +
				" AMT, STATE, BUSINESS_TYPE," +
				"GREEN_BALANCE,GREEN_BUSINESS_AMT," +
				"OPT_USER, OPT_DATE,OPT_TERM)" +
				" VALUES" +
				"('"+parm.getValue("TRADE_NO")+"', '"+parm.getValue("CARD_NO")+"', '"+parm.getValue("MR_NO")+"'," +
				"'"+parm.getValue("CASE_NO")+"','"+parm.getValue("PAT_NAME")+"','"+parm.getValue("OLD_AMT")+"'," +
				" '"+parm.getValue("AMT")+"', '"+parm.getValue("STATE")+"','"+parm.getValue("BUSINESS_TYPE")+"'," +
				"'"+parm.getValue("GREEN_BALANCE")+"','"+parm.getValue("GREEN_BUSINESS_AMT")+"'," +
				"'QeApp', SYSDATE,'"+parm.getValue("OPT_TERM")+"')";
		RegQEServiceImpl.writerLog2("insertEktTrade=="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
		return result;
	}
	
	public TParm insertEKTDetail(TParm parm,TConnection conn){
		String sql = "INSERT INTO EKT_ACCNTDETAIL(" +
				"BUSINESS_NO,BUSINESS_SEQ,CARD_NO," +
				"MR_NO,CASE_NO,ORDER_CODE,RX_NO," +
				"SEQ_NO,CHARGE_FLG,ORIGINAL_BALANCE," +
				"BUSINESS_AMT,CURRENT_BALANCE," +
				"CASHIER_CODE,BUSINESS_DATE,BUSINESS_STATUS," +
				"ACCNT_STATUS,OPT_USER,OPT_DATE,OPT_TERM)" +
				"VALUES(" +
				"'"+parm.getValue("BUSINESS_NO")+"','"+parm.getValue("BUSINESS_SEQ")+"','"+parm.getValue("CARD_NO")+"'," +
				"'"+parm.getValue("MR_NO")+"','"+parm.getValue("CASE_NO")+"','"+parm.getValue("ORDER_CODE")+"','"+parm.getValue("RX_NO")+"'," +
				" "+parm.getValue("SEQ_NO")+",'"+parm.getValue("CHARGE_FLG")+"',"+parm.getDouble("ORIGINAL_BALANCE")+"," +
				" "+parm.getDouble("BUSINESS_AMT")+","+parm.getDouble("CURRENT_BALANCE")+"," +
				" '"+parm.getValue("CASHIER_CODE")+"',SYSDATE,'"+parm.getValue("BUSINESS_STATUS")+"'," +
				" '"+parm.getValue("ACCNT_STATUS")+"','"+parm.getValue("OPT_USER")+"',SYSDATE,'"+parm.getValue("OPT_TERM")+"')";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
		return result;
	}
	
	public TParm insertEKTBilPay(TParm parm,TConnection conn){
		String sql = "INSERT INTO EKT_BIL_PAY(" +
				"CARD_NO,CURT_CARDSEQ,ACCNT_TYPE," +
				"MR_NO,ID_NO,NAME," +
				"AMT,CREAT_USER,OPT_USER," +
				" OPT_DATE,OPT_TERM,BIL_BUSINESS_NO," +
				"GATHER_TYPE,STORE_DATE,PROCEDURE_AMT) " +
				"VALUES(" +
				"'"+parm.getValue("CARD_NO")+"','"+parm.getValue("CURT_CARDSEQ")+"','"+parm.getValue("ACCNT_TYPE")+"'," +
				"'"+parm.getValue("MR_NO")+"','"+parm.getValue("ID_NO")+"','"+parm.getValue("NAME")+"'," +
				" "+parm.getDouble("AMT")+",'"+parm.getValue("CREAT_USER")+"','"+parm.getValue("OPT_USER")+"'," +
				" SYSDATE,'"+parm.getValue("OPT_TERM")+"','"+parm.getValue("BIL_BUSINESS_NO")+"'," +
				"'"+parm.getValue("GATHER_TYPE")+"',SYSDATE,"+parm.getDouble("PROCEDURE_AMT")+") ";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
		return result;
	}
	
	
	public TParm getEktMaster(String cardNo){
		String sql = "SELECT * FROM EKT_MASTER WHERE CARD_NO='"+cardNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	public TParm updateEktMaster(String cardNo,double currentBalance,String optUser,String optTerm,TConnection conn){
		String sql = "UPDATE EKT_MASTER SET CURRENT_BALANCE="+currentBalance+"," +
				"OPT_USER='"+optUser+"',OPT_TERM='"+optTerm+"',OPT_DATE=SYSDATE  WHERE CARD_NO='"+cardNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
		return result;
	}
	
	
	public TParm updateEktTrade(String treadNo,String optUser,String optTerm,String treadNoR,TConnection conn){
		String sql = "UPDATE EKT_TRADE SET STATE='3', RESET_TRADE_NO='"+treadNoR+"'," +
				" OPT_TERM='"+optTerm+"',OPT_DATE=SYSDATE WHERE TRADE_NO='"+treadNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
		return result;
	}
	
	public TParm updateEktMasterHistoryByCaseNo(String tradeNo,TConnection conn){
		String sql = "UPDATE EKT_MASTER_HISTORY SET CASE_NO='' WHERE CASE_NO='"+tradeNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
		return result;
	}
	
	
	public TParm getEktIssuelog(String cardNo){
		String sql = "SELECT * FROM EKT_ISSUELOG WHERE CARD_NO='"+cardNo+"'  AND WRITE_FLG='Y'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	public TParm getEktIssuelogByMrNo(String mrNo){
		String sql = "SELECT * FROM EKT_ISSUELOG WHERE MR_NO='"+mrNo+"'  AND WRITE_FLG='Y'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	public TParm getEktParm(String caseNo){
		String sql = "SELECT A.MR_NO,B.CARD_NO,C.CURRENT_BALANCE,D.PAT_NAME,D.IDNO " +
				" FROM REG_PATADM A,EKT_ISSUELOG B,EKT_MASTER C,SYS_PATINFO D" +
				"  WHERE A.CASE_NO='"+caseNo+"'" +
				" AND A.MR_NO = B.MR_NO" +
				" AND B.WRITE_FLG='Y'" +
				" AND B.CARD_NO=C.CARD_NO" +
				" AND A.MR_NO=D.MR_NO ORDER BY B.CARD_NO DESC";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	public String getPatName(String mrNo){
		String sql = "SELECT PAT_NAME FROM SYS_PATINFO WHERE MR_NO='"+mrNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result.getValue("PAT_NAME", 0);
	}
	
	public TParm insertEktAppTrade(TParm parm,TConnection conn){

		String sql="INSERT INTO EKT_APP_TRADE(" +
				"TRADE_NO, CARD_NO, MR_NO," +
				" ORDER_NO,AMT," +
				"BUSINESS_APP_TYPE,ORDER_TIME," +
				"OPT_USER, OPT_DATE,OPT_TERM,BIL_BUSINESS_NO)" +
				" VALUES" +
				"('"+parm.getValue("TRADE_NO")+"', '"+parm.getValue("CARD_NO")+"', '"+parm.getValue("MR_NO")+"'," +
				" '"+parm.getValue("ORDER_NO")+"','"+parm.getValue("AMT")+"'," +
				" '"+parm.getValue("BUSINESS_APP_TYPE")+"',TO_DATE('"+parm.getValue("ORDER_TIME")+"','YYYYMMDDHH24MISS')," +
				" '"+parm.getValue("OPT_USER")+"', SYSDATE,'"+parm.getValue("OPT_TERM")+"','"+parm.getValue("BIL_BUSINESS_NO")+"')";
//		RegQEServiceImpl.writerLog2(sql);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
		return result;
	}
	
	public String getTradeNo(String orderNo){
		String sql="SELECT TRADE_NO FROM EKT_APP_TRADE WHERE ORDER_NO='"+orderNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result.getValue("TRADE_NO", 0);
	}
	
	public TParm getEktAppTrade(String orderNo){
		String sql="SELECT * FROM EKT_APP_TRADE WHERE ORDER_NO='"+orderNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	public TParm getConfirmNo(String caseNo){
		String sql = "SELECT * FROM INS_MZ_CONFIRM WHERE CASE_NO='"+caseNo+"' AND INS_CROWD_TYPE='1' AND INS_PAT_TYPE='1'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
		
	}
	
	public TParm updateEktTradeCaseNo(String treadNo,String caseNo,TConnection conn){
		String sql = "UPDATE EKT_TRADE SET CASE_NO='"+caseNo+"' " +
				" WHERE TRADE_NO='"+treadNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
		return result;
	}
	
	public TParm updateEktTradeAmt(String treadNo,double amt,TConnection conn){
		String sql = "UPDATE EKT_TRADE SET AMT="+amt+
				" WHERE TRADE_NO='"+treadNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
		return result;
	}
	
	public TParm updateEktMasterHistoryAmt(String mrNo,String seqNo,double amt,TConnection conn){
		String sql = "UPDATE EKT_MASTER_HISTORY SET BUSSINESS_AMT="+amt+" , CURRENT_AMT=LATEST_AMT-"+amt+
				" WHERE MR_NO='"+mrNo+"' AND SEQ_NO='"+seqNo+"'";
		RegQEServiceImpl.writerLog2(sql);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
		return result;
	}
	
	public TParm getEktTrade(String tradeNo){
		String sql = "SELECT * FROM EKT_TRADE WHERE TRADE_NO='"+tradeNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	
	/**
	 * 班表对应的时段
	 * @return
	 */
	public TParm getPeriodCode(){
		TParm parm = new TParm();
		parm.addData("START_TIME_S", "0830");
		parm.addData("START_TIME_E", "0930");
		parm.addData("CODE", "08");
		parm.addData("SESSION_CODE", "A");
		
		parm.addData("START_TIME_S", "0930");
		parm.addData("START_TIME_E", "1030");
		parm.addData("CODE", "09");
		parm.addData("SESSION_CODE", "A");
		
		parm.addData("START_TIME_S", "1030");
		parm.addData("START_TIME_E", "1100");
		parm.addData("CODE", "10");
		parm.addData("SESSION_CODE", "A");
		
//		parm.addData("START_TIME_S", "1130");
//		parm.addData("START_TIME_E", "1230");
//		parm.addData("CODE", "11");
//		parm.addData("SESSION_CODE", "A");
//		
//		parm.addData("START_TIME_S", "1230");
//		parm.addData("START_TIME_E", "1330");
//		parm.addData("CODE", "12");
//		parm.addData("SESSION_CODE", "A");
		
		parm.addData("START_TIME_S", "1330");
		parm.addData("START_TIME_E", "1430");
		parm.addData("CODE", "13");
		parm.addData("SESSION_CODE", "P");
		
		parm.addData("START_TIME_S", "1430");
		parm.addData("START_TIME_E", "1530");
		parm.addData("CODE", "14");
		parm.addData("SESSION_CODE", "P");
		
		parm.addData("START_TIME_S", "1530");
		parm.addData("START_TIME_E", "1600");
		parm.addData("CODE", "15");
		parm.addData("SESSION_CODE", "P");
		
//		parm.addData("START_TIME_S", "1630");
//		parm.addData("START_TIME_E", "1730");
//		parm.addData("CODE", "16");
//		parm.addData("SESSION_CODE", "P");
//		
//		parm.addData("START_TIME_S", "1730");
//		parm.addData("START_TIME_E", "1830");
//		parm.addData("CODE", "17");		
//		parm.addData("SESSION_CODE", "P");
		
		parm.setCount(parm.getCount("CODE"));
		return parm;
		
	}
	
	public TParm getRegPatadmList(String sDate,String eDate){
		String sql = "SELECT A.MR_NO,D.INSCARD_NO,C.IDNO,B.REG_FEE_REAL,B.CLINIC_FEE_REAL," +
				" B.AR_AMT,A.REALDEPT_CODE,A.REALDR_CODE,A.CLINICTYPE_CODE," +
				" A.REGION_CODE || '#' || A.ADM_TYPE || '#' || TO_CHAR(A.ADM_DATE,'YYYYMMDD') || '#' || A.SESSION_CODE || '#' || A.CLINICROOM_NO|| '#' || A.REALDR_CODE AS ID," +
				" A.CASE_NO,C.PAT_NAME,C.SEX_CODE,A.OPT_USER,A.CONFIRM_NO,A.CASE_NO REGID," +
				" FLOOR(MONTHS_BETWEEN(TO_DATE(CONCAT(EXTRACT(YEAR FROM SYSDATE),'-10-31'),'YYYY-MM-DD'),C.BIRTH_DATE)/12) AGE," +
				" TO_CHAR(A.REG_DATE,'YYYY-MM-DD HH24:MI:SS') REG_DATE,A.QUE_NO,B.PAY_MEDICAL_CARD" +
				" ,E.INS_PAT_TYPE,E.INS_CROWD_TYPE,E.TOT_AMT AS INS_TOT_AMT," +
				" E.UNACCOUNT_PAY_AMT,E.UNREIM_AMT,E.ACCOUNT_PAY_AMT," +
				" E.TOTAL_AGENT_AMT,E.ARMY_AI_AMT,E.FLG_AGENT_AMT,E.ILLNESS_SUBSIDY_AMT," +
				" E.OINSTOT_AMT,E.INS_PAY_AMT,E.REIM_TYPE,D.DISEASE_CODE" +
				" ,F.USER_NAME DR_DESC,G.DEPT_CHN_DESC DEPT_DESC,A.SESSION_CODE ,TO_CHAR( A.ADM_DATE,'YYYY-MM-DD') ADM_DATE,A.REGMETHOD_CODE " +
				" FROM REG_PATADM A,BIL_REG_RECP B,SYS_PATINFO C,INS_MZ_CONFIRM D,INS_OPD E" +
				" ,SYS_OPERATOR F,SYS_DEPT G " +
				" WHERE A.CASE_NO = B.CASE_NO AND A.MR_NO = C.MR_NO AND A.ADM_TYPE='O' " +
				" AND A.REGCAN_USER IS NULL " +
				" AND A.CASE_NO = D.CASE_NO(+) AND A.CONFIRM_NO = D.CONFIRM_NO(+)" +
				" AND A.CASE_NO = E.CASE_NO(+) AND A.CONFIRM_NO = E.CONFIRM_NO(+)" +
				" AND A.REALDEPT_CODE = G.DEPT_CODE AND A.REALDR_CODE = F.USER_ID" +
				" AND B.BILL_DATE BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+eDate+"','YYYYMMDDHH24MISS')" +
				" ORDER BY A.CASE_NO";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	public TParm getRxNoNotBillList(String caseNo){
		String sql = "  SELECT A.RX_NO, A.RX_TYPE,'' AS RX_AMT, B.DEPT_CHN_DESC, " +
				" TO_CHAR(A.ORDER_DATE,'YYYY-MM-DD HH24:MI:SS') ORDER_DATE," +
				" A.ORDER_CODE, A.ORDER_DESC, A.SPECIFICATION, A.MEDI_QTY, C.UNIT_CHN_DESC," +
				" A.DOSAGE_QTY, A.OWN_PRICE, A.AR_AMT, D.CHN_DESC, A.SETMAIN_FLG, A.HIDE_FLG," +
				" A.CAT1_TYPE, A.ORDERSET_CODE,A.ORDERSET_GROUP_NO,E.UNIT_CHN_DESC PRICE_UNIT  " +
				" FROM OPD_ORDER A, SYS_DEPT B, SYS_UNIT C,SYS_DICTIONARY D,SYS_UNIT E " +
				" WHERE  CASE_NO = '"+caseNo+"'" +
				" AND A.BILL_FLG = 'N'" +
				" AND A.EXEC_DEPT_CODE = B.DEPT_CODE(+)" +
				" AND A.MEDI_UNIT = C.UNIT_CODE" +
				" AND A.REXP_CODE = D.ID" +
				" AND D.GROUP_ID = 'SYS_CHARGE'" +
				" AND A.DOSAGE_UNIT = E.UNIT_CODE" +
				" AND A.RELEASE_FLG='N'" +
				" AND A.RX_TYPE <> '6'" +
				" ORDER BY A.CASE_NO, A.RX_NO, A.SEQ_NO";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	public TParm getBilRegRecpList(String receiptNo){
		String sql = "SELECT A.MR_NO,A.CASE_NO,B.PAT_NAME,A.AR_AMT,A.PAY_MEDICAL_CARD," +
				" A.PAY_INS_CARD,A.BILL_DATE,A.OPT_USER,A.RECEIPT_NO " +
				" , A.INS_TYPE,A.ACCOUNT_PAY,RESET_RECEIPT_NO" +				
				" FROM BIL_OPB_RECP A,SYS_PATINFO B" +
				" WHERE A.MR_NO = B.MR_NO" +
				" AND A.RECEIPT_NO='"+receiptNo+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	public TParm getBilOpbRecpDetail(String receiptNo){
		String sql = "SELECT A.CASE_NO,A.RX_NO,A.SEQ_NO,A.ORDER_CODE,A.ORDER_DESC," +
				" A.SPECIFICATION,A.DOSAGE_QTY,A.AR_AMT,A.ORDERSET_GROUP_NO," +
				" A.ORDERSET_CODE,A.CAT1_TYPE,A.SETMAIN_FLG,A.HIDE_FLG" +
				" FROM OPD_ORDER A,BIL_OPB_RECP B " +
				" WHERE B.RECEIPT_NO='"+receiptNo+"' AND B.CASE_NO=A.CASE_NO " +
				" ORDER BY A.RX_NO,A.SEQ_NO";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	public TParm getRxNoPhaCount(String rxNo){
		String sql = "SELECT PHA_DISPENSE_CODE FROM OPD_ORDER WHERE RX_NO='"+rxNo+"' AND CAT1_TYPE='PHA'  AND RELEASE_FLG='N' AND RX_TYPE <> '6'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	public TParm getRxNoBillList(String sDate,String eDate){
		String sql = "SELECT A.CASE_NO, A.RX_NO, A.RX_TYPE, SUM (A.AR_AMT) AR_AMT" +
				" FROM OPD_ORDER A" +
				" WHERE A.BILL_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+eDate+"', 'YYYYMMDDHH24MISS')" +
				" AND A.BILL_FLG='Y'" +
				" AND A.ADM_TYPE='O'" +
				" AND A.RELEASE_FLG='N'" +
				" AND A.RX_TYPE <> '6'" +
				" GROUP BY A.CASE_NO,A.RX_NO,A.RX_TYPE" +
				" ORDER BY A.CASE_NO,A.RX_NO";
		RegQEServiceImpl.writerLog2("getRxNoBillList=="+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	
	}
	
	public TParm getOpdDiage(String caseNo){
		String sql = "  SELECT B.ICD_CHN_DESC, TO_CHAR(A.ORDER_DATE,'YYYY-MM-DD HH24:MI:SS') ORDER_DATE" +
				" FROM OPD_DIAGREC A, SYS_DIAGNOSIS B" +
				" WHERE A.CASE_NO = '"+caseNo+"' AND A.ICD_CODE = B.ICD_CODE" +
				" ORDER BY A.MAIN_DIAG_FLG DESC";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	public String getOpdOrderBillDate(String caseNo,String rxNo){
		String sql = "SELECT TO_CHAR(BILL_DATE,'YYYY-MM-DD HH24:MI:SS') BILL_DATE FROM OPD_ORDER " +
				" WHERE CASE_NO='"+caseNo+"' AND RX_NO='"+rxNo+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//		RegQEServiceImpl.writerLog2("getOpdOrderBillDate=="+sql);
		return parm.getValue("BILL_DATE", 0);
	}
	
	
	public TParm getRxNoBillDetail(String rxNo){
		String sql = "SELECT A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,A.TAKE_DAYS,E.FREQ_CHN_DESC," +
				" A.MEDI_QTY,C.UNIT_CHN_DESC MEDI_UNIT,A.DOSAGE_QTY,D.UNIT_CHN_DESC DOSAGE_UNIT," +
				" A.OWN_PRICE,A.AR_AMT,A.SEQ_NO,A.SETMAIN_FLG, A.HIDE_FLG,A.ORDERSET_CODE," +
				" A.ORDERSET_GROUP_NO,A.RX_NO,B.ROUTE_CHN_DESC" +
				" FROM OPD_ORDER A,SYS_PHAROUTE B,SYS_UNIT C,SYS_UNIT D,SYS_PHAFREQ E " +
				" WHERE A.ROUTE_CODE = B.ROUTE_CODE(+)" +
				" AND A.MEDI_UNIT = C.UNIT_CODE(+)" +
				" AND A.FREQ_CODE = E.FREQ_CODE(+)" +
				" AND A.DOSAGE_UNIT = D.UNIT_CODE(+)" +
				" AND A.RX_NO='"+rxNo+"'" +
				" AND A.BILL_FLG='Y'" +
				" AND A.RELEASE_FLG='N'" +
				" AND A.RX_TYPE <> '6'" +
				" ORDER BY A.SEQ_NO";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	public TParm getLisReportList(String sDate,String eDate){
		String sql = "SELECT A.CASE_NO,A.APPLICATION_NO,A.ORDER_CODE,A.ORDER_DESC,A.PAT_NAME,C.CATEGORY_CHN_DESC RPTTYPE_DESC,A.RPTTYPE_CODE, " +
				" A.SEX_CODE,D.DEPT_CHN_DESC DEPT_DESC,TO_CHAR(A.PRINT_DATE,'YYYY-MM-DD HH24:MI:SS') PRINT_DATE," +
				" B.USER_NAME DR_DESC, FLOOR(MONTHS_BETWEEN(TO_DATE(CONCAT(EXTRACT(YEAR FROM SYSDATE),'-10-31'),'YYYY-MM-DD'),A.BIRTH_DATE)/12) AGE," +
				" E.ICD_CHN_DESC,A.REPORT_DR,TO_CHAR(A.REPORT_DATE,'YYYY-MM-DD HH24:MI:SS') REPORT_DATE,A.EXAMINE_DR," +
				" TO_CHAR(A.EXAMINE_DATE,'YYYY-MM-DD HH24:MI:SS') EXAMINE_DATE" +
				" FROM MED_APPLY A,SYS_OPERATOR B,SYS_CATEGORY C,SYS_DEPT D,SYS_DIAGNOSIS E" +
				" WHERE A.ADM_TYPE='O' AND A.CAT1_TYPE='LIS' AND A.STATUS='7' " +
				" AND A.RPTTYPE_CODE = C.CATEGORY_CODE(+) AND A.ORDER_DEPT_CODE = D.DEPT_CODE(+)" +
				" AND A.ORDER_DR_CODE = B.USER_ID(+) AND A.ICD_CODE = E.ICD_CODE(+)" +
				" AND A.EXAMINE_DATE BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS') " +
				" AND TO_DATE('"+eDate+"','YYYYMMDDHH24MISS')" +
				" ORDER BY A.CASE_NO,A.APPLICATION_NO";
//		RegQEServiceImpl.writerLog2(sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	public TParm getLisReportDetail(String applicationNo){
		String sql = "SELECT RPDTL_SEQ,TESTITEM_CHN_DESC,TEST_VALUE,LOWER_LIMIT||'-'|| UPPE_LIMIT AS LIMIT,TEST_UNIT " +
				" FROM MED_LIS_RPT WHERE APPLICATION_NO = '"+applicationNo+"' ORDER BY RPDTL_SEQ";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//		RegQEServiceImpl.writerLog2(sql);
		return parm;
	}
	
	public TParm getRisReportList(String sDate,String eDate){
		String sql = "SELECT CASE_NO,APPLICATION_NO,ORDER_DESC,ORDER_CODE FROM MED_APPLY " +
				" WHERE CAT1_TYPE = 'RIS' AND ADM_TYPE = 'O' AND STATUS='7' " +
				" AND EXAMINE_DATE BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS') " +
				" AND TO_DATE('"+eDate+"','YYYYMMDDHH24MISS')" +
				" ORDER BY CASE_NO,APPLICATION_NO";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//		RegQEServiceImpl.writerLog2(sql);
		return parm;
	}
	
	public TParm getRisReportDetail(String applicationNo){
		String sql = "SELECT  A.PAT_NAME,E.CHN_DESC SEX_DESC,D.DEPT_CHN_DESC DEPT_DESC," +
				" C.USER_NAME DR_DESC,A.ORDER_DESC," +
				" FLOOR(MONTHS_BETWEEN(TO_DATE(CONCAT(EXTRACT(YEAR FROM SYSDATE),'-10-31'),'YYYY-MM-DD'),A.BIRTH_DATE)/12) AGE," +
				" CASE WHEN B.OUTCOME_TYPE='H' THEN '阳性' WHEN B.OUTCOME_TYPE='T' THEN '阴性' END OUTCOME_TYPE," +
				" A.MR_NO, A.ADM_TYPE ,TO_CHAR(A.REGISTER_DATE,'YYYY-MM-DD') REGISTER_DATE," +
				" TO_CHAR(A.REGISTER_DATE,'HH24:MI:SS') REGISTER_TIME," +
				" TO_CHAR(A.REPORT_DATE,'YYYY-MM-DD') REPORT_DATE," +
				" TO_CHAR(A.REPORT_DATE,'HH24:MI:SS') REPORT_TIME,A.REPORT_DR," +
				" TO_CHAR(A.EXAMINE_DATE,'YYYY-MM-DD') EXAMINE_DATE," +
				" TO_CHAR(A.EXAMINE_DATE,'HH24:MI:SS') EXAMINE_TIME,A.EXAMINE_DR," +
				" F.ICD_CHN_DESC,B.OUTCOME_CONCLUSION,B.OUTCOME_DESCRIBE,A.OPTITEM_CHN_DESC" +
				" FROM MED_APPLY A,MED_RPTDTL B,SYS_OPERATOR C,SYS_DEPT D,SYS_DICTIONARY E,SYS_DIAGNOSIS F" +
				" WHERE A.APPLICATION_NO = B.APPLICATION_NO AND A.ORDER_DR_CODE = C.USER_ID(+)" +
				" AND A.ORDER_DEPT_CODE = D.DEPT_CODE(+) AND E.GROUP_ID = 'SYS_SEX'" +
				" AND A.SEX_CODE = E.ID(+) AND A.ICD_CODE = F.ICD_CODE(+)" +
				" AND A.APPLICATION_NO = '"+applicationNo+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//		RegQEServiceImpl.writerLog2(sql);
		return parm;
	}
	
	
	public TParm getOpdOrderNotBill(String caseNo,String rxNo){
		String sql = "SELECT B.PAT_NAME PAT_NAME1 ,A.* FROM OPD_ORDER A,SYS_PATINFO B WHERE A.MR_NO= B.MR_NO AND" +
				" A.CASE_NO='"+caseNo+"' AND A.RX_NO IN ("+rxNo+") AND A.BILL_FLG <> 'Y' AND A.RELEASE_FLG='N'" +
				" AND A.RX_TYPE <> '6'" ;
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	public TParm getOpdOrderSumArAmt(String caseNo,String rxNo){
		String sql = "SELECT SUM(AR_AMT) AR_AMT FROM OPD_ORDER WHERE CASE_NO='"+caseNo+"' "
				+ " AND RX_NO IN ("+rxNo+") "
				+ " AND BILL_FLG <> 'Y' "
				+ " AND RELEASE_FLG ='N' "
				+ " AND RX_TYPE <> '6'"
				+ " AND PRINT_FLG='N'";
		
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	public String getBussinessNo(String orderNo){
		String sql = "SELECT TRADE_NO FROM EKT_APP_TRADE WHERE ORDER_NO='"+orderNo+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("TRADE_NO", 0);
	}
	
	public TParm insertOpbReceipt(TParm parm,TConnection conn){
		String sql = "INSERT INTO BIL_OPB_RECP( " +
				" CASE_NO,RECEIPT_NO,ADM_TYPE,REGION_CODE,MR_NO," +
				" RESET_RECEIPT_NO,PRINT_NO,BILL_DATE,CHARGE_DATE," +
				" PRINT_DATE,CHARGE01,CHARGE02,CHARGE03,CHARGE04," +
				" CHARGE05,CHARGE06,CHARGE07,CHARGE08,CHARGE09," +
				" CHARGE10,CHARGE11,CHARGE12,CHARGE13,CHARGE14," +
				" CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19," +
				" CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24," +
				" CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29," +
				" CHARGE30,TOT_AMT,REDUCE_REASON,REDUCE_AMT," +
				" REDUCE_DATE,REDUCE_DEPT_CODE,REDUCE_RESPOND," +
				" AR_AMT,PAY_CASH,PAY_MEDICAL_CARD,PAY_BANK_CARD," +
				" PAY_INS_CARD,PAY_CHECK,PAY_DEBIT,PAY_BILPAY," +
				" PAY_INS,PAY_OTHER1,PAY_OTHER2,PAY_REMARK," +
				" CASHIER_CODE,OPT_USER,OPT_DATE,OPT_TERM,ORDER_NO" +
				" ,INS_TYPE,ACCOUNT_PAY," +
				" RE_SOURCE,ALIPAY,CONFIRM_NO,QE_PAY_TYPE ) " +
				" VALUES(" +
				" '"+parm.getValue("CASE_NO")+"','"+parm.getValue("RECEIPT_NO")+"','"+parm.getValue("ADM_TYPE")+"'," +
				" '"+parm.getValue("REGION_CODE")+"','"+parm.getValue("MR_NO")+"','"+parm.getValue("RESET_RECEIPT_NO")+"'," +
				" '"+parm.getValue("PRINT_NO")+"',SYSDATE,SYSDATE,''," +
				" "+parm.getValue("CHARGE01")+","+parm.getValue("CHARGE02")+","+parm.getValue("CHARGE03")+"," +
				" "+parm.getValue("CHARGE04")+","+parm.getValue("CHARGE05")+"," +
				" "+parm.getValue("CHARGE06")+","+parm.getValue("CHARGE07")+","+parm.getValue("CHARGE08")+"," +
				" "+parm.getValue("CHARGE09")+","+parm.getValue("CHARGE10")+"," +
				" "+parm.getValue("CHARGE11")+","+parm.getValue("CHARGE12")+","+parm.getValue("CHARGE13")+"," +
				" "+parm.getValue("CHARGE14")+","+parm.getValue("CHARGE15")+"," +
				" "+parm.getValue("CHARGE16")+","+parm.getValue("CHARGE17")+","+parm.getValue("CHARGE18")+"," +
				" "+parm.getValue("CHARGE19")+","+parm.getValue("CHARGE20")+"," +
				" "+parm.getValue("CHARGE21")+","+parm.getValue("CHARGE22")+","+parm.getValue("CHARGE23")+"," +
				" "+parm.getValue("CHARGE24")+","+parm.getValue("CHARGE25")+"," +
				" "+parm.getValue("CHARGE26")+","+parm.getValue("CHARGE27")+","+parm.getValue("CHARGE28")+"," +
				" "+parm.getValue("CHARGE29")+","+parm.getValue("CHARGE30")+"," +				
				" "+parm.getValue("TOT_AMT")+",'"+parm.getValue("REDUCE_REASON")+"',"+parm.getValue("REDUCE_AMT")+",'"+parm.getValue("REDUCE_DATE")+"'," +
				" '"+parm.getValue("REDUCE_DEPT_CODE")+"','"+parm.getValue("REDUCE_RESPOND")+"',"+parm.getValue("AR_AMT")+","+parm.getValue("PAY_CASH")+"," +
				" "+parm.getValue("PAY_MEDICAL_CARD")+","+parm.getValue("PAY_BANK_CARD")+","+parm.getValue("PAY_INS_CARD")+"," +
				" "+parm.getValue("PAY_CHECK")+","+parm.getValue("PAY_DEBIT")+","+parm.getValue("PAY_BILPAY")+","+parm.getValue("PAY_INS")+"," +
				" "+parm.getValue("PAY_OTHER1")+","+parm.getValue("PAY_OTHER2")+",'"+parm.getValue("PAY_REMARK")+"','"+parm.getValue("CASHIER_CODE")+"'," +
				" '"+parm.getValue("OPT_USER")+"',SYSDATE,'"+parm.getValue("OPT_TERM")+"','"+parm.getValue("ORDER_NO")+"'," +
				" '"+parm.getValue("INS_TYPE")+"',"+parm.getValue("ACCOUNT_PAY")+
				" ,'"+parm.getValue("RE_SOURCE")+"',"+parm.getValue("ALIPAY")+",'"+parm.getValue("CONFIRM_NO")+"','"+parm.getValue("QE_PAY_TYPE")+"')";
		RegQEServiceImpl.writerLog2("opbRecp==="+sql);
		TParm reParm = new TParm(TJDODBTool.getInstance().update(sql,conn));
		return reParm;
	}
	
	
	public TParm getRegStatus(String caseNo,String admType,boolean flg){
		String sql = "SELECT CASE_NO,BILL_FLG FROM OPD_ORDER WHERE CASE_NO='"+caseNo+"' ";
		if(flg){
			Timestamp date = SystemTool.getInstance().getDate();
//			Timestamp yesterday = StringTool.rollDate(date, -2);
			
			int staDay = selRegSystem(admType)-1;
			
			if(staDay < 0){
				staDay=0;
			}
			
			Timestamp yesterday = StringTool.rollDate(date, -staDay);
			

			String sDate = StringTool.getString(yesterday, "yyyyMMdd")+"000000";
			
			sql += " AND BILL_FLG='N' AND ORDER_DATE > TO_DATE('"+sDate+"','YYYYMMDDHH24MISS') ";
		}
		
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
		
	}
	
	public int selRegSystem(String admType){
		String sql = "SELECT Q_O_EFFECT_DAYS,Q_E_EFFECT_DAYS FROM REG_SYSPARM";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		int day = 0;
		if(admType.equals("O")){
			day = parm.getInt("Q_O_EFFECT_DAYS", 0);
		}
		if(admType.equals("E")){
			day = parm.getInt("Q_E_EFFECT_DAYS", 0);
		}
		return day;
	}
	
	/**
	 * 插入病患信息
	 * @param parm
	 * @return
	 */
	public TParm insertPat(TParm parm,TConnection conn){
		String sql = "INSERT INTO SYS_PATINFO (" +
				"MR_NO,PAT_NAME,IDNO,BIRTH_DATE," +
				"CTZ1_CODE,SEX_CODE,ADDRESS," +
				"OPT_USER,OPT_DATE,OPT_TERM) " +
				"VALUES (" +
				"'"+parm.getValue("MR_NO")+"','"+parm.getValue("PAT_NAME")+"','"+parm.getValue("IDNO")+"'," +
				" TO_DATE('"+parm.getValue("BIRTH_DATE")+"','YYYYMMDD')," +
				"'"+parm.getValue("CTZ1_CODE")+"','"+parm.getValue("SEX_CODE")+"','"+parm.getValue("ADDRESS")+"'," +
				"'"+parm.getValue("OPT_USER")+"',SYSDATE,'"+parm.getValue("OPT_TERM")+"'" +
				")";
		RegQEServiceImpl.writerLog2("insertPat==="+sql);
		TParm re = new TParm(TJDODBTool.getInstance().update(sql,conn));
		return re;
	}
	
	/**
	 * 插入病患信息
	 * @param parm
	 * @return
	 */
	public TParm insertPat(TParm parm){
		String sql = "INSERT INTO SYS_PATINFO (" +
				"MR_NO,PAT_NAME,IDNO,BIRTH_DATE," +
				"CTZ1_CODE,SEX_CODE,ADDRESS," +
				"OPT_USER,OPT_DATE,OPT_TERM) " +
				"VALUES (" +
				"'"+parm.getValue("MR_NO")+"','"+parm.getValue("PAT_NAME")+"','"+parm.getValue("IDNO")+"'," +
				" TO_DATE('"+parm.getValue("BIRTH_DATE")+"','YYYYMMDD')," +
				"'"+parm.getValue("CTZ1_CODE")+"','"+parm.getValue("SEX_CODE")+"','"+parm.getValue("ADDRESS")+"'," +
				"'"+parm.getValue("OPT_USER")+"',SYSDATE,'"+parm.getValue("OPT_TERM")+"'" +
				")";
		RegQEServiceImpl.writerLog2("insertPat==="+sql);
		TParm re = new TParm(TJDODBTool.getInstance().update(sql));
		return re;
	}
	
	public TParm insertEkt(TParm parm,TConnection conn){
		String sql ="INSERT INTO EKT_ISSUELOG"
				+ " (CARD_NO, MR_NO, CARD_SEQ, "
				+ "ISSUE_DATE, ISSUERSN_CODE, "
				+ "FACTORAGE_FEE, PASSWORD, WRITE_FLG, "
				+ "OPT_USER, OPT_DATE, OPT_TERM)"
				+ " VALUES"
				+ " ('"+parm.getValue("CARD_NO")+"', '"+parm.getValue("MR_NO")+"', '"+parm.getValue("CARD_SEQ")+"', "
				+ " SYSDATE, '"+parm.getValue("ISSUERSN_CODE")+"', "
				+ parm.getDouble("FACTORAGE_FEE")+", '"+parm.getValue("PASSWORD")+"', '"+parm.getValue("WRITE_FLG")+"', "
				+ " '"+parm.getValue("OPT_USER")+"',SYSDATE , '"+parm.getValue("OPT_TERM")+"')";
		TParm re = new TParm(TJDODBTool.getInstance().update(sql,conn));
		return re;
	}
	
	public TParm insertEktMaster(TParm parm ,TConnection conn){
		String sql = "INSERT INTO EKT_MASTER"
				+ " (CARD_NO, ID_NO, MR_NO, "
				+ " NAME, CURRENT_BALANCE, "
				+ " CREAT_USER, OPT_USER, OPT_DATE, OPT_TERM)"
				+ " VALUES"
				+ " ('"+parm.getValue("CARD_NO")+"', '"+parm.getValue("ID_NO")+"', '"+parm.getValue("MR_NO")+"', "
				+ " '"+parm.getValue("NAME")+"', "+parm.getDouble("CURRENT_BALANCE")+", "
				+ " '"+parm.getValue("OPT_USER")+"', '"+parm.getValue("OPT_USER")+"', SYSDATE , '"+parm.getValue("OPT_TERM")+"')";
		TParm re = new TParm(TJDODBTool.getInstance().update(sql,conn));
		return re;
	}
	
	public double getEktAmt(String mrNo){
		double amt = 0;
		String sql = "SELECT B.CURRENT_BALANCE  FROM EKT_ISSUELOG A,EKT_MASTER B WHERE A.CARD_NO=B.CARD_NO AND A.WRITE_FLG='Y' AND A.MR_NO='"+mrNo+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()> 0){
			amt = parm.getDouble("CURRENT_BALANCE", 0);
		}
		return amt;
	}
	
	public TParm updateRegCTZ(String caseNo,String ctzCode1){
		String sql = "UPDATE REG_PATADM SET CTZ1_CODE='"+ctzCode1+"' WHERE CASE_NO='"+caseNo+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
		return parm;
	}
	
	public TParm updateRegCTZ2(String caseNo,String ctzCode1){
		String sql = "UPDATE REG_PATADM SET CTZ2_CODE='"+ctzCode1+"' WHERE CASE_NO='"+caseNo+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
		return parm;
	}
	
	public TParm getEktPay(String sDate,String eDate,String cardNo,String mrNo){

		String whereSql = "";
		
		if(cardNo.length() > 0){
			whereSql += " AND A.CARD_NO='"+cardNo+"'";
		}
		if(mrNo.length() > 0){
			whereSql += " AND A.MR_NO='"+mrNo+"'";
		}
		
//		String sql = " SELECT * FROM (SELECT A.CASE_NO, A.ORDER_NO, B.CARD_NO, A.MR_NO," +
//				" A.AR_AMT, A.PAY_MEDICAL_CARD, A.ALIPAY, " +
//				" TO_CHAR( A.BILL_DATE,'YYYY-MM-DD HH24:MI:SS') BILL_DATE,  '1' BUSSTYPE" +
//				" FROM BIL_REG_RECP A, EKT_APP_TRADE B" +
//				" WHERE  A.ORDER_NO IS NOT NULL" +
//				"  AND A.ORDER_NO = B.ORDER_NO(+)" +
//				" AND A.BILL_DATE BETWEEN TO_DATE ('"+sDate+"', 'YYYYMMDDHH24MISS')" +
//				" AND TO_DATE ('"+eDate+"', 'YYYYMMDDHH24MISS')" +whereSql+
//				" UNION ALL" +
//				" SELECT A.CASE_NO, A.ORDER_NO, B.CARD_NO, A.MR_NO, A.AR_AMT," +
//				" A.PAY_MEDICAL_CARD, A.ALIPAY, " +
//				" TO_CHAR( A.BILL_DATE,'YYYY-MM-DD HH24:MI:SS') BILL_DATE, '2' BUSSTYPE" +
//				" FROM BIL_OPB_RECP A, EKT_APP_TRADE B" +
//				" WHERE     A.ORDER_NO IS NOT NULL" +
//				" AND A.ORDER_NO = B.ORDER_NO(+)" +
//				" AND A.BILL_DATE BETWEEN TO_DATE ('"+sDate+"','YYYYMMDDHH24MISS')" +
//				" AND TO_DATE ('"+eDate+"','YYYYMMDDHH24MISS'))" +whereSql+
//				" ORDER BY ORDER_NO";
//		String sql = "SELECT A.TRADE_NO, B.CASE_NO, A.MR_NO, A.CARD_NO, A.ORDER_NO," +
//				" B.STATE, B.BUSINESS_TYPE, A.AMT, A.BUSINESS_APP_TYPE," +
//				" TO_CHAR( A.OPT_DATE,'YYYY-MM-DD HH24:MI:SS') AS OPT_DATE" +
//				" ,C.RECEIPT_NO RECEIPT_NO_R,D.RECEIPT_NO RECEIPT_NO_O " +
//				" FROM EKT_APP_TRADE A, EKT_TRADE B,BIL_REG_RECP C,BIL_OPB_RECP D " +
//				" WHERE A.TRADE_NO = B.TRADE_NO" +
//				" AND B.CASE_NO IS NOT NULL " +
//				" AND A.ORDER_NO = C.ORDER_NO(+)" +
//				" AND A.ORDER_NO = D.ORDER_NO(+)" +
//				" AND A.OPT_DATE BETWEEN TO_DATE ('"+sDate+"','YYYYMMDDHH24MISS')" +
//				" AND TO_DATE ('"+eDate+"','YYYYMMDDHH24MISS')"+whereSql;
		
		String sql = "SELECT A.ORDER_NO,A.CARD_NO, A.MR_NO, A.AMT, (B.OLD_AMT - B.AMT) CURRENT_BALANCE,"
				+ " '1' STATE,B.BUSINESS_TYPE,B.TRADE_NO,"
				+ " TO_CHAR (A.ORDER_TIME, 'YYYY-MM-DD HH24:MI:SS') ORDER_TIME,A.BUSINESS_APP_TYPE "
				+ " FROM EKT_APP_TRADE A, EKT_TRADE B"
				+ " WHERE A.TRADE_NO = B.TRADE_NO "
				+ " AND A.ORDER_TIME BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS')"
				+ " AND TO_DATE('"+eDate+"','YYYYMMDDHH24MISS') "
				+ whereSql 
				+ " UNION ALL "
				+ " SELECT A.ORDER_NO,A.CARD_NO, A.MR_NO,  ABS(C.BUSSINESS_AMT) AMT, C.CURRENT_AMT CURRENT_BALANCE,"
				+ " '2' STATE,B.BUSINESS_TYPE,B.RESET_TRADE_NO TRADE_NO,"
				+ " TO_CHAR (B.OPT_DATE, 'YYYY-MM-DD HH24:MI:SS') ORDER_TIME, A.BUSINESS_APP_TYPE"
				+ " FROM EKT_APP_TRADE A, EKT_TRADE B,EKT_MASTER_HISTORY C"
				+ " WHERE A.BUSINESS_APP_TYPE IN ('1', '2') AND A.TRADE_NO = B.TRADE_NO AND B.STATE='3'"
				+ " AND B.RESET_TRADE_NO=C.HISTORY_NO "
				+ " AND C.CHANGE_DATE BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS') "
				+ " AND TO_DATE('"+eDate+"','YYYYMMDDHH24MISS') "
				+ whereSql
				+ " ORDER BY ORDER_TIME"; 
//		System.out.println(sql);
		RegQEServiceImpl.writerLog2("getEktPay=="+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	public TParm getEktAccntDetail(String cardNo,String mrNo,String sDate,String eDate){
		
		String whereSql = "";
		
		if(cardNo.length() > 0){
			whereSql += " AND A.CARD_NO='"+cardNo+"'";
		}
		if(mrNo.length() > 0){
			whereSql += " AND A.MR_NO='"+mrNo+"'";
		}
		
		String sql = "SELECT A.ORDER_NO,A.CARD_NO, A.MR_NO, A.AMT, (B.OLD_AMT - B.AMT) CURRENT_BALANCE,"
				+ " '1' STATE,B.BUSINESS_TYPE,B.TRADE_NO,TO_CHAR (A.ORDER_TIME, 'YYYY-MM-DD HH24:MI:SS') ORDER_TIME"
				+ " FROM EKT_APP_TRADE A, EKT_TRADE B"
				+ " WHERE A.BUSINESS_APP_TYPE IN ('1', '2') AND A.TRADE_NO = B.TRADE_NO "
				+ " AND A.ORDER_TIME BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS')"
				+ " AND TO_DATE('"+eDate+"','YYYYMMDDHH24MISS') "
				+ whereSql 
				+ " UNION ALL "
				+ " SELECT A.ORDER_NO,A.CARD_NO, A.MR_NO,  ABS(C.BUSSINESS_AMT) AMT, C.CURRENT_AMT CURRENT_BALANCE,"
				+ " '2' STATE,B.BUSINESS_TYPE,B.RESET_TRADE_NO TRADE_NO,TO_CHAR (B.OPT_DATE, 'YYYY-MM-DD HH24:MI:SS') ORDER_TIME"
				+ " FROM EKT_APP_TRADE A, EKT_TRADE B,EKT_MASTER_HISTORY C"
				+ " WHERE A.BUSINESS_APP_TYPE IN ('1', '2') AND A.TRADE_NO = B.TRADE_NO AND B.STATE='3'"
				+ " AND B.RESET_TRADE_NO=C.HISTORY_NO "
				+ " AND C.CHANGE_DATE BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS') "
				+ " AND TO_DATE('"+eDate+"','YYYYMMDDHH24MISS') "
				+ whereSql
				+ " ORDER BY ORDER_TIME"; 
//		System.out.println(sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	public String getMedCardNo(String mrNo){
		String sql = "SELECT INSCARD_NO FROM INS_MZ_CONFIRM WHERE MR_NO='"+mrNo+"' ORDER BY OPT_DATE DESC";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("INSCARD_NO", 0);
	}
	
	public TParm getInsType(String mrNo,String admDate){
		String sql = "SELECT A.CASE_NO,A.CONFIRM_NO,B.INS_CROWD_TYPE,B.INS_PAT_TYPE,A.REALDR_CODE " +
				" FROM REG_PATADM A, INS_OPD B" +
				" WHERE A.ADM_DATE = TO_DATE ('"+admDate+"', 'YYYYMMDD')" +
				" AND A.MR_NO='"+mrNo+"'" +
				" AND A.CONFIRM_NO IS NOT NULL" +
				" AND A.REGCAN_USER IS NULL" +
				" AND A.CONFIRM_NO = B.CONFIRM_NO" +
				" AND B.RECP_TYPE='REG'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
	}
	
	public String getClinicroomDesc(String clinicroomCode){
		String sql = "SELECT  CLINICROOM_DESC FROM REG_CLINICROOM WHERE CLINICROOM_NO = '"+clinicroomCode+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("CLINICROOM_DESC", 0);
	}
	
	
	public String getClinicroom(String clinicroomCode){
		String sql = "SELECT  A.CLINICROOM_DESC,B.CLINIC_DESC FROM REG_CLINICROOM A,REG_CLINICAREA B WHERE A.CLINICROOM_NO ='"+clinicroomCode+"' AND A.CLINICAREA_CODE=B.CLINICAREA_CODE";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("CLINIC_DESC", 0)+" "+parm.getValue("CLINICROOM_DESC", 0);
	}
	
	//--------------医保
	
	
	/**
	 * 医保读卡操作
	 * 
	 * @param parm
	 * @return
	 */
	public TParm readCard(TParm parm) {
		TParm readParm = new TParm();
		TParm regionParm = null;// 获得医保区域代码
		regionParm = SYSRegionTool.getInstance().selectdata("H01");// 获得医保区域代码
		readParm = new TParm(INSTJReg.getInstance().readINSCard(
				parm.getData()));// 读卡动作
		if (null == readParm || readParm.getErrCode() < 0
				|| null == readParm.getValue("CARD_NO")
				|| readParm.getValue("CARD_NO").length() <= 0) {
			return null;
		}
		readParm.setData("PASSWORD", parm.getValue("PASSWORD"));//密码
		String crowdType = readParm.getValue("CROWD_TYPE");//1城职,2城居
		String diseaseCode = parm.getValue("DISEASE_CODE");//0 门诊,4门特
		RegQEServiceImpl.writerLog2("crowdType:"+crowdType);
		RegQEServiceImpl.writerLog2("diseaseCode:"+diseaseCode);
		//门特单病种(默认)
		if (crowdType.equals("2")||diseaseCode.equals("4")) {	
			readParm.setData("DISEASE_CODE",parm.getValue("DISEASE_CODE"));//门特病种(糖尿病)
		}
		int insType = 0;
		TParm testParm = new TParm();
		if (crowdType.equals("1")&&diseaseCode.equals("0")) {
			insType = 1;
			RegQEServiceImpl.writerLog2("城职普通");
			testParm = INSTJFlow.getInstance().insIdentificationChZPt(readParm);
			RegQEServiceImpl.writerLog2("testParm:"+testParm);
			if (testParm.getErrCode() < 0) {
				return testParm;
			}
			String ctzCode=testParm.getValue("CTZ_CODE");
			TParm ctzParm=sysCtzParm(1,ctzCode);
			if (ctzParm.getErrCode()<0) {
				return null;
			}
			readParm.setData("CTZ_CODE",ctzParm.getValue("CTZ_CODE",0));//人员类别
			parm.setData("INS_PAT_TYPE","1");
			// 城职门特
		} else if (crowdType.equals("1")&&!diseaseCode.equals("0")) {
			insType = 2;
			RegQEServiceImpl.writerLog2("城职门特");
			readParm.setData("PAY_KIND", 13);
			// 城职门特刷卡返回参数,得到个人信息
			testParm = INSTJFlow.getInstance().insCreditCardChZMt(readParm);
			RegQEServiceImpl.writerLog2("testParm:"+testParm);
			if (testParm.getErrCode() < 0) {
				return testParm;
			}
			String ctzCode=testParm.getValue("PAT_TYPE");
			TParm ctzParm=sysCtzParm(1,ctzCode);
			if (ctzParm.getErrCode()<0) {
				return null;
			}
			readParm.setData("CTZ_CODE",ctzParm.getValue("CTZ_CODE",0));
			parm.setData("INS_PAT_TYPE","2");
		} else if (crowdType.equals("2")&&!diseaseCode.equals("0")) {
			insType = 3;
			RegQEServiceImpl.writerLog2("城居门特");
			readParm.setData("PAY_KIND", 41);
			// 城居门特刷卡返回参数,得到个人信息
			testParm = INSTJFlow.getInstance().insCreditCardChJMt(readParm);
			RegQEServiceImpl.writerLog2("testParm:"+testParm);
			if (testParm.getErrCode() < 0) {
				return testParm;
			}
			String ctzCode=testParm.getValue("PAT_TYPE");
			TParm ctzParm=sysCtzParm(2,ctzCode);
			if (ctzParm.getErrCode()<0) {
				return null;
			}
			readParm.setData("CTZ_CODE",ctzParm.getValue("CTZ_CODE",0));
			parm.setData("INS_PAT_TYPE","2");
		}
		if (null == testParm || null == testParm.getValue("CONFIRM_NO")) {
			return null;
		}
		testParm.setData("BED_FEE",regionParm.getValue("TOP_BEDFEE",0));//床位费
		testParm.setData("REGION_CODE",regionParm.getValue("NHI_NO", 0));// 医保区域代码
		readParm.setData("opbReadCardParm", testParm.getData());// 资格确认书出参
		readParm.setData("CONFIRM_NO", testParm.getValue("CONFIRM_NO"));// 门特就医顺序号
		readParm.setData("INS_TYPE", insType);
		readParm.setData("RETURN_TYPE", 1);// 返回数据 1.成功 2.失败
		readParm.setData("INS_PAT_TYPE", parm.getValue("INS_PAT_TYPE"));// 1.门诊,2.门特
		if (crowdType.equals("2")||diseaseCode.equals("4")) 	
		readParm.setData("DISEASE_CODE", parm.getValue("DISEASE_CODE"));//门特病种(糖尿病)
		else
		readParm.setData("DISEASE_CODE", "");	
		readParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0));// 医保区域代码
		return readParm;
	}
	private TParm sysCtzParm(int type, String ctzCode) {
		String ctzSql = "SELECT CTZ_CODE FROM SYS_CTZ WHERE NHI_NO='" + ctzCode
				+ "' AND NHI_CTZ_FLG='Y'";
		if (type == 1) {
			ctzSql += " AND INS_CROWD_TYPE = '1'";
		}
		if (type == 2) {
			ctzSql += " AND INS_CROWD_TYPE = '2'";
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
		//获得当前时间
		String sysdate =StringTool.getString(SystemTool.
				getInstance().getDate(),"yyyyMMddHHmmss");
		String clinictypeCode = (String) parm.getData("CLINICTYPE_CODE");
		String regFeesql = "SELECT A.ORDER_CODE,B.ORDER_DESC,B.NHI_CODE_O, "
				+ "B.NHI_CODE_E, B.NHI_CODE_I,B.OWN_PRICE ,"
				+ "B.OWN_PRICE AS AR_AMT ,'1' AS DOSAGE_QTY, "
				+ "'0' AS TAKE_DAYS, '' AS NS_NOTE, '' AS SPECIFICATION,'' AS DR_CODE,A.RECEIPT_TYPE,"
				+ "C.DOSE_CODE FROM REG_CLINICTYPE_FEE A,SYS_FEE_HISTORY B,PHA_BASE C WHERE A.ORDER_CODE=B.ORDER_CODE(+) "
				+ "AND A.ORDER_CODE=C.ORDER_CODE(+) AND  A.ADM_TYPE='O'"
				+ " AND A.CLINICTYPE_CODE='" + clinictypeCode + "'" 
				+ " AND '" + sysdate+ "' BETWEEN B.START_DATE AND B.END_DATE";
		if(parm.getValue("MT_CLINIC_FEE_CODE").trim().length() > 0){ //modify by huangtt 20170504
			regFeesql += " UNION ALL "
					+ " SELECT B.ORDER_CODE,B.ORDER_DESC,B.NHI_CODE_O, "
					+ " B.NHI_CODE_E, B.NHI_CODE_I,B.OWN_PRICE ,"
					+ " B.OWN_PRICE AS AR_AMT ,'1' AS DOSAGE_QTY, "
					+ " '0' AS TAKE_DAYS, '' AS NS_NOTE, '' AS SPECIFICATION,'' AS DR_CODE,'' AS RECEIPT_TYPE,"
					+ " C.DOSE_CODE FROM SYS_FEE_HISTORY B,PHA_BASE C"
					+ " WHERE B.ORDER_CODE = '" + parm.getValue("MT_CLINIC_FEE_CODE") + "'"
					+ " AND B.ORDER_CODE=C.ORDER_CODE(+)	"
					+ " AND '" + sysdate+ "' BETWEEN B.START_DATE AND B.END_DATE";
		}
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
			if(parm.getValue("MT_CLINIC_FEE_CODE").trim().length() > 0){ //modify by huangtt 20170504
				if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("")) {
					regFeeParm.setData("RECEIPT_TYPE", i, parm.getDouble("MT_CLINIC_FEE"));
					regFeeParm.setData("AR_AMT", i, parm.getDouble("MT_CLINIC_FEE"));
				}
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
		returnParm.setData("PRINT_NO", "111111"); // 票号(默认)
//		returnParm.setData("QUE_NO", parm.getData("QUE_NO"));
		returnParm.setData("FeeY", reg_fee + clinic_fee+ parm.getDouble("MT_CLINIC_FEE")); //modify by huangtt 20170504
		String date = StringTool.getString(SystemTool.
				getInstance().getDate(), "yyyyMMdd");
		returnParm.setData("ADM_DATE", date);
		return returnParm;
	}	
	/**
	 * 医保卡执行费用显示操作 flg 是否执行退挂 false： 执行退挂 true： 正流程操作
	 * 
	 * @param flg
	 *            boolean
	 * @return TParm
	 */
	public TParm insExeFee(boolean flg, TParm insParm, TParm parm) {
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
		insParm.setData("PRINT_NO", "111111"); // 票据号
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
		// 个人实际帐户支付
		double accountPay = 0.00;

		if (insType == 1) {
			sOTOT_Amt = returnParm.getDouble("TOT_AMT", 0) - // 总金额
					returnParm.getDouble("UNACCOUNT_PAY_AMT", 0) - // 非账户支付
					returnParm.getDouble("UNREIM_AMT", 0);// 基金未报销
			sUnaccount_pay_amt = returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					+ returnParm.getDouble("UNREIM_AMT", 0);// 现金支付金额
			accountPay = returnParm.getDouble("ACCOUNT_PAY_AMT", 0);
		}
		// 城职门特
		if (insType == 2) {
			sOTOT_Amt = returnParm.getDouble("TOT_AMT", 0)
					- returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					- returnParm.getDouble("UNREIM_AMT", 0);
			sUnaccount_pay_amt = returnParm.getDouble("UNACCOUNT_PAY_AMT", 0)
					+ returnParm.getDouble("UNREIM_AMT", 0);
			accountPay = returnParm.getDouble("ACCOUNT_PAY_AMT", 0);
			
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

			// 个人实际支付
			sUnaccount_pay_amt = returnParm.getDouble("TOT_AMT", 0)
					- returnParm.getDouble("TOTAL_AGENT_AMT", 0)
					- returnParm.getDouble("FLG_AGENT_AMT", 0)
					- returnParm.getDouble("ARMY_AI_AMT", 0)
					- returnParm.getDouble("ILLNESS_SUBSIDY_AMT", 0)//城乡大病金额
					+ returnParm.getDouble("UNREIM_AMT", 0);
		}
		TParm parm = new TParm();
		parm.setData("OTOT_AMT", sOTOT_Amt);//医保支付
		parm.setData("ACCOUNT_AMT", accountPay);//个人账户
		parm.setData("UACCOUNT_AMT", sUnaccount_pay_amt);//个人实际支付
		return parm;
	}
	/**
	 * 医保执行退费操作
	 * 
	 */
	public TParm reSetExeFee(TParm parm) {
		TParm result = INSTJFlow.getInstance().selectResetFee(parm);
		if (result.getErrCode() < 0) {
			return null;
		}
		return result;

	}
	/**
	 *  获取医保数据
	 */
	public TParm getInsOpd(TParm parm) {
		String caseNo =parm.getValue("CASE_NO");
		String confirmNo =parm.getValue("CONFIRM_NO");
		String sql = " SELECT B.TOT_AMT,A.INS_PAY_AMT,A.UNREIM_AMT,A.OINSTOT_AMT,"+
		             " A.OWN_AMT,A.UNACCOUNT_PAY_AMT,A.ACCOUNT_PAY_AMT," +
		             " A.TOT_AMT AS INS_TOT_AMT,A.ILLNESS_SUBSIDY_AMT," +
		             " B.CONFIRM_NO,B.CTZ_CODE AS PAT_TYPE,B.DISEASE_CODE,B.PAY_KIND," +
		             " A.REIM_TYPE,A.TOTAL_AGENT_AMT,A.ARMY_AI_AMT,A.FLG_AGENT_AMT," +
		             " A.INS_CROWD_TYPE,A.INS_PAT_TYPE"+
                     " FROM INS_OPD A,ins_mz_confirm B"+   
                     "  WHERE A.CASE_NO='" + caseNo+ "'"+
                     " AND A.CONFIRM_NO='" + confirmNo+ "'"+ 
                     " AND A.CONFIRM_NO=B.CONFIRM_NO "+
                     " AND A.CASE_NO=B.CASE_NO "+
                     " AND INSAMT_FLG='1'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	/**
	 *  更新医保交易表INS_OPD
	 */
	public TParm updateInsOpd(String receiptNo,String confirmNo,String caseNo) {		
		TParm result = new TParm(); 
		String sql = " UPDATE INS_OPD SET RECEIPT_NO ='" + receiptNo+ "' " +
				     " WHERE CASE_NO ='" + caseNo + "'" +
		             " AND CONFIRM_NO='" + confirmNo + "'";
       // 更新医保交易表INS_OPD
		result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() < 0) {
        	return result;
        }		
		return result;
	}
	/**
	 *  获取医保数据
	 */
	public TParm getInsData(String caseNo,String confirmNo) {
		String sql = " SELECT CASE_NO,INS_CROWD_TYPE,INS_PAT_TYPE FROM INS_OPD"+
			         " WHERE CONFIRM_NO='" + confirmNo+ "'" +
			         " AND CASE_NO ='" + caseNo+ "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	/**
	 *  获取费用发生时间			
	 */
	public TParm getAdmDate(String caseNo) {
		String sql = " SELECT ADM_DATE FROM REG_PATADM"+
                     " WHERE CASE_NO ='" + caseNo+ "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/**
	 *  获取医保缴费医嘱数据	
	 */
	public TParm getInsOrder(String caseNo,String rxNo) {
		String sql = " SELECT A.CASE_NO,A.RX_NO,A.SEQ_NO,A.OPT_USER,A.OPT_DATE,"+
			" A.OPT_TERM,A.PRESRT_NO,A.REGION_CODE,A.MR_NO,A.ADM_TYPE,"+
			" A.RX_TYPE,A.TEMPORARY_FLG,A.RELEASE_FLG,A.LINKMAIN_FLG,A.LINK_NO,"+
			" A.ORDER_CODE,A.ORDER_DESC ||CASE"+
			" WHEN TRIM(A.SPECIFICATION) IS NOT NULL OR TRIM(A.SPECIFICATION) <>''"+
			" THEN '(' || A.SPECIFICATION || ')' ELSE ''"+
			" END AS ORDER_DESC,A.SPECIFICATION,A.GOODS_DESC,A.ORDER_CAT1_CODE,A.MEDI_QTY,"+
			" A.MEDI_UNIT,A.FREQ_CODE,A.ROUTE_CODE,A.TAKE_DAYS,A.DOSAGE_QTY,A.DOSAGE_UNIT,"+
			" A.DISPENSE_QTY,A.DISPENSE_UNIT,A.GIVEBOX_FLG,A.OWN_PRICE,A.NHI_PRICE,"+
			" A.DISCOUNT_RATE,A.OWN_AMT,A.AR_AMT,A.DR_NOTE,A.NS_NOTE,"+
			" A.DR_CODE,A.ORDER_DATE,A.DEPT_CODE,A.DC_DR_CODE,A.DC_ORDER_DATE,"+
			" A.DC_DEPT_CODE,A.EXEC_DEPT_CODE,A.SETMAIN_FLG,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,"+
			" A.HIDE_FLG,A.RPTTYPE_CODE,A.OPTITEM_CODE,A.DEV_CODE,A.MR_CODE,"+
			" A.FILE_NO,A.DEGREE_CODE,A.URGENT_FLG,A.INSPAY_TYPE,A.PHA_TYPE,"+
			" A.DOSE_TYPE,A.PRINTTYPEFLG_INFANT,A.EXPENSIVE_FLG," +
			" A.CTRLDRUGCLASS_CODE,A.PRESCRIPT_NO,"+
			" A.ATC_FLG,A.SENDATC_DATE,A.RECEIPT_NO,A.BILL_FLG,A.BILL_DATE,"+
			" A.BILL_USER,A.PRINT_FLG,A.REXP_CODE,A.HEXP_CODE,A.CONTRACT_CODE,"+
			" A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.PHA_CHECK_CODE,A.PHA_CHECK_DATE,"+
			" A.PHA_DOSAGE_CODE,A.PHA_DOSAGE_DATE,A.PHA_DISPENSE_CODE," +
			" A.PHA_DISPENSE_DATE,A.NS_EXEC_CODE,"+
			" A.NS_EXEC_DATE,A.NS_EXEC_DEPT,A.DCTAGENT_CODE,A.DCTEXCEP_CODE,A.DCT_TAKE_QTY,"+
			" A.PACKAGE_TOT,A.AGENCY_ORG_CODE,A.DCTAGENT_FLG," +
			" A.DECOCT_CODE,A.EXEC_FLG,A.RECEIPT_FLG,A.CAT1_TYPE,"+
			" A.MED_APPLY_NO,A.BILL_TYPE,A.PHA_RETN_CODE,A.BUSINESS_NO," +
			" B.NHI_CODE_O,B.NHI_CODE_E,B.NHI_CODE_I"+ 
			" FROM OPD_ORDER A,SYS_FEE_HISTORY B"+ 
			" WHERE A.CASE_NO = '" + caseNo+ "'"+ 
			" AND A.RX_NO IN (" + rxNo+ ")"+ 
			" AND A.ORDER_CODE=B.ORDER_CODE "+
			" AND A.BILL_FLG='N'"+ 
			" AND A.RELEASE_FLG='N'" +
			" AND A.RX_TYPE <> '6'" +
			" AND A.PRINT_FLG='N'"+
			" AND A.ORDER_DATE BETWEEN TO_DATE(B.START_DATE,'YYYYMMDDHH24MISS')"+ 
			" AND  TO_DATE(B.END_DATE,'YYYYMMDDHH24MISS')"+	
			" ORDER BY A.RX_TYPE,A.RX_NO,A.SEQ_NO";
		RegQEServiceImpl.writerLog2("getInsOrder=========="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	/**
	 *  获取医保数据
	 */
	public TParm queryInsmzconfirm(String caseNo) {
		String sql = " SELECT DISEASE_CODE,INS_CROWD_TYPE,INS_PAT_TYPE FROM INS_MZ_CONFIRM"+
			         " WHERE  CASE_NO ='" + caseNo+ "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	/***
	 * 执行医保前操作(缴费)
	 * 
	 * @param parm
	 * @return
	 */
	public TParm saveCardBeforeOPB(TParm tparm, TParm returnParm,Pat pat,Reg reg) {
		TParm parm = new TParm();
		DecimalFormat df = new DecimalFormat("##########0.00");
		int count = tparm.getCount();
		RegQEServiceImpl.writerLog2("tparm=========="+tparm);
		RegQEServiceImpl.writerLog2("count=========="+count);
		double billAmt = 0.00;// 未收费医嘱金额
		TParm result = null;
		for (int i = 0; i < count; i++) {
			RegQEServiceImpl.writerLog2("saveCardBeforeOPB==========");
			if (tparm.getData("ORDERSET_CODE", i) != null
					&& tparm.getData("ORDERSET_CODE", i).equals(
							tparm.getData("ORDER_CODE", i))) {
				continue;
			}
			if (null != tparm.getData("BILL_FLG", i)
					&& "N".equals(tparm.getData("BILL_FLG", i))) {// 未收费医嘱
				billAmt += tparm.getDouble("AR_AMT", i);
			}
			RegQEServiceImpl.writerLog2("saveCardBeforeOPB==========www"+billAmt);
			parm.addData("RX_NO", tparm.getData("RX_NO", i));
			parm.addData("ORDER_CODE", tparm.getData("ORDER_CODE", i));
			parm.addData("SEQ_NO", tparm.getData("SEQ_NO", i));
			parm.addData("AMT", tparm.getData("AMT", i));
			parm.addData("AR_AMT", tparm.getData("AR_AMT", i));
			parm.addData("EXEC_FLG", tparm.getData("EXEC_FLG", i));
			parm.addData("RECEIPT_FLG", tparm.getData("RECEIPT_FLG", i));
			parm.addData("OWN_PRICE", tparm.getData("OWN_PRICE", i));
			parm.addData("QTY", tparm.getData("QTY", i));
			parm.addData("DOSAGE_QTY", tparm.getData("DOSAGE_QTY", i));
			parm.addData("SPECIFICATION", tparm.getData("SPECIFICATION", i));
			parm.addData("TAKE_DAYS", tparm.getData("TAKE_DAYS", i));
			parm.addData("DR_NOTE", tparm.getData("DR_NOTE", i));
			parm.addData("ORDERSET_CODE", tparm.getData("ORDERSET_CODE", i));
			parm.addData("HIDE_FLG", tparm.getData("HIDE_FLG", i));
			parm.addData("NHI_CODE_O", tparm.getData("NHI_CODE_O", i));
			parm.addData("NHI_CODE_E", tparm.getData("NHI_CODE_E", i));
			parm.addData("NHI_CODE_I", tparm.getData("NHI_CODE_I", i));
			parm.addData("ORDER_DESC", (String) tparm.getData("ORDER_DESC", i)
					+ (String) tparm.getData("SPECIFICATION", i));
			parm.addData("BILL_FLG", tparm.getData("BILL_FLG", i));
			TParm insparm = new TParm();
			insparm.setData("BILL_D", SystemTool.getInstance().getDate());
			insparm.setData("INS_CODE", tparm.getData("NHI_CODE_O", i));
			result = INSIbsTool.getInstance().queryInsIbsOrderByInsRule(insparm);
			if (result.getErrCode() < 0) {
				return result;
			}
			parm.addData("YF", result.getValue("YF", 0));// 用法
			parm.addData("ZFBL1", result.getValue("ZFBL1", 0));// 自负比例
			parm.addData("PZWH", result.getValue("PZWH", 0));// 批准文号
         }
		
		    parm.setData("billAmt", df.format(billAmt) );// 未收费医嘱金额
		    RegQEServiceImpl.writerLog2("saveCardBeforeOPB==========sss"+parm); 
		    //整理分割数据
		    returnParm.setData("ID_NO", pat.getIdNo());
		    returnParm.setData("NAME", pat.getName());
		    returnParm.setData("REG_PARM", parm.getData()); // 所有要分割的医嘱
		    returnParm.setData("CONFIRM_NO", returnParm.getValue("CONFIRM_NO")); // 医保就诊号			
		    returnParm.setData("INS_TYPE", returnParm.getValue("INS_TYPE"));
		    returnParm.setData("MR_NO", pat.getMrNo()); // 病患号
		    returnParm.setData("PAY_KIND", "11"); // 4 支付类别:11门诊、药店21住院//支付类别12
		    returnParm.setData("CASE_NO", reg.caseNo()); // 就诊号
		    returnParm.setData("RECP_TYPE", "OPB"); // 就诊类别			
		    returnParm.setData("OPT_USER", (String) tparm.getData("OPT_USER",0)); //操作人员		    
		    returnParm.setData("REG_TYPE", "0"); // 挂号标志:1 挂号0 非挂号
		    returnParm.setData("OPT_TERM", (String) tparm.getData("OPT_TERM",0));//操作IP  
		    if (reg.getAdmType().equals("E")) {
		    	returnParm.setData("EREG_FLG", "1"); // 急诊
			} else {
				returnParm.setData("EREG_FLG", "0"); // 普通
			}
		    returnParm.setData("FeeY", parm.getDouble("billAmt")); // 未收费医嘱金额
		    returnParm.setData("ADM_TYPE", "O");
		    returnParm.setData("PRINT_NO", "111111");// 票号暂时写死
		    String sql = "SELECT MR_NO,NHI_NO,REALDR_CODE FROM REG_PATADM WHERE CASE_NO='"
				+ reg.caseNo() + "' AND ARRIVE_FLG='Y'";
		    TParm Doctor = new TParm(TJDODBTool.getInstance().select(sql));
		    String drCode = (String) Doctor.getData("REALDR_CODE", 0);	
		    returnParm.setData("DR_CODE",drCode);
		    String admdate = StringTool.getString(reg.getAdmDate(), "yyyyMMdd");
		    returnParm.setData("ADM_DATE", admdate);
		    RegQEServiceImpl.writerLog2("saveCardBeforeOPB==========sssssss"+returnParm);
		return returnParm;
	}
	
	/**
	 * 刷卡动作 刷卡 执行函数：DataDown_sp, 
	 * 方法 U 调用险种识别交易 函数DataDown_czys,方法 A
	 * @return
	 */
	public TParm readCardPat(TParm parm) {
		TParm readParm = INSTJTool.getInstance().DataDown_sp_U(
				parm.getValue("TEXT"));// U方法，取卡号
		TParm readINSParm = new TParm();
		parm.setData("CARD_NO", readParm.getValue("CARD_NO"));// 医保卡号
		parm.setData("TYPE", 1);// 传入类型:1社保卡卡号2 身份证号码 传固定值 1
		readINSParm = INSTJTool.getInstance().DataDown_czys_A(parm);// A方法，人群类别信息
		return readINSParm;
	}
	
	public TParm getRegRecp(String sDate,String eDate,String mrNo,TParm result){
		String sql = "SELECT C.IDNO,A.MR_NO,C.PAT_NAME,B.RE_SOURCE,D.INS_CROWD_TYPE," +
				" D.INS_PAT_TYPE,E.INSCARD_NO,A.CASE_NO," +
				" TO_CHAR(B.BILL_DATE,'YYYY-MM-DD HH24:MI:SS') BILL_DATE," +
				" B.RECEIPT_NO,B.AR_AMT,B.PAY_INS_CARD,B.PAY_MEDICAL_CARD,B.ALIPAY " +
				" ,D.TOT_AMT AS INS_TOT_AMT, D.UNACCOUNT_PAY_AMT,D.UNREIM_AMT,D.ACCOUNT_PAY_AMT," +
				" D.TOTAL_AGENT_AMT,D.ARMY_AI_AMT,D.FLG_AGENT_AMT,D.ILLNESS_SUBSIDY_AMT," +
				" D.OINSTOT_AMT,D.INS_PAY_AMT,D.REIM_TYPE" +
				" FROM REG_PATADM A,BIL_REG_RECP B,SYS_PATINFO C,INS_OPD D,INS_MZ_CONFIRM E" +
				" WHERE A.CASE_NO = B.CASE_NO" +
				" AND A.REGCAN_USER IS NULL" +
				" AND A.MR_NO = C.MR_NO" +
				" AND A.CASE_NO = D.CASE_NO(+)" +
				" AND B.RESET_RECEIPT_NO IS NULL" +
				" AND A.CASE_NO = E.CASE_NO(+)" +
				" AND A.CONFIRM_NO = D.CONFIRM_NO(+)" +
				" AND A.CONFIRM_NO = E.CONFIRM_NO(+)" +
				" AND B.BILL_DATE BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS') " +
				" AND  TO_DATE('"+eDate+"','YYYYMMDDHH24MISS')" ;
		
		if(mrNo.length() > 0){
			sql += " AND A.MR_NO='"+mrNo+"'";
		}	
		
		sql += " ORDER BY B.BILL_DATE";
		RegQEServiceImpl.writerLog2("getRegRecp---"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < parm.getCount(); i++) {
			result.addData("IDNO", parm.getValue("IDNO", i));
			result.addData("MR_NO", parm.getValue("MR_NO", i));
			result.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
			
			if(parm.getValue("RE_SOURCE", i).length() == 0){
				result.addData("RE_SOURCE", "window");
			}else{
				result.addData("RE_SOURCE", parm.getValue("RE_SOURCE", i));
			}
			
			//判断医保类型(城职 普通、城职门特 、城居门特)
			String insType = "0";
			String inscrowdType =parm.getValue("INS_CROWD_TYPE", i);
			String inspayType =parm.getValue("INS_PAT_TYPE", i);
			if(inscrowdType.equals("1")&&inspayType.equals("1"))
				insType = "1";
			if(inscrowdType.equals("1")&&inspayType.equals("2"))
				insType = "2";
			if(inscrowdType.equals("2")&&inspayType.equals("2"))
				insType = "3";
			
			double insurepay = 0;
			double cashpay = parm.getDouble("AR_AMT", i);
			double accountPay = 0;
			
			if(insType.equals("1")||insType.equals("2")){
				insurepay = parm.getDouble("INS_TOT_AMT", i) - // 总金额
				parm.getDouble("UNACCOUNT_PAY_AMT", i) - // 非账户支付
				parm.getDouble("UNREIM_AMT", i);// 基金未报销
				cashpay =parm.getDouble("UNACCOUNT_PAY_AMT", i)+         
				parm.getDouble("UNREIM_AMT", i);// 现金支付金额
		        accountPay = parm.getDouble("ACCOUNT_PAY_AMT", i);	
			}
	        if(insType.equals("3")){
	        	if (null != parm.getValue("REIM_TYPE", i)
						&& parm.getInt("REIM_TYPE", i) == 1) {
	        		insurepay = parm.getDouble("TOTAL_AGENT_AMT", i)
							+ parm.getDouble("ARMY_AI_AMT", i)
							+ parm.getDouble("FLG_AGENT_AMT", i)
							+ parm.getDouble("ILLNESS_SUBSIDY_AMT", i)//城乡大病金额
							- parm.getDouble("UNREIM_AMT", i);
				} else {
					insurepay = parm.getDouble("TOTAL_AGENT_AMT", i)
							+ parm.getDouble("FLG_AGENT_AMT", i)
							+ parm.getDouble("ARMY_AI_AMT", i)
					        + parm.getDouble("ILLNESS_SUBSIDY_AMT", i);//城乡大病金额
				}
				// 个人实际支付
	        	cashpay = parm.getDouble("INS_TOT_AMT", i)
						- parm.getDouble("TOTAL_AGENT_AMT", i)
						- parm.getDouble("FLG_AGENT_AMT", i)
						- parm.getDouble("ARMY_AI_AMT", i)
						- parm.getDouble("ILLNESS_SUBSIDY_AMT", i)//城乡大病金额
						+ parm.getDouble("UNREIM_AMT", i);	
			}	
	        insurepay = insurepay -accountPay;
			
			
	        
			if(insType.equals("3")){
				result.addData("INS_TYPE", "2");
			}else{
				result.addData("INS_TYPE", insType);	
			}
			
			result.addData("INSCARD_NO", parm.getValue("INSCARD_NO", i));
			result.addData("REG_CODE", parm.getValue("CASE_NO", i));
			result.addData("RECEIPT_CODE", "");
			result.addData("BILL_DATE", parm.getValue("BILL_DATE", i));
			result.addData("AR_AMT", parm.getValue("AR_AMT", i));
			result.addData("INS_PAY", insurepay);
			result.addData("ACCOUNT_PAY", accountPay);
			result.addData("CASH_PAY", cashpay);
			result.addData("RECEIPT_NO", parm.getValue("RECEIPT_NO", i));
			result.addData("COMMENT", "");
			
			
			
		}
//		RegQEServiceImpl.writerLog2(result);
		
		return result;
		
	}
	
	public TParm getOpbRecp(String sDate,String eDate,String mrNo,TParm result){
		String sql = "SELECT B.IDNO,A.MR_NO,B.PAT_NAME,A.RE_SOURCE,A.INS_TYPE,C.INSCARD_NO,A.RECEIPT_NO," +
				" TO_CHAR(A.BILL_DATE,'YYYY-MM-DD HH24:MI:SS') BILL_DATE,A.AR_AMT,A.ACCOUNT_PAY,A.PAY_INS_CARD" +
				" FROM BIL_OPB_RECP A,SYS_PATINFO B,INS_MZ_CONFIRM C" +
				" WHERE A.MR_NO = B.MR_NO" +
				" AND A.RESET_RECEIPT_NO IS NULL" +
				" AND A.AR_AMT > 0" +
				" AND A.CASHIER_CODE = 'QeApp'" +
				" AND A.CASE_NO = C.CASE_NO(+)" +
				" AND A.CONFIRM_NO = C.CONFIRM_NO(+)" +
				" AND A.BILL_DATE BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS') " +
				" AND  TO_DATE('"+sDate+"','YYYYMMDDHH24MISS')" ;
				
		
		if(mrNo.length() > 0){
			sql += " AND A.MR_NO='"+mrNo+"'";
		}	
		sql += " ORDER BY A.BILL_DATE";
		RegQEServiceImpl.writerLog2("getOpbRecp-QeApp--"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < parm.getCount(); i++) {
			result.addData("IDNO", parm.getValue("IDNO", i));
			result.addData("MR_NO", parm.getValue("MR_NO", i));
			result.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
			result.addData("RE_SOURCE", parm.getValue("RE_SOURCE", i));
			if(parm.getValue("INS_TYPE", i).equals("自费")){
				result.addData("INS_TYPE", "0");
			}
			if(parm.getValue("INS_TYPE", i).equals("医保")){
				result.addData("INS_TYPE", "1");
			}
			if(parm.getValue("INS_TYPE", i).equals("门特")){
				result.addData("INS_TYPE", "2");
			}
			
			result.addData("INSCARD_NO", parm.getValue("INSCARD_NO", i));
			result.addData("REG_CODE", "");
			result.addData("RECEIPT_CODE", parm.getValue("RECEIPT_NO", i));
			result.addData("BILL_DATE", parm.getValue("BILL_DATE", i));
			result.addData("AR_AMT", parm.getDouble("AR_AMT", i));
			double insurepay = parm.getDouble("PAY_INS_CARD", i)-parm.getDouble("ACCOUNT_PAY", i);
			double accountPay = parm.getDouble("ACCOUNT_PAY", i);
			double cashpay = parm.getDouble("AR_AMT", i)- parm.getDouble("PAY_INS_CARD", i);
			result.addData("INS_PAY", insurepay);
			result.addData("ACCOUNT_PAY", accountPay);
			result.addData("CASH_PAY", cashpay);
			result.addData("RECEIPT_NO", parm.getValue("RECEIPT_NO", i));
			result.addData("COMMENT", "");
			
		}
		
		String sql1 = "SELECT  B.IDNO,A.MR_NO,B.PAT_NAME,A.CASE_NO,D.CONFIRM_NO," +
				" TO_CHAR(A.BILL_DATE,'YYYY-MM-DD HH24:MI:SS') BILL_DATE," +
				" A.RECEIPT_NO,A.AR_AMT,D.INS_CROWD_TYPE,D.INS_PAT_TYPE,D.TOT_AMT AS INS_TOT_AMT," +
				" D.UNACCOUNT_PAY_AMT,D.UNREIM_AMT,D.ACCOUNT_PAY_AMT," +
				" D.TOTAL_AGENT_AMT,D.ARMY_AI_AMT,D.FLG_AGENT_AMT,D.ILLNESS_SUBSIDY_AMT," +
				" D.OINSTOT_AMT,D.INS_PAY_AMT,D.REIM_TYPE" +
				" FROM BIL_OPB_RECP A,SYS_PATINFO B,INS_OPD D" +
				" WHERE A.MR_NO = B.MR_NO" +
				" AND A.RESET_RECEIPT_NO IS NULL" +
				" AND A.AR_AMT > 0" +
				" AND A.CASHIER_CODE <> 'QeApp'" +
				" AND A.CASE_NO = D.CASE_NO(+)" +
				" AND A.PRINT_NO = D.INV_NO(+)" +
				" AND A.BILL_DATE BETWEEN TO_DATE('"+sDate+"','YYYYMMDDHH24MISS')" +
				" AND  TO_DATE('"+eDate+"','YYYYMMDDHH24MISS')" ;
		if(mrNo.length() > 0){
			sql1 += " AND A.MR_NO='"+mrNo+"'";
		}	
		sql1 += " ORDER BY A.BILL_DATE";
		RegQEServiceImpl.writerLog2("getOpbRecp-----"+sql);
		parm = new TParm(TJDODBTool.getInstance().select(sql1));
		for (int i = 0; i < parm.getCount(); i++) {
			result.addData("IDNO", parm.getValue("IDNO", i));
			result.addData("MR_NO", parm.getValue("MR_NO", i));
			result.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
			result.addData("RE_SOURCE", "window");
			//判断医保类型(城职 普通、城职门特 、城居门特)
			String insType = "0";
			String inscrowdType =parm.getValue("INS_CROWD_TYPE", i);
			String inspayType =parm.getValue("INS_PAT_TYPE", i);
			if(inscrowdType.equals("1")&&inspayType.equals("1"))
				insType = "1";
			if(inscrowdType.equals("1")&&inspayType.equals("2"))
				insType = "2";
			if(inscrowdType.equals("2")&&inspayType.equals("2"))
				insType = "3";
			
			double insurepay = 0;
			double cashpay = parm.getDouble("AR_AMT", i);
			double accountPay = 0;
			
			if(insType.equals("1")||insType.equals("2")){
				insurepay = parm.getDouble("INS_TOT_AMT", i) - // 总金额
				parm.getDouble("UNACCOUNT_PAY_AMT", i) - // 非账户支付
				parm.getDouble("UNREIM_AMT", i);// 基金未报销
				cashpay =parm.getDouble("UNACCOUNT_PAY_AMT", i)+         
				parm.getDouble("UNREIM_AMT", i);// 现金支付金额
		        accountPay = parm.getDouble("ACCOUNT_PAY_AMT", i);	
			}
	        if(insType.equals("3")){
	        	if (null != parm.getValue("REIM_TYPE", i)
						&& parm.getInt("REIM_TYPE", i) == 1) {
	        		insurepay = parm.getDouble("TOTAL_AGENT_AMT", i)
							+ parm.getDouble("ARMY_AI_AMT", i)
							+ parm.getDouble("FLG_AGENT_AMT", i)
							+ parm.getDouble("ILLNESS_SUBSIDY_AMT", i)//城乡大病金额
							- parm.getDouble("UNREIM_AMT", i);
				} else {
					insurepay = parm.getDouble("TOTAL_AGENT_AMT", i)
							+ parm.getDouble("FLG_AGENT_AMT", i)
							+ parm.getDouble("ARMY_AI_AMT", i)
					        + parm.getDouble("ILLNESS_SUBSIDY_AMT", i);//城乡大病金额
				}
				// 个人实际支付
	        	cashpay = parm.getDouble("INS_TOT_AMT", i)
						- parm.getDouble("TOTAL_AGENT_AMT", i)
						- parm.getDouble("FLG_AGENT_AMT", i)
						- parm.getDouble("ARMY_AI_AMT", i)
						- parm.getDouble("ILLNESS_SUBSIDY_AMT", i)//城乡大病金额
						+ parm.getDouble("UNREIM_AMT", i);	
			}	
	        insurepay = insurepay -accountPay;
			
			
	        
			if(insType.equals("3")){
				result.addData("INS_TYPE", "2");
			}else{
				result.addData("INS_TYPE", insType);	
			}
			if(parm.getValue("CONFIRM_NO", i).length() > 0){
				result.addData("INSCARD_NO", getInsCardNo(parm.getValue("CASE_NO", i),parm.getValue("CONFIRM_NO", i)));
			}else{
				result.addData("INSCARD_NO", "");
			}
			
			result.addData("REG_CODE", "");
			result.addData("RECEIPT_CODE", parm.getValue("RECEIPT_NO", i));
			result.addData("BILL_DATE", parm.getValue("BILL_DATE", i));
			result.addData("AR_AMT", parm.getValue("AR_AMT", i));
			result.addData("INS_PAY", insurepay);
			result.addData("ACCOUNT_PAY", accountPay);
			result.addData("CASH_PAY", cashpay);
			result.addData("RECEIPT_NO", parm.getValue("RECEIPT_NO", i));
			result.addData("COMMENT", "");
			

		}
//		RegQEServiceImpl.writerLog2(result);
		return result;

	}
	
	
	public String getInsCardNo(String caseNo,String confirmNo ){
		String sql = "SELECT  INSCARD_NO FROM INS_MZ_CONFIRM WHERE CASE_NO='"+caseNo+"' AND CONFIRM_NO='"+confirmNo+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("INSCARD_NO", 0);
	}

	public TParm insertEktMasterHistory(TParm tempEktParm,TConnection conn){
	
		String sql="INSERT INTO EKT_MASTER_HISTORY " +
		" (MR_NO,SEQ_NO,CHANGE_DATE,LATEST_AMT,BUSSINESS_AMT," +
		" CURRENT_AMT,OPT_TYPE,CARD_NO,OPT_USER,OPT_DATE," +
		" OPT_TERM,HISTORY_NO,CASE_NO,EKT_BUSINESS_NO,MZCONFIRM_NO) " +
		" VALUES " +
		" ('"+tempEktParm.getValue("MR_NO")+"','"+tempEktParm.getValue("SEQ")+"',SYSDATE, "+tempEktParm.getDouble("OLD_AMT")+", "+tempEktParm.getDouble("AMT")+" ," +
		"  "+tempEktParm.getDouble("EKT_AMT")+",'"+tempEktParm.getValue("BUSINESS_TYPE")+"','"+tempEktParm.getValue("CARD_NO")+"','QeApp',SYSDATE," +
		"  '"+tempEktParm.getValue("OPT_TERM")+"','"+tempEktParm.getValue("HISTORY_NO")+"','"+tempEktParm.getValue("CASE_NO")+"','"+tempEktParm.getValue("EKT_BUSINESS_NO")+"','"+tempEktParm.getValue("CONFIRM_NO")+"')";
		RegQEServiceImpl.writerLog2("insertEktMasterHistorySql==="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
		return result;
		
	}
	
	public TParm updateEktMasterHistoryCaseNo(String treadNo,String caseNo,String confirmNo,TConnection conn){
		String sql = "UPDATE EKT_MASTER_HISTORY SET CASE_NO='"+caseNo+"', MZCONFIRM_NO='"+confirmNo+"'" +
				" WHERE CASE_NO='T-"+treadNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
		return result;
	}
	
	public TParm selEktMasterHistory(String treadNo){
		String sql = "SELECT MR_NO,SEQ_NO,LATEST_AMT FROM  EKT_MASTER_HISTORY " +
				" WHERE CASE_NO='T-"+treadNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getCount() > 0){
			return result.getRow(0);
		}
		return new TParm();
	}
	
	public String getektMasterHistoryNo(String tradeNo){
		String sql = "SELECT HISTORY_NO FROM EKT_MASTER_HISTORY WHERE CASE_NO='T-"+tradeNo+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("TRADE_NO", 0);
	}
	
	public String getHistoryNo(){
		String historyNo = SystemTool.getInstance().getNo("ALL", "EKT", "HISTORY_NO","HISTORY_NO");
		if (null==historyNo||historyNo.length()<=0) {
			historyNo = SystemTool.getInstance().getNo("ALL", "EKT", "HISTORY_NO","HISTORY_NO");// 得到医疗卡外部交易号
		}
		return historyNo;
	}
	
	public int getHistorySeq(String mrNo){
		TParm seqParm = new TParm(
				TJDODBTool.getInstance().select(
						"SELECT MAX(SEQ_NO) SEQ FROM EKT_MASTER_HISTORY WHERE MR_NO='"+mrNo+"'"));
		
		int seq = 0;
		
		if(seqParm.getInt("SEQ",0) > 0){
			seq = seqParm.getInt("SEQ",0)+ 1;
		}else{
			seq = 1;
		}
		
		return seq;
		
		
	}
	
	public String getCtz2Code(String clinictypeCode,TParm patInfo ){
		String ctz2Code = "";		
//		String clinictypeCode = parmSchDay.getValue("CLINICTYPE_CODE", 0);
		String ageS = OdoUtil.showAge(patInfo.getTimestamp("BIRTH_DATE", 0),
				SystemTool.getInstance().getDate());

		if(ageS.length() > 0){
			String age1 = "0";
			 if ("en".equalsIgnoreCase(Operator.getLanguage())) {
				 age1=ageS.substring(0, ageS.lastIndexOf("Y")); 
			 }else{
				 age1=ageS.substring(0, ageS.lastIndexOf("岁")); 
			 }
			
			int age = Integer.parseInt(age1);
			if(age >= 60 && "03".equals(clinictypeCode)){
				ctz2Code="31";
			}
		}
		return ctz2Code;
	}
	
	public String getClinictypeDesc(String code){
		String sql = "SELECT CLINICTYPE_DESC FROM REG_CLINICTYPE WHERE CLINICTYPE_CODE='"+code+"'";
		String re="";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() > 0){
			re = parm.getValue("CLINICTYPE_DESC", 0);
		}
		return re;
	}
	
	public String getDrDesc(String code){
		String sql = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='"+code+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String re = "";
		if(parm.getCount() > 0){
			re = parm.getValue("USER_NAME", 0);
		}
		return re;
	}
	
	public String getDeptDesc(String code){
		String sql = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+code+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String re = "";
		if(parm.getCount() > 0){
			re = parm.getValue("DEPT_CHN_DESC", 0);
		}
		return re;
	}
	
	public TParm bilDailyFee(String mrNo, String sDate, String eDate){	
        String sql2 =
        		"SELECT H.MR_NO, "
                        +"H.PAT_NAME, "
                        +"H.SEX_CODE, "
                        +"I.CTZ_DESC, "
                        +"J.DEPT_CHN_DESC ADM_DEPT_DESC, "
                        +"K.STATION_DESC ADM_STATION_DESC, "
                        +"A.REXP_CODE, "
                        +"A.HEXP_CODE, "
                        +"A.ORDER_CODE, "
                        +"B.ORDER_DESC, "
                        +"B.SPECIFICATION, "
                        +"A.DOSAGE_UNIT, "
                        +"A.OWN_PRICE, "
                        +"SUM (A.DOSAGE_QTY) AS DOSAGE_QTY, "
                        +"SUM (A.TOT_AMT) AS TOT_AMT, "
                        +"C.UNIT_CHN_DESC, "
                        +"D.CHARGE_HOSP_DESC, "
                        +"E.CHN_DESC, "
                        +"TO_CHAR (A.BILL_DATE, 'YYYY/MM/DD') AS BILL_DATE, "
                        +"A.EXE_DEPT_CODE, "
                        +"F.COST_CENTER_CHN_DESC AS DEPT_CHN_DESC, "
                        +"B.ORDER_CAT1_CODE, "
                        +"B.NHI_CODE_I, "
                        +"G.CASE_NO, "
                        +"G.BED_NO, "
                        +"G.IPD_NO, "
                        +"MAX(A.EXEC_DATE) AS EXEC_DATE,L.ZFBL1 "
                   +" FROM IBS_ORDD A, "
                        +"SYS_FEE B, "
                        +"SYS_UNIT C, "
                        +"SYS_CHARGE_HOSP D, "
                        +"SYS_DICTIONARY E, "
                        +"SYS_COST_CENTER F, "
                        +"ADM_INP G, "
                        +"SYS_PATINFO H, "
                        +"SYS_CTZ I, "
                        +"SYS_DEPT J, "
                        +"SYS_STATION K,INS_RULE L "
                  +"WHERE A.CASE_NO = G.CASE_NO "
                        +"AND G.MR_NO = H.MR_NO "
                        +"AND G.MR_NO = '"+mrNo+"' "
                        +"AND A.BILL_DATE BETWEEN TO_DATE ('"+sDate+"000000', 'YYYYMMDDHH24MISS')AND TO_DATE ('"+eDate+"235959', 'YYYYMMDDHH24MISS') "
                        +"AND A.OWN_PRICE <> 0 "
                        +"AND A.ORDER_CODE = B.ORDER_CODE "
                        +"AND A.DOSAGE_UNIT = C.UNIT_CODE(+) "
                        +" AND B.NHI_CODE_I = L.SFXMBM(+) " 
                        +"AND A.HEXP_CODE = D.CHARGE_HOSP_CODE "
                        +"AND E.GROUP_ID = 'SYS_CHARGE' "
                        +"AND A.REXP_CODE = E.ID "
                        +"AND A.EXE_DEPT_CODE = F.COST_CENTER_CODE(+) "
                        +"AND G.CANCEL_FLG <> 'Y' "
                        +"AND G.DS_DATE IS NULL "
                        +"AND G.CTZ1_CODE = I.CTZ_CODE "
                        +"AND G.DEPT_CODE = J.DEPT_CODE "
                        +"AND G.STATION_CODE = K.STATION_CODE "
              +"GROUP BY H.MR_NO, "
                       +"H.PAT_NAME, "
                       +"I.CTZ_DESC, "
                       +"J.DEPT_CHN_DESC, "
                       +"K.STATION_DESC, "
                       +"H.SEX_CODE, "
                       +"A.REXP_CODE, "
                       +"A.HEXP_CODE, "
                       +"A.ORDER_CODE, "
                       +"B.ORDER_DESC, "
                       +"A.DOSAGE_UNIT, "
                       +"A.OWN_PRICE, "
                       +"A.EXE_DEPT_CODE, "
                       +"G.CASE_NO, "
                       +"TO_CHAR (A.BILL_DATE, 'YYYY/MM/DD'), "
                       +"C.UNIT_CHN_DESC, "
                       +"D.CHARGE_HOSP_DESC, "
                       +"E.CHN_DESC, "
                       +"B.SPECIFICATION, "
                       +"F.COST_CENTER_CHN_DESC, "
                       +"B.ORDER_CAT1_CODE, "
                       +"B.NHI_CODE_I, "
                       +"G.BED_NO, "
                       +"G.IPD_NO,L.ZFBL1 "
             +"ORDER BY TO_CHAR (A.BILL_DATE, 'YYYY/MM/DD'), A.HEXP_CODE, A.ORDER_CODE ";
       TParm result = new TParm(TJDODBTool.getInstance().select(sql2));
       System.out.println(sql2);
		return result;
	
  }
	
	/**
	 * STATUS  //0失败 1成功 2执行中
	 * @param parm
	 * @return
	 */
	public TParm insertEktAppStatus(TParm parm){

		String sql="INSERT INTO EKT_APP_STATUS(" +
				"ORDER_NO, STATUS, OPT_USER," +
				"  OPT_DATE,OPT_TERM,TYPE)" +
				" VALUES" +
				"('"+parm.getValue("ORDER_NO")+"', '"+parm.getValue("STATUS")+"',"+
				" '"+parm.getValue("OPT_USER")+"', SYSDATE,'"+parm.getValue("OPT_TERM")+"','"+parm.getValue("TYPE")+"')";
//		RegQEServiceImpl.writerLog2(sql);
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		return result;
	}
	
	public TParm updateEktAppStatus(String orderNo,String status){
		String sql = "UPDATE EKT_APP_STATUS SET STATUS='"+status+"',OPT_DATE=SYSDATE "				
				+ " WHERE ORDER_NO='"+orderNo+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		return result;
	}
	
	public TParm updateEktAppStatusParm(TParm parm){
		String sql = "UPDATE EKT_APP_STATUS SET STATUS='"+parm.getValue("STATUS")+"',OPT_DATE=SYSDATE,"
				+ " QUEUN_NO='"+parm.getValue("QUEUN_NO")+"', "
				+ " VISITADDRESS='"+parm.getValue("VISITADDRESS")+"', "
				+ " CASE_NO='"+parm.getValue("CASE_NO")+"', "
				+ " RECEIPT_NO='"+parm.getValue("RECEIPT_NO")+"', "
				+ " PAYMENTTYPE='"+parm.getValue("PAYMENTTYPE")+"', "
				+ " CARD_NO='"+parm.getValue("CARD_NO")+"', "
				+ " ACCOUNTPAY="+parm.getDouble("ACCOUNTPAY")+", " 
				+ " CASHPAY="+parm.getDouble("CASHPAY")+", "
				+ " INSUREPAY="+parm.getDouble("INSUREPAY")
				+ " WHERE ORDER_NO='"+parm.getValue("ORDER_NO")+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		return result;
	}
	
	public TParm getEktAppStatus(String orderNo,String type){
		String sql = "SELECT * FROM  EKT_APP_STATUS WHERE ORDER_NO='"+orderNo+"'";
		if(type.length() > 0){
			sql += " AND TYPE='"+type+"'";
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/**
	 *  获取医保数据
	 */
	public TParm getInsBackData(String caseNo,String confirmNo) {
		String sql = " SELECT CASE_NO,INS_CROWD_TYPE,INS_PAT_TYPE FROM INS_OPD"+
			         " WHERE CONFIRM_NO='" + confirmNo+ "'" +
			         " AND CASE_NO ='" + caseNo+ "'" +
			         " AND INSAMT_FLG = '3'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}	
}
