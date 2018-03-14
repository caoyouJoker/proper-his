package com.javahis.ui.bms;

import javax.swing.SwingUtilities;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
/**
 * 血液库存统计表
 * @author wangqing 20180117
 *
 */
public class BMSBloodStatisticsControl extends TControl {

	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
//		this.messageBox("血液库存统计表_初始化");
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				onQuery();
			}
		});
		
	}
	
	/**
	 * 刷新
	 */
	public void onReset(){
		onQuery();
	}
		
	/**
	 * 查询
	 */
	public void onQuery(){
		TTable table = (TTable) this.getComponent("TABLE");
		table.removeRowAll();
		String sql = /* Formatted on 2018/1/17 12:13:23 (QP5 v5.227.12220.39754) */


				"WITH M"
				+ " AS (  SELECT A.BLD_CODE,"
				+ "   A.BLDCODE_DESC,"
				+ "   A.UNIT_CODE,"
				+ "   B.UNIT_CHN_DESC"
				+ "  FROM BMS_BLDCODE A, SYS_UNIT B"
				+ "   WHERE A.UNIT_CODE = B.UNIT_CODE(+)"
				+ "   ORDER BY A.BLD_CODE),"
				+ "		N"
				+ "  AS (  SELECT A.BLD_CODE,"
				+ "          (CASE A.BLD_CODE"
				+ "           WHEN '01' THEN 7"
				+ "                 WHEN '02' THEN 7"
				+ "                   WHEN '05' THEN 30"
				+ "                    WHEN '06' THEN 1"
				+ "		                      WHEN '07' THEN 7"
				+ "		                      WHEN '08' THEN 7 / 24"
				+ "			                      WHEN '09' THEN 30"
				+ " WHEN '11' THEN 7"
				+ "   WHEN '12' THEN 30"
				+ "        WHEN '13' THEN 7 / 24"
				+ "         ELSE 0"
				+ "            END)"
				+ "                   AS DATE_1"
				+ "        FROM BMS_BLDCODE A"
				+ "    ORDER BY BLD_CODE),"
				+ "    O"
				+ "    AS (  SELECT A.BLD_CODE,"
				+ "          A.BLD_TYPE,"
				+ "       A.RH_FLG,"
				+ "               SUM (A.BLOOD_VOL) AS COUNT"
				+ "      FROM BMS_BLOOD A"
				+ "         WHERE A.STATE_CODE IN ('0', '1')"
				+ "     GROUP BY A.BLD_CODE, A.BLD_TYPE, A.RH_FLG"
				+ "      ORDER BY A.BLD_CODE, A.BLD_TYPE, A.RH_FLG),"
				+ "     P"
				+ "  AS (  SELECT O.BLD_CODE, SUM (O.COUNT) AS COUNT"
				+ "       FROM O"
				+ "   GROUP BY O.BLD_CODE"
				+ "  ORDER BY O.BLD_CODE),"
				+ "  Q"
				+ "  AS (  SELECT A.BLD_CODE, SUM (A.BLOOD_VOL) AS COUNT"
				+ "     FROM BMS_BLOOD A, N"
				+ "       WHERE     A.BLD_CODE = N.BLD_CODE"
				+ "     AND A.STATE_CODE IN ('0', '1')"
				+ "    AND A.END_DATE <= SYSDATE + N.DATE_1 AND A.END_DATE>=SYSDATE "
				+ "  GROUP BY A.BLD_CODE"
				+ "  ORDER BY A.BLD_CODE) "
				+ "SELECT M.BLDCODE_DESC,"
				+ " M.UNIT_CHN_DESC,"
				+ "  (SELECT O.COUNT"
				+ "     FROM O"
				+ "     WHERE     O.BLD_CODE = M.BLD_CODE"
				+ "    AND O.BLD_TYPE = 'A'"
				+ "        AND O.RH_FLG = '+')"
				+ "     AS A_POSITIVE,"
				+ "  (SELECT O.COUNT"
				+ "  FROM O"
				+ "   WHERE     O.BLD_CODE = M.BLD_CODE"
				+ "     AND O.BLD_TYPE = 'A'"
				+ "     AND O.RH_FLG = '-')"
				+ "   AS A_NEGATIVE,"
				+ "    (SELECT O.COUNT"
				+ "    FROM O"
				+ "   WHERE     O.BLD_CODE = M.BLD_CODE"
				+ "      AND O.BLD_TYPE = 'B'"
				+ "    AND O.RH_FLG = '+')"
				+ "   AS B_POSITIVE,"
				+ "  (SELECT O.COUNT"
				+ "    FROM O"
				+ "   WHERE     O.BLD_CODE = M.BLD_CODE"
				+ "    AND O.BLD_TYPE = 'B'"
				+ "          AND O.RH_FLG = '-')"
				+ "      AS B_NEGATIVE,"
				+ "      (SELECT O.COUNT"
				+ "        FROM O"
				+ "    WHERE     O.BLD_CODE = M.BLD_CODE"
				+ "           AND O.BLD_TYPE = 'O'"
				+ "      AND O.RH_FLG = '+')"
				+ "       AS O_POSITIVE,"
				+ "   (SELECT O.COUNT"
				+ "   FROM O"
				+ "     WHERE     O.BLD_CODE = M.BLD_CODE"
				+ "     AND O.BLD_TYPE = 'O'"
				+ "       AND O.RH_FLG = '-')"
				+ "    AS O_NEGATIVE,"
				+ "   (SELECT O.COUNT"
				+ "   FROM O"
				+ "   WHERE     O.BLD_CODE = M.BLD_CODE"
				+ "  AND O.BLD_TYPE = 'AB'"
				+ "     AND O.RH_FLG = '+')"
				+ "    AS AB_POSITIVE,"
				+ "   (SELECT O.COUNT"
				+ "   FROM O"
				+ "   WHERE     O.BLD_CODE = M.BLD_CODE"
				+ "     AND O.BLD_TYPE = 'AB'"
				+ "     AND O.RH_FLG = '-')"
				+ "     AS AB_NAGATIVE,"
				+ "   (SELECT P.COUNT"
				+ "    FROM P"
				+ "    WHERE P.BLD_CODE = M.BLD_CODE)"
				+ "   AS AB_TOTAL,"
				+ " (SELECT Q.COUNT"
				+ "   FROM Q"
				+ "   WHERE Q.BLD_CODE = M.BLD_CODE)"
				+ "     AS AB_NEAR_TERM_EFFECT"
				+ "	  FROM M";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			return;
		}
//		for(int i=0; i<result.getCount(); i++){
//			if(result.getInt("A_POSITIVE", i)==null || result.getInt("A_POSITIVE", i).){
//				
//			}
//		}
		
		
		
		
		table.setParmValue(result);
				
	}
	
	/**
	 * 导出excel
	 */
	public void onExport(){
		TTable table = (TTable) this.getComponent("TABLE");
		if (table.getRowCount()<1) {
			return;
		}		 
		ExportExcelUtil.getInstance().exportExcelForBmsBloodStatistics(table, "血液库存统计表");		
	}

	
	
	
}
