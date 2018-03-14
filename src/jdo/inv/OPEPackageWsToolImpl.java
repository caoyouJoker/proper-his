package jdo.inv;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.jws.WebService;

import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;


import com.dongyang.Service.Server;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.sysfee.sysOdrPackDObserver;
import com.javahis.ui.bil.BILSPCINVRecordControl;
import com.javahis.util.OdiUtil;

/**
 * <p>
 * Title: 手术对接手术包 werbservice接口
 * </p>
 * 
 * <p>
 * Description: 手术对接手术包 werbservice接口
 * </p>
 *   
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p> 
 *  
 * @author fux 2014-5-4
 * @version 4.0     
 */
@WebService  
public class OPEPackageWsToolImpl implements OPEPackageWsTool 
{
    //只能放在web\WEB-INF\classes\jdo\ope
	//并且应放在his端,因为在his取值
	@Override
	public  String onSaveOpePackage(String opCode,String supTypeCode, String opDateS, String opDateE,String state,String optUser ,String optTerm) 
	{
		System.out.println("------------onSaveOpe 开始-----------------");
		StringBuffer strBuf = new StringBuffer(); 
		strBuf.append("<OPE_SAVE_RESULT>"); 
		//========================================================
		//====================start===================== 
 		String sql = ""; 
 		String why = ""; 
		//0 申请， 1 排程完毕 ，2手术完成 
		if ("0".equals(state)) {
			why = " AND STATE = '0' "; 
		} else if ("1".equals(state)) {
			why = " AND STATE = '1' ";  
		}
 		//OPBOOK_SEQ ; OP_CODE  ; SUPTYPE_CODE; OP_DATE ; STATE
 		//排程完成时间  APROVE_DATE  排程完成人员  APROVE_USER
			  sql = " SELECT OPBOOK_SEQ,OP_CODE1 AS OP_CODE,TYPE_CODE AS SUPTYPE_CODE,OP_DATE,STATE "+
			 		" FROM  OPE_OPBOOK " +
			        " WHERE OP_DATE BETWEEN TO_DATE('"+opDateS+"000000', 'YYYYMMDDHH24MISS') " +
		    		" AND TO_DATE('"+opDateE+"235959', 'YYYYMMDDHH24MISS') " +
		    		" '"+why+"' " +
			 		" ORDER BY OPBOOK_SEQ"; 
			  System.out.println("手术传回---"+sql); 
			 
 		TParm selParm = new TParm(); 
 		
		if(sql.length()!=0 && !sql.equals("")){
			 selParm = new TParm(TJDODBTool.getInstance().select(sql));
		}        
        if(selParm.getCount()>0)
    		for(int i = 0;i<selParm.getCount();i++){
    			String opbookSeq = selParm.getData("OPBOOK_SEQ",i).toString();
    			opCode = selParm.getData("OP_CODE",i).toString();
    			supTypeCode = selParm.getData("SUPTYPE_CODE",i).toString();
    			String opDate = selParm.getData("OP_DATE",i).toString();
    			//0 申请， 1 排程完毕 ，2手术完成
    		    state = selParm.getData("STATE",i).toString();
                
    		    Timestamp now = TJDODBTool.getInstance().getDBTime();
    			strBuf.append("<RESULT"+i+">");

    			strBuf.append("<OPBOOK_SEQ"+i+">");
    			strBuf.append(opbookSeq);
    			strBuf.append("</OPBOOK_SEQ"+i+">");
    			
    			strBuf.append("<OP_CODE"+i+">");
    			strBuf.append(opCode);
    			strBuf.append("</OP_CODE"+i+">");
    			
    			strBuf.append("<SUPTYPE_CODE"+i+">");
    			strBuf.append(supTypeCode);
    			strBuf.append("</SUPTYPE_CODE"+i+">");
    			
    			
    			strBuf.append("<OPT_USER>");
    			strBuf.append(optUser);
    			strBuf.append("</OPT_USER>");
    			
    			strBuf.append("<OPT_DATE"+i+">");
    			strBuf.append(now);
    			strBuf.append("</OPT_DATE"+i+">");
    			
    			strBuf.append("<OPT_TERM>");
    			strBuf.append(optTerm);  
    			strBuf.append("</OPT_TERM>");
    			
    		   
   			strBuf.append("<OP_DATE"+i+">");
			strBuf.append(opDate);
			strBuf.append("</OP_DATE"+i+">");
	
			strBuf.append("<STATE"+i+">");
			strBuf.append(state);
			strBuf.append("</STATE"+i+">");
    			
 		   strBuf.append("</RESULT"+i+">");
    		}
    		 System.out.println("循环结束");  
    		strBuf.append("</OPE_SAVE_RESULT>");
		System.out.println("保存结果----》"+strBuf.toString());
		return strBuf.toString();
		
	}

 

}
