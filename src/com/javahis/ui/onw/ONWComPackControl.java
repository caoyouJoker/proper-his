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
 * <p>口头医嘱套餐维护界面</p>
 * 
 * @author wangqing 20170831
 * 
 * <p>update时需要保证所有字段不为null</p>
 * 
 * <p>注意table.acceptText的正确使用</p>
 * 
 * <p>常用的事件：点击、编辑、值改变、checkBox点击；编辑完后返回值、checkBox点击需要acceptText</p>
 *
 */
public class ONWComPackControl extends TControl {
	/**
	 * 套餐table
	 */
	private TTable tablePack;
	/**
	 * 医嘱table
	 */
	private TTable tableOrder;
	/**
	 * 保存tablePack要删除的数据
	 */
	private TParm deletePackParm;
	/**
	 * 保存tableOrder要删除的数据
	 */
	private TParm deleteOrderParm;
	/**
	 * 套餐号
	 */
	private String packCode;

	/**
	 * 初始化
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
		// 初始化控件值
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
	 * 新增套餐
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
		String rxType = this.getValueString("RX_TYPE");// 套餐类别
		String deptCode = this.getValueString("DEPT_CODE");// 科室
		if(rxType.length()<=0){
			this.messageBox("请选择套餐类别！");
			return;
		}
		if(deptCode.length()<=0){
			this.messageBox("请选择科室！");
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
		// 赋初始值
		packParm.setData("PACK_CODE", row, packCode);// 套餐号
		TablePublicTool.modifyRow(tablePack, row);
		TablePublicTool.modifyRow(tablePack, row, 0, "RX_TYPE", "", rxType);// 套参类型
		TablePublicTool.modifyRow(tablePack, row, 2, "DEPT_CODE", "", deptCode);// 科室
		TablePublicTool.modifyRow(tablePack, row, 3, "OPT_USER", "", Operator.getID());// 操作人员
		TablePublicTool.modifyRow(tablePack, row, 4, "OPT_DATE", "", SystemTool.getInstance().getDate());// 操作时间
		TablePublicTool.modifyRow(tablePack, row, 5, "OPT_TERM", "", Operator.getIP());// 操作终端
		// 默认选中
		tablePack.setSelectedRow(row);
		onTableClicked(row);
	}

	/**
	 * 查询套餐
	 */
	public void onQuery(){
		// 初始化
		this.onClear2();
		this.unlock(tableOrder);
		String rxType = this.getValueString("RX_TYPE");// 套餐类别
		if(rxType.length()<=0){
			this.messageBox("请选择套餐类别！");
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
//		// 默认勾选第一行	
//		if(selectResult.getCount()>0){
//			tablePack.setSelectedRow(0);
//			tablePack.setSelectedColumn(0);
//			onTableClicked(0);
//		}
	}

	/**
	 * 删除套餐
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
			this.messageBox("请选择删除行");
			return;
		}				
		String packCode = packParmValue.getValue("PACK_CODE", selectedRow);
		if(packCode == null || packCode.trim().length()==0){
			this.messageBox("packCode is null");
			return;
		}
		// tableOrder解锁
		this.unlock(tableOrder);
		// tableOrder清空
		this.onClear4();		
		if(deletePackParm == null){
			deletePackParm = new TParm();
			deletePackParm.setCount(0);
		}
		TablePublicTool.removeRow(tablePack, selectedRow, deletePackParm, new String[]{"PACK_CODE"}, new String[]{packCode});
	}

	/**
	 * 新增医嘱
	 * @param packCode 套餐类型
	 * @param seqNo 医嘱序号
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
		// 赋初始值
		orderParm.setData("PACK_CODE", row, packCode);// 套餐类型
		TablePublicTool.modifyRow(tableOrder, row);
		orderParm.setData("SEQ_NO", row, seqNo);// 医嘱序号
		TablePublicTool.modifyRow(tableOrder, row);
		TablePublicTool.modifyRow(tableOrder, row, 6, "FREQ_CODE", "", "STAT");// 频次，默认为立即使用
		TablePublicTool.modifyRow(tableOrder, row, 7, "TAKE_DAYS", "", 1);// 天数，默认为1天
		TablePublicTool.modifyRow(tableOrder, row, 8, "OPT_USER", "", Operator.getID());// 操作人员
		TablePublicTool.modifyRow(tableOrder, row, 9, "OPT_DATE", "", SystemTool.getInstance().getDate());// 操作时间
		TablePublicTool.modifyRow(tableOrder, row, 10, "OPT_TERM", "", Operator.getIP());// 操作终端
		return row;
	}

	/**
	 * 删除医嘱
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
			this.messageBox("请选择删除行");
			return;
		}
		// 空行不能删除
		if(orderParmValue.getValue("ORDER_CODE", selectedRow) == null 
				|| orderParmValue.getValue("ORDER_CODE", selectedRow).trim().length()==0){
			return;
		}	
		// tableOrder解锁
		this.unlock(tableOrder);
		if(deleteOrderParm == null){
			deleteOrderParm = new TParm();
			deleteOrderParm.setCount(0);
		}	
				
		String packCode;
		String seqNo;		
		// 如果是连嘱主项，删除同组的医嘱（空行除外）
		if(orderParmValue.getValue("LINKMAIN_FLG", selectedRow) != null 
				&& orderParmValue.getValue("LINKMAIN_FLG", selectedRow).equals("Y")){
			String linkNo = orderParmValue.getValue("LINK_NO", selectedRow);
			// 循环删除同组
			for(int i=orderParmValue.getCount()-1; i>=0; i--){// 从大行号开始删除
				// 空行不能删除
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
		}else{// 非连嘱主项
			packCode = orderParmValue.getValue("PACK_CODE", selectedRow);
			seqNo = orderParmValue.getValue("SEQ_NO", selectedRow);
			TablePublicTool.removeRow(tableOrder, selectedRow, deleteOrderParm, new String[]{"PACK_CODE", "SEQ_NO"}, new String[]{packCode, seqNo});
		}
		// tableOrder加锁
		this.lock();
	}

	/**
	 * 保存
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
		// 保存数据
		TParm saveResult = new TParm();
		saveResult = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "onSave", saveParm);
		if(saveResult == null 
				|| saveResult.getErrCode()<0){
			this.messageBox("保存失败！！！");
			return;
		}
		this.messageBox("保存成功！！！");	
		// 刷新数据
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
	 * 清空
	 */
	public void onClear(){
		this.onClear1();
		this.onQuery();
	}
	
	/**
	 * 清空1
	 */
	public void onClear1(){
		this.clearValue("RX_TYPE;DEPT_CODE");
		this.setValue("RX_TYPE", "0");
		this.setValue("DEPT_CODE", "0202");
		this.onClear2();
	}

	/**
	 * 清空2
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
	 * TABLE_PACK单击事件
	 * @param row 单击的行
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
		// tableOrder解锁
		this.unlock(tableOrder);
		// tableOrder清空
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
		// 新增空行
		int newR = onNewOrder(packCode, seqNo);	
		if(newR<0){
			this.messageBox("newR<0");
			// tableOrder加锁
			this.lock();
			return;
		}
		// tableOrder加锁
		this.lock();
	}

	/**
	 * TABLE_ORDER单击事件
	 * @param row
	 */
	public void onTableOrderClicked(int row) {
	}

	/**
	 * TABLE_PACK值改变监听
	 * @param obj TTableNode
	 * @return true，不改变；false，改变
	 */
	public boolean onChangeTablePackValue(Object obj) {
		TTableNode node = (TTableNode) obj;
		if (node == null){ 
			return true;	
		}
		int row = node.getRow();// 行号		
		int col = node.getColumn();// 列号
		TTable table = node.getTable(); // 数据表
		//		String columnName = node.getTable().getParmMap(col);// 列名
		if(col != 1){// 非套餐名称列
			return true;
		}
		TablePublicTool.modifyRow(table, row);
		return false;
	}

	/**
	 * TABLE_ORDER值改变监听
	 * @param obj TTableNode
	 * @return true，不改变；false，改变
	 */
	public boolean onChangeTableOrderValue(Object obj) {
		TTableNode node = (TTableNode) obj;
		if (node == null){ 
			return true;	
		}	
		int row = node.getRow();// 行号		
		int col = node.getColumn();// 列号
		TTable table = node.getTable(); // 数据表
		//		String columnName = node.getTable().getParmMap(col);// 列名
		TablePublicTool.modifyRow(table, row);
		// 如果是列为用法列，且是连嘱主项，修改同组的用法	
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
	 * TABLE_ORDER监听事件（编辑医嘱）
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
			parm.setData("ONW_PACK_ORDER", "A");// 全部医嘱
			//设置弹出菜单
			textFilter.setPopupMenuParameter("ONW_PACK_ORDER", getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
			//定义接受返回值方法
			textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		}		
	}

	/**
	 * TABLE_ORDER监听事件（编辑医嘱），接受返回值方法
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
	 * TABLE_ORDER监听事件（编辑医嘱），修改医嘱
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
		if(isSame(orderParm, orderCode, row)){// 有相同医嘱
			if (messageBox("友情提示", "有相同医嘱，是否继续开立此医嘱?", TControl.YES_NO_OPTION) != 0) {// 不开立
				TablePublicTool.modifyRow(tableOrder, row, 2, "ORDER_DESC", 
						orderParm.getValue("ORDER_DESC", row), getOrderDesc(orderParm.getValue("ORDER_CODE", row)));	
				return;
			}else{// 开立
				insertPackOrder(row,parm);
			}
		}else{// 没有相同医嘱
			insertPackOrder(row,parm);
		}
	}

	/**
	 * TABLE_ORDER监听事件（编辑医嘱），判断是否有相同医嘱
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
	 * TABLE_ORDER监听事件（编辑医嘱），修改医嘱
	 * @param row
	 * @param parm
	 */
	public void insertPackOrder(int row,TParm parm){
		// tableOrder解锁
		this.unlock(tableOrder);		
		TParm orderParmValue = tableOrder.getParmValue();
		orderParmValue.setData("ORDER_CODE", row, parm.getValue("ORDER_CODE"));// 医嘱代码	
		TablePublicTool.modifyRow(tableOrder, row);
		TablePublicTool.modifyRow(tableOrder, row, 2, "ORDER_DESC", 
				orderParmValue.getValue("ORDER_DESC", row), parm.getValue("ORDER_DESC")+ " "+parm.getValue("SPECIFICATION"));// 医嘱描述	
		orderParmValue.setData("CAT1_TYPE", row, parm.getValue("CAT1_TYPE"));// 医嘱大类型
		TablePublicTool.modifyRow(tableOrder, row);
		orderParmValue.setData("ORDER_CAT1_CODE", row, parm.getValue("ORDER_CAT1_CODE"));// 医嘱小类型
		TablePublicTool.modifyRow(tableOrder, row);	
		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			TParm action = new TParm();
			action.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
			TParm result = OdiMainTool.getInstance().queryPhaBase(action);
			TablePublicTool.modifyRow(tableOrder, row, 3, "MEDI_QTY", 
					orderParmValue.getValue("MEDI_QTY", row), result.getValue("MEDI_QTY", 0));// 数量
			TablePublicTool.modifyRow(tableOrder, row, 4, "MEDI_UNIT", 
					orderParmValue.getValue("MEDI_UNIT", row), result.getValue("MEDI_UNIT", 0));// 单位
			TablePublicTool.modifyRow(tableOrder, row, 5, "ROUTE_CODE", 
					orderParmValue.getValue("ROUTE_CODE", row), result.getValue("ROUTE_CODE", 0));// 用法
		}else{
			TablePublicTool.modifyRow(tableOrder, row, 3, "MEDI_QTY", 
					orderParmValue.getValue("MEDI_QTY", row), 1);// 数量
			TablePublicTool.modifyRow(tableOrder, row, 4, "MEDI_UNIT", 
					orderParmValue.getValue("MEDI_UNIT", row), parm.getData("UNIT_CODE"));// 单位
			TablePublicTool.modifyRow(tableOrder, row, 5, "ROUTE_CODE", 
					orderParmValue.getValue("ROUTE_CODE", row), "");// 用法
		}
		if(!isNew(orderParmValue)){
			int seqNo = getMaxSeqNo(packCode);
			if(seqNo<0){
				this.messageBox("err seqNo");
				// tableOrder加锁
				this.lock();
				return;
			}			
			int newR = onNewOrder(packCode, seqNo);
			if(newR<0){
				this.messageBox("newR<0");
				// tableOrder加锁
				this.lock();
				return;
			}
		}
		// tableOrder加锁
		this.lock();	
	}

	/**
	 * 判断是否有未编辑的医嘱
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
	 * 获取最大seqNo
	 * @param packCode
	 * @return 最大seqNo
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
	 * TABLE_ORDER监听事件（点击连）
	 * @param obj
	 */
	public void onCheckBoxValue(Object obj){
		// tableOrder解锁
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
			// tableOrder加锁
			this.lock();
			return;
		}		
		if(col == 0){// 连
			// 选中，大于等于此行&&小于第一个遇到的连嘱主项行 &&小于空行
			// 划分成一组，用法保持一致
			if("Y".equals(orderParmValue.getValue("LINKMAIN_FLG", row))){
				// 空行不能选中
				if (orderParmValue.getValue("ORDER_CODE", row) == null 
						|| orderParmValue.getValue("ORDER_CODE", row).length() == 0) {
					this.messageBox("请开立医嘱");
					TablePublicTool.modifyRow(table, row, 0, "LINKMAIN_FLG", 
							orderParmValue.getValue("LINKMAIN_FLG", row), "N");// 连
					// tableOrder加锁
					this.lock();
					return;
				}
				// 更新用法
				if("PHA".equals(orderParmValue.getValue("CAT1_TYPE", row))){
					TParm action = new TParm();
					action.setData("ORDER_CODE", orderParmValue.getValue("ORDER_CODE", row));
					TParm result = OdiMainTool.getInstance().queryPhaBase(action);
					TablePublicTool.modifyRow(table, row, 5, "ROUTE_CODE", 
							orderParmValue.getValue("ROUTE_CODE", row), result.getValue("ROUTE_CODE",0));// 用法
				}else{
					TablePublicTool.modifyRow(table, row, 5, "ROUTE_CODE", 
							orderParmValue.getValue("ROUTE_CODE", row), "");// 用法
				}			
				int maxLinkNo = getMaxLinkNo();
				for(int i=row; i<orderParmValue.getCount(); i++){					
					if(i>row){
						if(orderParmValue.getValue("LINKMAIN_FLG", i) != null 
								&& orderParmValue.getValue("LINKMAIN_FLG", i).equals("Y")){// 连嘱主项
							break;
						}	
						if(orderParmValue.getValue("ORDER_CODE", i) == null 
								|| orderParmValue.getValue("ORDER_CODE", i).trim().length()==0){// 空行
							break;
						}
					}
					TablePublicTool.modifyRow(table, i, 1, "LINK_NO", 
							orderParmValue.getValue("LINK_NO", i), maxLinkNo);// 组号
					TablePublicTool.modifyRow(table, i, 5, "ROUTE_CODE", 
							orderParmValue.getValue("ROUTE_CODE", i), orderParmValue.getValue("ROUTE_CODE", row));// 用法
				}
			}else{// 取消选中，相同组的LINK_NO赋值为空，相同组重设用法
				String linkNo = orderParmValue.getValue("LINK_NO", row);			
				for(int i=row; i<orderParmValue.getCount(); i++){
					if(orderParmValue.getValue("LINK_NO", i) != null 
							&& orderParmValue.getValue("LINK_NO", i).equals(linkNo)){
						TablePublicTool.modifyRow(table, i, 1, "LINK_NO", 
								orderParmValue.getValue("LINK_NO", i), "");// 组号
						if("PHA".equals(orderParmValue.getValue("CAT1_TYPE", i))){
							TParm action = new TParm();
							action.setData("ORDER_CODE", orderParmValue.getValue("ORDER_CODE", i));
							TParm result = OdiMainTool.getInstance().queryPhaBase(action);
							TablePublicTool.modifyRow(table, i, 5, "ROUTE_CODE", 
									orderParmValue.getValue("ROUTE_CODE", i), result.getValue("ROUTE_CODE",0));// 用法
						}else{
							TablePublicTool.modifyRow(table, i, 5, "ROUTE_CODE", 
									orderParmValue.getValue("ROUTE_CODE", i), "");// 用法
						}
					}
				}
			}
		}	
		// tableOrder加锁
		this.lock();
	}

	/**
	 * 拿到最大连结号
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
	 * 校验数据
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
	 * 校验数据
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
	 * 获取医嘱描述
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
	 * 解锁
	 * @param table
	 */
	public void unlock(TTable table){
		table.setLockCellMap(new HashMap());
	}

	/**
	 * tableOrder加锁
	 */
	public void lock(){
		TParm orderParm = tableOrder.getParmValue();
		if(orderParm == null){
			this.messageBox("orderParm is null");
			return;
		}
		for(int i=0; i<orderParm.getCount(); i++){
			// 有医嘱的行， 医嘱名称不可编辑
			if(orderParm.getValue("ORDER_CODE", i) != null 
					&& orderParm.getValue("ORDER_CODE", i).trim().length()>0){
				tableOrder.setLockCell(i, 2, true);
			}
			// 连嘱细项， 用法不可编辑
			if((!(orderParm.getValue("LINKMAIN_FLG", i) != null 
					&& orderParm.getValue("LINKMAIN_FLG", i).equals("Y"))) 
					&& tableOrder.getParmValue().getValue("LINK_NO", i) != null 
					&& tableOrder.getParmValue().getValue("LINK_NO", i).trim().length()>0 ){
				tableOrder.setLockCell(i, 5, true);
			}		
		}
	}
	
	/**
	 * 测试
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
