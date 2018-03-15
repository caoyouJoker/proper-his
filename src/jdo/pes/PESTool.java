package jdo.pes;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 处方点评工具类
 * </p>
 * 
 * <p>
 * Description: 处方点评工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author zhangp
 * @version 1.0
 */
public class PESTool extends TJDOTool {

	/**
	 * 构造器
	 */
	public PESTool() {
		setModuleName("pes\\PESModule.x");
		onInit();
	}

	/**
	 * 实例
	 */
	private static PESTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PESTool
	 */
	public static PESTool getInstance() {
		if (instanceObject == null)
			instanceObject = new PESTool();
		return instanceObject;
	}

	// /**
	// * 得到交易号
	// *
	// * @return String
	// */
	// public String getBusinessNo() {
	// return SystemTool.getInstance().getNo("ALL", "EKT", "BUSINESS_NO",
	// "BUSINESS_NO");
	// }

	/**
	 * 查询待选列表
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectRxNo(TParm parm) {
		TParm result = query("selectRxNo", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 按rx_no取PES_OPD的数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectRxD(TParm parm) {
		TParm result = query("selectRxNoD", parm);
		
		System.out.println("xxxxxxxxxxxxxxxxxxxxxx"+result);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * add caoyong 20140214
	 * 按case_no取ODI_ORDER的数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectIpdQ(TParm parm) {
		
		TParm result = query("selectIpdQ", parm);
		////System.out.println("nnnnnnnnnnnnnnnnn"+result);
		
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 按case_no取得除去主诊断以外的诊断
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectZD(TParm parm) {
		TParm result = query("selectZD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 插入PES_OPDM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertPESOPDM(TParm parm, TConnection connection) {
		TParm result = update("insertPESOPDM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 插入PES_OPDM
	 * ADD CAOYONG 2014214
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertIPDPESOPDM(TParm parm, TConnection connection) {
		TParm result = update("insertIPDPESOPDM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 插入PES_OPDD
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertPESOPDD(TParm parm, TConnection connection) {
		TParm result = update("insertPESOPDD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 插入PES_IDPOPDD
	 * add caoyong 20140218
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertPESIPDOPDD(TParm parm, TConnection connection) {
		TParm result = update("insertPESIPDOPDD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 插入PESRESULT
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertPESRESULT(TParm parm) {
		TParm result = update("insertPESRESULT", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 插入PESRESULT
	 * add caoyong 20140218
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertIpdPESRESULT(TParm parm) {
		TParm result = update("insertIpdPESRESULT", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询OPDM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOPDM(TParm parm) {
		TParm result = query("selectOPDM", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	/**
	 * 查询PES_IPDOPDM
	 * ADD CAOYONG 20130219
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectIPDOPDM(TParm parm) {
		TParm result = query("selectIPDOPDM", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询OPDD
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOPDD(TParm parm) {
		TParm result = query("selectOPDD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 查询PES_IPDOPDD
	 * add caoyong 20140220
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectIPDOPDD(TParm parm) {
		TParm result = query("selectIPDOPDD", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新PES_OPDM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatePESOPDM(TParm parm, TConnection connection) {
		TParm result = update("updatePESOPDM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 更新PES_IPDOPDM
	 * add caoyong20140220
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatePESIPDOPDM(TParm parm, TConnection connection) {
		TParm result = update("updatePESIPDOPDM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新PES_OPDD
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatePESOPDD(TParm parm, TConnection connection) {
		TParm result = update("updatePESOPDD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 更新PES_IDPOPDD
	 * add caoyong20140220
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatePESIDPOPDD(TParm parm, TConnection connection) {
		TParm result = update("updatePESIDPOPDD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新PESResult
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatePESResult(TParm parm) {
		TParm result = update("updatePESResult", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 更新PESResult
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updatePESIPDResult(TParm parm) {
		TParm result = update("updatePESIPDResult", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询OPDM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOPDMforPrint(TParm parm) {
		TParm result = query("selectOPDMforPrint", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 查询PESResult
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectPESResult(TParm parm) {
		TParm result = query("selectPESResult", parm);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * 查询住院
	 * add caoyong 20140213
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	
	public TParm selectinpNo(TParm parm) {
		//TParm result = query("selectinpNo", parm);
		String deptCode ="";
		String drCode="";
		String antType="";
		if(parm.getValue("DEPT_CODE")!=null&&!"".equals(parm.getValue("DEPT_CODE"))){
			deptCode=" AND C.DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
		}
		if(parm.getValue("DR_CODE")!=null&&!"".equals(parm.getValue("DR_CODE"))){
			drCode=" AND A.VS_DR_CODE='"+parm.getValue("DR_CODE")+"' ";
		}
		
		if (!"".equals(parm.getValue("ANTIBIOTIC_CODE"))&&parm.getValue("ANTIBIOTIC_CODE")!=null) {
			  antType="AND A.ANTIBIOTIC_CODE='"+parm.getValue("ANTIBIOTIC_CODE")+"' ";
		}
		String sql="SELECT DISTINCT A.CASE_NO,A.IPD_NO, B.MR_NO,'N' FLG," +
	               "A.IN_DATE, "+
			       "A.VS_DR_CODE,A.DEPT_CODE, "+
			       "D.PAT_NAME "+
			       "FROM ADM_INP A,OPE_OPBOOK B,ODI_ORDER C,SYS_PATINFO D "+
			       "WHERE A.CASE_NO = B.CASE_NO "+
			       "AND A.CASE_NO = C.CASE_NO "+
			       "AND A.MR_NO = D.MR_NO "+
			       "AND A.DS_DATE IS NOT NULL  "+
			       "AND C.RX_KIND='ST' "+
			       deptCode+drCode+antType +
			       "AND A.DS_DATE BETWEEN TO_DATE ('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') "+
		   		   "AND TO_DATE ('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS') ORDER BY IN_DATE";
		System.out.print(sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * 插入PES_OPDD
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertIpdPESOPDD(TParm parm, TConnection connection) {
		TParm result = update("insertIpdPESOPDD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ADD caoyong 20140217
	 * 
	 * @param parm
	 * @param table
	 * @return
	 */
	
	public TParm selectMaxsql(TParm parm,String table) {
		String SQL ="SELECT MAX(SEQ) AS SEQ FROM "+ table  +
					" WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' " +
				    " AND PES_NO='"+parm.getValue("PES_NO")+"' " +
				    " AND TYPE_CODE='"+parm.getValue("TYPE_CODE")+"' ";
		
		
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL));
		
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	/**
	 * ADD caoyong 20140217
	 * 
	 * @param parm
	 * @param table
	 * @return
	 */
	public TParm selectMaxRsql(TParm parm,String table) {
		String SQL ="SELECT MAX(SEQ) AS SEQ FROM "+ table  +
					" WHERE  PES_NO='"+parm.getValue("PES_NO")+"' " +
				    " AND  TYPE_CODE='"+parm.getValue("TYPE_CODE")+"' ";
		
		
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL));
		
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/*public TParm selectMaxOpddSql(TParm parmD,TParm parm, int i,String table) {
		String SQL ="SELECT MAX(SEQ) AS SEQ FROM "+ table  +
					" WHERE CASE_NO='"+parmD.getValue("CASE_NO",i)+"' " +
					" AND  PES_NO='"+parm.getValue("PES_NO")+"' " +
		            " AND  ORDER_NO='"+parmD.getValue("ORDER_NO",i)+"' "+
					" AND  ORDER_SEQ='"+parmD.getValue("ORDER_SEQ",i)+"' ";
		
		
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL));
		
		if (result.getErrCode() < 0) {
			err("ERR:M " + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}*/
}
