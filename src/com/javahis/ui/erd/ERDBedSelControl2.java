package com.javahis.ui.erd;

import com.dongyang.control.TControl;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;

/**
 * <p>Title: 急诊抢救绑定床位</p>
 *
 * <p>Description: 急诊抢救绑定床位 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author wangqing 20171205
 * @version 1.0
 */
public class ERDBedSelControl2 extends TControl{

	/**
	 * 初始化
	 */
    public void onInit() {
        super.onInit();
        initTable();
    }

    /**
     * 初始化界面Table
     */
    public void initTable(){
    	TParm parm = getERDBed();
    	if(parm.getErrCode()<0){
    		return;
    	}
        ((TTable)getComponent("TABLE")).setParmValue(parm);
    }
    
    /**
     * 查询空床
     * @return
     */
    public TParm getERDBed(){
   	 String sql = " SELECT ERD_REGION_CODE, BED_NO, OCCUPY_FLG, TRIAGE_NO FROM ERD_BED WHERE NOT(OCCUPY_FLG IS NOT NULL AND OCCUPY_FLG='Y') ";
   	 TParm result = new TParm(TJDODBTool.getInstance().select(sql));
   	 return result;
   }

    /**
     * 确定
     */
    public void onConfirm() {
        if(((TTable)getComponent("TABLE")).getSelectedRow() < 0)
            return;
        setReturnValue(((TTable) getComponent("TABLE")).getParmValue().getRow(((TTable)getComponent("TABLE")).getSelectedRow()));
        closeWindow();
    }
   
    /**
     * 取消
     */
    public void onCancel() {
        closeWindow();
    }
}
