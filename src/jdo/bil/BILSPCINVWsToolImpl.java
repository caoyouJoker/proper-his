package jdo.bil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.jws.WebService;

import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import action.bil.BILSPCINVFeeAction;
import action.bil.SPCINVRecordAction;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.sun.jmx.snmp.Timestamp;

/**
 * <p>
 * Title: ���ü�¼�Ʒ�werbservice�ӿ�
 * </p>
 * 
 * <p>
 * Description: ���ü�¼�Ʒ� werbservice�ӿ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author caowl 2013-7-31
 * @version 4.0
 */

@WebService
public class BILSPCINVWsToolImpl implements BILSPCINVWsTool{



	/**
	 * סԺ�Ʒѷ���
	 * */
	public String insertIBSOrder(String inString1,String inString2,String inStringM){
		String result = "";
		TParm inParm1 = new TParm();
		String2TParmTool tool = new String2TParmTool();
		inParm1 = tool.string2Ttparm(inString1);		
		TParm inParm2 = new TParm();
		inParm2 = tool.string2Ttparm(inString2);
		TParm inParmM = new TParm();
		inParmM = tool.string2Ttparm(inStringM);
		SPCINVRecordAction spcInvRecord = new SPCINVRecordAction();
		TParm results = spcInvRecord.countFee(inParm1,inParm2,inParmM);
		result = tool.tparm2String(results);
		return result;
	}
   /**
    * ����Ʒѷ���
    * */
	
	public String insertOpdOrder(String inString) {
		String result = "";

		return result;
	}
	
	/**
	 * ��ò�����Ϣ
	 * */
	public String onMrNo(String mrNo,String adm_type) {
		String result = "";
		String2TParmTool tool = new String2TParmTool();
		TParm parm = new TParm();
		parm.setData("MR_NO",mrNo);
		parm.setData("ADM_TYPE",adm_type);
//		System.out.println("=============��ִ��==================");
		String mr_no =PatTool.getInstance().checkMrno(
					TypeTool.getString(mrNo));
//		System.out.println("==============δִ��============");
		//סԺ����
		if(adm_type.equals("I")){
			 String sql = "SELECT A.MR_NO, A.IPD_NO, A.PAT_NAME, A.BIRTH_DATE, A.SEX_CODE, B.CASE_NO,B.DEPT_CODE,B.STATION_CODE,B.VS_DR_CODE AS DR_CODE"+
			 " FROM ADM_INP B, SYS_PATINFO A "+
			 " WHERE A.MR_NO = '"+mr_no+"' "+
			 "  AND A.MR_NO = B.MR_NO "+
			  " AND B.DS_DATE IS NULL "+
			  " AND B.IN_DATE IS NOT NULL "+
			  " AND B.CANCEL_FLG <> 'Y'";

//		  System.out.println("sql=="+sql);
          TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));  
          result = tool.tparm2String(selParm);
		}
		//�ż��ﲡ��
		if(adm_type.equals("O") || adm_type.equals("E")){
			 java.sql.Timestamp sysDate = SystemTool.getInstance().getDate();
//			 System.out.println(sysDate);
//			 System.out.println(sysDate.toString().substring(0,10));
			 String reg_date_start = sysDate.toString().substring(0,10)+" 00:00:00";
			 String reg_date_end = sysDate.toString().substring(0,10)+" 23:59:59";
			String sql = "SELECT A.MR_NO, A.IPD_NO, A.PAT_NAME, A.BIRTH_DATE, A.SEX_CODE, B.CASE_NO,B.DEPT_CODE,B.DR_CODE   "+   
		      " FROM   SYS_PATINFO A,REG_PATADM B    "+
		      " WHERE A.MR_NO = B.MR_NO "+
		      " AND A.MR_NO = '"+mrNo+"' "+
		      " AND B.REG_DATE BETWEEN TO_DATE('"+reg_date_start+"','yyyy-mm-dd hh24:mi:ss') AND TO_DATE('"+reg_date_end+"','yyyy-mm-dd hh24:mi:ss')"+		 
		      " ORDER BY B.CASE_NO DESC";	
			TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
			
			result = tool.tparm2String(selParm);
		}
		return result;
	}
	
	/**
	 * ��ȡ�Ʒ�����
	 * */
	public String onFeeData(String inString) {
		String result = "";
		TParm inParm1 = new TParm();
		String2TParmTool tool = new String2TParmTool();
		inParm1 = tool.string2Ttparm(inString);				
		SPCINVRecordAction spcInvRecord = new SPCINVRecordAction();
		TParm results = spcInvRecord.feeData(inParm1);
		result = tool.tparm2String(results);
		return result;
	}
	
	
	
	
	
	
    /**
     * ���Ʒ�״̬
     * @param caseNo
     * @param billDate
     * @return true/false   �ѼƷ�/δ�Ʒ�
     */
    public boolean onCheckFeeState(String caseNo,String billDate) {//wanglong add 20141014
        String checksql =
                "SELECT * FROM IBS_ORDM A, IBS_ORDD B           "
                        + " WHERE A.CASE_NO = B.CASE_NO         "
                        + "   AND A.CASE_NO_SEQ = B.CASE_NO_SEQ "
                        + "   and A.DATA_TYPE = '5'            "
                        + "   AND B.CASE_NO = '#'           "
                        + "   AND B.BILL_DATE =  TO_DATE('@', 'YYYY/MM/DD HH24:MI:SS') ";
        checksql = checksql.replaceFirst("#", caseNo);
        checksql = checksql.replaceFirst("@", billDate);
        TParm result = new TParm(TJDODBTool.getInstance().select(checksql));
        if (result.getCount() > 0) {
            return true;
        }
        return false;
    }
	



}