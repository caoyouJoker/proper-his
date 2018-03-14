package com.javahis.ui.ins;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.ins.INSTJTool;
import jdo.ins.INSTool;
import jdo.ins.InsManager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.sun.xml.xsom.impl.Ref.Term;

/**
 * <p>Title: 门诊联网医疗费运行情况表</p>
 *
 * <p>Description:门诊联网医疗费运行情况表</p>
 *
 * <p>Copyright: Copyright (c) 2016</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangs  
 * @version 1.0
 */
public class INSOpdCostControl extends TControl {
	
	private TTable tTable ;
	
	private TRadioButton cz ;
	
	private TRadioButton cj ;
	
	private String nhi_no ;
	private String nhi_name;
	public DecimalFormat df = new DecimalFormat("##########0.00");
	/**
	 * 初始化方法
	 */
	public void onInit() {
		this.tTable = (TTable) this.getComponent("TTABLE") ;
		this.cz = (TRadioButton)this.getComponent("CZ") ;
		this.cj = (TRadioButton)this.getComponent("CJ") ;
		this.cz.setValue(true) ;
		//onQuery();// 查询table1
//		TParm parm = null;
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		this.nhi_no = regionParm.getData("NHI_NO", 0).toString();// 获取HOSP_NHI_NO
		this.nhi_name= regionParm.getData("REGION_CHN_DESC", 0).toString();// 获取REGION_CHN_DESC
	    this.setValue("BEGIN_TIME", SystemTool.getInstance().getDate());
	    
//	    this.nhi_no="000139";
//	    this.setValue("BEGIN_TIME", "2015/07");
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
//		String endTime = this.getValueString("END_TIME");
		boolean czValue = this.getValueBoolean("CZ");

		if ("".equals(beginTime)) {
			messageBox("期号不可为空");
			return;
		}
		String[] beginTimeArray = beginTime.split("-");
		String newBeginTime = beginTimeArray[0].concat(beginTimeArray[1]);

//		String[] endTimeArray = endTime.split("-");
//		String newEndTime = endTimeArray[0].concat(endTimeArray[1]);

//		if (Integer.parseInt(newBeginTime) > Integer.parseInt(newEndTime)) {
//			messageBox("查询日期不符");
//			return;
//		}
		
		TParm parm = new TParm(); 
		parm.setData("PIPELINE", "DataDown_zjkd");
		parm.setData("PLOT_TYPE", "N");
        parm.addData("NHI_HOSP_NO", this.nhi_no);
		parm.addData("YEAR_MON", newBeginTime);
		if(czValue){
		    parm.addData("TYPE", "01");//城职被选中.
		}else{
			parm.addData("TYPE", "02");//城居被选中.
		}
		parm.addData("PARM_COUNT", 3);
//		System.out.println("parm:"+parm);
		TParm resultParm = InsManager.getInstance().safe(parm,null);
//		TParm resultParm = INSTJTool.getInstance().DataDown_zjkd_N(parm);
//		System.out.println("parm:"+parm);
//System.out.println("resultParm:"+resultParm);
		tTable.setHeader("汇总批号,100;序号,100;汇总期号,100;本月指标,100;本月联网垫付统筹申请,100;联网垫付超标金额,100;上月联网垫付超标剩余,100;筛查审核拒付金额,100; 上月筛查审核拒付剩余,100;筛查审核拒付剩余金额累计,100;本期有效月指标,100;本月联网统筹申请,100;本月统筹调整支付,100;调整减支付金额,100;本月智能审核拒付,100;上月筛查审核拒付调整,100;本月智能审核拒付调整,100;上月统筹申请超标,100;本月有效统筹申请,100;本月统筹申请超标,100;超标金额合计,100;本月指标结余,100;本月统筹应支付金额,100;本月考核预留金额,100;本月统筹支付金额,100;清算应收回金额,100;清算实际收回金额,100;清算收回剩余额,100;本月实际统筹支付,100;公务员补助金额,100;军残补助金额,100;城乡救助金额,100;民政优抚金额,100;非典补助金额,100;个人账户金额,100;本月支付合计,100;上月指标结余,100");
        tTable.setParmMap("BATCH_NO;SEQ_NO;YEAR_MON;MON_QUOTA_AMT;PRE_PAY_AMT;PRE_PAY_EXC_AMT;LMON_PRE_OVER_AMT;VERIFY_REFUSE_AMT;LMON_OVERREF_AMT;VREF_OVER_TOTAL_AMT;TPER_QUOTA_AMT;NHI_AMT;CHANGE_PAY_AMT;CHANGE_TPAY_AMT;REFUSE_AMT;CHANGE_VREFUSE_AMT;CHANGE_REFUSE_AMT;OVER_TOTAL_AMT;TPER_NHI_AMT;OVER_NHI_AMT;OVER_AMT;QUOTA_OVER_AMT;TPER_VERIFY_AMT;EXA_AMT;TPER_PAY_AMT;LYEAR_GET_AMT;LYEAR_GETREAL_AMT;LYEAR_OVERGET_AMT;TMON_PAY_REAL_AMT;SERVANT_AMT;ARMAY_AMT;CANDC_AMT;CIVIL_AMT;SARS_AMT;ACCOUT_PAY_AMT;TPER_TOTAL_AMT;LPER_OVER_AMT;");
		tTable.setColumnHorizontalAlignmentData("0,left");
        if (!INSTJTool.getInstance().getErrParm(resultParm)) {
	        messageBox("无下载资料!");
	        this.callFunction("UI|TTABLE|setParmValue", new TParm());
	        return;
	    }
		
		this.callFunction("UI|TTABLE|setParmValue", resultParm);
	}

	/**
	 * 打印天津市基本医疗保险定点机构基金清算表
	 */
	public void onPrint(){
		int selectedRow = this.tTable.getSelectedRow() ;

		if(selectedRow<0){
			messageBox("请选中一笔记录进行下载") ;
			return ;
		}
		boolean czValue = this.getValueBoolean("CZ");
		TParm resultParm=this.DataDown_zjkd_N(selectedRow);
		if(resultParm==null){
			return;
		}
		
		print_N(resultParm,czValue) ;
		
	}
	
	  /**
	   * 打印天津市基本医疗保险定点机构基金清算表
	   */
	  public TParm DataDown_zjkd_N(int selectedRow)
	  {
			//BATCH_NO;SEQ_NO;YEAR_MON;MON_QUOTA_AMT;PRE_PAY_AMT;PRE_PAY_EXC_AMT;LMON_PRE_OVER_AMT;
			//汇总批号;序号;汇总期号;本月指标;本月联网垫付统筹申请;联网垫付超标金额;上月联网垫付超标剩余;
			//VERIFY_REFUSE_AMT;LMON_OVERREF_AMT;VREF_OVER_TOTAL_AMT;
			//筛查审核拒付金额;上月筛查审核拒付剩余;筛查审核拒付剩余金额累计;
			//TPER_QUOTA_AMT;NHI_AMT;CHANGE_PAY_AMT;CHANGE_TPAY_AMT;REFUSE_AMT;CHANGE_VREFUSE_AMT;
			//本期有效月指标;本月联网统筹申请;本月统筹调整支付;调整减支付金额;本月智能审核拒付;上月筛查审核拒付调整;
			//CHANGE_REFUSE_AMT;OVER_TOTAL_AMT;TPER_NHI_AMT;OVER_NHI_AMT;OVER_AMT;QUOTA_OVER_AMT;
			//本月智能审核拒付调整;上月统筹申请超标;本月有效统筹申请;本月统筹申请超标;超标金额合计;本月指标结余;
			//TPER_VERIFY_AMT;EXA_AMT;TPER_PAY_AMT;LYEAR_GET_AMT;LYEAR_GETREAL_AMT;
			//本月统筹应支付金额;本月考核预留金额;本月统筹支付金额;清算应收回金额;清算实际收回金额;
			//LYEAR_OVERGET_AMT;TMON_PAY_REAL_AMT;SERVANT_AMT;ARMAY_AMT;CANDC_AMT;CIVIL_AMT;
			//清算收回剩余额;本月实际统筹支付;公务员补助金额;军残补助金额;城乡救助金额;民政优抚金额;    
			//SARS_AMT;ACCOUT_PAY_AMT;TPER_TOTAL_AMT;LPER_OVER_AMT
			//非典补助金额;个人账户金额;本月支付合计;上月指标结余;
			TTable table = (TTable) this.getComponent("TTABLE");// TABLE1
			if (table.getParmValue() == null ) {
				messageBox("请选择一条记录");
				return null;
			}
		  TParm tempParm=table.getParmValue();
//		  resultParm.addData("NHI_HOSP_NO", this.nhi_no) ;
//		  resultParm.addData("REGION_CHN_DESC", this.nhi_name) ;
//		  resultParm.addData("YEAR",this.getYear(resultParm.getValue("YEAR_MON", 0))) ;
			TParm resultParm=new TParm();
//			resultParm.setData("YEAR_MON", "201507");
			resultParm.setData("YEAR_MON","TEXT", tempParm.getValue("YEAR_MON", selectedRow).substring(0, 4)+
					"年"+tempParm.getValue("YEAR_MON", selectedRow).substring(5, 6)+"月");//期号
			resultParm.setData("BATCH_NO","TEXT", tempParm.getValue("BATCH_NO", selectedRow));//汇总批号
			resultParm.setData("NHI_HOSP_NO","TEXT",this.nhi_no) ;//机构编码
			resultParm.setData("YEAR","TEXT",this.getYear(tempParm.getValue("YEAR_MON", selectedRow))) ;//协议年度
			resultParm.setData("REGION_CHN_DESC","TEXT",this.nhi_name) ;//机构编码

			resultParm.setData("MON_QUOTA_AMT","TEXT",df.format(tempParm.getDouble("MON_QUOTA_AMT", selectedRow))) ;//本月指标
			resultParm.setData("LPER_OVER_AMT","TEXT",df.format(tempParm.getDouble("LPER_OVER_AMT", selectedRow))) ;//上月指标结余
			resultParm.setData("PRE_PAY_AMT","TEXT",df.format(tempParm.getDouble("PRE_PAY_AMT", selectedRow))) ;//本月联网垫付统筹申请
			resultParm.setData("LMON_PRE_OVER_AMT","TEXT",df.format(tempParm.getDouble("LMON_PRE_OVER_AMT", selectedRow))) ;//上月联网垫付超标剩余
			resultParm.setData("VERIFY_REFUSE_AMT","TEXT",df.format(tempParm.getDouble("VERIFY_REFUSE_AMT", selectedRow))) ;//本月筛查审核拒付
			resultParm.setData("LMON_OVERREF_AMT","TEXT",df.format(tempParm.getDouble("LMON_OVERREF_AMT", selectedRow))) ;//上月筛查审核拒付剩余
			resultParm.setData("CHANGE_VREFUSE_AMT","TEXT",df.format(tempParm.getDouble("CHANGE_VREFUSE_AMT", selectedRow))) ;//上月筛查审核拒付调整
			resultParm.setData("TPER_QUOTA_AMT","TEXT",df.format(tempParm.getDouble("TPER_QUOTA_AMT", selectedRow))) ;//本月有效指标
			
			resultParm.setData("NHI_AMT","TEXT",df.format(tempParm.getDouble("NHI_AMT", selectedRow))) ;//本月联网统筹申请
			resultParm.setData("CHANGE_PAY_AMT","TEXT",df.format(tempParm.getDouble("CHANGE_PAY_AMT", selectedRow))) ;//本月统筹调整支付
			resultParm.setData("REFUSE_AMT","TEXT",df.format(tempParm.getDouble("REFUSE_AMT", selectedRow))) ;//本月智能审核拒付
			resultParm.setData("CHANGE_REFUSE_AMT","TEXT",df.format(tempParm.getDouble("CHANGE_REFUSE_AMT", selectedRow))) ;//本月智能审核拒付调整
			resultParm.setData("OVER_TOTAL_AMT","TEXT",df.format(tempParm.getDouble("OVER_TOTAL_AMT", selectedRow))) ;//上月统筹申请超标
			resultParm.setData("TPER_NHI_AMT","TEXT",df.format(tempParm.getDouble("TPER_NHI_AMT", selectedRow))) ;//本月有效统筹申请
			resultParm.setData("TPER_VERIFY_AMT","TEXT",df.format(tempParm.getDouble("TPER_VERIFY_AMT", selectedRow))) ;//本月统筹应支付金额
			resultParm.setData("EXA_AMT","TEXT",df.format(tempParm.getDouble("EXA_AMT", selectedRow))) ;//本月考核预留金额
			resultParm.setData("TPER_PAY_AMT","TEXT",df.format(tempParm.getDouble("TPER_PAY_AMT", selectedRow))) ;//本月统筹支付金额
			resultParm.setData("QUOTA_OVER_AMT","TEXT",df.format(tempParm.getDouble("QUOTA_OVER_AMT", selectedRow))) ;//本月指标结余
			resultParm.setData("OVER_NHI_AMT","TEXT",df.format(tempParm.getDouble("OVER_NHI_AMT", selectedRow))) ;//本月统筹申请超标
			resultParm.setData("OTHER_AMT","TEXT","0.00") ;//其它
			
			resultParm.setData("LYEAR_GET_AMT","TEXT",df.format(tempParm.getDouble("LYEAR_GET_AMT", selectedRow))) ;//清算应收回金额
			resultParm.setData("LYEAR_GETREAL_AMT","TEXT",df.format(tempParm.getDouble("LYEAR_GETREAL_AMT", selectedRow))) ;//清算实际收回金额
			resultParm.setData("LYEAR_OVERGET_AMT","TEXT",df.format(tempParm.getDouble("LYEAR_OVERGET_AMT", selectedRow))) ;//清算收回剩余额
			
			resultParm.setData("TMON_PAY_REAL_AMT","TEXT",df.format(tempParm.getDouble("TMON_PAY_REAL_AMT", selectedRow))) ;//本月实际统筹支付
			resultParm.setData("SERVANT_AMT","TEXT",df.format(tempParm.getDouble("SERVANT_AMT", selectedRow))) ;//公务员补助金额
			resultParm.setData("ARMAY_AMT","TEXT",df.format(tempParm.getDouble("ARMAY_AMT", selectedRow))) ;//军残补助金额
			resultParm.setData("CANDC_AMT","TEXT",df.format(tempParm.getDouble("CANDC_AMT", selectedRow))) ;//民政补助金额
			resultParm.setData("CIVIL_AMT","TEXT",df.format(tempParm.getDouble("CIVIL_AMT", selectedRow))) ;//民政优抚金额
			resultParm.setData("ACCOUT_PAY_AMT","TEXT",df.format(tempParm.getDouble("ACCOUT_PAY_AMT", selectedRow))) ;//个人账户
			resultParm.setData("TPER_TOTAL_AMT","TEXT",df.format(tempParm.getDouble("TPER_TOTAL_AMT", selectedRow))) ;//本月支付合计
			Timestamp date = SystemTool.getInstance().getDate();
			String d=date.toString().substring(0, 10).replace("-", "");
			resultParm.setData("DATE1","TEXT",d.substring(0,4)+"年"+
					d.substring(4,6)+"月"+d.substring(6,8)+"日") ;//定点机构
			resultParm.setData("DATE2","TEXT",d.substring(0,4)+"年"+
					d.substring(4,6)+"月"+d.substring(6,8)+"日") ;//定点机构

			return resultParm;
	  }  
	  /**
	   * 打印天津市基本医疗保险定点机构基金清算表
	   */
   private void print_N(TParm resultParm,boolean czValue){
		  if(czValue){//城职被选中.
			  this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSCityCost.jhw",resultParm);
		  }else{//城居被选中.
			  this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSCountryCost.jhw",resultParm);
		  }
	}
   /**
    * 门诊运行情况表打印
    */
   public void print_O(){
	   boolean czValue = this.getValueBoolean("CZ");
	   String type="";
		if(czValue){
			type= "01";//城职被选中.
		}else{
			type= "02";//城居被选中.
		}
		TParm resultParm=getPrintData(DataDown_zjkd_O(type));
		  if(czValue){//城职被选中.
			  this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSOpdCostCz.jhw",resultParm);
		  }else{//城居被选中.
			  this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSOpdCostCj.jhw",resultParm);
		  }
   }
   /**
    * 住院运行情况表打印
    */
   public void print_P(){
	   boolean czValue = this.getValueBoolean("CZ");
	   String type="";
		if(czValue){
			type= "01";//城职被选中.
		}else{
			type= "02";//城居被选中.
		}
		TParm resultParm=getPrintData(DataDown_zjkd_P(type));
		  if(czValue){//城职被选中.
			  this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSInpCostCz.jhw",resultParm);
		  }else{//城居被选中.
			  this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSInpCostCj.jhw",resultParm);
		  }
   }
   /**
    * 拒付汇总
    */
   public void print_Q(){
		int selectedRow = this.tTable.getSelectedRow() ;

		if(selectedRow<0){
			messageBox("请选中一条记录") ;
			return ;
		}
		String reportNo = (String) this.tTable.getValueAt_(selectedRow, 0) ;
		  
		  TParm parm = new TParm() ;
		  parm.addData("HOSP_NHI_NO", this.nhi_no) ;
		  parm.addData("ADM_SEQ", reportNo) ;
		  
		  boolean czValue = this.getValueBoolean("CZ");
		  String type="";
          String typeDesc="";
		  if(czValue){
			  type= "01";//城职被选中.
			  typeDesc="城职";
			  parm.setData("PIPELINE", "DataDown_zjks") ;
			  parm.setData("PLOT_TYPE", "B") ;
		  }else{
			  type= "02";//城居被选中.
			  typeDesc="城居";
			  parm.setData("PIPELINE", "DataDown_cjks") ;
			  parm.setData("PLOT_TYPE", "B") ;
		  }
		  parm.addData("PARM_COUNT", 2);
		  TParm resultParm = InsManager.getInstance().safe(parm,null);
//		  System.out.println("DataDown_zjks:"+resultParm);
			double tot = resultParm.getDouble("THE_PRO_AMT", 0);

	   double aiOpdMzTot=getDataDown_zjkd_Q_Tot(DataDown_zjkd_Q(type,"01"));
	   double aiOpdMtTot=getDataDown_zjkd_Q_Tot(DataDown_zjkd_Q(type,"02"));
	   double aiInp=getDataDown_zjkd_Q_Tot(DataDown_zjkd_Q(type,"03"));
	   double countTot=aiInp+aiOpdMzTot+aiOpdMtTot+tot;
//	   resultParm.setData("NHI_HOSP_NO","TEXT",this.nhi_no) ;//机构编码
	   String yearMon=(String) this.tTable.getValueAt_(selectedRow, 2);
//	   printParm.setData("YEAR_MON", "TEXT",(String) this.tTable.getValueAt_(selectedRow, 2)) ;
		TParm printParm =new TParm();
	   printParm.setData("YEAR_MON","TEXT", yearMon.substring(0, 4)+
				"年"+yearMon.substring(5, 6)+"月");//期号
	   printParm.setData("BATCH_NO", "TEXT",reportNo) ;
	   printParm.setData("HOSP_NHI_NO", "TEXT",this.nhi_no) ;
	   printParm.setData("REGION_CHN_DESC","TEXT", this.nhi_name) ;
	   printParm.setData("TYPEDESC","TEXT",typeDesc) ;
	   printParm.setData("AIINPTOT","TEXT",df.format(aiInp)) ;
	   printParm.setData("AIOPDTOT","TEXT",df.format(aiOpdMzTot+aiOpdMtTot)) ;
	   printParm.setData("AITOT","TEXT",df.format(aiInp+aiOpdMzTot+aiOpdMtTot)) ;
	   printParm.setData("TOT","TEXT",df.format(tot)) ;
	   printParm.setData("COUNTTOT","TEXT",df.format(countTot)) ;
	   printParm.setData("UCOUNTTOT","TEXT",StringUtil.getInstance().numberToWord(countTot)) ;
	   
	   this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSReviewPayment.jhw",printParm);
   } 
   /**
    * 取得DataDown_zjkd_Q中拒付金额合计
    */
   private double getDataDown_zjkd_Q_Tot(TParm parm){
//	   System.out.println("getDataDown_zjkd_Q_Tot:"+parm);
	   double tot=0;
	   int rowCount=parm.getCount("REFUSE_AMT");
	   for(int i=0;i<rowCount;i++){
//		   System.out.println(tot+"::::::"+parm.getDouble("REFUSE_AMT", i));
		   tot=tot+parm.getDouble("REFUSE_AMT", i);
	   }
	   return tot;
   }
   /**
    * 取得协议年度
    */
   public String getYear(String YearMon){
	   String year="";
//	   System.out.println("getYear:"+YearMon.substring(5, 6));
	   if(Integer.parseInt(YearMon.substring(5, 6))>=4){
		   year=YearMon.substring(0, 4);
	   }else{
		   year=String.valueOf(Integer.parseInt(YearMon.substring(0, 4))-1);
	   }
	   return year;
   }
   /**
    * 取得住院门诊运行情况表打印数据
    */
   private TParm getPrintData(TParm parm){
//	   BATCH_NO	申报批号
//	   SEQ_NO	序号
//	   YEAR_MON	汇总期号
//	   START_DATE	结算开始时间
//	   END_DATE	结算结束时间
//	   SUM_TYPE	汇总类别 
//	   PAY_TYPE	支付类别
//	   PRE_PAY_B	统筹联网垫付申请人次
//	   PRE_PAY_AMT	统筹联网垫付申请金额  
//	   VERIFY_REFUSE_AMT	筛查审核拒付金额 
//	   NHI_B	联网统筹申请人次
//	   NHI_AMT	联网统筹申请金额  
//	   NHIR_B	联网统筹退费人次
//	   NHIR_AMT	联网统筹退费金额 
//	   CHANGE_PAY_AMT	统筹调整支付金额
//	   CHANGE_PAY_B	统筹调整支付人次
//	   CHANGE_TPAY_AMT	调整减支付金额
//	   CHANGE_TPAY_B	调整减支付人次
//	   REFUSE_B	智能审核拒付人次 
//	   REFUSE_AMT	智能审核拒付金额
//	   CHANGE_REF_AMT	智能审核拒付调整金额
//	   CHANGE_VREF_AMT	筛查审核拒付调整金额
//	   String BATCH_NO=parm.getValue("BATCH_NO", 0);//	申报批号
//	   String SEQ_NO="";//	序号
//	   String YEAR_MON=parm.getValue("YEAR_MON", 0);//	汇总期号
//	   String START_DATE="";//	结算开始时间
//	   String END_DATE="";//	结算结束时间
//	   String SUM_TYPE="";//	汇总类别 
//	   String PAY_TYPE="";//	支付类别
//	   int PRE_PAY_B=0;//	统筹联网垫付申请人次
//	   double PRE_PAY_AMT=0;//	统筹联网垫付申请金额  
//	   double VERIFY_REFUSE_AMT=0;//	筛查审核拒付金额 
//	   int NHI_B=0;//	联网统筹申请人次
//	   double NHI_AMT=0;//	联网统筹申请金额  
//	   int NHIR_B=0;//	联网统筹退费人次
//	   double NHIR_AMT=0;//	联网统筹退费金额 
//	   double CHANGE_PAY_AMT=0;//	统筹调整支付金额
//	   int CHANGE_PAY_B=0;//	统筹调整支付人次
//	   double CHANGE_TPAY_AMT=0;//	调整减支付金额
//	   int CHANGE_TPAY_B=0;//	调整减支付人次
//	   int REFUSE_B=0;//	智能审核拒付人次 
//	   double REFUSE_AMT=0;//	智能审核拒付金额
//	   double CHANGE_REF_AMT=0;//	智能审核拒付调整金额
//	   double CHANGE_VREF_AMT=0;//	筛查审核拒付调整金额
//	   
//	   int countRow=parm.getCount("BATCH_NO");
//	   double SERVANT_AMT=0;//	公务员补助金额
//	   double ARMAY_AMT=0;//	军残补助金额
//	   double CANDC_AMT=0;//	民政救助金额
//	   double CIVIL_AMT=0;//	民政优抚金额
//	   double ACCOUT_PAY_AMT=0;//	个人账户金额 
//	   for(int i=0;i<countRow;i++){
//	        if(parm.getValue("PAY_TYPE", i).equals("03")){
//	        	ARMAY_AMT=ARMAY_AMT+(parm.getDouble("NHI_AMT", i)-parm.getDouble("NHIR_AMT", i)-parm.getDouble("PRE_PAY_AMT", i));
//	        	continue;
//	        }else if(parm.getValue("PAY_TYPE", i).equals("04")){
//	        	SERVANT_AMT=SERVANT_AMT+(parm.getDouble("NHI_AMT", i)-parm.getDouble("NHIR_AMT", i)-parm.getDouble("PRE_PAY_AMT", i));
//	        	continue;
//	        }else if(parm.getValue("PAY_TYPE", i).equals("10")){
//	        	ACCOUT_PAY_AMT=ACCOUT_PAY_AMT+(parm.getDouble("NHI_AMT", i)-parm.getDouble("NHIR_AMT", i)-parm.getDouble("PRE_PAY_AMT", i));
//	        	continue;
//	        }else if(parm.getValue("PAY_TYPE", i).equals("11")){
//	        	CANDC_AMT=CANDC_AMT+(parm.getDouble("NHI_AMT", i)-parm.getDouble("NHIR_AMT", i)-parm.getDouble("PRE_PAY_AMT", i));
//	        	continue;
//	        }else if(parm.getValue("PAY_TYPE", i).equals("12")){
//	        	CIVIL_AMT=CIVIL_AMT+(parm.getDouble("NHI_AMT", i)-parm.getDouble("NHIR_AMT", i)-parm.getDouble("PRE_PAY_AMT", i));
//	        	continue;
//	        }
//		    PRE_PAY_B=PRE_PAY_B+parm.getInt("PRE_PAY_B", i);//	统筹联网垫付申请人次
//		    PRE_PAY_AMT=PRE_PAY_AMT+parm.getDouble("PRE_PAY_AMT", i);//	统筹联网垫付申请金额  
//		    VERIFY_REFUSE_AMT=VERIFY_REFUSE_AMT+parm.getDouble("VERIFY_REFUSE_AMT", i);//	筛查审核拒付金额 
//		    NHI_B=NHI_B+parm.getInt("NHI_B", i);//	联网统筹申请人次
//		    NHI_AMT=NHI_AMT+parm.getDouble("NHI_AMT", i);//	联网统筹申请金额  
//		    NHIR_B=NHIR_B+parm.getInt("NHIR_B", i);//	联网统筹退费人次
//		    NHIR_AMT=NHIR_AMT+parm.getDouble("NHIR_AMT", i);//	联网统筹退费金额 
//		    CHANGE_PAY_AMT=CHANGE_PAY_AMT+parm.getDouble("CHANGE_PAY_AMT", i);//	统筹调整支付金额
//		    CHANGE_PAY_B=CHANGE_PAY_B+parm.getInt("CHANGE_PAY_B", i);//	统筹调整支付人次
//		    CHANGE_TPAY_AMT=CHANGE_TPAY_AMT+parm.getDouble("CHANGE_TPAY_AMT", i);//	调整减支付金额
//		    CHANGE_TPAY_B=CHANGE_TPAY_B+parm.getInt("CHANGE_TPAY_B", i);//	调整减支付人次
//		    REFUSE_B=REFUSE_B+parm.getInt("REFUSE_B", i);//	智能审核拒付人次 
//		    REFUSE_AMT=REFUSE_AMT+parm.getDouble("REFUSE_AMT", i);//	智能审核拒付金额
//		    CHANGE_REF_AMT=CHANGE_REF_AMT+parm.getDouble("CHANGE_REF_AMT", i);//	智能审核拒付调整金额
//		    CHANGE_VREF_AMT=CHANGE_VREF_AMT+parm.getDouble("CHANGE_VREF_AMT", i);//	筛查审核拒付调整金额
//	   } 
//	   NHI_SUM_B	联网统筹申请合计人次
//	   NHI_SUM_AMT	联网统筹申请合计金额    
//	   NHI_B	联网统筹申请人次
//	   NHI_AMT	联网统筹申请金额  
//	   PRE_PAY_B	统筹联网垫付申请人次
//	   PRE_PAY_AMT	统筹联网垫付申请金额  
//	   CHANGE_PAY_B	统筹调整支付人次
//	   CHANGE_PAY_AMT	统筹调整支付金额
//	   REFUSE_AMT	智能审核拒付金额
//	   VERIFY_REFUSE_AMT	筛查审核拒付金额 
//	   CHANGE_REF_AMT	智能审核拒付调整金额
//	   CHANGE_VREF_AMT	筛查审核拒付调整金额
//	   SERVER_AMT	公务员补助金额金额   
//	   ARMY_AMT	军残补助金额
//	   MZJZ_AMT	民政救助金额
//	   MZYF_AMT	民政优抚金额
	   int selectedRow = this.tTable.getSelectedRow() ;
		TTable table = (TTable) this.getComponent("TTABLE");// TABLE1

		if (selectedRow==-1) {
			messageBox("请选择一条记录");
			return null;
		}

	  TParm tempParm=table.getParmValue();
	  
	   TParm resultParm = new TParm() ;

		resultParm.setData("YEAR_MON","TEXT", tempParm.getValue("YEAR_MON", selectedRow).substring(0, 4)+
				"年"+tempParm.getValue("YEAR_MON", selectedRow).substring(5, 6)+"月");//期号
		resultParm.setData("BATCH_NO","TEXT", tempParm.getValue("BATCH_NO", selectedRow));//汇总批号
	   resultParm.setData("HOSP_NHI_NO", "TEXT",this.nhi_no) ;
	   resultParm.setData("REGION_CHN_DESC", "TEXT",this.nhi_name) ;
	   resultParm.setData("YEAR","TEXT",this.getYear(tempParm.getValue("YEAR_MON", selectedRow))) ;
	  
	   resultParm.setData("NHI_B_TOT", "TEXT",parm.getValue("NHI_SUM_B", 0)) ;
	   resultParm.setData("NHI_AMT_TOT", "TEXT",df.format(parm.getDouble("NHI_SUM_AMT", 0))) ;
	   resultParm.setData("NHI_B", "TEXT",parm.getValue("NHI_B", 0)) ;
	   resultParm.setData("NHI_AMT", "TEXT",df.format(parm.getDouble("NHI_AMT", 0))) ;
	   resultParm.setData("PRE_PAY_B", "TEXT",parm.getValue("PRE_PAY_B", 0)) ;
	   resultParm.setData("PRE_PAY_AMT", "TEXT",df.format(parm.getDouble("PRE_PAY_AMT", 0))) ;
	   resultParm.setData("CHANGE_PAY_B","TEXT", parm.getValue("CHANGE_PAY_B", 0)) ;
	   resultParm.setData("CHANGE_PAY_AMT","TEXT", df.format(parm.getDouble("CHANGE_PAY_AMT", 0))) ;
	   resultParm.setData("REFUSE_AMT","TEXT", df.format(parm.getDouble("REFUSE_AMT", 0))) ;
	   resultParm.setData("VERIFY_REFUSE_AMT","TEXT", df.format(parm.getDouble("VERIFY_REFUSE_AMT", 0))) ;
	   resultParm.setData("CHANGE_REF_AMT","TEXT", df.format(parm.getDouble("CHANGE_REF_AMT", 0))) ;
	   resultParm.setData("CHANGE_VREF_AMT","TEXT", df.format(parm.getDouble("CHANGE_VREF_AMT", 0))) ;
//	           民政救助金额
	   resultParm.setData("CANDC_AMT","TEXT", df.format(parm.getDouble("MZJZ_AMT", 0))) ;
//		民政优抚金额
	   resultParm.setData("CIVIL_AMT","TEXT", df.format(parm.getDouble("MZYF_AMT", 0))) ;
//		公务员补助金额
	   resultParm.setData("SERVANT_AMT","TEXT", df.format(parm.getDouble("SERVER_AMT", 0))) ;
//		军残补助金额
	   resultParm.setData("ARMAY_AMT","TEXT", df.format(parm.getDouble("ARMY_AMT", 0))) ;
//		个人账户金额 
	   resultParm.setData("ACCOUT_PAY_AMT","TEXT", df.format(parm.getDouble("ACCOUNT_PAY_AMT", 0))) ;
	   
	   return resultParm;
   }
   /**
	 * 门诊运行情况表下载
	 */
   private TParm DataDown_zjkd_O(String type){
	   TParm parm = this.getInsParm("DataDown_zjkd","O",type, null);
		  TParm resultParm = InsManager.getInstance().safe(parm,null) ;
		  
		  if(resultParm == null){
			  messageBox("此批号下载失败!") ;
			  return null;
		  }
	   return resultParm;
   }

   /**
	 * 住院运行情况表下载
	 */
   private TParm DataDown_zjkd_P(String type){
	   TParm parm = this.getInsParm("DataDown_zjkd","P",type, null);
		  TParm resultParm = InsManager.getInstance().safe(parm,null) ;
		  
		  if(resultParm == null){
			  messageBox("此批号下载失败!") ;
			  return null;
		  }
	   return resultParm;
   }
   /**
	 * 智能审核拒付汇总下载
	 */
  private TParm DataDown_zjkd_Q(String type,String payType){
	   TParm parm = this.getInsParm("DataDown_zjkd","Q",type, payType);
//	   System.out.printf("DataDown_zjkd_Q:"+parm);
		  TParm resultParm = InsManager.getInstance().safe(parm,null) ;
		  
		  if(resultParm == null){
			  messageBox("此批号下载失败!") ;
			  return null;
		  }
	   return resultParm;
  }
   /**
    * 取得调用医保方法参数
    */
   private TParm getInsParm(String pipeline,String plotType,
		   String type,String payType){
		int selectedRow = this.tTable.getSelectedRow() ;

		if(selectedRow<0){
			messageBox("请选中一条记录") ;
			return null;
		}
//		boolean czValue = this.getValueBoolean("CZ");
//		if(czValue){//城职被选中.
//			
//		}else{//城居被选中.
//			
//		}
		  String reportNo = (String) this.tTable.getValueAt_(selectedRow, 0) ;
		  
			  TParm parm = new TParm() ;
			  parm.setData("PIPELINE", pipeline) ;
			  parm.setData("PLOT_TYPE", plotType) ;
			  parm.addData("NHI_HOSP_NO", this.nhi_no) ;
			  parm.addData("BATCH_NO", reportNo) ;
			  parm.addData("TYPE", type) ;
			  if(StringUtil.isNullString(payType)){
			      parm.addData("PARM_COUNT", 3);
			  }else{
				  parm.addData("PAY_TYPE", payType) ;
				  parm.addData("PARM_COUNT", 4);
			  }
			  return parm;
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
	////////////////////////////////////////////////////////////////////////////
	/**
	 * 调整支付明细下载
	 */
	public void APDDown() { 
		
		int selectedRow = this.tTable.getSelectedRow() ;

		if(selectedRow<0){
			messageBox("请选中一条记录") ;
			return ;
		}
		String reportNo = (String) this.tTable.getValueAt_(selectedRow, 0) ;
		  
		  TParm parm = new TParm() ;
		  parm.addData("BATCH_NO", reportNo) ;
//		  parm.addData("BATCH_NO", "ZKZ000000201612CS1") ;
//		  parm.addData("BATCH_NO", "ZKX000000201612CS1") ;
		  parm.addData("NHI_HOSP_NO", this.nhi_no) ;
		  
		  
		  boolean czValue = this.getValueBoolean("CZ");
//		  String type="";
//          String typeDesc="";
		  if(czValue){
//			  type= "01";//城职被选中.
//			  typeDesc="城职";
			  parm.setData("PIPELINE", "DataDown_zjkd") ;
			  parm.setData("PLOT_TYPE", "V") ;
		  }else{
//			  type= "02";//城居被选中.
//			  typeDesc="城居";
			  parm.setData("PIPELINE", "DataDown_cjkd") ;
			  parm.setData("PLOT_TYPE", "V") ;
		  }
		  parm.addData("PARM_COUNT", 2);
		  TParm resultParm = InsManager.getInstance().safe(parm,null);

		  tTable.setHeader("被调整顺序号,100;身份证号码,100;姓名 ,100;人员类别,100;补助人员类别,100;统筹调整支付金额,100;补助调整支付金额,100;个人帐户调整支付金额,100;数据来源,100");
	      tTable.setParmMap("ADJUST_SEQ_NO;IDNO;PAT_NAME;CTZ_TYPE;SPC_CTZ_TYPE;ADJUST_NHI_AMT;ADJUST_SUB_AMT;ADJUST_ACCOUT_AMT;DATA_SOURCE;");
//		  tTable.setColumnHorizontalAlignmentData("0,left");
          if (!INSTJTool.getInstance().getErrParm(resultParm)) {
	          messageBox("无下载资料!");
	          this.callFunction("UI|TTABLE|setParmValue", new TParm());
	          return;
          }
		  this.callFunction("UI|TTABLE|setParmValue", resultParm);
	}
	/**
	 * 调整支付明细导出Excel
	 * */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
//		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		String Title="调整支付明细";
		ExportExcelUtil.getInstance().exportExcel(tTable, Title);
	}

}
