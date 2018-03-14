package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import jdo.bms.BMSApplyMTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TMenuItem;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BMSTakeNoControl
    extends TControl {

    // 外部调用传参
    private TParm parm;
    
    //APPLY_NO为根据备血单号查询取血单
    private String type = "";
    
    private String code = "";

    public BMSTakeNoControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
    	( (TMenuItem) getComponent("new")).setEnabled(false);
    	TParm result = new TParm();
        Object obj = this.getParameter();
        if (obj != null) {
            parm = (TParm) obj;
            type = parm.getValue("TYPE");
            code = parm.getValue("CODE");
            if("APPLY_NO".equals(type)){
            	String sql = getApplicationNoByApplyNo(code);
            	result = new TParm(TJDODBTool.getInstance().select(sql)) ;
            }
        }

        
        this.getTable("TABLE").setParmValue(result);
    }

    /**
     * 返回方法
     */
    public void onReturn() {
        TTable table = this.getTable("TABLE");
        if (table.getSelectedRow() < 0) {
            this.messageBox("E0134");
            return;
        }
        TParm inparm = new TParm();
        inparm.setData("BLOODTAKE_NO",
                       table.getItemString(table.getSelectedRow(), "BLOODTAKE_NO"));
        this.setReturnValue(inparm);
        this.closeWindow();
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    //根据备血单号查询血管号
    private String getApplicationNoByApplyNo(String APPLY_NO){
    	String sql = "";
    	sql += " SELECT " 
    		+ " BLOOD_TANO AS BLOODTAKE_NO , BLOOD_DATE AS EXEC_DATE " 
    		+ " FROM BMS_BLDTAKEM " 
    		+ " WHERE "
    		+ " APPLY_NO = '"+APPLY_NO+"'";
    	return sql;
    }
    
}
