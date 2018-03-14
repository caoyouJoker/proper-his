package com.javahis.ui.ins;


import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Vector;


import jdo.ins.INSADMConfirmTool;
import jdo.ins.INSCJAdvanceTool;
import jdo.ins.INSIbsOrderTool;
import jdo.ins.INSIbsTool;
import jdo.ins.INSIbsUpLoadTool;
import jdo.ins.INSTJTool;
import jdo.ins.InsManager;
import jdo.sys.CTZTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
/**
 * <p>
 * Title:住院垫付费用上传
 * Description:住院垫付费用上传
 * Copyright: Copyright (c) 2017
 * @version 1.0
 */
public class INSAdvancePayMentControl extends TControl{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
	TTable tableInfo;// 垫付病患列表
	TTable oldTable;// 明细汇总前数据
	TTable newTable;// 明细汇总后数据
	TParm regionParm;// 医保区域代码
	TTabbedPane tabbedPane;// 页签
	int selectNewRow; // 明细汇总后数据获得当前选中行
	// 排序
	private Compare compare = new Compare();
	private boolean ascending = false;
	private int sortColumn = -1;
	// 明细汇总前数据
	private String[] pagetwo = { "ORDER_CODE", "ORDER_DESC", "DOSE_DESC",
			"STANDARD", "PHAADD_FLG", "CARRY_FLG", "PRICE",
			"NHI_ORD_CLASS_CODE", "NHI_CODE_I", "OWN_PRICE", "BILL_DATE" };
	//  明细汇总后数据
	private String[] pagethree = { "SEQ_NO", "ORDER_CODE", "ORDER_DESC",
			"PRICE", "ADDPAY_FLG", "NHI_ORDER_CODE",
			"HYGIENE_TRADE_CODE", "NHI_ORD_CLASS_CODE","CHARGE_DATE" };
	
	/**
     * 初始化方法
     */
    public void onInit() {
		tableInfo = (TTable) this.getComponent("TABLEINFO");//垫付病患列表
		tabbedPane = (TTabbedPane) this.getComponent("TABBEDPANE");// 页签
		oldTable = (TTable) this.getComponent("OLD_TABLE");// 明细汇总前数据
		newTable = (TTable) this.getComponent("NEW_TABLE");// 明细汇总后数据
		setValue("YEAR_MON", SystemTool.getInstance().getDate());
		setValue("START_DATE", SystemTool.getInstance().getDate());
 	    setValue("END_DATE", SystemTool.getInstance().getDate());	 
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// 获得医保区域代码
		newTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
		"onExaCreateEditComponent");
		 //总量 列触发
        this.addEventListener("NEW_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                              "onTableChangeValue");
     // 排序监听
		addListener(newTable);
    }
	/**
	 * 校验为空方法
	 * 
	 * @param name
	 * @param message
	 */
	private void onCheck(String name, String message) {
		this.messageBox(message);
		this.grabFocus(name);
	}
	/**
	 * 查询
	 */
	public void onQuery() {
		if (null == this.getValue("START_DATE")
				|| this.getValue("START_DATE").toString().length() <= 0) {
			onCheck("START_DATE", "入院开始日期不可以为空");
			return;
		}
		if (null == this.getValue("END_DATE")
				|| this.getValue("END_DATE").toString().length() <= 0) {
			onCheck("END_DATE", "入院结束日期不可以为空");
			return;
		}
		TParm parm = new TParm();
		parm.setData("START_DATE", sdf.format(this.getValue("START_DATE")));// 入院开始时间
		parm.setData("END_DATE", sdf.format(this.getValue("END_DATE"))); // 入院结束时间
		String sql1 ="";
	    String sql2 ="";
	    if(this.getValue("MR_NO").toString().length()>0)
			sql1 = " AND A.MR_NO = '"+ getValue("MR_NO") + "'";
		if(!this.getValue("STATUS_FLG").equals(""))	
			sql2 = " AND A.STATUS_FLG = '"+ getValue("STATUS_FLG") + "'";	
		String SQL = " SELECT A.YEAR_MON,A.CONFIRM_NO, A.MR_NO, A.CASE_NO, A.ID_NO, A.PAT_NAME, " +
				     " CASE A.SEX_CODE  WHEN '1' THEN '男' WHEN '2' THEN '女' " +
				     " ELSE '' END AS SEX_DESC,A.COMPANY_NAME,(SELECT S.CTZ_DESC FROM SYS_CTZ S " +
				     " WHERE S.NHI_NO = A.CTZ_CODE) AS CTZ_DESC,A.IN_DATE, A.DS_DATE," +
				     " A.TOTAL_AMT, A.INV_NO,CASE A.STATUS_FLG  WHEN '1' THEN '已下载' "+
				     " WHEN '2' THEN '已上传' WHEN '3' THEN '已撤销' WHEN '4' THEN '已对账' " +
				     " ELSE '' END AS STATUS_FLG,A.UPLOAD_DATE,A.SPECIAL_SITUATION " +
				     " FROM INS_ADVANCE_PAYMENT A "+ 
				     " WHERE A.IN_DATE BETWEEN TO_DATE " +
				     " ('"+ parm.getValue("START_DATE")+"000000"+"','YYYYMMDDhh24miss')"+  
				     " AND TO_DATE ('"+ parm.getValue("END_DATE")+"235959"+"', 'YYYYMMDDhh24miss')" +
				     sql1+
				     sql2;	
//		System.out.println("SQL=====:"+SQL);
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL));	
//		System.out.println("result=====:"+result);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// 执行失败
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("没有查询的数据");
			tableInfo.removeRowAll();
			return;
		}
		tableInfo.setParmValue(result);
	}
	/**
	 * 查询数据(病案号查询)
	 */
	public void onQueryNO() {
		String mrno = PatTool.getInstance().checkMrno(
			TypeTool.getString(getValue("MR_NO")));
		setValue("MR_NO",mrno);
		onQuery();		
	}
		
	
	
	/**
	 * 信息下载
	 */
	public void onDownload(){
		if (null == this.getValue("YEAR_MON")
				|| this.getValue("YEAR_MON").toString().length() <= 0) {
			onCheck("YEAR_MON", "入院期号不可以为空");
			return;
		}
		
	   TParm downParm = new TParm();
	   TParm result = new TParm();
	   String caseNo = "";
	   String yearmon = df.format(this.getValue("START_DATE"));
//	   System.out.println("yearmon=====:"+yearmon);
	   if(this.getValue("CASE_NO").toString().length()>0)
		 caseNo = getValueString("CASE_NO");
	   downParm.addData("HOSP_NHI_NO",regionParm.getData("NHI_NO", 0).toString());//医院编码
	   downParm.addData("CASE_NO", caseNo);//住院号
	   downParm.addData("BEGIN_DATE", yearmon);//入院期号YYYYMM
	   downParm.addData("PARM_COUNT", 3);//入参数量       	   	   
	   downParm.setData("PIPELINE", "DataDown_czyd");
	   downParm.setData("PLOT_TYPE", "M");	  	    
       System.out.println("downParm:"+downParm);
       result = InsManager.getInstance().safe(downParm,"");
//       System.out.println("result:============"+result);
       if (result.getErrCode() < 0) {	        	
     	    this.messageBox(result.getErrText());
			return;
       }else{
    	//在插入INS_ADVANCE_PAYMENT表之前查询是否有已下载数据，若有则删除
    	 String sql1 = "";
    	 if(this.getValue("CASE_NO").toString().length()>0)
    		 sql1 = " AND CASE_NO = '"+ caseNo + "'"; 
    	 
    	String sql = " SELECT * FROM INS_ADVANCE_PAYMENT"+
    	             " WHERE YEAR_MON = '"+ yearmon + "'"+
    	             sql1;
    	TParm data = new TParm(TJDODBTool.getInstance().select(sql));
//    	 System.out.println("data:============count"+data.getCount());
   		if(data.getErrCode()<0){
   		    this.messageBox(data.getErrText());
   			return;
   		}
   		//可否重复下载
   		if(data.getCount()<=0){
   			result.setData("YEAR_MON", yearmon);//入院期号
   			result.setData("CASE_NO_FLG", caseNo);//判断是否是一笔数据
   			result.setData("OPT_USER", Operator.getID());
   			result.setData("OPT_TERM", Operator.getIP());
	        this.insertAdvancePayment(result);
	        messageBox("下载成功");
   		}else{
   		   //删除再下载
//   			String sqldel = " DELETE FROM INS_ADVANCE_PAYMENT"+
//            " WHERE YEAR_MON = '"+ yearmon + "'"+
//            sql1;
//            TParm datadel = new TParm(TJDODBTool.getInstance().update(sqldel));
//            if(datadel.getErrCode()<0){
//       		    this.messageBox(datadel.getErrText());
//       			return;
//       		}
// 			result.setData("YEAR_MON", yearmon);//入院期号
//   			result.setData("CASE_NO_FLG", caseNo);//判断是否是一笔数据
//   			result.setData("OPT_USER", Operator.getID());
//   			result.setData("OPT_TERM", Operator.getIP());
//	        this.insertAdvancePayment(result); 
//	        messageBox("下载成功");   			
   		    messageBox("已下载,不能再下载");  				
   		    return;
   		}	      
     }
	}
	/**
	 * 插入INS_ADVANCE_PAYMENT
	 * @param parm
	 */
	public void insertAdvancePayment(TParm parm){
//		 System.out.println("parm=====:"+parm);
		 TParm result = new TParm();
		for (int i = 0; i < parm.getCount("CONFIRM_NO"); i++){
			String sqldel = " SELECT A.MR_NO FROM ADM_INP A " +
					" WHERE A.CASE_NO = '" + parm.getValue("CASE_NO",i) + "'";
            TParm datadel = new TParm(TJDODBTool.getInstance().select(sqldel));
            String mrNo = datadel.getValue("MR_NO", 0);
//            System.out.println("mrNo=====:"+mrNo);
    		String indate = parm.getValue("IN_HOSP_DATE",i); //入院时间
    		String dsdate = parm.getValue("OUT_HOSP_DATE",i); //出院时间
//    		System.out.println("indate=====:"+indate);
//			System.out.println("dsdate=====:"+dsdate);
    		String	sql = " INSERT INTO INS_ADVANCE_PAYMENT(YEAR_MON,CONFIRM_NO,CASE_NO,MR_NO," +
            " ID_NO,PAT_NAME,SEX_CODE ,COMPANY_NAME,CTZ_CODE,IN_DATE,DS_DATE," +
            " TOTAL_AMT,STATUS_FLG,OPT_USER,OPT_DATE,OPT_TERM)" +
            " VALUES('"+ parm.getValue("YEAR_MON")+ "', " + 
            "'" + parm.getValue("CONFIRM_NO",i) + "', " +
            "'" + parm.getValue("CASE_NO",i) + "', " +
            "'" + mrNo + "', " +
            "'" + parm.getValue("SID",i) + "', " +
            "'" + parm.getValue("NAME",i) + "', " +
            "'" + parm.getValue("SEX_CODE",i) + "', " +
            "'" + parm.getValue("WORK_DEPARTMENT",i) + "', " +
            "'" + parm.getValue("CTZ_CODE",i) + "', " +
            " TO_DATE('" + indate + "','YYYYMMDDHH24MISS'), " +
            " TO_DATE('" + dsdate + "','YYYYMMDDHH24MISS'), " +
            " "+ parm.getDouble("TOTAL_AMT",i)+ "," +
            " '1',"+
            "'" + parm.getValue("OPT_USER")+ "',"+
            "SYSDATE,"+
            "'" + parm.getValue("OPT_TERM")+ "'"+
            ")";
//	      System.out.println("sql============"+sql);
	       result = new TParm(TJDODBTool.getInstance().update(sql));
			if(result.getErrCode()<0){
				messageBox(result.getErrText());
				return;
			}
		}
		onReQuery(parm);
	}
	/**
	 * 查询数据
	 */
	public void onReQuery(TParm parm) {	
		String sql1 = "";
		 String yearmon = parm.getValue("YEAR_MON") ;
		 String caseNo = parm.getValue("CASE_NO_FLG");
		if(caseNo.length()>0)
   		 sql1 = " AND CASE_NO = '"+ caseNo + "'";   	 
   	    String sql = " SELECT A.YEAR_MON,A.CONFIRM_NO, A.MR_NO, A.CASE_NO, A.ID_NO, A.PAT_NAME, " +
                 " CASE A.SEX_CODE  WHEN '1' THEN '男' WHEN '2' THEN '女' " +
                 " ELSE '' END AS SEX_DESC,A.COMPANY_NAME,(SELECT S.CTZ_DESC FROM SYS_CTZ S " +
                 " WHERE S.NHI_NO = A.CTZ_CODE) AS CTZ_DESC,A.IN_DATE, A.DS_DATE," +
                 " A.TOTAL_AMT, A.INV_NO,CASE A.STATUS_FLG  WHEN '1' THEN '已下载' "+
                 " WHEN '2' THEN '已上传' WHEN '3' THEN '已撤销' WHEN '4' THEN '已对账' " +
                 " ELSE '' END AS STATUS_FLG,A.UPLOAD_DATE,A.SPECIAL_SITUATION " +
                 " FROM INS_ADVANCE_PAYMENT A "+
   	             " WHERE YEAR_MON = '"+ yearmon + "'"+
   	             sql1;
     	TParm data = new TParm(TJDODBTool.getInstance().select(sql));
  		if(data.getErrCode()<0){
  		    this.messageBox(data.getErrText());
  			return;
  		}
  		tableInfo.setParmValue(data);
	}

	/**
	 * 明细汇总
	 */
	public void onSumdetail(){
		TParm parm = getTableSeleted();	
		if (null == parm) {
			return;
		}
		parm.setData("REGION_CODE", Operator.getRegion()); // 医院代码
		parm.setData("NHIHOSP_NO",regionParm.getData("NHI_NO", 0).toString());//医院编码
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		String endDate = sdf.format(SystemTool.getInstance().getDate());
//		System.out.println("endDate============"+endDate);
		parm.setData("END_DATE", endDate); // 现在时间
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSBalanceAction", "onExeAdvance", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("执行失败:"+result.getErrText());
			return;
		} 
	
		this.messageBox("汇总成功");
	}
	/**
	 * 校验是否有获得焦点
	 * 
	 * @return TParm
	 */
	private TParm getTableSeleted() {
		int row = tableInfo.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择要执行的数据");
			tabbedPane.setSelectedIndex(0);
			return null;
		}
		TParm parm = tableInfo.getParmValue().getRow(row);
		parm.setData("YEAR_MON", parm.getValue("YEAR_MON"));//期号
		parm.setData("CASE_NO", parm.getValue("CASE_NO")); // 住院号
		parm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); //垫付住院顺序号
		parm.setData("START_DATE", 
				parm.getValue("IN_DATE").replace("-", "").substring(0,8));//开始时间
		parm.setData("MR_NO", parm.getValue("MR_NO"));
//		System.out.println("parm============"+parm);
		return parm;
	}
	
	/**
	 * 费用上传
	 */
	public void onUpload(){	
		//是否不用传票据号(测试)
//		if (null == this.getValue("INV_NO")
//				|| this.getValue("INV_NO").toString().length() <= 0) {
//			onCheck("INV_NO", "票据号不可以为空");
//			return;
//		}
		if(!this.getRadioButton("NEW_RDO_1").isSelected()){
			this.messageBox("请在全部下上传");
			return;
		}
			
		TParm parm = getTableSeleted();
		if (parm == null) {
			return;
		}
		//执行费用撤销
		this.DataDown_czys_U(parm);
		//执行费用上传
		if(this.DataUpload_H(parm).getErrCode() < 0)
	       return;	   
		else{
	     //上传费用特别情况说明	
	    this.DataDown_czys_T(parm);
	    this.messageBox("上传成功");
		}      
	}
	 /**
     * 执行费用上传
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUpload_H(TParm parm) {
		TParm tableParm = null;
		TParm newParm = new TParm(); // 上传数据
		TParm result = new TParm();
		TParm parmValue = newTable.getParmValue(); // 获得明细汇总后数据
		for (int i = 0; i < parmValue.getCount(); i++) {
			tableParm = parmValue.getRow(i);
			String nhiOrderCode = tableParm.getValue("NHI_ORDER_CODE");
			//去除合计行
			if (nhiOrderCode.equals("")) {// 医保号码
				continue;
			}
			newParm.addData("CONFIRM_NO", tableParm.getValue("CONFIRM_NO"));//垫付住院顺序号
			newParm.addData("HOSP_NHI_NO",regionParm.getData("NHI_NO", 0).toString());//医院编码
			String chargedate  = tableParm.getValue("CHARGE_DATE").replace("/", "-")+" 00:00:00";
//	 		System.out.println("chargedate============"+chargedate);	 		
			newParm.addData("CHARGE_DATE", chargedate); // 明细录入时间
			newParm.addData("SEQ_NO", tableParm.getValue("SEQ_NO"));//序号
			newParm.addData("NHI_CODE", tableParm.getValue("NHI_ORDER_CODE"));//三目医保编码
			newParm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC"));//医嘱名称
			newParm.addData("PRICE", tableParm.getDouble("PRICE"));//单价
			newParm.addData("QTY", tableParm.getInt("QTY"));//数量
			newParm.addData("TOTAL_AMT", tableParm.getDouble("TOTAL_AMT"));//总金额          
            newParm.addData("ADD_FLG", 
            		tableParm.getValue("ADDPAY_FLG").equals("Y")? "1" : "0");//累计增付标志
            newParm.addData("PZWH", tableParm.getValue("HYGIENE_TRADE_CODE"));//批准文号	
            newParm.addData("PRINT_NO", this.getValue("INV_NO"));//医保专用票据号
            newParm.addData("PARM_COUNT", 12);//入参数量   
		}
            newParm.setData("PIPELINE", "DataUpload");
            newParm.setData("PLOT_TYPE", "H");	  	    
//            System.out.println("newParm:====="+newParm);
            result = InsManager.getInstance().safe(newParm);
//            System.out.println("result:====="+result);
         if (result.getErrCode() < 0) {	        	
      	    //执行费用撤销 
      	    this.DataDown_czys_U(parm);  
      	    this.messageBox("上传失败");
 			return result; 
            }else{
		   //更新INS_ADVANCE_PAYMENT表状态 2 已上传 、票据号和上传时间
		 String sql1 = " UPDATE INS_ADVANCE_PAYMENT " +
             " SET STATUS_FLG = '2'," +
             " INV_NO = '" + this.getValue("INV_NO") + "'," +
             " UPLOAD_DATE = SYSDATE" +
             " WHERE CONFIRM_NO = '" + parm.getData("CONFIRM_NO") + "' ";
		 result = new TParm(TJDODBTool.getInstance().update(sql1));	
		 if (result.getErrCode() < 0) {
             this.messageBox(result.getErrText());
             return result;
         }
		 //更新INS_IBS_UPLOAD_ADVANCE表票据号
		 String sql2 = " UPDATE INS_IBS_UPLOAD_ADVANCE " +
         " SET INV_NO = '" + this.getValue("INV_NO") + "'" +
         " WHERE CONFIRM_NO = '" + parm.getData("CONFIRM_NO") + "' ";
	     result = new TParm(TJDODBTool.getInstance().update(sql2)); 
	       if (result.getErrCode() < 0) {
               this.messageBox(result.getErrText());
               return result;
       }		 
    }
         return result;
 }
    /**
     * 城乡垫付特殊情况上传
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_czys_T(TParm parm) {  	
    	TParm result = new TParm();
    	//上传费用特别情况说明
		String specialSitu = this.getValueString("SPECIAL_SITUATION");
	    if(specialSitu.length()>0){
	    parm.setData("SPECIAL_SITUATION",specialSitu);    
        TParm specialParm = new TParm();
        specialParm.addData("CONFIRM_NO",parm.getValue("CONFIRM_NO"));//垫付住院顺序号
        specialParm.addData("HOSP_NHI_NO", 
        		regionParm.getData("NHI_NO", 0).toString());//医院编码
        specialParm.addData("SPECIAL_SITUATION", 
        		parm.getValue("SPECIAL_SITUATION"));//特殊情况说明 
        specialParm.addData("PARM_COUNT", 3);//入参数量   
        specialParm.setData("PIPELINE", "DataDown_czys");
        specialParm.setData("PLOT_TYPE", "T");       
        result = InsManager.getInstance().safe(specialParm);
//        System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        //更新INS_ADVANCE_PAYMENT特殊情况
        String sql2 =
            " UPDATE INS_ADVANCE_PAYMENT " +
            " SET SPECIAL_SITUATION = '" + parm.getData("SPECIAL_SITUATION") + "' " +
            " WHERE CONFIRM_NO = '" + parm.getData("CONFIRM_NO") + "' ";
//        System.out.println("sql2=======" + sql2);
        result = new TParm(TJDODBTool.getInstance().update(sql2));	        
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }      
    }
	    return result;
  }
	
	/**
	 * 费用撤销
	 */
	public void onCancel(){
		TParm parm = getTableSeleted();
		if (parm == null) {
			return;
		}
		if(this.DataDown_czys_U(parm).getErrCode() < 0){
		   messageBox("撤销失败");
		   return;
		}
		else{
	    //更新INS_ADVANCE_PAYMENT状态 3已撤销
	     String sql = " UPDATE INS_ADVANCE_PAYMENT " +
		            " SET STATUS_FLG = '3' " +
		            " WHERE CONFIRM_NO = '" + parm.getData("CONFIRM_NO") + "' ";
//		 System.out.println("sql=======" + sql);
		 TParm  result = new TParm(TJDODBTool.getInstance().update(sql));
		 if (result.getErrCode() < 0) {
	            this.messageBox(result.getErrText());
	            return;
	        }      
		 messageBox("撤销成功");
		}
	}
	 /**
     * 垫付撤销
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_czys_U(TParm parm) {
        TParm result = new TParm();
        TParm cancelParm = new TParm(); // 撤销数据
        cancelParm.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));//垫付住院顺序号
        cancelParm.addData("HOSP_NHI_NO", 
        		regionParm.getData("NHI_NO", 0).toString());//医院编码
        cancelParm.addData("PARM_COUNT", 2);//入参数量   
        cancelParm.setData("PIPELINE", "DataDown_czys");
        cancelParm.setData("PLOT_TYPE", "U");      
        result = InsManager.getInstance().safe(cancelParm);
//        System.out.println("result垫付撤销" + result);
//        if (result.getErrCode() < 0) {
//            this.messageBox(result.getErrText());
//            return result;
//        }       
        return result;
    }
	/**
	 * 清空
	 */
	public void onClear(){
		this.setValue("YEAR_MON", SystemTool.getInstance().getDate());
		this.setValue("CASE_NO", "");	
		this.setValue("INV_NO", "");
		this.setValue("MR_NO", "");
		this.setValue("STATUS_FLG", "");
		this.setValue("SPECIAL_SITUATION", "");
		tableInfo.removeRowAll();
		oldTable.acceptText();
		oldTable.setDSValue();
		oldTable.removeRowAll();
		newTable.acceptText();
		newTable.setDSValue();
		newTable.removeRowAll();
		tabbedPane.setSelectedIndex(0); // 第一个页签
		clearValue("SUM_AMT;NEW_SUM_AMT");
	}
	/**
	 * 页签点击事件
	 */
	public void onChangeTab() {
		switch (tabbedPane.getSelectedIndex()) {
		// 1 :明细汇总前页签 2：明细汇总后页签
		case 1:
			onSplitOld();
			break;
		case 2:
			onSplitNew();
			break;
		}
	}
	/**
	 * 明细汇总前数据
	 */
	public void onSplitOld() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		// 统计代码查询：01 药品费，02 检查费，03 治疗费，04手术费，
		//05床位费，06材料费，07其他费，08全血费，09成分血费
		for (int i = 1; i <= 10; i++) {
			if (this.getRadioButton("OLD_RDO_" + i).isSelected()) {
				if (i != 1) {
					parm.setData("NHI_ORD_CLASS_CODE", this.getRadioButton(
							"OLD_RDO_" + i).getName());
					break;
				}
			}
		}
		TParm result = INSIbsOrderTool.getInstance().queryOldSplit(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (result.getCount() <= 0) {
			oldTable.acceptText();
			oldTable.setDSValue();
			oldTable.removeRowAll();
			return;
			}		
		double qty = 0.00; // 数量
		double totalAmt = 0.00; // 发生金额

		for (int i = 0; i < result.getCount(); i++) {
			qty += result.getDouble("QTY", i);
			totalAmt += result.getDouble("TOTAL_AMT", i);
		}

		// //添加合计
		for (int i = 0; i < pagetwo.length; i++) {
			if (i == 0) {
				result.addData(pagetwo[i], "合计:");
				continue;
			}
			result.addData(pagetwo[i], "");
		}
		result.addData("QTY", qty);
		result.addData("TOTAL_AMT", totalAmt);
		result.setCount(result.getCount() + 1);
		oldTable.setParmValue(result);
		this.setValue("SUM_AMT", totalAmt); // 添加总金额
	}
	/**
	 * 明细汇总后数据
	 */
	public void onSplitNew() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		// 统计代码查询：01 药品费，02 检查费，03 治疗费，04手术费，
		//05床位费，06材料费，07其他费，08全血费，09成分血费
		for (int i = 1; i <= 10; i++) {
			if (this.getRadioButton("NEW_RDO_" + i).isSelected()) {
				if (i != 1) {
					parm.setData("NHI_ORD_CLASS_CODE", this.getRadioButton(
							"NEW_RDO_" + i).getName());
					break;
				}
				else {
					parm.setData("NHI_ORD_CLASS_CODE","");	
				}
			}
		}
		String sql1 = "";
		if(parm.getValue("NHI_ORD_CLASS_CODE").length()>0)
		sql1 = " AND A.NHI_ORD_CLASS_CODE ='" + parm.getData("NHI_ORD_CLASS_CODE") + "'";
		//获得明细汇总后数据
		 String sql = " SELECT A.SEQ_NO,A.ORDER_CODE,A.ORDER_DESC," +
		 " A.PRICE,A.QTY,A.TOTAL_AMT,A.ADDPAY_FLG," +
		 " A.NHI_ORDER_CODE,A.HYGIENE_TRADE_CODE,A.NHI_ORD_CLASS_CODE," +
		 " TO_CHAR(A.CHARGE_DATE,'YYYY/MM/DD') AS CHARGE_DATE,A.CONFIRM_NO,'N' AS FLG" +
		 " FROM INS_IBS_UPLOAD_ADVANCE A " +
		 " WHERE A.CONFIRM_NO = '" + parm.getData("CONFIRM_NO") + "'" +
		 " AND A.TOTAL_AMT <> 0" +
		 sql1 +
		 " ORDER BY A.SEQ_NO";
		 TParm upLoadParmOne = new TParm(TJDODBTool.getInstance().select(sql));
		if (upLoadParmOne.getErrCode() < 0) {
			this.messageBox("E0005"); // 执行失败
			return;
		}
		
		if (upLoadParmOne.getCount() == 0) {
			newTable.acceptText();
			newTable.setDSValue();
			newTable.removeRowAll();
			return;
			}
		double qty = 0.00; // 个数
		double totalAmt = 0.00; // 发生金额
		for (int i = 0; i < upLoadParmOne.getCount(); i++) {
			qty += upLoadParmOne.getDouble("QTY", i);
			totalAmt += upLoadParmOne.getDouble("TOTAL_AMT", i);
		}

		// //添加合计
		for (int i = 0; i < pagethree.length; i++) {
			if (i == 1) {
				upLoadParmOne.addData(pagethree[i], "合计:");
				continue;
			}
			upLoadParmOne.addData(pagethree[i], "");
		}
		upLoadParmOne.addData("QTY", qty);
		upLoadParmOne.addData("TOTAL_AMT", totalAmt);
		upLoadParmOne.addData("CONFIRM_NO", "");// 垫付住院顺序号
		upLoadParmOne.addData("FLG", ""); // 新增操作
		upLoadParmOne.addData("HYGIENE_TRADE_CODE", ""); //批文准号
		upLoadParmOne.addData("CHARGE_DATE", "");
		upLoadParmOne.addData("ADDPAY_FLG", "");//累计增负标志
		upLoadParmOne.setCount(upLoadParmOne.getCount() + 1);
		// 添加合计
		newTable.setParmValue(upLoadParmOne);
		this.setValue("NEW_SUM_AMT", totalAmt); // 总金额显示
		callFunction("UI|upload|setEnabled", true);
	}
	/**
	 * 获得单选控件
	 * 
	 * @param name
	 *            String
	 * @return TRadioButton
	 */
	private TRadioButton getRadioButton(String name) {
		return (TRadioButton) this.getComponent(name);
	}
	/**
	 * 明细汇总后数据保存操作
	 */
	public void onSave() {
		TParm parm = newTable.getParmValue();
		if (parm.getCount() <= 0) {
			this.messageBox("没有需要保存的数据");
			return;
		}
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion()); // 区域代码
		// 执行添加INS_IBS_UPLOAD_ADVANCE表操作
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSBalanceAction", "updateUpLoadAdvance", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
			onSplitNew();
		}
	}
	/**
     * 总量列触发
     * @param obj Object
     */
    public void onTableChangeValue(Object obj) { // 数量合计数据
    	newTable.acceptText();
         TTableNode node = (TTableNode) obj;
         if (node == null) {
             return;
         }
         int row = node.getRow();        
         int column = node.getColumn();
 		// 计算当前总金额
      	double qty = 0.0;
      	 if (column == 4) {
      		qty = Double.parseDouble(String.valueOf(node.getValue()));
          } else {
         	 qty = Double.parseDouble(String.valueOf(newTable.
                      getItemData(row, "QTY")));
          }
        double price = newTable.getParmValue().getDouble("PRICE",row);
        TParm parm = getTotalAmt(qty,price);
		newTable.setItem(row, "TOTAL_AMT",parm.getValue("FEES"));
//		System.out.println("newTable=====:"+newTable.getParmValue());
    }
    /**
     * 计算总金额
     */
    public TParm getTotalAmt(double total, double ownPrice) {
        TParm parm = new TParm();
        double fees =  Math.abs(StringTool.round(total * ownPrice,2));
//    	System.out.println("fees=====:"+fees);
        parm.setData("FEES", fees);
        return parm;
    }
	/**
	 * 明细汇总后数据新建操作
	 */
	public void onNew() {
		String[] amtName = { "PRICE", "QTY", "TOTAL_AMT"};
		TParm parm = newTable.getParmValue();
//		System.out.println("parm111=======" + parm);
		TParm result = new TParm();
		// 添加一条新数据
		for (int i = 0; i < pagethree.length; i++) {
			result.setData(pagethree[i], "");
		}
		for (int j = 0; j < amtName.length; j++) {
			result.setData(amtName[j], "0.00");
		}
		result.setData("FLG", "Y"); // 新增操作
		if (parm.getCount() > 0) {
			// 获得合计数据
			result.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO",0)); // 就诊顺序号 主键		
			TParm lastParm = parm.getRow(parm.getCount() - 1);
			parm.removeRow(parm.getCount() - 1); // 移除合计
			int seqNo = -1; // 获得最大顺序号码
			for (int i = 0; i < parm.getCount(); i++) {
				if (null != parm.getValue("SEQ_NO", i)
						&& parm.getValue("SEQ_NO", i).length() > 0) {
					if (parm.getInt("SEQ_NO", i) > seqNo) {
						seqNo = parm.getInt("SEQ_NO", i);
					}
				}
			}
			result.setData("SEQ_NO", seqNo + 1); // 顺序号
			parm.setRowData(parm.getCount(), result, -1); // 添加新建的数据
			parm.setCount(parm.getCount() + 1);
			parm.setRowData(parm.getCount(), lastParm, -1); // 将合计重新放入
			parm.setCount(parm.getCount() + 1);
		} else {
			this.messageBox("没有数据不可以新建操作");
			return;
		}
		newTable.setParmValue(parm);
	}
	/**
	 * 添加SYS_FEE弹出窗口(检验检查窗口)
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onExaCreateEditComponent(Component com, int row, int column) {
		selectNewRow = row;
		// 求出当前列号
		column = newTable.getColumnModel().getColumnIndex(column);
		String columnName = newTable.getParmMap(column);
		// 医嘱 和 数量操作
		if ("ORDER_CODE".equalsIgnoreCase(columnName)
				|| "QTY".equalsIgnoreCase(columnName)) {
		} else {
			return;
		}
		if ("ORDER_CODE".equalsIgnoreCase(columnName)) {
			TTextField textfield = (TTextField) com;
			TParm parm = new TParm();
			parm.setData("RX_TYPE", ""); // 检验检查 CAT1_TYPE = LIS/RIS
			textfield.onInit();
			// 给table上的新text增加sys_fee弹出窗口
			textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
			// 给新text增加接受sys_fee弹出窗口的回传值
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popExaReturn");
		}
	}
	/**
	 * 重新赋值
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popExaReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		newTable.acceptText();
		TParm newParm = newTable.getParmValue();
		newParm
				.setData("ORDER_CODE", selectNewRow, parm
						.getValue("ORDER_CODE")); // 医嘱码
		newParm
				.setData("ORDER_DESC", selectNewRow, parm
						.getValue("ORDER_DESC")); // 医嘱名称
		newParm.setData("PRICE", selectNewRow, parm.getDouble("OWN_PRICE")); // 单价
		newParm.setData("NHI_ORDER_CODE", selectNewRow, parm
				.getValue("NHI_CODE_I")); // 医保费用代码
		newParm.setData("HYGIENE_TRADE_CODE", selectNewRow, parm
				.getValue("HYGIENE_TRADE_CODE")); //批准文号
		//累计增负标志
		 String SQL =" SELECT LJZFBZ FROM INS_RULE"+
         " WHERE SFXMBM = '"+ parm.getValue("NHI_CODE_I") + "'";
         TParm LJZF = new TParm(TJDODBTool.getInstance().select(SQL));
        if (LJZF.getCount()>0) 
        	newParm.setData("ADDPAY_FLG",selectNewRow, 
        			LJZF.getValue("LJZFBZ",0).equals("1")? "Y" : "N");
        else           	
	        newParm.setData("ADDPAY_FLG",selectNewRow, "N");
		newTable.setParmValue(newParm);
	}
	
	/**
	 * 明细汇总后数据删除操作
	 */
	public void onDel() {
		int row = newTable.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择要删除的数据");
			return;

		}
		TParm parm = newTable.getParmValue();
		if (parm.getValue("FLG", row).trim().length() <= 0) {
			this.messageBox("不可以删除合计数据");
			return;
		}
        String sql = " DELETE FROM INS_IBS_UPLOAD_ADVANCE " +
        		     " WHERE CONFIRM_NO= '" + parm.getData("CONFIRM_NO",row) + "' " +
        	         " AND SEQ_NO='" + parm.getData("SEQ_NO",row) + "'";
        TParm  result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005"); // 执行失败
			return;
		}
		this.messageBox("P0005"); // 执行成功
		onSplitNew();
	}
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 *            TTable
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = newTable.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = newTable.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * vectory转成param
	 * 
	 * @param vectorTable
	 *            Vector
	 * @param parmTable
	 *            TParm
	 * @param columnNames
	 *            String
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		newTable.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}
	/**
	 * 拿到菜单
	 * 
	 * @param tag
	 *            String
	 * @return TMenuItem
	 */
	public TMenuItem getTMenuItem(String tag) {
		return (TMenuItem) this.getComponent(tag);
	}

	/**
	 * 得到 Vector 值
	 * 
	 * @param parm
	 *            TParm
	 * @param group
	 *            String
	 * @param names
	 *            String
	 * @param size
	 *            int
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * 转换parm中的列
	 * 
	 * @param columnName
	 *            String[]
	 * @param tblColumnName
	 *            String
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}

}
