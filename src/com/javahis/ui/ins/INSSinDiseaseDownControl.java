package com.javahis.ui.ins;

//import java.sql.Timestamp;
import java.sql.Timestamp;
import java.text.DecimalFormat;
//import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import jdo.ins.INSTJTool;
//import jdo.ins.INSTool;
import jdo.ins.InsManager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
//import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
//import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
//import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
//import com.sun.xml.xsom.impl.Ref.Term;

/**
 * <p>Title: 人头及病种付费月清算表</p>
 *
 * <p>Description:人头及病种付费月清算表</p>
 *
 * <p>Copyright: Copyright (c) 2016</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangs  
 * @version 1.0
 */
public class INSSinDiseaseDownControl extends TControl {
	
	private TTable tTable ;
	
	private TComboBox INS_TYPE ;
	
	private TComboBox PAY_TYPE ;
	
	private String nhi_no ;
	private String nhi_name;
	public DecimalFormat df = new DecimalFormat("##########0.00");
	/**
	 * 初始化方法
	 */
	public void onInit() {
		this.tTable = (TTable) this.getComponent("TTABLE") ;
		this.INS_TYPE = (TComboBox)this.getComponent("INS_TYPE") ;
		this.PAY_TYPE = (TComboBox)this.getComponent("PAY_TYPE") ;
		PAY_TYPE.setValue("01");
		//onQuery();// 查询table1
//		TParm parm = null;
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		this.nhi_no = regionParm.getData("NHI_NO", 0).toString();// 获取HOSP_NHI_NO
		this.nhi_name= regionParm.getData("REGION_CHN_DESC", 0).toString();// 获取REGION_CHN_DESC
	    this.setValue("YEAR_MON", SystemTool.getInstance().getDate());
	    
//	    this.nhi_no="000139";
//	    this.setValue("BEGIN_TIME", "2015/07");
	}
	
	public void onQuery(){
		this.onDownload() ;
	}
	/**
	 * 下载
	 */
	public void onDownload() { 
		
		// 验证用户输入的合法性.
		String yearMon = this.getValueString("YEAR_MON");
		String insType = INS_TYPE.getValue();
		String payType = PAY_TYPE.getValue();

		if ("".equals(yearMon)) {
			messageBox("期号不可为空");
			return;
		}
		if ("".equals(insType)) {
			messageBox("险种不可为空");
			return;
		}
		if ("".equals(payType)) {
			messageBox("付费方式不可为空");
			return;
		}
		String[] beginTimeArray = yearMon.split("-");
		String newBeginTime = beginTimeArray[0].concat(beginTimeArray[1]);
		
		TParm parm = new TParm(); 
		parm.setData("PIPELINE", "DataDown_zjkd");
		parm.setData("PLOT_TYPE", "S");
        parm.addData("NHI_HOSP_NO", this.nhi_no);
		parm.addData("YEAR_MON", newBeginTime);			
		parm.addData("INS_TYPE", insType);
		parm.addData("PAY_TYPE", payType);
		parm.addData("PARM_COUNT", 4);
//		System.out.println("parm:"+parm);
		TParm resultParm = InsManager.getInstance().safe(parm,null);
//		System.out.println("parm:"+parm);
//        System.out.println("resultParm:"+resultParm);
        if (!INSTJTool.getInstance().getErrParm(resultParm)) {
	        messageBox("无下载资料!");
	        this.callFunction("UI|TTABLE|setParmValue", new TParm());
	        return;
	    }
		
		this.callFunction("UI|TTABLE|setParmValue", resultParm);
	}

   /**
    * 人头付费运行情况表
    */
   public void onPrint(){
		int selectedRow = this.tTable.getSelectedRow() ;

		if(selectedRow<0){
			messageBox("请选中一条记录") ;
			return ;
		}
		  TParm tableParm=this.tTable.getParmValue();
		  TParm parm = new TParm() ;
		  parm.addData("HOSP_NHI_NO", this.nhi_no) ;
		  parm.addData("YEAR_MON", tableParm.getValue("SUM_YEARMON", selectedRow));	
		  parm.addData("MON_BATCH_NO", tableParm.getValue("MON_BATCH_NO", selectedRow)) ;
		  parm.addData("SINGLE_BATCH_NO", tableParm.getValue("SINGLE_BATCH_NO", selectedRow)) ;
		  parm.addData("INS_TYPE", INS_TYPE.getValue()) ;
		  parm.setData("PIPELINE", "DataDown_zjkd") ;
		  parm.setData("PLOT_TYPE", "T") ;
		  parm.addData("PARM_COUNT", 5);
		  TParm resultParm = InsManager.getInstance().safe(parm,null);

       int childrenTime=0;
       double childrenAmt=0;
       int adultHighTime=0;
       double adultHighAmt=0;
       int adultMedianTime=0;
       double adultMedianAmt=0;
       int adultLowTime=0;
       double adultLowAmt=0;
       int referralTime=0;
       double referralAmt=0;
       ///////////////////////////////////////////////////数据计算
	   int rowCount=resultParm.getCount("REFUSE_AMT");
	   for(int i=0;i<rowCount;i++){
//		   System.out.println(tot+"::::::"+parm.getDouble("REFUSE_AMT", i));
		   if(resultParm.getValue("SUM_TYPE", i).equals("12")){
			   if(resultParm.getValue("PAT_TYPE", i).equals("01")||
					   resultParm.getValue("PAT_TYPE", i).equals("11")){
				   childrenTime = resultParm.getInt("SUM_P", i);
				   childrenAmt = resultParm.getDouble("TOT_NHI_AMT", i);
			   }else if(resultParm.getValue("PAT_TYPE", i).equals("02")||
					   resultParm.getValue("PAT_TYPE", i).equals("21")){
				   adultHighTime = resultParm.getInt("SUM_P", i);
				   adultHighAmt = resultParm.getDouble("TOT_NHI_AMT", i);
			   }
			   else if(resultParm.getValue("PAT_TYPE", i).equals("03")||
					   resultParm.getValue("PAT_TYPE", i).equals("51")){
				   adultMedianTime = resultParm.getInt("SUM_P", i);
				   adultMedianAmt = resultParm.getDouble("TOT_NHI_AMT", i);
			   }else if(resultParm.getValue("PAT_TYPE", i).equals("04")){
				   adultLowTime = resultParm.getInt("SUM_P", i);
				   referralAmt = resultParm.getDouble("TOT_NHI_AMT", i);
			   }

		   }else if(resultParm.getValue("SUM_TYPE", i).equals("14")){
			   referralTime=referralTime+resultParm.getInt("SUM_P", i);
			   referralAmt=referralAmt+resultParm.getDouble("TOT_NHI_AMT", i);
		   }
	   }
       //////////////////////////////////////////////////
       double tot=childrenAmt+adultHighAmt+adultMedianAmt+adultLowAmt-referralAmt;
       
	   String yearMon=tableParm.getValue("SUM_YEARMON", selectedRow);
	   TParm printParm =new TParm();
	   printParm.setData("YEAR_MON","TEXT", yearMon.substring(0, 4)+
				"年"+yearMon.substring(5, 6)+"月");//期号
	   printParm.setData("INS_TYPE","TEXT", INS_TYPE.getText());//险种类别
	   printParm.setData("MON_BATCH_NO", "TEXT",tableParm.getValue("MON_BATCH_NO", selectedRow)) ;
	   printParm.setData("HOSP_NHI_NO", "TEXT",this.nhi_no) ;
	   printParm.setData("REGION_CHN_DESC","TEXT", this.nhi_name) ;
	   printParm.setData("YEAR","TEXT",tableParm.getValue("YEAR", selectedRow)) ;
	   printParm.setData("CHILDREN_TIME","TEXT",df.format(childrenTime)) ;//学生儿童
	   printParm.setData("CHILDREN_AMT","TEXT",df.format(childrenAmt)) ;
	   printParm.setData("ADULT_HIGH_TIME","TEXT",df.format(adultHighTime)) ;//成年人（高档缴费）
	   printParm.setData("ADULT_HIGH_AMT","TEXT",df.format(adultHighAmt)) ;
	   printParm.setData("ADULT_MEDIAN_TIME","TEXT",df.format(adultMedianTime)) ;//成年人（高中缴费）
	   printParm.setData("ADULT_MEDIAN_AMT","TEXT",df.format(adultMedianAmt)) ;
	   printParm.setData("ADULT_LOW_TIME","TEXT",df.format(adultLowTime)) ;//成年人（高低缴费）
	   printParm.setData("ADULT_LOW_AMT","TEXT",df.format(adultLowAmt)) ;
	   printParm.setData("REFERRAL_TIME","TEXT",df.format(referralTime)) ;//转诊
	   printParm.setData("REFERRAL_AMT","TEXT",df.format(referralAmt)) ;
	   printParm.setData("TOT","TEXT",df.format(tot)) ;//合计
	   printParm.setData("PAY_AMT","TEXT",df.format(tot)) ;
	   printParm.setData("AMT_IN_WORDS","TEXT",StringUtil.getInstance().numberToWord(tot)) ;
       if(INS_TYPE.getValue().equals("310")){
    	   this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSSinDiseaseCZ.jhw",printParm);    	   
       }else if(INS_TYPE.getValue().equals("390")){
    	   this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSSinDiseaseCJ.jhw",printParm);    
       }
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
	 * 清空
	 */
	public void onClear(){
		this.setValue("YEAR_MON", "") ;
		this.setValue("INS_TYPE", "") ;
		this.setValue("PAY_TYPE", "") ;
		this.callFunction("UI|TTABLE|setParmValue", new TParm());
	}
	
	/**
	 * 格式化
	 */
	private String round2(double d){
		 return new DecimalFormat("###########0.00").format(StringTool.round(d, 2));
	}
	public void onInsType(){
		this.callFunction("UI|TTABLE|setParmValue", new TParm());
	}
	  /**
	    * 病种汇总表
	    */
	   public void onSinPrint(){
			int selectedRow = this.tTable.getSelectedRow() ;

			TParm tableParm=this.tTable.getParmValue();
			TParm parm = new TParm() ;
			parm.addData("NHI_HOSP_NO", this.nhi_no) ;
			String yearMon="";
			if(selectedRow<0){
				String[] beginTimeArray = this.getValueString("YEAR_MON").split("-");
				yearMon=beginTimeArray[0].concat(beginTimeArray[1]);
				parm.addData("YEAR_MON", yearMon);	
				parm.addData("MON_BATCH_NO", "") ;
				parm.addData("SINGLE_BATCH_NO", "") ;
				selectedRow=0;
			}else{
				yearMon=tableParm.getValue("SUM_YEARMON", selectedRow);
			    parm.addData("YEAR_MON", yearMon);	
				parm.addData("MON_BATCH_NO", tableParm.getValue("MON_BATCH_NO", selectedRow)) ;
				parm.addData("SINGLE_BATCH_NO", tableParm.getValue("SINGLE_BATCH_NO", selectedRow)) ;
			}
			parm.addData("INS_TYPE", INS_TYPE.getValue()) ;
			parm.setData("PIPELINE", "DataDown_zjkd") ;
			parm.setData("PLOT_TYPE", "T2") ;
			parm.addData("PARM_COUNT", 5);
//			System.out.println(parm);
			TParm resultParm = InsManager.getInstance().safe(parm,null);
//			System.out.println(resultParm);
		       ///////////////////////////////////////////////////数据计算
			int applyVisits=0;
			double applyAmt=0;
			double applyAmtShould=0;
			double retentionMoney=0;
			int varianceCaseVisits=0;
			double varianceCaseAmt=0;
			double servantAmt=0;
			double armyAiAmt=0;
			double medicalHelpAmt=0;
			double civilAmt=0;
			double accountPayAmt=0;
			double refoseAmt=0;
			double totalAmtAdjust=0;
			double insPayAmt=0;

			int rowCount=resultParm.getCount("APPLY_AMT");
			   for(int i=0;i<rowCount;i++){
//				   System.out.println(tot+"::::::"+parm.getDouble("REFUSE_AMT", i));
				   applyVisits=+resultParm.getInt("APPLY_VISITS", i);
				   applyAmt=+resultParm.getDouble("APPLY_AMT", i);//统筹基金申请支付金额
				   applyAmtShould=+resultParm.getDouble("APPLY_AMT_SHOULD", i);//统筹基金应支付金额
				   retentionMoney=+resultParm.getDouble("RETENTION_MONEY", i);//质量保证金
				   varianceCaseVisits=+resultParm.getInt("VARIANCE_CASE_VISITS", i);//变异病例人次
				   varianceCaseAmt=+resultParm.getDouble("VARIANCE_CASE_AMT", i);//变异病例超病种标准金额
				   servantAmt=+resultParm.getDouble("SERVANT_AMT", i);//公务员补助
				   armyAiAmt=+resultParm.getDouble("ARMY_AI_AMT", i);//军残补助
				   medicalHelpAmt=+resultParm.getDouble("MEDICAL_HELP_AMT", i);//城乡医疗救助
				   civilAmt=+resultParm.getDouble("CIVIL_AMT", i);//民政优抚
				   accountPayAmt=+resultParm.getDouble("ACCOUNT_PAY_AMT", i);//个人帐户
				   refoseAmt=+resultParm.getDouble("REFOSE_AMT", i);//拒付金额
				   totalAmtAdjust=+resultParm.getDouble("TOTAL_AMT_ADJUST", i);//调整支付
				   insPayAmt=+resultParm.getDouble("INS_PAY_AMT", i);//实际支付金额
			   }
		       //////////////////////////////////////////////////
		       double tot=
		       insPayAmt+
		       servantAmt+
		       armyAiAmt+
		       medicalHelpAmt+
		       civilAmt+
		       accountPayAmt;
	       
		   TParm printParm =new TParm();
		   printParm.setData("YEAR_MON","TEXT", yearMon.substring(0, 4)+
					"年"+yearMon.substring(5, 6)+"月");//期号
		   printParm.setData("INS_TYPE","TEXT", INS_TYPE.getSelectedText());//险种类别
		   printParm.setData("MON_BATCH_NO", "TEXT",tableParm.getValue("MON_BATCH_NO", selectedRow)) ;
		   printParm.setData("HOSP_NHI_NO", "TEXT",this.nhi_no) ;
		   printParm.setData("REGION_CHN_DESC","TEXT", this.nhi_name) ;
		   printParm.setData("APPLY_AMT","TEXT",df.format(applyAmt)) ;//统筹基金申请支付金额01
		   printParm.setData("APPLY_AMT_SHOULD","TEXT",df.format(applyAmtShould)) ;//统筹基金应支付金额04
		   printParm.setData("RETENTION_MONEY","TEXT",df.format(retentionMoney)) ;//质量保证金05
		   printParm.setData("VARIANCE_CASE_VISITS","TEXT",df.format(varianceCaseVisits)) ;//变异病例人次07
		   printParm.setData("VARIANCE_CASE_AMT","TEXT",df.format(varianceCaseAmt)) ;//变异病例超病种标准金额08
		   printParm.setData("SERVANT_AMT","TEXT",df.format(servantAmt)) ;//公务员补助09
		   printParm.setData("ARMY_AI_AMT","TEXT",df.format(armyAiAmt)) ;//军残补助10
		   printParm.setData("MEDICAL_HELP_AMT","TEXT",df.format(medicalHelpAmt)) ;//城乡医疗救助11
		   printParm.setData("CIVIL_AMT","TEXT",df.format(civilAmt)) ;//民政优抚12
		   printParm.setData("ACCOUNT_PAY_AMT","TEXT",df.format(accountPayAmt)) ;//个人帐户13
		   printParm.setData("REFOSE_AMT","TEXT",df.format(refoseAmt)) ;//拒付金额02
		   printParm.setData("TOTAL_AMT_ADJUST","TEXT",df.format(totalAmtAdjust)) ;//调整支付03
		   printParm.setData("INS_PAY_AMT","TEXT",df.format(insPayAmt)) ;//实际支付金额06
		   printParm.setData("TOT","TEXT",df.format(tot)) ;//合计
		   printParm.setData("APPLY_VISITS","TEXT",df.format(applyVisits)) ;//合计
//		   printParm.setData("PAY_AMT","TEXT",df.format(tot)) ;
//		   printParm.setData("AMT_IN_WORDS","TEXT",StringUtil.getInstance().numberToWord(tot)) ;
	       if(INS_TYPE.getValue().equals("310")){
	    	   this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSSinTotCZ.jhw",printParm);    	   
	       }else if(INS_TYPE.getValue().equals("390")){
	    	   this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSSinTotCJ.jhw",printParm);    
	       }
	   } 
		  /**
		   * 打印天津市基本医疗保险定点机构基金清算表
		   */
		  public void DataDown_zjkd_N()
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
				int selectedRow = this.tTable.getSelectedRow() ;

				TParm tableParm=this.tTable.getParmValue();
				TParm parm = new TParm() ;
				parm.addData("NHI_HOSP_NO", this.nhi_no) ;
				String yearMon="";
				if(selectedRow<0){
					String[] beginTimeArray = this.getValueString("YEAR_MON").split("-");
					yearMon=beginTimeArray[0].concat(beginTimeArray[1]);
					parm.addData("YEAR_MON", yearMon);	
					parm.addData("MON_BATCH_NO", "") ;
					parm.addData("SINGLE_BATCH_NO", "") ;
					selectedRow=0;
				}else{
					yearMon=tableParm.getValue("SUM_YEARMON", selectedRow);
				    parm.addData("YEAR_MON", yearMon);	
					parm.addData("MON_BATCH_NO", tableParm.getValue("MON_BATCH_NO", selectedRow)) ;
					parm.addData("SINGLE_BATCH_NO", tableParm.getValue("SINGLE_BATCH_NO", selectedRow)) ;
				}
				parm.addData("INS_TYPE", INS_TYPE.getValue()) ;
				parm.setData("PIPELINE", "DataDown_zjkd") ;
				parm.setData("PLOT_TYPE", "T2") ;
				parm.addData("PARM_COUNT", 5);
//				System.out.println(parm);
				TParm tempParm = InsManager.getInstance().safe(parm,null);
//				System.out.println(""+tempParm);
//			  resultParm.addData("NHI_HOSP_NO", this.nhi_no) ;
//			  resultParm.addData("REGION_CHN_DESC", this.nhi_name) ;
//			  resultParm.addData("YEAR",this.getYear(resultParm.getValue("YEAR_MON", 0))) ;
				TParm resultParm=new TParm();
//				resultParm.setData("YEAR_MON", "201507");
				resultParm.setData("YEAR_MON","TEXT", yearMon.substring(0, 4)+
						"年"+yearMon.substring(5, 6)+"月");//期号
				resultParm.setData("INS_TYPE","TEXT", INS_TYPE.getSelectedText());//险种类别
				resultParm.setData("BATCH_NO","TEXT", tableParm.getValue("SINGLE_BATCH_NO", selectedRow));//汇总批号
				resultParm.setData("NHI_HOSP_NO","TEXT",this.nhi_no) ;//机构编码
				resultParm.setData("YEAR","TEXT",yearMon.substring(0, 4)) ;//协议年度
				resultParm.setData("REGION_CHN_DESC","TEXT",this.nhi_name) ;//机构编码

				resultParm.setData("MON_QUOTA_AMT","TEXT",df.format(0)) ;//本月指标
				resultParm.setData("LPER_OVER_AMT","TEXT",df.format(0)) ;//上月指标结余
				resultParm.setData("PRE_PAY_AMT","TEXT",df.format(0)) ;//本月联网垫付统筹申请
				resultParm.setData("LMON_PRE_OVER_AMT","TEXT",df.format(0)) ;//上月联网垫付超标剩余
				resultParm.setData("VERIFY_REFUSE_AMT","TEXT",df.format(0)) ;//本月筛查审核拒付
				resultParm.setData("LMON_OVERREF_AMT","TEXT",df.format(0)) ;//上月筛查审核拒付剩余
				resultParm.setData("CHANGE_VREFUSE_AMT","TEXT",df.format(0)) ;//上月筛查审核拒付调整
				resultParm.setData("TPER_QUOTA_AMT","TEXT",df.format(0)) ;//本月有效指标
				
				resultParm.setData("NHI_AMT","TEXT",df.format(tempParm.getDouble("APPLY_AMT", 0))) ;//本月联网统筹申请
				resultParm.setData("CHANGE_PAY_AMT","TEXT",df.format(0)) ;//本月统筹调整支付
				resultParm.setData("REFUSE_AMT","TEXT",df.format(tempParm.getDouble("REFOSE_AMT", 0))) ;//本月智能审核拒付
				resultParm.setData("CHANGE_REFUSE_AMT","TEXT",df.format(0)) ;//本月智能审核拒付调整
				resultParm.setData("OVER_TOTAL_AMT","TEXT",df.format(0)) ;//上月统筹申请超标
				resultParm.setData("TPER_NHI_AMT","TEXT",df.format(tempParm.getDouble("APPLY_AMT", 0))) ;//本月有效统筹申请
				resultParm.setData("TPER_VERIFY_AMT","TEXT",df.format(0)) ;//本月统筹应支付金额
				resultParm.setData("EXA_AMT","TEXT",df.format(tempParm.getDouble("RETENTION_MONEY", 0))) ;//本月考核预留金额

				   double applyAmtShould=+tempParm.getDouble("APPLY_AMT_SHOULD", 0);//统筹基金应支付金额
				   double retentionMoney=+tempParm.getDouble("RETENTION_MONEY", 0);//质量保证金
				   double insPayAmt=applyAmtShould-retentionMoney;
				resultParm.setData("TPER_PAY_AMT","TEXT",df.format(insPayAmt)) ;//本月统筹支付金额
				resultParm.setData("QUOTA_OVER_AMT","TEXT",df.format(0)) ;//本月指标结余
				resultParm.setData("OVER_NHI_AMT","TEXT",df.format(0)) ;//本月统筹申请超标
				resultParm.setData("OTHER_AMT","TEXT","0.00") ;//其它
				
				resultParm.setData("LYEAR_GET_AMT","TEXT",df.format(0)) ;//清算应收回金额
				resultParm.setData("LYEAR_GETREAL_AMT","TEXT",df.format(0)) ;//清算实际收回金额
				resultParm.setData("LYEAR_OVERGET_AMT","TEXT",df.format(0)) ;//清算收回剩余额
				
				resultParm.setData("TMON_PAY_REAL_AMT","TEXT",df.format(insPayAmt)) ;//本月实际统筹支付
				resultParm.setData("SERVANT_AMT","TEXT",df.format(tempParm.getDouble("SERVANT_AMT", 0))) ;//公务员补助金额
				resultParm.setData("ARMAY_AMT","TEXT",df.format(tempParm.getDouble("ARMY_AI_AMT", 0))) ;//军残补助金额
				resultParm.setData("CANDC_AMT","TEXT",df.format(tempParm.getDouble("MEDICAL_HELP_AMT", 0))) ;//民政补助金额
				resultParm.setData("CIVIL_AMT","TEXT",df.format(tempParm.getDouble("CIVIL_AMT", 0))) ;//民政优抚金额
				resultParm.setData("ACCOUT_PAY_AMT","TEXT",df.format(tempParm.getDouble("ACCOUNT_PAY_AMT", 0))) ;//个人账户
				resultParm.setData("TPER_TOTAL_AMT","TEXT",df.format(tempParm.getDouble("INS_PAY_AMT", 0))) ;//本月支付合计

				resultParm.setData("TMON_PAY_REAL_AMT_D","TEXT",StringUtil.getInstance().numberToWord(insPayAmt)) ;//本月实际统筹支付
				resultParm.setData("SERVANT_AMT_D","TEXT",StringUtil.getInstance().numberToWord(tempParm.getDouble("SERVANT_AMT", 0))) ;//公务员补助金额
				resultParm.setData("ARMAY_AMT_D","TEXT",StringUtil.getInstance().numberToWord(tempParm.getDouble("ARMY_AI_AMT", 0))) ;//军残补助金额
				resultParm.setData("CANDC_AMT_D","TEXT",StringUtil.getInstance().numberToWord(tempParm.getDouble("MEDICAL_HELP_AMT", 0))) ;//民政补助金额
				resultParm.setData("CIVIL_AMT_D","TEXT",StringUtil.getInstance().numberToWord(tempParm.getDouble("CIVIL_AMT", 0))) ;//民政优抚金额
				resultParm.setData("ACCOUT_PAY_AMT_D","TEXT",StringUtil.getInstance().numberToWord(tempParm.getDouble("ACCOUNT_PAY_AMT", 0))) ;//个人账户
				resultParm.setData("TPER_TOTAL_AMT_D","TEXT",StringUtil.getInstance().numberToWord(tempParm.getDouble("INS_PAY_AMT", 0))) ;//本月支付合计

				Timestamp date = SystemTool.getInstance().getDate();
				String d=date.toString().substring(0, 10).replace("-", "");
				resultParm.setData("DATE1","TEXT",d.substring(0,4)+"年"+
						d.substring(4,6)+"月"+d.substring(6,8)+"日") ;//定点机构
				resultParm.setData("DATE2","TEXT",d.substring(0,4)+"年"+
						d.substring(4,6)+"月"+d.substring(6,8)+"日") ;//定点机构
			    this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSSinCityCost.jhw",resultParm);
				
		  }  


}
