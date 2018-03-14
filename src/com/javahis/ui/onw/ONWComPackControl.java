package com.javahis.ui.onw;

import java.awt.Component;
import java.util.HashMap;
import javax.swing.SwingUtilities;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.ui.reg.TablePublicTool;
import jdo.odi.OdiMainTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p>��ͷҽ���ײ�ά������</p>
 * 
 * @author wangqing 20170831
 * 
 * <p>updateʱ��Ҫ��֤�����ֶβ�Ϊnull</p>
 * 
 * <p>ע��table.acceptText����ȷʹ��</p>
 * 
 * <p>���õ��¼���������༭��ֵ�ı䡢checkBox������༭��󷵻�ֵ��checkBox�����ҪacceptText</p>
 *
 */
public class ONWComPackControl extends TControl {
	/**
	 * �ײ�table
	 */
	private TTable tablePack;
	/**
	 * ҽ��table
	 */
	private TTable tableOrder;
	/**
	 * ����tablePackҪɾ��������
	 */
	private TParm deletePackParm;
	/**
	 * ����tableOrderҪɾ��������
	 */
	private TParm deleteOrderParm;
	/**
	 * �ײͺ�
	 */
	private String packCode;

	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();
		tablePack = (TTable) this.getComponent("TABLE_PACK");
		tableOrder = (TTable) this.getComponent("TABLE_ORDER");	
		callFunction("UI|" + "TABLE_PACK" + "|addEventListener", "TABLE_PACK" + "->" + TTableEvent.CLICKED, this, "onTableClicked");
		addEventListener("TABLE_PACK" + "->" + TTableEvent.CHANGE_VALUE, this, "onChangeTablePackValue");
		callFunction("UI|" + "TABLE_ORDER" + "|addEventListener", "TABLE_ORDER" + "->" + TTableEvent.CLICKED, this, "onTableOrderClicked");
		addEventListener("TABLE_ORDER" + "->" + TTableEvent.CHANGE_VALUE, this, "onChangeTableOrderValue");
		tableOrder.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this, "onCreateEditComoponent");
		tableOrder.addEventListener(TTableEvent.CHECK_BOX_CLICKED,this,"onCheckBoxValue");	
		// ��ʼ���ؼ�ֵ
		this.callFunction("UI|RX_TYPE|setEnabled", false);
		this.callFunction("UI|DEPT_CODE|setEnabled", false);
		//		this.callFunction("UI|NEW_PACK|setVisible", false);
		//		this.callFunction("UI|DELETE_PACK|setVisible", false);
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				onClear();			
			}
		});	
	}

	/**
	 * �����ײ�
	 */
	public void onNewPack(){	
		if(tablePack == null){
			this.messageBox("tablePack is null");
			return;
		}
		TParm packParm = tablePack.getParmValue();
		if(packParm == null){
			this.messageBox("packParm is null");
			return;
		}		
		String rxType = this.getValueString("RX_TYPE");// �ײ����
		String deptCode = this.getValueString("DEPT_CODE");// ����
		if(rxType.length()<=0){
			this.messageBox("��ѡ���ײ����");
			return;
		}
		if(deptCode.length()<=0){
			this.messageBox("��ѡ����ң�");
			return;
		}
		String packCode = SystemTool.getInstance().getNo("ALL", "ONW", "ONWPACK_NO", "ONWPACK_NO");
		if(packCode == null || packCode.trim().length()==0){
			this.messageBox("packCode is null");
			return;
		}	
		int row = TablePublicTool.addRow(tablePack);
		if(row == -1){
			this.messageBox("row == -1");
			return;
		}
		// ����ʼֵ
		packParm.setData("PACK_CODE", row, packCode);// �ײͺ�
		TablePublicTool.modifyRow(tablePack, row);
		TablePublicTool.modifyRow(tablePack, row, 0, "RX_TYPE", "", rxType);// �ײ�����
		TablePublicTool.modifyRow(tablePack, row, 2, "DEPT_CODE", "", deptCode);// ����
		TablePublicTool.modifyRow(tablePack, row, 3, "OPT_USER", "", Operator.getID());// ������Ա
		TablePublicTool.modifyRow(tablePack, row, 4, "OPT_DATE", "", SystemTool.getInstance().getDate());// ����ʱ��
		TablePublicTool.modifyRow(tablePack, row, 5, "OPT_TERM", "", Operator.getIP());// �����ն�
		// Ĭ��ѡ��
		tablePack.setSelectedRow(row);
		onTableClicked(row);
	}

	/**
	 * ��ѯ�ײ�
	 */
	public void onQuery(){
		// ��ʼ��
		this.onClear2();
		this.unlock(tableOrder);
		String rxType = this.getValueString("RX_TYPE");// �ײ����
		if(rxType.length()<=0){
			this.messageBox("��ѡ���ײ����");
			return;
		}	
		TParm selectParm = new TParm();
		TParm selectResult = new TParm();
		selectParm.setData("RX_TYPE", rxType);
		selectResult = TIOM_AppServer.executeAction("action.onw.ONWComPackAction","selectOnwPackMain", selectParm);
		if(selectResult.getErrCode()<0){
			this.messageBox("err selectResult");
			return;
		}
		TablePublicTool.setParmValue(tablePack, selectResult);
//		// Ĭ�Ϲ�ѡ��һ��	
//		if(selectResult.getCount()>0){
//			tablePack.setSelectedRow(0);
//			tablePack.setSelectedColumn(0);
//			onTableClicked(0);
//		}
	}

	/**
	 * ɾ���ײ�
	 */
	public void onDeletePack(){
		if(tablePack == null){
			this.messageBox("tablePack is null");
			return;
		}
		TParm packParmValue = tablePack.getParmValue();
		if(packParmValue == null){
			this.messageBox("packParmValue is null");
			return;
		}
		int selectedRow = tablePack.getSelectedRow();
		if(selectedRow<0){
			this.messageBox("��ѡ��ɾ����");
			return;
		}				
		String packCode = packParmValue.getValue("PACK_CODE", selectedRow);
		if(packCode == null || packCode.trim().length()==0){
			this.messageBox("packCode is null");
			return;
		}
		// tableOrder����
		this.unlock(tableOrder);
		// tableOrder���
		this.onClear4();		
		if(deletePackParm == null){
			deletePackParm = new TParm();
			deletePackParm.setCount(0);
		}
		TablePublicTool.removeRow(tablePack, selectedRow, deletePackParm, new String[]{"PACK_CODE"}, new String[]{packCode});
	}

	/**
	 * ����ҽ��
	 * @param packCode �ײ�����
	 * @param seqNo ҽ�����
	 */
	public int onNewOrder(String packCode, int seqNo){
		if(packCode == null || packCode.trim().length()==0){
			this.messageBox("packCode is null");
			return -1;
		}
		if(tableOrder == null){
			this.messageBox("tableOrder is null");
			return -1;
		}
		TParm orderParm = tableOrder.getParmValue();
		if(orderParm == null){
			this.messageBox("orderParm is null");
			return -1;
		}
		int row = TablePublicTool.addRow(tableOrder);
		if(row == -1){
			this.messageBox("row == -1");
			return row;
		}
		// ����ʼֵ
		orderParm.setData("PACK_CODE", row, packCode);// �ײ�����
		TablePublicTool.modifyRow(tableOrder, row);
		orderParm.setData("SEQ_NO", row, seqNo);// ҽ�����
		TablePublicTool.modifyRow(tableOrder, row);
		TablePublicTool.modifyRow(tableOrder, row, 6, "FREQ_CODE", "", "STAT");// Ƶ�Σ�Ĭ��Ϊ����ʹ��
		TablePublicTool.modifyRow(tableOrder, row, 7, "TAKE_DAYS", "", 1);// ������Ĭ��Ϊ1��
		TablePublicTool.modifyRow(tableOrder, row, 8, "OPT_USER", "", Operator.getID());// ������Ա
		TablePublicTool.modifyRow(tableOrder, row, 9, "OPT_DATE", "", SystemTool.getInstance().getDate());// ����ʱ��
		TablePublicTool.modifyRow(tableOrder, row, 10, "OPT_TERM", "", Operator.getIP());// �����ն�
		return row;
	}

	/**
	 * ɾ��ҽ��
	 */
	public void onDeleteOrder(){
		if(tableOrder == null){
			this.messageBox("tableOrder is null");
			return;
		}
		TParm orderParmValue = tableOrder.getParmValue();
		if(orderParmValue == null){
			this.messageBox("orderParmValue is null");
			return;
		}
		int selectedRow = tableOrder.getSelectedRow();
		if(selectedRow<0){
			this.messageBox("��ѡ��ɾ����");
			return;
		}
		// ���в���ɾ��
		if(orderParmValue.getValue("ORDER_CODE", selectedRow) == null 
				|| orderParmValue.getValue("ORDER_CODE", selectedRow).trim().length()==0){
			return;
		}	
		// tableOrder����
		this.unlock(tableOrder);
		if(deleteOrderParm == null){
			deleteOrderParm = new TParm();
			deleteOrderParm.setCount(0);
		}	
				
		String packCode;
		String seqNo;		
		// ������������ɾ��ͬ���ҽ�������г��⣩
		if(orderParmValue.getValue("LINKMAIN_FLG", selectedRow) != null 
				&& orderParmValue.getValue("LINKMAIN_FLG", selectedRow).equals("Y")){
			String linkNo = orderParmValue.getValue("LINK_NO", selectedRow);
			// ѭ��ɾ��ͬ��
			for(int i=orderParmValue.getCount()-1; i>=0; i--){// �Ӵ��кſ�ʼɾ��
				// ���в���ɾ��
				if(orderParmValue.getValue("ORDER_CODE", i)==null 
						|| orderParmValue.getValue("ORDER_CODE", i).trim().length()==0 ){
					TablePublicTool.modifyRow(tableOrder, i, 1, "LINK_NO", orderParmValue.getValue("LINK_NO", i), "");
				}
				if(orderParmValue.getValue("LINK_NO", i) != null 
						&& orderParmValue.getValue("LINK_NO", i).equals(linkNo)){
					packCode = orderParmValue.getValue("PACK_CODE", i);
					seqNo = orderParmValue.getValue("SEQ_NO", i);
					TablePublicTool.removeRow(tableOrder, i, deleteOrderParm, new String[]{"PACK_CODE", "SEQ_NO"}, new String[]{packCode, seqNo});
				}
			}
		}else{// ����������
			packCode = orderParmValue.getValue("PACK_CODE", selectedRow);
			seqNo = orderParmValue.getValue("SEQ_NO", selectedRow);
			TablePublicTool.removeRow(tableOrder, selectedRow, deleteOrderParm, new String[]{"PACK_CODE", "SEQ_NO"}, new String[]{packCode, seqNo});
		}
		// tableOrder����
		this.lock();
	}

	/**
	 * ����
	 */
	public void onSave(){
		if(tablePack == null){
			this.messageBox("tablePack is null");
			return;
		}
		if(tableOrder == null){
			this.messageBox("tableOrder is null");
			return;
		}
		TParm parmPackValue = tablePack.getParmValue();
		TParm parmOrderValue = tableOrder.getParmValue();	
		TParm saveParm = new TParm();
		if(parmPackValue != null){
			this.checkData(parmPackValue, "PACK_CODE;PACK_DESC;RX_TYPE;DEPT_CODE;OPT_USER;OPT_DATE;OPT_TERM;#STATUS");
			saveParm.setData("#PACK", parmPackValue.getData());
		}
		if(parmOrderValue != null){
			this.checkData(parmOrderValue, "PACK_CODE;SEQ_NO;ORDER_CODE;ORDER_DESC;OPT_USER;OPT_DATE;OPT_TERM");
			saveParm.setData("#ORDER", parmOrderValue.getData());
		}		
		if(deletePackParm != null){
			saveParm.setData("#PACK_DELETE", deletePackParm.getData());
		}
		if(deleteOrderParm != null){
			saveParm.setData("#ORDER_DELETE", deleteOrderParm.getData());
		}
		// ��������
		TParm saveResult = new TParm();
		saveResult = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "onSave", saveParm);
		if(saveResult == null 
				|| saveResult.getErrCode()<0){
			this.messageBox("����ʧ�ܣ�����");
			return;
		}
		this.messageBox("����ɹ�������");	
		// ˢ������
		String tempPackCode = this.packCode;
		this.onClear();	
		if(tempPackCode != null && tempPackCode.trim().length()>0){
			TParm packParm = tablePack.getParmValue();
			for(int i=0; i<packParm.getCount(); i++){
				if(packParm.getValue("PACK_CODE", i) != null && packParm.getValue("PACK_CODE", i).equals(tempPackCode)){
					tablePack.setSelectedRow(i);
					tablePack.setSelectedColumn(0);
					this.onTableClicked(i);
					break;
				}
			}
		}	
	}

	/**
	 * ���
	 */
	public void onClear(){
		this.onClear1();
		this.onQuery();
	}
	
	/**
	 * ���1
	 */
	public void onClear1(){
		this.clearValue("RX_TYPE;DEPT_CODE");
		this.setValue("RX_TYPE", "0");
		this.setValue("DEPT_CODE", "0202");
		this.onClear2();
	}

	/**
	 * ���2
	 */
	public void onClear2(){
		this.onClear3();
		this.onClear4();
	}

	public void onClear3(){
		TParm parmValue1 = new TParm();
		parmValue1.setCount(0);
		tablePack.setParmValue(parmValue1);
		deletePackParm = new TParm();
		deletePackParm.setCount(0);
	}

	public void onClear4(){
		packCode = "";
		TParm parmValue2 = new TParm();
		parmValue2.setCount(0);
		tableOrder.setParmValue(parmValue2);
		deleteOrderParm = new TParm();	
		deleteOrderParm.setCount(0);
	}

	/**
	 * TABLE_PACK�����¼�
	 * @param row ��������
	 */
	public void onTableClicked(int row) {
		if(row<0){
			return;
		}
		TParm packParm = tablePack.getParmValue();
		String packCode = packParm.getValue("PACK_CODE", row);
		if(packCode == null || packCode.trim().length()==0){
			this.messageBox("packCode is null");
			return;
		}
		// tableOrder����
		this.unlock(tableOrder);
		// tableOrder���
		this.onClear4();
		this.packCode = packCode;
		String deptCode = packParm.getValue("DEPT_CODE", row);
		this.setValue("DEPT_CODE", deptCode);			
		TParm selectParm = new TParm();
		TParm selectResult = new TParm();
		selectParm.setData("PACK_CODE", packCode);
		selectResult = TIOM_AppServer.executeAction("action.onw.ONWComPackAction","selectOnwPackOrder", selectParm);
		if(selectResult.getErrCode()<0){
			this.messageBox("selectResult.getErrCode()<0");
			return;
		}
		TablePublicTool.setParmValue(tableOrder, selectResult);	
		int seqNo = getMaxSeqNo(packCode);
		if(seqNo<0){
			this.messageBox("seqNo<0");
			return;
		}
		// ��������
		int newR = onNewOrder(packCode, seqNo);	
		if(newR<0){
			this.messageBox("newR<0");
			// tableOrder����
			this.lock();
			return;
		}
		// tableOrder����
		this.lock();
	}

	/**
	 * TABLE_ORDER�����¼�
	 * @param row
	 */
	public void onTableOrderClicked(int row) {
	}

	/**
	 * TABLE_PACKֵ�ı����
	 * @param obj TTableNode
	 * @return true�����ı䣻false���ı�
	 */
	public boolean onChangeTablePackValue(Object obj) {
		TTableNode node = (TTableNode) obj;
		if (node == null){ 
			return true;	
		}
		int row = node.getRow();// �к�		
		int col = node.getColumn();// �к�
		TTable table = node.getTable(); // ���ݱ�
		//		String columnName = node.getTable().getParmMap(col);// ����
		if(col != 1){// ���ײ�������
			return true;
		}
		TablePublicTool.modifyRow(table, row);
		return false;
	}

	/**
	 * TABLE_ORDERֵ�ı����
	 * @param obj TTableNode
	 * @return true�����ı䣻false���ı�
	 */
	public boolean onChangeTableOrderValue(Object obj) {
		TTableNode node = (TTableNode) obj;
		if (node == null){ 
			return true;	
		}	
		int row = node.getRow();// �к�		
		int col = node.getColumn();// �к�
		TTable table = node.getTable(); // ���ݱ�
		//		String columnName = node.getTable().getParmMap(col);// ����
		TablePublicTool.modifyRow(table, row);
		// �������Ϊ�÷��У�������������޸�ͬ����÷�	
		TParm orderParm = table.getParmValue();
		if((col == 5) 
				&& orderParm.getValue("LINKMAIN_FLG", row) != null 
				&& orderParm.getValue("LINKMAIN_FLG", row).equals("Y")){
			String linkNo = orderParm.getValue("LINK_NO", row);		
			for(int i=0; i<orderParm.getCount(); i++){
				if(orderParm.getValue("LINK_NO", i) != null 
						&& orderParm.getValue("LINK_NO", i).equals(linkNo)){
					TablePublicTool.modifyRow(table, i, 5, "ROUTE_CODE", orderParm.getValue("ROUTE_CODE", i), node.getValue());
				}
			}
		}
		return false;
	}

	/**
	 * TABLE_ORDER�����¼����༭ҽ����
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComoponent(Component com,int row,int col){	
		if(col == 2){
			if(!(com instanceof TTextField))
				return;
			TTextField textFilter = (TTextField)com;
			textFilter.onInit();
			TParm parm = new TParm();
			parm.setData("ONW_PACK_ORDER", "A");// ȫ��ҽ��
			//���õ����˵�
			textFilter.setPopupMenuParameter("ONW_PACK_ORDER", getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
			//������ܷ���ֵ����
			textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		}		
	}

	/**
	 * TABLE_ORDER�����¼����༭ҽ���������ܷ���ֵ����
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag,Object obj){
		if (obj == null || !(obj instanceof TParm)) {
			return;
		}
		TParm parm = (TParm)obj;
		if("ONW_PACK_ORDER".equals(tag)){			
			tableOrder.acceptText();
			int row = tableOrder.getSelectedRow();
			int col = tableOrder.getSelectedColumn();
//			System.out.println("======//////row="+row);
//			System.out.println("======//////col="+col);
			onInsertOrderList(row,parm);
		}
	}

	/**
	 * TABLE_ORDER�����¼����༭ҽ�������޸�ҽ��
	 * @param row
	 * @param parm
	 */
	public void onInsertOrderList(int row,TParm parm){
		TParm orderParm = tableOrder.getParmValue();
		if(orderParm == null){
			this.messageBox("orderParm is null");
			return;
		}
		String orderCode = parm.getValue("ORDER_CODE");
		if(isSame(orderParm, orderCode, row)){// ����ͬҽ��
			if (messageBox("������ʾ", "����ͬҽ�����Ƿ����������ҽ��?", TControl.YES_NO_OPTION) != 0) {// ������
				TablePublicTool.modifyRow(tableOrder, row, 2, "ORDER_DESC", 
						orderParm.getValue("ORDER_DESC", row), getOrderDesc(orderParm.getValue("ORDER_CODE", row)));	
				return;
			}else{// ����
				insertPackOrder(row,parm);
			}
		}else{// û����ͬҽ��
			insertPackOrder(row,parm);
		}
	}

	/**
	 * TABLE_ORDER�����¼����༭ҽ�������ж��Ƿ�����ͬҽ��
	 * @param parm
	 * @param orderCode
	 * @param row
	 * @return
	 */
	public boolean isSame(TParm parm, String orderCode, int row){
		boolean flag = false;
		int count = parm.getCount();
		for(int i=0; i<count; i++){
			if(i == row){
				continue;
			}
			if(parm.getValue("ORDER_CODE", i) == null || parm.getValue("ORDER_CODE", i).trim().length()==0){
				continue;
			}
			if(parm.getValue("ORDER_CODE", i).equals(orderCode)){
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * TABLE_ORDER�����¼����༭ҽ�������޸�ҽ��
	 * @param row
	 * @param parm
	 */
	public void insertPackOrder(int row,TParm parm){
		// tableOrder����
		this.unlock(tableOrder);		
		TParm orderParmValue = tableOrder.getParmValue();
		orderParmValue.setData("ORDER_CODE", row, parm.getValue("ORDER_CODE"));// ҽ������	
		TablePublicTool.modifyRow(tableOrder, row);
		TablePublicTool.modifyRow(tableOrder, row, 2, "ORDER_DESC", 
				orderParmValue.getValue("ORDER_DESC", row), parm.getValue("ORDER_DESC")+ " "+parm.getValue("SPECIFICATION"));// ҽ������	
		orderParmValue.setData("CAT1_TYPE", row, parm.getValue("CAT1_TYPE"));// ҽ��������
		TablePublicTool.modifyRow(tableOrder, row);
		orderParmValue.setData("ORDER_CAT1_CODE", row, parm.getValue("ORDER_CAT1_CODE"));// ҽ��С����
		TablePublicTool.modifyRow(tableOrder, row);	
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			TParm action = new TParm();
			action.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
			TParm result = OdiMainTool.getInstance().queryPhaBase(action);
			TablePublicTool.modifyRow(tableOrder, row, 3, "MEDI_QTY", 
					orderParmValue.getValue("MEDI_QTY", row), result.getValue("MEDI_QTY", 0));// ����
			TablePublicTool.modifyRow(tableOrder, row, 4, "MEDI_UNIT", 
					orderParmValue.getValue("MEDI_UNIT", row), result.getValue("MEDI_UNIT", 0));// ��λ
			TablePublicTool.modifyRow(tableOrder, row, 5, "ROUTE_CODE", 
					orderParmValue.getValue("ROUTE_CODE", row), result.getValue("ROUTE_CODE", 0));// �÷�
		}else{
			TablePublicTool.modifyRow(tableOrder, row, 3, "MEDI_QTY", 
					orderParmValue.getValue("MEDI_QTY", row), 1);// ����
			TablePublicTool.modifyRow(tableOrder, row, 4, "MEDI_UNIT", 
					orderParmValue.getValue("MEDI_UNIT", row), parm.getData("UNIT_CODE"));// ��λ
			TablePublicTool.modifyRow(tableOrder, row, 5, "ROUTE_CODE", 
					orderParmValue.getValue("ROUTE_CODE", row), "");// �÷�
		}
		if(!isNew(orderParmValue)){
			int seqNo = getMaxSeqNo(packCode);
			if(seqNo<0){
				this.messageBox("err seqNo");
				// tableOrder����
				this.lock();
				return;
			}			
			int newR = onNewOrder(packCode, seqNo);
			if(newR<0){
				this.messageBox("newR<0");
				// tableOrder����
				this.lock();
				return;
			}
		}
		// tableOrder����
		this.lock();	
	}

	/**
	 * �ж��Ƿ���δ�༭��ҽ��
	 * @param parm
	 */
	public boolean isNew(TParm parm){
		boolean flag = false;
		int count = parm.getCount();
		for(int i=0; i<count; i++){
			if(parm.getValue("ORDER_CODE", i) == null ||parm.getValue("ORDER_CODE", i).trim().length()==0){
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * ��ȡ���seqNo
	 * @param packCode
	 * @return ���seqNo
	 */
	public int getMaxSeqNo(String packCode){
		String selSql = " SELECT MAX(SEQ_NO) AS SEQ_NO FROM ONW_PACK_ORDER WHERE PACK_CODE = '"+packCode+"' ";
		TParm selResult = new TParm(TJDODBTool.getInstance().select(selSql));
		if(selResult.getErrCode()<0){
			return -1;
		}	
		int temp = selResult.getInt("SEQ_NO", 0);
		TParm orderParm = tableOrder.getParmValue();
		int count = orderParm.getCount();
		for(int i=0; i<count; i++){
			int seqNo = orderParm.getInt("SEQ_NO", i);
			if(seqNo>temp){
				temp = seqNo;
			}
		}	
		return temp+1;
	}

	/**
	 * TABLE_ORDER�����¼����������
	 * @param obj
	 */
	public void onCheckBoxValue(Object obj){
		// tableOrder����
		this.unlock(tableOrder);
		TTable table = (TTable)obj;		
		table.acceptText();
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();
//		System.out.println("======//////row="+row);
//		System.out.println("======//////col="+col);
		TParm orderParmValue = table.getParmValue();
		if(orderParmValue == null){
			this.messageBox("orderParmValue is null");
			// tableOrder����
			this.lock();
			return;
		}		
		if(col == 0){// ��
			// ѡ�У����ڵ��ڴ���&&С�ڵ�һ������������������ &&С�ڿ���
			// ���ֳ�һ�飬�÷�����һ��
			if("Y".equals(orderParmValue.getValue("LINKMAIN_FLG", row))){
				// ���в���ѡ��
				if (orderParmValue.getValue("ORDER_CODE", row) == null 
						|| orderParmValue.getValue("ORDER_CODE", row).length() == 0) {
					this.messageBox("�뿪��ҽ��");
					TablePublicTool.modifyRow(table, row, 0, "LINKMAIN_FLG", 
							orderParmValue.getValue("LINKMAIN_FLG", row), "N");// ��
					// tableOrder����
					this.lock();
					return;
				}
				// �����÷�
				if("PHA".equals(orderParmValue.getValue("CAT1_TYPE", row))){
					TParm action = new TParm();
					action.setData("ORDER_CODE", orderParmValue.getValue("ORDER_CODE", row));
					TParm result = OdiMainTool.getInstance().queryPhaBase(action);
					TablePublicTool.modifyRow(table, row, 5, "ROUTE_CODE", 
							orderParmValue.getValue("ROUTE_CODE", row), result.getValue("ROUTE_CODE",0));// �÷�
				}else{
					TablePublicTool.modifyRow(table, row, 5, "ROUTE_CODE", 
							orderParmValue.getValue("ROUTE_CODE", row), "");// �÷�
				}			
				int maxLinkNo = getMaxLinkNo();
				for(int i=row; i<orderParmValue.getCount(); i++){					
					if(i>row){
						if(orderParmValue.getValue("LINKMAIN_FLG", i) != null 
								&& orderParmValue.getValue("LINKMAIN_FLG", i).equals("Y")){// ��������
							break;
						}	
						if(orderParmValue.getValue("ORDER_CODE", i) == null 
								|| orderParmValue.getValue("ORDER_CODE", i).trim().length()==0){// ����
							break;
						}
					}
					TablePublicTool.modifyRow(table, i, 1, "LINK_NO", 
							orderParmValue.getValue("LINK_NO", i), maxLinkNo);// ���
					TablePublicTool.modifyRow(table, i, 5, "ROUTE_CODE", 
							orderParmValue.getValue("ROUTE_CODE", i), orderParmValue.getValue("ROUTE_CODE", row));// �÷�
				}
			}else{// ȡ��ѡ�У���ͬ���LINK_NO��ֵΪ�գ���ͬ�������÷�
				String linkNo = orderParmValue.getValue("LINK_NO", row);			
				for(int i=row; i<orderParmValue.getCount(); i++){
					if(orderParmValue.getValue("LINK_NO", i) != null 
							&& orderParmValue.getValue("LINK_NO", i).equals(linkNo)){
						TablePublicTool.modifyRow(table, i, 1, "LINK_NO", 
								orderParmValue.getValue("LINK_NO", i), "");// ���
						if("PHA".equals(orderParmValue.getValue("CAT1_TYPE", i))){
							TParm action = new TParm();
							action.setData("ORDER_CODE", orderParmValue.getValue("ORDER_CODE", i));
							TParm result = OdiMainTool.getInstance().queryPhaBase(action);
							TablePublicTool.modifyRow(table, i, 5, "ROUTE_CODE", 
									orderParmValue.getValue("ROUTE_CODE", i), result.getValue("ROUTE_CODE",0));// �÷�
						}else{
							TablePublicTool.modifyRow(table, i, 5, "ROUTE_CODE", 
									orderParmValue.getValue("ROUTE_CODE", i), "");// �÷�
						}
					}
				}
			}
		}	
		// tableOrder����
		this.lock();
	}

	/**
	 * �õ���������
	 * @return
	 */
	public int getMaxLinkNo(){
		String selSql = " SELECT PACK_CODE, LINK_NO FROM ONW_PACK_ORDER WHERE PACK_CODE = '"+packCode+"' ";
		TParm selResult = new TParm(TJDODBTool.getInstance().select(selSql));
		if(selResult.getErrCode()<0){
			return -1;
		}
		int linkNo = 0;
		for(int i=0; i<selResult.getCount(); i++){
			if(selResult.getInt("LINK_NO", i) > linkNo){
				linkNo = selResult.getInt("LINK_NO", i);
			}
		}
		TParm orderParm = tableOrder.getParmValue();
		for(int i=0; i<orderParm.getCount(); i++){
			if(orderParm.getInt("LINK_NO", i) > linkNo){
				linkNo = orderParm.getInt("LINK_NO", i);
			}
		}
		return linkNo+1;
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
	 * ��ȡҽ������
	 * @param orderCode
	 * @return
	 */
	public String getOrderDesc(String orderCode){
		String sql = " SELECT ORDER_CODE, ORDER_DESC||''||SPECIFICATION AS ORDER_DESC FROM SYS_FEE WHERE ORDER_CODE='"+orderCode+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			return "";			
		}
		if(result.getCount()<=0){
			return "";
		}
		return result.getValue("ORDER_DESC", 0);
	}

	/**
	 * ����
	 * @param table
	 */
	public void unlock(TTable table){
		table.setLockCellMap(new HashMap());
	}

	/**
	 * tableOrder����
	 */
	public void lock(){
		TParm orderParm = tableOrder.getParmValue();
		if(orderParm == null){
			this.messageBox("orderParm is null");
			return;
		}
		for(int i=0; i<orderParm.getCount(); i++){
			// ��ҽ�����У� ҽ�����Ʋ��ɱ༭
			if(orderParm.getValue("ORDER_CODE", i) != null 
					&& orderParm.getValue("ORDER_CODE", i).trim().length()>0){
				tableOrder.setLockCell(i, 2, true);
			}
			// ����ϸ� �÷����ɱ༭
			if((!(orderParm.getValue("LINKMAIN_FLG", i) != null 
					&& orderParm.getValue("LINKMAIN_FLG", i).equals("Y"))) 
					&& tableOrder.getParmValue().getValue("LINK_NO", i) != null 
					&& tableOrder.getParmValue().getValue("LINK_NO", i).trim().length()>0 ){
				tableOrder.setLockCell(i, 5, true);
			}		
		}
	}
	
	/**
	 * ����
	 */
	public void onTest(){
//		System.out.println("======//////pack="+tablePack.getParmValue());
//		System.out.println("======//////order="+tableOrder.getParmValue());
//		System.out.println("======//////deletePackParm="+deletePackParm);
//		System.out.println("======//////deleteOrderParm="+deleteOrderParm);
		System.out.println("****************row="+tablePack.getSelectedRow());
		System.out.println("col="+tablePack.getSelectedColumn());
	}


}
