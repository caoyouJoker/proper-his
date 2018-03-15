package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;


/**
 * <p>
 * Title: 物联网医院商药品编码比对
 * </p>
 *
 * <p>
 * Description: 物联网医院商药品编码比对
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
 * @author liuzhen 2013.1.17
 * @version 1.0
 */
public class SPCCodeMapHisTool extends TJDODBTool {
	
	 /** 实例*/
    public static SPCCodeMapHisTool instanceObject;

    /**得到实例*/
    public static SPCCodeMapHisTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCCodeMapHisTool();
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
    	String regionCode = parm.getValue("REGION_CODE");
    	    	
    	if(null != regionCode &&!"".equals(regionCode)){
    		cond2 = " AND A.REGION_CODE = '"+regionCode.trim()+"' ";
    	}
    	
    	String cond3 = "";
    	String hisOrderCode = parm.getValue("HIS_ORDER_CODE");
    	    	
    	if(null != hisOrderCode &&!"".equals(hisOrderCode)){
    		cond3 = " AND A.HIS_ORDER_CODE = '"+hisOrderCode.trim()+"' ";
    	}
    	   
    	String sql = "SELECT " +
    					"B.ORDER_CODE, A.REGION_CODE, A.HIS_ORDER_CODE, " +
    					"B.ORDER_DESC, B.SPECIFICATION, A.ACTIVE_FLG,A.OPT_DATE " +
    				"FROM SYS_FEE_SPC A, PHA_BASE B " +
    				"WHERE A.ORDER_CODE(+) = B.ORDER_CODE " + 
    				cond1 +
    				cond2 + 
    				cond3 +
    				" ORDER BY A.REGION_CODE";
    	
    	TParm result = new TParm(this.select(sql));

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    /**查询物联网药品信息*/
    public TParm updateQuery(TParm parm) {

    	String cond1 = "";
    	String orderCode = parm.getValue("ORDER_CODE");
    	
    	if(null != orderCode &&!"".equals(orderCode)){
    		cond1 = " AND A.ORDER_CODE LIKE '%"+orderCode+"%' ";
    	}
    	
    	String cond2 = "";
    	String regionCode = parm.getValue("REGION_CODE");
    	    	
    	if(null != regionCode &&!"".equals(regionCode)){
    		cond2 = " AND A.REGION_CODE = '"+regionCode.trim()+"' ";
    	}
    	   
    	String cond3 = "";
    	String active_flg = parm.getValue("ACTIVE_FLG");
    	    	
    	if(null != active_flg &&!"".equals(active_flg)){
    		cond3 = " AND ACTIVE_FLG = '"+active_flg.trim()+"' ";
    	}

    	String sql = "SELECT " +
    					"A.ORDER_CODE, A.REGION_CODE, A.HIS_ORDER_CODE, " +
    					"B.ORDER_DESC, B.SPECIFICATION " +
    				"FROM SYS_FEE_SPC A, PHA_BASE B " +
    				"WHERE A.ORDER_CODE = B.ORDER_CODE " + 
    				cond1 +
    				cond2 + 
    				cond3 +
    				" ORDER BY A.REGION_CODE";
    	
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
		
//		if(!queryBase(parm)) return false;
		
		String sql = "INSERT INTO SYS_FEE_SPC " +
							"(ORDER_CODE,REGION_CODE,HIS_ORDER_CODE,ACTIVE_FLG,OPT_DATE) " +
					"VALUES('"+parm.getData("ORDER_CODE")+"','"+
								parm.getData("REGION_CODE")+"','"+
								parm.getData("HIS_ORDER_CODE")+"','"+
								parm.getData("ACTIVE_FLG")+"',SYSDATE)";

		TParm result = new TParm(this.update(sql));
		if (result.getErrCode() < 0) return false;
		
		return true;
	}
	
	/**导入保存方法*/
	public boolean importSave(TParm parm) {
		
		// 保存之前先查询,数据库中是否有该记录
		TParm result = SPCCodeMapHisTool.getInstance().updateQuery(parm);

		if (result.getCount("ORDER_CODE") > 0) {
			boolean flg = SPCCodeMapHisTool.getInstance().update(parm);
			if (!flg) return false;
		} else {
			boolean flg = SPCCodeMapHisTool.getInstance().save(parm);
			if (!flg) return false;
		}
			
		return true;
	}
	
    
	/**修改方法*/
	public boolean update(TParm parm) {
			
		String sql = "UPDATE SYS_FEE_SPC " +
						 "SET " +
				 			"HIS_ORDER_CODE='" + parm.getData("HIS_ORDER_CODE") 
				 			+ "',ACTIVE_FLG='"+parm.getData("ACTIVE_FLG")+"',OPT_DATE=SYSDATE "+				
				 	"WHERE ORDER_CODE='"+ parm.getData("ORDER_CODE") + "' " +
						"AND REGION_CODE='"+parm.getData("REGION_CODE")+"'"+
						"AND ACTIVE_FLG='"+parm.getData("ACTIVE_FLG_UPDATE")+"'";
		TParm result = new TParm(this.update(sql));
		if (result.getErrCode() < 0) 
			return false;
		
		return true;
	}
	
    
	/**删除操作*/
	public boolean delete(TParm parm) {
		
		String sql = "DELETE SYS_FEE_SPC " +
					"WHERE " +
						"ORDER_CODE='"+parm.getData("ORDER_CODE")+"' " +
					"AND REGION_CODE='"+parm.getData("REGION_CODE")+"'" +
					"AND ACTIVE_FLG='"+parm.getData("ACTIVE_FLG")+"'";
		TParm result = new TParm(this.update(sql));
		if (result.getErrCode() < 0) return false;
		
		return true;
	}
	
	/**
	 * 判断是否一个order_code对应多个his_order_code
	 * @param parm
	 * @return
	 * @author shendr 2013.08.06
	 */
	public TParm queryCount(TParm parm){
		String sql="SELECT COUNT(HIS_ORDER_CODE) " +
				"FROM SYS_FEE_SPC " +
				"WHERE REGION_CODE='"
				+parm.getData("REGION_CODE")+"' " +
				"AND ORDER_CODE='"
				+parm.getData("ORDER_CODE")+"'";
		TParm result = new TParm(this.select(sql));
		return result;
	}
	
	/**
	 * 判断是否有相同的REGION_CODE和HIS_ORDER_CODE对应ACTIVE_FLG为Y
	 * @param parm
	 * @return
	 * @author shendr 2013.09.02
	 */
	public TParm queryCounts(TParm parm){
		String sql="SELECT  COUNT(ACTIVE_FLG) AS COUNTS FROM SYS_FEE_SPC "
					+"WHERE REGION_CODE='"+parm.getData("REGION_CODE")+"' "
					+"AND HIS_ORDER_CODE='"+parm.getData("HIS_ORDER_CODE")+"'";
		TParm result = new TParm(this.select(sql));
		return result;
	}
	
	/**
	 * 查询对应order_code的库存量
	 */
	public TParm queryQtyByOrderCode(TParm parm){
		String sql = "SELECT A.ORDER_CODE,B.STOCK_QTY,C.ORG_CHN_DESC "
					+"FROM SYS_FEE_SPC A,IND_STOCK B,IND_ORG C "
					+"WHERE A.ORDER_CODE='"+parm.getData("ORDER_CODE")+"'  "
					+"AND B.STOCK_QTY>0 "
					+"AND A.ORDER_CODE = B.ORDER_CODE "
					+"AND B.ORG_CODE = C.ORG_CODE ";
		TParm result = new TParm(this.select(sql));
		return result;
	}
	
	/**
	 * 判断这个物联网编码是否有对应的数据
	 * @param parm
	 * @return
	 * @author shendr 2013.08.22
	 */
	public TParm queryLastOrder(TParm parm){
		String sql = "SELECT ORDER_CODE " +
				"FROM SYS_FEE_SPC WHERE ORDER_CODE='"
			+parm.getData("ORDER_CODE")+"'";
		TParm result = new TParm(this.select(sql));
		return result;
	}
	
	/**
	 * 根据ORDER_CODE更新ACTIVE_FLG字段为未启用
	 * @return
	 * @author shendr 2013.08.22
	 */
	public TParm updateActiveFlg(TParm parm){
		String sql = "UPDATE SYS_FEE_SPC SET ACTIVE_FLG = 'N' " +
				"WHERE REGION_CODE='"+parm.getData("REGION_CODE")+"' " +
						"AND ORDER_CODE = '"+parm.getData("ORDER_CODE")+"'";
		TParm result = new TParm(this.update(sql));
		return result;
	}
	
	/**
	 * 根据ORDER_CODE查询单位及单位转换率
	 * @param parm
	 * @return
	 */
	public TParm queryPha(TParm parm){
		String sql="SELECT A.PURCH_UNIT,A.STOCK_UNIT,A.DOSAGE_UNIT,A.MEDI_UNIT,B.PURCH_QTY, "
			        +"B.PURCH_QTY,B.STOCK_QTY,B.DOSAGE_QTY,B.MEDI_QTY,B.OPT_USER, "
			        +"B.OPT_DATE,B.OPT_TERM,B.REGION_CODE "
			        +"FROM PHA_BASE A,PHA_TRANSUNIT B "
			        +"WHERE A.ORDER_CODE = '"
			        +parm.getValue("ORDER_CODE")
			        +"' "
			        +"AND A.ORDER_CODE = B.ORDER_CODE";
		TParm result = new TParm(this.update(sql));
		return result;
	}
	
}
