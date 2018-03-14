package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title:住院患者医保明细表</p>
 *
 * <p>Description:住院患者医保明细表 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author lim
 * @version 1.0
 */
public class BILInpInsDetailControl extends TControl {
	
	/**
	 * 初始化
	 */
	public void onInit(){
		Timestamp date = SystemTool.getInstance().getDate() ;
		String transDate = StringTool.getString(date, "yyyy/MM/dd") ;
		this.setValue("START_DATE", transDate) ;
		this.setValue("END_DATE", transDate) ;
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		String startDate = this.getValueString("START_DATE") ;
		String endDate = this.getValueString("END_DATE") ;
		 if(startDate.length()==0){
				messageBox("起始时间不能为空!") ;
				return ;
		}
		 if(endDate.length()==0){
				messageBox("结束时间不能为空!") ;
				return ;
		} 
		String sql = 
			" SELECT A.IPD_NO,C.PAT_NAME,D.DEPT_CHN_DESC DEPT_DESC,E.STATION_DESC," +
			" A.MR_NO,A.AR_AMT SUM,A.TJINS03+A.TJINS04 COORDINATION_PAY,A.TJINS02 HELP_PAY," +
			" A.TJINS05 ILLNESS_SUBSIDY_AMT,A.OWN_AMT OWN_PAY,A.TJINS01 OWN_ACCOUNT_PAY,A.CHARGE_DATE"+
			" FROM BIL_IBS_RECPM A,ADM_INP B,SYS_PATINFO C,SYS_DEPT D,SYS_STATION E"+
			" WHERE  A.CASE_NO =B.CASE_NO"+
			" AND  A.MR_NO = C.MR_NO"+
			" AND B.DEPT_CODE = D.DEPT_CODE"+
			" AND B.STATION_CODE = E.STATION_CODE"+
			" AND A.CHARGE_DATE BETWEEN TO_DATE('" + SystemTool.getInstance().getDateReplace(startDate, true)+ "','YYYYMMDDHH24MISS')"+ 
			" AND  TO_DATE('" +SystemTool.getInstance().getDateReplace(endDate.substring(0,10), false) + "','YYYYMMDDHH24MISS')"+
			" AND (A.PAY_INS_CARD IS NOT NULL OR A.PAY_INS IS NOT NULL)" +
			//" AND B.CONFIRM_NO IS NOT NULL"+			
			" ORDER BY  A.IPD_NO";
//		 System.out.println("sql====="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		double sumPaySum=0;//总金额
		double coordinationPaySum=0;//统筹支付（含ARMYAI_AMT补助金额）
		double helpPaySum=0;//救助支付
		double ownPaySum=0;//个人支付
		double ownAccountPaySum=0;//个人账户
		double illnesssubsidyamt=0;//城乡大病
		int m =0;
		for (int i = 0; i < result.getCount(); i++) {
			sumPaySum+=	 StringTool.round(result.getDouble("SUM", i), 2)    ;
			coordinationPaySum+=	 StringTool.round(result.getDouble("COORDINATION_PAY", i), 2);
			helpPaySum+=	 StringTool.round(result.getDouble("HELP_PAY", i), 2);
			ownPaySum+= StringTool.round(	result.getDouble("OWN_PAY", i), 2);
			ownAccountPaySum+=	 StringTool.round(result.getDouble("OWN_ACCOUNT_PAY", i), 2);
			illnesssubsidyamt+=	 StringTool.round(result.getDouble("ILLNESS_SUBSIDY_AMT", i), 2);
			m=m+1;
		}
		result.addData( "SUM"  ,    StringTool.round( sumPaySum, 2) );
		result.addData(  "COORDINATION_PAY" , StringTool.round( coordinationPaySum  , 2)  );
		result.addData(  "HELP_PAY" , StringTool.round( helpPaySum  , 2)  );
		result.addData(  "ILLNESS_SUBSIDY_AMT" , StringTool.round( illnesssubsidyamt  , 2)  );
		result.addData(   "OWN_PAY", StringTool.round( ownPaySum  , 2)  );
		result.addData(  "OWN_ACCOUNT_PAY" , StringTool.round( ownAccountPaySum   , 2) );
		result.addData("IPD_NO", "") ;
		result.addData("PAT_NAME", "") ;
		result.addData("DEPT_DESC", "合计") ;
		result.addData("MR_NO","");	
		result.addData("CHARGE_DATE","");
		result.addData("STATION_DESC", m) ;	
    	//   获得错误信息消息
    	if (result.getErrCode() < 0) {
    	    messageBox(result.getErrText());
    	    return;      
    	}
        if (result.getCount() <= 0) {
            messageBox("查无数据");
            this.callFunction("UI|TTABLE|setParmValue", new TParm());
            return;
        }		
        this.callFunction("UI|TTABLE|setParmValue", result);
	}

	/**
	 * 清空
	 */
	public void onClear(){
		this.clearValue("START_DATE;END_DATE") ;
		this.callFunction("UI|TTABLE|setParmValue", new TParm());
	}
	
	/**
	 * 打印
	 */
	public void onPrint(){
		TTable dataTable = (TTable)this.getComponent("TTABLE") ;
		TParm parm = dataTable.getShowParmValue() ;
		
		if(parm.getCount()<=0){
			messageBox("没有打印数据!") ;
			return ;
		}
		TParm tableParm = new TParm() ;
		DecimalFormat df = new DecimalFormat("##########0.00");
		//=====modify-begin (by wanglong 20120615)===============================
		for (int i = 0; i < parm.getCount(); i++) {
		
//		double sumPaySum=0;
//		double coordinationPaySum=0;
//		double helpPaySum=0;
//		double ownPaySum=0;
//		double ownAccountPaySum=0;
//		int i = 0;
//		for (; i < parm.getCount(); i++) {
	    //======modify-end========================================================
			tableParm.addData("IPD_NO", parm.getValue("IPD_NO", i)) ;
			tableParm.addData("PAT_NAME", parm.getValue("PAT_NAME", i)) ;
			tableParm.addData("DEPT_DESC", parm.getValue("DEPT_DESC", i)) ;
			tableParm.addData("SUM", df.format(parm.getDouble("SUM", i))) ;
			tableParm.addData("COORDINATION_PAY", df.format(parm.getDouble("COORDINATION_PAY", i))) ;
			tableParm.addData("HELP_PAY", df.format(parm.getDouble("HELP_PAY", i))) ;
			tableParm.addData("OWN_PAY", df.format(parm.getDouble("OWN_PAY", i))) ;
			tableParm.addData("OWN_ACCOUNT_PAY", df.format(parm.getDouble("OWN_ACCOUNT_PAY", i))) ;
			tableParm.addData("ILLNESS_SUBSIDY_AMT", df.format(parm.getDouble("ILLNESS_SUBSIDY_AMT", i))) ;
			//=====modify-begin (by wanglong 20120615)===============================
			
//			 sumPaySum+=parm.getDouble("SUM", i);
//			 coordinationPaySum+=parm.getDouble("COORDINATION_PAY", i);
//			 helpPaySum+=parm.getDouble("HELP_PAY", i);
//			 ownPaySum+=parm.getDouble("OWN_PAY", i);
//			 ownAccountPaySum+=parm.getDouble("OWN_ACCOUNT_PAY", i);

			
			tableParm.addData("MR_NO",parm.getValue("MR_NO", i));	
			tableParm.addData("CHARGE_DATE",parm.getValue("CHARGE_DATE", i));
			tableParm.addData("STATION_DESC", parm.getValue("STATION_DESC", i)) ;
			//======modify-end========================================================
		}
		//=====modify-begin (by wanglong 20120615)===============================

//		tableParm.addData("SUM", df.format(sumPaySum)) ;
//		tableParm.addData("COORDINATION_PAY", df.format(coordinationPaySum)) ;
//		tableParm.addData("HELP_PAY", df.format(helpPaySum)) ;
//		tableParm.addData("OWN_PAY", df.format(ownPaySum)) ;
//		tableParm.addData("OWN_ACCOUNT_PAY", df.format(ownAccountPaySum)) ;
//		
//		tableParm.addData("IPD_NO", "") ;
//		tableParm.addData("PAT_NAME", "") ;
//		tableParm.addData("DEPT_DESC", "合计") ;
//		tableParm.addData("MR_NO","");	
//		tableParm.addData("CHARGE_DATE","");
//		tableParm.addData("STATION_DESC", "") ;			
		//======modify-end========================================================
		tableParm.setCount(tableParm.getCount("IPD_NO")) ;
		//=====modify-begin (by wanglong 20120615)===============================
		tableParm.addData("SYSTEM", "COLUMNS", "IPD_NO");
		tableParm.addData("SYSTEM", "COLUMNS", "PAT_NAME"); 
		tableParm.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
		tableParm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		tableParm.addData("SYSTEM", "COLUMNS", "MR_NO");	
		tableParm.addData("SYSTEM", "COLUMNS", "SUM");
		tableParm.addData("SYSTEM", "COLUMNS", "COORDINATION_PAY");
		tableParm.addData("SYSTEM", "COLUMNS", "HELP_PAY");
		tableParm.addData("SYSTEM", "COLUMNS", "ILLNESS_SUBSIDY_AMT");
		tableParm.addData("SYSTEM", "COLUMNS", "OWN_PAY");	
		tableParm.addData("SYSTEM", "COLUMNS", "OWN_ACCOUNT_PAY");	
		tableParm.addData("SYSTEM", "COLUMNS", "CHARGE_DATE");
		//======modify-end========================================================
		TParm data = new TParm() ;
		String startDate = this.getValueString("START_DATE") ;
		String endDate = this.getValueString("END_DATE") ;		
		data.setData("START_DATE","TEXT", startDate.replace("-", "/").substring(0,10)) ;
		data.setData("END_DATE","TEXT",endDate.replace("-", "/").substring(0,10)) ;
		data.setData("USER","TEXT",Operator.getName());//修改 添加收费员签章 20130722 caoyong
		data.setData("TABLE", tableParm.getData()) ;
//		 System.out.println("tableParm====="+tableParm);
		this.openPrintWindow("%ROOT%\\config\\prt\\bil\\BILInpInsDetail.jhw",data);
	}
	/**
	 * 导出Excel
	 * */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TTABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "住院患者医保明细表");
	}
}
