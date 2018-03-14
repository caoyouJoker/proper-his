package action.pda;

import org.apache.commons.lang.StringUtils;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

import jdo.pda.PDATool;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PDAaction extends TAction {
	/**
	 * 介入安全核查单保存
	 * @param parm
	 * @return
	 */
	public TParm onSaveInterCheck(TParm parm){
		TConnection conn = getConnection();
		TParm result = new TParm();
		String sqlSelect = " SELECT * FROM OPE_CHECK WHERE OPBOOK_SEQ = '"+parm.getValue("OPBOOK_SEQ")+"'  ";
//		System.out.println("sqlSelect:"+sqlSelect);
		TParm parmsql = new TParm(TJDODBTool.getInstance().select(sqlSelect));
		
		/**保存结构化病例，插入EMR_FILE_INDEX start*/
		TParm action = new TParm(this.getDBTool().select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"+parm.getValue("CASE_NO")+"'"));
		String indexStr = "01";
		int index = action.getInt("MAXFILENO",0);
		if(index<10){
	           indexStr = "0"+index;
	        }else{
	            indexStr = ""+index;
	        }
//		String filePath = "JHW"+"\\"+parm.getValue("CASE_NO").substring(2,4)+"\\"+parm.getValue("CASE_NO").substring(4,6)+"\\"+parm.getValue("CASE_NO");
		String filePath = "JHW"+"\\"+parm.getValue("CASE_NO").substring(0,2)+"\\"+parm.getValue("CASE_NO").substring(2,4)+"\\"+parm.getValue("MR_NO");//modify by wangjc 路径改为年月+MR_NO
		String fileName = parm.getValue("CASE_NO")+"_"+"介入安全核查单"+"_"+indexStr;
		String gesignName = "介入安全核查单"+"("+parm.getValue("DSTR")+")";
//		System.out.println("gesignName:"+gesignName);
	    /**保存结构化病例，插入EMR_FILE_INDEX end*/
		
//		System.out.println("OPBOOK_SEQ:"+parmsql.getCount("OPBOOK_SEQ"));
		if(!this.isBlank(parm.getValue("CHECK_NS_CODE"))){
			if(parmsql.getCount("OPBOOK_SEQ")>0){
//				System.out.println("ALLERGIC_MARK:"+parm.getValue("ALLERGIC_MARK"));
				/**更新OPE_CHECK表*/
				String sqlUpdate = "UPDATE OPE_CHECK SET "
						+ " TYPE_CODE = '"+parm.getValue("TYPE_CODE")+"', "
						+ " OPERATION_ICD = '"+parm.getValue("OPERATION_ICD")+"', "
						+ " OPT_CHN_DESC = '"+parm.getValue("OPT_CHN_DESC")+"', "
						+ " ALLERGIC_FLG = '"+parm.getValue("ALLERGIC_FLG")+"', "
						+ " READY_FLG = '"+parm.getValue("READY_FLG")+"', "
						+ " VALID_DATE_FLG = '"+parm.getValue("VALID_DATE_FLG")+"', "
						+ " SPECIFICATION_FLG = '"+parm.getValue("SPECIFICATION_FLG")+"', "
						+ " ANA_USER = '"+parm.getValue("ANA_USER1")+"', "
						+ " CHECK_DR_CODE = '"+parm.getValue("CHECK_DR_CODE")+"', "
						+ " CHECK_NS_CODE = '"+parm.getValue("CHECK_NS_CODE")+"', "
						+ " CHECK_DATE = TO_DATE('"+parm.getValue("CHECK_DATE")+"','YYYYMMDDHH24MISS'), "
						+ " ALLERGIC_MARK = '"+parm.getValue("ALLERGIC_MARK")+"', "
						+ " OPT_USER = '"+parm.getValue("OPT_USER")+"', "
						+ " OPT_TERM = '"+parm.getValue("OPT_TERM")+"', "
						+ " OPT_DATE = TO_DATE('"+parm.getValue("CHECK_DATE")+"','YYYYMMDDHH24MISS') "
						+ " WHERE CHECK_NO = '"+parmsql.getValue("CHECK_NO",0)+"'";
//				System.out.println("sqlUpdate:"+sqlUpdate);
				result = new TParm(TJDODBTool.getInstance().update(sqlUpdate, conn)); 
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					conn.rollback();
					conn.close();
					return result;
				}
				/** 介入安全核查  绑定手术单号 */
//				System.out.println("parm.getValue:"+parm.getValue("ROOM_NO)"));
				if (StringUtils.isNotEmpty(parm.getValue("OPE_SAVE_CHECK"))){
					if("Y".equals(parm.getValue("OPE_SAVE_CHECK"))){
						String sqlRoom = "UPDATE OPE_IPROOM SET "
		        				+ " OPBOOK_SEQ = '"+parm.getValue("OPBOOK_SEQ")+"', "
		        				+ " OPT_USER = '"+parm.getValue("OPT_USER")+"',"
		        				+ " OPT_DATE = SYSDATE, "
		        				+ " OPT_TERM = '"+parm.getValue("OPT_TERM")+"' "
		        				+ " WHERE IP ='"+parm.getValue("OPT_TERM")+"'";
//					System.out.println("sqlRoom:"+sqlRoom);
						result = new TParm(TJDODBTool.getInstance().update(sqlRoom, conn)); 
						if(result.getErrCode()<0){
							err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
							conn.rollback();
							conn.close();
							return result;
						}
					}
				}
				/*
				String sql1 = "UPDATE INW_TRANSFERSHEET_WO SET "
						+ " ALLERGIC_FLG = '"+parm.getValue("ALLERGIC_FLG")+"', "
						+ " ALLERGIC_MARK = '"+parm.getValue("ALLERGIC_MARK")+"' "
						+ " WHERE TRANSFER_CODE = '"+parm.getValue("TRANSFER_CODE")+"' ";
				result = new TParm(TJDODBTool.getInstance().update(sql1, conn)); 
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					conn.rollback();
					conn.close();
					return result;
				}
				*/
				/**更新EMR_FILE_INDEX表*/
				String sqlEmr = "UPDATE EMR_FILE_INDEX SET "
								+ " OPT_USER = '"+parm.getValue("OPT_USER")+"', "
								+ " OPT_DATE =TO_DATE('"+parm.getValue("CHECK_DATE")+"','YYYYMMDDHH24MISS'), "
								+ " OPT_TERM = '"+parm.getValue("OPT_TERM")+"', "
								+ " CANPRINT_FLG = 'Y', "
								+ " MODIFY_FLG = 'Y', "
								+ " CURRENT_USER = '"+parm.getValue("OPT_USER")+"' "
								+ " WHERE CASE_NO = '"+parm.getValue("CASE_NO")+"' AND OPBOOK_SEQ = '"+parm.getValue("OPBOOK_SEQ")+"' AND CLASS_CODE = 'EMR0604' AND SUBCLASS_CODE = 'EMR0604022'";
//				System.out.println("sqlEmr:"+sqlEmr);
				result = new TParm(TJDODBTool.getInstance().update(sqlEmr, conn)); 
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					conn.rollback();
					conn.close();
					return result;
				}
				/** transferUser 核查人*/
				if (!this.isBlank(parm.getValue("CHECK_NS_CODE"))) {  
					String sqlOpebook = "UPDATE OPE_OPBOOK SET TIMEOUT_USER = '"+parm.getValue("CHECK_NS_CODE")+"', TIMEOUT_DATE = TO_DATE('"+ parm.getValue("DSTR") +"', 'yy-mm-dd hh24:mi:ss') WHERE OPBOOK_SEQ='"+parm.getValue("OPBOOK_SEQ")+"'";
//					System.out.println("sqlOpebook:"+sqlOpebook);
					result = new TParm(TJDODBTool.getInstance().update(sqlOpebook, conn)); 
					if(result.getErrCode()<0){
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						conn.rollback();
						conn.close();
						return result;
					}
				}
			}else {
				/**插入OPE_CHECK表*/
				String sqlInsert = "INSERT INTO OPE_CHECK "
						+ "(CHECK_NO,MR_NO,PAT_NAME,SEX,BIRTH_DATE,OPBOOK_SEQ,TYPE_CODE," +  
						"OPERATION_ICD,OPT_CHN_DESC,ALLERGIC_FLG, READY_FLG,VALID_DATE_FLG," +
						"SPECIFICATION_FLG,ANA_USER,CHECK_DR_CODE,CHECK_NS_CODE, CHECK_DATE,ALLERGIC_MARK,OPT_USER,OPT_TERM,OPT_DATE)" +  
						" VALUES ('"+parm.getValue("CHECK_NO")+"',"
						+ "'"+parm.getValue("MR_NO")+"'," 
						+ "'"+parm.getValue("PAT_NAME")+"'," 
						+ "'"+parm.getValue("SEX_CODE")+"'," 
						+ "TO_DATE('"+parm.getValue("BIRTH_DATE")+"','YYYY/MM/DD'),"
						+ "'"+parm.getValue("OPBOOK_SEQ")+"',"   
						+ "'"+parm.getValue("TYPE_CODE")+"',"  
						+ "'"+parm.getValue("OPERATION_ICD")+"',"    
						+ "'"+parm.getValue("OPT_CHN_DESC")+"',"     
						+ "'"+parm.getValue("ALLERGIC_FLG")+"'," 
						+ "'"+parm.getValue("READY_FLG")+"',"   
						+ "'"+parm.getValue("VALID_DATE_FLG")+"'," 
						+ "'"+parm.getValue("SPECIFICATION_FLG")+"'," 
						+ "'"+parm.getValue("ANA_USER1")+"'," 
						+ "'"+parm.getValue("CHECK_DR_CODE")+"'," 
						+ "'"+parm.getValue("CHECK_NS_CODE")+"'," 
						+ "TO_DATE('"+parm.getValue("CHECK_DATE")+"','YYYYMMDDHH24MISS'),"
						+ "'"+parm.getValue("ALLERGIC_MARK")+"',"
						+ "'"+parm.getValue("OPT_USER")+"',"
						+ "'"+parm.getValue("OPT_TERM")+"'," 
						+ "TO_DATE('"+parm.getValue("CHECK_DATE")+"','YYYYMMDDHH24MISS'))";
//				System.out.println("sqlInsert:"+sqlInsert);
				result = new TParm(TJDODBTool.getInstance().update(sqlInsert, conn)); 
				if(result.getErrCode()<0){
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					conn.rollback();
					conn.close();
					return result;
				}
				/*
				String sql1 = "UPDATE INW_TRANSFERSHEET_WO SET "
						+ " ALLERGIC_FLG = '"+parm.getValue("ALLERGIC_FLG")+"', "
						+ " ALLERGIC_MARK = '"+parm.getValue("ALLERGIC_MARK")+"' "
						+ " WHERE TRANSFER_CODE = '"+parm.getValue("TRANSFER_CODE")+"' ";
				result = new TParm(TJDODBTool.getInstance().update(sql1, conn)); 
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					conn.rollback();
					conn.close();
					return result;
				}
				*/
				/** 介入安全核查  绑定手术单号 */
//				System.out.println("parm.getValue:"+parm.getValue("ROOM_NO)"));
				if (StringUtils.isNotEmpty(parm.getValue("OPE_SAVE_CHECK"))){
					if("Y".equals(parm.getValue("OPE_SAVE_CHECK"))){
						String sqlRoom = "UPDATE OPE_IPROOM SET "
		        				+ " OPBOOK_SEQ = '"+parm.getValue("OPBOOK_SEQ")+"', "
		        				+ " OPT_USER = '"+parm.getValue("OPT_USER")+"',"
		        				+ " OPT_DATE = SYSDATE, "
		        				+ " OPT_TERM = '"+parm.getValue("OPT_TERM")+"' "
		        				+ " WHERE IP ='"+parm.getValue("OPT_TERM")+"'";
//					System.out.println("sqlRoom:"+sqlRoom);
						result = new TParm(TJDODBTool.getInstance().update(sqlRoom, conn)); 
						if(result.getErrCode()<0){
							err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
							conn.rollback();
							conn.close();
							return result;
						}
					}
				}
				/**保存结构化病例，插入EMR_FILE_INDEX*/
			    if (!this.isBlank(parm.getValue("CHECK_NS_CODE"))) {
			    	String sql = "INSERT INTO EMR_FILE_INDEX "
			    			+ "(CASE_NO, FILE_SEQ, MR_NO, IPD_NO, FILE_PATH,FILE_NAME, DESIGN_NAME, CLASS_CODE, SUBCLASS_CODE, "
			    			+ "DISPOSAC_FLG,OPT_USER, OPT_DATE, OPT_TERM,CREATOR_USER,CREATOR_DATE,REPORT_FLG,OPBOOK_SEQ) "
			    			+ " VALUES "
			    			+" ('"+parm.getValue("CASE_NO")+"', "
			    			+ "'"+indexStr+"', "
			    			+ "'"+parm.getValue("MR_NO")+"', "
			    			+ "'', "
			    			+ "'"+filePath+"', "
			    			+ "'"+fileName+"', "
			    			+ "'"+gesignName+"', "
			    			+ "'EMR0604', "
			    			+ "'EMR0604022', "
			    			+ "'N',"
			    			+ " '"+parm.getValue("OPT_USER")+"', "
			    			+ "SYSDATE, "
			    			+ "'"+parm.getValue("OPT_TERM")+"', "
			    			+ "'"+parm.getValue("OPT_USER")+"', "
			    			+ "SYSDATE, "
			    			+ "'N',"
			    			+ "'"+parm.getValue("OPBOOK_SEQ")+"')";
//			    	System.out.println("sql:"+sql);
			    	result = new TParm(TJDODBTool.getInstance().update(sql, conn)); 
			    	if(result.getErrCode()<0){
			    		err("ERR:" + result.getErrCode() + result.getErrText()
			    				+ result.getErrName());
			    		conn.rollback();
			    		conn.close();
			    		return result;
			    	}
			    }
			    /** transferUser 核查人*/
				if (!this.isBlank(parm.getValue("CHECK_NS_CODE"))) {  
					String sqlOpebook = "UPDATE OPE_OPBOOK SET TIMEOUT_USER = '"+parm.getValue("CHECK_NS_CODE")+"', TIMEOUT_DATE = TO_DATE('"+ parm.getValue("DSTR") +"', 'yy-mm-dd hh24:mi:ss') WHERE OPBOOK_SEQ='"+parm.getValue("OPBOOK_SEQ")+"'";
//					System.out.println("sqlOpebook:"+sqlOpebook);
					result = new TParm(TJDODBTool.getInstance().update(sqlOpebook, conn)); 
					if(result.getErrCode()<0){
						err("ERR:" + result.getErrCode() + result.getErrText()
								+ result.getErrName());
						conn.rollback();
						conn.close();
						return result;
					}
				}
				result.setData("PATH",filePath);
		        result.setData("FILENAME",fileName);
			}
		}
		conn.commit();
//		System.out.println("result:"+result);
		conn.close();
		return result;
	}
	
	//手术交接保存
	public TParm onSaveOpeConnect(TParm parm){
		TConnection conn = getConnection();
		TParm result = new TParm();
//		TParm countParm = new TParm(this.getDBTool().select("SELECT FILE_PATH,FILE_NAME,SUBCLASS_CODE FROM EMR_FILE_INDEX WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND CLASS_CODE='EMR0603' AND SUBCLASS_CODE='EMR0603081' AND OPBOOK_SEQ = '"+parm.getValue("OPBOOK_SEQ")+"'"));
		/**保存结构化病例，插入EMR_FILE_INDEX start*/
		TParm action = new TParm(this.getDBTool().select("SELECT NVL(MAX(FILE_SEQ)+1,1) AS MAXFILENO FROM EMR_FILE_INDEX WHERE CASE_NO='"+parm.getValue("CASE_NO")+"'"));
		String indexStr = "01";
		int index = action.getInt("MAXFILENO",0);
		if(index<10){
           indexStr = "0"+index;
        }else{
            indexStr = ""+index;
        }
//		String filePath = "JHW"+"\\"+parm.getValue("CASE_NO").substring(2,4)+"\\"+parm.getValue("CASE_NO").substring(4,6)+"\\"+parm.getValue("CASE_NO");// modify by wangjc 20180205 路径按年月+MR_NO
		String filePath = "JHW"+"\\"+parm.getValue("CASE_NO").substring(2,4)+"\\"+parm.getValue("CASE_NO").substring(4,6)+"\\"+parm.getValue("CASE_NO");
		String fileName = parm.getValue("CASE_NO")+"_"+"手术交接单"+"_"+indexStr;
		String gesignName = "手术交接单"+"("+parm.getValue("DSTR")+")";
//		System.out.println("gesignName:"+gesignName);
	    /**保存结构化病例，插入EMR_FILE_INDEX end*/
		String sqlOpbook = "UPDATE OPE_OPBOOK SET "
				+ " HANDOVER_USER = '"+parm.getValue("HANDOVER_USER")+"', "
				+ " HANDOVER_DATE = TO_DATE('"+parm.getValue("TRANSFER_DATE")+"','YYYYMMDDHH24MISS'),"
				+ " TRANSFER_USER = '"+parm.getValue("TRANSFER_USER")+"',"
				+ " TRANSFER_DATE = TO_DATE('"+parm.getValue("TRANSFER_DATE")+"','YYYYMMDDHH24MISS'),"
				+ " OPBOOK_SEQ = '"+parm.getValue("OPBOOK_SEQ")+"' "
				+ " WHERE OPBOOK_SEQ= '"+parm.getValue("OPBOOK_SEQ")+"'";
		result = new TParm(TJDODBTool.getInstance().update(sqlOpbook, conn)); 
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
//		String sqlInw = "UPDATE INW_TRANSFERSHEET SET "
//				+ " STATUS_FLG= '5',"
//				+ " FROM_USER= '"+parm.getValue("HANDOVER_USER")+"',"
//				+ " TO_USER= '"+parm.getValue("TRANSFER_USER")+"',"
//				+ " TRANSFER_DATE= TO_DATE('"+parm.getValue("TRANSFER_DATE")+"','YYYYMMDDHH24MISS') "
//				+ " WHERE TRANSFER_CLASS= 'WO' AND STATUS_FLG = '4' "
//				+ " AND CRE_DATE BETWEEN TO_DATE('"+parm.getValue("dstrYMD")+"00:00:00','yy-mm-dd hh24:mi:ss') "
//				+ " AND TO_DATE('"+parm.getValue("dstrYMD")+"23:59:59','yy-mm-dd hh24:mi:ss') "
//				+ " AND MR_NO = '"+parm.getValue("MR_NO")+"'";
		String sqlInw = "UPDATE INW_TRANSFERSHEET SET "
				+ " TRANSFER_FILE_PATH = '"+filePath+"', "
				+ " TRANSFER_FILE_NAME = '"+fileName+"', "
				+ " STATUS_FLG= '5',"
				+ " FROM_USER= '"+parm.getValue("HANDOVER_USER")+"',"
				+ " TO_USER= '"+parm.getValue("TRANSFER_USER")+"',"
				+ " TRANSFER_DATE= TO_DATE('"+parm.getValue("TRANSFER_DATE")+"','YYYYMMDDHH24MISS') "
				+ " WHERE TRANSFER_CLASS = 'WO' AND STATUS_FLG = '4' "
				+ " AND TRANSFER_CODE = '"+parm.getValue("TRANSFER_CODE")+"'";
		result = new TParm(TJDODBTool.getInstance().update(sqlInw, conn)); 
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		
		String sqlRoom = "UPDATE OPE_IPROOM SET "
				+ " OPBOOK_SEQ= '"+parm.getValue("OPBOOK_SEQ")+"',"
				+ " OPT_USER = '"+parm.getValue("OPT_USER")+"',"
				+ " OPT_TERM = '"+parm.getValue("OPT_TERM")+"',"
				+ " OPT_DATE = SYSDATE "
				+ " WHERE ROOM_NO= '"+parm.getValue("ROOM_NO")+"'";
		result = new TParm(TJDODBTool.getInstance().update(sqlRoom, conn)); 
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			conn.rollback();
			conn.close();
			return result;
		}
		/**保存结构化病例，插入EMR_FILE_INDEX*/
//		if(countParm.getCount()<0){
		if (!this.isBlank(parm.getValue("HANDOVER_USER")) && !this.isBlank(parm.getValue("TRANSFER_USER"))) {
	    	String sqlFile = "INSERT INTO EMR_FILE_INDEX "
	    			+ "(CASE_NO, FILE_SEQ, MR_NO, IPD_NO, FILE_PATH,FILE_NAME, DESIGN_NAME, CLASS_CODE, SUBCLASS_CODE, "
	    			+ "DISPOSAC_FLG,OPT_USER, OPT_DATE, OPT_TERM,CREATOR_USER,CREATOR_DATE,REPORT_FLG,OPBOOK_SEQ) "
	    			+ " VALUES "
	    			+" ('"+parm.getValue("CASE_NO")+"', "
	    			+ "'"+indexStr+"', "
	    			+ "'"+parm.getValue("MR_NO")+"', "
	    			+ "'', "
	    			+ "'"+filePath+"', "
	    			+ "'"+fileName+"', "
	    			+ "'"+gesignName+"', "
	    			+ "'EMR0603', "
	    			+ "'EMR0603081', "
	    			+ "'N',"
	    			+ " '"+parm.getValue("OPT_USER")+"', "
	    			+ "SYSDATE, "
	    			+ "'"+parm.getValue("OPT_TERM")+"', "
	    			+ "'"+parm.getValue("OPT_USER")+"', "
	    			+ "TO_DATE('"+parm.getValue("TRANSFER_DATE")+"','YYYYMMDDHH24MISS'), "
	    			+ "'N',"
	    			+ "'"+parm.getValue("OPBOOK_SEQ")+"')";
//	    	System.out.println("sql:"+sqlFile);
	    	result = new TParm(TJDODBTool.getInstance().update(sqlFile, conn)); 
	    	if(result.getErrCode()<0){
	    		err("ERR:" + result.getErrCode() + result.getErrText()
	    				+ result.getErrName());
	    		conn.rollback();
	    		conn.close();
	    		return result;
	    	}
	    	
	    }
//		} else {
//			/**更新EMR_FILE_INDEX表*/
//			String sqlEmr = "UPDATE EMR_FILE_INDEX SET "
//							+ " OPT_USER = '"+parm.getValue("OPT_USER")+"', "
//							+ " OPT_DATE =TO_DATE('"+parm.getValue("TRANSFER_DATE")+"','YYYYMMDDHH24MISS'), "
//							+ " OPT_TERM = '"+parm.getValue("OPT_TERM")+"', "
//							+ " CANPRINT_FLG = 'Y', "
//							+ " MODIFY_FLG = 'Y', "
//							+ " CURRENT_USER = '"+parm.getValue("OPT_USER")+"' "
//							+ " WHERE CASE_NO = '"+parm.getValue("CASE_NO")+"' AND OPBOOK_SEQ = '"+parm.getValue("OPBOOK_SEQ")+"' AND CLASS_CODE = 'EMR0603' AND SUBCLASS_CODE = 'EMR0603081'";
////			System.out.println("sqlEmr:"+sqlEmr);
//			result = new TParm(TJDODBTool.getInstance().update(sqlEmr, conn)); 
//			if (result.getErrCode() < 0) {
//				err("ERR:" + result.getErrCode() + result.getErrText()
//						+ result.getErrName());
//				conn.rollback();
//				conn.close();
//				return result;
//			}
//		}
		result.setData("PATH",filePath);
        result.setData("FILENAME",fileName);
		conn.commit();
		conn.close();
		return result;
	}
	/** 校验是否为空 */
	private boolean isBlank(String str) {
		if (null == str || "".equals(str.trim())) {
			return true;
		} else {
			return false;
		}
	}
	/**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
    
    
    public TParm onQueryPDABindingIP(TParm parm) {
        TParm result = new TParm();
        result = PDATool.getInstance().onQueryPDABindingIP(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    public TParm onUpdatePDABindingIP(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = PDATool.getInstance().onUpdatePDABindingIP(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    public TParm onInsertPDABindingIP(TParm parm) {
    	TConnection conn = getConnection();
    	TParm result = new TParm();
    	result = PDATool.getInstance().onInsertPDABindingIP(parm, conn);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText()
    		+ result.getErrName());
    		conn.close();
    		return result;
    	}
    	conn.commit();
    	conn.close();
    	return result;
    }
    
    public TParm onRemovePDABindingIP(TParm parm) {
    	TConnection conn = getConnection();
    	TParm result = new TParm();
    	result = PDATool.getInstance().onRemovePDABindingIP(parm, conn);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText()
    		+ result.getErrName());
    		conn.close();
    		return result;
    	}
    	conn.commit();
    	conn.close();
    	return result;
    }
    
    public TParm onDeletePDABindingIP(TParm parm) {
    	TConnection conn = getConnection();
    	TParm result = new TParm();
    	result = PDATool.getInstance().onDeletePDABindingIP(parm, conn);
    	if (result.getErrCode() < 0) {
    		err("ERR:" + result.getErrCode() + result.getErrText()
    		+ result.getErrName());
    		conn.close();
    		return result;
    	}
    	conn.commit();
    	conn.close();
    	return result;
    }
}
                                                  