package com.javahis.ui.ekt;

import java.sql.Timestamp;

import jdo.ekt.EKTTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.control.TControl;

/**
 * <p>Title: 门诊建行预交金收入表</p>
 *
 * <p>Description: 门诊建行预交金收入表 </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author WangLong 20120920 
 * @version 1.0
 */
public class EKTCCBTradeQueryControl extends TControl {
	
	/**
     * 初始化方法
     */
    public void onInit() {
    	setValue("OPT_USER", Operator.getID());
    	Timestamp today = SystemTool.getInstance().getDate();
    	String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", today);
    }
    
    /**
     * 查询方法
     */
	public void onQuery(){
		String startDate = this.getValueString("START_DATE");
		String endDate = this.getValueString("END_DATE");
		String optUser = this.getValue("OPT_USER")+"";
		TParm parm = new TParm();
		parm.setData("START_DATE", startDate);
		parm.setData("END_DATE", endDate);
		parm.setData("OPT_USER", optUser);
		TParm ccbTrade = EKTTool.getInstance().queryCCBTrade(parm);
		if (ccbTrade.getErrCode() < 0) {
			messageBox(ccbTrade.getErrText());
			return;
		}
		if (ccbTrade.getCount() <= 0) {
			messageBox("E0008");
			this.callFunction("UI|TABLE|setParmValue", new TParm());
			return;
		}
		this.callFunction("UI|TABLE|setParmValue", new TParm());
		this.callFunction("UI|TABLE|setParmValue", ccbTrade);
	}
	
	/**
	 * 打印
	 */
	public void onPrint(){
		TParm data = (TParm) this.callFunction("UI|TABLE|getShowParmValue");
		if(data.getCount("USER_NAME")==0) return;
		double income = 0.0;
		double expense = 0.0;
		for (int i = 0; i < data.getCount("USER_NAME"); i++) {
			if (data.getDouble("AMT", i) >= 0) {
				income += data.getDouble("AMT", i);
			} else
				expense += data.getDouble("AMT", i);
		}
		data.addData("OPT_DATE","");
		data.addData("USER_NAME","");
		data.addData("STATE","");
		data.addData("BUSINESS_NO","");
		data.addData("MR_NO","");
		data.addData("CARD_NO","");
		data.addData("PAT_NAME","");
		data.addData("AMT","");
		data.addData("OTHER","");
		data.addData("OPT_DATE","");
		data.addData("USER_NAME","");
		data.addData("STATE","收费合计:");
		data.addData("BUSINESS_NO","");
		data.addData("MR_NO","");
		data.addData("CARD_NO","");
		data.addData("PAT_NAME","");
		data.addData("AMT"," "+income+"0");
		data.addData("OTHER","");
		data.addData("OPT_DATE","");
		data.addData("USER_NAME","");
		data.addData("STATE","退费合计:");
		data.addData("BUSINESS_NO","");
		data.addData("MR_NO","");
		data.addData("CARD_NO","");
		data.addData("PAT_NAME","");
		data.addData("AMT",""+expense+"0");
		data.addData("OTHER","");
		data.addData("OPT_DATE","");
		data.addData("USER_NAME","");
		data.addData("STATE","总计:");
		data.addData("BUSINESS_NO","");
		data.addData("MR_NO","");
		data.addData("CARD_NO","");
		data.addData("PAT_NAME","");
		data.addData("AMT",""+(income+expense)+"0");
		data.addData("OTHER","");
		data.setCount(data.getCount("OPT_USER"));
		data.addData("SYSTEM", "COLUMNS", "OPT_DATE");
		data.addData("SYSTEM", "COLUMNS", "USER_NAME");
		data.addData("SYSTEM", "COLUMNS", "STATE");
		data.addData("SYSTEM", "COLUMNS", "BUSINESS_NO");
		data.addData("SYSTEM", "COLUMNS", "MR_NO");
		data.addData("SYSTEM", "COLUMNS", "CARD_NO");
		data.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		data.addData("SYSTEM", "COLUMNS", "AMT");
		data.addData("SYSTEM", "COLUMNS", "OTHER");
		data.setCount(data.getCount("AMT"));
		TParm tableParm = new TParm();
		String date = SystemTool.getInstance().getDate().toString();
		tableParm.setData("TITLE1", "TEXT", Operator.getHospitalCHNFullName());
		tableParm.setData("PRINTDATE", "TEXT", "打印日期: "+date.substring(0, 4)+
    			"/"+date.substring(5, 7)+"/"+date.substring(8, 10));
		tableParm.setData("TABLE", data.getData());//把表格数据添加进要打印的parm
		this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTCCBTradeQuery.jhw",tableParm);
	}
	
	/**
	 * 清空
	 */
	public void onClear(){
		clearValue("OPT_USER");
		callFunction("UI|TABLE|setParmValue", new TParm());
	}
}
