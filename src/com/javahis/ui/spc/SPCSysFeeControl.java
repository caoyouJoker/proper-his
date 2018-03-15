package com.javahis.ui.spc;

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import jdo.spc.SPCSysFeeTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;
                                           
/**
 * <p>
 * Title:药品编码比对
 * </p>
 * 
 * <p>
 * Description:药品编码比对
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
 * @author fuwj 2012.12.20
 * @version 1.0
 */
public class SPCSysFeeControl extends TControl {

	private TTable talbe;

	private String action = "save";

	/**
	 * 构造器
	 */
	public SPCSysFeeControl() {
		super();
		// this.messageBox("2222");
		/*
		 * talbe = this.getTable("TABLE_M"); talbe.addEventListener("TABLE_M->"
		 * + TTableEvent.CHANGE_VALUE, this, "onOrderValueChange");
		 */
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 初始画面数据
		initPage();
	}

	/**
	 * 初始画面数据
	 */
	private void initPage() {
		// 设置弹出菜单
		callFunction("UI|DELETE|setEnabled", false);
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
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
	 * 导入药品对照表
	 */
	public void onInsertPatByExl() {
		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showOpenDialog(null);
		TParm parm = new TParm();
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				Workbook wb = Workbook.getWorkbook(file);
				Sheet st = wb.getSheet(0);
				int row = st.getRows();
				int column = st.getColumns();
				StringBuffer wrongMsg = new StringBuffer();
				String id = "";
				int count = 0;
				for (int i = 1; i < row; i++) {

					String hisOrderCode = st.getCell(0, i).getContents().trim();
					String orderCode = st.getCell(1, i).getContents().trim();
					if ("".equals(hisOrderCode) || "".equals(orderCode)) {
						continue;
					}
					TParm searchParm = new TParm();
					searchParm.setData("HIS_ORDER_CODE", hisOrderCode);
					/*TParm result = SPCSysFeeTool.getInstance().queryInfo(
							searchParm);
					if (result.getErrCode() < 0 || result.getCount() <= 0) {
						err(result.getErrCode() + " " + result.getErrText());
						continue;
					}
					String hisOrderDesc = "";
					if (result.getData("ORDER_DESC", 0) != null) {
						hisOrderDesc = (String) result.getData("ORDER_DESC", 0);
					}
					String hisSpecification = "";
					if (result.getData("SPECIFICATION", 0) != null) {
						hisSpecification = (String) result.getData(
								"SPECIFICATION", 0);
					}
					String hisGoodDesc = "";
					if (result.getData("GOODS_DESC", 0) != null) {
						hisGoodDesc = (String) result.getData("GOODS_DESC", 0);
					}*/

					parm.addData("ORDER_CODE", orderCode);
					// 医院药品编码
					parm.addData("HIS_ORDER_CODE", hisOrderCode); 

					// 医院药品名称
				//	parm.addData("HIS_ORDER_DESC", hisOrderDesc);
				//	parm.addData("HIS_SPECIFICATION", hisSpecification);
				//	parm.addData("HIS_GOODS_DESC", hisGoodDesc);
					parm.addData("OPT_USER", Operator.getID());
					parm.addData("OPT_TERM", Operator.getIP());
					parm.setData("IS_UPDATE", "Y");
					count++;
				}
				parm.setCount(count);
				// int count = parm.getCount();
				if (count < 1) {
					this.messageBox("导入数据失败");
					return;
				}
				TParm result = TIOM_AppServer.executeAction(
						"action.spc.SPCSysFeeAction", "updateSysFeeAll", parm);
				if (result.getErrCode() < 0) {
					this.messageBox("更新失败");
					return;
				}
				this.messageBox("更新成功");
			} catch (BiffException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void onQuery() {
		TParm tparm = new TParm();
		String ORDER_CODE = this.getValueString("ORDER_CODE");
		if (!"".equals(ORDER_CODE) && ORDER_CODE != null) {
			tparm.setData("ORDER_CODE", ORDER_CODE);
		}
		tparm.setData("REGION_CODE", "H01");
		TParm result = SPCSysFeeTool.getInstance().querySpcSysFee(tparm);
		TTable talbe = this.getTable("TABLE_M");
		talbe.setParmValue(result);
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
	 * 接受返回值方法
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code)) {
			getTextField("ORDER_CODE").setValue(order_code);
		}
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc)) {
			getTextField("ORDER_DESC").setValue(order_desc);
		}
	}

	/**
	 * 主表点击事件
	 */
	public void onTableClicked() {
		TTable t = this.getTable("TABLE_M");
		int row = t.getSelectedRow();
		if (row != -1) {
			TParm parm = t.getParmValue();
			this.setValue("ORDER_CODE", parm.getData("ORDER_CODE", row));
			this.setValue("ORDER_DESC", parm.getData("ORDER_DESC", row));
			this.setValue("GOODS_DESC", parm.getData("GOODS_DESC", row));				
			this.setValue("SPECIFICATION", parm.getData("SPECIFICATION", row));
			this
					.setValue("HIS_ORDER_CODE", parm.getData("HIS_ORDER_CODE",
							row));
			callFunction("UI|DELETE|setEnabled", true);
			action = "update";
		}
	}
	
	/**
	 * 保存操作
	 */
	public void onSave() {
		TParm parm = new TParm();
		parm.setData("REGION_CODE","H01");   
		parm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
		parm.setData("ORDER_DESC", getValueString("ORDER_DESC"));
		parm.setData("GOODS_DESC", getValueString("GOODS_DESC"));
		parm.setData("SPECIFICATION", getValueString("SPECIFICATION"));
		parm.setData("HIS_ORDER_CODE", getValueString("HIS_ORDER_CODE"));
		parm.setData("OPT_USER", Operator.getID());  
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM", Operator.getIP());
		TParm result;
		if ("save".equals(action)) {
			if (!CheckSaveData()) {
				return;  
			}
			result = SPCSysFeeTool.getInstance().insertSysFeeSpc(parm);
			if(result.getErrCode()<0) {
				this.messageBox("添加数据失败");   
				return;
			}
			this.setValue("ORDER_CODE", "");
			this.setValue("ORDER_DESC", "");
			this.setValue("GOODS_DESC", "");
			this.setValue("SPECIFICATION", "");
			this.setValue("HIS_ORDER_CODE", "");
			callFunction("UI|DELETE|setEnabled", false);
			this.messageBox("保存成功");
			onQuery();
		} else if ("update".equals(action)) {
			if (!CheckUpdateData()) {
				return;
			}
			result = SPCSysFeeTool.getInstance().updateSysFeeSpc(parm);
			if(result.getErrCode()<0) {
				this.messageBox("更新数据失败");  
				return;
			}
			this.setValue("ORDER_CODE", "");
			this.setValue("ORDER_DESC", "");
			this.setValue("GOODS_DESC", "");
			this.setValue("SPECIFICATION", "");
			this.setValue("HIS_ORDER_CODE", "");
			callFunction("UI|DELETE|setEnabled", false);
			this.messageBox("更新成功");
			onQuery();
		}
	}
	
	/**
	 * 检核添加数据
	 * @return
	 */
	private boolean CheckSaveData() {
		if ("".equals(getValueString("ORDER_CODE"))) {
			this.messageBox("物联网药品编码不能为空");
			return false;
		}
		if ("".equals(getValueString("HIS_ORDER_CODE"))) {
			this.messageBox("HIS药品编码不能为空");
			return false;
		}
		TParm searchParm = new TParm();
		searchParm.setData("REGION_CODE","H01");
		searchParm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
		TParm result = SPCSysFeeTool.getInstance().querySpcSysFee(searchParm);
		if (result.getErrCode() < 0 || result.getCount() > 0) {
			this.messageBox("该物联网编码已经存在");
			return false;
		}      
		TParm searchParm1 = new TParm();
		searchParm1.setData("REGION_CODE","H01");
		searchParm1.setData("HIS_ORDER_CODE", getValueString("HIS_ORDER_CODE"));
		result = SPCSysFeeTool.getInstance().querySpcSysFee(searchParm1);
		if (result.getErrCode() < 0 || result.getCount() > 0) {
			this.messageBox("该HIS编码已经存在");
			return false;
		}
		return true;  
	}
	
	/**
	 * 检核更新数据
	 * @return
	 */
	private boolean CheckUpdateData() {
		if ("".equals(getValueString("ORDER_CODE"))) {
			this.messageBox("物联网药品编码不能为空");
			return false;
		}
		if ("".equals(getValueString("HIS_ORDER_CODE"))) {
			this.messageBox("HIS药品编码不能为空");
			return false;
		}
	/*	TParm searchParm = new TParm();
		searchParm.setData("REGION_CODE","H01");
		searchParm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
		searchParm.setData("HIS_ORDER_CODE", getValueString("HIS_ORDER_CODE"));
		TParm result = SPCSysFeeTool.getInstance().querySpcSysFee(searchParm);
		if (result.getErrCode() < 0 || result.getCount() > 0) {
			this.messageBox("该编码对应关系已存在");
			return false;
		}    */
		return true;
	}
	
	/**
	 * 清空操作
	 */
	public void onClear() {
		this.setValue("ORDER_CODE", "");
		this.setValue("ORDER_DESC", "");
		this.setValue("GOODS_DESC", "");
		this.setValue("SPECIFICATION", "");
		this.setValue("HIS_ORDER_CODE", "");
		TTable t = this.getTable("TABLE_M");
		t.removeRowAll();
		action = "save";
	}
	
	/**
	 * 数据删除操作
	 */
	public void onDelete() {
		if (this.messageBox("询问", "确定删除?", 0) == 1) {
			return;
		}
		if (this.getValue("ORDER_CODE").equals("")
				|| this.getValue("ORDER_CODE").equals(null)) {
			this.messageBox("请选择要删除的项！");
			return;
		}
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
		parm.setData("HIS_ORDER_CODE", getValueString("HIS_ORDER_CODE"));
		TParm result = SPCSysFeeTool.getInstance().deleteSysFeeSpc(parm);
		if(result.getErrCode()<0) {
			err(result.getErrCode() + " " + result.getErrText());
			this.messageBox("删除数据失败");
			return;
		}
		this.setValue("ORDER_CODE", "");
		this.setValue("ORDER_DESC", "");
		this.setValue("GOODS_DESC", "");
		this.setValue("SPECIFICATION", "");
		this.setValue("HIS_ORDER_CODE", "");
		callFunction("UI|DELETE|setEnabled", false);
		this.messageBox("删除成功！");
		onQuery();
	}

}
