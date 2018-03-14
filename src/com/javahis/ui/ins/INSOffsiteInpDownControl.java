package com.javahis.ui.ins;

import java.text.DecimalFormat;

import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 异地住院联网月清算表信息下载</p>
 *
 * <p>Description:异地住院联网月清算表信息下载</p>
 *
 * <p>Copyright: Copyright (c) 2016</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangs  
 * @version 1.0
 */

public class INSOffsiteInpDownControl  extends TControl {
	private TTable tTable ;
//	private TTextFormat yearMon ;
	private String nhi_no ;
	private String nhi_name;
	public DecimalFormat df = new DecimalFormat("##########0.00");
	
	/**
	 * 初始化方法
	 */
	public void onInit() {
		this.tTable = (TTable) this.getComponent("TTABLE") ;
		tTable.setText("异地住院联网月清算信息下载");
//		this.yearMon = (TTextFormat) this.getComponent("YEAR_MON") ;
		TParm regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());
		this.nhi_no = regionParm.getData("NHI_NO", 0).toString();// 获取HOSP_NHI_NO
		this.nhi_name= regionParm.getData("REGION_CHN_DESC", 0).toString();// 获取REGION_CHN_DESC
	    this.setValue("YEAR_MON", SystemTool.getInstance().getDate());
	}
	/**
	 * 清空
	 */
	public void onClear(){
		this.setValue("YEAR_MON", "") ;
		this.callFunction("UI|TTABLE|setParmValue", new TParm());
		tTable.setText("异地住院联网月清算信息下载");
	}
	/**
	 * 设置TABLE
	 * @param tabParm
	 * @param tag
	 */
	public void setUITTable(TParm tabParm,String tag){
		if(tag.equals("A")){
			tTable.setHeader("汇总批号,100;本期发生金额,100;本期发生人次,100;申请基金支付金额,120;申请基金支付人次,120;医保拒付金额,120;调整补支付金额,120;调整补支付人次,120;调整减支付金额,120;调整减支付人次,120;统筹基金支付金额,120");
			tTable.setParmMap("BATCH_NO;TOTAL_AMT;SUM_PERTIME;SQTOT_AMT;SQTOT_COUNT;REFOSE_AMT;AD_PAY_AMT_ADD;ADD_NUM;AD_PAY_AMT_REDUCE;REDUCE_NUM;APPLY_AMT");
			tTable.setText("异地住院联网月清算信息下载");
		}else if(tag.equals("B")){
			tTable.setHeader("姓名,100;性别,60;上传日期,100;发生金额,100;申报金额,100");
			tTable.setParmMap("PAT_NAME;SEX_DESC;UPLOAD_DATE;TOTAL_AMT;INS_AMT");
			tTable.setText("异地住院联网月清算明细下载");
		}else if(tag.equals("C")){
			tTable.setHeader("汇总批号,100;就医流水号,100;审核扣款金额,100;审核扣款原因,400");
			tTable.setParmMap("BATCH_NO;ADM_SEQ;AUDIT_DEBIT_AMT;AUDIT_DEBIT_REASONS");
			tTable.setText("异地住院联网月审核扣款信息下载");
		}
		tTable.setParmValue(tabParm);

	}

	  /**
	    * 异地住院联网月清算信息下载
	    */
	   public void onLiquidationTablePrint(){
		    TParm parm = new TParm() ;
			parm.addData("NHI_HOSP_NO", this.nhi_no) ;
			String[] beginTimeArray = this.getValueString("YEAR_MON").split("-");
			String yearMon=beginTimeArray[0].concat(beginTimeArray[1]);
			parm.addData("LIQUIDATION_PERIOD", yearMon);	
			parm.setData("PIPELINE", "DataDown_yjks") ;
			parm.setData("PLOT_TYPE", "A3") ;
//			parm.setData("PIPELINE", "DataDown_ydjs") ;
//			parm.setData("PLOT_TYPE", "U") ;
			parm.addData("PARM_COUNT", 2);
//			System.out.println(parm);
			TParm resultParm = InsManager.getInstance().safe(parm,null);
//			System.out.println(resultParm);
			this.setUITTable(resultParm,"A");
		       ///////////////////////////////////////////////////数据计算
			double totalAmt=resultParm.getDouble("TOTAL_AMT", 0);//本期发生金额
			int sumPertime=resultParm.getInt("SUM_PERTIME", 0);//本期发生人次
			double sqtotAmt=resultParm.getDouble("SQTOT_AMT", 0);//申请基金支付金额
			int sqtotCount=resultParm.getInt("SQTOT_COUNT", 0);//申请基金支付人次
			double refoseAmt=resultParm.getDouble("REFOSE_AMT", 0);//医保拒付金额
			double adPayAmtAdd=resultParm.getDouble("AD_PAY_AMT_ADD", 0);//调整补支付金额
			int AddNum=resultParm.getInt("ADD_NUM", 0);//调整补支付人次
			double adPayAmtReduce=resultParm.getDouble("AD_PAY_AMT_REDUCE", 0);//调整减支付金额
			int reduceNum=resultParm.getInt("REDUCE_NUM", 0);//调整减支付人次
			double applyAmt=resultParm.getDouble("APPLY_AMT", 0);//统筹基金支付金额
		    double tot=sqtotAmt-refoseAmt+adPayAmtAdd+adPayAmtReduce;
		       //////////////////////////////////////////////////
		   
		   TParm printParm =new TParm();
		   printParm.setData("YEAR_MON","TEXT", beginTimeArray[0]+
					"年"+beginTimeArray[1]+"月");//期号
		   printParm.setData("HOSP_NHI_NO", "TEXT",this.nhi_no) ;
		   printParm.setData("REGION_CHN_DESC","TEXT", this.nhi_name) ;
		   printParm.setData("BATCH_NO","TEXT",resultParm.getValue("BATCH_NO", 0)) ;//汇总批号
		   printParm.setData("SUM_PERTIME","TEXT",sumPertime) ;//本期发生人次
		   printParm.setData("TOTAL_AMT","TEXT",df.format(totalAmt)) ;//本期发生金额
		   printParm.setData("SQTOT_COUNT","TEXT",sqtotCount) ;//申请基金支付人次
		   printParm.setData("SQTOT_AMT","TEXT",df.format(sqtotAmt)) ;//申请基金支付金额
		   printParm.setData("REFOSE_AMT","TEXT",df.format(refoseAmt)) ;//医保拒付金额
		   printParm.setData("ADD_NUM","TEXT",AddNum) ;//调整补支付人次
		   printParm.setData("AD_PAY_AMT_ADD","TEXT",df.format(adPayAmtAdd)) ;//调整补支付金额
		   printParm.setData("REDUCE_NUM","TEXT",reduceNum) ;//调整减支付人次
		   printParm.setData("AD_PAY_AMT_REDUCE","TEXT",df.format(adPayAmtReduce)) ;//调整减支付金额
		   printParm.setData("APPLY_AMT","TEXT",StringUtil.getInstance().numberToWord(applyAmt)) ;//统筹基金支付金额
		   printParm.setData("TOT","TEXT",StringUtil.getInstance().numberToWord(tot)) ;//合计
	       this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSLiquidationTable.jhw",printParm);    
	   } 
	   /**
	    * 异地住院联网月清算明细下载
	    */
	   public void onLiquidationDetailsDown(){
			int selectedRow = this.tTable.getSelectedRow() ;

			if(selectedRow<0){
				messageBox("请选中一条记录") ;
				return ;
			}
			TParm tableParm=this.tTable.getParmValue();
			TParm parm = new TParm() ;
			parm.addData("NHI_HOSP_NO", this.nhi_no) ;
			parm.addData("BATCH_NO", tableParm.getValue("BATCH_NO", selectedRow)) ;
			parm.setData("PIPELINE", "DataDown_yjkd") ;
			parm.setData("PLOT_TYPE", "A3") ;
//			parm.setData("PIPELINE", "DataDown_ydjs") ;
//			parm.setData("PLOT_TYPE", "X") ;
			parm.addData("PARM_COUNT", 2);
			TParm resultParm = InsManager.getInstance().safe(parm,null);
			String admSeq="";
			int rowCount=resultParm.getCount("ADM_SEQ");
			System.out.println(resultParm);
			for(int i=0;i<rowCount;i++){
				admSeq=admSeq+"'"+resultParm.getValue("ADM_SEQ", i)+"'";
				if((rowCount-1)>i){
					admSeq=admSeq+",";
				}
			}
			System.out.println(admSeq);
			   String sql=" SELECT A.PAT_NAME,A.SEX_DESC,A.UPLOAD_DATE,A.TOTAL_AMT,A.INS_AMT "+
			   " FROM INS_YD_DOWNLOAD A "+
			   " WHERE A.ADM_SEQ IN ("+admSeq+") ";
			   TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			this.setUITTable(result,"B");
	   }
	   /**
	    * 异地住院联网月审核扣款信息下载
	    */
	   public void onAuditChargesDown(){
			int selectedRow = this.tTable.getSelectedRow() ;

			if(selectedRow<0){
				messageBox("请选中一条记录") ;
				return ;
			}
			TParm tableParm=this.tTable.getParmValue();
			TParm parm = new TParm() ;
			parm.addData("NHI_HOSP_NO", this.nhi_no) ;
			parm.addData("BATCH_NO", tableParm.getValue("BATCH_NO", selectedRow)) ;
			parm.addData("ADM_SEQ", "") ;
			parm.setData("PIPELINE", "DataDown_yjkd") ;
			parm.setData("PLOT_TYPE", "A1") ;
//			parm.setData("PIPELINE", "DataDown_ydjs") ;
//			parm.setData("PLOT_TYPE", "V") ;
			parm.addData("PARM_COUNT", 3);
			TParm resultParm = InsManager.getInstance().safe(parm,null);
			this.setUITTable(resultParm,"C");
	   }
		/**
		 * 导出Excel
		 * */
		public void onExport() {
			// 得到UI对应控件对象的方法（UI|XXTag|getThis）
			TTable table = (TTable) callFunction("UI|TTABLE|getThis");
			ExportExcelUtil.getInstance().exportExcel(table, tTable.getText());
		}

}
