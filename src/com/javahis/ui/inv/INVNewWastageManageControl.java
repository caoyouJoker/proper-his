package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import jdo.inv.INVNewBackDisnfectionTool;
import jdo.inv.INVPackMTool;
import jdo.inv.INVPublicTool;
import jdo.inv.InvPackStockMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.manager.INVPackOberver;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description: 损耗管理查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author wangming 2013-7-12
 * @version 1.0
 */
public class INVNewWastageManageControl extends TControl {

	private TTable tableD;		

	/**
	 * 初始化
	 */
	public void onInit() {
	
		tableD = (TTable) getComponent("TABLE");

		//弹出手术包类型选择窗口
		TParm parm = new TParm();
		((TTextField)getComponent("PACK_CODE")).setPopupMenuParameter("UD",
	            getConfigParm().newConfig(
	                "%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
	    // 定义接受返回值方法
		((TTextField)getComponent("PACK_CODE")).addEventListener(TPopupMenuEvent.
	            RETURN_VALUE, this, "popReturn");

	}

	/**
	 * 手术包选择返回数据处理
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String pack_code = parm.getValue("PACK_CODE");
        if (!StringUtil.isNullString(pack_code))
        	this.getTextField("PACK_CODE").setValue(pack_code);
        String pack_desc = parm.getValue("PACK_DESC");
        if (!StringUtil.isNullString(pack_code))
        	this.getTextField("PACK_DESC").setValue(pack_desc);
        this.grabFocus("PACK_SEQ_NO");	//定位焦点
	}
	
	/**
	 * 扫描条码
	 */
	public void onScream() {
		
		String packageCode = getValueString("SCREAM");
		if (packageCode == null || packageCode.length() == 0) {
			return;
		}
//		setValue("PACK_CODE", packageCode.substring(0, packageCode.length() - 4));
//		setValue("PACK_SEQ_NO",
//				packageCode.substring((packageCode.length() - 4), packageCode.length()));
		this.grabFocus("SCREAM");	//定位焦点
		this.onQuery();
		setValue("SCREAM", "");
	}
	
	/**
	 * 查询方法
	 */
	public void onQuery() {
		TParm parm = new TParm();
		if(getValueString("SCREAM").toString().length()>0){
			parm.setData("BARCODE", getValueString("SCREAM"));
			TParm result = new TParm();
			result = INVNewBackDisnfectionTool.getInstance().queryPackMaterialInfoByBarcode(parm);
			if (result.getErrCode() < 0) { 
	            err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	            return;
	        }  
			tableD.setParmValue(result);
			return; 
		}
		if(getValueString("PACK_CODE").toString().length()>0&&getValueString("PACK_SEQ_NO").toString().length()>0){
			if(getValueString("PACK_SEQ_NO").toString().equals("0")){
				messageBox("请输入手术包序号！");
				return;
			}
			parm.setData("PACK_CODE", getValueString("PACK_CODE"));
			parm.setData("PACK_SEQ_NO", getValueString("PACK_SEQ_NO"));
			TParm result = new TParm();
			//损耗管理注意  
			result = INVNewBackDisnfectionTool.getInstance().queryPackageInfoByBarcode(parm);
			if (result.getErrCode() < 0) { 
	            err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	            return;
	        }  
			parm.setData("BARCODE", result.getData("BARCODE", 0));
			result = INVNewBackDisnfectionTool.getInstance().queryPackMaterialInfoByBarcode(parm);
			if (result.getErrCode() < 0) { 
	            err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
	            return;
	        }  
			tableD.setParmValue(result);
			return;
		}
		messageBox("请输入查询条件！");
		
		
	}
	/**
	 * 清空方法
	 */
	public void onClear() {
		
		// 清空属性
		clearText();
		// 清空明细表
		clearTable(tableD);
	}
	/**
	 * 清空控件值
	 */
	private void clearText() {
		this.clearValue("PACK_CODE;PACK_SEQ_NO;PACK_DESC;SCREAM");
	}

	/**清空table**/
	private void clearTable(TTable table) {
		table.removeRowAll();
	}
	/** 获得TTextField类型控件 **/
	private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

}
