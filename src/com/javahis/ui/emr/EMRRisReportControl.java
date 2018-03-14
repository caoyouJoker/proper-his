package com.javahis.ui.emr;

import java.awt.Window;

import jdo.emr.EMRCdrTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.tui.DMessageIO;
import com.dongyang.tui.text.ECapture;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWord;

public class EMRRisReportControl extends TControl implements DMessageIO{
	TWord word;
	@Override
	public void onInit() {
		super.onInit();
		Window window = (Window) this.getComponent("UI");
	    window.setAlwaysOnTop(true);//µ¯³öµÄ´°¿ÚÖÃ¶¥
	    
		TParm parm=(TParm) this.getParameter();
		TTabbedPane tabbedPane = (TTabbedPane) this.getComponent("tTabbedPane_1");
		tabbedPane.setEnabledAt(1, false);
		this.setValue("OUTCOME_DESCRIBE", parm.getValue("OUTCOME_DESCRIBE"));
		this.setValue("OUTCOME_CONCLUSION", parm.getValue("OUTCOME_CONCLUSION"));
		this.setValue("OUTCOME_TYPE", parm.getValue("OUTCOME_TYPE"));
		
		TTable table = (TTable) this.getComponent("TABLE");
		TParm result = EMRCdrTool.getInstance().getPhiscalParam(parm);
		if(result.getCount()>0){
			table.setParmValue(result);
			tabbedPane.setEnabledAt(1, true);
		}
		
	}
	
	
}
