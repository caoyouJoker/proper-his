package jdo.inw;

import java.sql.Timestamp;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title: 住院护士站对于ODI的主Tool
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ZangJH
 * @version 1.0
 */
public class InwForOdiTool extends TJDOTool {

	/**
	 * 实例
	 */
	private static InwForOdiTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PatTool
	 */
	public static InwForOdiTool getInstance() {
		if (instanceObject == null)
			instanceObject = new InwForOdiTool();
		return instanceObject;
	}

	public InwForOdiTool() {

		// 加载Module文件
		this.setModuleName("inw\\INWForOdiModule.x");

		onInit();

	}

	/**
	 * 根据用量判断库存
	 * 
	 * @param org_code
	 *            药房代码
	 * @param order_code
	 *            药品代码
	 * @param dosage_qty
	 *            用量
	 * @return boolean
	 */
	public boolean inspectIndStock(String org_code, String order_code,
			double dosage_qty) {
		if (dosage_qty < 0)
			return false;
		if (getStockQTY(org_code, order_code) >= dosage_qty)
			return true;
		return false;
	}

	/**
	 * 根据药库编号及药品代码查询药库库存量
	 * 
	 * @param org_code
	 *            药库编号
	 * @param order_code
	 *            药品代码
	 * @return QTY 库存量
	 */
	public double getStockQTY(String org_code, String order_code) {
		if ("".equals(org_code) || "".equals(order_code)) {
			return -1;
		}
		TParm parm = new TParm();
		parm.setData("ORG_CODE", org_code);
		parm.setData("ORDER_CODE", order_code);
		TParm result = this.query("getIndStockQty", parm);
		if (result == null || result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return -1;
		}
		return result.getDouble("INDSTOCKQTY", 0);
	}

	/**
	 * 插入一条新的长期处置向ODI_DSPND
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertOdiDspnd(TParm parm, TConnection connection) {
		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("insertOdiDspnd", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 插入一条新的长期处置向ODI_DSPNM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm insertOdiDspnm(TParm parm, TConnection connection) {
		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("insertOdiDspnM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据查询条件查询ODI_ORDER表数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOdiOrder(TParm parm) {
		TParm result = new TParm();
		result = query("selectOdiOrder", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 根据查询条件查询ODI_ORDER表数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm selectOdiDspnm(TParm parm) {
		TParm result = new TParm();
		result = query("selectOdiDspnm", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
     * 根据查询条件查询ODI_DSPND表数据
     * 
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm selectOdiDspnD(TParm parm) {// add by wanglong 20130626
        TParm result = new TParm();
        result = query("selectOdiDspnD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
    
	/**
	 * 更新ODI_ORDER
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * 
	 *         public TParm updateOdiOrder(TParm parm, TConnection connection) {
	 * 
	 *         TParm result = new TParm(); //执行module上的insert update
	 *         delete用update result = update("updateOdiOrder", parm,
	 *         connection); if (result.getErrCode() < 0) { err("ERR:" +
	 *         result.getErrCode() + result.getErrText() + result.getErrName());
	 *         return result; } return result; }
	 */
	/**
	 * 更新ODI_ORDER
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiOrder(TParm parm, TConnection connection) {
		TParm result = new TParm();
		String user = parm.getValue("OPT_USER").equals("<TNULL>")?"":parm.getValue("OPT_USER");
		String ip = parm.getValue("OPT_TERM").equals("<TNULL>")?"":parm.getValue("OPT_TERM");
		double lastqty = parm.getDouble("LASTDSPN_QTY");
		double acudqty = parm.getDouble("ACUMDSPN_QTY");
		double acumqty = parm.getDouble("ACUMMEDI_QTY");
		double dosqty = parm.getDouble("DOSAGE_QTY");
		double disqty = parm.getDouble("DISPENSE_QTY");
		String dosunit = parm.getValue("DOSAGE_UNIT");
		String disunit = parm.getValue("DISPENSE_UNIT");
		String nscheckcode = parm.getValue("NS_CHECK_CODE").equals("<TNULL>")?"":parm.getValue("NS_CHECK_CODE");
		String dcnscheckcode = parm.getValue("DC_NS_CHECK_CODE").equals("<TNULL>")?"":parm.getValue("DC_NS_CHECK_CODE");
		String takemedorg = parm.getValue("TAKEMED_ORG").equals("<TNULL>")?"":parm.getValue("TAKEMED_ORG");
		String caseNo = parm.getValue("CASE_NO");
		String orderNo = parm.getValue("ORDER_NO");
		int orderSeq = parm.getInt("ORDER_SEQ");
		String optdate = "NULL";
		String lastdate = "NULL";
		String nscheckdate = "NULL";
		String dcnscheckdate = "NULL";
		if (!parm.getValue("OPT_DATE").equals(
				String.valueOf(new TNull(Timestamp.class))))
			optdate = "TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("OPT_DATE"),
							"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
		if (!parm.getValue("LAST_DSPN_DATE").equals(
				String.valueOf(new TNull(Timestamp.class))))
			lastdate = "TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("LAST_DSPN_DATE"),
							"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
		if (!parm.getValue("NS_CHECK_DATE").equals(
				String.valueOf(new TNull(Timestamp.class))))
			nscheckdate = "TO_DATE('"
					+ StringTool.getString(parm.getTimestamp("NS_CHECK_DATE"),
							"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
		if (!parm.getValue("DC_NS_CHECK_DATE").equals(
				String.valueOf(new TNull(Timestamp.class))))
			dcnscheckdate = "TO_DATE('"
					+ StringTool
							.getString(parm.getTimestamp("DC_NS_CHECK_DATE"),
									"yyyyMMddHHmmss") + "','YYYYMMDDHH24MISS')";
		String sql = "UPDATE ODI_ORDER SET " + "OPT_DATE=" + optdate
				+ ",OPT_USER='" + user + "',OPT_TERM='" + ip + "',"
				+ "LASTDSPN_QTY=" + lastqty + ",LAST_DSPN_DATE=" + lastdate
				+ ",ACUMDSPN_QTY=" + acudqty + ",ACUMMEDI_QTY=" + acumqty + ","
				+ "DOSAGE_QTY=" + dosqty + ",DOSAGE_UNIT='" + dosunit
				+ "',DISPENSE_QTY=" + disqty + ",DISPENSE_UNIT='" + disunit
				+ "'," + "NS_CHECK_DATE=" + nscheckdate + ",NS_CHECK_CODE='"
				+ nscheckcode + "',DC_NS_CHECK_CODE='" + dcnscheckcode
				+ "',DC_NS_CHECK_DATE=" + dcnscheckdate + ","
				+ "TEMPORARY_FLG='Y',TAKEMED_ORG='" + takemedorg + "'"
				+ " WHERE CASE_NO='" + caseNo + "' AND ORDER_NO='" + orderNo
				+ "' AND ORDER_SEQ=" + orderSeq;
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新ODI_ORDER
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateDCToOrder(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("updateDCToOrder", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新ODI_DSPNM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateDCToDspnM(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("updateDCToDspnM", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新ODI_DSPND
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateDCToDspnD(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("updateDCToDspnD", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新ODI_DSPNM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiDspnmForExec(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("updateOdiDspnmForExec", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新ODI_DSPNM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiDspnmByIBS(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("updateOdiDspnmByIBS", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新ODI_DSPND
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiDspndByIBS(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("updateOdiDspndByIBS", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新ODI_DSPND
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiDspndForExec(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("updateOdiDspndForExec", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新ODI_ORDER取消审核
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiOrderForUndoCk(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("updateOdiOrderForUndoCk", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 删除ODI_DSPNM取消审核
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm delOdiDspnmForUndoCk(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("delOdiDspnmForUndoCk", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 删除ODI_DSPND取消审核
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm delOdiDspndForUndoCk(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("delOdiDspndForUndoCk", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新ODI_DSPND
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiDspndToNsNot(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("updateOdiDspndToNsNot", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新ODI_ORDER
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiOrderForNote(TParm parm, TConnection connection) {
		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("updateOdiOrderForNote", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	public TParm updateOdiOrderU(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("updateOdiOrderU", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新检验送检人员和时间
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateOdidspnmLisData(TParm parm) {
		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("updateOdidspnmLisData", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新配液条码
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateOdidspnDBarCode(TParm parm) {
		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("updateOdiDspndBarCode", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新ODI_ORDER
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiOrdertakeMedOrg(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("updateOdiOrdertakeMedOrg", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新ODI_DSPNM
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiDspnmtakeMedOrg(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("updateOdiDspnmtakeMedOrg", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

	/**
	 * 更新ODI_DSPND
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateOdiDspnDtakeMedOrg(TParm parm, TConnection connection) {

		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("updateOdiDspnDtakeMedOrg", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}

    /**
     * 查询取药单号下是否存在普药
     * 
     * @param parm
     * @return
     */
    public boolean existCommonOrder(TParm parm) {// add by wanglong 20130607
        if (parm == null) {
            return false;
        }
        String sql =
                "SELECT A.* FROM ODI_ORDER A, ODI_DSPNM B WHERE A.CASE_NO = B.CASE_NO "
                        + " AND A.ORDER_NO = B.ORDER_NO AND A.ORDER_SEQ = B.ORDER_SEQ "
                        + " AND B.TAKEMED_NO = '#' AND A.CASE_NO IN(#) AND A.CTRLDRUGCLASS_CODE IS NULL";
        sql = sql.replaceFirst("#", parm.getValue("TAKEMED_NO"));
        sql = sql.replaceFirst("#", parm.getValue("WHERE_1"));//add by wanglong 20130620
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            return false;
        }
        if (result.getCount() < 1) {
            return false;
        }
        return true;
    }
    
    /**
     * 查询取药单号下是否存在麻精药嘱
     * 
     * @param parm
     * @return
     */
    public boolean existDrugOrder(TParm parm) {// add by wanglong 20130607
        if (parm == null) {
            return false;
        }
        String sql =
                "SELECT A.* FROM ODI_ORDER A, ODI_DSPNM B WHERE A.CASE_NO = B.CASE_NO "
                        + " AND A.ORDER_NO = B.ORDER_NO AND A.ORDER_SEQ = B.ORDER_SEQ "
                        + " AND B.TAKEMED_NO = '#' AND A.CASE_NO IN(#) AND A.CTRLDRUGCLASS_CODE IS NOT NULL";
        sql = sql.replaceFirst("#", parm.getValue("TAKEMED_NO"));
        sql = sql.replaceFirst("#", parm.getValue("WHERE_1"));//add by wanglong 20130620
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            return false;
        }
        if (result.getCount() < 1) {
            return false;
        }
        return true;
    }
    
    /**
     * 查询取药单号的医嘱开立医师
     * 
     * @param parm
     * @return
     */
    public String queryOrderDrCode(TParm parm) {// add by wanglong 20130607
        if (parm == null) {
            return "";
        }
        String sql =
                "SELECT DISTINCT A.ORDER_DR_CODE FROM ODI_ORDER A, ODI_DSPNM B WHERE A.CASE_NO = B.CASE_NO "
                        + " AND A.ORDER_NO = B.ORDER_NO AND A.ORDER_SEQ = B.ORDER_SEQ "
                        + " AND B.TAKEMED_NO = '#' AND A.CASE_NO IN(#) ORDER BY ORDER_DR_CODE";
        sql = sql.replaceFirst("#", parm.getValue("TAKEMED_NO"));
        sql = sql.replaceFirst("#", parm.getValue("WHERE_1"));//add by wanglong 20130620
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            return "";
        }
        if (result.getCount() < 1) {
            return "";
        }
        return result.getValue("ORDER_DR_CODE", 0);
    }
    
    /**
     * 查询条码号（FROM ODI_ORDER）
     * @param parm
     * @return
     */
    public TParm queryMedNo(TParm parm) {// add by wanglong 20130809
        TParm result = new TParm();
        result = query("selectOdiOrderMedNo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据病案号查询此患者包床的正确床号
     * @param value 病案号
     * @return 床号
     */
	public String selectBedNoByMrNo(String mrNo) {
		String bedNo = "";
		String sql = " SELECT BED_NO_DESC FROM SYS_BED " +
					 " WHERE MR_NO IN (SELECT MR_NO FROM SYS_BED GROUP BY MR_NO HAVING COUNT(MR_NO) > 1) " +
					 " AND BED_OCCU_FLG='Y' AND MR_NO = '" + mrNo + "'" ;
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return bedNo;
        }
		bedNo = result.getValue("BED_NO_DESC", 0);
		return bedNo;
	}
	/**
	 * 插入MED_NODIFY
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * yanjing 20140714
	 */
	public TParm insertIntoMedNodify(TParm parm, TConnection connection) {
		TParm result = new TParm();
		// 执行module上的insert update delete用update
		result = update("insertIntoMedNodify", parm, connection);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
}
