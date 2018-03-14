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
 * <p>口头医嘱界面</p>
 * 
 * @author wangqing 20171013
 *
 */
public class ONWOrderControl extends TControl {
	private TTable tablePack;
	private TTable tableOrder;
	private TTable orderT;
	/**
	 * 系统参数
	 */
	private TParm sysParm;
	/**
	 * 检伤号
	 */
	private String triageNo;

	private TParm deleteParm;
	
	/**
	 * 套餐号
	 */
	private String packCode;

	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		Object o = this.getParameter();
		if(o != null && o instanceof TParm){
			sysParm = (TParm) o;
			triageNo = sysParm.getValue("TRIAGE_NO");
			this.setValue("TRIAGE_NO", triageNo);// 检伤号
			this.setValue("MR_NO", sysParm.getValue("MR_NO"));// 病案号
			this.setValue("PAT_NAME", sysParm.getValue("PAT_NAME"));// 患者姓名
			this.setValue("PAT_SEX", sysParm.getValue("PAT_SEX"));// 患者性别
			this.setValue("PAT_AGE", sysParm.getValue("PAT_AGE"));// 患者年龄
		}else{
			this.messageBox("系统参数错误！！！");
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
		//TABLE_PACK单击事件
		callFunction("UI|" + "TABLE_PACK" + "|addEventListener", "TABLE_PACK" + "->" + TTableEvent.CLICKED, this, "onTableClicked");
		//TABLE_ORDER单击事件
		callFunction("UI|" + "TABLE_ORDER" + "|addEventListener", "TABLE_ORDER" + "->" + TTableEvent.DOUBLE_CLICKED, this, "onTableOrderDoubleClicked");
		// orderT单击事件
		callFunction("UI|" + "TABLE_ORDER" + "|addEventListener", "TABLE_ORDER" + "->" + TTableEvent.CLICKED, this, "onOrderTClicked");
		// orderT的值改变事件
		addEventListener("ORDER" + "->" + TTableEvent.CHANGE_VALUE, this, "onChangeOrderTValue");
		// orderT的checkBox事件
		orderT.addEventListener(TTableEvent.CHECK_BOX_CLICKED,this,"onCheckBoxValue");
		// orderT的控件编辑事件
		orderT.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this, "onCreateEditComoponent");		
		// 初始化数据
		this.callFunction("UI|RX_TYPE|setEnabled", false);
		this.callFunction("UI|DEPT_CODE|setEnabled", false);				
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				TFrame frame = (TFrame) getComponent();
//				frame.setPreferredSize(new Dimension(1000,500));
//				frame.pack();
				frame.setResizable(false);  // 不可缩放
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // 默认最大化		
				onClear();	
			}
		});
	}

	/**
	 * 查询套餐
	 */
	public void onQuery(){
		// 初始化
		this.onClear2();	
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
	}

	/**
	 * TABLE_ORDER双击事件
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
		// 校验此医嘱是否已经停用
		if(!checkActive(orderCode)){
			this.messageBox(orderDesc+"已经停用，不可以开立");
			return;
		}	
		// 校验是否已经添加
		for(int i=0; i<orderP.getCount(); i++){
			if(orderP.getValue("ORDER_CODE", i) == null 
					|| orderP.getValue("ORDER_CODE", i).trim().length()==0){
				continue;
			}
			if(orderP.getValue("ORDER_CODE", i).equals(orderCode)){
				if (messageBox("友情提示", "此医嘱已经开立，是否继续开立?", TControl.YES_NO_OPTION) != 0) {// 不开立	
					return;
				}
			}
		}	
		//  orderT解锁
		this.unlock(orderT);
		// orderT删除空行
		if(deleteParm != null){
			deleteParm = new TParm();
			deleteParm.setCount(0);
		}	
		int delR = orderP.getCount()-1;
		TablePublicTool.removeRow(orderT, delR, deleteParm, 
				new String[]{"TRIAGE_NO", "SEQ_NO"}, new String[]{triageNo, orderP.getValue("SEQ_NO", delR)});
	    // 插入新行	
		// 如果是连嘱，同组的全插入
		if(rowParm.getValue("LINK_NO") != null 
				&& rowParm.getValue("LINK_NO").trim().length()>0){// 连嘱
			int linkNo = getMaxLinkNo();// 组号
			for(int i=0; i<orderParm.getCount(); i++){
				if(orderParm.getValue("LINK_NO", i) != null 
						&& orderParm.getValue("LINK_NO", i).equals(rowParm.getValue("LINK_NO"))){// 同组连嘱
					String orderCodeTemp = orderParm.getValue("ORDER_CODE", i);
					String orderDescTemp = orderParm.getValue("ORDER_DESC", i);
					// 校验此医嘱是否已经停用
					if(!checkActive(orderCodeTemp)){
						this.messageBox(orderDescTemp+"已经停用，不可以开立");
						continue;
					}
					int seqNo = getMaxSeqNo(triageNo);// 医嘱序号
					int newR = this.onNewOrder(triageNo, seqNo);
					if(newR<0){
						this.messageBox("newR<0");
						// orderT加锁
						this.lock();
						return;
					}
					// 赋值
					TablePublicTool.modifyRow(orderT, newR, 1, "LINKMAIN_FLG", 
							orderP.getValue("LINKMAIN_FLG", newR), orderParm.getValue("LINKMAIN_FLG", i));// 连
					TablePublicTool.modifyRow(orderT, newR, 2, "LINK_NO", 
							orderP.getValue("LINK_NO", newR), linkNo);// 组
					orderP.setData("ORDER_CODE", newR, orderParm.getValue("ORDER_CODE", i));// 医嘱代码
					TablePublicTool.modifyRow(orderT, row);
					TablePublicTool.modifyRow(orderT, newR, 3, "ORDER_DESC", 
							orderP.getValue("ORDER_DESC", newR), orderParm.getValue("ORDER_DESC", i));// 医嘱描述
					TablePublicTool.modifyRow(orderT, newR, 4, "MEDI_QTY", 
							orderP.getValue("MEDI_QTY", newR), orderParm.getValue("MEDI_QTY", i));// 用量
					TablePublicTool.modifyRow(orderT, newR, 5, "MEDI_UNIT", 
							orderP.getValue("MEDI_UNIT", newR), orderParm.getValue("MEDI_UNIT", i));// 单位
					TablePublicTool.modifyRow(orderT, newR, 6, "ROUTE_CODE", 
							orderP.getValue("ROUTE_CODE", newR), orderParm.getValue("ROUTE_CODE", i));// 用法
					TablePublicTool.modifyRow(orderT, newR, 7, "FREQ_CODE", 
							orderP.getValue("FREQ_CODE", newR), orderParm.getValue("FREQ_CODE", i));// 频次
					TablePublicTool.modifyRow(orderT, newR, 8, "TAKE_DAYS", 
							orderP.getValue("TAKE_DAYS", newR), orderParm.getValue("TAKE_DAYS", i));// 天数
					orderP.setData("CAT1_TYPE", newR, orderParm.getValue("CAT1_TYPE", i));// 大分类
					TablePublicTool.modifyRow(orderT, row);
					orderP.setData("ORDER_CAT1_CODE", newR, orderParm.getValue("ORDER_CAT1_CODE", i));// 小分类
					TablePublicTool.modifyRow(orderT, row);
					TablePublicTool.modifyRow(orderT, newR, 9, "NOTE_DATE", 
							orderP.getValue("NOTE_DATE", newR), SystemTool.getInstance().getDate());// 记录日期					
				}
			}				
		}else{// 非连嘱
			int seqNo = getMaxSeqNo(triageNo);
			int newR = this.onNewOrder(triageNo, seqNo);
			if(newR<0){
				this.messageBox("newR<0");
				// orderT加锁
				this.lock();
				return;
			}
			// 赋值
			TablePublicTool.modifyRow(orderT, newR, 1, "LINKMAIN_FLG", 
					orderP.getValue("LINKMAIN_FLG", newR), rowParm.getValue("LINKMAIN_FLG"));// 连
			orderP.setData("ORDER_CODE", newR, rowParm.getValue("ORDER_CODE"));// 医嘱代码
			TablePublicTool.modifyRow(orderT, row);
			TablePublicTool.modifyRow(orderT, newR, 3, "ORDER_DESC", 
					orderP.getValue("ORDER_DESC", newR), rowParm.getValue("ORDER_DESC"));// 医嘱描述
			TablePublicTool.modifyRow(orderT, newR, 4, "MEDI_QTY", 
					orderP.getValue("MEDI_QTY", newR), rowParm.getValue("MEDI_QTY"));// 用量
			TablePublicTool.modifyRow(orderT, newR, 5, "MEDI_UNIT", 
					orderP.getValue("MEDI_UNIT", newR), rowParm.getValue("MEDI_UNIT"));// 单位
			TablePublicTool.modifyRow(orderT, newR, 6, "ROUTE_CODE", 
					orderP.getValue("ROUTE_CODE", newR), rowParm.getValue("ROUTE_CODE"));// 用法
			TablePublicTool.modifyRow(orderT, newR, 7, "FREQ_CODE", 
					orderP.getValue("FREQ_CODE", newR), rowParm.getValue("FREQ_CODE"));// 频次
			TablePublicTool.modifyRow(orderT, newR, 8, "TAKE_DAYS", 
					orderP.getValue("TAKE_DAYS", newR), rowParm.getValue("TAKE_DAYS"));// 天数
			orderP.setData("CAT1_TYPE", newR, rowParm.getValue("CAT1_TYPE"));// 大分类
			TablePublicTool.modifyRow(orderT, row);
			orderP.setData("ORDER_CAT1_CODE", newR, rowParm.getValue("ORDER_CAT1_CODE"));// 小分类
			TablePublicTool.modifyRow(orderT, row);
			TablePublicTool.modifyRow(orderT, newR, 9, "NOTE_DATE", 
					orderP.getValue("NOTE_DATE", newR), SystemTool.getInstance().getDate());// 记录日期
		}
		int seqNo = getMaxSeqNo(triageNo);
		// 新增空行
		int newR = this.onNewOrder(triageNo, seqNo);
		if(newR<0){
			this.messageBox("newR<0");
			// orderT加锁
			this.lock();
			return;
		}
		// orderT加锁
		this.lock();	
	}

	/**
	 * orderT单击事件
	 * @param row
	 */
	public void onOrderTClicked(int row){
		
	}
	
	/**
	 * 查询口头医嘱列表
	 */
	public void onQueryOrder(){	
		if(triageNo == null 
				|| triageNo.trim().length()==0){
			this.messageBox("triageNo is null");
			return;
		}
		// 初始化
		this.onClear5();
		// orderT解锁
		this.unlock(orderT);	
		TParm parm = new TParm();
		parm.setData("TRIAGE_NO", triageNo);
		TParm result = new TParm();
		result = TIOM_AppServer.executeAction("action.onw.ONWComPackAction", "selectOnwOrder", parm);
		if(result.getErrCode()<0){
			System.out.println("result.getErrCode()<0");
			// orderT加锁
			this.lock();
			return;
		}
		for(int i=0; i<result.getCount(); i++){
			result.setData("SEL_FLG", i, "N");// 选
		}	
		TablePublicTool.setParmValue(orderT, result);
		// 新增医嘱
		int newR = this.onNewOrder(triageNo, this.getMaxSeqNo(triageNo));
		if(newR<0){
			this.messageBox("newR<0");
			// orderT加锁
			this.lock();
			return;
		}
		// orderT加锁
		this.lock();	
	}

	public void onCheckBoxValue(Object obj){
		// orderT解锁
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
			// orderP加锁
			this.lock();
			return;
		}
		if(col == 0){// 选
			// 如果是空行，不能勾选
			if((orderP.getValue("ORDER_CODE", row) == null || orderP.getValue("ORDER_CODE", row).trim().length()==0) 
					&& (orderP.getValue("SEL_FLG", row) != null && orderP.getValue("SEL_FLG", row).equals("Y")) ){
				this.messageBox("空行不能勾选！！！");
				TablePublicTool.modifyRow(orderT, row, 0, "SEL_FLG", 
						orderP.getValue("SEL_FLG", row), "N");// 选		
				// orderP加锁
				this.lock();
				return;
			}	
			// 如果医生已经签名，不能勾选
			if((orderP.getValue("SIGN_DR", row) != null && orderP.getValue("SIGN_DR", row).trim().length()>0) 
					&& (orderP.getValue("SEL_FLG", row) != null && orderP.getValue("SEL_FLG", row).equals("Y"))){
				this.messageBox("医生已经签名，不能勾选");
				TablePublicTool.modifyRow(orderT, row, 0, "SEL_FLG", 
						orderP.getValue("SEL_FLG", row), "N");// 选		
				// orderP加锁
				this.lock();
				return;
			}	
			// 如果连嘱细项，并且取消勾选
			// 有同组连嘱主项，并且已经勾选，则同组连嘱细项不能取消勾选
			if( (!(orderP.getValue("LINKMAIN_FLG", row) != null && orderP.getValue("LINKMAIN_FLG", row).equals("Y"))) 
					&& (orderP.getValue("LINK_NO", row) != null && orderP.getValue("LINK_NO", row).length()>0) 
					&& (!(orderP.getValue("SEL_FLG", row) != null && orderP.getValue("SEL_FLG", row).equals("Y"))) ){
				for(int i=0; i<orderP.getCount(); i++){
					if( (orderP.getValue("LINKMAIN_FLG", i) != null && orderP.getValue("LINKMAIN_FLG", i).equals("Y")) 
							&& (orderP.getValue("LINK_NO", i) != null && orderP.getValue("LINK_NO", i).length()>0) 
							&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")) ){
						this.messageBox("同组连嘱主项已经勾选，则同组连嘱细项不能取消勾选");
						TablePublicTool.modifyRow(orderT, row, 0, "SEL_FLG", 
								orderP.getValue("SEL_FLG", row), "Y");// 选
						// orderP加锁
						this.lock();
						return;
					}
				}			
			}
			// 如果连嘱主项，则此组全部保持一致
			if( (orderP.getValue("LINKMAIN_FLG", row) != null 
					&& orderP.getValue("LINKMAIN_FLG", row).equals("Y")) ){
				for(int i=0; i<orderP.getCount(); i++){
					if(orderP.getValue("LINK_NO", i) != null 
							&& orderP.getValue("LINK_NO", i).equals(orderP.getValue("LINK_NO", row))){// 同组连嘱
						TablePublicTool.modifyRow(orderT, i, 0, "SEL_FLG", 
								orderP.getValue("SEL_FLG", i), orderP.getValue("SEL_FLG", row));// 选
					}
				}		
			}					
		}
		if(col == 1){// 连
			// 选中，大于等于此行&&小于第一个遇到的连嘱主项行 &&小于空行&&小于护士已签名行&&小于医生已签名行
			// 划分成一组，用法保持一致
			if(orderP.getValue("LINKMAIN_FLG", row) != null 
					&& orderP.getValue("LINKMAIN_FLG", row).equals("Y")){// 选中
				// 空行不能选中
				if (orderP.getValue("ORDER_CODE", row) == null 
						|| orderP.getValue("ORDER_CODE", row).trim().length()==0) {
					this.messageBox("请开立医嘱");
					TablePublicTool.modifyRow(orderT, row, 1, "LINKMAIN_FLG", 
							orderP.getValue("LINKMAIN_FLG", row), "N");// 选
					// orderP加锁
					this.lock();
					return;
				}
				// 更新用法
				if("PHA".equals(orderP.getValue("CAT1_TYPE", row))){
					TParm action = new TParm();
					action.setData("ORDER_CODE", orderP.getValue("ORDER_CODE", row));
					TParm result = OdiMainTool.getInstance().queryPhaBase(action);
					TablePublicTool.modifyRow(table, row, 6, "ROUTE_CODE", 
							orderP.getValue("ROUTE_CODE", row), result.getValue("ROUTE_CODE",0));// 用法
				}else{
					TablePublicTool.modifyRow(table, row, 6, "ROUTE_CODE", 
							orderP.getValue("ROUTE_CODE", row), "");// 用法
				}
				int maxLinkNo = getMaxLinkNo();
				for(int i=row; i<orderP.getCount(); i++){
					if(i>row){
						// 连嘱主项
						if(orderP.getValue("LINKMAIN_FLG", i) != null 
								&& orderP.getValue("LINKMAIN_FLG", i).equals("Y")){
							break;
						}
						// 护士已经签名
						if(orderP.getValue("SIGN_NS", i) != null 
								&& orderP.getValue("SIGN_NS", i).trim().length()>0){
							break;
						}
						// 医生已经签名
						if(orderP.getValue("SIGN_DR", i) != null 
								&& orderP.getValue("SIGN_DR", i).trim().length()>0){
							break;
						}
						if(orderP.getValue("ORDER_CODE", i) == null 
								|| orderP.getValue("ORDER_CODE", i).trim().length()==0){// 空行
							break;
						}											
					}					
					TablePublicTool.modifyRow(table, i, 2, "LINK_NO", 
							orderP.getValue("LINK_NO", i), maxLinkNo);// 组号
					TablePublicTool.modifyRow(table, i, 6, "ROUTE_CODE", 
							orderP.getValue("ROUTE_CODE", i), orderP.getValue("ROUTE_CODE", row));// 用法
				}			
			}else{//  取消选中，相同组的LINK_NO赋值为空，相同组重设用法
				String linkNo = orderP.getValue("LINK_NO", row);
				for(int i=row; i<orderP.getCount(); i++){
					if(orderP.getValue("LINK_NO", i) != null 
							&& orderP.getValue("LINK_NO", i).equals(linkNo)){// 相同组
						TablePublicTool.modifyRow(table, i, 2, "LINK_NO", 
								orderP.getValue("LINK_NO", i), "");// 组号
						if("PHA".equals(orderP.getValue("CAT1_TYPE", i))){
							TParm action = new TParm();
							action.setData("ORDER_CODE", orderP.getValue("ORDER_CODE", i));
							TParm result = OdiMainTool.getInstance().queryPhaBase(action);
							TablePublicTool.modifyRow(table, i, 6, "ROUTE_CODE", 
									orderP.getValue("ROUTE_CODE", i), result.getValue("ROUTE_CODE",0));// 用法
						}else{
							TablePublicTool.modifyRow(table, i, 6, "ROUTE_CODE", 
									orderP.getValue("ROUTE_CODE", i), "");// 用法
						}
					}
				}
			}
		}
		// orderP加锁
		this.lock();
	}

	/**
	 * orderT值改变监听
	 * @param obj TTableNode
	 * @return true，不改变；false，改变
	 */
	public boolean onChangeOrderTValue(Object obj) {
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
	 * 新增医嘱
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
		// 赋初始值
		TablePublicTool.modifyRow(orderT, row, 0, "SEL_FLG", "", "N");// 选
		orderP.setData("TRIAGE_NO", row, triageNo);// 检伤号
		TablePublicTool.modifyRow(orderT, row);
		orderP.setData("SEQ_NO", row, seqNo);// 医嘱序号
		TablePublicTool.modifyRow(orderT, row);
		TablePublicTool.modifyRow(orderT, row, 7, "FREQ_CODE", "", "STAT");// 频次，默认为立即使用
		TablePublicTool.modifyRow(orderT, row, 8, "TAKE_DAYS", "", 1);// 天数，默认为1天
		orderP.setData("OPT_USER", row, Operator.getID());// 操作人员
		TablePublicTool.modifyRow(orderT, row);
		orderP.setData("OPT_DATE", row, SystemTool.getInstance().getDate());// 操作时间
		TablePublicTool.modifyRow(orderT, row);
		orderP.setData("OPT_TERM", row, Operator.getIP());// 操作终端
		TablePublicTool.modifyRow(orderT, row);
		return row;
	}
	
	/**
	 * 删除医嘱
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
		//  orderT解锁
		this.unlock(orderT);
		if(deleteParm == null){
			deleteParm = new TParm();
			deleteParm.setCount(0);
		}	
		boolean flg = false;// 标记是否有勾选行
		for(int i=0; i<orderP.getCount(); i++){
			// 护士已经签名的
			if( (orderP.getValue("SIGN_NS", i) != null && orderP.getValue("SIGN_NS", i).trim().length()>0) 
					&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y"))  ){
				this.messageBox("护士已经签名的行，不可删除");
				// orderT加锁
				this.lock();
				return;
			}
			// 医生已经签名的
			if( (orderP.getValue("SIGN_DR", i) != null && orderP.getValue("SIGN_DR", i).trim().length()>0) 
					&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y"))  ){
				this.messageBox("医生已经签名的的行，不可删除");
				// order
				this.lock();
				return;
			}
			if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				flg = true;
			}	
		}
		if(!flg){
			this.messageBox("请选择删除行！！！");
			return;
		}
		for(int i=orderP.getCount()-1; i>=0; i--){
			if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				TablePublicTool.removeRow(orderT, i, deleteParm, 
						new String[]{"TRIAGE_NO", "SEQ_NO"}, new String[]{triageNo, orderP.getValue("SEQ_NO", i)});	
			}
		}
		// orderT加锁
		this.lock();	
	}

	/**
	 * 获取最大seqNo
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
	 * 清空
	 */
	public void onClear(){
		this.onClear1();
		this.onQuery();
		this.onQueryOrder();
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
	 * 护士签名
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
		boolean flg = false;// 标记是否有勾选行
		for(int i=0; i<orderP.getCount(); i++){
			// 护士已经签名的不可签名
			if( (orderP.getValue("SIGN_NS", i) != null && orderP.getValue("SIGN_NS", i).trim().length()>0) 
					&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y"))  ){
				this.messageBox("护士已经签名的行，不可签名！！！");				
				return;
			}		
			// 医生已经签名的不可签名
			if( (orderP.getValue("SIGN_DR", i) != null && orderP.getValue("SIGN_DR", i).trim().length()>0) 
					&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y"))  ){
				this.messageBox("医生已经签名的行，不可签名！！！");				
				return;
			}
			// 连嘱细项勾选，主项没有勾选
			if( (!(orderP.getValue("LINKMAIN_FLG", i) != null && orderP.getValue("LINKMAIN_FLG", i).equals("Y"))) 
					&& (orderP.getValue("LINK_NO", i) != null && orderP.getValue("LINK_NO", i).trim().length()>0) 
					&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")) ){
				for(int j=0; j<orderP.getCount(); j++){
					if( (orderP.getValue("LINKMAIN_FLG", j) != null && orderP.getValue("LINKMAIN_FLG", j).equals("Y")) 
							&& (orderP.getValue("LINK_NO", j) != null && orderP.getValue("LINK_NO", j).equals(orderP.getValue("LINK_NO", i))) 
							&& (!(orderP.getValue("SEL_FLG", j) != null && orderP.getValue("SEL_FLG", j).equals("Y"))) ){
						this.messageBox("连嘱细项不能单独签名！！！");
						return;
					}
				}
			}			
			if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				flg = true;
			}			
		}
		if(!flg){
			this.messageBox("请选择签名行！！！");
			return;
		}	
		TParm parm = new TParm();
		Object obj = this.openDialog("%ROOT%\\config\\reg\\REGSavePassWordCheck.x", parm);
		if(obj != null && obj instanceof TParm){
			parm = (TParm) obj;
			// 取消签名
			if(parm.getValue("RESULT") != null && parm.getValue("RESULT").equals("CANCLE")){
				// 取消勾选
				for(int i=0; i<orderP.getCount(); i++){
					if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
						TablePublicTool.modifyRow(orderT, i, 0, "SEL_FLG", orderP.getValue("SEL_FLG", i), "N");
					}			
				}
				this.messageBox("签名失败！！！");
				return;
			}
			for(int i=0; i<orderP.getCount(); i++){ 
				if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
					TablePublicTool.modifyRow(orderT, i, 0, "SEL_FLG", orderP.getValue("SEL_FLG", i), "N");// 取消勾选 
					TablePublicTool.modifyRow(orderT, i, 11, "SIGN_NS", orderP.getValue("SIGN_NS", i), parm.getValue("USER_ID"));// 护士签名 
				}			
			}	
			// 保存
			if(this.onSave1()){
				this.messageBox("签名成功！！！");
			}else{
				this.messageBox("签名失败！！！");
			}			
		}else{
			// 取消勾选
			for(int i=0; i<orderP.getCount(); i++){
				if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
					TablePublicTool.modifyRow(orderT, i, 0, "SEL_FLG", orderP.getValue("SEL_FLG", i), "N");
				}			
			}	
			this.messageBox("签名失败！！！");
			return;
		}
	}

	/**
	 * 护士取消签名
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
		boolean flg = false;// 标记是否有勾选行
		for(int i=0; i<orderP.getCount(); i++){
			// 护士未签名的不可取消签名
			if( (!(orderP.getValue("SIGN_NS", i) != null && orderP.getValue("SIGN_NS", i).trim().length()>0)) 
					&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y"))  ){
				this.messageBox("护士未签名的，不可取消签名");				
				return;
			}
			// 医生已经签字的不能取消签名
			if( (orderP.getValue("SIGN_DR", i) != null && orderP.getValue("SIGN_DR", i).trim().length()>0) 
					&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y"))  ){
				this.messageBox("医生已经签字的，不能取消签名");				
				return;
			}
			// 连嘱细项勾选，主项没有勾选
			if( (!(orderP.getValue("LINKMAIN_FLG", i) != null && orderP.getValue("LINKMAIN_FLG", i).equals("Y"))) 
					&& (orderP.getValue("LINK_NO", i) != null && orderP.getValue("LINK_NO", i).trim().length()>0) 
					&& (orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")) ){
				for(int j=0; j<orderP.getCount(); j++){
					if( (orderP.getValue("LINKMAIN_FLG", j) != null && orderP.getValue("LINKMAIN_FLG", j).equals("Y")) 
							&& (orderP.getValue("LINK_NO", j) != null && orderP.getValue("LINK_NO", j).equals(orderP.getValue("LINK_NO", i))) 
							&& (!(orderP.getValue("SEL_FLG", j) != null && orderP.getValue("SEL_FLG", j).equals("Y"))) ){
						this.messageBox("连嘱细项不能单独取消签名！！！");
						return;
					}
				}
			}
			if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				flg = true;
			}			
		}
		if(!flg){
			this.messageBox("请选择取消签名行！！！");
			return;
		}	
		for(int i=0; i<orderP.getCount(); i++){
			if(orderP.getValue("SEL_FLG", i) != null && orderP.getValue("SEL_FLG", i).equals("Y")){
				TablePublicTool.modifyRow(orderT, i, 0, "SEL_FLG", orderP.getValue("SEL_FLG", i), "N");// 取消勾选 
				TablePublicTool.modifyRow(orderT, i, 11, "SIGN_NS", orderP.getValue("SIGN_NS", i), "");// 护士签名 
			}
		}		
		// 保存
		if(this.onSave2()){
			this.messageBox("取消签名成功！！！");
		}else{
			this.messageBox("取消签名失败！！！");
		}
	}
	
	/**
	 * 保存医嘱
	 * @param flg
	 */
	public void onSave(){
		if(this.onSave1()){
			this.messageBox("保存成功");
		}else{
			this.messageBox("保存失败");
		}
	}

	/**
	 * 护士签名
	 */
	public boolean onSave1(){
		TParm saveParm = new TParm();
		if(orderT != null && orderT.getParmValue() != null){
			TParm orderP = orderT.getParmValue();
			this.checkData(orderP, "TRIAGE_NO;SEQ_NO;ORDER_CODE;ORDER_DESC;#STATUS");
			// 处理时间格式
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
		String tempPackCode = this.packCode;// add by wangqing 2017年10月27日
		// 刷新数据
		this.onClear();	
		// 默认勾选
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
	 * 保存医嘱、取消护士签名
	 */
	public boolean onSave2(){
		TParm saveParm = new TParm();
		if(orderT != null && orderT.getParmValue() != null){
			TParm orderP = orderT.getParmValue();
			this.checkData(orderP, "TRIAGE_NO;SEQ_NO;ORDER_CODE;ORDER_DESC;#STATUS");
			// 处理时间格式
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
		String tempPackCode = this.packCode;// add by wangqing 2017年10月27日
		// 刷新数据
		this.onClear();	
		// 默认勾选
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
	 * 拿到最大连结号
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
	 * orderT监听事件（编辑医嘱）
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComoponent(Component com,int row,int column){	
		if(column == 3){// 医嘱描述
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
	 * orderT监听事件（编辑医嘱），接受返回值方法
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
	 * orderT监听事件（编辑医嘱），修改医嘱
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
		if(isSame(orderP, orderCode, row)){// 有相同医嘱
			if (messageBox("友情提示", "有相同医嘱，是否继续开立此医嘱?", TControl.YES_NO_OPTION) != 0) {// 不开立
				TablePublicTool.modifyRow(orderT, row, 3, "ORDER_DESC", 
						orderP.getValue("ORDER_DESC", row), getOrderDesc(orderP.getValue("ORDER_CODE", row)));
				return;
			}else{// 开立
				insertPackOrder(row,parm);
			}
		}else{// 没有相同医嘱
			insertPackOrder(row,parm);
		}
	}

	/**
	 * orderT监听事件（编辑医嘱），判断是否有相同医嘱
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
	 * orderT监听事件（编辑医嘱），修改医嘱
	 * @param row
	 * @param parm
	 */
	public void insertPackOrder(int row,TParm parm){
		// orderT解锁
		this.unlock(orderT);
		TParm orderParmValue = orderT.getParmValue();
		orderParmValue.setData("ORDER_CODE", row, parm.getValue("ORDER_CODE"));// 医嘱代码	
		TablePublicTool.modifyRow(orderT, row);
		TablePublicTool.modifyRow(orderT, row, 3, "ORDER_DESC", 
				orderParmValue.getValue("ORDER_DESC", row), parm.getValue("ORDER_DESC")+ " "+parm.getValue("SPECIFICATION"));// 医嘱描述	
		orderParmValue.setData("CAT1_TYPE", row, parm.getValue("CAT1_TYPE"));// 医嘱大类型
		TablePublicTool.modifyRow(orderT, row);
		orderParmValue.setData("ORDER_CAT1_CODE", row, parm.getValue("ORDER_CAT1_CODE"));// 医嘱小类型
		TablePublicTool.modifyRow(orderT, row);	
		TablePublicTool.modifyRow(orderT, row, 9, "NOTE_DATE", 
				orderParmValue.getValue("NOTE_DATE", row), SystemTool.getInstance().getDate());// 日期

		if ("PHA".equals(parm.getValue("CAT1_TYPE"))) {
			TParm action = new TParm();
			action.setData("ORDER_CODE", parm.getValue("ORDER_CODE"));
			TParm result = OdiMainTool.getInstance().queryPhaBase(action);
			TablePublicTool.modifyRow(orderT, row, 4, "MEDI_QTY", 
					orderParmValue.getValue("MEDI_QTY", row), result.getValue("MEDI_QTY", 0));// 数量
			TablePublicTool.modifyRow(orderT, row, 5, "MEDI_UNIT", 
					orderParmValue.getValue("MEDI_UNIT", row), result.getValue("MEDI_UNIT", 0));// 单位
			TablePublicTool.modifyRow(orderT, row, 6, "ROUTE_CODE", 
					orderParmValue.getValue("ROUTE_CODE", row), result.getValue("ROUTE_CODE", 0));// 用法
		}else{
			TablePublicTool.modifyRow(orderT, row, 4, "MEDI_QTY", 
					orderParmValue.getValue("MEDI_QTY", row), 1);// 数量
			TablePublicTool.modifyRow(orderT, row, 5, "MEDI_UNIT", 
					orderParmValue.getValue("MEDI_UNIT", row), parm.getData("UNIT_CODE"));// 单位
			TablePublicTool.modifyRow(orderT, row, 6, "ROUTE_CODE", 
					orderParmValue.getValue("ROUTE_CODE", row), "");// 用法
		}		
		if(!isNew(orderParmValue)){
			int seqNo = getMaxSeqNo(triageNo);
			if(seqNo<0){
				this.messageBox("err seqNo");
				// tableOrder加锁
				this.lock();
				return;
			}			
			int newR = onNewOrder(triageNo, seqNo);
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
	 * 解锁
	 * @param table
	 */
	public void unlock(TTable table){
		table.setLockCellMap(new HashMap());
	}
	
	/**
	 * orderT加锁
	 */
	public void lock(){
		TParm parm = orderT.getParmValue();
		if(parm == null){
			this.messageBox("parm is null");
			return;
		}
		for(int i=0; i<parm.getCount(); i++){
			// 有医嘱的行， 医嘱名称不可编辑
			if(parm.getValue("ORDER_CODE", i) != null 
					&& parm.getValue("ORDER_CODE", i).trim().length()>0){
				orderT.setLockCell(i, 3, true);
			}
			// 连嘱细项， 用法不可编辑
			if((!(parm.getValue("LINKMAIN_FLG", i) != null 
					&& parm.getValue("LINKMAIN_FLG", i).equals("Y"))) 
					&& parm.getValue("LINK_NO", i) != null 
					&& parm.getValue("LINK_NO", i).trim().length()>0 ){
				orderT.setLockCell(i, 6, true);
			}	
			// 护士已签名的，除了选择框，都不可以编辑
			if(parm.getValue("SIGN_NS", i) != null 
					&& parm.getValue("SIGN_NS", i).trim().length()>0){
				for(int j=1; j<orderT.getColumnCount(); j++){
					orderT.setLockCell(i, j, true);
				}
			}
			// 医生已签名的，都不可以编辑
			if(parm.getValue("SIGN_DR", i) != null 
					&& parm.getValue("SIGN_DR", i).trim().length()>0){
				for(int j=0; j<orderT.getColumnCount(); j++){
					orderT.setLockCell(i, j, true);
				}
			}
			// 空行不可以勾选
			if(parm.getValue("ORDER_CODE", i) == null 
					|| parm.getValue("ORDER_CODE", i).trim().length()==0){
				orderT.setLockCell(i, 0, true);
			}		
		}	
	}

	/**
	 * 
	 * 校验医嘱是否已启用
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
	 * 全选
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
	 * 测试
	 */
	public void onTest(){
		Map map = orderT.getLockCellMap();
		System.out.println("======lock:"+map.get("6:0"));
		Boolean boolean1 = (Boolean) map.get("6:0");
	}
	
	
	
}
