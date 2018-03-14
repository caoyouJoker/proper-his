package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 物联网供应商药品编码比对
 * </p>
 *
 * <p>
 * Description: 物联网供应商药品编码比对
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 *
 * <p>
 * Company: BLUECORE
 * </p>
 *
 * @author liuzhen 2013.1.17
 * @version 1.0
 */
public class SPCCodeMapTool extends TJDODBTool {
	
	 /** 实例*/
    public static SPCCodeMapTool instanceObject;

    /**得到实例*/
    public static SPCCodeMapTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCCodeMapTool();
        return instanceObject;
    }
    
    
    /**查询物联网药品信息*/
    public TParm query(TParm parm) {

    	String cond1 = "";
    	String orderCode = parm.getValue("ORDER_CODE");
    	
    	if(null != orderCode &&!"".equals(orderCode)){
    		cond1 = " AND A.ORDER_CODE LIKE '%"+orderCode+"%' ";
    	}
    	
    	String cond2 = "";
    	String supCode = parm.getValue("SUP_CODE");
    	    	
    	if(null != supCode &&!"".equals(supCode)){
    		cond2 = " AND A.SUP_CODE = '"+supCode.trim()+"' ";
    	}
    	
    	String cond3 = "";
    	String supOrderCode = parm.getValue("SUP_ORDER_CODE");
    	    	
    	if(null != supCode &&!"".equals(supOrderCode)){
    		cond3 = " AND A.SUP_ORDER_CODE = '"+supOrderCode.trim()+"' ";
    	}
    	   

    	String sql = "SELECT " +
    					"B.ORDER_CODE, A.SUP_CODE, A.SUP_ORDER_CODE, " +
    					"B.ORDER_DESC, B.SPECIFICATION,A.SUPPLY_UNIT_CODE,A.CONVERSION_RATIO " +
    				"FROM IND_CODE_MAP A, PHA_BASE B " +
    				"WHERE A.ORDER_CODE(+) = B.ORDER_CODE " + 
    				cond1 +
    				cond2 + 
    				cond3 + 
    				" ORDER BY A.SUP_CODE";
    	
    	TParm result = new TParm(this.select(sql));

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    /**查询物联网药品信息*/
    public TParm updateQuery(TParm parm) {

    	String orderCode = parm.getValue("ORDER_CODE");    	
    	String cond1 = " AND A.ORDER_CODE = '"+orderCode+"' ";
    	
    	// <----- 主键更改 identity by shendr 20131030
//    	String supCode = parm.getValue("SUP_CODE");    	    	
//    	String	cond2 = " AND A.SUP_CODE = '"+supCode+"' ";
    	String supOrderCode = parm.getValue("SUP_ORDER_CODE");    	    	
    	String	cond2 = " AND A.SUP_ORDER_CODE = '"+supOrderCode+"' ";  
    	// ------>
    	String sql = "SELECT " +
    					"A.ORDER_CODE, A.SUP_CODE, A.SUP_ORDER_CODE, " +
    					"B.ORDER_DESC, B.SPECIFICATION " +
    				"FROM IND_CODE_MAP A, PHA_BASE B " +
    				"WHERE A.ORDER_CODE = B.ORDER_CODE " + 
    				cond1 +
    				cond2;
    	
    	TParm result = new TParm(this.select(sql));

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    /**查询PHA_BASE中是否有该药品*/
    public boolean queryBase(TParm parm){
    	
    	String sql = "SELECT ORDER_CODE FROM PHA_BASE WHERE ORDER_CODE='" + parm.getData("ORDER_CODE") + "'";
    	TParm result = new TParm(this.select(sql));
    	
    	if (result.getErrCode() < 0 || result.getCount()<=0) return false;
    	
    	return true;
    }
    
	/**保存方法*/
	public boolean save(TParm parm) {
		
		String sql = "INSERT INTO IND_CODE_MAP " +
							"(ORDER_CODE,SUP_CODE,SUP_ORDER_CODE,SUPPLY_UNIT_CODE," +
							"CONVERSION_RATIO,OPT_USER,OPT_DATE,OPT_TERM) " +
					"VALUES('"+parm.getData("ORDER_CODE")+"','"+
								parm.getData("SUP_CODE")+"','"+
								parm.getData("SUP_ORDER_CODE")+"','"+
								parm.getData("SUPPLY_UNIT_CODE")+"',"+
								parm.getDouble("CONVERSION_RATIO")+",'"+
								parm.getValue("OPT_USER")+"',SYSDATE,'"+
								parm.getValue("OPT_TERM")+"')";

		TParm result = new TParm(this.update(sql));
		if (result.getErrCode() < 0) return false;
		
		return true;
	}
	
	/**导入保存方法*/
	public boolean importSave(TParm parm) {
		
		// 保存之前先查询,数据库中是否有该记录
		TParm result = SPCCodeMapTool.getInstance().updateQuery(parm);

		if (result.getCount("ORDER_CODE") > 0) {
			boolean flg = SPCCodeMapTool.getInstance().update(parm);
			if (!flg) return false;
		} else {
			boolean flg = SPCCodeMapTool.getInstance().save(parm);
			if (!flg) return false;
		}
			
		return true;
	}
	
    
	/**修改方法*/
	public boolean update(TParm parm) {
			
		String sql = "UPDATE IND_CODE_MAP " +
						 "SET " +
				 			"ORDER_CODE='" + parm.getData("ORDER_CODE") + "', "+
				 			"SUPPLY_UNIT_CODE='"+parm.getData("SUPPLY_UNIT_CODE")+"', "+
				 			"CONVERSION_RATIO='"+parm.getData("CONVERSION_RATIO")+"', "+
				 			"OPT_USER='"+parm.getValue("OPT_USER")+"',"+
				 			"OPT_DATE=SYSDATE," +
				 			"OPT_TERM='"+parm.getValue("OPT_TERM")+"' "+
				 	"WHERE SUP_CODE='"+ parm.getData("SUP_CODE") + "' " +
						"AND SUP_ORDER_CODE='"+parm.getData("SUP_ORDER_CODE")+"'" ;
		
		TParm result = new TParm(this.update(sql));
		if (result.getErrCode() < 0) 
			return false;
		
		return true;
	}
	
    
	/**删除操作*/
	public boolean delete(TParm parm) {
		
		String sql = "DELETE IND_CODE_MAP " +
					"WHERE " +
						"SUP_CODE='"+parm.getData("SUP_CODE")+"' " +
					"AND SUP_ORDER_CODE='"+parm.getData("SUP_ORDER_CODE")+"'";
		
		TParm result = new TParm(this.update(sql));
		if (result.getErrCode() < 0) return false;
		
		return true;
	}
	
	/**
	 * 查询库存单位
	 * @return
	 */
	public TParm queryPhaUnit(String order_code){
		String sql = "SELECT B.UNIT_CHN_DESC "
					+"FROM PHA_BASE A,SYS_UNIT B "
					+"WHERE A.STOCK_UNIT = B.UNIT_CODE "
					+"AND A.ORDER_CODE = '"+order_code+"'";
		TParm result = new TParm(this.select(sql));
		return result;
	}
	
}
