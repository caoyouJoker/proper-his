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
public class BMSApplicationNoControl
    extends TControl {

    // �ⲿ���ô���
    private TParm parm;
    
    //APPLY_NOΪ���ݱ�Ѫ���Ų�ѯѪ�ܺ� ��MR_NOΪ���ݲ����Ų�ѯѪ�ܺ�
    private String type = "";
    
    private String code = "";

    public BMSApplicationNoControl() {
    }

    /**
     * ��ʼ������
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
     * ���ط���
     */
    public void onReturn() {
        TTable table = this.getTable("TABLE");
        if (table.getSelectedRow() < 0) {
            this.messageBox("E0134");
            return;
        }
        TParm inparm = new TParm();
        inparm.setData("APPLICATION_NO",
                       table.getItemString(table.getSelectedRow(), "APPLICATION_NO"));
        this.setReturnValue(inparm);
        this.closeWindow();
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    //���ݱ�Ѫ���Ų�ѯѪ�ܺ�
    private String getApplicationNoByApplyNo(String APPLY_NO){
    	String sql = "";
    	sql += " SELECT " 
    		+ " B.MED_APPLY_NO AS APPLICATION_NO,O.NS_EXEC_DATE_REAL AS EXEC_DATE " 
    		+ " FROM ODI_DSPND O , ODI_ORDER B " 
    		+ " WHERE "
    		+ " B.ORDER_SEQ = O.ORDER_SEQ " 
    		+ " AND B.ORDER_NO = O.ORDER_NO "
    		+ " AND B.CASE_NO = O.CASE_NO "
    		+ " AND B.SETMAIN_FLG = 'Y' "
    		+ " AND B.APPLY_NO = '"+APPLY_NO+"'";
    	return sql;
    }
    
    //����case_no��ѯѪ�ܺ�
    private String getApplicationNoByMrNo(String CASE_NO){
    	String sql = "";
    	sql += " SELECT " 
    		+ " B.MED_APPLY_NO AS APPLICATION_NO,O.NS_EXEC_DATE_REAL AS EXEC_DATE " 
    		+ " FROM ODI_DSPND O , ODI_ORDER B " 
    		+ " WHERE "
    		+ " B.ORDER_SEQ = O.ORDER_SEQ " 
    		+ " AND B.ORDER_NO = O.ORDER_NO "
    		+ " AND B.CASE_NO = O.CASE_NO "
    		+ " AND B.SETMAIN_FLG = 'Y' "
    		+ " AND B.CASE_NO = '"+CASE_NO+"'";
    	return sql;
    }
}
