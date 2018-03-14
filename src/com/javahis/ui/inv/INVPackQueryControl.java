package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

public class INVPackQueryControl extends TControl{
	
/**
 *  初始化
 */
public void onInit(){
	 
 }


/** 
 *  查询
 */
public void onQuery(){
	String sql = " SELECT ORG_CODE,PACK_CODE,SUM(QTY) AS QTY,USE_COST,USE_COST*SUM(QTY) AS TOT " +
			" FROM INV_PACKSTOCKM " +
			" WHERE 1 == 1 " ;
	String orgCode = this.getValueString("ORG_CODE"); 
	String packCode = this.getValueString("PACK_CODE");
	if("".equals(orgCode)|| orgCode==null){
		sql =  sql+ " AND ORG_CODE = '"+orgCode+"' ";
	} 
	if("".equals(packCode)|| packCode==null){
		sql =  sql+ " AND PACK_CODE = '"+packCode+"' ";
	} 
    sql = sql + " GROUP BY ORG_CODE,PACK_CODE,USE_COST "; 
	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    double num = 0;  
    double totValue = 0.00;  
    for (int i = 0; i < result.getCount(); i++) {    
    	num = num + result.getDouble("QTY",i);           
    	totValue = totValue +result.getDouble("TOT", i);  
   } 
	
	if(result == null){
		this.messageBox("无查询数据！");
	}   
    this.setValue("NUM", num);     
    this.setValue("TOT_VALUE", totValue); 
	this.callFunction("UI|TABLE|setParmValue", result); 
	//INV_PACKSTOCKM
	//INV_PACKSTOCKD
}


/**
 *  点击事件
 */ 
public void onTableClick(){
    int row =  this.getTable("TABLE").getClickedRow();
    TParm parm = this.getTable("TABLE").getParmValue().getRow(row); 
	String sql = " SELECT CON_FLG,ORG_CODE,PACK_CODE,USE_COST,PACK_SEQ_NO,VALUE_DATE " +
	" FROM INV_PACKSTOCKM " +    
	" WHERE PACK_CODE = '"+parm.getValue("PACK_CODE")+"' " ;
    String orgCode = this.getValueString("ORG_CODE");
    if("".equals(orgCode)|| orgCode==null){
       sql =  sql+ " AND ORG_CODE = '"+orgCode+"' ";    
    }   
    TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//    this.setValue("NUM", num);     
//    this.setValue("TOT_VALUE", AllFee);      
    this.callFunction("UI|TABLED|setParmValue", result); 
}
/**  
 * 得到表控件
 * @param tagName String
 * @return TTable 
 */
    private TTable getTable(String tagName) {
       return (TTable) getComponent(tagName);
    }
   
}
