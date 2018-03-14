package com.javahis.ui.inv;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;

/**
 * <p>Title: ������Ӧ������</p>
 *    
 * <p>Description: </p>  
 *
 * <p>Copyright: Copyright (c) 2014</p>
 *
 * <p>Company: </p> 
 *
 * @author fux
 * @version 4.0
 */
public class INVOpeAndPackageBaseControl extends TControl{
	private TTable table; 
    /**  
     * ��ʼ������
     *
     * @param tag
     * @param obj
     */
	public void onInit(){
        TParm parmPack = new TParm(); 
        table = this.getTable("TABLE");
        parmPack.setData("PACK_CODE", "");
        // ���õ����˵�
        getTextField("PACK_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVPackPopup.x"),
            parmPack);
        // ������ܷ���ֵ����    
        getTextField("PACK_CODE").addEventListener( 
            TPopupMenuEvent.RETURN_VALUE, this, "popReturnPack");
        
        TParm parmIcd = new TParm(); 
        parmIcd.setData("OPERATION_ICD", "");
        // ���õ����˵�
        getTextField("OPERATION_ICD").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\sysOpICD.x"),
            parmIcd);  
        // ������ܷ���ֵ���� 
        getTextField("OPERATION_ICD").addEventListener( 
            TPopupMenuEvent.RETURN_VALUE, this, "popReturnIcd");
		onQuery();
	}  
    /**
     * ���ܷ���ֵ����(pack)
     *
     * @param tag
     * @param obj
     */
    public void popReturnPack(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if(parm == null){
            return;
        } 
        String pack_code = parm.getValue("PACK_CODE"); 
        if (!StringUtil.isNullString(pack_code))
            getTextField("PACK_CODE").setValue(pack_code);
        String pack_desc = parm.getValue("PACK_DESC"); 
        if (!StringUtil.isNullString(pack_desc))  
            getTextField("PACK_DESC").setValue(pack_desc);
    }
    
    
    /**
     * ���ܷ���ֵ����(icd)
     *
     * @param tag
     * @param obj
     */
    public void popReturnIcd(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if(parm == null){
            return;
        }
        String operation_code = parm.getValue("OPERATION_ICD");
        if (!StringUtil.isNullString(operation_code))
            getTextField("OPERATION_ICD").setValue(operation_code);
        String opt_desc = parm.getValue("OPT_CHN_DESC");
        if (!StringUtil.isNullString(opt_desc)) 
            getTextField("OPT_CHN_DESC").setValue(opt_desc);
    }
	
    /**
     * ���
     */ 
	public void onClear() {
		this.clearValue("OPERATION_ICD;OPT_CHN_DESC;PACK_CODE;PACK_DESC;QTY;GDVAS_CODE");
		table = getTable("TABLE");
		table.removeRowAll();
        getTextField("OPERATION_ICD").setEnabled(true);  
        getTextField("OPT_CHN_DESC").setEnabled(false);
        getTextField("PACK_CODE").setEnabled(true);   
        getTextField("PACK_DESC").setEnabled(false);
       
	}
 
    /**
     * ���淽��
     */
    public void onSave() {
        // INV_AGENT��������
        TParm parmAgent = new TParm();
        parmAgent = getParmAgent(parmAgent);
        TParm result = new TParm(); 
        if (table.getSelectedRow() < 0) {
        	String flg = "INSERT";
            if (!CheckData(flg)) {
                return;
            }
        	messageBox("����");
            // ��������
            result = TIOM_AppServer.executeAction(
                "action.inv.INVOpeAndPackageAction", "onInsert", parmAgent);
        }
        else {  
        	String flg = "UPDATE";
            if (!CheckData(flg)) {
                return;
            }
        	messageBox("����");
            // ��������
            result = TIOM_AppServer.executeAction(
                "action.inv.INVOpeAndPackageAction", "onUpdate", parmAgent);
        }
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        //this.onClear();
        onQuery();
    }


    /**
     * ���ݼ���
     *
     * @return
     */
    private boolean CheckData(String flg) {
    	if ("INSERT".equals(flg)) {
    		   if ("".equals(getValueString("OPERATION_ICD"))) {
    	            this.messageBox("�������벻��Ϊ��");
    	            return false;
    	        }
    	        if ("".equals(getValueString("OPT_CHN_DESC"))) {
    	            this.messageBox("�������Ʋ���Ϊ��");
    	            return false;
    	        }
    	        if ("".equals(getValueString("PACK_CODE"))) {
    	            this.messageBox("���������벻��Ϊ��");
    	            return false;
    	        }
    	        if ("".equals(getValueString("PACK_DESC"))) {
    	            this.messageBox("���������Ʋ���Ϊ��");
    	            return false;
    	        }
		}  
        if ("".equals(getValueString("QTY"))) {
            this.messageBox("��������С�ڻ����0");
            return false;
        }
        return true;
    }
    
    
    /**
     * INV_AGENT��������
     * @param parm TParm
     * @return TParm
     */ 
    public TParm getParmAgent(TParm parm) {
        String packCode = this.getValueString("PACK_CODE");
        String sql = " SELECT SEQ_FLG " +  
        		" FROM INV_PACKM " +
        		" WHERE��PACK_CODE��= '"+packCode+"'��";
        TParm parmSel = new TParm(TJDODBTool.getInstance().select(sql));
        
        parm.setData("OPERATION_ICD", this.getValueString("OPERATION_ICD"));
        parm.setData("OPT_CHN_DESC", this.getValueString("OPT_CHN_DESC"));
        parm.setData("PACK_CODE", this.getValueString("PACK_CODE"));
        parm.setData("PACK_DESC", this.getValueString("PACK_DESC"));
        parm.setData("QTY", this.getValueDouble("QTY"));  
        parm.setData("SEQ_FLG", parmSel.getValue("SEQ_FLG", 0));
        //��¼Ѫ��
        parm.setData("GDVAS_CODE",  this.getValueString("GDVAS_CODE"));
        parm.setData("OPT_USER", Operator.getID()); 
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        return parm;
    }
    
    /**
     * ���(CLNDIAG_TABLE)�����¼�????
     */
    public void onTableClicked() {
        getTextField("OPERATION_ICD").setEnabled(false);
        getTextField("OPT_CHN_DESC").setEnabled(false);
        getTextField("PACK_CODE").setEnabled(false);
        getTextField("PACK_DESC").setEnabled(false);
        TParm parm = table.getParmValue().getRow(table.getSelectedRow()); 
//        String opIcd = parm.getValue("OPERATION_ICD");
//        String packCode = parm.getValue("PACK_CODE"); 
        String textStr = "OPERATION_ICD;OPT_CHN_DESC;PACK_CODE;PACK_DESC;QTY;GDVAS_CODE"; 
        this.setValueForParm(textStr, parm);
    } 
     
	
    /**
     * ɾ������
     */
    public void onDelete() {
        if (table.getSelectedRow() < 0) {
            this.messageBox("��ѡ��ɾ����");
            return;
        }
        TParm parm = new TParm();
        parm.setData("OPERATION_ICD", this.getValueString("OPERATION_ICD"));
        parm.setData("PACK_CODE", this.getValueString("PACK_CODE"));
        TParm result = TIOM_AppServer.executeAction(  
            "action.inv.INVOpeAndPackageAction", "onDelete", parm);
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("ɾ��ʧ��");
            return;    
        }
        this.messageBox("ɾ���ɹ�");
        //this.onClear();
        onQuery();
    }
	
    /**
     * ��ѯ����
     */
	public void onQuery(){
		String opIcd = this.getValueString("OPERATION_ICD");
		String packCode = this.getValueString("PACK_CODE");
		String gdvasCode = this.getValueString("GDVAS_CODE");
		String why = "";

		String sql = " SELECT OPERATION_ICD,OPT_CHN_DESC,PACK_CODE,QTY,PACK_DESC," +
				" SEQ_FLG,GDVAS_CODE " +
				" FROM OPE_ICDPACKAGE" +
				" WHERE 1=1" +
				"";           
        StringBuffer SQL = new StringBuffer();   
        SQL.append(sql);    
		if(!"".equals(opIcd)){
			SQL.append(" AND OPERATION_ICD = '"+opIcd+ "'");  
		}      
		if(!"".equals(packCode)){
			SQL.append(" AND PACK_CODE = '"+packCode+ "'");  
		}
		if(!"".equals(gdvasCode)){
			SQL.append(" AND GDVAS_CODE = '"+gdvasCode+ "'");  
		}
		System.out.println("SQL:"+SQL);  
		TParm parm = new TParm(TJDODBTool.getInstance().select(SQL.toString())); 
        if (parm.getCount() <= 0) {  
            this.messageBox("û�в�ѯ����");
            return;  
        }
        table.setParmValue(parm);
	}
	//OP_ICD OP_DESC
	//PACK_CODE PACK_DESC
    //QTY
	 
	//TABLE:   OPERATION_ICD;OPT_CHN_DESC;PACK_CODE;PACK_DESC;QTY;SEQ_FLG
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
    
    
    /**
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
    
    
    
    /**
     * �õ�TComboBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */ 
    private TComboBox getTCombox(String tagName) {
        return (TComboBox) getComponent(tagName);  
    }
}
