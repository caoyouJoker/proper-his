package com.javahis.ui.spc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jdo.spc.SPCInStoreTool;
import jdo.spc.SPCPoisonTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
/**
 * <p>
 * Title:麻精药符码打包(住院药房入库)
 * </p>
 * 
 * <p>
 * Description:麻精药符码打包(住院药房入库)
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author fuwj 2012.10.23
 * @version 1.0
 */
public class SPCPoisonControl extends TControl {

	private String action = "save";

	// 主项表格
	private TTable TABLE_D, TABLE_M;
	
	/**
	 * 构造器
	 */
	public SPCPoisonControl() {
		super();
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 初始画面数据
		TABLE_D = getTable("TABLE_D");
		TABLE_M = getTable("TABLE_M");
		TABLE_D.addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE, this,
				"onOrderValueChange");
	}

	/**
	 * 得到Table对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * 扫描出库单号
	 */
	public void onClick() {
		TABLE_M = getTable("TABLE_M");
		TABLE_M.removeRowAll();
		TParm t = new TParm();
		String code = this.getValueString("ORDER_NO");
		t.setData("ORDER_NO", (String) code);
		String orderNo = this.getValueString("ORDER_NO");
		if ("".equals(code)) {
			this.messageBox("出库单号 不能为空");
			return;
		}
		TParm result = SPCPoisonTool.getInstance().queryInfo(t);
		if (result.getCount() <= 0) {
			this.messageBox("出库单号错误");
		}
		TABLE_M.setParmValue(result);
		getTextField("BOX_ESL_ID").grabFocus();			
		onTableClicked();
	}

	/**
	 * 点击主Table触发
	 */
	public void onTableClicked() {
		int row = TABLE_M.getSelectedRow();
		// add by wangbin 20141118 解决数组越界的异常 START
		if (row < 0) {
			return;
		}
		// add by wangbin 20141118 解决数组越界的异常 END
		TParm rowParm = TABLE_M.getParmValue();
		int qty = rowParm.getInt("QTY", row);
		int ACUM_PACK_QTY = rowParm.getInt("ACUM_PACK_QTY", row);
		String disNo = (String) rowParm.getData("DISPENSE_NO", row);
		int seqNo = rowParm.getInt("SEQNO", row);
		TParm parm = new TParm();
		parm.setData("DISPENSE_NO", disNo);
		parm.setData("DISPENSE_SEQ_NO", seqNo);
		TParm result = SPCPoisonTool.getInstance().queryYdb(parm);
		TABLE_D.removeRowAll();
		TABLE_D.setParmValue(result);
		if (qty != ACUM_PACK_QTY) {
			TABLE_D.addRow();
		}
	}

	/**
	 * 扫描容器
	 */
	public void conCilck() {
		int row = TABLE_D.getSelectedRow();
		if (row != -1) {
			TParm parm = TABLE_D.getParmValue();
			String containerNo = (String) parm.getData("CONTAINER_ID", row);
			TParm tparm = new TParm();
			tparm.setData("CONTAINER_ID", containerNo);
			TParm result = SPCPoisonTool.getInstance().queryContainerNum(tparm);
			if (result.getCount() > 0) {
				this.messageBox("此容器已经被使用");
				onTableClicked();
				return;
			}
		}
	}

	/**
	 * 子Table值改变触发
	 * 
	 * @param tNode
	 * @return
	 */
	public boolean onOrderValueChange(TTableNode tNode) {
		int rowNum = TABLE_M.getSelectedRow();
		String orderCode = TABLE_M.getItemString(rowNum, "ORDER_CODE");
		TParm rowParm = TABLE_M.getParmValue();
		int sjNum = rowParm.getInt("QTY", rowNum)
				- rowParm.getInt("ACUM_PACK_QTY", rowNum);
		int column = tNode.getColumn();
		String colName = TABLE_D.getParmMap(column);
		String noString = "";
		if ("CONTAINER_ID".equalsIgnoreCase(colName)) {
			noString = (String) tNode.getValue();
		}
		int row = TABLE_D.getSelectedRow();
		if (row != -1) { 
			TParm parm = TABLE_D.getParmValue();
			String containerNo = noString;
			TParm tparm = new TParm();
			tparm.setData("CONTAINER_ID", containerNo);
			TParm result = SPCPoisonTool.getInstance().queryContainerNum(tparm);
			if (result.getCount() > 0) {
				// add by wangbin 20141117 如果发现该容器没有解除绑定则自动解除 START
				result = SPCPoisonTool.getInstance().deleteContainerWithToxic(tparm);
				if (result.getErrCode() < 0) {
					this.messageBox("解除麻精盒子与麻精药品的绑定关系时发生异常");
					err("ERR:" + result.getErrCode() + result.getErrText());
					return true;
				}
				
				result = SPCPoisonTool.getInstance().deleteContainerWithCabinet(tparm);
				if (result.getErrCode() < 0) {
					this.messageBox("解除麻精盒子与智能柜的绑定关系时发生异常");
					err("ERR:" + result.getErrCode() + result.getErrText());
					return true;
				}
				// add by wangbin 20141117 如果发现该容器没有解除绑定则自动解除 END
			}
			result = SPCPoisonTool.getInstance().queryContainerCount(tparm);
			if (result.getCount() <= 0) {
				this.messageBox("容器编号错误");
				return true;
			}
			String containerDesc = (String) result.getData("CONTAINER_DESC", 0);
			int TOXIC_QTY = result.getInt("TOXIC_QTY", 0);
			String queryCode = (String) result.getData("ORDER_CODE", 0);
			if (!orderCode.equals(queryCode)) {
				this.messageBox("容器类型不符，请更换容器");
				return true;
			}
			int num = 0;
			if (sjNum > TOXIC_QTY) {
				num = TOXIC_QTY;
			} else {
				num = sjNum;
			}
			List idList = new ArrayList();
			for (int i = 0; i < num; i++) {				
				String rfid = SPCInStoreTool.getInstance().getToxic();			
				idList.add(rfid);					
			}
			String boxId = this.getValueString("BOX_ESL_ID");
			String orderDesc = (String) rowParm.getData("ORDER_DESC", rowNum);
			int ACUM_PACK_QTY = rowParm.getInt("ACUM_PACK_QTY", rowNum) + num;
			String dispenseNo = (String) rowParm.getData("DISPENSE_NO", rowNum);
			int SEQNO = rowParm.getInt("SEQNO", rowNum);
			String batchNo = (String) rowParm.getData("BATCH_NO", rowNum);
			int batchSeq = rowParm.getInt("BATCH_SEQ", rowNum);
			double price = rowParm.getDouble("VERIFYIN_PRICE", rowNum);
			String unitCodeString = (String) rowParm.getData("UNIT_CODE",
					rowNum);
			Date vailDate = (Date) rowParm.getData("VALID_DATE", rowNum);
			String supCode = rowParm.getValue("SUP_CODE",rowNum);
			String supOrderCode = rowParm.getValue("SUP_ORDER_CODE",rowNum);
			TParm insertParm = new TParm();
			insertParm.setData("BOX_ESL_ID",boxId);
			insertParm.setData("DISPENSE_SEQ_NO", SEQNO);
			insertParm.setData("SEQ_NO", SEQNO);
			insertParm.setData("DISPENSE_NO", dispenseNo);
			insertParm.setData("ACUM_PACK_QTY", ACUM_PACK_QTY);
			insertParm.setData("CONTAINER_ID", containerNo);
			insertParm.setData("ORDER_CODE", orderCode);
			insertParm.setData("BATCH_NO", batchNo);
			insertParm.setData("VALID_DATE", vailDate);
			insertParm.setData("BATCH_SEQ", batchSeq);
			insertParm.setData("VERIFYIN_PRICE", price);
			insertParm.setData("UNIT_CODE", unitCodeString);
			insertParm.setData("idList", idList);
			insertParm.setData("OPT_USER", Operator.getID());
			insertParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
			insertParm.setData("IS_BOXED", "Y");   
			insertParm.setData("BOXED_USER", Operator.getID());
			insertParm.setData("BOXED_DATE", SystemTool.getInstance().getDate());
			insertParm.setData("OPT_TERM", Operator.getIP());
			insertParm.setData("CABINET_ID", " ");   
			insertParm.setData("CONTAINER_DESC", containerDesc);		
			insertParm.setData("IS_PACK", "Y");
			insertParm.setData("PACK_USER", Operator.getID());
			insertParm.setData("PACK_DATE", SystemTool.getInstance().getDate());		
			insertParm.setData("SUP_CODE", supCode);		
			insertParm.setData("SUP_ORDER_CODE",supOrderCode);
			String oldValueString = (String) tNode.getOldValue();
			if ("".equals(oldValueString)) {
				result = TIOM_AppServer.executeAction(
						"action.spc.SPCPoisonAction", "insertContainer",
						insertParm);
				if (result.getErrCode() < 0) {
					this.messageBox("打包失败");
					return true; 
				}  
				String serialString = idList.get(0) + "~"
						+ idList.get(idList.size() - 1);
				TABLE_D.setItem(row, "CONTAINER_DESC", containerDesc);
				TABLE_D.setItem(row, "TOXIC_QTY", TOXIC_QTY);
				TABLE_D.setItem(row, "SJ_QTY", num);
				TABLE_D.setItem(row, "SERIAL", serialString);
				TABLE_M.setItem(rowNum, "ACUM_PACK_QTY", rowParm.getInt(
						"ACUM_PACK_QTY", rowNum)
						+ num);   
				if (rowParm.getInt("ACUM_PACK_QTY", rowNum) != rowParm.getInt(
						"QTY", rowNum)) {
					TABLE_D.addRow();
				}  
				TParm printData = new TParm();
				
				/*for(int i=0;i<idList.size();i++) {
				    if(i%2==0) {		
				    	printData.setData("BAR_CODE","TEXT",(String)idList.get(i));
				    	printData.setData("ORDER_DESC","TEXT",orderDesc+" "+(String)idList.get(i));
				    	if(i<idList.size()-1)  {
					    	printData.setData("BAR_CODE1","TEXT",(String)idList.get(i+1));
					    	printData.setData("ORDER_DESC1","TEXT",orderDesc+" "+(String)idList.get(i+1));
				    	}								
				    	this.openPrintWindow("%ROOT%\\config\\prt\\ind\\mjbar.jhw",
								printData,true);     
				    }				
				}	*/		
				for(int i=0;i<idList.size();i++) {
				    	printData.setData("BAR_CODE","TEXT",(String)idList.get(i));
				    	printData.setData("ORDER_DESC","TEXT",orderDesc+""+(String)idList.get(i));	
				    	this.openPrintWindow("%ROOT%\\config\\prt\\ind\\mjbar.jhw",
								printData,true);    
				}	   													
									  		
			} else {
				TParm updateParm = new TParm();
				updateParm.setData("CONTAINER_ID", oldValueString);
				insertParm.setData("OLD_ID", oldValueString);
				result = SPCPoisonTool.getInstance().queryContainerCount(
						updateParm);
				if (result.getCount() <= 0) {
					this.messageBox("容器编号错误");
					return true;
				}
				int OLD_TOXIC_QTY = result.getInt("TOXIC_QTY", 0);
				if (OLD_TOXIC_QTY != TOXIC_QTY) {
					this.messageBox("两容器的容量不符,请更换容器");
					return true;
				}
				if (this.messageBox("更新", "确定是否更换容器", 2) == 0) {
					result = TIOM_AppServer.executeAction(
							"action.spc.SPCPoisonAction", "updateContainer",
							insertParm);
					if (result.getErrCode() < 0) {
						this.messageBox("更换容器失败");
						return true;
					}   
					TABLE_D.setItem(row, "CONTAINER_DESC", containerDesc);
					this.messageBox("更换容器成功");
				}

			}
		}
		return false;
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		for(int i=0;i<1;i++) {
			TParm printData = new TParm(); 
			printData.setData("BAR_CODE","TEXT","00000001");
			printData.setData("ORDER_DESC","TEXT","盐酸吗啡溶液 00000001");
			this.openPrintWindow("%ROOT%\\config\\prt\\ind\\mjbar.jhw",
					printData);		
		}
	

	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		TABLE_D.removeRowAll();
		TABLE_M.removeRowAll();
		this.setValue("ORDER_NO", "");
	}
	
	
	/**             
	 * 扫描周转箱事件   
	 */
	public void boxClick() {
		this.getTextField("BOX_ESL_ID").removeAll();           
		EleTagControl.getInstance().login();
		EleTagControl.getInstance().sendEleTag(this.getValueString("BOX_ESL_ID"), "住院药房", "", "", 10);
			  				                                                                   
		TABLE_M.setSelectedRow(0);			
		onTableClicked();	  
		
	}
	
}
