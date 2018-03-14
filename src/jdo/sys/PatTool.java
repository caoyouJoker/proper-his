package jdo.sys;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.util.TDebug;
import com.dongyang.util.StringTool;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;



/**
 * 
 * <p>
 * Title:病患工具类
 * </p>
 * 
 * <p>
 * Description:病患工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author lzk 2008.09.19 
 * @version 1.0
 */
public class PatTool extends TJDOTool {
	/**
	 * 实例
	 */
	private static PatTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PatTool
	 */
	public static PatTool getInstance() {
		if (instanceObject == null)
			instanceObject = new PatTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public PatTool() {
		setModuleName("sys\\SYSPatInfoModule.x");
		onInit();
	}

	/**
	 * 根据病案号查询姓名
	 * 
	 * @param mrno
	 *            String 病案号
	 * @return String 姓名
	 */
	public String getNameForMrno(String mrno) {
		if (mrno == null || mrno.length() == 0)
			return "";
		TParm parm = new TParm();
		parm.setData("MR_NO", checkMrno(mrno));
		return getResultString(query("getNameForMrno", parm), "PAT_NAME");
	}

	/**
	 * 根据病案号查询身份证号
	 * 
	 * @param mrno
	 *            String 病案号
	 * @return String 身份证号
	 */
	public String getIdnoForMrno(String mrno) {
		if (mrno == null || mrno.length() == 0)
			return "";
		TParm parm = new TParm();
		parm.setData("MR_NO", checkMrno(mrno));
		return getResultString(query("getIdnoForMrno", parm), "IDNO");
	}

	/**
	 * 根据病案号查询基本信息
	 * 
	 * @param mrno
	 *            String 病案号
	 * @return TParm <PAT_NAME>,<IDNO>
	 */
	public TParm getInfoForMrno(String mrno) {
		if (mrno == null || mrno.length() == 0)
			return new TParm();
		// 处理合并病案号
//		mrno = getMergeMrno(mrno);
		TParm parm = new TParm();
		parm.setData("MR_NO", checkMrno(mrno));
		TParm result = query("getInfoForMrno", parm);
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	/**
	 *根据身份证号查询基本信息
	 * @param idno
	 * @return
	 */
	public TParm getInfoForNEWIdno(String idno) { 
		if (idno == null || idno.length() == 0)
			return new TParm();
		// 处理合并病案号
//		idno = getMergeMrno(idno);
		TParm parm = new TParm();
		parm.setData("IDNO", idno);
		TParm result = query("getInfoForNEWIdno", parm);
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	
	/**
	 * 检测病案号长度
	 * 
	 * @param mrno
	 *            String
	 * @return String
	 */
	public String checkMrno(String mrno) {
		int mrnoLength = getMrNoLength();
		if (mrnoLength <= 0)
			mrnoLength = mrno.length();
		return StringTool.fill("0", mrnoLength - mrno.length()) + mrno;
	}

	/**
	 * 得到病案号的长度
	 * 
	 * @return int
	 */
	public int getMrNoLength() {
		return getResultInt(query("getMrNoLength"), "MRNO_LENGTH");
	}

	/**
	 * 检核住院号长度
	 * 
	 * @param ipdno
	 *            String
	 * @return String
	 */
	public String checkIpdno(String ipdno) {
		int ipdnoLength = getMrNoLength();
		if (ipdnoLength <= 0)
			ipdnoLength = ipdno.length();
		return StringTool.fill("0", ipdnoLength - ipdno.length()) + ipdno;
	}

	/**
	 * 得到住院号长度
	 * 
	 * @return int
	 */
	public int getIpdNoLength() {
		return getResultInt(query("getIpdNoLength"), "IPDNO_LENGTH");
	}

	/**
	 * 新增病患
	 * 
	 * @param pat
	 *            Pat 病患对象
	 * @return boolean true 成功 false 失败
	 */
	public boolean newPat(Pat pat) {
		if (pat == null)
			return false;
		TParm parm = pat.getParm();
		String newMrNo = SystemTool.getInstance().getMrNo();
		if (newMrNo == null || newMrNo.length() == 0) {
			err("-1 取病案号错误!");
			return false;
		}
		parm.setData("MR_NO", newMrNo);
		TParm result = update("insertInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		pat.setMrNo(newMrNo);
		return true;
	}

	/**
	 * 保存病患信息
	 * 
	 * @param pat
	 *            Pat 病患对象
	 * @return boolean true 成功 false 失败
	 */
	public boolean onSave(Pat pat) {
		if (pat == null)
			return false;
		TParm parm = pat.getParm();
		if (!onSave(parm)) {
			return false;
		}
		return true;
	}

	/**
	 * 保存病患信息
	 * 
	 * @param parm
	 *            TParm 病患对象
	 * @return boolean true 成功 false 失败
	 */
	public boolean onSave(TParm parm) {
		if (parm == null)
			return false;
		TParm result = update("updateInfo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * 更新病患信息
	 * 
	 * @param parm
	 *            TParm 参数
	 * @param connection
	 *            TConnection 连接
	 * @return TParm 结果
	 */
	public TParm updatePat(TParm parm, TConnection connection) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "参数错误");
			return result;
		}
		// 执行保存
		result = update("updateInfo", parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}

		return result;
	}

	/**
	 * 保存病患信息(带链接,后台使用)
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return boolean
	 */
	public boolean onSave(TParm parm, TConnection connection) {
		if (parm == null)
			return false;
		TParm result = update("updateInfo", parm, connection);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * 删除病患信息
	 * 
	 * @param pat
	 *            Pat 病患对象
	 * @return boolean true 成功 false 失败
	 */
	public boolean onDelete(Pat pat) {
		if (pat == null)
			return false;
		TParm parm = new TParm();
		if (pat.getMrNo() == null || pat.getMrNo().trim().length() == 0)
			return false;
		parm.setData("MR_NO", pat.getMrNo());
		TParm result = update("deletePat", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * 删除病患信息(物理删除)
	 * 
	 * @param pat
	 *            Pat 病患对象
	 * @return boolean true 成功 false 失败
	 */
	public boolean onDelete$(Pat pat) {
		if (pat == null)
			return false;
		TParm parm = new TParm();
		if (pat.getMrNo() == null || pat.getMrNo().trim().length() == 0)
			return false;
		parm.setData("MR_NO", pat.getMrNo());
		TParm result = update("deletePat$", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * 得到合并的病案号
	 * 
	 * @param mrNo
	 *            String
	 * @return String
	 */
	public String getMergeMrno(String mrNo) {
		if (mrNo == null || mrNo.trim().length() == 0)
			return "";
		mrNo = checkMrno(mrNo);
		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		String newMrNo = getResultString(query("getMergeMrno", parm),
				"MERGE_TOMRNO");
		if (newMrNo.length() == 0)
			return mrNo;
		return getMergeMrno(newMrNo);
	}

	/**
	 * 病人是否存在
	 * 
	 * @param mrNo
	 *            String 病案号
	 * @return boolean true 存在 false 不存在
	 */
	public boolean existsPat(String mrNo) {
		if (mrNo == null || mrNo.length() == 0)
			return false;
		mrNo = checkMrno(mrNo);
		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		return getResultInt(query("existsPat", parm), "COUNT") > 0;
	}

	/**
	 * 加锁病患(前台调用)
	 * 
	 * @param mrNo
	 *            String 病案号
	 * @param program
	 *            String 程序名
	 * @return boolean true 成功 false 失败
	 */
	public boolean lockPat(String mrNo, String program) {
		return lockPat(mrNo, program, Operator.getID(), Operator.getIP());
	}

	/**
	 * 加锁病患(后台调用)
	 * 
	 * @param mrNo
	 *            String 病案号
	 * @param program
	 *            String 程序名
	 * @param userId
	 *            String 操作人员
	 * @param userIp
	 *            String 操作终端
	 * @return boolean
	 */
	public boolean lockPat(String mrNo, String program, String userId,
			String userIp) {
		if (mrNo == null || mrNo.length() == 0)
			return false;
		TParm parm = new TParm();
		mrNo = checkMrno(mrNo);
		parm.setData("MR_NO", mrNo);
		parm.setData("TERM_IP", userIp);
		parm.setData("OPT_USER", userId);
		parm.setData("PRG_ID", program);
		TParm result = update("lockPat", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return false;
		}
		return true;
	}

	/**
	 * 病患是否已经锁定
	 * 
	 * @param mrNo
	 *            String 病案号
	 * @return boolean true 锁定 false 没有锁定
	 */
	public boolean isLockPat(String mrNo) {
		if (mrNo == null || mrNo.length() == 0)
			return false;
		TParm result = getLockPat(mrNo);
		// System.out.println("result=="+result);
		if (result == null)
			return false;
		return result.getCount() > 0;
	}

	/**
	 * 得到加锁信息
	 * 
	 * @param mrNo
	 *            String 病案号
	 * @return TParm OPT_USER;TERM_IP;LOCK_TIME;PRG_ID
	 */
	public TParm getLockPat(String mrNo) {
		if (mrNo == null || mrNo.length() == 0)
			return null;
		TParm parm = new TParm();
		mrNo = checkMrno(mrNo);
		parm.setData("MR_NO", mrNo);
		return query("getLockPat", parm);
	}

	/**
	 * 解锁病患
	 * 
	 * @param mrNo
	 *            String 病案号
	 * @return boolean true 成功 false 失败
	 */
	public boolean unLockPat(String mrNo) {
		if (mrNo == null || mrNo.length() == 0)
			return false;
		TParm parm = new TParm();
		mrNo = checkMrno(mrNo);
		parm.setData("MR_NO", mrNo);
		TParm result = update("unLockPat", parm);
		if (result.getErrCode() < 0)
			return false;
		return true;
	}

	/**
	 * 得到加锁信息
	 * 
	 * @param mrNo
	 *            String 病案号
	 * @return String
	 */
	public String getLockParmString(String mrNo) {
		TParm result = getLockPat(mrNo);
		if (result == null)
			return "";
		if (result.getCount() == 0)
			return "";
		String userName = OperatorTool.getInstance().getOperatorName(
				result.getValue("OPT_USER", 0));
		String time = StringTool.getString(result.getTimestamp("OPT_DATE", 0),
				"yyyy年MM月dd日HH:mm:ss");
		String name = getNameForMrno(mrNo);
		String ip = result.getValue("OPT_TERM", 0);
		String program = result.getValue("PRG_ID", 0);
		return userName + "在" + time + "锁定患者" + name + "," + ip + program;
	}

	/**
	 * 更新病患信息(For REG)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm upDateForReg(TParm parm) {
		TParm result = new TParm();
		if (parm == null)
			return result;
		result = update("upDateForReg", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * adm病患查询
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm queryPat(TParm parm) {
		// System.out.println("查询");
		TParm result = query("queryAdmPat", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新最近就诊信息
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm upLatestDeptDate(TParm parm, TConnection connection) {
		TParm result = new TParm();
		if (parm == null)
			return result;
		String admType = parm.getValue("ADM_TYPE");
		String visitCode = parm.getValue("VISIT_CODE");
		String deptCode = parm.getValue("REALDEPT_CODE");
		String mrNo = parm.getValue("MR_NO");
		String sql = "";
		String visitWhere = "";
		if ("0".equals(visitCode)) {
			visitWhere = " FIRST_ADM_DATE = SYSDATE, ";
		}
		if ("O".equals(admType)) {
			sql = "UPDATE SYS_PATINFO " + "   SET " + visitWhere
					+ "       RCNT_OPD_DATE = SYSDATE,"
					+ "       RCNT_OPD_DEPT = '" + deptCode + "' "
					+ " WHERE MR_NO = '" + mrNo + "' ";
		}
		if ("E".equals(admType)) {
			sql = "UPDATE SYS_PATINFO " + "   SET " + visitWhere
					+ "       RCNT_EMG_DATE = SYSDATE,"
					+ "       RCNT_EMG_DEPT = '" + deptCode + "' "
					+ " WHERE MR_NO = '" + mrNo + "' ";
		}
		if ("I".equals(admType)) {
			sql = "UPDATE SYS_PATINFO " + "   SET RCNT_IPD_DATE = SYSDATE,"
					+ "       RCNT_IPD_DEPT = '" + deptCode + "' "
					+ " WHERE MR_NO = '" + mrNo + "' ";
		}
		// System.out.println("sql:"+sql);
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		return result;
	}

	/**
	 * 根据身份证号码查询基本信息 住院医保资格确认书开立下载使用
	 * 
	 * @param parm IDNO
	 * ======pangben 20120127
	 * @return TParm 
	 */
	public TParm getInfoForIdNo(TParm parm) {
		TParm result = query("getInfoForIdNo", parm);
		if (result.getErrCode() < 0)
			err(parm.getErrCode() + " " + parm.getErrText());
		return result;
	}
	
	/**
	 * 
	 * 更新病患血型.
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updatePatBldType(TParm parm,TConnection conn){
        TParm result = this.update("updatePatBldType", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * 验证出生地编码是否存在
	 * @param homeplacecode
	 * @return
	 */
	public boolean isExistHomePlace(String homeplacecode){
		if(homeplacecode.equals("")){
			return false;
		}
		String  sql="SELECT * FROM SYS_HOMEPLACE WHERE HOMEPLACE_CODE='"+homeplacecode+"'";
	    TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
	    if(parm.getErrCode()<0||parm.getCount()<=0){
	    	return false;
	    }
		return true;
	}
	
	/**
	 * 合并病案号后查询数据 要把所有病案号都查询出来
	 * @param mrNo
	 * @return
	 */
	public String getMrRegMrNos(String mrNo){
//		String meRegMrNo = "";
//		String re ="";
//		String sql = "SELECT MERGE_TOMRNO FROM SYS_PATINFO WHERE MR_NO='"+mrNo+"'";
//		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//		if(parm.getValue("MERGE_TOMRNO", 0).length() > 0){
//			meRegMrNo = parm.getValue("MERGE_TOMRNO", 0);
//			re = "'"+meRegMrNo+"'";
//			sql = "SELECT MR_NO FROM SYS_PATINFO WHERE MERGE_TOMRNO IN( '"+mrNo+"','"+meRegMrNo+"') AND MERGE_FLG='Y'";
//		}else{
//			 re = "'"+mrNo+"'";
//			 sql = "SELECT MR_NO FROM SYS_PATINFO WHERE MERGE_TOMRNO='"+mrNo+"' AND MERGE_FLG='Y'";
//		}
		String re ="'"+mrNo+"'";
		String sql = "SELECT MR_NO,MERGE_FLG FROM SYS_PATINFO WHERE MERGE_TOMRNO='"+mrNo+"' AND MERGE_FLG='Y'";
		
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() > 0){
			String meRegMrNo = "";
			for (int i = 0; i < parm.getCount(); i++) {
				re += ",'"+parm.getValue("MR_NO", i)+"'";
				if(parm.getBoolean("MERGE_FLG", i)){
					meRegMrNo+= "'"+parm.getValue("MR_NO", i)+"',";
				}
			}
			
			if(meRegMrNo.length()> 0){
				meRegMrNo = meRegMrNo.substring(0,meRegMrNo.length()-1);
				sql = "SELECT MR_NO,MERGE_FLG FROM SYS_PATINFO WHERE MERGE_TOMRNO IN ("+meRegMrNo+") AND MERGE_FLG='Y'";
				parm = new TParm(TJDODBTool.getInstance().select(sql));
				for (int i = 0; i < parm.getCount(); i++) {
					re += ",'"+parm.getValue("MR_NO", i)+"'";
				}
			}
			
		}
		
		
		
		return re;
	}
	
	public List<String> getMrNoList(String mrNo){
		List<String> m = new ArrayList<String>();
		String sql = "SELECT MR_NO FROM SYS_PATINFO WHERE MERGE_TOMRNO='"+mrNo+"' AND MERGE_FLG='Y'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() > 0){
			for (int i = 0; i < parm.getCount(); i++) {
				m.add(parm.getValue("MR_NO", i));
			}
		}
		return m;
	}
	
	public boolean selCheckIdNo(String idNo,String patName){
		boolean flg = false;
		String sql = "SELECT MR_NO FROM SYS_PATINFO WHERE IDNO='"+idNo+"' AND PAT_NAME='"+patName+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() > 1){
			flg = true;
		}
		return flg;
		
		
	}

	public static void main(String args[]) {
		TDebug.initClient();
		// TDebug.initServer();
		PatTool pat = new PatTool();
		// System.out.println(pat.lockPat("16","UddRtnRgstPat"));
		// System.out.println(pat.getLockParmString("27"));
		// System.out.println(pat.getNameForMrno("308"));
		// pat.setModuleName("sys\\SYSOperatorModule.x");
		/*
		 * pat.onInit(); TParm parm = new TParm();
		 * parm.setData("FINAL_FLG","Y"); parm.setData("CLASSIFY","9");
		 * System.out.println(pat.query("selectByWhere",parm));
		 */
	}
	
}
