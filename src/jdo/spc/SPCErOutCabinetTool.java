package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class SPCErOutCabinetTool extends TJDOTool {

	/**
	 * 实例
	 */
	public static SPCErOutCabinetTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return ClinicRoomTool
	 */
	public static SPCErOutCabinetTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCErOutCabinetTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SPCErOutCabinetTool() {
		// setModuleName("spc\\SPCErOutCabinetModule.x");
		onInit();
	}

	public TParm onQuery(TParm parm) {
		 
		String rxNo = parm.getValue("RX_NO");
		String startDate = parm.getValue("START_DATE");
		String endDate = parm.getValue("END_DATE");
		// N:未出库 Y:已出库
		String status = parm.getValue("STATUS");

		String sql =  "  SELECT A.RX_NO,A.MR_NO,B.PAT_NAME,A.ORDER_DATE,D.USER_NAME "
					+ "  FROM OPD_ORDER A,SYS_PATINFO B,SYS_CTRLDRUGCLASS C,SYS_OPERATOR D "
					+ "  WHERE     A.DR_CODE = D.USER_ID "
					+ "        AND A.MR_NO = B.MR_NO(+) "
					+ "        AND A.CAT1_TYPE = 'PHA' "
					+ "        AND A.ADM_TYPE = 'E'   "
					+ "        AND (    A.CTRLDRUGCLASS_CODE IS NOT NULL "
					+ "             AND A.CTRLDRUGCLASS_CODE = C.CTRLDRUGCLASS_CODE "
					+ "             AND C.CTRL_FLG = 'Y') ";

		if(rxNo != null && !rxNo.equals("")){
			sql += " AND A.RX_NO='"+rxNo+"' " ;
		}
		
		if (status != null && !status.equals("")) {
			if (status.equals("N")) {
				sql += "  AND NVL (A.ACUM_OUTBOUND_QTY, 0) < A.DISPENSE_QTY ";
			} else if (status.equals("Y")) {
				sql += "  AND NVL (A.ACUM_OUTBOUND_QTY, 0) = A.DISPENSE_QTY ";
			}
		}

		if (startDate != null && !startDate.equals("")) {
			sql += " AND A.ORDER_DATE >=TO_DATE('" + startDate + "','YYYYMMDDHH24MISS') ";
		}

		if (endDate != null && !endDate.equals("")) {
			sql += " AND A.ORDER_DATE <=TO_DATE('" + endDate + "','YYYYMMDDHH24MISS') ";
		}

		sql += "	GROUP BY A.RX_NO,A.MR_NO,B.PAT_NAME,A.ORDER_DATE,D.USER_NAME ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	/**
	 * 根据处方签号查询对应的数据列表
	 * @param parm
	 * @return
	 */
	public TParm onQueryOpd(TParm parm){
		String rxNo = parm.getValue("RX_NO");
		if(rxNo == null || rxNo.equals("")){
			System.out.println("--------------处方签号为空------------------");
			return new TParm();
		}
		String sql = "  SELECT A.CASE_NO,A.RX_NO,A.SEQ_NO,A.ORDER_DESC||A.SPECIFICATION AS ORDER_DESC, "
					+" 		  D.PAT_NAME,A.ACUM_OUTBOUND_QTY,A.DOSAGE_QTY,TO_CHAR (A.MEDI_QTY, 'FM9999990.099') || '' || E.UNIT_CHN_DESC AS MEDI_QTY, "
				    +"        F.ROUTE_CHN_DESC,G.FREQ_CHN_DESC,A.TAKE_DAYS,TO_CHAR (A.DISPENSE_QTY, 'FM9999990.099') AS DISPENSE_QTY, "
				    +"        H.UNIT_CHN_DESC AS DISPENSE_UNIT,A.ORDER_CODE "
					+"	FROM OPD_ORDER A,SYS_CTRLDRUGCLASS C, SYS_PATINFO D,SYS_UNIT E,SYS_PHAROUTE F, "
					+"	     SYS_PHAFREQ G,SYS_UNIT H "      
					+"	WHERE     C.CTRLDRUGCLASS_CODE(+) = A.CTRLDRUGCLASS_CODE "
					+"	       AND C.CTRL_FLG = 'Y'  "
					+"	       AND D.MR_NO = A.MR_NO  "
					+"	       AND E.UNIT_CODE(+) = A.MEDI_UNIT  "
					+"	       AND F.ROUTE_CODE(+) = A.ROUTE_CODE  "
					+"	       AND G.FREQ_CODE(+) = A.FREQ_CODE "
					+"	       AND H.UNIT_CODE(+) = A.DISPENSE_UNIT " 
					+"		   AND A.RX_NO = '"+rxNo+"' ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	public TParm onUpdateOpdorderNum(TParm parm,TConnection conn){
		
		String toxicId = parm.getValue("TOXIC_ID");
		String filed = "A.TOXIC_ID";
		String sql = " UPDATE OPD_ORDER A SET " + filed+"='"+toxicId+"' , A.ACUM_OUTBOUND_QTY= NVL(A.ACUM_OUTBOUND_QTY,0) + 1 "+
	     " WHERE A.CASE_NO='"+parm.getValue("CASE_NO")+"' AND A.SEQ_NO="+parm.getValue("SEQ_NO")+" "+
	     "          AND A.RX_NO='"+parm.getValue("RX_NO")+"'  ";
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

}
