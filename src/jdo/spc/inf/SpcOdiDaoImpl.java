package jdo.spc.inf;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class SpcOdiDaoImpl extends TJDOTool {
	
	/**
	 * 实例
	 */
	public static SpcOdiDaoImpl instanceObject;
	
	/**
	 * 得到实例
	 * 
	 * @return INDTool
	 */
	public static SpcOdiDaoImpl getInstance() {
		if (instanceObject == null)
			instanceObject = new SpcOdiDaoImpl();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SpcOdiDaoImpl() {
		onInit();
	}
	
	public TParm inwCheck(TParm parm){
		String stationCode = parm.getValue("STATION_CODE");
		String regionCode =  parm.getValue("REGION_CODE") ;
		TParm orderParm = parm.getParm("ORDER");
		int count = orderParm.getCount("ORDER_CODE") ;
		
		//构造返回结果
		TParm result = new TParm();
		result.setData("STATION_CODE",stationCode);
		result.setData("REGION_CODE",regionCode);
		TParm returnParm = new TParm();
		
		
		/**
		 * 循环查找ORDERCODE对应的库存数 2013/7/16修改为查询IND_STOCK
		 */
		for(int i = 0; i < count ; i++ ) {
			 
			String orderCode = orderParm.getValue("ORDER_CODE",i) ;
			String sql ="  SELECT E.HIS_ORDER_CODE,SUM(E.TOT_QTY) QTY "+
						"  FROM (  "+
						"       SELECT C.HIS_ORDER_CODE,SUM(B.STOCK_QTY* (CASE WHEN D.STOCK_UNIT=B.STOCK_UNIT THEN D.STOCK_QTY*D.DOSAGE_QTY ELSE 1 END) ) AS TOT_QTY  "+
						"			FROM IND_CABINET A, IND_STOCK  B,SYS_FEE_SPC C,PHA_TRANSUNIT D "+
						"			     WHERE  B.ORG_CODE = A.ORG_CODE "+
						"			       AND C.ORDER_CODE=B.ORDER_CODE "+
						"			       AND D.ORDER_CODE=C.ORDER_CODE "+
						"			       AND A.ORG_CODE='"+stationCode+"'  "+
						"			       AND C.REGION_CODE='"+regionCode+"' "+
						"			       AND C.HIS_ORDER_CODE='"+orderCode+"'  "+
						"			    GROUP BY C.HIS_ORDER_CODE "+
						"			    UNION ALL "+
						"			    SELECT C.HIS_ORDER_CODE,COUNT(B.TOXIC_ID) AS TOT_QTY "+
						"			      FROM IND_CABINET A,IND_CONTAINERD B,SYS_FEE_SPC C "+
						"			     WHERE B.CABINET_ID=A.CABINET_ID "+
						"			       AND C.ORDER_CODE=B.ORDER_CODE "+
						"			       AND  A.ORG_CODE='"+stationCode+"' "+
						"			       AND C.REGION_CODE='"+regionCode+"' "+
						"			       AND C.HIS_ORDER_CODE='"+orderCode+"' "+
						"			    GROUP BY C.HIS_ORDER_CODE "+
						"    ) E GROUP BY E.HIS_ORDER_CODE " ;
			

			TParm searchParm = new TParm( TJDODBTool.getInstance().select(sql));
			
			double qty = searchParm.getDouble("QTY",0);
			returnParm.setData("QTY",i,qty);
			returnParm.setData("ORDER_CODE",i,orderCode);
		}
		
		
		result.setData("ORDER",returnParm.getData());
		return result;
	}
	
}
