package com.javahis.ui.odi;

import java.awt.Component;
import java.util.Vector;

import jdo.odi.ODIInfecPackTool;
import jdo.odi.OdiMainTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:感染医嘱套餐
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author SHIBL
 * @version 1.0
 */
public class ODIInfecPackICDControl extends TControl {
	
	TTable tablem;// 主表
	TTable tableorder;// 医嘱表 ODIInf

	public void onInit() {
		super.onInit();
		this.setValue("SMEALTYPE", "OP");
		onClickonchageButton();
		tablem = this.getTTable("TABLEM");
		tableorder = this.getTTable("TABLEORDER");
		getTTable("TABLEORDER").addEventListener(
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComoponent");
		// 连接医嘱监听事件
		getTTable("TABLEORDER").addEventListener(TTableEvent.CHECK_BOX_CLICKED,
				this, "onCheckBoxValue");
		
		 addEventListener("TABLEORDER->" + TTableEvent.CHANGE_VALUE,"onTableValueChange");
	}
	/**
	 * 改变组值的事件 add caoyong 20131112
	 * @param obj
	 */
	public void onTableValueChange(Object obj) {
		 TTableNode node = (TTableNode)obj;
		 
	        onTableValueChange0(node);

		
	}
	
	/**
	 * 组改变值事件 add caoyong 20131112
	 * @param node
	 */
    public void onTableValueChange0 (TTableNode node){
    	 if(node.getColumn() != 1 ){
             return;
    	 }
        boolean flag=false;
        tableorder.acceptText();
    	int col = tableorder.getSelectedColumn();
		String columnName = getFactColumnName("TABLEORDER", col);
		int row = node.getRow();
		String linkNO="" + node.getValue();//取得改变值
		if("LINK_NO".equals(columnName)){
			
			//连被选中后比较组如果其他的连被选中并且组相等则提醒校验
			if("Y".equals(tableorder.getItemString(row, "LINKMAIN_FLG"))){
				
			flag=this.getCompare(linkNO,row);//比较组号
			
			if(flag){
				this.messageBox("连的组号有重复，请重新操作！");
				tableorder.setItem(row, "LINKMAIN_FLG", "N");
				return;
			}else{
				getTresultQ(linkNO,row);
			}
			}else{//改变组织查询和连相同的组号然后把用量单位等变成连的
				getTresult(linkNO,row);
			}
			}
      }
	/**
	 *ORDER值改变事件
	 */
	public void onCheckBoxValue(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int col = table.getSelectedColumn();
		String columnName = getFactColumnName("TABLEORDER", col);
		int row = table.getSelectedRow();
		// 连接医嘱
		if ("LINKMAIN_FLG".equals(columnName)) {
			if ("Y".equals(table.getItemString(row, "LINKMAIN_FLG"))) {
				if (table.getItemString(row, "ORDER_CODE").length() == 0) {
					// 请开立医嘱
					this.messageBox("请开立医嘱");
					table.setItem(row, "LINKMAIN_FLG", "N");
					return;
				}
				// 查询最大连结号
				int maxLinkNo = getMaxLinkNo();
				table.setItem(row, "LINK_NO", maxLinkNo);
			} else {
				table.setItem(row, "LINK_NO", "");
				if ("PHA".equals(table.getItemString(row, "CAT1_TYPE"))) {
					// 得到PHA_BASE数据
					TParm action = new TParm();
					action.setData("ORDER_CODE", table.getItemString(row,
							"ORDER_CODE"));
					TParm result = OdiMainTool.getInstance().queryPhaBase(
							action);
					table.setItem(row, "ROUTE_CODE", result.getValue(
							"ROUTE_CODE", 0));
					table.setItem(row, "FREQ_CODE", result.getValue(
							"FREQ_CODE", 0));
				} else {
					table.setItem(row, "ROUTE_CODE", "");
					table.setItem(row, "FREQ_CODE", "");
				}
			}
		}
	}

	/**
	 * 拿到最大连结号
	 * 
	 * @param table
	 *            TTable
	 */
	public int getMaxLinkNo() {
		tableorder.acceptText();
		int result = 0;
		for (int i = 0; i < tableorder.getRowCount(); i++) {
			if (tableorder.getItemString(i, "ORDER_CODE").equals(""))
				continue;
			if (TypeTool.getInt(tableorder.getItemData(i, "LINK_NO")) > result){
				result = TypeTool.getInt(tableorder.getItemData(i, "LINK_NO"));
			}
		}
		return result == 0 ? 1 : result + 1;
	}

	/**
	 * 返回实际列名
	 * 
	 * @param column
	 *            String
	 * @param column
	 *            int
	 * @return String
	 */
	public String getFactColumnName(String tableTag, int column) {
		int col = this.getThisColumnIndex(column, tableTag);
		return this.getTTable(tableTag).getDataStoreColumnName(col);
	}

	/**
	 * 拿到更变之前的列号
	 * 
	 * @param column
	 *            int
	 * @return int
	 */
	public int getThisColumnIndex(int column, String table) {
		return this.getTTable(table).getColumnModel().getColumnIndex(column);
	}

	/**
	 * 当TABLE创建编辑控件时
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponent(Component com, int row, int column) {
		// 拿到列名
		String columnName = this.getFactColumnName("TABLEORDER", column);
		if (!columnName.contains("ORDER_DESC"))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// 设置弹出菜单
		textFilter.setPopupMenuParameter("ITEM", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");

	}

	/**
	 * 返回值事件
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		TParm parm = (TParm) obj;
		if ("ITEM".equals(tag)) {
			this.getTTable("TABLEORDER").acceptText();
			int selRow = this.getTTable("TABLEORDER").getSelectedRow();
			// 插入行
			onInsertOrderList(selRow, parm);
		}
	}

	/**
	 * 插入细项
	 * 
	 * @param type
	 *            int
	 */
	public void onInsertOrderList(int row, TParm parm) {
		tableorder.acceptText();
		String orderCode = parm.getValue("ORDER_CODE");
		TParm parmRow = tablem.getParmValue().getRow(tablem.getSelectedRow());
		if (this.getValue("SMEALTYPE").equals("OP")) {//手术操作
			callFunction("UI|PHA_PREVENCODE|setEnabled", true);
		}else{
			callFunction("UI|PHA_PREVENCODE|setEnabled", false);
		}
		String packcode = parmRow.getValue("PACK_CODE");
		String sql = "SELECT MAX(SEQ_NO) AS SEQ_NO FROM ODI_INFEC_PACKORDER " + "WHERE PACK_CODE='"
			+ packcode + "'";
		TParm parmorder = new TParm(TJDODBTool.getInstance().select(sql));
		/*int maxSeq=0;
		if (parmorder.getCount()>0) {
			maxSeq=parmorder.getInt("SEQ_NO",0);
		}*/
		
		 int maxSeq=0;
	     maxSeq=this.getSeqNO();//add caoyong 20131107
		if (!isAnti(orderCode)) {
			this.messageBox("此药品为非抗生素,禁止开立");
			//tableorder.setItem(row, "ORDER_DESC", "");
			this.addItemRow(row); //add caoyong 20140225  更换药品时，所选药品停用或者不是抗菌药物时，数据全部清空
			return;
		} else if (this.isSame(tableorder, orderCode, row)) {
			if (messageBox("提示信息", "有相同医嘱是否开立此医嘱?", this.YES_NO_OPTION) != 0) {
				//tableorder.setItem(row, "ORDER_DESC", "");
				this.addItemRow(row); //add caoyong 20140225  更换药品时，所选药品停用或者不是抗菌药物时，数据全部清空
				return;
			} else {
				insertPackOrder(row, parm,maxSeq);
				if (row == tableorder.getRowCount() - 1)
					this.insertOrderRow();
			}
		} else {
			insertPackOrder(row, parm,maxSeq);
			if (row == tableorder.getRowCount() - 1)
				this.insertOrderRow();
		}
	}
	
	/**
	 * 序号 add caoyong 20131112
	 * @return
	 */
	public int getSeqNO(){
		  int count=0; //序号 
		  count=tableorder.getRowCount();
		  if(tableorder.getRowCount()==1){
			  return 1;
		  }else{
			 return Integer.parseInt(tableorder.getItemString((count-2),"SEQ_NO"))+1 ;
		  }
     }

	/**
	 */
	private boolean isAnti(String orderCode) {
		TParm actionDs = new TParm();
		actionDs.setData("ORDER_CODE", orderCode);
		TParm resultDs = OdiMainTool.getInstance().queryPhaBase(actionDs);
		if (resultDs.getValue("ANTIBIOTIC_CODE", 0).equals("")) {
			return false;
		} else {
			return true;
		}
	}

	private void insertPackOrder(int row, TParm parm,int maxSeq) {
		// TODO Auto-generated method stub
		tableorder.acceptText();
		if (row > 0) {
			String blinkNo = tableorder.getItemString(row - 1, "LINK_NO");
			if (blinkNo.length() != 0) {
				tableorder.setItem(row, "LINK_NO", blinkNo);
			} else {
				tableorder.setItem(row, "LINK_NO", "");
			}
		}
		tableorder.setItem(row, "SEQ_NO", maxSeq);
		tableorder.setItem(row, "ORDER_CODE", parm.getData("ORDER_CODE"));
		tableorder.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
		tableorder.setItem(row, "CAT1_TYPE", parm.getData("CAT1_TYPE"));
		tableorder.setItem(row, "ORDER_CAT1_CODE", parm
				.getData("ORDER_CAT1_CODE"));
		tableorder.setItem(row, "ORDER_DESC", parm.getData("ORDER_DESC"));
		tableorder.setItem(row, "TRADE_ENG_DESC", parm
				.getData("TRADE_ENG_DESC"));
		tableorder.setItem(row, "SPECIFICATION", parm.getData("SPECIFICATION"));
		tableorder.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
		tableorder.setItem(row, "TAKE_DAYS", 1);
		// 得到PHA_BASE数据
		TParm actionDs = new TParm();
		actionDs.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
		TParm resultDs = OdiMainTool.getInstance().queryPhaBase(actionDs);
		tableorder.setItem(row, "MEDI_QTY", resultDs.getData("MEDI_QTY", 0));
		tableorder.setItem(row, "MEDI_UNIT", resultDs.getData("MEDI_UNIT", 0));
		if (row > 0) {
			String ROUTE_CODE = tableorder.getItemString(row - 1, "ROUTE_CODE");
			if (ROUTE_CODE.length() != 0) {
				tableorder.setItem(row, "ROUTE_CODE", ROUTE_CODE);
			} else {
				tableorder.setItem(row, "ROUTE_CODE", resultDs.getData(
						"ROUTE_CODE", 0));
			}
			String FREQ_CODE = tableorder.getItemString(row - 1, "FREQ_CODE");
			if (FREQ_CODE.length() != 0) {
				tableorder.setItem(row, "FREQ_CODE", FREQ_CODE);
			} else {
				tableorder.setItem(row, "FREQ_CODE", resultDs.getData(
						"FREQ_CODE", 0));
			}
		} else {
			tableorder.setItem(row, "ROUTE_CODE", resultDs.getData(
					"ROUTE_CODE", 0));
//			tableorder.setItem(row, "FREQ_CODE", "");
			tableorder.setItem(row, "FREQ_CODE",resultDs.getData(
					"FREQ_CODE", 0));
		}
	}

	/**
	 * 
	 * @param table
	 * @param orderCode
	 * @param row
	 * @return
	 */
	public boolean isSame(TTable table, String orderCode, int row) {
		table.acceptText();
		for (int i = 0; i < table.getRowCount(); i++) {
			if (i == row)
				continue;
			String orderCodeC = table.getItemString(i, "ORDER_CODE");
			if (orderCodeC.equals(orderCode))
				return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public void onDeleteRowicd() {
		TTable table = getTTable("TABLEICD");
		int row = table.getTable().getSelectedRow();
		table.removeRow(row);
	}

	public void onDeleteRoworder() {
		TTable table = getTTable("TABLEORDER");
		// modify caoyong 20140225 空行不可以被删除 已经删除数据库数据 start
		int row = table.getSelectedRow();
		TParm parma=new TParm();
		TParm result=new TParm();
		//TParm parm=table.getParmValue().getRow(row);
		parma.addData("PACK_CODE", this.getValue("PACK_CODE"));
		parma.addData("SEQ_NO", table.getItemData(row,"SEQ_NO"));
		parma.addData("ORDER_CODE", table.getItemData(row, "ORDER_CODE"));
		result=ODIInfecPackTool.getInstance().onDeleteIcd(parma);
		if (result.getErrCode() < 0) {
			
		}else if(row!=table.getRowCount()-1){
		   table.removeRow(row);
		}
		
		
		// modify caoyong 20140225  空行不可以被删除 已经删除数据库数据  end 
	}
	

	/**
	 * 当TABLE创建编辑控件
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponentS(Component com, int row, int column) {
		if (column != 0 && column != 2) {
			return;
		}
		if (!(com instanceof TTextField))
			return;
		String icd_type = "W";
		TParm parm = new TParm();
		parm.setData("ICD_TYPE", icd_type);
		parm.setData("ICD_EXCLUDE", "Y");
		parm.setData("ICD_MIN_EX", "M80000/0");
		parm.setData("ICD_MAX_EX", "M99890/1");
		parm.setData("ICD_START_EX", "V");
		parm.setData("ICD_END_EX", "Y");
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		// 设置弹出菜单
		textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSICDPopup.x"), parm);
		String returmMethodName = "";
		if (column == 0) {
			returmMethodName = "popReturnicdS";
		} else if (column == 2) {
			returmMethodName = "popReturnicdE";
		}
		// 定义接受返回值方法
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				returmMethodName);
	}


	/**
	 * 模糊查询（内部类）
	 */
	public class OrderList extends TLabel {
		TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");

		public String getTableShowValue(String s) {
			if (dataStore == null)
				return s;
			String bufferString = dataStore.isFilter() ? dataStore.FILTER
					: dataStore.PRIMARY;
			TParm parm = dataStore.getBuffer(bufferString);
			Vector v = (Vector) parm.getData("ICD_CODE");
			Vector d = (Vector) parm.getData("ICD_CHN_DESC");
			int count = v.size();
			for (int i = 0; i < count; i++) {
				if (s.equals(v.get(i)))
					return "" + d.get(i);
			}
			return s;
		}
	}

	/**
	 * 添加一行新数据
	 */
	public void insertOrderRow() {
		tableorder.acceptText();
		int row = tableorder.addRow();
		this.addItemRow(row);//add caoyong 20140225
		
	}

	/**
	 * 新增
	 */
	public void onNew() {
		this.onClear();
		String packCodestr = SystemTool.getInstance().getNo("ALL", "ODI",
				"ODIPACK_NO", "ODIPACK_NO");
		this.setValue("PACK_CODE", packCodestr);
	}


	/**
	 * 
	 * @return
	 */
	private TParm getOrderParm() {
		tableorder.acceptText();
		TParm parm = new TParm();
		int row = tableorder.getRowCount();
		//int seq = 1;
		for (int i = 0; i < row; i++) {
			if (tableorder.getItemString(i, "ORDER_CODE").equals(""))
				continue;
			String order_desc=tableorder.getItemString(i, "ORDER_DESC");
			String freq=tableorder.getItemString(i, "FREQ_CODE");
			if(freq.equals("")){
				parm.setErrCode(-1);
				parm.setErrText(order_desc+"的频次不能为空");
				return parm;
			}
			parm.addData("PACK_CODE", this.getValue("PACK_CODE"));
			parm.addData("SEQ_NO", tableorder.getItemData(i,
			"SEQ_NO"));
			parm.addData("LINKMAIN_FLG", tableorder.getItemData(i,
					"LINKMAIN_FLG"));
			parm.addData("LINK_NO", tableorder.getItemData(i, "LINK_NO"));
			parm.addData("ORDER_DESC", tableorder.getItemData(i, "ORDER_DESC"));
			parm.addData("MEDI_QTY", tableorder.getItemData(i, "MEDI_QTY"));
			parm.addData("MEDI_UNIT", tableorder.getItemData(i, "MEDI_UNIT"));
			parm.addData("ROUTE_CODE", tableorder.getItemData(i, "ROUTE_CODE"));
			parm.addData("FREQ_CODE", tableorder.getItemData(i, "FREQ_CODE"));
			parm.addData("SPECIFICATION", tableorder.getItemData(i,
					"SPECIFICATION"));
			parm.addData("DESCRIPTION", tableorder
					.getItemData(i, "DESCRIPTION"));
			parm.addData("ORDER_CODE", tableorder.getItemData(i, "ORDER_CODE"));
			parm.addData("CAT1_TYPE", tableorder.getItemData(i, "CAT1_TYPE"));
			parm.addData("ORDER_CAT1_CODE", tableorder.getItemData(i,
					"ORDER_CAT1_CODE"));
			parm.addData("TRADE_ENG_DESC", tableorder.getItemData(i,
					"TRADE_ENG_DESC"));
			parm.addData("OPT_DATE", SystemTool.getInstance().getDate());
			parm.addData("OPT_TERM", Operator.getIP());
			parm.addData("OPT_USER", Operator.getID());
			//seq++;
		}
		parm.setCount(parm.getCount("PACK_CODE"));
		return parm;
	}

	/**
	 * 单击事件
	 */
	public void onTableMclick() {
		int row = tablem.getSelectedRow();
		if (row < 0) {
			return;
		}
		// 诊断ICD替换中文
		OrderList orderDesc = new OrderList();
		TParm parmRow = tablem.getParmValue().getRow(row);
		this.setValueForParm("PACK_CODE;PACK_DESC;PY1;MESSAGE;PHA_PREVENCODE;SMEALTYPE", parmRow);
		String packcode = parmRow.getValue("PACK_CODE");
		TParm parmorder = ODIInfecPackTool.getInstance().getInfecOrderParm(
				packcode);
		if (parmorder.getErrCode() < 0) {
			this.messageBox("套餐医嘱查询错误");
			return;
		}
		tableorder.setParmValue(parmorder);
		this.insertOrderRow();
	}
	
	
	
/**
 * 	套餐类型:W:诊断 OP:手术 
 *  add  caoyong 2013830
 */
	
	public void onClickonchageButton() {
		if("OP".equalsIgnoreCase(getValueString("SMEALTYPE"))){
			callFunction("UI|PHA_PREVENCODE|setEnabled", true);
			this.grabFocus("PHA_PREVENCODE");
		}
		if ("W".equalsIgnoreCase(this.getValueString("SMEALTYPE"))) {
			callFunction("UI|PHA_PREVENCODE|setEnabled", false);
			this.grabFocus("PHA_PREVENCODE");
		}
		    //this.onClear();
	}
	
	
	/**
	 * 保存
	 */
	public void onSave() {
		tableorder.acceptText();
		if("OP".equalsIgnoreCase(getValueString("SMEALTYPE"))){//add caoyong 2013830 判断套餐类型
			if("".equals(getValueString("PHA_PREVENCODE"))){//add caoyong 2013829 抗菌素限制类型不能为空
				messageBox("抗菌素限制类型不能为空");
				this.grabFocus("PHA_PREVENCODE");
				return;
			}	
		}else{
			callFunction("UI|PHA_PREVENCODE|setEnabled", false);
			this.setValue("PHA_PREVENCODE", "");//诊断不需要限制类型
		}
		TParm parmM = new TParm();
		parmM.setData("PHA_PREVENCODE", this.getValueString("PHA_PREVENCODE"));//add 抗菌素限制类型 coyong 2013829
		parmM.setData("SMEALTYPE", this.getValueString("SMEALTYPE"));//add 套餐类型套餐类型  coyong 2013829
		parmM.setData("PACK_CODE", this.getValueString("PACK_CODE"));
		parmM.setData("PACK_CODE", this.getValueString("PACK_CODE"));
		parmM.setData("PACK_DESC", this.getValueString("PACK_DESC"));
		parmM.setData("PY1", this.getValueString("PY1"));
		parmM.setData("MESSAGE", this.getValueString("MESSAGE"));
		parmM.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parmM.setData("OPT_TERM", Operator.getIP());
		parmM.setData("OPT_USER", Operator.getID());

		TParm parmorder = this.getOrderParm();
		if(parmorder.getErrCode()<0){
			this.messageBox(parmorder.getErrText());
			return;
		}
		if (this.getValueString("PACK_CODE").equals("")) {
			this.messageBox("套餐编码为空");
			return;
		}
		
	//	System.out.println("哈哈哈哈哈哈哈哈：：："+parmorder.getData());
		TParm inparm = new TParm();
		inparm.setData("mPARM", parmM.getData());
		inparm.setData("orderPARM", parmorder.getData());
		TParm result = TIOM_AppServer.executeAction(
				"action.odi.ODIInfecPackAction", "onSave", inparm);
		if (result.getErrCode() < 0) {
			this.messageBox("保存失败");
			onQuery();
			return;
		}
		this.messageBox("保存成功");
		onQuery();
	}
	/**
	 * 删除
	 */
	public void onDelete() {
		tablem.acceptText();
//		tableicd.acceptText();
		tableorder.acceptText();
		TParm parmM = tablem.getParmValue();
		if (parmM.getCount() <= 0) {
			this.messageBox("没有保存的数据");
			return;
		}
		int row = tablem.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择要删除的数据");
			return;
		}
		TParm inparm = new TParm();
		inparm.setData("mPARM", parmM.getRow(row).getData());
		TParm result = TIOM_AppServer.executeAction(
				"action.odi.ODIInfecPackAction", "onDelete", inparm);
		if (result.getErrCode() < 0) {
			this.messageBox("删除失败");
			onQuery();
			return;
		}
		this.messageBox("删除成功");
		this.onClear();
		onQuery();
	}

	/**
	 * 名称回车事件
	 */
	public void onUserNameAction() {
		String userName = getValueString("PACK_DESC");
		String py = TMessage.getPy(userName);
		setValue("PY1", py);
		((TTextField) getComponent("PY1")).grabFocus();
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		String where = "WHERE SMEALTYPE = 'OP'";
		if (!this.getValueString("PACK_CODE").equals(""))
			where += " AND PACK_CODE='" + this.getValueString("PACK_CODE")
					+ "' ";
		if (!this.getValueString("PACK_DESC").equals("")) {
			if (where.length() > 0) {
				where += " AND PACK_DESC='" + this.getValueString("PACK_DESC")+"' ";
			} else {
				where += " AND PACK_DESC='"
						+ this.getValueString("PACK_DESC")+"' ";
						
			}
		}
		where +=" ORDER BY PACK_CODE";
		String sql = "SELECT * FROM ODI_INFEC_PACKM " + where;
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getErrCode() < 0) {
			this.messageBox("查询错误");
			return;
		}
		if (parm.getCount() <= 0) {
			this.messageBox("没有查询数据");
			return;
		}
		this.tablem.setParmValue(parm);
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.clearValue("PACK_CODE;PACK_DESC;PY1;MESSAGE;PHA_PREVENCODE;SMEALTYPE;PHA_PREVENCODE");
		tablem.removeRowAll();
		tableorder.removeRowAll();
	}

	/**
	 * 得到TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	
	/**
	  * 比较组  add caoyong 20131106 
	  * @param linkNO
	  * @return
	  */
		public boolean getCompare(String linkNO,int row ){
			
			 boolean flag=false;
			 for (int i = 0; i < tableorder.getRowCount(); i++) {
				if(i!=row){
				if (tableorder.getItemString(i, "LINK_NO").equals(linkNO)&&"Y".equals(tableorder.getItemString(i, "LINKMAIN_FLG"))){
					flag=true;
				    break;
			    }
				}
			}
			return flag;
			
		}
		/**
		 * 改变连行中组的值则没有连的组值
		 * 和连中的素质相同则把组的单位等变成连的
		 * @param liNo
		 * @param row
		 *  add caoyong 2013117
		 * @return
		 */
		public TParm getTresultQ(String liNo,int row){
			TParm tresult=new TParm();
			for (int i = 0; i < tableorder.getRowCount(); i++) {
				if(row!=i){
					if(liNo.equals(tableorder.getItemString(i, "LINK_NO"))&&!"Y".equals(tableorder.getItemString(i, "LINKMAIN_FLG"))){
						tresult=tableorder.getParmValue().getRow(row);
						getChangeValue(tresult, i );
						
					}
				}
			}
			return tresult;
		}
		
		/**
		 * 改变组织查询和连相同的组号
		 * 然后把用量单位等变成连的
		 * @param liNo
		 *  add caoyong 2013117
		 * @return
		 */
		public TParm getTresult(String  liNo,int row){
			TParm tresult=new TParm();
			 for (int i = 0; i < tableorder.getRowCount(); i++) {
				 if(row!=i){
				 if(liNo.equals(tableorder.getItemString(i, "LINK_NO"))&&"Y".equals(tableorder.getItemString(i, "LINKMAIN_FLG"))){
					 tresult=tableorder.getParmValue().getRow(i);
						getChangeValue(tresult, row );
					 break;
				 }
				 }
				}
			 return tresult;
		}
		/**
		 * 改变频次和用法
		 * @param tresult
		 * @param row
		 *  add caoyong 2013117
		 */
	    public void  getChangeValue(TParm tresult,int row ){
	    	//tableorder.setItem(row, "MEDI_QTY", tresult.getData("MEDI_QTY"));//用量
			//tableorder.setItem(row, "MEDI_UNIT", tresult.getData("MEDI_UNIT"));//单位
			tableorder.setItem(row, "ROUTE_CODE", tresult.getData("ROUTE_CODE" ));//用法
			tableorder.setItem(row, "FREQ_CODE", tresult.getData("FREQ_CODE")); //频次
			//tableorder.setItem(row, "SPECIFICATION", tresult.getData("SPECIFICATION"));//规格
			//tableorder.setItem(row, "DESCRIPTION", tresult.getData("DESCRIPTION"));//备注
	    	
	    }
	    
	    public void addItemRow( int row){
	    	tableorder.setItem(row, "LINKMAIN_FLG", "N");
			tableorder.setItem(row, "SEQ_NO", "");
			tableorder.setItem(row, "LINK_NO", "");
			tableorder.setItem(row, "ORDER_DESC", "");
			tableorder.setItem(row, "MEDI_QTY", "");
			tableorder.setItem(row, "MEDI_UNIT", "");
			tableorder.setItem(row, "ROUTE_CODE", "");
			tableorder.setItem(row, "FREQ_CODE", "");
			tableorder.setItem(row, "SPECIFICATION", "");
			tableorder.setItem(row, "DESCRIPTION", "");
			tableorder.setItem(row, "ORDER_CODE", "");
			tableorder.setItem(row, "CAT1_TYPE", "");
			tableorder.setItem(row, "ORDER_CAT1_CODE", "");
			tableorder.setItem(row, "TRADE_ENG_DESC", "");
	    }
}
