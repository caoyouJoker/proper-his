package com.javahis.ui.erd;

import com.dongyang.control.TControl;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;

/**
 * <p>Title: �������Ȱ󶨴�λ</p>
 *
 * <p>Description: �������Ȱ󶨴�λ </p>
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
	 * ��ʼ��
	 */
    public void onInit() {
        super.onInit();
        initTable();
    }

    /**
     * ��ʼ������Table
     */
    public void initTable(){
    	TParm parm = getERDBed();
    	if(parm.getErrCode()<0){
    		return;
    	}
        ((TTable)getComponent("TABLE")).setParmValue(parm);
    }
    
    /**
     * ��ѯ�մ�
     * @return
     */
    public TParm getERDBed(){
   	 String sql = " SELECT ERD_REGION_CODE, BED_NO, OCCUPY_FLG, TRIAGE_NO FROM ERD_BED WHERE NOT(OCCUPY_FLG IS NOT NULL AND OCCUPY_FLG='Y') ";
   	 TParm result = new TParm(TJDODBTool.getInstance().select(sql));
   	 return result;
   }

    /**
     * ȷ��
     */
    public void onConfirm() {
        if(((TTable)getComponent("TABLE")).getSelectedRow() < 0)
            return;
        setReturnValue(((TTable) getComponent("TABLE")).getParmValue().getRow(((TTable)getComponent("TABLE")).getSelectedRow()));
        closeWindow();
    }
   
    /**
     * ȡ��
     */
    public void onCancel() {
        closeWindow();
    }
}
