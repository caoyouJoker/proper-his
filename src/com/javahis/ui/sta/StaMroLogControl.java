package com.javahis.ui.sta;

import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

public class StaMroLogControl extends TControl {
	private TTable table;
	/**
	 * 页面初始化
	 */
	public void onInit() {
		super.init();
		Timestamp date = SystemTool.getInstance().getDate();
		String e = StringTool.getString(date, "yyyy/MM/dd");
		// 设置初始时间
		this.setValue("S_DATE", e.substring(0, 8) + "01");
		this.setValue("E_DATE", e);
		table = (TTable) this.getComponent("TABLE");
		onQuery();
	}

	/**
	 * 查询
	 */
	public void onQuery(){
    	String s=this.getValueString("S_DATE");
    	String e=this.getValueString("E_DATE");
    	if(s.equals("")||e.equals("")){
    		this.messageBox("日期不能为空");
    		return;		
    	}
    	String sline=StringTool.getString((Timestamp)getValue("S_DATE"), "yyyyMMddHHmmss");
    	String eline=StringTool.getString((Timestamp)getValue("E_DATE"), "yyyyMMddHHmmss");
    	String sql="SELECT LOG_DATE, RESULT, ERRORCODE, ID, LOADDATE, STATUS, ERRORMSG, HASERRORMSG, GRADE0, GRADE1,"
                   +"GRADE2, GRADE3, STOCKED, TOTAL, GRADE ,LOG FROM STA_MRO_LOG WHERE LOG_DATE BETWEEN "+sline
                   +" AND "+eline.substring(0, 10)+"235959 ORDER BY LOG_DATE DESC";
    	TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm.getCount()<=0){
    		this.messageBox("没有数据");
    		table.removeRowAll();
    		return;
    	}
    	for(int i=0;i<parm.getCount();i++){
    		TParm row=parm.getRow(i);
    		Timestamp logDate=StringTool.getTimestamp(row.getValue("LOG_DATE"), "yyyyMMddHHmmss");
    		parm.setData("LOG_DATE", i,StringTool.getString(logDate, "yyyy/MM/dd HH:mm:ss"));
    		Timestamp loadDate=row.getTimestamp("LOADDATE");
    		parm.setData("LOADDATE", i, StringTool.getString(loadDate, "yyyy/MM/dd HH:mm:ss"));
    	}
    	table.setParmValue(parm);
    }
}
