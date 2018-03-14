package jdo.hrm;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
 * <p>Title: 健康检查挂号</p>
 *
 * <p>Description: 健康检查挂号</p>
 *
 * <p>Copyright: javahis 20090922</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMPatAdm extends TDS {
	private static final String INIT="SELECT * FROM HRM_PATADM WHERE CASE_NO='#'";
	private static final String INIT_BY_CASENO="SELECT * FROM HRM_PATADM WHERE CASE_NO='#'";
	private static final String INIT_BY_DEPT="SELECT * FROM HRM_PATADM WHERE DEPT_CODE='#'";
	private static final String GET_LATEST_CASE_NO="SELECT MAX(CASE_NO) CASE_NO FROM HRM_PATADM WHERE MR_NO ='#' ORDER BY CASE_NO DESC";
	
	/**
	 * 根据病案号／合同号查询HRM_PATADM里的就诊号
	 */
	private static final String GET_LATEST_CASE_NO_BY="SELECT MAX(CASE_NO) CASE_NO " +
													  "FROM HRM_PATADM " +
													  "WHERE MR_NO ='#1' AND CONTRACT_CODE='#2' ORDER BY CASE_NO DESC";
	
	private static final String GET_CASE_NO="SELECT CASE_NO FROM HRM_PATADM WHERE MR_NO ='#' ORDER BY CASE_NO DESC";
	private static final String GET_PAT_NAME_BY_CASE="SELECT PAT_NAME FROM HRM_PATADM WHERE CASE_NO='#'";
	//根据团体代码和合同代码查得数据
	private static final String GET_DATA_BY_COMPANY_CONTRACT=
		"SELECT A.MR_NO,A.PAT_NAME NAME,A.ID_NO,A.CASE_NO ID,B.PY1,B.PY2 " +
		"	FROM HRM_PATADM A,SYS_PATINFO B " +
		"	WHERE A.COMPANY_CODE='#' AND A.CONTRACT_CODE='#' AND A.MR_NO=B.MR_NO" +
		"	ORDER BY A.CASE_NO";
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
	 * 查询
	 * @return
	 */
	public int onQueryByCaseNo(String caseNo){
		this.setSQL(INIT.replaceFirst("#", caseNo));
		return this.retrieve();
	}
	/**
	 * 根据给入DEPT_CODE初始化就诊病患名单
	 * @param deptCode String
	 * @return int
	 */
	public int onQuery(String deptCode){
		if(StringUtil.isNullString(deptCode)){
			return -1;
		}
		this.setSQL(this.INIT_BY_DEPT.replaceFirst("#", deptCode));
		return this.retrieve();
	}
	
    /**
     * 初始化
     * @param parm
     * @return
     */
    public boolean onNewAdm(TParm parm) {
        return onNewAdm(parm, getDBTime());
    }
    
	/**
	 * 初始化
	 * @param parm
	 * @param now
	 * @return
	 */
	public boolean onNewAdm(TParm parm,Timestamp now){
		int row=this.insertRow();
		//CASE_NO
		String newCaseNo = SystemTool.getInstance().getNo("ALL", "REG",
	            "CASE_NO", "CASE_NO");
		if(StringUtil.isNullString(newCaseNo)){
			return false;
		}
		this.setItem(row, "CASE_NO", newCaseNo);
		//MR_NO
		this.setItem(row, "MR_NO", parm.getValue("MR_NO"));
		//FINAL_JUDGE_DR
		//FINAL_JUDGE_DATE

		//REPORT_DATE
		this.setItem(row, "REPORT_DATE", now);
		//START_DATE
		this.setItem(row, "START_DATE", now);
		//END_DATE
		this.setItem(row, "END_DATE", now);
		//PAT_NAME
		this.setItem(row, "PAT_NAME", parm.getValue("PAT_NAME"));
		//ID_NO
		this.setItem(row, "ID_NO", parm.getValue("IDNO"));
		//SEX_CODE
		this.setItem(row, "SEX_CODE", parm.getValue("SEX_CODE"));
		//BIRTHDAY
		Timestamp birthday=StringTool.getTimestamp(parm.getValue("BIRTH_DATE"),"yyyy-MM-dd HH:mm:ss");
		this.setItem(row, "BIRTHDAY", birthday);
		//CHARGE_DATE
		this.setItem(row, "CHAREGE_DATE", now);
		//TEL
		this.setItem(row, "TEL", parm.getValue("TEL"));
		//POST_CODE
		this.setItem(row, "POST_CODE", parm.getValue("POST_CODE"));
		//COVER_FLG
		this.setItem(row, "COVER_FLG", "Y");
		//ADDRESS
		this.setItem(row, "ADDRESS", parm.getValue("ADDRESS"));
		//DEPT_CODE
		this.setItem(row, "DEPT_CODE", Operator.getDept());
		//OPT_USER
		this.setItem(row, "OPT_USER", id);
		//OPT_DATE
		this.setItem(row, "OPT_DATE", now);
		//OPT_TERM
		this.setItem(row, "OPT_TERM", ip);
		//COMPANY_PAY_FLG
		this.setItem(row, "COMPANY_PAY_FLG", parm.getData("COMPANY_PAY_FLG"));
		//COMPANY_CODE
		this.setItem(row, "COMPANY_CODE", parm.getData("COMPANY_CODE"));
		//CONTRACT_CODE
		this.setItem(row, "CONTRACT_CODE", parm.getData("CONTRACT_CODE"));
	    //PACKAGE_CODE
        this.setItem(row, "PACKAGE_CODE", parm.getData("PACKAGE_CODE"));
		//REPORT_STATUS
		this.setItem(row, "REPORT_STATUS", "1");
		//REPORTLIST
		this.setItem(row, "REPORTLIST", parm.getData("REPORTLIST"));
		//INTRO_USER
		this.setItem(row, "INTRO_USER", parm.getData("INTRO_USER"));
		this.setItem(row, "MARRIAGE_CODE", parm.getData("MARRIAGE_CODE"));//add by wanglong 20130117
		this.setItem(row, "PAT_DEPT", parm.getData("PAT_DEPT"));//add by wanglong 20130225
	    this.setItem(row, "DISCNT", parm.getDouble("DISCNT"));//add by wanglong 20130226
		this.setActive(row,true);
		return true;
	}
	
	/**
	 * add by lx 
	 * 预报到
	 * @param parm
	 * @return
	 */
    public boolean onPreAdm(TParm parm){
        return onNewAdm(parm, getDBTime());
    }
	
    /**
     * add by wanglong
     * 预报到
     * @param parm
     * @param now
     * @return
     */
    public boolean onPreAdm(TParm parm, Timestamp now) {
        // Timestamp now = this.getDBTime();
        int row=this.insertRow();
        //CASE_NO
        String newCaseNo = SystemTool.getInstance().getNo("ALL", "REG",
                "CASE_NO", "CASE_NO");
        if(StringUtil.isNullString(newCaseNo)){
            return false;
        }
        this.setItem(row, "CASE_NO", newCaseNo);
        //MR_NO
        this.setItem(row, "MR_NO", parm.getValue("MR_NO"));
        //FINAL_JUDGE_DR
        //FINAL_JUDGE_DATE

        //REPORT_DATE
        //this.setItem(row, "REPORT_DATE", now);
        //START_DATE
        //this.setItem(row, "START_DATE", now);
        //END_DATE
        //this.setItem(row, "END_DATE", now);
        //COMPANY_CODE
        //CONTRACT_CODE
        
        //PAT_NAME
        this.setItem(row, "PAT_NAME", parm.getValue("PAT_NAME"));
        //ID_NO
        this.setItem(row, "ID_NO", parm.getValue("IDNO"));
        //SEX_CODE
        this.setItem(row, "SEX_CODE", parm.getValue("SEX_CODE"));
        //BIRTHDAY
        Timestamp birthday=StringTool.getTimestamp(parm.getValue("BIRTH_DATE"),"yyyy-MM-dd HH:mm:ss");
        this.setItem(row, "BIRTHDAY", birthday);
        //CHARGE_DATE
        this.setItem(row, "CHAREGE_DATE", now);
        //TEL
        this.setItem(row, "TEL", parm.getValue("TEL"));
        //POST_CODE
        this.setItem(row, "POST_CODE", parm.getValue("POST_CODE"));
        //COVER_FLG
        this.setItem(row, "COVER_FLG", "N");//未到检
        //ADDRESS
        this.setItem(row, "ADDRESS", parm.getValue("ADDRESS"));
        //DEPT_CODE
        this.setItem(row, "DEPT_CODE", Operator.getDept());
        //OPT_USER
        this.setItem(row, "OPT_USER", id);
        //OPT_DATE
        this.setItem(row, "OPT_DATE", now);
        //OPT_TERM
        this.setItem(row, "OPT_TERM", ip);
        //COMPANY_PAY_FLG
        this.setItem(row, "COMPANY_PAY_FLG", parm.getData("COMPANY_PAY_FLG"));
        //COMPANY_CODE
        this.setItem(row, "COMPANY_CODE", parm.getData("COMPANY_CODE"));
        //CONTRACT_CODE
        this.setItem(row, "CONTRACT_CODE", parm.getData("CONTRACT_CODE"));
        //PACKAGE_CODE
        this.setItem(row, "PACKAGE_CODE", parm.getData("PACKAGE_CODE"));
        //REPORT_STATUS
        this.setItem(row, "REPORT_STATUS", "1");
        //REPORTLIST
        this.setItem(row, "REPORTLIST", parm.getData("REPORTLIST"));
        //INTRO_USER
        this.setItem(row, "INTRO_USER", parm.getData("INTRO_USER"));
        this.setItem(row, "MARRIAGE_CODE", parm.getData("MARRIAGE_CODE"));// add by wanglong 20130117
        this.setItem(row, "PAT_DEPT", parm.getData("PAT_DEPT"));// add by wanglong 20130225
        this.setItem(row, "DISCNT", parm.getDouble("DISCNT"));// add by wanglong 20130226
        this.setActive(row,true);
        return true;
    }
    
	/**
	 * 根据病案号得到就诊序号
	 * @param mrNo
	 * @return
	 */
	public static String getLatestCaseNo(String mrNo){
		String caseNo="";
		if(StringUtil.isNullString(mrNo)){
			// System.out.println("mr is none");
			return caseNo;
		}
		String sql=GET_LATEST_CASE_NO.replace("#", mrNo);
//		// System.out.println("getLatestCaseNo.sql========="+sql);
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		if(parm==null){
//			// System.out.println("parm is null");
			return caseNo;
		}
		if(parm.getErrCode()!=0){
//			// System.out.println("parm.wrong="+parm.getErrText());
			return caseNo;
		}
		caseNo=parm.getValue("CASE_NO",0);
		return caseNo;
	}
	
	/**
	 * 得到就诊序号
	 * @param mrNo           病案号
	 * @param contract_code  合同号
	 * @return
	 */
	public static String getLatestCaseNoBy(String mrNo,String contractCode){
		String caseNo="";
		if(StringUtil.isNullString(mrNo) || StringUtil.isNullString(contractCode)){
			return caseNo;
		}
		String sql=GET_LATEST_CASE_NO_BY.replace("#1", mrNo).replace("#2", contractCode);
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		if(parm==null){
			return caseNo;
		}
		if(parm.getErrCode()!=0){
			return caseNo;
		}
		caseNo=parm.getValue("CASE_NO",0);
		return caseNo;
	}
	
	
	
	
	/**
	 * 得到其他列数据
	 * @param parm TParm
	 * @param row int
	 * @param column String
	 * @return Object
	 */
	public Object getOtherColumnValue(TParm parm, int row, String column) {
		if("DONE".equalsIgnoreCase(column)){
			return StringUtil.isNullString(parm.getValue("FINAL_JUDGE_DR", row));
		}
		return "";
	}
	/**
	 * 根据CASE_NO查得病患名称
	 * @param caseNo
	 * @return
	 */
	public String getPatNameByCase(String caseNo){
		String patName="";
		if(StringUtil.isNullString(caseNo)){
			return patName;
		}
		String sql=this.GET_PAT_NAME_BY_CASE.replaceFirst("#", caseNo);
		// System.out.println("sql="+sql);
		patName=new TParm(TJDODBTool.getInstance().select(sql)).getValue("PAT_NAME",0);
		return patName;

	}
	/**
	 * 根据团体代码和合同代码查询数据
	 * @param companyCode
	 * @param contractCode
	 * @return
	 */
	public TParm getTParmByCompanyAndContract(String companyCode,String contractCode){
		TParm result=new TParm();
		if(StringUtil.isNullString( companyCode)||StringUtil.isNullString( contractCode)){
			return null;
		}
		String sql=this.GET_DATA_BY_COMPANY_CONTRACT.replaceFirst("#", companyCode).replaceFirst("#", contractCode);
		// System.out.println("getTParmByCompanyAndContract.sql="+sql);
		result=new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	
	/**
	 * 更新报到标志
	 * 
	 * @param caseNo
	 * @param now
	 */
	public TParm updateCoverFlg(String caseNo, Timestamp now,String tel) {
		TParm result = new TParm();
		String sql = "UPDATE HRM_PATADM SET COVER_FLG='Y',";
		sql += "REPORT_DATE=TO_DATE('"
				+ StringTool.getString(now, "yyyyMMddHHmmss")
				+ "','yyyyMMddHH24miss'),";

		sql += "START_DATE=TO_DATE('"
				+ StringTool.getString(now, "yyyyMMddHHmmss")
				+ "','yyyyMMddHH24miss'),";
		sql += "END_DATE=TO_DATE('"
				+ StringTool.getString(now, "yyyyMMddHHmmss")
				+ "','yyyyMMddHH24miss'),";
		sql += "TEL='"+tel+"' ";

		sql += " WHERE CASE_NO='" + caseNo + "'";
		result = new TParm(TJDODBTool.getInstance().update(sql));
		return result;
	
	}
}
