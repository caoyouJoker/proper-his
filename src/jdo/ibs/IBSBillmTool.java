package jdo.ibs;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 *
 * <p>Title: 住院账务主档工具类</p>
 *
 * <p>Description: 住院账务主档工具类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class IBSBillmTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static IBSBillmTool instanceObject;
    /**
     * 得到实例
     * @return IBSBillmTool
     */
    public static IBSBillmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IBSBillmTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public IBSBillmTool() {
        setModuleName("ibs\\IBSBillmModule.x");
        onInit();
    }

    /**
     * 新增
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertdata(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("insertMdata", parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除
     * @param parm TParm
     * @return TParm
     */
    public TParm deletedata(TParm parm) {
        TParm result = new TParm();
        result = update("deletedata", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询账单数据(缴费作业)
     * @param parm TParm
     * @return TParm
     */
    public TParm selDataForCharge(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "IBSBillmTool.selDataForCharge()参数异常!");
            return result;
        }
        result = query("selDataForCharge", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;

        }
        return result;
    }

    /**
     * 查询所有数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectAllData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "IBSBillmTool.selectAllData()参数异常!");
            return result;
        }
        result = query("selectAllData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 查找最大账单序号
     * @param parm TParm
     * @return TParm
     */
    public TParm selMaxSeq(TParm parm) {
        TParm result = new TParm();
        result = query("selMaxSeq", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 更新收据号码(For 缴费作业)
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updataForRecp(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
            " UPDATE IBS_BILLM SET RECEIPT_NO = '" + parm.getValue("RECEIPT_NO") +
            "',CHARGE_DATE = SYSDATE  " +
            "  WHERE BILL_NO IN (" + parm.getValue("BILL_NO") + ") " +
            "    AND (REFUND_FLG <> 'Y' OR REFUND_FLG IS NULL) ";
//        System.out.println("更新收据号码(For 缴费作业)" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql, connection));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 作废账单中更新账单操作
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updataDate(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
            " UPDATE IBS_BILLM SET REFUND_FLG = 'Y'," +
            "                      REFUND_BILL_NO='" +
            parm.getValue("REFUND_BILL_NO") + "'," +
            "                      REFUND_CODE='" + parm.getValue("REFUND_CODE") +
            "'," +
            "                      REFUND_DATE = SYSDATE " +
            "  WHERE BILL_NO = '" + parm.getValue("BILL_NO") + "' " +
            "    AND RECEIPT_NO IS NULL  "+
            "    AND (REFUND_FLG <> 'Y' OR REFUND_FLG IS NULL ) ";
//        System.out.println("作废账单" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql, connection));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询作废账单数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selDataForBill(TParm parm) {
        TParm result = new TParm();
        result = query("selDataForBill", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * 查询作废账单全部数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selBillData(TParm parm) {
        TParm result = new TParm();
        result = query("selBillData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;

    }
    /**
     * 查询账单审核数据(账单审核)
     * @param parm TParm
     * @return TParm
     */
    public TParm selAuditFee(TParm parm) {
        TParm result = new TParm();
        result = query("selAuditFee", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;

    }
    /**
     * 收据召回更新退费人员,注记,时间
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updataForReturnRecp(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
            " UPDATE IBS_BILLM SET REFUND_FLG = 'Y'," +
            "                      REFUND_BILL_NO='" +
            parm.getValue("REFUND_BILL_NO") + "'," +
            "                      REFUND_CODE='" + parm.getValue("REFUND_CODE") +
            "'," +
            "                      REFUND_DATE = SYSDATE " +
            "  WHERE BILL_NO = '" + parm.getValue("BILL_NO") + "' " +
            "    AND RECEIPT_NO = '" + parm.getValue("RECEIPT_NO") + "' "+
//            "    AND RECEIPT_NO IS NULL  "+
            "    AND (REFUND_FLG <> 'Y' OR REFUND_FLG IS NULL ) ";
//        System.out.println("作废账单" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql, connection));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 是否存在作废账单或召回账单
     * @param caseNo String
     * @return TParm
     */
    public TParm selBackBill(TParm parm) {
        TParm result = new TParm();
        String sql =
                " SELECT CASE_NO, BILL_NO, IPD_NO, MR_NO, AR_AMT " +
                "   FROM IBS_BILLM " +
                "  WHERE AR_AMT < 0 "+
                "    AND CASE_NO = '" + parm.getValue("CASE_NO") + "' ";
//        System.out.println("召回账单或作废账单" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
	 * 修改身份
	 * 
	 * @author caowl
	 * 
	 * @param parm
	 *            TParm
	 * @param connection
	 *            TConnection
	 * @return TParm
	 */
	public TParm updataDateforCTZ(TParm parm, TConnection connection) {

		TParm result = new TParm();

		String sql = "UPDATE  IBS_BILLM SET REFUND_FLG = 'Y' "				
				+ ",REFUND_DATE = SYSDATE ,REFUND_CODE = '"+parm.getValue("REFUND_CODE")+"' ,REFUND_BILL_NO = '"+parm.getValue("REFUND_BILL_NO")+"'" + 
				" WHERE BILL_NO ='"	+ parm.getValue("BILL_NO") + "' AND RECEIPT_NO IS NULL";
		//System.out.println("sql--->"+sql);	
		result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	
	/**
	 * 根据case_no查询账单信息
	 * 
	 * @author caowl
	 * 
	 * @param TParm
	 * 
	 * @return TParm
	 * */
	public TParm selByCase_no(TParm parm) {

		String sql = "SELECT BILL_NO,BILL_SEQ,CASE_NO,IPD_NO,MR_NO," +
						" BILL_DATE,REFUND_FLG,REFUND_BILL_NO,RECEIPT_NO,CHARGE_DATE," +
						" CTZ1_CODE,CTZ2_CODE,CTZ3_CODE,BEGIN_DATE,END_DATE," +
						" DISCHARGE_FLG,DEPT_CODE,STATION_CODE,BED_NO,OWN_AMT," +
						" NHI_AMT,APPROVE_FLG,REDUCE_REASON,REDUCE_AMT,REDUCE_DATE," +
						" REDUCE_DEPT_CODE,REDUCE_RESPOND,AR_AMT,PAY_AR_AMT,CANDEBT_CODE," +
						" CANDEBT_PERSON,OPT_USER,OPT_DATE,OPT_TERM,REFUND_CODE," +
						" REFUND_DATE,REGION_CODE " +
				     " FROM IBS_BILLM WHERE CASE_NO ='"
				+ parm.getData("CASE_NO") + "'";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));

		if (selParm.getErrCode() < 0) {
			err(selParm.getErrName() + " " + selParm.getErrText());
			return selParm;
		}

		return selParm;
	}


}
