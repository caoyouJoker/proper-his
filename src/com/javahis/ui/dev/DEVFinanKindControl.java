package com.javahis.ui.dev;

import jdo.dev.DEVFinanKindTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
/**
 * <p>
 * Title:�豸������൥�� 
 * </p>
 *
 * <p>
 * Description:�豸������൥�� 
 * </p>  
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: bluecore
 * </p> 
 *
 * @author fux 2013.09.22
 * @version 1.0
 */
public class DEVFinanKindControl extends TControl {
	private TTable table;
	String sql = " SELECT FINAN_KIND,FINAN_DESC,DEP_DEADLINE,DESCRIPTION,CLASSIFY," +
    " CLASSIFY_DESC,OPT_USER,OPT_DATE,OPT_TERM" +
    " FROM DEV_FINANKIND" ; 
	/**
	 * ��ʼ������
	 */ 
	public void onInit(){
		table = (TTable) this.getComponent("TABLE");
		onQuery(); 
	} 
	/**
	 * ��շ���
	 */
	public void onClear(){
		this.clearValue("FINAN_KIND");
		this.clearValue("FINAN_DESC"); 
		this.clearValue("DEP_DEADLINE");
		this.clearValue("DESCRIPTION");
		
	}
	/**
	 * ��ѯ����
	 */
	public void onQuery(){
		String finanKind = getValueString("FINAN_KIND");	
		String finanDesc = getValueString("FINAN_DESC");

        StringBuffer SQL = new StringBuffer(); 
        SQL.append(sql);
        SQL.append("  WHERE FINAN_KIND IS NOT NULL "); 
		if(finanKind.length() > 0){ 
			SQL.append(" AND FINAN_KIND LIKE '%"+finanKind+"%'");
		} 
		if(finanDesc.length() > 0){ 
			SQL.append(" AND FINAN_DESC LIKE '%"+finanDesc+"%'");
		} 
		//System.out.println("SQL"+SQL);  
		TParm parm = new TParm(TJDODBTool.getInstance().select(SQL.toString())); 
		if(parm.getCount() < 0){
			messageBox("�������ݣ�");  
			return;
		}
		if(parm.getErrCode() < 0){ 
			messageBox("��������");
			return; 
		}
		table.setParmValue(parm);  
	} 
	/**
	 * ���淽��
	 */
	public void onSave(){ 
		String finanKind = getValueString("FINAN_KIND");	
		String finanDesc = getValueString("FINAN_DESC");
		String depDeadLine = getValueString("DEP_DEADLINE");	
		String descrition = getValueString("DESCRIPTION");
		TParm parm = new TParm();
		parm.setData("FINAN_KIND",finanKind);
        StringBuffer SQLSave = new StringBuffer();  
        //System.out.println("sql===="+sql);
        SQLSave.append(sql);  
        SQLSave.append(" WHERE FINAN_KIND = '"+finanKind+"' ");
        TParm FlgParm =new TParm (TJDODBTool.getInstance().select(SQLSave.toString()));
		if(FlgParm.getCount() > 0){  
			//update   
			parm.setData("FINAN_DESC",finanDesc);
			parm.setData("DEP_DEADLINE",depDeadLine);
			parm.setData("DESCRIPTION",descrition);
			parm.setData("CLASSIFY",finanKind);
			parm.setData("CLASSIFY_DESC",finanKind);
			parm.setData("OPT_USER",Operator.getName());
			parm.setData("OPT_DATE",SystemTool.getInstance().getDate());  
			parm.setData("OPT_TERM",Operator.getIP());
			TParm updateParm =  DEVFinanKindTool.getInstance().updateFinan(parm);
			if(updateParm.getErrCode() < 0){
				messageBox("����ʧ�ܣ�"); 
				return; 
			}  
			messageBox("���³ɹ���");
			onClear();
			onQuery();
			
		} 
		else{ 
			//insert 
			parm.setData("FINAN_DESC",finanDesc); 
			parm.setData("DEP_DEADLINE",depDeadLine);
			parm.setData("DESCRIPTION",descrition);
			parm.setData("CLASSIFY",finanKind); 
			parm.setData("CLASSIFY_DESC",finanKind);  
			parm.setData("OPT_USER",Operator.getName());
			parm.setData("OPT_DATE",SystemTool.getInstance().getDate());  
			parm.setData("OPT_TERM",Operator.getIP());
			TParm insertParm =  DEVFinanKindTool.getInstance().insertFinan(parm);
			if(insertParm.getErrCode() < 0){
				messageBox("����ʧ�ܣ�");
				return;  
			} 
			messageBox("����ɹ���");
			onClear();
			onQuery();
		} 		
	}   
	/**
	 * ����¼� 
	 */
	public void onTableClick(){ 
		 int row =table.getSelectedRow();
		 TParm RowParm = table.getParmValue();
		 this.setValue("FINAN_KIND", RowParm.getData("FINAN_KIND", row)); 
		 this.setValue("FINAN_DESC", RowParm.getData("FINAN_DESC", row));
		 this.setValue("DEP_DEADLINE",RowParm.getData("DEP_DEADLINE", row));
		 this.setValue("DESCRIPTION", RowParm.getData("DESCRIPTION", row));
	}
   
}
