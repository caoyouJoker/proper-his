package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class INVRegressGoodTool extends TJDOTool{

	 /**
     * 实例
     */
    public static INVRegressGoodTool instanceObject;
    /**
     * 得到实例  
     * @return SPCINVRecordTool
     */
    public static INVRegressGoodTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVRegressGoodTool();
        return instanceObject; 
    }

    /**
     * 构造器  
     */
    public INVRegressGoodTool() {
    	  setModuleName("inv\\INVRegressGoodModule.x");
	      onInit();
    }
    
    /**
     * 插入数据
     * */
    public TParm insertData(TParm parm, TConnection connection) {
        TParm result = this.update("insertData", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**    
     * 插入数据
     * */
    public TParm insertDataForSingleUse(TParm parm, TConnection connection) {
        TParm result = this.update("insertDataForSingleUse", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 插入数据
     * */
    public TParm insertDataForBaseQty(TParm parm, TConnection connection) {
        TParm result = this.update("insertDataForBaseQty", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 插入数据
     * */
    public TParm upStockMBaseQty(TParm parm, TConnection connection) {
        TParm result = this.update("updInvStockMBaseQty", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 插入数据  盘点  
     * */
    public TParm insertDataForQtyCheck(TParm parm, TConnection connection) {
        TParm result = this.update("insertDataForQtyCheck", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 扣库 
     * INV_STOCKM
     * */   
    public TParm updInvStockM(TParm parm, TConnection connection) {
        TParm result = this.update("updInvStockM", parm, connection);//11111
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    /**
     * 扣库
     * INV_STOCKD
     * */
    public TParm updInvStockD(TParm parm, TConnection connection) {
        TParm result = this.update("updInvStockD", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
      
    /**    
     * 盘点库存
     * INV_STOCKD   
     * */
    public TParm CheckUpdInvStockD(TParm parm, TConnection connection) { 
        TParm result = this.update("CheckUpdInvStockD", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    /**
     * 扣库            
     * INV_STOCKDD
     * */
    public TParm updInvStockDD(TParm parm, TConnection connection) {
        TParm result = update("updInvStockDD", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }

    /**
     * 根据退货号或供应商查询
     * @param parm
     */
	public TParm onQuery(TParm parm) {
		TParm result = null;
		String sqlWhere = "",returnNo = "",supCode = "";
		if(parm != null){
			returnNo = parm.getValue("RETURN_NO");
			supCode = parm.getValue("SUP_CODE");
			if(returnNo != null && returnNo.trim().length() > 0)
				sqlWhere = " AND RETURN_NO = '" + returnNo + "'";
			if(supCode != null && supCode.trim().length() > 0)
				sqlWhere = " AND A.SUP_CODE = '" + supCode + "'";
		}
		String sql = " SELECT RETURN_NO,A.INV_CODE,B.INV_CHN_DESC,RFID,DESCRIPTION,A.QTY,A.SUP_CODE,B.MAN_CODE" +
					 " FROM INV_RETURNHIGH A" +
					 " LEFT JOIN INV_BASE B ON A.INV_CODE = B.INV_CODE" +
					 " WHERE 1=1" + sqlWhere;
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
		return result;
	}
	
	
	   /**
     * 根据退货号或供应商查询
     * @param parm
     */
	public TParm onQueryForBaseQty(TParm parm) {
		TParm result = null;
		String sqlWhere = "",baseNo = "",orgCode = "";
		if(parm != null){
			baseNo = parm.getValue("BASE_NO");
			orgCode = parm.getValue("ORG_CODE");
			if(baseNo != null && baseNo.trim().length() > 0)
				sqlWhere = " AND BASE_NO = '" + baseNo + "'";
			if(orgCode != null && orgCode.trim().length() > 0)
				sqlWhere = " AND ORG_CODE = '" + orgCode + "'";
		}
		String sql = " SELECT BASE_NO,BASE_DATE,ORG_CODE " +
					 " FROM INV_BASEQTY " +
					 " WHERE 1=1" + sqlWhere;
		sql+=" group by BASE_NO,BASE_DATE,ORG_CODE ";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
		return result;
	}
}
