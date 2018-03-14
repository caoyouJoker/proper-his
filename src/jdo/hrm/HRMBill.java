package jdo.hrm;

import java.util.ArrayList;
import java.util.List;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
 * <p>
 * Title: 健康检查结算
 * </p>
 * 
 * <p>
 * Description: 健康检查结算
 * </p>
 * 
 * <p>
 * Copyright: javahis 20090922
 * </p>
 * 
 * <p>
 * Company:JavaHis
 * </p>
 * 
 * @author ehui
 * @version 1.0
 */
public class HRMBill extends TDataStore {
	//查询
	private static final String INIT="SELECT * FROM HRM_BILL";
	//按CASE_NO查询
	private static final String GET_BILL_NO="SELECT BILL_NO FROM HRM_ORDER WHERE CASE_NO='#'";
	//按团体代码和合同代码查询
	private static final String GET_BILL_NO_BY_COM="SELECT * FROM HRM_BILL WHERE COMPANY_CODE='#' AND CONTRACT_CODE='#'";
	public boolean itemNow=false;
	private StringTool tool;
	private StringUtil util;
	private TJDODBTool jdoTool=TJDODBTool.getInstance();
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
	 * 查看是否存在此账单
	 * @param billNo
	 * @return
	 */
    public boolean existBill(String billNo) {// add by wanglong 20130317
        String sql = INIT + " WHERE BILL_NO='" + billNo + "'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0 || result.getCount() < 1) {
            return false;
        }
        return true;
    }

    /**
     * 根据结算单号初始化DataStore
     * @param billNo
     * @return
     */
    public int onQueryByBillNo(String billNo) {// add by wanglong 20130317
        if (StringUtil.isNullString(billNo)) {
            return -1;
        }
        String sql = INIT + " WHERE BILL_NO='" + billNo + "'";
        // System.out.println("onQueryByBillNo.sql="+sql);
        this.setSQL(sql);
        return this.retrieve();
    }
    
    /**
     * 根据结算单号列表初始化DataStore
     * @param billNoList
     * @return
     */
    public int onQueryByBillNoList(String billNoList) {// add by wanglong 20130510
        if (StringUtil.isNullString(billNoList)) {
            return -1;
        }
        String sql = INIT + " WHERE BILL_NO IN (#)";
        sql = sql.replaceFirst("#", billNoList);
        // System.out.println("onQueryByBillNo.sql="+sql);
        this.setSQL(sql);
        return this.retrieve();
    }
    
    /**
     * 根据账单号初始化DataStore
     * @param ReceiptNo
     * @return
     */
    public int onQueryByReceiptNo(String ReceiptNo) {// add by wanglong 20130324
        if (StringUtil.isNullString(ReceiptNo)) {
            return -1;
        }
        String sql = INIT + " WHERE RECEIPT_NO='" + ReceiptNo + "'";
        // System.out.println("onQueryByReceiptNo.sql="+sql);
        this.setSQL(sql);
        return this.retrieve();
    }
    
	/**
	 * 根据团体代码和合同代码查询
	 * @param companyCode
	 * @param contractCode
	 * @return
	 */
	public int onQueryByCom(String companyCode,String contractCode,String billNoStr){
		if(StringUtil.isNullString(companyCode)||StringUtil.isNullString(contractCode)){
			// System.out.println("companyCode"+companyCode);
			// System.out.println("contracactCode"+contractCode);
			return -1;
		}
		// System.out.println("onQueryByCom========================================================");
		String sql=GET_BILL_NO_BY_COM.replaceFirst("#", companyCode).replaceFirst("#", contractCode);
		sql += "  AND BILL_NO IN ("+billNoStr+")" ;
//		 System.out.println("onQueryByCom.sql="+sql);
		this.setSQL(sql);
		return this.retrieve();
	}
	/**
	 * 根据给入团体代码和合同代码初始化
	 * @param companyCode
	 * @param contractCode
	 * @return
	 */
	public int onQuery(String companyCode,String contractCode){
		if(util.isNullString(companyCode)||util.isNullString(contractCode)){
			return -1;
		}
		String sql=this.INIT+" WHERE COMPANY_CODE='"+companyCode+"' AND CONTRACT_CODE ='"+contractCode+"'  ORDER BY BILL_NO";
//		// System.out.println("sql===="+sql);
		this.setSQL(sql);
		return this.retrieve();
	}
	/**
	 * 插入一行，并取得BILL_NO
	 */
	public int insertRow(int row,String companyCode,String contractCode,HRMOrder order){
		if(row==-1){
			row=super.insertRow();
			//CASE_NO
			String billNo = SystemTool.getInstance().getNo("ALL", "HRM",
		            "BILL_NO", "BILL_NO");
			if(util.isNullString(billNo)){
				return -1;
			}
			this.setItem(row, "BILL_NO", billNo);
			this.setItem(row, "COMPANY_CODE", companyCode);
			this.setItem(row, "CONTRACT_CODE", contractCode);
			order.setFilter("");
			order.filter();
			int[] upRows=order.getModifiedRows();
//			// System.out.println("order.Amamt");
//			order.showDebug();
			double ownAmt=0.0;
			for(int upRow:upRows){
				
				ownAmt=order.getItemDouble(upRow, "OWN_AMT")+ownAmt;
			}
			this.setItem(row, "OWN_AMT", ownAmt);
		}
		return row;
	}
	/**
	 * 根据团体代码和合同代码插入一行数据
	 * @param row
	 * @param companyCode
	 * @param contractCode
	 * @return
	 */
	public int insertRow(int row,String companyCode,String contractCode){
		if(row==-1){
			row=super.insertRow();
			String billNo=SystemTool.getInstance().getNo("ALL", "HRM","BILL_NO", "BILL_NO");
			if(util.isNullString(billNo)){
				return -1;
			}
			this.setItem(row, "BILL_NO", billNo);
			this.setItem(row, "COMPANY_CODE", companyCode);
			this.setItem(row, "CONTRACT_CODE", contractCode);
			this.setItem(row, "OPT_USER", Operator.getID());
			this.setItem(row, "OPT_DATE", this.getDBTime());
			this.setItem(row, "OPT_TERM", Operator.getIP());
			this.setActive(row,false);
			return row;
		}
		return -1;
	}
	/**
	 * 根据给定病案号初始化数据
	 * @param mrNo
	 * @return
	 */
	public int  onQueryByMrNo(String mrNo){
		if(util.isNullString(mrNo)){
			return -1;
		}
		HRMContractD contract=new HRMContractD();
		TParm caseParm=contract.getComInfoByMrNO(mrNo);
		if(caseParm==null){
			return -1;
		}
		if(caseParm.getErrCode()!=0){
			return -1;
		}
		int count=caseParm.getCount();
		if(count<1){
			return -1;
		}
		List billNos=new ArrayList();
		for(int i=0;i<count;i++){
			String billNo=StringUtil.getDesc("HRM_ORDER", "BILL_NO", "CASE_NO='" +caseParm.getValue("CASE_NO",i)+"'");
			if(util.isNullString(billNo)){
				continue;
			}
			billNos.add(billNo);
		}
		String sql="SELECT * FROM HRM_BILL WHERE BILL_NO IN(#)";
		if(billNos.size()<1){
			return -1;
		}
		String where="";
		for(int i=0;i<billNos.size();i++){
			where+="'"+billNos.get(i)+"',";
		}
		where=where.substring(0,where.lastIndexOf(","));
		sql=sql.replaceFirst("#", where);
//		// System.out.println("sql here======="+sql);
		this.setSQL(sql);
		int result=this.retrieve();
//		// System.out.println("result====="+result);
		return result;
	}
	/**
	 * 列的值改变
	 */
//	public boolean setItem(int row, String column, Object value) {
//		
//		if(itemNow){
//			return true;
//		}
//		if("OWN_AMT".equalsIgnoreCase(column)){
//			double ownAmt=TypeTool.getDouble(value);
//			if(ownAmt<0){
//				itemNow=false;
//				return false;
//			}
//			// System.out.println("ownAmt="+ownAmt);
//			double arAmt=ownAmt-this.getItemDouble(row, "CUT_AMT")-this.getItemDouble(row, "DISCOUNT_AMT");
//			// System.out.println("arAmt="+arAmt);
//			if(arAmt<0){
//				itemNow=false;
//				return false;
//			}
//			super.setItem(row, column, value);
//			itemNow=false;
//			this.setItem(row, "AR_AMT", arAmt);
//			itemNow=false;
//			return true;
//		}
//		if("CUT_AMT".equalsIgnoreCase(column)){
//			double cutAmt=TypeTool.getDouble(value);
//			if(cutAmt<0){
//				itemNow=false;
//				return false;
//			}
//			double ownAmt=this.getItemDouble(row, "OWN_AMT");
//			if(ownAmt<=0){
//				itemNow=false;
//				return false;
//			}
//			this.setItem(row, "CUT_USER", Operator.getID());
//			double arAmt=ownAmt-cutAmt-this.getItemDouble(row, "DISCOUNT_AMT");
//			if(arAmt<0){
//				itemNow=false;
//				return false;
//			}
//			if(arAmt>ownAmt){
//				itemNow=false;
//				return false;
//			}
//			super.setItem(row, column, value);
//			this.setItem(row, "AR_AMT", arAmt);
//			itemNow=false;
//			return true;
//		}
//		if("DISCOUNT_RATE".equalsIgnoreCase(column)){
//			double ownAmt=this.getItemDouble(row, "OWN_AMT");
//			if(ownAmt<=0){
//				itemNow=false;
//				return false;
//			}
//			
//			//总价-减免价之后再打折
//			double beforeCount=this.getItemDouble(row, "OWN_AMT")-this.getItemDouble(row, "CUT_AMT");
//			double rate=TypeTool.getDouble(value);
//			if(rate<=0||rate>=1){
//				itemNow=false;
//				return false;
//			}
//			double discountAmt=StringTool.round(beforeCount*rate, 2);
//			double arAmt=beforeCount-discountAmt;
//			if(arAmt>ownAmt){
//				itemNow=false;
//				return false;
//			}
//			super.setItem(row, column, value);
//			this.setItem(row, "DISCOUNT_USER", Operator.getID());
//			this.setItem(row, "DISCOUNT_AMT", discountAmt);
//			itemNow=false;
//			return true;
//		}
//		if("CUT_DESCRIPTION".equalsIgnoreCase(column)){
//			double ownAmt=this.getItemDouble(row, "OWN_AMT");
//			if(ownAmt<=0){
//				itemNow=false;
//				return false;
//			}
//			super.setItem(row, column, value);
//			itemNow=false;
//			return true;
//		}
//		if("DISCOUTN_DESCRIPTION".equalsIgnoreCase(column)){
//			double ownAmt=this.getItemDouble(row, "OWN_AMT");
//			if(ownAmt<=0){
//				itemNow=false;
//				return false;
//			}
//			super.setItem(row, column, value);
//			itemNow=false;
//			return true;
//		}
//		super.setItem(row, column, value);
//		itemNow=false;
//		return true;
//	}
}
