package com.javahis.ui.inv;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;

import jdo.inv.INVCodeMapTool;
import jdo.spc.SPCCodeMapHisTool;
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
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:物联网供应商物资编码比对 
 * </p>
 * 
 * <p>   
 * Description:物联网供应商物资编码比对
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>                   
 * Company: bluecore
 * </p>   
 *  
 * @author fux 2013.6.6
 * @version 1.0
 */
public class INVCodeMapControl extends TControl {
	                                 
	private TTable table;  //table
	private TTextField inv_code;//物资代码
	private TTextField inv_desc;//物资名称
	private TTextField inv_spe;//物联网药品规格
	
	private TTextFormat sup_code;//供应商代码
	private TTextField sup_inv_code;//供应商药品代码
	
	 
	/**构造器   */   
	public INVCodeMapControl() {
		super();             	
	}  
	
    /**初始化方法*/  
    public void onInit() {
    	table = (TTable) getComponent("TABLE_M");
    	
    	inv_code = (TTextField) getComponent("INV_CODE");//物联网药品代码
    	inv_desc = (TTextField) getComponent("INV_CHN_DESC");;//物联网药品名称
    	inv_spe = (TTextField) getComponent("DESCRIPTION");;//物联网药品规格
    	
    	sup_code = (TTextFormat) getComponent("SUP_CODE");;//供应商代码
    	sup_inv_code = (TTextField) getComponent("SUP_INV_CODE");;//供应商药品代码
    	TParm parm = new TParm();
    	parm.setData("SUP_CODE", getValueString("SUP_CODE"));
    	getTextField("INV_CODE")
		.setPopupMenuParameter("UI", getConfigParm().newConfig(
        "%ROOT%\\config\\inv\\INVBasePopup.x"), parm);
    	// 定义接受返回值方法 
    	getTextField("INV_CODE").addEventListener(
		TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
    					
    }
    
    
    /**查询方法*/
	public void onQuery() {
		TParm parm = new TParm();
		parm.setData("INV_CODE",inv_code.getValue());
		 
		parm.setData("SUP_CODE",sup_code.getValue());
		  
		TParm result = INVCodeMapTool.getInstance().query(parm);
		if(result.getCount()<=0){
			messageBox("查询无结果！"); 
		}
		table.setParmValue(result);			
	} 
	
	
	/**
	 * 保存修改操作
	 */
	public void onSave() {

		TParm parm = this.getParmForTag("INV_CODE;SUP_CODE;SUP_INV_CODE");
		if (!checkData(parm)) return;
		
		//检查系统中是否有该药品
		boolean queryFlg = INVCodeMapTool.getInstance().queryBase(parm);
		if(!queryFlg){
			messageBox("系统中没有该物资！");
			return;
		}

		// 保存之前先查询,数据库中是否有该记录
		TParm result = INVCodeMapTool.getInstance().updateQuery(parm);

		if (result.getCount("INV_CODE") > 0) {
			boolean flg = INVCodeMapTool.getInstance().update(parm);
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
			boolean flg = INVCodeMapTool.getInstance().save(parm);
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
  
		setValueForParm("INV_CODE;INV_CHN_DESC;DESCRIPTION;SUP_CODE;SUP_INV_CODE",
						table.getParmValue(), 
						rowno);
		inv_code.setEnabled(false);
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
						String invCode = st.getCell(0, i).getContents().trim();
						String supCode = st.getCell(1, i).getContents().trim();
						String supInvCode = st.getCell(2, i).getContents().trim();
						 
						parm.addData("INV_CODE", invCode);							
						parm.addData("SUP_CODE", supCode);
						parm.addData("SUP_INV_CODE", supInvCode);
						
						count++;
					}		    
				    
					parm.setCount(count);			
					if (count < 1) {  
						this.messageBox("导入数据失败");			
						return;
					}
					TParm result = TIOM_AppServer.executeAction(
							"action.inv.INVCodeMapAction", "importMap",
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
		String values = "INV_CODE;INV_CHN_DESC;DESCRIPTION;SUP_CODE;SUP_INV_CODE";
		this.clearValue(values);
		inv_code.setEnabled(true);
		sup_code.setEnabled(true);
	}
	
	/**检查数据培训记录*/  
	private boolean checkData(TParm parm) {
		//INV_CODE;SUP_CODE;SUP_INV_CODE
		if (null==parm.getValue("INV_CODE") || "".equals(parm.getValue("INV_CODE"))) {
			messageBox("物资编码不能为空！");
			return false;
		}
		if (null==parm.getValue("SUP_CODE") || "".equals(parm.getValue("SUP_CODE").toString())) {
			messageBox("供应商不能为空！");
			return false;
		}
		if (null==parm.getValue("SUP_INV_CODE") || "".equals(parm.getValue("SUP_INV_CODE"))) {
			messageBox("供应商物资编码不能为空！");
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
		String inv_code = parm.getValue("INV_CODE");
		if (!StringUtil.isNullString(inv_code)) {
			getTextField("INV_CODE").setValue(inv_code);
		}  
		String inv_desc = parm.getValue("INV_CHN_DESC");
		if (!StringUtil.isNullString(inv_desc)) {
			getTextField("INV_CHN_DESC").setValue(inv_desc);
		}
		String SPECIFICATION = parm.getValue("DESCRIPTION");
		if (!StringUtil.isNullString(SPECIFICATION)) {                                    
			getTextField("DESCRIPTION").setValue(SPECIFICATION);			
		}
	}
	
	
}
