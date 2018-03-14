package com.javahis.ui.mro;

import jdo.mro.EMRScopeTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;

public class EMRScopeControl extends TControl {

	
	TTable table; 
	/**
     * ��ʼ��
     */ 
    public void onInit() { 
    	table = (TTable)this.getComponent("TABLE");
    	//this.onQuery();
    }
    /**
     * ��ѯ���������������뵽parm��
     */
    public void onQuery(){
    	TParm parm = new TParm();
    	if(this.getValue("EMR_SCOPE_CODE").toString().length()>0){
    		parm.setData("EMR_SCOPE_CODE", this.getValue("EMR_SCOPE_CODE").toString());
    	}
    	//��ҳ���е�PRG_ID���뵽parm��
    	if(this.getValue("IN_HOP_DEPT_DOC").toString().length()>0){
    		parm.setData("IN_HOP_DEPT_DOC", this.getValue("IN_HOP_DEPT_DOC").toString());
    	}
    	//��ҳ���е�OPT_USER���뵽parm��
    	if(this.getValue("EMR_SCOPE_NAME").toString().length()>0){
    		parm.setData("EMR_SCOPE_NAME", this.getValue("EMR_SCOPE_NAME").toString());
    	}
    	
    	TParm result = EMRScopeTool.getInstance().onQuery(parm);
    	System.out.println("rsul::::"+result);
    	
    	table.setParmValue(result);
    	
    	//this.messageBox("��ѯ�ɹ�");
    }
    /**
     * ����/�޸�
     */
    public void onSave(){
    	
    	//��ȡҳ���е� TTextField �ؼ�
		TTextField tt=(TTextField) this.getComponent("EMR_SCOPE_CODE");
		//����
		if(tt.isEnabled()){
			TParm parm = this.writeParm();
	    	Boolean flag = EMRScopeTool.getInstance().onSave(parm);
	    	if(flag){ 
	    		this.messageBox("�����ɹ�");
	    		this.onClear();
	    		this.onQuery();
	    		return;
	    	}
	    	this.messageBox("����ʧ��");	 
	    	return;
		}
		//����
		TParm parm = this.writeParm();
		Boolean flag = EMRScopeTool.getInstance().onUpdata(parm);
		if(flag){ 
    		this.messageBox("�޸ĳɹ�");
    		this.onClear();
    		this.onQuery();
    		return;
    	}
    	this.messageBox("�޸�ʧ��");		
    	return;
    	
    }
    
    /**
     * ��ȡҳ����ѡ�������
     * @return
     */
    public TParm writeParm(){
    	
    	TParm parm = new TParm();
    	
    	if("".equals(this.getValueString("EMR_SCOPE_CODE"))){
    		this.messageBox("���뷶Χ����Ϊ��");
    		return parm;
    	}
    	if("".equals(this.getValueString("IN_HOP_DEPT_DOC"))){
    		this.messageBox("��Χ����Ϊ��");
    		return parm;
    	}
    	if("".equals(this.getValueString("EMR_SCOPE_NAME"))){
    		this.messageBox("��Χ���Ʋ���Ϊ��");
    		return parm;
    	}
    	parm.setData("EMR_SCOPE_NAME", this.getValueString("EMR_SCOPE_NAME"));
    	parm.setData("EMR_SCOPE_CODE", this.getValueString("EMR_SCOPE_CODE"));
    	parm.setData("IN_HOP_DEPT_DOC", this.getValueString("IN_HOP_DEPT_DOC"));
    	parm.setData("MEMO", this.getValueString("MEMO"));
    	
		return parm;
    	
    	
    	
    }
    
    /**
     * ��շ���
     * ��ҳ���пؼ����������
     */
    public void onClear() {
    	
        // ��ջ�������
        String clearString =
        		"EMR_SCOPE_NAME;EMR_SCOPE_CODE;IN_HOP_DEPT_DOC;MEMO";
        clearValue(clearString);
      
        //����ҳ���еĿؼ�EMR_SCOPE_CODEΪ�ɱ༭״̬
        setTextEnabled(true);
        //getComboBox("EMR_SCOPE_CODE").setEnabled(true);
       
        
    } 
 
    /**
     * �����ϲ��ɱ༭�Ŀؼ�
     * @param boo boolean
     */
    public void setTextEnabled(boolean boo) {
    	callFunction("UI|EMR_SCOPE_CODE|setEnabled", boo);
    }
    /**
     * ��굥���¼�
     */
    public void clickMouse(){
    	TParm parm = new TParm();   	
    	parm.setData("EMR_SCOPE_NAME", (String)table.getItemData(table.getSelectedRow(),"EMR_SCOPE_NAME"));   	
    	parm.setData("EMR_SCOPE_CODE", (String)table.getItemData(table.getSelectedRow(),"EMR_SCOPE_CODE"));
    	parm.setData("IN_HOP_DEPT_DOC", (String)table.getItemData(table.getSelectedRow(),"IN_HOP_DEPT_DOC"));   	
    	parm.setData("MEMO", (String)table.getItemData(table.getSelectedRow(),"MEMO"));
    	//���������Ϸ�����
    	this.setTextValue(parm);
    	setTextEnabled(false);
    }
    
    /**
     * �����Ϸ�
     * @param parm TParm
     * @param row int
     */
    public void setTextValue(TParm parm) {
        setValueForParm("EMR_SCOPE_NAME;EMR_SCOPE_CODE;IN_HOP_DEPT_DOC;MEMO", parm);
    }
    /**
     * ��������
     */
    public void onAdd(){
    	
    	TParm parm = new TParm();
    	
    	if("".equals(this.getValueString("EMR_SCOPE_CODE"))){
    		this.messageBox("���뷶Χ����Ϊ��");
    		return;
    	}
    	if("".equals(this.getValueString("IN_HOP_DEPT_DOC"))){
    		this.messageBox("��Χ����Ϊ��");
    		return;
    	}
    	if("".equals(this.getValueString("EMR_SCOPE_NAME"))){
    		this.messageBox("��Χ���Ʋ���Ϊ��");
    		return;
    	}
    	String sql ="SELECT MAX(ID) MAX FROM EMR_SCOPE";
    	TParm result =new TParm(TJDODBTool.getInstance().select(sql.toString()));
    	String s = result.getValue("MAX",0);  	
    	String id = StringTool.addString(s);
    	//this.messageBox(id);
    	parm.setData("ID", id);
    	parm.setData("EMR_SCOPE_NAME", this.getValueString("EMR_SCOPE_NAME"));
    	parm.setData("EMR_SCOPE_CODE", this.getValueString("EMR_SCOPE_CODE"));
    	parm.setData("IN_HOP_DEPT_DOC", this.getValueString("IN_HOP_DEPT_DOC"));
    	parm.setData("MEMO", this.getValueString("MEMO"));
    	Boolean flag = EMRScopeTool.getInstance().onSave(parm);
    	if(flag){ 
    		this.messageBox("�����ɹ�");
    		this.onClear();
    		this.onQuery();
    		return;
    	}
    	this.messageBox("����ʧ��");	 
    }
    /**
     * ɾ������
     * ͨ��IDɾ��
     */
    public void onDelete(){
    	
    	TParm parm = new TParm();
    	String id = table.getParmValue().getValue("EMR_SCOPE_CODE",table.getSelectedRow());
    	
//    	this.messageBox(id);
    	parm.setData("EMR_SCOPE_CODE", id);
    	Boolean flag = EMRScopeTool.getInstance().onDelete(parm);
    	if(flag){
    		this.messageBox("ɾ���ɹ�");
    		this.onClear();
    		this.onQuery();
    		return;
    	}
    	this.messageBox("ɾ��ʧ��");
    }
}
                                                  