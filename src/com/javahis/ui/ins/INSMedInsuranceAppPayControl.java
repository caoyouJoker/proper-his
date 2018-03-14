package com.javahis.ui.ins;

import java.text.DecimalFormat;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.ins.INSTool;
import jdo.ins.InsManager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 门急诊医疗费申请支付表</p>
 *
 * <p>Description:门急诊医疗费申请支付表</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author lim  
 * @version 1.0
 */
public class INSMedInsuranceAppPayControl extends TControl {
	
	private TTable tTable ;
	
	private TRadioButton cz ;
	
	private TRadioButton cj ;
	
	private String nhi_no ;
	
	/**
	 * 初始化方法
	 */
	public void onInit() {
		this.tTable = (TTable) this.getComponent("TTABLE") ;
		this.cz = (TRadioButton)this.getComponent("CZ") ;
		this.cj = (TRadioButton)this.getComponent("CJ") ;
		this.cz.setValue(true) ;
		String sql = "SELECT * FROM SYS_REGION WHERE REGION_CODE = '"+Operator.getRegion()+"'" ;
		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql));
		this.nhi_no = result2.getValue("NHI_NO",0) ;
	}
	
	public void onQuery(){
		onDownload() ;
	}
	/**
	 * 下载
	 */
	public void onDownload() { 
		
		// 验证用户输入的合法性.
		String beginTime = this.getValueString("BEGIN_TIME");
		String endTime = this.getValueString("END_TIME");
		boolean czValue = this.getValueBoolean("CZ");

		if ("".equals(beginTime) || "".equals(endTime)) {
			messageBox("查询日期不可为空");
			return;
		}
		String[] beginTimeArray = beginTime.split("-");
		String newBeginTime = beginTimeArray[0].concat(beginTimeArray[1]);

		String[] endTimeArray = endTime.split("-");
		String newEndTime = endTimeArray[0].concat(endTimeArray[1]);

		if (Integer.parseInt(newBeginTime) > Integer.parseInt(newEndTime)) {
			messageBox("查询日期不符");
			return;
		}
		
		TParm parm = new TParm(); 
		if (czValue) {// 城职
			parm.setData("PIPELINE", "DataDown_sp");
			parm.setData("PLOT_TYPE", "J");
		} else {// 城居
			parm.setData("PIPELINE", "DataDown_cmtd");  
			parm.setData("PLOT_TYPE", "F");
		}
		
		parm.addData("HOSP_NHI_NO", this.nhi_no);
		parm.addData("START_CODE", newBeginTime);
		parm.addData("END_CODE", newEndTime);
		parm.addData("PARM_COUNT", 3);

		TParm resultParm = InsManager.getInstance().safe(parm,null);

		if (resultParm == null) {
			this.callFunction("UI|TTABLE|setParmValue", new TParm());
			return;
		}
		
		int count = resultParm.getCount("REPORT_CODE");
		if (count <= 0) {
			messageBox("无下载资料!");
			this.callFunction("UI|TTABLE|setParmValue", new TParm());
			return;
		}
		this.callFunction("UI|TTABLE|setParmValue", resultParm);
	}

	/**
	 * 打印
	 */
	public void onPrint(){
		int selectedRow = this.tTable.getSelectedRow() ;

		if(selectedRow<0){
			messageBox("请选中一笔报盘批号进行下载") ;
			return ;
		}
		boolean czValue = this.getValueBoolean("CZ");
		if(czValue){//城职被选中.
			proCZ(selectedRow) ;
		}else{//城居被选中.
			proCJ(selectedRow) ;
		}
	}
	
	  /**
	   * 处理城居打印
	   */
	  private void proCJ(int selectedRow)
	  {
		  String reportNo = (String) this.tTable.getValueAt_(selectedRow, 0) ;
		  String beginTime = this.getValueString("BEGIN_TIME");
		  String endTime = this.getValueString("END_TIME");		  
		  TParm parm = new TParm() ;
		  parm.setData("PIPELINE", "DataDown_cmtd") ;
		  parm.setData("PLOT_TYPE", "G") ;
		  parm.addData("REPORT_CODE", reportNo) ;
		  parm.addData("HOSP_NHI_NO", this.nhi_no) ;
		  parm.addData("PARM_COUNT", 2);
		  TParm resultParm = InsManager.getInstance().safe(parm,null) ;
		  
		  if(resultParm == null){
			  messageBox("此批号下载失败!") ;
			  return ;
		  }
		 
		 
		  resultParm.addData("REPORT_CODE", reportNo) ;
		  resultParm.addData("OPT_USER", Operator.getID()) ;
		  resultParm.addData("OPT_DATE", SystemTool.getInstance().getDate()) ;
		  resultParm.addData("OPT_TERM", Operator.getIP()) ;
		  resultParm.addData("REGION_CODE",  Operator.getRegion()) ;
		  resultParm.addData("BEGIN_DATE", beginTime) ;
		  resultParm.addData("END_DATE", endTime) ;
		  
		  TParm inputParm = new TParm() ;
		  inputParm.setData("REGION_CODE", Operator.getRegion()) ;
		  TParm result1 = INSTool.getInstance().getSysRegionInfo(inputParm) ;
	      if (result1.getErrCode() < 0) {
	    	  messageBox("数据错误 " + result1.getErrText());
	    	  return ;
	        }		  
//		  String bank = result1.getValue("BANK",0);
//		  String account = result1.getValue("ACCOUNT",0);
		  String company_name = result1.getValue("REGION_CHN_ABN",0);
		  String nhi_hosp_code =result1.getValue("NHI_NO",0);
		  
		//从汇总批号中截取结算期起止时间
		  String startDate = "" ;
		  String endDate = "" ;
		  
		  if(reportNo.length()>=23){
			  startDate = reportNo.substring(8,12)+"年"+
			  			  reportNo.substring(12,14)+"月"+
			              reportNo.substring(14,16)+"日";
			  endDate = reportNo.substring(16,20)+"年"+
		                reportNo.substring(20,22)+"月"+
		                reportNo.substring(22,24)+"日";
		  }
		  
		  int row = resultParm.getCount("CTZ1_CODE") ;
		 
		  DecimalFormat df2 = new DecimalFormat("############0.00");
		  DecimalFormat df0 = new DecimalFormat("######0");
		  
		  int BQACCOM_PERTIME=0,DROP_PERTIME=0,REFOSE_PERTIME=0;
		  double BQACCOM_AMT=0.0,DROP_AMT=0.0,REFOSE_AMT=0.0;
		  double AD_PAY_AMT_ADD=0.0;
		  double AD_PAY_AMT_REDUCE=0.0;
		  double APPLY_AMT_MZ=0.0,APPLY_AMT_MT=0.0,FLG_AGENT_AMT=0.0;
		   //民政人次，金额
//		   int mzPerTime = 0;// ,mzMzPerTime = 0;
		   double mzMtPerAmt = 0,mzMzPerAmt = 0;
		   //优抚人次，金额
//		   int yfPerTime = 0;//, yfMzPerTime = 0;
		   double yfMtPerAmt = 0,yfMzPerAmt = 0;
		   //非典人次，金额
//		   int fdPerTime = 0;//, fdMzPerTime = 0;
		   double fdMtPerAmt = 0,fdMzPerAmt = 0;
		   //联网垫付人次，金额
		   int lwdfPerTime = 0;//, lwdfMzPerTime = 0;
		   double lwdfPerAmt = 0;//,lwdfMzPerAmt = 0;
		   //调整补支付人次、调整减支付人次
		   int AD_PAY_AMT_ADD_PERTIME =0;
		   int AD_PAY_AMT_REDUCE_PERTIME=0;
		   double FUND_AMT=0.00;//基金支付金额
		   String sunCode = "" ;
		   for(int i=0; i<row; i++){
			   sunCode = resultParm.getValue("SUN_CODE", i).trim() ;//汇总类别现参数为  05 基本医疗  10 民政救助  11 优抚补助  
			                                                         //12 非典补助
			   BQACCOM_PERTIME+=Integer.valueOf(resultParm.getValue("BQACCOM_PERTIME", i));
	           DROP_PERTIME+=Integer.valueOf(resultParm.getValue("DROP_PERTIME", i));
	           REFOSE_PERTIME+=Integer.valueOf(resultParm.getValue("REFOSE_PERTIME", i));
	           
	           BQACCOM_AMT+=resultParm.getDouble("BQACCOM_AMT", i);
	           DROP_AMT+=resultParm.getDouble("DROP_AMT", i);
	           REFOSE_AMT+=resultParm.getDouble("REFOSE_AMT", i);
	           
	           AD_PAY_AMT_ADD+=resultParm.getDouble("AD_PAY_AMT_ADD",i);
	           AD_PAY_AMT_REDUCE+=resultParm.getDouble("AD_PAY_AMT_REDUCE",i);
	           FLG_AGENT_AMT+=resultParm.getDouble("FLG_AGENT_AMT",i);
			   AD_PAY_AMT_ADD_PERTIME =Integer.valueOf(resultParm.getValue("ADD_NUM", i));
	           AD_PAY_AMT_REDUCE_PERTIME =Integer.valueOf(resultParm.getValue("REDUCE_NUM", i));
	           if(resultParm.getValue("PAY_TYPE",i).equals("11")){
	        	   mzMzPerAmt  += resultParm.getDouble("MZ_AGENT_AMT",i);	
	        	   yfMzPerAmt   += resultParm.getDouble("YF_AGENT_AMT",i);
	        	   fdMzPerAmt   += resultParm.getDouble("FD_AGENT_AMT",i);
	           }else if(resultParm.getValue("PAY_TYPE",i).equals("41")){
	        	   
//	        	   mzMtPerAmt  += resultParm.getDouble("MZ_AGENT_AMT",i);
	        	   if("10".equals(sunCode)){
	        	   mzMtPerAmt  += resultParm.getDouble("FUND_AMT",i);
	        	   } 
	        	   yfMtPerAmt   += resultParm.getDouble("YF_AGENT_AMT",i);
	        	   fdMtPerAmt   += resultParm.getDouble("FD_AGENT_AMT",i);
	           }
	           lwdfPerTime += resultParm.getDouble("DF_NUM",i);
	           lwdfPerAmt += resultParm.getDouble("DF_AMT",i);
	           FUND_AMT+=resultParm.getDouble("FUND_AMT",i);
	           if("02".equals(sunCode)){//02 门急诊
	        	   if(resultParm.getValue("PAY_TYPE",i).equals("11")){
		        	   APPLY_AMT_MZ+=resultParm.getDouble("APPLY_AMT",i);
				   }else if(resultParm.getValue("PAY_TYPE",i).equals("41")){
					   APPLY_AMT_MT+=resultParm.getDouble("APPLY_AMT",i);
				   }   
	           }
			   if("05".equals(sunCode)){	  //05 基本医疗          	           
				   if(resultParm.getValue("PAY_TYPE",i).equals("11")){
		        	   APPLY_AMT_MZ+=resultParm.getDouble("APPLY_AMT",i);
				   }else if(resultParm.getValue("PAY_TYPE",i).equals("41")){
					   APPLY_AMT_MT+=resultParm.getDouble("APPLY_AMT",i);
				   }
//		           FLG_AGENT_AMT+=resultParm.getDouble("FLG_AGENT_AMT",i);
//		           mzMtPerTime +=  resultParm.getDouble("MZ_AGENT_PERTIME",i);
//		           mzMtPerAmt  += resultParm.getDouble("MZ_AGENT_AMT",i);
//		           yfMtPerTime  += resultParm.getDouble("YF_AGENT_PERTIME",i);
//		           yfMtPerAmt   += resultParm.getDouble("YF_AGENT_AMT",i);   		          
		           
			   }
//			   else if("10".equals(sunCode)){// 10 民政救助
//		           mzPerTime +=  resultParm.getDouble("MZ_AGENT_PERTIME",i);		          			   
//			   }else if("11".equals(sunCode)){//11 优抚补助
//		           yfPerTime  += resultParm.getDouble("YF_AGENT_PERTIME",i);		         
//			   }else if("12".equals(sunCode)){//12 非典补助
//				   fdPerTime  += resultParm.getDouble("FD_AGENT_PERTIME",i);     
//			   }
		   }
		   TParm reportParm = new TParm() ;
		   reportParm.setData("START_DATE","TEXT", startDate) ;//结算期起
		   reportParm.setData("END_DATE","TEXT", endDate) ;//结算期至
		   reportParm.setData("COMPANY_CODE","TEXT", nhi_hosp_code) ;//单位代码
		   reportParm.setData("ACCOUNT_NAME","TEXT", company_name) ;//开户名称
		   reportParm.setData("BANK","TEXT", "工行天津第一支行(0902)") ;//开户银行
		   reportParm.setData("ACCOUNT_NO","TEXT", "0302090209300020489") ;//开户帐号
		   reportParm.setData("REPORT_CODE","TEXT", reportNo) ;//汇总批号
		   
		   reportParm.setData("BQACCOM_PERTIME_SUM","TEXT", BQACCOM_PERTIME==0?"":df0.format(BQACCOM_PERTIME));//本期完成人次
		   reportParm.setData("BQACCOM_AMT_SUM","TEXT",      BQACCOM_AMT==0.00?"":df2.format(Math.abs(BQACCOM_AMT)));//本期完成金额
		   reportParm.setData("DROP_PERTIME_SUM","TEXT",     DROP_PERTIME==0?"":df0.format(DROP_PERTIME));//退费人次
		   reportParm.setData("DROP_AMT_SUM","TEXT",         DROP_AMT==0.00?"":df2.format(Math.abs(DROP_AMT)));//退费金额
		   reportParm.setData("REFOSE_PERTIME_SUM","TEXT",   REFOSE_PERTIME==0?"":df0.format(REFOSE_PERTIME));//拒付人次
		   reportParm.setData("REFOSE_AMT_SUM","TEXT",       REFOSE_AMT==0.00?"":df2.format(Math.abs(REFOSE_AMT)));//拒付金额
		   reportParm.setData("AD_PAY_AMT_ADD_SUM_PERTIME","TEXT", AD_PAY_AMT_ADD_PERTIME==0.00?"":df2.format(AD_PAY_AMT_ADD_PERTIME) );//调整补支付人次
		   reportParm.setData("AD_PAY_AMT_ADD_SUM","TEXT",   AD_PAY_AMT_ADD==0.00?"":df2.format(Math.abs(AD_PAY_AMT_ADD)) );//调整补支付金额
		   reportParm.setData("AD_PAY_AMT_REDUCE_SUM_PERTIME","TEXT",AD_PAY_AMT_REDUCE_PERTIME==0.00?"":df2.format(AD_PAY_AMT_REDUCE_PERTIME) );//调整减支付人次
		   reportParm.setData("AD_PAY_AMT_REDUCE_SUM","TEXT",AD_PAY_AMT_REDUCE==0.00?"":df2.format(Math.abs(AD_PAY_AMT_REDUCE)) );//调整减支付金额
		   reportParm.setData("MJZHJ_PERTIME_SUM","TEXT",   BQACCOM_PERTIME-DROP_PERTIME-REFOSE_PERTIME+AD_PAY_AMT_ADD_PERTIME-AD_PAY_AMT_REDUCE_PERTIME==0?"":
               df0.format(BQACCOM_PERTIME-DROP_PERTIME-REFOSE_PERTIME+AD_PAY_AMT_ADD_PERTIME-AD_PAY_AMT_REDUCE_PERTIME));//合计人次
		   reportParm.setData("MJZHJ_AMT_SUM","TEXT",       BQACCOM_AMT-DROP_AMT-REFOSE_AMT+AD_PAY_AMT_ADD-AD_PAY_AMT_REDUCE==0.00?"":
               df2.format(BQACCOM_AMT-DROP_AMT-REFOSE_AMT+AD_PAY_AMT_ADD-AD_PAY_AMT_REDUCE));//合计金额
		   
//		   reportParm.setData("MZRC","TEXT",mzPerTime);//民政人次
//		   reportParm.setData("MZJE","TEXT",mzMzPerAmt+mzMtPerAmt);//民政金额
//		   reportParm.setData("YFRC","TEXT",yfPerTime);//优抚人次小计
//		   reportParm.setData("YFJE","TEXT",yfMzPerAmt+yfMtPerAmt);//优抚金额小计
//		   reportParm.setData("FDRC","TEXT",fdPerTime);//非典人次小计
//		   reportParm.setData("FDJE","TEXT",fdMzPerAmt+fdMtPerAmt);//非典金额小计
		   reportParm.setData("MTLWRC","TEXT",lwdfPerTime);//门特联网垫付人次小计
     	   reportParm.setData("MTLWJE","TEXT",lwdfPerAmt);	//门特联网垫付金额小计	
     	   
		   
		   double amt = BQACCOM_AMT-DROP_AMT-REFOSE_AMT+AD_PAY_AMT_ADD-AD_PAY_AMT_REDUCE;
		   reportParm.setData("SJZFJE","TEXT",StringTool.round(APPLY_AMT_MT+APPLY_AMT_MZ+mzMzPerAmt+mzMtPerAmt+yfMzPerAmt+yfMtPerAmt+fdMzPerAmt+fdMtPerAmt, 2));//实际支付金额
		   reportParm.setData("SJZFJEDX","TEXT",StringUtil.getInstance().numberToWord(APPLY_AMT_MT+APPLY_AMT_MZ+mzMzPerAmt+mzMtPerAmt+yfMzPerAmt+yfMtPerAmt+fdMzPerAmt+fdMtPerAmt));//实际支付金额大写
		   
		   //门诊特殊病
		   reportParm.setData("TCMTJE","TEXT",APPLY_AMT_MT);//统筹门特金额
		   reportParm.setData("MZMTJE","TEXT",mzMtPerAmt);//民政门特金额
		   reportParm.setData("YFMTJE","TEXT",yfMtPerAmt);//优抚门特金额
		   reportParm.setData("FDMEJE","TEXT",fdMtPerAmt);//非典门特金额
		   
		   //门诊
		   reportParm.setData("TCMZJE","TEXT",APPLY_AMT_MZ);//统筹门诊金额
		   reportParm.setData("MZMZJE","TEXT",mzMzPerAmt);//民政门诊金额
		   reportParm.setData("YFMZJE","TEXT",yfMzPerAmt);//优抚门诊金额
		   reportParm.setData("FDMZJE","TEXT",fdMzPerAmt);//非典门诊金额
		  
		   this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSMedInsuranceAppPayCJ.jhw",reportParm);
	  }  
	  /**
	   * 处理城职打印
	   */
	  private void proCZ(int selectedRow)
	  {
		  String reportNo = (String) this.tTable.getValueAt_(selectedRow, 0) ;
		  String beginTime = this.getValueString("BEGIN_TIME");
		  String endTime = this.getValueString("END_TIME");		  
		  TParm parm = new TParm() ;
		  parm.setData("PIPELINE", "DataDown_sp") ;
		  parm.setData("PLOT_TYPE", "K") ;
		  parm.addData("REPORT_CODE", reportNo) ;
		  parm.addData("HOSP_NHI_NO", this.nhi_no) ;
		  parm.addData("PARM_COUNT", 2);
		  TParm resultParm = InsManager.getInstance().safe(parm,null);
		 
		  if (resultParm == null) {
		      this.messageBox("此批号下载失败！");
		      return;
		    }	
		  resultParm.addData("REPORT_CODE", reportNo) ;
		  resultParm.addData("OPT_USER", Operator.getID()) ;
		  resultParm.addData("OPT_DATE", SystemTool.getInstance().getDate()) ;
		  resultParm.addData("OPT_TERM", Operator.getIP()) ;
		  resultParm.addData("REGION_CODE",  Operator.getRegion()) ;
		  resultParm.addData("BEGIN_DATE", beginTime) ;
		  resultParm.addData("END_DATE", endTime) ;
		  
		    //打印申请支付表
		    printCz(resultParm);		  
	  }
	  
	  private void printCz(TParm resultParm){
		    TParm inputParm = new TParm() ;
		    inputParm.setData("REGION_CODE", Operator.getRegion()) ;
//		    TParm result1 = INSTool.getInstance().getSysRegionInfo(inputParm) ;
//	        if (result1.getErrCode() < 0) {
//	            messageBox("数据错误 " + result1.getErrText());
//	            return ;
//	        }		
		    TParm result1  = SYSRegionTool.getInstance().selectdata(Operator.getRegion());//获得医保区域代码
//		    String bank = result1.getValue("BANK",0);
//		    String acc = result1.getValue("ACCOUNT",0);
		    String hosp_name = result1.getValue("REGION_CHN_ABN",0);
		    String nhi_hosp_code =result1.getValue("NHI_NO",0);		  
		    
		    int count = resultParm.getCount("SUM_CODE");
		    
		    //汇总期号,医院编码,汇总类别,支付类别,本期完成人次,本期完成金额,回退人次,回退金额,拒付人次,拒付金额,调整补支付人次,调整补支付金额,调整减支付人次,调整减支付金额,超标金额
		    //本期发生人次,回退人次...
		    int bqfsrc = 0, htrc = 0, jfrc = 0, tzbzfrc = 0, tzjzfrc = 0,lwdfrc = 0;
		    //本期发生金额,回退金额,项目付费决算收回金额,项目付费考核预留金...
		    double bqfsje = 0, htje = 0, jfje = 0, tzbzfje = 0, tzjzfje = 0, xjje = 0,
		        cbje = 0, jjzfje = 0, grzhzf = 0, zfjehj = 0, xmffjsshje = 0, xmffkhylj = 0,lwdf = 0,lwdfje = 0;
		    //联网垫付超标
		    double lwdfcb = 0;
		    //申请支付超标
		    double sqzfcb = 0;
		    //单个汇总类别的统计
		    double bqfs = 0, ht = 0, jf = 0, tzbzf = 0, tzjzf = 0, xj = 0;
		    //个人账户
		    double grbqfs = 0, grht = 0, grjf = 0, grtzbzf = 0, grtzjzf = 0, grxj = 0,grlwdf = 0;
		    //其中: 基本,困难...
		    double jb = 0, kn = 0, kt = 0, nmg = 0, gb = 0, jc = 0, md = 0, de = 0,mz = 0,fy = 0, fd  = 0;
		    //汇总类别,支付类别
		    String hzlb = "", zflb = "";
		    double fund_amt=0.00;//基金支付金额
		    //循环计算每种汇总类别
		    for (int i = 0; i < count; i++) {
		      hzlb = resultParm.getValue("SUM_CODE", i);
		      //支付类别: 11,门诊,12,门特
		      zflb = resultParm.getValue("PAY_TYPE", i);
		      //09:大额,单独统计
		      if ("09".equals(hzlb))
		        continue;
		      //统计01-05的人数
		      //本期完成人次
		      bqfsrc += Integer.valueOf(resultParm.getValue("BQACCOM_PERTIME", i));
		      //回退人次
		      htrc += Integer.valueOf(resultParm.getValue("DROP_PERTIME", i));
		      //拒付人次
		      jfrc += Integer.valueOf(resultParm.getValue("REFOSE_PERTIME", i));
		      //调整补支付人次
		      tzbzfrc += Integer.valueOf(resultParm.getValue("TZBZ_PERTIME", i));
		      //调整减支付人次
		      tzjzfrc += Integer.valueOf(resultParm.getValue("TZBJ_PERTIME", i));
		      //联网垫付人次
		      lwdfrc += Integer.valueOf(resultParm.getValue("DF_NUM", i));
		      //基金支付
		      
		      //取单个汇总类别的金额	
		      if("01".equals(hzlb))
		      //本期完成金额
		      {
		         grbqfs = resultParm.getDouble("BQACCOM_AMT", i);
		         //回退金额
		         grht = resultParm.getDouble("DROP_AMT", i);
		         //拒付金额
		         grjf = resultParm.getDouble("REFOSE_AMT", i);
		         //调整补支付金额
		         grtzbzf = resultParm.getDouble("TZBZ_AMT", i);
		         //调整减支付金额
		         grtzjzf = resultParm.getDouble("TZBJ_AMT", i);
		         //联网垫付金额
		         grlwdf  = resultParm.getDouble("DF_AMT", i);
		         grxj = grbqfs - grht - grjf + grtzbzf - grtzjzf;
		       }else
		       {
		         bqfs = resultParm.getDouble("BQACCOM_AMT", i);
		         //回退金额
		         ht = resultParm.getDouble("DROP_AMT", i);
		         //拒付金额
		         jf = resultParm.getDouble("REFOSE_AMT", i);
		         //调整补支付金额
		         tzbzf = resultParm.getDouble("TZBZ_AMT", i);
		         //调整减支付金额
		         tzjzf = resultParm.getDouble("TZBJ_AMT", i);
		         //联网垫付金额
		         lwdf =  resultParm.getDouble("DF_AMT", i);
		         xj = bqfs - ht - jf + tzbzf - tzjzf;
		        
		       }
		      fund_amt=resultParm.getDouble("FUND_AMT", i);
		      //项目付费决算收回金额,项目付费考核预留金
		      xmffjsshje = resultParm.getDouble("XMFFJS_AMT", i);
		      xmffkhylj  = resultParm.getDouble("XMKHYL_AMT", i);
		      //统计01-05的金额
		      bqfsje += bqfs;
		      htje += ht;
		      jfje += jf;
		      tzbzfje += tzbzf;
		      tzjzfje += tzjzf;
		      //联网垫付金额
		      lwdfje += lwdf;
		      //小计金额
		      xjje += xj;
		      //申请支付超标	      
		      sqzfcb +=  resultParm.getDouble("OVER_AMT", i); 			  
		      //联网垫付超标
		      lwdfcb += resultParm.getDouble("DFOVER_AMT", i);
		      
		      
		     
		      //汇总类别: 01,个人帐户,02,门急诊,03,军残补助,04,公务员补助,05,基本,06,困难,07,困退,08,农民工,09,大额,10,民政补助
		      if ("01".equals(hzlb))
		        grzhzf += grxj;
		      else if ("02".equals(hzlb))
		        md += fund_amt;
		      else if ("03".equals(hzlb))
		        jc += fund_amt;
		      else if ("04".equals(hzlb))
		        gb += fund_amt;
		      else if ("05".equals(hzlb))
		        jb += fund_amt;
		      else if ("06".equals(hzlb))
		        kn += fund_amt;
		      else if ("07".equals(hzlb))
		        kt += fund_amt;
		      else if ("08".equals(hzlb))
		        nmg += fund_amt;
		      else if ("09".equals(hzlb))
		        de += fund_amt;
		      else if ("10".equals(hzlb))
		        mz += fund_amt;
		      else if("11".equals(hzlb))
		        fy += fund_amt;
		      else if("12".equals(hzlb))
		        fd += fund_amt;
		    }
		    //基金支付金额 = 小计 - 申请支付超标 - 项目付费决算收回金额 - 项目付费考核预留金-联网垫付金额, 支付金额合计 = 基金支付金额 + 个人账户支付
		    jjzfje = xjje - sqzfcb- xmffjsshje -xmffkhylj-lwdfje;
		    zfjehj = jjzfje + grzhzf;
		    //基本，优抚
		    double jiben = 0;
		    double youfu = 0;
		    jiben = jb + kn + kt + nmg + md + fd;
		    youfu = fy + jc;
            // 项目超标金额 = 联网垫付超标+申请支付超标
		    cbje = lwdfcb + sqzfcb;
		    
		    TParm parm = new TParm() ;
		    //表头部分
		    
			String[] beginTimeArray = resultParm.getValue("BEGIN_DATE",0).split("-");
			String newBeginTime = beginTimeArray[0].concat(beginTimeArray[1]);

			String[] endTimeArray = resultParm.getValue("END_DATE",0).split("-");
			String newEndTime = endTimeArray[0].concat(endTimeArray[1]);	
			
		    parm.setData("BEGIN_DATE","TEXT", newBeginTime);//结算期起
		    parm.setData("END_DATE","TEXT", newEndTime);//结算期止
		    parm.setData("HOSP_CODE","TEXT", nhi_hosp_code);//单位代码
		    parm.setData("HOSP_NAME","TEXT", hosp_name);//开户名称
		    parm.setData("BANK","TEXT", "工行天津第一支行(0902)");//开户银行
		    parm.setData("ACCOUNT","TEXT", "0302090209300020489");//开户帐号
		    
		    parm.setData("BQFSRC","TEXT", bqfsrc);//本期发生人次
		    parm.setData("BQFSJE","TEXT", round2(bqfsje));//本期发生金额
		    parm.setData("HTRC","TEXT", htrc);//回退人次
		    parm.setData("HTJE","TEXT", round2(htje));//回退金额
		    parm.setData("JFRC","TEXT", jfrc);//拒付人次
		    parm.setData("JFJE","TEXT", round2(jfje));//拒付金额
		    parm.setData("TZBZFRC","TEXT", tzbzfrc);//调整补支付人次
		    parm.setData("TZBZFJE","TEXT", round2(tzbzfje));//调整补支付金额
		    parm.setData("TZJZFRC","TEXT", tzjzfrc);//调整减支付人次
		    parm.setData("TZJZFJE","TEXT", round2(tzjzfje));//调整减支付金额
		    parm.setData("XJJE","TEXT", this.round2(xjje));//小计金额
		    parm.setData("LWDFRC","TEXT", lwdfrc);//联网垫付人次
		    parm.setData("LWDFJE","TEXT", round2(lwdfje));//联网垫付金额		    
		    parm.setData("CBJE","TEXT", this.round2(cbje));//超标金额
		    parm.setData("LWDFCB","TEXT", this.round2(lwdfcb));//联网垫付超标
		    parm.setData("SQZFCB","TEXT", this.round2(sqzfcb));//申请支付超标
		    parm.setData("XMFFJSSHJE","TEXT", this.round2(xmffjsshje));//项目付费决算收回金额
		    parm.setData("XMFFKHYLJ","TEXT", this.round2(xmffkhylj));//项目付费考核预留金
		    
		    parm.setData("JJZFJE1","TEXT", StringTool.round(jjzfje, 3));//基金支付金额

		    parm.setData("JJZFJE","TEXT", StringUtil.getInstance().numberToWord(StringTool.round(jjzfje, 2)));//基金支付金额

		    parm.setData("JBEN","TEXT", this.round2(jiben));//基本
		    parm.setData("GWYBZ","TEXT", this.round2(gb));//公务员补助
		    parm.setData("YFBZ","TEXT", this.round2(youfu));//优抚补助
		    parm.setData("CXJZ","TEXT", this.round2(mz));//城乡救助
		    parm.setData("GRZHZF","TEXT", StringTool.round(grzhzf, 2));//个人账户支付
		    parm.setData("ZFJEHJ","TEXT", StringTool.round(zfjehj, 2));//支付金额合计
		    parm.setData("GRZHZFDX","TEXT", StringUtil.getInstance().numberToWord(StringTool.round(grzhzf, 2)));//个人账户支付大写
		    parm.setData("ZFJEHJDX","TEXT", StringUtil.getInstance().numberToWord(StringTool.round(zfjehj, 2)));//支付金额合计大写		 
		    
		    this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSMedInsuranceAppPayCZ.jhw",parm);
	  }
	
	/**
	 * 清空
	 */
	public void onClear(){
		this.setValue("BEGIN_TIME", "") ;
		this.setValue("END_TIME", "");
		this.callFunction("UI|TTABLE|setParmValue", new TParm());
	}
	
	/**
	 * 格式化
	 */
	private String round2(double d){
		 return new DecimalFormat("###########0.00").format(StringTool.round(d, 2));
	}

	public TTable gettTable() {
		return tTable;
	}

	public void settTable(TTable tTable) {
		this.tTable = tTable;
	}

	public TRadioButton getCz() {
		return cz;
	}

	public void setCz(TRadioButton cz) {
		this.cz = cz;
	}

	public TRadioButton getCj() {
		return cj;
	}

	public void setCj(TRadioButton cj) {
		this.cj = cj;
	}

	public String getNhi_no() {
		return nhi_no;
	}

	public void setNhi_no(String nhiNo) {
		nhi_no = nhiNo;
	}
	
}
