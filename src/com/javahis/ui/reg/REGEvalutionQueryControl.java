package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TToolButton;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.emr.AnimationWindowUtils;
import com.javahis.util.OdoUtil;
import com.sun.awt.AWTUtilities;

public class REGEvalutionQueryControl extends TControl{
	TTable table;
	String sql;
	
	
	public void onInit(){
		table = (TTable) this.getComponent("TABLE");
		callFunction("UI|TABLE|addEventListener",
                "TABLE->" + TTableEvent.DOUBLE_CLICKED, this, "onTABLEClicked");
		initPage();
		
	}
	
	/**
     * 初始化界面
     */
    public void initPage() { 
    	this.setValue("DEPT_CODE", Operator.getDept());
		this.setValue("CLINICAREA_CODE", Operator.getStation());
		this.setValue("TRIAGE_USER", Operator.getID());
		this.setValue("ADM_TYPE", "E");
		Timestamp today = SystemTool.getInstance().getDate();
		this.setValue("S_DATE", today);
		this.setValue("E_DATE", today);
		this.setValue("S_TIME", StringTool.getTimestamp("00:00:00", "hh:mm:ss")); 
	    this.setValue("E_TIME", StringTool.getTimestamp("23:59:59", "hh:mm:ss"));
    }
    
    public void onTABLEClicked(int row){
    	if (row < 0) {
            return;
        }
    	String triageNo = table.getItemString(row, "TRIAGE_NO");
    	String mrNo = table.getItemString(row, "MR_NO");
    	TParm parm = new TParm();
    	parm.setData("TRIAGE_NO", triageNo);
    	parm.setData("FLG", "SAVE");
    	if(mrNo.length()>0){
    		Pat pat = Pat.onQueryByMrNo(mrNo);
    		parm.setData("MR_NO", mrNo);
        	parm.setData("PAT_NAME", pat.getName());
        	parm.setData("SEX", pat.getSexString());
        	parm.setData("AGE", OdoUtil.showAge(pat.getBirthday(),
     				SystemTool.getInstance().getDate()));
    	}else{
    		parm.setData("MR_NO", "");
        	parm.setData("PAT_NAME", "");
        	parm.setData("SEX", "");
        	parm.setData("AGE", "");
    	}
//    	List l = new ArrayList();
//		l.add(parm);
//		l.add(this);
    	// modified by wangqing 20170623
    	final TParm parm1 = new TParm();
		parm1.setData("TRIAGE_NO", triageNo);
		parm1.setData("FLG", "QUERY");
		
		// modified by wangqing 20170629
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				openWindow(
						"%ROOT%\\config\\reg\\REGPreviewingTriage.x", parm1, false);
			}
		});	
    }
    
	public void onQuery() {
		String levelCode = this.getValueString("LEVEL_CODE");
		String triageUser = this.getValueString("TRIAGE_USER");
		String deptCode = this.getValueString("DEPT_CODE");
		String clinicreaCode = this.getValueString("CLINICAREA_CODE");
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("S_DATE")), "yyyyMMdd")
				+ StringTool.getString((Timestamp) this.getValue("S_TIME"),
						"HHmmss");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("E_DATE")), "yyyyMMdd")
				+ StringTool.getString((Timestamp) this.getValue("E_TIME"),
						"HHmmss");
		
		 String whereSql = " AND    TO_CHAR (A.ADM_DATE, 'YYYYMMDD')"
				+ " || TO_CHAR (A.COME_TIME, 'HH24MISS') BETWEEN '"+startTime+"'"
				+ " AND '"+endTime+"'";
		
		if(this.getValueString("TRIAGE_NO").length() > 0){
			whereSql += " AND A.TRIAGE_NO='"+getValueString("TRIAGE_NO")+"'";
		}
		if(this.getValueString("ADM_TYPE").length() > 0){
			whereSql += " AND A.ADM_TYPE='"+getValueString("ADM_TYPE")+"'";
		}
		if(deptCode.length() > 0){
			whereSql += " AND A.DEPT_CODE='"+deptCode+"'";
		 }
		 if(clinicreaCode.length() > 0){
			 whereSql += " AND A.CLINICAREA_CODE='"+clinicreaCode+"'";
		 }
		 // modified by wangqing 20170629 分诊人不作为查询条件
//		 if(triageUser.length() > 0){
//			 whereSql += " AND A.TRIAGE_USER='"+triageUser+"'";
//		 }
		 
		 if(levelCode.length() > 0){
			 whereSql += " AND A.LEVEL_CODE='"+levelCode+"'";
		 }

		sql = " SELECT * FROM ( SELECT D.DISCHG_TYPE ,B.CASE_NO, A.TRIAGE_NO, B.MR_NO," +
				" A.PAT_NAME, A.SEX_CODE, A.ADM_TYPE, A.DEPT_CODE, A.CLINICAREA_CODE," +
				" A.TRIAGE_USER, C.LEVEL_DESC LEVEL_CODE," +
				" TO_CHAR (A.ADM_DATE, 'YYYY/MM/DD') || ' ' || TO_CHAR (A.COME_TIME, 'HH24:MI:SS') ADM_DATE" +
				" FROM ERD_EVALUTION A, REG_PATADM B, REG_ERD_LEVEL C,ERD_RECORD D" +
				"  WHERE     A.TRIAGE_NO = B.TRIAGE_NO" +
				" AND A.LEVEL_CODE = C.LEVEL_CODE" +
				" AND B.CASE_NO = D.CASE_NO(+)" +
				" AND A.ADM_TYPE = 'E'" +
				whereSql +
				" UNION ALL " +
				" SELECT '' DISCHG_TYPE ,A.CASE_NO, A.TRIAGE_NO, A.MR_NO, A.PAT_NAME, A.SEX_CODE," +
				" A.ADM_TYPE, A.DEPT_CODE, A.CLINICAREA_CODE, A.TRIAGE_USER, C.LEVEL_DESC LEVEL_CODE," +
				" TO_CHAR (A.ADM_DATE, 'YYYY/MM/DD') || ' ' || TO_CHAR (A.COME_TIME, 'HH24:MI:SS') ADM_DATE" +
				" FROM ERD_EVALUTION A,  REG_ERD_LEVEL C" +
				" WHERE A.LEVEL_CODE = C.LEVEL_CODE" +				
				" AND A.ADM_TYPE = 'E'" +
				whereSql +
				" AND A.CASE_NO IS NULL AND A.MR_NO IS NULL )" +
				" ORDER BY ADM_DATE DESC";		
		
//		System.out.println(sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() < 0){
			this.messageBox("没有要查询的数据!");;
			return;
			
		}
		
		String[] headers = table.getParmMap().split(";");
		
		int count = parm.getCount();
		
		for (int i = 0; i < headers.length; i++) {
			
			if("TRIAGE_NO".equals(headers[i])){
				parm.addData(headers[i], "总人数");
			}else if("MR_NO".equals(headers[i])){
				parm.addData(headers[i], count);
			}else{
				parm.addData(headers[i], "");
			}
			
		}

		table.setParmValue(parm);
		
	}
	
	public void onNew(){
		TParm parm = new TParm();
		parm.setData("FLG", "NEW");
		
		List l = new ArrayList();
		l.add(parm);
		l.add(this);
		this.openWindow("%ROOT%\\config\\reg\\REGPreviewingTriage.x", l);
//		TToolButton tb = (TToolButton) getComponent("Query");
		
		
	}
	
	
	public void onClear(){
		this.clearValue("TRIAGE_NO");
		initPage();
		table.removeRowAll();
	}
	
	public void onWrist(){
		table.acceptText();
		int row = table.getSelectedRow();
		if(row < 0){
			this.messageBox("请选择要打印的检伤号");
			return;
		}
		TParm tableParm = table.getShowParmValue();
		TParm print = new TParm();
        print.setData("BARCODE", "TEXT", tableParm.getValue("TRIAGE_NO", row));
        print.setData("PAT_NAME", "TEXT", tableParm.getValue("PAT_NAME", row));
        print.setData("SEQ", "TEXT", tableParm.getValue("TRIAGE_NO", row).substring(8));
        print.setData("LEVEL_CODE", "TEXT", tableParm.getValue("LEVEL_CODE", row).substring(1));
        this.openPrintDialog("%ROOT%\\config\\prt\\ERD\\ERDEvalutionWist", print);
		
	}
	
	public void onUpdate(){
		table.acceptText();
		int row = table.getSelectedRow();
		if(row < 0){
			this.messageBox("请选择要修改的检伤号");
			return;
		}
		TParm tableParm = table.getShowParmValue();
		TParm parm = new TParm();
		parm.setData("TRIAGE_NO",  tableParm.getValue("TRIAGE_NO", row));
        this.openDialog("%ROOT%\\config\\reg\\REGErdLevelUpdate.x", parm); 
        onQuery();
		
	}


}
