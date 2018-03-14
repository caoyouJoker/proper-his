package com.javahis.manager;

import java.util.Vector;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.util.StringTool;

import jdo.spc.INDSQL;
import jdo.spc.INDTool; 
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;

/**
 * <p>
 * Title: 药库部门订购管理观察者
 * </p>
 *
 * <p>
 * Description: 药库部门订购管理观察者
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
 * @author zhangy 2009.05.13
 * @version 1.0
 */

public class IndPurOrderObserver
    extends TObserverAdapter {

    private double price = 0.00;
    private double qty = 0.0;          
    private double sum = 0.00;
    private String flg = "0";
    private String orgCode="";
    // 供货商代码
    private String supCode = "";
    // 判断是从移货申请调用还是其它订购管理调用
    private String spcOrIndS = "";

	// 获得prefech本地数据
    TDataStore dataStore;

    public IndPurOrderObserver() {
        dataStore = new TDataStore();
        dataStore.setSQL(
            "SELECT ORDER_CODE, ORDER_DESC, SPECIFICATION, GOODS_DESC FROM PHA_BASE");
        dataStore.retrieve();
    }

    /**
     *
     * @param s
     *            根据的ORDER_CODE
     * @param colName
     *            要查的列名
     * @return String
     */
    public String getTableShowValue(String s, String colName) {
        if (dataStore == null)
            return s;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER
            : dataStore.PRIMARY;
        TParm parm = dataStore.getBuffer(bufferString);
        Vector vKey = (Vector) parm.getData("ORDER_CODE");
        Vector vDesc = (Vector) parm.getData(colName);
        int count = vKey.size();
        for (int i = 0; i < count; i++) {
            if (s.equals(vKey.get(i)))
                return "" + vDesc.get(i);
        }
        return s;
    }

    /**
     * 得到其他列数据
     *
     * @param parm
     *            TParm
     * @param row
     *            int
     * @param column
     *            String
     * @return Object
     */
    public Object getOtherColumnValue(TDS ds, TParm parm, int row,
                                      String column) {
        if ("ORDER".equals(column)) {
            String orderCode = parm.getValue("ORDER_CODE", row);
            String icd_name = getTableShowValue(orderCode, "ORDER_DESC");
            String goods_desc = getTableShowValue(orderCode, "GOODS_DESC");
            icd_name = INDTool.getInstance().getOrderDescAndGoodsDesc("",
                icd_name, goods_desc);
            if ("".equals(icd_name))
                return "";
            // 给这个列一个值
            return icd_name;
        }
        if ("END_FLG".equals(column)) {
            flg = parm.getValue("UPDATE_FLG", row);
            if ("2".equals(flg)) {
                return "Y";
            }
            return "N";
        }

        if ("TOT_MONEY".equals(column)) {
            price = parm.getDouble("PURORDER_PRICE", row);
            qty = parm.getDouble("PURORDER_QTY", row);
            sum = StringTool.round(price * qty, 2);
//            TParm result = SYSRegionTool.getInstance().selectdata(Operator.getRegion());
//            String spc_flg = result.getValue("SPC_FLG",0);
            String orderCode = parm.getValue("ORDER_CODE", row);
            TParm unitResultParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getSupplyUnit(supCode, orderCode)));
            double conversion_ratio = unitResultParm.getDouble("CONVERSION_RATIO",0);
            if ("SPC".equals(spcOrIndS) && conversion_ratio == 0) {
        		TParm qtyResultParm = new TParm(TJDODBTool.getInstance().select(INDSQL.getStockQtyPhaTransunit(orderCode)));
	        	conversion_ratio = qtyResultParm.getDouble("STOCK_QTY",0);
			}      
            sum = sum * conversion_ratio;
            return sum;
        }
        if ("SPECIFICATION".equals(column)) {
            String orderCode = parm.getValue("ORDER_CODE", row);
            String icd_name = getTableShowValue(orderCode, "SPECIFICATION");
            if ("".equals(icd_name))
                return "";
            // 给这个列一个值
            return icd_name;
        }
        if("TOT_QTY".equals(column)){
 			double qty = jdo.spc.INDTool.getInstance().getStockQTY(orgCode, parm.getValue("ORDER_CODE",row));
			if(qty <  0){  
				qty = 0 ;
			}
			int unit = 1;
			TParm result = jdo.spc.INDTool.getInstance().getDosageQty(orgCode, parm.getValue("ORDER_CODE",row));
			if (result.getErrCode() < 0||result.getCount()<=0) {
				unit=1; 
			} 	else {		
				unit = result.getInt("DOSAGE_QTY",0);	
			}
        	return StringTool.round(qty/unit, 3);							
        }
        if("PACK_UNIT".equals(column)){
        	String packUnit="";  
			TParm result = jdo.spc.INDTool.getInstance().getPackUnit(parm.getValue("ORDER_CODE",row));
			if (result.getErrCode() < 0||result.getCount()<=0) {
				packUnit=""; 
			} 
			packUnit = result.getValue("PACK_UNIT", 0);
        	return packUnit;   
        }
        
        if("YS_PRICE".equals(column)){
        	double price=0;  
			TParm result = jdo.spc.INDTool.getInstance().getVerifyinPrice(orgCode, parm.getValue("ORDER_CODE",row));
			if (result.getErrCode() < 0||result.getCount()<=0) {
				price=0; 
			} 
			price = result.getDouble("PRICE", 0);	   
        	return price;      
        }
           
        return "";
    }

    /**
     * 设置其他列数据
     *
     * @param parm
     *            TParm
     * @param row
     *            int 激发该动作的行(table)
     * @param column
     *            String 激发该动作的列(table)
     * @param value
     *            Object
     * @return boolean 该列目前的值
     */
    public boolean setOtherColumnValue(TDS ds, TParm parm, int row,
                                       String column, Object value) {
        if (column.equals("ORDER")) {
            ds.setItem(row, "ORDER_CODE", parm.getValue("ORDER", row));
            return true;
        }
        if (column.equals("END_FLG")) {
            if ("Y".equals(value))
                ds.setItem(row, "UPDATE_FLG", "2");
            else
                ds.setItem(row, "UPDATE_FLG", "0");
            return true;
        }
        return true;
    }
     
	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getSupCode() {
		return supCode;
	}

	public void setSupCode(String supCode) {
		this.supCode = supCode;
	}
	
    public String getSpcOrIndS() {
		return spcOrIndS;
	}

	public void setSpcOrIndS(String spcOrIndS) {
		this.spcOrIndS = spcOrIndS;
	}
	
}
