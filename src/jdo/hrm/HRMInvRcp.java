package jdo.hrm;

import java.sql.Timestamp;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
 * <p>Title: 健康检查票据详细</p>
 *
 * <p>Description: 健康检查票据详细</p>
 *
 * <p>Copyright: javahis 20090922</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMInvRcp extends TDataStore {
	// 初始化SQL
	private static final String INIT = "SELECT * FROM BIL_INVRCP WHERE INV_NO='#' AND RECP_TYPE='OPB'";
	private String id = Operator.getID();
	private String ip = Operator.getIP();
	/**
	 * 查询事件
	 *
	 * @return
	 */
	public int onQuery() {
		this.setSQL(INIT);
		return this.retrieve();
	}
	/**
	 * 按票号查询
	 * @param invNo
	 * @return
	 */
	public int onQueryByInvNo(String invNo){
		if(StringUtil.isNullString(invNo)){
			return -1;
		}
		String sql=INIT.replaceFirst("#", invNo);
		// System.out.println("onQuerybyInvNo.sql="+sql);
		this.setSQL(sql);
		return this.retrieve();
	}
	/**
	 * 新增一行
	 * @param parm
	 * @return
	 */
	public boolean insert(TParm parm){
		if(parm==null){
			return false;
		}
		int row=this.insertRow();
		Timestamp now=this.getDBTime();
		//RECP_TYPE
		this.setItem(row, "RECP_TYPE", parm.getValue("RECP_TYPE"));
		//INV_NO
		this.setItem(row, "INV_NO", parm.getValue("INV_NO"));
		//RECEIPT_NO
		this.setItem(row, "RECEIPT_NO", parm.getValue("RECEIPT_NO"));
		//CASHIER_CODE
		this.setItem(row, "CASHIER_CODE", id);
		//AR_AMT
		this.setItem(row, "AR_AMT", parm.getDouble("AR_AMT"));
		//CANCEL_FLG
		this.setItem(row, "CANCEL_FLG", parm.getValue("CANCEL_FLG"));
		//CANCEL_USER
		this.setItem(row, "CANCEL_USER", parm.getValue("CANCEL_USER"));
		//CANCEL_DATE
//		this.setItem(row, "CANCEL_DATE", now);
		//OPT_USER
		this.setItem(row, "OPT_USER", id);

                this.setItem(row, "ADM_TYPE", "H");
		//OPT_DATE\
		this.setItem(row, "OPT_DATE", now);
		//OPT_TERM
		this.setItem(row, "OPT_TERM", ip);
		//ACCOUNT_FLG
		this.setItem(row, "ACCOUNT_FLG", parm.getValue("ACCOUNT_FLG"));
		//ACCOUNT_SEQ
		this.setItem(row, "ACCOUNT_SEQ", parm.getValue("ACCOUNT_SEQ"));
		//ACCOUNT_USER\
		this.setItem(row, "ACCOUNT_USER", parm.getValue("ACCOUNT_USER"));
		//ACCOUNT_DATE
//		this.setItem(row, "ACCOUNT_DATE", null);
		//PRINT_USER
		this.setItem(row, "PRINT_USER", id);
		//PRINT_DATE
		this.setItem(row, "PRINT_DATE", now);
                //PRINT_DATE
		this.setItem(row, "STATUS",  parm.getValue("STATUS"));
		return true;
	}
	/**
	 * 根据给入主键补印动作，即更新旧数据CANCEL_FLG为1，CANCEL_USER,CANCEL_DATE，再新插入一条记录
	 * @param caseNo
	 * @param printNo
	 * @return
	 */
	public boolean onRePrint(String printNo,String newPrintNo){
		if(StringUtil.isNullString(printNo)||StringUtil.isNullString(newPrintNo)){
			return false;
		}
		String sql=this.INIT.replaceFirst("#", printNo);
		this.setSQL(sql);
		this.retrieve();
		if(this.rowCount()!=1){
			return false;
		}
		TParm rowParm=this.getRowParm(0);
		Timestamp now=this.getDBTime();
		this.setItem(0, "CANCEL_FLG", "3");
		this.setItem(0, "CANCEL_USER", Operator.getID());
		this.setItem(0, "CANCEL_DATE", now);
		this.setItem(0, "OPT_USER", Operator.getID());
		this.setItem(0, "OPT_DATE", now);
		this.setItem(0, "OPT_TERM", Operator.getIP());
		rowParm.setData("INV_NO",newPrintNo);
		rowParm.setData("ACCOUNT_FLG","");
		rowParm.setData("ACCOUNT_SEQ","");
		rowParm.setData("ACCOUNT_USER","");
		rowParm.setData("ACCOUNT_DATE","");
		this.insert(rowParm);
		this.setActive(this.rowCount()-1,true);
		return true;
	}
	/**
	 * 退费动作
	 * @param printNo
	 * @return
	 */
	public boolean onDisCharge(String printNo,String receiptNo,String cancelFlg,double amt){
		if(StringUtil.isNullString(printNo)||StringUtil.isNullString(receiptNo)||StringUtil.isNullString(cancelFlg)){
			// System.out.println("onDisCharge.param is null");
			return false;
		}
		if(amt>=0){
			// System.out.println("onDisCharge.amt>=0");
			return false;
		}
		TParm parm=new TParm();
		Timestamp now=this.getDBTime();
		parm.setData("RECP_TYPE","OPB");
		parm.setData("INV_NO",printNo);
		parm.setData("RECEIPT_NO",receiptNo);
		parm.setData("AR_AMT",amt);
		parm.setData("CANCEL_FLG",cancelFlg);
		parm.setData("CANCEL_USER",Operator.getID());
		parm.setData("CANCEL_DATE",now);
		parm.setData("ACCOUNT_FLG","N");
		parm.setData("ACCOUNT_SEQ", 0);
		parm.setData("ACCOUNT_USER", "");
		parm.setData("ACCOUNT_DATE","");

		return insert(parm);
	}
}
