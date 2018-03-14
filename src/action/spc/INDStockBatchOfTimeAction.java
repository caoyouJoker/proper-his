package action.spc;

import java.sql.Timestamp;
import java.util.Date;

import jdo.spc.INDSQL;
import jdo.spc.INDTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.patch.Patch;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: �ս���ʹ���
 * </p> 
 * 
 * <p>
 * Description: �ս���ʹ���
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
 * @author zhangy 2009.09.02
 * @version 1.0
 */
public class INDStockBatchOfTimeAction extends Patch {

	public INDStockBatchOfTimeAction() {
	}

	public boolean run() {
		TConnection conn = TDBPoolManager.getInstance().getConnection();
		Timestamp date = StringTool.getTimestamp(new Date());
		Timestamp trandate = StringTool.rollDate(date, -1);
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getINDSysParm()));
		// �ж�ҩ��������趨
		if (parm.getValue("MANUAL_TYPE", 0) == null) {
			conn.close();
			return false;
		}
		String manual_type = parm.getValue("MANUAL_TYPE", 0);
		// �ж��Ƿ�Ϊ�Զ�������
		if (!"0".equals(manual_type)) {
			conn.close();
			return false;
		}
		// ��ѯ����BATCH_FLG='N'�Ĳ��� 
		TParm orgParm = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getINDBatchLockORG("N")));
		String datetime = date.toString().substring(0, 4)
				+ date.toString().substring(5, 7)
				+ date.toString().substring(8, 10);
		// Timestamp date = StringTool.getTimestamp(new Date()); 
		// �ȱ���HISTORY
		for (int i = 0; i < orgParm.getCount("ORG_CODE"); i++) {
            //1.4 ҩƷ����ս�������ҵ
            TParm inparm = new TParm();
            inparm.setData("ORG_CODE", orgParm.getValue("ORG_CODE",i));
            inparm.setData("BATCH_FLG", "Y");
            inparm.setData("OPT_USER", "PATCH_USER");
            inparm.setData("OPT_DATE", date);
            inparm.setData("OPT_TERM", "PATCH_IP");
            inparm.setData("TRANDATE",  trandate);
			TParm result = new TParm();
			result = INDTool.getInstance().onSaveIndStockHistory(datetime, orgParm.getValue("ORG_CODE",i));
		}
		
		
		
		for (int i = 0; i < orgParm.getCount("ORG_CODE"); i++) {
            //1.4 ҩƷ����ս�������ҵ
            TParm inparm = new TParm();
            inparm.setData("ORG_CODE", orgParm.getValue("ORG_CODE",i));
            inparm.setData("BATCH_FLG", "Y");
            inparm.setData("OPT_USER", "PATCH_USER");
            inparm.setData("OPT_DATE", date);
            inparm.setData("OPT_TERM", "PATCH_IP");
            inparm.setData("TRANDATE",  trandate);
			TParm result = new TParm();
			result = INDTool.getInstance().onIndStockBatchByOrgCode(inparm, conn);
		}	
		
		/**
		 * auto update ind_stockm_qty  is lock_stock_qty
		 */
		TParm result = new TParm();
		result = INDTool.getInstance().onUpdateIndStockMLockQty(conn) ;
		if (result.getErrCode() < 0) {
			 
			conn.rollback();
			conn.close();
			return false;
		}
		conn.commit();
		conn.close();

		return true;
	}
}
