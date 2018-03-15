package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.StringUtils;


import jdo.spc.SPCContainerTool;
import jdo.spc.SPCInStoreTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.javahis.util.RFIDPrintUtils;
import com.javahis.util.StringUtil;


/**
 * <p>
 * Title: 毒麻容器Control
 * </p>
 * 
 * <p>
 * Description: 毒麻容器Control
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
 * @author:　fuwj 20121024
 * @version 1.0
 */

public class SPCContainerControl extends TControl {

	private String action = "save";
	
	private static String hexString = "0123456789ABCDEF";

	// 主项表格
	private TTable table;

	public SPCContainerControl() {
		super();
	}
	
	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 初始画面数据
		initPage();
	}

	/**
	 * 保存方法
	 */
	public void onSave() {
		Timestamp date = StringTool.getTimestamp(new Date());
		TTextField combo = getTextField("CONTAINER_ID");
		TParm parm = new TParm();    
		parm.setData("CONTAINER_DESC", getValueString("CONTAINER_DESC"));
		parm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
		parm.setData("RFID_ID", getValueString("RFID_ID"));
		parm.setData("TOXIC_QTY", getValueString("TOXIC_QTY"));
		parm.setData("ORDER_DESC", getValueString("ORDER_DESC"));
		parm.setData("PY_CODE", getValueString("PY_CODE"));
		parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", date);
		parm.setData("OPT_TERM", Operator.getIP());
		TParm result = new TParm();
		result = SPCContainerTool.getInstance().queryISMj(parm);
		if (result.getCount() <= 0) {
			this.messageBox("请选择麻禁药品");
			return;
		}
		if (!CheckData()) {
			return;   
		}
		if ("".equals(this.getValueString("CONTAINER_ID"))||this.getValueString("CONTAINER_ID")==null) {
			String id = SPCInStoreTool.getInstance().getToxBox();
			// modify by wangbin 20140721
			// 取得药品对应的供应商编码
			TParm supOrderCodeParm = SPCContainerTool.getInstance().querySupOrderCode(getValueString("ORDER_CODE"));
			if (StringUtils.isEmpty(supOrderCodeParm.getValue("SUP_ORDER_CODE", 0))) {
				this.messageBox("获取供应商编码错误");
				return;
			} else {
				String containerId = supOrderCodeParm.getValue("SUP_ORDER_CODE", 0) + id;
				parm.setData("CONTAINER_ID", containerId);
				result = SPCContainerTool.getInstance().insertInfo(parm);
				this.messageBox("新建成功！");				
				onClear();
				initPage(); 
			}
		} else {
			parm.setData("CONTAINER_ID", this.getValueString("CONTAINER_ID"));
			result = SPCContainerTool.getInstance().updateInfo(parm);
			onClear();
			initPage();			
			this.messageBox("修改成功！");
		}
		if (result.getErrCode() < 0) {
			this.messageBox("错误！", result.getErrText(), -1);
			return;
		}
	//	getTextField("TOXIC_ID").setEnabled(false);
		onQuery();                      
	}

	/**
	 * 容器ID回车事件
	 */
	public void onContainId() {
		  
	}

	/**
	 * 主表点击事件
	 */
	public void onTableClicked() {
		TTable t = this.getTable("tTable_0");
		int row = t.getSelectedRow();

		if (row != -1) {
			TParm parm = t.getParmValue();
			this.setValue("CONTAINER_ID", parm.getData("CONTAINER_ID", row));
			this
					.setValue("CONTAINER_DESC", parm.getData("CONTAINER_DESC",
							row));
			this.setValue("ORDER_CODE", parm.getData("ORDER_CODE", row));
			// this.setValue("RFID_ID", parm.getData("RFID_ID", row));
			this.setValue("PY_CODE", parm.getData("PY_CODE", row));
			this.setValue("DESCRIPTION", parm.getData("DESCRIPTION", row));

			this.setValue("TOXIC_QTY", parm.getData("TOXIC_QTY", row));
			this.setValue("ORDER_DESC", parm.getData("ORDER_DESC", row));
			callFunction("UI|DELETE|setEnabled", true);
			getTextField("CONTAINER_ID").setEnabled(false);
			TTable td = this.getTable("tTable_1");
			TParm p = new TParm();
			p.setData("CONTAINER_ID", parm.getData("CONTAINER_ID", row));
			TParm tParm = SPCContainerTool.getInstance().queryMxInfo(p);
			td.setParmValue(tParm);
			action = "save";
		}
	}

	/**
	 * 删除方法
	 */
	public void onDelete() {
		
		
		if (this.messageBox("询问", "确定删除?", 0) == 1) {
			return;
		}
		if (this.getValue("CONTAINER_ID").equals("")
				|| this.getValue("CONTAINER_ID").equals(null)) {
			this.messageBox("请选择要删除的项!");
			return;
		}
		String containerId = this.getValueString("CONTAINER_ID");
		TParm parm = new TParm();
		parm.setData("CONTAINER_ID", this.getValue("CONTAINER_ID"));
		TParm result = SPCContainerTool.getInstance().queryMxInfo(parm);
		if (result.getCount() > 0) {
			this.messageBox("此容器已使用不可删除");
			return;
		}
		result = SPCContainerTool.getInstance().deleteInfo(parm);
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return;
		}
		this.messageBox("删除成功！");
		//getTextField("TOXIC_ID").setEnabled(false);
		onClear();
		initPage();
		onQuery();
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		table = getTable("tTable_0");
		table.removeRowAll();
		String CONTAINER_ID = this.getValueString("CONTAINER_ID");
		String ORDER_CODE = this.getValueString("ORDER_CODE");
		String CABINET_ID = this.getValueString("CABINET_ID");
		TParm parm = new TParm();
		if (!"".equals(CONTAINER_ID) && CONTAINER_ID != null) {
			parm.setData("CONTAINER_ID", CONTAINER_ID);
		}
		if (!"".equals(ORDER_CODE) && ORDER_CODE != null) {
			parm.setData("ORDER_CODE", ORDER_CODE);
		}
		if (!"".equals(CABINET_ID) && CABINET_ID != null) {
			parm.setData("CABINET_ID", CABINET_ID);
		}
		TParm tParm = SPCContainerTool.getInstance().queryInfo(parm);
		table.setParmValue(tParm);
	}

	/**
	 * 清空方法
	 */
	public void onClear() {		
		table.removeRowAll();
		String tags = "CONTAINER_ID;CONTAINER_DESC;ORDER_CODE;RFID_ID;TOXIC_QTY;ORDER_DESC;PY_CODE;DESCRIPTION;RFID;CABINET_ID";
		clearValue(tags);
		getTextField("CONTAINER_ID").setEnabled(false);
		TTable td = this.getTable("tTable_1");
		td.removeRowAll();
	}

	/**
	 * 料位名称回车事件
	 */
	public void onMaterialAction() {	
		String name = getValueString("MATERIAL_CHN_DESC");
		if (name.length() > 0)
			setValue("PY1", TMessage.getPy(name));
		((TTextField) getComponent("MATERIAL_ENG_DESC")).grabFocus();
	}

	/**
	 * 初始画面数据
	 */
	private void initPage() {
		/*
		 * table = getTable("tTable_0"); table.removeRowAll(); TParm t = new
		 * TParm(); TParm tParm = SPCContainerTool.getInstance().queryInfo(t);
		 * table.setParmValue(tParm);
		 */
		table = getTable("tTable_0");
		callFunction("UI|DELETE|setEnabled", false);
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");		
		// 设置弹出菜单
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
	 * 得到ComboBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * 得到TextFormat对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
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
	 * 检查数据
	 */
	private boolean CheckData() {
		/*if ("".equals(getValueString("CONTAINER_ID"))) {
			this.messageBox("容器编号不能为空");
			return false;
		}*/
		if ("".equals(getValueString("CONTAINER_DESC"))) {
			this.messageBox("容器名称不能为空");
			return false; 			
		}
		if ("".equals(getValueString("ORDER_CODE"))) {
			this.messageBox("药品代码不能为空");
			return false;
		}
		if (this.getValueInt("TOXIC_QTY") <= 0) {
			this.messageBox("数量必须大于0");
			return false;
		}
		return true;
	}

	public void onClick() {
		TTable t = this.getTable("tTable_0");
		int row = t.getSelectedRow();
		if (row == -1) {
			this.messageBox("请选择容器");
			return;
		}
		TParm parm = t.getParmValue();
		String orderCode = (String) parm.getData("ORDER_CODE", row);
		String toxicId = this.getValueString("TOXIC_ID");
		TParm tparm = new TParm();
		tparm.setData("TOXIC_ID", toxicId);
		TParm result = SPCContainerTool.getInstance().queryMXByToxicId(tparm);
		if (result.getCount() <= 0) {
			this.messageBox("此麻禁药未符码");
			return;
		}
		String reOrderCode = (String) result.getData("ORDER_CODE", 0);
		if (!orderCode.equals(reOrderCode)) {
			this.messageBox("容器与麻禁药不匹配");
			return;
		}
		String containerId = this.getValueString("CONTAINER_ID");
		tparm.setData("CONTAINER_ID", containerId);
		result = SPCContainerTool.getInstance().updateOrderCode(tparm);
		if (result.getErrCode() < 0) {
			this.messageBox("错误！", result.getErrText(), -1);
			return;
		}
		TTable td = this.getTable("tTable_1");
		td.removeRowAll();
		result = SPCContainerTool.getInstance().queryMxInfo(tparm);
		td.setParmValue(result);
		getTextField("TOXIC_ID").setEnabled(true);
	}

	/**
	 * ManDesc回车事件
	 */
	public void onManDescAction() {
		String py = TMessage.getPy(this.getValueString("CONTAINER_DESC"));
		setValue("PY_CODE", py);
	}
	
	/**
	 * 打印黏标
	 */
	public void onPrint() {
		/*TTable t = this.getTable("tTable_0");
		int row = t.getSelectedRow();
		if (row == -1) {
			this.messageBox("请选择要打印黏标的容器");			
			return;    
		}
		String containerId = t.getItemString(row, "CONTAINER_ID");
		String containerName = t.getItemString(row, "CONTAINER_DESC");
		String orderName = t.getItemString(row, "ORDER_DESC");
		String specification = t.getItemString(row, "SPECIFICATION");		
		TParm printData = new TParm();
		printData.setData("BAR_CODE", "TEXT", containerId);
		printData.setData("ORDER_DESC", "TEXT",orderName);
		printData.setData("CONTAINER_CODE", "TEXT", containerName);				
		printData.setData("SPECIFICATION", "TEXT", specification);	  
		this.openPrintWindow("%ROOT%\\config\\prt\\spc\\SPCStockM.jhw",
				printData);*/
		TTable t = this.getTable("tTable_0");
		int row = t.getSelectedRow();
		if (row == -1) {
			this.messageBox("请选择要打印黏标的容器");				
			return;
		}
		String containerId = t.getItemString(row, "CONTAINER_ID");
		String containerName = t.getItemString(row, "CONTAINER_DESC");
		String orderName = t.getItemString(row, "ORDER_DESC");
		String specification = t.getItemString(row, "SPECIFICATION");
		String num = t.getItemString(row, "TOXIC_QTY");
		TParm parm = new TParm();	
		parm.setData(RFIDPrintUtils.PARM_NAME, containerName);
		parm.setData(RFIDPrintUtils.PARM_SPEC, specification);	             							
		// 十进制						   
  
		parm.setData(RFIDPrintUtils.PARM_CODE, containerId);

		// 需要转化(看一下应用制作模版是否可以设置)				  		
		// 十六进制
		parm.setData(RFIDPrintUtils.PARM_PRFID, containerId);
		
		parm.setData(RFIDPrintUtils.PARM_NUM, num);			
					
		// 单个打印
		RFIDPrintUtils.send2LPTToxic(parm);				
	}
	
/*	*//**
	 * 补打麻精药黏标
	 *//*
	public void onBarcode() {
		TTable t = this.getTable("tTable_1");
		int row = t.getSelectedRow();
		if (row == -1) {
			this.messageBox("请选择要打印的麻精药品");
			return;
		}
		String toxicId = t.getItemString(row, "TOXIC_ID");
		String orderName = t.getItemString(row, "ORDER_DESC");
		TParm printData = new TParm();
		printData.setData("BAR_CODE", "TEXT", toxicId);
		printData.setData("ORDER_DESC", "TEXT", orderName + " " + toxicId);
		this.openPrintWindow("%ROOT%\\config\\prt\\ind\\mjbar.jhw", printData);
	}*/
	
	/**
	 * 麻精条码补打
	 */
	public void onBarcode() {
		TTable t = this.getTable("tTable_0");
		int row = t.getSelectedRow();
		if (row == -1) {
			this.messageBox("请选择要补打的容器");				
			return;
		}
		TTable t1 = this.getTable("tTable_1");
		int count = t1.getRowCount();
		if(count<=0) {
			return;
		}
		TParm printData = new TParm();
		for(int i=0;i<count;i++) {
			String toxicId = t1.getItemString(i, "TOXIC_ID");
			String orderDesc = t1.getItemString(i, "ORDER_DESC");
			printData.setData("BAR_CODE","TEXT",toxicId);
	    	printData.setData("ORDER_DESC","TEXT",orderDesc+""+toxicId);	
	    	this.openPrintWindow("%ROOT%\\config\\prt\\ind\\mjbar.jhw",
					printData,true);    
		}
	}
	
	/**
	 * 关闭
	 */
	public void onCloseContainer() {
		this.closeWindow();								
	} 
	
	/**
	 * 清空容器
	 */
	public void onReset() {
		if (this.getValue("CONTAINER_ID").equals("")
				|| this.getValue("CONTAINER_ID").equals(null)) {
			this.messageBox("请选择要清空的容器!");
			return;
		}		
		if (this.messageBox("询问", "确定清空?", 0) == 1) {
			return;
		}  
		TParm parm = new TParm();
		String containerId = this.getValueString("CONTAINER_ID");
		parm.setData("CONTAINER_ID",containerId);			
		TParm result = SPCContainerTool.getInstance().resetContainer(parm);
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return;
		}
		this.messageBox("清空成功");
		TTable td = this.getTable("tTable_1");
		td.removeRowAll();
	}
	
	

	
}
