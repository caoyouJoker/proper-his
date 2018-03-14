package jdo.ibs;

import java.sql.Timestamp;
import jdo.sys.Operator;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * 
 * <p>
 * Title: 住院费用明细档工具类
 * </p>
 * 
 * <p>
 * Description: 住院费用明细档工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangl
 * @version 1.0
 */
public class IBSOrderdTool extends TJDOTool {
	/**
	 * 实例
	 */
	public static IBSOrderdTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return IBSOrderdTool
	 */
	public static IBSOrderdTool getInstance() {
		if (instanceObject == null)
			instanceObject = new IBSOrderdTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public IBSOrderdTool() {
		setModuleName("ibs\\IBSOrderdModule.x");
		onInit();
	}

	/**
	 * 查询未产生账单的费用档数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectdata(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selectdate()参数异常!");
			return result;
		}
		result = query("selectdata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}
	
	
	/**
	 * 查询未产生账单的费用档数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectdata2(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selectdate()参数异常!");
			return result;
		}
		result = query("selectdata2", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * 查询收据项目(费用查询)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectdataAll(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selectdataAll()参数异常!");
			return result;
		}
		result = query("selectdataAll", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * 查询费用明细(费用查询)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selFeeDetail(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selFeeDetail()参数异常!");
			return result;
		}
		result = query("selFeeDetail", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * 查询计价明细(费用查询)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selBillDetail(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selBillDetail()参数异常!");
			return result;
		}
		result = query("selBillDetail", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * 查询单项医嘱(费用查询)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selSingleOrder(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selSingleOrder()参数异常!");
			return result;
		}
		result = query("selSingleOrder", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * 查询合并费用明细(费用查询)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selMergeFee(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selMergeFee()参数异常!");
			return result;
		}
		result = query("selMergeFee", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * 科室费用查询(费用查询)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selFeeByDept(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selFeeByDept()参数异常!");
			return result;
		}
		result = query("selFeeByDept", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * 查询未产生账单的费用明细档所有数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selOrderDAll(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selOrderDAll()参数异常!");
			return result;
		}
		result = query("selOrderDAll", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * 查询病患最小医嘱生效起时,最大生效迄时
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selOrderTime(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selOrderTime()参数异常!");
			return result;
		}
		result = query("selOrderTime", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * 新增
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm insertdata(TParm parm, TConnection connection) {
        if (parm.getData("CLNCPATH_CODE") == null) {//wanglong add 20150129
            parm.setData("CLNCPATH_CODE", "");
        }
        if (parm.getData("SCHD_CODE") == null) {
            parm.setData("SCHD_CODE", "");
        }
        if (parm.getData("EXEC_DATE") == null) {
            parm.setData("EXEC_DATE", new TNull(Timestamp.class));
        }
		TParm result = new TParm();
		result = update("insertDdata", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 删除
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm CASE_NO,CASE_NO_SEQ,SEQ_NO
	 */
	public TParm deletedata(TParm parm) {
		TParm result = new TParm();
		result = update("deletedata", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询case_no个数
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selCaseNoCount(TParm parm) {
		TParm result = new TParm();
		result = query("selCaseNoCount", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询账单审核数据(账单审核)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selAuditFeeData(TParm parm) {
		TParm result = new TParm();
		result = query("selAuditFeeData", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新账单号码
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updateBillNO(TParm parm, TConnection connection) {
		TParm result = new TParm();
		result = update("updateBillNO", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询病患费用信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selPatOrderData(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "IBSOrderdTool.selPatOrderData()参数异常!");
			return result;
		}
		result = query("selPatOrderData", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;

		}
		return result;
	}

	/**
	 * 科室备药生成
	 * 
	 * @param caseNo
	 *            String
	 * @param caseNoSeq
	 *            int
	 * @param seqNo
	 *            int
	 * @param requestFlg
	 *            String
	 * @param requestNo
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm upForDeptMedic(String caseNo, int caseNoSeq, int seqNo,
			String requestFlg, String requestNo, TConnection conn) {
		TParm parm = new TParm();
		TParm result = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("CASE_NO_SEQ", caseNoSeq);
		parm.setData("SEQ_NO", seqNo);
		parm.setData("REQUEST_FLG", requestFlg);
		parm.setData("REQUEST_NO", requestNo);
		result = this.update("upForDeptMedic", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	// 按照收据费用查询病患费用(FOR 医疗统计)
	public TParm selRexpCodePatFee(String caseNo) {
		TParm result = new TParm();
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		result = query("selRexpCodePatFee", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 查询账务表对应药房,药品用量(For UDD)
	 * 
	 * @param caseNo
	 *            String
	 * @param orderCode
	 *            String
	 * @param exeDeptCode
	 *            String
	 * @return TParm
	 */
	public TParm checkIBSQutryForUDD(String caseNo, String orderCode,
			String exeDeptCode) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("ORDER_CODE", orderCode);
		parm.setData("EXE_DEPT_CODE", exeDeptCode);
		TParm result = new TParm();
		result = query("checkIBSQutryForUDD", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}

	/**
	 * 删除费用主档(批次)
	 * 
	 * @param caseNo
	 *            String
	 * @param billDate
	 *            String
	 * @param caseNoSeq
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm deleteOrderDPatch(String caseNo, String billDate,
			String caseNoSeq, TConnection conn) {
		TParm parm = new TParm();
		TParm result = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("S_DATE", billDate + "000000");
		parm.setData("E_DATE", billDate + "235959");
		parm.setData("CASE_NO_SEQ", caseNoSeq);
		result = update("deleteOrderDPatch", parm, conn);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	/**
	 * 删除费用主档(批次)
	 * 
	 * @param caseNo
	 *            String
	 * @param billDate
	 *            String
	 * @param caseNoSeq
	 *            String
	 * @param conn
	 *            TConnection
	 * @return TParm
	 */
	public TParm deleteOrderDPatchSum(String caseNo, String billDate,
			String caseNoSeq, TConnection conn) {
//		TParm parm = new TParm();
		TParm result = new TParm();
//		parm.setData("CASE_NO", caseNo);
//		parm.setData("S_DATE", billDate + "000000");
//		parm.setData("E_DATE", billDate + "235959");
//		parm.setData("CASE_NO_SEQ", caseNoSeq);
		String sql="DELETE IBS_ORDD WHERE CASE_NO = '"+caseNo
		+"' AND BILL_DATE >= TO_DATE('"+billDate 
		+ "000000', 'yyyyMMddhh24miss') AND BILL_DATE <= TO_DATE('"+billDate +
		"235959', 'yyyyMMddhh24miss') AND CASE_NO_SEQ IN ("+caseNoSeq+")";
		result = new TParm(TJDODBTool.getInstance().update(sql,conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 查询作废账单数据(作废账单)
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selBillReturnD(TParm parm) {
		TParm result = new TParm();
		result = query("selBillReturnD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询批次待删除数据
	 * 
	 * @param caseNo
	 *            String
	 * @param billDate
	 *            String
	 * @param caseNoSeq
	 *            String
	 * @return TParm
	 */
	public TParm seldelOrderDPatch(String caseNo, String billDate,
			String caseNoSeq) {
		TParm parm = new TParm();
		TParm result = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("S_DATE", billDate + "000000");
		parm.setData("E_DATE", billDate + "235959");
		parm.setData("CASE_NO_SEQ", caseNoSeq);
		result = query("seldelOrderDPatch", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return result;
		}
		return result;
	}
	/**
	 * 查询批次待删除数据===pangben 2015-11-9
	 * 
	 * @param caseNo
	 *            String
	 * @param billDate
	 *            String
	 * @param caseNoSeq
	 *            String
	 * @return TParm
	 */
	public TParm seldelOrderDPatchSum(String caseNo, String billDate,
			String caseNoSeq) {
//		TParm parm = new TParm();
		TParm result = new TParm();
//		parm.setData("CASE_NO", caseNo);
//		parm.setData("S_DATE", billDate + "000000");
//		parm.setData("E_DATE", billDate + "235959");
//		parm.setData("CASE_NO_SEQ", caseNoSeq);
		String sql= "SELECT SUM(TOT_AMT) AS TOT_AMT FROM IBS_ORDD WHERE CASE_NO = '"+caseNo
		+"' AND BILL_DATE >= TO_DATE('"+billDate
		+ "000000', 'yyyyMMddhh24miss') AND BILL_DATE <= TO_DATE('"+billDate + 
		"235959', 'yyyyMMddhh24miss') AND CASE_NO_SEQ IN ("+caseNoSeq+")";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 查询最大账务序号
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selMaxSeq(TParm parm) {
		TParm result = new TParm();
		result = query("selMaxSeq", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询不同的账务序号
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm =======pangben 2011-11-16
	 */
	public TParm selDifferentSeq(TParm parm) {
		TParm result = new TParm();
		result = query("selDifferentSeq", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 科室费用查询
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm =======wangzl 2012-08-07
	 */
	public TParm selDeptFee(TParm parm) {
		String COST_CENTER_CODEWhere = "";
		String CAT1_CODEWHERE = "";
		String PHA_BASEFrom = "";
		String CTRLDRUGCLASS_codeWhere = "";
		if (parm.getData("cost_center_code").toString().length() != 0) {
			COST_CENTER_CODEWhere = " AND D.EXE_DEPT_CODE ='"
					+ parm.getData("cost_center_code") + "'";
		}
		if (parm.getData("order_cat1_code") != null
				&& parm.getData("order_cat1_code").toString().length() != 0) {
			CAT1_CODEWHERE = " AND D.ORDER_CAT1_CODE='"
					+ parm.getData("order_cat1_code") + "'";
		}
		if (parm.getData("ctfig").toString()!= "") {//===modify by caowl 20120814
			PHA_BASEFrom = " PHA_BASE  P,SYS_CTRLDRUGCLASS C,";
			CTRLDRUGCLASS_codeWhere = " AND P.ORDER_CODE = D.ORDER_CODE AND " +
					"C.CTRLDRUGCLASS_CODE = P.CTRLDRUGCLASS_CODE AND C.CTRL_FLG='"+parm.getData("ctfig")+"'";//==modify by caowl 20120821
		}
		String sql = "SELECT D.BILL_DATE,S.ORDER_DESC || CASE"
				+ "        WHEN S.SPECIFICATION IS NOT NULL THEN"
				+ "         '(' || S.SPECIFICATION || ')'" + " ELSE" + " ''"
				+ "        END AS ORDER_DESC," + " D.OWN_FLG, D.DOSAGE_QTY,"
				+          "D.OWN_PRICE, D.TOT_AMT, D.OPT_DATE,"
				+          "U.USER_NAME, D.INDV_FLG, D.ORDER_CODE,"
				+          "D.DOSAGE_UNIT, D.ORDERSET_CODE, D.CASE_NO_SEQ ,"
				+ 		   "D.ORDERSET_GROUP_NO,X.COST_CENTER_CHN_DESC "//===modify by caowl 20120814
				+ " FROM IBS_ORDD  D,"
				+ PHA_BASEFrom 
				+        " SYS_FEE S, "
				+        "SYS_OPERATOR  U,SYS_COST_CENTER X" //====modify by caowl 20120814
				+ " WHERE D.CASE_NO = '"+ parm.getData("CASE_NO") + "'" 
				+ 		  COST_CENTER_CODEWhere
				+ 		  " AND d.bill_date >= TO_DATE('" + parm.getData("START_DATE")
				+ 		  "', 'YYYYMMDD HH24:MI:SS')" 
				+         " AND d.bill_date <= TO_DATE('"
				+         parm.getData("END_DATE") + "', 'YYYYMMDD HH24:MI:SS')"
				+         CAT1_CODEWHERE + CTRLDRUGCLASS_codeWhere
				+         " AND S.ORDER_CODE = D.ORDER_CODE"
				+         " AND U.USER_ID = D.OPT_USER"
				+         " AND D.EXE_DEPT_CODE = X.COST_CENTER_CODE "//===modify by caowl 20120814
				+ " ORDER BY D.CASE_NO_SEQ, D.SEQ_NO";
		//System.out.println("sql::::"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;

	}

	/**
	 * 初始化成本中心
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm =======wangzl 2012-08-07
	 */
	public TParm initCostCenterCode() {
		String COST_CENTER_CODE = Operator.getCostCenter(); // 得到当前登录user 的成本中心
		String sql = "SELECT IPD_FIT_FLG,COST_CENTER_CODE,COST_CENTER_CHN_DESC "
				+ "FROM SYS_COST_CENTER WHERE COST_CENTER_CODE='"
				+ COST_CENTER_CODE + "'"; // 抓取IPD_FIT_FLG(科室成本中心fig)
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 多个临床路径校验
	 * @param parm
	 * @return
	 */
	public TParm selClpClncpathCode(TParm parm){
		TParm result = new TParm();
		result = query("selClpClncpathCode", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 路径病患校验是否存在空的路径时程
	 * @param parm
	 * @return
	 */
	public TParm selClpSchdCode(TParm parm){
		TParm result = new TParm();
		result = query("selClpSchdCode", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
