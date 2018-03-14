package jdo.lis;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.dongyang.Service.Server;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class NISJdo extends TJDOTool {
	/**
	 * 实例
	 */
	public static NISJdo instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return TestJDO
	 */
	public static NISJdo getInstance() {
		if (instanceObject == null)
			instanceObject = new NISJdo();
		return instanceObject;
	}

	/**
	 * NIS体温信息录入SUM_VITALSIGN体温单表
	 * 
	 * @return
	 */
	public TParm getInsertNewVITALSIGN(TParm parm) {
		TParm result = new TParm();
		String sql = "INSERT INTO SUM_VITALSIGN(CASE_NO,EXAMINE_DATE,ADM_TYPE,IPD_NO,MR_NO,"
				+ "INHOSPITALDAYS,OPE_DAYS2,ECTTIMES,MCFLG,HOURSOFSLEEP,"
				+ "MINUTESOFSLEEP,STOOL,SPECIALSTOOLNOTE,INTAKEFLUIDQTY,INTAKEDIETQTY,OUTPUTURINEQTY,"
				+ "OUTPUTDRAINQTY,OUTPUTOTHERQTY,BATH,GUESTKIND,STAYOUTSIDE,LEAVE,LEAVEREASONCODE,"
				+ "HEIGHT,WEIGHT,NOTE,STATUS_CODE,DISPOSAL_FLG,DISPOSAL_REASON,"
				+ "USER_ID,USER_DEFINE_1,USER_DEFINE_1_VALUE,USER_DEFINE_2,USER_DEFINE_2_VALUE,USER_DEFINE_3,"
				+ "USER_DEFINE_3_VALUE,OPT_USER,OPT_DATE,OPT_TERM,AUTO_STOOL,ENEMA,DRAINAGE,OPE_DAYS)"
				+ "VALUES('"
				+ parm.getValue("CASE_NO")
				+ "','"
				+ parm.getValue("EXAMINE_DATE")
				+ "','"
				+ parm.getValue("ADM_TYPE")
				+ "','"
				+ parm.getValue("IPD_NO")
				+ "','"
				+ parm.getValue("MR_NO")
				+ "','"
				+ parm.getValue("INHOSPITALDAYS")
				+ "','"
				+ parm.getValue("OPE_DAYS2")
				+ "','"
				+ parm.getValue("ECTTIMES")
				+ "','"
				+ parm.getValue("MCFLG")
				+ "','"
				+ parm.getValue("HOURSOFSLEEP")
				+ "','"
				+ parm.getValue("MINUTESOFSLEEP")
				+ "','"
				+ parm.getValue("STOOL")
				+ "','"
				+ parm.getValue("SPECIALSTOOLNOTE")
				+ "','"
				+ parm.getValue("INTAKEFLUIDQTY")
				+ "','"
				+ parm.getValue("INTAKEDIETQTY")
				+ "','"
				+ parm.getValue("OUTPUTURINEQTY")
				+ "','"
				+ parm.getValue("OUTPUTDRAINQTY")
				+ "','"
				+ parm.getValue("OUTPUTOTHERQTY")
				+ "','"
				+ parm.getValue("BATH")
				+ "','"
				+ parm.getValue("GUESTKIND")
				+ "','"
				+ parm.getValue("STAYOUTSIDE")
				+ "','"
				+ parm.getValue("LEAVE")
				+ "','"
				+ parm.getValue("LEAVEREASONCODE")
				+ "','"
				+ parm.getValue("HEIGHT")
				+ "','"
				+ parm.getValue("WEIGHT")
				+ "','"
				+ parm.getValue("NOTE")
				+ "','"
				+ parm.getValue("STATUS_CODE")
				+ "','"
				+ parm.getValue("DISPOSAL_FLG")
				+ "','"
				+ parm.getValue("DISPOSAL_REASON")
				+ "','"
				+ "NIS"
				+ "','"
				+ parm.getValue("USER_DEFINE_1")
				+ "','"
				+ parm.getValue("USER_DEFINE_1_VALUE")
				+ "','"
				+ parm.getValue("USER_DEFINE_2")
				+ "','"
				+ parm.getValue("USER_DEFINE_2_VALUE")
				+ "','"
				+ parm.getValue("USER_DEFINE_3")
				+ "','"
				+ parm.getValue("USER_DEFINE_3_VALUE")
				+ "','"
				+ "NIS"
				+ "',SYSDATE,'"
				+ "NIS"
				+ "','"
				+ parm.getValue("AUTO_STOOL")
				+ "','"
				+ parm.getValue("ENEMA")
				+ "','"
				+ parm.getValue("DRAINAGE")
				+ "','"
				+ parm.getValue("OPE_DAYS")
				+ "')";
		Server.autoInit(this);
		System.out.println("getInsertNewVITALSIGN=========" + sql);
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			result.setErr(-1, "录入失败");
			return result;
		}
		return result;
	}

	/**
	 * NIS体温信息修改SUM_VITALSIGN体温单表
	 * 
	 * @return
	 */
	public TParm getUpdateNewVITALSIGN(TParm parm) {
		TParm result = new TParm();
		String sql = "UPDATE SUM_VITALSIGN SET CASE_NO = '"
				+ parm.getValue("CASE_NO") + "', EXAMINE_DATE = '"
				+ parm.getValue("EXAMINE_DATE") + "', ADM_TYPE ='"
				+ parm.getValue("ADM_TYPE") + "', IPD_NO ='"
				+ parm.getValue("IPD_NO") + "', MR_NO ='"
				+ parm.getValue("MR_NO") + "' , INHOSPITALDAYS ='"
				+ parm.getValue("INHOSPITALDAYS") + "', OPE_DAYS2 ='"
				+ parm.getValue("OPE_DAYS2") + "', ECTTIMES ='"
				+ parm.getValue("ECTTIMES") + "' , MCFLG ='"
				+ parm.getValue("MCFLG") + "', HOURSOFSLEEP ='"
				+ parm.getValue("HOURSOFSLEEP") + "' , MINUTESOFSLEEP ='"
				+ parm.getValue("MINUTESOFSLEEP") + "' , STOOL ='"
				+ parm.getValue("STOOL") + "', SPECIALSTOOLNOTE ='"
				+ parm.getValue("SPECIALSTOOLNOTE") + "',INTAKEFLUIDQTY='"
				+ parm.getValue("INTAKEFLUIDQTY") + "',INTAKEDIETQTY = '"
				+ parm.getValue("INTAKEDIETQTY") + "',OUTPUTURINEQTY ='"
				+ parm.getValue("OUTPUTURINEQTY") + "',OUTPUTDRAINQTY='"
				+ parm.getValue("OUTPUTDRAINQTY") + "',OUTPUTOTHERQTY='"
				+ parm.getValue("OUTPUTOTHERQTY") + "',BATH='"
				+ parm.getValue("BATH") + "',GUESTKIND='"
				+ parm.getValue("GUESTKIND") + "',STAYOUTSIDE='"
				+ parm.getValue("STAYOUTSIDE") + "',LEAVE='"
				+ parm.getValue("LEAVE") + "',LEAVEREASONCODE ='"
				+ parm.getValue("LEAVEREASONCODE") + "',HEIGHT='"
				+ parm.getValue("HEIGHT") + "',WEIGHT='"
				+ parm.getValue("WEIGHT") + "',NOTE='" + parm.getValue("NOTE")
				+ "',STATUS_CODE='" + parm.getValue("STATUS_CODE")
				+ "',DISPOSAL_FLG='" + parm.getValue("DISPOSAL_FLG")
				+ "',DISPOSAL_REASON='" + parm.getValue("DISPOSAL_REASON")
				+ "',USER_ID='" + "NIS" + "',USER_DEFINE_1='"
				+ parm.getValue("USER_DEFINE_1") + "',USER_DEFINE_1_VALUE='"
				+ parm.getValue("USER_DEFINE_1_VALUE") + "',USER_DEFINE_2='"
				+ parm.getValue("USER_DEFINE_2") + "',USER_DEFINE_2_VALUE='"
				+ parm.getValue("USER_DEFINE_2_VALUE") + "',USER_DEFINE_3='"
				+ parm.getValue("USER_DEFINE_3") + "',USER_DEFINE_3_VALUE='"
				+ parm.getValue("USER_DEFINE_3_VALUE") + "',OPT_USER='" + "NIS"
				+ "',OPT_DATE=SYSDATE,OPT_TERM='" + "NIS" + "'"
				+ ",AUTO_STOOL='" + parm.getValue("AUTO_STOOL") + "',ENEMA='"
				+ parm.getValue("ENEMA") + "',DRAINAGE='"
				+ parm.getValue("DRAINAGE") + "',OPE_DAYS='"
				+ parm.getValue("OPE_DAYS") + "' WHERE CASE_NO = '" + parm.getValue("CASE_NO") + "'AND EXAMINE_DATE = '"
				+ parm.getValue("EXAMINE_DATE") + "'AND ADM_TYPE ='"
				+ parm.getValue("ADM_TYPE") + "'";
		Server.autoInit(this);
		System.out.println("getUpdateNewVITALSIGN=========" + sql);
		result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			result.setErr(-1, "更新失败");
			return result;
		}
		return result;
	}

	/**
	 * NIS体温信息录入SUM_VTSNTPRDTL体温记录表
	 * 
	 * @return
	 */
	public TParm getInsertNewVTSNTPRDTL(TParm parm) {
		TParm result = new TParm();
		String sql = "INSERT INTO SUM_VTSNTPRDTL(CASE_NO,EXAMINE_DATE,ADM_TYPE,EXAMINESESSION,"
				+ "PHYSIATRICS,RECTIME,SPCCONDCODE,TMPTRKINDCODE,TEMPERATURE,PLUSE,RESPIRE,SYSTOLICPRESSURE,"
				+ "DIASTOLICPRESSURE,NOTPRREASONCODE,PTMOVECATECODE,PTMOVECATEDESC,USER_ID,OPT_USER,OPT_DATE,"
				+ "OPT_TERM,HEART_RATE,SPECIALRESPIRENOTE)VALUES('"
				+ parm.getValue("CASE_NO")
				+ "','"
				+ parm.getValue("EXAMINE_DATE")
				+ "','"
				+ parm.getValue("ADM_TYPE")
				+ "','"
				+ parm.getValue("EXAMINESESSION")
				+ "','"
				+ parm.getValue("PHYSIATRICS")
				+ "','"
				+ parm.getValue("RECTIME")
				+ "','"
				+ parm.getValue("SPCCONDCODE")
				+ "','"
				+ parm.getValue("TMPTRKINDCODE")
				+ "','"
				+ parm.getValue("TEMPERATURE")
				+ "','"
				+ parm.getValue("PLUSE")
				+ "','"
				+ parm.getValue("RESPIRE")
				+ "','"
				+ parm.getValue("SYSTOLICPRESSURE")
				+ "','"
				+ parm.getValue("DIASTOLICPRESSURE")
				+ "','"
				+ parm.getValue("NOTPRREASONCODE")
				+ "','"
				+ parm.getValue("PTMOVECATECODE")
				+ "','"
				+ parm.getValue("PTMOVECATEDESC")
				+ "','"
				+ "NIS"
				+ "','"
				+ "NIS"
				+ "',SYSDATE,'"
				+ "NIS"
				+ "','"
				+ parm.getValue("HEART_RATE")
				+ "','"
				+ parm.getValue("SPECIALRESPIRENOTE") + "')";
		Server.autoInit(this);
		result = new TParm(TJDODBTool.getInstance().update(sql));
		System.out.println("SUM_VTSNTPRDTL=========" + sql);
		if (result.getErrCode() < 0) {
			result.setErr(-1, "更新失败");
			return result;
		}
		return result;
	}

	/**
	 * NIS体温信息修改SUM_VTSNTPRDTL体温记录表
	 * 
	 * @return
	 */
	public TParm getUpdateNewVTSNTPRDTL(TParm parm) {
		TParm result = new TParm();
		String sql = "UPDATE SUM_VTSNTPRDTL SET CASE_NO = '"
				+ parm.getValue("CASE_NO") + "', EXAMINE_DATE = '"
				+ parm.getValue("EXAMINE_DATE") + "', ADM_TYPE ='"
				+ parm.getValue("ADM_TYPE") + "', EXAMINESESSION ='"
				+ parm.getValue("EXAMINESESSION") + "', PHYSIATRICS ='"
				+ parm.getValue("PHYSIATRICS") + "' , RECTIME ='"
				+ parm.getValue("RECTIME") + "', SPCCONDCODE ='"
				+ parm.getValue("SPCCONDCODE") + "', TMPTRKINDCODE ='"
				+ parm.getValue("TMPTRKINDCODE") + "' , TEMPERATURE ='"
				+ parm.getValue("TEMPERATURE") + "', PLUSE ='"
				+ parm.getValue("PLUSE") + "' , RESPIRE ='"
				+ parm.getValue("RESPIRE") + "' , SYSTOLICPRESSURE ='"
				+ parm.getValue("SYSTOLICPRESSURE") + "', DIASTOLICPRESSURE ='"
				+ parm.getValue("DIASTOLICPRESSURE") + "',NOTPRREASONCODE='"
				+ parm.getValue("NOTPRREASONCODE") + "',PTMOVECATECODE = '"
				+ parm.getValue("PTMOVECATECODE") + "',PTMOVECATEDESC ='"
				+ parm.getValue("PTMOVECATEDESC") + "',USER_ID='" + "NIS" + "',OPT_USER='" + "NIS"
				+ "',OPT_DATE=SYSDATE,OPT_TERM='" + "NIS" + "'"
				+ ",HEART_RATE='" + parm.getValue("HEART_RATE") + "',SPECIALRESPIRENOTE='"
				+ parm.getValue("SPECIALRESPIRENOTE") + "' WHERE CASE_NO = '" + parm.getValue("CASE_NO") + "'AND EXAMINE_DATE = '"
				+ parm.getValue("EXAMINE_DATE") + "'AND ADM_TYPE ='"
				+ parm.getValue("ADM_TYPE") + "'AND EXAMINESESSION ='"
				+ parm.getValue("EXAMINESESSION") + "'";
		Server.autoInit(this);
		result = new TParm(TJDODBTool.getInstance().update(sql));
		System.out.println("SUM_VTSNTPRDTL=========" + sql);
		if (result.getErrCode() < 0) {
			result.setErr(-1, "更新失败");
			return result;
		}
		return result;
	}
}
