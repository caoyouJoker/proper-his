package action.inv;

import com.dongyang.action.TAction;

import jdo.bil.String2TParmTool;  
import jdo.inv.StringToTParmTool;
import jdo.ope.client.OPEPackageWsTool_OPEPackageWsToolImplPort_Client;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author  fux 
 * @version 1.0  
 */
public class INVAutoRequsetAction
    extends TAction {                             
//    public INVAutoRequsetAction() {
//    }
    /**    
 	 * 查询手术申请   
 	 * */ 
 	public TParm onOpePackage(TParm parm){
 	 		//fux modify 20150504
 			String sql = "";
 			String why = "";
 			String state = parm.getValue("STATE").replace('[',' ').replace(']',' ').trim() ;
 			if ("0".equals(state)) {
 				why = " AND STATE = '0' ";  
 			} else if ("1".equals(state)) {
 				why = " AND STATE = '1' ";
 			} else {
 				why = " AND STATE IN ('0','1','2','3','4','5','6') ";
 			}
 			// OPBOOK_SEQ ; OP_CODE ; SUPTYPE_CODE; OP_DATE ; STATE
 			// 排程完成时间 APROVE_DATE 排程完成人员 APROVE_USER
 			// 2014-05-27
 			String opDateS =parm.getValue("OPDATE_S").replace('[',' ').replace(']',' ').trim();
 			String opDateE =parm.getValue("OPDATE_E").replace('[',' ').replace(']',' ').trim();
 			String opDateSYearS = opDateS.substring(0, 4);
 			String opDateSMonthS = opDateS.substring(5, 7);  
 			String opDateSDayS = opDateS.substring(8, 10);
 			String opDateSYearE = opDateE.substring(0, 4);  
 			String opDateSMonthE = opDateE.substring(5, 7);
 			String opDateSDayE = opDateE.substring(8, 10);
 			sql = " SELECT A.OPBOOK_SEQ,A.OP_CODE1 AS OP_CODE,A.TYPE_CODE AS SUPTYPE_CODE,A.REMARK,"
 				+ " A.OP_DATE,A.STATE,A.MR_NO,B.PAT_NAME,A.GDVAS_CODE "
 				+ " FROM  OPE_OPBOOK A,SYS_PATINFO B "  
 				+ " WHERE A.OP_DATE BETWEEN TO_DATE('"
 				+ parm.getValue("OPDATE_S").replace('[',' ').replace(']',' ').trim()
 				+ opDateSYearS
 				+ opDateSMonthS  
 				+ opDateSDayS  
 				+ "000000', 'YYYYMMDDHH24MISS') "
 				+ " AND TO_DATE('"  
 				+ opDateSYearE
 				+ opDateSMonthE
 				+ opDateSDayE
 				+ "235959', 'YYYYMMDDHH24MISS') " + " AND A.MR_NO = B.MR_NO " +
 				// " WHERE OP_DATE BETWEEN TO_DATE('20140526000000', 'YYYYMMDDHH24MISS') "
 				// +
 				// " AND TO_DATE('20140527235959', 'YYYYMMDDHH24MISS') " +
 				why + " ORDER BY A.OPBOOK_SEQ";
 			System.out.println("sql_opeOpbook:"+sql);    
// 	 		 OPEPackageWsTool_OPEPackageWsToolImplPort_Client client = new OPEPackageWsTool_OPEPackageWsToolImplPort_Client();  
 	//
// 	 	     String returnString = client.onSaveOpePackage(parm.getValue("OPCODE").replace('[',' ').replace(']',' ').trim() 
// 	 	    		 ,parm.getValue("SUPTYPECODE").replace('[',' ').replace(']',' ').trim()
// 	 	    		 //2014-05-28 00:00:00.0    
// 	 	    		 ,parm.getValue("OPDATE_S").replace('[',' ').replace(']',' ').trim()
// 	 	    		 ,parm.getValue("OPDATE_E").replace('[',' ').replace(']',' ').trim()    
// 	 	    		 ,parm.getValue("STATE").replace('[',' ').replace(']',' ').trim()                
// 	 	    		 ,parm.getValue("ID").replace('[',' ').replace(']',' ').trim()       
// 	 	    		 ,parm.getValue("IP").replace('[',' ').replace(']',' ').trim());       
// 	 	     System.out.println("returnString："+returnString);                     
// 	 	     String2TParmTool tool = new String2TParmTool();        
 	 	     TParm result = new TParm();  
 	 	     result =  new TParm(TJDODBTool.getInstance().select(sql));
// 	 	     result = tool.string2Ttparm(returnString);
 	 	     return result;    
 	 	
 	}
}
//	/**
//	 * 根据病案号查询
//	 * */ 
//	public TParm onMrNo(TParm parm){
//		TParm result = new TParm(); 
//		String mrNo = parm.getData("MR_NO").toString(); 
//		String admType = parm.getData("ADM_TYPE").toString();
////	    System.out.println("parm---->"+parm);
////	    System.out.println("mrNo"+mrNo);
////	    System.out.println("admType"+admType);
//	    //BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client BILSPCINVWsToolImplClient = new BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client();					
//	    BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client BILSPCINVWsToolImplClient = new BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client();					
//	    System.out.println("ws 开始执行。。。。");
//	    String returnDto=  BILSPCINVWsToolImplClient.onMrNo(mrNo, admType);
//	    System.out.println("====>"+returnDto);
//	    System.out.println("ws 执行结束。。。。");
//	    if(returnDto.equals(":#PKs:")){
//	    	return result;
//	    }
//		String2TParmTool tool = new String2TParmTool();
//		result = tool.string2Ttparm(returnDto);
//		
//			
//		System.out.println("action--->"+result);
//		return result;
//	}  

