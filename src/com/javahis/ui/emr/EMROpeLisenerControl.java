package com.javahis.ui.emr;

import java.awt.Window;

import jdo.emr.EMRCdrTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;

/**
 * 术中监护
 * @author Administrator
 *
 */
public class EMROpeLisenerControl extends TControl{
	TTable table;
	@Override
	public void onInit() {
		super.onInit();
		Window window = (Window) this.getComponent("UI");
	    window.setAlwaysOnTop(true);
		initPage();
	}
	/**
	 * 初始化页面
	 */
	public void initPage(){
		TTabbedPane tTabbedPane = (TTabbedPane) this.getComponent("tTabbedPane_2");
		tTabbedPane.setEnabledAt(0, false);
		tTabbedPane.setEnabledAt(1, false);
		tTabbedPane.setEnabledAt(2, false);
		
		
		Object obj=this.getParameter();
		TParm parm=new TParm();
		if(obj instanceof TParm)
			parm=(TParm) obj;
		TParm param=new TParm();
		param.setData("ADM_TYPE",parm.getValue("ADM_TYPE"));
		param.setData("OPE_BOOK_NO",parm.getValue("APPLY_NO"));
		
		int count1 = 0;
		int count2 = 0;
		TParm result = EMRCdrTool.getInstance().getOpeEventData(parm);//术中 事件
		table=(TTable) this.getComponent("TABLE_EVENT");
		table.removeRowAll();
		if(result.getCount()>0){
			count1++;
			tTabbedPane.setSelectedIndex(0);
			tTabbedPane.setEnabledAt(0, true);
			table.setParmValue(result);
		}
		
		result=EMRCdrTool.getInstance().getOpeLisenerData(param);//体征监测数据
		table=(TTable) this.getComponent("TABLE1");
		table.removeRowAll();
		if(result.getCount()>0){
			count2++;
			if(count1 == 0){
				tTabbedPane.setSelectedIndex(1);
			}
			tTabbedPane.setEnabledAt(1, true);
			table.setParmValue(result);
		}
		
		result=EMRCdrTool.getInstance().getOpeAnaData(param);//麻醉用药数据
		table=(TTable) this.getComponent("TABLE2");
		table.removeRowAll();
		if(result.getCount()>0){
			if(count1==0 && count2==0){
				tTabbedPane.setSelectedIndex(2);
			}
			tTabbedPane.setEnabledAt(2, true);
			table.setParmValue(result);
		}
	}
}
