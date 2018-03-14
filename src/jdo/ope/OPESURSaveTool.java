package jdo.ope;

import jdo.sys.Operator;

import java.text.SimpleDateFormat;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

public class OPESURSaveTool {
	private static OPESURSaveTool instanceObject;
	public OPESURSaveTool(){
		
	}
	public static synchronized OPESURSaveTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new OPESURSaveTool();
        }
        return instanceObject;
    }
	
	  /**
     * 取得手术室护士-胸痛中心记录
     * @return
     */
    public String[] getErdLevelTemplet(){
    	
        //String subClassCode = TConfig.getSystemValue("ERDLevelSUBCLASSCODE");
        String subClassCode = "EMR02000611";
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
     * 新建
     * @param mrNo String
     * @param caseNo String
     * @return String[]
     */
    public TParm saveELFile(String caseNo,String subclassCode,String name)
    {
        TParm result = new TParm();
        TParm action = new TParm(this.getDBTool().select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"'"));
        String indexStr = "01";
        //String classCode = TConfig.getSystemValue("ERDLevelCLASSCODE");
        String classCode = "EMR020006";
        int index = action.getInt("MAXFILENO",0);
        if(index<10){
           indexStr = "0"+index;
        }else{
            indexStr = ""+index;
        }
        String mrNo="";
        String fileName = caseNo+"_"+name+"_"+indexStr;
        //String filePath = "JHW"+"\\"+caseNo.substring(2,4)+"\\"+caseNo.substring(4,6)+"\\"+caseNo;//triagono
        String filePath = "JHW"+"\\"+caseNo.substring(0,2)+"\\"+caseNo.substring(2,4)+"\\"+caseNo;//caseno
        TParm saveParm = new TParm( this.getDBTool().update("INSERT INTO EMR_FILE_INDEX (CASE_NO, FILE_SEQ, MR_NO, IPD_NO, FILE_PATH,FILE_NAME, DESIGN_NAME, CLASS_CODE, SUBCLASS_CODE, DISPOSAC_FLG,OPT_USER, OPT_DATE, OPT_TERM,CREATOR_USER) VALUES "+
                                " ('"+caseNo+"', '"+indexStr+"', '"+mrNo+"', '', '"+filePath+"', "+
                                " '"+fileName+"', '"+fileName+"', '"+classCode+"', '"+subclassCode+"', 'N',"+
                                " '"+Operator.getID()+"', SYSDATE, '"+Operator.getIP()+"','"+Operator.getID()+"')"));

        if(saveParm.getErrCode()<0)
            return saveParm;
        result.setData("PATH",filePath);
        result.setData("FILENAME",fileName);
        return result;
    }
    
    /**
     * 删除
     * @return 
     */
    public TParm onDeleteSURRecord(String TRIAGE_NO)
    {

		String deletesql =
      	      " DELETE FROM AMI_SUR_NS_RECORD WHERE CASE_NO = '" +
      	    		TRIAGE_NO + "' ";
        TParm delParm = new TParm(TJDODBTool.getInstance().update(
      	   deletesql));
        if(delParm.getErrCode()<0)
            return delParm;
        else
        	return null;

    }
    /**
	 * 插入 AMI_SUR_NS_RECORD
	 * */
	public TParm onInsertNSRecord(TParm parm) {
		

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//進行轉換
		//Date date = sdf.parse(dateString);
		TParm saveParm = new TParm( this.getDBTool().update("INSERT INTO AMI_SUR_NS_RECORD (CASE_NO,CABG_DECIDE_TIME, CABG_START_TIME, CABG_END_TIME, THOR_DECIDE_TIME, THOR_INFO_CONT_START,THOR_INFO_CONT_SIGN, THOR_START_TIME, THOR_END_TIME, THOR_RESULT, OPT_USER, OPT_DATE, OPT_TERM) VALUES "+
                " ('"+parm.getData("CASE_NO")+"', TO_DATE('"+parm.getData("CABG_DECIDE_TIME")+"','yyyy/MM/dd HH24:mi:ss'), TO_DATE('"+parm.getData("CABG_START_TIME")+"','yyyy/MM/dd HH24:mi:ss'), TO_DATE('"+parm.getData("CABG_END_TIME")+"','yyyy/MM/dd HH24:mi:ss'), "+
                " TO_DATE('"+parm.getData("THOR_DECIDE_TIME")+"','yyyy/MM/dd HH24:mi:ss'), TO_DATE('"+parm.getData("THOR_INFO_CONT_START")+"','yyyy/MM/dd HH24:mi:ss'), TO_DATE('"+parm.getData("THOR_INFO_CONT_SIGN")+"','yyyy/MM/dd HH24:mi:ss'), TO_DATE('"+parm.getData("THOR_START_TIME")+"','yyyy/MM/dd HH24:mi:ss'), "+
                " TO_DATE('"+parm.getData("THOR_END_TIME")+"','yyyy/MM/dd HH24:mi:ss'), '"+parm.getData("THOR_RESULT")+"', "+
                " '"+Operator.getID()+"', SYSDATE, '"+Operator.getIP()+"')"));

//			if(saveParm.getErrCode()<0)
			return saveParm;

	}
	
	
	
    /**
     * 拿CABG 记录
     * @param triageNo String
     * @return TParm
     */
    public TParm getCABGOPBOOK1(String caseNo) {
    	String sql = "SELECT OPE_OPBOOK.OP_CODE1,SYS_OPERATIONICD.OPT_CHN_DESC,OPE_OPBOOK.OPT_DATE,OPE_OPBOOK.CASE_NO"
    			+ " FROM OPE_OPBOOK INNER JOIN SYS_OPERATIONICD ON OPE_OPBOOK.OP_CODE1 = SYS_OPERATIONICD.OPERATION_ICD WHERE SYS_OPERATIONICD.OPT_CHN_DESC like '%搭桥%'"
    			//+" AND CASE_NO='120607000221'";
    		    + " AND CASE_NO='"+caseNo+"' order by OPT_DATE desc";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 拿CABG 记录
     * @param triageNo String
     * @return TParm
     */
    public TParm getCABGOPBOOK2(String caseNo) {
    	String sql = "SELECT OPE_OPBOOK.OP_CODE2,SYS_OPERATIONICD.OPT_CHN_DESC,OPE_OPBOOK.OPT_DATE,OPE_OPBOOK.CASE_NO"
    			+ " FROM OPE_OPBOOK INNER JOIN SYS_OPERATIONICD ON OPE_OPBOOK.OP_CODE2 = SYS_OPERATIONICD.OPERATION_ICD WHERE SYS_OPERATIONICD.OPT_CHN_DESC like '%搭桥%'"
    			//+" AND CASE_NO='120607000221'";
    		    + " AND CASE_NO='"+caseNo+"' order by OPT_DATE desc";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 拿CABG 记录
     * @param triageNo String
     * @return TParm
     */
    public TParm getCABGDETAIL(String caseNo) {
    	String sql = "SELECT OPE_OPDETAIL.OP_START_DATE,OPE_OPDETAIL.OP_END_DATE"
    			+ " FROM OPE_OPDETAIL"
    			//+" WHERE CASE_NO='120607000221'";
    		    + " WHERE CASE_NO='"+caseNo+"' order by OPT_DATE desc";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 拿开胸 记录
     * @param triageNo String
     * @return TParm
     */
    public TParm getETOPBOOK1(String caseNo) {
    	String sql = "SELECT OPE_OPBOOK.OP_CODE1,SYS_OPERATIONICD.OPT_CHN_DESC,OPE_OPBOOK.OPT_DATE,OPE_OPBOOK.CASE_NO"
    			+ " FROM OPE_OPBOOK INNER JOIN SYS_OPERATIONICD ON OPE_OPBOOK.OP_CODE1 = SYS_OPERATIONICD.OPERATION_ICD WHERE SYS_OPERATIONICD.OPT_CHN_DESC like '%开胸%'"
    			//+"AND CASE_NO='150708002221'";
    		    + " AND CASE_NO='"+caseNo+"' order by OPT_DATE desc";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 拿开胸 记录
     * @param triageNo String
     * @return TParm
     */
    public TParm getETOPBOOK2(String caseNo) {
    	String sql = "SELECT OPE_OPBOOK.OP_CODE2,SYS_OPERATIONICD.OPT_CHN_DESC,OPE_OPBOOK.OPT_DATE,OPE_OPBOOK.CASE_NO"
    			+ " FROM OPE_OPBOOK INNER JOIN SYS_OPERATIONICD ON OPE_OPBOOK.OP_CODE2 = SYS_OPERATIONICD.OPERATION_ICD WHERE SYS_OPERATIONICD.OPT_CHN_DESC like '%开胸%'"
    			//+"AND CASE_NO='150708002221'";
    		    + " AND CASE_NO='"+caseNo+"' order by OPT_DATE desc";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 拿开胸 记录
     * @param triageNo String
     * @return TParm
     */
    public TParm getETDETAIL(String caseNo) {
    	String sql = "SELECT OPE_OPDETAIL.OP_START_DATE,OPE_OPDETAIL.OP_END_DATE"
    			+ " FROM OPE_OPDETAIL"
    			//+" WHERE CASE_NO='150708002221'";
    		    + " AND CASE_NO='"+caseNo+"' order by OPT_DATE desc";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
	
	
    /**
     * 拿开胸探查手术知情同意书
     * @param triageNo String
     * @return TParm
     */
    public TParm getET(String caseNo) {
    	String sql = "SELECT OPT_DATE"
    			+ " FROM EMR_THRFILE_INDEX WHERE FILE_NAME like '%开胸%'"
    			//+" AND CASE_NO='150708002221'";
    		    + " AND CASE_NO='"+caseNo+"' order by OPT_DATE desc";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
//            err("ERR:" + result.getErrCode() + result.getErrText() +
//            		result.getErrName());
            return result;
        }
        return result;
    }
	
    /**
     * 拿到之前保存的文件
     * @param caseNo String
     * @return String[]
     */
    public String[] getELFile(String caseNo)
    {
        //String classCode = TConfig.getSystemValue("ERDLevelCLASSCODE");
        //String subclassCode = TConfig.getSystemValue("ERDLevelSUBCLASSCODE");
        String classCode = "EMR020006";
        String subclassCode = "EMR02000611";
        TParm emrParm = new TParm(this.getDBTool().select("SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'"));
        //System.out.println("======已看诊  getGSTempletSql========="+"SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+caseNo+"' AND CLASS_CODE='"+classCode+"' AND SUBCLASS_CODE='"+subclassCode+"'");

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
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

}
