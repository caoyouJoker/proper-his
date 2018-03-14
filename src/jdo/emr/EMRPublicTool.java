package jdo.emr;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import jdo.reg.RegPreedTool;
import jdo.sys.Operator;

/**
 * <p>病历公共jdo</p>
 * 
 * @author wangqing 20170731
 */
public class EMRPublicTool {
	private static EMRPublicTool instanceObject;
	
	private EMRPublicTool(){

	}
	
	public static synchronized EMRPublicTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new EMRPublicTool();
		}
		return instanceObject;
	}

	/**
	 * <p>获取病历模板</p>
	 * @param subClassCodeConfig
	 * @return
	 */
	public String[] getEmrTemplet(String subClassCodeConfig){ 	
		String subClassCode = TConfig.getSystemValue(subClassCodeConfig);
		TParm result = new TParm();
		String sql = "SELECT CLASS_CODE,SUBCLASS_CODE,SUBCLASS_DESC,TEMPLET_PATH," +
				"SEQ,EMT_FILENAME FROM EMR_TEMPLET WHERE SUBCLASS_CODE='"+subClassCode+"'";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		String s[] = null;
		if (result.getCount("CLASS_CODE") > 0) {
			s = new String[] {
					result.getValue("TEMPLET_PATH", 0),
					result.getValue("SUBCLASS_DESC", 0),
					result.getValue("SUBCLASS_CODE", 0)};
		}
		return s;     
	}

	/**
	 * <p>保存病历</p>
	 * @param caseNo 急诊就诊号
	 * @param classCodeConfig
	 * @param subClassCodeConfig
	 * @param subClassDesc
	 * @return
	 */
	public TParm saveEmrFile(String caseNo, String classCodeConfig, String subClassCodeConfig, String subClassDesc)
	{
		TParm result = new TParm();
		TParm action = new TParm(TJDODBTool.getInstance().select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"'"));
		String indexStr = "01";
		String classCode = TConfig.getSystemValue(classCodeConfig);
		String subClassCode = TConfig.getSystemValue(subClassCodeConfig);
		int index = action.getInt("MAXFILENO",0);
		if(index<10){
			indexStr = "0"+index;
		}else{
			indexStr = ""+index;
		}
		String mrNo="";
		String fileName = caseNo+"_"+subClassDesc+"_"+indexStr;
		String filePath = "JHW"+"\\"+caseNo.substring(2,4)+"\\"+caseNo.substring(4,6)+"\\"+caseNo;
		TParm saveParm = new TParm(TJDODBTool.getInstance().update("INSERT INTO EMR_FILE_INDEX (CASE_NO, FILE_SEQ, MR_NO, IPD_NO, FILE_PATH,FILE_NAME, DESIGN_NAME, CLASS_CODE, SUBCLASS_CODE, DISPOSAC_FLG,OPT_USER, OPT_DATE, OPT_TERM,CREATOR_USER, CREATOR_DATE) VALUES "+
				" ('"+caseNo+"', '"+indexStr+"', '"+mrNo+"', '', '"+filePath+"', "+
				" '"+fileName+"', '"+fileName+"', '"+classCode+"', '"+subClassCode+"', 'N',"+
				" '"+Operator.getID()+"', SYSDATE, '"+Operator.getIP()+"','"+Operator.getID()+"', SYSDATE)"));

		if(saveParm.getErrCode()<0)
			return saveParm;
		result.setData("PATH",filePath);
		result.setData("FILENAME",fileName);
		return result;
	}

	/**
	 * <p>拿到之前保存的病历</p>
	 * @param caseNo 急诊就诊号
	 * @param classCodeConfig
	 * @param subClassCodeConfig
	 * @return
	 */
	public String[] getEmrFile(String caseNo, String classCodeConfig, String subClassCodeConfig)
	{
		String classCode = TConfig.getSystemValue(classCodeConfig);
		String subclassCode = TConfig.getSystemValue(subClassCodeConfig);
		TParm emrParm = new TParm(TJDODBTool.getInstance().select("SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE "
				+ "FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'"));
		String dir="";
		String file="";
		String subClassCode = "";
		if(emrParm.getCount()>0){
			dir = emrParm.getValue("FILE_PATH",0);
			file = emrParm.getValue("FILE_NAME",0);
			subClassCode = emrParm.getValue("SUBCLASS_CODE",0);
			String s[] = {dir,file,subClassCode};
			return s;
		}else{
			return null;
		}	
	}
	
	/**
	 * <p>删除已有病历</p>
	 * @param caseNo
	 * @param classCodeConfig
	 * @param subClassCodeConfig
	 * @return true 成功；false 失败
	 */
	public boolean deleteEmrFile(String caseNo, String classCodeConfig, String subClassCodeConfig){
		String classCode = TConfig.getSystemValue(classCodeConfig);
		String subclassCode = TConfig.getSystemValue(subClassCodeConfig);
		String sql = " DELETE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'";
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
			return false;
		}
		return true;
	}
	
	
	//------------------rebuild by wangqing 20180123 start--------------
	/**
	 * 保存病历索引，之前的保存方法没有保存病案号
	 * @author wangqing
	 * @param parm
	 * @return
	 */
	public TParm saveEmrFile(TParm parm)
	{
		TParm result = new TParm();
		if(parm==null){
			result.setErrCode(-1);
			result.setErrText("参数为null");
			return result;
		}
		String caseNo = parm.getValue("CASE_NO");// 可能是检伤号
		String classCodeConfig = parm.getValue("CLASS_CODE_CONFIG");
		String subClassCodeConfig = parm.getValue("SUB_CLASS_CODE_CONFIG");
		String subClassDesc = parm.getValue("SUB_CLASS_DESC");
		String mrNo = parm.getValue("MR_NO");
		if(caseNo==null || caseNo.trim().length()<=0){
			result.setErrCode(-1);
			result.setErrText("CASE_NO为null");
			return result;
		}
		if(classCodeConfig==null || classCodeConfig.trim().length()<=0){
			result.setErrCode(-1);
			result.setErrText("CLASS_CODE_CONFIG为null");
			return result;
		}
		if(subClassCodeConfig==null || subClassCodeConfig.trim().length()<=0){
			result.setErrCode(-1);
			result.setErrText("SUB_CLASS_CODE_CONFIG为null");
			return result;
		}
		if(subClassDesc==null || subClassDesc.trim().length()<=0){
			result.setErrCode(-1);
			result.setErrText("SUB_CLASS_DESC为null");
			return result;
		}
		if(mrNo==null){
			mrNo="";
		}
		String classCode = TConfig.getSystemValue(classCodeConfig);
		String subClassCode = TConfig.getSystemValue(subClassCodeConfig);
		if(classCode==null || classCode.trim().length()<=0){
			result.setErrCode(-1);
			result.setErrText("TConfig.x没有配置"+classCodeConfig);
			return result;
		}
		if(subClassCode==null || subClassCode.trim().length()<=0){
			result.setErrCode(-1);
			result.setErrText("TConfig.x没有配置"+subClassCodeConfig);
			return result;
		}
		// 获取seq	
		TParm action = new TParm(TJDODBTool.getInstance().select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"'"));
		String indexStr = "01";		
		int index = action.getInt("MAXFILENO",0);
		if(index<10){
			indexStr = "0"+index;
		}else{
			indexStr = ""+index;
		}
		
		String fileName = caseNo+"_"+subClassDesc+"_"+indexStr;
		String filePath = "JHW"+"\\"+caseNo.substring(2,4)+"\\"+caseNo.substring(4,6)+"\\"+caseNo;
		TParm saveParm = new TParm(TJDODBTool.getInstance().update("INSERT INTO EMR_FILE_INDEX (CASE_NO, FILE_SEQ, MR_NO, IPD_NO, FILE_PATH,FILE_NAME, DESIGN_NAME, CLASS_CODE, SUBCLASS_CODE, DISPOSAC_FLG,OPT_USER, OPT_DATE, OPT_TERM,CREATOR_USER, CREATOR_DATE) VALUES "+
				" ('"+caseNo+"', '"+indexStr+"', '"+mrNo+"', '', '"+filePath+"', "+
				" '"+fileName+"', '"+fileName+"', '"+classCode+"', '"+subClassCode+"', 'N',"+
				" '"+Operator.getID()+"', SYSDATE, '"+Operator.getIP()+"','"+Operator.getID()+"', SYSDATE)"));

		if(saveParm.getErrCode()<0)
			return saveParm;
		result.setData("PATH",filePath);
		result.setData("FILENAME",fileName);
		return result;
	}

	/**
	 * 拿到之前保存的病历
	 * @param caseNo
	 * @param classCodeConfig
	 * @param subClassCodeConfig
	 * @return
	 */
	public String[] getEmrFileRebuild(String caseNo, String classCodeConfig, String subClassCodeConfig)
	{		
		String classCode = TConfig.getSystemValue(classCodeConfig);
		String subclassCode = TConfig.getSystemValue(subClassCodeConfig);
		TParm emrParm = new TParm(TJDODBTool.getInstance().select("SELECT CASE_NO, MR_NO, FILE_PATH,FILE_NAME,SUBCLASS_CODE "
				+ "FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'"));
		String dir="";
		String file="";
		String subClassCode = "";
		String mrNo = "";// add mrNo by wangqing 20180123
		if(emrParm.getCount()>0){
			dir = emrParm.getValue("FILE_PATH",0);
			file = emrParm.getValue("FILE_NAME",0);
			subClassCode = emrParm.getValue("SUBCLASS_CODE",0);
			mrNo = emrParm.getValue("MR_NO",0);
			String s[] = {dir,file,subClassCode, mrNo};
			return s;
		}else{
			return null;
		}	
	}
	
	
	
	
	
}
