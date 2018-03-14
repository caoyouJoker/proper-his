package jdo.inv;

import java.sql.Timestamp;

import javax.print.DocFlavor.STRING;

import jdo.ind.IndDDStockTool;
import jdo.ind.IndOrgTool;
import jdo.ind.IndStockDTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title:物资财务
 *
 * <p>Description: 
 *
 * <p>Copyright: 
 *
 * <p>Company: JavaHis</p>
 *
 * @author  chenx 
 * @version 4.0
 */
public class INVsettlementTool extends TJDOTool{
	
	/**
	 * 构建实例
	 */
  public static INVsettlementTool instanceObject ;
    /**
     * 得到实例
     * @return
     */
  public static INVsettlementTool getInstance(){
	  if(instanceObject==null)
		  instanceObject = new INVsettlementTool() ;
	  return instanceObject ;
  }
  
  /**
   * 构造器
   */
  public INVsettlementTool(){
	  setModuleName("inv\\INVsettlementModule.x") ;
	  onInit() ;
  }
  
  /**
   * 查询供应商对应的药品是否为空
   */
  public TParm getAgentInvCode(TParm parm){
	  String sql = " SELECT SUP_CODE, INV_CODE " +
	  		       "  FROM  INV_AGENT    " +
	  		       " WHERE SUP_CODE = '"+parm.getValue("SUP_CODE")+"'" +
	  		       " AND   INV_CODE = '"+parm.getValue("INV_CODE")+"'"  ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 自动生成请领单获取月结数据
   * inv_account
   */
  public TParm getRequestAccount(TParm parm){
	  String sql = " SELECT INV_CODE,TOTAL_OUT_QTY QTY,VERIFYIN_PRICE COST_PRICE  FROM INV_ACCOUNT WHERE " +
	  		      "  CLOSE_DATE = '"+parm.getValue("CLOSE_DATE")+"'" +
	  		      "  AND ORG_CODE = '"+parm.getValue("ORG_CODE")+"'"  ;
	  System.out.println("sql=自动生成请领单获取月结数据====="+sql);
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 自动生成请领单获取月结数据的科室
   * inv_account
   */
  public TParm getRequestInvOrg(TParm parm){
	  String sql = " SELECT DISTINCT ORG_CODE,CLOSE_DATE FROM INV_ACCOUNT WHERE " +
	  		      "  CLOSE_DATE = '"+parm.getValue("CLOSE_DATE")+"'"  ;
	  System.out.println("sql==自动生成请领单获取月结数据的科室===="+sql);
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 获取日结交易档
   */
  public TParm getDayStock(TParm parm){
	  TParm result = query("getDayStock",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 物资部门费用统计报表
   */
  public TParm queryDeptFee(TParm parm){
	  TParm result = query("queryDeptFee",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  
  /**
   * 物资验证入库汇总报表查询-duzhw
   */
  public TParm queryInAllDispense(TParm parm){
	  TParm result = query("queryInAllDispense",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  
  /**
   * 物资入库明细报表查询-duzhw
   */
  public TParm queryInDetailDispense(TParm parm){
	  TParm result = query("queryInDetailDispense",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  
  /**
   * 物资出库明细报表查询-duzhw
   */
  public TParm queryOutDetailDispense(TParm parm){
	  TParm result = query("queryOutDetailDispense",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  
  /**
   * 物资出库单报表
   */
  public TParm queryOutDispense(TParm parm){
	  TParm result = query("queryOutDispense",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 物资入库单报表
   */
  public TParm queryInDispense(TParm parm){
	  TParm result = query("queryInDispense",parm);
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 获取耗材sql
   */
  public String  getSpcInvRecordSql(String startDate,String endDate,String supCode){
	  String  sql = "SELECT C.INV_CODE ,A.INV_CHN_DESC ,A.DESCRIPTION,C.QTY," +
                   " C.OWN_PRICE ,B.UNIT_CHN_DESC,C.QTY*C.OWN_PRICE TOT_AMT,D.ORG_DESC, " +
                   " C.DEPT_CODE ORG_CODE,B.UNIT_CODE " +
                   " FROM  INV_BASE A ,SYS_UNIT B ,INV_ORG D ," +
                   " (SELECT INV_CODE ,SUM(QTY) QTY,OWN_PRICE,DEPT_CODE FROM  SPC_INV_RECORD  " +
                   " WHERE BILL_FLG='Y'  " +
                   " AND BILL_DATE BETWEEN TO_DATE ( '"+startDate+"' , 'YYYYMMDDHH24MISS')" +
                   " AND  TO_DATE ( '"+endDate+"' , 'YYYYMMDDHH24MISS')" +
                   " GROUP BY INV_CODE,OWN_PRICE,DEPT_CODE) C" +
                   " WHERE  C.INV_CODE = A.INV_CODE " +
                   " AND    C.DEPT_CODE = D.ORG_CODE" +
                   " AND    A.STOCK_UNIT = B.UNIT_CODE" +
                   " AND    A.SUP_CODE = '"+supCode+"'"  ;
	  return   sql  ;
  }
  /**
   * 获取出库结算sql
   */
  public String  getInvDispenseSql(String startDate,String endDate,String supCode){
	  String  sql = "SELECT C.INV_CODE ,A.INV_CHN_DESC ,A.DESCRIPTION,C.QTY," +
        " C.COST_PRICE OWN_PRICE ,B.UNIT_CHN_DESC,C.QTY*C.COST_PRICE TOT_AMT,D.ORG_DESC, " +
        " C.FROM_ORG_CODE ORG_CODE ,B.UNIT_CODE " +
        " FROM  INV_BASE A ,SYS_UNIT B ,INV_ORG D, " +
       " (SELECT DE.INV_CODE ,SUM(DE.QTY) QTY,DE.COST_PRICE,MA.FROM_ORG_CODE " +
       " FROM  INV_DISPENSED DE , INV_DISPENSEM MA " +
       " WHERE DE.DISPENSE_NO=MA.DISPENSE_NO " +
       "  AND    MA.FINA_FLG  IN ('1','3') " +
       "  AND MA.DISPENSE_DATE BETWEEN TO_DATE ( '"+startDate+"' , 'YYYYMMDDHH24MISS') " +
       "  AND TO_DATE ( '"+endDate+"' , 'YYYYMMDDHH24MISS') " +
       " GROUP BY DE.INV_CODE,DE.COST_PRICE,MA.FROM_ORG_CODE) C" +
       " WHERE  C.INV_CODE = A.INV_CODE " +
       " AND    C.FROM_ORG_CODE = D.ORG_CODE" +
       " AND    A.STOCK_UNIT = B.UNIT_CODE" +
       " AND    A.SUP_CODE = '"+supCode+"'"  ;
  
	  return   sql  ;
  }
  /**
   * 获取退库结算sql
   */
  public String  getInvRtnSql(){
	  String sql  =""  ;
	  return   sql  ;
  }
  /**
   * 获取智能柜sql
   */
  public String  getInvCabinetSql(){
	  String sql  =""  ;
	  return   sql  ;
  }
  /**
   * 获取结算数据
   */
  public TParm getAccountData(TParm parm){
	  //开始时间
	  String startDate = parm.getValue("START_DATE") ;
	 //结束时间
	String endDate = parm.getValue("END_DATE");
	//供应商代码
	String supCode = parm.getValue("SUP_CODE");
	//获取耗材sql
	String getSpcInvRecordSql = this.getSpcInvRecordSql(startDate,endDate, supCode) ;
	//获取出库结算sql
	String getInvDispenseSql = this.getInvDispenseSql(startDate,endDate, supCode) ;
	String sql = getSpcInvRecordSql + " UNION ALL "  +getInvDispenseSql +" ORDER BY ORG_DESC" ;
	System.out.println("SQL ==========物资结算sql========"+sql);
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 查询物资所有部门
   */
  public TParm getInvOrg(){
	  String sql = "SELECT ORG_CODE FROM INV_ORG  "  ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
	/**
	 * 是否结算过
	 * @param parm
	 * @return
	 */
	public TParm checkInvAccount(TParm parm){
		String sql = " SELECT  ACCOUNT_NO FROM INV_DISPENSEM  WHERE  " +
				"   DISPENSE_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') " +
				"   AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS') " +
		        "   AND FINA_FLG IN ('1','3') " +
		        "   AND ACCOUNT_NO IS NOT NULL " +
		        "  UNION ALL " +
		        "  SELECT ACCOUNT_NO  FROM SPC_INV_RECORD WHERE" +
		        "  BILL_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') " +
				"  AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS') " +
		        "  AND BILL_FLG = 'Y' " +
		        "  AND ACCOUNT_NO IS NOT NULL " ;
		System.out.println("sql============="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		 if (result.getErrCode() < 0) {
	          err(result.getErrCode() + " " + result.getErrText());
	          return result;
	      }
		return result;
	}
	/**
	 * 查询inv_account 数据，将其数据同步给his
	 * @param parm
	 * @return
	 */
	public TParm onQueryInvAccount(TParm parm){
		String sql = "SELECT * FROM INV_ACCOUNT  WHERE  CLOSE_DATE='"+parm.getValue("CLOSE_DATE")+"' " ;
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		 if (result.getErrCode() < 0) {
	          err(result.getErrCode() + " " + result.getErrText());
	          return result;
	      }
		return result;
	}
	/**
	 * 查询取消日结的数据
	 * @param parm
	 * @return
	 */
	public TParm onCancleInvAccount(String date){   
		String sql = "SELECT  DISTINCT ACCOUNT_NO ,CLOSE_DATE,'N' AS FLG FROM INV_ACCOUNT  " +
				" WHERE  CLOSE_DATE='"+date+"'  " ;
		System.out.println("SQL=查询取消日结的数据=="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		 if (result.getErrCode() < 0) {
	          err(result.getErrCode() + " " + result.getErrText());
	          return result;
	      }
		return result;
	}
	/**
	 * 查inv_account历史数据
	 * @param parm
	 * @return
	 */
	public TParm getAccountData(String accountNo){
		String sql ="SELECT C.INV_CODE ,A.INV_CHN_DESC INV_DESC,A.DESCRIPTION,C.TOTAL_OUT_QTY QTY," +
        " C.VERIFYIN_PRICE OWN_PRICE ,B.UNIT_CHN_DESC,C.VERIFYIN_AMT TOT_AMT,D.ORG_DESC " +
        " FROM  INV_BASE A ,SYS_UNIT B , INV_ACCOUNT C ,INV_ORG D " +
        " WHERE  C.INV_CODE = A.INV_CODE " +
        " AND    C.ORG_CODE = D.ORG_CODE" +
        " AND    A.STOCK_UNIT = B.UNIT_CODE" +
        " AND    C.ACCOUNT_NO = '"+accountNo+"'"  ;
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		 if (result.getErrCode() < 0) {
	          err(result.getErrCode() + " " + result.getErrText());
	          return result;
	      }
		return result;
	}
  /**
   * 新增invaccount 数据
   * @param parm
   * @return
   */
  public TParm insertAccountData(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount();i++){
		  result = update("insertAccountData", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;
	      }  
	  }
	
	  return result ;
	  
  }
  /**
   * 根据流水号，删除inv_account
   * @param parm
   * @return
   */
  public TParm deleteDataByAccountNo(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
		  result = update("deleteDataByAccountNo", parm,conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;
	      }  
	  return result ;
	  
  }
  /**
   * 根据流水号，清空耗用记录流水号
   * @param parm
   * @return
   */
  public TParm updateSpcInvRecordByAccountNo(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
		for(int i=0;i<parm.getCount();i++){
			  result = update("updateSpcInvRecordByAccountNo", parm.getRow(i),conn);
		      if (result.getErrCode() < 0) {
		          err("ERR:" + result.getErrCode() + result.getErrText() +
		              result.getErrName());
		          return result;
		      }  	
		}
		
	  return result ;
	  
  }
  /**
   * 根据流水号，清空出库流水号
   * @param parm
   * @return
   */
  public TParm updateInvDisByAccountNo(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
		for(int i=0;i<parm.getCount();i++){
			 result = update("updateInvDisByAccountNo", parm.getRow(i),conn);
		      if (result.getErrCode() < 0) {
		          err("ERR:" + result.getErrCode() + result.getErrText() +
		              result.getErrName());
		          return result;
		      }  	
		}
		 
	  return result ;
	  
  }
  /**
   * 更新交易表的account_no ,说明交易表的这笔数据已结算    
   * 耗用记录
   * @param parm
   * @return
   */
  public TParm updateSpcInvRecordAccountNo(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	
		  result = update("updateSpcInvRecordAccountNo", parm,conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;

	  }
	
	  return result ;
	  
  }
  /**
   * 更新交易表的account_no ,说明交易表的这笔数据已结算  
   * 出库记录  
   * @param parm
   * @return
   */
  public TParm updateInvDisAccountNo(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
		  result = update("updateInvDisAccountNo", parm,conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;

	  }
	
	  return result ;
	  
  }
  /**
   * 新增insertDDStock 数据
   * @param parm
   * @return
   */
  public TParm insertDDStock(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("insertDDStock", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
	  return result ;
	  
  }
  /**
   *  更新将物资库表入出库的数据归0
   * @param parm
   * @return
   */
  
  public TParm updateInvStockDToZero(TParm parm ,TConnection conn){
	  TParm result = new TParm() ;
	  result = update("updateInvStockDToZero", parm,conn);
      if (result.getErrCode() < 0) {
          err("ERR:" + result.getErrCode() + result.getErrText() +
              result.getErrName());
          return result;
      }
      return result;
  }

  /**
   * 删除 incaccount 数据
   * @param parm
   * @return
   */
  public TParm deleteAccountData(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
		for(int i=0;i<parm.getCount();i++){
			  result = update("deleteAccountData", parm.getRow(i),conn);
		      if (result.getErrCode() < 0) {
		          err("ERR:" + result.getErrCode() + result.getErrText() +
		              result.getErrName());
		          return result;
		      }
		}
	
	  return result ;
  }
  /**
   * 新增 请领  主表
   * @param parm
   * @return
   */
  public TParm insertRequestM(TParm parm,TConnection conn){
	       TParm result = new TParm() ;
	    	   result = update("insertRequestM", parm,conn);
			   if (result.getErrCode() < 0) {
			    err("ERR:" + result.getErrCode() + result.getErrText() +
			           result.getErrName());
			      return result;
	       }
		
	
	  return result ;
  }
  /**
   * 新增 请领  细表
   * @param parm
   * @return
   */
  public TParm insertRequestD(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount();i++){
		  result = update("insertRequestD", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;

	}  
	  }
			
	
	  return result ;
  }
  /**
   * 新增 出入库  细表
   * @param parm
   * @return
   */
  public TParm insertDispenseD(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	  for(int i=0;i<parm.getCount();i++){
		  result = update("insertDispenseD", parm.getRow(i),conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;

	}  
	  }
			
	
	  return result ;
  }
  /**
   *新增 出入库  主表
   * @param parm
   * @return
   */
  public TParm insertDispenseM(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
	
		  result = update("insertDispenseM", parm,conn);
	      if (result.getErrCode() < 0) {
	          err("ERR:" + result.getErrCode() + result.getErrText() +
	              result.getErrName());
	          return result;

	  
	  }
			
	
	  return result ;
  }
  /**
   * 删除  his   incaccount 数据
   * @param parm
   * @return
   */
  public TParm deleteAccountDataHis(TParm parm,TConnection conn){
	  TParm result = new TParm() ;
			  result = update("deleteAccountDataHis", parm,conn);
		      if (result.getErrCode() < 0) {
		          err("ERR:" + result.getErrCode() + result.getErrText() +
		              result.getErrName());
		          return result;

		}
	
	  return result ;
  }
  /**
   * 物资 日结批次作业
   * @param parm TParm
   * @param conn TConnection
   * @return TParm
   */
  public TParm onInvStockBatchByOrgCode(TParm parm, TConnection conn) {
      // 结果集
      TParm result1 = new TParm();
      result1.setErrCode(-1) ;
      // 数据检核
      if (parm == null)
          return result1;
      TParm resutlParm = new TParm();
      //查询日库存交易档
      TParm result = this.getDayStock(parm) ;
      if (result.getErrCode() < 0) {
          return result1;
      }
      //日结批次作业
      for (int i = 0; i < result.getCount("INV_CODE"); i++) {
          // 当前库存
          double stock_qty = result.getDouble("STOCK_QTY", i);
          // 昨日库存
          double last_stock_qty = result.getDouble("LASTDAY_TOLSTOCK_QTY", i);
          // 本日入
          double in_qty = result.getDouble("DAYIN_QTY", i);
          // 本日出
          double out_qty = result.getDouble("DAYOUT_QTY", i);
          // 本日盘差
          double modi_qty = result.getDouble("DAY_CHECKMODI_QTY", i);
          if (stock_qty == 0 && last_stock_qty == 0 && in_qty == 0 &&
              out_qty == 0 && modi_qty == 0) {
              continue;
          }
          // 计算日使用量 = 今日出库 - 盘差调整
          double UseQty = out_qty - modi_qty;
          // 计算日库存量 = 昨日库存 + 今日入库 - 今日出库 + 盘差调整
          double TotQty = stock_qty;//last_stock_qty + in_qty - out_qty + modi_qty;;;;update by liyh stock_qty是实时值，不用计算
          if(TotQty != (last_stock_qty + in_qty - out_qty + modi_qty)){//不等于 写日志  add liyh by liudy
          	System.out.println("-------STOCK_QTY != (last_stock_qty + in_qty - out_qty + modi_qty):::"+"stock:"+TotQty+",last_stock_qty:"+last_stock_qty+
          			",in_qty:"+in_qty+",out_qty:"+out_qty+",modi_qty:"+modi_qty);
          }
          // 计算日支出金额(成本价)
          double UseAmt = UseQty * result.getDouble("COST_PRICE", i); 
          // 计算日库存金额
          double TotAmt = TotQty * result.getDouble("COST_PRICE", i);
          // 计算昨日库存金额
          double TotLastStockAmt = last_stock_qty *
              result.getDouble("COST_PRICE", i);

          // 写入物资库存日结作业
          TParm inparm = new TParm();
          inparm.setData("TRANDATE",
                         parm.getValue("TRANDATE").substring(0, 10).
                         replaceAll("-", ""));

          inparm.setData("ORG_CODE", result.getData("ORG_CODE",i));     
          inparm.setData("INV_CODE", result.getData("INV_CODE", i));
          inparm.setData("BATCH_SEQ", result.getData("BATCH_SEQ", i));
          inparm.setData("REGION_CODE", result.getData("REGION_CODE", i));
          inparm.setData("BATCH_NO", result.getData("BATCH_NO", i));
          inparm.setData("VALID_DATE", result.getData("VALID_DATE", i));
          inparm.setData("DD_IN_QTY", result.getDouble("DAYIN_QTY", i));
          inparm.setData("DD_OUT_QTY", result.getDouble("DAYOUT_QTY", i));
          inparm.setData("DD_CHECKMODI_QTY", result.getDouble("DAY_CHECKMODI_QTY", i));
          inparm.setData("DD_STOCK_QTY", TotQty);
          inparm.setData("DD_STOCK_AMT", TotAmt);
          inparm.setData("LAST_DD_STOCK_QTY", last_stock_qty);
          inparm.setData("LAST_DD_STOCK_AMT", TotLastStockAmt);  
          inparm.setData("DD_VERIFYIN_QTY", result.getDouble("DAY_VERIFYIN_QTY", i));
          inparm.setData("DD_VERIFYIN_AMT", result.getDouble("DAY_VERIFYIN_AMT", i));
          inparm.setData("DD_GIFTIN_QTY", result.getDouble("GIFTIN_QTY", i));
          inparm.setData("DD_REGRESSGOODS_QTY",
                         result.getDouble("DAY_REGRESSGOODS_QTY", i));
          inparm.setData("DD_REGRESSGOODS_AMT",
                         result.getDouble("DAY_REGRESSGOODS_AMT", i));
          inparm.setData("DD_ASSIGNIN_QTY",
                         result.getDouble("DAY_REQUESTIN_QTY", i));
          inparm.setData("DD_ASSIGNOUT_QTY",
                         result.getDouble("DAY_REQUESTOUT_QTY", i));
          inparm.setData("DD_CHANGEIN_QTY",result.getDouble("DAY_CHANGEIN_QTY", i));
          inparm.setData("DD_CHANGEOUT_QTY",result.getDouble("DAY_CHANGEOUT_QTY", i));
          inparm.setData("DD_TRANSMITIN_QTY", result.getDouble("DAY_TRANSMITIN_QTY", i));
          inparm.setData("DD_TRANSMITOUT_QTY", result.getDouble("DAY_TRANSMITOUT_QTY", i));
          inparm.setData("DD_WASTE_QTY", result.getDouble("DAY_WASTE_QTY", i));
          inparm.setData("DD_DISPENSE_QTY", result.getDouble("DAY_DISPENSE_QTY", i));
          inparm.setData("DD_REGRESS_QTY", result.getDouble("DAY_REGRESS_QTY", i));
          inparm.setData("DD_STOCK_PRICE", result.getDouble("UNIT_PRICE", i));
          inparm.setData("DD_NHI_PRICE", result.getDouble("UNIT_PRICE", i));
          inparm.setData("DD_OWN_PRICE", result.getDouble("UNIT_PRICE", i));
          inparm.setData("DD_RPP_AMT", result.getDouble("UNIT_PRICE", i));
          inparm.setData("OPT_USER", parm.getData("OPT_USER"));
          inparm.setData("OPT_TERM", parm.getData("OPT_TERM"));
          resutlParm = this.insertDDStock(inparm, conn) ;
          if (resutlParm.getErrCode() < 0) {
              return result1;
          }
          // 各项出库纪录归零
          TParm srockParm = new TParm();
          srockParm.setData("ORG_CODE", result.getData("ORG_CODE",i));
          srockParm.setData("INV_CODE", result.getData("INV_CODE", i));
          srockParm.setData("BATCH_SEQ", result.getData("BATCH_SEQ", i));
          srockParm.setData("LASTDAY_TOLSTOCK_QTY", TotQty);
          srockParm.setData("OPT_USER", parm.getData("OPT_USER"));
          srockParm.setData("OPT_TERM", parm.getData("OPT_TERM"));

          resutlParm = this.updateInvStockDToZero(srockParm, conn) ;
          if (resutlParm.getErrCode() < 0) {
              return result1;
          }
      }
      return resutlParm;
  } 
  /**
   * 通过物资编码，获取物资的一些基本信息
   */
  public TParm getInvBase(String invCode){
	  String sql = " SELECT INV_CHN_DESC ,DESCRIPTION,MAN_CODE,STOCK_UNIT " +
	  		       " FROM  INV_BASE WHERE" +
	  		       "  ACTIVE_FLG = 'Y'       " +
	  		       "  AND INV_CODE = '"+invCode+"' " ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 查看是否日结
   */
  public TParm selectTranDate(String tranDate){
	  String sql = "SELECT TRANDATE  FROM INV_DDSTOCK WHERE TRANDATE = '"+tranDate+"'" ;
	 
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 查看最大结算日期
   */
  public TParm maxTranDate(){
	  String sql = "SELECT MAX(TRANDATE) TRANDATE  FROM INV_DDSTOCK " ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 查看日结当日的昨天是否日结
   */
  public TParm yesterdayTranDate(String yesterday){
	  String sql = "SELECT  TRANDATE  FROM INV_DDSTOCK WHERE TRANDATE ='"+yesterday+"'" ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
      return result;
  }
  /**
   * 结算数据与发票xml对比
   * @param parm
   * @return
   */
  public TParm getCompareData(TParm parm){
	  String sql=" SELECT A.INV_CODE,A.INV_CHN_DESC,A.DESCRIPTION,A.STOCK_UNIT," +
	  		     "  B.CQTY,C.DQTY,B.CQTY-C.DQTY D_VALUE FROM" +
	  		     " INV_BASE A ,(SELECT INV_CODE,SUM(QTY) CQTY FROM INV_VERIFYIND WHERE " +
	  		     " VERIFYIN_NO = '"+parm.getValue("VERIFYIN_NO")+"' GROUP BY INV_CODE ) B ," +
	  		     " ( SELECT INV_CODE,SUM(TOTAL_OUT_QTY) DQTY FROM INV_ACCOUNT " +
	  		     " WHERE CLOSE_DATE = '"+parm.getValue("CLOSE_DATE")+"' GROUP BY INV_CODE) C " +
	  		     " WHERE A.INV_CODE = B.INV_CODE " +
	  		     " AND   A.INV_CODE = C.INV_CODE " +
	  		     " AND B.CQTY-C.DQTY <> '0' "  ;
	  System.out.println("sql=结算数据与发票xml对比==="+sql);
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql)) ;
	  if (result.getErrCode() < 0) {
          err(result.getErrCode() + " " + result.getErrText());
          return result;
      }
	  return result ;
  }
  /**
   * 重新日结 ，一天的验收入库量
   */
  public TParm againInVerifyin(TParm parm){
	 
	  //一天的入库量 根据科室分类，一天的入库量
	  String sql = " SELECT SUM(B.QTY) QTY,B.UNIT_PRICE,B.INV_CODE,B.UNIT_PRICE*QTY AMT FROM" +
	  		       " INV_VERIFYINM A,INV_VERIFYIND B " +
	  		       " WHERE A.CHECK_FLG = 'Y' " +
	  		       " AND A.VERIFYIN_DATE BETWEEN TO_DATE ('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS')" +
	  		       " AND TO_DATE ('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')" +
	  		       " AND A.VERIFYIN_DEPT = '"+parm.getValue("ORG_CODE")+"'" +
	  		       " AND B.INV_CODE = '"+parm.getValue("INV_CODE")+"'" +
	  		       " GROUP BY B.INV_CODE,B.UNIT_PRICE,B.UNIT_PRICE*QTY " ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql));
	  if(result.getErrCode()<0){
		  err(result.getErrCode() + " " + result.getErrText());
          return result;
	  }
	  TParm resultParm = new TParm() ;
	 if(result.getCount()<0){
		 resultParm.setData("QTY", 0) ; 
		 resultParm.setData("UNIT_PRICE", 0) ; 
		 resultParm.setData("AMT", 0) ; 
		 return resultParm ;
	 }
	 return result.getRow(0) ;
  } 
  /**
   * 重新日结 ，计入病患数量 
   */
  public TParm againPatRecord(TParm parm){
	 
	  //一天的入库量 根据科室分类，一天的入库量
	  String sql = " SELECT SUM(QTY) QTY  FROM" +
	  		       " SPC_INV_RECORD " +
	  		       " WHERE BILL_FLG = 'Y' " +
	  		       " AND BILL_DATE BETWEEN TO_DATE ('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS')" +
	  		       " AND TO_DATE ('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')" +
	  		       " AND EXE_DEPT_CODE = '"+parm.getValue("ORG_CODE")+"'" +
	  		       " AND INV_CODE = '"+parm.getValue("INV_CODE")+"'" +
	  		     "   AND BATCH_SEQ = '"+parm.getValue("BATCH_SEQ")+"'"  ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql));
	  if(result.getErrCode()<0){
		  err(result.getErrCode() + " " + result.getErrText());
          return result;
	  }
	  TParm resultParm = new TParm() ;
	 if(result.getCount()<0){
		 resultParm.setData("QTY", 0) ; 
		 resultParm.setData("UNIT_PRICE", 0) ; 
		 resultParm.setData("AMT", 0) ; 
		 return resultParm ;
	 }
	 return result.getRow(0) ;
  } 
  /**
   * 重新日结 ，昨天的库存量
   */
  public TParm againDDstockQty(TParm parm){
	 
	  //一天的入库量 根据科室分类，一天的入库量
	  String sql = " SELECT DD_STOCK_QTY,DD_STOCK_AMT,DD_STOCK_PRICE FROM" +
	  		       " INV_DDSTOCK " +
	  		       " WHERE TRANDATE = '"+parm.getValue("YESTERDAY")+"' " +
	  		       " AND ORG_CODE = '"+parm.getValue("ORG_CODE")+"'" +
	  		       " AND INV_CODE = '"+parm.getValue("INV_CODE")+"'" +
	  		       " AND BATCH_SEQ = '"+parm.getValue("BATCH_SEQ")+"'" ;
	  TParm result = new TParm(TJDODBTool.getInstance().select(sql));
	  if(result.getErrCode()<0){
		  err(result.getErrCode() + " " + result.getErrText());
          return result;
	  }
	  TParm resultParm = new TParm() ;
		 if(result.getCount()<0){
			 resultParm.setData("DD_STOCK_QTY", 0) ; 
			 resultParm.setData("DD_STOCK_AMT", 0) ; 
			 return resultParm ;
		 }
	 return result.getRow(0) ;
  } 
  /**
   * 物资 重新日结作业
   * @param parm TParm
   * @param conn TConnection
   * @return TParm
   */
  public TParm onAgainInvStockBatch(TParm parm, TConnection conn) {
      // 结果集
      TParm result1 = new TParm();
      result1.setErrCode(-1) ;
      // 数据检核
      if (parm == null)
          return result1;
      TParm resutlParm = new TParm();
      //查询日库存交易档
      TParm result = this.getDayStock(parm) ;
      if (result.getErrCode() < 0) {
          return result1;
      }
      //日结批次作业
      TParm queryParm =new TParm() ;
      for (int i = 0; i < result.getCount("INV_CODE"); i++) {
          // 写入物资库存日结作业
    	  queryParm.setData("START_DATE", parm.getValue("START_DATE"));
    	  queryParm.setData("END_DATE", parm.getValue("END_DATE"));
    	  queryParm.setData("ORG_CODE", result.getData("ORG_CODE",i));
    	  queryParm.setData("INV_CODE", result.getData("INV_CODE",i));
    	  queryParm.setData("BATCH_SEQ", result.getData("BATCH_SEQ",i));
    	  queryParm.setData("YESTERDAY",  parm.getValue("YESTERDAY"));
    	   //验收入库数据
    	  TParm verifyinParm = this.againInVerifyin(queryParm) ; 
    	  //计入病患数据
    	  //出库数据 就是出智能柜
    	  //昨日库存数据
    	  TParm ddstockParm = this.againDDstockQty(queryParm);
    	  //根据昨日得到今日的当前库存，last_stock_qty + in_qty - out_qty + modi_qty
    	  //昨日库存量
    	  int  last_stock_qty = ddstockParm.getInt("DD_STOCK_QTY") ;
    	  //进货量
    	  int in_qty = verifyinParm.getInt("QTY") ;
    	  //出货量
    	  int  out_qty = 0 ;
    	  //盘点量
    	  int modi_qty = 0 ;
    	  //当前库存量
    	  int stock_qty  = last_stock_qty + in_qty - out_qty + modi_qty ;
    	  //单价
    	  double price = result.getDouble("UNIT_PRICE") ;
          TParm inparm = new TParm();
          inparm.setData("TRANDATE",
                         parm.getValue("START_DATE").substring(0, 8));
          inparm.setData("ORG_CODE", result.getData("ORG_CODE",i));     
          inparm.setData("INV_CODE", result.getData("INV_CODE", i));
          inparm.setData("BATCH_SEQ", result.getData("BATCH_SEQ", i));
          inparm.setData("REGION_CODE", result.getData("REGION_CODE", i));
          inparm.setData("BATCH_NO", result.getData("BATCH_NO", i));
          inparm.setData("VALID_DATE", result.getData("VALID_DATE", i));
          inparm.setData("DD_IN_QTY", verifyinParm.getInt("QTY"));
          inparm.setData("DD_OUT_QTY", out_qty);//出库量
          inparm.setData("DD_CHECKMODI_QTY", modi_qty);//盘点量
          inparm.setData("DD_STOCK_QTY", stock_qty);//当天库存量
          inparm.setData("DD_STOCK_AMT", stock_qty*price);//当天库存金额
          inparm.setData("LAST_DD_STOCK_QTY", last_stock_qty);//昨日库存量
          inparm.setData("LAST_DD_STOCK_AMT", ddstockParm.getDouble("DD_STOCK_AMT"));  //昨日库存金额
          inparm.setData("DD_VERIFYIN_QTY", verifyinParm.getInt("QTY"));
          inparm.setData("DD_VERIFYIN_AMT", verifyinParm.getDouble("AMT"));
          inparm.setData("DD_GIFTIN_QTY", 0);
          inparm.setData("DD_REGRESSGOODS_QTY",0);//日退货量
          inparm.setData("DD_REGRESSGOODS_AMT",0);//日退货金额
          inparm.setData("DD_ASSIGNIN_QTY",0);//日请领入库量
          inparm.setData("DD_ASSIGNOUT_QTY",0);//日请领出库量
          inparm.setData("DD_CHANGEIN_QTY",0);//日调拨入库量
          inparm.setData("DD_CHANGEOUT_QTY",0);//日调拨出库量
          inparm.setData("DD_TRANSMITIN_QTY", 0);//日退庫入庫量
          inparm.setData("DD_TRANSMITOUT_QTY",0);//日退庫出庫量
          inparm.setData("DD_WASTE_QTY", 0);//日內部耗損數量
          inparm.setData("DD_DISPENSE_QTY", 0);//日计入病患数量
          inparm.setData("DD_REGRESS_QTY", 0);//日计入病患退回数量
          inparm.setData("DD_STOCK_PRICE", price);
          inparm.setData("DD_NHI_PRICE", price);
          inparm.setData("DD_OWN_PRICE", price);
          inparm.setData("DD_RPP_AMT", price);
          inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
          inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
          resutlParm = this.insertDDStock(inparm, conn) ;
          if (resutlParm.getErrCode() < 0) {
              return result1;
          }
      }
      return resutlParm;
  } 
}

