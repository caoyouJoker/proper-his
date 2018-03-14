package action.inv;

import java.sql.Timestamp;
import java.util.Date;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.patch.Patch;
import com.dongyang.util.StringTool;
import jdo.ind.INDSQL;
import jdo.ind.INDTool;
import jdo.inv.INVsettlementTool;


/**
 * <p>
 * Title: 物资日结过帐管理
 * </p>
 * 
 * <p>
 * Description: 物资日结过帐管理
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author chenx 2013.07.22
 * @version 4.0
 */
public class INVStockBatchOfTimeAction extends Patch {     

	public INVStockBatchOfTimeAction() {
	}

	public boolean run() {
		TConnection conn = TDBPoolManager.getInstance().getConnection();
		Timestamp date = StringTool.getTimestamp(new Date());     
		Timestamp trandate = StringTool.rollDate(date, -1);
//		TParm orgParm = INVsettlementTool.getInstance().getInvOrg() ;
		
            //1.4 物资库存日结批次作业
            TParm inparm = new TParm();
            inparm.setData("OPT_USER", "PATCH_USER"); 
            inparm.setData("OPT_DATE", date);
            inparm.setData("OPT_TERM", "PATCH_IP");
            inparm.setData("TRANDATE",  trandate);
		  TParm 	result = INVsettlementTool.getInstance().onInvStockBatchByOrgCode(inparm, conn) ;
		  
		  if(result.getErrCode()<0){
			  conn.rollback();
			  conn.close() ;
			  return false ;
		  }


		conn.commit();
		conn.close();
		return true;
	}
}
