package com.javahis.ui.ins;

import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
/**
 * <p>Title: 医保垫付对账</p>
 * <p>Description: 医保垫付对账</p>
 * @version 1.0
 */
public class INSCheckAccount_DFControl extends TControl{
	TParm regionParm;//医保区域代码
	TTable localTable;//本地数据
	TTable centerTable;//中心数据
	/**
     * 初始化方法
     */
    public void onInit() {
    	setValue("UPLOAD_DATE", SystemTool.getInstance().getDate());
    	regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// 获得医保区域代码
    	localTable = (TTable) this.getComponent("TABLE1");//本地数据
    	centerTable = (TTable) this.getComponent("TABLE2");//中心数据
    }
    /**
     * 查询
     */
    public void onQuery(){
    	//数据检核
    	if(checkdata())
		    return;  
    	 String startdate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
         "UPLOAD_DATE")), "yyyyMMdd")+"000000"; //开始日期
    	 String enddate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
         "UPLOAD_DATE")), "yyyyMMdd")+"235959"; //结束日期
    	String sql =
    		" SELECT CONFIRM_NO,PAT_NAME,ID_NO,TOTAL_AMT,UPLOAD_DATE," +
    		" CASE STATUS_FLG  WHEN '1' THEN '已下载' WHEN '2' THEN '已上传' " +
    		" WHEN '3' THEN '已撤销' WHEN '4' THEN '已对账' ELSE '' END AS STATUS_FLG "+
    		" FROM INS_ADVANCE_PAYMENT " +
    		" WHERE UPLOAD_DATE BETWEEN TO_DATE('"+startdate+"','YYYYMMDDhh24miss') " +
    		" AND TO_DATE('"+enddate+"','YYYYMMDDhh24miss') " +
    		" AND STATUS_FLG IN('2','4')";
//    	System.out.println("sql=======" + sql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		return;
    	}
    	if(result.getCount()<0){
    		messageBox("查无数据");
    	}
    	localTable.setParmValue(result);
    }
    /**
	 * 数据检核
	 */
	private boolean checkdata(){
	   	if(this.getValue("UPLOAD_DATE").equals("")){
    		this.messageBox("对账日期不能为空");
    		return true;
    	}
	    return false; 
	} 
    
    
    /**
     * 对总账
     */
    public void onCheckAll(){
    	//数据检核
    	if(checkdata())
		    return;
    	TParm localParm = localTable.getParmValue();
//    	System.out.println("localParm=======" + localParm);
    	if(localParm==null){
    		 messageBox("请先查询数据");
    		 return;
    	} 
    	int count = localParm.getCount();
//    	System.out.println("count=======" + count);
    	double totalAmt = 0.00;
    	int allTime = count;
    	for (int i = 0; i < count; i++) {
			totalAmt += localParm.getDouble("TOTAL_AMT",i);
    	}
//    	System.out.println("totalAmt=======" +  StringTool.round(totalAmt, 2));		
//		System.out.println("allTime=======" + allTime);
    	 String uploadDate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
         "UPLOAD_DATE")), "yyyyMMdd"); //对账日期  
    	String hospital =  regionParm.getData("NHI_NO", 0).toString();//获取HOSP_NHI_NO
    	TParm parm = new TParm();
    	parm.addData("HOSP_NHI_NO", hospital);//医院编码
		parm.addData("DATE", uploadDate);//对账日期
		parm.addData("TOTAL_AMT", StringTool.round(totalAmt, 2));//发生金额
		parm.addData("ALL_TIME", allTime);//总人次
		parm.addData("PARM_COUNT", 4);//入参数量
		parm.setData("PIPELINE", "DataDown_czys");
		parm.setData("PLOT_TYPE", "S");	
		TParm result = InsManager.getInstance().safe(parm);
//		System.out.println("result=======" + result);	
		 if (result.getErrCode() < 0) {	        	
	     	    this.messageBox(result.getErrText());
				return;
		 }else{
//		  System.out.println("TOTAL_AMT=======" + result.getDouble("TOTAL_AMT"));
//		  System.out.println("ALL_TIME=======" + result.getInt("ALL_TIME"));	 
		 if(StringTool.round(totalAmt, 2)==result.getDouble("TOTAL_AMT")&&
			allTime==result.getInt("ALL_TIME")){
			 String startdate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
	         "UPLOAD_DATE")), "yyyyMMdd")+"000000"; //开始日期
	    	 String enddate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
	         "UPLOAD_DATE")), "yyyyMMdd")+"235959"; //结束日期
			//更新INS_ADVANCE_PAYMENT表状态 4 已对账
			 String sql1 = " UPDATE INS_ADVANCE_PAYMENT " +
             " SET STATUS_FLG = '4'" +          
             " WHERE UPLOAD_DATE BETWEEN TO_DATE('"+startdate+"','YYYYMMDDhh24miss') " +
     		 " AND TO_DATE('"+enddate+"','YYYYMMDDhh24miss')";
		 result = new TParm(TJDODBTool.getInstance().update(sql1));	
		 if (result.getErrCode() < 0) {
             this.messageBox(result.getErrText());
             return;
         } 		 
		   messageBox("对账成功");
		   onQuery();
		 }		 
		 else if(StringTool.round(totalAmt, 2)!=result.getDouble("TOTAL_AMT")||
					allTime!=result.getInt("ALL_TIME"))
		   messageBox("发生金额或人次有问题,可对明细账"); 
			 
		 }
		
		
		
    }
    /**
     * 对明细账
     */
    public void onCheckDetailAccnt(){
    	//数据检核
    	if(checkdata())
		    return;
    	TParm localParm = localTable.getParmValue();
//    	System.out.println("localParm=======" + localParm);
    	if(localParm==null){
    		 messageBox("请先查询数据");
    		 return;
    	} 
    	TParm parm = new TParm();   
    	 String uploadDate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
         "UPLOAD_DATE")), "yyyyMMdd"); //对账日期  
    	String hospital =  regionParm.getData("NHI_NO", 0).toString();//获取HOSP_NHI_NO
		parm.addData("HOSP_NHI_NO", hospital);//医院编码
		parm.addData("DATE", uploadDate);//对账日期
		parm.addData("PARM_COUNT", 2);
	  	parm.setData("PIPELINE", "DataDown_czyd");
		parm.setData("PLOT_TYPE", "N");
		TParm result = InsManager.getInstance().safe(parm);	
//		System.out.println("result=======" + result);
		if(result.getErrCode()<0){
			messageBox(result.getErrText());
			return;
		}
		centerTable.setParmValue(result);
//		countDetail();
    }
    /**
     * 清空
     */
    public void onclear(){
    	this.setValue("UPLOAD_DATE", SystemTool.getInstance().getDate());
    	localTable.removeRowAll();
    	centerTable.removeRowAll();
    }  
    
    
    /**
     * 计算明细账差别
     */
    public void countDetail(){
    	TTable table1 = (TTable)this.getComponent("TABLE1");//TABLE1
    	TTable table2 = (TTable)this.getComponent("TABLE2");//TABLE2
    	if(table1.getParmValue()==null||table2.getParmValue()==null){
    		messageBox("对账数据不能为空");
    		return;
    	}
//    	ADM_SEQ;PAT_NAME;TOT_AMT;NHI_AMT;OWN_AMT;ADD_AMT;UPLOAD_DATE
//    	CONFIRM_NO;NAME;TOTAL_AMT;TOTAL_NHI_AMT;OWN_AMT;ADDPAY_AMT
    	TParm tableParm1 = table1.getParmValue();
    	TParm tableParm2 = table2.getParmValue();
    	TParm parm = new TParm();
    	for (int i = 0; i < table1.getRowCount(); i++) {
    	      String admSeqLocal = tableParm1.getData("ADM_SEQ", i).toString();
    	      boolean canfind = false;
    	      for(int j = 0;j < table2.getRowCount();j++){
    	        String admSeqCenter = tableParm2.getData("CONFIRM_NO", j).toString();
    	        if(!admSeqLocal.equals(admSeqCenter))
    	          continue;
    	        canfind = true;
    	        //本地金额
    	        double totAmtLocal = tableParm1.getDouble("TOT_AMT", i);//发生金额
    	        double nhiAmtLocal = tableParm1.getDouble("NHI_AMT", i);//申报金额
    	        double ownAmtLocal = tableParm1.getDouble("OWN_AMT", i);//全自费金额
    	        double addAmtLocal = tableParm1.getDouble("ADD_AMT", i);//增付金额
    	        //中心端金额
    	        double totAmtCenter = tableParm2.getDouble("TOTAL_AMT", j);//发生金额
    	        double nhiAmtCenter = tableParm2.getDouble("TOTAL_NHI_AMT", j);//申报金额
    	        double ownAmtCenter = tableParm2.getDouble("OWN_AMT", j);//全自费金额
    	        double addAmtCenter = tableParm2.getDouble("ADDPAY_AMT", j);//增付金额
    	        if(totAmtLocal != totAmtCenter ||
    	                nhiAmtLocal != nhiAmtCenter ||
    	                ownAmtLocal != ownAmtCenter ||
    	                addAmtLocal != addAmtCenter ){
    	               parm.addData("STATUS_ONE", "Y");
    	               parm.addData("STATUS_TWO", "N");
    	               parm.addData("STATUS_THREE", "N");
    	               parm.addData("ADM_SEQ",tableParm1.getData("ADM_SEQ", i));
    	               parm.addData("NAME",tableParm1.getData("PAT_NAME", i));
    	               parm.addData("TOT_AMT_LOCAL",tableParm1.getData("TOT_AMT", i));
    	               parm.addData("TOT_AMT_CENTER",tableParm2.getData("TOTAL_AMT", j));
    	               parm.addData("NHI_AMT_LOCAL",tableParm1.getData("NHI_AMT", i));
    	               parm.addData("NHI_AMT_CENTER",tableParm2.getData("TOTAL_NHI_AMT", j));
    	               parm.addData("OWN_AMT_LOCAL",tableParm1.getData("OWN_AMT", i));
    	               parm.addData("OWN_AMT_CENTER",tableParm2.getData("OWN_AMT", j));
    	               parm.addData("ADD_AMT_LOCAL",tableParm1.getData("ADD_AMT", i));
    	               parm.addData("ADD_AMT_CENTER",tableParm2.getData("ADDPAY_AMT", j));
    	             }
    	      }
    	      if(!canfind){
    	          parm.addData("STATUS_ONE", "N");
    	          parm.addData("STATUS_TWO", "Y");
    	          parm.addData("STATUS_THREE", "N");
	              parm.addData("ADM_SEQ",tableParm1.getData("ADM_SEQ", i));
	              parm.addData("NAME",tableParm1.getData("PAT_NAME", i));
    	          parm.addData("TOT_AMT_LOCAL",tableParm1.getData("TOT_AMT", i));
    	          parm.addData("TOT_AMT_CENTER",0);
    	          parm.addData("NHI_AMT_LOCAL",tableParm1.getData("NHI_AMT", i));
    	          parm.addData("NHI_AMT_CENTER",0);
    	          parm.addData("OWN_AMT_LOCAL",tableParm1.getData("OWN_AMT", i));
    	          parm.addData("OWN_AMT_CENTER",0);
    	          parm.addData("ADD_AMT_LOCAL",tableParm1.getData("ADD_AMT", i));
    	          parm.addData("ADD_AMT_CENTER",0);
    	        }
		}
//    	ADM_SEQ;PAT_NAME;TOT_AMT;NHI_AMT;OWN_AMT;ADD_AMT;UPLOAD_DATE
//    	CONFIRM_NO;NAME;TOTAL_AMT;TOTAL_NHI_AMT;OWN_AMT;ADDPAY_AMT
    	for(int i = 0;i < table2.getRowCount();i++){
    	      String confirmNoCenter = tableParm2.getData("CONFIRM_NO", i).toString();
    	      boolean canfind = false;
    	      for (int j = 0; j < table1.getRowCount(); j++) {
    	        String confirmNoLocal = tableParm1.getData("ADM_SEQ", i).toString();
    	        if (!confirmNoLocal.equals(confirmNoCenter))
    	          continue;
    	        canfind = true;
    	      }
    	      if(!canfind){
    	        parm.addData("STATUS_ONE", "N");
    	        parm.addData("STATUS_TWO", "N");
    	        parm.addData("STATUS_THREE", "Y");
    	        parm.addData("ADM_SEQ",tableParm2.getData("CONFIRM_NO", i));
    	        parm.addData("NAME",tableParm2.getData("NAME", i));
    	        parm.addData("TOT_AMT_LOCAL",0);
    	        parm.addData("TOT_AMT_CENTER",tableParm2.getData("TOTAL_AMT", i));
    	        parm.addData("NHI_AMT_LOCAL",0);
    	        parm.addData("NHI_AMT_CENTER",tableParm2.getData("TOTAL_NHI_AMT", i));
    	        parm.addData("OWN_AMT_LOCAL",0);
    	        parm.addData("OWN_AMT_CENTER",tableParm2.getData("OWN_AMT", i));
    	        parm.addData("ADD_AMT_LOCAL",0);
    	        parm.addData("ADD_AMT_CENTER",tableParm2.getData("ADDPAY_AMT", i));
    	      }
    	    }
    	    if(parm.getCount("ADM_SEQ") <= 0){
    	    	messageBox("对明细帐成功");
    	    	return;
    	    }
    	    TParm reParm = (TParm)this.openDialog(
    	            "%ROOT%\\config\\ins\\INSCheckAccount_DFDetail.x", parm);
    }
}
