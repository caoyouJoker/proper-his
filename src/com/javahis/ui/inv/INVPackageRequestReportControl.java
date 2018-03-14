package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jdo.inv.INVNewBackDisnfectionTool;
import jdo.inv.INVNewRepackTool;
import jdo.inv.INVNewSterilizationTool;
import jdo.inv.INVReturnedCheckTool;
import jdo.inv.INVSQL;
import jdo.inv.InvPackStockMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;


/**
 * 
 * <p>
 * Title:诊疗包申请情况查询报表
 * </p>
 * 
 * <p>
 * Description: 诊疗包申请情况查询报表
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
 * @author wangming 2013-12-19
 * @version 1.0
 */
public class INVPackageRequestReportControl extends TControl{

	private TTable tableM;			//请领查询显示列表主表
	
	private TTable tableD;			//请领查询显示列表细表

	/**
	 * 初始化
	 */
	public void onInit() {
	
		tableM = (TTable) getComponent("TABLEM");
		
		tableD = (TTable) getComponent("TABLED");
		
		this.setTimes();
		
		TParm parm = new TParm();
        // 设置弹出菜单
        getTextField("PACK_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
        // 定义接受返回值方法
        getTextField("PACK_CODE").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn");
		
        setValue("DEPT_CODE", Operator.getDept());
	}
	
	/**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String pack_code = parm.getValue("PACK_CODE");
        if (!StringUtil.isNullString(pack_code))
            getTextField("PACK_CODE").setValue(pack_code);
        String pack_desc = parm.getValue("PACK_DESC");
        if (!StringUtil.isNullString(pack_desc))
            getTextField("PACK_DESC").setValue(pack_desc);
 
    }
	
    public void onClear(){
    	this.setTimes();
    	getTextField("PACK_CODE").setValue("");
    	getTextField("PACK_DESC").setValue("");
    	setValue("DEPT_CODE", "");
    	
    	tableM.removeRowAll();
    	tableD.removeRowAll();
    }
    
    public void onQuery(){
    	
    	if(!this.checkConditions()){
    		return;
    	}
    	
    	String sql = this.getQuerySQL();
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		if(result.getCount() <= 0){
			this.messageBox("无查询结果");
			tableM.removeRowAll();
			tableD.removeRowAll();
			return;
		}
		tableM.setParmValue(result);
		tableD.removeRowAll();
    }
    
    private String getQuerySQL(){
    	
    	String sql = "";
    	
    	sql = "SELECT D.INV_CODE, PM.PACK_DESC, SUM(D.QTY) AS QTY, SUM(D.ACTUAL_QTY) AS ACTUAL_QTY " 
    		+ " FROM INV_SUPREQUESTD D LEFT JOIN INV_SUPREQUESTM M ON D.REQUEST_NO = M.REQUEST_NO LEFT JOIN INV_PACKM PM ON D.INV_CODE = PM.PACK_CODE " 
    		+ " WHERE M.REQUEST_DATE BETWEEN TO_DATE('"+this.getValueString("START_DATE").substring(0, 19)+"','yyyy/mm/dd hh24:mi:ss') AND TO_DATE('"+this.getValueString("END_DATE").substring(0, 19)+"','yyyy/mm/dd hh24:mi:ss') " 
    		+ " AND D.PACK_MODE = '1' AND M.APP_ORG_CODE = '" + this.getValueString("DEPT_CODE") + "' ";

    	if(null!=this.getValueString("PACK_CODE") && !this.getValueString("PACK_CODE").equals("")){
    		sql = sql + " AND D.INV_CODE = '" + this.getValueString("PACK_CODE") + "' ";
    	}
    	
    	
    	sql = sql + " GROUP BY D.INV_CODE,PM.PACK_DESC ORDER BY D.INV_CODE ASC ";
    	
    	return sql;
    	
    }
    
    private String getQueryDetailSQL(String packCode, double qty){
    	
    	String sql = "";
    		
    	sql = " SELECT D.INV_CODE, B.INV_CHN_DESC, D.DESCRIPTION, D.QTY*"+qty+" AS INV_QTY, S.UNIT_CHN_DESC " 
    		+ " FROM INV_PACKD D LEFT JOIN INV_BASE B ON D.INV_CODE = B.INV_CODE LEFT JOIN SYS_UNIT S ON D.STOCK_UNIT = S.UNIT_CODE " 
    		+ " WHERE D.PACK_CODE = '"+packCode+"' ";
    	
    	return sql;
    	
    }
    
	public void onTableMClick(){
		int row = tableM.getSelectedRow();
		TParm selParm = tableM.getParmValue().getRow(row);
		
		String packCode = selParm.getData("INV_CODE").toString();
		
		double qty = selParm.getDouble("ACTUAL_QTY");
		
		String sql = this.getQueryDetailSQL(packCode,qty);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		if(result.getCount() <= 0){
			tableD.removeRowAll();
			return;
		}
		tableD.removeRowAll();
		tableD.setParmValue(result);
		
	}
    
    
    
    private boolean checkConditions(){
    	
//    	if( null == this.getValueString("PACK_CODE") || "".equals(this.getValueString("PACK_CODE")) || this.getValueString("PACK_CODE").length() <= 0){
//    		messageBox("请输入手术包类型!");
//    		return false;
//    	}
    	if( null == this.getValueString("DEPT_CODE") || "".equals(this.getValueString("DEPT_CODE")) || this.getValueString("DEPT_CODE").length() <= 0){
    		messageBox("请选择请领部门!");
			return false;
    	}
    	if( null == this.getValueString("START_DATE") || "".equals(this.getValueString("START_DATE")) || this.getValueString("START_DATE").length() <= 0 || null == this.getValueString("END_DATE") || "".equals(this.getValueString("END_DATE")) || this.getValueString("END_DATE").length() <= 0 ){
    		messageBox("请输入日期!");
    		return false;
    	}
    	return true;
    }
    
	 /**
	  * 导出excel
	  * */
	 public void onExcel(){

		 TTable table = (TTable) this.getComponent("TABLEM");
		 if (table.getRowCount() > 0){
			 ExportExcelUtil.getInstance().exportExcel(table, "诊疗包请领查询报表");
		 }

	 }
    
    
	private void setTimes(){
		//初始化    退货日期查询区间
		Timestamp date = new Timestamp(new Date().getTime());
		this.setValue("START_DATE", 
				new Timestamp(date.getTime() + -7 * 24L * 60L * 60L * 1000L).toString()
					.substring(0, 10).replace("-", "/") + " 00:00:00");
		this.setValue("END_DATE", 
				date.toString().substring(0, 10).replace("-", "/") + " 23:59:59");
	}
	
	private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
}
