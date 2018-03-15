package com.javahis.ui.spc;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JFileChooser;

import jdo.spc.SPCCodeMapTool;
import jdo.sys.Operator;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.system.textFormat.TextFormatSYSUnit;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:物联网供应商药品编码比对
 * </p>
 * 
 * <p>
 * Description:物联网供应商药品编码比对
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>                      
 * Company: ProperSoft
 * </p>  
 * 
 * @author liuzhen 2013.1.16
 * @version 1.0
 */
public class SPCCodeMapControl extends TControl {
	
	private TTable table;  //table
	private TTextField order_code;//物联网药品代码
	private TTextField order_desc;//物联网药品名称
	private TTextField order_spe;//物联网药品规格
	
	private TTextFormat sup_code;//供应商代码
	private TTextField sup_order_code;//供应商药品代码
	
	
	/**构造器   */   
	public SPCCodeMapControl() {
		super();             	
	} 
	
    /**初始化方法*/
    public void onInit() {
    	table = (TTable) getComponent("TABLE_M");
    	
    	order_code = (TTextField) getComponent("ORDER_CODE");//物联网药品代码
    	order_desc = (TTextField) getComponent("ORDER_DESC");;//物联网药品名称
    	order_spe = (TTextField) getComponent("SPECIFICATION");;//物联网药品规格
    	
    	sup_code = (TTextFormat) getComponent("SUP_CODE");;//供应商代码
    	sup_order_code = (TTextField) getComponent("SUP_ORDER_CODE");;//供应商药品代码
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
    
    
    /**查询方法*/
	public void onQuery() {
		TParm parm = new TParm();
		parm.setData("ORDER_CODE",order_code.getValue());
		
		parm.setData("SUP_CODE",sup_code.getValue());
		
		parm.setData("SUP_ORDER_CODE",sup_order_code.getValue());
		
		TParm result = SPCCodeMapTool.getInstance().query(parm);
		if(result.getCount()<=0){
			messageBox("查询无结果！");
		}
		table.setParmValue(result);			
	} 
	
	
	/**
	 * 保存修改操作
	 */
	public void onSave() {

		TParm parm = this.getParmForTag("ORDER_CODE;SUP_CODE;SUP_ORDER_CODE;SUPPLY_UNIT_CODE");
		if (!checkData(parm)) return;
		// <------ IND_CODE_MAP 补充字段 OPT_USER,OPT_DATE,OPT_TERM identity by shendr 20131107
		parm.setData("OPT_USER",Operator.getID());
		parm.setData("OPT_TERM",Operator.getIP());
		double conversion_ratio = this.getValueDouble("CONVERSION_RATIO");
		parm.setData("CONVERSION_RATIO",conversion_ratio);
		// ------>
		//检查系统中是否有该药品
		boolean queryFlg = SPCCodeMapTool.getInstance().queryBase(parm);
		if(!queryFlg){
			messageBox("系统中没有该药品！");
			return;
		}

		// 保存之前先查询,数据库中是否有该记录
		TParm result = SPCCodeMapTool.getInstance().updateQuery(parm);

		if (result.getCount("ORDER_CODE") > 0) {
			boolean flg = SPCCodeMapTool.getInstance().update(parm);
			if (flg) {
				messageBox("保存成功");
				onClear();
				onQuery();
				return;
			} else {
				messageBox("保存失败");
				return;
			}
		} else {
			boolean flg = SPCCodeMapTool.getInstance().save(parm);
			if (flg) {
				messageBox("保存成功");
				onClear();
				onQuery();
				return;
			}
			messageBox("保存失败");
		}
	}
	
	
	/**删除操作*/
	public void onDelete() {

		table.acceptText();
		int rowno = table.getSelectedRow();

		if (rowno < 0) {
			messageBox("请选择要删除的信息");
			return;
		}

		TParm parm = table.getParmValue().getRow(rowno);

		if (this.messageBox("提示", "是否删除该记录", 2) == 0) {
			boolean flg = SPCCodeMapTool.getInstance().delete(parm);

			if (flg) {
				messageBox("删除成功");
				onClear();
				onQuery();
				return;
			}
			messageBox("删除失败");
		}

	}
	
	/** table 单击事件 */
	public void onTableClick() {

		table.acceptText();
		int rowno = table.getSelectedRow();

		setValueForParm("ORDER_CODE;ORDER_DESC;SPECIFICATION;SUP_CODE;SUP_ORDER_CODE;SUPPLY_UNIT_CODE;CONVERSION_RATIO",
						table.getParmValue(), 
						rowno);
		TParm result = SPCCodeMapTool.getInstance().queryPhaUnit(this.getValueString("ORDER_CODE"));
		String unit_chn_desc = result.getValue("UNIT_CHN_DESC",0);
		this.setValue("UNIT_CHN_DESC", unit_chn_desc);
		order_code.setEnabled(false);
		if(null==sup_code.getValue() || "".equals((sup_code.getValue()).toString().trim())){
			sup_code.setEnabled(true);
		}else{
			sup_code.setEnabled(false);
		}
	}

	
	/** 导入药品对照表*/
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
				int count=0;
				for (int i = 1; i < row; i++) {
						String orderCode = st.getCell(0, i).getContents().trim();
						String supCode = st.getCell(1, i).getContents().trim();
						String supOrderCode = st.getCell(2, i).getContents().trim();
						
						parm.addData("ORDER_CODE", orderCode);							
						parm.addData("SUP_CODE", supCode);
						parm.addData("SUP_ORDER_CODE", supOrderCode);
						
						count++;
					}		    
				    
					parm.setCount(count);			
					if (count < 1) {  
						this.messageBox("导入数据失败");			
						return;
					}
					
					TParm result = TIOM_AppServer.executeAction(
							"action.spc.SPCCodeMapAction", "importMap",
							parm);

					String flg = result.getValue("FLG");
					if ("Y".equals(flg)) {
						this.messageBox("更新成功！");
						onQuery();
						
					}else if("N".equals(flg)){
						this.messageBox("更新失败！");
						return;
					}else{
						this.messageBox(flg);
					}				
					
			} catch (BiffException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}         
	}
	
	/**清空操作*/
	public void onClear() {
		String values = "ORDER_CODE;ORDER_DESC;SPECIFICATION;SUP_CODE;SUP_ORDER_CODE;SUPPLY_UNIT_CODE;CONVERSION_RATIO;UNIT_CHN_DESC";
		this.clearValue(values);
		order_code.setEnabled(true);
		sup_code.setEnabled(true);
	}
	
	/**检查数据培训记录*/
	private boolean checkData(TParm parm) {
		//ORDER_CODE;SUP_CODE;SUP_ORDER_CODE
		if (null==parm.getValue("ORDER_CODE") || "".equals(parm.getValue("ORDER_CODE"))) {
			messageBox("物联网药品编码不能为空！");
			return false;
		}
		if (null==parm.getValue("SUP_CODE") || "".equals(parm.getValue("SUP_CODE").toString())) {
			messageBox("供应商不能为空！");
			return false;
		}
		if (null==parm.getValue("SUP_ORDER_CODE") || "".equals(parm.getValue("SUP_ORDER_CODE"))) {
			messageBox("供应商药品编码不能为空！");
			return false;
		}
		return true;
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
		String SPECIFICATION = parm.getValue("SPECIFICATION");
		if (!StringUtil.isNullString(SPECIFICATION)) {                                    
			getTextField("SPECIFICATION").setValue(SPECIFICATION);			
		}
		TParm result = SPCCodeMapTool.getInstance().queryPhaUnit(order_code);
		String unit_chn_desc = result.getValue("UNIT_CHN_DESC",0);
		if (!StringUtil.isNullString(unit_chn_desc)) {                                    
			((TextFormatSYSUnit)getComponent("UNIT_CHN_DESC")).setValue(unit_chn_desc);			
		}
	}
	
	
}
