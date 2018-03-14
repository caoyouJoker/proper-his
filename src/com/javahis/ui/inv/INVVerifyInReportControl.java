package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 物资验收统计Control 
 * </p>
 *
 * <p>
 * Description: 物资验收统计Control 
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangh 2013.5.8
 * @version 1.0
 */

public class INVVerifyInReportControl
    extends TControl {
	
    public INVVerifyInReportControl() {
    }
    
    private String orgCode = "";	//验收部门
    
    private String invKind = "";	//物资分类
    	
    private String supCode = "";	//供应商
    
    private String upSupCode = "";	//上级供应商
    
    private String invCode = "";	//物资编码
    
    private String startDate = "";	//起始时间
    
    private String endDate = "";	//终止时间
    
    private String verifyinNo = "";	//入库单号
    
    private String conCode = "";	//直接入库部门
    
    private String seqNo = "";	//xuhao
    
    private TTable table;			//主表
    
    private TTable tableD;			//明细表

    
    /*
     * 界面初始化
     * 
     */
    public void onInit(){
    	initUI();
    }

    /*
     * 初始化界面
     */
    private void initUI() {
		Timestamp date = new Timestamp(new Date().getTime());
		this.setValue("START_TIME", 
				new Timestamp(date.getTime() + -7 * 24L * 60L * 60L * 1000L).toString()
					.substring(0, 10).replace("-", "/") + " 00:00:00");
		this.setValue("END_TIME", 
				date.toString().substring(0, 10).replace("-", "/") + " 23:59:59");
//		this.setValue("ORG_CODE", Operator.getDept());
		table = (TTable) this.getComponent("TABLE");
		tableD = (TTable) this.getComponent("TABLED");
		TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "OTH");
		// 设置弹出菜单
        getTextField("INV_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parm);
		// 定义接受返回值方法
        getTextField("INV_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        
        this.setValue("ORG_CODE", Operator.getDept());
        this.setValue("CON_CODE", Operator.getDept());
        
        if (this.getPopedem("ALL")) {
			this.callFunction("UI|ORG_CODE|setEnabled", false);
		}else{
			this.callFunction("UI|ORG_CODE|setEnabled", true);
		}
        
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
        String order_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("INV_CODE").setValue(order_code);
        String order_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("INV_DESC").setValue(order_desc);
    }
    
    /*
     * 查询方法
     */
    public void onQuery(){
    	query();
    }
    
    /**
	 * 主表点击事件
	 */
	public void onTableMClicked() {
		int row = table.getSelectedRow();
		
		if(row>=0){
			String invCode = table.getParmValue().getValue("INV_CODE", row);
			String verNo = table.getParmValue().getValue("VERIFYIN_NO", row);
			String seqNO = table.getParmValue().getValue("SEQ_NO", row);
			this.queryDetail(invCode, verNo,seqNO);
		}
	}

	private void queryDetail(String invCode, String verNo,String seqNo){
		if(this.getValueString("START_TIME") == null || this.getValueString("START_TIME").length() <= 0
				|| this.getValueString("END_TIME") == null || this.getValueString("END_TIME").length() <= 0){
			this.messageBox("请填写查询时间！");
			return;
		}
		//开始时间
		startDate = this.getValueString("START_TIME").substring(0, 
				this.getValueString("START_TIME").lastIndexOf(".")).replace("-", "").
				replace(":", "").replace(" ", "");
		//结束时间
		endDate = this.getValueString("END_TIME").substring(0, 
				this.getValueString("END_TIME").lastIndexOf(".")).replace("-", "").
				replace(":", "").replace(" ", "");
		//供应商
//		if(this.getValueString("SUP_CODE") != null && this.getValueString("SUP_CODE").length() > 0){
			supCode = this.getValueString("SUP_CODE");
//		}
		//验收部门
//		if(this.getValueString("ORG_CODE") != null && this.getValueString("ORG_CODE").length() > 0){
			orgCode = this.getValueString("ORG_CODE");
//		}
		//上级供应商
//		if(this.getValueString("UP_SUP_CODE") != null && this.getValueString("UP_SUP_CODE").length() > 0){
			upSupCode = this.getValueString("UP_SUP_CODE");
//		}
		//物资分类
//		if(this.getValueString("INV_KIND") != null && this.getValueString("INV_KIND").length() > 0){
			invKind = this.getValueString("INV_KIND");
//		}
		//物资代码
		this.invCode = invCode;
		//入库单号
//		this.verifyinNo = this.getValueString("VERIFYINNO");
		this.verifyinNo = verNo;
		
		this.seqNo = seqNo;
		//直接入库部门
		conCode = this.getValueString("CON_CODE");
		
		
		
		String sql = this.getSqlDetail();
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		if(result.getCount() <= 0){
//			this.messageBox("无查询结果");
			tableD.removeRowAll();
			return;
		}
		tableD.setParmValue(result);
	}
	
	private void query() {
		if(this.getValueString("START_TIME") == null || this.getValueString("START_TIME").length() <= 0
				|| this.getValueString("END_TIME") == null || this.getValueString("END_TIME").length() <= 0){
			this.messageBox("请填写查询时间！");
			return;
		}
		//开始时间
		startDate = this.getValueString("START_TIME").substring(0, 
				this.getValueString("START_TIME").lastIndexOf(".")).replace("-", "").
				replace(":", "").replace(" ", "");
		//结束时间
		endDate = this.getValueString("END_TIME").substring(0, 
				this.getValueString("END_TIME").lastIndexOf(".")).replace("-", "").
				replace(":", "").replace(" ", "");
		//供应商
		supCode = this.getValueString("SUP_CODE");
		//物资代码
		invCode = this.getValueString("INV_CODE");
		//验收部门
		orgCode = this.getValueString("ORG_CODE");
		//上级供应商
		upSupCode = this.getValueString("UP_SUP_CODE");
		//物资分类
		invKind = this.getValueString("INV_KIND");
		//入库单号
		this.verifyinNo = this.getValueString("VERIFYINNO");
		//直接入库部门
		conCode = this.getValueString("CON_CODE");
			
		String sql = this.getSql();
		  
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
//		TParm result = INVVerifyInReportTool.getInstance().onQueryVerifyInReport(sql);
		if(result.getCount() <= 0){
			this.messageBox("无查询结果");
			this.setValue("AMTSUM", "");
			this.setValue("AMTCOUNT", "");
			table.removeRowAll();
			tableD.removeRowAll();
			return;
		}
		
		int count = result.getCount("INV_CODE");
		
		
		this.setValue("AMTCOUNT", String.valueOf(count));
		
		double amt = 0;
		double qty = 0;
		for(int i=0;i<result.getCount("INV_CODE");i++){
			amt = amt + result.getDouble("AMT_PRICE", i); 
			qty = qty + result.getDouble("TOTALQTY", i);
		}  
		  
		DecimalFormat df=new DecimalFormat(",###.00");
		this.setValue("NUM", String.valueOf(qty));
		this.setValue("AMTSUM", String.valueOf(df.format(amt)) );
		
		table.setParmValue(result);  
		tableD.removeRowAll();
	}
	
	/*
     * 清空方法
     */
    public void onClear(){
    	this.clearValue("INV_CODE;INV_DESC;SUP_CODE;INV_KIND;UP_SUP_CODE;START_TIME;END_TIME;AMTSUM;AMTCOUNT;VERIFYINNO");
    	table.removeRowAll();
    	tableD.removeRowAll();
    }
    
    /*
     * 打印方法
     */
    public void onPrint(){
//    	print();
    }

	private void print() {
		if(table.getRowCount() <= 0){
			this.messageBox("无打印数据！");
			return;
		}
		TParm tableData = table.getParmValue();
		TParm printData = new TParm();
		TParm printParm = new TParm();
		for (int i = 0; i < tableData.getCount("INV_CODE"); i++) {
			printData.addData("COUNTS", i+1);
			printData.addData("INV_CODE", tableData.getData("INV_CODE", i));
			printData.addData("INV_CHN_DESC", tableData.getData("INV_CHN_DESC", i));
//			String verifyinDate = "",validdate = "";
//			Timestamp verifyInDate = tableData.getTimestamp("VERIFYIN_DATE", i);
//			Timestamp validDate = tableData.getTimestamp("VALID_DATE", i);
//			if(verifyInDate != null)
//				verifyinDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date(verifyInDate.getTime()));
//			printData.addData("VERIFYIN_DATE", verifyinDate);
			printData.addData("DESCRIPTION", tableData.getData("DESCRIPTION", i));
			printData.addData("TOTALQTY", tableData.getData("TOTALQTY", i));
			printData.addData("UNIT_CHN_DESC", tableData.getData("UNIT_CHN_DESC", i));
			printData.addData("UNIT_PRICE", tableData.getData("UNIT_PRICE", i));
			printData.addData("AMT_PRICE", tableData.getData("AMT_PRICE", i));
			
			
			printData.addData("OWN_PRICE", tableData.getData("OWN_PRICE", i));
			printData.addData("AMT_SELL", tableData.getData("AMT_SELL", i));
			printData.addData("SUPDESC", tableData.getData("SUPDESC", i));
//			if(validDate != null)
//				validdate = new SimpleDateFormat("yyyy/MM/dd").format(new Date(validDate.getTime()));
//			printData.addData("VALID_DATE", validdate);
		}
		printData.setCount(tableData.getCount("INV_CODE"));
		printData.addData("SYSTEM", "COLUMNS", "COUNTS");
		printData.addData("SYSTEM", "COLUMNS", "INV_CODE");
		printData.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		printData.addData("SYSTEM", "COLUMNS", "TOTALQTY");
		printData.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
		printData.addData("SYSTEM", "COLUMNS", "AMT_PRICE");
		printData.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
		printData.addData("SYSTEM", "COLUMNS", "AMT_SELL");
		printData.addData("SYSTEM", "COLUMNS", "SUPDESC");
		
		printParm.setData("TABLE", printData.getData());
//		printParm.setData("TITLE", "TEXT", "验收入库统计表");
		if(this.getValueString("ORG_CODE") != null && this.getValueString("ORG_CODE").length() > 0){
			TTextFormat dept = (TTextFormat) getComponent("ORG_CODE");
			printParm.setData("DEPT", "TEXT",  dept.getText());
		}else{
			printParm.setData("DEPT", "TEXT", "所有");
		}
		printParm.setData("CDATE", "TEXT", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVNewVerifyInReport.jhw",
				printParm);
	}
	
	/*
     * 导出excel方法
     */
    public void onExecl(){
    	if(table.getRowCount() <= 0){
			this.messageBox("没有数据！");
			return;
		}
    	ExportExcelUtil.getInstance().exportExcel(table, "物资验收入库汇总");
    }
    
    /**
     * 得到TextField对象
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
    
    private String getSql(){  
    	String sql = "";
    	
    	sql = "SELECT A.VERIFYIN_NO, B.INV_CODE, B.SEQ_NO,C.INV_CHN_DESC, C.DESCRIPTION, SUM(B.QTY) AS TOTALQTY, E.UNIT_CHN_DESC, " + 
        		" B.UNIT_PRICE, SUM(B.QTY * B.UNIT_PRICE) AS AMT_PRICE, SF.OWN_PRICE, SUM(B.QTY*SF.OWN_PRICE) AS AMT_SELL, " + 
        		" F.SUP_ABS_DESC AS SUPDESC,S.CHN_DESC,FS.SUP_ABS_DESC AS UPSUPDESC,FT.SUP_ABS_DESC AS MANDESC, SF.OWN_PRICE-B.UNIT_PRICE AS SUB_PRICE, " +
        		" B.BATCH_NO,B.VALID_DATE,A.VERIFYIN_DATE " +       
        		" FROM INV_VERIFYINM A,INV_VERIFYIND B,INV_BASE C,SYS_UNIT E,SYS_SUPPLIER F,INV_ORG G,SYS_FEE SF, " + 
        		" ( SELECT ID, CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='INV_BASE_KIND' ) S, SYS_SUPPLIER FS, SYS_SUPPLIER FT " + 
        		" WHERE  B.VERIFYIN_NO = A.VERIFYIN_NO(+) AND  A.VERIFYIN_DEPT =G.ORG_CODE(+) AND  B.STOCK_UNIT = E.UNIT_CODE(+) " + 
        		" AND C.SUP_CODE = F.SUP_CODE(+)  AND B.INV_CODE = C.INV_CODE(+)   " + 
        		" AND C.INV_KIND = S.ID(+) AND C.ORDER_CODE = SF.ORDER_CODE(+) AND C.UP_SUP_CODE = FS.SUP_CODE(+) " + 
        		" AND C.MAN_CODE = FT.SUP_CODE(+) AND A.CHECK_FLG = 'Y' ";
    	
    	if(((TRadioButton) getComponent("ISHIGH")).isSelected()){
    		sql = sql + " AND C.SEQMAN_FLG = 'Y' AND C.EXPENSIVE_FLG = 'Y' ";
    	}
    	if(((TRadioButton) getComponent("ISLOW")).isSelected()){
    		sql = sql + "  AND ( ( C.SEQMAN_FLG = 'N' AND C.EXPENSIVE_FLG = 'N' ) OR ( C.SEQMAN_FLG = 'Y' AND C.EXPENSIVE_FLG = 'N' ) OR ( C.SEQMAN_FLG = 'N' AND C.EXPENSIVE_FLG = 'Y' ) )  ";
    	}
//    	if(((TCheckBox) getComponent("CONSIGNFLG")).isSelected()){
//    		sql = sql + " AND ( C.CONSIGN_FLG = 'Y' OR C.CONSIGN_FLG = 'N' ) ";
//    	}else{
//    		sql = sql + " AND C.CONSIGN_FLG = 'N' ";
//    	}
    	if( supCode!="" && supCode.length()>0 ){
    		sql = sql + " AND C.SUP_CODE = '" + supCode + "' ";
    	}
    	if( invCode!="" && invCode.length()>0 ){
    		sql = sql + " AND B.INV_CODE LIKE '%" + invCode + "%' "; 
    	}
    	if( upSupCode!="" && upSupCode.length()>0 ){
    		sql = sql + " AND C.UP_SUP_CODE = '"+upSupCode+"' ";
    	}
    	if( orgCode!="" && orgCode.length()>0 ){
    		sql = sql + " AND A.VERIFYIN_DEPT = '"+orgCode+"' "; 
    	}
    	if( invKind!="" && invKind.length()>0 ){
    		sql = sql + " AND C.INV_KIND = '"+invKind+"' ";
    	}
    	if( verifyinNo!="" && verifyinNo.length()>0 ){
    		sql = sql + " AND A.VERIFYIN_NO = '"+verifyinNo+"' ";
    	}
    	//fux 20160721
//    	if( conCode!="" && conCode.length()>0 ){
//    		sql = sql + " AND A.CON_FLG = 'Y' AND A.CON_ORG = '" + conCode + "' ";
//    	}else{
//    		sql = sql + " AND ( A.CON_FLG = 'N' OR A.CON_FLG IS NULL ) ";
//    	}
//    	if(((TCheckBox) getComponent("CONFLG")).isSelected()){
//    		sql = sql + " AND A.CON_FLG = 'Y' ";
//    	}else{
//    		sql = sql + " AND ( A.CON_FLG = 'N' OR A.CON_FLG IS NULL ) ";
//    	}
    	
    	sql = sql + " AND A.VERIFYIN_DATE BETWEEN TO_DATE('"+startDate+"','yyyy/mm/dd hh24:mi:ss') AND TO_DATE('"+endDate+"','yyyy/mm/dd hh24:mi:ss') ";
    	
    	sql = sql + " GROUP BY A.VERIFYIN_NO, B.SEQ_NO, B.INV_CODE, C.INV_CHN_DESC, C.DESCRIPTION, E.UNIT_CHN_DESC, B.UNIT_PRICE, SF.OWN_PRICE, " + 
    		"F.SUP_ABS_DESC,S.CHN_DESC,FS.SUP_ABS_DESC,FT.SUP_ABS_DESC,B.BATCH_NO,B.VALID_DATE,A.VERIFYIN_DATE ";
    	System.out.println("sql:::"+sql);
    	return sql;  
    }
     
    private String getSqlDetail(){
    	  
    	String sql = "";
    	
    	sql = " SELECT  DD.INV_CODE, C.INV_CHN_DESC, C.DESCRIPTION, E.UNIT_CHN_DESC, B.UNIT_PRICE,  SF.OWN_PRICE, " + 
              " F.SUP_ABS_DESC AS SUPDESC,S.CHN_DESC,FS.SUP_ABS_DESC AS UPSUPDESC,FT.SUP_ABS_DESC AS MANDESC, " + 
              " SF.OWN_PRICE-B.UNIT_PRICE AS SUB_PRICE,A.VERIFYIN_DATE,A.VERIFYIN_NO,DD.VALID_DATE,B.PURORDER_NO, DD.RFID " +
              " FROM INV_VERIFYINM A,INV_VERIFYIND B,INV_VERIFYINDD DD,INV_BASE C,SYS_UNIT E,SYS_SUPPLIER F,INV_ORG G,SYS_FEE SF, " + 
              " ( SELECT ID, CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='INV_BASE_KIND' ) S, SYS_SUPPLIER FS, SYS_SUPPLIER FT " + 
              " WHERE DD.VERIFYIN_NO = B.VERIFYIN_NO(+) AND DD.VERIFYIN_NO = A.VERIFYIN_NO(+) AND B.STOCK_UNIT = E.UNIT_CODE(+) " +
              " AND C.SUP_CODE = F.SUP_CODE(+)  AND DD.INV_CODE = C.INV_CODE(+)  AND A.VERIFYIN_DEPT =G.ORG_CODE(+) " +
              " AND C.INV_KIND = S.ID(+) AND C.ORDER_CODE = SF.ORDER_CODE(+) AND C.UP_SUP_CODE = FS.SUP_CODE(+) " +
              " AND C.MAN_CODE = FT.SUP_CODE(+) AND　DD.SEQ_NO =B.SEQ_NO　";
              
    	if(((TRadioButton) getComponent("ISHIGH")).isSelected()){
    		sql = sql + " AND C.SEQMAN_FLG = 'Y' AND C.EXPENSIVE_FLG = 'Y' ";
    	}
    	if(((TRadioButton) getComponent("ISLOW")).isSelected()){
    		sql = sql + " AND ( ( C.SEQMAN_FLG = 'N' AND C.EXPENSIVE_FLG = 'N' ) OR ( C.SEQMAN_FLG = 'Y' AND C.EXPENSIVE_FLG = 'N' ) OR ( C.SEQMAN_FLG = 'N' AND C.EXPENSIVE_FLG = 'Y' ) ) ";
    	}
//    	if(((TCheckBox) getComponent("CONSIGNFLG")).isSelected()){
//    		sql = sql + " AND ( C.CONSIGN_FLG = 'Y' OR C.CONSIGN_FLG = 'N' ) ";
//    	}else{
//    		sql = sql + " AND C.CONSIGN_FLG = 'N' ";
//    	}
    	if( supCode!="" && supCode.length()>0 ){
    		sql = sql + " AND C.SUP_CODE = '" + supCode + "' ";
    	}
    	if( invCode!="" && invCode.length()>0 ){
    		sql = sql + " AND DD.INV_CODE = '" + invCode + "' AND B.INV_CODE = '" + invCode + "' "; 
    	}
    	if( upSupCode!="" && upSupCode.length()>0 ){
    		sql = sql + " AND C.UP_SUP_CODE = '"+upSupCode+"' ";
    	}
    	if( orgCode!="" && orgCode.length()>0 ){
    		sql = sql + " AND A.VERIFYIN_DEPT = '"+orgCode+"' "; 
    	}
    	if( invKind!="" && invKind.length()>0 ){
    		sql = sql + " AND C.INV_KIND = '"+invKind+"' ";
    	}
    	if( verifyinNo!="" && verifyinNo.length()>0 ){
    		sql = sql + " AND A.VERIFYIN_NO = '"+verifyinNo+"' ";
    	}
    	if( seqNo!="" && seqNo.length()>0 ){
    		sql = sql + " AND B.SEQ_NO = '"+seqNo+"' ";
    	}
    	
    	
    	//fux 20160721
//    	if( conCode!="" && conCode.length()>0 ){
//    		sql = sql + " AND A.CON_FLG = 'Y' AND A.CON_ORG = '" + conCode + "' ";
//    	}else{
//    		sql = sql + " AND ( A.CON_FLG = 'N' OR A.CON_FLG IS NULL ) ";
//    	}
//    	if(((TCheckBox) getComponent("CONFLG")).isSelected()){
//    		sql = sql + " AND A.CON_FLG = 'Y' ";
//    	}else{
//    		sql = sql + " AND ( A.CON_FLG = 'N' OR A.CON_FLG IS NULL ) ";
//    	}

    	sql = sql + " AND A.VERIFYIN_DATE BETWEEN TO_DATE('"+startDate+"','yyyy/mm/dd hh24:mi:ss') AND TO_DATE('"+endDate+"','yyyy/mm/dd hh24:mi:ss') ";
    	
              
        sql = sql + " ORDER BY VERIFYIN_DATE DESC ";
    	System.out.println("sqldetail:"+sql);
    	return sql;
    }
    
    
    
}
