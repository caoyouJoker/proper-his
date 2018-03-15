package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * 
 * <p>
 * Title:打包入库Tool
 * </p>
 * 
 * <p>
 * Description: 打包入库Tool
 * </p>
 * 
 * <p>
 * Copyright (c) ProperSoft 2013
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author wangzl 20130411
 * @version 1.0
 */
public class INVPackageTool extends TJDODBTool {
	/** 实例 */
	private static INVPackageTool instance;

	/** 得到实例 */
	public static INVPackageTool getInstance() {
		if (instance == null)
			return instance = new INVPackageTool();
		return instance;
	}

	/**
	 * 查询手术包主档
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onQuery(TParm parm) {
		String ORG_CODE=parm.getValue("ORG_CODE");
		String PACK_CODE=parm.getValue("PACK_CODE");
		String PACK_SEQ_NO=parm.getValue("PACK_SEQ_NO");
		
		String ORG_CODESql="";
		String PACK_CODESql="";
		String PACK_SEQ_NOSql="";
		if(ORG_CODE.length()!=0)
			ORG_CODESql=" AND A.ORG_CODE='"+ORG_CODE+"'";
		if(PACK_CODE.length()!=0)
			PACK_CODESql=" AND A.PACK_CODE='"+PACK_CODE+"'";
		if(PACK_SEQ_NO.length()!=0)
			PACK_SEQ_NOSql=" AND A.PACK_SEQ_NO='"+PACK_SEQ_NO+"'";
	
		
		String sql = "SELECT B.PACK_DESC, A.PACK_SEQ_NO, A.QTY, A.STATUS, B.USE_COST, A.ONCE_USE_COST, "
						  + "A.DESCRIPTION, A.OPT_USER, A.OPT_DATE, A.OPT_TERM, A.ORG_CODE, A.PACK_CODE "
					 + "FROM INV_PACKSTOCKM A, INV_PACKM B "
					+ "WHERE A.PACK_CODE = B.PACK_CODE"+ORG_CODESql+PACK_CODESql+PACK_SEQ_NOSql;
		TParm result = new TParm(this.select(sql));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
	/**点击主表 根据PACK_CODE 和PACK_SEQ_NO 查询细表数据*/
	public TParm onQueryINVPackDInfo(TParm parm){
		String sql = "SELECT A.INV_CODE,B.INV_CHN_DESC,B.STOCK_UNIT,C.QTY,"
						  + "B.COST_PRICE, B.SEQMAN_FLG, C.RECOUNT_TIME, C.ONCE_USE_FLG, "
						  + "C.INVSEQ_NO "
				    + " FROM INV_PACKD A, INV_BASE B, INV_PACKSTOCKD C "
				    + "WHERE A.INV_CODE = B.INV_CODE "
				      + "AND A.INV_CODE = C.INV_CODE "
				      + "AND A.PACK_CODE = C.PACK_CODE "
				      + "AND B.INV_CODE = C.INV_CODE " 
				      + "AND A.PACK_CODE = '"+ parm.getData("PACK_CODE") + "' " 
				      + "AND C.PACK_SEQ_NO = '"+parm.getData("PACK_SEQ_NO")+"'";
		TParm result = new TParm(this.select(sql));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
	 /**
     * 取得单位信息
     * @param unit_code String
     * @return TParm
     */
	public TParm onQueryBySYSUnit(String unit_code){
		String sql="SELECT * FROM SYS_UNIT WHERE UNIT_CODE = '" + unit_code + "'";
		TParm result = new TParm(this.select(sql));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
	/**得到手術包庫存細項信息*/
	public TParm onQueryByPackStockd(TParm parm){
		String sql = "SELECT ORG_CODE, PACK_CODE, PACK_SEQ_NO, "
						  + "INV_CODE, INVSEQ_NO, BATCH_SEQ, "
						  + "DESCRIPTION, RECOUNT_TIME, COST_PRICE, "
						  + "QTY, STOCK_UNIT, ONCE_USE_FLG, "
						  + "OPT_USER, OPT_DATE, OPT_TERM "
				     + "FROM INV_PACKSTOCKD "
				     +"WHERE ORG_CODE='"+parm.getData("ORG_CODE")+"' "
				     +  "AND PACK_CODE='"+parm.getData("PACK_CODE")+"' "
				     +  "AND PACK_SEQ_NO='"+parm.getData("PACK_SEQ_NO")+"' "
				     +  "AND INV_CODE='"+parm.getData("INV_CODE")+"'";
		TParm result = new TParm(this.select(sql));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
	/**得到 手术报库存主项信息*/
	public TParm onQueryByPackStockM(TParm parm){
		String sql = "SELECT ORG_CODE, PACK_CODE, PACK_SEQ_NO, DESCRIPTION, "
						  + "RECYCLE_DATE, WASH_DATE, QTY, DISINFECTION_DATE, DISINFECTION_VALID_DATE, "
						  + "DISINFECTION_POTSEQ, DISINFECTION_PROGRAM, DISINFECTION_USER, "
						  + "STERILIZATION_DATE, STERILIZATION_VALID_DATE, STERILIZATION_POTSEQ, "
						  + "STERILIZATION_PROGRAM, STERILIZATION_USER, PACK_DATE, "
						  + "USE_COST, ONCE_USE_COST, STATUS, "
						  + "OPT_USER, OPT_DATE, OPT_TERM "
				     + "FROM INV_PACKSTOCKM "
				     +"WHERE ORG_CODE='"+parm.getData("ORG_CODE")+"' "
				     +  "AND PACK_CODE='"+parm.getData("PACK_CODE")+"' "
				     +  "AND PACK_SEQ_NO='"+parm.getData("PACK_SEQ_NO")+"' ";
		TParm result = new TParm(this.select(sql));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
	/** 根据 部门代码 物资序号 得到 物资库存 INVSTOCKD 的库存量*/
	public TParm onQueryByINVStockd(TParm parm){
		String sql="SELECT STOCK_QTY FROM INV_STOCKD " 
				 + " WHERE ORG_CODE = '" +parm.getData("ORG_CODE") + "' " 
				 +    "AND INV_CODE = '" +parm.getData("INV_CODE") + "' " 
				 + " ORDER BY BATCH_SEQ";
		TParm result = new TParm(this.select(sql));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
	/**根据 部门代码 物资序号 得到物资库存 INVSTOCKD 该物资的总库存量*/
	public TParm onQueryByInvStockDSumQty(TParm parm){
		String sql="SELECT SUM(A.STOCK_QTY) AS STOCK_QTY, B.INV_CHN_DESC "
			+ " FROM INV_STOCKD A, INV_BASE B "
            + "WHERE A.ORG_CODE = '" + parm.getData("ORG_CODE") + "' " 
            +   "AND A.INV_CODE = '" +parm.getData("INV_CODE") + "' " 
            +   "AND A.INV_CODE = B.INV_CODE GROUP BY B.INV_CHN_DESC";
		TParm result = new TParm(this.select(sql));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
	/**得到手术包最大的序号*/
	public TParm onQueryByPackMaxSeq(TParm parm){
		String sql="SELECT MAX(PACK_SEQ_NO) AS PACK_SEQ_NO FROM INV_PACKSTOCKM "
				  + "WHERE ORG_CODE = '" + parm.getData("ORG_CODE") + "' " 
				  +   "AND PACK_CODE = '" +parm.getData("PACK_CODE") + "'";
		TParm result = new TParm(this.select(sql));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
	/**得到手术包明细的价格*/
	public TParm onQueryByINVPackDByCostPrice(TParm parm){
		String sql = "SELECT B.COST_PRICE, A.QTY FROM INV_PACKD A, INV_BASE B "
				   + " WHERE A.INV_CODE = B.INV_CODE " 
				   +    "AND A.PACK_CODE = '" + parm.getData("PACK_CODE") +"'";
		TParm result = new TParm(this.select(sql));
		if(result.getErrCode()<0)
			return result;
		return result;
	}
	/**
	 * 手术包打包
	 * 
	 * @param parm
	 *            TParm
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm onInvPackage(TParm parm, TConnection conn){
		TParm result = new TParm();
		// 更新库存主项数据INV_STOCKM
		if (parm.existData("INV_STOCKM")) {
			TParm invStockMParm = parm.getParm("INV_STOCKM");
			for (int i = 0; i < invStockMParm.getCount("INV_CODE"); i++) {
				result = InvStockMTool.getInstance().onUpdateStockQtyByPack(
						invStockMParm.getRow(i), conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		// 更新库存细项数据INV_STOCKD
		if (parm.existData("INV_STOCKD")) {
			TParm invStockDParm = parm.getParm("INV_STOCKD");
			for (int i = 0; i < invStockDParm.getCount("INV_CODE"); i++) {
				result = InvStockDTool.getInstance().onUpdateStockQtyByPack(
						invStockDParm.getRow(i), conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		// 更新库存明细项数据INV_STOCKDD
		if (parm.existData("INV_STOCKDD")) {
			TParm invStockDDParm = parm.getParm("INV_STOCKDD");
			for (int i = 0; i < invStockDDParm.getCount("INV_CODE"); i++) {
				result = InvStockDDTool.getInstance().onUpdateStockQtyByPack(
						invStockDDParm.getRow(i), conn);
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		// 更新手术包库存主项数据INV_PACKSTOCKM
		if (parm.existData("INV_PACKSTOCKM")) {
			TParm invPackStockMParm = parm.getParm("INV_PACKSTOCKM");
			if ("I".equals(invPackStockMParm.getValue("UPDATE"))) {
				result = InvPackStockMTool.getInstance()
						.onInsertStockQtyByPack(invPackStockMParm, conn);
			} else {
				result = InvPackStockMTool.getInstance()
						.onUpdateStockQtyByPack(invPackStockMParm, conn);
			}
			if (result.getErrCode() < 0) {
				return result;
			}
		}
		// 更新手术包库存细项数据INV_PACKSTOCKD
		if (parm.existData("INV_PACKSTOCKD")) {
			TParm invPackStockDParm = parm.getParm("INV_PACKSTOCKD");
			for (int i = 0; i < invPackStockDParm.getCount("PACK_CODE"); i++) {
				if ("I".equals(invPackStockDParm.getValue("UPDATE", i))) {
					result = InvPackStockDTool.getInstance()
							.onInsertStockQtyByPack(
									invPackStockDParm.getRow(i), conn);
				} else {
					result = InvPackStockDTool.getInstance()
							.onUpdateStockQtyByPack(
									invPackStockDParm.getRow(i), conn);
				}
				if (result.getErrCode() < 0) {
					return result;
				}
			}
		}
		return result;
	}
}
