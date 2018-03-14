package jdo.med;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

public class MedToLedTool extends TJDODBTool{
    /**
     * 实例
     */
    public static MedToLedTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static MedToLedTool getInstance() {
        if (instanceObject == null)
            instanceObject = new MedToLedTool();
        return instanceObject;
    }
    public TParm queryPatInfo(String mrNo){
    	StringBuffer sqlbf = new StringBuffer();
    	sqlbf.append("SELECT A.MR_NO,A.PAT_NAME,A.SEX_CODE,TO_CHAR(A.BIRTH_DATE,'YYYY/MM/DD') AS BIRTH_DATE,B.CHN_DESC AS SEX_DESC,to_number(TO_CHAR(sysdate,'YYYY'))-to_number(TO_CHAR(A.BIRTH_DATE,'YYYY')) AS AGE ");
    	sqlbf.append(" FROM SYS_PATINFO A ,SYS_DICTIONARY B ");
    	sqlbf.append("  WHERE A.SEX_CODE=B.ID(+) AND B.GROUP_ID='SYS_SEX' ");
    	sqlbf.append(" AND A.MR_NO='"+mrNo+"' ");
    	//System.out.println("执行sql："+sqlbf.toString());
        TParm result = new TParm(this.select(sqlbf.toString()));
        return result;
    }
    public TParm queryOrderDetail(String mrNo,String sAdmType,String caseNo){
    	StringBuffer sqlbf = new StringBuffer();//REGCAN_DATE  ARRIVE_FLG 
    	if("I".equals(sAdmType)){
    		String caseNoSql = "SELECT CASE_NO FROM ADM_INP WHERE MR_NO = '"+mrNo+"' AND DS_DATE IS NULL";
    		TParm caseNoParm = new TParm(TJDODBTool.getInstance().select(caseNoSql));
    		String odiSql = "SELECT 'Y' AS CHK, B.ORDER_DESC, "
    				+ " CASE WHEN C.NS_EXEC_CODE IS NULL THEN 'N' "
    				+ " WHEN C.NS_EXEC_CODE IS NOT NULL THEN 'Y' END AS BILL_FLG,"
    				+ " TO_CHAR (A.ORDER_DATE, 'YYYYMMDDHH24MISS') AS ORDER_DATE,"
    				+ " A.ORDER_CODE, A.ORDERSET_GROUP_NO, A.CASE_NO "
    				+ " FROM ODI_ORDER A, SYS_FEE B, ODI_DSPNM C "
    				+ " WHERE     A.ORDER_CODE = B.ORDER_CODE "
    				+ " AND A.HIDE_FLG = 'N' "
    				+ " AND A.CAT1_TYPE = 'RIS' "
    				+ " AND A.CASE_NO = '"+caseNoParm.getValue("CASE_NO", 0)+"' "
    				+ " AND A.CASE_NO = C.CASE_NO "
    				+ " AND A.ORDER_NO = C.ORDER_NO "
    				+ " AND A.ORDER_SEQ = C.ORDER_SEQ ";
    		sqlbf.append(odiSql);
    	}else{
    		sqlbf.append(" SELECT 'Y' AS CHK,B.ORDER_DESC,A.BILL_FLG,to_char(A.ORDER_DATE,'YYYYMMDDHH24MISS') AS ORDER_DATE,A.ORDER_CODE,A.ORDERSET_GROUP_NO,A.CASE_NO ");
    		sqlbf.append(" FROM OPD_ORDER A,SYS_FEE B   ");
    		sqlbf.append(" WHERE A.ORDER_CODE= B.ORDER_CODE  AND A.HIDE_FLG='N'  AND A.CAT1_TYPE='RIS' ");//AND A.CAT1_TYPE='RIS'
    		if("".equals(caseNo)){
    			sqlbf.append(" AND A.CASE_NO='"+getCaseNoOE(mrNo)+"' ");    			
    		}else{
    			sqlbf.append(" AND A.CASE_NO='"+caseNo+"' ");    			
    		}
    	}
//    	System.out.println("执行sql："+sqlbf.toString());
    	TParm result = new TParm(this.select(sqlbf.toString()));
    	return result;
    }
    //得到caseNo type 类别
    public String  getCaseNoOE(String mrNo){
    	StringBuffer sqlbf = new StringBuffer();//REGCAN_DATE  ARRIVE_FLG 
    	sqlbf.append("SELECT CASE_NO FROM REG_PATADM WHERE MR_NO = '"+mrNo+"' ");
    	sqlbf.append(" AND ARRIVE_FLG='Y' AND REGCAN_DATE IS  NULL  ");
    	//System.out.println("执行sql："+sqlbf.toString());
    	TParm result = new TParm(this.select(sqlbf.toString()));
    	if(result.getCount("CASE_NO")>0){
    		return result.getValue("CASE_NO",0);
    	}
    	return "";
    }
    //得到 得到MRNO对应的病历列表的集合（case_no_挂号时间）
    public TParm  getALLCaseNoOEForCombo(String mrNo){
    	StringBuffer sqlbf = new StringBuffer();//REGCAN_DATE  ARRIVE_FLG 
    	sqlbf.append("SELECT CASE_NO AS  ID,CASE_NO||'-'||TO_CHAR(ADM_DATE,'YYYY/MM/DD') AS NAME FROM REG_PATADM WHERE MR_NO = '"+mrNo+"' ");
    	sqlbf.append(" AND ARRIVE_FLG='Y' AND REGCAN_DATE IS  NULL  ");
    	//System.out.println("执行sql："+sqlbf.toString());
    	TParm result = new TParm(this.select(sqlbf.toString()));
    	return result;
    }
    //得到caseNo type 类别
    public String  getCaseNoI(String mrNo){
    	StringBuffer sqlbf = new StringBuffer();//REGCAN_DATE  ARRIVE_FLG 
    	sqlbf.append("SELECT CASE_NO FROM ADM_INP WHERE MR_NO = '"+mrNo+"' ");
    	//sqlbf.append(" AND ARRIVE_FLG='Y' AND REGCAN_DATE IS  NULL  ");
    	//System.out.println("执行sql："+sqlbf.toString());
    	TParm result = new TParm(this.select(sqlbf.toString()));
    	if(result.getCount("CASE_NO")>0){
    		return result.getValue("CASE_NO",0);
    	}
    	return "";
    }
    //得到caseNo type 类别
    public TParm getCaseNoIH(String mrNo){
    	StringBuffer sqlbf = new StringBuffer();//REGCAN_DATE  ARRIVE_FLG 
    	sqlbf.append("");
    	//System.out.println("执行sql："+sqlbf.toString());
    	TParm result = new TParm(this.select(sqlbf.toString()));
    	return result;
    }
    /**
     * 
     * 得到门诊医嘱
     * @param caseNo
     * @return
     */
    public TParm getOrderListForOPD(String caseNo){
		StringBuffer sqlbf = new StringBuffer();
		sqlbf.append(" SELECT CASE_NO,ORDERSET_CODE,ORDERSET_GROUP_NO,SETMAIN_FLG, ");
		sqlbf.append(" ORDER_DESC,SPECIFICATION,DOSAGE_QTY,MEDI_UNIT,EXEC_DEPT_CODE,OPTITEM_CODE,INSPAY_TYPE ");
		sqlbf.append(" FROM OPD_ORDER A WHERE  A.CAT1_TYPE='RIS'   AND A.CASE_NO='"+caseNo+"'");
		//System.out.println("sql:"+sqlbf.toString());
		TParm parm =  new TParm(this.select(sqlbf.toString()));
		return parm;
    }
}
