package com.javahis.ui.onw;

import java.awt.Dimension;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TDialog;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.reg.TablePublicTool;
import jdo.sys.Operator;

/**
 * <p>����ҽ��վ������ͷҽ��</p>
 * 
 * @author wangqing 20170919
 *
 */
public class ONWOrderUIControl extends TControl {
	/**
	 * ϵͳ����
	 */
	private TParm sysParm;

	/**
	 * ���˺�
	 */
	private String triageNo;

	private TTable orderTable;
	
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();
		orderTable = (TTable) this.getComponent("TABLE_ORDER");
		orderTable.addEventListener(TTableEvent.CHECK_BOX_CLICKED,this,"onCheckBoxValue");
		Object o = this.getParameter();
		if(o != null && o instanceof TParm){
			sysParm = (TParm)o;
			triageNo = sysParm.getValue("TRIAGE_NO");
		}else{
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					closeWindow();
				}
			});	
			return;
		}
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
//				TFrame frame = (TFrame) getComponent();
//				frame.setPreferredSize(new Dimension(1200,800));
//				frame.pack();	
//				triageNo = "20170921002";
				onQuery();
			}
		});
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery(){
		TParm onwOrderParm = new TParm();
		onwOrderParm.setData("TRIAGE_NO", triageNo);
		TParm onwOrderResult = new TParm();
		onwOrderResult = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "selectOnwOrder", onwOrderParm);
		if(onwOrderResult.getErrCode()<0){
			System.out.println("err onwOrderResult");
			return;
		}
		for(int i=0; i<onwOrderResult.getCount(); i++){
			onwOrderResult.setData("SEL_FLG", i, "N");// ѡ
		}
		TablePublicTool.setParmValue(orderTable, onwOrderResult);
	}

	/**
	 * ҽ��ǩ��
	 */
	public void onSign(){
		if(orderTable == null){
			this.messageBox("orderTable is null");
			return;
		}
		TParm orderP = orderTable.getParmValue();
		if(orderP == null){
			this.messageBox("orderP is null");
			return;
		}
		for(int i=0; i<orderP.getCount(); i++){
			// ҽ���Ѿ�ǩ���Ĳ���ǩ��
			if(orderP.getValue("SIGN_DR", i) != null 
					&& orderP.getValue("SIGN_DR", i).trim().length()>0 
					&& orderP.getValue("SEL_FLG", i) != null 
					&& orderP.getValue("SEL_FLG", i).equals("Y")){
				this.messageBox("ҽ���Ѿ�ǩ���Ĳ���ǩ��");
				return;
			}
			// ��ʿδǩ���Ĳ���ǩ��
			if( (orderP.getValue("SIGN_NS", i) == null || orderP.getValue("SIGN_NS", i).trim().length()==0) 
					&& orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				this.messageBox("��ʿδǩ���Ĳ���ǩ��");
				return;
			}	
		}
		for(int i=0; i<orderP.getCount(); i++){
			if(orderP.getValue("SEL_FLG", i) != null 
					&& orderP.getValue("SEL_FLG", i).equals("Y")){
				TablePublicTool.modifyRow(orderTable, i, 0, "SEL_FLG", orderP.getValue("SEL_FLG", i), "N");// ѡ
				TablePublicTool.modifyRow(orderTable, i, 11, "SIGN_DR", orderP.getValue("SIGN_DR", i), Operator.getID());// ҽ��
			}		
		}
		// ����
		if(this.onSave1()){
			this.messageBox("ǩ���ɹ�������");
		}else{
			this.messageBox("ǩ��ʧ�ܣ�����");
		}	
	}

	/**
	 * ȡ��ǩ��
	 */
	public void onCancelSign(){
		if(orderTable == null){
			this.messageBox("orderTable is null");
			return;
		}
		TParm orderP = orderTable.getParmValue();
		if(orderP == null){
			this.messageBox("orderP is null");
			return;
		}
		for(int i=0; i<orderP.getCount(); i++){
			// ҽ��û��ǩ���Ĳ���ȡ��ǩ��
			if( (orderP.getValue("SIGN_DR", i) == null || orderP.getValue("SIGN_DR", i).trim().length()==0) 
					&& orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				this.messageBox("ҽ��û��ǩ���Ĳ���ȡ��ǩ��!!!");
				return;
			}
			// ��ʿû��ǩ���Ĳ���ȡ��ǩ��
			if( (orderP.getValue("SIGN_NS", i) == null || orderP.getValue("SIGN_NS", i).trim().length()==0) 
					&& orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				this.messageBox("��ʿû��ǩ���Ĳ���ȡ��ǩ��!!!");
				return;
			}
			// �Ѿ�������ҽ������ȡ��ǩ��
			if( (orderP.getValue("EXE_FLG", i) != null && orderP.getValue("EXE_FLG", i).equals("Y")) 
					&& orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				this.messageBox("�Ѿ�������ҽ������ȡ��ǩ��!!!");
				return;
			}	
		}
		for(int i=0; i<orderP.getCount(); i++){
			if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				TablePublicTool.modifyRow(orderTable, i, 0, "SEL_FLG", orderP.getValue("SEL_FLG", i), "N");// ѡ
				TablePublicTool.modifyRow(orderTable, i, 11, "SIGN_DR", orderP.getValue("SIGN_DR", i), "");// ҽ��		
			}			
		}
		// ����
		if(this.onSave2()){
			this.messageBox("ȡ��ǩ���ɹ�������");
		}else{
			this.messageBox("ȡ��ǩ��ʧ�ܣ�����");
		}
	}

	public void onCheckBoxValue(Object obj){
		TTable table = (TTable)obj;		
		table.acceptText();
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();
//		System.out.println("======//////row="+row);
//		System.out.println("======//////col="+col);
		TParm orderP = table.getParmValue();
		if(orderP == null){
			this.messageBox("orderP is null");
			return;
		}
		if(col == 0){// ѡ	
			if(orderP.getValue("LINK_NO", row) != null 
					&& orderP.getValue("LINK_NO", row).trim().length()>0 ){// �����������ͬ��ı���һ��
				for(int i=0; i<orderP.getCount(); i++){			
					if(orderP.getValue("LINK_NO", i) != null 
							&& orderP.getValue("LINK_NO", i).equals(orderP.getValue("LINK_NO", row))){// ͬ�M
						TablePublicTool.modifyRow(table, i, 0, "SEL_FLG", 
								orderP.getValue("SEL_FLG", i), orderP.getValue("SEL_FLG", row));// ѡ	
					}
				}
			}
		}
	}
	
	/**
	 * У������
	 */
	public void checkData(TParm parm, String names1, String names2){
		String [] nameArr1 = names1.split(";");
		String [] nameArr2 = names2.split(";");	
		for(int i=parm.getCount()-1; i>=0; i--){
			for(int j=0; j<nameArr1.length; j++){
				if(parm.getData(nameArr1[j], i)==null){
					parm.setData(nameArr1[j], i, "");
				}
			}
			for(int k=0; k<nameArr2.length; k++){
				if(parm.getData(nameArr2[k], i)==null || parm.getData(nameArr2[k], i).toString().trim().length()==0){
					parm.removeRow(i);
					break;
				}
			}	   
		}
	}

	/**
	 * У������
	 * @param parm
	 * @param names2
	 */
	public void checkData(TParm parm, String names2){
		String[] names = parm.getNames(TParm.DEFAULT_GROUP);
		StringBuffer namesStr = new StringBuffer();
		for(int i=0; i<names.length; i++){
			if(namesStr.length()>0){
				namesStr.append(";");
			}
			namesStr.append(names[i]);
		}	
		String names1 = namesStr.toString();
		this.checkData(parm, names1, names2);
	}

	/**
	 * ҽ��ǩ��
	 */
	public boolean onSave1(){
		if(orderTable == null){
			this.messageBox("orderTable is null");
			return false;
		}
		TParm orderP = orderTable.getParmValue();
		if(orderP == null){
			this.messageBox("orderP is null");
			return false;
		}
		// У������
		checkData(orderP, "TRIAGE_NO;SEQ_NO;ORDER_CODE;ORDER_DESC");
		// ����ʱ���ʽ
		for(int i=0; i<orderP.getCount(); i++){
			String noteDate = StringTool.getString(TypeTool.getTimestamp(orderP.getData("NOTE_DATE", i)), "yyyy/MM/dd HH:mm:ss");
			orderP.setData("NOTE_DATE", i, noteDate);
		}
		TParm saveParm = new TParm();
		TParm result = new TParm();
		saveParm.setData("#ORDER", orderP.getData());	
		result = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "onSaveOrder3", saveParm);
		if(result.getErrCode()<0){
			this.messageBox("result.getErrCode()<0");
			return false;
		}
		// ˢ������
		this.onQuery();
		return true;
	}
	
	/**
	 * ȡ��ҽ��ǩ��
	 */
	public boolean onSave2(){
		if(orderTable == null){
			this.messageBox("orderTable is null");
			return false;
		}
		TParm orderP = orderTable.getParmValue();
		if(orderP == null){
			this.messageBox("orderP is null");
			return false;
		}
		// У������
		checkData(orderP, "TRIAGE_NO;SEQ_NO;ORDER_CODE;ORDER_DESC");
		// ����ʱ���ʽ
		for(int i=0; i<orderP.getCount(); i++){
			String noteDate = StringTool.getString(TypeTool.getTimestamp(orderP.getData("NOTE_DATE", i)), "yyyy/MM/dd HH:mm:ss");
			orderP.setData("NOTE_DATE", i, noteDate);
		}
		TParm saveParm = new TParm();
		TParm result = new TParm();
		saveParm.setData("#ORDER", orderP.getData());	
		result = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "onSaveOrder4", saveParm);
		if(result.getErrCode()<0){
			this.messageBox("result.getErrCode()<0");
			return false;
		}
		// ˢ������
		this.onQuery();
		return true;
	}

	/**
	 * ȫѡ
	 */
	public void onAllSelect(){
		if(orderTable == null){
			return;
		}
		TParm parm = orderTable.getParmValue();
		if(parm == null){
			return;
		}
		Map map = orderTable.getLockCellMap();	
		for(int i=0; i<parm.getCount(); i++){
			if(map != null && map.get(i+":0") != null && (Boolean) map.get(i+":0")){
				continue;
			}
			TablePublicTool.modifyRow(orderTable, i, 0, "SEL_FLG", parm.getBoolean("SEL_FLG", i), !parm.getBoolean("SEL_FLG", i));
		}
	}
	
}
