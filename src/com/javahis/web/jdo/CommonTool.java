package com.javahis.web.jdo;

import com.dongyang.Service.Server;
import com.dongyang.config.TConfigParm;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TDialog;
import com.javahis.web.form.SysEmrIndexForm;
import com.javahis.web.util.CommonUtil;

public class CommonTool {
	public CommonTool() {
		Server.autoInit(this);
	}
	/**
	 * ´ó×ÖµäSYS_DICTIONARY£¨ÈçÃÅ¼±×¡±ð£©
	 */
	public TParm getDictionaryList(String groupId) {
		String sql = " SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID='"
			+ groupId + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// ÅÐ¶Ï´íÎóÖµ
        if (result.getErrCode() < 0) {
            System.out.println("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ¾ÍÕï¿ÆÊÒ
	 */
	public TParm getDeptList() {
		String sql = " SELECT * FROM SYS_DEPT ORDER BY DEPT_CODE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// ÅÐ¶Ï´íÎóÖµ
        if (result.getErrCode() < 0) {
            System.out.println("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	/**
	 * ¾ÍÕïÖ÷Ë÷Òý
	 */
	public TParm getEmrIndexParm(SysEmrIndexForm form) {
		String sql = " SELECT SEI.CASE_NO, SEI.MR_NO, SEI.IPD_NO, SP.PAT_NAME,";
		sql += " TO_CHAR(SEI.ADM_DATE, 'YYYY/MM/DD') AS ADM_DATE, SA.CHN_DESC AS ADM_TYPE, SD.DEPT_CHN_DESC AS DEPT_CODE";
		sql += " FROM SYS_EMR_INDEX SEI, SYS_DEPT SD,";
		if (CommonUtil.checkInputString(form.getMr_no())) {
			sql += " (SELECT * FROM SYS_PATINFO WHERE MR_NO = '" + form.getMr_no() + "' OR MERGE_TOMRNO = '" + form.getMr_no() + "') SP,";
		} else {
			sql += " SYS_PATINFO SP,";
		}
		sql += " (SELECT ID, CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_ADMTYPE') SA";
		sql += " WHERE SEI.MR_NO = SP.MR_NO ";
		sql += " AND SEI.DEPT_CODE = SD.DEPT_CODE ";
		sql += " AND SEI.ADM_TYPE = SA.ID ";
		if(CommonUtil.checkInputString(form.getAdm_date_begin())) {
			sql += " AND SEI.ADM_DATE >= TO_DATE('" + form.getAdm_date_begin() + "', 'YYYY-MM-DD HH24:MI:SS')";
		}
		if(CommonUtil.checkInputString(form.getAdm_date_end())) {
			sql += " AND SEI.ADM_DATE <= TO_DATE('" + form.getAdm_date_end() + "', 'YYYY-MM-DD HH24:MI:SS')";
		}
		sql += " AND ( 1!=1";
		if (CommonUtil.checkInputString(form.getAdm_type_0())) {
			sql += " OR SEI.ADM_TYPE = '" + form.getAdm_type_0() + "'";
		}
		if (CommonUtil.checkInputString(form.getAdm_type_1())) {
			sql += " OR SEI.ADM_TYPE = '" + form.getAdm_type_1() + "'";
		}
		if (CommonUtil.checkInputString(form.getAdm_type_2())) {
			sql += " OR SEI.ADM_TYPE = '" + form.getAdm_type_2() + "'";
		}
		if (CommonUtil.checkInputString(form.getAdm_type_3())) {
			sql += " OR SEI.ADM_TYPE = '" + form.getAdm_type_3() + "'";
		}
		sql += " )";
		if(CommonUtil.checkInputString(form.getDept_code())) {
			sql += " AND SEI.DEPT_CODE = '" + form.getDept_code() + "'";
		}
		if (!CommonUtil.checkInputString(form.getSort1())
				|| !CommonUtil.checkInputString(form.getSort2())
				||!CommonUtil.checkInputString(form.getSort3())) {
			sql += " ORDER BY SEI.ADM_DATE, SEI.ADM_TYPE, SEI.DEPT_CODE";
		} else {
			sql += " ORDER BY";
			sql += " SEI." + form.getSort1();
			sql += " ,SEI." + form.getSort2();
			sql += " ,SEI." + form.getSort3();
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// ÅÐ¶Ï´íÎóÖµ
        if (result.getErrCode() < 0) {
            System.out.println("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * ¸ù¾Ý²¡°¸ºÅ²éÑ¯²¡»¼ÐÅÏ¢
	 * @param form
	 * @return
	 */
	public TParm getPatInfoByMrNo(SysEmrIndexForm form) {
		String sql = " SELECT * FROM SYS_PATINFO WHERE 1 = 1";
		if (CommonUtil.checkInputString(form.getMr_no())) {
			sql += " AND MR_NO = '" + form.getMr_no() + "'";
		} else {
			sql += " AND 1 = 2";
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// ÅÐ¶Ï´íÎóÖµ
        if (result.getErrCode() < 0) {
            System.out.println("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	

    public TParm getEmrClassParm(SysEmrIndexForm form) {
    	if(!CommonUtil.checkInputString(form.getCase_no())) {
    		return new TParm();
    	}
    	String sql = " SELECT * FROM EMR_CLASS WHERE PARENT_CLASS_CODE IS NOT NULL ORDER BY CLASS_CODE, SEQ";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        // ÅÐ¶Ï´íÎóÖµ
        if (result.getErrCode() < 0) {
            System.out.println("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    public boolean checkAdmType(SysEmrIndexForm form, String admType) {
    	if(admType.equals(form.getAdm_type_0())) {
    		return true;
    	}
    	if(admType.equals(form.getAdm_type_1())) {
    		return true;
    	}
    	if(admType.equals(form.getAdm_type_2())) {
    		return true;
    	}
    	if(admType.equals(form.getAdm_type_3())) {
    		return true;
    	}
    	return false;
    }

    public TParm getEmrTempletFileIndexParm(String classCode, SysEmrIndexForm form) {
//        String sql = "SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.IPD_NO,A.FILE_PATH,A.FILE_NAME,A.DESIGN_NAME,A.CLASS_CODE,A.SUBCLASS_CODE,A.DISPOSAC_FLG,A.CREATOR_USER,A.CURRENT_USER,A.CANPRINT_FLG,A.MODIFY_FLG,B.SUBCLASS_DESC,B.DEPT_CODE,B.RUN_PROGARM,B.SUBTEMPLET_CODE,B.CLASS_STYLE,B.OIDR_FLG,B.NSS_FLG "
//        	+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B"
//        	+ " WHERE A.CASE_NO='" + form.getCase_no()+"' AND A.CLASS_CODE='" + classCode + "' AND A.DISPOSAC_FLG<>'Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE ";
      String sql = "SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.IPD_NO,A.FILE_PATH,A.FILE_NAME,A.DESIGN_NAME,A.CLASS_CODE,A.SUBCLASS_CODE,A.DISPOSAC_FLG,A.CREATOR_USER,A.CURRENT_USER,A.CANPRINT_FLG,A.MODIFY_FLG,B.SUBCLASS_DESC,B.DEPT_CODE,B.RUN_PROGARM,B.SUBTEMPLET_CODE,B.CLASS_STYLE,B.OIDR_FLG,B.NSS_FLG "
    	+ " FROM EMR_FILE_INDEX A INNER JOIN EMR_TEMPLET B ON A.SUBCLASS_CODE=B.SUBCLASS_CODE"
    	+ " WHERE A.CASE_NO='" + form.getCase_no()+"' AND A.CLASS_CODE='" + classCode + "' AND A.DISPOSAC_FLG<>'Y'  ";
    	sql += " AND ( 1!=1";
		if (checkAdmType(form, "O")) {
			sql += " OR B.OPD_FLG='Y'";
		}
		if (checkAdmType(form, "I")) {
			sql += " OR B.IPD_FLG='Y'";
		}
		if (checkAdmType(form, "E")) {
			sql += " OR B.EMG_FLG='Y'";
		}
		if (checkAdmType(form, "H")) {
			sql += " OR B.HRM_FLG='Y'";
		}
		sql += " )";
		sql += " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	// ÅÐ¶Ï´íÎóÖµ
        if (result.getErrCode() < 0) {
            System.out.println("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    public TParm getEmrFileIndexRootAttributes() {
    	String sql = " SELECT * FROM EMR_CLASS WHERE PARENT_CLASS_CODE IS NULL";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        // ÅÐ¶Ï´íÎóÖµ
        if (result.getErrCode() < 0) {
            System.out.println("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    public TParm getEmrFileIndexObject(TParm parm) {
    	String sql = " SELECT A.*, B.TEMPLET_PATH, B.EMT_FILENAME FROM EMR_FILE_INDEX A, EMR_TEMPLET B";
    	sql += " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE(+)";
    	sql += " AND A.CASE_NO = '" + parm.getValue("CASE_NO") + "'";
    	sql += " AND A.FILE_SEQ = '" + parm.getValue("FILE_SEQ") + "'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	// ÅÐ¶Ï´íÎóÖµ
        if (result.getErrCode() < 0) {
            System.out.println("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    public TParm queryEmrTempletFileIndexParm(SysEmrIndexForm form) {
//      String sql = "SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.IPD_NO,A.FILE_PATH,A.FILE_NAME,A.DESIGN_NAME,A.CLASS_CODE,A.SUBCLASS_CODE,A.DISPOSAC_FLG,A.CREATOR_USER,A.CURRENT_USER,A.CANPRINT_FLG,A.MODIFY_FLG,B.SUBCLASS_DESC,B.DEPT_CODE,B.RUN_PROGARM,B.SUBTEMPLET_CODE,B.CLASS_STYLE,B.OIDR_FLG,B.NSS_FLG "
//      	+ " FROM EMR_FILE_INDEX A,EMR_TEMPLET B"
//      	+ " WHERE A.CASE_NO='" + form.getCase_no()+"' AND A.CLASS_CODE='" + classCode + "' AND A.DISPOSAC_FLG<>'Y' AND A.SUBCLASS_CODE=B.SUBCLASS_CODE ";
    String sql = "SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.IPD_NO,A.FILE_PATH,A.FILE_NAME,A.DESIGN_NAME,A.CLASS_CODE,A.SUBCLASS_CODE,A.DISPOSAC_FLG,A.CREATOR_USER,A.CURRENT_USER,A.CANPRINT_FLG,A.MODIFY_FLG,B.SUBCLASS_DESC,B.DEPT_CODE,B.RUN_PROGARM,B.SUBTEMPLET_CODE,B.CLASS_STYLE,B.OIDR_FLG,B.NSS_FLG "
  	+ " FROM EMR_FILE_INDEX A INNER JOIN EMR_TEMPLET B ON A.SUBCLASS_CODE=B.SUBCLASS_CODE"
  	+ " WHERE A.CASE_NO='" + form.getCase_no()+"' AND A.DISPOSAC_FLG<>'Y'  ";
  	sql += " AND ( 1!=1";
		if (checkAdmType(form, "O")) {
			sql += " OR B.OPD_FLG='Y'";
		}
		if (checkAdmType(form, "I")) {
			sql += " OR B.IPD_FLG='Y'";
		}
		if (checkAdmType(form, "E")) {
			sql += " OR B.EMG_FLG='Y'";
		}
		if (checkAdmType(form, "H")) {
			sql += " OR B.HRM_FLG='Y'";
		}
		sql += " )";
		sql += " ORDER BY A.CLASS_CODE,A.SUBCLASS_CODE,A.FILE_SEQ";
  	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
  	// ÅÐ¶Ï´íÎóÖµ
      if (result.getErrCode() < 0) {
          System.out.println("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
  }
    
    
}
