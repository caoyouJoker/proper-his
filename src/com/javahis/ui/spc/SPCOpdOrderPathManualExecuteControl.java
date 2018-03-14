package com.javahis.ui.spc;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import jdo.ind.ElectronicTagsImpl;
import jdo.ind.ElectronicTagsInf;
import jdo.spc.SPCUpdateElecTagPathTool;

import com.dongyang.control.TControl;   
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TTextFormat;

/**
 * shou dong zhixing pi ci 
 */

public class SPCOpdOrderPathManualExecuteControl extends TControl {
	
	TButton b1;
	TTextArea exe_result;
	TTextFormat syc_date;//SYC_DATE
	
	
	/**构造器   */   
	public SPCOpdOrderPathManualExecuteControl() {
		super();             	
	} 
	
    /**初始化方法*/
    public void onInit() {
    	syc_date = (TTextFormat) this.getComponent("SYC_DATE");
    	b1 = (TButton) this.getComponent("B1");
    	exe_result = (TTextArea) this.getComponent("EXE_RESULT");    	
    }
    
	/** b1按钮 */
	public void b1Click() {
		
		TParm parm = new TParm();
		
		parm.setData("DATE_STR", syc_date.getText());
		
        TParm result = TIOM_AppServer.executeAction(
                "action.spc.SPCOpdOrderPatchManualExecute", "executeSync", parm);
		
		exe_result.setText(result.getValue("RESULT_MEG"));
		this.messageBox("执行完毕!");
		
	}
    
    
    
    
    
    /**查询方法*/
	public void onQuery() {
		
	} 
	
	/**
	 * 保存修改操作
	 */
	public void onSave() {

	}
	
	/**删除操作*/
	public void onDelete() {

	}
	
	/** table 单击事件 */
	public void onTableClick() {

	}
	
	/** 导入药品对照表*/
	public void onInsertPatByExl() {
		      
	}
	
	/**清空操作*/
	public void onClear() {
		
	}
	

	
	
}
