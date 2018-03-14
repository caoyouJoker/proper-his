package jdo.hrm;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
 * <p>Title: 健康检查预交金</p>
 * 
 * <p>Description: 健康检查预交金</p>
 * 
 * <p>Copyright: javahis 20090922</p>
 * 
 * <p>Company:JavaHis</p>
 * 
 * @author ehui
 * @version 1.0
 */
public class HRMPrePay extends TDataStore {
	//ID
	private String id=Operator.getID();
	//IP
	private String ip=Operator.getIP();
	//transactionType.charge
	public String CHAREGE="01";
	//transactionType.refund
	public String REFUND="02";
	//初始化SQL
	private static final String INIT_SQL="SELECT * FROM HRM_BIL_PAY ORDER BY OPT_DATE";
	//初始化SQL
	private static final String INIT_SQL_BY_COMPANY="SELECT * FROM HRM_BIL_PAY WHERE COMPANY_CODE='#' ORDER BY OPT_DATE";
	/**
	 * 初始化数据
	 * @return
	 */
	public boolean onQuery(){
		this.setSQL(INIT_SQL);
		this.retrieve();
		return true;
	}
	/**
	 * 初始化数据
	 * @return
	 */
	public boolean onQuery(String companyCode){
		if(StringUtil.isNullString(companyCode)){
			return false;
		}
		this.setSQL(INIT_SQL_BY_COMPANY.replaceFirst("#", companyCode));
		this.retrieve();
		return true;
	}
	/**
	 * 根据给入团体代码新增一行
	 * @param companyCode
	 * @return
	 */
	public int insertRow(String companyCode){
		int row=this.insertRow();
		this.setItem(row, "COMPANY_CODE", companyCode);
		this.setItem(row, "OPT_USER", id);
		this.setItem(row, "OPT_TERM", ip);
		this.setItem(row, "CASHIER_CODE", id);
		Timestamp now=this.getDBTime();
		this.setItem(row, "CHARGE_DATE", now);
		this.setItem(row, "OPT_DATE", now);
		//$$======add by lx 2012/05/25 start=====$$//
		this.setItem(row, "REFUND_FLG", "N");
		
		//$$======add by lx 2012/05/25 end=====$$//
		this.setActive(row,false);
		return row;
	}
	/**
	 * 给据给入团体代码过滤团体信息
	 * @param companyCode
	 * @return
	 */
	public boolean filt(String companyCode){
		if(StringUtil.isNullString(companyCode)){
			return false;
		}
		this.setFilter("COMPANY_CODE='" +companyCode+ "'");
		return this.filter();
	}
	/**
	 * 给据给入团体代码过滤团体信息
	 * @param companyCode
	 * @return
	 */
	public boolean filt(String companyCode,String transactType){
		if(StringUtil.isNullString(companyCode)||StringUtil.isNullString(transactType)){
			return false;
		}
		this.setFilter("COMPANY_CODE='" +companyCode+ "' AND TRANSACT_TYPE='" +transactType+ "'");
		return this.filter();
	}
	/**
	 * 根据给入的团体代码得到预交金总额（交易类型：充值）
	 * @param companyCode
	 * @return
	 */
	public double getAllPay(String companyCode){
		double allPay=0.0;
		if(StringUtil.isNullString(companyCode)){
			return allPay;
		}
		if(!this.filt(companyCode, CHAREGE));
		int count=this.rowCount();
		for(int i=0;i<count;i++){
			allPay+=TypeTool.getDouble(this.getItemData(i, "PRE_AMT"));
		}
		this.filt(companyCode);
		return allPay;
		
	}
	/**
	 * 根据给入团体代码取得剩余预交金
	 * @param companyCode
	 * @return
	 */
	public double getSumPay(String companyCode){
		double sumPay=0.0;
		if(StringUtil.isNullString(companyCode)){
			return sumPay;
		}
		if(!this.filt(companyCode, CHAREGE));
		int count=this.rowCount();
		for(int i=0;i<count;i++){
			sumPay+=TypeTool.getDouble(this.getItemData(i, "PRE_AMT"));
		}
		
		if(!this.filt(companyCode, REFUND));
		count=this.rowCount();
		for(int i=0;i<count;i++){
			sumPay-=TypeTool.getDouble(this.getItemData(i, "PRE_AMT"));
		}
		this.filt(companyCode);
		return sumPay;
	}
	/**
	 * 根据给入的团体代码和金额新增一行数据
	 * @param companyCode
	 * @param preAmt
	 * @return
	 */
	public int insertRow(String companyCode,double preAmt){
		int row=insertRow();
		String receiptNo = SystemTool.getInstance().getNo("ALL", "BIL",
                "RECEIPT_NO", "RECEIPT_NO");
		this.setItem(row, "RECEIPT_NO", receiptNo);
		this.setItem(row, "PRE_AMT", preAmt);
		this.setItem(row, "COMPANY_CODE", companyCode);
		this.setItem(row, "OPT_USER", id);
		this.setItem(row, "CASHIER_CODE", id);
		this.setItem(row, "OPT_TERM", ip);
		Timestamp now=this.getDBTime();
		this.setItem(row, "CHARGE_DATE", now);
		this.setItem(row, "OPT_DATE", now);
		//$$======add by lx 2012/05/25 start=====$$//
		this.setItem(row, "REFUND_FLG", "N");
		
		//$$======add by lx 2012/05/25 end=====$$//
		this.setActive(row,true);
		return row;
	}
	/**
	 * 保存
	 * @return
	 */
	public TParm onSave(){
		
		TParm result=new TParm();
		String[] sql=this.getUpdateSQL();
		if(sql==null){
			result.setErrCode(-1);
			result.setErrText("没有保存数据");
			return result;
		}
		if(sql.length<1){
			result.setErrCode(-1);
			result.setErrText("没有保存数据");
			return result;
		}
		for(String tempSql:sql){
			result=new TParm(TJDODBTool.getInstance().update(tempSql));
			if(result.getErrCode()!=0){
				return result;
			}
		}
		return result;
		
	}
}
