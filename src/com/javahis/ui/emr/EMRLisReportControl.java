package com.javahis.ui.emr;

import java.awt.GridLayout;
import java.awt.Window;

import jdo.emr.EMRCdrTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;

public class EMRLisReportControl extends TControl{
	TTable table;
	String cat1Typ;
	String applyNo;
	public void onInit() {
		super.onInit();
		Window window = (Window) this.getComponent("UI");
	    window.setAlwaysOnTop(true);
	    
		Object  obj= this.getParameter();
		TParm parm=new TParm();
		if(obj instanceof TParm)
			 parm=(TParm) obj;
		initPage(parm);
	}
	public void initPage(TParm parm){
		
		cat1Typ=parm.getValue("CAT1_TYPE");
		applyNo=parm.getValue("APPLY_NO");
		TParm param=new TParm();
		param.setData("CAT1_TYPE",cat1Typ);
		param.setData("APPLY_NO",applyNo);
		TTabbedPane tTabbedPane = (TTabbedPane) this.getComponent("tTabbedPane_1");
		tTabbedPane.setEnabledAt(0, false);
		tTabbedPane.setEnabledAt(1, false);
		tTabbedPane.setEnabledAt(2, false);
		TParm result=new TParm();
		
		//一般检验
		table=(TTable) this.getComponent("TABLE1");
		table.removeRowAll();
		result=EMRCdrTool.getInstance().getLisData1(param);
		if(result.getCount()>0){
			tTabbedPane.setEnabledAt(0, true);
			tTabbedPane.setSelectedIndex(0);
			table.setParmValue(result);
		}
		
		//药敏实验
		table=(TTable) this.getComponent("TABLE2");
		table.removeRowAll();
		result=EMRCdrTool.getInstance().getLisAntitest(param);
		int count=0;
		if(result.getCount()>0){
			count++;
			tTabbedPane.setEnabledAt(1, true);
			tTabbedPane.setSelectedIndex(1);
			table.setParmValue(result);
		}
		
		//细菌培养
		table=(TTable) this.getComponent("TABLE3");
		table.removeRowAll();
		result=EMRCdrTool.getInstance().getLisCulrpt(param);
		if(result.getCount()>0){
			if(count==0){
				tTabbedPane.setSelectedIndex(2);
			}
			tTabbedPane.setEnabledAt(2, true);
			table.setParmValue(result);
		}
			
			
			
			
		
		
		
	}
}
