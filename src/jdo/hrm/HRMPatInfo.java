package jdo.hrm;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
/**
 * <p>Title: 健康检查病患</p>
 *
 * <p>Description: 健康检查病患</p>
 *
 * <p>Copyright: javahis 20090922</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMPatInfo extends TDataStore {
	private static final String INIT="SELECT * FROM SYS_PATINFO WHERE MR_NO='#'";
	private static final String IS_SAME_PAT_BY_NAME="SELECT A.*,B.BIRTH_DATE FROM HRM_PATADM A,SYS_PATINFO B WHERE A.PAT_NAME='#' AND A.MR_NO=B.MR_NO  ORDER BY A.REPORT_DATE DESC";
	private static final String IS_SAME_PAT_BY_ID="SELECT A.*,B.BIRTH_DATE FROM HRM_PATADM A,SYS_PATINFO B WHERE A.ID_NO='#' AND A.MR_NO=B.MR_NO ORDER BY A.REPORT_DATE DESC";
	private static final String GET_CASE_BY_MR="SELECT A.CASE_NO,A.REPORT_DATE,B.*,C.ROLE_TYPE FROM HRM_PATADM A,SYS_PATINFO B,HRM_CONTRACTD C WHERE B.MR_NO='#' AND A.MR_NO=B.MR_NO AND A.CONTRACT_CODE = C.CONTRACT_CODE AND A.MR_NO = C.MR_NO ORDER BY A.CASE_NO";
	private static final String GET_REVIEW="SELECT A.PAT_NAME PAT_NAME1,B.COMPANY_DESC,C.SEX_DESC,A.BIRTH_DATE,E.CHN_DESC,F.CHN_DESC,G.CHN_DESC,H.CHN_DESC,";
	
    private static final String GET_HRMPAT_BY_CASENO_MRNO =
            "SELECT A.*,"
                    + " (SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE B.GROUP_ID = 'SYS_SEX' AND A.SEX_CODE = B.ID) SEX_DESC,"//add by wanglong 20140214
                    + " (SELECT B.COMPANY_DESC FROM HRM_COMPANY B, HRM_CONTRACTD C WHERE B.COMPANY_CODE = C.COMPANY_CODE AND A.CONTRACT_CODE = C.CONTRACT_CODE AND A.MR_NO = C.MR_NO) COMPANY_DESC,"
                    + " (SELECT B.CONTRACT_DESC FROM HRM_CONTRACTD B WHERE A.CONTRACT_CODE = B.CONTRACT_CODE AND A.MR_NO = B.MR_NO) CONTRACT_DESC,"
                    + " (SELECT B.SEQ_NO FROM HRM_CONTRACTD B WHERE A.CONTRACT_CODE = B.CONTRACT_CODE AND A.MR_NO = B.MR_NO) SEQ_NO,"
                    + " (SELECT B.STAFF_NO FROM HRM_CONTRACTD B WHERE A.CONTRACT_CODE = B.CONTRACT_CODE AND A.MR_NO = B.MR_NO) STAFF_NO, "
                    + " (SELECT B.DEPT_CHN_DESC FROM SYS_DEPT B WHERE A.DEPT_CODE = B.DEPT_CODE) DEPT_DESC "//add by wanglong 20140214
                    + " FROM HRM_PATADM A WHERE A.MR_NO = '#' AND A.CASE_NO = '#' ";//modify by wanglong 20130813
	private String id = Operator.getID();
	private String ip = Operator.getIP();
	/**
	 * 查询
	 * @return
	 */
	public int onQuery(){
		this.setSQL(INIT);
		return 0;
	}
	/**
	 * 根据给定MR_NO查询
	 * @param mrNo
	 * @return
	 */
	public int onQuery(String mrNo){
		if(StringUtil.isNullString(mrNo)){
			return -1;
		}
		this.setSQL(INIT.replace("#", mrNo));
		return this.retrieve();
	}
	/**
	 * 新生成一条病患记录
	 * @return row int
	 */
	public int newPat(){
		int row=this.insertRow();
		this.setItem(row, "OPT_USER", id);
		this.setItem(row, "OPT_TERM", ip);
		this.setItem(row, "OPT_DATE", this.getDBTime());
		return row;
	}
	/**
	 * 根据给入的病患名称找寻相同患者
	 * @param patName
	 * @return
	 */
	public TParm isSamePatByName(String patName){
		TParm result=new TParm();
		if(StringUtil.isNullString(patName)){
			return result;
		}
		String selPat = " SELECT OPT_DATE AS REPORT_DATE,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,"
			+ "        POST_CODE,ADDRESS,MR_NO "
			+ "   FROM SYS_PATINFO "
			+ "  WHERE PAT_NAME = '"
			+ patName
			+ "' "
			+ "  ORDER BY OPT_DATE ";
		String sql=IS_SAME_PAT_BY_NAME.replace("#", patName);
		result=new TParm(TJDODBTool.getInstance().select(selPat));
		return result;
	}
	/**
	 * 根据给入的病患身份证号找寻相同患者
	 * @param id
	 * @return
	 */
	public TParm isSamePatByID(String id){
		TParm result=new TParm();
		if(StringUtil.isNullString(id)){
			return result;
		}
		String sql=IS_SAME_PAT_BY_ID.replace("#", id);
		//// System.out.println("isSamePatByID.sql="+sql);
		result=new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	/**
	 * 取得MR_NO
	 * @return
	 */
	public String getMrNo(){
		return SystemTool.getInstance().getMrNo();
	}
	/**
	 * 根据给入的TParm新增一条病患记录
	 * @param parm
	 * @return
	 */
	public boolean newPat(TParm parm){
		if(parm==null){
			return false;
		}
		int row=newPat();
		//PAT_NAME;MR_NO;IDNO;BIRTH_DATE;SEX_CODE;ADDRESS;TEL;POST_CODE
		this.setItem(row, "PAT_NAME", parm.getValue("PAT_NAME"));
		this.setItem(row, "MR_NO", parm.getValue("MR_NO"));
		this.setItem(row, "IDNO", parm.getValue("IDNO"));
		String birthday=parm.getValue("BIRTH_DATE");
		Timestamp birth=StringTool.getTimestamp(birthday, "yyyy-MM-dd HH:mm:ss");
		this.setItem(row, "BIRTH_DATE", birth);
		this.setItem(row, "SEX_CODE", parm.getValue("SEX_CODE"));
		this.setItem(row, "ADDRESS", parm.getValue("ADDRESS"));
		this.setItem(row, "TEL_HOME", parm.getValue("TEL"));
		this.setItem(row, "POST_CODE", parm.getValue("POST_CODE"));
		this.setItem(row, "FOREIGNER_FLG", parm.getValue("FOREIGNER_FLG"));
		return true;
	}
	/**
	 * 根据给入信息更新一个病患
	 * @param parm
	 * @return
	 */
	public boolean updatePat(TParm parm){
		if(parm==null){
			return false;
		}
		if(this.rowCount()<=0){
			return false;
		}
		if(StringUtil.isNullString(this.getItemString(0, "MR_NO"))){
			return false;
		}
		int row=0;
		//PAT_NAME;MR_NO;IDNO;BIRTH_DATE;SEX_CODE;ADDRESS;TEL;POST_CODE
		this.setItem(row, "PAT_NAME", parm.getValue("PAT_NAME"));
		this.setItem(row, "MR_NO", parm.getValue("MR_NO"));
		this.setItem(row, "IDNO", parm.getValue("IDNO"));
		String birthday=parm.getValue("BIRTH_DATE");
		Timestamp birth=StringTool.getTimestamp(birthday, "yyyy-MM-dd HH:mm:ss");
		this.setItem(row, "BIRTH_DATE", birth);
		this.setItem(row, "SEX_CODE", parm.getValue("SEX_CODE"));
		this.setItem(row, "ADDRESS", parm.getValue("ADDRESS"));
		this.setItem(row, "TEL_HOME", parm.getValue("TEL"));
		this.setItem(row, "POST_CODE", parm.getValue("POST_CODE"));
		this.setItem(row, "FOREIGNER_FLG", parm.getValue("FOREIGNER_FLG"));
		return true;

	}
	/**
	 * 根据病案号查询是否旧病人
	 * @param mrNo
	 * @return int -1:错误,0:旧病人,1:新病人
	 */
	public int isOldPat(String mrNo){
		if(StringUtil.isNullString(mrNo)){
			return -1;
		}
		String sql=this.INIT.replaceFirst("#", mrNo);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()!=0){
			return -1;
		}
		if(result.getCount()==1){
			return 0;
		}
		return 1;
	}
	/**
	 * 保存事件
	 * @return
	 */
	public TParm onSave(){
		TParm result=new TParm();
		String[] sql=this.getUpdateSQL();
		if(sql==null&&sql.length<1){
			return result;
		}
		result=new TParm(TJDODBTool.getInstance().update(sql));
		this.resetModify();
		return result;
	}
	/**
	 * 根据MR_NO查得历史记录
	 * @param mrNo
	 * @return
	 */
	public TParm getCaseByMr(String mrNo){
		TParm result=new TParm();
		if(StringUtil.isNullString(mrNo)){
			return null;
		}
		String sql=this.GET_CASE_BY_MR.replaceFirst("#", mrNo);
		// System.out.println("getCaseByMr.sql="+sql);
		result=new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	/**
	 * 根据病案号查询总检报告信息
	 * @param mrNo
	 * @return
	 */
	public TParm getReview(String mrNo){
		TParm result=new TParm();

		return result;
	}
	
	/**
	 * 获得健检病人基本信息
	 * @param mrNo
	 * @param caseNo
	 * @return
	 */
    public TParm getHRMPatInfo(String mrNo, String caseNo) {
        TParm result = new TParm();
        if (StringUtil.isNullString(mrNo)) {
            return null;
        }
        String sql = GET_HRMPAT_BY_CASENO_MRNO.replaceFirst("#", mrNo).replaceFirst("#", caseNo);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
}