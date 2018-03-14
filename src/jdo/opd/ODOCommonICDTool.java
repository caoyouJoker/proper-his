package jdo.opd;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;
/**
*
* <p>Title: 门诊医生工作站常用诊断</p>
*
* <p>Description:门诊医生工作站常用诊断</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company:Javahis </p>
*
* @author ehui 200800904
* @version 1.0
*/
public class ODOCommonICDTool {
	TJDODBTool dbTool;
	/*
	 * DEPT_OR_DR     VARCHAR2(1 BYTE)               DEFAULT '1'                   NOT NULL,
  DEPTORDR_CODE  VARCHAR2(20 BYTE)              NOT NULL,
  ICD_TYPE       VARCHAR2(1 BYTE)               DEFAULT 'W'                   NOT NULL,
  ICD_CODE       VARCHAR2(20 BYTE)              NOT NULL,
  SEQ            NUMBER(3),
  SPCFYDEPT      VARCHAR2(20 BYTE),
  OPT_USER       VARCHAR2(20 BYTE)              NOT NULL,
  OPT_DATE       DATE                           NOT NULL,
  OPT_TERM       VARCHAR2(20 BYTE)              NOT NULL
	 */

	public TJDODBTool getDbTool(){
		return dbTool.getInstance();
	}
	public TParm getFormLoadParm(String deptOrDr,String code,String icdType){
		//System.out.println("here");
		
		
		
		//System.out.println("fuck");
		TParm result=new TParm();
		if(StringUtil.isNullString(deptOrDr)||StringUtil.isNullString(code)){
			//System.out.println("null");
			return result;
		}
			
		StringBuffer sql=new StringBuffer();
		sql.append(" AND A.DEPT_OR_DR='").append(deptOrDr).append("' AND A.DEPTORDR_CODE='").append(code).append("' AND A.ICD_TYPE='"+icdType+"' ");
		//System.out.println("commonICD------------->"+sql.toString());
		result=new TParm(getDbTool().select(sql.toString()));
		return result;
	}
}
