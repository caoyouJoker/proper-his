package com.javahis.ui.onw;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.database.VPicDialogControl;
import com.javahis.ui.reg.TablePublicTool;
import jdo.odi.OdiMainTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p>��ͷҽ������</p>
 * 
 * @author wangqing 20171013
 *
 */
public class ONWOrderControl extends TControl {
	private TTable tablePack;
	private TTable tableOrder;
	private TTable orderT;
	/**
	 * ϵͳ����
	 */
	private TParm sysParm;
	/**
	 * ���˺�
	 */
	private String triageNo;

	private TParm deleteParm;
	
	/**
	 * �ײͺ�
	 */
	private String packCode;

	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();
		Object o = this.getParameter();
		if(o != null && o instanceof TParm){
			sysParm = (TParm) o;
			triageNo = sysParm.getValue("TRIAGE_NO");
			this.setValue("TRIAGE_NO", triageNo);// ���˺�
			this.setValue("MR_NO", sysParm.getValue("MR_NO"));// ������
			this.setValue("PAT_NAME", sysParm.getValue("PAT_NAME"));// ��������
			this.setValue("PAT_SEX", sysParm.getValue("PAT_SEX"));// �����Ա�
			this.setValue("PAT_AGE", sysParm.getValue("PAT_AGE"));// ��������
		}else{
			this.messageBox("ϵͳ�������󣡣���");
			SwingUtilities.invokeLater(new Runnable(){
				public void run() {
					closeWindow();
				}
			});	
			return;
		}
//		triageNo = "20171027002";
		tablePack = (TTable) this.getComponent("TABLE_PACK");
		tableOrder = (TTable) this.getComponent("TABLE_ORDER");
		orderT = (TTable) this.getComponent("ORDER");				
		//TABLE_PACK�����¼�
		callFunction("UI|" + "TABLE_PACK" + "|addEventListener", "TABLE_PACK" + "->" + TTableEvent.CLICKED, this, "onTableClicked");
		//TABLE_ORDER�����¼�
		callFunction("UI|" + "TABLE_ORDER" + "|addEventListener", "TABLE_ORDER" + "->" + TTableEvent.DOUBLE_CLICKED, this, "onTableOrderDoubleClicked");
		// orderT�����¼�
		callFunction("UI|" + "TABLE_ORDER" + "|addEventListener", "TABLE_ORDER" + "->" + TTableEvent.CLICKED, this, "onOrderTClicked");
		// orderT��ֵ�ı��¼�
		addEventListener("ORDER" + "->" + TTableEvent.CHANGE_VALUE, this, "onChangeOrderTValue");
		// orderT��checkBox�¼�
		orderT.addEventListener(TTableEvent.CHECK_BOX_CLICKED,this,"onCheckBoxValue");
		// orderT�Ŀؼ��༭�¼�
		orderT.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this, "onCreateEditComoponent");		
		// ��ʼ������
		this.callFunction("UI|RX_TYPE|setEnabled", false);
		this.callFunction("UI|DEPT_CODE|setEnabled", false);				
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				TFrame frame = (TFrame) getComponent();
//				frame.setPreferredSize(new Dimension(1000,500));
//				frame.pack();
				frame.setResizable(false);  // ��������
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Ĭ�����		
				onClear();	
			}
		});
	}

	/**
	 * ��ѯ�ײ�
	 */
	public void onQuery(){
		// ��ʼ��
		this.onClear2();	
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
	}

	/**
	 * TABLE_ORDER˫���¼�
	 * @param row
	 */
	public void onTableOrderDoubleClicked(int row) {
		if(row<0){
			return;
		}
		if(tableOrder == null){
			this.messageBox("tableOrder is null");
			return;
		}
		TParm orderParm = tableOrder.getParmValue();
		if(orderParm == null){
			this.messageBox("orderParm is null");
			return;
		}
		TParm rowParm = orderParm.getRow(row);
		if(rowParm == null){
			this.messageBox("rowParm is null");
			return;
		}
		TParm orderP = orderT.getParmValue();
		if(orderP == null){
			this.messageBox("orderP is null");
			return;
		}			
		
		String orderCode = rowParm.getValue("ORDER_CODE");
		String orderDesc = rowParm.getValue("ORDER_DESC");
		// У���ҽ���Ƿ��Ѿ�ͣ��
		if(!checkActive(orderCode)){
			this.messageBox(orderDesc+"�Ѿ�ͣ�ã������Կ���");
			return;
		}	
		// У���Ƿ��Ѿ����
		for(int i=0; i<orderP.getCount(); i++){
			if(orderP.getValue("ORDER_CODE", i) == null 
					|| orderP.getValue("ORDER_CODE", i).trim().length()==0){
				continue;
			}
			if(orderP.getValue("ORDER_CODE", i).equals(orderCode)){
				if (messageBox("������ʾ", "��ҽ���Ѿ��������Ƿ��������?", TControl.YES_NO_OPTION) != 0) {// ������	
					return;
				}
			}
		}	
		//  orderT����
		this.unlock(orderT);
		// orderTɾ������
		if(deleteParm != null){
			deleteParm = new TParm();
			deleteParm.setCount(0);
		}	
		int delR = orderP.getCount()-1;
		TablePublicTool.removeRow(orderT, delR, deleteParm, 
				new String[]{"TRIAGE_NO", "SEQ_NO"}, new String[]{triageNo, orderP.getValue("SEQ_NO", delR)});
	    // ��������	
		// �����������ͬ���ȫ����
		if(rowParm.getValue("LINK_NO") != null 
				&& rowParm.getValue("LINK_NO").trim().length()>0){// ����
			int linkNo = getMaxLinkNo();// ���
			for(int i=0; i<orderParm.getCount(); i++){
				if(orderParm.getValue("LINK_NO", i) != null 
						&& orderParm.getValue("LINK_NO", i).equals(rowParm.getValue("LINK_NO"))){// ͬ������
					String orderCodeTemp = orderParm.getValue("ORDER_CODE", i);
					String orderDescTemp = orderParm.getValue("ORDER_DESC", i);
					// У���ҽ���Ƿ��Ѿ�ͣ��
					if(!checkActive(orderCodeTemp)){
						this.messageBox(orderDescTemp+"�Ѿ�ͣ�ã������Կ���");
						continue;
					}
					int seqNo = getMaxSeqNo(triageNo);// ҽ�����
					int newR = this.onNewOrder(triageNo, seqNo);
					if(newR<0){
						this.messageBox("newR<0");
						// orderT����
						this.lock();
						return;
					}
					// ��ֵ
					TablePublicTool.modifyRow(orderT, newR, 1, "LINKMAIN_FLG", 
							orderP.getValue("LINKMAIN_FLG", newR), orderParm.getValue("LINKMAIN_FLG", i));// ��
					TablePublicTool.modifyRow(orderT, newR, 2, "LINK_NO", 
							orderP.getValue("LINK_NO", newR), linkNo);// ��
					orderP.setData("ORDER_CODE", newR, orderParm.getValue("ORDER_CODE", i));// ҽ������
					TablePublicTool.modifyRow(orderT, row);
					TablePublicTool.modifyRow(orderT, newR, 3, "ORDER_DESC", 
							orderP.getValue("ORDER_DESC", newR), orderParm.getValue("ORDER_DESC", i));// ҽ������
					TablePublicTool.modifyRow(orderT, newR, 4, "MEDI_QTY", 
							orderP.getValue("MEDI_QTY", newR), orderParm.getValue("MEDI_QTY", i));// ����
					TablePublicTool.modifyRow(orderT, newR, 5, "MEDI_UNIT", 
							orderP.getValue("MEDI_UNIT", newR), orderParm.getValue("MEDI_UNIT", i));// ��λ
					TablePublicTool.modifyRow(orderT, newR, 6, "ROUTE_CODE", 
							orderP.getValue("ROUTE_CODE", newR), orderParm.getValue("ROUTE_CODE", i));// �÷�
					TablePublicTool.modifyRow(orderT, newR, 7, "FREQ_CODE", 
							orderP.getValue("FREQ_CODE", newR), orderParm.getValue("FREQ_CODE", i));// Ƶ��
					TablePublicTool.modifyRow(orderT, newR, 8, "TAKE_DAYS", 
							orderP.getValue("TAKE_DAYS", newR), orderParm.getValue("TAKE_DAYS", i));// ����
					orderP.setData("CAT1_TYPE", newR, orderParm.getValue("CAT1_TYPE", i));// �����
					TablePublicTool.modifyRow(orderT, row);
					orderP.setData("ORDER_CAT1_CODE", newR, orderParm.getValue("ORDER_CAT1_CODE", i));// С����
					TablePublicTool.modifyRow(orderT, row);
					TablePublicTool.modifyRow(orderT, newR, 9, "NOTE_DATE", 
							orderP.getValue("NOTE_DATE", newR), SystemTool.getInstance().getDate());// ��¼����					
				}
			}				
		}else{// ������
			int seqNo = getMaxSeqNo(triageNo);
			int newR = this.onNewOrder(triageNo, seqNo);
			if(newR<0){
				this.messageBox("newR<0");
				// orderT����
				this.lock();
				return;
			}
			// ��ֵ
			TablePublicTool.modifyRow(orderT, newR, 1, "LINKMAIN_FLG", 
					orderP.getValue("LINKMAIN_FLG", newR), rowParm.getValue("LINKMAIN_FLG"));// ��
			orderP.setData("ORDER_CODE", newR, rowParm.getValue("ORDER_CODE"));// ҽ������
			TablePublicTool.modifyRow(orderT, row);
			TablePublicTool.modifyRow(orderT, newR, 3, "ORDER_DESC", 
					orderP.getValue("ORDER_DESC", newR), rowParm.getValue("ORDER_DESC"));// ҽ������
			TablePublicTool.modifyRow(orderT, newR, 4, "MEDI_QTY", 
					orderP.getValue("MEDI_QTY", newR), rowParm.getValue("MEDI_QTY"));// ����
			TablePublicTool.modifyRow(orderT, newR, 5, "MEDI_UNIT", 
					orderP.getValue("MEDI_UNIT", newR), rowParm.getValue("MEDI_UNIT"));// ��λ
			TablePublicTool.modifyRow(orderT, newR, 6, "ROUTE_CODE", 
					orderP.getValue("ROUTE_CODE", newR), rowParm.getValue("ROUTE_CODE"));// �÷�
			TablePublicTool.modifyRow(orderT, newR, 7, "FREQ_CODE", 
					orderP.getValue("FREQ_CODE", newR), rowParm.getValue("FREQ_CODE"));// Ƶ��
			TablePublicTool.modifyRow(orderT, newR, 8, "TAKE_DAYS", 
					orderP.getValue("TAKE_DAYS", newR), rowParm.getValue("TAKE_DAYS"));// ����
			orderP.setData("CAT1_TYPE", newR, rowParm.getValue("CAT1_TYPE"));// �����
			TablePublicTool.modifyRow(orderT, row);
			orderP.setData("ORDER_CAT1_CODE", newR, rowParm.getValue("ORDER_CAT1_CODE"));// С����
			TablePublicTool.modifyRow(orderT, row);
			TablePublicTool.modifyRow(orderT, newR, 9, "NOTE_DATE", 
					orderP.getValue("NOTE_DATE", newR), SystemTool.getInstance().getDate());// ��¼����
		}
		int seqNo = getMaxSeqNo(triageNo);
		// ��������
		int newR = this.onNewOrder(triageNo, seqNo);
		if(newR<0){
			this.messageBox("newR<0");
			// orderT����
			this.lock();
			return;
		}
		// orderT����
		this.lock();	
	}

	/**
	 * orderT�����¼�
	 * @param row
	 */
	public void onOrderTClicked(int row){
		
	}
	
	/**
	 * ��ѯ��ͷҽ���б�
	 */
	public void onQueryOrder(){	
		if(triageNo == null 
				|| triageNo.trim().length()==0){
			this.messageBox("triageNo is null");
			return;
		}
		// ��ʼ��
		this.onClear5();
		// orderT����
		this.unlock(orderT);	
		TParm parm = new TParm();
		parm.setData("TRIAGE_NO", triageNo);
		TParm result = new TParm();
		result = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "selectOnwOrder", parm);
		if(result.getErrCode()<0){
			System.out.println("result.getErrCode()<0");
			// orderT����
			this.lock();
			return;
		}
		for(int i=0; i<result.getCount(); i++){
			result.setData("SEL_FLG", i, "N");// ѡ
		}	
		TablePublicTool.setParmValue(orderT, result);
		// ����ҽ��
		int newR = this.onNewOrder(triageNo, this.getMaxSeqNo(triageNo));
		if(newR<0){
			this.messageBox("newR<0");
			// orderT����
			this.lock();
			return;
		}
		// orderT����
		this.lock();	
	}

	public void onCheckBoxValue(Object obj){
		// orderT����
		this.unlock(orderT);	
		TTable table = (TTable)obj;		
		table.acceptText();
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();
//		System.out.println("======//////row="+row);
//		System.out.println("======//////col="+col);
		TParm orderP = table.getParmValue();
		if(orderP == null){
			this.messageBox("orderP is null");
			// orderP����
			this.lock();
			return;
		}
		if(col == 0){// ѡ
			// ����ǿ��У����ܹ�ѡ
			if((orderP.getValue("ORDER_CODE", row) == null || orderP.getValue("ORDER_CODE", row).trim().length()==0) 
					&& (orderP.getValue("SEL_FLG", row) != null && orderP.getValue("SEL_FLG", row).equals("Y")) ){
				this.messageBox("���в��ܹ�ѡ������");
				TablePublicTool.modifyRow(orderT, row, 0, "SEL_FLG", 
						orderP.getValue("SEL_FLG", row), "N");// ѡ		
				// orderP����
				this.lock();
				return;
			}	
			// ���ҽ���Ѿ�ǩ�������ܹ�ѡ
			if((orderP.getValue("SIGN_DR", row) != null && orderP.getValue("SIGN_DR", row).trim().length()>0) 
					&& (orderP.getValue("SEL_FLG", row) != null && orderP.getValue("SEL_FLG", row).equals("Y"))){
				this.messageBox("ҽ���Ѿ�ǩ�������ܹ�ѡ");
				TablePublicTool.modifyRow(orderT, row, 0, "SEL_FLG", 
						orderP.getValue("SEL_FLG", row), "N");// ѡ		
				// orderP����
				this.lock();
				return;
			}	
			// �������ϸ�����ȡ����ѡ
			// ��ͬ��������������Ѿ���ѡ����ͬ������ϸ���ȡ����ѡ
			if( (!(orderP.getValue("LINKMAIN_FLG", row) != null && orderP.getValue("LINKMAIN_FLG", row).equals("Y"))) 
					&& (orderP.getValue("LINK_NO", row) != null && orderP.getValue("LINK_NO", row).length()>0) 
					&& (!(orderP.getValue("SEL_FLG", row) != null && orderP.getValue("SEL_FLG", row).equals("Y"))) ){
				for(int i=0; i<orderP.getCount(); i++){
					if( (orderP.getValue("LINKMAIN_FLG", i) != null && orderP.getValue("LINKMAIN_FLG", i).equals("Y")) 
							&& (orderP.getValue("LINK_NO", i) != null && orderP.getValue("LINK_NO", i).length()>0) 
							&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")) ){
						this.messageBox("ͬ�����������Ѿ���ѡ����ͬ������ϸ���ȡ����ѡ");
						TablePublicTool.modifyRow(orderT, row, 0, "SEL_FLG", 
								orderP.getValue("SEL_FLG", row), "Y");// ѡ
						// orderP����
						this.lock();
						return;
					}
				}			
			}
			// ���������������ȫ������һ��
			if( (orderP.getValue("LINKMAIN_FLG", row) != null 
					&& orderP.getValue("LINKMAIN_FLG", row).equals("Y")) ){
				for(int i=0; i<orderP.getCount(); i++){
					if(orderP.getValue("LINK_NO", i) != null 
							&& orderP.getValue("LINK_NO", i).equals(orderP.getValue("LINK_NO", row))){// ͬ������
						TablePublicTool.modifyRow(orderT, i, 0, "SEL_FLG", 
								orderP.getValue("SEL_FLG", i), orderP.getValue("SEL_FLG", row));// ѡ
					}
				}		
			}					
		}
		if(col == 1){// ��
			// ѡ�У����ڵ��ڴ���&&С�ڵ�һ������������������ &&С�ڿ���&&С�ڻ�ʿ��ǩ����&&С��ҽ����ǩ����
			// ���ֳ�һ�飬�÷�����һ��
			if(orderP.getValue("LINKMAIN_FLG", row) != null 
					&& orderP.getValue("LINKMAIN_FLG", row).equals("Y")){// ѡ��
				// ���в���ѡ��
				if (orderP.getValue("ORDER_CODE", row) == null 
						|| orderP.getValue("ORDER_CODE", row).trim().length()==0) {
					this.messageBox("�뿪��ҽ��");
					TablePublicTool.modifyRow(orderT, row, 1, "LINKMAIN_FLG", 
							orderP.getValue("LINKMAIN_FLG", row), "N");// ѡ
					// orderP����
					this.lock();
					return;
				}
				// �����÷�
				if("PHA".equals(orderP.getValue("CAT1_TYPE", row))){
					TParm action = new TParm();
					action.setData("ORDER_CODE", orderP.getValue("ORDER_CODE", row));
					TParm result = OdiMainTool.getInstance().queryPhaBase(action);
					TablePublicTool.modifyRow(table, row, 6, "ROUTE_CODE", 
							orderP.getValue("ROUTE_CODE", row), result.getValue("ROUTE_CODE",0));// �÷�
				}else{
					TablePublicTool.modifyRow(table, row, 6, "ROUTE_CODE", 
							orderP.getValue("ROUTE_CODE", row), "");// �÷�
				}
				int maxLinkNo = getMaxLinkNo();
				for(int i=row; i<orderP.getCount(); i++){
					if(i>row){
						// ��������
						if(orderP.getValue("LINKMAIN_FLG", i) != null 
								&& orderP.getValue("LINKMAIN_FLG", i).equals("Y")){
							break;
						}
						// ��ʿ�Ѿ�ǩ��
						if(orderP.getValue("SIGN_NS", i) != null 
								&& orderP.getValue("SIGN_NS", i).trim().length()>0){
							break;
						}
						// ҽ���Ѿ�ǩ��
						if(orderP.getValue("SIGN_DR", i) != null 
								&& orderP.getValue("SIGN_DR", i).trim().length()>0){
							break;
						}
						if(orderP.getValue("ORDER_CODE", i) == null 
								|| orderP.getValue("ORDER_CODE", i).trim().length()==0){// ����
							break;
						}											
					}					
					TablePublicTool.modifyRow(table, i, 2, "LINK_NO", 
							orderP.getValue("LINK_NO", i), maxLinkNo);// ���
					TablePublicTool.modifyRow(table, i, 6, "ROUTE_CODE", 
							orderP.getValue("ROUTE_CODE", i), orderP.getValue("ROUTE_CODE", row));// �÷�
				}			
			}else{//  ȡ��ѡ�У���ͬ���LINK_NO��ֵΪ�գ���ͬ�������÷�
				String linkNo = orderP.getValue("LINK_NO", row);
				for(int i=row; i<orderP.getCount(); i++){
					if(orderP.getValue("LINK_NO", i) != null 
							&& orderP.getValue("LINK_NO", i).equals(linkNo)){// ��ͬ��
						TablePublicTool.modifyRow(table, i, 2, "LINK_NO", 
								orderP.getValue("LINK_NO", i), "");// ���
						if("PHA".equals(orderP.getValue("CAT1_TYPE", i))){
							TParm action = new TParm();
							action.setData("ORDER_CODE", orderP.getValue("ORDER_CODE", i));
							TParm result = OdiMainTool.getInstance().queryPhaBase(action);
							TablePublicTool.modifyRow(table, i, 6, "ROUTE_CODE", 
									orderP.getValue("ROUTE_CODE", i), result.getValue("ROUTE_CODE",0));// �÷�
						}else{
							TablePublicTool.modifyRow(table, i, 6, "ROUTE_CODE", 
									orderP.getValue("ROUTE_CODE", i), "");// �÷�
						}
					}
				}
			}
		}
		// orderP����
		this.lock();
	}

	/**
	 * orderTֵ�ı����
	 * @param obj TTableNode
	 * @return true�����ı䣻false���ı�
	 */
	public boolean onChangeOrderTValue(Object obj) {
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
		if((col == 6) 
				&& orderParm.getValue("LINKMAIN_FLG", row) != null 
				&& orderParm.getValue("LINKMAIN_FLG", row).equals("Y")){
			String linkNo = orderParm.getValue("LINK_NO", row);		
			for(int i=0; i<orderParm.getCount(); i++){
				if(orderParm.getValue("LINK_NO", i) != null 
						&& orderParm.getValue("LINK_NO", i).equals(linkNo)){
					TablePublicTool.modifyRow(table, i, 6, "ROUTE_CODE", orderParm.getValue("ROUTE_CODE", i), node.getValue());
				}
			}
		}
		return false;
	}
		
	/**
	 * ����ҽ��
	 * @param triageNo
	 * @param seqNo
	 * @return
	 */
	public int onNewOrder(String triageNo, int seqNo){
		if(triageNo == null 
				|| triageNo.trim().length()==0){
			this.messageBox("triageNo is null");
			return -1;
		}
		if(orderT == null){
			this.messageBox("orderT is null");
			return -1;
		}
		TParm orderP = orderT.getParmValue();
		if(orderP == null){
			this.messageBox("orderParm is null");
			return -1;
		}	
		int row = TablePublicTool.addRow(orderT);
		if(row == -1){
			this.messageBox("row == -1");
			return row;
		}
		// ����ʼֵ
		TablePublicTool.modifyRow(orderT, row, 0, "SEL_FLG", "", "N");// ѡ
		orderP.setData("TRIAGE_NO", row, triageNo);// ���˺�
		TablePublicTool.modifyRow(orderT, row);
		orderP.setData("SEQ_NO", row, seqNo);// ҽ�����
		TablePublicTool.modifyRow(orderT, row);
		TablePublicTool.modifyRow(orderT, row, 7, "FREQ_CODE", "", "STAT");// Ƶ�Σ�Ĭ��Ϊ����ʹ��
		TablePublicTool.modifyRow(orderT, row, 8, "TAKE_DAYS", "", 1);// ������Ĭ��Ϊ1��
		orderP.setData("OPT_USER", row, Operator.getID());// ������Ա
		TablePublicTool.modifyRow(orderT, row);
		orderP.setData("OPT_DATE", row, SystemTool.getInstance().getDate());// ����ʱ��
		TablePublicTool.modifyRow(orderT, row);
		orderP.setData("OPT_TERM", row, Operator.getIP());// �����ն�
		TablePublicTool.modifyRow(orderT, row);
		return row;
	}
	
	/**
	 * ɾ��ҽ��
	 */
	public void onDelete(){
		if(orderT == null){
			this.messageBox("orderT is null");
			return;
		}
		TParm orderP = orderT.getParmValue();
		if(orderP == null){
			this.messageBox("orderP is null");
			return;
		}
		//  orderT����
		this.unlock(orderT);
		if(deleteParm == null){
			deleteParm = new TParm();
			deleteParm.setCount(0);
		}	
		boolean flg = false;// ����Ƿ��й�ѡ��
		for(int i=0; i<orderP.getCount(); i++){
			// ��ʿ�Ѿ�ǩ����
			if( (orderP.getValue("SIGN_NS", i) != null && orderP.getValue("SIGN_NS", i).trim().length()>0) 
					&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y"))  ){
				this.messageBox("��ʿ�Ѿ�ǩ�����У�����ɾ��");
				// orderT����
				this.lock();
				return;
			}
			// ҽ���Ѿ�ǩ����
			if( (orderP.getValue("SIGN_DR", i) != null && orderP.getValue("SIGN_DR", i).trim().length()>0) 
					&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y"))  ){
				this.messageBox("ҽ���Ѿ�ǩ���ĵ��У�����ɾ��");
				// order
				this.lock();
				return;
			}
			if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				flg = true;
			}	
		}
		if(!flg){
			this.messageBox("��ѡ��ɾ���У�����");
			return;
		}
		for(int i=orderP.getCount()-1; i>=0; i--){
			if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				TablePublicTool.removeRow(orderT, i, deleteParm, 
						new String[]{"TRIAGE_NO", "SEQ_NO"}, new String[]{triageNo, orderP.getValue("SEQ_NO", i)});	
			}
		}
		// orderT����
		this.lock();	
	}

	/**
	 * ��ȡ���seqNo
	 * @param packCode
	 * @return seqNo
	 */
	public int getMaxSeqNo(String triageNo){
		String selSql = " SELECT MAX(SEQ_NO) AS SEQ_NO FROM ONW_ORDER WHERE TRIAGE_NO = '"+triageNo+"' ";
		TParm selResult = new TParm(TJDODBTool.getInstance().select(selSql));
		if(selResult.getErrCode()<0){
			return -1;
		}	
		int temp = selResult.getInt("SEQ_NO", 0);
		TParm orderP = orderT.getParmValue();
		int count = orderP.getCount();
		for(int i=0; i<count; i++){
			int seqNo = orderP.getInt("SEQ_NO", i);
			if(seqNo>temp){
				temp = seqNo;
			}
		}	
		return temp+1;
	}
	
	/**
	 * ���
	 */
	public void onClear(){
		this.onClear1();
		this.onQuery();
		this.onQueryOrder();
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
		this.onClear5();
	}

	public void onClear3(){
		TParm parmValue1 = new TParm();
		parmValue1.setCount(0);
		tablePack.setParmValue(parmValue1);
		this.packCode = null;
	}

	public void onClear4(){
		TParm parmValue2 = new TParm();
		parmValue2.setCount(0);
		tableOrder.setParmValue(parmValue2);
	}
	
	public void onClear5(){
		TParm parmValue3 = new TParm();
		parmValue3.setCount(0);
		orderT.setParmValue(parmValue3);
		deleteParm = new TParm();
		deleteParm.setCount(0);
	}
	
	/**
	 * ��ʿǩ��
	 */
	public void onSign(){
		if(triageNo == null || triageNo.trim().length()==0){
			this.messageBox("triageNo is null");
			return;
		}
		if(orderT == null){
			this.messageBox("orderT is null");
			return;
		}
		TParm orderP = orderT.getParmValue();
		if(orderP == null){
			this.messageBox("orderP is null");
			return;
		}
		boolean flg = false;// ����Ƿ��й�ѡ��
		for(int i=0; i<orderP.getCount(); i++){
			// ��ʿ�Ѿ�ǩ���Ĳ���ǩ��
			if( (orderP.getValue("SIGN_NS", i) != null && orderP.getValue("SIGN_NS", i).trim().length()>0) 
					&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y"))  ){
				this.messageBox("��ʿ�Ѿ�ǩ�����У�����ǩ��������");				
				return;
			}		
			// ҽ���Ѿ�ǩ���Ĳ���ǩ��
			if( (orderP.getValue("SIGN_DR", i) != null && orderP.getValue("SIGN_DR", i).trim().length()>0) 
					&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y"))  ){
				this.messageBox("ҽ���Ѿ�ǩ�����У�����ǩ��������");				
				return;
			}
			// ����ϸ�ѡ������û�й�ѡ
			if( (!(orderP.getValue("LINKMAIN_FLG", i) != null && orderP.getValue("LINKMAIN_FLG", i).equals("Y"))) 
					&& (orderP.getValue("LINK_NO", i) != null && orderP.getValue("LINK_NO", i).trim().length()>0) 
					&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")) ){
				for(int j=0; j<orderP.getCount(); j++){
					if( (orderP.getValue("LINKMAIN_FLG", j) != null && orderP.getValue("LINKMAIN_FLG", j).equals("Y")) 
							&& (orderP.getValue("LINK_NO", j) != null && orderP.getValue("LINK_NO", j).equals(orderP.getValue("LINK_NO", i))) 
							&& (!(orderP.getValue("SEL_FLG", j) != null && orderP.getValue("SEL_FLG", j).equals("Y"))) ){
						this.messageBox("����ϸ��ܵ���ǩ��������");
						return;
					}
				}
			}			
			if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				flg = true;
			}			
		}
		if(!flg){
			this.messageBox("��ѡ��ǩ���У�����");
			return;
		}	
		TParm parm = new TParm();
		Object obj = this.openDialog("%ROOT%\\config\\reg\\REGSavePassWordCheck.x", parm);
		if(obj != null && obj instanceof TParm){
			parm = (TParm) obj;
			// ȡ��ǩ��
			if(parm.getValue("RESULT") != null && parm.getValue("RESULT").equals("CANCLE")){
				// ȡ����ѡ
				for(int i=0; i<orderP.getCount(); i++){
					if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
						TablePublicTool.modifyRow(orderT, i, 0, "SEL_FLG", orderP.getValue("SEL_FLG", i), "N");
					}			
				}
				this.messageBox("ǩ��ʧ�ܣ�����");
				return;
			}
			for(int i=0; i<orderP.getCount(); i++){ 
				if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
					TablePublicTool.modifyRow(orderT, i, 0, "SEL_FLG", orderP.getValue("SEL_FLG", i), "N");// ȡ����ѡ 
					TablePublicTool.modifyRow(orderT, i, 11, "SIGN_NS", orderP.getValue("SIGN_NS", i), parm.getValue("USER_ID"));// ��ʿǩ�� 
				}			
			}	
			// ����
			if(this.onSave1()){
				this.messageBox("ǩ���ɹ�������");
			}else{
				this.messageBox("ǩ��ʧ�ܣ�����");
			}			
		}else{
			// ȡ����ѡ
			for(int i=0; i<orderP.getCount(); i++){
				if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
					TablePublicTool.modifyRow(orderT, i, 0, "SEL_FLG", orderP.getValue("SEL_FLG", i), "N");
				}			
			}	
			this.messageBox("ǩ��ʧ�ܣ�����");
			return;
		}
	}

	/**
	 * ��ʿȡ��ǩ��
	 */
	public void onCancelSisn(){
		if(triageNo == null 
				|| triageNo.trim().length()==0){
			this.messageBox("triageNo is null");
			return;
		}
		if(orderT == null){
			this.messageBox("orderT is null");
			return;
		}
		TParm orderP = orderT.getParmValue();
		if(orderP == null){
			this.messageBox("orderP is null");
			return;
		}
		boolean flg = false;// ����Ƿ��й�ѡ��
		for(int i=0; i<orderP.getCount(); i++){
			// ��ʿδǩ���Ĳ���ȡ��ǩ��
			if( (!(orderP.getValue("SIGN_NS", i) != null && orderP.getValue("SIGN_NS", i).trim().length()>0)) 
					&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y"))  ){
				this.messageBox("��ʿδǩ���ģ�����ȡ��ǩ��");				
				return;
			}
			// ҽ���Ѿ�ǩ�ֵĲ���ȡ��ǩ��
			if( (orderP.getValue("SIGN_DR", i) != null && orderP.getValue("SIGN_DR", i).trim().length()>0) 
					&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y"))  ){
				this.messageBox("ҽ���Ѿ�ǩ�ֵģ�����ȡ��ǩ��");				
				return;
			}
			// ����ϸ�ѡ������û�й�ѡ
			if( (!(orderP.getValue("LINKMAIN_FLG", i) != null && orderP.getValue("LINKMAIN_FLG", i).equals("Y"))) 
					&& (orderP.getValue("LINK_NO", i) != null && orderP.getValue("LINK_NO", i).trim().length()>0) 
					&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")) ){
				for(int j=0; j<orderP.getCount(); j++){
					if( (orderP.getValue("LINKMAIN_FLG", j) != null && orderP.getValue("LINKMAIN_FLG", j).equals("Y")) 
							&& (orderP.getValue("LINK_NO", j) != null && orderP.getValue("LINK_NO", j).equals(orderP.getValue("LINK_NO", i))) 
							&& (!(orderP.getValue("SEL_FLG", j) != null && orderP.getValue("SEL_FLG", j).equals("Y"))) ){
						this.messageBox("����ϸ��ܵ���ȡ��ǩ��������");
						return;
					}
				}
			}
			if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				flg = true;
			}			
		}
		if(!flg){
			this.messageBox("��ѡ��ȡ��ǩ���У�����");
			return;
		}	
		for(int i=0; i<orderP.getCount(); i++){
			if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				TablePublicTool.modifyRow(orderT, i, 0, "SEL_FLG", orderP.getValue("SEL_FLG", i), "N");// ȡ����ѡ 
				TablePublicTool.modifyRow(orderT, i, 11, "SIGN_NS", orderP.getValue("SIGN_NS", i), "");// ��ʿǩ�� 
			}
		}		
		// ����
		if(this.onSave2()){
			this.messageBox("ȡ��ǩ���ɹ�������");
		}else{
			this.messageBox("ȡ��ǩ��ʧ�ܣ�����");
		}
	}
	
	/**
	 * ����ҽ��
	 * @param flg
	 */
	public void onSave(){
		if(this.onSave1()){
			this.messageBox("����ɹ�");
		}else{
			this.messageBox("����ʧ��");
		}
	}

	/**
	 * ��ʿǩ��
	 */
	public boolean onSave1(){
		TParm saveParm = new TParm();
		if(orderT != null && orderT.getParmValue() != null){
			TParm orderP = orderT.getParmValue();
			this.checkData(orderP, "TRIAGE_NO;SEQ_NO;ORDER_CODE;ORDER_DESC;#STATUS");
			// ����ʱ���ʽ
			for(int i=0; i<orderP.getCount(); i++){
				String noteDate = StringTool.getString(TypeTool.getTimestamp(orderP.getData("NOTE_DATE", i)), "yyyy/MM/dd HH:mm:ss");
				orderP.setData("NOTE_DATE", i, noteDate);
			}			
			saveParm.setData("#ORDER", orderP.getData());
		}	
		if(deleteParm != null){
			saveParm.setData("#ORDER_DELETE", deleteParm.getData());
		}
		TParm saveResult = new TParm();
		saveResult = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "onSaveOrder1", saveParm);
		if(saveResult == null 
				||saveResult.getErrCode()<0){
			return false;
		}	
		String tempPackCode = this.packCode;// add by wangqing 2017��10��27��
		// ˢ������
		this.onClear();	
		// Ĭ�Ϲ�ѡ
		if(tempPackCode != null && tempPackCode.trim().length()>0){
			TParm packParm = tablePack.getParmValue();
			for(int i=0; i<packParm.getCount(); i++){
				if(packParm.getValue("PACK_CODE", i) != null 
						&& packParm.getValue("PACK_CODE", i).equals(tempPackCode)){
					tablePack.setSelectedRow(i);
					tablePack.setSelectedColumn(0);
					this.onTableClicked(i);
					break;
				}
			}
		}		
		return true;
	}
	
	/**
	 * ����ҽ����ȡ����ʿǩ��
	 */
	public boolean onSave2(){
		TParm saveParm = new TParm();
		if(orderT != null && orderT.getParmValue() != null){
			TParm orderP = orderT.getParmValue();
			this.checkData(orderP, "TRIAGE_NO;SEQ_NO;ORDER_CODE;ORDER_DESC;#STATUS");
			// ����ʱ���ʽ
			for(int i=0; i<orderP.getCount(); i++){
				String noteDate = StringTool.getString(TypeTool.getTimestamp(orderP.getData("NOTE_DATE", i)), "yyyy/MM/dd HH:mm:ss");
				orderP.setData("NOTE_DATE", i, noteDate);
			}			
			saveParm.setData("#ORDER", orderP.getData());
		}	
		if(deleteParm != null){
			saveParm.setData("#ORDER_DELETE", deleteParm.getData());
		}
		TParm saveResult = new TParm();
		saveResult = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "onSaveOrder2", saveParm);
		if(saveResult == null 
				||saveResult.getErrCode()<0){
			return false;
		}		
		String tempPackCode = this.packCode;// add by wangqing 2017��10��27��
		// ˢ������
		this.onClear();	
		// Ĭ�Ϲ�ѡ
		if(tempPackCode != null && tempPackCode.trim().length()>0){
			TParm packParm = tablePack.getParmValue();
			for(int i=0; i<packParm.getCount(); i++){
				if(packParm.getValue("PACK_CODE", i) != null 
						&& packParm.getValue("PACK_CODE", i).equals(tempPackCode)){
					this.onTableClicked(i);
					break;
				}
			}
		}	
		return true;
	}

	/**
	 * �õ���������
	 * @return
	 */
	public int getMaxLinkNo(){
		String selSql = " SELECT TRIAGE_NO, LINK_NO FROM ONW_ORDER WHERE TRIAGE_NO = '"+triageNo+"' ";
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
		TParm orderP = orderT.getParmValue();
		for(int i=0; i<orderP.getCount(); i++){
			if(orderP.getInt("LINK_NO", i) > linkNo){
				linkNo = orderP.getInt("LINK_NO", i);
			}
		}
		return linkNo+1;
	}

	/**
	 * orderT�����¼����༭ҽ����
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComoponent(Component com,int row,int column){	
		if(column == 3){// ҽ������
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
	 * orderT�����¼����༭ҽ���������ܷ���ֵ����
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag,Object obj){
		if (obj == null || !(obj instanceof TParm)) {
			return;
		}
		TParm parm = (TParm)obj;
		if("ONW_PACK_ORDER".equals(tag)){
			orderT.acceptText();
			int row = orderT.getSelectedRow();
			int col = orderT.getSelectedColumn();
//			System.out.println("======//////row="+row);
//			System.out.println("======//////col="+col);
			onInsertOrderList(row,parm);
		}
	}

	/**
	 * orderT�����¼����༭ҽ�������޸�ҽ��
	 * @param row
	 * @param parm
	 */
	public void onInsertOrderList(int row,TParm parm){
		TParm orderP = orderT.getParmValue();
		if(orderP == null){
			this.messageBox("orderP is null");
			return;
		}
		String orderCode = parm.getValue("ORDER_CODE");
		if(isSame(orderP, orderCode, row)){// ����ͬҽ��
			if (messageBox("������ʾ", "����ͬҽ�����Ƿ����������ҽ��?", TControl.YES_NO_OPTION) != 0) {// ������
				TablePublicTool.modifyRow(orderT, row, 3, "ORDER_DESC", 
						orderP.getValue("ORDER_DESC", row), getOrderDesc(orderP.getValue("ORDER_CODE", row)));
				return;
			}else{// ����
				insertPackOrder(row,parm);
			}
		}else{// û����ͬҽ��
			insertPackOrder(row,parm);
		}
	}

	/**
	 * orderT�����¼����༭ҽ�������ж��Ƿ�����ͬҽ��
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
	 * orderT�����¼����༭ҽ�������޸�ҽ��
	 * @param row
	 * @param parm
	 */
	public void insertPackOrder(int row,TParm parm){
		// orderT����
		this.unlock(orderT);
		TParm orderParmValue = orderT.getParmValue();
		orderParmValue.setData("ORDER_CODE", row, parm.getValue("ORDER_CODE"));// ҽ������	
		TablePublicTool.modifyRow(orderT, row);
		TablePublicTool.modifyRow(orderT, row, 3, "ORDER_DESC", 
				orderParmValue.getValue("ORDER_DESC", row), parm.getValue("ORDER_DESC")+ " "+parm.getValue("SPECIFICATION"));// ҽ������	
		orderParmValue.setData("CAT1_TYPE", row, parm.getValue("CAT1_TYPE"));// ҽ��������
		TablePublicTool.modifyRow(orderT, row);
		orderParmValue.setData("ORDER_CAT1_CODE", row, parm.getValue("ORDER_CAT1_CODE"));// ҽ��С����
		TablePublicTool.modifyRow(orderT, row);	
		TablePublicTool.modifyRow(orderT, row, 9, "NOTE_DATE", 
				orderParmValue.getValue("NOTE_DATE", row), SystemTool.getInstance().getDate());// ����

		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			TParm action = new TParm();
			action.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
			TParm result = OdiMainTool.getInstance().queryPhaBase(action);
			TablePublicTool.modifyRow(orderT, row, 4, "MEDI_QTY", 
					orderParmValue.getValue("MEDI_QTY", row), result.getValue("MEDI_QTY", 0));// ����
			TablePublicTool.modifyRow(orderT, row, 5, "MEDI_UNIT", 
					orderParmValue.getValue("MEDI_UNIT", row), result.getValue("MEDI_UNIT", 0));// ��λ
			TablePublicTool.modifyRow(orderT, row, 6, "ROUTE_CODE", 
					orderParmValue.getValue("ROUTE_CODE", row), result.getValue("ROUTE_CODE", 0));// �÷�
		}else{
			TablePublicTool.modifyRow(orderT, row, 4, "MEDI_QTY", 
					orderParmValue.getValue("MEDI_QTY", row), 1);// ����
			TablePublicTool.modifyRow(orderT, row, 5, "MEDI_UNIT", 
					orderParmValue.getValue("MEDI_UNIT", row), parm.getData("UNIT_CODE"));// ��λ
			TablePublicTool.modifyRow(orderT, row, 6, "ROUTE_CODE", 
					orderParmValue.getValue("ROUTE_CODE", row), "");// �÷�
		}		
		if(!isNew(orderParmValue)){
			int seqNo = getMaxSeqNo(triageNo);
			if(seqNo<0){
				this.messageBox("err seqNo");
				// tableOrder����
				this.lock();
				return;
			}			
			int newR = onNewOrder(triageNo, seqNo);
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
	 * ����
	 * @param table
	 */
	public void unlock(TTable table){
		table.setLockCellMap(new HashMap());
	}
	
	/**
	 * orderT����
	 */
	public void lock(){
		TParm parm = orderT.getParmValue();
		if(parm == null){
			this.messageBox("parm is null");
			return;
		}
		for(int i=0; i<parm.getCount(); i++){
			// ��ҽ�����У� ҽ�����Ʋ��ɱ༭
			if(parm.getValue("ORDER_CODE", i) != null 
					&& parm.getValue("ORDER_CODE", i).trim().length()>0){
				orderT.setLockCell(i, 3, true);
			}
			// ����ϸ� �÷����ɱ༭
			if((!(parm.getValue("LINKMAIN_FLG", i) != null 
					&& parm.getValue("LINKMAIN_FLG", i).equals("Y"))) 
					&& parm.getValue("LINK_NO", i) != null 
					&& parm.getValue("LINK_NO", i).trim().length()>0 ){
				orderT.setLockCell(i, 6, true);
			}	
			// ��ʿ��ǩ���ģ�����ѡ��򣬶������Ա༭
			if(parm.getValue("SIGN_NS", i) != null 
					&& parm.getValue("SIGN_NS", i).trim().length()>0){
				for(int j=1; j<orderT.getColumnCount(); j++){
					orderT.setLockCell(i, j, true);
				}
			}
			// ҽ����ǩ���ģ��������Ա༭
			if(parm.getValue("SIGN_DR", i) != null 
					&& parm.getValue("SIGN_DR", i).trim().length()>0){
				for(int j=0; j<orderT.getColumnCount(); j++){
					orderT.setLockCell(i, j, true);
				}
			}
			// ���в����Թ�ѡ
			if(parm.getValue("ORDER_CODE", i) == null 
					|| parm.getValue("ORDER_CODE", i).trim().length()==0){
				orderT.setLockCell(i, 0, true);
			}		
		}	
	}

	/**
	 * 
	 * У��ҽ���Ƿ�������
	 * @param orderCode
	 * @return
	 */
	public boolean checkActive(String orderCode){
		String sql = " SELECT A.ORDER_CODE, A.ORDER_DESC, A.ACTIVE_FLG, B.START_DATE, B.END_DATE "
				+ " FROM SYS_FEE A, SYS_FEE_HISTORY B "
				+ " WHERE A.ORDER_CODE='"+orderCode+"' AND A.ACTIVE_FLG = 'Y' "
						+ " AND A.ORDER_CODE = B.ORDER_CODE "
						+ " AND TO_DATE(B.START_DATE, 'YYYY/MM/DD HH24:MI:SS') < SYSDATE "
						+ " AND TO_DATE(B.END_DATE, 'YYYY/MM/DD HH24:MI:SS') > SYSDATE "
						+ " AND B.ACTIVE_FLG = 'Y' ";
		System.out.println("======//////sql="+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			return false;			
		}
		if(result.getCount()<=0){
			return false;
		}
		return result.getBoolean("ACTIVE_FLG", 0);
	}
	
	/**
	 * ȫѡ
	 */
	public void onAllSelect(){
		if(orderT == null){
			return;
		}
		TParm parm = orderT.getParmValue();
		if(parm == null){
			return;
		}
		Map map = orderT.getLockCellMap();	
		for(int i=0; i<parm.getCount(); i++){
			if(map != null && map.get(i+":0") != null && (Boolean) map.get(i+":0")){
				continue;
			}
			TablePublicTool.modifyRow(orderT, i, 0, "SEL_FLG", parm.getBoolean("SEL_FLG", i), !parm.getBoolean("SEL_FLG", i));
		}
	}
	
	
	/**
	 * ����
	 */
	public void onTest(){
		Map map = orderT.getLockCellMap();
		System.out.println("======lock:"+map.get("6:0"));
		Boolean boolean1 = (Boolean) map.get("6:0");
	}
	
	
	
}
