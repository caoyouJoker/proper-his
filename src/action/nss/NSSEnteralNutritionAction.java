package action.nss;

import org.apache.commons.lang.StringUtils;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 肠内营养Action</p>
 *
 * <p>Description: 肠内营养Action</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangbin 2015.3.20
 * @version 1.0
 */
public class NSSEnteralNutritionAction extends TAction {
	
	public NSSEnteralNutritionAction() {
	}

    /**
     * 肠内营养展开医嘱保存
     *
     * @param parm
     *            TParm
     * @return TParm
     */
	public TParm onSaveNSSENDspnM(TParm parm) {
		TConnection conn = getConnection();
		int count  = parm.getCount();
		TParm result = new TParm();
		String prepareNo = "";
		int seq = 0;
		int saveCount = 0;
		
		for (int i = 0; i < count; i++) {
			// 如果数据已经展开过则不再插入
			if (parm.getBoolean("EXIST_FLG", i)) {
				continue;
			}
			
			// 如果由于界面没有刷新，导致停用数据进来，则不进行插入
			if (parm.getBoolean("DC_FLG", i)) {
				continue;
			}
			
			prepareNo = SystemTool.getInstance().getNo("ALL", "NSS",
					"EN_PREPARE_NO", "EN_PREPARE_NO");
			// 取号原则(为了单次执行扫码时区分是否是肠内营养，在生成的号码前加标识0)
			parm.setData("EN_PREPARE_NO", i, "0" + prepareNo);
			
			// 保存营养师展开医嘱主项
			result = NSSEnteralNutritionTool.getInstance().insertNSSENDspnM(
					parm.getRow(i), conn);
			
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.close();
				return result;
			}
			
			// 标签张数即细项展开条数
			seq = parm.getInt("LABEL_QTY", i);
			for (int j = 1; j <= seq; j++) {
				// 序号
				parm.setData("SEQ", i, j);
				// 保存营养师展开医嘱细项
				result = NSSEnteralNutritionTool.getInstance().insertNSSENDspnD(
						parm.getRow(i), conn);
				
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText()
							+ result.getErrName());
					conn.close();
					return result;
				}
			}
			
			saveCount++;
		}
		
		conn.commit();
		conn.close();
		result.setData("SAVE_COUNT", saveCount);
		return result;
	}
	
	/**
     * 删除肠内营养配方数据
     *
     * @param parm
     *            TParm
     * @return TParm
     */
	public TParm deleteNSSENOrderD(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		String[] sqlArray = (String[]) parm.getData("DELETE_SQL");
		if (null == sqlArray) {
			result.setErr(-1, "传参错误");
			return result;
		}
		
		int count = sqlArray.length;
		for (int i = 0; i < count; i++) {
			if (sqlArray[i].contains("INSERT")) {
				continue;
			}
			
			// 保存营养师展开医嘱主项
			result = NSSEnteralNutritionTool.getInstance().deleteNSSENOrderD(
					sqlArray[i], conn);

			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.close();
				return result;
			}
		}
		
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * 肠内营养配制完成时保存操作
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm onSaveByPrepareComplete(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		
		// 配制人员完成配制时更新肠内营养执行主档表数据
		result = NSSEnteralNutritionTool.getInstance().updateENDspnMByPrepare(
				parm.getParm("parmM"), conn);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText());
			result.setErr(-1, "更新配制状态发生异常");
			conn.close();
			return result;
		}
		
		// 含有计费项目则进行计费
		if (parm.getBoolean("CHARGE_FLG")) {
			if (null == parm.getParm("parmD")) {
				result.setErr(-1, "计费参数传入错误");
				conn.close();
				return result;
			} else {
				// 肠内营养配制完成计费
				result = NSSEnteralNutritionTool.getInstance().insertIBSOrder(parm.getParm("parmD"),
						conn);
				
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText());
					result.setErr(-1, "计费发生异常");
					conn.close();
					return result;
				}
			}
		}
		
		conn.commit();
		conn.close();
		return result;
	}
	
    /**
     * 肠内营养护士单次执行保存
     *
     * @param parm
     *            TParm
     * @return TParm
     */
	public TParm onSaveNSSENDspnBySingleExe(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();

		// 护士单次执行保存操作更新医嘱展开主项
		result = NSSEnteralNutritionTool.getInstance()
				.updateENDspnMBySingleExe(parm, conn);

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText());
			result.setErr(-1, "更新医嘱展开主项错误");
			conn.close();
			return result;
		}
		
		// 护士单次执行保存操作更新医嘱展开细项
		result = NSSEnteralNutritionTool.getInstance()
				.updateENDspnDBySingleExe(parm, conn);

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText());
			result.setErr(-1, "更新医嘱展开细项错误");
			conn.close();
			return result;
		}
		
		if ((StringUtils.equals("UD", parm.getValue("RX_KIND"))
				&& parm.getInt("TOTAL_ACCU_QTY") > 0 && parm
				.getInt("TOTAL_ACCU_QTY")
				% parm.getInt("MEDI_QTY") == 0)
				|| StringUtils.equals("ST", parm.getValue("RX_KIND"))) {
			TParm queryParm = new TParm();
			queryParm.setData("CASE_NO", parm.getValue("CASE_NO"));
			queryParm.setData("ORDER_NO", parm.getValue("ORDER_NO"));
			queryParm.setData("ORDER_SEQ", parm.getValue("ORDER_SEQ"));
			queryParm.setData("ORDER_DATE", SystemTool.getInstance().getDate()
					.toString().substring(0, 10).replaceAll("-", ""));
			queryParm.setData("EXEC_FLG", "N");
			
			// 查询ODI_DSPND表展开明细
			result = NSSEnteralNutritionTool.getInstance().queryODIDspnDInfo(queryParm);
			
			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText());
				result.setErr(-1, "查询ODI_DSPND表展开明细错误");
				conn.close();
				return result;
			}
			
			if (result.getCount("CASE_NO") > 0) {
				// 护士单次执行保存操作更新医嘱展开细项ODI_DSPND
				TParm updateParm = new TParm();
				updateParm.setData("CASE_NO", result.getValue("CASE_NO", 0));
				updateParm.setData("ORDER_NO", result.getValue("ORDER_NO", 0));
				updateParm.setData("ORDER_SEQ", result.getValue("ORDER_SEQ", 0));
				updateParm.setData("ORDER_DATE", result.getValue("ORDER_DATE", 0));
				updateParm.setData("ORDER_DATETIME", result.getValue("ORDER_DATETIME", 0));
				updateParm.setData("NS_EXEC_CODE_REAL", parm.getValue("OPT_USER"));
				result = NSSEnteralNutritionTool.getInstance().updateODIDspnD(updateParm, conn);
				
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrCode() + result.getErrText());
					result.setErr(-1, "更新医嘱展开细项OID_DSPND表错误");
					conn.close();
					return result;
				}
			}
		}

		conn.commit();
		conn.close();
		return result;
	}
	
	/**
     * 肠内营养交接
     *
     * @param parm
     *            TParm
     * @return TParm
     */
	public TParm onSaveByENHandOver(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			
			// 保存交接数据
			result = NSSEnteralNutritionTool.getInstance().onSaveByENHandOver(
					parm.getRow(i), conn);

			if (result.getErrCode() < 0) {
				err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
				conn.rollback();
				conn.close();
				return result;
			}
		}
		
		conn.commit();
		conn.close();
		return result;
	}
	
	/**
	 * 肠内营养配制附加费用计费
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm chargeENExtraFee(TParm parm) {
		TConnection conn = getConnection();
		TParm result = new TParm();
		
		// 肠内营养配制完成计费
		result = NSSEnteralNutritionTool.getInstance().insertIBSOrder(parm,
				conn);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText());
			result.setErr(-1, "计费发生异常");
			conn.close();
			return result;
		}
		
		conn.commit();
		conn.close();
		return result;
	}
}
