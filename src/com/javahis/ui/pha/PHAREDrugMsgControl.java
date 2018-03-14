package com.javahis.ui.pha;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
/**
 * 库存不足，提示替代药品
 * @author lix
 *
 */
public class PHAREDrugMsgControl extends TControl{
	public TTable table;
	 /*
     * 初始化方法
     */
    public void onInit() {
    	Object obj = this.getParameter();
    	//1.取obj库存不足code;
    	//String orderCode="2C010008";
    	//$$add by lx 加入提示=============$$//	
    	String orderCode=((TParm) obj).getValue("orderCode"); 	
    	String orderSql="SELECT ORDER_DESC FROM SYS_FEE";
    	orderSql+=" WHERE ORDER_CODE='"+orderCode+"'";
    	 TParm orderResult = new TParm(TJDODBTool.getInstance().select(orderSql));	
    	this.setValue("tblOrderDesc", orderResult.getValue("ORDER_DESC",0));
    	//$$add by lx 加入提示=============$$//	
    	//System.out.println("==orderCode=="+orderCode);
    	
        table = this.getTable("TABLE");
        String sql="SELECT A.REPLACE_ORDER_CODE,B.ORDER_DESC,A.DESCRIPTION";
               sql+=" FROM PHA_REDRUG A, SYS_FEE B";
               sql+=" WHERE A.ORDER_CODE='"+orderCode+"'";
               sql+=" AND A.REPLACE_ORDER_CODE=B.ORDER_CODE";
               sql+=" ORDER BY A.SEQ";
               
        //System.out.println("==sql=="+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        table.setParmValue(result);
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

}
